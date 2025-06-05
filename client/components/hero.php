 <!-- Hero Section -->
    <section class="hero">
        
        <?php session_start(); ?>
        <a href="#" onclick="openLoginPopup(); return false;">
            <h2 class="login-heading">
                <?php
                    if (isset($_SESSION['username'])){
                        echo "Hi, " . ucwords(htmlspecialchars($_SESSION['username'])) . "!";
                    } else {
                        echo "Login";
                    }
                ?>
            </h2>
        </a>
        
        <!-- Blue box with appointment only -->  
        <a href="view_appointment.php" class="appointment-link"></a>
        <div class="appointment-box">
            <div class="appointment-info">
                <p>Upcoming Appointment:</p>
                <p>May 31, Saturday</p>
            </div>
        </div>
        
        <a href="#" class="log-out-link">Log Out</a>
        
        <div class="logo-container">
            <img src="../static/images/EasySmileLogo.png" alt="Easy Smile Dental and Orthodontics Clinic">
        </div>
        
        <a href="#" class="book-now">BOOK NOW</a>
        
        <div class="create-account">
            <a href="create_new_account.php">Need an Account? Create one!</a>
        </div>
        
        <h1 class="tagline">Your Best Smiles Starts Here!</h1>

        <div id="login-popup-container"></div>

    </section>
    <script>
        function openLoginPopup() {
            // Only load if not already loaded
            if (!document.getElementById('login-popup')) {
                fetch('components/login.html')
                    .then(response => response.text())
                    .then(html => {
                        document.getElementById('login-popup-container').innerHTML = html;
                        document.getElementById('login-popup').style.display = 'flex';
                    });
            } else {
                document.getElementById('login-popup').style.display = 'flex';
            }
        }
        function closeLoginPopup() {
            if (document.getElementById('login-popup')) {
                document.getElementById('login-popup').style.display = 'none';
            }
        }
        // Optional: Close popup when clicking outside the form
        window.onclick = function(event) {
            var popup = document.getElementById('login-popup');
            if (popup && event.target === popup) {
                popup.style.display = "none";
            }
        }
    </script>
