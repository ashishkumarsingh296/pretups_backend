package com.btsl.security;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.Constants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CustomRequestWrapper extends HttpServletRequestWrapper {

	   private Map<String, String[]> allParameters = null;
	   
/*
  private final String body;
 
 
  public CustomRequestWrapper(HttpServletRequest request) throws IOException 
  {
    //So that other request method behave just like before
    super(request);
     
    StringBuilder stringBuilder = new StringBuilder();
    BufferedReader bufferedReader = null;
    try {
      InputStream inputStream = request.getInputStream();
      if (inputStream != null) {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        char[] charBuffer = new char[128];
        int bytesRead = -1;
        while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
          stringBuilder.append(charBuffer, 0, bytesRead);
        }
      } else {
        stringBuilder.append("");
      }
    } catch (IOException ex) {
      throw ex;
    } finally {
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (IOException ex) {
          throw ex;
        }
      }
    }
    //Store request pody content in 'body' variable 
    body = stringBuilder.toString();
  }
 
  @Override
  public ServletInputStream getInputStream() throws IOException {
    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
    ServletInputStream servletInputStream = new ServletInputStream() {
      public int read() throws IOException {
        return byteArrayInputStream.read();
      }

	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setReadListener(ReadListener arg0) {
		// TODO Auto-generated method stub
		
	}
    };
    return servletInputStream;
  }
 
  @Override
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader(this.getInputStream()));
  }
 
  //Use this method to read the request body N times
  public String getBody() {
    return this.body;
  }
*/


	private final byte[] body;

    public CustomRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        byte[] encryptedBody = HttpRequestUtil.getBodyContent(request).getBytes(Charset.forName("UTF-8"));
        String encryptedBodyString = new String(encryptedBody, "UTF-8");
        String decryptedBody = null;
        try{

            //System.out.println("encryptedBodyString  "+encryptedBodyString);
            Object object = new JSONParser().parse(encryptedBodyString);
            JSONObject jsonObject = (JSONObject)object;
            encryptedBodyString = (String)jsonObject.get("encryptedBody");
            decryptedBody = AESEncryptionUtil.aesDecryptor(encryptedBodyString,Constants.A_KEY);
        }catch (Exception e){
         //   e.printStackTrace();
        }
        if(decryptedBody == null){
            body = encryptedBody;
        }else{
            body = decryptedBody.getBytes();
        }
    }

    public String getBody() throws UnsupportedEncodingException {
        return new String(body, "UTF-8");
//        String encryptedBody = new String(body, "UTF-8");
//        String decryptedBody = null;
//        try{
//            Object object = new JSONParser().parse(encryptedBody);
//            JSONObject jsonObject = (JSONObject)object;
//            encryptedBody = (String)jsonObject.get("encryptedBody");
//            decryptedBody = AESEncryptionUtil.aesDecryptor(encryptedBody,Constants.A_KEY);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    	return decryptedBody;
    }
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public String getParameter(final String name)
    {
        String[] strings = getParameterMap().get(name);
        if (strings != null)
        {
            return strings[0];
        }
        return super.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap()
    {
        if (allParameters == null)
        {
            allParameters = new TreeMap<String, String[]>();

            Map<String, String[]> map = super.getParameterMap() ;
            Map<String, String[]> mapModified = new HashMap<String, String[]>();


            for(String key: map.keySet()) {
            	String[] values = map.get(key) ;
            	try{values[0] =  AESEncryptionUtil.aesDecryptor(values[0], Constants.A_KEY) ; }catch(Exception e) {}
            	mapModified.put(key, values);
            }
            allParameters.putAll(mapModified);

        }
        //Return an unmodifiable collection because we need to uphold the interface contract.
        return Collections.unmodifiableMap(allParameters);
    }

    @Override
    public Enumeration<String> getParameterNames()
    {
        return Collections.enumeration(getParameterMap().keySet());
    }

    @Override
    public String[] getParameterValues(final String name)
    {
        return getParameterMap().get(name);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream bais = new ByteArrayInputStream(body);

        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }

}