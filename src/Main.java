package com.portafolio;

import com.sun.net.httpserver.HttpServer;
import com.portafolio.controller.PortafolioController;
import com.portafolio.controller.AuthController;
import com.portafolio.controller.CPanelController;
import com.portafolio.controller.StaticController;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {
        File publicDir = new File("public");
        if (!publicDir.exists()) publicDir.mkdirs();

        int port = 8085;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Mapeo de Rutas conectadas a SQLite y protegidas por sesión
        server.createContext("/", new PortafolioController());
        server.createContext("/login", new AuthController.LoginHandler());
        server.createContext("/logout", new AuthController.LogoutHandler());
        server.createContext("/cpanel", new CPanelController());
        server.createContext("/subir-trabajo", new CPanelController.SubirTrabajoHandler());
        server.createContext("/editar-trabajo", new CPanelController.EditarTrabajoHandler());
        server.createContext("/eliminar-trabajo", new CPanelController.EliminarTrabajoHandler());
        server.createContext("/static/", new StaticController());

        server.setExecutor(null);
        System.out.println("Servidor MVC iniciado con éxito en http://localhost:" + port);
        server.start();
    }
}