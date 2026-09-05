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
        // Obtiene la ruta solicitada (ej. /public/semana1/semana1.pdf)
        String requestPath = exchange.getRequestURI().getPath();

        // Mapea la ruta web al sistema de archivos local
        File file = new File("." + requestPath);

        if (file.exists() && !file.isDirectory()) {
            // 1. Detectar tipo MIME de forma explícita por extensión
            String contentType = obtenerContentType(requestPath);

            // 2. Establecer cabeceras necesarias para visualización
            exchange.getResponseHeaders().set("Content-Type", contentType);

            // Permite que el navegador abra el PDF en linea en lugar de forzar descarga
            if (contentType.equals("application/pdf")) {
                exchange.getResponseHeaders().set("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
            }

            exchange.sendResponseHeaders(200, file.length());

            // 3. Transmitir el archivo
            try (OutputStream os = exchange.getResponseBody();
                 FileInputStream fs = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int count;
                while ((count = fs.read(buffer)) != -1) {
                    os.write(buffer, 0, count);
                }
                os.flush();
            }
        } else {
            // Respuesta 404 si el archivo no existe
            String response = "404 - Archivo No Encontrado: " + requestPath;
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(404, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
                os.flush();
            }
        }
    }

    private String obtenerContentType(String path) {
        String lowerPath = path.toLowerCase();
        if (lowerPath.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerPath.endsWith(".html") || lowerPath.endsWith(".htm")) {
            return "text/html; charset=UTF-8";
        } else if (lowerPath.endsWith(".css")) {
            return "text/css";
        } else if (lowerPath.endsWith(".js")) {
            return "application/javascript";
        } else if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerPath.endsWith(".png")) {
            return "image/png";
        }

        // Intento secundario por el sistema operativo
        try {
            File f = new File("." + path);
            String probed = Files.probeContentType(f.toPath());
            if (probed != null) {
                return probed;
            }
        } catch (IOException ignored) {}

        return "application/octet-stream";
    }
}