package com.inter.claroca.multicurrency;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Base64;

public class TestMultC {
	
	private  String _endPoint = "";
	private String _propertiesFilePath=null;
	private String _action=null;
	private String _msisdn=null;
	private String _product=null;
	private String _coutnryCode=null;
	private HttpURLConnection _urlConnection = null;
	PrintWriter _out=null;
	String _request=null;
	String _responseStr=null;
	BufferedReader _in=null;
	public static void main (String args[])
    {
		TestMultC testDTH= new TestMultC();
		testDTH._propertiesFilePath=args[0].trim();
		testDTH._action=args[1].trim();
		testDTH.loadInputs();
		testDTH.fireRequest(testDTH._action, testDTH._endPoint);
		
    }
	public void fireRequest (String action,String endPoint)
    {
		
		
            try
            {
	URL url = new URL(endPoint);
                    _urlConnection = (HttpURLConnection) url.openConnection();
                    _urlConnection.setConnectTimeout(10000);
                    _urlConnection.setReadTimeout(10000);
                    _urlConnection.setDoOutput(true);
                    _urlConnection.setDoInput(true);
                    _urlConnection.setRequestMethod("POST");
                    BASE64Encoder encode = new BASE64Encoder();
                    String userPass = "INTERFAZ_WS" + ":" + "dXZ90g8hyH5$";
                    String encodedPass = encode.encode(userPass.getBytes());
                    
                    _urlConnection.setRequestProperty("Authorization", "Basic " + encodedPass);
                    _urlConnection.setRequestProperty("Content-Type", "text/xml");
		_request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:soap:functions:mc-style\"><soapenv:Header/> <soapenv:Body><urn:ZtipoDeCambio><Fcurr>USD</Fcurr><Gdatu>2016-11-17</Gdatu><Kurst>M</Kurst><Tcurr>GTQ</Tcurr></urn:ZtipoDeCambio></soapenv:Body></soapenv:Envelope>";
try {
                        _out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_urlConnection.getOutputStream())), true);
                        _out.flush();
                        _out.println(_request);
                        _out.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                        
                    }
try {
                    	StringBuffer buffer = new StringBuffer();
                        String response = "";
                        _in = new BufferedReader(new InputStreamReader(_urlConnection.getInputStream()));
                        
                        while ((response = _in.readLine()) != null) {
                            buffer.append(response);
                        }
                        _responseStr = buffer.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                        
                    }
                    
        	System.out.println(" Request String :"+_request); 
                   System.out.println(" Response String :"+_responseStr);           
                   
            }
            catch (Exception e)
            {
                    e.printStackTrace();
            }
            finally
            {
            	try {
                    if (_out != null)
                        _out.close();
                } catch (Exception e) {
                }
                try {
                    if (_in != null)
                        _in.close();
                } catch (Exception e) {
                }
            }


    }
	 public void loadInputs()
     {
             try{
                     Properties properties = new Properties();
             File file= new File(_propertiesFilePath);
             properties.load(new FileInputStream(file));
             _endPoint = properties.getProperty("END_POINT");
             _msisdn=properties.getProperty("MSISDN");
             _product=properties.getProperty("PRODUCT");
             _coutnryCode=properties.getProperty("COUNTRY_CODE");
             System.out.println("URL : " +_endPoint+" MSISDN : "+_msisdn+" Product : "+_product);
             }
             catch(Exception e){
                     e.printStackTrace();
             }


     }
	 
	 
}

