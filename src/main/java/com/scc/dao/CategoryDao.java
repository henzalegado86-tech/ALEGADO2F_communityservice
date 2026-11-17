package com.scc.dao;

import com.scc.model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao {
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "CREATE TABLE IF NOT EXISTS categories (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL);" +
                "INSERT OR IGNORE INTO categories(id, name) VALUES (1, 'General Community Inquiry'), (2, 'Infrastructure & Road Repair'), (3, 'Sanitation & Garbage Collection');";
        // Ensure table exists
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS categories (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL);");
            // Seed default if empty
            ResultSet rsCheck = st.executeQuery("SELECT COUNT(*) FROM categories");
            if (rsCheck.next() && rsCheck.getInt(1) == 0) {
                st.execute("INSERT INTO categories(name) VALUES ('General Community Inquiry');");
                st.execute("INSERT INTO categories(name) VALUES ('Infrastructure & Road Repair');");
                st.execute("INSERT INTO categories(name) VALUES ('Sanitation & Garbage Collection');");
            }

            ResultSet rs = st.executeQuery("SELECT id, name FROM categories");
            while (rs.next()) {
                list.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw DatabaseConnection.fail(e);
        }
        return list;
    }
}