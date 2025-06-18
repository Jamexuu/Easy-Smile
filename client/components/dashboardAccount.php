<?php
session_start();
// Placeholder variables for user info (no database connection)
$firstName = $lastName = $middleName = $birthDate = $gender = $email = $phone = "";
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>EasySmile Dashboard & Appointments</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="../css/style.css">
    <style>
        html, body {
            height: 100%;
        }
        body {
            background: #fff;
            margin: 0;
            font-family: Segoe UI, sans-serif;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        .top-header {
            width: 100%;
            background: #fff;
            z-index: 10;
        }
        .main-wrapper {
            flex: 1 0 auto;
            display: flex;
            flex-direction: column;
            min-height: 0;
        }
        .container {
            display: flex;
            justify-content: center;
            align-items: flex-start;
            flex: 1;
            width: 100%;
            margin-top: 0;
        }
        .sidebar {
            background: #f2f2f2;
            width: 200px;
            border-radius: 4px;
            padding: 30px 0;
            margin-top: 115px;
            margin-right: 20px;
            margin-left: 100px; 
            flex-shrink: 0;
            height: fit-content;
        }
        .sidebar ul {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        .sidebar ul li {
            margin: 20px 0;
            padding-left: 30px;
        }
        .sidebar ul li a {
            text-decoration: none;
            color: #222;
            font-weight: bold;
            cursor: pointer;
        }
        .sidebar ul li.active a {
            color: #222;
            text-decoration: underline;
        }
        .content-area {
            flex: 1;
            display: flex;
            flex-direction: column;
            align-items: center;
        }
        .logo {
            text-align: center;
            margin: 50px 0 20px 0;
            /* Shift logo left to visually center it on the whole page */
            margin-left: -120px; /* Adjust this value as needed for your layout */
        }
        .logo img {
            height: 70px;
        }
        .main-content, .appointments-content {
            background: #ededed;
            border-radius: 4px;
            padding: 40px 60px 40px 40px;
            margin-top: 0;
            width: 100%;
            max-width: 1100px;
        }
        .main-content {
            min-width: 480px;
            min-height: 420px;
            display: block;
        }
        .appointments-content {
            display: none;
        }
        h1 {
            font-size: 2em;
            margin-bottom: 0;
        }
        .subtitle {
            font-size: 1em;
            color: #444;
            margin-bottom: 30px;
            font-weight: bold;
        }
        form {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px 30px;
        }
        label {
            display: block;
            margin-bottom: 6px;
            font-size: 0.97em;
            color: #333;
        }
        input[type="text"], input[type="email"], input[type="date"] {
            width: 95%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 3px;
            font-size: 1em;
            background: #f9f9f9;
        }
        input[readonly] {
            background: #f3f3f3;
            color: #444;
            cursor: not-allowed;
        }
        .full-width {
            grid-column: 1 / 3;
        }
        .logout-btn {
            margin-top: 30px;
            width: 180px;
            padding: 10px 0;
            background: #1976d2;
            color: #fff;
            border: none;
            border-radius: 3px;
            font-size: 1em;
            cursor: pointer;
            transition: background 0.2s;
            float: right;
        }
        .logout-btn:hover {
            background: #125ea7;
        }
        .appointments-table {
            border-collapse: collapse;
            width: 100%;
            background: #b3e6ff;
        }
        .appointments-table th, .appointments-table td {
            border: 1px solid #cbe7fa;
            padding: 12px 10px;
            text-align: center;
        }
        .appointments-table th {
            background: #1976d2;
            color: #fff;
            font-weight: bold;
            font-size: 1em;
        }
        .appointments-table td {
            background: #cceeff;
            font-size: 1em;
        }
        .appointments-table tr:nth-child(even) td {
            background: #e6f7ff;
        }
        .footer {
            width: 100%;
            background: #fff;
            padding: 10px 0;
            text-align: center;
            position: relative;
            bottom: 0;
            flex-shrink: 0;
        }
    </style>
    <script>
        function showSection(section) {
            // Hide both
            document.getElementById('dashboard-section').style.display = 'none';
            document.getElementById('appointments-section').style.display = 'none';
            // Remove active from both
            document.getElementById('sidebar-dashboard').classList.remove('active');
            document.getElementById('sidebar-appointment').classList.remove('active');
            // Show the selected
            if (section === 'dashboard') {
                document.getElementById('dashboard-section').style.display = 'block';
                document.getElementById('sidebar-dashboard').classList.add('active');
            } else {
                document.getElementById('appointments-section').style.display = 'block';
                document.getElementById('sidebar-appointment').classList.add('active');
            }
        }
        window.onload = function() {
            showSection('dashboard'); // Show dashboard by default
        };
    </script>
</head>
<body>
    <header class="top-header">
        <?php include '../components/header.html'; ?>
    </header>

    <div class="main-wrapper">
        <div class="container">
            <nav class="sidebar">
                <ul>
                    <li class="active" id="sidebar-dashboard">
                        <a onclick="showSection('dashboard')">Dashboard</a>
                    </li>
                    <li id="sidebar-appointment">
                        <a onclick="showSection('appointment')">Appointment</a>
                    </li>
                </ul>
            </nav>
            <div class="content-area">
                <div class="logo">
                    <img src="client/static/images/EasySmileLogo.png" alt="EasySmile Logo">
                </div>
                <div class="main-content" id="dashboard-section">
                    <h1>Dashboard</h1>
                    <div class="subtitle">Account Information</div>
                   <form>
                <div>
                    <label for="firstName">First Name</label>
                    <input type="text" id="firstName" name="firstName" value="<?php echo $firstName; ?>" readonly>
                </div>
                <div>
                    <label for="lastName">Last Name</label>
                    <input type="text" id="lastName" name="lastName" value="<?php echo $lastName; ?>" readonly>
                </div>
                <div>
                    <label for="middleName">Middle Name <span style="color: #888; font-size: 0.9em;">(optional)</span></label>
                    <input type="text" id="middleName" name="middleName" value="<?php echo $middleName; ?>" readonly>
                </div>
                <div>
                    <label for="birthDate">Birth Date</label>
                    <input type="text" id="birthDate" name="birthDate" value="<?php echo $birthDate; ?>" readonly>
                </div>
                <div>
                    <label for="gender">Gender</label>
                    <input type="text" id="gender" name="gender" value="<?php echo $gender; ?>" readonly>
                </div>
                <div>
                    <label for="email">Email</label>
                    <input type="text" id="email" name="email" value="<?php echo $email; ?>" readonly>
                </div>
                <div>
                    <label for="phone">Phone Number</label>
                    <input type="text" id="phone" name="phone" value="<?php echo $phone; ?>" readonly>
                </div>
            </form>

                    <button class="logout-btn">Logout</button>
                </div>
                <div class="appointments-content" id="appointments-section">
                    <h1>Appointments</h1>
                    <table class="appointments-table">
                        <thead>
                            <tr>
                                <th>Appointment ID</th>
                                <th>Patient Full Name</th>
                                <th>Service Name</th>
                                <th>Scheduled By</th>
                                <th>Appointment Date</th>
                                <th>Appointment Time</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- Appointment rows will be dynamically generated from the database in the future -->
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <footer class="top-header">
        <?php include '../components/footer.html'; ?>
    </footer>
</body>
</html>
