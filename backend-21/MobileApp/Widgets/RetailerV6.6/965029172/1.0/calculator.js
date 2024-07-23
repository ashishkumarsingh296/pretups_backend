var PreTUPSSERVER = widget.widgetProperty ("PreTUPSSERVER") ;
var LOGIN = widget.widgetProperty ("LOGIN") ;
var PASSWORD = widget.widgetProperty ("PASSWORD") ;
var REQUEST_GATEWAY_CODE = widget.widgetProperty ("REQUEST_GATEWAY_CODE") ;
var REQUEST_GATEWAY_TYPE = widget.widgetProperty ("REQUEST_GATEWAY_TYPE") ;
var SERVICE_PORT = widget.widgetProperty ("SERVICE_PORT") ;
var SOURCE_TYPE = widget.widgetProperty ("SOURCE_TYPE") ;
var MSISDN = widget.widgetProperty("MSISDN");
var RET_TYPE = widget.widgetProperty("RET_TYPE");
var imei = widget.getHeader("IMEI");
var presentLang=widget.fetchLanguage();
var langCode = getPresentLan(presentLang);
widget.logWrite(7,"language code ::::::" +langCode);

var postData = "";
var cdrStr="";
var url = "";
var txn_message = "";
var prodServices="";
prodServices = getProdServicesGatway();   
url=PreTUPSSERVER + "?REQUEST_GATEWAY_CODE="+REQUEST_GATEWAY_CODE+"&REQUEST_GATEWAY_TYPE="+REQUEST_GATEWAY_TYPE+"&LOGIN="+LOGIN+"&PASSWORD="+PASSWORD+"&SOURCE_TYPE="+SOURCE_TYPE+"&SERVICE_PORT="+SERVICE_PORT;

function calculator(){
var serviceType="O2C_o2c,C2C_c2c";
var product = prodServices.split("||");
product=product[0];
var prdNo= product.split(",");
var str="";
var l="";
var str1="";
str += "<div width='100%' class='c3lTitle'><a  align='left' style='padding:1% 20% 10% 20%' href='keypad://clearall?target=$mobilenu$;$pin$;$amount$;$txnid$;$payeemobilenu$;$Giftermobilenu$&action=Menulist:menulist'><img   width='10%'  valign='middle' resimg='menu.png' src='menu.png'/><img width='50%' src='pretups_logo.png' resimg='pretups_logo.png'/></a>";
str+= "<a class='c3lMenuGroup'  valign='middle' href='#myAcc'><div class='c3lMenuGroup account'  id='pretitle'><img widht='10%' height='8%' src='Account.png' resimg='Account.png'/></div></a>";
str += "<hr/></div>";
str +="<div  class='c3lMenuGroup' width='100%'><a id='signIn' width='49%'  align='left'class='c3lMenuGroup marginBottom10' href='disable://control?id=$signIn$&action=enable://control?id=$signUp$&action=c3ltoggle:product;servicetype;amount'><span align='center'  style='font-weight:bold;padding:10% 10% 10% 10%' class='textColor'>Base Commission</span></a><span style='color:gray;font-size:14px;padding:10% 0% 10% 0%'>|</span><a id='signUp'class='c3lMenuGroup' href='enable://control?id=$signIn$&action=disable://control?id=$signUp$&action=c3ltoggle:product;adservices;gateway;amount'  valign='middle' width='49%' align='right'><span style='font-weight:bold;padding:10% 10% 10% 10%' align='center' class='textColor'>Addition Commission</span></a></div>";


var prdNo = prdNo.length;
if(prdNo == 1){
var text = product.split(",");
//widget.logWrite(7,"Base Calculator text====="+text);

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


//add
/*
var product1 = prodServices.split("||");
var module="c2s:C2S";
var services = product1[1];
var gateway = product1[2];
str1 +=" <div class='c3lMenuGroup ' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:10px;'><setvar name='product' value=''/> <select align='left' id='product' name='product' sendIndex='false'>";
var text = module.split(",");
for(var i=0;i<text.length;i++){
                      l = text[i].split(":");
                                         
                   str +="<option class='c3lMenuGroup' id = " +l[0] +" value= " +l[0] +" >" +l[1] +"</option>";
                  
                }
str1 +="</select></div>";
str1 +=" <div class='c3lMenuGroup ' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:5px;'><setvar name='adservices' value=''/> <select align='left' id='adservices' name='adservices' sendIndex='false'>";
str1 +="<option class='c3lMenuGroup' value='selectsrv' id='selectsrv'>Select Service</option>";
var text = services.split(",");
for(var i=0;i<text.length;i++){
                      l = text[i].split("_");
                                          
                   str1 +="<option class='c3lMenuGroup' id = " +l[1] +" value= " +l[1] +" >" +l[0] +"</option>";
                  
                }
str1 +="</select></div>";


str1 +=" <div class='c3lMenuGroup ' style='padding-left:15px;padding-right:15px;padding-bottom:5px;padding-top:5px;'><setvar name='gateway' value=''/> <select align='left' id='gateway' name='gateway' sendIndex='false'>";
str1 +="<option class='c3lMenuGroup' value='selectgt' id='selectgt'>Select Gateway</option>";
var text = gateway.split(",");
for(var i=0;i<text.length;i++){
                      l = text[i].split("_");
                                         
                   str1 +="<option class='c3lMenuGroup' id = " +l[1] +" value= " +l[1] +" >" +l[0] +"</option>";
                  
                }
str1 +="</select></div>";




str1+="<div class='c3lMenuGroup' style='padding-left:15px;padding-right:15px;padding-bottom:5px;'><input  id='amount' 	name='amount' type='decimal' maxLength='" + AMOUNT_LENGTH + "' class='inputBg2' emptyok='false' title='" + STR_AMOUNT_TITLE + "'  /></div>";
*/



document.getElementById("bcalculator").innerHTML = str + str1 ;
document.getElementById("bcalculator").style.display = "block";

}



function baseCalcultion(amount,product,servicetype){

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

	
	
	/*
widget.logWrite(7,"Base calculation method baseCalcultion()=="+amount+","+product+","+servicetype);
var xmlText = "TYPE=PREGRES&TXNSTATUS=200&MESSAGE=5%,10,15.5,20";
if (xmlText != null && !xmlText.indexOf("null") > -1 )
					{
						
						var txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
						txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
						cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
						widget.writeCDR (1, cdrStr) ;
						widget.logWrite(7,"cdrstr logs.."+cdrStr);
											
						if(txn_status == "200")
						{
                                           var response = txn_message.split(",");
                                            
                                           window.location = "wgt:965029172/1.0:baseCalculationRes('"+response[0]+"','"+response[1]+"','"+response[2]+"','"+response[3]+"')";
                                          }
                                          else
					       {
						document.getElementById("toast").innerHTML = txn_message ;
						document.getElementById("toast").style.display = "block";
						
					       }
                                   }
                                   */

}

function additionalCalcultion(){
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


}

