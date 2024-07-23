var xmlHttp = null ;
var USSD_BEARER_TYPE = 2;
var SMS_BEARER_TYPE = 3;
var widget = window.widget ;	
var bearer = widget.fetchBearerType () ;

var mobile = widget.retrieveWidgetUserData(179878594,"launchMSISDN") ;
var imei = widget.retrieveWidgetUserData(179878594, "IMEI");
var pinNum = widget.retrieveWidgetUserData(179878594,"launchPIN") ;

var PreTUPSSERVER = widget.widgetProperty ("PreTUPSSERVER") ;
var LOGIN = widget.widgetProperty ("LOGIN") ;
var PASSWORD = widget.widgetProperty ("PASSWORD") ;
var REQUEST_GATEWAY_CODE = widget.widgetProperty ("REQUEST_GATEWAY_CODE") ;
var REQUEST_GATEWAY_TYPE = widget.widgetProperty ("REQUEST_GATEWAY_TYPE") ;
var SERVICE_PORT = widget.widgetProperty ("SERVICE_PORT") ;
var SOURCE_TYPE = widget.widgetProperty ("SOURCE_TYPE") ;
var TYPE = widget.widgetProperty ("TYPE") ;
var EXTNWCODE = widget.widgetProperty ("EXTNWCODE") ;
var MSISDN = widget.widgetProperty ("MSISDN") ;
var PIN = widget.widgetProperty ("PIN") ;
var RETURNSTOCK_TYPE = widget.widgetProperty ("PLAINREQ_TYPE") ;
var EXTCODE = widget.widgetProperty ("EXTCODE") ;
var EXTTXNNUMBER = widget.widgetProperty ("EXTTXNNUMBER") ;
var LOGINID = widget.widgetProperty("LOGINID");
var PASSWORDXML = widget.widgetProperty("PASSWORDXML");
var MSISDN2 = widget.widgetProperty("MSISDN2");
var LANGUAGE1 = widget.widgetProperty("LANGUAGE1");
var LANGUAGE2 = widget.widgetProperty("LANGUAGE2");
var SELECTOR =  widget.widgetProperty("SELECTOR");
var CATCODE = widget.widgetProperty("CATCODE");
var EMPCODE = widget.widgetProperty("EMPCODE");
var USERLOGINID = widget.widgetProperty("USERLOGINID");
var USEREXTCODE = widget.widgetProperty("USEREXTCODE");
var EXTREFNUM = widget.widgetProperty("EXTREFNUM");

function sendstockReq(msisdn,Amount,Pin)
{
	msisdn = nullorUndefCheck(msisdn);
	Amount = nullorUndefCheck(Amount);
	Pin= nullorUndefCheck(Pin);
	
	if(SENDREQUEST_XML)
	{
		sendstockReqXML('',Amount,Pin);
	}else
	{
	
	var str = "";
	var cdrStr = "";
	var divElement = "";
	var postData = "";
		
	url="" + PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT+"";
	if(DEMO_FLAG == '1' || DEMO_FLAG == 1)
	{
		url= DEMO_URL;
	}
	
	if(SEND_ENCRYPTREQ)
	{
		postData = "TYPE="+RETURNSTOCK_TYPE+"&MSISDN="+mobile+"&Message="+getEncrypt("IMEI="+imei+"&AMOUNT="+Amount+"&PIN="+Pin);
		
	}else
	{	
		postData = "TYPE="+RETURNSTOCK_TYPE+"&MSISDN="+mobile+"&MSISDN2="+msisdn+"&IMEI="+imei+"&AMOUNT="+Amount+"&PIN="+Pin+"&LANGUAGE1="+LANGUAGE1+"&LANGUAGE2="+LANGUAGE2;
	
	}
	widget.logWrite(7,"url sendstockReq :"+postData);
	widget.logWrite(7,"postData sendstockReq request:"+postData);

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
				widget.logWrite(7," response for sendstockReq ::"+xmlText);
				if (xmlText != null && !xmlText.indexOf("null") > -1)
				{
					var  txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
					var  txn_message		 = responseStr(xmlText, STR_TXNMESSAGE);
					cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
					
					widget.logWrite(7,"cdrstr logs.."+cdrStr);
					cdrcommon(cdrStr);

					if(bearer == USSD_BEARER_TYPE)
					{
						txn_message += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
					}

					divElement= document.getElementById("post");
					divElement.title=STR_TITLE;
					divElement.innerHTML = txn_message+"<setvar name='mobilenu' value=''/><setvar name='amount' value=''/><setvar name='pin' value=''/>";
					divElement.style.display = "block";  


				}else
				{
					str = STR_SERVER_ERROR;
					divElement= document.getElementById("post");
					divElement.title= STR_TITLE;
					divElement.innerHTML = str+"<setvar name='mobilenu' value=''/><setvar name='amount' value=''/><setvar name='pin' value=''/>";
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
				divElement.innerHTML = str+"<setvar name='mobilenu' value=''/><setvar name='amount' value=''/><setvar name='pin' value=''/>";
				divElement.style.display = "block";

			}

		};
		xmlHttp.open ("POST", url , false) ;
		xmlHttp.setRequestHeader("Content-Type",SOURCE_TYPE);
		xmlHttp.setRequestHeader("Connection", "close");
		cdrStr += changetimeformat()+"| sendstockReq ";
		xmlHttp.send (postData) ;
	}
}
}


function USSDBillGetAmount(msisdn)
{
	var str="";
	str += "<span>Return Stock</span><br/>Enter Amount<br/>";
	str += "<div id='main' class=''>";
	str += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDBillGetPin("+msisdn+",$amount)\">";
	str += "<input type='text' id='amount' emptyok='false' name='amount' value='' title='Enter Amount'/><setvar name=\"amount\" value=\"\"/>";
	str += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
	str += "</form></div>";
	divElement= document.getElementById("return_stock");
	divElement.innerHTML = str;
	divElement.style.display = "block";  


}
function USSDBillGetPin(msisdn,amount)
{
	var str="";
	str += "<span>Return Stock</span><br/>Enter PIN<br/>";
	str += "<div id='main' class=''>";
	str += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:confirmMsgUSSD("+msisdn+","+amount+",$password)\">";
	str += "<input type='password' id='password' emptyok='false' name='password' maxlength='10' value='' title='Enter PIN'/><setvar name=\"password\" value=\"\"/>";
	str += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
	str += "</form></div>";
	divElement= document.getElementById("return_stock");
	divElement.innerHTML = str;
	divElement.style.display = "block";  



}
function confirmMsgUSSD(MSISDN,amount,password)
{
	var formPage="";
	formPage += "<span>Return Stock</span><br/>Confirm Mobile No<br/>";
	formPage += "<div id='main' class=''>";
	formPage += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDConfirmNum("+MSISDN+","+amount+","+password+",$mobilenu)\">";
	formPage += "<input type='mobileno' id='mobilenu' emptyok='false' maxlength='15' name='mobilenu' value=''/><setvar name=\"mobilenu\" value=\"\"/>";
	formPage += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
	formPage += "</form></div>";
	
	divElement= document.getElementById("return_stock");
	divElement.innerHTML = formPage;
	divElement.style.display = "block";  
}

function USSDConfirmNum(msisdn,amount,pin,mobno)
{
	var formPage="";
	if(msisdn == mobno)
	{
		sendstockReq();
	}
	else
	{
		
		formPage += "<span>Please enter correct Mobile no</span><br/>Confirm Mobile No<br/>";
		formPage += "<div id='main' class=''>";
		formPage += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDConfirmNum("+msisdn+","+amount+","+pin+",$mobilenu)\">";
		formPage += "<input type='mobileno' id='mobilenu' emptyok='false' maxlength='15' name='mobilenu' value=''/><setvar name=\"mobilenu\" value=\"\"/>";
		formPage += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
		formPage += "</form></div>";
		divElement= document.getElementById("return_stock");
		divElement.innerHTML = formPage;
		divElement.style.display = "block";
	}


}

function sendstockReqXML(mobNumber,Amount,Pin)
{
var str="";
	var divElement;
	Amount = Number(Amount);
	var k = isNaN(Amount) ;
	k = k.toString(); 
	widget.logWrite(6,"number value"+Amount);
	widget.logWrite(6,"number value1"+k);
	
	var flag;
	//flag = isNAN(amount);
	
	if( Amount == "" || Pin =="")
	{
		str="Sorry,field(s) cannot be empty. Please enter valid input";

		divElement = document.getElementById("post2");
		divElement.innerHTML = str ;
		divElement.style.display = "block";
	}
	else if(Amount>11111111 || k=="true")
	{
		
	var element = document.getElementById ("post2");
	element.style.display = "block";
	
	element.innerHTML = "Please Enter Valid Amount.";
	}
	else
	{
	/* var mobNumber = widget.retrieveUserData("MobNo") ;
	var Amount = widget.retrieveUserData("Amount") ;
	var Pin = widget.retrieveUserData("Password") ; 
	widget.logWrite(6,"mobile no to be retrieved"+mobNumber);*/
	
	
	var date = changetimeformat();
	
	var url="" + PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT+"";
	//var xmldata = "<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE>EXOTHUSRBALREQ</TYPE><DATE>20-08-12 12:00:00</DATE><EXTNWCODE>MO</EXTNWCODE><CATCODE>CCE</CATCODE><EMPCODE>123</EMPCODE><LOGINID>Rahul_Choube</LOGINID><PASSWORD>com@1234</PASSWORD><EXTREFNUM></EXTREFNUM><DATA><MSISDN>9867045847</MSISDN><USERLOGINID>Mo_ws1</USERLOGINID><USEREXTCODE>123</USEREXTCODE></DATA></COMMAND>";
	
	//<?xml version="1.0"?><!DOCTYPE COMMAND PUBLIC "-//Ocam//DTD XML Command 1.0//EN" "xml/command.dtd"><COMMAND><TYPE>EXOTHUSRBALREQ</TYPE><EXTNWCODE>ET</EXTNWCODE><LANGUAGE1>0</LANGUAGE1><LANGUAGE2>0</LANGUAGE2><DATE>05/09/13</DATE><EXTREFNUM>665656</EXTREFNUM><EXTCODE>54800003</EXTCODE><LOGINID></LOGINID><PASSWORD></PASSWORD><MSISDN>548000000</MSISDN><MSISDN2>54800003</MSISDN2><PIN>2468</PIN><LOGINID></LOGINID><PASSWORD></PASSWORD><EXTCODE>3333></EXTCODE></COMMAND>
	
	
	//<?xml version="1.0"?><!DOCTYPE COMMAND PUBLIC "-//Ocam//DTD XML Command1.0//EN" "xml/command.dtd"><COMMAND><TYPE>EXOTHUSRBALREQ</TYPE><EXTNWCODE>MO</EXTNWCODE><LANGUAGE1>0</LANGUAGE1><LANGUAGE2>0</LANGUAGE2><DATE>31-10-2013 13:15:30</DATE><EXTREFNUM>665656</EXTREFNUM><EXTCODE>54800003</EXTCODE><LOGINID></LOGINID><PASSWORD></PASSWORD><MSISDN>548000000</MSISDN><MSISDN2>54800003</MSISDN2><PIN>2468</PIN></COMMAND>
	widget.logWrite(7,"Returnstock url:"+url);
	var xmldata = [ "<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command1.0//EN\" \"xml/command.dtd\">",
	              "<COMMAND>",
	              "<TYPE>" + TYPE + "</TYPE>",
	              "<DATE>"+date+"</DATE>",
	              "<EXTNWCODE>" + EXTNWCODE + "</EXTNWCODE>",
				  "<MSISDN>" + mobile + "</MSISDN>",
				  "<PIN>"+pinNum+"</PIN>",
				  "<LOGINID>"+LOGINID+"</LOGINID>",
	              "<PASSWORD>"+PASSWORDXML+"</PASSWORD>",
				  "<EXTCODE>"+EXTCODE+"</EXTCODE>",
				  "<EXTREFNUM>"+EXTREFNUM+"</EXTREFNUM>",	             
	              "</COMMAND>"].join("");
	
	widget.logWrite(7,"xml Returnstock request:"+xmldata);
	if (null == xmlHttp)
	{
		xmlHttp = new XMLHttpRequest () ;			
	}
	if (xmlHttp)
	{
		xmlHttp.onreadystatechange = returnstockReq ;
		xmlHttp.open ("POST", url , false) ;
		xmlHttp.setRequestHeader("Content-Type", "xml");
		xmlHttp.setRequestHeader("Connection", "close");
		xmlHttp.send (xmldata) ;
	}
	}

}

function returnstockReq()
{

	if (4 == xmlHttp.readyState &&  200 == xmlHttp.status)
	{
		//var xmlDoc = xmlHttp.responseXML ;
		var xmlText = xmlHttp.responseText ;
		widget.logWrite(7,"xml response for return stock"+xmlText);
		if (xmlText != null )
		{
			parsereturnstock(xmlText) ;
		}
		else
		{
		str = "Service Unavailable";
		}
	}
	else
		{
			var str;
			var divElement;
			str = "Service Unavailable";
				if (bearer == USSD_BEARER_TYPE)
			{
				str += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
			}
			divElement= document.getElementById("post");
			divElement.innerHTML = str+"<setvar name='mobilenu' value=''/><setvar name='amount' value=''/><setvar name='pin' value=''/>";
			divElement.style.display = "block";  
		}
}


function parsereturnstock(xmlDoc)
{
	var divElement;
	var str="";
	var rootele = document.createElement ("root") ;
	rootele.innerHTML = xmlDoc ;
	
	var stockInfo = rootele.getElementsByTagName("COMMAND") ;
	var type =  stockInfo[0].getElementsByTagName("TYPE")[0].textContent;
	var tran_Status =  stockInfo[0].getElementsByTagName("TXNSTATUS")[0].textContent;
	var date =  stockInfo[0].getElementsByTagName("DATE")[0].textContent;
	var unque_no =  stockInfo[0].getElementsByTagName("EXTREFNUM")[0].textContent;
	var is_txnid = stockInfo[0].getElementsByTagName("TXNID")[0];
	var tran_ID ;
	if(is_txnid != null){		
		tran_ID =  is_txnid.textContent;
	}
	var is_tran_Msg =  stockInfo[0].getElementsByTagName("MESSAGE")[0];
	var tran_Msg;
	if(is_tran_Msg != null){
		tran_Msg =  is_tran_Msg.textContent;
	}
		
	var is_record = rootele.getElementsByTagName("RECORD");
	if(is_record){
		var is_prodCode = is_record[0].getElementsByTagName("PRODUCTCODE")[0];
		var prod_Code ;
		if(is_prodCode != null){		
			prod_Code =  is_prodCode.textContent;
		}
		var is_prodName = is_record[0].getElementsByTagName("PRODUCTSHORTNAME")[0];
		var prod_Name ;
		if(is_prodName != null){		
			prod_Name =  is_prodName.textContent;
		}
		var is_bal = is_record[0].getElementsByTagName("BALANCE")[0];
		var bal ;
		if(is_bal != null){		
			bal =  is_bal.textContent;
		}
		if(prod_Name != null && bal != null){
			tran_Msg = "Product Name: " + prod_Name + "\nBalance: "+bal;
		}
	}
	if(tran_Msg != null){
		str += tran_Msg;
	}else{
		str += "Error ( "+tran_Status+" )";
	}	
	if (bearer == USSD_BEARER_TYPE)
	{
		str += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
	}
		
	divElement= document.getElementById("post");
	divElement.innerHTML = str+"<setvar name='mobilenu' value=''/><setvar name='amount' value=''/><setvar name='pin' value=''/>";
	divElement.style.display = "block";  
	
}