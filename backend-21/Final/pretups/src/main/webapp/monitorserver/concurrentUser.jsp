<!--
 * @(#)concurrentUser.jsp 
 * Copyright(c) 2005, Bharti Telesoft Ltd. 
 * All Rights Reserved
 * 
 * -------------------------------------------------------------------------------------------------
 * Author 				Date 			History
 * -------------------------------------------------------------------------------------------------
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

<%
int count = 0;
%>
<html>
<head>
<title>eRecharge Monitor Server</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%= request.getContextPath()%>/monitorserver/common/main.css" rel="stylesheet" type="text/css">
</head>
<body  leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<form>
<div class="heading"><center><strong>Concurrent Users</strong></center></div>
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
				<td class="tabhead">Hits Details</td>
			</tr>
			
		
<%
try
{
	SessionVO locationSessionVO=null;
	SessionVO typeSessionVO=null;
	ArrayList userList=null;
	Enumeration enumeration=SessionCounter.userCounters.keys();
	
	String key=null;
	while(enumeration.hasMoreElements())
	{
		count++;
		key = (String)enumeration.nextElement();
		locationSessionVO=(SessionVO)SessionCounter.userCounters.get(key);
		%>
		
		<tr>
			<td class="tabcol">
				<%=key%>&nbsp;<%="("+locationSessionVO.getCounter()+")"%>
				<td class="tabcol">&nbsp;</td>
				<td class="tabcol">&nbsp;</td>
				<td class="tabcol">&nbsp;</td>
				<td class="tabcol">&nbsp;</td>
			</td>
		</tr>	
		<%
		if(locationSessionVO!=null)
		{
			Hashtable userTypeHash=(Hashtable)locationSessionVO.getList();
			if(userTypeHash!=null)
			{
				Enumeration enumTypes=userTypeHash.keys();
				String keyType=null;
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
					for(int j=0;j<userList.size();j++)
					{
						UserVO userVO=(UserVO)userList.get(j);
						
					
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
						<table cellspacing="1" cellpadding="3"  class="back" align="center">
						<tr>
							<td class="tabhead">Role Code</td>
							<td class="tabhead">Total Hits</td>
							<td class="tabhead">Total Under Process</td>
							<td class="tabhead">Total Time(ms)</td>
							<td class="tabhead">Last Access Time</td>
						</tr>
						<%
						com.btsl.user.businesslogic.SessionInfoVO sessionInfoVO=userVO.getSessionInfoVO();
						java.util.HashMap hitMap=(java.util.HashMap)sessionInfoVO.getRoleHitTimeMap();
						java.util.Set set =hitMap.entrySet();
						java.util.Iterator itr=set.iterator();
						while(itr.hasNext())
						{
							java.util.Map.Entry me=(java.util.Map.Entry)itr.next();
							String hitKey=(String)me.getKey();
							com.btsl.user.businesslogic.RoleHitTimeVO hitVal=(com.btsl.user.businesslogic.RoleHitTimeVO)me.getValue();
							%>
							<tr>
								<td class="tabcol"><%=hitVal.getRoleCode()%></td>
								<td class="tabcol"><%=hitVal.getTotalHits()%></td>
								<td class="tabcol"><%=hitVal.getTotalUnderProcess()%></td>
								<td class="tabcol"><%=hitVal.getTotalTime()%></td>
								<td class="tabcol"><%=hitVal.getLastAccessTime()%></td>
							</tr>
						<%
						}
						%>
						</table>
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