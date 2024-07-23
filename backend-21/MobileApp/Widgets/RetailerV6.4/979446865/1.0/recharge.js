var xmlHttp = null ;
var USSD_BEARER_TYPE = 2;
var SMS_BEARER_TYPE = 3;
var widget = window.widget ;	
var bearer = widget.fetchBearerType () ;

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


var RECHARGE_STATUS_TYPE = widget.widgetProperty ("RECHARGE_STATUS_TYPE") ;
var IDENT = widget.widgetProperty ("IDENT") ;

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
      	var formPage="";
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
			str += "<setvar name='selMode' value=''/><select align='right'  style='background-color:rgb(242,242,242)' width='30%'  name='selMode' id='selMode'>" +
			"<option class='c3lMenuGroup' onclick='c3lshow:gprsbut;gprsbut1;gprsbut2;gprsbut3&action=c3lhide:ussdbut;ussdbut1;ussdbut2;ussdbut3;smsbut;smsbut1;smsbut2;smsbut3' value='gprs'>GPRS</option>" +
			"<option class='c3lMenuGroup' onclick='c3lshow:ussdbut;ussdbut1;ussdbut2;ussdbut3&action=c3lhide:gprsbut;gprsbut1;gprsbut2;gprsbut3;smsbut;smsbut1;smsbut2;smsbut3' value='ussd'>USSD</option>" +
			"<option class='c3lMenuGroup' onclick='c3lshow:smsbut;smsbut1;smsbut2;smsbut3&action=c3lhide:ussdbut;ussdbut1;ussdbut2;ussdbut3;gprsbut;gprsbut1;gprsbut2;gprsbut3' value='sms'>SMS</option></select>" ;
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
				postData = "TYPE="+RECHARGE_TYPE+"&MSISDN="+mobile+"&IMEI="+imei+"&PIN="+Pin+"&MSISDN2="+mobNumber+"&AMOUNT="+RechargeAmount+"&SELECTOR="+SELECTOR+"&LANGUAGE1="+LANGUAGE1+"&LANGUAGE2="+LANGUAGE2;
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
		postData = "TYPE="+RECHARGE_STATUS_TYPE+"&MSISDN="+mobile+"&IMEI="+imei+"&PIN="+pin+"&TXNID="+txnid+"&LANGUAGE1="+LANGUAGE1+"&IDENT="+IDENT;
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
						divElement= document.getElementById("post");
						divElement.title = STR_TITLE;
						divElement.innerHTML = txn_message+"<setvar name='txnid' value=''/><setvar name='pin' value=''/>";
						divElement.style.display = "block";

					}else
					{
						divElement= document.getElementById("post");
						divElement.title = STR_TITLE;
						divElement.innerHTML = txn_message+"<setvar name='txnid' value=''/><setvar name='pin' value=''/>";
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