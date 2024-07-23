//var imagePath = "wgt:214807648/1.0/"+widget.resourcePath+"/";

function accounts()
{
	
	var str = "";
	str +="<div   class='c3lTitle'><a class='c3lMenuGroup' href='close:'>"+BackImage+"</a><span  class='topText' id='paytitle' valign='middle' align='center' style='margin-left:-"+backImgWidth+"px'  >"+STR_ACCOUNT_TITLE+"</span><hr/></div>";
	str +="<span id='changepintext' class='recTypeText' style='margin-top:15px'>"+STR_CHANGEPIN_TEXT+"</span>";	
	str +="<input   id='oldpin' name='oldpin' type='numpassword' class='inputBg' emptyok='false' encrypt='true' maxlength= '"+PIN_LENGTH+"' title='"+STR_OLDPIN_TITLE+"' />";
	str +="<input   id='newpin' name='newpin' type='numpassword' class='inputBg' emptyok='false' encrypt='true' maxlength= '"+PIN_LENGTH+"' title='"+STR_NEWPIN_TITLE+"' />";
	str +="<input   id='cnewpin' name='cnewpin' type='numpassword' class='inputBg' emptyok='false' encrypt='true' maxlength= '"+PIN_LENGTH+"' title='"+STR_CNEWPIN_TITLE+"'/>";
	str +="<input type='checkbox' id='check' class='marginLeft20 textColor' name='check' value='' onclick=\"unmask://pwd?id=$oldpin$;$newpin$;$cnewpin$\"> "+STR_PINCHANGECHECK+"</input>";
	str +="<a class='c3lMenuGroup buttonBg'   href='wgt:950181717/1.0:sendSubsChangepinReq($oldpin,$newpin,$cnewpin,1)'><span id='' class='buttonText' align='center'> "+STR_CHANGE_TEXT+"</span></a>";
	str = [str, "<specialcache name='pretupsHome' url='wgt:264711061/1.0:pretupsHome()' type='screen'/>"].join("");
	/*var divEle = document.getElementById("accountPage");
	divEle.innerHTML = str;
	divEle.style.display = "block";*/
	return str;
	
	
}



