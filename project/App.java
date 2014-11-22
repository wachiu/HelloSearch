package project;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

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
		
		finalize();
		
		pagerank();
	}
	
	public void pagerank() {
		//page rank
		PageRank pr = new PageRank(0.85, 8);
		try {
			pr.compute();
			pr.finalize();
		}
		catch (IOException ioe) { }
	}
	
	public void finalize() {
		spider.finalize();
		indexer.finalize();
	}
	
	public void search(String query) throws IOException {
		
		Matcher m = Pattern.compile("\"[a-zA-Z ]+\"").matcher(query);
		
		
		ArrayList<String> al = new ArrayList<String>(Arrays.asList(query.split(" ")));
		ArrayList<String> phases = new ArrayList<String>();
		
		while(m.find()) {
			phases.add(m.group(1).toLowerCase());
		}
		
		VectorSpace vs = new VectorSpace(al, phases);
			
		ArrayList<VectorScore> ss = vs.compute();
			
		JSONArray ja = new JSONArray();
		for(VectorScore vso:ss)
			ja.put(new JSONObject(new QueryInfo(vso, al)));
		System.out.println(ja.toString());
		
	}
	
	public void suggest(String query) throws IOException {
		
		ArrayList<String> al = new ArrayList<String>(Arrays.asList(query.split(" ")));
		
		//get the last word of query
		String lastWord = al.get(al.size() - 1);
		//Map<String, String> map = new HashMap<String, String>();
		InvertedIndex idUrl = new InvertedIndex("idUrl", "ht1");
		FastIterator iter = idUrl.getIteratorKeys();
		String key;
		urlInfo val;
		ArrayList<String> suggestWords = new ArrayList<String>();
		PrintWriter writer = new PrintWriter(System.out);
		JSONWriter jsonwriter = new JSONWriter(writer).object().key("words");
		
		while((key = (String)iter.next()) != null) {
			val = (urlInfo)idUrl.getEntryObject(key);
			TreeMap<String, Integer> map = val.getBodyUniqCount();
			
			for(Entry<String, Integer> e : map.entrySet()) {
				if(e.getKey().startsWith(lastWord)) {
					if(suggestWords.indexOf(e.getKey()) != -1) continue;
					
					suggestWords.add(e.getKey());
					break;
				}
			}
			
			if(suggestWords.size() >= 5) break;
		}
		
		jsonwriter.value(suggestWords).endObject();
		
		writer.flush();
		writer.close();
	}
	
	public void stem(String words) {
		StopStem stopStem = new StopStem("stopwords.txt");
		
		ArrayList<String> al = new ArrayList<String>(Arrays.asList(words.split(" ")));
		ArrayList<String> alStem = new ArrayList<String>();
		
		PrintWriter writer = new PrintWriter(System.out);
		
		JSONWriter jsonwriter = new JSONWriter(writer).object().key("stems");
		
		for(String s : al) {
			if(stopStem.isStopWord(s)) continue;
			alStem.add(stopStem.stem(s));
		}
		
		jsonwriter.value(alStem).endObject();
		
		writer.flush();
		writer.close();
	}
	
	public static void main (String[] args) {
		if(args.length == 0) return;
		//*//
		App app = new App();
		
		//check the action that not require args
		args[0] = args[0].toLowerCase();
		if(args[0].equals("index")) {
			app.run();
		}
		//else if(args[0].equals("pagerank")) {
		//	app.pagerank();
		//}
		
		if(args.length < 2) return;
		
		//check the action with args
		args[1] = args[1].toLowerCase();
		if(args[0].equals("query") || args[0].equals("search")) {
			try {
				app.search(args[1].toLowerCase());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(args[0].equals("suggest")) {
			try {
				app.suggest(args[1].toLowerCase());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		else if(args[0].equals("stem")) {
			app.stem(args[1].toLowerCase());
		}
		
		/*/
		App.test();
		//*/
	}
	
}
