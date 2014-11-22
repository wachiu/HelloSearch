package project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class QueryInfo {

	private VectorScore vso;
	private InvertedIndex idUrl;
	//private InvertedIndex urlId;
	private urlInfo info;
	private InvertedIndex idBodyIndex;
	private ArrayList<String> queryString;
	private InvertedIndex bodyIdIndex;
	private StopStem stopStem;
	
	public QueryInfo(VectorScore vso, ArrayList<String> queryString) throws IOException {
		this.vso = vso;
		this.idUrl = GlobalFile.idUrl();
		this.idBodyIndex = GlobalFile.idBody();
		this.bodyIdIndex = GlobalFile.bodyId();
		//this.urlId = new InvertedIndex("urlId", "ht1");
		info = (urlInfo)idUrl.getEntryObject(vso.getUrlId());
		this.queryString = new ArrayList<String>();
		stopStem = GlobalFile.stopStem();
		for(String s : queryString) {
			if(stopStem.isStopWord(s)) continue;
			this.queryString.add(stopStem.stem(s));
		}
	}
	
	public double getScore() {
		return vso.getScore();
	}
	
	public String getUrlId() {
		return vso.getUrlId();
	}
	
	public String getUrl() {
		return info.url;
	}
	
	public String getPageTitle() {
		return info.title;
	}
	
	public int getSize() {
		return info.size;
	}
	
	public String getLastModified() {
		return info.lastModified;
	}
	
	public String getWordFreqs() {
		JSONArray ja = new JSONArray();
		TreeMap<String, Integer> map = info.getBodyUniqCount();
		
		int i = 0;
		for(Entry<String, Integer> e : map.entrySet()) {
			ja.put(e);
			if(i++ >= 4) break;
		}
		
		return ja.toString();
	}
	
	public String getChildrenLinks() throws IOException {
		List<Integer> children = info.children;
		
		JSONArray ja = new JSONArray();
		
		for(Integer id : children) {
			ja.put(((urlInfo)idUrl.getEntryObject(id + "")).url);
		}
		
		return ja.toString();
	}
	
	public String getParentLinks() throws IOException {
		List<Integer> parent = info.parent;
		
		JSONArray ja = new JSONArray();
		
		for(Integer id : parent) {
			ja.put(((urlInfo)idUrl.getEntryObject(id + "")).url);
		}
		
		return ja.toString();
	}
	
	public String getDocumentText() throws IOException {
		if(queryString.size() == 0) return "";
		String text = "";
		String curText = "";
		int firstposition;
		LinkedList<Integer> positions = null;
		ArrayList<String> documentText = null;
		
		for(int i = 0; i < queryString.size(); i++) {
			String word = queryString.get(i);
			
			Word w = (Word)this.idBodyIndex.getEntryObject(
				//id
				(String)this.bodyIdIndex.getEntryObject(word)
			);
			
			Posting posting = w.getPosting(this.getUrlId());
			if(posting == null) continue;
			
			positions = posting.getPositionsByList();
			documentText = info.getDocumentText();
			
			if(positions.size() != 0) break;
		}
		
		firstposition = positions.getFirst();
		
		for(int i = firstposition - 10; i < firstposition + 10; i++) {
			if(0 <= i && i < documentText.size()) {
				curText = documentText.get(i);
				
				if(queryString.indexOf(stopStem.stem(curText)) != -1) {
					text += "<b>" + curText + "</b> ";
				}
				else {
					text += curText + " ";
				}
			}
		}
		return text;
	}
	/*
	public String getDocumentText() {
		ArrayList<String> text = info.getDocumentText();
		
		JSONArray ja = new JSONArray();
		
		for(String s : text)
			ja.put(s);
		
		return ja.toString();
	}
	*/
	
}
