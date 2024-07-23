<%@ page language="java" import="java.util.*,com.btsl.user.businesslogic.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
	
	<BR>
	<div align="center" >
	<INPUT type="button" class="btn" value="Back"onclick="history.back()" /><br><br><br>
	<table border="0" width="100%" cellpadding="3" cellspacing="1">
		<tr height="20">       
			 <td  class="btn1" align="center"><a class="btn1" onmouseover ="this.style.color = 'white';" onmouseout ="this.style.color = 'white';" href='http://<bean:message key="login.index.label.clientsite"/>'/><bean:message key="login.index.label.clientsite"/></a>
      
			</td>
	    </tr>
	</table>
	</div>
