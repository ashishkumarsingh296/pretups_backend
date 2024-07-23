package com.web.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.DivisionDeptVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.superadmin.DivisionVO;

public class DivisionDeptWebDAO {

    private static Log _log = LogFactory.getFactory().getInstance(DivisionDeptWebDAO.class.getName());

    /**
     * Method loadDivisionDetails.
     * This method is used to load division details from division_department
     * table
     * 
     * @param p_con
     *            Connection
     * @param p_userVO
     *            UserVO
     * @param p_divisionVO
     *            DivisionDeptVO
     * @return domainList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadDivisionDetails(Connection p_con, UserVO p_userVO, DivisionDeptVO p_divisionVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadDivisionDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered VO:" + p_divisionVO + "User VO:" + p_userVO);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList divisionList = new ArrayList();
        final StringBuffer strBuff = new StringBuffer("SELECT DD.divdept_id,DD.divdept_name,DD.divdept_short_code,DD.status,LK.lookup_name,");
        strBuff.append("DD.divdept_type,DD.divdept,DD.created_on,DD.created_by,DD.modified_on,DD.modified_by,");
        strBuff.append("DD.parent_id,DD.user_id,DT.domain_type_name FROM division_department DD,lookups LK,domain_types DT ");
        strBuff.append("WHERE DD.divdept=? AND DD.status<>'N' AND LK.lookup_code=DD.status AND DT.domain_type_code=DD.divdept_type ");
        strBuff.append("AND DT.div_dept_allowed='Y' AND LK.lookup_type=? ORDER BY DD.divdept_name");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query= " + sqlSelect);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_divisionVO.getDivDept());
            pstmtSelect.setString(2, PretupsI.STATUS_TYPE);

            rs = pstmtSelect.executeQuery();
            DivisionDeptVO divisionVO = null;
            int radioIndex = 0;
            while (rs.next()) {
                divisionVO = new DivisionDeptVO();
                divisionVO.setDivDeptId(rs.getString("divdept_id"));
                divisionVO.setDivDeptName(rs.getString("divdept_name"));
                divisionVO.setDivDeptTypeName(rs.getString("domain_type_name"));
                divisionVO.setDivDeptType(rs.getString("divdept_type"));
                divisionVO.setDivDeptShortCode(rs.getString("divdept_short_code"));
                divisionVO.setLastModified(rs.getTimestamp("modified_on").getTime());
                divisionVO.setParentId(rs.getString("parent_id"));
                divisionVO.setRadioIndex(radioIndex);
                divisionVO.setStatus(rs.getString("status"));
                divisionVO.setStatusName(rs.getString("lookup_name"));
                radioIndex++;
                divisionList.add(divisionVO);
            }
        }

        catch (SQLException sqe) {
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[loadDivisionDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[loadDivisionDetails]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");

        } finally {
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
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting size=" + divisionList.size());
            }
        }

        return divisionList;
    }

    /**
     * Method loadDepartmentDetails.
     * This method is used to load department details according to the
     * particular division
     * the user selected
     * 
     * @param p_con
     *            Connection
     * @param p_userVO
     *            UserVO
     * @param p_divisionVO
     *            DivisionDeptVO
     * @return divisionList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadDepartmentDetails(Connection p_con, UserVO p_userVO, DivisionDeptVO p_divisionVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadDepartmentDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered VO:" + p_divisionVO + "User VO:" + p_userVO);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList divisionList = new ArrayList();
        final StringBuffer strBuff = new StringBuffer("SELECT DD.divdept_id,DD.divdept_name,DD.divdept_short_code,DD.status,DT.domain_type_name,");
        strBuff.append("DD.divdept_type,DD.divdept,DD.created_on,DD.created_by,DD.modified_on,DD.modified_by,");
        strBuff.append("DD.parent_id,DD.user_id FROM division_department DD,domain_types DT WHERE ");
        strBuff.append("DT.domain_type_code=DD.divdept_type AND DT.div_dept_allowed='Y' AND DD.divdept=? AND DD.status<>'N'");
        strBuff.append(" AND DD.parent_id=? ORDER BY DD.divdept_name");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query= " + sqlSelect);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_divisionVO.getDivDept());
            pstmtSelect.setString(2, p_divisionVO.getParentId());
            rs = pstmtSelect.executeQuery();
            DivisionDeptVO divisionVO = null;
            int radioIndex = 0;
            while (rs.next()) {
                divisionVO = new DivisionDeptVO();
                divisionVO.setDivDeptId(rs.getString("divdept_id"));
                divisionVO.setDivDeptName(rs.getString("divdept_name"));
                divisionVO.setDivDeptType(rs.getString("divdept_type"));
                divisionVO.setDivDeptTypeName(rs.getString("domain_type_name"));
                divisionVO.setDivDeptShortCode(rs.getString("divdept_short_code"));
                divisionVO.setLastModified(rs.getTimestamp("modified_on").getTime());
                divisionVO.setParentId(rs.getString("parent_id"));
                divisionVO.setRadioIndex(radioIndex);
                radioIndex++;
                divisionList.add(divisionVO);
            }
        }

        catch (SQLException sqe) {
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[loadDepartmentDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[loadDepartmentDetails]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");

        } finally {
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
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting size=" + divisionList.size());
            }
        }

        return divisionList;
    }

    /**
     * Method addDivision.
     * This method is used to add the Division details in division_department
     * table
     * 
     * @param p_con
     *            Connection
     * @param p_divisionVO
     *            DivisionDeptVO
     * @return addCount int
     * @throws BTSLBaseException
     */

    public int addDivision(Connection p_con, DivisionDeptVO p_divisionVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("addDivision()", "Entering VO " + p_divisionVO);
        }
        int addCount = -1;
        // commented for DB2 OraclePreparedStatement pstmtInsert = null;
        PreparedStatement pstmtInsert = null;
        final StringBuffer insertQueryBuff = new StringBuffer("INSERT INTO division_department ");
        insertQueryBuff.append("(divdept_id,divdept_name,divdept_short_code,status,divdept_type,");
        insertQueryBuff.append("divdept,created_on,created_by,modified_on,modified_by,parent_id,");
        insertQueryBuff.append("user_id) VALUES (UPPER(?),?,?,?,?,?,?,?,?,?,?,?)");
        final String insertQuery = insertQueryBuff.toString();
        final String METHOD_NAME = "addDivision";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Insert Query= " + insertQuery);
        }
        try {
            // commented for DB2 pstmtInsert =
            // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
            pstmtInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(1, p_divisionVO.getDivDeptId());
            // commented for DB2
            // pstmtInsert.setFormOfUse(2,OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(2, p_divisionVO.getDivDeptName());
            pstmtInsert.setString(3, p_divisionVO.getDivDeptShortCode());
            pstmtInsert.setString(4, p_divisionVO.getStatus());
            pstmtInsert.setString(5, p_divisionVO.getDivDeptType());
            pstmtInsert.setString(6, p_divisionVO.getDivDept());
            pstmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(p_divisionVO.getCreatedOn()));
            pstmtInsert.setString(8, p_divisionVO.getCreatedBy());
            pstmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(p_divisionVO.getCreatedOn()));
            pstmtInsert.setString(10, p_divisionVO.getModifiedBy());
            // Adding the division parent id and division id is same
            // pstmtInsert.setString(11,p_divisionVO.getDivDeptId());
            pstmtInsert.setString(11, p_divisionVO.getParentId());
            pstmtInsert.setString(12, p_divisionVO.getUserId());
            addCount = pstmtInsert.executeUpdate();
        }

        catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[addDivision]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[addDivision]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
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
                _log.debug(METHOD_NAME, " Exiting addCount " + addCount);
            }
        }

        return addCount;
    }

    /**
     * Method isDivisionNameExistsForAdd
     * This method is used before adding the record in the division_department
     * table
     * it will check for the uniqueness of the constraint divdept_name
     * and returns true if record exists in table
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_divisionVO
     *            DivisionDeptVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isDivisionNameExistsForAdd(Connection p_con, DivisionDeptVO p_divisionVO) throws BTSLBaseException {

        final String METHOD_NAME = "isDivisionNameExistsForAdd";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered VO::" + p_divisionVO);
        }
        boolean found = false;
        // commented for DB2 OraclePreparedStatement pstmtSelect=null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM division_department WHERE");
        sqlBuff.append(" UPPER(divdept_name)=UPPER(?) AND divdept=? AND divdept_type=? AND status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query::" + selectQuery);
        }
        try {
            // commented for DB2
            // pstmtSelect=(OraclePreparedStatement)p_con.prepareStatement(selectQuery);
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            // commented for DB2
            // pstmtSelect.setFormOfUse(1,OraclePreparedStatement.FORM_NCHAR);
            pstmtSelect.setString(1, p_divisionVO.getDivDeptName());
            pstmtSelect.setString(2, p_divisionVO.getDivDept());
            pstmtSelect.setString(3, p_divisionVO.getDivDeptType());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDivisionNameExistsForAdd]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDivisionNameExistsForAdd]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
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
                _log.debug(METHOD_NAME, "Exiting  found=" + found);
            }
        }
        return found;
    }

    /**
     * Method isDepartmentNameExistsForAdd
     * This method is used before adding the record in the division_department
     * table
     * it will check for the uniqueness of the constraint divdept_name
     * and returns true if record exists in table
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_divisionVO
     *            DivisionDeptVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isDepartmentNameExistsForAdd(Connection p_con, DivisionDeptVO p_divisionVO) throws BTSLBaseException {

        final String METHOD_NAME = "isDepartmentNameExistsForAdd";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered VO parms:: Parent id=" + p_divisionVO.getParentId() + " DivDept=" + p_divisionVO.getDivDept() + "DivDeptname=" + p_divisionVO
                .getDivDeptName() + "DivdeptType=" + p_divisionVO.getDivDeptType());
        }
        boolean found = false;
        // commented for DB2 OraclePreparedStatement pstmtSelect=null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM division_department where parent_id=? AND divdept=? ");
        sqlBuff.append(" AND UPPER(divdept_name)=UPPER(?) AND divdept_type=? AND status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query::" + selectQuery);
        }
        try {
            // commented for DB2
            // pstmtSelect=(OraclePreparedStatement)p_con.prepareStatement(selectQuery);
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_divisionVO.getParentId());
            pstmtSelect.setString(2, p_divisionVO.getDivDept());
            // commented for DB2
            // pstmtSelect.setFormOfUse(3,OraclePreparedStatement.FORM_NCHAR);
            pstmtSelect.setString(3, p_divisionVO.getDivDeptName());
            pstmtSelect.setString(4, p_divisionVO.getDivDeptType());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDepartmentNameExistsForAdd]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isDivisionNameExistsForAdd", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDepartmentNameExistsForAdd]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
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
                _log.debug(METHOD_NAME, "Exiting  found=" + found);
            }
        }
        return found;
    }

    /**
     * Method isDepartmentNameExistsForModify
     * This method is used before modifying the record in the
     * division_department table
     * it will check for the uniqueness of the constraint divdept_name
     * and returns true if record exists in table
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_divisionVO
     *            DivisionDeptVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isDepartmentNameExistsForModify(Connection p_con, DivisionDeptVO p_divisionVO) throws BTSLBaseException {

        final String METHOD_NAME = "isDepartmentNameExistsForModify";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered VO parms:: Parent id=" + p_divisionVO.getParentId() + " DivDept=" + p_divisionVO.getDivDept() + "DivDeptname=" + p_divisionVO
                .getDivDeptName() + "DivdeptType=" + p_divisionVO.getDivDeptType() + "DivDeptId=" + p_divisionVO.getDivDeptId());
        }
        boolean found = false;
        // commented for DB2 OraclePreparedStatement pstmtSelect=null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM division_department where parent_id=? AND divdept=? ");
        sqlBuff.append(" AND UPPER(divdept_name)=UPPER(?) AND divdept_type=? AND status<>'N' AND divdept_id!=?");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query::" + selectQuery);
        }
        try {
            // commented for DB2
            // pstmtSelect=(OraclePreparedStatement)p_con.prepareStatement(selectQuery);
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_divisionVO.getParentId());
            pstmtSelect.setString(2, p_divisionVO.getDivDept());
            // commented for DB2
            // pstmtSelect.setFormOfUse(3,OraclePreparedStatement.FORM_NCHAR);
            pstmtSelect.setString(3, p_divisionVO.getDivDeptName());
            pstmtSelect.setString(4, p_divisionVO.getDivDeptType());
            pstmtSelect.setString(5, p_divisionVO.getDivDeptId());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDepartmentNameExistsForModify]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDepartmentNameExistsForModify]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
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
                _log.debug(METHOD_NAME, "Exiting  found=" + found);
            }
        }
        return found;
    }

    /**
     * Method isDepartmentShortCodeExistsForAdd
     * This method is used before adding the record in the division_department
     * table
     * it will check for the uniqueness of the constraint short code of
     * department
     * and returns true if record exists in table
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_divisionVO
     *            DivisionDeptVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isDepartmentShortCodeExistsForAdd(Connection p_con, DivisionDeptVO p_divisionVO) throws BTSLBaseException {

        final String METHOD_NAME = "isDepartmentShortCodeExistsForAdd";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME,
                "Entered VO parms:: Parent id=" + p_divisionVO.getParentId() + " DivDept=" + p_divisionVO.getDivDept() + "DivDeptShortCode=" + p_divisionVO
                    .getDivDeptShortCode() + "DivdeptType=" + p_divisionVO.getDivDeptType());
        }
        boolean found = false;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM division_department where parent_id=? AND divdept=? ");
        sqlBuff.append(" AND UPPER(divdept_short_code)=UPPER(?) AND divdept_type=? AND status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query::" + selectQuery);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_divisionVO.getParentId());
            pstmtSelect.setString(2, p_divisionVO.getDivDept());
            pstmtSelect.setString(3, p_divisionVO.getDivDeptShortCode());
            pstmtSelect.setString(4, p_divisionVO.getDivDeptType());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDepartmentShortCodeExistsForAdd]",
                "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDepartmentShortCodeExistsForAdd]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
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
                _log.debug(METHOD_NAME, "Exiting  found=" + found);
            }
        }
        return found;
    }

    /**
     * Method isDepartmentShortCodeExistsForModify
     * This method is used before Modifying the record in the
     * division_department table
     * it will check for the uniqueness of the constraint short code of
     * department
     * and returns true if record exists in table
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_divisionVO
     *            DivisionDeptVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isDepartmentShortCodeExistsForModify(Connection p_con, DivisionDeptVO p_divisionVO) throws BTSLBaseException {
        final String METHOD_NAME = "isDepartmentShortCodeExistsForModify";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME,
                "Entered VO parms:: Parent id=" + p_divisionVO.getParentId() + " DivDept=" + p_divisionVO.getDivDept() + "DivDeptShortCode=" + p_divisionVO
                    .getDivDeptShortCode() + "DivdeptType=" + p_divisionVO.getDivDeptType() + "divdept_id=" + p_divisionVO.getDivDeptId());
        }
        boolean found = false;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM division_department where parent_id=? AND divdept=? ");
        sqlBuff.append(" AND UPPER(divdept_short_code)=UPPER(?) AND divdept_type=? AND status<>'N' AND divdept_id!=?");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query::" + selectQuery);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_divisionVO.getParentId());
            pstmtSelect.setString(2, p_divisionVO.getDivDept());
            pstmtSelect.setString(3, p_divisionVO.getDivDeptShortCode());
            pstmtSelect.setString(4, p_divisionVO.getDivDeptType());
            pstmtSelect.setString(5, p_divisionVO.getDivDeptId());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "DivisionDeptDAO[isDepartmentShortCodeExistsForModify]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "DivisionDeptDAO[isDepartmentShortCodeExistsForModify]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
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
                _log.debug(METHOD_NAME, "Exiting  found=" + found);
            }
        }
        return found;
    }

    /**
     * method isDivisionShortCodeExistsForAdd
     * This method is used before adding the record in the division_department
     * table
     * it will check for the uniqueness of the constraint divdept_short_code
     * and returns true if record exists in table
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_divisionVO
     *            DivisionDeptVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isDivisionShortCodeExistsForAdd(Connection p_con, DivisionDeptVO p_divisionVO) throws BTSLBaseException {

        final String METHOD_NAME = "isDivisionShortCodeExistsForAdd";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered VO::" + p_divisionVO);
        }
        boolean found = false;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM division_department WHERE");
        sqlBuff.append(" UPPER(divdept_short_code)=UPPER(?) AND divdept=? AND divdept_type=? AND status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query::" + selectQuery);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_divisionVO.getDivDeptShortCode());
            pstmtSelect.setString(2, p_divisionVO.getDivDept());
            pstmtSelect.setString(3, p_divisionVO.getDivDeptType());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDivisionShortCodeExistsForAdd]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDivisionShortCodeExistsForAdd]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
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
                _log.debug(METHOD_NAME, "Exiting  found=" + found);
            }
        }
        return found;
    }

    /**
     * This method is used before deleting the record in the division_department
     * table
     * it will check that if any department exists for that division if yes then
     * it will
     * and returns true and the division will not be deleted from the table
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_divisionVO
     *            DivisionDeptVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isDepartmentExistsForDivision(Connection p_con, DivisionDeptVO p_divisionVO) throws BTSLBaseException {

        final String METHOD_NAME = "isDepartmentExistsForDivision";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered VO::" + p_divisionVO);
        }

        boolean found = false;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM division_department WHERE");
        sqlBuff.append(" divdept=? AND parent_id=? AND status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query::" + selectQuery);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, PretupsI.DIVDEPT_DEPARTMENT);
            pstmtSelect.setString(2, p_divisionVO.getParentId());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDepartmentExistsForDivision]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDepartmentExistsForDivision]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
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
                _log.debug(METHOD_NAME, "Exiting  found=" + found);
            }
        }
        return found;
    }

    /**
     * This method is used before deleting the record in the division_department
     * table
     * it will check that if any user is associated with that department if yes
     * then it will
     * returns true and the department will not be deleted from the table
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_divisionVO
     *            DivisionDeptVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isUserExistsForDepartment(Connection p_con, DivisionDeptVO p_departmentVO) throws BTSLBaseException {

        final String METHOD_NAME = "isUserExistsForDepartment";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered VO::" + p_departmentVO);
        }
        boolean found = false;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM division_department DD,users U");
        sqlBuff.append(" WHERE DD.divdept_id=U.department AND DD.divdept_id=? AND U.status<>'N' AND U.status<>'C'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query::" + selectQuery);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_departmentVO.getDivDeptId());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isUserExistsForDepartment]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isUserExistsForDepartment]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
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
                _log.debug(METHOD_NAME, "Exiting  found=" + found);
            }
        }
        return found;
    }

    /**
     * Method deleteDivision.
     * This method is used to soft delete the division details from the
     * division_department table
     * 
     * @param p_con
     *            Connection
     * @param p_divisionVO
     *            DivisionDeptVO
     * @return updateCount int
     * @throws BTSLBaseException
     */

    public int deleteDivision(Connection p_con, DivisionDeptVO p_divisionVO) throws BTSLBaseException {

        final String METHOD_NAME = "deleteDivision";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entering VO " + p_divisionVO);
        }

        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;

        final boolean modified = this.recordModified(p_con, p_divisionVO.getDivDeptId(), p_divisionVO.getLastModified());
        try {
            final StringBuffer updateQueryBuff = new StringBuffer("UPDATE division_department SET");
            updateQueryBuff.append(" status=?,modified_on=?,modified_by=? WHERE divdept_id=?");
            final String updateQuery = updateQueryBuff.toString();
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(1, PretupsI.DIVISION_STATUS_DELETE);
            pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_divisionVO.getModifiedOn()));
            pstmtUpdate.setString(3, p_divisionVO.getModifiedBy());
            pstmtUpdate.setString(4, p_divisionVO.getDivDeptId());
            if (modified) {
                throw new BTSLBaseException(this, METHOD_NAME, "error.modify.true");
            }
            updateCount = pstmtUpdate.executeUpdate();
        } catch (BTSLBaseException be) {
            throw be;
        }

        catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[deleteDivision]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[deleteDivision]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
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
                _log.debug(METHOD_NAME, "Exiting updateCount " + updateCount);
            }
        }

        return updateCount;
    }

    /**
     * Method for loading Division List.
     * Used in(Users Action)
     * 
     * @author mohit.goel
     * @param p_con
     *            java.sql.Connection
     * @param p_divDeptType
     *            String
     * @param p_divDept
     *            String
     * @param p_statusUsed
     *            String
     * @param p_status
     *            String
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadDivisionDeptList(Connection p_con, String p_divDeptType, String p_divDept, String p_statusUsed, String p_status) throws BTSLBaseException {
        final String METHOD_NAME = "loadDivisionDeptList";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered p_divDeptType=" + p_divDeptType + " p_divDept=" + p_divDept + " p_statusUsed=" + p_statusUsed + " p_status=" + p_status);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT divdept_id,divdept_name,parent_id,status ");
        strBuff.append("FROM division_department WHERE divdept_type = ? and divdept = ? ");

        if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append("AND status = ? ");
        } else {
            strBuff.append("AND status IN (" + p_status + ")");
        }

        strBuff.append(" ORDER BY divdept_name");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_divDeptType);
            pstmt.setString(2, p_divDept);
            if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
                pstmt.setString(3, p_status);
            }
            rs = pstmt.executeQuery();
            ListValueVO listVO = null;
            while (rs.next()) {
                listVO = new ListValueVO(rs.getString("divdept_name"), rs.getString("divdept_id") + ":" + rs.getString("parent_id"));
                listVO.setStatus(rs.getString("status"));
                list.add(listVO);
            }

        } catch (SQLException sqe) {
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[loadDivisionDeptList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[loadDivisionDeptList]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(METHOD_NAME, "Exiting: divisionDeptList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method loadDivisionListForDept
     * This Method is used To load the divisions list for department
     * 
     * @param p_con
     *            java.sql.Connection
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */

    public ArrayList loadDivisionListForDept(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadDivisionListForDept";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT divdept_id,divdept_name,parent_id,divdept_type ");
        strBuff.append("FROM division_department WHERE divdept = ? and status<>'N' ");
        strBuff.append(" ORDER BY divdept_name");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.DIVDEPT_DIVISION);
            rs = pstmt.executeQuery();
            ListValueVO listVO = null;
            while (rs.next()) {
                listVO = new ListValueVO(rs.getString("divdept_name"), rs.getString("divdept_type") + ":" + rs.getString("divdept_id"));
                list.add(listVO);
            }

        } catch (SQLException sqe) {
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[loadDivisionListForDept]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            ;
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[loadDivisionListForDept]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(METHOD_NAME, "Exiting: divisionDeptList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method loadDivisionTypeList
     * This Method is used for loding the domain types for which division and
     * departments
     * are allowed(Status='Y')
     * 
     * @param p_con
     *            java.sql.Connection
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadDivisionTypeList(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadDivisionTypeList";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT domain_type_code,domain_type_name FROM domain_types WHERE div_dept_allowed='Y' ORDER BY domain_type_name");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            ListValueVO listVO = null;
            while (rs.next()) {
                listVO = new ListValueVO(rs.getString("domain_type_name"), rs.getString("domain_type_code"));
                list.add(listVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadDivisionTypeList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadDivisionTypeList]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(METHOD_NAME, "Exiting: divisionDeptList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * This method is used to check whether the record in the database is
     * 
     * @return boolean
     * @param con
     *            Connection
     * @param String
     * @param oldlastModified
     *            Long
     * @exception BTSLBaseException
     */

    public boolean recordModified(Connection p_con, String p_divisionID, long oldLastModified) throws BTSLBaseException {
        final String METHOD_NAME = "recordModified";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered: p_divisionID= " + p_divisionID + "oldLastModified= " + oldLastModified);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        final String sqlRecordModified = "SELECT modified_on FROM division_department WHERE divdept_id=?";
        Timestamp newLastModified = null;

        if ((oldLastModified) == 0) {
            return false;
        }
        try {
            _log.info(METHOD_NAME, "QUERY: sqlselect= " + sqlRecordModified);
            pstmt = p_con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, p_divisionID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }

            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " old=" + oldLastModified);
                _log.debug(METHOD_NAME, " new=" + newLastModified.getTime());
            }

            if (newLastModified.getTime() != oldLastModified) {
                modified = true;
            }

        }

        catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[recordModified]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[recordModified]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
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
                _log.debug(METHOD_NAME, "Exititng modified=" + modified);
            }
        }
        return modified;
    }

    /**
     * Method modifyDivision.
     * This method is used to Modify the Division detais in the
     * division_department table
     * 
     * @param p_con
     *            Connection
     * @param p_divisionVO
     *            DivisionDeptVO
     * @return updateCount int
     * @throws BTSLBaseException
     */

    public int modifyDivision(Connection p_con, DivisionDeptVO p_divisionVO) throws BTSLBaseException {

        final String METHOD_NAME = "modifyDivision";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entering VO " + p_divisionVO);
        }

        int updateCount = -1;
        // commented for DB2 OraclePreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate = null;
        final boolean modified = this.recordModified(p_con, p_divisionVO.getDivDeptId(), p_divisionVO.getLastModified());
        try {
            final StringBuffer updateQueryBuff = new StringBuffer("UPDATE division_department SET");
            updateQueryBuff.append(" divdept_name=?,divdept_short_code=?,");
            updateQueryBuff.append("modified_on=?,modified_by=?,status=? WHERE");
            updateQueryBuff.append(" divdept_id=?");

            final String updateQuery = updateQueryBuff.toString();
            // commented for DB2 pstmtUpdate =
            // (OraclePreparedStatement)p_con.prepareStatement(updateQuery);
            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);
            // commented for DB2
            // pstmtUpdate.setFormOfUse(1,OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(1, p_divisionVO.getDivDeptName());
            pstmtUpdate.setString(2, p_divisionVO.getDivDeptShortCode());
            pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_divisionVO.getModifiedOn()));
            pstmtUpdate.setString(4, p_divisionVO.getModifiedBy());
            pstmtUpdate.setString(5, p_divisionVO.getStatus());
            pstmtUpdate.setString(6, p_divisionVO.getDivDeptId());

            if (modified) {
                throw new BTSLBaseException(this, METHOD_NAME, "error.modify.true");
            }

            updateCount = pstmtUpdate.executeUpdate();

        } catch (BTSLBaseException be) {
            throw be;
        }

        catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[modifyDivision]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[modifyDivision]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
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
                _log.debug(METHOD_NAME, "Exiting updateCount " + updateCount);
            }
        }

        return updateCount;
    }

    /**
     * Method isDivisionNameExistsForModify
     * This method is used before modifying the record in the
     * division_department table
     * it will check for the uniqueness of the constraint divdept_name
     * and returns true if record exists in table
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_divisionVO
     *            DivisionDeptVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isDivisionNameExistsForModify(Connection p_con, DivisionDeptVO p_divisionVO) throws BTSLBaseException {

        final String METHOD_NAME = "isDivisionNameExistsForModify";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered VO::" + p_divisionVO);
        }

        boolean found = false;
        // commented for DB2 OraclePreparedStatement pstmtSelect=null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM division_department WHERE status<>'N' AND ");
        sqlBuff.append(" UPPER(divdept_name)=UPPER(?) AND divdept_id!=? AND divdept=? AND divdept_type=?");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query::" + selectQuery);
        }
        try {
            // commented for DB2
            // pstmtSelect=(OraclePreparedStatement)p_con.prepareStatement(selectQuery);
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            // commented for DB2
            // pstmtSelect.setFormOfUse(1,OraclePreparedStatement.FORM_NCHAR);
            pstmtSelect.setString(1, p_divisionVO.getDivDeptName());
            pstmtSelect.setString(2, p_divisionVO.getDivDeptId());
            pstmtSelect.setString(3, p_divisionVO.getDivDept());
            pstmtSelect.setString(4, p_divisionVO.getDivDeptType());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDivisionNameExistsForModify]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDivisionNameExistsForModify]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
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
                _log.debug(METHOD_NAME, "Exiting  found=" + found);
            }
        }
        return found;
    }

    /**
     * Method isDivisionShortCodeExistsForModify
     * This method is used before modifying the record in the
     * division_department table
     * it will check for the uniqueness of the constraint divdept_short_code
     * and returns true if record exists in table
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_divisionVO
     *            DivisionDeptVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isDivisionShortCodeExistsForModify(Connection p_con, DivisionDeptVO p_divisionVO) throws BTSLBaseException {

        final String METHOD_NAME = "isDivisionShortCodeExistsForModify";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered VO::" + p_divisionVO);
        }
        boolean found = false;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM division_department WHERE status<>'N' AND ");
        sqlBuff.append(" UPPER(divdept_short_code)=UPPER(?) AND divdept_id!=? AND divdept=? AND divdept_type=?");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query::" + selectQuery);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_divisionVO.getDivDeptShortCode());
            pstmtSelect.setString(2, p_divisionVO.getDivDeptId());
            pstmtSelect.setString(3, p_divisionVO.getDivDept());
            pstmtSelect.setString(4, p_divisionVO.getDivDeptType());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDivisionShortCodeExistsForModify]",
                "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDivisionShortCodeExistsForModify]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
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
                _log.debug(METHOD_NAME, "Exiting  found=" + found);
            }
        }
        return found;
    }
    public ArrayList loadDivisionDetails1(Connection p_con, UserVO p_userVO, DivisionVO p_divisionVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadDivisionDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered VO:" + p_divisionVO + "User VO:" + p_userVO);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList divisionList = new ArrayList();
        final StringBuffer strBuff = new StringBuffer("SELECT DD.divdept_id,DD.divdept_name,DD.divdept_short_code,DD.status,LK.lookup_name,");
        strBuff.append("DD.divdept_type,DD.divdept,DD.created_on,DD.created_by,DD.modified_on,DD.modified_by,");
        strBuff.append("DD.parent_id,DD.user_id,DT.domain_type_name FROM division_department DD,lookups LK,domain_types DT ");
        strBuff.append("WHERE DD.divdept=? AND DD.status<>'N' AND LK.lookup_code=DD.status AND DT.domain_type_code=DD.divdept_type ");
        strBuff.append("AND DT.div_dept_allowed='Y' AND LK.lookup_type=? ");
        strBuff.append("ORDER BY DD.divdept_name");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query= " + sqlSelect);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_divisionVO.getDivDept());
            pstmtSelect.setString(2, PretupsI.STATUS_TYPE);

            rs = pstmtSelect.executeQuery();
            DivisionVO divisionVO = null;
            int radioIndex = 0;
            while (rs.next()) {
                divisionVO = new DivisionVO();
                divisionVO.setDivDeptId(rs.getString("divdept_id"));
                divisionVO.setDivDeptName(rs.getString("divdept_name"));
                divisionVO.setDivDeptTypeName(rs.getString("domain_type_name"));
                divisionVO.setDivDeptType(rs.getString("divdept_type"));
                divisionVO.setDivDeptShortCode(rs.getString("divdept_short_code"));
                divisionVO.setLastModified(rs.getTimestamp("modified_on").getTime());
                divisionVO.setParentId(rs.getString("parent_id"));
                divisionVO.setRadioIndex(radioIndex);
                divisionVO.setStatus(rs.getString("status"));
                divisionVO.setStatusName(rs.getString("lookup_name"));
                radioIndex++;
                divisionList.add(divisionVO);
            }
        }

        catch (SQLException sqe) {
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[loadDivisionDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[loadDivisionDetails]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");

        } finally {
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
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting size=" + divisionList.size());
            }
        }

        return divisionList;
    }
    
    
    
    
    
    /**
     * Method for loading Division List.
     * Used in(Users Action)
     * 
     * @author mohit.goel
     * @param p_con
     *            java.sql.Connection
     * @param divID
     *            String
     
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadDepartmentListBYDivID(Connection p_con, String divID) throws BTSLBaseException {
        final String METHOD_NAME = "loadDepartmentListBYDivID";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered DivisionID = " + divID);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT divdept_id,divdept_name,parent_id,status FROM division_department  WHERE DIVDEPT='DEPARTMENT' AND PARENT_ID =?  and status ='Y' ");
        strBuff.append(" ORDER BY divdept_name");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, divID);
            rs = pstmt.executeQuery();
            ListValueVO listVO = null;
            while (rs.next()) {
                listVO = new ListValueVO(rs.getString("divdept_name"), rs.getString("divdept_id") + ":" + rs.getString("parent_id"),rs.getString("divdept_id"),null);
                listVO.setStatus(rs.getString("status"));
                list.add(listVO);
            }

        } catch (SQLException sqe) {
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[loadDepartmentListBYDivID]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[loadDepartmentListBYDivID]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(METHOD_NAME, "Exiting: divisionDeptList size=" + list.size());
            }
        }
        return list;
    }


}
