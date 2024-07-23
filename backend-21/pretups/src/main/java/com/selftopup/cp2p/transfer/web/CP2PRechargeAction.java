package com.selftopup.cp2p.transfer.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Date;

import jakarta.servlet.http.HttpSession;

import org.spring.custom.action.Globals;

import com.opensymphony.xwork2.interceptor.ScopedModelDriven;
import com.selftopup.common.BTSLActionSupport;
import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.BTSLMessages;
import com.selftopup.cp2p.login.businesslogic.CP2PLoginDAO;
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
import com.selftopup.pretups.network.businesslogic.NetworkPrefixCache;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixVO;
import com.selftopup.pretups.p2p.transfer.businesslogic.CardDetailsDAO;
import com.selftopup.pretups.p2p.transfer.businesslogic.CardDetailsVO;
import com.selftopup.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.util.OperatorUtilI;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;
import com.selftopup.util.OracleUtil;
import com.selftopup.util.UtilValidate;

public class CP2PRechargeAction extends BTSLActionSupport implements ScopedModelDriven<P2PTransferVO> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    public Log _log = LogFactory.getLog(this.getClass().getName());

    private P2PTransferVO p2pTransferVO;
    // ************P2P Receiver
    private static String _instanceCode = null;
    public static OperatorUtilI calculatorI = null;
    // ********
    private String MODEL_SESSION_KEY;

    public P2PTransferVO getModel() {
        return p2pTransferVO;
    }

    public String getScopeKey() {
        return MODEL_SESSION_KEY;
    }

    public void setModel(P2PTransferVO arg0) {
        this.p2pTransferVO = (P2PTransferVO) arg0;
    }

    public void setScopeKey(String arg0) {
        MODEL_SESSION_KEY = arg0;
    }

    public P2PTransferVO getP2PTransferVO() {
        return p2pTransferVO;
    }

    public void setP2PTransferVO(P2PTransferVO p2pTransferVO) {
        this.p2pTransferVO = p2pTransferVO;
    }

    public String input() {
        HttpSession session = _request.getSession();
        CP2PSubscriberVO loggedInUser = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");
        p2pTransferVO.flush();
        if (loggedInUser != null) {
            p2pTransferVO.setReceiverMsisdn(loggedInUser.getMsisdn());
        }
        return "amountPage";
    }

    public void validate() {
        String forward = null;
        P2PTransferVO p2pTransferVO = this.p2pTransferVO;
        this.getMonthYearList(p2pTransferVO);
        if ((_request.getServletPath().equals("/cp2precharge/cp2precharge_processRecharge.action")) && BTSLUtil.isNullString(_request.getParameter("btnBack"))) {
            String cardType = null;
            try {

                if (!BTSLUtil.isNullString(p2pTransferVO.getCardNumber())) {
                    if (!BTSLUtil.isNumeric(p2pTransferVO.getCardNumber())) {
                        this.addActionError(this.getText("cp2p.cardnumber.nonmuneric"));
                    } else {

                        cardType = UtilValidate.getCardType(p2pTransferVO.getCardNumber());
                        boolean flag = false;
                        if ("Unknown".equalsIgnoreCase(cardType)) {
                            flag = true;
                            this.addActionError(this.getText("cp2p.user.adhoc.recharge.unknowntype"));
                        }
                        // we're having only those card registered with the
                        // system which are allowed to do the transaction from
                        // the cards like VISA, MASTER, AM-EX
                        if (flag == false)
                            if (p2pTransferVO.getCardNumber().length() != 16) {
                                if (p2pTransferVO.getCardNumber().length() != 15)
                                    this.addActionError(this.getText("cp2p.user.creditcard.registration.unknowntype"));
                            }
                    }
                } else {
                    this.addActionError(this.getText("cp2p.cardnumber.required"));
                }

                if (BTSLUtil.isNullString(p2pTransferVO.getCardName())) {
                    this.addActionError(this.getText("cp2p.nameofembossing.required"));
                }
                if (p2pTransferVO.getCardName().length() > PretupsI.VALID_LENGTH_CARD_HOLDER_NAME) {
                    this.addActionError(this.getText("cp2p.nameofembossing.maxlength.error"));
                }

                boolean m = BTSLUtil.isNullString(p2pTransferVO.getMonthExpiry());
                boolean y = BTSLUtil.isNullString(p2pTransferVO.getYearExpiry());

                if ((m == false) && (y == false)) {
                    int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);

                    int expiryMonth = Integer.valueOf(p2pTransferVO.getMonthExpiry());
                    int expiryYear = Integer.valueOf(p2pTransferVO.getYearExpiry());

                    if ((expiryMonth <= currentMonth) && expiryYear <= (currentYear % 100)) {
                        this.addActionError(this.getText("cp2p.valid.date"));
                    } else {
                        p2pTransferVO.setCardExpiry(p2pTransferVO.getMonthExpiry() + p2pTransferVO.getYearExpiry().substring(p2pTransferVO.getYearExpiry().length() - 2));
                    }
                } else {
                    this.addActionError(this.getText("cp2p.Expirydate.required"));
                }
                if (BTSLUtil.isNullString(p2pTransferVO.getCvv())) {
                    this.addActionError(this.getText("cp2p.cvv.required"));
                } else if (!BTSLUtil.isNumeric(p2pTransferVO.getCvv())) {
                    this.addActionError(this.getText("cp2p.cvv.nonnumeric"));
                } else if (p2pTransferVO.getCvv().length() != 3) {
                    this.addActionError(this.getText("cp2p.cvv.lengtherror"));
                } else if (!BTSLUtil.isAlphaNumericIncludingSpace(p2pTransferVO.getCardName())) {
                    this.addActionError(this.getText("cp2p.user.creditcard.recharge.cardName.invalid"));
                }
            } catch (Exception e) {
                _log.error("validate", "Exception: " + e.getMessage());
                _log.errorTrace("validate: Exception print stack trace: ", e);
                this.addActionError(this.getText("error.processing"));

            } finally {
                if (_log.isDebugEnabled())
                    _log.debug("validate", "Exiting" + forward);
            }

        }

    }

    /**
     * Method to load the first page - CardRegistration.jsp
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public String loadCardPage() {
        if (_log.isDebugEnabled())
            _log.debug("loadCardPage", "Entered");
        String forward = null;
        HttpSession session = _request.getSession();
        Connection con = null;
        try {
            CP2PSubscriberVO loggedInUser = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");
            if (validateInputDetails(p2pTransferVO)) {
                forward = ERROR;
                return forward;
            }
            con = OracleUtil.getConnection();
            this.getMonthYearList(p2pTransferVO);
            p2pTransferVO.setBankName(PretupsI.DEFAULT_PAYMENT_GATEWAY);
            forward = "defaultCard";
        } catch (Exception e) {
            _log.errorTrace("loadCardPage: Exception print stack trace: ", e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException sqle) {
                _log.errorTrace("loadCardPage: Exception print stack trace: ", sqle);
            }
            if (_log.isDebugEnabled())
                _log.debug("loadCardPage", "Exiting" + forward);
        }
        return forward;
    }

    public String loadRegistredCardDetails() {

        if (_log.isDebugEnabled())
            _log.debug("loadRegistredCardDetails", "Entered");
        String forward = null;
        HttpSession session = _request.getSession();
        CardDetailsDAO dao = null;
        ArrayList cardDetailVOList = null;
        ArrayList cardDetailsList = null;
        Connection con = null;

        try {
            p2pTransferVO.semiFlush();
            CP2PSubscriberVO loggedInUser = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");
            if (validateInputDetails(p2pTransferVO)) {
                forward = ERROR;
                return forward;
            }
            con = OracleUtil.getConnection();
            cardDetailsList = new ArrayList();
            cardDetailVOList = new ArrayList();

            dao = new CardDetailsDAO();
            cardDetailVOList = dao.loadSubscriberRegisteredCardDetails(con, loggedInUser.getUserId());
            if (cardDetailVOList.isEmpty()) {
                this.addActionError(this.getText("cp2p.user.creditcard.autoenable.nocardfound"));
                forward = "noCardFound";
                return forward;
            } else {
                CardDetailsVO tempVO = null;
                for (int i = 0; i < cardDetailVOList.size(); i++) {
                    tempVO = (CardDetailsVO) cardDetailVOList.get(i);
                    cardDetailsList.add(tempVO.getCardNickName());
                }
                p2pTransferVO.setCardDetailsList(cardDetailsList);
            }
            p2pTransferVO.setBankName(PretupsI.DEFAULT_PAYMENT_GATEWAY);
            forward = "registeredCard";
        } catch (Exception e) {
            _log.errorTrace("loadRegistredCardDetails: Exception print stack trace: ", e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException sqle) {
                _log.errorTrace("loadRegistredCardDetails: Exception print stack trace: ", sqle);
            }
            if (_log.isDebugEnabled())
                _log.debug("loadRegistredCardDetails", "Exiting" + forward);
        }

        return forward;

    }

    private boolean validateInputDetails(P2PTransferVO p_p2pTransferVO) {

        if (_log.isDebugEnabled())
            _log.debug("validateInputDetails", "Entered");

        boolean errorFlag = false;
        Connection con = null;// OracleUtil.getConnection();
        try {
            HttpSession session = _request.getSession();
            CP2PSubscriberVO loggedInUser = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");
            String decimalAllowedServices = SystemPreferences.DECIMAL_ALLOWED_IN_SERVICES;
            ArrayList<String> arr = null;
            arr = new ArrayList<String>();
            boolean isDecimalAllow = false;
            con = OracleUtil.getConnection();
            String pin = null;
            CP2PLoginDAO _cp2pLoginDAO = new CP2PLoginDAO();
            CP2PSubscriberVO cp2pSubscriberVO = _cp2pLoginDAO.loadCP2PSubscriberDetails(con, loggedInUser.getLoginId(), loggedInUser.getPassword(), BTSLUtil.getBTSLLocale(_request));
            pin = BTSLUtil.decryptText(cp2pSubscriberVO.getSmsPin());

            StringTokenizer st = new StringTokenizer(decimalAllowedServices, ",");
            int i = 0;
            while (st.hasMoreElements()) {
                arr.add(st.nextElement().toString());
                System.out.println(st.nextToken());
                i++;
            }
            for (i = 0; i < arr.size(); i++) {
                if (arr.get(i).equals(PretupsI.SERVICE_TYPE_SELFTOPUP_ADHOCRECHARGE) || arr.get(i).equals(PretupsI.SERVICE_TYPE_SELFTOPUP_ADHOCRECHARGE)) {
                    isDecimalAllow = true;
                }
            }
            if (BTSLUtil.isNullString(p2pTransferVO.getRechargeAmount())) {
                this.addActionError(this.getText("cp2p.user.adhoc.recharge.amount.null"));
                errorFlag = true;

            } else if (isDecimalAllow && !BTSLUtil.isDecimalValue(p2pTransferVO.getRechargeAmount())) {
                this.addActionError(this.getText("cp2p.user.adhoc.recharge.amount.nonnumeric"));
                errorFlag = true;
            } else if (!isDecimalAllow && !BTSLUtil.isNumeric(p2pTransferVO.getRechargeAmount())) {
                this.addActionError(this.getText("cp2p.user.adhoc.recharge.amount.nonnumeric"));
                errorFlag = true;
            } else if (Double.valueOf(p2pTransferVO.getRechargeAmount()) > (Long) SystemPreferences.MAX_AUTOTOPUP_AMT) {
                String arr1[] = { String.valueOf((Long) SystemPreferences.MAX_AUTOTOPUP_AMT) };
                this.addActionError(this.getText("cp2p.user.creditcard.autoenable.amount.thresholdreached", arr1));
                errorFlag = true;
            }
            if (BTSLUtil.isNullString(p2pTransferVO.getPin())) {
                this.addActionError(this.getText("cp2p.user.adhoc.recharge.pin.null"));
                errorFlag = true;

            } else if (!BTSLUtil.isNumeric(p2pTransferVO.getPin())) {
                this.addActionError(this.getText("cp2p.user.adhoc.recharge.pin.nonnumeric"));
                errorFlag = true;
            }
            // else
            // if(!p2pTransferVO.getPin().equals(BTSLUtil.decryptText(loggedInUser.getSmsPin())))
            else if (!p2pTransferVO.getPin().equals(pin)) {
                this.addActionError(this.getText("cp2p.user.adhoc.recharge.pin.invalid"));
                errorFlag = true;
            }
            if (BTSLUtil.isNullString(p2pTransferVO.getReceiverMsisdn())) {
                this.addActionError(this.getText("cp2p.user.adhoc.recharge.receivermsisdn.null"));
                errorFlag = true;

            } else if (!BTSLUtil.isValidMSISDN(p2pTransferVO.getReceiverMsisdn())) {
                this.addActionError(this.getText("cp2p.user.adhoc.recharge.receivermsisdn.nonnumeric"));
                errorFlag = true;
            }

            return errorFlag;
        } catch (Exception e) {
            _log.errorTrace("validateInputDetails", e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    // ignored
                }
            }
            if (_log.isDebugEnabled())
                _log.debug("validateInputDetails", "Exiting errorFlag:" + errorFlag);
        }
        return errorFlag;
    }

    /**
     * method to get the month and year list
     * 
     * @param form
     */
    private void getMonthYearList(P2PTransferVO p_p2pTransferVO) {

        if (_log.isDebugEnabled())
            _log.debug("getMonthYearList", "Entered");

        ArrayList monthList = new ArrayList();
        ;
        ArrayList yearList = new ArrayList();

        for (int i = 1; i <= 12; i++) {
            String monthValue = "";
            if (i < 10)
                monthValue = String.valueOf("0" + i);
            else
                monthValue = String.valueOf(i);
            monthList.add(monthValue);
        }
        // yearList=new ArrayList();
        java.util.Calendar getRight = GregorianCalendar.getInstance();
        // int year=getRight.get(Calendar.YEAR);
        String yearAsString = String.valueOf(getRight.get(Calendar.YEAR)).substring(2);
        int year = Integer.parseInt(yearAsString);

        for (int i = year; i <= year + 20; i++) {
            String yearValue = String.valueOf(i);
            yearList.add(yearValue);
        }
        p_p2pTransferVO.setMonthList(monthList);
        p_p2pTransferVO.setYearList(yearList);

        if (_log.isDebugEnabled())
            _log.debug("getMonthYearList", "Exited");
    }

    public String processRecharge() {
        if (_log.isDebugEnabled())
            _log.debug("processRecharge", "Entered");
        String forward = null;
        HttpSession session = _request.getSession();
        String httpURLPrefix = "http://";
        String urlToSend = null;
        String msisdnPrefix = null;
        InstanceLoadVO instanceLoadVO = null;
        HttpURLConnection _con = null;
        BufferedReader in = null;
        Connection con = null;

        HashMap _map = null;
        CP2PSubscriberVO loggedInUser = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");
        try {

            if (!BTSLUtil.isNullString(_request.getParameter("btnBack"))) {
                p2pTransferVO.semiFlush();
                forward = "amountPage";
                return forward;
            } else {
                con = OracleUtil.getConnection();
                CP2PLoginDAO _cp2pLoginDAO = new CP2PLoginDAO();
                CP2PSubscriberVO cp2pSubscriberVO = _cp2pLoginDAO.loadCP2PSubscriberDetails(con, loggedInUser.getLoginId(), loggedInUser.getPassword(), BTSLUtil.getBTSLLocale(_request));
                String pin = BTSLUtil.decryptText(cp2pSubscriberVO.getSmsPin());
                if (!p2pTransferVO.getPin().equals(pin)) {
                    this.addActionError(this.getText("cp2p.user.adhoc.recharge.pin.invalid"));
                    forward = ERROR;
                    return forward;
                }
                if (SystemPreferences.HTTPS_ENABLE)
                    httpURLPrefix = "https://";
                MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(PretupsI.GATEWAY_TYPE_WEB);
                if (_log.isDebugEnabled())
                    _log.debug("CP2PRechargeAction", "messageGatewayVO: " + messageGatewayVO);
                if (messageGatewayVO == null) {
                    // throw exception with message no gateway found
                    _log.error("processRecharge", "**************Message Gateway not found in cache**************");
                    throw new BTSLBaseException(this, "processRecharge", "cp2p.recharge.error.sessiondatanotfound", "processRecharge");
                }
                if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus()))
                    throw new BTSLBaseException(this, "processRecharge", "cp2p.recharge.error.messagegatewaynotactive", "processRecharge");
                else if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getRequestGatewayVO().getStatus()))
                    throw new BTSLBaseException(this, "processRecharge", "cp2p.recharge.error.reqmessagegatewaynotactive", "processRecharge");
                String saperator = null;
                if (!BTSLUtil.isNullString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR)))
                    saperator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
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

                msisdnPrefix = PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredIdentificationNumber(loggedInUser.getMsisdn()));
                NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                String[] strArr = null;
                if (networkPrefixVO == null) {
                    strArr = new String[] { loggedInUser.getMsisdn() };
                    throw new BTSLBaseException("CP2PRechargeActionAction", "processRecharge", SelfTopUpErrorCodesI.CHNL_ERROR_RECR_NOTFOUND_RECEIVERNETWORK, 0, strArr, null);
                }
                String networkCode = networkPrefixVO.getNetworkCode();
                String smsInstanceID = null;

                // Changed to handle multiple SMS servers for C2S and P2P on
                // 20/07/06
                if (LoadControllerCache.getNetworkLoadHash() != null && LoadControllerCache.getNetworkLoadHash().containsKey(LoadControllerCache.getInstanceID() + "_" + networkCode))
                    smsInstanceID = ((NetworkLoadVO) (LoadControllerCache.getNetworkLoadHash().get(LoadControllerCache.getInstanceID() + "_" + networkCode))).getC2sInstanceID();
                else {
                    _log.error("CP2PRechargeActionAction", " Not able to get the instance ID for the network=" + networkCode + " where the request for registration needs to be send");
                    throw new BTSLBaseException(this, "cp2pRegistration", "cp2p.recharge.label.unsuccesfulregistration", "cp2pRegistration");
                }
                instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(smsInstanceID + "_" + networkCode + "_" + PretupsI.REQUEST_SOURCE_TYPE_SMS);
                if (instanceLoadVO == null)
                    instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(smsInstanceID + "_" + networkCode + "_" + PretupsI.REQUEST_SOURCE_TYPE_WEB);
                if (instanceLoadVO == null)// Entry for Dummy(used for Apache)
                    instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(smsInstanceID + "_" + networkCode + "_" + PretupsI.REQUEST_SOURCE_TYPE_DUMMY);
                // SenderVO
                // senderVO=SubscriberBL.validateSubscriberDetails(con,filteredMSISDN);
                urlToSend = httpURLPrefix + instanceLoadVO.getHostAddress() + ":" + instanceLoadVO.getHostPort() + Constants.getProperty("CHANNEL_WEB_CP2PSUBSCRIBER_REGISTRATION_SERVLET") + "?MSISDN=";
                urlToSend = urlToSend + loggedInUser.getMsisdn() + "&USERLOGINID=" + loggedInUser.getLoginId() + "&EMAILID=" + loggedInUser.getEmailId() + "&IMEI=" + PretupsI.DEFAULT_P2P_WEB_IMEI;
                urlToSend = urlToSend + "&MESSAGE=" + URLEncoder.encode(PretupsI.SERVICE_TYPE_SELFTOPUP_ADHOCRECHARGE + saperator + p2pTransferVO.getReceiverMsisdn() + saperator + p2pTransferVO.getRechargeAmount() + saperator + p2pTransferVO.getBankName() + saperator + p2pTransferVO.getCardNumber() + saperator + p2pTransferVO.getCardName() + saperator + p2pTransferVO.getCardExpiry() + saperator + p2pTransferVO.getCvv() + saperator + p2pTransferVO.getPin());
                urlToSend = urlToSend + "&REQUEST_GATEWAY_CODE=" + messageGatewayVO.getGatewayCode() + "&REQUEST_GATEWAY_TYPE=" + messageGatewayVO.getGatewayType();
                urlToSend = urlToSend + "&LOGIN=" + messageGatewayVO.getRequestGatewayVO().getLoginID();
                urlToSend = urlToSend + "&PASSWORD=" + msgGWPass + "&SOURCE_TYPE=" + PretupsI.GATEWAY_TYPE_WEB + "&SERVICE_PORT=" + messageGatewayVO.getRequestGatewayVO().getServicePort();

                URL url = null;
                url = new URL(urlToSend);
                if (_log.isDebugEnabled())
                    _log.debug("processRecharge", "URL: =" + url);

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
                    _log.error("processRecharge", e.getMessage());
                    _log.errorTrace("processRecharge: Exception print stack trace: ", e);
                    String arr[] = new String[2];
                    arr[0] = instanceLoadVO.getHostAddress();
                    arr[1] = instanceLoadVO.getHostPort();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRechargeActionAction[processRecharge]", "", "", "", "Exception:" + e.getMessage());
                    throw new BTSLBaseException(this, "processRecharge", "cp2p.recharge.error.connectionfailed", 0, arr, "CP2PRechargeActionAction");
                }
                String responseStr = null;
                String finalResponse = "";
                while ((responseStr = in.readLine()) != null) {
                    finalResponse = finalResponse + responseStr;
                }

                if (_log.isDebugEnabled())
                    _log.debug("processRecharge", "Response: =" + finalResponse);

                if (!BTSLUtil.isNullString(finalResponse)) {

                    _map = BTSLUtil.getStringToHash(finalResponse, "&", "=");
                    finalResponse = URLDecoder.decode((String) _map.get("MESSAGE"), "UTF16");
                    String txn_status = (String) _map.get("TXN_STATUS");
                    if (_log.isDebugEnabled())
                        _log.debug("processRecharge", "message=" + finalResponse + "TXN_STATUS=" + txn_status);
                    // changed by ankit Z on date 3/8/06 for problem as below:
                    // If there is mo mclass in message and message have colon
                    // then previously it remove the message before colon
                    // So now it only remove portion of message before colon if
                    // there is mclass
                    if (!BTSLUtil.isNullString(finalResponse) && finalResponse.indexOf("mclass^") > -1 && finalResponse.indexOf(":") > -1)
                        finalResponse = finalResponse.substring(finalResponse.indexOf(":") + 1);
                    String[] arr = new String[1];
                    arr[0] = URLDecoder.decode(finalResponse, "UTF16");
                    // BTSLMessages btslMessage = new
                    // BTSLMessages("btsl.blank.message",arr,"CP2PRechargeActionAction");
                    BTSLMessages btslMessage = null;
                    if (PretupsI.TXN_STATUS_SUCCESS.equals(txn_status)) {
                        btslMessage = new BTSLMessages("cp2p.recharge.label.succesfulregistration");
                        this.addActionError(this.getText(arr[0]));
                        forward = SUCCESS;
                        return forward;
                    } else// changed by ankit Z on date 3/8/06 for problem of
                          // value loss on error case
                    {
                        btslMessage = new BTSLMessages("cp2p.recharge.label.unsuccesfulrecharge", arr, "processRecharge");
                        this.addActionError(this.getText(arr[0]));
                        forward = ERROR;
                        return forward;
                    }

                } else {
                    forward = ERROR;
                    throw new BTSLBaseException("cp2p.recharge.label.unsuccesfulrecharge", "cp2pRegistration");
                }
            }

        } catch (BTSLBaseException be) {
            /*
             * If exception comes during login we are forwarding to the
             * cp2pLogin.jsp
             * so before fowarding to the cp2pRegistration.jsp we need to set
             * the default locale
             */
            _request.getSession().setAttribute(Globals.LOCALE_KEY, _request.getLocale());
            BTSLBaseException beException = new BTSLBaseException(this, "processRecharge", be.getMessage(), 0, be.getArgs(), "cp2pRegistration");
            _log.error("processRecharge", "Exceptin:e=" + be);
            _log.errorTrace("processRecharge Exception print stack trace: ", be);
            this.addActionError(this.getText(be.getMessage()));
            return ERROR;
        } catch (Exception e) {
            /*
             * If exception comes during login we are forwarding to the
             * cp2pRegistration.jsp
             * so before fowarding to the cp2pRegistration.jsp we need to set
             * the default locale
             */
            _request.getSession().setAttribute(Globals.LOCALE_KEY, _request.getLocale());
            _log.error("processRecharge", "Exceptin:e=" + e);
            _log.errorTrace("processRecharge Exception print stack trace: ", e);
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
            if (!BTSLUtil.isNullString(_request.getParameter("btnBack"))) {
                p2pTransferVO.semiFlush();
            } else {
                p2pTransferVO.flush();
                p2pTransferVO.setReceiverMsisdn(loggedInUser.getMsisdn());
            }
            if (_log.isDebugEnabled())
                _log.debug("processRecharge", "Exiting" + forward);
        }

    }

    public String processRegistredCardRecharge() {
        if (_log.isDebugEnabled())
            _log.debug("processRegistredCardRecharge", "Entered");
        String forward = null;
        HttpSession session = _request.getSession();
        String httpURLPrefix = "http://";
        String urlToSend = null;
        String msisdnPrefix = null;
        InstanceLoadVO instanceLoadVO = null;
        HttpURLConnection _con = null;
        BufferedReader in = null;
        Connection con = null;
        HashMap _map = null;

        CardDetailsDAO dao = null;
        CP2PSubscriberVO loggedInUser = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");
        try {

            if (!BTSLUtil.isNullString(_request.getParameter("btnBack"))) {
                p2pTransferVO.semiFlush();
                forward = "amountPage";
                return forward;
            } else {

                if (BTSLUtil.isNullString(p2pTransferVO.getCvv())) {
                    this.addActionError(this.getText("cp2p.cvv.required"));
                    forward = "cvvInvalid";
                    return forward;
                } else if (!BTSLUtil.isNumeric(p2pTransferVO.getCvv())) {
                    this.addActionError(this.getText("cp2p.cvv.nonnumeric"));
                    forward = "cvvInvalid";
                    return forward;
                } else if (p2pTransferVO.getCvv().length() != 3) {
                    this.addActionError(this.getText("cp2p.cvv.lengtherror"));
                    forward = "cvvInvalid";
                    return forward;
                }

                con = OracleUtil.getConnection();
                CP2PLoginDAO _cp2pLoginDAO = new CP2PLoginDAO();
                CP2PSubscriberVO cp2pSubscriberVO = _cp2pLoginDAO.loadCP2PSubscriberDetails(con, loggedInUser.getLoginId(), loggedInUser.getPassword(), BTSLUtil.getBTSLLocale(_request));
                String pin = BTSLUtil.decryptText(cp2pSubscriberVO.getSmsPin());
                if (!p2pTransferVO.getPin().equals(pin)) {
                    this.addActionError(this.getText("cp2p.user.adhoc.recharge.pin.invalid"));
                    forward = ERROR;
                    return forward;
                }
                if (SystemPreferences.HTTPS_ENABLE)
                    httpURLPrefix = "https://";
                MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(PretupsI.GATEWAY_TYPE_WEB);
                if (_log.isDebugEnabled())
                    _log.debug("CP2PRechargeAction", "messageGatewayVO: " + messageGatewayVO);
                if (messageGatewayVO == null) {
                    // throw exception with message no gateway found
                    _log.error("processRegistredCardRecharge", "**************Message Gateway not found in cache**************");
                    throw new BTSLBaseException(this, "processRegistredCardRecharge", "cp2p.recharge.error.sessiondatanotfound", "processRegistredCardRecharge");
                }
                if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus()))
                    throw new BTSLBaseException(this, "processRegistredCardRecharge", "cp2p.recharge.error.messagegatewaynotactive", "processRegistredCardRecharge");
                else if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getRequestGatewayVO().getStatus()))
                    throw new BTSLBaseException(this, "processRegistredCardRecharge", "cp2p.recharge.error.reqmessagegatewaynotactive", "processRegistredCardRecharge");
                String saperator = null;
                if (!BTSLUtil.isNullString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR)))
                    saperator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
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

                msisdnPrefix = PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredIdentificationNumber(loggedInUser.getMsisdn()));
                NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                String[] strArr = null;
                if (networkPrefixVO == null) {
                    strArr = new String[] { loggedInUser.getMsisdn() };
                    throw new BTSLBaseException("CP2PRechargeActionAction", "processRegistredCardRecharge", SelfTopUpErrorCodesI.CHNL_ERROR_RECR_NOTFOUND_RECEIVERNETWORK, 0, strArr, null);
                }
                String networkCode = networkPrefixVO.getNetworkCode();
                String smsInstanceID = null;

                // Changed to handle multiple SMS servers for C2S and P2P on
                // 20/07/06
                if (LoadControllerCache.getNetworkLoadHash() != null && LoadControllerCache.getNetworkLoadHash().containsKey(LoadControllerCache.getInstanceID() + "_" + networkCode))
                    smsInstanceID = ((NetworkLoadVO) (LoadControllerCache.getNetworkLoadHash().get(LoadControllerCache.getInstanceID() + "_" + networkCode))).getC2sInstanceID();
                else {
                    _log.error("CP2PRechargeActionAction", " Not able to get the instance ID for the network=" + networkCode + " where the request for registration needs to be send");
                    throw new BTSLBaseException(this, "cp2pRegistration", "cp2p.recharge.label.unsuccesfulregistration", "cp2pRegistration");
                }
                instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(smsInstanceID + "_" + networkCode + "_" + PretupsI.REQUEST_SOURCE_TYPE_SMS);
                if (instanceLoadVO == null)
                    instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(smsInstanceID + "_" + networkCode + "_" + PretupsI.REQUEST_SOURCE_TYPE_WEB);
                if (instanceLoadVO == null)// Entry for Dummy(used for Apache)
                    instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(smsInstanceID + "_" + networkCode + "_" + PretupsI.REQUEST_SOURCE_TYPE_DUMMY);
                dao = new CardDetailsDAO();
                // con = OracleUtil.getConnection();
                p2pTransferVO.setCardDetailsVO(dao.loadCredtCardDetails(con, loggedInUser.getUserId(), p2pTransferVO.getNickName()));
                SimpleDateFormat date_format = new SimpleDateFormat("MM/yy");
                if (BTSLUtil.getDifferenceInUtilDates(BTSLUtil.getDateFromDateString(date_format.format(new Date()), "MM/yy"), BTSLUtil.getDateFromDateString(BTSLUtil.decryptText(p2pTransferVO.getCardDetailsVO().getExpiryDate()), "MM/yy")) < 0)
                // if(BTSLUtil.getDifferenceInUtilDates(new Date(),
                // BTSLUtil.getDateFromDateString(BTSLUtil.decryptText(p2pTransferVO.getCardDetailsVO().getExpiryDate()),"MM/yy"))<=0)
                {
                    throw new BTSLBaseException(this, "cp2pRegistration", "cp2p.recharge.expirydate.error", "cp2pRegistration");
                }
                urlToSend = httpURLPrefix + instanceLoadVO.getHostAddress() + ":" + instanceLoadVO.getHostPort() + Constants.getProperty("CHANNEL_WEB_CP2PSUBSCRIBER_REGISTRATION_SERVLET") + "?MSISDN=";
                urlToSend = urlToSend + loggedInUser.getMsisdn() + "&USERLOGINID=" + loggedInUser.getLoginId() + "&EMAILID=" + loggedInUser.getEmailId() + "&IMEI=" + PretupsI.DEFAULT_P2P_WEB_IMEI;
                urlToSend = urlToSend + "&MESSAGE=" + URLEncoder.encode(PretupsI.SERVICE_TYPE_SELFTOPUP_RECHARGE_USING_REGISTERED_CARD + saperator + p2pTransferVO.getReceiverMsisdn() + saperator + p2pTransferVO.getRechargeAmount() + saperator + p2pTransferVO.getBankName() + saperator + BTSLUtil.decryptText(p2pTransferVO.getCardDetailsVO().getCardNumber()) + saperator + p2pTransferVO.getCardDetailsVO().getCardNickName() + saperator + BTSLUtil.decryptText(p2pTransferVO.getCardDetailsVO().getExpiryDate()) + saperator + p2pTransferVO.getCvv() + saperator + p2pTransferVO.getPin());
                urlToSend = urlToSend + "&REQUEST_GATEWAY_CODE=" + messageGatewayVO.getGatewayCode() + "&REQUEST_GATEWAY_TYPE=" + messageGatewayVO.getGatewayType();
                urlToSend = urlToSend + "&LOGIN=" + messageGatewayVO.getRequestGatewayVO().getLoginID();
                urlToSend = urlToSend + "&PASSWORD=" + msgGWPass + "&SOURCE_TYPE=" + PretupsI.GATEWAY_TYPE_WEB + "&SERVICE_PORT=" + messageGatewayVO.getRequestGatewayVO().getServicePort();

                URL url = null;
                url = new URL(urlToSend);
                if (_log.isDebugEnabled())
                    _log.debug("processRegistredCardRecharge", "URL: =" + url);

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
                    _log.error("processRegistredCardRecharge", e.getMessage());
                    _log.errorTrace("processRegistredCardRecharge: Exception print stack trace: ", e);
                    String arr[] = new String[2];
                    arr[0] = instanceLoadVO.getHostAddress();
                    arr[1] = instanceLoadVO.getHostPort();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRechargeActionAction[processRegistredCardRecharge]", "", "", "", "Exception:" + e.getMessage());
                    throw new BTSLBaseException(this, "processRegistredCardRecharge", "cp2p.recharge.error.connectionfailed", 0, arr, "CP2PRechargeActionAction");
                }
                String responseStr = null;
                String finalResponse = "";
                while ((responseStr = in.readLine()) != null) {
                    finalResponse = finalResponse + responseStr;
                }

                if (_log.isDebugEnabled())
                    _log.debug("processRegistredCardRecharge", "Response: =" + finalResponse);

                if (!BTSLUtil.isNullString(finalResponse)) {

                    _map = BTSLUtil.getStringToHash(finalResponse, "&", "=");
                    finalResponse = URLDecoder.decode((String) _map.get("MESSAGE"), "UTF16");
                    String txn_status = (String) _map.get("TXN_STATUS");
                    if (_log.isDebugEnabled())
                        _log.debug("processRegistredCardRecharge", "message=" + finalResponse + "TXN_STATUS=" + txn_status);
                    // changed by ankit Z on date 3/8/06 for problem as below:
                    // If there is mo mclass in message and message have colon
                    // then previously it remove the message before colon
                    // So now it only remove portion of message before colon if
                    // there is mclass
                    if (!BTSLUtil.isNullString(finalResponse) && finalResponse.indexOf("mclass^") > -1 && finalResponse.indexOf(":") > -1)
                        finalResponse = finalResponse.substring(finalResponse.indexOf(":") + 1);
                    String[] arr = new String[1];
                    arr[0] = URLDecoder.decode(finalResponse, "UTF16");
                    // BTSLMessages btslMessage = new
                    // BTSLMessages("btsl.blank.message",arr,"CP2PRechargeActionAction");
                    BTSLMessages btslMessage = null;
                    if (PretupsI.TXN_STATUS_SUCCESS.equals(txn_status)) {
                        btslMessage = new BTSLMessages("cp2p.recharge.label.succesfulregistration");
                        this.addActionError(this.getText(arr[0]));
                        forward = SUCCESS;
                        return forward;
                    } else// changed by ankit Z on date 3/8/06 for problem of
                          // value loss on error case
                    {
                        btslMessage = new BTSLMessages("cp2p.recharge.label.unsuccesfulrecharge", arr, "processRegistredCardRecharge");
                        this.addActionError(this.getText(arr[0]));
                        forward = ERROR;
                        return forward;
                    }

                } else {
                    forward = ERROR;
                    throw new BTSLBaseException("cp2p.recharge.label.unsuccesfulrecharge", "cp2pRegistration");
                }

            }
        } catch (BTSLBaseException be) {
            /*
             * If exception comes during login we are forwarding to the
             * cp2pLogin.jsp
             * so before fowarding to the cp2pRegistration.jsp we need to set
             * the default locale
             */
            _request.getSession().setAttribute(Globals.LOCALE_KEY, _request.getLocale());
            BTSLBaseException beException = new BTSLBaseException(this, "processRegistredCardRecharge", be.getMessage(), 0, be.getArgs(), "cp2pRegistration");
            _log.error("processRegistredCardRecharge", "Exceptin:e=" + be);
            _log.errorTrace("processRegistredCardRecharge: Exception print stack trace: ", be);
            this.addActionError(this.getText(be.getMessage()));
            return ERROR;
        } catch (Exception e) {
            /*
             * If exception comes during login we are forwarding to the
             * cp2pRegistration.jsp
             * so before fowarding to the cp2pRegistration.jsp we need to set
             * the default locale
             */
            _request.getSession().setAttribute(Globals.LOCALE_KEY, _request.getLocale());
            _log.error("processRegistredCardRecharge", "Exceptin:e=" + e);
            _log.errorTrace("processRegistredCardRecharge: Exception print stack trace: ", e);
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
            if (!BTSLUtil.isNullString(_request.getParameter("btnBack"))) {
                p2pTransferVO.semiFlush();
            } else if (!BTSLUtil.isNullString(forward)) {
                if (forward.equals("cvvInvalid")) {
                    p2pTransferVO.semiFlush();
                } else {
                    p2pTransferVO.flush();
                    p2pTransferVO.setReceiverMsisdn(loggedInUser.getMsisdn());
                }
            } else {
                p2pTransferVO.flush();
                p2pTransferVO.setReceiverMsisdn(loggedInUser.getMsisdn());
            }

            if (_log.isDebugEnabled())
                _log.debug("processRegistredCardRecharge", "Exiting" + forward);
        }

    }

    // Recharge without hitting receiver
    /**
     * @return
     * @throws BTSLBaseException
     */
    /*
     * public String processRecharge()throws BTSLBaseException
     * {
     */

    /**
     * This process will take place in three steps
     * STEP 1-
     * 1. Sender will be validated at IN i.e. whether this mobile number is
     * active or not
     * 2.If it is valid only then we will move to next step else error will be
     * sent for sender invalid.
     * 
     * STEP 2-
     * 1. Curl request will hit paypal where user will further input card
     * details.
     * 2.Debit payment will be done on this side.
     * 3. If transaction is successful only then we will move to step 3 else
     * process will terminate here with an error message
     * 
     * STEP 3-
     * 1.For successful transaction at paypal request will be sent at IN.
     * 2.User credit or refill request will be sent.
     * 3.If transaction is successful only then success message will be sent
     * else fail.
     * 
     */
    /*
     * 
     * HttpSession session=_request.getSession();
     * CP2PSubscriberVO loggedInUser = (CP2PSubscriberVO)
     * session.getAttribute("cp2pSubscriberVO");
     * 
     * try
     * {
     * if(!BTSLUtil.isNumeric(p2pTransferVO.getRechargeAmount()))
     * {
     * return ERROR;
     * }
     * ++_requestID;
     * String requestIDStr=String.valueOf(_requestID);
     * return processRequest(requestIDStr, 1);
     * }
     * catch(Exception be)
     * {
     * 
     * }
     * 
     * return SUCCESS;
     * }
     */

    /*
     * private String processRequest(String requestIDStr,int p_requestFrom)
     * throws IOException
     * {
     * BufferedReader in=null;
     * HashMap _map=null;
     * String responseStr=null;
     * String finalResponse="";
     * PrintWriter out=null;
     * Connection con=null;
     * SenderVO senderVO=null;
     * Date currentDate=new Date();
     * NetworkPrefixVO networkPrefixVO=null;
     * RequestVO requestVO=new RequestVO();
     * long requestStartTime=System.currentTimeMillis();
     * long requestEndTime=0;
     * boolean isMarkedUnderprocess=false;
     * String filteredMSISDN=null;
     * GatewayParsersI gatewayParsersObj=null;
     * long requestIDMethod=0;
     * String requestType=null;
     * String networkID=null;
     * String serviceType=null;
     * String externalInterfaceAllowed=null;
     * String requestMessage=null;
     * String message=null;
     * HttpSession session=_request.getSession();
     * CP2PSubscriberVO loggedInUser = (CP2PSubscriberVO)
     * session.getAttribute("cp2pSubscriberVO");
     * try
     * {
     * if(_log.isDebugEnabled())_log.debug("processRequest",requestIDStr,
     * "************Start Time***********="+requestStartTime);
     * requestIDMethod=Long.parseLong(requestIDStr);
     * if(_log.isDebugEnabled())_log.debug("processRequest","Content Type: "+
     * "request.getContentType()");
     * requestVO.setRequestID(requestIDMethod);
     * requestVO.setModule(PretupsI.P2P_MODULE);
     * requestVO.setInstanceID(_instanceCode);
     * requestVO.setCreatedOn(currentDate);
     * requestVO.setLocale(new
     * Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),SystemPreferences
     * .DEFAULT_COUNTRY));
     * requestVO.setDecreaseLoadCounters(false);
     * requestVO.setPlainMessage(true);
     * requestVO.setRequestStartTime(requestStartTime);
     * requestVO.setRequestMSISDN(loggedInUser.getMsisdn());
     * String saperator=null;
     * if(!BTSLUtil.isNullString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR)))
     * saperator=(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
     * else
     * saperator=" ";
     * requestMessage=PretupsI.SERVICE_TYPE_SELFTOPUP_ADHOCRECHARGE+saperator+
     * loggedInUser
     * .getMsisdn()+saperator+p2pTransferVO.getRechargeAmount()+saperator
     * +p2pTransferVO.getBankName()+saperator+
     * p2pTransferVO.getCardNumber()+saperator+p2pTransferVO.getCardName()+saperator
     * +
     * p2pTransferVO.getCardExpiry()+saperator+p2pTransferVO.getCvv()+saperator+
     * BTSLUtil.decryptText(loggedInUser.getSmsPin());
     * requestVO.setRequestMessage(requestMessage);
     * requestVO.setDecryptedMessage(requestMessage);
     * requestVO.setServiceType(PretupsI.SERVICE_TYPE_SELFTOPUP);
     * 
     * //LOAD gateway details
     * MessageGatewayVO
     * messageGatewayVO=MessageGatewayCache.getObject(PretupsI.GATEWAY_TYPE_WEB
     * );
     * if (_log.isDebugEnabled())
     * _log.debug("recharge", "messageGatewayVO: "+messageGatewayVO);
     * if(messageGatewayVO==null)
     * {
     * //throw exception with message no gateway found
     * _log.error("confirmC2SRecharge",
     * "**************Message Gateway not found in cache**************");
     * throw new BTSLBaseException(this, "confirmC2SRecharge",
     * "c2stranfer.c2srecharge.error.sessiondatanotfound","c2sRecharge");
     * }
     * if(!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus()))
     * throw new BTSLBaseException(this, "confirmC2SRecharge",
     * "c2stranfer.c2srecharge.error.messagegatewaynotactive","c2sRecharge");
     * else
     * if(!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getRequestGatewayVO
     * ().getStatus()))
     * throw new BTSLBaseException(this, "confirmC2SRecharge",
     * "c2stranfer.c2srecharge.error.reqmessagegatewaynotactive","c2sRecharge");
     * 
     * parseRequest(requestIDStr,requestVO,messageGatewayVO);
     * requestVO.setMessageGatewayVO(messageGatewayVO);
     * requestType=requestVO.getMessageGatewayVO().getGatewayType();
     * //Forward to handler class to get the request message
     * gatewayParsersObj=(GatewayParsersI)PretupsBL.getGatewayHandlerObj(
     * messageGatewayVO.getHandlerClass());
     * 
     * gatewayParsersObj.parseRequestMessage(requestVO);
     * 
     * String taxClass = (String)
     * PreferenceCache.getSystemPreferenceValue(PreferenceI
     * .OPERATOR_UTIL_CLASS);
     * calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
     * filteredMSISDN=calculatorI.getSystemFilteredMSISDN(requestVO.getRequestMSISDN
     * ());
     * requestVO.setFilteredMSISDN(filteredMSISDN);
     * if(!BTSLUtil.isValidMSISDN(filteredMSISDN))
     * {
     * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.MAJOR,"CP2PRechargeAction[processRequest]",requestIDStr
     * ,filteredMSISDN,"","Sender MSISDN Not valid");
     * requestVO.setSenderMessageRequired(false);
     * throw new BTSLBaseException(this,"processRequest",PretupsErrorCodesI.
     * P2P_ERROR_INVALID_SENDER_MSISDN);
     * }
     * 
     * LoadController.checkInstanceLoad(requestIDMethod,LoadControllerI.
     * INSTANCE_NEW_REQUEST);
     * requestVO.setDecreaseLoadCounters(true);
     * 
     * if(requestVO.getReceiverLocale()==null)
     * requestVO.setReceiverLocale(requestVO.getLocale());
     * 
     * //load network details
     * networkPrefixVO=PretupsBL.getNetworkDetails(filteredMSISDN,PretupsI.
     * USER_TYPE_SENDER);
     * 
     * requestVO.setRequestNetworkCode(networkPrefixVO.getNetworkCode());
     * 
     * //Check Network Load : If true then pass the request else refuse the
     * request
     * LoadController.checkNetworkLoad(requestIDMethod,networkPrefixVO.
     * getNetworkCode(),LoadControllerI.NETWORK_NEW_REQUEST);
     * requestVO.setDecreaseNetworkLoadCounters(true);
     * 
     * networkID=networkPrefixVO.getNetworkCode();
     * 
     * //check network status
     * if(!PretupsI.YES.equals(networkPrefixVO.getStatus()))
     * {
     * //ChangeID=LOCALEMASTER
     * //Set the message based on locale master value for the requested locale
     * LocaleMasterVO localeVO=LocaleMasterCache.getLocaleDetailsFromlocale(new
     * Locale
     * ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
     * if(PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage()))
     * message=networkPrefixVO.getLanguage1Message();
     * else
     * message=networkPrefixVO.getLanguage2Message();
     * requestVO.setSenderReturnMessage(message);
     * throw new BTSLBaseException(this,"processRequest",PretupsErrorCodesI.
     * P2P_NETWORK_NOT_ACTIVE);
     * }
     * 
     * //check network load
     * String requestHandlerClass;
     * con=OracleUtil.getConnection();
     * 
     * 
     * //load subscriber details
     * senderVO=SubscriberBL.validateSubscriberDetails(con,filteredMSISDN);
     * if(senderVO!=null)
     * {
     * //senderVO.setPrefixID(networkPrefixVO.getPrefixID());
     * senderVO.setModifiedBy(senderVO.getUserID());
     * //senderVO.setModifiedOn(currentDate);
     * senderVO.setModule(PretupsI.P2P_MODULE);
     * requestVO.setSenderVO(senderVO);
     * requestVO.setLocale(senderVO.getLocale());
     * String msisdnPrefix=PretupsBL.getMSISDNPrefix(filteredMSISDN);
     * networkPrefixVO =
     * (NetworkPrefixVO)NetworkPrefixCache.getObject(msisdnPrefix,
     * senderVO.getSubscriberType());
     * 
     * //This condition is added to update the prefixID of Subscriber in
     * P2PSubscriber table Date 25/01/08
     * if(senderVO.getPrefixID()!=networkPrefixVO.getPrefixID())
     * {
     * SubscriberBL.updateSubscriberPrefixID(con,senderVO,networkPrefixVO.
     * getPrefixID());
     * }
     * //end of prefixID updation
     * 
     * //check for previous request under process
     * SubscriberBL.checkRequestUnderProcess(con,requestIDStr,senderVO,true);
     * isMarkedUnderprocess=true;
     * con.commit();
     * 
     * try{con.close();}catch(Exception e){}
     * con=null;
     * 
     * }
     * 
     * con.close();
     * con=null;
     * 
     * if(_log.isDebugEnabled())_log.debug("processRequest",requestIDStr,
     * "Sender Locale in Request if any"+requestVO.getSenderLocale());
     * 
     * if(requestVO.getSenderLocale()!=null)
     * requestVO.setLocale(requestVO.getSenderLocale());
     * else
     * requestVO.setSenderLocale(requestVO.getLocale());
     * 
     * 
     * //message encryption check
     * //parse message
     * String []
     * messageArray=PretupsBL.parsePlainMessage(requestVO.getDecryptedMessage
     * ());
     * requestVO.setRequestMessageArray(messageArray);
     * //load service details, CR 000009 Sub Keyword Based Service Type
     * identification
     * ServiceKeywordCacheVO
     * serviceKeywordCacheVO=ServiceKeywordCache.getServiceKeywordObj
     * (requestVO);
     * 
     * if(serviceKeywordCacheVO==null)
     * {
     * //return with error message
     * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.INFO,"CP2PRechargeAction[processRequest]",requestIDStr
     * ,
     * filteredMSISDN,"","Service keyword not found for the keyword="+messageArray
     * [
     * 0]+" For Gateway Type="+requestVO.getRequestGatewayType()+"Service Port="
     * +requestVO.getServicePort());
     * throw new
     * BTSLBaseException("CP2PRechargeAction","processRequest",PretupsErrorCodesI
     * .ERROR_NOTFOUND_SERVICEKEYWORD);
     * }
     * else if(serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND))
     * {
     * serviceType=serviceKeywordCacheVO.getServiceType();
     * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.INFO,"CP2PRechargeAction[processRequest]",requestIDStr
     * ,
     * filteredMSISDN,"","Service keyword suspended for the keyword="+messageArray
     * [
     * 0]+" For Gateway Type="+requestVO.getRequestGatewayType()+"Service Port="
     * +requestVO.getServicePort());
     * throw new
     * BTSLBaseException("CP2PRechargeAction","processRequest",PretupsErrorCodesI
     * .P2P_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
     * }
     * serviceType=serviceKeywordCacheVO.getServiceType();
     * if(senderVO==null)
     * {
     * //if service type is not reqistration
     * //if(! (serviceKeywordCacheVO.getServiceType().equals(PretupsI.
     * SERVICE_TYPE_REGISTERATION) ||
     * serviceKeywordCacheVO.getServiceType().equals
     * (PretupsI.SERVICE_TYPE_BARRED) ))
     * if(serviceKeywordCacheVO.getUnregisteredAccessAllowed().equals(PretupsI.NO
     * ))
     * throw new
     * BTSLBaseException("CP2PRechargeAction","processRequest",PretupsErrorCodesI
     * .ERROR_NOTFOUND_SUBSCRIBER);
     * }
     * else if((serviceKeywordCacheVO.getServiceType().equals(PretupsI.
     * SERVICE_TYPE_P2PSUSPEND)) &&
     * senderVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND))
     * {
     * throw new BTSLBaseException("CP2PRechargeAction", "processRequest",
     * PretupsErrorCodesI.P2P_USER_STATUS_ALREADY_SUSPENDED);
     * }
     * else if((!serviceKeywordCacheVO.getServiceType().equals(PretupsI.
     * SERVICE_TYPE_RESUMESERVICE)) &&
     * senderVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND))
     * {
     * throw new BTSLBaseException("CP2PRechargeAction", "processRequest",
     * PretupsErrorCodesI.P2P_ERROR_SENDER_SUSPEND);
     * }
     * else if((!serviceKeywordCacheVO.getServiceType().equals(PretupsI.
     * MULT_CRE_TRA_DED_ACC))&&
     * (!serviceKeywordCacheVO.getServiceType().equals(PretupsI
     * .SERVICE_TYPE_P2PRECHARGE))&&
     * (!serviceKeywordCacheVO.getServiceType().equals
     * (PretupsI.SERVICE_TYPE_P2PCREDITRECHARGE)) &&
     * (!serviceKeywordCacheVO.getServiceType
     * ().equals(PretupsI.SERVICE_TYPE_ACCOUNTINFO)) &&
     * (!serviceKeywordCacheVO.getServiceType
     * ().equals(PretupsI.SERVICE_TYPE_P2PCHANGEPIN)) &&
     * (!serviceKeywordCacheVO.
     * getServiceType().equals(PretupsI.SERVICE_TYPE_REGISTERATION)) &&
     * (!serviceKeywordCacheVO
     * .getServiceType().equals(PretupsI.SERVICE_TYPE_P2PRECHARGEWITHVALEXT)) &&
     * senderVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_NEW))
     * {
     * throw new BTSLBaseException("CP2PRechargeAction", "processRequest",
     * PretupsErrorCodesI.P2P_ERROR_SENDER_STATUS_NEW);
     * }
     * requestHandlerClass=serviceKeywordCacheVO.getRequestHandlerClass();
     * requestVO.setServiceType(serviceKeywordCacheVO.getServiceType());
     * requestVO.setType(serviceKeywordCacheVO.getType());
     * requestVO.setActualMessageFormat(serviceKeywordCacheVO.getMessageFormat())
     * ;
     * requestVO.setUseInterfaceLanguage(serviceKeywordCacheVO.
     * getUseInterfaceLanguage());
     * externalInterfaceAllowed=serviceKeywordCacheVO.getExternalInterface();
     * 
     * //call process of controller
     * ServiceKeywordControllerI
     * controllerObj=(ServiceKeywordControllerI)PretupsBL
     * .getServiceKeywordHandlerObj(requestHandlerClass);
     * controllerObj.process(requestVO);
     * 
     * }
     * catch(BTSLBaseException be)
     * {
     * requestVO.setSuccessTxn(false);
     * if(_log.isDebugEnabled())_log.debug("processRequest",requestIDStr,
     * "BTSLBaseException be:"+be.getMessage());
     * if(!BTSLUtil.isNullString(requestVO.getSenderReturnMessage()))
     * message=requestVO.getSenderReturnMessage();
     * if(be.isKey())
     * {
     * requestVO.setMessageCode(be.getMessageKey());
     * requestVO.setMessageArguments(be.getArgs());
     * }
     * else
     * requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
     * //construct Message
     * this.addActionError(this.getText(be.getMessageKey()));
     * return ERROR;
     * }
     * 
     * catch(Exception e)
     * {
     * requestVO.setSuccessTxn(false);
     * _log.error("processRequest",requestIDStr,"Exception e:"+e.getMessage());
     * e.printStackTrace();
     * requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"CP2PRechargeAction[processRequest]",requestIDStr
     * ,"","","Exception in CP2PRechargeAction:"+e.getMessage());
     * this.addActionError("Exception occured");
     * return ERROR;
     * }
     * finally
     * {
     * 
     * try
     * {
     * 
     * if( con==null && ( senderVO!=null ||
     * "Y".equals(Constants.getProperty("LOAD_TEST"))) )
     * con=OracleUtil.getConnection();
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"SelfTopUpReceiver[processrequest]",requestIDStr
     * ,"","","Exception in SelfTopUpReceiver while getting connection :"+e.
     * getMessage());
     * }
     * 
     * if(senderVO!=null && isMarkedUnderprocess &&
     * requestVO.isUnmarkSenderRequired() && con!=null)
     * {
     * try
     * {
     * //If need to bar the user for PIN Change
     * if(_log.isDebugEnabled())_log.debug("processRequest",requestIDStr,
     * "User Barring required because of PIN change......"
     * +senderVO.isBarUserForInvalidPin());
     * if(senderVO.isBarUserForInvalidPin())
     * {
     * SubscriberBL.barSenderMSISDN(con,senderVO,PretupsI.BARRED_TYPE_PIN_INVALID
     * ,currentDate);
     * con.commit();
     * }
     * }
     * catch(BTSLBaseException be)
     * {
     * _log.error("processRequest",requestIDStr,"BTSLBaseException be: "+be.
     * getMessage());
     * try{con.rollback();} catch(Exception e){}
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * try{con.rollback();} catch(Exception ex){}
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"SelfTopUpReceiver[processRequest]",requestIDStr
     * ,"","","Exception in SelfTopUpReceiver:"+e.getMessage());
     * }
     * try
     * {
     * SubscriberBL.checkRequestUnderProcess(con,requestIDStr,senderVO,false);
     * con.commit();
     * }
     * catch(BTSLBaseException be)
     * {
     * _log.error("processRequest",requestIDStr,"BTSLBaseException be:"+be.
     * getMessage());
     * try{con.rollback();} catch(Exception e){}
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * try{con.rollback();} catch(Exception ex){}
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"SelfTopUpReceiver[processRequest]",requestIDStr
     * ,"","","Exception in SelfTopUpReceiver:"+e.getMessage());
     * }
     * }
     * 
     * if(con!=null)
     * {
     * if(Constants.getProperty("LOAD_TEST")!=null
     * &&"Y".equals(Constants.getProperty("LOAD_TEST")))
     * {
     * try
     * {
     * //Done so that whatever the above transaction has done will be closed by
     * the above code
     * //or else if above some exception is there it will be rollbacked
     * con.rollback();
     * SubscriberTransferDAO subscriberTransferDAO=new SubscriberTransferDAO();
     * subscriberTransferDAO.addP2PReceiverRequests(con,requestVO);
     * con.commit();
     * }
     * catch(Exception
     * ex){ex.printStackTrace();try{con.rollback();}catch(Exception e){}}
     * }
     * 
     * try{con.close();}catch(Exception e){e.printStackTrace();}
     * con=null;
     * }
     * 
     * //Decrease the counters only when it is required
     * if(requestVO.isDecreaseLoadCounters())
     * {
     * if(requestVO.isDecreaseNetworkLoadCounters())
     * LoadController.decreaseCurrentNetworkLoad(requestIDMethod,networkPrefixVO.
     * getNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);
     * LoadController.decreaseCurrentInstanceLoad(requestIDMethod,LoadControllerI
     * .DEC_LAST_TRANS_COUNT);
     * }
     * //return message to sender
     * //pass default locale also
     * requestEndTime=System.currentTimeMillis();
     * 
     * //For increaseing the counters in network and service type
     * ReqNetworkServiceLoadController.increaseIntermediateCounters(_instanceCode
     * ,requestType,networkID,serviceType,requestIDStr,LoadControllerI.
     * COUNTER_NEW_REQUEST
     * ,requestStartTime,requestEndTime,requestVO.isSuccessTxn
     * (),requestVO.isDecreaseLoadCounters());
     * 
     * if(requestVO.getMessageGatewayVO()!=null)
     * if(_log.isDebugEnabled())
     * _log.debug(this,requestIDStr,"requestVO.getMessageGatewayVO()="
     * +requestVO.getMessageGatewayVO().getTimeoutValue());
     * 
     * //Forward to handler class to get the request message
     * if(gatewayParsersObj==null)
     * {
     * //Will be changed after discussion with ABHIJIT
     * try
     * {
     * gatewayParsersObj=(GatewayParsersI)PretupsBL.getGatewayHandlerObj(requestVO
     * .getMessageGatewayVO().getHandlerClass());
     * }
     * catch(Exception e)
     * {
     * 
     * }
     * }
     * if(_log.isDebugEnabled())
     * _log.debug(this,requestIDStr,"gatewayParsersObj="+gatewayParsersObj);
     * if(gatewayParsersObj!=null)
     * gatewayParsersObj.generateResponseMessage(requestVO);
     * else
     * {
     * 
     * if(!BTSLUtil.isNullString(requestVO.getSenderReturnMessage()))
     * message=requestVO.getSenderReturnMessage();
     * else
     * message=BTSLUtil.getMessage(requestVO.getLocale(),requestVO.getMessageCode
     * (),requestVO.getMessageArguments());
     * requestVO.setSenderReturnMessage(message);
     * 
     * }
     * 
     * try
     * {
     * String reqruestGW=requestVO.getRequestGatewayCode();
     * String altrnetGW=BTSLUtil.NullToString(Constants.getProperty(
     * "P2P_REC_MSG_REQD_BY_ALT_GW"));
     * if(!BTSLUtil.isNullString(altrnetGW)&& (altrnetGW.split(":")).length>=2)
     * {
     * if(reqruestGW.equalsIgnoreCase(altrnetGW.split(":")[0]))
     * {
     * reqruestGW=(altrnetGW.split(":")[1]).trim();
     * if(_log.isDebugEnabled())
     * _log.debug("processRequest: Sender Message push through alternate GW"
     * ,reqruestGW,"Requested GW was:"+requestVO.getRequestGatewayCode());
     * }
     * }
     * int messageLength=0;
     * String
     * messLength=BTSLUtil.NullToString(Constants.getProperty("MSG_LENGTH_GW"));
     * if(!BTSLUtil.isNullString(messLength))
     * messageLength=(new Integer(messLength)).intValue();
     * 
     * //if(!(!BTSLUtil.isNullString(requestVO.getReqContentType()) &&
     * (requestVO.getReqContentType().indexOf("xml")!=-1 ||
     * requestVO.getReqContentType().indexOf("XML")!=-1)))
     * if(!(!BTSLUtil.isNullString(requestVO.getReqContentType()) &&
     * (requestVO.getReqContentType().indexOf("xml")!=-1 ||
     * requestVO.getReqContentType().indexOf("XML")!=-1))&&
     * !PretupsI.GATEWAY_TYPE_USSD
     * .equals(requestVO.getRequestGatewayType()))//@@
     * {
     * String txn_status=null;
     * if(requestVO.isSuccessTxn())
     * txn_status=PretupsI.TXN_STATUS_SUCCESS;
     * else
     * txn_status=requestVO.getMessageCode();
     * if(! reqruestGW.equalsIgnoreCase(requestVO.getRequestGatewayCode()) ||
     * !reqruestGW.equalsIgnoreCase("WEB") )
     * {
     * message=requestVO.getSenderReturnMessage();
     * String message1=null;
     * if((messageLength>0)&& (message.length()>messageLength))
     * {
     * message1=BTSLUtil.getMessage(requestVO.getLocale(),PretupsErrorCodesI.
     * REQUEST_IN_QUEUE_UB,requestVO.getMessageArguments());
     * PushMessage pushMessage1=new
     * PushMessage(requestVO.getFilteredMSISDN(),message1
     * ,requestVO.getRequestIDStr
     * (),requestVO.getRequestGatewayCode(),requestVO.getLocale());
     * pushMessage1.push();
     * requestVO.setRequestGatewayCode(reqruestGW);
     * }
     * }
     * else
     * message="MESSAGE="+URLEncoder.encode(requestVO.getSenderReturnMessage(),
     * "UTF16")+"&TXN_ID="+BTSLUtil.NullToString(requestVO.getTransactionID())+
     * "&TXN_STATUS="+BTSLUtil.NullToString(txn_status);
     * }
     * else
     * message=requestVO.getSenderReturnMessage();
     * }
     * catch(Exception e){e.printStackTrace();}
     * // End of Rajdeep's code
     * //message=requestVO.getSenderReturnMessage();
     * 
     * if(_log.isDebugEnabled())
     * _log.debug(this,requestIDStr,"Locale="+requestVO
     * .getLocale()+" requestEndTime="
     * +requestEndTime+" requestStartTime="+requestStartTime
     * +" Message Code="+requestVO
     * .getMessageCode()+" Args="+requestVO.getMessageArguments
     * ()+" Message If any="+message);
     * if(requestVO.getMessageGatewayVO()==null
     * ||requestVO.getMessageGatewayVO()
     * .getResponseType().equalsIgnoreCase(PretupsI
     * .MSG_GATEWAY_RESPONSE_TYPE_RESPONSE) ||
     * (requestEndTime-requestStartTime)/
     * 1000<requestVO.getMessageGatewayVO().getTimeoutValue())
     * {
     * requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE);
     * out.println(message);
     * 
     * if(!BTSLUtil.isNullString(requestVO.getReqContentType()) &&
     * requestVO.isSuccessTxn() &&
     * (requestVO.getReqContentType().indexOf("xml")!=-1 ||
     * requestVO.getReqContentType().indexOf("XML")!=-1) &&
     * requestVO.isSenderMessageRequired())
     * {
     * if(requestVO.isPushMessage() &&
     * !PretupsI.YES.equals(externalInterfaceAllowed))
     * {
     * String senderMessage=BTSLUtil.getMessage(requestVO.getLocale(),requestVO.
     * getMessageCode(),requestVO.getMessageArguments());
     * 
     * PushMessage pushMessage=null;
     * pushMessage=new
     * PushMessage(requestVO.getFilteredMSISDN(),senderMessage,requestVO
     * .getRequestIDStr
     * (),requestVO.getRequestGatewayCode(),requestVO.getLocale());
     * pushMessage.push();
     * }
     * }
     * 
     * //return message to sender
     * 
     * if(message!=null)
     * out.println(message);
     * else
     * out.println(BTSLUtil.getMessage(requestVO.getLocale(),requestVO.
     * getMessageCode(),requestVO.getMessageArguments()));
     * 
     * }
     * else
     * {
     * requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_PUSH);
     * //Will be removed in the future: For testing
     * //out.println(message);
     * 
     * if(message!=null)
     * out.println(message);
     * else
     * out.println(BTSLUtil.getMessage(requestVO.getLocale(),requestVO.
     * getMessageCode(),requestVO.getMessageArguments()));
     * 
     * if(requestVO.isSenderMessageRequired())
     * {
     * PushMessage pushMessage=null;
     * pushMessage=new
     * PushMessage(requestVO.getFilteredMSISDN(),message,requestVO
     * .getRequestIDStr
     * (),requestVO.getRequestGatewayCode(),requestVO.getLocale());
     * pushMessage.push();
     * }
     * 
     * if(message!=null)
     * pushMessage=new
     * PushMessage(requestVO.getFilteredMSISDN(),message,requestVO
     * .getRequestIDStr
     * (),requestVO.getRequestGatewayCode(),requestVO.getLocale());
     * else
     * pushMessage=new
     * PushMessage(requestVO.getFilteredMSISDN(),BTSLUtil.getMessage
     * (requestVO.getLocale
     * (),requestVO.getMessageCode(),requestVO.getMessageArguments
     * ()),requestVO.getRequestIDStr
     * (),requestVO.getRequestGatewayCode(),requestVO.getLocale());
     * 
     * }
     * 
     * out.flush();
     * out.close();
     * //Log the request in Request Logger
     * P2PGatewayRequestLog.log(requestVO);
     * if(_log.isDebugEnabled())_log.debug("processRequest",requestIDStr,"Exiting"
     * );
     * 
     * }
     * return SUCCESS;
     * }
     *//**
     * Parse Request and retreives details
     * 
     * @param requestID
     * @param request
     * @param p_requestVO
     * @return
     * @throws BTSLBaseException
     */
    /*
     * public void parseRequest(String requestID,RequestVO
     * p_requestVO,MessageGatewayVO p_messageGatewayVO) throws BTSLBaseException
     * {
     * if(_log.isDebugEnabled())_log.debug("parseRequest",requestID,"Entered");
     * 
     * String requestGatewayCode =
     * p_messageGatewayVO.getRequestGatewayVO().getGatewayCode();
     * String requestGatewayType = PretupsI.GATEWAY_TYPE_WEB;
     * String servicePort =
     * p_messageGatewayVO.getRequestGatewayVO().getServicePort();
     * String login = p_messageGatewayVO.getRequestGatewayVO().getLoginID();
     * String password =
     * BTSLUtil.decryptText(p_messageGatewayVO.getRequestGatewayVO
     * ().getPassword());
     * String sourceType =PretupsI.GATEWAY_TYPE_WEB;
     * 
     * if(_log.isDebugEnabled())_log.debug("parseRequest",requestID,
     * "requestGatewayCode: "
     * +requestGatewayCode+" requestGatewayType: "+requestGatewayType
     * +" servicePort: "
     * +servicePort+" login: "+login+" password: "+password+" sourceType: "
     * +sourceType);
     * if (BTSLUtil.isNullString(requestGatewayCode))
     * {
     * throw new BTSLBaseException("CP2PRechargeAction", "parseRequest",
     * PretupsErrorCodesI.P2P_ERROR_BLANK_REQUESTINTID);
     * }
     * else
     * requestGatewayCode = requestGatewayCode.trim();
     * p_requestVO.setRequestGatewayCode(requestGatewayCode);
     * 
     * if (BTSLUtil.isNullString(requestGatewayType))
     * {
     * throw new BTSLBaseException("CP2PRechargeAction", "parseRequest",
     * PretupsErrorCodesI.P2P_ERROR_BLANK_REQUESTINTTYPE);
     * }
     * else
     * requestGatewayType = requestGatewayType.trim();
     * p_requestVO.setRequestGatewayType(requestGatewayType);
     * 
     * p_requestVO.setLogin(login);
     * p_requestVO.setPassword(password);
     * p_requestVO.setServicePort(servicePort);
     * p_requestVO.setSourceType(sourceType);
     * 
     * if(_log.isDebugEnabled())_log.debug("parseRequest",requestID,"Exiting");
     * }
     */

}
