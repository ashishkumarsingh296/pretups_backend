package com.web.pretups.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.btsl.util.MessageResources;

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
import com.btsl.pretups.cellidmgt.businesslogic.CellIdMgmtDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.transfer.businesslogic.TransferRulesVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;
import com.restapi.networkadmin.requestVO.SearchPromoTransferReqVO;
import com.restapi.networkadmin.responseVO.PromoParentUserVO;
import com.restapi.networkadmin.responseVO.PromoTransferSearchVO;
/**
 * class TransferWebDAO
 */
public class TransferWebDAO {

    /**
     * Field LOG.
     */
    private Log LOG = LogFactory.getLog(TransferWebDAO.class.getName());
    private TransferWebQry transferWebQry = (TransferWebQry)ObjectProducer.getObject(QueryConstants.TRANSFER_WEB_QUERY, QueryConstants.QUERY_PRODUCER);

    /**
     * Method addTransferRule.
     * This method is used to add the record in the transfer_rules table .
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addTransferRule(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        final String methodName = "addTransferRule";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        }
        
        int addCount = 0;
        int i = 1;
        try {
            StringBuilder insertQueryBuff = new StringBuilder();
            insertQueryBuff.append("INSERT INTO transfer_rules (module,network_code,sender_subscriber_type,  ");
            insertQueryBuff.append("receiver_subscriber_type, sender_service_class_id,receiver_service_class_id, ");
            insertQueryBuff.append("created_on,created_by, modified_on, modified_by,card_group_set_id,status,sub_service,service_type, ");
            insertQueryBuff.append("gateway_code,grade_code,category_code,cell_group_id) ");
            insertQueryBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            String insertQuery = insertQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Insert query:" + insertQuery);
            }
           try(PreparedStatement pstmtInsert = p_con.prepareStatement(insertQuery);)
           {
            pstmtInsert.setString(i, p_transferRulesVO.getModule());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getNetworkCode());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getSenderSubscriberType());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getReceiverSubscriberType());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getSenderServiceClassID());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getReceiverServiceClassID());
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getCreatedOn()));
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getCreatedBy());
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getModifiedOn()));
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getModifiedBy());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getCardGroupSetID());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getStatus());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getSubServiceTypeId());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getServiceType());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getGatewayCode());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getGradeCode1());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getCategoryCode1());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getCellGroupId());
            i++;
            addCount = pstmtInsert.executeUpdate();
        }
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addTransferRule]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addTransferRule]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	
           
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting addCount=" + addCount);
            }
        }// end of finally
        return addCount;
    }

    /**
     * Method updateTransferRule.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @param deleteFlag
     *            boolean
     * @return int
     * @throws BTSLBaseException
     */
    public int updateTransferRule(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        final String methodName = "updateTransferRule";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        }
       
        int updateCount = 0;
        int i = 1;
        try {
            final StringBuilder updateQueryBuff = new StringBuilder();
            updateQueryBuff.append("UPDATE transfer_rules SET  modified_on=?, modified_by=? ");
            updateQueryBuff.append(",card_group_set_id=? ,status=? ");
            updateQueryBuff.append("WHERE module = ? AND network_code=? AND sender_subscriber_type=? ");
            updateQueryBuff.append("AND receiver_subscriber_type=? AND sender_service_class_id=? ");
            updateQueryBuff.append("AND receiver_service_class_id=? AND sub_service = ? AND service_type = ?");
            // added by shashank for roadmap bug fix
            updateQueryBuff.append(" AND RULE_TYPE= ? ");
            updateQueryBuff.append(" AND gateway_code=? AND category_code=? AND grade_code=?");
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Update query:" + updateQueryBuff);
            }
            try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQueryBuff.toString());)
            {
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getModifiedOn()));
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getModifiedBy());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getCardGroupSetID());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getStatus());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getModule());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getNetworkCode());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getSenderSubscriberType());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getReceiverSubscriberType());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getSenderServiceClassID());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getReceiverServiceClassID());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getSubServiceTypeId());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getServiceType());
            i++;
            // added by shashank for roadmap bug fix
            if (!BTSLUtil.isNullString(p_transferRulesVO.getRuleType()) && p_transferRulesVO.getRuleType().equalsIgnoreCase(PretupsI.TRANSFER_RULE_PROMOTIONAL)) {
                pstmtUpdate.setString(i, PretupsI.TRANSFER_RULE_PROMOTIONAL);
                i++;
            } else {
                pstmtUpdate.setString(i, PretupsI.TRANSFER_RULE_NORMAL);
                i++;
            }
            pstmtUpdate.setString(i, p_transferRulesVO.getGatewayCode());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getCategoryCode());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getGradeCode());
            i++;
            // for the checking is the record modified during the transaction.
            final boolean modified = this.isRecordModified(p_con, p_transferRulesVO);
            if (modified) {
                throw new BTSLBaseException(this, "updateServiceType", "error.modify.true");
            }
            updateCount = pstmtUpdate.executeUpdate();
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferRule]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferRule]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
       
        	
           
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting updateCount=" + updateCount);
            }
        }// end of finally
        return updateCount;
    }

    /**
     * Method deleteTransferRule.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @param deleteFlag
     *            boolean
     * @return int
     * @throws BTSLBaseException
     */
    public int deleteTransferRule(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        final String methodName = "deleteTransferRule";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        }
        
        int deleteCount = 0;
        int i = 1;
        try {
            final StringBuilder deleteQueryBuff = new StringBuilder();
            deleteQueryBuff.append("DELETE FROM transfer_rules ");
            deleteQueryBuff.append("WHERE module = ? AND network_code=? AND sender_subscriber_type=? ");
            deleteQueryBuff.append("AND receiver_subscriber_type=? AND sender_service_class_id=? AND ");
            deleteQueryBuff.append("receiver_service_class_id=? AND sub_service=? AND service_type = ?");
            deleteQueryBuff.append(" AND gateway_code=? AND category_code=? AND grade_code=?");
            final String deleteQuery = deleteQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "delete query:" + deleteQuery);
            }
           try(PreparedStatement pstmtDelete = p_con.prepareStatement(deleteQuery);)
           {
            pstmtDelete.setString(i, p_transferRulesVO.getModule());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getNetworkCode());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getSenderSubscriberType());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getReceiverSubscriberType());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getSenderServiceClassID());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getReceiverServiceClassID());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getSubServiceTypeId());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getServiceType());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getGatewayCode());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getCategoryCode());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getGradeCode());
            i++;
            // for the checking is the record modified during the transaction.
            final boolean modified = this.isRecordModified(p_con, p_transferRulesVO);
            if (modified) {
                throw new BTSLBaseException(this, "updateServiceType", "error.modify.true");
            }
            deleteCount = pstmtDelete.executeUpdate();
        }
        }catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[deleteTransferRule]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[deleteTransferRule]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	
            
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting deleteCount=" + deleteCount);
            }
        }// end of finally
        return deleteCount;
    }

    /**
     * Method isTransferRuleExist.
     * This method is used to check the uniqueness of the transfer rule in the
     * transfer_rules table .
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isTransferRuleExist(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        final String methodName = "isTransferRuleExist";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        }
        
        boolean isExist = false;
        int i = 1;
        try {
            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT 1 FROM transfer_rules ");
            selectQuery.append("WHERE module=? AND network_code=? AND sender_subscriber_type=? AND receiver_subscriber_type=?  ");
            selectQuery.append("AND sender_service_class_id=? AND receiver_service_class_id=? AND sub_service=? AND service_type=?"); // AND
           
            selectQuery.append(" AND gateway_code=?");
            selectQuery.append(" AND category_code=? and grade_code=?");

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "SELECT QUERY:" + selectQuery);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery.toString());)
            {
            pstmtSelect.setString(i, p_transferRulesVO.getModule());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getNetworkCode());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getSenderSubscriberType());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getReceiverSubscriberType());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getSenderServiceClassID());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getReceiverServiceClassID());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getSubServiceTypeId());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getServiceType());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getGatewayCode());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getCategoryCode1());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getGradeCode1());
            i++;
            

            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                isExist = true;
            }
        }
            }
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isTransferRuleExist]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isTransferRuleExist]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "ExitingIsExist =" + isExist);
            }
        }// end of finally
        return isExist;
    }

    /**
     * Method loadTransferRuleList.
     * This method is to load the list of all the transfer rules which are form
     * the specified network and
     * which status is ACTIVE.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_module
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadTransferRuleList(Connection p_con, String p_networkCode, String p_module) throws BTSLBaseException {
        final String methodName = "loadTransferRuleList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:p_networkCode=" + p_networkCode + ",p_module=" + p_module);
        }
       
        
        TransferRulesVO rulesVO = null;
        ArrayList transferRulesList = null;
        String sqlSelect = transferWebQry.loadTransferRuleListQry();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadTransferRuleList", "QUERY sqlSelect=" + sqlSelect);
        }
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, PretupsI.TRANSFER_RULE_STATUS_DELETE);
            pstmt.setString(3, p_module);
            pstmt.setString(4, PretupsI.TRANSFER_RULE_NORMAL);
           
            try(ResultSet rs = pstmt.executeQuery();)
            {
            transferRulesList = new ArrayList();
            int index = 0;
            while (rs.next()) {
                rulesVO = new TransferRulesVO();
                String status=rs.getString("cat_status");
               if(!PretupsI.TRANSFER_RULE_STATUS_DELETE.equals(status)){ 
                rulesVO.setModule(rs.getString("module"));
                rulesVO.setNetworkCode(rs.getString("network_code"));
                rulesVO.setStatus(rs.getString("status"));
                rulesVO.setSenderSubscriberType(rs.getString("sender_subscriber_type"));
                rulesVO.setReceiverSubscriberType(rs.getString("receiver_subscriber_type"));
                rulesVO.setSenderServiceClassID(rs.getString("sender_service_class_id"));
                rulesVO.setReceiverServiceClassID(rs.getString("receiver_service_class_id"));
                rulesVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                rulesVO.setModifiedOn(rs.getDate("modified_on"));
                rulesVO.setModifiedBy(rs.getString("modified_by"));
                rulesVO.setCreatedOn(rs.getDate("created_on"));
                rulesVO.setCreatedBy(rs.getString("created_by"));
                rulesVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                ++index;
                rulesVO.setRowID("" + index);
                rulesVO.setSubServiceTypeId(rs.getString("sub_service"));
                rulesVO.setServiceType(rs.getString("service_type"));
                rulesVO.setGatewayCode(rs.getString("gateway_code"));
                rulesVO.setGradeCode(rs.getString("grade_code"));
                rulesVO.setCategoryCode(rs.getString("category_code"));
                rulesVO.setDomainCode(rs.getString("sender_subscriber_type"));
                rulesVO.setCategoryCodeDes(rs.getString("category_name"));
                rulesVO.setGradeCodeDes(rs.getString("grade_name"));
                rulesVO.setCellGroupIdDesc(rs.getString("cell_group_id"));
                transferRulesList.add(rulesVO);
               }
            }
        } 
        CellIdMgmtDAO cellIdMgmtDAO = new CellIdMgmtDAO();
		ArrayList<ListValueVO> groupCellIdList = (ArrayList<ListValueVO>)cellIdMgmtDAO.loadCellGroupID(p_con,p_networkCode);
		if (!(null == groupCellIdList && groupCellIdList.isEmpty())) {
			Map<String, String> groupCellIdMap = groupCellIdList.stream()
					.collect(Collectors.toMap(ListValueVO::getValue, ListValueVO::getLabel));
			((List<TransferRulesVO>) (List<?>) transferRulesList).forEach(transferRule -> {
				if (groupCellIdMap.containsKey(transferRule.getCellGroupIdDesc()))
					transferRule.setCellGroupIdDesc((String) groupCellIdMap.get(transferRule.getCellGroupIdDesc()));
			});

			}
        }catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadTransferRuleList]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadTransferRuleList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
           
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: transferRulesList.size=" + transferRulesList.size());
            }
        }
        return transferRulesList;
    }

    /**
     * Method isRecordModified.
     * This method is used to check that is the record modified during the
     * processing.
     * 
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @return boolean
     * @throws BTSLBaseException
     */
    private boolean isRecordModified(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        final String methodName = "isRecordModified";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:p_transferRulesVO=" + p_transferRulesVO.toString());
        }
        
        Boolean isCellGroupRequired = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED);
        Boolean isServiceProviderPromoAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW);
        
        boolean modified = false;
        final StringBuilder sqlRecordModified = new StringBuilder();
        sqlRecordModified.append("SELECT modified_on FROM transfer_rules ");
        sqlRecordModified.append("WHERE module = ? AND network_code=? AND sender_subscriber_type=? ");
        sqlRecordModified.append("AND receiver_subscriber_type=? AND sender_service_class_id=? AND ");
        sqlRecordModified.append("receiver_service_class_id=? AND sub_service=? AND service_type=?");
        // added by shashank for roadmap bug fix
        sqlRecordModified.append(" AND  RULE_TYPE=? ");
        if (isCellGroupRequired || isServiceProviderPromoAllow) {
            if (!BTSLUtil.isNullString(p_transferRulesVO.getRuleType()) && p_transferRulesVO.getRuleType().equalsIgnoreCase(PretupsI.TRANSFER_RULE_PROMOTIONAL)) {
                sqlRecordModified.append("AND sp_group_id = ? AND subscriber_status = ?");
            }
        }
        sqlRecordModified.append(" AND  GATEWAY_CODE=? AND category_code=? AND grade_code=?");
        java.sql.Timestamp newlastModified = null;
        if (p_transferRulesVO.getLastModifiedTime() == 0) {
            return false;
        }
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "QUERY=" + sqlRecordModified);
            }
            final String query = sqlRecordModified.toString();
           try(PreparedStatement pstmtSelect = p_con.prepareStatement(query);)
           {
            int i = 1;
            pstmtSelect.setString(i, p_transferRulesVO.getModule());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getNetworkCode());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getSenderSubscriberType());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getReceiverSubscriberType());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getSenderServiceClassID());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getReceiverServiceClassID());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getSubServiceTypeId());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getServiceType());
            i++;
            // added by shashank for roadmap bug fix
            if (!BTSLUtil.isNullString(p_transferRulesVO.getRuleType()) && p_transferRulesVO.getRuleType().equalsIgnoreCase(PretupsI.TRANSFER_RULE_PROMOTIONAL)) {
                pstmtSelect.setString(i, PretupsI.TRANSFER_RULE_PROMOTIONAL);
                i++;
            } else {
                pstmtSelect.setString(i, PretupsI.TRANSFER_RULE_NORMAL);
                i++;
            }
            if (isCellGroupRequired || isServiceProviderPromoAllow) {
                if (!BTSLUtil.isNullString(p_transferRulesVO.getRuleType()) && p_transferRulesVO.getRuleType().equalsIgnoreCase(PretupsI.TRANSFER_RULE_PROMOTIONAL)) {
                    if (p_transferRulesVO.getRuleLevel().equalsIgnoreCase(PretupsI.PROMOTIONAL_LEVEL_SERVICEGROUP)) {
                        pstmtSelect.setString(i, p_transferRulesVO.getSenderSubscriberType());
                        i++;
                    } else {
                        pstmtSelect.setString(i, p_transferRulesVO.getServiceGroupCode());
                        i++;
                    }
                    pstmtSelect.setString(i, p_transferRulesVO.getSubscriberStatus());
                    i++;
                }
            }
            if (!BTSLUtil.isNullString(p_transferRulesVO.getGatewayCode())) {
                pstmtSelect.setString(i, p_transferRulesVO.getGatewayCode());
                i++;
            } else {
                pstmtSelect.setString(i, PretupsI.ALL);
                i++;
            }
            if (!BTSLUtil.isNullString(p_transferRulesVO.getCategoryCode())) {
                pstmtSelect.setString(i, p_transferRulesVO.getCategoryCode());
                i++;
            } else {
                pstmtSelect.setString(i, PretupsI.ALL);
                i++;
            }
            if (!BTSLUtil.isNullString( p_transferRulesVO.getGradeCode())) {
                pstmtSelect.setString(i,  p_transferRulesVO.getGradeCode());
                i++;
            } else {
                pstmtSelect.setString(i, PretupsI.ALL);
                i++;
            }
            /**
           * pstmtSelect.setString(i, p_transferRulesVO.getCategoryCode());
           * i++;
           * pstmtSelect.setString(i, p_transferRulesVO.getGradeCode());
           * i++;
            */
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                newlastModified = rs.getTimestamp("modified_on");
            }
            // The record is not present because the record is modified by other
            // person and the
            // modification is done on the value of the primary key.
            else {
                modified = true;
                return true;
            }
            if (newlastModified.getTime() != p_transferRulesVO.getLastModifiedTime()) {
                modified = true;
            }
        }
           }
        }// end of try
        catch (SQLException sqe) {
            LOG.error(methodName, "SQLException:" + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isRecordModified]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception:" + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isRecordModified]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        
           
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exititng:modified=" + modified);
            }
        }// end of finally
        return modified;
    }// end recordModified

    /**
     * Method loadP2PTransferVOList.
     * 
     * @param p_con
     *            Connection
     * @param p_fromDate
     *            Date
     * @param p_toDate
     *            Date
     * @param p_networkCode
     *            String
     * @param p_networkCodeType
     *            String
     * @param p_serviceType
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadP2PReconciliationList(Connection p_con, Date p_fromDate, Date p_toDate, String p_networkCode, String p_networkCodeType, String p_serviceType) throws BTSLBaseException {

        final String methodName = "loadP2PReconciliationList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                "Entered p_fromDate:" + p_fromDate + " p_toDate: " + p_toDate + ",p_networkCode=" + p_networkCode + ",p_networkCodeType=" + p_networkCodeType + ",p_serviceType=" + p_serviceType);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        P2PTransferVO p2pTransferVO = null;
        final ArrayList p2pTransferVOList = new ArrayList();
        try {
            final String selectQuery = transferWebQry.loadP2PReconciliationListQry(p_networkCodeType);
            if (LOG.isDebugEnabled()) {
                LOG.debug("loadP2PTransferVOList", "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setString(i, PretupsI.P2P_ERRCODE_VALUS);
            i++;
            pstmtSelect.setString(i, PretupsI.KEY_VALUE_TYPE_REOCN);
            i++;
            pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            i++;
            pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(p_toDate, 1)));
            i++;
            pstmtSelect.setString(i, p_serviceType);
            i++;
            pstmtSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            i++;
            // by sandeep ID REC001
            // as now we are loading all the UNDERPROCESS or AMBIGUOUS txn. for
            // the reconciliation
            pstmtSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            i++;
            pstmtSelect.setString(i, p_networkCode);
            i++;
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                p2pTransferVO = new P2PTransferVO();

                p2pTransferVO.setProductName(rs.getString("short_name"));
                p2pTransferVO.setServiceName(rs.getString("name"));
                p2pTransferVO.setSenderName(rs.getString("user_name"));
                p2pTransferVO.setErrorMessage(rs.getString("value"));
                p2pTransferVO.setTransferID(rs.getString("transfer_id"));
                p2pTransferVO.setTransferDate(rs.getDate("transfer_date"));
                p2pTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                p2pTransferVO.setTransferDateStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time"))));
                p2pTransferVO.setNetworkCode(rs.getString("network_code"));
                p2pTransferVO.setSenderID(rs.getString("sender_id"));
                p2pTransferVO.setProductCode(rs.getString("product_code"));
                p2pTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                p2pTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                p2pTransferVO.setReceiverNetworkCode(rs.getString("receiver_network_code"));
                p2pTransferVO.setTransferValue(rs.getLong("transfer_value"));
                p2pTransferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                p2pTransferVO.setErrorCode(rs.getString("error_code"));
                p2pTransferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                p2pTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                p2pTransferVO.setReferenceID(rs.getString("reference_id"));
                p2pTransferVO.setPaymentMethodType(rs.getString("payment_method_type"));
                p2pTransferVO.setServiceType(rs.getString("service_type"));
                p2pTransferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
                p2pTransferVO.setLanguage(rs.getString("language"));
                p2pTransferVO.setCountry(rs.getString("country"));
                p2pTransferVO.setSkey(rs.getLong("skey"));
                p2pTransferVO.setSkeyGenerationTime(rs.getDate("skey_generation_time"));
                p2pTransferVO.setSkeySentToMsisdn(rs.getString("skey_sent_to_msisdn"));
                p2pTransferVO.setRequestThroughQueue(rs.getString("request_through_queue"));
                p2pTransferVO.setCreditBackStatus(rs.getString("credit_back_status"));
                p2pTransferVO.setQuantity(rs.getLong("quantity"));
                p2pTransferVO.setReconciliationFlag(rs.getString("reconciliation_flag"));
                p2pTransferVO.setReconciliationDate(rs.getDate("reconciliation_date"));
                p2pTransferVO.setReconciliationBy(rs.getString("reconciliation_by"));
                p2pTransferVO.setCreatedOn(rs.getDate("created_on"));
                p2pTransferVO.setCreatedBy(rs.getString("created_by"));
                p2pTransferVO.setModifiedOn(rs.getDate("modified_on"));
                p2pTransferVO.setModifiedBy(rs.getString("modified_by"));
                p2pTransferVO.setTransferStatus(rs.getString("txn_status"));
                p2pTransferVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                p2pTransferVO.setVersion(rs.getString("version"));
                p2pTransferVO.setCardGroupID(rs.getString("card_group_id"));
                p2pTransferVO.setSenderAccessFee(rs.getLong("sender_access_fee"));
                p2pTransferVO.setSenderTax1Type(rs.getString("sender_tax1_type"));
                p2pTransferVO.setSenderTax1Rate(rs.getDouble("sender_tax1_rate"));
                p2pTransferVO.setSenderTax1Value(rs.getLong("sender_tax1_value"));
                p2pTransferVO.setSenderTax2Type(rs.getString("sender_tax2_type"));
                p2pTransferVO.setSenderTax2Rate(rs.getDouble("sender_tax2_rate"));
                p2pTransferVO.setSenderTax2Value(rs.getLong("sender_tax2_value"));
                p2pTransferVO.setSenderTransferValue(rs.getLong("sender_transfer_value"));
                p2pTransferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
                p2pTransferVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                p2pTransferVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                p2pTransferVO.setReceiverTax1Value(rs.getLong("receiver_tax1_value"));
                p2pTransferVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                p2pTransferVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                p2pTransferVO.setReceiverTax2Value(rs.getLong("receiver_tax2_value"));
                p2pTransferVO.setReceiverValidity(rs.getInt("receiver_validity"));
                p2pTransferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
                p2pTransferVO.setReceiverBonusValue(rs.getLong("receiver_bonus_value"));
                p2pTransferVO.setReceiverGracePeriod(rs.getInt("receiver_grace_period"));
                p2pTransferVO.setTransferCategory(rs.getString("transfer_category"));
                p2pTransferVO.setReceiverBonusValidity(rs.getInt("receiver_bonus_validity"));
                p2pTransferVO.setCardGroupCode(rs.getString("card_group_code"));
                p2pTransferVO.setReceiverValPeriodType(rs.getString("receiver_valperiod_type"));
                p2pTransferVO.setCardReference(rs.getString("card_reference"));
                p2pTransferVO.setTxnStatus(rs.getString("transfer_status"));
                p2pTransferVO.setVoucherSerialNumber(rs.getString("VOUCHER_SERIAL_NUMBER"));
                p2pTransferVOList.add(p2pTransferVO);
            }

        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadP2PReconciliationList]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error("loadP2PTransferVOList", "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadP2PReconciliationList]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
           
            
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting p2pTransferVOList.size()=" + p2pTransferVOList.size());
            }
        }// end of finally

        return p2pTransferVOList;
    }

    /**
     * Method isTransferRuleExistforCardGroup.
     * This method is used to check that either transfer rule exists for the
     * card group or not.
     * This methos is called before deletion of card group
     * This method is added for CR00045
     * 
     * @param p_con
     *            Connection
     * @param p_cardgroupSetID
     *            String
     * @param p_module
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isTransferRuleExistforCardGroup(Connection p_con, String p_cardgroupSetID, String p_module) throws BTSLBaseException {
        final String methodName = "isTransferRuleExistforCardGroup";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_cardgroupSetID=" + p_cardgroupSetID + " p_module=" + p_module);
        }
       
        boolean isExist = false;
        int i = 1;
        try {
            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT 1 FROM transfer_rules ");
            selectQuery.append("WHERE module=? AND card_group_set_id=? AND status<>?  ");
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Select query:" + selectQuery);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery.toString());)
            {
            pstmtSelect.setString(i, p_module);
            i++;
            pstmtSelect.setString(i, p_cardgroupSetID);
            i++;
            pstmtSelect.setString(i, PretupsI.STATUS_DELETE);
            i++;
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                isExist = true;
            }
        }
            }
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isTransferRuleExistforCardGroup]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isTransferRuleExistforCardGroup]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
           
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting isExist=" + isExist);
            }
        }// end of finally
        return isExist;
    }

    /**
     * This method loads the list of promotional transfer rules defined on the
     * basis of passed network code of the user,
     * module, sender type (pre/post) and promotional level
     * (user/grade/category)
     * 
     * @author Varun
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_module
     *            String
     * @param p_sender_subscriber_type
     * @param p_rule_type
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadPromotionalTransferRulesList(Connection p_con, String p_networkCode, String p_module, String p_sender_subscriber_type, String p_rule_level, String p_dateRange) throws BTSLBaseException {
        final String methodName = "loadPromotionalTransferRulesList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName,
                "Entered:p_networkCode=" + p_networkCode + ",p_module=" + p_module + ",sender_subscriber_type=" + p_sender_subscriber_type + ",p_rule_type=" + p_rule_level);
        }
        
        Boolean isCellGroupRequired = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED);
        Boolean isServiceProviderPromoAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW);

        TransferRulesVO rulesVO = null;
        ArrayList transferRulesList = null;
        final StringBuilder strBuff = new StringBuilder();
        NetworkDAO networkDAO = null;
        strBuff.append("SELECT module, network_code, sender_subscriber_type, receiver_subscriber_type,status, ");
        strBuff.append("sender_service_class_id,receiver_service_class_id, card_group_set_id,  modified_on, ");
		strBuff.append("modified_by , created_on, created_by,sub_service,service_type,start_time,end_time,time_slab, date_range,");
        if (isCellGroupRequired || isServiceProviderPromoAllow) {
            strBuff.append("sp_group_id,subscriber_status ");
        } else {
            strBuff.append("ALLOWED_DAYS,ALLOWED_SERIES,DENIED_SERIES");
        }
        // added by akanksha for tigo_gtcr
        strBuff.append(" ,rule_type ");
        strBuff.append(" ,gateway_code ");
        strBuff.append(" FROM transfer_rules ");
        strBuff.append("WHERE network_code=? AND status <> ? AND module=? AND sender_subscriber_type=? AND rule_level = ? ");
		if(!BTSLUtil.isNullString(p_dateRange) && !p_dateRange.equals(PretupsI.PROMOTIONAL_TRNFR_TYPE_BOTH))
		strBuff.append(" AND date_range=? ");
        strBuff.append("ORDER BY modified_on,sender_subscriber_type, sub_service,service_type");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadPromotionalTransferRulesList", "QUERY sqlSelect=" + sqlSelect);
        }
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, PretupsI.TRANSFER_RULE_STATUS_DELETE);
            pstmt.setString(3, p_module);
            pstmt.setString(4, p_sender_subscriber_type);
            pstmt.setString(5, p_rule_level);
			if(!BTSLUtil.isNullString(p_dateRange) && !p_dateRange.equals(PretupsI.PROMOTIONAL_TRNFR_TYPE_BOTH))
            pstmt.setString(6, p_dateRange);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            transferRulesList = new ArrayList();
            int index = 0;
            while (rs.next()) {
                rulesVO = TransferRulesVO.getInstance();

                rulesVO.setModule(rs.getString("module"));
                rulesVO.setNetworkCode(rs.getString("network_code"));
                rulesVO.setStatus(rs.getString("status"));
                rulesVO.setSenderSubscriberType(rs.getString("sender_subscriber_type"));
                rulesVO.setReceiverSubscriberType(rs.getString("receiver_subscriber_type"));
                rulesVO.setSenderServiceClassID(rs.getString("sender_service_class_id"));
                rulesVO.setReceiverServiceClassID(rs.getString("receiver_service_class_id"));
                rulesVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                rulesVO.setModifiedOn(rs.getDate("modified_on"));
                rulesVO.setModifiedBy(rs.getString("modified_by"));
                rulesVO.setCreatedOn(rs.getDate("created_on"));
                rulesVO.setCreatedBy(rs.getString("created_by"));
                rulesVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                ++index;
                rulesVO.setRowID("" + index);
                rulesVO.setSubServiceTypeId(rs.getString("sub_service"));
                rulesVO.setServiceType(rs.getString("service_type"));
                rulesVO.setStartTime(rs.getTimestamp("start_time"));
                rulesVO.setEndTime(rs.getTimestamp("end_time"));
                rulesVO.setMultipleSlab(rs.getString("time_slab"));
				rulesVO.setDateRange(rs.getString("date_range"));
                // added by arvinder to get allowed days
                if (!(isCellGroupRequired || isServiceProviderPromoAllow)) {
                    if (rs.getString("ALLOWED_DAYS") != null) {
                        rulesVO.setAllowedDays(BTSLUtil.numberToWeekdays(rs.getString("ALLOWED_DAYS")).toString());// added
                        // by
                        // arvinder
                        // for
                        // allowed
                        // days

                    }// end//
                     // added by arvinder to get allowed and denied series
                    if (p_rule_level.equals(PretupsI.PROMOTIONAL_LEVEL_PREFIXID)) {
                        if (rs.getString("ALLOWED_SERIES") != null) {
                            networkDAO = NetworkDAO.getInstance();
                            rulesVO.setAllowedSeries(networkDAO.getSeries(p_con, rs.getString("ALLOWED_SERIES")));
                        }
                        if (rs.getString("DENIED_SERIES") != null) {
                            networkDAO = NetworkDAO.getInstance();
                            rulesVO.setDeniedSeries(networkDAO.getSeries(p_con, rs.getString("DENIED_SERIES")));
                        }
                    }// end/
                } else {
                    rulesVO.setSubscriberStatus(rs.getString("subscriber_status"));
                    rulesVO.setServiceGroupCode(rs.getString("sp_group_id"));
                }
                // added by akanksha for tigo_gtcr
                rulesVO.setRuleType(rs.getString("rule_type"));
                rulesVO.setGatewayCode(rs.getString("gateway_code"));
                transferRulesList.add(rulesVO);
            }
        }
        }catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadPromotionalTransferRulesList]", "",
                "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadPromotionalTransferRulesList]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            
            if (LOG.isDebugEnabled()) {
            	
                LOG.debug(methodName, "Exiting: transferRulesList.size=" + transferRulesList.size());
            }
        }
        return transferRulesList;
    }

    /**
     * Method addPromotionalTransferRule.
     * This method is used to add the record in the transfer_rules table .
     * 
     * @author Varun Kumar
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addPromotionalTransferRule(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        final String methodName = "addPromotionalTransferRule";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        }

        Boolean isCellGroupRequired = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED);
        Boolean isServiceProviderPromoAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW);

        int addCount = 0;
        int i = 1;
        try {
            final StringBuilder insertQueryBuff = new StringBuilder();
            insertQueryBuff.append("INSERT INTO transfer_rules (module,network_code,sender_subscriber_type,  ");
            insertQueryBuff.append("receiver_subscriber_type, sender_service_class_id,receiver_service_class_id, ");
            insertQueryBuff
                .append("created_on,created_by, modified_on, modified_by,card_group_set_id,status,sub_service,service_type,start_time,end_time,rule_type,rule_level, date_range, time_slab, ");
            if (isCellGroupRequired || isServiceProviderPromoAllow) {
                insertQueryBuff.append("sp_group_id,subscriber_status ");
            } else {
                insertQueryBuff.append("ALLOWED_DAYS,ALLOWED_SERIES,DENIED_SERIES ");
            }

            if(p_transferRulesVO.getModule().equalsIgnoreCase(PretupsI.P2P_MODULE))
            insertQueryBuff.append(", GATEWAY_CODE )");
            else
            	insertQueryBuff.append(" )");
            
            insertQueryBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");
            if (isCellGroupRequired || isServiceProviderPromoAllow) {
                insertQueryBuff.append("?,? ");
            } else {
                insertQueryBuff.append("?,?,? ");
            }
            
            if(p_transferRulesVO.getModule().equalsIgnoreCase(PretupsI.P2P_MODULE))
            insertQueryBuff.append(", ? )");
            else
            insertQueryBuff.append(" )");
            

            final String insertQuery = insertQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Insert query:" + insertQuery);
            }
            try(PreparedStatement pstmtInsert = p_con.prepareStatement(insertQuery);)
            {
            pstmtInsert.setString(i, p_transferRulesVO.getModule());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getNetworkCode());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getSenderSubscriberType());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getReceiverSubscriberType());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getSenderServiceClassID());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getReceiverServiceClassID());
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getCreatedOn()));
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getCreatedBy());
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getModifiedOn()));
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getModifiedBy());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getCardGroupSetID());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getStatus());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getSubServiceTypeId());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getServiceType());
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getStartTime()));
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getEndTime()));
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getRuleType());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getRuleLevel());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getSelectRangeType());
            i++;
            pstmtInsert.setString(i, p_transferRulesVO.getMultipleSlab());
            i++;
            if (!(isCellGroupRequired || isServiceProviderPromoAllow)) {
                pstmtInsert.setString(i, p_transferRulesVO.getAllowedDays());// added
                i++; // by
                // arvinder
                // for
                // allowed
                // days
                pstmtInsert.setString(i, p_transferRulesVO.getAllowedSeries());// added
                i++; // by
                // arvinder
                // for
                // allowed
                // series
                pstmtInsert.setString(i, p_transferRulesVO.getDeniedSeries());// added
                i++; // by
                // arvinder
                // for
                // denied
                // series
            } else {
                if (p_transferRulesVO.getPromotionCode().equalsIgnoreCase(PretupsI.PROMOTIONAL_LEVEL_SERVICEGROUP)) {
                    pstmtInsert.setString(i, p_transferRulesVO.getSenderSubscriberType());
                    i++;
                } else {
                    pstmtInsert.setString(i, p_transferRulesVO.getServiceGroupCode());
                    i++;
                }
                pstmtInsert.setString(i, p_transferRulesVO.getSubscriberStatus());
                i++;
            }

            // added for gateway
            if(p_transferRulesVO.getModule().equalsIgnoreCase(PretupsI.P2P_MODULE))
            pstmtInsert.setString(i, p_transferRulesVO.getGatewayCode());
            
            addCount = pstmtInsert.executeUpdate();
        }
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addPromotionalTransferRule]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addPromotionalTransferRule]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting addCount=" + addCount);
            }
        }// end of finally
        return addCount;
    }

    /**
     * Method isPromotionalTransferRuleExist.
     * This method is used to check the uniqueness of the transfer rule in the
     * transfer_rules table .
     * 
     * @author Varun Kumar
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isPromotionalTransferRuleExist(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        final String methodName = "isPromotionalTransferRuleExist";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        }
        
        boolean isExist = false;
        int i = 1;
        try {
            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT 1 FROM transfer_rules ");
            selectQuery.append("WHERE module=? AND network_code=? AND sender_subscriber_type=? AND receiver_subscriber_type=?  ");
            selectQuery.append("AND sender_service_class_id=? AND receiver_service_class_id=? AND sub_service=? AND service_type=? AND rule_Level=?");
         
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Select query:" + selectQuery);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery.toString());)
            {
            pstmtSelect.setString(i, p_transferRulesVO.getModule());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getNetworkCode());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getSenderSubscriberType());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getReceiverSubscriberType());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getSenderServiceClassID());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getReceiverServiceClassID());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getSubServiceTypeId());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getServiceType());
            i++;
            pstmtSelect.setString(i, p_transferRulesVO.getRuleLevel());
            i++;
           

            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                isExist = true;
            }
        }
            }
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isPromotionalTransferRuleExist]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isPromotionalTransferRuleExist]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
           
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting isExist=" + isExist);
            }
        }// end of finally
        return isExist;
    }

    /*
     * Update the promotional transfer rule if exist.By ranjana
     */

    public boolean isPromotionalTransferRuleUpdates(Connection p_con, TransferRulesVO p_transferRulesVO, Date p_currentDate) throws BTSLBaseException {
        final String methodName = "isPromotionalTransferRuleExist";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        }
        
        boolean isUpdate = false;
        int i = 1;
        int updateCount = 0;
        try {

            final StringBuilder updateQuery = new StringBuilder();
            updateQuery.append("Update transfer_rules set start_time=? , end_time=?, time_slab=?, date_range=? ");
            updateQuery.append("WHERE module=? AND network_code=? AND sender_subscriber_type=? AND receiver_subscriber_type=?  ");
            updateQuery.append("AND sender_service_class_id=? AND receiver_service_class_id=? AND sub_service=? AND service_type=? AND rule_Level=?"); // AND
            
            updateQuery.append("AND start_time<? AND end_time<? ");
            final String update = updateQuery.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Entered updateQuery:" + update);
            }
            try(PreparedStatement pstmtUpdate = p_con.prepareStatement(update);)
            {
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getStartTime()));
            i++;
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getEndTime()));
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getMultipleSlab());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getSelectRangeType());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getModule());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getNetworkCode());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getSenderSubscriberType());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getReceiverSubscriberType());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getSenderServiceClassID());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getReceiverServiceClassID());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getSubServiceTypeId());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getServiceType());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getRuleLevel());
            i++;
          
            pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_currentDate));
            i++;
            pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_currentDate));
            i++;
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0) {
                isUpdate = true;
            }

        }
        }// end of try
        catch (SQLException sqle) {
            LOG.error("isPromotionalTransferRuleUpdates", "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isPromotionalTransferRuleUpdates]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isPromotionalTransferRuleUpdates", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error("isPromotionalTransferRuleUpdates", "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isPromotionalTransferRuleUpdates]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isPromotionalTransferRuleUpdates", "error.general.processing");
        }// end of catch
        finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug("isPromotionalTransferRuleUpdates", "Exiting isExist=" + isUpdate);
            }
        }// end of finally
        return isUpdate;
    }

    /**
     * Method updateTransferRule.
     * 
     * @author Varun Kumar
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @param deleteFlag
     *            boolean
     * @return int
     * @throws BTSLBaseException
     */
    public int updatePromotionalTransferRule(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        final String methodName = "updatePromotionalTransferRule";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        }
        
        Boolean isCellGroupRequired = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED);
        Boolean isServiceProviderPromoAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW);

        int updateCount = 0;
        int i = 1;
        try {
            final StringBuilder updateQueryBuff = new StringBuilder();
            updateQueryBuff.append("UPDATE transfer_rules SET  modified_on=?, modified_by=? ");
            updateQueryBuff.append(",card_group_set_id=? ,status=? ,start_time=? ,end_time=?, time_slab=?");
            if (!(isCellGroupRequired || isServiceProviderPromoAllow)) {
                updateQueryBuff.append(" ,ALLOWED_DAYS=?,ALLOWED_SERIES=?,DENIED_SERIES=?");
            }
            updateQueryBuff.append("WHERE module = ? AND network_code=? AND sender_subscriber_type=? ");
            updateQueryBuff.append("AND receiver_subscriber_type=? AND sender_service_class_id=? ");
            updateQueryBuff.append("AND receiver_service_class_id=? AND sub_service = ? AND service_type = ? AND rule_level=?");

            if (isCellGroupRequired || isServiceProviderPromoAllow) {
                updateQueryBuff.append(" AND sp_group_id = ? AND subscriber_status = ?");
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Update query:" + updateQueryBuff);
            }
            try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQueryBuff.toString());)
            {
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getModifiedOn()));
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getModifiedBy());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getCardGroupSetID());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getStatus());
            i++;
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getStartTime()));
            i++;
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getEndTime()));
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getMultipleSlab());
            i++;
            // added by arvinder to update existing allowed days
            if (!(isCellGroupRequired || isServiceProviderPromoAllow)) {
                if (p_transferRulesVO.getMallowedDays() != null) {
                    pstmtUpdate.setString(i, p_transferRulesVO.getMallowedDays());
                    i++;
                } else {
                    pstmtUpdate.setString(i, BTSLUtil.weekDaysToNumber(p_transferRulesVO.getAllowedDays()).toString());
                    i++;
                }// end//
                pstmtUpdate.setString(i, p_transferRulesVO.getAllowedSeries());// added
                i++; // by
                // arvinder
                // to
                // update
                // existing
                // allowed
                // series
                pstmtUpdate.setString(i, p_transferRulesVO.getDeniedSeries());// added
                i++; // by
                // arvinder
                // to
                // update
                // existing
                // denies
                // series
            }

            pstmtUpdate.setString(i, p_transferRulesVO.getModule());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getNetworkCode());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getSenderSubscriberType());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getReceiverSubscriberType());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getSenderServiceClassID());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getReceiverServiceClassID());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getSubServiceTypeId());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getServiceType());
            i++;
            pstmtUpdate.setString(i, p_transferRulesVO.getRuleLevel());
            i++;
            if (isCellGroupRequired || isServiceProviderPromoAllow) {
                if (p_transferRulesVO.getRuleLevel().equalsIgnoreCase(PretupsI.PROMOTIONAL_LEVEL_SERVICEGROUP)) {
                    pstmtUpdate.setString(i, p_transferRulesVO.getSenderSubscriberType());
                    i++;
                } else {
                    pstmtUpdate.setString(i, p_transferRulesVO.getServiceGroupCode());
                    i++;
                }
                pstmtUpdate.setString(i, p_transferRulesVO.getSubscriberStatus());
                i++;
            }
            // for the checking is the record modified during the transaction.
            final boolean modified = this.isRecordModified(p_con, p_transferRulesVO);
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = pstmtUpdate.executeUpdate();
        }
        }catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updatePromotionalTransferRule]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updatePromotionalTransferRule]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            
            
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting updateCount=" + updateCount);
            }
        }// end of finally
        return updateCount;
    }

    /**
     * Method deleteTransferRule.
     * 
     * @author Varun Kumar
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @param deleteFlag
     *            boolean
     * @return int
     * @throws BTSLBaseException
     */
    public int deletePromotionalTransferRule(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        final String methodName = "deletePromotionalTransferRule";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        }
        
        int deleteCount = 0;
        int i = 1;
        try {
            final StringBuilder deleteQueryBuff = new StringBuilder();
            deleteQueryBuff.append("DELETE FROM transfer_rules ");
            deleteQueryBuff.append("WHERE module = ? AND network_code=? AND sender_subscriber_type=? ");
            deleteQueryBuff.append("AND receiver_subscriber_type=? AND sender_service_class_id=? AND ");
            deleteQueryBuff.append("receiver_service_class_id=? AND sub_service=? AND service_type=? AND rule_level=?");
            final String deleteQuery = deleteQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "delete query:" + deleteQuery);
            }
            try(PreparedStatement pstmtDelete = p_con.prepareStatement(deleteQuery);)
            {
            pstmtDelete.setString(i, p_transferRulesVO.getModule());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getNetworkCode());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getSenderSubscriberType());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getReceiverSubscriberType());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getSenderServiceClassID());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getReceiverServiceClassID());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getSubServiceTypeId());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getServiceType());
            i++;
            pstmtDelete.setString(i, p_transferRulesVO.getRuleLevel());
            i++;
            // for the checking is the record modified during the transaction.
            final boolean modified = this.isRecordModified(p_con, p_transferRulesVO);
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            deleteCount = pstmtDelete.executeUpdate();
        }
        }catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[deletePromotionalTransferRule]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error("deleteTransferRule", "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[deletePromotionalTransferRule]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
           
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting deleteCount=" + deleteCount);
            }
        }// end of finally
        return deleteCount;
    }

    /**
     * Method for loading User List.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_parentGraphDomainCode
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadUserList(Connection p_con, String p_parentGraphDomainCode, String p_networkCode, String p_categoryCode, String p_username) throws BTSLBaseException {
        final String methodName = "loadUserList";
        if (LOG.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_parentGraphDomainCode= ");
        	msg.append(p_parentGraphDomainCode);
        	msg.append(",p_networkCode= ");
        	msg.append(p_networkCode);      
        	msg.append(",p_categoryCode= ");
        	msg.append(p_categoryCode);
        	msg.append(",p_username= ");
        	msg.append(p_username);
        	
        	String message=msg.toString();
            LOG.debug(methodName,message);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final String sqlSelect = transferWebQry.loadUserListQry();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        final ArrayList list = new ArrayList();
        try {
            
            pstmt = (PreparedStatement) p_con.prepareStatement(sqlSelect);
            int i = 1;
            
            
            pstmt.setString(i, p_parentGraphDomainCode);
            i++;
         
            pstmt.setString(i, PretupsI.USER_TYPE_CHANNEL);
            i++;
            pstmt.setString(i, p_networkCode);
            i++;
            pstmt.setString(i, p_categoryCode);
            i++;
            pstmt.setString(i, p_username);
            i++;
            rs = pstmt.executeQuery();
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new UserVO();
                userVO.setUserID(rs.getString("user_id"));
                userVO.setUserName(rs.getString("user_name"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userVO.setLoginID(rs.getString("login_id"));
                list.add(userVO);
            }

        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadUserList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadUserList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
            
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: userList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method addPromotionalTransferRuleFile.
     * This method is used to add Batch record in the transfer_rules table .
     * 
     * @author Sanjeew
     * @param p_con
     *            Connection
     * @param ArrayList
     *            transfer_rule_list, ArrayList error_Vo_List
     * @return int
     * @throws BTSLBaseException
     */
    public void addPromotionalTransferRuleFile(Connection p_con, ArrayList p_transferRuleList, ArrayList p_errorVoList, MessageResources p_messages, Locale p_locale, String p_promotionLevel, String p_category, String geodomainCd) throws BTSLBaseException {
        final String methodName = "addPromotionalTransferRuleFile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferRuleList.size():" + p_transferRuleList.size());
        }
        
        Boolean isCellGroupRequired = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED);
        Boolean isServiceProviderPromoAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW);
        
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtMSISDN = null;
        final ResultSet rsInsert = null;
        ResultSet rsSelect = null;
        ResultSet rsMSISDN = null;
        int addCount = 0;
        int i = 1;
        TransferRulesVO transferRulesVO = null;
        try {
            String selectMSISDN=transferWebQry.addPromotionalTransferRuleFileQry();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Select User ID:" + selectMSISDN);
            }
            pstmtMSISDN = p_con.prepareStatement(selectMSISDN);

            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT 1 FROM transfer_rules ");
            selectQuery.append("WHERE module=? AND network_code=? AND sender_subscriber_type=? AND receiver_subscriber_type=?  ");
            selectQuery.append("AND sender_service_class_id=? AND receiver_service_class_id=? AND sub_service=? AND service_type=? AND rule_Level=?"); // AND
            
            if (isCellGroupRequired || isServiceProviderPromoAllow) {
                selectQuery.append(" AND sp_group_id = ? AND subscriber_status = ?");
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());

            final StringBuilder insertQueryBuff = new StringBuilder();
            insertQueryBuff.append("INSERT INTO transfer_rules (module,network_code,sender_subscriber_type, ");
            insertQueryBuff.append("receiver_subscriber_type, sender_service_class_id,receiver_service_class_id, ");
            insertQueryBuff
                .append("created_on,created_by, modified_on, modified_by,card_group_set_id,status,sub_service,service_type,START_TIME,END_TIME,RULE_TYPE,RULE_LEVEL,DATE_RANGE,TIME_SLAB ");
            if (isCellGroupRequired || isServiceProviderPromoAllow) {
                insertQueryBuff.append(" ,sp_group_id,subscriber_status)");
            } else {
                insertQueryBuff.append(" )");
            }

            insertQueryBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
            if (isCellGroupRequired || isServiceProviderPromoAllow) {
                insertQueryBuff.append(",?,? )");
            } else {
                insertQueryBuff.append(" )");
            }

            final String insertQuery = insertQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Insert query:" + insertQuery);
            }
            pstmtInsert = p_con.prepareStatement(insertQuery);

            ListValueVO errorVO = null;
            if (p_errorVoList == null) {
                p_errorVoList = new ArrayList();
            }
            int transferRuleListSize = p_transferRuleList.size();
            for (int s = 0; s < transferRuleListSize; s++) {
                i = 1;
                transferRulesVO = (TransferRulesVO) p_transferRuleList.get(s);
                if (p_promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_USR)) {
                    pstmtMSISDN.clearParameters();
                    pstmtMSISDN.setString(i, geodomainCd);
                    i++;
                    pstmtMSISDN.setString(i, transferRulesVO.getNetworkCode());
                    i++;
                    pstmtMSISDN.setString(i, p_category);
                    i++;
                    pstmtMSISDN.setString(i, transferRulesVO.getSenderSubscriberType());
                    i++;
                    try {
                        rsMSISDN = pstmtMSISDN.executeQuery();
                        if (rsMSISDN.next()) {
                            transferRulesVO.setSenderSubscriberType(rsMSISDN.getString(1));
                        } else {
                            errorVO = new ListValueVO("", transferRulesVO.getRowID(), p_messages.getMessage(p_locale,
                                "promotionaltransferrule.addpromotionaltransferrulerile.error.trfrule.useriddoesnotexist"));
                            p_errorVoList.add(errorVO);
                            continue;
                        }
                    } catch (SQLException sqle) {
                        errorVO = new ListValueVO("", transferRulesVO.getRowID(), p_messages.getMessage(p_locale,
                            "promotionaltransferrule.addpromotionaltransferrulerile.error.trfrule.useriddoesnotexist"));
                        p_errorVoList.add(errorVO);
                        LOG.errorTrace(methodName, sqle);
                        continue;
                    }// end of catch
                }

                pstmtSelect.clearParameters();
                i = 1;
                pstmtSelect.setString(i, transferRulesVO.getModule());
                i++;
                pstmtSelect.setString(i, transferRulesVO.getNetworkCode());
                i++;
                pstmtSelect.setString(i, transferRulesVO.getSenderSubscriberType());
                i++;
                pstmtSelect.setString(i, transferRulesVO.getReceiverSubscriberType());
                i++;
                pstmtSelect.setString(i, transferRulesVO.getSenderServiceClassID());
                i++;
                pstmtSelect.setString(i, transferRulesVO.getReceiverServiceClassID());
                i++;
                pstmtSelect.setString(i, transferRulesVO.getSubServiceTypeId());
                i++;
                pstmtSelect.setString(i, transferRulesVO.getServiceType());
                i++;
                pstmtSelect.setString(i, transferRulesVO.getRuleLevel());
                i++;
                if (isCellGroupRequired || isServiceProviderPromoAllow) {
                    if (p_promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_SERVICE)) {
                        pstmtSelect.setString(i, transferRulesVO.getSenderSubscriberType());
                        i++;
                    } else {
                        pstmtSelect.setString(i, transferRulesVO.getServiceGroupCode());
                        i++;
                    }
                    pstmtSelect.setString(i, transferRulesVO.getSubscriberStatus());
                    i++;
                }

                try {
                    rsSelect = pstmtSelect.executeQuery();
                    if (rsSelect.next()) {
                        errorVO = new ListValueVO("", transferRulesVO.getRowID(), p_messages.getMessage(p_locale,
                            "promotionaltransferrule.addpromotionaltransferrulerile.error.trfrule.allreadyexist"));
                        p_errorVoList.add(errorVO);
                        continue;
                    } else {
                        i = 1;
                        addCount = 0;
                        pstmtInsert.clearParameters();
                        pstmtInsert.setString(i, transferRulesVO.getModule());
                        i++;
                        pstmtInsert.setString(i, transferRulesVO.getNetworkCode());
                        i++;
                        pstmtInsert.setString(i, transferRulesVO.getSenderSubscriberType());
                        i++;
                        pstmtInsert.setString(i, transferRulesVO.getReceiverSubscriberType());
                        i++;
                        pstmtInsert.setString(i, transferRulesVO.getSenderServiceClassID());
                        i++;
                        pstmtInsert.setString(i, transferRulesVO.getReceiverServiceClassID());
                        i++;
                        pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(transferRulesVO.getCreatedOn()));
                        i++;
                        pstmtInsert.setString(i, transferRulesVO.getCreatedBy());
                        i++;
                        pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(transferRulesVO.getModifiedOn()));
                        i++;
                        pstmtInsert.setString(i, transferRulesVO.getModifiedBy());
                        i++;
                        pstmtInsert.setString(i, transferRulesVO.getCardGroupSetID());
                        i++;
                        pstmtInsert.setString(i, transferRulesVO.getStatus());
                        i++;
                        pstmtInsert.setString(i, transferRulesVO.getSubServiceTypeId());
                        i++;
                        pstmtInsert.setString(i, transferRulesVO.getServiceType());
                        i++;
                        pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(transferRulesVO.getStartTime()));
                        i++;
                        pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(transferRulesVO.getEndTime()));
                        i++;
                        pstmtInsert.setString(i, transferRulesVO.getRuleType());
                        i++;
                        pstmtInsert.setString(i, transferRulesVO.getRuleLevel());
                        i++;
                        // //ranjana
                        pstmtInsert.setString(i, transferRulesVO.getSelectRangeType());
                        i++;
                        pstmtInsert.setString(i, transferRulesVO.getMultipleSlab());
                        i++;
                        if (isCellGroupRequired || isServiceProviderPromoAllow) {
                            if (p_promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_SERVICE)) {
                                pstmtInsert.setString(i, transferRulesVO.getSenderSubscriberType());
                                i++;
                            } else {
                                pstmtInsert.setString(i, transferRulesVO.getServiceGroupCode());
                                i++;
                            }
                            pstmtInsert.setString(i, transferRulesVO.getSubscriberStatus());
                            i++;
                        }

                        addCount = pstmtInsert.executeUpdate();
                        if (addCount == 0) {
                            errorVO = new ListValueVO("", transferRulesVO.getRowID(), p_messages.getMessage(p_locale,
                                "promotionaltransferrule.addpromotionaltransferrulerile.error.trfrule.insertfailed"));
                            p_errorVoList.add(errorVO);
                            continue;
                        }
                    }
                } catch (SQLException sqle) {
                    LOG.errorTrace(methodName, sqle);
                    errorVO = new ListValueVO("", transferRulesVO.getRowID(), p_messages.getMessage(p_locale,
                        "promotionaltransferrule.addpromotionaltransferrulerile.error.trfrule.insertfailed"));
                    p_errorVoList.add(errorVO);
                    continue;
                }// end of catch
            }
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addPromotionalTransferRuleFile]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error("addTransferRule", "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addPromotionalTransferRuleFile]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	try{
            	if (rsInsert!= null){
            		rsInsert.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing result set.", e);
            }
        	try{
            	if (rsMSISDN!= null){
            		rsMSISDN.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing result set.", e);
            }
        	try{
            	if (rsSelect!= null){
            		rsSelect.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing result set.", e);
            }
        	try{
        		if (pstmtInsert!= null){
        			pstmtInsert.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	try{
        		if (pstmtMSISDN!= null){
        			pstmtMSISDN.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
                       
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting addCount=" + addCount);
            }
        }// end of finally
    }
    

    public ArrayList getSubscriberStatusList(Connection p_con, String lookup_type) throws BTSLBaseException {
        final String methodName = "getSubscriberStatusList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:");
        }
        final ArrayList subscriberStatusList = new ArrayList();
       
        TransferVO transferVO = null;
       
        try {
            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append(" SELECT sl.SUB_LOOKUP_NAME,sl.LOOKUP_CODE ");
            selectQuery.append(" FROM SUB_LOOKUPS sl,LOOKUPS l where sl.LOOKUP_TYPE=? and sl.LOOKUP_CODE=l.LOOKUP_CODE and sl.LOOKUP_TYPE=l.LOOKUP_TYPE  ");

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Query=" + selectQuery);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery.toString());)
            {
            pstmtSelect.setString(1, lookup_type);
           try(ResultSet rs = pstmtSelect.executeQuery();)
           {
            while (rs.next()) {
                transferVO = new TransferVO();
                transferVO.setSubscriberStatus(SqlParameterEncoder.encodeParams(rs.getString("SUB_LOOKUP_NAME")));
                transferVO.setServiceType(SqlParameterEncoder.encodeParams(rs.getString("LOOKUP_CODE")));
                subscriberStatusList.add(transferVO);
            }
        } 
            }
        }catch (SQLException sqe) {
            LOG.error(methodName, "SQLException:" + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[getSubscriberStatusList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error(methodName, "Exception:" + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[getSubscriberStatusList]", "", "", "",
                "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } finally {
        	
            
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting:list size=" + subscriberStatusList.size());
            }
        }

        return subscriberStatusList;
    }
    
    public ArrayList loadTransferRuleList1(Connection p_con, String p_networkCode, String p_module ,String status1 ,String gatewayCode ,String domain ,String category ,String grade) throws BTSLBaseException {
        final String methodName = "loadTransferRuleList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:p_networkCode=" + p_networkCode + ",p_module=" + p_module);
        }
       
        
        TransferRulesVO rulesVO = null;
        ArrayList transferRulesList = null;
        String sqlSelect = transferWebQry.loadTransferRuleListQry1(status1,gatewayCode,domain,category,grade);
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadTransferRuleList", "QUERY sqlSelect=" + sqlSelect);
        }
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
        	int i = 1;
            pstmt.setString(i, p_networkCode);
            i++;
            pstmt.setString(i, p_module);
            i++;
            pstmt.setString(i, PretupsI.TRANSFER_RULE_NORMAL);
            i++;
            if(!status1.equals(PretupsI.ALL)) {
            pstmt.setString(i, status1 );
            i++;
            }
            if(!gatewayCode.equals(PretupsI.ALL)) {
            pstmt.setString(i, gatewayCode);
            i++;
            }
            if(!domain.equals(PretupsI.ALL)) {
            pstmt.setString(i, domain);
            i++;
            }
            if(!category.equals(PretupsI.ALL)) {
            pstmt.setString(i, category);
            i++;
            }
            if(!grade.equals(PretupsI.ALL)) {
            pstmt.setString(i, grade);
            i++;
            }
            try(ResultSet rs = pstmt.executeQuery();)
            {
            transferRulesList = new ArrayList();
            int index = 0;
            while (rs.next()) {
                rulesVO = new TransferRulesVO();
                String status=rs.getString("cat_status");  
                rulesVO.setModule(rs.getString("module"));
                rulesVO.setNetworkCode(rs.getString("network_code"));
                rulesVO.setStatus(rs.getString("status"));
                rulesVO.setSenderSubscriberType(rs.getString("sender_subscriber_type"));
                rulesVO.setReceiverSubscriberType(rs.getString("receiver_subscriber_type"));
                rulesVO.setSenderServiceClassID(rs.getString("sender_service_class_id"));
                rulesVO.setReceiverServiceClassID(rs.getString("receiver_service_class_id"));
                rulesVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                rulesVO.setModifiedOn(rs.getDate("modified_on"));
                rulesVO.setModifiedBy(rs.getString("modified_by"));
                rulesVO.setCreatedOn(rs.getDate("created_on"));
                rulesVO.setCreatedBy(rs.getString("created_by"));
                rulesVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                ++index;
                rulesVO.setRowID("" + index);
                rulesVO.setSubServiceTypeId(rs.getString("sub_service"));
                rulesVO.setServiceType(rs.getString("service_type"));
                rulesVO.setGatewayCode(rs.getString("gateway_code"));
                rulesVO.setGradeCode(rs.getString("grade_code"));
                rulesVO.setCategoryCode(rs.getString("category_code"));
                rulesVO.setDomainCode(rs.getString("sender_subscriber_type"));
                rulesVO.setDomainCodeDes(rs.getString("domain_name"));
                rulesVO.setCategoryCodeDes(rs.getString("category_name"));
                rulesVO.setGradeCodeDes(rs.getString("grade_name"));
                rulesVO.setCellGroupIdDesc(rs.getString("cell_group_id"));
                rulesVO.setCardGroupSetIDDes(rs.getString("card_group_set_name"));
                transferRulesList.add(rulesVO);
               
            }
        } 
        CellIdMgmtDAO cellIdMgmtDAO = new CellIdMgmtDAO();
		ArrayList<ListValueVO> groupCellIdList = (ArrayList<ListValueVO>)cellIdMgmtDAO.loadCellGroupID(p_con,p_networkCode);
		if (!(null == groupCellIdList && groupCellIdList.isEmpty())) {
			Map<String, String> groupCellIdMap = groupCellIdList.stream()
					.collect(Collectors.toMap(ListValueVO::getValue, ListValueVO::getLabel,(existingValue, newValue) -> existingValue));
			((List<TransferRulesVO>) (List<?>) transferRulesList).forEach(transferRule -> {
				if (groupCellIdMap.containsKey(transferRule.getCellGroupIdDesc()))
					transferRule.setCellGroupIdDesc((String) groupCellIdMap.get(transferRule.getCellGroupIdDesc()));
			});

			}
        }catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadTransferRuleList]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadTransferRuleList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
           
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: transferRulesList.size=" + transferRulesList.size());
            }
        }
        return transferRulesList;
    }
    
    
    public ArrayList loadPromoUserList(Connection p_con, String p_parentGraphDomainCode, String p_networkCode, String p_categoryCode, String p_username) throws BTSLBaseException {
        final String methodName = "loadPromoUserList";
        if (LOG.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_parentGraphDomainCode= ");
        	msg.append(p_parentGraphDomainCode);
        	msg.append(",p_networkCode= ");
        	msg.append(p_networkCode);      
        	msg.append(",p_categoryCode= ");
        	msg.append(p_categoryCode);
        	msg.append(",p_username= ");
        	msg.append(p_username);
        	
        	String message=msg.toString();
            LOG.debug(methodName,message);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final String sqlSelect = transferWebQry.loadUserListQry();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        final ArrayList list = new ArrayList();
        try {
            
            pstmt = (PreparedStatement) p_con.prepareStatement(sqlSelect);
            int i = 1;
            
            
            pstmt.setString(i, p_parentGraphDomainCode);
            i++;
         
            pstmt.setString(i, PretupsI.USER_TYPE_CHANNEL);
            i++;
            pstmt.setString(i, p_networkCode);
            i++;
            pstmt.setString(i, p_categoryCode);
            i++;
            pstmt.setString(i, p_username);
            i++;
            rs = pstmt.executeQuery();
            PromoParentUserVO userVO = null;
            while (rs.next()) {
                userVO = new PromoParentUserVO();
                userVO.setUserID(rs.getString("user_id"));
                userVO.setUserName(rs.getString("user_name"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userVO.setLoginID(rs.getString("login_id"));
                list.add(userVO);
            }

        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadPromoUserList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadPromoUserList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
            
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: userList size=" + list.size());
            }
        }
        return list;
    }
    
    
    
    
    public ArrayList searchPromotionalTransferRulesList(Connection p_con, String p_networkCode, String p_module,String p_senderSubscriberType, SearchPromoTransferReqVO searchPromoTransferReqVO) throws BTSLBaseException {
        final String methodName = "loadPromotionalTransferRulesList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName,
                "Entered:p_networkCode=" + p_networkCode + ",p_module=" + p_module );
        }
        
        Boolean isCellGroupRequired = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED);
        Boolean isServiceProviderPromoAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW);

        PromoTransferSearchVO rulesVO = null;
        ArrayList transferRulesList = null;
        final StringBuilder strBuff = new StringBuilder();
        NetworkDAO networkDAO = null;
        
        strBuff.append("select rtype.lookup_name typeDisplay , sc.service_class_name ServiceClass , tr.subscriber_status ,  spg.group_name  serviceProvGroup , stat.lookup_name statusDesc , st.name  serviceTypeDesc, stsm.selector_name subserviceDesc,cgs.card_group_set_name cardGroupName, ");
        strBuff.append( " tr.start_time,tr.end_time,tr.time_slab, " );
        strBuff.append(" tr.module, tr.network_code, tr.sender_subscriber_type, tr.receiver_subscriber_type,tr.status, ");
        strBuff.append(" tr.sender_service_class_id,tr.receiver_service_class_id, tr.card_group_set_id,  tr.modified_on, ");
		strBuff.append(" tr.modified_by , tr.created_on, tr.created_by,tr.sub_service,tr.service_type, tr.date_range, ");
        if (isCellGroupRequired || isServiceProviderPromoAllow) {
            strBuff.append("tr.sp_group_id,tr.subscriber_status ");
        } else {
            strBuff.append("tr.ALLOWED_DAYS,tr.ALLOWED_SERIES,tr.DENIED_SERIES");
        }
        // added by akanksha for tigo_gtcr
        strBuff.append(" ,tr.rule_type ");
        strBuff.append(" ,tr.gateway_code ");
        strBuff.append(" FROM transfer_rules tr ,");
        strBuff.append(" service_classes sc,service_type st,lookups rtype ,service_type_selector_mapping stsm,service_provider_groups spg  ,lookups stat,card_group_set cgs ");
        strBuff.append(" WHERE tr.network_code=? AND tr.status <> ? AND tr.module=? AND tr.sender_subscriber_type=? AND tr.rule_level = ? ");
        strBuff.append(" and tr.receiver_service_class_id = sc.service_class_id and tr.service_type=st.service_type ");
        strBuff.append(" and rtype.lookup_type = 'SUBTP'  and tr.receiver_subscriber_type=rtype.lookup_code ");
        strBuff.append(" and stsm.service_type=tr.service_type  and stsm.selector_code =tr.sub_service ");
        if (isCellGroupRequired || isServiceProviderPromoAllow)
        	strBuff.append(" and tr.sp_group_id = spg.group_id "); 
        strBuff.append(" and  stat.lookup_code= tr.status  and stat.lookup_type ='STAT' ");
        strBuff.append(" and tr.card_group_set_id = cgs.card_group_set_id ");
        strBuff.append(" ORDER BY tr.modified_on,tr.sender_subscriber_type, tr.sub_service,tr.service_type");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadPromotionalTransferRulesList", "QUERY sqlSelect=" + sqlSelect);
        }
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, PretupsI.TRANSFER_RULE_STATUS_DELETE);
            pstmt.setString(3, p_module);
            pstmt.setString(4, p_senderSubscriberType);
            pstmt.setString(5, searchPromoTransferReqVO.getOptionTab());
			
            try(ResultSet rs = pstmt.executeQuery();)
            {
            transferRulesList = new ArrayList();
            int index = 0;
            while (rs.next()) {
                rulesVO = PromoTransferSearchVO.getInstancePromoTransferSearchVO();
                rulesVO.setTypeDisplay(rs.getString("typeDisplay"));
                rulesVO.setServiceClassName(rs.getString("ServiceClass"));
                rulesVO.setSubscriberStatus(rs.getString("subscriber_status"));
                rulesVO.setServiceProvideGroup(rs.getString("serviceProvGroup"));
                rulesVO.setStatusDesc(rs.getString("statusDesc"));
                rulesVO.setServiceTypeDesc(rs.getString("serviceTypeDesc"));
                rulesVO.setSubServiceDesc(rs.getString("subserviceDesc"));
                rulesVO.setCardGroupSetName(rs.getString("cardGroupName"));
                rulesVO.setModule(rs.getString("module"));
                rulesVO.setNetworkCode(rs.getString("network_code"));
                rulesVO.setStatus(rs.getString("status"));
                rulesVO.setSenderSubscriberType(rs.getString("sender_subscriber_type"));
                rulesVO.setReceiverSubscriberType(rs.getString("receiver_subscriber_type"));
                rulesVO.setSenderServiceClassID(rs.getString("sender_service_class_id"));
                rulesVO.setReceiverServiceClassID(rs.getString("receiver_service_class_id"));
                rulesVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                rulesVO.setModifiedOn(rs.getDate("modified_on"));
                rulesVO.setModifiedBy(rs.getString("modified_by"));
                rulesVO.setCreatedOn(rs.getDate("created_on"));
                rulesVO.setCreatedBy(rs.getString("created_by"));
                rulesVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                ++index;
                rulesVO.setRowID("" + index);
                rulesVO.setSubServiceTypeId(rs.getString("sub_service"));
                rulesVO.setServiceType(rs.getString("service_type"));
                rulesVO.setStartTime(rs.getTimestamp("start_time"));
                rulesVO.setApplicableFrom(BTSLUtil.getDateTimeStringFromDate(rs.getDate("start_time"), ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT))));
                rulesVO.setApplicableTO(BTSLUtil.getDateTimeStringFromDate(rs.getDate("end_time"), ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT))));
                rulesVO.setEndTime(rs.getTimestamp("end_time"));
                rulesVO.setMultipleSlab(rs.getString("time_slab"));
				rulesVO.setDateRange(rs.getString("date_range"));
                // added by arvinder to get allowed days
                if (!(isCellGroupRequired || isServiceProviderPromoAllow)) {
                    if (rs.getString("ALLOWED_DAYS") != null) {
                        rulesVO.setAllowedDays(BTSLUtil.numberToWeekdays(rs.getString("ALLOWED_DAYS")).toString());// added
                        // by
                        // arvinder
                        // for
                        // allowed
                        // days

                    }// end//
                     // added by arvinder to get allowed and denied series
                    if (searchPromoTransferReqVO.getOptionTab().equals(PretupsI.PROMOTIONAL_LEVEL_PREFIXID)) {
                        if (rs.getString("ALLOWED_SERIES") != null) {
                            networkDAO = NetworkDAO.getInstance();
                            rulesVO.setAllowedSeries(networkDAO.getSeries(p_con, rs.getString("ALLOWED_SERIES")));
                        }
                        if (rs.getString("DENIED_SERIES") != null) {
                            networkDAO = NetworkDAO.getInstance();
                            rulesVO.setDeniedSeries(networkDAO.getSeries(p_con, rs.getString("DENIED_SERIES")));
                        }
                    }// end/
                } else {
                    rulesVO.setSubscriberStatus(rs.getString("subscriber_status"));
                    rulesVO.setServiceGroupCode(rs.getString("sp_group_id"));
                }
                // added by akanksha for tigo_gtcr
                rulesVO.setRuleType(rs.getString("rule_type"));
                rulesVO.setGatewayCode(rs.getString("gateway_code"));
                transferRulesList.add(rulesVO);
            }
        }
        }catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[searchPromotionalTransferRulesList]", "",
                "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[searchPromotionalTransferRulesList]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            
            if (LOG.isDebugEnabled()) {
            	
                LOG.debug(methodName, "Exiting: transferRulesList.size=" + transferRulesList.size());
            }
        }
        return transferRulesList;
    }
    
    
    
    
    



}
