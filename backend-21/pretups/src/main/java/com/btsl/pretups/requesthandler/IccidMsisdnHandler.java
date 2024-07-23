/*
 * @(#)IccidMsisdnHandler.java
 * 
 * Name Date History
 * ------------------------------------------------------------------------
 * Manish K. Singh 11/12/2006 Initial Creation
 * 
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * 
 * This program processes the request for ICCID MSISDN User details for ICCID
 * or/and MSISDN.
 * If both MSISDN and ICCID are there the processing will be done only when
 * MSISDN and ICCID are matching.
 * It loads the ICCID-MSISDN details (PosKeyVO object)and sets it (RequestVO
 * object)
 */
package com.btsl.pretups.requesthandler;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Hashtable;

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
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyDAO;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

/**
 * com.btsl.pretups.requesthandler
 * IccidMsisdnHandler.java
 * 
 * This program processes the request for ICCID MSISDN User details for ICCID
 * or/and MSISDN.
 * If both MSISDN and ICCID are there the processing will be done only when
 * MSISDN and ICCID are matching.
 * It loads the ICCID-MSISDN details (PosKeyVO object)and sets it (RequestVO
 * object)
 */
public class IccidMsisdnHandler implements ServiceKeywordControllerI {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private HashMap _requestMap = null;
    private static final String MSISDN_STR = "USERMSISDN";
    private static final String ICCID_STR = "ICCID";

    /**
     * This method is the entry point in the class. The method is declared in
     * the Interface ServiceKeywordControllerI
     * This method processes the request for the MSISDN OR/AND ICCID
     * calls the validate() for validating MSISDN, ICCID
     * calls the loadIccidMsisdnDetails() that sets the ICCID-MSISDN details in
     * the p_requestVO
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
            // validating the request.
            // "MSISDN, filteredMsisdn" is added if present and "ICCID, iccid"
            // is added if present to the Hashtable
            Hashtable htRequestKeys = validate(p_requestVO);

            mcomCon = new MComConnection();con=mcomCon.getConnection();

            // loading the Iccid Msisdn Details
            loadIccidMsisdnDetails(con, htRequestKeys, p_requestVO);

            p_requestVO.setSuccessTxn(true);

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IccidMsisdnHandler[process]", "", "", "", "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("IccidMsisdnHandler#process");
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
     * This method checks for the mandatory value MSISDN OR ICCID is not null,
     * if msisdn is there, then Network Prefix and supporting network are there
     * or not
     * 
     * @param p_requestVO
     *            RequestVO
     * @return htRequestKeys Hashtable
     * @throws BTSLBaseException
     */
    private Hashtable validate(RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "validate";
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Entered.....");
        }
        String msisdn = null;
        String iccid = null;
        String filteredMsisdn = null;
        String msisdnPrefix = null;
        NetworkPrefixVO networkPrefixVO = null;
        String networkCode = null;
        Hashtable htRequestKeys = new Hashtable();
        try {
            msisdn = (String) _requestMap.get(MSISDN_STR);
            iccid = (String) _requestMap.get(ICCID_STR);

            if (BTSLUtil.isNullString(msisdn) && BTSLUtil.isNullString(iccid)) { // if
                                                                                 // both
                                                                                 // absent
                _requestMap.put("RES_ERR_KEY", "MSISDN or ICCID"); // tbd
                if (_log.isDebugEnabled()) {
                    _log.debug("validate", "Missing mandatory value: MSISDN OR ICCID");
                }
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_ATLEAST_ONE_VALUE_REQUIRED);
            }
            if (!BTSLUtil.isNullString(msisdn)) { // if msisdn present
                // System.out.println("isValidMSISDN"+BTSLUtil.isValidMSISDN(msisdn));
                filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn); // before
                                                                      // process
                                                                      // MSISDN
                                                                      // filter
                                                                      // each-one
                if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                    _requestMap.put("RES_ERR_KEY", MSISDN_STR);
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
                if (networkCode != null && !networkCode.equals(((UserVO) p_requestVO.getSenderVO()).getNetworkID())) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("validate", "No supporting Network for msisdn=" + msisdn);
                    }
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                }
                htRequestKeys.put(MSISDN_STR, msisdn);
            }
            if (!BTSLUtil.isNullString(iccid)) { // if iccid present
                htRequestKeys.put(ICCID_STR, iccid);
            }
        } catch (BTSLBaseException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("validate", "BTSLBaseException " + e.getMessage());
            throw e;
        } catch (Exception e) {
            _log.error("validate", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IccidMsisdnHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Exiting.....");
        }
        return htRequestKeys;
    }

    /**
     * This method sets the ICCID details posKeyVO in the RequestVO p_requestVO
     * 
     * @param p_con
     * @param p_htRequestKeys
     *            Hashtable
     * @param p_requestVO
     *            RequestVO
     * @throws BTSLBaseException
     */
    private void loadIccidMsisdnDetails(Connection p_con, Hashtable p_htRequestKeys, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadIccidMsisdnDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("loadIccidMsisdnDetails", "Entered.....");
        }
        String filteredMsisdn = "";
        String filteredMsisdnPOS = "";
        String iccid = "";
        String newIccid = "";
        PosKeyVO posKeyVO = null;
        String networkCode = ((UserVO) p_requestVO.getSenderVO()).getNetworkID();
        try {
            filteredMsisdn = (String) p_htRequestKeys.get(MSISDN_STR);
            iccid = (String) p_htRequestKeys.get(ICCID_STR);

            PosKeyDAO posKeyDAO = new PosKeyDAO();

            if (!BTSLUtil.isNullString(iccid)) {
                newIccid = BTSLUtil.calcIccId(iccid, networkCode);
            }
            if (!BTSLUtil.isNullString(newIccid)) {
                posKeyVO = posKeyDAO.loadPosKey(p_con, newIccid, null);
                if (posKeyVO == null) {
                    throw new BTSLBaseException(this, "loadIccidMsisdnDetails", PretupsErrorCodesI.CCE_XML_ERROR_ICCID_DETAILS_NOT_FOUND4ICCID);
                } else if (!posKeyVO.getNetworkCode().equals(networkCode)) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("loadIccidMsisdnDetails", "ICCID is from unsupported network" + iccid);
                    }
                    throw new BTSLBaseException(this, "loadIccidMsisdnDetails", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                }
                if(BTSLUtil.isNullString(posKeyVO.getMsisdn())){
                	throw new BTSLBaseException(this, "loadIccidMsisdnDetails", PretupsErrorCodesI.CCE_XML_ERROR_CU_ICCID_DETAILS_NO_MSISDN);	
                }
                else{
                filteredMsisdnPOS = PretupsBL.getFilteredMSISDN(posKeyVO.getMsisdn());
                }
                // if iccid and msisdn are Matching
                if (!BTSLUtil.isNullString(filteredMsisdn) && !filteredMsisdn.equals(filteredMsisdnPOS)) {
                    throw new BTSLBaseException(this, "loadIccidMsisdnDetails", PretupsErrorCodesI.CCE_XML_ERROR_ICCID_NOT_MATCHING_MSISDN);
                }
                p_requestVO.setValueObject(posKeyVO);
            } else { // if Only msisdn no. entered
                _log.debug("loadIccidMsisdnDetails", "only msisdn entered");
                posKeyVO = posKeyDAO.loadPosKey(p_con, null, filteredMsisdn);
                if (posKeyVO == null) {
                    throw new BTSLBaseException(this, "loadIccidMsisdnDetails", PretupsErrorCodesI.CCE_XML_ERROR_ICCID_DETAILS_NOT_FOUND4MSISDN);
                } else if (!posKeyVO.getNetworkCode().equals(networkCode)) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("loadIccidMsisdnDetails", "MSISDN is from unsupported network" + filteredMsisdn);
                    }
                    throw new BTSLBaseException(this, "loadIccidMsisdnDetails", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                }
                p_requestVO.setValueObject(posKeyVO);
            }
        } catch (BTSLBaseException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("loadIccidMsisdnDetails", "BTSLBaseException " + e.getMessage());
            throw e;
        } catch (Exception e) {
            _log.error("loadIccidMsisdnDetails", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IccidMsisdnHandler[loadIccidMsisdnDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadIccidMsisdnDetails", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadIccidMsisdnDetails", "Exiting.....");
        }
    }

}
