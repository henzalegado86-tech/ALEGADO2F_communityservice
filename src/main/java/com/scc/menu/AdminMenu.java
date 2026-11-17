package com.scc.menu;

import com.scc.model.User;
import com.scc.ui.ActivityLogUI;

public class AdminMenu extends BaseMenu {

    private final ActivityLogUI activityLogUI = new ActivityLogUI();

    public AdminMenu(User user) {
        super(user);
    }

    public void viewAllUsers() {
        userDao.viewAllUsers();
    }

    public void viewPendingRegistrations() {
        userDao.viewPendingRegistrations();
    }

    public void approveUserAccount() {
        System.out.print("Enter User ID to approve: ");
        try {
            int uid = Integer.parseInt(scanner.nextLine().trim());
            userDao.approveUserAccount(uid);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }

    public void approveUserAccount(int userId) {
        userDao.approveUserAccount(userId);
    }

    public void updateUser() {
        System.out.print("Enter User ID to update: ");
        try {
            int uid = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter new role (RESIDENTS/WORKER/ADMIN): ");
            String newRole = scanner.nextLine().trim();
            userDao.updateUser(uid, newRole);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    public void updateUser(int userId, String newRole) {
        userDao.updateUser(userId, newRole);
    }

    public void deleteUser() {
        System.out.print("Enter User ID to delete: ");
        try {
            int uid = Integer.parseInt(scanner.nextLine().trim());
            userDao.deleteUser(uid);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    public void deleteUser(int userId) {
        userDao.deleteUser(userId);
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("[1] View All System Users");
            System.out.println("[2] View Pending Registrations");
            System.out.println("[3] Approve User Account");
            System.out.println("[4] Update User Role/Status");
            System.out.println("[5] View System Audit Logs");
            System.out.println("[0] Logout");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAllUsers();
                    break;
                case "2":
                    viewPendingRegistrations();
                    break;
                case "3":
                    approveUserAccount();
                    break;
                case "4":
                    updateUser();
                    break;
                case "5":
                    activityLogUI.show();
                    break;
                case "0":
                    System.out.println("Logging out from Admin menu...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}