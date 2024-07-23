package simulator.loadtest.loadgenerator.tempserver;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import simulator.loadtest.loadgenerator.Server0Log;
import simulator.loadtest.loadgenerator.Server1Log;
import simulator.loadtest.loadgenerator.logging.Log;
import simulator.loadtest.loadgenerator.logging.LogFactory;



public class Server0 extends HttpServlet {
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
	{
		 Log _log = LogFactory.getLog(this.getClass().getName());
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
		 Server0Log.log("Request :: "+requestStream);
		//System.out.println("Request On server 0"+requestStream);
		OutputStream os=res.getOutputStream();
		StringBuffer sbf= new StringBuffer();
		/*sbf.append("<?xml version=\"1.0\"\\?>");
		sbf.append(" <methodResponse>");
		sbf.append("<params>");
		sbf.append("<param>");
		sbf.append("<value>");
		sbf.append("<struct>");
		sbf.append("<member>");
		sbf.append("<name>responseCode</name>");
		sbf.append("<value>");
		sbf.append("<string>100</string>");
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
		sbf.append("</methodResponse>");*/
		sbf.append("<methodResponse><member><name>responseCode</name><value><i4>100</i4></value></member><member><name>originTransactionID</name><value><string>10001111</string></value></member><member><name>serviceClassCurrent</name><value><i4>102</i4></value></member><member><name>accountValue1</name><value><string>23467</string></value></member>       <member>        <name>serviceFeeExpiryDate</name>        <value>         <dateTime.iso8601>20050930T00:00:00+0000</dateTime.iso8601></value></member><member><name>supervisionExpiryDate</name><value><dateTime.iso8601>20050930T00:00:00+0000</dateTime.iso8601></value></member><member><name>dedicatedAccountInformation</name><value><struct><member><name>dedicatedAccountID</name><value><i4>4536</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>4536</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>20050930T00:00:00+0000</dateTime.iso8601></value></member></struct></value></member></struct></value></param></params></methodResponse>");
		String a= sbf.toString();
		os.write(a.getBytes());
	}

}
