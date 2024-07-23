var mobile = widget.retrieveUserData("regMSISDN");
var regPIN = widget.retrieveSecureUserData("regPIN");
var imei = widget.getHeader("IMEI");
var date = changetimeformat();
var widget = window.widget ;	
var xmlHttp;
/* subscriber APP URL start */

var url=PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;
//var url = "http://172.16.7.194:1515/pretups/SelfTopUpReceiver?REQUEST_GATEWAY_CODE=STUGW&REQUEST_GATEWAY_TYPE=STUGW&LOGIN=pretups&PASSWORD=pretups123&SOURCE_TYPE=plain&SERVICE_PORT=190";
//var url = "http://124.153.86.45:5554/pretups/C2SReceiver?REQUEST_GATEWAY_CODE=EXTGW&REQUEST_GATEWAY_TYPE=EXTGW&LOGIN=pretups&PASSWORD=pretups123&SOURCE_TYPE=EXTGW&SERVICE_PORT=190";


if(DEMO_FLAG == 1 || DEMO_FLAG == "1")
{
	var demo_url = DEMO_URL ;
	url = demo_url;
}
widget.logWrite(7,"url for Subscriber PreTUPS demo flag:: "+DEMO_FLAG);
widget.logWrite(7,"url for Subscriber PreTUPS :: "+url);

/* subscriber APP URL end */

/*this function for User Registration of PreTUPS App*/

function SubscriberRegReq(imei,ucode)
{

	widget.clearUserData("eKey");
	widget.clearUserData("regMSISDN");
		
	ucode = nullorUndefCheck(ucode);
	imei = nullorUndefCheck(imei);
	
	var postData = "";
	var cdrStr="";
	
	//TYPE=REGREQ&IMEI=<IMEI no>&MSISDN=<Initiating Subscriber MSISDN>&LANGUAGE1=en&EMAILID=<Subscriber e-mail id>
	//var postdata = "TYPE="+TYPE+"&MSISDN="+msisdn+"&IMEI="+imei+"&EMAILID="+EMAIL; // working
	if(DEMO_FLAG == 1 || DEMO_FLAG == "1")
	{
		postData = "TYPE=STPREGREQ&IMEI="+imei+"&UCODE="+ucode;
	}else
	{
		url = url+"&TYPE="+SUBS_REG_TYPE+"&MSISDN="+DEF_MSISDN+"&UCODE="+ucode+"&IMEI="+imei;
	}
	//var postdata = "TYPE=STPREGREQ&MSISDN=7285508430&IMEI=353743053371926&EMAILID=abc@xyz.com";
	widget.logWrite(7,"SubscriberRegReq postdata request::"+postData);

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
				//var xmlDoc = xmlHttp.responseXML ;
				var xmlText = xmlHttp.responseText ;
				//var xmlText = "TYPE=PREGRES&TXNSTATUS=200&MESSAGE=Registration Successful&ENK=33F876F832F7592C";
				widget.logWrite(7,"response for SubscriberRegReq::"+xmlText);
				if (xmlText != null && !xmlText.indexOf("null") > -1 )
				{
					
					var txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
					var txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
					var mobileNo  = responseStr(xmlText, STR_MSISDN) ;
					var encryptKey  = responseStr(xmlText, STR_EKEY) ;
					cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
					widget.writeCDR (1, cdrStr) ;
					widget.logWrite(7,"cdrstr logs.."+cdrStr);
					
					widget.storeUserData("ekey",encryptKey);
					widget.storeUserData("regMSISDN",mobileNo);
					
					//if(txn_status == "4010")
					if(txn_status == STR_SUCCESS_CODE)
					{
			
						//	widget.storeUserData("regMSISDN",msisdn);
						window.location = "wgt:913980753/1.0:createPIN()";
					}/*else if(txn_status == "4010")
					{
						widget.storeUserData("ekey",encryptKey);
						widget.storeUserData("regMSISDN",msisdn);
						//	widget.storeUserData("regMSISDN",msisdn);
						window.location = "wgt:913980753/1.0:smsPinVerify()";
					}*/
					else if(txn_status == STR_SECLEVELREG_RES_CODE)
					{
						
						
						window.location = "wgt:913980753/1.0:createPIN()";
					}else if(txn_status == STR_REINSTALL_RES_CODE)
					{
						
						window.location = "wgt:264711061/1.0";
					}
					else
					{
						divElement= document.getElementById("toast");
						divElement.title = "PreTUPS";
						divElement.innerHTML = txn_message;
						divElement.style.display = "block";
					}
				}else
				 {
					divElement= document.getElementById("toast");
					divElement.title = "PreTUPS";
					divElement.innerHTML = STR_SERVICE_ERROR ;
					divElement.style.display = "block";
				 }
			}else
			{
				var str;
				var divElement;
				str = STR_SERVER_ERROR;
				divElement= document.getElementById("toast");
				divElement.title = "PreTUPS";
				divElement.innerHTML = str;
				divElement.style.display = "block";
			}  				


		};
	}
	xmlHttp.open ("POST", url , false) ;
	xmlHttp.setRequestHeader("Content-Type", "plain");

	xmlHttp.setRequestHeader("Connection", "close");
	
	cdrStr += changetimeformat()+"| Subscriber Registration";
	xmlHttp.send (postData) ;


}

/*function checkregMSISDN(){

	var postdata = "";	
	
	TYPE=STPRCREQ&IMEI=<IMEI no>&MSISDN=<Initiating Subscriber MSISDN>&PIN=<Subscriber PIN>&MSISDN2=<Receiver MSISDN>&EXTREFNUM=<Unique Reference number in Retailer App>&AMOUNT=<Amount>&SELECTOR=1&LANGUAGE1=en&LANGUAGE2=en&HOLDERNAME=<Holder Name>&CARDNO=<Card number>&EDATE=<Card’s Expiry date>&CVV=<cvv>
	if(SENDENCRYPTREQ)
	{
		postdata = "TYPE=" + SUBS_SMSPIN_TYPE + "&MSISDN="+ mobile +"&MESSAGE="+getEncrypt("IMEI=" + imei + "&&PIN=" + pin+"&LANGUAGE1=" + LANGUAGE1);
	}else
	{
		postdata = "TYPE=" + SUBS_SMSPIN_TYPE + "&MSISDN="+ mobile +"&MESSAGE=IMEI=" + imei + "&&PIN=" + pin+"&LANGUAGE1=" + LANGUAGE1;
	}
	
	widget.logWrite(7,"sendSMSpinReq postdata request::"+postdata);
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
				//var xmlDoc = xmlHttp.responseXML ;
				//var xmlText = xmlHttp.responseText ;
				var xmlText = "TYPE=PREGRES&TXNSTATUS=200&MESSAGE=SMSPIN Successful";
				widget.logWrite(7,"xml response for checkregMSISDN: "+xmlText);
				if (xmlText != null && !xmlText.indexOf("null") > -1 )
				{
					var txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
					var txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
					if(txn_status == STR_SUCCESS)
					{
						window.location = "wgt:264711061/1.0:pretupsHome(1)";
					}else
					{
						divElement= document.getElementById("toast");
						divElement.title = "PreTUPS";
						divElement.innerHTML = txn_message;
						divElement.style.display = "block";
					}
				}else
				 {
					divElement= document.getElementById("toast");
					divElement.title = "PreTUPS";
					divElement.innerHTML = STR_SERVICE_ERROR;
					divElement.style.display = "block";
				 }
				}else
				{
					var str;
					var divElement;
					str = STR_SERVER_ERROR;
	
					divElement= document.getElementById("toast");
					divElement.title = "PreTUPS";
					divElement.innerHTML = str;
					divElement.style.display = "block";
				}  			

		};
		xmlHttp.open ("POST", url , false) ;
		xmlHttp.setRequestHeader("Content-Type", "plain");
		xmlHttp.setRequestHeader("Connection", "close");
		cdrStr += changetimeformat()+"| MSISDN check";
		xmlHttp.send (postdata) ;
	}
}*/
/*this function for First Time change PIN and PIN change Request of User */
function sendSubsChangepinReq(pin,newpin,confirmpin,type) {
	
	widget.logWrite(7,"sendSubsChangepinReq  type:"+type);
	
	if (newpin != confirmpin) 
	{
		document.getElementById("toast").innerHTML = STR_PINCHECK;
		document.getElementById("toast").style.display = "block";
		
	}else
	{
		
		pin = nullorUndefCheck(pin);
		newpin = nullorUndefCheck(newpin);
		confirmpin = nullorUndefCheck(confirmpin);
		type = nullorUndefCheck(type);
		
		var cdrStr ="";
		var postdata = "";
		
		if("" == nullorUndefCheck(mobile))
		{
			mobile = widget.retrieveWidgetUserData(950181717,"regMSISDN");
		}
		if("" == nullorUndefCheck(pin))
		{
			pin = DEF_PIN;
		}
		
		/*TYPE=P2PCPREQ&IMEI=<IMEI no>&MSISDN=<Initiating Subscriber MSISDN>&OLDPIN=<Subscriber Old PIN>&NEWPIN=<Subscriber New PIN>CONFIRMPIN=<Subscriber Confirm PIN>&LANGUAGE1=en */
		if(SENDENCRYPTREQ)
		{
			postdata ="TYPE=" + SUBS_CHANGEPIN_TYPE + "&MSISDN=" + mobile + "&Message="+getEncrypt("IMEI=" + imei + "&OLDPIN=" + pin + "&NEWPIN=" + newpin + "&CONFIRMPIN=" + confirmpin); 
		}else
		{
			postdata ="TYPE=" + SUBS_CHANGEPIN_TYPE + "&MSISDN=" + mobile + "&Message=IMEI=" + imei + "&OLDPIN=" + pin + "&NEWPIN=" + newpin + "&CONFIRMPIN=" + confirmpin;	
		}
		
		widget.logWrite(7, "sendSubsChangepinReq postdata request:: " + postdata);
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
					//var xmlText = "TYPE=PREGRES&TXNSTATUS=200&MESSAGE=PIN CHANGED"; 
					
					widget.logWrite(7,"response for sendSubsChangepinReq:"+xmlText );
					
					if (xmlText != null && !xmlText.indexOf("null") > -1 )
					{
					
						var txn_status = responseStr(xmlText, STR_TXNSTATUS) ;
						var txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
						
						cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
						widget.logWrite(7,"cdrstr logs.."+cdrStr);
						cdrcommon(cdrStr);
						
						if(txn_status == STR_SUCCESS_CODE)
						{	
							widget.storeSecureUserData("regPIN",newpin);
							
							if(type == 1 || type == "1")
							{
								document.getElementById("toast").innerHTML = txn_message+"<setvar name='oldpin' value=''/><setvar name='newpin' value=''/><setvar name='cnewpin' value=''/>";;
								document.getElementById("toast").style.display = "block";
							}else
							{
								window.location = "wgt:264711061/1.0";
							}

						}else
						{
							document.getElementById("toast").innerHTML = txn_message;
							document.getElementById("toast").style.display = "block";
							
							
						}
					}else
					 {
						
						document.getElementById("toast").innerHTML = STR_SERVER_ERROR;
						document.getElementById("toast").style.display = "block";
						
					 }
				}else
				{
					
					
					document.getElementById("toast").innerHTML = STR_SERVICE_ERROR;
					document.getElementById("toast").style.display = "block";
					
					
				}  			
			};


			xmlHttp.open ("POST", url , false) ;
			xmlHttp.setRequestHeader("Content-Type", "plain");
			xmlHttp.setRequestHeader("Connection", "close");
			cdrStr += changetimeformat()+"| Subscriber pinChange";
			xmlHttp.send (postdata) ;
		}


	}
}

/*this function for subscriber self/another request of PreTUPS*/
function sendP2PRechargeReq(receiverMSISDN,amount,pin,type){
	
	var cdrStr = "";
	var txn_status = "";
	var txn_message = "";
	var postdata = "";
	
	pin = nullorUndefCheck(pin);
	rechargeAmount = nullorUndefCheck(amount);
	receiverMSISDN = validateMSISDN(nullorUndefCheck(receiverMSISDN));
	
	//var enteredpin = widget.retrieveWidgetSecureUserData(264711061,"enteredpin");
	
	/*if("" == nullorUndefCheck(enteredpin))
	{
		pin = enteredpin;
	}*/
	if(Number(clientVersion_actual) >= Number(clientVersion_config))
	{
		pin = regPIN;
	}
	/*if("" == nullorUndefCheck(receiverMSISDN))
	{
		receiverMSISDN = widget.retrieveWidgetUserData(264711061,"msisdn");	
	}
	if("" == nullorUndefCheck(amount))
	{
		amount = widget.retrieveWidgetUserData(264711061,"amount");	
	}*/
	 
	//TYPE=STPPRCREQ&IMEI=<IMEI no>&MSISDN=<Initiating Subscriber MSISDN>&PIN=<Subscriber PIN>&MSISDN1=<Receiver MSISDN>&EXTREFNUM=<Unique Reference number in Retailer App>&AMOUNT=<Amount>&SELECTOR=1&LANGUAGE1=en&LANGUAGE2=en
	if(SENDENCRYPTREQ)
	{
		postdata ="TYPE=" + SUBS_P2P_TYPE + "&MSISDN=" + mobile + "&Message="+getEncrypt("IMEI=" + imei + "&PIN=" + pin + "&MSISDN1=" + receiverMSISDN + "&AMOUNT=" + rechargeAmount + "&SELECTOR=" + SELECTOR + "&LANGUAGE1=" + LANGUAGE1);
	}else
	{
		postdata ="TYPE=" + SUBS_P2P_TYPE + "&MSISDN=" + mobile + "&Message=IMEI=" + imei + "&PIN=" + pin + "&MSISDN1=" + receiverMSISDN + "&AMOUNT=" + rechargeAmount + "&SELECTOR=" + SELECTOR + "&LANGUAGE1=" + LANGUAGE1;
	}
	widget.logWrite(7,"sendP2PRechargeReq postdata request::"+postdata);
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
				widget.logWrite(7,"response for sendP2PRechargeReq:"+xmlText);
				
				if (xmlText != null && !xmlText.indexOf("null") > -1 )
				{
					 txn_status = responseStr(xmlText, STR_TXNSTATUS) ;
					txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
					cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
					//widget.writeCDR (1, cdrStr) ;
					widget.logWrite(7,"cdrstr logs.."+cdrStr);
					cdrcommon(cdrStr);
					
					if(txn_status == STR_SUCCESS_CODE && type == 1)
					{
						var query = "DELETE FROM PRETUPS_RECHARGE_REQUESTS WHERE MSISDN='"+mobile+"' AND REQUESTOR_MOBILE = '"+receiverMSISDN+"'";
						widget.addEditTableData(query);
					}
					
					window.location = "wgt:264711061/1.0:payStatus('"+txn_status+"','"+txn_message+"')";

				}else
				 {
					
					window.location = "wgt:264711061/1.0:payStatus('"+txn_status+"','"+STR_SERVER_ERROR+"')";
				 }
			}else
			{
				
				
				
				window.location = "wgt:264711061/1.0:payStatus('"+txn_status+"','"+STR_SERVICE_ERROR+"')";
				
			}  				
		};


		xmlHttp.open ("POST", url , false) ;
		xmlHttp.setRequestHeader("Content-Type", "plain");
		xmlHttp.setRequestHeader("Connection", "close");
		cdrStr += changetimeformat()+"| P2P Recharge ";

		xmlHttp.send (postdata) ;
	}


}

/*this is for card recharge of subscriber */
function sendSubsCardRechReq(cvv,nickName,msisdn,amount,pin){

	widget.logWrite(7,"sendSubsCardRechReq amount and pin::"+cvv+" "+nickName+" "+msisdn+" "+amount+" "+pin);
	var cdrStr = "";
	var postdata = "";
	var txn_status="";
	var txn_message="";
	
	var receiverMSISDN = nullorUndefCheck(msisdn);
	var rechargeAmount = nullorUndefCheck(amount);
	pin = nullorUndefCheck(pin);
	
	/*var rechargeAmount  	= nullorUndefCheck(widget.retrieveWidgetUserData(264711061,"amount"));
	var receiverMSISDN 		= validateMSISDN(nullorUndefCheck(widget.retrieveWidgetUserData(264711061,"msisdn")));
	var pin				= nullorUndefCheck(widget.retrieveWidgetSecureUserData(264711061,"enteredpin"));
	 */
	if("" == nullorUndefCheck(nickName))
	{
	 nickName = widget.retrieveWidgetUserData(264711061,"nickName");
	}
		
		/*TYPE=STPRCREQ&IMEI=<IMEI no>&MSISDN=<Initiating Subscriber MSISDN>&PIN=<Subscriber PIN>&MSISDN2=<Receiver MSISDN>&EXTREFNUM=<Unique Reference number in Retailer App>&AMOUNT=<Amount>&SELECTOR=1&LANGUAGE1=en&LANGUAGE2=en&NNAME=<Subscriber Nick Name>&CVV=<cvv>*/
		if(SENDENCRYPTREQ)
		{
			postdata = "TYPE=" + SUBS_CARD_RECH_TYPE + "&MSISDN="+ mobile +"&Message="+getEncrypt("IMEI=" + imei + "&PIN=" + pin + "&MSISDN2=" + receiverMSISDN + "&AMOUNT=" + rechargeAmount + "&SELECTOR=" + SELECTOR + "&NNAME=" + nickName + "&CVV=" + cvv);
		}else
		{
			postdata = "TYPE=" + SUBS_CARD_RECH_TYPE + "&MSISDN="+ mobile +"&Message=IMEI=" + imei + "&PIN=" + pin + "&MSISDN2=" + receiverMSISDN + "&AMOUNT=" + rechargeAmount + "&SELECTOR=" + SELECTOR + "&NNAME=" + nickName + "&CVV=" + cvv;
		}
		widget.logWrite(7,"sendSubsCardRechReq postdata request::"+postdata);
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
					var xmlDoc = xmlHttp.responseXML ;
					var xmlText = xmlHttp.responseText ;
					widget.logWrite(7,"response for sendSubsCardRechReq: "+xmlText);
					if (xmlText != null && !xmlText.indexOf("null") > -1 )
					{
						txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
						txn_message = responseStr(xmlText, STR_TXNMESSAGE).toString() ;
						cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
						widget.writeCDR (1, cdrStr) ;
						widget.logWrite(7,"cdrstr logs.."+cdrStr);
						cdrcommon(cdrStr);
						window.location = "wgt:264711061/1.0:payStatus('"+txn_status+"','"+txn_message+"')";

						
					}else
					 {
						
						window.location = "wgt:264711061/1.0:payStatus('"+txn_status+"','"+STR_SERVER_ERROR+"')";
					 }
				}else
				{
					
					window.location = "wgt:264711061/1.0:payStatus('"+txn_status+"','"+STR_SERVICE_ERROR+"')";
				}  			

			};
			xmlHttp.open ("POST", url , false) ;
			xmlHttp.setRequestHeader("Content-Type", "plain");
			xmlHttp.setRequestHeader("Connection", "close");
			cdrStr += changetimeformat()+"| Card Recharge";

			xmlHttp.send (postdata) ;
		}
	
}
function sendBuddyRech(nickName,amount,pin){

	var cdrStr = "";
	var postdata = "";
	var txn_status="";
	var txn_message="";
	
	
	pin = nullorUndefCheck(pin);
	nickName = nullorUndefCheck(nickName);
	amount = nullorUndefCheck(amount);
	
	if( "" == nullorUndefCheck(mobile))
	{
		mobile = widget.retrieveWidgetUserData(950181717,"regMSISDN");	
	}
	
	if(Number(clientVersion_actual) >= Number(clientVersion_config))
	{
		pin = regPIN;
	}
	
	//TYPE=PRCREQ&IMEI=<IMEI no>&MSISDN=<Initiating Subscriber MSISDN>&BUDDYNNAME=<Buddy Nick Name>& AMOUNT=<Default Amount>&LANGUAGE1=en&PIN=<Subscriber PIN>
	
		if(SENDENCRYPTREQ)
		{
			postdata = "TYPE=" + BUDDY_RECH_TYPE + "&MSISDN="+ mobile +"&Message="+getEncrypt("IMEI=" + imei + "&PIN=" + pin + "&BUDDYNNAME=" + nickName + "&AMOUNT=" + amount + "&LANGUAGE1=" + LANGUAGE1);
		}else
		{
			postdata = "TYPE=" + BUDDY_RECH_TYPE + "&MSISDN="+ mobile +"&Message=IMEI=" + imei + "&PIN=" + pin + "&BUDDYNNAME=" + nickName + "&AMOUNT=" + amount + "&LANGUAGE1=" + LANGUAGE1;
		}
		widget.logWrite(7,"sendBuddyRech postdata request::"+postdata);
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
					widget.logWrite(7,"response for sendBuddyRech: "+xmlText);
					if (xmlText != null && !xmlText.indexOf("null") > -1 )
					{
						txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
						txn_message = responseStr(xmlText, STR_TXNMESSAGE).toString() ;
						cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
						widget.writeCDR (1, cdrStr) ;
						widget.logWrite(7,"cdrstr logs.."+cdrStr);
						cdrcommon(cdrStr);
						window.location = "wgt:264711061/1.0:payStatus('"+txn_status+"','"+txn_message+"')";

						
					}else
					 {
						
						window.location = "wgt:264711061/1.0:payStatus('"+txn_status+"','"+STR_SERVER_ERROR+"')";
					 }
				}else
				{
					
					window.location = "wgt:264711061/1.0:payStatus('"+txn_status+"','"+STR_SERVICE_ERROR+"')";
				}  			

			};
			xmlHttp.open ("POST", url , false) ;
			xmlHttp.setRequestHeader("Content-Type", "plain");
			xmlHttp.setRequestHeader("Connection", "close");
			cdrStr += changetimeformat()+"| Card Recharge";

			xmlHttp.send (postdata) ;
		}
	
}

/*this is for adhoc recharge of subscriber */
function sendSubsAdhocRechReq(msisdn,amount,pin,cardName,cardNum,cvv,expiry,nickName,saveCardCheck){
	widget.logWrite(7,"sendSubsAdhocRechReq saveCard"+saveCardCheck);
	
	if("true" == nullorUndefCheck(saveCardCheck))
	{
		widget.logWrite(7,"sendSubsAdhocRechReq saveCardInSide::"+saveCardCheck);
		sendaddCardReq(cardName,cardNum,cvv,expiry,nickName,pin,msisdn,amount,saveCardCheck);
		
	}else
	{
		
		pin = nullorUndefCheck(pin);
		var rechargeAmount = nullorUndefCheck(amount);
		var receiverMSISDN = validateMSISDN(msisdn);
		
		/*var pin = nullorUndefCheck(widget.retrieveWidgetSecureUserData(264711061,"enteredpin"));
		var rechargeAmount = nullorUndefCheck(widget.retrieveWidgetUserData(264711061,"amount"));
		var receiverMSISDN = validateMSISDN(widget.retrieveWidgetUserData(264711061,"msisdn"));*/
		
		if("" == receiverMSISDN)
		{
			receiverMSISDN = mobile;
		}
		if(Number(clientVersion_actual) >= Number(clientVersion_config))
		{
			pin = regPIN;
		}
		
		cardName = nullorUndefCheck(cardName);
		cardNum = nullorUndefCheck(cardNum);
		cvv = nullorUndefCheck(cvv);
		nickName = nullorUndefCheck(nickName);
		
		
		var cdrStr = "";
		var postdata = "";
		var txn_status = "";
		var txn_message = "";

		//var cardNum = nullorUndefCheck(cardno1)+nullorUndefCheck(cardno2)+nullorUndefCheck(cardno3)+nullorUndefCheck(cardno4);
		var EXTREFNUM = Math.floor((Math.random() * 1000000) + 1);
		if(expiry.indexOf("/") > -1)
		{
			expiry = expiry.split("/");
			expiry = expiry[0] + "/" +  expiry[1].substring(2,4);
		}
		
		/*TYPE=STPRCREQ&IMEI=<IMEI no>&MSISDN=<Initiating Subscriber MSISDN>&PIN=<Subscriber PIN>&	MSISDN2=<Receiver MSISDN>&EXTREFNUM=<Unique Reference number in Retailer App>&AMOUNT=<Amount>&SELECTOR=1&LANGUAGE1=en&LANGUAGE2=en&HOLDERNAME=<Holder Name>&CARDNO=<Card number>&EDATE=<Card’s Expiry date>&CVV=<cvv>*/
		if(SENDENCRYPTREQ)
		{
			postdata = "TYPE=" + SUBS_ADHOC_RECH_TYPE + "&MSISDN="+ mobile +"&Message="+getEncrypt("IMEI=" + imei + "&PIN=" + pin + "&MSISDN2=" + receiverMSISDN + "&AMOUNT=" + rechargeAmount + "&SELECTOR=" + SELECTOR  + "&HOLDERNAME=" + cardName + "&CARDNO=" + cardNum + "&EDATE=" + expiry + "&CVV=" + cvv+"&EXTREFNUM="+EXTREFNUM+"&LANGUAGE1="+LANGUAGE1 +"&LANGUAGE2="+LANGUAGE2);
		}else
		{
			postdata = "TYPE=" + SUBS_ADHOC_RECH_TYPE + "&MSISDN="+ mobile +"&Message=IMEI=" + imei + "&PIN=" + pin + "&MSISDN2=" + receiverMSISDN + "&AMOUNT=" + rechargeAmount + "&SELECTOR=" + SELECTOR  + "&HOLDERNAME=" + cardName + "&CARDNO=" + cardNum + "&EDATE=" + expiry + "&CVV=" + cvv+"&EXTREFNUM="+EXTREFNUM+"&LANGUAGE1="+LANGUAGE1 +"&LANGUAGE2="+LANGUAGE2;
		}
		widget.logWrite(7,"sendSubsAdhocRechReq postdata request::"+postdata);
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
					widget.logWrite(7,"response for sendSubsAdhocRechReq: "+xmlText);
					if (xmlText != null && !xmlText.indexOf("null") > -1 )
					{
						txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
						txn_message = responseStr(xmlText, STR_TXNMESSAGE);
						cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
						widget.writeCDR (1, cdrStr) ;
						widget.logWrite(7,"cdrstr logs.."+cdrStr);
						cdrcommon(cdrStr);
						window.location = "wgt:264711061/1.0:payStatus('"+txn_status+"','"+txn_message+"')";
					}else
					 {
						
						window.location = "wgt:264711061/1.0:payStatus('"+txn_status+"','"+STR_SERVER_ERROR+"')";
					 }
				}else
				{
					
					window.location = "wgt:264711061/1.0:payStatus('"+txn_status+"','"+STR_SERVICE_ERROR+"')";
				}  			

			};
			xmlHttp.open ("POST", url , false) ;
			xmlHttp.setRequestHeader("Content-Type", "plain");
			xmlHttp.setRequestHeader("Connection", "close");
			cdrStr += changetimeformat()+"| Adhoc Recharge ";
			xmlHttp.send (postdata) ;
		}
	}
}

function sendviewCardReq(pin,type){
		
		var cdrStr= "";
		var cardDetails = "";
		var postdata = "";
		
		if(PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_NONFIN == STR_PIN_CHECK && PIN_CHECK_VIEWCARD == STR_PIN_CHECK)
		{
			if("" == nullorUndefCheck(pin))
			{
				pin = nullorUndefCheck(widget.retrieveWidgetUserData(251238406,"viewCardPin"));
				if("" == nullorUndefCheck(pin))
				{
					pin = nullorUndefCheck(widget.retrieveWidgetUserData(264711061,"viewCardPin"));
				}
				if("" == nullorUndefCheck(pin))
				{
					pin = nullorUndefCheck(widget.retrieveWidgetUserData(950181717,"viewCardPin"));
				}
				if("" == nullorUndefCheck(pin))
				{
					pin = nullorUndefCheck(widget.retrieveUserData("viewCardPin"));
				}
			}	
		}else
		{	
			if(Number(clientVersion_actual) >= Number(clientVersion_config))
			{
				pin = nullorUndefCheck(regPIN);
			}else
			{
				pin = "";
			}
			
		}
		
				
		if( "" == nullorUndefCheck(mobile))
		{
			mobile = widget.retrieveWidgetUserData(950181717,"regMSISDN");	
		}
		
						
		/*TYPE=STPVCREQ&IMEI=<IMEI no>&MSISDN=<Initiating Subscriber MSISDN>&PIN=<Subscriber PIN>&LANGUAGE1=en*/
		if(SENDENCRYPTREQ)
		{
			postdata = "TYPE=" + VIEWCARDS_TYPE + "&MSISDN=" + mobile + "&Message="+getEncrypt("IMEI=" + imei + "&PIN=" + pin); 
		}else
		{
			 postdata = "TYPE=" + VIEWCARDS_TYPE + "&MSISDN=" + mobile + "&Message=IMEI=" + imei + "&PIN=" + pin;
		}
		widget.logWrite(7,"sendviewCardReqpostdata request ::"+postdata);
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
					widget.logWrite(7,"response for sendviewCardReq: "+xmlText );
					if (xmlText != null && !xmlText.indexOf("null") > -1 )
					{
						var txn_status 		= responseStr(xmlText, STR_TXNSTATUS) ;
						var txn_message 	= responseStr(xmlText, STR_TXNMESSAGE) ;
						cardDetails 		= responseStr(xmlText, STR_CARDDETAILS);
						//var cardCount 	= responseStr(xmlText, STR_CARDCOUNT);
						cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
						widget.writeCDR (1, cdrStr) ;
						widget.logWrite(7,"cdrstr logs.."+cdrStr);
						cdrcommon(cdrStr);
						
						if(txn_status == STR_SUCCESS_CODE || txn_status ==STR_NOCARD_RES_CODE)	
						{
							if(type == 1 || type == "1")
							{
								if( "" != nullorUndefCheck(pin))
								{
									widget.storeUserData("viewCardPin",pin);
								}
								if("" == nullorUndefCheck(cardDetails))
								{
									window.location = "wgt:251238406/1.0:cards('','"+STR_NOCARD_RES+"')";
								}else
								{
									window.location = "wgt:251238406/1.0:cards('','"+cardDetails+"')";
								}
								
							}else
							{
								return cardDetails;
							}
													
						}else
						{
							if(type == 1 || type == "1")
							{
								document.getElementById("toast").innerHTML = txn_message;
								document.getElementById("toast").style.display="block";
							}else
							{
								return cardDetails;
							}
						}

						return cardDetails;

					}else
					{
						if(type == 1 || type == "1")
						{
							document.getElementById("toast").innerHTML = STR_SERVER_ERROR;
							document.getElementById("toast").style.display="block";
						}else
						{
							return cardDetails;
						}
					}
				}else
				{
					if(type == 1 || type == "1")
					{
						document.getElementById("toast").innerHTML = STR_SERVICE_ERROR;
						document.getElementById("toast").style.display="block";
					}else
					{
						return cardDetails;
					}
				}  				

			};
			xmlHttp.open ("POST", url , false) ;
			xmlHttp.setRequestHeader("Content-Type", "plain");
			xmlHttp.setRequestHeader("Connection", "close");
			cdrStr += changetimeformat()+"| viewCards";
			xmlHttp.send (postdata) ;
		}

		return cardDetails;
}

//view buddy's
function sendviewBuddyReq(pin,type)
{
	
	
	var cdrStr="";
	var buddylist = "";
	var postdata = "";
	if(PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_NONFIN == STR_PIN_CHECK && PIN_CHECK_VIEWBUDDY == STR_PIN_CHECK)
	{
		if("" == nullorUndefCheck(pin))
		{
			pin = widget.retrieveUserData("viewbuddypin");
		}
		if("" == nullorUndefCheck(pin))
		{
			pin = widget.retrieveWidgetUserData(950181717,"viewbuddypin");
		}
		
	}else
	{
		if(Number(clientVersion_actual) >= Number(clientVersion_config))
		{
			pin = nullorUndefCheck(regPIN);
		}else
		{
			pin = "";
		}
	}
	
	pin = nullorUndefCheck(pin);
	
	if( "" == nullorUndefCheck(mobile))
	{
		mobile = widget.retrieveWidgetUserData(950181717,"regMSISDN");	
	}
	
	/*TYPE=PLISTREQ&IMEI=<IMEI no>&MSISDN=<Initiating Subscriber MSISDN>&PIN=<Subscriber PIN>&LANGUAGE1=en*/
	if(SENDENCRYPTREQ)
	{
		postdata = "TYPE=" + VIEWBUDDY_TYPE + "&MSISDN=" + mobile + "&Message="+getEncrypt("IMEI=" + imei + "&PIN=" + pin + "&LANGUAGE1="+LANGUAGE1);
	}else
	{
		postdata = "TYPE=" + VIEWBUDDY_TYPE + "&MSISDN=" + mobile + "&Message=IMEI=" + imei + "&PIN=" + pin + "&LANGUAGE1="+LANGUAGE1;
	}
	widget.logWrite(7,"sendviewBuddyReq postdata request::"+postdata);
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
				widget.logWrite(7,"response for sendviewBuddyReq: "+xmlText);
				if (xmlText != null && !xmlText.indexOf("null") > -1 )
				{
					var txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
					var txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
					buddylist =		 responseStr(xmlText,STR_BUDDYLIST);
					cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
					//widget.writeCDR (1, cdrStr) ;
					widget.logWrite(7,"cdrstr logs.."+cdrStr);
					cdrcommon(cdrStr);
										
					if(txn_status == STR_SUCCESS_CODE || txn_status == STR_NOBUDDIES)	
					{
						if(type == 1 || type == "1")
						{
							if( "" != nullorUndefCheck(pin))
							{
								widget.storeUserData("viewbuddypin",pin);
						
							}
							if("" == nullorUndefCheck(buddylist))
							{
								window.location = "wgt:264711061/1.0:buddies('','','','"+STR_NOBUDDY_RES+"')";
							}else
							{
								window.location = "wgt:264711061/1.0:buddies('','','','"+buddylist+"')";
							}
						}else
						{
							return buddylist;
						}
													
					}else
					{
						if(type == 1 || type == "1")
						{
							document.getElementById("toast").innerHTML = txn_message;
							document.getElementById("toast").style.display="block";
						}else
						{
							return buddylist;
						}
					}
					return buddylist;
				}else
				 {
					if(type == 1 || type == "1")
					{
						document.getElementById("toast").innerHTML = STR_SERVER_ERROR;
						document.getElementById("toast").style.display="block";
					}else
					{
						return buddylist;
					}
				 }
			}else
			{
				if(type == 1 || type == "1")
				{
					document.getElementById("toast").innerHTML = STR_SERVICE_ERROR;
					document.getElementById("toast").style.display="block";
				}else
				{
					return buddylist;
				}
			}  				

		};
		xmlHttp.open ("POST", url , false) ;
		xmlHttp.setRequestHeader("Content-Type", "plain");
		xmlHttp.setRequestHeader("Connection", "close");
		cdrStr += changetimeformat()+"| viewBuddys ";
		xmlHttp.send (postdata) ;
	}

	return buddylist;
}


function responseStr(respText,strParam)
{

	var respParams;
	var paramValue="";
	if("" != nullorUndefCheck(respText))
	{
		respText = respText.split("&");
		for (var i = 0;i < respText.length; i++)
		{
			respParams = respText[i].split("=");
			if(respParams.length > 1 && respParams[0] == strParam)
			{
				paramValue = respParams[1];
			}
		}
	}
	/*if(paramValue.indexOf(":") > -1)
	{
		paramValue = paramValue.split(":");
		paramValue = paramValue[paramValue.length-1];
	}*/
	
	return paramValue.replace(/\n/g, ' ');

}

function getEncrypt(data)
{
	var encrypt_url = ENCRYPT_URL;
	
	var respText = null;
	var xmlhttp = null;
	var encryptKey = "";
	encryptKey = widget.retrieveUserData("ekey");
	if("" == nullorUndefCheck(encryptKey))
	{
		encryptKey = widget.retrieveWidgetUserData(950181717,"ekey");
	}
	var postdata = encryptKey+"|"+data;
	widget.logWrite(7,"encrypt PostData:"+postdata);
	if (null == xmlhttp)
	{
		xmlhttp = new XMLHttpRequest();
	}
	if (xmlhttp)
	{
		xmlhttp.onreadystatechange = function ()
		{
			widget.logWrite(6, " getEncrypt xmlhttp.readyState =============== > " + xmlhttp.readyState);
			if (xmlhttp.readyState == 4)
			{
				if (200 == xmlhttp.status)
				{
					//widget.storePageData ("mstrack2plg", 1, xmlhttp.responseBytes) ; 
					respText = xmlhttp.responseText;
					widget.logWrite(6, " getEncrypt respText =============== > " + respText);
				}
			}
		};
	}
	xmlhttp.open("POST",encrypt_url, false);
	xmlhttp.send(postdata);
	return respText;
}

function registration(mobile,pin,imei)
{
	var encryptKey = "FCAAF35E4C29086D";
	
	var checkMSISDN = "SELECT MSISDN FROM PRETUPS_REG WHERE MSISDN = '"+mobile+"'";
	var result = widget.selectFromTable(checkMSISDN);
	
	if(!(result.indexOf("Row") > -1))
	{
		var query = "INSERT INTO PRETUPS_REG VALUES('"+mobile+"','"+imei+"','"+encryptKey+"','"+pin+"')";
		var result = widget.addEditTableData(query);
		widget.logWrite(7,"registration result::"+result);
		
		if(1 == nullorUndefCheck(result))
		{
			widget.storeUserData("ekey",encryptKey);
			widget.storeUserData("regMSISDN",mobile);
			widget.storeSecureUserData("regPIN",pin);
			widget.storeUserData("imei",imei);
			window.location = "wgt:264711061/1.0";
		}else
		{
			document.getElementById("toast").innerHTML = "Registration Failed";
			document.getElementById("toast").style.display = "block";
		}
	}else
	{
		document.getElementById("toast").innerHTML = "Already Registered";
		document.getElementById("toast").style.display = "block";
	}
	
	

}

function regCheck(mobile,pin,imei)
{
	var query = "SELECT EKEY FROM PRETUPS_REG WHERE MSISDN='"+mobile+"'";
	var result = widget.selectFromTable(query);
	widget.logWrite(7,"regCheck result::"+result);
	if("" != nullorUndefCheck(result) && nullorUndefCheck(result).indexOf("EKEY") > -1)
	{
		var rootele = document.createElement ("root") ;
	    rootele.innerHTML = result ;

	    var resultInfo = rootele.getElementsByTagName("Row") ;
	   	var encryptKey = resultInfo[0].getElementsByTagName("EKEY")[0].textContent;
	   	widget.storeUserData("ekey",encryptKey);
		widget.storeUserData("regMSISDN",mobile);
		widget.storeSecureUserData("regPIN",pin);
		
		window.location = "wgt:264711061/1.0";
	}else
	{
		document.getElementById("toast").innerHTML = LOGIN_ERROR;
		document.getElementById("toast").style.display = "block";
	}

}

function sendRequest(mobile,amount)
{
	var requestor_mobile = widget.retrieveUserData("regMSISDN");
	
	var query = "INSERT INTO PRETUPS_RECHARGE_REQUESTS(MSISDN,REQUESTOR_MOBILE,AMOUNT,REQUEST_ID) VALUES ('"+mobile+"','"+requestor_mobile+"','"+amount+"','"+transId()+"')";
	
	if(nullorUndefCheck(mobile) != requestor_mobile)
	{
		var result = widget.addEditTableData(query);
		if(1 == result)
		{
			document.getElementById("toast").innerHTML = STR_TALKMEUP_RESPMSG +"<setvar name='msisdn' value=''/><setvar name='amount' value=''/>" ;
			document.getElementById("toast").style.display = "block";
		}else
		{
			document.getElementById("toast").innerHTML = "Request sent failed" ;
			document.getElementById("toast").style.display = "block";
		
		}
		
	}else
	{
		document.getElementById("toast").innerHTML = "Registered User and Requestor should not be equal" ;
		document.getElementById("toast").style.display = "block";
	}
	
	
}

function transId()
{
    var d = new Date();
 var transid =d.getFullYear().toString().substring(2,4)+""+d.getMonth()+1+""+d.getDate()+""+Math.floor(Math.random()*10000);
	
	return transid; 
}

function getGooglePlusAccessToken(tokenID) {
widget.logWrite(7,"google plus login**************************");
    var reqtime=storeStartTime();
    var url = widget.widgetProperty("GET_DEALS_URL");


    var googleplusInfoUrl = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=" + tokenID;
    var xmlHttpReg = new XMLHttpRequest();
    xmlHttpReg.open("GET", googleplusInfoUrl, false);
    xmlHttpReg.send(null);
    obj = eval('(' + xmlHttpReg.responseText + ')');

    var tokenExpiry = obj.expires_in;
    var email = obj.email;

    if (tokenExpiry != null && tokenExpiry != undefined && tokenExpiry != "undefined") {
        var googlepluspeopleInfoUrl = "https://www.googleapis.com/plus/v1/people/me?access_token=" + tokenID;
        xmlHttpReg = new XMLHttpRequest();
        xmlHttpReg.open("GET", googlepluspeopleInfoUrl, false);
        xmlHttpReg.send(null);
        ppl = eval('(' + xmlHttpReg.responseText + ')');

        email = ppl.emails[0].value;
        var name = ppl.displayName;

        var postData = "method=zerchLogin&emailId=" + email + "&password=&name=" + name + "&loginType=GP&accessToken=" + tokenID + "&tokenExpiry=" + tokenExpiry;

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
           // widget.storeWidgetUserData(212135013, "USERID", response.result[0].userId);
           // widget.storeWidgetUserData(212135013, "LOGINTYPE", response.result[0].loginType);
           // widget.storeWidgetUserData(977719700, "USERID", response.result[0].userId);
           // widget.storeWidgetUserData(977719700, "LOGINTYPE", response.result[0].loginType);
           // widget.storeWidgetUserData(159896473, "USERID", response.result[0].userId);
           // widget.storeWidgetUserData(159896473, "LOGINTYPE", response.result[0].loginType);
	    cdrcommon(reqtime,imei,subscriberId,"GOOGLEPLUS LOGIN","","","GOOGLEPLUS LOGIN SUCCESSFULL","","200");
            window.location = "wgt:264711061/1.0";

        }

    }

}          
