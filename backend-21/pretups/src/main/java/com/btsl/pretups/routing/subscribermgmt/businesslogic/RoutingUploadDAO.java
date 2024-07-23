/**
 * @(#)RoutingUploadDAO.java
 *                           Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                           All Rights Reserved
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Siddhartha Srivastava March 8th,2007 Initial
 *                           Creation
 *                           --------------------------------------------------
 *                           ----------------------------------------------
 *                           This class is responsible for performing operation
 *                           related to data insertion for the validated data
 * 
 */

package com.btsl.pretups.routing.subscribermgmt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.RoutingFileProcessLog;
import com.btsl.util.BTSLUtil;

/**
 * @author simarnoor.bains
 *
 */
public class RoutingUploadDAO {
    private Log log = LogFactory.getLog(this.getClass().getName());

    /**
     * This method loads the external Id on the basis of passed interface id
     * 
     * @param pCon
     * @param pInterfaceID
     * @return
     * @throws BTSLBaseException
     */
    public String loadExternalID(Connection pCon, String pInterfaceID) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("loadExternalID", "Entered pInterfaceID:" + pInterfaceID);
        }

        final String methodName = "loadExternalID";
        String externalID = null;
        PreparedStatement pstmtSelectExtID = null;
        ResultSet rsSelectExtId = null;
        String selectExtId = "SELECT external_id FROM interfaces WHERE interface_id=?";
        try {
            if (log.isDebugEnabled()) {
                log.debug("loadExternalID", "Select Query= " + selectExtId);
            }

            pstmtSelectExtID = pCon.prepareStatement(selectExtId);
            pstmtSelectExtID.setString(1, pInterfaceID);
            rsSelectExtId = pstmtSelectExtID.executeQuery();
            if (rsSelectExtId.next()) {
                externalID = rsSelectExtId.getString("external_id");
            }

        }// end of try
        catch (SQLException sqe) {
            log.error("loadExternalID", "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingUploadDAO[loadExternalID]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadExternalID", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error("loadExternalID", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingUploadDAO[loadExternalID]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadExternalID", "error.general.processing");
        } finally {
        	try{
            	if (rsSelectExtId!= null){
            		rsSelectExtId.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing result set.", e);
            }
        	try{
        		if (pstmtSelectExtID!= null){
        			pstmtSelectExtID.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
            if (log.isDebugEnabled()) {
                log.debug("loadExternalID", "Exiting: externalID=" + externalID);
            }
        }// end of finally.
        return externalID;
    }

    /**
     * This method performs the actual task of inserting the validated data in
     * the passed list in database.
     * If any data already exists in the database then that data is simply
     * updated and the information is stored in
     * the log file.
     * 
     * @param pCon
     * @param p_headerMap
     * @param p_msisdnList
     * @param p_fileName
     * @return int
     * @throws BTSLBaseException
     */
    public int insertData(Connection pCon, HashMap p_headerMap, ArrayList p_msisdnList, String p_fileName) throws BTSLBaseException {
        final String methodName = "insertData";
        if (log.isDebugEnabled()) {
            log.debug("insertData", " Entered p_msisdnList:" + p_msisdnList + ",p_networkCode= " + p_headerMap.get("NETWORK_CODE") + ",p_interfaceCategory= " + p_headerMap.get("INTERFACE_CATEGORY") + ",pInterfaceID= " + p_headerMap.get("INTERFACE_ID") + ",p_externalID=" + p_headerMap.get("EXTERNAL_ID"));
        }
        int totRecords = 0;
        Date currentDate = new Date(System.currentTimeMillis());
        PreparedStatement insertPstmt = null;
        PreparedStatement pstmtUpdate = null;
        int msisdnAdded = 0;
        int msisdnUpdated = 0;
        int queryResult = -1;
        int i = 0;

        // getting the values of the headers from the passed Map
        String externalID = p_headerMap.get("EXTERNAL_ID").toString();
        String networkCode = p_headerMap.get("NETWORK_CODE").toString();
        String interfaceID = p_headerMap.get("INTERFACE_ID").toString();
        String interfaceCategory = p_headerMap.get("INTERFACE_CATEGORY").toString();

        // Query for Inserting
        StringBuilder insertSql = new StringBuilder("INSERT INTO subscriber_routing(msisdn, interface_id, ");
        insertSql.append("subscriber_type, external_interface_id, status, created_by, created_on, modified_by, ");
        insertSql.append("modified_on, text1, text2) VALUES (?,?,?,?,?,?,?,?,?,?,?) ");

        StringBuilder updateQuery = new StringBuilder();
        updateQuery.append("UPDATE subscriber_routing SET interface_id = ?, external_interface_id = ?, ");
        updateQuery.append("status = ?, modified_by = ?, modified_on = ?, text1 = ?, text2 = ? ");
        updateQuery.append("WHERE msisdn =? AND subscriber_type=? ");

        if (log.isDebugEnabled()) {
            log.debug("insertData", "Query=" + updateQuery);
        }

        try {
            insertPstmt = pCon.prepareStatement(insertSql.toString());
            pstmtUpdate = pCon.prepareStatement(updateQuery.toString());

            // here we try to insert the record in database
            // if record already exists in database then exception is thrown
            // the exception is caught in catch block and check if it is of
            // unique key constraint voilation
            // if exception is of unique key constraint violation then update
            // the record that exists in database with new values
            for (int size = 0, len = p_msisdnList.size(); size < len; size++) {
                String msisdn = p_msisdnList.get(size).toString();
                try {
                    // Set paremeters for insertion
                    i = 1;
                    insertPstmt.setString(i++, msisdn);
                    insertPstmt.setString(i++, interfaceID);
                    insertPstmt.setString(i++, interfaceCategory);
                    insertPstmt.setString(i++, externalID);
                    insertPstmt.setString(i++, PretupsI.STATUS_ACTIVE);
                    insertPstmt.setString(i++, PretupsI.SYSTEM_USER);
                    insertPstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                    insertPstmt.setString(i++, PretupsI.SYSTEM_USER);
                    insertPstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                    insertPstmt.setString(i++, null);
                    insertPstmt.setString(i++, null);
                    // Execute Query
                    // if sql exception occurs then it is caught in catch block
                    queryResult = insertPstmt.executeUpdate();
                    // if any error occurs in inserting the record then make an
                    // entry in logs for that msisdn.
                    if (queryResult <= 0) {
                        RoutingFileProcessLog.log("RoutingUpload", PretupsI.SYSTEM_USER, msisdn, size + 1, "Error while inserting record", "Fail", p_fileName + "," + networkCode);
                    } else {
                        msisdnAdded += queryResult;
                        totRecords++; // Total records count to be inserted or
                                      // updated in database
                        RoutingFileProcessLog.log("RoutingUpload", PretupsI.SYSTEM_USER, msisdn, size + 1, "Record inserted", "Pass", p_fileName + "," + networkCode);
                    }
                    // Clear Paremeters
                    insertPstmt.clearParameters();

                }// end of try
                catch (SQLException sqe) {
                    // check the error code of exception
                    if (sqe.getErrorCode() == 00001) {
                        // alse check the constraint for which the above error
                        // code is caught
                        if (sqe.getMessage().indexOf("PK_SUBSCRIBER_ROUTING") >= 0) {
                            if (log.isDebugEnabled()) {
                                log.debug("insertData", "MSISDN already Exist" + msisdn);
                            }
                            // make an entry in logs for updating the record
                            RoutingFileProcessLog.log("RoutingUpload", PretupsI.SYSTEM_USER, msisdn, size + 1, "MSISDN already Exist so updating record", "Pass", p_fileName + "," + networkCode);
                            i = 1;
                            pstmtUpdate.setString(i++, interfaceID);
                            pstmtUpdate.setString(i++, externalID);
                            pstmtUpdate.setString(i++, PretupsI.STATUS_ACTIVE);
                            pstmtUpdate.setString(i++, PretupsI.SYSTEM_USER);
                            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                            pstmtUpdate.setString(i++, null);
                            pstmtUpdate.setString(i++, null);
                            pstmtUpdate.setString(i++, msisdn);
                            pstmtUpdate.setString(i++, interfaceCategory);
                            // execute update query
                            queryResult = pstmtUpdate.executeUpdate();
                            // if error occured in updating then put an entry in
                            // logs corresponding to that msisdn
                            if (queryResult <= 0) {
                                RoutingFileProcessLog.log("RoutingUpload", PretupsI.SYSTEM_USER, msisdn, size + 1, "Error while updating record", "Fail", p_fileName + "," + networkCode);
                            } else {
                                msisdnUpdated += queryResult;
                                totRecords++;// total number of records entered
                                             // or updated in database
                                RoutingFileProcessLog.log("RoutingUpload", PretupsI.SYSTEM_USER, msisdn, size + 1, "Record updated", "Pass", p_fileName + "," + networkCode);
                            }
                            // clears the parameters of preparedstatement
                            pstmtUpdate.clearParameters();

                        }
                    }
                    else {
                        log.error("insertData", "SQLException= " + sqe.getMessage());
                        log.errorTrace(methodName, sqe);
                        RoutingFileProcessLog.log("RoutingUpload", PretupsI.SYSTEM_USER, msisdn, size + 1, "SQLException=" + sqe.getMessage(), "Exception", p_fileName + "," + networkCode);
                    }
                }// end of catch
            }
        }// end of try(just inside the while loop)
        catch (Exception e) {
            log.error("insertData", "Exception=" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[insertData]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "insertData", "error.general.processing");
        }// end of catch
        finally {
            if (log.isDebugEnabled()) {
                log.debug("insertData", "processed till record no:" + totRecords);
            }
            // Write in LOGS
            if (log.isDebugEnabled()) {
                log.debug("insertData", "p_userID:" + PretupsI.SYSTEM_USER + " p_networkCode:" + networkCode + " Processed=" + " ,No of records=" + totRecords);
            }
            RoutingFileProcessLog.log("insertData", PretupsI.SYSTEM_USER, null, totRecords, "Records uploaded to the database", "Finally Block", networkCode);
            try{
        		if (insertPstmt!= null){
        			insertPstmt.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
            try{
        		if (pstmtUpdate!= null){
        			pstmtUpdate.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
            if (log.isDebugEnabled()) {
                log.debug("insertData", " Exiting updateCount= " + msisdnAdded);
            }
        }// end of finally
         // adding up the updated and inserted record count and returning that
        if (msisdnUpdated > 0) {
            msisdnAdded += msisdnUpdated;
        }
        return msisdnAdded;
    }
}
