/*
 * @# RoutingDAO.java
 * This class is the controller class of the Channel user Module.
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Oct 27, 2005 Ankit Zindal Initial creation
 * Dec 19, 2006 Ankit Zindal Modified Change ID=ACCOUNTID
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.selftopup.pretups.routing.subscribermgmt.businesslogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.logging.RoutingFileProcessLog;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixCache;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixVO;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

/**
 * 
 */

public class RoutingDAO {
    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method to update the Routing
     * 
     * @param p_con
     * @param p_inMsisdn
     * @param p_subscriberType
     *            String
     * @param p_outMsisdn
     *            String[]
     * @param p_userID
     *            String
     * @return p_subscriberType
     * @throws BTSLBaseException
     */
    public int deleteMsisdn(Connection p_con, String[] p_inMsisdn, String p_subscriberType, String[] p_outMsisdn, String p_userID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("deleteMsisdn", "Entered p_inMsisdn:" + p_inMsisdn + "p_subscriberType" + p_subscriberType + ",p_userID = " + p_userID);
        PreparedStatement pstmt = null;
        int updateCount = 0;
        int i = 1;
        int outIndex = 0, index = 0;
        try {
            StringBuffer updateQueryBuff = new StringBuffer("DELETE FROM subscriber_routing ");
            updateQueryBuff.append("WHERE msisdn=? AND subscriber_type=?");
            String updateQuery = updateQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("deleteMsisdn", "updaet query:" + updateQuery);
            pstmt = p_con.prepareStatement(updateQuery);

            for (index = 0; index < p_inMsisdn.length; index++) {
                // check if the msisdn exists or not .
                // If not exists then insert that msisdn into p_outMsisdn.
                if (!BTSLUtil.isNullString(p_inMsisdn[index])) {
                    if (!this.isMsisdnExist(p_con, p_inMsisdn[index], p_subscriberType)) {
                        p_outMsisdn[outIndex++] = p_inMsisdn[index];
                        RoutingFileProcessLog.log("Delete MSISDN", p_userID, p_inMsisdn[index], index, "No Record Exist", "Fail", null);
                        continue;
                    }
                    pstmt.setString(i++, p_inMsisdn[index]);
                    pstmt.setString(i++, p_subscriberType);
                    updateCount = pstmt.executeUpdate();
                    if (updateCount > 0)
                        RoutingFileProcessLog.log("Delete MSISDN", p_userID, p_inMsisdn[index], index, "Record deleted successfully", "Pass", null);
                    else
                        RoutingFileProcessLog.log("Delete MSISDN", p_userID, p_inMsisdn[index], index, "Record can not be deleted", "Fail", null);

                    i = 1;
                    pstmt.clearParameters();
                }
            }// end of for
        }// end of try
        catch (SQLException sqle) {
            _log.error("deleteMsisdn", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[deleteMsisdn]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberControlDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("deleteMsisdn", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[deleteMsisdn]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteMsisdn", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteMsisdn", "Exiting updateCount:" + updateCount);
            RoutingFileProcessLog.log("Delete MSISDN", p_userID, null, 0, index - outIndex + " out of " + index + " records deleted successfully", null, "Inside finally block");
        }// end of finally
        return updateCount;
    }

    /**
     * Method for checking Is record exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_Msisdn
     *            String
     * @param p_subscriberType
     *            String
     * @return flag boolean
     * @throws BTSLBaseException
     */
    public boolean isMsisdnExist(Connection p_con, String p_Msisdn, String p_subscriberType) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isMsisdnExist", "Entered p_Msisdn:" + p_Msisdn + "p_subscriberType" + p_subscriberType);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        StringBuffer strBuff = new StringBuffer("SELECT msisdn ");
        strBuff.append(" FROM subscriber_routing  ");
        strBuff.append(" WHERE msisdn = ? AND subscriber_type=? AND status='Y'");
        try {
            if (_log.isDebugEnabled())
                _log.debug("isMsisdnExist", "Select Query= " + strBuff.toString());
            pstmt = p_con.prepareStatement(strBuff.toString());
            pstmt.setString(1, p_Msisdn);
            pstmt.setString(2, p_subscriberType);
            rs = pstmt.executeQuery();
            if (rs.next())
                existFlag = true;
            return existFlag;
        }// end of try
        catch (SQLException sqe) {
            _log.error("isMsisdnExist", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[isMsisdnExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isMsisdnExist", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("isMsisdnExist", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[isMsisdnExist]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "isMsisdnExist", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isMsisdnExist", "Exiting: existFlag=" + existFlag);
        }// end of finally.
    }

    /**
     * Method writeFileToDatabase.
     * 
     * @param p_con
     *            Connection
     * @param p_filename
     *            String
     * @param p_userID
     *            String
     * @param p_locationCode
     *            String
     * @param p_subsType
     *            String
     * @param p_interfaceCode
     *            String
     * @param p_file
     *            String
     * @param p_invalidMsisdn
     *            StringBuffer
     * @paramr String p_interfaceStr
     * @return int
     * @throws BTSLBaseException
     */
    public int writeFileToDatabase(Connection p_con, String p_filename, String p_userID, String p_locationCode, String p_subsType, String p_interfaceCode, String p_file, StringBuffer p_invalidMsisdn, String p_interfaceStr) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("writeFileToDatabase", " Entered p_filename:" + p_filename + ",p_interfaceCode=" + p_interfaceCode + ",p_subsType=" + p_subsType + "p_interfaceStr=" + p_interfaceStr);
        boolean processFile = false;
        FileReader fileReader = null; // file reader
        BufferedReader bufferReader = null;
        long recordsTotal = 0;
        int totRecords = 0;
        String delim = Constants.getProperty("DelimiterforuploadRouting");
        Date currentDate = new Date(System.currentTimeMillis());
        if (BTSLUtil.isNullString(delim))
            delim = " ";
        PreparedStatement insertPstmt = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtSelectExtID = null;
        ResultSet rsSelectExtId = null;
        String extId = null;// Defines the external id corresponding to the
                            // interface id --Added Ashish
        String interfaceID = null;// Defines the interface id,for which external
                                  // id to be fetched from the db---Added by
                                  // Ashish
        String tempStr = null;
        int msisdnAdded = 0;
        int msisdnUpdated = 0;
        int queryResult = -1;
        // Query for Inserting
        StringBuffer insertSql = new StringBuffer("INSERT INTO subscriber_routing(msisdn, interface_id, ");
        insertSql.append("subscriber_type, external_interface_id, status, created_by, created_on, modified_by, ");
        insertSql.append("modified_on, text1, text2) VALUES (?,?,?,?,?,?,?,?,?,?,?) ");
        StringBuffer updateQuery = new StringBuffer();
        updateQuery.append("UPDATE subscriber_routing SET interface_id = ?, external_interface_id = ?, ");
        updateQuery.append("status = ?, modified_by = ?, modified_on = ?, text1 = ?, text2 = ? ");
        updateQuery.append("WHERE msisdn =? AND subscriber_type=? ");

        // This query is used to get the external id based on the interface id
        // from interface table.
        String selectExtId = "SELECT external_id FROM interfaces WHERE interface_id=?";

        if (_log.isDebugEnabled())
            _log.debug("writeFileToDatabase", "Query=" + updateQuery);
        try {
            String labelName = null;
            String labelValue = null;
            String[] lineNumberArray = new String[2];
            boolean fileMoved = false;
            StringTokenizer startparser = null;
            insertPstmt = p_con.prepareStatement(insertSql.toString());
            pstmtUpdate = p_con.prepareStatement(updateQuery.toString());
            pstmtSelectExtID = p_con.prepareStatement(selectExtId);// Added by
                                                                   // ashish
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
                tempStr = bufferReader.readLine();
                // Read the First line must be IN Name= ????
                if (BTSLUtil.isNullString(tempStr))
                    throw new BTSLBaseException(this, "writeFileToDatabase", "routing.routinguploadfile.error.innamemissing");

                startparser = new StringTokenizer(tempStr, "=");
                if (_log.isDebugEnabled()) {
                    _log.debug("writeFileToDatabase", "Input=" + tempStr);
                    _log.debug("writeFileToDatabase", "There are " + startparser.countTokens() + "entries");
                }
                if (startparser.countTokens() != 2) {
                    throw new BTSLBaseException(this, "writeFileToDatabase", "routing.routinguploadfile.error.innamemissing");

                }
                if (startparser.countTokens() < 3) {
                    try {
                        // Added by Ashish to fetch the external id from the
                        // interface table.
                        interfaceID = p_interfaceStr.substring(p_interfaceStr.indexOf("|") + 1).trim();
                        if (_log.isDebugEnabled())
                            _log.debug("writeFileToDatabase", "interfaceID::" + interfaceID);
                        // Setting the interface ID to query.
                        pstmtSelectExtID.setString(1, interfaceID);
                        rsSelectExtId = pstmtSelectExtID.executeQuery();
                        if (rsSelectExtId.next()) {
                            extId = rsSelectExtId.getString("external_id");
                            if (_log.isDebugEnabled())
                                _log.debug("writeFileToDatabase", "extId::" + extId);
                        }
                        // Check whether the extID corresponding to interface is
                        // found,if not then show proper message and stop
                        // processing-Added by Ashish
                        if (BTSLUtil.isNullString(extId))
                            throw new BTSLBaseException(this, "writeFileToDatabase", "routing.routinguploadfile.error.noextidforinterface", 0, new String[] { interfaceID }, "uploadroutingfile");
                        while (startparser.hasMoreTokens()) {
                            labelName = startparser.nextToken().trim();
                            labelValue = startparser.nextToken().trim();
                            // Get this from constants props
                            if (!Constants.getProperty("routing.routinguploadfile.inname").equalsIgnoreCase(labelName)) {
                                // First line must be IN NAME
                                throw new BTSLBaseException(this, "writeFileToDatabase", "routing.routinguploadfile.error.innamefirstline");
                            }
                            if (BTSLUtil.isNullString(labelValue)) {
                                // First line must be IN name
                                throw new BTSLBaseException(this, "writeFileToDatabase", "routing.routinguploadfile.error.innamevaluefirstline");
                            } else if (labelValue.length() > 10) {
                                throw new BTSLBaseException(this, "writeFileToDatabase", "routing.routinguploadfile.error.innamevaluelength");
                            }
                            // Check whether the External id defined in the
                            // uploading file is exist or not, if not then
                            // return the error with proper message.--Added by
                            // Ashish
                            if (!extId.trim().equals(labelValue))
                                throw new BTSLBaseException(this, "writeFileToDatabase", "routing.routinguploadfile.error.extidnotexist", 0, new String[] { labelValue }, "uploadroutingfile");
                        }
                    } catch (NoSuchElementException e) {
                        lineNumberArray[0] = "1";
                        throw new BTSLBaseException(this, "writeFileToDatabase", "routing.routinguploadfile.error.notgetrecord", lineNumberArray);
                    }
                } else {
                    lineNumberArray[0] = "1";
                    throw new BTSLBaseException(this, "writeFileToDatabase", "routing.routinguploadfile.error.impropersyntax", lineNumberArray);
                }
                startparser = null;
                recordsTotal = 0;
                totRecords = 0;
                String filteredMsisdn;
                String msisdnPrefix;
                NetworkPrefixVO networkPrefixVO = null;
                String networkCode;
                String msisdn;
                int i = 1;
                // while(!BTSLUtil.isNullString(tempStr=bufferReader.readLine()))
                // // If Line is not Blank Process the Number
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
                        if (_log.isDebugEnabled()) {
                            _log.debug("writeFileToDatabase", "Input = " + tempStr);
                            _log.debug("writeFileToDatabase", "There are " + startparser.countTokens() + " entries");
                        }
                        msisdn = startparser.nextToken().trim();
                        // Change ID=ACCOUNTID
                        // FilteredMSISDN is replaced by
                        // getFilteredIdentificationNumber
                        // This is done because this field can contains msisdn
                        // or account id
                        filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(msisdn);
                        // Change ID=ACCOUNTID
                        // isValidMsisdn is replaced by
                        // isValidIdentificationNumber
                        // This is done because this field can contains msisdn
                        // or account id
                        if (!BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
                            if (_log.isDebugEnabled())
                                _log.debug("writeFileToDatabase", "Not a valid MSISDN" + msisdn);
                            RoutingFileProcessLog.log("File Upload", p_userID, msisdn, recordsTotal, "Not a valid MSISDN", "Fail", p_filename + "," + p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");
                            // throw new
                            // BTSLBaseException(this,"writeFileToDatabase","p2psubscriber.msg.notsupportnetwork","entermsisdn");
                            continue;
                        }
                        msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);

                        if (networkPrefixVO == null) {
                            if (_log.isDebugEnabled())
                                _log.debug("writeFileToDatabase", "Not supporting Network" + msisdn);
                            RoutingFileProcessLog.log("File Upload", p_userID, msisdn, recordsTotal, "Not supporting Network", "Fail", p_filename + "," + p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");

                            // throw new
                            // BTSLBaseException(this,"writeFileToDatabase","p2psubscriber.msg.notsupportnetwork","entermsisdn");
                            continue;
                        }
                        networkCode = networkPrefixVO.getNetworkCode();
                        if (!networkCode.equals(p_locationCode)) {
                            if (_log.isDebugEnabled())
                                _log.debug("writeFileToDatabase", "Not supporting Network" + msisdn);
                            RoutingFileProcessLog.log("File Upload", p_userID, msisdn, recordsTotal, "Not supporting Network", "Fail", p_filename + "," + p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");

                            // throw new
                            // BTSLBaseException(this,"showSubscriberDetail","p2psubscriber.msg.notsupportnetwork","entermsisdn");
                            continue;
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
                            // Set paremeters
                            i = 1;
                            insertPstmt.setString(i++, filteredMsisdn);
                            insertPstmt.setString(i++, p_interfaceCode);
                            insertPstmt.setString(i++, p_subsType);
                            insertPstmt.setString(i++, labelValue);
                            insertPstmt.setString(i++, PretupsI.STATUS_ACTIVE);
                            insertPstmt.setString(i++, p_userID);
                            insertPstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                            insertPstmt.setString(i++, p_userID);
                            insertPstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                            if (startparser.countTokens() >= 2) {
                                insertPstmt.setString(i++, startparser.nextToken());
                                insertPstmt.setString(i++, startparser.nextToken());
                            } else if (startparser.countTokens() == 1) {
                                insertPstmt.setString(i++, startparser.nextToken());
                                insertPstmt.setString(i++, null);
                            } else {
                                insertPstmt.setString(i++, null);
                                insertPstmt.setString(i++, null);
                            }

                            // Execute Query
                            // if sql exception occure then it is caught in
                            // catch block
                            queryResult = insertPstmt.executeUpdate();
                            // if any error occurs in inserting the record then
                            // make an entry in logs for that msisdn.
                            if (queryResult <= 0)
                                RoutingFileProcessLog.log("File Upload", p_userID, msisdn, recordsTotal, "Error while inserting record", "Fail", p_filename + "," + p_locationCode);
                            else {
                                msisdnAdded += queryResult;
                                totRecords++; // Total records count to be
                                              // inserted or updated in database
                                RoutingFileProcessLog.log("File Upload", p_userID, msisdn, recordsTotal, "Record inserted", "Pass", p_filename + "," + p_locationCode);
                            }
                            // Clear Paremeters
                            insertPstmt.clearParameters();

                        }// end of try
                        catch (SQLException sqe) {
                            // check teh error code of exception
                            if (sqe.getErrorCode() == 00001) {
                                // alse check the constraint for which the above
                                // error code is caught
                                if (sqe.getMessage().indexOf("PK_SUBSCRIBER_ROUTING") > 0) {
                                    if (_log.isDebugEnabled())
                                        _log.debug("writeFileToDatabase", "MSISDN already Exist" + msisdn);
                                    // make an entry in logs for updating the
                                    // record
                                    RoutingFileProcessLog.log("File Upload", p_userID, msisdn, recordsTotal, "MSISDN already Exist so updating record", "Pass", p_filename + "," + p_locationCode);
                                    i = 1;
                                    pstmtUpdate.setString(i++, p_interfaceCode);
                                    pstmtUpdate.setString(i++, labelValue);
                                    pstmtUpdate.setString(i++, PretupsI.STATUS_ACTIVE);
                                    pstmtUpdate.setString(i++, p_userID);
                                    pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                                    if (startparser.countTokens() >= 2) {
                                        pstmtUpdate.setString(i++, startparser.nextToken());
                                        pstmtUpdate.setString(i++, startparser.nextToken());
                                    } else if (startparser.countTokens() == 1) {
                                        pstmtUpdate.setString(i++, startparser.nextToken());
                                        pstmtUpdate.setString(i++, null);
                                    } else {
                                        pstmtUpdate.setString(i++, null);
                                        pstmtUpdate.setString(i++, null);
                                    }
                                    pstmtUpdate.setString(i++, filteredMsisdn);
                                    pstmtUpdate.setString(i++, p_subsType);
                                    // execute update query
                                    queryResult = pstmtUpdate.executeUpdate();
                                    // if error occured in updating then put an
                                    // entry in logs corresponding to that
                                    // msisdn
                                    if (queryResult <= 0)
                                        RoutingFileProcessLog.log("File Upload", p_userID, msisdn, recordsTotal, "Error while updating record", "Fail", p_filename + "," + p_locationCode);
                                    else {
                                        msisdnUpdated += queryResult;
                                        totRecords++;// total number of records
                                                     // entered or updated in
                                                     // database
                                        RoutingFileProcessLog.log("File Upload", p_userID, msisdn, recordsTotal, "Record updated", "Pass", p_filename + "," + p_locationCode);
                                    }
                                    // clears the parameters of
                                    // preparedstatement
                                    pstmtUpdate.clearParameters();

                                }// end of if(checking constraint name)
                            }// end of if(checking error code)
                            else {
                                _log.error("writeFileToDatabase", "SQLException=" + sqe.getMessage());
                                sqe.printStackTrace();
                                RoutingFileProcessLog.log("File Upload", p_userID, msisdn, recordsTotal, "SQLException=" + sqe.getMessage(), "Exception", p_filename + "," + p_locationCode);
                            }
                        }// end of catch
                    }// end of try(just inside the while loop)
                    catch (BTSLBaseException be) {
                        throw be;
                    } // end of catch
                    catch (Exception e) {
                        _log.error("writeFileToDatabase", "Exception=" + e.getMessage());
                        e.printStackTrace();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
                        throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.processing");
                    }// end of catch
                } // end of While
                if (_log.isDebugEnabled())
                    _log.debug("writeFileToDatabase", "Total lines in File " + recordsTotal + " And Total Records for process:" + totRecords);
            }// end of bufferedReader !=null
            if (recordsTotal == 0) {
                throw new BTSLBaseException(this, "writeFileToDatabase", "routing.routinguploadfile.error.zerofilesize");
            }
            if (totRecords == 0) {
                throw new BTSLBaseException(this, "writeFileToDatabase", "routing.routinguploadfile.error.norecords");
            }
            if (bufferReader != null)
                bufferReader.close();
            if (fileReader != null)
                fileReader.close();

            // Moving File after Processing
            if (msisdnAdded > 0 || msisdnUpdated > 0) {
                fileMoved = this.moveFileToArchive(p_filename, p_file);
                if (fileMoved)
                    processFile = true;
                else
                    throw new BTSLBaseException(this, "writeFileToDatabase", "routing.routinguploadfile.error.filenomove");
            }

        }// end of try(outer most try)
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error("writeFileToDatabase", "SQLException " + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[writeFileToDatabase]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("writeFileToDatabase", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "writeFileToDatabase", "error.general.processing");
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("writeFileToDatabase", "processed till record no:" + totRecords);
            // Write in LOGS
            if (!processFile)
                totRecords = 0;
            if (_log.isDebugEnabled())
                _log.debug("writeFileToDatabase", "p_userID:" + p_userID + " p_locationCode:" + p_locationCode + " Processed=" + p_file + " ,No of records=" + totRecords + " ,Status=" + processFile);
            RoutingFileProcessLog.log("File Upload", p_userID, null, totRecords, "Records uploaded to the database", "Finally Block", p_filename + "," + p_locationCode);
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
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception ex) {
            }
            try {
                if (rsSelectExtId != null)
                    rsSelectExtId.close();
            } catch (Exception exp) {
            }
            try {
                if (pstmtSelectExtID != null)
                    pstmtSelectExtID.close();
            } catch (Exception ex1) {
            }
            if (_log.isDebugEnabled())
                _log.debug("writeFileToDatabase", " Exiting updateCount=" + msisdnAdded + ", p_invalidMsisdn=" + p_invalidMsisdn);
        }// end of finally
         // adding up the updated and inserted record count and return that
        if (msisdnUpdated > 0)
            msisdnAdded += msisdnUpdated;
        return msisdnAdded;
    }

    /**
     * Method moveFileToArchive.
     * 
     * @param p_fileName
     *            String
     * @param p_file
     *            String
     * @return boolean
     */
    private boolean moveFileToArchive(String p_fileName, String p_file) {
        if (_log.isDebugEnabled())
            _log.debug("moveFileToArchive", " Entered ");
        File fileRead = new File(p_fileName);
        File fileArchive = new File("" + Constants.getProperty("RoutingArchiveFilePath"));
        if (!fileArchive.isDirectory())
            fileArchive.mkdirs();

        // fileArchive = new
        // File(""+Constants.getProperty("RoutingArchiveFilePath")+"/"+p_file);
        fileArchive = new File("" + Constants.getProperty("RoutingArchiveFilePath") + p_file + "." + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()); // to
                                                                                                                                                               // make
                                                                                                                                                               // the
                                                                                                                                                               // new
                                                                                                                                                               // file
                                                                                                                                                               // name
        boolean flag = fileRead.renameTo(fileArchive);
        if (_log.isDebugEnabled())
            _log.debug("moveFileToArchive", " Exiting File Moved=" + flag);
        return flag;
    }// end of moveFileToArchive

    /**
     * Method loadInterfaceID.
     * This method is to get the interface id on the basis of the subscriber
     * type and the msisdn.
     * 
     * @param p_con
     *            Connection
     * @param p_Msisdn
     *            String
     * @param p_subscriberType
     *            String
     * @return ListValueVO
     * @throws BTSLBaseException
     */
    public ListValueVO loadInterfaceID(Connection p_con, String p_Msisdn, String p_subscriberType) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadInterfaceID", "Entered p_Msisdn:" + p_Msisdn + "p_subscriberType" + p_subscriberType);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ListValueVO listValueVO = null;
        StringBuffer strBuff = new StringBuffer("SELECT SR.interface_id  ,I.external_id, I.status,I.message_language1,");
        strBuff.append(" I.message_language2, IT.handler_class,IT.underprocess_msg_reqd,SC.service_class_id,");
        strBuff.append(" I.status_type statustype, I.single_state_transaction ");
        strBuff.append(" FROM subscriber_routing SR,interfaces I,interface_types IT ,service_classes SC ");
        strBuff.append(" WHERE SR.msisdn = ? AND SR.subscriber_type=? AND SR.status='Y' AND I.status<>'N' ");
        strBuff.append(" AND I.interface_id=SR.interface_id AND I.interface_type_id=IT.interface_type_id ");
        strBuff.append(" AND I.interface_id=SC.interface_id(+) AND SC.service_class_code(+)=? AND SC.STATUS(+)<>'N' ");
        try {
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceID", "Select Query= " + strBuff.toString());
            pstmt = p_con.prepareStatement(strBuff.toString());
            pstmt.setString(1, p_Msisdn);
            pstmt.setString(2, p_subscriberType);
            pstmt.setString(3, PretupsI.ALL);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("handler_class"), rs.getString("interface_id"));
                listValueVO.setType(rs.getString("underprocess_msg_reqd"));
                listValueVO.setTypeName(rs.getString("service_class_id"));
                listValueVO.setIDValue(rs.getString("external_id"));
                listValueVO.setStatus(rs.getString("status"));
                listValueVO.setStatusType(rs.getString("statustype"));
                listValueVO.setOtherInfo(rs.getString("message_language1"));
                listValueVO.setOtherInfo2(rs.getString("message_language2"));
                listValueVO.setSingleStep(rs.getString("single_state_transaction"));
            }

        }// end of try
        catch (SQLException sqe) {
            _log.error("loadInterfaceID", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[loadInterfaceID]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceID", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadInterfaceID", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[loadInterfaceID]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceID", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceID", "Exiting: listValueVO=" + listValueVO);
        }// end of finally.
        return listValueVO;
    }

    /**
     * Method addSubscriberRoutingInfo.
     * This method is used to insert the routing information of subscriber
     * into the routing database
     * 
     * @param p_con
     *            Connection
     * @param p_routingVO
     *            RoutingVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addSubscriberRoutingInfo(Connection p_con, RoutingVO p_routingVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("addSubscriberRoutingInfo", "Entered:p_routingVO=" + p_routingVO);
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            StringBuffer insertQuery = new StringBuffer();
            insertQuery.append("INSERT INTO subscriber_routing (msisdn, interface_id, subscriber_type, ");
            insertQuery.append("external_interface_id, status, created_by, created_on, modified_by, modified_on, ");
            insertQuery.append("text1, text2)");
            insertQuery.append("VALUES(?,?,?,?,?,?,?,?,?,?,?)");
            if (_log.isDebugEnabled())
                _log.debug("addSubscriberRoutingInfo", "Query=" + insertQuery);
            pstmtInsert = p_con.prepareStatement(insertQuery.toString());
            int i = 1;
            pstmtInsert.setString(i++, p_routingVO.getMsisdn());
            pstmtInsert.setString(i++, p_routingVO.getInterfaceID());
            pstmtInsert.setString(i++, p_routingVO.getSubscriberType());
            pstmtInsert.setString(i++, p_routingVO.getExternalInterfaceID());
            pstmtInsert.setString(i++, p_routingVO.getStatus());
            pstmtInsert.setString(i++, p_routingVO.getCreatedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_routingVO.getCreatedOn()));
            pstmtInsert.setString(i++, p_routingVO.getModifiedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_routingVO.getModifiedOn()));
            pstmtInsert.setString(i++, p_routingVO.getText1());
            pstmtInsert.setString(i++, p_routingVO.getText2());
            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqe) {
            int errorCode = sqe.getErrorCode();
            _log.error("addSubscriberRoutingInfo", "SQLException:" + sqe.getMessage() + " errorCode: " + errorCode);
            // Ignore Eventhandling if SQL Error corresponds to Unique
            // Constraint voilation, because this can occur if sender sends
            // second request and first request is not still processed
            if (errorCode != 00001) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[addSubscriberRoutingInfo]", "", "", "", "SQL Exception:" + sqe.getMessage());
            }
            sqe.printStackTrace();
            throw new BTSLBaseException(this, "addSubscriberRoutingInfo", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("addSubscriberRoutingInfo", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[addSubscriberRoutingInfo]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addSubscriberRoutingInfo", "error.general.processing");
        } finally {
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("addSubscriberRoutingInfo", "Exiting:return=" + addCount);
        }
        return addCount;
    }

    /**
     * Method updateSubscriberRoutingInfo.
     * This method is used to update the routing information of subscriber
     * The updation is performed based on msisdn and subscriber type
     * 
     * @param p_con
     *            Connection
     * @param p_routingVO
     *            RoutingVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateSubscriberRoutingInfo(Connection p_con, RoutingVO p_routingVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateSubscriberRoutingInfo", "Entered:p_routingVO=" + p_routingVO);
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            StringBuffer updateQuery = new StringBuffer();
            updateQuery.append("UPDATE subscriber_routing SET interface_id = ?, external_interface_id = ?, ");
            updateQuery.append("status = ?, modified_by = ?, modified_on = ?, text1 = ?, text2 = ? ");
            updateQuery.append("WHERE msisdn =? AND subscriber_type=? ");
            if (_log.isDebugEnabled())
                _log.debug("updateSubscriberRoutingInfo", "Query=" + updateQuery);
            pstmtUpdate = p_con.prepareStatement(updateQuery.toString());
            int i = 1;
            pstmtUpdate.setString(i++, p_routingVO.getInterfaceID());
            pstmtUpdate.setString(i++, p_routingVO.getExternalInterfaceID());
            pstmtUpdate.setString(i++, p_routingVO.getStatus());
            pstmtUpdate.setString(i++, p_routingVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_routingVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_routingVO.getText1());
            pstmtUpdate.setString(i++, p_routingVO.getText2());
            pstmtUpdate.setString(i++, p_routingVO.getMsisdn());
            pstmtUpdate.setString(i++, p_routingVO.getSubscriberType());
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqe) {
            _log.error("updateSubscriberRoutingInfo", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[updateSubscriberRoutingInfo]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateSubscriberRoutingInfo", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("updateSubscriberRoutingInfo", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[updateSubscriberRoutingInfo]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateSubscriberRoutingInfo", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateSubscriberRoutingInfo", "Exiting:return=" + updateCount);
        }
        return updateCount;
    }

    /**
     * Method deleteSubscriberRoutingInfo.
     * This method is used to delete the routing information of subscriber from
     * the routing database
     * the deletion is performed based on msisdn and subscriber type
     * 
     * @param p_con
     *            Connection
     * @param p_routingVO
     *            RoutingVO
     * @return int
     * @throws BTSLBaseException
     */
    public int deleteSubscriberRoutingInfo(Connection p_con, RoutingVO p_routingVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("deleteSubscriberRoutingInfo", "Entered:p_routingVO=" + p_routingVO);
        PreparedStatement pstmtDelete = null;
        int deleteCount = 0;
        try {
            StringBuffer deleteQuery = new StringBuffer();
            deleteQuery.append("DELETE FROM subscriber_routing ");
            deleteQuery.append("WHERE msisdn =? AND subscriber_type=? ");
            if (_log.isDebugEnabled())
                _log.debug("deleteSubscriberRoutingInfo", "Query=" + deleteQuery);
            pstmtDelete = p_con.prepareStatement(deleteQuery.toString());
            int i = 1;
            pstmtDelete.setString(i++, p_routingVO.getMsisdn());
            pstmtDelete.setString(i++, p_routingVO.getSubscriberType());
            deleteCount = pstmtDelete.executeUpdate();
        } catch (SQLException sqe) {
            _log.error("deleteSubscriberRoutingInfo", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[deleteSubscriberRoutingInfo]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "deleteSubscriberRoutingInfo", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("deleteSubscriberRoutingInfo", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[deleteSubscriberRoutingInfo]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteSubscriberRoutingInfo", "error.general.processing");
        } finally {
            try {
                if (pstmtDelete != null)
                    pstmtDelete.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteSubscriberRoutingInfo", "Exiting:return=" + deleteCount);
        }
        return deleteCount;
    }

    /**
     * load the the subscriber control details according to msisdn.
     * method loadSubscriberRoutingList
     * 
     * @param p_con
     * @param p_msisdn
     * @return
     * @throws BTSLBaseException
     * @author ved.sharma
     */
    public ArrayList loadSubscriberRoutingList(Connection p_con, String p_msisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadSubscriberRoutingList", "Entered:p_msisdn=" + p_msisdn);
        PreparedStatement pstmtSelect = null;
        ArrayList list = new ArrayList();
        ResultSet rs = null;
        try {
            StringBuffer strBuff = new StringBuffer(250);
            strBuff.append("SELECT SR.msisdn, SR.interface_id, L.lookup_name subscriber_type, SR.external_interface_id,");
            strBuff.append("SR.status, U1.user_name created_by, SR.created_on, U2.user_name modified_by, SR.modified_on, text1, text2");
            strBuff.append(" FROM subscriber_routing sr, users u1, users u2, lookups L ");
            strBuff.append(" WHERE U1.user_id=SR.created_by AND U2.user_id=SR.modified_by ");
            strBuff.append(" AND L.lookup_code=SR.subscriber_type AND L.lookup_type=? AND SR.msisdn=?");
            if (_log.isDebugEnabled())
                _log.debug("loadSubscriberRoutingList", "Select Query= " + strBuff.toString());
            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            pstmtSelect.setString(1, PretupsI.SUBSRICBER_TYPE);
            pstmtSelect.setString(2, p_msisdn);
            rs = pstmtSelect.executeQuery();
            RoutingVO routingVO = null;
            while (rs.next()) {
                routingVO = new RoutingVO();
                routingVO.setMsisdn(rs.getString("msisdn"));
                routingVO.setInterfaceID(rs.getString("interface_id"));
                routingVO.setSubscriberType(rs.getString("subscriber_type"));
                routingVO.setExternalInterfaceID(rs.getString("external_interface_id"));
                routingVO.setStatus(rs.getString("status"));
                routingVO.setCreatedBy(rs.getString("created_by"));
                if (rs.getTimestamp("created_on") != null)
                    routingVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                routingVO.setModifiedBy(rs.getString("modified_by"));
                if (rs.getTimestamp("modified_on") != null)
                    routingVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                routingVO.setText1(rs.getString("text1"));
                routingVO.setText2(rs.getString("text2"));
                list.add(routingVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadSubscriberRoutingList", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[loadSubscriberRoutingList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberRoutingList", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadSubscriberRoutingList", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[loadSubscriberRoutingList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberRoutingList", "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadSubscriberRoutingList", "Exiting:return list=" + list.size());
        }
        return list;
    }

    /**
     * This method deletes MSISDN from the Whilelist
     * 
     * @param p_con
     * @param p_inMsisdn
     * @param p_subscriberType
     *            String
     * @param p_outMsisdn
     *            String[]
     * @param p_userID
     *            String
     * @return p_subscriberType
     * @throws BTSLBaseException
     * @author Shishupal
     */

    public int deleteMsisdnFromWhiteList(Connection p_con, String[] p_inMsisdn, String[] outMsisdn, String p_userID, String p_networkCode) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("deleteMsisdnFromWhiteList", "Entered p_inMsisdn:" + p_inMsisdn + ",p_userID = " + p_userID);
        PreparedStatement pstmtForDelete = null, pstmtForSelectQuery = null;
        ResultSet rs = null;
        int deleteCount = 0, deleteRows = 0;
        int index = 0, noOfMsisdn = 0;
        StringBuffer outMsisdnBuf = new StringBuffer();
        try {
            StringBuffer deleteQueryBuff = new StringBuffer("DELETE FROM white_list WHERE msisdn=? AND network_code=? ");
            if (_log.isDebugEnabled())
                _log.debug("deleteMsisdnFromWhiteList", "deleteQueryBuff query:" + deleteQueryBuff.toString());
            pstmtForDelete = p_con.prepareStatement(deleteQueryBuff.toString());

            StringBuffer strBuff = new StringBuffer("SELECT 1 FROM white_list WHERE msisdn = ? AND network_code=? AND status='Y'");
            if (_log.isDebugEnabled())
                _log.debug("deleteMsisdnFromWhiteList", "Select Query= " + strBuff.toString());
            pstmtForSelectQuery = p_con.prepareStatement(strBuff.toString());
            for (index = 0, noOfMsisdn = p_inMsisdn.length; index < noOfMsisdn; index++) {
                // check if the msisdn exists or not .
                // If not exists then insert that msisdn into p_outMsisdn.
                if (!BTSLUtil.isNullString(p_inMsisdn[index])) {
                    pstmtForSelectQuery.setString(1, p_inMsisdn[index]);
                    pstmtForSelectQuery.setString(2, p_networkCode);
                    rs = pstmtForSelectQuery.executeQuery();
                    pstmtForSelectQuery.clearParameters();
                    if (!rs.next()) {
                        outMsisdnBuf.append(p_inMsisdn[index]);
                        outMsisdnBuf.append(",");
                        RoutingFileProcessLog.log("BATCHDELETE", p_userID, p_inMsisdn[index], index, "No Record Exist", "Fail", "When subscriber type is POST then delete from white list");
                        continue;
                    }
                    pstmtForDelete.setString(1, p_inMsisdn[index]);
                    pstmtForDelete.setString(2, p_networkCode);
                    deleteCount = pstmtForDelete.executeUpdate();
                    if (deleteCount > 0) {
                        deleteRows += deleteCount;
                        RoutingFileProcessLog.log("BATCHDELETE", p_userID, p_inMsisdn[index], index, "Record deleted successfully", "Pass", "When subscriber type is POST then delete from white list");
                    } else {
                        outMsisdnBuf.append(p_inMsisdn[index]);
                        RoutingFileProcessLog.log("BATCHDELETE", p_userID, p_inMsisdn[index], index, "Record can not be deleted", "Fail", "When subscriber type is POST then delete from white list");
                    }
                    pstmtForDelete.clearParameters();
                }
            }// end of for
            outMsisdn[0] = outMsisdnBuf.toString();
            if (!BTSLUtil.isNullString(outMsisdn[0]))
                outMsisdn[0] = outMsisdn[0].substring(0, outMsisdn[0].length() - 1);
        }// end of try
        catch (SQLException sqle) {
            _log.error("deleteMsisdnFromWhiteList", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[deleteMsisdnFromWhiteList]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteMsisdnFromWhiteList", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("deleteMsisdnFromWhiteList", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[deleteMsisdnFromWhiteList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteMsisdnFromWhiteList", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtForSelectQuery != null)
                    pstmtForSelectQuery.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtForDelete != null)
                    pstmtForDelete.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteMsisdnFromWhiteList", "Exiting updateCount:" + deleteCount);
            RoutingFileProcessLog.log("BATCHDELETE", p_userID, null, 0, deleteRows + " out of " + noOfMsisdn + " records deleted successfully", null, "Inside finally block");
        }// end of finally
        return deleteRows;
    }

    /**
     * This methods deletes MSISDN batchwise from the WhiteList
     * 
     * @param p_con
     * @param p_inMsisdn
     * @param p_subscriberType
     *            String
     * @param p_outMsisdn
     *            String[]
     * @param p_userID
     *            String
     * @return p_subscriberType
     * @throws BTSLBaseException
     * @author Shishupal
     */

    public int deleteMsisdnBatch(Connection p_con, String[] p_inMsisdn, String p_subscriberType, String[] outMsisdn, String p_userID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("deleteMsisdnBatch", "Entered p_inMsisdn:" + p_inMsisdn + " outMsisdn " + outMsisdn + " p_subscriberType" + p_subscriberType + ",p_userID = " + p_userID);
        PreparedStatement pstmtForDelete = null, pstmtForSelectQuery = null;
        ResultSet rs = null;
        int deleteCount = 0, deleteRows = 0;
        int index = 0, len = 0;
        StringBuffer outMsisdnBuf = new StringBuffer();
        try {
            StringBuffer deleteQueryBuff = new StringBuffer("DELETE FROM subscriber_routing WHERE msisdn=? AND subscriber_type=? ");
            String deleteQuery = deleteQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("deleteMsisdnBatch", "Delete query:" + deleteQuery);
            pstmtForDelete = p_con.prepareStatement(deleteQuery);

            StringBuffer strBuff = new StringBuffer("SELECT 1 FROM subscriber_routing WHERE msisdn = ? AND subscriber_type=? AND status='Y' ");
            if (_log.isDebugEnabled())
                _log.debug("deleteMsisdnBatch", "Select Query= " + strBuff.toString());
            pstmtForSelectQuery = p_con.prepareStatement(strBuff.toString());

            for (index = 0, len = p_inMsisdn.length; index < len; index++) {

                if (!BTSLUtil.isNullString(p_inMsisdn[index])) {
                    pstmtForSelectQuery.setString(1, p_inMsisdn[index]);
                    pstmtForSelectQuery.setString(2, p_subscriberType);
                    rs = pstmtForSelectQuery.executeQuery();
                    pstmtForSelectQuery.clearParameters();
                    if (!rs.next()) {
                        outMsisdnBuf.append(p_inMsisdn[index]);
                        outMsisdnBuf.append(",");
                        RoutingFileProcessLog.log("BATCHDELETE", p_userID, p_inMsisdn[index], index, "No Record Exist", "Fail", null);
                        continue;
                    }
                    pstmtForDelete.setString(1, p_inMsisdn[index]);
                    pstmtForDelete.setString(2, p_subscriberType);
                    deleteCount = pstmtForDelete.executeUpdate();
                    if (deleteCount > 0) {
                        deleteRows += deleteCount;
                        RoutingFileProcessLog.log("BATCHDELETE", p_userID, p_inMsisdn[index], index, "Record deleted successfully", "Pass", null);
                    } else {
                        outMsisdnBuf.append(p_inMsisdn[index]);
                        RoutingFileProcessLog.log("BATCHDELETE", p_userID, p_inMsisdn[index], index, "Record can not be deleted", "Fail", null);
                    }
                    pstmtForDelete.clearParameters();
                }
            }// end of for
            outMsisdn[0] = outMsisdnBuf.toString();
            if (!BTSLUtil.isNullString(outMsisdn[0]))
                outMsisdn[0] = outMsisdn[0].substring(0, outMsisdn[0].length() - 1);
        }// end of try
        catch (SQLException sqle) {
            _log.error("deleteMsisdnBatch", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[deleteMsisdnBatch]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteMsisdnBatch", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("deleteMsisdnBatch", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[deleteMsisdnBatch]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteMsisdnBatch", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtForSelectQuery != null)
                    pstmtForSelectQuery.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtForDelete != null)
                    pstmtForDelete.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteMsisdnBatch", "Exiting updateCount:" + deleteCount);
            RoutingFileProcessLog.log("BATCHDELETE", p_userID, null, 0, deleteRows + " out of " + len + " records deleted successfully", null, "Inside finally block");
        }// end of finally
        return deleteRows;
    }

    /**
     * Date : Feb 26, 2007
     * Discription :This method is used to upload routing number.
     * Method : writeRoutingToDatabase
     * 
     * @param p_con
     * @param msisdnArray
     *            String[]
     * @param p_userID
     * @param p_locationCode
     * @param p_subsType
     * @param p_interfaceCode
     * @param p_invalidMsisdn
     * @param p_interfaceStr
     * @return msisdnAdded
     * @author Shishupal
     */
    public int writeRoutingToDatabase(Connection p_con, String[] p_msisdnArray, String p_userID, String p_locationCode, String p_subsType, String p_interfaceCode, StringBuffer p_invalidMsisdn, String p_interfaceStr) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("writeRoutingToDatabase", " Entered p_interfaceCode=" + p_interfaceCode + ",p_subsType=" + p_subsType + "p_interfaceStr=" + p_interfaceStr + " p_invalidMsisdn" + p_invalidMsisdn.toString() + " p_locationCode " + p_locationCode + " p_userID " + p_userID + " p_msisdnArray " + p_msisdnArray.toString());
        long recordsTotal = 0;
        int totRecords = 0;
        Date currentDate = new Date(System.currentTimeMillis());
        PreparedStatement insertPstmt = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtSelectExtID = null;
        ResultSet rs = null;
        int msisdnAdded = 0;
        int msisdnUpdated = 0;
        int queryResult = -1;
        // Query for Inserting
        StringBuffer insertSql = new StringBuffer("INSERT INTO subscriber_routing(msisdn, interface_id, ");
        insertSql.append("subscriber_type, external_interface_id, status, created_by, created_on, modified_by, ");
        insertSql.append("modified_on, text1, text2) VALUES (?,?,?,?,?,?,?,?,?,?,?) ");
        StringBuffer updateQuery = new StringBuffer();
        updateQuery.append("UPDATE subscriber_routing SET interface_id = ?, external_interface_id = ?, ");
        updateQuery.append("status = ?, modified_by = ?, modified_on = ?, text1 = ?, text2 = ? ");
        updateQuery.append("WHERE msisdn =? AND subscriber_type=? ");
        // This query is used to get the external id based on the interface id
        // from interface table.
        String selectExtId = "SELECT external_id FROM interfaces WHERE interface_id=? and status<>'N'";
        if (_log.isDebugEnabled())
            _log.debug("writeRoutingToDatabase", "Query=" + updateQuery);
        try {
            String labelValue = null;
            insertPstmt = p_con.prepareStatement(insertSql.toString());
            pstmtUpdate = p_con.prepareStatement(updateQuery.toString());
            pstmtSelectExtID = p_con.prepareStatement(selectExtId);// Added by
                                                                   // ashish
            // executing the query to get the external id
            pstmtSelectExtID.setString(1, p_interfaceCode);
            rs = pstmtSelectExtID.executeQuery();
            if (rs.next())
                labelValue = rs.getString("external_id");
            if (p_msisdnArray != null || p_msisdnArray.length != 0) // If File
                                                                    // Not Blank
                                                                    // Read line
                                                                    // by Line
            {
                recordsTotal = 0;
                totRecords = 0;
                String msisdnPrefix;
                NetworkPrefixVO networkPrefixVO = null;
                String networkCode;
                String msisdn;
                int i = 1;
                int len = p_msisdnArray.length;
                for (int j = 0; j < len; j++) {
                    msisdn = p_msisdnArray[j];
                    if (msisdn.trim().length() == 0)// To skip all the blank
                                                    // lines
                        continue;
                    recordsTotal++; // Keeps track of line number
                    try {
                        if (!BTSLUtil.isValidIdentificationNumber(msisdn)) {
                            if (_log.isDebugEnabled())
                                _log.debug("writeRoutingToDatabase", "Not a valid MSISDN " + msisdn);
                            RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, msisdn, recordsTotal, "Not a valid MSISDN ", " Fail ", p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");
                            continue;
                        }
                        msisdnPrefix = PretupsBL.getMSISDNPrefix(msisdn);
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);

                        if (networkPrefixVO == null) {
                            if (_log.isDebugEnabled())
                                _log.debug("writeRoutingToDatabase ", " Not supporting Network " + msisdn);
                            RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, msisdn, recordsTotal, "Not supporting Network", "Fail", p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");
                            continue;
                        }
                        networkCode = networkPrefixVO.getNetworkCode();
                        if (!networkCode.equals(p_locationCode)) {
                            if (_log.isDebugEnabled())
                                _log.debug("writeRoutingToDatabase", "Not supporting Network" + msisdn);
                            RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, msisdn, recordsTotal, "Not supporting Network", "Fail", p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");
                            continue;
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
                            // Set paremeters
                            i = 1;
                            insertPstmt.setString(i++, msisdn);
                            insertPstmt.setString(i++, p_interfaceCode);
                            insertPstmt.setString(i++, p_subsType);
                            insertPstmt.setString(i++, labelValue);
                            insertPstmt.setString(i++, PretupsI.STATUS_ACTIVE);
                            insertPstmt.setString(i++, p_userID);
                            insertPstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                            insertPstmt.setString(i++, p_userID);
                            insertPstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                            insertPstmt.setString(i++, null);
                            insertPstmt.setString(i++, null);

                            // Execute Query
                            // if sql exception occurs then it is caught in
                            // catch block
                            queryResult = insertPstmt.executeUpdate();
                            // if any error occurs in inserting the record then
                            // make an entry in logs for that msisdn.
                            if (queryResult <= 0)
                                RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, msisdn, recordsTotal, "Error while inserting record", "Fail", p_locationCode);
                            else {
                                msisdnAdded += queryResult;
                                totRecords++; // Total records count to be
                                              // inserted or updated in database
                                RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, msisdn, recordsTotal, "Record inserted", "Pass", p_locationCode);
                            }
                            // Clear Paremeters
                            insertPstmt.clearParameters();

                        }// end of try
                        catch (SQLException sqe) {
                            // check the error code of exception
                            if (sqe.getErrorCode() == 00001) {
                                // also check the constraint for which the above
                                // error code is caught
                                if (sqe.getMessage().indexOf("PK_SUBSCRIBER_ROUTING") > 0) {
                                    if (_log.isDebugEnabled())
                                        _log.debug("writeRoutingToDatabase", "MSISDN already Exist" + msisdn);
                                    // make an entry in logs for updating the
                                    // record
                                    RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, msisdn, recordsTotal, "MSISDN already Exist so updating record", "Pass", p_locationCode);
                                    i = 1;
                                    pstmtUpdate.setString(i++, p_interfaceCode);
                                    pstmtUpdate.setString(i++, labelValue);
                                    pstmtUpdate.setString(i++, PretupsI.STATUS_ACTIVE);
                                    pstmtUpdate.setString(i++, p_userID);
                                    pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                                    pstmtUpdate.setString(i++, null);
                                    pstmtUpdate.setString(i++, null);
                                    pstmtUpdate.setString(i++, msisdn);
                                    pstmtUpdate.setString(i++, p_subsType);
                                    // execute update query
                                    queryResult = pstmtUpdate.executeUpdate();
                                    // if error occured in updating then put an
                                    // entry in logs corresponding to that
                                    // msisdn
                                    if (queryResult <= 0)
                                        RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, msisdn, recordsTotal, "Error while updating record", "Fail", p_locationCode);
                                    else {
                                        msisdnUpdated += queryResult;
                                        totRecords++;// total number of records
                                                     // entered or updated in
                                                     // database
                                        RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, msisdn, recordsTotal, "Record updated", "Pass", p_locationCode);
                                    }
                                    // clears the parameters of
                                    // preparedstatement
                                    pstmtUpdate.clearParameters();

                                }// end of if(checking constraint name)
                            }// end of if(checking error code)
                            else {
                                _log.error("writeRoutingToDatabase", "SQLException=" + sqe.getMessage());
                                sqe.printStackTrace();
                                RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, msisdn, recordsTotal, "SQLException=" + sqe.getMessage(), "Exception", p_locationCode);
                            }
                        }// end of catch
                    }// end of try(just inside the while loop)
                    catch (Exception e) {
                        _log.error("writeRoutingToDatabase", "Exception=" + e.getMessage());
                        e.printStackTrace();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[writeRoutingToDatabase]", "", "", "", "Exception:" + e.getMessage());
                        throw new BTSLBaseException(this, "writeRoutingToDatabase", "error.general.processing");
                    }// end of catch
                } // end of While
                if (_log.isDebugEnabled())
                    _log.debug("writeRoutingToDatabase", "Total Records for process:" + totRecords);
            }// end of bufferedReader !=null
            if (recordsTotal == 0) {
                throw new BTSLBaseException(this, "writeRoutingToDatabase", "routing.routingupload.error.zeroroutingnumber");
            }
            if (totRecords == 0) {
                throw new BTSLBaseException(this, "writeRoutingToDatabase", "routing.routingupload.error.norecords");
            }

        }// end of try(outer most try)
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error("writeRoutingToDatabase", "SQLException " + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[writeRoutingToDatabase]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "writeRoutingToDatabase", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("writeRoutingToDatabase", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[writeRoutingToDatabase]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "writeRoutingToDatabase", "error.general.processing");
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("writeRoutingToDatabase", "p_userID:" + p_userID + " p_locationCode:" + p_locationCode + " ,No of records=" + totRecords);
            RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, null, totRecords, "Records uploaded to the database", "Finally Block", p_locationCode);
            // Destroying different objects
            try {
                if (insertPstmt != null)
                    insertPstmt.close();
            } catch (Exception ex) {
            }
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelectExtID != null)
                    pstmtSelectExtID.close();
            } catch (Exception ex1) {
            }
            if (_log.isDebugEnabled())
                _log.debug("writeRoutingToDatabase", " Exiting updateCount=" + msisdnAdded + ", p_invalidMsisdn=" + p_invalidMsisdn);
        }// end of finally
         // adding up the updated and inserted record count and return that
        if (msisdnUpdated > 0)
            msisdnAdded += msisdnUpdated;
        return msisdnAdded;
    }

    /**
     * Method for checking Is record exist or not.
     * 
     * @param p_con
     * @param p_msisdn
     * @param p_serviceType
     * @param p_serviceClassCode
     * @param p_status
     * @return flag boolean
     * @throws BTSLBaseException
     */
    public boolean isMsisdnServiceClassMapped(Connection p_con, String p_Msisdn, String p_serviceType, String p_serviceClassCode, String p_status, String p_module, String p_userType) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isMsisdnServiceClassMapped", "Entered p_Msisdn:" + p_Msisdn + " p_serviceType=" + p_serviceType + " p_serviceClassCode=" + p_serviceClassCode + " p_status=" + p_status + " p_module=" + p_module + " p_userType=" + p_userType);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        StringBuffer strBuff = new StringBuffer("SELECT 1 FROM srv_class_mapped_msisdn ");
        strBuff.append(" WHERE msisdn = ? AND service_type=? AND service_class_code=? and status=? and module=? and user_type=?");
        try {
            if (_log.isDebugEnabled())
                _log.debug("isMsisdnServiceClassMapped", "Select Query= " + strBuff.toString());
            pstmt = p_con.prepareStatement(strBuff.toString());
            int i = 0;
            pstmt.setString(++i, p_Msisdn);
            pstmt.setString(++i, p_serviceType);
            pstmt.setString(++i, p_serviceClassCode);
            pstmt.setString(++i, p_status);
            pstmt.setString(++i, p_module);
            pstmt.setString(++i, p_userType);
            rs = pstmt.executeQuery();
            if (rs.next())
                existFlag = true;
            return existFlag;
        }// end of try
        catch (SQLException sqe) {
            _log.error("isMsisdnServiceClassMapped", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[isMsisdnServiceClassMapped]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isMsisdnServiceClassMapped", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("isMsisdnServiceClassMapped", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[isMsisdnServiceClassMapped]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "isMsisdnServiceClassMapped", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isMsisdnServiceClassMapped", "Exiting: existFlag=" + existFlag);
        }// end of finally.
    }

    /**
     * Date : oct 11, 2007
     * Discription :This method is used to upload routing number.
     * Method : insertInServiceClassRouting
     * 
     * @param p_con
     * @param msisdnArray
     *            String[]
     * @param p_userID
     * @param p_locationCode
     * @param p_serviceType
     * @param p_serviceClass
     * @param p_status
     * @param p_module
     * @param p_userTypeDesc
     * @param p_interfaceStr
     * @return msisdnAdded
     * @author Vipul
     */
    public int insertInServiceClassRouting(Connection p_con, String[] p_msisdnArray, String p_userID, String p_locationCode, String p_serviceType, String p_serviceClass, String p_status, String p_module, String p_userType, String p_serviceTypeStr) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("insertInServiceClassRouting", " Entered p_serviceType=" + p_serviceType + ",p_serviceClassCode=" + p_serviceClass + " ,p_status =" + p_status + " ,p_module= " + p_module + ",p_userType= " + p_userType + ", p_serviceTypeStr=" + p_serviceTypeStr + " ,p_locationCode = " + p_locationCode + " ,p_userID= " + p_userID + " ,p_msisdnArray " + p_msisdnArray.toString());
        long recordsTotal = 0;
        int totRecords = 0;
        Date currentDate = new Date(System.currentTimeMillis());
        PreparedStatement insertPstmt = null;
        PreparedStatement updatePstmt = null;
        // ResultSet rs=null;
        int msisdnAdded = 0;
        int msisdnUpdated = 0;
        int queryResult = -1;
        String msisdn = null;
        // Query for Inserting
        StringBuffer insertSql = new StringBuffer("INSERT INTO srv_class_mapped_msisdn(msisdn, service_type, ");
        insertSql.append("service_class_code, status, created_on, created_by, modified_on, ");
        insertSql.append("modified_by, module, user_type) VALUES (?,?,?,?,?,?,?,?,?,?) ");
        if (_log.isDebugEnabled())
            _log.debug("insertInServiceClassRouting", "Query=" + insertSql.toString());

        StringBuffer updateQuery = new StringBuffer();
        updateQuery.append("UPDATE srv_class_mapped_msisdn SET status = ?, ");
        updateQuery.append("modified_on = ?, modified_by = ? ");
        updateQuery.append("WHERE msisdn =? AND service_type =? AND service_class_code =? AND module =? ");
        updateQuery.append("AND user_type=?");
        if (_log.isDebugEnabled())
            _log.debug("insertInServiceClassRouting", "Query=" + updateQuery.toString());
        try {
            insertPstmt = p_con.prepareStatement(insertSql.toString());
            updatePstmt = p_con.prepareStatement(updateQuery.toString());

            if (p_msisdnArray != null || p_msisdnArray.length != 0) // If File
                                                                    // Not Blank
                                                                    // Read line
                                                                    // by Line
            {
                recordsTotal = 0;
                totRecords = 0;
                int i = 1;
                int len = p_msisdnArray.length;
                for (int j = 0; j < len; j++) {
                    msisdn = p_msisdnArray[j];
                    if (msisdn.trim().length() == 0)// To skip all the blank
                                                    // lines
                        continue;
                    recordsTotal++; // Keeps track of line number
                    try {
                        // here we try to insert the record in database
                        // if record already exists in database then exception
                        // is thrown
                        // the exception is caught in catch block and check if
                        // it is of unique key constraint voilation
                        // if exception is of unique key constraint voilation
                        // then update the record that exists in database with
                        // new values
                        try {
                            // Set paremeters
                            i = 1;
                            insertPstmt.setString(i++, msisdn);
                            insertPstmt.setString(i++, p_serviceType);
                            insertPstmt.setString(i++, p_serviceClass);
                            insertPstmt.setString(i++, p_status);
                            insertPstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                            insertPstmt.setString(i++, p_userID);
                            insertPstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                            insertPstmt.setString(i++, p_userID);
                            insertPstmt.setString(i++, p_module);
                            insertPstmt.setString(i++, p_userType);

                            // Execute Query
                            // if sql exception occurs then it is caught in
                            // catch block
                            queryResult = insertPstmt.executeUpdate();
                            // if any error occurs in inserting the record then
                            // make an entry in logs for that msisdn.
                            if (queryResult <= 0)
                                RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClass, p_status, "Error while inserting record", "Fail", p_locationCode);
                            else {
                                msisdnAdded += queryResult;
                                totRecords++; // Total records count to be
                                              // inserted or updated in database
                                RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClass, p_status, "Record inserted", "Pass", p_locationCode);
                            }
                            // Clear Paremeters
                            insertPstmt.clearParameters();

                        }// end of try
                        catch (SQLException sqe) {
                            // check the error code of exception
                            if (sqe.getErrorCode() == 00001) {
                                // also check the constraint for which the above
                                // error code is caught
                                if (sqe.getMessage().indexOf("PK_SRV_MSISDN") > 0) {
                                    if (_log.isDebugEnabled())
                                        _log.debug("insertInServiceClassRouting", "MSISDN already Exist" + msisdn);
                                    // make an entry in logs for updating the
                                    // record
                                    RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClass, p_status, "MSISDN already Exist so updating record", "Pass", p_locationCode);
                                    i = 1;
                                    updatePstmt.setString(i++, p_status);
                                    updatePstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                                    updatePstmt.setString(i++, p_userID);
                                    updatePstmt.setString(i++, msisdn);
                                    updatePstmt.setString(i++, p_serviceType);
                                    updatePstmt.setString(i++, p_serviceClass);
                                    updatePstmt.setString(i++, p_module);
                                    updatePstmt.setString(i++, p_userType);

                                    // execute update query
                                    queryResult = updatePstmt.executeUpdate();
                                    // if error occured in updating then put an
                                    // entry in logs corresponding to that
                                    // msisdn
                                    if (queryResult <= 0)
                                        RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClass, p_status, "Error while updating record", "Fail", p_locationCode);
                                    else {
                                        msisdnUpdated += queryResult;
                                        totRecords++;// total number of records
                                                     // entered or updated in
                                                     // database
                                        RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClass, p_status, "Record updated", "Pass", p_locationCode);
                                    }
                                    // clears the parameters of
                                    // preparedstatement
                                    updatePstmt.clearParameters();

                                }// end of if(checking constraint name)
                            }// end of if(checking error code)
                            else {
                                _log.error("insertInServiceClassRouting", "SQLException=" + sqe.getMessage());
                                sqe.printStackTrace();
                                RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClass, p_status, "SQLException=" + sqe.getMessage(), "Exception", p_locationCode);
                            }
                        }// end of catch
                    }// end of try(just inside the while loop)
                    catch (Exception e) {
                        _log.error("insertInServiceClassRouting", "Exception=" + e.getMessage());
                        e.printStackTrace();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[insertInServiceClassRouting]", "", "", "", "Exception:" + e.getMessage());
                        throw new BTSLBaseException(this, "insertInServiceClassRouting", "error.general.processing");
                    }// end of catch
                } // end of While
                if (_log.isDebugEnabled())
                    _log.debug("insertInServiceClassRouting", "Total Records for process:" + totRecords);
            }// end of bufferedReader !=null
            if (recordsTotal == 0) {
                throw new BTSLBaseException(this, "insertInServiceClassRouting", "srvrouting.routingupload.error.zeroroutingnumber");
            }
            if (totRecords == 0) {
                throw new BTSLBaseException(this, "insertInServiceClassRouting", "srvrouting.routingupload.error.norecords");
            }

        }// end of try(outer most try)
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error("insertInServiceClassRouting", "SQLException " + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[insertInServiceClassRouting]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "insertInServiceClassRouting", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("insertInServiceClassRouting", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[insertInServiceClassRouting]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "insertInServiceClassRouting", "error.general.processing");
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("insertInServiceClassRouting", "p_userID:" + p_userID + " p_locationCode:" + p_locationCode + " ,No of records=" + totRecords);
            RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClass, p_status, "Records uploaded to the database", "Finally Block", p_locationCode);
            // Destroying different objects
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
            if (_log.isDebugEnabled())
                _log.debug("insertInServiceClassRouting", " Exiting updateCount=" + msisdnAdded);
        }// end of finally
         // adding up the updated and inserted record count and return that
        if (msisdnUpdated > 0)
            msisdnAdded += msisdnUpdated;
        return msisdnAdded;
    }

    /**
     * Method deletingMsisdn
     * This method is used to delete the numbers from the database.
     * 
     * @param p_con
     * @param p_inMsisdn
     * @param p_outMsisdn
     *            String[]
     * @param p_serviceType
     * @param p_serviceClassCode
     * @param p_status
     * @param p_module
     * @param p_userTypeDesc
     * @param p_userID
     *            String
     * @throws BTSLBaseException
     * @author Vipul
     */
    public int deletingMsisdn(Connection p_con, String[] p_inMsisdn, String[] p_outMsisdn, String p_serviceType, String p_serviceClassCode, String p_module, String p_userType, String p_status, String p_userID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("deletingMsisdn", "Entered p_inMsisdn:" + p_inMsisdn.toString() + "p_outMsisdn:" + p_outMsisdn.toString() + "p_serviceType" + p_serviceType + "p_serviceClassCode" + p_serviceClassCode + "p_module" + p_module + "p_userType" + p_userType + ",p_userID = " + p_userID);
        PreparedStatement delPstmt = null;
        int deleteCount = 0;
        int i = 1;
        int outIndex = 0, index = 0;
        try {
            StringBuffer deleteQueryBuff = new StringBuffer("DELETE FROM srv_class_mapped_msisdn ");
            deleteQueryBuff.append("WHERE msisdn=? AND service_type=? AND service_class_code=? AND module=? AND user_type=?");
            String deleteQuery = deleteQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("deletingMsisdn", "update query:" + deleteQuery);
            delPstmt = p_con.prepareStatement(deleteQuery);
            for (index = 0; index < p_inMsisdn.length; index++) {
                // check if the msisdn exists or not .
                // If not exists then insert that msisdn into p_outMsisdn.
                if (!BTSLUtil.isNullString(p_inMsisdn[index])) {
                    if (!this.isMsisdnExisting(p_con, p_inMsisdn[index], p_serviceType, p_serviceClassCode, p_module, p_userType, p_status)) {
                        p_outMsisdn[outIndex++] = p_inMsisdn[index];
                        RoutingFileProcessLog.log(p_module, p_inMsisdn[index], p_userType, p_serviceType, p_serviceClassCode, p_status, "No Record Exist", "Fail", null);
                        continue;
                    }
                    delPstmt.setString(i++, p_inMsisdn[index]);
                    delPstmt.setString(i++, p_serviceType);
                    delPstmt.setString(i++, p_serviceClassCode);
                    delPstmt.setString(i++, p_module);
                    delPstmt.setString(i++, p_userType);
                    deleteCount = delPstmt.executeUpdate();
                    if (deleteCount > 0)
                        RoutingFileProcessLog.log(p_module, p_inMsisdn[index], p_userType, p_serviceType, p_serviceClassCode, p_status, "Record deleted successfully", "Pass", null);
                    else
                        RoutingFileProcessLog.log(p_module, p_inMsisdn[index], p_userType, p_serviceType, p_serviceClassCode, p_status, "Record can not be deleted", "Fail", null);
                    i = 1;
                    delPstmt.clearParameters();
                }
            }// end of for
        }// end of try
        catch (SQLException sqle) {
            _log.error("deletingMsisdn", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[deletingMsisdn]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberControlDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("deletingMsisdn", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[deletingMsisdn]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deletingMsisdn", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (delPstmt != null)
                    delPstmt.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deletingMsisdn", "Exiting updateCount:" + deleteCount);
            RoutingFileProcessLog.log(p_module, null, p_userType, p_serviceType, p_serviceClassCode, p_status, index - outIndex + " out of " + index + " records deleted successfully", null, "Inside finally block");
        }// end of finally
        return deleteCount;
    }

    /**
     * Method for checking Is record exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_Msisdn
     *            String
     * @param p_serviceType
     * @param p_serviceClassCode
     * @param p_module
     * @param p_userTypeDesc
     * @throws BTSLBaseException
     * @author Vipul
     */
    public boolean isMsisdnExisting(Connection p_con, String p_Msisdn, String p_serviceType, String p_serviceClassCode, String p_module, String p_userTypeDesc, String p_status) throws BTSLBaseException {
        int i = 1;
        if (_log.isDebugEnabled())
            _log.debug("isMsisdnExist", "Entered p_Msisdn:" + p_Msisdn + ",p_serviceType" + p_serviceType + ",p_serviceClassCode" + p_serviceClassCode + ",p_module" + p_module + ",p_userTypeDesc" + p_userTypeDesc + ",p_status" + p_status);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        StringBuffer strBuff = new StringBuffer("SELECT 1");
        strBuff.append(" FROM srv_class_mapped_msisdn");
        strBuff.append(" WHERE msisdn=? AND service_type=? AND service_class_code=? AND module=? AND user_type=? AND status=?");
        try {
            if (_log.isDebugEnabled())
                _log.debug("isMsisdnExisting", "Select Query= " + strBuff.toString());
            pstmt = p_con.prepareStatement(strBuff.toString());
            pstmt.setString(i++, p_Msisdn);
            pstmt.setString(i++, p_serviceType);
            pstmt.setString(i++, p_serviceClassCode);
            pstmt.setString(i++, p_module);
            pstmt.setString(i++, p_userTypeDesc);
            pstmt.setString(i++, p_status);
            rs = pstmt.executeQuery();
            if (rs.next())
                existFlag = true;
            return existFlag;
        }// end of try
        catch (SQLException sqe) {
            _log.error("isMsisdnExisting", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[isMsisdnExisting]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isMsisdnExisting", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("isMsisdnExisting", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[isMsisdnExisting]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "isMsisdnExisting", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isMsisdnExisting", "Exiting: existFlag=" + existFlag);
        }// end of finally.
    }

    /**
     * Method writingFileToDatabase.
     * This methods add MSISDN batchwise for service class based routing.
     * 
     * @param p_con
     *            Connection
     * @param p_filename
     *            String
     * @param p_userID
     *            String
     * @param p_locationCode
     *            String
     * @param p_serviceType
     * @param p_serviceClassCode
     * @param p_status
     * @param p_module
     * @param p_userTypeDesc
     * @param p_file
     *            String
     * @param p_invalidMsisdn
     *            StringBuffer
     * @return int
     * @throws BTSLBaseException
     **/
    public int writingFileToDatabase(Connection p_con, String p_filename, String p_userID, String p_locationCode, String p_serviceType, String p_serviceClassCode, String p_status, String p_module, String p_userType, String p_file, StringBuffer p_invalidMsisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("writingFileToDatabase", " Entered p_filename:" + p_filename + ",p_serviceType=" + p_serviceType + ",p_serviceClassCode=" + p_serviceClassCode + " ,p_status =" + p_status + " ,p_module= " + p_module + ",p_userType= " + p_userType + ", p_invalidMsisdn=" + p_invalidMsisdn.toString() + " p_userID= " + p_userID + " p_locationCode= " + p_locationCode);
        boolean processFile = false;
        FileReader fileReader = null; // file reader
        BufferedReader bufferReader = null;
        long recordsTotal = 0;
        int totRecords = 0;
        String delim = null;
        Date currentDate = null;
        PreparedStatement insertPstmt = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtSelectExtID = null;
        ResultSet rsSelectExtId = null;
        String tempStr = null;
        boolean fileMoved = false;

        int msisdnAdded = 0;
        int msisdnUpdated = 0;
        int queryResult = -1;
        String filteredMsisdn;
        String msisdnPrefix;
        NetworkPrefixVO networkPrefixVO = null;
        String networkCode;
        String msisdn;

        // Query for Inserting
        StringBuffer insertSql = new StringBuffer("INSERT INTO srv_class_mapped_msisdn(msisdn, service_type, ");
        insertSql.append("service_class_code, status, created_on, created_by, modified_on, ");
        insertSql.append("modified_by, module, user_type) VALUES (?,?,?,?,?,?,?,?,?,?) ");
        if (_log.isDebugEnabled())
            _log.debug("writingFileToDatabase", "Query=" + insertSql.toString());
        StringBuffer updateQuery = new StringBuffer();
        updateQuery.append("UPDATE srv_class_mapped_msisdn SET status = ?, ");
        updateQuery.append("modified_on = ?, modified_by = ? ");
        updateQuery.append("WHERE msisdn =? AND service_type =? AND service_class_code =? AND module =? ");
        updateQuery.append("AND user_type=?");
        if (_log.isDebugEnabled())
            _log.debug("writingFileToDatabase", "Query=" + updateQuery.toString());
        try {
            delim = Constants.getProperty("DelimiterforuploadRouting");
            if (BTSLUtil.isNullString(delim))
                delim = " ";
            currentDate = new Date(System.currentTimeMillis());
            insertPstmt = p_con.prepareStatement(insertSql.toString());
            pstmtUpdate = p_con.prepareStatement(updateQuery.toString());
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
                int j = 0, i = 0;
                int length = 0;
                while ((tempStr = bufferReader.readLine()) != null) // Read the
                                                                    // file till
                                                                    // the end
                                                                    // of file.
                {
                    String msisdnArray[] = tempStr.split(delim);
                    for (j = 0, length = msisdnArray.length; j < length; j++) {
                        msisdn = msisdnArray[j];
                        if (msisdn.trim().length() == 0)// To skip all the blank
                                                        // lines
                            continue;
                        recordsTotal++; // Keeps track of line number
                        try {
                            // FilteredMSISDN is replaced by
                            // getFilteredIdentificationNumber
                            // This is done because this field can contains
                            // msisdn or account id
                            filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(msisdn);
                            // isValidMsisdn is replaced by
                            // isValidIdentificationNumber
                            // This is done because this field can contains
                            // msisdn or account id
                            if (!BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
                                if (_log.isDebugEnabled())
                                    _log.debug("writingFileToDatabase", "Not a valid MSISDN" + msisdn);
                                RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status, "Not a valid MSISDN", "Fail", p_locationCode);
                                p_invalidMsisdn.append(msisdn);
                                p_invalidMsisdn.append(",");
                                continue;
                            }
                            msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                            if (networkPrefixVO == null) {
                                if (_log.isDebugEnabled())
                                    _log.debug("writingFileToDatabase", "Not supporting Network" + msisdn);
                                RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status, "Not supporting Network", "Fail", p_locationCode);

                                p_invalidMsisdn.append(msisdn);
                                p_invalidMsisdn.append(",");
                                continue;
                            }
                            networkCode = networkPrefixVO.getNetworkCode();
                            if (!networkCode.equals(p_locationCode)) {
                                if (_log.isDebugEnabled())
                                    _log.debug("writingFileToDatabase", "Not supporting Network" + msisdn);
                                RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status, "Not supporting Network", "Fail", p_locationCode);
                                p_invalidMsisdn.append(msisdn);
                                p_invalidMsisdn.append(",");
                                continue;
                            }
                            // here we try to insert the record in database
                            // if record already exists in database then
                            // exception is thrown
                            // the exception is caught in catch block and check
                            // if it is of unique key constraint voilation
                            // if exception is of unique key constraint
                            // voilation then update the record that exists in
                            // database with new values
                            try {
                                // Set paremeters
                                i = 1;
                                insertPstmt.setString(i++, filteredMsisdn);
                                insertPstmt.setString(i++, p_serviceType);
                                insertPstmt.setString(i++, p_serviceClassCode);
                                insertPstmt.setString(i++, p_status);
                                insertPstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                                insertPstmt.setString(i++, p_userID);
                                insertPstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                                insertPstmt.setString(i++, p_userID);
                                insertPstmt.setString(i++, p_module);
                                insertPstmt.setString(i++, p_userType);
                                // Execute Query
                                // if sql exception occur then it is caught in
                                // catch block
                                queryResult = insertPstmt.executeUpdate();
                                // if any error occurs in inserting the record
                                // then make an entry in logs for that msisdn.
                                if (queryResult <= 0)
                                    RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status, "Error while inserting record", "Fail", p_locationCode);
                                else {
                                    msisdnAdded += queryResult;
                                    totRecords++; // Total records count to be
                                                  // inserted or updated in
                                                  // database
                                    RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status, "Record inserted", "Pass", p_locationCode);
                                }
                                // Clear Paremeters
                                insertPstmt.clearParameters();
                            }// end of try
                            catch (SQLException sqe) {
                                // check teh error code of exception
                                if (sqe.getErrorCode() == 00001) {
                                    // alse check the constraint for which the
                                    // above error code is caught
                                    if (sqe.getMessage().indexOf("PK_SRV_MSISDN") > 0) {
                                        if (_log.isDebugEnabled())
                                            _log.debug("writingFileToDatabase", "MSISDN already Exist" + msisdn);
                                        // make an entry in logs for updating
                                        // the record
                                        RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status, "MSISDN already Exist so updating record", "Pass", p_locationCode);
                                        i = 1;
                                        pstmtUpdate.setString(i++, p_status);
                                        pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                                        pstmtUpdate.setString(i++, p_userID);
                                        pstmtUpdate.setString(i++, filteredMsisdn);
                                        pstmtUpdate.setString(i++, p_serviceType);
                                        pstmtUpdate.setString(i++, p_serviceClassCode);
                                        pstmtUpdate.setString(i++, p_module);
                                        pstmtUpdate.setString(i++, p_userType);

                                        // execute update query
                                        queryResult = pstmtUpdate.executeUpdate();
                                        // if error occured in updating then put
                                        // an entry in logs corresponding to
                                        // that msisdn
                                        if (queryResult <= 0)
                                            RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status, "Error while updating record", "Fail", p_locationCode);

                                        else {
                                            msisdnUpdated += queryResult;
                                            totRecords++;// total number of
                                                         // records entered or
                                                         // updated in database
                                            RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status, "Record updated", "Pass", p_locationCode);
                                        }

                                        // clears the parameters of
                                        // preparedstatement
                                        pstmtUpdate.clearParameters();

                                    }// end of if(checking constraint name)
                                }// end of if(checking error code)
                                else {
                                    _log.error("writingFileToDatabase", "SQLException=" + sqe.getMessage());
                                    sqe.printStackTrace();
                                    RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status, "SQLException=" + sqe.getMessage(), "Exception", p_locationCode);
                                }
                            }// end of catch
                        }// end of try(just inside the while loop)
                        catch (BTSLBaseException be) {
                            throw be;
                        } // end of catch
                        catch (Exception e) {
                            _log.error("writingFileToDatabase", "Exception=" + e.getMessage());
                            e.printStackTrace();
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[writingFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
                            throw new BTSLBaseException(this, "writingFileToDatabase", "error.general.processing");
                        }// end of catch
                    }
                }// end of While
                if (_log.isDebugEnabled())
                    _log.debug("writingFileToDatabase", "Total lines in File " + recordsTotal + " And Total Records for process:" + totRecords);
            }// end of bufferedReader !=null
            if (recordsTotal == 0) {
                throw new BTSLBaseException(this, "writingFileToDatabase", "srvrouting.routinguploadfile.error.zerofilesize");
            }
            if (totRecords == 0) {
                throw new BTSLBaseException(this, "writingFileToDatabase", "srvrouting.routinguploadfile.error.norecords");
            }
            if (bufferReader != null)
                bufferReader.close();
            if (fileReader != null)
                fileReader.close();
            // Moving File after Processing
            if (msisdnAdded > 0 || msisdnUpdated > 0) {
                fileMoved = this.moveFileToArchive(p_filename, p_file);
                if (fileMoved)
                    processFile = true;
                else
                    throw new BTSLBaseException(this, "writingFileToDatabase", "srvrouting.routinguploadfile.error.filenomove");
            }
        }// end of try(outer most try)
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error("writingFileToDatabase", "SQLException " + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[writingFileToDatabase]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "writingFileToDatabase", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("writingFileToDatabase", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[writingFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "writingFileToDatabase", "error.general.processing");
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("writingFileToDatabase", "processed till record no:" + totRecords);
            // Write in LOGS
            if (!processFile)
                totRecords = 0;
            if (_log.isDebugEnabled())
                _log.debug("writingFileToDatabase", "p_userID:" + p_userID + " p_locationCode:" + p_locationCode + " Processed=" + p_file + " ,No of records=" + totRecords + " ,Status=" + processFile);
            RoutingFileProcessLog.log(p_module, null, p_userType, p_serviceType, p_serviceClassCode, p_status, "Records uploaded to the database", "Finally Block", p_locationCode);
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
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception ex) {
            }
            try {
                if (rsSelectExtId != null)
                    rsSelectExtId.close();
            } catch (Exception exp) {
            }
            try {
                if (pstmtSelectExtID != null)
                    pstmtSelectExtID.close();
            } catch (Exception ex1) {
            }
            if (_log.isDebugEnabled())
                _log.debug("writingFileToDatabase", " Exiting updateCount=" + msisdnAdded + ", p_invalidMsisdn=" + p_invalidMsisdn);
        }// end of finally
         // adding up the updated and inserted record count and return that
        if (msisdnUpdated > 0)
            msisdnAdded += msisdnUpdated;
        return msisdnAdded;
    }

    /**
     * Method deletingMsisdnBatch
     * This methods deletes MSISDN batchwise
     * 
     * @param p_con
     * @param p_inMsisdn
     * @param p_outMsisdn
     *            String[]
     * @param p_serviceType
     * @param p_serviceClassCode
     * @param p_status
     * @param p_module
     * @param p_userTypeDesc
     * @param p_userID
     *            String
     * @throws BTSLBaseException
     * @author Vipul
     */
    public int deletingMsisdnBatch(Connection p_con, String[] p_inMsisdn, String[] p_outMsisdn, String p_serviceType, String p_serviceClassCode, String p_module, String p_userType, String p_status, String p_userID) {
        if (_log.isDebugEnabled())
            _log.debug("deletingMsisdnBatch", "Entered p_inMsisdn:" + p_inMsisdn.toString() + "outMsisdn" + p_outMsisdn.toString() + "p_serviceType" + p_serviceType + "p_serviceClassCode" + p_serviceClassCode + "p_module" + p_module + "p_userType" + p_userType + ",p_userID = " + p_userID + ",p_status = " + p_status);
        PreparedStatement pstmtForDelete = null, pstmtForSelectQuery = null;
        ResultSet rs = null;
        int deleteCount = 0, deleteRows = 0;
        int index = 0, len = 0;
        StringBuffer outMsisdnBuf = null;
        try {
            StringBuffer deleteQueryBuff = new StringBuffer("DELETE FROM srv_class_mapped_msisdn ");
            deleteQueryBuff.append("WHERE msisdn=? AND service_type=? AND service_class_code=? AND module=? AND user_type=?");
            String deleteQuery = deleteQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("deletingMsisdnBatch", "Delete query:" + deleteQuery);
            pstmtForDelete = p_con.prepareStatement(deleteQuery);
            StringBuffer strBuff = new StringBuffer("SELECT 1 FROM srv_class_mapped_msisdn");
            strBuff.append(" WHERE msisdn=? AND service_type=? AND service_class_code=? AND module=? AND user_type=? AND status=?");
            if (_log.isDebugEnabled())
                _log.debug("deletingMsisdnBatch", "Select Query= " + strBuff.toString());
            pstmtForSelectQuery = p_con.prepareStatement(strBuff.toString());
            for (index = 0, len = p_inMsisdn.length; index < len; index++) {
                if (!BTSLUtil.isNullString(p_inMsisdn[index])) {
                    pstmtForSelectQuery.setString(1, p_inMsisdn[index]);
                    pstmtForSelectQuery.setString(2, p_serviceType);
                    pstmtForSelectQuery.setString(3, p_serviceClassCode);
                    pstmtForSelectQuery.setString(4, p_module);
                    pstmtForSelectQuery.setString(5, p_userType);
                    pstmtForSelectQuery.setString(6, p_status);
                    rs = pstmtForSelectQuery.executeQuery();
                    pstmtForSelectQuery.clearParameters();
                    if (!rs.next()) {
                        outMsisdnBuf = new StringBuffer();
                        outMsisdnBuf.append(p_inMsisdn[index]);
                        outMsisdnBuf.append(",");
                        RoutingFileProcessLog.log(p_module, p_inMsisdn[index], p_userType, p_serviceType, p_serviceClassCode, p_status, "No Record Exist", "Fail", null);
                        continue;
                    }
                    pstmtForDelete.setString(1, p_inMsisdn[index]);
                    pstmtForDelete.setString(2, p_serviceType);
                    pstmtForDelete.setString(3, p_serviceClassCode);
                    pstmtForDelete.setString(4, p_module);
                    pstmtForDelete.setString(5, p_userType);
                    deleteCount = pstmtForDelete.executeUpdate();
                    if (deleteCount > 0) {
                        deleteRows += deleteCount;
                        RoutingFileProcessLog.log(p_module, p_inMsisdn[index], p_userType, p_serviceType, p_serviceClassCode, p_status, "Record deleted successfully", "Pass", null);
                    } else {
                        outMsisdnBuf.append(p_inMsisdn[index]);
                        RoutingFileProcessLog.log(p_module, p_inMsisdn[index], p_userType, p_serviceType, p_serviceClassCode, p_status, "Record can not be deleted", "Fail", null);
                    }
                    pstmtForDelete.clearParameters();
                }
            }// end of for
            p_outMsisdn[0] = outMsisdnBuf.toString();
            if (!BTSLUtil.isNullString(p_outMsisdn[0]))
                p_outMsisdn[0] = p_outMsisdn[0].substring(0, p_outMsisdn[0].length() - 1);
        }// end of try
        catch (SQLException sqle) {
            _log.error("deletingMsisdnBatch", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[deletingMsisdnBatch]", "", "", "", "Exception:" + sqle.getMessage());
            try {
                throw new BTSLBaseException(this, "deletingMsisdnBatch", "error.general.sql.processing");
            } catch (BTSLBaseException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }// end of catch
        catch (Exception e) {
            _log.error("deletingMsisdnBatch", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[deleteMsisdnBatch]", "", "", "", "Exception:" + e.getMessage());
            try {
                throw new BTSLBaseException(this, "deletingMsisdnBatch", "error.general.processing");
            } catch (BTSLBaseException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtForSelectQuery != null)
                    pstmtForSelectQuery.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtForDelete != null)
                    pstmtForDelete.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deletingMsisdnBatch", "Exiting updateCount:" + deleteCount);
            RoutingFileProcessLog.log(p_module, null, p_userType, p_serviceType, p_serviceClassCode, p_status, deleteRows + " out of " + len + " records deleted successfully", null, "Inside finally block");
        }// end of finally
        return deleteRows;
    }

    /**
     * Method updateSubscriberRoutingInfo.
     * This method is used to update the routing information of subscriber
     * The updation is performed based on msisdn and subscriber type
     * 
     * @param p_con
     *            Connection
     * @param p_routingVO
     *            RoutingVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateSubscriberAilternateRoutingInfo(Connection p_con, RoutingVO p_routingVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateSubscriberRoutingInfo", "Entered:p_routingVO=" + p_routingVO);
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            StringBuffer updateQuery = new StringBuffer();
            updateQuery.append("UPDATE subscriber_routing SET interface_id = ?, external_interface_id = ?, ");
            updateQuery.append("status = ?, modified_by = ?, modified_on = ?, text1 = ?, text2 = ? ,subscriber_type=? ");
            updateQuery.append("WHERE msisdn =? ");
            if (_log.isDebugEnabled())
                _log.debug("updateSubscriberRoutingInfo", "Query=" + updateQuery);
            pstmtUpdate = p_con.prepareStatement(updateQuery.toString());
            int i = 1;
            pstmtUpdate.setString(i++, p_routingVO.getInterfaceID());
            pstmtUpdate.setString(i++, p_routingVO.getExternalInterfaceID());
            pstmtUpdate.setString(i++, p_routingVO.getStatus());
            pstmtUpdate.setString(i++, p_routingVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_routingVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_routingVO.getText1());
            pstmtUpdate.setString(i++, p_routingVO.getText2());
            pstmtUpdate.setString(i++, p_routingVO.getSubscriberType());
            pstmtUpdate.setString(i++, p_routingVO.getMsisdn());
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqe) {
            _log.error("updateSubscriberAilternateRoutingInfo", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[updateSubscriberAilternateRoutingInfo]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateSubscriberAilternateRoutingInfo", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("updateSubscriberAilternateRoutingInfo", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[updateSubscriberAilternateRoutingInfo]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateSubscriberRoutingInfo", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateSubscriberAilternateRoutingInfo", "Exiting:return=" + updateCount);
        }
        return updateCount;
    }

}
