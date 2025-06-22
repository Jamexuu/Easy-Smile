<?php
session_start();
?>

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
    <link rel="stylesheet" href="../css/style.css">
    <link rel="stylesheet" href="../css/register.css">
</head>
<body>
    <header class="top-header">
        <?php include '../components/header.html'; ?>
    </header>

    <div class="appointment-container">
        <h1>Schedule Appointment</h1>
        <p class="appointment-subtitle">Patients Personal Information</p>
        
        <?php
            // Display any error messages
            if (isset($_SESSION['errors'])) {
                echo '<div class="error-messages">';
                foreach ($_SESSION['errors'] as $error) {
                    echo '<p class="error">' . htmlspecialchars($error) . '</p>';
                }
                echo '</div>';
                unset($_SESSION['errors']);
            }
            
            // Get user data if logged in and preserve form data
            $formData = $_SESSION['form_data'] ?? [];
            unset($_SESSION['form_data']);
            
            if (isset($_SESSION['user_name']) && isset($_SESSION['user_email']) && empty($formData)) {
                $nameParts = explode(' ', $_SESSION['user_name']);
                $formData['firstName'] = $nameParts[0] ?? '';
                $formData['lastName'] = $nameParts[1] ?? '';
                $formData['email'] = $_SESSION['user_email'];
            }
        ?>

        <form action="DAO/appointment_dao.php" method="POST" class="appointment-form">
            <div class="form-group">
                <label for="firstName">First Name</label>
                <input type="text" id="firstName" name="firstName" placeholder="Enter your first name" 
                    value="<?= htmlspecialchars($formData['firstName'] ?? '') ?>" required>
            </div>
            
            <div class="form-group">
                <label for="middleName">Middle Name</label>
                <!-- Fix: This should use middleName, not lastName -->
                <input type="text" id="middleName" name="middleName" placeholder="Enter your middle name" 
                    value="<?= htmlspecialchars($formData['middleName'] ?? '') ?>" required>
            </div>
            
            <div class="form-group">
                <label for="lastName">Last Name</label>
                <!-- Fix: This should use lastName -->
                <input type="text" id="lastName" name="lastName" placeholder="Enter your last name" 
                    value="<?= htmlspecialchars($formData['lastName'] ?? '') ?>" required>
            </div>
            
            <div class="form-group">
                <label for="birthDate">Birth Date</label>
                <div class="date-input-container">
                    <input type="date" id="birthDate" name="birthDate" 
                    value="<?= htmlspecialchars($formData['birthDate'] ?? '') ?>" required>
                </div>
            </div>
            
            <div class="form-group">
                <label for="gender">Gender <span class="optional">(optional)</span></label>
                <select id="gender" name="gender">
                    <option value="">-- Select gender --</option>
                    <option value="male" <?= ($formData['gender'] ?? '') === 'male' ? 'selected' : '' ?>>Male</option>
                    <option value="female" <?= ($formData['gender'] ?? '') === 'female' ? 'selected' : '' ?>>Female</option>
                    <option value="other" <?= ($formData['gender'] ?? '') === 'other' ? 'selected' : '' ?>>Other</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="purposeOfVisit">Purpose of Visit</label>
                <select id="purposeOfVisit" name="purposeOfVisit" required>
                    <option value="">-- Select purpose of visit --</option>
                    <option value="consultation" <?= ($formData['purposeOfVisit'] ?? '') === 'consultation' ? 'selected' : '' ?>>Consultation</option>
                    <option value="cleaning" <?= ($formData['purposeOfVisit'] ?? '') === 'cleaning' ? 'selected' : '' ?>>Cleaning</option>
                    <option value="filling" <?= ($formData['purposeOfVisit'] ?? '') === 'filling' ? 'selected' : '' ?>>Filling</option>
                    <option value="extraction" <?= ($formData['purposeOfVisit'] ?? '') === 'extraction' ? 'selected' : '' ?>>Extraction</option>
                    <option value="rootcanal" <?= ($formData['purposeOfVisit'] ?? '') === 'rootcanal' ? 'selected' : '' ?>>Root Canal</option>
                    <option value="orthodontics" <?= ($formData['purposeOfVisit'] ?? '') === 'orthodontics' ? 'selected' : '' ?>>Orthodontics</option>
                    <option value="other" <?= ($formData['purposeOfVisit'] ?? '') === 'other' ? 'selected' : '' ?>>Other</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="appointmentDate">Preffered Appointment Date</label>
                <div class="date-input-container">
                    <input type="date" id="appointmentDate" name="appointmentDate" 
                    value="<?= htmlspecialchars($formData['appointmentDate'] ?? '') ?>" required>
                </div>
            </div>

            <div class="form-group">
                <label for="appointmentTime">Preferred Time</label>
                <select id="appointmentTime" name="appointmentTime" required>
                    <option value="">-- Select time --</option>
                    <option value="09:00" <?= ($formData['appointmentTime'] ?? '') === '09:00' ? 'selected' : '' ?>>9:00 AM</option>
                    <option value="10:00" <?= ($formData['appointmentTime'] ?? '') === '10:00' ? 'selected' : '' ?>>10:00 AM</option>
                    <option value="11:00" <?= ($formData['appointmentTime'] ?? '') === '11:00' ? 'selected' : '' ?>>11:00 AM</option>
                    <option value="13:00" <?= ($formData['appointmentTime'] ?? '') === '13:00' ? 'selected' : '' ?>>1:00 PM</option>
                    <option value="14:00" <?= ($formData['appointmentTime'] ?? '') === '14:00' ? 'selected' : '' ?>>2:00 PM</option>
                    <option value="15:00" <?= ($formData['appointmentTime'] ?? '') === '15:00' ? 'selected' : '' ?>>3:00 PM</option>
                    <option value="16:00" <?= ($formData['appointmentTime'] ?? '') === '16:00' ? 'selected' : '' ?>>4:00 PM</option>
                    <option value="17:00" <?= ($formData['appointmentTime'] ?? '') === '17:00' ? 'selected' : '' ?>>5:00 PM</option>
                </select>
            </div>
            
            <button type="submit" class="book-button">Book My Appointment!</button>
        </form>

        <div class="back-link" style="margin-top: 10px">
            <a href="../../index.php">← Back to Home</a>
        </div>

    </div>
    <footer class="top-header">
        <?php include '../components/footer.html'; ?>
    </footer>
</body>

<script>
    // Set minimum date to today for both birth date and appointment
    const today = new Date().toISOString().split('T')[0];
    
    // Birth date should be in the past (max today)
    document.getElementById('birthDate').max = today;
    
    // Appointment date should be in the future (min tomorrow)
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    document.getElementById('appointmentDate').min = tomorrow.toISOString().split('T')[0];
    
    // Phone number formatting
    document.getElementById('phone').addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length >= 3) {
            if (value.length <= 6) {
                value = value.substring(0,3) + '-' + value.substring(3);
            } else if (value.length <= 10) {
                value = value.substring(0,3) + '-' + value.substring(3,6) + '-' + value.substring(6,10);
            } else {
                value = value.substring(0,3) + '-' + value.substring(3,6) + '-' + value.substring(6,10);
            }
        }
        e.target.value = value;
    });

    // Validate appointment date is not on weekends (optional)
    document.getElementById('appointmentDate').addEventListener('change', function(e) {
        const selectedDate = new Date(e.target.value);
        const dayOfWeek = selectedDate.getDay();
        
        // 0 = Sunday, 6 = Saturday
        if (dayOfWeek === 0 || dayOfWeek === 6) {
            alert('Please select a weekday (Monday-Friday) for your appointment.');
            e.target.value = '';
        }
    });

    // Form validation before submit
    document.querySelector('.appointment-form').addEventListener('submit', function(e) {
        const appointmentDate = document.getElementById('appointmentDate').value;
        const appointmentTime = document.getElementById('appointmentTime').value;
        
        if (!appointmentDate || !appointmentTime) {
            e.preventDefault();
            alert('Please select both appointment date and time.');
            return false;
        }
        
        // Check if date is in the future
        const selectedDate = new Date(appointmentDate);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        
        if (selectedDate <= today) {
            e.preventDefault();
            alert('Please select a future date for your appointment.');
            return false;
        }
    });
</script>
</html>