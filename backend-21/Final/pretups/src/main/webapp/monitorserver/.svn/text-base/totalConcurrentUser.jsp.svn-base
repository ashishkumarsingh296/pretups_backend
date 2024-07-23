<!--
 * @(#)totalConcurrentUser.jsp 
 * Copyright(c) 2005, Bharti Telesoft Ltd. 
 * All Rights Reserved
 * 
 * -------------------------------------------------------------------------------------------------
 * Author 				Date 			History
 * -------------------------------------------------------------------------------------------------
 * 
 * 
 * This jsp will display the list of Users in session
 * 
-->

<%@ page import="com.btsl.util.SessionVO" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="com.btsl.user.businesslogic.UserVO" %>
<%@ page import="com.btsl.util.SessionCounter" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.btsl.user.businesslogic.RoleHitTimeVO" %>

<%
int count = 0;
int totalMaxUnderProcess=0;
			
%>
<html>
<head>
<title>eRecharge Monitor Server</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%= request.getContextPath()%>/monitorserver/common/main.css" rel="stylesheet" type="text/css">
</head>
<body  leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<form>
<div class="heading"><center><strong>Total Concurrent Users</strong></center></div>
<br/>
	<table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
		<tr>
			<td> 
			<table width="100%" cellspacing="1" cellpadding="3"  class="back" align="center">
			<tr>
			<td class="tabhead">Role Code</td>
			<td class="tabhead">Total Hits</td>
			<td class="tabhead">Total Under Process</td>
			<td class="tabhead">Max Under Process</td>
			<td class="tabhead">Total Time(ms)</td>
			<td class="tabhead">Last Access Time</td>
		</tr>
		<%
		try
		{
			SessionVO locationSessionVO=null;
			SessionVO typeSessionVO=null;
			ArrayList userList=null;
			Enumeration enumeration=SessionCounter.underProcessRoleHash.keys();
			
			String key=null;
			com.btsl.user.businesslogic.RoleHitTimeVO roleHitTimeVO=null;
			String key1=null;
			String value=null;
			while(enumeration.hasMoreElements())
			{
				count++;
				key = (String)enumeration.nextElement();
				roleHitTimeVO=(com.btsl.user.businesslogic.RoleHitTimeVO)SessionCounter.underProcessRoleHash.get(key);
				//out.println(" key : "+key+" roleHitTimeVO : "+roleHitTimeVO+" <br>");
				totalMaxUnderProcess +=roleHitTimeVO.getMaxUnderProcess();
		%>
		<tr>
				<td class="tabcol"><%=roleHitTimeVO.getRoleCode()%></td>
				<td class="tabcol"><%=roleHitTimeVO.getTotalHits()%></td>
				<td class="tabcol"><%=roleHitTimeVO.getTotalUnderProcess()%></td>
				<td class="tabcol"><%=roleHitTimeVO.getMaxUnderProcess()%></td>
				<td class="tabcol"><%=roleHitTimeVO.getTotalTime()%></td>
				<td class="tabcol"><%=roleHitTimeVO.getLastAccessTime()%></td>
			</tr>
<%		}
	}
catch(Exception e)
{
	e.printStackTrace();
}
%>
		<tr>
				<td class="tabcol">Total Current Under Process</td>
				<td class="tabcol" colspan=5><%=SessionCounter.currentTotalUnderProcess%></td>
				
			</tr>
			<tr>
				<td class="tabcol">Max Total Under Process</td>
				<td class="tabcol" colspan=5><%=SessionCounter.maxTotalUnderProcess%></td>
				
			</tr>

</table>
</td>
</tr>
</table>
<% 
	if(count == 0)
	{
%>
	<p align="center" class="heading2">No User List exist in the session</p>
<%
	}
%>
<jsp:include page="/monitorserver/common/bottomband.jsp"/>
</form>
</body>
</html>
