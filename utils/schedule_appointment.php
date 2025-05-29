<!-- filepath: c:\Users\Genesis Pardo\Desktop\EasySmileDentalClinic\index.php -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Schedule Appointment - Easy Smile Dental Clinic</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/register.css">

    <header class="top-header">
        <?php include 'components/header.html'; ?>
    </header>
</head>
<body>
    <div class="appointment-container">
        <h1>Schedule Appointment</h1>
        <p class="appointment-subtitle">Patients Personal Information</p>
        
        <form action="book_appointment.php" method="POST" class="appointment-form">
            <div class="form-group">
                <label for="firstName">First Name</label>
                <input type="text" id="firstName" name="firstName" placeholder="Enter your name" required>
            </div>
            
            <div class="form-group">
                <label for="middleName">Middle Name</label>
                <input type="text" id="middleName" name="middleName" placeholder="Enter your name">
            </div>
            
            <div class="form-group">
                <label for="lastName">Last Name</label>
                <input type="text" id="lastName" name="lastName" placeholder="Enter your name" required>
            </div>
            
            <div class="form-group">
                <label for="birthDate">Birth Date</label>
                <div class="date-input-container">
                    <input type="text" id="birthDate" name="birthDate" placeholder="MM/DD/YYYY" required>
                    <i class="far fa-calendar date-icon"></i>
                </div>
            </div>
            
            <div class="form-group">
                <label for="gender">Gender <span class="optional">(optional)</span></label>
                <select id="gender" name="gender">
                    <option value="">-- Select gender --</option>
                    <option value="male">Male</option>
                    <option value="female">Female</option>
                    <option value="other">Other</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="purposeOfVisit">Purpose of Visit</label>
                <select id="purposeOfVisit" name="purposeOfVisit" required>
                    <option value="">-- Select purpose of visit --</option>
                    <option value="consultation">Consultation</option>
                    <option value="cleaning">Cleaning</option>
                    <option value="filling">Filling</option>
                    <option value="extraction">Extraction</option>
                    <option value="rootcanal">Root Canal</option>
                    <option value="orthodontics">Orthodontics</option>
                    <option value="other">Other</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="appointmentDate">Date and Time</label>
                <div class="date-input-container">
                    <input type="text" id="appointmentDate" name="appointmentDate" placeholder="MM/DD/YYYY" required>
                    <i class="far fa-calendar date-icon"></i>
                </div>
            </div>
            
            <button type="submit" class="book-button">Book My Appointment!</button>
        </form>
    </div>
    
</body>

 <footer class="top-header">
        <?php include 'components/footer.html'; ?>
    </footer>
</html>