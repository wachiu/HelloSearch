<?php

class App {

	private $results;
	private $query_str;

	//benchmarking
	private $start_time;
	private $end_time;

	public function __construct() {
		$this->results = array();
		$this->query_str = '';
		$this->start_time = microtime(true);
	}

	public function run() {

		//check if any query come in
		if(isset($_GET['query'])) $this->query_str = $_GET['query'];
		else return;

		$raw = null;

		//check if jar exists
		if(file_exists('../executable/app.jar')) $raw = shell_exec('cd ../executable/ && java -jar app.jar search ' . escapeshellarg($this->query_str));
		else return;

		$this->results = json_decode($raw);

		$this->end_time = microtime(true);
	}

	public function has_query() {
		return $this->query_str !== '';
	}

	public function get_results() {
		return $this->results;
	}

	public function get_query_str() {
		return $this->query_str;
	}

	public function get_finished_time() {
		$time = $this->end_time - $this->start_time;
		//if no query, time will be negative, use random time to pretend ...
		if($time < 0) return '0.0' . rand(10, 99);
		else return number_format($time, 3);
	}

	public function is_ajax() {
		return isset($_SERVER['HTTP_X_REQUESTED_WITH']) && strtolower($_SERVER['HTTP_X_REQUESTED_WITH']) === 'xmlhttprequest';
	}

}

$app = new App;

$app->run();

?>
