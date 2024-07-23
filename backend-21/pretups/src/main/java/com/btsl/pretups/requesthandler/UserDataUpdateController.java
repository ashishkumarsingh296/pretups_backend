package com.btsl.pretups.requesthandler;

import java.sql.Connection;
import java.util.Date;

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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserPhonesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

public class UserDataUpdateController implements ServiceKeywordControllerI {

    private static Log _log = LogFactory.getLog(UserDataUpdateController.class.getName());
    private String _requestID = null;

    private static OperatorUtilI _operatorUtil = null;

    static {
        String utilClassName = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClassName).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDataUpdateController[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        Connection con = null;MComConnectionI mcomCon = null;
        String[] p_requestArr = p_requestVO.getRequestMessageArray();
        UserPhonesDAO userPhonesDAO = new UserPhonesDAO();
        ChannelUserVO channelUserVO = ChannelUserVO.getInstance();
        ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        UserVO userVO = new UserVO();
        UserDAO userDAO = new UserDAO();
        _requestID = p_requestVO.getRequestIDStr();
        String Msisdn = null;
        String requestPin = null;
        String correctPin = null;
        int updateCount;
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, _requestID, " Entered " + p_requestVO);
        }
        try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

            int messageLength = p_requestArr.length;
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Message Length=" + messageLength);
            }

            try {
            requestPin = (String) p_requestVO.getRequestMap().get("PIN");
            Msisdn = (String) p_requestVO.getRequestMap().get("MSISDN");
			} catch (Exception e) {
				requestPin=p_requestArr[2];
				Msisdn=p_requestArr[1];
			}
            correctPin = userPhonesDAO.loadPin(con, Msisdn);
            userVO = userDAO.loadUserDetailsByMsisdn(con, Msisdn);
            if(userVO==null || BTSLUtil.isNullString(correctPin)) {
            	mcomCon.finalCommit();
                p_requestVO.setSuccessTxn(false);
                p_requestVO.setMessageCode(PretupsErrorCodesI.NO_DATA);
            }
            channelUserVO.setMsisdn(Msisdn);
			if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "requestPin=" + requestPin+", Msisdn="+Msisdn);
            }
            boolean pinValidate=false;
            
            	if ("SHA".equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
            	if(correctPin.equals(BTSLUtil.encryptText(requestPin)))
            		pinValidate =true;
            	}
            	else if(requestPin.equals(correctPin)) {
            		pinValidate =true;
            	}
            	else
            		pinValidate =false;
    
            if (pinValidate) {
                if ((BTSLUtil.isNullString(p_requestVO.getRequestMap().get("SHORTNAME").toString()) && (BTSLUtil.isNullString(p_requestVO.getRequestMap().get("CONTACTPERSON").toString())) && (BTSLUtil.isNullString(p_requestVO.getRequestMap().get("SUBSCRIBERCODE").toString())) && (BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("APPOINTMENTDATE"))))) {
                	mcomCon.finalCommit();
                    p_requestVO.setSuccessTxn(false);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.NO_DATA);
                }

                if ((String) p_requestVO.getRequestMap().get("SHORTNAME") != null) {
                    if ((p_requestVO.getRequestMap().get("SHORTNAME").toString().length() <= 15)) {
                        channelUserVO.setShortName((String) p_requestVO.getRequestMap().get("SHORTNAME"));
                    } else {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.SHORT_NAME_LENGTH);
                        p_requestVO.setSuccessTxn(false);
                        throw new BTSLBaseException("UserDataUpdateController", METHOD_NAME, PretupsErrorCodesI.SHORT_NAME_LENGTH);
                    }

                }

                if ((String) p_requestVO.getRequestMap().get("CONTACTPERSON") != null) {
                    if ((p_requestVO.getRequestMap().get("CONTACTPERSON").toString().length() <= 80)) {
                        channelUserVO.setContactPerson((String) p_requestVO.getRequestMap().get("CONTACTPERSON"));
                    } else {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.CONTACT_PERSON_LENGTH);
                        p_requestVO.setSuccessTxn(false);
                        throw new BTSLBaseException("UserDataUpdateController", METHOD_NAME, PretupsErrorCodesI.CONTACT_PERSON_LENGTH);
                    }
                }

                if ((String) p_requestVO.getRequestMap().get("SUBSCRIBERCODE") != null) {
                    if ((p_requestVO.getRequestMap().get("SUBSCRIBERCODE").toString().length() <= 12)) {
                        channelUserVO.setEmpCode((String) p_requestVO.getRequestMap().get("SUBSCRIBERCODE"));
                    } else {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_CODE_LENGTH);
                        p_requestVO.setSuccessTxn(false);
                        throw new BTSLBaseException("UserDataUpdateController", METHOD_NAME, PretupsErrorCodesI.SUBSCRIBER_CODE_LENGTH);
                    }
                }

                if (!(BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("APPOINTMENTDATE")))) {
                    if (BTSLUtil.isValidDatePattern((String) p_requestVO.getRequestMap().get("APPOINTMENTDATE"))) {
                        channelUserVO.setAppointmentDate(BTSLUtil.getDateFromDateString((String) p_requestVO.getRequestMap().get("APPOINTMENTDATE")));
                    } else {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_DATE_FORMAT);
                        p_requestVO.setSuccessTxn(false);
                        throw new BTSLBaseException("UserDataUpdateController", METHOD_NAME, PretupsErrorCodesI.INVALID_DATE_FORMAT);
                    }
                }

                if ((String) p_requestVO.getRequestMap().get("SSN") != null) {
                    if ((p_requestVO.getRequestMap().get("SSN").toString().length() <= 15)) {
                        channelUserVO.setSsn((String) p_requestVO.getRequestMap().get("SSN"));
                    } else {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_REQ_SSN_LENGTH_EXCEEDS);
                        p_requestVO.setSuccessTxn(false);
                        throw new BTSLBaseException("UserDataUpdateController", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_SSN_LENGTH_EXCEEDS);
                    }
                }

                Date modified_on = new Date();
                channelUserVO.setModifiedOn(modified_on);
                channelUserVO.setModifiedBy(userVO.getUserID());
                updateCount = channelUserDAO.updateChannelUserInfoForPinReset(con, channelUserVO);

                if (updateCount > 0) {
                	mcomCon.finalCommit();
                    p_requestVO.setSuccessTxn(true);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.DATA_UPDATION_SUCCESSFUL);
                } else {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.CANNOT_BE_PROCESSED);
                    p_requestVO.setSuccessTxn(false);
                    throw new BTSLBaseException("UserDataUpdateController", METHOD_NAME, PretupsErrorCodesI.CANNOT_BE_PROCESSED);
                }

            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.PIN_NOT_VALID);
                p_requestVO.setSuccessTxn(false);
                throw new BTSLBaseException("UserDataUpdateController", METHOD_NAME, PretupsErrorCodesI.PIN_NOT_VALID);
            }

        } catch (Exception be) {
            p_requestVO.setSuccessTxn(false);
            _log.error("UserDataUpdateController", "Exception:be=" + be);
            _log.errorTrace(METHOD_NAME, be);
        }

        finally {
			if (mcomCon != null) {
				mcomCon.close("UserDataUpdateController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("UserDataUpdateController", "Exiting");
            }
        }
        return;
    }

}
