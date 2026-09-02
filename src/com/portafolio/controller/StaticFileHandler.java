package com.portafolio.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class StaticFileHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Obtiene la ruta solicitada (ej. /static/trabajo_123.pdf)
        String requestPath = exchange.getRequestURI().getPath();

        // Mapea la ruta web a la carpeta local de tu proyecto/contenedor
        File file = new File("." + requestPath);

        if (file.exists() && !file.isDirectory()) {
            // Detectar el tipo de contenido (Content-Type)
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, file.length());

            try (OutputStream os = exchange.getResponseBody();
                 FileInputStream fs = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int count;
                while ((count = fs.read(buffer)) != -1) {
                    os.write(buffer, 0, count);
                }
                os.flush();
            }
        } else {
            String response = "Not Found";
            exchange.sendResponseHeaders(404, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
                os.flush();
            }
        }
    }
}