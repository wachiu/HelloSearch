package project;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

import org.htmlparser.beans.StringBean;

class Word implements Serializable {

	private String word;
	private LinkedList<Posting> lists;
	
	
	public Word(String word) {
		this.word = word;
		this.lists = new LinkedList<Posting>();
	}
	
	public void addPosting(String documentId, int position) {
		Posting p = listContains(documentId);
		if(p == null) { // if posting not exists, create one
			p = new Posting(documentId);
			lists.addLast(p);
		}
		p.addPosition(position);
		
		/*System.out.println("--" + this.word + "--");
		for(Posting tmp : lists) {
			System.out.println(tmp.getDocumentId() + ": " + tmp.getPositions());
		}*/
	}
	
	public void removePosting(String id) {
		LinkedList<Posting> new_lists = new LinkedList<Posting>();
		
		for(Posting tmp : lists) {
			if(!tmp.getDocumentId().equals(id)) {
				new_lists.add(tmp);
			}
		}
		
		lists = new_lists;
	}
	
	private Posting listContains(String documentId) {
		for(Posting tmp : lists) {
			if(tmp.getDocumentId().equals(documentId)) return tmp; 
		}
		return null;
	}
}

class Posting implements Serializable {
	private String documentId;
	private LinkedList<Integer> positions;
	
	public Posting(String documentId) {
		this.documentId = documentId;
		this.positions = new LinkedList<Integer>();
	}
	
	public void addPosition(int position) {
		positions.addLast(position);
	}
	
	public String getDocumentId() {
		return documentId;
	}
	
	public String getPositions() {
		String p = "";
		for(int i : this.positions) {
			p += i + ", ";
		}
		return p;
	}
	
}

public class Indexer {

	private StopStem stopStem;
	private InvertedIndex bodyIndex;
	private InvertedIndex titleIndex;;
	private int bodywordId;
	private int titlewordId;
	
	public Indexer() {
		bodywordId = 0;
		titlewordId = 0;
		try {
			stopStem = new StopStem("stopwords.txt");
			bodyIndex = new InvertedIndex("bodyId", "ht1");
			titleIndex = new InvertedIndex("titleId", "ht1");
		}
		catch (IOException ioe) {
			ioe.printStackTrace ();
		}
	}
	
	public void IndexPage(String id, String title, String body) {
		//body = body.replaceAll("<[^>]*>", "");	//remove all tags
		
		//index title first
		Matcher m = Pattern.compile("([A-Za-z0-9']+)").matcher(title);
		
		int position = 0;
		while(m.find()) {
		    String text = m.group(1);
		    if(stopStem.isStopWord(text)) continue;
		    else text = stopStem.stem(text);
		    
		    try {
		    	Word w;
				if(!titleIndex.exists(text)) {
					w = new Word(text);
					titleIndex.addEntry(text, "" + (++titlewordId));
					//pageIndex.addEntry("" + wordId, w);
				}
				else {
					w = (Word)titleIndex.getEntryObject(text);
				}
				w.addPosting(id, ++position);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    //System.out.println(id + ":" + text);
		}
		
		//then index body
		m = Pattern.compile("([A-Za-z0-9']+)").matcher(body);
		
		position = 0;
		while(m.find()) {
		    String text = m.group(1);
		    if(stopStem.isStopWord(text)) continue;
		    else text = stopStem.stem(text);
		    
		    try {
		    	Word w;
				if(!bodyIndex.exists(text)) {
					w = new Word(text);
					bodyIndex.addEntry(text, "" + (++bodywordId));
					//pageIndex.addEntry("" + wordId, w);
				}
				else {
					w = (Word)bodyIndex.getEntryObject(text);
				}
				w.addPosting(id, ++position);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    //System.out.println(id + ":" + text);
		}
	}
	
	public void UpdateIndex(String id, String title, String body) {
		FastIterator iter = titleIndex.getIterator();
		Word p;
		while((p = (Word)iter.next()) != null) {
			p.removePosting(id);
		}
		
		iter = bodyIndex.getIterator();
		while((p = (Word)iter.next()) != null) {
			p.removePosting(id);
		}
		
		IndexPage(id, title, body);
	}
	
	public void finalize() {
		try {
			bodyIndex.finalize();
			titleIndex.finalize();
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
}
