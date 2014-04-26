

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class date_addition {
	static Hashtable<String, String> h = new Hashtable<String, String>();
	static Hashtable<String, Date> d = new Hashtable<String,Date>();
	static Hashtable<String,Integer> dgroup = new Hashtable<String,Integer>();
	public static void main(String[] args) throws Exception {
	allrestaurantinhash();
	readandprocess_initial();
	getrange();
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
		br.close();
		//System.out.println(h.size());
	}
	
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
