<%@ page import="com.btsl.util.*" %>
<html>
<head>
<jsp:include page="/monitorserver/common/topband.jsp">
<jsp:param name="helpFile" value="/help/serverMemory.jsp" />
</jsp:include>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%= request.getContextPath()%>/monitorserver/common/main.css" rel="stylesheet" type="text/css">
<title>eRecharge Monitor Server</title>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<div class="heading"><center><strong>eRecharge Server Memory Usage:</strong></center></div>
<br/>
<%
double t_mem=Runtime.getRuntime().totalMemory()/1049576;
Runtime.getRuntime().gc();
double f_mem=Runtime.getRuntime().freeMemory()/1048576 ; 
%>
<table border="0" cellspacing="0" cellpadding="0"  align="center">
<tr>
<td>
<font color="#808080" style="Verdana, Arial, Helvetica, sans-serif" SIZE="3"><b>Total Memory =<%=t_mem%> MB</b></font></BR>
</td>
</tr>
<tr>
<td>
<font color="#808080" style="Verdana, Arial, Helvetica, sans-serif" SIZE="3"><b>Used Memory =<%=(t_mem - f_mem)%> MB </b></font></BR>
</td>
</tr>
<tr>
<td>
<font color="#808080" style="Verdana, Arial, Helvetica, sans-serif" SIZE="3"><b>Free Memory = <%=f_mem%> MB</b></font></BR>
</td>
</tr>
</table>
<jsp:include page="/monitorserver/common/bottomband.jsp"/>
</body>
</html>