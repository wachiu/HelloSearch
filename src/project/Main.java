package project;

import java.io.IOException;

public class Main {
	
	public static void main (String[] args) {
		Indexer indexer = new Indexer();
		
		//crawl page
		indexer.CrawlPages();
		
		//index page
		try {
			indexer.Index();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//import to JDBM
		indexer.SaveToJDBM();
	}
	
}
