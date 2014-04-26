import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class parsecheckin {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		 readandprocess();

	}

	private static Object numit(Object object) {
		int total=0;
		String t = object.toString();
		String[] a  = t.split(",");
		for(int i =0;i<a.length;i++)
		{
			int count = Integer.parseInt(a[i].split(":")[1].charAt(0)+"");
			total+=count;
		}
		return total;
	}
	
	private static void readandprocess() throws IOException
	{
		File file = new File("filename.txt");
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("business_id"+"\t"+"checkin_info"+"\n");
		BufferedReader br = null;
		 
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader("testing.json"));
 
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
		bw.write(obj2.get("business_id")+"\t"+numit(obj2.get("checkin_info"))+"\n");
		//System.out.println(numit(obj2.get("checkin_info")));
		//System.out.println("=========================");
		
	}

}
