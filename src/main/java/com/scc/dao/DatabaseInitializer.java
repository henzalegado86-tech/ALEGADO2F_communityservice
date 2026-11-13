package com.scc.dao;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Automatically creates the activity_logs table on startup if it does not exist
            String createLogsTable = "CREATE TABLE IF NOT EXISTS activity_logs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "action_type TEXT NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
            stmt.execute(createLogsTable);

        } catch (Exception e) {
            System.out.println("Database initialization error: " + e.getMessage());
        }
    }
}