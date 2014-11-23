<?php

class Cache {

	private $query_str;
	private $query_escaped;
	private $query_stems;

	public function __construct($query_str) {
		$this->query_str = $query_str;
		$this->query_escaped = preg_replace('/[^A-Za-z0-9_\- ]/', '_', $query_str);
		$this->query_stems = array();

		if($this->allow_cache())
			$this->stem_words();
	}

	//escape the filename, only check cached file when the escaped string equal to input string
	public function allow_cache() {
		return $this->query_str == $this->query_escaped;
	}

	public function load_cache() {
		$files = glob('../cached/*');

		foreach($files as $file) {
			$file = substr($file, 10);
			$parts = explode(' ', $file);

			if(count($parts) == count($this->query_stems)) {
				$match = true;
				foreach($parts as $part) {
					if(!in_array($part, $this->query_stems)) {
						$match = false;
						break;
					}
				}
				if($match) return file_get_contents('../cached/' . $file);
			}

		}

		return false;
	}

	public function save_cache($raw) {
		if(count($this->query_stems) != 0)
			file_put_contents('../cached/' . implode(' ', $this->query_stems), $raw);
	}

	private function stem_words() {
		$raw = shell_exec('cd ../executable/ && java -jar app.jar stem ' . escapeshellarg($this->query_str));
		$result = json_decode($raw);
		$this->query_stems = $result->stems;
	}

}

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

	public function suggest() {

		//check if any input words come in
		if(isset($_POST['query'])) $this->query_str = $_POST['query'];
		else return;

		$raw = null;

		//check if jar exists
		if(file_exists('../executable/app.jar')) $raw = shell_exec('cd ../executable/ && java -jar app.jar suggest ' . escapeshellarg($this->query_str));
		else return;

		$this->results = json_decode($raw);
	}

	public function search() {

		//check if any query come in
		if(isset($_GET['query'])) $this->query_str = $_GET['query'];
		else return;

		$raw = null;

		$cache = new Cache($this->query_str);
		//check if the result is cached
		
		if($cache->allow_cache()) {
			$raw = $cache->load_cache();
			if($raw === false) $raw = null;
		}

		if($raw === null) {
			//check if jar exists
			if(file_exists('../executable/app.jar')) $raw = shell_exec('cd ../executable/ && java -jar app.jar search ' . escapeshellarg($this->query_str));
			else return;

			//only save into cached when the escaped string equal to input string
			if($raw !== null && count($raw) > 0 && $cache->allow_cache()) 
				$cache->save_cache($raw);
		}

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

?>
