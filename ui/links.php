<?php

//include layer to call java

include 'layer.php';

//variable exports
$app->links();

$results = $app->get_results();

if($app->is_ajax()) {
	$return = array(
		'results' => $results
	);
	echo json_encode($return);
	die();
}

?>
