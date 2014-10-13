package project;

import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import org.htmlparser.beans.StringBean;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import java.util.StringTokenizer;
import org.htmlparser.beans.LinkBean;
import java.net.URL;
import java.io.IOException;
import project.InvertedIndex;

public class Spider
{
	private String url;
	private int pages;
	private InvertedIndex index;
	
	Spider(String _url, int _pages) throws IOException {
		url = _url;
		pages = _pages;
		index = new InvertedIndex("idUrl","ht1");
	}
	
	public void crawl() throws IOException {
		Queue<String> links = new LinkedList<String>();
		List<String> crawled = new LinkedList<String>();
		links.add(this.url);
		
		crawl_recursive(links, crawled, this.pages);
		
//		index.printAll(); // DEBUG
		System.out.println("\nComplete! " + pages + " pages crawled in total.");
	}
	
	private void crawl_recursive(Queue<String> links, List<String> crawled, int numPages) throws IOException {
		if(links.isEmpty() || numPages < 1) return;
		
		String _url = links.remove();
		crawled.add(_url);
		
		// TODO: Extract keywords and insert to inverted file (Indexer)
		
		if(!index.exists(_url))
			index.addEntry(Integer.toString(index.count()), _url);
		
		System.out.println(numPages + "/" + this.pages + " pages remaining. Crawling " + _url + "...");
		
		LinkBean lb = new LinkBean();
		lb.setURL(_url);
		URL[] urls = lb.getLinks();
		
		for(int i = 0; i < urls.length; i++) {
			String current = urls[i].toString();
			if(!crawled.contains(current) && !links.contains(current))
				links.add(current);
		}
		
		crawl_recursive(links, crawled, numPages-1);
	}
	
	public InvertedIndex Index() {
		return index;
	}
	
	public static void main (String[] args) {
		try {
			Spider crawler = new Spider("http://www.cse.ust.hk/", 30);
			crawler.crawl();
		}
		catch (IOException ioe) {
			ioe.printStackTrace ();
		}
//		catch (ParserException pe) {
//			pe.printStackTrace ();
//		}
	}
}
	