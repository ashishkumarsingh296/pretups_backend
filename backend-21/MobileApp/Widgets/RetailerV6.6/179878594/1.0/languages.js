var presentLang=widget.fetchLanguage();
var presentTheme=widget.retrieveUserData("theme");
var widget=window.widget;
widget.logWrite(7,"present Language========"+ presentLang);

function homeOnLoad(){
	document.getElementById("text").innerHTML=STR_HELLOWORLD;
	loadStrings();	
	if(presentLang=="undefined"){
	presentLang="en";
	widget.changeLanguage("en");
	}
	document.getElementById(presentLang).setAttribute("selected","selected");
	document.getElementById(presentTheme).setAttribute("selected","selected");
	document.getElementById("main").style.display="block";
}

function setOptions(){ 
 widget.logWrite(7,"set option method starting========");

	
	loadStrings();
	
widget.logWrite(7,"set option method Ending========");

}

function setLanguage(lan){
	widget.changeLanguage(lan);
	widget.logWrite(7,"lang-----"+lan);
	
	var ele = document.getElementById("alertLang");
	if (lan=="en")
	{
		ele.innerHTML = STR_LANG_CHANGE_EN;
	}
	else if (lan=="fr")
	{
		ele.innerHTML = STR_LANG_CHANGE_FR;
	}
	else if (lan=="ar")
	{
		ele.innerHTML = STR_LANG_CHANGE_AR;
	}
	document.getElementById("alertLang").title = STR_TITLE;

	document.getElementById("alertLang").style.display="block";
} 

function loadStrings(){
widget.logWrite(7,"loadStrings method calling start========" );

widget.logWrite(7,"loadStrings method calling with language========" + presentLang );


    widget.changeLanguage(presentLang);
    widget.storeUserData("USRLANG",presentLang);
  

   window.location = "wgt:179878594/1.0";
	

widget.logWrite(7,"loadStrings method calling End========" );

}


