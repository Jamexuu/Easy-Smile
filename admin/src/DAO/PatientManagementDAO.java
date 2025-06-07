package DAO;

import DataBase.DBConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientManagementDAO {
    
    /**
     * Patient model class for data transfer
     */
    public static class Patient {
        private int id;
        private String firstName;
        private String middleName;
        private String lastName;
        private String birthday;
        private String gender;
        private Timestamp createdAt;
        private Timestamp updatedAt;
        
        // Constructors
        public Patient() {}
        
        public Patient(String firstName, String middleName, String lastName, String birthday, String gender) {
            this.firstName = firstName;
            this.middleName = middleName;
            this.lastName = lastName;
            this.birthday = birthday;
            this.gender = gender;
        }
        
        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getMiddleName() { return middleName; }
        public void setMiddleName(String middleName) { this.middleName = middleName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getBirthday() { return birthday; }
        public void setBirthday(String birthday) { this.birthday = birthday; }
        
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        
        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
        
        public Timestamp getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
        
        @Override
        public String toString() {
            return "Patient{" +
                    "id=" + id +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", gender='" + gender + '\'' +
                    '}';
        }
    }
    
    // SQL Queries
    private static final String SELECT_ALL_PATIENTS = 
        "SELECT user_id, first_name, middle_name, last_name, birth_date, gender, created_at, updated_at FROM patients_tbl ORDER BY created_at DESC";
    
    private static final String SELECT_PATIENT_BY_ID = 
        "SELECT user_id, first_name, middle_name, last_name, birth_date, gender, created_at, updated_at FROM patients_tbl WHERE user_id = ?";
    
    private static final String INSERT_PATIENT = 
        "INSERT INTO patients_tbl (first_name, middle_name, last_name, birth_date, gender) VALUES (?, ?, ?, ?, ?)";
    
    private static final String UPDATE_PATIENT = 
        "UPDATE patients_tbl SET first_name = ?, middle_name = ?, last_name = ?, birth_date = ?, gender = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
    
    private static final String DELETE_PATIENT = 
        "DELETE FROM patients_tbl WHERE user_id = ?";
    
    private static final String SEARCH_PATIENTS = 
        "SELECT user_id, first_name, middle_name, last_name, birth_date, gender, created_at, updated_at FROM patients_tbl WHERE " +
        "first_name LIKE ? OR middle_name LIKE ? OR last_name LIKE ? OR gender LIKE ? " +
        "ORDER BY created_at DESC";
    
    private static final String COUNT_PATIENTS = 
        "SELECT COUNT(*) FROM patients_tbl";
    
    /**
     * Get all patients from database
     * @return List of Patient objects
     */
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_PATIENTS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
            
            System.out.println("Retrieved " + patients.size() + " patients from database");
            
        } catch (SQLException e) {
            System.err.println("Error retrieving patients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return patients;
    }
    
    /**
     * Get patient by ID
     * @param id Patient ID
     * @return Patient object or null if not found
     */
    public Patient getPatientById(int id) {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_PATIENT_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving patient by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Add new patient to database
     * @param patient Patient object to add
     * @return true if successful, false otherwise
     */
    public boolean addPatient(Patient patient) {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_PATIENT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getMiddleName());
            stmt.setString(3, patient.getLastName());
            stmt.setString(4, patient.getBirthday());
            stmt.setString(5, patient.getGender());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get generated ID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        patient.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Patient added successfully with ID: " + patient.getId());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding patient: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Update existing patient
     * @param patient Patient object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updatePatient(Patient patient) {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_PATIENT)) {
            
            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getMiddleName());
            stmt.setString(3, patient.getLastName());
            stmt.setString(4, patient.getBirthday());
            stmt.setString(5, patient.getGender());
            stmt.setInt(6, patient.getId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Patient updated successfully: " + patient.getId());
                return true;
            } else {
                System.err.println("No patient found with ID: " + patient.getId());
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating patient: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Delete patient by ID
     * @param id Patient ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deletePatient(int id) {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_PATIENT)) {
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Patient deleted successfully: " + id);
                return true;
            } else {
                System.err.println("No patient found with ID: " + id);
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting patient: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Search patients by keyword
     * @param keyword Search keyword
     * @return List of matching Patient objects
     */
    public List<Patient> searchPatients(String keyword) {
        List<Patient> patients = new ArrayList<>();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPatients();
        }
        
        String searchPattern = "%" + keyword.trim() + "%";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_PATIENTS)) {
            
            // Set search pattern for 4 searchable columns
            stmt.setString(1, searchPattern); // first_name
            stmt.setString(2, searchPattern); // middle_name
            stmt.setString(3, searchPattern); // last_name
            stmt.setString(4, searchPattern); // gender
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(mapResultSetToPatient(rs));
                }
            }
            
            System.out.println("Found " + patients.size() + " patients matching: " + keyword);
            
        } catch (SQLException e) {
            System.err.println("Error searching patients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return patients;
    }
    
    /**
     * Get total count of patients
     * @return Total number of patients
     */
    public int getPatientCount() {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_PATIENTS);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting patients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Map ResultSet to Patient object
     * @param rs ResultSet from database query
     * @return Patient object
     * @throws SQLException if database error occurs
     */
    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getInt("user_id"));
        patient.setFirstName(rs.getString("first_name"));
        patient.setMiddleName(rs.getString("middle_name"));
        patient.setLastName(rs.getString("last_name"));
        patient.setBirthday(rs.getString("birth_date"));
        patient.setGender(rs.getString("gender"));
        patient.setCreatedAt(rs.getTimestamp("created_at"));
        patient.setUpdatedAt(rs.getTimestamp("updated_at"));
        return patient;
    }
    
    /**
     * Test database operations
     * @return true if all tests pass, false otherwise
     */
    public boolean testDatabaseOperations() {
        System.out.println("Testing PatientManagementDAO operations...");
        
        try {
            // Test connection
            if (!DBConnector.testConnection()) {
                System.err.println("Database connection test failed");
                return false;
            }
            
            // Test getting all patients
            List<Patient> patients = getAllPatients();
            System.out.println("Total patients in database: " + patients.size());
            
            // Test count
            int count = getPatientCount();
            System.out.println("Patient count: " + count);
            
            System.out.println("All DAO tests completed successfully!");
            return true;
            
        } catch (Exception e) {
            System.err.println("DAO test failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}