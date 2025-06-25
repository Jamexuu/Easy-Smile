<?php
require_once 'Connection/db_connection.php';

// Start session for error/success messages
session_start();

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Get and sanitize form data
    $firstName = trim($_POST['firstName']);
    $middleName = trim($_POST['middleName']); // Fixed - was incorrectly using firstName
    $lastName = trim($_POST['lastName']);
    $birthDate = $_POST['birthDate'];
    $gender = $_POST['gender'] ?? null;
    $phone = trim($_POST['phone']);
    $email = trim($_POST['email']);
    $password = $_POST['password'];
    $confirmPassword = $_POST['confirmPassword'];
    
    // Get address information
    $barangay = trim($_POST['barangay'] ?? ''); // Optional
    $city = trim($_POST['city'] ?? '');
    $province = trim($_POST['province'] ?? '');
    
    // Validation
    $errors = [];
    
    // Basic validations (existing code)
    if ($password !== $confirmPassword) {
        $errors[] = "Passwords do not match";
    }
    
    if (strlen($password) < 6) {
        $errors[] = "Password must be at least 6 characters long";
    }
    
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $errors[] = "Invalid email format";
    }
    
    // Validate address fields
    if (empty($city)) {
        $errors[] = "City is required";
    }
    
    if (empty($province)) {
        $errors[] = "Province is required";
    }

    // Replace the phone validation with:
        $phone = str_replace('-', '', trim($_POST['phone'] ?? ''));
        if (empty($phone)) {
            $errors[] = "Phone number is required";
        } elseif (!preg_match('/^[0-9]{10,15}$/', $phone)) {
            $errors[] = "Phone number must be 10-15 digits";
        }
    
    // Convert birth date format (MM/DD/YYYY to YYYY-MM-DD) if needed
    if (!preg_match('/^\d{2}\/\d{2}\/\d{4}$/', $birthDate)) {
        $errors[] = "Birth date must be in MM/DD/YYYY format";
    } else {
        $birthDateArray = explode('/', $birthDate);
        $birthDate = $birthDateArray[2] . '-' . $birthDateArray[0] . '-' . $birthDateArray[1];
    }
    
    if (empty($errors)) {
        try {
            $conn = create_connection();
            
            // Start transaction to handle both account and address creation
            $conn->begin_transaction();
            
            // Check if email already exists
            $checkEmail = $conn->prepare("SELECT AccountID FROM AccountTbl WHERE Email = ?");
            $checkEmail->bind_param("s", $email);
            $checkEmail->execute();
            $result = $checkEmail->get_result();
            
            if ($result->num_rows > 0) {
                $errors[] = "Email already exists";
                $conn->rollback();
            } else {
                // Generate AccountID with format "ACC-1000001"
                $idStmt = $conn->prepare("SELECT MAX(InternalID) as maxId FROM AccountTbl");
                $idStmt->execute();
                $idResult = $idStmt->get_result();
                $idRow = $idResult->fetch_assoc();
                $nextId = ($idRow['maxId'] ?? 0) + 1;
                $accountId = formatId("ACC-", 1000000, $nextId);
                $idStmt->close();
                
                // Hash password for security
                $hashedPassword = password_hash($password, PASSWORD_DEFAULT);
                
                do {
                    $checkAccountId = $conn->prepare("SELECT AccountID FROM AccountTbl WHERE AccountID = ?");
                    $checkAccountId->bind_param("s", $accountId);
                    $checkAccountId->execute();
                    $result = $checkAccountId->get_result();

                    if ($result->num_rows > 0) {
                        // Increment InternalID and regenerate AccountID
                        $nextId++;
                        $accountId = formatId("ACC-", 1000000, $nextId);
                    } else {
                        break; // No duplicate found
                    }

                    $checkAccountId->close();
                } while (true);

                // Insert new account
                $stmt = $conn->prepare("INSERT INTO AccountTbl (AccountID, InternalID, FirstName, MiddleName, LastName, BirthDate, Gender, Email, PhoneNumber, Password, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())");
                $stmt->bind_param("sissssssss", $accountId, $nextId, $firstName, $middleName, $lastName, $birthDate, $gender, $email, $phone, $hashedPassword);
                
                if ($stmt->execute()) {
                    // Now create address record
                    $addressId = createAddressRecord($conn, $barangay, $city, $province, $accountId);
                    
                    // If address creation was successful
                    if ($addressId) {
                        // Commit the transaction
                        $conn->commit();
                        
                        // Set session variables
                        $_SESSION['user_id'] = $accountId;
                        $_SESSION['user_name'] = $firstName . ' ' . $lastName;
                        $_SESSION['user_email'] = $email;
                        $_SESSION['success'] = "Registration successful!";
                        
                        // Redirect to success page
                        header("Location: ../../index.php?registration=success");
                        exit();
                    } else {
                        $errors[] = "Failed to create address record";
                        $conn->rollback();
                    }
                } else {
                    $errors[] = "Registration failed. Please try again.";
                    $conn->rollback();
                }
                
                $stmt->close();
            }
            
            $checkEmail->close();
            $conn->close();
            
        } catch (Exception $e) {
            if (isset($conn) && $conn->connect_errno === 0) {
                $conn->rollback();
            }
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

/**
 * Create address record in AccountAddressTbl
 * @return string|bool Returns the AccountAddressID if successful, false otherwise
 */
function createAddressRecord($conn, $barangay, $city, $province, $accountId) {
    // Generate AddressID with format "AADD-1000001"
    $stmt = $conn->prepare("SELECT MAX(InternalID) as maxId FROM AccountAddressTbl");
    $stmt->execute();
    $result = $stmt->get_result();
    $row = $result->fetch_assoc();
    $nextId = ($row['maxId'] ?? 0) + 1;
    $addressId = formatId("AADD-", 1000000, $nextId);
    $stmt->close();
    
    // Check for duplicate AddressID
    do {
        $checkAddressId = $conn->prepare("SELECT AccountAddressID FROM AccountAddressTbl WHERE AccountAddressID = ?");
        $checkAddressId->bind_param("s", $addressId);
        $checkAddressId->execute();
        $result = $checkAddressId->get_result();

        if ($result->num_rows > 0) {
            // Increment InternalID and regenerate AddressID
            $nextId++;
            $addressId = formatId("AADD-", 1000000, $nextId);
        } else {
            break; // No duplicate found
        }

        $checkAddressId->close();
    } while (true);

    // Insert into AccountAddressTbl
    $stmt = $conn->prepare("INSERT INTO AccountAddressTbl (AccountAddressID, InternalID, Barangay, City, Province, AccountID) VALUES (?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("sissss", $addressId, $nextId, $barangay, $city, $province, $accountId);
    
    if ($stmt->execute()) {
        $stmt->close();
        return $addressId;
    } else {
        $stmt->close();
        return false;
    }
}

/**
 * Format ID with prefix and zero-padded number
 */
function formatId($prefix, $base, $id) {
    return $prefix . str_pad($base + $id - 1, 7, '0', STR_PAD_LEFT);
}
?>