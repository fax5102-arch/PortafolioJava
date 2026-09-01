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