var xmlHttp = null ;
var USSD_BEARER_TYPE = 2;
var SMS_BEARER_TYPE = 3;
var widget = window.widget ;	
var bearer = widget.fetchBearerType () ;
var mobile = widget.retrieveWidgetUserData(179878594,"launchMSISDN") ;
var imei = widget.retrieveWidgetUserData(179878594, "IMEI");

function billPaymentMenu()

{
	var formPage="";
	var divElement;
		
	formPage +="<setvar name='mobilenu' value=''/><setvar name='amount' value=''/><setvar name='pin' value=''/>";
	formPage += "<div id='billpay' class='c3lMenuGroup'>";
	formPage += "<div width='100%' class='c3lMenuGroup topMessage'><span width='85%' align='left' >PreTUPS</span><a style='margin-right:20px' align='right' id= 'home' name='home' href='#Menu_Recharge'><img class='listicon' resimg='listicon.png' src='listicon.png'/></a></div><br/>";
	formPage += "<span width='100%' align='left' class='welcomeMsg'>Bill Payment</span><br/>";
	//formPage += "<img name=\"img\" valign='middle' align=\"center\" src=\"pretups_image.png\" /><br/>";
	formPage += "<form id='formid' name='form1' onsubmit='sendbillpayreq($mobilenu,$amount,$pin);'>";
	formPage += "<input type='mobileno' id='mobilenu'  emptyok='false' class='inputBG' align='center' resimg='inputbg.9.png' name='mobilenu' value='' title='"+STR_MSISDN_TITLE+"' maxLength='"+MSISDN_LEN+"'/><setvar name=\"mobilenu\" value=\"\"/>";
	formPage += "<input type='numeric' id='amount'  emptyok='false' align='center' class='inputBG' resimg='inputbg.9.png' name='amount' value=''   title='"+STR_AMOUNT_TITLE+"' maxLength='"+AMOUNT_LEN+"'/><setvar name=\"amount\" value=\"\"/>";
	formPage += "<input  emptyok='false' class='inputBG'   align='center'      type='numpassword' id='pin' resimg='inputbg.9.png' name='pin' title='"+STR_PIN_TITLE+"' value='' encrypt='true' maxLength='"+PIN_LEN+"'/><setvar name=\"pin\" value=\"\"/><br/>";
	formPage += "<br/>";
	formPage += "<input type='submit' name='submit' value='Submit' class='buttonBgBlue' resimg='buttonnew.png' align='center'/>" ;
	//formPage += "<br/>";
	formPage += "</form></div>" ;
	
	formPage = [formPage,"<specialcache name='Bill_Payment' url='specialCacheBillpay()' type='screen'/>"].join("");
	
	widget.savePermanent = SAVE_PERMINENT;
	divElement= document.getElementById("Bill_Payment");
	divElement.innerHTML = formPage;
	divElement.style.display = "block";  

return formPage;
}

function specialCacheBillpay()
{
	billPaymentMenu();
  
}
/*function billConfirmMsg(MSISDN,amount,password)
{
	var str="";
	var divElement;
	amount = Number(amount);
	var k = isNaN(amount) ;
	k = k.toString(); 
	widget.logWrite(6,"number value"+amount);
	widget.logWrite(6,"number value1"+k);
	widget.storeUserData("MobNo", MSISDN) ;
	widget.storeUserData("Amount", amount) ;
	widget.storeUserData("Password", password) ;
	
	
	if(MSISDN == "" || amount == "" || password=="")
	{
		str="Sorry,field(s) cannot be empty. Please enter valid input";

		divElement = document.getElementById("post2");
		divElement.innerHTML = str ;
		divElement.style.display = "block";
	}
	else if(amount>11111111||k=="true")
	{
		
	var element = document.getElementById ("post2");
	element.style.display = "block";
	
	element.innerHTML = "Please Enter Valid Amount.";
	}
	else
	{
		
		
		str += "Mobile no: "+MSISDN+"<br/><hr\>,";
		str += "Amount: "+amount+"<br/>";

		divElement= document.getElementById("billpayReq");
		divElement.innerHTML = str;
		divElement.style.display = "block";
		
	}
}*/

function sendbillpayreq(mobNumber,Amount,Pin)
{

	mobNumber 		= validateMSISDN(nullorUndefCheck(mobNumber));
	Amount 			= nullorUndefCheck(Amount);
	Pin 			= nullorUndefCheck(Pin);

	var str="";
	var divElement;
	var cdrStr = "";
	var url="";
	Amount = Number(Amount);
	var k = isNaN(Amount) ;
	k = k.toString(); 
	widget.logWrite(6,"number value"+Amount);
	widget.logWrite(6,"number value1"+k);
	
	if(mobNumber == "" || Amount == "" || Pin=="")
	{
		str="Sorry,field(s) cannot be empty. Please enter valid input";

		divElement = document.getElementById("post2");
		divElement.innerHTML = str ;
		divElement.style.display = "block";
	}
	else if(Amount>11111111||k=="true")
	{

		var element = document.getElementById ("post2");
		element.style.display = "block";

		element.innerHTML = "Please Enter Valid Amount.";
	}
	else
	{

		/*var PreTUPSSERVER = widget.widgetProperty ("PreTUPSSERVER") ;
		var LOGIN = widget.widgetProperty ("LOGIN") ;
		var PASSWORD = widget.widgetProperty ("PASSWORD") ;
		var REQUEST_GATEWAY_CODE = widget.widgetProperty ("REQUEST_GATEWAY_CODE") ;
		var REQUEST_GATEWAY_TYPE = widget.widgetProperty ("REQUEST_GATEWAY_TYPE") ;
		var SERVICE_PORT = widget.widgetProperty ("SERVICE_PORT") ;
		var SOURCE_TYPE = widget.widgetProperty ("SOURCE_TYPE") ;
		var TYPE = widget.widgetProperty ("TYPE") ;
		var SELECTOR =  widget.widgetProperty("SELECTOR");*/

		url= PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;
		if(DEMO_FLAG == '1' || DEMO_FLAG == 1)
		{
			url= DEMO_URL;
		}
		widget.logWrite(7,"url of sendbillpayreq request::"+url);
		//TYPE=STUPPBTRFREQ&IMEI=<IMEI_no>&MSISDN=<Retailer_MSISDN>&PIN=<DEALER_PIN>&MSISDN2=<Payee_MSISDN>&AMOUNT=<Amount>&SELECTOR=<0>
		//widget.logWrite(7,"postData of sendbillpayreq encrypt request::"+"TYPE="+TYPE+"&MSISDN="+mobile+"&Message=IMEI="+imei+"&MSISDN="+mobile+"&PIN="+Pin+"&MSISDN2="+mobNumber+"&AMOUNT="+Amount+"&SELECTOR="+SELECTOR);
		//var postData ="TYPE="+TYPE+"&MSISDN="+mobile+"&Message="+getEncrypt("IMEI="+imei+"&MSISDN="+mobile+"&PIN="+Pin+"&MSISDN2="+mobNumber+"&AMOUNT="+Amount+"&SELECTOR="+SELECTOR); 
		var postData ="TYPE="+BILLPAY_TYPE+"&MSISDN="+mobile+"&Message="+getEncrypt("IMEI="+imei+"&PIN="+Pin+"&MSISDN2="+mobNumber+"&AMOUNT="+Amount+"&SELECTOR="+SELECTOR);
		widget.logWrite(7,"postData of sendbillpayreq request::"+postData);

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
					widget.logWrite(7," response for billpayment::"+xmlText);
					if (xmlText != null && !xmlText.indexOf("null") > -1 )
					{
						var  txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
						var txn_message = responseStr(xmlText, STR_TXNMESSAGE);
						cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
						//widget.writeCDR (1, cdrStr) ;
						widget.logWrite(7,"cdrstr logs.."+cdrStr);
						cdrcommon(cdrStr);

						if (bearer == USSD_BEARER_TYPE)
						{
							str += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
						}

						divElement= document.getElementById("post");
						divElement.title= STR_TITLE;
						divElement.innerHTML = txn_message+"<setvar name=\"mobilenu\" value=\"\"/><setvar name=\"amount\" value=\"\"/><setvar name=\"pin\" value=\"\" />";
						divElement.style.display = "block";  


					}else
					{
						str = STR_SERVER_ERROR;
						divElement= document.getElementById("post");
						divElement.title= STR_TITLE;
						divElement.innerHTML = str+"<setvar name=\"mobilenu\" value=\"\"/><setvar name=\"amount\" value=\"\"/><setvar name=\"pin\" value=\"\" />";
						divElement.style.display = "block";  
					}
				}else
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
			xmlHttp.setRequestHeader("Content-Type",SOURCE_TYPE);
			xmlHttp.setRequestHeader("Connection", "close");
			cdrStr += changetimeformat()+"| billPayment ";
			xmlHttp.send (postData) ;
		}
	}
}
	

