<?php

	require_once "init.php";

	$con = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);

	if($con){
		echo "Database connection successfully!";
	}
	else{
		echo "Database connection failed!";
	}
?>