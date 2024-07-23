/*
 * Created on Jan 22, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ussd;

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

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.web.pretups.forgotpassword.web.ForgotPasswordController;

/*import ussd.USSDTestProgram;
*/
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class USSDTestProgram 
{
	private static final Log LOG = LogFactory.getLog(USSDTestProgram.class.getName());
	public static final Properties properties = new Properties();
	private static String content_type = null;
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
		sbf.append("/");
		sbf.append((String)properties.get("CONTEXT"));
		sbf.append("/");
		sbf.append((String)properties.get("SERVICE_NAME"));
		sbf.append("?REQUEST_GATEWAY_CODE=");
		sbf.append((String)properties.get("REQUEST_GATEWAY_CODE"));
		sbf.append("&REQUEST_GATEWAY_TYPE=");
		sbf.append((String)properties.get("REQUEST_GATEWAY_TYPE"));
		sbf.append("&LOGIN=");
		sbf.append((String)properties.get("LOGIN"));
		sbf.append("&PASSWORD=");
		sbf.append((String)properties.get("PASSWORD"));
		sbf.append("&SOURCE_TYPE=");
		sbf.append((String)properties.get("SOURCE_TYPE"));
		sbf.append("&SERVICE_PORT=");
		sbf.append((String)properties.get("SERVICE_PORT"));
		
		String urlString = sbf.toString();	
		String requestXML = (String)properties.get("DATA");
		
		System.out.println("************ URL ************");
		System.out.println(urlString);
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
			if("XML".equalsIgnoreCase(USSDTestProgram.content_type))
			{
				con.addRequestProperty("Content-Type", "text/xml");
			}
			else
			{
				con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			}

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

			wr.close();
			rd.close();
			
		}catch (Exception e)
		{
			LOG.error("sendRequest", "Exception: " + e.getMessage());
            LOG.errorTrace("sendRequest", e);
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
			if(args.length==2)
		{
			System.out.println("HI................");
			USSDTestProgram.content_type = args[1];
		}
		else
		{
			USSDTestProgram.content_type = "XML";
		}
			load(filePath);
			sendRequest();
		} catch (IOException e)
		{
			LOG.error("sendRequest", "Exception: " + e.getMessage());
            LOG.errorTrace("sendRequest", e);
		}
		
	}
}
