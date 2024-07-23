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
var OTHERBAL_TYPE = widget.widgetProperty ("TYPE") ;
var LANGUAGE1 = widget.widgetProperty("LANGUAGE1");
var LANGUAGE2 = widget.widgetProperty("LANGUAGE2");

var presentLang=widget.fetchLanguage();
var langCode = getPresentLan(presentLang);
widget.logWrite(7,"language code ::::::" +langCode);

function otherbalmenu()
{
	var formPage="";
	var divElement;
	
	if (bearer == USSD_BEARER_TYPE)
	{
		formPage += "<span>Withdraw</span><br/>Enter Mobile No<br/>";
		formPage += "<div id='main' class=''>";
		formPage += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDBillGetAmount($mobilenu)\">";
		formPage += "<input type='mobileno' id='mobilenu' emptyok='false' maxlength='15' name='mobilenu' value=''/><setvar name=\"mobilenu\" value=\"\"/>";
		formPage += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
		formPage += "</form></div>";


	}
	else
	{
		
		formPage += "<div  class='c3lMenuGroup'>";
		formPage += "<div width='100%' class='c3lMenuGroup topMessage'><span width='85%' align='left' >PreTUPS</span><a style='margin-right:20px' align='right' id= 'home' name='home' href='wgt:179878594/1.0/:displayMain()'><img src='listicon.png'/></a></div><br/>";
		formPage += "<span width='100%' align='left' class='welcomeMsg'>Withdraw</span><br/>";
		//formPage += "<img name=\"img\" valign='middle' align=\"center\" src=\"pretups_image.png\" /><br/>";
		formPage += "<form id='formid' name='form1' onsubmit='sendWithdrawReq($mobilenu,$amount,$pin);'>";
		formPage += "<input type='mobileno' id='mobilenu' class='inputTitle' emptyok='false' class='inputBG' align='center' width='80%'  maxlength='15' name='mobilenu' value='' title='Enter MSISDN'/><setvar name=\"mobilenu\" value=\"\"/>";
		formPage += "<input type='numeric' id='amount' class='inputTitle' emptyok='false' align='center' class='inputBG' name='amount' value='' width='80%'  title='Enter Amount'/><setvar name=\"amount\" value=\"\"/>";
		formPage += "<input class='inputTitle' emptyok='false' class='inputBG'   align='center'    width='80%'  type='numpassword' id='pin' name='pin' title='PIN' value=''/><setvar name=\"pin\" value=\"\"/><br/>";
		formPage += "<br/>";
		formPage += "<input type='submit' name='submit' value='Submit' class='buttonBgBlue' align='center'/>" ;
		//formPage += "<br/>";
		formPage += "</form></div>" ;
	}
	widget.savePermanent = true;
	divElement= document.getElementById("otherbal");
	divElement.innerHTML = formPage;
	divElement.style.display = "block";  


}

/* function confirmMsg(MSISDN,amount,password)
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
	var flag;
	//flag = isNAN(amount);
	
	if(MSISDN == "" || amount == "" || password=="")
	{
		str="Sorry,field(s) cannot be empty. Please enter valid input";

		divElement = document.getElementById("post2");
		divElement.innerHTML = str ;
		divElement.style.display = "block";
	}
	else if(amount>11111111 || k=="true")
	{
		
	var element = document.getElementById ("post2");
	element.style.display = "block";
	
	element.innerHTML = "Please Enter Valid Amount.";
	}
	else
	{
		
		
		str += "Mobile no: "+MSISDN+"<br/><hr\>,";
		str += "Amount: "+amount+"<br/>";

		divElement= document.getElementById("rechargeReq");
		divElement.innerHTML = str;
		divElement.style.display = "block";
		
	}
} */



function sendOtherBalReq(mobNumber,Pin)
{
	mobNumber 		= nullorUndefCheck(mobNumber);
	Pin 			= nullorUndefCheck(Pin);

	var str="";
	var postData = "";
	var divElement = "";
	var url = "";
	var cdrStr = "";


	url=PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;
	if(DEMO_FLAG == '1' || DEMO_FLAG == 1)
	{
		url= DEMO_URL;
	}

	widget.logWrite(7,"url for sendOtherBalReq request::"+url);
	if(SEND_ENCRYPTREQ)
	{
		postData = "TYPE="+OTHERBAL_TYPE+"&MSISDN="+mobile+"&Message="+getEncrypt("IMEI="+imei+"&PIN="+Pin+"&MSISDN2="+mobNumber+"&AMOUNT="+RechargeAmount+"&SELECTOR="+SELECTOR);

	}else
	{		
		postData = "TYPE="+OTHERBAL_TYPE+"&MSISDN="+mobile+"&IMEI="+imei+"&PIN="+Pin+"&MSISDN2="+mobNumber+"&LANGUAGE1="+langCode+"&LANGUAGE2="+langCode;
	}

	widget.logWrite(7," sendOtherBalReq postdata ::"+postData);

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
				widget.logWrite(7,"response for sendOtherBalReq::"+xmlText);
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
		cdrStr += changetimeformat()+"| OtherBalance";
		xmlHttp.send (postData) ;


	}

}
	



