package DAO;

import DataBase.DBConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DentistDAO {
    
    // ID formatting function
    public static String formatId(String prefix, int base, int internalId) {
        return prefix + String.format("%07d", base + internalId - 1);
    }

    // Inner class for Dentist entity
    public static class Dentist {
        private int internalId;
        private String dentistId; // Formatted ID like "den-1000000"
        private String title;
        private String firstName;
        private String middleName;
        private String lastName;
        private int age;
        private String bio;
        private String dentistImgPath;
        private String createdAt;
        private String updatedAt;

        // Constructors
        public Dentist() {}

        public Dentist(int internalId, String title, String firstName, String middleName, 
                      String lastName, int age, String bio, String dentistImgPath) {
            this.internalId = internalId;
            this.title = title;
            this.firstName = firstName;
            this.middleName = middleName;
            this.lastName = lastName;
            this.age = age;
            this.bio = bio;
            this.dentistImgPath = dentistImgPath;
            // Generate formatted dentist ID
            this.dentistId = formatId("den-", 1000000, internalId);
        }

        // Getters and Setters
        public int getInternalId() { return internalId; }
        public void setInternalId(int internalId) { 
            this.internalId = internalId; 
            // Update formatted ID when internal ID changes
            if (internalId > 0) {
                this.dentistId = formatId("den-", 1000000, internalId);
            }
        }

        public String getDentistId() { return dentistId; }
        public void setDentistId(String dentistId) { this.dentistId = dentistId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getMiddleName() { return middleName; }
        public void setMiddleName(String middleName) { this.middleName = middleName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }

        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }

        public String getDentistImgPath() { return dentistImgPath; }
        public void setDentistImgPath(String dentistImgPath) { this.dentistImgPath = dentistImgPath; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

        // Helper method to get full name
        public String getFullName() {
            StringBuilder fullName = new StringBuilder();
            if (title != null && !title.trim().isEmpty()) {
                fullName.append(title).append(" ");
            }
            if (firstName != null && !firstName.trim().isEmpty()) {
                fullName.append(firstName).append(" ");
            }
            if (middleName != null && !middleName.trim().isEmpty()) {
                fullName.append(middleName).append(" ");
            }
            if (lastName != null && !lastName.trim().isEmpty()) {
                fullName.append(lastName);
            }
            return fullName.toString().trim();
        }
    }

    // Helper method to create Dentist object from ResultSet
    private Dentist createDentistFromResultSet(ResultSet rs) throws SQLException {
        Dentist dentist = new Dentist();
        dentist.setInternalId(rs.getInt("InternalID"));
        dentist.setDentistId(rs.getString("DentistID"));
        dentist.setTitle(rs.getString("Title"));
        dentist.setFirstName(rs.getString("FirstName"));
        dentist.setMiddleName(rs.getString("MiddleName"));
        dentist.setLastName(rs.getString("LastName"));
        dentist.setAge(rs.getInt("Age"));
        dentist.setBio(rs.getString("Bio"));
        dentist.setDentistImgPath(rs.getString("DentistImgPath"));
        dentist.setCreatedAt(rs.getString("created_at"));
        dentist.setUpdatedAt(rs.getString("updated_at"));
        return dentist;
    }

    // Get all dentists
    public List<Dentist> getAllDentists() throws SQLException {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM DentistTbl ORDER BY InternalID ASC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                dentists.add(createDentistFromResultSet(rs));
            }
        }
        return dentists;
    }

    // Get dentist by Internal ID
    public Dentist getDentistById(int internalId) throws SQLException {
        String sql = "SELECT * FROM DentistTbl WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, internalId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createDentistFromResultSet(rs);
                }
            }
        }
        return null;
    }

    // Get dentist by formatted ID (e.g., "den-1000000")
    public Dentist getDentistByFormattedId(String formattedId) throws SQLException {
        String sql = "SELECT * FROM DentistTbl WHERE DentistID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, formattedId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createDentistFromResultSet(rs);
                }
            }
        }
        return null;
    }

    // Add new dentist
    public boolean addDentist(Dentist dentist) throws SQLException {
        String sql = "INSERT INTO DentistTbl (DentistID, InternalID, Title, FirstName, MiddleName, " +
                    "LastName, Age, Bio, DentistImgPath) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Get next internal ID
            int nextInternalId = getNextInternalId();
            String formattedId = formatId("den-", 1000000, nextInternalId);
            
            stmt.setString(1, formattedId);
            stmt.setInt(2, nextInternalId);
            stmt.setString(3, dentist.getTitle());
            stmt.setString(4, dentist.getFirstName());
            stmt.setString(5, dentist.getMiddleName());
            stmt.setString(6, dentist.getLastName());
            stmt.setInt(7, dentist.getAge());
            stmt.setString(8, dentist.getBio());
            stmt.setString(9, dentist.getDentistImgPath());

            boolean success = stmt.executeUpdate() > 0;
            
            if (success) {
                // Update the dentist object with the generated IDs
                dentist.setInternalId(nextInternalId);
                dentist.setDentistId(formattedId);
            }
            
            return success;
        }
    }

    // Update existing dentist
    public boolean updateDentist(Dentist dentist) throws SQLException {
        String sql = "UPDATE DentistTbl SET Title = ?, FirstName = ?, MiddleName = ?, " +
                    "LastName = ?, Age = ?, Bio = ?, DentistImgPath = ? WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, dentist.getTitle());
            stmt.setString(2, dentist.getFirstName());
            stmt.setString(3, dentist.getMiddleName());
            stmt.setString(4, dentist.getLastName());
            stmt.setInt(5, dentist.getAge());
            stmt.setString(6, dentist.getBio());
            stmt.setString(7, dentist.getDentistImgPath());
            stmt.setInt(8, dentist.getInternalId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Update dentist by formatted ID
    public boolean updateDentistByFormattedId(Dentist dentist) throws SQLException {
        String sql = "UPDATE DentistTbl SET Title = ?, FirstName = ?, MiddleName = ?, " +
                    "LastName = ?, Age = ?, Bio = ?, DentistImgPath = ? WHERE DentistID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, dentist.getTitle());
            stmt.setString(2, dentist.getFirstName());
            stmt.setString(3, dentist.getMiddleName());
            stmt.setString(4, dentist.getLastName());
            stmt.setInt(5, dentist.getAge());
            stmt.setString(6, dentist.getBio());
            stmt.setString(7, dentist.getDentistImgPath());
            stmt.setString(8, dentist.getDentistId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Delete dentist by Internal ID
    public boolean deleteDentist(int internalId) throws SQLException {
        String sql = "DELETE FROM DentistTbl WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, internalId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete dentist by formatted ID
    public boolean deleteDentistByFormattedId(String formattedId) throws SQLException {
        String sql = "DELETE FROM DentistTbl WHERE DentistID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, formattedId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Search dentists by name, title, or formatted ID
    public List<Dentist> searchDentists(String searchText) throws SQLException {
        List<Dentist> dentists = new ArrayList<>();
        
        // Check if search text is a formatted ID
        if (searchText.startsWith("den-")) {
            Dentist dentist = getDentistByFormattedId(searchText);
            if (dentist != null) {
                dentists.add(dentist);
                return dentists;
            }
        }
        
        String sql = "SELECT * FROM DentistTbl WHERE " +
                    "Title LIKE ? OR " +
                    "FirstName LIKE ? OR " +
                    "MiddleName LIKE ? OR " +
                    "LastName LIKE ? OR " +
                    "Bio LIKE ? " +
                    "ORDER BY InternalID ASC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchText + "%";
            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, searchPattern);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dentists.add(createDentistFromResultSet(rs));
                }
            }
        }
        return dentists;
    }

    // Get dentists by age range
    public List<Dentist> getDentistsByAgeRange(int minAge, int maxAge) throws SQLException {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM DentistTbl WHERE Age BETWEEN ? AND ? ORDER BY InternalID ASC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, minAge);
            stmt.setInt(2, maxAge);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dentists.add(createDentistFromResultSet(rs));
                }
            }
        }
        return dentists;
    }

    // Get dentists by title
    public List<Dentist> getDentistsByTitle(String title) throws SQLException {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM DentistTbl WHERE Title = ? ORDER BY InternalID ASC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, title);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dentists.add(createDentistFromResultSet(rs));
                }
            }
        }
        return dentists;
    }

    // Get total dentist count
    public int getTotalDentistCount() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM DentistTbl";

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
        String sql = "SELECT COALESCE(MAX(InternalID), 0) + 1 as nextId FROM DentistTbl";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("nextId");
            }
        }
        return 1; // Default to 1 if no records exist
    }

    // Check if dentist exists by name
    public boolean dentistExistsByName(String firstName, String lastName, int excludeInternalId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM DentistTbl WHERE FirstName = ? AND LastName = ? AND InternalID != ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setInt(3, excludeInternalId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }

    // Validate dentist information
    public boolean validateDentist(Dentist dentist) {
        if (dentist == null) return false;
        
        return dentist.getTitle() != null && !dentist.getTitle().trim().isEmpty() &&
               dentist.getFirstName() != null && !dentist.getFirstName().trim().isEmpty() &&
               dentist.getLastName() != null && !dentist.getLastName().trim().isEmpty() &&
               dentist.getAge() >= 18 && dentist.getAge() <= 100 &&
               dentist.getBio() != null && !dentist.getBio().trim().isEmpty();
    }

    // Get dentists with images
    public List<Dentist> getDentistsWithImages() throws SQLException {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM DentistTbl WHERE DentistImgPath IS NOT NULL AND DentistImgPath != '' ORDER BY InternalID ASC";

        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                dentists.add(createDentistFromResultSet(rs));
            }
        }
        return dentists;
    }

    // Get dentists without images
    public List<Dentist> getDentistsWithoutImages() throws SQLException {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM DentistTbl WHERE DentistImgPath IS NULL OR DentistImgPath = '' ORDER BY InternalID ASC";

        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                dentists.add(createDentistFromResultSet(rs));
            }
        }
        return dentists;
    }

    // Update dentist image path only
    public boolean updateDentistImage(int internalId, String imagePath) throws SQLException {
        String sql = "UPDATE DentistTbl SET DentistImgPath = ? WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, imagePath);
            stmt.setInt(2, internalId);

            return stmt.executeUpdate() > 0;
        }
    }

    // Get distinct titles
    public List<String> getDistinctTitles() throws SQLException {
        List<String> titles = new ArrayList<>();
        String sql = "SELECT DISTINCT Title FROM DentistTbl WHERE Title IS NOT NULL ORDER BY Title";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                titles.add(rs.getString("Title"));
            }
        }
        return titles;
    }
}