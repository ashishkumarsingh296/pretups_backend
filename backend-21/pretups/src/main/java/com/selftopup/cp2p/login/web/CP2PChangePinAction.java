package com.selftopup.cp2p.login.web;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import jakarta.servlet.http.HttpSession;

import com.selftopup.common.BTSLActionSupport;
import com.selftopup.common.BTSLBaseException;
import com.selftopup.cp2p.registration.businesslogic.CP2PRegistrationDAO;
import com.selftopup.cp2p.subscriber.businesslogic.CP2PSubscriberVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.gateway.businesslogic.PushMessage;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.util.OperatorUtilI;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;
import com.opensymphony.xwork2.interceptor.ScopedModelDriven;

public class CP2PChangePinAction extends BTSLActionSupport implements ScopedModelDriven<CP2PSubscriberVO> {

    private static final long serialVersionUID = 1L;

    private CP2PSubscriberVO cp2pSubscriberVO;
    private String MODEL_SESSION_KEY;

    public CP2PSubscriberVO getModel() {
        // TODO Auto-generated method stub
        return cp2pSubscriberVO;
    }

    public String getScopeKey() {
        // TODO Auto-generated method stub
        return MODEL_SESSION_KEY;
    }

    public void setModel(CP2PSubscriberVO arg0) {
        // TODO Auto-generated method stub
        this.cp2pSubscriberVO = (CP2PSubscriberVO) arg0;
    }

    public void setScopeKey(String arg0) {
        // TODO Auto-generated method stub
        MODEL_SESSION_KEY = arg0;
    }

    public CP2PSubscriberVO getCP2PSubscriberVO() {
        return cp2pSubscriberVO;
    }

    public void setCP2PSubscriberVO(CP2PSubscriberVO cp2pSubscriberVO) {
        this.cp2pSubscriberVO = cp2pSubscriberVO;
    }

    private Log _log = LogFactory.getLog(this.getClass().getName());
    public static OperatorUtilI _operatorUtil = null;
    public CP2PSubscriberVO _cp2pSubscriberVO = null;
    public SubscriberDAO _subscriberDAO = null;
    // Loads operator specific class
    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangePinController[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public String input() {
        return INPUT;
    }

    public String change() {
        if (_log.isDebugEnabled())
            _log.debug("change", "Entered");
        HttpSession session = _request.getSession();
        Connection con = null;
        int count;
        String returnStr = null;
        try {

            if (BTSLUtil.isNullString(cp2pSubscriberVO.getOldPin()) || BTSLUtil.isNullString(cp2pSubscriberVO.getNewPin()) || BTSLUtil.isNullString(cp2pSubscriberVO.getConfirmPin())) {
                this.addActionError(this.getText("cp2p.change.pin.null"));
                return ERROR;
            }
            _cp2pSubscriberVO = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");

            String newPin = cp2pSubscriberVO.getNewPin();
            String confirmPin = cp2pSubscriberVO.getConfirmPin();
            String oldPin = null;
            _subscriberDAO = new SubscriberDAO();
            CP2PRegistrationDAO _cp2pRegistrationDAO = new CP2PRegistrationDAO();
            con = OracleUtil.getConnection();
            String[] userDetails = new String[4];
            userDetails[0] = _cp2pSubscriberVO.getUserId();
            // String oldPin=
            // BTSLUtil.decryptText(_cp2pSubscriberVO.getSmsPin());
            boolean isMsisdnExist = false;
            if (!BTSLUtil.isNullString(_cp2pSubscriberVO.getMsisdn()))
                isMsisdnExist = _cp2pRegistrationDAO.isSubscriberMobileNumberExist(con, _cp2pSubscriberVO);
            if (isMsisdnExist) {
                oldPin = _cp2pSubscriberVO.getPin();
            } else
                throw new BTSLBaseException("cp2p.changepin.error.failed", "change");
            userDetails[1] = oldPin;
            userDetails[2] = _cp2pSubscriberVO.getMsisdn();

            checkOldPinEqualDbPin(userDetails[1], cp2pSubscriberVO.getOldPin());
            checkNewPinEqualOldPin(userDetails[1], newPin);
            checkNewPinEqualConfirmPin(newPin, confirmPin);
            _operatorUtil.validatePINRules(cp2pSubscriberVO.getNewPin());
            String modifificationType = PretupsI.USER_PIN_MANAGEMENT;
            boolean pin_status = _subscriberDAO.checkPasswordHistory(con, modifificationType, _cp2pSubscriberVO.getUserID(), _cp2pSubscriberVO.getMsisdn(), BTSLUtil.encryptText(cp2pSubscriberVO.getNewPin()));
            if (pin_status) {
                String lenArr[] = new String[2];
                lenArr[0] = String.valueOf(SystemPreferences.PREV_PIN_NOT_ALLOW);
                throw new BTSLBaseException(this, "updateUserPIN", SelfTopUpErrorCodesI.P2P_PIN_CHECK_HISTORY_EXIST, 0, lenArr, null);
            } else {
                _cp2pSubscriberVO.setSmsPin(newPin);
                count = _subscriberDAO.changePinFromWeb(con, _cp2pSubscriberVO);
                if (count > 0) {
                    con.commit();
                    _cp2pSubscriberVO.setSmsPin(BTSLUtil.encryptText(newPin));
                    String args[] = new String[] { newPin };
                    try {
                        (new PushMessage(_cp2pSubscriberVO.getMsisdn(), BTSLUtil.getMessage(new Locale(_cp2pSubscriberVO.getLanguage(), _cp2pSubscriberVO.getCountry()), SelfTopUpErrorCodesI.PIN_CHANGE_SUCCESS, args), "", PretupsI.REQUEST_SOURCE_TYPE_WEB, new Locale(_cp2pSubscriberVO.getLanguage(), _cp2pSubscriberVO.getCountry()))).push();
                    } catch (Exception e) {
                        _log.errorTrace("confirmCreditCardDetails: Exception print stack trace: ", e);
                    }
                    session.setAttribute("cp2pSubscriberVO", _cp2pSubscriberVO);
                    returnStr = SUCCESS;
                    this.addActionError(this.getText("cp2p.changepin.success"));
                    return returnStr;
                } else {
                    con.rollback();
                    throw new BTSLBaseException("cp2p.changepin.error.failed", "change");
                }
            }

        } catch (BTSLBaseException be) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
            }
            if (be.getMessage() != null) {
                if (be.getMessage().equals(SelfTopUpErrorCodesI.NEWPIN_NOTNUMERIC)) {
                    this.addActionError(this.getText("cp2p.changepin.error.notnumeric"));
                } else if (be.getMessage().equals(SelfTopUpErrorCodesI.PIN_LENGTHINVALID)) {
                    String msg[] = { String.valueOf(SystemPreferences.MIN_SMS_PIN_LENGTH), String.valueOf(SystemPreferences.MAX_SMS_PIN_LENGTH) };
                    this.addActionError(this.getText("cp2p.changepin.error.invalidlength", msg));
                } else if (be.getMessage().equals(SelfTopUpErrorCodesI.PIN_SAMEDIGIT)) {
                    this.addActionError(this.getText("cp2p.changepin.error.samedigits"));
                } else if (be.getMessage().equals(SelfTopUpErrorCodesI.PIN_CONSECUTIVE)) {
                    this.addActionError(this.getText("cp2p.changepin.error.consecutive.digits"));
                } else {
                    this.addActionError(this.getText(be.getMessage()));
                }
            }
            _log.error("change", "Exiting", "BTSLBaseException " + be.getMessage());
            return ERROR;

        } catch (Exception e) {
            _log.errorTrace("change: Exception print stack trace: ", e);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e1) {
            }
            this.addActionError(this.getText("error.general.processing"));
            _log.error("change", "Exiting", "BTSLBaseException " + e.getMessage());
            return ERROR;

        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("change", " Exited ");
        }

    }

    /**
     * @description : Method to check whether the New pin is same as Old pin
     * @param p_oldPin
     * @param p_pin
     * @throws BTSLBaseException
     */
    private void checkNewPinEqualOldPin(String p_oldPin, String p_pin) throws BTSLBaseException {
        if (p_pin.equals(p_oldPin))
            throw new BTSLBaseException("cp2p.changepin.error.newoldsame", "checkNewPinEqualOldPin");
    }

    /**
     * @description : Method to check whether the New pin is same as confirm pin
     * @param p_oldPin
     * @param p_pin
     * @throws BTSLBaseException
     */
    private void checkNewPinEqualConfirmPin(String p_newPin, String p_confirmPin) throws BTSLBaseException {
        if (!p_newPin.equals(p_confirmPin))
            throw new BTSLBaseException("cp2p.changepin.error.newconfirmnotsame", "checkNewPinEqualOldPin");
    }

    /**
     * @description : Method to check whether the Old is same as that in DB.
     * @param p_oldPin
     * @param p_pin
     * @throws BTSLBaseException
     */
    private void checkOldPinEqualDbPin(String p_oldPin, String p_dbPin) throws BTSLBaseException {
        if (!p_oldPin.equals(p_dbPin))
            throw new BTSLBaseException("cp2p.changepin.error.olddbnotsame", "checkNewPinEqualOldPin");
    }

    public void validate() {
        if (_request.getServletPath().equals("/cp2plogin/cp2pchangepin_change.action")) {
            if (BTSLUtil.isNullString(cp2pSubscriberVO.getOldPin()) || BTSLUtil.isNullString(cp2pSubscriberVO.getNewPin()) || BTSLUtil.isNullString(cp2pSubscriberVO.getConfirmPin())) {
                this.addActionError(this.getText("cp2p.change.pin.null"));
            }
        }
    }

}
