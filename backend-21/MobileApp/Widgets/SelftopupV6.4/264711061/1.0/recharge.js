//var imagePath = "wgt:214807648/1.0/"+widget.resourcePath+"/";
//widget.logWrite(6, " Recharge imagePath.." + imagePath);
function pretupsHome() {
    widget.logWrite(6, "pretupsHome........**************");
    widget.storeUserData("regFlag", true);
   
    var str = "";
	str += "<setvar name='msisdn' value='' /><setvar name='amount' value='' /><setvar name='pin' value='' />";
    // top level navigation -- start
	widget.logWrite(6, "clientVersion........**************"+clientVersion_actual);
	widget.logWrite(6, "clientVersion_config........**************"+clientVersion_config);
	if(Number(clientVersion_actual) < Number(clientVersion_config))
	{
		 str += "<div width='100%' class='c3lTitle'><span class='topSize'  id='pretitle' align='center'>" + STR_PRETUPS_TITLE + "</span><hr /></div>";
		 str += "<div align='center' class='welcome'>	<span id='wel'>" + STR_WELCOME + "</span></div>";

		 str += "<div id='selfnav'     class='c3lMenuGroup rechMargin'><img  valign='middle' id='activeself' class='marginTop15 rechGap' resimg='recharge_active.9.png'  width='20%' src='" + imagePath + "recharge_active.9.png' /><a href='c3ltoggle:anothernav;selfnav;rechanotitle;rechtitle;rechanobut;rechbut;rechmobileip&action=keypad://clearall?name=$amount$;$pin$;$msisdn$' class='c3lMenuGroup rechGap'><img  valign='middle'	id='rechanother' width='20%' resimg='rechargefriend_inactive.9.png' src='" + imagePath + "rechargefriend_inactive.9.png'/></a><a id='topMeUp' href='c3ltoggle:topmeupnav;selfnav;tmpsubtitle;rechtitle;sendrechbut;rechbut;rechmobileip;pin&action=keypad://clearall?name=$amount$;$msisdn$' class='c3lMenuGroup rechGap'><img valign='middle' id='talkmeup' width='20%' resimg='rechargerequest_inactive.9.png' src='" + imagePath + "rechargerequest_inactive.9.png'/></a></div>";
		 str += "<div id='anothernav'  style='visibility:hidden'  class='c3lMenuGroup rechMargin'><a href='c3ltoggle:selfnav;anothernav;rechtitle;rechanotitle;rechbut;rechanobut;rechmobileip&action=keypad://clearall?name=$amount$;$pin$' class='c3lMenuGroup rechGap'><img valign='middle' id='self' width='20%' resimg='recharge_inactive.9.png' src='" + imagePath + "recharge_inactive.9.png'/></a><img  class='marginTop15 rechGap' valign='middle' id='activeanother' width='20%' resimg='rechargefriend_active.9.png' src='" + imagePath + "rechargefriend_active.9.png' /><a id='topMeUp1' href='c3ltoggle:topmeupnav;anothernav;tmpsubtitle;rechanotitle;sendrechbut;rechanobut;pin&action=keypad://clearall?name=$amount$;$msisdn$'	class='c3lMenuGroup '><img  valign='middle'	id='talkmeup1' width='20%'  resimg='rechargerequest_inactive.9.png' src='" + imagePath + "rechargerequest_inactive.9.png'/></a></div>";
		 str += "<div id='topmeupnav'  style='visibility:hidden'  class='c3lMenuGroup rechMargin'><a href='c3ltoggle:selfnav;topmeupnav;rechtitle;tmpsubtitle;rechbut;sendrechbut;rechmobileip;pin&action=keypad://clearall?name=$amount$;$pin$' class='c3lMenuGroup rechGap'><img valign='middle' id='self1' width='20%' resimg='recharge_inactive.9.png' src='" + imagePath + "recharge_inactive.9.png'/></a><a	class='c3lMenuGroup rechGap' href='c3ltoggle:anothernav;topmeupnav;rechanotitle;tmpsubtitle;rechanobut;sendrechbut;pin&action=keypad://clearall?name=$amount$;$pin$;$msisdn$'><img	 valign='middle' id='rechanother1' width='20%' resimg='rechargefriend_inactive.9.png' src='" + imagePath + "rechargefriend_inactive.9.png'/></a><img  valign='middle' id='activetakemeup' class='marginTop15 rechGap' width='20%' resimg='rechargerequest_active.9.png' src='" + imagePath + "rechargerequest_active.9.png'/></div>";
	}else
	{
		str += "<div width='100%' class='c3lTitle'><a class='c3lMenuGroup' valign='middle' align='left' style='padding:10% 10% 10% -10%' href='#menulist'><img   width='8%'  valign='middle' src='" + imagePath + "menu.png'/></a><img width='50%' src='"+imagePath+"pretups_logo.png' resimg='pretups_logo.png'/> <a class='c3lMenuGroup' valign='middle' href='#myAcc'><div class='c3lMenuGroup account'  id='pretitle'>" + STR_MYACC + "</div></a><hr/></div>";//<span class='topSize'  id='pretitle'>" + STR_PRETUPS_TITLE + "</span>
		
		str += "<div id='selfnav'     class='c3lMenuGroup rechMargin '><img  valign='middle' id='activeself' class='rechGap' resimg='recharge_active.9.png'  width='20%' src='" + imagePath + "recharge_active.9.png' /><a href='c3ltoggle:anothernav;selfnav;rechanotitle;rechtitle;rechanobut;rechbut;rechmobileip;qrself;qranother&action=keypad://clearall?target=$amount$;$pin$;$msisdn$' class='c3lMenuGroup rechGap'><img  valign='middle'	id='rechanother' width='20%' resimg='rechargefriend_inactive.9.png' src='" + imagePath + "rechargefriend_inactive.9.png'/></a><a id='topMeUp' href='c3ltoggle:topmeupnav;selfnav;tmpsubtitle;rechtitle;sendrechbut;rechbut;rechmobileip;pin;or;qrself&action=keypad://clearall?target=$amount$;$msisdn$' class='c3lMenuGroup rechGap'><img valign='middle' id='talkmeup' width='20%' resimg='rechargerequest_inactive.9.png' src='" + imagePath + "rechargerequest_inactive.9.png'/></a></div>";
	     str += "<div id='anothernav'  style='visibility:hidden'  class='c3lMenuGroup rechMargin'><a href='c3ltoggle:selfnav;anothernav;rechtitle;rechanotitle;rechbut;rechanobut;rechmobileip;qrself;qranother&action=keypad://clearall?target=$amount$;$pin$' class='c3lMenuGroup rechGap'><img valign='middle' id='self' width='20%' resimg='recharge_inactive.9.png' src='" + imagePath + "recharge_inactive.9.png'/></a><img  class='marginTop15 rechGap' valign='middle' id='activeanother' width='20%' resimg='rechargefriend_active.9.png' src='" + imagePath + "rechargefriend_active.9.png' /><a id='topMeUp1' href='c3ltoggle:topmeupnav;anothernav;tmpsubtitle;rechanotitle;sendrechbut;rechanobut;pin;or;qranother&action=keypad://clearall?target=$amount$;$msisdn$'	class='c3lMenuGroup '><img  valign='middle'	id='talkmeup1' width='20%'  resimg='rechargerequest_inactive.9.png' src='" + imagePath + "rechargerequest_inactive.9.png'/></a></div>";
	    str += "<div id='topmeupnav'  style='visibility:hidden'  class='c3lMenuGroup rechMargin'><a href='c3ltoggle:selfnav;topmeupnav;rechtitle;tmpsubtitle;rechbut;sendrechbut;rechmobileip;pin;or;qrself&action=keypad://clearall?target=$amount$;$pin$' class='c3lMenuGroup rechGap'><img valign='middle' id='self1' width='20%' resimg='recharge_inactive.9.png' src='" + imagePath + "recharge_inactive.9.png'/></a><a	class='c3lMenuGroup rechGap' href='c3ltoggle:anothernav;topmeupnav;rechanotitle;tmpsubtitle;rechanobut;sendrechbut;pin;or;qranother&action=keypad://clearall?target=$amount$;$pin$;$msisdn$'><img	 valign='middle' id='rechanother1' width='20%' resimg='rechargefriend_inactive.9.png' src='" + imagePath + "rechargefriend_inactive.9.png'/></a><img  valign='middle' id='activetakemeup' class='marginTop15 rechGap' width='20%' resimg='rechargerequest_active.9.png' src='" + imagePath + "rechargerequest_active.9.png'/></div>";
	    	
	}
    // top level navigation -- end
    // sub title start

    str += "<span id='rechtitle'  class='c3lMenuGroup recTypeText' >" + STR_SELF_RECHAGE_TITLE + "</span>";
    str += "<span  id='rechanotitle'  class='c3lMenuGroup recTypeText' style='visibility:hidden' >" + STR_ANOTHER_RECHAGE_TITLE + "</span>";
    str += "<span id='tmpsubtitle'   class='c3lMenuGroup recTypeText' style='visibility:hidden' >" + STR_TOPMEUP_TITLE + "</span>";
    // sub title end
    //input mobile-- start
    //topmerechAnother
    str += "<div id='rechmobileip' class='c3lMenuGroup ' style='visibility:hidden'>";
    str += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='mdef' name='mdef' type='text'	title='" + COUNTRY_CODE + "' class='inputBg1 countryCode' value='' readonly /><input  align='right' emptyok='false' maxlength='" + MSISDN_LENGTH + "' id='msisdn' class='inputBg1 inputMobile' name='msisdn' type='mobileno' title='' value='' encrypt='true'  /></div>";
    str += "</div>";
    //input mobile-- end
    //input amount --start
    str += "<div id='amountinput' class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='amount' 	name='amount' type='decimal' maxLength='" + AMOUNT_LENGTH + "' class='inputBg' emptyok='false' title='" + STR_AMOUNT + "' encrypt='true'  /></div>";
    //input amount --end
    //input pin -- start
    if (PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_FIN == STR_PIN_CHECK && PINCHECK_SELF_AND_ANOTHER == STR_PIN_CHECK) {
        str += "<input id='pin' name='pin' class='c3lMenuGroup' type='numpassword' class='inputBg' maxLength='" + PIN_LENGTH + "' emptyok='false' title='PIN' encrypt='true' />";
    }
    //input pin-- end

    // submit button-- start
    str += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-top:0px;'><a id='rechbut' class='c3lMenuGroup buttonRec' 	href=\"paySelf($amount,$pin,'1');\"	><span class='buttonText'  align='center'>" + STR_RECHARGENOW + "</span></a></div>";

    str += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-top:0px;'><a id='rechanobut' class='c3lMenuGroup buttonRec' style='visibility:hidden'	href=\"javascript:payOption($msisdn,$amount,$pin,'2');\"	><span class='buttonText' id='rechnow' name='rechnow' align='center'>" + STR_RECHARGENOW + "</span></a></div>";

    str += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-top:0px;'><a id='sendrechbut' class='c3lMenuGroup buttonRec' style='visibility:hidden'	href=\"wgt:950181717/1.0:sendRequest($msisdn,$amount,'','3');\"	><span class='buttonText' id='rechnow' name='rechnow' align='center'>" + STR_SENDREQUEST + "</span></a></div>";
   
    if(Number(clientVersion_actual) >= Number(clientVersion_config))
    {
    str +="<span id='or' class='c3lMenuGroup' align='center' valign='middle' width='20%' style='font-size:14px;font-weight:bold;background-image:url("+imagePath+"circle_red.9.png);color:white;padding:16 10 16 20;margin-top:10' align='center'>"+STR_OR+"</span>";
    
    str += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-top:0px;'><a id='qrself' class='c3lMenuGroup buttonRec' href=\"generateQrCode($amount);\"	><span class='buttonText' id='rechnow' name='rechnow' align='center'>Generate QR</span></a></div>";
    
    str += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-top:0px;'><a id='qranother' style='visibility:hidden' class='c3lMenuGroup buttonRec' href=\"generateQrCode($amount,$msisdn);\"	><span class='buttonText' id='rechnow' name='rechnow' align='center'>Generate QR</span></a></div>";
    
    widget.showAdvertisement(true);
    str = [str, "<div class='c3lAd'><span  align='center' style='color:red;background-color:rgb(242,242,242)' >"+ADV_STR+"</span></div>"].join("");
    }
    if(Number(clientVersion_actual) < Number(clientVersion_config))
    {
    	str += "<div id='rechNavigation'  class='c3lNavigation bgColor'><hr class='gray' /><div class='c3lMenuGroup'>";

    	if (BUDDY_MANAGEMENT) 
    	{

    		var buddyImg = imagePath + "new_buddies.9.png";
    		var buddytitle = STR_BUDDYTEXT;
    		if (PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_NONFIN == STR_PIN_CHECK && PIN_CHECK_VIEWBUDDY == STR_PIN_CHECK) 
    		{
    			str += "<a  class='c3lMenuGroup marginNav'  width='25%' href=\"#buddyPinPopUp\" align='center'><img class='navTextSize' id='mybuddies' resimg='new_buddies.png' src='" + buddyImg + "' title='" + buddytitle + "'></img></a>";

    		} else 
    		{
    			//str += "<a  class='c3lMenuGroup marginNav' 	width='25%'  href=\"wgt:950181717/1.0:sendviewBuddyReq('',1)\" align='center'><img class='navTextSize' id='mybuddies' resimg='new_buddies.png' src='" + buddyImg + "' title='" + buddytitle + "'></img></a>";
    			str += "<a  class='c3lMenuGroup marginNav' 	width='25%'  href=\"#buddiespage\" align='center'><img class='navTextSize' id='mybuddies' resimg='new_buddies.png' src='" + buddyImg + "' title='" + buddytitle + "'></img></a>";
    		}
    	}

    	if (SOS_RECHARGE == STR_ENABLE) {
    		str += "<a id='soslink' class='c3lMenuGroup marginNav' width='25%' align='center'	href='#sosPage'><img id='sos' title='" + STR_SOSTEXT + "' resimg='new_sos.png' src='" + imagePath + "new_sos.9.png'  class='navTextSize'></img></a>";
    	}
    	if (CARD_MANAGEMENT) {
    		//str += "<a id='cardlink' class='c3lMenuGroup' type='ajax' href=\"ajaxCards()\">loading...</a>";
    		var cardImg = imagePath + "new_card.9.png";
    		var cardTitle = STR_CARDSTEXT;
    		if (PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_NONFIN == STR_PIN_CHECK && PIN_CHECK_VIEWCARD == STR_PIN_CHECK)
    		{
    			str += "<a 	class='c3lMenuGroup marginNav'  width='25%'	align='center' href=\"#cardPinPopUp\"><img id='cards' title='" + cardTitle + "' resimg='new_card.png' src='" + cardImg + "' class='navTextSize'></img></a>";
    		} 
    		else
    		{
    			//  str += "<a  class='c3lMenuGroup marginNav'  width='25%'	align='center' href=\"wgt:950181717/1.0:sendviewCardReq('',1)\"><img id='cards' title='" + cardTitle + "' resimg='new_card.png' src='" + cardImg + "' class='navTextSize'></img></a>";
    			str += "<a  class='c3lMenuGroup marginNav'  width='25%'	align='center' href=\"wgt:251238406/1.0:cards()\"><img id='cards' title='" + cardTitle + "' resimg='new_card.png' src='" + cardImg + "' class='navTextSize'></img></a>";
    		}
    	}
    	str += "<a	width='25%' align='center' class='c3lMenuGroup marginNav' href='#accountPage'><img id='accounts' class='navTextSize' resimg='new_settings.9.png' src='" + imagePath + "new_settings.9.png' title='" + STR_ACCOUNTTEXT + "'></img></a>";
    	
      
       str += "</div>";
    	str += "</div>";
       
    }
    str = [str, "<specialcache name='buddiespage' url='buddies()' type='screen'/>"].join("");
    str = [str, "<specialcache name='cardPage' url='wgt:251238406/1.0:cards()' type='screen'/>"].join("");
    str = [str, "<specialcache name='voucherqr' url='voucherQRCode()' type='screen'/>"].join("");
    str = [str, "<specialcache name='paySelf' url='paySelf()' type='screen'/>"].join("");
    str = [str, "<specialcache name='payOption' url='payOption()' type='screen'/>"].join("");
    str = [str, "<specialcache name='rechReqs' url='rechargeRequests()' type='screen'/>"].join("");
    widget.savePermanent = true;// SAVE_PERMINENT;
    document.getElementById("pretupsHome").innerHTML = str;
    document.getElementById("pretupsHome").style.display = "block";
   
    document.getElementById("sosPage").style.display = "block";
    document.getElementById("sosPage").style.display = "block";
    
    document.getElementById("accountPage").innerHTML=accounts();
    document.getElementById("accountPage").style.display = "block";
  
    
    
   /* document.getElementById("menulist").innerHTML=menuList();
    document.getElementById("menulist").style.display = "block";*/
       
	document.getElementById("buddyPinPopUp").style.display = "block";
	document.getElementById("cardPinPopUp").style.display = "block";



}

function paySelf(amount, pin)
{	
	
	widget.logWrite(7, "paySelf.... start");
	var str = "";
	
	//widget.storeUserData("amount", amount); 
	//widget.storeSecureUserData("enteredpin", nullorUndefCheck(pin));
	//str += "<setvar name='msisdn' value='' /><setvar name='amount' value='' /><setvar name='pin' value='' />";	
	if(CARD_MANAGEMENT != STR_ENABLE)
	{
		window.location = "wgt:251238406/1.0/:adhocRechargePage();";
	}else
	{
		str +="<div class='c3lTitle'><div align='left'  class='c3lMenuGroup bgColor'><a class='c3lMenuGroup' href='#pretupsHome'>"+BackImage+"</a><span class='topText' id='paytitle' valign='middle' align='center' style='margin-left:-" + backImgWidth + "px'  >" + STR_PAYTITLE + "</span></div><hr/></div>";
	}
	str += "<a id='cardPayLink' class='c3lMenuGroup' type='ajax' href=\"cardPayAjax('',$amount,$pin)\">loading...</a>";
	str += "<div id='newcard' class='c3lNavigation bgColor' align='center'><hr class='gray'><a class='c3lMenuGroup'	href=\"#adhocpage\"><img id='cards2'	class='navAddcard' valign='middle' resimg='icon_newcard.9.png' src='" + imagePath + "icon_newcard.9.png'/><span id='newcardtext'	class='buttonText'>" + STR_NEWCARD + "</span></a></div>";
	//str +="<div id='newcard' class='c3lNavigation bgColor'  align='center'><hr class='gray'/><a class='c3lMenuGroup' href='addCard();'><div><img id='addcard' class='navAddcard' valign='middle' resimg='icon_newcard.9.png' src='"+imagePath+"icon_newcard.9.png'/><span valign='middle' class='buttonText' >"+STR_NEWCARD+"</span></div></a></div>";
	//var cvvstr ="<div  style='background-color:white'><div  class='c3lMenuGroup navConfPadding'><span id='confpaytext1' class='navConfTitle'>" + STR_CONFIRM_HEADING + "</span><hr/><div class='c3lMenuGroup marginTop15' align='center'><a class='c3lMenuGroup navBgImage' href=\"close://action=keypad://clearall?name=$cvv$\"><img   align='center' resimg='button_cancel.9.png' src='" + imagePath + "button_cancel.9.png' /></a><input class='inputBg1 navInput'  align='center' id='cvvNo' name='cvvNo' maxLength='" + CVV_LENGTH + "' type='numpassword' title= 'CVV'   emptyok='false' /><a class='c3lMenuGroup navBgImage' href=\"wgt:950181717/1.0:sendSubsCardRechReq($cvvNo,$nickName);\"><img   align='center'  resimg='button_accept.9.png' src='" + imagePath + "button_accept.9.png' /></a></div></div></div>";
	str = [str, "<specialcache name='pretupsHome' url='pretupsHome()' type='screen'/>"].join("");
	//str = [str, "<specialcache name='adhocpage' url='addCard(1)' type='screen'/>"].join("");
	widget.savePermanent = true;	
	document.getElementById("paySelf").innerHTML = str;
	document.getElementById("paySelf").style.display = "block";
	document.getElementById("adhocpage").innerHTML = addCard(1);
	document.getElementById("adhocpage").style.display = "block";
	document.getElementById("cvvpopup").style.display="block";
	
	widget.logWrite(7, "paySelf.... end");
}

function payOption(msisdn,amount,pin) {
	widget.logWrite(7, "payOption.... start");
		var str = "";
		var regAmount = "";
		amount = nullorUndefCheck(amount);
		msisdn = validateMSISDN(nullorUndefCheck(msisdn));
		pin = nullorUndefCheck(pin);
		/*if (amount != "") { 	widget.storeUserData("amount", amount); }
		if (pin != "") {	widget.storeSecureUserData("enteredpin", pin); }
		if(msisdn != "") { widget.storeUserData("msisdn", msisdn);	}
		*/
		
		 if (CARD_MANAGEMENT != STR_ENABLE && P2P_RECHARGE != STR_ENABLE) 
		{
			window.location = "wgt:251238406/1.0/:addCard(1);";
		} else 
		{
			if (P2P_RECHARGE == STR_ENABLE) 
			{
				
				str += "<div class='c3lTitle'><div align='left'  class='c3lMenuGroup bgColor'><a class='c3lMenuGroup' href='#pretupsHome'>"+BackImage+"</a><span class='topText' id='paytitle' valign='middle' align='center' style='margin-left:-" + backImgWidth + "px' >" + STR_PAYTITLE + "</span></div><hr/><div id='p2p'>";
				
				if ("" != nullorUndefCheck(regAmount))
				{
					str += "<a id='p2ppayment' class='c3lMenuGroup' href=\"#p2pPopUp\"><div width='100%' class='c3lMenuGroup payoption' align='center'><span id='payText' width='20%' valign='middle' style='color:white;font-size:8px' align='left'>" + STR_PAYTEXT + "</span><span align='left' valign='middle' id='amount' style='color:white;font-weight:bold;font-size: 38px' >" + regAmount + "</span><img  valign='middle' align='right'  width='8%' resimg='icon_forward.9.png' src='" + imagePath + "icon_forward.9.png' /></div></a>";
				}else 
				{
					str += "<a id='p2ppayment' class='c3lMenuGroup' href=\"#p2pPopUp\"><div class='c3lMenuGroup showbalance'><span id='payText' class='showbalanceText' width='70%' valign='middle' align='left'>" + STR_NOBAL_PAYTEXT + "</span><img id='forwardicon' width='8%' align='right' resimg='icon_forward.9.png' src='" + imagePath + "icon_forward.9.png' /></div></a>";
				}
				str += "<div  width='100%' class='paycardText'><span id='cardText' align='center'>" + STR_PAYCARD_TEXT + "</span></div></div></div>";
			} else 
			{
				str += "<div align='left'  class='c3lMenuGroup bgColor'><a href='wgt:264711061/1.0'><img  width='" + backImgWidth + "px' height='" + backImgWidth + "px' align='left' resimg='icon_back.9.png' src='" + imagePath + "icon_back.9.png'/></a><span class='topText' id='paytitle' valign='middle' align='center' style='margin-left:-" + backImgWidth + "px'  >" + STR_PAYTITLE + "</span></div><hr/>";
			}
		}
		str += "<a id='cardPayLink' class='c3lMenuGroup' type='ajax' href=\"cardPayAjax($msisdn,$amount,$pin)\">loading...</a>";
		str += "<div id='newcard' class='c3lNavigation bgColor' align='center'><hr class='gray'><a id='newcardAction'class='c3lMenuGroup' href=\"#adhocpage\"><img id='cards2'	class='navAddcard' valign='middle' resimg='icon_newcard.9.png' src='" + imagePath + "icon_newcard.9.png'/><span id='newcardtext'	class='buttonText'>" + STR_NEWCARD + "</span></a></div>";
		//var cvvstr ="<div  style='background-color:white'><div  class='c3lMenuGroup navConfPadding'><span id='confpaytext1' class='navConfTitle'>" + STR_CONFIRM_HEADING + "</span><hr/><div class='c3lMenuGroup marginTop15' align='center'><a class='c3lMenuGroup navBgImage' href=\"close:\"><img   align='center' resimg='button_cancel.9.png' src='" + imagePath + "button_cancel.9.png' /></a><input class='inputBg1 navInput'  align='center' id='cvvNo' name='cvvNo' maxLength='" + CVV_LENGTH + "' type='numpassword' title= 'CVV'   emptyok='false' /><a class='c3lMenuGroup navBgImage' href=\"wgt:950181717/1.0:sendSubsCardRechReq($cvvNo,$nickName);\"><img   align='center'  resimg='button_accept.9.png' src='" + imagePath + "button_accept.9.png' /></a></div></div></div>";
		str = [str, "<specialcache name='pretupsHome' url='pretupsHome()' type='screen'/>"].join("");
		//str = [str, "<specialcache name='adhocpage' url='addCard(1)' type='screen'/>"].join("");
		
		widget.savePermanent = true;
		document.getElementById("payOption").innerHTML = str;
		document.getElementById("payOption").style.display = "block";
		document.getElementById("adhocpage").innerHTML=addCard(1);
		document.getElementById("adhocpage").style.display = "block";
		document.getElementById("p2pPopUp").style.display = "block";
		document.getElementById("cvvpopup").style.display="block";
		
		widget.logWrite(7, "payOption.... end");
	
}

function cardPayAjax(msisdn,amount,pin)
{
	var str = "";
	var cardDetails = sendviewCardReq();
	widget.logWrite(7, "cardetails in recharge ::" + cardDetails);
   
    str +="<div id='cardPayLink' class='c3lMenuGroup'>";
	if (CARD_MANAGEMENT) {
        //var cardDetails =widget.retrieveWidgetUserData(950181717,"cards");
        if ("" != nullorUndefCheck(cardDetails) && cardDetails.indexOf(STR_SEMICOLON) > -1) {
            cardDetails = cardDetails.split(STR_HASH);
            widget.logWrite(6, "cardDetails ..." + cardDetails.length); //
            var carddes;
           
            str += "<div id='cardpayment' class='c3lMenuGroup'>";
            for (var i = 0; i < cardDetails.length; i++) {
                carddes = cardDetails[i].split(STR_SEMICOLON);
                if ("" != nullorUndefCheck(carddes[0]) && "" != nullorUndefCheck(carddes[1]) && "" != nullorUndefCheck(carddes[2]) && "" != nullorUndefCheck(carddes[3]) && "" != nullorUndefCheck(carddes[4])) {

                    str += "<a class='c3lMenuGroup'  href=\"setvar://cardNickName="+carddes[4]+"&msisdn="+msisdn+"&amount="+amount+"&pin="+pin+"&action=Menulist:cvvpopup\"><div class='c3lMenuGroup cardlist'  width='100%'><img width='15%' resimg='icon_payment_" + carddes[2].toLowerCase() + ".9.png' src='" + imagePath + "icon_payment_" + carddes[2].toLowerCase() + ".9.png'/><span style='margin:0px 6px 0px 6px;font-weight:bold'>" + carddes[4] + "</span><span style='margin:0px 6px 0px 6px'>" + carddes[0].substring(carddes[0].length - 4, carddes[0].length) + "</span><img  width='8%'  align='right' resimg='icon_forward.9.png' src='" + imagePath + "icon_forward.9.png' /></div><hr class='gray'/></a>";
                }

            }
            str += "</div>";
          
        }else
        {
        	str += "<span class='bgTextAjaxResEmpty'>.</span>";// if ajax response empty
        }
    }
	str +="</div>";
	document.getElementById("payOption").innerHTML = str;
    document.getElementById("payOption").style.display = "block";
    document.getElementById("adhocpage").style.display = "block";
}

function payStatus(respCode, message) 
{

	if (respCode == STR_SUCCESS_CODE) {
		document.getElementById("paystatus").innerHTML = STR_SUCCESS;
		document.getElementById("payinfo").innerHTML = message;
		document.getElementById("tryagainnav").style.display = "none";
	} else {
		document.getElementById("paystatus").innerHTML = STR_FAILURE;
		document.getElementById("payinfo").innerHTML = message;
		document.getElementById("tryagain").src = imagePath + "icon_tryagain.9.png";
		document.getElementById("tryagaintext").style.title = STR_TRYAGAIN;
		document.getElementById("tryagainnav").style.display = "block";
	}
	var menu = window.menu;
	addMenu(menu, "back", "wgt:264711061/1.0:pretupsHome();","" , 1, 0);
	document.getElementById("payStatus").style.display = "block";
}


/*function sos()
{

    widget.logWrite(7, "sos Page start");
    document.getElementById("sostoptext").innerHTML = STR_TOP_TEXT;
    document.getElementById("sosbottomtext").innerHTML = STR_BOTTOM_TEXT;
    var menu = window.menu;
    addMenu(menu, "back", "close:","" , 1, 0); 
    widget.savePermanent = SAVE_PERMINENT;
   
    document.getElementById("sosPage").style.display = "block";

}*/

function voucherQRCode()
{
	var str = "";
	
	//str += "<div  class='c3lTitle bgColor marginBottom20'><a  class='c3lMenuGroup' href='#pretupsHome'><img width='"+backImgWidth+"px' height='"+backImgWidth+"px'   align='left' resimg='icon_back.9.png' src='"+imagePath+"icon_back.9.png'/></a><span class='topText' valign='middle' align='center' style='margin-left:-"+backImgWidth+"px' >"+STR_PRETUPS_TITLE+"</span><hr/></div>";//str +="<div   class='c3lTitle'><a  class='c3lMenuGroup' valign='middle' href='close:'><img width='15%' align='left' resimg='icon_back.9.png' src='icon_back.9.png'/></a><img width='75%' src='pretups_logo.png' resimg='pretups_logo.png'/><hr/></div>";
	str += "<div  class='c3lTitle bgColor'><a  class='c3lMenuGroup' href='#pretupsHome'>"+BackImage+"</a><span class='topText'  id='pretitle' align='center' valign='middle' style='margin-left:-"+backImgWidth+"px'>"+STR_VOUCHER_QR_RECHARGE+"</span><hr/></div>";
    str += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-top:25px;'><input  id='mdef' name='mdef' type='text'	title='" + COUNTRY_CODE + "' class='inputBg1 countryCode' value='' readonly /><input  align='right' emptyok='false' maxlength='" + MSISDN_LENGTH + "' id='msisdn' class='inputBg1 inputMobile' name='msisdn' type='mobileno' title='' value=''  /></div>";
	str += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:0px;'><input  id='qrVoucherId' name='qrVoucherId' type='text' style='visibility:hidden'	title='" + COUNTRY_CODE + "' class='inputBg1 countryCode' value='' /></div>"; 
    str += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-top:0px;'><a href=\"validate://targetid=$msisdn$&action=qrcode://scan?text=$qrVoucherId$&action=$qrVoucherRecharge($msisdn,$qrVoucherId)$\" class='c3lMenuGroup buttonRec' ><span class='buttonText' id='rechnow' name='rechnow' align='center'>"+ STR_SCANQR +"</span></a><div>";
	str +="<span class='c3lMenuGroup' align='center' valign='middle' width='20%' style='font-size:14px;font-weight:bold;background-image:url("+imagePath+"circle_red.9.png);color:white;padding:16 10 18 20;margin-top:8; margin-bottom:8;'align='center'>"+STR_OR+"</span>";
	
       str += "<div id='amountinput' class='c3lMenuGroup'><input  id='voucherCode' 	name='voucherCode' type='decimal' maxLength='" + AMOUNT_LENGTH + "' class='inputBg' emptyok='false' title='" + STR_VOUCHERCODE_TITLE + "'  /></div>";
	str += "<div class='c3lMenuGroup'><a  class='c3lMenuGroup buttonRec' href=\"qrVoucherRecharge($voucherCode);\"	><span class='buttonText'  align='center'>"+ STR_RECHARGE +"</span></a></div>";
	
      str = [str, "<specialcache name='pretupsHome' url='pretupsHome()' type='screen'/>"].join("");
	
	document.getElementById("voucherqr").innerHTML = str;
	document.getElementById("voucherqr").style.display = "block";
}

function qrVoucherRecharge(mobile,qrVoucherId)
{
	document.getElementById("toast").innerHTML = "You has been Recharged Sucessfully";
	document.getElementById("toast").style.display = "block";
}

function rechargeRequests()
{
	var str = "";
	//str += "<div  class='c3lTitle bgColor'><a  class='c3lMenuGroup' href='pretupsHome()'><img width='"+backImgWidth+"px' height='"+backImgWidth+"px' align='left' resimg='icon_back.9.png' src='"+imagePath+"icon_back.9.png'/></a><span class='topText' valign='middle' align='center' style='margin-left:-"+backImgWidth+"px' >"+STR_PRETUPS_TITLE+"</span><hr/></div>";
	str += "<div  class='c3lTitle bgColor'><a  class='c3lMenuGroup' href='#pretupsHome'>"+BackImage+"</a><span class='topSize'  id='pretitle' align='center' style='margin-left:-"+backImgWidth+"px' >"+STR_RECH_REQUESTS+"</span><hr/></div>";
	str += "<a id='dynamiccontent' class='c3lMenuGroup' type='ajax' href=\"rechReqs()\">load...</a>";
	str = [str, "<specialcache name='pretupsHome' url='pretupsHome()' type='screen'/>"].join("");
	document.getElementById("rechReqs").innerHTML = str;
	document.getElementById("rechReqs").style.display = "block";

}

function rechReqs()
{
	var mobile = widget.retrieveWidgetUserData(950181717,"regMSISDN");
	var str = "<div id='dynamiccontent' class='c3lMenuGroup'>";
	//var query = "INSERT INTO PRETUPS_RECHARGE_REQUESTS VALUES('8550843803','7550743701','20')";
	//widget.addEditTableData(query);
	var query = "SELECT * FROM PRETUPS_RECHARGE_REQUESTS WHERE MSISDN='"+mobile+"'";
	widget.logWrite(7,"query::"+query);
	widget.logWrite(7,"values from webaxn database::"+widget.selectFromTable(query));
	//<Results><Row><MSISDN>8550843803</MSISDN><REQUESTOR_MOBILE>7550743703</REQUESTOR_MOBILE><AMOUNT>20</AMOUNT></Row><Row><MSISDN>8550843803</MSISDN><REQUESTOR_MOBILE>7550743701</REQUESTOR_MOBILE><AMOUNT>20</AMOUNT></Row></Results>
	
	var rootele = document.createElement ("root") ;
	rootele.innerHTML = widget.selectFromTable(query) ;
	var requestorList = rootele.getElementsByTagName("Row") ;
	var reqsLength = requestorList.length ;
	widget.logWrite(7,"Length of Transaction Detail:"+reqsLength);
	
	if(reqsLength > 0)
	{
		for(var i =0; i < reqsLength; i++){
			widget.logWrite(7,"Data of Transaction Detail["+ i+"]");

			var requestor_mobile = requestorList[i].getElementsByTagName("REQUESTOR_MOBILE")[0].textContent;
			var requestor_amount  = requestorList[i].getElementsByTagName("AMOUNT")[0].textContent;
			var request_id = requestorList[i].getElementsByTagName("REQUEST_ID")[0].textContent;
			widget.logWrite(7,"Length of Transaction Detail:"+requestor_mobile+" "+requestor_amount);
			if("" != requestor_mobile && "" != requestor_amount)
			{

				str +="<div width='100%' class='c3lMenuGroup  navConfPadding'><div width='60%' class='c3lMenuGroup marginLeft20'><span class='textColor'>From : </span><span class='redColor'>"+requestor_mobile+"</span><br/><span class='textColor'>Amount : "+requestor_amount+"</span></div><a class='c3lMenuGroup'  style='margin-right:5%' href=\"wgt:950181717/1.0:sendP2PRechargeReq('"+requestor_mobile+"','"+requestor_amount+"','',1)\"><img  valign='middle' width='15%'  resimg='req_accept.png' src='"+imagePath+"req_accept.png'/></a><a class='c3lMenuGroup'  href=\"requestReject('"+requestor_mobile+"','"+requestor_amount+"','"+request_id+"')\"><img width='15%' valign='middle' resimg='req_reject.png' src='"+imagePath+"req_reject.png'/></a></div><hr style='color:gray'/>";
			}else
			{
				str += "<span class='bgTextAjaxResEmpty'>.</span>";// if ajax response empty
			}
		}
	}else
	{
		str += "<span class='bgTextAjaxResEmpty'>.</span>";// if ajax response empty
	}
	
	str += "</div>";//ajax

var divEle = document.getElementById("rechReqs");
	divEle.innerHTML = str;
	divEle.style.display = "block";
}

function requestReject(requestor_mobile,amount,request_id)
{
	var query = "DELETE FROM PRETUPS_RECHARGE_REQUESTS WHERE REQUEST_ID ='"+request_id+"'";;
	var result=widget.addEditTableData(query);
	if(1 == result)
	{
		rechargeRequests();
	}else
	{
		document.getElementById("toast").innerHTML = STR_SERVER_ERROR;
		document.getElementById("toast").style.display = "block";
	}
}
/*function menuList()
{
	var str = "";
	//str +="<div width='100%' style='margin-top:8%' class='c3lTitle'><a  class='c3lMenuGroup' style='padding:10% 10% 10% 10%' valign='middle' href='close:'><img width='15%' align='left' resimg='icon_back.9.png' src='icon_back.9.png'/></a><span class='topSize'  id='pretitle'>PreTUPS</span><hr /></div>";
	str +="<div class='c3lTitle'><a  class='c3lMenuGroup' valign='middle' href='close:'><img width='20%' align='left' resimg='icon_back.9.png' src='icon_back.9.png'/></a><img width='72%' src='pretups_logo.png' resimg='pretups_logo.png'/><hr/></div>";
	str +="<a href='#buddiespage' class ='c3lMenuGroup eachMenu'  width='100%'><span align='left' valign='middle'  class='menuText'>"+STR_BUDDYTEXT+"</span><img width='15%' align='right'  src='arrow_right.png'/></a><hr class='gray'/>";
	str +="<a href='#sosPage' class ='c3lMenuGroup eachMenu'  width='100%'><span align='left' valign='middle'  class='menuText'>"+STR_SOSTEXT+"</span><img width='15%' align='right'  src='arrow_right.png'/></a><hr class='gray'/>";
	str +="<a href='wgt:251238406/1.0:cards()' class ='c3lMenuGroup eachMenu'  width='100%'><span align='left' valign='middle'  class='menuText'>"+STR_CARDSTEXT+"</span><img width='15%' align='right'  src='arrow_right.png'/></a><hr class='gray'/>";
	str +="<a href='rechargeRequests()' class ='c3lMenuGroup eachMenu'  width='100%'><span align='left'  valign='middle'  class='menuText'>"+STR_RECH_REQUESTS+"</span><img width='15%' align='right'  src='arrow_right.png'/></a><hr class='gray'/>";
	str +="<a href='voucherQRCode()' class ='c3lMenuGroup eachMenu'  width='100%'><span align='left' valign='middle'  class='menuText'>"+STR_QRVOUCHER_RECH+"</span><img width='15%' align='right'  src='arrow_right.png'/></a><hr class='gray'/>";
	str +="<a href='#accountPage' class ='c3lMenuGroup eachMenu'  width='100%'><span align='left' valign='middle'  class='menuText'>"+STR_ACCOUNTTEXT+"</span><img width='15%' align='right'  src='arrow_right.png'/></a><hr class='gray'/>";
	str +="<a href='#faqpage' class ='c3lMenuGroup eachMenu'  width='100%'><span align='left' valign='middle'  class='menuText'>"+STR_FAQ+"</span><img width='15%' align='right'  src='arrow_right.png'/></a><hr class='gray'/>";
	str +="<a href='logOut()' class ='c3lMenuGroup eachMenu'  width='100%'><span align='left' valign='middle'  class='menuText'>"+STR_LOGOUT+"</span><img width='15%' align='right'  src='arrow_right.png'/></a><hr class='gray'/>";

	return str;
	
}*/
function logOut()
{
	widget.clearUserData("regFlag");
	var dbQuery = "DELETE FROM SUBSCRIBERDATA_950181717 WHERE SUBSCRIBERID='"+widget.fetchSubscriberID()+"'";  
	 widget.addEditTableData(dbQuery);
	//widget.clearWidgetUserData(950181717,"regMSISDN");
	//widget.clearWidgetUserData(950181717,"regPIN");
	widget.clearCachedRequest("wgt:264711061/1.0/recharge.html:pretupsHome()");
	window.location = "wgt:913980753/1.0:login()";
}

function closeApp()
{
	var menu = window.menu;
	addMenu(menu,"No","#","" , 1, 0);
	addMenu(menu, "Yes","exit:","",1, 1);
	
	widget.savePermanent = SAVE_PERMINENT;
	
	var divEle = document.getElementById("closeApp");
	divEle.setAttribute("id","PreTUPS") ;
	divEle.setAttribute("type", "confirm");
	divEle.innerHTML = "Do You want to Exit?";
	divEle.style.display = "block"; 
}
function workInProgress()
{
	widget.savePermanent = true;
    var str = "WORK IN PROGRESS...";
    var divEle = document.getElementById("toast");
    divEle.innerHTML = str;
    divEle.style.display = "block";
    divEle.title = "PreTUPS";
}