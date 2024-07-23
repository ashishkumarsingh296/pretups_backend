package com.btsl.user.businesslogic;



public class MessageGatewayRedisCache implements Runnable {

	@Override
	public void run() {
		
		VMSCacheRepository vmsCacheRepository = (VMSCacheRepository) com.btsl.common.ApplicationContextProvider.getApplicationContext("TEST")
                .getBean(VMSCacheRepository.class);
		vmsCacheRepository.loadAllMessageGateway();
		
	}

}
