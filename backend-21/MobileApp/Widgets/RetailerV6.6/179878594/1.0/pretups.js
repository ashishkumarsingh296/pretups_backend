var xmlHttp = null ;
var USSD_BEARER_TYPE = 2;
var SMS_BEARER_TYPE = 3;
var widget = window.widget ;	
var bearer = widget.fetchBearerType ();
var groupName = widget.fetchGroupName();
var phoneNumber = widget.fetchMSISDNNumber();
//var subscriberId = widget.fetchSubscruberID();
var presentLang=widget.fetchLanguage();
var SER_PRD_GATWAY = widget.widgetProperty("SER_PRD_GATWAY_VAS");




var langCode = getPresentLan(presentLang);
widget.logWrite(7,"language code ::::::" +langCode);


var mobile = widget.retrieveWidgetUserData(179878594,"launchMSISDN") ;
              var imei = widget.retrieveWidgetUserData(179878594, "IMEI");
              var ekey = widget.retrieveWidgetUserData(179878594, "ekey");
              widget.logWrite(7,"mobile ===============" + mobile + "imei ================" +imei );

widget.logWrite(7,"present language at the time of launch" +presentLang);


function checkonLaunch()
{	
     
       var presentLang=widget.fetchLanguage();
       widget.logWrite(7,"present language at the time of launch" +presentLang);
      
   
       widget.logWrite(7,"phoneNumber ::"+phoneNumber);
       //widget.logWrite(7,"subscriberId ::"+subscriberId);
       if(presentLang=="undefined"){
	  presentLang="en";
	  widget.changeLanguage("en");
	 }
       
      
	var pin = widget.retrieveUserData("launchPIN") ;
	var msisdn = widget.retrieveUserData("launchMSISDN") ;
	var flag = widget.retrieveUserData("flag");
     	widget.logWrite(7,"pin::"+pin);
	widget.logWrite(7,"msisdn::"+msisdn);
	widget.logWrite(7,"flag::"+flag);
	var signup = "";
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
     /* else if(msisdn == null){
             var str="";
             widget.logWrite(7,"log out scenario=====");
                     if(widget.clientVersion.indexOf("J2ME") != -1)
			{
				str += "<div width='100%' class='c3lMenuGroup'><span class='topSize' align='center' >"+STR_PRETUPS_TITLE+"</span></div><hr/>";
			}else
			{
				str += "<div width='100%' class='c3lTitle marginBottom20'><img width='50%' align='center' resimg='pretups_logo.png' src='pretups_logo.png' /><hr/></div>";	
			}
			str += "<input  id='mobilenu' name='mobilenu' type='text' maxLength='50' class='inputBg2' emptyok='false' title='Mobile Number or Email'/>";
			str += "<input id='pin' name='pin'  type='numpassword' class='inputBg2' maxLength='" + PIN_LENGTH + "' emptyok='false' title='PreTUPS PIN' encrypt='true' alt='PIN should be 4 digits'/>";
			str += "<a id='rechbut' class='c3lMenuGroup buttonBg' 	href=\"verifyUser($mobilenu,$pin,$%IMEI%);\" althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$'	><span class='buttonText' align='center'>" + STR_LOGIN + "</span></a>";
			str +="<a align='center' width='50%' href='#forgot' style='font-size:10px;color:red;padding:10% 10% 10% 10%'>"+STR_FORGOTPIN+"?</a>";
			str += "</div>";

                     
                     //divElement = document.getElementById("loginv1");
                    // divElement.innerHTML = str;
			//divElement.style.display = "block";



       }*/
	else
	{
            /* var str="";
             str += "<div width='100%' class='c3lTitle marginBottom20'><img width='50%' align='center' resimg='pretups_logo.png' src='pretups_logo.png' /><hr/></div>";
             str +="<div  class='c3lMenuGroup' width='100%'><a id='signIn' width='49%'  align='left'class='c3lMenuGroup marginBottom20' href='disable://control?id=$signIn$&action=enable://control?id=$signUp$&action=c3ltoggle:mobilenu;pin;signinbut;signUpnav;mobile;email;ppin;signUpbut;hr1;hr2;;gplus;fb'><span align='center'  style='font-weight:bold;padding:10% 10% 10% 10%' class='textColor'>"+STR_SIGNIN+"</span></a><span style='color:gray;font-size:14px;padding:10% 0% 10% 0%'>|</span><a id='signUp'class='c3lMenuGroup' href='enable://control?id=$signIn$&action=disable://control?id=$signUp$&action=c3ltoggle:mobilenu;pin;signinbut;signUpnav;mobile;email;ppin;signUpbut;hr1;hr2;;gplus;fb'  valign='middle' width='49%' align='right'><span style='font-weight:bold;padding:10% 10% 10% 10%' align='center' class='textColor'>"+STR_SIGNUP+"</span></a></div>";
	      str +="<div class='c3lMenuGroup marginBottom20' style='margin-top:20px' valign='top'><hr id='hr1' valign='top' width='49%' align='left' class='c3lMenuGroup'/><hr id='hr2' valign='top' align='right' style='visibility:hidden' width='49%' class='c3lMenuGroup'/></div>";
             str += "<input  id='mobilenu' name='mobilenu' type='text' maxLength='50' class='inputBg2' emptyok='false' title='Mobile Number or Email'/>";
	      str += "<input id='pin' name='pin'  type='numpassword' class='inputBg2' maxLength='" + PIN_LENGTH + "' emptyok='false' title='PreTUPS PIN' encrypt='true' alt='PIN should be 4 digits'/>";
	      str += "<div id='signinbut' class='c3lMenuGroup'>";
             str += "<a id='signinbut' class='c3lMenuGroup buttonBg' 	href=\"verifyUser($mobilenu,$pin,$%IMEI%);\" althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$'	><span class='buttonText' align='center'>" + STR_LOGIN + "</span></a>";
            str += "</div>";
            signup += "<input  id='mobile' style='visibility:hidden' name='mobile' type='numeric' maxLength='" + MSISDN_LENGTH + "' class='inputBg2' emptyok='false' title='Mobile Number'/>";
	     signup += "<input  id='email' style='visibility:hidden' name='email' type='email' maxLength='50' class='inputBg2' emptyok='false' title='Email'/>";
            signup += "<input id='ppin' style='visibility:hidden' name='ppin'  type='numpassword' class='inputBg2' maxLength='" + PIN_LENGTH + "' emptyok='false' title='PreTUPS PIN' encrypt='true' alt='PIN should be 4 digits'/>";
	     signup += "<div id='signUpbut' style='visibility:hidden' class='c3lMenuGroup'>";
            signup += "<a id='signUpbut' class='c3lMenuGroup buttonBg' 	href=\"retRegistration($mobile,$ppin,$%IMEI%,$email);\" althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$'	><span class='buttonText' align='center'>" + STR_SIGNUP + "</span></a>";
	     //signup +="<a align='center' width='50%' href='#forgot' style='font-size:10px;color:red;padding:10% 10% 10% 10%'>"+STR_FORGOTPIN+"?</a>";
	     signup +="</div>";

            document.getElementById("loginv1").innerHTML =str +signup;
	     document.getElementById("loginv1").style.display = "block";*/
            home();
		//login();
	}
	
}
function logout()
{
        var str="";
       widget.logWrite(7,"Log out from retailer app");

	widget.clearUserData("launchPIN");
	widget.clearUserData("launchMSISDN");
	widget.clearUserData("flag");
         //widget.storeUserData("flag","false");

	widget.clearCachedRequest("wgt:979446865/1.0/recharge.html");
       /*    str += "<div id='login' class='c3lMenuGroup'>";
        str += "<div width='100%' class='c3lTitle marginBottom20'><img width='50%' align='center' resimg='pretups_logo.png' src='pretups_logo.png' /><hr/></div>";
			str += "<span width='100%' align='center'>Thank U for using preTUPS APP</span><br/>";

       divElement= document.getElementById("login");
	divElement.innerHTML = str;
	divElement.style.display = "block";*/
	

      //login();

        // str += "<div width='100%' class='c3lTitle marginBottom20'><img width='50%' align='center' resimg='pretups_logo.png' src='pretups_logo.png' /><hr/></div>";
	 // str +="<div class='c3lMenuGroup' style='margin:10 10 0 10'><span align='center' class='navTextSize' >"Thank u"</span></div>";
        // divEle = document.getElementById("logout"); 
	 // divEle.innerHTML = str;
	 // divEle.style.display = "block";
       home();


}

/*
  Added by tribhuwan
*/
function home(){
          
             var language2 = "";
              
             var language2 = selectData();

             // var language2 =  widget.retrieveWidgetUserData(179878594,"LANGUAGE") ;
             widget.logWrite(7,"response message for language4 ===========******" +language2 );
              if("" == nullorUndefCheck(language2)){
             language2 = getLanguage();
             }
             var prodServices="";
           // prodServices = getProdServicesGatway();   
             widget.logWrite(7,"data from prod_services_gateway table ===========******" +prodServices );

             if("" == nullorUndefCheck(prodServices)) {
                 getProductServicesGatewayAPI();
                 getVasServicesSelection();
				 //added by parul
				 getvasenquiry();
				 
             } 
             
             //language2 = "English-2(en_US)_0,French(fr_NG)_1,Arabic(ar_NG)_2";
            // widget.logWrite(7,"response message for language2  ===========******" +language2);
             var signup = ""; 
             var str="";
             str += "<div width='100%' class='c3lTitle marginBottom10'><img width='50%' align='center' resimg='pretups_logo.png' src='pretups_logo.png' /><hr/></div>";
             str +="<div  class='c3lMenuGroup' width='100%'><a id='signIn' width='49%'  align='left'class='c3lMenuGroup marginBottom10' href='disable://control?id=$signIn$&action=enable://control?id=$signUp$&action=c3ltoggle:mobilenu;pin;signinbut;signUpnav;mobile;email;ppin;opr;oprt;language;language1;signUpbut;hr1;hr2;;gplus;fb'><span align='center'  style='font-weight:bold;padding:10% 10% 10% 10%' class='textColor'>"+STR_SIGNIN+"</span></a><span style='color:gray;font-size:14px;padding:10% 0% 10% 0%'>|</span><a id='signUp'class='c3lMenuGroup' href='enable://control?id=$signIn$&action=disable://control?id=$signUp$&action=c3ltoggle:mobilenu;pin;signinbut;signUpnav;mobile;email;ppin;opr;oprt;language;language1;signUpbut;hr1;hr2;;gplus;fb'  valign='middle' width='49%' align='right'><span style='font-weight:bold;padding:10% 10% 10% 10%' align='center' class='textColor'>"+STR_SIGNUP+"</span></a></div>";
	      str +="<div class='c3lMenuGroup marginBottom10' style='margin-top:0px' valign='top'><hr id='hr1' valign='top' width='49%' align='left' class='c3lMenuGroup'/><hr id='hr2' valign='top' align='right' style='visibility:hidden' width='49%' class='c3lMenuGroup'/></div>";
             str += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='mobilenu' name='mobilenu' type='text' maxLength='50' class='inputBg2' emptyok='false' title='" + STR_MSISDN_TITLE + "'/></div>";
	     /* if(presentLang=="ar"){
             document.getElementById("loginv1").setAttribute("dir","rtl");
          
             }*/
             str += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input id='pin' name='pin'  type='numpassword' class='inputBg2' maxLength='" + PIN_LENGTH + "' emptyok='false' title='"+STR_PIN_TITLE+"' encrypt='true' alt='PIN should be 4 digits'/></div>";
	   
          if(MULTI_OPERATOR==true || MULTI_OPERATOR=="true"){
              str +="<select align='left' id=\"opr\" name=\"opr\" class='inputBg2' title='Operator'>";
	      
	       str +="<option value=\"Airtel\">Airtel</option>";
	       str +="<option value=\"Idea\">Idea</option>";
	       str +="<option value=\"Vodafone\">Vodafone</option></select>";
             }
                 var x="";
                 var l="";
              if(presentLang=="undefined"){
               widget.logWrite(7,"present language undefined====");

             str +=" <div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><setvar name='language' value=''/> <select align='left' id='language' name='language' sendIndex='false' onchange='javascript:setLanguage($language)'>";
             
            
             var splitL = splitLang(language2);
             var text = splitL.split(",");
               
                
                for(var i=0;i<text.length;i++){
                     l = text[i].split(":");
                      x = l[1].split("_");
                    widget.logWrite(7,"after calling text else == " +l[0]);
                   widget.logWrite(7,"after calling text else == " +x[0]);
                   str +="<option class='c3lMenuGroup' id = " +x[0] +" value= " +x[0] +" >" +l[0] +"</option>";
                  
                }
		str +="</select></div>";
              } else{

                 widget.logWrite(7,"present language not undefined====");

                 str +=" <div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><setvar name='language' value=''/> <select align='left' id='language' name='language' sendIndex='false' onchange='javascript:setLanguage($language)'>";
           
           

             var splitL = splitLang(language2);
             var text = splitL.split(",");
               var x="";
               var l="";
               var z=""; 
               var selectValue="none";
                for(var i=0;i<text.length;i++){
                     l = text[i].split(":");
                      x = l[1].split("_");
 			if(presentLang==x[0]){
                      selectValue="selected";
                     }  else{
			selectValue="none";
                       }
               
                   str +="<option class='c3lMenuGroup' id = " +x[0]+" value= " +x[0]+" selected=" +  selectValue +    " >" +l[0]+"</option>";
                }

                widget.logWrite(7,"present string ====>>>>>>>" + str);             
                widget.logWrite(7,"present language ====>>>>>>>" + presentLang);
                widget.logWrite(7,"after calling text else x[0] ==>>>>>>>> " +x[0]);
                widget.logWrite(7,"after calling text else l[0] ==>>>>>>>> " +l);
		  str +="</select></div>";


                } 
 
             str += "<div id='signinbut' class='c3lMenuGroup'>";
             str += "<a id='signinbut' class='c3lMenuGroup buttonBg' 	href=\"verifyUser($mobilenu,$pin,$%IMEI%);\" althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$'	><span class='buttonText' align='center'>" + STR_LOGIN + "</span></a>";
             //str += "<a align='right' width='50%' href='#forgot' style='font-size:10px;color:red;padding:10% 10% 10% 8%'>Forgot PIN?</a>";
             str += "</div>";
           
           
           signup +="<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='mobile' style='visibility:hidden' name='mobile' type='numeric' maxLength='" + MSISDN_LENGTH + "' class='inputBg2' emptyok='false' title='" + STR_MSISDN_TITLE + "'/></div>";
	    signup += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='email' style='visibility:hidden' name='email' type='email' maxLength='50' class='inputBg2' emptyok='false' title='"+STR_EMAIL+"'/></div>";
           signup += "<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input id='ppin' style='visibility:hidden' name='ppin'  type='numpassword' class='inputBg2' maxLength='" + PIN_LENGTH + "' emptyok='false' title='"+STR_PIN_TITLE+"' encrypt='true' alt='PIN should be 4 digits'/></div>";
	    if(MULTI_OPERATOR==true || MULTI_OPERATOR=="true"){
              signup +="<select align='left' id=\"oprt\" name=\"oprt\" class='inputBg2' style='visibility:hidden' title='Operator'>";
	     
	       signup +="<option value=\"Airtel\">Airtel</option>";
	       signup +="<option value=\"Idea\">Idea</option>";
	       signup +="<option value=\"Vodafone\">Vodafone</option></select>";
             }
            
            /* if(presentLang=="undefined"){
              widget.logWrite(7,"present language undefined ===========" +presentLang);

             signup +=" <div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><setvar name='language1' value=''/> <select align='left' id='language1' name='language1' sendIndex='false' onchange='javascript:setLanguage($language1)' style='visibility:hidden'>";
	      
	       signup +="<option class='c3lMenuGroup' value='en' id='en'>English</option>";
	       signup +="<option value='fr' id = 'fr'>French</option>";
	       signup +="<option value='ar' id = 'ar'>Arabic</option></select></div>"; 

            }
             else{

              widget.logWrite(7,"present language present ==============" +presentLang);


              signup +=" <div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><setvar name='language1' value='presentLang'/> <select align='left' id='language1' name='language1'  sendIndex='false' onchange='javascript:setLanguage($language1)' style='visibility:hidden'>";

	       signup +="<option class='c3lMenuGroup' value='en' id='en'  selected='" +  (presentLang=="en" ? "selected" : "none"  )  +    "' >English</option>";
	       signup +="<option value='fr' id = 'fr' selected='" +  (presentLang=="fr" ? "selected" : "none"  )  +    "' > French</option>";
	       signup +="<option value='ar' id = 'ar' selected='" +  (presentLang=="ar" ? "selected" : "none"  )  +    "'>Arabic</option></select></div>";
              }
              */

                 var x="";
                 var l="";
              if(presentLang=="undefined"){
               widget.logWrite(7,"present language undefined====");

             signup +=" <div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><setvar name='language1' value='presentLang'/> <select align='left' id='language1' name='language1'  sendIndex='false' onchange='javascript:setLanguage($language1)' style='visibility:hidden'>";
             
            
             var splitL = splitLang(language2);
             var text = splitL.split(",");
               
                
                for(var i=0;i<text.length;i++){
                     l = text[i].split(":");
                      x = l[1].split("_");
                    widget.logWrite(7,"after calling text else == " +l[0]);
                   widget.logWrite(7,"after calling text else == " +x[0]);
                   signup +="<option class='c3lMenuGroup' id = " +x[0] +" value= " +x[0] +" >" +l[0] +"</option>";
                  
                }
		signup +="</select></div>";
              } else{

                 widget.logWrite(7,"present language not undefined====");

                 signup +=" <div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><setvar name='language1' value='presentLang'/> <select align='left' id='language1' name='language1'  sendIndex='false' onchange='javascript:setLanguage($language1)' style='visibility:hidden'>";
           
           

             var splitL = splitLang(language2);
             var text = splitL.split(",");
               var x="";
               var l="";
               var z=""; 
               var selectValue="none";
                for(var i=0;i<text.length;i++){
//widget.logWrite(7,"present language text text texttttttt===="+text);
                     l = text[i].split(":");
//widget.logWrite(7,"present language llllllllllllllllllllllllllll===="+l);

                      x = l[1].split("_");
//widget.logWrite(7,"present language xxxxxxxxxxxxxxxxxxxxxxxxxxxxx===="+x);
 			if(presentLang==x[0]){
                      selectValue="selected";
                     }  else{
			selectValue="none";
                       }
               
                   signup +="<option class='c3lMenuGroup' id = " +x[0]+" value= " +x[0]+" selected=" +  selectValue +    " >" +l[0]+"</option>";
                }

                widget.logWrite(7,"present string ====>>>>>>>" + str);             
                widget.logWrite(7,"present language ====>>>>>>>" + presentLang);
                widget.logWrite(7,"after calling text else x[0] ==>>>>>>>> " +x[0]);
                widget.logWrite(7,"after calling text else l[0] ==>>>>>>>> " +l);
		  signup +="</select></div>";


                }




            signup += "<div id='signUpbut' style='visibility:hidden' class='c3lMenuGroup'>";
            signup += "<div class='c3lMenuGroup' style='margin:3% 6% 3% 8%'><span style='color:rgb(94,94,96)'>"+TERMS_TEXT1+"</span><a style='color:red' href='#termsPage'>&nbsp;<span>"+TERMS_TEXT2+"</span></a></div>";
            signup += "<a id='signUpbut' class='c3lMenuGroup buttonBg' href=\"retRegistration($mobile,$ppin,$%IMEI%,$email);\" althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$'	><span class='buttonText' align='center'>" + STR_SIGNUP + "</span></a>";
	     signup += "<a align='right' width='50%' href='wgt:130508023/1.0' style='font-size:10px;color:red;padding:10% 10% 10% 10%'>Click Here</a>";
	     
           signup +="</div>";
            document.getElementById("loginv1").innerHTML =str +signup;
	     document.getElementById("loginv1").style.display = "block";

            var forgot = "";
            forgot +="<div   class='c3lTitle marginBottom20'><a class='c3lMenuGroup' href='close:'><img width='15%' align='left' resimg='icon_back.9.png' src='icon_back.9.png'/></a><span  class='topText' id='paytitle' valign='middle'>"+STR_FORGOTPIN+"</span><hr/></div>";
            forgot +="<span  style='margin:0% 17% 20% 20%;color:rgb(94,94,96)'>"+STR_FORGOTPIN_CONFIRM+"</span>";
	     forgot += "<input  id='mobilenu' name='mobilenu' type='numeric' maxLength='" + MSISDN_LENGTH + "' class='inputBg2' emptyok='false' title='"+STR_MSISDN_TT+"'  />";
	     forgot += "<a class='c3lMenuGroup buttonBg' href=\"verify($mobilenu,$pin,$%IMEI%);\" althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$'	><span class='buttonText' align='center'>"+STR_PROCEED+"</span></a>";
	     document.getElementById("forgot").innerHTML= forgot;
	     document.getElementById("forgot").style.display = "block";
           
            var terms="";
            terms+= "<div   class='c3lTitle marginBottom20' ><a class='c3lMenuGroup' href='close:'><img width='15%' align='left' resimg='icon_back.9.png' src='icon_back.9.png'/></a><span  class='topText' id='paytitle' valign='middle'>"+STR_TERMS_TEXT+"</span><hr/></div>";
            //terms += "<input  id='mobilenu' name='mobilenu' type='numeric' maxLength='" + MSISDN_LENGTH + "' class='inputBg2' emptyok='false' title='"+STR_MSISDN_TT+"'  />";

            terms+="<div>"+termsText()+"</div>";
	     document.getElementById("termsPage").innerHTML= terms;
	     document.getElementById("termsPage").style.display = "block";

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
                     //retRegistration();
			if(widget.clientVersion.indexOf("J2ME") != -1)
			{
				str += "<div width='100%' class='c3lMenuGroup'><span class='topSize' align='center' >"+STR_PRETUPS_TITLE+"</span></div><hr/>";
			}else
			{
				str += "<div width='100%' class='c3lTitle marginBottom20'><img width='50%' align='center' resimg='pretups_logo.png' src='pretups_logo.png' /><hr/></div>";	
			}
                     //str += "<div id='mobileip' class='c3lMenuGroup marginLeft20' style='margin-right:17%;visibility:hidden'>";
                    
	              //str += "<input  id='mdef' name='mdef' type='text' title='" + COUNTRY_CODE + "' class='inputBg2 countryCode' value='' readonly /><input  align='right' emptyok='false' maxlength='" + MSISDN_LENGTH + "' id='mobilenu' name='mobilenu' isFocus='true' class='inputBg2 inputMobile'  type='mobileno' title='' value=''  />";
	              //str += "</div>";
			str += "<input  id='mobilenu' name='mobilenu' type='numeric' maxLength='" + MSISDN_LENGTH + "' class='inputBg2' emptyok='false' title='Mobile Number'/>";
			str += "<input  id='email' name='email' type='email' maxLength='50' class='inputBg2' emptyok='false' title='Email'/>";
                     str += "<input id='pin' name='pin'  type='numpassword' class='inputBg2' maxLength='" + PIN_LENGTH + "' emptyok='false' title='PreTUPS PIN' encrypt='true' alt='PIN should be 4 digits'/>";
			str += "<a id='rechbut' class='c3lMenuGroup buttonBg' 	href=\"retRegistration($mobilenu,$pin,$%IMEI%,$email);\" althref='sms://inapp?to=+919986027668&text=PRC $mobilenu$:$amount$:$pin$'	><span class='buttonText' align='center'>" + STR_SIGNUP + "</span></a>";
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
				var xmlText = "TYPE=PREGRES&TXNSTATUS=200&MESSAGE=Registration Successful&ENK=33F876F832F7592C";
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


/*
 *  Added by Tribhuwan
 *  Starting------
 */


/*
* This function is used for user registration
*/

function retRegistration(mobile,pin,imei,email)
{
           encryptKey = "33F876F832F7592C";
	widget.clearUserData("eKey");
	widget.clearUserData("launchMSISDN");
	var ucode = Math.floor((Math.random() * 10000000000) + 1);
	var imei = widget.getHeader("IMEI");
	var PreTUPSSERVER = widget.widgetProperty ("PreTUPSSERVER") ;
	var LOGIN = widget.widgetProperty ("LOGIN") ;
	var PASSWORD = widget.widgetProperty ("PASSWORD") ;
	var REQUEST_GATEWAY_CODE = widget.widgetProperty ("REQUEST_GATEWAY_CODE") ;
	var REQUEST_GATEWAY_TYPE = widget.widgetProperty ("REQUEST_GATEWAY_TYPE") ;
	var SERVICE_PORT = widget.widgetProperty ("SERVICE_PORT") ;
	var SOURCE_TYPE = widget.widgetProperty ("SOURCE_TYPE") ;
	var TYPE = widget.widgetProperty ("TYPE") ;
	var LANGUAGE1 = widget.widgetProperty ("LANGUAGE1") ;
	var MSISDN = widget.widgetProperty("MSISDN");
	var RET_TYPE = widget.widgetProperty("RET_TYPE");
       
	var postData = "";
	var cdrStr="";
	var url = "";
	var txn_message = "";
       
	url=PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;
	
	
	if(DEMO_FLAG == '1' || DEMO_FLAG == 1)
	{
		url= DEMO_URL;
		postData = "TYPE=STPREGREQ&IMEI="+imei+"&UCODE="+ucode;
	}else
	{
		//postData = "TYPE="+RET_TYPE+"&MSISDN="+mobile+"&UCODE="+ucode+"&IMEI="+imei;DATA=TYPE=USRREGREQ&MSISDN=7200998978&IMEI=353743053371929&EMAIL=shishu82@gmail.com&PIN=1357
		postData = "TYPE="+RET_TYPE+"&MSISDN="+mobile+"&IMEI="+imei+"&EMAIL="+email+"&PIN="+pin;	
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
					txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
					//var mobileNo  = responseStr(xmlText, STR_MSISDN) ;
					var encryptKey  = responseStr(xmlText, STR_EKEY) ;
					cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
					widget.writeCDR (1, cdrStr) ;
					widget.logWrite(7,"cdrstr logs.."+cdrStr);
										
					if(txn_status == "200" || txn_status == "230")
					{
                                          widget.logWrite(7,"txn_status::" +txn_status);

						widget.storeUserData("flag","true");
						widget.storeUserData("ekey",encryptKey);
						widget.storeUserData("launchMSISDN",mobile);
						widget.storeUserData("launchPIN",pin);
						widget.storeUserData("IMEI",imei);
                                          widget.storeUserData("Email",email);

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

/*
* This function is used to verify the user when lounch after logout
*/

function verifyUser(msisdn,password,imei,language)
{
      widget.logWrite(7,"retailer login imei request::"+imei);
     // widget.logWrite(7,"retailer login opr request::"+opr);

        widget.logWrite(7,"retailer login language::"+language);
       var imei = widget.getHeader("IMEI");
	var PreTUPSSERVER = widget.widgetProperty ("PreTUPSSERVER") ;
	var LOGIN = widget.widgetProperty ("LOGIN") ;
	var PASSWORD = widget.widgetProperty ("PASSWORD") ;
	var REQUEST_GATEWAY_CODE = widget.widgetProperty ("REQUEST_GATEWAY_CODE") ;
	var REQUEST_GATEWAY_TYPE = widget.widgetProperty ("REQUEST_GATEWAY_TYPE") ;
	var SERVICE_PORT = widget.widgetProperty ("SERVICE_PORT") ;
	var SOURCE_TYPE = widget.widgetProperty ("SOURCE_TYPE") ;
	var TYPE = widget.widgetProperty ("TYPE") ;
	var LANGUAGE1 = widget.widgetProperty ("LANGUAGE1") ;
	var MSISDN = widget.widgetProperty("MSISDN");
	var USER_AUTH = widget.widgetProperty("USER_AUTH");
       
	var postData = "";
	var cdrStr="";
	var url = "";
	var txn_message = "";
        

	url=PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;
	
	
	if(DEMO_FLAG == '1' || DEMO_FLAG == 1)
	{
		url= DEMO_URL;
		postData = "TYPE=STPREGREQ&IMEI="+imei+"&UCODE="+ucode;
	}else
	{
		//postData = "TYPE="+RET_TYPE+"&MSISDN="+mobile+"&UCODE="+ucode+"&IMEI="+imei;DATA=TYPE=USRREGREQ&MSISDN=7200998978&IMEI=353743053371929&EMAIL=shishu82@gmail.com&PIN=1357
		postData = "TYPE="+USER_AUTH+"&MSISDN="+msisdn+"&IMEI="+imei+"&PIN="+password;	
	}
		
	
	
	widget.logWrite(7,"retailer login url::"+url);
	widget.logWrite(7,"retailer login postdata request::"+postData);

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
				widget.logWrite(7,"response for retailer login::"+xmlText);
				if (xmlText != null && !xmlText.indexOf("null") > -1 )
				{
					
					var txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
					txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
					//var mobileNo  = responseStr(xmlText, STR_MSISDN) ;
					var encryptKey  = responseStr(xmlText, STR_EKEY) ;
					cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
					widget.writeCDR (1, cdrStr) ;
					widget.logWrite(7,"cdrstr logs.."+cdrStr);
										
					if(txn_status == "200" || txn_status == "230")
					{
                                          widget.logWrite(7,"txn_status::" +txn_status);

						widget.storeUserData("flag","true");
						widget.storeUserData("launchMSISDN",msisdn);
						widget.storeUserData("launchPIN",password);
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


        
	/*var str = "";
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

              var mobile = widget.retrieveWidgetUserData(179878594,"launchMSISDN") ;
              var imei = widget.retrieveWidgetUserData(179878594, "IMEI");
              var ekey = widget.retrieveWidgetUserData(179878594, "ekey");
              widget.logWrite(7,"mobile ===============" + mobile + "imei ================" +imei );

		widget.logWrite(7,"inside main");
              var pin = widget.retrieveUserData("launchPIN") ;
              
	       var mobNumber = widget.retrieveUserData("launchMSISDN") ;
	       var email = widget.retrieveUserData("Email") ;
              widget.logWrite(7,"pin=" + pin + "email =" +email );
              if(password == pin && (msisdn == mobNumber || msisdn == email)){
               widget.logWrite(7,"password=" + password + "mobile=" +mobNumber );
                widget.storeUserData("flag",'true');
                window.location = "wgt:979446865/1.0:rechargeMenu()";


              }
              else
		{
			str = "please check your login details and try again later";
			divElement= document.getElementById("post1");
			divElement.title = STR_TITLE;
			divElement.innerHTML = str;
			divElement.style.display = "block";
		}
		
	}*/

}



//Ending





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

/*
 *  This function is used for get the languages from pretups.
 */
function getLanguage(){
	   widget.logWrite(7,"get language function is calling starting....");
		var imei = widget.getHeader("IMEI");
		var PreTUPSSERVER = widget.widgetProperty ("PreTUPSSERVER") ;
              var languageUrl=widget.widgetProperty ("languageUrl") ;
		var LOGIN = widget.widgetProperty ("LOGIN") ;
		var PASSWORD = widget.widgetProperty ("PASSWORD") ;
		var REQUEST_GATEWAY_CODE = widget.widgetProperty ("REQUEST_GATEWAY_CODE") ;
		var REQUEST_GATEWAY_TYPE = widget.widgetProperty ("REQUEST_GATEWAY_TYPE") ;
		var SERVICE_PORT = widget.widgetProperty ("SERVICE_PORT") ;
		var SOURCE_TYPE = widget.widgetProperty ("SOURCE_TYPE") ;
		var TYPE = widget.widgetProperty ("TYPE") ;
		var LAN_TYPE = widget.widgetProperty("LAN_TYPE");
	       var ExtNwCode = widget.widgetProperty("EXTNWCODE");  
		var postData = "";
		var cdrStr="";
		var url = "";
		var txn_message = "";
              var langMessage="";
	       
		url=languageUrl+ "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;
		
		postData = "TYPE="+LAN_TYPE+"&EXTNW="+ExtNwCode+"&IMEI="+imei;	

		
		widget.logWrite(7,"get language function request::"+postData);

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
					widget.logWrite(7,"get language function response::"+xmlText);
					if (xmlText != null && !xmlText.indexOf("null") > -1 )
					{
						
						var txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
						txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
						cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
						widget.writeCDR (1, cdrStr) ;
						widget.logWrite(7,"cdrstr logs.."+cdrStr);
											
						if(txn_status == "200")
						{

                                              

                                              widget.logWrite(7,"txn_message::====="+txn_message);
	                                       langMessage= txn_message.split(":");
                                              widget.logWrite(7,"langMessage::====="+langMessage[1]);
                                             // widget.storeUserData("LANGUAGE",langMessage[1]) ;
                                              var query = "INSERT INTO PRETUPS_LANG VALUES('OPERATOR1','"+langMessage[1]+"')";
		                                 var result = widget.addEditTableData(query);
                                               widget.logWrite(7,"inserted data into PRETUPS_LANG "+result);


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

function splitLang(language){
 widget.logWrite(7,"split langauge call::"+language);
	 var split="";
        
 split= language.replace(/\(/g,":"); 
 split = split.replace(/\)/g,"");
       widget.logWrite(7,"return split langauge====.."+split);
	return split;
}

/*
 *  This function is used for get the product,services and gateway.
 */
function getProductServicesGatewayAPI(){
	   widget.logWrite(7,"get language function is calling starting....");
		var imei = widget.getHeader("IMEI");
		var PreTUPSSERVER = widget.widgetProperty ("PreTUPSSERVER") ;
              var languageUrl=widget.widgetProperty ("languageUrl") ;
		var LOGIN = widget.widgetProperty ("LOGIN") ;
		var PASSWORD = widget.widgetProperty ("PASSWORD") ;
		var REQUEST_GATEWAY_CODE = widget.widgetProperty ("REQUEST_GATEWAY_CODE") ;
		var REQUEST_GATEWAY_TYPE = widget.widgetProperty ("REQUEST_GATEWAY_TYPE") ;
		var SERVICE_PORT = widget.widgetProperty ("SERVICE_PORT") ;
		var SOURCE_TYPE = widget.widgetProperty ("SOURCE_TYPE") ;
		var TYPE = widget.widgetProperty ("TYPE") ;
		var SER_PRD_GATWAY = widget.widgetProperty("SER_PRD_GATWAY");
	       var ExtNwCode = widget.widgetProperty("EXTNWCODE");  
              var productFlag=widget.widgetProperty("PRODUCT");  
              var serviceFlag=widget.widgetProperty("SERVICE");
              var gatewayFlag=widget.widgetProperty("GATEWAY");
		var postData = "";
		var cdrStr="";
		var url = "";
		var txn_message = "";
              var langMessage="";
	       
		url=languageUrl+ "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;
		
		postData = "TYPE="+SER_PRD_GATWAY+"&EXTNW="+ExtNwCode+"&IMEI="+imei+"&PRODUCT="+productFlag+"&SERVICE="+serviceFlag+"&GATEWAY="+gatewayFlag;	

		
		widget.logWrite(7,"get product,services and gateway request::"+postData);

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
					widget.logWrite(7,"get product,services and gateway  response::"+xmlText);
					if (xmlText != null && !xmlText.indexOf("null") > -1 )
					{
						
						var txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
						txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
						cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
						widget.writeCDR (1, cdrStr) ;
						widget.logWrite(7,"cdrstr logs.."+cdrStr);
											
						if(txn_status == "200")
						{

                                              

                                               widget.logWrite(7,"txn_message::====="+txn_message);
                                               var etopUp="";
                                               var res = txn_message.split(":");

                                               for(var i=1;i<res.length;i++){
                                               var xy = res[i].split("||");
                                               etopUp += xy[0]+":";
                                                  
                                                  
                                               }
                                               widget.logWrite(7,"etopUp  message::====="+etopUp );
                                               var textMessage = etopUp.split(":");
                                               var product=textMessage[0];
                                               var services = textMessage[1];
                                               var gateway = textMessage[2]; 
                                               var query = "INSERT INTO PRETUPS_PROD_SERVICES_GATEWAY VALUES('Vodafone','"+product+"','"+services+"','"+gateway+"')";
                                               var result = widget.addEditTableData(query);
                                              widget.logWrite(7,"inserted query into PRETUPS_PROD_SERVICES_GATEWAY "+query);
                                              widget.logWrite(7,"inserted data into PRETUPS_PROD_SERVICES_GATEWAY "+result);


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


/*
 *  This function is used for get the product
 */
function getVasServicesSelection(){
	   widget.logWrite(7,"getVasServices ====================== function is calling starting....");
		var imei = widget.getHeader("IMEI");
		var PreTUPSSERVER = widget.widgetProperty ("PreTUPSSERVER") ;
              var languageUrl=widget.widgetProperty ("languageUrl") ;
		var LOGIN = widget.widgetProperty ("LOGIN") ;
		var PASSWORD = widget.widgetProperty ("PASSWORD") ;
		var REQUEST_GATEWAY_CODE = widget.widgetProperty ("REQUEST_GATEWAY_CODE") ;
		var REQUEST_GATEWAY_TYPE = widget.widgetProperty ("REQUEST_GATEWAY_TYPE") ;
		var SERVICE_PORT = widget.widgetProperty ("SERVICE_PORT") ;
		var SOURCE_TYPE = widget.widgetProperty ("SOURCE_TYPE") ;
		var TYPE = widget.widgetProperty ("TYPE") ;
		var SER_PRD_GATWAY = widget.widgetProperty("SER_PRD_GATWAY_VAS");
	       var ExtNwCode = widget.widgetProperty("EXTNWCODE");  
              var productFlag=widget.widgetProperty("PRODUCT");  
              var serviceFlag=widget.widgetProperty("SERVICE");
              var gatewayFlag=widget.widgetProperty("GATEWAY");
		var postData = "";
		var cdrStr="";
		var url = "";
		var txn_message = "";
              var langMessage="";
              var str="";
	       
		url=languageUrl+ "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;
		
		postData = "TYPE="+SER_PRD_GATWAY+"&EXTNW="+ExtNwCode+"&IMEI="+imei;	

		
		widget.logWrite(7,"get product,services and gateway request::"+postData);

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
					widget.logWrite(7,"getVasServicesSelection response========================"+xmlText);
					if (xmlText != null && !xmlText.indexOf("null") > -1 )
					{
						
						var txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
						txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
						cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
						widget.writeCDR (1, cdrStr) ;
						widget.logWrite(7,"cdrstr logs.."+cdrStr);
											
						if(txn_status == "200")
						{

                                              

                                              widget.logWrite(7,"txn_message::====="+txn_message);
                                               var etopUp="";
                                               str= txn_message.split(":");
                                               vasstore = str[1];
                                               var query = "INSERT INTO PRETUPS_PROD_SERVICES_GATEWAY VALUES('VasServices','VasServices','VasServices','"+str[1]+"')";
                                               var result = widget.addEditTableData(query);
                                              widget.logWrite(7,"inserted query into PRETUPS_PROD_SERVICES_GATEWAY "+query);
                                              widget.logWrite(7,"inserted data into PRETUPS_PROD_SERVICES_GATEWAY "+result);
                                              
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


//added by parul
//added by parul for vas enquiry
function getvasenquiry()
{
 widget.logWrite(7,"getVasServices ====================== function is calling starting....");
		var imei = widget.getHeader("IMEI");
		var PreTUPSSERVER = widget.widgetProperty ("PreTUPSSERVER") ;
        var languageUrl=widget.widgetProperty ("languageUrl") ;
		var LOGIN = widget.widgetProperty ("LOGIN") ;
		var PASSWORD = widget.widgetProperty ("PASSWORD") ;
		var REQUEST_GATEWAY_CODE = widget.widgetProperty ("REQUEST_GATEWAY_CODE") ;
		var REQUEST_GATEWAY_TYPE = widget.widgetProperty ("REQUEST_GATEWAY_TYPE") ;
		var SERVICE_PORT = widget.widgetProperty ("SERVICE_PORT") ;
		var SOURCE_TYPE = widget.widgetProperty ("SOURCE_TYPE") ;
		var TYPE = widget.widgetProperty ("TYPE") ;
		var VAS_TYPE_ENQUIRY = widget.widgetProperty("VAS_TYPE_ENQUIRY");
	    var ExtNwCode = widget.widgetProperty("EXTNWCODE");  
              var productFlag=widget.widgetProperty("PRODUCT");  
              var serviceFlag=widget.widgetProperty("SERVICE");
              var gatewayFlag=widget.widgetProperty("GATEWAY");
		var postData = "";
		var cdrStr="";
		var url = "";
		var txn_message = "";
              var langMessage="";
              var str="";
	       
		url=languageUrl+ "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;
		
		postData = "TYPE="+VAS_TYPE_ENQUIRY+"&EXTNW="+ExtNwCode+"&IMEI="+imei;	

		
		widget.logWrite(7,"get product,services and gateway request::"+postData);

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
					widget.logWrite(7,"get product,services and gateway  response::"+xmlText);
					if (xmlText != null && !xmlText.indexOf("null") > -1 )
					{
						
						var txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
						txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
						cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
						widget.writeCDR (1, cdrStr) ;
						widget.logWrite(7,"cdrstr logs.."+cdrStr);
											
						if(txn_status == "200")
						{

                                              

                                              widget.logWrite(7,"txn_message::====="+txn_message);
                                               var etopUp="";
                                               str= txn_message.split(":");
                                               vasstore1 = str[1];
                                               var query = "INSERT INTO PRETUPS_PROD_SERVICES_GATEWAY VALUES('VasEnquiry','VasEnquiry','VasEnquiry','"+str[1]+"')";
                                               var result = widget.addEditTableData(query);
                                              widget.logWrite(7,"inserted query into PRETUPS_PROD_SERVICES_GATEWAY "+query);
                                              widget.logWrite(7,"inserted data into PRETUPS_PROD_SERVICES_GATEWAY "+result);
                                              
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