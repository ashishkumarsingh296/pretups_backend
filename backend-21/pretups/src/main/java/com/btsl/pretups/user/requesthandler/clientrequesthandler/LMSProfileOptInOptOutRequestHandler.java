/**
 * @(#)LMSProfileOptInOptOutRequestHandler.java
 *                                              Copyright(c) 2015, Mahindra
 *                                              Comviva Technologies Ltd.
 *                                              All Rights Reserved
 *                                              --------------------------------
 *                                              --------------------------------
 *                                              --
 *                                              -------------------------------
 *                                              Author Date History
 *                                              --------------------------------
 *                                              --------------------------------
 *                                              --
 *                                              -------------------------------
 *                                              Harsh Dixit June 25,2015 Initial
 *                                              Creation
 *                                              This handler is used to process
 *                                              & validate Opt-In/Opt-Out LMS
 *                                              Profile request for Channel User
 *                                              --------------------------------
 *                                              --------------------------------
 *                                              --------------------------------
 */
package com.btsl.pretups.user.requesthandler.clientrequesthandler;

import java.sql.Connection;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class LMSProfileOptInOptOutRequestHandler implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(LMSProfileOptInOptOutRequestHandler.class.getName());
    private boolean _isValidTimeForOptInOut = false;
    private boolean _isValidLMSProfile = false;
    private boolean _isAlreadyOptIn = false;
    private ChannelUserDAO channelUserDAO = new ChannelUserDAO();

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered " + p_requestVO);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
		UserPhoneVO userPhoneVO = null;
        final String serviceType = p_requestVO.getServiceType();
        try {
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }
            final String messageArr[] = p_requestVO.getRequestMessageArray();
            if (messageArr.length == 2) {
            	mcomCon = new MComConnection();
            	con=mcomCon.getConnection();
                if (userPhoneVO.getPinRequired().equals(PretupsI.YES) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_PIN_BYPASS_GATEWAY_TYPE)).contains(p_requestVO.getRequestGatewayType())) {
                    try {
                        ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), messageArr[1]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                            mcomCon.finalCommit();
                        }
                        throw be;
                    }
                }
                if (BTSLUtil.isNullString(channelUserVO.getLmsProfile())) {
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.NO_LMS_PROFILE_ASSOCIATED);
                }
                _isValidLMSProfile = channelUserDAO.isLMSProfileAppForOptInOut(con, channelUserVO.getUserID());
                if (!_isValidLMSProfile) {
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.LMS_PROFILE_OPT_IN_OUT_NA);
                }
                _isValidTimeForOptInOut = channelUserDAO.isValidTimeForOptInOut(con, channelUserVO.getLmsProfile());
                if (!_isValidTimeForOptInOut) {
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.LMS_PROFILE_OPT_IN_OUT_REQTIME_INVALID);
                }

                if (PretupsI.LMS_PROFILE_OPT_IN.equals(serviceType)) {
                    this.handleOptInFeature(con, channelUserVO);
                }
                if (PretupsI.LMS_PROFILE_OPT_OUT.equals(serviceType)) {
                    this.handleOptOutFeature(con, channelUserVO);
                }
            } else {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.INVALID_MESSAGE_LENGTH);
            }

        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception e1) {
                _log.errorTrace(METHOD_NAME, e1);
            }
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("LMSProfileOptInOptOutRequestHandler#process");
        		mcomCon=null;
        		}
        	
//			try{
//	            Locale   locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
//	            PushMessage pushMessage=new PushMessage(userPhoneVO.getMsisdn(),BTSLUtil.getMessage(locale,  p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()) , "",  p_requestVO.getMessageCode(), locale);      
//	            pushMessage.push();   
//            }
//            catch (Exception e) {
//                _log.errorTrace(METHOD_NAME, e);
//            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Exited ");
            }
        }
    }

    private void handleOptInFeature(Connection p_con, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        final String METHOD_NAME = "handleOptInFeature";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered " + p_channelUserVO);
        }
        int updateCount = 0;
        _isAlreadyOptIn = channelUserDAO.isLMSProfileAlreadyOptIn(p_con, p_channelUserVO.getUserID());
        if (_isAlreadyOptIn) {
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.LMS_PROFILE_ALREADY_OPT_IN);
        } else {
            updateCount = channelUserDAO.updateLMSProfileForOptIn(p_con, p_channelUserVO.getUserID());
        }
        if (updateCount > 0) {
            if (p_con != null) {
                try {
                    p_con.commit();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
            }
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.LMS_PROFILE_OPT_IN_SUCCESS);
        }
    }

    private void handleOptOutFeature(Connection p_con, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        final String METHOD_NAME = "handleOptInFeature";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered " + p_channelUserVO);
        }
        int updateCount = 0;
        String isOptInAfterOptOutAllowed = null;
        try {
            isOptInAfterOptOutAllowed = Constants.getProperty("IS_USER_CAN_OPTIN_AFTER_OPTOUT");
            if (BTSLUtil.isNullString(isOptInAfterOptOutAllowed)) {
                isOptInAfterOptOutAllowed = "N";
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            isOptInAfterOptOutAllowed = "N";
        }
        _isAlreadyOptIn = channelUserDAO.isLMSProfileAlreadyOptIn(p_con, p_channelUserVO.getUserID());
        if (_isAlreadyOptIn) {
            if (PretupsI.YES.equals(isOptInAfterOptOutAllowed)) {
                updateCount = channelUserDAO.updateLMSProfileForOptOut(p_con, p_channelUserVO.getUserID());
            } else {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.LMS_PROFILE_OPT_OUT_FAILURE);
            }
        } else {
            updateCount = channelUserDAO.updateLMSProfileForOptOut(p_con, p_channelUserVO.getUserID());
        }
        if (updateCount > 0) {
            if (p_con != null) {
                try {
                    p_con.commit();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
            }
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.LMS_PROFILE_OPT_OUT_SUCCESS);
        }
    }
}
