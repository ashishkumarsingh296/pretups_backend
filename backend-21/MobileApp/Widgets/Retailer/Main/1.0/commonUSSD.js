
function USSDMenu(flag)
{
	var formPage="";
	var divElement;
	var widget = window.widget ;	
	widget.logWrite(7,"inside gift");

	if((flag == 0) || (flag == 1 )|| (flag == 2) )
	{
		if(flag == 0)
		{
		formPage += "<span>Recharge</span>";
		formPage += "<br/>Enter Mobile No<br/>";
		}
		if(flag == 1)
		{
		formPage += "<span>Bill Payment</span>";
		formPage += "<br/>Enter Mobile No<br/>";
		}
		if(flag == 2)
		{
		formPage += "<span>Return Stock</span>";
		formPage += "<br/>Enter Agent's MSISDN<br/>";
		}
		
		formPage += "<div id='main' class=''>";
		formPage += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDGetAmount("+flag+",$mobilenu)\">";
		formPage += "<input type='mobileno' id='mobilenu' emptyok='false' maxlength='15' name='mobilenu' value=''/><setvar name=\"mobilenu\" value=\"\"/>";
		formPage += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
		formPage += "</form></div>";
	}
	else if(flag == 3)
	{
	
		formPage += "<span>Reports</span><br/>";
		formPage += "<a id='lastfive' href=\"javascript:lasttrans();\">1. Last 5 Transactions</a>";
		formPage += "<a id='stockbal'  href=\"javascript:stockbalreq();\">2. Stock Balance</a>";
		formPage += "<a id='dailyreports' href=\"javascript:dailyreports();\">3. Daily Reports</a>";
		formPage += "<a id='back' href='javascript:displayMain();'>4. Back</a>";
	
	
	}
	else if(flag == 4)
	{
	
		formPage += "<span>PIN Change</span><br/>Enter Old PIN<br/>";
		formPage += "<div id='main' class=''>";
		formPage += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDGetNewPIN($oldpin)\">";
		formPage += "<input type='text' id='oldpin' emptyok='false' name='oldpin' value='' title='Enter PIN'/><setvar name=\"oldpin\" value=\"\"/>";
		formPage += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
		formPage += "</form></div>";
	
	
	}
	else if(flag == 5)
	{
		widget.logWrite(7,"inside gift");
		formPage += "<span>Gift Recharge</span><br/>Enter Payee MSISDN<br/>";
		formPage += "<div id='main' class=''>";
		formPage += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDGetGifterMSISDN($mobilenu)\">";
		formPage += "<input type='mobileno' id='mobilenu' emptyok='false' maxlength='15' name='mobilenu' value=''/><setvar name=\"mobilenu\" value=\"\"/>";
		formPage += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
		formPage += "</form></div>";
	}
	divElement= document.getElementById("Menu");
	divElement.innerHTML = formPage;
	divElement.style.display = "block";  


}
function USSDGetAmount(flag,msisdn)
{
	var str="";
	if(flag == 0)
		str += "<span>Recharge</span>";
	if(flag == 1)
	str += "<span>Bill Payment</span>";
	if(flag == 2)
	str += "<span>Return Stock</span>";
	str += "<br/>Enter Amount<br/>";
	str += "<div id='main' class=''>";
	str += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDGetPin("+flag+","+msisdn+",$amount)\">";
	str += "<input type='text' id='amount' emptyok='false' name='amount' value='' title='Enter Amount'/><setvar name=\"amount\" value=\"\"/>";
	str += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
	str += "</form></div>";
	
	divElement= document.getElementById("Menu");
	divElement.innerHTML = str;
	divElement.style.display = "block";  


}
function USSDGetPin(flag,msisdn,amount)
{
	var str="";
	if(flag == 0)
	str += "<span>Recharge</span>";
	if(flag == 1)
	str += "<span>Bill Payment</span>";
	if(flag == 2)
	str += "<span>Return Stock</span>";
	str += "<br/>Enter PIN<br/>";
	str += "<div id='main' class=''>";
	str += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:confirmMsgUSSD("+flag+","+msisdn+","+amount+",$password)\">";
	str += "<input type='password' id='password' emptyok='false' name='password' maxlength='10' value='' title='Enter PIN'/><setvar name=\"password\" value=\"\"/>";
	str += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
	str += "</form></div>";
	
	divElement= document.getElementById("Menu");
	divElement.innerHTML = str;
	divElement.style.display = "block";  



}

function confirmMsgUSSD(flag,MSISDN,amount,password)
{
	var formPage="";
	if(flag == 0)
	formPage += "<span>Recharge</span>";
	if(flag == 1)
	formPage += "<span>Bill Payment</span>";
	if(flag == 2)
	formPage += "<span>Return Stock</span>";
	formPage += "<span>Confirm Mobile No</span>";
	formPage += "<div id='main' class=''>";
	formPage += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDConfirmNum("+flag+","+MSISDN+","+amount+","+password+",$mobilenu)\">";
	formPage += "<input type='mobileno' id='mobilenu' emptyok='false' maxlength='15' name='mobilenu' value=''/><setvar name=\"mobilenu\" value=\"\"/>";
	formPage += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
	formPage += "</form></div>";
	
	divElement= document.getElementById("Menu");
	divElement.innerHTML = formPage;
	divElement.style.display = "block";  
}

function USSDConfirmNum(flag,msisdn,amount,pin,mobno)
{
	var formPage="";
	if(msisdn == mobno)
	{
		if(flag == 0)
		{
			sendRechargeReq();
		}
		else if(flag == 1)
		{
			sendbillpayreq();
		}
		else if(flag == 2)
		{
			sendstockReq();
		}
		
	}
	else
	{
		
		if(flag == 0)
	formPage += "<span>Recharge</span>";
	if(flag == 1)
	formPage += "<span>Bill Payment</span>";
	if(flag == 2)
	formPage += "<span>Return Stock</span>";
		formPage += "<span>Please enter correct Mobile no</span><br/>Confirm Mobile No<br/>";
		formPage += "<div id='main' class=''>";
		formPage += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDConfirmNum("+flag+","+msisdn+","+amount+","+pin+",$mobilenu)\">";
		formPage += "<input type='mobileno1' id='mobilenu1' emptyok='false' maxlength='15' name='mobilenu1' value=''/><setvar name=\"mobilenu1\" value=\"\"/>";
		formPage += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
		formPage += "</form></div>";
		divElement= document.getElementById("Menu");
		divElement.innerHTML = formPage;
		divElement.style.display = "block";
	}


}

function USSDGetNewPIN(oldpin)
{
	var formPage="";
	var divElement;
	
		formPage += "<span>PIN Change</span><br/>Enter New PIN<br/>";
		formPage += "<div id='main' class=''>";
		formPage += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:confirmNewPIN("+oldpin+",$newpin)\">";
		formPage += "<input type='text' id='newpin' emptyok='false' name='newpin' value='' title='Enter PIN'/><setvar name=\"newpin\" value=\"\"/>";
		formPage += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
		formPage += "</form></div>";


	
	divElement= document.getElementById("Menu");
	divElement.innerHTML = formPage;
	divElement.style.display = "block";  


}
function confirmNewPIN(oldpin,newpin)
{
	var formPage="";
	formPage += "<span>PIN Change</span><br/>Confirm New PIN<br/>";
	formPage += "<div id='main' class=''>";
	formPage += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDConfirmPIN("+oldpin+","+newpin+",$newpin)\">";
	formPage += "<input type='text' id='newpin' emptyok='false' name='newpin' value='' title='Enter PIN'/><setvar name=\"newpin\" value=\"\"/>";
	formPage += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
	formPage += "</form></div>";
	
	divElement= document.getElementById("Menu");
	divElement.innerHTML = formPage;
	divElement.style.display = "block";  
}
function USSDConfirmPIN(oldpin,newpin,newpin1)
{
	var formPage="";
	if(newpin == newpin1)
	{
		sendchangepinreq();
	}
	else
	{
		
		formPage += "<span>Please enter correct PIN</span><br/>Confirm New PIN<br/>";
		formPage += "<div id='main' class=''>";
		formPage += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDConfirmPIN("+oldpin+","+newpin+",$newpin)\">";
		formPage += "<input type='text' id='newpin' emptyok='false' name='newpin' value='' title='Enter PIN'/><setvar name=\"newpin\" value=\"\"/>";
		formPage += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
		formPage += "</form></div>";
	
		divElement= document.getElementById("Menu");
		divElement.innerHTML = formPage;
		divElement.style.display = "block";
	}


}
function USSDGetGifterMSISDN(msisdn)
{
	var str="";
	str += "<span>Gift Recharge</span><br/>Enter Gifter MSISDN<br/>";
	str += "<div id='main' class=''>";
	str += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDGetAmount1("+msisdn+",$mobilenu1)\">";
	str += "<input type='mobileno1' id='mobilenu1' emptyok='false' maxlength='15' name='mobilenu1' value=''/><setvar name=\"mobilenu1\" value=\"\"/>";
	str += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
	str += "</form></div>";
	divElement= document.getElementById("Menu");
	divElement.innerHTML = str;
	divElement.style.display = "block";  


}
function USSDGetAmount1(msisdn,msisdn1)
{
	var str="";
	str += "<span>Gift Recharge</span><br/>Enter Amount<br/>";
	str += "<div id='main' class=''>";
	str += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDGetPin1("+msisdn+","+msisdn1+",$amount)\">";
	str += "<input type='text' id='amount' emptyok='false' name='amount' value='' title='Enter Amount'/><setvar name=\"amount\" value=\"\"/>";
	str += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
	str += "</form></div>";
	divElement= document.getElementById("Menu");
	divElement.innerHTML = str;
	divElement.style.display = "block";  


}
function USSDGetPin1(msisdn,msisdn1,amount)
{
	var str="";
	str += "<span>Gift Recharge</span><br/>Enter PIN<br/>";
	str += "<div id='main' class=''>";
	str += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:confirmMsgUSSD1("+msisdn+","+msisdn1+","+amount+",$password)\">";
	str += "<input type='password' id='password' emptyok='false' name='password' maxlength='10' value='' title='Enter PIN'/><setvar name=\"password\" value=\"\"/>";
	str += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
	str += "</form></div>";
	divElement= document.getElementById("Menu");
	divElement.innerHTML = str;
	divElement.style.display = "block";  



}

function confirmMsgUSSD1(MSISDN,MSISDN1,amount,password)
{
	var formPage="";
	formPage += "<span>Gift Recharge</span><br/>Confirm Payee MSISDN <br/>";
	formPage += "<div id='main' class=''>";
	formPage += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDConfirmNum1("+MSISDN+","+MSISDN1+","+amount+","+password+",$mobilenu)\">";
	formPage += "<input type='mobileno' id='mobilenu' emptyok='false' maxlength='15' name='mobilenu' value=''/><setvar name=\"mobilenu\" value=\"\"/>";
	formPage += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
	formPage += "</form></div>";
	
	divElement= document.getElementById("Menu");
	divElement.innerHTML = formPage;
	divElement.style.display = "block";  
}

function USSDConfirmNum1(msisdn,msisdn1,amount,pin,mobno)
{
	var formPage="";
	if(msisdn == mobno)
	{
		sendGiftRechargeReq();
	}
	else
	{
		
		formPage += "<span>Please enter correct Mobile no</span><br/>Confirm Mobile No<br/>";
		formPage += "<div id='main' class=''>";
		formPage += "<form id=\"formidMain\" name=\"myformMain\" onsubmit=\"javascript:USSDConfirmNum("+msisdn+","+amount+","+pin+",$mobilenu)\">";
		formPage += "<input type='mobileno' id='mobilenu' emptyok='false' maxlength='15' name='mobilenu' value=''/><setvar name=\"mobilenu\" value=\"\"/>";
		formPage += "<input id='login' style='font-size:7px;' class='buttonBgBlue' width='50%' align='center' type=\"submit\" value=\"Login\" name=\"SignIn\" />";
		formPage += "</form></div>";
		divElement= document.getElementById("Menu");
		divElement.innerHTML = formPage;
		divElement.style.display = "block";
	}


}
