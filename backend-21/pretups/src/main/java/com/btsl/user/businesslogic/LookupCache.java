package com.btsl.user.businesslogic;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btsl.user.businesslogic.entity.LookupTypes;

public class LookupCache  implements Runnable{

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(LookupCache.class);

	@Override
	public void run() {
		
		VMSCacheRepository vmsCacheRepository = (VMSCacheRepository) com.btsl.common.ApplicationContextProvider.getApplicationContext("TEST")
                .getBean(VMSCacheRepository.class);
		LookupTypesRepository lookupTypeRep = (LookupTypesRepository) com.btsl.common.ApplicationContextProvider.getApplicationContext("TEST")
                .getBean(LookupTypesRepository.class);
		List<LookupTypes> listLookups =lookupTypeRep.getAllLookUpTypes();
		for(LookupTypes lookupType : listLookups ) {
			LOGGER.debug( "*** Loading Lookup Type data" + lookupType.getLookupType() );
			vmsCacheRepository.loadLookupDropDown(lookupType.getLookupType());
		}
		
		
	}

}
