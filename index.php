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
    
    <?php include 'components/hero.html'; ?>
    <?php include 'components/services.html'; ?>

    <?php include 'components/dentist.html'; ?>

    <div class="small_footer_logo">
        <img src="static/images/smalllogonotext.png">
    </div>

   
</body>
 <footer class="top-header">
        <?php include 'components/footer.html'; ?>
    </footer>
</html>