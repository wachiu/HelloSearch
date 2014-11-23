package project;

public class VectorScore {
	String urlId;
	double score;
	int matchTerms;
	
	VectorScore(String id, double temp, int matchTerms) {
		this.urlId = id;
		this.score = temp;
		this.matchTerms = matchTerms;
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
