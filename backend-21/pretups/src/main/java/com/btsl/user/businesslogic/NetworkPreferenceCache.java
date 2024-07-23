package com.btsl.user.businesslogic;

public class NetworkPreferenceCache implements  Runnable {
	
	   private SysPrefService systePrefService;
	
	
	public void run() {
		systePrefService =(SysPrefService) com.btsl.common.ApplicationContextProvider.getApplicationContext("TEST")
                .getBean(SysPrefService.class);
		systePrefService.loadAllnetworkPreferences();
		systePrefService.loadAllcontrolPreferences();
	}

}
