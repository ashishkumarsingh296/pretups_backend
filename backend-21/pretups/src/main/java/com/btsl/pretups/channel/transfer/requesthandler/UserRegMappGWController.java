package com.btsl.pretups.channel.transfer.requesthandler;

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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;

public class UserRegMappGWController implements ServiceKeywordControllerI {
    private static OperatorUtilI _operatorUtil = null;
    private ChannelUserVO channelUserVO = null;
    private int isUpdateChUserInfo = -1;
    private static Log _log = LogFactory.getLog(UserRegMappGWController.class.getName());
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CRBTRegistrationController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("UserRegMappGWController process", "Entered p_requestVO=" + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        ChannelUserTxnDAO channelUserTxnDAO = null;
        final Date date = new Date();
        try {
            channelUserTxnDAO = new ChannelUserTxnDAO();
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            final String[] p_requestArr = p_requestVO.getRequestMessageArray();
            channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            HashMap requestMap = p_requestVO.getRequestMap();
            String mHash=(String) requestMap.get("MHASH");
            String token=(String) requestMap.get("TOKEN");

            if ((PretupsI.MAPP_REG_REQ.equals(p_requestVO.getServiceType())) && (!p_requestArr[1].equals(p_requestVO.getImei()))) {
                channelUserVO.setDecryptionKey(BTSLUtil.genrateAESKey());
                channelUserVO.setEmail(p_requestVO.getEmailId());
                channelUserVO.setImei(p_requestArr[1]);                
                
                p_requestVO.setEncryptionKey(channelUserVO.getDecryptionKey());
                /*if ((((ChannelUserVO) p_requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)) {
                    try {
                        ChannelUserBL.validatePIN(con, (ChannelUserVO) p_requestVO.getSenderVO(), p_requestArr[2]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN_BILLPAY)) || (be.getMessageKey()
                            .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK_BILLPAY)))) {
                            con.commit();
                        }
                        throw be;
                    }
                }*/
                channelUserVO = channelUserTxnDAO.loadUserOTPDeatils(con, channelUserVO,PretupsI.SERVICE_TYPE_USER_AUTH);

                if (_log.isDebugEnabled()) {
                    _log.debug("UserRegMappGWController process", "Entered channelUserVO.getOtpInvalidCount()"+channelUserVO.getOtpInvalidCount()+"Integer.parseInt(Constants.getProperty(\"OTP_MAX_ATTEMPTS_APP\"))=" + Integer.parseInt(Constants.getProperty("OTP_MAX_ATTEMPTS_APP")));
                }
                if (channelUserVO.getOtpInvalidCount() >= Integer.parseInt(Constants.getProperty("OTP_MAX_ATTEMPTS_APP"))) {
                    throw new BTSLBaseException("UserRegMappGWController", "process", PretupsErrorCodesI.MAX_INVALID_ATTEMPTS_REACHED);
                } else if (!BTSLUtil.encryptText(p_requestArr[3]).equals(channelUserVO.getOTP())) {
                    isUpdateChUserInfo = channelUserTxnDAO.updateOTPDetails(con, channelUserVO);
                    if (isUpdateChUserInfo > 0) {
                        con.commit();
                    }
                    if (channelUserVO.getOtpInvalidCount()+1 >= Integer.parseInt(Constants.getProperty("OTP_MAX_ATTEMPTS_APP"))) {
                        throw new BTSLBaseException("UserRegMappGWController", "process", PretupsErrorCodesI.MAX_INVALID_ATTEMPTS_REACHED);
                    }
                    throw new BTSLBaseException("UserRegMappGWController", "process", PretupsErrorCodesI.INVALID_OTP_APP);
                } else if (BTSLUtil.isTimeExpired(channelUserVO.getOtpModifiedOn(), Integer.parseInt(Constants.getProperty("OTP_EXPIRY_MINUTES_APP")))) {
                    throw new BTSLBaseException("UserRegMappGWController", "process", PretupsErrorCodesI.OTP_APP_EXPIRED);
                }
                /*
                 * if(!(p_requestArr[2]).equals(channelUserVO.getEmail())){
                 * throw new
                 * BTSLBaseException("UserRegMappGWController","process"
                 * ,PretupsErrorCodesI.INVALID_EMAIL_MAPP);
                 * }
                 */
                //System.out.println("gaurav" + "controllr");
                isUpdateChUserInfo = channelUserTxnDAO.updateImeiAndEncKey(con, channelUserVO,mHash,token);
                // isUpdateChUserInfo = channelUserTxnDAO.updateOTPDetails(con,
                // channelUserVO);

                if (isUpdateChUserInfo > 0) {
                    con.commit();
                    p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_USER_REG_MAPPGW_SUCC);
                } else {
                    throw new BTSLBaseException("UserRegMappGWController", METHOD_NAME, PretupsErrorCodesI.REQ_NOT_PROCESS);
                }
            }

            else {
                throw new BTSLBaseException("UserRegMappGWController", "process", PretupsErrorCodesI.MAPP_USER_ALREADY_REGISTERED);
            }

        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
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
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.error(METHOD_NAME, "Exception:" + ee.getMessage());
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRegMappGWController[process]", "", "", "",
                "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
			if (mcomCon != null) {
				mcomCon.close("UserRegMappGWController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Exited ");
            }
        }
    }
}
