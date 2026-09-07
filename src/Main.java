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
            // 1. Inicializar conexión a la base de datos
            System.out.println("Inicializando la base de datos...");
            try (Connection conn = Database.getConnection()) {
                if (conn != null) {
                    System.out.println("¡Base de datos conectada e inicializada con éxito!");
                }
            } catch (SQLException e) {
                System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            }

            // 2. Crear el servidor HTTP
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // 3. Rutas de vistas
            server.createContext("/", new PortafolioController());
            server.createContext("/cpanel", new CPanelController());

            // 4. Rutas de acciones del CPanel
            server.createContext("/subir-trabajo", new CPanelController.SubirTrabajoHandler());
            server.createContext("/editar-trabajo", new CPanelController.EditarTrabajoHandler());
            server.createContext("/eliminar-trabajo", new CPanelController.EliminarTrabajoHandler());

            // 5. Autenticación
            server.createContext("/login", new AuthController.LoginHandler());
            server.createContext("/auth", new AuthController.LoginHandler());
            server.createContext("/logout", new AuthController.LogoutHandler());

            // 6. Archivos estáticos
            server.createContext("/static", new StaticController());
            server.createContext("/public", new StaticFileHandler());

            // 7. Iniciar servidor
            server.setExecutor(null);
            server.start();

            System.out.println("==================================================");
            System.out.println("Servidor corriendo en: http://localhost:" + PORT + "/");
            System.out.println("==================================================");

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor HTTP: " + e.getMessage());
            e.printStackTrace();
        }
    }
}