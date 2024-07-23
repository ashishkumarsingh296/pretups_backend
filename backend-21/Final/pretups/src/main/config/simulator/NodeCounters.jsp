<%@ page import="com.btsl.pretups.inter.cs3mobinil.cs3scheduler.NodeManager" %>
<%@ page import="com.btsl.pretups.inter.cs3mobinil.cs3scheduler.NodeScheduler" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="com.btsl.pretups.inter.cs3mobinil.cs3scheduler.NodeVO" %>
<html>
  <head>
   <title>Test Server Report</title>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/jsp/common/main.css"/>
  </head>
<%
String interfaceId=request.getParameter("interfaceId");
NodeScheduler nodeSheduler=  NodeManager.getScheduler(interfaceId);
Hashtable nodeTable=nodeSheduler.getNodeTable();
int lSize=nodeTable.size();
NodeVO nodeVO[] = new NodeVO[nodeTable.size()+1] ;
%>

<body>
<H3 align="center">Test Server Report</H3>
<TABLE width="100%" border="1" bordercolor="gray">
	
	<TR>
		<td align="center">
		<b>Attribute</b>
		</td>
		<%		
		for(int i=1;i<=lSize;i++)
			{%>
		<td align="center">
		<b>Server<%=i%></b>
		</td>
		<%}%>
	
	</TR>
	
<%
for(int i=1;i<=lSize;i++)
{
nodeVO[i]=(NodeVO)nodeTable.get(String.valueOf(i));
}
%>
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>URL </b>
		</TD>
		<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getUrl()%>
				</TD>
			<%}%>
	</TR>
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>Connection Time Out</b> 
		</TD>
	<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getConnectionTimeOut()%>
				</TD>
			<%}%>
	</TR>
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>Top Read Time Out </b>
		</TD>
	<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getTopReadTimeOut()%>
				</TD>
			<%}%>
	</TR>
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>Node Number </b>
		</TD>
		<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getNodeNumber()%>
				</TD>
			<%}%>
	</TR>
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>Expiry Time </b>
		</TD>
		<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getExpiryDuration()%>
				</TD>
			<%}%>
	</TR>	
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>Keep Alive </b>
		<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getKeepAlive()%>
				</TD>
			<%}%>
	</TR>
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>Maximum Number Alllowed </b>
		</TD>
		<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getMaxSpanNodes()%>
				</TD>
			<%}%>
	</TR>
		<TR>
		<TD align="left" nowrap="nowrap">
		<b>Is Blocked </b>
		</TD>
		<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].isBlocked()%>
				</TD>
			<%}%>
	</TR>	
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>Is Blocked (Read Time Out)</b>
		</TD>
	<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].isBlockedByReadTimeOut()%>
				</TD>
			<%}%>
	</TR>
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>Is Blocked (Conn Time Out)</b>
		</TD>
	<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].isBlockedByConTimeOut()%>
				</TD>
			<%}%>
	</TR>
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>Max Allowed Conn Per Node</b>
		</TD>
	<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getMaxConPerNode()%>
				</TD>
			<%}%>
	</TR>
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>Blocked At</b>
		</TD>
	<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getBlokedAt()%>
				</TD>
			<%}%>
	</TR>
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>Connection Number</b>
		</TD>
	<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getConNumber()%>
				</TD>
			<%}%>
	</TR>
		<TR>
		<TD align="left" nowrap="nowrap">
		<b>Is Max Reached</b>
		</TD>
		<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].isMaxConReached()%>
				</TD>
			<%}%>
	</TR>
		<TR>
		<TD align="left" nowrap="nowrap">
		<b>Last Suspend At</b>
		</TD>
	<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getLastSuspendedAt()%>
				</TD>
			<%}%>
	</TR>
		<TR>
		<TD align="left" nowrap="nowrap">
		<b>Suspended at</b>
		</TD>
	<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getSuspendedAt()%>
				</TD>
			<%}%>
	</TR>
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>Is Suspended</b>
		</TD>
	<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].isSuspended()%>
				</TD>
			<%}%>
	</TR>
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>Time of first AMBG Trans.</b>
		</TD>
	<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getTimeOfFirstAmbiguousTxn()%>
				</TD>
			<%}%>
	</TR>
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>Current AMBG Trans. Time</b>
		</TD>
	<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getCurrentAmbTxnTime()%>
				</TD>
			<%}%>
	</TR>
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>AMBG Transc. Counter</b>
		</TD>
	<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getCurrentAmbTxnCounter()%>
				</TD>
			<%}%>
	</TR>
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>No. Of AMBG Transac. Allowed</b>
		</TD>
	<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getNumberOfAmbguousTxnAllowed()%>
				</TD>
			<%}%>
	</TR>
	<TR>
		<TD align="left" nowrap="nowrap">
		<b>Threshold Time</b>
		</TD>
		<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getThresholdTime()%>
				</TD>
			<%}%>
	</TR>
		<TR>
		<TD align="left" nowrap="nowrap">
		<b>Process Single Req Flag</b>
		</TD>
	<%		
		for(int i=1;i<=lSize;i++)
			{%>
				<TD align="left">
				<%=nodeVO[i].getProcessSingleReqFlag()%>
				</TD>
			<%}%>
	</TR>
	
</TABLE>
</body>
</html>  
