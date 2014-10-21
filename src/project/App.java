package project;

import java.io.IOException;
import java.util.LinkedList;

import jdbm.helper.FastIterator;

public class App {

	private Indexer indexer;
	private Spider spider;
	
	public App() {
		indexer = new Indexer();
		try {
			spider = new Spider("http://www.cse.ust.hk/", 30, indexer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			spider.crawl();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void finalize() {
		spider.finalize();
		indexer.finalize();
	}
	
	public static void test() {
		try {
			//InvertedIndex index = new InvertedIndex("idUrl", "ht1");
			//InvertedIndex index = new InvertedIndex("urlId", "ht1");
			//InvertedIndex index = new InvertedIndex("titleId", "ht1");
			//InvertedIndex index = new InvertedIndex("idTitle", "ht1");
			InvertedIndex index = new InvertedIndex("bodyId", "ht1");
			
			FastIterator iter = index.getIteratorVals();
			Object obj;
			Word w;
			urlInfo u;
			
			for(int i = 0; i < 2000 && (obj = iter.next()) != null; i++) {
				//1.
				//System.out.println(obj);
				//u = (urlInfo)obj;
				//System.out.println(u.key + "," + u.url + "," + u.title + "," + u.lastModified + "," + u.size + "," + u.parent + "," + u.children);
				
				//2.
				//System.out.println(obj);

				//3.
				//System.out.println(obj);
				
				//4.
				//LinkedList<Posting> p = ((Word)obj).getPosting();
				//System.out.println(p.get(0).getDocumentId());
				//System.out.println(p.get(0).getPositions());
				//System.out.println(p.get(1).getDocumentId());
				//System.out.println(p.get(1).getPositions());	

				//5.
				System.out.println(obj);
				
				//6.
				//LinkedList<Posting> p = ((Word)obj).getPosting();
				//System.out.println(((Word)obj).getWord());
				//System.out.println(p.get(0).getDocumentId());
				//System.out.println(p.get(0).getPositions());
				//System.out.println(p.get(1).getDocumentId());
				//System.out.println(p.get(1).getPositions());	
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main (String[] args) {
		//*//
		App app = new App();
		app.run();
		app.finalize();
		/*/
		App.test();
		//*/
	}
	
}
