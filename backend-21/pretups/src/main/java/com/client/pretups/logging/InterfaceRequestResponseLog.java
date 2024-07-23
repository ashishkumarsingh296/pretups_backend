package com.client.pretups.logging;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class InterfaceRequestResponseLog {

    
    private static Log _log = LogFactory.getFactory().getInstance(InterfaceRequestResponseLog.class.getName());
	private static  SimpleDateFormat _sdf = new SimpleDateFormat ("yyMMddHHmm");
    /**
	 * Method log for interface transaction table.
	 * @param p_interfaceModuleVO InterfaceModuleVO
	 */
    public static void log(String IP,HashMap _map,String Request,String Response)
	{
		StringBuffer message=new StringBuffer();
		message.append("[URL: "+IP+"] ");
		//message.append("[START TIME: "+BTSLUtil.getDateStringFromDate(_map.get("IN_START_TIME").toString(), _sdf) +"] ");
		//message.append("[END TIME: "+_sdf.format(_map.get("IN_END_TIME").toString())+"] ");	
		//	message.append("[ERRORCODE: "+ErrorCode+"] ");
		message.append("[MAP: "+_map+"] ");
	//	message.append("[PROCESSING TIME: "+(Integer.parseInt((String)_map.get("IN_END_TIME"))-Integer.parseInt((String)_map.get("IN_START_TIME")))+"ms ]");
	//	message.append("[REQUEST: "+Request+"] ");
		message.append("[RESPONSE: "+Response+"] ");
		_log.info(" ",message.toString());
	}

}
