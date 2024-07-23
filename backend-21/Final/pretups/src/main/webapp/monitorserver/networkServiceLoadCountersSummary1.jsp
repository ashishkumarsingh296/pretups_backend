<%--
/**
 *
 * @(#)networkServiceLoadCountersSummary.jsp
 *  ------------------------------------------------------------------
 *  Author 		           Date(DD/MM/YYYY)	    History
 *  ------------------------------------------------------------------
 * Nitin Rohilla             02/05/2006			Creation
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

<div class="heading"><center><strong>eRecharge Request Counters Summary</strong></center></div>
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
<td class="tabhead" >Sr no.</td><%--width=5%
--%><td class="tabhead" colspan="2" >Service Type</td><%--width=10%
--%><td class="tabhead" >Service Name</td><%--width=15%
--%><td class="tabhead" >Received Counts</td><%--width=5%
--%><td class="tabhead" >Success Counts</td><%--width=5%
--%><td class="tabhead" >Fail Counts</td><%--width=5%
--%><td class="tabhead" >Under Process Counts</td><%--width=5%
--%><td class="tabhead" >Other Fail Counts</td><%--width=5%
--%><td class="tabhead" >Other Network Request Counts</td><%--width=5%
--%><td class="tabhead">Before Gateway Found Error</td>
<td class="tabhead">Before Network Found Error</td>
<td class="tabhead">Before Service Type Found Error</td>
<td class="tabhead">Last Received Time</td><%--width=20%
--%><td class="tabhead" >Average Service Time(ms)</td><%--width=10%
--%><td class="tabhead" >Last Request Service Time(ms)</td><%--width=10%

--%></tr>
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
Date  lastRcvdTime=null;
double counter=0;
long bforGtwayFoundErr=0, bforGtwayFoundErrTtl=0, bforNtwrkFoundErr=0, bforNtwrkFoundErrTtl=0, bforSrvcTypeFoundErr=0, bforSrvcTypeFoundErrTtl=0;
int receivedCount=0,successCount=0,failCount=0,underProcessCount=0,otherFailCount=0,otherNetwrkReqCount=0,avgServiceTime=0;
int receivedCountTtl=0,successCountTtl=0,failCountTtl=0,underProcessCountTtl=0,otherFailCountTtl=0,otherNetwrkReqCountTtl=0,avgServiceTimeTtl=0;
double lastReqSrvcTime=0;

networkServiceLoadVO = (NetworkServiceLoadVO)tempList.get(0);
double min=networkServiceLoadVO.getLastRequestServiceTime();
Date max = BTSLUtil.getUtilDateFromTimestamp(networkServiceLoadVO.getLastReceievedTime());
int i=1,j=0;
boolean showParam=false;
boolean showNetworkParam=false;
boolean showModuleParam=false;
boolean showInstanceParam=false;
boolean showLastDetails=false;
for(i=0;i<tempList.size();i++)
{
	networkServiceLoadVO = (NetworkServiceLoadVO)tempList.get(i);
	System.out.println("i="+i+" "+networkServiceLoadVO.getInstanceID()+networkServiceLoadVO.getNetworkCode()+networkServiceLoadVO.getServiceType()+networkServiceLoadVO.getModuleCode()+networkServiceLoadVO.getGatewayType());
	if(instanceID==null) instanceID=BTSLUtil.NullToString(networkServiceLoadVO.getInstanceID());
	if(serviceType==null) serviceType=BTSLUtil.NullToString(networkServiceLoadVO.getServiceType());
	if(networkCode==null) networkCode=BTSLUtil.NullToString(networkServiceLoadVO.getNetworkCode());
	if(module==null ) module=BTSLUtil.NullToString(networkServiceLoadVO.getModuleCode());

	if(instanceID.equals(BTSLUtil.NullToString(networkServiceLoadVO.getInstanceID())))
	{
		showInstanceParam=false;
		if(networkCode.equals(BTSLUtil.NullToString(networkServiceLoadVO.getNetworkCode())))
		{
			showNetworkParam=false;
			if(module.equals(BTSLUtil.NullToString(networkServiceLoadVO.getModuleCode())))
			{
					showModuleParam=false;
					if(serviceType.equals(BTSLUtil.NullToString(networkServiceLoadVO.getServiceType())))
					{
						serviceName=networkServiceLoadVO.getServiceName();
						receivedCount += networkServiceLoadVO.getRecievedCount();
						successCount += networkServiceLoadVO.getSuccessCount();
						failCount += networkServiceLoadVO.getFailCount();
						underProcessCount += networkServiceLoadVO.getUnderProcessCount();
						otherFailCount += networkServiceLoadVO.getOthersFailCount();
						otherNetwrkReqCount += networkServiceLoadVO.getOtherNetworkReqCount();
						bforGtwayFoundErr += networkServiceLoadVO.getBeforeGatewayFoundError();	
						bforNtwrkFoundErr += networkServiceLoadVO.getBeforeNetworkFoundError();	
						bforSrvcTypeFoundErr += networkServiceLoadVO.getBeforeServiceTypeFoundError();
						lastRcvdTime = BTSLUtil.getUtilDateFromTimestamp(networkServiceLoadVO.getLastReceievedTime());
						if(max.getTime()<lastRcvdTime.getTime())
						{
							max=lastRcvdTime;
						}
						avgServiceTime += networkServiceLoadVO.getAverageServiceTime();    
						counter+=(networkServiceLoadVO.getRecievedCount()*networkServiceLoadVO.getAverageServiceTime()); 
						lastReqSrvcTime = networkServiceLoadVO.getLastRequestServiceTime();
						if(min > lastReqSrvcTime  && lastReqSrvcTime!=0.0)
						{
							min=lastReqSrvcTime;
						}
						if( min==0.0)
						{
							min=lastReqSrvcTime;
						}
						/*
						instanceID=networkServiceLoadVO.getInstanceID();
						serviceType=networkServiceLoadVO.getServiceType();
						networkCode=networkServiceLoadVO.getNetworkCode();
						module=networkServiceLoadVO.getModuleCode();
						serviceName=networkServiceLoadVO.getServiceName();
						*/
					}
					else
					{
						j++;
						if(!showParam)
						{
						%>
						<tr>
						<td class="tabhead" colspan="17">Instance : <%=instanceID%></td>
						</tr>
						<tr>
						<td class="tabhead" colspan="17">Network : <%=networkCode%></td>
						</tr>
						<tr>
						<td class="tabhead" colspan="17">Module : <%=module%></td>
						</tr>
						<%
						showParam=true;
						}%>
						<tr>
						<td class="tabcol"><%=(j)%></td>
						<td class="tabcol" colspan="2"><%=serviceType%></td>
						<td class="tabcol"><%=serviceName%></td>
						<td class="tabcol"><%=receivedCount%></td>
						<%receivedCountTtl+=receivedCount;%>
						<td class="tabcol"><%=successCount%></td>
						<%successCountTtl+=successCount;%>
						<td class="tabcol"><%=failCount%></td>
						<%failCountTtl+=failCount;%>
						<td class="tabcol"><%=underProcessCount%></td>
						<%underProcessCountTtl+=underProcessCount;%>
						<td class="tabcol"><%=otherFailCount%></td>
						<%otherFailCountTtl+=otherFailCount;%>
						<td class="tabcol"><%=otherNetwrkReqCount%></td>
						<%otherNetwrkReqCountTtl+=otherNetwrkReqCount;%>
						<td class="tabcol"><%=bforGtwayFoundErr%></td>
						<%bforGtwayFoundErrTtl+=bforGtwayFoundErr;%>
						<td class="tabcol"><%=bforNtwrkFoundErr%></td>
						<%bforNtwrkFoundErrTtl+=bforNtwrkFoundErr;%>
						<td class="tabcol"><%=bforSrvcTypeFoundErr%></td>
						<%bforSrvcTypeFoundErrTtl+=bforSrvcTypeFoundErr;%>
						<td class="tabcol"><%=BTSLUtil.getTimestampFromUtilDate(max)%></td>
						<td class="tabcol">
						<%if(receivedCount>0)
						{%><%=BTSLUtil.roundToStr((counter/receivedCount),3)%>
						<%  } else
						{%><%=BTSLUtil.roundToStr((0),3)%></td><%}%>
						<td class="tabcol" colspan="2"><%=BTSLUtil.roundToStr(min,3)%></td>
						</tr>
						<%
						serviceName="";
					    counter=0;
						lastReqSrvcTime=0;
						receivedCount=successCount=failCount=underProcessCount=otherFailCount=otherNetwrkReqCount=avgServiceTime=0;
						bforGtwayFoundErr=bforNtwrkFoundErr=bforSrvcTypeFoundErr=0;
						serviceType=networkServiceLoadVO.getServiceType();
						min=networkServiceLoadVO.getLastRequestServiceTime();
						max = BTSLUtil.getUtilDateFromTimestamp(networkServiceLoadVO.getLastReceievedTime());
					    
						instanceID=networkServiceLoadVO.getInstanceID();
						serviceType=networkServiceLoadVO.getServiceType();
						networkCode=networkServiceLoadVO.getNetworkCode();
						module=networkServiceLoadVO.getModuleCode();
						serviceName=networkServiceLoadVO.getServiceName();
						receivedCount += networkServiceLoadVO.getRecievedCount();
						successCount += networkServiceLoadVO.getSuccessCount();
						failCount += networkServiceLoadVO.getFailCount();
						underProcessCount += networkServiceLoadVO.getUnderProcessCount();
						otherFailCount += networkServiceLoadVO.getOthersFailCount();
						otherNetwrkReqCount += networkServiceLoadVO.getOtherNetworkReqCount();
						bforGtwayFoundErr += networkServiceLoadVO.getBeforeGatewayFoundError();	
						bforNtwrkFoundErr += networkServiceLoadVO.getBeforeNetworkFoundError();	
						bforSrvcTypeFoundErr += networkServiceLoadVO.getBeforeServiceTypeFoundError();
						lastRcvdTime = BTSLUtil.getUtilDateFromTimestamp(networkServiceLoadVO.getLastReceievedTime());
						if(max.getTime()<lastRcvdTime.getTime())
						{
							max=lastRcvdTime;
						}
						avgServiceTime += networkServiceLoadVO.getAverageServiceTime();    
						counter+=(networkServiceLoadVO.getRecievedCount()*networkServiceLoadVO.getAverageServiceTime()); 
						lastReqSrvcTime = networkServiceLoadVO.getLastRequestServiceTime();
						if(min > lastReqSrvcTime  && lastReqSrvcTime!=0.0)
						{
							min=lastReqSrvcTime;
						}
						if( min==0.0)
						{
							min=lastReqSrvcTime;
						}
					}
					 

			}
			else
			{
				j++;
				if(!showParam)
				{
				%>
				<tr>
				<td class="tabhead" colspan="17">Instance : <%=instanceID%></td>
				</tr>
				<tr>
				<td class="tabhead" colspan="17">Network : <%=networkCode%></td>
				</tr>
				<tr>
				<td class="tabhead" colspan="17">Module : <%=module%></td>
				</tr>
				<%
				showParam=true;
				showModuleParam=false;
				}%>
			
				<tr>
				<td class="tabcol"><%=(j)%></td>
				<td class="tabcol" colspan="2"><%=serviceType%></td>
				<td class="tabcol"><%=serviceName%></td>
				<td class="tabcol"><%=receivedCount%></td>
				<%receivedCountTtl+=receivedCount;%>
				<td class="tabcol"><%=successCount%></td>
				<%successCountTtl+=successCount;%>
				<td class="tabcol"><%=failCount%></td>
				<%failCountTtl+=failCount;%>
				<td class="tabcol"><%=underProcessCount%></td>
				<%underProcessCountTtl+=underProcessCount;%>
				<td class="tabcol"><%=otherFailCount%></td>
				<%otherFailCountTtl+=otherFailCount;%>
				<td class="tabcol"><%=otherNetwrkReqCount%></td>
				<%otherNetwrkReqCountTtl+=otherNetwrkReqCount;%>
				<td class="tabcol"><%=bforGtwayFoundErr%></td>
				<%bforGtwayFoundErrTtl+=bforGtwayFoundErr;%>
				<td class="tabcol"><%=bforNtwrkFoundErr%></td>
				<%bforNtwrkFoundErrTtl+=bforNtwrkFoundErr;%>
				<td class="tabcol"><%=bforSrvcTypeFoundErr%></td>
				<%bforSrvcTypeFoundErrTtl+=bforSrvcTypeFoundErr;%>
				<td class="tabcol"><%=BTSLUtil.getTimestampFromUtilDate(max)%></td>
				<td class="tabcol">
				<%if(receivedCount>0)
				{%><%=BTSLUtil.roundToStr((counter/receivedCount),3)%>
				<%  } else
				 {%>
				<%=BTSLUtil.roundToStr((0),3)%></td><%}%>
				<td class="tabcol" colspan="2"><%=BTSLUtil.roundToStr(min,3)%></td>
				</tr>
				<%
				if(!showModuleParam)
				{
				%>
				<tr>
				<td class="tabhead" colspan="17">Module : <%=(networkServiceLoadVO.getModuleCode()==null?"N.A":networkServiceLoadVO.getModuleCode())%></td>
				</tr>
				<%
				showModuleParam=true;
				}				

				serviceName="";
				counter=0;
				lastReqSrvcTime=0;
			receivedCount=successCount=failCount=underProcessCount=otherFailCount=otherNetwrkReqCount=avgServiceTime=0;
			bforGtwayFoundErr=bforNtwrkFoundErr=bforSrvcTypeFoundErr=0;
				serviceType=networkServiceLoadVO.getServiceType();
				min=networkServiceLoadVO.getLastRequestServiceTime();
				max = BTSLUtil.getUtilDateFromTimestamp(networkServiceLoadVO.getLastReceievedTime());
				instanceID=networkServiceLoadVO.getInstanceID();
				serviceType=networkServiceLoadVO.getServiceType();
				networkCode=networkServiceLoadVO.getNetworkCode();
				module=networkServiceLoadVO.getModuleCode();
				serviceName=networkServiceLoadVO.getServiceName();
				receivedCount += networkServiceLoadVO.getRecievedCount();
				successCount += networkServiceLoadVO.getSuccessCount();
				failCount += networkServiceLoadVO.getFailCount();
				underProcessCount += networkServiceLoadVO.getUnderProcessCount();
				otherFailCount += networkServiceLoadVO.getOthersFailCount();
				otherNetwrkReqCount += networkServiceLoadVO.getOtherNetworkReqCount();
				bforGtwayFoundErr += networkServiceLoadVO.getBeforeGatewayFoundError();	
				bforNtwrkFoundErr += networkServiceLoadVO.getBeforeNetworkFoundError();	
				bforSrvcTypeFoundErr += networkServiceLoadVO.getBeforeServiceTypeFoundError();
				lastRcvdTime = BTSLUtil.getUtilDateFromTimestamp(networkServiceLoadVO.getLastReceievedTime());
				if(max.getTime()<lastRcvdTime.getTime())
				{
					max=lastRcvdTime;
				}
				avgServiceTime += networkServiceLoadVO.getAverageServiceTime();    
				counter+=(networkServiceLoadVO.getRecievedCount()*networkServiceLoadVO.getAverageServiceTime()); 
				lastReqSrvcTime = networkServiceLoadVO.getLastRequestServiceTime();
				if(min > lastReqSrvcTime  && lastReqSrvcTime!=0.0)
				{
					min=lastReqSrvcTime;
				}
				if( min==0.0)
				{
					min=lastReqSrvcTime;
				}
			}
		}
		else
		{
			j++;
			if(!showParam)
			{
			%>
			<tr>
			<td class="tabhead" colspan="17">Instance : <%=instanceID%></td>
			</tr>
			<tr>
			<td class="tabhead" colspan="17">Network : <%=networkCode%></td>
			</tr>
			<tr>
			<td class="tabhead" colspan="17">Module : <%=module%></td>
			</tr>
			<%
			showParam=true;
			showNetworkParam=false;
			}%>
			
			
			<tr>
			<td class="tabcol"><%=(j)%></td>
			<td class="tabcol" colspan="2"><%=serviceType%></td>
			<td class="tabcol"><%=serviceName%></td>
			<td class="tabcol"><%=receivedCount%></td>
			<%receivedCountTtl+=receivedCount;%>
			<td class="tabcol"><%=successCount%></td>
			<%successCountTtl+=successCount;%>
			<td class="tabcol"><%=failCount%></td>
			<%failCountTtl+=failCount;%>
			<td class="tabcol"><%=underProcessCount%></td>
			<%underProcessCountTtl+=underProcessCount;%>
			<td class="tabcol"><%=otherFailCount%></td>
			<%otherFailCountTtl+=otherFailCount;%>
			<td class="tabcol"><%=otherNetwrkReqCount%></td>
			<%otherNetwrkReqCountTtl+=otherNetwrkReqCount;%>
			<td class="tabcol"><%=bforGtwayFoundErr%></td>
			<%bforGtwayFoundErrTtl+=bforGtwayFoundErr;%>
			<td class="tabcol"><%=bforNtwrkFoundErr%></td>
			<%bforNtwrkFoundErrTtl+=bforNtwrkFoundErr;%>
			<td class="tabcol"><%=bforSrvcTypeFoundErr%></td>
			<%bforSrvcTypeFoundErrTtl+=bforSrvcTypeFoundErr;%>
			<td class="tabcol"><%=BTSLUtil.getTimestampFromUtilDate(max)%></td>
			<td class="tabcol">
			<%if(receivedCount>0)
			{%><%=BTSLUtil.roundToStr((counter/receivedCount),3)%>
			<%  } else
			 {%>
			<%=BTSLUtil.roundToStr((0),3)%></td><%}%>
			<td class="tabcol" colspan="2"><%=BTSLUtil.roundToStr(min,3)%></td>
			</tr>
			<%
			if(!showNetworkParam)
			{
			%>
		
			<tr>
			<td class="tabhead" colspan="17">Network : <%=(networkServiceLoadVO.getNetworkCode()==null?"N.A":networkServiceLoadVO.getNetworkCode())%></td>
			</tr>

			<%
			showNetworkParam=true;
			}%>
			<tr>
			<td class="tabhead" colspan="17">Module : <%=(networkServiceLoadVO.getModuleCode()==null?"N.A":networkServiceLoadVO.getModuleCode())%></td>
			</tr>
			<%
		
			serviceName="";
			counter=0;
			lastReqSrvcTime=0;
			receivedCount=successCount=failCount=underProcessCount=otherFailCount=otherNetwrkReqCount=avgServiceTime=0;
			bforGtwayFoundErr=bforNtwrkFoundErr=bforSrvcTypeFoundErr=0;

			serviceType=networkServiceLoadVO.getServiceType();
			min=networkServiceLoadVO.getLastRequestServiceTime();
			max = BTSLUtil.getUtilDateFromTimestamp(networkServiceLoadVO.getLastReceievedTime());
			instanceID=networkServiceLoadVO.getInstanceID();
			serviceType=networkServiceLoadVO.getServiceType();
			networkCode=networkServiceLoadVO.getNetworkCode();
			module=networkServiceLoadVO.getModuleCode();
			serviceName=networkServiceLoadVO.getServiceName();
			receivedCount += networkServiceLoadVO.getRecievedCount();
			successCount += networkServiceLoadVO.getSuccessCount();
			failCount += networkServiceLoadVO.getFailCount();
			underProcessCount += networkServiceLoadVO.getUnderProcessCount();
			otherFailCount += networkServiceLoadVO.getOthersFailCount();
			otherNetwrkReqCount += networkServiceLoadVO.getOtherNetworkReqCount();
			bforGtwayFoundErr += networkServiceLoadVO.getBeforeGatewayFoundError();	
			bforNtwrkFoundErr += networkServiceLoadVO.getBeforeNetworkFoundError();	
			bforSrvcTypeFoundErr += networkServiceLoadVO.getBeforeServiceTypeFoundError();
			lastRcvdTime = BTSLUtil.getUtilDateFromTimestamp(networkServiceLoadVO.getLastReceievedTime());
			if(max.getTime()<lastRcvdTime.getTime())
			{
				max=lastRcvdTime;
			}
			avgServiceTime += networkServiceLoadVO.getAverageServiceTime();    
			counter+=(networkServiceLoadVO.getRecievedCount()*networkServiceLoadVO.getAverageServiceTime()); 
			lastReqSrvcTime = networkServiceLoadVO.getLastRequestServiceTime();
			if(min > lastReqSrvcTime  && lastReqSrvcTime!=0.0)
			{
				min=lastReqSrvcTime;
			}
			if( min==0.0)
			{
				min=lastReqSrvcTime;
			}
		}

	}
	else
	{
		j++;
		if(!showParam)
		{
		%>
		<tr>
		<td class="tabhead" colspan="17">Instance : <%=instanceID%></td>
		</tr>
		<tr>
		<td class="tabhead" colspan="17">Network : <%=(networkCode==null?"N.A":networkCode)%></td>
		</tr>
		<tr>
		<td class="tabhead" colspan="17">Module : <%=(module==null?"N.A":module)%></td>
		</tr>
		<%
		showParam=true;
		showInstanceParam=false;
		}%>
			
		<td class="tabcol"><%=(j)%></td>
		<td class="tabcol" colspan="2"><%=serviceType%></td>
		<td class="tabcol"><%=serviceName%></td>
		<td class="tabcol"><%=receivedCount%></td>
		<%receivedCountTtl+=receivedCount;%>
		<td class="tabcol"><%=successCount%></td>
		<%successCountTtl+=successCount;%>
		<td class="tabcol"><%=failCount%></td>
		<%failCountTtl+=failCount;%>
		<td class="tabcol"><%=underProcessCount%></td>
		<%underProcessCountTtl+=underProcessCount;%>
		<td class="tabcol"><%=otherFailCount%></td>
		<%otherFailCountTtl+=otherFailCount;%>
		<td class="tabcol"><%=otherNetwrkReqCount%></td>
		<%otherNetwrkReqCountTtl+=otherNetwrkReqCount;%>
		<td class="tabcol"><%=bforGtwayFoundErr%></td>
		<%bforGtwayFoundErrTtl+=bforGtwayFoundErr;%>
		<td class="tabcol"><%=bforNtwrkFoundErr%></td>
		<%bforNtwrkFoundErrTtl+=bforNtwrkFoundErr;%>
		<td class="tabcol"><%=bforSrvcTypeFoundErr%></td>
		<%bforSrvcTypeFoundErrTtl+=bforSrvcTypeFoundErr;%>
		<td class="tabcol"><%=BTSLUtil.getTimestampFromUtilDate(max)%></td>
		<td class="tabcol">
		<%if(receivedCount>0)
		{%><%=BTSLUtil.roundToStr((counter/receivedCount),3)%>
		<%  } else
		 {%>
		<%=BTSLUtil.roundToStr((0),3)%></td><%}%>
		<td class="tabcol" colspan="2"><%=BTSLUtil.roundToStr(min,3)%></td>
		</tr>
		
		<%
		
		
		serviceName="";
		counter=0;
		lastReqSrvcTime=0;
		receivedCount=successCount=failCount=underProcessCount=otherFailCount=otherNetwrkReqCount=avgServiceTime=0;
		bforGtwayFoundErr=bforNtwrkFoundErr=bforSrvcTypeFoundErr=0;

		serviceType=networkServiceLoadVO.getServiceType();
		min=networkServiceLoadVO.getLastRequestServiceTime();
		max = BTSLUtil.getUtilDateFromTimestamp(networkServiceLoadVO.getLastReceievedTime());	
		instanceID=networkServiceLoadVO.getInstanceID();
		serviceType=networkServiceLoadVO.getServiceType();
		networkCode=networkServiceLoadVO.getNetworkCode();
		module=networkServiceLoadVO.getModuleCode();
		serviceName=networkServiceLoadVO.getServiceName();
		receivedCount += networkServiceLoadVO.getRecievedCount();
		successCount += networkServiceLoadVO.getSuccessCount();
		failCount += networkServiceLoadVO.getFailCount();
		underProcessCount += networkServiceLoadVO.getUnderProcessCount();
		otherFailCount += networkServiceLoadVO.getOthersFailCount();
		otherNetwrkReqCount += networkServiceLoadVO.getOtherNetworkReqCount();
		bforGtwayFoundErr += networkServiceLoadVO.getBeforeGatewayFoundError();	
		bforNtwrkFoundErr += networkServiceLoadVO.getBeforeNetworkFoundError();	
		bforSrvcTypeFoundErr += networkServiceLoadVO.getBeforeServiceTypeFoundError();
		lastRcvdTime = BTSLUtil.getUtilDateFromTimestamp(networkServiceLoadVO.getLastReceievedTime());
		if(max.getTime()<lastRcvdTime.getTime())
		{
			max=lastRcvdTime;
		}
		avgServiceTime += networkServiceLoadVO.getAverageServiceTime();    
		counter+=(networkServiceLoadVO.getRecievedCount()*networkServiceLoadVO.getAverageServiceTime()); 
		lastReqSrvcTime = networkServiceLoadVO.getLastRequestServiceTime();
		if(min > lastReqSrvcTime  && lastReqSrvcTime!=0.0)
		{
			min=lastReqSrvcTime;
		}
		if( min==0.0)
		{
			min=lastReqSrvcTime;
		}
		if(!showInstanceParam)
		{
		%>
		<tr>
		<td class="tabhead" colspan="17">Instance : <%=(networkServiceLoadVO.getInstanceID()==null?"N.A":networkServiceLoadVO.getInstanceID())%></td>
		<tr>
		<tr>
		<td class="tabhead" colspan="17">Network : <%=(networkServiceLoadVO.getNetworkCode()==null?"N.A":networkServiceLoadVO.getNetworkCode())%></td>
		</tr>
		<tr>
		<td class="tabhead" colspan="17">Module : <%=(networkServiceLoadVO.getModuleCode()==null?"N.A":networkServiceLoadVO.getModuleCode())%></td>
		</tr>
		<%
		showInstanceParam=true;
		}
		showParam=false;
	}
}
j++;
	
	if(tempList!=null && tempList.size()==1)
	{
	%>
	<tr>
	<td class="tabhead" colspan="17">Instance : <%=(instanceID==null?"N.A":instanceID)%></td>
	</tr>
	<tr>
	<td class="tabhead" colspan="17">Network : <%=(networkCode==null?"N.A":networkCode)%></td>
	</tr>
	<tr>
	<td class="tabhead" colspan="17">Module : <%=(module==null?"N.A":module)%></td>
	</tr>
	<%
	showParam=true;
	showInstanceParam=true;
	}%>
			
	<tr>
	<td class="tabcol"><%=(j)%></td>
	<td class="tabcol" colspan="2"><%=serviceType%></td>
	<td class="tabcol"><%=serviceName%></td>
	<td class="tabcol"><%=receivedCount%></td>
	<%receivedCountTtl+=receivedCount;%>
	<td class="tabcol"><%=successCount%></td>
	<%successCountTtl+=successCount;%>
	<td class="tabcol"><%=failCount%></td>
	<%failCountTtl+=failCount;%>
	<td class="tabcol"><%=underProcessCount%></td>
	<%underProcessCountTtl+=underProcessCount;%>
	<td class="tabcol"><%=otherFailCount%></td>
	<%otherFailCountTtl+=otherFailCount;%>
	<td class="tabcol"><%=otherNetwrkReqCount%></td>
	<%otherNetwrkReqCountTtl+=otherNetwrkReqCount;%>
	<td class="tabcol"><%=bforGtwayFoundErr%></td>
	<%bforGtwayFoundErrTtl+=bforGtwayFoundErr;%>
	<td class="tabcol"><%=bforNtwrkFoundErr%></td>
	<%bforNtwrkFoundErrTtl+=bforNtwrkFoundErr;%>
	<td class="tabcol"><%=bforSrvcTypeFoundErr%></td>
	<%bforSrvcTypeFoundErrTtl+=bforSrvcTypeFoundErr;%>
	<td class="tabcol"><%=BTSLUtil.getTimestampFromUtilDate(max)%></td>
	<td class="tabcol">
	<%if(receivedCount>0)
	{%><%=BTSLUtil.roundToStr((counter/receivedCount),3)%>
	<%  } else
	{%><%=BTSLUtil.roundToStr((0),3)%></td><%}%>
	<td class="tabcol" colspan="2"><%=BTSLUtil.roundToStr(min,3)%></td>
	</tr>

	<tr>
		<td class="tabhead" colspan="4" align=middle><b>Total</td>
		<td class="tabhead"><%=receivedCountTtl%></td>
		<td class="tabhead"><%=successCountTtl%></td>
		<td class="tabhead"><%=failCountTtl%></td>
		<td class="tabhead"><%=underProcessCountTtl%></td>
		<td class="tabhead"><%=otherFailCountTtl%></td>
		<td class="tabhead" ><%=otherNetwrkReqCountTtl%></td>
		<td class="tabhead" ><%=bforGtwayFoundErrTtl%></td>
		<td class="tabhead" ><%=bforNtwrkFoundErrTtl%></td>
		<td class="tabhead" colspan="5"><%=bforSrvcTypeFoundErrTtl%></td>
	</tr>
		
</table>
<jsp:include page="/monitorserver/common/bottomband.jsp"/>
</body>
</html>

