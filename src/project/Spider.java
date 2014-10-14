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

//import org.htmlparser.beans.LinkBean;
//import org.htmlparser.beans.StringBean;
//import org.htmlparser.Node;
//import org.htmlparser.NodeFilter;
//import org.htmlparser.Parser;
//import org.htmlparser.filters.AndFilter;
//import org.htmlparser.filters.NodeClassFilter;
//import org.htmlparser.filters.TagNameFilter;
//import org.htmlparser.tags.HeadingTag;
//import org.htmlparser.tags.LinkTag;
//import org.htmlparser.tags.TitleTag;
//import org.htmlparser.util.NodeList;
//import org.htmlparser.util.ParserException;
//import org.htmlparser.util.SimpleNodeIterator;
//import java.net.URL;

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
		System.out.println("\nComplete! " + pages + " pages crawled in total.");
	}
	
	private void crawl_recursive(Queue<String> links, List<String> crawled, int numPages) throws IOException {
		if(links.isEmpty() || numPages < 1) return;
		
		String _url = links.remove();
		urlInfo info = new urlInfo();
		info.url = _url;
		
		crawled.add(_url);
		
		// TODO: Extract keywords and insert to inverted file (Indexer)
		
		if(!index.exists(_url))
			index.addEntry(Integer.toString(index.count()), info);
		
		System.out.println(numPages + "/" + this.pages + " pages remaining. Crawling " + _url + "...");
				
		Document doc = Jsoup.connect(_url).get();
		Elements urls = doc.select("a[href]");
		
        for (Element a : urls) {
        	String current = a.attr("abs:href");
        	if(!crawled.contains(current) && !urls.contains(current))
        		links.add(current);
        }
		
//		LinkBean lb = new LinkBean();
//		lb.setURL(_url);
//		URL[] urls = lb.getLinks();
		
//		for(int i = 0; i < urls.length; i++) {
//		String current = urls[i].toString();
//		if(!crawled.contains(current) && !urls.contains(current))
//			links.add(current);
//	}
		
//		Parser p = new Parser();
//		p.main(new String[] {_url,"TITLE"});
		

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
	