package project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.lang.Math.*;
import java.util.HashSet;
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
	private ArrayList<String> filter;
	private ArrayList<String> matchTerms;
	
	public VectorSpace(ArrayList<String> query, ArrayList<String> phase) {
		this.query = query;
		this.phase = phase;
		this.stopStem = GlobalFile.stopStem();
		this.bodyId = GlobalFile.bodyId();
		this.idBody = GlobalFile.idBody();
		this.titleId = GlobalFile.titleId();
		this.idTitle = GlobalFile.idTitle();
		this.similarity = new ArrayList<VectorScore>();
		this.filter = new ArrayList<String>();
		this.matchTerms = new ArrayList<String>();
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

	private void setSimilarity(String check, double input, String word) {
		VectorScore temp;
		for(int i = 0; i < similarity.size();i++) {
			temp = similarity.get(i);
			if(temp.urlId.equals(check)) {
				temp.score += input;
				
				//only increase the match term if the method call from body
				temp.setMatchString(word);
				
				similarity.set(i, temp);
				break;
			}
		}
	}
	
	private Boolean checkFilter(String check) {
		String temp;
		for(int i = 0; i < filter.size();i++) {
			temp = filter.get(i);
			if(temp.equals(check)) {
				return true;
			}
		}
		return false;
	}
	
	
	public ArrayList<VectorScore>  compute() throws IOException {
		String tempWordId;
		Word tempWord;
		//String tempUrlId;
		double tempWeight;
		String next;
		LinkedList<Posting> tempList;
		ListIterator<Posting> iter;
		Posting tempPosting;
		String[] tempPhase;
		String[] tempStemPhase;
		ListIterator<String> qIter = phase.listIterator();
		Boolean checker = true;
		InvertedIndex idUrl = GlobalFile.idUrl();
		urlInfo tempUrlInfo;
		Boolean useFilter;
		
		if(phase.isEmpty())
			useFilter = false;
		else
			useFilter = true;

		////////////////////////////////////Body filter//////////////////////////////////////////////////
		while(qIter.hasNext()) {//loop Phase
			next = qIter.next();
			tempPhase = next.split(" ");
			tempStemPhase = next.split(" ");
			
			for(int i =0;i<tempPhase.length;i++) {//stem all phases
				tempStemPhase[i] = stopStem.stem(tempPhase[i]);
			}

			//check if the word exists in bodyId hashtable
			if(!bodyId.exists(tempStemPhase[0]))
				continue;
			tempWordId = (String)bodyId.getEntryObject(tempStemPhase[0]);
			
			//check if the word id exists in the idBody hashtable
			if(!idBody.exists(tempWordId)) {
				continue;
			}
			tempWord = (Word)idBody.getEntryObject(tempWordId);

			tempList = tempWord.getAllPostings();
			iter = tempList.listIterator();
			while(iter.hasNext()) {
				tempPosting = iter.next();
				tempUrlInfo = (urlInfo)idUrl.getEntryObject(tempPosting.getDocumentId());
				for(int i =0;i < tempPosting.getPositionsByList().size();i++) {
					for(int j =1;j< tempPhase.length;j++) {
						if((tempPosting.getPositionsByList().get(i)+j) < tempUrlInfo.getDocumentText().size()) {
							if(!tempPhase[j].equals(tempUrlInfo.getDocumentText().get(tempPosting.getPositionsByList().get(i)+j))) {
								checker = false;
							}
						}
						else
							checker = false;
							
					}
					if(checker) {
						filter.add(tempPosting.getDocumentId());
					}
					else
						checker = true;
				}
				
			}
			//////////////////////////////////////////////title filter///////////////////////////////////////////////////
			if(!titleId.exists(tempStemPhase[0]))
				continue;
			tempWordId = (String)titleId.getEntryObject(tempStemPhase[0]);
			
			//check if the word id exists in the idBody hashtable
			if(!idTitle.exists(tempWordId)) {
				continue;
			}
			tempWord = (Word)idTitle.getEntryObject(tempWordId);

			tempList = tempWord.getAllPostings();
			iter = tempList.listIterator();
			while(iter.hasNext()) {
				tempPosting = iter.next();
				tempUrlInfo = (urlInfo)idUrl.getEntryObject(tempPosting.getDocumentId());
				for(int i =0;i < tempPosting.getPositionsByList().size();i++) {
					for(int j =1;j< tempPhase.length;j++) {
						if((tempPosting.getPositionsByList().get(i)+j) < tempUrlInfo.getTitleText().size()) {
							if(!tempPhase[j].equals(tempUrlInfo.getTitleText().get(tempPosting.getPositionsByList().get(i)+j))) {
								checker = false;
							}
						}
						else
							checker = false;
							
					}
					if(checker) {
						filter.add(tempPosting.getDocumentId());
					}
					else
						checker = true;
				}
				
			}
			/////////////////////////////////////////////////////////////////////////////////////////////////////
		}
		HashSet hs = new HashSet();
		hs.addAll(filter);
		filter.clear();
		filter.addAll(hs);
//		for(int i = 0;i< filter.size();i++) {
//			System.out.print(filter.get(i));
//			System.out.println();
//		}
		///////////////////////////////////////////////////////////////////////////////////////////
		
		
		qIter = query.listIterator();
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
			if(!idBody.exists(tempWordId))
				continue;
			tempWord = (Word)idBody.getEntryObject(tempWordId);
			
			
			tempList = tempWord.getAllPostings();
			
			iter = tempList.listIterator();
			while(iter.hasNext()) {
				tempPosting = iter.next();
				tempWeight = (0.5+0.5*(double)tempPosting.tf() / (double)tempWord.maxTf()) * (Math.log10(1 + (double)(300.00/tempWord.df()))/Math.log10(2.00));
				if(checkSimilarity(tempPosting.getDocumentId())) {
					if(useFilter) {
						if(checkFilter(tempPosting.getDocumentId()))
							setSimilarity(tempPosting.getDocumentId(),tempWeight, tempWord.getWord());
					}
					else
						setSimilarity(tempPosting.getDocumentId(),tempWeight, tempWord.getWord());
				}
				else {
					if(useFilter) {
						if(checkFilter(tempPosting.getDocumentId())) {
							similarity.add(new VectorScore(tempPosting.getDocumentId(), tempWeight).setMatchString(tempWord.getWord()));
						}
					}
					else
						similarity.add(new VectorScore(tempPosting.getDocumentId(), tempWeight).setMatchString(tempWord.getWord()));
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
				tempWeight = (0.5 + 0.5*(double)tempPosting.tf() / (double)tempWord.maxTf()) * (Math.log10((double)(1 + 300.00/tempWord.df()))/Math.log10(2.00));
				if(checkSimilarity(tempPosting.getDocumentId())) {
					if(useFilter) {
						if(checkFilter(tempPosting.getDocumentId()))
							setSimilarity(tempPosting.getDocumentId(),tempWeight, tempWord.getWord());
					}
					else
						setSimilarity(tempPosting.getDocumentId(),tempWeight, tempWord.getWord());
				}
				else {
					if(useFilter) {
						if(checkFilter(tempPosting.getDocumentId())) {
							similarity.add(new VectorScore(tempPosting.getDocumentId(), tempWeight).setMatchString(tempWord.getWord()));
							
						}
					}
					else
						similarity.add(new VectorScore(tempPosting.getDocumentId(), tempWeight).setMatchString(tempWord.getWord()));
				}
			}
		}
		Comparator comparator = new VectorScoreComparator();
		Collections.sort(this.similarity, comparator);
		Ranking merge = new Ranking(this.similarity);
		this.similarity = merge.compute();
		
		for(int i = this.similarity.size() - 1;i >= 50; i--) {
			this.similarity.remove(this.similarity.get(i));
		}
			
//		for(VectorScore o : this.similarity) {
//			System.out.print(o.urlId + " " + o.score);
//			System.out.println();
//		}
		
		return this.similarity;
	}
}
