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
        private String id;
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
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
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
        "SELECT AccountID, FirstName, MiddleName, LastName, BirthDate, email, PhoneNumber, created_at, updated_at FROM patients_tbl ORDER BY created_at DESC";
    
    private static final String SELECT_ACCOUNT_BY_ID = 
        "SELECT AccountID, FirstName, MiddleName, LastName, BirthDate, email, PhoneNumber, created_at, updated_at FROM patients_tbl WHERE AccountID = ?";
    
    private static final String SELECT_ACCOUNT_BY_EMAIL = 
        "SELECT AccountID, FirstName, MiddleName, LastName, BirthDate, email, PhoneNumber, created_at, updated_at FROM patients_tbl WHERE email = ?";
    
    private static final String INSERT_ACCOUNT = 
        "INSERT INTO patients_tbl (FirstName, MiddleName, LastName, BirthDate, email, PhoneNumber) VALUES (?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_ACCOUNT = 
        "UPDATE patients_tbl SET FirstName = ?, MiddleName = ?, LastName = ?, BirthDate = ?, email = ?, PhoneNumber = ?, updated_at = CURRENT_TIMESTAMP WHERE AccountID = ?";
    
    private static final String DELETE_ACCOUNT = 
        "DELETE FROM patients_tbl WHERE AccountID = ?";
    
    private static final String SEARCH_ACCOUNTS = 
        "SELECT AccountID, FirstName, MiddleName, LastName, BirthDate, email, PhoneNumber, created_at, updated_at FROM patients_tbl WHERE " +
        "FirstName LIKE ? OR MiddleName LIKE ? OR LastName LIKE ? OR email LIKE ? OR PhoneNumber LIKE ? " +
        "ORDER BY created_at DESC";
    
    private static final String COUNT_ACCOUNTS = 
        "SELECT COUNT(*) FROM patients_tbl";
    
    private static final String CHECK_EMAIL_EXISTS = 
        "SELECT COUNT(*) FROM patients_tbl WHERE email = ? AND AccountID != ?";
    
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
    public Account getAccountById(String id) {
        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_ACCOUNT_BY_ID)) {

            stmt.setString(1, id);

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

    public Account getAccountByFormattedId(String formattedId) {
        String sql = "SELECT AccountID, FirstName, MiddleName, LastName, BirthDate, Gender, Email, Password, PhoneNumber, created_at, updated_at FROM AccountTbl WHERE AccountID = ?";
        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, formattedId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving account by formatted ID: " + e.getMessage());
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
        if (isEmailExists(account.getEmail(), null)) {
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
                        account.setId(generatedKeys.getString(1));
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
            stmt.setString(7, account.getId());
            
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
    public boolean deleteAccount(String accountId) {
        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_ACCOUNT)) {

            stmt.setString(1, accountId);

            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Account deleted successfully: " + accountId);
                return true;
            } else {
                System.err.println("No account found with ID: " + accountId);
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
            stmt.setString(1, searchPattern); // FirstName
            stmt.setString(2, searchPattern); // MiddleName
            stmt.setString(3, searchPattern); // LastName
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
    public boolean isEmailExists(String email, String excludeId) {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_EMAIL_EXISTS)) {
            
            stmt.setString(1, email);
            stmt.setString(2, excludeId);
            
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
        account.setId(rs.getString("AccountID"));
        account.setFirstName(rs.getString("FirstName"));
        account.setMiddleName(rs.getString("MiddleName"));
        account.setLastName(rs.getString("LastName"));
        account.setBirthday(rs.getString("BirthDate"));
        account.setEmail(rs.getString("Email"));
        account.setPhoneNumber(rs.getString("PhoneNumber"));
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