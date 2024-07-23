/**
* @(#)ConfigurationWebDAO.java
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

package com.web.pretups.configuration.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.btsl.pretups.configuration.businesslogic.ConfigurationCacheVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.util.BTSLUtil;

public class ConfigurationWebDAO {

	
    /**
     * Commons Logging instance.
     */
    private static Log log = LogFactory.getLog(ConfigurationWebDAO.class.getName());
    public static final String ERROR_GENERAL_PROCESSING = "error.general.processing";
    public static final String ERROR_GENERAL_SQL_PROCESSING = "error.general.sql.processing";
    
    
	/**
	 * Method loadConfigurationData.
	 * This method is to load the data of the type SYSTEM PREFERENCE and only those records which
	 * are allowed to display i.e. display='Y'.
	 * @author sandeep.goel
	 * @param pCon Connection
	 * @param pConfigType String
	 * @param pInstanceId String
	 * @param pInterfaceId String
	 * @return List
	 * @throws BTSLBaseException
	 */
	public List<ConfigurationCacheVO> loadConfigurationData(Connection pCon,String pConfigType, String pInstanceId,String pInterfaceId) throws BTSLBaseException
	{
		final String methodName = "loadConfigurationData";
		StringBuffer msg = new StringBuffer("");
		msg.append("Entered: pConfigType : ");
		msg.append(pConfigType);
		msg.append(", pInstanceId : ");
		msg.append(pInstanceId);
		msg.append(", pInterfaceId");
		msg.append(pInterfaceId);
		LogFactory.printLog(methodName, msg.toString(), log);
	
		ConfigurationCacheVO configurationCacheVO = null;  
		
		List<ConfigurationCacheVO> configList=new ArrayList<ConfigurationCacheVO>();
		try
		{
			StringBuilder selectQuery=new StringBuilder();
			selectQuery.append("SELECT instance_id, interface_id, type, key, ");
			selectQuery.append("value, description, modified_allowed, ");
			selectQuery.append("modified_on FROM configurations ");
			selectQuery.append("WHERE display_allowed = 'Y' ");
			selectQuery.append("AND type = ? AND instance_id = ? AND interface_id = ? ORDER BY key");
			LogFactory.printLog(methodName, "Query="+selectQuery, log);
			try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery.toString());)
			{

			pstmtSelect.setString(1,pConfigType);
			pstmtSelect.setString(2,pInstanceId);
			pstmtSelect.setString(3,pInterfaceId);
			try(ResultSet rs = pstmtSelect.executeQuery();)
			{
			List valueTypeList = LookupsCache.loadLookupDropDown(PretupsI.CONFIGURATION_TYPE,true);
			List<ListValueVO> descriptionList=new ArrayList<ListValueVO>();
			List<ListValueVO> descriptionListNew=new ArrayList<ListValueVO>();
			ListValueVO listValueVO=null;
			while(rs.next())
			{
				configurationCacheVO=new ConfigurationCacheVO();
				configurationCacheVO.setType("type");
				configurationCacheVO.setIntidKey(rs.getString("key"));
				configurationCacheVO.setValue(rs.getString("value"));
				configurationCacheVO.setInstanceId(rs.getString("instance_id"));
				configurationCacheVO.setInterfaceId(rs.getString("interface_id"));
				configurationCacheVO.setValueTypeDesc((BTSLUtil.getOptionDesc(configurationCacheVO.getValueType(),valueTypeList)).getLabel());
				configurationCacheVO.setDescription(rs.getString("description"));
				configurationCacheVO.setDescriptionNew(rs.getString("description"));
				configurationCacheVO.setModifiedAllowed(rs.getString("modified_allowed"));
				if(configurationCacheVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.MODIFY_ALLOWED_YES)) {
					configurationCacheVO.setDisableAllow(false);
				} else if(configurationCacheVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.MODIFY_ALLOWED_NO)) {
					configurationCacheVO.setDisableAllow(true);
				}
				configurationCacheVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());

				listValueVO = new ListValueVO(rs.getString("description"),rs.getString("description"));
				descriptionList.add(listValueVO);
				descriptionListNew.add(listValueVO);
				configurationCacheVO.setAllowAction("N");//for default selection of the radio button
				configList.add(configurationCacheVO);
			}
			if(configurationCacheVO != null) {
				configurationCacheVO.setDescriptionList(descriptionList);
				configurationCacheVO.setDescriptionListNew(descriptionListNew);
			}
		}
			}
		}
		catch(SQLException sqe)
		{
			log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PreferenceDAO[loadConfigurationData]","","","",PretupsI.SQLEXCEPTION+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, ERROR_GENERAL_SQL_PROCESSING);
		}
		catch(Exception e)
		{
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PreferenceDAO[loadConfigurationData]","","","",PretupsI.EXCEPTION+e.getMessage());
			throw new BTSLBaseException(this, methodName, ERROR_GENERAL_PROCESSING);
		}
		finally
		{
			
			LogFactory.printLog(methodName, "Exiting:configList size="+configList.size(), log);
		}
		return configList;
	}
	
	/**
	 * Method updateConfigurations.
	 * This method is to update the record of the SYSTEM PREFERENCE of the specified preferenceCode.
	 * @author sandeep.goel
	 * @param pCon Connection
	 * @param p_preferenceList List
	 * @param pDate Date
	 * @param pUserID String
	 * @return int
	 * @throws BTSLBaseException
	 */
	public int 	updateConfigurations(Connection pCon,List pConfigList,Date pDate,String pUserID) throws BTSLBaseException
	{
		final String methodName = "updateConfigurations";
		StringBuffer msg = new StringBuffer("");
		msg.append("Entered: pConfigList.size() : ");
		msg.append(pConfigList.size());
		msg.append(", pDate : ");
		msg.append(pDate);
		msg.append(", pUserID");
		msg.append(pUserID);
		LogFactory.printLog(methodName, msg.toString(), log);
		
		int updateCount=0;
		try
		{
			StringBuilder updateQuery= new StringBuilder();
			updateQuery.append("UPDATE configurations SET value=?,description=?,modified_by=?,modified_on=? ");
			updateQuery.append("WHERE key=? and instance_id=? and interface_id=? and modified_allowed='Y'");
			String query=updateQuery.toString();
			LogFactory.printLog(methodName, "Query="+query, log);
			try(PreparedStatement pstmtUpdate=pCon.prepareStatement(query);)
			{
			ConfigurationCacheVO configurationCacheVO=null;
			for(int i=0;i<pConfigList.size();i++)
			{
				configurationCacheVO=(ConfigurationCacheVO)pConfigList.get(i);
				pstmtUpdate.setString(1,configurationCacheVO.getValue());
				pstmtUpdate.setString(2,configurationCacheVO.getDescription());
				pstmtUpdate.setString(3,pUserID);
				pstmtUpdate.setTimestamp(4,BTSLUtil.getTimestampFromUtilDate(pDate));
				pstmtUpdate.setString(5,configurationCacheVO.getIntidKey());
				pstmtUpdate.setString(6,configurationCacheVO.getInstanceId());
				pstmtUpdate.setString(7,configurationCacheVO.getInterfaceId());
				//for the checking is the record be modified during the transaction.
				boolean modified=isRecordModified(pCon,configurationCacheVO,1);
				if(modified) {
					throw new BTSLBaseException(this, methodName, "error.modify.true");
				}
				updateCount +=pstmtUpdate.executeUpdate();
				pstmtUpdate.clearParameters();
			}
			
		}
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(SQLException sqe)
		{
			log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PreferenceDAO[updateConfigurations]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, ERROR_GENERAL_SQL_PROCESSING);
		}
		catch(Exception e)
		{
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PreferenceDAO[updateConfigurations]","","","",PretupsI.EXCEPTION+e.getMessage());
			throw new BTSLBaseException(this, methodName, ERROR_GENERAL_PROCESSING);
		}
		finally
		{			
			LogFactory.printLog(methodName, "Exiting:return="+updateCount, log);
		}
		return updateCount;
	}
	
	/**
	 * Method recordModified.
	 * This method is used to check is the record modified during the transaction for the various 
	 * preference tables depending on the value of the flag
	 * flag=1 for system preferences table
	 * flag=2 for network preferences
	 * flag=3 for service class preferences table
	 * flag=4 for zone preferences table
	 * @author sandeep.goel
	 * @param pCon Connection
	 * @param pConfigurationCacheVO ConfigurationCacheVO
	 * @param pFlag int
	 * @return boolean
	 * @throws BTSLBaseException
	 */
	public boolean isRecordModified(Connection pCon,ConfigurationCacheVO pConfigurationCacheVO,int pFlag)throws BTSLBaseException
	{
		final String methodName = "isRecordModified";
		StringBuffer msg = new StringBuffer("");
		msg.append("Entered: pConfigurationCacheVO : ");
		msg.append(pConfigurationCacheVO);
		msg.append(", pFlag : ");
		msg.append(pFlag);

		LogFactory.printLog(methodName, msg.toString(), log);
		PreparedStatement pstmtSelect = null;
		ResultSet rs=null;
		boolean modified=false;
		StringBuilder sqlRecordModified=new StringBuilder();
		try
		{
			if(pFlag==1)// for INTID configurations
			{
				sqlRecordModified.append("SELECT modified_on FROM configurations ");
				sqlRecordModified.append("WHERE key=? and instance_id=? and interface_id=?");
				LogFactory.printLog(methodName, "QUERY="+sqlRecordModified, log);
				String query=sqlRecordModified.toString();
				pstmtSelect = pCon.prepareStatement(query);
				pstmtSelect.setString(1,pConfigurationCacheVO.getIntidKey());
				pstmtSelect.setString(2,pConfigurationCacheVO.getInstanceId());
				pstmtSelect.setString(3,pConfigurationCacheVO.getInterfaceId());
			}
			Timestamp newlastModified = null;
			if(pConfigurationCacheVO.getLastModifiedTime()==0)
			{
				return false;
			}
			rs = pstmtSelect.executeQuery();
			if(rs.next())
			{
				newlastModified = rs.getTimestamp("modified_on");
			}	
			if(newlastModified.getTime()!=pConfigurationCacheVO.getLastModifiedTime())
			{
				modified=true;
			}
		}//end of try
		catch(SQLException sqe)
		{
			log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PreferenceDAO[isRecordModified]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, ERROR_GENERAL_SQL_PROCESSING);
		}//end of catch
		catch(Exception e)
		{
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PreferenceDAO[isRecordModified]","","","",PretupsI.EXCEPTION+e.getMessage());
			throw new BTSLBaseException(this, methodName, ERROR_GENERAL_PROCESSING);
		}
		finally
		{
			try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
			try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing statement.", e);
            }
			LogFactory.printLog(methodName, "Exititng:modified="+modified, log);
		}//end of finally
		return modified;
	}//end recordModified
	
}
