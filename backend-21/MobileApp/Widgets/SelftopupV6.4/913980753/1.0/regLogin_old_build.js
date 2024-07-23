//var imagePath = "wgt:214807648/1.0/"+widget.resourcePath+"/";

function login()
{

       widget.logWrite(6, "login page===============>");
	var str = "";
	var signup = "";
	var title = "";
	var regflag = widget.retrieveWidgetUserData(264711061,"regFlag");

	if(regflag == true || regflag == "true")
	{
               widget.logWrite(6, "mobile login flag" +regflag );
		window.location = "wgt:264711061/1.0";

	}
	else
	{

		title= "<div width='100%' class='c3lTitle'><img width='50%' align='center' src='"+imagePath+"pretups_logo.png' resimg='pretups_logo.png'/><hr/></div>";		//<span class='topSize'   align='center'>" + STR_PRETUPS_TITLE + "</span>	

		str +="<div  class='c3lMenuGroup' width='100%'><a id='signIn' width='49%'  align='left'class='c3lMenuGroup marginBottom20' href='disable://control?id=$signIn$&action=enable://control?id=$signUp$&action=c3ltoggle:mobile;ppin;signinbut;signUpnav;mobileip;email;pin;signUpbut;hr1;hr2;;gplus;fb'><span align='center'  style='font-weight:bold;padding:10% 10% 10% 10%' class='textColor'>"+STR_SIGNIN+"</span></a><span style='color:gray;font-size:14px;padding:10% 0% 10% 0%'>|</span><a id='signUp'class='c3lMenuGroup' href='enable://control?id=$signIn$&action=disable://control?id=$signUp$&action=c3ltoggle:mobile;ppin;signinbut;signUpnav;mobileip;email;pin;signUpbut;hr1;hr2;gplus;fb'  valign='middle' width='49%' align='right'><span style='font-weight:bold;padding:10% 10% 10% 10%' align='center' class='textColor'>"+STR_SIGNUP+"</span></a></div>";
		str +="<div class='c3lMenuGroup marginBottom20' style='margin-top:-20px' valign='top'><hr id='hr1' valign='top' width='49%' align='left' class='c3lMenuGroup'/><hr id='hr2' valign='top' align='right' style='visibility:hidden' width='49%' class='c3lMenuGroup'/></div>";


		//str +="<div id='signInnav' class='c3lMenuGroup marginBottom20' width='100%'><div width='49%' class='c3lMenuGroup' align='left'><span align='center'  style='font-weight:bold;padding:10% 10% 10% 10%' class='textColor'>Sign In</span><hr id='hr1' class='c3lMenuGroup'/></div><span style='color:gray;font-size:14px;padding:10% 0% 10% 0%'>|</span><a href='c3ltoggle:signuppage;signInnav;mobile;ppin;signinbut;signUpnav;mobilenu;email;pin;signUpbut' class='c3lMenuGroup'  valign='middle' width='49%' align='right'><span style='font-weight:bold;padding:10% 10% 10% 10%' align='center' class='textColor'>Sign Up</span></a></div>";			
		str += "<input  id='mobile' name='mobile' type='numeric' maxLength='" + MSISDN_LENGTH + "' class='inputBg' emptyok='false' title='Email or Mobile' alt='"+MSISDN_TT+"'/>";
		str += "<input id='ppin' name='ppin'  type='numpassword' class='inputBg' maxLength='" + PIN_LENGTH + "' emptyok='false' title='PreTUPS PIN' encrypt='true' alt='"+PIN_TT+"'/>";
		str += "<div id='signinbut' class='c3lMenuGroup'>";
		str += "<a class='c3lMenuGroup buttonBg' 	href=\"wgt:950181717/1.0:regCheck($mobile,$ppin,$%IMEI%);\" 	><span class='buttonText' align='center'>" + STR_LOGIN + "</span></a>";
		str += "<a align='center' width='50%' href='#forgot' style='font-size:10px;color:red;padding:10% 10% 10% 8%'>Forgot PIN?</a>";
		//str += "<a align='right' width='50%' href='wgt:165439271/1.0' style='font-size:10px;color:red;padding:10% 10% 10% 10%'>Indrajit</a>";
		//str += "<a align='right' width='50%' href='wgt:264541829/1.0' style='font-size:10px;color:red;padding:10% 10% 10% 10%'>Indrajit</a>";

		str += "</div>";

		//str += "<a class='c3lMenuGroup' href='wgt:165439271/1.0'>Indrajit</a>";
		str += "<div class='c3lNavigation marginBottom20'>";
		str += "<a id='gplus' class='c3lMenuGroup' align='center' width='70%'   href='login://gplus?authcode=$tokenID$&read_permission=email&action=wgt:950181717/1.0:getGooglePlusAccessToken($tokenID);'><div  class='c3lMenuGroup gplustext' ><img width='15%' align='left'  src='"+imagePath+"googleplus_icon.png' resimg='googleplus_icon.png'/><span  valign='middle'>"+GPLUS_TEXT+"</span><div></a><br/>";
               //str += "<a id='gplus' class='c3lMenuGroup' align='center' width='70%'   href='login://gplus?token=$tokenID$&action=$wgt:950181717/1.0:getGooglePlusAccessToken($tokenID)$;'><div  class='c3lMenuGroup gplustext' ><img width='15%' align='left'  src='"+imagePath+"googleplus_icon.png' resimg='googleplus_icon.png'/><span  valign='middle'>"+GPLUS_TEXT+"</span><div></a><br/>";

		str += "<a id='fb'    class='c3lMenuGroup' align='center' width='70%'   href='login://fb?token=$accessTok$&read_permission=email&publish_permission=publish_action&action=wgt:913980753/1.0:getAccessToken($accessTok);'><div  class='c3lMenuGroup fbtext'><img width='15%' align='left'  src='"+imagePath+"facebook_icon.png' resimg='facebook_icon.png'/><span  valign='middle'>"+FB_TEXT+"</span><div></a>";
		str += "</div>";


		
		//signup +="<div id='signUpnav' style='visibility:hidden' class='c3lMenuGroup marginBottom20' width='100%'><a href='c3ltoggle:signuppage;signInnav;mobile;ppin;signinbut;signUpnav;mobilenu;email;pin;signUpbut' width='49%' class='c3lMenuGroup' align='left'><span align='center'  style='font-weight:bold;padding:10% 10% 10% 10%' class='textColor'>Sign In</span></a><span style='color:gray;font-size:14px;padding:10% 0% 10% 0%'>|</span><div  class='c3lMenuGroup'  valign='middle' width='49%' align='right'><span style='font-weight:bold;padding:10% 10% 10% 10%' align='center' class='textColor'>Sign Up</span><hr/></div></div>";			
		str += "<div id='mobileip' class='c3lMenuGroup marginLeft20' style='margin-right:17%;visibility:hidden'>";
	    str += "<input  id='mdef' name='mdef' type='text'	title='" + COUNTRY_CODE + "' class='inputBg1 countryCode' value='' readonly /><input  align='right' emptyok='false' maxlength='" + MSISDN_LENGTH + "' id='mobilenu' name='mobilenu' isFocus='true' class='inputBg1 inputMobile'  type='mobileno' title='' value=''  />";
	    str += "</div>";
		//signup += "<input  id='mobilenu' style='visibility:hidden'	name='mobilenu' type='numeric' maxLength='" + MSISDN_LENGTH + "' class='inputBg' emptyok='false' title='" + STR_MSISDN_TITLE + "' alt='"+MSISDN_TT+"'  />";
		signup += "<input  id='email' style='visibility:hidden'	name='email' type='email' maxLength='50' class='inputBg' emptyok='false' title='Email'  />";
		signup += "<input id='pin' name='pin' style='visibility:hidden'  type='numpassword' class='inputBg' maxLength='" + PIN_LENGTH + "' emptyok='false' title='Create PreTUPS PIN' encrypt='true' alt='"+PIN_TT+"' />";
		signup += "<div id='signUpbut' style='visibility:hidden' class='c3lMenuGroup'>";
		//signup += "<span style='color:red;font-size:8px;font-style=italic;margin-right:17%' align='right' >PIN should be 4 digits.all numbers</span>";
		signup += "<div class='c3lMenuGroup' style='margin:5% 17% 5% 20%'><span style='color:rgb(94,94,96)'>"+TERMS_TEXT1+"</span><a style='color:red' href='#termsPage'><br/><span align='center'>"+TERMS_TEXT2+"</span></a></div>";
		signup += "<a id='signupbut' class='c3lMenuGroup buttonBg' href=\"wgt:950181717/1.0:SubscriberRegReq($mobilenu,$pin,$%IMEI%,$email);\" althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$'	><span class='buttonText' align='center'>"+STR_SIGNUP+"</span></a>";
              //signup += "<a id='signupbut' class='c3lMenuGroup buttonBg' href=\"wgt:950181717/1.0:SubscriberRegReq($%IMEI%,$ucode,$mobilenu);\" althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$'	><span class='buttonText' align='center'>"+STR_SIGNUP+"</span></a>";

		signup +="</div>";
	
		document.getElementById("login").innerHTML = title+ str +signup;
		document.getElementById("login").style.display = "block";

		var forgot= "<div   class='c3lTitle marginBottom20'><a class='c3lMenuGroup' href='close:'>"+BackImage+"</a><span  class='topText' id='paytitle'  align='center' valign='middle' style='margin-left:-"+backImgWidth+"px'>"+FORGOTPIN_TEXT+"</span><hr/></div>";
		forgot +="<span  style='margin:0% 17% 20% 20%;color:rgb(94,94,96)'>"+FORGOT_SMS_TEXT+"</span>";
		forgot += "<input  id='mobilenu' name='mobilenu' type='numeric' maxLength='" + MSISDN_LENGTH + "' class='inputBg' emptyok='false' title='"+MOBILEOREMAIL_TT+"'  />";
		forgot += "<a class='c3lMenuGroup buttonBg' href=\"verify($mobilenu,$pin,$%IMEI%);\" althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$'	><span class='buttonText' align='center'>"+STR_PROCEED+"</span></a>";

		document.getElementById("forgot").innerHTML= forgot;
		document.getElementById("forgot").style.display = "block";

		var terms = "<div   class='c3lTitle marginBottom20'><a class='c3lMenuGroup' href='close:'>"+BackImage+"</a><span  class='topText' id='paytitle' valign='middle' align='center' style='margin-left:-"+backImgWidth+"px'>"+STR_TERMS_TEXT+"</span><hr/></div>";
		terms +="<div>"+termsText()+"</div>";
		document.getElementById("termsPage").innerHTML= terms;
		document.getElementById("termsPage").style.display = "block";
	}
}



function terms()
{

	/*document.getElementById("termssubtitle").innerHTML=STR_TERM_SUBTITLE;
	document.getElementById("acceptance").innerHTML=STR_ACCEPTANCE;
	document.getElementById("acceptancetext").innerHTML=STR_ACCEPTANCE_TEXT;
	document.getElementById("acceptancesubtext").innerHTML=STR_ACCEPTANCE_TEXT_SUB;
	document.getElementById("advicement").innerHTML=STR_ADVICEMENT;
	document.getElementById("advicementtext").innerHTML=STR_ADVICEMENT_TEXT;
	document.getElementById("advicementsubtext").innerHTML=STR_ADVICEMENT_TEXT_SUB;
	document.getElementById("terms").style.display = "block";*/

	var str = "";
	var regflag = widget.retrieveWidgetUserData(264711061,"regFlag");
	var UCODE = Math.floor((Math.random() * 10000000000) + 1);
	var IMEI = widget.getHeader("imei");
	UCODE = "1234567890";
	//IMEI  = "358422050496045";
	widget.logWrite(7,"regFlag Value::"+regflag);
	
	if(regflag == true || regflag == "true")
	{
		window.location = "wgt:264711061/1.0";

	}
	else
	{
		str +="<div  align='left' width='100%' class='c3lTitle bgColor topMargin'><span  class='topSize' align='center' valign='middle' >"+STR_TERMS_HEAD+"</span><hr></div>";
		str +="<div><span id='termssubtitle' class='marginLeft20 termsubtitle'>"+STR_TERM_SUBTITLE+"</span></div>";
	    str +="<div>"+termsText()+"</div>";
		//str +="<div class='c3lNavigation'><div class='c3lMenuGroup'><a width='50%' style='margin-right:7%' href=\"exit:\" class='c3lMenuGroup buttonBg1'><span class='buttonText'  align='center'>"+STR_CANCEL+"</span></a><a id='back' href=\"sms://to=+919986027668&text=pretups IMEI="+IMEI+";UCODE="+UCODE+"&encrypt=false&action=wgt:950181717/1.0:SubscriberRegReq('"+IMEI+"','"+UCODE+"');\"  width='49%' class='c3lMenuGroup buttonBg1'><span class='buttonText'  align='center'>"+STR_AGREE+"</span></a></div></div>";
		 str +="<div class='c3lNavigation'><div class='c3lMenuGroup'><a width='50%' style='margin-right:7%' href=\"exit:\" class='c3lMenuGroup buttonBg1'><span class='buttonText'  align='center'>"+STR_CANCEL+"</span></a><a id='back' href=\"wgt:950181717/1.0:SubscriberRegReq('"+IMEI+"','"+UCODE+"');\"  width='49%' class='c3lMenuGroup buttonBg1'><span class='buttonText'  align='center'>"+STR_AGREE+"</span></a></div></div>";
		//str = [str,"<specialcache name='terms' url='termscache()' type='screen'/>"].join("");
		
		document.getElementById("terms").innerHTML = str;
		document.getElementById("terms").style.display = "block";
	}
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


function createPIN(smspin)
{
	/*document.getElementById("pin").title=STR_PIN;
	document.getElementById("cpin").title=STR_CONFIRMPIN;
	document.getElementById("createpin").innerHTML=STR_FINALLY;
	document.getElementById("createPIN").style.display = "block";*/
	
	var str = "";
	var divEle = "";
	
	str +="<setvar name='pin' value=''/><setvar name='cpin' value=''/>";
	str +="<div  align='left' width='100%' class='c3lMenuGroup bgColor marginBottom20'><a href='javascript:terms();'><img  width='"+backImgWidth+"px' height='"+backImgWidth+"px' align='left' resimg='icon_back.9.png' src='"+imagePath+"icon_back.9.png'/></a><span class='topText' id='paytitle' valign='middle' align='center' style='margin-left:-"+backImgWidth+"px'>"+STR_CREATEPIN+"</span><hr></div>";
	str +="<input  class='inputBg' id='pin' name='pin' type='numpassword'	maxLength='"+PIN_LENGTH+"' encrypt='true' emptyok='false' title='"+STR_PIN+"' />";
	str +="<input  	class='inputBg' id='cpin' name='cpin' type='numpassword'	maxLength='"+PIN_LENGTH+"' encrypt='true' emptyok='false' title='"+STR_CONFIRMPIN+"' />";
	str +="<a href=\"wgt:950181717/1.0:sendSubsChangepinReq('"+smspin+"',$pin,$cpin);\" class='c3lMenuGroup buttonBg'><span class='buttonText' id='createpin' align='center'>"+STR_FINALLY+"</span></a>";	
	
	var menu = window.menu;
	addMenu(menu, "back", "wgt:913980753/1.0:terms();","" , 1, 0);	
	
	str = [str,"<specialcache name='createPIN' url='createpincache()' type='screen'/>"].join("");
	
	widget.savePermanent = SAVE_PERMINENT;
	
	divEle = document.getElementById("createPIN");
	divEle.innerHTML=str;
	divEle.style.display = "block";	
}

function getAccessToken(accessTok){
    
  /*  widget.logWrite(6, "handleFacebookLogin Access Token ********** : "+accessTok);
    var fbInfoUrl = "https://graph.facebook.com/v2.0/me?access_token="+accessTok;
    xmlHttpReg = new XMLHttpRequest();
    xmlHttpReg.open( "GET", fbInfoUrl, false );
    xmlHttpReg.send( null );
    obj = eval('(' + xmlHttpReg.responseText + ')');
    widget.logWrite(6, "handleFacebookLogin FB API RESPONSE ******** NAME : "+obj.name+" ----- EMAIL "+obj.email);

    var name = obj.name;
    var email = obj.email;
    
    
    if(obj == null || obj == undefined || obj == "undefined" || obj.email == null || obj.email == undefined || obj.email == "undefined"){
    
                    alertCode("#","User Email to Login");
                    
                    return false;
    }

else {

document.getElementById("toast").innerHTML = "Failed";
document.getElementById("toast").style.display = "block";
    }
*/


   // var reqtime=storeStartTime();
    var fbInfoUrl = "https://graph.facebook.com/v2.0/me?access_token=" + accessTok;
    var xmlHttpReg = new XMLHttpRequest();
    xmlHttpReg.open("GET", fbInfoUrl, false);
    xmlHttpReg.send(null);
    obj = eval('(' + xmlHttpReg.responseText + ')');

    var name = obj.name;
    var email = obj.email;


    
        var url = widget.widgetProperty("GET_DEALS_URL");
        var postData = "method=zerchLogin&emailId=" + email + "&password=&name=" + name + "&loginType=FB&accessToken=" + accessTok + "&tokenExpiry=";

        var xmlHttpReg = new XMLHttpRequest();

        if (xmlHttpReg) {
            xmlHttpReg.onreadystatechange = function() {
                if (4 == xmlHttpReg.readyState) {
                    resText = xmlHttpReg.responseText;
                }

            };
            xmlHttpReg.open('POST', url, false);
            xmlHttpReg.send(postData);
        }

        var response = eval('(' + resText + ')');
        if (response.result[0].txnstatus == '200') {
            widget.storeWidgetUserData(264711061, "USERID", response.result[0].userId);
            widget.storeWidgetUserData(264711061, "LOGINTYPE", response.result[0].loginType);
            //cdrcommon(reqtime,imei,subscriberId,"FACEBOOK LOGIN","","","FACEBOOK LOGIN SUCCESSFULL","","200");
             //window.location = "wgt:212135013/1.0:home_page()";
              widget.logWrite(6, "facebook login");

		 window.location = "wgt:264711061/1.0";
        }

    


}


function getGooglePlusAccessToken(tokenID) {
widget.logWrite(6, "GooglePlus login using schema ***");

var url = widget.widgetProperty("GET_DEALS_URL");

widget.logWrite(6, "token***" +tokenID);

    var googleplusInfoUrl = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token="+tokenID;
    xmlHttpReg = new XMLHttpRequest();
    xmlHttpReg.open( "GET", googleplusInfoUrl, false );
    xmlHttpReg.send( null );
    obj = eval('(' + xmlHttpReg.responseText + ')');
    widget.logWrite(6, "googleplus  API RESPONSE ******** TOKENEXPIRY : "+obj.expires_in+" ----- EMAIL "+obj.email);

    var tokenExpiry = obj.expires_in;
    var email = obj.email;
    
    if(tokenExpiry!=null && tokenExpiry!=undefined && tokenExpiry!="undefined"){
    var googlepluspeopleInfoUrl = "https://www.googleapis.com/plus/v1/people/me?access_token="+tokenID;
    xmlHttpReg = new XMLHttpRequest();
    xmlHttpReg.open( "GET", googlepluspeopleInfoUrl, false );
    xmlHttpReg.send( null );
    ppl = eval('(' + xmlHttpReg.responseText + ')');
    widget.logWrite(6, "googleplus  API RESPONSE ******** NAME : "+ppl.displayName+" ----- EMAIL "+ppl.emails[0]);
    
    var email=ppl.emails[0].value;
    //email =ppl.emails[0].value;
    var name=ppl.displayName;
    
    var postData = "method=zerchLogin&emailId="+email+"&password=&name="+name+"&loginType=GP&accessToken="+tokenID+"&tokenExpiry="+tokenExpiry;
    
    var xmlHttpReg = new XMLHttpRequest () ;                                        

if (xmlHttpReg){
    xmlHttpReg.onreadystatechange = function () {
                    if (4 == xmlHttpReg.readyState){
                                    resText = xmlHttpReg.responseText;
                                    widget.logWrite(6, "resp::::::::::::::::::::::"+resText) ;
                    }
                    
    };  
    xmlHttpReg.open ('POST',url,false) ;
    xmlHttpReg.send (postData) ;
    }
                    
    var response = eval('(' + resText + ')');
    if(response.result[0].txnstatus=='200')
    {
                    widget.logWrite(7,"UserId:"+response.result[0].userId);
                    widget.logWrite(7,"LoginType:"+response.result[0].loginType);
                    widget.logWrite(6, "google login");
                    window.location = "wgt:264711061/1.0";

                    
    }

    }

}              







