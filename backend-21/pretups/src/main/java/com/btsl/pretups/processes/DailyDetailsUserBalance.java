package com.btsl.pretups.processes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class DailyDetailsUserBalance {
    private static Log _logger = LogFactory.getLog(DailyDetailsUserBalance.class.getName());
    private static Locale _locale = null;
    private static String _fileLabelUBSCAT = null;
    private static String _fileLabelUBSUSER = null;
    private static boolean _isHeaderShow = true;
    private ProcessStatusVO processVO = null;

    /**
     * Method main
     * 
     * @param arg
     *            String[]
     *            arg[0] = Constants.props (With path)
     *            arg[1] = ProcessLogConfig.props (With path)
     *            arg[2] = Locale(0 or 1)
     * @author Ankit Malhotra
     */

    public static void main(String[] arg) {
        final String METHOD_NAME = "main";
        try {
            if (arg.length != 3)// check the argument length
            {
                _logger.info(METHOD_NAME, "DailyDetailsUserBalance :: Not sufficient arguments, please pass Constants.props ProcessLogconfig.props   Locale");
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists())// check file (Constants.props) exist or
            // not
            {
                _logger.debug(METHOD_NAME, "DailyDetailsUserBalance" + " Constants File Not Found at the path : " + arg[0]);
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists())// check file (ProcessLogConfig.props)
            // exist or not
            {
                _logger.debug(METHOD_NAME, "DailyDetailsUserBalance" + " ProcessLogConfig File Not Found at the path : " + arg[1]);
                return;
            }

            if (!BTSLUtil.isNumeric(arg[2]))// check the Locale is numeric
            {
                _logger.debug(METHOD_NAME, "DailyDetailsUserBalance :: Invalid Locale " + arg[2] + " It should be Numeric");
                return;
            }

            // use to load the Constants.props and ProcessLogConfig.props files
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end of try
        catch (Exception e) {
            _logger.error(METHOD_NAME, "Main: Error in loading the Cache information.." + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DailyDetailsUserBalance[main]", "", "", "",
                "  Error in loading the Cache information");
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            _locale = LocaleMasterCache.getLocaleFromCodeDetails(arg[2]);
            if (_locale == null) {
                _locale = LocaleMasterCache.getLocaleFromCodeDetails("0");
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "Error : Invalid Locale " + arg[2] + " ");
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "DailyDetailsUserBalance[main]", "", "", "",
                    "  Message:  Invalid Locale " + arg[2] + " ");
            }
        }// end of try
        catch (Exception e) {
            _logger.error(METHOD_NAME, " Invalid locale : " + arg[2] + " Exception:" + e.getMessage());
            _locale = new Locale("en", "US");
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "DailyDetailsUserBalance[main]", "", "", "",
                "  Message:  Not able to get the locale");
        }// end of catch
        try {
            _fileLabelUBSUSER = BTSLUtil.getMessage(_locale, "UBS_RPT_REPORT_LABEL_USER", null);
        } catch (Exception ee) {
            _logger.error(METHOD_NAME, "Missing/Wrong entry from Constants.props  " + ee.getMessage());
            _logger.errorTrace(METHOD_NAME, ee);
            ConfigServlet.destroyProcessCache();
            return;
        }

        try {
            final DailyDetailsUserBalance ddub = new DailyDetailsUserBalance();
            ddub.processUBS();
        }// end of try
        catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException :" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            // event handle
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyDetailsUserBalance[main]", "", "", "",
                "BTSLBaseException:" + be.getMessage());
        }// end of catch
        catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception :" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            // event handle
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyDetailsUserBalance[main]", "", "", "",
                "Exception:" + e.getMessage());
        }// end of catch
        finally {
            ConfigServlet.destroyProcessCache();
        }// end of finally
    }// end of main

    /**
     * Method process
     * only one process execute i.e ProcessUBS
     * 
     * @throws BTSLBaseException
     * @author Ankit Malhotra
     */
    private void processUBS() throws BTSLBaseException {
        final String METHOD_NAME = "processUBS";
        Date processedUpto = null;
        Date dateCount = null;
        Date currentDate = null;
        int beforeInterval = 0;
        Connection con = null;
        Date executedTill = null;
        boolean statusOk = false;

        try {
            final ProcessBL processBL = new ProcessBL();// This class is used to
            // check
            // the status of the process
            final ProcessStatusDAO processDAO = new ProcessStatusDAO(); // This
            // class
            // used to
            // implement
            // the process
            // related
            // business
            // logics.
            currentDate = new Date();
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            con = OracleUtil.getSingleConnection();// This method returns
            // connection from the
            // connection pool, if there
            // is no connection in the
            // pool then adds new
            // connection in the
            // connection pool.
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("processUBS", " DATABASE Connection is NULL ");
                }
                throw new BTSLBaseException(this, "processUBS", "Not able to get the connection");
            }
            try {
                processVO = processBL.checkProcessUnderProcess(con, ProcessI.UBS);// This
                // method
                // is
                // used
                // to
                // check
                // the
                // process
                // status
                statusOk = processVO.isStatusOkBool();// this variable stores
                // the value of processVO
                // as true or false.
                if (statusOk) {
                    con.commit();
                    beforeInterval = BTSLUtil.parseLongToInt( processVO.getBeforeInterval() / (60 * 24) );// converted
                    // the
                    // before
                    // Interval
                    // in
                    // days.
                    processedUpto = processVO.getExecutedUpto();
                    if (processedUpto != null) {
                        // to check whether process has been executed till
                        // current date or not
                        if (processedUpto.compareTo(currentDate) == 0) {
                            throw new BTSLBaseException("DailyDetailsUserBalance", "processUBS", PretupsErrorCodesI.UBS_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
                        }
                        // adding 1 in processed upto date as we have to start
                        // from the next day till which process has been
                        // executed
                        processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                        executedTill = BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval);// The
                        // date
                        // variable
                        // stores
                        // the
                        // total
                        // no
                        // of
                        // days
                        // after
                        // adding
                        // the
                        // no.
                        // of
                        // days
                        // in
                        // passed
                        // date.
                        // loop to be started for each date
                        // the loop starts from the date till which process has
                        // been executed and executes one day before current
                        // date
                        for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto); dateCount.before(executedTill); dateCount = BTSLUtil.addDaysInUtilDate(dateCount, 1)) {
                            this.fetchUBSData(con, dateCount);// to fetch the
                            // UserBalance
                            // Summary Data
                            processVO.setExecutedUpto(dateCount);
                            if (processDAO.updateProcessDetail(con, processVO) > 0) {
                                con.commit();
                            }
                        }// end of for loop
                    } else {
                        throw new BTSLBaseException("UBSFileCreation", "process", PretupsErrorCodesI.UBS_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
                    }
                } else {
                    throw new BTSLBaseException("DailyDetailsUserBalance", "process", "Process is already running..");
                }
            }// end of try

            catch (BTSLBaseException e1) {
                _logger.error("processUBS", "BTSLBaseException  " + e1.getMessage());
                _logger.errorTrace(METHOD_NAME, e1);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyDetailsUserBalance", "", "", "",
                    "Exception for Process ID=" + e1.getMessage());
            }// end of catch
            catch (Exception e2) {
                _logger.error("processUBS", "Exception : UBS : " + e2.getMessage());
                _logger.errorTrace(METHOD_NAME, e2);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyDetailsUserBalance", "", "", "",
                    "Exception:" + e2.getMessage());
            }// end of catch
            finally {
                if (statusOk) {
                    processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    try {
                        if (processDAO.updateProcessDetail(con, processVO) > 0) {
                            con.commit();
                        } else {
                            con.rollback();
                        }
                    } catch (Exception e) {
                        _logger.error("processUBS", " Exception in update process detail" + e.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyDetailsUserBalance[processUBS] ", "",
                            "", "", "Exception in update process detail for Process ID=" + ProcessI.UBS + " :" + e.getMessage());
                        _logger.errorTrace(METHOD_NAME, e);
                    }
                }
            }// end of finally
        }// end of try
        catch (BTSLBaseException be) {
            _logger.error("processUBS", "BTSLBaseException : " + be.getMessage());
            throw be;
        } catch (Exception be) {
            _logger.error("processUBS", "Exception : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw new BTSLBaseException(this, "processUBS", "Exception=" + be.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("processUBS", "Exiting..... ");
            }
        }

    }// end of ProcessUBS Method

    /**
     * method fetchUBSData
     * This will fetch all the required USER BALANCE SUMMARY transactions data
     * from database
     * 
     * @param p_con
     *            Connection
     * @param dateCount
     *            Date
     * @throws BTSLBaseException
     * @author Ankit Malhotra
     */
    private void fetchUBSData(Connection p_con, Date dateCount) throws BTSLBaseException {
        final String METHOD_NAME = "fetchUBSData";
        if (_logger.isDebugEnabled()) {
            _logger.debug("fetchUBSData", " Entered: dateCount=" + dateCount);
        }

        String BtscQryKey = null;
        String BtsuQryKey = null;
        PreparedStatement pstmtCategorySelect = null;
        PreparedStatement pstmtUserSelect = null;
        ResultSet rsCategory = null;
        ResultSet rsUser = null;
        int i;
        int j;
        try {
        	DailyDetailsUserBalanceQry dailyDetailsUserBalanceQry = (DailyDetailsUserBalanceQry) ObjectProducer.getObject(QueryConstants.DAILY_DETAILS_USER_BALANCE_QRY, QueryConstants.QUERY_PRODUCER);
            BtscQryKey = dailyDetailsUserBalanceQry.fetchUBSDataQry();
            BtsuQryKey = dailyDetailsUserBalanceQry.fetchUBSDataSelectUserBalQry();
            pstmtCategorySelect = p_con.prepareStatement(BtscQryKey);
            i = 1;
            pstmtCategorySelect.clearParameters();
            pstmtCategorySelect.setString(i++, PretupsI.OPERATOR_TYPE_OPT);
            pstmtCategorySelect.setString(i++, PretupsI.STAFF_USER_TYPE);
            pstmtCategorySelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(dateCount));
            pstmtCategorySelect.setString(i++, PreferenceI.AMOUNT_MULT_FACTOR);
            pstmtCategorySelect.setString(i++, PretupsI.USER_STATUS_NEW);
            pstmtCategorySelect.setString(i++, PretupsI.USER_STATUS_CANCELED);
            rsCategory = pstmtCategorySelect.executeQuery();
            pstmtUserSelect = p_con.prepareStatement(BtsuQryKey);
            j = 1;
            pstmtUserSelect.clearParameters();
            pstmtUserSelect.setString(j++, PretupsI.OPERATOR_TYPE_OPT);
            pstmtUserSelect.setString(j++, PretupsI.STAFF_USER_TYPE);
            pstmtUserSelect.setDate(j++, BTSLUtil.getSQLDateFromUtilDate(dateCount));
            pstmtUserSelect.setString(j++, PreferenceI.AMOUNT_MULT_FACTOR);
            rsUser = pstmtUserSelect.executeQuery();
            if (rsCategory != null && rsUser != null) {
                writeUBSDataInFile(rsCategory, rsUser, dateCount);
            }
        }// end of try
        catch (SQLException sqe) {
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyDetailsUserBalance[fetchUBSData]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("DailyDetailsUserBalance", "fetchUBSData", "SQLException :" + sqe.getMessage());
        }// end of catch
        finally {
            if (rsUser != null) {
                try {
                    rsUser.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (rsCategory != null) {
                try {
                    rsCategory.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (pstmtUserSelect != null) {
                try {
                    pstmtUserSelect.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (pstmtCategorySelect != null) {
                try {
                    pstmtCategorySelect.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("fetchUBSData", "Exiting ");
            }
        }// end of finally
    }// end of Fetch UBS Method

    /**
     * Method writeUBSDataInFile
     * This method write UBS data in csv files
     * 
     * @param p_rsc
     *            ResultSet
     * @param p_rsu
     *            ResultSet
     * @param date
     *            Date
     * @throws BTSLBaseException
     * @author Ankit Malhotra
     */
    private void writeUBSDataInFile(ResultSet p_rsc, ResultSet p_rsu, Date date) throws BTSLBaseException {
        final String METHOD_NAME = "writeUBSDataInFile";
        if (_logger.isDebugEnabled()) {
            _logger.debug("writeUBSDataInFile", " Entered:  p_rsc=" + p_rsc + " p_rsu: " + p_rsu + " date : " + date);
        }
        PrintWriter out = null;
        File newFile = null;
        String fileHeader = null;
        String networkCode = null;
        String prevnetworkCode = null;
        String networkName = null;
        String prevnetworkName = null;
        String categoryName = null;
        String categoryCode = null;
        int totalPersons = 0;
        int totalBalance = 0;
        String categoryCodeUser = null;
        String closingBal = null;

        String finalFileName = null;
        String filePath = null;
        String fileName = null;
        int status = 1;
        int temp = 0;
        try {
            fileName = Constants.getProperty("DAILY_DETAILS_USER_BALANCE_FILE_NAME");

        }// end of try
        catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("fetchUBSData", "Exception" + e.getMessage());
            throw new BTSLBaseException("DailyDetailsUserBalance", "fetchUBSData", "Directory not created or file name not found please check Constants.props");
        }// end of try

        try {
            File fileDir = null;
            while (p_rsc.next())// Loop for navigating through categorywise
            // resultset
            {
                networkCode = p_rsc.getString(1);
                networkName = p_rsc.getString(2);
                categoryName = p_rsc.getString(3);
                categoryCode = p_rsc.getString(4);
                totalPersons = p_rsc.getInt(5);
                totalBalance = p_rsc.getInt(6);
                // flag=true;
                // To create folder besed on network
                filePath = Constants.getProperty("CREATE_DAILY_DETAILS_USER_BALANCE_UBSREPORT") + File.separator + networkCode + File.separator;
                fileDir = new File(filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();
                }

                if (temp != 0)// This condition is added for generation of
                // separate files for separate networks.
                {
                    if (!prevnetworkCode.equalsIgnoreCase(networkCode)) {
                        finalFileName = filePath + fileName + "_" + networkCode + "_" + (BTSLUtil.getFileNameStringFromDate(date)).substring(0, 6) + "_" + "CLOSINGBALDETAILS" + ".csv";
                        out.close();
                        newFile = new File(finalFileName);
                        _isHeaderShow = true;
                        out = new PrintWriter(new BufferedWriter(new FileWriter(newFile, true)));
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("writeUBSDataInFile", "  fileName=" + finalFileName);
                        }
                        if (_isHeaderShow) {
                            fileHeader = constructFileHeader(networkName, date);
                            out.write(fileHeader + "\n");
                            _isHeaderShow = false;
                        }
                    }

                    final String arr[] = new String[3];
                    arr[0] = categoryName;
                    arr[1] = String.valueOf(totalBalance);
                    arr[2] = String.valueOf(totalPersons);
                    _fileLabelUBSCAT = BTSLUtil.getMessage(_locale, "UBS_RPT_REPORT_LABEL_CAT", arr);
                    out.write(_fileLabelUBSCAT + "\n");// Writes the value
                    // category name,total
                    // balance and total no.
                    // of persons in the file
                    out.flush();
                    out.write(_fileLabelUBSUSER + "\n");
                    out.flush();
                    if (status != 0) {
                        while (p_rsu.next())// Loop for navigating through the
                        // UserWise resultset
                        {
                            categoryCodeUser = p_rsu.getString(1);// Retrieves
                            // the value
                            // of the
                            // designated
                            // column in
                            // the current
                            // row of this
                            // ResultSet
                            // object as a
                            // String
                            if (categoryCode.equals(categoryCodeUser))// compares
                            // the
                            // category
                            // code
                            // retreived
                            // from
                            // the
                            // categorywise
                            // resultset
                            // with
                            // the
                            // category
                            // code of
                            // user
                            {
                                closingBal = p_rsu.getString(2);
                                out.write(closingBal + "\n");// Write a string.
                                out.flush();// Flush the stream.

                                status = 1;
                            } else {
                                status = 0;
                                break;
                            }
                        }// end of while loop(p_rsu.next)
                    }// end of if
                    else {
                        closingBal = p_rsu.getString(2);
                        out.write(closingBal + "\n");
                        out.flush();

                        status = 1;
                        while (p_rsu.next())// Moves the cursor down one row
                        // from its current position.
                        {
                            categoryCodeUser = p_rsu.getString(1);// Retrieves
                            // the value
                            // of the
                            // designated
                            // column in
                            // the current
                            // row of this
                            // ResultSet
                            // object as a
                            // String
                            if (categoryCode.equals(categoryCodeUser)) {
                                closingBal = p_rsu.getString(2);
                                out.write(closingBal + "\n");
                                out.flush();

                                status = 1;
                            } else {
                                status = 0;
                                break;
                            }
                        }// end of while loop(p_rsu.next)
                    }
                }// end of if (temp!=0)
                else// else of temp!=0
                {
                    prevnetworkName = networkName;
                    prevnetworkCode = networkCode;
                    temp = 1;

                    finalFileName = filePath + fileName + "_" + networkCode + "_" + (BTSLUtil.getFileNameStringFromDate(date)).substring(0, 6) + "_" + "CLOSINGBALDETAILS" + ".csv";
                    newFile = new File(finalFileName);
                    out = new PrintWriter(new BufferedWriter(new FileWriter(newFile, true)));
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("writeUBSDataInFile", "  fileName=" + finalFileName);
                    }
                    _isHeaderShow = true;
                    if (_isHeaderShow)// This condition is used for displaying
                    // the Header in the file.
                    {
                        fileHeader = constructFileHeader(prevnetworkName, date);
                        out.write(fileHeader + "\n");
                        _isHeaderShow = false;
                    }
                    final String arr[] = new String[3];
                    arr[0] = categoryName;
                    arr[1] = String.valueOf(totalBalance);
                    arr[2] = String.valueOf(totalPersons);

                    _fileLabelUBSCAT = BTSLUtil.getMessage(_locale, "UBS_RPT_REPORT_LABEL_CAT", arr);
                    out.write(_fileLabelUBSCAT + "\n");// Writes the value
                    // category name,total
                    // balance and total no.
                    // of persons in the file
                    out.flush();
                    out.write(_fileLabelUBSUSER + "\n");
                    out.flush();
                    if (status != 0)// This condition is used so that the record
                    // belonging to a different category is not
                    // skipped.
                    {
                        while (p_rsu.next())// Loop for navigating through the
                        // UserWise resultset
                        {
                            categoryCodeUser = p_rsu.getString(1);// Retrieves
                            // the value
                            // of the
                            // designated
                            // column in
                            // the current
                            // row of this
                            // ResultSet
                            // object as a
                            // String
                            if (categoryCode.equals(categoryCodeUser))// compares
                            // the
                            // category
                            // code
                            // retreived
                            // from
                            // the
                            // categorywise
                            // resultset
                            // with
                            // the
                            // category
                            // code of
                            // user
                            {
                                closingBal = p_rsu.getString(2);
                                out.write(closingBal + "\n");// Write a string.
                                out.flush();// Flush the stream.

                                status = 1;
                            } else {
                                status = 0;
                                break;
                            }
                        }// end of while loop(p_rsu.next)
                    }// end of if
                    else {
                        closingBal = p_rsu.getString(2);
                        out.write(closingBal + "\n");
                        out.flush();

                        status = 1;
                        while (p_rsu.next())// Moves the cursor down one row
                        // from its current position.
                        {
                            categoryCodeUser = p_rsu.getString(1);// Retrieves
                            // the value
                            // of the
                            // designated
                            // column in
                            // the current
                            // row of this
                            // ResultSet
                            // object as a
                            // String
                            if (categoryCode.equals(categoryCodeUser)) {
                                closingBal = p_rsu.getString(2);
                                out.write(closingBal + "\n");
                                out.flush();

                                status = 1;
                            } else {
                                status = 0;
                                break;
                            }
                        }// end of while loop(p_rsu.next)
                    }
                }// end of else(temp!=0)
            }// end of while loop(p_rsc.next)

        }// end of try
        catch (BTSLBaseException e) {
            _logger.debug("writeUBSDataInFile", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                	_logger.debug(METHOD_NAME, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyDetailsUserBalance[writeUBSDataInFile]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("DailyDetailsUserBalance", "writeUBSDataInFile", "Exception-The data cannot be written into the files  " + e.getMessage());
        }// end of catch
        catch (SQLException e) {
            _logger.debug("writeUBSDataInFile", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                	_logger.debug(METHOD_NAME, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyDetailsUserBalance[writeUBSDataInFile]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("DailyDetailsUserBalance", "writeUBSDataInFile", "Exception-The data cannot be written into the files  " + e.getMessage());
        } catch (IOException e) {

            _logger.debug("writeUBSDataInFile", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                	_logger.debug(METHOD_NAME, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyDetailsUserBalance[writeUBSDataInFile]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("DailyDetailsUserBalance", "writeUBSDataInFile", "Exception-The data cannot be written into the files  " + e.getMessage());
        } catch (ParseException e) {
            _logger.debug("writeUBSDataInFile", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                	_logger.debug(METHOD_NAME, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyDetailsUserBalance[writeUBSDataInFile]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("DailyDetailsUserBalance", "writeUBSDataInFile", "Exception-The data cannot be written into the files  " + e.getMessage());
        } catch (Exception e) {
            _logger.debug("writeUBSDataInFile", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                	_logger.debug(METHOD_NAME, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyDetailsUserBalance[writeUBSDataInFile]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("DailyDetailsUserBalance", "writeUBSDataInFile", "Exception-The data cannot be written into the files  " + e.getMessage());
        }

        finally {
            if (out != null) {
                out.close();
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeUBSDataInFile", "Exiting ");
            }
        }
    }// end of writeUBS method

    /**
     * This method is used to constuct file header
     * 
     * @param date
     *            Date
     * @param networkName
     *            String
     * @return String fileHeaderBuf
     * @throws BTSLBaseException
     * @author Ankit Malhotra
     */
    private String constructFileHeader(String networkName, Date date) throws BTSLBaseException {
        final String METHOD_NAME = "constructFileHeader";
        if (_logger.isDebugEnabled()) {
            _logger.debug("constructFileHeader", " Entered: networkName :" + networkName + "  date :" + date);
        }
        StringBuffer fileHeaderBuf = null;
        try {
            fileHeaderBuf = new StringBuffer("Network Name=" + networkName);
            fileHeaderBuf.append("\n" + "Date=" + BTSLUtil.getSQLDateFromUtilDate(date));
            fileHeaderBuf.append("\n");
        }// end of try
        catch (Exception e) {
            _logger.error("constructFileHeader", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("DailyDetailsUserBalance", "constructFileHeader", "Exception: " + e.getMessage());
        }// end of catch
        if (_logger.isDebugEnabled()) {
            _logger.debug("constructFileHeader", "Exiting: fileHeaderBuf.toString()=" + fileHeaderBuf.toString());
        }
        return fileHeaderBuf.toString();
    }// end of method constructFileHeader
}
