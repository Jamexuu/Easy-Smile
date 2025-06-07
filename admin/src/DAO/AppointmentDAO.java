package DAO;

import DataBase.DBConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    
    // ID formatting function
    public static String formatId(String prefix, int base, int internalId) {
        return prefix + String.format("%07d", base + internalId - 1);
    }

    // Inner class for Appointment entity - Updated to match SQL schema
    public static class Appointment {
        private int internalId;
        private String appointmentId; // Formatted ID like "apo-1000000"
        private String patientId;     // Foreign key to PatientTbl
        private String scheduledBy;   // Foreign key to AccountTbl
        private String serviceId;     // Foreign key to ServicesTbl
        private String appointmentDate;     // DATE
        private String appointmentTime;     // TIME
        private String appointmentDateTime; // DATETIME
        private String createdAt;
        private String updatedAt;

        // Constructor
        public Appointment() {}

        public Appointment(int internalId, String patientId, String scheduledBy, String serviceId,
                          String appointmentDate, String appointmentTime, String appointmentDateTime) {
            this.internalId = internalId;
            this.patientId = patientId;
            this.scheduledBy = scheduledBy;
            this.serviceId = serviceId;
            this.appointmentDate = appointmentDate;
            this.appointmentTime = appointmentTime;
            this.appointmentDateTime = appointmentDateTime;
            // Generate formatted appointment ID
            this.appointmentId = formatId("apo-", 1000000, internalId);
        }

        // Getters and Setters
        public int getInternalId() { return internalId; }
        public void setInternalId(int internalId) { 
            this.internalId = internalId; 
            // Update formatted ID when internal ID changes
            if (internalId > 0) {
                this.appointmentId = formatId("apo-", 1000000, internalId);
            }
        }

        public String getAppointmentId() { return appointmentId; }
        public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

        public String getPatientId() { return patientId; }
        public void setPatientId(String patientId) { this.patientId = patientId; }

        public String getScheduledBy() { return scheduledBy; }
        public void setScheduledBy(String scheduledBy) { this.scheduledBy = scheduledBy; }

        public String getServiceId() { return serviceId; }
        public void setServiceId(String serviceId) { this.serviceId = serviceId; }

        public String getAppointmentDate() { return appointmentDate; }
        public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

        public String getAppointmentTime() { return appointmentTime; }
        public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }

        public String getAppointmentDateTime() { return appointmentDateTime; }
        public void setAppointmentDateTime(String appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

        // Helper methods
        public String getFormattedDateTime() {
            return appointmentDate + " " + appointmentTime;
        }
    }

    // Helper method to create appointment object from ResultSet
    private Appointment createAppointmentFromResultSet(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setInternalId(rs.getInt("InternalID"));
        appointment.setAppointmentId(rs.getString("AppointmentID"));
        appointment.setPatientId(rs.getString("PatientID"));
        appointment.setScheduledBy(rs.getString("ScheduledBy"));
        appointment.setServiceId(rs.getString("ServiceID"));
        appointment.setAppointmentDate(rs.getString("AppointmentDate"));
        appointment.setAppointmentTime(rs.getString("AppointmentTime"));
        appointment.setAppointmentDateTime(rs.getString("AppointmentDateTime"));
        appointment.setCreatedAt(rs.getString("created_at"));
        appointment.setUpdatedAt(rs.getString("updated_at"));
        return appointment;
    }

    // Get all appointments
    public List<Appointment> getAllAppointments() throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT AppointmentID, InternalID, PatientID, ScheduledBy, ServiceID, " +
                    "AppointmentDate, AppointmentTime, AppointmentDateTime, created_at, updated_at " +
                    "FROM AppointmentTbl ORDER BY AppointmentDateTime DESC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                appointments.add(createAppointmentFromResultSet(rs));
            }
        }
        return appointments;
    }

    // Get appointment by Internal ID
    public Appointment getAppointmentById(int internalId) throws SQLException {
        String sql = "SELECT AppointmentID, InternalID, PatientID, ScheduledBy, ServiceID, " +
                    "AppointmentDate, AppointmentTime, AppointmentDateTime, created_at, updated_at " +
                    "FROM AppointmentTbl WHERE InternalID = ?";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, internalId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createAppointmentFromResultSet(rs);
                }
            }
        }
        return null;
    }

    // Get appointment by formatted ID (e.g., "apo-1000000")
    public Appointment getAppointmentByFormattedId(String formattedId) throws SQLException {
        String sql = "SELECT AppointmentID, InternalID, PatientID, ScheduledBy, ServiceID, " +
                    "AppointmentDate, AppointmentTime, AppointmentDateTime, created_at, updated_at " +
                    "FROM AppointmentTbl WHERE AppointmentID = ?";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, formattedId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createAppointmentFromResultSet(rs);
                }
            }
        }
        return null;
    }

    // Add new appointment
    public boolean addAppointment(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO AppointmentTbl (AppointmentID, InternalID, PatientID, ScheduledBy, " +
                    "ServiceID, AppointmentDate, AppointmentTime, AppointmentDateTime) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Get next internal ID
            int nextInternalId = getNextInternalId();
            String formattedId = formatId("apo-", 1000000, nextInternalId);
            
            stmt.setString(1, formattedId);
            stmt.setInt(2, nextInternalId);
            stmt.setString(3, appointment.getPatientId());
            stmt.setString(4, appointment.getScheduledBy());
            stmt.setString(5, appointment.getServiceId());
            stmt.setString(6, appointment.getAppointmentDate());
            stmt.setString(7, appointment.getAppointmentTime());
            stmt.setString(8, appointment.getAppointmentDateTime());

            boolean success = stmt.executeUpdate() > 0;
            
            if (success) {
                // Update the appointment object with the generated IDs
                appointment.setInternalId(nextInternalId);
                appointment.setAppointmentId(formattedId);
            }
            
            return success;
        }
    }

    // Update appointment
    public boolean updateAppointment(Appointment appointment) throws SQLException {
        String sql = "UPDATE AppointmentTbl SET PatientID = ?, ScheduledBy = ?, ServiceID = ?, " +
                    "AppointmentDate = ?, AppointmentTime = ?, AppointmentDateTime = ? " +
                    "WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, appointment.getPatientId());
            stmt.setString(2, appointment.getScheduledBy());
            stmt.setString(3, appointment.getServiceId());
            stmt.setString(4, appointment.getAppointmentDate());
            stmt.setString(5, appointment.getAppointmentTime());
            stmt.setString(6, appointment.getAppointmentDateTime());
            stmt.setInt(7, appointment.getInternalId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Delete appointment
    public boolean deleteAppointment(int internalId) throws SQLException {
        String sql = "DELETE FROM AppointmentTbl WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, internalId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete appointment by formatted ID
    public boolean deleteAppointmentByFormattedId(String formattedId) throws SQLException {
        String sql = "DELETE FROM AppointmentTbl WHERE AppointmentID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, formattedId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Get appointments by patient ID
    public List<Appointment> getAppointmentsByPatientId(String patientId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT AppointmentID, InternalID, PatientID, ScheduledBy, ServiceID, " +
                    "AppointmentDate, AppointmentTime, AppointmentDateTime, created_at, updated_at " +
                    "FROM AppointmentTbl WHERE PatientID = ? ORDER BY AppointmentDateTime DESC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(createAppointmentFromResultSet(rs));
                }
            }
        }
        return appointments;
    }

    // Get appointments by service ID
    public List<Appointment> getAppointmentsByServiceId(String serviceId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT AppointmentID, InternalID, PatientID, ScheduledBy, ServiceID, " +
                    "AppointmentDate, AppointmentTime, AppointmentDateTime, created_at, updated_at " +
                    "FROM AppointmentTbl WHERE ServiceID = ? ORDER BY AppointmentDateTime DESC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, serviceId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(createAppointmentFromResultSet(rs));
                }
            }
        }
        return appointments;
    }

    // Get appointments by date range
    public List<Appointment> getAppointmentsByDateRange(String startDate, String endDate) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT AppointmentID, InternalID, PatientID, ScheduledBy, ServiceID, " +
                    "AppointmentDate, AppointmentTime, AppointmentDateTime, created_at, updated_at " +
                    "FROM AppointmentTbl WHERE AppointmentDate BETWEEN ? AND ? " +
                    "ORDER BY AppointmentDateTime ASC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(createAppointmentFromResultSet(rs));
                }
            }
        }
        return appointments;
    }

    // Get appointments for a specific date
    public List<Appointment> getAppointmentsByDate(String date) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT AppointmentID, InternalID, PatientID, ScheduledBy, ServiceID, " +
                    "AppointmentDate, AppointmentTime, AppointmentDateTime, created_at, updated_at " +
                    "FROM AppointmentTbl WHERE AppointmentDate = ? ORDER BY AppointmentTime ASC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, date);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(createAppointmentFromResultSet(rs));
                }
            }
        }
        return appointments;
    }

    // Get total appointment count
    public int getTotalAppointmentCount() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM AppointmentTbl";

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
        String sql = "SELECT COALESCE(MAX(InternalID), 0) + 1 as nextId FROM AppointmentTbl";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("nextId");
            }
        }
        return 1; // Default to 1 if no records exist
    }

    // Search appointments (by Patient ID, Service ID, or formatted ID)
    public List<Appointment> searchAppointments(String searchText) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        
        // First check if search text is a formatted ID
        if (searchText.startsWith("apo-")) {
            Appointment appointment = getAppointmentByFormattedId(searchText);
            if (appointment != null) {
                appointments.add(appointment);
                return appointments;
            }
        }
        
        String sql = "SELECT AppointmentID, InternalID, PatientID, ScheduledBy, ServiceID, " +
                    "AppointmentDate, AppointmentTime, AppointmentDateTime, created_at, updated_at " +
                    "FROM AppointmentTbl WHERE " +
                    "PatientID LIKE ? OR " +
                    "ServiceID LIKE ? OR " +
                    "ScheduledBy LIKE ? " +
                    "ORDER BY AppointmentDateTime DESC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchText + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(createAppointmentFromResultSet(rs));
                }
            }
        }
        return appointments;
    }

    // Check if appointment exists by date and time
    public boolean appointmentExistsByDateTime(String appointmentDateTime, int excludeInternalId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM AppointmentTbl WHERE AppointmentDateTime = ? AND InternalID != ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, appointmentDateTime);
            stmt.setInt(2, excludeInternalId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }

    // Validate appointment information
    public boolean validateAppointment(Appointment appointment) {
        if (appointment == null) return false;
        
        return appointment.getPatientId() != null && !appointment.getPatientId().trim().isEmpty() &&
               appointment.getScheduledBy() != null && !appointment.getScheduledBy().trim().isEmpty() &&
               appointment.getServiceId() != null && !appointment.getServiceId().trim().isEmpty() &&
               appointment.getAppointmentDate() != null && !appointment.getAppointmentDate().trim().isEmpty() &&
               appointment.getAppointmentTime() != null && !appointment.getAppointmentTime().trim().isEmpty() &&
               appointment.getAppointmentDateTime() != null && !appointment.getAppointmentDateTime().trim().isEmpty();
    }

    // Check if formatted ID is valid
    public static boolean isValidAppointmentIdFormat(String appointmentId) {
        return appointmentId != null && appointmentId.matches("^apo-\\d{7}$");
    }

    // Extract internal ID from formatted appointment ID
    public static int extractInternalIdFromFormattedId(String formattedId) {
        if (!isValidAppointmentIdFormat(formattedId)) {
            return -1;
        }
        try {
            String numberPart = formattedId.substring(4); // Remove "apo-"
            int extractedNumber = Integer.parseInt(numberPart);
            return extractedNumber - 1000000 + 1; // Reverse the formatting logic
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}