<%@page import="java.util.*" %>


<HTML>
<HEAD>
<TITLE> </TITLE>
<SCRIPT langauage="javascript">
function redirectBack()
  {
  	document.location = 'zebraSimulator.jsp';
  }
</SCRIPT>

</HEAD>

<BODY onload="redirectBack();">

<%

	System.setProperty("iat.credit.Txn.Status",request.getParameter("iatResponse"));
	System.setProperty("iat.credit.message",request.getParameter("iatText"));
	
	if(request.getParameter("submitform")!=null)
	{
		String IP=request.getParameter("IAT_DB_IP");
		String port=request.getParameter("IAT_DB_PORT");
		String sid=request.getParameter("IAT_DB_SID");
	
		System.setProperty("iat.db.datasource","jdbc:oracle:thin:@"+IP+":"+port+":"+sid);
	
		System.setProperty("iat.db.user",request.getParameter("IAT_DB_USER"));
		System.setProperty("iat.db.pass",request.getParameter("IAT_DB_PASS"));
		System.out.println("@@@@@@@ datasource 1 :: "+System.getProperty("iat.db.datasource"));
	}

//String creditStatus = System.getProperty("iat.credit.Txn.Status");
//request.setAttribute("creditStatus",creditStatus);
//String creditMessage = System.getProperty("iat.credit.message");
%>

</BODY>
</HTML>

