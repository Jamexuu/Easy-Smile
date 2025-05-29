<!-- filepath: c:\Users\Genesis Pardo\Desktop\EasySmileDentalClinic\index.php -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - Easy Smile Dental Clinic</title>
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
    <div class="register-container">
        <h1>Register</h1>
        <p class="register-subtitle">Fill Personal Information</p>
        
        <form action="register_process.php" method="POST" class="register-form">
            <div class="form-row">
                <div class="form-group">
                    <label for="firstName">First Name</label>
                    <input type="text" id="firstName" name="firstName" placeholder="Enter your name" required>
                </div>
                <div class="form-group">
                    <label for="lastName">Last Name</label>
                    <input type="text" id="lastName" name="lastName" placeholder="Enter your name" required>
                </div>
            </div>
            
            <div class="form-group">
                <label for="birthDate">Birth Date</label>
                <input type="text" id="birthDate" name="birthDate" placeholder="MM/DD/YYYY" required>
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
                <label for="phone">Phone Number</label>
                <input type="tel" id="phone" name="phone" placeholder="Enter you phone number" required>
            </div>
            
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" placeholder="Enter your email" required>
            </div>
            
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" placeholder="Enter your password" required>
            </div>
            
            <div class="form-group password-group">
                <label for="confirmPassword">Confirm password</label>
                <div class="password-container">
                    <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Retype your password" required>
                    <span class="password-toggle" onclick="togglePassword()">
                        <i class="far fa-eye"></i>
                    </span>
                </div>
            </div>
            
            <button type="submit" class="register-button">Register</button>
        </form>
    </div>

    <script>
        function togglePassword() {
            const passwordField = document.getElementById('confirmPassword');
            const icon = document.querySelector('.password-toggle i');
            
            if (passwordField.type === 'password') {
                passwordField.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                passwordField.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        }
    </script>
</body>
 
 <footer class="top-header">
        <?php include 'components/footer.html'; ?>
    </footer>
</html>