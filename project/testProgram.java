package project;

//import jdbm.RecordManager;
//import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class testProgram {
	
	public static void main (String[] args) throws IOException
	{
		InvertedIndex urlIdInfo = new InvertedIndex("idUrl", "ht1");
		//InvertedIndex urlID = new InvertedIndex("urlId","ht1");
		//InvertedIndex bodyID = new InvertedIndex("bodyId", "ht1");
		//InvertedIndex titleIdIndex = new InvertedIndex("titleId", "ht1");
		InvertedIndex idBodyIndex = new InvertedIndex("idBody", "ht1");
		InvertedIndex idTitleIndex = new InvertedIndex("idTitle", "ht1");
		//InvertedIndex url2IDs = new InvertedIndex("url2IDs", "ht1");
		FastIterator cur = urlIdInfo.getIteratorKeys();
		PrintWriter writer = new PrintWriter(new FileWriter("spider_result.txt"));
		String key = null;
		//String key1 = null;
		String url = null;
		while((key = (String)cur.next()) != null)
		{
			url = ((urlInfo)urlIdInfo.getEntryObject(key)).url;
			writer.println(((urlInfo)urlIdInfo.getEntryObject(key)).title);
			writer.println(url);
			writer.printf("%s, %d",((urlInfo)urlIdInfo.getEntryObject(key)).lastModified,((urlInfo)urlIdInfo.getEntryObject(key)).size);
//			writer.println("Title keywords:")
			
			for(String temp : ((urlInfo)urlIdInfo.getEntryObject(key)).getTitleWordIds())
			{
				writer.print(((Word)idTitleIndex.getEntryObject(temp)).getWord());
				writer.print(" ");
				writer.print(((Word)idTitleIndex.getEntryObject(temp)).freq(key));
				writer.print("; ");
			}
			writer.println();
			for(String temp : ((urlInfo)urlIdInfo.getEntryObject(key)).getBodyWordIds())
			{
				writer.print(((Word)idBodyIndex.getEntryObject(temp)).getWord());
				writer.print(" ");
				writer.print(((Word)idBodyIndex.getEntryObject(temp)).freq(key));
				writer.print("; ");
			}
			writer.println();
			for(int temp : ((urlInfo)urlIdInfo.getEntryObject(key)).children)
			{
				writer.println(((urlInfo)urlIdInfo.getEntryObject(Integer.toString(temp))).url);
			}
			writer.println("-------------------------------------------------------------------------------------------");
		}
		writer.close();
	}
}
