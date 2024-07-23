<!--
 * @(#)activeSessions.jsp 
 * Copyright(c) 2005, Bharti Telesoft Ltd. 
 * All Rights Reserved
 * 
 * -------------------------------------------------------------------------------------------------
 * Author 				Date 			History
 * -------------------------------------------------------------------------------------------------
 * Mohit Goel 		25/11/2005 		Initial Creation
 * 
 * This jsp will display the list of Active Users in session
   User can also invalidate the session
 * 
-->

<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.btsl.user.businesslogic.UserVO" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="com.btsl.session.businesslogic.ActiveSessionVO" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Date" %>
<%@ page import="com.btsl.util.SessionCounter" %>
<%@ page import="com.btsl.util.BTSLUtil" %>
<%@ page import="javax.servlet.http.HttpSession" %><%@ page import="com.btsl.util.Constants" %> 

<html>
<head>
<title>eRecharge Monitor Server</title>
<jsp:include page="/monitorserver/common/topband.jsp">
<jsp:param name="helpFile" value="/help/activeSessions.jsp" />
</jsp:include>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%= request.getContextPath()%>/monitorserver/common/main.css" rel="stylesheet" type="text/css">
</head>
<body  leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<form>
<%
int count = 0;
Hashtable h=SessionCounter.getActiveSessionsHash();
String key = request.getParameter("key");
HttpSession tempSession=null; 
if(key!=null)
{
	tempSession=(HttpSession )h.get(key);
	if(tempSession!=null)
	{
		tempSession.invalidate();
	}
	%>
	<p class ="message" align="center">
	Session successfully Invalidated
	</p>
<%}
%>
<%
String userId=(String)session.getAttribute("userId");
String password=(String)session.getAttribute("password");
String user=Constants.getProperty("monitorServerUser");
String pass=Constants.getProperty("monitorServerPass");

%>
<div class="heading"><center><strong>Active Sessions</strong></center></div>
<br/>
<table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
<tr>
<td> 
	<table width="100%" cellspacing="1" cellpadding="3"  class="back" align="center">
	
		<tr>
			<td class="tabhead">&nbsp;</td>
			<td class="tabhead">Login Id</td>
			<td class="tabhead">Type</td>
			<td class="tabhead">Request Host</td>
			<td class="tabhead">Request Ip</td>
			<td class="tabhead">Login Time</td>
			<td class="tabhead">Last Accessed Time</td>
			<td class="tabhead">&nbsp;</td>
		</tr>
		<%
			try 
	        {
	            h=SessionCounter.getActiveSessionsHash();
	        	Enumeration enumeration=h.keys();
	        	tempSession=null; 
	        	UserVO userVO=null;
	        	ActiveSessionVO activeVO = null;
	        	ArrayList activeList = new ArrayList();
	        	
	        	while(enumeration.hasMoreElements())
	        	{
	        		key = (String)enumeration.nextElement();
	        		tempSession=(HttpSession)h.get(key);
	        		if(tempSession!=null)
	    			{
	    				userVO=(UserVO)tempSession.getAttribute("user");
	    				activeVO = new ActiveSessionVO();
	    				if(userVO!=null)
	    				{
	    				    count = count + 1;
	    				    %>
							<tr>
							<td class="tabcol"><%=count%></td>
							<td class="tabcol"><%=userVO.getLoginID()%></td>
							<td class="tabcol"><%=userVO.getCategoryCode()%></td>
							<td class="tabcol"><%=userVO.getSessionInfoVO().getRemoteAddr()%></td>
							<td class="tabcol"><%=userVO.getSessionInfoVO().getRemoteHost()%></td>
							<td class="tabcol"><%=BTSLUtil.getDateTimeStringFromDate((new Date(tempSession.getCreationTime())))%></td>
							<td class="tabcol"><%=BTSLUtil.getDateTimeStringFromDate(new Date(tempSession.getLastAccessedTime()))%></td>
							<td class="tabcol">
							<%
							/*
								Invalidate link will shoe only when admin user logged in
							*/
							if((BTSLUtil.NullToString(userId).equalsIgnoreCase(BTSLUtil.NullToString(user))) && (BTSLUtil.NullToString(password).equalsIgnoreCase(BTSLUtil.NullToString(pass))))
							{
							%>
								<a href="<%=request.getContextPath()%>/monitorserver/activeSessions.jsp?key=<%=key%>&jspHeading=<%= request.getParameter("jspHeading")%>">Invalidate</a>
							<%
							}
							%>
							</td>
							</tr>
							<%
	    				}
	    			}
	        	}
	        } catch (Exception e) {} 
		%>
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
