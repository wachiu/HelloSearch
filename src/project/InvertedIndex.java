package project;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.io.IOException;
import java.io.Serializable;

class urlInfo implements Serializable {
	public String key;
	public String url;
	public String title;
	public int size;
	public List<Integer> parent;
	public List<Integer> children;
	public String lastModified;
	
	
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
	
//	public urlInfo(String url, String title, int size, int[] children, int parent) {
//		this.url = url;
//		this.title = title;
//		this.size = size;
//		this.children = children;
//		this.parent = parent;	
//	}
//	
//	public urlInfo(String url, String title, int size, int[] children, int parent, String lastModified) {
//		this.url = url;
//		this.title = title;
//		this.size = size;
//		this.children = children;
//		this.parent = parent;
//		this.lastModified = lastModified;
//	}
}

public class InvertedIndex
{
	private RecordManager recman;
	private HTree hashtable;

	public InvertedIndex(String recordmanager, String objectname) throws IOException
	{
		recman = RecordManagerFactory.createRecordManager(recordmanager);
		long recid = recman.getNamedObject(objectname);
			
		if (recid != 0)
			hashtable = HTree.load(recman, recid);
		else
		{
			hashtable = HTree.createInstance(recman);
			recman.setNamedObject(objectname, hashtable.getRecid() );
		}
	}

	public void finalize() throws IOException
	{
		recman.commit();
		recman.close();		
	}

	public void addEntry(String id, String newEntry) throws IOException
	{
		hashtable.put(id, newEntry);
		recman.commit();
	}
	public void addEntry(String id, urlInfo newEntry) throws IOException {
		hashtable.put(id, newEntry);
		recman.commit();
	}
	public urlInfo getEntry(String key) throws IOException {
		return (urlInfo)hashtable.get(key);
	}
	public String getEntryString(String key) throws IOException {
		return (String)hashtable.get(key);
	}
	public void delEntry(String word) throws IOException
	{
		// Delete the word and its list from the hashtable
		hashtable.remove(word);
	} 
	public void printAll() throws IOException
	{
		// Print all the data in the hashtable
		FastIterator iter = hashtable.keys();
		String key;
		while((key = (String)iter.next()) != null) {
			urlInfo info = (urlInfo)hashtable.get(key);
			System.out.println("\n#" + key + ": " + info.url + " (" + info.title + ") " + info.lastModified);
			System.out.print("Parent ID(s): ");
			for(int i = 0; i < info.parent.size(); i++) {
				System.out.print((info.parent.get(i)) + " ");
			}
			System.out.print("\nChildren ID(s): ");
			for(int i = 0; i < info.children.size(); i++) {
				System.out.print((info.children.get(i)) + " ");
			}
			if(info.children.size() == 0) System.out.print("None");
		}
	}	
	public int count() throws IOException {
		FastIterator iter = hashtable.keys();
		int count = 0;
		while((String)iter.next() != null) {
			count++;
		}
		return count;
	}
	public boolean exists(String _url) throws IOException {
		FastIterator iter = hashtable.values();
		urlInfo info;
		while((info = (urlInfo)iter.next()) != null) {
			if(info.url.equals(_url)) return true;
		}
		return false;
	}
//	public static void main(String[] args)
//	{
//		try
//		{
//			InvertedIndex index = new InvertedIndex("lab1","ht1");
//	
//			index.addEntry("cat", 2, 6);
//			index.addEntry("dog", 1, 33);
//			System.out.println("First print");
//			index.printAll();
//			
//			index.addEntry("cat", 8, 3);
//			index.addEntry("dog", 6, 73);
//			index.addEntry("dog", 8, 83);
//			index.addEntry("dog", 10, 5);
//			index.addEntry("cat", 11, 106);
//			System.out.println("Second print");
//			index.printAll();
//			
//			index.delEntry("dog");
//			System.out.println("Third print");
//			index.printAll();
//			index.finalize();
//		}
//		catch(IOException ex)
//		{
//			System.err.println(ex.toString());
//		}
//
//	}
}