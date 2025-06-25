<?php
session_start();
require_once 'Connection/db_connection.php';

// Create connection using db_connection.php
$conn = create_connection();

// Display success/error messages
if (isset($_SESSION['success'])) {
    $successMessage = $_SESSION['success'];
    unset($_SESSION['success']);
}

if (isset($_SESSION['error'])) {
    $errorMessage = $_SESSION['error'];
    unset($_SESSION['error']);
}

// Check connection
if (!$conn) {
    die("Connection failed: Unable to connect to the database.");
}

// Get AppointmentID from the URL
$appointmentId = $_GET['id'] ?? null;

if (!$appointmentId) {
    die("No appointment ID provided.");
}

// Fetch appointment details
$sql = "SELECT a.AppointmentID, a.AppointmentDate, a.AppointmentTime, a.Status, 
        s.ServiceName, p.FirstName, p.LastName, p.MiddleName, p.BirthDate, 
        p.Gender, p.PhoneNumber, pa.Barangay, pa.City, pa.Province
        FROM AppointmentTbl a
        INNER JOIN ServicesTbl s ON a.ServiceID = s.ServiceID
        INNER JOIN PatientTbl p ON a.PatientID = p.PatientID
        INNER JOIN PatientAddressTbl pa ON p.PatientID = pa.PatientID
        WHERE a.AppointmentID = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $appointmentId);
$stmt->execute();
$result = $stmt->get_result();

if ($result && $result->num_rows > 0) {
    $row = $result->fetch_assoc();
    $appointmentDate = htmlspecialchars($row['AppointmentDate']);
    $appointmentTime = htmlspecialchars($row['AppointmentTime']);
    $status = htmlspecialchars($row['Status']);
    $serviceName = htmlspecialchars($row['ServiceName']);
    $firstName = htmlspecialchars($row['FirstName']);
    $lastName = htmlspecialchars($row['LastName']);
    $middleName = htmlspecialchars($row['MiddleName']);
    $birthDate = htmlspecialchars($row['BirthDate']);
    $gender = htmlspecialchars($row['Gender']);
    $phone = htmlspecialchars($row['PhoneNumber']);
    $barangay = htmlspecialchars($row['Barangay']);
    $city = htmlspecialchars($row['City']);
    $province = htmlspecialchars($row['Province']);
} else {
    die("No appointment found with the provided ID.");
}

$stmt->close();
$conn->close();
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>View Appointment</title>
    <link rel="stylesheet" href="css/view_appointment.css"> <!-- Link the CSS file -->
    <style>
        body {
            background-color: #f9f9f9;
            margin: 0;
            font-family: 'Segoe UI', sans-serif;
            color: #333;
        }

        .logo-container {
            text-align: center;
            margin-top: 20px;
            margin-bottom: 20px;
        }

        .logo-container img {
            max-width: 300px;
        }

        h1, h2 {
            text-align: center;
            color: #1976d2;
        }

        h1 {
            font-size: 2em;
            margin-bottom: 20px;
        }

        h2 {
            font-size: 1.5em;
            margin-top: 30px;
            margin-bottom: 15px;
        }

        .container {
            display: flex;
            flex-direction: column;
            align-items: center;
            margin: 0 auto;
            padding: 20px;
            max-width: 800px;
        }

        .section {
            width: 100%;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            padding: 20px;
            margin-bottom: 20px;
        }

        p {
            font-size: 1em;
            margin-bottom: 10px;
            line-height: 1.5;
        }

        p strong {
            font-weight: bold;
            color: #222;
        }

        footer {
            text-align: center;
            margin-top: 40px;
            font-size: 0.9em;
            color: #888;
        }

        .homepage-link {
            display: block;
            text-align: center;
            margin-top: 20px;
            font-size: 1em;
            color: #1976d2;
            text-decoration: none;
            font-weight: bold;
        }

        .homepage-link:hover {
            text-decoration: underline;
        }

        .message {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 8px;
            text-align: center;
            width: 100%;
        }
        
        .success-message {
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
            color: #155724;
        }
        
        .error-message {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
        }
        
        .status-badge {
            display: inline-block;
            padding: 6px 12px;
            font-weight: bold;
            border-radius: 20px;
        }

        .status-upcoming {
            background-color: #cfe8ff;
            color: #0256c2;
        }
        
        .status-completed {
            background-color: #d4edda;
            color: #155724;
        }
        
        .status-canceled {
            background-color: #f8d7da;
            color: #721c24;
        }
        
        .cancel-btn {
            background-color: #dc3545;
            color: white;
            border: none;
            border-radius: 4px;
            padding: 10px 20px;
            cursor: pointer;
            font-weight: bold;
            margin-top: 15px;
            transition: background-color 0.3s;
        }

        .cancel-btn:hover {
            background-color: #c82333;
        }
        
        .action-buttons {
            margin-top: 20px;
            text-align: center;
        }
    </style>
</head>
<body>
    <!-- Logo Section -->
    <div class="logo-container">
        <img src="../static/images/EasySmileLogo.png" alt="Easy Smile Logo"> <!-- Corrected path to logo -->
    </div>

    <!-- Include Header -->
    <?php include '../components/header.html'; ?>

    <div class="container">
        <?php if (isset($successMessage)): ?>
            <div class="message success-message"><?php echo $successMessage; ?></div>
        <?php endif; ?>
        
        <?php if (isset($errorMessage)): ?>
            <div class="message error-message"><?php echo $errorMessage; ?></div>
        <?php endif; ?>

        <h2>Appointment Details</h2>
        <div class="section appointment-section">
            <p><strong>Appointment ID:</strong> <?php echo $appointmentId; ?></p>
            <p><strong>Appointment Date:</strong> <?php echo $appointmentDate; ?></p>
            <p><strong>Appointment Time:</strong> <?php echo $appointmentTime; ?></p>
            <p><strong>Status:</strong> 
                <span class="status-badge status-<?php echo strtolower($status); ?>">
                    <?php echo $status; ?>
                </span>
            </p>
            <p><strong>Service:</strong> <?php echo $serviceName; ?></p>
        </div>

        <h2>Patient Information</h2>
        <div class="section patient-section">
            <p><strong>First Name:</strong> <?php echo $firstName; ?></p>
            <p><strong>Last Name:</strong> <?php echo $lastName; ?></p>
            <p><strong>Middle Name:</strong> <?php echo $middleName; ?></p>
            <p><strong>Birth Date:</strong> <?php echo $birthDate; ?></p>
            <p><strong>Gender:</strong> <?php echo $gender; ?></p>
            <p><strong>Phone Number:</strong> <?php echo $phone; ?></p>
        </div>
        <h2>Address</h2>
        <div class="section address-section">
            <p><strong>Barangay:</strong> <?php echo $barangay; ?></p>
            <p><strong>City:</strong> <?php echo $city; ?></p>
            <p><strong>Province:</strong> <?php echo $province; ?></p>
            <div class="action-buttons">
            <?php if (strtolower($status) === 'upcoming'): ?>
                <form method="post" action="DAO/cancel_appointment.php">
                    <input type="hidden" name="appointment_id" value="<?php echo $appointmentId; ?>">
                    <button type="submit" class="cancel-btn" onclick="return confirm('Are you sure you want to cancel this appointment? This action cannot be undone.');">
                        Cancel Appointment
                    </button>
                </form>
            <?php endif; ?>
            <a href="/index.php" class="homepage-link">Go back to homepage</a>
        </div>
    </div>
    <?php include '../components/footer.html'; ?>
</body>
</html>
