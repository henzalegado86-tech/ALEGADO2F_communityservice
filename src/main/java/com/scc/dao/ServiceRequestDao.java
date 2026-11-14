package com.scc.dao;

import com.scc.model.ServiceRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRequestDao {

    public List<ServiceRequest> getAllRequests() {
        List<ServiceRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM service_requests ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving service requests: " + e.getMessage());
        }
        return requests;
    }

    public List<ServiceRequest> getRequestsByResident(int userId) {
        List<ServiceRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM service_requests WHERE user_id = ? ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToRequest(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving requests by resident: " + e.getMessage());
        }
        return requests;
    }

    public List<ServiceRequest> getAssignedRequests(int workerId) {
        List<ServiceRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM service_requests WHERE worker_id = ? ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, workerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToRequest(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving assigned requests: " + e.getMessage());
        }
        return requests;
    }

    public ServiceRequest getRequestById(int requestId) {
        String sql = "SELECT * FROM service_requests WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRequest(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving request by ID: " + e.getMessage());
        }
        return null;
    }

    // Overloaded method to support ResidentMenu calling (int, String, String, String)
    public boolean createRequest(int userId, String category, String description, String location) {
        String sql = "INSERT INTO service_requests (user_id, category, description, location, status) VALUES (?, ?, ?, ?, 'PENDING')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, category);
            pstmt.setString(3, description);
            pstmt.setString(4, location);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error creating service request: " + e.getMessage());
            return false;
        }
    }

    // Overloaded method to support ResidentMenu calling (int, int, String)
    public boolean createRequest(int userId, int categoryId, String description) {
        String sql = "INSERT INTO service_requests (user_id, category, description, status) VALUES (?, ?, ?, 'PENDING')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, String.valueOf(categoryId));
            pstmt.setString(3, description);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error creating service request: " + e.getMessage());
            return false;
        }
    }

    public boolean updateRequestStatus(int requestId, String status) {
        String sql = "UPDATE service_requests SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, requestId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating request status: " + e.getMessage());
            return false;
        }
    }

    private ServiceRequest mapResultSetToRequest(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userId = rs.getInt("user_id");

        int workerId = 0;
        try {
            workerId = rs.getInt("worker_id");
        } catch (Exception ignored) {}

        String category = rs.getString("category");
        String description = rs.getString("description");
        String status = rs.getString("status");

        return new ServiceRequest(id, userId, workerId, category, description, status);
    }
}