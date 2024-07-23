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
package com.btsl.pretups.messages.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.util.OracleUtil;
import com.restapi.networkadmin.messagemanagement.requestVO.MessageRequestVO;
//import org.apache.xpath.operationsowasp.Bool;

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
        if (_log.isDebugEnabled()) {
            _log.debug("loadMessageByLocale", "Entered ");
        }
        final String METHOD_NAME = "loadMessageByLocale";
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

            StringBuilder selectQueryBuff = new StringBuilder(" SELECT message_code, ");

            

            if ("0".equalsIgnoreCase(langCode) && "en".equalsIgnoreCase(p_localLng)) {
                selectQueryBuff.append(" message1 "); // /English
            } else if ("1".equalsIgnoreCase(langCode)) {
                selectQueryBuff.append(" message2 "); // Amrahic
            } else if ("2".equalsIgnoreCase(langCode)) // Oromiffa
            {
                selectQueryBuff.append(" message3 ");
            } else if ("3".equalsIgnoreCase(langCode)) // Somali
            {
                selectQueryBuff.append(" message4 ");
            } else if ("4".equalsIgnoreCase(langCode)) // Tigrigna
            {
                selectQueryBuff.append(" message5 ");
            } else {
                selectQueryBuff.append(" default_message ");
            }

            selectQueryBuff.append(" message ");

            selectQueryBuff.append(" FROM MESSAGES_MASTER  ");

            String selectQuery = selectQueryBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("loadMessageByLocale", "Select Query= " + selectQuery);
            }

            pstmtSelect = con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();

            messagesMap = new HashMap<>();
            message = new HashMap<>();

            while (rs.next()) {
                // Changed By Diwakar on 08-May-2014 for OCM
                message.put(rs.getString("message_code"), rs.getString("message"));
               
            }
            messagesMap.put(p_localLng, message);

        } catch (SQLException sqle) {
            // TODO: handle exception
            _log.error("loadMessageByLocale", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, "loadMessagesMap", "error.general.sql.processing");
        }// end of sql catch
        catch (Exception e) {
            // TODO: handle exception
            _log.error("loadMessageByLocale", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "loadMessageByLocale", "error.general.processing");
        }// end of catch
        finally {
        	try{
        		if (localeRs!= null){
        			localeRs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtlocaleSelect!= null){
        			pstmtlocaleSelect.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
        	OracleUtil.closeQuietly(con);           
            if (_log.isDebugEnabled()) {
                _log.debug("loadMessageByLocale", "Exiting loadMessagesMap.size:");
            }
        }// end of finally

        return messagesMap;
    }

    /**
     * Description: This method fetch the message list with messages arguments
     * This method is call by Action for load the messages.
     * 
     * @return Map<String,MessagesVO>
     * @throws BTSLBaseException
     */
    public Map<String, MessagesVO> loadMessageWithArgs(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadMessageWithArgs", "Entered ");
        }
        final String METHOD_NAME = "loadMessageWithArgs";
        PreparedStatement pstmtMessages = null;
        PreparedStatement pstmtArgument = null;

        ResultSet rsMessages = null;
        ResultSet rsArgument = null;

        MessagesVO messagesVO = null;
        MessageArgumentVO argumentVO = null;

        Map<String, MessagesVO> messagesMap = null;
        List<MessageArgumentVO> argsList = null;
        String messageCode = null;

        try {

            /* Fetch data from MESSAGES_MASTER query */
            StringBuilder messagesQueryBuff = new StringBuilder(" SELECT message_type,message_code,default_message,network_code,");
            messagesQueryBuff.append(" message1,message2,message3,message4,message5,mclass,description FROM MESSAGES_MASTER  ");

            String messagesQuery = messagesQueryBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("loadMessageWithArgs", "Select MESSAGES_MASTER Query= " + messagesQuery);
            }
            /* MESSAGES_MASTER End */

            /* Fetch data from MESSAGE_ARGUMENT query */
            StringBuilder argumentQueryBuff = new StringBuilder(" SELECT argument,argument_description");
            argumentQueryBuff.append(" FROM MESSAGE_ARGUMENT where message_code =?  ");

            String argumentQuery = argumentQueryBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("loadMessageWithArgs", "Select MESSAGE_ARGUMENT Query= " + argumentQuery);
            }
            pstmtArgument = p_con.prepareStatement(argumentQuery);

            /* MESSAGE_ARGUMENT End */

            pstmtMessages = p_con.prepareStatement(messagesQuery);
            rsMessages = pstmtMessages.executeQuery();

            messagesMap = new HashMap<>();

            while (rsMessages.next()) {
                messagesVO = new MessagesVO();

                messageCode = rsMessages.getString("message_code");

                messagesVO.setMessageType(rsMessages.getString("message_type"));
                messagesVO.setMessageCode(messageCode);
                messagesVO.setDefaultMessage(rsMessages.getString("default_message"));
                messagesVO.setNetworkCode(rsMessages.getString("network_code"));
                messagesVO.setMessage1(rsMessages.getString("message1"));
                messagesVO.setMessage2(rsMessages.getString("message2"));
                messagesVO.setMessage3(rsMessages.getString("message3"));
                messagesVO.setMessage4(rsMessages.getString("message4"));
                messagesVO.setMessage5(rsMessages.getString("message5"));
                messagesVO.setMclass(rsMessages.getString("mclass"));
                messagesVO.setDescription(rsMessages.getString("description"));

                // fetch the list of arguments based on the message_code

                pstmtArgument.setString(1, messageCode);
                rsArgument = pstmtArgument.executeQuery();

                argsList = new ArrayList<>();

                while (rsArgument.next()) {
                    argumentVO = new MessageArgumentVO();

                    argumentVO.setMessageCode(messageCode);
                    argumentVO.setArgument(rsArgument.getString("argument"));
                    argumentVO.setArgumentDesc(rsArgument.getString("argument_description"));

                    argsList.add(argumentVO);
                }
                messagesVO.setArgumentList(argsList);

                // fill the map with message_code as key and messageVO as value
                messagesMap.put(messagesVO.getMessageCode(), messagesVO);

                // clearing query parameter
                pstmtArgument.clearParameters();
            }

        } catch (SQLException sqle) {
            // TODO: handle exception
            _log.error("loadMessageWithArgs", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, "loadMessageWithArgs", "error.general.sql.processing");
        }// end of sql catch
        catch (Exception e) {
            // TODO: handle exception
            _log.error("loadMessageWithArgs", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "loadMessageWithArgs", "error.general.processing");
        }// end of catch
        finally {
        	try{
            	if (rsMessages!= null){
            		rsMessages.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing result set.", e);
            }
        	try{
        		if (pstmtMessages!= null){
        			pstmtMessages.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (rsArgument!= null){
            		rsArgument.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing result set.", e);
            }
        	try{
        		if (pstmtArgument!= null){
        			pstmtArgument.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
            if (_log.isDebugEnabled()) {
                _log.debug("loadMessageWithArgs", "Exiting loadMessageWithArgs.size:");
            }
        }// end of finally

        return messagesMap;
    }

    /**
     * Description: This method fetch the message list with messages arguments
     * This method is call by Action for load the messages.
     *
     * @return Map<String,MessagesVO>
     * @throws BTSLBaseException
     */
    public Map<String, MessagesVO> loadMessageWithArgsForNetwork(Connection con, String networkCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadMessageWithArgsForNetwork", "Entered ");
        }
        final String METHOD_NAME = "loadMessageWithArgsForNetwork";
        PreparedStatement pstmtMessages = null;
        PreparedStatement pstmtArgument = null;

        ResultSet rsMessages = null;
        ResultSet rsArgument = null;

        MessagesVO messagesVO = null;
        MessageArgumentVO argumentVO = null;

        Map<String, MessagesVO> messagesMap = null;
        List<MessageArgumentVO> argsList = null;
        String messageCode = null;

        try {

            /* Fetch data from MESSAGES_MASTER query */
            StringBuilder messagesQueryBuff = new StringBuilder(" SELECT message_type,message_code,default_message,network_code,");
            messagesQueryBuff.append(" message1,message2,message3,message4,message5,mclass,description FROM MESSAGES_MASTER  where network_code = ?");

            String messagesQuery = messagesQueryBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("loadMessageWithArgs", "Select MESSAGES_MASTER Query= " + messagesQuery);
            }
            /* MESSAGES_MASTER End */

            /* Fetch data from MESSAGE_ARGUMENT query */
            StringBuilder argumentQueryBuff = new StringBuilder(" SELECT argument,argument_description");
            argumentQueryBuff.append(" FROM MESSAGE_ARGUMENT where message_code =?  ");

            String argumentQuery = argumentQueryBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("loadMessageWithArgs", "Select MESSAGE_ARGUMENT Query= " + argumentQuery);
            }
            pstmtArgument = con.prepareStatement(argumentQuery);

            /* MESSAGE_ARGUMENT End */

            pstmtMessages = con.prepareStatement(messagesQuery);
            pstmtMessages.setString(1, networkCode);
            rsMessages = pstmtMessages.executeQuery();

            messagesMap = new HashMap<>();

            while (rsMessages.next()) {
                messagesVO = new MessagesVO();

                messageCode = rsMessages.getString("message_code");

                messagesVO.setMessageType(rsMessages.getString("message_type"));
                messagesVO.setMessageCode(messageCode);
                messagesVO.setDefaultMessage(rsMessages.getString("default_message"));
                messagesVO.setNetworkCode(rsMessages.getString("network_code"));
                messagesVO.setMessage1(rsMessages.getString("message1"));
                messagesVO.setMessage2(rsMessages.getString("message2"));
                messagesVO.setMessage3(rsMessages.getString("message3"));
                messagesVO.setMessage4(rsMessages.getString("message4"));
                messagesVO.setMessage5(rsMessages.getString("message5"));
                messagesVO.setMclass(rsMessages.getString("mclass"));
                messagesVO.setDescription(rsMessages.getString("description"));

                // fetch the list of arguments based on the message_code

                pstmtArgument.setString(1, messageCode);
                rsArgument = pstmtArgument.executeQuery();

                argsList = new ArrayList<>();

                while (rsArgument.next()) {
                    argumentVO = new MessageArgumentVO();

                    argumentVO.setMessageCode(messageCode);
                    argumentVO.setArgument(rsArgument.getString("argument"));
                    argumentVO.setArgumentDesc(rsArgument.getString("argument_description"));

                    argsList.add(argumentVO);
                }
                messagesVO.setArgumentList(argsList);

                // fill the map with message_code as key and messageVO as value
                messagesMap.put(messagesVO.getMessageCode(), messagesVO);

                // clearing query parameter
                pstmtArgument.clearParameters();
            }

        } catch (SQLException sqle) {
            // TODO: handle exception
            _log.error("loadMessageWithArgs", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
        }// end of sql catch
        catch (Exception e) {
            // TODO: handle exception
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
        }// end of catch
        finally {
            try{
                if (rsMessages!= null){
                    rsMessages.close();
                }
            }
            catch (SQLException e){
                _log.error("An error occurred closing result set.", e);
            }
            try{
                if (pstmtMessages!= null){
                    pstmtMessages.close();
                }
            }
            catch (SQLException e){
                _log.error("An error occurred closing result set.", e);
            }
            try{
                if (rsArgument!= null){
                    rsArgument.close();
                }
            }
            catch (SQLException e){
                _log.error("An error occurred closing result set.", e);
            }
            try{
                if (pstmtArgument!= null){
                    pstmtArgument.close();
                }
            }
            catch (SQLException e){
                _log.error("An error occurred closing result set.", e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting loadMessageWithArgs.size:");
            }
        }// end of finally

        return messagesMap;
    }


    /**
     * Description: This method fetch the message list with messages arguments
     * This method is call by Action for load the messages.
     * 
     * @return Map<String,MessagesVO>
     * @throws BTSLBaseException
     */
    public MessagesVO loadMessageWithArgs(Connection p_con, String messageCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadMessageWithArgs", "Entered ");
        }
        final String METHOD_NAME = "loadMessageWithArgs";
        PreparedStatement pstmtMessages = null;
        PreparedStatement pstmtArgument = null;

        ResultSet rsMessages = null;
        ResultSet rsArgument = null;

        MessagesVO messagesVO = null;
        MessageArgumentVO argumentVO = null;
        List<MessageArgumentVO> argsList = null;

        try {

            /* Fetch data from MESSAGES_MASTER query */
            StringBuilder messagesQueryBuff = new StringBuilder(" SELECT message_type,message_code,default_message,network_code,");
            messagesQueryBuff.append(" message1,message2,message3,message4,message5,mclass,description FROM MESSAGES_MASTER WHERE message_code = ? ");

            /* MESSAGES_MASTER End */

            /* Fetch data from MESSAGE_ARGUMENT query */
            StringBuilder argumentQueryBuff = new StringBuilder(" SELECT argument,argument_description");
            argumentQueryBuff.append(" FROM MESSAGE_ARGUMENT where message_code =?  ");

            String argumentQuery = argumentQueryBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("loadMessageWithArgs", "Select MESSAGE_ARGUMENT Query= " + argumentQuery);
            }
            pstmtArgument = p_con.prepareStatement(argumentQuery);

            /* MESSAGE_ARGUMENT End */

            pstmtMessages = p_con.prepareStatement(messagesQueryBuff.toString());
            pstmtMessages.setString(1, messageCode);
            rsMessages = pstmtMessages.executeQuery();


            if(rsMessages.next()) {
                messagesVO = new MessagesVO();

                messageCode = rsMessages.getString("message_code");

                messagesVO.setMessageType(rsMessages.getString("message_type"));
                messagesVO.setMessageCode(messageCode);
                messagesVO.setDefaultMessage(rsMessages.getString("default_message"));
                messagesVO.setNetworkCode(rsMessages.getString("network_code"));
                messagesVO.setMessage1(rsMessages.getString("message1"));
                messagesVO.setMessage2(rsMessages.getString("message2"));
                messagesVO.setMessage3(rsMessages.getString("message3"));
                messagesVO.setMessage4(rsMessages.getString("message4"));
                messagesVO.setMessage5(rsMessages.getString("message5"));
                messagesVO.setMclass(rsMessages.getString("mclass"));
                messagesVO.setDescription(rsMessages.getString("description"));

                // fetch the list of arguments based on the message_code

                pstmtArgument.setString(1, messageCode);
                rsArgument = pstmtArgument.executeQuery();

                argsList = new ArrayList<>();

                while (rsArgument.next()) {
                    argumentVO = new MessageArgumentVO();

                    argumentVO.setMessageCode(messageCode);
                    argumentVO.setArgument(rsArgument.getString("argument"));
                    argumentVO.setArgumentDesc(rsArgument.getString("argument_description"));

                    argsList.add(argumentVO);
                }
                messagesVO.setArgumentList(argsList);

                // fill the map with message_code as key and messageVO as value
          

                // clearing query parameter
                pstmtArgument.clearParameters();
            }

        } catch (SQLException sqle) {
            // TODO: handle exception
            _log.error("loadMessageWithArgs", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, "loadMessageWithArgs", "error.general.sql.processing");
        }// end of sql catch
        catch (Exception e) {
            // TODO: handle exception
            _log.error("loadMessageWithArgs", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "loadMessageWithArgs", "error.general.processing");
        }// end of catch
        finally {
        	try{
            	if (rsMessages!= null){
            		rsMessages.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing result set.", e);
            }
        	try{
        		if (pstmtMessages!= null){
        			pstmtMessages.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (rsArgument!= null){
            		rsArgument.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing result set.", e);
            }
        	try{
        		if (pstmtArgument!= null){
        			pstmtArgument.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
            if (_log.isDebugEnabled()) {
                _log.debug("loadMessageWithArgs", "Exiting loadMessageWithArgs.size:");
            }
        }// end of finally

        return messagesVO;
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
        final String METHOD_NAME = "updateMessages";
        PreparedStatement pstmtUpdate = null;
        MessagesVO messagesVO = null;
        int updateCount = 0;
        boolean flag = false;

        try {

            StringBuilder selectQueryBuff = new StringBuilder(" UPDATE MESSAGES_MASTER SET network_code=?,");
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
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, "updateMessageWithArgs", "error.general.sql.processing");
        }// end of sql catch
        catch (Exception e) {
            // TODO: handle exception
            _log.error("updateMessageWithArgs", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "updateMessageWithArgs", "error.general.processing");
        }// end of catch
        finally {
        	try{
        		if (pstmtUpdate!= null){
        			pstmtUpdate.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
            if (_log.isDebugEnabled()) {
                _log.debug("updateMessageWithArgs", "Exiting updateMessageWithArgs");
            }
        }// end of finally

        return flag;
    }


    /**
     * Description: This method update the messages into the database.
     * This method is call by Action for update the messages.
     *
     * @param p_mlist
     * @return boolean
     * @throws BTSLBaseException
     */


    /**
     * Desc:This method is used to get data according to the message_code into
     * the Database.
     * 
     * @param p_con
     * @param p_msgargs
     * @return MessagesVO
     * @throws BTSLBaseException
     */
    public MessagesVO loadMessages(Connection p_con, String p_msgargs) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadMessages", "Entered ");
        }
        final String METHOD_NAME = "loadMessages";
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        MessagesVO argumentVO = null;

        try {

            StringBuilder selectQueryBuff = new StringBuilder(" SELECT DEFAULT_MESSAGE, MESSAGE1, MESSAGE2, MESSAGE3, MESSAGE4, MESSAGE5");
            selectQueryBuff.append(" FROM MESSAGES_MASTER where message_code =?  ");

            String selectQuery = selectQueryBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("loadMessages", "Select Query= " + selectQuery);
            }

            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_msgargs);
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                argumentVO = new MessagesVO();

                argumentVO.setMessageCode(p_msgargs);
                argumentVO.setDefaultMessage(rs.getString("DEFAULT_MESSAGE"));
                argumentVO.setMessage1(rs.getString("MESSAGE1"));
                argumentVO.setMessage2(rs.getString("MESSAGE2"));
                argumentVO.setMessage3(rs.getString("MESSAGE3"));
                argumentVO.setMessage4(rs.getString("MESSAGE4"));
                argumentVO.setMessage5(rs.getString("MESSAGE5"));



            }
        } catch (SQLException sqle) {
            // TODO: handle exception
            _log.error("loadMessages", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, "loadMessages", "error.general.sql.processing");
        }// end of sql catch
        catch (Exception e) {
            // TODO: handle exception
            _log.error("loadArguments", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "loadMessages", "error.general.processing");
        }// end of catch
        finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadMessages", "Exiting loadArguments.size:");
            }
        }// end of finally

        return argumentVO;
    }

    /**
     * Desc: This method update the messages with message arguments into the
     * database.
     * 
     * @param p_messagesVO
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean updateMessages(MessagesVO p_messagesVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateMessages", "Entered ");
        }
        final String METHOD_NAME = "updateMessages";
        PreparedStatement pstmtUpdate = null;
        Connection con = null;
        int updateCount = 0;
        boolean flag = Boolean.FALSE;

        try {

            con = OracleUtil.getSingleConnection();

            StringBuilder selectQueryBuff = new StringBuilder(" UPDATE MESSAGES_MASTER SET message1=? ,");
            selectQueryBuff.append(" message2=?,message3=?, message4=?,message5=? WHERE message_code=?");

            String selectQuery = selectQueryBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("updateMessages", "Select Query= " + selectQuery);
            }

            pstmtUpdate = con.prepareStatement(selectQuery);

            pstmtUpdate.setString(1, p_messagesVO.getMessage1());
            pstmtUpdate.setString(2, p_messagesVO.getMessage2());
            pstmtUpdate.setString(3, p_messagesVO.getMessage3());
            pstmtUpdate.setString(4, p_messagesVO.getMessage4());
            pstmtUpdate.setString(5, p_messagesVO.getMessage5());
            pstmtUpdate.setString(6, p_messagesVO.getMessageCode());

            updateCount = pstmtUpdate.executeUpdate();

            if (updateCount > 0) {
                flag = Boolean.TRUE;
                // commit perform for each row of the file
                con.commit();
            } else {
                // roll back all the changes
                con.rollback();
                throw new BTSLBaseException(this, "updateMessages", "error.general.sql.processing");

            }

        } catch (SQLException sqle) {
            // TODO: handle exception
            _log.error("updateMessageWithArgs", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, "updateMessages", "error.general.sql.processing");
        }// end of sql catch
        catch (Exception e) {
            // TODO: handle exception
            _log.error("updateMessages", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "updateMessages", "error.general.processing");
        }// end of catch
        finally {
        	try{
        		if (pstmtUpdate!= null){
        			pstmtUpdate.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	OracleUtil.closeQuietly(con);
            if (_log.isDebugEnabled()) {
                _log.debug("updateMessages", "Exiting updateMessageWithArgs");
            }
        }// end of finally

        return flag;
    }

    /**
     * Desc: This method update the messages with message arguments into the
     * database.
     *
     * @param messagesVO
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean updateMessages(Connection con, MessageRequestVO messagesVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateMessages", "Entered ");
        }
        final String METHOD_NAME = "updateMessages";
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        boolean flag = Boolean.FALSE;

        try {
            StringBuilder selectQueryBuff = new StringBuilder(" UPDATE MESSAGES_MASTER SET message1=? ,");
            selectQueryBuff.append(" message2=?,message3=?, message4=?,message5=? WHERE message_code=?");

            String selectQuery = selectQueryBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("updateMessages", "Select Query= " + selectQuery);
            }

            pstmtUpdate = con.prepareStatement(selectQuery);

            pstmtUpdate.setString(1, messagesVO.getMessage1());
            pstmtUpdate.setString(2, messagesVO.getMessage2());
            pstmtUpdate.setString(3, messagesVO.getMessage3());
            pstmtUpdate.setString(4, messagesVO.getMessage4());
            pstmtUpdate.setString(5, messagesVO.getMessage5());
            pstmtUpdate.setString(6, messagesVO.getMessageDetailCode());

            updateCount = pstmtUpdate.executeUpdate();

            if (updateCount > 0) {
                flag = Boolean.TRUE;
                // commit perform for each row of the file
                con.commit();
            } else {
                // roll back all the changes
                con.rollback();
                throw new BTSLBaseException(this, "updateMessages", "error.general.sql.processing");

            }

        } catch (SQLException sqle) {
            // TODO: handle exception
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }// end of sql catch
        catch (Exception e) {
            // TODO: handle exception
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }// end of catch
        finally {
            try{
                if (pstmtUpdate!= null){
                    pstmtUpdate.close();
                }
            }
            catch (SQLException e){
                _log.error("An error occurred closing result set.", e);
            }
            OracleUtil.closeQuietly(con);
            if (_log.isDebugEnabled()) {
                _log.debug("updateMessages", "Exiting updateMessageWithArgs");
            }
        }// end of finally

        return flag;
    }

    /**
     * Load the Message Management List cache
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * @author Jasmine
     */
    public ArrayList loadMessageManagementList() throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadMessageManagementList()", "Entered");
        }
        final String METHOD_NAME = "loadMessageManagementList";
        PreparedStatement pstmt = null;
        ResultSet rsMessages = null;

        ArrayList arr = new ArrayList();

        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT message_type,message_code,default_message,network_code,");
        strBuff.append(" message1,message2,message3,mclass,description FROM MESSAGES_MASTER ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadMessageManagementList", "QUERY sqlSelect=" + sqlSelect);
        }

        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();

            pstmt = con.prepareStatement(sqlSelect);

            rsMessages = pstmt.executeQuery();
            MessagesVO messagesVO = null;
            String messageCode = null;
            while (rsMessages.next()) {
                messagesVO = new MessagesVO();

                messageCode = rsMessages.getString("message_code");

                messagesVO.setMessageType(rsMessages.getString("message_type"));
                messagesVO.setMessageCode(messageCode);
                messagesVO.setDefaultMessage(rsMessages.getString("default_message"));
                messagesVO.setNetworkCode(rsMessages.getString("network_code"));
                messagesVO.setMessage1(rsMessages.getString("message1"));
                messagesVO.setMessage2(rsMessages.getString("message2"));
                messagesVO.setMessage3(rsMessages.getString("message3"));
                messagesVO.setMclass(rsMessages.getString("mclass"));
                messagesVO.setDescription(rsMessages.getString("description"));
                arr.add(messagesVO);
            }

        } catch (SQLException sqe) {
            _log.error("loadMessageManagementList()", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessagesDAO[loadMessageManagementList]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadMessageManagementList()", "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            _log.error("loadMessageManagementList()", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessagesDAO[loadMessageManagementList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadMessageManagementList()", "error.general.processing");
        } finally {
        	try{
            	if (rsMessages!= null){
            		rsMessages.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing result set.", e);
            }
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
        	OracleUtil.closeQuietly(con);
           if (_log.isDebugEnabled()) {
                _log.debug("loadMessageManagementList()", "Exiting: networkMap size=" + arr.size());
            }
        }
        return arr;
    }

    public List<ListValueVO> updateMessages(Connection p_con, List<MessagesVO> p_mlist, List<ListValueVO> fileErrorList, Locale locale) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateMessages", "Entered ");
        }
        final String METHOD_NAME = "updateMessages";
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtMsgCodeExist = null;
        MessagesVO messagesVO = null;
        int updateCount = 0;
        boolean flag = false;
        ArrayList dbErrorList = new ArrayList();
        ResultSet rsIsMsgCodeExist = null;
        ListValueVO errorVO = null;
        boolean fileValidationErrorExists = Boolean.FALSE;
        long failCount = 0;

        try {
            StringBuilder updateQueryBuff = new StringBuilder(" UPDATE MESSAGES_MASTER SET network_code=?,");
            updateQueryBuff.append(" message1=?,message2=?,message3=?,message4=?,message5=? WHERE message_code=?");

            String updateQuery = updateQueryBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Select Query= " + updateQuery);
            }

            pstmtUpdate = p_con.prepareStatement(updateQuery);


            for (int i = 0, k = 1, j = p_mlist.size(); i < j; i++) {
                messagesVO = p_mlist.get(i);

                pstmtUpdate.setString(1, messagesVO.getNetworkCode());
                pstmtUpdate.setString(2, messagesVO.getMessage1());
                pstmtUpdate.setString(3, messagesVO.getMessage2());
                pstmtUpdate.setString(4, messagesVO.getMessage3());
                pstmtUpdate.setString(5, messagesVO.getMessage4());
                pstmtUpdate.setString(6, messagesVO.getMessage5());

                pstmtUpdate.setString(7, messagesVO.getMessageCode());

                updateCount = pstmtUpdate.executeUpdate();

                if (updateCount <= 0) {
                    // roll back all the changes
                    String error = RestAPIStringParser.getMessage(locale,
                            PretupsErrorCodesI.ERROR_GENERAL_PROCESSING,
                            null);
                    errorVO = new ListValueVO("", String.valueOf(i + 1), error);
                    fileErrorList.add(errorVO);
                    p_con.rollback();
                    continue;
                }
                p_con.commit();
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, "Messages have been updated successfully into database");
                }
            }
        } catch (SQLException sqle) {
            // TODO: handle exception
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, "updateMessages", "error.general.sql.processing");
        }// end of sql catch
        catch (Exception e) {
            // TODO: handle exception
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "updateMessages", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
                if (pstmtMsgCodeExist != null) {
                    pstmtMsgCodeExist.close();
                }
                if (rsIsMsgCodeExist != null) {
                    rsIsMsgCodeExist.close();
                }
            } catch (SQLException e) {
                _log.error("An error occurred closing result set.", e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting updateMessages");
            }
        }// end of finally

        return fileErrorList;
    }

    public Boolean isMessageCodeExist(Connection con, String messageCode) throws BTSLBaseException {
        final String METHOD_NAME = "isMessageCodeExist";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered ");
        }
        PreparedStatement pstmtMsgCodeExist = null;
        ResultSet rsIsMsgCodeExist = null;
        Boolean isExist = false;
        try {
            final String isMsgCodeExistQuery = "SELECT 1 FROM MESSAGES_MASTER WHERE message_code=?";
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "isMsgCodeExistQuery = " + isMsgCodeExistQuery);
            }
            pstmtMsgCodeExist = con.prepareStatement(isMsgCodeExistQuery.toString());

            pstmtMsgCodeExist.setString(1, messageCode);
            rsIsMsgCodeExist = pstmtMsgCodeExist.executeQuery();
            pstmtMsgCodeExist.clearParameters();

            if (rsIsMsgCodeExist.next()) {
                isExist = true;
            }
        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
        }
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
        }
        finally {
            try{
                if (rsIsMsgCodeExist!= null){
                    rsIsMsgCodeExist.close();
                }
            }
            catch (SQLException e){
                _log.error("An error occurred closing result set.", e);
            }
            try{
                if (pstmtMsgCodeExist!= null){
                    pstmtMsgCodeExist.close();
                }
            }
            catch (SQLException e){
                _log.error("An error occurred closing statement.", e);
            }
        }
        return isExist;
    }

}
