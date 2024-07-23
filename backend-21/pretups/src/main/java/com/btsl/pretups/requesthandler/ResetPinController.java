package com.btsl.pretups.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class ResetPinController implements ServiceKeywordControllerI {

    private static Log log = LogFactory.getLog(ResetPinController.class.getName());
    private String _requestID = null;
    private static OperatorUtilI _operatorUtil = null;

    static {
        String utilClassName = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClassName).newInstance();
        } catch (Exception e) {
            log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ResetPinController[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        Connection con = null;MComConnectionI mcomCon = null;
        _requestID = p_requestVO.getRequestIDStr();
        String answer = null;
        String msisdn = null;
        String correctAnswer = null;
        String otp = null;

        String newPin=null;
        String confirmNewPin=null;

        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, _requestID, " Entered " + p_requestVO);
        }
        try {
            String[] p_requestArr = p_requestVO.getRequestMessageArray();
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            UserPhonesDAO userPhonesDAO = new UserPhonesDAO();
            ChannelUserVO channelUserVO = new ChannelUserVO();
            ArrayList<Object> list = new ArrayList<Object>();
            msisdn = p_requestArr[1];
            // msisdn = p_requestVO.getRequestMSISDN();
            log.debug(METHOD_NAME, "## parameters p_requestArr : " + Arrays.toString(p_requestArr));
            
            int messageLength = p_requestArr.length;
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Message Length=" + messageLength);
            }

            //OTP suppress requirement from AUP client
            try {
            if(PretupsI.YES.equalsIgnoreCase(Constants.getProperty("AUP_SUPPRESS_OTP_PINRESET"))){
            	msisdn = p_requestArr[1];
            	answer = p_requestArr[2];
            	newPin = p_requestArr[3];
            	confirmNewPin = p_requestArr[4];
            	
            }else{
            	msisdn = p_requestArr[1];
            	otp = p_requestArr[2];
            	answer = p_requestArr[3];
            	newPin = p_requestArr[4];
            	confirmNewPin = p_requestArr[5];
            	
            }
			} catch (Exception e1) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.MANDATORY_EMPTY);
                p_requestVO.setSuccessTxn(false);
                throw new BTSLBaseException("ResetPinController", METHOD_NAME, PretupsErrorCodesI.MANDATORY_EMPTY);
			}
            
            channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            try {
            list = userPhonesDAO.verifyOTP(con, msisdn);
			} catch (Exception e) {
				 p_requestVO.setSuccessTxn(false);
	             throw new BTSLBaseException("ResetPinController", METHOD_NAME, PretupsErrorCodesI.OTP_INCORRECT);
			}
            Date otpDate = null;
            Date currentDate = null;

            otpDate = BTSLUtil.getTimestampFromUtilDate((java.util.Date) list.get(1));
            currentDate = BTSLUtil.getTimestampFromUtilDate(new java.util.Date());

            long diff = TimeUnit.MILLISECONDS.toMinutes(currentDate.getTime() - otpDate.getTime());

            correctAnswer = channelUserDAO.securityQuesAns(con, msisdn);


            if (!(correctAnswer.equals(answer))) // security question answer
                                                 // validation
            {
                p_requestVO.setMessageCode(PretupsErrorCodesI.SECURITY_ANSWER_INCORRECT);
                p_requestVO.setSuccessTxn(false);
                throw new BTSLBaseException("ResetPinController", METHOD_NAME, PretupsErrorCodesI.SECURITY_ANSWER_INCORRECT);
            }

            else if (otp !=null) // OTP validation
            {

            	if ("SHA".equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
            		if (log.isDebugEnabled()) {
                        log.debug(METHOD_NAME, "Validating otp using SHA");
                    }

            		if(!BTSLUtil.encryptText(otp).equals(list.get(0)))
            		{
            			p_requestVO.setMessageCode(PretupsErrorCodesI.OTP_INCORRECT);
            			p_requestVO.setSuccessTxn(false);
            			throw new BTSLBaseException("ResetPinController", METHOD_NAME, PretupsErrorCodesI.OTP_INCORRECT);
            		}
            	}
            	else if(!otp.equals(list.get(0))) {
            		p_requestVO.setMessageCode(PretupsErrorCodesI.OTP_INCORRECT);
            		p_requestVO.setSuccessTxn(false);
            		throw new BTSLBaseException("ResetPinController", METHOD_NAME, PretupsErrorCodesI.OTP_INCORRECT);

            	}
            }
            else if (diff > (Long.parseLong(Constants.getProperty("OTP_EXPIRY_MINUTES"))) && !PretupsI.YES.equalsIgnoreCase(Constants.getProperty("AUP_SUPPRESS_OTP_PINRESET"))) // OTP
                                                                                           // expiry
                                                                                           // validation
            {
                p_requestVO.setMessageCode(PretupsErrorCodesI.OTP_EXPIRED);
                p_requestVO.setSuccessTxn(false);
                throw new BTSLBaseException("ResetPinController", METHOD_NAME, PretupsErrorCodesI.OTP_EXPIRED);
            }

                try {
                    _operatorUtil.validatePINRules(newPin); // validation
                                                                     // for pin
                } 
				catch (BTSLBaseException be) {
                    log.errorTrace(METHOD_NAME, be);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.PIN_NOT_VALID);
                    p_requestVO.setSuccessTxn(false);
                    if (be.isKey()) {
                        if (PretupsErrorCodesI.PIN_LENGTHINVALID.equals(be.getMessageKey())) {
                            String lenArr[] = new String[2];
                            lenArr[0] = String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_PIN_MIN_LENGTH))).intValue());
                            lenArr[1] = String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_PIN_MAX_LENGTH))).intValue());
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_LENGTHINVALID, 0, lenArr, null);
                        } else if (PretupsErrorCodesI.NEWPIN_NOTNUMERIC.equals(be.getMessageKey())) {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_NEWPIN_NOTNUMERIC);
                        } else if (PretupsErrorCodesI.PIN_SAMEDIGIT.equals(be.getMessageKey())) {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_SAMEDIGIT);
                        } else if (PretupsErrorCodesI.PIN_CONSECUTIVE.equals(be.getMessageKey())) {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_CONSECUTIVE);
                        } else {
                            throw be;
                        }
                    }

                }
				final UserDAO userDAO = new UserDAO();
             if (userDAO.checkPasswordHistory(con, PretupsI.USER_PIN_MANAGEMENT, channelUserVO.getUserID(), msisdn, BTSLUtil.encryptText(newPin))) {
                        log.error("save", "Error: Pin exist in password_history table");
						  p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_PIN_CHECK_HISTORY_EXIST);
						   p_requestVO.setMessageArguments(new String[] { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue()), msisdn });
					p_requestVO.setSuccessTxn(false);
              
						throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_CHECK_HISTORY_EXIST,0,new String[] { String
                                        .valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue()), msisdn },null);
			 }

            if (!(newPin.equals(confirmNewPin))) // new pin and
                                                            // confirm new pin
                                                            // validation
            {
                p_requestVO.setMessageCode(PretupsErrorCodesI.PIN_CONFIRMPIN_DIFFERENT);
                p_requestVO.setSuccessTxn(false);
                throw new BTSLBaseException("ResetPinController", METHOD_NAME, PretupsErrorCodesI.PIN_CONFIRMPIN_DIFFERENT);
            }
            channelUserVO.setModifiedBy(channelUserVO.getUserID());
            channelUserVO.setModifiedOn(currentDate);
            UserPhoneVO phoneVO = channelUserDAO.loadUserPhoneDetails(con, channelUserVO.getUserID());
            if(phoneVO.getShowSmsPin().equals(newPin)) {            	
            	p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_PIN_OLDNEWSAME);
                p_requestVO.setSuccessTxn(false);
                throw new BTSLBaseException("ResetPinController", METHOD_NAME, PretupsErrorCodesI.C2S_PIN_OLDNEWSAME);
            }
            int count = channelUserDAO.changePin(con, newPin, channelUserVO);
            if (count > 0) {
               mcomCon.finalCommit();
                p_requestVO.setSuccessTxn(true);
                p_requestVO.setMessageCode(PretupsErrorCodesI.PIN_RESET_SUCCESSFUL);

                // push message on successful pin reset
                BTSLMessages btslMessage = null;
                PushMessage pushMessage = null;

                String recAlternetGatewaySMS = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_REQD_BY_ALT_GW"));
                String reqruestGW = p_requestVO.getRequestGatewayCode();
                if (!BTSLUtil.isNullString(recAlternetGatewaySMS) && (recAlternetGatewaySMS.split(":")).length >= 2) {
                    if (reqruestGW.equalsIgnoreCase(recAlternetGatewaySMS.split(":")[0])) {
                        reqruestGW = (recAlternetGatewaySMS.split(":")[1]).trim();
                        if (log.isDebugEnabled()) {
                            log.debug("process:User Message push through alternate GW for PIN Reset success", reqruestGW, "Requested GW was:" + p_requestVO.getRequestGatewayCode());
                        }
                    }
                }
                Locale locale = null;
                locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
                p_requestVO.setLocale(locale);

                String smsKey = PretupsErrorCodesI.PIN_RESET_SUCCESSFUL;
                btslMessage = new BTSLMessages(smsKey);
                pushMessage = new PushMessage(p_requestVO.getFilteredMSISDN(), btslMessage, null, null, locale, p_requestVO.getNetworkCode()); // success
                                                                                                                                               // SMS
                                                                                                                                               // push
                pushMessage.push();

            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.CANNOT_BE_PROCESSED);
                p_requestVO.setSuccessTxn(false);
                throw new BTSLBaseException("ResetPinController", METHOD_NAME, PretupsErrorCodesI.CANNOT_BE_PROCESSED);
            }

        } catch (Exception be) {
            p_requestVO.setSuccessTxn(false);
            log.error("ResetPinController", "Exception:be=" + be);
            log.errorTrace(METHOD_NAME, be);
        }

        finally {
			if (mcomCon != null) {
				mcomCon.close("ResetPinController#process");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug("ResetPinController", "Exiting");
            }
        }
        return;

    }
}
