package project;

import java.io.IOException;
import jdbm.helper.FastIterator;

public class PageRank {
	private InvertedIndex idUrlIndex;
	private InvertedIndex idPageRankIndex;
	private InvertedIndex idPageRankPrevIndex;
	private double d;
	private int iterations;
	
	public PageRank(double d, int iterations) {
		try {
			this.d = d;
			this.iterations = iterations;
			this.idUrlIndex = new InvertedIndex("idUrl", "ht1");
			this.idPageRankIndex = new InvertedIndex("idPageRank", "ht1");
			this.idPageRankPrevIndex = new InvertedIndex("idPageRankPrev", "ht1");
		}
		catch (IOException ioe) {
			ioe.printStackTrace ();
		}
	}
	/* 	PageRank Formula:
		PR(A) = (1-d) + d(PR(T1)/C(T1)+...+PR(Tn)/C(Tn))
	*/
	public boolean compute() throws IOException{		
		String key;
		urlInfo url;
		FastIterator iter = idUrlIndex.getIteratorKeys();

		while((key = (String)iter.next()) != null)
			idPageRankIndex.addEntry(key, 1.0); // Initial PR is 1 for all pages
		
		for(int i = 0; i < iterations; i++) {
			updatePrev();
			
			iter = idUrlIndex.getIteratorVals();
			while((url = (urlInfo)iter.next()) != null) {
				double intermediate = 0.0;
				for(int T: url.parent) {
					if(T == Integer.parseInt(url.key)) continue;
					urlInfo infoT = (urlInfo)idUrlIndex.getEntryObject(Integer.toString(T));
					double pageRankOfT = (Double)idPageRankPrevIndex.getEntryObject(Integer.toString(T));
					double numOfTChildren = infoT.children.size();
					if(infoT.children.contains(T))
						numOfTChildren = infoT.children.size() - 1;
					
					intermediate += pageRankOfT/numOfTChildren;
				}
				double pageRank = (1-d) + d*intermediate;
				idPageRankIndex.addEntry(url.key,pageRank);
			}
		}
		printAll();
		return true;
	}
	
	public void updatePrev() throws IOException {
		FastIterator iter = idPageRankIndex.getIteratorKeys();
		String key;
		while((key=(String)iter.next()) != null) {
			idPageRankPrevIndex.addEntry(key,idPageRankIndex.getEntryObject(key));
		}
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
