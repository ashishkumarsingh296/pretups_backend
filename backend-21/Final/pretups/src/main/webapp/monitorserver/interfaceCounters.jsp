<%
/**
 *
 * @(#)interfaceLoadCounters.jsp
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
<jsp:param name="helpFile" value="/help/pretupsInterfaceCounters.jsp" />
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

<div class="heading"><center><strong>eRecharge Interface Counters:</strong></center></div>
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
Hashtable interfaceHash = LoadControllerCache.getInterfaceLoadHash();
Set keySet = interfaceHash.keySet();
java.util.Iterator tempItr = keySet.iterator();
java.util.Iterator itr = keySet.iterator();
InterfaceLoadVO interfaceLoadVO = null;
String tempkey=null;
if(tempItr.hasNext())
{
	tempkey = (String)tempItr.next();
	interfaceLoadVO = (InterfaceLoadVO)interfaceHash.get(tempkey);
%>
<p class ="message">Last Initialized time:&nbsp;<%=interfaceLoadVO.getLastInitializationTime()%></p>
<br>
<%
}
interfaceLoadVO = null;
String key = null;
int count =0;
%>
<td class="tabhead">Sr no.</td>
<td class="tabhead">Instance ID</td>
<td class="tabhead">Network Code</td>
<td class="tabhead">Interface ID</td>
<td class="tabhead">Allowed Load</td>
<%
	if(isSuperAdmin)
	{
%>
<td class="tabhead">Allowed TPS</td>
<%
	}
%>
<td class="tabhead">Queue Size</td>
<td class="tabhead">Recieved Count</td>
<td class="tabhead">Current Count</td>
<td class="tabhead">Current Queue Size</td>
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
<td class="tabhead">Last Queue Addition Time</td>
</tr>
<%
long totCurrentLoad=0;
long totCurrentTPS=0;
long totRequestRecieved=0;
long totCurrentUPCount=0;
long totCurrentReqInSameSec=0;
long totRequestProcessed=0;
long totalRefusedCount=0;
long totalQueueSizeCount=0;
long totalCurrentQueueSizeCount=0;

while(itr.hasNext())
{
	key = (String)itr.next();
    interfaceLoadVO = (InterfaceLoadVO)interfaceHash.get(key);
	count = count + 1;
	totCurrentLoad +=interfaceLoadVO.getTransactionLoad();
	totCurrentTPS +=interfaceLoadVO.getCurrentTPS();
	totRequestRecieved +=interfaceLoadVO.getRecievedCount();
	totCurrentUPCount +=interfaceLoadVO.getCurrentTransactionLoad();
	totCurrentReqInSameSec +=interfaceLoadVO.getNoOfRequestSameSec();
	totRequestProcessed +=interfaceLoadVO.getRequestCount();
	totalRefusedCount +=interfaceLoadVO.getTotalRefusedCount();
	totalQueueSizeCount +=interfaceLoadVO.getQueueSize();
	totalCurrentQueueSizeCount +=interfaceLoadVO.getCurrentQueueSize();
%>
<tr>
<td class="tabcol"><%=count%></td>
<td class="tabcol"><%=interfaceLoadVO.getInstanceID()%></td>
<td class="tabcol"><%=interfaceLoadVO.getNetworkCode()%></td>
<td class="tabcol"><%=interfaceLoadVO.getInterfaceID()%></td>
<td class="tabcol"><%=interfaceLoadVO.getTransactionLoad()%></td>
<%
	if(isSuperAdmin)
	{
%>
<td class="tabcol"><%=interfaceLoadVO.getCurrentTPS()%></td>
<%
	}
%>
<td class="tabcol"><%=interfaceLoadVO.getQueueSize()%></td>
<td class="tabcol"><%=interfaceLoadVO.getRecievedCount()%></td>
<td class="tabcol"><%=interfaceLoadVO.getCurrentTransactionLoad()%></td>
<td class="tabcol"><%=interfaceLoadVO.getCurrentQueueSize()%></td>
<%
	if(isSuperAdmin)
	{
%>
<td class="tabcol"><%=interfaceLoadVO.getNoOfRequestSameSec()%></td>
<%
	}
%>
<td class="tabcol"><%=interfaceLoadVO.getRequestCount()%></td>
<td class="tabcol"><%=interfaceLoadVO.getTotalRefusedCount()%></td>
<td class="tabcol"><%=interfaceLoadVO.getLastReceievedTime()%></td>
<td class="tabcol"><%=interfaceLoadVO.getLastRefusedTime()%></td>
<td class="tabcol"><%=interfaceLoadVO.getLastTxnProcessStartTime()%></td>
<td class="tabcol"><%=interfaceLoadVO.getLastQueueAdditionTime()%></td>

</tr>
<%
}
%>
<tr>
<td class="tabcol" colspan="17"></td>
</tr>
<tr rowspan="2">
<td class="tabcol" colspan="4"><b>TOTAL</b></td>
<td class="tabcol"><b><%=totCurrentLoad%></b></td>
<%
	if(isSuperAdmin)
	{
%>
<td class="tabcol"><b><%=totCurrentTPS%></b></td>
<%
	}
%>
<td class="tabcol"><b><%=totalQueueSizeCount%></b></td>
<td class="tabcol"><b><%=totRequestRecieved%></b></td>
<td class="tabcol"><b><%=totCurrentUPCount%></b></td>
<td class="tabcol"><b><%=totalCurrentQueueSizeCount%></b></td>
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
<td class="tabcol" colspan="4"></td>
</tr></table>
<jsp:include page="/monitorserver/common/bottomband.jsp"/>
</body>
</html>
