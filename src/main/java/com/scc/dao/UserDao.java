package com.scc.dao;

import com.scc.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public UserDao() {
        ensureTable();
    }

    private void ensureTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE, " +
                "password TEXT, " +
                "role TEXT, " +
                "status TEXT, " +
                "full_name TEXT, " +
                "email TEXT, " +
                "phone TEXT)";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement()) {
            st.execute(sql);

            // Bootstrap default admin if table is empty
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                String adminSql = "INSERT INTO users (username, password, role, status, full_name, email, phone) " +
                        "VALUES ('Administrator', 'admin123', 'ADMIN', 'APPROVED', 'System Administrator', 'admin@scc.com', '0000000000')";
                st.execute(adminSql);
            }
        } catch (SQLException e) {
            throw DatabaseConnection.fail(e);
        }
    }

    public boolean registerUser(String username, String password, String role, String fullName, String email, String phone) {
        String sql = "INSERT INTO users (username, password, role, status, full_name, email, phone) VALUES (?, ?, ?, 'PENDING', ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.setString(4, fullName);
            ps.setString(5, email);
            ps.setString(6, phone);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getString("status"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone")
                    );
                }
            }
        } catch (SQLException e) {
            throw DatabaseConnection.fail(e);
        }
        return null;
    }

    public void viewAllUsers() {
        String sql = "SELECT id, username, role, status, full_name, email, phone FROM users";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n--- ALL SYSTEM USERS ---");
            while (rs.next()) {
                System.out.printf("ID: %d | Username: %s | Role: %s | Status: %s | Name: %s%n",
                        rs.getInt("id"), rs.getString("username"), rs.getString("role"),
                        rs.getString("status"), rs.getString("full_name"));
            }
        } catch (SQLException e) {
            System.out.println("Error viewing users: " + e.getMessage());
        }
    }

    public void viewPendingRegistrations() {
        String sql = "SELECT id, username, role, full_name FROM users WHERE status = 'PENDING'";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n--- PENDING REGISTRATIONS ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("ID: %d | Username: %s | Role: %s | Name: %s%n",
                        rs.getInt("id"), rs.getString("username"), rs.getString("role"), rs.getString("full_name"));
            }
            if (!found) System.out.println("No pending registrations.");
        } catch (SQLException e) {
            System.out.println("Error viewing pending registrations: " + e.getMessage());
        }
    }

    public void approveUserAccount(int userId) {
        String sql = "UPDATE users SET status = 'APPROVED' WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("User account approved successfully!");
            else System.out.println("User ID not found.");
        } catch (SQLException e) {
            System.out.println("Error approving account: " + e.getMessage());
        }
    }

    public void updateUser(int userId, String newRole) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newRole);
            ps.setInt(2, userId);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("User updated successfully!");
            else System.out.println("User ID not found.");
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }

    public void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("User deleted successfully!");
            else System.out.println("User ID not found.");
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }

    public void updateProfile(int userId, String fullName, String email, String phone) {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ? WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DatabaseConnection.fail(e);
        }
    }

    public void changePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DatabaseConnection.fail(e);
        }
    }
}