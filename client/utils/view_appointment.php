<!-- filepath: c:\Users\Genesis Pardo\Desktop\EasySmileDentalClinic\view_appointment.php -->
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
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/appointment.css">
</head>
<body>
    <header class="top-header">
        <?php include 'components/header.html'; ?>
    </header>

    <div class="appointment-view-container">
        <h1>Upcoming Appointment</h1>
        
        <div class="appointment-date">May 31, Saturday</div>
        
        <div class="appointment-details">
            <div class="detail-row">
                <div class="detail-label">First Name:</div>
                <div class="detail-value">John</div>
            </div>
            
            <div class="detail-row">
                <div class="detail-label">Middle Name:</div>
                <div class="detail-value">Michael</div>
            </div>
            
            <div class="detail-row">
                <div class="detail-label">Last Name:</div>
                <div class="detail-value">Doe</div>
            </div>
            
            <div class="detail-row">
                <div class="detail-label">Birth Date:</div>
                <div class="detail-value">05/15/1985</div>
            </div>
            
            <div class="detail-row">
                <div class="detail-label">Gender:</div>
                <div class="detail-value">Male</div>
            </div>
            
            <div class="detail-row">
                <div class="detail-label">Purpose of Visit:</div>
                <div class="detail-value">Cleaning</div>
            </div>
        </div>
        
        <button class="cancel-button">Cancel Appointment</button>
    </div>

    <footer class="top-header">
        <?php include 'components/footer.html'; ?>
    </footer>

    
</body>
</html>