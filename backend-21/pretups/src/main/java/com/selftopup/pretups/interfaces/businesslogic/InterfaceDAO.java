/*
 * @# InterfaceDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * June 10, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.selftopup.pretups.interfaces.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

// commented for DB2 import oracle.jdbc.OraclePreparedStatement;

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
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

public class InterfaceDAO {

    private Log _log = LogFactory.getFactory().getInstance(InterfaceDAO.class.getName());

    /**
     * Constructor for InterfaceDAO.
     */
    public InterfaceDAO() {
        super();
    }

    /**
     * Method loadInterfaceTypeId.
     * This method is used to load interface type id from interface_types table
     * If there is any error then throws the SQLException
     * 
     * @param p_con
     *            Connection
     * @param p_interfaceCategory
     *            String
     * @return interfaceTypeIdList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadInterfaceTypeId(Connection p_con, String p_interfaceCategory) throws BTSLBaseException {
        final String methodName = "loadInterfaceTypeId";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_interfaceCategory=" + p_interfaceCategory);

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ListValueVO listValueBean = null;
        ArrayList interfaceTypeIdList = new ArrayList();
        StringBuffer strBuff = new StringBuffer("SELECT interface_type_id,interface_name");
        strBuff.append(" FROM interface_types WHERE interface_type_id=?");
        strBuff.append(" ORDER BY interface_type_id");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Select Query= " + sqlSelect);

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_interfaceCategory);
            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Query Executed= " + sqlSelect);
            while (rs.next()) {
                listValueBean = new ListValueVO(rs.getString("interface_name"), rs.getString("interface_type_id"));
                interfaceTypeIdList.add(listValueBean);
            }
        }

        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceTypeId]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceTypeId]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting size=" + interfaceTypeIdList.size());
        }

        return interfaceTypeIdList;
    }

    /**
     * Method loadInterfaceDetails.
     * This method is used to load interface details into ArrayList
     * If there is any error then throws the SQLException or Exception
     * 
     * @param p_con
     *            Connection
     * @param p_interfaceCategory
     *            String
     * @return interfaceDetails ArrayList
     */

    public ArrayList loadInterfaceDetails(Connection p_con, String p_interfaceCategory) throws BTSLBaseException {
        final String methodName = "loadInterfaceDetails";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_interfaceCategory= " + p_interfaceCategory);
        StringBuffer strBuff = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList interfaceDetails = new ArrayList();
        try {
            strBuff = new StringBuffer("SELECT masters.interface_name,");
            strBuff.append("detail.interface_description,masters.interface_type_id,");
            strBuff.append("lcat.lookup_name AS category,masters.interface_category,");
            strBuff.append("detail.interface_id,detail.external_id,");
            strBuff.append("detail.concurrent_connection,lstat.lookup_name AS statusDesc,");
            strBuff.append("detail.status,detail.single_state_transaction,");
            // strBuff.append("detail.validation_time_out,detail.update_time_out,");
            strBuff.append("detail.message_language1,detail.message_language2,");
            strBuff.append("detail.modified_on,detail.status_type, detail.val_expiry_time,detail.topup_expiry_time FROM interface_types");
            strBuff.append(" masters,interfaces detail,lookups lcat,lookups lstat");
            strBuff.append(" WHERE lstat.lookup_type=? AND lcat.lookup_type=? AND");
            strBuff.append(" lcat.lookup_code=masters.interface_category AND ");
            strBuff.append("lstat.lookup_code=detail.status AND masters.interface_type_id=?");
            strBuff.append("  AND masters.interface_type_id=detail.interface_type_id");
            String sqlLoad = strBuff.toString();
            pstmtSelect = p_con.prepareStatement(sqlLoad);
            pstmtSelect.setString(1, PretupsI.STATUS_TYPE);
            pstmtSelect.setString(2, PretupsI.INTERFACE_CATEGORY);
            pstmtSelect.setString(3, p_interfaceCategory);
            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "QUERY Executed= " + sqlLoad);
            InterfaceVO interfaceVO = null;
            int index = 0;

            while (rs.next()) {
                interfaceVO = new InterfaceVO();
                interfaceVO.setInterfaceName(rs.getString("interface_name"));
                interfaceVO.setInterfaceDescription(rs.getString("interface_description"));
                interfaceVO.setExternalId(rs.getString("external_id"));
                interfaceVO.setStatus(rs.getString("statusDesc"));
                interfaceVO.setStatusCode(rs.getString("status"));
                interfaceVO.setConcurrentConnection(rs.getInt("concurrent_connection"));
                interfaceVO.setInterfaceTypeId(rs.getString("interface_type_id"));
                interfaceVO.setInterfaceCategory(rs.getString("category"));
                interfaceVO.setInterfaceCategoryCode(rs.getString("interface_category"));
                interfaceVO.setInterfaceId(rs.getString("interface_id"));
                interfaceVO.setSingleStateTransaction(rs.getString("single_state_transaction"));
                // interfaceVO.setValidationTimeout(rs.getLong("validation_time_out"));
                // interfaceVO.setUpdateTimeOut(rs.getLong("update_time_out"));
                interfaceVO.setRadioIndex(index);
                interfaceVO.setLanguage1Message(rs.getString("message_language1"));
                interfaceVO.setLanguage2Message(rs.getString("message_language2"));
                interfaceVO.setLastModified(rs.getTimestamp("modified_on").getTime());
                interfaceVO.setStatusType(rs.getString("status_type"));
                interfaceVO.setValExpiryTime(rs.getLong("val_expiry_time"));
                interfaceVO.setTopUpExpiryTime(rs.getLong("topup_expiry_time"));
                interfaceDetails.add(interfaceVO);
                index++;
            }

        }

        catch (SQLException sqe) {
            _log.error(methodName, " SQL Exception " + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, " Exiting size=" + interfaceDetails.size());
        }
        return interfaceDetails;
    }

    /**
     * Method addInterfaceDetails.
     * This method is used to add the Details of Interfaces in the Interfaces
     * Table
     * 
     * @param p_con
     *            Connection
     * @param p_interfaceVO
     *            InterfaceVO
     * @return int
     * @throws BTSLBaseException
     */

    public int addInterfaceDetails(Connection p_con, InterfaceVO p_interfaceVO) throws BTSLBaseException {
        int addCount = -1;
        final String methodName = "addInterfaceDetails";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entering VO " + p_interfaceVO);
        // commented for DB2 OraclePreparedStatement pstmtInsert = null;
        PreparedStatement pstmtInsert = null;
        StringBuffer insertQueryBuff = new StringBuffer("INSERT INTO interfaces ");
        insertQueryBuff.append("(interface_id,external_id,interface_description,");
        insertQueryBuff.append("interface_type_id,status,clouser_date,message_language1,");
        insertQueryBuff.append("message_language2,concurrent_connection,single_state_transaction,");
        // insertQueryBuff.append("validation_time_out,update_time_out,");
        insertQueryBuff.append("created_on,created_by,modified_on,modified_by,status_type,val_expiry_time,topup_expiry_time)");
        insertQueryBuff.append(" VALUES(?, ?, ?, ?, ?, ?, ? ,? ,? ,? ,? ,? ,? ,? ,?,?,?)");
        String insertQuery = insertQueryBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Insert Query= " + insertQuery);
        try {
            // commented for DB2 pstmtInsert =
            // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
            pstmtInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(1, p_interfaceVO.getInterfaceId());
            pstmtInsert.setString(2, p_interfaceVO.getExternalId());
            // commented for DB2
            // pstmtInsert.setFormOfUse(3,OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(3, p_interfaceVO.getInterfaceDescription());
            pstmtInsert.setString(4, p_interfaceVO.getInterfaceTypeId());
            pstmtInsert.setString(5, p_interfaceVO.getStatusCode());
            pstmtInsert.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(p_interfaceVO.getClosureDate()));
            pstmtInsert.setString(7, p_interfaceVO.getLanguage1Message());
            // commented for DB2
            // pstmtInsert.setFormOfUse(8,OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(8, p_interfaceVO.getLanguage2Message());
            pstmtInsert.setInt(9, p_interfaceVO.getConcurrentConnection());
            pstmtInsert.setString(10, p_interfaceVO.getSingleStateTransaction());
            // pstmtInsert.setLong(11,p_interfaceVO.getValidationTimeout());
            // pstmtInsert.setLong(12,p_interfaceVO.getUpdateTimeOut());
            pstmtInsert.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_interfaceVO.getCreatedOn()));
            pstmtInsert.setString(12, p_interfaceVO.getCreatedBy());
            pstmtInsert.setTimestamp(13, BTSLUtil.getTimestampFromUtilDate(p_interfaceVO.getCreatedOn()));
            pstmtInsert.setString(14, p_interfaceVO.getModifiedBy());
            if (BTSLUtil.isNullString(p_interfaceVO.getStatusType()))
                pstmtInsert.setString(15, PretupsI.INTERFACE_STATUS_TYPE_MANUAL);
            else
                pstmtInsert.setString(15, p_interfaceVO.getStatusType());
            pstmtInsert.setLong(16, p_interfaceVO.getValExpiryTime());
            pstmtInsert.setLong(17, p_interfaceVO.getTopUpExpiryTime());

            addCount = pstmtInsert.executeUpdate();
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[addInterfaceDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[addInterfaceDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, " Exiting addCount " + addCount);
        }

        return addCount;
    }

    /**
     * Method modifyInterfaceDetails.
     * This method is used to Modify the Details of Interfaces in the Interfaces
     * Table
     * 
     * @param p_con
     *            Connection
     * @param p_interfaceVO
     *            InterfaceVO
     * @return int
     * @throws BTSLBaseException
     */

    public int modifyInterfaceDetails(Connection p_con, InterfaceVO p_interfaceVO) throws BTSLBaseException {

        final String methodName = "modifyInterfaceDetails";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entering VO " + p_interfaceVO);

        int updateCount = -1;
        // commented for DB2 OraclePreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate = null;
        // check wether the record already updated or not
        boolean modified = this.recordModified(p_con, p_interfaceVO.getInterfaceId(), p_interfaceVO.getLastModified());
        // call the DAO method to Update the interface Detail
        try {

            StringBuffer updateQueryBuff = new StringBuffer("UPDATE interfaces SET");
            updateQueryBuff.append(" external_id=?,interface_description=?,");
            updateQueryBuff.append("interface_type_id=?,status=?,clouser_date=?,message_language1=?,");
            updateQueryBuff.append("message_language2=?,concurrent_connection=?,single_state_transaction=?,");
            // updateQueryBuff.append("validation_time_out=?,update_time_out=?,");
            updateQueryBuff.append("modified_on=?,modified_by=?,status_type=?, val_expiry_time=?, topup_expiry_time=? WHERE interface_id=?");
            String insertQuery = updateQueryBuff.toString();
            // commented for DB2 pstmtUpdate =
            // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(insertQuery);
            pstmtUpdate.setString(1, p_interfaceVO.getExternalId());
            // commented for DB2
            // pstmtUpdate.setFormOfUse(2,OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(2, p_interfaceVO.getInterfaceDescription());
            pstmtUpdate.setString(3, p_interfaceVO.getInterfaceTypeId());
            pstmtUpdate.setString(4, p_interfaceVO.getStatusCode());
            pstmtUpdate.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_interfaceVO.getClosureDate()));
            pstmtUpdate.setString(6, p_interfaceVO.getLanguage1Message());
            // commented for DB2
            // pstmtUpdate.setFormOfUse(7,OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(7, p_interfaceVO.getLanguage2Message());
            pstmtUpdate.setInt(8, p_interfaceVO.getConcurrentConnection());
            pstmtUpdate.setString(9, p_interfaceVO.getSingleStateTransaction());
            // pstmtUpdate.setLong(10,p_interfaceVO.getValidationTimeout());
            // pstmtUpdate.setLong(11,p_interfaceVO.getUpdateTimeOut());
            pstmtUpdate.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(p_interfaceVO.getModifiedOn()));
            pstmtUpdate.setString(11, p_interfaceVO.getModifiedBy());
            if (BTSLUtil.isNullString(p_interfaceVO.getStatusType()))
                pstmtUpdate.setString(12, PretupsI.INTERFACE_STATUS_TYPE_MANUAL);
            else
                pstmtUpdate.setString(12, p_interfaceVO.getStatusType());
            pstmtUpdate.setLong(13, p_interfaceVO.getValExpiryTime());
            pstmtUpdate.setLong(14, p_interfaceVO.getTopUpExpiryTime());
            pstmtUpdate.setString(15, p_interfaceVO.getInterfaceId());
            if (modified)
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            updateCount = pstmtUpdate.executeUpdate();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Query Executed= " + insertQuery);
        } catch (BTSLBaseException be) {
            throw be;
        }

        catch (SQLException sqle) {
            _log.error(methodName, " SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[modifyInterfaceDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[modifyInterfaceDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting updateCount " + updateCount);
        }

        return updateCount;
    }

    /**
     * This method is used to check whether the record in the database is
     * modified or not
     * 
     * @return boolean
     * @param con
     *            Connection
     * @param String
     * @param oldlastModified
     *            Long
     * @exception BTSLBaseException
     */

    public boolean recordModified(Connection con, String interfaceId, long oldLastModified) throws BTSLBaseException {
        final String methodName = "recordModified";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: interfaceId= " + interfaceId + "oldLastModified= " + oldLastModified);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        String sqlRecordModified = "SELECT modified_on FROM interfaces WHERE interface_id=?";
        Timestamp newLastModified = null;

        if ((oldLastModified) == 0)
            return false;
        try {
            _log.info(methodName, "QUERY: sqlselect= " + sqlRecordModified);
            pstmt = con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, interfaceId);
            rs = pstmt.executeQuery();
            if (rs.next())
                newLastModified = rs.getTimestamp("modified_on");

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " old=" + oldLastModified);
                _log.debug(methodName, " new=" + newLastModified.getTime());
            }

            if (newLastModified.getTime() != oldLastModified)
                modified = true;

        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[recordModified]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[recordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exititng modified=" + modified);
            }
        }
        return modified;
    }

    /**
     * This method is used before adding/modifying the record in the interfaces
     * table
     * it will check for the uniqueness of the interface_description column
     * if the interface_description the user enterd exists in the database
     * the method return true and record will not inserted in the interfaces
     * table.
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_interfaceDesc
     *            String
     * @param p_interfaceId
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExists(Connection p_con, String p_interfaceDesc, String p_interfaceId) throws BTSLBaseException {

        final String methodName = "isExists";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered params p_interfaceDesc::" + p_interfaceDesc + " p_interfaceId=" + p_interfaceId);

        // commented for DB2 OraclePreparedStatement pstmtSelect=null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        StringBuffer sqlBuff = new StringBuffer("SELECT interface_description FROM");
        sqlBuff.append(" interfaces WHERE UPPER(interface_description)=UPPER(?)");

        if ((p_interfaceId != null) && (!p_interfaceId.equals("null"))) {
            sqlBuff.append(" AND interface_id !=?");
        }

        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Select Query::" + selectQuery);

        try {
            // commented for DB2
            // pstmtSelect=(OraclePreparedStatement)p_con.prepareStatement(selectQuery);
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            // commented for DB2
            // pstmtSelect.setFormOfUse(1,OraclePreparedStatement.FORM_NCHAR);
            pstmtSelect.setString(1, p_interfaceDesc);
            if ((!(BTSLUtil.isNullString(p_interfaceId))) && (!p_interfaceId.equals("null"))) {
                pstmtSelect.setString(2, p_interfaceId);
            }
            rs = pstmtSelect.executeQuery();

            if (_log.isDebugEnabled())
                _log.debug(methodName, "Query Executed::" + selectQuery);

            if (rs.next())
                found = true;
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExists]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isExists()", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExists]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExists found=" + found);
            }
        }

        return found;
    }

    /**
     * This method is used before adding the record in the interfaces table
     * it will check for the uniqueness of the external_id column
     * if the external_id the user enterd exists in the database
     * the method return true and record will not inserted in the interfaces
     * table.
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_externalID
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsExternalId(Connection p_con, String p_externalID) throws BTSLBaseException {

        final String methodName = "isExistsExternalId";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered params p_externalID=" + p_externalID);

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        String sqlBuff = "SELECT 1 FROM interfaces WHERE UPPER(external_id)=UPPER(?) AND status<>'N'";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Select Query::" + sqlBuff);

        try {
            pstmtSelect = p_con.prepareStatement(sqlBuff);
            pstmtSelect.setString(1, p_externalID);
            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Query Executed::" + sqlBuff);

            if (rs.next())
                found = true;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExistsExternalId]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExistsExternalId]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting isExists found=" + found);
        }
        return found;
    }

    /**
     * This method is used before modifying the record in the interfaces table
     * it will check for the uniqueness of the external_id column
     * if the external_id the user enterd exists in the database
     * the method return true and record will not inserted in the interfaces
     * table.
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_externalID
     *            String
     * @param p_interfaceID
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsExternalIdModify(Connection p_con, String p_externalID, String p_interfaceID) throws BTSLBaseException {

        final String methodName = "isExistsExternalIdModify";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered params p_externalID=" + p_externalID, "p_interfaceID=" + p_interfaceID);

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        String sqlBuff = "SELECT 1 FROM interfaces WHERE UPPER(external_id)=UPPER(?) AND interface_id!=? AND status<>'N'";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Select Query::" + sqlBuff);

        try {
            pstmtSelect = p_con.prepareStatement(sqlBuff);
            pstmtSelect.setString(1, p_externalID);
            pstmtSelect.setString(2, p_interfaceID);
            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Query Executed::" + sqlBuff);

            if (rs.next())
                found = true;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExistsExternalIdModify]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExistsExternalIdModify]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting isExists found=" + found);
        }
        return found;
    }

    /**
     * Method loadInterfaceDetails.
     * This method is used to load interface details into ArrayList
     * If there is any error then throws the SQLException or Exception
     * 
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_lookupType
     *            String
     * @param p_interfaceID
     *            String
     *            (In add mode interfaceId is null but in edit mode it is not
     *            null so we load the interface list
     *            on the basis of the passed interfaceId, in add mode load those
     *            interfaces that are not already
     *            inserted by the user in interface_network_mapping table)
     * 
     * @return interfaceDetails ArrayList
     */

    public ArrayList loadInterfaceList(Connection p_con, String p_networkCode, String p_lookupType, String p_interfaceID) throws BTSLBaseException {
        final String methodName = "loadInterfaceList";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_networkCode=" + p_networkCode + " p_lookupType=" + p_lookupType);
        StringBuffer strBuff = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList interfaceDetails = new ArrayList();
        try {
            if (BTSLUtil.isNullString(p_interfaceID)) {
                strBuff = new StringBuffer("SELECT i.interface_description,");
                strBuff.append("i.interface_id,it.interface_category,it.INTERFACE_TYPE_ID ");
                strBuff.append(" FROM interfaces i,interface_types it,lookups l ");
                strBuff.append(" WHERE i.status!='N' AND i.interface_id not in (select interface_id from interface_network_mapping where network_code = ?) ");
                strBuff.append(" AND i.interface_type_id = it.interface_type_id AND it.interface_type_id = l.lookup_code");
                strBuff.append(" AND l.lookup_type = ? ");
                strBuff.append(" ORDER BY it.interface_name");
            } else {
                strBuff = new StringBuffer("SELECT i.interface_description,it.INTERFACE_TYPE_ID,");
                strBuff.append("i.interface_id,it.interface_category ");
                strBuff.append(" FROM interfaces i,interface_types it,lookups l ");
                strBuff.append(" WHERE i.status!='N' AND i.interface_id = ? ");
                strBuff.append(" AND i.interface_type_id = it.interface_type_id AND it.interface_type_id = l.lookup_code");
                strBuff.append(" AND l.lookup_type = ? ");
                strBuff.append(" ORDER BY it.interface_name");
            }

            String sqlLoad = strBuff.toString();
            pstmtSelect = p_con.prepareStatement(sqlLoad);

            if (BTSLUtil.isNullString(p_interfaceID)) {
                pstmtSelect.setString(1, p_networkCode);
                pstmtSelect.setString(2, p_lookupType);
            } else {
                pstmtSelect.setString(1, p_interfaceID);
                pstmtSelect.setString(2, p_lookupType);
            }

            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "QUERY Executed= " + sqlLoad);
            // ListValueVO listVO=null;
            // int index=0;

            while (rs.next()) {
                interfaceDetails.add(new ListValueVO(rs.getString("interface_description"), rs.getString("INTERFACE_TYPE_ID") + ":" + rs.getString("interface_id")));
            }

        }

        catch (SQLException sqe) {
            _log.error(methodName, " SQL Exception " + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, " Exiting size=" + interfaceDetails.size());
        }
        return interfaceDetails;
    }

    /**
     * Method deleteInterface.
     * This method is used to Soft delete the interface according to the
     * interface id
     * 
     * @param p_con
     *            Connection
     * @param p_interfaceVO
     *            InterfaceVO
     * @return int
     * @throws BTSLBaseException
     */

    public int deleteInterface(Connection p_con, InterfaceVO p_interfaceVO) throws BTSLBaseException {

        final String methodName = "deleteInterface";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entering VO " + p_interfaceVO);

        int deleteCount = -1;
        PreparedStatement pstmtUpdate = null;
        // check wether the record already updated or not
        boolean modified = this.recordModified(p_con, p_interfaceVO.getInterfaceId(), p_interfaceVO.getLastModified());
        // call the DAO method to Update the interface Detail
        try {

            StringBuffer updateQueryBuff = new StringBuffer("UPDATE interfaces SET status='N',modified_by=?,modified_on=? WHERE interface_id=?");
            String insertQuery = updateQueryBuff.toString();
            pstmtUpdate = p_con.prepareStatement(insertQuery);
            pstmtUpdate.setString(1, p_interfaceVO.getModifiedBy());
            pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_interfaceVO.getModifiedOn()));
            pstmtUpdate.setString(3, p_interfaceVO.getInterfaceId());

            if (modified)
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            deleteCount = pstmtUpdate.executeUpdate();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Query Executed= " + insertQuery);
        } catch (BTSLBaseException be) {
            throw be;
        }

        catch (SQLException sqle) {
            _log.error(methodName, " SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[deleteInterface]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[deleteInterface]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled())
                _log.debug("modifyInterfaceDetails", "Exiting deleteCount " + deleteCount);
        }

        return deleteCount;
    }

    /**
     * Method isInterfaceExistsInInterfaceNwkPrefix
     * This method is used before soft deleting the interface
     * it will check for the interface id in the INTF_NTWRK_PRFX_MAPPING table
     * if the interface_id exists in the table it will return true
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_interfaceId
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isInterfaceExistsInInterfaceNwkPrefix(Connection p_con, String p_interfaceId) throws BTSLBaseException {

        final String methodName = "isInterfaceExistsInInterfaceNwkPrefix";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_interfaceId=" + p_interfaceId);

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM INTF_NTWRK_PRFX_MAPPING where interface_id=?");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Select Query::" + selectQuery);

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_interfaceId);
            rs = pstmtSelect.executeQuery();
            if (rs.next())
                found = true;
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExists]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExists]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExists found=" + found);
            }
        }

        return found;
    }

    /**
     * Method isInterfaceExistsInInterfaceNwkMapping
     * This method is used before soft deleting the interface
     * it will check for the interface id in the INTERFACE_NETWORK_MAPPING table
     * if the interface_id exists in the table it will return true
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_interfaceId
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isInterfaceExistsInInterfaceNwkMapping(Connection p_con, String p_interfaceId) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("isInterfaceExistsInInterfaceNwkPrefix", "Entered p_interfaceId=" + p_interfaceId);

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM INTERFACE_NETWORK_MAPPING where interface_id=?");
        String selectQuery = sqlBuff.toString();
        final String methodName = "isInterfaceExistsInInterfaceNwkMapping";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Select Query::" + selectQuery);

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_interfaceId);
            rs = pstmtSelect.executeQuery();
            if (rs.next())
                found = true;
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExists]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExists]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExists found=" + found);
            }
        }

        return found;
    }

    /**
     * This method will load all interfaces details.
     * 
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     * @author Dhiraj Tiwari
     */

    public ArrayList loadInterfaceDetailsList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadInterfaceDetailsList";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered ");
        StringBuffer strBuff = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        InterfaceVO interfaceVO = null;
        ArrayList loadInterfaceDetailsList = new ArrayList();
        try {
            strBuff = new StringBuffer("SELECT interface_id,external_id,");
            strBuff.append("interface_description,interface_type_id,status,");
            strBuff.append("clouser_date,message_language1,message_language2,");
            strBuff.append("concurrent_connection,single_state_transaction,");
            // strBuff.append("validation_time_out,update_time_out,");
            strBuff.append("created_on,created_by,modified_on,modified_by,status_type,val_expiry_time,topup_expiry_time FROM interfaces WHERE status <> 'N'");

            String sqlLoad = strBuff.toString();
            pstmtSelect = p_con.prepareStatement(sqlLoad);
            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "QUERY Executed= " + sqlLoad);

            while (rs.next()) {
                interfaceVO = new InterfaceVO();
                interfaceVO.setInterfaceId(rs.getString("interface_id"));
                interfaceVO.setExternalId(rs.getString("external_id"));
                interfaceVO.setInterfaceDescription(rs.getString("interface_description"));
                interfaceVO.setInterfaceTypeId(rs.getString("interface_type_id"));
                interfaceVO.setStatusCode(rs.getString("status"));
                interfaceVO.setClosureDate(rs.getDate("clouser_date"));
                interfaceVO.setLanguage1Message(rs.getString("message_language1"));
                interfaceVO.setLanguage2Message(rs.getString("message_language2"));
                interfaceVO.setConcurrentConnection(rs.getInt("concurrent_connection"));
                interfaceVO.setSingleStateTransaction(rs.getString("single_state_transaction"));
                // interfaceVO.setValidationTimeout(rs.getLong("validation_time_out"));
                // interfaceVO.setUpdateTimeOut(rs.getLong("update_time_out"));
                interfaceVO.setCreatedOn(rs.getDate("created_on"));
                interfaceVO.setCreatedBy(rs.getString("created_by"));
                interfaceVO.setModifiedOn(rs.getDate("modified_on"));
                interfaceVO.setModifiedBy(rs.getString("modified_by"));
                interfaceVO.setStatusType(rs.getString("status_type"));
                interfaceVO.setValExpiryTime(rs.getLong("val_expiry_time"));
                interfaceVO.setTopUpExpiryTime(rs.getLong("topup_expiry_time"));
                loadInterfaceDetailsList.add(interfaceVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, " SQL Exception " + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceDetailsList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("ResumeSuspendProcess", methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceDetailsList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ResumeSuspendProcess", methodName, "error.general.processing");

        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, " Exiting , no. of Interfaces = " + loadInterfaceDetailsList.size());
        }
        return loadInterfaceDetailsList;
    }

    /**
     * This method loads the list of list value VO for interface
     * 
     * @param p_con
     * @return arraylist
     * @throws BTSLBaseException
     */
    public ArrayList loadInterfacesTypeValueVOList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadInterfacesTypeValueVOList";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entering");
        ArrayList interfaceList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer selectQueryBuff = new StringBuffer("SELECT interface_type_id, interface_name, interface_category FROM interface_types ORDER BY interface_name");
        String selectQuery = selectQueryBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Select Query " + selectQuery);
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                interfaceList.add(new ListValueVO(rs.getString("interface_name"), rs.getString("interface_type_id")));
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Query Executed " + selectQuery);
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfacesTypeValueVOList]", "", "", "", "SQLException:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfacesTypeValueVOList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting Size " + interfaceList.size());
        }
        return interfaceList;
    }

    /**
     * This method loads the list of interface based on the interface category
     * type ie PRE, POST etc.
     * 
     * @param p_con
     * @return arraylist
     * @throws BTSLBaseException
     */
    public ArrayList loadInterfacesTypeList(Connection p_con) throws BTSLBaseException {

        final String methodName = "loadInterfacesTypeList";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entering");

        ArrayList interfaceList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        StringBuffer selectQueryBuff = new StringBuffer("SELECT interface_type_id, interface_name, interface_category FROM interface_types ORDER BY interface_name");
        String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled())
            _log.debug(methodName, "Select Query " + selectQuery);
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                interfaceList.add(new ListValueVO(rs.getString("interface_name"), rs.getString("interface_category") + ":" + rs.getString("interface_type_id")));
            }

            if (_log.isDebugEnabled())
                _log.debug(methodName, "Query Executed " + selectQuery);
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfacesTypeList]", "", "", "", "SQLException:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfacesTypeList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting Size " + interfaceList.size());
        }
        return interfaceList;
    }

    /**
     * Method loadInterfaceByID.
     * This method is used to load interface details corresponding to
     * interfaceID
     * If there is any error then throws the SQLException or Exception
     * 
     * @param p_con
     *            Connection
     * @param p_interfaceID
     *            String
     * @return interfaceDetails ArrayList
     */

    public HashMap loadInterfaceByID() throws BTSLBaseException {
        final String methodName = "loadInterfaceByID";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered");
        StringBuffer strBuff = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        HashMap interfaceDetail = new HashMap();
        InterfaceVO interfaceVO = null;
        Connection con = null;
        try {
            strBuff = new StringBuffer("SELECT i.interface_id,i.external_id,i.interface_description,i.interface_type_id,");
            strBuff.append("i.status,i.clouser_date,i.message_language1,i.message_language2,i.concurrent_connection,");
            strBuff.append("i.single_state_transaction,");
            // strBuff.append(",i.validation_time_out,i.update_time_out,");
            strBuff.append("i.status_type,i.status,i.val_expiry_time,i.topup_expiry_time,it.interface_type_id,");
            strBuff.append("it.interface_name,it.interface_category,it.handler_class,it.underprocess_msg_reqd ");
            strBuff.append(" FROM interfaces i,interface_types it WHERE it.interface_type_id=i.interface_type_id");

            String sqlLoad = strBuff.toString();
            con = OracleUtil.getSingleConnection();
            pstmtSelect = con.prepareStatement(sqlLoad);

            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceList", "QUERY Executed= " + sqlLoad);
            while (rs.next()) {
                interfaceVO = new InterfaceVO();
                interfaceVO.setInterfaceId(rs.getString("interface_id"));
                interfaceVO.setInterfaceTypeId(rs.getString("interface_type_id"));
                interfaceVO.setConcurrentConnection(rs.getInt("concurrent_connection"));
                interfaceVO.setInterfaceName(rs.getString("interface_name"));
                interfaceVO.setInterfaceCategory(rs.getString("interface_category"));
                interfaceVO.setHandlerClass(rs.getString("handler_class"));
                // interfaceVO.setValidationTimeout(rs.getLong("validation_time_out"));
                // interfaceVO.setUpdateTimeOut(rs.getLong("update_time_out"));
                interfaceVO.setStatusType(rs.getString("status_type"));
                interfaceVO.setStatusCode(rs.getString("status"));
                interfaceVO.setValExpiryTime(rs.getLong("val_expiry_time"));
                interfaceVO.setTopUpExpiryTime(rs.getLong("topup_expiry_time"));
                interfaceVO.setLanguage1Message(rs.getString("message_language1"));
                interfaceVO.setLanguage2Message(rs.getString("message_language2"));
                interfaceDetail.put(interfaceVO.getInterfaceId(), interfaceVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, " SQL Exception " + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceByID]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceByID]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, " Exiting interfaceDetail size=" + interfaceDetail.size());
        }
        return interfaceDetail;
    }

    /**
     * Method isInterfaceExistsInIATMapping
     * This method is used before soft deleting the interface
     * it will check for the interface id in the IAT_NW_SERVICE_MAPPING table
     * if the interface_id exists in the table it will return true
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_interfaceId
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isInterfaceExistsInIATMapping(Connection p_con, String p_interfaceId) throws BTSLBaseException {

        final String methodName = "isInterfaceExistsInIATMapping";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_interfaceId=" + p_interfaceId);

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM IAT_NW_SERVICE_MAPPING where IAT_CODE=?");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Select Query::" + selectQuery);

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_interfaceId);
            rs = pstmtSelect.executeQuery();
            if (rs.next())
                found = true;
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isInterfaceExistsInIATMapping]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "isInterfaceExistsInIATMapping[isExists]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExists found=" + found);
            }
        }

        return found;
    }
}