<?php session_start(); ?>
<!-- Hero Section -->
<section class="hero">
    
    <?php if (isset($_SESSION['user_name'])): ?>
        <!-- Show user info and logout when logged in -->
        <div class="user-info">
            <h2 class="login-heading">Hi, <?= ucwords(htmlspecialchars($_SESSION['user_name'])) ?>!</h2>
            <a href="client/utils/logout.php" class="log-out-link">Log Out</a>
        </div>
        
        <!-- Blue box with appointment (only show for logged in users) -->  
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
        
        <div class="create-account">
            <a href="client/utils/create_new_account.php">Need an Account? Create one!</a>
        </div>
    <?php endif; ?>
    
    <div class="logo-container">
        <img src="client/static/images/EasySmileLogo.png" alt="Easy Smile Dental and Orthodontics Clinic">
    </div>
    
    <a href="#" class="book-now">BOOK NOW</a>
    
    <h1 class="tagline">Your Best Smiles Starts Here!</h1>

    <!-- Login popup container (only show when not logged in) -->
    <?php if (!isset($_SESSION['user_name'])): ?>
        <div id="login-popup-container"></div>
    <?php endif; ?>

</section>

<script>
    function openLoginPopup() {
        <?php if (!isset($_SESSION['user_name'])): ?>
            if (!document.getElementById('login-popup')) {
                fetch('client/components/login.html')
                    .then(response => response.text())
                    .then(html => {
                        document.getElementById('login-popup-container').innerHTML = html;
                        document.getElementById('login-popup').style.display = 'flex';
                    })
                    .catch(error => {
                        console.error('Error loading login form:', error);
                    });
            } else {
                document.getElementById('login-popup').style.display = 'flex';
            }
        <?php else: ?>
            // Redirect to appointments or dashboard
            window.location.href = 'view_appointment.php';
        <?php endif; ?>
    }
    
    function closeLoginPopup() {
        if (document.getElementById('login-popup')) {
            document.getElementById('login-popup').style.display = 'none';
        }
    }
    
    window.onclick = function(event) {
        var popup = document.getElementById('login-popup');
        if (popup && event.target === popup) {
            popup.style.display = "none";
        }
    }

    console.log('Current page location:', window.location.pathname);
    console.log('Trying to fetch from: client/components/login.html');
</script>