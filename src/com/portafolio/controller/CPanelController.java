package com.portafolio.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.portafolio.config.Database;
import com.portafolio.model.Evidencia;
import com.portafolio.view.ViewHtml;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;

public class CPanelController implements HttpHandler {

    public static List<Evidencia> obtenerEvidencias() {
        List<Evidencia> lista = new ArrayList<>();
        String sql = "SELECT id, semana, descripcion, pdf_url FROM evidencias";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Evidencia(
                        String.valueOf(rs.getInt("id")),
                        rs.getString("semana"),
                        rs.getString("descripcion"),
                        rs.getString("pdf_url")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!AuthController.esAutenticado(exchange)) {
            exchange.getResponseHeaders().set("Location", "/");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        List<Evidencia> listaEvidencias = obtenerEvidencias();
        String htmlResponse = ViewHtml.renderCPanel(listaEvidencias);
        sendResponse(exchange, 200, htmlResponse);
    }

    public static class SubirTrabajoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println(">>> Recibida peticion POST en /subir-trabajo");
            if (!AuthController.esAutenticado(exchange) || !"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                return;
            }

            byte[] data = readRequestBody(exchange);
            String boundary = extractBoundary(exchange);

            if (boundary != null) {
                String semana = extractField(data, boundary, "semana");
                String descripcion = extractField(data, boundary, "descripcion");
                String pdfUrl = "";

                System.out.println("Semana recibida: " + semana);

                if (hasFileAttached(data, boundary, "pdfFile")) {
                    String filename = "trabajo_" + System.currentTimeMillis() + ".pdf";
                    File pubDir = new File("public");
                    if (!pubDir.exists()) pubDir.mkdirs();

                    saveFileField(data, boundary, "pdfFile", "public/" + filename);
                    pdfUrl = "/static/" + filename;
                    System.out.println("Archivo PDF guardado como: " + filename);
                }

                if (semana != null && !semana.isEmpty()) {
                    String sql = "INSERT INTO evidencias (semana, descripcion, pdf_url) VALUES (?, ?, ?)";
                    try (Connection conn = Database.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, semana);
                        pstmt.setString(2, descripcion);
                        pstmt.setString(3, pdfUrl);
                        pstmt.executeUpdate();
                        System.out.println("Evidencia insertada con éxito en SQLite.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println(">>> Error: No se pudo detectar el boundary en la peticion.");
            }

            exchange.getResponseHeaders().set("Location", "/cpanel");
            exchange.sendResponseHeaders(302, -1);
        }
    }

    public static class EditarTrabajoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!AuthController.esAutenticado(exchange) || !"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                return;
            }

            byte[] data = readRequestBody(exchange);
            String boundary = extractBoundary(exchange);

            if (boundary != null) {
                String idStr = extractField(data, boundary, "id");
                String nuevaSemana = extractField(data, boundary, "semana");
                String nuevaDescripcion = extractField(data, boundary, "descripcion");

                if (idStr != null && !idStr.isEmpty()) {
                    int id = Integer.parseInt(idStr);

                    String selectSql = "SELECT pdf_url FROM evidencias WHERE id = ?";
                    String pdfUrlActual = "";
                    try (Connection conn = Database.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                        pstmt.setInt(1, id);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next()) pdfUrlActual = rs.getString("pdf_url");
                        }
                    } catch (SQLException e) { e.printStackTrace(); }

                    boolean hasNewFile = hasFileAttached(data, boundary, "pdfFile");
                    String finalPdfUrl = pdfUrlActual;

                    if (hasNewFile) {
                        if (pdfUrlActual != null && pdfUrlActual.startsWith("/static/")) {
                            File oldFile = new File("public/" + pdfUrlActual.replace("/static/", ""));
                            if (oldFile.exists()) oldFile.delete();
                        }
                        String filename = "trabajo_" + System.currentTimeMillis() + ".pdf";
                        File pubDir = new File("public");
                        if (!pubDir.exists()) pubDir.mkdirs();

                        saveFileField(data, boundary, "pdfFile", "public/" + filename);
                        finalPdfUrl = "/static/" + filename;
                    }

                    // Corrección aquí: se eliminó fecha_actualizacion = CURRENT_TIMESTAMP
                    String updateSql = "UPDATE evidencias SET semana = ?, descripcion = ?, pdf_url = ? WHERE id = ?";
                    try (Connection conn = Database.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                        pstmt.setString(1, nuevaSemana);
                        pstmt.setString(2, nuevaDescripcion);
                        pstmt.setString(3, finalPdfUrl);
                        pstmt.setInt(4, id);
                        pstmt.executeUpdate();
                    } catch (SQLException e) { e.printStackTrace(); }
                }
            }

            exchange.getResponseHeaders().set("Location", "/cpanel");
            exchange.sendResponseHeaders(302, -1);
        }
    }

    public static class EliminarTrabajoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!AuthController.esAutenticado(exchange) || !"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                return;
            }

            Map<String, String> params = parseSimpleFormData(exchange);
            String idStr = params.get("id");

            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                String selectSql = "SELECT pdf_url FROM evidencias WHERE id = ?";
                try (Connection conn = Database.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                    pstmt.setInt(1, id);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            String pdfUrl = rs.getString("pdf_url");
                            if (pdfUrl != null && pdfUrl.startsWith("/static/")) {
                                File fileToDelete = new File("public/" + pdfUrl.replace("/static/", ""));
                                if (fileToDelete.exists()) fileToDelete.delete();
                            }
                        }
                    }
                } catch (SQLException e) { e.printStackTrace(); }

                String deleteSql = "DELETE FROM evidencias WHERE id = ?";
                try (Connection conn = Database.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                } catch (SQLException e) { e.printStackTrace(); }
            }

            exchange.getResponseHeaders().set("Location", "/cpanel");
            exchange.sendResponseHeaders(302, -1);
        }
    }

    private static byte[] readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) baos.write(buffer, 0, bytesRead);
            return baos.toByteArray();
        }
    }

    private static String extractBoundary(HttpExchange exchange) {
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType != null && contentType.contains("boundary=")) {
            return contentType.substring(contentType.indexOf("boundary=") + 9).trim();
        }
        return null;
    }

    private static String extractField(byte[] data, String boundary, String fieldName) {
        try {
            String token = "name=\"" + fieldName + "\"";
            byte[] tokenBytes = token.getBytes(StandardCharsets.UTF_8);
            int startHeader = indexOfBytes(data, tokenBytes, 0);
            if (startHeader == -1) return "";

            int startData = findHeaderEnd(data, startHeader);
            if (startData == -1) return "";

            byte[] boundaryBytes = ("\r\n--" + boundary).getBytes(StandardCharsets.UTF_8);
            int endData = indexOfBytes(data, boundaryBytes, startData);
            if (endData == -1) {
                boundaryBytes = ("\n--" + boundary).getBytes(StandardCharsets.UTF_8);
                endData = indexOfBytes(data, boundaryBytes, startData);
            }
            if (endData == -1) endData = data.length;

            byte[] campoBytes = Arrays.copyOfRange(data, startData, endData);
            String val = new String(campoBytes, StandardCharsets.UTF_8).trim();

            if (val.endsWith("\r")) val = val.substring(0, val.length() - 1);
            if (val.endsWith("\n")) val = val.substring(0, val.length() - 1);

            return val;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static int findHeaderEnd(byte[] data, int startFrom) {
        for (int i = startFrom; i < data.length - 3; i++) {
            if (data[i] == '\r' && data[i+1] == '\n' && data[i+2] == '\r' && data[i+3] == '\n') {
                return i + 4;
            }
            if (data[i] == '\n' && data[i+1] == '\n') {
                return i + 2;
            }
        }
        return -1;
    }

    private static int indexOfBytes(byte[] outer, byte[] target, int start) {
        for (int i = start; i <= outer.length - target.length; i++) {
            boolean found = true;
            for (int j = 0; j < target.length; j++) {
                if (outer[i + j] != target[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }

    private static boolean hasFileAttached(byte[] data, String boundary, String fieldName) {
        try {
            String token = "name=\"" + fieldName + "\"; filename=";
            byte[] tokenBytes = token.getBytes(StandardCharsets.UTF_8);
            int startHeader = indexOfBytes(data, tokenBytes, 0);
            if (startHeader == -1) return false;

            int startData = findHeaderEnd(data, startHeader);
            if (startData == -1) return false;

            byte[] boundaryBytes = ("\r\n--" + boundary).getBytes(StandardCharsets.UTF_8);
            int endData = indexOfBytes(data, boundaryBytes, startData);
            if (endData == -1) {
                boundaryBytes = ("\n--" + boundary).getBytes(StandardCharsets.UTF_8);
                endData = indexOfBytes(data, boundaryBytes, startData);
            }
            if (endData == -1) return false;

            return (endData - startData) > 10;
        } catch (Exception e) { return false; }
    }

    private static void saveFileField(byte[] data, String boundary, String fieldName, String outputPath) {
        try {
            String token = "name=\"" + fieldName + "\"; filename=";
            byte[] tokenBytes = token.getBytes(StandardCharsets.UTF_8);
            int startHeader = indexOfBytes(data, tokenBytes, 0);
            if (startHeader == -1) return;

            int startData = findHeaderEnd(data, startHeader);
            if (startData == -1) return;

            byte[] boundaryBytes = ("\r\n--" + boundary).getBytes(StandardCharsets.UTF_8);
            int endData = indexOfBytes(data, boundaryBytes, startData);
            if (endData == -1) {
                boundaryBytes = ("\n--" + boundary).getBytes(StandardCharsets.UTF_8);
                endData = indexOfBytes(data, boundaryBytes, startData);
            }
            if (endData == -1) return;

            if ((endData - startData) > 0) {
                byte[] fileBytes = Arrays.copyOfRange(data, startData, endData);
                Files.write(new File(outputPath).toPath(), fileBytes);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static Map<String, String> parseSimpleFormData(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder formData = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) formData.append(line);

        Map<String, String> map = new HashMap<>();
        for (String pair : formData.toString().split("&")) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                map.put(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
            }
        }
        return map;
    }

    private void sendResponse(HttpExchange exchange, int code, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
            os.flush();
        }
    }
}