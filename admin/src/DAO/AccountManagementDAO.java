package DAO;

import DataBase.DBConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountManagementDAO {
    
    /**
     * Account model class for data transfer
     */
    public static class Account {
        private int id;
        private String firstName;
        private String middleName;
        private String lastName;
        private String birthday;
        private String email;
        private String phoneNumber;
        private Timestamp createdAt;
        private Timestamp updatedAt;
        
        // Constructors
        public Account() {}
        
        public Account(String firstName, String middleName, String lastName, String birthday, 
                      String email, String phoneNumber) {
            this.firstName = firstName;
            this.middleName = middleName;
            this.lastName = lastName;
            this.birthday = birthday;
            this.email = email;
            this.phoneNumber = phoneNumber;
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
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
        
        public Timestamp getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
        
        @Override
        public String toString() {
            return "Account{" +
                    "id=" + id +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }
    }
    
    // SQL Queries - Updated without address column
    private static final String SELECT_ALL_ACCOUNTS = 
        "SELECT user_id, first_name, middle_name, last_name, birth_date, email, phone, created_at, updated_at FROM patients_tbl ORDER BY created_at DESC";
    
    private static final String SELECT_ACCOUNT_BY_ID = 
        "SELECT user_id, first_name, middle_name, last_name, birth_date, email, phone, created_at, updated_at FROM patients_tbl WHERE user_id = ?";
    
    private static final String SELECT_ACCOUNT_BY_EMAIL = 
        "SELECT user_id, first_name, middle_name, last_name, birth_date, email, phone, created_at, updated_at FROM patients_tbl WHERE email = ?";
    
    private static final String INSERT_ACCOUNT = 
        "INSERT INTO patients_tbl (first_name, middle_name, last_name, birth_date, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_ACCOUNT = 
        "UPDATE patients_tbl SET first_name = ?, middle_name = ?, last_name = ?, birth_date = ?, email = ?, phone = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
    
    private static final String DELETE_ACCOUNT = 
        "DELETE FROM patients_tbl WHERE user_id = ?";
    
    private static final String SEARCH_ACCOUNTS = 
        "SELECT user_id, first_name, middle_name, last_name, birth_date, email, phone, created_at, updated_at FROM patients_tbl WHERE " +
        "first_name LIKE ? OR middle_name LIKE ? OR last_name LIKE ? OR email LIKE ? OR phone LIKE ? " +
        "ORDER BY created_at DESC";
    
    private static final String COUNT_ACCOUNTS = 
        "SELECT COUNT(*) FROM patients_tbl";
    
    private static final String CHECK_EMAIL_EXISTS = 
        "SELECT COUNT(*) FROM patients_tbl WHERE email = ? AND user_id != ?";
    
    /**
     * Get all accounts from database
     * @return List of Account objects
     */
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_ACCOUNTS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
            
            System.out.println("Retrieved " + accounts.size() + " accounts from database");
            
        } catch (SQLException e) {
            System.err.println("Error retrieving accounts: " + e.getMessage());
            e.printStackTrace();
        }
        
        return accounts;
    }
    
    /**
     * Get account by ID
     * @param id Account ID
     * @return Account object or null if not found
     */
    public Account getAccountById(int id) {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ACCOUNT_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving account by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get account by email
     * @param email Account email
     * @return Account object or null if not found
     */
    public Account getAccountByEmail(String email) {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ACCOUNT_BY_EMAIL)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving account by email: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Add new account to database
     * @param account Account object to add
     * @return true if successful, false otherwise
     */
    public boolean addAccount(Account account) {
        // Check if email already exists
        if (isEmailExists(account.getEmail(), 0)) {
            System.err.println("Account with email " + account.getEmail() + " already exists");
            return false;
        }
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_ACCOUNT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, account.getFirstName());
            stmt.setString(2, account.getMiddleName());
            stmt.setString(3, account.getLastName());
            stmt.setString(4, account.getBirthday());
            stmt.setString(5, account.getEmail());
            stmt.setString(6, account.getPhoneNumber());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get generated ID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        account.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Account added successfully with ID: " + account.getId());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding account: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Update existing account
     * @param account Account object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateAccount(Account account) {
        // Check if email already exists for another account
        if (isEmailExists(account.getEmail(), account.getId())) {
            System.err.println("Email " + account.getEmail() + " is already used by another account");
            return false;
        }
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_ACCOUNT)) {
            
            stmt.setString(1, account.getFirstName());
            stmt.setString(2, account.getMiddleName());
            stmt.setString(3, account.getLastName());
            stmt.setString(4, account.getBirthday());
            stmt.setString(5, account.getEmail());
            stmt.setString(6, account.getPhoneNumber());
            stmt.setInt(7, account.getId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Account updated successfully: " + account.getId());
                return true;
            } else {
                System.err.println("No account found with ID: " + account.getId());
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating account: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Delete account by ID
     * @param id Account ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteAccount(int id) {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_ACCOUNT)) {
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Account deleted successfully: " + id);
                return true;
            } else {
                System.err.println("No account found with ID: " + id);
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting account: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Search accounts by keyword
     * @param keyword Search keyword
     * @return List of matching Account objects
     */
    public List<Account> searchAccounts(String keyword) {
        List<Account> accounts = new ArrayList<>();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllAccounts();
        }
        
        String searchPattern = "%" + keyword.trim() + "%";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_ACCOUNTS)) {
            
            // Set search pattern for 5 searchable columns (removed address)
            stmt.setString(1, searchPattern); // first_name
            stmt.setString(2, searchPattern); // middle_name
            stmt.setString(3, searchPattern); // last_name
            stmt.setString(4, searchPattern); // email
            stmt.setString(5, searchPattern); // phone
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));
                }
            }
            
            System.out.println("Found " + accounts.size() + " accounts matching: " + keyword);
            
        } catch (SQLException e) {
            System.err.println("Error searching accounts: " + e.getMessage());
            e.printStackTrace();
        }
        
        return accounts;
    }
    
    /**
     * Get total count of accounts
     * @return Total number of accounts
     */
    public int getAccountCount() {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_ACCOUNTS);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting accounts: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Check if email already exists for another account
     * @param email Email to check
     * @param excludeId ID to exclude from check (for updates)
     * @return true if email exists, false otherwise
     */
    public boolean isEmailExists(String email, int excludeId) {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_EMAIL_EXISTS)) {
            
            stmt.setString(1, email);
            stmt.setInt(2, excludeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Map ResultSet to Account object - Updated without address
     * @param rs ResultSet from database query
     * @return Account object
     * @throws SQLException if database error occurs
     */
    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setId(rs.getInt("user_id"));
        account.setFirstName(rs.getString("first_name"));
        account.setMiddleName(rs.getString("middle_name"));
        account.setLastName(rs.getString("last_name"));
        account.setBirthday(rs.getString("birth_date"));
        account.setEmail(rs.getString("email"));
        account.setPhoneNumber(rs.getString("phone"));
        account.setCreatedAt(rs.getTimestamp("created_at"));
        account.setUpdatedAt(rs.getTimestamp("updated_at"));
        return account;
    }
    
    /**
     * Test database operations
     * @return true if all tests pass, false otherwise
     */
    public boolean testDatabaseOperations() {
        System.out.println("Testing AccountManagementDAO operations...");
        
        try {
            // Test connection
            if (!DBConnector.testConnection()) {
                System.err.println("Database connection test failed");
                return false;
            }
            
            // Test getting all accounts
            List<Account> accounts = getAllAccounts();
            System.out.println("Total accounts in database: " + accounts.size());
            
            // Test count
            int count = getAccountCount();
            System.out.println("Account count: " + count);
            
            System.out.println("All DAO tests completed successfully!");
            return true;
            
        } catch (Exception e) {
            System.err.println("DAO test failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}