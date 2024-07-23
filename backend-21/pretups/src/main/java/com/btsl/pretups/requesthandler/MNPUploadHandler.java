/*
 * @# MNPUploadHandler.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Apr 10, 2007 Vikas yadav Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2007 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.requesthandler;

import java.sql.Connection;
import java.util.Date;
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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.NumberPortDAO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.NumberPortVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

public class MNPUploadHandler implements ServiceKeywordControllerI {
    private Log log = LogFactory.getLog(MNPUploadHandler.class.getName());
    private HashMap _requestMap = null;
    private RequestVO _requestVO = null;
    private String _msisdn = null;
    private String _userType = null;
    private String _portType = null;
    private static final String XML_TAG_SUBSTYPE = "SUBSTYPE";
    private static final String XML_TAG_MSISDN = "USERMSISDN";// 21-02-2014
    private static final String XML_TAG_PORTTYPE = "PORTTYPE";

    /**
     * This method is the entry point into the class.
     * method process
     * 
     * @param RequestVO
     */

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (log.isDebugEnabled()) {
            log.debug("process", "Entered....: p_requestVO= " + p_requestVO.toString());
        }
        _requestVO = p_requestVO;
        Connection con = null;MComConnectionI mcomCon = null;
        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            // retreiving the Map which has all the values in the request.These
            // values can be retreived by
            // using the tag name
            _requestMap = _requestVO.getRequestMap();
            _msisdn = (String) _requestMap.get(XML_TAG_MSISDN);
            _userType = (String) _requestMap.get(XML_TAG_SUBSTYPE);
            _portType = (String) _requestMap.get(XML_TAG_PORTTYPE);
            NumberPortDAO numberPortDAO = new NumberPortDAO();
            NetworkPrefixVO networkPrefixVO = null;

            if (BTSLUtil.isNullString(_msisdn) || BTSLUtil.isNullString(_userType) || BTSLUtil.isNullString(_portType)) {
                if (BTSLUtil.isNullString(_msisdn)) {
                    _requestMap.put("RES_ERR_KEY", "MSISDN");
                } else if (BTSLUtil.isNullString(_userType)) {
                    _requestMap.put("RES_ERR_KEY", "SUBSTYPE");
                } else {
                    _requestMap.put("RES_ERR_KEY", "PORTTYPE");
                }

                if (log.isDebugEnabled()) {
                    log.debug("process", "Mandatory value is missing");
                }
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }
            if (!(_userType.equalsIgnoreCase(PretupsI.SERIES_TYPE_PREPAID) || _userType.equalsIgnoreCase(PretupsI.SERIES_TYPE_POSTPAID))) {
                if (log.isDebugEnabled()) {
                    log.debug("process", "Input values are not valid");
                }
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.MNP_INVALID_SERIES_TYPE);
            }
            if (!(_portType.equalsIgnoreCase(PretupsI.PORTED_IN) || _portType.equalsIgnoreCase(PretupsI.PORTED_OUT))) {
                if (log.isDebugEnabled()) {
                    log.debug("process", "Input values are not valid");
                }
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.MNP_INVALID_PORT_TYPE);
            }

            String filteredMsisdn = PretupsBL.getFilteredMSISDN(_msisdn);
            if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                if (log.isDebugEnabled()) {
                    log.debug("process", "Not a valid MSISDN" + _msisdn);
                }
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.MNP_NOT_VALID_MSISDN);
            }
            String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);

            if (networkPrefixVO == null) {
                if (log.isDebugEnabled()) {
                    log.debug("process", "Not supporting Network" + _msisdn);
                }
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.MNP_MSISDN_PORTPREFIX_NOTDEFINED);
            }

            else if (_portType.equalsIgnoreCase(PretupsI.PORTED_IN)) {
                if (!networkPrefixVO.getOperator().equalsIgnoreCase(PretupsI.OPERATOR_TYPE_PORT)) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.MNP_MSISDN_INPORTPREFIX_NOTDEFINED);
                }
            }

            UserVO userVO = (UserVO) _requestVO.getSenderVO();
            String p_locationCode = userVO.getNetworkID();
            String networkCode = networkPrefixVO.getNetworkCode();
            if (!networkCode.equals(p_locationCode)) {
                if (log.isDebugEnabled()) {
                    log.debug("process", "Not supporting Network" + _msisdn);
                }
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.MNP_MSISDN_UNSUPPORTED_NETWORK);
            }

            if (numberPortDAO.isExists(con, filteredMsisdn, _userType, _portType)) {
                if (log.isDebugEnabled()) {
                    log.debug("process", "Msisdn already exist" + filteredMsisdn);
                }
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_CHNL_MSISDN_ALREADY_EXIST);
            }

            ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            Date currentDate = new Date(System.currentTimeMillis());
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PORT_USR_SUSPEND_REQ))).booleanValue()) {
                // suspension of user is according to the flag
                // PORT_USR_SUSPEND_REQ from system preferences.
                if (_portType.equalsIgnoreCase(PretupsI.PORTED_OUT)) {
                    UserVO userVOStatus = channelUserDAO.loadPhoneExistsWithStatus(con, filteredMsisdn);
                    if (userVOStatus != null) {
                        // suspend channel user
                        String status = userVOStatus.getStatus();
                        if (status.equals(PretupsI.USER_STATUS_SUSPEND_REQUEST) || status.equals(PretupsI.USER_STATUS_SUSPEND)) {
                            if (log.isDebugEnabled()) {
                                log.debug("processMNPFile", "User suspend process fail, " + filteredMsisdn);
                            }
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.MNP_USER_ALREAY_SUSPEND);
                        }
                        int updateStatusCount = channelUserDAO.updateUserStatus(con, filteredMsisdn, PretupsI.USER_STATUS_SUSPEND, userVO.getUserID(), currentDate);
                        if (updateStatusCount <= 0) {
                            if (log.isDebugEnabled()) {
                                log.debug("processMNPFile", "User suspend process fail, " + filteredMsisdn);
                            }
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.MNP_USER_SUSPEND_FAIL);
                        }
                    }
                }
            }
            NumberPortVO numberPortVO = new NumberPortVO();
            numberPortVO.setMsisdn(filteredMsisdn);
            numberPortVO.setSubscriberType(_userType);
            numberPortVO.setPortType(_portType);
            numberPortVO.setCreatedBy(userVO.getUserID());
            numberPortVO.setCreatedOn(currentDate);
            int addCount = numberPortDAO.writeMobileNumberToDatabase(con, numberPortVO);
            if (addCount > 0) {
                mcomCon.finalCommit();
                _requestVO.setSuccessTxn(true);

                // Added By Diwakar
                String[] arrArray = { _portType };
                p_requestVO.setMessageArguments(arrArray);
                p_requestVO.setMessageCode(PretupsErrorCodesI.MNP_PORT_SUCCESS);
                // Ended Here
            } else {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_MNP_MSISDN_UPDATE_FAIL);
                // setting the transaction status to true
            }
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
                    mcomCon.finalRollback();
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
                    mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                log.errorTrace(METHOD_NAME, ee);
            }
            log.error("process", "Exception " + ex.getMessage());
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MNPUploadHandler[process]", "", "", "", "Exception:" + ex.getMessage());
            _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }// end of Exception
        finally {
            _requestVO.setRequestMap(_requestMap);
            p_requestVO = _requestVO;
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("MNPUploadHandler#process");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug("process", " Exited ");
            }
        }// end of finally
    }

}
