<%
/**
 *
 * @(#)SMSCGateway.jsp
 *  ------------------------------------------------------------------
 *  Author 		Date(DD/MM/YYYY)	History
 *  ------------------------------------------------------------------
 * Ankit zindal            28/02/06			Creation
 *  ------------------------------------------------------------------
 */
%>
<html>
<head>
<title>eRecharge Monitor Server</title>
<jsp:include page="/monitorserver/common/topband.jsp">
<jsp:param name="helpFile" value="/help/SMSCGatewayHlp.jsp" />
</jsp:include>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="<%= request.getContextPath()%>/monitorserver/common/main.css" rel="stylesheet" type="text/css">
</head><body>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
<%@ page import="java.io.BufferedReader,java.io.InputStreamReader,java.net.HttpURLConnection,java.net.URL" %>
<%@ page import="com.btsl.util.Constants" %>
<div class="heading"><center><strong>SMSC Gateway Status</strong></center></div>
<table border="1" cellspacing="0" cellpadding="0"  align="center" width="80%">
<%
String urlString=Constants.getProperty("SMSCGateway_URL");
String urlArr[]=urlString.split(",");
String res=null;
HttpURLConnection httpcon=null;
BufferedReader buffReader=null;

try
{
        for(int i=0;i<urlArr.length;i++)
        {
	        System.out.println("URL="+urlArr[i]);
	        httpcon=(HttpURLConnection)(new URL((urlArr[i]))).openConnection();
	        buffReader=new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
	        %><tr><td width="20%"><strong>SMSC Gateway Status for URL <%=urlArr[i]%></strong></td><td width="80%"><%
	 
	        while((res=buffReader.readLine())!=null)
	        {
	                if(res !=null)
	                        res.trim();
	        %>
	              <br><font ><%=res %></font>
	        <%}%>
	        </td></tr>
	  <%  }
}

catch(Exception e)
{
        System.out.println("URL Connection Exception:"+e.getMessage());
}
%>
</tr></table>
<jsp:include page="/monitorserver/common/bottomband.jsp"/>
</body>
</html>