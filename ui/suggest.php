<?php

//include layer to call java

include 'layer.php';

//variable exports
$app->suggest();

$results = $app->get_results();

//if($app->is_ajax()) {
	$return = array(
		'results' => $results
	);
	echo json_encode($return);
	die();
//}

?>
