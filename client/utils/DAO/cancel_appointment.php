<?php
session_start();
require_once '../Connection/db_connection.php';

if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['appointment_id'])) {
    $appointmentId = (int)$_POST['appointment_id'];
    
    try {
        $conn = create_connection();
        
        // Update appointment status to cancelled
        $stmt = $conn->prepare("UPDATE appointment_tbl SET status = 'cancelled' WHERE appointment_id = ?");
        $stmt->bind_param("i", $appointmentId);
        
        if ($stmt->execute()) {
            $_SESSION['message'] = "Appointment cancelled successfully.";
            header("Location: ../schedule_appointment.php");
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
    header("Location: ../schedule_appointment.php");
}
exit();
?>