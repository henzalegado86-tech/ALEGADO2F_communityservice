package com.scc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:CommunityService.db";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DatabaseConnection() {}

    // Automatically initialize database tables and bootstrap admin on startup
    static {
        initializeDatabase();
    }

    public static void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "full_name TEXT, " +
                "email TEXT, " +
                "phone TEXT)";
        try (Connection c = getConnection();
             Statement st = c.createStatement()) {
            st.execute(sql);

            // Bootstrap default admin if table is empty
            String checkAdmin = "SELECT COUNT(*) FROM users";
            try (ResultSet rs = st.executeQuery(checkAdmin)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertAdmin = "INSERT INTO users(username, password, role, status, full_name, email, phone) VALUES('Administrator', 'admin123', 'ADMIN', 'APPROVED', 'System Administrator', 'admin@scc.com', '09000000000')";
                    st.executeUpdate(insertAdmin);
                }
            }
        } catch (SQLException e) {
            throw fail(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection c = DriverManager.getConnection(URL);
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON;");
        }
        return c;
    }

    public static String now() { return LocalDateTime.now().format(FMT); }

    public static IllegalStateException fail(SQLException e) {
        return new IllegalStateException("Database error: " + e.getMessage(), e);
    }
}