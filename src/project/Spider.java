package project;

import java.util.Queue;
import java.util.List;
import java.util.LinkedList;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;

import project.InvertedIndex;

class urlTemp {
	String url;
	int parent;
	urlTemp() {}
	urlTemp(String url, int parent) {
		this.url = url;
		this.parent = parent;
	}
}

public class Spider
{
	private String url;
	private int pages;
	private InvertedIndex index;
	private InvertedIndex urlIdIndex;
	
	Spider(String _url, int _pages) throws IOException {
		url = _url;
		pages = _pages;
		index = new InvertedIndex("idUrl","ht1");
		urlIdIndex = new InvertedIndex("urlId","ht1");
	}
	
	public void crawl() throws IOException {
		Queue<urlTemp> links = new LinkedList<urlTemp>();
		List<String> crawled = new LinkedList<String>();
		links.add(new urlTemp(this.url, -1));
		
		crawl_recursive(links, crawled, this.pages);
		
		index.printAll(); // DEBUG
		print("\nComplete! " + pages + " pages crawled in total.");
	}
	
	private void crawl_recursive(Queue<urlTemp> links, List<String> crawled, int numPages) throws IOException {
		if(links.isEmpty() || numPages < 1) return;
				
		urlTemp cur = links.remove();
		
		String entryId = urlIdIndex.getEntryString(cur.url);
		
		if(entryId == null) { // if not crawled yet
			urlInfo info = new urlInfo(cur.url, cur.parent);
			
			entryId = Integer.toString(index.count());
			print(numPages + "/" + this.pages + " pages remaining. Crawling " + info.url + "...");
			
			links = extractLinks(links, info, entryId, cur, false); 

			crawl_recursive(links, crawled, numPages-1);
			
		} else {
			urlInfo entry = index.getEntry(entryId);
			print("Crawled already! (" + entry.url + " #" + entry.key + ")");
			
			links = extractLinks(links, entry, entryId, cur, true);
			
			crawl_recursive(links, crawled, numPages);
		}
	}
	
	public Queue<urlTemp> extractLinks(Queue<urlTemp> links, urlInfo info, String entryId, urlTemp cur, boolean crawled) {
		try {
			Connection.Response cr = Jsoup.connect(info.url).ignoreContentType(true).execute();
			Document doc = cr.parse();
			String previousLastModified = info.lastModified;
			info.lastModified = cr.header("Last-Modified");

			if(!crawled || (info.lastModified != null && !info.lastModified.equals(previousLastModified))) {
				Elements urls = doc.select("a[href]");
				info.title = doc.title();
				info.key = entryId;

				for (Element a : urls) {
					String current = a.attr("abs:href");
					links.add(new urlTemp(current, Integer.parseInt(entryId)));
		        }
			}

			if(cur.parent != -1) {
				info.addParent(cur.parent);

				urlInfo tmp = index.getEntry(Integer.toString(cur.parent));
				tmp.addChildren(Integer.parseInt(entryId));
				index.addEntry(tmp.key, tmp);
			}
			
			index.addEntry(info.key, info);
			if(!crawled) urlIdIndex.addEntry(info.url, info.key);
		}
		catch(UnsupportedMimeTypeException mte) {
			System.out.println(mte.toString());
		}
		catch(SocketTimeoutException ste) {
			System.out.println(ste.toString()); // TODO: Implement retry?
		}
		catch(HttpStatusException hse) {
			System.out.println(hse.toString()); // TODO: Skip
		}
		catch(Exception e) {
			System.out.println("Generic exception: " + e.toString());
		}
		
		return links;
	}
	
	public InvertedIndex Index() {
		return index;
	}
	
	public void print(String s) {
		System.out.println(s);
	}
	
	public static void main (String[] args) {
		try {
			Spider crawler = new Spider("http://www.cse.ust.hk/", 100);
			crawler.crawl();
		}
		catch (IOException ioe) {
			ioe.printStackTrace ();
		}
	}
}
	