<link rel="stylesheet" href="newdesign.css" type="text/css" />
<SCRIPT langauage="javascript">

function fnChangeHandler_A(getdropdown)
  {
	var vSelectIndex_A = 0;
	var vSelectIndex_Value = 0;
	var vSelectIndex_Text = 0;

	vSelectIndex_A = getdropdown.options.selectedIndex;
	vSelectIndex_Value = getdropdown.options.value;
	
	vSelectIndex_Text = getdropdown.options[getdropdown.options.selectedIndex].text;
	
	
	if(vSelectIndex_A == 0)
	{
		document.forms[0].iatText.value="Your request is successfully accepted at IAT HUB";
	}

	if(vSelectIndex_A == 1)
	{
		document.forms[0].iatText.value="Your request is rejected from IAT HUB";
	}

	if(vSelectIndex_A == 2)
	{
		document.forms[0].iatText.value="IAT is suspended, please try after some time";
	}	

	if(vSelectIndex_A == 3)
	{
		document.forms[0].iatText.value="Internal server error, response is unkown";
	}	
	
	alert("IAT Response set by you :-"+vSelectIndex_Text);
 }
  

function windowClose()
{
	window.close();
}
var debug = true; 
function right(e) 
{ 
	if (navigator.appName == 'Netscape' && (e.which == 3 || e.which== 2)) return false; 
	else if (navigator.appName == 'Microsoft Internet Explorer' && (event.button == 2 || event.button == 3)) 
	{ 
		alert('This Page is fully protected! \n ©2009 Right click not allowed '); return false; 
	} 
	return true; 
} 
document.onmousedown=right; 
if (document.layers) 
window.captureEvents(Event.MOUSEDOWN); 
window.onmousedown=right;

var statusmsg="PreTUPS eRecharge System"

function hidestatus()
{
	window.status=statusmsg
	return true
}
document.onmouseover=hidestatus;
document.onmouseout=hidestatus;


function fullScreen() {
window.open("zebraSimulator.jsp", '', 'fullscreen=yes, scrollbars=auto');
}

function max() 
{ 
var obj = new ActiveXObject("Wscript.shell"); 
obj.SendKeys("{F11}"); 
} 

function showNotification()
{
 var val = document.ussd.servicetype.options.value;
 
 var RTMSISDN= document.getElementById('RTMSISDN');
 var RPIN= document.getElementById('RPIN');
 var RxMSISDN= document.getElementById('RxMSISDN');
 var AMT= document.getElementById('AMT'); 
 var NMSISDN= document.getElementById('NMSISDN');
 var NPIN= document.getElementById('NPIN');
 var CPIN= document.getElementById('CPIN');
 
	 if(val == 'RR')
	 {
	 		alert("OK");
			RTMSISDN.style.display = 'block';
			alert("OK1");
			RTMSISDN.style.visibility = 'visible';
			alert("OK2");
			RPIN.style.display = 'block';
			RPIN.style.visibility  = 'visible';

			RxMSISDN.style.display = 'block';
			RxMSISDN.style.visibility = 'visible';

			AMT.style.display = 'block';
			AMT.style.visibility = 'visible';

			NMSISDN.style.display = 'none';
			NMSISDN.style.visibility = 'hidden';

			NPIN.style.display = 'none';
			NPIN.style.visibility = 'hidden';

			CPIN.style.display = 'none';
			CPIN.style.visibility = 'hidden';
	 }
	 else if(val == 'IR')
	 {
			RTMSISDN.style.display = 'block';
			RTMSISDN.style.visibility = 'visible';

			RPIN.style.display = 'block';
			RPIN.style.visibility  = 'visible';

			RxMSISDN.style.display = 'block';
			RxMSISDN.style.visibility = 'visible';

			AMT.style.display = 'block';
			AMT.style.visibility = 'visible';

			NMSISDN.style.display = 'block';
			NMSISDN.style.visibility = 'visible';

			NPIN.style.display = 'none';
			NPIN.style.visibility = 'hidden';

			CPIN.style.display = 'none';
			CPIN.style.visibility = 'hidden';
	 }
	 else if(val == 'RC')
	 {
			RTMSISDN.style.display = 'block';
			RTMSISDN.style.visibility = 'visible';

			RPIN.style.display = 'block';
			RPIN.style.visibility  = 'visible';

			RxMSISDN.style.display = 'block';
			RxMSISDN.style.visibility = 'visible';

			AMT.style.display = 'block';
			AMT.style.visibility = 'visible';

			NMSISDN.style.display = 'none';
			NMSISDN.style.visibility = 'hidden';

			NPIN.style.display = 'none';
			NPIN.style.visibility = 'hidden';

			CPIN.style.display = 'none';
			CPIN.style.visibility = 'hidden';
		
	 }
	 else if(val == 'CPN' )
	 {
			RTMSISDN.style.display = 'block';
			RTMSISDN.style.visibility = 'visible';

			RPIN.style.display = 'block';
			RPIN.style.visibility  = 'visible';

			RxMSISDN.style.display = 'none';
			RxMSISDN.style.visibility = 'hidden';

			AMT.style.display = 'none';
			AMT.style.visibility = 'hidden';

			NMSISDN.style.display = 'block';
			NMSISDN.style.visibility = 'hidden';

			NPIN.style.display = 'block';
			NPIN.style.visibility = 'visible';

			CPIN.style.display = 'block';
			CPIN.style.visibility = 'visible';		
	 }
	
}  


function start()
{
 var RTMSISDN= document.getElementById('RTMSISDN');
 var RPIN= document.getElementById('RPIN');
 var RxMSISDN= document.getElementById('RxMSISDN');
 var AMT= document.getElementById('AMT'); 
 var NMSISDN= document.getElementById('NMSISDN');
 var OPIN= document.getElementById('OPIN');
 var NPIN= document.getElementById('NPIN');
 var CPIN= document.getElementById('CPIN');

			RTMSISDN.style.display = 'block';
			RTMSISDN.style.visibility = 'visible';

			RPIN.style.display = 'block';
			RPIN.style.visibility  = 'visible';

			RxMSISDN.style.display = 'block';
			RxMSISDN.style.visibility = 'visible';

			AMT.style.display = 'block';
			AMT.style.visibility = 'visible';

			NMSISDN.style.display = 'none';
			NMSISDN.style.visibility = 'hidden';

			NPIN.style.display = 'none';
			NPIN.style.visibility = 'hidden';

			CPIN.style.display = 'none';
			CPIN.style.visibility = 'hidden';


    document.ussd.servicetype.options[0].selected = true;
}

</SCRIPT>


