<?php
    function create_connection(){
        $server = "localhost";
        $username = "root";
        $password = "AstaxNoelle22";
        $database = "easysmile_db";

        try{
            // Enable mysqli exceptions
            mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);
            
            $conn = new mysqli($server, $username, $password, $database);
            
            // Set charset to utf8 for proper character handling
            $conn->set_charset("utf8");
            
            // Remove this echo - it's causing issues with headers
            // echo "Database connected successfully!<br>";
            return $conn;
            
        } catch(mysqli_sql_exception $e){
            error_log("Database connection failed: " . $e->getMessage());
            return false;
        }
    }
    
    // Optional: Function to test the connection
    function test_connection(){
        $conn = create_connection();
        if($conn){
            // Remove this echo too
            // echo "PHP Database connection test: SUCCESS<br>";
            return true;
        }
        return false;
    }
?>