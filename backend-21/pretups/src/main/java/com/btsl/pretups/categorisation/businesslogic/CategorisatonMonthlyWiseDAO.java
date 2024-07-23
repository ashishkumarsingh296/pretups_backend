/**
 * @(#)CategorisatonMonthlyWiseDao.java
 * 
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Author Date History
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Narendra Kumar 12 JAN 2013 Initial
 *                                      Creation
 * 
 * 
 *                                      This class is used for Categorisation on
 *                                      Monthly Basis.
 * 
 */
package com.btsl.pretups.categorisation.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.util.BTSLUtil;

/**
 * @author 
 *
 */
public class CategorisatonMonthlyWiseDAO {
    private Log log = LogFactory.getLog(CategorisatonMonthlyWiseDAO.class.getName());

    /*
     * @param mapping
     * 
     * @param form
     * 
     * @param request
     * 
     * @param response
     * 
     * @return ActionForward
     * This methods is used to load the user Geography list
     */
    public ArrayList loadUserGeographyList(Connection pCon, String p_userId, String p_networkCode) throws BTSLBaseException {
    	final String methodName = "loadUserGeographyList";
    	StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_userId=" );
        	loggerValue.append(p_userId);
        	log.debug(methodName,loggerValue);
            
        }
        
         
        StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT ug.grph_domain_code,gd.grph_domain_name,gdt.grph_domain_type_name,gdt.sequence_no,gd.status, ");
        strBuff.append(" gdt.grph_domain_type FROM user_geographies ug,geographical_domains gd,geographical_domain_types gdt ");
        strBuff.append("WHERE ug.user_id = ? ");
        strBuff.append("AND gd.network_code = ? ");
        strBuff.append("AND gd.grph_domain_code = ug.grph_domain_code ");
        strBuff.append("AND gdt.grph_domain_type = gd.grph_domain_type ");
        strBuff.append(" ORDER BY gd.grph_domain_name");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=" );
        	loggerValue.append(sqlSelect);
        	log.debug(methodName,loggerValue);
        }
        ArrayList regionlist = new ArrayList();
        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_userId);
            pstmt.setString(2, p_networkCode);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            UserGeographiesVO geographyVO = null;

            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("grph_domain_name"), rs.getString("grph_domain_code"));
                listValueVO.setType(rs.getString("grph_domain_type_name"));
                listValueVO.setOtherInfo(rs.getString("sequence_no"));
                listValueVO.setStatusType(rs.getString("status"));
                regionlist.add(listValueVO);
            }
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[loadUserGeographyList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserGeographyList", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[loadUserGeographyList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            
            if (log.isDebugEnabled()) {
                loggerValue.setLength(0);
            	loggerValue.append("Exiting: userGeographyList size=" );
            	loggerValue.append(regionlist.size());
            	log.debug(methodName,loggerValue);
            }
        }
        return regionlist;
    }

    /*
     * @param mapping
     * 
     * @param form
     * 
     * @param request
     * 
     * @param response
     * 
     * @return ActionForward
     * This methods is used to add the user details in DB
     */
    public int addCategorisationDetails(Connection pCon, CategorisationVO pCategorisationVO) throws BTSLBaseException {
        
        final String methodName = "addCategorisationDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        int addCount = 0;
        int insertCount = 0;
        if (log.isDebugEnabled()) {
            log.debug("addCategorisationDetails", "Entered: CategorisationVO= " + pCategorisationVO);
        }

        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO user_profile_classification (profile_id, ");
            strBuff.append(" profile_name,domain_code,category_code,geolevel1, ");
            strBuff.append("geolevel2,txn_type,from_count,to_count,from_amount,");
            strBuff.append(" to_amount,from_active_days,to_active_days,class_type, ");
            strBuff.append("created_by,created_on,modified_by,modified_on)");
            strBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            String insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug("addCategorisationDetails", "QUERY sqlInsert:" + insertQuery);
            }
            try(PreparedStatement psmtInsert =  pCon.prepareStatement(insertQuery);)
            {
            psmtInsert.setString(1, pCategorisationVO.getProfileId());
            psmtInsert.setString(2, pCategorisationVO.getProfileName());
            psmtInsert.setString(3, pCategorisationVO.getChannelDomain());
            psmtInsert.setString(4, pCategorisationVO.getCategoryCode());
            psmtInsert.setString(5, pCategorisationVO.getRegion());
            psmtInsert.setString(6, pCategorisationVO.getAreaCode());
            psmtInsert.setString(7, pCategorisationVO.getTransferType());
            psmtInsert.setString(8, pCategorisationVO.getCountBegin());
            psmtInsert.setString(9, pCategorisationVO.getCountEnd());
            psmtInsert.setString(10, pCategorisationVO.getAmountBegin());
            psmtInsert.setString(11, pCategorisationVO.getAmountEnd());
            psmtInsert.setString(12, pCategorisationVO.getActivedaytBegin());
            psmtInsert.setString(13, pCategorisationVO.getActivedaytEnd());
            psmtInsert.setString(14, pCategorisationVO.getClassType());
            psmtInsert.setString(15, pCategorisationVO.getCreatedBy());
            psmtInsert.setTimestamp(16, BTSLUtil.getTimestampFromUtilDate(pCategorisationVO.getCreatedOn()));
            psmtInsert.setString(17, pCategorisationVO.getModifiedBy());
            psmtInsert.setTimestamp(18, BTSLUtil.getTimestampFromUtilDate(pCategorisationVO.getModifiedOn()));
            insertCount = psmtInsert.executeUpdate();

        } 
        }// end of try
        catch (SQLException sqle) {
            log.error("addCategorisationDetails", "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategorisatonMonthlyWiseDao[addCategorisationDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addCategorisationDetails", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error("addCategorisationDetails", "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategorisatonMonthlyWiseDao[addCategorisationDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addCategorisationDetails", "error.general.processing");
        } // end of catch
        finally {
           
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("addCategorisationDetails");
            	loggerValue.append("Exiting: insertCount=");
            	loggerValue.append(insertCount);
            	log.debug(methodName,loggerValue);
            }
        } // end of finally

        return insertCount;
    }

    /*
     * @param mapping
     * 
     * @param form
     * 
     * @param request
     * 
     * @param response
     * 
     * @return ActionForward
     * This methods is used to check if the profile exist
     */
    /**
     * @param pCon
     * @param pCategorisationVO
     * @return  existFlag
     * @throws BTSLBaseException
     */
    public boolean isCategorisationProfileExist(Connection pCon, CategorisationVO pCategorisationVO) throws BTSLBaseException {
    	final String methodName = "isCategorisationProfileExist";
    	 StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
            loggerValue.setLength(0);
        	loggerValue.append("Entered: pCategorisationVO=" );
        	loggerValue.append(pCategorisationVO);
        	log.debug(methodName,loggerValue);
        }

        
         
        
        boolean existFlag = false;
        StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT 1 FROM user_profile_classification ");
        strBuff.append(" WHERE domain_code = ? AND category_code = ? AND geolevel1 = ? ");
        strBuff.append(" AND geolevel2 = ? AND txn_type = ? AND  class_type = ? ");
        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        
            loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect="  );
        	loggerValue.append(sqlSelect);
        	log.debug(methodName,loggerValue);
        }
        try(PreparedStatement pstmtSelect = pCon.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, pCategorisationVO.getChannelDomain());
            pstmtSelect.setString(2, pCategorisationVO.getCategoryCode());
            pstmtSelect.setString(3, pCategorisationVO.getRegion());
            pstmtSelect.setString(4, pCategorisationVO.getAreaCode());
            pstmtSelect.setString(5, pCategorisationVO.getTransferType());
            pstmtSelect.setString(6, pCategorisationVO.getClassType());
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                existFlag = true;
            }
        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategorisatonMonthlyWiseDao[isCategorisationProfileExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isCategorisationProfileExist", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategorisatonMonthlyWiseDao[isCategorisationProfileExist]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	
            if (log.isDebugEnabled()) {
                loggerValue.setLength(0);
                loggerValue.append("isCategorisationProfileExist :" );
            	loggerValue.append("Exiting: existFlag=" );
            	loggerValue.append(existFlag);
            	log.debug(methodName,loggerValue);
            }
        }
        return existFlag;
    }

    /*
     * @param mapping
     * 
     * @param form
     * 
     * @param request
     * 
     * @param response
     * 
     * @return ActionForward
     * This methods is used to load the profile detail from DB
     */
    /**
     * @param pCon
     * @param pCategorisationVO
     * @throws BTSLBaseException
     */
    public void loadCategorisationProfileDetails(Connection pCon, CategorisationVO pCategorisationVO) throws BTSLBaseException {
    	final String methodName = "loadCategorisationProfileDetails";
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("loadCategorisationProfileDetails" );
        	loggerValue.append("Entered: CategorisationVO= " );
        	loggerValue.append(pCategorisationVO );
        	log.debug(methodName,loggerValue);
            
        }
        
        
        
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT profile_id,profile_name,domain_code,geolevel1, ");
        strBuff.append(" geolevel2,txn_type,from_count,to_count,from_amount, ");
        strBuff.append(" to_amount,from_active_days,to_active_days,class_type, ");
        strBuff.append(" created_by,created_on,modified_by,modified_on FROM user_profile_classification ");
        strBuff.append(" WHERE domain_code = ? AND category_code = ? AND geolevel1 = ? ");
        strBuff.append(" AND geolevel2 = ? AND txn_type = ? AND  class_type = ? ");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("loadCategorisationProfileDetails()", "QUERY sqlSelect=" + sqlSelect);
        }

        try (PreparedStatement pstmtSelect = pCon.prepareStatement(sqlSelect);){
            
            pstmtSelect.setString(1, pCategorisationVO.getChannelDomain());
            pstmtSelect.setString(2, pCategorisationVO.getCategoryCode());
            pstmtSelect.setString(3, pCategorisationVO.getRegion());
            pstmtSelect.setString(4, pCategorisationVO.getAreaCode());
            pstmtSelect.setString(5, pCategorisationVO.getTransferType());
            pstmtSelect.setString(6, pCategorisationVO.getClassType());

            try(ResultSet rs = pstmtSelect.executeQuery();)
            {

            while (rs.next()) {
                pCategorisationVO.setProfileId(rs.getString("profile_id"));
                pCategorisationVO.setProfileName(rs.getString("profile_name"));
                pCategorisationVO.setCountBegin(rs.getString("from_count"));
                pCategorisationVO.setCountEnd(rs.getString("to_count"));
                pCategorisationVO.setAmountBegin(rs.getString("from_amount"));
                pCategorisationVO.setAmountEnd(rs.getString("to_amount"));
                pCategorisationVO.setActivedaytBegin(rs.getString("from_active_days"));
                pCategorisationVO.setActivedaytEnd(rs.getString("to_active_days"));
                pCategorisationVO.setCreatedBy(rs.getString("created_by"));
                pCategorisationVO.setCreatedOn(rs.getTimestamp("created_on"));
                pCategorisationVO.setModifiedBy(rs.getString("modified_by"));
                pCategorisationVO.setModifiedOn(rs.getTimestamp("modified_on"));
            }
            }
        } catch (SQLException sqe) {
            log.error("loadCategorisationProfileDetails()", "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategorisatonMonthlyWiseDao[loadCategorisationProfileDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCommissionProfileSetVersion()", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error("loadCategorisationProfileDetails()", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategorisatonMonthlyWiseDao[loadCategorisationProfileDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCategorisationProfileDetails()", "error.general.processing");
        } finally {
        	

            if (log.isDebugEnabled()) {
                log.debug("loadCategorisationProfileDetails()", "Exiting: loadCategorisationProfileDetails size=" + pCategorisationVO.toString());
            }
        }
    }

    /*
     * @param mapping
     * 
     * @param form
     * 
     * @param request
     * 
     * @param response
     * 
     * @return ActionForward
     * This methods is used to delete the user profile
     */
    /**
     * @param pCon
     * @param pCategorisationVO
     * @param pClassType
     * @return deleteCount
     * @throws BTSLBaseException
     */
    public int deleteCategorisationGrade(Connection pCon, CategorisationVO pCategorisationVO, String pClassType) throws BTSLBaseException {
    	final String methodName = "deleteCategorisationGrade";
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered: pCategorisationVO= ");
        	loggerValue.append(pCategorisationVO);
        	loggerValue.append(" pClassType= ");
        	loggerValue.append(pClassType);
            log.debug(methodName, loggerValue );
        }
        
         
        int deleteCount = 0;
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append(" DELETE FROM user_profile_classification ");
            strBuff.append(" WHERE domain_code = ? AND category_code = ? AND geolevel1 = ? ");
            strBuff.append(" AND geolevel2 = ? AND txn_type = ? AND class_type = ? ");

            String deleteQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlDelete:" );
            	loggerValue.append(deleteQuery);
                log.debug(methodName, loggerValue );
                
               
            }
            try(PreparedStatement psmtDelete = pCon.prepareStatement(deleteQuery);)
            {

            psmtDelete.setString(1, pCategorisationVO.getChannelDomain());
            psmtDelete.setString(2, pCategorisationVO.getCategoryCode());
            psmtDelete.setString(3, pCategorisationVO.getRegion());
            psmtDelete.setString(4, pCategorisationVO.getAreaCode());
            psmtDelete.setString(5, pCategorisationVO.getTransferType());
            psmtDelete.setString(6, pClassType);

            deleteCount = psmtDelete.executeUpdate();
        }
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategorisatonMonthlyWiseDao[deleteCategorisationGrade]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error("deleteCategorisationGrade", "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategorisatonMonthlyWiseDao[deleteCategorisationGrade]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: deleteCount=" );
            	loggerValue.append(deleteCount);
                log.debug(methodName, loggerValue );
                
                
            }
        } // end of finally

        return deleteCount;
    }

    /*
     * @param mapping
     * 
     * @param form
     * 
     * @param request
     * 
     * @param response
     * 
     * @return ActionForward
     * This methods is used to load the user Grade list
     */
    public ArrayList loadCategorisationGradeList(Connection pCon, CategorisationVO pCategorisationVO) throws BTSLBaseException {
    	final String methodName = "loadCategorisationGradeList";
        
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	
        	loggerValue.append("Entered pCategorisationVO:");
        	loggerValue.append(pCategorisationVO);
        	log.debug(methodName,loggerValue);
           
        }
         
        

        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT profile_id,profile_name,domain_code,geolevel1, ");
        strBuff.append(" geolevel2,txn_type,from_count,to_count,from_amount, ");
        strBuff.append(" to_amount,from_active_days,to_active_days,class_type, ");
        strBuff.append(" created_by,created_on,modified_by,modified_on FROM user_profile_classification ");
        strBuff.append(" WHERE domain_code = ? AND category_code = ? AND geolevel1 = ? ");
        strBuff.append(" AND geolevel2 = ? AND txn_type = ? ORDER BY class_type  ");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("loadCategorisationGradeList()", "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try (PreparedStatement pstmtSelect = pCon.prepareStatement(sqlSelect);){
           
            pstmtSelect.setString(1, pCategorisationVO.getChannelDomain());
            pstmtSelect.setString(2, pCategorisationVO.getCategoryCode());
            pstmtSelect.setString(3, pCategorisationVO.getRegion());
            pstmtSelect.setString(4, pCategorisationVO.getAreaCode());
            pstmtSelect.setString(5, pCategorisationVO.getTransferType());
           try( ResultSet rs = pstmtSelect.executeQuery();)
           {
            while (rs.next()) {
                pCategorisationVO = new CategorisationVO();
                pCategorisationVO.setProfileId(rs.getString("profile_id"));
                pCategorisationVO.setProfileName(rs.getString("profile_name"));
                pCategorisationVO.setTransferType(rs.getString("txn_type"));
                pCategorisationVO.setCountBegin(rs.getString("from_count"));
                pCategorisationVO.setCountEnd(rs.getString("to_count"));
                pCategorisationVO.setAmountBegin(rs.getString("from_amount"));
                pCategorisationVO.setAmountEnd(rs.getString("to_amount"));
                pCategorisationVO.setActivedaytBegin(rs.getString("from_active_days"));
                pCategorisationVO.setActivedaytEnd(rs.getString("to_active_days"));
                pCategorisationVO.setCreatedBy(rs.getString("created_by"));
                pCategorisationVO.setCreatedOn(rs.getTimestamp("created_on"));
                pCategorisationVO.setModifiedBy(rs.getString("modified_by"));
                pCategorisationVO.setModifiedOn(rs.getTimestamp("modified_on"));
                pCategorisationVO.setClassType(rs.getString("class_type"));

                list.add(pCategorisationVO);
            }
           }

        } catch (SQLException sqe) {
            log.error("loadCategorisationGradeList()", "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategorisatonMonthlyWiseDao[loadCategorisationGradeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceNetworkMappingList()", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error("loadCategorisationGradeList()", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategorisatonMonthlyWiseDao[loadCategorisationGradeList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceNetworkMappingList()", "error.general.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug("loadCategorisationGradeList()", "Exiting: Grade Size =" + list.size());
            }
        }
        return list;
    }

    /*
     * @param mapping
     * 
     * @param form
     * 
     * @param request
     * 
     * @param response
     * 
     * @return ActionForward
     * This methods is used to check if the categorisation profile exist
     */
    /**
     * @param pCon
     * @param pCategorisationVO
     * @return existFlag
     * @throws BTSLBaseException
     */
    public boolean isCategorisationGradeExist(Connection pCon, CategorisationVO pCategorisationVO) throws BTSLBaseException {
    	final String methodName = "isCategorisationGradeExist";

        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pCategorisationVO=");
        	loggerValue.append(pCategorisationVO);
		    log.debug(methodName,loggerValue);
            
        }

        
       
       
        boolean existFlag = false;
        StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT 1 FROM user_profile_classification ");
        strBuff.append(" WHERE domain_code = ? AND category_code = ? AND geolevel1 = ? ");
        strBuff.append(" AND geolevel2 = ? AND txn_type = ? ");
        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
		    log.debug(methodName,loggerValue);
        }
        try (PreparedStatement pstmtSelect = pCon.prepareStatement(sqlSelect);){
           
            pstmtSelect.setString(1, pCategorisationVO.getChannelDomain());
            pstmtSelect.setString(2, pCategorisationVO.getCategoryCode());
            pstmtSelect.setString(3, pCategorisationVO.getRegion());
            pstmtSelect.setString(4, pCategorisationVO.getAreaCode());
            pstmtSelect.setString(5, pCategorisationVO.getTransferType());
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                existFlag = true;
            }
        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategorisatonMonthlyWiseDao[isCategorisationGradeExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(" isCategorisationGradeExist", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategorisatonMonthlyWiseDao[isCategorisationGradeExist]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
            	 loggerValue.setLength(0);
             	loggerValue.append("Exiting: existFlag=" );
             	loggerValue.append(existFlag);
     		    log.debug(methodName,loggerValue);
               
            }
        }
        return existFlag;
    }

    /**
     * @param pCon
     * @param pCategorisationVO
     * @return updateCount
     * @throws BTSLBaseException
     */
    public int modifyCategorisationDetails(Connection pCon, CategorisationVO pCategorisationVO) throws BTSLBaseException {
        
        int updateCount = 0;
        final String methodName = "modifyCategorisationDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
            loggerValue.setLength(0);
        	loggerValue.append("Entered: pCategorisationVO= " );
        	loggerValue.append(pCategorisationVO);
        	log.debug(methodName,loggerValue);
        }
        
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append(" Update user_profile_classification SET profile_name = ?,");
            strBuff.append(" from_count = ?,to_count = ?,from_amount = ?, ");
            strBuff.append(" to_amount = ?, from_active_days= ? , to_active_days = ? ,");
            strBuff.append(" modified_by = ? , modified_on = ?  ");
            strBuff.append(" WHERE domain_code = ? AND category_code = ? AND geolevel1 = ? ");
            strBuff.append(" AND geolevel2 = ? AND txn_type = ? AND class_type = ? ");

            String updateQuery = strBuff.toString();

            if (log.isDebugEnabled()) {
                loggerValue.setLength(0);
            	loggerValue.append("Query sqlUpdate:" );
            	loggerValue.append(updateQuery);
            	log.debug(methodName,loggerValue);
            }

            try(PreparedStatement psmtUpdate = pCon.prepareStatement(updateQuery);)
            {

            psmtUpdate.setString(1, pCategorisationVO.getProfileName());
            psmtUpdate.setString(2, pCategorisationVO.getCountBegin());
            psmtUpdate.setString(3, pCategorisationVO.getCountEnd());
            psmtUpdate.setString(4, pCategorisationVO.getAmountBegin());
            psmtUpdate.setString(5, pCategorisationVO.getAmountEnd());
            psmtUpdate.setString(6, pCategorisationVO.getActivedaytBegin());
            psmtUpdate.setString(7, pCategorisationVO.getActivedaytEnd());
            psmtUpdate.setString(8, pCategorisationVO.getModifiedBy());
            psmtUpdate.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(pCategorisationVO.getModifiedOn()));
            psmtUpdate.setString(10, pCategorisationVO.getChannelDomain());
            psmtUpdate.setString(11, pCategorisationVO.getCategoryCode());
            psmtUpdate.setString(12, pCategorisationVO.getRegion());
            psmtUpdate.setString(13, pCategorisationVO.getAreaCode());
            psmtUpdate.setString(14, pCategorisationVO.getTransferType());
            psmtUpdate.setString(15, pCategorisationVO.getClassType());

            updateCount = psmtUpdate.executeUpdate();

        } 
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategorisatonMonthlyWiseDao[modifyCategorisationDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error("modifyCategorisationDetails", "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategorisatonMonthlyWiseDao[modifyCategorisationDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
        	

            if (log.isDebugEnabled()) {
                loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount=");
            	loggerValue.append(updateCount);
            	log.debug(methodName,loggerValue);
            }
        } // end of finally

        return updateCount;
    }
}