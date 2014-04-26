import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class parsebusiness_details {

	/**
	 * @param args
	 */
	/**
	 * @param args
	 * @throws IOException 
	 */
	static Hashtable<String, String> h = new Hashtable<String, String>();
	public static void main(String[] args) throws Exception {
		allrestaurantinhash();
		readandprocess();
		//sendPost();
	}
	
	private static void sendPost() throws Exception {
		final String USER_AGENT = "Mozilla/5.0";
		String url = "http://text-processing.com/api/sentiment/";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		//add reuqest header
		con.setRequestMethod("POST");
	//	con.setRequestProperty("User-Agent", USER_AGENT);
		//con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
		String urlParameters = "text=great";
 
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
		//System.out.println("\nSending 'POST' request to URL : " + url);
		//System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		System.out.println(response.toString());
		String s = "["+response+"]";
		Object ob=JSONValue.parse(s);
		JSONArray array=(JSONArray)ob;
		//System.out.println("=========================");
		//System.out.println(array.get(0));
		JSONObject ob2=(JSONObject)array.get(0);
		
			s = "["+ob2.get("probability")+"]";
			ob = JSONValue.parse(s);
			array=(JSONArray)ob;
			ob2=(JSONObject)array.get(0);
			System.out.println(ob2.get("neg"));
	}

	
	
	private static void readandprocess() throws IOException
	{
		File file = new File("filenamebusiness.txt");
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("business_id"+"\n");
		BufferedReader br = null;
		 
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader("C:\\Users\\Arunima\\Desktop\\Yelp_Scripts\\yelp_phoenix_academic_dataset\\yelp_academic_dataset_business.json"));
 
			while ((sCurrentLine = br.readLine()) != null) {
				process(sCurrentLine,bw);
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
		
		bw.close();
        System.out.println("Done");

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
	
	private static void writecontenttofile(String content, String name) throws IOException
	{
		//content=content.replaceAll("\n\n", "\n");
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

	private static void process(String sCurrentLine, BufferedWriter bw) throws IOException {
		String s = "["+sCurrentLine+"]";
		Object obj=JSONValue.parse(s);
		JSONArray array=(JSONArray)obj;
		//System.out.println("=========================");
		//System.out.println(array.get(0));
		JSONObject obj2=(JSONObject)array.get(0);
		if(h.containsKey(obj2.get("business_id")))
		{
			writecontenttofile(obj2.get("business_id").toString()+","+obj2.get("stars")+","+obj2.get("review_count")+"\n","business_restaurant_ratings_reviews.csv");
		}
		//System.out.println(numit(obj2.get("checkin_info")));
		//System.out.println("=========================");
		
	}

}
