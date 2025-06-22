package DataBase;

import java.sql.*;

public class DBConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/easysmile_db";
    private static final String USER = "root";
    private static final String PASSWORD = "AstaxNoelle22";
    
    private static Connection connection = null;
    
    /**
     * Get database connection
     * @return Connection object or null if failed
     */
    public static Connection getConnection() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create connection if it doesn't exist or is closed
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected successfully!");
            }
            
            return connection;
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test database connection
     * @return true if successful, false otherwise
     */
    public static boolean testConnection() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1");
                if (rs.next()) {
                    System.out.println("Database connection test: SUCCESS");
                    return true;
                }
            } catch (SQLException e) {
                System.err.println("Database test failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }
}