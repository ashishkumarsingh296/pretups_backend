/**
 * @(#)ServicePaymentMappingCache.java
 *                                     Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                     All Rights Reserved
 * 
 *                                     <description>
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     Author Date History
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     avinash.kamthan Mar 20, 2005 Initital
 *                                     Creation
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 * 
 */

package com.btsl.pretups.payment.businesslogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.CacheOperationLog;
import com.btsl.util.BTSLUtil;

/**
 * @author avinash.kamthan
 * 
 */
public class ServicePaymentMappingCache implements Runnable {

    private static Log _log = LogFactory.getLog(ServicePaymentMappingCache.class.getName());
    private static final String CLASS_NAME = "ServicePaymentMappingCache";

    private static HashMap _serviceDefaultMap = new HashMap();
    private static HashMap _servicePaymentMap = new HashMap();

    public void run() {
		try {
			Thread.sleep(50);
			loadServicePaymentMappingOnStartUp();
		} catch (Exception e) {
			_log.error("loadServicePaymentMappingOnStartUp init() Exception ", e);
		}
	}
    
    /**
     * To load the Service Payment Mapping onn Start up
     * @throws Exception 
     * 
     */
    public static void loadServicePaymentMappingOnStartUp() throws BTSLBaseException {
    	final String methodName = "loadServicePaymentMappingOnStartUp";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        
        loadServicePaymentMapping(false);
        
        }
        catch(BTSLBaseException be) {
        	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e)
        {
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading the Service Payment Mapping on Start up.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    /**
     * load the payment mapping from data base and filter them
     * on the bases of default payment value and put them in diffrent map.
     * 
     * comparing both old values and new values on the bases of flag.
     * 
     * @param p_isCompareReqired
     * @throws Exception 
     */
    private static void loadServicePaymentMapping(boolean p_isCompareReqired) throws BTSLBaseException {
    	final String methodName = "loadServicePaymentMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED + p_isCompareReqired);
    	}
        PaymentDAO paymentMethodDAO = new PaymentDAO();
        try {
            ArrayList list = paymentMethodDAO.loadServicePaymentMappingCache();
            HashMap tempPaymentMap = new HashMap();
            HashMap tempDefaultMap = new HashMap();
            ServicePaymentMappingVO mappingVO = null;
            String key = null;
            for (int i = 0, k = list.size(); i < k; i++) {
                mappingVO = (ServicePaymentMappingVO) list.get(i);
                key = mappingVO.getServiceType() + "_" + mappingVO.getSubscriberType();
                if (mappingVO.getDefaultPaymentMethod() != null) {
                    tempDefaultMap.put(key, mappingVO);
                }
                key += "_" + mappingVO.getPaymentMethod();
                tempPaymentMap.put(key, mappingVO);
            }
            if (p_isCompareReqired) {
                compareMaps(_serviceDefaultMap, tempDefaultMap);
                compareMaps(_servicePaymentMap, tempPaymentMap);
            }
            _serviceDefaultMap = tempDefaultMap;
            _servicePaymentMap = tempPaymentMap;
        }
        catch(BTSLBaseException be) {
        	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e)
        {
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading the Service Payment Mapping.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    /**
     * To update the cache value
     * @throws Exception 
     * 
     */
    public static void updateServicePaymentMapping() throws BTSLBaseException {
    	final String methodName = "updateServicePaymentMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        loadServicePaymentMapping(true);
        }
        catch(BTSLBaseException be) {
        	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e)
        {
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in updating the Service Payment Mapping.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    /**
     * get the default payment method
     * 
     * @param p_serviceType
     * @param p_subscriberType
     * @return
     */
    public static String getDefaultPaymentMethod(String p_serviceType, String p_subscriberType) {
        if (_log.isDebugEnabled()) {
            _log.debug("getDefaultPaymentMethod()", "Entered Service Type " + p_serviceType + "  Subscriber Type " + p_subscriberType);
        }
        String paymentMethod = null;
        ServicePaymentMappingVO servicePaymentMappingVO = (ServicePaymentMappingVO) _serviceDefaultMap.get(p_serviceType + "_" + p_subscriberType);
        if (servicePaymentMappingVO == null) {
            return null;
        }
        paymentMethod = servicePaymentMappingVO.getDefaultPaymentMethod();

        if (_log.isDebugEnabled()) {
            _log.debug("getDefaultPaymentMethod()", "Exited Payment method is : " + paymentMethod);
        }

        return paymentMethod;
    }

    /**
     * To check whether this payment exist with given service type and
     * subscriber type
     * 
     * @param p_serviceType
     * @param p_subscriberType
     * @param p_paymentMethod
     * @return
     */
    public static boolean isMappingExist(String p_serviceType, String p_subscriberType, String p_paymentMethod) {

        boolean paymentMethodExist = false;
        if (_log.isDebugEnabled()) {
            _log.debug("isMappingExist()", "Entered p_serviceType: " + p_serviceType + "  p_subscriberType: " + p_subscriberType + " p_paymentMethod: " + p_paymentMethod);
        }

        paymentMethodExist = _servicePaymentMap.containsKey(p_serviceType + "_" + p_subscriberType + "_" + p_paymentMethod);

        if (_log.isDebugEnabled()) {
            _log.debug("isMappingExist()", "Exited :: paymentMethodExist: " + paymentMethodExist);
        }

        return paymentMethodExist;

    }

    /**
     * compare two hashmap and check which have changed and log the value which
     * has been changed
     * 
     * @param p_previousMap
     * @param p_currentMap
     *            void
     */
    private static void compareMaps(HashMap p_previousMap, HashMap p_currentMap) {
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Entered p_previousMap: " + p_previousMap + "  p_currentMap: " + p_currentMap);
        }
        final String METHOD_NAME = "compareMaps";
        try {
            Iterator iterator = null;
            Iterator copiedIterator = null;

            if (p_previousMap.size() == p_currentMap.size()) {
                iterator = p_previousMap.keySet().iterator();
                copiedIterator = p_previousMap.keySet().iterator();
            } else if (p_previousMap.size() > p_currentMap.size()) {
                iterator = p_previousMap.keySet().iterator();
                copiedIterator = p_previousMap.keySet().iterator();
            } else if (p_previousMap.size() < p_currentMap.size()) {
                iterator = p_currentMap.keySet().iterator();
                copiedIterator = p_previousMap.keySet().iterator();
            }

            // to check whether any new network added or not but size of
            boolean isNewAdded = false;

            while (iterator != null && iterator.hasNext()) {
                String key = (String) iterator.next();
                ServicePaymentMappingVO prevVO = (ServicePaymentMappingVO) p_previousMap.get(key);
                ServicePaymentMappingVO curVO = (ServicePaymentMappingVO) p_currentMap.get(key);

                if ((prevVO != null) && (curVO == null)) {
                    isNewAdded = true;
                    CacheOperationLog.log("ServicePaymentMappingCache", BTSLUtil.formatMessage("Delete", prevVO.getServiceSubscriberMapping(), prevVO.logInfo()));
                } else if ((prevVO == null) && (curVO != null)) {
                    CacheOperationLog.log("ServicePaymentMappingCache", BTSLUtil.formatMessage("Add", curVO.getServiceSubscriberMapping(), curVO.logInfo()));
                } else if ((prevVO != null) && (curVO != null)) {
                    if (!curVO.equalsServicePaymentMappingVO(prevVO)) {
                        CacheOperationLog.log("ServicePaymentMappingCache", BTSLUtil.formatMessage("Modify", curVO.getServiceSubscriberMapping(), curVO.differences(prevVO)));
                    }
                }
            }

            /**
             * Note: this case arises when same number of network added and
             * deleted as well
             */
            if ((p_previousMap.size() == p_currentMap.size()) && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);

                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                Iterator iterator2 = tempMap.keySet().iterator();

                while (iterator2.hasNext()) {
                    // new added
                    ServicePaymentMappingVO VO = (ServicePaymentMappingVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("ServicePaymentMappingCache", BTSLUtil.formatMessage("Add", VO.getServiceSubscriberMapping(), VO.logInfo()));
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Exited");
        }
    }
}
