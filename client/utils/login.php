<?php
    require_once 'Connection/db_connection.php';

    // Start session
    session_start();

    if($_SERVER["REQUEST_METHOD"] == "POST") {
        $email = trim($_POST["email"] ?? "");
        $password = $_POST["password"] ?? "";
        
        // Basic validation
        if (empty($email) || empty($password)) {
            http_response_code(400);
            echo "error=Please+enter+both+email+and+password";
            exit();
        }
        
        try {
            $conn = create_connection();
            
            if (!$conn) {
                throw new Exception("Database connection failed");
            }
            
            // Check in patients table first
            $stmt = $conn->prepare("SELECT user_id, first_name, last_name, email, password FROM patients_tbl WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $result = $stmt->get_result();
            
            if ($result->num_rows > 0) {
                $user = $result->fetch_assoc();
                
                // Verify password (for hashed passwords) or direct comparison (for plain text)
                if (password_verify($password, $user['password']) || $password === $user['password']) {
                    // Successful patient login
                    $_SESSION["user_id"] = $user['user_id'];  // Fixed: use user_id from database
                    $_SESSION["user_email"] = $user['email'];
                    $_SESSION["user_name"] = $user['first_name'] . ' ' . $user['last_name'];
                    $_SESSION["user_type"] = "patient";
                    
                    $stmt->close();
                    $conn->close();
                    
                    // Fixed: redirect to index.php outside client folder
                    header("Location: ../../index.php");
                    exit();
                }
            }
            
            $stmt->close();
            
            // Check admin/users table
            $stmt = $conn->prepare("SELECT user_id, email, name, password, role FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $result = $stmt->get_result();
            
            if ($result->num_rows > 0) {
                $user = $result->fetch_assoc();
                
                // For admin users, check password
                if ($password === $user['password'] || password_verify($password, $user['password'])) {
                    // Successful admin login
                    $_SESSION["user_id"] = $user['user_id'];  // Fixed: use user_id from database
                    $_SESSION["user_email"] = $user['email'];
                    $_SESSION["user_name"] = $user['name'];
                    $_SESSION["user_type"] = $user['role'] ?? "admin";
                    
                    $stmt->close();
                    $conn->close();
                    
                    // Fixed: redirect to index.php outside client folder
                    header("Location: ../../index.php");
                    exit();
                }
            }
            
            $stmt->close();
            $conn->close();
            
            // Invalid credentials
            http_response_code(401);
            echo "error=Invalid+email+or+password";
            exit();
            
        } catch (Exception $e) {
            // More detailed error logging
            error_log("Login error: " . $e->getMessage());
            error_log("Login error trace: " . $e->getTraceAsString());
            
            // For debugging - remove this in production
            echo "error=Debug:+" . urlencode($e->getMessage());
            exit();
        }
        
    } else {
        http_response_code(405);
        echo "error=Method+not+allowed";
        exit();
    }
?>