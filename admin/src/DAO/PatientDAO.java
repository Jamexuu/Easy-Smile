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

        private String barangay;
        private String city;
        private String province;

        // Constructors
        public Patient() {}

        public Patient(int internalId, String firstName, String middleName, String lastName,
                      String birthDate, String gender, String createdBy, String barangay,
                      String city, String province) {
            this.internalId = internalId;
            this.firstName = firstName;
            this.middleName = middleName;
            this.lastName = lastName;
            this.birthDate = birthDate;
            this.gender = gender;
            this.createdBy = createdBy;
            this.barangay = barangay;
            this.city = city;
            this.province = province;
            // Generate formatted patient ID
            this.patientId = formatId("PAT-", 1000000, internalId);
        }

        // Getters and Setters
        public int getInternalId() { return internalId; }
        public void setInternalId(int internalId) { 
            this.internalId = internalId; 
            // Update formatted ID when internal ID changes
            if (internalId > 0) {
                this.patientId = formatId("PAT-", 1000000, internalId);
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

        public String getBarangay() { return barangay; }
        public void setBarangay(String barangay) { this.barangay = barangay; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }

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

        patient.setBarangay(rs.getString("Barangay"));
        patient.setCity(rs.getString("City"));
        patient.setProvince(rs.getString("Province"));
        return patient;
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

        // Get all patients with address
    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT p.*, pa.Barangay, pa.City, pa.Province " +
                "FROM PatientTbl p " +
                "LEFT JOIN PatientAddressTbl pa ON p.PatientID = pa.PatientID " +
                "ORDER BY p.InternalID ASC";

        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                patients.add(createPatientFromResultSet(rs));
            }
        }
        return patients;
    }

    // Get patient by Internal ID with address
    public Patient getPatientById(int internalId) throws SQLException {
        String sql = "SELECT p.*, pa.Barangay, pa.City, pa.Province " +
                    "FROM PatientTbl p " +
                    "LEFT JOIN PatientAddressTbl pa ON p.PatientID = pa.PatientID " +
                    "WHERE p.InternalID = ?";

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

   // Fix the updatePatient method

    public boolean updatePatient(Patient patient) throws SQLException {
        Connection conn = null;
        boolean success = false;
        
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // 1. First update patient basic information - DO NOT update CreatedBy field
            String patientSql = "UPDATE PatientTbl SET FirstName = ?, MiddleName = ?, LastName = ?, " +
                            "BirthDate = ?, Gender = ? WHERE InternalID = ?";
            
            try (PreparedStatement patientStmt = conn.prepareStatement(patientSql)) {
                patientStmt.setString(1, patient.getFirstName());
                patientStmt.setString(2, patient.getMiddleName());
                patientStmt.setString(3, patient.getLastName());
                patientStmt.setString(4, patient.getBirthDate());
                patientStmt.setString(5, patient.getGender());
                patientStmt.setInt(6, patient.getInternalId());
                
                int patientRows = patientStmt.executeUpdate();
                System.out.println("Updated patient rows: " + patientRows);
                
                // 2. Now update the address - use PatientID not InternalID
                String addressSql = "UPDATE PatientAddressTbl SET Barangay = ?, City = ?, Province = ? " +
                                "WHERE PatientID = (SELECT PatientID FROM PatientTbl WHERE InternalID = ?)";
                
                try (PreparedStatement addressStmt = conn.prepareStatement(addressSql)) {
                    addressStmt.setString(1, patient.getBarangay());
                    addressStmt.setString(2, patient.getCity());
                    addressStmt.setString(3, patient.getProvince());
                    addressStmt.setInt(4, patient.getInternalId());
                    
                    int addressRows = addressStmt.executeUpdate();
                    System.out.println("Updated address rows: " + addressRows);
                    
                    // Commit if patient update was successful, don't require address rows to be updated
                    // as some patients might not have address data
                    if (patientRows > 0) {
                        conn.commit();
                        success = true;
                    } else {
                        conn.rollback();
                    }
                }
            }
        } catch (SQLException e) {
            // Print detailed error info
            System.err.println("SQL Error updating patient: " + e.getMessage());
            e.printStackTrace();
            
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return success;
    }

    // Delete patient by Internal ID
    public boolean deletePatient(int internalId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            // Start transaction to ensure all operations succeed or fail together
            conn.setAutoCommit(false);
            
            // First, get the patient's ID
            String getPatientIdSql = "SELECT PatientID FROM PatientTbl WHERE InternalID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(getPatientIdSql)) {
                stmt.setInt(1, internalId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    String patientId = rs.getString("PatientID");
                    System.out.println("Deleting all records for patient ID: " + patientId);
                    
                    // 1. First delete all appointments for this patient
                    String deleteAppointmentsSql = "DELETE FROM AppointmentTbl WHERE PatientID = ?";
                    try (PreparedStatement deleteAppts = conn.prepareStatement(deleteAppointmentsSql)) {
                        deleteAppts.setString(1, patientId);
                        int appointmentsDeleted = deleteAppts.executeUpdate();
                        System.out.println("Deleted " + appointmentsDeleted + " appointments");
                    }
                    
                    // 2. Delete patient address records
                    String deleteAddressSql = "DELETE FROM PatientAddressTbl WHERE PatientID = ?";
                    try (PreparedStatement deleteAddress = conn.prepareStatement(deleteAddressSql)) {
                        deleteAddress.setString(1, patientId);
                        int addressesDeleted = deleteAddress.executeUpdate();
                        System.out.println("Deleted " + addressesDeleted + " address records");
                    }
                    
                    // 3. Now delete the patient
                    String deletePatientSql = "DELETE FROM PatientTbl WHERE InternalID = ?";
                    try (PreparedStatement deletePatient = conn.prepareStatement(deletePatientSql)) {
                        deletePatient.setInt(1, internalId);
                        int result = deletePatient.executeUpdate();
                        
                        // Commit the transaction if we get here
                        conn.commit();
                        return result > 0;
                    }
                } else {
                    // Patient not found
                    conn.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            // Something went wrong, roll back the transaction
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // Rethrow the exception for the UI to handle
        } finally {
            // Restore auto-commit and close connection
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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
        String sql = "SELECT p.*, pa.Barangay, pa.City, pa.Province " +
                 "FROM PatientTbl p " +
                 "LEFT JOIN PatientAddressTbl pa ON p.PatientID = pa.PatientID " +
                 "WHERE p.FirstName LIKE ? OR p.LastName LIKE ? OR pa.City LIKE ? OR pa.Province LIKE ?";
        
        // Check if search text is a formatted ID
        if (searchText.startsWith("pat-")) {
            Patient patient = getPatientByFormattedId(searchText);
            if (patient != null) {
                patients.add(patient);
                return patients;
            }
        }

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchText + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);

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