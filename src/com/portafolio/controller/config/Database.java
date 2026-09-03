package com.portafolio.config;

import java.sql.Connection;
import java.sql.DriverManager;
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
        String sql = "CREATE TABLE IF NOT EXISTS evidencias (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "semana TEXT NOT NULL, " +
                "descripcion TEXT NOT NULL, " +
                "pdf_url TEXT, " +
                "fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP);";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}