package com.scc.ui;

import com.scc.dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ActivityLogUI {

    public void show() {
        System.out.println("\n" + "=".repeat(115));
        System.out.printf("%-6s | %-8s | %-18s | %-45s | %-19s%n",
                "ID", "User ID", "Action Type", "Description", "Timestamp");
        System.out.println("-".repeat(115));

        String sql = "SELECT id, user_id, action_type, description, timestamp FROM activity_logs ORDER BY id DESC";

        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                int id = rs.getInt("id");
                int userId = rs.getInt("user_id");
                String actionType = rs.getString("action_type");
                String description = rs.getString("description");
                String timestamp = rs.getString("timestamp");

                // Handle potential nulls safely
                if (actionType == null) actionType = "";
                if (description == null) description = "";
                if (timestamp == null) timestamp = "";

                // Truncate long descriptions if they exceed layout width using .length()
                if (description.length() > 42) {
                    description = description.substring(0, 39) + "...";
                }

                System.out.printf("%-6d | %-8d | %-18s | %-45s | %-19s%n",
                        id, userId, actionType, description, timestamp);
            }

            if (!hasRows) {
                System.out.println("No activity logs found.");
            }

        } catch (SQLException e) {
            System.out.println("Error loading activity logs: " + e.getMessage());
        }

        System.out.println("=".repeat(115) + "\n");
    }
}