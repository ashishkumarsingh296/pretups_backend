function checkClientVersion()
{
	var isLatestversion 	= 0;
	var isUpgardeRequired 	= widget.widgetProperty("IS_UPGRADE_REQUIRED") ;
	var url = widget.widgetProperty("URL_DOWNLOAD") ;

	if(isUpgardeRequired == "1")
	{
		 isLatestversion = widget.validateClientVersion(); 
	}
	
	widget.logWrite(7," checkClientVersion isUpgardeRequired::"+isUpgardeRequired);
	widget.logWrite(7," checkClientVersion isLatestversion::"+isLatestversion);
	
	if (isLatestversion == 1)
	{
		widget.logWrite(7, "Optional upgrade");
		widget.storeWgtCDRData("-100");
		dispConfirmCommon(STRLATER, STRDOWNLOAD, "javascript:registration()", "javascript:downloadApp()", STROPTDOWN);
	}
	else if (isLatestversion == 2)
	{
		widget.logWrite(7, "Force upgrade");
		widget.storeWgtCDRData("-100");
		//dispConfirmCommon(STREXIT, STRDOWNLOAD, "exit:", URL_DOWNLOAD, STRFORCEDOWN);
		dispConfirmCommon(STREXIT, STRDOWNLOAD, "exit:", url, STRFORCEDOWN);
	}
	else
	{
		if(Number(clientVersion_actual) >= Number(clientVersion_config))
		{
			login();
		}else
		{
			terms();
		}
		
		
	}
	
}

function downloadApp()
{
	var URL_DOWNLOAD = widget.widgetProperty ("URL_DOWNLOAD") ;
	widget.logWrite(7,"client version download url::"+URL_DOWNLOAD);
	widget.redirectUrl(URL_DOWNLOAD);
}


function dispConfirmCommon(rightKey,leftKey,rightAction,leftAction,Msg)
{
	var menu = window.menu;

	addMenu(menu, leftKey, leftAction,"" , 1, 0);
	addMenu(menu, rightKey, rightAction,"",1, 1);
	
	var element = document.getElementById("commonDiv");
	element.setAttribute("id","confirmPage") ;
	element.setAttribute("type", "confirm");
	/*if(LANG == "fa")
	{
		element.setAttribute("dir","rtl") ;
	}*/
	element.innerHTML = Msg ;
	element.style.display = "block";
}

function addMenu(aMenu, aMenuName, aAction, alink, aIndex, aKeyPosition) // adds action to soft-keys dynamically.
{
    if (aKeyPosition == 0)
    {
        submenu = new MenuItem(aMenuName, aIndex);
        aMenu.append(submenu);
        submenu.onSelectEx = aAction;
        if (alink != "")
        {
            submenu.link = alink;
        }
    }
    else
    {
        aMenu.setRightSoftkeyLabelEx(aMenuName, aAction);
        if (alink != "")
        {
            aMenu.rightSoftkeyLink = alink;
        }
    }
}



/*function dispDownMesg(isForceDown)
{
        var downloadURL = widget.widgetProperty("DOWNLOADURL") ;
        var divElement  = document.getElementById ("home") ;
        var message             = "";

        message         = [message,"<div class='c3lMenuGroup'>"].join("");

        if(isForceDown)
        {
                divElement.setAttribute("id","forcedownload") ;
                message         = [message,strForceDownMsg,"<br/>"].join("");
                message         = [message,"<a id='down' class=\"buttonBg\"  name='down' href='",downloadURL,"'>",strContinue,"</a>"].join("");
        }
        else
        {
                divElement.setAttribute("id","downloadclient") ;
                message         = [message,strNewVersionAvail,"<br/>"].join("");
                message         = [message,"<a id='can'  class=\"buttonBg\" width=\"30%\" name='can' href=\"javascript:mShopHome()\"><span align='center'>",strCancelUp,"</span></a>"].join("");
                message         = [message,"<a id='down' class=\"buttonBg\" width=\"30%\" name='down' href='",downloadURL,"'><span align='center'>",strUpgrade,"</span></a>"].join("");
        }

        message                 = [message,"</div>"].join("");

        divElement.innerHTML            = message;
        divElement.style.display        = "block";
}*/
