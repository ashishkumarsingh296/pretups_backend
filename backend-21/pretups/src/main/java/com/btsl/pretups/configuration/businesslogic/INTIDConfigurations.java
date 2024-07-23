package com.btsl.pretups.configuration.businesslogic;
/**
* @(#)INTIDConfigurations.java
* Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
* All Rights Reserved
* This class is used to store System Preferences for Pretups System.
*-------------------------------------------------------------------------------------------------
* Author						Date			History
* Sanjay Kumar Bind1            May 7, 2017     Initital Creation
* ------------------------------------------------------------------------------------------------
*/

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @author sanjay.bind1
 *
 */
public class INTIDConfigurations {

	private static Log log = LogFactory.getLog(INTIDConfigurations.class.getName());
	private static String DEFAULT_LANGUAGE = null;//for default language
	private static String DEFAULT_COUNTRY = null;//for default country
	
	public static void load()
	{
		final String methodName = "load";
		LogFactory.printLog(methodName, "Entered", log);
		try
		{
			DEFAULT_LANGUAGE = (String)(ConfigurationCache.getSystemPreferenceValue(ConfigurationI.DEFAULT_LANGUAGE ));
			DEFAULT_COUNTRY = (String)(ConfigurationCache.getSystemPreferenceValue(ConfigurationI.DEFAULT_COUNTRY ));
		}
		catch(Exception e)
		{
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"INTIDConfigurations["+methodName+"]","","","","Exception:"+e.getMessage());
			log.errorTrace(methodName, e);
		}
		
	}
	public static void reload()
	{
		load();
	}
	private INTIDConfigurations() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
