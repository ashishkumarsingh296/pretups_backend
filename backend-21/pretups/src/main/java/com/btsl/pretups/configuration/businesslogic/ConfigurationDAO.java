/**
* @(#)ConfigurationDAO.java
* Copyright(c) 2005, Bharti Telesoft Ltd.
* All Rights Reserved
* 
* <description>
*-------------------------------------------------------------------------------------------------
* Author                        Date            History
*-------------------------------------------------------------------------------------------------
* Sanjay Kumar Bind1            May 7, 2017     Initital Creation
*-------------------------------------------------------------------------------------------------
*
*/

package com.btsl.pretups.configuration.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;

/**
 * @author Sanjay Kumar Bind1
 *
 */
public class ConfigurationDAO {

    /**
     * Commons Logging instance.
     */
    private static Log log = LogFactory.getLog(ConfigurationDAO.class.getName());
    public static final String ERROR_GENERAL_PROCESSING = "error.general.processing";
    public static final String ERROR_GENERAL_SQL_PROCESSING = "error.general.sql.processing";

    
    /**
     * load the lookups with lookup types.
     * 
     *  lookupTypes is key and lookups is List associated 
     * @return Map
     * @throws BTSLBaseException
     */
    public Map loadConfigurations() throws BTSLBaseException{
        
        final String methodName = "loadConfigurations";
        LogFactory.printLog(methodName, "Entered ", log);
        
        Connection con=null;
        
        Map<String,ConfigurationCacheVO> map = new HashMap<String,ConfigurationCacheVO>();
        
        try
        {
            con = OracleUtil.getSingleConnection();
            
            /*
             * Load the all preferences  
             */
            loadINConfigurations(con,map);
        }
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch (Exception ex)
        {
            log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ConfigurationDAO[loadConfigurations]","","","",PretupsI.EXCEPTION+ex.getMessage());
            throw new BTSLBaseException(this, "loadConfigurations()", ERROR_GENERAL_PROCESSING);
        }
		finally
		{
			try{
				if(con != null)
				{ 
					con.close(); 
				}
			}catch (SQLException sqe){
				log.errorTrace(methodName, sqe);
			}catch (Exception e)
			{
				log.errorTrace(methodName, e);
			}
			LogFactory.printLog(methodName, "Exited: Map size="+map.size(), log);
		}
        return map;
    }
    
    /**
     * Load the System preferences
     * @param pCon
     * @param pMap
     * @throws BTSLBaseException
     * void
     * ConfigurationDAO
     */
    private void loadINConfigurations(Connection pCon, Map pMap) throws BTSLBaseException{
		final String methodName = "loadINConfigurations";
        LogFactory.printLog(methodName, "Entered Map"+pMap, log);
         
		ConfigurationCacheVO cacheVO=null;
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT ");
        strBuff.append("key, value, type, value_type, instance_id, modified_on,");
        strBuff.append(" interface_id FROM configurations ");
        String sqlSelect = strBuff.toString();
		LogFactory.printLog(methodName, "QUERY sqlSelect=" + sqlSelect, log);
        try ( PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);ResultSet rs = pstmt.executeQuery();)
		{
          
           
            while (rs.next()) 
			{
                cacheVO = new ConfigurationCacheVO();
                cacheVO.setIntidKey(rs.getString("key"));
                cacheVO.setInterfaceId(rs.getString("interface_id"));
                cacheVO.setIntidValue(rs.getString("value"));
                cacheVO.setType(rs.getString("type"));
                cacheVO.setValueType(rs.getString("value_type"));
                cacheVO.setInstanceId(rs.getString("instance_id"));
                cacheVO.setModifiedOn(rs.getDate("modified_on"));
				cacheVO.setModifiedTimeStamp(rs.getTimestamp("modified_on"));
                pMap.put(cacheVO.getIntidKey(),cacheVO);
            }            
        }
		catch (SQLException sqe) 
		{
			log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ConfigurationDAO[loadINConfigurations]","","","",PretupsI.SQLEXCEPTION+sqe.getMessage());
			throw new BTSLBaseException(this, "loadINConfigurations()", ERROR_GENERAL_SQL_PROCESSING);
        }
		catch (Exception ex)
		{
			log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ConfigurationDAO[loadINConfigurations]","","","",PretupsI.EXCEPTION+ex.getMessage());
			throw new BTSLBaseException(this, "loadINConfigurations()", ERROR_GENERAL_PROCESSING);
        } 
		finally 
		{
          
            LogFactory.printLog(methodName, "Exited: Map size="+pMap.size(), log);
        }
    }

	/**
	 * Method loadConfigurationList.
	 * This method is used to load the list of the preferences of the specified type as the argument pPreferenceType
	 * this method loads only those records which is allowed to display.
	 * @author sandeep.goel
	 * @param pCon Connection
	 * @param pPreferenceType String
	 * @param pModuleCode
	 * @return List
	 * @throws BTSLBaseException
	 */
	public List loadConfigurationList(Connection pCon,String pPreferenceType, String pModuleCode) throws BTSLBaseException
	{
		final String methodName = "loadConfigurationList";
		LogFactory.printLog(methodName, "Entered: preferenceType="+pPreferenceType+",pModuleCode="+pModuleCode, log);
		List<ListValueVO> preferenceTypeList = new ArrayList<ListValueVO>();
		 
		try{
			StringBuilder selectQuery=new StringBuilder();
			selectQuery.append("SELECT SP.preference_code,SP.name ");
			selectQuery.append("FROM system_preferences SP,lookups L1,lookups L2 ");
			selectQuery.append("WHERE SP.type= ? AND SP.display='Y' AND SP.value_type=L2.lookup_code AND SP.type=L1.lookup_code ");
			if(!BTSLUtil.isNullString(pModuleCode)) {
				selectQuery.append("AND SP.module = ?");
			}
			selectQuery.append("ORDER BY SP.name");
			LogFactory.printLog(methodName, "Query="+selectQuery, log);
			try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery.toString());)
			{
			pstmtSelect.setString(1,pPreferenceType);
			if(!BTSLUtil.isNullString(pModuleCode)) {
				pstmtSelect.setString(2,pModuleCode);
			}
			try(ResultSet rs = pstmtSelect.executeQuery();)
			{
			while(rs.next())
			{
				ListValueVO listValueVO =new ListValueVO(rs.getString("name"),rs.getString("preference_code"));
				preferenceTypeList.add(listValueVO);
			}
		}
			}
		}
		catch(SQLException sqe)
		{
			 log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ConfigurationDAO[loadConfigurationList]","","","",PretupsI.SQLEXCEPTION+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, ERROR_GENERAL_SQL_PROCESSING);
		}
		catch(Exception e)
		{
			 log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ConfigurationDAO[loadConfigurationList]","","","",PretupsI.EXCEPTION+e.getMessage());
			throw new BTSLBaseException(this, methodName, ERROR_GENERAL_PROCESSING);
		}
		finally
		{
			
			LogFactory.printLog(methodName, PretupsI.EXITED+preferenceTypeList.size(), log);
		}
		return preferenceTypeList;
	}
	
	
	/**
	 * @param pCon
	 * @param pConfigType
	 * @return
	 * @throws BTSLBaseException
	 */
	public List<ListValueVO> loadInstanceIdList(Connection pCon,String pConfigType) throws BTSLBaseException
	{
		final String methodName = "loadInstanceIdList";
		LogFactory.printLog(methodName, "Entered: pConfigType="+pConfigType, log);
		List<ListValueVO> instanceIdList = new ArrayList<ListValueVO>();
		 
		try{
			StringBuilder selectQuery=new StringBuilder();
			selectQuery.append("SELECT distinct instance_id ");
			selectQuery.append("FROM configurations ");
			selectQuery.append("WHERE type=? ");
			selectQuery.append("ORDER BY instance_id");
			LogFactory.printLog(methodName, "Query="+selectQuery, log);
			try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery.toString());)
			{
			pstmtSelect.setString(1,pConfigType);
			try(ResultSet rs = pstmtSelect.executeQuery();)
			{
			while(rs.next())
			{
				ListValueVO listValueVO = new ListValueVO(rs.getString("instance_id"),rs.getString("instance_id"));
				instanceIdList.add(listValueVO);
			}
		}
			}
		}
		catch(SQLException sqe)
		{
			 log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ConfigurationDAO[loadInstanceIdList]","","","",PretupsI.SQLEXCEPTION+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, ERROR_GENERAL_SQL_PROCESSING);
		}
		catch(Exception e)
		{
			 log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ConfigurationDAO[loadInstanceIdList]","","","",PretupsI.EXCEPTION+e.getMessage());
			throw new BTSLBaseException(this, methodName, ERROR_GENERAL_PROCESSING);
		}
		finally
		{
			
			LogFactory.printLog(methodName, PretupsI.EXITED+instanceIdList.size(), log);
		}
		return instanceIdList;
	}
	
	/**
	 * @param pCon
	 * @param pConfigType
	 * @param pInstanceId
	 * @return
	 * @throws BTSLBaseException
	 */
	public List<ListValueVO> loadInterfaceIdList(Connection pCon,String pConfigType,String pInstanceId) throws BTSLBaseException
	{
		final String methodName = "loadInterfaceIdList";
		LogFactory.printLog(methodName, "Entered: pConfigType="+pConfigType, log);
		List<ListValueVO> interfaceIdList = new ArrayList<ListValueVO>();
		 
		try{
			StringBuilder selectQuery=new StringBuilder();
			selectQuery.append("SELECT distinct interface_id ");
			selectQuery.append("FROM configurations ");
			selectQuery.append("WHERE type=? AND instance_id=?");
			selectQuery.append("ORDER BY interface_id");
			LogFactory.printLog(methodName, "Query="+selectQuery, log);
			try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery.toString());)
			{
			pstmtSelect.setString(1,pConfigType);
			pstmtSelect.setString(2,pInstanceId);
			try(ResultSet rs = pstmtSelect.executeQuery();)
			{
			while(rs.next())
			{
				ListValueVO listValueVO = new ListValueVO(rs.getString("interface_id"),rs.getString("interface_id"));
				interfaceIdList.add(listValueVO);
			}
		}
			}
		}
		catch(SQLException sqe)
		{
			 log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ConfigurationDAO[loadInterfaceIdList]","","","",PretupsI.SQLEXCEPTION+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, ERROR_GENERAL_SQL_PROCESSING);
		}
		catch(Exception e)
		{
			 log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ConfigurationDAO[loadInterfaceIdList]","","","",PretupsI.EXCEPTION+e.getMessage());
			throw new BTSLBaseException(this, methodName, ERROR_GENERAL_PROCESSING);
		}
		finally
		{
			
			LogFactory.printLog(methodName, PretupsI.EXITED+interfaceIdList.size(), log);
		}
		return interfaceIdList;
	}
	
	/**
	 * Method that will give the no of service class present apart from ALL (Default)
	 * @param pCon
	 * @param pServiceClassCode
	 * @param pPreferenceCode
	 * @param pNetworkID
	 * @param pModule
	 * @return int
	 * @throws BTSLBaseException
	 */  
	public int getServiceClassCountOfCode(Connection pCon, String pServiceClassCode,String pPreferenceCode,String pNetworkID,String pModule) throws BTSLBaseException
	  {
		final String methodName = "getServiceClassCountOfCode";
	        LogFactory.printLog(methodName, "Entered pNetworkID="+pNetworkID+" pModule="+pModule+" pServiceClassCode"+pServiceClassCode+" pPreferenceCode="+pPreferenceCode, log);
	        
	        
	        StringBuilder strBuff = new StringBuilder();
			int i=0;
	        strBuff.append(" SELECT count(service_class_id) cnt ");
	        strBuff.append(" FROM service_class_preferences scp ");
	        strBuff.append(" WHERE scp.preference_code = ?  AND service_class_id<>? AND network_code=? AND module=? ");
			
	        String sqlSelect = strBuff.toString();
			LogFactory.printLog(methodName, "QUERY sqlSelect=" + sqlSelect, log);
	        try (PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);)
			{
                
				pstmt.setString(1,pPreferenceCode);
				pstmt.setString(2,pServiceClassCode);
				pstmt.setString(3,pNetworkID);
				pstmt.setString(4,pModule);
	            try(ResultSet rs = pstmt.executeQuery();)
	            {
	            while (rs.next()) 
				{
					i=rs.getInt("cnt");		                
	            }            
				return i;
	        } 
			}
			catch (SQLException sqe) 
			{
	            log.errorTrace(methodName, sqe);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ConfigurationDAO[loadServicePreferences]","","","",PretupsI.SQLEXCEPTION+sqe.getMessage());
	            throw new BTSLBaseException(this, "getServiceClassCountOfCode()", ERROR_GENERAL_PROCESSING);
	        } 
			catch (Exception ex) 
			{
				log.errorTrace(methodName, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ConfigurationDAO[loadServicePreferences]","","","",PretupsI.EXCEPTION+ex.getMessage());
	            throw new BTSLBaseException(this, "getServiceClassCountOfCode()", ERROR_GENERAL_PROCESSING);
	        } 
			finally 
			{
	            
	            LogFactory.printLog(methodName, "Exited: i="+i, log);
	        }
	    }

}
