//var imagePath = "wgt:214807648/1.0/"+widget.resourcePath+"/";
widget.logWrite(6," Recharge imagePath.."+imagePath);
var backImgWidth = backTopTitle();
function cards(response,cardlist)//viewcardDetails,deletecard function
{

	var str= "";
	var divEle="";

	str += "<div  class='c3lTitle bgColor'><a id='backbut' class='c3lMenuGroup' href='#pretupsHome'>"+BackImage+"</a><span class='topText' valign='middle' align='center' style='margin-left:-"+backImgWidth+"px' >"+STR_CARD_TITLE+"</span><hr/></div>";
	str += "<a id='dynamiccontent' class='c3lMenuGroup' type='ajax' href=\"ajaxCards('','"+cardlist+"')\">loading...</a>";
	str +="<div id='newcard' class='c3lNavigation bgColor' align='center'><hr class='gray'/><a class='c3lMenuGroup' href='#addcardPage'><div><img id='addcard' class='navAddcard' valign='middle' resimg='icon_newcard.9.png' src='"+imagePath+"icon_newcard.9.png'/><span  class='buttonText' >"+STR_ADDCARD_TEXT+"</span></div></a></div>";
	str = [str,"<specialcache name='pretupsHome' url=\"wgt:264711061/1.0:pretupsHome()\" type='screen'/>"].join("");
	str = [str,"<specialcache name='cardDetailsPage' url=\"cardDetails()\" type='screen'/>"].join("");
	divEle = document.getElementById("cardPage"); 
	
	
	document.getElementById("addcardPage").innerHTML = addCard();
	document.getElementById("addcardPage").style.display = "block";
	document.getElementById("addcardpopup").style.display = "block";
	if ("" != nullorUndefCheck(response))
	{
		divEle.setAttribute("link","#toasttest");
		//divEle.setAttribute("href","javascript:toasttesting('"+response+"');");
		divEle.setAttribute("href","sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$");
	}
	divEle.innerHTML = str;
	divEle.style.display = "block";
	if ("" != nullorUndefCheck(response))
	{
		var divEle1 = document.getElementById("toasttest");
		divEle1.innerHTML = response;
		divEle1.style.display = "block";
	}

}

function cardDetails(cardNum,cardName,cardType,expiry,nickName,response)
{
	var str = "";
	var divEle = "";
	var delstr = "";
	str += "<div  class='c3lTitle'><a id='backbut' class='c3lMenuGroup' href='#cardPage'>"+BackImage+"</a><span class='topText' valign='middle' align='center' style='margin-left:-"+backImgWidth+"px' >"+STR_CARD_TITLE+"</span><hr/></div>";

	if(""!=nullorUndefCheck(cardNum) && ""!=nullorUndefCheck(cardName) && ""!=nullorUndefCheck(expiry) && ""!=nullorUndefCheck(nickName) )
	{

		str +="<a id='tag1' class='c3lMenuGroup blackBgfull' style = 'background-image:url("+imagePath+"card_full.9.png)' resimg='card_full.9.png' href='c3ltoggle:tag1;tag2;tag3'><div id='details' class=''><span class='blackBgCardType'>"+cardType.toUpperCase()+"</span><br/><span class='blackBgText'>"+nickName+"</span><br/>";
		str +="<div class='c3lMenuGroup blackBgGap' ><div class='c3lMenuGroup'><span class='blackBgText'>"+cardName+"</span><br/><span class='blackBgText'>"+cardNum+"</span><br/><span class='blackBgText'>Expiry "+expiry+"</span></div><img class='cardTypeWidth' align='right' resimg='icon_"+cardType.toLowerCase()+".png' src='"+imagePath+"icon_"+cardType+".9.png'></div></div></a>";
		str +="<div id='tag3' class='c3lMenuGroup marginTop20'><a id='editbut' href=\"editCardPage('"+cardName+"','"+cardNum+"','"+cardType+"','"+expiry+"','"+nickName+"')\" class='c3lMenuGroup editButton'><img align='center' class='editDelWidth' resimg='icon_edit.9.png' src='"+imagePath+"icon_edit.9.png'/></a>";
		if(PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_NONFIN == STR_PIN_CHECK && PIN_CHECK_DELCARD == STR_PIN_CHECK)
		{	
			str +="<a  id='delbut' href=\"#popuppage\"  class='c3lMenuGroup deleteButton'><img class='editDelWidth' align='center' resimg='icon_delete.9.png' src='"+imagePath+"icon_delete.9.png'/></a></div>";
			delstr +="<div id='pinconf'  style='background-color:white'><div class='c3lMenuGroup navConfPadding'><span class='navConfTitle'>"+STR_CONF+"</span><hr /><div class='c3lMenuGroup marginTop15' align='center'><a class='c3lMenuGroup navBgImage' href=\"close://action=keypad://clearall?name=$pin$\"><img  align='center' class='navButtonLeft' resimg='button_cancel.9.png' src='"+imagePath+"button_cancel.9.png' /></a><input	class='inputBg1 navInput' align='center' id='pin' name='pin' maxLength='"+PIN_LENGTH+"' type='numpassword' title= '"+STR_PIN+"' isFocused='true'  emptyok='false' encrypt='true'/><a class='c3lMenuGroup navBgImage' href=\"wgt:950181717/1.0:senddeleteCardReq('"+nickName+"',$pin);\"><img class='navButtonRight' align='center' resimg='button_accept.9.png'	src='"+imagePath+"button_accept.9.png' /></a></div></div></div><setvar name='pin' value=''/>";
		}else
		{
			str +="<a  class='c3lMenuGroup deleteButton' href='#popuppage'><img class='editDelWidth' align='center' resimg='icon_delete.9.png' src='"+imagePath+"icon_delete.9.png'/></a></div>"	;
			delstr += "<div  style='background-color:white'><div class='c3lMenuGroup navConfPadding'><span  class='navConfTitle'>"+STR_CONF+"</span><hr/><a id='back' href=\"close://action=keypad://clearall?name=$pin$\"   class='c3lMenuGroup navButtonBgLeft' style='background-image:url("+imagePath+"confirmbutton.9.png)' resimg='confirmbutton.png'><span align='center' class='buttonText' >"+STR_CANCEL+"</span></a><a  href=\"wgt:950181717/1.0:senddeleteCardReq('"+nickName+"');\"   class='c3lMenuGroup navButtonBgRight' style='background-image:url("+imagePath+"confirmbutton.9.png)' resimg='confirmbutton.png'><span align='center' class='buttonText' >"+STR_DELETE+"</span></a></div></div>";
		}
		str += "<a id='tag2' class='c3lMenuGroup blackBg' width='100%' style='visibility:hidden;background-image:url("+imagePath+"card_half.9.png)' resimg='card_half.9.png' href='c3ltoggle:tag2;tag1;tag3'><span style='font-weight:bold;font-size:14px;color:white'>"+cardType.toUpperCase()+"</span><br/><span style='font-size:10px;color:white'>"+nickName+"</span></a>";

	}	
	str += "<a id='dynamiccontent' class='c3lMenuGroup' type='ajax' href=\"ajaxCards('"+nickName+"')\">loading...</a>";
	//str +="<div id='newcard' class='c3lNavigation bgColor' align='center'><hr class='gray'/><a class='c3lMenuGroup newcardMargin' href='#addcardPage'><div><img id='addcard' class='navAddcard' valign='middle' resimg='icon_newcard.9.png' src='"+imagePath+"icon_newcard.9.png'/><span  class='buttonText' >"+STR_ADDCARD_TEXT+"</span></div></a></div>";
	str +="<div id='newcard' class='c3lNavigation bgColor' align='center'><hr class='gray'/><a class='c3lMenuGroup' href='#addcardPage'><div><img id='addcard' class='navAddcard' valign='middle' resimg='icon_newcard.9.png' src='"+imagePath+"icon_newcard.9.png'/><span  class='buttonText' >"+STR_ADDCARD_TEXT+"</span></div></a></div>";
	str = [str,"<specialcache name='editcard' url=\"editCardPage('"+cardName+"','"+cardNum+"','"+cardType+"','"+expiry+"','"+nickName+"')\" type='screen'/>"].join("");
	
	divEle = document.getElementById("cardDetailsPage"); 
	
	document.getElementById("addcardpopup").style.display = "block";
	if ("" != nullorUndefCheck(response))
	{
		divEle.setAttribute("link","#toasttest");	
		divEle.setAttribute("href","javascript:toasttesting('"+response+"');");
	}
	divEle.innerHTML = str;
	divEle.style.display = "block";

	if ("" != nullorUndefCheck(response))
	{
		var divEle1 = document.getElementById("toasttest");
		divEle1.innerHTML = response;
		divEle1.style.display = "block";
	}

	document.getElementById("popuppage").innerHTML = delstr;
	document.getElementById("popuppage").style.display = "block";
	document.getElementById("addcardPage").innerHTML = addCard();
	document.getElementById("addcardPage").style.display = "block";
}


function ajaxCards(nickName,cardsData)
{

	var str ="";
	str += "<div id='dynamiccontent' class='c3lMenuGroup'>";
	if(STR_NOCARD_RES == nullorUndefCheck(cardsData))
	{
		cardsData = ""; 

	}else if ("" == nullorUndefCheck(cardsData))
	{
		cardsData = sendviewCardReq();
	}

	cardsData = nullorUndefCheck(cardsData);
	widget.logWrite(7,"ajax cardsData::"+cardsData);
	if("" != cardsData)
	{
		cardsData = cardsData.split(STR_HASH);

		var carddes ;

		if("" != nullorUndefCheck(cardsData))
		{
			widget.logWrite(7,"cardsData::"+cardsData);

			for(var i=0; i<cardsData.length; i++)
			{
				carddes = cardsData[i].split(STR_SEMICOLON);

				if(nickName != carddes[4])
				{

					if( ""!=nullorUndefCheck(carddes[0]) && ""!= nullorUndefCheck(carddes[1]) && ""  !=nullorUndefCheck(carddes[2]) && "" !=nullorUndefCheck(carddes[3]) && "" !=nullorUndefCheck(carddes[4]))
					{
						str += "<a class='c3lMenuGroup blackBg' width='100%' style='background-image:url("+imagePath+"card_half.9.png)' resimg='card_half.9.png' href=\"cardDetails('"+carddes[0]+"','"+carddes[1]+"','"+carddes[2]+"','"+carddes[3]+"','"+carddes[4]+"')\"><div width='100%' class=''><span style='font-weight:bold;font-size:14px;color:white'>"+carddes[2].toUpperCase()+"</span><br/><span style='font-size:10px;color:white'>"+carddes[4]+"</span></div></a>";
					}


				}else
				{
					str += "<span class='bgTextAjaxResEmpty'>.</span>";// if ajax response empty
				}

			}
		}

	}
	else
	{
		widget.logWrite(7,"NoCardPage"+cardsData);
		var height= widget.fetchScreenHeight();
		if("" != nullorUndefCheck(height))
		{
			height = Number(height)/2-80;
		}
		str +="<div class='c3lMenuGroup' style='margin:"+NOCARD_MARGIN+" 10 0 10'><span align='center' class='navTextSize' >"+STR_NOCARD_TEXT+"</span></div>";
	}
	str += "</div>";//ajax end
	var menu = window.menu;	
	addMenu(menu, "back", "wgt:264711061/1.0:pretupsHome();","" , 1, 0);	

	divEle = document.getElementById("cardPage"); 
	divEle.innerHTML = str;
	divEle.style.display = "block";
	document.getElementById("addcardPage").style.display = "block";
}


function editCardPage(cardNum,cardName,cardType,expiry,nickName)
{
	var str = "";
	var pinstr = "";
	if("" != nullorUndefCheck(cardType))
	{
		cardType =  cardType.toUpperCase();
	}

	str += "<div  class='c3lTitle'><a id='backbut' class='c3lMenuGroup' href=\"close:\">"+BackImage+"</a><span class='topText' valign='middle' align='center' style='margin-left:-"+backImgWidth+"px' >"+STR_EDIT+"</span><hr/></div>";
	str +="<div class='blackBgfull' style='background-image:url("+imagePath+"card_full.9.png)' resimg='card_full.9.png'><span class='blackBgCardType'>"+cardType+"</span><br/><span class='blackBgText'>"+nickName+"</span><br/>";
	str +="<div class='c3lMenuGroup marginTop20' ><div class='c3lMenuGroup'><span class='blackBgText'>"+cardName+"</span><br/><span class='blackBgText'>"+cardNum+"</span><br/><span class='blackBgText'>Expiry "+expiry+"</span></div><img width='25%' align='right' resimg='icon_"+cardType.toLowerCase()+".png' src='"+imagePath+"icon_"+cardType.toLowerCase()+".9.png'></div></div>";
	str +="<input  id='nickName' name='nickName' type='text' value='"+nickName+"' class='inputBg' maxLength='"+NAME_LENTH+"' emptyok='false' title='NickName' /> ";																							
	if(PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_NONFIN == STR_PIN_CHECK && PIN_CHECK_EDITCARD == STR_PIN_CHECK)
	{
		//str +="<div class='c3lMenuGroup' ><a id='butcancel' href=\"cards()\"   class='c3lMenuGroup buttonBgLeft'><span class='buttonText'  align='center'>"+STR_CANCEL+"</span></a><a id='editbut'  href=\"validate://targetid=$nickName$&action=disable://control?id=$nickName$;$butcancel$;$editbut$;$backbut$&action=c3ltoggle:conf;newcard\"  class='c3lMenuGroup buttonBgRight'><span class='buttonText'  align='center'>"+STR_SAVE+"</span></a></div>"	;//href=\"disable://control?id=$nickName$;$butcancel$;$editbut$;$backbut$&action=c3ltoggle:conf;newcard\"  //href=\"validate://targetid=$nickName$&action=Menulist:editcardpin\"
		//str +="<div id='conf' class='c3lNavigation' style='background-color:white;visibility:hidden'><hr class='gray' /><div class='c3lMenuGroup navConfPadding'><span id='confpaytext1' class='navConfTitle'>"+STR_CONF+"</span><hr /><div class='c3lMenuGroup marginTop15' align='center'><a class='c3lMenuGroup navBgImage' href=\"enable://control?id=$nickName$;$butcancel$;$editbut$;$backbut$&action=c3ltoggle:conf;newcard\"><img id='cvvcancel' align='center'  class='navButtonLeft' resimg='button_cancel.png' src='"+imagePath+"button_cancel.9.png' /></a><input	class='inputBg1 navInput' align='center' id='pin' name='pin' maxLength='"+PIN_LENGTH+"' type='numpassword' title= '"+STR_PIN+"'   emptyok='false' encrypt='true' /><a class='c3lMenuGroup navBgImage' href=\"wgt:950181717/1.0:sendeditCardReq('"+cardNum+"','"+cardName+"','"+cardType+"','"+expiry+"','"+nickName+"',$nickName,$pin)\"><img id='cvvaccept' align='center' class='navButtonRight' resimg='button_accept.9.png'	src='"+imagePath+"button_accept.9.png' /></a></div></div></div>";
		str +="<div class='c3lMenuGroup' ><a id='butcancel' href=\"close:\"   class='c3lMenuGroup buttonBgLeft'><span class='buttonText'  align='center'>"+STR_CANCEL+"</span></a><a id='editbut'  href='validate://targetid=$nickName$&action=setvar://nickName=$nickName$&action=Menulist:popuppage'  class='c3lMenuGroup buttonBgRight'><span class='buttonText'  align='center'>"+STR_SAVE+"</span></a></div>"	;
		pinstr +="<div id='conf'  style='background-color:white'><div class='c3lMenuGroup navConfPadding'><span id='confpaytext1' class='navConfTitle'>"+STR_CONF+"</span><hr /><div class='c3lMenuGroup marginTop15' align='center'><a class='c3lMenuGroup navBgImage' href=\"close://action=keypad://clearall?name=$pin$\"><img id='cvvcancel' align='center'  class='navButtonLeft' resimg='button_cancel.png' src='"+imagePath+"button_cancel.9.png' /></a><input	class='inputBg1 navInput' align='center' id='pin' name='pin' maxLength='"+PIN_LENGTH+"' type='numpassword' title= '"+STR_PIN+"'   emptyok='false' encrypt='true' /><a class='c3lMenuGroup navBgImage' href=\"wgt:950181717/1.0:sendeditCardReq('"+cardNum+"','"+cardName+"','"+cardType+"','"+expiry+"','"+nickName+"',$nickName,$pin)\"><img id='cvvaccept' align='center' class='navButtonRight' resimg='button_accept.9.png'	src='"+imagePath+"button_accept.9.png' /></a></div></div></div>";
	}else
	{
		str +="<div class='c3lMenuGroup' ><a  href=\"close:\"  class='c3lMenuGroup buttonBgLeft'><span class='buttonText'  align='center'>"+STR_CANCEL+"</span></a><a  href=\"wgt:950181717/1.0:sendeditCardReq('"+cardNum+"','"+cardName+"','"+cardType+"','"+expiry+"','"+nickName+"',$nickName)\"  class='c3lMenuGroup buttonBgRight'><span class='buttonText'  align='center'>"+STR_SAVE+"</span></a></div>"	;
	}
	//str +="<div id='newcard' class='c3lNavigation bgColor marginBottom10' align='center'><hr class='gray'/><a class='c3lMenuGroup newcardMargin' href='#addcardPage'><div><img id='addcard' class='navAddcard' valign='middle' resimg='icon_newcard.9.png' src='"+imagePath+"icon_newcard.9.png'/><span  class='buttonText' >"+STR_ADDCARD_TEXT+"</span></div></a></div>";
	

	widget.savePermanent = SAVE_PERMINENT;	

	var divEle = document.getElementById("editcard");
	divEle.innerHTML = str;
	divEle.style.display = "block";
	document.getElementById("popuppage").innerHTML = pinstr+"<setvar name='pin' value=''/>";
	document.getElementById("popuppage").style.display="block";
	
}

function addCard(type)
{

	var str = "";
	var cardTitle = "";
	if (type ==1 || type == '1')
	{
		cardTitle = STR_NEWCARD_TEXT;
	}else
	{
		cardTitle = STR_ADDCARD_TEXT;	
	}
	str +="<setvar name='cardName' value=''/><setvar name='cardNum' value=''/><setvar name='cvv' value=''/><setvar name='expiry' value=''/><setvar name='nickName' value=''/><setvar name='pin' value=''>";
	
	str +="<div class='c3lTitle'>";
	str +="<a  class='c3lMenuGroup' href=\"close://action=keypad://clearall?target=$cardName$;$cardNum$;$cvv$$expiry$;$nickName$;$cardName$\">"+BackImage+"</a><span class='topText' valign='middle' align='center' style='margin-left:-"+backImgWidth+"px' >"+cardTitle+"</span><hr/></div>";
	str +="<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-top:30px;padding-bottom:5px;'><input  id='cardName' name='cardName'  type='text' class='inputBg' emptyok='false' maxLength='"+NAME_LENTH+"'	title='"+STR_CARDNAME_TITLE+"' /></div> ";
	str +="<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='cardNum' name='cardNum'  type='numeric' class='inputBg' emptyok='false' maxLength='"+CARDNO_LENGTH+"' 	title='"+STR_CARDNUM_TITLE+"' alt='"+CARDNO_TT+"' /> </div>";
	//str +="<div class='c3lMenuGroup' style='margin:0% 17% 0% 20%'><input width='25%' class='inputbg1'  tabID='cardno2' type='numeric' title='Card' id='cardno1' name='cardno1' emptyok='false' maxlength='4'/><input width='25%'class='inputbg1'  tabID='cardno3' type='numeric' title='No' id='cardno2' name='cardno2' emptyok='false' maxlength='4'/><input width='25%'class='inputbg1'  tabID='cardno4' type='numeric' title='' id='cardno3' name='cardno3' emptyok='false' maxlength='4'/><input width='25%'class='inputbg1'  type='numeric' title='' id='cardno4' name='cardno4' emptyok='false' maxlength='4'/></div>";
	if (type ==1 || type == '1')
	{
		str +="<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='cvv'name='cvv' type='numeric' class='inputBg1 cvvdim' maxLength='"+CVV_LENGTH+"' emptyok='false' title='"+STR_CVV_TITLE+"'/><input emptyok='false' id='expiry' name='expiry' class='inputBg1 expirydim'  type='text' title='Expiry' value='' href='datepicker://targetid=$date$&format=MM/YYYY' readonly/><a name='expact' id='expact' href='datepicker://targetid=$expiry$&format=MM/YYYY'><img class='contactPicker'  id='contacts' name='contacts' resimg='button_datepicker.9.png' src='"+imagePath+"button_datepicker.9.png'/></a></div>";
	}else
	{
		if(CVV_CONFIG)
		{
			str +="<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='cvv'name='cvv' type='numeric' class='inputBg1 cvvdim' maxLength='"+CVV_LENGTH+"' emptyok='false' title='"+STR_CVV_TITLE+"'/><input emptyok='false' id='expiry' name='expiry' class='inputBg1 expirydim'  type='text' title='Expiry' value='' href='datepicker://targetid=$date$&format=MM/YYYY' readonly/><a name='expact' id='expact' href='datepicker://targetid=$expiry$&format=MM/YYYY'><img class='contactPicker'  id='contacts' name='contacts' resimg='button_datepicker.9.png' src='"+imagePath+"button_datepicker.9.png'/></a></div>"; 
		}else
		{
			str +="<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input emptyok='false' id='expiry' name='expiry' class='inputBg1 expirydimNewcard'  type='text' title='Expiry' value='' href='datepicker://targetid=$date$&format=MM/YYYY' readonly/><a name='expact' id='expact' href='datepicker://targetid=$expiry$&format=MM/YYYY'><img class='contactPicker'  id='contacts' name='contacts' resimg='button_datepicker.9.png' src='"+imagePath+"button_datepicker.9.png'/></a></div>"; 
		}
	}
	str +="<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input id='nickName'name='nickName' type='text' class='inputBg' emptyok='false' maxLength='"+NAME_LENTH+"' title='"+STR_NICKNAME_TITLE+"' /></div></div>";
	if (type ==1 || type == '1')
	{
		str +="<input type='checkbox' sendIndex='false' class='marginLeft20 textColor' id='check' name='check' value='true'>"+STR_ADDCARDCHECK+"</input>";
		str +="<div class='c3lMenuGroup'><a  href=\"close:\"  class='c3lMenuGroup buttonBgLeft'><span class='buttonText'  align='center'>"+STR_CANCEL+"</span></a><a  href=\"wgt:950181717/1.0:sendSubsAdhocRechReq($msisdn,$amount,$pin,$cardName,$cardNum,$cvv,$expiry,$nickName,$check);\"  class='c3lMenuGroup buttonBgRight'><span class='buttonText'  align='center'>"+STR_PAY+"</span></a></div>"	;
		
	}else
	{
		if(PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_NONFIN == STR_PIN_CHECK && PIN_CHECK_ADDCARD == STR_PIN_CHECK)
		{
			str +="<div class='c3lMenuGroup'><a  href=\"close://action=keypad://clearall?target=$cardName$;$cardNum$;$cvv$;$expiry$;$nickName$;$cardName$\" class='c3lMenuGroup buttonBgLeft'><span class='buttonText'  align='center'>"+STR_CANCEL+"</span></a><a  href=\"validate://targetid=$cardName$;$cardNum$;$cvv$;$expiry$;$nickName$&action=setvar://cardName=$cardName$;cardNum=$cardNum$;cvv=$cvv$;expiry=$expiry$;nickName=$nickName$&action=Menulist:addcardpopup\"  class='c3lMenuGroup buttonBgRight'><span class='buttonText'  align='center'>"+STR_ADD+"</span></a></div>"	;
			//pinstr +="<div id='conf'  style='background-color:white'><hr class='gray' /><div class='c3lMenuGroup navConfPadding'><span id='confpaytext1'  class='navConfTitle'>"+STR_CONF+"</span><hr /><div class='c3lMenuGroup marginTop15' align='center'><a class='c3lMenuGroup navBgImage' href=\"close:\"><img id='cvvcancel'  align='center' resimg='button_cancel.9.png'  src='"+imagePath+"button_cancel.9.png' /></a><input class='inputBg1 navInput'  align='center' id='pin' name='pin' maxLength='"+PIN_LENGTH+"' type='numpassword' title= '"+STR_PIN+"'  emptyok='false'  encrypt='true' /><a class='c3lMenuGroup navBgImage' href=\"wgt:950181717/1.0:sendaddCardReq($cardName,$cardNum,'',$expiry,$nickName,$pin);\"><img id='cvvaccept'  align='center' resimg='button_accept.9.png'  src='"+imagePath+"button_accept.9.png' /></a></div></div></div>";

		}else
		{
			str +="<div class='c3lMenuGroup' ><a  href=\"close:\" class='c3lMenuGroup buttonBgLeft'><span class='buttonText'  align='center'>"+STR_CANCEL+"</span></a><a  href=\"wgt:950181717/1.0:sendaddCardReq($cardName,$cardNum,'',$expiry,$nickName);\"  class='c3lMenuGroup buttonBgRight'><span class='buttonText'  align='center'>"+STR_ADD+"</span></a></div>"	;	
		}
	}
	
	
	/*divEle = document.getElementById("addcardPage");
	divEle.innerHTML = str;
	divEle.style.display = "block";
	document.getElementById("popuppage").innerHTML = pinstr;
	document.getElementById("popuppage").style.display = "block";*/
	return str;
}




function toasttesting(response)
{
	widget.logWrite(7,"in toast");

	var divElement = document.getElementById("toasttest");
	divElement.innerHTML = response ;
	divElement.style.display = "block";
}
