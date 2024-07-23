<%@ page import="java.util.*,java.io.BufferedReader,java.io.InputStreamReader,java.net.HttpURLConnection,java.net.URL,com.btsl.util.*" %>
<%@ page import="com.btsl.util.BTSLUtil" %>
<html>
<head>
<title>eRecharge Monitor Server</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%= request.getContextPath()%>/monitorserver/common/main.css" rel="stylesheet" type="text/css">
</head>
<body  leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<%
String userId=(String)session.getAttribute("userId");
String password=(String)session.getAttribute("password");
String user=Constants.getProperty("monitorServerUser");
String pass=Constants.getProperty("monitorServerPass");
String userView=Constants.getProperty("monitorServerUserView");
String passView=Constants.getProperty("monitorServerPassView");
boolean found=false;
if((! BTSLUtil.NullToString(userId).equalsIgnoreCase(BTSLUtil.NullToString(userView))) || (! BTSLUtil.NullToString(password).equalsIgnoreCase(BTSLUtil.NullToString(passView))))
{
	found=false;
}
else
{
	found=true;
}
if(!found)
{
	if((! BTSLUtil.NullToString(userId).equalsIgnoreCase(BTSLUtil.NullToString(user))) || (! BTSLUtil.NullToString(password).equalsIgnoreCase(BTSLUtil.NullToString(pass))))
	{
		found=false;
	}
	else
	{
		found=true;
	}
}
if(!found)
{
	%>
		<jsp:forward page="loginMonitorServer.jsp" >
		<jsp:param name="message" value="Session has been expired, please re login"/>
		</jsp:forward>
	<%
}
String instanceName = request.getParameter("instanceName");
String ipAddress = request.getParameter("ipAddress");
String port = request.getParameter("port");
String jspName = request.getParameter("jspName");
String instanceID=request.getParameter("instanceID");

String jspHeading = "Instance Name: "+instanceName+" IP: "+ipAddress+" Port: "+port;


if(BTSLUtil.isNullString(ipAddress) || BTSLUtil.isNullString(port))
{
%>
		<jsp:forward page="loginMonitorServer.jsp" >
		<jsp:param name="message" value="Invalid IP Address or Port"/>
		</jsp:forward>
	<%
}
%>
<%--<br/><p align="center" class="heading2"><%= jspHeading%></p>--%>
<%
String url1=null;

if(ipAddress.equals(request.getServerName()) && port.equals(request.getServerPort()+""))
{
	
		url1="/monitorserver/"+jspName+".jsp?jspHeading="+java.net.URLEncoder.encode(jspHeading)+"&instanceID="+instanceID;
%>
	<jsp:forward page="<%=url1%>"/>
<%
}
else
{
	url1="http://"+ipAddress+":"+port+request.getContextPath()+"/monitorserver/"+jspName+".jsp?jspHeading="+java.net.URLEncoder.encode(jspHeading)+"&instanceID="+instanceID;
	
	String res=null;
	HttpURLConnection httpcon1=null;
	BufferedReader buffReader=null;
	try
	{
		httpcon1=(HttpURLConnection)(new URL((url1))).openConnection();
		buffReader=new BufferedReader(new InputStreamReader(httpcon1.getInputStream()));
	%>
	
	<%
		while((res=buffReader.readLine())!=null)
		{
	        if(res !=null)
	                res.trim();
	%>
			<%=res %><%
		}
	}
	catch(Exception e)
	{
		System.out.println("URL Connection Exception for "+url1+"  e:"+e);
	%>
		<jsp:forward page="showOptions.jsp" >
			<jsp:param name="message" value="Application is not running on the selected Instance"/>
		</jsp:forward>
	<%
	}
}//end of else
%>

</body>
</html>