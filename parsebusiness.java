import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class parsebusiness {

	/**
	 * @param args
	 */
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		 readandprocess();

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
 
			br = new BufferedReader(new FileReader("business.json"));
 
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
	}

	private static void process(String sCurrentLine, BufferedWriter bw) throws IOException {
		String s = "["+sCurrentLine+"]";
		Object obj=JSONValue.parse(s);
		JSONArray array=(JSONArray)obj;
		//System.out.println("=========================");
		//System.out.println(array.get(0));
		JSONObject obj2=(JSONObject)array.get(0);
		if(obj2.get("categories").toString().contains("Restaurant"))
		{
			bw.write(obj2.get("business_id")+"\n");
		}
		//System.out.println(numit(obj2.get("checkin_info")));
		//System.out.println("=========================");
		
	}

}
