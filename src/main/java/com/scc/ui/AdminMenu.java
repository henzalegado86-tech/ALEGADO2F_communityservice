package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu extends BaseMenu {
    private final User admin;
    private final UserDao userDao = new UserDao();
    private final ActivityLogDao logDao = new ActivityLogDao();
    private final ActivityLogUI logUI = new ActivityLogUI();
    private final Scanner scanner = new Scanner(System.in);

    public AdminMenu(User admin) {
        super(admin);
        this.admin = admin;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("[1] View All System Users");
            System.out.println("[2] View Pending Registrations");
            System.out.println("[3] Approve User Account");
            System.out.println("[4] Update User Role & Status");
            System.out.println("[5] Delete User");
            System.out.println("[6] View System Audit Logs");
            System.out.println("[0] Logout");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    displayUsersTable(userDao.getAllUsers());
                    break;
                case "2":
                    displayUsersTable(userDao.getPendingUsers());
                    break;
                case "3":
                    handleApproveUser();
                    break;
                case "4":
                    handleUpdateRoleAndStatus();
                    break;
                case "5":
                    handleDeleteUser();
                    break;
                case "6":
                    logUI.showAuditLogsMenu();
                    break;
                case "0":
                    logDao.logAction(admin.getId(), "USER_LOGOUT",
                            "User " + admin.getUsername() + " (ID: " + admin.getId() + ") logged out.");
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void displayUsersTable(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        String format = "%-5s | %-15s | %-12s | %-10s | %-25s | %-15s%n";
        String separator = "-".repeat(95);

        System.out.println(separator);
        System.out.printf(format, "ID", "Username", "Role", "Status", "Email", "Phone Number");
        System.out.println(separator);

        for (User u : users) {
            System.out.printf(format,
                    u.getId(),
                    u.getUsername(),
                    u.getRole(),
                    u.getStatus(),
                    u.getEmail() != null ? u.getEmail() : "",
                    u.getPhoneNumber() != null ? u.getPhoneNumber() : ""
            );
        }
        System.out.println(separator);
    }

    private void displayCompactUsersTable(List<User> users) {
        String border = "+-------+-----------------+-----------------+-----------------+";
        String format = "| %-5s | %-15s | %-15s | %-15s |%n";
        System.out.println(border);
        System.out.printf(format, "ID", "Username", "Role", "Status");
        System.out.println(border);
        for (User u : users) {
            System.out.printf(format, u.getId(), u.getUsername(), u.getRole(), u.getStatus());
        }
        System.out.println(border);
    }

    private void handleApproveUser() {
        List<User> pending = userDao.getPendingUsers();
        if (pending.isEmpty()) {
            System.out.println("No pending registrations to approve.");
            return;
        }

        System.out.println("\n=== APPROVE USER ACCOUNT ===");
        displayCompactUsersTable(pending);

        while (true) {
            System.out.print("Enter User ID to approve (0 to cancel): ");
            String input = scanner.nextLine().trim();
            int targetId;
            try {
                targetId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid numeric ID.");
                continue;
            }

            if (targetId == 0) return;

            if (userDao.approveUser(targetId)) {
                logDao.logAction(admin.getId(), "USER_APPROVED",
                        "Admin " + admin.getId() + " approved User " + targetId);
                System.out.println("SUCCESS: User ID " + targetId + " approved!");
                return;
            } else {
                System.out.println("User ID not found. Please try again.");
            }
        }
    }

    private void handleUpdateRoleAndStatus() {
        List<User> users = userDao.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users available.");
            return;
        }

        System.out.println("\n=== UPDATE USER ROLE & STATUS ===");
        displayCompactUsersTable(users);

        int targetId;
        while (true) {
            System.out.print("Enter User ID to update (0 to cancel): ");
            String input = scanner.nextLine().trim();
            try {
                targetId = Integer.parseInt(input);
                if (targetId == 0) return;

                boolean exists = false;
                for (User u : users) {
                    if (u.getId() == targetId) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    System.out.println("User ID not found. Please enter an existing ID.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid numeric ID.");
            }
        }

        String newRole;
        while (true) {
            System.out.print("Enter new role (RESIDENT/WORKER, 0 to cancel): ");
            newRole = scanner.nextLine().trim().toUpperCase();
            if (newRole.equals("0")) return;
            if (newRole.equals("RESIDENT") || newRole.equals("WORKER")) {
                break;
            }
            System.out.println("Invalid role! Please enter RESIDENT or WORKER.");
        }

        String newStatus;
        while (true) {
            System.out.print("Enter new status (APPROVED/PENDING, 0 to cancel): ");
            newStatus = scanner.nextLine().trim().toUpperCase();
            if (newStatus.equals("0")) return;
            if (newStatus.equals("APPROVED") || newStatus.equals("PENDING")) {
                break;
            }
            System.out.println("Invalid status! Please enter APPROVED or PENDING.");
        }

        System.out.println("Updating user...");
        if (userDao.updateUserRoleAndStatus(targetId, newRole, newStatus)) {
            logDao.logAction(admin.getId(), "USER_UPDATED",
                    "Admin " + admin.getId() + " changed role of User " + targetId +
                            " to " + newRole + " and status to " + newStatus + ".");
            System.out.println("SUCCESS: User role and status updated successfully!");
        } else {
            System.out.println("ERROR: Failed to update user.");
        }
    }

    private void handleDeleteUser() {
        List<User> users = userDao.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users available.");
            return;
        }

        System.out.println("\n=== DELETE USER ===");
        displayCompactUsersTable(users);

        while (true) {
            System.out.print("Enter User ID to delete (0 to cancel): ");
            String input = scanner.nextLine().trim();
            int targetId;
            try {
                targetId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid numeric ID.");
                continue;
            }

            if (targetId == 0) return;
            if (targetId == admin.getId()) {
                System.out.println("Cannot delete your own active admin account.");
                continue;
            }

            if (userDao.deleteUser(targetId)) {
                logDao.logAction(admin.getId(), "USER_DELETED",
                        "Admin " + admin.getId() + " deleted User " + targetId);
                System.out.println("SUCCESS: User ID " + targetId + " deleted.");
                return;
            } else {
                System.out.println("User ID not found. Please try again.");
            }
        }
    }
}