
function getDeviceCategory ()
{
	var NONTOUCH				= 0 ;
	var IPHONE					= 1 ;
	var ANDROID					= 2 ;
	var TOUCH					= 3 ;
	var IPHONE_PATH 			= "iphone" ;
	var IPAD_PATH				= "ipad";
	var J2ME_TOUCH_PATH			= "j2metouch";
	var J2ME_NONTOUCH_PATH		= "j2ment";
	var BB_TOUCH_PATH			= "bbtouch";
	var BB_NONTOUCH_PATH		= "bbnt";
	var J2ME_LOWEND_PATH		= "j2mentlow" ;
	
	var ANDROID_PATH_LOW			= "android/low";
	var ANDROID_PATH_MEDIUM			= "android/medium";
	var ANDROID_PATH_HIGH			= "android/high";
	var ANDROID_PATH_XHIGH			= "android/xhigh";
	var ANDROID_PATH_CSS			= "android/common";
	
	var category 				= NONTOUCH ;
	var screenWidth				= widget.fetchScreenWidth() ;
	var screenHeight				= widget.fetchScreenHeight() ;
	var clientVersion 			= (widget.clientVersion).toUpperCase();
	var devicemodel				= (widget.fetchUserAgent()).toUpperCase();
	var touch					= widget.touchEnabled ;
	widget.logWrite(6, " ## screen width ## touch ## " + screenWidth+ " ## screen height ##  " + screenHeight+ " ## touch ## " + touch +"## deviceModel ##"+devicemodel) ;	

	
	var dpi = getDPI();
	

	widget.logWrite(6, "screenWidth with dpi "+dpi) ;


	   
    
    if(devicemodel.indexOf("IPAD") != -1)
    {
    	category = IPHONE ;
        widget.resourcePath = IPAD_PATH ;
    }
    else if(clientVersion.indexOf("IOS") != -1)
    {
    	category = IPHONE ;
        widget.resourcePath = IPHONE_PATH ;
    }
    else if(clientVersion.indexOf("ANDROID") != -1)
    {
    	
    	category = ANDROID ;
    	if(dpi <= 160)
    	{
    		widget.resourcePath = ANDROID_PATH_CSS ;

    	}else if(dpi <= 240)
    	{
    		widget.resourcePath = ANDROID_PATH_CSS ;
    	}else 
    	{
    		widget.resourcePath = ANDROID_PATH_CSS ;
    	}
    }
    else if(touch)
    {
    	if((devicemodel.indexOf("BLACKBERRY") != -1) ||(devicemodel.indexOf("RIM") != -1))
    	{
        	widget.resourcePath = BB_TOUCH_PATH ;
    	}
    	else
    	{
        	widget.resourcePath = J2ME_NONTOUCH_PATH ;
    	}
    	
    	category = NONTOUCH ;

    }
    else
    {
    	if(dpi > 240)
		{
    		if((devicemodel.indexOf("BLACKBERRY") != -1) ||(devicemodel.indexOf("RIM") != -1))
	    	{
	        	widget.resourcePath = BB_NONTOUCH_PATH ;
	    	}
	    	else
	    	{
	        	widget.resourcePath = J2ME_NONTOUCH_PATH;
	    	}
		}
		else
		{	
			widget.resourcePath = J2ME_LOWEND_PATH ;
		}
    }
    
    return category;
}

var DEVICE_CATEGORY = getDeviceCategory () ;
widget.logWrite(6, " checkdevice resourcePath " + widget.resourcePath);
widget.logWrite(6, " checkdevice DEVICE_CATEGORY " + DEVICE_CATEGORY);
function isNonTouch()
{
	if(DEVICE_CATEGORY == 0)
	{
		return true ;
	}
	else
	{
		return false ;
	}
}
function getDPI()
{
	var density = widget.getHeader("DENSITY"); 
	var dpi = "";
	
	widget.logWrite(6, " start screenWidth density ===>" + density);
	if(density != undefined && density != null && (density.indexOf(";") > -1))
	{
		dpi = density.split(";");
	//var actwidth = screenWidth *dpi[0];
		dpi  = dpi[1];
	}
	return dpi;
}



var NONTOUCH = isNonTouch() ;






