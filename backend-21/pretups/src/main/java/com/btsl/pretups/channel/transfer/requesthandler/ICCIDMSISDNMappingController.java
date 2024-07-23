/*
 * @(#)ICCIDMSISDNMappingController.java
 * This program processes the request for ICCID-MSISDN Mapping.
 * If both MSISDN and ICCID are there the processing will be done only when
 * MSISDN and ICCID are matching.
 * It loads the ICCID-MSISDN details (PosKeyVO object)and sets it (RequestVO
 * object)
 */
package com.btsl.pretups.channel.transfer.requesthandler;

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
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

/**
 * @description : This controller class will be used to process the ICCID
 *              Mapping with MSISDN request for user through external system via
 *              operator receiver.
 * @remark : This program processes the request for ICCID-MSISDN Mapping.
 *         If both MSISDN and ICCID are there the processing will be done only
 *         when MSISDN and ICCID are matching.
 *         It loads the ICCID-MSISDN details (PosKeyVO object)and sets it
 *         (RequestVO object)
 * @author : diwakar
 */
public class ICCIDMSISDNMappingController implements ServiceKeywordControllerI {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private HashMap _requestMap = null;

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
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            _requestMap = p_requestVO.getRequestMap();
            mcomCon = new MComConnection();con=mcomCon.getConnection();

            // Map the Iccid Msisdn according to inputs
            this.mapIccidMsisdn(con, p_requestVO);
            p_requestVO.setSuccessTxn(true);

        } catch (BTSLBaseException baseException) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception exception) {
                _log.errorTrace(METHOD_NAME, exception);
            }
            _log.error("process", p_requestVO.getRequestIDStr(), "BTSLBaseException " + baseException.getMessage());
            if (baseException.isKey()) {
                p_requestVO.setMessageCode(baseException.getMessageKey());
                final String[] args = baseException.getArgs();
                p_requestVO.setMessageArguments(args);
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            _log.errorTrace(METHOD_NAME, baseException);
        } catch (Exception exception) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception exception2) {
                _log.errorTrace(METHOD_NAME, exception2);
            }
            _log.error("process", p_requestVO.getRequestIDStr(), "Exception " + exception.getMessage());
            _log.errorTrace(METHOD_NAME, exception);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ICCIDMSISDNMappingController[process]", "", "", "",
                "Exception:" + exception.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("ICCIDMSISDNMappingController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", p_requestVO.getRequestIDStr(), "Exited.....p_requestVO=" + p_requestVO);
            }
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
     */
    private void mapIccidMsisdn(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("mapIccidMsisdn", "Entered.....");
        }
        final String METHOD_NAME = "mapIccidMsisdn";
        PosKeyVO posKeyVO = null, posKeyVO2 = null;
        ArrayList posKeyVOList = null;
        int updateCount = 0;
        try {
            final String filteredMsisdn = (String) _requestMap.get("MSISDN");
            final String iccid = (String) _requestMap.get("ICCID");
            // calculate ICCID as per network code on 11-APR-2104
            String newIccid = null;
            if (!BTSLUtil.isNullString(iccid)) {
                newIccid = BTSLUtil.calcIccId(iccid, (String) _requestMap.get("EXTNWCODE"));
            }
            // Ended Here
            final PosKeyDAO posKeyDAO = new PosKeyDAO();
            posKeyVO = posKeyDAO.loadPosKey(p_con, newIccid, null);

            if (posKeyVO != null) {
                posKeyVOList = posKeyDAO.loadICCIDMsisdnDetails(p_con, filteredMsisdn, newIccid);
                final String userCode = ((UserVO) p_requestVO.getSenderVO()).getUserCode();
                final String networkCode = ((UserVO) p_requestVO.getSenderVO()).getNetworkID();
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
                        if ((newIccid.equals(posKeyVO.getIccId()) && !filteredMsisdn.equals(posKeyVO.getMsisdn())) || (newIccid.equals(posKeyVO2.getIccId()) && !filteredMsisdn
                            .equals(posKeyVO2.getMsisdn()))) {
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
                        else if ((newIccid.equals(posKeyVO.getIccId()) && BTSLUtil.isNullString(posKeyVO.getMsisdn()) && filteredMsisdn.equals(posKeyVO2.getMsisdn()) && !newIccid
                            .equals(posKeyVO2.getIccId())) || (newIccid.equals(posKeyVO2.getIccId()) && BTSLUtil.isNullString(posKeyVO2.getMsisdn()) && filteredMsisdn
                            .equals(posKeyVO.getMsisdn()) && !newIccid.equals(posKeyVO.getIccId()))) {
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
                    p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_ICCID_MSISDN_MAP_SUCCESS);
                } else {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_ICCID_MSISDN_MAP_FAILURE);
                }

            } catch (SQLException exception) {
                _log.errorTrace(METHOD_NAME, exception);
                throw new BTSLBaseException(this, "mapIccidMsisdn", PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
        } catch (BTSLBaseException e) {
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
                throw new BTSLBaseException(this, METHOD_NAME, "Exception in Mapping ICCID MSISDN");
            }
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ICCIDMSISDNMappingController[loadIccidMsisdnDetails]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "mapIccidMsisdn", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("mapIccidMsisdn", "Exiting.....");
        }
    }
}
