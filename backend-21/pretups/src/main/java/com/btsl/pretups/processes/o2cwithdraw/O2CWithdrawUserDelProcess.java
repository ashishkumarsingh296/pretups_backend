package com.btsl.pretups.processes.o2cwithdraw;

/**
 * @(#) O2CWithdrawUserDelete.java
 *      Copyright(c) 2010, Comviva Technologies Ltd.
 *      All Rights Reserved
 *      <this process is basically used for the deleting the user and
 *      withdrawing their balances to the network stock >
 *      ------------------------------------------------------------------------
 *      -------------------------
 *      Author Date History
 *      ------------------------------------------------------------------------
 *      -------------------------
 *      Vikram Kumar 26/feb/2010 Initial Creation
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGeneratorDAO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockDAO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.txn.pretups.channel.profile.businesslogic.CommissionProfileTxnDAO;

public class O2CWithdrawUserDelProcess {
    private static Log _log = LogFactory.getLog(O2CWithdrawUserDelProcess.class.getName());
    private static HashMap _networkProductMap = new HashMap();
    // o2c withdraw form users by vikram
    public static final String O2C_WDHW_PROCS_ERR_ARG_MISSING = "6850";
    public static final String O2C_WDHW_PROCS_ERR_ASS_PROD_NTFOND = "6851";
    public static final String O2C_WDHW_PROCS_ERR_FILE_NOT_EXIST = "6852";
    public static final String O2C_WDHW_PROCS_ERR_CONN_NULL = "6853";
    public static final String O2C_WDHW_PROCS_ERR_NTADM_NTFND = "6854";
    public static final String O2C_WDHW_PROCS_MISSING_CONST_FILE = "6855";
    public static final String O2C_WDHW_PROCS_MISSING_LOG_FILE = "6866";
    public static final String O2C_WDHW_PROCS_UPLOAD_GENERAL_ERROR = "6867";
    public static final String ERROR_USER_TRANSFER_NOT_ALLOWED_NOW = "6107";
    public static final String ERROR_UPDATING_DATABASE = "7113";
    private static IDGeneratorDAO _idGeneratorDAO = new IDGeneratorDAO();
    private static OperatorUtilI calculatorI = null;

    /**
     * Main Method. This method creates an instance of the class
     * MNPFileUploadProcess. After creating the instance, the methods
     * reads the network code Constant props and Process log config file as
     * parameter.
     * 
     * @param args
     *            1-O2cWdhConstants file
     *            2-O2cWdhLogConfig file
     *            3-userLists file
     *            4-NetworkCode
     */

    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        long startTime = (new Date()).getTime();
        try {
            if (args.length != 4) {
                // System.out.println("Four arguments are required  : O2CWithdrawUserDelProcess [O2cWdhConstants file] [O2cWdhLogConfig file] [userLists file] [NetworkCode networkCode]");
                _log.error("O2CWithdrawUserDelProcess main()", " Usage : O2CWithdrawUserDelProcess [O2cWdhConstants file] [O2cWdhLogConfig file] [userLists file] [NetworkCode networkCode]");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "O2CWithdrawUserDelProcess[main]", "", "", "", "Improper usage. Usage : O2CWithdrawUserDelProcess [O2cWdhConstants file] [O2cWdhLogConfig file] [userLists file] [NetworkCode networkCode]");
                throw new BTSLBaseException("O2CWithdrawUserDelProcess ", " main ", O2CWithdrawUserDelProcess.O2C_WDHW_PROCS_ERR_ARG_MISSING);// change
                                                                                                                                              // error
                                                                                                                                              // code
            }

            new O2CWithdrawUserDelProcess().process(args);
        } catch (BTSLBaseException be) {
            _log.error("main", " : Exiting BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            _log.error("main ", ": Exiting Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CWithdrawUserDelProcess: main", "", "", "", "Exiting the exception of main");
            _log.errorTrace(METHOD_NAME, e);
        }// end of outer Exception
        finally {
            long endTime = (new Date()).getTime();
            // ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * This method takes the file name from argument passed. then calls a method
     * processRecords which process
     * all the records in the file.
     * 
     * @param String
     *            [] p_args
     */

    public void process(String[] p_args) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled())
            _log.debug(METHOD_NAME, "Entered p_args: " + p_args);
        Connection con = null;
        int noRecord = 0;
        try {
            // load Constants.props and ProccessLogConfig file
            loadCachesAndLogFiles(p_args[0], p_args[1]);
            NetworkPrefixCache.loadNetworkPrefixesAtStartup();

            // //opening the connection
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                // System.out.println(" Could not connect to database. Please make sure that database server is up..............");
                _log.error("process ", ": Could not connect to database. Please make sure that database server is up..............");
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CWithdrawUserDelProcess[process]", "", "", "", "Could not connect to Database");
                throw new BTSLBaseException("O2CWithdrawUserDelProcess", METHOD_NAME, O2CWithdrawUserDelProcess.O2C_WDHW_PROCS_ERR_CONN_NULL);
            }
            try {
                // main processing of the file happens here.
                // this will process records in the file one by one.
                noRecord = processRecords(con, p_args[2], p_args[3]);
            } catch (BTSLBaseException be) {
                _log.error(METHOD_NAME, "Error occurred during processing records .Database not updated. BTSLBaseException : " + be.getMessage());
                _log.errorTrace(METHOD_NAME, be);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CWithdrawUserDelProcess[main]", "", "", "", "Error occurred during processing records. Database not updated. BTSLBaseException : " + be.getMessage());
                try {
                    if (con != null)
                        con.rollback();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                return;
            }
        } catch (BTSLBaseException be) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            return;

        }// end of BTSLBaseException
        catch (Exception ex) {
            // Rollbacking the transaction
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error(METHOD_NAME, "BTSLBaseException " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CWithdrawUserDelProcess[process]", "", "", "", "Exception:" + ex.getMessage());
            return;

        }// end of Exception
        finally {
            // clossing database connection
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                if (noRecord == 0)
                    _log.debug(METHOD_NAME, " No records in the file were processed. No updation to the database. ");
                else
                    _log.debug(METHOD_NAME, "Processed Msidsns up to record no. :" + noRecord + " in file: " + p_args[2]);
                _log.debug(METHOD_NAME, " Exited ");
            }
            ConfigServlet.destroyProcessCache();
        }// end of finally
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
        if (_log.isDebugEnabled())
            _log.debug(" loadCachesAndLogFiles ", " Entered with p_arg1=" + p_arg1 + " p_arg2=" + p_arg2);
        File logconfigFile = null;
        File constantsFile = null;
        try {
            constantsFile = new File(p_arg1);
            if (!constantsFile.exists()) {
                // System.out.println("O2CWithdrawUserDelProcess loadCachesAndLogFiles Constants file not found on location:: "+constantsFile.toString());
                _log.error("O2CWithdrawUserDelProcess[loadCachesAndLogFiles]", " Constants file not found on location:: " + constantsFile.toString());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "O2CWithdrawUserDelProcess[loadCachesAndLogFiles]", "", "", "", " The Constants file doesn't exists at the path specified. ");
                throw new BTSLBaseException("O2CWithdrawUserDelProcess ", " loadCachesAndLogFiles ", O2CWithdrawUserDelProcess.O2C_WDHW_PROCS_MISSING_CONST_FILE);
            }

            logconfigFile = new File(p_arg2);
            if (!logconfigFile.exists()) {
                // System.out.println("O2CWithdrawUserDelProcess loadCachesAndLogFiles Logconfig file not found on location:: "+logconfigFile.toString());
                _log.error("O2CWithdrawUserDelProcess[loadCachesAndLogFiles]", " ProcessLogConfig file not found on location:: " + logconfigFile.toString());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CWithdrawUserDelProcess[loadCachesAndLogFiles]", "", "", "", " The ProcessLogConfig file doesn't exists  at the path specified. ");
                throw new BTSLBaseException("O2CWithdrawUserDelProcess ", "loadCachesAndLogFiles ", O2CWithdrawUserDelProcess.O2C_WDHW_PROCS_MISSING_LOG_FILE);
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (BTSLBaseException be) {
            _log.error("O2CWithdrawUserDelProcess[loadCachesAndLogFiles]", "BTSLBaseException =" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("O2CWithdrawUserDelProcess[loadCachesAndLogFiles]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CWithdrawUserDelProcess[loadCachesAndLogFiles]", "", "", "", "Exception=" + e.getMessage());
            throw new BTSLBaseException("O2CWithdrawUserDelProcess ", " loadCachesAndLogFiles ", O2CWithdrawUserDelProcess.O2C_WDHW_PROCS_UPLOAD_GENERAL_ERROR);
        }// end of Exception
        finally {
            if (logconfigFile != null)
                logconfigFile = null;
            if (constantsFile != null)
                constantsFile = null;
            if (_log.isDebugEnabled())
                _log.debug("O2CWithdrawUserDelProcess[loadCachesAndLogFiles]", " Exiting..........");
        }// end of finally
    }

    /**
     * This method processes the records in the files. creates the transaction
     * details(channel transfer and network stock transactions)
     * Creates the logs as well
     * 
     * @param Connection
     *            p_con
     * @param String
     *            p_file
     * @param String
     *            p_networkId
     * @throws BTSLBaseException
     * @throws Exception
     */

    private int processRecords(Connection p_con, String p_file, String p_networkId) throws BTSLBaseException {
        final String METHOD_NAME = "processRecords";
        if (_log.isDebugEnabled())
            _log.debug(METHOD_NAME, " Entered p_file : " + p_file + " p_networkId :  " + p_networkId);
        boolean processFile = false;
        FileReader fileReader = null; // file reader
        BufferedReader bufferReader = null;
        int count = 0;
        String msisdnStr = null;
        Date currentDate = new Date(System.currentTimeMillis());
        StringBuffer invalidMsisdnBuf = new StringBuffer();
        StringBuffer processedMsisdnBuf = new StringBuffer();
        StringBuffer noActionMsisdnBuf = new StringBuffer();
        StringBuffer deletedMsisdnBuf = new StringBuffer();
        String filteredMsisdn = null;
        String msisdnPrefix;
        File unprocessedMsisdnFile = null;
        File processedMsisdnFile = null;
        File noActionMsisdnFile = null;
        File deletedMsisdnFile = null;
        NetworkPrefixVO networkPrefixVO = null;
        UserVO userVO = null;
        ArrayList totMsisdn = new ArrayList();
        FileWriter unprocessedFileWriter = null;
        FileWriter processedFileWriter = null;
        FileWriter noActionfFleWriter = null;
        FileWriter deletedMsisdnFleWriter = null;
        try {

            File userListFile = new File(p_file);
            if (!userListFile.exists()) {
                _log.error("O2CWithdrawUserDelProcess[processRecords]", " UserList file not found on location:: " + userListFile.toString());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "O2CWithdrawUserDelProcess[processRecords]", "", "", "", " The UserList file doesn't exists at the path specified. ");
                throw new BTSLBaseException("O2CWithdrawUserDelProcess ", " processRecords ", O2CWithdrawUserDelProcess.O2C_WDHW_PROCS_ERR_FILE_NOT_EXIST);
            }
            String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            try {
                calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
            }
            String delim = "\n"; // always , separated or new line
            fileReader = new FileReader("" + p_file);
            if (fileReader != null)
                bufferReader = new BufferedReader(fileReader);
            else
            	bufferReader = null;
            if (bufferReader != null && bufferReader.ready()) {
                String tempStr = null;
                StringTokenizer parser = null;
                // read the file line by line untill line read is null
                while (!BTSLUtil.isNullString(tempStr = bufferReader.readLine())) {
                    parser = new StringTokenizer(tempStr, delim);
                    while (parser.hasMoreTokens()) {
                        msisdnStr = parser.nextToken().trim();
                        totMsisdn.add(msisdnStr);
                    }
                }
            } else {
                if (_log.isDebugEnabled())
                    _log.debug(METHOD_NAME, " No records could be processed. Can't create file reader for the file : " + p_file);
                throw new BTSLBaseException("O2CWithdrawUserDelProcess", "process", O2CWithdrawUserDelProcess.O2C_WDHW_PROCS_ERR_FILE_NOT_EXIST);
            }
            bufferReader.close();
            if (totMsisdn.isEmpty()) {
                if (_log.isDebugEnabled())
                    _log.debug(METHOD_NAME, " No records found for processing in the file: " + p_file);
                return count;
            }
            // for creating more logs..
            // three log files will be created as
            // inputfile__O2CUnprocessedMsisdn.txt and
            // inputfile_O2CProcessedMsisdn.txt
            // third file will be inputfile__NoActionMsisdn.txt
            try {
                String filePath = p_file.substring(0, p_file.lastIndexOf("."));
                unprocessedMsisdnFile = new File(filePath + "_" + "UnprocessedMsisdn.txt");
                processedMsisdnFile = new File(filePath + "_" + "ProcessedMsisdn.txt");
                noActionMsisdnFile = new File(filePath + "_" + "NoActionMsisdn.txt");
                deletedMsisdnFile = new File(filePath + "_" + "DeletedMsisdn.txt");
                unprocessedMsisdnFile.createNewFile();
                processedMsisdnFile.createNewFile();
                noActionMsisdnFile.createNewFile();
                deletedMsisdnFile.createNewFile();
                unprocessedFileWriter = new FileWriter("" + unprocessedMsisdnFile);
                if (unprocessedFileWriter != null)
                    unprocessedFileWriter.write(" Unprocessed Msisdns are as :: \n ");
                else if (_log.isDebugEnabled())
                    _log.debug(METHOD_NAME, "Could not initialize file writer for unprocessed msisdns ");
                processedFileWriter = new FileWriter("" + processedMsisdnFile);
                if (processedFileWriter != null)
                    processedFileWriter.write(" Processed Msisdns are as :: \n ");
                else if (_log.isDebugEnabled())
                    _log.debug(METHOD_NAME, "Could not initialize file writer for processed msisdns ");
                noActionfFleWriter = new FileWriter("" + noActionMsisdnFile);
                if (noActionfFleWriter != null)
                    noActionfFleWriter.write(" No action Msisdns are as :: \n ");
                else if (_log.isDebugEnabled())
                    _log.debug(METHOD_NAME, "Could not initialize file writer for no action msisdns ");
                deletedMsisdnFleWriter = new FileWriter("" + deletedMsisdnFile);
                if (deletedMsisdnFleWriter != null)
                    deletedMsisdnFleWriter.write(" Deleted Msisdns are as ::  \n");
                else if (_log.isDebugEnabled())
                    _log.debug(METHOD_NAME, "Could not initialize file writer for Deleted msisdns ");
            } catch (Exception e) {
                _log.error(METHOD_NAME, " While creating log files Exception " + e.getMessage());
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException(this, "Creating log files", "error.general.processing");
            }

            ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            userVO = channelUserDAO.loadOptUserForO2C(p_con, p_networkId);
            if (userVO == null) {
                _log.error("process ", ": No network admin details could be found . Please make sure that network code given is right..............");
                // EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"O2CWithdrawUserDelProcess[processRecords]","","","","Network admin details not found in Database");
                throw new BTSLBaseException("O2CWithdrawUserDelProcess", "process", O2CWithdrawUserDelProcess.O2C_WDHW_PROCS_ERR_NTADM_NTFND);
            }
            // load the network products defined and set it into hash map.
            NetworkProductVO networkProductVO = null;
            NetworkProductDAO networkProductDao = new NetworkProductDAO();
            ArrayList networkProductList = networkProductDao.loadProductListForXfr(p_con, null, p_networkId);
            int networkProductLists = networkProductList.size();
            for (int i = 0; i < networkProductLists ; i++) {
                networkProductVO = (NetworkProductVO) networkProductList.get(i);
                _networkProductMap.put(networkProductVO.getProductCode(), networkProductVO);
            }
            CommissionProfileDAO commPrDAO = new CommissionProfileDAO();
            CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();
            // UserDAO userDAO= new UserDAO();
            // userVO = channelUserDAO.loadOptUserForO2C(p_con, p_networkCode);
            // for all the msisdns withdraw their balances create transaction
            // details
            int insertCount = -1;
            int deleteCount = -1;
            ChannelUserVO channelUserVO = null;
            UserBalancesVO userBalanceVO = null;
            ChannelTransferVO channelTransferVO = null;
            ChannelTransferItemsVO channelTransferItemsVO = null;
            ArrayList userBalanceList = null;
            String commProfileLatestVer = null;
            ArrayList tempChannelTransferItemsList = null;
            ChannelTransferDAO channelTrfDAO = new ChannelTransferDAO();

            boolean isUserDeleteRequired = false;
            String checkFlag = BTSLUtil.NullToString(Constants.getProperty("USER_DELETE_REQUIRED"));
            if ("Y".equalsIgnoreCase(checkFlag))
                isUserDeleteRequired = true;

            while (totMsisdn.size() != count) {
                msisdnStr = (String) totMsisdn.get(count);
                count++;
                // check if the msisdn is valid or not. If not valid then add it
                // into p_invalidMsisdn string
                // also create the logs regarding the reason for failure.
                filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdnStr);
                if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(METHOD_NAME, " Not a valid MSISDN : " + msisdnStr);
                        invalidMsisdnBuf.append(msisdnStr + ",\n");
                    }
                    O2CWdhUserDelProcessLog.log("File Processing", msisdnStr, count, "Fail", "Not a valid mobile number ", " Please check the Msisdn ");
                    unprocessedFileWriter.write(msisdnStr + ",\n");
                    continue;
                }
                networkPrefixVO = null;
                msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                if (networkPrefixVO == null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("processRecords ", " Msisdn prefix not defined for MSISDN: " + msisdnStr);
                        invalidMsisdnBuf.append(msisdnStr + ",\n");
                    }
                    O2CWdhUserDelProcessLog.log("File Processing", msisdnStr, count, "Fail", " Msisdn prefix not defined for MSISDN ", " Please check the Msisdn ");
                    unprocessedFileWriter.write(msisdnStr + ", \n");
                    continue;
                } else if (!networkPrefixVO.getNetworkCode().equals(p_networkId)) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("processRecords ", msisdnStr + " : Msisdn given does not belong to the given NetworkCode : " + p_networkId);
                        invalidMsisdnBuf.append(msisdnStr + ",\n");
                    }
                    O2CWdhUserDelProcessLog.log("File Processing", msisdnStr, count, "Fail", " Msisdn given does not belong to the given NetworkCode ", " Please ensure that Msisdn is of given Network code ");
                    unprocessedFileWriter.write(msisdnStr + ", \n");
                    continue;
                } else {
                    // Load the user balance for the products.
                    // if no balance exist for that user write it in to log and
                    // continue
                    // if balance exist for the user load user details.
                    // and create an O2C withdraw request and execute it .
                    // after execution network stock transaction channel
                    // transfers will be update along with user daily balances
                    // commit the transactions for one msisdn
                    // will store the user balances for diff. products
                    // now load the channel user details
                    insertCount = -1;
                    deleteCount = -1;
                    channelUserVO = channelUserDAO.loadChannelUserDetails(p_con, filteredMsisdn);
                    if (channelUserVO == null) {
                        // if no channel user found
                        if (_log.isDebugEnabled()) {
                            _log.debug(METHOD_NAME, " No channel user found with Msisdn: " + filteredMsisdn);
                            invalidMsisdnBuf.append(msisdnStr + ",\n");
                        }
                        O2CWdhUserDelProcessLog.log("File Processing", msisdnStr, count, "Fail", " No channel user found with Msisdn ", " Give only the msisdn of valid Channel user");
                        unprocessedFileWriter.write(msisdnStr + ", \n");
                        continue;
                    }
                    // for deleting the channel user filling the info
                    channelUserVO.setPreviousStatus(channelUserVO.getStatus());
                    channelUserVO.setStatus(PretupsI.USER_STATUS_DELETED);
                    channelUserVO.setModifiedBy(userVO.getUserID());
                    channelUserVO.setModifiedOn(currentDate);
                    // load the user balance list
                    userBalanceList = this.loadUserBalancesForUserId(p_con, channelUserVO.getUserID());
                    if (userBalanceList.isEmpty()) {
                        // if no channel user found
                        if (_log.isDebugEnabled()) {
                            _log.debug(METHOD_NAME, " No channel user balance found for the Msisdn: " + filteredMsisdn);
                            noActionMsisdnBuf.append(msisdnStr + ",\n");
                        }
                        noActionfFleWriter.write(msisdnStr + ", \n");
                        if (isUserDeleteRequired) {
                            // deleting the channel user
                            deleteCount = this.o2cWithdrawdeleteUser(p_con, channelUserVO);
                            if (deleteCount <= 0) {
                                p_con.rollback();
                                _log.error(METHOD_NAME, "Error: while Deleting User");
                                throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
                            }
                            // creating log file
                            if (_log.isDebugEnabled()) {
                                _log.debug(METHOD_NAME, " Channel user have been deleted successfully: " + filteredMsisdn);
                                deletedMsisdnBuf.append(msisdnStr + ",\n");
                            }
                            O2CWdhUserDelProcessLog.log("File Processing", msisdnStr, count, "DeleteSucess", " Channel user have been deleted successfully ", " No balance found for user id : " + channelUserVO.getUserID());
                            deletedMsisdnFleWriter.write(msisdnStr + ", \n");
                        }
                        p_con.commit();
                        continue;
                    }
                    // load the latest version of commission profile
                    CommissionProfileSetVO commissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(p_con, channelUserVO.getCommissionProfileSetID(), currentDate);
                    commProfileLatestVer = commissionProfileSetVO.getCommProfileVersion();
                    if (BTSLUtil.isNullString(commProfileLatestVer)) {
                        if (_log.isDebugEnabled()) {
                            _log.debug(METHOD_NAME, "No commission profile version found for channel user found with Msisdn " + filteredMsisdn);
                            invalidMsisdnBuf.append(msisdnStr + ",\n");
                        }
                        O2CWdhUserDelProcessLog.log("File Processing", msisdnStr, count, "Fail", "No commission profile version found for channel user found with Msisdn", "Please check Commission profile details");
                        unprocessedFileWriter.write(msisdnStr + ",\n");
                        continue;
                    }
                    // now for every product deduct the user balance through O2C
                    // withdraw
                    int userBalanceLists = userBalanceList.size();
                    for (int i = 0; i < userBalanceLists; i++) {
                        userBalanceVO = (UserBalancesVO) userBalanceList.get(i);
                        if (userBalanceVO.getBalance() == 0 && i == (userBalanceList.size() - 1)) {
                            // user do not have balance for any of the products
                            if (_log.isDebugEnabled()) {
                                _log.debug(METHOD_NAME, "Channel user does not have balance for any product with msisdn: " + filteredMsisdn + " for product: " + userBalanceVO.getProductName());
                                noActionMsisdnBuf.append(msisdnStr + ",\n");
                            }
                            noActionfFleWriter.write(msisdnStr + ", \n");
                            if (isUserDeleteRequired) {
                                // deleting the channel user
                                deleteCount = this.o2cWithdrawdeleteUser(p_con, channelUserVO);
                                if (deleteCount <= 0) {
                                    p_con.rollback();
                                    _log.error(METHOD_NAME, "Error: while Deleting User");
                                    throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
                                }
                                // creating log file
                                if (_log.isDebugEnabled()) {
                                    _log.debug(METHOD_NAME, " Channel user have been deleted successfully: " + filteredMsisdn);
                                    deletedMsisdnBuf.append(msisdnStr + ",\n");
                                }
                                O2CWdhUserDelProcessLog.log("File Processing", msisdnStr, count, "DeleteSucess", " Channel user have been deleted successfully ", " User does not have any balance user_id: " + channelUserVO.getUserID());
                                deletedMsisdnFleWriter.write(msisdnStr + ", \n");
                            }
                            p_con.commit();
                            continue;
                        } else if (userBalanceVO.getBalance() == 0)
                            continue;
                        else {
                            // if user have balance for the product then go for
                            // O2C withdraw
                            channelUserVO.setCommissionProfileSetVersion(commProfileLatestVer);
                            // prepare details for channel transfers items
                            channelTransferVO = new ChannelTransferVO();
                            tempChannelTransferItemsList = new ArrayList();
                            channelTransferItemsVO = prepareChannelTransferItemsVO(p_con, channelUserVO, userBalanceVO);
                            tempChannelTransferItemsList.add(channelTransferItemsVO);
                            // this method loads and calculates the various
                            // taxes applicable on the product
                            ChannelTransferBL.calculateMRPWithTaxAndDiscount(tempChannelTransferItemsList, PretupsI.TRANSFER_TYPE_O2C);
                            channelTransferVO.setChannelTransferitemsVOList(tempChannelTransferItemsList);
                            // prepare details for channel transfers
                            prepareChannelTransferVO(channelTransferVO, channelTransferItemsVO, currentDate, channelUserVO, userVO);
                            // generate O2C withdraw transfer id
                            // genrateWithdrawID(p_con, channelTransferVO );
                            ChannelTransferBL.genrateWithdrawID(channelTransferVO);
                            channelTransferItemsVO.setTransferID(channelTransferVO.getTransferID());
                            channelTransferVO.setControlTransfer(PretupsI.YES);
                            // performs all the transaction for the withdraw
                            // opertaion
                            transactionApproval(p_con, channelTransferVO, channelTransferItemsVO, userVO.getUserID(), currentDate);
                            // updates the channel_transfer and
                            // channel_transfer_items table
                            OneLineTXNLog.log(channelTransferVO, null);
                            insertCount = channelTrfDAO.addChannelTransfer(p_con, channelTransferVO);
                            if (insertCount < 1) {
                                p_con.rollback();
                                throw new BTSLBaseException(this, METHOD_NAME, O2CWithdrawUserDelProcess.ERROR_USER_TRANSFER_NOT_ALLOWED_NOW);
                            }
                            ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
                        }
                    }
                    // commit connection for one Msisdn
                    if (insertCount > 0) {
                        if (_log.isDebugEnabled()) {
                            _log.debug(METHOD_NAME, " User is successfully withdrawn with its balance and is deleted . for the msisdn: " + msisdnStr);
                            processedMsisdnBuf.append(msisdnStr + ",\n");
                        }
                        processedFileWriter.write(msisdnStr + ", \n");
                        if (isUserDeleteRequired) {
                            // deleting the channel user
                            deleteCount = this.o2cWithdrawdeleteUser(p_con, channelUserVO);
                            if (deleteCount <= 0) {
                                p_con.rollback();
                                _log.error(METHOD_NAME, "Error: while Deleting User");
                                throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
                            }
                            // creating log file
                            if (_log.isDebugEnabled()) {
                                _log.debug(METHOD_NAME, " Channel user have been deleted successfully: " + filteredMsisdn);
                                deletedMsisdnBuf.append(msisdnStr + ",\n");
                            }
                            O2CWdhUserDelProcessLog.log("File Processing ", msisdnStr, count, "WithdrawDELSucess", " User is successfully withdrawn with its balance and is deleted ", " User balance withdrawn");
                            deletedMsisdnFleWriter.write(msisdnStr + ", \n");
                        }
                        p_con.commit();// ankuj
                    }
                }
            }
            if (totMsisdn.size() == count)
                processFile = true; // file processed successfully
            // will always take only .txt file as input. else through exception

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            _log.error(METHOD_NAME, "SQLException " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CWithdrawUserDelProcess[writeFileToDatabase]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CWithdrawUserDelProcess[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
        	if(bufferReader != null)
    		{
    			try{
            		if(bufferReader != null){
            			bufferReader.close();	
            		}
            	}catch(Exception e){
            		 _log.errorTrace("O2CWithdrawUserDelProcess:"+METHOD_NAME, e);
            	}
    		}
        	
        	if(fileReader != null)
    		{
    			try{
            		if(fileReader != null){
            			fileReader.close();	
            		}
            	}catch(Exception e){
            		 _log.errorTrace("O2CWithdrawUserDelProcess:"+METHOD_NAME, e);
            	}
    		}
        	
            if (_log.isDebugEnabled()) {
                if (processedMsisdnBuf.length() != 0)
                    _log.debug(METHOD_NAME, " processed msisdns are : " + processedMsisdnBuf.toString() + " in the file " + p_file);
                if (invalidMsisdnBuf.length() != 0)
                    _log.debug(METHOD_NAME, " Unprocessed msisdns are as : " + invalidMsisdnBuf.toString() + " in the file " + p_file);
                if (noActionMsisdnBuf.length() != 0)
                    _log.debug(METHOD_NAME, " Noaction msisdns are as : " + noActionMsisdnBuf.toString() + " in the file " + p_file);
            }
            _log.debug(METHOD_NAME, " processed till record no: " + count + " process executed successfully:: " + processFile);
            System.out.println("processRecords:: processed till record no: " + count + " process executed successfully ::" + processFile);
            try {
                // write all the buffers and close all log files writer
                // noActionfFleWriter.write(noActionMsisdnBuf.toString());
                noActionfFleWriter.close();
                // unprocessedFileWriter.write(invalidMsisdnBuf.toString());
                unprocessedFileWriter.close();
                // processedFileWriter.write(processedMsisdnBuf.toString());
                processedFileWriter.close();
                // deletedMsisdnFleWriter.write(deletedMsisdnBuf.toString());
                deletedMsisdnFleWriter.close();

            } catch (Exception e) {
                _log.error(METHOD_NAME, "Exception:: while writing all the log files " + e.getMessage());
                _log.errorTrace(METHOD_NAME, e);
            }
        }
        return count;
    }

    /**
     * This method prepares the ChannelTransferItemsVO from the arguments
     * channelTransferVO,userBalanceVO
     * channelUserVO, and userVO
     * 
     * @param CoNNECI
     * @param p_channelTransferVO
     * @param p_userBalanceVO
     * @throws BTSLBaseException
     */
    private ChannelTransferItemsVO prepareChannelTransferItemsVO(Connection p_con, ChannelUserVO p_channelUserVO, UserBalancesVO p_userBalanceVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("prepareChannelTransferItemsVO", "Entering  : p_channelUserVO" + p_channelUserVO + "UserBalancesVO" + p_userBalanceVO);
        NetworkProductVO networkProductVO = null;
        ChannelTransferItemsVO channelTransferItemsVO = null;
        networkProductVO = (NetworkProductVO) _networkProductMap.get(p_userBalanceVO.getProductCode());
        // default commission rate
        double commRate = 0.0;
        if (p_userBalanceVO.getProductCode().equals(networkProductVO.getProductCode())) {
            channelTransferItemsVO = new ChannelTransferItemsVO();
            channelTransferItemsVO.setProductType(networkProductVO.getProductType());
            channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
            channelTransferItemsVO.setProductCode(networkProductVO.getProductCode());
            channelTransferItemsVO.setProductName(networkProductVO.getProductName());
            channelTransferItemsVO.setShortName(networkProductVO.getShortName());
            channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
            channelTransferItemsVO.setProductCategory(networkProductVO.getProductCategory());
            channelTransferItemsVO.setErpProductCode(networkProductVO.getErpProductCode());
            channelTransferItemsVO.setStatus(networkProductVO.getStatus());
            channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());
            channelTransferItemsVO.setModuleCode(networkProductVO.getModuleCode());
            channelTransferItemsVO.setProductUsage(networkProductVO.getProductUsage());
            channelTransferItemsVO.setRequestedQuantity(PretupsBL.getDisplayAmount(p_userBalanceVO.getBalance()));
            channelTransferItemsVO.setRequiredQuantity(p_userBalanceVO.getBalance());
            // setting the default value for this
            channelTransferItemsVO.setTax1Type(PretupsI.AMOUNT_TYPE_PERCENTAGE);
            channelTransferItemsVO.setTax1Rate(commRate);
            channelTransferItemsVO.setTax2Type(PretupsI.AMOUNT_TYPE_PERCENTAGE);
            channelTransferItemsVO.setTax2Rate(commRate);
            channelTransferItemsVO.setTax3Type(PretupsI.AMOUNT_TYPE_PERCENTAGE);
            channelTransferItemsVO.setTax3Rate(commRate);
            channelTransferItemsVO.setCommType(PretupsI.AMOUNT_TYPE_PERCENTAGE);
            channelTransferItemsVO.setCommRate(commRate);
            channelTransferItemsVO.setCommProfileDetailID(PretupsI.NOT_APPLICABLE);
            channelTransferItemsVO.setRequiredQuantity(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
            channelTransferItemsVO.setDiscountType(PretupsI.AMOUNT_TYPE_PERCENTAGE);
            channelTransferItemsVO.setDiscountRate(commRate);
        } else {
            // System.out.println("  Associated product for the user could not be found. ");
            _log.error("prepareChannelTransferItemsVO ", ": Associated product for the user could not be found for the user id : " + p_channelUserVO.getUserID());
            // EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"O2CWithdrawUserDelProcess[prepareChannelTransferItemsVO]","","","","Network admin details not found in Database");
            throw new BTSLBaseException("O2CWithdrawUserDelProcess", "prepareChannelTransferItemsVO", O2CWithdrawUserDelProcess.O2C_WDHW_PROCS_ERR_ASS_PROD_NTFOND);
        }
        if (_log.isDebugEnabled())
            _log.debug("prepareChannelTransferItemsVO", "Exiting : channelTransferItemsVO" + channelTransferItemsVO);
        return channelTransferItemsVO;
    }

    /**
     * This method prepares the ChannelTransferVO from the arguments
     * channelTransferVO, requestVO,
     * channelUserVO, filteredPrdList and userVO
     * 
     * @param p_channelTransferVO
     * @param p_channelTransferItemsVO
     * @param p_curDate
     * @param p_channelUserVO
     * @param p_userVO
     * @return ChannelTransferVO
     * @throws BTSLBaseException
     */

    private ChannelTransferVO prepareChannelTransferVO(ChannelTransferVO p_channelTransferVO, ChannelTransferItemsVO p_channelTransferItemsVO, Date p_curDate, ChannelUserVO p_channelUserVO, UserVO p_userVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("prepareChannelTransferVO", "Entering  : p_channelTransferVO" + p_channelTransferVO + "p_channelUserVO" + p_channelUserVO + "p_userVO" + p_userVO);

        p_channelTransferVO.setNetworkCode(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setNetworkCodeFor(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setGraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
        p_channelTransferVO.setCategoryCode(p_channelUserVO.getCategoryCode());
        p_channelTransferVO.setReceiverGradeCode("");
        p_channelTransferVO.setSenderGradeCode(p_channelUserVO.getUserGrade());
        p_channelTransferVO.setFromUserID(p_channelUserVO.getUserID());
        p_channelTransferVO.setFromUserCode(p_channelUserVO.getUserCode());
        p_channelTransferVO.setToUserID(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setToUserCode(p_userVO.getUserCode());
        p_channelTransferVO.setTransferDate(p_curDate);
        p_channelTransferVO.setCommProfileSetId(p_channelUserVO.getCommissionProfileSetID());
        p_channelTransferVO.setCommProfileVersion(p_channelUserVO.getCommissionProfileSetVersion());
        p_channelTransferVO.setDualCommissionType(p_channelUserVO.getDualCommissionType());
        p_channelTransferVO.setCreatedOn(p_curDate);
        p_channelTransferVO.setCreatedBy(p_userVO.getUserID());
        p_channelTransferVO.setModifiedOn(p_curDate);
        p_channelTransferVO.setModifiedBy(p_userVO.getUserID());
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
        p_channelTransferVO.setTransferInitatedBy(p_userVO.getUserID());
        p_channelTransferVO.setReceiverTxnProfile("");
        p_channelTransferVO.setSenderTxnProfile(p_channelUserVO.getTransferProfileID());
        p_channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_TYPE_WEB);
        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setFromUserCode(p_channelUserVO.getUserCode());
        p_channelTransferVO.setTransferCategory(PretupsI.TRANSFER_TYPE_SALE);
        String wallet = BTSLUtil.NullToString(Constants.getProperty("WALLET_TYPE"));
        if (BTSLUtil.isNullString(wallet))
            p_channelTransferVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
        else {
            wallet = (wallet.trim()).toUpperCase();
            if (PretupsI.SALE_WALLET_TYPE.equals(wallet))
                p_channelTransferVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
            else if (PretupsI.FOC_WALLET_TYPE.equals(wallet))
                p_channelTransferVO.setWalletType(PretupsI.FOC_WALLET_TYPE);
            else if (PretupsI.INCENTIVE_WALLET_TYPE.equals(wallet))
                p_channelTransferVO.setWalletType(PretupsI.INCENTIVE_WALLET_TYPE);
            else
                p_channelTransferVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
        }
        //p_channelTransferVO.setWalletType(Constants.getProperty("WALLET_TYPE"));
        String productType = null;
        long totRequestQty = 0, totMRP = 0, totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
        totRequestQty += PretupsBL.getSystemAmount(p_channelTransferItemsVO.getRequestedQuantity());
        totMRP += (Double.parseDouble(p_channelTransferItemsVO.getRequestedQuantity()) * p_channelTransferItemsVO.getUnitValue());
        totPayAmt += p_channelTransferItemsVO.getPayableAmount();
        totNetPayAmt += p_channelTransferItemsVO.getNetPayableAmount();
        totTax1 += p_channelTransferItemsVO.getTax1Value();
        totTax2 += p_channelTransferItemsVO.getTax2Value();
        totTax3 += p_channelTransferItemsVO.getTax3Value();
        productType = p_channelTransferItemsVO.getProductType();
        p_channelTransferVO.setRequestedQuantity(totRequestQty);
        p_channelTransferVO.setTransferMRP(totMRP);
        p_channelTransferVO.setPayableAmount(totPayAmt);
        p_channelTransferVO.setNetPayableAmount(totNetPayAmt);
        p_channelTransferVO.setTotalTax1(totTax1);
        p_channelTransferVO.setTotalTax2(totTax2);
        p_channelTransferVO.setTotalTax3(totTax3);
        p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
        p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
        p_channelTransferVO.setProductType(p_channelTransferItemsVO.getProductType());
        p_channelTransferVO.setProductType(productType);

        if (_log.isDebugEnabled())
            _log.debug("prepareChannelTransferVO", "Exiting .....  :p_channelTransferVO" + p_channelTransferVO);
        return p_channelTransferVO;

    }

    /**
     * This method performs all the transaction for the O2CWithdraw operation.
     * This method prepares the Network Stock, credit the network stock, updates
     * the channel User balance, updates the
     * transfer in values(it will not update the tranfer in count as it is
     * withdraw).
     * Also it updates the daily balance for the Channel User
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param p_channelTransferItemsVO
     * @param p_userID
     * @param p_date
     * @return ChannelTransferVO
     * @throws BTSLBaseException
     */

    private void transactionApproval(Connection p_con, ChannelTransferVO p_channelTransferVO, ChannelTransferItemsVO p_channelTransferItemsVO, String p_userID, Date p_date) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("transactionApproval", "Entering  : p_channelTransferVO " + p_channelTransferVO);

        int updateCount = -1;

        updateCount = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, p_userID, p_date, false);
        if (updateCount < 1) {
            throw new BTSLBaseException("O2CWithdrawUserDelProcess", "transactionApproval", O2CWithdrawUserDelProcess.ERROR_UPDATING_DATABASE);
        }// end of if
        updateCount = -1;
        // this method updates the network stock and also updates the network
        // transaction details
        updateCount = updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, p_userID, p_date);
        if (updateCount < 1) {
            throw new BTSLBaseException("O2CWithdrawUserDelProcess", "transactionApproval", O2CWithdrawUserDelProcess.ERROR_UPDATING_DATABASE);
        }// end of if
        UserBalancesVO userBalanceVO = null;
        // ChannelTransferItemsVO chnlTrfItemsVO = null;
        UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
        userBalanceVO = new UserBalancesVO();

        userBalanceVO.setUserID(p_channelTransferVO.getFromUserID());
        userBalanceVO.setProductCode(p_channelTransferItemsVO.getProductCode());
        userBalanceVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
        userBalanceVO.setNetworkFor(p_channelTransferVO.getNetworkCodeFor());
        userBalanceVO.setLastTransferID(p_channelTransferVO.getTransferID());
        userBalanceVO.setLastTransferType(p_channelTransferVO.getTransferType());
        userBalanceVO.setLastTransferOn(p_channelTransferVO.getTransferDate());
        userBalanceVO.setPreviousBalance(userBalanceVO.getBalance());
        userBalanceVO.setQuantityToBeUpdated(p_channelTransferItemsVO.getRequiredQuantity());
        // userBalanceVO.setUserMSISDN(p_channelTransferVO.getFromUserCode());

        updateCount = -1;
        // this method updates the user balances performing debit/credit on his
        // balance ar applicable
        updateCount = userBalancesDAO.updateUserDailyBalances(p_con, p_date, userBalanceVO);
        if (updateCount < 1) {
            throw new BTSLBaseException("O2CWithdrawUserDelProcess", "transactionApproval", O2CWithdrawUserDelProcess.ERROR_UPDATING_DATABASE);
        }// end of if
        ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        updateCount = -1;
        updateCount = channelUserDAO.debitUserBalances(p_con, p_channelTransferVO, false, null);
        if (updateCount < 1) {
            throw new BTSLBaseException("O2CWithdrawUserDelProcess", "transactionApproval", O2CWithdrawUserDelProcess.ERROR_UPDATING_DATABASE);
        }//
        updateCount = -1;
        // this call updates the counts/values for daily, weekly and monthly IN
        updateCount = ChannelTransferBL.updateOptToChannelUserInCounts(p_con, p_channelTransferVO, null, p_date);
        if (updateCount < 1) {
            throw new BTSLBaseException("O2CWithdrawUserDelProcess", "transactionApproval", O2CWithdrawUserDelProcess.ERROR_UPDATING_DATABASE);
        }// end of if

        if (_log.isDebugEnabled())
            _log.debug("transactionApproval", "Exiting...... : p_channelTransferVO " + p_channelTransferVO);
    }

    /**
     * This Method will be basically user by O2CwithdrawDelProces
     * Method for Deleting User Information from Users Table
     * (This is soft delete just update the status, set status =
     * N = delete.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param ChannelUserVO
     *            p_channelUserVO
     * @return deleteCount int
     * @throws BTSLBaseException
     */

    private int o2cWithdrawdeleteUser(Connection p_con, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        final String METHOD_NAME = "o2cWithdrawdeleteUser";
        if (_log.isDebugEnabled())
            _log.debug(METHOD_NAME, "Entered ");
        PreparedStatement pstmtDelete = null;
        int deleteCount = 0;
        try {
            boolean modified = false;
            modified = this.recordModified(p_con, p_channelUserVO.getUserID(), p_channelUserVO.getLastModified());

            // if modified = true means record modified by another user
            if (modified)
                throw new BTSLBaseException("error.modified");

            StringBuffer strBuffDel = new StringBuffer();
            strBuffDel.append("UPDATE users SET status = ?, previous_status = ? , modified_by = ?,");
            strBuffDel.append(" modified_on = ?,login_id=? WHERE user_id = ?");

            String deleteQuery = strBuffDel.toString();
            if (_log.isDebugEnabled())
                _log.debug("deleteSuspendUser", "QUERY deleteQuery:" + deleteQuery);

            pstmtDelete = p_con.prepareStatement(deleteQuery);
            if (PretupsI.USER_STATUS_DELETED.equals(p_channelUserVO.getStatus())) {
                pstmtDelete.setString(1, p_channelUserVO.getStatus());
                pstmtDelete.setString(2, p_channelUserVO.getPreviousStatus());
                pstmtDelete.setString(3, p_channelUserVO.getModifiedBy());
                pstmtDelete.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_channelUserVO.getModifiedOn()));
                pstmtDelete.setString(5, p_channelUserVO.getUserID());
                pstmtDelete.setString(6, p_channelUserVO.getUserID());
                deleteCount = pstmtDelete.executeUpdate();
                pstmtDelete.clearParameters();
            }
            // end if
            // check the status of the update
            if (deleteCount <= 0)
                throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } // end of try
        catch (BTSLBaseException be) {
            _log.error(METHOD_NAME, "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException: " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CWithdrawUserDelProcess[o2cWithdrawdeleteUser]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CWithdrawUserDelProcess[o2cWithdrawdeleteUser]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtDelete != null) {
                    pstmtDelete.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteSuspendUser", "Exiting: deleteCount=" + deleteCount);
        } // end of finally

        return deleteCount;
    }

    /**
     * This method is used to check whether the record in the database is
     * modified or not If there is any error then throws the SQLException
     * 
     * @param p_con
     *            Connection
     * @param p_userId
     *            String
     * @param p_oldLastModified
     *            long
     * @return boolean
     * @throws BTSLBaseException
     */
    private boolean recordModified(Connection p_con, String p_userId, long p_oldLastModified) throws BTSLBaseException {
        final String METHOD_NAME = "recordModified";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered: userId= " + p_userId + "oldLastModified= " + p_oldLastModified);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        String sqlRecordModified = "SELECT modified_on FROM users WHERE user_id = ?";
        Timestamp newLastModified = null;
        if (p_oldLastModified == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled())
                _log.debug(METHOD_NAME, "QUERY: sqlselect= " + sqlRecordModified);
            // create a prepared statement and execute it
            pstmt = p_con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, p_userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " old=" + p_oldLastModified);
                if (newLastModified != null) {
                    _log.debug(METHOD_NAME, " new=" + newLastModified.getTime());
                } else
                    _log.debug(METHOD_NAME, " new=null");
            }
            if (newLastModified != null && newLastModified.getTime() != p_oldLastModified) {
                modified = true;
            }
            return modified;
        } // end of try
        catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException: " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CWithdrawUserDelProcess[recordModified]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CWithdrawUserDelProcess[recordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } // end of catch

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exititng: modified=" + modified);
            }
        } // end of finally
    } // end recordModified

    /**
     * Method loadUserBalancesForMsisdn.
     * This method loads the balnaces of the user corresponding to the msisdn
     * entered
     * 
     * @param p_con
     *            Connection
     * @param p_userId
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserBalancesForUserId(Connection p_con, String p_userId) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserBalancesForUserId";
        if (_log.isDebugEnabled())
            _log.debug(METHOD_NAME, "Entered p_userId=" + p_userId);
        ArrayList userList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT ub.balance,p.product_short_code,p.product_name, ");
            selectQuery.append("p.product_code FROM user_balances ub,products p ");
            selectQuery.append("WHERE ub.product_code=p.product_code ");
            selectQuery.append("AND ub.user_id=?");
            if (_log.isDebugEnabled())
                _log.debug(METHOD_NAME, "Query=" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_userId);
            rs = pstmtSelect.executeQuery();
            UserBalancesVO balanceVO = null;
            while (rs.next()) {
                balanceVO = new UserBalancesVO();
                balanceVO.setBalance(rs.getLong("balance"));
                balanceVO.setProductShortCode(rs.getString("product_short_code"));
                balanceVO.setProductName(rs.getString("product_name"));
                balanceVO.setProductCode(rs.getString("product_code"));
                userList.add(balanceVO);
            }
        } catch (SQLException sqe) {
            _log.error(METHOD_NAME, "SQLException:" + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CWithdrawUserDelProcess[loadUserBalancesForUserId]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CWithdrawUserDelProcess[loadUserBalancesForUserId]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            if (_log.isDebugEnabled())
                _log.debug(METHOD_NAME, "Exiting:list size=" + userList.size());
        }
        return userList;
    }

    /**
     * Genrate the operator to channel withdraw ID
     * 
     * @param con
     * @param p_channelTransferVO
     * @throws BTSLBaseException
     */
    /*
     * public static void genrateWithdrawID(Connection con,ChannelTransferVO
     * p_channelTransferVO) throws BTSLBaseException
     * {
     * //for generating new withdraw id.
     * //returns new unique withdraw id
     * final String METHOD_NAME = "genrateWithdrawID";
     * if (_log.isDebugEnabled())
     * _log.debug(METHOD_NAME, "Entered ChannelTransferVO =" +
     * p_channelTransferVO);
     * 
     * try
     * {
     * long id=_idGeneratorDAO.getNextID(con,PretupsI.CHANNEL_WITHDRAW_O2C_ID,
     * BTSLUtil.getFinancialYear() , p_channelTransferVO);
     * p_channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(
     * p_channelTransferVO,PretupsI.CHANNEL_WITHDRAW_O2C_ID,id));
     * 
     * } catch (Exception e)
     * {
     * _log.error(METHOD_NAME, "Exception " + e.getMessage());
     * _log.errorTrace(METHOD_NAME, e);
     * //EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
     * EventStatusI
     * .RAISED,EventLevelI.FATAL,"ChannelTransferBL[genrateWithdrawID]"
     * ,"","","","Exception:"+e.getMessage());
     * throw new BTSLBaseException("O2CWithdrawUserDelProcess", METHOD_NAME,
     * PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
     * }finally
     * {
     * if (_log.isDebugEnabled())
     * _log.debug("genrateReturnID",
     * "Exited  ID ="+p_channelTransferVO.getTransferID());
     * }
     * }
     */
    /**
     * Method genrateStockTransctionID.
     * 
     * @param con
     *            Connection
     * @param p_networkStockTxnVO
     *            NetworkStockTxnVO
     * @return String
     * @throws BTSLBaseException
     */
    public static String genrateStockTransctionID(Connection con, NetworkStockTxnVO p_networkStockTxnVO) throws BTSLBaseException {
        final String METHOD_NAME = "genrateStockTransctionID";
        if (_log.isDebugEnabled())
            _log.debug(METHOD_NAME, "Entered ");
        String uniqueID = null;
        try {
            // long
            // id=_idGeneratorDAO.getNextID(con,PretupsI.NETWORK_STOCK_TRANSACTION_ID,
            // BTSLUtil.getFinancialYear(),p_networkStockTxnVO.getNetworkCode(),p_networkStockTxnVO.getCreatedOn());
            long id = _idGeneratorDAO.getNextID(con, PretupsI.NETWORK_STOCK_TRANSACTION_ID, BTSLUtil.getFinancialYear(), p_networkStockTxnVO);
            uniqueID = calculatorI.formatNetworkStockTxnID(p_networkStockTxnVO, id);
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"NetworkStockBL[genrateStockTransctionID]","","","","Exception:"+e.getMessage());
            throw new BTSLBaseException("O2CWithdrawUserDelProcess", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(METHOD_NAME, "Exited  " + uniqueID);
        }
        return uniqueID;
    }

    /**
     * update the Network Stock Transaction
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param p_userID
     * @param p_curDate
     * @return int
     * @throws BTSLBaseException
     */
    public static int updateNetworkStockTransactionDetails(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userID, Date p_curDate) throws BTSLBaseException {
        // basicaly updates the network stock
        if (_log.isDebugEnabled())
            _log.debug("updateNetworkStockTransactionDetails", "Entered ChannelTransferVO =" + p_channelTransferVO + " USERID " + p_userID + " Curdate " + p_curDate);
        int updateCount = 0;

        NetworkStockTxnVO networkStockTxnVO = new NetworkStockTxnVO();
        networkStockTxnVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
        networkStockTxnVO.setNetworkFor(p_channelTransferVO.getNetworkCodeFor());
        if (p_channelTransferVO.getNetworkCode().equals(p_channelTransferVO.getNetworkCodeFor()))
            networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
        else
            networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
        networkStockTxnVO.setReferenceNo(p_channelTransferVO.getReferenceNum());
        networkStockTxnVO.setTxnDate(p_channelTransferVO.getModifiedOn());
        networkStockTxnVO.setRequestedQuantity(p_channelTransferVO.getRequestedQuantity());
        networkStockTxnVO.setApprovedQuantity(p_channelTransferVO.getRequestedQuantity());
        networkStockTxnVO.setInitiaterRemarks(p_channelTransferVO.getChannelRemarks());
        networkStockTxnVO.setFirstApprovedRemarks(p_channelTransferVO.getFirstApprovalRemark());
        networkStockTxnVO.setSecondApprovedRemarks(p_channelTransferVO.getSecondApprovalRemark());
        networkStockTxnVO.setFirstApprovedBy(p_channelTransferVO.getFirstApprovedBy());
        networkStockTxnVO.setSecondApprovedBy(p_channelTransferVO.getSecondApprovedBy());
        networkStockTxnVO.setFirstApprovedOn(p_channelTransferVO.getFirstApprovedOn());
        networkStockTxnVO.setSecondApprovedOn(p_channelTransferVO.getSecondApprovedOn());
        networkStockTxnVO.setCancelledBy(p_channelTransferVO.getCanceledBy());
        networkStockTxnVO.setCancelledOn(p_channelTransferVO.getCanceledOn());
        networkStockTxnVO.setCreatedBy(p_userID);
        networkStockTxnVO.setCreatedOn(p_curDate);
        networkStockTxnVO.setModifiedOn(p_curDate);
        networkStockTxnVO.setModifiedBy(p_userID);

        networkStockTxnVO.setTxnStatus(p_channelTransferVO.getStatus());
        networkStockTxnVO.setTxnNo(genrateStockTransctionID(p_con, networkStockTxnVO));
        p_channelTransferVO.setReferenceID(networkStockTxnVO.getTxnNo());

        if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(p_channelTransferVO.getTransferType())) {
            networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_TRANSFER);
            networkStockTxnVO.setTxnType(PretupsI.DEBIT);
        } else if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(p_channelTransferVO.getTransferType())) {
            networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_RETURN);
            networkStockTxnVO.setTxnType(PretupsI.CREDIT);
        }

        networkStockTxnVO.setInitiatedBy(p_userID);
        networkStockTxnVO.setFirstApproverLimit(p_channelTransferVO.getFirstApproverLimit());
        networkStockTxnVO.setUserID(p_channelTransferVO.getFromUserID());
        networkStockTxnVO.setTxnMrp(p_channelTransferVO.getTransferMRP());

        ArrayList list = p_channelTransferVO.getChannelTransferitemsVOList();
        ChannelTransferItemsVO channelTransferItemsVO = null;
        NetworkStockTxnItemsVO networkItemsVO = null;

        ArrayList arrayList = new ArrayList();
        int j = 1;
        for (int i = 0, k = list.size(); i < k; i++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) list.get(i);

            networkItemsVO = new NetworkStockTxnItemsVO();
            networkItemsVO.setSNo(j++);
            networkItemsVO.setTxnNo(networkStockTxnVO.getTxnNo());
            networkItemsVO.setRequiredQuantity(channelTransferItemsVO.getRequiredQuantity());
            networkItemsVO.setApprovedQuantity(channelTransferItemsVO.getApprovedQuantity());
            networkItemsVO.setMrp(channelTransferItemsVO.getApprovedQuantity() * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue())));
            networkItemsVO.setAmount(channelTransferItemsVO.getPayableAmount());
            if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(p_channelTransferVO.getTransferType())) {
                networkItemsVO.setStock(channelTransferItemsVO.getAfterTransSenderPreviousStock());
            } else {
                networkItemsVO.setStock(channelTransferItemsVO.getAfterTransReceiverPreviousStock());
            }

            networkItemsVO.setProductCode(channelTransferItemsVO.getProductCode());
            networkItemsVO.setDateTime(p_curDate);
            arrayList.add(networkItemsVO);
        }
        networkStockTxnVO.setNetworkStockTxnItemsList(arrayList);

        NetworkStockDAO networkStockDAO = new NetworkStockDAO();
        // call the dao to update the newtorkstoock tarnsaction
        updateCount = networkStockDAO.addNetworkStockTransaction(p_con, networkStockTxnVO);

        if (_log.isDebugEnabled())
            _log.debug("updateNetworkStockTransactionDetails", "Exited  updateCount " + updateCount);

        return updateCount;
    }

}
