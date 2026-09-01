package com.portafolio.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class StaticController implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath().replace("/static/", "");
        File file = new File("public/" + path);

        if (file.exists() && !file.isDirectory()) {
            String mimeType = "application/octet-stream";
            if (path.endsWith(".css")) mimeType = "text/css";
            else if (path.endsWith(".js")) mimeType = "application/javascript";
            else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) mimeType = "image/jpeg";
            else if (path.endsWith(".png")) mimeType = "image/png";
            else if (path.endsWith(".pdf")) mimeType = "application/pdf";

            exchange.getResponseHeaders().set("Content-Type", mimeType);
            exchange.sendResponseHeaders(200, file.length());
            Files.copy(file.toPath(), exchange.getResponseBody());
            exchange.getResponseBody().close();
        } else {
            String response = "404 (Not Found)";
            exchange.sendResponseHeaders(404, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        }
    }
}