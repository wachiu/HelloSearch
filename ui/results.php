<?php

//include layer to call java

include 'layer.php';

//variable exports
$app->search();

$results = $app->get_results();

$query_str = htmlentities($app->get_query_str());

$finished_time = $app->get_finished_time();

$has_query = $app->has_query();

if($app->is_ajax()) {
	$return = array(
		'results' => $results,
		'query_str' => $query_str,
		'finished_time' => $finished_time,
		'has_query' => $has_query
	);
	echo json_encode($return);
	die();
}

//document start
?>
<!doctype html>
<html>
	<head>
		<title><?=$query_str?> | Hello Search</title>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta name="description" content="">
		<meta name="author" content="">
		<link rel="icon" href="favicon.ico">
		<link href="css/bootstrap.css" rel="stylesheet">
		<link href="css/main.css" rel="stylesheet">
	</head>
	<body>
		<nav>
			<ul>
				<li class="active"><a href="#">Search</a></li>
				<li><a href="#">History</a></li>
			</ul>
		</nav>



<?php if($has_query) { ?>

		<div class="container results">
			<p>Showing <?=count($results)?> results (<?=$finished_time?> seconds)</p>
			
			
			<div class="result row" display="none">
				<div class="col-sm-9">
					<div class="result-header">
						<a class="result-title" href="#">This is the page's title</a>
						<a class="result-url" href="#">http://theactualurl.com/</a>
					</div>
					<div class="result-body">
						<span>Last Modified: <span class="result-modified">November 10 2014 02:33:18</span></span>
					</div>
				</div>
				<div class="col-sm-3">
					<div class="result-stats">
						<div class="result-stat">
							<small>Score</small>
							<span>4.02</span>
						</div>
						<div class="result-stat">
							<small>Size</small>
							<span class="result-size">1357 kb</span>
						</div>
					</div>
				</div>
				<div class="col-sm-12">
					<div class="result-frequent">
						<span>these <span class="badge">20</span></span>
						<span>are <span class="badge">14</span></span>
						<span>some <span class="badge">6</span></span>
						<span>frequent <span class="badge">3</span></span>
						<span>words <span class="badge">3</span></span>
					</div>
				</div>
			</div>
			
			<!-- end of div.result.row -->

<?php foreach($results as $result) { ?>
			<div class="result row">
				<div class="col-sm-9">
					<div class="result-header">
						<a class="result-title" href="<?=$result->url?>" target="_blank"><?=$result->pageTitle?></a>
						<a class="result-url" href="<?=$result->url?>" target="_blank"><?=$result->url?></a>
					</div>
					<div class="result-body">
						<span>Last Modified: <span class="result-modified"><?=$result->lastModified?></span></span>
					</div>
				</div>
				<div class="col-sm-3">
					<div class="result-stats">
						<div class="result-stat">
							<small>Score</small>
							<span><?=number_format($result->score, 2)?></span>
						</div>
						<div class="result-stat">
							<small>Size</small>
							<span class="result-size"><?=number_format($result->size / 1024, 2)?> kb</span>
						</div>
					</div>
				</div>
				<div class="col-sm-12">
					<div class="result-frequent">
						<span>these <span class="badge">20</span></span>
						<span>are <span class="badge">14</span></span>
						<span>some <span class="badge">6</span></span>
						<span>frequent <span class="badge">3</span></span>
						<span>words <span class="badge">3</span></span>
					</div>
				</div>
			</div>

<?php } ?>


		</div>
		<!-- end of div.container.results -->

<?php } ?>

		<footer class="container">
			&copy; Hello Search 2014.
		</footer>

		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
		<script src="js/bootstrap.js"></script>
		<script src="js/main.js"></script>
	</body>
</html>
