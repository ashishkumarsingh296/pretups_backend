function getDeviceCategory()
{
	var SMARTPHONE = 1;
	var FEATUREPHONE = 0;
	var COMMON_PATH = "Common";
	var BLACKBERRY_PATH = "Blackberry";
	var IPHONE_PATH = "iphone";
	var J2ME_PATH = "j2me";
	var category = SMARTPHONE;
	var clientVersion = widget.clientVersion;
	if(clientVersion !=null && clientVersion != "null" && clientVersion != "undefined" && clientVersion != undefined && clientVersion != "")
	{
		clientVersion = clientVersion.toUpperCase();
	}
	widget.logWrite(7,"getDeviceCategory clientVersion::"+clientVersion);
	var devicemodel = widget.fetchUserAgent();
	if ((devicemodel.indexOf("BLACKBERRY") != -1) || (devicemodel.indexOf("RIM") != -1))
	{
		widget.resourcePath = BLACKBERRY_PATH;
	}
	else if(clientVersion.indexOf("IOS") != -1)
	{
		widget.resourcePath = IPHONE_PATH;
	}
	else if(clientVersion.indexOf("J2ME") != -1)
	{
		widget.resourcePath = J2ME_PATH;
	}
	else
	{
		widget.resourcePath = COMMON_PATH;
	}
	return category;
}
var DEVICE_CATEGORY = getDeviceCategory();

