<?php
    function create_connection(){
        $server = "localhost";
        $username = "root";
        $password = "";
        $database = "appdb";

        try{
            $conn = new mysqli($server, $username, $password, $database);
            return $conn;
        } catch(mysqli_sql_exception $e){
            die("Connection failed: " . $e->getMessage());
        }
    }
?>