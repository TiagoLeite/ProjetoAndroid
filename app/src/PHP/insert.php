<?php

require_once "connect.php";

$name = $_POST["name"];
$email = $_POST["email"];
$birth = $_POST["birth"];
$gender = $_POST["gender"];
$username = $_POST["username"];
$password = $_POST["password"];

if($con){
	//echo "Connection is fine!";
	$checkEmail = "SELECT * FROM User WHERE email = '$email'";
	$checkEmailResults = mysqli_query($con, $checkEmail);
	$checkUser = "SELECT * FROM User WHERE username = '$username'";
	$checkUserResults = mysqli_query($con, $checkUser);
	if(mysqli_num_rows($checkEmailResults) > 0 && mysqli_num_rows($checkUserResults) > 0)
		echo "Email and User already exists!";
	else if(mysqli_num_rows($checkEmailResults) > 0 && mysqli_num_rows($checkUserResults) == 0)
		echo "Email already exists!";
	else if(mysqli_num_rows($checkEmailResults) == 0 && mysqli_num_rows($checkUserResults) > 0)
		echo "Username already exists!";
	else{
		$sqlinsert = "INSERT INTO User(name, email, gender, birth, username, password) VALUES('$name', '$email', '$gender', '$birth', '$username', '$password');";
		if(mysqli_query($con, $sqlinsert)){
			echo "Registration successfully!";
		}
		else
			echo "There was a error during registration. Please, try again.";
	}

}else{
	echo "Connection not working!";
}

?>