package com.btsl.pretups.requesthandler;

/**
 * * @(#)RoutingEnquiryHandler.java
 * Copyright(c) 2005-2006, Bharti Telesoft Ltd.
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Ved prakash Sharma Dec 13, 2006 Initial Creation
 * 
 * This class parses the request received on the basis of format for the
 * routing.
 */

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
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.txn.pretups.routing.subscribermgmt.businesslogic.RoutingTxnDAO;

/**
 * @author ved.sharma
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class RoutingEnquiryHandler implements ServiceKeywordControllerI {
    private Log log = LogFactory.getLog(UnBarUserHandler.class.getName());
    private HashMap _requestMap = null;
    private RequestVO _requestVO = null;
    private static final String XML_TAG_MSISDN = "MSISDN";

    /**
     * This method is the entry point into the class.
     * method process
     * 
     * @param RequestVO
     *            p_requestVO
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (log.isDebugEnabled()) {
            log.debug("process", "Entered....: p_requestVO= " + p_requestVO.toString());
        }
        _requestVO = p_requestVO;
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            // retreiving the Map which has all the values in the request.These
            // values can be retreived by
            // using the tag name
            _requestMap = _requestVO.getRequestMap();

            // this method validates whether the msisdn, module and action have
            // valid values in the request
            validate();
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            loadRoutingDetails(con);

            // setting the transaction status to true
            _requestVO.setSuccessTxn(true);

        } catch (BTSLBaseException be) {
            log.error("process", "BTSLBaseException " + be.getMessage());
            _requestVO.setSuccessTxn(false);
            if (be.isKey()) {
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else {
                _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            log.errorTrace(METHOD_NAME, be);
        } catch (Exception ex) {
            _requestVO.setSuccessTxn(false);

            // Rollbacking the transaction
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                log.errorTrace(METHOD_NAME, ee);
            }
            log.error("process", "Exception " + ex.getMessage());
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingEnquiryHandler[process]", "", "", "", "Exception:" + ex.getMessage());
            _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }// end of Exception
        finally {
            _requestVO.setRequestMap(_requestMap);
            p_requestVO = _requestVO;
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("RoutingEnquiryHandler#process");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug("process", " Exited ");
            }
        }// end of finally
    }

    /**
     * This method is called to validate the values present in the requestMap of
     * the requestVO
     * The purpose of this method is to validate the values of the msisdn
     * 
     * @throws BTSLBaseException
     */
    private void validate() throws BTSLBaseException {
        final String METHOD_NAME = "validate";
        if (log.isDebugEnabled()) {
            log.debug("validate", "Entered.....");
        }
        try {
            // getting the msisdn of the retailer
            String msisdn = (String) _requestMap.get(XML_TAG_MSISDN);
            if (BTSLUtil.isNullString(msisdn)) {
                _requestMap.put("RES_ERR_KEY", XML_TAG_MSISDN);
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }
            // filtering the msisdn for country independent dial format
            String filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
            if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                _requestMap.put("RES_ERR_KEY", XML_TAG_MSISDN);
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
            }
            String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
            _requestVO.setFilteredMSISDN(filteredMsisdn);
            // checking whether the msisdn prefix is valid in the network
            NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            if (networkPrefixVO == null) {
                String arr[] = new String[] { filteredMsisdn };
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK, arr);
            }
            String networkCode = networkPrefixVO.getNetworkCode();
            if (networkCode != null && !networkCode.equals(((UserVO) _requestVO.getSenderVO()).getNetworkID())) {
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
            }
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            log.error("validate", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error("validate", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingEnquiryHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("RoutingEnquiryHandler", "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (log.isDebugEnabled()) {
            log.debug("validate", "Exiting ");
        }
    }

    /**
     * This method is called when a request is received for bar user.
     * method loadRoutingDetails
     * 
     * @param p_con
     * @throws BTSLBaseException
     */
    private void loadRoutingDetails(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadRoutingDetails";
        if (log.isDebugEnabled()) {
            log.debug("viewBarredUser", "Entered .....");
        }
        try {
            RoutingTxnDAO routingTxnDAO = new RoutingTxnDAO();
            ArrayList list = routingTxnDAO.loadSubscriberRoutingList(p_con, _requestVO.getFilteredMSISDN());
            if (list != null && list.size() > 0) {
                _requestVO.setValueObject(list);
            } else {
                throw new BTSLBaseException("RoutingEnquiryHandler", "viewBarredUser", PretupsErrorCodesI.CCE_XML_ERROR_MSISDN_DETAILS_NOTFOUND_ROUTING_LIST);
            }

        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            log.error("loadRoutingDetails", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error("loadRoutingDetails", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingEnquiryHandler[loadRoutingDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadRoutingDetails", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (log.isDebugEnabled()) {
            log.debug("loadRoutingDetails", "Exiting ");
        }
    }
}
