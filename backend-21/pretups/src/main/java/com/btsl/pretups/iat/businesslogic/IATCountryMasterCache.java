/**
 * @(#)IATCountryMasterCache.java
 *                                Copyright(c) 2009, Bharti Telesoft Ltd.
 *                                All Rights Reserved
 * 
 *                                Give the country master list for IAT
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                vikas.yadav July 06, 2009 Initital Creation
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 * 
 */

package com.btsl.pretups.iat.businesslogic;

import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

public class IATCountryMasterCache implements Runnable {
    private static Log _log = LogFactory.getLog(IATCountryMasterCache.class.getName());

    public void run() {
        try {
            Thread.sleep(50);
            loadIATCountryMasterCache();
        } catch (Exception e) {
        	 _log.error("IATCountryMasterCache init() Exception ", e);
        }
    }
    private static ArrayList _countryMasterList = new ArrayList();

    public static void loadIATCountryMasterCache() throws BTSLBaseException {
    	final String methodName = "loadIATCountryMasterCache";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
		try{
        _countryMasterList = loadCountryMasterCache();
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
			throw new BTSLBaseException("IATCountryMasterCache", methodName, "Exception in loading Country Master Cache.");
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, PretupsI.EXITED);
			}
		}
    }

    /**
     * To load the country master details
     * 
     * @return
     *         HashMap
     * @throws Exception 
     */
    private static ArrayList loadCountryMasterCache() throws BTSLBaseException {
    	final String methodName = "loadCountryMasterCache";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
    	
        IATDAO iatDAO = new IATDAO();
        ArrayList list = null;
        try {
            list = iatDAO.loadIATCountryMasterList();

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
        	throw new BTSLBaseException("IATCountryMasterCache", methodName, "Exception in loading Country Master Cache.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
        return list;
    }

    /**
     * get getIATCountryMasterObject
     * 
     * @return ArrayList
     */
    public static ArrayList getIATCountryMasterObject() {

        if (_log.isDebugEnabled()) {
            _log.debug("getIATCountryMasterObject()", "entered ");
        }
        return _countryMasterList;
    }

}
