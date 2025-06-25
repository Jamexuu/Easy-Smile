<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

require_once 'client/utils/Connection/db_connection.php';

$appointments = [];
if (isset($_SESSION['user_id'])) {
    $conn = create_connection();
    if ($conn) {
        $userId = $_SESSION['user_id'];
        $sql = "SELECT AppointmentID, AppointmentDate, AppointmentTime 
                FROM AppointmentTbl 
                WHERE ScheduledBy = ? AND AppointmentDate >= CURDATE() AND Status = 'Upcoming' 
                ORDER BY AppointmentDate ASC";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $userId);
        $stmt->execute();
        $result = $stmt->get_result();
        while ($row = $result->fetch_assoc()) {
            $appointments[] = $row;
        }
        $stmt->close();
        $conn->close();
    }
}

?>
<section class="hero">
    
    <?php if (isset($_SESSION['user_name'])): ?>
        <div class="user-info">
            <h2 class="login-heading">Hi, <?= ucwords(htmlspecialchars($_SESSION['user_name'])) ?>!</h2>
            <a href="client/utils/logout.php" class="log-out-link">Log Out</a>
        </div>
        
        <div class="appointment-box">
            <p>Upcoming Appointment:</p>
            <?php if (!empty($appointments)): ?>
                <?php foreach ($appointments as $appointment): ?>
                    <div class="appointment-info">
                        <a href="client/utils/view_appointment.php?id=<?= htmlspecialchars($appointment['AppointmentID']) ?>" class="appointment-link">
                            <p><?= htmlspecialchars(date('F j, l', strtotime($appointment['AppointmentDate']))) ?> at <?= htmlspecialchars($appointment['AppointmentTime']) ?></p>
                        </a>
                    </div>
                <?php endforeach; ?>
            <?php else: ?>
                <p>No upcoming appointments.</p>
            <?php endif; ?>
        </div>
        
    <?php else: ?>
        <!-- Show login and create account when not logged in -->
        <a href="#" onclick="openLoginPopup(); return false;">
            <h2 class="login-heading">Login</h2>
        </a>
    <?php endif; ?>
    
    <div class="logo-container">
        <img src="client/static/images/EasySmileLogo.png" alt="Easy Smile Dental and Orthodontics Clinic">
    </div>

    <?php if (isset($_SESSION['user_name'])):?>
        <a href="client/utils/schedule_appointment.php" class="book-now">BOOK NOW</a>
    <?php else: ?>
        <a href="#" onclick="showLoginRequired()" class="book-now">BOOK NOW </a>
    <?php endif; ?>

    <div class="create-account">
            <a href="client/utils/create_new_account.php">Need an Account? Create one!</a>
    </div>
    <h1 class="tagline">Your Best Smiles Starts Here!</h1>

    <!-- displays login pop up when the user is not logged in -->
    <?php if (!isset($_SESSION['user_name'])): ?>
        <div id="login-popup-container"></div>
    <?php endif; ?>

</section>

<script src="client/js/login.js"></script>