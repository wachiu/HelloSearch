package project;

import java.util.ArrayList;
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
import java.io.Serializable;
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

class urlInfo implements Serializable {
	public String key;
	public String url;
	public String title;
	public List<Integer> parent;
	public List<Integer> children;
	public String lastModified;
	public int size;
	
	
	public urlInfo(String url, int parent) {
		this.parent = new ArrayList<Integer>();
		this.children = new ArrayList<Integer>();
		this.addParent(parent);
		this.url = url;
	}
	
	public void addParent(int id) {
		if(!parent.contains(id) && id != -1) this.parent.add(id);
	}
	public void addChildren(int id) {
		if(!children.contains(id) && id != -1) this.children.add(id);
	}
}

public class Spider
{
	private String url;
	private int pages;
	private Indexer indexer;
	private InvertedIndex index;
	private InvertedIndex urlIdIndex;
	
	Spider(String url, int pages, Indexer indexer) throws IOException {
		this.url = url;
		this.pages = pages;
		index = new InvertedIndex("idUrl","ht1");
		urlIdIndex = new InvertedIndex("urlId","ht1");
		this.indexer = indexer;
	}
	
	public void crawl() throws IOException {
		Queue<urlTemp> links = new LinkedList<urlTemp>();
		links.add(new urlTemp(this.url, -1));
		
		crawl_recursive(links, this.pages);
		
		index.printAll(); // DEBUG
		print("\nComplete! " + pages + " pages crawled in total.");
	}
	
	private void crawl_recursive(Queue<urlTemp> links, int numPages) throws IOException {
		if(links.isEmpty() || numPages < 1) return;
				
		urlTemp cur = links.remove();
		
		String entryId = (String)urlIdIndex.getEntryObject(cur.url);
		
		if(entryId == null) { // if not crawled yet
			urlInfo info = new urlInfo(cur.url, cur.parent);
			
			entryId = Integer.toString(index.count());
			print(numPages + "/" + this.pages + " pages remaining. Crawling " + info.url + "...");
			
			try {
				links = extractLinks(links, info, entryId, cur, false);
				crawl_recursive(links, numPages-1);
			}
			catch(Exception e) {
//				System.out.println("Exception: " + e.toString());
				crawl_recursive(links, numPages);
			}
			
			
		} else {
			urlInfo entry = (urlInfo)index.getEntryObject(entryId);
			print("Crawled already! (" + entry.url + " #" + entry.key + ")");
			
			try {
				links = extractLinks(links, entry, entryId, cur, true);
			}
			catch(Exception e) {
//				System.out.println("Exception: " + e.toString());
			}
			
			crawl_recursive(links, numPages);
		}
	}
	
	public Queue<urlTemp> extractLinks(Queue<urlTemp> links, urlInfo info, String entryId, urlTemp cur, boolean crawled)
		throws Exception{
//		throws UnsupportedMimeTypeException, SocketTimeoutExceptionm, HttpStatusException, Exception{

		Connection.Response cr = Jsoup.connect(info.url)
				//.ignoreContentType(true) // Ignore PDFs, etc..?
				.execute();
		Document doc = cr.parse();
		
		String previousLastModified = info.lastModified;
		info.lastModified = cr.header("Last-Modified");
		if(info.lastModified == null) info.lastModified = cr.header("Date");

		int previousSize = info.size;
		String tmpSize = cr.header("Content-Length");
		if(tmpSize == null) info.size = cr.bodyAsBytes().length;
		else info.size = Integer.parseInt(tmpSize);
		
		boolean update = (previousLastModified != null
				&& !info.lastModified.equals(previousLastModified)
				&& previousSize != 0 && previousSize != info.size);
		
		if(!crawled || update) {
			if(update) print("Update..." + info.lastModified + " :: " + previousLastModified + " -- " + info.size + " :: " + previousSize);
			
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

			urlInfo tmp = (urlInfo)index.getEntryObject(Integer.toString(cur.parent));
			tmp.addChildren(Integer.parseInt(entryId));
			index.addEntry(tmp.key, tmp);
		}
		
		index.addEntry(info.key, info);
		if(!crawled) {
			urlIdIndex.addEntry(info.url, info.key);
			indexer.IndexPage(entryId, doc.body().toString());
		}
				
		return links;
	}
	
	public void print(String s) {
		System.out.println(s);
	}
	
	public void finalize() {
		try {
			index.finalize();
			urlIdIndex.finalize();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*public static void main (String[] args) {
		try {
			Spider crawler = new Spider("http://www.cse.ust.hk/", 100);
			crawler.crawl();
		}
		catch (IOException ioe) {
			ioe.printStackTrace ();
		}
	}*/
}
	