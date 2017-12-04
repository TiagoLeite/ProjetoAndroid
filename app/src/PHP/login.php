<?php
    
    require_once "connect.php";
    
    $username = $_POST["username"];
    $password = $_POST["password"];

    $count = 0;

    $statement = mysqli_prepare($con, "SELECT * FROM User WHERE username = '$username'");

    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $colUserID, $colName, $colemail, $colGender, $colBirth, $colUsername, $colPassword);
    
    $response = array();
    $response["success"] = false;
    $response["usernameExists"] = false;
    
    while(mysqli_stmt_fetch($statement)){
        $response["usernameExists"] = true;
        if ($password == $colPassword) {
            $response["success"] = true;  
            $response["name"] = $colName;
            $response["email"] = $colemail;
            $response["gender"] = $colGender;
            $response["birth"] = $colBirth;
            $response["username"] = $colUsername;
            $response["password"] = $colPassword;
        }
    }
    echo json_encode($response);
    mysqli_close($con);
?>