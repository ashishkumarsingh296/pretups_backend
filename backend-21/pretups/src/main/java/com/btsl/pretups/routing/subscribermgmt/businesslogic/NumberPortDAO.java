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
package com.btsl.pretups.routing.subscribermgmt.businesslogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.MNPPocessingLog;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * 
 *
 */
public class NumberPortDAO {

    /**
     * Field logger.
     */
    private static final  Log logger = LogFactory.getLog(NumberPortDAO.class.getName());

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
     * @param pCon
     *            java.sql.Connection
     * @param pFilename
     *            java.lang.String
     * @param pCreatedBy
     *            java.lang.String
     * @param pLocationCode
     *            String
     * @param pFile
     *            String
     * @exception BTSLBaseException
     */

    /**
     * @param pCon
     * @param pFilename
     * @param pCreatedBy
     * @param pLocationCode
     * @param pFile
     * @return
     * @throws BTSLBaseException
     */
    public int writeFileToDatabase(Connection pCon, String pFilename, String pCreatedBy, String pLocationCode, String pFile) throws BTSLBaseException {
        final String methodName = "writeFileToDatabase";
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " Entered pFilename:" + pFilename + "pLocationCode" + pLocationCode + "pCreatedBy" + pCreatedBy + "pFile" + pFile);
        }
        boolean processFile = false;
        FileReader fileReader = null; // file reader
        BufferedReader bufferReader = null;
        long recordsTotal = 0;// for line numbers
        int totRecords = 0;
        String delim = Constants.getProperty("DelimiterforMNPFile");
        Date currentDate = new Date(System.currentTimeMillis());
        if (BTSLUtil.isNullString(delim)) {
            delim = " ";
        }
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
        StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM ported_msisdn WHERE msisdn=? ");
        sqlBuff.append("AND subscriber_type=? ");
        sqlBuff.append("AND port_type=? ");
        // msisdn is already exist as ported in or out then only update not
        // insert
        String selectQuery1 = "SELECT 1 FROM ported_msisdn WHERE msisdn=? ";
        // update if msisdn is already pesent in database
        String updateQuery = "UPDATE  ported_msisdn SET subscriber_type=UPPER(?), port_type=UPPER(?), created_by=?, created_on=? WHERE msisdn=? ";
        // Query for Inserting
        StringBuilder insertSql = new StringBuilder("INSERT INTO ported_msisdn(msisdn,subscriber_type,");
        insertSql.append("port_type,created_by,created_on) VALUES (?,UPPER(?),UPPER(?),?,?)");

        // check mobile number is already exist in database
        StringBuilder strBuff = new StringBuilder("select UP.MSISDN,U.status");
        strBuff.append(" FROM user_phones UP,users U WHERE UP.msisdn = ?");
        strBuff.append(" AND U.user_id=UP.user_id AND (U.status <> 'N' AND U.status <> 'C')");
        // if exist and we ae tying to add as ported out number then suspend the
        // user(preference base)
        StringBuilder updateSql1 = new StringBuilder("UPDATE users SET previous_status=status, status=?, modified_on=?, modified_by=? ");
        updateSql1.append("WHERE user_id=(SELECT UP.user_id FROM user_phones UP, users U WHERE U.user_id=UP.user_id AND U.status NOT IN('N','C') AND UP.msisdn=?) ");

        String selectQuery = sqlBuff.toString();
        String inserttQuery = insertSql.toString();
        String selectQueryUsr = strBuff.toString();
        String suspendQueryUsr = updateSql1.toString();
        StringBuilder pInvalidMsisdn = new StringBuilder();
        try {
            boolean fileMoved = false;
            StringTokenizer startparser = null;
            int updateStatusCount = 0;
            insertPstmt = pCon.prepareStatement(inserttQuery);
            updatePstmt = pCon.prepareStatement(updateQuery);
            isExistPstmt = pCon.prepareStatement(selectQuery1);
            isExistPortedPstmt = pCon.prepareStatement(selectQuery);
            isExistUserPstmt = pCon.prepareStatement(selectQueryUsr);
            suspendUserPstmt = pCon.prepareStatement(suspendQueryUsr);

            fileReader = new FileReader("" + pFilename);
            if (fileReader != null) {
                bufferReader = new BufferedReader(fileReader);
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
                String subscriberType;
                String portedType;
                int i = 1;
                // If Line is not Blank Process the Number
                while ((tempStr = bufferReader.readLine()) != null) // Read the
                                                                    // file till
                                                                    // the end
                                                                    // of file.
                {
                    if (tempStr.trim().length() == 0) {
                        // lines
                        continue;
                    }
                    recordsTotal++; // Keeps track of line number
                    try {
                        startparser = new StringTokenizer(tempStr, delim);
                        if (logger.isDebugEnabled()) {
                            logger.debug(methodName, "Input = " + tempStr);
                            logger.debug(methodName, "There are " + startparser.countTokens() + " entries");
                        }

                        if (startparser.countTokens() != 3) {
                            if (logger.isDebugEnabled()) {
                                logger.debug(methodName, "Not a valid entry" + tempStr);
                            }
                            MNPPocessingLog.log("MNP File Upload", pCreatedBy, "", recordsTotal, "Not a valid entry", "Fail", pFilename + "," + pLocationCode);
                            continue;
                        }
                        msisdn = BTSLUtil.NullToString(startparser.nextToken()).trim();
                        subscriberType = BTSLUtil.NullToString(startparser.nextToken()).trim().toUpperCase();
                        portedType = BTSLUtil.NullToString(startparser.nextToken()).trim().toUpperCase();
                        filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);

                        if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                            if (logger.isDebugEnabled()) {
                                logger.debug(methodName, "Not a valid MSISDN" + msisdn);
                            }
                            MNPPocessingLog.log("MNP File Upload", pCreatedBy, msisdn, recordsTotal, "Not a valid MSISDN", "Fail", pFilename + "," + pLocationCode);
                            pInvalidMsisdn.append(msisdn);
                            pInvalidMsisdn.append(",");
                            continue;
                        }
                        msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);

                        if (networkPrefixVO == null) {
                            if (logger.isDebugEnabled()) {
                                logger.debug(methodName, "Port prefix not defined" + msisdn);
                            }
                            MNPPocessingLog.log("File Upload", pCreatedBy, msisdn, recordsTotal, "Port prefix not defined", "Fail", pFilename + "," + pLocationCode);
                            pInvalidMsisdn.append(msisdn);
                            pInvalidMsisdn.append(",");
                            continue;
                        } else if (portedType.equalsIgnoreCase(PretupsI.PORTED_IN)) {
                            if (!networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug(methodName, "Port prefix not defined" + msisdn);
                                }
                                MNPPocessingLog.log("File Upload", pCreatedBy, msisdn, recordsTotal, "Port prefix not defined", "Fail", pFilename + "," + pLocationCode);
                                pInvalidMsisdn.append(msisdn);
                                pInvalidMsisdn.append(",");
                                continue;
                            }
                        }

                        networkCode = networkPrefixVO.getNetworkCode();
                        if (!networkCode.equals(pLocationCode)) {
                            if (logger.isDebugEnabled()) {
                                logger.debug(methodName, "Not supporting Network" + msisdn);
                            }
                            MNPPocessingLog.log("File Upload", pCreatedBy, msisdn, recordsTotal, "Not supporting Network", "Fail", pFilename + "," + pLocationCode);
                            pInvalidMsisdn.append(msisdn);
                            pInvalidMsisdn.append(",");
                            continue;
                        }

                        if (!(subscriberType.equalsIgnoreCase(PretupsI.SERIES_TYPE_PREPAID) || subscriberType.equalsIgnoreCase(PretupsI.SERIES_TYPE_POSTPAID))) {
                            if (logger.isDebugEnabled()) {
                                logger.debug(methodName, "Not supported Subscriber type" + msisdn);
                            }
                            MNPPocessingLog.log("File Upload", pCreatedBy, msisdn, recordsTotal, "Not supported Subscriber type", "Fail", pFilename + "," + pLocationCode);
                            pInvalidMsisdn.append(msisdn);
                            pInvalidMsisdn.append(",");
                            continue;
                        }

                        if (!(portedType.equalsIgnoreCase(PretupsI.PORTED_IN) || portedType.equalsIgnoreCase(PretupsI.PORTED_OUT))) {
                            if (logger.isDebugEnabled()) {
                                logger.debug(methodName, "Not supported PORT type" + msisdn);
                            }
                            MNPPocessingLog.log("File Upload", pCreatedBy, msisdn, recordsTotal, "Not supported Port type", "Fail", pFilename + "," + pLocationCode);
                            pInvalidMsisdn.append(msisdn);
                            pInvalidMsisdn.append(",");
                            continue;
                        }

                        try {
                            isExistPortedPstmt.setString(1, filteredMsisdn);
                            isExistPortedPstmt.setString(2, subscriberType);
                            isExistPortedPstmt.setString(3, portedType);
                            isExistsAsPort = isExistPortedPstmt.executeQuery();
                        } catch (Exception e) {
                            logger.error(methodName, "Exception=" + e.getMessage());
                            logger.errorTrace(methodName, e);
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
                            throw new BTSLBaseException(this, methodName, "error.general.processing");
                        }// end of catch
                        if (isExistsAsPort.next()) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("processMNPFile", "Msisdn already exist" + filteredMsisdn);
                            }
                            MNPPocessingLog.log("File Upload", pCreatedBy, msisdn, recordsTotal, "Msisdn already exist", "Fail", pFilename + "," + pLocationCode);
                            pInvalidMsisdn.append(msisdn);
                            pInvalidMsisdn.append(",");
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
                                    logger.error(methodName, "Exception=" + e.getMessage());
                                    logger.errorTrace(methodName, e);
                                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
                                    throw new BTSLBaseException(this, methodName, "error.general.processing");
                                }// end of catch
                                if (isExistingUser.next()) {
                                    String status = isExistingUser.getString("status");
                                    if (status.equals(PretupsI.USER_STATUS_SUSPEND_REQUEST) || status.equals(PretupsI.USER_STATUS_SUSPEND)) {
                                        if (logger.isDebugEnabled()) {
                                            logger.debug("processMNPFile", "User is already suspended, " + filteredMsisdn);
                                        }
                                        MNPPocessingLog.log("File Upload", pCreatedBy, msisdn, recordsTotal, "User is already suspended", "Fail", pFilename + "," + pLocationCode);
                                        pInvalidMsisdn.append(msisdn);
                                        pInvalidMsisdn.append(",");
                                        continue;
                                    }
                                    // suspend channel user
                                    try {
                                        suspendUserPstmt.setString(4, filteredMsisdn);
                                        suspendUserPstmt.setString(1, PretupsI.USER_STATUS_SUSPEND);
                                        suspendUserPstmt.setString(3, pCreatedBy);
                                        suspendUserPstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                                        updateStatusCount = suspendUserPstmt.executeUpdate();
                                    } catch (Exception e) {
                                        logger.error(methodName, "Exception=" + e.getMessage());
                                        logger.errorTrace(methodName, e);
                                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
                                        throw new BTSLBaseException(this, methodName, "error.general.processing");
                                    }// end of catch
                                    if (updateStatusCount <= 0) {
                                        if (logger.isDebugEnabled()) {
                                            logger.debug("processMNPFile", "User suspend process fail, " + filteredMsisdn);
                                        }
                                        MNPPocessingLog.log("File Upload", pCreatedBy, msisdn, recordsTotal, "User suspend process fail", "Fail", pFilename + "," + pLocationCode);
                                        pInvalidMsisdn.append(msisdn);
                                        pInvalidMsisdn.append(",");
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
                                updatePstmt.setString(i++, pCreatedBy);
                                updatePstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                                updatePstmt.setString(i++, filteredMsisdn);
                                queryResult = updatePstmt.executeUpdate();
                            } else {

                                insertPstmt.setString(i++, filteredMsisdn);
                                insertPstmt.setString(i++, subscriberType);
                                insertPstmt.setString(i++, portedType);
                                insertPstmt.setString(i++, pCreatedBy);
                                insertPstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                                // Execute Query
                                // if sql exception occure then it is caught in
                                // catch block
                                queryResult = insertPstmt.executeUpdate();
                            }
                            // if any error occurs in inserting the record then
                            // make an entry in logs for that msisdn.
                            if (queryResult <= 0) {
                                MNPPocessingLog.log("File Upload", pCreatedBy, msisdn, recordsTotal, "Error while inserting record", "Fail", pFilename + "," + pLocationCode);
                            } else {
                                msisdnAdded += queryResult;
                                totRecords++; // Total records count to be
                                              // inserted or updated in database
                                MNPPocessingLog.log("File Upload", pCreatedBy, msisdn, recordsTotal, "Record inserted", "Pass", pFilename + "," + pLocationCode);
                                pCon.commit();
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
                            logger.error(methodName, "Exception=" + e.getMessage());
                            logger.errorTrace(methodName, e);
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
                            throw new BTSLBaseException(this, methodName, "error.general.processing");
                        }// end of catch
                    } catch (Exception e) {
                        logger.error(methodName, "Exception=" + e.getMessage());
                        logger.errorTrace(methodName, e);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
                        throw new BTSLBaseException(this, methodName, "error.general.processing");
                    }// end of catch
                } // end of While
                if (logger.isDebugEnabled()) {
                    logger.debug(methodName, "Total lines in File " + recordsTotal + " And Total Records for process:" + totRecords);
                }
            }// end of bufferedReader !=null
            if (recordsTotal == 0) {
                throw new BTSLBaseException(this, methodName, "routing.mnp.error.zerofilesize", "selectMNPfile");
            }
            if (totRecords == 0) {
                throw new BTSLBaseException(this, methodName, "routing.mnp.error.norecords", "selectMNPfile");
            }
            // Moving File after Processing
            if (msisdnAdded > 0) {
                fileMoved = this.moveFileToArchive(pFilename, pFile);
                if (fileMoved) {
                    processFile = true;
                } else {
                    throw new BTSLBaseException(this, methodName, "routing.mnp.error.filenomove", "selectMNPfile");
                }
            }

        }// end of try(outer most try)
        catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (SQLException sqe) {
            logger.error(methodName, "SQLException " + sqe.getMessage());
            logger.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeFileToDatabase]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            logger.error(methodName, "Exception " + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeFileToDatabase]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "processed till record no:" + totRecords);
            }
            // Write in LOGS
            if (!processFile) {
                totRecords = 0;
            }
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "p_userID:" + pCreatedBy + " pLocationCode:" + pLocationCode + " Processed=" + pFile + " ,No of records=" + totRecords + " ,Status=" + processFile);
            }
            MNPPocessingLog.log("File Upload", pCreatedBy, null, totRecords, "Records uploaded to the database", "Finally Block", pFilename + "," + pLocationCode);
            // Destroying different objects
            try {
                if (bufferReader != null) {
                    bufferReader.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            try {
                if (insertPstmt != null) {
                    insertPstmt.close();
                }
            } catch (Exception ex) {
                logger.errorTrace(methodName, ex);
            }
            try {
                if (updatePstmt != null) {
                    updatePstmt.close();
                }
            } catch (Exception ex) {
                logger.errorTrace(methodName, ex);
            }

            try{
		        if (isExistPstmt!= null){
		        	isExistPstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  logger.error("An error occurred closing statement.", e);
		      }
            try{
		        if (isExistPortedPstmt!= null){
		        	isExistPortedPstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  logger.error("An error occurred closing statement.", e);
		      }
            try{
		        if (isExistUserPstmt!= null){
		        	isExistUserPstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  logger.error("An error occurred closing statement.", e);
		      }
            try{
		        if (suspendUserPstmt!= null){
		        	suspendUserPstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  logger.error("An error occurred closing statement.", e);
		      }
            try{
		        if (isExistsAsPort!= null){
		        	isExistsAsPort.close();
		        }
		      }
		      catch (SQLException e){
		    	  logger.error("An error occurred closing statement.", e);
		      }
            try {
                if (isExistingUser != null) {
                    isExistingUser.close();
                }
            } catch (Exception ex) {
                logger.errorTrace(methodName, ex);
            }
            try {
                if (found != null) {
                    found.close();
                }
            } catch (Exception ex) {
                logger.errorTrace(methodName, ex);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, " Exiting updateCount=" + msisdnAdded + ", pInvalidMsisdn=" + pInvalidMsisdn);
            }
        }// end of finally
         // adding up the updated and inserted record count and return that
        return msisdnAdded;
    }

    /**
     * This method will move the processed file in seperate folder
     * 
     * @param pFilename
     * @param pFile
     * @return boolean
     */
    public boolean moveFileToArchive(String pFilename, String pFile) {
        final String methodName = "moveFileToArchive";
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " Entered ");
        }
        File fileRead = new File(pFilename);
        File fileArchive = new File("" + Constants.getProperty("MNPArchiveFilePath"));
        if (!fileArchive.isDirectory()) {
            fileArchive.mkdirs();
        }
        fileArchive = new File("" + Constants.getProperty("MNPArchiveFilePath") + pFile + "." + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()); // to
                                                                                                                                                           // make
                                                                                                                                                           // the
                                                                                                                                                           // new
                                                                                                                                                           // file
                                                                                                                                                           // name
        boolean flag = fileRead.renameTo(fileArchive);
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " Exiting File Moved=" + flag);
        }
        return flag;
    }// end of moveFileToArchive

    /**
     * Method to write the number in ported list in DB
     * 
     * @param pCon
     * @param pNumberPortVO
     * @return
     * @throws BTSLBaseException
     */
    public int writeMobileNumberToDatabase(Connection pCon, NumberPortVO pNumberPortVO) throws BTSLBaseException {
        final String methodName = "writeMobileNumberToDatabase";
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " Entered pFilename:" + pNumberPortVO);
        }
        PreparedStatement insertPstmt = null;
        PreparedStatement updatePstmt = null;
        int msisdnAdded = 0;

        String insertSql = "INSERT INTO ported_msisdn(msisdn,subscriber_type,port_type,created_by,created_on) VALUES(?,UPPER(?),UPPER(?),?,?) ";
        String updateSql = "UPDATE  ported_msisdn SET subscriber_type=UPPER(?), port_type=UPPER(?), created_by=?, created_on=? WHERE msisdn=? ";
        try {

            if (isExists(pCon, pNumberPortVO.getMsisdn(), null, null)) {
                updatePstmt = pCon.prepareStatement(updateSql);
                updatePstmt.setString(1, pNumberPortVO.getSubscriberType());
                updatePstmt.setString(2, pNumberPortVO.getPortType());
                updatePstmt.setString(3, pNumberPortVO.getCreatedBy());
                updatePstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(pNumberPortVO.getCreatedOn()));
                updatePstmt.setString(5, pNumberPortVO.getMsisdn());
                msisdnAdded = updatePstmt.executeUpdate();
            } else {
                insertPstmt = pCon.prepareStatement(insertSql);
                insertPstmt.setString(1, pNumberPortVO.getMsisdn());
                insertPstmt.setString(2, pNumberPortVO.getSubscriberType());
                insertPstmt.setString(3, pNumberPortVO.getPortType());
                insertPstmt.setString(4, pNumberPortVO.getCreatedBy());
                insertPstmt.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(pNumberPortVO.getCreatedOn()));
                msisdnAdded = insertPstmt.executeUpdate();
            }
        }// end of try
        catch (SQLException sqe) {
            logger.error(methodName, "SQLException " + sqe.getMessage());
            logger.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeMobileNumberToDatabase]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            logger.error(methodName, "Exception " + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[writeMobileNumberToDatabase]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try{
		        if (insertPstmt!= null){
		        	insertPstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  logger.error("An error occurred closing statement.", e);
		      }
        	try{
		        if (updatePstmt!= null){
		        	updatePstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  logger.error("An error occurred closing statement.", e);
		      }
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, " Exiting updateCount=" + msisdnAdded);
            }
        }// end of finally
        return msisdnAdded;
    }

    /**
     * Check mobile number already exist in ported_msisdn table and returns true
     * if the record is found
     * 
     * @param pCon
     *            Connection
     * @param p_networkCode
     *            String
     * @param pMsisdn
     *            String
     * @param pUserType
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    /**
     * @param pCon
     * @param pMsisdn
     * @param pUserType
     * @param pPortType
     * @return
     * @throws BTSLBaseException
     */
    public boolean isExists(Connection pCon, String pMsisdn, String pUserType, String pPortType) throws BTSLBaseException {

        final String methodName = "isExists";
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, "Entered pMsisdn=" + pMsisdn + ",pUserType=" + pUserType + ",p_barredType=" + pPortType);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM ported_msisdn WHERE msisdn=? ");
        if (!BTSLUtil.isNullString(pUserType)) {
            sqlBuff.append("AND subscriber_type=? ");
        }
        if (!BTSLUtil.isNullString(pPortType)) {
            sqlBuff.append("AND port_type=? ");
        }
        String selectQuery = sqlBuff.toString();
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, "Select Query::" + selectQuery);
        }

        try {
            pstmtSelect = pCon.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setString(i++, pMsisdn);
            if (!BTSLUtil.isNullString(pUserType)) {
                pstmtSelect.setString(i++, pUserType);
            }
            if (!BTSLUtil.isNullString(pPortType)) {
                pstmtSelect.setString(i++, pPortType);
            }
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            logger.error(methodName, "SQLException: " + sqle.getMessage());
            logger.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[isExists]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            logger.error(methodName, "Exception: " + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NumberPortDAO[isExists]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		logger.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	logger.error("An error occurred closing statement.", e);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Exiting: isExists found=" + found);
            }
        }
        return found;
    }

}
