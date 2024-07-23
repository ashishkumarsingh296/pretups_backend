/*
 * Created on Jan 22, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package EXTGW;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EXTGWTestProgram 
{
	public static Properties properties = new Properties();
	
    public static void load(String fileName) throws IOException
    {
    	File file = new File(fileName);
     	properties.load(new FileInputStream(file));
	}//end of load
    
    
	public static void sendRequest()
	{

        StringBuffer sbf = new StringBuffer("http://");
		sbf.append((String)properties.get("IP"));
		sbf.append(":");
		sbf.append((String)properties.get("PORT"));
		sbf.append("/pretups/");
		sbf.append((String)properties.get("SERVICE_NAME"));
	    sbf.append("?");			
		String urlString = sbf.toString();
		System.out.println("************ URL ************");
		System.out.println(urlString);
		System.out.println();
		
		StringBuffer sbf1 = new StringBuffer();
		sbf1.append("REQUEST_GATEWAY_CODE=");
		sbf1.append((String)properties.get("REQUEST_GATEWAY_CODE"));
		sbf1.append("&REQUEST_GATEWAY_TYPE=");
		sbf1.append((String)properties.get("REQUEST_GATEWAY_TYPE"));
		sbf1.append("&LOGIN=");
		sbf1.append((String)properties.get("LOGIN"));
		sbf1.append("&PASSWORD=");
		sbf1.append((String)properties.get("PASSWORD"));
		sbf1.append("&SOURCE_TYPE=");
		sbf1.append((String)properties.get("SOURCE_TYPE"));
		sbf1.append("&SERVICE_PORT=");
		sbf1.append((String)properties.get("SERVICE_PORT"));
		String authorization = sbf1.toString();
		System.out.println();
		System.out.println("************ Authorization Parameter ************");
		System.out.print(authorization);
		System.out.println();

		String requestXML = (String)properties.get("DATA");
		System.out.println("************ Request XML ************");
		System.out.print(requestXML);
		System.out.println();

		HttpURLConnection con = null;
		try
		{
			String encodeUrl = URLEncoder.encode(urlString);
			URL url = new URL(urlString);
			 
			URLConnection uc = url.openConnection();
			con = (HttpURLConnection) uc;
			con.addRequestProperty("Content-Type", "text/xml");
			con.addRequestProperty("Authorization", authorization);
        	con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestMethod("POST");

			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF8"));
			// Send data
			wr.write(requestXML);
			wr.flush();

			// Get response
			InputStream rd = con.getInputStream();
			int c = 0;
			String line = "";

			while ((c = rd.read()) != -1)
			{
				// Process line...
				line += (char) c;
			}
			System.out.println();
			System.out.println("****** RESPONSE DATA ***************** \n"+line);
			//System.out.println(line);

			wr.close();
			rd.close();
			
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(con != null)
			{
				con.disconnect();
			}
		}
	}
	
	public static void main(String[] args)
	{
		String filePath = args[0];
		try 
		{
			load(filePath);
			sendRequest();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
}
