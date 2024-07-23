package com.btsl.pretups.channel.user.businesslogic;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

public class MobileAppRestServiceImpl implements MobileAppRestService {

	public static final Log log = LogFactory.getLog(MobileAppRestServiceImpl.class.getName());
	static final  String CLASSNAME = "MobileAppRestServiceImpl";

	
	@Override
	public PretupsResponse<Double> loadAppVersion(String requestData) throws  Exception {

		final String methodName = "loadCategoryData";
		
		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+"#"+methodName, PretupsI.ENTERED);
		}
		PretupsResponse<Double> response = new PretupsResponse<>();
		
		try{
			 	 response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,((Double)PreferenceCache.getSystemPreferenceValue(PreferenceI.MOBILE_APP_VERSION)).doubleValue());
			
		}catch (Exception  e) {
			throw new BTSLBaseException(e);
		}
		finally {
           
       
       		if (log.isDebugEnabled()) {
       			log.debug(CLASSNAME+"#"+methodName, PretupsI.EXITED);
       		}
           
        }
		
		
		return response;
		}

}
