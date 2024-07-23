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
package com.selftopup.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.sql.SQLException;
import java.util.ArrayList;

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

/**
 */
public class SubLookUpDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getFactory().getInstance(SubLookUpDAO.class.getName());

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
        int addCount = -1;
        if (_log.isDebugEnabled())
            _log.debug("addSubLookup", "Entering p_sublookupVO " + p_sublookupVO);

        // commented for DB2 OraclePreparedStatement pstmtInsert=null;
        PreparedStatement pstmtInsert = null;
        StringBuffer insertQueryBuff = new StringBuffer("INSERT INTO sub_lookups");
        insertQueryBuff.append("(sub_lookup_code,lookup_code,sub_lookup_name,lookup_type,");
        insertQueryBuff.append("status,created_on,created_by,modified_on,modified_by)");
        insertQueryBuff.append(" VALUES(?,?,?,?,?,?,?,?,?)");
        String insertQuery = insertQueryBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("addSubLookup", "Insert Query= " + insertQuery);
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
            _log.error("addSubLookup", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[addSubLookup]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addSubLookup", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("addSubLookup", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[addSubLookup]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addSubLookup", "error.general.processing");
        }

        finally {
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("addSubLookup", "Exiting addCount " + addCount);
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
        if (_log.isDebugEnabled())
            _log.debug("updateSubLookup", "Entering p_subLookupVO " + p_subLookupVO);
        int updateCount = -1;
        // commented for DB2 OraclePreparedStatement pstmtUpdate=null;
        PreparedStatement pstmtUpdate = null;
        StringBuffer updateQueryBuff = new StringBuffer("UPDATE sub_lookups");
        updateQueryBuff.append(" SET sub_lookup_name=?,modified_on=?,");
        updateQueryBuff.append("modified_by=? WHERE sub_lookup_code=?");
        String updateQuery = updateQueryBuff.toString();
        boolean modified = this.recordModified(p_con, p_subLookupVO.getSubLookupCode(), p_subLookupVO.getLastModified());

        if (_log.isDebugEnabled())
            _log.debug("updateSubLookup", "Update Query " + updateQuery);

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
            if (modified)
                throw new BTSLBaseException(this, "updateSubLookup", "error.modify.true");
            updateCount = pstmtUpdate.executeUpdate();
        }

        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error("updateSubLookup", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[updateSubLookup]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateSubLookup", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("updateSubLookup", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[updateSubLookup]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateSubLookup", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateSubLookup", " Exiting updateCount" + updateCount);
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

        if (_log.isDebugEnabled())
            _log.debug("deleteSubLookup", "Entered ");
        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;
        StringBuffer updateQueryBuff = new StringBuffer("UPDATE sub_lookups");
        updateQueryBuff.append(" SET status=?,modified_on=?,");
        updateQueryBuff.append("modified_by=? WHERE sub_lookup_code=?");
        String updateQuery = updateQueryBuff.toString();
        boolean modified = this.recordModified(p_con, p_sublookupVO.getSubLookupCode(), p_sublookupVO.getLastModified());

        if (_log.isDebugEnabled())
            _log.debug("deleteSubLookup", "Update Query " + updateQuery);

        try {
            if (modified)
                throw new BTSLBaseException(this, "deleteSubLookup", "error.modify.true");
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(1, PretupsI.SUBLOOKUP_STATUS_NO);
            pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_sublookupVO.getModifiedOn()));
            pstmtUpdate.setString(3, p_sublookupVO.getModifiedBy());
            pstmtUpdate.setString(4, p_sublookupVO.getSubLookupCode());
            updateCount = pstmtUpdate.executeUpdate();
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error("deleteSubLookup", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[deleteSubLookup]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteSubLookup", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("deleteSubLookup", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[deleteSubLookup]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteSubLookup", "error.general.processing");
        }

        finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteSubLookup", " Exiting updateCount" + updateCount);
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

    public ArrayList loadSublookup(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadSublookup", "Entering");

        ArrayList subLookupList = null;
        StringBuffer selectQueryBuff = new StringBuffer("SELECT sub_lookup_code,modified_on,");
        selectQueryBuff.append("lookup_code,sub_lookup_name,delete_allowed FROM sub_lookups WHERE");
        selectQueryBuff.append(" status=? ORDER BY sub_lookup_name");
        String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled())
            _log.debug("loadSublookup", "Select Query " + selectQuery);
        subLookupList = new ArrayList();
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
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadSublookup]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadSublookup", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("loadSublookup", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadSublookup]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadSublookup", "error.general.processing");
        }

        finally {
           
            if (_log.isDebugEnabled())
                _log.debug("loadSublookup", " Exiting size()" + subLookupList.size());
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
        if (_log.isDebugEnabled())
            _log.debug("loadLookup", "Entering");
        ArrayList lookupList = null;
        
        ListValueVO listVO = null;
        StringBuffer selectQueryBuff = new StringBuffer();
        selectQueryBuff.append("SELECT lookup_code,lookup_name,L.lookup_type");
        selectQueryBuff.append(" FROM lookups L, lookup_types LT ");
        selectQueryBuff.append("WHERE L.lookup_type=LT.lookup_type AND LT.modified_allowed=? AND L.status<>'N' ");
        selectQueryBuff.append("ORDER BY lookup_name");
        String selectQuery = selectQueryBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadLookup", "Select Query " + selectQuery);
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
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadLookup]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadLookup", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadLookup", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadLookup]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadLookup", "error.general.processing");
        } finally {
          
            if (_log.isDebugEnabled())
                _log.debug("loadLookup", " Exiting size()" + lookupList.size());
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
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        String sqlRecordModified = "SELECT modified_on FROM sub_lookups WHERE sub_lookup_code=?";
        Timestamp newLastModified = null;

        if ((oldLastModified) == 0)
            return false;
        try {
            if (_log.isDebugEnabled())
                _log.debug("recordModified", "QUERY sqlselect " + sqlRecordModified);
            pstmt = con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, p_subLookupCode);
            rs = pstmt.executeQuery();
            if (rs.next())
                newLastModified = rs.getTimestamp("modified_on");

            if (_log.isDebugEnabled()) {
                _log.debug("recordModified", " old " + oldLastModified);
                _log.debug("recordModified", " new " + newLastModified.getTime());
            }

            if (newLastModified.getTime() != oldLastModified)
                modified = true;

        }

        catch (SQLException sqle) {
            _log.error("recordModified", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[recordModified]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "recordModified", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("recordModified", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[recordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "recordModified", "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("recordModified", "Exititng: modified=" + modified);
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

        if (_log.isDebugEnabled())
            _log.debug("isExists", "Entered params p_sublookupName " + p_sublookupName + " p_lookupCode " + p_lookupCode + "p_sublookupCode=" + p_sublookupCode);

        // commented for DB2 OraclePreparedStatement pstmtSelect=null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM sub_lookups WHERE");
        sqlBuff.append(" UPPER(sub_lookup_name)=UPPER(?) AND lookup_code=?");
        if ((p_sublookupCode != null) && (!p_sublookupCode.equals("null")))
            sqlBuff.append(" AND sub_lookup_code !=?");

        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("isExists", "Select Query " + selectQuery);

        try {
            // commented for DB2
            // pstmtSelect=(OraclePreparedStatement)p_con.prepareStatement(selectQuery);
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            // commented for DB2
            // pstmtSelect.setFormOfUse(1,OraclePreparedStatement.FORM_NCHAR);
            pstmtSelect.setString(1, p_sublookupName);
            pstmtSelect.setString(2, p_lookupCode);
            if ((p_sublookupCode != null) && (!p_sublookupCode.equals("null")))
                pstmtSelect.setString(3, p_sublookupCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next())
                found = true;
        } catch (SQLException sqle) {
            _log.error("isExists", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[isExists]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isExists", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("isExists", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[isExists]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isExists", "error.general.processing");
        }

        finally {
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
            if (_log.isDebugEnabled())
                _log.debug("isExists", "Exiting: isExists found " + found);
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
        if (_log.isDebugEnabled())
            _log.debug("loadSublookupByLookupType", "Entering p_lookUpType=" + p_lookUpType);

        ArrayList subLookupList = null;
        StringBuffer selectQueryBuff = new StringBuffer("SELECT sub_lookup_code,modified_on,");
        selectQueryBuff.append("lookup_code,sub_lookup_name FROM sub_lookups WHERE");
        selectQueryBuff.append(" status != 'N' AND lookup_type = ? ORDER BY sub_lookup_name");
        String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled())
            _log.debug("loadSublookupByLookupType", "Select Query " + selectQuery);
        subLookupList = new ArrayList();
        try (PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);){
          
            pstmtSelect.setString(1, p_lookUpType);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                subLookupList.add(new ListValueVO(rs.getString("sub_lookup_name"), rs.getString("sub_lookup_code") + ":" + rs.getString("lookup_code")));
            }
        } 
        }catch (SQLException sqle) {
            _log.error("loadSublookupByLookupType", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadSublookupByLookupType]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadSublookupByLookupType", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("loadSublookupByLookupType", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadSublookupByLookupType]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadSublookupByLookupType", "error.general.processing");
        }

        finally {
            
            if (_log.isDebugEnabled())
                _log.debug("loadSublookupByLookupType", " Exiting size()" + subLookupList.size());
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
        if (_log.isDebugEnabled())
            _log.debug("loadSublookupVOList", "Entering p_lookUpType=" + p_lookUpType);

        ArrayList subLookupList = null;
        
        StringBuffer selectQueryBuff = new StringBuffer("SELECT sub_lookup_code, lookup_code, sub_lookup_name, ");
        selectQueryBuff.append("lookup_type, status, modified_on, modified_by,delete_allowed ");
        selectQueryBuff.append("FROM sub_lookups ");
        selectQueryBuff.append("WHERE status != 'N' AND lookup_type = ? ORDER BY lookup_code,sub_lookup_name");
        String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled())
            _log.debug("loadSublookupVOList", "Select Query " + selectQuery);
        subLookupList = new ArrayList();
        try (PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);){
           
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
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadSublookupVOList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadSublookupVOList", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("loadSublookupVOList", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubLookUpDAO[loadSublookupVOList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadSublookupVOList", "error.general.processing");
        }

        finally {
           
            if (_log.isDebugEnabled())
                _log.debug("loadSublookupVOList", " Exiting size()" + subLookupList.size());
        }

        return subLookupList;
    }

}
