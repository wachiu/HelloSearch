package project;

import java.io.IOException;

public class GlobalFile {

	private static Boolean isInit = false;
	private static InvertedIndex idUrl;
	private static InvertedIndex idBody;
	private static InvertedIndex bodyId;
	private static InvertedIndex idTitle;
	private static InvertedIndex titleId;
	private static StopStem stopStem;
	
	public static void init() throws IOException {
		if(GlobalFile.isInit) return;
		
		GlobalFile.idUrl = new InvertedIndex("idUrl", "ht1");
		GlobalFile.idBody = new InvertedIndex("idBody", "ht1");
		GlobalFile.bodyId = new InvertedIndex("bodyId", "ht1");
		GlobalFile.idTitle = new InvertedIndex("idTitle", "ht1");
		GlobalFile.titleId = new InvertedIndex("titleId", "ht1");
		GlobalFile.stopStem = new StopStem("stopwords.txt");
		
		GlobalFile.isInit = true;
	}
	
	public static InvertedIndex idUrl() {
		return GlobalFile.idUrl;
	}
	
	public static InvertedIndex idBody() {
		return GlobalFile.idBody;
	}
	
	public static InvertedIndex bodyId() {
		return GlobalFile.bodyId;
	}
	
	public static InvertedIndex idTitle() {
		return GlobalFile.idTitle;
	}
	
	public static InvertedIndex titleId() {
		return GlobalFile.titleId;
	}
	
	public static StopStem stopStem() {
		return GlobalFile.stopStem;
	}
}
