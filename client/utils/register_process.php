<?php
require_once 'Connection/db_connection.php';

// Start session for error/success messages
session_start();

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Get and sanitize form data
    $firstName = trim($_POST['firstName']);
    $middleName = trim($_POST['middleName']);
    $lastName = trim($_POST['lastName']);
    $birthDate = $_POST['birthDate'];
    $gender = $_POST['gender'] ?? null;
    $phone = trim($_POST['phone']);
    $email = trim($_POST['email']);
    $password = $_POST['password'];
    $confirmPassword = $_POST['confirmPassword'];
    
    // Validation
    $errors = [];
    
    // Check if passwords match
    if ($password !== $confirmPassword) {
        $errors[] = "Passwords do not match";
    }
    
    // Check password strength (minimum 6 characters)
    if (strlen($password) < 6) {
        $errors[] = "Password must be at least 6 characters long";
    }
    
    // Validate email format
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $errors[] = "Invalid email format";
    }
    
    // Validate birth date format (MM/DD/YYYY)
    if (!preg_match('/^\d{2}\/\d{2}\/\d{4}$/', $birthDate)) {
        $errors[] = "Birth date must be in MM/DD/YYYY format";
    } else {
        // Convert to MySQL date format (YYYY-MM-DD)
        $birthDateArray = explode('/', $birthDate);
        $birthDate = $birthDateArray[2] . '-' . $birthDateArray[0] . '-' . $birthDateArray[1];
    }
    
    if (empty($errors)) {
        try {
            $conn = create_connection();
            
            // Check if email already exists
            $checkEmail = $conn->prepare("SELECT user_id FROM patients_tbl WHERE email = ?");
            $checkEmail->bind_param("s", $email);
            $checkEmail->execute();
            $result = $checkEmail->get_result();
            
            if ($result->num_rows > 0) {
                $errors[] = "Email already exists";
            } else {
                // Hash password for security
                $hashedPassword = password_hash($password, PASSWORD_DEFAULT);
                
                // Insert new patient
                $stmt = $conn->prepare("INSERT INTO patients_tbl (first_name, last_name, birth_date, gender, phone, email, password, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())");
                $stmt->bind_param("sssssss", $firstName, $lastName, $birthDate, $gender, $phone, $email, $hashedPassword);
                
                if ($stmt->execute()) {
                    $_SESSION['success'] = "Registration successful! You can now login.";
                    header("Location: ../../index.php?registration=success");
                    exit();
                } else {
                    $errors[] = "Registration failed. Please try again.";
                }
                
                $stmt->close();
            }
            
            $checkEmail->close();
            $conn->close();
            
        } catch (Exception $e) {
            $errors[] = "Database error: " . $e->getMessage();
        }
    }
    
    // If there are errors, redirect back with error messages
    if (!empty($errors)) {
        $_SESSION['errors'] = $errors;
        $_SESSION['form_data'] = $_POST; // Preserve form data
        header("Location: create_new_account.php");
        exit();
    }
} else {
    // Redirect if accessed directly
    header("Location: create_new_account.php");
    exit();
}
?>