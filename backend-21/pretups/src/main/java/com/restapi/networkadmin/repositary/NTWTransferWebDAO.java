package com.restapi.networkadmin.repositary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.transfer.businesslogic.TransferRulesVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.transfer.businesslogic.TransferWebQry;
/**
 * class TransferWebDAO
 */
public class NTWTransferWebDAO {

    /**
     * Field LOG.
     */
    private Log LOG = LogFactory.getLog(NTWTransferWebDAO.class.getName());
    private TransferWebQry transferWebQry = (TransferWebQry)ObjectProducer.getObject(QueryConstants.TRANSFER_WEB_QUERY, QueryConstants.QUERY_PRODUCER);

      
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
    
    public void addPromotionalTransferRuleFile(Connection p_con, ArrayList p_transferRuleList, ArrayList p_errorVoList,  Locale p_locale, String p_promotionLevel, String p_category, String geodomainCd) throws BTSLBaseException {
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
                        	final String arr2[] = { transferRulesVO.getSenderSubscriberType() };
                            errorVO = new ListValueVO("", transferRulesVO.getRowID(), RestAPIStringParser.getMessage(p_locale,
									PretupsErrorCodesI.MSISDN_DOESNT_EXIST, arr2));
                            p_errorVoList.add(errorVO);
                            continue;
                        }
                    } catch (SQLException sqle) {
                    	final String arr2[] = { transferRulesVO.getSenderSubscriberType() };

                        errorVO = new ListValueVO("", transferRulesVO.getRowID(), RestAPIStringParser.getMessage(p_locale,
								PretupsErrorCodesI.MSISDN_DOESNT_EXIST, arr2));
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
                        errorVO = new ListValueVO("", transferRulesVO.getRowID(),RestAPIStringParser.getMessage(p_locale,
								PretupsErrorCodesI.TRANSFERRULE_ALLREADY_EXIST, null) );
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
                            errorVO = new ListValueVO("", transferRulesVO.getRowID(), 
                            		RestAPIStringParser.getMessage(p_locale,
        									PretupsErrorCodesI.INSERT_FAILD, null));
                            p_errorVoList.add(errorVO);
                            continue;
                        }
                    }
                } catch (SQLException sqle) {
                    LOG.errorTrace(methodName, sqle);
                    errorVO = new ListValueVO("", transferRulesVO.getRowID(), 
                    		RestAPIStringParser.getMessage(p_locale,
									PretupsErrorCodesI.INSERT_FAILD, null));
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
    

    
    
    

     
    
    
    
    
    



}
