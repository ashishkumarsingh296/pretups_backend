package com.inter.zteethopia;

/**
 * @(#)ZTEPoolManager.java
 *                         Copyright(c) 2009, Bharti Telesoft Int. Public Ltd.
 *                         All Rights Reserved
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Shamit June 27, 2009 Initial Creation
 *                         ----------------------------------------------------
 *                         -------------------------------------------
 *                         This class is responsible to instantiate the Client
 *                         object and maintain a pool.
 */
import org.apache.log4j.Logger;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

public class ZTEINNewClientConnection {

    static Logger _logger = Logger.getLogger(ZTEINNewClientConnection.class.getName());

    /**
     * This method is used to get the new client object based on the interface
     * id
     * 
     * @param p_interfaceID
     * @return Object
     * @throws BTSLBaseException
     */
    public static synchronized ZTEINSocketWrapper getNewClientObject(String fileCacheId, String p_interfaceID) throws BTSLBaseException {
        if (_logger.isDebugEnabled())
            _logger.debug("getNewClientObject p_interfaceID::" + p_interfaceID);
        ZTEINSocketWrapper ZTEKnFixSocketWrapper = null;
        try {
            ZTEKnFixSocketWrapper = new ZTEINSocketWrapper(fileCacheId, p_interfaceID);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("getNewClientObject Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEPoolManager[getNewClientObject]", "INTERFACEID:" + p_interfaceID, "", "", "While getting the instance of Client objects got the Exception " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_GETTIN_NEW_CLIENT_OBJECT);
        } finally {
            if (_logger.isDebugEnabled())
                _logger.debug("getNewClientObject Exiting ZTEKnFixSocketWrapper:" + ZTEKnFixSocketWrapper);
        }
        return ZTEKnFixSocketWrapper;
    }

    public static synchronized ZTEINSocketWrapper getNewClientObject(String fileCacheId, String p_interfaceID, ZTEINSocket socket) throws BTSLBaseException {
        if (_logger.isDebugEnabled())
            _logger.debug("getNewClientObject p_interfaceID::" + p_interfaceID);
        ZTEINSocketWrapper ZTEKnFixSocketWrapper = null;
        try {
            ZTEKnFixSocketWrapper = new ZTEINSocketWrapper(fileCacheId, p_interfaceID, socket);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("getNewClientObject Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEPoolManager[getNewClientObject]", "INTERFACEID:" + p_interfaceID, "", "", "While getting the instance of Client objects got the Exception " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_GETTIN_NEW_CLIENT_OBJECT);
        } finally {
            if (_logger.isDebugEnabled())
                _logger.debug("getNewClientObject Exiting ZTEKnFixSocketWrapper:" + ZTEKnFixSocketWrapper);
        }
        return ZTEKnFixSocketWrapper;
    }

}
