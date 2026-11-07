# HTTP internals lab

Run the service locally and inspect responses with curl.

```sh
curl -i http://127.0.0.1:8080/health
curl -i http://127.0.0.1:8080/api/photos
curl -i http://127.0.0.1:8080/missing
curl -i -X POST http://127.0.0.1:8080/health
```

Predict the status codes before running: 200, 200, 404, and 405.

The server dispatches by request path and method. The response contains a status, headers, and UTF-8 bytes. Content-Length counts bytes, not Java characters. Closing the response stream completes the response.

Change the health JSON, recompile, and restart. Compare the response before and after. Change the HTML and refresh without restarting: the handler reads the file on each request.

127.0.0.1 binds the server to your computer. When deploying to AWS we will explicitly decide which interfaces and network rules should allow access.
