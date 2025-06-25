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
    <link rel="stylesheet" href="client/css/style.css">
</head>
<body>
    <header class="top-header">
        <?php include 'client/components/header.html'; ?>
    </header>
    
    <?php include 'client/components/hero.php'; ?>
    <?php include 'client/components/services.html'; ?>

    <?php include 'client/components/dentist.html'; ?>

    <div class="small_footer_logo">
        <img src="client/static/images/smalllogonotext.png">
    </div>

    <?php if (isset($_GET['registration']) && $_GET['registration'] === 'success'): ?>
        <div id="success-popup-container">
            <?php include 'client/components/success_popup.html'; ?>
        </div>
        
        <script>
            // Auto-show popup when page loads
            window.addEventListener('DOMContentLoaded', function() {
                if (typeof showSuccessPopup === 'function') {
                    showSuccessPopup();
                }
            });
        </script>
    <?php endif; ?>

    <script>
        // Check for appointment cancellation status
        <?php if (isset($_GET['cancelled']) && $_GET['cancelled'] === 'true'): ?>
            alert("Your appointment has been cancelled successfully!");
        <?php elseif (isset($_GET['cancelled']) && $_GET['cancelled'] === 'false'): ?>
            alert("Error: <?php echo isset($_SESSION['error']) ? $_SESSION['error'] : 'Failed to cancel appointment'; ?>");
        <?php endif; ?>
        
        <?php 
        // Clear the session variables after showing the message
        if (isset($_SESSION['success'])) unset($_SESSION['success']);
        if (isset($_SESSION['error'])) unset($_SESSION['error']);
        ?>
    </script>
</body>
<footer class="top-header">
    <?php include 'client/components/footer.html'; ?>
</footer>
</html>




