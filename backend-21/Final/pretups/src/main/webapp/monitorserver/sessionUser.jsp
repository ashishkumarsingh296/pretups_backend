<!--
 * @(#)sessionUser.jsp 
 * Copyright(c) 2005, Bharti Telesoft Ltd. 
 * All Rights Reserved
 * 
 * -------------------------------------------------------------------------------------------------
 * Author 				Date 			History
 * -------------------------------------------------------------------------------------------------
 * Mohit Goel 		25/11/2005 		Initial Creation
 * 
 * This jsp will display the list of Users in session
 * 
-->


<%@ page import="com.btsl.util.SessionVO" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="com.btsl.user.businesslogic.UserVO" %>
<%@ page import="com.btsl.util.SessionCounter" %>

<%
int count = 0;
%>
<html>
<head>
<title>eRecharge Monitor Server</title>
<jsp:include page="/monitorserver/common/topband.jsp">
<jsp:param name="helpFile" value="/help/sessionUsers.jsp" />
</jsp:include>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%= request.getContextPath()%>/monitorserver/common/main.css" rel="stylesheet" type="text/css">
</head>
<body  leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<form>
<div class="heading"><center><strong>User Logged In</strong></center></div>
<br/>
	<table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
		<tr>
			<td> 
			<table width="100%" cellspacing="1" cellpadding="3"  class="back" align="center">
			<tr>
			<td class="tabhead">Network Code</td>
			<td class="tabhead">Category Code</td>
			<td class="tabhead">User Name</td>
			<td class="tabhead">Msisdn No</td>
			<td class="tabhead">Last Accessed Time</td>
		</tr>
<%
try
{
	SessionVO locationSessionVO=null;
	SessionVO typeSessionVO=null;
	ArrayList userList=null;
	Enumeration enumeration=SessionCounter.userCounters.keys();
	
	String key=null;
	String sessionID=null;
	Hashtable userTypeHash=null;
	Enumeration enumTypes=null;
	String keyType=null;
	String lastAccessTime=null;
	UserVO userVO=null;
	while(enumeration.hasMoreElements())
	{
		count++;
		key = (String)enumeration.nextElement();
		locationSessionVO=(SessionVO)SessionCounter.userCounters.get(key);
		%>
		<tr>
			<td class="tabcol">
				<%=key%>&nbsp;<%="("+locationSessionVO.getCounter()+")"%>
			</td>
			<td class="tabcol">&nbsp;</td>
			<td class="tabcol">&nbsp;</td>
			<td class="tabcol">&nbsp;</td>
			<td class="tabcol">&nbsp;</td>
		</tr>	
		<%
		if(locationSessionVO!=null)
		{
			userTypeHash=(Hashtable)locationSessionVO.getList();
			if(userTypeHash!=null)
			{
				enumTypes=userTypeHash.keys();
				keyType=null;
				while(enumTypes.hasMoreElements())
				{
					keyType = (String)enumTypes.nextElement();
					typeSessionVO=(SessionVO)userTypeHash.get(keyType);
					%>
					<tr>
						<td class="tabcol">&nbsp;</td>
						<td class="tabcol">
							<%=keyType%>&nbsp;<%="("+typeSessionVO.getCounter()+")"%>
						</td>
						<td class="tabcol">&nbsp;</td>
						<td class="tabcol">&nbsp;</td>
						<td class="tabcol">&nbsp;</td>
					</tr>
					<%
					userList=(ArrayList)typeSessionVO.getList();
					lastAccessTime=null;
					for(int j=0;j<userList.size();j++)
					{
						userVO=(UserVO)userList.get(j);
						try
						{
							sessionID=userVO.getSessionInfoVO().getSessionID();
							lastAccessTime=com.btsl.util.BTSLUtil.getDateTimeStringFromDate(new Date(((javax.servlet.http.HttpSession)SessionCounter.getActiveSessionsHash().get(sessionID)).getLastAccessedTime()));
						}
						catch(Exception e)
						{
							System.out.println("sessionUsers.jsp userVO.getSessionInfoVO(): "+userVO.getSessionInfoVO()+" sessionID: "+sessionID+" SessionCounter.getActiveSessionsHash().get(sessionID): "+SessionCounter.getActiveSessionsHash().get(sessionID));
							new SessionCounter().removeUserFromCounters(sessionID);
						}
					
					%>
					<tr>
						<td class="tabcol">&nbsp;</td>
						<td class="tabcol">&nbsp;</td>
						<td class="tabcol">
							<%=userVO.getUserName()%>
						</td>
						<td class="tabcol">
							<%=userVO.getMsisdn()%>
						</td>
						<td class="tabcol">
							<%=(lastAccessTime==null?"":lastAccessTime)%>
						</td>
					</tr>
					<%
					}
				}
			}
		}
	}
}
catch(Exception e)
{
	e.printStackTrace();
}
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