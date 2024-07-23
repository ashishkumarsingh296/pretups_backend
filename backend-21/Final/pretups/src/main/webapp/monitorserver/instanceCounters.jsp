<%
/**
 *
 * @(#)instanceLoadCounters.jsp
 *  ------------------------------------------------------------------
 *  Author 		Date(DD/MM/YYYY)	History
 *  ------------------------------------------------------------------
 * Gurjeet              14/07/2005			Creation
 *  ------------------------------------------------------------------
 */
%>
<html>
<head>
<title>eRecharge Monitor Server</title>
<jsp:include page="/monitorserver/common/topband.jsp">
<jsp:param name="helpFile" value="/help/pretupsInstanceCounters.jsp" />
</jsp:include>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="<%= request.getContextPath()%>/monitorserver/common/main.css" rel="stylesheet" type="text/css">
</head><body>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
<table border="1" cellspacing="0" cellpadding="0"  >
<tr>
<%@ page import="com.btsl.loadcontroller.*,java.util.*" %>
<%@ page import="com.btsl.util.BTSLUtil" %>
<%@ page import="com.btsl.util.Constants" %>
<div class="heading"><center><strong>eRecharge Instance Counters:</strong></center></div>
<br/>
<%

String userId=(String)session.getAttribute("userId");
String password=(String)session.getAttribute("password");
String user=Constants.getProperty("monitorServerUser");
String pass=Constants.getProperty("monitorServerPass");
boolean isSuperAdmin = false;
if((BTSLUtil.NullToString(userId).equalsIgnoreCase(BTSLUtil.NullToString(user))) && (BTSLUtil.NullToString(password).equalsIgnoreCase(BTSLUtil.NullToString(pass))))
{
	isSuperAdmin = true;
}

Hashtable instanceHash = LoadControllerCache.getInstanceLoadHash();
Set keySet = instanceHash.keySet();
java.util.Iterator tempItr = keySet.iterator();
java.util.Iterator itr = keySet.iterator();
InstanceLoadVO instanceLoadVO = null;
String tempkey=null;
if(tempItr.hasNext())
{
	tempkey = (String)tempItr.next();
	instanceLoadVO = (InstanceLoadVO)instanceHash.get(tempkey);
%>
<p class ="message">Last Initialized time:&nbsp;<%=instanceLoadVO.getLastInitializationTime()%></p>
<br>
<%
}
instanceLoadVO = null;
String key = null;
int count =0;
%>
<td class="tabhead">Sr no.</td>
<td class="tabhead">Instance ID</td>
<td class="tabhead">Instance Name</td>
<td class="tabhead">Allowed Load</td>
<%
	if(isSuperAdmin)
	{
%>
<td class="tabhead">Allowed TPS</td>
<%
	}
%>
<td class="tabhead">Recieved Count</td>
<td class="tabhead">Current Count</td>
<%
	if(isSuperAdmin)
	{
%>
<td class="tabhead">Request In Same Sec</td>
<%
	}
%>
<td class="tabhead">Total Request Count</td>
<td class="tabhead">Total Refused Count</td>
<td class="tabhead">Last Received Time</td>
<td class="tabhead">Last Refused Time</td>
<td class="tabhead">Last Under Process Time</td>
<td class="tabhead">Status</td>
<td class="tabhead">Host</td>
<td class="tabhead">Port</td>
</tr>
<%
long totCurrentLoad=0;
long totCurrentTPS=0;
long totRequestRecieved=0;
long totCurrentUPCount=0;
long totCurrentReqInSameSec=0;
long totRequestProcessed=0;
long totalRefusedCount=0;

while(itr.hasNext())
{
	key = (String)itr.next();
	instanceLoadVO = (InstanceLoadVO)instanceHash.get(key);
	count = count + 1;
	totCurrentLoad +=instanceLoadVO.getTransactionLoad();
	totCurrentTPS +=instanceLoadVO.getCurrentTPS();
	totRequestRecieved +=instanceLoadVO.getRecievedCount();
	totCurrentUPCount +=instanceLoadVO.getCurrentTransactionLoad();
	totCurrentReqInSameSec +=instanceLoadVO.getNoOfRequestSameSec();
	totRequestProcessed +=instanceLoadVO.getRequestCount();
	totalRefusedCount +=instanceLoadVO.getTotalRefusedCount();
%>
<tr>
<td class="tabcol"><%=count%></td>
<td class="tabcol"><%=instanceLoadVO.getInstanceID()%></td>
<td class="tabcol"><%=instanceLoadVO.getInstanceName()%></td>
<td class="tabcol"><%=instanceLoadVO.getTransactionLoad()%></td>
<%
	if(isSuperAdmin)
	{
%>
<td class="tabcol"><%=instanceLoadVO.getCurrentTPS()%></td>
<%
	}
%>
<td class="tabcol"><%=instanceLoadVO.getRecievedCount()%></td>
<td class="tabcol"><%=instanceLoadVO.getCurrentTransactionLoad()%></td>
<%
	if(isSuperAdmin)
	{
%>
<td class="tabcol"><%=instanceLoadVO.getNoOfRequestSameSec()%></td>
<%
	}
%>
<td class="tabcol"><%=instanceLoadVO.getRequestCount()%></td>
<td class="tabcol"><%=instanceLoadVO.getTotalRefusedCount()%></td>
<td class="tabcol"><%=instanceLoadVO.getLastReceievedTime()%></td>
<td class="tabcol"><%=instanceLoadVO.getLastRefusedTime()%></td>
<td class="tabcol"><%=instanceLoadVO.getLastTxnProcessStartTime()%></td>
<td class="tabcol"><%=instanceLoadVO.getCurrentStatus()%></td>
<td class="tabcol"><%=instanceLoadVO.getHostAddress()%></td>
<td class="tabcol"><%=instanceLoadVO.getHostPort()%></td>
</tr>
<%
}
%>
<tr>
<td class="tabcol" colspan="16"></td>
</tr>
<tr rowspan="2">
<td class="tabcol" colspan="3"><b>TOTAL</b></td>
<td class="tabcol"><b><%=totCurrentLoad%></b></td>
<%
	if(isSuperAdmin)
	{
%>
<td class="tabcol"><b><%=totCurrentTPS%></b></td>
<%
	}
%>
<td class="tabcol"><b><%=totRequestRecieved%></b></td>
<td class="tabcol"><b><%=totCurrentUPCount%></b></td>
<%
	if(isSuperAdmin)
	{
%>
<td class="tabcol"><b><%=totCurrentReqInSameSec%></b></td>
<%
	}
%>
<td class="tabcol"><b><%=totRequestProcessed%></b></td>
<td class="tabcol"><b><%=totalRefusedCount%></b></td>
<td class="tabcol" colspan="6"></td>
</tr></table>
<jsp:include page="/monitorserver/common/bottomband.jsp"/>
</body>
</html>