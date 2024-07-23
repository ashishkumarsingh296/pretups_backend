<%
/**
 *
 * @(#)transactionLoadCounters.jsp
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
<jsp:param name="helpFile" value="/help/pretupsTransactionCounters.jsp" />
</jsp:include>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="<%= request.getContextPath()%>/monitorserver/common/main.css" rel="stylesheet" type="text/css">
</head><body>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
<table border="1" cellspacing="0" cellpadding="0"  >
<tr>
<%@ page import="com.btsl.loadcontroller.*,java.util.*" %>
<%@ page import="com.btsl.util.Constants" %>
<%@ page import="com.btsl.util.BTSLUtil" %>
<div class="heading"><center><strong>eRecharge Transaction Counters:</strong></center></div>
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

Hashtable hash = LoadControllerCache.getTransactionLoadHash();
Set keySet = hash.keySet();
java.util.Iterator tempItr = keySet.iterator();
java.util.Iterator itr = keySet.iterator();
TransactionLoadVO transactionLoadVO = null;
String tempkey=null;
if(tempItr.hasNext())
{
	tempkey = (String)tempItr.next();
	transactionLoadVO = (TransactionLoadVO)hash.get(tempkey);
%>
<p class ="message">Last Initialized time:&nbsp;<%=transactionLoadVO.getLastInitializationTime()%></p>
<br>
<%
}
transactionLoadVO = null;
String key = null;
int count =0;
%>
<td class="tabhead">Sr no.</td>
<td class="tabhead">Network Code</td>
<td class="tabhead">Interface ID</td>
<td class="tabhead">Service Type</td>
<td class="tabhead">Allowed Load</td>
<%
	if(isSuperAdmin)
	{
%>
<td class="tabhead">Allowed TPS</td>
<td class="tabhead">Overflow Size</td>
<%
	}
%>
<td class="tabhead">Recieved Count</td>
<td class="tabhead">Current Count</td>
<%
	if(isSuperAdmin)
	{
%>
<td class="tabhead">Current Overflow Size</td>
<td class="tabhead">Request In Same Sec</td>
<%
	}
%>
<td class="tabhead">Total Sender Validation</td>
<td class="tabhead">Current Sender Validation</td>
<td class="tabhead">Total Receiver Validation</td>
<td class="tabhead">Current Receiver Validation</td>
<td class="tabhead">Total Sender Topup</td>
<td class="tabhead">Current Sender Topup</td>
<td class="tabhead">Total Receiver Topup</td>
<td class="tabhead">Current Receiver Topup</td>
<td class="tabhead">Total Internal Fail</td>
<td class="tabhead">Total Sender Val Fail</td>
<td class="tabhead">Total Receiver Val Fail</td>
<td class="tabhead">Total Sender Topup Fail</td>
<td class="tabhead">Total Receiver Topup Fail</td>

<td class="tabhead">Total Request Count</td>
<td class="tabhead">Total Refused Count</td>
<td class="tabhead">Last Received Time</td>
<td class="tabhead">Last Refused Time</td>
<td class="tabhead">Last Under Process Time</td>
</tr>
<%
long totCurrentLoad=0;
long totCurrentTPS=0;
long totRequestRecieved=0;
long totCurrentUPCount=0;
long totCurrentReqInSameSec=0;
long totRequestProcessed=0;
long totalRefusedCount=0;
long totalOverflowSizeCount=0;
long totalCurrentOverflowSizeCount=0;

long totSenderVal=0;
long totCurrentSenderVal=0;
long totReceiverVal=0;
long totCurrentReceiverVal=0;
long totSenderTopup=0;
long totCurrentSenderTopup=0;
long totReceiverTopup=0;
long totCurrentReceiverTopup=0;
long totInternalVal=0;
long totSenderValFail=0;
long totReceiverValFail=0;
long totSenderTopFail=0;
long totReceiverTopFail=0;



while(itr.hasNext())
{
	key = (String)itr.next();
    transactionLoadVO = (TransactionLoadVO)hash.get(key);
	count = count + 1;
	totCurrentLoad +=transactionLoadVO.getTransactionLoad();
	totCurrentTPS +=transactionLoadVO.getCurrentTPS();
	totRequestRecieved +=transactionLoadVO.getRecievedCount();
	totCurrentUPCount +=transactionLoadVO.getCurrentTransactionLoad();
	totCurrentReqInSameSec +=transactionLoadVO.getNoOfRequestSameSec();
	totRequestProcessed +=transactionLoadVO.getRequestCount();
	totalRefusedCount +=transactionLoadVO.getTotalRefusedCount();
	totalOverflowSizeCount +=transactionLoadVO.getDefinedOverFlowCount();
	totalCurrentOverflowSizeCount +=transactionLoadVO.getOverFlowCount();

	totSenderVal+=transactionLoadVO.getTotalSenderValidationCount();
	totCurrentSenderVal+=transactionLoadVO.getCurrentSenderValidationCount();
	totReceiverVal+=transactionLoadVO.getTotalRecieverValidationCount();
	totCurrentReceiverVal+=transactionLoadVO.getCurrentRecieverValidationCount();
	totSenderTopup+=transactionLoadVO.getTotalSenderTopupCount();
	totCurrentSenderTopup+=transactionLoadVO.getCurrentSenderTopupCount();
	totReceiverTopup+=transactionLoadVO.getTotalRecieverTopupCount();
	totCurrentReceiverTopup+=transactionLoadVO.getCurrentRecieverTopupCount();
	totInternalVal+=transactionLoadVO.getTotalInternalFailCount();
	totSenderValFail+=transactionLoadVO.getTotalSenderValFailCount();
	totReceiverValFail+=transactionLoadVO.getTotalRecieverValFailCount();
	totSenderTopFail+=transactionLoadVO.getTotalSenderTopupFailCount();
	totReceiverTopFail+=transactionLoadVO.getTotalRecieverTopupFailCount();

%>
<tr>
<td class="tabcol"><%=count%></td>
<td class="tabcol"><%=transactionLoadVO.getNetworkCode()%></td>
<td class="tabcol"><%=transactionLoadVO.getInterfaceID()%></td>
<td class="tabcol"><%=transactionLoadVO.getServiceType()%></td>
<td class="tabcol"><%=transactionLoadVO.getTransactionLoad()%></td>
<%
	if(isSuperAdmin)
	{
%>
<td class="tabcol"><%=transactionLoadVO.getCurrentTPS()%></td>
<td class="tabcol"><%=transactionLoadVO.getDefinedOverFlowCount()%></td>
<%
	}
%>
<td class="tabcol"><%=transactionLoadVO.getRecievedCount()%></td>
<td class="tabcol"><%=transactionLoadVO.getCurrentTransactionLoad()%></td>
<%
	if(isSuperAdmin)
	{
%>
<td class="tabcol"><%=transactionLoadVO.getOverFlowCount()%></td>
<td class="tabcol"><%=transactionLoadVO.getNoOfRequestSameSec()%></td>
<%
	}
%>
<td class="tabcol"><%=transactionLoadVO.getTotalSenderValidationCount()%></td>
<td class="tabcol"><%=transactionLoadVO.getCurrentSenderValidationCount()%></td>
<td class="tabcol"><%=transactionLoadVO.getTotalRecieverValidationCount()%></td>
<td class="tabcol"><%=transactionLoadVO.getCurrentRecieverValidationCount()%></td>
<td class="tabcol"><%=transactionLoadVO.getTotalSenderTopupCount()%></td>
<td class="tabcol"><%=transactionLoadVO.getCurrentSenderTopupCount()%></td>
<td class="tabcol"><%=transactionLoadVO.getTotalRecieverTopupCount()%></td>
<td class="tabcol"><%=transactionLoadVO.getCurrentRecieverTopupCount()%></td>
<td class="tabcol"><%=transactionLoadVO.getTotalInternalFailCount()%></td>
<td class="tabcol"><%=transactionLoadVO.getTotalSenderValFailCount()%></td>
<td class="tabcol"><%=transactionLoadVO.getTotalRecieverValFailCount()%></td>
<td class="tabcol"><%=transactionLoadVO.getTotalSenderTopupFailCount()%></td>
<td class="tabcol"><%=transactionLoadVO.getTotalRecieverTopupFailCount()%></td>


<td class="tabcol"><%=transactionLoadVO.getRequestCount()%></td>
<td class="tabcol"><%=transactionLoadVO.getTotalRefusedCount()%></td>
<td class="tabcol"><%=transactionLoadVO.getLastReceievedTime()%></td>
<td class="tabcol"><%=transactionLoadVO.getLastRefusedTime()%></td>
<td class="tabcol"><%=transactionLoadVO.getLastTxnProcessStartTime()%></td>

</tr>
<%
}
%>
<tr>
<td class="tabcol" colspan="28"></td>
</tr>
<tr rowspan="2">
<td class="tabcol" colspan="4"><b>TOTAL</b></td>
<td class="tabcol"><b><%=totCurrentLoad%></b></td>
<%
	if(isSuperAdmin)
	{
%>
<td class="tabcol"><b><%=totCurrentTPS%></b></td>
<td class="tabcol"><b><%=totalOverflowSizeCount%></b></td>
<%
	}
%>
<td class="tabcol"><b><%=totRequestRecieved%></b></td>
<td class="tabcol"><b><%=totCurrentUPCount%></b></td>
<%
	if(isSuperAdmin)
	{
%>
<td class="tabcol"><b><%=totalCurrentOverflowSizeCount%></b></td>
<td class="tabcol"><b><%=totCurrentReqInSameSec%></b></td>
<%
	}
%>

<td class="tabcol"><b><%=totSenderVal%></b></td>
<td class="tabcol"><b><%=totCurrentSenderVal%></b></td>
<td class="tabcol"><b><%=totReceiverVal%></b></td>
<td class="tabcol"><b><%=totCurrentReceiverVal%></b></td>
<td class="tabcol"><b><%=totSenderTopup%></b></td>
<td class="tabcol"><b><%=totCurrentSenderTopup%></b></td>
<td class="tabcol"><b><%=totReceiverTopup%></b></td>
<td class="tabcol"><b><%=totCurrentReceiverTopup%></b></td>
<td class="tabcol"><b><%=totInternalVal%></b></td>
<td class="tabcol"><b><%=totSenderValFail%></b></td>
<td class="tabcol"><b><%=totReceiverValFail%></b></td>
<td class="tabcol"><b><%=totSenderTopFail%></b></td>
<td class="tabcol"><b><%=totReceiverTopFail%></b></td>

<td class="tabcol"><b><%=totRequestProcessed%></b></td>
<td class="tabcol"><b><%=totalRefusedCount%></b></td>
<td class="tabcol" colspan="3"></td>
</tr></table>
<jsp:include page="/monitorserver/common/bottomband.jsp"/>
</body>
</html>
