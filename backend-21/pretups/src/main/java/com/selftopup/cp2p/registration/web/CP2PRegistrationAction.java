package com.selftopup.cp2p.registration.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.spring.custom.action.Globals;

import com.opensymphony.xwork2.interceptor.ScopedModelDriven;
import com.selftopup.common.BTSLActionSupport;
import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.BTSLMessages;
import com.selftopup.cp2p.login.businesslogic.CP2PLoginLoggerVO;
import com.selftopup.cp2p.registration.businesslogic.CP2PRegistrationDAO;
import com.selftopup.cp2p.subscriber.businesslogic.CP2PSubscriberVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.loadcontroller.InstanceLoadVO;
import com.selftopup.loadcontroller.LoadControllerCache;
import com.selftopup.loadcontroller.NetworkLoadVO;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.gateway.businesslogic.MessageGatewayCache;
import com.selftopup.pretups.gateway.businesslogic.MessageGatewayVO;
import com.selftopup.pretups.gateway.businesslogic.PushMessage;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixCache;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixVO;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.util.OperatorUtil;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;
import com.selftopup.util.OracleUtil;

public class CP2PRegistrationAction extends BTSLActionSupport implements ScopedModelDriven<CP2PSubscriberVO> {

    private static final long serialVersionUID = 1L;
    OperatorUtil operatorUtil = new OperatorUtil();

    private CP2PSubscriberVO cp2pSubscriberVO;
    private String MODEL_SESSION_KEY;
    private String forward = null;

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

    public Log _log = LogFactory.getLog(this.getClass().getName());

    public String forgotPassword() {

        if (_log.isDebugEnabled())
            _log.debug("forgotPassword", "Entered");

        String returnStr = null;
        try {

            returnStr = "forgotPassword";
            return returnStr;

        } catch (Exception e) {
            _log.error("registerSubscriber", "Exceptin:e=" + e);
            this.addActionError(this.getText("error.general.processing"));
            _log.errorTrace("forgotPassword: Exception print stack trace:e=", e);
        } finally {

            if (_log.isDebugEnabled())
                _log.debug("forgotPassword", "Exiting" + returnStr);
        }
        return returnStr;

    }

    public String checkPassword() {

        if (_log.isDebugEnabled())
            _log.debug("checkPassword", "Entered");
        Connection con = null;
        String returnStr = null;
        try {

            con = OracleUtil.getConnection();
            CP2PRegistrationDAO _cp2pRegistrationDAO = new CP2PRegistrationDAO();

            CP2PSubscriberVO cp2pSubscriberVOFromDb = new CP2PSubscriberVO();
            cp2pSubscriberVOFromDb = _cp2pRegistrationDAO.loadCP2PSubscriberDetails(con, cp2pSubscriberVO.getMsisdn(), cp2pSubscriberVO.getLoginId());

            boolean msisdnBarred = _cp2pRegistrationDAO.isSubscriberBarred(con, cp2pSubscriberVO.getMsisdn());

            if (msisdnBarred) {
                throw new BTSLBaseException("cp2p.login.error.msisdnbarred", "forgotPassword");
            }

            cp2pSubscriberVO = validateSubscriberDetails(con, cp2pSubscriberVOFromDb, _request);

            // forward = mapping.findForward("forgotPassword");
            if (cp2pSubscriberVO.getValidStatus() != 0) {
                BTSLMessages btslMessage = new BTSLMessages("cp2p.forgotpassword.label.succesful", "forgotPassword");
                this.addActionError(this.getText("cp2p.forgotpassword.label.succesful"));
                returnStr = SUCCESS;
                return returnStr;
            } else {
                BTSLMessages btslMessage = new BTSLMessages("cp2p.forgotpassword.label.unsuccesful", "forgotPassword");
                this.addActionError(this.getText("cp2p.forgotpassword.label.unsuccesful"));
                returnStr = ERROR;
                return returnStr;
            }

        } catch (BTSLBaseException be) {
            _request.getSession().setAttribute(Globals.LOCALE_KEY, _request.getLocale());
            _log.error("checkPassword", "Exceptin:e=" + be);
            _log.errorTrace("checkPassword: Exception print stack trace: ", be);
            this.addActionError(this.getText(be.getMessage()));
            return ERROR;
        } catch (Exception e) {
            _request.getSession().setAttribute(Globals.LOCALE_KEY, _request.getLocale());
            _log.error("checkPassword", "Exceptin:e=" + e);
            _log.errorTrace("checkPassword: Exception print stack trace: ", e);
            this.addActionError(this.getText("error.general.processing"));
            return ERROR;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
            cp2pSubscriberVO.flush();
            if (_log.isDebugEnabled())
                _log.debug("checkPassword", "Exiting returnStr" + returnStr);
        }

    }

    /**
     * Method to validate the user like Network status, multiple login allowed
     * etc
     * 
     * @param p_con
     * @param p_loginForm
     * @param request
     * @param mapping
     * @param p_loginLoggerVO
     * @return
     * @throws BTSLBaseException
     */
    public CP2PSubscriberVO validateSubscriberDetails(Connection p_con, CP2PSubscriberVO p_cp2pSubscriberVO, HttpServletRequest request) throws Exception {
        int validStatus = 0;
        String randomPassword;
        String subscriberPassword;
        Date current_date = new Date();
        String[] argsArr = null;

        if (_log.isDebugEnabled())
            _log.debug("validateSubscriberDetails", "Entered");

        // this used for not allowing cache to store password. and donot
        // increase the password count as per discusswith sanjay sir
        // if(!BTSLUtil.isNullString(p_cp2pRegistrationForm.getOtherInfo())&&
        // !p_cp2pRegistrationForm.getPassword().equals(BTSLUtil.decryptPasswordOverRequest(p_cp2pRegistrationForm.getOtherInfo())))
        // throw new
        // BTSLBaseException("login.index.error.invalidpassword","cp2pLogin");

        if (p_cp2pSubscriberVO == null) {
            // p_cp2pLoginLoggerVO.setOtherInformation(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),"login.index.error.invalidlogin"));
            throw new BTSLBaseException("cp2p.forgotpassword.label.invalidmsisdnloginid", "forgotPassword");
        }

        else if (!BTSLUtil.isNullString(p_cp2pSubscriberVO.getMsisdn()) && !cp2pSubscriberVO.getMsisdn().equals(p_cp2pSubscriberVO.getMsisdn()))
            throw new BTSLBaseException("cp2p.forgotpassword.label.invalidmsisdn", "forgotPassword");
        else if (BTSLUtil.isNullString(cp2pSubscriberVO.getLoginId()))
            throw new BTSLBaseException("cp2p.forgotpassword.label.invalidmsisdn", "forgotPassword");
        else if (!BTSLUtil.isNullString(p_cp2pSubscriberVO.getLoginId()) && !cp2pSubscriberVO.getLoginId().equals(p_cp2pSubscriberVO.getLoginId()))
            throw new BTSLBaseException("cp2p.forgotpassword.label.invalidLoginID", "forgotPassword");

        // updated by shishupal on 14/03/2007
        // else if (userVO.getInvalidPasswordCount() ==
        // SystemPreferences.MAX_PASSWORD_BLOCK_COUNT)

        else if ((PretupsI.STATUS_SUSPEND.equals(p_cp2pSubscriberVO.getNetworkStatus()))) {
            // p_cp2pLoginLoggerVO.setOtherInformation(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),"login.index.error.networksuspend"));
            /*
             * If netwrok is temporally suspended and also the view_on_network
             * flag of that category
             * is N throw a exception which show the langguage 1 or language 2
             * message why netwrok is
             * suspended
             */
            String[] str = { p_cp2pSubscriberVO.getMessage() };
            throw new BTSLBaseException("cp2p.login.error.networksuspend", str, "forgotPassword");
        }

        else {

            _log.info("validateSubscriberDetails", "_loginID=" + cp2pSubscriberVO.getLoginId() + "     url=" + request.getRemoteAddr() + "  time=" + new Date());

            if (PretupsI.USER_STATUS_NEW.equals(p_cp2pSubscriberVO.getStatus()) || PretupsI.USER_STATUS_APPROVED.equals(p_cp2pSubscriberVO.getStatus())) {
                // p_cp2pLoginLoggerVO.setOtherInformation(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),"login.index.error.userapprovalpending"));
                throw new BTSLBaseException("cp2p.login.error.userapprovalpending", "forgotPassword");
            } else if (PretupsI.USER_STATUS_SUSPEND.equals(p_cp2pSubscriberVO.getStatus())) {
                // p_cp2pLoginLoggerVO.setOtherInformation(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),"login.index.error.usersuspended"));
                throw new BTSLBaseException("cp2p.login.error.usersuspended", "forgotPassword");
            } else if (PretupsI.USER_STATUS_BLOCK.equals(p_cp2pSubscriberVO.getStatus())) {
                // p_cp2pLoginLoggerVO.setOtherInformation(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),"login.index.error.userblocked"));
                throw new BTSLBaseException("cp2p.login.error.userblocked", "forgotPassword");
            } else if (PretupsI.USER_STATUS_DEREGISTERED.equals(p_cp2pSubscriberVO.getStatus())) {
                // p_cp2pLoginLoggerVO.setOtherInformation(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),"login.index.error.userderegistered"));
                throw new BTSLBaseException("cp2p.login.error.userderegistered", "forgotPassword");
            } else if (PretupsI.USER_STATUS_SUSPEND_REQUEST.equals(p_cp2pSubscriberVO.getStatus())) {
                // p_cp2pLoginLoggerVO.setOtherInformation(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),"login.index.error.usersuspendedrequest"));
                throw new BTSLBaseException("cp2p.login.error.usersuspendedrequest", "forgotPassword");
            } else if (PretupsI.USER_STATUS_DELETE_REQUEST.equals(p_cp2pSubscriberVO.getStatus())) {
                // p_cp2pLoginLoggerVO.setOtherInformation(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),"login.index.error.userdeletedrequest"));
                throw new BTSLBaseException("cp2p.login.error.userdeletedrequest", "forgotPassword");
            }

            else {
                if (_log.isDebugEnabled()) {
                    _log.debug("validateSubscriberDetails()", " Calling CP2PSessionCounter to checkMaxLocationTypeUsers");
                }

                populateLanguageSettings(p_cp2pSubscriberVO);
                randomPassword = operatorUtil.randomPwdGenerate();
                subscriberPassword = BTSLUtil.encryptText(randomPassword);
                CP2PRegistrationDAO cp2pRegistrationDAO = new CP2PRegistrationDAO();
                // int count =
                // cp2pRegistrationDAO.updateSubscriberPasswordDetails(p_con,subscriberPassword,cp2pSubscriberVO.getMsisdn(),cp2pSubscriberVO.getLoginId(),cp2pSubscriberVO)
                // ;
                int count = cp2pRegistrationDAO.updateSubscriberPasswordDetails(p_con, subscriberPassword, current_date, cp2pSubscriberVO.getMsisdn(), cp2pSubscriberVO.getLoginId(), p_cp2pSubscriberVO);
                if (count > 0) {
                    // Added this code from CP2PRegistrationDAO.addSubscriber
                    p_cp2pSubscriberVO.setMessageCode(SelfTopUpErrorCodesI.CP2P_WEB_FORGOTPASSWORD_SMS);
                    argsArr = new String[2];

                    if ("SHA".equals(SystemPreferences.PINPAS_EN_DE_CRYPTION_TYPE))
                        argsArr[1] = BTSLUtil.decryptText(randomPassword);
                    else
                        argsArr[1] = randomPassword;

                    argsArr[0] = p_cp2pSubscriberVO.getLoginId();
                    p_cp2pSubscriberVO.setMessageArguments(argsArr);
                    String subscriberMessage = BTSLUtil.getMessage(p_cp2pSubscriberVO.getLocale(), p_cp2pSubscriberVO.getMessageCode(), p_cp2pSubscriberVO.getMessageArguments());
                    PushMessage pushMessage = new PushMessage(p_cp2pSubscriberVO.getMsisdn(), subscriberMessage, null, null, p_cp2pSubscriberVO.getLocale());
                    pushMessage.push();

                    p_cp2pSubscriberVO.setSubscriberPassword(subscriberPassword);
                    validStatus = 2;
                    p_con.commit();
                } else {
                    validStatus = 0;
                    p_con.rollback();
                }
                p_cp2pSubscriberVO.setValidStatus(validStatus);
            }
        }

        return p_cp2pSubscriberVO;
    }

    /**
     * Method that will populate the language to be used while sending the
     * response
     * 
     * @param p_cp2pSubscriberVO
     */
    private void populateLanguageSettings(CP2PSubscriberVO p_cp2pSubscriberVO) {

        if (p_cp2pSubscriberVO.getLocale() == null)
            p_cp2pSubscriberVO.setLocale(new Locale(p_cp2pSubscriberVO.getLanguage(), p_cp2pSubscriberVO.getCountry()));

    }

    public String registerSubscriber() {
        if (_log.isDebugEnabled()) {
            _log.debug("registerSubscriber", "Entered");
        }

        try {

            forward = "regSubscriber";
        } catch (Exception e) {
            _log.error("registerSubscriber", "Exception:e=" + e);
            _log.errorTrace("registerSubscriber: Exception print stack trace: ", e);
            forward = "regSubscriber";
            this.addActionError(this.getText("error.general.processing"));
            return forward;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("regSubscriber", "Exiting forward=" + forward);
        }
        return forward;
    }

    public String confirmRegistration() {

        if (_log.isDebugEnabled())
            _log.debug("confirmRegistration", "Entered");

        String forward = null;
        try {
            forward = "confirmRegistration";

            return forward;

        } catch (Exception e) {
            _request.getSession().setAttribute(Globals.LOCALE_KEY, _request.getLocale());
            _log.error("confirmRegistration", "Exceptin:e=" + e);
            this.addActionError(this.getText("error.general.processing"));
            _log.errorTrace("confirmRegistration: Exception print stack trace: ", e);
            return ERROR;
        } finally {

            if (_log.isDebugEnabled())
                _log.debug("confirmRegistration", "Exiting" + forward);
        }

    }

    /**
     * Method to validate the user like Network status, multiple login allowed
     * etc
     * 
     * @param p_con
     * @param p_loginForm
     * @param request
     * @param mapping
     * @param p_loginLoggerVO
     * @return
     * @throws BTSLBaseException
     */
    public void validateRegistrationDetails(CP2PSubscriberVO p_cp2pSubscriberVO, CP2PLoginLoggerVO p_cp2pLoginLoggerVO) throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("validateRegistrationDetails", "Entered");

        // CP2PSubscriberVO
        // cp2pSubscriberVO=p_cp2pRegistrationForm.getCp2pSubscriberVO();

        if (p_cp2pSubscriberVO == null) {
            p_cp2pLoginLoggerVO.setOtherInformation(this.getText("login.index.error.invalidlogin"));
            throw new BTSLBaseException("cp2p.forgotpassword.label.invalidmsisdnloginid", "regSubscriber");
        }

        else if ((PretupsI.STATUS_SUSPEND.equals(p_cp2pSubscriberVO.getNetworkStatus()))) {
            p_cp2pLoginLoggerVO.setOtherInformation(this.getText("login.index.error.networksuspend"));
            /*
             * If netwrok is temporally suspended and also the view_on_network
             * flag of that category
             * is N throw a exception which show the langguage 1 or language 2
             * message why netwrok is
             * suspended
             */
            String[] str = { p_cp2pSubscriberVO.getMessage() };
            throw new BTSLBaseException("cp2p.login.error.networksuspend", str, "regSubscriber");
        } else {

            if (PretupsI.USER_STATUS_NEW.equals(p_cp2pSubscriberVO.getStatus()) || PretupsI.USER_STATUS_APPROVED.equals(p_cp2pSubscriberVO.getStatus())) {
                p_cp2pLoginLoggerVO.setOtherInformation(this.getText("login.index.error.userapprovalpending"));
                throw new BTSLBaseException("cp2p.login.error.userapprovalpending", "regSubscriber");
            } else if (PretupsI.USER_STATUS_SUSPEND.equals(p_cp2pSubscriberVO.getStatus())) {
                p_cp2pLoginLoggerVO.setOtherInformation(this.getText("login.index.error.usersuspended"));
                throw new BTSLBaseException("cp2p.login.error.usersuspended", "regSubscriber");
            } else if (PretupsI.USER_STATUS_BLOCK.equals(p_cp2pSubscriberVO.getStatus())) {
                p_cp2pLoginLoggerVO.setOtherInformation(this.getText("login.index.error.userblocked"));
                throw new BTSLBaseException("cp2p.login.error.userblocked", "regSubscriber");
            } else if (PretupsI.USER_STATUS_DEREGISTERED.equals(p_cp2pSubscriberVO.getStatus())) {
                p_cp2pLoginLoggerVO.setOtherInformation(this.getText("login.index.error.userderegistered"));
                throw new BTSLBaseException("cp2p.login.error.userderegistered", "regSubscriber");
            } else if (PretupsI.USER_STATUS_SUSPEND_REQUEST.equals(p_cp2pSubscriberVO.getStatus())) {
                p_cp2pLoginLoggerVO.setOtherInformation(this.getText("login.index.error.usersuspendedrequest"));
                throw new BTSLBaseException("cp2p.login.error.usersuspendedrequest", "regSubscriber");
            } else if (PretupsI.USER_STATUS_DELETE_REQUEST.equals(p_cp2pSubscriberVO.getStatus())) {
                p_cp2pLoginLoggerVO.setOtherInformation(this.getText("login.index.error.userdeletedrequest"));
                throw new BTSLBaseException("cp2p.login.error.userdeletedrequest", "regSubscriber");
            }
        }
    }

    public String addSubscriber() {
        if (_log.isDebugEnabled())
            _log.debug("addSubscriber", "Entered");
        String forward = null;
        HttpSession session = _request.getSession();
        String httpURLPrefix = "http://";
        String urlToSend = null;
        String msisdnPrefix = null;
        InstanceLoadVO instanceLoadVO = null;
        HttpURLConnection _con = null;
        BufferedReader in = null;
        StringBuffer loggerMessage = null;
        Connection con = null;
        HashMap _map = null;
        if (_request.getParameter("backButton") != null) {
            forward = "regSubscriber";
            return forward;
        } else {
            String subscriberPassword = null;
            CP2PSubscriberVO cp2pSubscriberVO = this.cp2pSubscriberVO;
            String randomPassword = null;
            boolean msisdnExist = false;
            boolean loginIDExist = false;
            boolean msisdnBarred = false;
            int count = 0;
            String[] argsArr = null;
            try {

                // recreateSession(theForm,request);
                con = OracleUtil.getConnection();
                CP2PRegistrationDAO _cp2pRegistrationDAO = new CP2PRegistrationDAO();
                if (!BTSLUtil.isNullString(cp2pSubscriberVO.getMsisdn()))
                    msisdnExist = _cp2pRegistrationDAO.isSubscriberMobileNumberExist(con, cp2pSubscriberVO);

                if (msisdnExist) { // Check for Msisdn Barred
                    msisdnBarred = _cp2pRegistrationDAO.isSubscriberBarred(con, cp2pSubscriberVO.getMsisdn());

                    if (msisdnBarred) {
                        throw new BTSLBaseException("cp2p.login.error.msisdnbarred", "cp2pRegistration");

                    }

                    if (!BTSLUtil.isNullString(cp2pSubscriberVO.getLoginId()))
                        loginIDExist = _cp2pRegistrationDAO.isSubscriebrLoginIdExist(con, cp2pSubscriberVO.getLoginId());
                    if (loginIDExist) {
                        throw new BTSLBaseException("cp2p.registration.label.loginIdexist", "cp2pRegistration");
                    }
                    if (!BTSLUtil.isNullString(cp2pSubscriberVO.getLoginId()))
                        cp2pSubscriberVO.setLoginId(cp2pSubscriberVO.getLoginId().trim());
                    CP2PSubscriberVO cp2pSubscriberVOFromDb = new CP2PSubscriberVO();
                    cp2pSubscriberVOFromDb = _cp2pRegistrationDAO.loadCP2PSubscriberDetails(con, cp2pSubscriberVO.getMsisdn());

                    if (!BTSLUtil.isNullString(cp2pSubscriberVOFromDb.getLoginId())) {
                        BTSLMessages btslMessage = new BTSLMessages("cp2p.registration.label.duplicateregistration");
                        this.addActionError(this.getText(btslMessage.getMessageKey()));
                        forward = ERROR;
                        return forward;
                    }
                    CP2PLoginLoggerVO cp2pLoginLoggerVO = new CP2PLoginLoggerVO();
                    validateRegistrationDetails(cp2pSubscriberVOFromDb, cp2pLoginLoggerVO);
                    populateLanguageSettings(cp2pSubscriberVOFromDb);
                    randomPassword = operatorUtil.randomPwdGenerate();
                    subscriberPassword = BTSLUtil.encryptText(randomPassword);
                    // theForm.setSubscriberPassword(subscriberPassword);
                    // if(!BTSLUtil.isNullString(cp2pSubscriberVO.getLoginId())&&!BTSLUtil.isNullString(cp2pSubscriberVOFromDb.getLoginId()))
                    count = _cp2pRegistrationDAO.addSubscriber(con, cp2pSubscriberVO.getMsisdn(), cp2pSubscriberVO.getLoginId(), subscriberPassword, cp2pSubscriberVOFromDb);

                    if (count > 0) {
                        con.commit();
                        // Added this code from
                        // CP2PRegistrationDAO.addSubscriber
                        // cp2pSubscriberVOFromDb.setMessageCode(SelfTopUpErrorCodesI.CP2P_WEB_REGISTRATION_SMS);
                        cp2pSubscriberVOFromDb.setMessageCode(SelfTopUpErrorCodesI.STU_REG_SUCCESS_WITH_PIN_WEB);
                        argsArr = new String[4];
                        /*
                         * if("SHA".equals(SystemPreferences.
                         * PINPAS_EN_DE_CRYPTION_TYPE))
                         * argsArr[1] = randomPassword;
                         * else
                         */
                        argsArr[0] = cp2pSubscriberVO.getMsisdn();
                        argsArr[1] = cp2pSubscriberVO.getLoginId();
                        // argsArr[1] =
                        // ((LookupsVO)LookupsCache.getObject(PretupsI.SUBSRICBER_TYPE,cp2pSubscriberVO.getSubscriberType())).getLookupName();
                        argsArr[2] = randomPassword;
                        argsArr[3] = cp2pSubscriberVO.getPin();
                        cp2pSubscriberVOFromDb.setMessageArguments(argsArr);
                        String subscriberMessage = BTSLUtil.getMessage(cp2pSubscriberVOFromDb.getLocale(), cp2pSubscriberVOFromDb.getMessageCode(), cp2pSubscriberVOFromDb.getMessageArguments());
                        PushMessage pushMessage = new PushMessage(cp2pSubscriberVOFromDb.getMsisdn(), subscriberMessage, null, null, cp2pSubscriberVOFromDb.getLocale());
                        pushMessage.push();

                        cp2pSubscriberVOFromDb.setSubscriberPassword(subscriberPassword);
                        {
                            BTSLMessages btslMessage = new BTSLMessages("cp2p.registration.label.succesfulregistration");
                            this.addActionError(this.getText(btslMessage.getMessageKey()));
                            forward = SUCCESS;
                            return forward;
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (SQLException e) {
                        }
                        _log.error("addSubscriber", "Error: while adding Subscriber");
                        throw new BTSLBaseException(this, "addSubscriber", "error.general.processing");
                    }
                } else {
                    if (!BTSLUtil.isNullString(cp2pSubscriberVO.getLoginId()))
                        loginIDExist = _cp2pRegistrationDAO.isSubscriebrLoginIdExist(con, cp2pSubscriberVO.getLoginId());
                    if (loginIDExist) {
                        throw new BTSLBaseException("cp2p.registration.label.loginIdexist", "cp2pRegistration");
                    }
                    if (!BTSLUtil.isNullString(cp2pSubscriberVO.getLoginId()))
                        cp2pSubscriberVO.setLoginId(cp2pSubscriberVO.getLoginId().trim());
                    if (SystemPreferences.HTTPS_ENABLE)
                        httpURLPrefix = "https://";
                    MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(PretupsI.GATEWAY_TYPE_WEB);
                    if (_log.isDebugEnabled())
                        _log.debug("CP2PRegistration", "messageGatewayVO: " + messageGatewayVO);
                    if (messageGatewayVO == null) {
                        // throw exception with message no gateway found
                        _log.error("addSubscriber", "**************Message Gateway not found in cache**************");
                        throw new BTSLBaseException(this, "addSubscriber", "cp2p.registration.error.sessiondatanotfound", "addSubscriber");
                    }
                    if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus()))
                        throw new BTSLBaseException(this, "addSubscriber", "cp2p.registration.error.messagegatewaynotactive", "addSubscriber");
                    else if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getRequestGatewayVO().getStatus()))
                        throw new BTSLBaseException(this, "addSubscriber", "cp2p.registration.error.reqmessagegatewaynotactive", "addSubscriber");
                    String saperator = null;
                    if (!BTSLUtil.isNullString(SystemPreferences.CHNL_PLAIN_SMS_SEPARATOR))
                        saperator = SystemPreferences.CHNL_PLAIN_SMS_SEPARATOR;
                    else
                        saperator = " ";
                    String msgGWPass = null;
                    // If Encrypted Password check box is not checked. i.e. send
                    // password in request as plain.
                    if (messageGatewayVO.getReqpasswordtype().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX))
                        msgGWPass = BTSLUtil.decryptText(messageGatewayVO.getRequestGatewayVO().getPassword());
                    else
                        // If Encrypted Password check box is checked. i.e. send
                        // password in request as encrypted.
                        msgGWPass = messageGatewayVO.getRequestGatewayVO().getPassword();

                    msisdnPrefix = PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredIdentificationNumber(cp2pSubscriberVO.getMsisdn()));
                    NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                    String[] strArr = null;
                    if (networkPrefixVO == null) {
                        strArr = new String[] { cp2pSubscriberVO.getMsisdn() };
                        throw new BTSLBaseException("CP2PRegistrationAction", "addSubscriber", SelfTopUpErrorCodesI.CHNL_ERROR_RECR_NOTFOUND_RECEIVERNETWORK, 0, strArr, null);
                    }
                    String networkCode = networkPrefixVO.getNetworkCode();
                    String smsInstanceID = null;

                    // Changed to handle multiple SMS servers for C2S and P2P on
                    // 20/07/06
                    if (LoadControllerCache.getNetworkLoadHash() != null && LoadControllerCache.getNetworkLoadHash().containsKey(LoadControllerCache.getInstanceID() + "_" + networkCode))
                        smsInstanceID = ((NetworkLoadVO) (LoadControllerCache.getNetworkLoadHash().get(LoadControllerCache.getInstanceID() + "_" + networkCode))).getC2sInstanceID();
                    else {
                        _log.error("CP2PRegistrationAction", " Not able to get the instance ID for the network=" + networkCode + " where the request for registration needs to be send");
                        throw new BTSLBaseException(this, "cp2pRegistration", "cp2p.registration.label.unsuccesfulregistration", "cp2pRegistration");
                    }
                    instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(smsInstanceID + "_" + networkCode + "_" + PretupsI.REQUEST_SOURCE_TYPE_SMS);
                    if (instanceLoadVO == null)
                        instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(smsInstanceID + "_" + networkCode + "_" + PretupsI.REQUEST_SOURCE_TYPE_WEB);
                    if (instanceLoadVO == null)// Entry for Dummy(used for
                                               // Apache)
                        instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(smsInstanceID + "_" + networkCode + "_" + PretupsI.REQUEST_SOURCE_TYPE_DUMMY);
                    // SenderVO
                    // senderVO=SubscriberBL.validateSubscriberDetails(con,filteredMSISDN);
                    urlToSend = httpURLPrefix + instanceLoadVO.getHostAddress() + ":" + instanceLoadVO.getHostPort() + Constants.getProperty("CHANNEL_WEB_CP2PSUBSCRIBER_REGISTRATION_SERVLET") + "?MSISDN=";
                    urlToSend = urlToSend + cp2pSubscriberVO.getMsisdn() + "&USERLOGINID=" + cp2pSubscriberVO.getLoginId() + "&EMAILID=" + cp2pSubscriberVO.getEmailId() + "&IMEI=" + PretupsI.DEFAULT_P2P_WEB_IMEI + "&MESSAGE=" + URLEncoder.encode(PretupsI.SERVICE_TYPE_SELFTOPUP_USER_REGISTRATION + saperator + PretupsI.DEFAULT_P2P_WEB_IMEI + saperator + cp2pSubscriberVO.getMsisdn() + saperator + cp2pSubscriberVO.getEmailId());
                    urlToSend = urlToSend + "&REQUEST_GATEWAY_CODE=" + messageGatewayVO.getGatewayCode() + "&REQUEST_GATEWAY_TYPE=" + messageGatewayVO.getGatewayType();
                    urlToSend = urlToSend + "&LOGIN=" + messageGatewayVO.getRequestGatewayVO().getLoginID();
                    urlToSend = urlToSend + "&PASSWORD=" + msgGWPass + "&SOURCE_TYPE=" + PretupsI.GATEWAY_TYPE_WEB + "&SERVICE_PORT=" + messageGatewayVO.getRequestGatewayVO().getServicePort();

                    URL url = null;
                    url = new URL(urlToSend);
                    if (_log.isDebugEnabled())
                        _log.debug("addSubscriber", "URL: =" + url);

                    try {
                        if (SystemPreferences.HTTPS_ENABLE)
                            _con = BTSLUtil.getConnection(url);
                        else
                            _con = (HttpURLConnection) url.openConnection();
                        _con.setDoInput(true);
                        _con.setDoOutput(true);
                        _con.setRequestMethod("GET");
                        in = new BufferedReader(new InputStreamReader(_con.getInputStream()));
                    } catch (Exception e) {
                        _log.error("addSubscriber", e.getMessage());
                        _log.errorTrace("addSubscriber: Exception print stack trace: ", e);
                        String arr[] = new String[2];
                        arr[0] = instanceLoadVO.getHostAddress();
                        arr[1] = instanceLoadVO.getHostPort();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRegistrationAction[addSubscriber]", "", "", "", "Exception:" + e.getMessage());
                        throw new BTSLBaseException(this, "addSubscriber", "cp2p.registration.error.connectionfailed", 0, arr, "CP2PRegistrationAction");
                    }
                    String responseStr = null;
                    String finalResponse = "";
                    while ((responseStr = in.readLine()) != null) {
                        finalResponse = finalResponse + responseStr;
                    }

                    if (_log.isDebugEnabled())
                        _log.debug("addSubscriber", "Response: =" + finalResponse);

                    if (!BTSLUtil.isNullString(finalResponse)) {

                        _map = BTSLUtil.getStringToHash(finalResponse, "&", "=");
                        finalResponse = URLDecoder.decode((String) _map.get("MESSAGE"), "UTF16");
                        String txn_status = (String) _map.get("TXN_STATUS");
                        if (_log.isDebugEnabled())
                            _log.debug("addSubscriber", "message=" + finalResponse + "TXN_STATUS=" + txn_status);
                        // changed by ankit Z on date 3/8/06 for problem as
                        // below:
                        // If there is mo mclass in message and message have
                        // colon then previously it remove the message before
                        // colon
                        // So now it only remove portion of message before colon
                        // if there is mclass
                        if (!BTSLUtil.isNullString(finalResponse) && finalResponse.indexOf("mclass^") > -1 && finalResponse.indexOf(":") > -1)
                            finalResponse = finalResponse.substring(finalResponse.indexOf(":") + 1);
                        String[] arr = new String[1];
                        arr[0] = URLDecoder.decode(finalResponse, "UTF16");
                        // BTSLMessages btslMessage = new
                        // BTSLMessages("btsl.blank.message",arr,"CP2PRegistrationAction");
                        BTSLMessages btslMessage = null;
                        if (PretupsI.TXN_STATUS_SUCCESS.equals(txn_status)) {
                            btslMessage = new BTSLMessages("cp2p.registration.label.succesfulregistration");
                            this.addActionError(this.getText(btslMessage.getMessageKey()));
                            forward = SUCCESS;
                            return forward;
                        } else// changed by ankit Z on date 3/8/06 for problem
                              // of value loss on error case
                        {
                            btslMessage = new BTSLMessages("cp2p.registration.label.unsuccesfulregistration", arr, "addSubscriber");
                            this.addActionError(this.getText(btslMessage.getMessageKey()));
                            forward = ERROR;
                            return forward;
                        }

                    } else {
                        forward = ERROR;
                        throw new BTSLBaseException("cp2p.registration.label.unsuccesfulregistration", "cp2pRegistration");
                    }

                }

            } catch (BTSLBaseException be) {
                /*
                 * If exception comes during login we are forwarding to the
                 * cp2pLogin.jsp
                 * so before fowarding to the cp2pRegistration.jsp we need to
                 * set the default locale
                 */
                _request.getSession().setAttribute(Globals.LOCALE_KEY, _request.getLocale());
                BTSLBaseException beException = new BTSLBaseException(this, "addSubscriber", be.getMessage(), 0, be.getArgs(), "cp2pRegistration");
                _log.error("addSubscriber", "Exceptin:e=" + be);
                _log.errorTrace("addSubscriber: Exception print stack trace: ", be);
                this.addActionError(this.getText(be.getMessage()));
                return ERROR;
            } catch (Exception e) {
                /*
                 * If exception comes during login we are forwarding to the
                 * cp2pRegistration.jsp
                 * so before fowarding to the cp2pRegistration.jsp we need to
                 * set the default locale
                 */
                _request.getSession().setAttribute(Globals.LOCALE_KEY, _request.getLocale());
                _log.error("addSubscriber", "Exceptin:e=" + e);
                _log.errorTrace("addSubscriber: Exception print stack trace: ", e);
                this.addActionError(this.getText("error.general.processing"));
                return ERROR;
            } finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (Exception e) {
                    }
                }
                // write the user information in the Log file
                cp2pSubscriberVO.flush();
                if (_log.isDebugEnabled())
                    _log.debug("addSubscriber", "Exiting" + forward);
            }

        }
    }

    public String checkAvailabilityofLoginID() {

        if (_log.isDebugEnabled())
            _log.debug("checkAvailabilityofLoginID", "Entered");

        String forward = null;
        String loginID = null;
        Connection con = null;
        boolean loginIDExist = false;
        try {
            con = OracleUtil.getConnection();
            CP2PRegistrationDAO _cp2pRegistrationDAO = new CP2PRegistrationDAO();
            loginID = cp2pSubscriberVO.getLoginId();
            if (!BTSLUtil.isNullString(loginID))
                loginIDExist = _cp2pRegistrationDAO.isSubscriebrLoginIdExist(con, loginID);
            else {
                BTSLMessages btslMessage = new BTSLMessages("cp2p.registration.label.loginidrqd", "cp2pRegistration");
                this.addActionError(btslMessage.getMessageKey());
                forward = ERROR;
                return forward;
            }

            if (loginIDExist) {
                BTSLMessages btslMessage = new BTSLMessages("cp2p.registration.label.loginIdexist", "cp2pRegistration");
                this.addActionError(btslMessage.getMessageKey());
                forward = ERROR;
                return forward;
            } else {
                throw new BTSLBaseException("cp2p.registration.label.loginIddonotexist", "cp2pRegistration");

            }

        } catch (Exception e) {
            _request.getSession().setAttribute(Globals.LOCALE_KEY, _request.getLocale());
            _log.error("checkAvailabilityofLoginID", "Exceptin:e=" + e);
            this.addActionError(this.getText("error.general.processing"));
            _log.errorTrace("checkAvailabilityofLoginID: Exception print stack trace: ", e);
            return ERROR;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
            if (_log.isDebugEnabled())
                _log.debug("checkAvailabilityofLoginID", "Exiting" + forward);
        }

    }

    public void validate() {

        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered");
        if (_request.getServletPath().equals("/cp2pregistration/regaction_confirmRegistration.action")) {
            if (cp2pSubscriberVO.getMsisdn().length() == 0) {
                this.addActionError(this.getText("cp2p.registration.label.msisdnrqd"));
            } else if (!BTSLUtil.isValidMSISDN(cp2pSubscriberVO.getMsisdn())) {
                this.addActionError(this.getText("cp2p.registration.label.msisdn.nonmumeric"));
            }

            if (cp2pSubscriberVO.getEmailId().length() == 0) {
                this.addActionError(this.getText("cp2p.registration.label.emaiidrqd"));

            } else if (!BTSLUtil.validateEmailID(cp2pSubscriberVO.getEmailId())) {
                this.addActionError(this.getText("cp2p.registration.label.emaiid.incorrect"));
            }
            if (cp2pSubscriberVO.getLoginId().length() == 0) {
                this.addActionError(this.getText("cp2p.registration.label.loginidrqd"));

            } else if (!BTSLUtil.isAlphaNumeric(cp2pSubscriberVO.getLoginId())) {
                this.addActionError(this.getText("cp2p.registration.label.loginid.incorrect"));
            }
        }
        if (_request.getServletPath().equals("/cp2pregistration/cp2pregistration_checkPassword.action")) {
            if (cp2pSubscriberVO.getMsisdn().length() == 0) {
                this.addActionError(this.getText("cp2p.registration.label.msisdnrqd"));
            }
            if (cp2pSubscriberVO.getLoginId().length() == 0) {
                this.addActionError(this.getText("cp2p.registration.label.loginidrqd"));

            }

        }
        if (_log.isDebugEnabled())
            _log.debug("validate", "Exiting");

    }

}
