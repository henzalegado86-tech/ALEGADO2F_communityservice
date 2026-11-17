package com.scc.menu;

import com.scc.model.ServiceRequest;
import com.scc.model.Category;
import com.scc.model.User;
import java.util.List;
import java.util.stream.Collectors;

public class ResidentMenu extends BaseMenu {

    public ResidentMenu(User user) {
        super(user);
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("\n=== RESIDENT MENU ===");
            System.out.println("[1] View My Service Requests");
            System.out.println("[2] Create New Service Request");
            System.out.println("[3] View Profile & Settings");
            System.out.println("[0] Logout");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewMyRequests();
                    break;
                case "2":
                    createServiceRequest();
                    break;
                case "3":
                    displayProfileMenu();
                    break;
                case "0":
                    System.out.println("Logging out from Resident menu...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void viewMyRequests() {
        List<ServiceRequest> requests = requestDao.getRequestsByResident(user.getId());
        printRequests(requests);
        if (!requests.isEmpty()) {
            System.out.print("Select a request ID to view details or 0 to go back: ");
            try {
                int reqId = Integer.parseInt(scanner.nextLine().trim());
                if (reqId > 0) {
                    ServiceRequest req = requestDao.getRequestById(reqId);
                    if (req != null) {
                        printDetails(req);
                    } else {
                        System.out.println("Request not found.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private void createServiceRequest() {
        System.out.print("Enter service title/description: ");
        String description = scanner.nextLine().trim();
        List<Category> categories = categoryDao.getAllCategories();
        System.out.println("\n--- AVAILABLE CATEGORIES ---");
        for (Category cat : categories) {
            System.out.println("[" + cat.getId() + "] " + cat.getName());
        }
        System.out.print("Enter Category ID: ");
        try {
            int catId = Integer.parseInt(scanner.nextLine().trim());
            requestDao.createRequest(user.getId(), catId, description);
            System.out.println("Service request created successfully!");
            log("CREATE_REQUEST", "Created service request under category ID: " + catId);
        } catch (NumberFormatException e) {
            System.out.println("Invalid category ID format.");
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
        System.out.print("Enter Request ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            return requests.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
        } catch (NumberFormatException e) {
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