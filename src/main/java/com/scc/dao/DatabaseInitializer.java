package com.scc.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create users table matching User.java and UserDao.java
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "username TEXT NOT NULL UNIQUE, "
                    + "password TEXT NOT NULL, "
                    + "role TEXT NOT NULL, "
                    + "status TEXT NOT NULL, "
                    + "full_name TEXT, "
                    + "email TEXT, "
                    + "phone TEXT)";
            stmt.execute(createUsersTable);

            // Seed default administrator if users table is empty
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.executeUpdate("INSERT INTO users (username, password, role, status, full_name, email, phone) "
                            + "VALUES ('Administrator', 'admin123', 'ADMIN', 'APPROVED', 'System Administrator', 'admin@scc.com', '09000000000')");
                }
            }

            // Create categories table matching CategoryDao.java
            String createCategoriesTable = "CREATE TABLE IF NOT EXISTS categories ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL)";
            stmt.execute(createCategoriesTable);

            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM categories")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO categories(name) VALUES ('General Community Inquiry');");
                    stmt.execute("INSERT INTO categories(name) VALUES ('Infrastructure & Road Repair');");
                    stmt.execute("INSERT INTO categories(name) VALUES ('Sanitation & Garbage Collection');");
                }
            }

            // Create service_requests table matching ServiceRequest.java and ServiceRequestDao.java
            String createRequestsTable = "CREATE TABLE IF NOT EXISTS service_requests ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "resident_id INTEGER NOT NULL, "
                    + "category_id INTEGER NOT NULL, "
                    + "description TEXT NOT NULL, "
                    + "status TEXT NOT NULL DEFAULT 'PENDING', "
                    + "created_at TEXT NOT NULL, "
                    + "FOREIGN KEY (resident_id) REFERENCES users(id), "
                    + "FOREIGN KEY (category_id) REFERENCES categories(id))";
            stmt.execute(createRequestsTable);

            // Create activity_logs table matching ActivityLog.java and ActivityLogDao.java
            String createLogsTable = "CREATE TABLE IF NOT EXISTS activity_logs ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "user_id INTEGER NOT NULL, "
                    + "action_type TEXT NOT NULL, "
                    + "description TEXT NOT NULL, "
                    + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)";
            stmt.execute(createLogsTable);

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}