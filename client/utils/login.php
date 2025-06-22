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
            
            // Check in AccountTbl first (for patients)
            $stmt = $conn->prepare("SELECT AccountID, InternalID, FirstName, MiddleName, LastName, Email, Password 
                                  FROM AccountTbl WHERE Email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $result = $stmt->get_result();
            
            if ($result->num_rows > 0) {
                $user = $result->fetch_assoc();
                
                // Verify password (allows both hashed and plain text for compatibility)
                if (password_verify($password, $user['Password']) || $password === $user['Password']) {
                    // Successful login
                    $_SESSION["user_id"] = $user['AccountID'];
                    $_SESSION["internal_id"] = $user['InternalID'];
                    $_SESSION["user_email"] = $user['Email'];
                    
                    // Create full name based on available fields
                    $fullName = $user['FirstName'];
                    if (!empty($user['MiddleName'])) {
                        $fullName .= ' ' . $user['MiddleName'];
                    }
                    $fullName .= ' ' . $user['LastName'];
                    
                    $_SESSION["user_name"] = $fullName;
                    $_SESSION["user_type"] = "patient"; // Default to patient
                    
                    $stmt->close();
                    $conn->close();
                    
                    // Redirect to index.php outside client folder
                    header("Location: ../../index.php");
                    exit();
                }
            }
            
            $stmt->close();
            
            // Check AdminTbl for administrators
            $stmt = $conn->prepare("SELECT AdminID, InternalID, Password FROM AdminTbl WHERE AdminID = ?");
            $stmt->bind_param("s", $email); // Assuming email is used as AdminID
            $stmt->execute();
            $result = $stmt->get_result();
            
            if ($result->num_rows > 0) {
                $admin = $result->fetch_assoc();
                
                // For admin users, check password
                if (password_verify($password, $admin['Password']) || $password === $admin['Password']) {
                    // Successful admin login
                    $_SESSION["user_id"] = $admin['AdminID'];
                    $_SESSION["internal_id"] = $admin['InternalID'];
                    $_SESSION["user_email"] = $admin['AdminID']; // Using AdminID as email
                    $_SESSION["user_name"] = "Administrator"; // Generic admin name
                    $_SESSION["user_type"] = "admin";
                    
                    $stmt->close();
                    $conn->close();
                    
                    // Redirect to admin dashboard
                    header("Location: ../../admin/dashboard.php");
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
            echo "error=Login+error:+" . urlencode($e->getMessage());
            exit();
        }
        
    } else {
        http_response_code(405);
        echo "error=Method+not+allowed";
        exit();
    }
?>