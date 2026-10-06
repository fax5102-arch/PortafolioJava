package com.portafolio.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.portafolio.controller.config.Database;
import com.portafolio.controller.config.SupabaseStorageService;
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
        // Ordenamiento por semana ascendente y luego por id
        String sql = "SELECT id, semana, descripcion, pdf_url FROM evidencias ORDER BY semana ASC, id ASC";
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
            if (!AuthController.esAutenticado(exchange) || !"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try (InputStream is = exchange.getRequestBody()) { is.readAllBytes(); }
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                exchange.close();
                return;
            }

            try {
                byte[] data = readRequestBody(exchange);
                String boundary = extractBoundary(exchange);

                if (boundary != null) {
                    String semana = extractField(data, boundary, "semana");
                    String descripcion = extractField(data, boundary, "descripcion");
                    String pdfUrl = "";

                    if (hasFileAttached(data, boundary, "pdfFile")) {
                        String filename = "trabajo_" + System.currentTimeMillis() + ".pdf";
                        File tempFile = File.createTempFile("upload_", ".pdf");
                        saveFileField(data, boundary, "pdfFile", tempFile.getAbsolutePath());

                        try {
                            pdfUrl = SupabaseStorageService.subirPDF(tempFile, filename);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (tempFile.exists()) tempFile.delete();
                        }
                    }

                    if (semana != null && !semana.trim().isEmpty()) {
                        String sql = "INSERT INTO evidencias (semana, descripcion, pdf_url) VALUES (?, ?, ?)";
                        try (Connection conn = Database.getConnection();
                             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setString(1, semana.trim());
                            pstmt.setString(2, descripcion != null ? descripcion.trim() : "");
                            pstmt.setString(3, pdfUrl);
                            pstmt.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            exchange.getResponseHeaders().set("Location", "/cpanel");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        }
    }

    public static class EditarTrabajoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!AuthController.esAutenticado(exchange) || !"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try (InputStream is = exchange.getRequestBody()) { is.readAllBytes(); }
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                exchange.close();
                return;
            }

            try {
                byte[] data = readRequestBody(exchange);
                String boundary = extractBoundary(exchange);

                if (boundary != null) {
                    String idStr = extractField(data, boundary, "id");
                    String nuevaSemana = extractField(data, boundary, "semana");
                    String nuevaDescripcion = extractField(data, boundary, "descripcion");

                    if (idStr != null && !idStr.trim().isEmpty()) {
                        int id = Integer.parseInt(idStr.trim());

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
                            String filename = "trabajo_" + System.currentTimeMillis() + ".pdf";
                            File tempFile = File.createTempFile("upload_edit_", ".pdf");
                            saveFileField(data, boundary, "pdfFile", tempFile.getAbsolutePath());

                            try {
                                finalPdfUrl = SupabaseStorageService.subirPDF(tempFile, filename);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (tempFile.exists()) tempFile.delete();
                            }
                        }

                        String updateSql = "UPDATE evidencias SET semana = ?, descripcion = ?, pdf_url = ? WHERE id = ?";
                        try (Connection conn = Database.getConnection();
                             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                            pstmt.setString(1, nuevaSemana != null ? nuevaSemana.trim() : "");
                            pstmt.setString(2, nuevaDescripcion != null ? nuevaDescripcion.trim() : "");
                            pstmt.setString(3, finalPdfUrl);
                            pstmt.setInt(4, id);
                            pstmt.executeUpdate();
                        } catch (SQLException e) { e.printStackTrace(); }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            exchange.getResponseHeaders().set("Location", "/cpanel");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        }
    }

    public static class EliminarTrabajoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!AuthController.esAutenticado(exchange) || !"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try (InputStream is = exchange.getRequestBody()) { is.readAllBytes(); }
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                exchange.close();
                return;
            }

            try {
                Map<String, String> params = parseSimpleFormData(exchange);
                String idStr = params.get("id");

                if (idStr != null && !idStr.trim().isEmpty()) {
                    int id = Integer.parseInt(idStr.trim());

                    String deleteSql = "DELETE FROM evidencias WHERE id = ?";
                    try (Connection conn = Database.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                        pstmt.setInt(1, id);
                        pstmt.executeUpdate();
                    } catch (SQLException e) { e.printStackTrace(); }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            exchange.getResponseHeaders().set("Location", "/cpanel");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
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
            String boundary = contentType.substring(contentType.indexOf("boundary=") + 9).trim();
            if (boundary.contains(";")) {
                boundary = boundary.substring(0, boundary.indexOf(";")).trim();
            }
            if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
                boundary = boundary.substring(1, boundary.length() - 1);
            }
            return boundary;
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

            if (endData > startData && data[endData - 1] == '\n') endData--;
            if (endData > startData && data[endData - 1] == '\r') endData--;

            byte[] campoBytes = Arrays.copyOfRange(data, startData, endData);
            return new String(campoBytes, StandardCharsets.UTF_8).trim();
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
            String token = "name=\"" + fieldName + "\"";
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

            return (endData - startData) > 5;
        } catch (Exception e) {
            return false;
        }
    }

    private static void saveFileField(byte[] data, String boundary, String fieldName, String outputPath) {
        try {
            String token = "name=\"" + fieldName + "\"";
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