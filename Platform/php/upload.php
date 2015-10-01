<?php
// For DB connectivity
require("connection.php");


if(!is_dir("uploads")){

	mkdir("uploads");
}

$location = "uploads/" . basename($_FILES['uploadedfile']['name']);

if(move_uploaded_file(($_FILES['uploadedfile']['tmp_name']), $location)){

	saveData($location);
	header("location: http://ridashboard.midnightjabber.com/");
	die();
}

else{

	if(copy($_FILES['uploadedfile']['tmp_name'], $location)){
		//header("location: listfiles.php");
	}
	else{
		// File uplaod failed
		echo "You totally Failed, click <a href=\"../index.php\"> to try again";
	}
}





function saveData($location){

	global $_FILE, $_POST;

	// Get connection to the DB
	$link = getConnection();

	// Generate global unique Game ID 
	$guid = getGUID();

	// Insert Game Data for the Player
	$query = "INSERT INTO Uploads VALUES('" . (string)$guid . "', '" . (string)$_POST['name'] . "', '" . (string)$_POST['description'] . "', 'Aspect', '" . (string)$location."');";
	
	$res = mysqli_query($link,$query);

	echo (string)mysqli_error($link);

}


function getGUID(){
    if (function_exists('com_create_guid')){
        return com_create_guid();
    }else{
        mt_srand((double)microtime()*10000);//optional for php 4.2.0 and up.
        $charid = strtoupper(md5(uniqid(rand(), true)));
        $hyphen = chr(45);// "-"
        $uuid = chr(123)// "{"
            .substr($charid, 0, 8).$hyphen
            .substr($charid, 8, 4).$hyphen
            .substr($charid,12, 4).$hyphen
            .substr($charid,16, 4).$hyphen
            .substr($charid,20,12)
            .chr(125);// "}"
        return $uuid;
    }
}

?>