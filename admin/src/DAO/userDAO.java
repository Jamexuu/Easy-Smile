package DAO;

import DataBase.DBConnector;
import java.sql.*;

public class userDAO {
    
    /**
     * Validates user credentials against database
     */
    public static boolean validateCredentials(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }
        
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        
        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email.trim());
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if user found
            
        } catch (SQLException e) {
            System.err.println("Database error during login: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets specific login error message for better user feedback
     * @param email The email entered by user
     * @param password The password entered by user
     * @return Error message or null if valid
     */
    public static String getLoginError(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Password is required";
        }
        if (!validateCredentials(email, password)) {
            return "Invalid email or password";
        }
        return null; // No error - valid credentials
    }
    
    /**
     * Get user details after successful login
     */
    public static User authenticateUser(String email, String password) {
        if (!validateCredentials(email, password)) {
            return null;
        }
        
        String query = "SELECT id, email, name, role FROM users WHERE email = ?";
        
        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email.trim());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("name"),
                    rs.getString("role")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Database error getting user: " + e.getMessage());
        }
        
        return null;
    }
    
    public static class User {
        private int id;
        private String email;
        private String name;
        private String role;
        
        public User(int id, String email, String name, String role) {
            this.id = id;
            this.email = email;
            this.name = name;
            this.role = role;
        }
        
        // Getters
        public int getId() { return id; }
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getRole() { return role; }
    }
}