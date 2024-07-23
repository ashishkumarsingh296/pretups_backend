<html>
<head>
<title> Information about IAT </title>
<jsp:include page="simulatorTopband.jsp"></jsp:include>
</head>
<body onload="start()">
<jsp:include page="divContents.jsp"></jsp:include>
    <form method="POST" name="ussd" action="zebraSimSetparams.jsp">
    <br><br>
	<font color="blue"><center><B><H2>Information about IAT</B></center></font>
	<br>
    <table border="0" align="center" cellspacing="0" cellpadding="0"  width="80%" bgcolor="#6699FF">
    <tr>
    <td width="100%">
        <table border="1" width="100%" align="center" cellspacing="0" cellpadding="0" bgcolor="#6699FF" >
		    <tr bgcolor="FFFF66">
		      <td width="100%" align="center" colspan="2">
		        <p align="center"><b>Set IAT DB Details</b></p></td>
		    </tr>
			    
			<tr>
			  <td align="left" width="40%">Database IP</td>
			  <td width="60%"><input type="text" name="IAT_DB_IP" size="15" value="127.0.0.1" maxlength="15"></td>
			</tr>
						
			<tr>
			  <td align="left" width="40%">Database Port</td>
			  <td width="60%"><input type="text" name="IAT_DB_PORT" size="15" value="1521" maxlength="5"></td>
			</tr>
			
			<tr>
			  <td align="left" width="40%">Database SID</td>
			  <td width="60%"><input type="text" name="IAT_DB_SID" size="15" value="PRTP" maxlength="5"></td>
			</tr>

			<tr>
			  <td align="left" width="40%">Database User Name</td>
			  <td width="60%"><input type="text" name="IAT_DB_USER" size="15" value="pretups_iat" maxlength="15"></td>
			</tr>

			<tr>
			  <td align="left" width="40%">Database PASSWORD</td>
			  <td width="60%"><input type="password" name="IAT_DB_PASS" size="15" value="" maxlength="15"></td>
			</tr>
		</Table>
		
	</td>
	</tr>
	
	<tr>
    <td>
			<table border="1" align="left" cellspacing="0" cellpadding="0" bgcolor="#6699FF" width="100%">
				<tr bgcolor="FFFF66">
					<td align="center" colspan="2"><p align="center"><b>Set IAT Response</b></p></td>
		    	</tr>
				<tr>
					<td width="50%" align="left" colspan="1">IAT Status:</td>
					<td width="50%" colspan="1">
						<select name="iatResponse" onChange="fnChangeHandler_A(this, event);">
						<option label="Accepted"  value="0" selected="selected" >Accepted</option>
						<option label="Rejected" value="1">Rejected</option>
						<option label="Suspend" value="19002">Suspend</option>
						<option label="Internal Error" value="19001">Internal Error</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="left" colspan="1" width="50%">IAT Message:</td>
					<td align="left" colspan="1" width="50%"><textarea name="iatText" rows=3 cols=40 readonly="readonly">Your request is successfully accepted at IAT HUB</textarea></td>
				</tr>
			</Table>
		</td>
	</tr>
	<tr ID="NMSISDN" >
      	<td  colspan="2" align="center" valign="center" height="50">
    		<jsp:include page="includeButtons.jsp"></jsp:include>
    	</td>
	</tr>
	</table>
  </form>
</div>
</body>
</html>