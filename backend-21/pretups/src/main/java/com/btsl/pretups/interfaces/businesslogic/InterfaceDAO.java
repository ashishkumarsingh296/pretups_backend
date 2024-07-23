/*
 * @# InterfaceDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * June 10, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.btsl.pretups.interfaces.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.btsl.util.SqlParameterEncoder;
import com.restapi.superadmin.interfacemanagement.responseVO.InterfaceTypeVO;


/**
 * This is interface class to manage interfaces
 *
 */
public class InterfaceDAO {

    private Log log = LogFactory.getFactory().getInstance(InterfaceDAO.class.getName());
    private String errsqlMesg="error.general.sql.processing";
    private String errGeneralMesg="error.general.processing";
    private String sqlExpMsg="SQL Exception:";

    /**
     * Constructor for InterfaceDAO.
     */ 
    public InterfaceDAO() {
        super();
    }

    /**
     * Method loadInterfaceTypeId.
     * This method is used to load interface type id from interface_types table
     * If there is any error then throws the SQLException
     * 
     * @param pCon
     *            Connection
     * @param pInterfaceCategory
     *            String
     * @return interfaceTypeIdList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList<ListValueVO> loadInterfaceTypeId(Connection pCon, String pInterfaceCategory) throws BTSLBaseException {
        final String methodName = "loadInterfaceTypeId";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_interfaceCategory=" + pInterfaceCategory);
        }

         
        
        ListValueVO listValueBean = null;
        ArrayList<ListValueVO> interfaceTypeIdList = new ArrayList<ListValueVO>();
        StringBuilder strBuilder = new StringBuilder("SELECT interface_type_id,interface_name");
        strBuilder.append(" FROM interface_types WHERE interface_type_id=?");
        strBuilder.append(" ORDER BY interface_type_id");
        String sqlSelect = strBuilder.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Select Query= " + sqlSelect);
        }

        try(PreparedStatement pstmtSelect = pCon.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, pInterfaceCategory);
           try(ResultSet rs = pstmtSelect.executeQuery();)
           {
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Query Executed = " + sqlSelect);
            }
            while (rs.next()) {
                listValueBean = new ListValueVO(SqlParameterEncoder.encodeParams(rs.getString("interface_name")), 
                		SqlParameterEncoder.encodeParams(rs.getString("interface_type_id")));
                interfaceTypeIdList.add(listValueBean);
            }
        }
        }
        catch (SQLException sqe) {
            log.error(methodName, "SQL Exception  " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceTypeId]", "", "", "", "SQL Exception," + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg);
        } catch (Exception e) {
            log.error(methodName, " Exception  " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceTypeId]", "", "", "", "Exception_:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg);

        } finally {
            
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting size= " + interfaceTypeIdList.size());
            }
        }

        return interfaceTypeIdList;
    }

    /**
     * Method loadInterfaceDetails.
     * This method is used to load interface details into ArrayList
     * If there is any error then throws the SQLException or Exception
     * 
     * @param pCon
     *            Connection
     * @param pInterfaceCategory
     *            String
     * @return interfaceDetails ArrayList
     */
	
 	public ArrayList<InterfaceVO> loadInterfaceDetails(Connection pCon,String InterfaceCategory, String CategoryCode, String NetworkCode) 
 	throws BTSLBaseException
	{
 	   final String methodName = "loadInterfaceDetails";
 	    LogFactory.printLog(methodName, "Entered p_interfaceCategory= "+InterfaceCategory+"p_categoryCode="+CategoryCode+"p_networkCode="+NetworkCode, log);
		StringBuilder strBuilder= null;
		String pInterfaceCategory = SqlParameterEncoder.encodeParams(InterfaceCategory);
		String pCategoryCode = SqlParameterEncoder.encodeParams(CategoryCode);
		String pNetworkCode = SqlParameterEncoder.encodeParams(NetworkCode);
		ArrayList<InterfaceVO> interfaceDetails = new ArrayList<InterfaceVO>();
   		try
		{
   		  strBuilder=new StringBuilder("SELECT masters.interface_name,");
   		  strBuilder.append("detail.interface_description,masters.interface_type_id,");
   		  strBuilder.append("lcat.lookup_name AS category,masters.interface_category,masters.max_nodes,");
   		  strBuilder.append("detail.interface_id,detail.external_id,");
   		  strBuilder.append("detail.concurrent_connection,lstat.lookup_name AS statusDesc,");
   		  strBuilder.append("detail.status,detail.single_state_transaction,");
   		  strBuilder.append("detail.message_language1,detail.message_language2,");
   		  strBuilder.append("detail.modified_on,detail.status_type, detail.val_expiry_time,detail.topup_expiry_time, detail.NUMBER_OF_NODES FROM interface_types");
   		  strBuilder.append(" masters,interfaces detail,lookups lcat,lookups lstat");
   		  if(pCategoryCode!=null && !pCategoryCode.equals("SUADM"))
   			  strBuilder.append(" ,INTERFACE_NETWORK_MAPPING mapping");
   		  strBuilder.append(" WHERE lstat.lookup_type=? AND lcat.lookup_type=? AND");
   		  strBuilder.append(" lcat.lookup_code=masters.interface_category AND ");
   		  strBuilder.append("lstat.lookup_code=detail.status AND masters.interface_type_id=?");
   		  strBuilder.append("  AND masters.interface_type_id=detail.interface_type_id");
	   		if(pCategoryCode!=null && !pCategoryCode.equals("SUADM"))
	   		{
	   			strBuilder.append("  AND detail.interface_id=mapping.interface_id and mapping.network_code=? ");
	   		}
   		  String sqlLoad=strBuilder.toString();
 	      try(PreparedStatement pstmtSelect=pCon.prepareStatement(sqlLoad);)
 	      {
		  pstmtSelect.setString(1,PretupsI.STATUS_TYPE);
		  pstmtSelect.setString(2,PretupsI.INTERFACE_CATEGORY);
		  pstmtSelect.setString(3,pInterfaceCategory);
		  if(pCategoryCode!=null && !pCategoryCode.equals("SUADM"))
			  pstmtSelect.setString(4,pNetworkCode);  
		  try(ResultSet rs=pstmtSelect.executeQuery();)
		  {
		  LogFactory.printLog(methodName, "QUERY Executed = "+sqlLoad, log);
 		  InterfaceVO interfaceVO=null; 
		  int index=0;
		 
		  while(rs.next())
 			{
				interfaceVO=new InterfaceVO();
				interfaceVO.setInterfaceName(SqlParameterEncoder.encodeParams(rs.getString("interface_name")));
				interfaceVO.setInterfaceDescription(SqlParameterEncoder.encodeParams(rs.getString("interface_description")));
				interfaceVO.setExternalId(SqlParameterEncoder.encodeParams(rs.getString("external_id")));
				interfaceVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("statusDesc")));
				interfaceVO.setStatusCode(SqlParameterEncoder.encodeParams(rs.getString("status")));
				interfaceVO.setConcurrentConnection(rs.getInt("concurrent_connection"));
				interfaceVO.setInterfaceTypeId(SqlParameterEncoder.encodeParams(rs.getString("interface_type_id")));
				interfaceVO.setInterfaceCategory(SqlParameterEncoder.encodeParams(rs.getString("category")));
				interfaceVO.setInterfaceCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("interface_category")));
				interfaceVO.setInterfaceId(SqlParameterEncoder.encodeParams(rs.getString("interface_id")));
				interfaceVO.setSingleStateTransaction(SqlParameterEncoder.encodeParams(rs.getString("single_state_transaction")));
				interfaceVO.setRadioIndex(index);
				interfaceVO.setLanguage1Message(SqlParameterEncoder.encodeParams(rs.getString("message_language1")));
				interfaceVO.setLanguage2Message(SqlParameterEncoder.encodeParams(rs.getString("message_language2")));
				interfaceVO.setLastModified(rs.getTimestamp("modified_on").getTime());
				interfaceVO.setStatusType(SqlParameterEncoder.encodeParams(rs.getString("status_type")));
				interfaceVO.setValExpiryTime(rs.getLong("val_expiry_time"));
				interfaceVO.setTopUpExpiryTime(rs.getLong("topup_expiry_time"));
				interfaceVO.setNoOfNodes(SqlParameterEncoder.encodeParams(rs.getString("NUMBER_OF_NODES")));
				interfaceDetails.add(interfaceVO);
				index++;
 			}
 			
		}
 	      }
		}
		catch(SQLException sqe)
 		{
		    log.error(methodName," SQL  Exception "+sqe.getMessage());
		    log.errorTrace(methodName,sqe);
 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"InterfaceDAO[loadInterfaceDetails]","","","",sqlExpMsg+sqe.getMessage());
 			throw new BTSLBaseException(this, methodName, errsqlMesg,sqe);
 		}
 		catch(Exception e)
 		{
 		    log.error(methodName," Exception   "+e.getMessage());
 		   log.errorTrace(methodName,e);
 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"InterfaceDAO[loadInterfaceDetails]","","","","Exception:"+e.getMessage());
 			throw new BTSLBaseException(this, methodName, errGeneralMesg,e);
 			
 		}
 		finally
 		{
 			
 			LogFactory.printLog(methodName, " Exiting size="+interfaceDetails.size(), log); 
 		}
 		return interfaceDetails;
	}

    /**
     * Method addInterfaceDetails.
     * This method is used to add the Details of Interfaces in the Interfaces
     * Table
     * 
     * @param pCon
     *            Connection
     * @param pInterfaceVO
     *            InterfaceVO
     * @return int
     * @throws BTSLBaseException
     */

    public int addInterfaceDetails(Connection pCon, InterfaceVO pInterfaceVO) throws BTSLBaseException {
        int addCount = -1;
        final String methodName = "addInterfaceDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entering VO " + pInterfaceVO);
        }
         
        StringBuilder insertQueryBuff = new StringBuilder("INSERT INTO interfaces ");
        insertQueryBuff.append("(interface_id,external_id,interface_description,");
        insertQueryBuff.append("interface_type_id,status,clouser_date,message_language1,");
        insertQueryBuff.append("message_language2,concurrent_connection,single_state_transaction,");
		insertQueryBuff.append("created_on,created_by,modified_on,modified_by,status_type,val_expiry_time,topup_expiry_time,NUMBER_OF_NODES)");
		insertQueryBuff.append(" VALUES(?, ?, ?, ?, ?, ?, ? ,? ,? ,? ,? ,? ,? ,? ,?,?,?,?)");
        String insertQuery = insertQueryBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Insert Query= " + insertQuery);
        }
        try (PreparedStatement pstmtInsert = pCon.prepareStatement(insertQuery);){
            
            pstmtInsert.setString(1, pInterfaceVO.getInterfaceId());
            pstmtInsert.setString(2, pInterfaceVO.getExternalId());
            pstmtInsert.setString(3, pInterfaceVO.getInterfaceDescription());
            pstmtInsert.setString(4, pInterfaceVO.getInterfaceTypeId());
            pstmtInsert.setString(5, pInterfaceVO.getStatusCode());
            pstmtInsert.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(pInterfaceVO.getClosureDate()));
            pstmtInsert.setString(7, pInterfaceVO.getLanguage1Message());
            pstmtInsert.setString(8, pInterfaceVO.getLanguage2Message());
            pstmtInsert.setInt(9, pInterfaceVO.getConcurrentConnection());
            pstmtInsert.setString(10, pInterfaceVO.getSingleStateTransaction());
            pstmtInsert.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(pInterfaceVO.getCreatedOn()));
            pstmtInsert.setString(12, pInterfaceVO.getCreatedBy());
            pstmtInsert.setTimestamp(13, BTSLUtil.getTimestampFromUtilDate(pInterfaceVO.getCreatedOn()));
            pstmtInsert.setString(14, pInterfaceVO.getModifiedBy());
            if (BTSLUtil.isNullString(pInterfaceVO.getStatusType())) {
                pstmtInsert.setString(15, PretupsI.INTERFACE_STATUS_TYPE_MANUAL);
            } else {
                pstmtInsert.setString(15, pInterfaceVO.getStatusType());
            }
            pstmtInsert.setLong(16, pInterfaceVO.getValExpiryTime());
			pstmtInsert.setLong(17,pInterfaceVO.getTopUpExpiryTime());
			pstmtInsert.setString(18, pInterfaceVO.getNoOfNodes());
            addCount = pstmtInsert.executeUpdate();
        }

        catch (SQLException sqle) {
            log.error(methodName, "SQL Exception " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[addInterfaceDetails]", "", "", "", sqlExpMsg + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqle);
        }

        catch (Exception e) {
            log.error(methodName, "  Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[addInterfaceDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);
        }

        finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting addCount " + addCount);
            }
        }

        return addCount;
    }

    /**
     * Method modifyInterfaceDetails.
     * This method is used to Modify the Details of Interfaces in the Interfaces
     * Table
     * 
     * @param pCon
     *            Connection
     * @param pInterfaceVO
     *            InterfaceVO
     * @return int
     * @throws BTSLBaseException
     */

    public int modifyInterfaceDetails(Connection pCon, InterfaceVO pInterfaceVO) throws BTSLBaseException {

        final String methodName = "modifyInterfaceDetails";
        LogFactory.printLog(methodName, " Entering VO " + pInterfaceVO, log);
        int updateCount = -1;
         
        // check wether the record already updated or not
        boolean modified = this.recordModified(pCon, pInterfaceVO.getInterfaceId(), pInterfaceVO.getLastModified());
        // call the DAO method to Update the interface Detail
        try {

            StringBuilder updateQueryBuff = new StringBuilder("UPDATE interfaces SET");
    		updateQueryBuff.append(" external_id=?,interface_description=?,");
    		updateQueryBuff.append("interface_type_id=?,status=?,clouser_date=?,message_language1=?,");
    		updateQueryBuff.append("message_language2=?,concurrent_connection=?,single_state_transaction=?,");
    		updateQueryBuff.append("modified_on=?,modified_by=?,status_type=?, val_expiry_time=?, topup_expiry_time=?,NUMBER_OF_NODES=? WHERE interface_id=?");
    		String insertQuery = updateQueryBuff.toString();
    		 try(PreparedStatement pstmtUpdate = pCon.prepareStatement(insertQuery);)
    		 {
    		pstmtUpdate.setString(1,pInterfaceVO.getExternalId());
			pstmtUpdate.setString(2,pInterfaceVO.getInterfaceDescription());
			pstmtUpdate.setString(3,pInterfaceVO.getInterfaceTypeId());
			pstmtUpdate.setString(4,pInterfaceVO.getStatusCode());
			pstmtUpdate.setTimestamp(5,BTSLUtil.getTimestampFromUtilDate(pInterfaceVO.getClosureDate()));
			pstmtUpdate.setString(6,pInterfaceVO.getLanguage1Message()); 
			pstmtUpdate.setString(7,pInterfaceVO.getLanguage2Message());
			pstmtUpdate.setInt(8,pInterfaceVO.getConcurrentConnection());
			pstmtUpdate.setString(9,pInterfaceVO.getSingleStateTransaction());
			pstmtUpdate.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(pInterfaceVO.getModifiedOn())); 
			pstmtUpdate.setString(11,pInterfaceVO.getModifiedBy());
			if(BTSLUtil.isNullString(pInterfaceVO.getStatusType()))
				pstmtUpdate.setString(12,PretupsI.INTERFACE_STATUS_TYPE_MANUAL);
			else
				pstmtUpdate.setString(12,pInterfaceVO.getStatusType());
			pstmtUpdate.setLong(13,pInterfaceVO.getValExpiryTime());
			pstmtUpdate.setLong(14,pInterfaceVO.getTopUpExpiryTime());
			pstmtUpdate.setString(15, pInterfaceVO.getNoOfNodes());
			pstmtUpdate.setString(16,pInterfaceVO.getInterfaceId());
			if (modified) 
               throw new BTSLBaseException(this,methodName,"error.modify.true");
    		updateCount = pstmtUpdate.executeUpdate();
    		LogFactory.printLog(methodName, "Query Executed _= "+ insertQuery, log);
		}
        }
		catch (BTSLBaseException be)
		{
          throw be;
        } 
		
		catch (SQLException sqle) 
		{
		    log.error(methodName," SQLException -" + sqle.getMessage());
		    log.errorTrace(methodName,sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"InterfaceDAO[modifyInterfaceDetails]","","","",sqlExpMsg+sqle.getMessage());
			throw new BTSLBaseException(this,methodName,errsqlMesg,sqle);
		}
		
		catch (Exception e) 
		{
		    log.error(methodName," Exception -" + e.getMessage());
		    log.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"InterfaceDAO[modifyInterfaceDetails]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,errGeneralMesg,e);
		}

        finally {
            
            LogFactory.printLog(methodName, "Exiting updateCount " + updateCount, log);
            
        }

        return updateCount;
    }

   
    /**
     * This method is used to check whether the record in the database is
     * modified or not
     * @param con
     * @param interfaceId
     * @param oldLastModified
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean recordModified(Connection con, String interfaceId, long oldLastModified) throws BTSLBaseException {
        final String methodName = "recordModified";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: interfaceId= " + interfaceId + "oldLastModified= " + oldLastModified);
        }
         
        boolean modified = false;
        String sqlRecordModified = "SELECT modified_on FROM interfaces WHERE interface_id=?";
        Timestamp newLastModified = null;

        if ((oldLastModified) == 0) {
            return false;
        }
        try (PreparedStatement pstmt = con.prepareStatement(sqlRecordModified);){
            log.info(methodName, "QUERY: sqlselect= " + sqlRecordModified);
            
            pstmt.setString(1, interfaceId);
            try( ResultSet rs = pstmt.executeQuery();)
            {
            modified = recordsModified(oldLastModified, methodName, rs,
					modified, newLastModified);

        }
        }

        catch (SQLException sqle) {
            log.error(methodName, "SQLException   " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[recordModified]", "", "", "", sqlExpMsg + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqle);
        }

        catch (Exception e) {
            log.error(methodName, "Exception   " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[recordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);
        }

        finally {
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exititng modified=" + modified);
            }
        }
        return modified;
    }

	private boolean recordsModified(long oldLastModified,
			final String methodName, ResultSet rs, boolean modified,
			Timestamp newLastModified) throws SQLException {
		if (rs.next()) {
		    newLastModified = rs.getTimestamp("modified_on");
		}

		if (log.isDebugEnabled()) {
		    log.debug(methodName, " old=" + oldLastModified);
		    if (newLastModified != null) {
		        log.debug(methodName, " new=" + newLastModified.getTime());
		    } else {
		        log.debug(methodName, " new=null");
		    }
		}

		if (newLastModified != null && newLastModified.getTime() != oldLastModified) {
		    modified = true;
		}
		return modified;
	}

    /**
     * This method is used before adding/modifying the record in the interfaces
     * table
     * it will check for the uniqueness of the interface_description column
     * if the interface_description the user enterd exists in the database
     * the method return true and record will not inserted in the interfaces
     * table.
     * 
     * @return boolean
     * @param pCon
     *            Connection
     * @param pInterfaceDesc
     *            String
     * @param pInterfaceId
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExists(Connection pCon, String pInterfaceDesc, String pInterfaceId) throws BTSLBaseException {

        final String methodName = "isExists";
        LogFactory.printLog(methodName, "Entered params p_interfaceDesc::" + pInterfaceDesc + " p_interfaceId=" + pInterfaceId, log);
         
         
        boolean found = false;
        StringBuilder sqlBuff = new StringBuilder("SELECT interface_description FROM");
        sqlBuff.append(" interfaces WHERE UPPER(interface_description)=UPPER(?)");

        if ((pInterfaceId != null) && (!("null".equals(pInterfaceId)))) {
            sqlBuff.append(" AND interface_id !=?");
        }

        String selectQuery = sqlBuff.toString();
        LogFactory.printLog(methodName, "Select Query: :" + selectQuery, log);

        try(PreparedStatement pstmtSelect =pCon.prepareStatement(selectQuery);) {
            
            pstmtSelect.setString(1, pInterfaceDesc);
            if ((!(BTSLUtil.isNullString(pInterfaceId))) && (!("null".equals(pInterfaceId)))) {
                pstmtSelect.setString(2, pInterfaceId);
            }
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {

            LogFactory.printLog(methodName, "Query Executed ::" + selectQuery, log);
            if (rs.next()) {
                found = true;
            }
        }
        }

        catch (SQLException sqle) {
            log.error(methodName, "  SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExists]", "", "", "", sqlExpMsg + sqle.getMessage());
            throw new BTSLBaseException(this, "isExists()", errsqlMesg,sqle);
        }

        catch (Exception e) {
            log.error(methodName, " Exception  " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExists]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);
        }

        finally {
            
            LogFactory.printLog(methodName, "Exiting isExists found  =" + found, log);
        }

        return found;
    }

    /**
     * This method is used before adding the record in the interfaces table
     * it will check for the uniqueness of the external_id column
     * if the external_id the user enterd exists in the database
     * the method return true and record will not inserted in the interfaces
     * table.
     * 
     * @return boolean
     * @param pCon
     *            Connection
     * @param pExternalID
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsExternalId(Connection pCon, String pExternalID) throws BTSLBaseException {

        final String methodName = "isExistsExternalId";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered params p_externalID=" + pExternalID);
        }

         
         
        boolean found = false;
        String sqlBuff = "SELECT 1 FROM interfaces WHERE UPPER(external_id)=UPPER(?) AND status<>'N'";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query:" + sqlBuff);
        }

        try(PreparedStatement pstmtSelect = pCon.prepareStatement(sqlBuff);) {
           
            pstmtSelect.setString(1, pExternalID);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query Executed::" + sqlBuff);
            }

            if (rs.next()) {
                found = true;
            }
        } 
        }catch (SQLException sqle) {
            log.error(methodName, " SQLException   " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExistsExternalId]", "", "", "", sqlExpMsg + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqle);
        } catch (Exception e) {
            log.error(methodName, "  Exception  " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExistsExternalId]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);
        }

        finally {
        	 
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting isExistsExternalId found=" + found);
            }
        }
        return found;
    }

    /**
     * This method is used before modifying the record in the interfaces table
     * it will check for the uniqueness of the external_id column
     * if the external_id the user enterd exists in the database
     * the method return true and record will not inserted in the interfaces
     * table.
     * 
     * @return boolean
     * @param pCon
     *            Connection
     * @param pExternalID
     *            String
     * @param pInterfaceID
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsExternalIdModify(Connection pCon, String pExternalID, String pInterfaceID) throws BTSLBaseException {

        final String methodName = "isExistsExternalIdModify";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered params p_externalID=" + pExternalID, "p_interfaceID=" + pInterfaceID);
        }

         
         
        boolean found = false;
        String sqlBuff = "SELECT 1 FROM interfaces WHERE UPPER(external_id)=UPPER(?) AND interface_id!=? AND status<>'N'";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Select Query " + sqlBuff);
        }

        try(PreparedStatement pstmtSelect = pCon.prepareStatement(sqlBuff);) {
            
            pstmtSelect.setString(1, pExternalID);
            pstmtSelect.setString(2, pInterfaceID);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query Executed::" + sqlBuff);
            }

            if (rs.next()) {
                found = true;
            }
        } 
        }catch (SQLException sqle) {
            log.error(methodName, "SQLException_ " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExistsExternalIdModify]", "", "", "", sqlExpMsg + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqle);
        } catch (Exception e) {
            log.error(methodName, "Exception_ " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExistsExternalIdModify]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);
        }

        finally {
        	 
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting isExistsExternalIdModify found=" + found);
            }
        }
        return found;
    }

    /**
     * Method loadInterfaceDetails.
     * This method is used to load interface details into ArrayList
     * If there is any error then throws the SQLException or Exception
     * 
     * @param pCon
     *            Connection
     * @param pNetworkCode
     *            String
     * @param pLookupType
     *            String
     * @param pInterfaceID
     *            String
     *            (In add mode interfaceId is null but in edit mode it is not
     *            null so we load the interface list
     *            on the basis of the passed interfaceId, in add mode load those
     *            interfaces that are not already
     *            inserted by the user in interface_network_mapping table)
     * 
     * @return interfaceDetails ArrayList
     */

    public ArrayList<ListValueVO> loadInterfaceList(Connection pCon, String pNetworkCode, String pLookupType, String pInterfaceID) throws BTSLBaseException {
        final String methodName = "loadInterfaceList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_networkCode=" + pNetworkCode + " p_lookupType=" + pLookupType);
        }
        StringBuilder strBuilder = null;
        
        ArrayList<ListValueVO> interfaceDetails = new ArrayList<ListValueVO>();
        try {
            if (BTSLUtil.isNullString(pInterfaceID)) {
                strBuilder = new StringBuilder("SELECT i.interface_description,");
                strBuilder.append("i.interface_id,it.interface_category,it.INTERFACE_TYPE_ID ");
                strBuilder.append(" FROM interfaces i,interface_types it,lookups l ");
                strBuilder.append(" WHERE i.status!='N' AND i.interface_id not in (select interface_id from interface_network_mapping where network_code = ?) ");
                strBuilder.append(" AND i.interface_type_id = it.interface_type_id AND it.interface_type_id = l.lookup_code");
                strBuilder.append(" AND l.lookup_type = ? ");
                strBuilder.append(" ORDER BY it.interface_name");
            } else {
                strBuilder = new StringBuilder("SELECT i.interface_description,it.INTERFACE_TYPE_ID,");
                strBuilder.append("i.interface_id,it.interface_category ");
                strBuilder.append(" FROM interfaces i,interface_types it,lookups l ");
                strBuilder.append(" WHERE i.status!='N' AND i.interface_id = ? ");
                strBuilder.append(" AND i.interface_type_id = it.interface_type_id AND it.interface_type_id = l.lookup_code");
                strBuilder.append(" AND l.lookup_type = ? ");
                strBuilder.append(" ORDER BY it.interface_name");
            }

            String sqlLoad = strBuilder.toString();
            try(PreparedStatement pstmtSelect = pCon.prepareStatement(sqlLoad);)
            {
            if (BTSLUtil.isNullString(pInterfaceID)) {
                pstmtSelect.setString(1, pNetworkCode);
                pstmtSelect.setString(2, pLookupType);
            } else {
                pstmtSelect.setString(1, pInterfaceID);
                pstmtSelect.setString(2, pLookupType);
            }

            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            LogFactory.printLog(methodName, "QUERY Executed : " + sqlLoad, log);

            while (rs.next()) {
                interfaceDetails.add(new ListValueVO(rs.getString("interface_description"), rs.getString("INTERFACE_TYPE_ID") + ":" + rs.getString("interface_id")));
            }
            }
        }
        }
        catch (SQLException sqe) {
            log.error(methodName, " SQL Exception- " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceList]", "", "", "", sqlExpMsg + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqe);
        } catch (Exception e) {
            log.error(methodName, " _Exception_" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);

        } finally {
        	 
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting size=" + interfaceDetails.size());
            }
        }
        return interfaceDetails;
    }

    /**
     * Method deleteInterface.
     * This method is used to Soft delete the interface according to the
     * interface id
     * 
     * @param pCon
     *            Connection
     * @param pInterfaceVO
     *            InterfaceVO
     * @return int
     * @throws BTSLBaseException
     */

    public int deleteInterface(Connection pCon, InterfaceVO pInterfaceVO) throws BTSLBaseException {

        final String methodName = "deleteInterface";
        LogFactory.printLog(methodName, "Entering VO " + pInterfaceVO, log);

        int deleteCount = -1;
        
        // check wether the record already updated or not
        boolean modified = this.recordModified(pCon, pInterfaceVO.getInterfaceId(), pInterfaceVO.getLastModified());
        // call the DAO method to Update the interface Detail
        try {

            StringBuilder updateQueryBuff = new StringBuilder("UPDATE interfaces SET status='N',modified_by=?,modified_on=? WHERE interface_id=?");
            String insertQuery = updateQueryBuff.toString();
            try(PreparedStatement pstmtUpdate = pCon.prepareStatement(insertQuery);)
            {
            pstmtUpdate.setString(1, pInterfaceVO.getModifiedBy());
            pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(pInterfaceVO.getModifiedOn()));
            pstmtUpdate.setString(3, pInterfaceVO.getInterfaceId());

            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            deleteCount = pstmtUpdate.executeUpdate();
            LogFactory.printLog(methodName, "QueryExecuted= " + insertQuery, log);
        }
        }catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        }

        catch (SQLException sqle) {
            log.error(methodName, " SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[deleteInterface]", "", "", "", sqlExpMsg + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqle);
        }

        catch (Exception e) {
            log.error(methodName, " Exception :-" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[deleteInterface]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);
        }

        finally {
        	
            if (log.isDebugEnabled()) {
                log.debug("modifyInterfaceDetails", "Exiting deleteCount " + deleteCount);
            }
        }

        return deleteCount;
    }

    /**
     * Method isInterfaceExistsInInterfaceNwkPrefix
     * This method is used before soft deleting the interface
     * it will check for the interface id in the INTF_NTWRK_PRFX_MAPPING table
     * if the interface_id exists in the table it will return true
     * 
     * @return boolean
     * @param pCon
     *            Connection
     * @param pInterfaceId
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isInterfaceExistsInInterfaceNwkPrefix(Connection pCon, String pInterfaceId) throws BTSLBaseException {

        final String methodName = "isInterfaceExistsInInterfaceNwkPrefix";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered  p_interfaceId=" + pInterfaceId);
        }

         
        
        boolean found = false;
        StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM INTF_NTWRK_PRFX_MAPPING where interface_id=?");
        String selectQuery = sqlBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Select Query=" + selectQuery);
        }

        try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);) {
           
            pstmtSelect.setString(1, pInterfaceId);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                found = true;
            }
        }
        }

        catch (SQLException sqle) {
            log.error(methodName, "SQLException :" + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExists]", "", "", "", sqlExpMsg + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqle);
        }

        catch (Exception e) {
            log.error(methodName, "Exception:: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExists]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);
        }

        finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting isInterfaceExistsInInterfaceNwkPrefix found=" + found);
            }
        }

        return found;
    }

    /**
     * Method isInterfaceExistsInInterfaceNwkMapping
     * This method is used before soft deleting the interface
     * it will check for the interface id in the INTERFACE_NETWORK_MAPPING table
     * if the interface_id exists in the table it will return true
     * 
     * @return boolean
     * @param pCon
     *            Connection
     * @param pInterfaceId
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isInterfaceExistsInInterfaceNwkMapping(Connection pCon, String pInterfaceId) throws BTSLBaseException {

        if (log.isDebugEnabled()) {
            log.debug("isInterfaceExistsInInterfaceNwkPrefix", "Entered p_interfaceId=" + pInterfaceId);
        }

       
        
        boolean found = false;
        StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM INTERFACE_NETWORK_MAPPING where interface_id=?");
        String selectQuery = sqlBuff.toString();
        final String methodName = "isInterfaceExistsInInterfaceNwkMapping";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query::" + selectQuery);
        }

        try( PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);) {
            
            pstmtSelect.setString(1, pInterfaceId);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                found = true;
            }
        }
        }

        catch (SQLException sqle) {
            log.error(methodName, "SQLException__ " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExists]", "", "", "", sqlExpMsg + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqle);
        }

        catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isExists]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);
        }

        finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting isExists found=" + found);
            }
        }

        return found;
    }

    /**
     * This method will load all interfaces details.
     * 
     * @param pCon
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     * @author Dhiraj Tiwari
     */

    public ArrayList<InterfaceVO> loadInterfaceDetailsList(Connection pCon) throws BTSLBaseException {
        final String methodName = "loadInterfaceDetailsList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        StringBuilder strBuilder = null;
        
        InterfaceVO interfaceVO = null;
        ArrayList<InterfaceVO> loadInterfaceDetailsList = new ArrayList<InterfaceVO>();
        try {
            strBuilder = new StringBuilder("SELECT interface_id,external_id,");
            strBuilder.append("interface_description,interface_type_id,status,");
            strBuilder.append("clouser_date,message_language1,message_language2,");
            strBuilder.append("concurrent_connection,single_state_transaction,");
            strBuilder.append("created_on,created_by,modified_on,modified_by,status_type,val_expiry_time,topup_expiry_time FROM interfaces WHERE status <> 'N'");

            String sqlLoad = strBuilder.toString();
            try( PreparedStatement pstmtSelect = pCon.prepareStatement(sqlLoad);ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY Executed= " + sqlLoad);
            }

            while (rs.next()) {
                interfaceVO = new InterfaceVO();
                interfaceVO.setInterfaceId(rs.getString("interface_id"));
                interfaceVO.setExternalId(rs.getString("external_id"));
                interfaceVO.setInterfaceDescription(rs.getString("interface_description"));
                interfaceVO.setInterfaceTypeId(rs.getString("interface_type_id"));
                interfaceVO.setStatusCode(rs.getString("status"));
                interfaceVO.setClosureDate(rs.getDate("clouser_date"));
                interfaceVO.setLanguage1Message(rs.getString("message_language1"));
                interfaceVO.setLanguage2Message(rs.getString("message_language2"));
                interfaceVO.setConcurrentConnection(rs.getInt("concurrent_connection"));
                interfaceVO.setSingleStateTransaction(rs.getString("single_state_transaction"));
                interfaceVO.setCreatedOn(rs.getDate("created_on"));
                interfaceVO.setCreatedBy(rs.getString("created_by"));
                interfaceVO.setModifiedOn(rs.getDate("modified_on"));
                interfaceVO.setModifiedBy(rs.getString("modified_by"));
                interfaceVO.setStatusType(rs.getString("status_type"));
                interfaceVO.setValExpiryTime(rs.getLong("val_expiry_time"));
                interfaceVO.setTopUpExpiryTime(rs.getLong("topup_expiry_time"));
                loadInterfaceDetailsList.add(interfaceVO);
            }
        }
        }catch (SQLException sqe) {
            log.error(methodName, " SQL Exception " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceDetailsList]", "", "", "", sqlExpMsg + sqe.getMessage());
            throw new BTSLBaseException("ResumeSuspendProcess", methodName, errsqlMesg,sqe);
        } catch (Exception e) {
            log.error(methodName, " Exception-: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceDetailsList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ResumeSuspendProcess", methodName, errGeneralMesg,e);

        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting , no. of Interfaces = " + loadInterfaceDetailsList.size());
            }
        }
        return loadInterfaceDetailsList;
    }

    /**
     * This method loads the list of list value VO for interface
     * 
     * @param pCon
     * @return arraylist
     * @throws BTSLBaseException
     */
    public ArrayList<ListValueVO> loadInterfacesTypeValueVOList(Connection pCon) throws BTSLBaseException {
        final String methodName = "loadInterfacesTypeValueVOList";
        if (log.isDebugEnabled()) {
        	log.debug(methodName, "Entering");
           }
        ArrayList<ListValueVO> interfaceList = new ArrayList<ListValueVO>();
         
        StringBuilder selectQueryBuff = new StringBuilder("SELECT interface_type_id, interface_name, interface_category FROM interface_types ORDER BY interface_name");
        String selectQuery = selectQueryBuff.toString();
        LogFactory.printLog(methodName, "Select Query " + selectQuery, log);
        try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);ResultSet rs = pstmtSelect.executeQuery();) {
           
            
            while (rs.next()) {
                interfaceList.add(new ListValueVO(rs.getString("interface_name"), rs.getString("interface_type_id")));
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query Executed " + selectQuery);
            }
        } catch (SQLException sqle) {
            log.error(methodName, "SQL Exception_: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfacesTypeValueVOList]", "", "", "", "SQLException:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqle);
        } catch (Exception e) {
            log.error(methodName, " :Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfacesTypeValueVOList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting Size " + interfaceList.size());
            }
        }
        return interfaceList;
    }

    /**
     * This method loads the list of interface based on the interface category
     * type ie PRE, POST etc.
     * 
     * @param pCon
     * @return arraylist
     * @throws BTSLBaseException
     */
    public ArrayList<ListValueVO> loadInterfacesTypeList(Connection pCon) throws BTSLBaseException {
        final String methodName = "loadInterfacesTypeList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entering");
        }

        ArrayList<ListValueVO> interfaceList = new ArrayList<ListValueVO>();
         

        StringBuilder selectQueryBuff = new StringBuilder("SELECT interface_type_id, interface_name, interface_category FROM interface_types ORDER BY interface_name");
        String selectQuery = selectQueryBuff.toString();

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query " + selectQuery);
        }
        try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);ResultSet rs = pstmtSelect.executeQuery();) {
           
            
            while (rs.next()) {
                interfaceList.add(new ListValueVO(rs.getString("interface_name"), rs.getString("interface_category") + ":" + rs.getString("interface_type_id")));
            }

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query Executed " + selectQuery);
            }
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfacesTypeList]", "", "", "", "SQLException:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqle);
        }

        catch (Exception e) {
            log.error(methodName, ":Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfacesTypeList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);
        }

        finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting Size " + interfaceList.size());
            }
        }
        return interfaceList;
    }

    /**
     * Method loadInterfaceByID.
     * This method is used to load interface details corresponding to
     * interfaceID
     * If there is any error then throws the SQLException or Exception
     * 
     * @param p_con
     *            Connection
     * @param p_interfaceID
     *            String
     * @return interfaceDetails ArrayList
     */

    public HashMap<String, InterfaceVO> loadInterfaceByID() throws BTSLBaseException {
        final String methodName = "loadInterfaceByID";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        StringBuilder strBuilder = null;
         
         
        HashMap<String, InterfaceVO> interfaceDetail = new HashMap<String, InterfaceVO>();
        InterfaceVO interfaceVO = null;
        Connection con = null;
        try {
            strBuilder = new StringBuilder("SELECT i.interface_id,i.external_id,i.interface_description,i.interface_type_id,");
            strBuilder.append("i.status,i.clouser_date,i.message_language1,i.message_language2,i.concurrent_connection,");
            strBuilder.append("i.single_state_transaction,");
            strBuilder.append("i.status_type,i.status,i.val_expiry_time,i.topup_expiry_time,it.interface_type_id,");
            strBuilder.append("it.interface_name,it.interface_category,it.handler_class,it.underprocess_msg_reqd ");
            strBuilder.append(" FROM interfaces i,interface_types it WHERE it.interface_type_id=i.interface_type_id");

            String sqlLoad = strBuilder.toString();
            con = OracleUtil.getSingleConnection();
            try(PreparedStatement pstmtSelect = con.prepareStatement(sqlLoad);ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (log.isDebugEnabled()) {
                log.debug("loadInterfaceList", "QUERY Executed= " + sqlLoad);
            }
            while (rs.next()) {
                interfaceVO = new InterfaceVO();
                interfaceVO.setInterfaceId(rs.getString("interface_id"));
                interfaceVO.setInterfaceTypeId(rs.getString("interface_type_id"));
                interfaceVO.setConcurrentConnection(rs.getInt("concurrent_connection"));
                interfaceVO.setInterfaceName(rs.getString("interface_name"));
                interfaceVO.setInterfaceCategory(rs.getString("interface_category"));
                interfaceVO.setHandlerClass(rs.getString("handler_class"));
                interfaceVO.setStatusType(rs.getString("status_type"));
                interfaceVO.setStatusCode(rs.getString("status"));
                interfaceVO.setValExpiryTime(rs.getLong("val_expiry_time"));
                interfaceVO.setTopUpExpiryTime(rs.getLong("topup_expiry_time"));
                interfaceVO.setLanguage1Message(rs.getString("message_language1"));
                interfaceVO.setLanguage2Message(rs.getString("message_language2"));
                interfaceDetail.put(interfaceVO.getInterfaceId(), interfaceVO);
            }
        } 
        }catch (SQLException sqe) {
            log.error(methodName, " SQL Exception " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceByID]", "", "", "", sqlExpMsg + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqe);
        } catch (Exception e) {
            log.error(methodName, " :Exception" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceByID]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);

        } finally {
        	
            OracleUtil.closeQuietly(con);
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting interfaceDetail size=" + interfaceDetail.size());
            }
        }
        return interfaceDetail;
    }

    /**
     * Method isInterfaceExistsInIATMapping
     * This method is used before soft deleting the interface
     * it will check for the interface id in the IAT_NW_SERVICE_MAPPING table
     * if the interface_id exists in the table it will return true
     * 
     * @return boolean
     * @param pCon
     *            Connection
     * @param pInterfaceId
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isInterfaceExistsInIATMapping(Connection pCon, String pInterfaceId) throws BTSLBaseException {

        final String methodName = "isInterfaceExistsInIATMapping";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_interfaceId=" + pInterfaceId);
        }

        
        
        boolean found = false;
        StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM IAT_NW_SERVICE_MAPPING where IAT_CODE=?");
        String selectQuery = sqlBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query::" + selectQuery);
        }

        try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);) {
            
            pstmtSelect.setString(1, pInterfaceId);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                found = true;
            }
        }
        }

        catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isInterfaceExistsInIATMapping]", "", "", "", sqlExpMsg + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqle);
        }

        catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "isInterfaceExistsInIATMapping[isExists]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);
        }

        finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting isExists found=" + found);
            }
        }

        return found;
    }

    /**
     * Method addNode
     * This method is used to insert the IN Node Details(IP,Port,URI,Status) in
     * the database
     * 
     * @author Akanksha
     * @return boolean
     * @param pCon
     *            Connection
     * @param pNodeList
     *            ArrayList
     * @exception BTSLBaseException
     * @return count
     */
    public HashMap<String, String> addModifyNode(Connection pCon, ArrayList<InterfaceNodeDetailsVO> pNodeList, String pInterfaceId, String user, String networkCode) throws BTSLBaseException {
    	final String methodName = "addModifyNode";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered", "p_batchList=" + pNodeList.size());
        }
        PreparedStatement psmt = null;
        PreparedStatement psmtSelect = null;
        PreparedStatement psmtUpdate = null;
        PreparedStatement psmtStatusUpdate = null;
        int addCount = 0;
        ResultSet rs = null;
        InterfaceNodeDetailsVO interfaceNodesVO = null;
        StringBuilder message = new StringBuilder();
        HashMap<String, String> detailMap = null;
        try {
            StringBuilder strBuilder = new StringBuilder("INSERT INTO interface_node_details (interface_id,ip,port, uri, status,created_on,created_by,modified_on,modified_by,sequence_id)");
            strBuilder.append(" VALUES (?,?,?,?,?,?,?,?,?,?)");
            StringBuilder selectQuery = new StringBuilder("Select sequence_id,status from interface_node_details where sequence_id=? ");

            StringBuilder updateQuery = new StringBuilder(" update interface_node_details set");
            updateQuery.append(" ip=?,port=?, uri=?, status=?,modified_on=?,modified_by=? where sequence_id=?");

            StringBuilder updateStausQuery = new StringBuilder(" update interface_node_details set");
            updateStausQuery.append(" ip=?,port=?, uri=?, status=?,suspended_On=?,suspended_By=? where sequence_id=?");
            detailMap = new HashMap<String, String>();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "addModifyNode()Query=" + strBuilder.toString());
            }
            psmt = pCon.prepareStatement(strBuilder.toString());
            psmtSelect = pCon.prepareStatement(selectQuery.toString());
            psmtStatusUpdate = pCon.prepareStatement(updateStausQuery.toString());
            psmtUpdate = pCon.prepareStatement(updateQuery.toString());
            for (int i = 0, j = pNodeList.size(); i < j; i++) {
                addCount = 0;
                interfaceNodesVO = pNodeList.get(i);
                psmtSelect.setString(1, interfaceNodesVO.getNodeName());
                rs = psmtSelect.executeQuery();
                if (rs.next()) {
                    String nodePreviousStatus = rs.getString("status");
                    if (nodePreviousStatus.equals(PretupsI.YES) && interfaceNodesVO.getNodeStatus().equalsIgnoreCase(PretupsI.SUSPEND)) {
                        
                        psmtStatusUpdate.setString(1, interfaceNodesVO.getIp());
                        psmtStatusUpdate.setString(2, interfaceNodesVO.getPort());
                        psmtStatusUpdate.setString(3, interfaceNodesVO.getUri());
                        psmtStatusUpdate.setString(4, interfaceNodesVO.getNodeStatus());
                        psmtStatusUpdate.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(new Date()));
                        psmtStatusUpdate.setString(6, user);
                        psmtStatusUpdate.setString(7, interfaceNodesVO.getNodeName());
                        addCount += psmtStatusUpdate.executeUpdate();
                        psmtStatusUpdate.clearParameters();
                        message.append(" node suspended for IP:" + interfaceNodesVO.getIp() + " port : " + interfaceNodesVO.getPort());
                    } else if (interfaceNodesVO.getNodeStatus().equalsIgnoreCase(PretupsI.YES) || (interfaceNodesVO.getNodeStatus().equalsIgnoreCase(PretupsI.SUSPEND) && nodePreviousStatus.equals(PretupsI.SUSPEND))) {
                        psmtUpdate.clearParameters();
                        psmtUpdate.setString(1, interfaceNodesVO.getIp());
                        psmtUpdate.setString(2, interfaceNodesVO.getPort());
                        psmtUpdate.setString(3, interfaceNodesVO.getUri());
                        psmtUpdate.setString(4, interfaceNodesVO.getNodeStatus());
                        psmtUpdate.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(new Date()));
                        psmtUpdate.setString(6, user);
                        psmtUpdate.setString(7, interfaceNodesVO.getNodeName());
                        addCount += psmtUpdate.executeUpdate();
                        psmtUpdate.clearParameters();
                        message.append(" node modified for IP:" + interfaceNodesVO.getIp() + " port: " + interfaceNodesVO.getPort());
                    }

                } else {
                    psmt.clearParameters();
                    psmt.setString(1, pInterfaceId);
                    psmt.setString(2, interfaceNodesVO.getIp());
                    psmt.setString(3, interfaceNodesVO.getPort());
                    psmt.setString(4, interfaceNodesVO.getUri());
                    psmt.setString(5, interfaceNodesVO.getNodeStatus());
                    psmt.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(new Date()));
                    psmt.setString(7, user);
                    psmt.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(new Date()));
                    psmt.setString(9, user);
                    String nodeidTpe = PretupsI.INTERFACE_NODE_TYPE_ID;
                    StringBuilder uniqueInterfaceId = new StringBuilder();
                    long nodeId = IDGenerator.getNextID(nodeidTpe, PretupsI.ALL);
                    int zeroes = 10 - (nodeidTpe.length() + Long.toString(nodeId).length());
                    for (int count = 0; count < zeroes; count++) {
                        uniqueInterfaceId.append(0);
                    }
                    uniqueInterfaceId.insert(0, nodeidTpe);
                    uniqueInterfaceId.append(Long.toString(nodeId));
                    psmt.setString(10, uniqueInterfaceId.toString());
                    addCount += psmt.executeUpdate();
                    message.append(" node added with IP:" + interfaceNodesVO.getIp() + " port: " + interfaceNodesVO.getPort() + " URI: " + interfaceNodesVO);
                    psmt.clearParameters();
                }

                psmtSelect.clearParameters();

            }
            detailMap.put("arg1", String.valueOf(addCount));
            detailMap.put("arg2", message.toString());
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (SQLException sqe) {
            log.error(" addModifyNode() ", "  SQL Exception =" + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[addModifyNode]", "", "", "", "SQL Exception: Error in adding batch info" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,sqe);
        } catch (Exception ex) {
            log.error(" addModifyNode() ", "  Exception =" + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[addModifyNode]", "", "", "", "Exception: Error in adding batch info" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,ex);
        } finally {
            try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            try{
                if (psmt!= null){
                	psmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            try{
                if (psmtUpdate!= null){
                	psmtUpdate.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            try{
                if (psmtStatusUpdate!= null){
                	psmtStatusUpdate.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            try{
                if (psmtSelect!= null){
                	psmtSelect.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            if (log.isDebugEnabled()) {
                log.debug("addModifyNode() Successful ", "Exiting:  addCount=" + addCount);
            }
            
        }
        return detailMap;
    }

    /**
     * Method loadNodeDetailsInCache.
     * This method is used to load interface type id from interface_types table
     * in the File Cache
     * If there is any error then throws the SQLException
     * 
     * @author akanksha
     * @param p_con
     *            Connection
     * @param p_interfaceId
     *            String
     * @return interfaceNodeList ArrayList
     * @throws BTSLBaseException
     */

    public HashMap<String, ArrayList<InterfaceVO>> loadNodeDetailsInCache() throws BTSLBaseException {
        final String methodName = "loadNodeDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_interfaceCategory");
        }

        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtIntSelect = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ArrayList<InterfaceVO> interfaceNodeList = new ArrayList<InterfaceVO>();
        HashMap<String, ArrayList<InterfaceVO>> nodesMap = new HashMap<String, ArrayList<InterfaceVO>>();
        StringBuilder interFaceBuff = new StringBuilder("SELECT interface_id  ");
        interFaceBuff.append(" from interfaces WHERE status=?");

        StringBuilder strBuilder = new StringBuilder("SELECT ip,port,uri,status ");
        strBuilder.append(" from INTERFACE_NODE_DETAILS WHERE interface_id=? and status=? order by sequence_id");
        String sqlSelect = strBuilder.toString();
        InterfaceVO interfaceVO = null;
        LogFactory.printLog(methodName, "Select Query:= " + sqlSelect, log);
        Connection pCon = null;
        try {
            pCon = OracleUtil.getSingleConnection();
            pstmtIntSelect = pCon.prepareStatement(interFaceBuff.toString());
            pstmtIntSelect.setString(1, PretupsI.YES);
            rs = pstmtIntSelect.executeQuery();
            pstmtSelect = pCon.prepareStatement(sqlSelect);
            while (rs.next()) {
                String interfaceId = rs.getString(1);
                
                pstmtSelect.setString(1, interfaceId);
                pstmtSelect.setString(2, PretupsI.YES);
                rs1 = pstmtSelect.executeQuery();
                interfaceNodeList = new ArrayList<InterfaceVO>();
                while (rs1.next()) {
                    interfaceVO = new InterfaceVO();
                    interfaceVO.setIp(rs1.getString("ip"));
                    interfaceVO.setPort(rs1.getString("port"));
                    interfaceVO.setUri(rs1.getString("uri"));
                    interfaceNodeList.add(interfaceVO);
                }
                if (!interfaceNodeList.isEmpty()) {
                    nodesMap.put(interfaceId, interfaceNodeList);
                }

                pstmtSelect.clearParameters();
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQL Exception_-" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadNodeDetails]", "", "", "", sqlExpMsg + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqe);
        } catch (Exception e) {
            log.error(methodName, " Exception_:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadNodeDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);

        } finally {
            try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            try{
                if (rs1!= null){
                	rs1.close();
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
            	  log.error("An error occurred closing result set.", e);
              }
            try{
                if (pstmtIntSelect!= null){
                	pstmtIntSelect.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            OracleUtil.closeQuietly(pCon);
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting size=" + nodesMap.size());
            }
        }

        return nodesMap;
    }

    /**
     * Method: UriRequired
     * This method checks whether URI field should be mandatory or not dependind
     * 
     * @author akanksha
     * @param pCon
     *            java.sql.Connection
     * @param interfaceCatCode
     *            String
     * @return req String
     * @throws BTSLBaseException
     */
    public HashMap<String, String> getRequiredDetails(Connection pCon, String interfaceCatCode) throws BTSLBaseException {
        final String methodName = "recordModified";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_productID=" + interfaceCatCode);
        }

         
        HashMap<String, String> map = new HashMap<String, String>();
         
        String req = null;
        String sqlSelect = null;
        sqlSelect = " SELECT uri_req,max_nodes FROM interface_types WHERE interface_type_id = ?";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, interfaceCatCode);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                map.put("uri_req", SqlParameterEncoder.encodeParams(rs.getString("uri_req")));
                map.put("max_nodes", SqlParameterEncoder.encodeParams(rs.getString("max_nodes")));

            }

        }
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[UriRequired]", "", "", "", sqlExpMsg + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqe);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[UriRequired]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,ex);
        } finally {
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: modified=" + req);
            }
        }
        return map;
    }

    /**
     * Method loadNodeDetails.
     * This method is used to load node details at the time for modification of
     * Nodes
     * If there is any error then throws the SQLException
     * 
     * @author akanksha
     * @param pCon
     *            Connection
     * @param pInterfaceId
     *            String
     * @return interfaceNodeList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList<InterfaceNodeDetailsVO> loadNodeDetails(Connection pCon, String pInterfaceId) throws BTSLBaseException {
        final String methodName = "loadNodeDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_interfaceCategory=" + pInterfaceId);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList<InterfaceNodeDetailsVO> interfaceNodeList = new ArrayList<InterfaceNodeDetailsVO>();
        StringBuilder strBuilder = new StringBuilder("SELECT ip,port,uri,status,sequence_id ");
        strBuilder.append(" from INTERFACE_NODE_DETAILS WHERE interface_id=? and status IN(?,?)");
        String sqlSelect = strBuilder.toString();
        InterfaceNodeDetailsVO interfaceVO = null;
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try {
            pstmtSelect = pCon.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, pInterfaceId);
            pstmtSelect.setString(2, PretupsI.YES);
            pstmtSelect.setString(3, PretupsI.SUSPEND);
            rs = pstmtSelect.executeQuery();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query Executed:= " + sqlSelect);
            }
            while (rs.next()) {

                interfaceVO = new InterfaceNodeDetailsVO();
                interfaceVO.setIp(rs.getString("ip"));
                interfaceVO.setPort(rs.getString("port"));
                interfaceVO.setUri(rs.getString("uri"));
                interfaceVO.setNodeStatus(rs.getString("status"));
                interfaceVO.setNodeName(rs.getString("sequence_id"));
                interfaceNodeList.add(interfaceVO);
            }
        }

        catch (SQLException sqe) {
            log.error(methodName, "SQL Exception" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadNodeDetails]", "", "", "", sqlExpMsg + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqe);
        } catch (Exception e) {
            log.error(methodName, " Exception" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadNodeDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);

        } finally {
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
            	  log.error("An error occurred closing result set.", e);
              }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting size=" + interfaceNodeList.size());
            }
        }

        return interfaceNodeList;
    }

  
    /**
     * Method: deleteNodes
     * Method for deleting the details of node in the INTERFACE_NODE_DETAIL
     * table
     * @param pCon
     * @param interfaceId
     * @param deletelist
     * @param userId
     * @return delCount int
     * @throws BTSLBaseException
     */
    public int deleteNodes(Connection pCon, String interfaceId, ArrayList<InterfaceNodeDetailsVO> deletelist, String userId) throws BTSLBaseException {
        final String methodName = "deleteNodes";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: intrfaceID= " + deletelist);
        }

         
        int delCount = 0;

        try {

            StringBuilder strBuilder = new StringBuilder(" update INTERFACE_NODE_DETAILS");
            strBuilder.append(" Set  status = ?,deleted_on=?,deleted_by=? where sequence_id=?");
            String delQuery = strBuilder.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlInsert:" + delQuery);
            }
            try(PreparedStatement psmtDel = pCon.prepareStatement(delQuery);)
            {
            	int deletelists=deletelist.size();
            for (int i = 0; i <deletelists ; i++) {
                InterfaceNodeDetailsVO intVO = deletelist.get(i);
                psmtDel.setString(1, PretupsI.NO);
                psmtDel.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(new Date()));
                psmtDel.setString(3, userId);
                psmtDel.setString(4, intVO.getNodeName());
                delCount = psmtDel.executeUpdate();
                psmtDel.clearParameters();
            }
        } 
        }// end of try
        catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[deleteNodes]", "", "", "", sqlExpMsg + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqle);
        } // end of catch
        catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[deleteNodes]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);
        } // end of catch
        finally {
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: insertCount=" + delCount);
            }
        } // end of finally
        return delCount;
    }

   

    /**
     * Method deleteInterface.
     * This method is used to Soft delete all interface nodes when the interface
     * is deleted according to the interface id
     * @param pCon
     * @param interfaceId
     * @param userId
     * @return int
     * @throws BTSLBaseException
     */
    public int deleteAllNodes(Connection pCon, String interfaceId, String userId) throws BTSLBaseException {

        final String methodName = "deleteAllNodes";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entering VO " + interfaceId);
        }

        int deleteCount = -1;
         
        // call the DAO method to Update the interface node details
        try {

            StringBuilder updateQueryBuff = new StringBuilder("UPDATE interface_node_details SET status=?,deleted_on=?,deleted_by=? WHERE interface_id=?");
            String insertQuery = updateQueryBuff.toString();
            try(PreparedStatement pstmtUpdate = pCon.prepareStatement(insertQuery);)
            {
            pstmtUpdate.setString(1, PretupsI.NO);
            pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(new Date()));
            pstmtUpdate.setString(3, userId);
            pstmtUpdate.setString(4, interfaceId);
            deleteCount = pstmtUpdate.executeUpdate();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query Executed= " + insertQuery);
            }
        }
        }catch (SQLException sqle) {
            log.error(methodName, " SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[deleteAllNodes]", "", "", "", sqlExpMsg + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqle);
        }

        catch (Exception e) {
            log.error(methodName, " Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[deleteAllNodes]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);
        }

        finally {
            
            if (log.isDebugEnabled()) {
                log.debug("deleteAllNodes", "Exiting deleteCount " + deleteCount);
            }
        }

        return deleteCount;
    }

    /**
     * @param pCon
     * @param pInterfaceId
     * @param pPreferenceCode
     * @param pNetworkCode
     * @return
     * @throws BTSLBaseException
     */
    public int getMaxNodeByInterfaceId(Connection pCon, String pInterfaceId, String pPreferenceCode, String pNetworkCode) throws BTSLBaseException {
        int maxNodes = 0;

        final String methodName = "getMaxNodeByInterfaceid";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entering " + pInterfaceId);
        }

         
         
        StringBuilder strBuilder = new StringBuilder("Select value from control_preferences WHERE control_code=? and preference_code=? and network_code=?");
        String sqlSelect = strBuilder.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try(PreparedStatement pstmtSelect = pCon.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, pInterfaceId);
            pstmtSelect.setString(2, pPreferenceCode);
            pstmtSelect.setString(3, pNetworkCode);

           try(ResultSet rs = pstmtSelect.executeQuery();)
           {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query Executed= " + sqlSelect);
            }
            if (rs.next()) {
                maxNodes = Integer.parseInt(rs.getString("value"));
            }
        }
        }
        catch (SQLException sqe) {
            log.error(methodName, "SQL Exception" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[getMaxNodeByInterfaceid]", "", "", "", sqlExpMsg + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqe);
        } catch (Exception e) {
            log.error(methodName, " Exception" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[getMaxNodeByInterfaceid]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);

        }
        finally {
        	log.debug(methodName, "inside finally");
        }
        return maxNodes;
    }
	
    /**
	 * @param pInterfaceId
	 * @param pInstanceID
	 * @return
	 * @throws BTSLBaseException
	 */
	public java.util.Properties getInterfaceIdProperties(String pInterfaceId, String pInstanceID) throws BTSLBaseException
 	{
 		  
 		 final String methodName = "getInterfaceIdProperties";
 		LogFactory.printLog(methodName, "Entering pInterfaceId=" +pInterfaceId+" pInstanceID="+pInstanceID, log);
 		
 	
 		java.util.Properties interfaceIdProps=new java.util.Properties(); 
 		 
		StringBuilder strBuff= new StringBuilder("Select key,value from configurations WHERE type=? and interface_id=? and instance_id=?");
		String sqlSelect=strBuff.toString();  
		LogFactory.printLog(methodName, "Select Query= "+sqlSelect, log);
		Connection pCon = null;
	    
	    try
		{
	        pCon = OracleUtil.getSingleConnection();
		    
     try( PreparedStatement pstmtSelect=pCon.prepareStatement(sqlSelect);)
     {
      pstmtSelect.setString(1,"Interface");
      pstmtSelect.setString(2,pInterfaceId);
      pstmtSelect.setString(3,pInstanceID);
      
	   try( ResultSet rs=pstmtSelect.executeQuery();)
	   {
	    LogFactory.printLog(methodName, "Query Executed= "+sqlSelect, log);
			while(rs.next())
			{
				interfaceIdProps.setProperty(rs.getString("key"), BTSLUtil.NullToString(rs.getString("value")));
				
			}
			
		}
		}
		}
		catch(SQLException sqe)
		{
	        log.error(methodName,"SQL Exception"+sqe.getMessage());
	        log.errorTrace(methodName,sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"InterfaceDAO[getMaxNodeByInterfaceid]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
		}
		catch(Exception e)
		{
	       log.error(methodName," Exception"+e.getMessage());
	      log.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"InterfaceDAO[getMaxNodeByInterfaceid]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing",e);
			
		}finally
 		{
			
			OracleUtil.closeQuietly(pCon);
			LogFactory.printLog(methodName, "Exiting size="+ interfaceIdProps.size(), log);
		}
  	    
 		  
 		return interfaceIdProps;
 	}


    /**
     * Method getInterfaceDetailsByInterfaceID.
     * This method is used to load interface details into ArrayList
     * If there is any error then throws the SQLException or Exception
     *
     * @param con
     *            Connection
     * @param interfaceId
     *            String
     * @return interfaceDetails ArrayList
     */

    public ArrayList<InterfaceVO> getInterfaceDetailsByInterfaceID(Connection con,String interfaceId)
            throws BTSLBaseException, SQLException {
        final String methodName = "getInterfaceDetailsByInterfaceID";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        StringBuilder strBuilder = null;

        ArrayList<InterfaceVO> interfaceDetail = new ArrayList<>();
        InterfaceVO interfaceVO = null;
        try {
            strBuilder = new StringBuilder("SELECT i.interface_id,i.external_id,i.interface_description,i.interface_type_id,");
            strBuilder.append("i.status,i.clouser_date,i.message_language1,i.message_language2,i.concurrent_connection,");
            strBuilder.append("i.single_state_transaction,");
            strBuilder.append("i.status_type,i.status,i.val_expiry_time,i.topup_expiry_time, i.number_of_nodes,it.interface_type_id,");
            strBuilder.append("it.interface_name,it.interface_category,it.handler_class,it.underprocess_msg_reqd ");
            strBuilder.append(" FROM interfaces i,interface_types it WHERE it.interface_type_id=i.interface_type_id");
            strBuilder.append(" AND i.interface_id =?");


            String sqlLoad = strBuilder.toString();
            try (PreparedStatement pstmtSelect = con.prepareStatement(sqlLoad);) {
                pstmtSelect.setString(1, interfaceId);
                try (ResultSet rs = pstmtSelect.executeQuery();) {
                    LogFactory.printLog(methodName, "QUERY Executed = " + sqlLoad, log);
                    int index = 0;

                    while (rs.next()) {
                        interfaceVO = new InterfaceVO();
                        interfaceVO.setInterfaceId(rs.getString("interface_id"));
                        interfaceVO.setExternalId(rs.getString("external_id"));
                        interfaceVO.setInterfaceDescription(rs.getString("interface_description"));
                        interfaceVO.setNoOfNodes(rs.getString("number_of_nodes"));
                        interfaceVO.setSingleStateTransaction(rs.getString("single_state_transaction"));
                        interfaceVO.setInterfaceTypeId(rs.getString("interface_type_id"));
                        interfaceVO.setConcurrentConnection(rs.getInt("concurrent_connection"));
                        interfaceVO.setInterfaceName(rs.getString("interface_name"));
                        interfaceVO.setInterfaceCategory(rs.getString("interface_category"));
                        interfaceVO.setHandlerClass(rs.getString("handler_class"));
                        interfaceVO.setStatusType(rs.getString("status_type"));
                        interfaceVO.setStatusCode(rs.getString("status"));
                        interfaceVO.setValExpiryTime(rs.getLong("val_expiry_time"));
                        interfaceVO.setTopUpExpiryTime(rs.getLong("topup_expiry_time"));
                        interfaceVO.setLanguage1Message(rs.getString("message_language1"));
                        interfaceVO.setLanguage2Message(rs.getString("message_language2"));
                        interfaceDetail.add(interfaceVO);
                    }
                }
            }
        } catch (SQLException sqe) {
            log.error(methodName, " SQL Exception " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[getInterfaceDetailsByInterfaceID]", "", "", "", sqlExpMsg + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg, sqe);
        } catch (Exception e) {
            log.error(methodName, " :Exception" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[getInterfaceDetailsByInterfaceID]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg, e);

        } finally {
//                OracleUtil.closeQuietly(con);
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting interfaceDetail size=" + interfaceDetail.size());
            }
        }
        return interfaceDetail;
    }

    /**
     * Method loadInterfaceNodeDetails.
     * This method is used to load node details at the time for modification of
     * Nodes
     * If there is any error then throws the SQLException
     *
     * @author kirankumar
     * @param con
     *            Connection
     * @param interfaceId
     *            String
     * @return interfaceNodeList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList<InterfaceNodeDetailsVO> loadInterfaceNodeDetails(Connection con, String interfaceId) throws BTSLBaseException {
        final String methodName = "loadInterfaceNodeDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_interfaceCategory=" + interfaceId);
        }

        ArrayList<InterfaceNodeDetailsVO> interfaceNodeList = new ArrayList<InterfaceNodeDetailsVO>();
        StringBuilder strBuilder = new StringBuilder("SELECT ip,port,uri,status,sequence_id ");
        strBuilder.append(" from INTERFACE_NODE_DETAILS WHERE interface_id=? and status IN(?,?)");
        String sqlSelect = strBuilder.toString();
        InterfaceNodeDetailsVO interfaceVO = null;
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try (PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);) {
            pstmtSelect.setString(1, interfaceId);
            pstmtSelect.setString(2, PretupsI.YES);
            pstmtSelect.setString(3, PretupsI.SUSPEND);
            try (ResultSet rs = pstmtSelect.executeQuery();) {
                LogFactory.printLog(methodName, "QUERY Executed = " + sqlSelect, log);
                int index = 0;
                while (rs.next()) {
                    interfaceVO = new InterfaceNodeDetailsVO();
                    interfaceVO.setIp(rs.getString("ip"));
                    interfaceVO.setPort(rs.getString("port"));
                    interfaceVO.setUri(rs.getString("uri"));
                    interfaceVO.setNodeStatus(rs.getString("status"));
                    interfaceVO.setNodeName(rs.getString("sequence_id"));
                    interfaceNodeList.add(interfaceVO);
                }
            }
        }
        catch (SQLException sqe) {
            log.error(methodName, "SQL Exception" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceNodeDetails]", "", "", "", sqlExpMsg + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg,sqe);
        } catch (Exception e) {
            log.error(methodName, " Exception" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceNodeDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);

        } finally {
            OracleUtil.closeQuietly(con);
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting interfaceNodeList size=" + interfaceNodeList.size());
            }
        }

        return interfaceNodeList;
    }

    /**
     * This method is used before adding/modifying the record in the interfaces
     * table
     * it will check for the uniqueness of the interface_description column
     * if the interface_description the user enterd exists in the database
     * the method return true and record will not inserted in the interfaces
     * table.
     *
     * @return boolean
     * @param pCon
     *            Connection
     * @param pInterfaceDesc
     *            String
     * @param pInterfaceId
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isInterfaceNameExists(Connection pCon, String pInterfaceDesc, String pInterfaceId) throws BTSLBaseException {

        final String methodName = "isInterfaceNameExists";
        LogFactory.printLog(methodName, "Entered params p_interfaceDesc::" + pInterfaceDesc + " p_interfaceId=" + pInterfaceId, log);


        boolean found = false;
        StringBuilder sqlBuff = new StringBuilder("SELECT interface_description FROM");
        sqlBuff.append(" interfaces WHERE UPPER(interface_description)=UPPER(?)");

        if ((pInterfaceId != null) && (!("null".equals(pInterfaceId)))) {
            sqlBuff.append(" AND interface_id !=?");
        }

        String selectQuery = sqlBuff.toString();
        LogFactory.printLog(methodName, "Select Query: :" + selectQuery, log);

        try(PreparedStatement pstmtSelect =pCon.prepareStatement(selectQuery);) {

            pstmtSelect.setString(1, pInterfaceDesc);
            if ((!(BTSLUtil.isNullString(pInterfaceId))) && (!("null".equals(pInterfaceId)))) {
                pstmtSelect.setString(2, pInterfaceId);
            }
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {

                LogFactory.printLog(methodName, "Query Executed ::" + selectQuery, log);
                if (rs.next()) {
                    found = true;
                }
            }
        }

        catch (SQLException sqle) {
            log.error(methodName, "  SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isInterfaceNameExists]", "", "", "", sqlExpMsg + sqle.getMessage());
            throw new BTSLBaseException(this, "isExists()", errsqlMesg,sqle);
        }

        catch (Exception e) {
            log.error(methodName, " Exception  " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[isInterfaceNameExists]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg,e);
        }

        finally {

            LogFactory.printLog(methodName, "Exiting isExists found  =" + found, log);
        }

        return found;
    }

    /**
     * Method loadInterfaceType.
     * This method is used to load interface type id from interface_types table
     * If there is any error then throws the SQLException
     *
     * @param pCon
     *            Connection
     * @param pInterfaceCategory
     *            String
     * @return interfaceTypeIdList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList<InterfaceTypeVO> loadInterfaceTypes(Connection pCon, String pInterfaceCategory) throws BTSLBaseException {
        final String methodName = "loadInterfaceType";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_interfaceCategory=" + pInterfaceCategory);
        }
        InterfaceTypeVO interfaceTypeVO = null;
        ArrayList<InterfaceTypeVO> interfaceTypeList = new ArrayList<InterfaceTypeVO>();
        StringBuilder strBuilder = new StringBuilder("SELECT interface_type_id,interface_name,uri_req");
        strBuilder.append(" FROM interface_types WHERE interface_type_id=?");
        strBuilder.append(" ORDER BY interface_type_id");
        String sqlSelect = strBuilder.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Select Query= " + sqlSelect);
        }

        try(PreparedStatement pstmtSelect = pCon.prepareStatement(sqlSelect);) {

            pstmtSelect.setString(1, pInterfaceCategory);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
                if (log.isDebugEnabled()) {
                    log.debug(methodName, " Query Executed = " + sqlSelect);
                }
                while (rs.next()) {
                    interfaceTypeVO = new InterfaceTypeVO();
                    interfaceTypeVO.setInterfaceTypeId(rs.getString("interface_type_id"));
                    interfaceTypeVO.setInterfaceTypeName(rs.getString("interface_name"));
                    interfaceTypeVO.setUriRequired(rs.getString("uri_req"));
                    interfaceTypeList.add(interfaceTypeVO);
                }
            }
        }
        catch (SQLException sqe) {
            log.error(methodName, "SQL Exception  " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceType]", "", "", "", "SQL Exception," + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errsqlMesg);
        } catch (Exception e) {
            log.error(methodName, " Exception  " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadInterfaceType]", "", "", "", "Exception_:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, errGeneralMesg);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting size= " + interfaceTypeList.size());
            }
        }
        return interfaceTypeList;
    }
    public boolean isExistsInterfaceId(Connection con, String interfaceID) throws BTSLBaseException {

        final String METHOD_NAME = "isExistsInterfaceId";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered params interfaceID=" + interfaceID);
        }
        boolean found = false;
        String sqlBuff = "SELECT 1 FROM interfaces WHERE UPPER(interface_id)=UPPER(?) AND status<>'N'";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Select Query:" + sqlBuff);
        }

        try(PreparedStatement pstmtSelect = con.prepareStatement(sqlBuff);) {

            pstmtSelect.setString(1, interfaceID);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
                if (log.isDebugEnabled()) {
                    log.debug(METHOD_NAME, "Query Executed::" + sqlBuff);
                }

                if (rs.next()) {
                    found = true;
                }
            }
        }catch (SQLException sqle) {
            log.error(METHOD_NAME, " SQLException   " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, METHOD_NAME, "", "", "", sqlExpMsg + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, errsqlMesg,sqle);
        } catch (Exception e) {
            log.error(METHOD_NAME, "  Exception  " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, METHOD_NAME, "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, errGeneralMesg,e);
        }
        finally {
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting isExistsInterfaceId found=" + found);
            }
        }
        return found;
    }

}
