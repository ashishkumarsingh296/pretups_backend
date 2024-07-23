<%@ page language="java" import="java.util.*,java.io.*,java.net.*" pageEncoding="ISO-8859-1"%>
<jsp:directive.page import="com.btsl.util.BTSLUtil"/>
<jsp:directive.page import="com.btsl.pretups.preference.businesslogic.SystemPreferences"/>

<%
String requestXML = "";
String urlString = "";
String responseString = "";
Exception exm = null;
String serviceName = null;
try
{

		String servicetype = request.getParameter("servicetype");
		String retailermsisdn = request.getParameter("retailermsisdn");
		String receivermsisdn = request.getParameter("receivermsisdn");
		String amount = request.getParameter("amount");
		String notificationmsisdn = request.getParameter("notificationmsisdn");
		String pin = request.getParameter("pin");
		
	
		String newpin = request.getParameter("newpin");
		String confirmpin = request.getParameter("confirmpin");

		
		String IP = request.getParameter("IP");
		String Port = request.getParameter("PORT");
		String REQUEST_GATEWAY_CODE = request.getParameter("REQUEST_GATEWAY_CODE");
		String REQUEST_GATEWAY_TYPE = request.getParameter("REQUEST_GATEWAY_TYPE");
		String SERVICE_PORT = request.getParameter("SERVICE_PORT");
		String LOGIN = request.getParameter("LOGIN");
		String PASSWORD = request.getParameter("PASSWORD");
		String SOURCE_TYPE = request.getParameter("SOURCE_TYPE");
		String APP = request.getParameter("APP");


		

		StringBuffer sbf = new StringBuffer();
 		if("RR".equals(servicetype))
 		{
 		
	 		serviceName = "Roam Recharge";
	 		
			sbf.append("<?xml version=\"1.0\"?>");
			sbf.append("<!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN/\" \"xml/command.dtd\">");
			sbf.append("<COMMAND>");
			sbf.append("<TYPE>ROAMRCREQ</TYPE>");
			sbf.append("<MSISDN1>");
	
			sbf.append(retailermsisdn);
			sbf.append("</MSISDN1>");
			sbf.append("<PIN>");
			sbf.append(pin);
			sbf.append("</PIN>");
			sbf.append("<MSISDN2>");
			sbf.append(receivermsisdn);
			sbf.append("</MSISDN2>");
			sbf.append("<AMOUNT>");
			sbf.append(amount);
			sbf.append("</AMOUNT>");
			sbf.append("<LANGUAGE1>0");
			sbf.append("</LANGUAGE1>");
			sbf.append("</COMMAND>");

 		}
 		else
 		if("IR".equals(servicetype))
 		{
 		
 			serviceName = "International Recharge";
 		
			sbf.append("<?xml version=\"1.0\"?>");
			sbf.append("<!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN/\" \"xml/command.dtd\">");
			sbf.append("<COMMAND>");
			sbf.append("<TYPE>ROAMRCREQ</TYPE>");
			sbf.append("<MSISDN1>");
			sbf.append(retailermsisdn);
			sbf.append("</MSISDN1>");
			sbf.append("<PIN>");
			sbf.append(pin);
			sbf.append("</PIN>");
			sbf.append("<MSISDN2>");
			sbf.append(receivermsisdn);
			sbf.append("</MSISDN2>");
			sbf.append("<AMOUNT>");
			sbf.append(amount);
			sbf.append("</AMOUNT>");
			sbf.append("<MSISDN3>");
			sbf.append(notificationmsisdn);
			sbf.append("</MSISDN3>");			
			sbf.append("<LANGUAGE1>0");
			sbf.append("</LANGUAGE1>");			
			sbf.append("<LANGUAGE2>0");
			sbf.append("</LANGUAGE2>");
			sbf.append("</COMMAND>"); 	
 		}
		else
		if("RC".equals(servicetype))
 		{
 		
	 		serviceName = "Customer Recharge";
	 		
			sbf.append("<?xml version=\"1.0\"?>");
			sbf.append("<!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN/\" \"xml/command.dtd\">");
			sbf.append("<COMMAND>");
			sbf.append("<TYPE>RCTRFREQ</TYPE>");
			sbf.append("<MSISDN1>");	
			sbf.append(retailermsisdn);
			sbf.append("</MSISDN1>");
			sbf.append("<PIN>");
			sbf.append(pin);
			sbf.append("</PIN>");
			sbf.append("<MSISDN2>");
			sbf.append(receivermsisdn);
			sbf.append("</MSISDN2>");
			sbf.append("<AMOUNT>");
			sbf.append(amount);
			sbf.append("</AMOUNT>");
			sbf.append("<LANGUAGE1>0");
			sbf.append("</LANGUAGE1>");
			sbf.append("<LANGUAGE2>0");
			sbf.append("</LANGUAGE2>");
			sbf.append("<SELECTOR>1");
			sbf.append("</SELECTOR>");
			sbf.append("</COMMAND>");

 		}
		else
		if("CPN".equals(servicetype))
 		{
 		
	 		serviceName = "Change PIN";
	 		
			sbf.append("<?xml version=\"1.0\"?>");
			sbf.append("<!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN/\" \"xml/command.dtd\">");
			sbf.append("<COMMAND>");
			sbf.append("<TYPE>RCPNREQ</TYPE>");
			sbf.append("<MSISDN1>");
			sbf.append(retailermsisdn);
			sbf.append("</MSISDN1>");
			sbf.append("<PIN>");
			sbf.append(pin);
			sbf.append("</PIN>");
			sbf.append("<NEWPIN>");
			sbf.append(newpin);
			sbf.append("</NEWPIN>");
			sbf.append("<CONFIRMPIN>");
			sbf.append(confirmpin);
			sbf.append("</CONFIRMPIN>");
			sbf.append("<LANGUAGE1>0</LANGUAGE1>");
			sbf.append("</COMMAND>"); 			
 		}

		requestXML = sbf.toString();

		HttpURLConnection con = null;
		urlString=IP+":"+Port+APP+"?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&SERVICE_PORT="+SERVICE_PORT+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE;
		//String encodeUrl = URLEncoder.encode(urlString);
        URL url =null;
		if(SystemPreferences.HTTPS_ENABLE)
		{
		urlString="https://"+urlString;
        url=new URL(urlString);
        con=BTSLUtil.getConnection(url);
        }
        else
        {
        urlString="http://"+urlString;
		url=new URL(urlString);
        URLConnection uc = url.openConnection();
        con = (HttpURLConnection) uc;
        }
        con.addRequestProperty("Content-Type", "text/xml");
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
       

        while ((c = rd.read()) != -1)
        {
                // Process line...
                responseString += (char) c;
        }

		System.out.println("Output"+responseString);

}catch(Exception ex)
{
	exm = ex;
}

%>
<html>
<head>
<title> USSD Request Response </title>
<jsp:include page="simulatorTopband.jsp"></jsp:include>
</head>
<body>
<jsp:include page="divContents.jsp"></jsp:include>
<form>
<br><br><br>
	<table border="0" width="80%" cellspacing="2" cellpadding="5" align="center" bgcolor="#6699FF">
	
	<tr bgcolor="FFFF66">
		      <td width="100%" align="center" colspan="2">
		        <p align="center"><b><%=serviceName%> Request Response</b></p></td>
	</tr>
	
	<tr>
      <td colspan="2" align="center">
			<table border="1" width="600" cellspacing="0" cellpadding="0" align="center" bgcolor="#6699FF">
<%
		if(exm != null)
		{
%>
			<tr>
		      <td width="150" valign="top"><b>Error</b></td>
		      <td width="450"><font color="red"><%=exm%></font></td>
		    </tr>
<%} %>			
			
			
			<tr>
		      <td width="150" valign="top"><b>URL String</b></td>
		      <td width="450"><textarea rows="5" cols="60" readonly><%=urlString%></textarea></td>
		    </tr>
		    
		    <tr>
		      <td width="150" valign="top"><b>Request String</b></td>
		      <td width="450"><textarea rows="5" cols="60" readonly><%=requestXML%></textarea></td>
		    </tr>
		    <tr>
		      <td width="150" valign="top"><b>Response String</b></td>
		      <td width="450"><textarea rows="5" cols="60" readonly><%=responseString%></textarea></td>
		    </tr>
		    <tr>
		      <td colspan="2"></td>
		    </tr>
		   
		    </table>
    
    	</td>
    	</tr>
 		<tr>
		      <td colspan="2" align="center">
		      <input type="button" value="Back" name="button" onclick=history.go(-1)>
		      </td>
		    </tr>    
    
  	</table>
  
</form>
</div>
</body>
</html>










