package project;

import java.io.IOException;

public class Indexer {

	private Spider spider;
	private StopStem stopStem;
	private InvertedIndex index;
	
	public Indexer() {
		
		try {
			spider = new Spider("http://www.cse.ust.hk/", 30);
			stopStem = new StopStem("stopwords.txt");
			index = spider.Index();
		}
		catch (IOException ioe) {
			ioe.printStackTrace ();
		}
	}

	public void CrawlPages() {
		try {
			spider.crawl();
		}
		catch (IOException ioe) {
			ioe.printStackTrace ();
		}
	}
	
	public void Index() {
		
		
	}
	
	public void SaveToJDBM() {
		
		try {
			index.finalize();
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
}
