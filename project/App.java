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
		//crawler and indexer
		
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
		
		//page rank
		PageRank pr = new PageRank(0.85, 8);
		try {
			pr.compute();
		}
		catch (IOException ioe) {}
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
