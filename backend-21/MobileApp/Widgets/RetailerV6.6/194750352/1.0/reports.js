var xmlHttp = null ;
var USSD_BEARER_TYPE = 2;
var SMS_BEARER_TYPE = 3;
var widget = window.widget ;	
var bearer = widget.fetchBearerType () ;
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
var LASTTRANS_TYPE = widget.widgetProperty ("LAST_PLAIN_TYPE") ;
var LAST5TRANS_TYPE = widget.widgetProperty ("LAST5_PLAIN_TYPE") ;
var BALANCEENQ_TYPE = widget.widgetProperty ("BALENQ_PLAIN_TYPE") ;
var DAILYREPORT_TYPE = widget.widgetProperty ("DAILY_PLAIN_TYPE") ;
var LANGUAGE1 = widget.widgetProperty("LANGUAGE1");
var LANGUAGE2 = widget.widgetProperty("LANGUAGE2");
var url = "";

var presentLang=widget.fetchLanguage();
var langCode = getPresentLan(presentLang);
widget.logWrite(7,"language code ::::::" +langCode);
url= PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;
if(DEMO_FLAG == '1' || DEMO_FLAG == 1)
{
	url= DEMO_URL;
}


/*function reportsMenu()
{
	var formPage="",headPage="",reportMenu="";
	var divElement = "";
	var pin_str = "";pin_last5 ="",pin_stockBal="",pin_daily="";

	headPage += "<div id='reports' class='c3lMenuGroup'>";
	headPage += "<div width='100%' class='c3lMenuGroup topMessage'><span width='85%' align='left' >PreTUPS</span><a style='margin-right:20px' align='right' id= 'home' name='home' href='wgt:179878594/1.0/:displayMain()'><img class='listicon' resimg ='listicon.png' src='listicon.png'/></a></div><br/>";
	headPage += "<span width='100%' align='left' class='welcomeMsg'>Reports</span><br/>";
	
	if(LAST_5TRANS)
	{
		if(PIN_CHECK)
		{
			formPage += "<button id=\"trans\" name=\"trans\" width='75%' align=\"center\" class=\"buttonBgBlue\" resimg='buttonnew.png' onclick='#Last5Tran'>"+STR_LAST5TRANS+"</button><br/>";
			
		}else
		{
			formPage += "<button id=\"trans\" name=\"trans\" width='75%' align=\"center\" class=\"buttonBgBlue\" resimg='buttonnew.png' onclick=\"javascript:last5transac();\">"+STR_LAST5TRANS+"</button><br/>";
		}

	}
	if(STOCK_BAL)
	{
		if(PIN_CHECK)
		{
			formPage += "<button id=\"balance\" name=\"balance\" width='75%' align=\"center\" class=\"buttonBgBlue\" resimg='buttonnew.png' onclick='#stockBal'>"+STR_STOCKBAL+"</button><br/>";
		}else
		{
			formPage += "<button id=\"balance\" name=\"balance\" width='75%' align=\"center\" class=\"buttonBgBlue\" resimg='buttonnew.png' onclick=\"javascript:stockbalreq();\">"+STR_STOCKBAL+"</button><br/>";
		}
	}
	if(DAILY_REPORT)
	{
		if(PIN_CHECK)
		{
			formPage += "<button id=\"dailyreports\" name=\"dailyreports\" width='75%' align=\"center\" class=\"buttonBgBlue\" resimg='buttonnew.png' onclick='#Dailyreport'>"+STR_DAILYREPORT+"</button><br/>";	
		}else
		{
			formPage += "<button id=\"dailyreports\" name=\"dailyreports\" width='75%' align=\"center\" class=\"buttonBgBlue\" resimg='buttonnew.png' onclick=\"javascript:dailyreports();\">"+STR_DAILYREPORT+"</button><br/>";
		}
	}
	formPage += "</div>";
	
	pin_str += "<input  emptyok='false' class='inputBG'   align='center'  resimg='inputbg.9.png'  type='numpassword' id='pin' name='pin' title='"+STR_PIN_TITLE+"' value='' encrypt='true' maxLength='"+PIN_LEN+"'/><br/><br/>";
	pin_last5 += "<input type='button' name='submit' value='Submit' class='buttonBgBlue' resimg='buttonnew.png' align='center' onclick='javascript:last5transac($pin);'/>";
	pin_last5 += "</div>";
	
	pin_stockBal += "<input type='button' name='submit' value='Submit' class='buttonBgBlue' resimg='buttonnew.png' align='center' onclick='javascript:stockbalreq($pin);'/>";
	pin_stockBal += "</div>";
	
	pin_daily +="<input type='button' name='submit' value='Submit' class='buttonBgBlue' resimg='buttonnew.png' align='center' onclick='javascript:dailyreports($pin);'/>";
	pin_daily += "</div>";

	
	
	widget.savePermanent = SAVE_PERMINENT;
	
	// REPORTS MAIN MENU
	
	divElement= document.getElementById("Prereports");
	divElement.innerHTML = reportMenu;
	divElement.style.display = "block";
	
	//PIN FOR LAST 5TRANS
	divElement= document.getElementById("Last5Tran");
	divElement.innerHTML = headPage+pin_str+pin_last5+"<setvar name='pin' value=''/>";
	divElement.style.display = "block";  
	
	// PIN FOR STOCKBALANCE
	divElement= document.getElementById("stockBal");
	divElement.innerHTML = headPage+pin_str+pin_stockBal+"<setvar name='pin' value=''/>";
	divElement.style.display = "block";  
	
	// PIN FOR DAILY REPORT
	divElement= document.getElementById("Dailyreport");
	divElement.innerHTML = headPage+pin_str+pin_daily+"<setvar name='pin' value=''/>";
	divElement.style.display = "block"; 
}*/

function lasttrans(pin)
{
	
	var cdrStr = "";
	var str = "";
	var divElement = "";
	var postData = "";
	if( "" != nullorUndefCheck(pin))
	{
		pinNum = pin;
	}
	
	if(SEND_ENCRYPTREQ)
	{
		 postData = "TYPE="+LASTTRANS_TYPE+"&MSISDN="+mobile+"&Message="+getEncrypt("IMEI="+imei+"&PIN="+pinNum);
	}else
	{
		postData = "TYPE="+LASTTRANS_TYPE+"&MSISDN="+mobile+"&IMEI="+imei+"&PIN="+pinNum+"&LANGUAGE1="+langCode;
	}
	widget.logWrite(7,"postdata of lasttrans request::"+postData);
	if (null == xmlHttp) {
		xmlHttp = new XMLHttpRequest();
	}
	if (xmlHttp) {
		xmlHttp.onreadystatechange = function()
		{
			if (4 == xmlHttp.readyState && 200 == xmlHttp.status) {
				//var xmlDoc = xmlHttp.responseXML ;
				var xmlText = xmlHttp.responseText;
				widget.logWrite(7, "response for lasttrans::" + xmlText);

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
					if("rpretups" != widget.fetchGroupName())
					{
						resLast5Trans();
					}else
					{
						divElement= document.getElementById("post");
						divElement.title= STR_TITLE;
						divElement.innerHTML = txn_message;
						divElement.style.display = "block";  
					}
					
					
				} else {
					str = STR_SERVER_ERROR;
					divElement = document.getElementById("post");
					divElement.title= STR_TITLE;
					divElement.innerHTML = str;
					divElement.style.display = "block";
				}
			} else {
				str = STR_SERVICE_ERROR;
				if (bearer == USSD_BEARER_TYPE) {
					str += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
				}
				divElement = document.getElementById("post");
				divElement.title= STR_TITLE;
				divElement.innerHTML = str;
				divElement.style.display = "block";
			}



		};
		xmlHttp.open("POST", url, false);
		xmlHttp.setRequestHeader("Content-Type",SOURCE_TYPE);
		xmlHttp.setRequestHeader("Connection", "close");
		cdrStr += changetimeformat()+"| Last Transaction ";
		xmlHttp.send(postData);
	}


}


function last5transac(pin)
{
	
	var cdrStr = "";
	var str = "";
	var divElement = "";
	var postData = "";
	if( "" != nullorUndefCheck(pin))
	{
		pinNum = pin;
	}
	
	if(SEND_ENCRYPTREQ)
	{
		 postData = "TYPE="+LAST5TRANS_TYPE+"&MSISDN="+mobile+"&Message="+getEncrypt("IMEI="+imei+"&PIN="+pinNum);
	}else
	{
		postData = "TYPE="+LAST5TRANS_TYPE+"&MSISDN="+mobile+"&IMEI="+imei+"&PIN="+pinNum+"&LANGUAGE1="+langCode;
	}
	widget.logWrite(7,"postdata of last5trans request::"+postData);
	if (null == xmlHttp) {
		xmlHttp = new XMLHttpRequest();
	}
	if (xmlHttp) {
		xmlHttp.onreadystatechange = function()
		{
			if (4 == xmlHttp.readyState && 200 == xmlHttp.status) {
				//var xmlDoc = xmlHttp.responseXML ;
				var xmlText = xmlHttp.responseText;
				widget.logWrite(7, "response for last5transac::" + xmlText);

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
					if("rpretups" != widget.fetchGroupName())
					{
						resLast5Trans();
					}else
					{
						divElement= document.getElementById("post");
						divElement.title= STR_TITLE;
						divElement.innerHTML = txn_message;
						divElement.style.display = "block";  
					}
					
					
				} else {
					str = STR_SERVER_ERROR;
					divElement = document.getElementById("post");
					divElement.title= STR_TITLE;
					divElement.innerHTML = str;
					divElement.style.display = "block";
				}
			} else {
				str = STR_SERVICE_ERROR;
				if (bearer == USSD_BEARER_TYPE) {
					str += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
				}
				divElement = document.getElementById("post");
				divElement.title= STR_TITLE;
				divElement.innerHTML = str;
				divElement.style.display = "block";
			}



		};
		xmlHttp.open("POST", url, false);
		xmlHttp.setRequestHeader("Content-Type",SOURCE_TYPE);
		xmlHttp.setRequestHeader("Connection", "close");
		cdrStr += changetimeformat()+"| Last 5 Trans ";
		xmlHttp.send(postData);
	}


}
function stockbalreq(pin)
{
	
	if( "" != nullorUndefCheck(pin))
	{
		pinNum = pin;
	}
	
		
		var str = "";
		var cdrStr = "";
		var divElement = "";
		var postData = "";
		if(SEND_ENCRYPTREQ)
		{
			postData = "TYPE="+BALANCEENQ_TYPE+"&MSISDN="+mobile+"&Message="+getEncrypt("IMEI="+imei+"&PIN="+pinNum+"&LANGUAGE1="+LANGUAGE1);
		}else
		{
			postData = "TYPE="+BALANCEENQ_TYPE+"&MSISDN="+mobile+"&IMEI="+imei+"&PIN="+pinNum+"&LANGUAGE1="+langCode;
		}
		widget.logWrite(7,"postData stockbalreq request"+postData);
		widget.logWrite(7,"stockbalreq url:"+url);
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
					widget.logWrite(7," response for stockbalreq ::"+xmlText);
					if (xmlText != null && !xmlText.indexOf("null") > -1)
					{
						var  txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
						var  txn_message		 = responseStr(xmlText, STR_TXNMESSAGE);
						cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
						//widget.writeCDR (1, cdrStr) ;
						widget.logWrite(7,"cdrstr logs.."+cdrStr);
						cdrcommon(cdrStr);

						if(bearer == USSD_BEARER_TYPE)
						{
							txn_message += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
						}

						divElement= document.getElementById("post");
						divElement.title=STR_TITLE;
						divElement.innerHTML = txn_message;
						divElement.style.display = "block";  


					}else
					{
						str = STR_SERVER_ERROR;
						divElement= document.getElementById("post");
						divElement.title= STR_TITLE;
						divElement.innerHTML = str;
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
					divElement.innerHTML = str;
					divElement.style.display = "block";

				}

			};
			xmlHttp.open ("POST", url , false) ;
			xmlHttp.setRequestHeader("Content-Type",SOURCE_TYPE);
			xmlHttp.setRequestHeader("Connection", "close");
			cdrStr += changetimeformat()+"| stockBalance ";
			xmlHttp.send (postData) ;
		}
}


function dailyreports(pin)
{
	if( "" != nullorUndefCheck(pin))
	{
		pinNum = pin;
	}
	
	var str = "";
	var cdrStr = "";
	var divElement = "";
	var postData  = "";
	if(SEND_ENCRYPTREQ)
	{
		postData = "TYPE="+DAILYREPORT_TYPE+"&MSISDN="+mobile+"&Message="+getEncrypt("IMEI="+imei+"&PIN="+pinNum);
	}else
	{
		postData = "TYPE="+DAILYREPORT_TYPE+"&MSISDN="+mobile+"&IMEI="+imei+"&PIN="+pinNum+"&LANGUAGE1="+langCode;
	}
	widget.logWrite(7,"postData dailyreports request::"+postData);
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
				widget.logWrite(7,"response for dailyreports ::"+xmlText);
				
				if (xmlText != null && !xmlText.indexOf("null") > -1)
				{
					var  txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
					var  txn_message		 = responseStr(xmlText, STR_TXNMESSAGE);
					cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
					//widget.writeCDR (1, cdrStr) ;
					widget.logWrite(7,"cdrstr logs.."+cdrStr);
					cdrcommon(cdrStr);

					if(bearer == USSD_BEARER_TYPE)
					{
						txn_message += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
					}
					if(txn_status == STR_SUCCESS)
					if("rpretups" == widget.fetchGroupName())
					{
						resDaily(txn_message);
					}else
					{
						divElement= document.getElementById("post");
						divElement.title= STR_TITLE;
						divElement.innerHTML = txn_message;
						divElement.style.display = "block";  
						
					}
					
					
				}else
				{
					str = STR_SERVER_ERROR;
					divElement= document.getElementById("post");
					divElement.title= STR_TITLE;
					divElement.innerHTML = str;
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
				divElement.innerHTML = str;
				divElement.style.display = "block";  
			}

			
			
		};
		xmlHttp.open ("POST", url , false) ;
		xmlHttp.setRequestHeader("Content-Type",SOURCE_TYPE);
		xmlHttp.setRequestHeader("Connection", "close");
		cdrStr += changetimeformat()+"| daily Report ";
		xmlHttp.send (postData) ;
	}
}

function resLast5Trans()
{

	var str = "";
	var details = "";
	var txn = "",mobile="",date= "",status="",type="",amount="";
	
	var  last5details="TxnID:P140902.1005.100001,MSISDN:8123123645,Date:02/09/14 10:05:35,Status:SUCCESS,Type:C2S,Amount:10;TxnID:R140901.1429.100001,MSISDN:7212345678,01/09/14 14:29:27,Status:SUCCESS,Type:C2S,Amount:20;TxnID:P140902.1005.100001,MSISDN:8123123645,Date:02/09/14 10:05:35,Status:SUCCESS,Type:C2S,Amount:10;TxnID:R140901.1429.100001,MSISDN:7212345678,Date:01/09/14 14:29:27,Status:SUCCESS,Type:C2S,Amount:20;TxnID:R140901.1429.100001,MSISDN:7212345678,Date:01/09/14 14:29:27,Status:SUCCESS,Type:C2S,Amount:20";
	last5details = last5details.split(";");
	str += "<span class='details'>Last 5 Transactions</span><hr/>";
	for (var i=0;i<last5details.length;i++)
	{
		details = last5details[i].split(",");
		txn = details[0].split(":");
		mobile = details[1].split(":");
		date = details[2].replace("Date:","");
		widget.logWrite(7,"dateformat of last 5trans:"+date);
		status = details[3].split(":");
		widget.logWrite(7,"dateformat of last 5trans status:"+status);
		//type = details[4].split(":");
		amount = details[5].split(":");
		
		str += "<span class='c3lMenuGroup' style='margin-left:15;color:rgb(94,94,96);'>"+Number(i+1)+":Transaction Id : <span style='font-weight:bold;color:rgb(94,94,96);'>"+txn[1]+"</span></span>";
		str += "<div class='c3lMenuGroup confirmreportpage' >";
		//str += "<hr style='color:grey;font-size:4px' />";
		str += "<span  class='td1'>MobileNo :</span>";
		str += "<span  class='td2'>"+mobile[1]+"</span><br/>";

		str += "<span  class='td1'>Amount :</span>";
		str +="<span  class='td2'>Rs."+amount[1]+"</span>";

		str += "<span  class='td1'>Date :</span>";
		str +="<span  class='td2'>"+date+"</span>";

		//str += "<span  class='td1'>Type :</span>";
		//str +="<span  class='td2'>"+type[1]+"</span>";

		str += "<span  class='td1'>Status :</span>";
		str +="<span  class='td2'>"+status[1]+"</span>";

		//str += "<hr style='color:grey;font-size:4px' />";
		str += "</div>";
	}   	
	var  divElement= document.getElementById("popUp");
	divElement.innerHTML = str;
	divElement.style.display = "block";

}
function resDaily(respmsg)
{

	var str = "";
	var eTopUP_In = "";
	var PeTopUP_In = "";
	var eTopUP_Out = "";
	var PeTopUP_Out = "";
	var eTopUP_transfer_Out = "";
	var PeTopUP_transfer_Out = "";
	var serviceName = "";
	
	
	//respmsg = "Daily transfer product:transfer:returns in:eTopUP:0:0, PeTopUP:0:0, out:eTopUP:0:0, PeTopUP:0:0, subscriber transfer out:Customer Recharge:eTopUP:0, Customer Recharge:PeTopUP:0,Bill Payment:eTopUP:0, Bill Payment:PeTopUP:0";
	respmsg= respmsg.split(",");
	
	if("" != nullorUndefCheck(respmsg[0]))
	{
		eTopUP_In = respmsg[0].substring(respmsg[0].lastIndexOf("eTopUP")+7,respmsg[0].length);
	}
	
	if("" != nullorUndefCheck(respmsg[1]))
	{
		PeTopUP_In = respmsg[1].substring(respmsg[1].lastIndexOf("PeTopUP")+8,respmsg[1].length);
	}
	
	if("" != nullorUndefCheck(respmsg[2]))
	{
		eTopUP_Out = respmsg[2].substring(respmsg[2].lastIndexOf("eTopUP")+7,respmsg[2].length);
	}
	
	if("" != nullorUndefCheck(respmsg[3]))
	{
		PeTopUP_Out = respmsg[3].substring(respmsg[3].lastIndexOf("PeTopUP")+8,respmsg[3].length);
	}
	
	widget.logWrite(7,"eTopUP_In - "+eTopUP_In);
	widget.logWrite(7,"PeTopUP_In - "+PeTopUP_In);
	widget.logWrite(7,"eTopUP_Out - "+eTopUP_Out);
	widget.logWrite(7,"PeTopUP_Out - "+PeTopUP_Out);
	widget.logWrite(7,"respmsg length - "+respmsg.length);
	
	
	/*widget.logWrite(7,"eTopUP_In - "+eTopUP_In);
	widget.logWrite(7,"eTopUP_In - "+eTopUP_In);
	widget.logWrite(7,"eTopUP_In - "+eTopUP_In);*/
	
	/*log.Write(7,"In eTopUp-"+strArray[0].substring(strArray[0].lastIndexOf("eTopUP")+7,strArray[0].length()));
	log.Write(7,"In out PeTopUP-"+strArray[1].substring(strArray[1].lastIndexOf("PeTopUP")+7,strArray[1].length()));
	log.Write(7,"In out eTopup-"+strArray[2].substring(strArray[2].lastIndexOf("eTopUP")+7,strArray[2].length()));
	log.Write(7,"In out eTopUP-"+strArray[3].substring(strArray[3].lastIndexOf("eTopUP")+7,strArray[3].length()));
	log.Write(7,"ServiceName:"+strArray[4].split(":")[1]);
	log.Write(7,"In out eTopup-"+strArray[2].substring(strArray[2].lastIndexOf("eTopUP")+7,strArray[2].length()));
	log.Write(7,"In out eTopUP-"+strArray[3].substring(strArray[3].lastIndexOf("eTopUP")+7,strArray[3].length()));*/
	//var details = "Postpaid Bill Payment-TransferIn:Rs 100,Return:Rs 100;Prepaid Recharge-TransferIn:Rs 100,Return:Rs 100";
	
	str += "<span class='details'>"+DAILY_REPORT_TITLE+"</span><hr/>";
	str += "<div class='c3lMenuGroup' style='padding:0 15 0 15'><span style='color:rgb(94,94,96);font-weight:bold'>"+STOCK_REPORT_TITLE+"</span><hr style='color:grey'/></div>";
	str += "<span style='margin-left:15;color:rgb(94,94,96);'>eTopUP</span>";
		str += "<div class='c3lMenuGroup confirmreportpage' >";
		str += "<span  class='td1'>Transfer :</span><span  class='td2'>"+eTopUP_In+"</span>";
		str += "<span  class='td1'>Returns :</span><span  class='td2'>"+eTopUP_Out+"</span>";
		//str += "<hr style='color:grey;font-size:4px' />";
		str += "</div>";
		str += "<span style='margin-left:15;color:rgb(94,94,96);'>PeTopUP</span>";
		str += "<div class='c3lMenuGroup confirmreportpage' >";
		str += "<span  class='td1'>Transfer :</span><span  class='td2'>"+PeTopUP_In+"</span>";
		str += "<span  class='td1'>Returns :</span><span  class='td2'>"+PeTopUP_Out+"</span>";
	//	str += "<hr style='color:grey;font-size:4px' />";
		str += "</div>";
		
	str += "<div class='c3lMenuGroup' style='padding:0 15 0 15'><span style='color:rgb(94,94,96);font-weight:bold'>"+CUSTOMER_TRANSOUT+"</span><hr style='color:grey'/></div>";
	
	for(var i=4;i< respmsg.length;i++)
	{
		if("" != nullorUndefCheck(respmsg[i]))
		{
			eTopUP_transfer_Out = respmsg[i].substring(respmsg[i].lastIndexOf("eTopUP")+7,respmsg[i].length);
		}
		if("" != nullorUndefCheck(respmsg[i+1]))
		{	
			serviceName =  respmsg[i+1].split(":")[0];
			PeTopUP_transfer_Out = respmsg[i+1].substring(respmsg[i+1].lastIndexOf("PeTopUP")+8,respmsg[i+1].length);
		}
		widget.logWrite(7,"eTopUP_transfer_Out - "+eTopUP_Out);
		widget.logWrite(7,"PeTopUP_transfer_Out - "+PeTopUP_Out);
		widget.logWrite(7,"serviceName - "+serviceName);
		
		str += "<span style='margin-left:15;color:rgb(94,94,96);'>"+serviceName+"</span>";
		str += "<div class='c3lMenuGroup confirmreportpage'>";
		str += "<span  class='td1'> eTopUP :</span><span  class='td2'>"+eTopUP_transfer_Out+"</span>";
		str += "<span  class='td1'>PeTopUP :</span><span  class='td2'>"+PeTopUP_transfer_Out+"</span>";
		//str += "<hr style='color:grey;font-size:4px' />";
		str += "</div>";
		
		i++;
	}		
		
	
	
	/*str += "<span style='margin-left:15;color:rgb(94,94,96);'>Prepaid Recharge</span>";
	str += "<div class='c3lMenuGroup confirmreportpage' >";
	str += "<span  class='td1'>Transfer In :</span><span  class='td2'>Rs.100</span>";
	str += "<span  class='td1'>Return :</span><span  class='td2'>Rs.100</span>";
	str += "<hr style='color:grey;font-size:4px' />";
	str += "</div>";*/

	
		
	 	
	var  divElement= document.getElementById("popUp");
	divElement.innerHTML = str;
	divElement.style.display = "block";

}
