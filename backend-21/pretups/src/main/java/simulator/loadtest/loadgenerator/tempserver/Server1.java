package simulator.loadtest.loadgenerator.tempserver;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import simulator.loadtest.loadgenerator.Server1Log;



public class Server1 extends HttpServlet {
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
	{
		
		InputStream is= req.getInputStream();
		int b;
		byte arr[]=new byte[req.getContentLength()];
		int m=0;
		while(is!=null && (b= is.read())!=-1)
		{
			arr[m]=(byte)b;
			m++;
		}
		String requestStream= new String(arr);
		Server1Log.log(requestStream);
		OutputStream os=res.getOutputStream();
		StringBuffer sbf= new StringBuffer();
		sbf.append("<?xml version=\"1.0\"\\?>");
		sbf.append(" <methodResponse>");
		sbf.append("<params>");
		sbf.append("<param>");
		sbf.append("<value>");
		sbf.append("<struct>");
		sbf.append("<member>");
		sbf.append("<name>responseCode</name>");
		sbf.append("<value>");
		sbf.append("<i4>100</i4>");
		sbf.append("</value>");
		sbf.append("</member>");
		sbf.append("<member>");
		sbf.append("<name>originTransactionID</name>");
		sbf.append("<value>");
		sbf.append("<string>10001111</string>");
		sbf.append("</value>");
		sbf.append("</member>");
		sbf.append("</struct>");
		sbf.append("</value>");
		sbf.append("</param>");
		sbf.append("</params>");
		sbf.append("</methodResponse>");
		String a= sbf.toString();
		os.write(a.getBytes());
	}

}
