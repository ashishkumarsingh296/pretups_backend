package com.btsl.user.businesslogic;

public class SysPreferenceCache implements  Runnable {
	
	   private SysPrefService systePrefService;
	   private VMSCacheRepository vmsCacheRepository;
	
	
	public void run() {
		systePrefService =(SysPrefService) com.btsl.common.ApplicationContextProvider.getApplicationContext("TEST")
                .getBean(SysPrefService.class);
		vmsCacheRepository =(VMSCacheRepository) com.btsl.common.ApplicationContextProvider.getApplicationContext("TEST")
                .getBean(VMSCacheRepository.class);
		systePrefService.loadAllSystemPreferences();
		systePrefService.loadSysPrefForModules();
		vmsCacheRepository.loadAllRegExSecurityProperties();
		
	}

}
