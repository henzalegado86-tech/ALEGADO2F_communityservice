package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.model.ActivityLog;

import java.util.List;
import java.util.Scanner;

public class ActivityLogUI {
    private final ActivityLogDao logDao = new ActivityLogDao();
    private final Scanner scanner = new Scanner(System.in);

    public void showAuditLogsMenu() {
        while (true) {
            System.out.println("\n=== SYSTEM AUDIT LOGS ===");
            System.out.println("[1] View All System Logs");
            System.out.println("[2] Filter Logs by User ID");
            System.out.println("[0] Back to Admin Menu");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    displayLogs(logDao.getAllLogs());
                    break;
                case "2":
                    System.out.print("Enter User ID to filter: ");
                    try {
                        int uid = Integer.parseInt(scanner.nextLine().trim());
                        displayLogs(logDao.getLogsByUserId(uid));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid User ID format.");
                    }
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public void displayLogs(List<ActivityLog> logs) {
        if (logs.isEmpty()) {
            System.out.println("No activity logs found.");
            return;
        }

        // Exact column formatting matching your screenshots:
        // ID (5s) | User ID (8s) | Action Type (18s) | Description (35s) | Timestamp (20s)
        String format = "%-5s | %-8s | %-18s | %-35s | %-20s%n";
        String lineSeparator = "-".repeat(5) + "-+-" + "-".repeat(8) + "-+-" + "-".repeat(18) + "-+-" + "-".repeat(35) + "-+-" + "-".repeat(20);

        System.out.println(lineSeparator);
        System.out.printf(format, "ID", "User ID", "Action Type", "Description", "Timestamp");
        System.out.println(lineSeparator);

        for (ActivityLog log : logs) {
            System.out.printf(format,
                    log.getId(),
                    log.getUserId(),
                    log.getActionType(),
                    truncate(log.getDescription(), 35),
                    log.getTimestamp()
            );
        }
        System.out.println(lineSeparator);
    }

    private String truncate(String value, int maxLength) {
        if (value == null) return "";
        return value.length() > maxLength ? value.substring(0, maxLength - 3) + "..." : value;
    }
}