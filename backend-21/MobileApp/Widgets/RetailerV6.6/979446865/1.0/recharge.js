var xmlHttp = null ;
var USSD_BEARER_TYPE = 2;
var SMS_BEARER_TYPE = 3;
var widget = window.widget ;	
var bearer = widget.fetchBearerType () ;
var vasstore ="";
var vasstore1= "";

var mobile = widget.retrieveWidgetUserData(179878594,"launchMSISDN") ;
widget.logWrite(7,"mobile number from recharge==" + mobile );

var imei = widget.retrieveWidgetUserData(179878594, "IMEI");
var ekey = widget.retrieveWidgetUserData(179878594, "ekey");

var PreTUPSSERVER = widget.widgetProperty ("PreTUPSSERVER") ;
var LOGIN = widget.widgetProperty ("LOGIN") ;
var PASSWORD = widget.widgetProperty ("PASSWORD") ;
var REQUEST_GATEWAY_CODE = widget.widgetProperty ("REQUEST_GATEWAY_CODE") ;
var REQUEST_GATEWAY_TYPE = widget.widgetProperty ("REQUEST_GATEWAY_TYPE") ;
var SERVICE_PORT = widget.widgetProperty ("SERVICE_PORT") ;
var SOURCE_TYPE = widget.widgetProperty ("SOURCE_TYPE") ;
var RECHARGE_TYPE = widget.widgetProperty ("PLAINREQ_TYPE") ;
var LANGUAGE1 = widget.widgetProperty("LANGUAGE1");
var LANGUAGE2 = widget.widgetProperty("LANGUAGE2");
var SELECTOR = widget.widgetProperty ("SELECTOR") ;
var CALC_TYPE=widget.widgetProperty ("COMMCALC") ;
var VAS_TYPE=widget.widgetProperty ("VAS_TYPE") ;
var vasProductStore="";


//added by parul for vas enquiry
var VAS_TYPE_ENQUIRY=widget.widgetProperty ("VAS_TYPE_ENQUIRY") ;


var presentLang=widget.fetchLanguage();
var langCode = getPresentLan(presentLang);
widget.logWrite(7,"language code ::::::" +langCode);


var RECHARGE_STATUS_TYPE = widget.widgetProperty ("RECHARGE_STATUS_TYPE") ;
var IDENT = widget.widgetProperty ("IDENT") ;
var prodServices="";
prodServices = getProdServicesGatway();
var url = "";
url=PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;
if(DEMO_FLAG == '1' || DEMO_FLAG == 1)
{
	//url= DEMO_URL;
	url = "http://122.166.59.161:8190/webaxn/plugin?plugin=pretupsDEMOSIMULATOR";
}

url =  widget.urlEncode(url);
function rechargeMenu(rechMode)
{      

widget.logWrite(7,"Data of prod_services_gatway" +prodServices);
      	var formPage="";
        //getVasServicesSelection();

	formPage += title();
	//formPage += "<div width='100%' class='c3lTitle marginBottom20'><a class='c3lMenuGroup' align='left' style='padding:10% 10% 10% 10%' href='#menulist'><img   width='8%'  valign='middle' src='menu.png'/></a><span class='topSize'  id='pretitle' align='center'>" + STR_PRETUPS_TITLE + "</span><a  align='right' style='padding:10% 10% 10% 10%' href='#settingslist'><img   width='10%'  valign='middle' src='mainbar.png'/></a><hr /></div>";
	//formPage += "<div width='100%' class='c3lTitle marginBottom20'><span class='topSize'  id='pretitle' align='center'>" + STR_PRETUPS_TITLE + "</span><hr /></div>";
	if(Number(clientVersion_actual) < Number(clientVersion_config))
	{
		formPage +="<input  id='mobilenu' 	name='mobilenu' type='mobileno' maxLength='" + MSISDN_LENGTH + "' class='inputBg2' emptyok='false' title='" + STR_MSISDN_TITLE + "'  />";
	}else
	{
		formPage += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='mobilenu' name='mobilenu' type='mobileno' value='' maxLength='" + MSISDN_LENGTH + "' class='inputBg3' emptyok='false' title='" + STR_MSISDN_TITLE + "' width='78%' /><a class='c3lMenuGroup buttonBg1'   width='17%' height='50px' href='qrcode://scan?delimiter=;&s1=$orderid$&s2=$rechid$&s3=$orderValue$&s4=$mobilenu$&s5=$amount$'><span class='redColor' align='center'>"+STR_SCANQR+"</span></a></div>";
	}
        
	formPage += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='amount' 	name='amount' type='decimal' maxLength='" + AMOUNT_LENGTH + "' class='inputBg2' emptyok='false' value='' title='" + STR_AMOUNT_TITLE + "'  /></div>";
	formPage += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input id='pin' name='pin' class='c3lMenuGroup' type='numpassword' class='inputBg2' maxLength='" + PIN_LENGTH + "' emptyok='false' title='"+STR_PIN_TITLE+"' encrypt='true' /><div>";
	

	formPage += "<a id='gprsbut' class='c3lMenuGroup buttonMenu' href=\"sendRechargeReq($mobilenu,$amount,$pin);\" althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$' althref='tel://call=*123#'><span class='buttonText'  align='center'>"+STR_RECHARGE+"</span></a>";
	formPage += "<a id='smsbut' style='visibility:hidden' class='c3lMenuGroup buttonMenu' href='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$' ><span class='buttonText'  align='center'>"+STR_RECHARGE_SMS+"</span></a>";
	formPage += "<a id='ussdbut' class='c3lMenuGroup buttonMenu' style='visibility:hidden' href='tel://call=*123*$mobilenu$*$amount$*$pin$#'><span class='buttonText' align='center'>"+STR_RECHARGE_USSD+"</span></a>";
	
	if(Number(clientVersion_actual) < Number(clientVersion_config))
	{
		formPage +=navBottom(1);// althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$'
	}
	//var mainPage = displayMain();
		
	document.getElementById("Menu_Recharge").innerHTML = formPage;
	document.getElementById("Menu_Recharge").style.display = "block"; 
	document.getElementById("withdraw").style.display = "block";
	document.getElementById("otherbal").style.display = "block";
	document.getElementById("transfer").style.display = "block";
	document.getElementById("billpay").style.display = "block";
	document.getElementById("returnstock").style.display = "block";
	document.getElementById("reports").style.display = "block";
	document.getElementById("giftRecharge").style.display = "block";
	document.getElementById("elecvocher").style.display = "block";
	document.getElementById("changepin").style.display = "block";
	document.getElementById("rechargeStatus").style.display = "block";
       document.getElementById("commission").style.display = "block";
       document.getElementById("vasrechargeDiv").style.display = "block";
       document.getElementById("enquiryvas").style.display = "block";
       document.getElementById("Menu_Recharge").innerHTML="<setvar name='mobilenu' value=''/><setvar name='pin' value=''/>"+formPage;

	//widget.savePermanent = true;//SAVE_PERMINENT
}
function mainMenu()
{
	var mainPage = displayMain();
	document.getElementById("Main_Menu").innerHTML = mainPage ;
	document.getElementById("Main_Menu").style.display = "block";
	document.getElementById("Menu_Recharge").style.display = "block"; 
	document.getElementById("transfer").style.display = "block";
	document.getElementById("billpay").style.display = "block";
	document.getElementById("returnstock").style.display = "block";
	document.getElementById("reports").style.display = "block";
	document.getElementById("giftRecharge").style.display = "block";
	document.getElementById("elecvocher").style.display = "block";
	document.getElementById("changepin").style.display = "block";
       document.getElementById("commission").style.display = "block";
	
}
function rechBut()
{
	widget.logWrite(7,"rechButton page start");
	var str = "";
	str += "<div class='c3lMenuGroup' id='rechBut'>";
	if(BYPASS_WEBAXN)
	{
		str += "<a id='recharge' class='c3lMenuGroup buttonBgBlue1' resimg='buttonnew.png' align='center' href=\"validate://targetid=$mobilenu$;$amount$;$pin$&action=fetchurl://url="+url+"&postdata=$postdata$&ekey="+ekey+"&encryptreq=true\"  althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$' ><span align='center'  class='buttonText' >Recharge</span></a>" ;
		str +="<setvar name='postdata' value='TYPE=ADHOCRC&MSISDN=7200026159&Message=IMEI="+imei+"&PIN=$pin$&MSISDN2=$mobilenu$&AMOUNT=$amount$&SELECTOR="+SELECTOR+"'/>";
	}else
	{
		str += "<a id='recharge' class='c3lMenuGroup buttonBgBlue1' resimg='buttonnew.png' align='center' href='sendRechargeReq($mobilenu,$amount,$pin);'  althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$' ><span align='center'  class='buttonText' >Recharge</span></a>" ;
	}
	str +="</div>";
	var divElement= document.getElementById("Menu_Recharge");
	divElement.innerHTML = str;
	divElement.style.display = "block";  
	document.getElementById("transfer").style.display = "block";
	document.getElementById("Menu_Recharge").style.display = "block"; 
	document.getElementById("transfer").style.display = "block";
	document.getElementById("billpay").style.display = "block";
	document.getElementById("returnstock").style.display = "block";
	document.getElementById("reports").style.display = "block";
	document.getElementById("giftRecharge").style.display = "block";
	document.getElementById("elecvocher").style.display = "block";
	document.getElementById("changepin").style.display = "block";
}
function title()
{

	var str = "";
	
	if(widget.clientVersion.indexOf("J2ME") != -1)
	{
		//str += "<div width='100%' class='c3lMenuGroup marginBottom20'><a align='left' style='padding:10% 10% 10% 10%' href='#menulist'><img   width='8%'  valign='middle' resimg='menu.png' src='menu.png'/></a><span class='topSize'  id='pretitle' align='center'>" + STR_PRETUPS_TITLE + "</span><hr /></div>";
		//str += "<div width='100%' class='c3lMenuGroup marginBottom20'><a  width='15%'  href='#menulist'><img align='left'  width='8%' resimg='menu.png' src='menu.png'/></a><span width='85%'  align='center'>" + STR_PRETUPS_TITLE + "</span><hr/></div>";
		str += "<div width='100%' class='c3lMenuGroup'><a style='margin:0 15% 0 15%' align='right' id= 'home' name='home' href='#menulist' ><img align='left'  width='15%' resimg='menu.png' src='menu.png'/></a><span class='topSize' width='85%' align='left' >"+STR_PRETUPS_TITLE+"</span></div><hr/><br/>";
	}else
	{

		if(Number(clientVersion_actual) < Number(clientVersion_config))
		{
			str += "<div width='100%' class='c3lTitle marginBottom20'><span class='topSize'  id='pretitle' align='center'>" + STR_PRETUPS_TITLE + "</span><hr /></div>";
		}else
		{
			str += "<div width='100%' class='c3lTitle'><a  align='left' style='padding:1% 20% 10% 20%' href='keypad://clearall?target=$mobilenu$;$pin$;$amount$;$txnid$;$payeemobilenu$;$Giftermobilenu$&action=Menulist:menulist'><img   width='10%'  valign='middle' resimg='menu.png' src='menu.png'/><img width='50%' src='pretups_logo.png' resimg='pretups_logo.png'/></a>";
			/*str += "<setvar name='selMode' value=''/><select align='right'  style='background-color:rgb(242,242,242)' width='30%'  name='selMode' id='selMode'>" +
			"<option class='c3lMenuGroup' onclick='c3lshow:gprsbut;gprsbut1;gprsbut2;gprsbut3&action=c3lhide:ussdbut;ussdbut1;ussdbut2;ussdbut3;smsbut;smsbut1;smsbut2;smsbut3' value='gprs'>GPRS</option>" +
			"<option class='c3lMenuGroup' onclick='c3lshow:ussdbut;ussdbut1;ussdbut2;ussdbut3&action=c3lhide:gprsbut;gprsbut1;gprsbut2;gprsbut3;smsbut;smsbut1;smsbut2;smsbut3' value='ussd'>USSD</option>" +
			"<option class='c3lMenuGroup' onclick='c3lshow:smsbut;smsbut1;smsbut2;smsbut3&action=c3lhide:ussdbut;ussdbut1;ussdbut2;ussdbut3;gprsbut;gprsbut1;gprsbut2;gprsbut3' value='sms'>SMS</option></select>" ;*/
			//str +="<span align='right'><a href='wgt:179878594/1.0:logout()'><img widht='10%' height='5%' src='MyAccount.png' resimg='MyAccount.png'/></a></span>";
                      str+= "<a class='c3lMenuGroup'  valign='middle' href='#myAcc'><div class='c3lMenuGroup account'  id='pretitle'><img widht='10%' height='8%' src='Account.png' resimg='Account.png'/></div></a>";
			str += "<hr/></div>";
	 		
		}
	}//return "<div width='100%' class='c3lTitle marginBottom20'><a class='c3lMenuGroup' align='left' style='padding:10% 10% 10% 10%' href='#menulist'><img   width='8%'  valign='middle' src='menu.png'/></a><img width='50%' src='pretups_logo.png' resimg='pretups_logo.png'/>";
	return str;
}

function navBottom(type)
{ 
		
	type = nullorUndefCheck(type);
	var str ="";
	str += "<div class='c3lNavigation' mode='nowrap-fit' width='100%' align='center' style='background-color:white'>";
	if(type == 1 || type == '1')
	{
		str += "<div><div align='center' class='c3lMenuGroup activeImg'><hr/><img width='50%' class='marginTop15' src='recharge.png' title=''/><br/><span class='textColor' align='center'>"+STR_RECHARGE+"</span></div>";
	}else
	{
		str += "<a align='center' class='c3lMenuGroup bottomMargin' href='keypad://clearall?target=$mobilenu$;$amount$;$pin$&action=Menulist:Menu_Recharge'><img width='50%' resimg='recharge.png' src='recharge.png' title=''/><br/><span class='textColor' align='center'>"+STR_RECHARGE+"</span></a>";

	}
	
	if(TRANSFER)
	{
		if(type == 7 || type == '7')
		{
			str +="<div align='center' class='c3lMenuGroup activeImg'><hr/><img width='50%' class='marginTop15' resimg='returnstock.png' src='returnstock.png' title=''/><br/><span class='textColor' align='center'>"+STR_TRANSFER+"</span></div>";
		}else
		{
			str +="<a align='center' class='c3lMenuGroup bottomMargin'  href='keypad://clearall?target=$mobilenu$;$amount$;$pin$&action=Menulist:transfer'><img width='50%'  src='returnstock.png' title=''/><br/><span class='textColor' align='center'>"+STR_TRANSFER+"</span></a>";
		}

	}
	
	if(BILLPAYMENT)
	{	
		if(type == 2 || type == '2')
		{
			str += "<div align='center' class='c3lMenuGroup activeImg'><hr/><img width='50%' class='marginTop15' resimg='billpayment.png' src='billpayment.png' title=''/><br/><span class='textColor' align='center'>"+STR_BILLPAY+"</span></div>";
		}else
		{
			str += "<a align='center' class='c3lMenuGroup bottomMargin'   href='keypad://clearall?target=$mobilenu$;$amount$;$pin$&action=Menulist:billpay'><img width='50%'  src='billpayment.png' title=''/><br/><span class='textColor' align='center'>"+STR_BILLPAY+"</span></a>";	
		}
		
	}
	if(GIFTRECHARGE)
	{
		if(type == 5 || type == '5')
		{
			str +="<div align='center' class='c3lMenuGroup activeImg'><hr/><img width='50%' class='marginTop15' resimg='giftrecharge.png' src='giftrecharge.png' title=''/><br/><span class='textColor' align='center'>"+STR_GIFT_RECHARGE+"</span></div>";
		}else
		{
			str +="<a align='center' class='c3lMenuGroup bottomMargin'  href='keypad://clearall?target=$payeemobilenu$;$Giftermobilenu$;$amount$;$pin$&action=Menulist:giftRecharge'><img width='50%'  src='giftrecharge.png' title=''/><br/><span class='textColor' align='center'>"+STR_GIFT_RECHARGE+"</span></a>";
		}
	}
	
	
	
	if(REPORTS)
	{
		if(type == 4 || type == '4')
		{
			str +="<div align='center' class='c3lMenuGroup activeImg'><hr/><img width='50%' class='marginTop15' resimg='reports.png' src='reports.png' title=''/><br/><span class='textColor' align='center'>"+STR_REPORT+"</span></div>";	
		}else
		{
			str +="<a align='center' class='c3lMenuGroup bottomMargin'  href='#reports'><img width='50%'  src='reports.png' title=''/><br/><span class='textColor' align='center'>"+STR_REPORT+"</span></a>";
		}

	}

	if(RETURNSTOCK)
	{
		if(type == 3 || type == '3')
		{
			str +="<div align='center' class='c3lMenuGroup activeImg'><hr/><img width='50%' class='marginTop15' resimg='returnstock.png' src='returnstock.png' title=''/><br/><span class='textColor' align='center'>"+STR_RETURNSTOCK+"</span></div>";
		}else
		{
			str +="<a align='center' class='c3lMenuGroup bottomMargin'  href='keypad://clearall?target=$amount$;$pin$&action=Menulist:returnstock'><img width='50%'  src='returnstock.png' title=''/><br/><span class='textColor' align='center'>"+STR_RETURNSTOCK+"</span></a>";	
		}
	}

	
	if(ELECVOUCHER)
	{
		if(type == 8 || type == '8')
		{
			str +="<div align='center' class='c3lMenuGroup activeImg'><hr/><img width='50%' class='marginTop15' resimg='evoucher.png' src='evoucher.png' title=''/><br/><span class='textColor' align='center'>"+STR_ELECVOCHER_BUT+"</span></div>";
		}else
		{
			str +="<a align='center' class='c3lMenuGroup bottomMargin'  href='keypad://clearall?target=$mobilenu$;$amount$;$pin$&action=Menulist:elecvocher'><img width='50%'  resimg='evoucher.png' src='evoucher.png' title=''/><br/><span class='textColor' align='center'>"+STR_ELECVOCHER_BUT+"</span></a>";
		}
	} 
	
	if(type == 6 || type == '6')
	{
		str +="<div align='center' class='c3lMenuGroup activeImg'><hr/><img width='50%' class='marginTop15' resimg='settings.png' src='settings.png' title=''/><br/><span class='textColor' align='center'>"+STR_CHANGEPIN+"</span></div>";
	}else
	{
		str +="<a align='center' class='c3lMenuGroup bottomMargin'  href='keypad://clearall?target=$oldpin$;$newpin$;$confirmnewpin$&action=Menulist:changepin'><img width='50%'  src='settings.png' title=''/><br/><span class='textColor' align='center'>"+STR_CHANGEPIN+"</span></a>";
	}
	
	str +="</div>";

	return str;
}
function display()
{
	var mainPage = "<div class='c3lMenuGroup'>";
	mainPage += "<span width='100%' align='left' class='topMessage'>PreTUPS</span><br/>";
	mainPage += "<a href=\"#Menu_Recharge\" id=\"recharge\" class =\"c3lMenuGroup options\" width=\"100%\"><img name=\"img\" valign='middle' align=\"left\" resimg='recharge.png' src=\"recharge.png\" /><span class='textalign' valign='middle'>Recharge</span><img name=\"img\" valign='middle' align=\"right\" resimg='rightarr.png' src=\"rightarr.png\" /></a><br/>";
	mainPage += "<a href=\"#changepin\"  class =\"c3lMenuGroup options\" width=\"100%\"><img name=\"img\" valign='middle' align=\"left\" resimg=\"settings.png\" src=\"settings.png\" /><span class='textalign' valign='middle'>Change PIN</span><img name=\"img\" valign='middle' align=\"right\" resimg='rightarr.png' src=\"rightarr.png\" /></a>";
	mainPage += "<a id='displayMainMenu' class='c3lMenuGroup' type='ajax' href='displayMainMenu()'>.</a>";
	mainPage += "</div>";
	//return mainPage;
	document.getElementById("Main_Menu").innerHTML = str ;
	document.getElementById("Main_Menu").style.display = "block";
	
}

function displayMainMenu()
{
	var str = "";
	str += "<div id='displayMainMenu' class='c3lMenuGroup'>"; 
	
	//str += "<span width='100%' align='left' class='topMessage'>PreTUPS</span><br/>";
						
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
	
	
	document.getElementById("Main_Menu").innerHTML = str ;
	document.getElementById("Main_Menu").style.display = "block";
	
	
}
function elevocreq(mobilenu,amount,pin)
{	
	var msg= "Thank You,You account has been recharged with Rs."+amount+" is Successful";
	widget.logWrite(6,"alertTemp***********************");
	var element = document.getElementById("alert");
	element.style.display = "block";
		element.innerHTML = msg+"<setvar name='mobilenu' value=''/><setvar name='amount' value=''/><setvar name='pin' value='' />";
}

function loadVirtualKeyPad()
{
	var anchorDisplay = new Array() ;
	var str = "";
	anchorDisplay[0] = "<a  width='30%' id='1'  class='c3lMenuGroup vkeypad'  href='keypad://append?name=$pin$&value=1'  style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>1</span></a>" ;
	anchorDisplay[1] = "<a  width='30%' id='2'  class='c3lMenuGroup vkeypad'  href='keypad://append?name=$pin$&value=2' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>2</span></a>" ;
	anchorDisplay[2] = "<a  width='30%' id='3'  class='c3lMenuGroup vkeypad'  href='keypad://append?name=$pin$&value=3' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>3</span></a>" ;
	anchorDisplay[3] = "<a  width='30%' id='4'  class='c3lMenuGroup vkeypad'  href='keypad://append?name=$pin$&value=4' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>4</span></a>" ;
	anchorDisplay[4] = "<a  width='30%' id='5'  class='c3lMenuGroup vkeypad'  href='keypad://append?name=$pin$&value=5' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>5</span></a>" ;
	anchorDisplay[5] = "<a  width='30%' id='6'  class='c3lMenuGroup vkeypad'  href='keypad://append?name=$pin$&value=6' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>6</span></a>" ;
	anchorDisplay[6] = "<a  width='30%' id='7'  class='c3lMenuGroup vkeypad'  href='keypad://append?name=$pin$&value=7' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>7</span></a>" ;
	anchorDisplay[7] = "<a  width='30%' id='8'  class='c3lMenuGroup vkeypad'  href='keypad://append?name=$pin$&value=8' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>8</span></a>" ;
	anchorDisplay[8] = "<a  width='30%' id='9'  class='c3lMenuGroup vkeypad'  href='keypad://append?name=$pin$&value=9' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>9</span></a>" ;
	clearBtn = "<a  width='30%' id='clr'  class='c3lMenuGroup vkeypad'  href='keypad://clearall?name=$pin$' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>CLR</span></a>" ;
	anchorDisplay[9] = "<a  width='30%' id='10' class='c3lMenuGroup vkeypad'  href='keypad://append?name=$pin$&value=0' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>0</span></a>" ;
	deleteBtn = "<a  width='30%' id='del'  class='c3lMenuGroup vkeypad'  href='keypad://clearchar?name=$pin$' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>DEL</span></a>" ;
	Recharge = "<a  width='30%' id='del'  class='c3lMenuGroup vkeypad'  href=\"validate://targetid=$mobilenu$;$amount$;$pin$&action=fetchurl://url="+url+"&postdata=$postdata$&ekey="+ekey+"&encryptreq=true\"  althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$'  style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>"+STR_RECH_TEXT+"</span></a>";
	//var urlencodedpin = widget.urlEncode("name=$pin$&value="+eKey+"&pinlength=$pinlength$");//$%nsi://"+urlencodedpin+"%,
	str += "<div width='100%' class='c3lMenuGroup topMessage'><span width='85%' align='left' >PreTUPS</span><a style='margin-right:20px' align='right' id= 'home' name='home' href='wgt:179878594/1.0/:displayMain()'><img align='right' resimg='listicon.png' src='listicon.png'/></a></div>";
	str += "<span width='100%' align='left' class='welcomeMsg'>Recharges</span>";
	str += "<input type='numeric'  id='mobilenu' name='mobilenu' align='center'  emptyok='false' resimg='inputbg.9.png'  class='inputBG'     value='' title='"+STR_MSISDN_TITLE+"' maxLength='"+MSISDN_LEN+"'/><setvar name=\"mobilenu\" value=\"\"/>";
	str += "<input type='numeric' id='amount'  emptyok='false' align='center' class='inputBG' resimg='inputbg.9.png' name='amount' value=''   title='"+STR_AMOUNT_TITLE+"' maxLength='"+AMOUNT_LEN+"'/><setvar name=\"amount\" value=\"\"/>";
	str += "<input  emptyok='false' class='inputBG'   align='center'      type='numpassword' id='pin' resimg='inputbg.9.png' name='pin' title='"+STR_PIN_TITLE+"' value='' encrypt='true' maxLength='"+PIN_LEN+"'/><setvar name=\"pin\" value=\"\"/>";

	//str += "<div class='c3lMenuGroup' style='margin : 0% 0% 0% 30%;'><input type='numpassword' id='pin' name='pin' emptyok='false' class='inputBG1' resimg='inputbg.9.png'   tabID='amount'   value=''   title='"+STR_PIN_TITLE+"' encrypt='true'  maxLength='"+PIN_LEN+"'/><input type='decimal'    id='amount' name='amount' emptyok='false' resimg='inputbg.9.png'   class='inputBG1'  value='' title='"+STR_AMOUNT_TITLE+" ' maxLength='"+AMOUNT_LEN+"'/><setvar name=\"amount\" value=\"\"/></div>";
	//str += "</div>";	<div align='center'><a id='recharge' class='c3lMenuGroup buttonBgBlue1' resimg='buttonnew.png' align='center' href=\"validate://targetid=$mobilenu$;$amount$;$pin$&action=fetchurl://url="+url+"&postdata=$postdata$&ekey="+ekey+"&encryptreq=true\"  althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$' ><span align='center' valign='middle' class='buttonText' >Recharge</span></a></div>				
	str = [str,"<div width='90%'  style='background-color:black' align='center' class='c3lMenuGroup'>"+anchorDisplay[0]+anchorDisplay[1]+anchorDisplay[2]+anchorDisplay[3]+anchorDisplay[4]+anchorDisplay[5]+anchorDisplay[6]+anchorDisplay[7]+anchorDisplay[8]+deleteBtn+anchorDisplay[9]+Recharge+"</div>"].join("");
					
	var divElement = document.getElementById("Menu_Recharge");
	divElement.style.display = "block" ;
	divElement.style.backgroundColor = 'black';
    divElement.innerHTML = "<setvar name='pin' value=''/><setvar name='pinlength' value=''/>"+ str;
	//return str;
}
function sendRechargeReq(mobNumber,RechargeAmount,Pin)
{
widget.logWrite(7,"Entered entered sendRechargeReq=====================::");
	mobNumber 		= nullorUndefCheck(mobNumber);
	RechargeAmount 	= nullorUndefCheck(RechargeAmount);
	Pin 			= nullorUndefCheck(Pin);

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

			widget.logWrite(7,"url for recharge request::"+url);
			if(SEND_ENCRYPTREQ)
			{
				postData = "TYPE="+RECHARGE_TYPE+"&MSISDN="+mobile+"&Message="+getEncrypt("IMEI="+imei+"&PIN="+Pin+"&MSISDN2="+mobNumber+"&AMOUNT="+RechargeAmount+"&SELECTOR="+SELECTOR);
				
			}else
			{	//TYPE=RC&MSISDN=<MSISDN>&IMEI=<IMEI>&PIN=<PIN>&MSISDN2=<MSISDN2>&AMOUNT=<AMOUNT>&SELECTOR=<selector>&LANGUAGE1=<langcode>&LANGUAGE2=<lang code>
				postData = "TYPE="+RECHARGE_TYPE+"&MSISDN="+mobile+"&IMEI="+imei+"&PIN="+Pin+"&MSISDN2="+mobNumber+"&AMOUNT="+RechargeAmount+"&SELECTOR="+SELECTOR+"&LANGUAGE1="+langCode+"&LANGUAGE2="+langCode;
			}

			widget.logWrite(7," sendRechargeReq ::"+postData);

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
						widget.logWrite(7,"response for rechargeReq::"+xmlText);
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
								divElement= document.getElementById("toast");
								divElement.title = STR_TITLE;
								divElement.innerHTML = txn_message+"<setvar name='mobilenu' value=''/><setvar name='amount' value=''/><setvar name='pin' value=''/>";
								divElement.style.display = "block";

							}else
							{
								divElement= document.getElementById("toast");
								divElement.title = STR_TITLE;
								divElement.innerHTML = txn_message+"<setvar name='mobilenu' value=''/><setvar name='amount' value=''/><setvar name='pin' value=''/>";
								divElement.style.display = "block";
							}
						}else
						{

							str = STR_SERVER_ERROR;
							divElement= document.getElementById("toast");
							divElement.title = STR_TITLE;
							divElement.innerHTML = str;
							divElement.style.display = "block";
						}
					}else
					{
						str = STR_SERVICE_ERROR;
						divElement= document.getElementById("toast");
						divElement.title =STR_TITLE;
						divElement.innerHTML = str;
						divElement.style.display = "block";
					} 

				};

				xmlHttp.open ("POST", url , false) ;
				xmlHttp.setRequestHeader("Content-Type", SOURCE_TYPE);
				xmlHttp.setRequestHeader("Connection", "close");
				cdrStr += changetimeformat()+"| Recharge";
				xmlHttp.send (postData) ;


			}

		}

}
function sendReqRechStatus(txnid,pin)
{

	txnid 		= nullorUndefCheck(txnid);
	pin 		= nullorUndefCheck(pin);

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

	widget.logWrite(7,"url for sendReqRechStatus request::"+url);
	if(SEND_ENCRYPTREQ)
	{
		postData = "TYPE="+RECHARGE_STATUS_TYPE+"&MSISDN="+mobile+"&Message="+getEncrypt("IMEI="+imei+"&PIN="+Pin+"&MSISDN2="+mobNumber+"&AMOUNT="+RechargeAmount+"&SELECTOR="+SELECTOR);

	}else
	{	//TYPE=RC&MSISDN=<MSISDN>&IMEI=<IMEI>&PIN=<PIN>&MSISDN2=<MSISDN2>&AMOUNT=<AMOUNT>&SELECTOR=<selector>&LANGUAGE1=<langcode>&LANGUAGE2=<lang code>
		postData = "TYPE="+RECHARGE_STATUS_TYPE+"&MSISDN="+mobile+"&IMEI="+imei+"&PIN="+pin+"&TXNID="+txnid+"&LANGUAGE1="+langCode+"&IDENT="+IDENT;
	}

	widget.logWrite(7," postdata for sendReqRechStatus ::"+postData);

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
				widget.logWrite(7,"response for sendReqRechStatus::"+xmlText);
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
						divElement= document.getElementById("toast");
						divElement.title = STR_TITLE;
						divElement.innerHTML = txn_message+"<setvar name='txnid' value=''/><setvar name='pin' value=''/>";
						divElement.style.display = "block";

					}else
					{
						divElement= document.getElementById("toast");
						divElement.title = STR_TITLE;
						divElement.innerHTML = txn_message+"<setvar name='txnid' value=''/><setvar name='pin' value=''/>";
						divElement.style.display = "block";
					}
				}else
				{

					str = STR_SERVER_ERROR;
					divElement= document.getElementById("toast");
					divElement.title = STR_TITLE;
					divElement.innerHTML = str;
					divElement.style.display = "block";
				}
			}else
			{
				str = STR_SERVICE_ERROR;
				divElement= document.getElementById("toast");
				divElement.title =STR_TITLE;
				divElement.innerHTML = str;
				divElement.style.display = "block";
			} 

		};

		xmlHttp.open ("POST", url , false) ;
		xmlHttp.setRequestHeader("Content-Type", SOURCE_TYPE);
		xmlHttp.setRequestHeader("Connection", "close");
		cdrStr += changetimeformat()+"| Recharge Status";
		xmlHttp.send (postData) ;


	}



}

function displayOpr(){
widget.logWrite(7,"display opertaor function=====");

var options =
[
  {
    "text"  : "Option 1",
    "value" : "Value 1"
  },
  {
    "text"     : "Option 2",
    "value"    : "Value 2",
    "selected" : true
  },
  {
    "text"  : "Option 3",
    "value" : "Value 3"
  }
];

var selectBox = document.getElementById('opr');

for(var i = 0, l = options.length; i < l; i++){
  var option = options[i];
  selectBox.options.add( new Option(option.text, option.value, option.selected) );
}
}


function baseCalculator(servicet,amt){
widget.logWrite(7,"Base Calculator products flag====="+servicet+","+amt);

var product = prodServices.split("||");

var str="";
var l="";
product=product[0];
widget.logWrite(7,"Base Calculator products====="+product );
var prdNo= product.split(",");
var serviceType="O2C_o2c,C2C_c2c";
//str+="<div width='100%' class='c3lTitle'> <a class='c3lMenuGroup' align='left'  href='#commission'><img   width='15%'  valign='middle' resimg='icon_back.9.png' src='icon_back.9.png'/></a><span class='topText'   id='pretitle' valign='middle'  >"+BUTTON_BASECOMMISSION+"</span><hr/></div>";

var prdNo = prdNo.length;
if(prdNo == 1){
var text = product.split(",");
widget.logWrite(7,"Base Calculator text====="+text);

l = product.split("_");

str +="<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-top:10px;'><input  id='product' 	name='product' type='text' value='"+l[1]+"' class='inputBg2' emptyok='false' /></div>";

}
else{

str +=" <div class='c3lMenuGroup ' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:10px;'><setvar name='product' value='product'/> <select align='left' id='product' name='product' sendIndex='false'>";
str +="<option class='c3lMenuGroup' value='selectprd' id='selectprd'>Select product</option>";
var text = product.split(",");
for(var i=0;i<text.length;i++){
                      l = text[i].split("_");
                                         
                   str +="<option class='c3lMenuGroup' id = " +l[1] +" value= " +l[1] +">" +l[0] +"</option>";
                  
                }
str +="</select></div>";
}

str +=" <div class='c3lMenuGroup ' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:5px;'><setvar name='servicetype' value='servicetype'/> <select align='left' id='servicetype' name='servicetype' sendIndex='false'>";
str +="<option class='c3lMenuGroup' value='selectservice' id='selectservice'>Select Service Type</option>";

var text = serviceType.split(",");
for(var i=0;i<text.length;i++){
                      l = text[i].split("_");
                                        
                   str +="<option class='c3lMenuGroup' id = " +l[0] +" value= " +l[0] +" >" +l[1] +"</option>";
                  
                }
str +="</select></div>";

str+="<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='amount' 	name='amount' type='decimal' maxLength='" + AMOUNT_LENGTH + "' class='inputBg2' emptyok='false' title='" + STR_AMOUNT_TITLE + "'  /></div>";



//str +="<div class='c3lMenuGroup buttonAllMenu1'><a id='smsbut' class='c3lMenuGroup buttonAllMenu' href='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$' ><span class='buttonText'  align='center'>"+BUTTON_CALCULATOR+"</span></a></div>";

return str;
}

function additionalCalculator(){

widget.logWrite(7,"additinal Calculator  start====================");


var str="";
var l="";
var product = prodServices.split("||");

var module="c2s:C2S";
var services = product[1];
var gateway = product[2];
//var services="C2S Gift Recharge_GRC,Customer Recharge_RC,Customer Roam Recharge_RRC,Enquiry request_C2SENQ,International Recharge_IR,Multiple Voucher Distribution_MVD,Postpaid Bill Payment_PPB,Postpaid Payment Through EL_PPEL,Private Recharge_EVD,Promo VAS Recharge_PVAS,Roaming recharge_RR,SIM Activation_SIMACT,VAS Recharge_VAS,Voucher Recharge_EVR";
//var gateway="EXTGW_GATE1754,EXTGW_EXTGW,MAPPGW_MAPPGW,SMSC_SMSC,USSD_USSD,WEB_WEB,XMLGW_XMLGW,EXTGW_jubb";
//str+="<div width='100%' class='c3lTitle'> <a class='c3lMenuGroup' align='left'  href='#commission'><img   width='15%'  valign='middle' resimg='icon_back.9.png' src='icon_back.9.png'/></a><span class='topText'   id='pretitle' valign='middle'  >"+BUTTON_BASECOMMISSION+"</span><hr/></div>";

str +=" <div class='c3lMenuGroup ' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:10px;'><setvar name='product' value=''/> <select align='left' id='product' name='product' sendIndex='false'>";
var text = module.split(",");
for(var i=0;i<text.length;i++){
                      l = text[i].split(":");
                                         
                   str +="<option class='c3lMenuGroup' id = " +l[0] +" value= " +l[0] +" >" +l[1] +"</option>";
                  
                }
str +="</select></div>";


str +=" <div class='c3lMenuGroup ' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:5px;'><setvar name='adservices' value=''/> <select align='left' id='adservices' name='adservices' sendIndex='false'>";
str +="<option class='c3lMenuGroup' value='selectsrv' id='selectsrv'>Select Service</option>";
var text = services.split(",");
for(var i=0;i<text.length;i++){
                      l = text[i].split("_");
                                          
                   str +="<option class='c3lMenuGroup' id = " +l[1] +" value= " +l[1] +" >" +l[0] +"</option>";
                  
                }
str +="</select></div>";


str +=" <div class='c3lMenuGroup ' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:5px;'><setvar name='gateway' value=''/> <select align='left' id='gateway' name='gateway' sendIndex='false'>";
str +="<option class='c3lMenuGroup' value='selectgt' id='selectgt'>Select Gateway</option>";
var text = gateway.split(",");
for(var i=0;i<text.length;i++){
                      l = text[i].split("_");
                                         
                   str +="<option class='c3lMenuGroup' id = " +l[1] +" value= " +l[1] +" >" +l[0] +"</option>";
                  
                }
str +="</select></div>";




str+="<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='amount' 	name='amount' type='decimal' maxLength='" + AMOUNT_LENGTH + "' class='inputBg2' emptyok='false' title='" + STR_AMOUNT_TITLE + "'  /></div>";



//str +="<div class='c3lMenuGroup buttonAllMenu1'><a id='smsbut' class='c3lMenuGroup buttonAllMenu' href='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$' ><span class='buttonText'  align='center'>"+BUTTON_CALCULATOR+"</span></a></div>";
widget.logWrite(7,"additinal Calculator  end====================");

return str;

}

function baseCalculation(product,serviceType,amount){
widget.logWrite(7,"Base calculation response page baseCalculationRes()=="+product+","+serviceType+","+amount);

var baseResp="";
var formPage="";
var divEle = "";

var postData = "";
var cdrStr="";
var txn_message = "";
var url="";

url=PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;
widget.logWrite(7,"base calculation req url::"+url);
postData = "TYPE="+CALC_TYPE+"&MSISDN="+mobile+"&MODULE="+serviceType+"&AMOUNT="+amount+"&IMEI="+imei+"&PRODUCT="+product+"&SERVICE=N&GATEWAY=N";
//postData = "TYPE="+RECHARGE_STATUS_TYPE+"&MSISDN="+mobile+"&IMEI="+imei+"&PIN=2460&TXNID=R1.009383"&LANGUAGE1="+langCode+"&IDENT="+IDENT;
if(serviceType=="selectservice"){
divEle = document.getElementById("post1");
divEle.title = STR_TITLE;
divEle.innerHTML = INVALID_SERVICE ;
divEle.style.display = "block";


} else if(amount==0){
divEle = document.getElementById("post1");
divEle.title = STR_TITLE;
divEle.innerHTML = ZERO_STOCK ;
divEle.style.display = "block";

}
else{

widget.logWrite(7,"base calculation request::"+postData);

       if (null == xmlHttp)
	{
		xmlHttp = new XMLHttpRequest () ;	
              
	}

      if (xmlHttp)
		{
                    widget.logWrite(7,"get base calculation state::"+xmlHttp.readyState);
    
			xmlHttp.onreadystatechange = function()
			{ widget.logWrite(7,"get base calculation state::"+xmlHttp.readyState);

				if (4 == xmlHttp.readyState &&  200 == xmlHttp.status)
				{
					var xmlText = xmlHttp.responseText ;
                                  // var xmlText= "TYPE=COMMCALCRES&TXNSTATUS=200&MESSAGE=Base Commission %: 10.0,BaseCommission Amount : 9000,Tax : 20,Net Commission : 9020.";


					widget.logWrite(7,"get base calculation response::"+xmlText);
					if (xmlText != null && !xmlText.indexOf("null") > -1 )
					{
						
						var txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
						txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
						cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
						widget.writeCDR (1, cdrStr) ;
						widget.logWrite(7,"cdrstr logs.."+cdrStr);
											
						if(txn_status == "200")
						{  

                                          /*var divElement= document.getElementById("post1");
						divElement.title = STR_TITLE;
						divElement.innerHTML = txn_message;
						divElement.style.display = "block";*/
                                  
                                           window.location = "wgt:979446865/1.0:baseCalRes('"+txn_message+"','"+serviceType+"','"+amount+"')";
                                           
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
		
		xmlHttp.open ("POST", url , false) ;
		xmlHttp.setRequestHeader("Content-Type", "plain");
		xmlHttp.setRequestHeader("Connection", "close");
		cdrStr += changetimeformat()+"|retailerReg ";
		xmlHttp.send (postData) ;
}
}

/*
var base = baseCalculator();
formPage += "<div width='100%' class='c3lTitle'><a class='c3lMenuGroup' align='left'  href='#commission'><img   width='15%'  valign='middle' resimg='icon_back.9.png' src='icon_back.9.png'/></a><span class='topText'   id='pretitle' valign='middle'  >"+BUTTON_BASECOMMISSION+"</span><hr/></div>";
formPage +=base; 
formPage +="<div class='c3lMenuGroup buttonAllMenu1'><a id='basbut' class='c3lMenuGroup buttonAllMenu' href=\"baseCalculation($product,$servicetype,$amount)\"><span class='buttonText'  align='center'>"+BUTTON_CALCULATOR+"</span></a></div>"

formPage += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:5px;'>Base Commission% :5%<hr class='gray'/></div>";
formPage += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:5px;'>Base Commission Amount :20<hr class='gray'/></div>";

formPage += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:5px;'>Tax Amonut:15<hr class='gray'/></div>";

formPage += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:5px;'>Net Commission :25<hr class='gray'/></div>";

document.getElementById("baseRsp").innerHTML = formPage;
document.getElementById("baseRsp").style.display = "block";
*/
}



function additionalCalcultion(product,adservices,gateway,amount){

widget.logWrite(7,"additional calculation additionalCalcultion()=="+product+","+services+","+gateway+","+amount);

var baseResp="";
var formPage="";
var divEle = "";

var postData = "";
var cdrStr="";
var txn_message = "";
var url="";

url=PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;
widget.logWrite(7,"additional calculation  req url::"+url);
postData = "TYPE="+CALC_TYPE+"&MSISDN="+mobile+"&MODULE="+product+"&AMOUNT="+amount+"&IMEI="+imei+"&PRODUCT=N&SERVICE="+adservices+"&GATEWAY="+gateway;
if(adservices=="selectsrv"){
divEle = document.getElementById("post1");
divEle.title = STR_TITLE;
divEle.innerHTML = INVALID_SERVICE ;
divEle.style.display = "block";


} else if(gateway=="selectgt"){
divEle = document.getElementById("post1");
divEle.title = STR_TITLE;
divEle.innerHTML = INVALID_GATEWAY;
divEle.style.display = "block";

}
else if(amount==0){
divEle = document.getElementById("post1");
divEle.title = STR_TITLE;
divEle.innerHTML = ZERO_STOCK ;
divEle.style.display = "block";

}
else{
widget.logWrite(7,"base calculation request::"+postData);

       if (null == xmlHttp)
	{
		xmlHttp = new XMLHttpRequest () ;	
              
	}

      if (xmlHttp)
		{
                    widget.logWrite(7,"get additinal calculation state::"+xmlHttp.readyState);
    
			xmlHttp.onreadystatechange = function()
			{ widget.logWrite(7,"get additinal calculation state::"+xmlHttp.readyState);

				if (4 == xmlHttp.readyState &&  200 == xmlHttp.status)
				{
					var xmlText = xmlHttp.responseText ;
                                  // var xmlText= "TYPE=COMMCALCRES&TXNSTATUS=200&MESSAGE=Base Commission %: 10.0,BaseCommission Amount : 9000,Tax : 20,Net Commission : 9020.";


					widget.logWrite(7,"get additinal calculation response::"+xmlText);
					if (xmlText != null && !xmlText.indexOf("null") > -1 )
					{
						
						var txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
						txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
						cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
						widget.writeCDR (1, cdrStr) ;
						widget.logWrite(7,"cdrstr logs.."+cdrStr);
											
						if(txn_status == "200")
						{  

                                                                                       
                                            window.location = "wgt:979446865/1.0:addCalRes('"+txn_message+"')";
                                           
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
		
		xmlHttp.open ("POST", url , false) ;
		xmlHttp.setRequestHeader("Content-Type", "plain");
		xmlHttp.setRequestHeader("Connection", "close");
		cdrStr += changetimeformat()+"|retailerReg ";
		xmlHttp.send (postData) ;
}

}

/*var xmlText="Additional Commission% :4%,Amount :10,Tax:20,Net Commission :39"
var additional = additionalCalculator();

formPage += "<div width='100%' class='c3lTitle'><a class='c3lMenuGroup' align='left'  href='#commission'><img   width='15%'  valign='middle' resimg='icon_back.9.png' src='icon_back.9.png'/></a><span class='topText'   id='pretitle' valign='middle'  >"+BUTTON_ADDCOMISSION+"</span><hr/></div>";
formPage +=additional; 
formPage +="<div class='c3lMenuGroup buttonAllMenu1'><a id='basbut' class='c3lMenuGroup buttonAllMenu' href=\"additionalCalcultion($product,$services,$gateway)\"><span class='buttonText'  align='center'>"+BUTTON_CALCULATOR+"</span></a></div>"
var res=xmlText.split(",");
for(var i=0;i<res.length;i++){
formPage += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:5px;'>"+res[i]+"<hr class='gray'/></div>";
//formPage += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:5px;'>Amount :10<hr class='gray'/></div>";

//formPage += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:5px;'>Tax:20<hr class='gray'/></div>";

//formPage += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:5px;'>Net Commission :30<hr class='gray'/></div>";
}

document.getElementById("addResponse").innerHTML = formPage;
document.getElementById("addResponse").style.display = "block";
*/


}

function baseCalRes(txn_message,serviceTye,amount){
widget.logWrite(7,"get base baseCalRes::"+txn_message);
var formPage="";
var x =0;
var base = baseCalculator(serviceTye,amount);
formPage += "<div width='100%' class='c3lTitle'><a class='c3lMenuGroup' align='left'  href='wgt:979446865/1.0:rechargeMenu()'><img   width='15%'  valign='middle' resimg='icon_back.9.png' src='icon_back.9.png'/></a><span class='topText'   id='pretitle' valign='middle'  >"+BUTTON_BASECOMMISSION+"</span><hr/></div>";
formPage +=base; 
formPage +="<div class='c3lMenuGroup buttonAllMenu1'><a id='basbut' class='c3lMenuGroup buttonAllMenu' href=\"baseCalculation($product,$servicetype,$amount)\"><span class='buttonText'  align='center'>"+BUTTON_CALCULATOR+"</span></a></div>"
var res=txn_message.split(",");
for(var i=0;i<res.length;i++){
formPage += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:5px;'>"+res[i]+"<hr class='gray'/></div>";
}
document.getElementById("baseRsp").innerHTML =formPage;
document.getElementById("baseRsp").style.display = "block";
//return formPage;
}


function addCalRes(txn_message){
widget.logWrite(7,"get additional calc response final-----::"+txn_message);
var formPage="";
var additional = additionalCalculator();
formPage += "<div width='100%' class='c3lTitle'><a class='c3lMenuGroup' align='left'  href='wgt:979446865/1.0:rechargeMenu()'><img   width='15%'  valign='middle' resimg='icon_back.9.png' src='icon_back.9.png'/></a><span class='topText'   id='pretitle' valign='middle'  >"+BUTTON_ADDCOMISSION+"</span><hr/></div>";
formPage +=additional; 
formPage +="<div class='c3lMenuGroup buttonAllMenu1'><a id='adbut' class='c3lMenuGroup buttonAllMenu' href=\"additionalCalcultion($product,$adservices,$gateway,$amount)\"><span class='buttonText'  align='center'>"+BUTTON_CALCULATOR+"</span></a></div>"
var res=txn_message.split(",");
for(var i=0;i<res.length;i++){
formPage += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:5px;'>"+res[i]+"<hr class='gray'/></div>";

}

document.getElementById("addResponse").innerHTML = formPage;
document.getElementById("addResponse").style.display = "block";


}

function VASService(mobilenu,vasproduct,amount,pin){
widget.logWrite(7,"enter eeeeeeeeeeeeeee================================================entering entering VASService methd"+"vasproduct"+vasproduct+"mobilenu"+mobilenu+"amount"+amount+"pin"+pin);
	mobilenu  = nullorUndefCheck(mobilenu);
	product = nullorUndefCheck(vasproduct);
	amount = nullorUndefCheck(amount);
pin = nullorUndefCheck(pin);

	var str = "";
	var msisdn = "msisdn:";
       var productName = "productName:";
       var empty = " ";
       var Amount = "Amount:";
       var Pinvar = "Pin:";
       var pinencrypt ="****";
	//str += "<setvar name='pin' value=''/>";
      // str+= title();
      str+= "<div width='100%' class='c3lTitle'> <a class='c3lMenuGroup' align='left'  href='wgt:979446865/1.0:rechargeMenu()'><img   width='15%'  valign='middle' resimg='icon_back.9.png' src='icon_back.9.png'/></a><span class='topText'   id='pretitle' valign='middle'  >"+STR_VASRECHARGE+"</span><hr/></div>";
	str += "<div class='c3lMenuGroup buttonAllMenu1' style='margin-top:6%;margin-bottom:3%'><a  id='gprsbut' class='c3lMenuGroup'><span class='buttonText' align='center'>"+STR_VASRECHARGE+"</span></a></div>";
	//str +="<div class='blackBgfull' style='background-image:url("+imagePath+"card_half.9.png)' resimg='card_half.9.png'><span class='blackBgCardType'>"+buddyName+"</span><br/><span class='blackBgText'>"+buddyNumber+"</span></div>";
	//str+="<div width='100%' align:'centre'><span id='pretitle' valign='left'  style='margin-left:20%;margin-top:10%'>"+msisdn+""+empty +""+mobilenu+"</span></div>";
       //str +="<div class='inputBg1' align='center'>mobile no:<input  id='mobilenu' name='mobilenu' type='text' value='"+mobilenu+"' title='mobilenu' readonly/></div>";
       str +="<input  id='mobilenu' name='mobilenu' type='text' value='"+mobilenu+"' align='center' class='inputBg1' title='mobilenu' readonly/>";	
	str +="<input  id='vasproduct' name='vasproduct' type='numeric' value='"+vasproduct+"' align='center' class='inputBg1' emptyok='false' title='vasproduct' readonly/> ";
	str +="<input  id='amount' name='amount' type='numeric' value='"+amount+"' align='center' class='inputBg1' emptyok='false' title='amount' readonly/> ";
	str +="<input  id='pin' name='pin' type='numeric' value='"+pin+"' align='center' class='inputBg1' emptyok='false' title='pin' readonly/> ";	
       str +="<div class='c3lMenuGroup' ><a class='c3lMenuGroup buttonBgLeft' href='wgt:979446865/1.0:sendVasRechargeReq($mobilenu,$amount,$vasproduct,$pin)'><span class='buttonText'  align='center'>"+STR_CONF+"</span></a><a class='c3lMenuGroup buttonBgRight' href='wgt:979446865/1.0:VasrechargeMenu()'><span class='buttonText'  align='center'>"+STR_BACK+"</span></a></div>"
	//str +="<div id='conf'  style='background-color:white'><div class='c3lMenuGroup navConfPadding'><span id='confpaytext1' class='navConfTitle' >"+STR_CONF+"</span><hr /><div class='c3lMenuGroup marginTop15' align='center'><a class='c3lMenuGroup navBgImage' href='wgt:979446865/1.0:#vasrecharge;'><img id='cvvcancel' align='center'  class='navButtonLeft' resimg='button_cancel.png' src='"+imagePath+"button_cancel.9.png' /></a><input	class='inputBg1 navInput' align='center' id='pin' name='pin' maxLength='"+PIN_LENGTH+"' type='numpassword' title= '"+STR_PIN+"'   emptyok='false' encrypt='true' /><a class='c3lMenuGroup navBgImage' href=\"wgt:950181717/1.0:sendeditBuddyReq('"+buddyName+"','"+buddyNumber+"','"+buddyAmount+"',$buddyName,$pin)\"><img id='cvvaccept' align='center' class='navButtonRight' resimg='button_accept.9.png'	src='"+imagePath+"button_accept.9.png' /></a></div></div></div>";
	//str +="<div class='c3lMenuGroup' ><a  href=\"close:\"  class='c3lMenuGroup buttonBgLeft'><span class='buttonText'  align='center'>"+STR_CANCEL+"</span></a><a  href=\"wgt:950181717/1.0:sendeditBuddyReq('"+buddyName+"','"+buddyNumber+"','"+buddyAmount+"',$buddyName)\"  class='c3lMenuGroup buttonBgRight'><span class='buttonText'  align='center'>"+STR_SAVE+"</span></a></div>"	;
	widget.logWrite(7,"VASService V VASService VASService================================================exitttttttttttttttttt"+mobilenu);

	var divEle = document.getElementById("vasid");
	divEle.innerHTML = str;
	divEle.style.display = "block";
	widget.logWrite(7,"VASService V VASService VASService================================================exitttttttttttttttttt"+mobilenu);
	}


function VASServicesList(){
	widget.logWrite(7,"VASServicesList VASServicesList VASServicesList================================================entred");
		var str="";
		var l="";
              var product="";
		         
	
        var result="";
widget.logWrite(7,"vasProductStore================================================vasProductStore vasProductStore vasProductStore "+vasProductStore);
if("" == vasProductStore){
	selectQuery = "SELECT PRODUCT,GATEWAY,SERVICES FROM PRETUPS_PROD_SERVICES_GATEWAY WHERE OPERATOR='VasServices' ";
	result = widget.selectFromTable(selectQuery);
	
	var rootele = document.createElement ("root") ;
	rootele.innerHTML = result;
	var requestorList = rootele.getElementsByTagName("Results") ;
	var reqsLength = requestorList.length ;
	widget.logWrite(7,"Length of language:"+reqsLength);
	 var requestor_lang="";
	if(reqsLength > 0)
	{
		for(var i =0; i < reqsLength; i++){
			widget.logWrite(7,"Data of prod_services_gatway["+ i+"]");

			var product = requestorList[i].getElementsByTagName("PRODUCT")[0].textContent;
                     var gateway = requestorList[i].getElementsByTagName("GATEWAY")[0].textContent;
                      var services =  requestorList[i].getElementsByTagName("SERVICES")[0].textContent;
			
			
		}
        result=services;
	}

       product=result;
   vasProductStore=result;
}product=vasProductStore;
		
		var str="";
		var l="";
		var prdNo= product.split(",");
		var prdNo = prdNo.length;
		if(prdNo == 1){
		var text = product.split(",");
		l = product.split("_");

		str +="<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-top:10px;'><input  id='product' 	name='product' type='text' value='"+l[1]+"' class='inputBg2' emptyok='false' /></div>";

		}
		else{
		str +=" <div class='c3lMenuGroup ' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:10px;'><setvar name='product' value='product'/> <select align='left' id='product' name='product' sendIndex='false'>";
		str +="<option class='c3lMenuGroup' value='selectprd' id='selectprd'>Select product</option>";
		var text = product.split(",");
		for(var i=0;i<text.length;i++){
		                      l = text[i].split("_");
		                   str +="<option class='c3lMenuGroup' id = " +l[1] +" value= " +l[0] +">" +l[0] +"</option>";
		                }
		str +="</select></div>";
		}
//widget.logWrite(7,"================================================return retuen VASServicesList ===================="+str);
       divElement= document.getElementById("vasid");
	divElement.innerHTML = str;
	divElement.style.display = "block";
    	return str;
		}



function VasrechargeMenu()
{      

widget.logWrite(7,"Data of prod_services_gatway" +prodServices);
      	var formPage="";
		
	formPage +="<div class='c3lMenuGroup buttonAllMenu1' style='margin-top:6%'><a  id='gprsbut' class='c3lMenuGroup'><span class='buttonText' align='center'>"+STR_VASRECHARGE+"</span></a></div>";
	//formPage += "<div width='100%' class='c3lTitle marginBottom20'><a class='c3lMenuGroup' align='left' style='padding:10% 10% 10% 10%' href='#menulist'><img   width='8%'  valign='middle' src='menu.png'/></a><span class='topSize'  id='pretitle' align='center'>" + STR_PRETUPS_TITLE + "</span><a  align='right' style='padding:10% 10% 10% 10%' href='#settingslist'><img   width='10%'  valign='middle' src='mainbar.png'/></a><hr /></div>";
	//formPage += "<div width='100%' class='c3lTitle marginBottom20'><span class='topSize'  id='pretitle' align='center'>" + STR_PRETUPS_TITLE + "</span><hr /></div>";
	/*if(Number(clientVersion_actual) < Number(clientVersion_config))
	{
		formPage +="<input  id='mobilenu' 	name='mobilenu' type='mobileno' maxLength='" + MSISDN_LENGTH + "' class='inputBg2' emptyok='false' title='" + STR_MSISDN_TITLE + "'  />";
	}else
	{
		formPage += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='mobilenu' name='mobilenu' type='mobileno' value='' maxLength='" + MSISDN_LENGTH + "' class='inputBg3' emptyok='false' title='" + STR_MSISDN_TITLE + "' width='78%' /><a class='c3lMenuGroup buttonBg1'   width='17%' height='50px' href='qrcode://scan?delimiter=;&s1=$orderid$&s2=$rechid$&s3=$orderValue$&s4=$mobilenu$&s5=$amount$'><span class='redColor' align='center'>"+STR_SCANQR+"</span></a></div>";
	}*/
	
	formPage +="<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='mobilenu' 	name='mobilenu' type='mobileno' maxLength='" + MSISDN_LENGTH + "' class='inputBg3' emptyok='false' title='" + STR_MSISDN_TITLE + "'  /></div>";
    formPage +=VASServicesList();
	formPage += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='amount' 	name='amount' type='decimal' maxLength='" + AMOUNT_LENGTH + "' class='inputBg2' emptyok='false' title='" + STR_AMOUNT_TITLE + "'  /></div>";
	formPage += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input id='pin' name='pin' class='c3lMenuGroup' type='numpassword' class='inputBg2' maxLength='" + PIN_LENGTH + "' emptyok='false' title='"+STR_PIN_TITLE+"' encrypt='true' /></div>";
	
       formPage += "<div class='c3lMenuGroup' ><a class='c3lMenuGroup buttonBgLeft' href=\"VASService($mobilenu,$product,$amount,$pin)\"><span class='buttonText'  align='center'>"+STR_SUBMIT+"</span></a><a class='c3lMenuGroup buttonBgRight' href='wgt:979446865/1.0:rechargeMenu()'><span class='buttonText'  align='center'>"+STR_BACK+"</span></a></div>";
	//formPage += "<div class='c3lMenuGroup buttonAllMenu1'><a  id='gprsbut' class='c3lMenuGroup buttonAllMenu' href=\"VASService($mobilenu,$product,$amount,$pin)\"><span class='buttonText' align='center'>"+STR_SUBMIT+"</span></a></div>";
	//formPage += "<a id='smsbut' style='visibility:hidden' class='c3lMenuGroup buttonMenu' href='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$' ><span class='buttonText'  align='center'>"+STR_RECHARGE_SMS+"</span></a>";
	//formPage += "<a id='ussdbut' class='c3lMenuGroup buttonMenu' style='visibility:hidden' href='tel://call=*123*$mobilenu$*$amount$*$pin$#'><span class='buttonText' align='center'>"+STR_RECHARGE_USSD+"</span></a>";
	
	if(Number(clientVersion_actual) < Number(clientVersion_config))
	{
		formPage +=navBottom(1);// althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$'
	}
	//var mainPage = displayMain();
		
	document.getElementById("vasid").innerHTML = formPage;
	document.getElementById("vasid").style.display = "block"; 
	//document.getElementById("toast").style.display = "block";
	document.getElementById("withdraw").style.display = "block";
	document.getElementById("otherbal").style.display = "block";
	document.getElementById("transfer").style.display = "block";
	document.getElementById("billpay").style.display = "block";
	document.getElementById("returnstock").style.display = "block";
	document.getElementById("reports").style.display = "block";
	document.getElementById("giftRecharge").style.display = "block";
	document.getElementById("elecvocher").style.display = "block";
	document.getElementById("changepin").style.display = "block";
	document.getElementById("rechargeStatus").style.display = "block";
       document.getElementById("commission").style.display = "block";
       document.getElementById("vasrechargeDiv").style.display = "block";
       document.getElementById("enquiryvas").style.display = "block";
       document.getElementById("vasid").innerHTML="<setvar name='mobilenu' value=''/><setvar name='pin' value=''/>"+formPage;

	//widget.savePermanent = true;//SAVE_PERMINENT
       // return formPage;
}

function sendVasRechargeReq(mobNumber,RechargeAmount,ProductValue,Pin)
{

widget.logWrite(7,"enteres sendVasRechargeReq =============================================::");
	mobNumber 		= nullorUndefCheck(mobNumber);
	RechargeAmount 	= nullorUndefCheck(RechargeAmount);
	Pin 			= nullorUndefCheck(Pin);
	ProductValue = nullorUndefCheck(ProductValue);

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

var t = vasProductStore.split(",");
	       for(var i=0;i<t.length;i++){
	    	   var l = t[i].split("_");
	    	   if(l[0].equals(ProductValue)){
               ProductValue=l[1];	    	   }    
         }



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

		widget.logWrite(7,"url for recharge request::"+url);
		if(SEND_ENCRYPTREQ)
		{
			postData = "TYPE="+RECHARGE_TYPE+"&MSISDN="+mobile+"&Message="+getEncrypt("IMEI="+imei+"&PIN="+Pin+"&MSISDN2="+mobNumber+"&AMOUNT="+RechargeAmount+"&SELECTOR="+SELECTOR);
			
		}else
		{	//TYPE=RC&MSISDN=<MSISDN>&IMEI=<IMEI>&PIN=<PIN>&MSISDN2=<MSISDN2>&AMOUNT=<AMOUNT>&SELECTOR=<selector>&LANGUAGE1=<langcode>&LANGUAGE2=<lang code>
			postData = "TYPE="+VAS_TYPE+"&MSISDN="+mobile+"&IMEI="+imei+"&PIN="+Pin+"&MSISDN2="+mobNumber+"&AMOUNT="+RechargeAmount+"&SELECTOR="+ProductValue+"&LANGUAGE1="+langCode+"&LANGUAGE2="+langCode;
			
			//DATA=TYPE=VAS&MSISDN=7200072000&IMEI=356530065348522&PIN=1357&MSISDN2=7212345678&AMOUNT=100&SELECTOR=3&LANGUAGE1=0&LANGUAGE2=0
		}

		widget.logWrite(7," sendRechargeReq ::"+postData);

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
					widget.logWrite(7,"response for VasrechargeReq::"+xmlText);
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
widget.logWrite(7,"enteres sendVasRechargeReq sendVasRechargeReq sucess================================::");
							divElement= document.getElementById("toast");
							divElement.title = STR_TITLE;
							divElement.innerHTML = txn_message+"<setvar name='mobilenu' value=''/><setvar name='amount' value=''/><setvar name='pin' value=''/>";
							divElement.style.display = "block";
                                        widget.logWrite(7,"enteres sendVasRechargeReq sendVasRechargeReq ============::"+txn_status);

						}else
						{
widget.logWrite(7,"enteres sendVasRechargeReq sendVasRechargeReq failure fail================================::");
							divElement= document.getElementById("toast");
							divElement.title = STR_TITLE;
							divElement.innerHTML = txn_message;
							divElement.style.display = "block";
						}
					}else
					{

						str = STR_SERVER_ERROR;
						divElement= document.getElementById("toast");
						divElement.title = STR_TITLE;
						divElement.innerHTML = str;
						divElement.style.display = "block";
					}
				}else
				{
					str = STR_SERVICE_ERROR;
					divElement= document.getElementById("toast");
					divElement.title =STR_TITLE;
					divElement.innerHTML = str;
					divElement.style.display = "block";
				} 

			};

			xmlHttp.open ("POST", url , false) ;
			xmlHttp.setRequestHeader("Content-Type", SOURCE_TYPE);
			xmlHttp.setRequestHeader("Connection", "close");
			cdrStr += changetimeformat()+"| Recharge";
			xmlHttp.send (postData) ;


		}

	}
}



//added by parul for vas enquiry
function vasenquiry()
{
	widget.logWrite(7,"VASEnquiryList VASEnquiryList VASEnquiryList ================================================entered");
	var str="";
	var l="";
       var product="";
	var result="";
	
	selectQuery = "SELECT PRODUCT,GATEWAY,SERVICES FROM PRETUPS_PROD_SERVICES_GATEWAY WHERE OPERATOR='VasEnquiry' ";
	result = widget.selectFromTable(selectQuery);
	
	var rootele = document.createElement ("root") ;
	rootele.innerHTML = result;
	var requestorList = rootele.getElementsByTagName("Results") ;
	var reqsLength = requestorList.length ;
	widget.logWrite(7,"Length of language:"+reqsLength);
	var requestor_lang="";
	if(reqsLength > 0)
	{
		for(var i =0; i < reqsLength; i++)
		{
			widget.logWrite(7,"Data of prod_services_gatway["+ i+"]");
	       	var product = requestorList[i].getElementsByTagName("PRODUCT")[0].textContent;
                     var gateway = requestorList[i].getElementsByTagName("GATEWAY")[0].textContent;
                     var services =  requestorList[i].getElementsByTagName("SERVICES")[0].textContent;
		}
	       result=services;
	}

       product=result;
		
	var text ="";
	widget.logWrite(7,"VASEnquiryList ====="+product );
				
	str  +="<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-top:10px;' ><table><tr><th>" +STR_PRODUCT_NAME+  "</th><th></th><th></th><th>" +STR_PRODUCT_CODE+ "</th></tr><br>";
	
	var res=product.split(",");
		
	for (var i=0;i<res.length;i++)
		{
			text=res[i].split("_");
			str +="<tr><td>" ;	
			str +=text[0] + "</td><td></td><td></td><td>" + text[1] +"</td></tr><br>";
		}
	str +="</table></div>";
		
		
	widget.logWrite(7,"================================================return return VASEnquiryList ==================== "+str);
	return str;
}