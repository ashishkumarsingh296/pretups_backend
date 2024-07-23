var xmlHttp = null ;
var USSD_BEARER_TYPE = 2;
var SMS_BEARER_TYPE = 3;
var widget = window.widget ;	
var bearer = widget.fetchBearerType ();
var groupName = widget.fetchGroupName();

function checkonLaunch()
{	

	var pin = widget.retrieveUserData("launchPIN") ;
	var msisdn = widget.retrieveUserData("launchMSISDN") ;
	var flag = widget.retrieveUserData("flag");
	widget.logWrite(7,"pin::"+pin);
	widget.logWrite(7,"msisdn::"+msisdn);
	widget.logWrite(7,"flag::"+flag);
	
	if(flag == true || flag == "true")
	{
		widget.logWrite(6,"rechargeMenu if msisdn*****"+msisdn);
		if(groupName == "rpretups")
		{
			window.location = "wgt:979446865/1.0:rechargeMenu()";
		}else
		{
			window.location = "wgt:237453940/1.0:rechargeMenu()";
		}
	}
	else
	{
		login();
	}
	
}
function logout()
{
	widget.clearUserData("launchPIN");
	widget.clearUserData("launchMSISDN");
	widget.clearUserData("flag");
	widget.clearCachedRequest("wgt:979446865/1.0/recharge.html");
	login();
}

function login()
{
	var str="";
	//var backImgWidth = backTopTitle();
	if (bearer == USSD_BEARER_TYPE)
	{
		//var mobileNo = "7799999" ;
		//widget.logWrite(7,"mobile no ussd"+mobileNo);
		str += "<span>Register</span><br/>Enter PIN<br/>";
		str += "<div id='main' class=''>";
		str += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:verify("+mobileNo+",$pin)\">";
		str += "<input emptyok='false' type='password' id='pin' name='pin' title='PIN' value=''/>";
		str += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
		str += "</form></div>";
	}
	else
	{
		 if(!groupName == "rpretups")
		{
			str += "<div id='login' class='c3lMenuGroup'>";
			str += "<span width='100%' align='left' class='topMessage'>PreTUPS</span><br/>";
			str += "<span width='100%' align='left' class='welcomeMsg'>Login</span><br/>";
			str += "<form id='formid' name='form1' onsubmit='verify($mobilenu,$pin,$%IMEI%);'>";
			str += "<input type='numeric' id='mobilenu' class='inputTitle' emptyok='false' class='inputBG' resimg='inputbg.9.png' align='center'    width='80%'  maxlength='15' name='mobilenu' value='' title='Enter MSISDN' encrypt='true' /><setvar name=\"mobilenu\" value=\"\" maxLength='"+MSISDN_LENGTH+"'/>";
			str += "<input class='inputTitle' emptyok='false' class='inputBG' resimg='inputbg.9.png'  align='center'    width='80%'  type='numpassword' id='pin' name='pin' title='PIN' encrypt='true' value=''/><setvar name=\"pin\" value=\"\"  maxLength='"+PIN_LENGTH+"'/><br/>";
			str += "<br/>";
			str += "<input type='submit' name='submit' value='Submit' resimg='buttonnew.png' class='buttonBgBlue'  resimg='buttonnew.png' align='center'/>" ;
			str = [str,"<specialcache name='Menu_Recharge' url='wgt:979446865/1.0:rechargeMenu()' type='screen'/>"].join("");
			str += "</form></div>" ;
			divElement = document.getElementById("login");
		}else
		{
			if(widget.clientVersion.indexOf("J2ME") != -1)
			{
				str += "<div width='100%' class='c3lMenuGroup'><span class='topSize' align='center' >"+STR_PRETUPS_TITLE+"</span></div><hr/>";
			}else
			{
				str += "<div width='100%' class='c3lTitle marginBottom20'><img width='50%' align='center' resimg='pretups_logo.png' src='pretups_logo.png' /><hr/></div>";	
			}
			str += "<input  id='mobilenu' name='mobilenu' type='numeric' maxLength='" + MSISDN_LENGTH + "' class='inputBg2' emptyok='false' title='Email or Mobile'/>";
			str += "<input id='pin' name='pin'  type='numpassword' class='inputBg2' maxLength='" + PIN_LENGTH + "' emptyok='false' title='PreTUPS PIN' encrypt='true' alt='PIN should be 4 digits'/>";
			str += "<a id='rechbut' class='c3lMenuGroup buttonBg' 	href=\"verify($mobilenu,$pin,$%IMEI%);\" althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$'	><span class='buttonText' align='center'>" + STR_LOGIN + "</span></a>";
			str +="<a align='center' width='50%' href='#forgot' style='font-size:10px;color:red;padding:10% 10% 10% 10%'>"+STR_FORGOTPIN+"?</a>";
			divElement = document.getElementById("loginv1");
		}
		
		
	}
	
			divElement.innerHTML = str;
			divElement.style.display = "block";
			
			var forgot= "";
				if(widget.clientVersion.indexOf("J2ME") != -1)
				{
					forgot += "<div width='100%' class='c3lMenuGroup'><span class='topSize'  align='center' >"+STR_FORGOTPIN+"</span></div><hr/>";
				}else
				{
					forgot +="<div   class='c3lTitle marginBottom20'><a class='c3lMenuGroup' href='close:'><img width='15%' align='left' resimg='icon_back.9.png' src='icon_back.9.png'/></a><span  class='topText' id='paytitle' valign='middle'>"+STR_FORGOTPIN+"</span><hr/></div>";
				}
				
				
			forgot +="<span  style='margin:0% 17% 20% 20%;color:rgb(94,94,96)'>"+STR_FORGOTPIN_CONFIRM+"</span>";
			forgot += "<input  id='mobilenu' name='mobilenu' type='numeric' maxLength='" + MSISDN_LENGTH + "' class='inputBg2' emptyok='false' title='"+STR_MSISDN_TT+"'  />";
			forgot += "<a class='c3lMenuGroup buttonBg' href=\"verify($mobilenu,$pin,$%IMEI%);\" althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$'	><span class='buttonText' align='center'>"+STR_PROCEED+"</span></a>";
			
			document.getElementById("forgot").innerHTML= forgot;
			document.getElementById("forgot").style.display = "block";

}

function termsText()
{
	var rawFile = new XMLHttpRequest();
    rawFile.open("GET", "terms.txt", false);
    

   /* rawFile.onreadystatechange = function ()
    {
        if(rawFile.readyState === 4)
        {
            if(rawFile.status === 200 || rawFile.status == 0)
            {
                allText = rawFile.responseText;
            }
        }
    }*/
    rawFile.send(null);
    return rawFile.responseText;;
    
}

function verify(msisdn,password,imei)
{
	var str = "";
	var divElement = "";
	if (bearer == USSD_BEARER_TYPE)
	{
		if(password == 2468)
		{
			widget.storeUserData("launchPIN",password) ;
			//displayMain();
			window.location = "wgt:979446865/1.0:rechargeMenu()";

		}
		else
		{
			str="Invalid PIN";	
			str += "<span>Enter Correct PIN</span>";
			str += "<div id='main' class='c3lMenuGroup'>";
			str += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:verify("+msisdn+",$pin)\">";
			str += "<input emptyok='false' type='password' id='pin' name='pin' title='PIN' value=''/>";
			str += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
			str += "</form></div>";
		}
		divElement = document.getElementById("post1");
		divElement.innerHTML = str ;
		divElement.style.display = "block";
	}
	else
	{
		widget.logWrite(7,"inside main");
		if(msisdn == DEMO_MSISDN && password == DEMO_PIN)
		{
			widget.logWrite(7,"inside main2");
			widget.storeUserData("launchPIN",password);
			widget.storeUserData("launchMSISDN",msisdn);
			if(DEMO_FLAG == 1 || DEMO_FLAG == "1")
			{
				widget.storeUserData("IMEI",DEMO_IMEI);
			}
			widget.storeUserData("IMEI",imei);
			widget.storeUserData("flag",'true');
			
			
			if(groupName == "rpretups")
			{
				window.location = "wgt:979446865/1.0:rechargeMenu()";
                            

			}else
			{
				window.location = "wgt:237453940/1.0:rechargeMenu()";
                           
			}
			
			//loginCheck(msisdn,password,imei);
		}else
		{
			str = "please enter RegisterMobileNo";
			divElement= document.getElementById("post1");
			divElement.title = STR_TITLE;
			divElement.innerHTML = str;
			divElement.style.display = "block";
		}
		
	}

}
function displayMain()
{
	var divElement;
	var str = "";
	
	if (bearer == USSD_BEARER_TYPE)
	{
		
		str += "<a id='recharge' href=\"wgt:213344215/1.0\">1. Recharge</a>";
		
		if(TRANSFER)
		{
			str += "<a id='transfer' href=\"wgt:203139478/1.0\">1. Transfer</a>";
		}
		if(BILLPAYMENT)
		{
		str += "<a id='billpay'  href=\"wgt:192775972/1.0\">2. Bill Payment</a>";
		}
		if(RETURNSTOCK)
		{
		str += "<a id='retstock' href=\"wgt:925464104/1.0\">3. Return Stock</a>";
		}
		if(REPORTS)
		{
		str += "<a id='reports'  href=\"wgt:261266208/1.0\">4. Reports</a>";
		}
		if(GIFTRECHARGE)
		{
		str += "<a id='gift' href=\"wgt:929959377/1.0\">5. Gift Recharge</a>";
		}
		str += "<a id='changepin' href=\"wgt:993256165/1.0\">6. Change PIN</a>";
		
		widget.savePermanent = SAVE_PERMINENT;
		divElement = document.getElementById("Menu");
		divElement.innerHTML = str ;
		divElement.style.display = "block";
	}
	else
	{
		str += "<div id='mainMenu' class='c3lMenuGroup'>"; 
		str += "<span width='100%' align='left' class='topMessage'>PreTUPS</span><br/>";
							
		
		
		str += "<a href=\"#Menu_Recharge\" id=\"recharge\" class =\"c3lMenuGroup options\" width=\"100%\"><img name=\"img\" valign='middle' align=\"left\" resimg='recharge.png' src=\"recharge.png\" /><span class='textalign' valign='middle'>Recharge</span><img name=\"img\" valign='middle' align=\"right\" resimg='rightarr.png' src=\"rightarr.png\" /></a><br/>";
		
		if(TRANSFER)
		{
		str += "<a href=\"#transfer\"  class =\"c3lMenuGroup options\" width=\"100%\"><img name=\"img\" valign='middle' align=\"left\"  resimg='returnstock.png' src=\"returnstock.png\" /><span class='textalign' valign='middle'>Transfer</span><img name=\"img\" valign='middle' align=\"right\" resimg='rightarr.png' src=\"rightarr.png\" /></a><br/>";
		}
		if(BILLPAYMENT)
		{
		str += "<a href=\"#billpay\"  class =\"c3lMenuGroup options\" width=\"100%\"><img name=\"img\" valign='middle' align=\"lefts\" resimg='billpayment.png' src=\"billpayment.png\" /><span class='textalign' valign='middle'>Bill Payment</span><img name=\"img\" valign='middle' align=\"right\" resimg='rightarr.png' src=\"rightarr.png\" /></a><br/>";
		}
		if(RETURNSTOCK)
		{
		str += "<a href=\"#returnstock\"  class =\"c3lMenuGroup options\" width=\"100%\"><img name=\"img\" valign='middle' align=\"left\" resimg=\"returnstock.png\" src=\"returnstock.png\" /><span class='textalign' valign='middle'>Return Stock</span><img name=\"img\" valign='middle' align=\"right\" resimg='rightarr.png' src=\"rightarr.png\" /></a><br/>";
		}
		if(REPORTS)
		{
		str += "<a href=\"#reports\" class =\"c3lMenuGroup options\" width=\"100%\"><img name=\"img\" valign='middle' align=\"left\" resimg=\"reports.png\" src=\"reports.png\" /><span class='textalign' valign='middle'>Reports</span><img name=\"img\" valign='middle' align=\"right\" resimg='rightarr.png' src=\"rightarr.png\" /></a><br/>";
		}
		if(GIFTRECHARGE)
		{
		str += "<a href=\"#giftRecharge\"  class =\"c3lMenuGroup options\" width=\"100%\"><img name=\"img\" valign='middle' align=\"left\" resimg=\"giftrecharge.png\"  src=\"giftrecharge.png\" /><span class='textalign' valign='middle'>Gift Recharge</span><img name=\"img\" valign='middle' align=\"right\" resimg='rightarr.png' src=\"rightarr.png\" /></a><br/>";
		}
		if(ELECVOUCHER)
		{
		str += "<a href='#elecvocher'  class =\"c3lMenuGroup options\" width=\"100%\"><img name=\"img\" valign='middle' align=\"left\" resimg=\"settings.png\" src=\"settings.png\" /><span class='textalign' valign='middle'>Electronic Voucher</span><img name=\"img\" valign='middle' align=\"right\" resimg='rightarr.png' src=\"rightarr.png\" /></a>";
		
		}
		str += "<a href=\"#changepin\"  class =\"c3lMenuGroup options\" width=\"100%\"><img name=\"img\" valign='middle' align=\"left\" resimg=\"settings.png\" src=\"settings.png\" /><span class='textalign' valign='middle'>Change PIN</span><img name=\"img\" valign='middle' align=\"right\" resimg='rightarr.png' src=\"rightarr.png\" /></a>";
		str +="</div>";

		return str;
	}
	
	
	
	
}


function electronicVoucher()
{
 	var formPage="";
	var divElement;
	var textmsg = "Your recharge amount is:"
	formPage += "<div id='recharge' class='c3lMenuGroup'>";
	
	formPage += "<div width='100%' class='c3lMenuGroup topMessage'><span width='85%' align='left' >PreTUPS</span><a style='margin-right:20px' align='right' id= 'home' name='home' href='wgt:179878594/1.0/:displayMain()'><img class='listicon' resimg ='listicon.png' src='listicon.png' /></a></div><br/>";
	formPage += "<span width='100%' align='left' class='welcomeMsg'>Recharges</span><br/>";
	
	//formPage += "<img name=\"img\" valign='middle' align=\"center\" src=\"pretups_image.png\" /><br/>";
	formPage += "<form id='formid' name='form1' onsubmit='elevocreq()'>";
	formPage += "<input type='numeric' id='mobilenu' class='inputTitle' emptyok='false' class='inputBG' align='center' width='80%'  maxlength='15' name='mobilenu' value='' title='Enter MSISDN'/><setvar name=\"mobilenu\" value=\"\"/>";
	formPage += "<input type='decimal' id='amount' class='inputTitle' emptyok='false' align='center' class='inputBG' name='amount' value='' width='80%'  title='Enter Amount '/><setvar name=\"amount\" value=\"\"/>";
	formPage += "<input class='inputTitle' emptyok='false' class='inputBG'   align='center'    width='80%'  type='numpassword' id='pin' name='pin' title='PIN' value=''/><setvar name=\"pin\" value=\"\"/><br/>";
	//sms:$mobilenu?body=RECHARGE&$amount;
	formPage += "<br/>";
	formPage += "<setvar id='to' name='to' value='9738942822'/><setvar id='SMSID' name='SMSID' value='RECHARGE'/><input type='submit' name='submit' value='Recharge' class='buttonBgBlue' align='center' />" ;
	formPage += "<setvar id='to' name='to' value='9738942822'/><setvar id='SMSID' name='SMSID' value='RECHARGE'/><input type='button' name='smsRecharge' value='Recharge by SMS' class='buttonBgBlue' align='center' onClick='sms://to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$'/>" ;
	formPage += "</form></div>" ;
	
	widget.savePermanent = SAVE_PERMINENT;	
	divElement= document.getElementById("Menu_Recharge1");
	divElement.innerHTML = formPage;
	divElement.style.display = "block";  
}


function retailerReg(mobile,pin)
{

	widget.clearUserData("eKey");
	widget.clearUserData("launchMSISDN");
	var ucode = Math.floor((Math.random() * 10000000000) + 1);
	var imei = widget.getHeader("IMEI");
	//if(imei == "00000000000000" )
	{	
		if(mobile == "8801232549")
		{
			imei = "353734053371962";
		}else
		{
			imei = "353743053371927";
		}
		
		
	}
	var PreTUPSSERVER = widget.widgetProperty ("PreTUPSSERVER") ;
	var LOGIN = widget.widgetProperty ("LOGIN") ;
	var PASSWORD = widget.widgetProperty ("PASSWORD") ;
	var REQUEST_GATEWAY_CODE = widget.widgetProperty ("REQUEST_GATEWAY_CODE") ;
	var REQUEST_GATEWAY_TYPE = widget.widgetProperty ("REQUEST_GATEWAY_TYPE") ;
	var SERVICE_PORT = widget.widgetProperty ("SERVICE_PORT") ;
	var SOURCE_TYPE = widget.widgetProperty ("SOURCE_TYPE") ;
	var TYPE = widget.widgetProperty ("TYPE") ;
	var LANGUAGE1 = widget.widgetProperty ("LANGUAGE1") ;
	
	
	var postData = "";
	var cdrStr="";
	var url = "";
	
	url=PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;
	
	
	if(DEMO_FLAG == '1' || DEMO_FLAG == 1)
	{
		url= DEMO_URL;
		postData = "TYPE=STPREGREQ&IMEI="+imei+"&UCODE="+ucode;
	}else
	{
		postData = "TYPE="+TYPE+"&MSISDN="+mobile+"&IMEI="+imei+"&PIN="+pin+"&LANGUAGE1="+LANGUAGE1;	
	}
		
	
	
	widget.logWrite(7,"retailerReg url::"+url);
	widget.logWrite(7,"retailerReg postdata request::"+postData);

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
				//var xmlText = "TYPE=PREGRES&TXNSTATUS=200&MESSAGE=Registration Successful&ENK=33F876F832F7592C";
				widget.logWrite(7,"response for retailerReg::"+xmlText);
				if (xmlText != null && !xmlText.indexOf("null") > -1 )
				{
					
					var txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
					var txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
					//var mobileNo  = responseStr(xmlText, STR_MSISDN) ;
					//var encryptKey  = responseStr(xmlText, STR_EKEY) ;
					cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
					widget.writeCDR (1, cdrStr) ;
					widget.logWrite(7,"cdrstr logs.."+cdrStr);
										
					if(txn_status == "200" || txn_status == "230")
					{
						widget.storeUserData("flag","true");
						//widget.storeUserData("ekey",encryptKey);
						widget.storeUserData("launchMSISDN",mobile);
						widget.storeUserData("launchPIN",pin);
						widget.storeUserData("IMEI",imei);

						if(groupName == "rpretups")
						{
							window.location = "wgt:979446865/1.0:rechargeMenu()";
						}else
						{
							window.location = "wgt:237453940/1.0:rechargeMenu()";
						}




					}else
					{
						divElement= document.getElementById("post1");
						divElement.title = STR_TITLE;
						divElement.innerHTML = txn_message ;
						divElement.style.display = "block";
					}
									
					
				}else
				 {
					divElement= document.getElementById("post1");
					divElement.title = STR_TITLE;
					divElement.innerHTML = STR_SERVER_ERROR ;
					divElement.style.display = "block";
				 }
			}else
			{
				var str;
				var divElement;
				str = STR_SERVICE_ERROR;
				divElement= document.getElementById("post1");
				divElement.title = STR_TITLE;
				divElement.innerHTML = str;
				divElement.style.display = "block";
			}  				


		};
	}
	xmlHttp.open ("POST", url , false) ;
	xmlHttp.setRequestHeader("Content-Type", "plain");
	xmlHttp.setRequestHeader("Connection", "close");
	cdrStr += changetimeformat()+"|retailerReg ";
	xmlHttp.send (postData) ;


}


function loginCheck(mobile,pin,imei)
{
		var str="";
		widget.logWrite(6,"bearer type:"+bearer);
		
		var divElement;
		if(bearer != SMS_BEARER_TYPE && (mobile == "" || pin == "" || imei==""))
		{
			str="Sorry,field(s) cannot be empty. Please enter valid input";

			divElement = document.getElementById("post2");
			divElement.innerHTML = str ;
			divElement.style.display = "block";
		}
		
		else
		{
		
		widget.storeUserData("IMEI",imei);
		
		var PreTUPSSERVER = widget.widgetProperty ("PreTUPSSERVER") ;
		var LOGIN = widget.widgetProperty ("LOGIN") ;
		var PASSWORD = widget.widgetProperty ("PASSWORD") ;
		var REQUEST_GATEWAY_CODE = widget.widgetProperty ("REQUEST_GATEWAY_CODE") ;
		var REQUEST_GATEWAY_TYPE = widget.widgetProperty ("REQUEST_GATEWAY_TYPE") ;
		var SERVICE_PORT = widget.widgetProperty ("SERVICE_PORT") ;
		var SOURCE_TYPE = widget.widgetProperty ("SOURCE_TYPE") ;
		var TYPE = widget.widgetProperty ("TYPE") ;
		var EXTNWCODE = widget.widgetProperty ("EXTNWCODE") ;
		var LOGINID = widget.widgetProperty("LOGINID");
		var PASSWORDXML = widget.widgetProperty("PASSWORDXML");
				
		var date = changetimeformat();	
		var url="" + PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT+"";
		//var xmldata = "<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE>EXRCTRFREQ</TYPE><DATE>25-7-2012 14:36:23</DATE><EXTNWCODE>NG</EXTNWCODE><MSISDN>9867045847</MSISDN><PIN>1357</PIN><LOGINID>Rahul_Choube</LOGINID><PASSWORD>com@1234</PASSWORD><EXTCODE></EXTCODE><EXTREFNUM></EXTREFNUM><MSISDN2>7799000</MSISDN2><AMOUNT>10</AMOUNT><LANGUAGE1>1</LANGUAGE1><LANGUAGE2>1</LANGUAGE2><SELECTOR>1</SELECTOR></COMMAND>";
		widget.logWrite(7,"url for logincheck request"+url);
		var xmldata = [ "<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command1.0//EN\" \"xml/command.dtd\">" ,
		               "<COMMAND>",
		               "<TYPE>" + TYPE + "</TYPE>",
		               "<DATE>29/04/14</DATE>",
		               "<EXTNWCODE>" + EXTNWCODE + "</EXTNWCODE>",
		               "<EXTREFNUM></EXTREFNUM>",
		               "<USERLOGINID>" + LOGINID + "</USERLOGINID>",
		               "<USERPASSWORD>" + PASSWORDXML + "</USERPASSWORD>",
		               "<MSISDN1>" + mobile + "</MSISDN1>",
		               "<PIN>" + pin + "</PIN>",
		               "<IMEINO>" + imei + "</IMEINO>",
		               "</COMMAND>"].join("");
	
		widget.logWrite(7,"xml format loginCheck request"+xmldata);
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
                                  widget.logWrite(7,"xmlHttp.readyState : "+xmlHttp.readyState + "xmlHttp.status :" +xmlHttp.status);

					var xmlDoc = xmlHttp.responseXML ;
					var xmlText = xmlHttp.responseText ;
                                   widget.logWrite(7,"Response:===================>"+xmlText);

					widget.logWrite(7,"xml response for recharge"+xmlDoc);
                                   window.location = "wgt:979446865/1.0:rechargeMenu()";

					//if (xmlText)
					//{
						parseLoginCheck(xmlText,mobile,pin) ;
					//}
				}else
				{
					var str;
					var divElement;
					str = "Service Unavailable";
					if(bearer == SMS_BEARER_TYPE){
						var senderNo = widget.fetchMSISDNNumber();
							widget.logWrite(7, "senders number " + senderNo);
							var sendStatus = widget.sendSMS(senderNo,str,null);//Handle sending SMS to short code with error string received for MSISDN
							if(sendStatus == 1){
							widget.logWrite(6, "SMS sent successfully on Error :"+str);
							}else{
							widget.logWrite(6, "SMS not sent on Error :"+str);
						}
					}else{
						if (bearer == USSD_BEARER_TYPE)
						{
							str += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
						}
						divElement= document.getElementById("post1");
						divElement.title = "PreTUPS";
						divElement.innerHTML = str;
						divElement.style.display = "block";
					}  				}

			};
			xmlHttp.open ("POST", url , false) ;
			xmlHttp.setRequestHeader("Content-Type", "xml");
			xmlHttp.setRequestHeader("Connection", "close");
			xmlHttp.send (xmldata) ;
		}
		
	}
	
	
}
function parseLoginCheck(xmlDoc,mobile,pin)
{
	var divElement;
	var str="";
	
	var rootele = document.createElement ("root") ;
	rootele.innerHTML = xmlDoc ;
	
	var rechargeInfo = rootele.getElementsByTagName("COMMAND") ;

	//	var type =  rechargeInfo[0].getElementsByTagName("TYPE")[0].textContent;
		var tran_Status =  rechargeInfo[0].getElementsByTagName("TXNSTATUS")[0].textContent;
		//var date =  rechargeInfo[0].getElementsByTagName("DATE")[0].textContent;
		//var unque_no =  rechargeInfo[0].getElementsByTagName("EXTREFNUM")[0].textContent;
		var tran_Msg =  rechargeInfo[0].getElementsByTagName("MESSAGE")[0].textContent;
		if(tran_Status== "200")
		{
			widget.storeUserData("launchMSISDN",mobile) ;
			widget.storeUserData("launchPIN",pin);
			widget.storeUserData("flag","true");
			window.location = "wgt:979446865/1.0:rechargeMenu()";
			//displayMain();
		}
		else
		{
			str += tran_Msg ;
		}
		
		if (bearer == USSD_BEARER_TYPE)
		{
			str += "<a id='back' href='javascript:displayMain();'>1. Back</a>";
		}
		
		divElement= document.getElementById("post1");
		divElement.title = "PreTUPS";
		divElement.innerHTML = str;
		divElement.style.display = "block";  
}



function getTopBar (flag)
{
	var str ;

	if(flag==0)
	{
		str =   "<div id='topbar' class='c3lMenuGroup' mode='nowrap-fit' >"+
		"<img id='recharge' class='title1'  src='tab_icon_my-plan.png'  title='Recharge'/>" +
		"<a id='billPay' href=\"wgt:192775972/1.0\"><img  id='billpay' class='title1'  src=\"tab_icon_my-plan.png\" title='Bill Payment' /></a>" +
		"<a id='changepin' href=\"wgt:993256165/1.0\"><img id='changepin'  class='title1' src=\"tab_icon_my-plan.png\" title='Change PIN'  /></a>" +
		"<a id='reports' href=\"wgt:261266208/1.0\"><img id='reports'  class='title1' src=\"tab_icon_my-plan.png\" title='Reports'  /></a>" +
		"<a id='returnstock' href=\"wgt:925464104/1.0\"><img id='returnstock'  class='title1' src=\"tab_icon_my-plan.png\" title='Return Stock'  /></a>" +
		"<a id='gift' href=\"wgt:929959377/1.0\"><img id='gift'  class='title1' src=\"tab_icon_my-plan.png\" title='Gift Recharge'  /></a></div><br/>";

	}

	else if(flag==1)
	{

		str =   "<div id='topbar' class='c3lMenuGroup' mode='nowrap-fit' >"+
		"<a id='recharge' href=\"wgt:213344215/1.0\"><img  id='recharge' class='title1' align='left' src=\"tab_icon_my-recharges.png\" title='Recharge' /></a>"+
		"<img ' src='tab_icon_my-recharges.png' class='title1' id='billpay' title='Bill Payment' />" +	
		"<a id='changepin' href=\"wgt:993256165/1.0\"><img id='changepin'  class='title1' src=\"tab_icon_my-plan.png\" title='Change PIN'  /></a>" +
		"<a id='reports' href=\"wgt:261266208/1.0\"><img id='reports'  class='title1' src=\"tab_icon_my-plan.png\" title='Reports'  /></a>" +
		"<a id='returnstock' href=\"wgt:925464104/1.0\"><img id='returnstock'  class='title1' src=\"tab_icon_my-plan.png\" title='Return Stock'  /></a>"+
		"<a id='gift' href=\"wgt:929959377/1.0\"><img id='gift'  class='title1' src=\"tab_icon_my-plan.png\" title='Gift Recharge'  /></a></div><br/>";

	}	
	else if(flag==2)
	{

		str =   "<div id='topbar' class='c3lMenuGroup' mode='nowrap-fit' >"+
		"<a id='recharge' href=\"wgt:213344215/1.0\"><img  id='recharge' align='left' class='title1' src=\"tab_icon_my-recharges.png\"  title='Recharge'/></a>"+
		"<a id='billPay' href=\"wgt:192775972/1.0\"><img class='title1' src='tab_icon_my-recharges.png' id='billpay' title='Bill Payment'/></a>"+		
		"<img id='changepin'  class='title1' src=\"tab_icon_my-plan.png\" title='Change PIN'  />" +
		"<a id='reports' href=\"wgt:261266208/1.0\"><img id='reports'  class='title1' src=\"tab_icon_my-plan.png\" title='Reports'  /></a>" +
		"<a id='returnstock' href=\"wgt:925464104/1.0\"><img id='returnstock'  class='title1' src=\"tab_icon_my-plan.png\" title='Return Stock'  /></a>"+
		"<a id='gift' href=\"wgt:929959377/1.0\"><img id='gift'  class='title1' src=\"tab_icon_my-plan.png\" title='Gift Recharge'  /></a></div><br/>";

	}	
	else if(flag==3)
	{

		str =   "<div id='topbar' class='c3lMenuGroup' mode='nowrap-fit' >"+
		"<a id='recharge' href=\"wgt:213344215/1.0\"><img  id='recharge' class='title1' align='left' src=\"tab_icon_my-recharges.png\"  title='Recharge'/></a>"+
		"<a id='billPay' href=\"wgt:192775972/1.0\"><img  class='title1' src='tab_icon_my-recharges.png' id='billpay' title='Bill Payment'/></a>"+		
		"<a id='changepin' href=\"wgt:993256165/1.0\"><img id='changepin'  class='title1' src=\"tab_icon_my-plan.png\" title='Change PIN'  /></a>" +
		"<img id='reports'  class='title1' src=\"tab_icon_my-plan.png\" title='Reports'  />"+
		"<a id='returnstock' href=\"wgt:925464104/1.0\"><img id='returnstock'  class='title1' src=\"tab_icon_my-plan.png\" title='Return Stock'  /></a>"+
		"<a id='gift' href=\"wgt:929959377/1.0\"><img id='gift'  class='title1' src=\"tab_icon_my-plan.png\" title='Gift Recharge'  /></a></div><br/>";

	}	
	else if(flag==4)
	{

		str =   "<div id='topbar' class='c3lMenuGroup' mode='nowrap-fit' >"+
		"<a id='recharge' href=\"wgt:213344215/1.0\"><img  id='recharge' class='title1' align='left' src=\"tab_icon_my-recharges.png\"  title='Recharge'/></a>"+
		"<a id='billPay' href=\"wgt:192775972/1.0\"><img  class='title1' src='tab_icon_my-recharges.png' id='billpay' title='Bill Payment'/></a>"+		
		"<a id='changepin' href=\"wgt:993256165/1.0\"><img id='changepin'  class='title1' src=\"tab_icon_my-plan.png\" title='Change PIN'  /></a>" +
		"<a id='reports' href=\"wgt:261266208/1.0\"><img id='reports'  class='title1' src=\"tab_icon_my-plan.png\" title='Reports'  /></a>" +
		"<img id='returnstock'  class='title1' src=\"tab_icon_my-plan.png\" title='Return Stock'  />" +
		"<a id='gift' href=\"wgt:929959377/1.0\"><img id='gift'  class='title1' src=\"tab_icon_my-plan.png\" title='Gift Recharge'  /></a></div><br/>";
	}	
	else
	{

		str =   "<div id='topbar' class='c3lMenuGroup' mode='nowrap-fit' >"+
		"<a id='recharge' href=\"wgt:213344215/1.0\"><img  id='recharge' class='title1' align='left' src=\"tab_icon_my-recharges.png\"  title='Recharge'/></a>"+
		"<a id='billPay' href=\"wgt:192775972/1.0\"><img  class='title1' src='tab_icon_my-recharges.png' id='billpay' title='Bill Payment'/></a>"+		
		"<a id='changepin' href=\"wgt:993256165/1.0\"><img id='changepin'  class='title1' src=\"tab_icon_my-plan.png\" title='Change PIN'  /></a>" +
		"<a id='reports' href=\"wgt:261266208/1.0\"><img id='reports'  class='title1' src=\"tab_icon_my-plan.png\" title='Reports'  /></a>" +
		"<a id='returnstock' href=\"wgt:925464104/1.0\"><img id='returnstock'  class='title1' src=\"tab_icon_my-plan.png\" title='Return Stock'  /></a>" +
		"<img id='gift'  class='title1' src=\"tab_icon_my-plan.png\" title='Gift Recharge'  /></div><br/>";
	}	
	return str ;
}
function changetimeformat()
{
	var date = new Date();
	var currentdate = date.getDate();
	var month = date.getMonth() + 1;
	var year = date.getFullYear();
	var hour = date.getHours();
	var min = date.getMinutes();
	var sec = date.getSeconds();
	//date = currentdate + "-" + month + "-" + year + " " + hour + ":" + min + ":" + sec ;
	date = currentdate + "/" + month + "/" + year.toString().substring(2,4);
	return date;
	
	
}
