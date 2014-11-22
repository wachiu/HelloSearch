package project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.lang.Math.*;

import jdbm.helper.FastIterator;

class VectorScoreComparator implements Comparator<VectorScore> {

	@Override
	public int compare(VectorScore arg0, VectorScore arg1) {
		// TODO Auto-generated method stub
		if(arg0.score > arg1.score)
			return -1;
		else if(arg0.score == arg1.score)
			return 0;
		else
			return 1;
	}
	
}


public class VectorSpace {
	private ArrayList<String> query;
	private InvertedIndex bodyId;
	private InvertedIndex titleId;
	private InvertedIndex idBody;
	private InvertedIndex idTitle;
	private StopStem stopStem;
	private ArrayList<VectorScore> similarity;
	public VectorSpace(ArrayList<String> query) {
		this.query = query;
		try {
			this.stopStem = new StopStem("stopwords.txt");
			this.bodyId = new InvertedIndex("bodyId", "ht1");
			this.idBody = new InvertedIndex("idBody", "ht1");
			this.titleId = new InvertedIndex("titleId", "ht1");
			this.idTitle = new InvertedIndex("idTitle", "ht1");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.similarity = new ArrayList<VectorScore>();
	}
	private Boolean checkSimilarity(String check) {
		Boolean result = false;
		VectorScore temp;
		for(int i = 0; i < similarity.size();i++) {
			temp = similarity.get(i);
			if(temp.urlId.equals(check))
				result = true;
		}
		return result;
	}

	private void setSimilarity(String check, double input) {
		VectorScore temp;
		for(int i = 0; i < similarity.size();i++) {
			temp = similarity.get(i);
			if(temp.urlId == check) {
				temp.score += input;
				similarity.set(i, temp);
			}
		}
	}
	
	
	public ArrayList<VectorScore>  compute() throws IOException {
		String tempWordId;
		Word tempWord;
		String tempUrlId;
		double tempWeight;
		String next;
		LinkedList<Posting> tempList;
		ListIterator<Posting> iter;
		ListIterator<String> qIter = query.listIterator();
		Posting tempPosting;
		while(qIter.hasNext()) {
			next = qIter.next();
			if(stopStem.isStopWord(next))
				continue;
			else
				next = stopStem.stem(next);
			//check if the word exists in bodyId hashtable
			if(!bodyId.exists(next))
				continue;
			tempWordId = (String)bodyId.getEntryObject(next);
			//check if the word id exists in the idBody hashtable
			if(!idBody.exists(tempWordId)) continue;
			tempWord = (Word)idBody.getEntryObject(tempWordId);
			
			
			tempList = tempWord.getAllPostings();
			
			iter = tempList.listIterator();
			while(iter.hasNext()) {
				tempPosting = iter.next();
				tempWeight = ((double)tempPosting.tf() / (double)tempWord.maxTf()) * (Math.log10((double)(300.00/tempWord.df()))/Math.log10(2.00));
				if(checkSimilarity(tempPosting.getDocumentId())) {
					setSimilarity(tempPosting.getDocumentId(),tempWeight);
				}
				else {
					similarity.add(new VectorScore(tempPosting.getDocumentId(), tempWeight));
				}	
			}
		}
		
		qIter = query.listIterator();
		while(qIter.hasNext()) {
			next = qIter.next();
			
			if(stopStem.isStopWord(next))
				continue;
			else
				next = stopStem.stem(next);
			//check if the word exists in bodyId hashtable
			if(!titleId.exists(next))
				continue;
			tempWordId = (String)titleId.getEntryObject(next);
			
			//check if the word id exists in the idBody hashtable
			if(!idTitle.exists(tempWordId)) continue;
			tempWord = (Word)idTitle.getEntryObject(tempWordId);
			
			
			tempList = tempWord.getAllPostings();
			iter = tempList.listIterator();
			while(iter.hasNext()) {
				tempPosting = iter.next();
				tempWeight = ((double)tempPosting.tf() / (double)tempWord.maxTf()) * (Math.log10((double)(300.00/tempWord.df()))/Math.log10(2.00));
				if(checkSimilarity(tempPosting.getDocumentId())) {
					setSimilarity(tempPosting.getDocumentId(),tempWeight);
				}
				else {
					similarity.add(new VectorScore(tempPosting.getDocumentId(), tempWeight));
				}	
			}
		}
		Comparator comparator = new VectorScoreComparator();
		Collections.sort(this.similarity, comparator);
		Ranking merge = new Ranking(this.similarity);
		this.similarity = merge.compute();
//		for(VectorScore o : this.similarity) {
//			System.out.print(o.urlId + " " + o.score);
//			System.out.println();
//		}
		
		return this.similarity;
	}
}
