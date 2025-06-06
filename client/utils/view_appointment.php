<?php
session_start();
require_once 'Connection/db_connection.php';

// Get appointment ID from URL
$appointmentId = $_GET['id'] ?? null;

if (!$appointmentId) {
    header("Location: schedule_appointment.php");
    exit();
}

// Fetch appointment data from database
try {
    $conn = create_connection();
    $stmt = $conn->prepare("SELECT * FROM appointment_tbl WHERE appointment_id = ?");
    $stmt->bind_param("i", $appointmentId);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows === 0) {
        echo "<script>alert('Appointment not found.'); window.location.href='schedule_appointment.php';</script>";
        exit();
    }
    
    $appointment = $result->fetch_assoc();
    $stmt->close();
    $conn->close();
    
    // Format the appointment date
    $appointmentDate = date('F j, l', strtotime($appointment['appointment_date']));
    $appointmentTime = date('g:i A', strtotime($appointment['appointment_time']));
    
} catch (Exception $e) {
    echo "<script>alert('Database error: " . $e->getMessage() . "'); window.location.href='schedule_appointment.php';</script>";
    exit();
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Appointment - Easy Smile Dental Clinic</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="../css/style.css">
    <link rel="stylesheet" href="../css/view_appointment.css">
</head>
<body>
    <header class="top-header">
        <?php include '../components/header.html'; ?>
    </header>

    <div class="appointment-view-container">
        <h1>Upcoming Appointment</h1>
        
        <div class="appointment-date"><?= htmlspecialchars($appointmentDate) ?></div>
        <div class="appointment-time">Time: <?= htmlspecialchars($appointmentTime) ?></div>
        
        <div class="appointment-details">
            <div class="detail-row">
                <div class="detail-label">First Name:</div>
                <div class="detail-value"><?= htmlspecialchars($appointment['ptnt_fname']) ?></div>
            </div>
            
            <div class="detail-row">
                <div class="detail-label">Middle Name:</div>
                <div class="detail-value"><?= htmlspecialchars($appointment['ptnt_mname'] ?? 'N/A') ?></div>
            </div>
            
            <div class="detail-row">
                <div class="detail-label">Last Name:</div>
                <div class="detail-value"><?= htmlspecialchars($appointment['ptnt_lname']) ?></div>
            </div>
            
            <div class="detail-row">
                <div class="detail-label">Birth Date:</div>
                <div class="detail-value"><?= htmlspecialchars(date('m/d/Y', strtotime($appointment['ptnt_birth_date']))) ?></div>
            </div>
            
            <div class="detail-row">
                <div class="detail-label">Gender:</div>
                <div class="detail-value"><?= htmlspecialchars(ucfirst($appointment['ptnt_gender'] ?? 'Not specified')) ?></div>
            </div>
            
            <div class="detail-row">
                <div class="detail-label">Purpose of Visit:</div>
                <div class="detail-value"><?= htmlspecialchars(ucfirst($appointment['purps_vst'])) ?></div>
            </div>
            
            <?php if (!empty($appointment['additional_notes'])): ?>
            <div class="detail-row">
                <div class="detail-label">Additional Notes:</div>
                <div class="detail-value"><?= htmlspecialchars($appointment['additional_notes']) ?></div>
            </div>
            <?php endif; ?>
            
            <div class="detail-row">
                <div class="detail-label">Status:</div>
                <div class="detail-value status-<?= strtolower($appointment['status']) ?>">
                    <?= htmlspecialchars(ucfirst($appointment['status'])) ?>
                </div>
            </div>
        </div>
        
        <?php if ($appointment['status'] === 'pending'): ?>
        <button class="cancel-button" onclick="cancelAppointment(<?= $appointment['appointment_id'] ?>)">
            Cancel Appointment
        </button>
        <?php endif; ?>
        
        <div class="action-buttons">
            <a href="../../index.php" class="home-btn">← Back to Home</a>
        </div>
    </div>

    <footer class="top-header">
        <?php include '../components/footer.html'; ?>
    </footer>

    <script>
    function cancelAppointment(appointmentId) {
        if (confirm('Are you sure you want to cancel this appointment?')) {
            // Create a form to submit the cancellation
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = 'DAO/cancel_appointment.php';
            
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'appointment_id';
            input.value = appointmentId;
            
            form.appendChild(input);
            document.body.appendChild(form);
            form.submit();
        }
    }
    </script>
</body>
</html>