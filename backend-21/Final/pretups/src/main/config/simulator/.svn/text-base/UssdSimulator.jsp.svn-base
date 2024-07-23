<html>
<head>
<title> USSD Simulator </title>
<jsp:include page="simulatorTopband.jsp"></jsp:include>
</head>
<body onload="start()">
<jsp:include page="divContents.jsp"></jsp:include>
    <form method="POST" name="ussd" action="ussdSimulatorSubmit.jsp">
    <br><br>
	<font color="blue"><center><B><H2>USSD Simulator</B></center></font>
    <table border="0" align="center" cellspacing="0" cellpadding="0" bgcolor="#6699FF">
    <tr>
    <td>
		    <table border="1" width="100%" align="center" cellspacing="0" cellpadding="0"  >
		    <tr bgcolor="FFFF66">
		      <td width="100%" align="center" colspan="4">
		        <p align="center"><b>Input Parameter</b></p></td>
		    </tr>
		    
		     <tr>
		      <td   align="left">Service type</td>
		      <td  >
		      <select name="servicetype" onchange="showNotification()">
			  <option value="RC">Customer Recharge</option> 
			  <option value="RR">Roam Recharge</option>
		      <option value="IR">International Recharge</option>			  
			  <option value="CPN">Change PIN</option> 	
		      </select>
		      </td>
		    </tr>
		    

		    <tr ID="RTMSISDN">
		      <td  align="left">Retailer MSISDN</td>
		      <td  ><input type="text" name="retailermsisdn" size="15"  maxlength="12"  ></td>
		    </tr>
		    
		    <tr ID="RPIN">
		      <td  align="left">Retailer PIN</td>
		      <td ><input type="password" name="pin" size="4" value="" maxlength="4" ></td>
		    </tr>
		    
		    <tr ID="NPIN">
		      <td  align="left">New PIN</td>
		      <td ><input type="password" name="newpin" size="4" value="" maxlength="4" ></td>
		    </tr>
		   
		    <tr ID="CPIN">
		      <td  align="left">Confirm PIN</td>
		      <td ><input type="password" name="confirmpin" size="4" value="" maxlength="4" ></td>
		    </tr>

		    <tr ID="RxMSISDN">
		      <td  align="left">Receiver MSISDN</td>
		      <td ><input type="text" name="receivermsisdn" size="15" maxlength="12"  ></td>
		    </tr>
		    
		    <tr ID="AMT">
		      <td  align="left">Amount</td>
		      <td ><input type="text" name="amount" size="10"    ></td>
		    </tr>
		 
		    <tr ID="NMSISDN">
		      <td  align="left">Notification MSISDN</td>
		      <td ><input type="text" name="notificationmsisdn" size="15" maxlength="12"  ></td>
		    </tr>
		    
		  </table>
	</td>
	</tr>
	
	<tr>
	<td>
			<table border="1" width="100%" align="center" cellspacing="0" cellpadding="0"  >
						
				<tr bgcolor="FFFF66">
			       <td width="100%" align="left" colspan="4">
			        <p align="center">
				<b>Connection information  &nbsp;</b></td>
			    </tr>
			     <tr>
			      <td colspan="1" align="left">IP</td>
			      <td colspan="1"><input type="text" name="IP" size="15" value="127.0.0.1"   ></td>
			       <td colspan="1" align="left">Port</td>
			      <td colspan="1"><input type="text" name="PORT" size="6" value="9898"  readonly="readonly" ></td>
			    </tr>
			     <tr>
			      <td colspan="1" align="left">Request Gateway Code</td>
			      <td colspan="1"><input type="text" name="REQUEST_GATEWAY_CODE" size="10" value="USSD" readonly="readonly" ></td>
			      <td colspan="1" align="left">Request Gateway Type</td>
			      <td colspan="1"><input type="text" name="REQUEST_GATEWAY_TYPE" size="10"  value="USSD" readonly="readonly" ></td>
			    </tr>
			   
			    <tr>
			      <td colspan="1" align="left">Service Port</td>
			      <td colspan="1"><input type="text" name="SERVICE_PORT" size="8" value="190"  readonly="readonly" ></td>
			      <td colspan="1" align="left">Login</td>
			      <td colspan="1"><input type="text" name="LOGIN" size="15" value="pretups"  readonly="readonly" ></td>
			    </tr>
			   
			    <tr>
			      <td colspan="1" align="left">Password</td>
			      <td colspan="1"><input type="password" name="PASSWORD" size="15" value="pretups123" readonly="readonly" ></td>
			       <td colspan="1" align="left">Source Type</td>
			      <td colspan="1"><input type="text" name="SOURCE_TYPE" size="10" value="USSD"  readonly="readonly" ></td>
			    </tr>
			     <tr>
			      <td colspan="1" align="left">Application</td>
			      <td colspan="3"><input type="text" name="APP" size="50" value="/pretups/C2SReceiver"   readonly="readonly" ></td>
			    </tr>
			</table>

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