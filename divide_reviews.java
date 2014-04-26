import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;



public class divide_reviews {

	
	static Hashtable<String, String> h = new Hashtable<String, String>();
	public static void main(String[] args) throws Exception {
		allrestaurantinhash();
		readandprocess();
		//sendPost();
	}
	
	
	private static void allrestaurantinhash() throws IOException
	{
		
		String file =("business_restaurant_checkin.csv");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line ="";
		while ((line = br.readLine()) != null) {
			 
	        // use comma as separator
		String[] business = line.split(",");

		
		h.put(business[0], business[1]);
		

	}
		//System.out.println(h.size());
	}
	
	private static void readandprocess() throws IOException
	{
		int count =1;
		BufferedReader br = null;
		 
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader("C:\\Users\\Arunima\\Desktop\\Yelp_Scripts\\yelp_phoenix_academic_dataset\\yelp_academic_dataset_review.json"));
 
			while ((sCurrentLine = br.readLine()) != null) {
				process(sCurrentLine,count);
				if(count%1000==0)
				{
					System.out.println(count);
				}
				count++;
				
				
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
	
        System.out.println("Done");

	}
	
	private static void process(String sCurrentLine,int count) throws IOException {
		String s = "["+sCurrentLine+"]";
		Object obj=JSONValue.parse(s);
		JSONArray array=(JSONArray)obj;
		//System.out.println("=========================");
		//System.out.println(array.get(0));
		JSONObject obj2=(JSONObject)array.get(0);
		if(h.containsKey(obj2.get("business_id")))
		{
			writecontenttofile(obj2.get("text").toString(),obj2.get("stars")+"\\"+obj2.get("review_id")+"("+count+")");
		}
		//System.out.println(numit(obj2.get("checkin_info")));
		//System.out.println("=========================");
		
	}
	
	private static void writecontenttofile(String content, String name) throws IOException
	{
		content=content.replaceAll("\n\n", "\n");
		//String data = "--------------------------$Start$\n"+content+"\n$End$-------------------------\n";
		 
		File file =new File("C:\\Users\\Arunima\\Desktop\\features\\"+name);

		//if file doesnt exists, then create it
		if(!file.exists()){
			file.createNewFile();
		}

		//true = append file
		FileWriter fileWritter = new FileWriter(file.getAbsolutePath(),true);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write(content);
	        bufferWritter.close();

		
	}
}
