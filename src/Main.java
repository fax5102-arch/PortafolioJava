package com.portafolio;

import com.sun.net.httpserver.HttpServer;
import com.portafolio.controller.*;
import com.portafolio.controller.config.Database;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            // 1. Inicializar base de datos
            System.out.println("Inicializando la base de datos...");
            Database.init();

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
            System.out.println("Servidor iniciado en http://localhost:" + PORT);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}