package project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.lang.Math.*;

import jdbm.helper.FastIterator;

class score {
	String urlId;
	double score;
	score(String id, double temp) {
		this.urlId = id;
		this.score = temp;
	}
}

public class VectorSpace {
	private ArrayList<String> query;
	private InvertedIndex bodyId;
	private InvertedIndex idBody;
	private InvertedIndex titleId;
	private InvertedIndex idTitle;
	private ArrayList<score> similarity;
	public VectorSpace(ArrayList<String> query, InvertedIndex bodyId, InvertedIndex idBody, InvertedIndex titleId, InvertedIndex idTitle) {
		//problem?
		this.query = query;
		this.bodyId = bodyId;
		this.idBody = idBody;
		this.titleId = titleId;
		this.idTitle = idTitle;
		this.similarity = new ArrayList<score>();
	}
	private Boolean checkSimilarity(String check) {
		Boolean result = false;
		score temp;
		for(int i = 0; i < similarity.size();i++) {
			temp = similarity.get(i);
			if(temp.urlId == check)
				result = true;
		}
		return result;
	}
	private void setSimilarity(String check, double input) {
		score temp;
		for(int i = 0; i < similarity.size();i++) {
			temp = similarity.get(i);
			if(temp.urlId == check) {
				temp.score += input;
				similarity.set(i, temp);
			}
		}
	}
	
	
	public String[]  compute() throws IOException {
		String tempWordId;
		Word tempWord;
		String tempUrlId;
		double tempWeight;
		int maxTf;
		LinkedList<Posting> tempList;
		ListIterator<Posting> iter;
		ListIterator<String> qIter = query.listIterator();
		Posting tempPosting;
		while(qIter.hasNext()) {
			tempWordId = (String)bodyId.getEntryObject(qIter.next());
			tempWord = (Word)idBody.getEntryObject(tempWordId);
			tempList = tempWord.getPosting();
			iter = tempList.listIterator();
			while(iter.hasNext()) {
				tempPosting = iter.next();
				tempWeight = (tempPosting.tf() / tempWord.maxTf()) * (Math.log10((double)(300/tempWord.df()))/Math.log10(2.00));
				if(checkSimilarity(tempPosting.getDocumentId())) {
					setSimilarity(tempPosting.getDocumentId(),tempWeight);
				}
				else {
					similarity.add(new score(tempPosting.getDocumentId(), tempWeight));
				}	
			}
		}
		qIter = query.listIterator();
		while(qIter.hasNext()) {
			tempWordId = (String)titleId.getEntryObject(qIter.next());
			tempWord = (Word)idTitle.getEntryObject(tempWordId);
			tempList = tempWord.getPosting();
			iter = tempList.listIterator();
			while(iter.hasNext()) {
				tempPosting = iter.next();
				tempWeight = (tempPosting.tf() / tempWord.maxTf()) * (Math.log10((double)(300/tempWord.df()))/Math.log10(2.00));
				if(checkSimilarity(tempPosting.getDocumentId())) {
					setSimilarity(tempPosting.getDocumentId(),tempWeight);
				}
				else {
					similarity.add(new score(tempPosting.getDocumentId(), tempWeight));
				}	
			}
		}
		
		return new String[10];
	}
	
}
