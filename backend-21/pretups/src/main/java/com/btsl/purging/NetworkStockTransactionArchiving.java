/*
 * Created on Dec 22, 2005
 */
package com.btsl.purging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.DatabasePurgingLog;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

/**
 * @author ankit.singhal
 */
public class NetworkStockTransactionArchiving {
    private static long _totalRecordsWrittenInFile = 0;
    private static String _networkStockTransactionTableName1 = PretupsI.NET_STK_TRA;
    private static String _networkStockTransactionTableName2 = PretupsI.NET_STK_TRA_ITEMS;
    private static String _networkStockTransactionFileName = PretupsI.NET_STK_TRA_ITEMS;
    public static SimpleDateFormat simpleDateFormat;
    private static String _month = null;
    private static String _year = null;
    private static int _monthInt = 0;
    private static int _yearInt = 0;
    private static Date _userToDate = null;
    private static Date _userFromDate = null;
    private static Log _logger = LogFactory.getLog(NetworkStockTransactionArchiving.class.getName());
    private static String _userEnterFromDayOnly = null;
    private static String _userEnterToDayOnly = null;
    private static String _masterDirectory = null;
    private static String _LINE = "------------------------------------------------------------------------------------";
    private static String _separator = "_";
    private static String _ENDOFFILE = "EOF";
    private static int _FILEFOOTERLENGTH = 50;
    private static String _FILEFOOTER = "ENDOFFILE=";

    /**
	 * to ensure no class instantiation 
	 */
    private NetworkStockTransactionArchiving() {
        
    }
    public static void main(String arg[]) {
        final String methodName = "main";
        String userNetworkCode = null;
        String networkStockTransactionLabel = null;
        String fileEXT = null;
        String directoryPath = null;
        long fileLength = 0;
        Date startDate = new Date();

        try {
            File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                _logger.info(methodName, " Constants File Not Found .............");
                return;
            }
            File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                _logger.info(methodName, " Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

            // for taking user input
            String args[] = new String[10];

            // Flag if the process is allowed at a particular time
            boolean isOperationAllowed = false;
            String fromTime = null;
            String toTime = null;

            try {
                fromTime = Constants.getProperty("ARCHIVE_FTIME");
            } catch (Exception e) {
                fromTime = "0000";
                _logger.errorTrace(methodName, e);
            }
            try {
                toTime = Constants.getProperty("ARCHIVE_TTIME");
            } catch (Exception e) {
                toTime = "0300";
                _logger.errorTrace(methodName, e);
            }
            try {
                simpleDateFormat = new SimpleDateFormat(Constants.getProperty("SIMPLE_DATE_FORMAT"));
            } catch (Exception e) {
                simpleDateFormat = new SimpleDateFormat(PretupsI.DATE_FORMAT);
                _logger.errorTrace(methodName, e);
            }

            isOperationAllowed = ArchivePretupsUtil.archivalTime(fromTime, toTime);
            if (!isOperationAllowed) {
                if (_logger.isDebugEnabled()) {
                    _logger.info(methodName, " This Process should be executed between " + fromTime + " and " + toTime);
                }
                return;
            }

            args = ArchivePretupsUtil.intractiveParameterCheck();

            if (!(args != null)) {
                return;
            }
            args[7] = "Archive";
            // displays the data for which processing will be done
            ArchivePretupsUtil.displayedUserInfo(args);
            userNetworkCode = BTSLUtil.NullToString(args[2]);
            _month = BTSLUtil.NullToString(args[3]);
            _year = BTSLUtil.NullToString(args[4]);
            _monthInt = Integer.parseInt(_month);
            String year = null;
            year = "" + _year.substring(_year.length() - 2);
            _yearInt = Integer.parseInt(year);
            _userFromDate = BTSLUtil.getDateFromDateString(args[5]);
            _userToDate = BTSLUtil.getDateFromDateString(args[6]);
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, " Error in Loading Files ...........................: " + e.getMessage());
            }
            _logger.errorTrace(methodName, e);
            return;
        }
        try {
            networkStockTransactionLabel = Constants.getProperty("NET_STK_TRA_LABEL");
            directoryPath = Constants.getProperty("DIR_PATH");
            if (BTSLUtil.isNullString(directoryPath)) {
            	_logger.error(methodName, "Directory Path is Missing ");
            }
            try {
                fileEXT = Constants.getProperty("FILEEXT");
            } catch (Exception e) {
                fileEXT = ".csv";
                _logger.errorTrace(methodName, e);
            }
            try {
                fileLength = Long.parseLong(Constants.getProperty("MAX_FILELENGTH"));
            } catch (Exception e) {
                fileLength = 10000;
                _logger.errorTrace(methodName, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, " File Length Successfully Uploaded");
            }
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, " ERROR in uploading File Length (loaded default)");
            }
            _logger.errorTrace(methodName, e);
            return;
        }
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.info(methodName, " DATABASE Connection NULL ");
                }
                return;
            }
            _userEnterFromDayOnly = BTSLUtil.getDateStringFromDate(_userFromDate).substring(0, 2);
            _userEnterToDayOnly = BTSLUtil.getDateStringFromDate(_userToDate).substring(0, 2);
            _masterDirectory = ArchivePretupsUtil.createMasterDirectory(directoryPath, _year, _month, _userEnterFromDayOnly, _userEnterToDayOnly);
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, " Creating Master Directory = " + _masterDirectory);
            }
            // code block to check whether files exist in the directory or not
            boolean isFile = true;
            String data = null;
            while (isFile) {
                isFile = ArchivePretupsUtil.isFileExists(ArchivePretupsUtil.partialFileName(_year, _month, "_"), _masterDirectory);
                if (isFile) {
                    _logger.info(methodName, "As archiving has been done previously. Please Move the files to the Separate folder and Press Y to continue");
                    data = ArchivePretupsUtil.getUserInputFromConsole();
                    if (BTSLUtil.NullToString(data).trim().indexOf("Y") != -1 || BTSLUtil.NullToString(data).trim().indexOf("y") != -1) {
                        continue;
                    }
                    _logger.debug(methodName, "Returning from Here ............. User Entered = " + data + " (operation aborted by user)");
                    return;
                } else {
                    break;
                }
            }// end while
             // end of file checking block
            ArrayList networkList = ArchivePretupsUtil.getNetworks(con, PretupsI.CIRCLE_NETWORK_TYPE);
            if (networkList == null || networkList.isEmpty()) {
                if (_logger.isDebugEnabled()) {
                    _logger.info(methodName, " Network List NULL, Control Returning ");
                }
                return;
            }
            String locCodeName = null;
            String networkCode = null;
            String networkName = null;
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, " userNetworkCode: " + userNetworkCode);
            }
            int networkListSize =networkList.size();
            for (int i = 0; i < networkListSize; i++) {
                locCodeName = (String) networkList.get(i);
                networkCode = locCodeName.substring(0, locCodeName.indexOf("#"));
                networkName = locCodeName.substring(locCodeName.indexOf("#") + 1);
                if (_logger.isDebugEnabled()) {
                    _logger.info(methodName, " Network Name = " + networkName + "  Network Code = " + networkCode);
                }

                processNetworkStockTxn(con, directoryPath, _masterDirectory, _networkStockTransactionFileName, fileEXT, networkStockTransactionLabel, fileLength, _networkStockTransactionTableName1, _networkStockTransactionTableName2, networkCode, networkName, _userFromDate, _userToDate);
                
                if (_logger.isDebugEnabled()) {
                    _logger.info(methodName, _LINE);
                }
            }
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, " " + e);
            }
            _logger.errorTrace(methodName, e);
            return;
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e1) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, " SQLEXCEPTION " + e1);
                }
                _logger.errorTrace(methodName, e1);
            }
            Date endDate = new Date();
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, "Total Time Taken = " + ((endDate.getTime() - startDate.getTime()) / (60 * 1000)) + " Min");
            }
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, "Total Number of Records Written in Files = " + _totalRecordsWrittenInFile);
            }
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, "Exiting Main Method ..................");
            }
        }
    }// end main

    /**
     * @param p_fileLength
     * @return
     */
    public static long loadFileLength(String p_fileLength) {
        final String methodName = "loadFileLength";
        long fileLength = 0;
        try {
            fileLength = Long.parseLong(p_fileLength);
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadFileLength", "Parameter " + p_fileLength + " not found or wrong parameter");
            }
            fileLength = 10000;
            _logger.errorTrace(methodName, e);
        }
        return fileLength;
    }

    /**
     * This method is used for netwrk stock transaction items transction
     * 
     * @param p_con
     *            Connection
     * @param p_directoyPath
     *            String
     * @param p_directoryName
     *            String
     * @param p_fileName
     *            String
     * @param p_fileExt
     *            String
     * @param p_rechargeMonth
     *            int
     * @param p_label
     *            String
     * @param p_maxFileLength
     *            long
     * @param p_tableName
     * @param p_tableName1
     * @param p_networkCode
     * @param p_networkName
     */
    public static void processNetworkStockTxn(Connection p_con, String p_directoyPath, String p_directoryName, String p_fileName, String p_fileExt, String p_label, long p_maxFileLength, String p_tableName1, String p_tableName2, String p_networkCode, String p_networkName, Date p_fromDate, Date p_toDate) {
        final String methodName = "processNetworkStockTxn";
        if (_logger.isDebugEnabled()) {
            _logger.info(methodName, " Entered:  " + p_directoryName + " " + p_fileName + " " + p_fileExt + " " + p_maxFileLength + " " + p_tableName1 + " " + p_tableName2 + " " + p_networkCode + " " + p_networkName);
        }
        StringBuffer queryBuf = new StringBuffer("SELECT nst.txn_no||','||nst.network_code||','||nst.network_code_for||','||nst.stock_type");
        queryBuf.append("||','||nst.reference_no||','||nst.txn_date||','||nst.requested_quantity||','||nst.approved_quantity");
        queryBuf.append("||','||nst.initiater_remarks||','||nst.first_approved_remarks||','||nst.second_approved_remarks");
        queryBuf.append("||','||nst.first_approved_by||','||nst.second_approved_by||','||nst.first_approved_on");
        queryBuf.append("||','||nst.second_approved_on||','||nst.cancelled_by||','||nst.cancelled_on||','||nst.created_by");
        queryBuf.append("||','||nst.created_on||','||nst.modified_on||','||nst.modified_by||','||nst.txn_status");
        queryBuf.append("||','||nst.entry_type||','||nst.txn_type||','||nst.initiated_by||','||nst.first_approver_limit");
        queryBuf.append("||','||nst.user_id||','||nst.txn_mrp||','||nsti.s_no||','||nsti.txn_no||','||nsti.product_code");
        queryBuf.append("||','||nsti.required_quantity||','||nsti.approved_quantity||','||nsti.stock||','||nsti.mrp");
        queryBuf.append("||','||nsti.amount FROM NETWORK_STOCK_TRANSACTIONS nst,NETWORK_STOCK_TRANS_ITEMS nsti ");
        queryBuf.append("WHERE nst.txn_no=nsti.txn_no AND nst.txn_date>=? AND nst.txn_date<=? AND nst.network_code=?");
        String query = queryBuf.toString();
        PreparedStatement pstmt = null;
        ResultSet rst = null;
        try {
            Date fromDate = null;
            Date toDate = null;
            ArrayList dateLst = new ArrayList();
            int status = 0;
            status = processArchivalDatesLatest(p_con, p_tableName1, p_tableName2, new Date(), p_fromDate, p_toDate, dateLst, p_networkCode);
            if (dateLst.size() > 0) {
                fromDate = (Date) dateLst.get(0);
                toDate = (Date) dateLst.get(1);
            }
            if (status == 0 && fromDate == null && toDate == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.info(methodName, "No Data found for network stock transaction for Processing ( Or Archival Date > To Date ). Returning From Here. ");
                }
                p_con.rollback();
                return;
            } else if (status == 0) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, " Error in Updating Records(network stock transaction) . Returning From Here. ");
                }
                p_con.rollback();
                return;
            }
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, " " + p_networkName + "   " + p_networkCode + " " + " From Date = " + simpleDateFormat.format(fromDate) + " To Date = " + simpleDateFormat.format(toDate));
            }
            pstmt = p_con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            pstmt.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(fromDate));
            pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(toDate));
            pstmt.setString(3, p_networkCode);
            rst = pstmt.executeQuery();
            writeInFile(p_directoyPath, p_directoryName, p_fileName, p_fileExt, rst, p_label, p_maxFileLength, false, toDate, fromDate, p_tableName1, p_networkCode, p_networkName);
        } catch (SQLException e1) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "SQLException " + e1.getMessage());
            }
            if (p_con != null) {
                try {
                    p_con.rollback();
                } catch (SQLException e2) {
                    _logger.errorTrace(methodName, e2);
                }
            }
            _logger.errorTrace(methodName, e1);
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exception " + e.getMessage());
            }
            if (p_con != null) {
                try {
                    p_con.rollback();
                } catch (SQLException e2) {
                    _logger.errorTrace(methodName, e2);
                }
            }
            _logger.errorTrace(methodName, e);
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(methodName, e2);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(methodName, e3);
                }
            }
            if (p_con != null) {
                try {
                    p_con.commit();
                } catch (SQLException e2) {
                    _logger.errorTrace(methodName, e2);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, "exiting :......................");
            }
        }
    }// end function

    /**
     * @param p_con
     *            Connection
     * @param p_tableName1
     *            String
     * @param p_tableName2
     *            String
     * @param presentDate
     *            Date
     * @param months
     *            Int
     * @param isOneMonth
     *            Boolean
     * @param toStoreDateLst
     *            Arraylist
     * @param networkCode
     *            String
     * @return
     * @throws Exception
     */
    public static int processArchivalDatesLatest(Connection p_con, String p_tableName1, String p_tableName2, Date p_presentDate, Date p_fromDate, Date p_toDate, ArrayList p_toStoreDateLst, String p_networkCode) {
        final String methodName = "processArchivalDatesLatest";
        if (_logger.isDebugEnabled()) {
            _logger.info(methodName, " entered ");
        }
        SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT);
        String queryDate = " SELECT archived_upto FROM archival_done_date WHERE table_name IN (?,?)  AND network_code =? ";
        int index = 0;
        int month = 0;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        ResultSet rst = null;
        boolean isDefaultDate = false;
        try {
            month = Integer.parseInt(Constants.getProperty("NET_STK_TRA_MONTHS"));
        } catch (Exception e) {
            month = 1;
            _logger.errorTrace(methodName, e);
        }
        try {
            pstmt = p_con.prepareStatement(queryDate);
            pstmt.setString(1, p_tableName1.trim());
            pstmt.setString(2, p_tableName2.trim());
            pstmt.setString(3, p_networkCode.trim());
            rst = pstmt.executeQuery();
            Date archivedUptoDate = null;
            if (rst.next()) {
                archivedUptoDate = rst.getDate("archived_upto");
                if (archivedUptoDate == null) {
                    archivedUptoDate = BTSLUtil.getDateFromDateString(Constants.getProperty("installation_date"));
                    isDefaultDate = true;
                    if (_logger.isDebugEnabled()) {
                        _logger.info(methodName, " No entry found in database for table = " + p_tableName1 + " and " + p_tableName2 + " Network Code =" + p_networkCode);
                    }
                }
            } else {
                archivedUptoDate = BTSLUtil.getDateFromDateString(Constants.getProperty("installation_date"));
                isDefaultDate = true;
            }
            Date oneMonthAfter = ArchivePretupsUtil.constructOneMonthAfterDate(archivedUptoDate);
            Date xMonthBefore = ArchivePretupsUtil.constructXBeforeDate(p_presentDate, month);
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, " Archived upto:" + sdf.format(archivedUptoDate) + " one month after:" + sdf.format(oneMonthAfter) + " x month before:" + sdf.format(xMonthBefore) + " first table:" + p_tableName1 + " second table:" + p_tableName2 + " and network code:" + p_networkCode);
            }
            if (oneMonthAfter.after(xMonthBefore)) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, "  Error For(descrption given below) table= " + p_tableName1 + " and " + p_tableName2 + " Network Code=" + p_networkCode + " Total Month=" + month);
                }
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, "  Xmonth before date is samller than one month after archival date, control returning from here");
                }
                p_fromDate = null;
                p_toDate = null;
                p_toStoreDateLst.add(p_fromDate);
                p_toStoreDateLst.add(p_toDate);
                index = 0;
                return index;
            }
            if ((p_fromDate.compareTo(archivedUptoDate) <= 0)) {
                if (!isDefaultDate) {
                    String updateQuery = " UPDATE archival_done_date SET archived_upto =?,modified_on =? WHERE table_name IN (?,?) and network_code =? ";
                    if (_logger.isDebugEnabled()) {
                        _logger.info(methodName, " updateQuery: " + updateQuery);
                    }
                    pstmt1 = p_con.prepareStatement(updateQuery);
                    int i = 1;
                    pstmt1.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                    pstmt1.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new Date()));
                    pstmt1.setString(i++, p_tableName1.trim());
                    pstmt1.setString(i++, p_tableName2.trim());
                    pstmt1.setString(i++, p_networkCode.trim());
                    index = pstmt1.executeUpdate();
                } else {
                    StringBuffer insertQueryBuff = new StringBuffer(" INSERT INTO archival_done_date (NETWORK_CODE, TABLE_NAME, ARCHIVED_UPTO, CREATED_ON, MODIFIED_ON, DELETED_UPTO) ");
                    insertQueryBuff.append("Values (?,?,?,?,?,?)");
                    if (_logger.isDebugEnabled()) {
                        _logger.info(methodName, " updateQuery: " + insertQueryBuff.toString());
                    }
                    pstmt2 = p_con.prepareStatement(insertQueryBuff.toString());
                    int i = 1;
                    pstmt2.setString(i++, p_networkCode.trim());
                    pstmt2.setString(i++, p_tableName1.trim());
                    pstmt2.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                    pstmt2.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new Date()));
                    pstmt2.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new Date()));
                    pstmt2.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new Date()));
                    index = pstmt2.executeUpdate();
                }
                p_toStoreDateLst.add(p_fromDate);
                p_toStoreDateLst.add(p_toDate);
            } else if (p_fromDate.compareTo(archivedUptoDate) > 0) {
                if (!isDefaultDate) {
                    if (_logger.isDebugEnabled()) {
                        _logger.info(methodName, " Data of previous months is still left for archival (for table) = " + p_tableName1 + " and " + p_tableName2 + " Network Code =" + p_networkCode + " .Please archive that data. Data Archived Upto " + sdf.format(archivedUptoDate));
                    }
                }

                p_fromDate = null;
                p_toDate = null;
                p_toStoreDateLst.add(p_fromDate);
                p_toStoreDateLst.add(p_toDate);
                index = 0;
            }
            return index;
        } catch (SQLException e1) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "SQLException " + e1.getMessage());
            }
            _logger.errorTrace(methodName, e1);
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exception " + e.getMessage());
            }
            _logger.errorTrace(methodName, e);
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(methodName, e2);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(methodName, e3);
                }
            }
            if (pstmt1 != null) {
                try {
                    pstmt1.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(methodName, e3);
                }
            }

            if (pstmt2 != null) {
                try {
                    pstmt2.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(methodName, e3);
                }
            }

            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, "exiting: (0 means data previously exists 1 means successfully updated)" + index);
            }
        }
        return index;
    }// end of function

    /**
     * This method is used to write into file
     * 
     * @param p_directoyPath
     *            String
     * @param p_directoryName
     *            String
     * @param p_fileName
     *            String
     * @param p_fileExt
     *            String
     * @param p_rs
     *            ResultSet
     * @param p_label
     *            String
     * @param p_maxFileLength
     *            long
     * @param isPlusMinusAllowed
     *            boolean
     * @param p_toDate
     *            Date
     * @param p_fromDate
     *            Date
     * @param p_tableName1
     *            String
     * @param p_networkCode
     *            String
     * @param p_networkName
     *            String
     */
    public static void writeInFile(String p_directoyPath, String p_directoryName, String p_fileName, String p_fileExt, ResultSet p_rs, String p_lable, long p_maxFileLength, boolean isPlusMinusAllowed, Date toDate, Date fromDate, String tableName1, String p_networkCode, String p_networkName) {
        final String methodName = "writeInFile";
        if (_logger.isDebugEnabled()) {
            _logger.info(methodName, "Entered: " + p_directoyPath + " " + p_directoryName + " " + p_fileName + " " + p_fileExt + " " + p_maxFileLength + " " + isPlusMinusAllowed + " " + toDate + " " + fromDate + " " + tableName1 + " " + p_networkName + " " + p_networkCode);
        }
        boolean isRollback = false;
        Date startTime = new Date();
        PrintWriter out = null;
        ArrayList fileNameLst = new ArrayList();
        long totalNoOfRecords = 0;
        try {
            String dirName = null;
            String fileName = null;
            long recordSetNumbers = 0;
            File newFile = null;
            ArrayList recordNumberLst = new ArrayList();
            if (p_rs != null) {
                p_rs.last();
                recordSetNumbers = p_rs.getRow();
                p_rs.beforeFirst();
            }
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, "" + p_networkName + " (" + p_networkCode + ")" + " " + recordSetNumbers);
            }
            if (recordSetNumbers == 0) {
                if (_logger.isDebugEnabled()) {
                    _logger.info(methodName, "" + p_networkName + " (" + p_networkCode + ")" + " " + recordSetNumbers + "  No Records found (Returning ....................)");
                }
                return;
            }
            long count = 0;
            StringBuffer buff = new StringBuffer();

            int fileNumber = 1;
            fileName = ArchivePretupsUtil.constructFileName(_masterDirectory + File.separator + p_fileName, _yearInt, _monthInt, p_networkCode, fileNumber, p_fileExt, _separator);
            newFile = new File(fileName);
            fileNameLst.add(fileName);
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
            String tableHeader = ArchivePretupsUtil.constructFileHeader(tableName1, fromDate, toDate, p_lable, 1);
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, "" + fileName + " " + tableHeader);
            }
            out.write(tableHeader);
            String fileData = null;
            DatabasePurgingLog.archiveLog("NetworkStockTransactionArchiving", p_directoyPath, p_directoryName, fileName, tableName1, p_networkName, "START");
            while (p_rs.next()) {
                fileData = p_rs.getString(1);
                if (BTSLUtil.NullToString(fileData).indexOf("\n") != -1) {
                    fileData = fileData.replaceAll("\n", "");
                }
                out.write(fileData + "\n");
                count++;
                totalNoOfRecords++;
                _totalRecordsWrittenInFile++;
                if (count >= p_maxFileLength) {
                    out.write(ArchivePretupsUtil.constructFileFooter(count, false));
                    if (_logger.isDebugEnabled()) {
                        _logger.info(methodName, _ENDOFFILE + " " + fileName + " " + ArchivePretupsUtil.constructFileFooter(count, true).trim());
                    }
                    recordNumberLst.add("" + count);
                    fileNumber = fileNumber + 1;
                    if (_logger.isDebugEnabled()) {
                        _logger.info(methodName, "FILENAME=" + fileName);
                    }
                    if (_logger.isDebugEnabled()) {
                        _logger.info(methodName, "Present Records in File  = " + count + " Total Number of Records(As Yet) = " + totalNoOfRecords);
                    }
                    DatabasePurgingLog.archiveLog("NetworkStockTransactionArchiving", p_directoyPath, p_directoryName, fileName, tableName1, p_networkName, "END-SUCCESS");
                    fileName = ArchivePretupsUtil.constructFileName(_masterDirectory + File.separator + p_fileName, _yearInt, _monthInt, p_networkCode, fileNumber, p_fileExt, _separator);
                    DatabasePurgingLog.archiveLog("NetworkStockTransactionArchiving", p_directoyPath, p_directoryName, fileName, tableName1, p_networkName, "START");
                    //out.close();

                    newFile = new File(fileName);
                    BTSLUtil.closeOpenStream(out, newFile);
                    fileNameLst.add(fileName);
                    //out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                    tableHeader = ArchivePretupsUtil.constructFileHeader(tableName1, fromDate, toDate, p_lable, fileNumber);
                    if (_logger.isDebugEnabled()) {
                        _logger.info(methodName, "" + fileName + " " + tableHeader);
                    }
                    out.write(tableHeader);
                    count = 0;
                }
            }// end of while loop
            DatabasePurgingLog.archiveLog("NetworkStockTransactionArchiving", p_directoyPath, p_directoryName, p_fileName, tableName1, p_networkName, "END-SUCCESS");
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, "FILENAME=" + fileName);
            }
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, " Present Records in File  = " + count + " Total Number of Records(As Yet) = " + totalNoOfRecords);
            }
            out.write(buff.toString());
            buff = null;
            if (count > 0) {
                out.write(ArchivePretupsUtil.constructFileFooter(count, false));
                recordNumberLst.add("" + count);
            } else {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                	_logger.debug(methodName, "File deleted successfully");
                }
                fileNameLst.remove(fileNameLst.size() - 1);
            }
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, _ENDOFFILE + " " + fileName + " " + ArchivePretupsUtil.constructFileFooter(count, true).trim());
            }
            isRollback = checkCreatedFiles(dirName, recordSetNumbers, totalNoOfRecords, fileNameLst, recordNumberLst);
            if (isRollback) {
                throw new BTSLBaseException("writeInFile Delete All Files and Rollback ");
            }
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "" + p_fileName + " Network Code=" + p_networkCode + "  Error =" + e);
            }
            ArchivePretupsUtil.deleteAllFile(fileNameLst);
            _logger.errorTrace(methodName, e);
        } finally {
            Date endTime = new Date();
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, "" + p_networkCode + " Records=" + totalNoOfRecords + " Time Taken" + ((endTime.getTime() - startTime.getTime()) / (60 * 1000)) + " Min");
            }
            try {
            	if (out != null) {
    				out.close();
    			}
            } catch (Exception e1) {
            	_logger.errorTrace(methodName, e1);
            }
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, "Exiting ");
            }
        }
    }

    /**
     * @param directoryPath
     *            String
     * @param rsTotalNumberOfRecords
     *            Long
     * @param totalNumberOfRecords
     *            Long
     * @param fileNameList
     *            Arraylist
     * @param recordCountList
     *            Arraylis
     * @return
     */
    public static boolean checkCreatedFiles(String directoryPath, long rsTotalNumberOfRecords, long totalNumberOfRecords, ArrayList fileNameList, ArrayList recordCountList) {
        final String methodName = "checkCreatedFiles";
        if (_logger.isDebugEnabled()) {
            _logger.info(methodName, "entered: directoryPath=" + directoryPath + " rsTotalNumberOfRecords=" + rsTotalNumberOfRecords + " totalNumberOfRecords=" + totalNumberOfRecords + " FileNameList=" + fileNameList.size() + " RecordCountListSize=" + recordCountList.size());
        }
        boolean deleteAllFiles = false;
        File newFile = null;
        String fileName = null;
        try {
            if (rsTotalNumberOfRecords != totalNumberOfRecords) {
                deleteAllFiles = true;
                if (_logger.isDebugEnabled()) {
                    _logger.info(methodName, "(rsTS != TS) " + rsTotalNumberOfRecords + " " + totalNumberOfRecords + " will be deleted");
                }
            } else {
                if (fileNameList.size() != recordCountList.size()) {
                    deleteAllFiles = true;
                    if (_logger.isDebugEnabled()) {
                        _logger.info(methodName, "(fileNameLst != RecordLst) " + fileNameList.size() + " " + recordCountList.size() + " will be deleted");
                    }
                } else {
                    String readFileFooter = null;
                    long noOfRecordsInFile = 0;
                    int index = 0;
                    int fileNameListSize = fileNameList.size();
                    for (int i = 0; i < fileNameListSize; i++) {
                        fileName = (String) fileNameList.get(i);
                        newFile = new File(fileName);
                        readFileFooter = readFileFooter(newFile);
                        readFileFooter = BTSLUtil.NullToString(readFileFooter).trim();
                        index = readFileFooter.indexOf(_FILEFOOTER);
                        if (_logger.isDebugEnabled()) {
                            _logger.info(methodName, "index value = " + index + " FooterValue=" + readFileFooter);
                        }
                        if (index == -1) {
                            deleteAllFiles = true;
                            break;
                        }
                        noOfRecordsInFile = Long.parseLong(readFileFooter.substring(index + _FILEFOOTER.length()));
                        if (noOfRecordsInFile != Long.parseLong((String) recordCountList.get(i))) {
                            if (_logger.isDebugEnabled()) {
                                _logger.info(methodName, "(noOfRecordsInFile != Count) " + noOfRecordsInFile + " " + Long.parseLong((String) recordCountList.get(i)) + " will be deleted");
                            }
                            deleteAllFiles = true;
                            break;
                        }
                    }// end of for loop
                }// end of else
            }// end of outer else
        }// end of try block
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exception: " + e);
            }
            deleteAllFiles = true;
            _logger.errorTrace(methodName, e);
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName, " exiting  deleteAllFiles =" + deleteAllFiles);
        }
        return deleteAllFiles;
    }

    /**
     * @param filePathName
     *            File
     * @return
     */
    public static String readFileFooter(File filePathName) {
        String fileFooterInfo = null;
        RandomAccessFile raf = null;
        final String methodName = "readFileFooter";
        try {
            raf = new RandomAccessFile(filePathName, "r");
            if (raf != null && filePathName.length() - _FILEFOOTERLENGTH > 0) {
                raf.seek(filePathName.length() - _FILEFOOTERLENGTH);
                fileFooterInfo = raf.readLine();
            }
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, "File Footer:" + fileFooterInfo);
            }
        } catch (IOException e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "" + filePathName + "  IOException =" + e);
            }
            _logger.errorTrace(methodName, e);
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e1) {
                    _logger.errorTrace(methodName, e1);
                }
            }
        }
        return fileFooterInfo;
    }
}// end class