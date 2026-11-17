package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.dao.ActivityLogDao;
import com.scc.model.User;

import java.util.Scanner;

public class Authentication {

    private final UserDao userDao = new UserDao();
    private final ActivityLogDao logDao = new ActivityLogDao();
    private final Scanner scanner = new Scanner(System.in);

    public void startSession() {
        showLoginMenu();
    }

    public void showLoginMenu() {
        while (true) {
            System.out.println("\n=== COMMUNITY SERVICE SYSTEM ===");
            System.out.println("[1] Login");
            System.out.println("[2] Register Account");
            System.out.println("[0] Exit");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handleRegistration();
                    break;
                case "0":
                    System.out.println("Exiting application. Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = userDao.login(username, password);
        if (user == null) {
            System.out.println("Login failed: Invalid username or password.");
            return;
        }

        if (!"APPROVED".equalsIgnoreCase(user.getStatus())) {
            System.out.println("Login failed: Your account is pending administrator approval.");
            return;
        }

        try {
            logDao.logAction(user.getId(), "USER_LOGIN", "User " + user.getUsername() + " (ID: " + user.getId() + ") logged in.");
        } catch (Exception ignored) {}

        System.out.println("Login successful! Welcome, " + user.getUsername() + ".");

        // Route to respective role menu
        String role = user.getRole() != null ? user.getRole().toUpperCase() : "";
        switch (role) {
            case "ADMIN":
                new AdminMenu(user).run();
                break;
            case "RESIDENT":
                new ResidentMenu(user).run();
                break;
            case "WORKER":
                new WorkerMenu(user).run();
                break;
            default:
                System.out.println("Error: Unrecognized user role (" + role + ").");
        }

        try {
            logDao.logAction(user.getId(), "USER_LOGOUT", "User " + user.getUsername() + " (ID: " + user.getId() + ") logged out.");
        } catch (Exception ignored) {}
    }

    private void handleRegistration() {
        System.out.println("\n--- REGISTER ACCOUNT ---");
        System.out.println("Select Account Role:");
        System.out.println("[1] Resident");
        System.out.println("[2] Staff / Field Worker");
        System.out.print("Choose: ");
        String roleChoice = scanner.nextLine().trim();

        String role = "RESIDENT";
        if ("2".equals(roleChoice)) {
            role = "WORKER";
        }

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Phone number: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        boolean success = userDao.registerUser(username, password, role, fullName, email, phone);
        if (success) {
            System.out.println("Registration successful! Your account is pending administrator approval.");
            try {
                User registeredUser = userDao.login(username, password);
                int uid = registeredUser != null ? registeredUser.getId() : 0;
                logDao.logAction(uid, "USER_REGISTERED", "New " + role + " account registered: " + username + " (ID: " + uid + "), status PENDING.");
            } catch (Exception ignored) {}
        }
    }
}