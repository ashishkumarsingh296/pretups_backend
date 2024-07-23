/*
 * #CategoryGradeDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Aug 5, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.domain.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;

public class CategoryGradeDAO {

    private Log _log = LogFactory.getFactory().getInstance(CategoryGradeDAO.class.getName());

    /**
     * Constructor for CategoryGradeDAO.
     */
    public CategoryGradeDAO() {
        super();
    }

    /**
     * Method loadGradeList.
     * This method is used to load categories grade details from channel_grade
     * table
     * 
     * @param p_con
     *            Connection
     * @param p_categoryCode
     *            String
     * @return gradeList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadGradeList(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadGradeList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_categoryCode=" + p_categoryCode);
        }

         
         
        ArrayList gradeList = new ArrayList();
        StringBuilder strBuff = new StringBuilder("SELECT G.grade_code,G.grade_name,G.category_code,G.status,");
        strBuff.append("G.created_on,G.created_by,G.modified_on,G.modified_by,G.is_default_grade,G.is_2fa_allowed FROM channel_grades G");
        strBuff.append(" WHERE G.category_code=? AND G.status=? ORDER BY grade_name");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try (PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);){
            
            pstmtSelect.setString(1, p_categoryCode);
            pstmtSelect.setString(2, PretupsI.GRADE_STATUS_ACTIVE);

            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            GradeVO gradeVO = null;
            int radioIndex = 0;
            while (rs.next()) {
                gradeVO = new GradeVO();
                gradeVO.setGradeCode(SqlParameterEncoder.encodeParams(rs.getString("grade_code")));
                gradeVO.setGradeName(SqlParameterEncoder.encodeParams(rs.getString("grade_name")));
                gradeVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
                gradeVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                gradeVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("status")));
                // added by nilesh:for default grade
                gradeVO.setDefaultGrade(SqlParameterEncoder.encodeParams(rs.getString("is_default_grade")));
                //2FA
                gradeVO.setTwoFAallowed(SqlParameterEncoder.encodeParams(rs.getString("is_2fa_allowed")));
                
                gradeVO.setRadioIndex(radioIndex);
                radioIndex++;
                gradeList.add(gradeVO);
            }
        }
        }
        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[loadGradeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[loadGradeList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);

        } finally {
        	
            
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting size=" + gradeList.size());
            }
        }

        return gradeList;
    }

    /**
     * Method saveGrade.
     * This method is used to save the Grade informaion in channel_grades table
     * If there is any error then throws the SQLException
     * 
     * @param p_con
     *            Connection
     * @param p_gradeVO
     *            GradeVO
     * @return addCount int
     * @throws BTSLBaseException
     */

    public int saveGrade(Connection p_con, GradeVO p_gradeVO) throws BTSLBaseException {
        final String methodName = "saveGrade";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered VO:" + p_gradeVO);
        }

         
        int addCount = -1;
        StringBuilder strBuff = new StringBuilder("INSERT INTO channel_grades (grade_code,grade_name,");
        strBuff.append(" category_code,status,created_on,created_by,modified_on,modified_by,is_default_grade,is_2fa_allowed)");
        strBuff.append(" VALUES(UPPER(?),?,?,?,?,?,?,?,?,?)");
        String sqlInsert = strBuff.toString();
        try (PreparedStatement pstmtInsert =  p_con.prepareStatement(sqlInsert);){
            
            pstmtInsert.setString(1, p_gradeVO.getGradeCode());
           
            pstmtInsert.setString(2, p_gradeVO.getGradeName());
            pstmtInsert.setString(3, p_gradeVO.getCategoryCode());
            pstmtInsert.setString(4, p_gradeVO.getStatus());
            pstmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_gradeVO.getCreatedOn()));
            pstmtInsert.setString(6, p_gradeVO.getCreatedBy());
            pstmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(p_gradeVO.getModifiedOn()));
            pstmtInsert.setString(8, p_gradeVO.getModifiedBy());
            // added by nilesh : for default grade
            pstmtInsert.setString(9, p_gradeVO.getDefaultGrade());
            // added for 2FA
            pstmtInsert.setString(10, p_gradeVO.getTwoFAallowed());
            addCount = pstmtInsert.executeUpdate();
        }

        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[saveGrade]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[saveGrade]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);

        } finally {
        	
        	
           
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting addCount=" + addCount);
            }
        }

        return addCount;
    }

    /**
     * This method is used before adding the record in the channel_grades table
     * it will check for the uniqueness of the grade_code column in the table
     * if the user enterd the same grade code that exists in the database
     * the method return true and record will not inserted
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_gradeVO
     *            GradeVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsGradeCodeForAdd(Connection p_con, GradeVO p_gradeVO) throws BTSLBaseException {

        final String methodName = "isExistsGradeCodeForAdd";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered params p_gradeVO::" + p_gradeVO);
        }

        
        boolean found = false;
        StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM channel_grades WHERE grade_code=?");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }

        try( PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
          
            pstmtSelect.setString(1, p_gradeVO.getGradeCode().toUpperCase());
            try( ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed::" + selectQuery);
            }

            if (rs.next()) {
                found = true;
            }
        }
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[isExistsGradeCodeForAdd]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[isExistsGradeCodeForAdd]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }

        finally {
        	
        	
            
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExists found=" + found);
            }
        }

        return found;
    }

    /**
     * This method is used before adding the record in the channel_grades table
     * it will check for the uniqueness of the grade_name column in the table
     * if the user enterd the same grade code that exists in the database
     * the method return true and record will not inserted
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_gradeVO
     *            GradeVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsGradeNameForAdd(Connection p_con, GradeVO p_gradeVO) throws BTSLBaseException {

        final String methodName = "isExistsGradeNameForAdd";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered params p_gradeVO::" + p_gradeVO);
        }

        
        
        boolean found = false;
        StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM channel_grades WHERE UPPER(grade_name)=UPPER(?) AND status<>'N'");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }

        try (  PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);){
          
           
            pstmtSelect.setString(1, p_gradeVO.getGradeName());
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed::" + selectQuery);
            }

            if (rs.next()) {
                found = true;
            }
        }
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[isExistsGradeNameForAdd]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isExistsGradeCodeForAdd", "error.general.sql.processing",sqle);
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[isExistsGradeNameForAdd]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }

        finally {
        	
           
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExists found=" + found);
            }
        }

        return found;
    }

    // isExistsGradeNameForModify

    /**
     * This method is used before modifying the record in the channel_grades
     * table
     * it will check for the uniqueness of the grade_name column in the table
     * if the user enterd the same grade code that exists in the database
     * the method return true and record will not inserted
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_gradeVO
     *            GradeVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsGradeNameForModify(Connection p_con, GradeVO p_gradeVO) throws BTSLBaseException {

        final String methodName = "isExistsGradeNameForModify";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered params p_gradeVO::" + p_gradeVO);
        }
        
        
        boolean found = false;
        StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM channel_grades WHERE UPPER(grade_name)=UPPER(?)");
        sqlBuff.append("AND grade_code!=? AND status<>'N'");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }

        try (PreparedStatement pstmtSelect =  p_con.prepareStatement(selectQuery);){
            
            
            pstmtSelect.setString(1, p_gradeVO.getGradeName());
            pstmtSelect.setString(2, p_gradeVO.getGradeCode());
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed::" + selectQuery);
            }

            if (rs.next()) {
                found = true;
            }
        }
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[isExistsGradeNameForModify]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[isExistsGradeNameForModify]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }

        finally {
        	
           
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExists found=" + found);
            }
        }

        return found;
    }

    /**
     * Method modifyGrade.
     * This method is used to Modify the Details of Grade in the channel_grades
     * table
     * 
     * @param p_con
     *            Connection
     * @param p_gradeVO
     *            GradeVO
     * @return updateCount int
     * @throws BTSLBaseException
     */

    public int modifyGrade(Connection p_con, GradeVO p_gradeVO) throws BTSLBaseException {

        final String methodName = "modifyGrade";
        LogFactory.printLog(methodName, "Entering VO " + p_gradeVO, _log);
       
        int updateCount = -1;
        

        try {
        	StringBuilder updateQueryBuff = new StringBuilder("UPDATE channel_grades SET");
            updateQueryBuff.append(" grade_name=?,status=?,modified_on=?,modified_by=?,is_default_grade=?,is_2fa_allowed=? WHERE grade_code=?");
            String insertQuery = updateQueryBuff.toString();
            try(PreparedStatement pstmtUpdate =  p_con.prepareStatement(insertQuery);)
            {
          
            pstmtUpdate.setString(1, p_gradeVO.getGradeName());
            pstmtUpdate.setString(2, p_gradeVO.getStatus());
            pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_gradeVO.getModifiedOn()));
            pstmtUpdate.setString(4, p_gradeVO.getModifiedBy());
            // added by nilesh
            pstmtUpdate.setString(5, p_gradeVO.getDefaultGrade());
            //2FA
            pstmtUpdate.setString(6, p_gradeVO.getTwoFAallowed());
            pstmtUpdate.setString(7, p_gradeVO.getGradeCode());
            boolean modified = this.recordModified(p_con, p_gradeVO.getGradeCode(), p_gradeVO.getLastModifiedTime(), methodName);

            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }

            updateCount = pstmtUpdate.executeUpdate();
            LogFactory.printLog(methodName, "Query Executed= " + insertQuery, _log);
           
        } 
        }catch (BTSLBaseException be) {
            throw be;
        }

        catch (SQLException sqle) {
            _log.error(methodName, " SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[modifyGrade]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "modifyGrade()", "error.general.sql.processing",sqle);
        }

        catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[modifyGrade]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }

        finally {
        	
            
        	LogFactory.printLog(methodName, "Exiting updateCount " + updateCount, _log);
        	
        }

        return updateCount;
    }

    /**
     * Method deleteGrade.
     * This method is used to delete(set the status to 'N') the grade
     * 
     * @param p_con
     *            Connection
     * @param p_gradeVO
     *            GradeVO
     * @return updateCount int
     * @throws BTSLBaseException
     */

    public int deleteGrade(Connection p_con, GradeVO p_gradeVO) throws BTSLBaseException {

        final String methodName = "deleteGrade";
        LogFactory.printLog(methodName, "Entering VO " + p_gradeVO, _log);
       

        int updateCount = -1;
         

        try {
        	StringBuilder updateQueryBuff = new StringBuilder("UPDATE channel_grades SET status=?,");
            updateQueryBuff.append("modified_on=?,modified_by=? WHERE grade_code=? AND category_code=? AND is_default_grade=? ");
            String updateQuery = updateQueryBuff.toString();
            try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
            {
            pstmtUpdate.setString(1, PretupsI.GRADE_STATUS_DELETE);
            pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_gradeVO.getModifiedOn()));
            pstmtUpdate.setString(3, p_gradeVO.getModifiedBy());
            pstmtUpdate.setString(4, p_gradeVO.getGradeCode());
            pstmtUpdate.setString(5, p_gradeVO.getCategoryCode());
            // added by nilesh
            pstmtUpdate.setString(6, p_gradeVO.getDefaultGrade());
            boolean modified = this.recordModified(p_con, p_gradeVO.getGradeCode(), p_gradeVO.getLastModifiedTime(), methodName);

            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = pstmtUpdate.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed= " + updateQuery);
            }
        } 
        }catch (BTSLBaseException be) {
            throw be;
        }

        catch (SQLException sqle) {
            _log.error(methodName, " SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[deleteGrade]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }

        catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[deleteGrade]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }

        finally {
        	
        	LogFactory.printLog(methodName, "Exiting updateCount " + updateCount, _log);
           
        }

        return updateCount;
    }

    /**
     * This method is used to check whether the record in the database is
     * modified or not If there is any error then throws the SQLException
     * 
     * @return boolean
     * @param con
     *            Connection
     * @param p_Code
     *            String
     * @param oldlastModified
     *            Long
     * @exception BTSLBaseException
     */

    public boolean recordModified(Connection con, String p_Code, long oldLastModified, String p_fromFunction) throws BTSLBaseException {
        final String methodName = "recordModified";
        LogFactory.printLog(methodName, "Entered: p_Code= " + p_Code + "oldLastModified= " + oldLastModified, _log);
       
         
        
        boolean modified = false;
        String sqlRecordModified = null;
        if ("modifyGrade".equalsIgnoreCase(p_fromFunction) || "deleteGrade".equalsIgnoreCase(p_fromFunction)) {
            sqlRecordModified = "SELECT modified_on FROM channel_grades WHERE grade_code=?";
        }
        Timestamp newLastModified = null;
        if ((oldLastModified) == 0) {
            return false;
        }
        try(PreparedStatement pstmt = con.prepareStatement(sqlRecordModified);) {
            _log.info(methodName, "QUERY: sqlselect= " + sqlRecordModified);
            
            pstmt.setString(1, p_Code);
            try( ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " old=" + oldLastModified);
                if (newLastModified != null) {
                    _log.debug(methodName, " new=" + newLastModified.getTime());
                } else {
                    _log.debug(methodName, " new=null");
                }
            }

            if (newLastModified != null && newLastModified.getTime() != oldLastModified) {
                modified = true;
            }

        }
        }
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[recordModified]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[recordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "recordModified()", "error.general.processing",e);
        }

        finally {
        	
            
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exititng modified=" + modified);
            }
        }
        return modified;
    }

    /**
     * Method 
     * This method is used before deleting the record in the channel_grades
     * table
     * If any active user found then the grade will not be deleted
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_gradeVO
     *            GradeVO
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isUserExistsForGrade(Connection p_con, GradeVO p_gradeVO) throws BTSLBaseException {

        final String methodName = "isUserExistsForGrade";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered params p_gradeVO::" + p_gradeVO);
        }

        
        boolean found = false;
        StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM users u,channel_users cu WHERE u.user_id=cu.user_id");
        sqlBuff.append(" AND u.status<>'N' AND u.status<>'C' AND cu.user_grade=?");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }

        try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
           
            pstmtSelect.setString(1, p_gradeVO.getGradeCode().toUpperCase());
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed::" + selectQuery);
            }
            if (rs.next()) {
                found = true;
            }
        } 
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[isUserExistsForGrade]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[isUserExistsForGrade]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }

        finally {
        	
           
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExists found=" + found);
            }
        }

        return found;
    }

    /**
     * Varun
     * Method loadGradeList.
     * This method is used to load categories grade details from channel_grade
     * table
     * 
     * @param p_con
     *            Connection
     * @param p_categoryCode
     *            String
     * @return gradeList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadGradeList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadGradeList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_con");
        }

         
        
        ArrayList gradeList = new ArrayList();
        StringBuilder strBuff = new StringBuilder("SELECT G.grade_code,G.grade_name,G.category_code,G.status,");
        strBuff.append("G.created_on,G.created_by,G.modified_on,G.modified_by,G.is_default_grade FROM channel_grades G");
        strBuff.append(" WHERE G.status=? ORDER BY grade_name");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
         
            pstmtSelect.setString(1, PretupsI.GRADE_STATUS_ACTIVE);

            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            GradeVO gradeVO = null;
            int radioIndex = 0;
            while (rs.next()) {
                gradeVO = new GradeVO();
                gradeVO.setGradeCode(rs.getString("grade_code"));
                gradeVO.setGradeName(rs.getString("grade_name"));
                gradeVO.setCategoryCode(rs.getString("category_code"));
                gradeVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                gradeVO.setStatus(rs.getString("status"));
                // added by nilesh
                gradeVO.setDefaultGrade(rs.getString("is_default_grade"));
                gradeVO.setRadioIndex(radioIndex);
                radioIndex++;
                gradeList.add(gradeVO);
            }
        }
        }
        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[loadGradeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[loadGradeList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);

        } finally {
        	
            
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting size=" + gradeList.size());
            }
        }

        return gradeList;
    }

    /**
     * @param p_con
     * @param p_categoryCode
     * @return
     * @throws BTSLBaseException
     * @author nilesh.kumar
     */
    public int updateGrade(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "updateGrade";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_categoryCode:" + p_categoryCode);
        }

         
        int updateCount = -1;
        try {

        	StringBuilder strBuffDefault = new StringBuilder("UPDATE channel_grades ");
            strBuffDefault.append("SET is_default_grade=? WHERE category_code=? and is_default_grade=? ");

            String updateQueryBuff = strBuffDefault.toString();
           try(PreparedStatement pstmtUpdate =  p_con.prepareStatement(updateQueryBuff);)
           {
            int i = 1;
            pstmtUpdate.setString(i++, PretupsI.NO);
            pstmtUpdate.setString(i++, p_categoryCode);
            pstmtUpdate.setString(i++, PretupsI.YES);
            updateCount = pstmtUpdate.executeUpdate();
        } 
    }catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[updateGrade]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[updateGrade]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);

        } finally {
        	
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting addCount=" + updateCount);
            }
        }

        return updateCount;
    }

    /**
     * @param p_con
     * @param p_categoryCode
     * @return
     * @throws BTSLBaseException
     * @author vikas.kumar
     */
    public GradeVO loadDefaultGradeListForCategory(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadDefaultGradeListForCategory";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_categoryCode=" + p_categoryCode);
        }

         
        
        GradeVO gradeVO = null;
        StringBuilder strBuff = new StringBuilder(" SELECT grade_code,grade_name,category_code,status,");
        strBuff.append(" created_on,created_by,modified_on,modified_by,is_default_grade FROM channel_grades ");
        strBuff.append(" WHERE category_code=? AND status=? and is_default_grade=? ORDER BY grade_name");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
           
            pstmtSelect.setString(1, p_categoryCode);
            pstmtSelect.setString(2, PretupsI.GRADE_STATUS_ACTIVE);
            pstmtSelect.setString(3, PretupsI.YES);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {

            while (rs.next()) {
                gradeVO = new GradeVO();
                gradeVO.setGradeCode(rs.getString("grade_code"));
                gradeVO.setGradeName(rs.getString("grade_name"));
                gradeVO.setCategoryCode(rs.getString("category_code"));
                gradeVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                gradeVO.setStatus(rs.getString("status"));
                gradeVO.setDefaultGrade(rs.getString("is_default_grade"));

            }
        }
        }

        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[loadDefaultGradeListForCategory]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[loadDefaultGradeListForCategory]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);

        } finally {
        	
        	
            
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting gradeVO=" + gradeVO);
            }
        }

        return gradeVO;
    }
    
    public ArrayList<GradeVO> viewGradeList(Connection p_con, String categoryCode) throws BTSLBaseException {
        final String methodName = "viewGradeList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

         
		ArrayList<GradeVO> gradeList = new ArrayList<GradeVO>();
        StringBuilder strBuff = new StringBuilder("SELECT *");
        strBuff.append(" FROM channel_grades G WHERE");
        if(!"ALL".equalsIgnoreCase(categoryCode)) {
        	strBuff.append(" category_code=? AND ");
        }
        strBuff.append("status=? ");
        strBuff.append(" ORDER BY grade_name");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try (PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);){
        	
            pstmtSelect.setString(1, categoryCode);
            pstmtSelect.setString(2, PretupsI.GRADE_STATUS_ACTIVE);
            
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            GradeVO gradeVO = null;
            while (rs.next()) {
                gradeVO = new GradeVO();
                gradeVO.setGradeCode(SqlParameterEncoder.encodeParams(rs.getString("grade_code")));
                gradeVO.setGradeName(SqlParameterEncoder.encodeParams(rs.getString("grade_name")));
                gradeVO.setDefaultGrade(rs.getString("is_default_grade"));
                gradeList.add(gradeVO);
            }
        }
        }
        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[loadGradeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[loadGradeList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);

        } finally {
        	
            
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting size=" + gradeList.size());
            }
        }

        return gradeList;
    }
    
    public ArrayList<GradeVO> viewGradeListByGradeCode(Connection p_con, String gradeCode) throws BTSLBaseException {
        final String methodName = "viewGradeList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

         
		ArrayList<GradeVO> gradeList = new ArrayList<GradeVO>();
        StringBuilder strBuff = new StringBuilder(" SELECT grade_code,grade_name,category_code,status,");
        strBuff.append(" created_on,created_by,modified_on,modified_by,is_default_grade");
        strBuff.append(" FROM channel_grades G");
        strBuff.append(" WHERE grade_code=?");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try (PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);){
        	
            pstmtSelect.setString(1, gradeCode);

            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            GradeVO gradeVO = null;
            while (rs.next()) {
                gradeVO = new GradeVO();
                gradeVO.setGradeCode(SqlParameterEncoder.encodeParams(rs.getString("grade_code")));
                gradeVO.setGradeName(SqlParameterEncoder.encodeParams(rs.getString("grade_name")));
                gradeVO.setCategoryCode(rs.getString("category_code"));
                gradeVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                gradeVO.setStatus(rs.getString("status"));
                gradeVO.setDefaultGrade(rs.getString("is_default_grade"));
                gradeVO.setModifiedBy(rs.getString("modified_by"));
                gradeVO.setCreatedOn(rs.getDate("created_on"));
                gradeVO.setModifiedOn(rs.getDate("modified_on"));
                gradeList.add(gradeVO);
            }
        }
        }
        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[loadGradeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[loadGradeList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);

        } finally {
        	
            
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting size=" + gradeList.size());
            }
        }

        return gradeList;
    }

}
