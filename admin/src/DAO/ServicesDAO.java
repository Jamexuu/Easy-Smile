package DAO;

import DataBase.DBConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicesDAO {
    
    // ID formatting function
    public static String formatId(String prefix, int base, int internalId) {
        return prefix + String.format("%07d", base + internalId - 1);
    }

    // Inner class for Service entity
    public static class Service {
        private int internalId;
        private String serviceId; // Formatted ID like "ser-1000000"
        private String serviceName;
        private String serviceDesc;
        private double startingPrice;
        private String status; // ENUM('Available', 'Unavailable')
        private String createdAt;
        private String updatedAt;

        // Constructors
        public Service() {}

        public Service(int internalId, String serviceName, String serviceDesc, 
                      double startingPrice, String status) {
            this.internalId = internalId;
            this.serviceName = serviceName;
            this.serviceDesc = serviceDesc;
            this.startingPrice = startingPrice;
            this.status = status;
            // Generate formatted service ID
            this.serviceId = formatId("ser-", 1000000, internalId);
        }

        // Getters and Setters
        public int getInternalId() { return internalId; }
        public void setInternalId(int internalId) { 
            this.internalId = internalId; 
            // Update formatted ID when internal ID changes
            if (internalId > 0) {
                this.serviceId = formatId("ser-", 1000000, internalId);
            }
        }

        public String getServiceId() { return serviceId; }
        public void setServiceId(String serviceId) { this.serviceId = serviceId; }

        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }

        public String getServiceDesc() { return serviceDesc; }
        public void setServiceDesc(String serviceDesc) { this.serviceDesc = serviceDesc; }

        public double getStartingPrice() { return startingPrice; }
        public void setStartingPrice(double startingPrice) { this.startingPrice = startingPrice; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

        // Helper method to check if service is available
        public boolean isAvailable() {
            return "Available".equals(status);
        }

        // Helper method to get formatted price
        public String getFormattedPrice() {
            return String.format("₱%.2f", startingPrice);
        }
    }

    // Helper method to create Service object from ResultSet
    private Service createServiceFromResultSet(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setInternalId(rs.getInt("InternalID"));
        service.setServiceId(rs.getString("ServiceID"));
        service.setServiceName(rs.getString("ServiceName"));
        service.setServiceDesc(rs.getString("ServicDesc"));
        service.setStartingPrice(rs.getDouble("StartingPrice"));
        service.setStatus(rs.getString("Status"));
        service.setCreatedAt(rs.getString("created_at"));
        service.setUpdatedAt(rs.getString("updated_at"));
        return service;
    }

    // Get all services
    public List<Service> getAllServices() throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM ServicesTbl ORDER BY InternalID ASC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                services.add(createServiceFromResultSet(rs));
            }
        }
        return services;
    }

    // Get service by Internal ID
    public Service getServiceById(int internalId) throws SQLException {
        String sql = "SELECT * FROM ServicesTbl WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, internalId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createServiceFromResultSet(rs);
                }
            }
        }
        return null;
    }

    // Get service by formatted ID (e.g., "ser-1000000")
    public Service getServiceByFormattedId(String formattedId) throws SQLException {
        String sql = "SELECT * FROM ServicesTbl WHERE ServiceID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, formattedId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createServiceFromResultSet(rs);
                }
            }
        }
        return null;
    }

    // Add new service
    public boolean addService(Service service) throws SQLException {
        String sql = "INSERT INTO ServicesTbl (ServiceID, InternalID, ServiceName, ServicDesc, " +
                    "StartingPrice, Status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Get next internal ID
            int nextInternalId = getNextInternalId();
            String formattedId = formatId("ser-", 1000000, nextInternalId);
            
            stmt.setString(1, formattedId);
            stmt.setInt(2, nextInternalId);
            stmt.setString(3, service.getServiceName());
            stmt.setString(4, service.getServiceDesc());
            stmt.setDouble(5, service.getStartingPrice());
            stmt.setString(6, service.getStatus());

            boolean success = stmt.executeUpdate() > 0;
            
            if (success) {
                // Update the service object with the generated IDs
                service.setInternalId(nextInternalId);
                service.setServiceId(formattedId);
            }
            
            return success;
        }
    }

    // Update existing service
    public boolean updateService(Service service) throws SQLException {
        String sql = "UPDATE ServicesTbl SET ServiceName = ?, ServicDesc = ?, " +
                    "StartingPrice = ?, Status = ? WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, service.getServiceName());
            stmt.setString(2, service.getServiceDesc());
            stmt.setDouble(3, service.getStartingPrice());
            stmt.setString(4, service.getStatus());
            stmt.setInt(5, service.getInternalId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Update service by formatted ID
    public boolean updateServiceByFormattedId(Service service) throws SQLException {
        String sql = "UPDATE ServicesTbl SET ServiceName = ?, ServicDesc = ?, " +
                    "StartingPrice = ?, Status = ? WHERE ServiceID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, service.getServiceName());
            stmt.setString(2, service.getServiceDesc());
            stmt.setDouble(3, service.getStartingPrice());
            stmt.setString(4, service.getStatus());
            stmt.setString(5, service.getServiceId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Delete service by Internal ID
    public boolean deleteService(int internalId) throws SQLException {
        String sql = "DELETE FROM ServicesTbl WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, internalId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete service by formatted ID
    public boolean deleteServiceByFormattedId(String formattedId) throws SQLException {
        String sql = "DELETE FROM ServicesTbl WHERE ServiceID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, formattedId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Search services by name, description, or formatted ID
    public List<Service> searchServices(String searchText) throws SQLException {
        List<Service> services = new ArrayList<>();
        
        // Check if search text is a formatted ID
        if (searchText.startsWith("ser-")) {
            Service service = getServiceByFormattedId(searchText);
            if (service != null) {
                services.add(service);
                return services;
            }
        }
        
        String sql = "SELECT * FROM ServicesTbl WHERE " +
                    "ServiceName LIKE ? OR " +
                    "ServicDesc LIKE ? " +
                    "ORDER BY InternalID ASC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchText + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    services.add(createServiceFromResultSet(rs));
                }
            }
        }
        return services;
    }

    // Get services by status
    public List<Service> getServicesByStatus(String status) throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM ServicesTbl WHERE Status = ? ORDER BY InternalID ASC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    services.add(createServiceFromResultSet(rs));
                }
            }
        }
        return services;
    }

    // Get available services only
    public List<Service> getAvailableServices() throws SQLException {
        return getServicesByStatus("Available");
    }

    // Get unavailable services only
    public List<Service> getUnavailableServices() throws SQLException {
        return getServicesByStatus("Unavailable");
    }

    // Get services by price range
    public List<Service> getServicesByPriceRange(double minPrice, double maxPrice) throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM ServicesTbl WHERE StartingPrice BETWEEN ? AND ? ORDER BY StartingPrice ASC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, minPrice);
            stmt.setDouble(2, maxPrice);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    services.add(createServiceFromResultSet(rs));
                }
            }
        }
        return services;
    }

    // Get total service count
    public int getTotalServiceCount() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM ServicesTbl";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    // Get service count by status
    public int getServiceCountByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM ServicesTbl WHERE Status = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
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
        String sql = "SELECT COALESCE(MAX(InternalID), 0) + 1 as nextId FROM ServicesTbl";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("nextId");
            }
        }
        return 1; // Default to 1 if no records exist
    }

    // Check if service exists by name
    public boolean serviceExistsByName(String serviceName, int excludeInternalId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM ServicesTbl WHERE ServiceName = ? AND InternalID != ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, serviceName);
            stmt.setInt(2, excludeInternalId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }

    // Validate service information
    public boolean validateService(Service service) {
        if (service == null) return false;
        
        return service.getServiceName() != null && !service.getServiceName().trim().isEmpty() &&
               service.getServiceDesc() != null && !service.getServiceDesc().trim().isEmpty() &&
               service.getStartingPrice() >= 0 &&
               service.getStatus() != null && 
               ("Available".equals(service.getStatus()) || "Unavailable".equals(service.getStatus()));
    }

    // Update service status only
    public boolean updateServiceStatus(int internalId, String status) throws SQLException {
        String sql = "UPDATE ServicesTbl SET Status = ? WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, internalId);

            return stmt.executeUpdate() > 0;
        }
    }

    // Update service price only
    public boolean updateServicePrice(int internalId, double newPrice) throws SQLException {
        String sql = "UPDATE ServicesTbl SET StartingPrice = ? WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, newPrice);
            stmt.setInt(2, internalId);

            return stmt.executeUpdate() > 0;
        }
    }

    // Get cheapest service
    public Service getCheapestService() throws SQLException {
        String sql = "SELECT * FROM ServicesTbl WHERE Status = 'Available' ORDER BY StartingPrice ASC LIMIT 1";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return createServiceFromResultSet(rs);
            }
        }
        return null;
    }

    // Get most expensive service
    public Service getMostExpensiveService() throws SQLException {
        String sql = "SELECT * FROM ServicesTbl WHERE Status = 'Available' ORDER BY StartingPrice DESC LIMIT 1";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return createServiceFromResultSet(rs);
            }
        }
        return null;
    }

    // Get average service price
    public double getAverageServicePrice() throws SQLException {
        String sql = "SELECT AVG(StartingPrice) as avgPrice FROM ServicesTbl WHERE Status = 'Available'";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble("avgPrice");
            }
        }
        return 0.0;
    }

    // Check if service ID format is valid
    public static boolean isValidServiceIdFormat(String serviceId) {
        return serviceId != null && serviceId.matches("^ser-\\d{7}$");
    }

    // Extract internal ID from formatted service ID
    public static int extractInternalIdFromFormattedId(String formattedId) {
        if (!isValidServiceIdFormat(formattedId)) {
            return -1;
        }
        try {
            String numberPart = formattedId.substring(4); // Remove "ser-"
            int extractedNumber = Integer.parseInt(numberPart);
            return extractedNumber - 1000000 + 1; // Reverse the formatting logic
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Bulk update service status
    public boolean bulkUpdateServiceStatus(List<Integer> internalIds, String status) throws SQLException {
        String sql = "UPDATE ServicesTbl SET Status = ? WHERE InternalID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            for (int internalId : internalIds) {
                stmt.setString(1, status);
                stmt.setInt(2, internalId);
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            conn.commit();
            
            // Check if all updates were successful
            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }
            return true;
            
        } catch (SQLException e) {
            // Rollback on error
            try (Connection conn = DBConnector.getConnection()) {
                conn.rollback();
            }
            throw e;
        }
    }
}