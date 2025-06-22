<?php

    require_once '../Connection/db_connection.php';

    $conn = create_connection();

    if(!$conn){
        echo json_encode(['error' => 'Database connection failed']);
        exit;
    }

    try {
        $query = "SELECT Title, FirstName, MiddleName, LastName, Age, Bio, DentistImgPath FROM DentistTbl LIMIT 1";
        $result = $conn->query($query);

        if($result->num_rows > 0){
            $dentist = $result->fetch_assoc();
            echo json_encode($dentist);
        } else {
            echo json_encode(['error' => 'No dentist found']);
        }

    }catch (Exception $e){
        echo json_encode(['error' => 'Error fetching dentist data: ' . $e->getMessage()]);
    } finally {
        $conn->close();
    }

?>