package simulator.loadtest.intest.cs3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Properties;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

import java.util.Base64;

public class CS3TestProgram 
{
	private Log _log = LogFactory.getLog(CS3TestProgram.class.getName());
    public void sendRequestToIN(String p_filePath)
    {
    	final String methodName = "sendRequestToIN";
    	FileInputStream fileInputStream = null;
        Properties properties = new Properties();
        File file = new File(p_filePath);
        HashMap hm=null;
        try
        {
            hm = new HashMap();
            fileInputStream = new FileInputStream(file);
			properties.load(fileInputStream);
                        String urlString = properties.getProperty("URL");
                        String requestXML = properties.getProperty("DATA");
                        String host     = properties.getProperty("HOST");
                        String userAgent =properties.getProperty("USER_AGENT");
			String userNamePwd = properties.getProperty("UIDPWD");
                        _log.debug(methodName, urlString);
                        _log.debug(methodName, requestXML);
                        HttpURLConnection con = null;
                        try
                        {
			
				BASE64Encoder encode = new BASE64Encoder();
				//String userPass="etopup:etopup";
				String userPass = userNamePwd;// "user:user";
				String encodedPass=encode.encode(userPass.getBytes());

	
                            //URLEncoder en =
                                URL url = new URL(urlString);
                                URLConnection uc = url.openConnection();
                                //System.out.println("HTTP RESPONSE CODE ::"+con.getResponseCode());
                                con = (HttpURLConnection) uc;

                                con.setUseCaches(false);
                                con.setDoInput(true);
                                con.setDoOutput(true);
                                con.setRequestMethod("POST");
                                con.addRequestProperty("Content-Type", "text/xml");
                                con.setRequestProperty("User-Agent",userAgent);
                                con.setRequestProperty("Host",host);
                                con.addRequestProperty("Connection", "Keep-Alive");
				con.setRequestProperty("Authorization","Basic "+encodedPass);
                                con.setRequestProperty("Content-Length",String.valueOf(requestXML.length()));

                BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF8"));

                wr.write(requestXML);
                wr.flush();
                InputStream rd = con.getInputStream();
                int c = 0;
                String line = "";
                while ((c = rd.read()) != -1)
                {
                        line += (char) c;
                }
                _log.debug(methodName, "****** RESPONSE DATA ***************** \n"+line);
                wr.close();
                rd.close();

			}catch (Exception e)
			{
                _log.errorTrace(methodName, e);
			}
			finally {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
				try {
					con.disconnect();
				} catch (Exception e) {
				}
			}

    	}
    	catch(Exception e)
    	{
    		_log.errorTrace(methodName, e);
    	}
    }
    
    public static void main(String[] args) {
        String filePath = args[0];
        CS3TestProgram csTest = new CS3TestProgram();
        csTest.sendRequestToIN(filePath);
    }
}

