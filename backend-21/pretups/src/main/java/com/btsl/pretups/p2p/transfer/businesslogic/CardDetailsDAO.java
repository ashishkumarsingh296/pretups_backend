package com.btsl.pretups.p2p.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;

public class CardDetailsDAO {
    private static final Log _log = LogFactory.getLog(CardDetailsDAO.class.getName());

    /**
     * @param p_con
     *            Connection
     * @param p_CardDetailsVO
     *            CardDetailsVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addCardDetails(Connection p_con, CardDetailsVO p_CardDetailsVO, boolean updateDefaultcard) throws BTSLBaseException {
        final String methodName = "addCardDetails";
     
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_CardDetailsVO: " + p_CardDetailsVO);
        }
        int count = 0;
        
        int updateCard = -1;
        final Date currentDate = new Date();
        p_CardDetailsVO.setCreatedOn(currentDate);
        try {
            final StringBuilder qryBuffer = new StringBuilder();
            qryBuffer.append("INSERT INTO CARD_DETAILS(USER_ID, NAME_OF_EMBOSSING, ");
            qryBuffer.append("CARD_NUMBER, CARD_TYPE, CARD_NICK_NAME, BANK, EXPIRY_DATE, MSISDN, ");
            qryBuffer.append("DOB, EMAIL, ADDRESS, CREATED_ON, STATUS, ACCEPT_T_C,is_default) ");
            qryBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

            final String query = qryBuffer.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + query);
            }
            if (updateDefaultcard) {
                updateCard = this.defaultCardExistUpdate(p_con, p_CardDetailsVO.getUserId());
                if (updateCard < 0) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DEFAULT_CARD_DETAILS_UPDATION_ERROR);
                }
            }

            try(PreparedStatement pstm = p_con.prepareStatement(query);)
            {
            int i = 0;
            pstm.setString(++i, p_CardDetailsVO.getUserId());
            pstm.setString(++i, p_CardDetailsVO.getNameOfEmbossing());
            pstm.setString(++i, p_CardDetailsVO.getCardNumber());
            pstm.setString(++i, p_CardDetailsVO.getCardType());
            pstm.setString(++i, p_CardDetailsVO.getCardNickName().toUpperCase());
            pstm.setString(++i, p_CardDetailsVO.getBankName());
            pstm.setString(++i, p_CardDetailsVO.getExpiryDate());
            pstm.setString(++i, p_CardDetailsVO.getMsisdn());
            pstm.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_CardDetailsVO.getDateOfBirth()));
            pstm.setString(++i, p_CardDetailsVO.getEmail());
            pstm.setString(++i, p_CardDetailsVO.getAddress());
            pstm.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_CardDetailsVO.getCreatedOn()));
            pstm.setString(++i, "Y");
            pstm.setString(++i, "Y");
            pstm.setString(++i, p_CardDetailsVO.getIsDefault());

            count = pstm.executeUpdate();
            if (count <= 0) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CARD_DETAILS_INSERTION_ERROR);
            }

        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[addCardDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CARD_DETAILS_INSERTION_ERROR);
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[addCardDetails]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CARD_DETAILS_INSERTION_ERROR);
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting count " + count);
            }
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
        final String methodName = "isCardNumberAlreadyRegistered";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_cardNo " + p_cardNo);
        }
        boolean isExist = false;
         
        try {
            final String query = " SELECT 1 FROM CARD_DETAILS WHERE  CARD_NUMBER=? AND STATUS='Y' ";

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + query);
            }

            try(PreparedStatement pstm = p_con.prepareStatement(query);)
            {
            pstm.setString(1, p_cardNo);
           try( ResultSet rst = pstm.executeQuery();)
           {

            if (rst.next()) {
                isExist = true;
            }
        }
            }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isCardNumberAlreadyRegistered]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isCardNumberAlreadyRegistered]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExist " + isExist);
            }
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
    public boolean isNickNameAlreadyRegistered(Connection p_con, String p_nickName) throws BTSLBaseException {
        final String methodName = "isNickNameAlreadyRegistered";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_nickName " + p_nickName);
        }
        boolean isExist = false;
         
        

        try {
            final String query = " SELECT 1 FROM CARD_DETAILS WHERE  UPPER(CARD_NICK_NAME)=? AND STATUS='Y' ";

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + query);
            }

            try(PreparedStatement pstm = p_con.prepareStatement(query);)
            {
            pstm.setString(1, p_nickName.toUpperCase());
            try(ResultSet rst = pstm.executeQuery();)
            {
            if (rst.next()) {
                isExist = true;
            }
        }
            }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isNickNameAlreadyRegistered]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isNickNameAlreadyRegistered]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExist " + isExist);
            }
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
        final String methodName = "validateNickName";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_nickName " + p_nickName + " p_senderId: " + p_senderId);
        }
        boolean isExist = false;
        
        
        try {
            final String query = " SELECT 1 FROM CARD_DETAILS WHERE  UPPER(CARD_NICK_NAME)=? AND STATUS='Y' AND USER_ID=? ";

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + query);
            }

           try(PreparedStatement pstm = p_con.prepareStatement(query);)
           {
            pstm.setString(1, p_nickName.toUpperCase());
            pstm.setString(2, p_senderId);
            try(ResultSet rst = pstm.executeQuery();)
            {
            if (rst.next()) {
                isExist = true;
            }
        }
           }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isNickNameAlreadyRegistered]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error("isNickNameAlreadyRegistered", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isNickNameAlreadyRegistered]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
        	
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExist " + isExist);
            }
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
        final String methodName = "loadCredtCardDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_nickName " + p_nickName + " p_senderId: " + p_senderId);
        }
        CardDetailsVO cardDetailsVO = null;
        
        try {
            final StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(" SELECT USER_ID, NAME_OF_EMBOSSING, CARD_NUMBER, CARD_TYPE, ");
            queryBuffer.append(" CARD_NICK_NAME, BANK, EXPIRY_DATE, MSISDN, DOB, EMAIL, ADDRESS, CREATED_ON, STATUS, ACCEPT_T_C,IS_DEFAULT ");
            queryBuffer.append(" FROM CARD_DETAILS WHERE USER_ID=? AND CARD_NICK_NAME=? AND STATUS=? ");

            final String query = queryBuffer.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + query);
            }

            try(PreparedStatement pstm = p_con.prepareStatement(query);)
            {
            pstm.setString(1, p_senderId);
            pstm.setString(2, p_nickName);
            pstm.setString(3, PretupsI.YES);
            try(ResultSet rst = pstm.executeQuery();)
            {

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
        } 
            }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[loadCredtCardDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[loadCredtCardDetails]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting cardDetailsVO " + cardDetailsVO);
            }
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
        final String methodName = "loadDefaultCredtCardDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered  p_senderId: " + p_senderId);
        }
        CardDetailsVO cardDetailsVO = null;
        
        try {
            final StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(" SELECT USER_ID, NAME_OF_EMBOSSING, CARD_NUMBER, CARD_TYPE, ");
            queryBuffer.append(" CARD_NICK_NAME, BANK, EXPIRY_DATE, MSISDN, DOB, EMAIL, ADDRESS, CREATED_ON, STATUS, ACCEPT_T_C,IS_DEFAULT ");
            queryBuffer.append(" FROM CARD_DETAILS WHERE USER_ID=? AND STATUS=? AND IS_DEFAULT=? ");

            final String query = queryBuffer.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + query);
            }

            try(PreparedStatement pstm = p_con.prepareStatement(query);)
            {
            pstm.setString(1, p_senderId);
            pstm.setString(2, PretupsI.YES);
            pstm.setString(3, PretupsI.YES);
            try( ResultSet  rst = pstm.executeQuery();)
            {

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
        } 
            }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[loadDefaultCredtCardDetails]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[loadDefaultCredtCardDetails]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting cardDetailsVO " + cardDetailsVO);
            }
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
        final String methodName = "loadSubscriberRegisteredCardDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userId " + p_userId);
        }

         
        ArrayList cardDetailList = null;
        CardDetailsVO p_cardVO = null;
        int i = 1;
        int index = 0;
        try {
            cardDetailList = new ArrayList();
            final StringBuilder sbf = new StringBuilder();
            sbf.append(" SELECT NAME_OF_EMBOSSING,CARD_NUMBER,CARD_TYPE,CARD_NICK_NAME,BANK,EXPIRY_DATE,ACCEPT_T_C,CREATED_ON, ");
            sbf.append(" MSISDN,DOB,EMAIL,ADDRESS,STATUS,is_default FROM CARD_DETAILS WHERE USER_ID= ? and status='Y'");

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + sbf.toString());
            }

            try(PreparedStatement pstm = p_con.prepareStatement(sbf.toString());)
            {
            pstm.setString(i++, p_userId);
            try(ResultSet rst = pstm.executeQuery();)
            {
            while (rst.next()) {
                p_cardVO = new CardDetailsVO();
                p_cardVO.setNameOfEmbossing(BTSLUtil.decryptText(rst.getString("NAME_OF_EMBOSSING")));
                p_cardVO.setAcceptTC(rst.getString("ACCEPT_T_C"));
                p_cardVO.setAddress(rst.getString("ADDRESS"));
                p_cardVO.setBankName(rst.getString("BANK"));
                p_cardVO.setCardNickName(rst.getString("CARD_NICK_NAME"));
                p_cardVO.setCardNumber(BTSLUtil.decryptText(rst.getString("CARD_NUMBER")));
                p_cardVO.setCardType(rst.getString("CARD_TYPE"));
                p_cardVO.setCreatedOn(BTSLUtil.getUtilDateFromSQLDate(rst.getDate("CREATED_ON")));
                p_cardVO.setMsisdn(rst.getString("MSISDN"));
                p_cardVO.setDateOfBirth(BTSLUtil.getUtilDateFromSQLDate(rst.getDate("DOB")));
                p_cardVO.setEmail(rst.getString("EMAIL"));
                p_cardVO.setStatus(rst.getString("STATUS"));
                String expiryDate = BTSLUtil.decryptText(rst.getString("EXPIRY_DATE"));
                if (expiryDate.indexOf("/") == -1) {
                    expiryDate = convertDate(expiryDate);
                }
                p_cardVO.setExpiryDate(expiryDate);
                p_cardVO.setIsDefault(rst.getString("IS_DEFAULT"));
                p_cardVO.setOriginalCardNumber(p_cardVO.getCardNumber());
                p_cardVO.setRadioIndex(index);

                String var = "x";
                String var1 = null;
                int k;
                int cardNumber=(BTSLUtil.decryptText(rst.getString("CARD_NUMBER")).length()) - 5;
                for (k = 0; k <= cardNumber; k++) {
                    var = var + 'x';
                }

                var1 = (BTSLUtil.decryptText(rst.getString("CARD_NUMBER"))).substring(k);

                var = var.concat(var1);

                p_cardVO.setDisplayCardNumber(var);

                cardDetailList.add(p_cardVO);
                index++;
            }

        } 
            }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[loadSubscriberRegisteredCardDetails]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[loadSubscriberRegisteredCardDetails]",
                "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting cardDetailList " + cardDetailList.size());
            }
        }
        return cardDetailList;
    }

    private String convertDate(String expiryDate) {
        final StringBuilder stringBuilder = new StringBuilder();
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
        final String methodName = "updateUserCardDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }

        
        int updateCount = 0;
        int i = 1;
        try {
            final String query = " update CARD_DETAILS set DOB=?, EMAIL=?, ADDRESS=?, is_default=? WHERE  user_id=? and card_number=? ";

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + query);
            }

            if (updateDefaultCard) {
                final int updateCard = this.defaultCardExistUpdate(p_con, p_cardVO.getUserId());
                if (updateCard < 0) {
                    throw new BTSLBaseException(this, "addCardDetails", PretupsErrorCodesI.DEFAULT_CARD_DETAILS_UPDATION_ERROR);
                }
            }
            try( PreparedStatement pstm = p_con.prepareStatement(query);)
            {
            pstm.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_cardVO.getDateOfBirth()));
            pstm.setString(i++, p_cardVO.getEmail());
            pstm.setString(i++, p_cardVO.getAddress());
            pstm.setString(i++, p_cardVO.getIsDefault());
            pstm.setString(i++, p_cardVO.getUserId());
            pstm.setString(i++, BTSLUtil.encryptAES(p_cardVO.getOriginalCardNumber()));

            updateCount = pstm.executeUpdate();

        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[updateUserCardDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[updateUserCardDetails]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExist " + updateCount);
            }
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
        final String methodName = "isDefaultCardExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }

       
        boolean selectCount = false;
        int i = 1;
        try {
            final String query = " select 1 from CARD_DETAILS WHERE user_id=? and card_number=? and is_default='Y' and status='Y'";

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + query);
            }

            try( PreparedStatement pstm = p_con.prepareStatement(query);)
            {
            pstm.setString(i++, p_cardVO.getUserId());
            pstm.setString(i++, BTSLUtil.encryptAES(p_cardVO.getOriginalCardNumber()));

            try(ResultSet rst = pstm.executeQuery();)
            {
            while (rst.next()) {
                selectCount = true;
            }
            }
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isDefaultCardExist]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isDefaultCardExist]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExist " + selectCount);
            }
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
        final String methodName = "deleteCardDetail";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }

      
        int deleteCount = -1;
        int i = 1;
        try {
            final String query = " update CARD_DETAILS set status=? WHERE user_id=? and card_number=? ";

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + query);
            }

            try(PreparedStatement pstm = p_con.prepareStatement(query);)
            {
            pstm.setString(i++, PretupsI.NO);
            pstm.setString(i++, p_cardVO.getUserId());
            pstm.setString(i++, BTSLUtil.encryptAES(p_cardVO.getOriginalCardNumber()));

            deleteCount = pstm.executeUpdate();

        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[deleteCardDetail]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[deleteCardDetail]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
        
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting deleted count " + deleteCount);
            }
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
        final String methodName = "defaultCardExistUpdate";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }

       
        PreparedStatement updateStmt = null;
        
        String cardNumber = "";
        int i = 1, j = 1, updateCount = -1;
        try {
            final String query = "select card_number from CARD_DETAILS WHERE user_id=? and is_default='Y' and status='Y'";
            final String updateQuery = "update CARD_DETAILS set is_default=? WHERE user_id=? and card_number=? and status='Y' ";

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + query + ",  updateQuery=" + updateQuery);
            }

            try(PreparedStatement pstm = p_con.prepareStatement(query);)
            {
            pstm.setString(i++, p_userId);

            try(ResultSet rst = pstm.executeQuery();)
            {
            while (rst.next()) {
                cardNumber = BTSLUtil.decryptAES(rst.getString("card_number"));
            }

            if (!(BTSLUtil.isNullString(cardNumber)) && !("").equals(cardNumber) && cardNumber.length() > 0) {
                updateStmt = p_con.prepareStatement(updateQuery);
                updateStmt.setString(j++, PretupsI.NO);
                updateStmt.setString(j++, p_userId);
                updateStmt.setString(j++, BTSLUtil.encryptAES(cardNumber));
                updateCount = updateStmt.executeUpdate();
                if (updateCount < 0) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
                }
            } else {
                updateCount = 1;
            }

        }
            }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[defaultCardExistUpdate]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[defaultCardExistUpdate]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
        	
        	try{
                if (updateStmt!= null){
                	updateStmt.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting updateCount " + updateCount);
            }
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
        final String methodName = "isFirstCardNumber";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered user_id " + user_id);
        }
        boolean isExist = true;
        
        try {
            final String query = " SELECT 1 FROM CARD_DETAILS WHERE user_id=? ";

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + query);
            }

          try(PreparedStatement  pstm = p_con.prepareStatement(query);)
          {
            pstm.setString(1, user_id);
            try(ResultSet rst = pstm.executeQuery();)
            {

            if (rst.next()) {
                isExist = false;
            }
        }
          }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isFirstCardNumber]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error("isCardNumberAlreadyRegistered", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[isFirstCardNumber]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExist " + isExist);
            }
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
        final String methodName = "getSubscriberKey";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered msisdn " + Msisdn);
        }
        String key = null;
        
        try {
            final String query = " SELECT ENCRYPTION_KEY FROM P2P_SUBSCRIBERS WHERE MSISDN=? ";

            if (_log.isDebugEnabled()) {
                _log.debug("isFirstCardNumber", "Query: " + query);
            }

           try(PreparedStatement pstm = p_con.prepareStatement(query);)
           {
            pstm.setString(1, Msisdn);
            try(ResultSet rst = pstm.executeQuery();)
            {
            if (rst.next()) {
                key = rst.getString("ENCRYPTION_KEY");
            }

        } 
           }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[getSubscriberKey]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardDetailsDAO[getSubscriberKey]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExist " + key);
            }
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
        final String methodName = "updateNick";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_msisdn:" + p_msisdn + "p_imei:" + p_imei + "p_newNickName:" + p_newNickName + "p_userid:" + p_userID);
        }
        boolean exist = false;
        
        int updateCount = 0;
        try {
            final StringBuilder updateQueryBuff = new StringBuilder("update card_details set card_nick_name = ? where status = ? and card_nick_name= ? ");
            updateQueryBuff.append(" and user_id= ? and msisdn = ?");
            final String updateQuery = updateQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "update query:" + updateQuery);
            }
            try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
            {
            pstmtUpdate.setString(1, p_newNickName);
            pstmtUpdate.setString(2, PretupsI.YES);
            pstmtUpdate.setString(3, p_nickName);
            pstmtUpdate.setString(4, p_userID);
            pstmtUpdate.setString(5, p_msisdn);
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0) {
                exist = true;
            }

        }
        }catch (SQLException sqle) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (SQLException e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateNick]", "", p_msisdn, "",
                " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (SQLException se) {
                _log.errorTrace(methodName, se);
            }
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateNick]", "", p_msisdn, "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        
            try {
                if (p_con != null) {
                    p_con.commit();
                }
            } catch (SQLException se) {
                _log.errorTrace(methodName, se);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting with status:" + exist);
            }
        }// end of finally

        return exist;

    }

    public boolean deleteCardDetails(Connection p_con, String p_nickName, String p_msisdn, String p_userId) throws BTSLBaseException, Exception {
        final String methodName = "deleteCardDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered  :: nick_name: " + p_nickName + "p_msisdn: " + p_msisdn + "p_userid: " + p_userId);
        }
        boolean isDeleted = false;
     
        int updateCount = 0;
        try {
            final StringBuilder sbf = new StringBuilder("DELETE FROM CARD_DETAILS WHERE CARD_NICK_NAME= ? AND MSISDN= ? AND USER_ID= ?");
            final String deleteQuery = sbf.toString();
           try( PreparedStatement  psmtDeleteCardDetails = p_con.prepareStatement(deleteQuery);)
           {
            psmtDeleteCardDetails.setString(1, p_nickName);
            psmtDeleteCardDetails.setString(2, p_msisdn);
            psmtDeleteCardDetails.setString(3, p_userId);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "delete query:" + deleteQuery);
            }
            updateCount = psmtDeleteCardDetails.executeUpdate();
            if (updateCount <= 0) {
                psmtDeleteCardDetails.clearParameters();
                p_con.rollback();
                isDeleted = false;
            } else {
                isDeleted = true;
            }
        }
        }

        catch (SQLException sqle) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (SQLException e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteNickName]", "", p_nickName, "",
                " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (SQLException se) {
                _log.errorTrace(methodName, se);
            }
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteNickName]", "", p_nickName, "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (p_con != null) {
                    p_con.commit();
                }
            } catch (SQLException se) {
                _log.errorTrace(methodName, se);
            }
           
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting with status:" + isDeleted);
            }
        }
        return isDeleted;
    }

    public boolean checkNickName(Connection p_con, String p_msisdn, String p_imei, String p_nickName, String p_userID) throws BTSLBaseException, Exception {
        boolean isExist = false;
        final String methodName = "checkNickName";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_msisdn: " + p_msisdn + "p_imei: " + p_imei + "p_nick: " + p_nickName + "p_userID: " + p_userID);
        }
      
        try {
            final StringBuilder selectQueryBuff = new StringBuilder("SELECT 1 from card_details where card_nick_name = ? and msisdn = ? and status = ? and user_id = ?");
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
           try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
           {
            pstmtSelect.setString(1, p_nickName);
            pstmtSelect.setString(2, p_msisdn);
            pstmtSelect.setString(3, PretupsI.YES);
            pstmtSelect.setString(4, p_userID);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                isExist = true;
                return isExist;
            }
        }
           }
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[checkNickName]", "", p_msisdn, "",
                " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[checkNickName]", "", p_msisdn, "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting with status:" + isExist);
            }
        }// end of finally
        return isExist;
    }
}
