package lab;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;

public final class Main {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final int MAX_UPLOAD = 5 * 1024 * 1024;

    private static void json(HttpExchange x, int status, Object value) throws IOException {
        byte[] body = JSON.writeValueAsBytes(value);
        x.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        x.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        x.sendResponseHeaders(status, body.length);
        try (var out = x.getResponseBody()) { out.write(body); }
    }

    public static void main(String[] args) throws IOException {
        String bucket = System.getenv("S3_BUCKET");
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalArgumentException("Set S3_BUCKET to your private photo bucket.");
        }
        // Region and temporary credentials come from the SDK's default provider chains.
        S3Client s3 = S3Client.builder().overrideConfiguration(c -> c
                .apiCallTimeout(Duration.ofSeconds(20))
                .apiCallAttemptTimeout(Duration.ofSeconds(10))).build();
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/", x -> {
            try {
                String path = x.getRequestURI().getPath();
                String method = x.getRequestMethod();
                if (path.equals("/health") && method.equals("GET")) {
                    json(x, 200, Map.of("status", "ok"));
                } else if (path.equals("/api/photos") && method.equals("GET")) {
                    var result = s3.listObjectsV2(ListObjectsV2Request.builder()
                            .bucket(bucket).prefix("photos/").maxKeys(100).build());
                    var photos = result.contents().stream().map(o -> Map.of(
                            "key", o.key(), "size", o.size())).toList();
                    json(x, 200, Map.of("photos", photos, "truncated", result.isTruncated()));
                } else if (path.equals("/api/photos") && method.equals("POST")) {
                    // Read at most limit + 1 bytes; never buffer an unbounded upload.
                    byte[] data = x.getRequestBody().readNBytes(MAX_UPLOAD + 1);
                    if (data.length == 0 || data.length > MAX_UPLOAD) {
                        json(x, data.length == 0 ? 400 : 413, Map.of("error", "Upload must contain 1 byte to 5 MiB"));
                        return;
                    }
                    String key = "photos/" + UUID.randomUUID();
                    s3.putObject(PutObjectRequest.builder().bucket(bucket).key(key)
                            .contentType("application/octet-stream").build(), RequestBody.fromBytes(data));
                    json(x, 201, Map.of("key", key, "size", data.length));
                } else if (path.equals("/") && method.equals("GET")) {
                    byte[] body = Files.readAllBytes(Path.of("web/index.html"));
                    x.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
                    x.sendResponseHeaders(200, body.length);
                    try (var out = x.getResponseBody()) { out.write(body); }
                } else if (path.equals("/api/photos") || path.equals("/health") || path.equals("/")) {
                    x.getResponseHeaders().set("Allow", path.equals("/api/photos") ? "GET, POST" : "GET");
                    json(x, 405, Map.of("error", "Method not allowed"));
                } else {
                    json(x, 404, Map.of("error", "Not found"));
                }
            } catch (software.amazon.awssdk.core.exception.SdkException e) {
                System.err.println("S3 request failed: " + e.getClass().getSimpleName());
                json(x, 503, Map.of("error", "Storage unavailable; check region, credentials, and permissions"));
            } finally { x.close(); }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> { server.stop(0); s3.close(); }));
        server.start();
        System.out.println("Photo Lab: http://127.0.0.1:" + server.getAddress().getPort());
    }
}
