package DAO;

import DataBase.DBConnector;
import java.sql.*;

public class adminDAO {
    
    // ID formatting function
    public static String formatId(String prefix, int base, int internalId) {
        return prefix + String.format("%07d", base + internalId - 1);
    }
    
    // Inner class for Admin entity
    public static class Admin {
        private int internalId;
        private String adminId; // Formatted ID like "adm-1000000"
        private String password;
        private String createdAt;
        private String updatedAt;

        // Constructors
        public Admin() {}

        public Admin(int internalId, String password) {
            this.internalId = internalId;
            this.password = password;
            // Generate formatted admin ID
            this.adminId = formatId("ADM-", 1000000, internalId);
        }

        // Getters and Setters
        public int getInternalId() { return internalId; }
        public void setInternalId(int internalId) { 
            this.internalId = internalId; 
            // Update formatted ID when internal ID changes
            if (internalId > 0) {
                this.adminId = formatId("ADM-", 1000000, internalId);
            }
        }

        public String getAdminId() { return adminId; }
        public void setAdminId(String adminId) { this.adminId = adminId; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    }
    
    /**
     * Validates admin credentials against AdminTbl
     */
    public static boolean validateCredentials(String adminId, String password) {
        if (adminId == null || password == null || adminId.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }
        
        String query = "SELECT * FROM AdminTbl WHERE AdminID = ? AND Password = ?";
        
        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, adminId.trim());
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if admin found
            
        } catch (SQLException e) {
            System.err.println("Database error during login: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets specific login error message for better user feedback
     */
    public static String getLoginError(String adminId, String password) {
        if (adminId == null || adminId.trim().isEmpty()) {
            return "Admin ID is required";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Password is required";
        }
        if (!validateCredentials(adminId, password)) {
            return "Invalid Admin ID or password";
        }
        return null; // No error - valid credentials
    }
    
    /**
     * Get admin details after successful login
     */
    public static Admin authenticateAdmin(String adminId, String password) {
        if (!validateCredentials(adminId, password)) {
            return null;
        }
        
        String query = "SELECT AdminID, InternalID, Password, created_at, updated_at FROM AdminTbl WHERE AdminID = ?";
        
        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, adminId.trim());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Admin admin = new Admin();
                admin.setAdminId(rs.getString("AdminID"));
                admin.setInternalId(rs.getInt("InternalID"));
                admin.setPassword(rs.getString("Password"));
                admin.setCreatedAt(rs.getString("created_at"));
                admin.setUpdatedAt(rs.getString("updated_at"));
                return admin;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error getting admin: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get admin by formatted ID
     */
    public static Admin getAdminByFormattedId(String formattedId) throws SQLException {
        String sql = "SELECT AdminID, InternalID, Password, created_at, updated_at FROM AdminTbl WHERE AdminID = ?";

        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, formattedId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setAdminId(rs.getString("AdminID"));
                    admin.setInternalId(rs.getInt("InternalID"));
                    admin.setPassword(rs.getString("Password"));
                    admin.setCreatedAt(rs.getString("created_at"));
                    admin.setUpdatedAt(rs.getString("updated_at"));
                    return admin;
                }
            }
        }
        return null;
    }
    
    /**
     * Initialize admin - Creates the single admin if it doesn't exist
     */
    public static boolean initializeAdmin(String password) throws SQLException {
        // Check if admin already exists
        if (adminExists()) {
            return true; // Admin already exists
        }
        
        String sql = "INSERT INTO AdminTbl (AdminID, InternalID, Password) VALUES (?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Always use InternalID = 1 for the single admin
            int internalId = 1;
            String formattedId = formatId("ADM-", 1000000, internalId);
            
            stmt.setString(1, formattedId);
            stmt.setInt(2, internalId);
            stmt.setString(3, password);

            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Check if the admin exists (there should only be one)
     */
    public static boolean adminExists() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM AdminTbl";

        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        }
        return false;
    }
    
    /**
     * Get the single admin record
     */
    public static Admin getAdmin() throws SQLException {
        String sql = "SELECT AdminID, InternalID, Password, created_at, updated_at FROM AdminTbl LIMIT 1";

        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                Admin admin = new Admin();
                admin.setAdminId(rs.getString("AdminID"));
                admin.setInternalId(rs.getInt("InternalID"));
                admin.setPassword(rs.getString("Password"));
                admin.setCreatedAt(rs.getString("created_at"));
                admin.setUpdatedAt(rs.getString("updated_at"));
                return admin;
            }
        }
        return null;
    }
    
    /**
     * Validate admin ID format
     */
    public static boolean isValidAdminIdFormat(String adminId) {
        return adminId != null && adminId.matches("^ADM-\\d{7}$");
    }
}