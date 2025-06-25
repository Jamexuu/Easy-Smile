<?php
session_start();
require_once '../Connection/db_connection.php';

if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['appointment_id'])) {
    $appointmentId = $_POST['appointment_id'];
    
    try {
        $conn = create_connection();
        
        // Update appointment status to cancelled - NOTE: Fixed case to 'Canceled' to match your DB
        $stmt = $conn->prepare("UPDATE AppointmentTbl SET Status = 'Canceled' WHERE AppointmentID = ?");
        $stmt->bind_param("s", $appointmentId);
        
        if ($stmt->execute()) {
            $_SESSION['success'] = "Appointment cancelled successfully.";
            // Change redirect to homepage instead of schedule_appointment.php
            header("Location: ../../index.php");
        } else {
            $_SESSION['error'] = "Failed to cancel appointment.";
            header("Location: ../view_appointment.php?id=" . $appointmentId);
        }
        
        $stmt->close();
        $conn->close();
        
    } catch (Exception $e) {
        $_SESSION['error'] = "Database error: " . $e->getMessage();
        header("Location: ../view_appointment.php?id=" . $appointmentId);
    }
} else {
    header("Location: /index.php");
}
exit();
?>