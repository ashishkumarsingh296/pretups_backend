var xmlHttp = null ;
var USSD_BEARER_TYPE = 2;
var SMS_BEARER_TYPE = 3;
var widget = window.widget ;	
var bearer = widget.fetchBearerType () ;
var mobile = widget.retrieveWidgetUserData(179878594,"launchMSISDN") ;
var imei = widget.retrieveWidgetUserData(179878594, "IMEI");

function transfermenu()
{
	var formPage="";
	var divElement;
	
	if (bearer == USSD_BEARER_TYPE)
	{
		formPage += "<span>Transfer</span><br/>Enter Mobile No<br/>";
		formPage += "<div id='main' class=''>";
		formPage += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDBillGetAmount($mobilenu)\">";
		formPage += "<input type='numeric' id='mobilenu' emptyok='false' maxlength='"+MSISDN_LEN+"' name='mobilenu' value=''/><setvar name=\"mobilenu\" value=\"\"/>";
		formPage += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
		formPage += "</form></div>";
		widget.savePermanent = SAVE_PERMINENT;
		divElement= document.getElementById("transfer");
		divElement.innerHTML = formPage;
		divElement.style.display = "block"; 

	}
	else
	{
		
		formPage += "<div class='c3lMenuGroup'>";
		formPage += "<setvar name=\"mobilenu\" value=\"\"/><setvar name=\"amount\" value=\"\"/><setvar name=\"pin\" value=\"\" />";
		formPage += "<div width='100%' class='c3lMenuGroup topMessage'><span align='left' >PreTUPS</span><a style='margin-right:20px' align='right'  href='#Menu_Recharge'><img class='listicon' resimg='listicon.png' src='listicon.png'/></a></div><br/>";
		formPage += "<span width='100%' align='left' class='welcomeMsg'>Transfer</span><br/>";
		formPage += "<input type='numeric' id='mobilenu'  emptyok='false' class='inputBG' align='center'   name='mobilenu' value='' title='"+STR_MSISDN_TITLE+"' maxLength='"+MSISDN_LEN+"'/>";
		formPage += "<input type='decimal' id='amount'  emptyok='false' align='center' class='inputBG' name='amount' value='' '  title='"+STR_AMOUNT_TITLE+"' maxLength='"+AMOUNT_LEN+"' />";
		formPage += "<input emptyok='false' class='inputBG'   align='center'      type='numpassword' id='pin' name='pin' title='"+STR_PIN_TITLE+"' value='' encrypt='true' maxLength='"+PIN_LEN+"'/><br/>";
		formPage += "<br/>";
		formPage += "<input type='button' name='submit' value='Transfer' class='buttonBgBlue' align='center' onClick='sendTransferReq($mobilenu,$amount,$pin);'/>";
		//formPage += "<input type='button' name='submit' value='Submit' class='buttonBgBlue' align='center' onClick='#confirmTransfer'/>";
		formPage += "</div>" ;
		
		return formPage; 
		
	}
	
	
}

function confirmTransfer(mobile,amt,password)
{
	var confirmElement = document.getElementById("confirmTransfer");
	var str = "";
/*	str += "<div id='transfer1' class='c3lMenuGroup'>";
	str += "<div width='100%' class='c3lMenuGroup topMessage'><span align='left' >PreTUPS</span><a style='margin-right:20px' align='right' id= 'home' name='home' href='wgt:179878594/1.0/:displayMain()'><img src='listicon.png'/></a></div><br/>";
	str += "<span width='100%' align='left' class='welcomeMsg'>Confirm Transfer</span><br/>";
	var widthStr = "";	
	var devicemodel = (widget.clientVersion).toUpperCase();
	if(!(widget.fetchScreenWidth() <= 176 && devicemodel.indexOf("J2ME") != -1)){
		widthStr = "width='90%'";
	}
	str += "<div id='transfer2' class='c3lMenuGroup inputBG confirmPad' "+widthStr+" align='center'><form id='formid' name='form1' onsubmit='sendTransferReq($mobilenu,$amount,$tranid);'>";
	str += "<span class='title1 confirmAlign' align='left'>You are requesting to transfer </span><br/><div class='c3lMenuGroup' align='center'><span class='title1 confirmAlign' align='left' valign='middle' width='50%'>Amount (In Rs.):</span>";
	str += "<input type='numeric' id='amount' class='confirmTitle confirmAlign' align='right' valign='middle' name='amount' value='' width='50%'  title='Amount' readonly /></div>";
	str += "<div class='c3lMenuGroup' align='center'><span class='title1 confirmAlign' align='left' valign='middle' width='50%' style='margin-left:0px'>Agent's number:</span><input type='numeric' id='mobilenu' class='confirmTitle confirmAlign ' align='right' valign='middle' width='50%'  maxlength='15' name='mobilenu' value='' title='MSISDN' readonly /></div><br/><span class='title1 confirmAlign' align='left'>Do you want to continue?</span>";
	str += "<br/>";
	str += "<input type='submit' name='submit' value='Yes' class='buttonBgBlue' align='center'/>"; 
	str += "<input type='button' name='cancel' value='No' class='buttonBgBlue' align='center' onClick='#transfer'/>";
	str += "</form></div></div>" ;*/
	str += "You are requesting to transfer amount of Rs. "+amt+" to the Agent's number "+mobile;
	storePin(password);
	var tranId =widget.fetchTransactionID();
	storeTXN(tranId);
	str += "<setvar name=\"mobilenu\" value=\""+mobile+"\"/><setvar name=\"amount\" value=\""+amt+"\"/><setvar name=\"tranid\" value=\""+tranId+"\" />";
	widget.logWrite(7,"Set var txnId ---->"+ tranId);
	confirmElement.innerHTML = str;
	confirmElement.style.display = "block";  
}

function sendTransferReq(mobNumber,Amount,Pin)
{
	var str="";
	var cdrStr = "";
	var url="";
	var divElement;
	Amount = Number(Amount);
	var k = isNaN(Amount) ;
	k = k.toString(); 
	widget.logWrite(7,"number value"+Amount);
	widget.logWrite(7,"number value1"+k);
	
	//var Pin = retrievePIN();
	//var storedTxnId = retrieveTXN();
	//widget.logWrite(7,"Rxd TX ID ---->"+txnId+"\nStored tX ID ---->"+storedTxnId);
	//var flag;
	
	if(bearer != SMS_BEARER_TYPE && (mobNumber == "" || RechargeAmount == "" || Pin==""))
	{
		str="Sorry,field(s) cannot be empty. Please enter valid input";

		divElement = document.getElementById("post2");
		divElement.innerHTML = str ;
		divElement.style.display = "block";
	}else if(bearer != SMS_BEARER_TYPE && (RechargeAmount>11111111 || k=="true"))
	{

		divElement = document.getElementById ("post2");
		divElement.style.display = "block";
		divElement.innerHTML = "Please Enter Valid Amount.";
	}
	/*else if(txnId != storedTxnId)
	{
		str="There seems to be some error. Technical team working on it. Please try again later.";
		str += "<setvar name=\"mobilenu\" value=\"\"/><setvar name=\"amount\" value=\"\"/><setvar name=\"tranid\" value=\"\" />";		
		divElement = document.getElementById("post2");
		clearSensitiveData();
		divElement.innerHTML = str ;
		divElement.style.display = "block";
	}*/else
	{	
	/*var PreTUPSSERVER = widget.systemProperty ("PreTUPSSERVER") ;
	var LOGIN = widget.systemProperty ("LOGIN") ;
	var PASSWORD = widget.systemProperty ("PASSWORD") ;
	var REQUEST_GATEWAY_CODE = widget.systemProperty ("REQUEST_GATEWAY_CODE") ;
	var REQUEST_GATEWAY_TYPE = widget.systemProperty ("REQUEST_GATEWAY_TYPE") ;
	var SERVICE_PORT = widget.systemProperty ("SERVICE_PORT") ;
	var SOURCE_TYPE = widget.systemProperty ("SOURCE_TYPE") ;
	var TYPE = widget.widgetProperty ("TYPE") ;
	var PRODUCT_CODE = widget.widgetProperty ("PRODUCT_CODE") ;	
	 */	
	url= PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;
	
	if(DEMO_FLAG == '1' || DEMO_FLAG == 1)
	{
		url= DEMO_URL;
	}
	
	if(mobNumber != "")
	{
		if(COUNTRY_CODE_CHECK)
		{
			if(mobNumber .indexOf(COUNTRY_CODE) >-1)
			{
				mobNumber.replace(COUNTRY_CODE,"");
			}
		}
	}
			
	//var postData = "TYPE="+TYPE+"&MSISDN="+mobile+"Message="+getEncrypt("&IMEI="+imei+"&MSISDN="+mobile+"&MSISDN2="+mobNumber+"&TOPUPVALUE="+Amount+"&PRODUCTCODE="+PRODUCT_CODE+"&PIN="+Pin);
	var postData = "TYPE="+TRANSFER_TYPE+"&MSISDN="+mobile+"&Message="+getEncrypt("IMEI="+imei+"&MSISDN2="+mobNumber+"&TOPUPVALUE="+Amount+"&PRODUCTCODE="+PRODUCT_CODE+"&PIN="+Pin);	
	widget.logWrite(7,"postData sendTransferReq::"+postData);
	if (null == xmlHttp)
	{
		xmlHttp = new XMLHttpRequest () ;			
	}
	if (xmlHttp)
	{
		xmlHttp.onreadystatechange = function()
		{
			if (4 == xmlHttp.readyState &&  200 == xmlHttp.status)
			{
				var xmlText = xmlHttp.responseText ;
				widget.logWrite(7,"response for transfer::"+ xmlText);
				
				if (xmlText != null && !xmlText.indexOf("null") > -1)
				{
					var  txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
					var  txn_message		 = responseStr(xmlText, STR_TXNMESSAGE);
					cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
					//widget.writeCDR (1, cdrStr) ;
					widget.logWrite(7,"cdrstr logs.."+cdrStr);
					
					cdrcommon(cdrStr);

					if (bearer == USSD_BEARER_TYPE)
					{
						txn_message += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
					}

					divElement= document.getElementById("post");
					divElement.title= STR_TITLE;
					divElement.innerHTML = txn_message+"<setvar name=\"mobilenu\" value=\"\"/><setvar name=\"amount\" value=\"\"/><setvar name=\"pin\" value=\"\" />";
					divElement.style.display = "block";  
					
				}
				else
				{
					str = STR_SERVER_ERROR;
					divElement= document.getElementById("post");
					divElement.title= STR_TITLE;
					divElement.innerHTML = str+"<setvar name=\"mobilenu\" value=\"\"/><setvar name=\"amount\" value=\"\"/><setvar name=\"pin\" value=\"\" />";
					divElement.style.display = "block";  
				}
			}
			else
			{
					var str;
					var divElement;
					str = STR_SERVICE_ERROR;
						if (bearer == USSD_BEARER_TYPE)
					{
						str += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
					}
					divElement= document.getElementById("post");
					divElement.title= STR_TITLE;
					divElement.innerHTML = str+"<setvar name=\"mobilenu\" value=\"\"/><setvar name=\"amount\" value=\"\"/><setvar name=\"pin\" value=\"\" />";
					divElement.style.display = "block";  
			}
		};
		xmlHttp.open ("POST", url , false) ;
		xmlHttp.setRequestHeader("Content-Type", "plain");
		xmlHttp.setRequestHeader("Connection", "close");
		cdrStr += changetimeformat()+"| Transfer ";
		xmlHttp.send (postData) ;
	}
	}

}

/*function parsetransfer(xmlDoc)
{
	var divElement;
	var str="";
	var rootele = document.createElement ("root") ;
	rootele.innerHTML = xmlDoc ;
	
	var stockInfo = rootele.getElementsByTagName("COMMAND") ;
	var type;
	if(stockInfo[0].getElementsByTagName("TYPE")){
	       	type = stockInfo[0].getElementsByTagName("TYPE")[0].textContent;
	}
	var tran_Status;
       if(stockInfo[0].getElementsByTagName("TXNSTATUS")){
       	    	tran_Status   =  stockInfo[0].getElementsByTagName("TXNSTATUS")[0].textContent;
       }
	var date1;
	if(stockInfo[0].getElementsByTagName("DATE")){
		date1 =  stockInfo[0].getElementsByTagName("DATE")[0].textContent;
	}
	var unque_no;
        if(stockInfo[0].getElementsByTagName("EXTREFNUM")){
		unque_no =  stockInfo[0].getElementsByTagName("EXTREFNUM")[0].textContent;
	}
	var tran_ID;
	if(stockInfo[0].getElementsByTagName("TXNID")){
	       	tran_ID = stockInfo[0].getElementsByTagName("TXNID")[0].textContent;
	}
	var tran_Msg;
	if(stockInfo[0].getElementsByTagName("MESSAGE")){
		tran_Msg =  stockInfo[0].getElementsByTagName("MESSAGE")[0].textContent;
	}
	clearSensitiveData();
	if(tran_Msg != null){
		str += tran_Msg;
	}else{
		str += "Error ( "+tran_Status+" )";
	}	
	widget.logWrite(6,"Transfer response message received:"+str);
	if (bearer == USSD_BEARER_TYPE)
	{
		str += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
	}
	str += "<setvar name=\"mobilenu\" value=\"\"/><setvar name=\"amount\" value=\"\"/><setvar name=\"tranid\" value=\"\" />";
	divElement= document.getElementById("post");
	divElement.innerHTML = str;
	divElement.style.display = "block";  
	
}*/
function USSDBillGetAmount(msisdn)
{
	var str="";
	str += "<span>Transfer</span><br/>Enter Amount<br/>";
	str += "<div id='main' class=''>";
	str += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDBillGetPin("+msisdn+",$amount)\">";
	str += "<input type='text' id='amount' emptyok='false' name='amount' value='' title='Enter Amount'/><setvar name=\"amount\" value=\"\"/>";
	str += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
	str += "</form></div>";
	divElement= document.getElementById("transfer");
	divElement.innerHTML = str;
	divElement.style.display = "block";  


}
function USSDBillGetPin(msisdn,amount)
{
	var str="";
	str += "<span>Transfer</span><br/>Enter PIN<br/>";
	str += "<div id='main' class=''>";
	str += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:confirmMsgUSSD("+msisdn+","+amount+",$password)\">";
	str += "<input type='password' id='password' emptyok='false' name='password' maxlength='10' value='' title='Enter PIN'/><setvar name=\"password\" value=\"\"/>";
	str += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
	str += "</form></div>";
	divElement= document.getElementById("transfer");
	divElement.innerHTML = str;
	divElement.style.display = "block";  



}
function confirmMsgUSSD(MSISDN,amount,password)
{
	var formPage="";
	formPage += "<span>Transfer</span><br/>Confirm Mobile No<br/>";
	formPage += "<div id='main' class=''>";
	formPage += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDConfirmNum("+MSISDN+","+amount+","+password+",$mobilenu)\">";
	formPage += "<input type='numeric' id='mobilenu' emptyok='false' maxlength='15' name='mobilenu' value=''/><setvar name=\"mobilenu\" value=\"\"/>";
	formPage += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
	formPage += "</form></div>";
	
	divElement= document.getElementById("transfer");
	divElement.innerHTML = formPage;
	divElement.style.display = "block";  
}

function USSDConfirmNum(msisdn,amount,pin,mobno)
{
	var formPage="";
	if(msisdn == mobno)
	{
		sendTransferReq();
	}
	else
	{
		
		formPage += "<span>Please enter correct Mobile no</span><br/>Confirm Mobile No<br/>";
		formPage += "<div id='main' class=''>";
		formPage += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDConfirmNum("+msisdn+","+amount+","+pin+",$mobilenu)\">";
		formPage += "<input type='numeric' id='mobilenu' emptyok='false' maxlength='15' name='mobilenu' value=''/><setvar name=\"mobilenu\" value=\"\"/>";
		formPage += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
		formPage += "</form></div>";
		divElement= document.getElementById("transfer");
		divElement.innerHTML = formPage;
		divElement.style.display = "block";
	}
}

