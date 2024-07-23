/**
 * @(#)VASServiceAPPController.java
 *                                  This controller is to retrieve VAS services
 *                                  enabled for a retailer for retailer mobile
 *                                  app.
 * 
 */

package com.btsl.pretups.user.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.util.Constants;

/**
 * 
 */
public class VASServiceAPPController implements ServiceKeywordControllerI {
    /**
     * Field _log.
     */
    private static final Log _log = LogFactory.getLog(VASServiceAPPController.class.getName());

    /**
     * 
     * @param p_requestVO
     *            RequestVO
     * @see com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI#process(RequestVO)
     */

    @Override
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";

        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
		String serviceType = null;
        try {
        	
        	 final String messageArr[] = p_requestVO.getRequestMessageArray();
        	 
        	  if (_log.isDebugEnabled()) {
                  _log.debug("process", " p_requestVO.getRequestMessageArray() " + p_requestVO.getRequestMessageArray()+"messageArr.length:"+messageArr.length);
              }
             if (messageArr.length == 3) {
            	 serviceType =messageArr[2];
             }
             
             if (_log.isDebugEnabled()) {
                 _log.debug("process", " serviceType " + serviceType);
             }
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            ArrayList<ServiceSelectorMappingVO> list1 = new ArrayList<ServiceSelectorMappingVO>();
            final StringBuilder sbf1 = new StringBuilder();
            boolean groupingAllowed=false;
            
            String groupServices=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MAPP_PRODUCT_GROUPING_REQ_SRV));
			if(null!=groupServices)
			{
				String[] groupingAllowedSer=groupServices.split(",");
				List<String> list = Arrays.asList(groupingAllowedSer);
				if(list.contains(serviceType)){
					groupingAllowed=true;
				}
			}
            p_requestVO.setInfo1(""+groupingAllowed);
            
            ArrayList  subServiceTypeList = ServiceSelectorMappingCache.loadSelectorDropDownForCardGroupPPEL();// LookupsCache.loadLookupDropDown(PretupsI.SUB_SERVICES,true);
        	if(!subServiceTypeList.isEmpty()) {
	        	ListIterator<ListValueVO> listvoiterator = subServiceTypeList.listIterator();
	        	String [] subServiceArr; 
	        	int recordCount=0;
	        	while(listvoiterator.hasNext()) 
	        	{
	        		ListValueVO listvaluevo = listvoiterator.next();
	        		subServiceArr = listvaluevo.getValue().split(":");
		             if (_log.isDebugEnabled()) {
		            	 if(subServiceArr.length >=5)
		                 _log.debug("process", " isDth " + subServiceArr[0]+","+subServiceArr[1]+","+subServiceArr[2]+","+subServiceArr[3]+","+subServiceArr[4] );
		             }
	        				if (serviceType!=null && serviceType.equals(subServiceArr[0]))
	        				{
	        					if(recordCount!=0) {sbf1.append(Constants.getProperty("MAPP_SERVICEINFO_RECORD_SEPARATOR"));}
		        		 		sbf1.append(listvaluevo.getLabel());
		        		 		sbf1.append(Constants.getProperty("MAPP_SERVICEINFO_RECORD_VALUE_SEPARATOR"));
		        		 		sbf1.append((subServiceArr[1]));
		        		 		sbf1.append(Constants.getProperty("MAPP_SERVICEINFO_RECORD_VALUE_SEPARATOR"));
		        		 		sbf1.append((subServiceArr[2]));
		        		 		if(groupingAllowed) {
		        		 		sbf1.append(Constants.getProperty("MAPP_SERVICEINFO_RECORD_VALUE_SEPARATOR"));
		        		 		sbf1.append((subServiceArr[4]));
		        		 		}
								recordCount++;
	        			 	}
	        	}
        	}
        	else {
        		 sbf1.append("NA");

        	}
            String   str1 = sbf1.toString();
            final String msgarg[] = { str1.trim() };
            p_requestVO.setMessageArguments(msgarg);
            p_requestVO.setMessageCode(PretupsErrorCodesI.MAPP_VAS_SERVICES);

                } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.MAPP_VAS_SERVICES_FAILED);
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VASServiceAPPController[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.MAPP_VAS_SERVICES_FAILED);
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("VASServiceAPPController#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }

        }
        return;
    }

}
