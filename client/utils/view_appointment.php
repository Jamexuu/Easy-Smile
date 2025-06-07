<?php
// --- Database connection setup ---
$servername = "localhost";
$username = "your_db_username";
$password = "your_db_password";
$dbname = "your_db_name";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// --- Fetch user data (replace with your actual logic, e.g., session user id) ---
$user_id = 1; // Example user id, replace as needed
$sql = "SELECT first_name, last_name, middle_name, birth_date, gender, email, phone FROM users WHERE id = $user_id";
$result = $conn->query($sql);

if ($result && $result->num_rows > 0) {
    $row = $result->fetch_assoc();
    $firstName = htmlspecialchars($row['first_name']);
    $lastName = htmlspecialchars($row['last_name']);
    $middleName = htmlspecialchars($row['middle_name']);
    $birthDate = htmlspecialchars($row['birth_date']);
    $gender = htmlspecialchars($row['gender']);
    $email = htmlspecialchars($row['email']);
    $phone = htmlspecialchars($row['phone']);
} else {
    $firstName = $lastName = $middleName = $birthDate = $gender = $email = $phone = "";
}

$conn->close();
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>EasySmile Dashboard</title>
    <style>
        body {
            background: #fff;
            margin: 0;
            font-family: Segoe UI, sans-serif;
        }
        .logo {
            text-align: center;
            margin-top: 30px;
            margin-bottom: 10px;
        }
        .logo img {
            height: 70px;
        }
        .logo-text {
            color: #1976d2;
            font-size: 1.1em;
            margin-top: 2px;
        }
        .container {
            display: flex;
            justify-content: center;
            align-items: flex-start;
            margin-top: 40px;
        }
        .sidebar {
            background: #f2f2f2;
            width: 200px;
            border-radius: 4px;
            padding: 30px 0;
            margin-right: 50px;
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
        }
        .sidebar ul li.active a {
            color: #222;
            text-decoration: underline;
        }
        .main-content {
            background: #ededed;
            border-radius: 4px;
            padding: 40px 60px 40px 40px;
            min-width: 480px;
            min-height: 420px;
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
    </style>
</head>
<body>
    <div class="logo">
        <img src="client/static/images/EasySmileLogo.png" alt="EasySmile Logo"> 
        <!-- need to be replaced with the actual path -->
    </div>
    <div class="container">
        <nav class="sidebar">
            <ul>
                <li class="active"><a href="#">Dashboard</a></li>
                <li><a href="#">Appointment</a></li>
            </ul>
        </nav>
        <div class="main-content">
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
                <div class="full-width">
                    <label for="gender">Gender <span style="color: #888; font-size: 0.9em;">(optional)</span></label>
                    <input type="text" id="gender" name="gender" value="<?php echo $gender; ?>" readonly>
                </div>
                <div class="full-width">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" value="<?php echo $email; ?>" readonly>
                </div>
                <div class="full-width">
                    <label for="phone">Phone Number</label>
                    <input type="text" id="phone" name="phone" value="<?php echo $phone; ?>" readonly>
                </div>
            </form>
            <button class="logout-btn">Logout</button>
        </div>
    </div>
</body>
</html>
