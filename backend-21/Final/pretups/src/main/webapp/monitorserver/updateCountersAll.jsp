
<%@ page import="com.btsl.loadcontroller.LoadControllerCache" %>

<html>
<head>
<title>eRecharge Monitor Server</title>
<jsp:include page="/monitorserver/common/topband.jsp"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%= request.getContextPath()%>/monitorserver/common/main.css" rel="stylesheet" type="text/css">
</head>
<body  leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<div class="heading"><center><strong>Update All Counters</strong></center></div>
<%
String instanceName = request.getParameter("instanceName");
String jspName = request.getParameter("jspName");
String instanceID=request.getParameter("instanceID");
try
{

LoadControllerCache.updateInstanceLoad(instanceID);
LoadControllerCache.updateNetworkLoadDetails();
LoadControllerCache.updateInterfaceLoadDetails();
LoadControllerCache.updateTransactionLoadDetails();
%>
<br>
<br>
<br>
<br>
<table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
<tr>
<td align="center">Counters are updated successfully</td>
</tr>
</table>
<%
}
catch(Exception e)
{
%>
<br>
<br>
<br>
<br>
<table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
<tr>
<td align="center">Counters are not updated successfully</td>
</tr>
</table>
<%
}
%>
<jsp:include page="/monitorserver/common/bottomband.jsp"/>
</body>
</html>