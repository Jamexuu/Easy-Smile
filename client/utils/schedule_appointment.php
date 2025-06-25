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
        
        <!-- Section 1: Personal Information -->
        <p class="section-title">Personal Information</p>
        
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
                <input type="text" id="middleName" name="middleName" placeholder="Enter your middle name" 
                    value="<?= htmlspecialchars($formData['middleName'] ?? '') ?>">
            </div>
            <div class="form-group">
                <label for="lastName">Last Name</label>
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
                    <option value="male" <?= ($formData['gender'] ?? '') === 'Male' ? 'selected' : '' ?>>Male</option>
                    <option value="female" <?= ($formData['gender'] ?? '') === 'Female' ? 'selected' : '' ?>>Female</option>
                    <option value="other" <?= ($formData['gender'] ?? '') === 'Other' ? 'selected' : '' ?>>Other</option>
                </select>
            </div>
            

            <div class="form-group">
                <label for="phone">Phone Number</label>
                <input type="tel" id="phone" name="phone" placeholder="09123456789" 
                    value="<?= htmlspecialchars($formData['phone'] ?? '') ?>" required 
                    pattern="[0-9]{10,15}" title="10-15 digits only">
            </div>

            <!-- Add address section here -->
            <div class="address-section">
                <p class="section-title">Patient Address</p>
                <div class="address-fields">
                    <div class="form-group">
                        <label for="barangay">Barangay</label>
                        <input type="text" id="barangay" name="barangay" placeholder="Enter your barangay" 
                               value="<?= htmlspecialchars($formData['barangay'] ?? '') ?>" required>
                    </div>
                    <div class="form-group">
                        <label for="city">City</label>
                        <input type="text" id="city" name="city" placeholder="Enter your city" 
                               value="<?= htmlspecialchars($formData['city'] ?? '') ?>" required>
                    </div>
                    <div class="form-group">
                        <label for="province">Province</label>
                        <input type="text" id="province" name="province" placeholder="Enter your province" 
                               value="<?= htmlspecialchars($formData['province'] ?? '') ?>" required>
                    </div>
                    
                    <input type="hidden" id="usingAccountAddress" name="usingAccountAddress" value="0">
                    <!-- Add "Same as Accounts Address" checkbox -->
                    <?php if(isset($_SESSION['user_email'])): ?>
                    <div class="form-group checkbox-group">
                        <input type="checkbox" id="sameAsAccount" name="sameAsAccount">
                        <label for="sameAsAccount">Same as Account's Address</label>
                    </div>
                    <?php endif; ?>
                </div>
            </div>
            
            <!-- Section 3: Appointment Details -->
            <p class="section-title">Appointment Details</p>
            <div class="form-group">
                <label for="purposeOfVisit">Purpose of Visit</label>
                <select id="purposeOfVisit" name="purposeOfVisit" required>
                    <option value="">-- Select purpose of visit --</option>
                    <option value="Braces" <?= ($formData['purposeOfVisit'] ?? '') === 'Braces' ? 'selected' : '' ?>>Braces</option>
                    <option value="Retainers" <?= ($formData['purposeOfVisit'] ?? '') === 'Retainers' ? 'selected' : '' ?>>Retainers</option>
                    <option value="Surgeries" <?= ($formData['purposeOfVisit'] ?? '') === 'Surgeries' ? 'selected' : '' ?>>Surgeries</option>
                    <option value="Checkup" <?= ($formData['purposeOfVisit'] ?? '') === 'Checkup' ? 'selected' : '' ?>>Checkup</option>
                    <option value="Deep Cleaning" <?= ($formData['purposeOfVisit'] ?? '') === 'Deep Cleaning' ? 'selected' : '' ?>>Deep Cleaning</option>
                    <option value="Tooth Extraction" <?= ($formData['purposeOfVisit'] ?? '') === 'Tooth Extraction' ? 'selected' : '' ?>>Tooth Extraction</option>
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
    
    // Replace the phone formatting with:
    document.getElementById('phone').addEventListener('input', function(e) {
        // Remove all non-digit characters
        let value = e.target.value.replace(/\D/g, '');
        // Limit to 15 characters
        value = value.substring(0, 15);
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

    // Handle "Same as Account's Address" checkbox
    const sameAsAccountCheckbox = document.getElementById('sameAsAccount');
    if (sameAsAccountCheckbox) {
        sameAsAccountCheckbox.addEventListener('change', function() {
            // Update the hidden field
            document.getElementById('usingAccountAddress').value = this.checked ? "1" : "0";
            
            if (this.checked) {
                // Fetch user's address from the server
                fetch('DAO/fetch_address.php')
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            // Fill in the address fields
                            document.getElementById('barangay').value = data.address.barangay || '';
                            document.getElementById('city').value = data.address.city || '';
                            document.getElementById('province').value = data.address.province || '';
                            
                            // Disable the fields when using account address
                            document.getElementById('barangay').readOnly = true;
                            document.getElementById('city').readOnly = true;
                            document.getElementById('province').readOnly = true;
                        } else {
                            alert('Could not retrieve your address. Please enter it manually.');
                            this.checked = false;
                            document.getElementById('usingAccountAddress').value = "0";
                        }
                    })
                    .catch(error => {
                        console.error('Error fetching address:', error);
                        alert('An error occurred. Please enter your address manually.');
                        this.checked = false;
                        document.getElementById('usingAccountAddress').value = "0";
                    });
            } else {
                // Enable fields for manual entry
                document.getElementById('barangay').readOnly = false;
                document.getElementById('city').readOnly = false;
                document.getElementById('province').readOnly = false;
            }
        });
    }
</script>

<style>
    /* Add this to your CSS or style block */
    .section-title {
        font-family: 'Poppins', sans-serif;
        font-size: 18px;
        font-weight: 600;
        color: #4da0ff;
        margin: 25px 0 15px 0;
        padding-bottom: 5px;
        border-bottom: 1px solid #e0e0e0;
    }
    
    /* If you want to keep existing classes but make them consistent */
    .appointment-subtitle, 
    .address-title {
        font-family: 'Poppins', sans-serif;
        font-size: 18px;
        font-weight: 600;
        color: #4da0ff;
        margin: 25px 0 15px 0;
        padding-bottom: 5px;
        border-bottom: 1px solid #e0e0e0;
    }
    
    .checkbox-group {
        display: flex;
        align-items: center;
        margin: 15px 0;
    }
    
    .checkbox-group input[type="checkbox"] {
        margin-right: 10px;
        width: auto;
        height: auto;
    }
    
    .checkbox-group label {
        font-size: 14px;
        color: #555;
        cursor: pointer;
    }
    
    input[readonly] {
        background-color: #f9f9f9;
        cursor: not-allowed;
    }
</style>
</html>