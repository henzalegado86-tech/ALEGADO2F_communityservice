package com.scc.menu;

import java.util.Scanner;

/** Small helpers for reading input and printing in the console. */
public final class ConsoleUtil {

    private static final Scanner IN = new Scanner(System.in);

    private ConsoleUtil() { }

    public static String ask(String prompt) {
        System.out.print(prompt);
        if (!IN.hasNextLine()) {
            throw new IllegalStateException("Input closed.");
        }
        return IN.nextLine().trim();
    }

    public static String askRequired(String prompt) {
        while (true) {
            String s = ask(prompt);
            if (!s.isEmpty()) return s;
            System.out.println("-> This field is required.");
        }
    }

    public static int askInt(String prompt, int min, int max) {
        while (true) {
            String s = ask(prompt);
            try {
                int v = Integer.parseInt(s);
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) {
                // fall through to the message below
            }
            System.out.println("-> Please enter a number from " + min + " to " + max + ".");
        }
    }

    public static boolean confirm(String prompt) {
        return ask(prompt + " (y/n): ").equalsIgnoreCase("y");
    }

    public static void pause() {
        ask("\nPress Enter to continue...");
    }

    public static String cut(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }

    public static void title(String text) {
        System.out.println("\n=== " + text + " ===");
    }
}
