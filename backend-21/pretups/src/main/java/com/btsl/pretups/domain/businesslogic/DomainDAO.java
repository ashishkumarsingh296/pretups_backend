/*
 * #DomainDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jul 29, 2005 Amit Ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.domain.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
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
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
/**
 * Class DomainDAO
 */
public class DomainDAO {

    private Log log = LogFactory.getFactory().getInstance(DomainDAO.class.getName());

    /**
     * Constructor for DomainDAO.
     */
    public DomainDAO() {
        super();
    }

    /**
     * Method loadDomainDetails.
     * This method is used to load domain details from Domains,Category &
     * domain_types
     * tables
     * 
     * @param p_con
     *            Connection
     * @return domainList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadDomainDetails(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadDomainDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

         
        ArrayList domainList = new ArrayList<DomainVO>();
        StringBuilder strBuff = new StringBuilder("SELECT D.domain_code,D.domain_name,DT.num_domain_allowed,");
        strBuff.append("D.domain_type_code,D.owner_category,C.category_name,D.status,");
        strBuff.append("D.modified_on,D.num_of_categories,DT.domain_type_name FROM");
        strBuff.append(" domains D,categories C,domain_types DT WHERE C.category_code=D.owner_category");
        strBuff.append(" AND D.domain_type_code=DT.domain_type_code AND D.status=?");
        strBuff.append(" AND DT.display_allowed=? ORDER BY D.domain_type_code,D.domain_name ");
        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query= " + sqlSelect);
        }
        CategoryWebDAO categoryWebDAO = new CategoryWebDAO();

        try (PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);){
            
            pstmtSelect.setString(1, PretupsI.DOMAIN_STATUS_ACTIVE);
            pstmtSelect.setString(2, PretupsI.DOMAIN_TYPE_DISPLAY_ALLOWED);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            DomainVO domainVO = null;
            int radioIndex = 0;
            int numberOfCategoryForDomain =0;
            while (rs.next()) {
                domainVO = new DomainVO();
                domainVO.setDomainCode(SqlParameterEncoder.encodeParams(rs.getString("domain_code")));
                domainVO.setDomainCodeforDomain(domainVO.getDomainCode());
                domainVO.setDomainName(SqlParameterEncoder.encodeParams(rs.getString("domain_name")));
                domainVO.setDomainTypeCode(SqlParameterEncoder.encodeParams(rs.getString("domain_type_code")));
                domainVO.setDomainTypeName(SqlParameterEncoder.encodeParams(rs.getString("domain_type_name")));
                domainVO.setOwnerCategory(SqlParameterEncoder.encodeParams(rs.getString("owner_category")));
                domainVO.setOwnerCategoryName(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
                domainVO.setDomainStatus(SqlParameterEncoder.encodeParams(rs.getString("status")));
                domainVO.setNumberOfCategories(SqlParameterEncoder.encodeParams(rs.getString("num_of_categories")));
                numberOfCategoryForDomain = categoryWebDAO.loadAvalibleCategoryForDomain(p_con, domainVO.getDomainCode());
                domainVO.setAddCategoryAllowedforThisDomain(true); 
                if(numberOfCategoryForDomain == Integer.parseInt(domainVO.getNumberOfCategories())) {
                	domainVO.setAddCategoryAllowedforThisDomain(false);	
                }
                
                domainVO.setAgentAllowedCheckBoxDisable(false);
                if(Integer.parseInt(domainVO.getNumberOfCategories()) - numberOfCategoryForDomain <=1 ) {
                	domainVO.setAgentAllowedCheckBoxDisable(true);	
                }
                
                
                domainVO.setRadioIndex(radioIndex);
                domainVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                radioIndex++;
                domainList.add(domainVO);
            }
        }
        }

        catch (SQLException sqe) {
            log.error(methodName, "SQL Exception" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadDomainDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, " Exception" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadDomainDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
         	
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting size=" + domainList.size());
            }
        }

        return domainList;
    }

    /**
     * Method loadCategoryDomainList.
     * This method loads all the category domains lists of the Domains table
     * except of OPT type owner_category
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryDomainList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadCategoryDomainList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered:");
        }
        ArrayList categoryDomainList = new ArrayList();
        
        try {
            StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT D.domain_code, D.domain_name,D.domain_type_code,restricted_msisdn,DT.display_allowed FROM domains D,domain_types DT ");
            selectQuery.append("WHERE D.status=? AND D.owner_category != ? AND DT.domain_type_code=D.domain_type_code ");
            selectQuery.append("ORDER BY domain_name ");
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query=" + selectQuery);
            }
           try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery.toString());)
           {
            pstmtSelect.setString(1, PretupsI.DOMAIN_STATUS_ACTIVE);
            pstmtSelect.setString(2, PretupsI.CATEGORY_TYPE_OPT);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            	
            
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("domain_name"), rs.getString("domain_code"));
                listValueVO.setType(rs.getString("restricted_msisdn"));
                listValueVO.setOtherInfo(rs.getString("domain_type_code"));
                listValueVO.setStatusType(rs.getString("display_allowed"));
                categoryDomainList.add(listValueVO);
            }
        } 
        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadCategoryDomainList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadCategoryDomainList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
                 if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:list size=" + categoryDomainList.size());
            }
        }
        return categoryDomainList;
    }

    /**
     * Method for loading Domain List by domainCode.
     * This method returns the arraylist which consist of ListValue VO's
     * 
     * Used in(UserAction, ChannelUserAction)
     * 
     * @author mohit.goel
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadDomainList(Connection p_con, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadDomainList";
        if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  p_domainCode=");
        	loggerValue.append(p_domainCode);
            log.debug(methodName,loggerValue);
        }
        
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT D.domain_code,D.domain_name,DT.restricted_msisdn,DT.display_allowed ");
        strBuff.append("FROM domains D,domain_types DT WHERE D.status <> 'N' AND D.domain_type_code =DT.domain_type_code ");
        strBuff.append("AND DT.domain_type_code <> ? ");
        strBuff.append("ORDER BY domain_name");
        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           
            pstmt.setString(1, p_domainCode);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                ListValueVO vo = new ListValueVO(rs.getString("domain_name"), rs.getString("domain_code"));
                vo.setType(rs.getString("restricted_msisdn"));
                vo.setStatusType(rs.getString("display_allowed"));
                list.add(vo);
            }

        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadDomainList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadDomainList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: domainList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for inserting User Domain Info.
     * Used in(UserAction, ChannelUserAction)
     * 
     * @author mohit.goel
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @param p_domainCodes
     *            String[]
     * @return insertCount int
     * @exception BTSLBaseException
     */
    public int addUserDomainList(Connection p_con, String p_userId, String[] p_domainCodes) throws BTSLBaseException {
        
        int insertCount = 0;
        final String methodName = "addUserDomainList";
        if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userId= ");
        	loggerValue.append(p_userId);
        	loggerValue.append(" p_domainCodes Size= ");
        	loggerValue.append(p_domainCodes.length);
            log.debug(methodName, loggerValue);
        }
        try {
            int count = 0;
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO user_domains (user_id,");
            strBuff.append("domain_code) values (?,?)");
            String insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }
           try(PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);)
           {
            for (int i = 0, j = p_domainCodes.length; i < j; i++) {
                psmtInsert.setString(1, p_userId);
                psmtInsert.setString(2, p_domainCodes[i]);
                insertCount = psmtInsert.executeUpdate();
                psmtInsert.clearParameters();
                // check the status of the update
                if (insertCount > 0) {
                    count++;
                }
            }
            if (count == p_domainCodes.length) {
                insertCount = 1;
            } else {
                insertCount = 0;
            }

        }
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[addUserDomainList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[addUserDomainList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally
        return insertCount;
    }

    /**
     * Method for loading Domain List By UserId.
     * 
     * Used in (UserAction, ChannelUserAction)
     * 
     * @author mohit.goel
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            String
     * @param p_userId
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadDomainListByUserId(Connection p_con, String p_userId) throws BTSLBaseException {
        final String methodName = "loadDomainListByUserID";
        if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  p_userId=");
        	loggerValue.append(BTSLUtil.maskParam(p_userId));
            log.debug(methodName, loggerValue);
        }
  
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT d.domain_code,d.domain_name,dt.restricted_msisdn,display_allowed ");
        strBuff.append(" FROM domains d,user_domains ud,domain_types dt ");
        strBuff.append(" WHERE d.status <> 'N' AND ud.user_id = ? ");
        strBuff.append(" AND dt.domain_type_code=d.domain_type_code");
        strBuff.append(" AND d.domain_code = ud.domain_code");
        strBuff.append(" ORDER BY d.domain_name");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        ListValueVO listVO;
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_userId);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                listVO = new ListValueVO(rs.getString("domain_name"), rs.getString("domain_code"));
                listVO.setType(rs.getString("restricted_msisdn"));
                listVO.setStatusType(rs.getString("display_allowed"));
                list.add(listVO);

            }
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadDomainListByUserID]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadDomainListByUserID]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: domainList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * To load the Domain VO on the basis of the domaincode
     * 
     * @author sandeep.goel
     * @param p_con
     * @param p_domainCode
     * @return
     * @throws BTSLBaseException
     *             DomainVO
     */

    public DomainVO loadDomainVO(Connection p_con, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadDomainVO";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT domain_code,domain_name,domain_type_code,owner_category,status,num_of_categories ");
        strBuff.append(" FROM domains ");
        strBuff.append(" WHERE status <> 'N' AND domain_code = ?");
        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query= " + sqlSelect);
        }
        DomainVO domainVO = null;
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
           
            pstmtSelect.setString(1, p_domainCode);
          try (ResultSet rs = pstmtSelect.executeQuery();)
          {
            if (rs.next()) {
                domainVO = new DomainVO();
                domainVO.setDomainCodeforDomain(rs.getString("domain_code"));
                domainVO.setDomainName(rs.getString("domain_name"));
                domainVO.setDomainTypeCode(rs.getString("domain_type_code"));
                domainVO.setOwnerCategory(rs.getString("owner_category"));
                domainVO.setNumberOfCategories(rs.getString("num_of_categories"));
            }
        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQL Exception" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadDomainVO]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, " Exception" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadDomainVO]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
                  if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting domainVO=" + domainVO);
            }
        }
        return domainVO;
    }
	
	//ASHU changes for batch c2s transfer rules
  	public ArrayList loadDomainCategoryMapping(Connection p_con,String p_domainCode) throws BTSLBaseException
  	{
  		final String methodName = "loadDomainCategoryMapping";
  		if (log.isDebugEnabled())
  		{
  			log.debug(methodName, "Entered  ");
  		}
  		
  		DomainQry domainQry = (DomainQry) ObjectProducer.getObject(QueryConstants.DOMAIN_QRY, QueryConstants.QUERY_PRODUCER);
  		String sqlSelect = domainQry.loadDomainCategoryMappingQry();
  		if (log.isDebugEnabled())
  		{
  		    log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
  		}    
  		ArrayList list = new ArrayList();
  		ListValueVO listVO;
  		try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);)
  		{
  			
  			pstmt.setString(1, p_domainCode);
  			try(ResultSet rs = pstmt.executeQuery();)
  			{
  			while (rs.next())
  			{
  				listVO = new ListValueVO(rs.getString("domain_code"),rs.getString("category_code")+":"+rs.getString("category_name")+":"+rs.getString("grade_code")+":"+rs.getString("grade_name"));
  				list.add(listVO);			    
  			}

  		} 
  		}catch (SQLException sqe)
  		{
  			log.error(methodName, "SQLException : " + sqe);
  			log.errorTrace(methodName,sqe);
  			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DomainDAO[loadDomainCategoryMapping]","","","","SQL Exception:"+sqe.getMessage());
  			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
  		} catch (Exception ex)
  		{
  		    log.error(methodName, "Exception : " + ex);
  		    log.errorTrace(methodName,ex);
  			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DomainDAO[loadDomainCategoryMapping]","","","","Exception:"+ex.getMessage());
  			throw new BTSLBaseException(this, methodName, "error.general.processing");
  		} finally
  		{
  			
  			
  			if (log.isDebugEnabled())
  			{
  				log.debug(methodName, "Exiting: domainCateogyMappingList size=" + list.size());
  			}
  		}
  		return list;
  	}
  	
  	
  	/**
     * Method for deleting User Domains.
     * 
     * @param con
     *            java.sql.Connection
     * @param userId
     *            String
     * @return deleteCount int
     * @exception BTSLBaseException
     */

    public int deleteUserDomains(Connection con, String userId) throws BTSLBaseException {
        final String methodName = "deleteUserDomains";
        
        int deleteCount = 0;
        LogFactory.printLog(methodName, "Entered: p_userId= " + userId, log);
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("DELETE FROM user_domains WHERE user_id = ?");
            String deleteQuery = strBuff.toString();
            LogFactory.printLog(methodName, "Query sqlDelete:" + deleteQuery, log);
            try(PreparedStatement psmtDelete = con.prepareStatement(deleteQuery);)
            {
            psmtDelete.setString(1, userId);
            deleteCount = psmtDelete.executeUpdate();
            psmtDelete.clearParameters();
            }
        }catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, 
            		"DomainDAO[deleteUserDomains]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, 
            		"DomainDAO[deleteUserDomains]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }finally {
        
            LogFactory.printLog(methodName, "Exiting: deleteCount=" + deleteCount, log);
        }

        return deleteCount;
    }

  	
    /**
     * Method loadCategoryDomainListFromUserDefault.
     * This method loads all the category domains lists of the Domains table
     * except of OPT type owner_category
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryDomainListFromUserDefault(Connection p_con) throws BTSLBaseException {
    	final String methodName = "loadCategoryDomainListFromUserDefault";
    	if (log.isDebugEnabled()) {
    		log.debug(methodName, "Entered:");
    	}
    	ArrayList categoryDomainList = new ArrayList();

    	try {
    		StringBuilder selectQuery = new StringBuilder();

    		selectQuery.append("SELECT D.domain_code, D.domain_name,D.domain_type_code,restricted_msisdn,DT.display_allowed FROM domains D,categories C,domain_types DT ");
    		selectQuery.append("WHERE C.category_code=D.owner_category AND D.domain_type_code=DT.domain_type_code AND D.status=? ");
    		selectQuery.append("AND DT.display_allowed=? ORDER BY D.domain_type_code,D.domain_name  ");
    		if (log.isDebugEnabled()) {
    			log.debug(methodName, "Query=" + selectQuery);
    		}
    		try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery.toString());)
    		{
    			pstmtSelect.setString(1, PretupsI.DOMAIN_STATUS_ACTIVE);
    			pstmtSelect.setString(2, PretupsI.DOMAIN_TYPE_DISPLAY_ALLOWED);
    			try(ResultSet rs = pstmtSelect.executeQuery();)
    			{

    				ListValueVO listValueVO = null;
    				while (rs.next()) {
    					listValueVO = new ListValueVO(rs.getString("domain_name"), rs.getString("domain_code"));
    					listValueVO.setType(rs.getString("restricted_msisdn"));
    					listValueVO.setOtherInfo(rs.getString("domain_type_code"));
    					listValueVO.setStatusType(rs.getString("display_allowed"));
    					categoryDomainList.add(listValueVO);
    				}
    			} 
    		}
    	}catch (SQLException sqe) {
    		log.error(methodName, "SQLException:" + sqe.getMessage());
    		log.errorTrace(methodName, sqe);
    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadCategoryDomainListFromUserDefault]", "", "", "", "SQL Exception:" + sqe.getMessage());
    		throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
    	} catch (Exception e) {
    		log.error(methodName, "Exception:" + e.getMessage());
    		log.errorTrace(methodName, e);
    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadCategoryDomainListFromUserDefault]", "", "", "", "Exception:" + e.getMessage());
    		throw new BTSLBaseException(this, methodName, "error.general.processing");
    	} finally {
    		if (log.isDebugEnabled()) {
    			log.debug(methodName, "Exiting:list size=" + categoryDomainList.size());
    		}
    	}
    	return categoryDomainList;
    }	
}
