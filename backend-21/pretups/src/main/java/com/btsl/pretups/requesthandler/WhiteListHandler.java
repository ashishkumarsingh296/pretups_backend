/*
 * @(#)WhiteListHandler.java
 * 
 * Name Date History
 * ------------------------------------------------------------------------
 * Manish K. Singh 05/12/2006 Initial Creation
 * 
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * 
 * This program processes the request for Whitelist Subscriber Details for a
 * mobile no.
 * It checks the mobile no. is in whitelist and it loads the subscriber's
 * details (WhiteListVO object)
 * and sets it (RequestVO object)
 */
package com.btsl.pretups.requesthandler;

import java.sql.Connection;
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
import com.btsl.pretups.whitelist.businesslogic.WhiteListDAO;
import com.btsl.pretups.whitelist.businesslogic.WhiteListVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

/**
 * com.btsl.pretups.requesthandler
 * WhiteListHandler.java
 * Dec 05, 2006
 * 
 * This program processes the request for Whitelist Subscriber Details for a
 * mobile no.
 * It checks the mobile no. is in whitelist and it loads the subscriber's
 * details (WhiteListVO object)
 * and sets it (RequestVO object)
 */
public class WhiteListHandler implements ServiceKeywordControllerI {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private HashMap _requestMap = null;

    /**
     * This method is the entry point in the class and is declared in the
     * Interface ServiceKeywordControllerI
     * This method processes the request for the MSISDN and sets the user
     * details in the p_requestVO if present
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Entered.....p_requestVO=" + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        _requestMap = p_requestVO.getRequestMap();
        try {
            // validating the request
            String filteredMsisdn = validate(p_requestVO);

            mcomCon = new MComConnection();con=mcomCon.getConnection();

            // Loading WhiteList Subscriber Details
            WhiteListVO whiteListVO = (new WhiteListDAO()).loadWhiteListSubsDetails(con, filteredMsisdn);
            if (whiteListVO != null) {
                p_requestVO.setValueObject(whiteListVO);
            } else {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CCE_XML_ERROR_MSISDN_NOT_IN_WHITELIST, new String[] { filteredMsisdn });
            }

            p_requestVO.setSuccessTxn(true);

        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error("process", p_requestVO.getRequestIDStr(), "BTSLBaseException " + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                String[] args = be.getArgs();
                p_requestVO.setMessageArguments(args);
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", p_requestVO.getRequestIDStr(), "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListHandler[process]", "", "", "", "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("WhiteListHandler#process");
				mcomCon = null;
			}
            _requestMap.put("RES_TYPE", "NA");
            p_requestVO.setRequestMap(_requestMap);
            if (_log.isDebugEnabled()) {
                _log.debug("process", p_requestVO.getRequestIDStr(), "Exited.....p_requestVO=" + p_requestVO);
            }
        }
    }

    /**
     * This method checks for the mandatory value MSISDN is not null,
     * and Network Prefix and supporting network are there or not
     * 
     * @param p_requestVO
     *            RequestVO
     * @return filteredMsisdn String
     * @throws BTSLBaseException
     */
    private String validate(RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "validate";
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Entered.....");
        }
        String msisdn = null;
        String filteredMsisdn = null;
        String msisdnPrefix = null;
        NetworkPrefixVO networkPrefixVO = null;
        String networkCode = null;
        try {
            msisdn = (String) _requestMap.get("MSISDN");

            if (!BTSLUtil.isNullString(msisdn)) {
                filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn); // before
                                                                      // process
                                                                      // MSISDN
                                                                      // filter
                                                                      // each-one

                // check the MSISDN is valid or not
                if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                    _requestMap.put("RES_ERR_KEY", "MSISDN");
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
                }

                // get prefix of the MSISDN
                msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get
                                                                          // the
                                                                          // prefix
                                                                          // of
                                                                          // the
                                                                          // MSISDN
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                if (networkPrefixVO == null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("validate", "No Network prefix found for msisdn=" + msisdn);
                    }
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK);
                }
                // check network support of the MSISDN
                networkCode = networkPrefixVO.getNetworkCode();
                if (!networkCode.equals(((UserVO) p_requestVO.getSenderVO()).getNetworkID())) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("validate", "No supporting Network for msisdn=" + msisdn);
                    }
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                }
            } else {
                _requestMap.put("RES_ERR_KEY", "MSISDN");
                if (_log.isDebugEnabled()) {
                    _log.debug("validate", "Missing mandatory value: MSISDN");
                }
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }
        } catch (BTSLBaseException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("validate", "BTSLBaseException " + e.getMessage());
            throw e;
        } catch (Exception e) {
            _log.error("validate", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Exiting.....");
        }
        return filteredMsisdn;
    }

}
