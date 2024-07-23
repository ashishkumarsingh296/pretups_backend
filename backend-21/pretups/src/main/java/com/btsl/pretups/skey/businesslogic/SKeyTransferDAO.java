package com.btsl.pretups.skey.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
// commented for DB2
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/*
 * SKeyTransferDAO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class to handle the database interaction for S key related tables
 */

public class SKeyTransferDAO {

    private static Log _log = LogFactory.getLog(SKeyTransferDAO.class.getName());

    /**
     * This method loads the SKey Transfer details of the particular msisdn
     * 
     * @param p_con
     * @param p_msisdn
     * @return SKeyTransferVO
     * @throws SQLException
     * @throws Exception
     */
    public SKeyTransferVO loadSKeyTransferDetails(Connection p_con, String p_msisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadSKeyTransferDetails", "Entered p_msisdn:" + p_msisdn);
        }
        final String METHOD_NAME = "loadSKeyTransferDetails";
        PreparedStatement pstmtSelect = null;
        SKeyTransferVO sKeyTransferVO = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT sender_id,module, service_type, sender_type, skey, receiver_msisdn, buddy,transfer_value, ");
            selectQueryBuff.append(" payment_method, request_date, request_on, request_by,skey_sent_to_msisdn,default_payment ");
            selectQueryBuff.append(" FROM skey_transfers ");
            // DB220120123for update WITH RS
            // selectQueryBuff.append(" WHERE sender_msisdn=? FOR UPDATE NOWAIT ");

            if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
                selectQueryBuff.append(" WHERE sender_msisdn=? FOR UPDATE WITH RS ");
            } else {
                selectQueryBuff.append(" WHERE sender_msisdn=? FOR UPDATE NOWAIT ");
            }
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("loadSKeyTransferDetails", "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_msisdn);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                sKeyTransferVO = new SKeyTransferVO();
                sKeyTransferVO.setSenderID(rs.getString("sender_id"));
                sKeyTransferVO.setSenderMsisdn(p_msisdn);
                sKeyTransferVO.setModule(rs.getString("module"));
                sKeyTransferVO.setServiceType(rs.getString("service_type"));
                sKeyTransferVO.setSenderType(rs.getString("sender_type"));
                sKeyTransferVO.setSkey(rs.getLong("skey"));
                sKeyTransferVO.setRecieverMsisdn(rs.getString("receiver_msisdn"));
                sKeyTransferVO.setBuddy(rs.getString("buddy"));
                sKeyTransferVO.setTransferValue(rs.getLong("transfer_value"));
                sKeyTransferVO.setRequestDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("request_date")));
                sKeyTransferVO.setRequestOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("request_on")));
                sKeyTransferVO.setRequestBy(rs.getString("request_by"));
                sKeyTransferVO.setSkeySentToMsisdn(rs.getString("skey_sent_to_msisdn"));
                sKeyTransferVO.setDefaultPaymentMethod(rs.getString("default_payment"));
            }
            return sKeyTransferVO;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadSKeyTransferDetails", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SKeyTransferDAO[loadSKeyTransferDetails]", "", p_msisdn, "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadSKeyTransferDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadSKeyTransferDetails", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SKeyTransferDAO[loadSKeyTransferDetails]", "", p_msisdn, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadSKeyTransferDetails", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadSKeyTransferDetails", "Exiting sKeyTransferVO:" + sKeyTransferVO);
            }
        }// end of finally
    }

    /**
     * This method add the Skey transfer details
     * 
     * @param p_con
     * @param p_sKeyTransferVO
     * @return int
     * @throws SQLException
     * @throws Exception
     */
    public int addSKeyTransferDetails(Connection p_con, SKeyTransferVO p_sKeyTransferVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("addSKeyTransferDetails", "Entered p_sKeyTransferVO:" + p_sKeyTransferVO.toString());
        }
        final String METHOD_NAME = "addSKeyTransferDetails";
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            int i = 1;
            StringBuffer insertQueryBuff = new StringBuffer(" INSERT INTO skey_transfers(sender_id, sender_msisdn, module, service_type, sender_type, skey, receiver_msisdn, buddy,transfer_value, ");
            insertQueryBuff.append(" payment_method, request_date, request_on, request_by,skey_sent_to_msisdn,default_payment) ");
            insertQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            String insertQuery = insertQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("addSKeyTransferDetails", "insert query:" + insertQuery);
            }
            pstmtInsert = p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(i++, p_sKeyTransferVO.getSenderID());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getSenderMsisdn());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getModule());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getServiceType());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getSenderType());
            pstmtInsert.setLong(i++, p_sKeyTransferVO.getSkey());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getRecieverMsisdn());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getBuddy());
            pstmtInsert.setLong(i++, p_sKeyTransferVO.getTransferValue());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getPaymentMethod());
            pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_sKeyTransferVO.getRequestDate()));
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_sKeyTransferVO.getRequestOn()));
            pstmtInsert.setString(i++, p_sKeyTransferVO.getRequestBy());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getSkeySentToMsisdn());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getDefaultPaymentMethod());
            addCount = pstmtInsert.executeUpdate();
            return addCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error("addSKeyTransferDetails", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SKeyTransferDAO[addSKeyTransferDetails]", "", p_sKeyTransferVO.getSenderMsisdn(), "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addSKeyTransferDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("addSKeyTransferDetails", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SKeyTransferDAO[addSKeyTransferDetails]", "", p_sKeyTransferVO.getSenderMsisdn(), "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addSKeyTransferDetails", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("addSKeyTransferDetails", "Exiting addCount=" + addCount);
            }
        }// end of finally
    }

    /**
     * This method add the SKey related details in the history table
     * 
     * @param p_con
     * @param p_sKeyTransferVO
     * @return int
     * @throws SQLException
     * @throws Exception
     */
    public int addSKeyTransferHistoryDetails(Connection p_con, SKeyTransferVO p_sKeyTransferVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("addSKeyTransferHistoryDetails", "Entered p_sKeyTransferVO:" + p_sKeyTransferVO.toString());
        }
        final String METHOD_NAME = "addSKeyTransferHistoryDetails";
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            int i = 1;
            StringBuffer insertQueryBuff = new StringBuffer(" INSERT INTO skey_transfer_history(sender_id, sender_msisdn, module, service_type, sender_type, skey, receiver_msisdn, buddy,transfer_value, ");
            insertQueryBuff.append(" payment_method, request_date, request_on, request_by,transfer_id,transfer_status,created_on,status,previous_status,skey_sent_to_msisdn,default_payment ) ");
            insertQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            String insertQuery = insertQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("addSKeyTransferHistoryDetails", "Insert query:" + insertQuery);
            }
            pstmtInsert = p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(i++, p_sKeyTransferVO.getSenderID());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getSenderMsisdn());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getModule());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getServiceType());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getSenderType());
            pstmtInsert.setLong(i++, p_sKeyTransferVO.getSkey());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getRecieverMsisdn());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getBuddy());
            pstmtInsert.setLong(i++, p_sKeyTransferVO.getTransferValue());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getPaymentMethod());
            pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_sKeyTransferVO.getRequestDate()));
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_sKeyTransferVO.getRequestOn()));
            pstmtInsert.setString(i++, p_sKeyTransferVO.getRequestBy());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getTransferID());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getTransferStatus());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_sKeyTransferVO.getCreatedOn()));
            pstmtInsert.setString(i++, p_sKeyTransferVO.getStatus());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getPreviousStatus());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getSkeySentToMsisdn());
            pstmtInsert.setString(i++, p_sKeyTransferVO.getDefaultPaymentMethod());
            addCount = pstmtInsert.executeUpdate();
            return addCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error("addSKeyTransferHistoryDetails", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SKeyTransferDAO[addSKeyTransferHistoryDetails]", p_sKeyTransferVO.getTransferID(), p_sKeyTransferVO.getSenderMsisdn(), "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addSKeyTransferHistoryDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("addSKeyTransferHistoryDetails", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SKeyTransferDAO[addSKeyTransferHistoryDetails]", p_sKeyTransferVO.getTransferID(), p_sKeyTransferVO.getSenderMsisdn(), "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addSKeyTransferHistoryDetails", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("addSKeyTransferHistoryDetails", "Exiting addCount=" + addCount);
            }
        }// end of finally
    }

    /**
     * Method to delete the skey from main table and move the contents to
     * history table
     * 
     * @param p_con
     * @param p_skeyTransferVO
     * @return
     * @throws BTSLBaseException
     */
    public int deleteSkeyTransferDetails(Connection p_con, SKeyTransferVO p_skeyTransferVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("deleteSkeyTransferDetails", "Entered p_skeyTransferVO:" + p_skeyTransferVO.toString());
        }
        final String METHOD_NAME = "deleteSkeyTransferDetails";
        PreparedStatement pstmtDelete = null;
        int deleteCount = 0;
        int addCount = 0;
        try {
            StringBuffer selectQueryBuff = new StringBuffer(" DELETE FROM  skey_transfers ");
            selectQueryBuff.append(" WHERE sender_msisdn=? AND skey=? ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("deleteSkeyTransferDetails", "select query:" + selectQuery);
            }
            pstmtDelete = p_con.prepareStatement(selectQuery);
            pstmtDelete.setString(1, p_skeyTransferVO.getSenderMsisdn());
            pstmtDelete.setLong(2, p_skeyTransferVO.getSkey());
            deleteCount = pstmtDelete.executeUpdate();
            if (deleteCount != 0) {
                addCount = addSKeyTransferHistoryDetails(p_con, p_skeyTransferVO);
                if (addCount <= 0) {
                    throw new BTSLBaseException("PretupsBL", "generateSKey", PretupsErrorCodesI.SKEY_NOTADDHISTORY);
                }
            }

            return deleteCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error("deleteSkeyTransferDetails", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SKeyTransferDAO[deleteSkeyTransferDetails]", "", p_skeyTransferVO.getSenderMsisdn(), "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteSkeyTransferDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("deleteSkeyTransferDetails", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SKeyTransferDAO[deleteSkeyTransferDetails]", "", p_skeyTransferVO.getSenderMsisdn(), "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteSkeyTransferDetails", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtDelete != null) {
                    pstmtDelete.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("deleteSkeyTransferDetails", "Exiting with deleteCount:" + deleteCount);
            }
        }// end of finally
    }
}
