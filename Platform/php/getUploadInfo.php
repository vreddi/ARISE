<?php

require("connection.php");

// Get the query/function that needs to be performed
$query = $_REQUEST['query'];

$flag = true;

switch((string)$query){
	// Record the score an dother game details for a single tweety game in the DB
	case "getAspects":
		$flag = false;

		echo getAspects();
		break;

	case "getSources":

		echo getSources();
		break;

	default:
		break;
}


/**
* This method gets all the Aspects uploaded in the service with all their information
* in a JSON list.
* 
* @return Aspects :: JSON containing all the information about all the aspects
*/
function getAspects(){

	// Get connection to the DB
	$link = getConnection();
	$query = "SELECT * FROM Uploads;";
	$res = mysqli_query($link,$query);
	// Associative Array to store the top players info
	$Aspects = array();
	$rank = 1;
	while($row = $res->fetch_array()){

		if($row['Type'] == "Aspect"){
			// Add Players Details to the Associative Array
			$Aspects[$rank]['ID']= $row['Upload_ID'];
			$Aspects[$rank]['Name'] = $row['Name'];
			$Aspects[$rank]['Description'] = $row['Description'];
			$Aspects[$rank]['Type'] = $row['Type'];
			$Aspects[$rank]['Location'] = $row['Location'];

			// Increase the rank
			$rank = $rank + 1;
		}
	}
	return json_encode($Aspects);

}

/**
* This method gets all the Sources uploaded in the service with all their information
* in a JSON list.
* 
* @return Sources :: JSON containing all the information about all the sources 
*/
function getSources(){

	// Get connection to the DB
	$link = getConnection();
	$query = "SELECT * FROM Uploads;";
	$res = mysqli_query($link,$query);
	// Associative Array to store the top players info
	$Sources = array();
	$rank = 1;
	while($row = $res->fetch_array()){

		if($row['Type'] == "Source"){
			// Add Players Details to the Associative Array
			$Sources[$rank]['ID']= $row['Upload_ID'];
			$Sources[$rank]['Name'] = $row['Name'];
			$Sources[$rank]['Description'] = $row['Description'];
			$Sources[$rank]['Type'] = $row['Type'];
			$Sources[$rank]['Location'] = $row['Location'];

			// Increase the rank
			$rank = $rank + 1;
		}
	}
	return json_encode($Sources);
}

?>
