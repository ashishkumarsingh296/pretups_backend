package com.btsl.pretups.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.util.MessageResources;

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
import com.btsl.pretups.logging.BatchesLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/** BatchTransferRulesDAO.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 * Shishupal Singh              18/04/2007         Initial Creation
 *------------------------------------------------------------------------
 * Copyright (c) 2007 Bharti Telesoft Ltd.
 * Transfer data access object class for interaction with the database
 */
/**
 */
public class BatchTransferRulesDAO {

	private Log log = LogFactory.getLog(TransferDAO.class.getName());
	
	/**
     * @param con
     * @param transferRuleslList
     * @param messages
     * @param locale
     * @param userVO
     * @param fileName
     * @return dbErrorList
     * @throws BTSLBaseException
     * @author shishupal.singh
     */
    public ArrayList addTransferRulesList(Connection con, ArrayList transferRuleslList, MessageResources messages, Locale locale, UserVO userVO, String fileName) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("addTransferRulesList",
                            "Entered: transferRuleslList.size()=" + transferRuleslList.size() + " messages=" + messages + " locale=" + locale + " fileName: " + fileName);
        }
        final String methodName = "addTransferRulesList";
        boolean batchIdFlag = true;
        String batchID = null;
        int commitCounter = 0, updateCount = 0;
        int commitNumber = 0;
        String[] arr = null;
        try {
            commitNumber = Integer.parseInt(Constants.getProperty("BATCH_USER_COMMIT_NUMBER"));
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            commitNumber = 100;
        }
        OperatorUtilI operatorUtil = null;
        final ArrayList dbErrorList = new ArrayList();
        ListValueVO errorVO = null;
        StringBuilder strBuilder = null;
        PreparedStatement psmtSelectTransferRule = null;
        PreparedStatement psmtBatchInsert = null;
        PreparedStatement psmtBatchUpdate = null;
        PreparedStatement psmtTransferRulesInsert = null;

        ResultSet rsSelectTransferRule = null;
        int insertTransferRuleCount = 0;
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            try {
                operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[addChannelUserList]", "", "", "",
                                "Exception while loading the class at the call:" + e.getMessage());
            }

            // ================== unique check for transfer rules
            strBuilder = new StringBuilder("SELECT COUNT(*) AS no_of_rows FROM transfer_rules WHERE ");
            strBuilder.append(" module=? AND network_code=? AND sender_subscriber_type=? AND sender_service_class_id=? AND receiver_subscriber_type=? ");
            strBuilder.append(" AND receiver_service_class_id=? AND sub_service=? AND service_type=? AND gateway_code =? AND cell_group_id =? ");
            final String selectTransferRuleQuery = strBuilder.toString();
            psmtSelectTransferRule = con.prepareStatement(selectTransferRuleQuery);

            // batches insert
            strBuilder = new StringBuilder("INSERT INTO batches (batch_id, batch_type, batch_size, batch_name, ");
            strBuilder.append("network_code, status, created_by, created_on, modified_by, modified_on,file_name) ");
            strBuilder.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?)");
            final String insertBatchQuery = strBuilder.toString();
            psmtBatchInsert = con.prepareStatement(insertBatchQuery);

            // update batches table
            strBuilder = new StringBuilder("UPDATE batches SET batch_size=?, status=? WHERE batch_id=? ");
            final String updateBatchQuery = strBuilder.toString();
            psmtBatchUpdate = con.prepareStatement(updateBatchQuery);

            // transfer rules insert
            strBuilder = new StringBuilder();
            strBuilder.append("INSERT INTO transfer_rules (module,network_code,sender_subscriber_type,  ");
            strBuilder.append("receiver_subscriber_type, sender_service_class_id,receiver_service_class_id, ");
            strBuilder.append("created_on,created_by, modified_on, modified_by,card_group_set_id,status,sub_service,service_type,gateway_code,grade_code,category_code,cell_group_id ) ");
            strBuilder.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            final String insertTransferRuleQuery = strBuilder.toString();
            psmtTransferRulesInsert = con.prepareStatement(insertTransferRuleQuery);

            final Iterator batchTransferRulesVOListItr = transferRuleslList.iterator();
            BatchTransferRulesVO batchTransferRulesVO = null;
            Start: while (batchTransferRulesVOListItr.hasNext()) {
                batchTransferRulesVO = (BatchTransferRulesVO) batchTransferRulesVOListItr.next();
                arr = new String[] { batchTransferRulesVO.getGatewayCode() + "," + batchTransferRulesVO.getCellGroupId() + "," + batchTransferRulesVO.getSenderSubscriberType() + "," + batchTransferRulesVO.getSenderServiceClassID() + "," + batchTransferRulesVO
                                .getReceiverSubscriberType() + "," + batchTransferRulesVO.getReceiverServiceClassID() + "," + batchTransferRulesVO.getServiceType() + "," + batchTransferRulesVO
                                .getSubServiceTypeId() };

                // ======================Validation 1: Check for transfer rule
                // which is already exist.
                if (!BTSLUtil.isNullString(batchTransferRulesVO.getSenderSubscriberType()) && !BTSLUtil.isNullString(batchTransferRulesVO.getSenderServiceClassID()) && !BTSLUtil
                                .isNullString(batchTransferRulesVO.getReceiverSubscriberType()) && !BTSLUtil.isNullString(batchTransferRulesVO.getReceiverServiceClassID()) && !BTSLUtil
                                .isNullString(batchTransferRulesVO.getSubServiceTypeId()) && !BTSLUtil.isNullString(batchTransferRulesVO.getServiceType())) {
                    psmtSelectTransferRule.setString(1, batchTransferRulesVO.getModule());
                    psmtSelectTransferRule.setString(2, batchTransferRulesVO.getNetworkCode());
                    psmtSelectTransferRule.setString(3, batchTransferRulesVO.getSenderSubscriberType());
                    psmtSelectTransferRule.setString(4, batchTransferRulesVO.getSenderServiceClassID());
                    psmtSelectTransferRule.setString(5, batchTransferRulesVO.getReceiverSubscriberType());
                    psmtSelectTransferRule.setString(6, batchTransferRulesVO.getReceiverServiceClassID());
                    psmtSelectTransferRule.setString(7, batchTransferRulesVO.getSubServiceTypeId());
                    psmtSelectTransferRule.setString(8, batchTransferRulesVO.getServiceType());
                    psmtSelectTransferRule.setString(9, batchTransferRulesVO.getGatewayCode());
                    psmtSelectTransferRule.setString(10, batchTransferRulesVO.getCellGroupId());
                    rsSelectTransferRule = psmtSelectTransferRule.executeQuery();
                    rsSelectTransferRule.next();
                    if (rsSelectTransferRule.getInt("no_of_rows") > 0) {
                        errorVO = new ListValueVO("", batchTransferRulesVO.getRecordNumber(), messages.getMessage(locale,
                                        "transferrules.createbatchtransferrules.msg.error.transferruleuniqueerr", arr));
                        dbErrorList.add(errorVO);
                        BatchesLog.transferRuleLog("BATCH_TRF_RULES_CREATION", batchTransferRulesVO, null, "Fail :=" + messages.getMessage(
                                        "transferrules.createbatchtransferrules.msg.error.transferruleuniqueerr", arr));
                        continue Start;
                    }
                    psmtSelectTransferRule.clearParameters();
                }
                // ====================== end transfer rule validation here

                if (batchIdFlag) {
                    // one time entry into batches table
                    batchID = operatorUtil.formatBatchesID(userVO.getNetworkID(), PretupsI.TRF_RULES_BATCH_PREFIX, new Date(), IDGenerator.getNextID(
                                    PretupsI.TRF_RULES_BATCH_ID, BTSLUtil.getFinancialYear(), userVO.getNetworkID()));
                    psmtBatchInsert.setString(1, batchID);
                    psmtBatchInsert.setString(2, PretupsI.TRF_RULES_BATCH_TYPE);
                    psmtBatchInsert.setInt(3, transferRuleslList.size());
                    psmtBatchInsert.setString(4, batchTransferRulesVO.getBatchName());
                    psmtBatchInsert.setString(5, batchTransferRulesVO.getNetworkCode());
                    psmtBatchInsert.setString(6, PretupsI.TRF_RULE_BATCH_STATUS_UNDERPROCESS);
                    psmtBatchInsert.setString(7, batchTransferRulesVO.getCreatedBy());
                    psmtBatchInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(batchTransferRulesVO.getCreatedOn()));
                    psmtBatchInsert.setString(9, batchTransferRulesVO.getModifiedBy());
                    psmtBatchInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(batchTransferRulesVO.getModifiedOn()));
                    psmtBatchInsert.setString(11, fileName);
                    if (psmtBatchInsert.executeUpdate() <= 0) {
                        con.rollback();
                        BatchesLog.transferRuleLog("BATCH_TRF_RULES_CREATION", batchTransferRulesVO, null, "Fail :=" + messages
                                        .getMessage("transferrules.createbatchtransferrules.err.batchnotcreated"));
                        throw new BTSLBaseException(this, "addTransferRulesList", "transferrules.createbatchtransferrules.err.batchnotcreated", "selectfile");
                    }
                    batchIdFlag = false;
                }

                if (commitCounter > commitNumber)// After 100 record commit the
                // records
                {
                    con.commit();
                    commitCounter = 0;// reset commit counter
                }

                // insert transfer rule info
                insertTransferRuleCount = this.addTransferRule(psmtTransferRulesInsert, batchTransferRulesVO);
                if (insertTransferRuleCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO("", batchTransferRulesVO.getRecordNumber(), messages.getMessage(locale,
                                    "transferrules.createbatchtransferrules.msg.error.transferruleinsertfail", arr));
                    dbErrorList.add(errorVO);
                    BatchesLog.transferRuleLog("BATCH_TRF_RULES_CREATION", batchTransferRulesVO, null, "Fail :=" + messages.getMessage(
                                    "transferrules.createbatchtransferrules.msg.error.transferruleinsertfail", arr));
                    continue Start;
                }
                commitCounter++;
                updateCount++;
            } // end while loop
            if (updateCount > 0) {
                psmtBatchUpdate.setInt(1, updateCount);
                psmtBatchUpdate.setString(2, PretupsI.TRF_RULE_BATCH_STATUS_CLOSE);
                psmtBatchUpdate.setString(3, batchID);
                psmtBatchUpdate.executeUpdate();
                psmtBatchUpdate.clearParameters();
                con.commit();
            } else {
                con.rollback();
            }
            errorVO = new ListValueVO("BATCHID", "", batchID);
            dbErrorList.add(errorVO);

        } // end of try
        catch (SQLException sqe) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error("addTransferRulesList", "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchTransferRulesDAO[addTransferRulesList]", "", "",
                            "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "addTransferRulesList", "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error("addTransferRulesList", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchTransferRulesDAO[addTransferRulesList]", "", "",
                            "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "addTransferRulesList", "error.general.processing");
        } finally {
            try {
                if (psmtSelectTransferRule != null) {
                    psmtSelectTransferRule.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtBatchInsert != null) {
                    psmtBatchInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtBatchUpdate != null) {
                    psmtBatchUpdate.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtTransferRulesInsert != null) {
                    psmtTransferRulesInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectTransferRule != null) {
                    rsSelectTransferRule.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug("addTransferRulesList", "Exiting: insertCount=" + insertTransferRuleCount);
            }
        }
        return dbErrorList;

    }
	
	/**
	 * @param con
	 * @param transferRuleslList
	 * @param messages
	 * @param locale
	 * @param userVO
	 * @param fileName
	 * @return dbErrorList
	 * @throws BTSLBaseException
	 * @author Ashutosh
	 */
	public ArrayList addC2STransferRulesList(Connection con, ArrayList transferRuleslList,ArrayList <BatchTransferRulesVO> errorLoglist,MessageResources messages, Locale locale, UserVO userVO,String fileName)throws BTSLBaseException
	{
    	if (log.isDebugEnabled())
    	    log.debug("addC2sTransferRulesList", "Entered: transferRuleslList.size()="+transferRuleslList.size()+" messages="+messages+" locale="+locale+" fileName: "+fileName);
    	final String methodName="addC2sTransferRulesList";
    	boolean batchIdFlag = true; 
    	String batchID=null;
    	int commitCounter=0,updateCount=0;
	    int commitNumber=0, deleteCount=0;
	    String[] arr = null;
	    try{ 
	        commitNumber = Integer.parseInt(Constants.getProperty("BATCH_USER_COMMIT_NUMBER"));
	    }catch(Exception e)
	    {
	    	log.errorTrace(methodName, e);
	    	commitNumber=100;}
		OperatorUtilI operatorUtil=null;
    	ArrayList dbErrorList = new ArrayList();
    	ListValueVO errorVO = null;
    	StringBuilder strBuilder = null;
    	PreparedStatement psmtSelectTransferRule = null;
    	PreparedStatement psmtBatchInsert = null;
    	PreparedStatement psmtBatchUpdate = null;
    	PreparedStatement psmtTransferRulesInsert = null;
    	PreparedStatement psmtTransferUpdate=null;
    	PreparedStatement psmtTransferRulesDelete=null;
    	
    	ResultSet rsSelectTransferRule = null;
		int insertTransferRuleCount=0;
    	try
    	{
    		String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
    		try{
    			operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
    		}catch(Exception e){
    			log.errorTrace(methodName, e);
    			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchUserDAO[addChannelUserList]","","","","Exception while loading the class at the call:"+e.getMessage());
    		}

    		//==================  unique check for transfer rules
    		strBuilder = new StringBuilder("SELECT CARD_GROUP_SET_ID, status FROM transfer_rules WHERE ");
    		strBuilder.append(" module=? AND network_code=? AND sender_subscriber_type=? AND sender_service_class_id=? AND receiver_subscriber_type=? ");
    		strBuilder.append(" AND receiver_service_class_id=? AND sub_service=? AND service_type=? and gateway_code=? and grade_code=? and category_code=?");
    		String selectTransferRuleQuery = strBuilder.toString();
    		psmtSelectTransferRule = con.prepareStatement(selectTransferRuleQuery);

    		//batches insert
    		strBuilder = new StringBuilder("INSERT INTO batches (batch_id, batch_type, batch_size, batch_name, ");
    		strBuilder.append("network_code, status, created_by, created_on, modified_by, modified_on,file_name) ");
    		strBuilder.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?)");
    		String insertBatchQuery = strBuilder.toString();
    		psmtBatchInsert = con.prepareStatement(insertBatchQuery);

    		//update batches table
    		strBuilder = new StringBuilder("UPDATE batches SET batch_size=?, status=? WHERE batch_id=? ");
    		String updateBatchQuery = strBuilder.toString();
    		psmtBatchUpdate = con.prepareStatement(updateBatchQuery);

    		//transfer rules insert
    		strBuilder =new StringBuilder();
    		strBuilder.append("INSERT INTO transfer_rules (module,network_code,sender_subscriber_type,  ");
    		strBuilder.append("receiver_subscriber_type, sender_service_class_id,receiver_service_class_id, ");
			strBuilder.append("created_on,created_by, modified_on, modified_by,card_group_set_id,status,sub_service,service_type,gateway_code,grade_code,category_code,cell_group_id) ");
			strBuilder.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			String insertTransferRuleQuery=strBuilder.toString();
    		psmtTransferRulesInsert = con.prepareStatement(insertTransferRuleQuery);


    		//for suspend
    		strBuilder =new StringBuilder();
    		strBuilder.append("UPDATE transfer_rules set CARD_GROUP_SET_ID=?, status=?, modified_on=?, modified_by=? where");
    		strBuilder.append(" module=? AND network_code=? AND sender_subscriber_type=? AND sender_service_class_id=? AND receiver_subscriber_type=? ");
    		strBuilder.append(" AND receiver_service_class_id=? AND sub_service=? AND service_type=? and gateway_code=? and grade_code=? and category_code=?");	
    		String suspendTransferRuleQuery=strBuilder.toString();
    		psmtTransferUpdate = con.prepareStatement(suspendTransferRuleQuery);

    		//for delete
    		strBuilder =new StringBuilder();
    		strBuilder.append("DELETE from transfer_rules where");
    		strBuilder.append(" module=? AND network_code=? AND sender_subscriber_type=? AND sender_service_class_id=? AND receiver_subscriber_type=? ");
    		strBuilder.append(" AND receiver_service_class_id=? AND sub_service=? AND service_type=? and gateway_code=? and grade_code=? and category_code=?");	
    		String deleteTransferRuleQuery=strBuilder.toString();
    		psmtTransferRulesDelete = con.prepareStatement(deleteTransferRuleQuery);

    		Iterator batchTransferRulesVOListItr = transferRuleslList.iterator();
    		BatchTransferRulesVO batchTransferRulesVO = null;

    		Start:
    			while(batchTransferRulesVOListItr.hasNext())
    			{
    				batchTransferRulesVO = (BatchTransferRulesVO)batchTransferRulesVOListItr.next();
    				arr = new String[]{batchTransferRulesVO.getGatewayCode()+":"+batchTransferRulesVO.getSenderSubscriberType()+":"+batchTransferRulesVO.getGradeCode() + ":" + batchTransferRulesVO.getSenderServiceClassID() + ":" + batchTransferRulesVO.getReceiverSubscriberType() + ":" + batchTransferRulesVO.getReceiverServiceClassID() + ":" + batchTransferRulesVO.getServiceType() + ":" + batchTransferRulesVO.getSubServiceTypeId()};
    				//check for insert, update or delete

    				if(PretupsI.TRANSFER_RULE_STATUS_DELETE.equals(batchTransferRulesVO.getStatus())) {

    					deleteCount = this.deleteTransferRule(psmtTransferRulesDelete, batchTransferRulesVO);
    					con.commit();
    				}

    				//Now proceed for insertion of transfer rule
    				else {
    					//======================Validation 1: Check for transfer rule which is already exist for insertion.
    					if( !BTSLUtil.isNullString(batchTransferRulesVO.getSenderSubscriberType()) && 
    							!BTSLUtil.isNullString(batchTransferRulesVO.getSenderServiceClassID()) &&
    							!BTSLUtil.isNullString(batchTransferRulesVO.getReceiverSubscriberType()) && 
    							!BTSLUtil.isNullString(batchTransferRulesVO.getReceiverServiceClassID()) &&
    							!BTSLUtil.isNullString(batchTransferRulesVO.getSubServiceTypeId()) && 
    							!BTSLUtil.isNullString(batchTransferRulesVO.getServiceType()) )
    					{
    						psmtSelectTransferRule.setString(1, batchTransferRulesVO.getModule());
    						psmtSelectTransferRule.setString(2, batchTransferRulesVO.getNetworkCode());
    						psmtSelectTransferRule.setString(3, batchTransferRulesVO.getSenderSubscriberType());
    						psmtSelectTransferRule.setString(4, batchTransferRulesVO.getSenderServiceClassID());
    						psmtSelectTransferRule.setString(5, batchTransferRulesVO.getReceiverSubscriberType());
    						psmtSelectTransferRule.setString(6, batchTransferRulesVO.getReceiverServiceClassID());
    						psmtSelectTransferRule.setString(7, batchTransferRulesVO.getSubServiceTypeId());
    						psmtSelectTransferRule.setString(8, batchTransferRulesVO.getServiceType());
    						psmtSelectTransferRule.setString(9, batchTransferRulesVO.getGatewayCode());
    						psmtSelectTransferRule.setString(10, batchTransferRulesVO.getGradeCode());
    						psmtSelectTransferRule.setString(11, batchTransferRulesVO.getCategoryCode());	
    						rsSelectTransferRule=psmtSelectTransferRule.executeQuery();
    						if(rsSelectTransferRule.next())
    						{  
    							if(!batchTransferRulesVO.getCardGroupSetID().equals(rsSelectTransferRule.getString("CARD_GROUP_SET_ID")) || !batchTransferRulesVO.getStatus().equals(rsSelectTransferRule.getString("status"))) {
    								this.updateTransferRule(psmtTransferUpdate, batchTransferRulesVO);
    								con.commit();
    								continue Start;
    							}
    							else { //duplicate request
    								errorVO=new ListValueVO("",batchTransferRulesVO.getRecordNumber(),messages.getMessage(locale,"transferrules.createbatchtransferrules.msg.error.transferruleuniqueerr",arr));
    								dbErrorList.add(errorVO);
    								BatchesLog.transferRuleLog("BATCH_TRF_RULES_CREATION",batchTransferRulesVO,null,"Fail :="+messages.getMessage("transferrules.createbatchtransferrules.msg.error.transferruleuniqueerr",arr));
    								batchTransferRulesVO.setError("Transfer rule for "+Arrays.toString(arr)+" already exists");
    								errorLoglist.add(batchTransferRulesVO);
    								continue Start;
    							}

    						}
    						psmtSelectTransferRule.clearParameters();
    					}
    					//====================== end transfer rule validation here 
    					//insert transfer rule info
    					if(PretupsI.TRANSFER_RULE_STATUS_ACTIVE.equals(batchTransferRulesVO.getStatus())) {
    						insertTransferRuleCount = this.addTransferRule(psmtTransferRulesInsert, batchTransferRulesVO);
        					if(insertTransferRuleCount <= 0){
        						con.rollback();
        						errorVO=new ListValueVO("",batchTransferRulesVO.getRecordNumber(),messages.getMessage(locale,"transferrules.createbatchtransferrules.msg.error.transferruleinsertfail",arr));
        						dbErrorList.add(errorVO);
        						BatchesLog.transferRuleLog("BATCH_TRF_RULES_CREATION",batchTransferRulesVO,null,"Fail :="+messages.getMessage("transferrules.createbatchtransferrules.msg.error.transferruleinsertfail",arr));
        						batchTransferRulesVO.setError("Transfer rule creation failed for "+Arrays.toString(arr));
        						errorLoglist.add(batchTransferRulesVO);
        						continue Start;
        					}
    					}
    					else {
    						errorVO=new ListValueVO("",batchTransferRulesVO.getRecordNumber(),messages.getMessage(locale,"transferrules.createbatchtransferrules.msg.error.notransferruleforsuspend",arr));
    						dbErrorList.add(errorVO);
    						BatchesLog.transferRuleLog("BATCH_TRF_RULES_CREATION",batchTransferRulesVO,null,"Fail :="+messages.getMessage("transferrules.createbatchtransferrules.msg.error.notransferruleforsuspend",arr));
    						batchTransferRulesVO.setError("No transfer rule exists for suspend "+Arrays.toString(arr));
    						errorLoglist.add(batchTransferRulesVO);
    						continue Start;
    					}
    				}
    			} // end while loop

    	} // end of try
    	catch (SQLException sqe)
    	{
    	    try
    	    {
    	    	if (con != null)
    	    	{
    	    		con.rollback();
    	    		}
    	    	} 
    	    catch (Exception e)
    	    { 
    	    	log.errorTrace(methodName, e);
    	    }
    	    log.error("addC2sTransferRulesList", "SQLException : " + sqe);
    	    log.errorTrace(methodName, sqe);
    	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchTransferRulesDAO[addC2sTransferRulesList]","","","","SQL Exception:"+sqe.getMessage());
    	    throw new BTSLBaseException(this, "addC2sTransferRulesList", "error.general.sql.processing");
    	}
    	catch (Exception ex)
    	{
    	    try
    	    {
    	    	if (con != null)
    	    	{
    	    		con.rollback();
    	    		}
    	    	} 
    	    catch (Exception e)
    	    {
    	    	log.errorTrace(methodName, e);
    	    }
    	    log.error("addTransferRulesList", "Exception : " + ex);
    	    log.errorTrace(methodName, ex);
    	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchTransferRulesDAO[addC2sTransferRulesList]","","","","Exception:"+ex.getMessage());
    	    throw new BTSLBaseException(this, "addC2sTransferRulesList", "error.general.processing");
    	}
    	finally
		{
    	    try{
    	    	if (psmtSelectTransferRule != null)
    	    		psmtSelectTransferRule.close();
    	    	}
    	    catch (Exception e)
    	    {
    	    	log.errorTrace(methodName, e);
    	    }
    	    try
    	    {
    	    	if (psmtBatchInsert != null)
    	    		psmtBatchInsert.close();
    	    	} 
    	    catch (Exception e)
    	    {
    	    	log.errorTrace(methodName, e);
    	    }
    	    try
    	    {
    	    	if (psmtBatchUpdate != null)
    	    		psmtBatchUpdate.close();
    	    	} catch (Exception e)
    	    {
    	    	log.errorTrace(methodName, e);
    	    }
    	    try
    	    {
    	    	if (psmtTransferUpdate != null)
    	    		psmtTransferUpdate.close();
    	    	} catch (Exception e)
    	    {
    	    	log.errorTrace(methodName, e);
    	    }
    	    try
    	    {
    	    	if (psmtTransferRulesDelete != null)
    	    		psmtTransferRulesDelete.close();
    	    	} catch (Exception e)
    	    {
    	    	log.errorTrace(methodName, e);
    	    }
    	    try
    	    {
    	    	if (psmtTransferRulesInsert != null)
    	    		psmtTransferRulesInsert.close();
    	    	} 
    	    catch (Exception e)
    	    {
    	    	
    	    	log.errorTrace(methodName, e);
    	    }
    	    try
    	    {
    	    	if (rsSelectTransferRule != null)
    	    		rsSelectTransferRule.close();
    	    	} 
    	    catch (Exception e)
    	    {
    	    	log.errorTrace(methodName, e);
    	    }
    	    if (log.isDebugEnabled())
				log.debug("addC2sTransferRulesList", "Exiting: insertCount=" + insertTransferRuleCount);
		}
    	return dbErrorList;
	    	    
	 }

	/**
	 * Method addTransferRule.
	 * This method is used to add the record in the transfer_rules table .
	 * @author shishupal.singh
	 * @param psmtTransferRulesInsert PreparedStatement
	 * @param transferRulesVO BatchTransferRulesVO
	 * @return int
	 * @throws BTSLBaseException
	 */
	public int addTransferRule(PreparedStatement psmtTransferRulesInsert, BatchTransferRulesVO transferRulesVO) throws BTSLBaseException
	{
		final String methodName = "addTransferRule";
		if(log.isDebugEnabled())
		    log.debug("addTransferRule","Entered transferRulesVO:"+transferRulesVO.toString());
		int addCount=0;
		int i=1;
		try
		{
			psmtTransferRulesInsert.setString(i++,transferRulesVO.getModule());
			psmtTransferRulesInsert.setString(i++,transferRulesVO.getNetworkCode());
			psmtTransferRulesInsert.setString(i++,transferRulesVO.getSenderSubscriberType());
			psmtTransferRulesInsert.setString(i++,transferRulesVO.getReceiverSubscriberType());
			psmtTransferRulesInsert.setString(i++,transferRulesVO.getSenderServiceClassID());
			psmtTransferRulesInsert.setString(i++,transferRulesVO.getReceiverServiceClassID());
			psmtTransferRulesInsert.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(transferRulesVO.getCreatedOn()));
			psmtTransferRulesInsert.setString(i++,transferRulesVO.getCreatedBy());
			psmtTransferRulesInsert.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(transferRulesVO.getModifiedOn()));
			psmtTransferRulesInsert.setString(i++,transferRulesVO.getModifiedBy());
			psmtTransferRulesInsert.setString(i++,transferRulesVO.getCardGroupSetID());
			psmtTransferRulesInsert.setString(i++,transferRulesVO.getStatus());
			psmtTransferRulesInsert.setString(i++,transferRulesVO.getSubServiceTypeId());
			psmtTransferRulesInsert.setString(i++,transferRulesVO.getServiceType());
			psmtTransferRulesInsert.setString(i++, transferRulesVO.getGatewayCode());
			psmtTransferRulesInsert.setString(i++, transferRulesVO.getGradeCode());
			psmtTransferRulesInsert.setString(i++, transferRulesVO.getCategoryCode());
			psmtTransferRulesInsert.setString(i++, transferRulesVO.getCellGroupId());
			addCount = psmtTransferRulesInsert.executeUpdate();
			psmtTransferRulesInsert.clearParameters();
		}//end of try
		catch (SQLException sqle)
		{
			log.error("addTransferRule","SQLException "+sqle.getMessage());
			log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchTransferRulesDAO[addTransferRule]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, "addTransferRule", "error.general.sql.processing");
		}//end of catch
		catch (Exception e)
		{
			log.error("addTransferRule","Exception "+e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchTransferRulesDAO[addTransferRule]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, "addTransferRule", "error.general.processing");
		}//end of catch
		finally
		{
			if(log.isDebugEnabled())
			    log.debug("addTransferRule","Exiting addCount="+addCount);
		 }//end of finally
		return addCount;
	}
	
	public int deleteTransferRule(PreparedStatement psmtTransferRulesDelete, BatchTransferRulesVO transferRulesVO) throws BTSLBaseException
	{
		final String methodName = "deleteTransferRule";
		if(log.isDebugEnabled())
		    log.debug("deleteTransferRule","Entered transferRulesVO:"+transferRulesVO.toString());
		int deleteCount=0;
		int i=1;
		try
		{
			psmtTransferRulesDelete.setString(1, transferRulesVO.getModule());
			psmtTransferRulesDelete.setString(2, transferRulesVO.getNetworkCode());
			psmtTransferRulesDelete.setString(3, transferRulesVO.getSenderSubscriberType());
			psmtTransferRulesDelete.setString(4, transferRulesVO.getSenderServiceClassID());
			psmtTransferRulesDelete.setString(5, transferRulesVO.getReceiverSubscriberType());
			psmtTransferRulesDelete.setString(6, transferRulesVO.getReceiverServiceClassID());
			psmtTransferRulesDelete.setString(7, transferRulesVO.getSubServiceTypeId());
			psmtTransferRulesDelete.setString(8, transferRulesVO.getServiceType());
			psmtTransferRulesDelete.setString(9, transferRulesVO.getGatewayCode());
			psmtTransferRulesDelete.setString(10, transferRulesVO.getGradeCode());
			psmtTransferRulesDelete.setString(11, transferRulesVO.getCategoryCode());
			deleteCount = psmtTransferRulesDelete.executeUpdate();
		}//end of try
		catch (SQLException sqle)
		{
			log.error("deleteTransferRule","SQLException "+sqle.getMessage());
			log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchTransferRulesDAO[deleteTransferRule]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, "deleteTransferRule", "error.general.sql.processing");
		}//end of catch
		catch (Exception e)
		{
			log.error("deleteTransferRule","Exception "+e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchTransferRulesDAO[deleteTransferRule]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, "addTransferRule", "error.general.processing");
		}//end of catch
		finally
		{
			if(log.isDebugEnabled())
			    log.debug("deleteTransferRule","Exiting addCount="+deleteCount);
		 }//end of finally
		return deleteCount;
	}
	
	public int updateTransferRule(PreparedStatement p_psmtTransferRulesUpdate, BatchTransferRulesVO transferRulesVO) throws BTSLBaseException
	{
		final String methodName = "updateTransferRule";
		if(log.isDebugEnabled())
		    log.debug("updateTransferRule","Entered transferRulesVO:"+transferRulesVO.toString());
		int updateCount=0;
		int i=1;
		try
		{
			p_psmtTransferRulesUpdate.setString(i++, transferRulesVO.getCardGroupSetID());
			p_psmtTransferRulesUpdate.setString(i++, transferRulesVO.getStatus());
			p_psmtTransferRulesUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(transferRulesVO.getModifiedOn()));
			p_psmtTransferRulesUpdate.setString(i++,transferRulesVO.getModifiedBy());
			p_psmtTransferRulesUpdate.setString(i++, transferRulesVO.getModule());
			p_psmtTransferRulesUpdate.setString(i++, transferRulesVO.getNetworkCode());
			p_psmtTransferRulesUpdate.setString(i++, transferRulesVO.getSenderSubscriberType());
			p_psmtTransferRulesUpdate.setString(i++, transferRulesVO.getSenderServiceClassID());
			p_psmtTransferRulesUpdate.setString(i++, transferRulesVO.getReceiverSubscriberType());
			p_psmtTransferRulesUpdate.setString(i++, transferRulesVO.getReceiverServiceClassID());
			p_psmtTransferRulesUpdate.setString(i++, transferRulesVO.getSubServiceTypeId());
			p_psmtTransferRulesUpdate.setString(i++, transferRulesVO.getServiceType());
			p_psmtTransferRulesUpdate.setString(i++, transferRulesVO.getGatewayCode());
			p_psmtTransferRulesUpdate.setString(i++, transferRulesVO.getGradeCode());
			p_psmtTransferRulesUpdate.setString(i++, transferRulesVO.getCategoryCode());
			p_psmtTransferRulesUpdate.executeUpdate();
		}//end of try
		catch (SQLException sqle)
		{
			log.error("updateTransferRule","SQLException "+sqle.getMessage());
			log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchTransferRulesDAO[updateTransferRule]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, "updateTransferRule", "error.general.sql.processing");
		}//end of catch
		catch (Exception e)
		{
			log.error("updateTransferRule","Exception "+e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchTransferRulesDAO[updateTransferRule]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, "updateTransferRule", "error.general.processing");
		}//end of catch
		finally
		{
			if(log.isDebugEnabled())
			    log.debug("updateTransferRule","Exiting addCount="+updateCount);
		 }//end of finally
		return updateCount;
	}

	//Added by Ashutosh to check the existence of a transfer rule
	public BatchTransferRulesVO isTransferRuleExists(Connection con,String netCode, String regGwCode, String senderSubsType, String catCode, String gradeCode, String recSubsType, String recServClassId, String servType, String subServTypeId) throws BTSLBaseException
	{
		final String methodName = "isTransferRuleExists";
		if(log.isDebugEnabled())
		    log.debug("isTransferRuleExists","Entered ");
		boolean exists = false;
		ResultSet rsSelectTransferRule = null;
		StringBuilder strBuilder = null;
		BatchTransferRulesVO trfRuleVO = null;
		
			strBuilder = new StringBuilder("select CARD_GROUP_SET_ID, status from TRANSFER_RULES ");
			strBuilder.append("where gateway_code=? and network_code=? and SENDER_SUBSCRIBER_TYPE=? and category_code=? and grade_code=? ");
			strBuilder.append(" and RECEIVER_SUBSCRIBER_TYPE=? and RECEIVER_SERVICE_CLASS_ID=? and SERVICE_TYPE=? and SUB_SERVICE=? and status <> ?");
			final String selectTransferRuleQuery = strBuilder.toString();
			try(PreparedStatement psmtSelectTransferRule = con.prepareStatement(selectTransferRuleQuery)){
            int i=1;
            psmtSelectTransferRule.setString(i++,regGwCode);
            psmtSelectTransferRule.setString(i++,netCode);
            psmtSelectTransferRule.setString(i++,senderSubsType);
            psmtSelectTransferRule.setString(i++,catCode);
            psmtSelectTransferRule.setString(i++,gradeCode);
            psmtSelectTransferRule.setString(i++,recSubsType);
            psmtSelectTransferRule.setString(i++,recServClassId);
            psmtSelectTransferRule.setString(i++,servType);
            psmtSelectTransferRule.setString(i++,subServTypeId);
            psmtSelectTransferRule.setString(i++,PretupsI.TRANSFER_RULE_STATUS_DELETE);
            rsSelectTransferRule = psmtSelectTransferRule.executeQuery();
            if(rsSelectTransferRule!=null) {
            	if(rsSelectTransferRule.next()) {
            		trfRuleVO = new BatchTransferRulesVO();
            		trfRuleVO.setCardGroupSetID(rsSelectTransferRule.getString("CARD_GROUP_SET_ID"));
            		trfRuleVO.setStatus(rsSelectTransferRule.getString("status"));
            	}
            }
            
		}//end of try
		catch (SQLException sqle)
		{
			log.error("isTransferRuleExists","SQLException "+sqle.getMessage());
			log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchTransferRulesDAO[isTransferRuleExists]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, "isTransferRuleExists", "error.general.sql.processing");
		}//end of catch
		catch (Exception e)
		{
			log.error("isTransferRuleExists","Exception "+e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchTransferRulesDAO[isTransferRuleExists]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, "isTransferRuleExists", "error.general.processing");
		}//end of catch
		finally
		{
			try{
				if(rsSelectTransferRule!=null)
					rsSelectTransferRule.close();
			}
			catch (Exception e) {
                log.errorTrace(methodName, e);
            }
			if(log.isDebugEnabled())
			    log.debug("isTransferRuleExists","Exiting trfRuleVO = "+trfRuleVO);
		 }//end of finally
		return trfRuleVO;
	}

	public ArrayList addC2STransferRulesListAg(Connection con, ArrayList transferRuleslList, ArrayList<BatchTransferRulesVO> errorLoglist, Locale locale, UserVO userVO, String fileName) throws BTSLBaseException {
		final String methodName = "addC2STransferRulesListAg";
		boolean batchIdFlag = true;
		String batchID = null;
		int commitCounter = 0, updateCount = 0;
		int commitNumber = 0, deleteCount = 0;
		String[] arr = null;
		try {
			commitNumber = Integer.parseInt(Constants.getProperty("BATCH_USER_COMMIT_NUMBER"));
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			commitNumber = 100;
		}
		OperatorUtilI operatorUtil = null;
		ArrayList dbErrorList = new ArrayList();
		ListValueVO errorVO = null;
		StringBuilder strBuilder = null;
		PreparedStatement psmtSelectTransferRule = null;
		PreparedStatement psmtBatchInsert = null;
		PreparedStatement psmtBatchUpdate = null;
		PreparedStatement psmtTransferRulesInsert = null;
		PreparedStatement psmtTransferUpdate = null;
		PreparedStatement psmtTransferRulesDelete = null;

		ResultSet rsSelectTransferRule = null;
		int insertTransferRuleCount = 0;
		try {
			String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			try {
				operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
			} catch (Exception e) {
				log.errorTrace(methodName, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[addChannelUserList]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
			}
			strBuilder = new StringBuilder("SELECT CARD_GROUP_SET_ID, status FROM transfer_rules WHERE ");
			strBuilder.append(" module=? AND network_code=? AND sender_subscriber_type=? AND sender_service_class_id=? AND receiver_subscriber_type=? ");
			strBuilder.append(" AND receiver_service_class_id=? AND sub_service=? AND service_type=? and gateway_code=? and grade_code=? and category_code=?");
			String selectTransferRuleQuery = strBuilder.toString();
			psmtSelectTransferRule = con.prepareStatement(selectTransferRuleQuery);

			strBuilder = new StringBuilder("INSERT INTO batches (batch_id, batch_type, batch_size, batch_name, ");
			strBuilder.append("network_code, status, created_by, created_on, modified_by, modified_on,file_name) ");
			strBuilder.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?)");
			String insertBatchQuery = strBuilder.toString();
			psmtBatchInsert = con.prepareStatement(insertBatchQuery);

			strBuilder = new StringBuilder("UPDATE batches SET batch_size=?, status=? WHERE batch_id=? ");
			String updateBatchQuery = strBuilder.toString();
			psmtBatchUpdate = con.prepareStatement(updateBatchQuery);

			strBuilder = new StringBuilder();
			strBuilder.append("INSERT INTO transfer_rules (module,network_code,sender_subscriber_type,  ");
			strBuilder.append("receiver_subscriber_type, sender_service_class_id,receiver_service_class_id, ");
			strBuilder.append("created_on,created_by, modified_on, modified_by,card_group_set_id,status,sub_service,service_type,gateway_code,grade_code,category_code,cell_group_id ) ");
			strBuilder.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			String insertTransferRuleQuery = strBuilder.toString();
			psmtTransferRulesInsert = con.prepareStatement(insertTransferRuleQuery);


			strBuilder = new StringBuilder();
			strBuilder.append("UPDATE transfer_rules set CARD_GROUP_SET_ID=?, status=?, modified_on=?, modified_by=? where");
			strBuilder.append(" module=? AND network_code=? AND sender_subscriber_type=? AND sender_service_class_id=? AND receiver_subscriber_type=? ");
			strBuilder.append(" AND receiver_service_class_id=? AND sub_service=? AND service_type=? and gateway_code=? and grade_code=? and category_code=?");
			String suspendTransferRuleQuery = strBuilder.toString();
			psmtTransferUpdate = con.prepareStatement(suspendTransferRuleQuery);


			strBuilder = new StringBuilder();
			strBuilder.append("DELETE from transfer_rules where");
			strBuilder.append(" module=? AND network_code=? AND sender_subscriber_type=? AND sender_service_class_id=? AND receiver_subscriber_type=? ");
			strBuilder.append(" AND receiver_service_class_id=? AND sub_service=? AND service_type=? and gateway_code=? and grade_code=? and category_code=?");
			String deleteTransferRuleQuery = strBuilder.toString();
			psmtTransferRulesDelete = con.prepareStatement(deleteTransferRuleQuery);

			Iterator batchTransferRulesVOListItr = transferRuleslList.iterator();
			BatchTransferRulesVO batchTransferRulesVO = null;

			Start:
			while (batchTransferRulesVOListItr.hasNext()) {
				batchTransferRulesVO = (BatchTransferRulesVO) batchTransferRulesVOListItr.next();
				arr = new String[]{batchTransferRulesVO.getGatewayCode() + ":" + batchTransferRulesVO.getSenderSubscriberType() + ":" + batchTransferRulesVO.getGradeCode() + ":" + batchTransferRulesVO.getSenderServiceClassID() + ":" + batchTransferRulesVO.getReceiverSubscriberType() + ":" + batchTransferRulesVO.getReceiverServiceClassID() + ":" + batchTransferRulesVO.getServiceType() + ":" + batchTransferRulesVO.getSubServiceTypeId()};

				if (PretupsI.TRANSFER_RULE_STATUS_DELETE.equals(batchTransferRulesVO.getStatus())) {

					deleteCount = this.deleteTransferRule(psmtTransferRulesDelete, batchTransferRulesVO);
					con.commit();
				} else {
					if (!BTSLUtil.isNullString(batchTransferRulesVO.getSenderSubscriberType()) && !BTSLUtil.isNullString(batchTransferRulesVO.getSenderServiceClassID()) && !BTSLUtil.isNullString(batchTransferRulesVO.getReceiverSubscriberType()) && !BTSLUtil.isNullString(batchTransferRulesVO.getReceiverServiceClassID()) && !BTSLUtil.isNullString(batchTransferRulesVO.getSubServiceTypeId()) && !BTSLUtil.isNullString(batchTransferRulesVO.getServiceType())) {
						psmtSelectTransferRule.setString(1, batchTransferRulesVO.getModule());
						psmtSelectTransferRule.setString(2, batchTransferRulesVO.getNetworkCode());
						psmtSelectTransferRule.setString(3, batchTransferRulesVO.getSenderSubscriberType());
						psmtSelectTransferRule.setString(4, batchTransferRulesVO.getSenderServiceClassID());
						psmtSelectTransferRule.setString(5, batchTransferRulesVO.getReceiverSubscriberType());
						psmtSelectTransferRule.setString(6, batchTransferRulesVO.getReceiverServiceClassID());
						psmtSelectTransferRule.setString(7, batchTransferRulesVO.getSubServiceTypeId());
						psmtSelectTransferRule.setString(8, batchTransferRulesVO.getServiceType());
						psmtSelectTransferRule.setString(9, batchTransferRulesVO.getGatewayCode());
						psmtSelectTransferRule.setString(10, batchTransferRulesVO.getGradeCode());
						psmtSelectTransferRule.setString(11, batchTransferRulesVO.getCategoryCode());
						rsSelectTransferRule = psmtSelectTransferRule.executeQuery();
						if (rsSelectTransferRule.next()) {
							if (!batchTransferRulesVO.getCardGroupSetID().equals(rsSelectTransferRule.getString("CARD_GROUP_SET_ID")) || !batchTransferRulesVO.getStatus().equals(rsSelectTransferRule.getString("status"))) {
								this.updateTransferRule(psmtTransferUpdate, batchTransferRulesVO);
								con.commit();
								continue Start;
							} else {
								errorVO = new ListValueVO("", batchTransferRulesVO.getRecordNumber(), RestAPIStringParser.getMessage(locale, "transferrules.createbatchtransferrules.msg.error.transferruleuniqueerr",arr));
								dbErrorList.add(errorVO);
								BatchesLog.transferRuleLog("BATCH_TRF_RULES_CREATION", batchTransferRulesVO, null, RestAPIStringParser.getMessage(locale,"transferrules.createbatchtransferrules.msg.error.transferruleuniqueerr",arr));
								batchTransferRulesVO.setError("Transfer rule for " + Arrays.toString(arr) + " already exists");
								errorLoglist.add(batchTransferRulesVO);
								continue Start;
							}

						}
						psmtSelectTransferRule.clearParameters();
					}

					if (PretupsI.TRANSFER_RULE_STATUS_ACTIVE.equals(batchTransferRulesVO.getStatus())) {
						insertTransferRuleCount = this.addTransferRule(psmtTransferRulesInsert, batchTransferRulesVO);
						if (insertTransferRuleCount <= 0) {
							con.rollback();
							errorVO = new ListValueVO("", batchTransferRulesVO.getRecordNumber(), RestAPIStringParser.getMessage(locale,"transferrules.createbatchtransferrules.msg.error.transferruleinsertfail",arr));
							dbErrorList.add(errorVO);
							BatchesLog.transferRuleLog("BATCH_TRF_RULES_CREATION", batchTransferRulesVO, null, RestAPIStringParser.getMessage(locale,"transferrules.createbatchtransferrules.msg.error.transferruleinsertfail",arr));
							batchTransferRulesVO.setError("Transfer rule creation failed for " + Arrays.toString(arr));
							errorLoglist.add(batchTransferRulesVO);
							continue Start;
						}else{
							con.commit();
						}
					} else {
						errorVO = new ListValueVO("", batchTransferRulesVO.getRecordNumber(), RestAPIStringParser.getMessage(locale,"transferrules.createbatchtransferrules.msg.error.notransferruleforsuspend",arr));
						dbErrorList.add(errorVO);
						BatchesLog.transferRuleLog("BATCH_TRF_RULES_CREATION", batchTransferRulesVO, null, RestAPIStringParser.getMessage(locale,"transferrules.createbatchtransferrules.msg.error.notransferruleforsuspend",arr));
						batchTransferRulesVO.setError("No transfer rule exists for suspend " + Arrays.toString(arr));
						errorLoglist.add(batchTransferRulesVO);
						continue Start;
					}
				}
			}

		} catch (SQLException sqe) {
			try {
				if (con != null) {
					con.rollback();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			log.error("addC2sTransferRulesList", "SQLException : " + sqe);
			log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchTransferRulesDAO[addC2sTransferRulesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "addC2sTransferRulesList", "error.general.sql.processing");
		} catch (Exception ex) {
			try {
				if (con != null) {
					con.rollback();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			log.error("addTransferRulesList", "Exception : " + ex);
			log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchTransferRulesDAO[addC2sTransferRulesList]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "addC2sTransferRulesList", "error.general.processing");
		} finally {
			try {
				if (psmtSelectTransferRule != null) psmtSelectTransferRule.close();
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (psmtBatchInsert != null) psmtBatchInsert.close();
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (psmtBatchUpdate != null) psmtBatchUpdate.close();
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (psmtTransferUpdate != null) psmtTransferUpdate.close();
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (psmtTransferRulesDelete != null) psmtTransferRulesDelete.close();
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (psmtTransferRulesInsert != null) psmtTransferRulesInsert.close();
			} catch (Exception e) {

				log.errorTrace(methodName, e);
			}
			try {
				if (rsSelectTransferRule != null) rsSelectTransferRule.close();
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			if (log.isDebugEnabled())
				log.debug("addC2sTransferRulesList", "Exiting: insertCount=" + insertTransferRuleCount);
		}
		return dbErrorList;

	}
}
