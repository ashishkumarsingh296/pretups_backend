/*
 * #SubLookUpDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jun 21, 2005 Amit Ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

// commented for DB2 import oracle.jdbc.OraclePreparedStatement;






import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.btsl.util.SqlParameterEncoder;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.methodParamPartsMappingType;

/**
 */
public class SubLookUpDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getFactory().getInstance(SubLookUpDAO.class.getName());
    public static final String CLASS_NAME = "SubLookUpDAO";

    /**
     * Method addSubLookup.
     * This method is used to add the sublookup in the sub_lookups table
     * 
     * @param p_con
     *            Connection
     * @param p_sublookupVO
     *            SubLookUpVO
     * @return addCount int
     * @throws BTSLBaseException
     */

    public int addSubLookup(Connection p_con, SubLookUpVO p_sublookupVO) throws BTSLBaseException {
    	final String METHOD_NAME = "addSubLookup";
        int addCount = -1;
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entering p_sublookupVO " );
        	loggerValue.append(p_sublookupVO);
            _log.debug(METHOD_NAME, loggerValue);
        }

        // commented for DB2 OraclePreparedStatement pstmtInsert=null;
        
        PreparedStatement pstmtInsert = null;
        StringBuffer insertQueryBuff = new StringBuffer("INSERT INTO sub_lookups");
        insertQueryBuff.append("(sub_lookup_code,lookup_code,sub_lookup_name,lookup_type,");
        insertQueryBuff.append("status,created_on,created_by,modified_on,modified_by)");
        insertQueryBuff.append(" VALUES(?,?,?,?,?,?,?,?,?)");
        String insertQuery = insertQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Insert Query= " + insertQuery);
        }
        try {
            // commented for DB2 pstmtInsert =
            // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
            pstmtInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(1, p_sublookupVO.getSubLookupCode());
            pstmtInsert.setString(2, p_sublookupVO.getLookupCode());
            // commented for DB2
            // pstmtInsert.setFormOfUse(3,OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(3, p_sublookupVO.getSubLookupName());
            pstmtInsert.setString(4, p_sublookupVO.getLookupType());
            pstmtInsert.setString(5, PretupsI.SUBLOOKUP_STATUS_YES);
            pstmtInsert.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(p_sublookupVO.getCreatedOn()));
            pstmtInsert.setString(7, p_sublookupVO.getCreatedBy());
            pstmtInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(p_sublookupVO.getModifiedOn()));
            pstmtInsert.setString(9, p_sublookupVO.getModifiedBy());
            addCount = pstmtInsert.executeUpdate();
        }

        catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[addSubLookup]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addSubLookup", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("addSubLookup", " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[addSubLookup]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addSubLookup", "error.general.processing");
        }

        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("addSubLookup", "Exiting addCount " + addCount);
            }
        }

        return addCount;
    }

    /**
     * Method updateSubLookup.
     * This method is used to update the sublookup in the sub_lookups table
     * 
     * @param p_con
     *            Connection
     * @param p_subLookupVO
     *            SubLookUpVO
     * @return updateCount int
     * @throws BTSLBaseException
     */

    public int updateSubLookup(Connection p_con, SubLookUpVO p_subLookupVO) throws BTSLBaseException {
    	 final String METHOD_NAME = "updateSubLookup";
    	if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entering p_subLookupVO ");
        	loggerValue.append(p_subLookupVO);
        	
            _log.debug(METHOD_NAME, loggerValue);
        }
        int updateCount = -1;
        // commented for DB2 OraclePreparedStatement pstmtUpdate=null;
       
        PreparedStatement pstmtUpdate = null;
        StringBuffer updateQueryBuff = new StringBuffer("UPDATE sub_lookups");
        updateQueryBuff.append(" SET sub_lookup_name=?,modified_on=?,");
        updateQueryBuff.append("modified_by=? WHERE sub_lookup_code=?");
        String updateQuery = updateQueryBuff.toString();
        boolean modified = this.recordModified(p_con, p_subLookupVO.getSubLookupCode(), p_subLookupVO.getLastModified());

        if (_log.isDebugEnabled()) {
            _log.debug("updateSubLookup", "Update Query " + updateQuery);
        }

        try {
            // commented for DB2 pstmtUpdate =
            // (OraclePreparedStatement)p_con.prepareStatement(updateQuery);
            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);
            // commented for DB2
            // pstmtUpdate.setFormOfUse(1,OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(1, p_subLookupVO.getSubLookupName());
            pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_subLookupVO.getModifiedOn()));
            pstmtUpdate.setString(3, p_subLookupVO.getModifiedBy());
            pstmtUpdate.setString(4, p_subLookupVO.getSubLookupCode());
            if (modified) {
                throw new BTSLBaseException(this, "updateSubLookup", "error.modify.true");
            }
            updateCount = pstmtUpdate.executeUpdate();
        }

        catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
        } catch (SQLException sqle) {
            _log.error("updateSubLookup", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[updateSubLookup]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateSubLookup", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("updateSubLookup", " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[updateSubLookup]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateSubLookup", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("updateSubLookup", " Exiting updateCount" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method deleteSubLookup
     * This method is used to delete the sublook up set the status "N"
     * 
     * @param p_con
     *            Connection
     * @param p_sublookupVO
     *            SubLookUpVO
     * @return int
     * @throws BTSLBaseException
     */

    public int deleteSubLookup(Connection p_con, SubLookUpVO p_sublookupVO) throws BTSLBaseException {
        final String METHOD_NAME = "deleteSubLookup";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered ");
        }
        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;
        StringBuffer updateQueryBuff = new StringBuffer("UPDATE sub_lookups");
        updateQueryBuff.append(" SET status=?,modified_on=?,");
        updateQueryBuff.append("modified_by=? WHERE sub_lookup_code=?");
        String updateQuery = updateQueryBuff.toString();
        boolean modified = this.recordModified(p_con, p_sublookupVO.getSubLookupCode(), p_sublookupVO.getLastModified());

        if (_log.isDebugEnabled()) {
            _log.debug("deleteSubLookup", "Update Query " + updateQuery);
        }

        try {
            if (modified) {
                throw new BTSLBaseException(this, "deleteSubLookup", "error.modify.true");
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(1, PretupsI.SUBLOOKUP_STATUS_NO);
            pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_sublookupVO.getModifiedOn()));
            pstmtUpdate.setString(3, p_sublookupVO.getModifiedBy());
            pstmtUpdate.setString(4, p_sublookupVO.getSubLookupCode());
            updateCount = pstmtUpdate.executeUpdate();
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
        } catch (SQLException sqle) {
            _log.error("deleteSubLookup", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[deleteSubLookup]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteSubLookup", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("deleteSubLookup", " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[deleteSubLookup]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteSubLookup", "error.general.processing");
        }

        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("deleteSubLookup", " Exiting updateCount" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method loadSublookup.
     * This method is used to load the sublookup from the sub_lookups table
     * 
     * @param p_con
     *            Connection
     * @return subLookupList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList<SubLookUpVO> loadSublookup(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadSublookup", "Entering");
        }

        final String METHOD_NAME = "loadSublookup";
        ArrayList<SubLookUpVO> subLookupList = null;
        
         
        StringBuffer selectQueryBuff = new StringBuffer("SELECT sub_lookup_code,modified_on,");
        selectQueryBuff.append("lookup_code,sub_lookup_name,delete_allowed FROM sub_lookups WHERE");
        selectQueryBuff.append(" status=? ORDER BY sub_lookup_name");
        String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("loadSublookup", "Select Query " + selectQuery);
        }
        subLookupList = new ArrayList<SubLookUpVO>();
        try (PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);){
            
           
            pstmtSelect.setString(1, PretupsI.SUBLOOKUP_STATUS_YES);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                SubLookUpVO subLookupVO = new SubLookUpVO();
                String code = rs.getString("lookup_code") + ":" + rs.getString("sub_lookup_code");
                String name = rs.getString("sub_lookup_name");
                subLookupVO.setSubLookupCode(code);
                subLookupVO.setSubLookupName(name);
                subLookupVO.setSubCode(rs.getString("sub_lookup_code"));
                subLookupVO.setLastModified(rs.getTimestamp("modified_on").getTime());
                subLookupVO.setDeleteAlowed(rs.getString("delete_allowed"));
                subLookupList.add(subLookupVO);
            }
        }
            
        }

        catch (SQLException sqle) {
            _log.error("loadSublookup", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadSublookup]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadSublookup", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("loadSublookup", " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadSublookup]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadSublookup", "error.general.processing");
        }

        finally {
            
            if (_log.isDebugEnabled()) {
                _log.debug("loadSublookup", " Exiting size()" + subLookupList.size());
            }
        }

        return subLookupList;
    }

    /**
     * Method loadLookup.
     * This method loads all lookups which are allowed to be modified.
     * 
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadLookup(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadLookup", "Entering");
        }
        final String METHOD_NAME = "loadLookup";
        ArrayList lookupList = null;
        
        ListValueVO listVO = null;
        StringBuffer selectQueryBuff = new StringBuffer();
        selectQueryBuff.append("SELECT lookup_code,lookup_name,L.lookup_type");
        selectQueryBuff.append(" FROM lookups L, lookup_types LT ");
        selectQueryBuff.append("WHERE L.lookup_type=LT.lookup_type AND LT.modified_allowed=? AND L.status<>'N' ");
        selectQueryBuff.append("ORDER BY lookup_name");
        String selectQuery = selectQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadLookup", "Select Query " + selectQuery);
        }
        lookupList = new ArrayList();
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
           
            
            pstmtSelect.setString(1, PretupsI.MODIFY_ALLOWED_YES);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                listVO = new ListValueVO(rs.getString("lookup_name"), rs.getString("lookup_code"));
                listVO.setType(rs.getString("lookup_type"));
                lookupList.add(listVO);
            }
        } 
        }catch (SQLException sqle) {
            _log.error("loadLookup", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadLookup]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadLookup", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadLookup", " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadLookup]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadLookup", "error.general.processing");
        } finally {
            
            if (_log.isDebugEnabled()) {
                _log.debug("loadLookup", " Exiting size()" + lookupList.size());
            }
        }

        return lookupList;
    }

    /**
     * This method is used to check whether the record in the database is
     * modified or not
     * 
     * @param con
     *            Connection
     * @param p_subLookupCode
     *            String
     * @param oldLastModified
     *            Long
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean recordModified(Connection con, String p_subLookupCode, long oldLastModified) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("recordModified", "Entered: p_subLookupCode= " + p_subLookupCode + "oldLastModified= " + oldLastModified);
        }
        final String METHOD_NAME = "recordModified";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        String sqlRecordModified = "SELECT modified_on FROM sub_lookups WHERE sub_lookup_code=?";
        Timestamp newLastModified = null;

        if ((oldLastModified) == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("recordModified", "QUERY sqlselect " + sqlRecordModified);
            }
            pstmt = con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, p_subLookupCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }

            if (_log.isDebugEnabled()) {
                _log.debug("recordModified", " old " + oldLastModified);
                _log.debug("recordModified", " new " + newLastModified.getTime());
            }

            if (newLastModified != null && newLastModified.getTime() != oldLastModified) {
                modified = true;
            }

        }

        catch (SQLException sqle) {
            _log.error("recordModified", "SQLException: " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[recordModified]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "recordModified", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("recordModified", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[recordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "recordModified", "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("recordModified", "Exititng: modified=" + modified);
            }
        }
        return modified;
    }

    /**
     * This method is used before adding/modifying the record in the sub_lookups
     * table
     * it will check before adding that the look up name the user enters already
     * exists
     * in database or not and return true if exists.
     * 
     * @param p_con
     *            Connection
     * @param p_sublookupName
     *            String
     * @param p_lookupCode
     *            String
     * @param p_sublookupCode
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean isExists(Connection p_con, String p_sublookupName, String p_lookupCode, String p_sublookupCode) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("isExists", "Entered params p_sublookupName " + p_sublookupName + " p_lookupCode " + p_lookupCode + "p_sublookupCode=" + p_sublookupCode);
        }

        // commented for DB2 OraclePreparedStatement pstmtSelect=null;
        final String METHOD_NAME = "isExists";
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM sub_lookups WHERE");
        sqlBuff.append(" UPPER(sub_lookup_name)=UPPER(?) AND lookup_code=?");
        if ((p_sublookupCode != null) && (!("null".equals(p_sublookupCode)))) {
            sqlBuff.append(" AND sub_lookup_code !=?");
        }

        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Select Query ");
        	loggerValue.append(selectQuery);
            _log.debug(METHOD_NAME, loggerValue);
        }

        try {
            // commented for DB2
            // pstmtSelect=(OraclePreparedStatement)p_con.prepareStatement(selectQuery);
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            // commented for DB2
            // pstmtSelect.setFormOfUse(1,OraclePreparedStatement.FORM_NCHAR);
            pstmtSelect.setString(1, p_sublookupName);
            pstmtSelect.setString(2, p_lookupCode);
            if ((p_sublookupCode != null) && (!("null".equals(p_sublookupCode)))) {
                pstmtSelect.setString(3, p_sublookupCode);
            }
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            _log.error("isExists", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[isExists]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isExists", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("isExists", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[isExists]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isExists", "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("isExists", "Exiting: isExists found " + found);
            }
        }

        return found;
    }

    /**
     * Method loadSublookupByLookupType.
     * This method is used to load the sublookup from the sub_lookups table
     * 
     * @param p_con
     *            Connection
     * @param p_lookUpType
     *            String
     * @return subLookupList ArrayList
     * @throws BTSLBaseException
     * 
     */

    public ArrayList loadSublookupByLookupType(Connection p_con, String p_lookUpType) throws BTSLBaseException {
    	 final String METHOD_NAME = "loadSublookupByLookupType";
    	if (_log.isDebugEnabled()) {
    		StringBuilder loggerValue= new StringBuilder();
    		loggerValue.setLength(0);
    		loggerValue.append("Entering p_lookUpType=");
    		loggerValue.append(p_lookUpType);
            _log.debug(METHOD_NAME,loggerValue);
        }

       
        ArrayList subLookupList = null;
       
         
        StringBuffer selectQueryBuff = new StringBuffer("SELECT sub_lookup_code,modified_on,");
        selectQueryBuff.append("lookup_code,sub_lookup_name FROM sub_lookups WHERE");
        selectQueryBuff.append(" status != 'N' AND lookup_type = ? ORDER BY sub_lookup_name");
        String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("loadSublookupByLookupType", "Select Query " + selectQuery);
        }
        subLookupList = new ArrayList();
        try( PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
           
            
            pstmtSelect.setString(1, p_lookUpType);
            try(ResultSet rs = pstmtSelect.executeQuery();){
            while (rs.next()) {
                subLookupList.add(new ListValueVO(SqlParameterEncoder.encodeParams(rs.getString("sub_lookup_name")), 
                		SqlParameterEncoder.encodeParams(rs.getString("sub_lookup_code")) 
                		+ ":" + SqlParameterEncoder.encodeParams(rs.getString("lookup_code"))));
            }
        } 
        }catch (SQLException sqle) {
            _log.error("loadSublookupByLookupType", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadSublookupByLookupType]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadSublookupByLookupType", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("loadSublookupByLookupType", " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadSublookupByLookupType]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadSublookupByLookupType", "error.general.processing");
        }

        finally {
            
            if (_log.isDebugEnabled()) {
                _log.debug("loadSublookupByLookupType", " Exiting size()" + subLookupList.size());
            }
        }

        return subLookupList;
    }

    /**
     * Method loadSublookupVOList.
     * This method is to load the information of sublookup
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_lookUpType
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadSublookupVOList(Connection p_con, String p_lookUpType) throws BTSLBaseException {
    	 final String METHOD_NAME = "loadSublookupVOList";
    	if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entering p_lookUpType=");
        	loggerValue.append(p_lookUpType);
            _log.debug(METHOD_NAME, loggerValue);
        }

       
        ArrayList subLookupList = null;
         
        StringBuffer selectQueryBuff = new StringBuffer("SELECT sub_lookup_code, lookup_code, sub_lookup_name, ");
        selectQueryBuff.append("lookup_type, status, modified_on, modified_by,delete_allowed ");
        selectQueryBuff.append("FROM sub_lookups ");
        selectQueryBuff.append("WHERE status != 'N' AND lookup_type = ? ORDER BY lookup_code,sub_lookup_name");
        String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("loadSublookupVOList", "Select Query " + selectQuery);
        }
        subLookupList = new ArrayList();
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
            
            
            pstmtSelect.setString(1, p_lookUpType);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            SubLookUpVO subLookUpVO = null;
            while (rs.next()) {
                subLookUpVO = new SubLookUpVO();
                subLookUpVO.setSubLookupCode(rs.getString("sub_lookup_code"));
                subLookUpVO.setLookupCode(rs.getString("lookup_code"));
                subLookUpVO.setSubLookupName(rs.getString("sub_lookup_name"));
                subLookUpVO.setLookupType(rs.getString("lookup_type"));
                subLookUpVO.setStatus(rs.getString("status"));
                subLookUpVO.setDeleteAlowed(rs.getString("delete_allowed"));
                subLookUpVO.setModifiedBy(rs.getString("modified_by"));
                subLookUpVO.setModifiedOn(rs.getDate("modified_on"));
                subLookupList.add(subLookUpVO);
            }
        }
        }catch (SQLException sqle) {
            _log.error("loadSublookupVOList", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadSublookupVOList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadSublookupVOList", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("loadSublookupVOList", " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadSublookupVOList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadSublookupVOList", "error.general.processing");
        }

        finally {
            
            if (_log.isDebugEnabled()) {
                _log.debug("loadSublookupVOList", " Exiting size()" + subLookupList.size());
            }
        }

        return subLookupList;
    }
    
    
    /**
     * Get Sublookups from DB
     *
     * @param lookUpType The value through you are filtering sublookups.
     * @return List<SubLookUpVO> list of subLookUpVO 
     */
    public List<SubLookUpVO> loadSublookupVOList(String lookUpType) throws SQLException, BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadSublookupVOList", "Entering lookUpType=" + lookUpType);
        }
       
        Connection con = null;
        MComConnectionI mcomCon = null;
         
        List<SubLookUpVO> subLookupList = null;
        try {
			StringBuilder queryBuilder = new StringBuilder("SELECT sub_lookup_code, lookup_code, sub_lookup_name, ");
			queryBuilder.append("lookup_type, status, modified_on, modified_by,delete_allowed ");
			queryBuilder.append("FROM sub_lookups ");
			queryBuilder.append("WHERE status != 'N' AND lookup_type = ? ORDER BY lookup_code,sub_lookup_name");
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			try(PreparedStatement preparedStatement = con.prepareStatement(queryBuilder.toString());)
			{
			preparedStatement.setString(1, lookUpType);
			try(ResultSet rs = preparedStatement.executeQuery();)
			{
			SubLookUpVO subLookUpVO = null;
			subLookupList = new ArrayList<SubLookUpVO>();
			while (rs.next()) {
				subLookUpVO = new SubLookUpVO();
				subLookUpVO.setSubLookupCode(rs.getString("sub_lookup_code"));
				subLookUpVO.setLookupCode(rs.getString("lookup_code"));
				subLookUpVO.setSubLookupName(rs.getString("sub_lookup_name"));
				subLookUpVO.setLookupType(rs.getString("lookup_type"));
				subLookUpVO.setStatus(rs.getString("status"));
				subLookUpVO.setDeleteAlowed(rs.getString("delete_allowed"));
				subLookUpVO.setModifiedBy(rs.getString("modified_by"));
				subLookUpVO.setModifiedOn(rs.getDate("modified_on"));
				subLookupList.add(subLookUpVO);
				} 
		    } 
			}
        }finally {
			
			
			if (mcomCon != null) {
				mcomCon.close("addSubLookup#loadSublookupVOList");
				mcomCon = null;
			}
		}
		return subLookupList;
    }


    public HashMap<String,ArrayList<SubLookUpVO>> loadSubLookups() throws BTSLBaseException {
        final String methodName = "loadSubLookups";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered ");
        }
        Connection con = null;
        HashMap<String,ArrayList<SubLookUpVO>> map = null;
        try {
            con = OracleUtil.getSingleConnection();
            map = this.loadSubLookupsMapList(con);
            ArrayList<SubLookUpVO> subLookupList = this.loadSublookupList(con);
            /**
             * Associate the sublooktype in map with sublookup arraylist
             */
            if (subLookupList != null) {
                ArrayList<SubLookUpVO> tempList = null;
                for (SubLookUpVO subLookupsVO : subLookupList) 
                    if (map.containsKey(subLookupsVO.getLookupType())) {
                        tempList = map.get(subLookupsVO.getLookupType());
                        tempList.add(subLookupsVO);
                    }
            }
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            _log.error(methodName, "Exception: " + ex);
            _log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exited: Map size=" + map.size());
            }
        }
        return map;
    }

    
    private HashMap<String,ArrayList<SubLookUpVO>> loadSubLookupsMapList(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadSubLookupsTypeList";
        if (_log.isDebugEnabled()) {
            _log.debug("loadSubLookupsTypeList()", "Entered ");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String,ArrayList<SubLookUpVO>> lookupTypeMap = new HashMap<String,ArrayList<SubLookUpVO>>();
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT lookup_code,lookup_type FROM sub_lookups");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String code = rs.getString("lookup_type");
                lookupTypeMap.put(code, new ArrayList<SubLookUpVO>());
            }
        } catch (SQLException sqe) {
            _log.error(METHOD_NAME, " SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            _log.error("loadLookupsTypeList()", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting: networkMap size=" + lookupTypeMap.size());
            }
        }
        return lookupTypeMap;
    }

    
    public ArrayList<SubLookUpVO> loadSublookupList(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadSublookupList", "Entering");
        }

        final String METHOD_NAME = "loadSublookupList";
        ArrayList<SubLookUpVO> subLookupList = null;
        
        StringBuffer selectQueryBuff = new StringBuffer("SELECT sub_lookup_code,lookup_code,sub_lookup_name,lookup_type,");
        selectQueryBuff.append("modified_on, modified_by,delete_allowed FROM sub_lookups WHERE");
        selectQueryBuff.append(" status=? ORDER BY sub_lookup_name");
        String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query " + selectQuery);
        }
        subLookupList = new ArrayList<SubLookUpVO>();
        try (PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);){
            
           
            pstmtSelect.setString(1, PretupsI.SUBLOOKUP_STATUS_YES);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                SubLookUpVO subLookupVO = new SubLookUpVO();
                String code = rs.getString("lookup_type");
                String name = rs.getString("sub_lookup_name");
                subLookupVO.setLookupType(code);
                subLookupVO.setSubLookupName(name);
                subLookupVO.setLookupCode(rs.getString("lookup_code"));
                subLookupVO.setSubLookupCode(rs.getString("sub_lookup_code"));
                subLookupVO.setSubCode(rs.getString("sub_lookup_code"));
                subLookupVO.setLastModified(rs.getTimestamp("modified_on").getTime());
                subLookupVO.setModifiedOn(Date.from(rs.getTimestamp("modified_on").toInstant()));
                subLookupVO.setDeleteAlowed(rs.getString("delete_allowed"));
                subLookupVO.setModifiedBy(rs.getString("modified_by"));
                subLookupList.add(subLookupVO);
            }
        }
            
        }

        catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadSublookup]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(METHOD_NAME, " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadSublookup]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }

        finally {
            
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Exiting size()" + subLookupList.size());
            }
        }

        return subLookupList;
    }
    public ArrayList loadSublookupByLookUpCode(Connection con, String lookUpCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadSublookupByLookUpCode";
        if (_log.isDebugEnabled()) {
            StringBuilder loggerValue = new StringBuilder();
            loggerValue.setLength(0);
            loggerValue.append("Entering lookUpCode=");
            loggerValue.append(lookUpCode);
            _log.debug(METHOD_NAME, loggerValue);
        }
        ArrayList subLookupList = null;
        StringBuffer selectQueryBuff = new StringBuffer("SELECT sub_lookup_code, lookup_code, sub_lookup_name, ");
        selectQueryBuff.append("lookup_type, status, modified_on, modified_by,delete_allowed ");
        selectQueryBuff.append("FROM sub_lookups ");
        selectQueryBuff.append("WHERE status != 'N' AND lookup_code = ? ORDER BY lookup_code,sub_lookup_name");
        String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("loadSublookupByLookUpCode", "Select Query " + selectQuery);
        }
        subLookupList = new ArrayList();
        try (PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);) {
            pstmtSelect.setString(1, lookUpCode);
            try (ResultSet rs = pstmtSelect.executeQuery();) {
                SubLookUpVO subLookUpVO = null;
                while (rs.next()) {
                    subLookUpVO = new SubLookUpVO();
                    subLookUpVO.setSubLookupCode(rs.getString("sub_lookup_code"));
                    subLookUpVO.setLookupCode(rs.getString("lookup_code"));
                    subLookUpVO.setSubLookupName(rs.getString("sub_lookup_name"));
                    subLookUpVO.setLookupType(rs.getString("lookup_type"));
                    subLookUpVO.setStatus(rs.getString("status"));
                    subLookUpVO.setDeleteAlowed(rs.getString("delete_allowed"));
                    subLookUpVO.setModifiedBy(rs.getString("modified_by"));
                    subLookUpVO.setModifiedOn(rs.getDate("modified_on"));
                    subLookupList.add(subLookUpVO);
                }
            }
        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, METHOD_NAME, "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
        } catch (Exception e) {
            _log.error(METHOD_NAME, " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, METHOD_NAME, "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Exiting size()" + subLookupList.size());
            }
        }
        return subLookupList;
    }

    /**
     * Method loadLookup.
     * This method loads all lookups which are allowed to be modified.
     *
     * @param con Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public LookupsVO loadLookupByLookUpCode(Connection con, String lookUpCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadLookupByLookUpCode";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entering");
        }
        LookupsVO lookupsVO = null;

        StringBuffer selectQueryBuff = new StringBuffer();
        selectQueryBuff.append("SELECT lookup_code,lookup_name,L.lookup_type");
        selectQueryBuff.append(" FROM lookups L ");
        selectQueryBuff.append("WHERE L.lookup_code = ? AND L.status<>'N' ");
        selectQueryBuff.append("ORDER BY lookup_name");
        String selectQuery = selectQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query " + selectQuery);
        }
        lookupsVO = new LookupsVO();
        try (PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);) {
            pstmtSelect.setString(1, lookUpCode);
            try (ResultSet rs = pstmtSelect.executeQuery();) {
                while (rs.next()) {
                    lookupsVO.setLookupName(rs.getString("lookup_name"));
                    lookupsVO.setLookupCode(rs.getString("lookup_code"));
                    lookupsVO.setLookupType(rs.getString("lookup_type"));
                }
            }
        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, METHOD_NAME, "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
        } catch (Exception e) {
            _log.error(METHOD_NAME, " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, METHOD_NAME, "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("loadLookup", "" + lookupsVO);
            }
        }
        return lookupsVO;
    }

    public SubLookUpVO loadSublookupBySubLookUpCode(Connection p_con, String subLookUpCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadSublookupBySubLookUpCode";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entering");
        }

        SubLookUpVO subLookUpVO = null;

        StringBuffer selectQueryBuff = new StringBuffer("SELECT sub_lookup_code,lookup_code,sub_lookup_name,lookup_type,");
        selectQueryBuff.append("modified_on, modified_by,delete_allowed FROM sub_lookups WHERE");
        selectQueryBuff.append(" status=? AND sub_lookup_code = ? ORDER BY sub_lookup_name");
        String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query " + selectQuery);
        }
        subLookUpVO = new SubLookUpVO();
        try (PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {

            pstmtSelect.setString(1, PretupsI.SUBLOOKUP_STATUS_YES);
            pstmtSelect.setString(2, subLookUpCode);
            try (ResultSet rs = pstmtSelect.executeQuery();) {
                while (rs.next()) {
                    String code = rs.getString("lookup_type");
                    String name = rs.getString("sub_lookup_name");
                    subLookUpVO.setLookupType(code);
                    subLookUpVO.setSubLookupName(name);
                    subLookUpVO.setLookupCode(rs.getString("lookup_code"));
                    subLookUpVO.setSubLookupCode(rs.getString("sub_lookup_code"));
                    subLookUpVO.setSubCode(rs.getString("sub_lookup_code"));
                    subLookUpVO.setLastModified(rs.getTimestamp("modified_on").getTime());
                    subLookUpVO.setDeleteAlowed(rs.getString("delete_allowed"));
                    subLookUpVO.setModifiedBy(rs.getString("modified_by"));
                }
            }

        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, METHOD_NAME, "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
        } catch (Exception e) {
            _log.error(METHOD_NAME, " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, METHOD_NAME, "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
        } finally {

            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, subLookUpVO);
            }
        }
        return subLookUpVO;
    }

}
