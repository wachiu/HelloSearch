package project;

import java.util.ArrayList;

public class VectorScore {
	String urlId;
	double score;
	int matchTerms;
	ArrayList<String> matchStrings;
	
	VectorScore(String id, double temp) {
		this.urlId = id;
		this.score = temp;
		this.matchTerms = 0;
		this.matchStrings = new ArrayList<String>();
	}
	
	public VectorScore setMatchString(String text) {
		if(this.matchStrings.indexOf(text) == -1) {
			this.matchTerms++;
			this.matchStrings.add(text);
		}
		return this;
	}
	
	public int getMatchTerms() {
		return this.matchTerms;
	}
	
	public double getScore() {
		return score;
	}
	
	public String getUrlId() {
		return urlId;
	}
	
}
