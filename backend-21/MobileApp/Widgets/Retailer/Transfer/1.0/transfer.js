var xmlHttp = null ;
var USSD_BEARER_TYPE = 2;
var SMS_BEARER_TYPE = 3;
var widget = window.widget ;	
var bearer = widget.fetchBearerType () ;
var mobile = widget.retrieveWidgetUserData(179878594,"launchMSISDN") ;
var imei = widget.retrieveWidgetUserData(179878594, "IMEI");

var PreTUPSSERVER = widget.widgetProperty ("PreTUPSSERVER") ;
var LOGIN = widget.widgetProperty ("LOGIN") ;
var PASSWORD = widget.widgetProperty ("PASSWORD") ;
var REQUEST_GATEWAY_CODE = widget.widgetProperty ("REQUEST_GATEWAY_CODE") ;
var REQUEST_GATEWAY_TYPE = widget.widgetProperty ("REQUEST_GATEWAY_TYPE") ;
var SERVICE_PORT = widget.widgetProperty ("SERVICE_PORT") ;
var SOURCE_TYPE = widget.widgetProperty ("SOURCE_TYPE") ;
var TRANSFER_TYPE = widget.widgetProperty ("TYPE") ;
var EXTNWCODE = widget.widgetProperty ("EXTNWCODE") ;
var MSISDN = widget.widgetProperty ("MSISDN") ;
var PIN = widget.widgetProperty ("PIN") ;
var EXTCODE = widget.widgetProperty ("EXTCODE") ;
var EXTTXNNUMBER = widget.widgetProperty ("EXTTXNNUMBER") ;
var LOGINID = widget.widgetProperty("LOGINID");
var PASSWORDXML = widget.widgetProperty("PASSWORDXML");
var MSISDN2 = widget.widgetProperty("MSISDN2");
var LANGUAGE1 = widget.widgetProperty("LANGUAGE1");
var LANGUAGE2 = widget.widgetProperty("LANGUAGE2");
var SELECTOR =  widget.widgetProperty("SELECTOR");
var PRODUCT_CODE =widget.widgetProperty("PRODUCT_CODE");
/*function transfermenu()
{
	var formPage="";
	var divElement;
	var str = "";
	if (bearer == USSD_BEARER_TYPE)
	{
		formPage += "<span>Transfer</span><br/>Enter Mobile No<br/>";
		formPage += "<div id='main' class=''>";
		formPage += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDBillGetAmount($mobilenu)\">";
		formPage += "<input type='numeric' id='mobilenu' emptyok='false' maxlength='"+MSISDN_LEN+"' name='mobilenu' value=''/><setvar name=\"mobilenu\" value=\"\"/>";
		formPage += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
		formPage += "</form></div>";


	}
	else
	{
		
		formPage += "<div id='transfer' class='c3lMenuGroup'>";
		formPage += "<div width='100%' class='c3lMenuGroup topMessage'><span align='left' >PreTUPS</span><a style='margin-right:20px' align='right' id= 'home' name='home' href='wgt:179878594/1.0/:displayMain()'><img class='listicon' resimg='listicon.png' src='listicon.png'/></a></div><br/>";
		formPage += "<span width='100%' align='left' class='welcomeMsg'>Transfer</span><br/>";
		//formPage += "<img name=\"img\" valign='middle' align=\"center\" src=\"pretups_image.png\" /><br/>";
		//formPage += "<form id='formid' name='form1' onsubmit='confirmMsg($mobilenu,$amount,$pin);'>";
		formPage += "<input type='numeric' id='mobilenu' class='inputTitle' emptyok='false' class='inputBG' resimg='inputbg.9.png' align='center' width='80%'  name='mobilenu' value='' title='Enter MSISDN' maxLength='"+MSISDN_LEN+"'/>";
		formPage += "<input type='decimal' id='amount' class='inputTitle' emptyok='false' align='center' class='inputBG' resimg='inputbg.9.png' name='amount' value='' width='80%'  title='Enter Amount' maxLength='"+AMOUNT_LEN+"' />";
		formPage += "<input class='inputTitle' emptyok='false' class='inputBG' resimg='inputbg.9.png'   align='center'    width='80%'  type='numpassword' id='pin' name='pin' title='PIN' value='' encrypt='true' maxLength='"+PIN_LEN+"'/><br/>";
		formPage += "<br/>";
		//formPage += "<input type='submit' name='submit' value='Submit' class='buttonBgBlue' align='center'/>" ;
		formPage += "<input type='button' name='submit' value='Submit' class='buttonBgBlue' resimg='buttonnew.png' align='center' onClick='sendTransferReq($mobilenu,$amount,$pin);'/>";
//		formPage += "<input type='button' name='submit' value='Submit' class='buttonBgBlue' align='center' onClick='#confirmTransfer'/>";

		//formPage += "<br/>";
		//formPage += "</form></div>" ;
		formPage += "</div>" ;
		formPage += "<setvar name=\"mobilenu\" value=\"\"/><setvar name=\"amount\" value=\"\"/><setvar name=\"pin\" value=\"\" />"
	

	}
	
	
	

	widget.savePermanent = SAVE_PERMINENT;
	divElement= document.getElementById("transfer");
	divElement.innerHTML = formPage;
	divElement.style.display = "block";  
}*/

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

function sendTransferReq(mobNumber,amount,pin)
{
	
	mobNumber 		= nullorUndefCheck(mobNumber);
	RechargeAmount 	= nullorUndefCheck(amount);
	Pin 			= nullorUndefCheck(pin);

		var str="";
		var postData = "";
		var divElement = "";
		var url = "";
		var cdrStr = "";
		RechargeAmount = Number(RechargeAmount);
		var k = isNaN(RechargeAmount) ;
		k = k.toString(); 
		widget.logWrite(6,"bearer type:"+bearer);
		widget.logWrite(6,"number value"+RechargeAmount);
		widget.logWrite(6,"number value1"+k);

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
			divElement.innerHTML = "Please Enter Valid Amount."+"<setvar name='mobilenu' value=''/><setvar name='amount' value=''/><setvar name='pin' value=''/>";
		}else
		{
			
						
			url=PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;

			if(DEMO_FLAG == '1' || DEMO_FLAG == 1)
			{
				url= DEMO_URL;
			}

			widget.logWrite(7,"url for sendTransferReq request::"+url);
			if(SEND_ENCRYPTREQ)
			{
				postData = "TYPE="+TRANSFER_TYPE+"&MSISDN="+mobile+"&Message="+getEncrypt("IMEI="+imei+"&PIN="+Pin+"&MSISDN2="+mobNumber+"&AMOUNT="+RechargeAmount+"&SELECTOR="+SELECTOR);
				
			}else
			{		
				postData = "TYPE="+TRANSFER_TYPE+"&MSISDN="+mobile+"&IMEI="+imei+"&PIN="+Pin+"&MSISDN2="+mobNumber+"&AMOUNT="+RechargeAmount+"&LANGUAGE1="+LANGUAGE1+"&LANGUAGE2="+LANGUAGE2;
			}

			widget.logWrite(7," sendTransferReq postdata ::"+postData);

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
						widget.logWrite(7,"response for sendTransferReq::"+xmlText);
						if (xmlText != null && !xmlText.indexOf("null") > -1)
						{
							var txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
							var txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
							cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
							//widget.writeCDR (1, cdrStr) ;
							widget.logWrite(7,"cdrstr logs.."+cdrStr);
							cdrcommon(cdrStr);
							if(txn_status == STR_SUCCESS)
							{
								divElement= document.getElementById("post");
								divElement.title = STR_TITLE;
								divElement.innerHTML = txn_message+"<setvar name='mobilenu' value=''/><setvar name='amount' value=''/><setvar name='pin' value=''/>";
								divElement.style.display = "block";

							}else
							{
								divElement= document.getElementById("post");
								divElement.title = STR_TITLE;
								divElement.innerHTML = txn_message+"<setvar name='mobilenu' value=''/><setvar name='amount' value=''/><setvar name='pin' value=''/>";
								divElement.style.display = "block";
							}
						}else
						{

							str = STR_SERVER_ERROR;
							divElement= document.getElementById("post");
							divElement.title = STR_TITLE;
							divElement.innerHTML = str;
							divElement.style.display = "block";
						}
					}else
					{
						str = STR_SERVICE_ERROR;
						divElement= document.getElementById("post");
						divElement.title =STR_TITLE;
						divElement.innerHTML = str;
						divElement.style.display = "block";
					} 

				};

				xmlHttp.open ("POST", url , false) ;
				xmlHttp.setRequestHeader("Content-Type", "plain");
				xmlHttp.setRequestHeader("Connection", "close");
				cdrStr += changetimeformat()+"| Transfer";
				xmlHttp.send (postData) ;


			}

		}
	

	
}


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
