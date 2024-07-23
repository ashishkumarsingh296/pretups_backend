var xmlHttp = null ;
var USSD_BEARER_TYPE = 2;
var SMS_BEARER_TYPE = 3;
var widget = window.widget ;	
var bearer = widget.fetchBearerType () ;
var mobile = widget.retrieveWidgetUserData(179878594,"launchMSISDN") ;
var imei = widget.retrieveWidgetUserData(179878594,"IMEI") ;

var PreTUPSSERVER = widget.widgetProperty ("PreTUPSSERVER") ;
var LOGIN = widget.widgetProperty ("LOGIN") ;
var PASSWORD = widget.widgetProperty ("PASSWORD") ;
var REQUEST_GATEWAY_CODE = widget.widgetProperty ("REQUEST_GATEWAY_CODE") ;
var REQUEST_GATEWAY_TYPE = widget.widgetProperty ("REQUEST_GATEWAY_TYPE") ;
var SERVICE_PORT = widget.widgetProperty ("SERVICE_PORT") ;
var SOURCE_TYPE = widget.widgetProperty ("SOURCE_TYPE") ;
var TYPE = widget.widgetProperty ("TYPE") ;
var BILLPAY_TYPE = widget.widgetProperty ("PALINREQ_TYPE") ;
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

var presentLang=widget.fetchLanguage();
var langCode = getPresentLan(presentLang);
widget.logWrite(7,"language code ::::::" +langCode);


function sendbillpayreq(mobNumber,Amount,Pin)
{	
	mobNumber 		= validateMSISDN(nullorUndefCheck(mobNumber));
	Amount 			= nullorUndefCheck(Amount);
	Pin 			= nullorUndefCheck(Pin);

	if(SENDREQUEST_XML)
	{
		sendbillpayreqXML(mobNumber,Amount,Pin);
	}else
	{
	
	
	var str="";
	var divElement;
	var cdrStr = "";
	var url="";
	var postData = "";
	Amount = Number(Amount);
	var k = isNaN(Amount) ;
	k = k.toString(); 
	widget.logWrite(6,"number value"+Amount);
	widget.logWrite(6,"number value1"+k);
	
	if(bearer != SMS_BEARER_TYPE && (mobNumber == "" || Amount == "" || Pin==""))
	{
		str="Sorry,field(s) cannot be empty. Please enter valid input";

		divElement = document.getElementById("post2");
		divElement.innerHTML = str ;
		divElement.style.display = "block";
	}else if(bearer != SMS_BEARER_TYPE && (Amount >11111111 || k=="true"))
	{

		divElement = document.getElementById ("post2");
		divElement.style.display = "block";
		divElement.innerHTML = "Please Enter Valid Amount.";
	}
	else
	{
		url= PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;
		if(DEMO_FLAG == '1' || DEMO_FLAG == 1)
		{
			url= DEMO_URL;
		}
		widget.logWrite(7,"url of sendbillpayreq request::"+url);
		
		if(SEND_ENCRYPTREQ)
		{
			postData ="TYPE="+BILLPAY_TYPE+"&MSISDN="+mobile+"&Message="+getEncrypt("IMEI="+imei+"&PIN="+Pin+"&MSISDN2="+mobNumber+"&AMOUNT="+Amount+"&SELECTOR="+SELECTOR+"&LANGUAGE1="+langCode);
			
		}else
		{
			postData ="TYPE="+BILLPAY_TYPE+"&MSISDN="+mobile+"&IMEI="+imei+"&PIN="+Pin+"&MSISDN2="+mobNumber+"&AMOUNT="+Amount+"&SELECTOR="+SELECTOR+"&LANGUAGE1="+langCode;
		}
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
						
						widget.logWrite(7,"cdrstr logs.."+cdrStr);
						cdrcommon(cdrStr);

						if (bearer == USSD_BEARER_TYPE)
						{
							str += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
						}

						divElement= document.getElementById("post");
						divElement.title= STR_TITLE;
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
					divElement.innerHTML = str+"<setvar name='mobilenu' value=''/><setvar name='amount' value=''/><setvar name='pin' value=''/>";
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
}

function sendbillpayreqXML(mobNumber,Amount,Pin)
{
        var str="";
        var divElement;
        Amount = Number(Amount);
        var k = isNaN(Amount) ;
        k = k.toString();
        widget.logWrite(6,"number value"+Amount);
        widget.logWrite(6,"number value1"+k);
        /* widget.storeUserData("MobNo", MSISDN) ;
        widget.storeUserData("Amount", amount) ;
        widget.storeUserData("Password", password) ; */


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

                element.innerHTML = "Please Enter Valid Amount."+"<setvar name='mobilenu' value=''/><setvar name='amount' value=''/><setvar name='pin' value=''/>";
        }
        else
        {
        	              var date = changetimeformat();
                
                        var url="" + PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT+"";
                        widget.logWrite(7,"xml format url:0"+url);
                        var xmldata = [ "<?xml version=\"1.0\"?>",
                                        "<COMMAND>",
                                        "<TYPE>" + TYPE + "</TYPE>",
                                        "<DATE>" + date + "</DATE>",
                                        "<EXTNWCODE>" + EXTNWCODE + "</EXTNWCODE>",
                                        "<MSISDN>" + mobile + "</MSISDN>",
                                        "<PIN>" + Pin + "</PIN>",
                          
                                        "<LOGINID>" + LOGINID + "</LOGINID>",
                                        "<PASSWORD>" + PASSWORDXML + "</PASSWORD>",
                                        "<EXTCODE></EXTCODE>",
                                        "<EXTREFNUM></EXTREFNUM>",
                                        "<MSISDN2>" + mobNumber + "</MSISDN2>",
                                        "<AMOUNT>" + Amount + "</AMOUNT>",
                                        "<LANGUAGE1>" + LANGUAGE1 + "</LANGUAGE1>",
                                        "<LANGUAGE2>" + LANGUAGE2 + "</LANGUAGE2>",
                                        "<SELECTOR>" + SELECTOR + "</SELECTOR>",
                                        "</COMMAND>"].join("");

                        widget.logWrite(7,"xml format request"+xmldata);
                        if (null == xmlHttp)
                        {
                                xmlHttp = new XMLHttpRequest () ;
                        }
                        if (xmlHttp)
                        {
                                xmlHttp.onreadystatechange = billpayReq;
                                xmlHttp.open ("POST", url , false) ;
                                xmlHttp.setRequestHeader("Content-Type", "xml");
                                xmlHttp.setRequestHeader("Connection", "close");
                                xmlHttp.send (xmldata) ;
                        }
                }
        }
function billpayReq()
{
        if (4 == xmlHttp.readyState &&  200 == xmlHttp.status)
        {
                var xmlText = xmlHttp.responseText ;
                widget.logWrite(7,"xml response for billpayment"+xmlText);
                if (xmlText)
                {
                        parseBillpayment(xmlText) ;
                }
        }else
        {

               
                var str;
                var divElement;
                str = "Service Unavailable";

                divElement= document.getElementById("post");
                divElement.innerHTML = str+"<setvar name='mobilenu' value=''/><setvar name='amount' value=''/><setvar name='pin' value=''/>";
                divElement.style.display = "block";

        }

}

     
function parseBillpayment(xmlDoc)
{
        var divElement;
        var str="";
        var rootele = document.createElement ("root") ;
        rootele.innerHTML = xmlDoc ;

        var billpayInfo = rootele.getElementsByTagName("COMMAND") ;
/*      if (0 == rechargeInfo.length)
        {
                str += "No information";
        }
        else
        {*/
                //var type =  rechargeInfo[0].getElementsByTagName("TYPE")[0].textContent;
                //var tran_Status =  rechargeInfo[0].getElementsByTagName("TXNSTATUS")[0].textContent;
                //var date =  rechargeInfo[0].getElementsByTagName("DATE")[0].textContent;
                //var unque_no =  rechargeInfo[0].getElementsByTagName("EXTREFNUM")[0].textContent;
        //var tran_Status =  rechargeInfo[0].getElementsByTagName("TXNSTATUS")[0].textContent;
        //var date =  rechargeInfo[0].getElementsByTagName("DATE")[0].textContent;
        //var unque_no =  rechargeInfo[0].getElementsByTagName("EXTREFNUM")[0].textContent;
        //var tran_ID =  rechargeInfo[0].getElementsByTagName("TXNID")[0].textContent;
        var tran_Msg =  billpayInfo[0].getElementsByTagName("MESSAGE")[0].textContent;
//}
/*      if(tran_Status==200)
                {
                        rechargeStatus();
                }
        else
                {
        */
        str += tran_Msg;
        if (bearer == USSD_BEARER_TYPE)
                {
                        str += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
                }
        //}
divElement= document.getElementById("post");
divElement.innerHTML = str+"<setvar name='mobilenu' value=''/><setvar name='amount' value=''/><setvar name='pin' value=''/>";
divElement.style.display = "block";

}
           