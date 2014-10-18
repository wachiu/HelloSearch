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
	
	public static void test() {
		Indexer myIndexer = new Indexer();
		myIndexer.IndexPage("1", "A sentence is a linguistic unit consisting of one or more words that are grammatically linked. A sentence can include words grouped meaningfully to express a statement, question, exclamation, request, command or suggestion.");
		myIndexer.IndexPage("2", "At YourDictionary we try to give you all of the tools you need to really understand what the word means. Seeing the word in a sentence can provide more context ");
		myIndexer.finalize();
	}
	
	public static void main (String[] args) {
		App app = new App();
		app.run();
		app.finalize();
		//App.test();
	}
	
}
