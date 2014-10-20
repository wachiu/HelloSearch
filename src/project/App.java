package project;

import java.io.IOException;

public class App {

	private Indexer indexer;
	private Spider spider;
	
	public App() {
		indexer = new Indexer();
		try {
			spider = new Spider("http://www.cse.ust.hk/", 100, indexer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			spider.crawl();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void finalize() {
		spider.finalize();
		indexer.finalize();
	}
	
	public static void main (String[] args) {
		App app = new App();
		app.run();
		app.finalize();
	}
	
}
