package com.scc.menu;

import com.scc.model.User;
import com.scc.dao.UserDao;
import com.scc.dao.ActivityLogDao;
import com.scc.dao.ServiceRequestDao;
import com.scc.dao.CategoryDao;
import java.util.Scanner;

public abstract class BaseMenu {

    protected final User user;
    protected final Scanner scanner = new Scanner(System.in);
    protected final ServiceRequestDao requestDao = new ServiceRequestDao();
    protected final CategoryDao categoryDao = new CategoryDao();
    protected final UserDao userDao = new UserDao();
    protected final ActivityLogDao logDao = new ActivityLogDao();

    public BaseMenu(User user) {
        this.user = user;
    }

    /** Shows the menu for this role until the user chooses Logout. */
    public abstract void run();

    public void displayProfileMenu() {
        if (user == null) {
            System.out.println("No user logged in.");
            return;
        }

        while (true) {
            System.out.println("\n=== PROFILE MANAGEMENT ===");
            System.out.println("[1] View Profile Details");
            System.out.println("[2] Update Profile (Name, Email, Phone)");
            System.out.println("[3] Change Password");
            System.out.println("[0] Back to Main Menu");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\n--- PROFILE DETAILS ---");
                    System.out.println("User ID   : " + user.getId());
                    System.out.println("Username  : " + user.getUsername());
                    System.out.println("Full Name : " + (user.getFullName() != null ? user.getFullName() : "N/A"));
                    System.out.println("Email     : " + (user.getEmail() != null ? user.getEmail() : "N/A"));
                    System.out.println("Phone     : " + (user.getPhone() != null ? user.getPhone() : "N/A"));
                    System.out.println("Role      : " + user.getRole());
                    System.out.println("Status    : " + user.getStatus());
                    break;
                case "2":
                    System.out.print("Enter new Full Name: ");
                    String fullName = scanner.nextLine().trim();
                    System.out.print("Enter new Email: ");
                    String email = scanner.nextLine().trim();
                    System.out.print("Enter new Phone Number: ");
                    String phone = scanner.nextLine().trim();

                    userDao.updateProfile(user.getId(), fullName, email, phone);
                    user.setFullName(fullName);
                    user.setEmail(email);
                    user.setPhone(phone);
                    System.out.println("Profile updated successfully!");
                    break;
                case "3":
                    System.out.print("Enter new Password: ");
                    String newPass = scanner.nextLine().trim();
                    userDao.changePassword(user.getId(), newPass);
                    System.out.println("Password changed successfully!");
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}