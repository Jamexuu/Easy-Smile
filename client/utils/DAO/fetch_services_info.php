<?php

require_once '../Connection/db_connection.php';

$conn = create_connection();

if (!$conn) {
    echo json_encode(['error' => 'Database connection failed']);
    exit;
}

try {
    $query = "SELECT ServiceName, ServiceDesc, StartingPrice, Status FROM ServicesTbl WHERE Status = 'Available'";
    $result = $conn->query($query);

    $services = [];
    while ($row = $result->fetch_assoc()) {
        $services[] = $row;
    }

    echo json_encode($services);
} catch (Exception $e) {
    echo json_encode(['error' => 'Error fetching services data: ' . $e->getMessage()]);
} finally {
    $conn->close();
}
?>