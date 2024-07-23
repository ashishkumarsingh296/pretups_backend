package com.btsl.pretups.processes;

/**
 * @(#) O2CExtTransactionProcess
 *      Copyright(c) 2006, Bharti Telesoft Ltd.
 *      All Rights Reserved
 * 
 *      ------------------------------------------------------------------------
 *      -------------------------
 *      Author Date History
 *      ------------------------------------------------------------------------
 *      -------------------------
 *      Ranjana Chouhan 24/12/2008 Initial Creation
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class O2CExtTransactionProcess {

    private static String _o2cTxnFileGenDirPath = null;
    private static String _o2cExtTransactionFileLabel = null;
    private static String _o2cExtTransactionQuery = null;
    private static String _o2cTxnConfigureParam = null;
    private static String _finalO2cExtTransactionDirectoryPath = null;
    private static String _fileExt = ".csv";
    private static int _maxFileLength = 10000;
    private static String _addHeaderFooter = null;
    private static ArrayList _fileNameLst = new ArrayList();
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;
    private static final Log LOGGER = LogFactory.getLog(O2CExtTransactionProcess.class.getName());
    private static String _childDirectory;

    /**
     * to ensure no class instantiation 
     */
    private O2CExtTransactionProcess(){
    	
    }
    
    public static void main(String arg[]) {
        final String methodName = "main";
        try {

            if (arg.length != 4) {
                System.out.println("Usage : O2CExtTrasactionReport [Constants file] [LogConfig file] [Query File] [AddHeaderFooter]");
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println("O2CExtTrasactionReport" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println("O2CExtTrasactionReport" + " Logconfig File Not Found .............");
                return;
            }
            final File queryFile = new File(arg[2]);
            if (!queryFile.exists()) {
                System.out.println("O2CExtTrasactionReport" + " Logconfig File Not Found .............");
                return;
            }
            _addHeaderFooter = arg[3];
            if (BTSLUtil.isNullString(_addHeaderFooter)) {
                System.out.println("O2CExtTrasactionReport" + " Add Header and Footer parameter not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            Constants.load(queryFile.toString());
        }// end of try
        catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(methodName, " Error in Loading Files ...........................: " + e.getMessage());
            }
            LOGGER.errorTrace(methodName, e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            process();
        } catch (BTSLBaseException be) {
            LOGGER.error(methodName, "BTSLBaseException : " + be.getMessage());
            LOGGER.errorTrace(methodName, be);
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(methodName, "Exiting..... ");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * method call to generate o2c and c2c transaction data(through external
     * gateway) file
     */
    private static void process() throws BTSLBaseException {
        final String METHOD_NAME = "process";
        Date processedUpto = null;
        Date dateCount = null;
        Date currentDate = new Date();
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        ProcessStatusDAO processStatusDAO = null;
        int beforeInterval = 0;
        HashMap map = null;
        int maxDoneDateUpdateCount = 0;

        try {
            LOGGER.debug("process", "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);

            // getting all the required parameters from Constants.props
            loadConstantParameters();

            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("process", " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CExtTransactionProcess[process]",
                    "", "", "", "DATABASE Connection is NULL");
                return;
            }
            // getting process id
            processId = ProcessI.O2CEXT_TXN_PROCESSID;
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            beforeInterval = BTSLUtil.parseLongToInt( _processStatusVO.getBeforeInterval() / (60 * 24));
            if (statusOk) {
                con.commit();
                // method call to find maximum date till which process has been
                // executed
                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    // adding 1 in processed upto date
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);

                    // loop to be started for each date
                    for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto); dateCount.before(BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval)); dateCount = BTSLUtil
                        .addDaysInUtilDate(dateCount, 1)) {
                        // method call to create master directory and child
                        // directory if does not exist
                        _childDirectory = createDirectory(_o2cTxnFileGenDirPath, processId, dateCount);

                        // method call to load transaction data
                        map = loadChannelTransactionData(con, dateCount, _o2cExtTransactionQuery);

                        // split the configured parameter,,first by semicolon(;)
                        // then colon(:)
                        if (!BTSLUtil.isNullString(_o2cTxnConfigureParam)) {
                            splitConfigurationParam(dateCount, _childDirectory, map);
                        } else {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CExtTransactionProcess[process]",
                                "", "", "", "Configuration parameter value is null,One file will generate which contains all transaction data i.e. O2C,C2C ");
                            final String[] _gatewayType = new String[1];
                            _gatewayType[0] = "EXTGW";
                            generateTransactionFileAndWriteData(_childDirectory, _gatewayType, map, null, null, _fileExt, _o2cExtTransactionFileLabel, dateCount,
                                _maxFileLength);
                        }
                        // method call to update maximum date till which process
                        // has been executed
                        _processStatusVO.setExecutedUpto(dateCount);
                        _processStatusVO.setExecutedOn(currentDate);
                        processStatusDAO = new ProcessStatusDAO();
                        maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, _processStatusVO);

                        // if the process is successful, transaction is commit,
                        // else rollback
                        if (maxDoneDateUpdateCount > 0) {
                            moveFilesToFinalDirectory(_o2cTxnFileGenDirPath, _finalO2cExtTransactionDirectoryPath, processId, dateCount);
                            con.commit();
                        } else {
                            deleteAllFiles();
                            con.rollback();
                            throw new BTSLBaseException("O2CExtTransactionProcess", "process", PretupsErrorCodesI.O2CEXT_ERROR_EXCEPTION);
                        }
                        // Transaction sleep has been added after processing
                        // records of one day
                        Thread.sleep(500);
                    }// end for

                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "O2CExtTransactionProcess[process]", "", "", "",
                        " O2CExtTransactionProcess process has been executed successfully.");
                } else {
                    throw new BTSLBaseException("O2CExtTransactionProcess", "process", PretupsErrorCodesI.O2CEXT_PROCESS_UPTO_DATE_NOT_FOUND);
                }
            }
        }// end of try
        catch (BTSLBaseException be) {
            LOGGER.error("process", "BTSLBaseException : " + be.getMessage());
            LOGGER.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            if (_fileNameLst.size() > 0) {
                deleteAllFiles();
            }
            LOGGER.error("process", "Exception : " + e.getMessage());
            LOGGER.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "O2CExtTransactionProcess[process]", "", "", "",
                " O2CExtTransactionProcess process could not be executed successfully.");
            throw new BTSLBaseException("O2CExtTransactionProcess", "process", PretupsErrorCodesI.O2CEXT_ERROR_EXCEPTION);
        } finally {
            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk) {
                try {
                    if (markProcessStatusAsComplete(con, processId) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            LOGGER.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            LOGGER.errorTrace(METHOD_NAME, e);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.errorTrace(METHOD_NAME, e);
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception ex) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("process", "Exception closing connection ");
                        LOGGER.errorTrace(METHOD_NAME, ex);
                    }
                }
            }
            LOGGER.debug("process", "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("process", "Exiting..... ");
            }
        }
    }

    /**
     * Call method to spilt the configuration parameter
     * configuration parameter like EXTGW:O2C,C2C;EXTGW2:O2C
     * first split on semicolon,then colon,then comma
     * If we split on semicolon,then the data EXTGW:O2C,C2C should be in one
     * file.
     * 
     * @param p_processingDate
     *            Date
     * @param p_dir
     *            String
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     */
    private static void splitConfigurationParam(Date p_processingDate, String p_dir, HashMap p_map) throws BTSLBaseException {
        final String METHOD_NAME = "splitConfigurationParam";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("splitConfigurationParam", " Entered:p_processingDate=" + p_processingDate + ",p_dir=" + p_dir + ",p_map=" + p_map);
        }
        try {
            final String[] _transcationFile = _o2cTxnConfigureParam.split(";");
            String _gatewayCodeType = null; // String ith form of code:type
            for (int semicolon = 0; semicolon < _transcationFile.length; semicolon++) {
                final String[] _codeAndType = _transcationFile[semicolon].split(":");
                final String[] _gatewayCode = _codeAndType[0].split(",");
                final String[] _type = _codeAndType[1].split(",");

                // store the strings in the format EXTGW:O2C,EXTGW1:C2C,
                // get the size of the this array by multiplying length of
                // gateway code array and type array
                final String[] _arrGatewayCodeType = new String[_gatewayCode.length * _type.length];
                int i = 0;
                for (int j = 0; j < _gatewayCode.length; j++) {
                    for (int x = 0; x < _type.length; x++) {
                        _gatewayCodeType = _gatewayCode[j] + ":" + _type[x];
                        _arrGatewayCodeType[i] = _gatewayCodeType;
                        i++;
                    }
                }
                LOGGER.debug("process", "_arrGatewayCodeType array contains the string in the format EXTGW1:O2C" + _arrGatewayCodeType);
                // method call to get the file name and write data in file
                generateTransactionFileAndWriteData(p_dir, _arrGatewayCodeType, p_map, _gatewayCode, _type, _fileExt, _o2cExtTransactionFileLabel, p_processingDate,
                    _maxFileLength);
            }
        } catch (BTSLBaseException be) {
            LOGGER.error("splitConfigurationParam", "BTSLBaseException : " + be.getMessage());
            LOGGER.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception ex) {
            LOGGER.error("splitConfigurationParam", "Exception : " + ex.getMessage());
            LOGGER.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException("O2CExtTransactionProcess", "splitConfigurationParam", PretupsErrorCodesI.O2CEXT_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("splitConfigurationParam", "Exiting ");
            }
        }
    }

    /**
     * Load all constants parameters from Constants.props
     * Query from query.props.
     */
    private static void loadConstantParameters() throws BTSLBaseException {
        final String METHOD_NAME = "loadConstantParameters";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loadConstantParameters", " Entered: ");
        }
        try {
            _o2cExtTransactionFileLabel = Constants.getProperty("O2CEXT_TRANSACTION_FILE_LABEL");
            if (BTSLUtil.isNullString(_o2cExtTransactionFileLabel)) {
                LOGGER.error("loadConstantParameters", " Could not find file label for transaction data in the Configuration file.");
            } else {
                LOGGER.debug("loadConstantParameters", " _o2cExtTransactionFileLabel=" + _o2cExtTransactionFileLabel);
            }

            _o2cTxnFileGenDirPath = Constants.getProperty("O2CEXT_TXN_FILE_GEN_DIR");
            if (BTSLUtil.isNullString(_o2cTxnFileGenDirPath)) {
                LOGGER.error("loadConstantParameters", " Could not find directory path for O2C Transaction data in the Configuration file.");
            } else {
                LOGGER.debug("loadConstantParameters", " _o2cTxnFileGenDirPath=" + _o2cTxnFileGenDirPath);
            }

            _o2cExtTransactionQuery = Constants.getProperty("O2CEXT_TRANSACTION_QUERY");
            if (BTSLUtil.isNullString(_o2cExtTransactionQuery)) {
                LOGGER.error("loadConstantParameters", " Could not find query in query file .");
            } else {
                LOGGER.debug("loadConstantParameters", " _o2cExtTransactionQuery=" + _o2cExtTransactionQuery);
            }

            _finalO2cExtTransactionDirectoryPath = Constants.getProperty("O2CEXT_TXN_FINAL_DIR_PATH");
            if (BTSLUtil.isNullString(_finalO2cExtTransactionDirectoryPath)) {
                LOGGER.error("loadConstantParameters", " Could not find final directory path for transaction data in the Configuration file.");
            } else {
                LOGGER.debug("loadConstantParameters", "_finalO2cExtTransactionDirectoryPath =" + _finalO2cExtTransactionDirectoryPath);
            }

            _o2cTxnConfigureParam = Constants.getProperty("O2CEXT_TXN_CONFIGURE_PARAM");
            if (BTSLUtil.isNullString(_finalO2cExtTransactionDirectoryPath)) {
                LOGGER.error("loadConstantParameters", " Could not find file name for transaction data in the Constants file.");
            } else {
                LOGGER.debug("loadConstantParameters", "_o2cTxnConfigureParam =" + _o2cTxnConfigureParam);
            }

            LOGGER.debug("loadConstantParameters", " Required information successfuly loaded from Constants.props...............: ");

            if (BTSLUtil.isNullString(_o2cExtTransactionFileLabel) || BTSLUtil.isNullString(_o2cTxnFileGenDirPath) || BTSLUtil.isNullString(_o2cExtTransactionQuery) || BTSLUtil
                .isNullString(_finalO2cExtTransactionDirectoryPath)) {
                throw new BTSLBaseException("O2CExtTransactionProcess", "loadConstantParameters", PretupsErrorCodesI.O2C_EXT_CONST_PARAM_NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.error("loadConstantParameters", "Exception : " + e.getMessage());
            LOGGER.errorTrace(METHOD_NAME, e);
            final BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.O2CEXT_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CExtTransactionProcess[loadConstantParameters]", "",
                "", "", "Message:" + btslMessage);
            throw new BTSLBaseException("O2CExtTransactionProcess", "loadConstantParameters", PretupsErrorCodesI.O2CEXT_ERROR_EXCEPTION);
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("loadConstantParameters", " Exiting: ");
            }
        }
    }

    /**
     * This method will create directory at the path defined in Constants.props,
     * if it does not exist
     * 
     * @param p_directoryPathAndName
     *            String
     * @param p_processId
     *            String
     * @param p_beingProcessedDate
     *            Date
     * @throws BTSLBaseException
     * @return String
     */
    private static String createDirectory(String p_directoryPathAndName, String p_processId, Date p_beingProcessedDate) throws BTSLBaseException {
        final String METHOD_NAME = "createDirectory";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("createDirectory",
                " Entered: p_directoryPathAndName=" + p_directoryPathAndName + " p_processId=" + p_processId + " p_beingProcessedDate=" + p_beingProcessedDate);
        }
        String dirName = null;
        SimpleDateFormat sdf = null;
        try {
            boolean success = false;
            final File parentDir = new File(p_directoryPathAndName);
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            // child directory name includes a file name and being processed
            // date,
            sdf = new SimpleDateFormat("ddMMyy");
           // dirName = p_directoryPathAndName + File.separator + p_processId + "_" + sdf.format(p_beingProcessedDate);
            dirName = p_directoryPathAndName + File.separator + p_processId + "_" + BTSLUtil.getDateStrForName(p_beingProcessedDate);
            final File newDir = new File(dirName);
            if (!newDir.exists()) {
                success = newDir.mkdirs();
            } else {
                success = true;
            }
            if (!success) {
                throw new BTSLBaseException("O2CExtTransactionProcess", "createDirectory", PretupsErrorCodesI.COULD_NOT_CREATE_DIR);
            }
        } catch (BTSLBaseException be) {
            LOGGER.error("createDirectory", "BTSLBaseException : " + be.getMessage());
            LOGGER.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception ex) {
            LOGGER.error("createDirectory", "Exception : " + ex.getMessage());
            LOGGER.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CExtTransactionProcess[createDirectory]", "", "",
                "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("O2CExtTransactionProcess", "createDirectory", PretupsErrorCodesI.O2CEXT_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("createDirectory", "Exiting dirName=" + dirName);
            }
        }
        return dirName;
    }

    /**
     * This method will fetch all the required transactions data from database
     * 
     * @param p_con
     *            Connection
     * @param p_beingProcessedDate
     *            Date
     * @param p_query
     *            String
     * @return map
     * @throws SQLException
     *             ,Exception
     */
    private static HashMap loadChannelTransactionData(Connection p_con, Date p_beingProcessedDate, String p_query) throws BTSLBaseException {
        final String METHOD_NAME = "loadChannelTransactionData";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loadChannelTransactionData", " Entered: p_beingProcessedDate=" + p_beingProcessedDate + " p_query=" + p_query);
        }

        final String channelTxnQuery = p_query;
        PreparedStatement channelTxnSelectPstmt = null;
        ResultSet rs = null;
        ArrayList arrList = new ArrayList();
        final HashMap map = new HashMap();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loadChannelTransactionData", "channel transaction select query:" + channelTxnQuery);
        }
        try {
            channelTxnSelectPstmt = p_con.prepareStatement(channelTxnQuery);
            channelTxnSelectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            rs = channelTxnSelectPstmt.executeQuery();

            LOGGER
                .debug(
                    "loadChannelTransactionData",
                    "Memory after loading channel transaction data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
            String _oldGateway = null;
            String _tempString = null;
            String _tempType = null;
            if (!BTSLUtil.isNullString(_o2cTxnConfigureParam)) {
                while (rs.next()) {
                    _tempString = rs.getString(1);
                    final String[] _tempStr = _tempString.split(",");
                    if (!BTSLUtil.isNullString(_oldGateway) && (_oldGateway.equals(_tempStr[8]) && _tempType.equals(_tempStr[9]))) {
                        arrList.add(_tempString);
                        _oldGateway = _tempStr[8];
                        _tempType = _tempStr[9];
                    } else if (BTSLUtil.isNullString(_oldGateway)) {
                        arrList.add(_tempString);
                        _oldGateway = _tempStr[8];
                        _tempType = _tempStr[9];
                    } else {
                        final String key = _oldGateway + ":" + _tempType;
                        map.put(key, arrList);
                        arrList = null;
                        arrList = new ArrayList();
                        arrList.add(_tempString);
                        _oldGateway = _tempStr[8];
                        _tempType = _tempStr[9];
                    }
                }
                final String key = _oldGateway + ":" + _tempType;
                map.put(key, arrList);
            } else {
                while (rs.next()) {
                    _tempString = rs.getString(1);
                    arrList.add(_tempString);
                }
                map.put("EXTGW", arrList);
            }
            LOGGER.debug("loadChannelTransactionData", "Memory after writing transaction files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime
                .getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
        } catch (SQLException sqe) {
            LOGGER.error("loadChannelTransactionData", "SQLException " + sqe.getMessage());
            LOGGER.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CExtTransactionProcess[loadChannelTransactionData]",
                "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("O2CExtTransactionProcess", "loadChannelTransactionData", PretupsErrorCodesI.O2CEXT_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            LOGGER.error("loadChannelTransactionData", "Exception : " + ex.getMessage());
            LOGGER.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CExtTransactionProcess[loadChannelTransactionData]",
                "", "", "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("O2CExtTransactionProcess", "loadChannelTransactionData", PretupsErrorCodesI.O2CEXT_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    LOGGER.errorTrace(METHOD_NAME, ex);
                }
            }
            if (channelTxnSelectPstmt != null) {
                try {
                    channelTxnSelectPstmt.close();
                } catch (Exception ex) {
                    LOGGER.errorTrace(METHOD_NAME, ex);
                }
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("loadChannelTransactionData", "Exiting " + map.size());
            }
        }// end of finally
        return map;
    }

    /**
     * Call method to generate file and write data in the file
     * 
     * @param p_gatewayCodeType
     *            String []
     * @param p_dirName
     *            String
     * @param p_fileLabel
     *            String
     * @param p_processUpto
     *            Date
     * @param p_fileEXT
     *            String
     * @param p_maxFileLength
     *            long
     * @param p_map
     *            HashMap
     * @param p_gatewayCode
     *            String []
     * @param p_type
     *            String []
     * @return void
     * @throws Exception
     */
    private static void generateTransactionFileAndWriteData(String p_dirName, String[] p_gatewayCodeType, HashMap p_map, String[] p_gatewayCode, String[] p_type, String p_fileExt, String p_fileLabel, Date p_processUpto, int p_maxFileLength) throws BTSLBaseException {
        final String METHOD_NAME = "generateTransactionFileAndWriteData";
        if (LOGGER.isDebugEnabled()) {
            LOGGER
                .debug(
                    "generateTransactionFileAndWriteData",
                    " Entered: p_gatewayCodeType=" + p_gatewayCodeType + " ,p_map=" + p_map + ",p_gatewayCode=" + p_gatewayCode + ",p_type=" + p_type + ",p_fileExt=" + p_fileExt + ",p_fileLabel=" + p_fileLabel + ",p_processUpto=" + p_processUpto + ",p_maxFileLength=" + p_maxFileLength);
        }
        int recordsWrittenInFile = 1;
        PrintWriter out = null;
        int fileNumber = 0;
        final String seperator = "_";
        String fileName = null;
        File newFile = null;
        String fileData = null;
        String fileHeader = null;
        String fileFooter = null;
        String tempFileName = null; // to store the file name, which is used
        // during creation of the second file on
        // same date.

        Iterator itr = null;
        try {
            // generating file name
            fileNumber = 1;
            if (!BTSLUtil.isNullString(_o2cTxnConfigureParam)) {
                for (int i = 0; i < p_gatewayCode.length; i++) {
                    fileName = p_gatewayCode[i] + seperator;
                }
                for (int i = 0; i < p_type.length; i++) {
                    fileName = fileName + p_type[i] + seperator;
                }
            } else {
                fileName = p_gatewayCodeType[0] + seperator;
            }

            tempFileName = fileName;
            // if the length of file number is 1, two zeros are added as prefix
            if (Integer.toString(fileNumber).length() == 1) {
                fileName = p_dirName + File.separator + fileName + "00" + fileNumber + p_fileExt;
            } else if (Integer.toString(fileNumber).length() == 2) {
                fileName = p_dirName + File.separator + fileName + "0" + fileNumber + p_fileExt;
            } else if (Integer.toString(fileNumber).length() == 3) {
                fileName = p_dirName + File.separator + fileName + fileNumber + p_fileExt;
            }

            LOGGER.debug("generateTransactionFileAndWriteData", "  fileName=" + fileName);

            newFile = new File(fileName);
            _fileNameLst.add(fileName);
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
            // addition of header and footer optional on the basis of passed
            // argument Y or N

            if ("Y".equalsIgnoreCase(_addHeaderFooter)) {
                fileHeader = constructFileHeader(p_processUpto, fileNumber, p_fileLabel);
                out.write(fileHeader);
            }
            // traverse the array contains the gatewaycode and type string like
            // EXTGW1:O2C,EXTGW1:C2C,EXTGW2:O2C,EXTGW2:C2C etc....
            for (int i = 0; i < p_gatewayCodeType.length; i++) {
                final ArrayList arrList = (ArrayList) p_map.get(p_gatewayCodeType[i]);
                if (arrList != null) {
                    itr = arrList.iterator();
                    while (itr.hasNext()) {
                        fileData = recordsWrittenInFile + "," + itr.next().toString();
                        out.write(fileData + "\n");
                        recordsWrittenInFile++;
                        if (recordsWrittenInFile > p_maxFileLength) {
                            if ("Y".equalsIgnoreCase(_addHeaderFooter)) {
                                fileFooter = constructFileFooter(recordsWrittenInFile - 1);
                                out.write(fileFooter);
                            }
                            recordsWrittenInFile = 1;
                            fileNumber = fileNumber + 1;
                            

                            // if the length of file number is 1, two zeros are
                            // added as prefix
                            if (Integer.toString(fileNumber).length() == 1) {
                                fileName = p_dirName + File.separator + tempFileName + "00" + fileNumber + p_fileExt;
                            } else if (Integer.toString(fileNumber).length() == 2) {
                                fileName = p_dirName + File.separator + tempFileName + "0" + fileNumber + p_fileExt;
                            } else if (Integer.toString(fileNumber).length() == 3) {
                                fileName = p_dirName + File.separator + tempFileName + fileNumber + p_fileExt;
                            }
                            LOGGER.debug("generateTransactionFileAndWriteData", "  fileName=" + fileName);
                            //out.close();
                            //out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                            
                            newFile = new File(fileName);
                            BTSLUtil.closeOpenStream(out, newFile);
                            _fileNameLst.add(fileName);
                            
                            // addition of header and footer optional on the
                            // basis of entry in Constants.props
                            if ("Y".equalsIgnoreCase(_addHeaderFooter)) {
                                fileHeader = constructFileHeader(p_processUpto, fileNumber, p_fileLabel);
                                out.write(fileHeader);
                            }
                        }
                    }
                    // clear array list
                    arrList.clear();
                }
            }
            // if number of records are not zero then footer is appended
            if (recordsWrittenInFile > 1) {
                // header and footer optional on the basis of entry in
                // Constants.props
                if ("Y".equalsIgnoreCase(_addHeaderFooter)) {
                    fileFooter = constructFileFooter(recordsWrittenInFile - 1);
                    out.write(fileFooter);
                }
            } else {
                boolean isDeleted = newFile.delete();
                if(isDeleted){
                 LOGGER.debug(METHOD_NAME, "File deleted successfully");
                }
                _fileNameLst.remove(_fileNameLst.size() - 1);
            }
        } catch (Exception e) {
            deleteAllFiles();
            LOGGER.debug("generateTransactionFileAndWriteData", "Exception: " + e.getMessage());
            LOGGER.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "O2CExtTransactionProcess[generateTransactionFileAndWriteData]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("O2CExtTransactionProcess", "generateTransactionFileAndWriteData", PretupsErrorCodesI.O2CEXT_ERROR_EXCEPTION);
        } finally {
        	try{
        		if (out != null) {
                    out.close();
                }
        	}catch(Exception e){
        		LOGGER.errorTrace(METHOD_NAME, e);
        	}
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("generateTransactionFileAndWriteData", "Exiting ");
            }
        }
    }

    /**
     * @param p_con
     *            Connection
     * @param p_processId
     *            String
     * @throws BTSLBaseException
     * @return int
     */
    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) throws BTSLBaseException {
        final String METHOD_NAME = "markProcessStatusAsComplete";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("markProcessStatusAsComplete", " Entered:  p_processId:" + p_processId);
        }
        int updateCount = 0;
        final Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            LOGGER.errorTrace(METHOD_NAME, e);
            LOGGER.error("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "O2CExtTransactionProcess[markProcessStatusAsComplete]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("O2CExtTransactionProcess", "markProcessStatusAsComplete", PretupsErrorCodesI.O2CEXT_ERROR_EXCEPTION);
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;

    }

    /**
     * This method will delete all the files if some error is encountered
     * after file creation and files need to be deleted.
     * 
     * @throws BTSLBaseException
     * @return void
     */
    private static void deleteAllFiles() throws BTSLBaseException {
        final String METHOD_NAME = "deleteAllFiles";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("deleteAllFiles", " Entered: ");
        }
        int size = 0;
        if (_fileNameLst != null) {
            size = _fileNameLst.size();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("deleteAllFiles", " : Number of files to be deleted " + size);
        }
        String fileName = null;
        File newFile = null;
        for (int i = 0; i < size; i++) {
            try {
                fileName = (String) _fileNameLst.get(i);
                newFile = new File(fileName);
                boolean isDeleted = newFile.delete();
                if(isDeleted){
                 LOGGER.debug(METHOD_NAME, "File deleted successfully");
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("", fileName + " file deleted");
                }
            } catch (Exception e) {
                LOGGER.error("deleteAllFiles", "Exception " + e.getMessage());
                LOGGER.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CExtTransactionProcess[deleteAllFiles]", "", "",
                    "", "Exception:" + e.getMessage());
                throw new BTSLBaseException("O2CExtTransactionProcess", "deleteAllFiles", PretupsErrorCodesI.O2CEXT_ERROR_EXCEPTION);
            }
        }// end of for loop
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CExtTransactionProcess[deleteAllFiles]", "", "", "",
            " Message: O2CExtTransactionProcess process has found some error, so deleting all the files.");
        if (_fileNameLst != null && _fileNameLst.isEmpty()) {
            _fileNameLst.clear();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("deleteAllFiles", " : Exiting.............................");
        }
    }

    /**
     * This method is used to constuct file header
     * 
     * @param p_beingProcessedDate
     *            Date
     * @param p_fileNumber
     *            long
     * @param p_fileLabel
     *            String
     * @return String
     */
    private static String constructFileHeader(Date p_beingProcessedDate, long p_fileNumber, String p_fileLabel) {
        final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
        final StringBuffer fileHeaderBuf = new StringBuffer("");
        fileHeaderBuf.append("\n" + " Today Date=" + sdf.format(new Date()));
        fileHeaderBuf.append("\n" + " Transaction Data For Date=" + sdf.format(p_beingProcessedDate));
        fileHeaderBuf.append("\n" + " File Number=" + p_fileNumber);
        fileHeaderBuf.append("\n" + p_fileLabel + "\n");

        return fileHeaderBuf.toString();
    }

    /**
     * This method is used to constuct file footer
     * 
     * @param p_noOfRecords
     *            long
     * @return String
     */
    private static String constructFileFooter(long p_noOfRecords) {
        StringBuffer fileHeaderBuf = null;
        fileHeaderBuf = new StringBuffer("");
        fileHeaderBuf.append(" Number of records=" + p_noOfRecords);
        return fileHeaderBuf.toString();
    }

    /**
     * This method will copy all the created files to another location.
     * the process will generate files in a particular directroy. if the process
     * thats has to read files strarts before copletion of the file generation,
     * errors will occur. so a different directory is created and files are
     * moved to that final directory.
     * 
     * @param p_oldDirectoryPath
     *            String
     * @param p_finalDirectoryPath
     *            String
     * @param p_processId
     *            String
     * @param p_beingProcessedDate
     *            Date
     * @throws BTSLBaseException
     * @return String
     */
    private static void moveFilesToFinalDirectory(String p_oldDirectoryPath, String p_finalDirectoryPath, String p_processId, Date p_beingProcessedDate) throws BTSLBaseException {
        final String METHOD_NAME = "moveFilesToFinalDirectory";
        if (LOGGER.isDebugEnabled()) {
            LOGGER
                .debug(
                    "moveFilesToFinalDirectory",
                    " Entered: p_oldDirectoryPath=" + p_oldDirectoryPath + " p_finalDirectoryPath=" + p_finalDirectoryPath + " p_processId=" + p_processId + " p_beingProcessedDate=" + p_beingProcessedDate);
        }

        String oldFileName = null;
        String newFileName = null;
        File oldFile = null;
        File newFile = null;
        final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
        File parentDir = new File(p_finalDirectoryPath);
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        p_beingProcessedDate = BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate);
        // child directory name includes a file name and being processed date,
        // month and year

        final String oldDirPath = p_oldDirectoryPath + File.separator + p_processId + "_" + sdf.format(p_beingProcessedDate);
        final String newDirPath = p_finalDirectoryPath + File.separator + p_processId + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_beingProcessedDate
            .toString().substring(5, 7) + p_beingProcessedDate.toString().substring(2, 4);
        File oldDir = new File(oldDirPath);
        File newDir = new File(newDirPath);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("moveFilesToFinalDirectory", " newDirPath=" + newDirPath);
        }

        final int size = _fileNameLst.size();
        try {
            for (int i = 0; i < size; i++) {
                oldFileName = (String) _fileNameLst.get(i);
                oldFile = new File(oldFileName);
                newFileName = oldFileName.replace(p_oldDirectoryPath, p_finalDirectoryPath);
                newFile = new File(newFileName);
                if(oldFile.renameTo(newFile))
                {
                	LOGGER.debug(METHOD_NAME, "File renamed successfully");
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("moveFilesToFinalDirectory", " File " + oldFileName + " is moved to " + newFileName);
                }
            }// end of for loop
            _fileNameLst.clear();
            if (oldDir.exists()) {
            	boolean isDeleted = oldDir.delete();
                if(isDeleted){
                	LOGGER.debug(METHOD_NAME, "Directory deleted successfully");
                }
            }
            LOGGER.debug("moveFilesToFinalDirectory", " File " + oldFileName + " is moved to " + newFileName);
        } catch (Exception e) {
            LOGGER.error("moveFilesToFinalDirectory", "Exception " + e.getMessage());
            LOGGER.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CExtTransactionProcess[moveFilesToFinalDirectory]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("O2CExtTransactionProcess", "deleteAllFiles", PretupsErrorCodesI.O2CEXT_ERROR_EXCEPTION);
        } finally {
            if (oldFile != null) {
                oldFile = null;
            }
            if (newFile != null) {
                newFile = null;
            }
            if (parentDir != null) {
                parentDir = null;
            }
            if (newDir != null) {
                newDir = null;
            }
            if (oldDir != null) {
                oldDir = null;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("moveFilesToFinalDirectory", "Exiting.. ");
            }
        } // end of finally
    }
}
