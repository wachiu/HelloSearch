package project;

import java.io.IOException;
import jdbm.helper.FastIterator;

public class AutoComplete {
	private InvertedIndex idUrlIndex;
	private InvertedIndex idPageRankIndex;
	private InvertedIndex idPageRankPrevIndex;
	private double d;
	private int iterations;
	
	public AutoComplete(String dictionary) {
		
	}
	
	public void printAll() throws IOException{
		FastIterator iter = idPageRankIndex.getIteratorKeys();
		String key;
		while((key = (String)iter.next()) != null) {
			double r = (Double)idPageRankIndex.getEntryObject(key);
			System.out.println("#" + key + ": " + r);
		}
	}
	
	public static void main(String[] args) {
		PageRank pr = new PageRank(0.85, 8);
		try {
			pr.compute();
		}
		catch (IOException ioe) {}
	}
}
