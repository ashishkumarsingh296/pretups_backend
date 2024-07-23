var xmlHttp = null ;

var pinNum = widget.retrieveWidgetUserData(179878594,"launchPIN") ;
var mobile = widget.retrieveWidgetUserData(179878594,"launchMSISDN") ;
var imei = widget.retrieveWidgetUserData(179878594, "IMEI");

var PreTUPSSERVER = widget.widgetProperty ("PreTUPSSERVER") ;
var LOGIN = widget.widgetProperty ("LOGIN") ;
var PASSWORD = widget.widgetProperty ("PASSWORD") ;
var REQUEST_GATEWAY_CODE = widget.widgetProperty ("REQUEST_GATEWAY_CODE") ;
var REQUEST_GATEWAY_TYPE = widget.widgetProperty ("REQUEST_GATEWAY_TYPE") ;
var SERVICE_PORT = widget.widgetProperty ("SERVICE_PORT") ;
var SOURCE_TYPE = widget.widgetProperty ("SOURCE_TYPE") ;
var LANGUAGE1 = widget.widgetProperty("LANGUAGE1");
var LANGUAGE2 = widget.widgetProperty("LANGUAGE2");
var GRECHARGE_TYPE = widget.widgetProperty ("PLAINREQ_TYPE") ;

var presentLang=widget.fetchLanguage();
var langCode = getPresentLan(presentLang);
widget.logWrite(7,"language code ::::::" +langCode);

function sendGiftRechargeReq(payeemobNumber,giftermobNumber,RechargeAmount,Pin)
{
	payeemobNumber 		= nullorUndefCheck(payeemobNumber);
	giftermobNumber 	= nullorUndefCheck(giftermobNumber);
	RechargeAmount 		= nullorUndefCheck(RechargeAmount);
	Pin 				= nullorUndefCheck(Pin);

	var str="";
	var divElement = "";
	var postData="";
	var url = "";
	var cdrStr = "";

	url=PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;

	if(DEMO_FLAG == '1' || DEMO_FLAG == 1)
	{
		url= DEMO_URL;
	}

	widget.logWrite(7,"url for sendGiftRechargeReq request::"+url);

	if(SEND_ENCRYPTREQ)
	{

		postData = "TYPE="+GRECHARGE_TYPE+"&MSISDN="+mobile+"&Message="+getEncrypt("IMEI="+imei+"&PIN="+Pin+"&MSISDN2="+giftermobNumber+"&AMOUNT="+RechargeAmount+"&SELECTOR="+SELECTOR);
	}else
	{//TYPE=GRC&MSISDN=<MSISDN>&IMEI=<IMEI>&PIN=<pin>&MSISDN2=<Receiver MSISDN>&AMOUNT=<amount>&GIFTER_NAME=<name>&GIFTER_MSISDN=<MSISDN>&LANGUAGE1=<lang code>&LANGUAGE2=<lang code>
		postData = "TYPE="+GRECHARGE_TYPE+"&MSISDN="+mobile+"&IMEI="+imei+"&PIN="+Pin+"&MSISDN2="+giftermobNumber+"&AMOUNT="+RechargeAmount+"&GIFTER_NAME=&GIFTER_MSISDN="+payeemobNumber+"&LANGUAGE1="+langCode+"&LANGUAGE2="+langCode;
	}

	widget.logWrite(7," sendGiftRechargeReq ::"+postData);

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
				widget.logWrite(7,"response for sendGiftRechargeReq::"+xmlText);
				if (xmlText != null && !xmlText.indexOf("null") > -1)
				{
					var txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
					var txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
					cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();

					widget.logWrite(7,"cdrstr logs.."+cdrStr);
					cdrcommon(cdrStr);
					if(txn_status == STR_SUCCESS)
					{
						divElement= document.getElementById("post");
						divElement.title = STR_TITLE;
						divElement.innerHTML = txn_message+"<setvar name=\"payeemobilenu\" value=''/><setvar name=\"Giftermobilenu\" value=''/><setvar name=\"amount\" value=''/><setvar name=\"pin\" value='' />";
						divElement.style.display = "block";

					}else
					{
						divElement= document.getElementById("post");
						divElement.title = STR_TITLE;
						divElement.innerHTML = txn_message+"<setvar name=\"mobilenu\" value=''><setvar name=\"amount\" value=''/><setvar name=\"pin\" value='' />";
						divElement.style.display = "block";
					}
				}else
				{

					str = STR_SERVER_ERROR;
					divElement= document.getElementById("post");
					divElement.title = STR_TITLE;
					divElement.innerHTML = str+"<setvar name=\"mobilenu\" value=''/><setvar name=\"amount\" value=''/><setvar name=\"pin\" value='' />";
					divElement.style.display = "block";
				}
			}else
			{
				str = STR_SERVICE_ERROR;
				divElement= document.getElementById("post");
				divElement.title =STR_TITLE;
				divElement.innerHTML = str+"<setvar name=\"mobilenu\" value=''/><setvar name=\"amount\" value=''/><setvar name=\"pin\" value=''/>";
				divElement.style.display = "block";
			} 

		};

		xmlHttp.open ("POST", url , false) ;
		xmlHttp.setRequestHeader("Content-Type", "plain");
		xmlHttp.setRequestHeader("Connection", "close");
		cdrStr += changetimeformat()+"| Recharge";
		xmlHttp.send (postData) ;


	}


}




