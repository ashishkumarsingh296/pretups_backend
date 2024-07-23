package com.btsl.pretups.iat.processes;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.ResponseGatewayVO;
import com.btsl.pretups.iat.businesslogic.IATNWServiceCache;
import com.btsl.pretups.iat.businesslogic.IATNetworkServiceMappingVO;
import com.btsl.pretups.iat.transfer.businesslogic.IATInterfaceVO;
import com.btsl.pretups.inter.module.IATInterfaceHandlerI;

/**
 * @(#)CheckIATStatus
 *                    Copyright(c) 2009, Bharti Telesoft Ltd.
 *                    All Rights Reserved
 * 
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    vikascyadav july 06, 2009 Initial Creation
 * 
 */
public class CheckIATStatus {
    private static Log _log = LogFactory.getLog(CheckIATStatus.class.getName());

    /*
     * this class will be responsible for getting the transaction status from
     * IAT
     * and will be used by ambiguous process/frontend and enquiry
     * and will be identified by isIATTxn (if value of this string will be IAT
     * then it will be sender
     * zebra transaction otherwise receiver zebra transaction) and source.
     * First get the IATinterfaceVO
     * find the IAT from cache if from sender side then on the basis of
     * interfaceID
     * if receiver side on the basis of ip /port
     * call the inhandler check status
     */
    public void checkIATTxnStatus(IATInterfaceVO iatInterfaceVO, String isIATTxn, String Source) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("checkIATTxnStatus()", "entered=" + iatInterfaceVO, "isIATTxn=" + isIATTxn);
        }
        final String METHOD_NAME = "checkIATTxnStatus";
        String iatHandlerClass = null;
        try {
            if (PretupsI.IAT_TRANSACTION_TYPE.equalsIgnoreCase(isIATTxn)) {
                // get the handler class on the basis of interfaceid.
                IATNetworkServiceMappingVO iatNetworkServiceMappingVO = IATNWServiceCache.getNetworkServiceIATIDObject(iatInterfaceVO.getIatInterfaceId());
                if (iatNetworkServiceMappingVO == null) {
                    _log.error(this, "Not able to found handler class as sender zebra" + isIATTxn);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CheckIATStatus[checkIATTxnStatus]", "", "", "", "Not able to found handler class as sende zebra");
                    throw new BTSLBaseException(this, "checkIATTxnStatus", PretupsErrorCodesI.IAT_NOT_FOUND);
                }
                iatHandlerClass = iatNetworkServiceMappingVO.getHandlerClass();

                // call the method check status on IAT
                checkIATFinalStatus(iatInterfaceVO, Source, iatHandlerClass);
            } else {
                // load the message gateway cache on the basis of gatewaycode
                // get handler class on the basis of these ip port
                MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(iatInterfaceVO.getIatGatewayCode());
                if (messageGatewayVO == null) {
                    _log.error(this, "Not able to found message gateway for IAT transaction" + iatInterfaceVO.getIatGatewayCode());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CheckIATStatus[checkIATTxnStatus]", "", "", "", "Not able to found IAt message gateway as receiver zebra");
                    throw new BTSLBaseException(this, "checkIATTxnStatus", PretupsErrorCodesI.IAT_NOT_FOUND);
                }
                ResponseGatewayVO responseGatewayVO = messageGatewayVO.getResponseGatewayVO();
                // need to be disscussed responseGatewayVO
                if (responseGatewayVO == null) {
                    _log.error(this, "response gateway vo null iatInterfaceVO.getIatGatewayCode=" + iatInterfaceVO.getIatGatewayCode());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CheckIATStatus[checkIATTxnStatus]", "", "", "", "Not able to found IAt message gateway as receiver zebra");
                    throw new BTSLBaseException(this, "checkIATTxnStatus", PretupsErrorCodesI.IAT_NOT_FOUND);
                }
                String key = messageGatewayVO.getHost() + "_" + responseGatewayVO.getServicePort();
                IATNetworkServiceMappingVO iatNetworkServiceMappingVO = IATNWServiceCache.getNetworkServiceIPObject(key);
                if (iatNetworkServiceMappingVO == null) {
                    _log.error(this, "Not able to found handler class as receiver zebra" + key);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CheckIATStatus[checkIATTxnStatus]", "", "", "", "Not able to found handler class as receiver zebra");
                    throw new BTSLBaseException(this, "checkIATTxnStatus", PretupsErrorCodesI.IAT_NOT_FOUND);
                }
                iatHandlerClass = iatNetworkServiceMappingVO.getHandlerClass();
                // using this interface id gethandler class
                // override interface id of user with interface id of iat
                iatInterfaceVO.setIatInterfaceId(iatNetworkServiceMappingVO.getIatCode());
                checkIATFinalStatus(iatInterfaceVO, Source, iatHandlerClass);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error(this, "Btsl base exception not able to send request to IAT");
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(this, "not able to send request to IAT");
            throw new BTSLBaseException(this, METHOD_NAME, "Exception in checking IAT Txn Status.");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("checkIATTxnStatus()", "Exited=" + iatInterfaceVO);
            }
        }

    }

    public void checkIATFinalStatus(IATInterfaceVO iatInterfaceVO, String Source, String className) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("checkIATFinalStatus()", "entered className=" + className, "Source=" + Source);
        }
        final String METHOD_NAME = "checkIATFinalStatus";
        try {
            IATInterfaceHandlerI iatInterfaceModule = getHandlerObj(className);// get
                                                                               // this
                                                                               // class
                                                                               // from
                                                                               // cache
            if (iatInterfaceModule != null) {
                if ("WEB".equalsIgnoreCase(Source)) {
                    iatInterfaceModule.checkTxnStatus(iatInterfaceVO, "WEB", 1, 0);
                } else {
                    iatInterfaceModule.checkTxnStatus(iatInterfaceVO, "PROCESS", 1, 0);
                }
            }

        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(this, "not able to send request to IAT");
            throw new BTSLBaseException(this, METHOD_NAME, "Exception in checking IAT Final Status.");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("checkIATFinalStatus()", "exited" + iatInterfaceVO.toString());
            }
        }

    }

    /**
     * Get Handler Object
     * 
     * @param handlerClassName
     * @return
     */
    public IATInterfaceHandlerI getHandlerObj(String handlerClassName) {
        if (_log.isDebugEnabled()) {
            _log.debug("getHandlerObj", "Entered handlerClassName:" + handlerClassName);
        }
        final String METHOD_NAME = "IATInterfaceHandlerI";
        IATInterfaceHandlerI handlerObj = null;
        try {
            handlerObj = (IATInterfaceHandlerI) Class.forName(handlerClassName).newInstance();

        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getHandlerObj", "Exception " + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getHandlerObj", "Exiting");
            }
        }
        return handlerObj;
    }// end of getHandlerObj
}
