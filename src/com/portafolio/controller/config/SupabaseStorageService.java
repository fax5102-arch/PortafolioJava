package com.portafolio.controller.config;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SupabaseStorageService {

    private static final String SUPABASE_URL = "https://ksneazbwkkbgfcuywysu.supabase.co";

    // Clave Legacy service_role activa
    private static final String RAW_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImtzbmVhemJ3a2tiZ2ZjdXl3eXN1Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc5MTI1Njc3NywiZXhwIjoyMTA2ODMyNzc3fQ.l6VDPNJmBjoxIPRPlwN2pI23IGKLTBfKtt6SjdAKaME";
    private static final String SUPABASE_KEY = RAW_KEY.replaceAll("\\s+", "").trim();

    private static final String BUCKET_NAME = "evidencias";

    public static String subirPDF(File file, String nombreArchivo) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String nombreLimpio = nombreArchivo.replaceAll("\\s+", "_");
        String uploadUrl = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + nombreLimpio;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .header("apikey", SUPABASE_KEY)
                .header("Content-Type", "application/pdf")
                .header("x-upsert", "true")
                .POST(HttpRequest.BodyPublishers.ofFile(file.toPath()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            return SUPABASE_URL + "/storage/v1/object/public/" + BUCKET_NAME + "/" + nombreLimpio;
        } else {
            throw new IOException("Error al subir a Supabase. Código: " + response.statusCode() + " | Detalle: " + response.body());
        }
    }
}