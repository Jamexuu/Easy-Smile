package DAO;

import DataBase.DBConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountManagementDAO {

    /* @param prefix The string prefix (e.g., "ACC-")
    * @param base The base number to start from (e.g., 1000000)
    * @param internalId The internal ID from the database
    * @return Formatted ID string
    */
    public static String formatId(String prefix, int base, int internalId) {
        return prefix + String.format("%07d", base + internalId - 1);
    }
    
    /**
     * Account model class for data transfer
     */

    public static class Account {
        private int internalId;
        private String id;
        private String firstName;
        private String middleName;
        private String lastName;
        private String birthday;
        private String email;
        private String phoneNumber;

        private String barangay;
        private String city;
        private String province;

        private String fullName;
        private String address;

        private Timestamp createdAt;
        private Timestamp updatedAt;

        public Account() {
            // Default constructor
        }

        public Account(String id, String firstName, String middleName, String lastName, String birthday,
                    String email, String phoneNumber, String barangay, String city, String province) {
            this.firstName = firstName;
            this.middleName = middleName;
            this.lastName = lastName;
            this.birthday = birthday;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.barangay = barangay;
            this.city = city;
            this.province = province;
        }
        
        // Getters and Setters
        public int getInternalId() { return internalId; }
        public void setInternalId(int internalId) { 
            this.internalId = internalId;
            // Auto-update the formatted id when internal id changes
            if (internalId > 0) {
                this.id = formatId("ACC-", 1000000, internalId);
            }
        }
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
        
        public String getBarangay() { return barangay; }
        public void setBarangay(String barangay) { this.barangay = barangay; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }

        // Getter for FullName
        public String getFullName() {
            return fullName;
        }

        // Setter for FullName
        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        // Getter for Address
        public String getAddress() {
            return address;
        }

        // Setter for Address
        public void setAddress(String address) {
            this.address = address;
        }

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
    "SELECT a.AccountID, a.FirstName, a.MiddleName, a.LastName, a.BirthDate, a.Email, a.PhoneNumber, " +
    "addr.Barangay, addr.City, addr.Province, a.created_at, a.updated_at " + 
    "FROM AccountTbl a " +
    "LEFT JOIN AccountAddressTbl addr ON a.AccountID = addr.AccountID " +
    "ORDER BY a.created_at DESC";
    
    private static final String SELECT_ACCOUNT_BY_ID = 
    "SELECT a.AccountID, a.FirstName, a.MiddleName, a.LastName, a.BirthDate, a.Email, a.PhoneNumber, " +
    "addr.Barangay, addr.City, addr.Province, a.created_at, a.updated_at " + 
    "FROM AccountTbl a " +
    "LEFT JOIN AccountAddressTbl addr ON a.AccountID = addr.AccountID " + 
    "WHERE a.AccountID = ?";
    
    private static final String SELECT_ACCOUNT_BY_EMAIL = 
    "SELECT a.AccountID, a.FirstName, a.MiddleName, a.LastName, a.BirthDate, a.Email, a.PhoneNumber, " +
    "addr.Barangay, addr.City, addr.Province, a.created_at, a.updated_at " + 
    "FROM AccountTbl a " +
    "LEFT JOIN AccountAddressTbl addr ON a.AccountID = addr.AccountID " + 
    "WHERE a.Email = ?";
    
    private static final String INSERT_ACCOUNT = 
        "INSERT INTO AccountTbl (FirstName, MiddleName, LastName, BirthDate, email, PhoneNumber) VALUES (?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_ACCOUNT = 
        "UPDATE AccountTbl SET FirstName = ?, MiddleName = ?, LastName = ?, BirthDate = ?, email = ?, PhoneNumber = ?, updated_at = CURRENT_TIMESTAMP WHERE AccountID = ?";
    
    private static final String DELETE_ACCOUNT = 
        "DELETE FROM AccountTbl WHERE AccountID = ?";
    
    private static final String SEARCH_ACCOUNTS = 
    "SELECT a.AccountID, a.FirstName, a.MiddleName, a.LastName, a.BirthDate, a.Email, a.PhoneNumber, " +
    "addr.Barangay, addr.City, addr.Province, a.created_at, a.updated_at " +
    "FROM AccountTbl a " + 
    "LEFT JOIN AccountAddressTbl addr ON a.AccountID = addr.AccountID " +
    "WHERE a.FirstName LIKE ? OR a.MiddleName LIKE ? OR a.LastName LIKE ? OR a.Email LIKE ? " +
    "OR a.PhoneNumber LIKE ? OR addr.City LIKE ? OR addr.Province LIKE ? " + 
    "ORDER BY a.created_at DESC";
    
    private static final String COUNT_ACCOUNTS = 
        "SELECT COUNT(*) FROM AccountTbl";
    
    private static final String CHECK_EMAIL_EXISTS = 
        "SELECT COUNT(*) FROM AccountTbl WHERE email = ? AND AccountID != ?";
    
    /**
     * Get all accounts from database
     * @return List of Account objects
     */
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT a.AccountID, a.FirstName, a.MiddleName, a.LastName, a.BirthDate, a.Email, a.PhoneNumber, " +
                 "addr.Barangay, addr.City, addr.Province, a.created_at, a.updated_at " +
                 "FROM AccountTbl a " +
                 "LEFT JOIN AccountAddressTbl addr ON a.AccountID = addr.AccountID " +
                 "ORDER BY a.created_at DESC";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
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
        String sql = "SELECT a.AccountID, a.FirstName, a.MiddleName, a.LastName, a.BirthDate, a.Email, a.PhoneNumber, " +
                 "addr.Barangay, addr.City, addr.Province, a.created_at, a.updated_at " +
                 "FROM AccountTbl a " +
                 "LEFT JOIN AccountAddressTbl addr ON a.AccountID = addr.AccountID " +
                 "WHERE a.AccountID = ?";
                 
        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        String sql = "SELECT a.AccountID, a.FirstName, a.MiddleName, a.LastName, a.BirthDate, a.Email, a.PhoneNumber, " +
                 "addr.Barangay, addr.City, addr.Province, a.created_at, a.updated_at " +
                 "FROM AccountTbl a " +
                 "LEFT JOIN AccountAddressTbl addr ON a.AccountID = addr.AccountID " +
                 "WHERE a.AccountID = ?";
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
     * Add new account to database with address
     * @param account Account object to add
     * @return true if successful, false otherwise
     */
    public boolean addAccount(Account account) {
        // Check if email already exists
        if (isEmailExists(account.getEmail(), null)) {
            System.err.println("Account with email " + account.getEmail() + " already exists");
            return false;
        }
        
        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_ACCOUNT, Statement.RETURN_GENERATED_KEYS)) {
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
                            int internalId = generatedKeys.getInt(1);
                            account.setInternalId(internalId);
                            String formattedId = formatId("ACC-", 1000000, internalId);
                            account.setId(formattedId);
                        }
                    }
                    
                    // Add address if city and province are provided
                    if (account.getCity() != null && !account.getCity().isEmpty() && 
                        account.getProvince() != null && !account.getProvince().isEmpty()) {
                        boolean addressSuccess = addOrUpdateAccountAddress(account.getId(), 
                                account.getBarangay(), account.getCity(), account.getProvince());
                        
                        if (!addressSuccess) {
                            conn.rollback();
                            System.err.println("Failed to add address for account: " + account.getId());
                            return false;
                        }
                    }
                    
                    conn.commit();
                    System.out.println("Account added successfully with ID: " + account.getId());
                    return true;
                } else {
                    conn.rollback();
                }
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding account: " + e.getMessage());
            e.printStackTrace();
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
        
        return false;
    }

    /**
     * Updates account information
     * @param account Account object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateAccount(Account account) {
        Connection conn = null;
        boolean success = false;
        
        try {
            // Get connection outside the try-with-resources to control when it's closed
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // 1. Update account basic info
            String accountSql = "UPDATE AccountTbl SET FirstName = ?, MiddleName = ?, LastName = ?, "
                             + "BirthDate = ?, Email = ?, PhoneNumber = ? WHERE AccountID = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(accountSql)) {
                stmt.setString(1, account.getFirstName());
                stmt.setString(2, account.getMiddleName());
                stmt.setString(3, account.getLastName());
                stmt.setString(4, account.getBirthday());
                stmt.setString(5, account.getEmail());
                stmt.setString(6, account.getPhoneNumber());
                stmt.setString(7, account.getId());
                
                int accountRows = stmt.executeUpdate();
                
                // 2. Handle address update - First check if address exists
                String checkAddressSql = "SELECT COUNT(*) FROM AccountAddressTbl WHERE AccountID = ?";
                boolean addressExists = false;
                
                try (PreparedStatement checkStmt = conn.prepareStatement(checkAddressSql)) {
                    checkStmt.setString(1, account.getId());
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        addressExists = true;
                    }
                }
                
                int addressRows = 0;
                // Update or insert address based on whether it exists
                if (addressExists) {
                    String updateAddressSql = "UPDATE AccountAddressTbl SET Barangay = ?, City = ?, Province = ? "
                                          + "WHERE AccountID = ?";
                    try (PreparedStatement addrStmt = conn.prepareStatement(updateAddressSql)) {
                        addrStmt.setString(1, account.getBarangay());
                        addrStmt.setString(2, account.getCity());
                        addrStmt.setString(3, account.getProvince());
                        addrStmt.setString(4, account.getId());
                        addressRows = addrStmt.executeUpdate();
                    }
                } else if (account.getBarangay() != null && !account.getBarangay().isEmpty()) {
                    // Only insert address if at least Barangay is provided
                    String insertAddressSql = "INSERT INTO AccountAddressTbl (AccountID, Barangay, City, Province) "
                                          + "VALUES (?, ?, ?, ?)";
                    try (PreparedStatement addrStmt = conn.prepareStatement(insertAddressSql)) {
                        addrStmt.setString(1, account.getId());
                        addrStmt.setString(2, account.getBarangay());
                        addrStmt.setString(3, account.getCity());
                        addrStmt.setString(4, account.getProvince());
                        addressRows = addrStmt.executeUpdate();
                    }
                }
                
                // Commit transaction if we updated the account
                if (accountRows > 0) {
                    conn.commit();
                    System.out.println("Account updated successfully: " + account.getId());
                    success = true;
                } else {
                    conn.rollback();
                    System.err.println("No rows affected when updating account: " + account.getId());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating account: " + e.getMessage());
            e.printStackTrace();
            // Attempt rollback, but don't throw if it fails
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
        } finally {
            // Close connection in finally block
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);  // Reset auto-commit
                    conn.close();              // Close connection
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
        
        return success;
    }

    /**
     * Delete account and associated address by ID
     * @param accountId Account ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteAccount(String accountId) {
        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            try {
                // Delete address first (due to foreign key constraints)
                boolean addressDeleted = deleteAccountAddress(accountId);
                System.out.println("Address deletion attempted for: " + accountId);
                
                // Now delete account
                try (PreparedStatement stmt = conn.prepareStatement(DELETE_ACCOUNT)) {
                    stmt.setString(1, accountId);
                    int rowsAffected = stmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        conn.commit();
                        System.out.println("Account deleted successfully: " + accountId);
                        return true;
                    } else {
                        System.err.println("No account found with ID: " + accountId);
                        conn.rollback();
                        return false;
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting account: " + e.getMessage());
            e.printStackTrace();
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
            return getAllAccounts(); // Return all accounts if no keyword is provided
        }

        String searchQuery = "SELECT a.AccountID, a.FirstName, a.MiddleName, a.LastName, a.BirthDate, a.Email, a.PhoneNumber, " +
                            "addr.Barangay, addr.City, addr.Province, a.created_at, a.updated_at " +
                            "FROM AccountTbl a " +
                            "LEFT JOIN AccountAddressTbl addr ON a.AccountID = addr.AccountID " +
                            "WHERE a.FirstName LIKE ? OR a.MiddleName LIKE ? OR a.LastName LIKE ? OR a.Email LIKE ? " +
                            "OR a.PhoneNumber LIKE ? OR addr.City LIKE ? OR addr.Province LIKE ? " +
                            "ORDER BY a.created_at DESC";

        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(searchQuery)) {

            // Set the same keyword for all placeholders
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern); // FirstName
            stmt.setString(2, searchPattern); // MiddleName
            stmt.setString(3, searchPattern); // LastName
            stmt.setString(4, searchPattern); // Email
            stmt.setString(5, searchPattern); // PhoneNumber
            stmt.setString(6, searchPattern); // City
            stmt.setString(7, searchPattern); // Province

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));
                }
            }
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
     * Create or update address record for an account
     * @param accountId The account ID
     * @param barangay The barangay
     * @param city The city
     * @param province The province
     * @return true if successful, false otherwise
     */
    public boolean addOrUpdateAccountAddress(String accountId, String barangay, String city, String province) {
        try (Connection conn = DBConnector.getConnection()) {
            // First check if address record already exists
            String checkSql = "SELECT AccountAddressID FROM AccountAddressTbl WHERE AccountID = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, accountId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        // Address exists, update it
                        String updateSql = "UPDATE AccountAddressTbl SET Barangay = ?, City = ?, Province = ? WHERE AccountID = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, barangay);
                            updateStmt.setString(2, city);
                            updateStmt.setString(3, province);
                            updateStmt.setString(4, accountId);
                            
                            int rows = updateStmt.executeUpdate();
                            return rows > 0;
                        }
                    } else {
                        // Address doesn't exist, create it
                        // Generate AccountAddressID with format "AADD-1000001"
                        String nextIdSql = "SELECT MAX(InternalID) as maxId FROM AccountAddressTbl";
                        int nextId = 1;
                        
                        try (PreparedStatement idStmt = conn.prepareStatement(nextIdSql);
                            ResultSet idRs = idStmt.executeQuery()) {
                            if (idRs.next()) {
                                nextId = (idRs.getObject("maxId") != null) ? idRs.getInt("maxId") + 1 : 1;
                            }
                        }
                        
                        String addressId = formatId("AADD-", 1000000, nextId);
                        
                        // Insert new address
                        String insertSql = "INSERT INTO AccountAddressTbl (AccountAddressID, InternalID, Barangay, City, Province, AccountID) VALUES (?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setString(1, addressId);
                            insertStmt.setInt(2, nextId);
                            insertStmt.setString(3, barangay);
                            insertStmt.setString(4, city);
                            insertStmt.setString(5, province);
                            insertStmt.setString(6, accountId);
                            
                            int rows = insertStmt.executeUpdate();
                            return rows > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error handling account address: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete address for an account
     * @param accountId The account ID
     * @return true if successful, false otherwise
     */
    public boolean deleteAccountAddress(String accountId) {
        try (Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM AccountAddressTbl WHERE AccountID = ?")) {
            
            stmt.setString(1, accountId);
            int rows = stmt.executeUpdate();
            return rows >= 0; // Return true even if no rows deleted (address might not exist)
            
        } catch (SQLException e) {
            System.err.println("Error deleting account address: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
        /**
     * Map ResultSet to Account object - Updated with FullName and Address
     * @param rs ResultSet from database query
     * @return Account object
     * @throws SQLException if database error occurs
     */
    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();

        // Handle AccountID
        String accountIdStr = rs.getString("AccountID");
        if (accountIdStr != null) {
            if (accountIdStr.startsWith("ACC-")) {
                account.setId(accountIdStr);
            } else {
                int internalId = rs.getInt("AccountID");
                account.setInternalId(internalId);
                account.setId(formatId("ACC-", 1000000, internalId));
            }
        }

        // Set basic fields
        account.setFirstName(rs.getString("FirstName"));
        account.setMiddleName(rs.getString("MiddleName"));
        account.setLastName(rs.getString("LastName"));
        account.setBirthday(rs.getString("BirthDate"));
        account.setEmail(rs.getString("Email"));
        account.setPhoneNumber(rs.getString("PhoneNumber"));

        // Set address fields
        account.setBarangay(rs.getString("Barangay"));
        account.setCity(rs.getString("City"));
        account.setProvince(rs.getString("Province"));

        // Concatenate Address
        String address = "";
        if (rs.getString("Barangay") != null && !rs.getString("Barangay").isEmpty()) {
            address += rs.getString("Barangay") + ", ";
        }
        address += rs.getString("City") + ", " + rs.getString("Province");
        account.setAddress(address);

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