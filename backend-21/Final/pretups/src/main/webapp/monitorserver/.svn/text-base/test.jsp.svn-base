<%--
/**
 *
 * @(#)interfaceLoadCounters.jsp
 *  ------------------------------------------------------------------
 *  Author 		Date(DD/MM/YYYY)	History
 *  ------------------------------------------------------------------
 * Gurjeet              14/07/2005			Creation
 *  ------------------------------------------------------------------
 */

--%><html>
<head>
<title>eRecharge Monitor Server</title>
<jsp:include page="/monitorserver/common/topband.jsp">
<jsp:param name="helpFile" value="/help/pretupsRequestCounters.jsp" />
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
<div class="heading"><center><strong>eRecharge Request Counters:</strong></center></div>
<br/>
<%
Hashtable networkServiceLoadHash = LoadControllerCache.getNetworkServiceLoadHash();
Set keySet = networkServiceLoadHash.keySet();
java.util.Iterator tempItr = keySet.iterator();
java.util.Iterator itr = keySet.iterator();
NetworkServiceLoadVO networkServiceLoadVO = null;
String tempkey=null;
if(tempItr.hasNext())
{
	tempkey = (String)tempItr.next();
	networkServiceLoadVO = (NetworkServiceLoadVO)networkServiceLoadHash.get(tempkey);
	%>
	<p class ="message">Last Initialized time:&nbsp;<%=networkServiceLoadVO.getLastInitializationTime()%></p>
	<br>
	<%
}
networkServiceLoadVO = null;
String key = null;
%>
<td class="tabhead">Sr no.</td>
<td class="tabhead">Service Type</td>
<td class="tabhead">Gateway Type</td>
<td class="tabhead">Service Name</td>
<td class="tabhead">Received Counts</td>
<td class="tabhead">Success Counts</td>
<td class="tabhead">Fail Counts</td>
<td class="tabhead">Under Process Counts</td>
<td class="tabhead">Other Fail Counts</td>
<td class="tabhead">Other Network Request Counts</td>
<td class="tabhead">Last Received Time</td>
<td class="tabhead">Average Service Time(ms)</td>
<td class="tabhead">Last Request Service Time(ms)</td>
<td class="tabhead">Before Gateway Found Error</td>
<td class="tabhead">Before Network Found Error</td>
<td class="tabhead">Before Service Type Found Error</td>
<td class="tabhead">Last Request ID</td>
</tr>
<%
ArrayList tempList=new ArrayList();

while(itr.hasNext())
{
	key = (String)itr.next();
    networkServiceLoadVO = (NetworkServiceLoadVO)networkServiceLoadHash.get(key);
	tempList.add(networkServiceLoadVO);
}
Collections.sort(tempList);
String instanceID=null;
String networkCode=null;
String module=null;
String serviceType=null; 
String serviceName=null;
double counter=0;
int receivedCount=0,successCount=0,failCount=0,underProcessCount=0,otherFailCount=0,otherNetwrkReqCount=0,avgServiceTime=0,beforGatewayFoundError=0,beforNetworkFoundError=0,beforSrvcTypeFoundError=0;
double lastReqSrvcTime=0;

networkServiceLoadVO = (NetworkServiceLoadVO)tempList.get(0);
double min=networkServiceLoadVO.getLastRequestServiceTime();

for(int i=1;i<tempList.size();i++)
{
	if(serviceType==null)
	{
		serviceType=networkServiceLoadVO.getServiceType();
	}
	if(instanceID==null||!instanceID.equals(networkServiceLoadVO.getInstanceID()))
	{
		instanceID=networkServiceLoadVO.getInstanceID();
		%>
		<tr>
		<td class="tabhead" colspan="18">Instance : <%=instanceID%></td>
		<tr>
		<%
	}
	if(networkCode==null||!networkCode.equals(networkServiceLoadVO.getNetworkCode()))
	{
		networkCode=networkServiceLoadVO.getNetworkCode();
		%>
		<tr>
		<td class="tabhead" colspan="18">Network : <%=networkCode%></td>
		<tr>
		<%
	}
	if(module==null)
	{
		module=networkServiceLoadVO.getModuleCode();
		
		%>
		<tr>
		<td class="tabhead" colspan="18">Module : <%=(module==null?"N.A":module)%></td>
		<tr>
		<%
	}
	if(serviceType.equals(networkServiceLoadVO.getServiceType()))
	{
		%>
	<tr>
	<td class="tabcol"><%=(i)%></td>
	<td class="tabcol"><%=networkServiceLoadVO.getServiceType()%></td>
	<td class="tabcol"><%=networkServiceLoadVO.getGatewayType()%></td>
	<td class="tabcol"><%=networkServiceLoadVO.getServiceName()%></td>
	<%serviceName=networkServiceLoadVO.getServiceName();%>
	<td class="tabcol"><%=networkServiceLoadVO.getRecievedCount()%></td>
	<%receivedCount += networkServiceLoadVO.getRecievedCount();%>
	
	<td class="tabcol"><%=networkServiceLoadVO.getSuccessCount()%></td>
	<%successCount += networkServiceLoadVO.getSuccessCount();%>
	<td class="tabcol"><%=networkServiceLoadVO.getFailCount()%></td>
	<%failCount += networkServiceLoadVO.getFailCount();%>
	<td class="tabcol"><%=networkServiceLoadVO.getUnderProcessCount()%></td>
	<%underProcessCount += networkServiceLoadVO.getUnderProcessCount();%>
	<td class="tabcol"><%=networkServiceLoadVO.getOthersFailCount()%></td>
	<%otherFailCount += networkServiceLoadVO.getOthersFailCount();%>
	<td class="tabcol"><%=networkServiceLoadVO.getOtherNetworkReqCount()%></td>
	<%otherNetwrkReqCount += networkServiceLoadVO.getOtherNetworkReqCount();%>
	<td class="tabcol"><%=networkServiceLoadVO.getLastReceievedTime()%></td>
	<td class="tabcol"><%=BTSLUtil.roundToStr(networkServiceLoadVO.getAverageServiceTime(),3)%></td>
	<%avgServiceTime += networkServiceLoadVO.getAverageServiceTime();     
	counter+=(networkServiceLoadVO.getRecievedCount()*networkServiceLoadVO.getAverageServiceTime());%>
	<td class="tabcol"><%=BTSLUtil.roundToStr(networkServiceLoadVO.getLastRequestServiceTime(),3)%></td>
	<%lastReqSrvcTime = networkServiceLoadVO.getLastRequestServiceTime();
	if(min >= lastReqSrvcTime && min!=0 && lastReqSrvcTime!=0)
	{
		min=lastReqSrvcTime;
	}
	%>
	<td class="tabcol"><%=networkServiceLoadVO.getBeforeGatewayFoundError()%></td>
	<%beforGatewayFoundError += networkServiceLoadVO.getBeforeGatewayFoundError();%>
	<td class="tabcol"><%=networkServiceLoadVO.getBeforeNetworkFoundError()%></td>
	<%beforNetworkFoundError += networkServiceLoadVO.getBeforeNetworkFoundError();%>
	<td class="tabcol"><%=networkServiceLoadVO.getBeforeServiceTypeFoundError()%></td>
	<%beforSrvcTypeFoundError += networkServiceLoadVO.getBeforeServiceTypeFoundError();%>
	<td class="tabcol"><%=networkServiceLoadVO.getLastRequestID()%></td>
	</tr>
	<%
	networkServiceLoadVO = (NetworkServiceLoadVO)tempList.get(i);
	
	} 
	else
	{%>
		<tr>
		<td class="tabcol"></td>
		<td class="tabcol"><b><%=serviceType%></td>
		<td class="tabcol"></td>
		<td class="tabcol"><b><%=serviceName%></td>
		<td class="tabcol"><b><%=receivedCount%></td>
		<td class="tabcol"><b><%=successCount%></td>
		<td class="tabcol"><b><%=failCount%></td>
		<td class="tabcol"><b><%=underProcessCount%></td>
		<td class="tabcol"><b><%=otherFailCount%></td>
		<td class="tabcol"><b><%=otherNetwrkReqCount%></td>
		<td class="tabcol"><b></td>
		<td class="tabcol"><b><%if(receivedCount>0)
		{%><%=BTSLUtil.roundToStr((counter/receivedCount),3)%>
		<%  } else
		 %>
		<%=BTSLUtil.roundToStr((0),3)%></td>
		<td class="tabcol"><b><%=BTSLUtil.roundToStr(min,3)%></td>
		<td class="tabcol"><b><%=beforGatewayFoundError%></td>
		<td class="tabcol"><b><%=beforNetworkFoundError%></td>
		<td class="tabcol"><b><%=beforSrvcTypeFoundError%></td>
	    </tr>
	    <% if(!module.equals(networkServiceLoadVO.getModuleCode()))
			{
				module=networkServiceLoadVO.getModuleCode();
		
		%>
		<tr>
		<td class="tabhead" colspan="18">Module : <%=(module==null?"N.A":module)%></td>
		<tr>
		<%
			}
		%>
	    <%receivedCount=successCount=failCount=underProcessCount=otherFailCount=otherNetwrkReqCount=avgServiceTime=beforGatewayFoundError=beforNetworkFoundError=beforSrvcTypeFoundError=0;
	    serviceType=networkServiceLoadVO.getServiceType();
	    min=networkServiceLoadVO.getLastRequestServiceTime();
	    i--;
	    counter=0;
	 }
	    %>
	
	
	<%
}
%>
<tr>
		<td class="tabcol"></td>
		<td class="tabcol"><b><%=serviceType%></td>
		<td class="tabcol"></td>
		<td class="tabcol"><b><%=serviceName%></td>
		<td class="tabcol"><b><%=receivedCount%></td>
		<td class="tabcol"><b><%=successCount%></td>
		<td class="tabcol"><b><%=failCount%></td>
		<td class="tabcol"><b><%=underProcessCount%></td>
		<td class="tabcol"><b><%=otherFailCount%></td>
		<td class="tabcol"><b><%=otherNetwrkReqCount%></td>
		<td class="tabcol"><b></td>
		<td class="tabcol"><b><%if(receivedCount>0)
		{%><%=BTSLUtil.roundToStr((counter/receivedCount),3)%>
		<%  } else
		 %>
		<%=BTSLUtil.roundToStr((0),3)%></td>
		<td class="tabcol"><b><%=BTSLUtil.roundToStr(min,3)%></td>
		<td class="tabcol"><b><%=beforGatewayFoundError%></td>
		<td class="tabcol"><b><%=beforNetworkFoundError%></td>
		<td class="tabcol"><b><%=beforSrvcTypeFoundError%></td>
</table>
<jsp:include page="/monitorserver/common/bottomband.jsp"/>
</body>
</html>

