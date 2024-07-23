/**
 * @# WhiteListDAO
 *    This class is the controller class of the Channel user Module.
 * 
 *    Created on Created by History
 *    --------------------------------------------------------------------------
 *    ------
 *    March 28, 2006 Ankit Zindal Initial creation
 *    May 16,2006 Ashish Kumar Modification
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.whitelist.businesslogic;

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
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

/**
 * @author 
 *
 */
public class WhiteListDAO {
    private Log log = LogFactory.getLog(this.getClass().getName());
    private static WhiteListQry whiteListQry;
    

    public WhiteListDAO() {
		super();
		whiteListQry = (WhiteListQry) ObjectProducer.getObject(
				QueryConstants.WHITE_LIST_QRY,
				QueryConstants.QUERY_PRODUCER);
	}

    /**
     * Method loadInterfaceDetails.
     * This method is to get the interface id from white list on the basis of
     * the subscriber msisdn.
     * 
     * @param pCon
     *            Connection
     * @param pMsisdn
     *            String
     * 
     * @return WhiteListVO
     * @throws BTSLBaseException
     */
    public WhiteListVO loadInterfaceDetails(Connection pCon, String pMsisdn) throws BTSLBaseException {
        final String methodName = "loadInterfaceDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered pMsisdn:" + pMsisdn);
        }
        PreparedStatement pstmt = null;
         
        WhiteListVO whiteListVO = null;
        ListValueVO listValueVO = null;
        
        try {
            pstmt = whiteListQry.loadInterfaceDetailsQry(pCon, pMsisdn);
           try(ResultSet rs = pstmt.executeQuery();)
           {
            if (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("handler_class"), rs.getString("interface_id"));
                listValueVO.setType(rs.getString("underprocess_msg_reqd"));
                listValueVO.setTypeName(rs.getString("service_class_id"));
                listValueVO.setIDValue(rs.getString("external_id"));
                listValueVO.setStatus(rs.getString("status"));
                listValueVO.setStatusType(rs.getString("statustype"));
                listValueVO.setOtherInfo(rs.getString("message_language1"));
                listValueVO.setOtherInfo2(rs.getString("message_language2"));
                listValueVO.setSingleStep(rs.getString("single_state_transaction"));
                whiteListVO = new WhiteListVO();
                whiteListVO.setAccountID(rs.getString("account_id"));
                whiteListVO.setAccountStatus(rs.getString("account_status"));
                whiteListVO.setServiceClassCode(rs.getString("service_class"));
                whiteListVO.setCreditLimit(rs.getLong("credit_limit"));
                whiteListVO.setNetworkCode(rs.getString("network_code"));
                whiteListVO.setMsisdn(rs.getString("msisdn"));
                whiteListVO.setEntryDate(rs.getDate("entry_date"));
                whiteListVO.setExternalInterfaceCode(rs.getString("external_interface_code"));
                whiteListVO.setCreatedBy(rs.getString("created_by"));
                whiteListVO.setCreatedOn(rs.getDate("created_on"));
                whiteListVO.setStatus(rs.getString("status"));
                whiteListVO.setImsi(rs.getString("imsi"));
                whiteListVO.setLanguage(rs.getString("language"));
                whiteListVO.setCountry(rs.getString("country"));
                whiteListVO.setListValueVO(listValueVO);
            }

        }
        }// end of try
        catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[loadInterfaceDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[loadInterfaceDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
       
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing statement.", e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: whiteListVO=" + whiteListVO);
            }
        }// end of finally.
        return whiteListVO;
    }

    /**
     * Method loadWhiteListSubsDetails
     * Method for Extracting Postpaid Subscriber Details.
     * 
     * @author Amit Ruwali
     * @param pCon
     *            Connection
     * @param pMsisdn
     *            String
     * @return whiteListVO WhiteListVO
     * @throws BTSLBaseException
     */

    public WhiteListVO loadWhiteListSubsDetails(Connection pCon, String pMsisdn) throws BTSLBaseException {

        final String methodName = "loadWhiteListSubsDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered pMsisdn=" + pMsisdn);
        }

        WhiteListVO whiteListVO = null;
        
        
        String sqlSelect = whiteListQry.loadWhiteListSubsDetailsQry();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try( PreparedStatement pstmtSubList = pCon.prepareStatement(sqlSelect);) {
          
            pstmtSubList.setString(1, PretupsI.WHITE_LIST_MOVEMENT_CODE);
            pstmtSubList.setString(2, PretupsI.WHITE_LIST_STATUS);
            pstmtSubList.setString(3, pMsisdn);
            try(ResultSet rs = pstmtSubList.executeQuery();)
            {
            if (rs.next()) {
                whiteListVO = new WhiteListVO();
                whiteListVO.setMsisdn(rs.getString("msisdn"));
                whiteListVO.setAccountID(rs.getString("account_id"));
                whiteListVO.setInterfaceID(rs.getString("interface_id"));
                whiteListVO.setInterfaceName(rs.getString("interface_name"));
                whiteListVO.setEntryDate(rs.getDate("entry_date"));
                whiteListVO.setEntryDateStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("entry_date")))));
                whiteListVO.setAccountStatus(rs.getString("account_status"));
                whiteListVO.setServiceClassCode(rs.getString("service_class"));
                whiteListVO.setServiceClassName(rs.getString("service_class_name"));
                whiteListVO.setCreditLimit(rs.getLong("credit_limit"));
                whiteListVO.setCreditLimitStr(String.valueOf(PretupsBL.getDisplayAmount(rs.getLong("credit_limit"))));
                whiteListVO.setStatus(rs.getString("status"));
                whiteListVO.setStatusStr(rs.getString("WLSTATUS"));
                whiteListVO.setLanguage(rs.getString("LANG"));
                whiteListVO.setCountry(rs.getString("country"));
                whiteListVO.setMovementCode(rs.getString("movement_code"));
                whiteListVO.setMovementName(rs.getString("lookup_name"));
                whiteListVO.setActivatedOn(rs.getDate("activated_on"));
                whiteListVO.setActivatedOnStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("activated_on"))));

                whiteListVO.setActivatedBy(rs.getString("UN"));
                whiteListVO.setImsi(rs.getString("imsi"));
                whiteListVO.setNetworkCode(rs.getString("network_code"));
            }
        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[loadWhiteListSubsDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[loadWhiteListSubsDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting whiteListVO=" + whiteListVO);
            }
        }
        return whiteListVO;
    }

    /**
     * This method is used to insert white list subscriber detail in batch.
     * 
     * @param Connection
     *            pCon
     * @param ArrayList
     *            pInsertList
     * @return int
     * @throws BTSLBaseException
     */
    public int batchInsertWhiteListSubsDetail(Connection pCon, ArrayList pInsertList) throws BTSLBaseException {
        final String methodName = "batchInsertWhiteListSubsDetail";
        if (log.isDebugEnabled()) {
            log.debug("batchInsertWhiteListSubsDetail", "Entered pInsertList.size()=" + pInsertList.size());
        }
        int insertCount = 0;
        WhiteListVO whiteListVO = null;
         
        int[] batchInCt;
        int k = 0;
        StringBuilder strBuff = new StringBuilder(" INSERT INTO white_list VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        if (log.isDebugEnabled()) {
            log.debug("batchInsertWhiteListSubsDetail", "SQL INSERT QUERY strBuff.toString()=" + strBuff.toString());
        }
        try(PreparedStatement  pstmtSubList = pCon.prepareStatement(strBuff.toString());) {
          
            for (int i = 0, size = pInsertList.size(); i < size; i++) {
                k = 0;
                whiteListVO = (WhiteListVO) pInsertList.get(i);
                pstmtSubList.setString(++k, whiteListVO.getNetworkCode());
                pstmtSubList.setString(++k, whiteListVO.getMsisdn());
                pstmtSubList.setString(++k, whiteListVO.getAccountID());
                pstmtSubList.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(whiteListVO.getEntryDate()));
                pstmtSubList.setString(++k, whiteListVO.getAccountStatus());
                pstmtSubList.setString(++k, whiteListVO.getServiceClassCode());
                pstmtSubList.setString(++k, whiteListVO.getCreditLimitStr());
                pstmtSubList.setString(++k, whiteListVO.getInterfaceID());
                pstmtSubList.setString(++k, whiteListVO.getExternalInterfaceCode());
                pstmtSubList.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(whiteListVO.getCreatedOn()));
                pstmtSubList.setString(++k, whiteListVO.getCreatedBy());
                pstmtSubList.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(whiteListVO.getModifiedOn()));
                pstmtSubList.setString(++k, whiteListVO.getModifiedBy());
                pstmtSubList.setString(++k, whiteListVO.getStatus());
                pstmtSubList.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(whiteListVO.getActivatedOn()));
                pstmtSubList.setString(++k, whiteListVO.getActivatedBy());
                pstmtSubList.setString(++k, whiteListVO.getMovementCode());
                pstmtSubList.setString(++k, whiteListVO.getLanguage());
                pstmtSubList.setString(++k, whiteListVO.getCountry());
                pstmtSubList.setString(++k, whiteListVO.getImsi());
                pstmtSubList.addBatch();
                whiteListVO = null;// Setting whiteListVO is null to suggest JVM
                                   // for GC.
            }
            batchInCt = pstmtSubList.executeBatch();
            pstmtSubList.clearBatch();
            insertCount = batchInCt.length;
        } catch (SQLException sqe) {
            log.error("batchInsertWhiteListSubsDetail", "SQLException : " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[batchInsertWhiteListSubsDetail]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "batchInsertWhiteListSubsDetail", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error("batchInsertWhiteListSubsDetail", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[batchInsertWhiteListSubsDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "batchInsertWhiteListSubsDetail", "error.general.processing");
        } finally {
         
            if (log.isDebugEnabled()) {
                log.debug("batchInsertWhiteListSubsDetail", "Exiting insertCount=" + insertCount);
            }
        }
        return insertCount;
    }

    /**
     * This method is used to delete all records in the white list in batch.
     * 
     * @param Connection
     *            pCon
     * @param String
     *            pNetworkCode
     * @param String
     *            pInterfaceID
     * @return int
     * @throws BTSLBaseException
     */
    public int batchDeleteWhiteListSubsDetail(Connection pCon, String pNetworkCode, String pInterfaceID) throws BTSLBaseException {
        final String methodName = "batchDeleteWhiteListSubsDetail";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered pNetworkCode = " + pNetworkCode + " pInterfaceID = " + pInterfaceID);
        }
        int deleteCount = 0;
      
        String deleteQuery = new String("DELETE FROM white_list WHERE NETWORK_CODE=? AND INTERFACE_ID=? ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "deleteQuery = " + deleteQuery);
        }
        try ( PreparedStatement pstmtSubList = pCon.prepareStatement(deleteQuery);){
            
            pstmtSubList.setString(1, pNetworkCode);
            pstmtSubList.setString(2, pInterfaceID);
            deleteCount = pstmtSubList.executeUpdate();
            if (deleteCount == 0) {
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "No records are found for delete");
                }
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[batchDeleteWhiteListSubsDetail]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[batchDeleteWhiteListSubsDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
         
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting deleteCount=" + deleteCount);
            }
        }
        return deleteCount;
    }

    /**
     * This method is used to delete all records in the white list in batch.
     * 
     * @param Connection
     *            pCon
     * @param ArrayList
     *            pDeleteList
     * @return int
     * @throws BTSLBaseException
     */
    public int deleteWhiteListSubsDetail(Connection pCon, ArrayList pDeleteList) throws BTSLBaseException {
        final String methodName = "deleteWhiteListSubsDetail";
        if (log.isDebugEnabled()) {
            log.debug("deleteWhiteListSubsDetail", "Entered pDeleteList.size() = " + pDeleteList.size());
        }
        int deleteCount = 0;
        int tempDelCount = 0;
        WhiteListVO whiteListVO = null;
       
        String deleteQuery = new String("DELETE FROM white_list WHERE network_code=? AND interface_id=? AND msisdn=?");
        if (log.isDebugEnabled()) {
            log.debug("deleteWhiteListSubsDetail", "deleteQuery = " + deleteQuery);
        }
        try (PreparedStatement pstmtSubList = pCon.prepareStatement(deleteQuery);){
           
            for (int i = 0, size = pDeleteList.size(); i < size; i++) {
                whiteListVO = (WhiteListVO) pDeleteList.get(i);
                pstmtSubList.setString(1, whiteListVO.getNetworkCode());
                pstmtSubList.setString(2, whiteListVO.getInterfaceID());
                pstmtSubList.setString(3, whiteListVO.getMsisdn());
                tempDelCount = pstmtSubList.executeUpdate();

                // Incase when number is not found for deletion,information will
                // be logged in error mode- discussed with Sanjay Sir
                if (tempDelCount == 0) {
                    log.error("batchDeleteWhiteListSubsDetail", "Records with MSISDN = " + whiteListVO.getMsisdn() + "is not found for delete");
                }
                pstmtSubList.clearParameters();
                deleteCount += tempDelCount;
                whiteListVO = null;// Setting whiteListVO is null to suggest JVM
                                   // for GC.
            }
        } catch (SQLException sqe) {
            log.error("deleteWhiteListSubsDetail", "SQLException : " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[deleteWhiteListSubsDetail]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "deleteWhiteListSubsDetail", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error("deleteWhiteListSubsDetail", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[deleteWhiteListSubsDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "deleteWhiteListSubsDetail", "error.general.processing");
        } finally {
          
            if (log.isDebugEnabled()) {
                log.debug("deleteWhiteListSubsDetail", "Exiting deleteCount=" + deleteCount);
            }
        }
        return deleteCount;
    }

    /**
     * This method is used to update the whiteList information.While updating
     * this method checks following
     * 1.If the record that is to be updated not found then Insert that record.
     * 
     * @param Connection
     *            pCon
     * @param ArrayList
     *            pUpdateList
     * @throws BTSLBaseException
     */
    public int updateWhiteListSubsDetail(Connection pCon, ArrayList pUpdateList) throws BTSLBaseException {
        final String methodName = "updateWhiteListSubsDetail";
        if (log.isDebugEnabled()) {
            log.debug("updateWhiteListSubsDetail", "Entered pUpdateList.size()= " + pUpdateList.size());
        }
        int updateCount = 0;
        int insertCt = 0;
        int tempUpdateCount = 0;
        int k = 0;
        ArrayList tempInsertList = new ArrayList();
        WhiteListVO whiteListVO = null;
        
        StringBuilder strBuff = new StringBuilder(" UPDATE white_list SET network_code=?, interface_id=?, account_status=?,credit_limit=?,imsi=?,account_id=?, ");
        strBuff.append("service_class=?,movement_code=?,modified_on=?,modified_by=?,activated_on=?, ");
        strBuff.append("activated_by=?,language=?,country=?  WHERE msisdn=? ");
        if (log.isDebugEnabled()) {
            log.debug("updateWhiteListSubsDetail", "UpdateQuery strBuff.toString() = " + strBuff.toString());
        }
        try(PreparedStatement pstmtUpdate = pCon.prepareStatement(strBuff.toString());) {
           
            for (int i = 0, size = pUpdateList.size(); i < size; i++) {
                k = 0;
                tempUpdateCount = 0;
                whiteListVO = (WhiteListVO) pUpdateList.get(i);
                pstmtUpdate.setString(++k, whiteListVO.getNetworkCode());
                pstmtUpdate.setString(++k, whiteListVO.getInterfaceID());
                pstmtUpdate.setString(++k, whiteListVO.getAccountStatus());
                pstmtUpdate.setString(++k, whiteListVO.getCreditLimitStr());
                pstmtUpdate.setString(++k, whiteListVO.getImsi());
                pstmtUpdate.setString(++k, whiteListVO.getAccountID());
                pstmtUpdate.setString(++k, whiteListVO.getServiceClassCode());
                pstmtUpdate.setString(++k, whiteListVO.getMovementCode());
                pstmtUpdate.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(whiteListVO.getModifiedOn()));
                pstmtUpdate.setString(++k, whiteListVO.getModifiedBy());
                pstmtUpdate.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(whiteListVO.getActivatedOn()));
                pstmtUpdate.setString(++k, whiteListVO.getActivatedBy());
                pstmtUpdate.setString(++k, whiteListVO.getLanguage());
                pstmtUpdate.setString(++k, whiteListVO.getCountry());
                pstmtUpdate.setString(++k, whiteListVO.getMsisdn());
                tempUpdateCount = pstmtUpdate.executeUpdate();
                pstmtUpdate.clearParameters();
                if (tempUpdateCount == 0) {
                    // Incase when number is not found for update, record will
                    // be inserted and information will be logged in error mode-
                    // discussed with Sanjay Sir
                    log.error("updateWhiteListSubsDetail", "RECORD WITH MSISDN = " + whiteListVO.getMsisdn() + " NOT FOUND FOR UPDATE");
                    tempInsertList.add(whiteListVO);
                } else {
                    whiteListVO = null;
                }
                updateCount += tempUpdateCount;
            }
            try {
                if (tempInsertList.size() > 0) {
                    insertCt = insertIndWhiteListDetail(pCon, tempInsertList);
                }
            } catch (BTSLBaseException be) {
                log.error("updateWhiteListSubsDetail", "BTSLBaseException", be.getMessage());
                throw be;
            }
        } catch (SQLException sqe) {
            log.error("updateWhiteListSubsDetail", "SQLException : " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[updateWhiteListSubsDetail]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateWhiteListSubsDetail", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error("updateWhiteListSubsDetail", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[updateWhiteListSubsDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "updateWhiteListSubsDetail", "error.general.processing");
        } finally {
           
            if (log.isDebugEnabled()) {
                log.debug("updateWhiteListSubsDetail", "Exiting updateCount =" + updateCount + " insertCt = " + insertCt);
            }
        }
        return updateCount + insertCt;
    }

    /**
     * This method is used to insert the white list information. While inserting
     * it cheks the following
     * 1.If the record that is to be inserted,already present, update that
     * record.
     * 
     * @param Connection
     *            pCon
     * @param ArrayList
     *            pInsertList
     * @return int
     * @throws BTSLBaseException
     */
    public int insertIndWhiteListDetail(Connection pCon, ArrayList pInsertList) throws BTSLBaseException {
        final String methodName = "insertIndWhiteListDetail";
        if (log.isDebugEnabled()) {
            log.debug("insertIndWhiteListDetail", "Entered pInsertList.size()=" + pInsertList.size());
        }
        int insertCount = 0;
        int updateCt = 0;
        int tempInsertCount = 0;
        int k = 0;
        WhiteListVO whiteListVO = null;
       
        ArrayList tempUpdateList = new ArrayList();
        String insertquery=whiteListQry.insertIndWhiteListDetail();
       
        if (log.isDebugEnabled()) {
            log.debug("insertIndWhiteListDetail", "SQL INSERT QUERY strBuff.toString()=" + insertquery);
        }
        try ( PreparedStatement pstmtSubList = pCon.prepareStatement(insertquery);){
           
            for (int i = 0, size = pInsertList.size(); i < size; i++) {
                k = 0;
                tempInsertCount = 0;
                try {
                    whiteListVO = (WhiteListVO) pInsertList.get(i);
                    pstmtSubList.setString(++k, whiteListVO.getNetworkCode());
                    pstmtSubList.setString(++k, whiteListVO.getMsisdn());
                    pstmtSubList.setString(++k, whiteListVO.getAccountID());
                    pstmtSubList.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(whiteListVO.getEntryDate()));
                    pstmtSubList.setString(++k, whiteListVO.getAccountStatus());
                    pstmtSubList.setString(++k, whiteListVO.getServiceClassCode());
                    pstmtSubList.setString(++k, whiteListVO.getCreditLimitStr());
                    pstmtSubList.setString(++k, whiteListVO.getInterfaceID());
                    pstmtSubList.setString(++k, whiteListVO.getExternalInterfaceCode());
                    pstmtSubList.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(whiteListVO.getCreatedOn()));
                    pstmtSubList.setString(++k, whiteListVO.getCreatedBy());
                    pstmtSubList.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(whiteListVO.getModifiedOn()));
                    pstmtSubList.setString(++k, whiteListVO.getModifiedBy());
                    pstmtSubList.setString(++k, whiteListVO.getStatus());
                    pstmtSubList.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(whiteListVO.getActivatedOn()));
                    pstmtSubList.setString(++k, whiteListVO.getActivatedBy());
                    pstmtSubList.setString(++k, whiteListVO.getMovementCode());
                    pstmtSubList.setString(++k, whiteListVO.getLanguage());
                    pstmtSubList.setString(++k, whiteListVO.getCountry());
                    pstmtSubList.setString(++k, whiteListVO.getImsi());
                    tempInsertCount = pstmtSubList.executeUpdate();
                    insertCount += tempInsertCount;
                    pstmtSubList.clearParameters();
                    whiteListVO = null;
                } catch (SQLException sqe) {
                    // Checking the cause of SQLException, if it occurs because
                    // of the record exists already,update that record.
                    if (sqe.getErrorCode() == 00001 && sqe.getMessage().indexOf("PK_WHITE_LIST") > 0) {
                        // Incase when number is already exist and insert will
                        // cause to update that records,information will be
                        // logged in error mode- discussed with Sanjay Sir
                        log.error("insertIndWhiteListDetail", "record already Exist = " + whiteListVO.getMsisdn());
                        pstmtSubList.clearParameters();// Confirm?
                        tempUpdateList.add(whiteListVO);
                    } else {
                        log.error("insertIndWhiteListDetail", "SQLException : " + sqe.getMessage());
                        log.errorTrace(methodName, sqe);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[insertIndWhiteListDetail]", "", "", "", "SQL Exception:" + sqe.getMessage());
                        throw new BTSLBaseException(this, "insertIndWhiteListDetail", "error.general.sql.processing");
                    }
                    log.errorTrace(methodName, sqe);

                }// end of catch-SQLException
            }// end of for loop
            if (tempUpdateList.size() > 0) {
                try {
                    updateCt = updateWhiteListSubsDetail(pCon, tempUpdateList);
                } catch (BTSLBaseException be) {
                    log.error("insertIndWhiteListDetail", "BTSLBaseException be=" + be.getMessage());
                    throw be;
                }// end of catch-Exception
            }// end of if
        } catch (SQLException sqe) {
            log.error("insertIndWhiteListDetail", "SQLException : " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[insertIndWhiteListDetail]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "insertIndWhiteListDetail", "error.general.sql.processing");
        }// end of catch-SQLException
        catch (Exception ex) {
            log.error("insertIndWhiteListDetail", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[insertIndWhiteListDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "insertIndWhiteListDetail", "error.general.processing");
        }// end of catch-Exception
        finally {
           
            if (log.isDebugEnabled()) {
                log.debug("insertIndWhiteListDetail", "Exiting insertCount=" + insertCount + " updateCt=" + updateCt);
            }
        }// end of finally
        return insertCount + updateCt;
    }
}
