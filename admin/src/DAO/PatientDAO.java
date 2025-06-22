package DAO;

import DataBase.DBConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
    
    // ID formatting function
    public static String formatId(String prefix, int base, int internalId) {
        return prefix + String.format("%07d", base + internalId - 1);
    }

    // Inner class for Patient entity
    public static class Patient {
        private int internalId;
        private String patientId; // Formatted ID like "pat-1000000"
        private String firstName;
        private String middleName;
        private String lastName;
        private String birthDate; // DATE field
        private String gender; // ENUM('Male', 'Female', 'Other')
        private String createdBy;
        private String createdAt;
        private String updatedAt;

        // Constructors
        public Patient() {}

        public Patient(int internalId, String firstName, String middleName, String lastName, 
                      String birthDate, String gender, String createdBy) {
            this.internalId = internalId;
            this.firstName = firstName;
            this.middleName = middleName;
            this.lastName = lastName;
            this.birthDate = birthDate;
            this.gender = gender;
            this.createdBy = createdBy;
            // Generate formatted patient ID
            this.patientId = formatId("PAT-", 1000000, internalId);
        }

        // Getters and Setters
        public int getInternalId() { return internalId; }
        public void setInternalId(int internalId) { 
            this.internalId = internalId; 
            // Update formatted ID when internal ID changes
            if (internalId > 0) {
                this.patientId = formatId("pat-", 1000000, internalId);
            }
        }

        public String getPatientId() { return patientId; }
        public void setPatientId(String patientId) { this.patientId = patientId; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getMiddleName() { return middleName; }
        public void setMiddleName(String middleName) { this.middleName = middleName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getBirthDate() { return birthDate; }
        public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }

        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

        // Helper method to get full name
        public String getFullName() {
            StringBuilder fullName = new StringBuilder();
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

    // Helper method to create Patient object from ResultSet
    private Patient createPatientFromResultSet(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setInternalId(rs.getInt("InternalID"));
        patient.setPatientId(rs.getString("PatientID"));
        patient.setFirstName(rs.getString("FirstName"));
        patient.setMiddleName(rs.getString("MiddleName"));
        patient.setLastName(rs.getString("LastName"));
        patient.setBirthDate(rs.getString("BirthDate"));
        patient.setGender(rs.getString("Gender"));
        patient.setCreatedBy(rs.getString("CreatedBy"));
        patient.setCreatedAt(rs.getString("created_at"));
        patient.setUpdatedAt(rs.getString("updated_at"));
        return patient;
    }

    // Get all patients
    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM PatientTbl ORDER BY InternalID ASC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                patients.add(createPatientFromResultSet(rs));
            }
        }
        return patients;
    }

    // Get patient by Internal ID
    public Patient getPatientById(int internalId) throws SQLException {
        String sql = "SELECT * FROM PatientTbl WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, internalId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createPatientFromResultSet(rs);
                }
            }
        }
        return null;
    }

    // Get patient by formatted ID (e.g., "pat-1000000")
    public Patient getPatientByFormattedId(String formattedId) throws SQLException {
        String sql = "SELECT * FROM PatientTbl WHERE PatientID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, formattedId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createPatientFromResultSet(rs);
                }
            }
        }
        return null;
    }

    // Add new patient
    public boolean addPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO PatientTbl (PatientID, InternalID, FirstName, MiddleName, " +
                    "LastName, BirthDate, Gender, CreatedBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Get next internal ID
            int nextInternalId = getNextInternalId();
            String formattedId = formatId("pat-", 1000000, nextInternalId);
            
            stmt.setString(1, formattedId);
            stmt.setInt(2, nextInternalId);
            stmt.setString(3, patient.getFirstName());
            stmt.setString(4, patient.getMiddleName());
            stmt.setString(5, patient.getLastName());
            stmt.setString(6, patient.getBirthDate());
            stmt.setString(7, patient.getGender());
            stmt.setString(8, patient.getCreatedBy());

            boolean success = stmt.executeUpdate() > 0;
            
            if (success) {
                // Update the patient object with the generated IDs
                patient.setInternalId(nextInternalId);
                patient.setPatientId(formattedId);
            }
            
            return success;
        }
    }

    // Update existing patient
    public boolean updatePatient(Patient patient) throws SQLException {
        String sql = "UPDATE PatientTbl SET FirstName = ?, MiddleName = ?, LastName = ?, " +
                    "BirthDate = ?, Gender = ?, CreatedBy = ? WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getMiddleName());
            stmt.setString(3, patient.getLastName());
            stmt.setString(4, patient.getBirthDate());
            stmt.setString(5, patient.getGender());
            stmt.setString(6, patient.getCreatedBy());
            stmt.setInt(7, patient.getInternalId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Update patient by formatted ID
    public boolean updatePatientByFormattedId(Patient patient) throws SQLException {
        String sql = "UPDATE PatientTbl SET FirstName = ?, MiddleName = ?, LastName = ?, " +
                    "BirthDate = ?, Gender = ?, CreatedBy = ? WHERE PatientID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getMiddleName());
            stmt.setString(3, patient.getLastName());
            stmt.setString(4, patient.getBirthDate());
            stmt.setString(5, patient.getGender());
            stmt.setString(6, patient.getCreatedBy());
            stmt.setString(7, patient.getPatientId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Delete patient by Internal ID
    public boolean deletePatient(int internalId) throws SQLException {
        String sql = "DELETE FROM PatientTbl WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, internalId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete patient by formatted ID
    public boolean deletePatientByFormattedId(String formattedId) throws SQLException {
        String sql = "DELETE FROM PatientTbl WHERE PatientID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, formattedId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Search patients by name, gender, or formatted ID
    public List<Patient> searchPatients(String searchText) throws SQLException {
        List<Patient> patients = new ArrayList<>();
        
        // Check if search text is a formatted ID
        if (searchText.startsWith("pat-")) {
            Patient patient = getPatientByFormattedId(searchText);
            if (patient != null) {
                patients.add(patient);
                return patients;
            }
        }
        
        String sql = "SELECT * FROM PatientTbl WHERE " +
                    "FirstName LIKE ? OR " +
                    "MiddleName LIKE ? OR " +
                    "LastName LIKE ? OR " +
                    "Gender LIKE ? OR " +
                    "CreatedBy LIKE ? " +
                    "ORDER BY InternalID ASC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchText + "%";
            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, searchPattern);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(createPatientFromResultSet(rs));
                }
            }
        }
        return patients;
    }

    // Get patients by gender
    public List<Patient> getPatientsByGender(String gender) throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM PatientTbl WHERE Gender = ? ORDER BY InternalID ASC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, gender);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(createPatientFromResultSet(rs));
                }
            }
        }
        return patients;
    }

    // Get patients by created by
    public List<Patient> getPatientsByCreatedBy(String createdBy) throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM PatientTbl WHERE CreatedBy = ? ORDER BY InternalID ASC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, createdBy);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(createPatientFromResultSet(rs));
                }
            }
        }
        return patients;
    }

    // Get patients by birth year
    public List<Patient> getPatientsByBirthYear(int year) throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM PatientTbl WHERE YEAR(BirthDate) = ? ORDER BY InternalID ASC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, year);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(createPatientFromResultSet(rs));
                }
            }
        }
        return patients;
    }

    // Get total patient count
    public int getTotalPatientCount() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM PatientTbl";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    // Get total patient count by gender
    public int getPatientCountByGender(String gender) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM PatientTbl WHERE Gender = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, gender);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    // Get next available internal ID
    private int getNextInternalId() throws SQLException {
        String sql = "SELECT COALESCE(MAX(InternalID), 0) + 1 as nextId FROM PatientTbl";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("nextId");
            }
        }
        return 1; // Default to 1 if no records exist
    }

    // Check if patient exists by name and birth date
    public boolean patientExistsByNameAndBirthDate(String firstName, String lastName, String birthDate, int excludeInternalId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM PatientTbl WHERE FirstName = ? AND LastName = ? AND BirthDate = ? AND InternalID != ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, birthDate);
            stmt.setInt(4, excludeInternalId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }

    // Validate patient information
    public boolean validatePatient(Patient patient) {
        if (patient == null) return false;
        
        return patient.getFirstName() != null && !patient.getFirstName().trim().isEmpty() &&
               patient.getMiddleName() != null && !patient.getMiddleName().trim().isEmpty() &&
               patient.getLastName() != null && !patient.getLastName().trim().isEmpty() &&
               patient.getBirthDate() != null && !patient.getBirthDate().trim().isEmpty() &&
               patient.getGender() != null && !patient.getGender().trim().isEmpty() &&
               patient.getCreatedBy() != null && !patient.getCreatedBy().trim().isEmpty();
    }

    // Get distinct genders
    public List<String> getDistinctGenders() throws SQLException {
        List<String> genders = new ArrayList<>();
        String sql = "SELECT DISTINCT Gender FROM PatientTbl WHERE Gender IS NOT NULL ORDER BY Gender";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                genders.add(rs.getString("Gender"));
            }
        }
        return genders;
    }

    // Get distinct creators
    public List<String> getDistinctCreators() throws SQLException {
        List<String> creators = new ArrayList<>();
        String sql = "SELECT DISTINCT CreatedBy FROM PatientTbl WHERE CreatedBy IS NOT NULL ORDER BY CreatedBy";

        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                creators.add(rs.getString("CreatedBy"));
            }
        }
        return creators;
    }

    // Check if patient ID format is valid
    public static boolean isValidPatientIdFormat(String patientId) {
        return patientId != null && patientId.matches("^pat-\\d{7}$");
    }

    // Extract internal ID from formatted patient ID
    public static int extractInternalIdFromFormattedId(String formattedId) {
        if (!isValidPatientIdFormat(formattedId)) {
            return -1;
        }
        try {
            String numberPart = formattedId.substring(4); // Remove "pat-"
            int extractedNumber = Integer.parseInt(numberPart);
            return extractedNumber - 1000000 + 1; // Reverse the formatting logic
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public boolean testDatabaseOperations() {
    try {
        // Test database connection by trying to get total count
        getTotalPatientCount();
        return true;
    } catch (Exception e) {
        System.err.println("Database test failed: " + e.getMessage());
        return false;
    }
}
}