/*
 * @(#)IccidMsisdnMappingHandler.java
 * 
 * Name Date History
 * ------------------------------------------------------------------------
 * Zafar Abbas 05/12/2007 Initial Creation
 * 
 * ------------------------------------------------------------------------
 * Copyright (c) 2007 Bharti Telesoft Ltd.
 * 
 * This program processes the request for ICCID-MSISDN Mapping.
 * If both MSISDN and ICCID are there the processing will be done only when
 * MSISDN and ICCID are matching.
 * It loads the ICCID-MSISDN details (PosKeyVO object)and sets it (RequestVO
 * object)
 */
package com.btsl.pretups.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
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
 * IccidMsisdnMappingHandler.java
 * 
 */
public class IccidMsisdnMappingHandler implements ServiceKeywordControllerI {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private HashMap _requestMap = null;
    private final String MSISDN_STR = "MSISDN";
    private final String ICCID_STR = "ICCID";

    /**
     * This method is the entry point in the class. The method is declared in
     * the Interface ServiceKeywordControllerI
     * This method processes the request for the MSISDN AND ICCID
     * calls the validate() for validating MSISDN and ICCID
     * calls the mapIccidMsisdn() that map the ICCID with the MSISDN , sent in
     * request.
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Entered.....p_requestVO=" + p_requestVO);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            // validating the request.
            this.validate(p_requestVO);

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            // Map the Iccid Msisdn according to inputs
            this.mapIccidMsisdn(con, p_requestVO);
            p_requestVO.setSuccessTxn(true);
        } catch (BTSLBaseException baseException) {
            _log.errorTrace(METHOD_NAME, baseException);
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception exception) {
                _log.errorTrace(METHOD_NAME, exception);
            }
            _log.error("process", p_requestVO.getRequestIDStr(), "BTSLBaseException " + baseException.getMessage());
            if (baseException.isKey()) {
                p_requestVO.setMessageCode(baseException.getMessageKey());
                String[] args = baseException.getArgs();
                p_requestVO.setMessageArguments(args);
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
        } catch (Exception exception) {
            _log.errorTrace(METHOD_NAME, exception);
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception exception2) {
                _log.errorTrace(METHOD_NAME, exception2);
            }
            _log.error("process", p_requestVO.getRequestIDStr(), "Exception " + exception.getMessage());

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IccidMsisdnMappingHandler[process]", "", "", "", "Exception:" + exception.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("IccidMsisdnMappingHandler#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", p_requestVO.getRequestIDStr(), "Exited.....p_requestVO=" + p_requestVO);
            }
        }
    }

    /**
     * This method checks for the mandatory value MSISDN AND ICCID is not null,
     * If MSISDN is not, then Network Prefix and supporting network are there or
     * not.
     * If ICCID is not null, then check its length with the configured length
     * and correct/swap it accordingly
     * 
     * @param p_requestVO
     *            RequestVO
     * @return htRequestKeys Hashtable
     * @throws BTSLBaseException
     */
    private void validate(RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "validate";
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Entered.....");
        }
        try {
            _requestMap = p_requestVO.getRequestMap();
            String iccid = (String) _requestMap.get(ICCID_STR);
            String filteredMsisdn = (String) _requestMap.get(MSISDN_STR);
            // if both absent
            if (BTSLUtil.isNullString(filteredMsisdn) && BTSLUtil.isNullString(iccid)) {
                _requestMap.put("RES_ERR_KEY", "MSISDN_AND_ICCID");
                if (_log.isDebugEnabled()) {
                    _log.debug("validate", "Missing mandatory value: MSISDN AND ICCID");
                }
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.XML_ERROR_ICCID_MSISDN_REQUIRED);
            }
            // if iccid present
            if (!BTSLUtil.isNullString(iccid)) {
                iccid = BTSLUtil.calcIccId(iccid, ((UserVO) p_requestVO.getSenderVO()).getNetworkID());
                _requestMap.put(ICCID_STR, iccid);
            } else {
                _requestMap.put("RES_ERR_KEY", ICCID_STR);
                if (_log.isDebugEnabled()) {
                    _log.debug("validate", "Missing mandatory value: ICCID");
                }
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.XML_ERROR_ICCID_IS_NULL);
            }
            // if msisdn present
            if (!BTSLUtil.isNullString(filteredMsisdn)) {
                filteredMsisdn = PretupsBL.getFilteredMSISDN(filteredMsisdn); // before
                                                                              // process
                                                                              // MSISDN
                                                                              // filter
                                                                              // each-one
                if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                    _requestMap.put("RES_ERR_KEY", MSISDN_STR);
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
                }
                // get prefix of the MSISDN
                String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                if (networkPrefixVO == null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("validate", "No Network prefix found for msisdn=" + filteredMsisdn);
                    }
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK);
                }
                // check network support of the MSISDN
                String networkCode = networkPrefixVO.getNetworkCode();
                if (networkCode != null && !networkCode.equals(((UserVO) p_requestVO.getSenderVO()).getNetworkID())) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("validate", "No supporting Network for msisdn=" + filteredMsisdn);
                    }
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                }
                _requestMap.put(MSISDN_STR, filteredMsisdn);
            } else {
                _requestMap.put("RES_ERR_KEY", MSISDN_STR);
                if (_log.isDebugEnabled()) {
                    _log.debug("validate", "Missing mandatory value: MSISDN");
                }
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.XML_ERROR_MSISDN_IS_NULL);
            }
        } catch (BTSLBaseException baseException) {
            _log.errorTrace(METHOD_NAME, baseException);
            _log.error("validate", "BTSLBaseException " + baseException.getMessage());
            throw baseException;
        } catch (Exception exception) {
            _log.error("validate", "Exception " + exception.getMessage());
            _log.errorTrace(METHOD_NAME, exception);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IccidMsisdnMappingHandler[validate]", "", "", "", "Exception:" + exception.getMessage());
            throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Exiting.....");
        }
    }

    /**
     * This method maps the ICCID with the MSISDN.
     * The mapping is done as:-
     * Scenario 1.)Unmapped ICCID mapped to new MSISDN.
     * Scenario 2.)Unmapped ICCID mapped to an existing MSISDN associated with
     * another ICCID.
     * Existing ICCID is de-registered and cannot be used, existing MSISDN
     * mapped to unmapped ICCID.
     * Scenario 3.)Mapped ICCID re-associated with new MSISDN. Existing MSISDN
     * will be replaced with the new MSISDN
     * Scenario 4.)If given ICCID is mapped with some MSISDN and given MSISDN is
     * also mapped with some ICCID
     * 
     * @param p_con
     * @param p_htRequestKeys
     *            Hashtable
     * @param p_requestVO
     *            RequestVO
     * @throws BTSLBaseException
     * @throws SQLException 
     */
    private void mapIccidMsisdn(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException, SQLException {
        final String METHOD_NAME = "mapIccidMsisdn";
        if (_log.isDebugEnabled()) {
            _log.debug("mapIccidMsisdn", "Entered.....");
        }
        PosKeyVO posKeyVO = null, posKeyVO2 = null;
        ArrayList posKeyVOList = null;
        int updateCount = 0;
        try {
            String filteredMsisdn = (String) _requestMap.get(MSISDN_STR);
            String newIccid = (String) _requestMap.get(ICCID_STR);
            PosKeyDAO posKeyDAO = new PosKeyDAO();
            posKeyVO = posKeyDAO.loadPosKey(p_con, newIccid, null);
            if (posKeyVO != null) {
                posKeyVOList = posKeyDAO.loadICCIDMsisdnDetails(p_con, filteredMsisdn, newIccid);
                String userCode = ((UserVO) p_requestVO.getSenderVO()).getUserCode();
                String networkCode = ((UserVO) p_requestVO.getSenderVO()).getNetworkID();
                if (posKeyVOList != null && networkCode.equals(posKeyVO.getNetworkCode())) {
                    // Iccid exist in pos_key table
                    if (posKeyVOList.size() == 1) {
                        posKeyVO = (PosKeyVO) posKeyVOList.get(0);
                        // iccid and msisdn are Matching
                        if (newIccid.equals(BTSLUtil.NullToString(posKeyVO.getIccId())) && filteredMsisdn.equals(BTSLUtil.NullToString(posKeyVO.getMsisdn()))) {
                            throw new BTSLBaseException(this, "mapIccidMsisdn", PretupsErrorCodesI.ICCID_MSISDN_ALREADY_MAPPED);
                        } else {
                            // icccid and msisdn are not matching
                            // Scenario 1:
                            // if iccid is unmapped
                            if (newIccid.equals(BTSLUtil.NullToString(posKeyVO.getIccId())) && BTSLUtil.isNullString(posKeyVO.getMsisdn())) {
                                updateCount = posKeyDAO.assignMsisdnWihIccId(p_con, filteredMsisdn, newIccid, userCode);
                                if (updateCount <= 0) {
                                    throw new BTSLBaseException(this, "mapIccidMsisdn", PretupsErrorCodesI.ERROR_ICCID_MSISDN_MAPPING_FAIL);
                                }
                            }
                            // Scenario 3:
                            // if iccid is matched to some other msisdn
                            else if (newIccid.equals(BTSLUtil.NullToString(posKeyVO.getIccId())) && !filteredMsisdn.equals(BTSLUtil.NullToString(posKeyVO.getMsisdn()))) {
                                updateCount = posKeyDAO.assignMsisdnWihIccId(p_con, filteredMsisdn, newIccid, userCode);
                                if (updateCount <= 0) {
                                    throw new BTSLBaseException(this, "mapIccidMsisdn", PretupsErrorCodesI.ERROR_ICCID_MSISDN_MAPPING_FAIL);
                                }
                            }
                        }
                    }
                    // given iccid may be mapped or unmapped and given Msisdn is
                    // associated with another Iccid
                    else if (posKeyVOList.size() > 1) {
                        posKeyVO = (PosKeyVO) posKeyVOList.get(0);
                        posKeyVO2 = (PosKeyVO) posKeyVOList.get(1);
                        // Scenario 4:
                        // if iccid is associated with some other msisdn and
                        // msisdn is associated with some other iccid
                        if ((newIccid.equals(posKeyVO.getIccId()) && !filteredMsisdn.equals(posKeyVO.getMsisdn())) || (newIccid.equals(posKeyVO2.getIccId()) && !filteredMsisdn.equals(posKeyVO2.getMsisdn()))) {
                            if (newIccid.equals(posKeyVO.getIccId())) {
                                updateCount = posKeyDAO.reUtilizeIccId(p_con, posKeyVO2.getIccId(), userCode);
                            } else if (newIccid.equals(posKeyVO2.getIccId())) {
                                updateCount = posKeyDAO.reUtilizeIccId(p_con, posKeyVO.getIccId(), userCode);
                            }
                            if (updateCount > 0) {
                                updateCount = posKeyDAO.assignMsisdnWihIccId(p_con, filteredMsisdn, newIccid, userCode);
                            } else {
                                throw new BTSLBaseException(this, "mapIccidMsisdn", PretupsErrorCodesI.ERROR_ICCID_MSISDN_MAPPING_FAIL);
                            }
                        }
                        // Scenario 2:
                        // if msisdn is associated with some other Iccid and
                        // given Iccid is unmapped.
                        else if ((newIccid.equals(posKeyVO.getIccId()) && BTSLUtil.isNullString(posKeyVO.getMsisdn()) && filteredMsisdn.equals(posKeyVO2.getMsisdn()) && !newIccid.equals(posKeyVO2.getIccId())) || (newIccid.equals(posKeyVO2.getIccId()) && BTSLUtil.isNullString(posKeyVO2.getMsisdn()) && filteredMsisdn.equals(posKeyVO.getMsisdn()) && !newIccid.equals(posKeyVO.getIccId()))) {
                            if (filteredMsisdn.equals(posKeyVO.getMsisdn())) {
                                updateCount = posKeyDAO.reUtilizeIccId(p_con, posKeyVO.getIccId(), userCode);
                            } else if (filteredMsisdn.equals(posKeyVO2.getMsisdn())) {
                                updateCount = posKeyDAO.reUtilizeIccId(p_con, posKeyVO2.getIccId(), userCode);
                            }
                            if (updateCount > 0) {
                                updateCount = posKeyDAO.assignMsisdnWihIccId(p_con, filteredMsisdn, newIccid, userCode);
                            } else {
                                throw new BTSLBaseException(this, "mapIccidMsisdn", PretupsErrorCodesI.ERROR_ICCID_MSISDN_MAPPING_FAIL);
                            }
                        }
                    } else {
                        // If posKeyVOList.size()<1
                        throw new BTSLBaseException(this, "mapIccidMsisdn", PretupsErrorCodesI.INVALID_ICCID_FOR_MAPPING);
                    }
                } else {
                    // if No record return from DB and network code did not
                    // match
                    throw new BTSLBaseException(this, "mapIccidMsisdn", PretupsErrorCodesI.INVALID_ICCID_FOR_MAPPING);
                }
            } else {
                // if posKeyVO==null
                throw new BTSLBaseException(this, "mapIccidMsisdn", PretupsErrorCodesI.INVALID_ICCID_FOR_MAPPING);
            }
            // Successfully completed the transactios so ,Commit the data
            try {
                if (updateCount > 0) {
                    p_con.commit();
                }
            } catch (SQLException exception) {
                _log.errorTrace(METHOD_NAME, exception);
                throw new BTSLBaseException(this, "mapIccidMsisdn", PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
        } catch (BTSLBaseException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("mapIccidMsisdn", "BTSLBaseException " + e.getMessage());
            // Exception so Rollback the transactions
            try {
                p_con.rollback();
            } catch (SQLException exception) {
                _log.errorTrace(METHOD_NAME, exception);
                throw e;
            }
            throw e;
        } catch (Exception e) {
            _log.error("mapIccidMsisdn", "Exception " + e.getMessage());
            // Exception so Rollback the transactions
            try {
                p_con.rollback();
            } catch (SQLException exception) {
                _log.errorTrace(METHOD_NAME, exception);
                throw exception;
            }
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IccidMsisdnMappingHandler[loadIccidMsisdnDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "mapIccidMsisdn", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("mapIccidMsisdn", "Exiting.....");
        }
    }
}
