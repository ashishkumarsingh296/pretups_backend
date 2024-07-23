<html>
<head>
<title>Monitor Server</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="<%= request.getContextPath()%>/monitorserver/common/main.css" rel="stylesheet" type="text/css">
</head>
<% String helpFile = request.getParameter("helpFile"); 
String context=request.getContextPath();
String commonPath=request.getContextPath()+"/monitorserver/common"; %>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
	    <table width="100%" border="0" cellpadding="0" cellspacing="0" >
    	  <tr>
        	<td colspan="2">
        		<table width="98%%" align="right" border="0" cellpadding="0" cellspacing="0" >
	       			  <tr>
      				  	<td>
				        &nbsp; 	
            			</td>
      				  </tr>
				      <tr> 
				        	<TD> <IMG SRC="<%= request.getContextPath()%>/monitorserver/common/images/logo.gif" border="0" width="200" height="85"></td>
				        	<td><IMG align="right" SRC="<%= request.getContextPath()%>/monitorserver/common/images/services2.jpg" width="500" height="85" border="0">
				        </TD>
      				  </tr>
      				  <tr>
      				  	<td>
				        &nbsp; 	
            			</td>
      				  </tr>
				      <tr> 
				        <td class="topbg" colspan="2">
				        &nbsp; 	
            			</td>
          			  </tr>
        		</table>
        	</td>
          </tr>
          <tr>
          	<td align="right" class="topbackbg1" colspan="2">
				<span class="toplink">
				Time : 
				<%
            		java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat ("dd/MM/yy HH:mm:ss");
            		sdf.setLenient(false);
            				
            	%>
            	<%=sdf.format(new java.util.Date())%>
				|
				<a class="toplink" href="<%= request.getContextPath()%>/monitorserver/showOptions.jsp">Main Menu</a>
        		|
        		<a class="toplink" href="<%= request.getContextPath()%>/monitorserver/loginMonitorServer.jsp">Log Out</a>
           		</span> 
           	</td>
          </tr>
           <tr> 
            <td> 
              <%if(helpFile==null || helpFile.length()==0)
              	{
              %>
              	<br>
              <%
              	}
              	else{
              	%>
             		<p align="right"><a href="javascript:win_popup('<%=helpFile%>')"><img src="<%=commonPath+"/images/help.gif"%>" border="0" align="absbottom" ></a></p>  
                <%
                }
				%>
          </td><td width="1"></td>
          </tr>
          <%
          		if(!"N".equals(request.getParameter("showJspHeading")))
          		{
          %>
          <tr>
          	
          	<td colspan="2">
	        	<div class="heading"><center><strong><%= request.getParameter("jspHeading")%></strong></center></div>
				<br/>
			</td>
		  </tr>	
		  <%
		  		}
		  %>
         </table>
         
	<BR>
<script language="JavaScript">
function win_popup(helpfile)
{
	
	var theURL = "<%=context+"/monitorserver"%>"+helpfile;
	window.open(theURL,'','scrollbars=yes,resizable=yes,width=600,height=500');
}

</script>