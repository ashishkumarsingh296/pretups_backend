<%@ page language="java" import="java.util.*,java.io.*,java.net.*" pageEncoding="ISO-8859-1"%>

<HTML>
<HEAD>
<TITLE> </TITLE>
<SCRIPT langauage="javascript">
function redirectBack()
  {
  	document.location = 'RZebraConnectSim.jsp';
  }
</SCRIPT>

</HEAD>

<BODY onload="redirectBack();">

<%
String urlString = "";

try
{
		String IP = request.getParameter("IP");
		String Port = request.getParameter("PORT");
		String REQUEST_GATEWAY_CODE = request.getParameter("REQUEST_GATEWAY_CODE");
		String REQUEST_GATEWAY_TYPE = request.getParameter("REQUEST_GATEWAY_TYPE");
		String SERVICE_PORT = request.getParameter("SERVICE_PORT");
		String LOGIN = request.getParameter("LOGIN");
		String PASSWORD = request.getParameter("PASSWORD");
		String SOURCE_TYPE = request.getParameter("SOURCE_TYPE");
		String APP = request.getParameter("APP");

		urlString="http://"+IP+":"+Port+APP+"?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&SERVICE_PORT="+SERVICE_PORT+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE;
		System.setProperty("receiver.zebra.url",urlString);

		System.out.println("@@@@@@@ urlString :: "+System.getProperty("receiver.zebra.url"));
		
}catch(Exception ex)
{
	ex.printStackTrace();
}
finally
{
}

%>


</BODY>
</HTML>
