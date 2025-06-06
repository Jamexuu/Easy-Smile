<?php
require_once '../Connection/db_connection.php';
session_start();

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Get form data
    $firstName = trim($_POST['firstName']);
    $middleName = trim($_POST['middleName'] ?? '');
    $lastName = trim($_POST['lastName']);
    $birthDate = $_POST['birthDate']; // Already in YYYY-MM-DD format from date input
    $gender = $_POST['gender'] ?? null;
    $purposeOfVisit = $_POST['purposeOfVisit'];
    $appointmentDate = $_POST['appointmentDate']; // Already in YYYY-MM-DD format
    $appointmentTime = $_POST['appointmentTime']; // In HH:MM format
    $additionalNotes = trim($_POST['additionalNotes'] ?? '');
    
    // Get email and phone from session (since user is registered)
    $email = $_SESSION['user_email'] ?? '';
    $phone = ''; // You might want to get this from the database based on user session
    
    // If we need phone from database, fetch it
    if (empty($phone) && !empty($email)) {
        try {
            $conn = create_connection();
            $stmt = $conn->prepare("SELECT phone FROM patients_tbl WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $result = $stmt->get_result();
            if ($result->num_rows > 0) {
                $user = $result->fetch_assoc();
                $phone = $user['phone'];
            }
            $stmt->close();
            $conn->close();
        } catch (Exception $e) {
            // Handle error silently or log it
            error_log("Error fetching phone: " . $e->getMessage());
        }
    }
    
    // Basic validation
    $errors = [];
    
    if (empty($firstName) || empty($lastName) || empty($purposeOfVisit)) {
        $errors[] = "Please fill in all required fields";
    }
    
    if (empty($email)) {
        $errors[] = "User session expired. Please login again.";
    }
    
    // Validate birth date
    if (empty($birthDate) || strtotime($birthDate) >= strtotime('today')) {
        $errors[] = "Please enter a valid birth date";
    }
    
    // Validate appointment date is in the future
    if (empty($appointmentDate) || strtotime($appointmentDate) <= strtotime('today')) {
        $errors[] = "Appointment date must be in the future";
    }
    
    // Validate appointment time
    if (empty($appointmentTime)) {
        $errors[] = "Please select an appointment time";
    }
    
    // Check if appointment date is a weekend
    $dayOfWeek = date('w', strtotime($appointmentDate));
    if ($dayOfWeek == 0 || $dayOfWeek == 6) {
        $errors[] = "Appointments are only available on weekdays (Monday-Friday)";
    }
    
    if (empty($errors)) {
        try {
            $conn = create_connection();
            
            // Check if appointment slot is already taken
            $checkSlot = $conn->prepare("SELECT appointment_id FROM appointment_tbl WHERE appointment_date = ? AND appointment_time = ? AND status != 'cancelled'");
            $checkSlot->bind_param("ss", $appointmentDate, $appointmentTime);
            $checkSlot->execute();
            $slotResult = $checkSlot->get_result();
            
            if ($slotResult->num_rows > 0) {
                $errors[] = "Sorry, this appointment slot is already taken. Please choose a different date or time.";
                $checkSlot->close();
                $conn->close();
                
                // Redirect immediately when slot is taken
                $_SESSION['errors'] = $errors;
                $_SESSION['form_data'] = $_POST;
                header("Location: ../schedule_appointment.php");
                exit();
            } else {
                // Create appointment datetime
                $appointmentDateTime = $appointmentDate . ' ' . $appointmentTime . ':00';
                
                // Insert appointment
                $stmt = $conn->prepare("INSERT INTO appointment_tbl (ptnt_fname, ptnt_mname, ptnt_lname, ptnt_birth_date, ptnt_gender, purps_vst, appointment_date, appointment_time, appointment_datetime, additional_notes, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'pending')");
                $stmt->bind_param("ssssssssss", $firstName, $middleName, $lastName, $birthDate, $gender, $purposeOfVisit, $appointmentDate, $appointmentTime, $appointmentDateTime, $additionalNotes);
                
                if ($stmt->execute()) {
                    // Get the appointment ID
                    $appointmentId = $conn->insert_id;
                    
                    $_SESSION['appointment_success'] = true;
                    $_SESSION['new_appointment_id'] = $appointmentId;
                    
                    $stmt->close();
                    $checkSlot->close();
                    $conn->close();
                    
                    // Redirect to view appointment page
                    header("Location: ../view_appointment.php?id=" . $appointmentId);
                    exit();
                } else {
                    $errors[] = "Failed to book appointment. Please try again.";
                    $errors[] = "SQL Error: " . $conn->error;
                    $errors[] = "SQL Error Number: " . $conn->errno;
                    $stmt->close();
                    $checkSlot->close();
                    $conn->close();
                }
            }
        } catch (Exception $e) {
            $errors[] = "Database error: " . $e->getMessage();
            $errors[] = "Error details: " . $e->getFile() . " on line " . $e->getLine();
        }
    }
    
    // Only display errors if there are any
    if (!empty($errors)) {
        echo "<h2>Errors occurred:</h2>";
        echo "<ul>";
        foreach ($errors as $error) {
            echo "<li>" . htmlspecialchars($error) . "</li>";
        }
        echo "</ul>";
        
        echo "<h3>Debug Information:</h3>";
        echo "<p><strong>First Name:</strong> " . htmlspecialchars($firstName) . "</p>";
        echo "<p><strong>Last Name:</strong> " . htmlspecialchars($lastName) . "</p>";
        echo "<p><strong>Email:</strong> " . htmlspecialchars($email) . "</p>";
        echo "<p><strong>Phone:</strong> " . htmlspecialchars($phone) . "</p>";
        echo "<p><strong>Birth Date:</strong> " . htmlspecialchars($birthDate) . "</p>";
        echo "<p><strong>Appointment Date:</strong> " . htmlspecialchars($appointmentDate) . "</p>";
        echo "<p><strong>Appointment Time:</strong> " . htmlspecialchars($appointmentTime) . "</p>";
        echo "<p><strong>Purpose of Visit:</strong> " . htmlspecialchars($purposeOfVisit) . "</p>";
        
        echo "<br><a href='../schedule_appointment.php'>← Back to Form</a>";
        exit();
    }
    
} else {
    echo "<h2>Error: Invalid Request Method</h2>";
    echo "<p>This page only accepts POST requests.</p>";
    echo "<br><a href='../schedule_appointment.php'>← Back to Form</a>";
    exit();
}
?>