package com.btsl.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;


public class BatchGeographicalDomainDAO {

	private Log _log = LogFactory.getLog(BatchGeographicalDomainDAO.class.getName());
	
	
	/**
	 * @param p_con
	 * @param p_geographyDomainList
	 * @param p_messages
	 * @param p_locale
	 * @param p_userVO
	 * @param p_fileName
	 * @return dbErrorList
	 * @throws BTSLBaseException
	 * @author vikas.chaudhary
	 */
	public ArrayList addGeographicalDomainsList(Connection p_con, ArrayList<BatchGeographicalDomainVO> p_geographyDomainList,UserVO p_userVO,String p_fileName)throws BTSLBaseException
	{
		final String METHOD_NAME ="BatchGeographicalDomainDAO[addGeographicalDomainsList()]";
    	if (_log.isDebugEnabled())
    	    _log.debug(METHOD_NAME, "Entered: p_geographyDomainList.size()="+p_geographyDomainList.size()+" p_fileName: "+p_fileName);
    	boolean batchIdFlag = true; 
    	String batchID=null;
    	int commitCounter=0,updateCount=0;
	    int commitNumber=0;
	    String[] arr = null;
	    try{ 
	        commitNumber = Integer.parseInt(Constants.getProperty("BATCH_USER_COMMIT_NUMBER"));
	    }catch(Exception e){
	    	_log.error(METHOD_NAME, "RuntimeException" + e);
			_log.errorTrace(METHOD_NAME,e);
	    	commitNumber=100;}
		OperatorUtilI operatorUtil=null;
    	ArrayList<ListValueVO> dbErrorList = new ArrayList<ListValueVO>();
    	ListValueVO errorVO = null;
    	StringBuffer strBuff = null;
    	PreparedStatement psmtBatchInsert = null;
    	PreparedStatement psmtBatchUpdate = null;
    	PreparedStatement psmtBatchGeography = null;
    	
		int insertModifyDeleteGeographicalDomainCount=0;
    	try
		{
    	    String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
    	    try{
				operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
			}catch(Exception e){
				_log.error(METHOD_NAME, "Exception:" + e.getMessage());
				_log.errorTrace(METHOD_NAME, e);
				
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,METHOD_NAME,"","","","Exception while loading the class at the call:"+e.getMessage());
			}
			
			//batches insert
	    	strBuff = new StringBuffer("INSERT INTO batches (batch_id, batch_type, batch_size, batch_name, ");
	    	strBuff.append("network_code, status, created_by, created_on, modified_by, modified_on,file_name) ");
	    	strBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?)");
	    	String insertBatchQuery = strBuff.toString();
			psmtBatchInsert = p_con.prepareStatement(insertBatchQuery);
	    	
	    	//update batches table
			strBuff = new StringBuffer("UPDATE batches SET batch_size=?, status=? WHERE batch_id=? ");
	    	String updateBatchQuery = strBuff.toString();
			psmtBatchUpdate = p_con.prepareStatement(updateBatchQuery);
	    	
			//geographical domain insert
			strBuff =new StringBuffer();
			strBuff.append("INSERT INTO geographical_domains(grph_domain_code, network_code, grph_domain_name,");
			strBuff.append("parent_grph_domain_code, grph_domain_short_name, description, status, grph_domain_type, created_on, created_by, modified_on, modified_by ");
			strBuff.append(" ) VALUES(UPPER(?),?,?,?,?,?,?,?,?,?,?,?)");
			
			String insertGeographicalDomainQuery=strBuff.toString();
			
			//geographical domain modify
			strBuff =new StringBuffer();
			strBuff.append("UPDATE geographical_domains SET grph_domain_name=?, ");
			strBuff.append(" grph_domain_short_name=?, description=?, modified_on=?, modified_by=? ");
			strBuff.append(" WHERE grph_domain_code=? AND network_code=? AND parent_grph_domain_code=? AND grph_domain_type=?");
			
			String updateGeographicalDomainQuery=strBuff.toString();
			
			//geographical domain delete (NO hard delete, only status will be updated to N)
			strBuff =new StringBuffer();
			strBuff.append("UPDATE geographical_domains SET status=?,  modified_on=?, modified_by=? ");
			strBuff.append(" WHERE grph_domain_code=? AND network_code=? AND parent_grph_domain_code=? AND grph_domain_type=?");
			
			String deleteGeographicalDomainQuery=strBuff.toString();
						
			Iterator batchGeographicalDomainVOListItr = p_geographyDomainList.iterator();
			BatchGeographicalDomainVO batchGeoDomainVO = null;
			Start:
			while(batchGeographicalDomainVOListItr.hasNext())
			{
				batchGeoDomainVO = (BatchGeographicalDomainVO)batchGeographicalDomainVOListItr.next();
	            arr = new String[]{batchGeoDomainVO.getGrphDomainCode() + "," + batchGeoDomainVO.getNetworkCode() + "," + batchGeoDomainVO.getGrphDomainName() + "," + batchGeoDomainVO.getParentDomainCode() + "," + batchGeoDomainVO.getGrphDomainShortName() + "," + batchGeoDomainVO.getGrphDomainType()};
			    
    	        if(batchIdFlag)
    	        {
    	            //one time entry into batches table
					batchID = operatorUtil.formatBatchesID(p_userVO.getNetworkID(),PretupsI.BATCH_GRPH_DOMAIN_PREFIX,new Date(),IDGenerator.getNextID(PretupsI.BATCH_GRPH_DOMAIN_ID,BTSLUtil.getFinancialYear(),p_userVO.getNetworkID()));
					psmtBatchInsert.setString(1,batchID);
					psmtBatchInsert.setString(2,PretupsI.BATCH_GRPH_CREATION);
					psmtBatchInsert.setInt(3,p_geographyDomainList.size());
					psmtBatchInsert.setString(4,batchGeoDomainVO.getBatchName());
					psmtBatchInsert.setString(5,batchGeoDomainVO.getNetworkCode());
					psmtBatchInsert.setString(6,PretupsI.BATCH_GRPH_DOMAIN_STATUS_UNDERPROCESS);
					psmtBatchInsert.setString(7,batchGeoDomainVO.getCreatedBy());
					psmtBatchInsert.setTimestamp(8,BTSLUtil.getTimestampFromUtilDate(batchGeoDomainVO.getCreatedOn()));
					psmtBatchInsert.setString(9,batchGeoDomainVO.getModifiedBy());
					psmtBatchInsert.setTimestamp(10,BTSLUtil.getTimestampFromUtilDate(batchGeoDomainVO.getModifiedOn()));
					psmtBatchInsert.setString(11,p_fileName);
    	            if(psmtBatchInsert.executeUpdate()<=0)
    	            {
    	                p_con.rollback();
    	                throw new BTSLBaseException(this,METHOD_NAME,"master.createbatchgeographicaldomains.err.batchnotcreated","selectfile");
    	            }
    	            batchIdFlag = false;
    	        }
    	        //After 100 record commit the records
    	        if(commitCounter > commitNumber)
    	        {
    	            p_con.commit();
    	            commitCounter=0;//reset commit counter
    	        }
    	        
    	        if(PretupsI.ADD_ACTION.equalsIgnoreCase(batchGeoDomainVO.getAction())) {
    	        	psmtBatchGeography = p_con.prepareStatement(insertGeographicalDomainQuery);
    	        } else if(PretupsI.MODIFY_ACTION.equalsIgnoreCase(batchGeoDomainVO.getAction())) {
    	        	psmtBatchGeography = p_con.prepareStatement(updateGeographicalDomainQuery);
    	        } else if(PretupsI.DELETE_ACTION.equalsIgnoreCase(batchGeoDomainVO.getAction())) {
    	        	psmtBatchGeography = p_con.prepareStatement(deleteGeographicalDomainQuery);
    	        }
    	        
		        //insert geography domain
    	        insertModifyDeleteGeographicalDomainCount = this.addModifyDeleteGeographyDomain(psmtBatchGeography, batchGeoDomainVO);
		        if(insertModifyDeleteGeographicalDomainCount <= 0){
		            p_con.rollback();
    	            errorVO=new ListValueVO(batchGeoDomainVO.getRecordNumber(),"master.createbatchgeographicaldomains.msg.error.geaographicaldomaininsertfail");
    	            dbErrorList.add(errorVO);
			        continue Start;
		        }
		        commitCounter++;
		        updateCount++;
			} // end while loop
			if(updateCount > 0)
			{
			    psmtBatchUpdate.setInt(1,updateCount);
    	        psmtBatchUpdate.setString(2,PretupsI.BATCH_GRPH_DOMAIN_STATUS_CLOSE);
    	        psmtBatchUpdate.setString(3,batchID);
    	        psmtBatchUpdate.executeUpdate();
    	        psmtBatchUpdate.clearParameters();
    	        p_con.commit();
			}else{
			    p_con.rollback();
			}
			errorVO = new ListValueVO("BATCHID","",batchID);
			dbErrorList.add(errorVO);
		
		} // end of try
    	catch (SQLException sqe)
    	{
    		
    	    try{if (p_con != null){p_con.rollback();}} catch (Exception e){
    	    	_log.error(METHOD_NAME, "RuntimeException" + e);
    			_log.errorTrace(METHOD_NAME,e);
    	    }
			_log.errorTrace(METHOD_NAME,sqe);
    	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,METHOD_NAME,"","","","SQL Exception:"+sqe.getMessage());
    	    throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
    	}
    	catch (Exception ex)
    	{
    	    try{if (p_con != null){p_con.rollback();}} catch (Exception e){
    	    	_log.error(METHOD_NAME, "RuntimeException" + e);
    			_log.errorTrace(METHOD_NAME,e);
    	    }
    	    _log.errorTrace(METHOD_NAME,ex);
    	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,METHOD_NAME,"","","","Exception:"+ex.getMessage());
    	    throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
    	}
    	finally
		{
    		try{
    	        if (psmtBatchInsert!= null){
    	        	psmtBatchInsert.close();
    	        }
    	      }
    	      catch (SQLException e){
    	    	  _log.error("An error occurred closing statement.", e);
    	      }
    		try{
    	        if (psmtBatchUpdate!= null){
    	        	psmtBatchUpdate.close();
    	        }
    	      }
    	      catch (SQLException e){
    	    	  _log.error("An error occurred closing statement.", e);
    	      }
    		try{
    	        if (psmtBatchGeography!= null){
    	        	psmtBatchGeography.close();
    	        }
    	      }
    	      catch (SQLException e){
    	    	  _log.error("An error occurred closing statement.", e);
    	      }
    	    
    	    if (_log.isDebugEnabled())
				_log.debug(METHOD_NAME, "Exiting: insertModifyDeleteGeographicalDomainCount=" + insertModifyDeleteGeographicalDomainCount);
		}
    	
    	return dbErrorList;
	    	    
	 }
	
	/**
	 * Method addModifyDeleteGeographyDomain.
	 * This method is used to add/update/delete the record in the geography domains table .
	 * @author vikas.chaudhary 
	 * @param p_psmtBatchGeography PreparedStatement
	 * @param p_batchGeoDomainVO BatchGeographicalDomainVO
	 * @return int
	 * @throws BTSLBaseException
	 */
	public int addModifyDeleteGeographyDomain(PreparedStatement p_psmtBatchGeography, BatchGeographicalDomainVO p_batchGeoDomainVO) throws BTSLBaseException
	{
		final String METHOD_NAME ="BatchGeographicalDomainDAO[addModifyDeleteGeographyDomain()]";
		if(_log.isDebugEnabled())
		    _log.debug(METHOD_NAME,"Entered p_batchGeoDomainVO:"+p_batchGeoDomainVO.toString());
		int addCount=0;
		try
		{
			if(PretupsI.DELETE_ACTION.equalsIgnoreCase(p_batchGeoDomainVO.getAction())) {
				
				p_psmtBatchGeography.setString(1, p_batchGeoDomainVO.getStatus());
				p_psmtBatchGeography.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_batchGeoDomainVO.getModifiedOn()));
				p_psmtBatchGeography.setString(3, p_batchGeoDomainVO.getModifiedBy());
				p_psmtBatchGeography.setString(4, p_batchGeoDomainVO.getGrphDomainCode());
				p_psmtBatchGeography.setString(5, p_batchGeoDomainVO.getNetworkCode());
				p_psmtBatchGeography.setString(6, p_batchGeoDomainVO.getParentDomainCode());
				p_psmtBatchGeography.setString(7, p_batchGeoDomainVO.getGrphDomainType());
				
			} else if (PretupsI.ADD_ACTION.equalsIgnoreCase(p_batchGeoDomainVO.getAction())) {
				
				p_psmtBatchGeography.setString(1, p_batchGeoDomainVO.getGrphDomainCode());
				p_psmtBatchGeography.setString(2, p_batchGeoDomainVO.getNetworkCode());
				
				p_psmtBatchGeography.setString(3, p_batchGeoDomainVO.getGrphDomainName());

				p_psmtBatchGeography.setString(4, p_batchGeoDomainVO.getParentDomainCode());
				
				p_psmtBatchGeography.setString(5, p_batchGeoDomainVO.getGrphDomainShortName());

				p_psmtBatchGeography.setString(6, p_batchGeoDomainVO.getDescription());
				
				p_psmtBatchGeography.setString(7, p_batchGeoDomainVO.getStatus());
				p_psmtBatchGeography.setString(8, p_batchGeoDomainVO.getGrphDomainType());
				p_psmtBatchGeography.setTimestamp(9,BTSLUtil.getTimestampFromUtilDate(p_batchGeoDomainVO.getCreatedOn()));
				p_psmtBatchGeography.setString(10, p_batchGeoDomainVO.getCreatedBy());
				p_psmtBatchGeography.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_batchGeoDomainVO.getModifiedOn()));
				p_psmtBatchGeography.setString(12, p_batchGeoDomainVO.getModifiedBy());
				
			} else if (PretupsI.MODIFY_ACTION.equalsIgnoreCase(p_batchGeoDomainVO.getAction())) {
				
				p_psmtBatchGeography.setString(1, p_batchGeoDomainVO.getGrphDomainName());
				p_psmtBatchGeography.setString(2, p_batchGeoDomainVO.getGrphDomainShortName());
				p_psmtBatchGeography.setString(3, p_batchGeoDomainVO.getDescription());
				p_psmtBatchGeography.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_batchGeoDomainVO.getModifiedOn()));
				p_psmtBatchGeography.setString(5, p_batchGeoDomainVO.getModifiedBy());
				p_psmtBatchGeography.setString(6, p_batchGeoDomainVO.getGrphDomainCode());
				p_psmtBatchGeography.setString(7, p_batchGeoDomainVO.getNetworkCode());
				p_psmtBatchGeography.setString(8, p_batchGeoDomainVO.getParentDomainCode());
				p_psmtBatchGeography.setString(9, p_batchGeoDomainVO.getGrphDomainType());
						
			}
			
			addCount = p_psmtBatchGeography.executeUpdate();
			p_psmtBatchGeography.clearParameters();
		}//end of try
		catch (SQLException sqle)
		{
			_log.errorTrace(METHOD_NAME,sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,METHOD_NAME,"","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
		}//end of catch
		catch (Exception e)
		{
			_log.errorTrace(METHOD_NAME,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,METHOD_NAME,"","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
		}//end of catch
		finally
		{
			try{
		        if (p_psmtBatchGeography!= null){
		        	p_psmtBatchGeography.close();
		        }
		      }
		      catch (SQLException e){
		    	  _log.error("An error occurred closing statement.", e);
		      }
			if(_log.isDebugEnabled())
			    _log.debug(METHOD_NAME,"Exiting addCount="+addCount);
		 }//end of finally
		return addCount;
	}

}
