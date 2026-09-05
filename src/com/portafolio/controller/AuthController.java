package com.portafolio.controller;

import com.portafolio.config.SessionManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class AuthController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Redirige la petición entrante al handler de Login
        new LoginHandler().handle(exchange);
    }

    public static boolean esAutenticado(HttpExchange exchange) {
        String token = obtenerCookieSesion(exchange);
        return SessionManager.esValida(token);
    }

    public static String obtenerCookieSesion(HttpExchange exchange) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader != null) {
            String[] cookies = cookieHeader.split(";");
            for (String cookie : cookies) {
                String[] partes = cookie.trim().split("=");
                if (partes.length == 2 && partes[0].equals("SESION_PORTAFOLIO")) {
                    return partes[1];
                }
            }
        }
        return null;
    }

    public static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String query = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                String usuario = "";
                String password = "";
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length == 2) {
                        if (pair[0].equals("usuario")) usuario = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                        if (pair[0].equals("password")) password = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                    }
                }

                if ("admin".equals(usuario) && "1234".equals(password)) {
                    String token = SessionManager.crearSesion(usuario);
                    exchange.getResponseHeaders().add("Set-Cookie", "SESION_PORTAFOLIO=" + token + "; Path=/; HttpOnly");
                    exchange.getResponseHeaders().set("Location", "/cpanel");
                    exchange.sendResponseHeaders(303, -1);
                } else {
                    exchange.getResponseHeaders().set("Location", "/?error=credenciales");
                    exchange.sendResponseHeaders(303, -1);
                }
            }
        }
    }

    public static class LogoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String token = obtenerCookieSesion(exchange);
            SessionManager.destruirSesion(token);

            exchange.getResponseHeaders().add("Set-Cookie", "SESION_PORTAFOLIO=; Path=/; Max-Age=0; HttpOnly");
            exchange.getResponseHeaders().set("Location", "/");
            exchange.sendResponseHeaders(303, -1);
        }
    }
}