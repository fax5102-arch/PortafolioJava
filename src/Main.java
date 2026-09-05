package com.portafolio;

import com.portafolio.config.Database;
import com.portafolio.controller.AuthController;
import com.portafolio.controller.CPanelController;
import com.portafolio.controller.PortafolioController;
import com.portafolio.controller.StaticController;
import com.portafolio.controller.StaticFileHandler;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            // 1. Inicializar y probar la conexión a la Base de Datos SQLite
            System.out.println("Inicializando la base de datos...");
            try (Connection conn = Database.getConnection()) {
                if (conn != null) {
                    System.out.println("¡Base de datos conectada e inicializada con éxito!");
                }
            } catch (SQLException e) {
                System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            }

            // 2. Crear el servidor HTTP en el puerto 8080
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // 3. Registrar los manejadores de rutas (Controllers)

            // Ruta principal de la vista pública (Portafolio)
            server.createContext("/", new PortafolioController());

            // Rutas de administración y autenticación
            server.createContext("/auth", new AuthController());
            server.createContext("/cpanel", new CPanelController());

            // Manejador de archivos estáticos genérico (si existe en tu proyecto)
            server.createContext("/static", new StaticController());

            // Manejador clave para archivos PDF, imágenes y CSS dentro de /public
            server.createContext("/public", new StaticFileHandler());

            // 4. Iniciar el servidor
            server.setExecutor(null); // Usar el ejecutor por defecto
            server.start();

            System.out.println("==================================================");
            System.out.println("Servidor iniciado correctamente en:");
            System.out.println("http://localhost:" + PORT + "/");
            System.out.println("==================================================");

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor HTTP: " + e.getMessage());
            e.printStackTrace();
        }
    }
}