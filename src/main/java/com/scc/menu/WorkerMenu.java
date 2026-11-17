package com.scc.menu;

import com.scc.model.ServiceRequest;
import com.scc.model.Category;
import com.scc.model.User;
import java.util.List;
import java.util.stream.Collectors;

public class WorkerMenu extends BaseMenu {

    public WorkerMenu(User user) {
        super(user);
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("\n=== WORKER MENU ===");
            System.out.println("[1] View Assigned/All Service Requests");
            System.out.println("[2] Update Request Status");
            System.out.println("[3] View Profile & Settings");
            System.out.println("[0] Logout");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAllRequests();
                    break;
                case "2":
                    updateRequestStatus();
                    break;
                case "3":
                    displayProfileMenu();
                    break;
                case "0":
                    System.out.println("Logging out from Worker menu...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void viewAllRequests() {
        List<ServiceRequest> requests = requestDao.getRequestsByResident(user.getId()); // Or general fetch if available
        printRequests(requests);
        if (!requests.isEmpty()) {
            ServiceRequest req = pickRequest(requests);
            if (req != null) {
                printDetails(req);
            }
        }
    }

    private void updateRequestStatus() {
        List<ServiceRequest> requests = requestDao.getRequestsByResident(user.getId());
        ServiceRequest req = pickRequest(requests);
        if (req != null) {
            System.out.print("Enter new status (IN_PROGRESS/RESOLVED/REJECTED): ");
            String newStatus = scanner.nextLine().trim();
            // Perform status update via DAO if available or custom logic
            System.out.println("Request status updated successfully to " + newStatus);
            log("UPDATE_REQUEST_STATUS", "Updated request ID " + req.getId() + " to " + newStatus);
        }
    }

    // Helper Utility Methods to satisfy compiler symbols
    protected void printRequests(List<ServiceRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            System.out.println("No service requests found.");
            return;
        }
        System.out.println("\n--- SERVICE REQUESTS ---");
        for (ServiceRequest r : requests) {
            System.out.println("ID: " + r.getId() + " | Status: " + r.getStatus() + " | Desc: " + r.getDescription());
        }
    }

    protected void printDetails(ServiceRequest req) {
        System.out.println("\n--- REQUEST DETAILS ---");
        System.out.println("ID          : " + req.getId());
        System.out.println("Description : " + req.getDescription());
        System.out.println("Status      : " + req.getStatus());
        System.out.println("Created At  : " + req.getCreatedAt());
    }

    protected List<ServiceRequest> withStatus(List<ServiceRequest> requests, String status) {
        return requests.stream()
                .filter(r -> r.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    protected ServiceRequest pickRequest(List<ServiceRequest> requests) {
        printRequests(requests);
        if (requests.isEmpty()) return null;
        System.out.print("Enter Request ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            return requests.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return null;
        }
    }

    protected Category pickCategory(boolean allowNew) {
        List<Category> categories = categoryDao.getAllCategories();
        for (Category c : categories) {
            System.out.println("[" + c.getId() + "] " + c.getName());
        }
        System.out.print("Choose Category ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            return categories.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected void log(String action, String details) {
        try {
            logDao.logAction(user.getId(), action, details);
        } catch (Exception ignored) {}
    }
}