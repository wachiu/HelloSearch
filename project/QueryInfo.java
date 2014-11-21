package project;

import java.io.IOException;


public class QueryInfo {

	private VectorScore vso;
	private InvertedIndex idUrl;
	//private InvertedIndex urlId;
	private urlInfo info;
	
	public QueryInfo(VectorScore vso) throws IOException {
		this.vso = vso;
		this.idUrl = new InvertedIndex("idUrl", "ht1");
		//this.urlId = new InvertedIndex("urlId", "ht1");
		info = (urlInfo)idUrl.getEntryObject(vso.getUrlId());
	}
	
	public double getScore() {
		return vso.getScore();
	}
	
	public String getUrlId() {
		return vso.getUrlId();
	}
	
	public String getUrl() {
		return info.url;
	}
	
	public String getPageTitle() {
		return info.title;
	}
	
	public int getSize() {
		return info.size;
	}
	
	public String getLastModified() {
		return info.lastModified;
	}
	
}
