<?php
    // Start session
    session_start();

    // Example hardcoded credentials (replace with your own authentication logic)
    $valid_username = "admin";
    $valid_password = "password123";

    $error = "";

    if ($_SERVER["REQUEST_METHOD"] == "POST") {
        $username = $_POST["username"] ?? "";
        $password = $_POST["password"] ?? "";

        if ($username === $valid_username && $password === $valid_password) {
            $_SESSION["username"] = $username;
            header("Location: ../index.php"); // Redirect after successful login
            
            exit();
        } else {
            // Redirect back to login.html with error message
            header("Location: ../components/login.html?error=Invalid+username+or+password");
            exit();
        }
    }
?>
