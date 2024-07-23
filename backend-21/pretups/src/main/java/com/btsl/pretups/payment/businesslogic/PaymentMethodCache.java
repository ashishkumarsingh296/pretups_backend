/**
 * @(#)PaymentMethodCache.java
 *                             Copyright(c) 2005, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 * 
 *                             <description>
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             avinash.kamthan June 20, 2005 Initital Creation
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 * 
 */

package com.btsl.pretups.payment.businesslogic;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

/**
 * @author avinash.kamthan
 * 
 */
public class PaymentMethodCache implements Runnable {

    private static Log _log = LogFactory.getLog(PaymentMethodCache.class.getName());
    private static final String CLASS_NAME = "PaymentMethodCache";

    private static HashMap _paymentMethodMap = new HashMap();
    
    public void run() {
        try {
            Thread.sleep(50);
            loadPaymentMethodCacheOnStartUp();
        } catch (Exception e) {
        	 _log.error("PaymentMethodCache init() Exception ", e);
        }
    }

    public static void loadPaymentMethodCacheOnStartUp() throws BTSLBaseException {
    	final String methodName = "loadPaymentMethodCacheOnStartUp";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        _paymentMethodMap = loadPaymentMethod();

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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading payment method cache on startup.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }

    }

    /**
     * To load the Payment methods from data base
     * 
     * @return Hashmap
     * @throws Exception 
     */
    private static HashMap loadPaymentMethod() throws BTSLBaseException {
    	final String methodName = "loadPaymentMethod";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}

        PaymentDAO paymentMethodDAO = new PaymentDAO();
        HashMap map = null;

        try {
            map = paymentMethodDAO.loadPaymentKeywordCache();
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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading payment method cache.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug("loadPaymentMethod()", PretupsI.EXITED + map.size());
        	}
        }

        return map;

    }

    /**
     * to update the payment methods which are modified. and put them in cache.
     * also compare them from current value to previous value.
     * @throws Exception 
     * 
     */
    public static void updatePaymentMethod() throws BTSLBaseException {
    	final String methodName = "updatePaymentMethod";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        HashMap currentMap = loadPaymentMethod();

        if ((_paymentMethodMap != null) && (_paymentMethodMap.size() > 0)) {
            // commented by deepika aggarwal while pretups code optimisation
            // compareMaps(_paymentMethodMap, currentMap);
        }

        _paymentMethodMap = currentMap;

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
        	throw new BTSLBaseException("PaymentMethodCache", methodName, "Exception in updating payment methods.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        		_log.debug("updatePaymentMethod()", PretupsI.EXITED + " size is " + _paymentMethodMap.size());
        	}
        }
    }

    /**
     * compare two hashmap and check which have changed and log the value which
     * has been changed
     * 
     * @param p_previousMap
     * @param p_currentMap
     *            void
     */
    /*
     * commented by deepika aggarwal while pretups 6.0 code optimisation as
     * entire body of code was already commented
     * private static void compareMaps(HashMap p_previousMap, HashMap
     * p_currentMap)
     * /*{
     * if (_log.isDebugEnabled())
     * {
     * _log.debug("compareMaps()", "Entered PreviousMap " + p_previousMap +
     * "  Current Map" + p_currentMap);
     * }
     * 
     * Iterator iterator = null;
     * Iterator copiedIterator = null;
     * 
     * if (p_previousMap.size() == p_currentMap.size())
     * {
     * iterator = p_previousMap.keySet().iterator();
     * copiedIterator = p_previousMap.keySet().iterator();
     * }
     * else if (p_previousMap.size() > p_currentMap.size())
     * {
     * iterator = p_previousMap.keySet().iterator();
     * copiedIterator = p_previousMap.keySet().iterator();
     * }
     * else if (p_previousMap.size() < p_currentMap.size())
     * {
     * iterator = p_currentMap.keySet().iterator();
     * copiedIterator = p_previousMap.keySet().iterator();
     * }
     * 
     * // to check whether any new network added or not but size of
     * boolean isNewAdded = false;
     * 
     * while (iterator.hasNext())
     * {
     * String key = (String) iterator.next();
     * PaymentMethodKeywordVO prevpaymentMethodVO = (PaymentMethodKeywordVO)
     * p_previousMap.get(key);
     * PaymentMethodKeywordVO curpaymentMethodVO = (PaymentMethodKeywordVO)
     * p_currentMap.get(key);
     * 
     * if ((prevpaymentMethodVO != null) && (curpaymentMethodVO == null))
     * {
     * isNewAdded = true;
     * _log.info("compareMaps()", BTSLUtil.formatMessage("Delete",
     * prevpaymentMethodVO.getKeywordInterface(), prevpaymentMethodVO.logInfo())
     * );
     * 
     * System.out.println( BTSLUtil.formatMessage("Delete",
     * prevpaymentMethodVO.getKeywordInterface(), prevpaymentMethodVO.logInfo())
     * );
     * }
     * else if ((prevpaymentMethodVO == null) && (curpaymentMethodVO != null))
     * {
     * _log.info("compareMaps()", BTSLUtil.formatMessage("Add",
     * curpaymentMethodVO.getKeywordInterface(), curpaymentMethodVO.logInfo())
     * );
     * 
     * System.out.println( BTSLUtil.formatMessage("Add",
     * curpaymentMethodVO.getKeywordInterface(), curpaymentMethodVO.logInfo())
     * );
     * }
     * else if ((prevpaymentMethodVO != null) && (curpaymentMethodVO != null))
     * {
     * if (! curpaymentMethodVO.equals(prevpaymentMethodVO))
     * {
     * _log.info("compareMaps()", BTSLUtil.formatMessage("Modify",
     * curpaymentMethodVO.getKeywordInterface(),
     * curpaymentMethodVO.differences(prevpaymentMethodVO)) );
     * 
     * System.out.println( BTSLUtil.formatMessage("Modify",
     * curpaymentMethodVO.getKeywordInterface(),
     * curpaymentMethodVO.differences(prevpaymentMethodVO)) );
     * }
     * }
     * }
     *//**
     * Note: this case arises when same number of network added and deleted
     * as well
     */
    /*
     * if ((p_previousMap.size() == p_currentMap.size()) && isNewAdded)
     * {
     * HashMap tempMap = new HashMap(p_currentMap);
     * 
     * while (copiedIterator.hasNext())
     * {
     * tempMap.remove((String) copiedIterator.next());
     * }
     * 
     * Iterator iterator2 = tempMap.keySet().iterator();
     * 
     * while (iterator2.hasNext())
     * {
     * //new added
     * PaymentMethodKeywordVO methodKeywordVO = (PaymentMethodKeywordVO)
     * p_currentMap.get(iterator2.next());
     * _log.info("compareMaps()", BTSLUtil.formatMessage("Add",
     * methodKeywordVO.getKeywordInterface(), methodKeywordVO.logInfo()) );
     * System.out.println( BTSLUtil.formatMessage("Add",
     * methodKeywordVO.getKeywordInterface(), methodKeywordVO.logInfo()) );
     * }
     * }
     * 
     * if (_log.isDebugEnabled())
     * {
     * _log.debug("compareMaps()", "Exited");
     * }
     */
    // }

    /**
     * to get the payment method on the bases of keyword type and request
     * interface
     * 
     * @param p_keywordType
     * @param p_reqInterfaceType
     * @return PaymentMethodKeywordVO
     */
    public static PaymentMethodKeywordVO getObject(String p_keyword, String p_serviceType, String p_networkCode) {

        PaymentMethodKeywordVO paymentMethodKeywordVO = null;

        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Entered Keyword Type=" + p_keyword + "  p_serviceType= " + p_serviceType + " p_networkCode=" + p_networkCode);
        }

        paymentMethodKeywordVO = (PaymentMethodKeywordVO) _paymentMethodMap.get(p_keyword + "_" + p_serviceType + "_" + p_networkCode);

        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Exited " + paymentMethodKeywordVO);
        }

        return paymentMethodKeywordVO;
    }

}
