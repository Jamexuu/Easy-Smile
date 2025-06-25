<?php
require_once '../Connection/db_connection.php';
session_start();

ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

// Check if user is logged in
if (empty($_SESSION['user_id'])) {
    $_SESSION['errors'][] = "User session expired. Please login again.";
    header("Location: ../login.php");
    exit();
}

$scheduledBy = $_SESSION['user_id'];

// Check the request method
if ($_SERVER["REQUEST_METHOD"] != "POST") {
    http_response_code(405);
    echo "<h2>Error: Method Not Allowed</h2>";
    echo "<p>This page only accepts POST requests.</p>";
    echo "<br><a href='../schedule_appointment.php'>← Back to Form</a>";
    exit();
}

// Get form data with proper null checks
$firstName = trim($_POST['firstName'] ?? '');
$middleName = trim($_POST['middleName'] ?? '');
$lastName = trim($_POST['lastName'] ?? '');
$birthDate = $_POST['birthDate'] ?? '';
$gender = $_POST['gender'] ?? null;
$phone = trim($_POST['phone'] ?? '');
$barangay = trim($_POST['barangay'] ?? '');
$city = trim($_POST['city'] ?? '');
$province = trim($_POST['province'] ?? '');
$purposeOfVisit = $_POST['purposeOfVisit'] ?? '';
$appointmentDate = $_POST['appointmentDate'] ?? '';
$appointmentTime = $_POST['appointmentTime'] ?? '';

// Basic validation with clearer checks
$errors = [];

if (empty($phone) || !preg_match('/^[0-9]{10,15}$/', $phone)) {
    $errors[] = "A valid phone number (10-15 digits) is required";
}

if (empty($firstName) || empty($lastName) || empty($purposeOfVisit)) {
    $errors[] = "Please fill in all required fields (First Name, Last Name, Purpose of Visit)";
}

if (empty($city) || empty($province)) {
    $errors[] = "Please provide your complete address information (City and Province)";
}

if (empty($birthDate) || !strtotime($birthDate) || strtotime($birthDate) >= strtotime('today')) {
    $errors[] = "Please enter a valid birth date in the past";
}

// Debug output (remove in production)
error_log("Form data received: " . print_r($_POST, true));
error_log("Validation errors: " . print_r($errors, true));

if (empty($errors)) {
    try {
        $conn = create_connection();
        $conn->begin_transaction();

        // Check appointment slot availability
        $checkSlot = $conn->prepare("SELECT AppointmentID FROM AppointmentTbl 
                                   WHERE AppointmentDate = ? AND AppointmentTime = ? AND Status != 'canceled'");
        $checkSlot->bind_param("ss", $appointmentDate, $appointmentTime);
        $checkSlot->execute();
        
        if ($checkSlot->get_result()->num_rows > 0) {
            throw new Exception("This appointment slot is already taken.");
        }

        // Fetch ServiceID based on the service name
        $serviceCheckStmt = $conn->prepare("SELECT ServiceID FROM ServicesTbl WHERE ServiceName = ?");
        $serviceCheckStmt->bind_param("s", $purposeOfVisit);
        $serviceCheckStmt->execute();
        $serviceResult = $serviceCheckStmt->get_result();

        if ($serviceResult->num_rows === 0) {
            throw new Exception("Invalid Service Name: " . $purposeOfVisit);
        }

        $serviceRow = $serviceResult->fetch_assoc();
        $purposeOfVisit = $serviceRow['ServiceID']; // Update $purposeOfVisit to the actual ServiceID

        // Debugging log
        error_log("Mapped Service Name to ServiceID: " . $purposeOfVisit);

        // Create or find patient
        $patientId = createOrFindPatient($conn, $firstName, $middleName, $lastName, 
                                      $birthDate, $gender, $phone, $scheduledBy);
        
        // Store address
        storePatientAddress($conn, $patientId, $barangay, $city, $province);

        // Create appointment
        $appointmentId = generateAppointmentId($conn);
        $datetime = $appointmentDate . ' ' . $appointmentTime . ':00';
        
        $stmt = $conn->prepare("INSERT INTO AppointmentTbl 
                            (AppointmentID, PatientID, ScheduledBy, ServiceID, 
                            AppointmentDate, AppointmentTime, AppointmentDateTime, Status)
                            VALUES (?, ?, ?, ?, ?, ?, ?, 'Upcoming')");
        $stmt->bind_param("sssssss", $appointmentId, $patientId, $scheduledBy, $purposeOfVisit, 
                        $appointmentDate, $appointmentTime, $datetime);

        error_log("ServiceID being used for appointment: " . $purposeOfVisit);

        if ($stmt->execute()) {
            $conn->commit();
            $_SESSION['appointment_success'] = true;
            header("Location: ../view_appointment.php?id=" . $appointmentId);
            exit();
        } else {
            throw new Exception("Failed to create appointment: " . $stmt->error);
        }
    } catch (Exception $e) {
        $conn->rollback();
        $errors[] = $e->getMessage();
        error_log("Database error: " . $e->getMessage());
    }
}

if (!empty($errors)) {
    $_SESSION['errors'] = $errors;
    header("Location: ../schedule_appointment.php");
    exit();
}

/**
 * Create or find patient with correct ID field
 */
function createOrFindPatient($conn, $firstName, $middleName, $lastName, $birthDate, $gender, $phone, $createdBy) {
    // Check if patient already exists
    $stmt = $conn->prepare("SELECT PatientID FROM PatientTbl WHERE CreatedBy = ? AND FirstName = ? AND LastName = ? AND BirthDate = ?");
    $stmt->bind_param("ssss", $createdBy, $firstName, $lastName, $birthDate);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        $stmt->close();
        return $row['PatientID'];
    } else {
        // Generate a unique PatientID
        $patientId = generateNextId($conn, 'PatientTbl', 'PAT-');

        // Insert new patient with PatientID
        $insertStmt = $conn->prepare("INSERT INTO PatientTbl (PatientID, FirstName, MiddleName, LastName, BirthDate, Gender, CreatedBy, PhoneNumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        $insertStmt->bind_param("ssssssss", $patientId, $firstName, $middleName, $lastName, $birthDate, $gender, $createdBy, $phone);

        if (!$insertStmt->execute()) {
            throw new Exception("Failed to create patient record: " . $insertStmt->error);
        }

        $insertStmt->close();
        return $patientId;
    }
}

/**
 * Generate appointment ID
 */
function generateAppointmentId($conn) {
    return generateNextId($conn, 'AppointmentTbl', 'apo-');
}

/**
 * Universal ID generator
 */
function generateNextId($conn, $table, $prefix) {
    // Get max numeric part from existing IDs
    $column = ($table === 'PatientTbl') ? 'PatientID' : 'AppointmentID';
    $stmt = $conn->prepare("SELECT MAX(CAST(SUBSTRING($column, LOCATE('-', $column) + 1) AS UNSIGNED)) AS max_num 
                        FROM $table 
                        WHERE $column LIKE ?");
    $likePrefix = $prefix . '%';
    $stmt->bind_param("s", $likePrefix);
    $stmt->execute();
    $result = $stmt->get_result();
    $row = $result->fetch_assoc();
    
    $nextNum = ($row['max_num'] !== null) ? $row['max_num'] + 1 : 1000001;
    return $prefix . $nextNum;
}


function storePatientAddress($conn, $patientId, $barangay, $city, $province) {
    // Generate the formatted PatientAddressID (PADD-1000001 format)
    $stmt = $conn->prepare("SELECT AUTO_INCREMENT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'PatientAddressTbl' AND TABLE_SCHEMA = DATABASE()");
    $stmt->execute();
    $result = $stmt->get_result();
    $row = $result->fetch_assoc();
    $nextId = $row['AUTO_INCREMENT'];
    $patientAddressId = formatId("PADD-", 1000000, $nextId);

    // Insert the address into the database
    $insertStmt = $conn->prepare("INSERT INTO PatientAddressTbl (PatientAddressID, Barangay, City, Province, PatientID) VALUES (?, ?, ?, ?, ?)");
    $insertStmt->bind_param("sssss", $patientAddressId, $barangay, $city, $province, $patientId);

    if (!$insertStmt->execute()) {
        throw new Exception("Failed to save address: " . $insertStmt->error);
    }

    $insertStmt->close();
    return $patientAddressId;
}

/**
 * Generate the next PatientAddressID (PADD-1000001 format)
 */
function generateNextAddressId($conn) {
    // Get the maximum existing ID number
    $stmt = $conn->prepare("SELECT MAX(CAST(SUBSTRING(PatientAddressID, 6) AS UNSIGNED)) AS max_num 
                        FROM PatientAddressTbl 
                        WHERE PatientAddressID LIKE 'PADD-%'");
    $stmt->execute();
    $result = $stmt->get_result();
    $row = $result->fetch_assoc();
    
    $nextNum = ($row['max_num'] !== null) ? $row['max_num'] + 1 : 1000001;
    return "PADD-" . $nextNum;
}

/**
 * Format ID with prefix and base number
 */
function formatId($prefix, $base, $id) {
    return $prefix . str_pad($base + $id - 1, 7, '0', STR_PAD_LEFT);
}

error_log("CreatedBy value: " . $createdBy);
error_log("Generated PatientID: " . $patientId);
if (empty($purposeOfVisit)) {
    throw new Exception("Purpose of Visit is required and must match a valid ServiceID.");
}


/**
 * Cancels an appointment by updating its status to 'Canceled'
 * 
 * @param object $conn Database connection
 * @param string $appointmentId The ID of the appointment to cancel
 * @param string $userId The ID of the user performing the cancellation (optional)
 * @return bool True if cancellation succeeded, false otherwise
 */
function cancelAppointment($conn, $appointmentId) {
    // Use a direct query with case-insensitive comparison
    $sql = "UPDATE AppointmentTbl SET Status = 'Canceled' 
            WHERE AppointmentID = ? 
            AND LOWER(Status) = 'upcoming'";
            
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $appointmentId);
    $stmt->execute();
    
    if ($stmt->affected_rows > 0) {
        return true;
    } else {
        // Check why update failed
        $checkStmt = $conn->prepare("SELECT Status FROM AppointmentTbl WHERE AppointmentID = ?");
        $checkStmt->bind_param("s", $appointmentId);
        $checkStmt->execute();
        $result = $checkStmt->get_result();
        
        if ($result->num_rows === 0) {
            throw new Exception("Appointment not found");
        } else {
            $status = $result->fetch_assoc()['Status'];
            throw new Exception("Cannot cancel: current status is $status");
        }
    }
}


// Check if this file is being called for an appointment cancellation
if (isset($_POST['action']) && $_POST['action'] === 'cancel_appointment') {
    try {
        if (empty($_POST['appointment_id'])) {
            throw new Exception("No appointment ID provided");
        }
        
        $conn = create_connection();
        $appointmentId = $_POST['appointment_id'];
        
        // Process cancellation
        if (cancelAppointment($conn, $appointmentId)) {
            $_SESSION['success'] = "Appointment successfully canceled";
        } else {
            $_SESSION['error'] = "Failed to cancel appointment";
        }
    }
    catch (Exception $e) {
        $_SESSION['error'] = $e->getMessage();
    }
    
    // Always redirect back to index
    header("Location: /index.php");
    exit();
}