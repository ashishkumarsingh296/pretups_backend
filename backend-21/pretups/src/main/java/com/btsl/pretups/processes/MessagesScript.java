package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.messages.businesslogic.MessageArgumentVO;
import com.btsl.pretups.messages.businesslogic.MessagesVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.xl.ExcelRW;

/**
 * @(#)MessagesScript.java
 *                         Copyright(c) 2011, Comviva technologies Ltd.
 *                         All Rights Reserved
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Samna Soin Dec 02,2011 Initial Creation
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 */
public class MessagesScript {

    private static Log _logger = LogFactory.getLog(MessagesScript.class.getName());
    private static String _filePath = null;
    private static String _file = null;

    /**
     * @param arg
     *            arg[0]=Constants.props
     *            arg[1]=ProcessLogconfig.props
     *            arg[2]=Excel File
     **/
    public static void main(String[] args) {
        final long startTime = (new Date()).getTime();
        final String METHOD_NAME = "main";
        try {
            final int argSize = args.length;
            if (argSize != 3) {
                _logger.info(METHOD_NAME, "Three arguments are required  : MessagesScript [Constants file] [ProcessLogConfig file] [Messages Excel Sheet]");
                _logger.error("MessagesScript main()", " Usage : MessagesScript [Constants file] [ProcessLogConfig file] [Messages Excel Sheet]");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MessagesScript[main]", "", "", "",
                    "Improper usage. Usage : MessagesScript [Constants file] [ProcessLogConfig file][Messages Excel Sheet] ");
                throw new BTSLBaseException("MessagesScript ", " main ", PretupsErrorCodesI.MESSAGE_SCRIPT_NUMBER_ARG_MISSING);
            }

            new MessagesScript().process(args);
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, " : Exiting BTSLBaseException " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            _logger.error("main ", ": Exiting Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MNPFileUploadProcess: main", "", "", "",
                "Exiting the exception of main");
            _logger.errorTrace(METHOD_NAME, e);
        }// end of outer Exception
        finally {
            final long endTime = (new Date()).getTime();
            _logger.debug(METHOD_NAME, "Main method Exiting .......Time taken in Milli seconds=" + (endTime - startTime));
            ConfigServlet.destroyProcessCache();
        }
    }

    private void process(String[] p_args) {
        String[][] excelArr = null;
        final String METHOD_NAME = "process";
        try {
            loadCachesAndLogFiles(p_args[0], p_args[1]);// load Constants.props
            // and ProccessLogConfig
            // file
            Connection con = null;

            _file = p_args[2];
            _filePath = Constants.getProperty("UploadMessageFilePath");
            if (BTSLUtil.isNullString(_filePath)) {
                _logger.info(METHOD_NAME, " File directory path for upload is not defined in Constant.props file Kindly define it..............");
                _logger.error("main ", ":File directory path for upload is not defined in Constant.props file Kindly define it..............");
                throw new BTSLBaseException("MessagesScript", METHOD_NAME, PretupsErrorCodesI.MESSAGE_SCRIPT_ERROR_FILEPATH_NULL);
            }
            _file = _filePath + _file;
            // opening the connection
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                _logger.info(METHOD_NAME, " Could not connect to database. Please make sure that database server is up..............");
                _logger.error("main ", ": Could not connect to database. Please make sure that database server is up..............");
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessagesScript[process]", "", "",
                    "", "Could not connect to Database");
                throw new BTSLBaseException("MessagesScript", METHOD_NAME, PretupsErrorCodesI.MESSAGE_SCRIPT_ERROR_CONN_NULL);
            }
            final ExcelRW excelRW = new ExcelRW();
            excelArr = excelRW.readExcel(null, _file);
            writeFileToDatabase(con, excelArr);
            con.commit();
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException=" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessagesScript[process]", "", "", "",
                "BTSLBaseException:" + be.getMessage());
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception=" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessagesScript[process]", "", "", "", "Exception:" + e
                .getMessage());
        }
    }

    public int writeFileToDatabase(Connection p_con, String[][] p_excelArr) throws BTSLBaseException {
        final String METHOD_NAME = "writeFileToDatabase";
        if (_logger.isDebugEnabled()) {
            _logger.debug("writeFileToDatabase", " Entered p_filename:");
        }
        int count = 0;
        final int noOfRows = p_excelArr.length;
        final int noOfCols = p_excelArr[0].length;
        MessageArgumentVO messageArgumentVO = null;
        MessagesVO messagesVO = null;
        int[] batchInCt;
        final ArrayList finalMsgArgList = new ArrayList();
        final ArrayList finalMsgList = new ArrayList();
        PreparedStatement insertPstmt = null;
        PreparedStatement insertArgPstmt = null;
        final StringBuffer masterInsertSql = new StringBuffer("INSERT INTO messages_master(message_type,message_code,");
        masterInsertSql.append("default_message,network_code,mclass");
        for (int i = 0, j = 4; j <= noOfCols; j++) {
            masterInsertSql.append(",message" + (++i));
        }
        masterInsertSql.append(") VALUES (?,?,?,?,?");
        for (int j = 4; j <= noOfCols; j++) {
            masterInsertSql.append(",?");
        }
        masterInsertSql.append(")");
        final StringBuffer msgArgInsertSql = new StringBuffer("INSERT INTO message_argument(message_code,argument,");
        msgArgInsertSql.append("argument_description) VALUES (?,?,?)");
        final String insertMsgQuery = masterInsertSql.toString();
        final String insertArgQuery = msgArgInsertSql.toString();
        try {
            String messageCode = null;
            String message = null;
            String argDesc = null;
            String allArgs = null;
            String argument = null;
            final String delimiters = "[,\\n]+";
            String name = null;
            for (int row = 1; row < noOfRows; row++) {
                messagesVO = new MessagesVO();
                messageCode = p_excelArr[row][0];
                message = p_excelArr[row][1];
                allArgs = p_excelArr[row][2];
                final String[] args = allArgs.split(delimiters);
                final int length = args.length;
                for (int i = 0; i < length; i++) {
                    final String[] finalArg = args[i].split("=");
                    if (finalArg.length == 2) {
                        argument = finalArg[0];
                        argDesc = finalArg[1];
                        messageArgumentVO = new MessageArgumentVO();
                        if (!BTSLUtil.isNullString(argument)) {
                            messageArgumentVO.setArgument(argument);
                        } else {
                            messageArgumentVO.setArgument("N.A");
                        }
                        if (!BTSLUtil.isNullString(argDesc)) {
                            messageArgumentVO.setArgumentDesc(argDesc);
                        } else {
                            messageArgumentVO.setArgumentDesc("N.A");
                        }
                        messageArgumentVO.setMessageCode(messageCode);
                        finalMsgArgList.add(messageArgumentVO);
                    }
                }
                messagesVO.setMessageType(PretupsI.ALL);
                messagesVO.setMessageCode(messageCode);
                ListValueVO listValuevo = null;
                if (!BTSLUtil.isNullString(message)) {
                    messagesVO.setDefaultMessage(message);
                } else {
                    messagesVO.setDefaultMessage("N.A");
                }
                for (int i = 1, j = 3; j < noOfCols; j++, i++) {
                    name = "Message" + i;
                    if (BTSLUtil.isNullString(p_excelArr[row][j])) {
                        p_excelArr[row][j] = "N.A";
                    }
                    listValuevo = new ListValueVO(name, p_excelArr[row][j]);
                    if ("message1".equalsIgnoreCase(listValuevo.getLabel())) {
                        messagesVO.setMessage1(listValuevo.getValue());
                    } else if ("message2".equalsIgnoreCase(listValuevo.getLabel())) {
                        messagesVO.setMessage2(listValuevo.getValue());
                    } else if ("message3".equalsIgnoreCase(listValuevo.getLabel())) {
                        messagesVO.setMessage3(listValuevo.getValue());
                    } else if ("message4".equalsIgnoreCase(listValuevo.getLabel())) {
                        messagesVO.setMessage4(listValuevo.getValue());
                    } else if ("message5".equalsIgnoreCase(listValuevo.getLabel())) {
                        messagesVO.setMessage5(listValuevo.getValue());
                    }

                }
                messagesVO.setNetworkCode("ALL");
                messagesVO.setMclass(PretupsI.YES);
                finalMsgList.add(messagesVO);
            }

            insertPstmt = p_con.prepareStatement(insertMsgQuery);
            insertArgPstmt = p_con.prepareStatement(insertArgQuery);
            for (int i = 0, j = finalMsgList.size(); i < j; i++) {
                messagesVO = (MessagesVO) finalMsgList.get(i);
                insertPstmt.setString(1, messagesVO.getMessageType());
                insertPstmt.setString(2, messagesVO.getMessageCode());
                insertPstmt.setString(3, messagesVO.getDefaultMessage());
                insertPstmt.setString(4, messagesVO.getNetworkCode());
                insertPstmt.setString(5, messagesVO.getMclass());
                if (!BTSLUtil.isNullString(messagesVO.getMessage1())) {
                    insertPstmt.setString(6, messagesVO.getMessage1());
                }
                if (!BTSLUtil.isNullString(messagesVO.getMessage2())) {
                    insertPstmt.setString(7, messagesVO.getMessage2());
                }
                if (!BTSLUtil.isNullString(messagesVO.getMessage3())) {
                    insertPstmt.setString(8, messagesVO.getMessage3());
                }
                if (!BTSLUtil.isNullString(messagesVO.getMessage4())) {
                    insertPstmt.setString(9, messagesVO.getMessage4());
                }
                if (!BTSLUtil.isNullString(messagesVO.getMessage5())) {
                    insertPstmt.setString(10, messagesVO.getMessage5());
                }
                insertPstmt.addBatch();
                // messagesVO = null;
            }
            batchInCt = insertPstmt.executeBatch();
            insertPstmt.clearBatch();
            count = batchInCt.length;
            for (int i = 0, j = finalMsgArgList.size(); i < j; i++) {
                messageArgumentVO = (MessageArgumentVO) finalMsgArgList.get(i);
                insertArgPstmt.setString(1, messageArgumentVO.getMessageCode());
                insertArgPstmt.setString(2, messageArgumentVO.getArgument());
                insertArgPstmt.setString(3, messageArgumentVO.getArgumentDesc());
                insertArgPstmt.addBatch();
                // messageArgumentVO = null;
            }
            final int[] batchInCt1 = insertArgPstmt.executeBatch();
            insertArgPstmt.clearBatch();
            count += batchInCt1.length;
        } catch (SQLException sqe) {
            _logger.error("writeFileToDatabase", "SQLException : " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessagesScript[writeFileToDatabase]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.sql.processing");
        } catch (Exception ex) {
            _logger.error("writeFileToDatabase", "Exception : " + ex);
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessagesScript[writeFileToDatabase]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.processing");
        } finally {
            try {
                if (insertArgPstmt != null) {
                    insertArgPstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (insertPstmt != null) {
                    insertPstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeFileToDatabase", "Exiting Count=" + count);
            }
        }
        return count;
    }

    /**
     * This method loads the Constants.props and ProccessLogConfig file and
     * checks whether the process is already running or not
     * 
     * @param arg1
     * @param arg2
     * @throws BTSLBaseException
     * @throws Exception
     */
    public static void loadCachesAndLogFiles(String p_arg1, String p_arg2) throws BTSLBaseException {
        final String METHOD_NAME = "loadCachesAndLogFiles";

        if (_logger.isDebugEnabled()) {
            _logger.debug(" loadCachesAndLogFiles ", " Entered with p_arg1=" + p_arg1 + " p_arg2=" + p_arg2);
        }
        File logconfigFile = null;
        File constantsFile = null;
        try {
            constantsFile = new File(p_arg1);
            if (!constantsFile.exists()) {
                _logger.debug(METHOD_NAME, "MessagesScript loadCachesAndLogFiles Constants file not found on location:: " + constantsFile.toString());
                _logger.error("MessagesScript[loadCachesAndLogFiles]", " Constants file not found on location:: " + constantsFile.toString());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MessagesScript[loadCachesAndLogFiles]", "", "", "",
                    " The Constants file doesn't exists at the path specified. ");
                throw new BTSLBaseException("MessagesScript ", " loadCachesAndLogFiles ", PretupsErrorCodesI.MESSAGE_SCRIPT_MISSING_CONST_FILE);
            }

            logconfigFile = new File(p_arg2);
            if (!logconfigFile.exists()) {
                _logger.debug(METHOD_NAME, "MessagesScript loadCachesAndLogFiles Logconfig file not found on location:: " + logconfigFile.toString());
                _logger.error("MessagesScript[loadCachesAndLogFiles]", " ProcessLogConfig file not found on location:: " + logconfigFile.toString());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessagesScript[loadCachesAndLogFiles]", "", "", "",
                    " The ProcessLogConfig file doesn't exists  at the path specified. ");
                throw new BTSLBaseException("MessagesScript ", "loadCachesAndLogFiles ", PretupsErrorCodesI.MESSAGE_SCRIPT_MISSING_LOG_FILE);
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (BTSLBaseException be) {
            _logger.error("MessagesScript[loadCachesAndLogFiles]", "BTSLBaseException =" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("MessagesScript[loadCachesAndLogFiles]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessagesScript[loadCachesAndLogFiles]", "", "", "",
                "Exception=" + e.getMessage());
            throw new BTSLBaseException("MessagesScript ", " loadCachesAndLogFiles ", PretupsErrorCodesI.MESSAGE_SCRIPT_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of Exception
        finally {
            if (logconfigFile != null) {
                logconfigFile = null;
            }
            if (constantsFile != null) {
                constantsFile = null;
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("MessagesScript[loadCachesAndLogFiles]", " Exiting..........");
            }
        }// end of finally
    }
}
