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

	default:
		break;
}

function getAspects(){

	// Get connection to the DB
	$link = getConnection();
	$query = "SELECT * FROM Uploads;";
	$res = mysqli_query($link,$query);
	// Associative Array to store the top players info
	$Aspects = array();
	$rank = 1;
	while($row = $res->fetch_array()){
		// Add Players Details to the Associative Array
		$Aspects[$rank]['ID']= $row['Upload_ID'];
		$Aspects[$rank]['Name'] = $row['Name'];
		$Aspects[$rank]['Description'] = $row['Description'];
		$Aspects[$rank]['Type'] = $row['Type'];
		$Aspects[$rank]['Location'] = $row['Location'];

		// Increase the rank
		$rank = $rank + 1;
	}
	return json_encode($Aspects);

}

?>
