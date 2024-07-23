package com.web.pretups.routing.subscribermgmt.businesslogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.RoutingFileProcessLog;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.superadmin.subscriberrouting.responseVO.BulkSubscriberRoutingErrorList;

public class RoutingWebDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());
    public static final String CLASS_NAME = "RoutingWebDAO";

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
        final String methodName = "deleteMsisdn";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_inMsisdn : ");
        	msg.append(p_inMsisdn);
        	msg.append(", p_subscriberType : ");
        	msg.append(p_subscriberType);
        	msg.append(", p_userID : ");
        	msg.append(p_userID);

        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
        int updateCount = 0;
        int i = 1;
        int outIndex = 0, index = 0;
        try {
            final StringBuffer updateQueryBuff = new StringBuffer("DELETE FROM subscriber_routing ");
            updateQueryBuff.append("WHERE msisdn=? AND subscriber_type=?");
            final String updateQuery = updateQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "updaet query:" + updateQuery);
            }
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
                    if (updateCount > 0) {
                        RoutingFileProcessLog.log("Delete MSISDN", p_userID, p_inMsisdn[index], index, "Record deleted successfully", "Pass", null);
                    } else {
                        RoutingFileProcessLog.log("Delete MSISDN", p_userID, p_inMsisdn[index], index, "Record can not be deleted", "Fail", null);
                    }

                    i = 1;
                    pstmt.clearParameters();
                }
            }// end of for
        }// end of try
        catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[deleteMsisdn]", "", "", "",
                "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberControlDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[deleteMsisdn]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting updateCount:" + updateCount);
            }
            RoutingFileProcessLog.log("Delete MSISDN", p_userID, null, 0, index - outIndex + " out of " + index + " records deleted successfully", null,
                "Inside finally block");
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
        final String methodName = "isMsisdnExist";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_Msisdn : ");
        	msg.append(p_Msisdn);
        	msg.append(", p_subscriberType : ");
        	msg.append(p_subscriberType);


        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuffer strBuff = new StringBuffer("SELECT msisdn ");
        strBuff.append(" FROM subscriber_routing  ");
        strBuff.append(" WHERE msisdn = ? AND subscriber_type=? AND status='Y'");
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query= " + strBuff.toString());
            }
            pstmt = p_con.prepareStatement(strBuff.toString());
            pstmt.setString(1, p_Msisdn);
            pstmt.setString(2, p_subscriberType);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        }// end of try
        catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[isMsisdnExist]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[isMsisdnExist]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }// end of finally.
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

    public int deleteMsisdnFromWhiteList(Connection con, String[] inMsisdn, String[] outMsisdn, String userID, String networkCode, Locale locale, ArrayList errorList) throws BTSLBaseException {
        final String methodName = "deleteMsisdnFromWhiteList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered inMsisdn : ");
        	msg.append(inMsisdn);
        	msg.append(", userID : ");
        	msg.append(userID);


        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtForDelete = null, pstmtForSelectQuery = null;
        ResultSet rs = null;
        int deleteCount = 0, deleteRows = 0;
        int index = 0, noOfMsisdn = 0;
        final StringBuffer outMsisdnBuf = new StringBuffer();
        try {
            final StringBuffer deleteQueryBuff = new StringBuffer("DELETE FROM white_list WHERE msisdn=? AND network_code=? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "deleteQueryBuff query:" + deleteQueryBuff.toString());
            }
            pstmtForDelete = con.prepareStatement(deleteQueryBuff.toString());

            final StringBuffer strBuff = new StringBuffer("SELECT 1 FROM white_list WHERE msisdn = ? AND network_code=? AND status='Y'");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query= " + strBuff.toString());
            }
            pstmtForSelectQuery = con.prepareStatement(strBuff.toString());
            BulkSubscriberRoutingErrorList errorVO;
            for (index = 0, noOfMsisdn = inMsisdn.length; index < noOfMsisdn; index++) {
                errorVO = new BulkSubscriberRoutingErrorList();
                // check if the msisdn exists or not .
                // If not exists then insert that msisdn into p_outMsisdn.
                if (!BTSLUtil.isNullString(inMsisdn[index])) {
                    pstmtForSelectQuery.setString(1, inMsisdn[index]);
                    pstmtForSelectQuery.setString(2, networkCode);
                    rs = pstmtForSelectQuery.executeQuery();
                    pstmtForSelectQuery.clearParameters();
                    if (!rs.next()) {
                        outMsisdnBuf.append(inMsisdn[index]);
                        outMsisdnBuf.append(",");
                        RoutingFileProcessLog.log("BATCHDELETE", userID, inMsisdn[index], index, "No Record Exist", "Fail",
                            "When subscriber type is POST then delete from white list");
                        errorVO.setMsisdn(inMsisdn[index]);
                        errorVO.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_RECORD_EXIST, null));
                        errorVO.setLineNumber(String.valueOf(index + 1));
                        errorList.add(errorVO);
                        continue;
                    }
                    pstmtForDelete.setString(1, inMsisdn[index]);
                    pstmtForDelete.setString(2, networkCode);
                    deleteCount = pstmtForDelete.executeUpdate();
                    if (deleteCount > 0) {
                        deleteRows += deleteCount;
                        RoutingFileProcessLog.log("BATCHDELETE", userID, inMsisdn[index], index, "Record deleted successfully", "Pass",
                            "When subscriber type is POST then delete from white list");
                    } else {
                        outMsisdnBuf.append(inMsisdn[index]);
                        RoutingFileProcessLog.log("BATCHDELETE", userID, inMsisdn[index], index, "Record can not be deleted", "Fail",
                            "When subscriber type is POST then delete from white list");
                        errorVO.setMsisdn(inMsisdn[index]);
                        errorVO.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_DELETING_RECORD, null));
                        errorVO.setLineNumber(String.valueOf(index + 1));
                        errorList.add(errorVO);
                    }
                    pstmtForDelete.clearParameters();
                }
            }// end of for
            outMsisdn[0] = outMsisdnBuf.toString();
            if (!BTSLUtil.isNullString(outMsisdn[0])) {
                outMsisdn[0] = outMsisdn[0].substring(0, outMsisdn[0].length() - 1);
            }
        }// end of try
        catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[deleteMsisdnFromWhiteList]", "", "", "",
                "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[deleteMsisdnFromWhiteList]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtForSelectQuery != null) {
                    pstmtForSelectQuery.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtForDelete != null) {
                    pstmtForDelete.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting updateCount:" + deleteCount);
            }
            RoutingFileProcessLog.log("BATCHDELETE", userID, null, 0, deleteRows + " out of " + noOfMsisdn + " records deleted successfully", null, "Inside finally block");
        }// end of finally
        return deleteRows;
    }

    /**
     * This methods deletes MSISDN batchwise from the WhiteList
     * 
     * @param con
     * @param inMsisdn
     * @param subscriberType
     *            String
     * @param outMsisdn
     *            String[]
     * @param p_userID
     *            String
     * @return p_subscriberType
     * @throws BTSLBaseException
     * @author Shishupal
     */

    public int deleteMsisdnBatch(Connection con, String[] inMsisdn, String subscriberType, String[] outMsisdn, String userID, Locale locale, ArrayList errorList) throws BTSLBaseException {
        final String methodName = "deleteMsisdnBatch";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_inMsisdn : ");
        	msg.append(inMsisdn);
        	msg.append(", outMsisdn : ");
        	msg.append(outMsisdn);
        	msg.append(", subscriberType : ");
        	msg.append(subscriberType);
        	msg.append(", userID : ");
        	msg.append(userID);


        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtForDelete = null, pstmtForSelectQuery = null;
        ResultSet rs = null;
        int deleteCount = 0, deleteRows = 0;
        int index = 0, len = 0;
        final StringBuffer outMsisdnBuf = new StringBuffer();
        try {
            final StringBuffer deleteQueryBuff = new StringBuffer("DELETE FROM subscriber_routing WHERE msisdn=? AND subscriber_type=? ");
            final String deleteQuery = deleteQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Delete query:" + deleteQuery);
            }
            pstmtForDelete = con.prepareStatement(deleteQuery);

            final StringBuffer strBuff = new StringBuffer("SELECT 1 FROM subscriber_routing WHERE msisdn = ? AND subscriber_type=? AND status='Y' ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query= " + strBuff.toString());
            }
            pstmtForSelectQuery = con.prepareStatement(strBuff.toString());
            BulkSubscriberRoutingErrorList errorVO;
            for (index = 0, len = inMsisdn.length; index < len; index++) {
                errorVO = new BulkSubscriberRoutingErrorList();
                if (!BTSLUtil.isNullString(inMsisdn[index])) {
                    pstmtForSelectQuery.setString(1, inMsisdn[index]);
                    pstmtForSelectQuery.setString(2, subscriberType);
                    rs = pstmtForSelectQuery.executeQuery();
                    pstmtForSelectQuery.clearParameters();
                    if (!rs.next()) {
                        outMsisdnBuf.append(inMsisdn[index]);
                        outMsisdnBuf.append(",");
                        RoutingFileProcessLog.log("BATCHDELETE", userID, inMsisdn[index], index, "No Record Exist", "Fail", null);
                        errorVO.setMsisdn(inMsisdn[index]);
                        errorVO.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_RECORD_EXIST, null));
                        errorVO.setLineNumber(String.valueOf(index + 1));
                        errorList.add(errorVO);
                        continue;
                    }
                    pstmtForDelete.setString(1, inMsisdn[index]);
                    pstmtForDelete.setString(2, subscriberType);
                    deleteCount = pstmtForDelete.executeUpdate();
                    if (deleteCount > 0) {
                        con.commit();
                        deleteRows += deleteCount;
                        RoutingFileProcessLog.log("BATCHDELETE", userID, inMsisdn[index], index, "Record deleted successfully", "Pass", null);
                    } else {
                        outMsisdnBuf.append(inMsisdn[index]);
                        RoutingFileProcessLog.log("BATCHDELETE", userID, inMsisdn[index], index, "Record can not be deleted", "Fail", null);
                        errorVO.setMsisdn(inMsisdn[index]);
                        errorVO.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_DELETING_RECORD, null));
                        errorVO.setLineNumber(String.valueOf(index + 1));
                        errorList.add(errorVO);
                    }
                    pstmtForDelete.clearParameters();
                }
            }// end of for
            outMsisdn[0] = outMsisdnBuf.toString();
            if (!BTSLUtil.isNullString(outMsisdn[0])) {
                outMsisdn[0] = outMsisdn[0].substring(0, outMsisdn[0].length() - 1);
            }
        }// end of try
        catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[deleteMsisdnBatch]", "", "", "",
                "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[deleteMsisdnBatch]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtForSelectQuery != null) {
                    pstmtForSelectQuery.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtForDelete != null) {
                    pstmtForDelete.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting updateCount:" + deleteCount);
            }
            RoutingFileProcessLog.log("BATCHDELETE", userID, null, 0, deleteRows + " out of " + len + " records deleted successfully", null, "Inside finally block");
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
    public int writeRoutingToDatabase(Connection p_con, String[] p_msisdnArray, String p_userID, String p_locationCode, String p_subsType, String p_interfaceCode, StringBuffer p_invalidMsisdn, String p_interfaceStr, StringBuffer invalidPrefix, StringBuffer invalidNetwork) throws BTSLBaseException {
        final String methodName = "writeRoutingToDatabase";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_interfaceCode : ");
        	msg.append(p_interfaceCode);
        	msg.append(", p_subsType : ");
        	msg.append(p_subsType);
        	msg.append(", p_interfaceStr : ");
        	msg.append(p_interfaceStr);
        	msg.append(", p_invalidMsisdn : ");
        	msg.append(p_invalidMsisdn.toString());
        	msg.append(", p_locationCode : ");
        	msg.append(p_locationCode);
        	msg.append(", p_userID : ");
        	msg.append(p_userID);
        	msg.append(", p_msisdnArray : ");
        	msg.append(p_msisdnArray.toString());
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        long recordsTotal = 0;
        int totRecords = 0;
        final Date currentDate = new Date(System.currentTimeMillis());
        PreparedStatement insertPstmt = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtSelectExtID = null;
        ResultSet rs = null;
        int msisdnAdded = 0;
        int msisdnUpdated = 0;
        int queryResult = -1;
        // Query for Inserting
        final StringBuffer insertSql = new StringBuffer("INSERT INTO subscriber_routing(msisdn, interface_id, ");
        insertSql.append("subscriber_type, external_interface_id, status, created_by, created_on, modified_by, ");
        insertSql.append("modified_on, text1, text2) VALUES (?,?,?,?,?,?,?,?,?,?,?) ");
        final StringBuffer updateQuery = new StringBuffer();
        updateQuery.append("UPDATE subscriber_routing SET interface_id = ?, external_interface_id = ?, ");
        updateQuery.append("status = ?, modified_by = ?, modified_on = ?, text1 = ?, text2 = ? ");
        updateQuery.append("WHERE msisdn =? AND subscriber_type=? ");
        // This query is used to get the external id based on the interface id
        // from interface table.
        final String selectExtId = "SELECT external_id FROM interfaces WHERE interface_id=? and status<>'N'";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Query=" + updateQuery);
        }
        try {
            String labelValue = null;
            insertPstmt = p_con.prepareStatement(insertSql.toString());
            pstmtUpdate = p_con.prepareStatement(updateQuery.toString());
            pstmtSelectExtID = p_con.prepareStatement(selectExtId);// Added by
            // ashish
            // executing the query to get the external id
            pstmtSelectExtID.setString(1, p_interfaceCode);
            rs = pstmtSelectExtID.executeQuery();
            if (rs.next()) {
                labelValue = rs.getString("external_id");
            }
            if (p_msisdnArray != null && p_msisdnArray.length != 0) // If File
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
                final int len = p_msisdnArray.length;
                for (int j = 0; j < len; j++) {
                    msisdn = p_msisdnArray[j];
                    if (msisdn.trim().length() == 0) {
                        continue;
                    }
                    recordsTotal++; // Keeps track of line number
                    try {
                        if (!BTSLUtil.isValidIdentificationNumber(msisdn)) {
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "Not a valid MSISDN " + msisdn);
                            }
                            RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, msisdn, recordsTotal, "Not a valid MSISDN ", " Fail ", p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");
                            continue;
                        }
                        if(isMsisdnExist(p_con, msisdn, p_subsType)){
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "Mobile number already exist in the system" + msisdn);
                            }
                            RoutingFileProcessLog.log("File Upload", p_userID, msisdn, recordsTotal, "Mobile number already exist in the system", "Fail", p_locationCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(",");
                            continue;
                        }
                        msisdnPrefix = PretupsBL.getMSISDNPrefix(msisdn);
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);

                        if (networkPrefixVO == null) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("writeRoutingToDatabase ", " Not supporting Network " + msisdn);
                            }
                            RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, msisdn, recordsTotal, "Not supporting Network", "Fail", p_locationCode);
                            invalidPrefix.append(msisdn);
                            invalidPrefix.append(",");
                            continue;
                        }
                        networkCode = networkPrefixVO.getNetworkCode();
                        if (!networkCode.equals(p_locationCode)) {
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "Not supporting Network" + msisdn);
                            }
                            RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, msisdn, recordsTotal, "Not supporting Network", "Fail", p_locationCode);
                            invalidNetwork.append(msisdn);
                            invalidNetwork.append(",");
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
                            if (queryResult <= 0) {
                                RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, msisdn, recordsTotal, "Error while inserting record", "Fail", p_locationCode);
                            } else {
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
                                    if (_log.isDebugEnabled()) {
                                        _log.debug(methodName, "MSISDN already Exist" + msisdn);
                                    }
                                    // make an entry in logs for updating the
                                    // record
                                    RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, msisdn, recordsTotal, "MSISDN already Exist so updating record", "Pass",
                                        p_locationCode);
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
                                    if (queryResult <= 0) {
                                        RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, msisdn, recordsTotal, "Error while updating record", "Fail",
                                            p_locationCode);
                                    } else {
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
                                _log.errorTrace(methodName, sqe);
                                RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, msisdn, recordsTotal, "SQLException=" + sqe.getMessage(), "Exception",
                                    p_locationCode);
                            }
                        }// end of catch
                    }// end of try(just inside the while loop)
                    catch (Exception e) {
                        _log.errorTrace(methodName, e);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[writeRoutingToDatabase]",
                            "", "", "", "Exception:" + e.getMessage());
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
                    }// end of catch
                } // end of While
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Total Records for process:" + totRecords);
                }
            }// end of bufferedReader !=null
            if(p_invalidMsisdn.isEmpty() && invalidNetwork.isEmpty() && invalidPrefix.isEmpty()) {
                if (recordsTotal == 0) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ROUTING_ROUTINGUPLOAD_ERROR_ZEROROUTINGNUMBER);
                }
                if (totRecords == 0) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ROUTING_ROUTINGUPLOAD_ERROR_NORECORDS);
                }
            }
        }// end of try(outer most try)
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[writeRoutingToDatabase]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[writeRoutingToDatabase]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Entered : p_userID : ");
            	msg.append(p_userID);
            	msg.append(", p_locationCode : ");
            	msg.append(p_locationCode);
            	msg.append(", No of records : ");
            	msg.append(totRecords);
            	
            	String message=msg.toString();
                _log.debug(methodName, message);
            }
            RoutingFileProcessLog.log("ADDSUBROUTING MANUAL", p_userID, null, totRecords, "Records uploaded to the database", "Finally Block", p_locationCode);
            // Destroying different objects
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (insertPstmt != null) {
                    insertPstmt.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelectExtID != null) {
                    pstmtSelectExtID.close();
                }
            } catch (Exception ex1) {
                _log.errorTrace(methodName, ex1);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting updateCount : ");
            	msg.append(msisdnAdded);
            	msg.append(", p_invalidMsisdn : ");
            	msg.append(p_invalidMsisdn);
            	
            	String message=msg.toString();
                _log.debug(methodName, message);
            }
        }// end of finally
         // adding up the updated and inserted record count and return that
        if (msisdnUpdated > 0) {
            msisdnAdded += msisdnUpdated;
        }
        return msisdnAdded;
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
        final String methodName = "insertInServiceClassRouting";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered : p_serviceType : ");
        	msg.append(p_serviceType);
        	msg.append(", p_serviceClassCode : ");
        	msg.append(p_serviceClass);        	
        	msg.append(", p_status : ");
        	msg.append(p_status);
        	msg.append(", p_module : ");
        	msg.append(p_module);
        	msg.append(", p_userType : ");
        	msg.append(p_userType);
        	msg.append(", p_serviceTypeStr : ");
        	msg.append(p_serviceTypeStr);
        	msg.append(", p_locationCode : ");
        	msg.append(p_locationCode);
        	msg.append(", p_userID : ");
        	msg.append(p_userID);
        	msg.append(", p_msisdnArray : ");
        	msg.append(p_msisdnArray.toString());
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        long recordsTotal = 0;
        int totRecords = 0;
        final Date currentDate = new Date(System.currentTimeMillis());
        PreparedStatement insertPstmt = null;
        PreparedStatement updatePstmt = null;
        int msisdnAdded = 0;
        int msisdnUpdated = 0;
        int queryResult = -1;
        String msisdn = null;
        // Query for Inserting
        final StringBuffer insertSql = new StringBuffer("INSERT INTO srv_class_mapped_msisdn(msisdn, service_type, ");
        insertSql.append("service_class_code, status, created_on, created_by, modified_on, ");
        insertSql.append("modified_by, module, user_type) VALUES (?,?,?,?,?,?,?,?,?,?) ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Query=" + insertSql.toString());
        }

        final StringBuffer updateQuery = new StringBuffer();
        updateQuery.append("UPDATE srv_class_mapped_msisdn SET status = ?, ");
        updateQuery.append("modified_on = ?, modified_by = ? ");
        updateQuery.append("WHERE msisdn =? AND service_type =? AND service_class_code =? AND module =? ");
        updateQuery.append("AND user_type=?");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Query=" + updateQuery.toString());
        }
        try {
            insertPstmt = p_con.prepareStatement(insertSql.toString());
            updatePstmt = p_con.prepareStatement(updateQuery.toString());

            if (p_msisdnArray != null && p_msisdnArray.length != 0) // If File
            // Not Blank
            // Read line
            // by Line
            {
                recordsTotal = 0;
                totRecords = 0;
                int i = 1;
                final int len = p_msisdnArray.length;
                for (int j = 0; j < len; j++) {
                    msisdn = p_msisdnArray[j];
                    if (msisdn.trim().length() == 0) {
                        continue;
                    }
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
                            if (queryResult <= 0) {
                                RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClass, p_status, "Error while inserting record", "Fail",
                                    p_locationCode);
                            } else {
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
                                    if (_log.isDebugEnabled()) {
                                        _log.debug(methodName, "MSISDN already Exist" + msisdn);
                                    }
                                    // make an entry in logs for updating the
                                    // record
                                    RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClass, p_status,
                                        "MSISDN already Exist so updating record", "Pass", p_locationCode);
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
                                    if (queryResult <= 0) {
                                        RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClass, p_status, "Error while updating record",
                                            "Fail", p_locationCode);
                                    } else {
                                        msisdnUpdated += queryResult;
                                        totRecords++;// total number of records
                                        // entered or updated in
                                        // database
                                        RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClass, p_status, "Record updated", "Pass",
                                            p_locationCode);
                                    }
                                    // clears the parameters of
                                    // preparedstatement
                                    updatePstmt.clearParameters();

                                }// end of if(checking constraint name)
                            }// end of if(checking error code)
                            else {
                                _log.errorTrace(methodName, sqe);
                                RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClass, p_status, "SQLException=" + sqe.getMessage(),
                                    "Exception", p_locationCode);
                            }
                        }// end of catch
                    }// end of try(just inside the while loop)
                    catch (Exception e) {
                        _log.errorTrace(methodName, e);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "RoutingWebDAO[insertInServiceClassRouting]", "", "", "", "Exception:" + e.getMessage());
                        throw new BTSLBaseException(this, methodName, "error.general.processing");
                    }// end of catch
                } // end of While
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Total Records for process:" + totRecords);
                }
            }// end of bufferedReader !=null
            if (recordsTotal == 0) {
                throw new BTSLBaseException(this, methodName, "srvrouting.routingupload.error.zeroroutingnumber");
            }
            if (totRecords == 0) {
                throw new BTSLBaseException(this, methodName, "srvrouting.routingupload.error.norecords");
            }

        }// end of try(outer most try)
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[insertInServiceClassRouting]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[insertInServiceClassRouting]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");            	
            	msg.append("p_userID : ");
            	msg.append(p_userID);
            	msg.append(", p_locationCode : ");
            	msg.append(p_locationCode);
            	msg.append(", No of records : ");
            	msg.append(totRecords);
            	
            	String message=msg.toString();
                _log.debug(methodName, message);
            }
            RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClass, p_status, "Records uploaded to the database", "Finally Block",
                p_locationCode);
            // Destroying different objects
            try {
                if (insertPstmt != null) {
                    insertPstmt.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (updatePstmt != null) {
                    updatePstmt.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting updateCount=" + msisdnAdded);
            }
        }// end of finally
         // adding up the updated and inserted record count and return that
        if (msisdnUpdated > 0) {
            msisdnAdded += msisdnUpdated;
        }
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
        final String methodName = "deletingMsisdnBatch";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered : p_inMsisdn : ");
        	msg.append(p_inMsisdn.toString());
        	msg.append(", outMsisdn : ");
        	msg.append(p_outMsisdn.toString());        	
        	msg.append(", p_serviceType : ");
        	msg.append(p_serviceType);
        	msg.append(", p_serviceClassCode : ");
        	msg.append(p_serviceClassCode);
        	msg.append(", p_module : ");
        	msg.append(p_module);
        	msg.append(", p_userType : ");
        	msg.append(p_userType);
        	msg.append(", p_userID : ");
        	msg.append(p_userID);
        	msg.append(", p_status : ");
        	msg.append(p_status);        	
        	
        	String message=msg.toString();
            _log.debug(methodName,message);
        }
        PreparedStatement pstmtForDelete = null, pstmtForSelectQuery = null;
        ResultSet rs = null;
        int deleteCount = 0, deleteRows = 0;
        int index = 0, len = 0;
        StringBuffer outMsisdnBuf = null;
        try {
            final StringBuffer deleteQueryBuff = new StringBuffer("DELETE FROM srv_class_mapped_msisdn ");
            deleteQueryBuff.append("WHERE msisdn=? AND service_type=? AND service_class_code=? AND module=? AND user_type=?");
            final String deleteQuery = deleteQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Delete query:" + deleteQuery);
            }
            pstmtForDelete = p_con.prepareStatement(deleteQuery);
            final StringBuffer strBuff = new StringBuffer("SELECT 1 FROM srv_class_mapped_msisdn");
            strBuff.append(" WHERE msisdn=? AND service_type=? AND service_class_code=? AND module=? AND user_type=? AND status=?");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query= " + strBuff.toString());
            }
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
                        RoutingFileProcessLog.log(p_module, p_inMsisdn[index], p_userType, p_serviceType, p_serviceClassCode, p_status, "Record deleted successfully", "Pass",
                            null);
                    } else {
                        outMsisdnBuf.append(p_inMsisdn[index]);
                        RoutingFileProcessLog.log(p_module, p_inMsisdn[index], p_userType, p_serviceType, p_serviceClassCode, p_status, "Record can not be deleted", "Fail",
                            null);
                    }
                    pstmtForDelete.clearParameters();
                }
            }// end of for
            p_outMsisdn[0] = outMsisdnBuf.toString();
            if (!BTSLUtil.isNullString(p_outMsisdn[0])) {
                p_outMsisdn[0] = p_outMsisdn[0].substring(0, p_outMsisdn[0].length() - 1);
            }
        }// end of try
        catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[deletingMsisdnBatch]", "", "", "",
                "Exception:" + sqle.getMessage());
            try {
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            } catch (BTSLBaseException e1) {
                // TODO Auto-generated catch block
                _log.errorTrace(methodName, e1);
            }
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[deleteMsisdnBatch]", "", "", "",
                "Exception:" + e.getMessage());
            try {
                throw new BTSLBaseException(this, methodName, "error.general.processing");
            } catch (BTSLBaseException e1) {
                // TODO Auto-generated catch block
                _log.errorTrace(methodName, e1);
            }
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtForSelectQuery != null) {
                    pstmtForSelectQuery.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtForDelete != null) {
                    pstmtForDelete.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting updateCount:" + deleteCount);
            }
            RoutingFileProcessLog.log(p_module, null, p_userType, p_serviceType, p_serviceClassCode, p_status,
                deleteRows + " out of " + len + " records deleted successfully", null, "Inside finally block");
        }// end of finally
        return deleteRows;
    }

    /**
     * Method writeFileToDatabase.
     * 
     * @param con
     *            Connection
     * @param filename
     *            String
     * @param userID
     *            String
     * @param locationCode
     *            String
     * @param subsType
     *            String
     * @param interfaceCode
     *            String
     * @param file
     *            String
     * @param invalidMsisdn
     *            StringBuffer
     * @paramr String p_interfaceStr
     * @return int
     * @throws BTSLBaseException
     */
    public int writeFileToDatabase(Connection con, String filename, String userID, String locationCode, String subsType, String interfaceCode, String file, StringBuffer invalidMsisdn, String interfaceStr, Locale locale, ArrayList errorList) throws BTSLBaseException {
        final String methodName = "writeFileToDatabase";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered : filename : ");
        	msg.append(filename);
        	msg.append(", interfaceCode : ");
        	msg.append(interfaceCode);
        	msg.append(", subsType : ");
        	msg.append(subsType);
        	msg.append(", interfaceStr : ");
        	msg.append(interfaceStr);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        boolean processFile = false;
        FileReader fileReader = null; // file reader
        BufferedReader bufferReader = null;
        long recordsTotal = 0;
        int totRecords = 0;
        String delim = Constants.getProperty("DelimiterforuploadRouting");
        final Date currentDate = new Date(System.currentTimeMillis());
        if (BTSLUtil.isNullString(delim)) {
            delim = " ";
        }
        BulkSubscriberRoutingErrorList errorVO;
        PreparedStatement insertPstmt = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtSelectExtID = null;
        ResultSet rsSelectExtId = null;
        String extId = null;
        String interfaceID = null;
        String tempStr = null;
        int msisdnAdded = 0;
        int msisdnUpdated = 0;
        int queryResult = -1;
        final StringBuffer insertSql = new StringBuffer("INSERT INTO subscriber_routing(msisdn, interface_id, ");
        insertSql.append("subscriber_type, external_interface_id, status, created_by, created_on, modified_by, ");
        insertSql.append("modified_on, text1, text2) VALUES (?,?,?,?,?,?,?,?,?,?,?) ");
        final StringBuffer updateQuery = new StringBuffer();
        updateQuery.append("UPDATE subscriber_routing SET interface_id = ?, external_interface_id = ?, ");
        updateQuery.append("status = ?, modified_by = ?, modified_on = ?, text1 = ?, text2 = ? ");
        updateQuery.append("WHERE msisdn =? AND subscriber_type=? ");

        final String selectExtId = "SELECT external_id FROM interfaces WHERE interface_id=?";

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Query=" + updateQuery);
        }
        try {
            String labelName = null;
            String labelValue = null;
            final String[] lineNumberArray = new String[2];
            boolean fileMoved = false;
            StringTokenizer startparser = null;
            insertPstmt = con.prepareStatement(insertSql.toString());
            pstmtUpdate = con.prepareStatement(updateQuery.toString());
            pstmtSelectExtID = con.prepareStatement(selectExtId);// Added by
                fileReader = new FileReader("" + filename);
                if (fileReader != null) {
                    bufferReader = new BufferedReader(fileReader);
                } else {
                    bufferReader = null;
                }
            if (bufferReader != null && bufferReader.ready()) 
            {
                tempStr = bufferReader.readLine();
                if (BTSLUtil.isNullString(tempStr)) {
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.ROUTING_ROUTINGUPLOADFILE_ERROR_INNAMEMISSING);
                }

                startparser = new StringTokenizer(tempStr, "=");
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Input=" + tempStr);
                    _log.debug(methodName, "There are " + startparser.countTokens() + "entries");
                }
                if (startparser.countTokens() != 2) {
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.ROUTING_ROUTINGUPLOADFILE_ERROR_INNAMEMISSING);

                }
                if (startparser.countTokens() < 3) {
                    try {
                        interfaceID = interfaceStr.substring(interfaceStr.indexOf("|") + 1).trim();
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "interfaceID::" + interfaceID);
                        }
                        pstmtSelectExtID.setString(1, interfaceID);
                        rsSelectExtId = pstmtSelectExtID.executeQuery();
                        if (rsSelectExtId.next()) {
                            extId = rsSelectExtId.getString("external_id");
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "extId::" + extId);
                            }
                        }
                        if (BTSLUtil.isNullString(extId)) {
                            throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.ROUTING_ROUTINGUPLOADFILE_ERROR_NOEXTIDFORINTERFACE, new String[] { interfaceID });
                        }
                        while (startparser.hasMoreTokens()) {
                            labelName = startparser.nextToken().trim();
                            labelValue = startparser.nextToken().trim();
                            if (!Constants.getProperty("routing.routinguploadfile.inname").equalsIgnoreCase(labelName)) {
                                throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.ROUTING_ROUTINGUPLOADFILE_ERROR_INNAMEFIRSTLINE);
                            }
                            if (BTSLUtil.isNullString(labelValue)) {
                                throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.ROUTING_ROUTINGUPLOADFILE_ERROR_INNAMEVALUEFIRSTLINE);
                            } else if (labelValue.length() > 10) {
                                throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.ROUTING_ROUTINGUPLOADFILE_ERROR_INNAMEVALUELENGTH);
                            }
                            if (!extId.trim().equals(labelValue)) {
                                throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.ROUTING_ROUTINGUPLOADFILE_ERROR_EXTIDNOTEXIST, new String[] { labelValue });
                            }
                        }
                    } catch (NoSuchElementException e) {
                        _log.errorTrace(methodName, e);
                        lineNumberArray[0] = "1";
                        throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.ROUTING_ROUTINGUPLOADFILE_ERROR_NOTGETRECORD, lineNumberArray);
                    }
                } else {
                    lineNumberArray[0] = "1";
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.ROUTING_ROUTINGUPLOADFILE_ERROR_IMPROPERSYNTAX, lineNumberArray);
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
                ArrayList<String> mobilenumberlist = new ArrayList();
                while ((tempStr = bufferReader.readLine()) != null) {
                    errorVO = new BulkSubscriberRoutingErrorList();
                    if (tempStr.trim().length() == 0) {
                        continue;
                    }
                    recordsTotal++; // Keeps track of line number
                    try {
                        startparser = new StringTokenizer(tempStr, delim);
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "Input = " + tempStr);
                            _log.debug(methodName, "There are " + startparser.countTokens() + " entries");
                        }
                        msisdn = startparser.nextToken().trim();
                        filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(msisdn);
                        if(mobilenumberlist.contains(msisdn)){
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "duplicate mobile number in the list" + msisdn);
                            }
                            RoutingFileProcessLog.log("File Upload", userID, msisdn, recordsTotal, "duplicate mobile number in the list", "Fail", filename + "," + locationCode);
                            invalidMsisdn.append(msisdn);
                            invalidMsisdn.append(",");
                            errorVO.setMsisdn(msisdn);
                            errorVO.setLineNumber(Long.toString(recordsTotal));
                            errorVO.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RSC_DUPLICATE_MSISDN, null));
                            errorList.add(errorVO);
                            continue;
                        }else{
                            mobilenumberlist.add(msisdn);
                        }
                        if (!BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "Not a valid MSISDN" + msisdn);
                            }
                            RoutingFileProcessLog.log("File Upload", userID, msisdn, recordsTotal, "Not a valid MSISDN", "Fail", filename + "," + locationCode);
                            invalidMsisdn.append(msisdn);
                            invalidMsisdn.append(",");
                            errorVO.setMsisdn(msisdn);
                            errorVO.setLineNumber(Long.toString(recordsTotal));
                            errorVO.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RSC_INVALID_MSISDN, null));
                            errorList.add(errorVO);
                            continue;
                        }
                        if(isMsisdnExist(con, msisdn, subsType)){
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "Mobile number already exist in the system" + msisdn);
                            }
                            RoutingFileProcessLog.log("File Upload", userID, msisdn, recordsTotal, "Mobile number already exist in the system", "Fail", filename + "," + locationCode);
                            invalidMsisdn.append(msisdn);
                            invalidMsisdn.append(",");
                            errorVO.setMsisdn(msisdn);
                            errorVO.setLineNumber(Long.toString(recordsTotal));
                            errorVO.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST, null));
                            errorList.add(errorVO);
                            continue;
                        }
                        msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);

                        if (networkPrefixVO == null) {
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "Not supporting Network" + msisdn);
                            }
                            RoutingFileProcessLog.log("File Upload", userID, msisdn, recordsTotal, "Not supporting Network", "Fail", filename + "," + locationCode);
                            invalidMsisdn.append(msisdn);
                            invalidMsisdn.append(",");
                            errorVO.setMsisdn(msisdn);
                            errorVO.setLineNumber(Long.toString(recordsTotal));
                            errorVO.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NETWORK_PREFIX_NOT_FOUND, null));
                            errorList.add(errorVO);
                            continue;
                        }
                        networkCode = networkPrefixVO.getNetworkCode();
                        if (!networkCode.equals(locationCode)) {
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "Not supporting Network" + msisdn);
                            }
                            RoutingFileProcessLog.log("File Upload", userID, msisdn, recordsTotal, "Not supporting Network", "Fail", filename + "," + locationCode);
                            invalidMsisdn.append(msisdn);
                            invalidMsisdn.append(",");
                            errorVO.setMsisdn(msisdn);
                            errorVO.setLineNumber(Long.toString(recordsTotal));
                            errorVO.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UNSUPPORTED_NETWORK, null));
                            errorList.add(errorVO);
                            continue;
                        }
                        try {
                            i = 1;
                            insertPstmt.setString(i++, filteredMsisdn);
                            insertPstmt.setString(i++, interfaceCode);
                            insertPstmt.setString(i++, subsType);
                            insertPstmt.setString(i++, labelValue);
                            insertPstmt.setString(i++, PretupsI.STATUS_ACTIVE);
                            insertPstmt.setString(i++, userID);
                            insertPstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                            insertPstmt.setString(i++, userID);
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
                            queryResult = insertPstmt.executeUpdate();
                            if (queryResult <= 0) {
                                RoutingFileProcessLog.log("File Upload", userID, msisdn, recordsTotal, "Error while inserting record", "Fail",
                                        filename + "," + locationCode);
                                errorVO.setMsisdn(msisdn);
                                errorVO.setLineNumber(Long.toString(recordsTotal));
                                errorVO.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_INSERTING_RECORD, null));
                                errorList.add(errorVO);
                            } else {
                                con.commit();
                                msisdnAdded += queryResult;
                                totRecords++; // Total records count to be
                                RoutingFileProcessLog.log("File Upload", userID, msisdn, recordsTotal, "Record inserted", "Pass", filename + "," + locationCode);
                            }
                            insertPstmt.clearParameters();

                        }// end of try
                        catch (SQLException sqe) {
                            if (sqe.getErrorCode() == 00001) {
                                if (sqe.getMessage().indexOf("PK_SUBSCRIBER_ROUTING") > 0) {
                                    if (_log.isDebugEnabled()) {
                                        _log.debug(methodName, "MSISDN already Exist" + msisdn);
                                    }
                                    RoutingFileProcessLog.log("File Upload", userID, msisdn, recordsTotal, "MSISDN already Exist so updating record", "Pass",
                                            filename + "," + locationCode);
                                    i = 1;
                                    pstmtUpdate.setString(i++, interfaceCode);
                                    pstmtUpdate.setString(i++, labelValue);
                                    pstmtUpdate.setString(i++, PretupsI.STATUS_ACTIVE);
                                    pstmtUpdate.setString(i++, userID);
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
                                    pstmtUpdate.setString(i++, subsType);
                                    queryResult = pstmtUpdate.executeUpdate();
                                    if (queryResult <= 0) {
                                        RoutingFileProcessLog.log("File Upload", userID, msisdn, recordsTotal, "Error while updating record", "Fail",
                                                filename + "," + locationCode);
                                    } else {
                                        msisdnUpdated += queryResult;
                                        totRecords++;// total number of records
                                        RoutingFileProcessLog.log("File Upload", userID, msisdn, recordsTotal, "Record updated", "Pass", filename + "," + locationCode);
                                    }
                                    pstmtUpdate.clearParameters();

                                }// end of if(checking constraint name)
                            }// end of if(checking error code)
                            else {
                                _log.errorTrace(methodName, sqe);
                                RoutingFileProcessLog.log("File Upload", userID, msisdn, recordsTotal, "SQLException=" + sqe.getMessage(), "Exception",
                                        filename + "," + locationCode);
                            }
                        }// end of catch
                    }// end of try(just inside the while loop)
                    catch (BTSLBaseException be) {
                        throw be;
                    } // end of catch
                    catch (Exception e) {
                        _log.errorTrace(methodName, e);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "",
                            "", "", "Exception:" + e.getMessage());
                        throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
                    }// end of catch
                } // end of While
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Total lines in File " + recordsTotal + " And Total Records for process:" + totRecords);
                }
            }// end of bufferedReader !=null
            if(invalidMsisdn == null || invalidMsisdn.isEmpty()) {
                if (recordsTotal == 0) {
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.N0_RECORD);
                }
                if (totRecords == 0) {
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.ROUTING_ROUTINGUPLOADFILE_ERROR_NORECORDS);
                }
            }
            // Moving File after Processing
            if (msisdnAdded > 0 || msisdnUpdated > 0) {
                fileMoved = this.moveRoutingFileToArchive(filename, file);
                if (fileMoved) {
                    processFile = true;
                } else {
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.FILE_CANNOT_MOVE);
                }
            }
        }// end of try(outer most try)
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
        } finally {
            try {
                if (bufferReader != null) {
                    bufferReader.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (insertPstmt != null) {
                    insertPstmt.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (rsSelectExtId != null) {
                    rsSelectExtId.close();
                }
            } catch (Exception exp) {
                _log.errorTrace(methodName, exp);
            }
            try {
                if (pstmtSelectExtID != null) {
                    pstmtSelectExtID.close();
                }
            } catch (Exception ex1) {
                _log.errorTrace(methodName, ex1);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "processed till record no:" + totRecords);
            }
            if (!processFile) {
                totRecords = 0;
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("p_userID : ");
            	msg.append(userID);
            	msg.append(", p_locationCode : ");
            	msg.append(locationCode);
            	msg.append(", Processed : ");
            	msg.append(file);
            	msg.append(", No of records : ");
            	msg.append(totRecords);      	
            	msg.append(", Status : ");
            	msg.append(processFile); 
            	String message=msg.toString();
                _log.debug(methodName,message);
            }
            RoutingFileProcessLog.log("File Upload", userID, null, totRecords, "Records uploaded to the database", "Finally Block", filename + "," + locationCode);
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting updateCount : ");
            	msg.append(msisdnAdded);
            	msg.append(", invalidMsisdn : ");
            	msg.append(invalidMsisdn);
            	String message=msg.toString();
                _log.debug(methodName, message);
            }
        }
        if (msisdnUpdated > 0) {
            msisdnAdded += msisdnUpdated;
        }
        return msisdnAdded;
    }

    private boolean moveRoutingFileToArchive(String filePathAndFileName, String fileName) throws BTSLBaseException, IOException {
        if (_log.isDebugEnabled()) {
            _log.debug("moveFileToArchive", " Entered filePathAndFileName=" + filePathAndFileName + " fileName=" + fileName);
        }
        File fileRead = new File(filePathAndFileName);
        String archivalPath = Constants.getProperty("RoutingArchiveFilePath");
        if (BTSLUtil.isNullString(archivalPath)) {
            if (_log.isDebugEnabled()) {
                _log.debug("processUploadedBlackListFile", "Archival File path not defined in Constant Property file");
            }
            throw new BTSLBaseException(this, "moveFileToArchive", "restrictedsubs.blacklisting.error.archivalpathnotfound", "multipleSubsSel");
        }
        File fileArchive = new File(String.valueOf(Constants.getProperty("RoutingArchiveFilePath")));
        if (!fileArchive.isDirectory()) {
            fileArchive.mkdirs();
        }
        fileArchive = new File(String.valueOf( Constants.getProperty("RoutingArchiveFilePath")) + fileName + "." + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()); // to

        File archfile = new File(archivalPath+fileName);
        Path sourceDir = Paths.get(filePathAndFileName);
        Path distDir = Paths.get(archivalPath+fileName);
        Files.copy(sourceDir, distDir, StandardCopyOption.REPLACE_EXISTING);
        boolean flag = archfile.renameTo(fileArchive);
        if (_log.isDebugEnabled()) {
            _log.debug("moveRoutingFileToArchive", " Exiting File Moved=" + flag);
        }
        return flag;
    }// end of moveFileToArchive


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
        final String methodName = "moveFileToArchive";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered ");
        }
        final File fileRead = new File(p_fileName);
        File fileArchive = new File("" + Constants.getProperty("RoutingArchiveFilePath"));
        if (!fileArchive.isDirectory()) {
            fileArchive.mkdirs();
        }

        StringBuffer filename=new StringBuffer("");
        filename.append(Constants.getProperty("RoutingArchiveFilePath"));
        filename.append(p_file);
        filename.append(".");
        filename.append(BTSLUtil.getTimestampFromUtilDate(new Date()).getTime());        	
    	
        fileArchive = new File(filename.toString()); // to
        // make
        // the
        // new
        // file
        // name
        final boolean flag = fileRead.renameTo(fileArchive);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting File Moved=" + flag);
        }
        return flag;
    }// end of moveFileToArchive

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
        final String methodName = "writingFileToDatabase";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_filename : ");
        	msg.append(p_filename);
        	msg.append(", p_serviceType : ");
        	msg.append(p_serviceType);
        	msg.append(", p_serviceClassCode : ");
        	msg.append(p_serviceClassCode);
        	msg.append(", p_status : ");
        	msg.append(p_status);
        	msg.append(", p_module : ");
        	msg.append(p_module);
        	msg.append(", p_userType : ");
        	msg.append(p_userType);
        	msg.append(", p_invalidMsisdn : ");
        	msg.append(p_invalidMsisdn.toString());
        	msg.append(", p_userID : ");
        	msg.append(p_userID);
        	msg.append(", p_locationCode : ");
        	msg.append(p_locationCode);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        boolean processFile = false;
        FileReader fileReader = null; // file reader
        BufferedReader bufferReader = null;
        long recordsTotal = 0;
        int totRecords = 0;
        String delim = null;
        Date currentDate = null;
        PreparedStatement insertPstmt = null;
        PreparedStatement pstmtUpdate = null;
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
        final StringBuffer insertSql = new StringBuffer("INSERT INTO srv_class_mapped_msisdn(msisdn, service_type, ");
        insertSql.append("service_class_code, status, created_on, created_by, modified_on, ");
        insertSql.append("modified_by, module, user_type) VALUES (?,?,?,?,?,?,?,?,?,?) ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Query=" + insertSql.toString());
        }
        final StringBuffer updateQuery = new StringBuffer();
        updateQuery.append("UPDATE srv_class_mapped_msisdn SET status = ?, ");
        updateQuery.append("modified_on = ?, modified_by = ? ");
        updateQuery.append("WHERE msisdn =? AND service_type =? AND service_class_code =? AND module =? ");
        updateQuery.append("AND user_type=?");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Query=" + updateQuery.toString());
        }
        try {
            delim = Constants.getProperty("DelimiterforuploadRouting");
            if (BTSLUtil.isNullString(delim)) {
                delim = " ";
            }
            currentDate = new Date(System.currentTimeMillis());
            insertPstmt = p_con.prepareStatement(insertSql.toString());
            pstmtUpdate = p_con.prepareStatement(updateQuery.toString());
            fileReader = new FileReader("" + p_filename);
            bufferReader = new BufferedReader(fileReader);
            if (bufferReader != null && bufferReader.ready()) // If File Not
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
                    final String msisdnArray[] = tempStr.split(delim);
                    for (j = 0, length = msisdnArray.length; j < length; j++) {
                        msisdn = msisdnArray[j];
                        if (msisdn.trim().length() == 0) {
                            continue;
                        }
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
                                if (_log.isDebugEnabled()) {
                                    _log.debug(methodName, "Not a valid MSISDN" + msisdn);
                                }
                                RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status, "Not a valid MSISDN", "Fail",
                                    p_locationCode);
                                p_invalidMsisdn.append(msisdn);
                                p_invalidMsisdn.append(",");
                                continue;
                            }
                            msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                            if (networkPrefixVO == null) {
                                if (_log.isDebugEnabled()) {
                                    _log.debug(methodName, "Not supporting Network" + msisdn);
                                }
                                RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status, "Not supporting Network", "Fail",
                                    p_locationCode);

                                p_invalidMsisdn.append(msisdn);
                                p_invalidMsisdn.append(",");
                                continue;
                            }
                            networkCode = networkPrefixVO.getNetworkCode();
                            if (!networkCode.equals(p_locationCode)) {
                                if (_log.isDebugEnabled()) {
                                    _log.debug(methodName, "Not supporting Network" + msisdn);
                                }
                                RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status, "Not supporting Network", "Fail",
                                    p_locationCode);
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
                                if (queryResult <= 0) {
                                    RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status, "Error while inserting record",
                                        "Fail", p_locationCode);
                                } else {
                                    msisdnAdded += queryResult;
                                    totRecords++; // Total records count to be
                                    // inserted or updated in
                                    // database
                                    RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status, "Record inserted", "Pass",
                                        p_locationCode);
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
                                        if (_log.isDebugEnabled()) {
                                            _log.debug(methodName, "MSISDN already Exist" + msisdn);
                                        }
                                        // make an entry in logs for updating
                                        // the record
                                        RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status,
                                            "MSISDN already Exist so updating record", "Pass", p_locationCode);
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
                                        if (queryResult <= 0) {
                                            RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status,
                                                "Error while updating record", "Fail", p_locationCode);
                                        } else {
                                            msisdnUpdated += queryResult;
                                            totRecords++;// total number of
                                            // records entered or
                                            // updated in database
                                            RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status, "Record updated", "Pass",
                                                p_locationCode);
                                        }

                                        // clears the parameters of
                                        // preparedstatement
                                        pstmtUpdate.clearParameters();

                                    }// end of if(checking constraint name)
                                }// end of if(checking error code)
                                else {
                                    _log.errorTrace(methodName, sqe);
                                    RoutingFileProcessLog.log(p_module, msisdn, p_userType, p_serviceType, p_serviceClassCode, p_status, "SQLException=" + sqe.getMessage(),
                                        "Exception", p_locationCode);
                                }
                            }// end of catch
                        }// end of try(just inside the while loop)
                        catch (BTSLBaseException be) {
                            throw be;
                        } // end of catch
                        catch (Exception e) {
                            _log.errorTrace(methodName, e);
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[writingFileToDatabase]",
                                "", "", "", "Exception:" + e.getMessage());
                            throw new BTSLBaseException(this, methodName, "error.general.processing");
                        }// end of catch
                    }
                }// end of While
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Total lines in File " + recordsTotal + " And Total Records for process:" + totRecords);
                }
            }// end of bufferedReader !=null
            if (recordsTotal == 0) {
                throw new BTSLBaseException(this, methodName, "srvrouting.routinguploadfile.error.zerofilesize");
            }
            if (totRecords == 0) {
                throw new BTSLBaseException(this, methodName, "srvrouting.routinguploadfile.error.norecords");
            }
            // Moving File after Processing
            if (msisdnAdded > 0 || msisdnUpdated > 0) {
                fileMoved = this.moveFileToArchive(p_filename, p_file);
                if (fileMoved) {
                    processFile = true;
                } else {
                    throw new BTSLBaseException(this, methodName, "srvrouting.routinguploadfile.error.filenomove");
                }
            }
        }// end of try(outer most try)
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[writingFileToDatabase]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[writingFileToDatabase]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (bufferReader != null) {
                    bufferReader.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (insertPstmt != null) {
                    insertPstmt.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "processed till record no:" + totRecords);
            }
            // Write in LOGS
            if (!processFile) {
                totRecords = 0;
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");            	
            	msg.append(", p_userID : ");
            	msg.append(p_userID);
            	msg.append(", p_locationCode : ");
            	msg.append(p_locationCode);
            	msg.append(", Processed : ");
            	msg.append(p_file);
            	msg.append(", No of records : ");
            	msg.append(totRecords);
            	msg.append(", Status : ");
            	msg.append(processFile);            
            	
            	String message=msg.toString();
                _log.debug(methodName, message);
            }
            RoutingFileProcessLog.log(p_module, null, p_userType, p_serviceType, p_serviceClassCode, p_status, "Records uploaded to the database", "Finally Block",
                p_locationCode);
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");            	
            	msg.append("Exiting updateCount : ");
            	msg.append(msisdnAdded);
            	msg.append(", p_invalidMsisdn : ");
            	msg.append(p_invalidMsisdn);           
            	
            	String message=msg.toString();
                _log.debug(methodName, message);
            }
        }// end of finally
         // adding up the updated and inserted record count and return that
        if (msisdnUpdated > 0) {
            msisdnAdded += msisdnUpdated;
        }
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
        final String methodName = "deletingMsisdn";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered : p_inMsisdn : ");
        	msg.append(p_inMsisdn.toString());
        	msg.append(", p_outMsisdn : ");
        	msg.append(p_outMsisdn.toString());        	
        	msg.append(", p_serviceType : ");
        	msg.append(p_serviceType);
        	msg.append(", p_serviceClassCode : ");
        	msg.append(p_serviceClassCode);
        	msg.append(", p_module : ");
        	msg.append(p_module);
        	msg.append(", p_userType : ");
        	msg.append(p_userType);
        	msg.append(", p_userID : ");
        	msg.append(p_userID);      	
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement delPstmt = null;
        int deleteCount = 0;
        int i = 1;
        int outIndex = 0, index = 0;
        try {
            final StringBuffer deleteQueryBuff = new StringBuffer("DELETE FROM srv_class_mapped_msisdn ");
            deleteQueryBuff.append("WHERE msisdn=? AND service_type=? AND service_class_code=? AND module=? AND user_type=?");
            final String deleteQuery = deleteQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "update query:" + deleteQuery);
            }
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
                    if (deleteCount > 0) {
                        RoutingFileProcessLog.log(p_module, p_inMsisdn[index], p_userType, p_serviceType, p_serviceClassCode, p_status, "Record deleted successfully", "Pass",
                            null);
                    } else {
                        RoutingFileProcessLog.log(p_module, p_inMsisdn[index], p_userType, p_serviceType, p_serviceClassCode, p_status, "Record can not be deleted", "Fail",
                            null);
                    }
                    i = 1;
                    delPstmt.clearParameters();
                }
            }// end of for
        }// end of try
        catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[deletingMsisdn]", "", "", "",
                "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberControlDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[deletingMsisdn]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (delPstmt != null) {
                    delPstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting updateCount:" + deleteCount);
            }
            RoutingFileProcessLog.log(p_module, null, p_userType, p_serviceType, p_serviceClassCode, p_status,
                index - outIndex + " out of " + index + " records deleted successfully", null, "Inside finally block");
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
        final String methodName = "isMsisdnExist";
        int i = 1;
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered : p_Msisdn : ");
        	msg.append(p_Msisdn);       	
        	msg.append(", p_serviceType : ");
        	msg.append(p_serviceType);
        	msg.append(", p_serviceClassCode : ");
        	msg.append(p_serviceClassCode);
        	msg.append(", p_module : ");
        	msg.append(p_module);
        	msg.append(", p_userTypeDesc : ");
        	msg.append(p_userTypeDesc);
        	msg.append(", p_status : ");
        	msg.append(p_status);      	
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuffer strBuff = new StringBuffer("SELECT 1");
        strBuff.append(" FROM srv_class_mapped_msisdn");
        strBuff.append(" WHERE msisdn=? AND service_type=? AND service_class_code=? AND module=? AND user_type=? AND status=?");
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("isMsisdnExisting", "Select Query= " + strBuff.toString());
            }
            pstmt = p_con.prepareStatement(strBuff.toString());
            pstmt.setString(i++, p_Msisdn);
            pstmt.setString(i++, p_serviceType);
            pstmt.setString(i++, p_serviceClassCode);
            pstmt.setString(i++, p_module);
            pstmt.setString(i++, p_userTypeDesc);
            pstmt.setString(i++, p_status);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        }// end of try
        catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[isMsisdnExisting]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isMsisdnExisting", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingWebDAO[isMsisdnExisting]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "isMsisdnExisting", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("isMsisdnExisting", "Exiting: existFlag=" + existFlag);
            }
        }// end of finally.
    }
}
