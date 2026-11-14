package com.scc.dao;

import com.scc.model.ActivityLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDao {

    public void logAction(int userId, String actionType, String description) {
        String sql = "INSERT INTO activity_logs(user_id, action_type, description, timestamp) VALUES(?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, actionType);
            ps.setString(3, description);
            ps.setString(4, DatabaseConnection.now());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DatabaseConnection.fail(e);
        }
    }

    // Alias method to match any calls using .log(...)
    public void log(int userId, String actionType, String description) {
        logAction(userId, actionType, description);
    }

    public List<ActivityLog> getAllLogs() {
        String sql = "SELECT id, user_id, action_type, description, timestamp FROM activity_logs ORDER BY id DESC";
        List<ActivityLog> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ActivityLog(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("action_type"),
                        rs.getString("description"),
                        rs.getString("timestamp")
                ));
            }
        } catch (SQLException e) {
            throw DatabaseConnection.fail(e);
        }
        return list;
    }

    public List<ActivityLog> getLogsByUserId(int userId) {
        String sql = "SELECT id, user_id, action_type, description, timestamp FROM activity_logs WHERE user_id = ? ORDER BY id DESC";
        List<ActivityLog> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ActivityLog(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("action_type"),
                            rs.getString("description"),
                            rs.getString("timestamp")
                    ));
                }
            }
        } catch (SQLException e) {
            throw DatabaseConnection.fail(e);
        }
        return list;
    }
}