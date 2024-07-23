package com.btsl.pretups.requesthandler;

import java.sql.Connection;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserPhonesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class InitiateResetPinController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(InitiateResetPinController.class.getName());
    private String _requestID = null;
    private static OperatorUtilI _operatorUtil = null;

    static {
        String utilClassName = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClassName).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InitiateResetPinController[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        Connection con = null;
        MComConnectionI mcomCon = null;
        _requestID = p_requestVO.getRequestIDStr();
        String Msisdn = null;
        String pin=null;
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, _requestID, " Entered " + p_requestVO);
        }
        try {
            Msisdn = p_requestVO.getRequestMSISDN();
            String[] p_requestArr = p_requestVO.getRequestMessageArray();
            Msisdn=p_requestArr[1];
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            ChannelUserVO channelUserVO = ChannelUserVO.getInstance();
            UserPhoneVO userPhoneVO = new UserPhoneVO();

            channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();

            Locale locale = null;

            int messageLength = p_requestArr.length;
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Message Length=" + messageLength);
            }

            String CheckNULL = channelUserDAO.securityQuesAns(con, Msisdn);
            if (BTSLUtil.isNullString(CheckNULL)) {
                p_requestVO.setSuccessTxn(false);
                p_requestVO.setMessageCode(PretupsErrorCodesI.SECURITY_QUESTION_NULL);
                throw new BTSLBaseException("InitiateResetPinController", METHOD_NAME, PretupsErrorCodesI.SECURITY_QUESTION_NULL);
            } else {
                UserPhonesDAO userPhonesDAO = new UserPhonesDAO();
                String otp = null;
                String userPhoneId = null;
                otp = _operatorUtil.generateOTP();
                userPhoneId = userPhonesDAO.loadUserPhonesID(con, channelUserVO);
                userPhoneVO.setOTP(otp);
                userPhoneVO.setUserPhonesId(userPhoneId);

                int updateCount = userPhonesDAO.pinResetOTP(con, userPhoneVO);

                if (updateCount > 0) {
                    // send response with security question
                    String SecurityQuesField = Constants.getProperty("SECURITY_QUESTION_FIELD");
                    p_requestVO.setSuccessTxn(true);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.SECURITY_QUESTION);
                    String[] SecurityQuestionField = new String[] { SecurityQuesField }; // setting
                                                                                         // security
                                                                                         // question
                                                                                         // field
                                                                                         // into
                                                                                         // the
                                                                                         // arguments
                                                                                         // for
                                                                                         // response
                                                                                         // message
                    p_requestVO.setMessageArguments(SecurityQuestionField);

                    // send OTP through SMS
                    BTSLMessages btslMessage = null;
                    PushMessage pushMessage = null;
                    String[] arr = null;

                    String recAlternetGatewaySMS = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_REQD_BY_ALT_GW"));
                    String reqruestGW = p_requestVO.getRequestGatewayCode();
                    if (!BTSLUtil.isNullString(recAlternetGatewaySMS) && (recAlternetGatewaySMS.split(":")).length >= 2) {
                        if (reqruestGW.equalsIgnoreCase(recAlternetGatewaySMS.split(":")[0])) {
                            reqruestGW = (recAlternetGatewaySMS.split(":")[1]).trim();
                            if (_log.isDebugEnabled()) {
                                _log.debug("process:User Message push through alternate GW for PIN Reset OTP", reqruestGW, "Requested GW was:" + p_requestVO.getRequestGatewayCode());
                            }
                        }
                    }
                    UserPhoneVO phoneVO = channelUserDAO.loadUserPhoneDetails(con, channelUserVO.getUserID());
                    if(phoneVO.getMsisdn().equals(Msisdn)) {
                    locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
                    p_requestVO.setLocale(locale);

                    String smsKey = PretupsErrorCodesI.PIN_RESET_OTP_SMS;
                    arr = new String[2];
                    arr[0] = userPhoneVO.getOTP();
                    arr[1] = Long.toString(TimeUnit.MINUTES.toSeconds(Long.parseLong(Constants.getProperty("OTP_EXPIRY_MINUTES"))));
                    btslMessage = new BTSLMessages(smsKey, arr);
                    pushMessage = new PushMessage(p_requestVO.getFilteredMSISDN(), btslMessage, null, reqruestGW, locale, p_requestVO.getNetworkCode()); // OTP
                                                                                                                                                   // SMS
                                                                                                                                                   // push
                    if(!PretupsI.YES.equalsIgnoreCase(Constants.getProperty("AUP_SUPPRESS_OTP_PINRESET"))){
                    pushMessage.push();
                    }
                    } else {
                    	p_requestVO.setMessageCode(PretupsErrorCodesI.PIN_NOT_VALID);
                        p_requestVO.setSuccessTxn(false);
                        throw new BTSLBaseException("InitiateResetPinController", METHOD_NAME, PretupsErrorCodesI.CANNOT_BE_PROCESSED);
                    }
                    mcomCon.finalCommit();
                }

                else {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.CANNOT_BE_PROCESSED);
                    p_requestVO.setSuccessTxn(false);
                    throw new BTSLBaseException("InitiateResetPinController", METHOD_NAME, PretupsErrorCodesI.CANNOT_BE_PROCESSED);
                }
            }

        } catch (Exception be) {
            p_requestVO.setSuccessTxn(false);
            _log.error("InitiateResetPinController", "Exception:be=" + be);
            _log.errorTrace(METHOD_NAME, be);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("InitiateResetPinController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("InitiateResetPinController", "Exiting");
            }
        }
        return;
    }

}