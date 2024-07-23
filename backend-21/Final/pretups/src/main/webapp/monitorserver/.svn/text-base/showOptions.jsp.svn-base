<%@ page import="com.btsl.util.*" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.btsl.loadcontroller.LoadControllerDAO" %>
<%@ page import="com.btsl.loadcontroller.InstanceLoadVO" %>
<%@ page import="java.util.StringTokenizer" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<html>
<head>
<title>eRecharge Monitor Server</title>
<jsp:include page="/monitorserver/common/topband.jsp" flush="false">
    <jsp:param name="helpFile" value="/help/clickFromTheFollowing.jsp" />
	<jsp:param name="showJspHeading" value="N" />
</jsp:include>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%= request.getContextPath()%>/monitorserver/common/main.css" rel="stylesheet" type="text/css">
</head>
<script language="javascript">
function initialiseAction(url,text)
{
	if(confirm('Are you sure to initialize '+text+' ?'))
	{
		location.href=url;
	}     
  
}
function updateAction(url,text)
{
	if(confirm('Are you sure to update '+text+' ?'))
	{
		location.href=url;
	}     
  
}
</script>

<%
	String userName=BTSLUtil.NullToString(request.getParameter("name"));
	String userPass=BTSLUtil.NullToString(request.getParameter("pass"));
	if(BTSLUtil.isNullString(userName))
		userName=(String)session.getAttribute("userId");
	if(BTSLUtil.isNullString(userPass))
		userPass=(String)session.getAttribute("password");
String errMessage=BTSLUtil.NullToString(request.getParameter("message"));
if(! BTSLUtil.isNullString(errMessage))
{
%>
<div class="message"><b><center><%=errMessage%></center></b></div>	
<%
}
if(BTSLUtil.isNullString(userName)&&BTSLUtil.isNullString(userPass))
{
%>
		<jsp:forward page="loginMonitorServer.jsp" >
		<jsp:param name="message" value="Login ID is required"/>
		<jsp:param name="message2" value="Password is required"/>
		</jsp:forward>

<%
}
else if(BTSLUtil.isNullString(userName))
{
%>
		<jsp:forward page="loginMonitorServer.jsp" >
		<jsp:param name="message" value="Login ID is required"/>
		</jsp:forward>
<%
}
else if(BTSLUtil.isNullString(userPass))
{
%>
		<jsp:forward page="loginMonitorServer.jsp" >
		<jsp:param name="message" value="Password is required"/>
		</jsp:forward>
<%
}
else
{
	//String userName=BTSLUtil.NullToString(request.getParameter("name"));
	//String userPass=BTSLUtil.NullToString(request.getParameter("pass"));
	//if(BTSLUtil.isNullString(userName))
		//userName=(String)session.getAttribute("userId");
	//if(BTSLUtil.isNullString(userPass))
		//userPass=(String)session.getAttribute("password");
	
	String user=Constants.getProperty("monitorServerUser");
	String pass=Constants.getProperty("monitorServerPass");
	String userView=Constants.getProperty("monitorServerUserView");
	String passView=Constants.getProperty("monitorServerPassView");
	boolean found=false;
	
	// code added to validate client machine ip address.
	String xForwardedFor=request.getHeader("x-forwarded-for");
    String ipAddress=null;
    String remoteAddr=null;
    String remoteHost=null;
    
    if(BTSLUtil.isNullString(xForwardedFor)){
		ipAddress=request.getRemoteAddr();
		remoteAddr=request.getRemoteAddr();
		remoteHost=request.getRemoteHost();
		//System.out.println("inside if : ipAddress"+ipAddress+"remoteAddr"+remoteAddr+"remoteHost"+remoteHost);
	}else{
		ipAddress=xForwardedFor;
		remoteAddr=xForwardedFor;
		remoteHost=xForwardedFor;
		//System.out.println("inside else : ipAddress"+ipAddress+"remoteAddr"+remoteAddr+"remoteHost"+remoteHost);
	}
	
	String validRequestURLs = Constants.getProperty("monitorAllowedIps");
	System.out.println("valid IPs are "+validRequestURLs);
	if(!BTSLUtil.isNullString(validRequestURLs))
	{
		StringTokenizer requestUrlTokens = new StringTokenizer(ipAddress,",");
		boolean found1=false;
		String tokenValue=null;
		String[] reqIP=new String[4];
		String[] validIP=new String[4];
		StringTokenizer validIPTokens=null;
		String validTokenIPValue=null;
		while(requestUrlTokens.hasMoreTokens())
		{
			tokenValue=requestUrlTokens.nextToken();
			if(validRequestURLs.indexOf(tokenValue)!=-1)
			{
				found1=true;
				break;
			}
			else
			{
				reqIP = tokenValue.split("\\.");
				validIPTokens = new StringTokenizer(validRequestURLs,",");
				while(validIPTokens.hasMoreTokens())
				{
					validTokenIPValue=validIPTokens.nextToken();
					validIP = validTokenIPValue.split("\\.");
					if(reqIP[0].equals(validIP[0]) && ("*".equals(validIP[1]) || reqIP[1].equals(validIP[1])))
					{
						if("*".equals(validIP[2]) || reqIP[2].equals(validIP[2]))
						{
							if("*".equals(validIP[3]) || reqIP[3].equals(validIP[3]))
							{
								found1=true;
								break;
							}
						}
					}
				}
				if(found1)
				{
					break;
				}
			}
		}
		if(!found1)
		{
			System.out.println("this ip not allowed " + ipAddress);
			%>
			    <jsp:forward page="loginMonitorServer.jsp" >
				<jsp:param name="message" value="You are not authorised to access PreTUPS monitoring interface"/>
				</jsp:forward>
			<%
		}
	}
	// code end
	
	if((! BTSLUtil.NullToString(userName).equalsIgnoreCase(BTSLUtil.NullToString(userView))) && (! BTSLUtil.NullToString(userName).equalsIgnoreCase(BTSLUtil.NullToString(user))))
	{
	%>
	    <jsp:forward page="loginMonitorServer.jsp" >
		<jsp:param name="message" value="Invalid Login ID"/>
		</jsp:forward>
	<%
	}
	else
	{
		if((BTSLUtil.NullToString(userName).equalsIgnoreCase(BTSLUtil.NullToString(userView))) && (! BTSLUtil.NullToString(userPass).equalsIgnoreCase(BTSLUtil.NullToString(passView))))
		{
		%>
		    <jsp:forward page="loginMonitorServer.jsp" >
			<jsp:param name="message" value="Invalid Password"/>
			</jsp:forward>
		<%
		}
		else if((BTSLUtil.NullToString(userName).equalsIgnoreCase(BTSLUtil.NullToString(user))) && (! BTSLUtil.NullToString(userPass).equalsIgnoreCase(BTSLUtil.NullToString(pass))))
			{
			%>
			    <jsp:forward page="loginMonitorServer.jsp" >
				<jsp:param name="message" value="Invalid Password"/>
				</jsp:forward>
			<%
			}
		else found=true;
	}
	if(!found)
	{
		%>
			<jsp:forward page="loginMonitorServer.jsp" >
			<jsp:param name="message" value="Invalid login/password"/>
			</jsp:forward>
		<%
	}
	else
	{
		session.setAttribute("userId",userName);
		session.setAttribute("password",userPass);
	%>
	<body  leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
		<br/>
	  <div class="heading2"> Click from the following</div>
	  <br>
	<%
		//load the intances information from the instance load table
		Connection con = null;
		ArrayList list = null;
		try
		{
			con = OracleUtil.getConnection();
			LoadControllerDAO controllerDAO = new LoadControllerDAO();
			list = controllerDAO.loadInstanceLoadDetails(con);
		} catch (Exception e)
		{
			System.out.println("Load Instance from Instance_load table Exception:"+e);
		%>
			<jsp:forward page="loginMonitorServer.jsp" >
			<jsp:param name="message" value="your request is not processed at this time, please try later"/>
			</jsp:forward>
		<%	
		}// end of catch
		finally
		{
			try{if (con != null)con.close();}catch (Exception e){}
		}// end of finally
	%>
	
	<% 
	if(list!=null && list.size()>0)
	{
		InstanceLoadVO instanceLoadVO=null;
		for(int i=0,j=list.size(); i<j ; i++)
		{
			instanceLoadVO = (InstanceLoadVO)list.get(i);
			if((BTSLUtil.NullToString(userName).equalsIgnoreCase(user)) && (userPass.equalsIgnoreCase(pass)) || (BTSLUtil.NullToString(userName).equalsIgnoreCase(userView)) && (userPass.equalsIgnoreCase(passView)))
			{
				if("WEB".equals(instanceLoadVO.getInstanceType()))
					{
	%>            <table width="90%" border="0" cellspacing="0" cellpadding="0" align="center" class="back">
				     <tr>
				       <td>
						<table border="0" width="100%" cellpadding="3" cellspacing="1">
					    	<tr>
						       <td colspan="9" class="tabhead">
						       	Instance Id: <%= instanceLoadVO.getInstanceID()%>&nbsp;
								Instance Name: <%= instanceLoadVO.getInstanceName()%>&nbsp;
								IP: <%= instanceLoadVO.getHostAddress()%>&nbsp;
								Port: <%= instanceLoadVO.getHostPort()%>
							   </td>
						    </tr>
							<tr>
							   <td colspan="9" class="tabhead"><center>Monitor eRecharge WEB Server</center></td>
							</tr>					
							<tr>
							   <td class="tabcol"><a class="level2" href="pullCounters.jsp?jspName=sessionUser&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>">Session Users</a></td>
							   <td class="tabcol"><a class="level2" href="pullCounters.jsp?jspName=activeSessions&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>">Active Sessions</a></td>
							   <td class="tabcol"><a class="level2" href="pullCounters.jsp?jspName=memoryCounters&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>">Server Memory</a></td>
							   <%if("Y".equalsIgnoreCase(instanceLoadVO.getShowOamlogs())){%>
							   <td class="tabcol" ><a class="level2" href="pullCounters.jsp?jspName=LmDisplayAlarmLog&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>">OAM</a></td>
							   <%}if("Y".equalsIgnoreCase(instanceLoadVO.getShowSmscStatus())){%>
							   <td class="tabcol" colspan="4"><a class="level2" href="pullCounters.jsp?jspName=SMSCGateway&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>">SMSCGateway Status</a></td>
							   <%}%>
							</tr>
						</table>
   					  </td>
					</tr>
				 </table>
				<%}
					else if("SMS".equals(instanceLoadVO.getInstanceType())){
			    %>
				     <table width="90%" border="0" cellspacing="0" cellpadding="0" align="center" class="back">
				  		<tr>
							<td>
								<table border="0" width="100%" cellpadding="3" cellspacing="1">
					     		<tr>
						   		  <td colspan="9" class="tabhead">
						       	   Instance Id: <%= instanceLoadVO.getInstanceID()%>&nbsp;
						   		   Instance Name: <%= instanceLoadVO.getInstanceName()%>&nbsp;
								   IP: <%= instanceLoadVO.getHostAddress()%>&nbsp;
  	       						   Port: <%= instanceLoadVO.getHostPort()%>
	  							  </td>
		                        </tr>
								<tr>
								  <td colspan="9" class="tabhead"><center>Monitor eRecharge SMS Server</center></td>
								</tr>
								<tr>
								   <td class="tabcol"><a class="level2" href="pullCounters.jsp?jspName=instanceCounters&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>">Instance Counters</a></td>
								   <td class="tabcol"><a class="level2" href="pullCounters.jsp?jspName=locationCounters&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>">Network Counters</a></td>
								   <td class="tabcol"><a class="level2" href="pullCounters.jsp?jspName=transactionCounters&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>">Transaction Counters</a></td>
								   <td class="tabcol"><a class="level2" href="pullCounters.jsp?jspName=networkServiceLoadCounters&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>">Request Counters</a></td>
								   <td class="tabcol"><a class="level2" href="pullCounters.jsp?jspName=networkServiceLoadCountersSummary&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>">Request Counters Summary</a></td>
								   <td class="tabcol"><a class="level2" href="pullCounters.jsp?jspName=interfaceCounters&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>">Interface Counters</a></td>
								   <td class="tabcol"><a class="level2" href="pullCounters.jsp?jspName=memoryCounters&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>">Server Memory</a></td>
								   <%if("Y".equalsIgnoreCase(instanceLoadVO.getShowOamlogs())){%>
								   <td class="tabcol" ><a class="level2" href="pullCounters.jsp?jspName=LmDisplayAlarmLog&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>">OAM</a></td>
								   <%}if("Y".equalsIgnoreCase(instanceLoadVO.getShowSmscStatus())){%>
								   <td class="tabcol" ><a class="level2" href="pullCounters.jsp?jspName=SMSCGateway&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>">SMSCGateway Status</a></td>
								   <%}%>
								 </tr>
								 <%if(Constants.getProperty("monitorServerUser").equalsIgnoreCase(userName)){%>
								 <tr>
								   <td colspan="9" class="tabhead"><center>Initialize Counters</center></td>
								 </tr>
								 <tr>
								   <td class="tabcol"><a class="level2" href="javascript:initialiseAction('pullCounters.jsp?jspName=initialiseCountersAll&instanceID=<%= instanceLoadVO.getInstanceID()%>&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>','All Counters')">All Counters</a></td>
								   <td class="tabcol"><a class="level2" href="javascript:initialiseAction('pullCounters.jsp?jspName=initialiseCountersInstance&instanceID=<%= instanceLoadVO.getInstanceID()%>&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>','Instance Counters')">Instance Counters</a></td>
								   <td class="tabcol"><a class="level2" href="javascript:initialiseAction('pullCounters.jsp?jspName=initialiseCountersNetwork&instanceID=<%= instanceLoadVO.getInstanceID()%>&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>','Network Counters')">Network Counters</a></td>
								   <td class="tabcol"><a class="level2" href="javascript:initialiseAction('pullCounters.jsp?jspName=initialiseCountersTransaction&instanceID=<%= instanceLoadVO.getInstanceID()%>&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>','Transaction Counters')">Transaction Counters</a></td>
								   <td class="tabcol"><a class="level2" href="javascript:initialiseAction('pullCounters.jsp?jspName=initialiseCountersNetworkServiceLoad&instanceID=<%= instanceLoadVO.getInstanceID()%>&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>','Request Counters')">Request Counters</a></td>
								   <td class="tabcol" colspan="4"><a class="level2" href="javascript:initialiseAction('pullCounters.jsp?jspName=initialiseCountersInterface&instanceID=<%= instanceLoadVO.getInstanceID()%>&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>','Interface Counters')">Interface Counters</a></td>
								</tr>
								<tr>
								  <td colspan="9" class="tabhead"><center>Update Counters</center></td>
								</tr>
								<tr>
								   <td class="tabcol"><a class="level2" href="javascript:updateAction('pullCounters.jsp?jspName=updateCountersAll&instanceID=<%= instanceLoadVO.getInstanceID()%>&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>','All Counters')">All Counters</a></td>
								   <td class="tabcol"><a class="level2" href="javascript:updateAction('pullCounters.jsp?jspName=updateCountersInstance&instanceID=<%= instanceLoadVO.getInstanceID()%>&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>','Instance Counters')">Instance Counters</a></td>
								   <td class="tabcol"><a class="level2" href="javascript:updateAction('pullCounters.jsp?jspName=updateCountersNetwork&instanceID=<%= instanceLoadVO.getInstanceID()%>&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>','Network Counters')">Network Counters</a></td>
								   <td class="tabcol"><a class="level2" href="javascript:updateAction('pullCounters.jsp?jspName=updateCountersTransaction&instanceID=<%= instanceLoadVO.getInstanceID()%>&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>','Transaction Counters')">Transaction Counters</a></td>
								   <td class="tabcol" colspan="5"><a class="level2" href="javascript:updateAction('pullCounters.jsp?jspName=updateCountersInterface&instanceID=<%= instanceLoadVO.getInstanceID()%>&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>','Interface Counters')">Interface Counters</a></td>
								</tr>
						<%}%>
							  </table>
   						    </td>
					     </tr>
					   </table>
	    			 <%}
						else
							{
							if("Y".equalsIgnoreCase(instanceLoadVO.getShowOamlogs()) || "Y".equalsIgnoreCase(instanceLoadVO.getShowSmscStatus())){
    				%>               
					     <table width="90%" border="0" cellspacing="0" cellpadding="0" align="center" class="back">
						    <tr>
						      <td>
								<table border="0" width="100%" cellpadding="3" cellspacing="1">
					    	        <tr>
						    	     <td colspan="9" class="tabhead">
							       	  Instance Id: <%= instanceLoadVO.getInstanceID()%>&nbsp;
							    	  Instance Name: <%= instanceLoadVO.getInstanceName()%>&nbsp;
								      IP: <%= instanceLoadVO.getHostAddress()%>&nbsp;
								      Port: <%= instanceLoadVO.getHostPort()%>
							         </td>
						            </tr>
								    <tr>
									 <td colspan="9" class="tabhead"><center>Monitor eRecharge OAM Common Server</center></td>
								    </tr>					
								    <tr>
									 <%if("Y".equalsIgnoreCase(instanceLoadVO.getShowOamlogs())){%>
									 <td class="tabcol" width=48%><a class="level2" href="pullCounters.jsp?jspName=LmDisplayAlarmLog&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>">OAM</a></td>
									 <%}if("Y".equalsIgnoreCase(instanceLoadVO.getShowSmscStatus())){%>
									 <td class="tabcol" width=52%><a class="level2" href="pullCounters.jsp?jspName=SMSCGateway&instanceName=<%= instanceLoadVO.getInstanceName()%>&ipAddress=<%= instanceLoadVO.getHostAddress()%>&port=<%= instanceLoadVO.getHostPort()%>">SMSCGateway Status</a></td>
									 <%}%>
								    </tr>
							   </table>
   						    </td>
					       </tr>
					    </table>
						<%
							}
					    }
			}
			if((BTSLUtil.NullToString(userName).equalsIgnoreCase(user)) && (BTSLUtil.NullToString(userPass).equalsIgnoreCase(pass)))
			{
		%>	
			<!--
				<br>
				<table width="90%" border="0" cellspacing="0" cellpadding="0" align="center" class="back">
				<tr>
			      <td>
					<table border="0" width="100%" cellpadding="3" cellspacing="1">
				<tr>
					<td colspan=5 class="tabhead"><center>Monitor ERS TopUp Server : Admin Operation </center></td>
				</tr>
				<tr>
						<td width="20%" height="25" class="tabcol"><a class="level2active" href="pullInitializeLocCounters.jsp">Initialize Location Counters</a></td>
						<td width="20%" height="25" class="tabcol"><a class="level2active" href="pullBuddyInitializeCounters.jsp">Initialize Buddy Counters</a></td>
						<td width="20%" height="25" class="tabcol"><a class="level2active" href="pullInitializeCounters.jsp">Initialize Counters</a></td>
						<td width="20%" height="25" class="tabcol"><a class="level2active" href="pullSMSServletCache.jsp">Update SMS Server Cache</a></td>
						<td width="20%" height="25" class="tabcol"><a class="level2active" href="pullWebServletCache.jsp">Update Web Server Cache</a></td>
					</tr>
					</table>
				</td>
			    </tr>
			  </table>
			-->
		<%
			}
		%>
			<br/><br/>
		<%	
		}//end of for
	}//end of main if
	else
	{
		%>
		<jsp:forward page="loginMonitorServer.jsp" >
			<jsp:param name="message" value="No data found in Instance_Load Table"/>
		</jsp:forward>
	<%
	}
	%>
	<table border="0" width="100%" cellpadding="3" cellspacing="1">
	<tr height="20">       
      <td  class="btn1" align="center"><a class="btn1" onmouseover ="this.style.color = 'white';" onmouseout ="this.style.color = 'white';" href='http://<bean:message key="login.index.label.clientsite"/>'/><bean:message key="login.index.label.clientsite"/></a>
      
      </td></tr>
 </table> 
	</body>
	<%
	}
}
%>
</html>