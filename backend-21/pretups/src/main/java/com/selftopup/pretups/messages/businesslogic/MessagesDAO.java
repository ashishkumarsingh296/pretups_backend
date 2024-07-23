/*
 * @# MessagesDAO.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Chhaya Sikheria Sep 29, 2011 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2011 Comviva Ltd.
 */
package com.selftopup.pretups.messages.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.OracleUtil;

public class MessagesDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Description:This method perform following action
     * Load the messages from DB and set messages into cache.
     * Select the language_code based on the language.
     * Based on the language_code and language fetch the message from the
     * MESSAGES_MASTER and create map for cache
     * 
     * @param localLng
     *            - This parameter value based on Local
     * @return Map<String,Object>
     * @throws BTSLBaseException
     */
    public Map<String, Object> loadMessageByLocale(String p_localLng) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadMessageByLocale", "Entered ");
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtlocaleSelect = null;
        Connection con = null;
        ResultSet rs = null;
        ResultSet localeRs = null;

        Map<String, Object> messagesMap = null;
        Map<String, String> message = null;

        String langCode = null;

        try {
            con = OracleUtil.getSingleConnection();

            String localeSelectQry = "SELECT language_code FROM locale_master where language = ?";

            if (_log.isDebugEnabled()) {
                _log.debug("loadMessageByLocale", "Select Query for locale= " + localeSelectQry);
            }

            pstmtlocaleSelect = con.prepareStatement(localeSelectQry);
            pstmtlocaleSelect.setString(1, p_localLng);
            localeRs = pstmtlocaleSelect.executeQuery();

            while (localeRs.next()) {

                langCode = String.valueOf(localeRs.getInt("language_code"));
            }

            if (_log.isDebugEnabled()) {
                _log.debug("loadMessageByLocale", "Locale Master language code=: " + langCode);
            }

            StringBuffer selectQueryBuff = new StringBuffer(" SELECT message_code, ");

            /*
             * if(langCode.equalsIgnoreCase("0") &&
             * p_localLng.equalsIgnoreCase("en")){
             * selectQueryBuff.append(" message1 ");
             * }else if(langCode.equalsIgnoreCase("1")){
             * selectQueryBuff.append(" message2 ");
             * }else{
             * selectQueryBuff.append(" message3 ");
             * }
             */

            if (langCode.equalsIgnoreCase("0") && p_localLng.equalsIgnoreCase("en")) {
                selectQueryBuff.append(" message1 "); // /English
            } else if (langCode.equalsIgnoreCase("1")) {
                selectQueryBuff.append(" message2 "); // Amrahic
            } else if (langCode.equalsIgnoreCase("2")) // Oromiffa
            {
                selectQueryBuff.append(" message3 ");
            } else if (langCode.equalsIgnoreCase("3")) // Somali
            {
                selectQueryBuff.append(" message4 ");
            } else if (langCode.equalsIgnoreCase("4")) // Tigrigna
            {
                selectQueryBuff.append(" message5 ");
            }

            selectQueryBuff.append(" message ");

            selectQueryBuff.append(" FROM MESSAGES_MASTER  ");

            String selectQuery = selectQueryBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("loadMessageByLocale", "Select Query= " + selectQuery);
            }

            pstmtSelect = con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();

            messagesMap = new HashMap<String, Object>();
            message = new HashMap<String, String>();

            while (rs.next()) {
                // Changed By Diwakar on 08-May-2014 for OCM
                // message.put(rs.getString("message_code"),
                // rs.getString("message"));
                message.put(rs.getString("message_code"), rs.getString("default_message"));
                // Ended Here
            }
            messagesMap.put(p_localLng, message);

        } catch (SQLException sqle) {
            // TODO: handle exception
            _log.error("loadMessageByLocale", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw new BTSLBaseException(this, "loadMessagesMap", "error.general.sql.processing");
        }// end of sql catch
        catch (Exception e) {
            // TODO: handle exception
            _log.error("loadMessageByLocale", "Exception " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(this, "loadMessageByLocale", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (localeRs != null)
                    localeRs.close();
            } catch (Exception e) {
            }
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtlocaleSelect != null)
                    pstmtlocaleSelect.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadMessageByLocale", "Exiting loadMessagesMap.size:");
        }// end of finally

        return messagesMap;
    }

    /**
     * Description: This method update the messages into the database.
     * This method is call by Action for update the messages.
     * 
     * @param p_mlist
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean updateMessages(Connection p_con, List<MessagesVO> p_mlist) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateMessageWithArgs", "Entered ");
        }
        PreparedStatement pstmtUpdate = null;
        MessagesVO messagesVO = null;
        int updateCount = 0;
        boolean flag = false;

        try {

            StringBuffer selectQueryBuff = new StringBuffer(" UPDATE MESSAGES_MASTER SET network_code=?,");
            selectQueryBuff.append(" message1=?,message2=?,message3=?,message4=?,message5=? WHERE message_code=?");

            String selectQuery = selectQueryBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("updateMessageWithArgs", "Select Query= " + selectQuery);
            }

            pstmtUpdate = p_con.prepareStatement(selectQuery);

            Iterator<MessagesVO> iterator = p_mlist.iterator();

            while (iterator.hasNext()) {

                messagesVO = iterator.next();

                pstmtUpdate.setString(1, messagesVO.getNetworkCode());
                pstmtUpdate.setString(2, messagesVO.getMessage1());
                pstmtUpdate.setString(3, messagesVO.getMessage2());
                pstmtUpdate.setString(4, messagesVO.getMessage3());
                pstmtUpdate.setString(5, messagesVO.getMessage4());
                pstmtUpdate.setString(6, messagesVO.getMessage5());

                pstmtUpdate.setString(7, messagesVO.getMessageCode());

                updateCount = pstmtUpdate.executeUpdate();

                if (updateCount > 0) {
                    flag = true;

                } else {
                    // roll back all the changes
                    p_con.rollback();
                    throw new BTSLBaseException(this, "updateMessageWithArgs", "error.general.sql.processing");
                }
            }
        } catch (SQLException sqle) {
            // TODO: handle exception
            _log.error("updateMessageWithArgs", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw new BTSLBaseException(this, "updateMessageWithArgs", "error.general.sql.processing");
        }// end of sql catch
        catch (Exception e) {
            // TODO: handle exception
            _log.error("updateMessageWithArgs", "Exception " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(this, "updateMessageWithArgs", "error.general.processing");
        }// end of catch
        finally {

            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("updateMessageWithArgs", "Exiting updateMessageWithArgs");
            }
        }// end of finally

        return flag;
    }

}
