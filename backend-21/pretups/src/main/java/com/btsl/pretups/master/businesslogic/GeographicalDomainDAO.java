/*
 * @# GeographicalDomainDAO.java
 * This the DAO class for the Geographical Domain Module
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jul 26, 2005 Sandeep Goel Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.util.BTSLUtil;
/**
 * class GeographicalDomainDAO 
 */

public class GeographicalDomainDAO {
    /**
     * Field _log. This field is used to display the logs for debugging purpose.
     */
    private final Log _log = LogFactory.getLog(GeographicalDomainDAO.class.getName());
    private GeographicalDomainQry geographicalDomainQry;
    /**
     * Constructor for GeographicalDomainDAO.
     */
    public GeographicalDomainDAO() {
        super();
        geographicalDomainQry = (GeographicalDomainQry)ObjectProducer.getObject(QueryConstants.GEO_DOMAIN_QRY, QueryConstants.QUERY_PRODUCER);
    }

    /**
     * Method isGeographicalDomainExist.
     * 
     * @param p_con
     *            Connection
     * @param p_grphDomainCode
     *            String
     * @param p_checkStatus
     *            boolean
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isGeographicalDomainExist(Connection p_con, String p_grphDomainCode, boolean p_checkStatus) throws BTSLBaseException {
        final String methodName = "isGeographicalDomainExist";
        LogFactory.printLog(methodName,  "Entered:p_grphDomainCode=" + p_grphDomainCode + ",p_checkStatus=" + p_checkStatus, _log);
       
        
        
        boolean isExist = false;
        StringBuilder sqlRecordExist = new StringBuilder();
        try {
            sqlRecordExist.append("SELECT 1 FROM  geographical_domains ");
            sqlRecordExist.append("WHERE grph_domain_code=UPPER(?) ");
            if (p_checkStatus) {
                sqlRecordExist.append("AND status=? ");
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY=" + sqlRecordExist);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlRecordExist.toString());)
            {
            int i = 1;
            pstmtSelect.setString(i++, p_grphDomainCode);
            if (p_checkStatus) {
                pstmtSelect.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
            }

           try(ResultSet rs = pstmtSelect.executeQuery();)
           {
            if (rs.next()) {
                isExist = true;
            }
        }
        }
        }// end of try
        catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[isGeographicalDomainExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[isGeographicalDomainExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	
        	LogFactory.printLog(methodName,"Exititng:isExist=" + isExist, _log);
          
        }// end of finally
        return isExist;
    }// end isGeographicalDomainExist

    /**
     * Method for loading User Assigned Geographies List.(That are assigned to
     * the user)
     * From the table user_geographies
     * 
     * Used for Users(UsersAction)
     * 
     * @author mohit.goel
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @param p_networkCode
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserGeographyList(Connection p_con, String p_userId, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadUserGeographyList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userId=" + BTSLUtil.maskParam(p_userId)+", p_networkCode="+p_networkCode);
        }
        
      
        StringBuilder strBuff = new StringBuilder();
        /*
         * For load the geographies information we can excute the simple query
         * SELECT grph_domain_code FROM user_geographies WHERE user_id = ?
         * But we also need the grph_domain_name of the geography so we execute
         * the different query
         * 
         * select ug.GRPH_DOMAIN_CODE,gd.GRPH_DOMAIN_NAME,gdt.GRPH_DOMAIN_NAME
         * FROM USER_GEOGRAPHIES ug,GEOGRAPHICAL_DOMAINS
         * gd,GEOGRAPHICAL_DOMAIN_TYPES gdt
         * WHERE ug.USER_ID = 'DL/B000048' (login Id of the logged In user)
         * AND gd.NETWORK_CODE = 'DL' (network_code of the logged In user)
         * AND gd.GRPH_DOMAIN_CODE = ug.GRPH_DOMAIN_CODE
         * AND gdt.GRPH_DOMAIN_TYPE = gd.GRPH_DOMAIN_TYPE
         */

        strBuff.append("SELECT ug.grph_domain_code,gd.grph_domain_name,gdt.grph_domain_type_name,gdt.sequence_no, ");
        strBuff.append(" gdt.grph_domain_type FROM user_geographies ug,geographical_domains gd,geographical_domain_types gdt ");
        strBuff.append("WHERE ug.user_id = ? ");
        strBuff.append("AND gd.network_code = ? ");
        strBuff.append("AND gd.grph_domain_code = ug.grph_domain_code ");
        strBuff.append("AND gdt.grph_domain_type = gd.grph_domain_type ");
		strBuff.append("AND gd.status='Y' ");
        strBuff.append(" ORDER BY gd.grph_domain_name");
		

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_userId);
            pstmt.setString(2, p_networkCode);
           try( ResultSet  rs = pstmt.executeQuery();)
           {
            UserGeographiesVO geographyVO = null;
            while (rs.next()) {
                geographyVO = new UserGeographiesVO();
                geographyVO.setGraphDomainCode(rs.getString("grph_domain_code"));
                geographyVO.setGraphDomainName(rs.getString("grph_domain_name"));
                geographyVO.setGraphDomainTypeName(rs.getString("grph_domain_type_name"));
                geographyVO.setGraphDomainSequenceNumber(rs.getInt("sequence_no"));
                geographyVO.setGraphDomainType(rs.getString("grph_domain_type"));
                list.add(geographyVO);
                
            }
        } 
        }catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[loadUserGeographyList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[loadUserGeographyList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
           
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userGeographyList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method isGeoDomainExistInHierarchy.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_grphDomainCode
     *            String
     * @param p_userID
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isGeoDomainExistInHierarchy(Connection p_con, String p_grphDomainCode, String p_userID) throws BTSLBaseException {
        final String methodName = "isGeoDomainExistInHierarchy";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_grphDomainCode=" + p_grphDomainCode + ",p_userID=" + p_userID);
        }
       
         
        boolean isExist = false;
        try {
        	String sqlRecordExist = geographicalDomainQry.isGeoDomainExistInHierarchyQry();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY=" + sqlRecordExist);
            }
            try( PreparedStatement pstmtSelect = p_con.prepareStatement(sqlRecordExist);)
            {
            int i = 1;
            pstmtSelect.setString(i++, p_userID);
            pstmtSelect.setString(i++, p_grphDomainCode);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                isExist = true;
            }
        }
            }
        }// end of try
        catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[isGeoDomainExistInHierarchy]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[isGeoDomainExistInHierarchy]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
           
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exititng:isExist=" + isExist);
            }
        }// end of finally
        return isExist;
    }// end isGeoDomainExistInHierarchy

    /**
     * Method isUserExistsInGeoDomainExistHierarchy.
     * 
     * @param p_con
     *            Connection
     * @param p_userID
     *            String
     * @param p_sessionUserID
     *            String
     * @return boolean
     * 
     * @throws BTSLBaseException
     */
    public boolean isUserExistsInGeoDomainExistHierarchy(Connection p_con, String p_userID, String p_sessionUserID) throws BTSLBaseException {
        final String methodName = "isUserExistsInGeoDomainExistHierarchy";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_userID=" + p_userID + ",p_sessionUserID=" + p_sessionUserID);
        }
        
         
        boolean isExist = false;
        try {
        	String sqlRecordExist=geographicalDomainQry.isUserExistsInGeoDomainExistHierarchyQry();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY=" + sqlRecordExist);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlRecordExist);)
            {
            int i = 1;
            pstmtSelect.setString(i++, p_sessionUserID);
            pstmtSelect.setString(i++, p_userID);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                isExist = true;
            }
        }
            }
        }// end of try
        catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[isUserExistsInGeoDomainExistHierarchy]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[isUserExistsInGeoDomainExistHierarchy]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
           
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exititng:isExist=" + isExist);
            }
        }// end of finally
        return isExist;
    }// end isUserExistsInGeoDomainExistHierarchy

    /**
     * This method will load Geographical Domain Code Heirarchy.
     * 
     * @param p_con
     * @param p_geodomaintype
     * @param p_geodomainCode
     * @param p_isTopToBottom
     * @throws BTSLBaseException
     */
    public ArrayList loadGeoDomainCodeHeirarchy(Connection p_con, String p_geodomaintype, String p_geodomainCode, boolean p_isTopToBottom) throws BTSLBaseException {

        final String methodName = "loadGeoDomainCodeHeirarchy";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_geodomaintype=" + p_geodomaintype);
        }
        ArrayList domainParentList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        GeographicalDomainVO geographicalDomainVO = null;
         
        try {
        	
            pstmtSelect = geographicalDomainQry.loadGeoDomainCodeHeirarchyQry(p_con, p_isTopToBottom, p_geodomaintype, p_geodomainCode);
          
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                geographicalDomainVO = new GeographicalDomainVO();
                geographicalDomainVO.setGrphDomainCode(rs.getString("GRPH_DOMAIN_CODE"));
                geographicalDomainVO.setNetworkCode(rs.getString("NETWORK_CODE"));
                geographicalDomainVO.setGrphDomainName(rs.getString("GRPH_DOMAIN_NAME"));
                geographicalDomainVO.setParentDomainCode(rs.getString("PARENT_GRPH_DOMAIN_CODE"));
                geographicalDomainVO.setGrphDomainShortName(rs.getString("GRPH_DOMAIN_SHORT_NAME"));
                geographicalDomainVO.setDescription(rs.getString("DESCRIPTION"));
                geographicalDomainVO.setStatus(rs.getString("STATUS"));
                geographicalDomainVO.setGrphDomainType(rs.getString("GRPH_DOMAIN_TYPE"));
                geographicalDomainVO.setCreatedOn(rs.getDate("CREATED_ON"));
                geographicalDomainVO.setCreatedBy(rs.getString("CREATED_BY"));
                geographicalDomainVO.setModifiedOn(rs.getDate("MODIFIED_ON"));
                geographicalDomainVO.setModifiedBy(rs.getString("MODIFIED_BY"));
                domainParentList.add(geographicalDomainVO);
            }
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[loadGeoDomainCodeHeirarchy]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[loadGeoDomainCodeHeirarchy]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:list size=" + domainParentList.size());
            }
        }
        return domainParentList;
    }
    
    
    /**
     * Method for loading  Assigned Geographies List for super channel admin.
     * From the table user_geographies
     * 
     * Used in(UsersAction)
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList<UserGeographiesVO> loadUserGeographyListForSuperChannelAdmin(Connection p_con, String p_userId) throws BTSLBaseException {
        final String methodName = "loadUserGeographyListForSuperChannelAdmin";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userId=" + p_userId);
        }
         
        
        StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT ug.grph_domain_code,gd.grph_domain_name,gdt.grph_domain_type_name,gdt.sequence_no, ");
        strBuff.append("gdt.grph_domain_type FROM user_geographies ug,geographical_domains gd,geographical_domain_types gdt ");
        strBuff.append("WHERE ug.user_id = ? ");
        strBuff.append("AND gd.grph_domain_code = ug.grph_domain_code ");
        strBuff.append("AND gdt.grph_domain_type = gd.grph_domain_type ");
        strBuff.append("ORDER BY gd.grph_domain_name");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList<UserGeographiesVO> list = new ArrayList<UserGeographiesVO>();
        try( PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           
            pstmt.setString(1, p_userId);
           try(ResultSet rs = pstmt.executeQuery();)
           {
            UserGeographiesVO geographyVO = null;
            while (rs.next()) {
                geographyVO = new UserGeographiesVO();
                geographyVO.setGraphDomainCode(rs.getString("grph_domain_code"));
                geographyVO.setGraphDomainName(rs.getString("grph_domain_name"));
                geographyVO.setGraphDomainTypeName(rs.getString("grph_domain_type_name"));
                geographyVO.setGraphDomainSequenceNumber(rs.getInt("sequence_no"));
                geographyVO.setGraphDomainType(rs.getString("grph_domain_type"));
                list.add(geographyVO);

            }
           }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[loadUserGeographyListForSuperChannelAdmin]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[loadUserGeographyListForSuperChannelAdmin]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userGeographyList size=" + list.size());
            }
        }
        return list;
    }
    
    /**
     * Method for loading  Assigned network List for super channel admin.
     * From the table user_geographies
     * 
     * Used in(UsersAction)
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList<UserGeographiesVO> loadUserNetworkList(Connection p_con, String p_userId) throws BTSLBaseException {
        final String methodName = "loadUserNetwrokList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userId=" + p_userId);
        }
         
        
        StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT ug.grph_domain_code, gd.grph_domain_name, gdt.grph_domain_type_name,gd.is_default,gd.parent_grph_domain_code,gd.GRPH_DOMAIN_TYPE ");
        strBuff.append(" FROM user_geographies ug,geographical_domains gd,geographical_domain_types gdt ");
        strBuff.append("WHERE ug.user_id = ? ");
        strBuff.append("AND gd.grph_domain_code = ug.grph_domain_code ");
        strBuff.append("AND gdt.grph_domain_type = gd.grph_domain_type ");
        

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList<UserGeographiesVO> list = new ArrayList<UserGeographiesVO>();
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_userId);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            UserGeographiesVO geographyVO = null;
            while (rs.next()) {
                geographyVO = new UserGeographiesVO();
                geographyVO.setGraphDomainCode(rs.getString("grph_domain_code"));
                geographyVO.setGraphDomainName(rs.getString("grph_domain_name"));
                geographyVO.setGraphDomainTypeName(rs.getString("grph_domain_type_name"));
                geographyVO.setIsDefault(rs.getString("is_default"));
                geographyVO.setParentGraphDomainCode(rs.getString("parent_grph_domain_code"));
                geographyVO.setGraphDomainType(rs.getString("GRPH_DOMAIN_TYPE"));

                list.add(geographyVO);

            }
        } 
        }
            catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[loadUserGeographyListForSuperChannelAdmin]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[loadUserGeographyListForSuperChannelAdmin]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
    
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userGeographyList size=" + list.size());
            }
        }
        return list;
    }
    /**
     * @param p_requestVO ***************************/
    public List<UserGeographiesVO> loadParentGeographyInfo(Connection p_con, String grph_domain_code, RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "loadParentGeographyInfo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered grph_domain_code=" + grph_domain_code);
        }
        
         
       
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT gd.grph_domain_code,gd.network_code,gd.parent_grph_domain_code,gd.status FROM GEOGRAPHICAL_DOMAINS gd WHERE gd.network_code = ? and gd.grph_domain_code = ? and gd.GRPH_DOMAIN_TYPE=  (SELECT GRPH_DOMAIN_TYPE FROM CATEGORIES WHERE CATEGORY_CODE= ?  AND STATUS='Y') ");
         
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        List<UserGeographiesVO> list = new ArrayList();
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_requestVO.getNetworkCode());
            pstmt.setString(2, grph_domain_code );
            pstmt.setString(3, p_requestVO.getUserCategory());
            try(ResultSet rs = pstmt.executeQuery();)
            {
            UserGeographiesVO geographyVO = null;
            while (rs.next()) {
                geographyVO = new UserGeographiesVO();
                geographyVO.setParentGraphDomainCode(rs.getString("parent_grph_domain_code"));
                geographyVO.setGraphDomainCode(rs.getString("grph_domain_code"));
                list.add(geographyVO);
           
            }
        } 
        }catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[loadParentGeographyInfo]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[loadParentGeographyInfo]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userGeographyList size=" + list.size());
            }
        }
        return list;
    }
    // added for channel user transfer
    public String loadDefaultGeographyUnderParent(Connection p_con,String p_GeoDomain_Code,String p_networkCode) throws BTSLBaseException
	{
		final String methodName = "loadDefaultGeographyUnderParent";
		if (_log.isDebugEnabled())
		{
			_log.debug(methodName, "Entered parent_GeoDomain_Code="+p_GeoDomain_Code);
		}
		 
		 
		StringBuilder strBuff = new StringBuilder();
		
		
		strBuff.append("SELECT gd.grph_domain_code FROM geographical_domains gd ");
		strBuff.append("WHERE gd.parent_grph_domain_code = ? AND gd.network_code = ? ");
		strBuff.append("AND gd.status = 'Y' ");
		strBuff.append("AND gd.IS_DEFAULT = 'Y' ");
		strBuff.append(" ORDER BY gd.grph_domain_name");
		
		String sqlSelect = strBuff.toString();
		if (_log.isDebugEnabled())
		{
		    _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		
		
		String defaultGeography ="";
		try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);)
		{
			
			pstmt.setString(1, p_GeoDomain_Code);
			pstmt.setString(2, p_networkCode);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			while (rs.next())
			{
				defaultGeography  =rs.getString("grph_domain_code");
			 }
		} 
		}catch (SQLException sqe)
		{
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"GeographicalDomainDAO[loadGeographyUnderParent]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex)
		{
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"GeographicalDomainDAO[loadGeographyUnderParent]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally
		{
			
		   
			if (_log.isDebugEnabled())
			{
				_log.debug(methodName, "Exiting: loadDefaultGeographyUnderParent value=" + defaultGeography);
			}
		}
		return defaultGeography;
	}
    // added for channel user transfer
    public ArrayList loadGeographyHierarchyUnderParent(Connection p_con,String p_GeoDomain_Code,String p_networkCode,String p_GeoDomain_Type) throws BTSLBaseException
	{
		final String methodName = "loadGeographyHierarchyUnderParent";
		if (_log.isDebugEnabled())
		{
			_log.debug(methodName, "Entered parent_GeoDomain_Code="+p_GeoDomain_Code);
		}
		PreparedStatement pstmt = null;
		
		
	
		ArrayList<String> list = new ArrayList<String>();
		try
		{
			
			pstmt=geographicalDomainQry.loadGeographyHierarchyUnderParentQry(p_con, p_GeoDomain_Code, p_networkCode, p_GeoDomain_Type);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			while (rs.next())
			{
			    list.add(rs.getString("grph_domain_code"));
			    }
		} 
		}catch (SQLException sqe)
		{
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"GeographicalDomainDAO[loadGeographyHierarchyUnderParent]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex)
		{
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"GeographicalDomainDAO[loadGeographyHierarchyUnderParent]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally
		{
			
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
			if (_log.isDebugEnabled())
			{
				_log.debug(methodName, "Exiting: loadGeographyHierarchyUnderParent size=" + list.size());
			}
		}
		return list;
	}
    
    /**
	 * Method for loading  Geographical Domain Types.
	 * 
     * @param con java.sql.Connection
     * @param sessionUserDomainSeqNo int
     * @param newUserDomainSeqNo int
     * @return java.util.List
     * @throws  BTSLBaseException
    
     */
    public List loadDomainTypes(Connection con,int sessionUserDomainSeqNo,int newUserDomainSeqNo) throws BTSLBaseException{
        final String methodName = "loadDomainTypes";
        LogFactory.printLog(methodName, "Entered  p_sessionUserDomainSeqNo="+sessionUserDomainSeqNo+", p_newUserDomainSeqNo="+newUserDomainSeqNo, _log);
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuilder strBuff = new StringBuilder();
        
        strBuff.append("SELECT grph_domain_type,grph_domain_type_name,grph_domain_parent, ");
        strBuff.append("controlling_unit, sequence_no FROM geographical_domain_types ");
        strBuff.append("WHERE sequence_no > ? AND sequence_no < ? ");
        strBuff.append("ORDER BY sequence_no ");
        
        String sqlSelect = strBuff.toString();
        LogFactory.printLog(methodName, "QUERY sqlSelect="+sqlSelect, _log);
        List list = new ArrayList();
        try{
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setInt(1, sessionUserDomainSeqNo);
            pstmt.setInt(2, newUserDomainSeqNo);
            
            rs = pstmt.executeQuery();
            GeographicalDomainTypeVO typeVO = null;
            while (rs.next()){
                typeVO = new GeographicalDomainTypeVO();
                typeVO.setGrphDomainType(rs.getString("grph_domain_type"));
                typeVO.setGrphDomainTypeName(rs.getString("grph_domain_type_name"));
                typeVO.setGrphDomainParent(rs.getString("grph_domain_parent"));
                typeVO.setGrphDomainSequenceNo(rs.getString("sequence_no"));
                
                list.add(typeVO);
            }
        } catch (SQLException sqe){
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,
            		"GeographicalDomainDAO[loadDomainTypes]","","","","SQL Exception:"+sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex){
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,
            		"GeographicalDomainDAO[loadDomainTypes]","","","","Exception:"+ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally{
            try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing result set.", e);
              }
            try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing result set.", e);
              }
            LogFactory.printLog(methodName," Exiting : geographyList size=" + list.size(),_log);
        }
        return list;
    }
    
    /**
	 * Method for loading  Geographical Domain Info having status except given
	 * 
     * @param con java.sql.Connection
     * @param networkCode String
     * @param status String
     * @return java.util.List
     * @throws  BTSLBaseException
     */
    public List loadGeoDomainList(Connection con,String networkCode,String status)throws BTSLBaseException{
        final String methodName = "loadGeoDomainList";
        LogFactory.printLog(methodName,"Entered:networkCode="+networkCode,_log);
        List domainParentList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        GeographicalDomainVO geographicalDomainVO=null; 
        ResultSet rs = null;
        try{
            StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT network_code, grph_domain_code, grph_domain_name, parent_grph_domain_code, grph_domain_short_name, ");
            selectQuery.append("description, status, grph_domain_type, modified_on, is_default "); 
            selectQuery.append("FROM geographical_domains ");
            selectQuery.append("WHERE network_code=? AND status <> ? ");
            selectQuery.append("ORDER BY grph_domain_name ");
            LogFactory.printLog(methodName,"Query selectQuery=" + selectQuery,_log);
            pstmtSelect = con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1,networkCode);
            pstmtSelect.setString( 2,status);
            rs = pstmtSelect.executeQuery();
            while (rs.next()){
                geographicalDomainVO =new GeographicalDomainVO();
                geographicalDomainVO.setNetworkCode(rs.getString("network_code"));
                geographicalDomainVO.setGrphDomainCode(rs.getString("grph_domain_code"));
                geographicalDomainVO.setGrphDomainName(rs.getString("grph_domain_name"));
                geographicalDomainVO.setParentDomainCode(rs.getString("parent_grph_domain_code"));
                geographicalDomainVO.setGrphDomainShortName(rs.getString("grph_domain_short_name"));
                geographicalDomainVO.setDescription(rs.getString("description"));
                geographicalDomainVO.setStatus(rs.getString("status"));
                geographicalDomainVO.setGrphDomainType(rs.getString("grph_domain_type"));
                geographicalDomainVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                geographicalDomainVO.setIsDefault(rs.getString("is_default"));
                domainParentList.add(geographicalDomainVO);
            }
        }catch (SQLException sqe){
            _log.error(methodName, "SQLException:"+ sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,
            		EventLevelI.FATAL,"GeographicalDomainDAO[loadGeoDomainList]","","","","SQL Exception:"+sqe.getMessage());
            throw new BTSLBaseException(this, methodName,"error.general.sql.processing");
        }catch (Exception e){
            _log.error(methodName, "Exception:"+ e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,
            		"GeographicalDomainDAO[loadGeoDomainList]","","","","Exception:"+e.getMessage());
            throw new BTSLBaseException(this, methodName,"error.general.processing");
        }finally{
            try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing result set.", e);
              }
            try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing result set.", e);
              }
            LogFactory.printLog(methodName,"Exiting:list size="+ domainParentList.size(),_log);
        }
        return domainParentList;
    }    
    
    
    /**
	 * Method for loading  Geographical Domain Info by Domain Code
	 * 
     * @param con java.sql.Connection
     * @param networkCode String
     * @param geoDomainType String
     * @param geoDomainCode String
     * @return geographicalDomainVO
     * @throws  BTSLBaseException
     */
    public GeographicalDomainVO loadGeoDomainVO(Connection con, String networkCode, String geoDomainType, String geoDomainCode) throws BTSLBaseException {
        final String methodName = "loadGeoDomainVO";
        LogFactory.printLog(methodName,"Entered:networkCode=" + networkCode+" geoDomainType=" + geoDomainType + "geoDomainCode="+geoDomainCode,_log);
        PreparedStatement pstmtSelect = null;
        GeographicalDomainVO geographicalDomainVO = null;
        ResultSet rs = null;
        try {
            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT network_code, grph_domain_code, grph_domain_name, parent_grph_domain_code, grph_domain_short_name, ");
            selectQuery.append("description, status, grph_domain_type, modified_on, is_default ");
            selectQuery.append("FROM geographical_domains ");
            selectQuery.append("WHERE network_code=? AND status <> ? AND grph_domain_type=? AND grph_domain_code=?");
            selectQuery.append("ORDER BY grph_domain_name ");
            LogFactory.printLog(methodName,"Query=" + selectQuery,_log);
            pstmtSelect = con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, networkCode);
            pstmtSelect.setString(2, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_DELETE);
            pstmtSelect.setString(3, geoDomainType);
            pstmtSelect.setString(4, geoDomainCode);
            rs = pstmtSelect.executeQuery();
            
            while (rs.next()) {
                geographicalDomainVO = GeographicalDomainVO.getInstance();
                geographicalDomainVO.setNetworkCode(rs.getString("network_code"));
                geographicalDomainVO.setGrphDomainName(rs.getString("grph_domain_name"));
                geographicalDomainVO.setParentDomainCode(rs.getString("parent_grph_domain_code"));
                geographicalDomainVO.setGrphDomainShortName(rs.getString("grph_domain_short_name"));
                geographicalDomainVO.setDescription(rs.getString("description"));
                geographicalDomainVO.setStatus(rs.getString("status"));
                geographicalDomainVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                geographicalDomainVO.setIsDefault(rs.getString("is_default"));
                geographicalDomainVO.setGrphDomainType(geoDomainType);
                geographicalDomainVO.setGrphDomainCode(geoDomainCode);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadGeoDomainList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadGeoDomainList]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing result set.", e);
              }
            try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing result set.", e);
              }
            LogFactory.printLog(methodName,"Exiting:list size=" + geographicalDomainVO,_log);
        }
        return geographicalDomainVO;
    }
    
    
    
    
    /**
     * Method isGeographicalDomainExist.
     * 
     * @param p_con
     *            Connection
     * @param p_grphDomainCode
     *            String
     * @param p_checkStatus
     *            boolean
     * @return String
     * @throws BTSLBaseException
     */
    public String  getGeographyName(Connection p_con, String p_grphDomainCode, boolean p_checkStatus) throws BTSLBaseException {
        final String methodName = "getGeographyName";
        LogFactory.printLog(methodName,  "Entered:p_grphDomainCode=" + p_grphDomainCode + ",p_checkStatus=" + p_checkStatus, _log);
       
        
        
        String geographyName =null;
        StringBuilder sqlRecordExist = new StringBuilder();
        try {
            sqlRecordExist.append("SELECT GRPH_DOMAIN_NAME FROM  geographical_domains ");
            sqlRecordExist.append("WHERE grph_domain_code=UPPER(?) ");
            if (p_checkStatus) {
                sqlRecordExist.append("AND status=? ");
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY=" + sqlRecordExist);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlRecordExist.toString());)
            {
            int i = 1;
            pstmtSelect.setString(i++, p_grphDomainCode);
            if (p_checkStatus) {
                pstmtSelect.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
            }

           try(ResultSet rs = pstmtSelect.executeQuery();)
           {
            if (rs.next()) {
            	geographyName=rs.getString("GRPH_DOMAIN_NAME");
            }
        }
        }
        }// end of try
        catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[getGeographyName]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[getGeographyName]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	
        	LogFactory.printLog(methodName,"Exititng:getGeographyName=" + geographyName, _log);
          
        }// end of finally
        return geographyName;
    }// end isGeographicalDomainExist

}
