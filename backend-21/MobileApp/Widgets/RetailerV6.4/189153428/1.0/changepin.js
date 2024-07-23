var xmlHttp = null;
var USSD_BEARER_TYPE = 2;
var SMS_BEARER_TYPE = 3;
var widget = window.widget ;
var bearer = widget.fetchBearerType () ;
var mobile = widget.retrieveWidgetUserData(179878594, "launchMSISDN");
var imei = widget.retrieveWidgetUserData(179878594, "IMEI");

var PreTUPSSERVER = widget.widgetProperty ("PreTUPSSERVER") ;
var LOGIN = widget.widgetProperty ("LOGIN") ;
var PASSWORD = widget.widgetProperty ("PASSWORD") ;
var REQUEST_GATEWAY_CODE = widget.widgetProperty ("REQUEST_GATEWAY_CODE") ;
var REQUEST_GATEWAY_TYPE = widget.widgetProperty ("REQUEST_GATEWAY_TYPE") ;
var SERVICE_PORT = widget.widgetProperty ("SERVICE_PORT") ;
var SOURCE_TYPE = widget.widgetProperty ("SOURCE_TYPE") ;
var TYPE = widget.widgetProperty ("TYPE") ;
var CHANGEPIN_TYPE = widget.widgetProperty ("PLAINREQ_TYPE") ;
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
var EXTREFNUM =widget.widgetProperty("EXTREFNUM");

function sendchangepinreq(pin, newpin, confirmpin) {

	if(SENDREQUEST_XML)
	{
		sendchangepinreqXML(pin, newpin, confirmpin);
	}else
	{

		var str= "";
		var cdrStr = "";
		var divElement = "";
		var url = "";
		var postData = "";
		if (newpin == confirmpin) {

			url = PreTUPSSERVER + "?REQUEST_GATEWAY_CODE=" + REQUEST_GATEWAY_CODE + "&REQUEST_GATEWAY_TYPE=" + REQUEST_GATEWAY_TYPE + "&LOGIN=" + LOGIN + "&PASSWORD=" + PASSWORD + "&SOURCE_TYPE=" + SOURCE_TYPE + "&SERVICE_PORT=" + SERVICE_PORT + "";

			if(DEMO_FLAG == '1' || DEMO_FLAG == 1)
			{
				url= DEMO_URL;
			}
			widget.logWrite(7, "url sendchangepinreq::" + url);
			if(SEND_ENCRYPTREQ)
			{
				postData = "TYPE="+CHANGEPIN_TYPE+"&MSISDN="+mobile+"&Message="+getEncrypt("IMEI="+imei+"&NEWPIN="+newpin+"&CONFIRMPIN="+confirmpin+"&PIN="+pin+"&LANGUAGE1="+LANGUAGE1);
			}else
			{
				postData = "TYPE="+CHANGEPIN_TYPE+"&MSISDN="+mobile+"&IMEI="+imei+"&OLDPIN="+pin+"&NEWPIN="+newpin+"&CONFIRMPIN="+confirmpin+"&LANGUAGE1="+LANGUAGE1;
			}
			widget.logWrite(7, "postData sendchangepinreq::" + postData);
			if (null == xmlHttp) {
				xmlHttp = new XMLHttpRequest();
			}
			if (xmlHttp) {
				xmlHttp.onreadystatechange = function()
				{
					if (4 == xmlHttp.readyState && 200 == xmlHttp.status) {
						//var xmlDoc = xmlHttp.responseXML ;
						var xmlText = xmlHttp.responseText;
						widget.logWrite(7, "response for sendchangepinreq::" + xmlText);

						if (xmlText != null && !xmlText.indexOf("null") > -1)
						{
							var  txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
							var  txn_message		 = responseStr(xmlText, STR_TXNMESSAGE);
							cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
							widget.logWrite(7,"cdrstr logs.."+cdrStr);
							cdrcommon(cdrStr);

							if (bearer == USSD_BEARER_TYPE)
							{
								txn_message += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
							}

							divElement= document.getElementById("post");
							divElement.title= STR_TITLE;
							divElement.innerHTML = txn_message+"<setvar name='oldpin' value=''/><setvar name='newpin' value=''/><setvar name='confirmnewpin' value=''/>";
							divElement.style.display = "block";  

						} else {
							str = STR_SERVER_ERROR;
							divElement = document.getElementById("post");
							divElement.title= STR_TITLE;
							divElement.innerHTML = str+"<setvar name='oldpin' value=''/><setvar name='newpin' value=''/><setvar name='confirmnewpin' value=''/>";
							divElement.style.display = "block";
						}
					} else {
						str = STR_SERVICE_ERROR;
						if (bearer == USSD_BEARER_TYPE) {
							str += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
						}
						divElement = document.getElementById("post");
						divElement.title= STR_TITLE;
						divElement.innerHTML = str+"<setvar name='oldpin' value=''/><setvar name='newpin' value=''/><setvar name='confirmnewpin' value=''/>";
						divElement.style.display = "block";
					}



				};
				xmlHttp.open("POST", url, false);
				xmlHttp.setRequestHeader("Content-Type",SOURCE_TYPE);
				xmlHttp.setRequestHeader("Connection", "close");
				cdrStr += changetimeformat()+"| pin Change ";
				xmlHttp.send(postData);
			}
		} else {
			str = STR_NEWPIN_ERROR;
			divElement = document.getElementById("post2");
			divElement.title= STR_TITLE;
			divElement.innerHTML = str+"<setvar name='oldpin' value=''/><setvar name='newpin' value=''/><setvar name='confirmnewpin' value=''/>";
			divElement.style.display = "block";


		}


	}
}
function sendchangepinreqXML(pin,newpin,confirmpin)
{
	
	if(newpin == confirmpin)
	{
			
		var date = changetimeformat();
		//var url = "http://124.153.86.45:5557/pretups/C2SReceiver?REQUEST_GATEWAY_CODE=EXTGW&REQUEST_GATEWAY_TYPE=EXTGW&LOGIN=pretups&PASSWORD=pretups123&SOURCE_TYPE=EXTGW&SERVICE_PORT=190";


		var url="" + PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT+"";
		widget.logWrite(7, "url sendchangepinreqXML::" + url);
		//var xmldata = "<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE>EXC2SCPNREQ</TYPE><DATE>20-07-12 14:12:53</DATE><EXTNWCODE>NG</EXTNWCODE><MSISDN>9867045847</MSISDN><PIN>1357</PIN><NEWPIN>1234</NEWPIN><CONFIRMPIN>1234</CONFIRMPIN><LOGINID>Rahul_Choube</LOGINID><PASSWORD>com@1234</PASSWORD><LANGUAGE1>0</LANGUAGE1><EXTREFNUM>12345</EXTREFNUM></COMMAND>";
		//var xmldata = "<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE>EXC2SCPNREQ</TYPE><DATE>20-07-12 14:12:53</DATE><EXTNWCODE>NG</EXTNWCODE><MSISDN>9810848301</MSISDN><PIN>1357</PIN><NEWPIN>2468</NEWPIN><CONFIRMPIN>2468</CONFIRMPIN><LOGINID>khanduja</LOGINID><PASSWORD>com@1357</PASSWORD><LANGUAGE1>0</LANGUAGE1><EXTREFNUM>12345</EXTREFNUM></COMMAND>";
		/*
	var xmldata = [ "<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command1.0//EN\" \"xml/command.dtd\">",
	                "<COMMAND>",
	                "<TYPE>"+TYPE+"</TYPE>" ,
	                "<DATE>20-07-12 14:12:53</DATE>",
	                "<EXTNWCODE>"+EXTNWCODE+"</EXTNWCODE>",
	                "<MSISDN>"+MSISDN+"</MSISDN>",
	                "<PIN>"+pin+"</PIN>", 
	                "<NEWPIN>"+newpin+"</NEWPIN>",
	                "<CONFIRMPIN>"+newpin+"</CONFIRMPIN>",
	                "<LOGINID>"+LOGINID+"</LOGINID>",
	                "<PASSWORD>"+PASSWORDXML+"</PASSWORD>",
	                "<LANGUAGE1>"+LANGUAGE1+"</LANGUAGE1>",
	                "<EXTREFNUM>"+EXTREFNUM+"</EXTREFNUM>",
	                "</COMMAND>"].join("");

	var xmldata = [ "<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command1.0//EN\" \"xml/command.dtd\">",
	                "<COMMAND>",
	                "<TYPE>"+TYPE+"</TYPE>" ,
	                "<MSISDN1>"+MSISDN+"</MSISDN1>",
	                "<PIN>"+pin+"</PIN>", 
	                "<NEWPIN>"+newpin+"</NEWPIN>",
	                "<CONFIRMPIN>"+newpin+"</CONFIRMPIN>",
	                "<LANGUAGE1>"+LANGUAGE1+"</LANGUAGE1>",
	                "</COMMAND>"].join("");
		 */	
		var xmldata = [ "<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command1.0//EN\" \"xml/command.dtd\">",
		                "<COMMAND>",
		                "<TYPE>"+TYPE+"</TYPE>",
		                "<DATE>" + date + "</DATE>",
		                "<EXTNWCODE>"+EXTNWCODE+"</EXTNWCODE>",
		                "<MSISDN>"+mobile+"</MSISDN>",
		                "<PIN>"+pin+"</PIN>", 
		                "<NEWPIN>"+newpin+"</NEWPIN>",
		                "<CONFIRMPIN>"+confirmpin+"</CONFIRMPIN>",
		                "<LOGINID>"+LOGINID+"</LOGINID>",
		                "<PASSWORD>"+PASSWORDXML+"</PASSWORD>",
		                "<LANGUAGE1>1</LANGUAGE1>",
		                "<EXTREFNUM>"+EXTREFNUM+"</EXTREFNUM>",					
		                "</COMMAND>"].join("");
		widget.logWrite(7,"xml format request"+xmldata);
		if (null == xmlHttp)
		{
			xmlHttp = new XMLHttpRequest () ;			
		}
		if (xmlHttp)
		{
			xmlHttp.onreadystatechange = changePinReq;
			xmlHttp.open ("POST", url , false) ;
			xmlHttp.setRequestHeader("Content-Type", "xml");
			xmlHttp.setRequestHeader("Connection", "close");
			xmlHttp.send (xmldata) ;
		}
	}else
	{
		var str = "Please enter a correct New Pin";
		divElement= document.getElementById("post2");
		divElement.innerHTML = str+"<setvar name='oldpin' value=''/><setvar name='newpin' value=''/><setvar name='confirmnewpin' value=''/>";
		divElement.style.display = "block";  


	}
}
function changePinReq()
{
	if (4 == xmlHttp.readyState &&  200 == xmlHttp.status)
	{
		//var xmlDoc = xmlHttp.responseXML ;
		var xmlText = xmlHttp.responseText ;
		widget.logWrite(7,"xml response for pinchange"+xmlText);
		if (xmlText)
		{
			parsePinchange(xmlText) ;
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
		divElement.innerHTML = str+"<setvar name='oldpin' value=''/><setvar name='newpin' value=''/><setvar name='confirmnewpin' value=''/>";
		divElement.style.display = "block";  

	}

}

function parsePinchange(xmlDoc)
{
	var divElement;
	var str="";
	var rootele = document.createElement ("root") ;
	rootele.innerHTML = xmlDoc ;
	
	var pinchangeInfo = rootele.getElementsByTagName("COMMAND") ;
/*	if (0 == rechargeInfo.length)
	{
		str += "No information";
	}
	else
	{*/
	
	
	
		//var type =  rechargeInfo[0].getElementsByTagName("TYPE")[0].textContent;
		var tran_Status =  pinchangeInfo[0].getElementsByTagName("TXNSTATUS")[0].textContent;
		//var date =  rechargeInfo[0].getElementsByTagName("DATE")[0].textContent;
		//var unque_no =  rechargeInfo[0].getElementsByTagName("EXTREFNUM")[0].textContent;
		//var tran_ID =  rechargeInfo[0].getElementsByTagName("TXNID")[0].textContent;
		//var tran_Msg =  rechargeInfo[0].getElementsByTagName("MESSAGE")[0].textContent;
		
	//}
	if(tran_Status==200)
		{
			str += "Pin changed successfully";
		}
	else
		{
		var messageObj = pinchangeInfo[0].getElementsByTagName("MESSAGE")[0];
		if(messageObj != null && messageObj != undefined)
		{	
		var tran_Msg =  pinchangeInfo[0].getElementsByTagName("MESSAGE")[0].textContent;
			str += tran_Msg;
		}
		else
		{
		 	str += "ERROR ("+tran_Status+")";
		}
		}
	/*
	else
		{
	*/	
		//str += tran_Status;
		if (bearer == USSD_BEARER_TYPE)
			{
				str += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
			}
		//}
	divElement= document.getElementById("post");
	divElement.innerHTML = str+"<setvar name='oldpin' value=''/><setvar name='newpin' value=''/><setvar name='confirmnewpin' value=''/>";
	divElement.style.display = "block";  
	
}
