package com.selftopup.pretups.p2p.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import com.selftopup.util.BTSLUtil;

public class CardDetailsDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * @param p_con
     *            Connection
     * @param p_CardDetailsVO
     *            CardDetailsVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addCardDetails(Connection p_con, CardDetailsVO p_CardDetailsVO, boolean updateDefaultcard) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("addCardDetails", "Entered p_CardDetailsVO: " + p_CardDetailsVO);
        int count = 0;
        PreparedStatement pstm = null;
        int updateCard = -1;
        Date currentDate = new Date();
        p_CardDetailsVO.setCreatedOn(currentDate);
        try {
            StringBuffer qryBuffer = new StringBuffer();
            qryBuffer.append("INSERT INTO CARD_DETAILS(USER_ID, NAME_OF_EMBOSSING, ");
            qryBuffer.append("CARD_NUMBER, CARD_TYPE, CARD_NICK_NAME, BANK, EXPIRY_DATE, MSISDN, ");
            qryBuffer.append("DOB, EMAIL, ADDRESS, CREATED_ON, STATUS, ACCEPT_T_C,is_default) ");
            qryBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

            String query = qryBuffer.toString();
            if (_log.isDebugEnabled())
                _log.debug("addCardDetails", "Query: " + query);
            if (updateDefaultcard) {
                updateCard = this.defaultCardExistUpdate(p_con, p_CardDetailsVO.getUserId());
                if (updateCard < 0)
                    throw new BTSLBaseException(this, "addCardDetails", SelfTopUpErrorCodesI.DEFAULT_CARD_DETAILS_UPDATION_ERROR);
            }

            pstm = p_con.prepareStatement(query);
            int i = 0;
            pstm.setString(++i, p_CardDetailsVO.getUserId());
            pstm.setString(++i, p_CardDetailsVO.getNameOfEmbossing());
            pstm.setString(++i, p_CardDetailsVO.getCardNumber());
            pstm.setString(++i, p_CardDetailsVO.getCardType());
            pstm.setString(++i, p_CardDetailsVO.getCardNickName().trim().toUpperCase());
            pstm.setString(++i, p_CardDetailsVO.getBankName());
            pstm.setString(++i, p_CardDetailsVO.getExpiryDate());
            // pstm.setString(++i,"demo");
            pstm.setString(++i, p_CardDetailsVO.getMsisdn());
            pstm.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_CardDetailsVO.getDateOfBirth()));
            pstm.setString(++i, p_CardDetailsVO.getEmail());
            pstm.setString(++i, p_CardDetailsVO.getAddress());
            pstm.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_CardDetailsVO.getCreatedOn()));
            // pstm.setString(++i,p_CardDetailsVO.getStatus());
            pstm.setString(++i, "Y");
            // pstm.setString(++i,p_CardDetailsVO.getAcceptTC());
            pstm.setString(++i, "Y");
            pstm.setString(++i, p_CardDetailsVO.getIsDefault());

            count = pstm.executeUpdate();
            if (count <= 0)
                throw new BTSLBaseException(this, "addCardDetails", SelfTopUpErrorCodesI.CARD_DETAILS_INSERTION_ERROR);

        } catch (SQLException sqe) {
            _log.error("addCardDetails", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[addCardDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "addCardDetails", SelfTopUpErrorCodesI.CARD_DETAILS_INSERTION_ERROR);
        } catch (Exception ex) {
            _log.error("addCardDetails", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[addCardDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "addCardDetails", SelfTopUpErrorCodesI.CARD_DETAILS_INSERTION_ERROR);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("addCardDetails", "Exiting count " + count);
        }
        return count;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_cardNo
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isCardNumberAlreadyRegistered(Connection p_con, String p_cardNo) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isCardNumberAlreadyRegistered", "Entered p_cardNo " + p_cardNo);
        boolean isExist = false;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        try {
            String query = " SELECT 1 FROM CARD_DETAILS WHERE  CARD_NUMBER=? AND STATUS='Y' ";

            if (_log.isDebugEnabled())
                _log.debug("isCardNumberAlreadyRegistered", "Query: " + query);

            pstm = p_con.prepareStatement(query);
            pstm.setString(1, p_cardNo);
            rst = pstm.executeQuery();

            if (rst.next())
                isExist = true;
        } catch (SQLException sqe) {
            _log.error("isCardNumberAlreadyRegistered", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isCardNumberAlreadyRegistered]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isCardNumberAlreadyRegistered", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error("isCardNumberAlreadyRegistered", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isCardNumberAlreadyRegistered]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "isCardNumberAlreadyRegistered", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
            }
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isCardNumberAlreadyRegistered", "Exiting isExist " + isExist);
        }
        return isExist;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_nickName
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isNickNameAlreadyRegistered(Connection p_con, String p_nickName, String p_msisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isCardNumberAlreadyRegistered", "Entered p_nickName " + p_nickName + "p_msisdn " + p_msisdn);
        boolean isExist = false;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        try {
            String query = " SELECT 1 FROM CARD_DETAILS WHERE  UPPER(CARD_NICK_NAME)=? AND STATUS='Y' AND MSISDN=? ";

            if (_log.isDebugEnabled())
                _log.debug("isNickNameAlreadyRegistered", "Query: " + query);

            pstm = p_con.prepareStatement(query);
            pstm.setString(1, p_nickName.toUpperCase());
            pstm.setString(2, p_msisdn);
            rst = pstm.executeQuery();

            if (rst.next())
                isExist = true;
        } catch (SQLException sqe) {
            _log.error("isNickNameAlreadyRegistered", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isNickNameAlreadyRegistered]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isNickNameAlreadyRegistered", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error("isNickNameAlreadyRegistered", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isNickNameAlreadyRegistered]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "isNickNameAlreadyRegistered", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
            }
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isNickNameAlreadyRegistered", "Exiting isExist " + isExist);
        }
        return isExist;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_senderId
     *            String
     * @param p_nickName
     *            String
     * @return boolean
     */
    public boolean validateNickName(Connection p_con, String p_senderId, String p_nickName) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateNickName", "Entered p_nickName " + p_nickName + " p_senderId: " + p_senderId);
        boolean isExist = false;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        try {
            String query = " SELECT 1 FROM CARD_DETAILS WHERE  UPPER(CARD_NICK_NAME)=? AND STATUS='Y' AND USER_ID=? ";

            if (_log.isDebugEnabled())
                _log.debug("validateNickName", "Query: " + query);

            pstm = p_con.prepareStatement(query);
            pstm.setString(1, p_nickName.toUpperCase());
            pstm.setString(2, p_senderId);
            rst = pstm.executeQuery();

            if (rst.next())
                isExist = true;
        } catch (SQLException sqe) {
            _log.error("validateNickName", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isNickNameAlreadyRegistered]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "validateNickName", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error("isNickNameAlreadyRegistered", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isNickNameAlreadyRegistered]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "validateNickName", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
            }
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("validateNickName", "Exiting isExist " + isExist);
        }
        return isExist;

    }

    /**
     * @param p_con
     *            Connection
     * @param p_senderId
     *            String
     * @param p_nickName
     *            String
     * @return boolean
     */
    public CardDetailsVO loadCredtCardDetails(Connection p_con, String p_senderId, String p_nickName) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadCredtCardDetails", "Entered p_nickName " + p_nickName + " p_senderId: " + p_senderId);
        CardDetailsVO cardDetailsVO = null;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        try {
            StringBuffer queryBuffer = new StringBuffer();
            queryBuffer.append(" SELECT USER_ID, NAME_OF_EMBOSSING, CARD_NUMBER, CARD_TYPE, ");
            queryBuffer.append(" CARD_NICK_NAME, BANK, EXPIRY_DATE, MSISDN, DOB, EMAIL, ADDRESS, CREATED_ON, STATUS, ACCEPT_T_C,IS_DEFAULT ");
            queryBuffer.append(" FROM CARD_DETAILS WHERE USER_ID=? AND CARD_NICK_NAME=? AND STATUS=? ");

            String query = queryBuffer.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadCredtCardDetails", "Query: " + query);

            pstm = p_con.prepareStatement(query);
            pstm.setString(1, p_senderId);
            pstm.setString(2, p_nickName);
            pstm.setString(3, PretupsI.YES);
            rst = pstm.executeQuery();

            while (rst.next()) {
                cardDetailsVO = new CardDetailsVO();
                cardDetailsVO.setUserId(rst.getString("USER_ID"));
                cardDetailsVO.setNameOfEmbossing(rst.getString("NAME_OF_EMBOSSING"));
                cardDetailsVO.setCardNumber(rst.getString("CARD_NUMBER"));
                cardDetailsVO.setCardType(rst.getString("CARD_TYPE"));
                cardDetailsVO.setCardNickName(rst.getString("CARD_NICK_NAME"));
                cardDetailsVO.setBankName(rst.getString("BANK"));
                cardDetailsVO.setExpiryDate(rst.getString("EXPIRY_DATE"));
                cardDetailsVO.setMsisdn(rst.getString("MSISDN"));
                cardDetailsVO.setDateOfBirth(rst.getDate("DOB"));
                cardDetailsVO.setEmail(rst.getString("EMAIL"));
                cardDetailsVO.setAddress(rst.getString("ADDRESS"));
                cardDetailsVO.setCreatedOn(rst.getDate("CREATED_ON"));
                cardDetailsVO.setStatus(rst.getString("STATUS"));
                cardDetailsVO.setAcceptTC(rst.getString("ACCEPT_T_C"));
                cardDetailsVO.setIsDefault(rst.getString("IS_DEFAULT"));
            }
        } catch (SQLException sqe) {
            _log.error("loadCredtCardDetails", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[loadCredtCardDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCredtCardDetails", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error("loadCredtCardDetails", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[loadCredtCardDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCredtCardDetails", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
            }
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadCredtCardDetails", "Exiting cardDetailsVO " + cardDetailsVO);
        }
        return cardDetailsVO;

    }

    /**
     * @param p_con
     *            Connection
     * @param p_senderId
     *            String
     * @param p_nickName
     *            String
     * @return boolean
     */
    public CardDetailsVO loadDefaultCredtCardDetails(Connection p_con, String p_senderId) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadDefaultCredtCardDetails", "Entered  p_senderId: " + p_senderId);
        CardDetailsVO cardDetailsVO = null;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        try {
            StringBuffer queryBuffer = new StringBuffer();
            queryBuffer.append(" SELECT USER_ID, NAME_OF_EMBOSSING, CARD_NUMBER, CARD_TYPE, ");
            queryBuffer.append(" CARD_NICK_NAME, BANK, EXPIRY_DATE, MSISDN, DOB, EMAIL, ADDRESS, CREATED_ON, STATUS, ACCEPT_T_C,IS_DEFAULT ");
            queryBuffer.append(" FROM CARD_DETAILS WHERE USER_ID=? AND STATUS=? AND IS_DEFAULT=? ");

            String query = queryBuffer.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadDefaultCredtCardDetails", "Query: " + query);

            pstm = p_con.prepareStatement(query);
            pstm.setString(1, p_senderId);
            pstm.setString(2, PretupsI.YES);
            pstm.setString(3, PretupsI.YES);
            rst = pstm.executeQuery();

            while (rst.next()) {
                cardDetailsVO = new CardDetailsVO();
                cardDetailsVO.setUserId(rst.getString("USER_ID"));
                cardDetailsVO.setNameOfEmbossing(rst.getString("NAME_OF_EMBOSSING"));
                cardDetailsVO.setCardNumber(rst.getString("CARD_NUMBER"));
                cardDetailsVO.setCardType(rst.getString("CARD_TYPE"));
                cardDetailsVO.setCardNickName(rst.getString("CARD_NICK_NAME"));
                cardDetailsVO.setBankName(rst.getString("BANK"));
                cardDetailsVO.setExpiryDate(rst.getString("EXPIRY_DATE"));
                cardDetailsVO.setMsisdn(rst.getString("MSISDN"));
                cardDetailsVO.setDateOfBirth(rst.getDate("DOB"));
                cardDetailsVO.setEmail(rst.getString("EMAIL"));
                cardDetailsVO.setAddress(rst.getString("ADDRESS"));
                cardDetailsVO.setCreatedOn(rst.getDate("CREATED_ON"));
                cardDetailsVO.setStatus(rst.getString("STATUS"));
                cardDetailsVO.setAcceptTC(rst.getString("ACCEPT_T_C"));
                cardDetailsVO.setIsDefault(rst.getString("IS_DEFAULT"));
            }
        } catch (SQLException sqe) {
            _log.error("loadDefaultCredtCardDetails", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[loadDefaultCredtCardDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadDefaultCredtCardDetails", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error("loadDefaultCredtCardDetails", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[loadDefaultCredtCardDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadDefaultCredtCardDetails", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
            }
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadDefaultCredtCardDetails", "Exiting cardDetailsVO " + cardDetailsVO);
        }
        return cardDetailsVO;

    }

    /**
     * @param p_con
     *            Connection
     * @param CP2PSubscriberVO
     *            Object
     * @param CardDetailsVO
     *            Object
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadSubscriberRegisteredCardDetails(Connection p_con, String p_userId) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadSubscriberRegisteredCardDetails", "Entered p_userId " + p_userId);

        PreparedStatement pstm = null;
        ResultSet rst = null;
        ArrayList cardDetailList = null;
        CardDetailsVO p_cardVO = null;
        int i = 1;
        int index = 0;
        try {
            cardDetailList = new ArrayList();
            StringBuffer sbf = new StringBuffer();
            sbf.append(" SELECT NAME_OF_EMBOSSING,CARD_NUMBER,CARD_TYPE,CARD_NICK_NAME,BANK,EXPIRY_DATE,ACCEPT_T_C,CREATED_ON, ");
            sbf.append(" MSISDN,DOB,EMAIL,ADDRESS,STATUS,is_default FROM CARD_DETAILS WHERE USER_ID= ? and status='Y'");

            if (_log.isDebugEnabled())
                _log.debug("loadSubscriberRegisteredCardDetails", "Query: " + sbf.toString());

            pstm = p_con.prepareStatement(sbf.toString());
            pstm.setString(i++, p_userId);
            rst = pstm.executeQuery();

            while (rst.next()) {
                p_cardVO = new CardDetailsVO();
                p_cardVO.setNameOfEmbossing(BTSLUtil.decryptText(rst.getString("NAME_OF_EMBOSSING")));
                p_cardVO.setAcceptTC(rst.getString("ACCEPT_T_C"));
                p_cardVO.setAddress(rst.getString("ADDRESS"));
                // p_cardVO.setBank(rst.getString("BANK"));
                p_cardVO.setBankName(rst.getString("BANK"));
                p_cardVO.setCardNickName(rst.getString("CARD_NICK_NAME"));
                p_cardVO.setCardNumber(BTSLUtil.decryptText(rst.getString("CARD_NUMBER")));
                p_cardVO.setCreatedOn(BTSLUtil.getUtilDateFromSQLDate(rst.getDate("CREATED_ON")));
                p_cardVO.setMsisdn(rst.getString("MSISDN"));
                // p_cardVO.setDob(BTSLUtil.getUtilDateFromSQLDate(rst.getDate("DOB")));
                p_cardVO.setDateOfBirth(BTSLUtil.getUtilDateFromSQLDate(rst.getDate("DOB")));
                p_cardVO.setCardType(rst.getString("CARD_TYPE"));
                p_cardVO.setEmail(rst.getString("EMAIL"));
                p_cardVO.setStatus(rst.getString("STATUS"));
                String expiryDate = BTSLUtil.decryptText(rst.getString("EXPIRY_DATE"));
                // expiryDate = convertDate(expiryDate);
                p_cardVO.setExpiryDate(expiryDate);
                p_cardVO.setIsDefault(rst.getString("IS_DEFAULT"));
                p_cardVO.setOriginalCardNumber(p_cardVO.getCardNumber());
                p_cardVO.setRadioIndex(index);

                String var = "";
                String var1 = null;
                int k;
                for (k = 0; k <= (BTSLUtil.decryptText(rst.getString("CARD_NUMBER")).length()) - 5; k++)
                    var = var + 'x';

                var1 = (BTSLUtil.decryptText(rst.getString("CARD_NUMBER"))).substring(k);

                var = var.concat(var1);

                p_cardVO.setDisplayCardNumber(var);

                cardDetailList.add(p_cardVO);
                index++;
            }

        } catch (SQLException sqe) {
            _log.error("loadSubscriberRegisteredCardDetails", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[loadSubscriberRegisteredCardDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberRegisteredCardDetails", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error("loadSubscriberRegisteredCardDetails", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[loadSubscriberRegisteredCardDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberRegisteredCardDetails", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
            }
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadSubscriberRegisteredCardDetails", "Exiting cardDetailList " + cardDetailList.size());
        }
        return cardDetailList;
    }

    private String convertDate(String expiryDate) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(expiryDate.substring(0, 2));
        stringBuilder.append("-");
        stringBuilder.append(expiryDate.substring(2, 4));
        return stringBuilder.toString();
    }

    /**
     * method for updating user card details
     * 
     * @param p_con
     *            Connection
     * @param p_cardVO
     *            CardDetailsVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateUserCardDetails(Connection p_con, CardDetailsVO p_cardVO, boolean updateDefaultCard) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateUserCardDetails", "Entered ");

        PreparedStatement pstm = null;
        int updateCount = 0;
        int i = 1;
        try {
            String query = " update CARD_DETAILS set DOB=?, EMAIL=?, ADDRESS=?, is_default=? WHERE  user_id=? and card_number=? ";

            if (_log.isDebugEnabled())
                _log.debug("updateUserCardDetails", "Query: " + query);

            if (updateDefaultCard) {
                int updateCard = this.defaultCardExistUpdate(p_con, p_cardVO.getUserId());
                if (updateCard < 0)
                    throw new BTSLBaseException(this, "addCardDetails", SelfTopUpErrorCodesI.DEFAULT_CARD_DETAILS_UPDATION_ERROR);
            }
            pstm = p_con.prepareStatement(query);
            pstm.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_cardVO.getDateOfBirth()));
            pstm.setString(i++, p_cardVO.getEmail());
            pstm.setString(i++, p_cardVO.getAddress());
            pstm.setString(i++, p_cardVO.getIsDefault());
            pstm.setString(i++, p_cardVO.getUserId());
            pstm.setString(i++, BTSLUtil.encryptAES(p_cardVO.getOriginalCardNumber()));

            updateCount = pstm.executeUpdate();

        } catch (SQLException sqe) {
            _log.error("updateUserCardDetails", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[updateUserCardDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateUserCardDetails", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error("updateUserCardDetails", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[updateUserCardDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "updateUserCardDetails", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateUserCardDetails", "Exiting isExist " + updateCount);
        }
        return updateCount;
    }

    /**
     * method to check that it is default card or not
     * 
     * @param p_con
     * @param p_cardVO
     * @return
     * @throws BTSLBaseException
     */
    public boolean isDefaultCardDetail(Connection p_con, CardDetailsVO p_cardVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isDefaultCardExist", "Entered ");

        PreparedStatement pstm = null;
        ResultSet rst = null;
        boolean selectCount = false;
        int i = 1;
        try {
            String query = " select 1 from CARD_DETAILS WHERE user_id=? and card_number=? and is_default='Y' and status='Y'";

            if (_log.isDebugEnabled())
                _log.debug("isDefaultCardExist", "Query: " + query);

            pstm = p_con.prepareStatement(query);
            pstm.setString(i++, p_cardVO.getUserId());
            pstm.setString(i++, BTSLUtil.encryptAES(p_cardVO.getOriginalCardNumber()));

            rst = pstm.executeQuery();
            while (rst.next()) {
                selectCount = true;
            }

        } catch (SQLException sqe) {
            _log.error("isDefaultCardExist", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isDefaultCardExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isDefaultCardExist", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error("isDefaultCardExist", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isDefaultCardExist]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "isDefaultCardExist", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
            }
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isDefaultCardExist", "Exiting isExist " + selectCount);
        }
        return selectCount;
    }

    /**
     * 
     * @param p_con
     * @param p_cardVO
     * @return
     * @throws BTSLBaseException
     */
    public int deleteCardDetail(Connection p_con, CardDetailsVO p_cardVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("deleteCardDetail", "Entered ");

        PreparedStatement pstm = null;
        int deleteCount = -1;
        int i = 1;
        try {
            String query = " update CARD_DETAILS set status=? WHERE user_id=? and card_number=? ";

            if (_log.isDebugEnabled())
                _log.debug("deleteCardDetail", "Query: " + query);

            pstm = p_con.prepareStatement(query);
            pstm.setString(i++, PretupsI.NO);
            pstm.setString(i++, p_cardVO.getUserId());
            pstm.setString(i++, BTSLUtil.encryptAES(p_cardVO.getOriginalCardNumber()));

            deleteCount = pstm.executeUpdate();

        } catch (SQLException sqe) {
            _log.error("deleteCardDetail", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[deleteCardDetail]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "deleteCardDetail", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error("deleteCardDetail", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[deleteCardDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "deleteCardDetail", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteCardDetail", "Exiting deleted count " + deleteCount);
        }
        return deleteCount;
    }

    /**
     * method to check that it is default card or not
     * 
     * @param p_con
     * @param p_cardVO
     * @return
     * @throws BTSLBaseException
     */
    private int defaultCardExistUpdate(Connection p_con, String p_userId) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("defaultCardExistUpdate", "Entered ");

        PreparedStatement pstm = null;
        PreparedStatement updateStmt = null;
        ResultSet rst = null;
        // boolean selectCount=false;
        String cardNumber = "";
        int i = 1, j = 1, updateCount = -1;
        try {
            String query = "select card_number from CARD_DETAILS WHERE user_id=? and is_default='Y' and status='Y'";
            String updateQuery = "update CARD_DETAILS set is_default=? WHERE user_id=? and card_number=? and status='Y' ";

            if (_log.isDebugEnabled())
                _log.debug("defaultCardExistUpdate", "Query: " + query + ",  updateQuery=" + updateQuery);

            pstm = p_con.prepareStatement(query);
            pstm.setString(i++, p_userId);

            rst = pstm.executeQuery();
            while (rst.next()) {
                cardNumber = BTSLUtil.decryptAES(rst.getString("card_number"));
            }

            if (cardNumber != null && cardNumber != "" && cardNumber.length() > 0) {
                updateStmt = p_con.prepareStatement(updateQuery);
                updateStmt.setString(j++, PretupsI.NO);
                updateStmt.setString(j++, p_userId);
                updateStmt.setString(j++, BTSLUtil.encryptAES(cardNumber));
                updateCount = updateStmt.executeUpdate();
                if (updateCount < 0)
                    throw new BTSLBaseException(this, "defaultCardExistUpdate", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
            } else
                updateCount = 1;

        } catch (SQLException sqe) {
            _log.error("defaultCardExistUpdate", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[defaultCardExistUpdate]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "defaultCardExistUpdate", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error("defaultCardExistUpdate", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[defaultCardExistUpdate]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "defaultCardExistUpdate", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
            }
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
            }
            try {
                if (updateStmt != null) {
                    updateStmt.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled())
                _log.debug("defaultCardExistUpdate", "Exiting updateCount " + updateCount);
        }
        return updateCount;
    }

    /***
     * @author gaurav.pandey
     * @param p_con
     * @param p_cardNo
     * @return
     * @throws BTSLBaseException
     */

    public boolean isFirstCardNumber(Connection p_con, String user_id) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isFirstCardNumber", "Entered user_id " + user_id);
        boolean isExist = true;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        try {
            String query = " SELECT 1 FROM CARD_DETAILS WHERE user_id=? ";

            if (_log.isDebugEnabled())
                _log.debug("isFirstCardNumber", "Query: " + query);

            pstm = p_con.prepareStatement(query);
            pstm.setString(1, user_id);
            rst = pstm.executeQuery();

            if (rst.next())
                isExist = false;
        } catch (SQLException sqe) {
            _log.error("isFirstCardNumber", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isFirstCardNumber]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isFirstCardNumber", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error("isCardNumberAlreadyRegistered", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isFirstCardNumber]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "isFirstCardNumber", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
            }
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isFirstCardNumber", "Exiting isExist " + isExist);
        }
        return isExist;
    }

    /***
     * @author gaurav.pandey
     * @param p_con
     * @param p_cardNo
     * @return
     * @throws BTSLBaseException
     */

    public String getSubscriberKey(Connection p_con, String Msisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getSubscriberKey", "Entered msisdn " + Msisdn);
        String key = null;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        try {
            String query = " SELECT ENCRYPTION_KEY FROM P2P_SUBSCRIBERS WHERE MSISDN=? ";

            if (_log.isDebugEnabled())
                _log.debug("isFirstCardNumber", "Query: " + query);

            pstm = p_con.prepareStatement(query);
            pstm.setString(1, Msisdn);
            rst = pstm.executeQuery();

            if (rst.next()) {
                key = rst.getString("ENCRYPTION_KEY");
            }

        } catch (SQLException sqe) {
            _log.error("getSubscriberKey", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[getSubscriberKey]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "getSubscriberKey", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error("getSubscriberKey", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[getSubscriberKey]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "getSubscriberKey", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
            }
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("getSubscriberKey", "Exiting isExist " + key);
        }
        return key;
    }

    /**
     * Method to update the card's nick
     * 
     * @param p_con
     * @param p_msisdn
     * @param p_imei
     * @param p_nickName
     * @param p_userID
     * @return exist
     * @throws BTSLBaseException
     * @author Vikas Singh
     */
    public boolean updateNick(Connection p_con, String p_msisdn, String p_imei, String p_nickName, String p_newNickName, String p_userID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateNick", "Entered p_msisdn:" + p_msisdn + "p_imei:" + p_imei + "p_newNickName:" + p_newNickName + "p_userid:" + p_userID);
        boolean exist = false;
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        // Connection con = p_con;
        try {
            StringBuffer updateQueryBuff = new StringBuffer("update card_details set card_nick_name = ? where status = ? and card_nick_name= ? ");
            updateQueryBuff.append(" and user_id= ? and msisdn = ?");
            String updateQuery = updateQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("updateNick", "update query:" + updateQuery);
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(1, p_newNickName);
            pstmtUpdate.setString(2, PretupsI.YES);
            pstmtUpdate.setString(3, p_nickName);
            pstmtUpdate.setString(4, p_userID);
            pstmtUpdate.setString(5, p_msisdn);
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0) {
                exist = true;
            }

        } catch (SQLException sqle) {
            try {
                if (p_con != null)
                    p_con.rollback();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            _log.error("updateNick", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateNick]", "", p_msisdn, "", " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateNick", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            try {
                if (p_con != null)
                    p_con.rollback();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            _log.error("updateNick", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateNick]", "", p_msisdn, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateNick", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (p_con != null)
                    p_con.commit();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            // try{if(rs!=null) rs.close();}catch(Exception e){}
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateNick", "Exiting with status:" + exist);
        }// end of finally

        return exist;

    }

    /**
     * Method to delete the card
     * 
     * @param p_con
     * @param p_msisdn
     * @param p_nickName
     * @param p_userID
     * @return isDeleted
     * @throws BTSLBaseException
     * @author Vikas Singh
     */
    public boolean deleteCardDetails(Connection p_con, String p_nickName, String p_msisdn, String p_userId) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("deleteNickName", "Entered  :: nick_name: " + p_nickName + "p_msisdn: " + p_msisdn + "p_userid: " + p_userId);
        boolean isDeleted = false;
        PreparedStatement psmtDeleteCardDetails = null;
        int updateCount = 0;
        try {
            StringBuffer sbf = new StringBuffer("DELETE FROM CARD_DETAILS WHERE CARD_NICK_NAME= ? AND MSISDN= ? AND USER_ID= ?");
            String deleteQuery = sbf.toString();
            psmtDeleteCardDetails = p_con.prepareStatement(deleteQuery);
            psmtDeleteCardDetails.setString(1, p_nickName);
            psmtDeleteCardDetails.setString(2, p_msisdn);
            psmtDeleteCardDetails.setString(3, p_userId);
            if (_log.isDebugEnabled())
                _log.debug("deleteCardDetails", "delete query:" + deleteQuery);
            updateCount = psmtDeleteCardDetails.executeUpdate();
            if (updateCount <= 0) {
                psmtDeleteCardDetails.clearParameters();
                p_con.rollback();
                isDeleted = false;
            } else
                isDeleted = true;
        }

        catch (SQLException sqle) {
            try {
                if (p_con != null)
                    p_con.rollback();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            _log.error("deleteNickName", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteNickName]", "", p_nickName, "", " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteCardDetails", "error.general.sql.processing");
        } catch (Exception e) {
            try {
                if (p_con != null)
                    p_con.rollback();
            } catch (SQLException se) {
                // TODO Auto-generated catch block
                se.printStackTrace();
            }
            _log.error("deleteNickName", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteNickName]", "", p_nickName, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteCardDetails", "error.general.processing");
        } finally {
            try {
                if (p_con != null)
                    p_con.commit();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            // try{if(rs!=null) rs.close();}catch(Exception e){}
            try {
                if (psmtDeleteCardDetails != null)
                    psmtDeleteCardDetails.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteCardDetails", "Exiting with status:" + isDeleted);
        }
        return isDeleted;
    }

    public boolean checkNickName(Connection p_con, String p_msisdn, String p_imei, String p_nickName, String p_userID) throws BTSLBaseException, Exception {
        boolean isExist = false;
        if (_log.isDebugEnabled())
            _log.debug("checkNickName", "Entered p_msisdn: " + p_msisdn + "p_imei: " + p_imei + "p_nick: " + p_nickName + "p_userID: " + p_userID);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer("SELECT 1 from card_details where card_nick_name = ? and msisdn = ? and status = ? and user_id = ?");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("checkNickName", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_nickName.toUpperCase());
            pstmtSelect.setString(2, p_msisdn);
            pstmtSelect.setString(3, PretupsI.YES);
            pstmtSelect.setString(4, p_userID);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                isExist = true;
                return isExist;
            }
        } catch (SQLException sqle) {
            _log.error("checkNickName", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[checkNickName]", "", p_msisdn, "", " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "checkNickName", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("checkNickName", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[checkNickName]", "", p_msisdn, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "checkNickName", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("checkNickName", "Exiting with status:" + isExist);
        }// end of finally
        return isExist;
    }

    public CardDetailsVO loadAutoEnabledCard(Connection p_con, String p_userId) throws BTSLBaseException, Exception {

        if (_log.isDebugEnabled())
            _log.debug("loadAutoEnabledCard", "Entered  p_userId: " + p_userId);
        CardDetailsVO cardDetailsVO = null;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        try {
            StringBuffer queryBuffer = new StringBuffer();
            queryBuffer.append(" SELECT USER_ID, SCHEDULE_TYPE,SCHEDULE_DATE, NICK_NAME, STATUS,AMOUNT,DE_ACTIVATION_DATE ");
            queryBuffer.append(" FROM SCHEDULE_TOPUP_DETAILS WHERE USER_ID=? AND STATUS= ?  ");

            String query = queryBuffer.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadAutoEnabledCard", "Query: " + query);

            pstm = p_con.prepareStatement(query);
            pstm.setString(1, p_userId);
            pstm.setString(2, PretupsI.YES);
            rst = pstm.executeQuery();

            while (rst.next()) {
                cardDetailsVO = new CardDetailsVO();
                cardDetailsVO.setUserId(rst.getString("USER_ID"));
                cardDetailsVO.setAutoTopupStatus(rst.getString("STATUS"));
                cardDetailsVO.setCardNickName(rst.getString("NICK_NAME"));
                cardDetailsVO.setAutoTopupAmount(rst.getDouble("AMOUNT"));
                cardDetailsVO.setScheduleType(rst.getString("SCHEDULE_TYPE"));
                cardDetailsVO.setScheduledDate(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rst.getDate("SCHEDULE_DATE"))));
                cardDetailsVO.setEndDate(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rst.getDate("DE_ACTIVATION_DATE"))));
            }
        } catch (SQLException sqe) {
            _log.error("loadAutoEnabledCard", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[loadAutoEnabledCard]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadAutoEnabledCard", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error("loadAutoEnabledCard", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[loadAutoEnabledCard]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadAutoEnabledCard", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
            }
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadAutoEnabledCard", "Exiting cardDetailsVO " + cardDetailsVO);
        }
        return cardDetailsVO;

    }

    public int disableAutoTopUp(Connection p_con, String p_userId) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("disableAutoTopUp", "Entered ");

        PreparedStatement pstm = null;
        int updateCount = 0;
        int i = 1;
        try {
            // String
            // query=" update SCHEDULE_TOPUP_DETAILS set status=?,modified_on=? WHERE  user_id=? ";
            String query = " delete from SCHEDULE_TOPUP_DETAILS WHERE  user_id=? ";
            if (_log.isDebugEnabled())
                _log.debug("disableAutoTopUp", "Query: " + query);

            pstm = p_con.prepareStatement(query);
            // pstm.setString(i++,PretupsI.NO);
            // pstm.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(new
            // Date()));
            pstm.setString(i++, p_userId);
            // pstm.setString(i++, p_cardNickName);
            updateCount = pstm.executeUpdate();

        } catch (SQLException sqe) {
            _log.error("disableAutoTopUp", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[disableAutoTopUp]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "disableAutoTopUp", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error("disableAutoTopUp", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[disableAutoTopUp]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "disableAutoTopUp", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {

            }
            if (_log.isDebugEnabled())
                _log.debug("disableAutoTopUp", "Exiting isExist " + updateCount);
        }
        return updateCount;
    }

    public int enableAutoTopup(Connection p_con, String p_userId, CardDetailsVO p_cardDetailsVO, Date p_endDate) {

        if (_log.isDebugEnabled())
            _log.debug("enableAutoTopup", "Entered p_userId: " + p_userId + "Entered p_cardDetailsVO: " + p_cardDetailsVO.getCardNickName() + "p_endDate" + p_endDate);
        int count = 0;
        boolean isCardPreviouslyEnabled = false;
        PreparedStatement pstmSelect = null;
        PreparedStatement pstmUpdate = null;
        PreparedStatement pstmInsert = null;
        Date currentDate = new Date();
        p_cardDetailsVO.setCreatedOn(currentDate);
        ResultSet rst = null;

        try {
            StringBuffer selectBuffer = new StringBuffer();

            // selectBuffer.append("Select status from SCHEDULE_TOPUP_DETAILS where user_id =? and nick_name= ? ");
            selectBuffer.append("Select status from SCHEDULE_TOPUP_DETAILS where user_id =? ");
            String selectQuery = selectBuffer.toString();
            if (_log.isDebugEnabled())
                _log.debug("enableAutoTopup", "selectQuery: " + selectQuery);
            int j = 0;
            pstmSelect = p_con.prepareStatement(selectQuery);
            pstmSelect.setString(++j, p_userId);
            // pstmSelect.setString(++j,p_cardDetailsVO.getCardNickName());
            rst = pstmSelect.executeQuery();

            while (rst.next()) {
                isCardPreviouslyEnabled = true;
                p_cardDetailsVO.setAutoTopupStatus(rst.getString("STATUS"));
            }

            if (isCardPreviouslyEnabled) {
                String updateQuery = " update SCHEDULE_TOPUP_DETAILS set status=? , schedule_type=?,amount=?, modified_on=?,SCHEDULE_DATE=? ,DE_ACTIVATION_DATE=?, nick_name=? WHERE  user_id=? ";

                if (_log.isDebugEnabled())
                    _log.debug("disableAutoTopUp", "updateQuery: " + updateQuery);
                int k = 0;
                pstmUpdate = p_con.prepareStatement(updateQuery);
                pstmUpdate.setString(++k, PretupsI.YES);
                pstmUpdate.setString(++k, p_cardDetailsVO.getScheduleType());
                pstmUpdate.setDouble(++k, p_cardDetailsVO.getAutoTopupAmount());
                // pstmUpdate.setTimestamp(++k,BTSLUtil.getTimestampFromUtilDate(new
                // Date()));
                pstmUpdate.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(new Date()));
                pstmUpdate.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_cardDetailsVO.getScheduledDate())));
                pstmUpdate.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(p_endDate));
                pstmUpdate.setString(++k, p_cardDetailsVO.getCardNickName());
                pstmUpdate.setString(++k, p_userId);

                count = pstmUpdate.executeUpdate();
            } else {
                StringBuffer qryBuffer = new StringBuffer();
                qryBuffer.append(" Insert into SCHEDULE_TOPUP_DETAILS ");
                qryBuffer.append("(USER_ID, SERVICE_TYPE, SCHEDULE_TYPE, AMOUNT, CREATED_ON,  ");
                qryBuffer.append(" MODIFIED_ON, NICK_NAME, STATUS, SCHEDULE_DATE,DE_ACTIVATION_DATE) ");
                qryBuffer.append("  Values ");
                qryBuffer.append(" (?,?,?,?,?,?,?,?,?,?) ");

                String insertQuery = qryBuffer.toString();
                if (_log.isDebugEnabled())
                    _log.debug("enableAutoTopup", "insertQuery: " + insertQuery);

                pstmInsert = p_con.prepareStatement(insertQuery);
                int i = 0;
                pstmInsert.setString(++i, p_userId);
                pstmInsert.setString(++i, "SCHDATP");
                pstmInsert.setString(++i, p_cardDetailsVO.getScheduleType());
                pstmInsert.setDouble(++i, p_cardDetailsVO.getAutoTopupAmount());
                // pstmInsert.setTimestamp(++i,BTSLUtil.getTimestampFromUtilDate(p_cardDetailsVO.getCreatedOn()));
                pstmInsert.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_cardDetailsVO.getCreatedOn()));
                // pstmInsert.setTimestamp(++i,BTSLUtil.getTimestampFromUtilDate(p_cardDetailsVO.getCreatedOn()));
                pstmInsert.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_cardDetailsVO.getCreatedOn()));
                // psmt.setDate(7,
                // BTSLUtil.getSQLDateFromUtilDate(p_scheduleDate));
                pstmInsert.setString(++i, p_cardDetailsVO.getCardNickName());
                pstmInsert.setString(++i, PretupsI.YES);
                pstmInsert.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_cardDetailsVO.getScheduledDate())));
                pstmInsert.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_endDate));
                count = pstmInsert.executeUpdate();
            }

        } catch (SQLException sqe) {
            _log.error("enableAutoTopup", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[enableAutoTopup]", "", "", "", "SQL Exception:" + sqe.getMessage());
        } catch (Exception ex) {
            _log.error("enableAutoTopup", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[enableAutoTopup]", "", "", "", "Exception:" + ex.getMessage());
        } finally {
            try {
                if (pstmSelect != null) {
                    pstmSelect.close();
                }
                if (pstmUpdate != null) {
                    pstmUpdate.close();
                }
                if (pstmInsert != null) {
                    pstmInsert.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("enableAutoTopup", "Exiting count " + count);
        }
        return count;

    }

}