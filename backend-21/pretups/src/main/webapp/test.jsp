<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.btsl.util.Constants" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<body>

<%
Thread.sleep(Integer.parseInt(Constants.getProperty("SMSC_SIMULATOR_DELAY")));
%>
running
</body>
</html>