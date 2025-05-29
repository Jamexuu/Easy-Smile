
<!-- filepath: c:\Users\Genesis Pardo\Desktop\EasySmileDentalClinic\index.php -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Easy Smile Dental Clinic</title>
    <!-- Google Fonts - Poppins -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;700&display=swap" rel="stylesheet">
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <header class="top-header">
        <?php include 'components/header.html'; ?>
    </header>
    
    <!-- Hero Section -->
    <section class="hero">
        <!-- Login text (outside blue box) -->
        <h2 class="login-heading">Login/Hi, Iyah!</h2>
        
        <!-- Blue box with appointment only -->  
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
            <a href="#">Need an Account? Create one!</a>
        </div>
        
        <h1 class="tagline">Your Best Smiles Starts Here!</h1>
    </section>
    
    <!-- Rest of your content remains the same -->
    <div class="services-banner">
        Services Offered
    </div>
    
    <!-- Main content -->
    <div class="services-grid">
        <!-- Service rows and items remain unchanged -->
        <div class="services-row">
            <div class="service-item">
                <img src="../static/images/ngipinbullet.png" alt="Service 1">
                <span>General Dentistry</span>
            </div>
            <div class="service-item">
                <img src="../static/images/ngipinbullet.png" alt="Service 2">
                <span>Cosmetic Dentistry</span>
            </div>
            <div class="service-item">
                <img src="../static/images/ngipinbullet.png" alt="Service 3">
                <span>Orthodontics</span>
            </div>
        </div>
        <div class="services-row">
            <div class="service-item">
                <img src="../static/images/ngipinbullet.png" alt="Service 4">
                <span>Pediatric Dentistry</span>
            </div>
            <div class="service-item">
                <img src="../static/images/ngipinbullet.png" alt="Service 5">
                <span>Periodontics</span>
            </div>
            <div class="service-item">
                <img src="../static/images/ngipinbullet.png" alt="Service 6">
                <span>Oral Surgery</span>
            </div>
        </div>
    </div>

    <!-- Our Dentist Section -->
    <div class="our-dentist-section">
        <div class="our-dentist-container">
            <div class="our-dentist-content">
                <h2 class="our-dentist-heading">Our Dentist</h2>
                <h3 class="our-dentist-name">Dr. Willie Revillame</h3>
                <p class="our-dentist-bio">
                    Willie Revillame is a popular Filipino television host, writer, songwriter, comedian, 
                    and businessman. He is best known for hosting various hit game and variety shows 
                    in the Philippines, such as Wowowee, Willing Willie, and Wowowin. With his 
                    signature charm, humor, and generosity, Willie became a household name, 
                    admired for his dedication to entertaining and helping ordinary Filipinos. Beyond 
                    showbiz, he is also a successful entrepreneur and music artist, known for books like 
                    Ikaw Na Nga and Boom Tarat Tarat.
                </p>
            </div>
            <div class="our-dentist-image-container">
                <img src="../static/images/dentistpicture.jpg" alt="Dr. Willie Revillame" class="our-dentist-image">
            </div>
        </div>
    </div>
</body>
</html>