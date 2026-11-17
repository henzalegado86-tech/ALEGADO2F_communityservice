package com.scc.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String status;
    private String fullName;
    private String email;
    private String phone;

    public User(int id, String username, String password, String role, String status, String fullName, String email, String phone) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    public int getId() { return id; }
    public int getUserId() { return id; } // Alias for BaseMenu

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getStatus() { return status; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public String getPhoneNumber() { return phone; } // Alias for BaseMenu
    public void setPhoneNumber(String phone) { this.phone = phone; }
    public void setPhone(String phone) { this.phone = phone; }
}