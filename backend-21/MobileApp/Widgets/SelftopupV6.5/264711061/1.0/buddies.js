function buddies(type,pin,response,buddylist)
{
	var str ="";
	var divEle = "";
	
	str +="<div class='c3lTitle bgColor'><a class='c3lMenuGroup' href='#pretupsHome'>"+BackImage+"</a><span  span class='topText' valign='middle' align='center' style='margin-left:-"+backImgWidth+"px' >"+STR_BUDDY_HEADING+"</span><hr/></div>";
	str += "<a id='dynamiccontent' class='c3lMenuGroup' type='ajax' href=\"ajaxBuddy('','"+buddylist+"')\">loading...</a>";
	str +="<div class='c3lNavigation bgColor' align='center'><hr class='gray'/><a id='newBuddy'  class='c3lMenuGroup' href='#addBuddy'><div><img width='15%' id='addcard' class='navAddBuddy' valign='middle' resimg='icon_addbuddy.png' src='"+imagePath+"icon_addbuddy.9.png'/><span valign='middle' class='buttonText' >"+STR_ADDBUDDY_TITLE+"</span></div></a></div>";
	str = [str,"<specialcache name='pretupsHome' url='pretupsHome()' type='screen'/>"].join("");
	//str = [str,"<specialcache name='delbuddypage' url=\"delBuddies('del')\" type='screen'/>"].join("");
	str = [str,"<specialcache name='buddyDetails' url='buddyDetails()' type='screen'/>"].join("");		
	
	divEle = document.getElementById("buddiespage"); 
	
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
	
	var pinstr = "<div style='background-color:white'><div class='c3lMenuGroup navConfPadding'><span  class='navConfTitle'>"+STR_RECH_BUDDY+"</span><hr /><div class='c3lMenuGroup marginTop15' align='center'><a class='c3lMenuGroup navBgImage' href=\"close:\"><img id='cvvcancel'  align='center' resimg='button_cancel.png'  src='"+imagePath+"button_cancel.9.png' /></a><input class='inputBg1 navInput'  align='center' id='pin' name='pin' maxLength='"+PIN_LENGTH+"' type='numpassword' title= '"+STR_PIN+"' emptyok='false' encrypt='true'/><a class='c3lMenuGroup navBgImage' href=\"wgt:950181717/1.0:sendBuddyRech($buddyName,$buddyAmount,$pin);\"><img id='cvvaccept'  align='center' resimg='button_accept.png'  src='"+imagePath+"button_accept.9.png' /></a></div></div></div>";
	var confBuddyRech = "<div   style='background-color:white'><div class='c3lMenuGroup navConfPadding'><span align='left' class='navConfTitle'>"+STR_RECH_BUDDY+"</span><hr /><a  class='c3lMenuGroup navButtonBgLeft' href=\"close:\" style='background-image:url("+imagePath+"confirmbutton.9.png)' resimg='confirmbutton.png'><span id='cancelpaytext' align='center' class='buttonText'>"+STR_CANCEL+"</span></a><a id='acceptpaytext'  class='c3lMenuGroup navButtonBgRight' style='background-image:url("+imagePath+"confirmbutton.9.png)' resimg='confirmbutton.png'	href=\"wgt:950181717/1.0:sendBuddyRech($buddyName,$buddyAmount);\"><span id='acceptpaytext' align='center' class='buttonText'>"+STR_ACCEPT+"</span></a></div></div>";
	
	document.getElementById("popuppage").innerHTML = pinstr;
	document.getElementById("popuppage").style.display = "block";
	document.getElementById("addBuddy").innerHTML = addbudd();
	document.getElementById("addBuddy").style.display = "block";
	document.getElementById("confBuddyRech").innerHTML = confBuddyRech;
	document.getElementById("confBuddyRech").style.display = "block";
	document.getElementById("addbuddpopup").style.display ="block" ;
}

function delBuddies(type)
{
	var str ="";
	var divEle = "";
	
	var buddylist = sendviewBuddyReq();
	widget.logWrite(7,"buddylist..."+buddylist);
	if(type == 'del' &&  "" == nullorUndefCheck(buddylist))
	{
		divEle = document.getElementById("toast");
		divEle.innerHTML=STR_NOBUDDIES;
		divEle.style.display = "block";

	}else
	{
		str +="<div  class='c3lTitle bgColor'><a class='c3lMenuGroup' href='#buddiespage'><img  width='15%' align='left' resimg='icon_back.png' src='"+imagePath+"icon_back.9.png'/></a><span id='confirmtext' valign='middle' align='center' class='c3lMenuGroup topText'>"+STR_BUDDY_HEADING+"</span><a  width='15%' align='right' class='c3lMenuGroup' href='#buddiespage'><img resimg='icon_delete_selected.png'   src='"+imagePath+"icon_delete_selected.9.png'/></a><hr/></div>";
	
		str += "<a id='dynamiccontent' class='c3lMenuGroup' type='ajax' href=\"ajaxBuddy('"+type+"','"+buddylist+"')\">loading...</a>";
	}
	str +="<div class='c3lNavigation bgColor' align='center'><hr class='gray'/><a   class='c3lMenuGroup' href='#addBuddy'><div><img width='15%' id='addcard' class='navAddBuddy' valign='middle' resimg='icon_addbuddy.png' src='"+imagePath+"icon_addbuddy.9.png'/><span valign='middle' class='buttonText' >"+STR_ADDBUDDY_TITLE+"</span></div></a></div>";
	//str = [str,"<specialcache name='addBuddy' url='addbudd()' type='screen'/>"].join("");
	str = [str,"<specialcache name='buddiespage' url='buddies()' type='screen'/>"].join("");
	
	var pindel ="<div  style='background-color:white'><hr class='gray'/><div class='c3lMenuGroup navConfPadding'><span   class='navConfTitle'>"+STR_DEL_BUDDY+"</span><hr /><div class='c3lMenuGroup marginTop15' align='center'><a class='c3lMenuGroup navBgImage' href=\"close://action=c3ltoggle:$$delInActive;$$delActive\"><img id='cvvcancel'  align='center' resimg='button_cancel.png' src='"+imagePath+"button_cancel.9.png' /></a><input class='inputBg1 navInput'  align='center' id='pin' name='pin' maxLength='"+PIN_LENGTH+"' type='numpassword' title= '"+STR_PIN+"'  emptyok='false' encrypt='true'/><a class='c3lMenuGroup navBgImage' href=\"wgt:950181717/1.0:senddeleteBuddyReq($buddyName,$pin);\"><img id='cvvaccept'  align='center' resimg='button_accept.png' src='"+imagePath+"button_accept.9.png' /></a></div></div></div><setvar name='pin' value=''/>";
	var confdelbuddy ="<div  style='background-color:white'><hr class='gray' /><div class='c3lMenuGroup navConfPadding'><span id='confpaytext' align='left' class='navConfTitle'>" + STR_DEL_BUDDY + "</span><hr /><a  class='c3lMenuGroup navButtonBgLeft' style='background-image:url("+imagePath+"confirmbutton.9.png)' resimg='confirmbutton.png' href=\"close://action=c3ltoggle:$$delInActive;$$delActive\" ><span id='cancelpaytext' align='center' class='buttonText'>" + STR_CANCEL + "</span></a><a id='acceptpaytext'  class='c3lMenuGroup navButtonBgRight' style='background-image:url("+imagePath+"confirmbutton.9.png)' resimg='confirmbutton.png' href=\"wgt:950181717/1.0:senddeleteBuddyReq($buddyName);\"><span id='acceptpaytext' align='center' class='buttonText'>" + STR_ACCEPT + "</span></a></div></div>";
	
	divEle = document.getElementById("delbuddypage"); 
	divEle.innerHTML = str;
	divEle.style.display = "block";
	document.getElementById("delbuddy").innerHTML = pindel;
	document.getElementById("delbuddy").style.display = "block";
	
	document.getElementById("confdelbuddy").innerHTML = confdelbuddy;
	document.getElementById("confdelbuddy").style.display = "block";
	document.getElementById("addBuddy").innerHTML = addbudd();
	document.getElementById("addBuddy").style.display = "block";
	document.getElementById("addbuddpopup").style.display ="block" ;
}

function ajaxBuddy(type,buddylist,buddyName)
{
	
	var str = "<div id='dynamiccontent' class='c3lMenuGroup'>";
	
	if(STR_NOBUDDY_RES == nullorUndefCheck(buddylist))
	{
		buddylist = "";
		
	}else if ("" == nullorUndefCheck(buddylist))
	{
		buddylist = sendviewBuddyReq();
	}
		
	buddylist = nullorUndefCheck(buddylist);
	widget.logWrite(7,"ajaxBuddy buddylist..."+buddylist);
	if("" != buddylist)
	{
				
		buddylist = buddylist.split(",");
		var buddDetail;
		
		str += "<div class='c3lMenuGroup buddyMargin' id='buddyrechdel'>";
		
		for(var i=0; i< buddylist.length;i++)
		{
			buddDetail = buddylist[i].split(" ");
			
			if(type == 'del' || type =='delete')
			{
				if( "" != nullorUndefCheck(buddDetail[0]) && "" != nullorUndefCheck(buddDetail[1]))
				{
					if(PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_NONFIN == STR_PIN_CHECK && PIN_CHECK_DELBUDDY == STR_PIN_CHECK)
					{
						str +="<a id='delInActive"+i+"' class='c3lMenuGroup' href=\"c3ltoggle:delActive"+i+";delInActive"+i+"&action=setvar://buddyName="+buddDetail[0]+"&delActive=delActive"+i+"&delInActive=delInActive"+i+"&action=Menulist:delbuddy\"><div  class='c3lMenuGroup eachbuddyMargin'><span class='buddy'>"+buddDetail[0]+"</span><br/><span class='textColor'>"+buddDetail[1]+"</span></div><span valign='middle' class='buddypipeMargin' align='right' >|</span><img   valign='middle' class='delBuddy' align='right' resimg='icon_selectuser_1.png' src='"+imagePath+"icon_selectuser_1.9.png'/><hr style='color:gray'/></a>";
						str +="<a id='delActive"+i+"' class='c3lMenuGroup' style='visibility:hidden' href=\"buddies()\"><div  class='c3lMenuGroup eachbuddyMargin'><span class='buddy'>"+buddDetail[0]+"</span><br/><span class='textColor'>"+buddDetail[1]+"</span></div><span valign='middle' class='buddypipeMargin' align='right' >|</span><img   valign='middle' class='delBuddy' align='right' resimg='icon_selectuser_2.png' src='"+imagePath+"icon_selectuser_2.9.png'/><hr style='color:gray'/></a>";
				
					}else
					{
						//str +="<a id='delInActive"+i+"' class='c3lMenuGroup' href=\"disable://control?id=$buddyrechdel$&action=c3ltoggle:delActive"+i+";delInActive"+i+";buddydelconf"+i+";newBuddy\"><div  class='c3lMenuGroup eachbuddyMargin'><span class='buddy'>"+buddDetail[0]+"</span><br/><span class='textColor'>"+buddDetail[1]+"</span></div><span valign='middle' class='buddypipeMargin' align='right' >|</span><img   valign='middle' class='delBuddy' align='right' resimg='icon_selectuser_1.png' src='"+imagePath+"icon_selectuser_1.9.png'/><hr style='color:gray'/></a>";
						//str +="<a id='delActive"+i+"' class='c3lMenuGroup' style='visibility:hidden' href=\"buddies()\"><div  class='c3lMenuGroup eachbuddyMargin'><span class='buddy'>"+buddDetail[0]+"</span><br/><span class='textColor'>"+buddDetail[1]+"</span></div><span valign='middle' class='buddypipeMargin' align='right' >|</span><img   valign='middle' class='delBuddy' align='right' resimg='icon_selectuser_2.png' src='"+imagePath+"icon_selectuser_2.9.png'/><hr style='color:gray'/></a>";
						str +="<a id='delInActive"+i+"' class='c3lMenuGroup' href=\"c3ltoggle:delActive"+i+";delInActive"+i+"&action=setvar://buddyName="+buddDetail[0]+";amount="+buddDetail[2]+"&delActive=delActive"+i+"&delInActive=delInActive"+i+"&action=Menulist:confdelbuddy\"><div  class='c3lMenuGroup eachbuddyMargin'><span class='buddy'>"+buddDetail[0]+"</span><br/><span class='textColor'>"+buddDetail[1]+"</span></div><span valign='middle' class='buddypipeMargin' align='right' >|</span><img   valign='middle' class='delBuddy' align='right' resimg='icon_selectuser_1.png' src='"+imagePath+"icon_selectuser_1.9.png'/><hr style='color:gray'/></a>";
						str +="<a id='delActive"+i+"' class='c3lMenuGroup' style='visibility:hidden' href=\"buddies()\"><div  class='c3lMenuGroup eachbuddyMargin'><span class='buddy'>"+buddDetail[0]+"</span><br/><span class='textColor'>"+buddDetail[1]+"</span></div><span valign='middle' class='buddypipeMargin' align='right' >|</span><img   valign='middle' class='delBuddy' align='right' resimg='icon_selectuser_2.png' src='"+imagePath+"icon_selectuser_2.9.png'/><hr style='color:gray'/></a>";

					}

				}
			}else if( nullorUndefCheck(buddyName) != nullorUndefCheck(buddDetail[0]) && "" != nullorUndefCheck(buddDetail[1]))
			{	
				
					if(PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_FIN == STR_PIN_CHECK && PIN_CHECK_BUDDYRECH == STR_PIN_CHECK)
					{	str +="<a class='c3lMenuGroup'   href=\"setvar://buddyName="+buddDetail[0]+"&action=Menulist:popuppage\"><div class='c3lMenuGroup eachbuddyMargin'><span class='buddy'>"+buddDetail[0]+"</span><br/><span class='textColor'>"+buddDetail[1]+"</span></div><span valign='middle' class='buddypipeMargin' align='right' >|</span><img   valign='middle' class='rechBuddy' align='right' resimg='icon_rechargebuddy.png' src='"+imagePath+"icon_rechargebuddy.9.png'/><hr style='color:gray'/></a>";
					}else
					{
						//str +="<a class='c3lMenuGroup'   href=\"setvar://buddyName="+buddDetail[0]+"&action=Menulist:confBuddyRech\"><div   class='c3lMenuGroup eachbuddyMargin'><span class='buddy'>"+buddDetail[0]+"</span><br/><span class='textColor'>"+buddDetail[1]+"</span></div><span valign='middle' class='buddypipeMargin' align='right' >|</span><img   valign='middle' class='rechBuddy' align='right' resimg='icon_rechargebuddy.9.png' src='"+imagePath+"icon_rechargebuddy.9.png'/><hr style='color:gray'/></a>";
						str +="<div width='100%' class='c3lMenuGroup'><a width='65%' class='c3lMenuGroup eachbuddyMargin' href=\"buddyDetails('"+buddDetail[0]+"','"+buddDetail[1]+"','"+buddDetail[2]+"')\"><span class='buddy'>"+buddDetail[0]+"</span><br/><span class='textColor'>"+buddDetail[1]+"</span></a><a class='c3lMenuGroup eachbuddyMargin'  href=\"setvar://buddyName="+buddDetail[0]+"&buddyAmount="+buddDetail[2]+"&action=Menulist:confBuddyRech\"><span  valign='middle' class='buddypipeMargin'>|</span><img  valign='middle' class='rechBuddy'  resimg='icon_rechargebuddy.png' src='"+imagePath+"icon_rechargebuddy.9.png'/></a><hr style='color:gray'/></div>";
					}
					

			}else
				{
				str += "<span class='bgTextAjaxResEmpty'>.</span>";// if ajax response empty
				}
			
		}	
		str += "</div>";
	}else
	{
			//str += "<span class='bgTextAjaxResEmpty'>.</span>";// if ajax response empty
			widget.logWrite(7,"NoBuddyPage"+buddylist);
			var height= widget.fetchScreenHeight();
			if("" != nullorUndefCheck(height))
			{
				height = Number(height)/2-80;
			}
			str +="<div class='c3lMenuGroup' style='margin:"+NOCARD_MARGIN+" 10 0 10'><span align='center' class='navTextSize' >"+STR_NOBUDDIESPAGE_TEXT+"</span></div>";
		
	}
	
	str += "</div>";//ajax
	if(type == 'del' || type =='delete')
	{
		divEle = document.getElementById("delbuddypage");
	}else
	{
		divEle = document.getElementById("buddiespage");	
	}
	 
	divEle.innerHTML = str;
	divEle.style.display = "block";
	document.getElementById("confBuddyRech").style.display = "block";
	

}

function buddyDetails(buddyName,buddyNumber,buddyAmount,response)
{
	var str = "";
	var divEle = "";
	var delstr = "";
	str += "<setvar name='pin' value=''/>";
	str += "<div  class='c3lTitle bgColor'><a  class='c3lMenuGroup' href='#buddiespage'>"+BackImage+"</a><span class='topText' valign='middle' align='center' style='margin-left:-"+backImgWidth+"px' >"+STR_BUDDY_HEADING+"</span><hr/></div>";

	if("" !=nullorUndefCheck(buddyName) && "" !=nullorUndefCheck(buddyNumber))
	{
		str +="<a id='tag1' class='c3lMenuGroup blackBgfull' style = 'background-image:url("+imagePath+"card_half.9.png)' resimg='card_half.9.png' href='c3ltoggle:tag1;tag2;tag3'><span class='blackBgCardType'>"+buddyName+"</span><br/><span class='blackBgText'>"+buddyNumber+"</span></a>";
		str +="<div id='tag3' class='c3lMenuGroup marginTop20'><a class='c3lMenuGroup editButton' href=\"editBuddy('"+buddyName+"','"+buddyNumber+"','"+buddyAmount+"')\" ><img align='center' class='editDelWidth' resimg='icon_edit.9.png' src='"+imagePath+"icon_edit.9.png'/></a>";
		if(PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_NONFIN == STR_PIN_CHECK && PIN_CHECK_DELBUDDY == STR_PIN_CHECK)
		{	
			str +="<a  href=\"setvar://buddyName="+buddyName+"&action=Menulist:popuppage\"  class='c3lMenuGroup deleteButton'><img class='editDelWidth' align='center' resimg='icon_delete.9.png' src='"+imagePath+"icon_delete.9.png'/></a></div>";
			delstr +="<div   style='background-color:white'><div class='c3lMenuGroup navConfPadding'><span class='navConfTitle'>"+STR_CONF+"</span><hr /><div class='c3lMenuGroup marginTop15' align='center'><a class='c3lMenuGroup navBgImage' href=\"close://action=keypad://clearall?target=$pin$\"><img  align='center' class='navButtonLeft' resimg='button_cancel.9.png' src='"+imagePath+"button_cancel.9.png' /></a><input	class='inputBg1 navInput' align='center' id='pin' name='pin' maxLength='"+PIN_LENGTH+"' type='numpassword' title= '"+STR_PIN+"' isFocused='true'  emptyok='false' encrypt='true'/><a class='c3lMenuGroup navBgImage' href=\"wgt:950181717/1.0:senddeleteBuddyReq($buddyName,$pin)\"><img class='navButtonRight' align='center' resimg='button_accept.9.png'	src='"+imagePath+"button_accept.9.png' /></a></div></div></div>";
		}else
		{
			str +="<a  class='c3lMenuGroup deleteButton' href=\"setvar://buddyName="+buddyName+"&action=Menulist:popuppage\"><img class='editDelWidth' align='center' resimg='icon_delete.9.png' src='"+imagePath+"icon_delete.9.png'/></a></div>"	;
			delstr += "<div  style='background-color:white'><div class='c3lMenuGroup navConfPadding'><span  class='navConfTitle'>"+STR_CONF+"</span><hr/><a id='back' href=\"close://action=keypad://clearall?name=$pin$\"   class='c3lMenuGroup navButtonBgLeft' style='background-image:url("+imagePath+"confirmbutton.9.png)' resimg='confirmbutton.png'><span align='center' class='buttonText' >"+STR_CANCEL+"</span></a><a  href=\"wgt:950181717/1.0:senddeleteBuddyReq($buddyName);\"   class='c3lMenuGroup navButtonBgRight' style='background-image:url("+imagePath+"confirmbutton.9.png)' resimg='confirmbutton.png'><span align='center' class='buttonText' >"+STR_DELETE+"</span></a></div></div>";
		}
		str += "<a id='tag2' class='c3lMenuGroup blackBg' width='100%' style='visibility:hidden;background-image:url("+imagePath+"card_half.9.png)' resimg='card_half.9.png' href='c3ltoggle:tag2;tag1;tag3'><span style='font-weight:bold;font-size:14px;color:white'>"+buddyName+"</span><br/><span style='font-size:10px;color:white'>"+buddyNumber+"</span></a>";

	}	
	str += "<a id='dynamiccontent' class='c3lMenuGroup' type='ajax' href=\"ajaxBuddy('','','"+buddyName+"')\">loading...</a>";
	//str +="<div  class='c3lNavigation bgColor' align='center'><hr class='gray'/><a class='c3lMenuGroup newcardMargin' href='#addBuddy'><div><img  class='navAddcard' valign='middle' resimg='icon_newcard.9.png' src='"+imagePath+"icon_newcard.9.png'/><span  class='buttonText' >"+STR_ADDBUDDY_TITLE+"</span></div></a></div>";
	str +="<div class='c3lNavigation bgColor' align='center'><hr class='gray'/><a  class='c3lMenuGroup' href='#addBuddy'><div><img width='15%'  class='navAddBuddy' valign='middle' resimg='icon_addbuddy.png' src='"+imagePath+"icon_addbuddy.9.png'/><span valign='middle' class='buttonText' >"+STR_ADDBUDDY_TITLE+"</span></div></a></div>";
	str = [str,"<specialcache name='editcard' url=\"editBuddy('"+buddyName+"','"+buddyNumber+"','"+buddyAmount+"')\" type='screen'/>"].join("");
	
	divEle = document.getElementById("buddyDetails"); 
	
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
	
	document.getElementById("addBuddy").innerHTML = addbudd();
	document.getElementById("addBuddy").style.display = "block";
	document.getElementById("addbuddpopup").style.display = "block";
	document.getElementById("popuppage").innerHTML = delstr;
	document.getElementById("popuppage").style.display = "block";
	
}

function editBuddy(buddyName,buddyNumber,buddyAmount)
{
	buddyName  = nullorUndefCheck(buddyName);
	buddyNumber = nullorUndefCheck(buddyNumber);
	buddyAmount = nullorUndefCheck(buddyAmount);
	
	var str = "";
	var pinstr = "";
	str += "<setvar name='pin' value=''/>";
	str += "<div  class='c3lTitle'><a  class='c3lMenuGroup' href='close:'>"+BackImage+"</a><span class='topText' valign='middle' align='center' style='margin-left:-"+backImgWidth+"px' >"+STR_BUDDY_HEADING+"</span><hr/></div>";
	str +="<div class='blackBgfull' style='background-image:url("+imagePath+"card_half.9.png)' resimg='card_half.9.png'><span class='blackBgCardType'>"+buddyName+"</span><br/><span class='blackBgText'>"+buddyNumber+"</span></div>";
	str +="<input  id='buddyName' name='buddyName' type='text' value='"+buddyName+"' class='inputBg' maxLength='"+NAME_LENTH+"' emptyok='false' title='buddyName' /> ";																							
	str +="<input  id='buddyAmount' name='buddyAmount' type='numeric' value='"+buddyAmount+"' class='inputBg' maxLength='"+AMOUNT_LENGTH+"' emptyok='false' title='Amount' /> ";
	if(PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_NONFIN == STR_PIN_CHECK && PIN_CHECK_BUDDYEDIT == STR_PIN_CHECK)
	{
		str +="<div class='c3lMenuGroup' ><a id='butcancel' href=\"close:\"   class='c3lMenuGroup buttonBgLeft'><span class='buttonText'  align='center'>"+STR_CANCEL+"</span></a><a id='editbut'  href='validate://targetid=$buddyName$;$buddyAmount$&action=setvar://buddyName=$buddyName$;buddyAmount=$buddyAmount$&action=Menulist:popuppage'  class='c3lMenuGroup buttonBgRight'><span class='buttonText'  align='center'>"+STR_SAVE+"</span></a></div>"	;
		pinstr +="<div id='conf'  style='background-color:white'><div class='c3lMenuGroup navConfPadding'><span id='confpaytext1' class='navConfTitle'>"+STR_CONF+"</span><hr /><div class='c3lMenuGroup marginTop15' align='center'><a class='c3lMenuGroup navBgImage' href=\"close://action=keypad://clearall?target=$pin$\"><img id='cvvcancel' align='center'  class='navButtonLeft' resimg='button_cancel.png' src='"+imagePath+"button_cancel.9.png' /></a><input	class='inputBg1 navInput' align='center' id='pin' name='pin' maxLength='"+PIN_LENGTH+"' type='numpassword' title= '"+STR_PIN+"'   emptyok='false' encrypt='true' /><a class='c3lMenuGroup navBgImage' href=\"wgt:950181717/1.0:sendeditBuddyReq('"+buddyName+"','"+buddyNumber+"','"+buddyAmount+"',$buddyName,$pin)\"><img id='cvvaccept' align='center' class='navButtonRight' resimg='button_accept.9.png'	src='"+imagePath+"button_accept.9.png' /></a></div></div></div>";
	}else
	{
		str +="<div class='c3lMenuGroup' ><a  href=\"close:\"  class='c3lMenuGroup buttonBgLeft'><span class='buttonText'  align='center'>"+STR_CANCEL+"</span></a><a  href=\"wgt:950181717/1.0:sendeditBuddyReq('"+buddyName+"','"+buddyNumber+"','"+buddyAmount+"',$buddyName)\"  class='c3lMenuGroup buttonBgRight'><span class='buttonText'  align='center'>"+STR_SAVE+"</span></a></div>"	;
	}
	
	widget.savePermanent = SAVE_PERMINENT;	
	
	var divEle = document.getElementById("editBuddy");
	divEle.innerHTML = str;
	divEle.style.display = "block";
	document.getElementById("popuppage").innerHTML = pinstr;
	document.getElementById("popuppage").style.display="block";
	
	
	
	
}
function addbudd()
{

	var str = "";
	str +="<setvar name='buddyName' value=''/><setvar name='buddyNo' value=''/><setvar name='buddyAmount' value=''/>";
	str +="<setvar name='pin' value=''/>";
	str +="<div  class='c3lTitle'><a class='c3lMenuGroup' href=\"close://action=keypad://clearall?target=$buddyName$;$buddyNo$;$buddyAmount$;$buddyName$\" >"+BackImage+"</a><span class='topText' id='paytitle' valign='middle' align='center' style='margin-left:-"+backImgWidth+"px'>"+STR_ADDBUDDY_TITLE+"</span><hr/></div>";
	str +="<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-top:25px;'><input  id='buddyName'	name='buddyName' type='text' class='inputBg' emptyok='false' maxLength='"+NAME_LENTH+"'	title='Name' /></div>";
	str +="<div  class='c3lMenuGroup'  style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='mdef' name='mdef' type='text'	title='"+COUNTRY_CODE+"' class='inputBg1 countryCode' value='' readonly /><input  emptyok='false' maxlength='"+MSISDN_LENGTH+"'	id='buddyNo' class='inputBg1 inputMobile' name='buddyNo' type='mobileno' title='' value='' /></div>";
	
	str +="<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='buddyAmount' name='buddyAmount' type='decimal' maxLength='"+AMOUNT_LENGTH+"' class='inputBg' emptyok='false' title='Amount' /></div>";
	
	if(PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_NONFIN == STR_PIN_CHECK && PIN_CHECK_ADDBUDDY == STR_PIN_CHECK)
	{
		//str +="<div class='c3lMenuGroup'><a id='butcancel' href=\"buddies()\" class='c3lMenuGroup buttonBgLeft'><span class='buttonText'  align='center'>"+STR_CANCEL+"</span></a><a id='addbuddybut' href=\"validate://targetid=$buddyName$;$buddyNo$;$buddyAmount$&action=disable://control?id=$buddyName$;$buddyNo$;$buddyAmount$;$butcancel$;$addbuddybut$&action=c3ltoggle:conf\"  class='c3lMenuGroup buttonBgRight'><span class='buttonText'  align='center'>"+STR_ADD+"</span></a></div>"	;
		//str +="<div id='conf' class='c3lNavigation'	 style='background-color:white;visibility:hidden'><hr class='gray' /><div class='c3lMenuGroup navConfPadding'><span id='confpaytext1'  class='navConfTitle'>"+STR_CONF+"</span><hr /><div class='c3lMenuGroup marginTop15' align='center'><a class='c3lMenuGroup navBgImage' href=\"enable://control?id=$buddyName$;$buddyNo$;$buddyAmount$;$butcancel$;$addbuddybut$&action=c3ltoggle:conf\"><img id='cvvcancel'  align='center' resimg='button_cancel.png'  src='"+imagePath+"button_cancel.9.png' /></a><input class='inputBg1 navInput'  align='center' id='pin' name='pin' maxLength='"+PIN_LENGTH+"' type='numpassword' title= '"+STR_PIN+"'  emptyok='false' encrypt='true'/><a class='c3lMenuGroup navBgImage' href=\"wgt:950181717/1.0:sendaddBuddyReq($buddyName,$buddyNo,$buddyAmount,$pin)\"><img id='cvvaccept'  align='center' resimg='button_accept.png' src='"+imagePath+"button_accept.9.png' /></a></div></div></div>";
		str +="<div class='c3lMenuGroup'><a id='butcancel' href=\"close://action=keypad://clearall?name=$buddyName$;$buddyNo$;$buddyAmount$;$buddyName$\" class='c3lMenuGroup buttonBgLeft'><span class='buttonText'  align='center'>"+STR_CANCEL+"</span></a><a id='addbuddybut' href=\"validate://targetid=$buddyName$;$buddyNo$;$buddyAmount$&action=setvar://buddyName=$buddyName$&buddyNo=$buddyNo$&buddyAmount=$buddyAmount$&action=Menulist:addbuddpopup\"  class='c3lMenuGroup buttonBgRight'><span class='buttonText'  align='center'>"+STR_ADD+"</span></a></div>"	;
		//pinstr +="<div id='conf'  style='background-color:white;'><hr class='gray' /><div class='c3lMenuGroup navConfPadding'><span id='confpaytext1'  class='navConfTitle'>"+STR_CONF+"</span><hr /><div class='c3lMenuGroup marginTop15' align='center'><a class='c3lMenuGroup navBgImage' href=\"close:\"><img id='cvvcancel'  align='center' resimg='button_cancel.png'  src='"+imagePath+"button_cancel.9.png' /></a><input class='inputBg1 navInput'  align='center' id='pin' name='pin' maxLength='"+PIN_LENGTH+"' type='numpassword' title= '"+STR_PIN+"'  emptyok='false' encrypt='true'/><a class='c3lMenuGroup navBgImage' href=\"wgt:950181717/1.0:sendaddBuddyReq($buddyName,$buddyNo,$buddyAmount,$pin)\"><img id='cvvaccept'  align='center' resimg='button_accept.png' src='"+imagePath+"button_accept.9.png' /></a></div></div></div>";
		
	}else
	{
		str +="<div class='c3lMenuGroup' ><a id='back' href=\"close:\"  class='c3lMenuGroup buttonBgLeft'><span class='buttonText'  align='center'>"+STR_CANCEL+"</span></a><a href=\"wgt:950181717/1.0:sendaddBuddyReq($buddyName,$buddyNo,$buddyAmount)\"   class='c3lMenuGroup buttonBgRight'><span class='buttonText'  align='center'>"+STR_ADD+"</span></a></div>";
	}
	
	
	//widget.savePermanent = SAVE_PERMINENT;
	
	/*var divEle = document.getElementById("addBuddy");
	divEle.innerHTML = str ;
	divEle.style.display = "block";
	//document.getElementById("popuppage").innerHTML = pinstr;
	document.getElementById("popuppage").style.display = "block";*/

	return str;
	
}

function toasttesting(response)
{
	widget.logWrite(7,"in buddies toast");
	
	var divElement = document.getElementById("toasttest");
	divElement.innerHTML = response ;
	divElement.style.display = "block";
}


