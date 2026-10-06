package com.portafolio.controller.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    // Usamos la URL del Connection Pooler (Soporta IPv4 e IPv6)
    private static final String URL = "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:6543/postgres?user=postgres.ksneazbwkkbgfcuywysu&password=JiNq6cSFXm7qzHl4";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL);
    }

    public static void init() {
        String sql = "CREATE TABLE IF NOT EXISTS evidencias (" +
                "id SERIAL PRIMARY KEY, " +
                "semana VARCHAR(50) NOT NULL, " +
                "descripcion TEXT NOT NULL, " +
                "pdf_url TEXT" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Base de datos PostgreSQL en Supabase conectada e inicializada con éxito.");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a Supabase PostgreSQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}