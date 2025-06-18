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
            
            $conn->set_charset("utf8");
            
            return $conn;
            
        } catch(mysqli_sql_exception $e){
            error_log("Database connection failed: " . $e->getMessage());
            return false;
        }
    }
    
    function test_connection(){
        $conn = create_connection();
        if($conn){
            return true;
        }
        return false;
    }
?>