package project;

import java.util.Queue;
import java.util.List;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
		
		index.printAll(); // DEBUG
		print("\nComplete! " + pages + " pages crawled in total.");
	}
	
	private void crawl_recursive(Queue<String> links, List<String> crawled, int numPages) throws IOException {
		if(links.isEmpty() || numPages < 1) return;
		
		String _url = links.remove();
		urlInfo info = new urlInfo();
		info.url = _url;
		
		// TODO: Extract keywords and insert to inverted file (Indexer)
				
		print(numPages + "/" + this.pages + " pages remaining. Crawling " + _url + "...");
				
		Document doc = Jsoup.connect(_url).get();
		Elements urls = doc.select("a[href]");
		info.title = doc.title();
		
        for (Element a : urls) {
        	String current = a.attr("abs:href");
        	if(!crawled.contains(current) && !urls.contains(current) && !_url.equals(current))
        		links.add(current);
        }
				
        if(!index.exists(_url))
			index.addEntry(Integer.toString(index.count()), info);
        
        crawled.add(_url);
		crawl_recursive(links, crawled, numPages-1);
	}
	
	public InvertedIndex Index() {
		return index;
	}
	
	public void print(String s) {
		System.out.println(s);
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
	