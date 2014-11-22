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
		if(arg0.getMatchTerms() > arg1.getMatchTerms())
			return -1;
		else if(arg0.getMatchTerms() < arg1.getMatchTerms())
			return 1;
		else {
			if(arg0.getScore() > arg1.getScore())
				return -1;
			else if(arg0.getScore() == arg1.getScore())
				return 0;
			else
				return 1;
		}
	}
	
}


public class VectorSpace {
	private ArrayList<String> query;
	private ArrayList<String> phase;
	private InvertedIndex bodyId;
	private InvertedIndex titleId;
	private InvertedIndex idBody;
	private InvertedIndex idTitle;
	private StopStem stopStem;
	private ArrayList<VectorScore> similarity;
	public VectorSpace(ArrayList<String> query, ArrayList<String> phase) {
		this.query = query;
		this.phase = phase;
		this.stopStem = GlobalFile.stopStem();
		this.bodyId = GlobalFile.bodyId();
		this.idBody = GlobalFile.idBody();
		this.titleId = GlobalFile.titleId();
		this.idTitle = GlobalFile.idTitle();
		this.similarity = new ArrayList<VectorScore>();
	}
	private Boolean checkSimilarity(String check) {
		VectorScore temp;
		for(int i = 0; i < similarity.size();i++) {
			temp = similarity.get(i);
			if(temp.urlId.equals(check))
				return true;
		}
		return false;
	}

	private void setSimilarity(String check, double input, Boolean fromBody) {
		VectorScore temp;
		for(int i = 0; i < similarity.size();i++) {
			temp = similarity.get(i);
			if(temp.urlId.equals(check)) {
				temp.score += input;
				
				//only increase the match term if the method call from body
				if(fromBody)
					temp.matchTerms++;
				
				similarity.set(i, temp);
				break;
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
					setSimilarity(tempPosting.getDocumentId(),tempWeight, true);
				}
				else {
					similarity.add(new VectorScore(tempPosting.getDocumentId(), tempWeight, 1));
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
					setSimilarity(tempPosting.getDocumentId(),tempWeight, false);
				}
				else {
					similarity.add(new VectorScore(tempPosting.getDocumentId(), tempWeight, 1));
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
