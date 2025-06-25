<?php
// filepath: c:\Users\james\OneDrive\Desktop\COMPROG\FE-IPT-IMP FINAL PROJECT\Easy-Smile\client\utils\DAO\fetch_clinic_info.php

require_once '../Connection/db_connection.php';

$conn = create_connection();

if (!$conn) {
    echo json_encode(['error' => 'Database connection failed']);
    exit;
}

try {
    $query = "SELECT PhoneNumber, Email, Location, FacebookLink, InstagramLink FROM ClinicInfoTbl LIMIT 1";
    $result = $conn->query($query);

    if ($result->num_rows > 0) {
        $clinicInfo = $result->fetch_assoc();
        echo json_encode($clinicInfo);
    } else {
        echo json_encode(['error' => 'No clinic information found']);
    }
} catch (Exception $e) {
    echo json_encode(['error' => 'Error fetching clinic information: ' . $e->getMessage()]);
} finally {
    $conn->close();
}
?>