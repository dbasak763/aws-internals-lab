package lab;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** A deliberately small starting point; AWS integrations arrive in later lessons. */
public final class Main {
    private static void respond(HttpExchange exchange, int status, String type, byte[] body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", type);
        exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.sendResponseHeaders(status, body.length);
        try (var out = exchange.getResponseBody()) { out.write(body); }
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (!exchange.getRequestMethod().equals("GET")) {
                respond(exchange, 405, "text/plain; charset=utf-8", "Method not allowed".getBytes(StandardCharsets.UTF_8));
            } else if (path.equals("/health")) {
                respond(exchange, 200, "application/json; charset=utf-8", "{\"status\":\"ok\"}".getBytes(StandardCharsets.UTF_8));
            } else if (path.equals("/api/photos")) {
                respond(exchange, 200, "application/json; charset=utf-8", "[]".getBytes(StandardCharsets.UTF_8));
            } else if (path.equals("/")) {
                respond(exchange, 200, "text/html; charset=utf-8", Files.readAllBytes(Path.of("web", "index.html")));
            } else {
                respond(exchange, 404, "text/plain; charset=utf-8", "Not found".getBytes(StandardCharsets.UTF_8));
            }
        });
        server.start();
        System.out.println("Photo Lab running at http://127.0.0.1:" + server.getAddress().getPort());
    }
}
