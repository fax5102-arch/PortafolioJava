package com.portafolio.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.portafolio.model.Evidencia;
import com.portafolio.view.ViewHtml;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PortafolioController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // 1. Manejar el POST cuando envían el formulario desde el cPanel
        if ("POST".equalsIgnoreCase(method) && path.endsWith("/subir-trabajo")) {
            // TODO: Aquí debes leer los datos del formulario multipart (Semana, Descripción, Archivo PDF)
            // y guardarlos usando CPanelController o tu clase de Base de Datos.
            // Ejemplo conceptual:
            // CPanelController.guardarEvidencia(exchange);

            // Redirigir de vuelta al panel para ver los cambios (Código 33 = See Other)
            exchange.getResponseHeaders().set("Location", "/"); // o la ruta de tu cPanel
            exchange.sendResponseHeaders(303, -1);
            return;
        }

        // 2. Manejar el GET normal (mostrar la vista con la lista de evidencias)
        List<Evidencia> listaEvidencias = CPanelController.obtenerEvidencias();
        boolean autenticado = AuthController.esAutenticado(exchange);

        String htmlResponse = ViewHtml.renderPortafolio(listaEvidencias, autenticado);

        byte[] bytes = htmlResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
            os.flush();
        }
    }
}