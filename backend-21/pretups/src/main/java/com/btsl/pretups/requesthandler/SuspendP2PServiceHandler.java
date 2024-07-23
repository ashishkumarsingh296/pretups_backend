package com.btsl.pretups.requesthandler;

/**
 * * @(#)SuspendP2PServiceHandler.java
 * Copyright(c) 2005-2006, Bharti Telesoft Ltd.
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Siddhartha Srivastava Dec 7, 2006 Initial Creation
 * 
 * This class handles the request for the suspension of P2P services for a
 * subscriber.
 * The subscriber will be suspended for the services based on the mobile number
 * sent to the customer care.
 * 
 */

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

public class SuspendP2PServiceHandler implements ServiceKeywordControllerI {

    private Log log = LogFactory.getLog(SuspendP2PServiceHandler.class.getName());
    private HashMap _requestMap = null;
    private RequestVO _requestVO = null;
    private SenderVO _senderVO = null;

    /**
     * This is the entry point and only public method of the class.The process
     * involved in the P2P services
     * suspension for a subscriber are called from this method.
     * 
     * @param p_requestVO
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (log.isDebugEnabled()) {
            log.debug("process", "Entered....: p_requestVO= " + p_requestVO);
        }

        _requestVO = p_requestVO;
        Connection con = null;
        MComConnectionI mcomCon = null;

        try {
            // retreiving the Map which has all the values in the request.These
            // values can be retreived by
            // using the tag name
            _requestMap = _requestVO.getRequestMap();
            // validates the parameter passed in the request eg. msisdn
            validate();

            mcomCon = new MComConnection();con=mcomCon.getConnection();
            // loading the details of the subscriber based on the passed msisdn
            // in the request
            loadUserDetails(con);
            // this method actually suspends the service P2P
            suspendService(con);

            // setting the transaction status to true
            _requestVO.setSuccessTxn(true);
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            _requestVO.setSuccessTxn(false);
            log.error("process", "BTSLBaseException " + be.getMessage());
            if (be.isKey()) {
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else {
                _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            // Rollbacking the transaction
            try {
                if (con != null) {
                  mcomCon.finalRollback();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            // be.printStackTrace();
        }// end of BTSLBaseException
        catch (Exception ex) {
            log.errorTrace(METHOD_NAME, ex);
            _requestVO.setSuccessTxn(false);
            // Rollbacking the transaction
            try {
                if (con != null) {
                   mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                log.errorTrace(METHOD_NAME, ee);
            }
            log.error("process", "Exception " + ex.getMessage());

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SuspendP2PServiceHandler[process]", "", "", "", "Exception:" + ex.getMessage());
            _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }// end of Exception
        finally {
            p_requestVO = _requestVO;
            p_requestVO.setRequestMap(_requestMap);
            _requestMap = null;
            _requestVO = null;
            _senderVO = null;
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("SuspendP2PServiceHandler#process");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug("process", " Exited ");
            }
        }// end of finally
        if (log.isDebugEnabled()) {
            log.debug("process", "Exiting ....: ");
        }
    }

    /**
     * This methods gets the msisdn from the requestMap and validates it. It
     * checks whether the msisdn is not null.
     * Also it checks whether the filteredMsisdn is from the network passed in
     * the request
     * 
     * @throws BTSLBaseException
     */
    private void validate() throws BTSLBaseException {
        final String METHOD_NAME = "validate";
        if (log.isDebugEnabled()) {
            log.debug("validate", "Entered    ");
        }

        String msisdnPrefix = null;
        String filteredMsisdn = null;

        try {
            // getting the msisdn of the retailer
            String msisdn = (String) _requestMap.get("MSISDN");
            // checking whether the msisdn is not blank
            if (BTSLUtil.isNullString(msisdn)) {
                _requestMap.put("RES_ERR_KEY", "MSISDN");
                throw new BTSLBaseException("SuspendP2PServiceHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }

            // filtering the msisdn for country independent dial format
            filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
            if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                _requestMap.put("RES_ERR_KEY", "MSISDN");
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
            }
            msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
            _requestVO.setFilteredMSISDN(filteredMsisdn);
            // checking whether the msisdn prefix is valid in the network
            NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            if (networkPrefixVO == null) {
                String[] arr = new String[] { filteredMsisdn };
                throw new BTSLBaseException("SuspendP2PServiceHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK, arr);
            }
            String networkCode = networkPrefixVO.getNetworkCode();
            if (networkCode != null && !networkCode.equals(((UserVO) _requestVO.getSenderVO()).getNetworkID())) {
                throw new BTSLBaseException("SuspendP2PServiceHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
            }
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            log.error("validate", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("validate", "Exception " + e.getMessage());

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SuspendP2PServiceHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SuspendP2PServiceHandler", "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (log.isDebugEnabled()) {
            log.debug("validate", "Exiting filteredMsisdn= " + filteredMsisdn);
        }
    }

    /**
     * This method loads the details of the user based on the msisdn passed in
     * the request.This method creates
     * an instance of SenderVO and assigns it to the global variable _senderVO
     * 
     * @param p_con
     */
    private void loadUserDetails(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserDetails";
        if (log.isDebugEnabled()) {
            log.debug("loadUserDetails", "Entered....");
        }

        Date currentDate = new Date(System.currentTimeMillis());

        try {
            SubscriberDAO subscriberDAO = new SubscriberDAO();
            ArrayList subscriberList = subscriberDAO.loadSubscriberDetails(p_con, _requestVO.getFilteredMSISDN(), null, null, PretupsI.ALL,null);

            if (subscriberList == null || subscriberList.size() <= 0) {
                throw new BTSLBaseException("SuspendP2PServiceHandler", "loadUserDetails", PretupsErrorCodesI.CCE_ERROR_SUBSCRIBER_DETAIL_NOT_FOUND);
            }

            _senderVO = (SenderVO) subscriberList.get(0);
            _senderVO.setModifiedBy(((UserVO) _requestVO.getSenderVO()).getUserID());
            _senderVO.setModifiedOn(currentDate);
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            log.error("loadUserDetails", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error("loadUserDetails", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SuspendP2PServiceHandler[loadUserDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SuspendP2PServiceHandler", "loadUserDetails", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (log.isDebugEnabled()) {
            log.debug("loadUserDetails", "Exiting ....: ");
        }
    }

    /**
     * This method is the method which actually suspends the P2P services of the
     * subscriber.
     * The subscriber is determined from the MSISDN passed in the request
     * 
     * @param p_con
     * @throws BTSLBaseException
     */
    private void suspendService(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "suspendService";
        if (log.isDebugEnabled()) {
            log.debug("suspendService", "Entering ....: ");
        }

        SubscriberDAO subscriberDAO = new SubscriberDAO();

        try {
            int updateCount = 0;
            BTSLMessages btslMessage = null;
            if (!_senderVO.getStatus().equals(PretupsI.USER_STATUS_NEW)) {
                if (_senderVO.getStatus().equals(PretupsI.USER_STATUS_SUSPEND)) {
                    throw new BTSLBaseException("SuspendP2PServiceHandler", "suspendService", PretupsErrorCodesI.CCE_ERROR_USER_ALREADY_SUSPENDED);
                } else {
                    btslMessage = new BTSLMessages(PretupsErrorCodesI.P2PSUBSCRIBER_SERVICE_SUSPEND);
                    _senderVO.setStatus(PretupsI.USER_STATUS_SUSPEND);
                }
            } else {
                throw new BTSLBaseException("SuspendP2PServiceHandler", "suspendService", PretupsErrorCodesI.CCE_ERROR_USER_STATUS_NEW);
            }

            updateCount = subscriberDAO.updateSubscriberStatus(p_con, _senderVO);
            if (p_con != null) {
                if (updateCount > 0) {
                    p_con.commit();
                    Locale locale = new Locale(_senderVO.getLanguage(), _senderVO.getCountry());
                    PushMessage pushMessage = new PushMessage(_senderVO.getMsisdn(), btslMessage, null, null, locale, _senderVO.getNetworkCode());
                    pushMessage.push();
                } else {
                    throw new BTSLBaseException("SuspendP2PServiceHandler", "suspendService", PretupsErrorCodesI.REQ_NOT_PROCESS);
                }
            }
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            log.error("suspendService", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error("suspendService", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SuspendP2PServiceHandler[suspendService]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SuspendP2PServiceHandler", "suspendService", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (log.isDebugEnabled()) {
            log.debug("suspendService", "Exiting....");
        }
    }
}
