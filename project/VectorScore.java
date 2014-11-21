package project;

public class VectorScore {
	String urlId;
	double score;
	VectorScore(String id, double temp) {
		this.urlId = id;
		this.score = temp;
	}
	
	public double getScore() {
		return score;
	}
	
	public String getUrlId() {
		return urlId;
	}
	
}
