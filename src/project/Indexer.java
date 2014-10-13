package project;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.htmlparser.beans.StringBean;

public class Indexer {

	private Spider spider;
	private StopStem stopStem;
	private InvertedIndex pageIndex;
	private InvertedIndex wordIndex;
	
	public Indexer() {
		
		try {
			spider = new Spider("http://www.cse.ust.hk/", 30);
			stopStem = new StopStem("stopwords.txt");
			pageIndex = spider.Index();
			wordIndex = new InvertedIndex("idWord", "word");
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
	
	public void Index() throws IOException {
		
		StringBean sb = new StringBean ();
		Vector<String> v_str = new Vector<String>();
		//while (pageIndex.getItr()) {
			boolean links = false;
			sb.setLinks (links);
			//sb.setURL (url);
			String temp = sb.getStrings();
			String temp2 = "";
			int wordCount = 0;
			StringTokenizer st = new StringTokenizer(temp, "\n");
			while(st.hasMoreTokens()){
				temp2 = st.nextToken();
				if(!v_str.contains(temp2)) {
					v_str.add(temp2);
					wordIndex.addEntry(++wordCount + "", temp2);
				}
	        }
		//}
	}
	
	public void SaveToJDBM() {
		
		try {
			pageIndex.finalize();
			wordIndex.finalize();
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
}
