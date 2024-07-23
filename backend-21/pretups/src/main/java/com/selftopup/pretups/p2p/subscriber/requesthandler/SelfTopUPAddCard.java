package com.selftopup.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.selftopup.pretups.p2p.transfer.businesslogic.CardDetailsDAO;
import com.selftopup.pretups.p2p.transfer.businesslogic.CardDetailsVO;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;
import com.selftopup.util.UtilValidate;

public class SelfTopUPAddCard implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(SelfTopUPAddCard.class.getName());
    String _nameEmbossing = null;
    String _cardNo = null;
    String _expiryDate = null;
    String _nickName = null;
    String _bank = null;
    String _cardType = null;
    String _encryptedName = null;
    String _encCardNo = null;
    String _encExpDate = null;
    Connection con = null;

    /**
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled())
            _log.debug("process", "Entered");
        try {
            SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            if (senderVO == null)
                throw new BTSLBaseException(this, "validateDetails", SelfTopUpErrorCodesI.SUBSCRIBER_NOT_REGISTERED);
            String[] args = p_requestVO.getRequestMessageArray();

            // message format
            // service_keyword name_embossing card_no. expiry_date nick_name
            // bank pin
            con = OracleUtil.getConnection();
            if (SystemPreferences.PIN_REQUIRED) {
                if (args.length == 7) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, args[6]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            con.commit();
                        throw be;
                    }
                } else
                    throw new BTSLBaseException(this, "processRequest", SelfTopUpErrorCodesI.P2P_ERROR_CARD_DETAILS_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
            }
            // TYPE IMEI PIN HOLDERNAME CARDNO EDATE NNAME
            // TYPE IMEI HOLDERNAME CARDNO EDATE NNAME PIN
            _nameEmbossing = args[2];
            _cardNo = args[3];
            _expiryDate = args[4];
            _nickName = args[5];

            validateDetails(p_requestVO);
            encryptCardDetails();
            CardDetailsDAO cardDetailsDAO = new CardDetailsDAO();
            if (cardDetailsDAO.isCardNumberAlreadyRegistered(con, _encCardNo))
                throw new BTSLBaseException(this, "processRequest", SelfTopUpErrorCodesI.CARD_DETAILS_ALREADY_EXIST);
            if (cardDetailsDAO.isNickNameAlreadyRegistered(con, _nickName, senderVO.getMsisdn()))
                throw new BTSLBaseException(this, "processRequest", SelfTopUpErrorCodesI.NICK_NAME_ALREADY_EXIST);
            CardDetailsVO cardDetailsVO = createCardDetailsVO(senderVO);
            if (cardDetailsDAO.isFirstCardNumber(con, cardDetailsVO.getUserId())) {
                cardDetailsVO.setIsDefault(PretupsI.YES);
            } else {
                cardDetailsVO.setIsDefault(PretupsI.NO);
            }

            int count = cardDetailsDAO.addCardDetails(con, cardDetailsVO, false);
            if (count <= 0)
                throw new BTSLBaseException(this, "processRequest", SelfTopUpErrorCodesI.CARD_DETAILS_INSERTION_ERROR);
            else {
                con.commit();
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.CARD_DETAILS_INSERTION_SUCCESSFUL);
            }
        } catch (BTSLBaseException be) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("processRequest", "BTSLBaseException " + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.CARD_DETAILS_INSERTION_FAILED);
        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("processRequest", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberCardDetailsControllerSc[processRequest]", "", "", "", "Getting Exception:" + e.getMessage());
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }

        finally {

            if (_log.isDebugEnabled())
                _log.debug("processRequest", "Exiting");
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {

            }
        }
    }

    /**
     * validate card details and if any exception occur show proper message
     * 
     * @throws BTSLBaseException
     */
    private void validateDetails(RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateDetails", "Entered");
        try {
            Date date = new Date();
            String name = "";
            String[] nameEmbossed = _nameEmbossing.split(SystemPreferences.NAMEEMBOSS_SEPT);
            for (int i = 0; i < nameEmbossed.length; i++) {
                name = name + " " + nameEmbossed[i];
            }
            name = name.substring(1, name.length());
            _nameEmbossing = name;
            if (_nameEmbossing.length() > PretupsI.VALID_LENGTH_CARD_HOLDER_NAME) {
                throw new BTSLBaseException(this, "validateDetails", SelfTopUpErrorCodesI.INVALID_LENGTH_HOLDER_NAME);
            }
            if (!BTSLUtil.isAlphaNumericIncludingSpace(_nameEmbossing)) {
                throw new BTSLBaseException(this, "validateDetails", SelfTopUpErrorCodesI.INVALID_HOLDER_NAME);
            }
            if (BTSLUtil.isNullString(_cardNo) || _cardNo.length() != 16) {
                if (_cardNo.length() != 15)
                    throw new BTSLBaseException(this, "validateDetails", SelfTopUpErrorCodesI.INVALID_CARD_NUMBER);
            }
            _cardType = UtilValidate.getCardType(_cardNo);

            if ("Unknown".equalsIgnoreCase(_cardType)) {
                throw new BTSLBaseException(this, "validateDetails", SelfTopUpErrorCodesI.INVALID_CREDITCARD_NUMBER);
            }

            if (BTSLUtil.isNullString(_expiryDate)) {
                throw new BTSLBaseException(this, "validateDetails", SelfTopUpErrorCodesI.INVALID_EXPIRY_DATE);
            }
            if (BTSLUtil.isValidDatePattern(_expiryDate)) {
                throw new BTSLBaseException(this, "validateDetails", SelfTopUpErrorCodesI.INVALID_EXPIRY_DATE);
            }
            SimpleDateFormat date_format = new SimpleDateFormat("MM/yy");
            if (BTSLUtil.getDifferenceInUtilDates(BTSLUtil.getDateFromDateString(date_format.format(date), "MM/yy"), BTSLUtil.getDateFromDateString(_expiryDate, "MM/yy")) < 0) {
                throw new BTSLBaseException(this, "validateDetails", SelfTopUpErrorCodesI.INVALID_EXPIRY_DATE_BEFORE);
            }
            // _expiryDate=BTSLUtil.getDateStringFromDate(BTSLUtil.getDateFromDateString(_expiryDate,PretupsI.DATE_FORMAT));
            if (!BTSLUtil.isNullString(_nickName)) {
                if (_nickName.length() > PretupsI.NAME_ALLOWED_LENGTH) {
                    String msgArr[] = { String.valueOf(PretupsI.NAME_ALLOWED_LENGTH) };
                    p_requestVO.setMessageArguments(msgArr);
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.ERROR_NICK_NAME_EXCEED_LENGTH);
                    throw new BTSLBaseException(this, "validateDetails", SelfTopUpErrorCodesI.ERROR_NICK_NAME_EXCEED_LENGTH, 0, msgArr, null);
                }
                char charArr[] = _nickName.toCharArray();
                for (int i = 0; i < charArr.length; i++) {
                    if (!((charArr[i] >= 65 && charArr[i] <= 90) || (charArr[i] >= 97 && charArr[i] <= 122) || (charArr[i] >= 48 && charArr[i] <= 57)))
                        throw new BTSLBaseException(this, "validateDetails", SelfTopUpErrorCodesI.INVALID_NICK_NAME);
                }
            }

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BTSLBaseException(this, "validateDetails", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validateDetails", "Exiting");
        }
    }

    /**
     * encrypt card number, expiry date and name embossing
     * 
     * @throws BTSLBaseException
     */
    private void encryptCardDetails() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("encryptCardDetails", "Entered");
        try {
            _encryptedName = BTSLUtil.encryptText(_nameEmbossing);
            _encCardNo = BTSLUtil.encryptText(_cardNo);
            _encExpDate = BTSLUtil.encryptText(_expiryDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BTSLBaseException(this, "encryptCardDetails", SelfTopUpErrorCodesI.CARD_DETAILS_ENCRYPTION_ERROR);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("encryptCardDetails", "Exiting");
        }
    }

    /**
     * create CardDetailsVO
     * 
     * @param p_senderVO
     * @return CardDetailsVO
     */
    private CardDetailsVO createCardDetailsVO(SenderVO p_senderVO) {
        if (_log.isDebugEnabled())
            _log.debug("createCardDetailsVO", "Entered p_senderVO: " + p_senderVO);
        CardDetailsVO cardDetailsVO = new CardDetailsVO();

        cardDetailsVO.setUserId(p_senderVO.getUserID());
        cardDetailsVO.setNameOfEmbossing(_encryptedName);
        cardDetailsVO.setCardNumber(_encCardNo);
        cardDetailsVO.setExpiryDate(_encExpDate);
        cardDetailsVO.setMsisdn(p_senderVO.getMsisdn());
        cardDetailsVO.setCardType(_cardType);
        cardDetailsVO.setCardNickName(_nickName.toUpperCase());
        cardDetailsVO.setBankName(_bank);
        cardDetailsVO.setCreatedOn(new Date());
        cardDetailsVO.setStatus(PretupsI.YES);
        cardDetailsVO.setAcceptTC(PretupsI.YES);

        if (_log.isDebugEnabled())
            _log.debug("createCardDetailsVO", "Exiting cardDetailsVO " + cardDetailsVO);
        return cardDetailsVO;
    }

}
