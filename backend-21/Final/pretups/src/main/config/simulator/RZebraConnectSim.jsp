<html>
<head>
<title> Receiver Zebra Connection information </title>
<jsp:include page="simulatorTopband.jsp"></jsp:include>
</head>
<body onload="">
<jsp:include page="divContents.jsp"></jsp:include>
    <form method="POST" name="ussd" action="RZebraConnectSimSubmit.jsp">
    <br><br>
	<font color="blue"><center><H2> Receiver Zebra Connection information </H2></center></font>
	<br>

    <Table align="center" border="1"  bgcolor="#6699FF" width="80%" height="60%">
    
			     <tr>
			      <td colspan="1" align="left">IP</td>
			      <td colspan="1"><input type="text" name="IP" size="15" value="127.0.0.1" maxlength="15"></td>
			       <td colspan="1" align="left">Port</td>
			      <td colspan="1"><input type="text" name="PORT" size="15" value="8080" maxlength="5"></td>
			    </tr>
			     <tr>
			      <td colspan="1" align="left">Request Gateway Code</td>
			      <td colspan="1"><input type="text" name="REQUEST_GATEWAY_CODE" size="15" value="EXTGW" maxlength="10" ></td>
			      <td colspan="1" align="left">Request Gateway Type</td>
			      <td colspan="1"><input type="text" name="REQUEST_GATEWAY_TYPE" size="15"  value="EXTGW" maxlength="10" ></td>
			    </tr>
			   
			    <tr>
			      <td colspan="1" align="left">Service Port</td>
			      <td colspan="1"><input type="text" name="SERVICE_PORT" size="15" value="190" maxlength="5"></td>
			      <td colspan="1" align="left">Login</td>
			      <td colspan="1"><input type="text" name="LOGIN" size="15" value="pretups"  maxlength="15"></td>
			    </tr>
			   
			    <tr>
			      <td colspan="1" align="left">Password</td>
			      <td colspan="1"><input type="text" name="PASSWORD" size="15" value="pretups123" maxlength="15"></td>
			       <td colspan="1" align="left">Source Type</td>
			      <td colspan="1"><input type="text" name="SOURCE_TYPE" size="15" value="EXTGW"   ></td>
			    </tr>
			    
			     <tr>
			      <td colspan="1" align="left">Application</td>
			      <td colspan="3"><input type="text" name="APP" size="68" value="/pretups/C2SReceiver"   readonly="readonly"></td>
			    </tr>
			    
		  		 <tr>
      				<td  colspan="4" align="center" valign="center" height="50">
      				<jsp:include page="includeButtons.jsp"></jsp:include>
      	   			</td>
    	  		</tr>
	</table>
  </form>
  </div>
  </body>
</html>
