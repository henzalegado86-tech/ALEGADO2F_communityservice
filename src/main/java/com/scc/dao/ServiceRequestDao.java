package com.scc.dao;

import com.scc.model.ServiceRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRequestDao {

    public ServiceRequestDao() {
        ensureTable();
    }

    private void ensureTable() {
        String sql = "CREATE TABLE IF NOT EXISTS service_requests (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "resident_id INTEGER NOT NULL, " +
                "category_id INTEGER NOT NULL, " +
                "description TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "created_at TEXT NOT NULL)";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw DatabaseConnection.fail(e);
        }
    }

    public void createRequest(int residentId, int categoryId, String description) {
        String sql = "INSERT INTO service_requests (resident_id, category_id, description, status, created_at) VALUES (?, ?, ?, 'PENDING', ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, residentId);
            ps.setInt(2, categoryId);
            ps.setString(3, description);
            ps.setString(4, DatabaseConnection.now());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DatabaseConnection.fail(e);
        }
    }

    public List<ServiceRequest> getRequestsByResident(int residentId) {
        List<ServiceRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM service_requests WHERE resident_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, residentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ServiceRequest(
                            rs.getInt("id"),
                            rs.getInt("resident_id"),
                            rs.getInt("category_id"),
                            rs.getString("description"),
                            rs.getString("status"),
                            rs.getString("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            throw DatabaseConnection.fail(e);
        }
        return list;
    }

    public ServiceRequest getRequestById(int id) {
        String sql = "SELECT * FROM service_requests WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ServiceRequest(
                            rs.getInt("id"),
                            rs.getInt("resident_id"),
                            rs.getInt("category_id"),
                            rs.getString("description"),
                            rs.getString("status"),
                            rs.getString("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            throw DatabaseConnection.fail(e);
        }
        return null;
    }
}