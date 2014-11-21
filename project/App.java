package project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import jdbm.helper.FastIterator;


public class App {

	private Indexer indexer;
	private Spider spider;
	
	public App() {

	}
	
	public void run() {
		indexer = new Indexer();
		try {
			spider = new Spider("http://www.cse.ust.hk/~ericzhao/COMP4321/TestPages/testpage.htm", 300, indexer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	
	public void search(String query) throws IOException {
		
		ArrayList<String> al = new ArrayList<String>(Arrays.asList(query.split(" ")));
		
		VectorSpace vs = new VectorSpace(al);
			
		ArrayList<VectorScore> ss = vs.compute();
			
		JSONArray ja = new JSONArray();
		for(VectorScore vso:ss)
			ja.put(new JSONObject(new QueryInfo(vso)));
		System.out.println(ja.toString());
		
	}
	
	public static void test() {
		try {
			InvertedIndex index = new InvertedIndex("idUrl", "ht1");
			//InvertedIndex index = new InvertedIndex("urlId", "ht1");
			//InvertedIndex index = new InvertedIndex("titleId", "ht1");
			//InvertedIndex index = new InvertedIndex("idTitle", "ht1");
			//InvertedIndex index = new InvertedIndex("bodyId", "ht1");
			
			FastIterator iter = index.getIteratorVals();
			Object obj;
			Word w;
			urlInfo u;
			
			for(int i = 0; i < 5 && (obj = iter.next()) != null; i++) {
				//1.
				System.out.println(obj);
				u = (urlInfo)obj;
				System.out.println(u.key + "," + u.url + "," + u.title + "," + u.lastModified + "," + u.size + "," + u.parent + "," + u.children);
				System.out.println(u.titleWordIds + "," + u.bodyWordIds);
				
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
				//System.out.println(obj);
				
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
		
		if(args.length >= 2 && (args[0].equals("query") || args[0].equals("search"))) {
			try {
				app.search(args[1].toLowerCase());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(args.length == 1 && args[0].equals("index")) {
			app.run();
			app.finalize();
		}
		/*/
		App.test();
		//*/
	}
	
}
