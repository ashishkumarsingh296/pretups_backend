package com.selftopup.cp2p.cardregistration.web;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import jakarta.servlet.http.HttpSession;

import org.apache.struts2.dispatcher.mapper.ActionMapping;

import com.opensymphony.xwork2.interceptor.ScopedModelDriven;
import com.selftopup.common.BTSLActionSupport;
import com.selftopup.common.BTSLBaseException;
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
import com.selftopup.pretups.p2p.transfer.businesslogic.CardDetailsDAO;
import com.selftopup.pretups.p2p.transfer.businesslogic.CardDetailsVO;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.util.OperatorUtilI;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;
import com.selftopup.util.UtilValidate;

/**
 * @(#)CardRegistrationAction.java Copyright(c) 2005, Bharti Telesoft Ltd. All
 *                                 Rights Reserved
 * 
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Shashi Ranjan 27/04/14 Initial Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 * 
 *                                 This class is used for Card Details
 *                                 Add/Modification/Delete and View Credit Card
 *                                 Details.
 * 
 */
public class CardRegistrationAction extends BTSLActionSupport implements ScopedModelDriven<CardDetailsVO> {

    private static final long serialVersionUID = 1L;
    public Log _log = LogFactory.getLog(this.getClass().getName());

    private CardDetailsVO cardDetailsVO;
    private String MODEL_SESSION_KEY;
    String _nameEmbossing = null;
    String _cardNo = null;
    String _expiryDate = null;
    String _nickName = null;
    String _bank = null;
    String _cardType = null;
    String _encryptedName = null;
    String _encCardNo = null;
    String _encExpDate = null;
    String _cardNumber = null;
    String _btnBack = null;

    public CardDetailsVO getModel() {
        return cardDetailsVO;
    }

    public String getScopeKey() {
        return MODEL_SESSION_KEY;
    }

    public void setModel(CardDetailsVO arg0) {
        this.cardDetailsVO = (CardDetailsVO) arg0;
    }

    public void setScopeKey(String arg0) {
        MODEL_SESSION_KEY = arg0;
    }

    public CardDetailsVO getCardDetailsVO() {
        return cardDetailsVO;
    }

    public void setCardDetailsVO(CardDetailsVO cardDetailsVO) {
        this.cardDetailsVO = cardDetailsVO;
    }

    public static OperatorUtilI calculatorI = null;
    static {
        String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BuddyMgtAction", "", "", "", "Exception while loading the operator util class at the addBuddyInfo:" + e.getMessage());
        }
    }

    /*
     * Method to load the card registration page - CardRegistration.jsp
     */
    public String loadFirstPage() {
        if (_log.isDebugEnabled()) {
            _log.debug("loadFirstPage", "Entered");
        }
        String returnStr = null;

        Connection con = null;
        HttpSession session = _request.getSession();
        CP2PSubscriberVO loggedInUser = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");
        try {
            if (_request.getParameter("btnTnC") == null) {
                cardDetailsVO.flush();
            }
            CardDetailsVO cardDetailsVO = this.cardDetailsVO;
            con = OracleUtil.getConnection();
            cardDetailsVO.setMsisdn(loggedInUser.getMsisdn());
            cardDetailsVO.setUserId(loggedInUser.getUserId());
            this.getMonthYearList(cardDetailsVO);

            returnStr = "addCardDetails";

        } catch (Exception e) {
            _log.errorTrace("loadFirstPage: Exception print stack trace: ", e);
            this.addActionError(this.getText("error.general.processing"));
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException sqle) {
                _log.errorTrace("loadFirstPage: Exception print stack trace: ", sqle);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadFirstPage", "Exiting" + returnStr);
            }
        }

        return returnStr;
    }

    /*
     * Method to add card detail forward to confirm page
     */
    public String addCreditCardDetails() {

        if (_log.isDebugEnabled()) {
            _log.debug("addCreditCardDetails", "Entered");
        }

        String returnStr = null;
        CardDetailsVO cardDetailsVO = this.cardDetailsVO;

        if (!BTSLUtil.isNullString(_request.getParameter("btnReset"))) {
            cardDetailsVO.flush();
            returnStr = "addCardDetails";
        } else if (!BTSLUtil.isNullString(_request.getParameter("btnAdd"))) {

            CardDetailsDAO dao = null;
            boolean cardNumberExist = false;
            boolean nickNameExist = false;
            Connection con = null;
            String cardType = null;
            try {
                con = OracleUtil.getConnection();

                dao = new CardDetailsDAO();
                cardNumberExist = dao.isCardNumberAlreadyRegistered(con, BTSLUtil.encryptText(cardDetailsVO.getCardNumber()));
                if (cardNumberExist) {
                    throw new BTSLBaseException(this, "addCreditCardDetails", "cp2p.user.creditcard.registration.cardnumber.alreadyexist", "addCardDetails");
                }
                nickNameExist = dao.isNickNameAlreadyRegistered(con, cardDetailsVO.getCardNickName().toUpperCase(), cardDetailsVO.getMsisdn());
                if (nickNameExist) {
                    throw new BTSLBaseException(this, "addCreditCardDetails", "cp2p.user.creditcard.registration.nickname.alreadyexist", "addCardDetails");
                }
                cardType = UtilValidate.getCardType(cardDetailsVO.getCardNumber());
                cardDetailsVO.setCardType(cardType);
                if ("Unknown".equalsIgnoreCase(cardType)) {
                    throw new BTSLBaseException(this, "addCreditCardDetails", "cp2p.user.creditcard.registration.unknowntype", "addCardDetails");
                }
                // we're having only those card registered with the system which
                // are allowed to do the transaction from the cards like VISA,
                // MASTER, AM-EX
                if (cardDetailsVO.getCardNumber().length() != 16) {
                    if (cardDetailsVO.getCardNumber().length() != 15)
                        throw new BTSLBaseException(this, "addCreditCardDetails", "cp2p.user.creditcard.registration.unknowntype", "addCardDetails");
                }

                if (cardDetailsVO.getAcceptTC().equals(true)) {
                    cardDetailsVO.setAcceptTC("Y");
                }
                returnStr = "confirmCard";

            } catch (BTSLBaseException be) {
                _log.error("addCreditCardDetails", "Exception: " + be.getMessage());
                _log.errorTrace("addCreditCardDetails: Exception print stack trace: ", be);
                this.addActionError(this.getText(be.getMessage()));
                return ERROR;
            } catch (Exception e) {
                _log.error("addCreditCardDetails", "Exception: " + e.getMessage());
                _log.errorTrace("addCreditCardDetails: Exception print stack trace: ", e);
                this.addActionError(this.getText("error.general.processing"));
                return ERROR;
            } finally {
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException sqle) {
                    _log.errorTrace("addCreditCardDetails: Exception print stack trace: ", sqle);
                }

                if (_log.isDebugEnabled()) {
                    _log.debug("addCreditCardDetails", "Exiting" + returnStr);
                }
            }
        }

        return returnStr;
    }

    /*
     * Method to add Card Details to Card Details table in database
     */

    public String confirmCreditCardDetails() {

        if (_log.isDebugEnabled()) {
            _log.debug("confirmCreditCardDetails", "Entered");
        }

        String returnStr = null;
        CardDetailsVO cardDetailsVO = this.cardDetailsVO;

        if (!BTSLUtil.isNullString(_request.getParameter("btnBack"))) {
            returnStr = "addCardDetails";
        } else if (!BTSLUtil.isNullString(_request.getParameter("btnConfirm"))) {
            CardDetailsVO cardVO = null;
            CardDetailsDAO dao = null;
            Connection con = null;
            ArrayList<CardDetailsVO> cardDetailList = null;
            HttpSession session = _request.getSession();
            CP2PSubscriberVO subsVO = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");
            int addCount = -1;
            try {
                con = OracleUtil.getConnection();
                dao = new CardDetailsDAO();
                cardVO = this.cardDetailsVO;

                cardVO.setExpiryDate(cardVO.getMonthExpiry() + cardVO.getYearExpiry());

                _nameEmbossing = cardVO.getNameOfEmbossing();
                _cardNo = cardVO.getCardNumber();
                _expiryDate = cardVO.getExpiryDate().substring(0, 2) + "/" + cardVO.getExpiryDate().substring(2, 4);
                _nickName = cardVO.getCardNickName().toUpperCase();
                _bank = cardVO.getBankName();

                encryptCardDetails();

                cardDetailsVO.setCardNumber(_encCardNo);
                cardDetailsVO.setNameOfEmbossing(_encryptedName);
                cardDetailsVO.setExpiryDate(_encExpDate);
                cardDetailsVO.setCardNickName(_nickName);
                if (PretupsI.YES.equalsIgnoreCase("Y")) {
                    addCount = dao.addCardDetails(con, cardDetailsVO, true);
                } else {
                    addCount = dao.addCardDetails(con, cardDetailsVO, false);
                }

                if (addCount > 0) {
                    // String args[] = new
                    // String[]{cardDetailsVO.getCardNickName()};
                    try {
                        (new PushMessage(subsVO.getMsisdn(), BTSLUtil.getMessage(new Locale(subsVO.getLanguage(), subsVO.getCountry()), SelfTopUpErrorCodesI.CARD_DETAILS_INSERTION_SUCCESSFUL, null), "", PretupsI.REQUEST_SOURCE_TYPE_WEB, new Locale(subsVO.getLanguage(), subsVO.getCountry()))).push();
                    } catch (Exception e) {
                        _log.errorTrace("confirmCreditCardDetails: Exception print stack trace: ", e);
                    }
                    try {
                        if (con != null) {
                            con.commit();
                        }

                    } catch (Exception e) {
                        _log.errorTrace("confirmCreditCardDetails: Exception print stack trace: ", e);
                        this.addActionError(this.getText("error.general.processing"));
                    }
                    this.addActionError(this.getText("Your card has been registered."));

                    cardDetailsVO.flush();

                    cardDetailList = dao.loadSubscriberRegisteredCardDetails(con, subsVO.getUserId());
                    cardDetailsVO.setCardDetailsList(cardDetailList);

                    returnStr = "cardDetails";
                } else {
                    throw new BTSLBaseException(this, "confirmCreditCardDetails", "cp2p.user.creditcard.registration.failed", "defaultCard");
                }

            } catch (BTSLBaseException be) {
                _log.error("confirmCreditCardDetails", "Exception: " + be.getMessage());
                this.addActionError(this.getText(be.getMessage()));
                return ERROR;
            } catch (Exception e) {
                _log.error("confirmCreditCardDetails", "Exception: " + e.getMessage());
                _log.errorTrace("confirmCreditCardDetails: Exception print stack trace: ", e);
                this.addActionError(this.getText("error.general.processing"));
                return ERROR;
            } finally {
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException sqle) {
                    _log.errorTrace("addCreditCardDetails: Exception print stack trace: ", sqle);
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("confirmCreditCardDetails", "Exiting" + returnStr);
                }
            }
        }
        return returnStr;
    }

    /*
     * Method to load expiry month and year
     */

    private void getMonthYearList(CardDetailsVO p_cardDetailsVO) {

        if (_log.isDebugEnabled()) {
            _log.debug("getMonthYearList", "Entered");
        }

        ArrayList monthList = new ArrayList();
        ArrayList yearList = new ArrayList();
        ArrayList cardTypeList = new ArrayList();

        for (int i = 1; i <= 12; i++) {
            String monthValue = "";
            if (i < 10) {
                monthValue = String.valueOf("0" + i);
            } else {
                monthValue = String.valueOf(i);
            }
            monthList.add(monthValue);
        }
        java.util.Calendar getRight = GregorianCalendar.getInstance();
        String yearAsString = String.valueOf(getRight.get(Calendar.YEAR)).substring(2);
        int year = Integer.parseInt(yearAsString);

        for (int i = year; i <= year + 20; i++)

        {
            String yearValue = String.valueOf(i);
            yearList.add(yearValue);
        }
        p_cardDetailsVO.setMonthList(monthList);
        p_cardDetailsVO.setYearList(yearList);
        cardTypeList.add("VISA");
        cardTypeList.add("MASTERCARD");
        cardTypeList.add("AMERICANEXPRESS");
        cardTypeList.add("DISCOVER");
        p_cardDetailsVO.setCardTypeList(cardTypeList);

        if (_log.isDebugEnabled()) {
            _log.debug("getMonthYearList", "Exited");
        }
    }

    /*
     * Method to load terms and condition jsp
     */

    public String loadTermsConditions() {

        String forward = null;

        if (_log.isDebugEnabled()) {
            _log.debug("loadTermsConditions", "Entered");
            forward = "openTermsCondition";
        }

        if (_log.isDebugEnabled()) {
            _log.debug("loadTermsConditions", "Exited");
        }

        return forward;
    }

    /*
     * Method for card details attribute's validation.
     */

    public void validate() {
        CardDetailsVO cardDetailsVO = this.cardDetailsVO;
        ActionMapping attribute = (ActionMapping) _request.getAttribute("struts.actionMapping");
        if (attribute.getName().equals("cp2pcardmodification_viewModifyDeleteCard")) {
            return;
        }

        if (_request.getServletPath().equals("/cp2pcardregistration/cp2pcardmodification_addCreditCardDetails.action")) {

            if (!BTSLUtil.isNullString(cardDetailsVO.getCardNumber())) {
                int length = cardDetailsVO.getCardNumber().length();
                if (!BTSLUtil.isNumeric(cardDetailsVO.getCardNumber())) {
                    this.addActionError(this.getText("cp2p.cardnumber.nonmuneric"));
                }

            } else {
                this.addActionError(this.getText("cp2p.cardnumber.required"));
            }

            if (!BTSLUtil.isNullString(cardDetailsVO.getNameOfEmbossing())) {
                String name = cardDetailsVO.getNameOfEmbossing();
                if (!BTSLUtil.isAlphaNumericIncludingSpace(name)) {
                    this.addActionError(this.getText("cp2p.nameofembossing.specialcharacters.error"));

                }
                if (cardDetailsVO.getNameOfEmbossing().length() > PretupsI.VALID_LENGTH_CARD_HOLDER_NAME) {
                    this.addActionError(this.getText("cp2p.nameofembossing.maxlength.error"));
                }
            } else {
                this.addActionError(this.getText("cp2p.nameofembossing.required"));
            }

            if (!BTSLUtil.isNullString(cardDetailsVO.getCardNickName())) {
                String p_nickName = cardDetailsVO.getCardNickName();

                if (!BTSLUtil.isAlphaNumeric(p_nickName)) {
                    this.addActionError(this.getText("cp2p.nickname.specialcharacters.error"));

                }

            } else {
                this.addActionError(this.getText("cp2p.nickname.required"));
            }

            if (!BTSLUtil.isNullString(cardDetailsVO.getEmail())) {
                if (!BTSLUtil.validateEmailID(cardDetailsVO.getEmail())) {
                    this.addActionError(this.getText("com.validemail.required"));
                }
            }

            boolean m = BTSLUtil.isNullString(cardDetailsVO.getMonthExpiry());
            boolean y = BTSLUtil.isNullString(cardDetailsVO.getYearExpiry());

            if ((m == false) && (y == false)) {
                int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);

                int expiryMonth = Integer.valueOf(cardDetailsVO.getMonthExpiry());
                int expiryYear = Integer.valueOf(cardDetailsVO.getYearExpiry());

                if ((expiryMonth <= currentMonth) && expiryYear <= (currentYear % 100)) {
                    this.addActionError(this.getText("cp2p.valid.date"));
                }
            } else {
                this.addActionError(this.getText("cp2p.Expirydate.required"));
            }

            Date dateOfBirth = cardDetailsVO.getDateOfBirth();
            Date curentDate = new Date();
            if (dateOfBirth != null) {
                if (dateOfBirth.after(curentDate)) {
                    this.addActionError(this.getText("cp2p.dob.aftercurrentdate"));
                }
                String stringStrutsDate = "01/01/70";
                try {
                    Date strutsDate = BTSLUtil.getDateFromDateString(stringStrutsDate);
                    if (BTSLUtil.getDifferenceInUtilDates(strutsDate, dateOfBirth) == 0) {
                        this.addActionError(this.getText("cp2p.dob.invalid"));
                    }
                } catch (Exception e) {
                    _log.errorTrace("validate", e);
                }
            }

        }

    }

    /*
     * Method to encrypt Embossing name , card no. and expiry date.
     */

    private void encryptCardDetails() throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("encryptCardDetails", "Entered");
        }
        try {
            _encryptedName = BTSLUtil.encryptText(_nameEmbossing);
            _encCardNo = BTSLUtil.encryptText(_cardNo);
            _encExpDate = BTSLUtil.encryptText(_expiryDate);
        } catch (Exception e) {
            _log.errorTrace("encryptCardDetails: Exception print stack trace:e=", e);
            throw new BTSLBaseException("cp2p.encryption.error");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("encryptCardDetails", "Exiting");
            }
        }
    }

    /*
     * Method to display registered cards
     */
    public String viewModifyDeleteCard() {

        if (_log.isDebugEnabled()) {
            _log.debug("viewModifyDeleteCard", "Entered");
        }

        String returnStr = null;
        CardDetailsVO cardDetailsVO = this.cardDetailsVO;

        CardDetailsDAO dao = null;
        ArrayList cardDetailList = null;
        Connection con = null;
        HttpSession session = _request.getSession();
        CP2PSubscriberVO subsVO = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");

        try {
            cardDetailsVO.flush();
            con = OracleUtil.getConnection();
            cardDetailsVO.setMsisdn(subsVO.getMsisdn());
            cardDetailsVO.setUserId(subsVO.getUserId());
            this.getMonthYearList(cardDetailsVO);
            dao = new CardDetailsDAO();
            cardDetailList = new ArrayList();

            cardDetailList = dao.loadSubscriberRegisteredCardDetails(con, subsVO.getUserId());
            cardDetailsVO.setCardDetailsList(cardDetailList);

            if (cardDetailList != null && cardDetailList.size() > 0) {
                returnStr = "cardDetails";
            } else {
                this.addActionError(this.getText("No card registered."));
                cardDetailsVO.flush();
                returnStr = "addCardDetails";
            }

        } catch (Exception e) {
            _log.errorTrace("viewModifyDeleteCard: Exception print stack trace: ", e);
            this.addActionError(this.getText("error.general.processing"));
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException sqle) {
                _log.errorTrace("viewModifyDeleteCard: Exception print stack trace: ", sqle);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("viewModifyDeleteCard", "Exiting" + returnStr);
            }
        }
        return returnStr;
    }

    /*
     * Method for confirm deletion of credit card detils.
     */

    public String confirmDeleteCreditCard() {

        if (_log.isDebugEnabled()) {
            _log.debug("confirmDeleteCreditCard", "Entered");
        }

        String returnStr = null;
        Connection con = null;
        ArrayList cardDetailList = null;
        CardDetailsDAO dao = new CardDetailsDAO();
        HttpSession session = _request.getSession();
        CP2PSubscriberVO subsVO = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");

        if (!BTSLUtil.isNullString(_request.getParameter("btnBack"))) {
            returnStr = "cardDetails";
        } else {
            try {
                con = OracleUtil.getConnection();
                boolean count = false;

                count = dao.deleteCardDetails(con, cardDetailsVO.getCardNickName(), subsVO.getMsisdn(), subsVO.getUserId());

                if (!count) {
                    con.rollback();
                    this.addActionError(this.getText(""));
                    returnStr = "";
                } else {
                    con.commit();
                    this.addActionError(this.getText(" Your card details has been deleted "));
                    String args[] = new String[] { cardDetailsVO.getCardNickName() };
                    try {
                        (new PushMessage(subsVO.getMsisdn(), BTSLUtil.getMessage(new Locale(subsVO.getLanguage(), subsVO.getCountry()), SelfTopUpErrorCodesI.CARD_DELETE_SUCCESS, args), "", PretupsI.REQUEST_SOURCE_TYPE_WEB, new Locale(subsVO.getLanguage(), subsVO.getCountry()))).push();
                    } catch (Exception e) {
                        _log.errorTrace("confirmAutoEnableDisable: Exception print stack trace: ", e);
                    }
                    cardDetailList = dao.loadSubscriberRegisteredCardDetails(con, subsVO.getUserId());
                    cardDetailsVO.setCardDetailsList(cardDetailList);
                    if (cardDetailList.size() == 0) {

                        returnStr = "addCardDetails";
                        cardDetailsVO.flush();
                    } else {
                        returnStr = "cardDetails";

                    }
                }

            } catch (Exception e) {
                _log.errorTrace("confirmDeleteCreditCard: Exception print stack trace:e=", e);
                this.addActionError(this.getText("error.general.processing"));
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ModifyCardController[validateNickName]", "", "", "", "Exception:" + e.getMessage());
            } finally {
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException sqle) {
                    _log.errorTrace("confirmDeleteCreditCardException print stack trace: ", sqle);
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("confirmDeleteCreditCard", "Exiting" + returnStr);
                }
            }
        }
        return returnStr;
    }

    /*
     * Method to view Credit card Details
     */

    public String viewCreditCardDetail() {

        if (_log.isDebugEnabled()) {
            _log.debug("viewCreditCardDetail", "Entered");
        }

        String returnStr = null;
        CardDetailsVO cardDetailsVO = this.cardDetailsVO;
        CardDetailsVO tempCardDetailsVO = null;

        int index = 0;
        index = Integer.valueOf(cardDetailsVO.getSelectedCard());

        if (index != 0) {
            try {

                tempCardDetailsVO = (CardDetailsVO) cardDetailsVO.getCardDetailsList().get(index - 1);

                cardDetailsVO.setBankName(tempCardDetailsVO.getBankName());
                cardDetailsVO.setCardNumber(tempCardDetailsVO.getCardNumber());
                cardDetailsVO.setExpiryDate(tempCardDetailsVO.getExpiryDate());
                cardDetailsVO.setNameOfEmbossing(tempCardDetailsVO.getNameOfEmbossing());
                cardDetailsVO.setCardNickName(tempCardDetailsVO.getCardNickName());
                cardDetailsVO.setDateOfBirth(tempCardDetailsVO.getDateOfBirth());
                cardDetailsVO.setAddress(tempCardDetailsVO.getAddress());
                cardDetailsVO.setEmail(tempCardDetailsVO.getEmail());
                cardDetailsVO.setDisplayCardNumber(tempCardDetailsVO.getDisplayCardNumber());
                cardDetailsVO.setSelectedCard(0);

                returnStr = "viewDetails";
            } catch (Exception e) {
                _log.errorTrace("viewCreditCardDetail: Exception print stack trace: ", e);
                this.addActionError(this.getText("error.general.processing"));
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ModifyCardController[validateNickName]", "", "", "", "Exception:" + e.getMessage());
            }

            finally {

                if (_log.isDebugEnabled()) {
                    _log.debug("viewCreditCardDetail", "Exiting" + returnStr);
                }
            }
        } else {
            this.addActionError(this.getText("Please select card first."));
            returnStr = "cardDetails";
        }

        return returnStr;
    }

    /*
     * Method to modify card nick name
     */

    public String modifyCardNickName() {

        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardNickName", "Entered");
        }

        String returnStr = null;
        Connection con = null;
        CardDetailsDAO dao = new CardDetailsDAO();
        SubscriberDAO subscriberDAO = new SubscriberDAO();
        HttpSession session = _request.getSession();
        CP2PSubscriberVO loggedInUser = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");

        if (!BTSLUtil.isNullString(_request.getParameter("btnBack"))) {
            returnStr = "cardDetails";
        } else {
            try {
                con = OracleUtil.getConnection();
                String p_nickName = cardDetailsVO.getCardNickName();
                CardDetailsVO tempVO = null;
                tempVO = new CardDetailsVO();
                tempVO = dao.loadAutoEnabledCard(con, loggedInUser.getUserId());

                if (BTSLUtil.isNullString(cardDetailsVO.getCardNickName())) {
                    this.addActionError(this.getText("cp2p.user.creditcard.registration.error.nicknamenull"));
                    returnStr = "modifyCard";
                } else if (tempVO != null && (tempVO.getAutoTopupStatus().equals("Y") && tempVO.getCardNickName().equalsIgnoreCase(cardDetailsVO.getCardNickName()))) {
                    this.addActionError(this.getText("cp2p.user.creditcard.registration.error.nicknameassociated"));
                    returnStr = "modifyCard";
                } else if (cardDetailsVO.getCardNickName().length() > PretupsI.NAME_ALLOWED_LENGTH) {
                    String arr[] = { String.valueOf(PretupsI.NAME_ALLOWED_LENGTH) };
                    this.addActionError(this.getText("cp2p.user.creditcard.registration.error.nicknamelong", arr));
                    returnStr = "modifyCard";
                }

                else if ((p_nickName.indexOf("%") != -1 || p_nickName.indexOf("^") != -1 || p_nickName.indexOf(" ") != -1 || p_nickName.indexOf("(") != -1 || p_nickName.indexOf(")") != -1 || p_nickName.indexOf("~") != -1 || p_nickName.indexOf("$") != -1 || p_nickName.indexOf("\"") != -1 || p_nickName.indexOf("@") != -1 || p_nickName.indexOf("+") != -1 || p_nickName.indexOf(",") != -1 || p_nickName.indexOf("#") != -1 || p_nickName.indexOf("@") != -1 || p_nickName.indexOf("&") != -1)) {
                    this.addActionError(this.getText("cp2p.user.creditcard.registration.error.nicknamealphanumeric"));
                    returnStr = "modifyCard";
                } else {

                    boolean nickNameExist = false;
                    boolean isAlreadyScheduled = false;

                    nickNameExist = dao.isNickNameAlreadyRegistered(con, cardDetailsVO.getCardNickName(), cardDetailsVO.getMsisdn());
                    isAlreadyScheduled = subscriberDAO.checkAlreadyEnabledCard(con, cardDetailsVO.getUserId(), (String) cardDetailsVO.getOldNickName());
                    if (!isAlreadyScheduled) {
                        if (nickNameExist) {
                            this.addActionError(this.getText("cp2p.user.creditcard.registration.error.nicknameexists"));
                            returnStr = "modifyCard";
                        } else {

                            returnStr = "confirmNickName";
                        }
                    } else {
                        this.addActionError(this.getText("cp2p.user.creditcard.registration.error.alreadyscheduled"));
                        returnStr = "modifyCard";
                    }

                }
            } catch (Exception e) {
                _log.errorTrace("modifyCardNickName: Exception print stack trace: ", e);
                this.addActionError(this.getText("error.general.processing"));
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ModifyCardController[validateNickName]", "", "", "", "Exception:" + e.getMessage());
            } finally {
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException sqle) {
                    _log.errorTrace("modifyCardNickName: Exception print stack trace: ", sqle);
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("modifyCardNickName", "Exiting" + returnStr);
                }
            }
        }
        return returnStr;

    }

    /*
     * Method to confirm modification of nickname
     */

    public String confirmNickName() {

        if (_log.isDebugEnabled()) {
            _log.debug("confirmNickName", "Entered");
        }

        ArrayList cardDetailList = null;
        String returnStr = null;
        Connection con = null;
        CardDetailsDAO dao = new CardDetailsDAO();
        HttpSession session = _request.getSession();
        CP2PSubscriberVO subsVO = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");

        if (!BTSLUtil.isNullString(_request.getParameter("btnBack"))) {
            returnStr = "modifyCard";
        } else {

            try {

                con = OracleUtil.getConnection();
                boolean count = false;
                count = dao.updateNick(con, subsVO.getMsisdn(), "0000", cardDetailsVO.getOldNickName(), cardDetailsVO.getCardNickName().toUpperCase(), subsVO.getUserId());

                if (count) {
                    con.commit();
                    this.addActionError(this.getText("cp2p.user.creditcard.registration.success.nicknameupdate"));

                    String args[] = new String[] { cardDetailsVO.getOldNickName(), cardDetailsVO.getCardNickName() };
                    try {
                        (new PushMessage(subsVO.getMsisdn(), BTSLUtil.getMessage(new Locale(subsVO.getLanguage(), subsVO.getCountry()), SelfTopUpErrorCodesI.CARD_MODIFY_SUCCESS, args), "", PretupsI.REQUEST_SOURCE_TYPE_WEB, new Locale(subsVO.getLanguage(), subsVO.getCountry()))).push();
                    } catch (Exception e) {
                        _log.errorTrace("confirmNickName: Exception print stack trace: ", e);
                    }

                    cardDetailList = dao.loadSubscriberRegisteredCardDetails(con, subsVO.getUserId());
                    cardDetailsVO.setCardDetailsList(cardDetailList);
                    returnStr = "cardDetails";
                }
            } catch (Exception e) {
                _log.errorTrace("confirmNickName: Exception print stack trace: ", e);
                this.addActionError(this.getText("error.general.processing"));
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ModifyCardController[validateNickName]", "", "", "", "Exception:" + e.getMessage());
            } finally {
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException sqle) {
                    _log.errorTrace("confirmNickName: Exception print stack trace: ", sqle);
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("confirmNickName", "Exiting" + returnStr);
                }
            }
        }

        return returnStr;
    }

    /*
     * Method to show jsp to for nickname modification.
     */
    public String modifyNickName() {

        if (_log.isDebugEnabled()) {
            _log.debug("modifyNickName", "Entered");
        }

        String returnStr = null;
        CardDetailsVO cardDetailsVO = this.cardDetailsVO;
        CardDetailsVO tempCardDetailsVO = null;
        int index = 0;
        index = Integer.valueOf(cardDetailsVO.getSelectedCard());

        if (index != 0) {
            try {

                tempCardDetailsVO = (CardDetailsVO) cardDetailsVO.getCardDetailsList().get(index - 1);

                cardDetailsVO.setCardNumber(tempCardDetailsVO.getCardNumber());
                cardDetailsVO.setExpiryDate(tempCardDetailsVO.getExpiryDate());
                cardDetailsVO.setCardNickName(tempCardDetailsVO.getCardNickName());
                cardDetailsVO.setOldNickName(tempCardDetailsVO.getCardNickName());
                cardDetailsVO.setDisplayCardNumber(tempCardDetailsVO.getDisplayCardNumber());
                cardDetailsVO.setSelectedCard(0);

                returnStr = "modifyCard";
            } catch (Exception e) {
                _log.errorTrace("modifyNickName: Exception print stack trace: ", e);
                this.addActionError(this.getText("error.general.processing"));
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ModifyCardController[validateNickName]", "", "", "", "Exception:" + e.getMessage());
            } finally {
                if (_log.isDebugEnabled()) {
                    _log.debug("modifyNickName", "Exiting" + returnStr);
                }
            }
        } else {
            this.addActionError(this.getText("Please select card first."));
            returnStr = "cardDetails";
        }

        return returnStr;
    }

    /*
     * Method for delete Credit Card.
     */

    public String deleteCreditCard() {

        if (_log.isDebugEnabled()) {
            _log.debug("deleteCreditCard", "Entered");
        }

        String returnStr = null;
        CardDetailsVO cardDetailsVO = this.cardDetailsVO;
        CardDetailsVO tempCardDetailsVO = null;
        int index = 0;
        index = Integer.valueOf(cardDetailsVO.getSelectedCard());

        if (index != 0) {
            try {

                tempCardDetailsVO = (CardDetailsVO) cardDetailsVO.getCardDetailsList().get(index - 1);

                cardDetailsVO.setBankName(tempCardDetailsVO.getBankName());
                cardDetailsVO.setCardNumber(tempCardDetailsVO.getCardNumber());
                cardDetailsVO.setExpiryDate(tempCardDetailsVO.getExpiryDate());
                cardDetailsVO.setNameOfEmbossing(tempCardDetailsVO.getNameOfEmbossing());
                cardDetailsVO.setCardNickName(tempCardDetailsVO.getCardNickName());
                cardDetailsVO.setDateOfBirth(tempCardDetailsVO.getDateOfBirth());
                cardDetailsVO.setAddress(tempCardDetailsVO.getAddress());
                cardDetailsVO.setEmail(tempCardDetailsVO.getEmail());
                cardDetailsVO.setDisplayCardNumber(tempCardDetailsVO.getDisplayCardNumber());
                cardDetailsVO.setSelectedCard(0);

                returnStr = "deleteCard";
            }

            catch (Exception e) {
                _log.errorTrace("deleteCreditCard: Exception print stack trace: ", e);
                this.addActionError(this.getText("error.general.processing"));
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ModifyCardController[validateNickName]", "", "", "", "Exception:" + e.getMessage());
            } finally {
                if (_log.isDebugEnabled()) {
                    _log.debug("deleteCreditCard", "Exiting" + returnStr);
                }
            }
        } else {
            this.addActionError(this.getText("Please select card first."));
            returnStr = "cardDetails";
        }
        return returnStr;

    }

    public String get_btnBack() {
        return _btnBack;
    }

    public void set_btnBack(String back) {
        _btnBack = back;
    }

    public String associateAutoTopup() {

        if (_log.isDebugEnabled()) {
            _log.debug("associateAutoTopup", "Entered");
        }
        cardDetailsVO.flush();
        String returnStr = null;
        String str = null;
        CardDetailsDAO dao = null;
        ArrayList cardDetailVOList = null;
        ArrayList cardDetailsList = null;
        ArrayList<String> autoScheduleTypeList = new ArrayList<String>();
        ArrayList<String> numberOfDays = new ArrayList<String>();
        StringBuilder sb = null;
        Connection con = null;
        HttpSession session = _request.getSession();
        CP2PSubscriberVO loggedInUser = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");
        try {

            CardDetailsVO cardDetailsVO = this.cardDetailsVO;
            autoScheduleTypeList.add("M");
            autoScheduleTypeList.add("W");
            autoScheduleTypeList.add("O");

            for (int i = 1; i <= 31; i++) {
                if (i < 10) {
                    sb = new StringBuilder();
                    sb.append("0");
                    sb.append(i);
                    str = sb.toString();
                    numberOfDays.add(str);
                } else {
                    str = String.valueOf(i);
                    numberOfDays.add(str);
                }
            }

            cardDetailsVO.setNumberOfDays(numberOfDays);

            cardDetailsList = new ArrayList();
            cardDetailVOList = new ArrayList();
            cardDetailsVO.setAutoScheduleTypeList(autoScheduleTypeList);
            cardDetailsVO.setCardDetailsList(cardDetailsList);
            con = OracleUtil.getConnection();
            dao = new CardDetailsDAO();
            cardDetailVOList = dao.loadSubscriberRegisteredCardDetails(con, loggedInUser.getUserId());
            if (cardDetailVOList.isEmpty()) {
                this.addActionError(this.getText("cp2p.user.creditcard.autoenable.nocardfound"));
                returnStr = "noCardFound";
                return returnStr;
            } else {
                CardDetailsVO tempVO = null;
                for (int i = 0; i < cardDetailVOList.size(); i++) {
                    tempVO = (CardDetailsVO) cardDetailVOList.get(i);
                    cardDetailsList.add(tempVO.getCardNickName());
                }
                cardDetailsVO.setCardDetailsList(cardDetailsList);
                tempVO = dao.loadAutoEnabledCard(con, loggedInUser.getUserId());
                if (tempVO == null) {
                    returnStr = "noCardAssociated";
                    return returnStr;
                } else if (tempVO.getAutoTopupStatus().equals("N")) {
                    returnStr = "noCardAssociated";
                    return returnStr;
                } else if (tempVO.getAutoTopupStatus().equals("Y")) {

                    cardDetailsVO.setAutoEnableFlag(true);
                    cardDetailsVO.setAutoTopupAmount(tempVO.getAutoTopupAmount());
                    cardDetailsVO.setCardNickName(tempVO.getCardNickName());
                    cardDetailsVO.setScheduleType(tempVO.getScheduleType());
                    cardDetailsVO.setEndDate(tempVO.getEndDate());
                    cardDetailsVO.setScheduledDate(tempVO.getScheduledDate());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(BTSLUtil.getDateFromDateString(tempVO.getScheduledDate()));
                    cardDetailsVO.setRequestedDay(cal.DAY_OF_MONTH);
                    returnStr = "cardAlreadyAssociated";
                    return returnStr;
                }
            }

        } catch (Exception e) {
            _log.errorTrace("associateAutoTopup: Exception print stack trace: ", e);
            this.addActionError(this.getText("error.general.processing"));
            return ERROR;
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException sqle) {
                _log.errorTrace("associateAutoTopup: Exception print stack trace: ", sqle);
                this.addActionError(this.getText("error.general.processing"));
                return ERROR;
            }
            if (_log.isDebugEnabled()) {
                _log.debug("associateAutoTopup", "Exiting returnStr:" + returnStr);
            }
        }

        return returnStr;

    }

    public String autoEnableDisable() {

        if (_log.isDebugEnabled()) {
            _log.debug("autoEnableDisable", "Entered");
        }
        String returnStr = null;
        boolean errorFlag = false;
        Connection con = null;
        CardDetailsDAO cardDetailsDao = null;
        HttpSession session = _request.getSession();
        CP2PSubscriberVO loggedInUser = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");
        try {

            if (cardDetailsVO.isAutoEnableFlag()) {
                returnStr = "confirmSuccess";
                return returnStr;
            } else {
                if (BTSLUtil.isNullString(cardDetailsVO.getCardNickName())) {
                    errorFlag = true;
                    this.addActionError(this.getText("cp2p.user.creditcard.autoenable.nickname.required"));
                }
                if (!BTSLUtil.isDecimalValue(String.valueOf(cardDetailsVO.getAutoTopupAmount()))) {
                    errorFlag = true;
                    this.addActionError(this.getText("cp2p.user.creditcard.autoenable.amount.nonnumeric"));
                } else if (cardDetailsVO.getAutoTopupAmount() == 0.0) {
                    errorFlag = true;
                    this.addActionError(this.getText("cp2p.user.creditcard.autoenable.amount.zero"));
                } else if (cardDetailsVO.getAutoTopupAmount() < 0.0) {
                    errorFlag = true;
                    this.addActionError(this.getText("cp2p.user.creditcard.autoenable.amount.negative"));
                } else if (Double.valueOf(cardDetailsVO.getAutoTopupAmount()) > (Long) SystemPreferences.MAX_AUTOTOPUP_AMT) {
                    String arr[] = { String.valueOf((Long) SystemPreferences.MAX_AUTOTOPUP_AMT) };
                    this.addActionError(this.getText("cp2p.user.creditcard.autoenable.amount.thresholdreached", arr));
                    errorFlag = true;
                }
                if (cardDetailsVO.getScheduleType().equalsIgnoreCase("M") || cardDetailsVO.getScheduleType().equalsIgnoreCase("O")) {
                    cardDetailsVO.setScheduledDate(BTSLUtil.getDateStringFromDate(BTSLUtil.getMonthlyScheduleDate(Integer.toString(cardDetailsVO.getRequestedDay()))));
                } else if (cardDetailsVO.getScheduleType().equalsIgnoreCase("W")) {
                    cardDetailsVO.setScheduledDate(BTSLUtil.getDateStringFromDate(BTSLUtil.getWeeklyScheduleDate(Integer.toString(cardDetailsVO.getRequestedDay()))));
                }
                if (cardDetailsVO.getScheduleType().equalsIgnoreCase("M") || cardDetailsVO.getScheduleType().equalsIgnoreCase("W") || cardDetailsVO.getScheduleType().equalsIgnoreCase("O")) {
                    if (BTSLUtil.isNullString(cardDetailsVO.getEndDate())) {
                        errorFlag = true;
                        this.addActionError(this.getText("cp2p.user.creditcard.autoenable.enddate.null"));
                    } else {
                        Date endDate;
                        try {
                            endDate = BTSLUtil.getDateFromDateString(cardDetailsVO.getEndDate());
                            // boolean
                            // before=BTSLUtil.getDateFromDateString(cardDetailsVO.getScheduledDate()).before(endDate);
                            SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yy");
                            boolean before2Days = BTSLUtil.getDifferenceInUtilDates(BTSLUtil.getDateFromDateString(cardDetailsVO.getScheduledDate()), endDate) < 2;
                            // if(!before){
                            if (before2Days) {
                                errorFlag = true;
                                String arr[] = { cardDetailsVO.getScheduledDate() };
                                this.addActionError(this.getText("cp2p.user.creditcard.autoenable.enddate.beforestartdate", arr));
                            }

                            CardDetailsVO cardDetailsExpiryVO = new CardDetailsVO();
                            cardDetailsDao = new CardDetailsDAO();
                            con = OracleUtil.getConnection();
                            cardDetailsExpiryVO = cardDetailsDao.loadCredtCardDetails(con, loggedInUser.getUserId(), cardDetailsVO.getCardNickName());
                            date_format = new SimpleDateFormat("MM/yy");
                            if (BTSLUtil.getDifferenceInUtilDates(BTSLUtil.getDateFromDateString(date_format.format(BTSLUtil.getDateFromDateString(cardDetailsVO.getEndDate())), "MM/yy"), BTSLUtil.getDateFromDateString(BTSLUtil.decryptText(cardDetailsExpiryVO.getExpiryDate()), "MM/yy")) < 0) {
                                errorFlag = true;
                                this.addActionError(this.getText("cp2p.user.creditcard.autoenable.enddate.enddateafterexpirydate"));
                            }
                        } catch (Exception e) {
                            errorFlag = true;
                            this.addActionError(this.getText("cp2p.user.creditcard.autoenable.enddate.formaterror"));
                        }
                    }
                }

                if (errorFlag) {
                    returnStr = ERROR;
                    return returnStr;
                } else {
                    returnStr = "confirmSuccess";
                    return returnStr;
                }
            }

        }

        catch (Exception e) {
            _log.errorTrace("autoEnableDisable: Exception print stack trace: ", e);
            this.addActionError(this.getText("error.general.processing"));
            return ERROR;
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException sqle) {
                _log.errorTrace("autoEnableDisable: Exception print stack trace: ", sqle);
                this.addActionError(this.getText("error.general.processing"));
                return ERROR;
            }
            if (_log.isDebugEnabled()) {
                _log.debug("autoEnableDisable", "Exiting returnStr:" + returnStr);
            }
        }

    }

    public String confirmAutoEnableDisable() {
        if (_log.isDebugEnabled()) {
            _log.debug("confirmAutoEnableDisable", "Entered");
        }
        String returnStr = null;
        CardDetailsDAO dao = null;
        Calendar cal = null;
        // int requestedDay=0;
        HttpSession session = _request.getSession();
        CP2PSubscriberVO loggedInUser = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");
        Connection con = null;
        int updateCount = 0;
        try {
            if (!BTSLUtil.isNullString(_request.getParameter("btnBack"))) {
                returnStr = "backAutoEnableDisable";
                return returnStr;
            } else {
                con = OracleUtil.getConnection();
                dao = new CardDetailsDAO();
                cal = Calendar.getInstance(TimeZone.getDefault());
                if (cardDetailsVO.isAutoEnableFlag()) {
                    updateCount = dao.disableAutoTopUp(con, loggedInUser.getUserId());
                    if (updateCount > 0) {
                        con.commit();
                        this.addActionError(this.getText("cp2p.user.creditcard.autodisable.success"));
                        try {
                            (new PushMessage(loggedInUser.getMsisdn(), BTSLUtil.getMessage(new Locale(loggedInUser.getLanguage(), loggedInUser.getCountry()), SelfTopUpErrorCodesI.AUTO_TOPUP_DISABLE_SUCCESS, null), "", PretupsI.REQUEST_SOURCE_TYPE_WEB, new Locale(loggedInUser.getLanguage(), loggedInUser.getCountry()))).push();
                        } catch (Exception e) {
                            _log.errorTrace("confirmAutoEnableDisable: Exception print stack trace: ", e);
                        }
                        returnStr = associateAutoTopup();
                    } else {
                        con.rollback();
                        this.addActionError(this.getText("error.general.processing"));
                        return ERROR;
                    }
                } else {
                    updateCount = dao.enableAutoTopup(con, loggedInUser.getUserId(), cardDetailsVO, BTSLUtil.getDateFromDateString(cardDetailsVO.getEndDate()));
                    if (updateCount > 0) {
                        con.commit();
                        String arr[] = { cardDetailsVO.getScheduledDate() };
                        this.addActionError(this.getText("cp2p.user.creditcard.autoenable.success", arr));
                        try {
                            (new PushMessage(loggedInUser.getMsisdn(), BTSLUtil.getMessage(new Locale(loggedInUser.getLanguage(), loggedInUser.getCountry()), SelfTopUpErrorCodesI.AUTO_TOPUP_REG_SUCCESSFUL, null), "", PretupsI.REQUEST_SOURCE_TYPE_WEB, new Locale(loggedInUser.getLanguage(), loggedInUser.getCountry()))).push();
                        } catch (Exception e) {
                            _log.errorTrace("confirmAutoEnableDisable: Exception print stack trace: ", e);
                        }
                        returnStr = associateAutoTopup();
                    } else {
                        con.rollback();
                        this.addActionError(this.getText("error.general.processing"));
                        return ERROR;
                    }
                }
            }
        }

        catch (Exception e) {
            _log.errorTrace("confirmAutoEnableDisable: Exception print stack trace: ", e);
            this.addActionError(this.getText("error.general.processing"));
            return ERROR;
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException sqle) {
                _log.errorTrace("confirmAutoEnableDisable: Exception print stack trace: ", sqle);
                this.addActionError(this.getText("error.general.processing"));
                return ERROR;
            }
            if (_log.isDebugEnabled()) {
                _log.debug("confirmAutoEnableDisable", "Exiting returnStr:" + returnStr);
            }
        }

        return returnStr;

    }

}
