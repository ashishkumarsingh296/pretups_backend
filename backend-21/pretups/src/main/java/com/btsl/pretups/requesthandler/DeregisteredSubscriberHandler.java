package com.btsl.pretups.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.P2PBuddiesDAO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

/**
 * * @(#)DeregisteredSubscriberHandler.java
 * Copyright(c) 2006-2007, Bharti Telesoft Ltd.
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Ankit Singhal Dec 13, 2006 Initial Creation
 * 
 * This class parses the request received on the basis of format for the
 * VIEWREGSUBSCRIBER (View registered user).
 */
public class DeregisteredSubscriberHandler implements ServiceKeywordControllerI {

    private Log _log = LogFactory.getLog(DeregisteredSubscriberHandler.class.getName());
    private HashMap _requestMap = null;
    private RequestVO _requestVO = null;
    private static final String XML_TAG_MSISDN = "MSISDN";

    /**
     * This method is the entry point into the class.
     * method process
     * 
     * @param RequestVO
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Entered....: p_requestVO= " + p_requestVO.toString());
        }
        _requestVO = p_requestVO;
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            // retreiving the Map which has all the values in the request.These
            // values can be retreived by
            // using the tag name
            _requestMap = _requestVO.getRequestMap();

            // this method validates whether the msisdn has valid values in the
            // request
            validate();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            deRegisteredSubscriber(con);

            // setting the transaction status to true
            _requestVO.setSuccessTxn(true);

        } catch (BTSLBaseException be) {
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _requestVO.setSuccessTxn(false);
            if (be.isKey()) {
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else {
                _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception ex) {
            _requestVO.setSuccessTxn(false);

            // Rollbacking the transaction
            try {
                if (con != null) {
                  mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "Exception " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DeregisteredSubscriberHandler[process]", "", "", "", "Exception:" + ex.getMessage());
            _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }// end of Exception
        finally {
            _requestVO.setRequestMap(_requestMap);
            p_requestVO = _requestVO;
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("DeregisteredSubscriberHandler#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }// end of finally
    }

    /**
     * This method is called to validate the values present in the requestMap of
     * the requestVO
     * The purpose of this method is to validate the value of the msisdn
     * 
     * @param p_con
     * @throws BTSLBaseException
     */
    // TBD i dont need connection, should i pass it here or not
    private void validate() throws BTSLBaseException {
        final String METHOD_NAME = "validate";
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Entered.....");
        }

        String msisdn = null;
        String arr[] = null;
        try {
            msisdn = (String) _requestMap.get(XML_TAG_MSISDN);
            if (!BTSLUtil.isNullString(msisdn)) {
                String filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
                if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                    _requestMap.put("RES_ERR_KEY", XML_TAG_MSISDN);
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
                }
                String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                _requestVO.setFilteredMSISDN(filteredMsisdn);
                NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                if (networkPrefixVO == null) {
                    arr = new String[] { filteredMsisdn };
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK, arr);
                }
                String networkCode = networkPrefixVO.getNetworkCode();
                // external network code, network code of sender and subscriber
                // should be same
                // first two are being checked in receiver and last two here, so
                // all match
                if (networkCode != null && !networkCode.equals(((UserVO) _requestVO.getSenderVO()).getNetworkID())) {
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                }
            } else {
                _requestMap.put("RES_ERR_KEY", XML_TAG_MSISDN);
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validate", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("validate", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DeregisteredSubscriberHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Exiting ");
        }
    }

    /**
     * This method is called when a request is received for p2p registration
     * check.
     * method addBarredUser
     * 
     * @param p_con
     */
    private void deRegisteredSubscriber(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "deRegisteredSubscriber";
        if (_log.isDebugEnabled()) {
            _log.debug("deRegisteredSubscriber", "Entered .....");
        }
        SenderVO senderVO = null;
        try {
            P2PBuddiesDAO p2PBuddiesDAO = new P2PBuddiesDAO();
            SubscriberDAO subscriberDAO = new SubscriberDAO();
            ArrayList userList = subscriberDAO.loadSubscriberDetails(p_con, _requestVO.getFilteredMSISDN(), null, null, PretupsI.ALL,null);
            if (userList == null || userList.isEmpty()) {
                throw new BTSLBaseException(this, "deRegisteredSubscriber", PretupsErrorCodesI.CCE_XML_ERROR_USER_NOT_REGISTERED);
            }
            // setting buddy list
            int userListSize = userList.size();
            for (int i = 0; i < userListSize; i++) {
                senderVO = (SenderVO) userList.get(i);
                senderVO.setVoList(p2PBuddiesDAO.loadBuddyList(p_con, senderVO.getUserID()));
                senderVO.setBuddySeqNumber(senderVO.getVoList().size());
            }
            if (!subscriberDAO.checkRequestUnderProcess(p_con, _requestVO.getFilteredMSISDN())) {
                int deleteCount = SubscriberBL.deleteSubscriber(p_con, senderVO);
                if (deleteCount > 0) {
                    _requestVO.setSuccessTxn(true);
                    p_con.commit();
                }
            } else {
                throw new BTSLBaseException(this, "deRegisteredSubscriber", PretupsErrorCodesI.CCE_XML_ERROR_USER_LAST_REQ_UNDERPROCESS);
                // _requestVO.setValueObject(userList);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("deRegisteredSubscriber", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("deRegisteredSubscriber", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DeregisteredSubscriberHandler[deRegisteredSubscriber]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deRegisteredSubscriber", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("deRegisteredSubscriber", "Exiting  filteredMsisdn=" + _requestVO.getFilteredMSISDN());
        }
    }
}
