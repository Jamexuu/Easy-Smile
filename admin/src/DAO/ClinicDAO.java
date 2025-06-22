package DAO;

import DataBase.DBConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClinicDAO {
    
    // ID formatting function
    public static String formatId(String prefix, int base, int internalId) {
        return prefix + String.format("%07d", base + internalId - 1);
    }

    // Inner class for ClinicInfo entity
    public static class ClinicInfo {
        private int internalId;
        private String clinicId; // Formatted ID like "cli-1000000"
        private String phoneNumber;
        private String email;
        private String location;
        private String facebookLink;
        private String instagramLink;
        private String createdAt;
        private String updatedAt;

        // Constructors
        public ClinicInfo() {}

        public ClinicInfo(int internalId, String phoneNumber, String email, String location, 
                         String facebookLink, String instagramLink) {
            this.internalId = internalId;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.location = location;
            this.facebookLink = facebookLink;
            this.instagramLink = instagramLink;
            // Generate formatted clinic ID
            this.clinicId = formatId("CLI-", 1000000, internalId);
        }

        // Getters and Setters
        public int getInternalId() { return internalId; }
        public void setInternalId(int internalId) { 
            this.internalId = internalId; 
            // Update formatted ID when internal ID changes
            this.clinicId = formatId("cli-", 1000000, internalId);
        }

        public String getClinicId() { return clinicId; }
        public void setClinicId(String clinicId) { this.clinicId = clinicId; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public String getFacebookLink() { return facebookLink; }
        public void setFacebookLink(String facebookLink) { this.facebookLink = facebookLink; }

        public String getInstagramLink() { return instagramLink; }
        public void setInstagramLink(String instagramLink) { this.instagramLink = instagramLink; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

        // Alias methods for UI compatibility
        public String getEmailAddress() { return email; }
        public void setEmailAddress(String emailAddress) { this.email = emailAddress; }
    }

    // Helper method to create ClinicInfo object from ResultSet
    private ClinicInfo createClinicInfoFromResultSet(ResultSet rs) throws SQLException {
        ClinicInfo clinicInfo = new ClinicInfo();
        clinicInfo.setInternalId(rs.getInt("InternalID"));
        clinicInfo.setClinicId(rs.getString("ClinicID"));
        clinicInfo.setPhoneNumber(rs.getString("PhoneNumber"));
        clinicInfo.setEmail(rs.getString("Email"));
        clinicInfo.setLocation(rs.getString("Location"));
        clinicInfo.setFacebookLink(rs.getString("FacebookLink"));
        clinicInfo.setInstagramLink(rs.getString("InstagramLink"));
        clinicInfo.setCreatedAt(rs.getString("created_at"));
        clinicInfo.setUpdatedAt(rs.getString("updated_at"));
        return clinicInfo;
    }

    // Get clinic information (assuming there's only one clinic record)
    public ClinicInfo getClinicInfo() throws SQLException {
        String sql = "SELECT * FROM ClinicInfoTbl LIMIT 1";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return createClinicInfoFromResultSet(rs);
            }
        }
        return null; // No clinic info found
    }

    // Get clinic information by Internal ID
    public ClinicInfo getClinicInfoById(int internalId) throws SQLException {
        String sql = "SELECT * FROM ClinicInfoTbl WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, internalId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createClinicInfoFromResultSet(rs);
                }
            }
        }
        return null;
    }

    // Get clinic information by formatted ID (e.g., "cli-1000000")
    public ClinicInfo getClinicInfoByFormattedId(String formattedId) throws SQLException {
        String sql = "SELECT * FROM ClinicInfoTbl WHERE ClinicID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, formattedId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createClinicInfoFromResultSet(rs);
                }
            }
        }
        return null;
    }

    // Add new clinic information
    public boolean addClinicInfo(ClinicInfo clinicInfo) throws SQLException {
        String sql = "INSERT INTO ClinicInfoTbl (ClinicID, InternalID, PhoneNumber, Email, " +
                    "Location, FacebookLink, InstagramLink) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Get next internal ID
            int nextInternalId = getNextInternalId();
            String formattedId = formatId("cli-", 1000000, nextInternalId);
            
            stmt.setString(1, formattedId);
            stmt.setInt(2, nextInternalId);
            stmt.setString(3, clinicInfo.getPhoneNumber());
            stmt.setString(4, clinicInfo.getEmail());
            stmt.setString(5, clinicInfo.getLocation());
            stmt.setString(6, clinicInfo.getFacebookLink());
            stmt.setString(7, clinicInfo.getInstagramLink());

            boolean success = stmt.executeUpdate() > 0;
            
            if (success) {
                // Update the clinicInfo object with the generated IDs
                clinicInfo.setInternalId(nextInternalId);
                clinicInfo.setClinicId(formattedId);
            }
            
            return success;
        }
    }

    // Update existing clinic information
    public boolean updateClinicInfo(ClinicInfo clinicInfo) throws SQLException {
        String sql = "UPDATE ClinicInfoTbl SET PhoneNumber = ?, Email = ?, Location = ?, " +
                    "FacebookLink = ?, InstagramLink = ? WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, clinicInfo.getPhoneNumber());
            stmt.setString(2, clinicInfo.getEmail());
            stmt.setString(3, clinicInfo.getLocation());
            stmt.setString(4, clinicInfo.getFacebookLink());
            stmt.setString(5, clinicInfo.getInstagramLink());
            stmt.setInt(6, clinicInfo.getInternalId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Update clinic information by formatted ID
    public boolean updateClinicInfoByFormattedId(ClinicInfo clinicInfo) throws SQLException {
        String sql = "UPDATE ClinicInfoTbl SET PhoneNumber = ?, Email = ?, Location = ?, " +
                    "FacebookLink = ?, InstagramLink = ? WHERE ClinicID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, clinicInfo.getPhoneNumber());
            stmt.setString(2, clinicInfo.getEmail());
            stmt.setString(3, clinicInfo.getLocation());
            stmt.setString(4, clinicInfo.getFacebookLink());
            stmt.setString(5, clinicInfo.getInstagramLink());
            stmt.setString(6, clinicInfo.getClinicId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Delete clinic information by Internal ID
    public boolean deleteClinicInfo(int internalId) throws SQLException {
        String sql = "DELETE FROM ClinicInfoTbl WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, internalId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete clinic information by formatted ID
    public boolean deleteClinicInfoByFormattedId(String formattedId) throws SQLException {
        String sql = "DELETE FROM ClinicInfoTbl WHERE ClinicID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, formattedId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Get all clinic information records
    public List<ClinicInfo> getAllClinicInfo() throws SQLException {
        List<ClinicInfo> clinicInfoList = new ArrayList<>();
        String sql = "SELECT * FROM ClinicInfoTbl ORDER BY InternalID ASC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clinicInfoList.add(createClinicInfoFromResultSet(rs));
            }
        }
        return clinicInfoList;
    }

    // Check if clinic information exists
    public boolean clinicInfoExists() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM ClinicInfoTbl";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        }
        return false;
    }

    // Get total clinic info count
    public int getTotalClinicInfoCount() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM ClinicInfoTbl";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    // Get next available internal ID
    private int getNextInternalId() throws SQLException {
        String sql = "SELECT COALESCE(MAX(InternalID), 0) + 1 as nextId FROM ClinicInfoTbl";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("nextId");
            }
        }
        return 1; // Default to 1 if no records exist
    }

    // Search clinic information (useful if you have multiple clinic records)
    public List<ClinicInfo> searchClinicInfo(String searchText) throws SQLException {
        List<ClinicInfo> clinicInfoList = new ArrayList<>();
        
        // Check if search text is a formatted ID
        if (searchText.startsWith("cli-")) {
            ClinicInfo clinicInfo = getClinicInfoByFormattedId(searchText);
            if (clinicInfo != null) {
                clinicInfoList.add(clinicInfo);
                return clinicInfoList;
            }
        }
        
        String sql = "SELECT * FROM ClinicInfoTbl WHERE " +
                    "PhoneNumber LIKE ? OR " +
                    "Email LIKE ? OR " +
                    "Location LIKE ? OR " +
                    "FacebookLink LIKE ? OR " +
                    "InstagramLink LIKE ? " +
                    "ORDER BY InternalID ASC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchText + "%";
            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, searchPattern);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clinicInfoList.add(createClinicInfoFromResultSet(rs));
                }
            }
        }
        return clinicInfoList;
    }

    // Validate clinic information
    public boolean validateClinicInfo(ClinicInfo clinicInfo) {
        if (clinicInfo == null) return false;
        
        return clinicInfo.getPhoneNumber() != null && !clinicInfo.getPhoneNumber().trim().isEmpty() &&
               clinicInfo.getEmail() != null && !clinicInfo.getEmail().trim().isEmpty() &&
               clinicInfo.getLocation() != null && !clinicInfo.getLocation().trim().isEmpty();
    }

    // Check if email already exists (for validation)
    public boolean emailExists(String email, int excludeInternalId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM ClinicInfoTbl WHERE Email = ? AND InternalID != ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setInt(2, excludeInternalId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }
}