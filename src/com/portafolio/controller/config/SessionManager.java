package com.portafolio.config;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private static final Map<String, String> sesionesActivas = new HashMap<>();

    public static String crearSesion(String usuario) {
        String token = UUID.randomUUID().toString();
        sesionesActivas.put(token, usuario);
        return token;
    }

    public static boolean esValida(String token) {
        return token != null && sesionesActivas.containsKey(token);
    }

    public static void destruirSesion(String token) {
        if (token != null) {
            sesionesActivas.remove(token);
        }
    }
}