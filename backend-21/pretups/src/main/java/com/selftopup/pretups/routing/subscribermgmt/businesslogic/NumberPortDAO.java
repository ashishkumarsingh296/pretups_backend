/*
 * @# NumberPortDAO.java
 * This class is used for database interaction of the Mobile number portability
 * Module.
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Apr 02, 2007 Vikas yadav Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2007 Bharti Telesoft Ltd.
 */
package com.selftopup.pretups.routing.subscribermgmt.businesslogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.StringTokenizer;
import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.logging.MNPPocessingLog;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixCache;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixVO;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

public class NumberPortDAO {

    /**
     * Field _logger.
     */
    private static Log _logger = LogFactory.getLog(NumberPortDAO.class.getName());

    /**
     * NumberPortDAO constructor comment.
     */
    public NumberPortDAO() {
        super();
    }

    /**
     * This method will insert the msisdn,type and action details in the
     * PORTED_MSISDN table
     * 
     * @return java.lang.String
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
        if (_logger.isDebugEnabled())
            _logger.debug("writeFileToDatabase", " Entered p_filename:" + p_filename + "p_locationCode" + p_locationCode + "p_createdBy" + p_createdBy + "p_file" + p_file);
        boolean processFile = false;
        FileReader fileReader = null; // file reader
        BufferedReader bufferReader = null;
        long recordsTotal = 0;// for line numbers
        int totRecords = 0;
        String delim = Constants.getProperty("DelimiterforMNPFile");
        Date currentDate = new Date(System.currentTimeMillis());
        if (BTSLUtil.isNullString(delim))
            delim = " ";
        PreparedStatement insertPstmt = null;
        PreparedStatement updatePstmt = null;
        PreparedStatement isExistPstmt = null;
        PreparedStatement isExistPortedPstmt = null;
        PreparedStatement isExistUserPstmt = null;
        PreparedStatement suspendUserPstmt = null;
        String tempStr = null;
        ResultSet isExistsAsPort = null;
        ResultSet isExistingUser = null;
        ResultSet found = null;
        int msisdnAdded = 0;
        int queryResult = -1;
        // check mobile number port type and subscriber type is exist in
        // database same as entered by user
        StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM ported_msisdn WHERE msisdn=? ");
        sqlBuff.append("AND subscriber_type=? ");
        sqlBuff.append("AND port_type=? ");
        // msisdn is already exist as ported in or out then only update not
        // insert
        String selectQuery1 = "SELECT 1 FROM ported_msisdn WHERE msisdn=? ";
        // update if msisdn is already pesent in database
        String updateQuery = "UPDATE  ported_msisdn SET subscriber_type=UPPER(?), port_type=UPPER(?), created_by=?, created_on=? WHERE msisdn=? ";
        // Query for Inserting
        StringBuffer insertSql = new StringBuffer("INSERT INTO ported_msisdn(msisdn,subscriber_type,");
        insertSql.append("port_type,created_by,created_on) VALUES (?,UPPER(?),UPPER(?),?,?)");

        // check mobile number is already exist in database
        StringBuffer strBuff = new StringBuffer("select UP.MSISDN,U.status");
        strBuff.append(" FROM user_phones UP,users U WHERE UP.msisdn = ?");
        strBuff.append(" AND U.user_id=UP.user_id AND (U.status <> 'N' AND U.status <> 'C')");
        // if exist and we ae tying to add as ported out number then suspend the
        // user(preference base)
        StringBuffer updateSql1 = new StringBuffer("UPDATE users SET previous_status=status, status=?, modified_on=?, modified_by=? ");
        updateSql1.append("WHERE user_id=(SELECT UP.user_id FROM user_phones UP, users U WHERE U.user_id=UP.user_id AND U.status NOT IN('N','C') AND UP.msisdn=?) ");

        String selectQuery = sqlBuff.toString();
        String inserttQuery = insertSql.toString();
        String selectQueryUsr = strBuff.toString();
        String suspendQueryUsr = updateSql1.toString();
        StringBuffer p_invalidMsisdn = new StringBuffer();
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

            try {
                fileReader = new FileReader("" + p_filename);
                if (fileReader != null)
                    bufferReader = new BufferedReader(fileReader);
                else
                    bufferReader = null;
            } catch (Exception e) {
                bufferReader = null;
            }
            if (bufferReader != null || bufferReader.ready()) // If File Not
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
                String subscriberType;
                String portedType;
                int i = 1;
                // If Line is not Blank Process the Number
                while ((tempStr = bufferReader.readLine()) != null) // Read the
                                                                    // file till
                                                                    // the end
                                                                    // of file.
                {
                    if (tempStr.trim().length() == 0)// To skip all the blank
                                                     // lines
                        continue;
                    recordsTotal++; // Keeps track of line number
                    try {
                        startparser = new StringTokenizer(tempStr, delim);
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("writeFileToDatabase", "Input = " + tempStr);
                            _logger.debug("writeFileToDatabase", "There are " + startparser.countTokens() + " entries");
                        }

                        if (startparser.countTokens() != 3) {
                            if (_logger.isDebugEnabled())
                                _logger.debug("writeFileToDatabase", "Not a valid entry" + tempStr);
                            MNPPocessingLog.log("MNP File Upload", p_createdBy, "", recordsTotal, "Not a valid entry", "Fail", p_filename + "," + p_locationCode);
                            continue;
                        }
                        msisdn = BTSLUtil.NullToString(startparser.nextToken()).trim();
                        subscriberType = BTSLUtil.NullToString(startparser.nextToken()).trim().toUpperCase();
                        portedType = BTSLUtil.NullToString(startparser.nextToken()).trim().toUpperCase();
                        filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);

                        if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                            if (_logger.isDebugEnabled())
                                _logger.debug("writeFileToDatabase", "Not a valid MSISDN" + msisdn);
                            MNPPocessingLog.log("MNP File Upload", p_createdBy, msisdn, recordsTotal, "Not a valid MSISDN", "Fail", p_filename + "," + p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");
                            continue;
                        }
                        msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);

                        if (networkPrefixVO == null) {
                            if (_logger.isDebugEnabled())
                                _logger.debug("writeFileToDatabase", "Port prefix not defined" + msisdn);
                            MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "Port prefix not defined", "Fail", p_filename + "," + p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");
                            continue;
                        } else if (portedType.equalsIgnoreCase(PretupsI.PORTED_IN)) {
                            if (!networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                                if (_logger.isDebugEnabled())
                                    _logger.debug("writeFileToDatabase", "Port prefix not defined" + msisdn);
                                MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "Port prefix not defined", "Fail", p_filename + "," + p_locationCode);
                                p_invalidMsisdn.append(msisdn);
                                p_invalidMsisdn.append(",");
                                continue;
                            }
                        }

                        networkCode = networkPrefixVO.getNetworkCode();
                        if (!networkCode.equals(p_locationCode)) {
                            if (_logger.isDebugEnabled())
                                _logger.debug("writeFileToDatabase", "Not supporting Network" + msisdn);
                            MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "Not supporting Network", "Fail", p_filename + "," + p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");
                            continue;
                        }

                        if (!(subscriberType.equalsIgnoreCase(PretupsI.SERIES_TYPE_PREPAID) || subscriberType.equalsIgnoreCase(PretupsI.SERIES_TYPE_POSTPAID))) {
                            if (_logger.isDebugEnabled())
                                _logger.debug("writeFileToDatabase", "Not supported Subscriber type" + msisdn);
                            MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "Not supported Subscriber type", "Fail", p_filename + "," + p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");
                            continue;
                        }

                        if (!(portedType.equalsIgnoreCase(PretupsI.PORTED_IN) || portedType.equalsIgnoreCase(PretupsI.PORTED_OUT))) {
                            if (_logger.isDebugEnabled())
                                _logger.debug("writeFileToDatabase", "Not supported PORT type" + msisdn);
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
                            e.printStackTrace();
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
                            throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.processing");
                        }// end of catch
                        if (isExistsAsPort.next()) {
                            if (_logger.isDebugEnabled())
                                _logger.debug("processMNPFile", "Msisdn already exist" + filteredMsisdn);
                            MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "Msisdn already exist", "Fail", p_filename + "," + p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");
                            continue;
                        }
                        if (SystemPreferences.PORT_OUT_USER_SUSPEND_REQUIRED) {
                            // suspension of user is according to the flag
                            // PORT_USR_SUSPEND_REQ from system preferences.
                            if (portedType.equalsIgnoreCase(PretupsI.PORTED_OUT)) {
                                try {
                                    isExistUserPstmt.setString(1, filteredMsisdn);
                                    isExistingUser = isExistUserPstmt.executeQuery();
                                } catch (Exception e) {
                                    _logger.error("writeFileToDatabase", "Exception=" + e.getMessage());
                                    e.printStackTrace();
                                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
                                    throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.processing");
                                }// end of catch
                                if (isExistingUser.next()) {
                                    String status = isExistingUser.getString("status");
                                    if (status.equals(PretupsI.USER_STATUS_SUSPEND_REQUEST) || status.equals(PretupsI.USER_STATUS_SUSPEND)) {
                                        if (_logger.isDebugEnabled())
                                            _logger.debug("processMNPFile", "User is already suspended, " + filteredMsisdn);
                                        MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "User is already suspended", "Fail", p_filename + "," + p_locationCode);
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
                                        e.printStackTrace();
                                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
                                        throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.processing");
                                    }// end of catch
                                    if (updateStatusCount <= 0) {
                                        if (_logger.isDebugEnabled())
                                            _logger.debug("processMNPFile", "User suspend process fail, " + filteredMsisdn);
                                        MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "User suspend process fail", "Fail", p_filename + "," + p_locationCode);
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
                            if (queryResult <= 0)
                                MNPPocessingLog.log("File Upload", p_createdBy, msisdn, recordsTotal, "Error while inserting record", "Fail", p_filename + "," + p_locationCode);
                            else {
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
                            e.printStackTrace();
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
                            throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.processing");
                        }// end of catch
                    } catch (Exception e) {
                        _logger.error("writeFileToDatabase", "Exception=" + e.getMessage());
                        e.printStackTrace();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
                        throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.processing");
                    }// end of catch
                } // end of While
                if (_logger.isDebugEnabled())
                    _logger.debug("writeFileToDatabase", "Total lines in File " + recordsTotal + " And Total Records for process:" + totRecords);
            }// end of bufferedReader !=null
            if (recordsTotal == 0) {
                throw new BTSLBaseException(this, "writeFileToDatabase", "routing.mnp.error.zerofilesize", "selectMNPfile");
            }
            if (totRecords == 0) {
                throw new BTSLBaseException(this, "writeFileToDatabase", "routing.mnp.error.norecords", "selectMNPfile");
            }
            if (bufferReader != null)
                bufferReader.close();
            if (fileReader != null)
                fileReader.close();

            // Moving File after Processing
            if (msisdnAdded > 0) {
                fileMoved = this.moveFileToArchive(p_filename, p_file);
                if (fileMoved)
                    processFile = true;
                else
                    throw new BTSLBaseException(this, "writeFileToDatabase", "routing.mnp.error.filenomove", "selectMNPfile");
            }

        }// end of try(outer most try)
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _logger.error("writeFileToDatabase", "SQLException " + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeFileToDatabase]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.sql.processing");
        } catch (Exception e) {
            _logger.error("writeFileToDatabase", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.processing");
        } finally {
            if (_logger.isDebugEnabled())
                _logger.debug("writeFileToDatabase", "processed till record no:" + totRecords);
            // Write in LOGS
            if (!processFile)
                totRecords = 0;
            if (_logger.isDebugEnabled())
                _logger.debug("writeFileToDatabase", "p_userID:" + p_createdBy + " p_locationCode:" + p_locationCode + " Processed=" + p_file + " ,No of records=" + totRecords + " ,Status=" + processFile);
            MNPPocessingLog.log("File Upload", p_createdBy, null, totRecords, "Records uploaded to the database", "Finally Block", p_filename + "," + p_locationCode);
            // Destroying different objects
            try {
                if (bufferReader != null)
                    bufferReader.close();
            } catch (Exception e) {
            }
            try {
                if (fileReader != null)
                    fileReader.close();
            } catch (Exception e) {
            }
            try {
                if (insertPstmt != null)
                    insertPstmt.close();
            } catch (Exception ex) {
            }
            try {
                if (updatePstmt != null)
                    updatePstmt.close();
            } catch (Exception ex) {
            }
            try {
                if (isExistPstmt != null)
                    isExistPstmt.close();
            } catch (Exception ex) {
            }
            try {
                if (isExistPortedPstmt != null)
                    isExistPortedPstmt.close();
            } catch (Exception ex) {
            }
            try {
                if (isExistUserPstmt != null)
                    isExistUserPstmt.close();
            } catch (Exception ex) {
            }
            try {
                if (suspendUserPstmt != null)
                    suspendUserPstmt.close();
            } catch (Exception ex) {
            }
            try {
                if (isExistsAsPort != null)
                    isExistsAsPort.close();
            } catch (Exception ex) {
            }
            try {
                if (isExistingUser != null)
                    isExistingUser.close();
            } catch (Exception ex) {
            }
            try {
                if (found != null)
                    found.close();
            } catch (Exception ex) {
            }
            if (_logger.isDebugEnabled())
                _logger.debug("writeFileToDatabase", " Exiting updateCount=" + msisdnAdded + ", p_invalidMsisdn=" + p_invalidMsisdn);
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
        if (_logger.isDebugEnabled())
            _logger.debug("moveFileToArchive", " Entered ");
        File fileRead = new File(p_fileName);
        File fileArchive = new File("" + Constants.getProperty("MNPArchiveFilePath"));
        if (!fileArchive.isDirectory())
            fileArchive.mkdirs();
        fileArchive = new File("" + Constants.getProperty("MNPArchiveFilePath") + p_file + "." + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()); // to
                                                                                                                                                           // make
                                                                                                                                                           // the
                                                                                                                                                           // new
                                                                                                                                                           // file
                                                                                                                                                           // name
        boolean flag = fileRead.renameTo(fileArchive);
        if (_logger.isDebugEnabled())
            _logger.debug("moveFileToArchive", " Exiting File Moved=" + flag);
        return flag;
    }// end of moveFileToArchive

    /**
     * Method to write the number in ported list in DB
     * 
     * @param p_con
     * @param p_numberPortVO
     * @return
     * @throws BTSLBaseException
     */
    public int writeMobileNumberToDatabase(Connection p_con, NumberPortVO p_numberPortVO) throws BTSLBaseException {
        if (_logger.isDebugEnabled())
            _logger.debug("writeMobileNumberToDatabase", " Entered p_filename:" + p_numberPortVO);
        PreparedStatement insertPstmt = null;
        PreparedStatement updatePstmt = null;
        int msisdnAdded = 0;

        String insertSql = "INSERT INTO ported_msisdn(msisdn,subscriber_type,port_type,created_by,created_on) VALUES(?,UPPER(?),UPPER(?),?,?) ";
        String updateSql = "UPDATE  ported_msisdn SET subscriber_type=UPPER(?), port_type=UPPER(?), created_by=?, created_on=? WHERE msisdn=? ";
        try {

            if (isExists(p_con, p_numberPortVO.getMsisdn(), null, null)) {
                updatePstmt = p_con.prepareStatement(updateSql);
                updatePstmt.setString(1, p_numberPortVO.getSubscriberType());
                updatePstmt.setString(2, p_numberPortVO.getPortType());
                updatePstmt.setString(3, p_numberPortVO.getCreatedBy());
                updatePstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_numberPortVO.getCreatedOn()));
                updatePstmt.setString(5, p_numberPortVO.getMsisdn());
                msisdnAdded = updatePstmt.executeUpdate();
            } else {
                insertPstmt = p_con.prepareStatement(insertSql);
                insertPstmt.setString(1, p_numberPortVO.getMsisdn());
                insertPstmt.setString(2, p_numberPortVO.getSubscriberType());
                insertPstmt.setString(3, p_numberPortVO.getPortType());
                insertPstmt.setString(4, p_numberPortVO.getCreatedBy());
                insertPstmt.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_numberPortVO.getCreatedOn()));
                msisdnAdded = insertPstmt.executeUpdate();
            }
        }// end of try
        catch (SQLException sqe) {
            _logger.error("writeMobileNumberToDatabase", "SQLException " + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeMobileNumberToDatabase]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "writeMobileNumberToDatabase", "error.general.sql.processing");
        } catch (Exception e) {
            _logger.error("writeMobileNumberToDatabase", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeMobileNumberToDatabase]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "writeMobileNumberToDatabase", "error.general.processing");
        } finally {
            try {
                if (insertPstmt != null)
                    insertPstmt.close();
            } catch (Exception ex) {
            }
            try {
                if (updatePstmt != null)
                    updatePstmt.close();
            } catch (Exception ex) {
            }
            if (_logger.isDebugEnabled())
                _logger.debug("writeMobileNumberToDatabase", " Exiting updateCount=" + msisdnAdded);
        }// end of finally
        return msisdnAdded;
    }

    /**
     * Check mobile number already exist in ported_msisdn table and returns true
     * if the record is found
     * 
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_msisdn
     *            String
     * @param p_userType
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isExists(Connection p_con, String p_msisdn, String p_userType, String p_portType) throws BTSLBaseException {

        if (_logger.isDebugEnabled())
            _logger.debug("isExists", "Entered p_msisdn=" + p_msisdn + ",p_userType=" + p_userType + ",p_barredType=" + p_portType);

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM ported_msisdn WHERE msisdn=? ");
        if (!BTSLUtil.isNullString(p_userType))
            sqlBuff.append("AND subscriber_type=? ");
        if (!BTSLUtil.isNullString(p_portType))
            sqlBuff.append("AND port_type=? ");
        String selectQuery = sqlBuff.toString();
        if (_logger.isDebugEnabled())
            _logger.debug("isExists", "Select Query::" + selectQuery);

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setString(i++, p_msisdn);
            if (!BTSLUtil.isNullString(p_userType))
                pstmtSelect.setString(i++, p_userType);
            if (!BTSLUtil.isNullString(p_portType))
                pstmtSelect.setString(i++, p_portType);
            rs = pstmtSelect.executeQuery();
            if (rs.next())
                found = true;
        } catch (SQLException sqle) {
            _logger.error("isExists", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[isExists]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isExists", "error.general.sql.processing");
        } catch (Exception e) {
            _logger.error("isExists", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[isExists]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isExists", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }
            if (_logger.isDebugEnabled())
                _logger.debug("isExists", "Exiting: isExists found=" + found);
        }
        return found;
    }

}
