<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}
?>
<section class="hero">
    
    <?php if (isset($_SESSION['user_name'])): ?>
        <div class="user-info">
            <h2 class="login-heading">Hi, <?= ucwords(htmlspecialchars($_SESSION['user_name'])) ?>!</h2>
            <a href="client/utils/logout.php" class="log-out-link">Log Out</a>
        </div>
        
        <a href="view_appointment.php" class="appointment-link"></a>
        <div class="appointment-box">
            <div class="appointment-info">
                <p>Upcoming Appointment:</p>
                <p>May 31, Saturday</p>
            </div>
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