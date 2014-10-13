package project;

import java.io.IOException;

public class Main {
	
	public static void main (String[] args) {
		Indexer indexer = new Indexer();
		
		//crawl page
		indexer.CrawlPages();
		
		//index page
		indexer.Index();
		
		//import to JDBM
		indexer.SaveToJDBM();
	}
	
}
