<?php
session_start();
require_once '../Connection/db_connection.php';

header('Content-Type: application/json');

// Check if user is logged in
if (!isset($_SESSION['user_email'])) {
    echo json_encode(['success' => false, 'message' => 'User not logged in']);
    exit;
}

$email = $_SESSION['user_email'];
$conn = create_connection();

if (!$conn) {
    echo json_encode(['success' => false, 'message' => 'Database connection failed']);
    exit;
}

try {
    // First get the AccountID associated with the user's email
    $stmt = $conn->prepare("SELECT AccountID FROM AccountTbl WHERE Email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $accountResult = $stmt->get_result();

    if ($accountResult->num_rows > 0) {
        $accountRow = $accountResult->fetch_assoc();
        $accountId = $accountRow['AccountID'];
        
        // Now get the address using the AccountID
        $stmt = $conn->prepare("SELECT Barangay, City, Province FROM AccountAddressTbl WHERE AccountID = ?");
        $stmt->bind_param("s", $accountId);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            echo json_encode([
                'success' => true, 
                'address' => [
                    'barangay' => $row['Barangay'],
                    'city' => $row['City'],
                    'province' => $row['Province']
                ]
            ]);
        } else {
            echo json_encode(['success' => false, 'message' => 'User address not found']);
        }
    } else {
        echo json_encode(['success' => false, 'message' => 'Account not found']);
    }
} catch (Exception $e) {
    echo json_encode(['success' => false, 'message' => 'Error: ' . $e->getMessage()]);
} finally {
    $conn->close();
}
?>