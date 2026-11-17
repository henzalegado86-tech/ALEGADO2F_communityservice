package com.scc.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create users table if not exists
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "username TEXT NOT NULL UNIQUE, "
                    + "password TEXT NOT NULL, "
                    + "roles TEXT NOT NULL, "
                    + "status TEXT NOT NULL, "
                    + "email TEXT, "
                    + "phone_number TEXT)";
            stmt.execute(createUsersTable);

            // Create categories table if not exists
            String createCategoriesTable = "CREATE TABLE IF NOT EXISTS categories ("
                    + "category_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "category_name TEXT NOT NULL UNIQUE, "
                    + "description TEXT, "
                    + "is_active INTEGER NOT NULL DEFAULT 1)";
            stmt.execute(createCategoriesTable);

            // Create service_requests table if not exists
            String createRequestsTable = "CREATE TABLE IF NOT EXISTS service_requests ("
                    + "request_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "resident_id INTEGER NOT NULL, "
                    + "assigned_worker_id INTEGER, "
                    + "category_id INTEGER NOT NULL, "
                    + "location TEXT, "
                    + "issue_description TEXT, "
                    + "request_date TEXT, "
                    + "status TEXT NOT NULL DEFAULT 'Pending', "
                    + "completion_notes TEXT, "
                    + "resolved_date TEXT, "
                    + "FOREIGN KEY (resident_id) REFERENCES users(id), "
                    + "FOREIGN KEY (assigned_worker_id) REFERENCES users(id), "
                    + "FOREIGN KEY (category_id) REFERENCES categories(category_id))";
            stmt.execute(createRequestsTable);

            // Upgrade an older activity_logs table (e.g. columns id, user_id, action, timestamp)
            upgradeActivityLogsIfNeeded(conn, stmt);

            // Create activity_logs table if not exists
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

    /**
     * If activity_logs already exists with the old structure (no action_type/description
     * columns, or a foreign key without ON DELETE CASCADE), rebuild it in the new structure
     * and keep the old rows.
     */
    private static void upgradeActivityLogsIfNeeded(Connection conn, Statement stmt) throws SQLException {
        boolean exists = false;
        boolean hasActionType = false;
        boolean hasDescription = false;
        boolean hasOldAction = false;
        try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(activity_logs)")) {
            while (rs.next()) {
                exists = true;
                String col = rs.getString("name");
                if ("action_type".equalsIgnoreCase(col)) hasActionType = true;
                if ("description".equalsIgnoreCase(col)) hasDescription = true;
                if ("action".equalsIgnoreCase(col)) hasOldAction = true;
            }
        }
        if (!exists) return;

        boolean hasCascade = false;
        try (ResultSet rs = stmt.executeQuery("PRAGMA foreign_key_list(activity_logs)")) {
            while (rs.next()) {
                if ("CASCADE".equalsIgnoreCase(rs.getString("on_delete"))) hasCascade = true;
            }
        }
        if (hasActionType && hasDescription && hasCascade) return; // already up to date

        String copyColumns;
        if (hasActionType && hasDescription) {
            copyColumns = "SELECT user_id, action_type, description, timestamp FROM activity_logs_old";
        } else if (hasOldAction) {
            copyColumns = "SELECT user_id, 'LEGACY', action, timestamp FROM activity_logs_old";
        } else {
            copyColumns = null; // unknown structure: start with an empty log table
        }

        stmt.execute("ALTER TABLE activity_logs RENAME TO activity_logs_old");
        stmt.execute("CREATE TABLE activity_logs ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "action_type TEXT NOT NULL, "
                + "description TEXT NOT NULL, "
                + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");
        if (copyColumns != null) {
            // Rows whose user no longer exists are skipped so the foreign key stays valid.
            stmt.execute("INSERT INTO activity_logs (user_id, action_type, description, timestamp) "
                    + copyColumns + " WHERE user_id IN (SELECT id FROM users)");
        }
        stmt.execute("DROP TABLE activity_logs_old");
    }
}
