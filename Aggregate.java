import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class Aggregate {
	
	
	public static void main(String[] args) throws Exception {allrestaurantinhash();readandprocess_initial();
	getrange();readandprocess();}
	
	static Hashtable<String, String> h = new Hashtable<String, String>();
	static Hashtable<String, Date> d = new Hashtable<String,Date>();
	static Hashtable<String,Integer> dgroup = new Hashtable<String,Integer>();
	

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
		br.close();
		//System.out.println(h.size());
	}
	
	
	private static void readandprocess() throws Exception
	{
		writecontenttofile("business_id"+"\t"+"review_count"+"\t"+"longitude"+"\t"+"latitude"+"\t"+"cat1"+"\t"+"cat2"+"\t"+"cat3"+"\t"+"cat4"+"\t"+"cat5"+"\t"+"cat6"+"\t"+"cat7"+"\t"+"cat8"+"\t"+"price"+"\t"+"wtavgtotalreviews"+"\t"+"wtavgbias"+"\t"+"wtavgstarrating_without_1"+"\t"+"agegroup"+"\t"+"stars"+"\n","wtagereview_details_without1");

		int count =1;
		BufferedReader br = null;
		 
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader("C:\\Users\\Arunima\\Desktop\\Yelp_Scripts\\yelp_phoenix_academic_dataset\\yelp_academic_dataset_business.json"));
 
			while ((sCurrentLine = br.readLine()) != null) {
				process(sCurrentLine,count);
				if(count%5==0)
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
	
	private static void process(String sCurrentLine,int count) throws Exception {
		String s = "["+sCurrentLine+"]";
		Object obj=JSONValue.parse(s);
		JSONArray array=(JSONArray)obj;
		//System.out.println("=========================");
		//System.out.println(array.get(0));
		JSONObject obj2=(JSONObject)array.get(0);
		if(h.containsKey(obj2.get("business_id")))
		{
			String att = obj2.get("attributes").toString();
			String satt = "["+att+"]";
			Object objat=JSONValue.parse(satt);
			JSONArray arrayat=(JSONArray)objat;
			JSONObject obj2at=(JSONObject)arrayat.get(0);
			String price ="";
			try{
				
			{price = obj2at.get("Price Range").toString();}}
			catch(Exception e)
			{}
			
			writecontenttofile(obj2.get("business_id").toString()+"\t"+obj2.get("review_count")+"\t"+obj2.get("longitude")+"\t"+obj2.get("latitude")+"\t"+processallcategories(processcategories(obj2.get("categories").toString()))+"\t"+price+"\t"+getrevthings(obj2.get("business_id").toString())+"\t"+dgroup.get(obj2.get("business_id"))+"\t"+obj2.get("stars")+"\n","wtagereview_details_without1");
		}
		//System.out.println(numit(obj2.get("checkin_info")));
		//System.out.println("=========================");
		
	}
	
	private static String processcategories(String arr)
	{
		String w = arr.substring(2, arr.lastIndexOf("]"));
		w=w.replaceAll("\"", "");
		w=w.replaceAll(",Restaurants", "");
		w.replaceAll("Restaurants,", "");
		return w;
	}
	
	private static String checkfile(String cat,String file) throws IOException
	{ 
		BufferedReader br = null;
		 
		{
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader(file));
 
			while ((sCurrentLine = br.readLine()) != null) {
				if(cat.contains(sCurrentLine))
				{
					br.close();
					return "1";
				}
				
			}
			br.close();
		} 
		
		return "0";
		
		
	}
	
	private static String processallcategories(String cat) throws IOException
	{
		String ret = checkfile(cat,"cat1")+"\t"+checkfile(cat,"cat2")+"\t"+checkfile(cat,"cat3")+"\t"+checkfile(cat,"cat4")+"\t"+checkfile(cat,"cat5")+"\t"+checkfile(cat,"cat6")+"\t"+checkfile(cat,"cat7");
		if(ret.contains("1"))
			ret+="\t0";
		else
			ret+="\t1";
		return ret;
	}
	
	private static String getrevthings(String business_id) throws Exception
	{
		double avgbias = 0;
		double avgtotalreviews = 0;
		double avgstarrating = 0;
		double count = 0;
		BufferedReader br = null;
		 
		
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader("C:\\Users\\Arunima\\Desktop\\Userdata\\Users\\"+business_id+".csv"));
 
			while ((sCurrentLine = br.readLine()) != null) {
				/////////////////////////////////////////////
				String revdet = getreviewerdetails(sCurrentLine.split(",")[0]);
				int num = Integer.parseInt(revdet.split(",")[1]);
				if(num==1)
				{}
				else
				{
				/////////////////////////////////////////////
				//count++;
				
				//avgtotalreviews+=Integer.parseInt(revdet.split(",")[1]);
				//avgbias+=Math.min(Math.abs(Double.parseDouble(revdet.split(",")[2])-2.5),Math.abs(Double.parseDouble(revdet.split(",")[2])-3.5));
				//avgstarrating+= Integer.parseInt(sCurrentLine.split(",")[1]);
				int bin =-1;
				if(((Math.log(num)/Math.log(2))>=1) && ((Math.log(num)/Math.log(2))<2))
				{bin = 2;}
				else if(((Math.log(num)/Math.log(2))>=2) && ((Math.log(num)/Math.log(2))<3))
				{bin = 3;}
				else if(((Math.log(num)/Math.log(2))>=3) && ((Math.log(num)/Math.log(2))<4))
				{bin = 4;}
				else if(((Math.log(num)/Math.log(2))>=4) && ((Math.log(num)/Math.log(2))<5))
				{bin = 5;}
				else if(((Math.log(num)/Math.log(2))>=5) && ((Math.log(num)/Math.log(2))<6))
				{bin = 6;}
				else if(((Math.log(num)/Math.log(2))>=6) && ((Math.log(num)/Math.log(2))<7))
				{bin = 7;}
				else if(((Math.log(num)/Math.log(2))>=7) && ((Math.log(num)/Math.log(2))<8))
				{bin = 8;}
				else if(((Math.log(num)/Math.log(2))>=8) && ((Math.log(num)/Math.log(2))<9))
				{bin = 9;}
				else if(((Math.log(num)/Math.log(2))>=9))
				{bin = 10;}
				else
				{bin = 0;}
				count+=bin;
				avgtotalreviews+=bin*(Integer.parseInt(revdet.split(",")[1]));
				avgbias+=bin*(Math.min(Math.abs(Double.parseDouble(revdet.split(",")[2])-2.5),Math.abs(Double.parseDouble(revdet.split(",")[2])-3.5)));
				avgstarrating+= bin*(Integer.parseInt(sCurrentLine.split(",")[1]));
				
				}
			}
			
			avgbias=avgbias/count;
			avgtotalreviews = avgtotalreviews/count;
			avgstarrating = avgstarrating/count;
 
		
		br.close();
		
		return avgtotalreviews+"\t"+avgbias+"\t"+avgstarrating;
	}
	
	
	private static String getreviewerdetails(String sCurrentLine) throws Exception {
		
		 
		
			BufferedReader br = null;

			String CurrentLine;
 
			br = new BufferedReader(new FileReader("C:\\Users\\Arunima\\Desktop\\Userdata\\all_restaurant_reviewers_details.csv"));
 
			while ((CurrentLine = br.readLine()) != null) {
				if(CurrentLine.contains(sCurrentLine))
				{br.close();return CurrentLine;}
				
				
			}
			br.close();
			throw new Exception();
			
 
		
		
		
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

	
	//---------------------------------------------------
	
	
	private static void getdate(String sCurrentLine) throws ParseException
	{
		String s = "["+sCurrentLine+"]";
		Object obj=JSONValue.parse(s);
		JSONArray array=(JSONArray)obj;
		//System.out.println("=========================");
		//System.out.println(array.get(0));
		JSONObject obj2=(JSONObject)array.get(0);
		if(h.containsKey(obj2.get("business_id")))
		{
			Date jsondate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(obj2.get("date").toString());
			
			
			if(d.containsKey(obj2.get("business_id")))
			{
				if(jsondate.before(d.get(obj2.get("business_id"))))
				{
					d.put((String) obj2.get("business_id"), jsondate);
					
				}
				else{}
			}
			else
			{
				d.put((String) obj2.get("business_id"), jsondate);
			}	
			
		}
		//System.out.println(numit(obj2.get("checkin_info")));
		//System.out.println("=========================");
		
	}
	
	@SuppressWarnings("deprecation")
	private static void getrange() throws ParseException
	{
		Date max = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("1975-01-01");
		Date min = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-01-01");
		int t2005 = 0;
		int t2006 = 0;
		int t2007 = 0;
		int t2008 = 0;
		int t2009 = 0;
		int t2010 = 0;
		int t2011 = 0;
		int t2012 = 0;
		int t2013 = 0;
		int t2014 = 0;
		
		for (String key : d.keySet())
		{
			int y = Integer.parseInt(d.get(key).toString().substring(d.get(key).toString().length()-4, d.get(key).toString().length()));
			if(y==2005)
			{
				t2005++;
				dgroup.put(key, new Integer(1));
			}
			if(y==2006)
			{
				t2006++;
				dgroup.put(key, new Integer(1));
			}
			if(y==2007)
			{
				t2007++;
				dgroup.put(key, new Integer(2));
			}
			if(y==2008)
			{
				t2008++;
				dgroup.put(key, new Integer(3));
			}
			if(y==2009)
			{
				t2009++;
				dgroup.put(key, new Integer(4));
			}
			if(y==2010)
			{
				t2010++;
				dgroup.put(key, new Integer(5));
			}
			
			if(y==2011)
			{
				t2011++;
				dgroup.put(key, new Integer(6));
			}
			if(y==2012)
			{
				t2012++;
				dgroup.put(key, new Integer(7));
			}
			if(y==2013)
			{
				t2013++;
				dgroup.put(key, new Integer(8));
			}
			if(y==2014)
			{
				t2014++;
				dgroup.put(key, new Integer(8));
			}
			
			if(d.get(key).before(min))
				min = d.get(key);
			if(d.get(key).after(max))
				max = d.get(key);
		}
		
		long diff = max.getTime() - min.getTime();
		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		
		System.out.println(max+" : "+min);
		System.out.println(diffDays/365 + " years, ");
		System.out.println(diffDays/7 + " weeks, ");
		System.out.print(diffDays + " days, ");
		System.out.print(diffHours + " hours, ");
		System.out.print(diffMinutes + " minutes, ");
		System.out.print(diffSeconds + " seconds.");
		System.out.println("###################################");
		System.out.println(t2005+" "+t2006+" "+t2007+" "+t2008+" "+t2009+" "+t2010+" "+t2011+" "+t2012+" "+t2013+" "+t2014);
	}
	private static void readandprocess_initial() throws Exception
	{

		int count =1;
		BufferedReader br = null;
		 
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader("C:\\Users\\Arunima\\Desktop\\Yelp_Scripts\\yelp_phoenix_academic_dataset\\yelp_academic_dataset_review.json"));
 
			while ((sCurrentLine = br.readLine()) != null) {
				getdate(sCurrentLine);
				if(count%5==0)
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
        System.out.println(d.size());

	}

}
