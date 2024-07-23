package com.inter.blin.cs5banglalink;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;

public class TestClass {
	private static Log _log = LogFactory.getLog(TestClass.class.getName());
    public static void main(String[] args) {
        try {
            HttpURLConnection con = null;
            String urlString = "http://10.60.16.96:10010/Air";
            URL url = new URL(urlString);
            URLConnection uc = url.openConnection();
            con = (HttpURLConnection) uc;
            String requestXML = FileCache.getValue("INTID00014", "REQUEST_STR");
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.addRequestProperty("Content-Type", "text/xml");
            con.addRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Content-Length", String.valueOf(requestXML.length()));
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF8"));
            wr.flush();
            InputStream rd = con.getInputStream();
            int c = 0;
            String line = "";
            while ((c = rd.read()) != -1) {
                line += (char) c;
            }
            System.out.println("****** RESPONSE DATA ***************** \n" + line);
            wr.close();
            rd.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
           _log.errorTrace("MalformedURLException",e);
        } catch (ProtocolException e) {
            // TODO Auto-generated catch block
        	_log.errorTrace("ProtocolException",e);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            _log.errorTrace("UnsupportedEncodingException", e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            _log.errorTrace("IOException",e);
        }
    }
}