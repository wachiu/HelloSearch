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
		int id = -1;
		
		String entryId = urlIdIndex.getEntryString(cur.url);
		
		if(entryId == null) { // if not crawled
			urlInfo info = new urlInfo(cur.url, cur.parent);
			
			id = index.count();
			print(numPages + "/" + this.pages + " pages remaining. Crawling " + info.url + "...");
			
			try {
				Connection.Response cr = Jsoup.connect(info.url).execute();
				Document doc = cr.parse();
				Elements urls = doc.select("a[href]");
				info.title = doc.title();
				info.lastModified = cr.header("Last-Modified");
				info.key = Integer.toString(id);
				
				if(cur.parent != -1) {
					info.addParent(cur.parent);
					urlInfo tmp = index.getEntry(Integer.toString(cur.parent));
					tmp.addChildren(id);
					index.addEntry(tmp.key, tmp);
				}
				
				for (Element a : urls) {
		        	String current = a.attr("abs:href");
		        	links.add(new urlTemp(current, id));
		        }

				index.addEntry(Integer.toString(id), info);
				urlIdIndex.addEntry(info.url, Integer.toString(id));
			}
			catch(UnsupportedMimeTypeException mte) {
				System.out.println("UnsupportedMimeTypeException!");
			}
			catch(SocketTimeoutException ste) {
				System.out.println("Timeout"); // TODO: Implement retry?
			}
			catch(HttpStatusException hse) {
				System.out.println("HttpStatusException"); // TODO: Skip
			}
			catch(Exception e) {
				System.out.println("Generic exception");
			}
			crawl_recursive(links, crawled, numPages-1);
			
		} else {
			urlInfo entry = index.getEntry(entryId);
			print("Crawled already! (" + entry.url + " #" + entry.key + ")");
			
			// TODO: Check last modified date
			
			if(cur.parent != -1) {
				entry.addParent(cur.parent);
				index.addEntry(entry.key, entry);
				
				urlInfo tmp = index.getEntry(Integer.toString(cur.parent));
				tmp.addChildren(Integer.parseInt(entryId));
				index.addEntry(tmp.key, tmp);
			}
			
			crawl_recursive(links, crawled, numPages);
		}
	}
	
	public InvertedIndex Index() {
		return index;
	}
	
	public void print(String s) {
		System.out.println(s);
	}
	
	public static void main (String[] args) {
		try {
			Spider crawler = new Spider("http://www.cse.ust.hk/", 300);
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
	