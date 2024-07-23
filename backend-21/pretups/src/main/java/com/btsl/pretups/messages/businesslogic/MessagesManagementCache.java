package com.btsl.pretups.messages.businesslogic;

/**
 * @(#)MessagesManagementCache.java
 *                                  Copyright(c) 2012, Comviva Technologies
 *                                  All Rights Reserved
 * 
 *                                  Give the country master list for IAT
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Jasmine July 06, 2009 Initital Creation
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 * 
 */

import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

public class MessagesManagementCache {
    private static Log _log = LogFactory.getLog(MessagesManagementCache.class.getName());

    private static ArrayList _messagesManagementCacheList = new ArrayList();

    /**
	 * ensures no instantiation
	 */
    private MessagesManagementCache(){
    	
    }
    public static void loadMessagManagementCache() throws BTSLBaseException {
    	final String methodName = "loadMessagManagementCache";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        _messagesManagementCacheList = loadMessagesManagementCache();

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
        	throw new BTSLBaseException("MessagesManagementCache", methodName, "Exception in loading Messagage Management Cache.");
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
    private static ArrayList loadMessagesManagementCache() throws BTSLBaseException {
    	final String methodName = "loadMessagesManagementCache";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        MessagesDAO messageDAO = new MessagesDAO();
        ArrayList list = null;
        try {
            list = messageDAO.loadMessageManagementList();

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
        	throw new BTSLBaseException("MessagesManagementCache", methodName, "Exception in loading Message Management Cache.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
        return list;
    }

    /**
     * get getmessagesManagementObject
     * 
     * @return ArrayList
     */
    public static ArrayList getmessagesManagementObject() {

        if (_log.isDebugEnabled()) {
            _log.debug("getmessagesManagementObject()", "entered ");
        }
        return _messagesManagementCacheList;
    }

}
