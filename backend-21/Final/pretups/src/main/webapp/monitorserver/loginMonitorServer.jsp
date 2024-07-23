<%@ page import="com.btsl.util.*" language="java" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<html>
<head>
<title>eRecharge Monitor Server</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%= request.getContextPath()%>/monitorserver/common/main.css" rel="stylesheet" type="text/css">
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<br/>
<%
session.setAttribute("userId","");
session.setAttribute("password","");
String errMessage=BTSLUtil.NullToString(request.getParameter("message"));
String errMessage2=BTSLUtil.NullToString(request.getParameter("message2"));
String isSessionLogin = (String)session.getAttribute("login");
%>
   <form method="post" name="form1" action="showOptions.jsp">
  	<table  width="80%" border='0' cellspacing='0' cellpadding='0' >
  	 <tr><td width="20%"><img src="<%= request.getContextPath()%>/monitorserver/common/images/logo.gif"></td>
  				<td width="80%" align="right" class="loginheading">
  				eRecharge Monitor Server&nbsp;&nbsp;&nbsp;
  					
 				 </td>
 			</tr><tr></tr><tr></tr><tr></tr><tr></tr>
  			<tr><td colspan="2"><img width="1000" height="150" src="<%= request.getContextPath()%>/monitorserver/common/images/loginimage.jpg"></td></tr>
  		</table>
  		<br>
<%
  		if(! BTSLUtil.isNullString(errMessage)&& !isSessionLogin.equalsIgnoreCase("null"))
{
%>
<div class="message"><b><center><%=errMessage%></center></b></div>	
<%
}
%>
<%
  		if(! BTSLUtil.isNullString(errMessage2)&& !isSessionLogin.equalsIgnoreCase("null"))
{
%>
<div class="message"><b><center><%=errMessage2%></center></b></div>	
<%
}
%>
  		
  	<br/>
  	<div class="mandatory" align="center" >
  	All fields marked with * are mandatory.
  	</div>
  	<br/>
	<table width="25%" border="0" cellspacing="0" cellpadding="0" align="center" class="back">
    <tr>
      <td>
		<table border="0" width="100%" cellpadding="3" cellspacing="1">
		  <tr> 
            <td width="38%" height="25" class="tabcol"> Login ID : </td>
            <td height="25" class="tabcol" colspan="3" width="62%"> 
          <input type="text" name="name" maxlength="60" size="12" value=""><font class="mandatory"> *</font>
          </tr>
		  <tr> 
            <td width="38%" height="25" class="tabcol"> Password : </td>
            <td height="25" class="tabcol" colspan="3" width="62%"> 
          <input type="password" name="pass" maxlength="60" size="12" value=""><font class="mandatory"> *</font>
          </tr>
	 </table>
      </td>
    </tr>
  </table>
  <br/>
  	<div align="center"> 
    	<input name="submit" type="submit" class="btn" value="Submit">
  	</div>
  </form>
<table border="0" width="100%" cellpadding="3" cellspacing="1">
<tr height="20">       
      <td  class="btn1" align="center"><a class="btn1" onmouseover ="this.style.color = 'white';" onmouseout ="this.style.color = 'white';" href='http://<bean:message key="login.index.label.clientsite"/>'/><bean:message key="login.index.label.clientsite"/></a>
      
      </td></tr>
 </table>
</body>
</html>

		