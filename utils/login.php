<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <link rel="stylesheet" href="../css/login.css">
</head>
<body>

    <?php
        // Start session
        session_start();

        // Example hardcoded credentials (replace with your own authentication logic)
        $valid_username = "admin";
        $valid_password = "password123";

        $error = "";

        if ($_SERVER["REQUEST_METHOD"] == "POST") {
            $username = $_POST["username"] ?? "";
            $password = $_POST["password"] ?? "";

            if ($username === $valid_username && $password === $valid_password) {
                $_SESSION["username"] = $username;
                header("Location: dashboard.php"); // Redirect after successful login
                exit();
            } else {
                $error = "Invalid username or password.";
            }
        }
    ?>
    <div class="login-container">
        <h2>Login</h2>
        <?php if ($error): ?>
            <p class="error"><?= htmlspecialchars($error) ?></p>
        <?php endif; ?>
        <form method="post" action="">
            <div>
                <label>Username:</label>
                <input type="text" name="username" required>
            </div>
            <div>
                <label>Password:</label>
                <input type="password" name="password" required>
            </div>
            <div style="margin-top:10px;">
                <button type="submit">Login</button>
            </div>
        </form>
    </div>
</body>
</html>