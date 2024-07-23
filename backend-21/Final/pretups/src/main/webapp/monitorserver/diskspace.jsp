<%@ page language="java" import="java.util.*" %>
<%@ page language="java" import="com.btsl.util.*"%>
<%
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <jsp:include page="/monitorserver/common/topband.jsp"/> 
	  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>PreTUPS Server Space Status</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
  </head>
  <BODY leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>
  <div class=heading><center><strong>PreTUPS eRecharge Server Disk Usage: appcluster02
</strong></center></div>
<br/>
<table height=44 width=450 border=1 cellspacing=0 cellpadding=0  align=center>
<tr><td align=center>
<font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2><b>
Server Disk </font>
</td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2><b> Total Disk</b></font>  </td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2><b> Used percent</b></font> </td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2><b> Available</b></font> 
</BR></td></tr>
<tr><td>
<font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2><b>
/
</b></font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
6.4G
</font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
37
%</font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
3.9G
</font>
</BR></td></tr>
<tr><td>
<font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2><b>
/boot
</b></font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
97M
</font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
17
%</font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
77M
</font>
</BR></td></tr>
<tr><td>
<font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2><b>
/home
</b></font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
7.7G
</font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
49
%</font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
3.8G
</font>
</BR></td></tr>
<tr><td>
<font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2><b>
/pretupshome
</b></font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
12G
</font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
21
%</font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
8.7G
</font>
</BR></td></tr>
<tr><td>
<font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2><b>
/var
</b></font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
67G
</font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
1
%</font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
64G
</font>
</BR></td></tr>
<tr><td>
<font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2><b>
/pretupsvar
</b></font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
135G
</font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
33
%</font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>
87G
</font>
</BR></td></tr>
</table>
   <br>
   <jsp:include page="/monitorserver/common/bottomband.jsp"/>
	</body></html>
