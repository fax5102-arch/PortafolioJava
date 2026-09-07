package com.portafolio.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:portafolio.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("Error: No se encontró el driver de SQLite en el classpath: " + e.getMessage());
        }

        Connection conn = DriverManager.getConnection(URL);
        inicializarBaseDatos(conn);
        return conn;
    }

    private static void inicializarBaseDatos(Connection conn) {
        String sqlCreateTable = "CREATE TABLE IF NOT EXISTS evidencias (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "semana TEXT NOT NULL, " +
                "descripcion TEXT NOT NULL, " +
                "pdf_url TEXT, " +
                "fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP);";

        try (Statement stmt = conn.createStatement()) {
            // 1. Crear la tabla si no existe
            stmt.execute(sqlCreateTable);

            // 2. Insertar datos iniciales SOLO si la tabla está completamente vacía
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM evidencias;");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO evidencias (semana, descripcion, pdf_url) VALUES " +
                        "('Semana 1', 'Informe y documentación de la Semana 1', '/public/semana1/semana1.pdf');");

                stmt.execute("INSERT INTO evidencias (semana, descripcion, pdf_url) VALUES " +
                        "('Semana 2', 'Actores de un Proyecto Web', '/public/semana2/Actores de un Proyecto Web.pdf'), " +
                        "('Semana 2', 'Herramientas de Gestión', '/public/semana2/Herramientas de Gestión.pdf'), " +
                        "('Semana 2', 'Organización del Trabajo', '/public/semana2/Organización del Trabajo.pdf'), " +
                        "('Semana 2', 'Roles y Responsabilidades', '/public/semana2/Roles y Responsabilidades.pdf'), " +
                        "('Semana 2', 'Tipos de Proyecto Web', '/public/semana2/Tipos de Proyecto Web.pdf');");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}