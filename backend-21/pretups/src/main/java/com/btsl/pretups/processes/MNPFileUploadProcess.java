package com.btsl.pretups.processes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.StringTokenizer;

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
import com.btsl.pretups.logging.MNPPocessingLog;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

/**
 * @(#)MNPFileUploadProcess.java
 *                               Copyright(c) 2007, Bharti Telesoft Ltd.
 *                               All Rights Reserved
 *                               This class is used for uploading the mobile
 *                               number into database.File will be on the
 *                               server.
 * 
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               vikas yadav 16/04/06 Initial Creation
 * 
 **/

public class MNPFileUploadProcess {

    // Get the file path from the constants .props file,first load the constants
    // .props file.
    public static String _filePath = null;
    private static Log _logger = LogFactory.getLog(MNPFileUploadProcess.class.getName());

    /**
     * Main Method. This method creates an instance of the class
     * MNPFileUploadProcess. After creating the instance, the methods
     * reads the network code Constant props and Process log config file as
     * parameter.
     * 
     * @param args
     */

    public static void main(String[] args) {
        final long startTime = (new Date()).getTime();
        final String METHOD_NAME = "main";
        try {
            final int argSize = args.length;
            if (argSize != 3) {
                _logger.info(METHOD_NAME, "Three arguments are required  : MNPFileUploadProcess [Network code] [Constants file] [ProcessLogConfig file]");
                _logger.error("MNPFileUploadProcess main()", " Usage : MNPFileUploadProcess [Network code] [Constants file] [ProcessLogConfig file]");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MNPFileUploadProcess[main]", "", "", "",
                    "Improper usage. Usage : MNPFileUploadProcess [Network code][Constants file] [ProcessLogConfig file] ");
                throw new BTSLBaseException("MNPFileUploadProcess ", " main ", PretupsErrorCodesI.MNP_NUMBER_ARG_MISSING);
            }

            new MNPFileUploadProcess().process(args);
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

    public void process(String[] p_args) {
        final String METHOD_NAME = "process";
        try {
            loadCachesAndLogFiles(p_args[1], p_args[2]);// load Constants.props
            // and ProccessLogConfig
            // file
            NetworkPrefixCache.loadNetworkPrefixesAtStartup();
            final String _networkCode = p_args[0];

            Connection con = null;
            final String _createdBy = PretupsI.SYSTEM_USER;

            _filePath = Constants.getProperty("MNP_UPLOAD_FILE_DIR_PATH");
            if (BTSLUtil.isNullString("_filePath")) {
                _logger.info(METHOD_NAME, " File directory path for upload is not defined in Constant.props file Kindly define it..............");
                _logger.error("main ", ":File directory path for upload is not defined in Constant.props file Kindly define it..............");
                throw new BTSLBaseException("MNPFileUploadProcess", METHOD_NAME, PretupsErrorCodesI.MNP_ERROR_FILEPATH_NULL);
            }
            // Check file path is eist or not
            final File _dir = new File(_filePath);
            if (!(_dir.exists())) {
                _logger.info(METHOD_NAME, " Could not found Directory specified, Please make sure Dir Exist..............");
                _logger.error("main ", ": Could not found Directory specified, Please make sure Dir Exist..............");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MNPFileUploadProcess[process]", "", "", "",
                    "File dir not found");
                throw new BTSLBaseException("MNPFileUploadProcess", METHOD_NAME, PretupsErrorCodesI.MNP_ERROR_DIR_NOT_EXIST);
            }

            // read files from this path
            final String arr[] = _dir.list();
            final int _numberOfFiles = arr.length;
            // opening the connection
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                _logger.info(METHOD_NAME, " Could not connect to database. Please make sure that database server is up..............");
                _logger.error("main ", ": Could not connect to database. Please make sure that database server is up..............");
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MNPFileUploadProcess[process]", "",
                    "", "", "Could not connect to Database");
                throw new BTSLBaseException("MNPFileUploadProcess", METHOD_NAME, PretupsErrorCodesI.MNP_ERROR_CONN_NULL);
            }

            for (int i = 0; i < _numberOfFiles; i++) {
                try {
                    writeFileToDatabase(con, _filePath + arr[i], _createdBy, _networkCode, arr[i]);
                } catch (BTSLBaseException be) {
                    _logger.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
                    final File file = new File(_filePath, arr[i]);
                    file.delete();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MNPFileUploadProcess[process]", "", "", "",
                        "Not able to upload file" + arr[i] + "FAIL" + be.getMessage());
                    _logger.errorTrace(METHOD_NAME, be);
                }
            }
        } catch (BTSLBaseException be) {
            _logger.error("writeFileToDatabase", "BTSLBaseException=" + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MNPFileUploadProcess[process]", "", "", "",
                "BTSLBaseException:" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            _logger.error("writeFileToDatabase", "Exception=" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MNPFileUploadProcess[process]", "", "", "",
                "Exception:" + e.getMessage());
        }

    }

    /**
     * This method will insert the msisdn,type and action details in the
     * PORTED_MSISDN table
     * 
     * @return int
     * @param p_con
     *            java.sql.Connection
     * @param p_fileName
     *            java.lang.String
     * @param p_createdBy
     *            java.lang.String
     * @param p_locationCode
     *            String
     * @param p_file
     *            String
     * @exception BTSLBaseException
     */

    public int writeFileToDatabase(Connection p_con, String p_filename, String p_createdBy, String p_locationCode, String p_file) throws BTSLBaseException {
        final String METHOD_NAME = "writeFileToDatabase";
        if (_logger.isDebugEnabled()) {
            _logger.debug("writeFileToDatabase", " Entered p_filename:" + p_filename + "p_locationCode" + p_locationCode + "p_createdBy" + p_createdBy + "p_file" + p_file);
        }
        boolean processFile = false;
        FileReader fileReader = null; // file reader
        BufferedReader bufferReader = null;
        long recordsTotal = 0;// for line numbers
        int totRecords = 0;
        String delim = Constants.getProperty("DelimiterforMNPFile");
        final Date currentDate = new Date(System.currentTimeMillis());
        if (BTSLUtil.isNullString(delim)) {
            delim = " ";
        }
        PreparedStatement insertPstmt = null;
        PreparedStatement updatePstmt = null;
        PreparedStatement isExistPstmt = null;
        PreparedStatement isExistPortedPstmt = null;
        PreparedStatement isExistUserPstmt = null;
        PreparedStatement suspendUserPstmt = null;
        java.sql.ResultSet isExistsAsPort = null;
        java.sql.ResultSet isExistingUser = null;
        java.sql.ResultSet found = null;
        String tempStr = null;
        int msisdnAdded = 0;
        int queryResult = -1;
        // check mobile number port type and subscriber type is exist in
        // database same as entered by user
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM ported_msisdn WHERE msisdn=? ");
        sqlBuff.append("AND subscriber_type=? ");
        sqlBuff.append("AND port_type=? ");
        // msisdn is already exist as ported in or out then only update not
        // insert
        final String selectQuery1 = "SELECT 1 FROM ported_msisdn WHERE msisdn=? ";
        // update if msisdn is already pesent in database
        final String updateQuery = "UPDATE  ported_msisdn SET subscriber_type=UPPER(?), port_type=UPPER(?), created_by=?, created_on=? WHERE msisdn=? ";
        // Query for Inserting
        final StringBuffer insertSql = new StringBuffer("INSERT INTO ported_msisdn(msisdn,subscriber_type,");
        insertSql.append("port_type,created_by,created_on) VALUES (?,UPPER(?),UPPER(?),?,?)");

        // check mobile number is already exist in database
        final StringBuffer strBuff = new StringBuffer("select UP.MSISDN,U.status");
        strBuff.append(" FROM user_phones UP,users U WHERE UP.msisdn = ?");
        strBuff.append(" AND U.user_id=UP.user_id AND (U.status <> 'N' AND U.status <> 'C')");
        // if exist and we ae tying to add as ported out number then suspend the
        // user(preference base)
        final StringBuffer updateSql1 = new StringBuffer("UPDATE users SET previous_status=status, status=?, modified_on=?, modified_by=? ");
        updateSql1.append("WHERE user_id=(SELECT UP.user_id FROM user_phones UP, users U WHERE U.user_id=UP.user_id AND U.status NOT IN('N','C') AND UP.msisdn=?) ");

        final String selectQuery = sqlBuff.toString();
        final String inserttQuery = insertSql.toString();
        final String selectQueryUsr = strBuff.toString();
        final String suspendQueryUsr = updateSql1.toString();
        final StringBuffer p_invalidMsisdn = new StringBuffer();
        try {
            boolean fileMoved = false;
            StringTokenizer startparser = null;
            int updateStatusCount = 0;
            insertPstmt = p_con.prepareStatement(inserttQuery);
            updatePstmt = p_con.prepareStatement(updateQuery);
            isExistPstmt = p_con.prepareStatement(selectQuery1);
            isExistPortedPstmt = p_con.prepareStatement(selectQuery);
            isExistUserPstmt = p_con.prepareStatement(selectQueryUsr);
            suspendUserPstmt = p_con.prepareStatement(suspendQueryUsr);

            fileReader = new FileReader("" + p_filename);
            if (fileReader != null) {
                bufferReader = new BufferedReader(fileReader);
            } else {
                bufferReader = null;
            }
            if (bufferReader != null && bufferReader.ready()) // If File Not
            // Blank Read line
            // by Line
            {
                recordsTotal = 0;
                totRecords = 0;
                String filteredMsisdn;
                String msisdnPrefix;
                NetworkPrefixVO networkPrefixVO = null;
                String networkCode;
                String msisdn;
                String subscriberType = "";
                String portedType = "";
                int i = 1;
                // If Line is not Blank Process the Number
                while ((tempStr = bufferReader.readLine()) != null) // Read the
                // file till
                // the end
                // of file.
                {
                    if (tempStr.trim().length() == 0) {
                        continue;
                    }
                    recordsTotal++; // Keeps track of line number
                    try {
                        startparser = new StringTokenizer(tempStr, delim);
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("writeFileToDatabase", "Input = " + tempStr);
                            _logger.debug("writeFileToDatabase", "There are " + startparser.countTokens() + " entries");
                        }

                        if (startparser.countTokens() != 3) {
                            if (_logger.isDebugEnabled()) {
                                _logger.debug("writeFileToDatabase", "Not a valid entry" + tempStr);
                            }
                            MNPPocessingLog.log("MNP File Upload", p_createdBy, "", recordsTotal, "Not a valid entry", "Fail", p_filename + "," + p_locationCode);
                            continue;
                        }
                        msisdn = startparser.nextToken().trim();
                        subscriberType = BTSLUtil.NullToString(startparser.nextToken()).trim().toUpperCase();
                        portedType = BTSLUtil.NullToString(startparser.nextToken()).trim().toUpperCase();
                        filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
                        if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                            if (_logger.isDebugEnabled()) {
                                _logger.debug("writeFileToDatabase", "Not a valid MSISDN" + msisdn);
                            }
                            MNPPocessingLog.log("MNP File Upload", p_createdBy, msisdn, recordsTotal, "Not a valid MSISDN", "Fail", p_filename + "," + p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");
                            continue;
                        }
                        msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);

                        if (networkPrefixVO == null) {
                            if (_logger.isDebugEnabled()) {
                                _logger.debug("writeFileToDatabase", "Port prefix not defined" + msisdn);
                            }
                            MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "Port prefix not defined", "Fail", p_filename + "," + p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");
                            continue;
                        } else if (portedType.equalsIgnoreCase(PretupsI.PORTED_IN)) {
                            if (!networkPrefixVO.getOperator().equalsIgnoreCase(PretupsI.OPERATOR_TYPE_PORT)) {
                                if (_logger.isDebugEnabled()) {
                                    _logger.debug("writeFileToDatabase", "Port prefix not defined" + msisdn);
                                }
                                MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "Port prefix not defined", "Fail", p_filename + "," + p_locationCode);
                                p_invalidMsisdn.append(msisdn);
                                p_invalidMsisdn.append(",");
                                continue;
                            }
                        }
                        networkCode = networkPrefixVO.getNetworkCode();
                        if (!networkCode.equals(p_locationCode)) {
                            if (_logger.isDebugEnabled()) {
                                _logger.debug("writeFileToDatabase", "Not supporting Network" + msisdn);
                            }
                            MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "Not supporting Network", "Fail", p_filename + "," + p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");
                            continue;
                        }

                        if (!(subscriberType.equalsIgnoreCase(PretupsI.SERIES_TYPE_PREPAID) || subscriberType.equalsIgnoreCase(PretupsI.SERIES_TYPE_POSTPAID))) {
                            if (_logger.isDebugEnabled()) {
                                _logger.debug("writeFileToDatabase", "Not supported Subscriber type" + msisdn);
                            }
                            MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "Not supported Subscriber type", "Fail", p_filename + "," + p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");
                            continue;
                        }

                        if (!(portedType.equalsIgnoreCase(PretupsI.PORTED_IN) || portedType.equalsIgnoreCase(PretupsI.PORTED_OUT))) {
                            if (_logger.isDebugEnabled()) {
                                _logger.debug("writeFileToDatabase", "Not supported PORT type" + msisdn);
                            }
                            MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "Not supported Port type", "Fail", p_filename + "," + p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");
                            continue;
                        }

                        try {
                            isExistPortedPstmt.setString(1, filteredMsisdn);
                            isExistPortedPstmt.setString(2, subscriberType);
                            isExistPortedPstmt.setString(3, portedType);
                            isExistsAsPort = isExistPortedPstmt.executeQuery();
                        } catch (Exception e) {
                            _logger.error("writeFileToDatabase", "Exception=" + e.getMessage());
                            _logger.errorTrace(METHOD_NAME, e);
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                "MNPFileUploadProcess[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
                            throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.processing");
                        }// end of catch
                        if (isExistsAsPort.next()) {
                            if (_logger.isDebugEnabled()) {
                                _logger.debug("writeFileToDatabase", "Msisdn already exist" + filteredMsisdn);
                            }
                            MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "Msisdn already exist", "Fail", p_filename + "," + p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");
                            continue;
                        }

                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PORT_USR_SUSPEND_REQ))).booleanValue()) {
                            // suspension of user is according to the flag
                            // PORT_USR_SUSPEND_REQ from system preferences.
                            if (portedType.equalsIgnoreCase(PretupsI.PORTED_OUT)) {
                                try {
                                    isExistUserPstmt.setString(1, filteredMsisdn);
                                    isExistingUser = isExistUserPstmt.executeQuery();
                                } catch (Exception e) {
                                    _logger.error("writeFileToDatabase", "Exception=" + e.getMessage());
                                    _logger.errorTrace(METHOD_NAME, e);
                                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                        "MNPFileUploadProcess[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
                                    throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.processing");
                                }// end of catch
                                if (isExistingUser.next()) {// if user status is
                                    // SR or S do not
                                    // suspend it.
                                    final String status = isExistingUser.getString("status");
                                    if (status.equals(PretupsI.USER_STATUS_SUSPEND_REQUEST) || status.equals(PretupsI.USER_STATUS_SUSPEND)) {
                                        if (_logger.isDebugEnabled()) {
                                            _logger.debug("writeFileToDatabase", "User is already suspended, " + filteredMsisdn);
                                        }
                                        MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "User is already suspended", "Fail",
                                            p_filename + "," + p_locationCode);
                                        p_invalidMsisdn.append(msisdn);
                                        p_invalidMsisdn.append(",");
                                        continue;
                                    }
                                    // suspend channel user
                                    try {
                                        suspendUserPstmt.setString(4, filteredMsisdn);
                                        suspendUserPstmt.setString(1, PretupsI.USER_STATUS_SUSPEND);
                                        suspendUserPstmt.setString(3, p_createdBy);
                                        suspendUserPstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                                        updateStatusCount = suspendUserPstmt.executeUpdate();
                                    } catch (Exception e) {
                                        _logger.error("writeFileToDatabase", "Exception=" + e.getMessage());
                                        _logger.errorTrace(METHOD_NAME, e);
                                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                            "MNPFileUploadProcess[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
                                        throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.processing");
                                    }// end of catch
                                    if (updateStatusCount <= 0) {
                                        if (_logger.isDebugEnabled()) {
                                            _logger.debug("writeFileToDatabase", "User suspend process fail, " + filteredMsisdn);
                                        }
                                        MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "User suspend process fail", "Fail",
                                            p_filename + "," + p_locationCode);
                                        p_invalidMsisdn.append(msisdn);
                                        p_invalidMsisdn.append(",");
                                        continue;
                                    }
                                }
                            }
                        }
                        // here we try to insert the record in database
                        // if record already exists in database then exception
                        // is thrown
                        // the exception is caught in catch block and check if
                        // it is of unique key constraint voilation
                        // if exception is of unique key constraint voilation
                        // then update the record that exists in database with
                        // new values
                        try {
                            i = 1;
                            // Set paremeters

                            isExistPstmt.setString(1, filteredMsisdn);
                            found = isExistPstmt.executeQuery();

                            if (found.next()) {
                                updatePstmt.setString(i++, subscriberType);
                                updatePstmt.setString(i++, portedType);
                                updatePstmt.setString(i++, p_createdBy);
                                updatePstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                                updatePstmt.setString(i++, filteredMsisdn);
                                queryResult = updatePstmt.executeUpdate();
                            } else {

                                insertPstmt.setString(i++, filteredMsisdn);
                                insertPstmt.setString(i++, subscriberType);
                                insertPstmt.setString(i++, portedType);
                                insertPstmt.setString(i++, p_createdBy);
                                insertPstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                                // Execute Query
                                // if sql exception occure then it is caught in
                                // catch block
                                queryResult = insertPstmt.executeUpdate();
                            }
                            // if any error occurs in inserting the record then
                            // make an entry in logs for that msisdn.
                            if (queryResult <= 0) {
                                MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "Error while inserting record", "Fail",
                                    p_filename + "," + p_locationCode);
                            } else {
                                msisdnAdded += queryResult;
                                totRecords++; // Total records count to be
                                // inserted or updated in database
                                MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "Record inserted", "Pass", p_filename + "," + p_locationCode);
                                p_con.commit();
                            }
                            // Clear Paremeters
                            insertPstmt.clearParameters();
                            updatePstmt.clearParameters();
                            suspendUserPstmt.clearParameters();
                            isExistPortedPstmt.clearParameters();
                            isExistPstmt.clearParameters();
                            isExistUserPstmt.clearParameters();
                        }// end of try
                        catch (Exception e) {
                            _logger.error("writeFileToDatabase", "Exception=" + e.getMessage());
                            _logger.errorTrace(METHOD_NAME, e);
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                "MNPFileUploadProcess[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
                            throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.processing");
                        }// end of catch
                    } catch (Exception e) {
                        _logger.error("writeFileToDatabase", "Exception=" + e.getMessage());
                        _logger.errorTrace(METHOD_NAME, e);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "MNPFileUploadProcess[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
                        throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.processing");
                    }// end of catch
                } // end of While
                if (_logger.isDebugEnabled()) {
                    _logger.debug("writeFileToDatabase", "Total lines in File " + recordsTotal + " And Total Records for process:" + totRecords);
                }
            }// end of bufferedReader !=null
            if (recordsTotal == 0) {
                throw new BTSLBaseException("MNPFileUploadProcess", "writeFileToDatabase", PretupsErrorCodesI.MNP_ERROR_ZERO_FILESIZE);
            }
            if (totRecords == 0) {
                throw new BTSLBaseException("MNPFileUploadProcess", "writeFileToDatabase", PretupsErrorCodesI.MNP_ERROR_NORECORD_IN_FILE);
            }
            // Moving File after Processing
            if (msisdnAdded > 0) {
                fileMoved = this.moveFileToArchive(p_filename, p_file);
                if (fileMoved) {
                    processFile = true;
                } else {
                    throw new BTSLBaseException("MNPFileUploadProcess", "writeFileToDatabase", PretupsErrorCodesI.MNP_ERROR_IN_FILE_MOVE);
                }
            } else {
                MNPPocessingLog.log("File Upload", p_createdBy, "", recordsTotal, "Error in all records", "Fail", p_filename + "," + p_locationCode);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MNPFileUploadProcess[writeFileToDatabase]", "",
                    "", "", "Error in all records");
            }

        }// end of try(outer most try)
        catch (BTSLBaseException be) {
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            _logger.error("writeFileToDatabase", "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MNPFileUploadProcess[writeFileToDatabase]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.sql.processing");
        } catch (Exception e) {
            _logger.error("writeFileToDatabase", "Exception " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MNPFileUploadProcess[writeFileToDatabase]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.processing");
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeFileToDatabase", "processed till record no:" + totRecords);
            }
            // Write in LOGS
            if (!processFile) {
                totRecords = 0;
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeFileToDatabase",
                    "p_userID:" + p_createdBy + " p_locationCode:" + p_locationCode + " Processed=" + p_file + " ,No of records=" + totRecords + " ,Status=" + processFile);
            }
            MNPPocessingLog.log("File Upload", p_createdBy, null, totRecords, "Records uploaded to the database", "Finally Block", p_filename + "," + p_locationCode);
            // Destroying different objects
            try {
                if (bufferReader != null) {
                    bufferReader.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (insertPstmt != null) {
                    insertPstmt.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (updatePstmt != null) {
                    updatePstmt.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (isExistPstmt != null) {
                    isExistPstmt.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (isExistPortedPstmt != null) {
                    isExistPortedPstmt.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (isExistUserPstmt != null) {
                    isExistUserPstmt.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (suspendUserPstmt != null) {
                    suspendUserPstmt.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (isExistsAsPort != null) {
                    isExistsAsPort.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (isExistingUser != null) {
                    isExistingUser.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (found != null) {
                    found.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeFileToDatabase", " Exiting updateCount=" + msisdnAdded + ", p_invalidMsisdn=" + p_invalidMsisdn);
            }
        }// end of finally
         // adding up the updated and inserted record count and return that
        return msisdnAdded;
    }

    /**
     * This method will move the processed file in seperate folder
     * 
     * @param p_fileName
     * @param p_file
     * @return boolean
     */
    public boolean moveFileToArchive(String p_fileName, String p_file) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("moveFileToArchive", " Entered ");
        }
        final File fileRead = new File(p_fileName);
        File fileArchive = new File("" + Constants.getProperty("MNPArchiveFilePath"));
        if (!fileArchive.isDirectory()) {
            fileArchive.mkdirs();
        }
        fileArchive = new File("" + Constants.getProperty("MNPArchiveFilePath") + p_file + "." + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()); // to
        // make
        // the
        // new
        // file
        // name
        final boolean flag = fileRead.renameTo(fileArchive);
        if (_logger.isDebugEnabled()) {
            _logger.debug("moveFileToArchive", " Exiting File Moved=" + flag);
        }
        return flag;
    }// end of moveFileToArchive

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

                _logger.debug(METHOD_NAME, "MNPFileUploadProcess loadCachesAndLogFiles Constants file not found on location:: " + constantsFile.toString());
                _logger.error("MNPFileUploadProcess[loadCachesAndLogFiles]", " Constants file not found on location:: " + constantsFile.toString());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MNPFileUploadProcess[loadCachesAndLogFiles]", "",
                    "", "", " The Constants file doesn't exists at the path specified. ");
                throw new BTSLBaseException("MNPFileUploadProcess ", " loadCachesAndLogFiles ", PretupsErrorCodesI.MNP_MISSING_CONST_FILE);
            }

            logconfigFile = new File(p_arg2);
            if (!logconfigFile.exists()) {
                _logger.debug(METHOD_NAME, "MNPFileUploadProcess loadCachesAndLogFiles Logconfig file not found on location:: " + logconfigFile.toString());
                _logger.error("MNPFileUploadProcess[loadCachesAndLogFiles]", " ProcessLogConfig file not found on location:: " + logconfigFile.toString());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MNPFileUploadProcess[loadCachesAndLogFiles]", "",
                    "", "", " The ProcessLogConfig file doesn't exists  at the path specified. ");
                throw new BTSLBaseException("MNPFileUploadProcess ", "loadCachesAndLogFiles ", PretupsErrorCodesI.MNP_MISSING_LOG_FILE);
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (BTSLBaseException be) {
            _logger.error("MNPFileUploadProcess[loadCachesAndLogFiles]", "BTSLBaseException =" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("MNPFileUploadProcess[loadCachesAndLogFiles]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileUploaderUtil[loadCachesAndLogFiles]", "",
                "", "", "Exception=" + e.getMessage());
            throw new BTSLBaseException("MNPFileUploadProcess ", " loadCachesAndLogFiles ", PretupsErrorCodesI.MNP_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of Exception
        finally {
            if (logconfigFile != null) {
                logconfigFile = null;
            }
            if (constantsFile != null) {
                constantsFile = null;
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("MNPFileUploadProcess[loadCachesAndLogFiles]", " Exiting..........");
            }
        }// end of finally
    }
}
