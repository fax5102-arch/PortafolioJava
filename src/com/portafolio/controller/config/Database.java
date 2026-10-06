package com.portafolio.controller.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:portafolio.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void inicializarBD() {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS evidencias (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "semana TEXT NOT NULL, " +
                "descripcion TEXT, " +
                "pdf_url TEXT" +
                ");";

        // Consulta para eliminar exactamente los registros de Semana 1 y Semana 2
        String sqlDeleteSemanasViejas = "DELETE FROM evidencias WHERE semana IN ('Semana 1', 'Semana 2');";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Crea la tabla si no existe
            stmt.execute(sqlCreate);

            // 2. Elimina Semana 1 y Semana 2 de la base de datos
            int borrados = stmt.executeUpdate(sqlDeleteSemanasViejas);
            if (borrados > 0) {
                System.out.println(">>> Se eliminaron " + borrados + " registros obsoletos (Semana 1 / Semana 2).");
            }

            System.out.println("¡Base de datos conectada e inicializada con éxito!");

        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }
}