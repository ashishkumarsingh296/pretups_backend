package com.btsl.pretups.processes.businesslogic;

import static com.btsl.db.util.DBConstants.PHONE_PROFILE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

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
import com.btsl.pretups.adjustments.businesslogic.DiffCalBL;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductServiceTypeCache;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.ibm.icu.util.Calendar;

public class HandleUnsettledCombinedCasesQueries {

    private static Log _log = LogFactory.getLog(HandleUnsettledCombinedCasesQueries.class.getName());
    private HandleUnsettledCombinedCasesQry handleUnsettledCasesQry = (HandleUnsettledCombinedCasesQry) ObjectProducer.getObject(QueryConstants.HANDLE_UNSETTLED_COMBINED_CASE,QueryConstants.QUERY_PRODUCER);
    

    public String loadC2STransferVO() {
        String selectQuery =  handleUnsettledCasesQry.loadC2STransferVOQuery();
        if (_log.isDebugEnabled()) {
            _log.debug("loadC2STransferVO", "select query:" + selectQuery);
        }
        return selectQuery;
    }
    
    public String loadC2STransferListQuery() {
        String selectQuery =  handleUnsettledCasesQry.loadC2STransferListQuery();
        if (_log.isDebugEnabled()) {
            _log.debug("loadC2STransferListQuery", "select query:" + selectQuery);
        }
        return selectQuery;
    }
    

    public String markC2SReceiverAmbiguous() {
        //local index implemented
        StringBuffer updateQueryBuff = new StringBuffer(" UPDATE c2s_transfers ");
        updateQueryBuff.append("SET credit_status=transfer_status,transfer_status=? WHERE  transfer_id=? and transfer_date=? ");
        String updateQuery = updateQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("updateTransferItemDetails", "Update query:" + updateQuery);
        }
        return updateQuery;
    }

    public String updateReconcilationStatus() {
        String query = handleUnsettledCasesQry.updateReconcilationStatusQuery();
        if (_log.isDebugEnabled()) {
            _log.debug("updateReconcilationStatus", "Query=" + query);
        }
        return query;

    }

    // public PreparedStatement addC2STransferItemDetailsQuery(Connection p_con)
    // throws SQLException
    // {
    // PreparedStatement pstmtInsert = null;
    // StringBuffer insertQueryBuff = new StringBuffer("INSERT INTO
    // c2s_transfer_items (transfer_id, sno,msisdn, entry_date, request_value,
    // previous_balance, post_balance, user_type, transfer_type, entry_type, ");
    // insertQueryBuff.append(" validation_status, update_status,
    // service_class_id,protocol_status,account_status, ");
    // insertQueryBuff.append(" transfer_value, interface_type, interface_id,
    // interface_response_code, ");
    // insertQueryBuff.append(" interface_reference_id, subscriber_type,
    // service_class_code, msisdn_previous_expiry, ");
    // insertQueryBuff.append(" msisdn_new_expiry, transfer_status,
    // transfer_date,
    // transfer_date_time,first_call,entry_date_time,prefix_id,reference_id,language,country)
    // ");
    // insertQueryBuff.append(" VALUES
    // (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
    // String insertQuery = insertQueryBuff.toString();
    // if (_log.isDebugEnabled())
    // _log.debug("addC2STransferItemDetailsQuery", "insertQuery=" +
    // insertQuery);
    // pstmtInsert = p_con.prepareStatement(insertQuery);
    // return pstmtInsert;
    //
    // }
    public String loadP2PReconciliationVO() {
        String selectQuery =handleUnsettledCasesQry.loadP2PReconciliationVOQuery();
        if (_log.isDebugEnabled()) {
            _log.debug("loadP2PReconciliationVO", "select query:" + selectQuery);
        }
        return selectQuery;
    }

    public String loadP2PReconciliationList() {
        String selectQuery =handleUnsettledCasesQry.loadP2PReconciliationList();
        if (_log.isDebugEnabled()) {
        	_log.debug("loadP2PReconciliationVO", "select query:" + selectQuery);
        }
        return selectQuery;
    }

    
    public String loadP2PReconciliationItemsList() {
        String P2PTransferItemsSelectQuery = handleUnsettledCasesQry.loadP2PReconciliationItemsListQuery();

        if (_log.isDebugEnabled()) {
            _log.debug("loadP2PReconciliationItemsList", "select query:" + P2PTransferItemsSelectQuery);
        }
        return P2PTransferItemsSelectQuery;
    }

    public String markP2PReceiverAmbiguous() {
        StringBuffer updateQueryBuff = new StringBuffer(" UPDATE transfer_items ");
        updateQueryBuff.append("SET update_status=transfer_status,transfer_status=? WHERE  transfer_id=? AND user_type=? ");
        String updateQuery = updateQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("markP2PReceiverAmbiguous", "Update query:" + updateQuery);
        }

        return updateQuery;
    }

    public String updateP2PReconcilationStatus() {
        
        String query = handleUnsettledCasesQry.updateP2PReconcilationStatusQuery();
        if (_log.isDebugEnabled()) {
            _log.debug("updateP2PReconcilationStatus", "Query=" + query);
        }
        return query;

    }

    public String addP2PTransferItemDetailsQuery() {
        StringBuffer insertQueryBuff = new StringBuffer(" INSERT INTO transfer_items (transfer_id, sno,prefix_id,msisdn, entry_date, request_value, previous_balance, post_balance, user_type, transfer_type, entry_type, ");
        insertQueryBuff.append(" validation_status, update_status, service_class_id,protocol_status,account_status, ");
        insertQueryBuff.append(" transfer_value, interface_type, interface_id, interface_response_code, ");
        insertQueryBuff.append(" interface_reference_id, subscriber_type, service_class_code, msisdn_previous_expiry, ");
        insertQueryBuff.append(" msisdn_new_expiry, transfer_status, transfer_date, transfer_date_time,entry_date_time,first_call,reference_id) ");
        insertQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        String insertQuery = insertQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("addP2PTransferItemDetailsQuery", "insertQuery=" + insertQuery);
        }
        return insertQuery;

    }

    public String loadC2SVOMSDetail() {
        StringBuffer selectQueryBuff = new StringBuffer(" select v.serial_no SERIALNO, v.current_status CURRENTSTAT,  v.previous_status PREVSTAT, v.status  STAT ,PIN_NO ");
        selectQueryBuff.append(" FROM voms_vouchers v WHERE v.SERIAL_NO=? ");

        String c2sVOMSDetailsSelectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("loadC2SVOMSDetail", "select query:" + c2sVOMSDetailsSelectQuery);
        }
        return c2sVOMSDetailsSelectQuery;
    }

    public String updateVoucherQuery() {
        StringBuffer updateQueryBuff = new StringBuffer(" UPDATE voms_vouchers SET   ");
        updateQueryBuff.append(" current_status=?, previous_status=?, status=?, ");
        updateQueryBuff.append(" modified_by=?, modified_on=?, last_consumed_on=? ");
        updateQueryBuff.append(" WHERE serial_no=? ");
        String updateQuery = updateQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("updateVoucherQuery", "updateQuery=" + updateQuery);
        }
        	return updateQuery;


    }

    public String updateVoucherAuditQuery() {
        StringBuffer updateQueryBuff = new StringBuffer(" UPDATE voms_voucher_audit SET   ");
        updateQueryBuff.append(" current_status=?, previous_status=?, ");
        updateQueryBuff.append(" modified_by=?, modified_on=? ");
        updateQueryBuff.append(" WHERE serial_no=? ");
        String updateQuery = updateQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("updateVoucherAuditQuery", "updateQuery=" + updateQuery);
        }
        return updateQuery;

    }

    /**
     * Method to prepare the new List that needs to be added in the items table
     * at the time of reconciliation
     * 
     * @param p_con
     *            Connection
     * @param p_c2sTransferVO
     *            C2STransferVO
     * @param p_status
     *            String
     * @param p_forwardPath
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList prepareNewC2SReconList(Connection p_con, C2STransferVO p_c2sTransferVO, String p_status, String p_forwardPath, PreparedStatement p_pstmtRestrictedSubDetailSelectLock, PreparedStatement p_pstmtRestrictedSubDetailSelect, PreparedStatement p_pstmtRestrictedMSISDNtxnCountUpdate, PreparedStatement p_pstmtChannelUserDetailSelect, PreparedStatement p_pstmtChannelUserServiceListSelect, PreparedStatement p_pstmtUserBalanceSelect, PreparedStatement p_pstmtUserDailyBalanceInsert, PreparedStatement p_pstmtUserDailyBalancesUpdate, PreparedStatement p_pstmtMessageGatewayTypeSelect, PreparedStatement p_pstmtUserProdBalanceSelect, PreparedStatement p_pstmtUserBalanceUpdate, PreparedStatement p_pstmtUserThreshHoldCountInsert, PreparedStatement p_pstmtTransferProfileProductSelect, PreparedStatement p_pstmtTransferCountsSelect, PreparedStatement p_pstmtTransferCountsWithLockSelect, PreparedStatement p_pstmtUserTransferCountsUpdate, PreparedStatement p_pstmtUserTransferCountsInsert, PreparedStatement p_pstmtTransferProfileSelect, PreparedStatement p_pstmtdEffTrfProfileProductListSelect) throws BTSLBaseException {
        ArrayList newList = null;
        final String METHOD_NAME = "prepareNewC2SReconList";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered Transfer ID:" + p_c2sTransferVO.getTransferID() + " p_status to be made=" + p_status + ", p_forwardPath=" + p_forwardPath);
        }
        try {
            ArrayList p_oldItemsList = p_c2sTransferVO.getTransferItemList();
            int listSize = p_oldItemsList.size();
            C2STransferItemVO receiverItemVO = null;
            String receiverStatus = null;
            Date currentDate = new Date();
            boolean creditedBackInAmb = false;
            C2STransferItemVO creditBackItemVO = null;
            String creditBackStatus = null;
            /*
             * here we check if orginal list size is 2 then the creditBack is
             * not done yet other wise if list size is 3 then sender's
             * creditBack is done at the time of txn.
             */
            switch (listSize) {
            case 2: {
                receiverItemVO = (C2STransferItemVO) p_oldItemsList.get(1);
                receiverStatus = receiverItemVO.getTransferStatus();
                /*
                 * here we are checking only AMBIGOUS case only in future
                 * UNDERPROCESS case may be considered
                 */
                if (receiverStatus.equals(InterfaceErrorCodesI.AMBIGOUS)) {
                    /*
                     * make that creditBack is not done and call other method to
                     * handle Ambiguous case
                     */
                    creditedBackInAmb = false;
                    newList = handleReceiverAmbigousCase(p_con, p_c2sTransferVO, p_status, listSize, currentDate, creditedBackInAmb, p_pstmtRestrictedSubDetailSelectLock, p_pstmtRestrictedSubDetailSelect, p_pstmtRestrictedMSISDNtxnCountUpdate, p_pstmtChannelUserDetailSelect, p_pstmtChannelUserServiceListSelect, p_pstmtUserBalanceSelect, p_pstmtUserDailyBalanceInsert, p_pstmtUserDailyBalancesUpdate, p_pstmtMessageGatewayTypeSelect, p_pstmtUserProdBalanceSelect, p_pstmtUserBalanceUpdate, p_pstmtUserThreshHoldCountInsert, p_pstmtTransferProfileProductSelect, p_pstmtTransferCountsSelect, p_pstmtTransferCountsWithLockSelect, p_pstmtUserTransferCountsUpdate, p_pstmtUserTransferCountsInsert, p_pstmtTransferProfileSelect, p_pstmtdEffTrfProfileProductListSelect);
                }
            }
                break;
            case 3: {
                receiverItemVO = (C2STransferItemVO) p_oldItemsList.get(1);
                receiverStatus = receiverItemVO.getTransferStatus();
                /*
                 * make that creditBack is done and call other method to handle
                 * Ambiguous case
                 */
                creditBackItemVO = (C2STransferItemVO) p_oldItemsList.get(2);
                creditBackStatus = creditBackItemVO.getTransferStatus();

                if (!BTSLUtil.isNullString(creditBackStatus)) {
                    if (creditBackStatus.equals(InterfaceErrorCodesI.SUCCESS)) {
                        creditedBackInAmb = true;
                    }
                    if (creditBackStatus.equals(InterfaceErrorCodesI.FAIL)) {
                        creditedBackInAmb = false;
                    }
                }
                /*
                 * At this time if creditBack is done then if the Txn is be make
                 * as success then we have to debit the user otherwise we have
                 * to credit the user.
                 */
                if (receiverStatus.equals(InterfaceErrorCodesI.AMBIGOUS)) {
                    newList = handleReceiverAmbigousCase(p_con, p_c2sTransferVO, p_status, listSize, currentDate, creditedBackInAmb, p_pstmtRestrictedSubDetailSelectLock, p_pstmtRestrictedSubDetailSelect, p_pstmtRestrictedMSISDNtxnCountUpdate, p_pstmtChannelUserDetailSelect, p_pstmtChannelUserServiceListSelect, p_pstmtUserBalanceSelect, p_pstmtUserDailyBalanceInsert, p_pstmtUserDailyBalancesUpdate, p_pstmtMessageGatewayTypeSelect, p_pstmtUserProdBalanceSelect, p_pstmtUserBalanceUpdate, p_pstmtUserThreshHoldCountInsert, p_pstmtTransferProfileProductSelect, p_pstmtTransferCountsSelect, p_pstmtTransferCountsWithLockSelect, p_pstmtUserTransferCountsUpdate, p_pstmtUserTransferCountsInsert, p_pstmtTransferProfileSelect, p_pstmtdEffTrfProfileProductListSelect);
                }
            }
                break;
            }
        } catch (BTSLBaseException be) {
            _log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Exiting for Transfer ID:" + p_c2sTransferVO.getTransferID() + " With Size=" + newList.size());
        }
        return newList;
    }

    /**
     * Method to handle the Receiver Ambigous case in the C2S module
     * 
     * @param p_con
     *            Connection
     * @param p_c2sTransferVO
     *            C2STransferVO
     * @param p_status
     * @param p_size
     * @param p_date
     * @param p_isAlreadyCreditBack
     * @return ArrayList
     * @throws BTSLBaseException
     */
    private ArrayList handleReceiverAmbigousCase(Connection p_con, C2STransferVO p_c2sTransferVO, String p_status, int p_size, Date p_date, boolean p_isAlreadyCreditBack, PreparedStatement p_pstmtRestrictedSubDetailSelectLock, PreparedStatement p_pstmtRestrictedSubDetailSelect, PreparedStatement p_pstmtRestrictedMSISDNtxnCountUpdate, PreparedStatement p_pstmtChannelUserDetailSelect, PreparedStatement p_pstmtChannelUserServiceListSelect, PreparedStatement p_pstmtUserBalanceSelect, PreparedStatement p_pstmtUserDailyBalanceInsert, PreparedStatement p_pstmtUserDailyBalancesUpdate, PreparedStatement p_pstmtMessageGatewayTypeSelect, PreparedStatement p_pstmtUserProdBalanceSelect, PreparedStatement p_pstmtUserBalanceUpdate, PreparedStatement p_pstmtUserThreshHoldCountInsert, PreparedStatement p_pstmtTransferProfileProductSelect, PreparedStatement p_pstmtTransferCountsSelect, PreparedStatement p_pstmtTransferCountsWithLockSelect, PreparedStatement p_pstmtUserTransferCountsUpdate, PreparedStatement p_pstmtUserTransferCountsInsert, PreparedStatement p_pstmtTransferProfileSelect, PreparedStatement p_pstmtdEffTrfProfileProductListSelect) throws BTSLBaseException {
        final String METHOD_NAME = "handleReceiverAmbigousCase";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered Transfer ID:" + p_c2sTransferVO.getTransferID() + " p_status to be made=" + p_status + " p_size=" + p_size + " p_date=" + p_date + "p_isAlreadyCreditBack=" + p_isAlreadyCreditBack);
        }
        ArrayList transList = new ArrayList();
        ArrayList p_transferItemList = p_c2sTransferVO.getTransferItemList();
        int listSize = p_transferItemList.size();
        UserBalancesVO userBalancesVO = null;
        try {
            C2STransferItemVO c2sTransferItemVO = null;
            /*
             * if request if for fail then check creditBack status of the txn if
             * not done then credit back the sender balance and update also
             * users daily balances
             */
            ChannelUserVO channelUserVO = null;
            if ("Fail".equals(p_status)) {
                channelUserVO = decreaseRestrictedSubscriberThresholds(p_c2sTransferVO, p_pstmtRestrictedSubDetailSelectLock, p_pstmtRestrictedSubDetailSelect, p_pstmtRestrictedMSISDNtxnCountUpdate, p_pstmtChannelUserDetailSelect, p_pstmtChannelUserServiceListSelect, p_pstmtMessageGatewayTypeSelect);
                if (!p_isAlreadyCreditBack) {

                    userBalancesVO = prepareUserBalanceVOFromTransferVO(p_c2sTransferVO, PretupsI.TRANSFER_TYPE_RCH_CREDIT, p_c2sTransferVO.getSourceType(), PretupsI.CREDIT, PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);

                    if (channelUserVO != null && !BTSLUtil.isNullString(channelUserVO.getAddress1())) {
                        System.out.println("channelUserVO.getAddress1()=" + channelUserVO.getAddress1());
                    }

                    if (channelUserVO != null) {
                        userBalancesVO.setTransferProfileID(channelUserVO.getTransferProfileID());
                    }
                    // updation of the date as only date was coming not the time
                    userBalancesVO.setLastTransferOn(p_c2sTransferVO.getModifiedOn());
                    // update user's daily balances
                    int updateCount = updateUserDailyBalances(p_c2sTransferVO.getModifiedOn(), userBalancesVO, p_pstmtUserBalanceSelect, p_pstmtUserDailyBalanceInsert, p_pstmtUserDailyBalancesUpdate);
                    if (updateCount <= 0) {
                        throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
                    }

                    // Debit the sender balance
                    // updateCount=new
                    // UserBalancesDAO().creditUserBalances(p_con,userBalancesVO);
                    updateCount = creditUserBalances(userBalancesVO, p_pstmtUserProdBalanceSelect, p_pstmtUserBalanceUpdate);
                    if (updateCount <= 0) {
                        throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
                    }
                    /*
                     * Now decrease the countes of the channel users if
                     * applicable as DAY,WEEK,MONTH,YEAR changes
                     */
                    UserTransferCountsVO userTransferCountsVO = decreaseC2STransferOutCounts(p_c2sTransferVO, true, p_date, p_pstmtTransferCountsSelect, p_pstmtTransferCountsWithLockSelect);
                    if (userTransferCountsVO != null) {
                        updateCount = updateUserTransferCounts(userTransferCountsVO, true, p_pstmtUserTransferCountsUpdate, p_pstmtUserTransferCountsInsert);
                        if (_log.isDebugEnabled()) {
                            _log.debug(METHOD_NAME, "Exited  updateCount " + updateCount);
                        }
                        if (updateCount <= 0) {
                            throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
                        }
                    }
                    // set the credit_back_status for the successful txn
                    p_c2sTransferVO.setCreditBackStatus(PretupsI.TXN_STATUS_SUCCESS);
                    // ends here
                    /*
                     * Update Previous and Post balances of sender in sender
                     * Item, creating new Items VO for credit back
                     */
                    C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) p_c2sTransferVO.getTransferItemList().get(0);
                    listSize = listSize + 1;
                    c2STransferItemVO.setSNo(listSize);
                    c2STransferItemVO.setEntryType(PretupsI.CREDIT);
                    c2STransferItemVO.setEntryDate(p_date);
                    c2STransferItemVO.setEntryDateTime(p_date);
                    c2STransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_RECON);
                    c2STransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                    c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                    c2STransferItemVO.setPreviousBalance(userBalancesVO.getPreviousBalance());
                    c2STransferItemVO.setPostBalance(userBalancesVO.getBalance());
                    transList.add(0, c2STransferItemVO);

                    // By sandeep goel ID RECON001
                    // to send SMS to the channel user.
                    // "Transaction ID {0} of date {1} for customer {2} of
                    // amount {3} is made failed, your account is credited back
                    // and your new balance is {4}"
                    String[] messageArgArray = { p_c2sTransferVO.getTransferID(), BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_c2sTransferVO.getTransferDate())), p_c2sTransferVO.getReceiverMsisdn(), PretupsBL.getDisplayAmount(p_c2sTransferVO.getTransferValue()), PretupsBL.getDisplayAmount(userBalancesVO.getBalance()) };
                    p_c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(), PretupsErrorCodesI.RECON_C2S_ADJUSTMENT_FAIL_MSG2, messageArgArray));
                    // ends here
                    // By sandeep goel ID RECON001
                    // set other informaiton for the balance logger informaiton.
                    userBalancesVO.setOtherInfo("IN C2S RECONCILIATION PROCESS TXN IS MADE FAIL AND CHANNEL USER ACCOUNT IS CREDITED BACK");
                    userBalancesVO.setRequestedQuantity(String.valueOf(p_c2sTransferVO.getTransferValue()));
                    p_c2sTransferVO.setOtherInfo1(userBalancesVO);
                } else {
                    // By sandeep goel ID RECON001
                    // to send SMS to the channel user.
                    // �Transaction ID {0} of date {1} for customer {2} of
                    // amount {3} is made fail�
                    String[] messageArgArray = { p_c2sTransferVO.getTransferID(), BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_c2sTransferVO.getTransferDate())), p_c2sTransferVO.getReceiverMsisdn(), PretupsBL.getDisplayAmount(p_c2sTransferVO.getTransferValue()) };
                    p_c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(), PretupsErrorCodesI.RECON_C2S_ADJUSTMENT_FAIL_MSG1, messageArgArray));
                    // ends here
                }
                
                //Handling of Failed transaction message in case of reconciliation
                try {
					PushMessage pushMessage=new PushMessage(p_c2sTransferVO.getSenderMsisdn(),p_c2sTransferVO.getSenderReturnMessage(),null,null,(((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale());
					pushMessage.push();
				} catch (Exception e) {
					 _log.errorTrace(METHOD_NAME, e);
				}
                
                /*
                 * creating the new object for the receiver's entry.
                 */
                // c2sTransferItemVO=prepareC2STransferItemsVO((C2STransferItemVO)p_transferItemList.get(1),PretupsErrorCodesI.TXN_STATUS_FAIL,listSize,p_date,PretupsI.CREDIT);
                // transList.add(c2sTransferItemVO);
            }
            /*
             * if request if for success then creating the new object for the
             * receiver's entry. and check that if user is already credit back
             * then debit the user balance and update user's daily balance and
             * also update the thresholds
             */
            else if ("Success".equals(p_status)) {
                if (p_isAlreadyCreditBack) {

                    userBalancesVO = prepareUserBalanceVOFromTransferVO(p_c2sTransferVO, PretupsI.TRANSFER_TYPE_RCH_DEBIT, p_c2sTransferVO.getSourceType(), PretupsI.DEBIT, PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);
                    // updation of the date as only date was coming not the time
                    userBalancesVO.setLastTransferOn(p_c2sTransferVO.getModifiedOn());
                    updateUserDailyBalances(p_c2sTransferVO.getModifiedOn(), userBalancesVO, p_pstmtUserBalanceSelect, p_pstmtUserDailyBalanceInsert, p_pstmtUserDailyBalancesUpdate);
                    // Debit the sender
                    // int
                    // updateCount=_userBalancesDAO.debitUserBalances(p_con,userBalancesVO,((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getTransferProfileID(),p_c2sTransferVO.getProductCode(),false);
                    int updateCount = debitUserBalances(userBalancesVO, ((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getTransferProfileID(), p_c2sTransferVO.getProductCode(), false, p_pstmtUserProdBalanceSelect, p_pstmtTransferProfileProductSelect, p_pstmtUserBalanceUpdate);
                    if (updateCount <= 0) {
                        throw new BTSLBaseException("ChannelUserBL", "debitUserBalanceForProduct", PretupsErrorCodesI.C2S_ERROR_NOT_DEBIT_BALANCE);
                    }

                    /*
                     * Now increase the countes of the channel users if
                     * applicable as DAY,WEEK,MONTH,YEAR changes
                     */
                    p_c2sTransferVO.setCreatedOn(p_date);// Done expicilty so
                    // that checks in
                    // increase counters
                    // work
                    p_c2sTransferVO.setRequestedAmount(p_c2sTransferVO.getQuantity());// Done
                    // expicilty
                    // so
                    // that
                    // checks
                    // in
                    // increase
                    // counters
                    // work
                    increaseC2STransferOutCounts(p_c2sTransferVO, false, p_pstmtUserTransferCountsUpdate, p_pstmtUserTransferCountsInsert, p_pstmtTransferCountsSelect, p_pstmtTransferCountsWithLockSelect, p_pstmtTransferProfileSelect, p_pstmtdEffTrfProfileProductListSelect);

                    /*
                     * Update Previous and Post balances of sender in sender
                     * Item, creating new Items VO for credit back
                     */
                    C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) p_c2sTransferVO.getTransferItemList().get(0);
                    listSize = listSize + 1;
                    c2STransferItemVO.setSNo(listSize);
                    c2STransferItemVO.setEntryType(PretupsI.DEBIT);
                    c2STransferItemVO.setEntryDate(p_date);
                    c2STransferItemVO.setEntryDateTime(p_date);
                    c2STransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_RECON);
                    c2STransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                    c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                    c2STransferItemVO.setPreviousBalance(userBalancesVO.getPreviousBalance());
                    c2STransferItemVO.setPostBalance(userBalancesVO.getBalance());

                    transList.add(0, c2STransferItemVO);
                    // By sandeep goel ID RECON001
                    // set other informaiton for the balance logger informaiton.
                    userBalancesVO.setOtherInfo("IN C2S RECONCILIATION PROCESS TXN IS MADE SUCCESS AND CHANNEL USER ACCOUNT IS DEBITED");
                    userBalancesVO.setRequestedQuantity(String.valueOf(p_c2sTransferVO.getTransferValue()));
                    p_c2sTransferVO.setOtherInfo1(userBalancesVO);
                }
                // By sandeep goel ID RECON001
                // check if differential commission is applicable then give it
                // to the channel user.
                // if differential commission is applicable with the service
                // type then the commission would be given
                // to the channel user otherwise no differential commission
                // would be given
                if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED)).contains(p_c2sTransferVO.getServiceType())) {
                    p_c2sTransferVO.setSelectorCode(p_c2sTransferVO.getSubService());
                } else {
                    p_c2sTransferVO.setSelectorCode(PretupsI.DEFAULT_SUBSERVICE);
                }
                ListValueVO productServiceTypeVO = NetworkProductServiceTypeCache.getProductServiceValueVO(p_c2sTransferVO.getServiceType(), p_c2sTransferVO.getSelectorCode());
                if ("Y".equalsIgnoreCase(productServiceTypeVO.getValue())) {
                    p_c2sTransferVO.setDifferentialAllowedForService(productServiceTypeVO.getValue());
                    p_c2sTransferVO.setGiveOnlineDifferential(productServiceTypeVO.getType());
                    
                	if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,p_c2sTransferVO.getNetworkCode()))
					{
						if(!p_c2sTransferVO.getServiceType().equalsIgnoreCase(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL))
						{
							ChannelTransferBL.increaseUserOTFCounts(p_con, p_c2sTransferVO, (ChannelUserVO)p_c2sTransferVO.getSenderVO());
						}
						else if(p_c2sTransferVO.getServiceType().equalsIgnoreCase(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)) 
						{						
							ChannelTransferBL.decreaseUserOTFCounts(p_con, p_c2sTransferVO, (ChannelUserVO)p_c2sTransferVO.getSenderVO());
						}
					}
                	
                    DiffCalBL diffCalBL = new DiffCalBL();
                    try {
                        diffCalBL.differentialCalculationsForRecon(p_con, p_c2sTransferVO, PretupsI.C2S_MODULE);
                    } catch (BTSLBaseException be) {
                        if (!("UNIQUE_CONSTRAINT".equals(be.getMessage()) && PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS.equals(p_c2sTransferVO.getTxnStatus()))) {
                            throw be;
                        }
                        _log.errorTrace(METHOD_NAME, be);
                    }

                }
                // following message would be send if differential commission is
                // not applicable or differential
                // commission is 0
                if (!"Y".equalsIgnoreCase(productServiceTypeVO.getValue()) || BTSLUtil.isNullString(p_c2sTransferVO.getSenderReturnMessage())) {
                    if (p_isAlreadyCreditBack) {

                        // to send SMS to the channel user.
                        // "Transaction ID {0} of date {1} for customer {2} of
                        // amount {3} is made successful, and your new balance
                        // is {4}"
                        String[] messageArgArray = { p_c2sTransferVO.getTransferID(), BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_c2sTransferVO.getTransferDate())), p_c2sTransferVO.getReceiverMsisdn(), PretupsBL.getDisplayAmount(p_c2sTransferVO.getTransferValue()), PretupsBL.getDisplayAmount(userBalancesVO.getBalance()) };
                        p_c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(), PretupsErrorCodesI.RECON_C2S_ADJUSTMENT_SUCCESS_MSG2, messageArgArray));
                        // ends here
                    } else {
                        // to send SMS to the channel user.
                        // �Transaction ID {0} of date {1} for customer {2} of
                        // amount {3} is made successful�
                        String[] messageArgArray = { p_c2sTransferVO.getTransferID(), BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_c2sTransferVO.getTransferDate())), p_c2sTransferVO.getReceiverMsisdn(), PretupsBL.getDisplayAmount(p_c2sTransferVO.getTransferValue()) };
                        p_c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(), PretupsErrorCodesI.RECON_C2S_ADJUSTMENT_SUCCESS_MSG1, messageArgArray));
                        // ends here
                    }
                }
                // ends here
                if (transList != null && transList.isEmpty()) {
                    C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) p_c2sTransferVO.getTransferItemList().get(0);
                    c2STransferItemVO.setEntryType(PretupsI.CREDIT);
                    transList.add(0, c2STransferItemVO);
                }
                // c2sTransferItemVO=prepareC2STransferItemsVO((C2STransferItemVO)p_transferItemList.get(1),PretupsErrorCodesI.TXN_STATUS_SUCCESS,listSize,p_date,PretupsI.CREDIT);
                // transList.add(c2sTransferItemVO);
            }
        } catch (BTSLBaseException be) {
            _log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Exiting transList=" + transList.size());
        }
        return transList;
    }

    /**
     * Method to prepare the User balance VO from the C2S Transfer VO for
     * updation of balances
     * 
     * @param p_c2sTransferVO
     * @param p_transferType
     * @param p_source
     * @param p_entryType
     * @param p_transType
     * @param p_transferCategory
     * @return UserBalancesVO
     */
    private UserBalancesVO prepareUserBalanceVOFromTransferVO(C2STransferVO p_c2sTransferVO, String p_transferType, String p_source, String p_entryType, String p_transType, String p_transferCategory) {
        UserBalancesVO userBalancesVO = UserBalancesVO.getInstance();
        userBalancesVO.setUserID(p_c2sTransferVO.getSenderID());
        userBalancesVO.setProductCode(p_c2sTransferVO.getProductCode());
        userBalancesVO.setProductName(p_c2sTransferVO.getProductName());
        userBalancesVO.setProductShortName(p_c2sTransferVO.getProductName());
        userBalancesVO.setNetworkCode(p_c2sTransferVO.getNetworkCode());
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue()) {
            userBalancesVO.setNetworkFor(p_c2sTransferVO.getNetworkCode());
        } else {
            userBalancesVO.setNetworkFor(p_c2sTransferVO.getReceiverNetworkCode());
        }

        userBalancesVO.setLastTransferID(p_c2sTransferVO.getTransferID());
        userBalancesVO.setSource(p_source);
        userBalancesVO.setCreatedBy(p_c2sTransferVO.getCreatedBy());
        userBalancesVO.setEntryType(p_entryType);
        userBalancesVO.setType(p_transType);
        userBalancesVO.setTransferCategory(p_transferCategory);
        userBalancesVO.setRequestedQuantity(String.valueOf(p_c2sTransferVO.getRequestedAmount()));
        userBalancesVO.setLastTransferType(p_transferType);
        userBalancesVO.setLastTransferOn(p_c2sTransferVO.getCreatedOn());
        userBalancesVO.setQuantityToBeUpdated(((C2STransferItemVO) p_c2sTransferVO.getTransferItemList().get(0)).getTransferValue());
        userBalancesVO.setUserMSISDN(p_c2sTransferVO.getSenderMsisdn());
        return userBalancesVO;
    }

    /**
     * Method to prepare the VO that needs to be inserted in database
     * 
     * @param p_c2sTransferItemVO
     *            C2STransferItemVO
     * @param p_status
     * @param p_size
     * @param p_date
     * @param p_entryType
     * @return TransferItemVO
     */
    // private C2STransferItemVO prepareC2STransferItemsVO(C2STransferItemVO
    // p_c2sTransferItemVO,String p_status,int p_size,Date p_date,String
    // p_entryType)
    // {
    // C2STransferItemVO c2sTransferItemVO=new C2STransferItemVO();
    // c2sTransferItemVO.setTransferID(p_c2sTransferItemVO.getTransferID());
    // c2sTransferItemVO.setMsisdn(p_c2sTransferItemVO.getMsisdn());
    // c2sTransferItemVO.setEntryDate(p_date);
    // c2sTransferItemVO.setRequestValue(p_c2sTransferItemVO.getRequestValue());
    // c2sTransferItemVO.setUserType(p_c2sTransferItemVO.getUserType());
    // c2sTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_RECON);
    // c2sTransferItemVO.setEntryType(p_entryType);
    // c2sTransferItemVO.setTransferValue(p_c2sTransferItemVO.getTransferValue());
    // c2sTransferItemVO.setSubscriberType(p_c2sTransferItemVO.getSubscriberType());
    // c2sTransferItemVO.setServiceClassCode(p_c2sTransferItemVO.getServiceClassCode());
    // c2sTransferItemVO.setTransferStatus(p_status);
    // c2sTransferItemVO.setTransferDate(p_c2sTransferItemVO.getTransferDate());
    // c2sTransferItemVO.setTransferDateTime(p_c2sTransferItemVO.getTransferDateTime());
    // c2sTransferItemVO.setEntryDateTime(p_date);
    // c2sTransferItemVO.setSNo(p_size+1);
    // c2sTransferItemVO.setPrefixID(p_c2sTransferItemVO.getPrefixID());
    // c2sTransferItemVO.setServiceClass(p_c2sTransferItemVO.getServiceClass());
    // c2sTransferItemVO.setPreviousBalance(p_c2sTransferItemVO.getPreviousBalance());
    // c2sTransferItemVO.setPostBalance(p_c2sTransferItemVO.getPostBalance());
    // c2sTransferItemVO.setValidationStatus(p_c2sTransferItemVO.getValidationStatus());
    // c2sTransferItemVO.setUpdateStatus(p_c2sTransferItemVO.getUpdateStatus());
    // c2sTransferItemVO.setUpdateStatus1(p_c2sTransferItemVO.getUpdateStatus1());
    // c2sTransferItemVO.setUpdateStatus2(p_c2sTransferItemVO.getUpdateStatus2());
    // c2sTransferItemVO.setInterfaceType(p_c2sTransferItemVO.getInterfaceType());
    // c2sTransferItemVO.setInterfaceID(p_c2sTransferItemVO.getInterfaceID());
    // c2sTransferItemVO.setInterfaceReferenceID(p_c2sTransferItemVO.getInterfaceReferenceID());
    // c2sTransferItemVO.setInterfaceResponseCode(p_c2sTransferItemVO.getInterfaceResponseCode());
    // c2sTransferItemVO.setPreviousExpiry(p_c2sTransferItemVO.getPreviousExpiry());
    // c2sTransferItemVO.setNewExpiry(p_c2sTransferItemVO.getNewExpiry());
    // c2sTransferItemVO.setAccountStatus(p_c2sTransferItemVO.getAccountStatus());
    // c2sTransferItemVO.setProtocolStatus(p_c2sTransferItemVO.getProtocolStatus());
    // return c2sTransferItemVO;
    // }
    /**
     * This method is used to decrease count and amount of the restricted
     * subscriber on reconciliation Method
     * :decreaseRestrictedSubscriberThresholds
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_c2sTransferVO
     *            C2STransferVO
     * @return int
     * @throws BTSLBaseException
     */
    public ChannelUserVO decreaseRestrictedSubscriberThresholds(C2STransferVO p_c2sTransferVO, PreparedStatement p_pstmtRestrictedSubDetailSelectLock, PreparedStatement p_pstmtRestrictedSubDetailSelect, PreparedStatement p_pstmtRestrictedMSISDNtxnCountUpdate, PreparedStatement p_pstmtChannelUserDetailSelect, PreparedStatement p_pstmtChannelUserServiceListSelect, PreparedStatement p_pstmtMessageGatewayTypeSelect) {
        final String METHOD_NAME = "decreaseRestrictedSubscriberThresholds";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered: p_c2sTransferVO" + p_c2sTransferVO);
        }
        int updateCount = 0;
        ChannelUserVO channelUserVO = null;

        String ownerId = p_c2sTransferVO.getOwnerUserID();
        String senderMsisdn = p_c2sTransferVO.getSenderMsisdn();
        String receiverMsisdn = p_c2sTransferVO.getReceiverMsisdn();
        long amount = p_c2sTransferVO.getTransferValue();
        Date transferDate = p_c2sTransferVO.getTransferDate();
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        try {
            channelUserVO = loadChannelUserDetails(senderMsisdn, p_pstmtChannelUserDetailSelect, p_pstmtChannelUserServiceListSelect, p_pstmtMessageGatewayTypeSelect);
            if (channelUserVO != null && PretupsI.STATUS_ACTIVE.equalsIgnoreCase(channelUserVO.getCategoryVO().getRestrictedMsisdns()) && PretupsI.STATUS_ACTIVE.equalsIgnoreCase(channelUserVO.getCategoryVO().getTransferToListOnly())) {
                Date currentDate = new Date();
                int transferYear = transferDate.getYear();
                int transferMonth = transferDate.getMonth();
                int currentYear = currentDate.getYear();
                int currentMonth = currentDate.getMonth();
                if ((transferYear == currentYear) && (currentMonth == transferMonth)) {
                    restrictedSubscriberVO = loadRestrictedSubscriberDetails(ownerId, receiverMsisdn, true, p_pstmtRestrictedSubDetailSelectLock, p_pstmtRestrictedSubDetailSelect);
                    if (restrictedSubscriberVO != null && restrictedSubscriberVO.getLastTransferOn() != null) {
                        if (p_pstmtRestrictedMSISDNtxnCountUpdate != null) {
                            p_pstmtRestrictedMSISDNtxnCountUpdate.clearParameters();
                            int i = 1;
                            p_pstmtRestrictedMSISDNtxnCountUpdate.setLong(i++, restrictedSubscriberVO.getMonthlyTransferCount() - 1);
                            p_pstmtRestrictedMSISDNtxnCountUpdate.setLong(i++, restrictedSubscriberVO.getMonthlyTransferAmount() - amount);
                            p_pstmtRestrictedMSISDNtxnCountUpdate.setString(i++, restrictedSubscriberVO.getOwnerID());
                            p_pstmtRestrictedMSISDNtxnCountUpdate.setString(i++, restrictedSubscriberVO.getMsisdn());
                            updateCount = p_pstmtRestrictedMSISDNtxnCountUpdate.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException sqe) {
            _log.error(METHOD_NAME, "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
        } catch (Exception ex) {
            _log.error(METHOD_NAME, "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting: channelUserVO:" + channelUserVO + ", updateCount=" + updateCount);
            }
        }
        return channelUserVO;
    }

    /**
     * This method is used to load the details of a restricted subscriber Method
     * :loadRestrictedSubscriberDetails
     * 
     * @param p_con
     *            java.sql.Connection
     * @param _c2sTransferVO
     *            C2STransferVO
     * @param p_channelUserId
     *            String
     * @param p_msisdn
     *            String
     * @param p_amount
     *            long
     * @param p_doLock
     *            boolean
     * @throws BTSLBaseException
     */
    public RestrictedSubscriberVO loadRestrictedSubscriberDetails(String p_ownerId, String p_msisdn, boolean p_doLock, PreparedStatement p_pstmtRestrictedSubDetailSelectLock, PreparedStatement p_pstmtRestrictedSubDetailSelect) throws BTSLBaseException {
        final String METHOD_NAME = "loadRestrictedSubscriberDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered: p_ownerId=" + p_ownerId + ", p_msisdn=" + p_msisdn + ", p_doLock=" + p_doLock);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        try {
            if (p_doLock) {
                pstmtSelect = p_pstmtRestrictedSubDetailSelectLock;
            } else {
                pstmtSelect = p_pstmtRestrictedSubDetailSelect;
            }
            int i = 1;
            if (pstmtSelect != null) {
                pstmtSelect.clearParameters();
                pstmtSelect.setString(i++, p_msisdn);
                pstmtSelect.setString(i++, p_ownerId);
                pstmtSelect.setString(i++, PretupsI.STATUS_ACTIVE);
                pstmtSelect.setString(i++, PretupsI.STATUS_SUSPEND);
                rs = pstmtSelect.executeQuery();
            }

            if (rs != null && rs.next()) {
                restrictedSubscriberVO = new RestrictedSubscriberVO();
                restrictedSubscriberVO.setMsisdn(p_msisdn);
                restrictedSubscriberVO.setSubscriberID(rs.getString("subscriber_id"));
                restrictedSubscriberVO.setChannelUserID(rs.getString("channel_user_id"));
                restrictedSubscriberVO.setChannelUserCategory(rs.getString("channel_user_category"));
                restrictedSubscriberVO.setOwnerID(rs.getString("owner_id"));
                restrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                restrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                restrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                restrictedSubscriberVO.setMinTxnAmount(rs.getLong("min_txn_amount"));
                restrictedSubscriberVO.setMaxTxnAmount(rs.getLong("max_txn_amount"));
                restrictedSubscriberVO.setBlackListStatus(rs.getString("black_list_status"));
                restrictedSubscriberVO.setRemarks(rs.getString("remark"));
                restrictedSubscriberVO.setApprovedBy(rs.getString("approved_by"));
                restrictedSubscriberVO.setApprovedOn(rs.getDate("approved_on"));
                restrictedSubscriberVO.setAssociatedBy(rs.getString("associated_by"));
                restrictedSubscriberVO.setAssociationDate(rs.getDate("association_date"));
                restrictedSubscriberVO.setMonthlyTransferAmount(rs.getLong("total_txn_amount"));
                restrictedSubscriberVO.setStatus(rs.getString("status"));
                restrictedSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                restrictedSubscriberVO.setMonthlyTransferCount(rs.getLong("total_txn_count"));
                restrictedSubscriberVO.setLanguage(rs.getString("language"));
                restrictedSubscriberVO.setCountry("country");
                restrictedSubscriberVO.setLastTransferOn(rs.getDate("last_transaction_date"));
            }
        } catch (SQLException sqe) {
            _log.error(METHOD_NAME, "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(METHOD_NAME, "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting restrictedSubscriberVO:" + restrictedSubscriberVO);
            }
        }
        return restrictedSubscriberVO;
    }

    public String loadRestrictedSubscriberDetailsQuery() {
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT subscriber_id, channel_user_id, channel_user_category,  owner_id, ");
        strBuff.append(" employee_code, employee_name, monthly_limit,  min_txn_amount, max_txn_amount, ");
        strBuff.append(" total_txn_count, total_txn_amount,  black_list_status, remark, ");
        strBuff.append(" approved_by, approved_on, associated_by,  status, association_date, last_transaction_date, ");
        strBuff.append(" subscriber_type, language, country FROM restricted_msisdns WHERE msisdn = ? AND owner_id = ? ");
        strBuff.append(" AND status IN( ? , ? ) ");
        String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("loadRestrictedSubscriberDetailsQuery", "select query:" + sqlSelect);
        }
        return sqlSelect;
    }

    public String loadRestrictedSubscriberDetailsQueryLock() {
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT subscriber_id, channel_user_id, channel_user_category,  owner_id, ");
        strBuff.append(" employee_code, employee_name, monthly_limit,  min_txn_amount, max_txn_amount, ");
        strBuff.append(" total_txn_count, total_txn_amount,  black_list_status, remark, ");
        strBuff.append(" approved_by, approved_on, associated_by,  status, association_date, last_transaction_date, ");
        strBuff.append(" subscriber_type, language, country FROM restricted_msisdns WHERE msisdn = ? AND owner_id = ? ");
        strBuff.append(" AND status IN( ? , ? ) ");
        // DB220120123for update WITH RS
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            strBuff.append("FOR UPDATE WITH RS ");
        } else {
            strBuff.append("FOR UPDATE ");
        }
        String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("loadRestrictedSubscriberDetailsQueryLock", "select query:" + sqlSelect);
        }
        return sqlSelect;
    }

    public String updateRestrictedMSISDNtxnCounQuery() {
        String query = null;
        query = ("UPDATE restricted_msisdns SET total_txn_count=? , total_txn_amount=? WHERE owner_id=? AND msisdn=? ");
        ;
        if (_log.isDebugEnabled()) {
            _log.debug("updateRestrictedMSISDNtxnCounQuery", "Query=" + query);
        }

        return query;
    }

    /**
     * Method loadChannelUserDetails. This method load user information by his
     * msisdn
     * 
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @return ChannelUserVO
     * @throws BTSLBaseException
     */
    public ChannelUserVO loadChannelUserDetails(String p_msisdn, PreparedStatement p_pstmtChannelUserDetailSelect, PreparedStatement p_pstmtChannelUserServiceListSelect, PreparedStatement p_pstmtMessageGatewayTypeSelect) throws BTSLBaseException {

        final String METHOD_NAME = "loadChannelUserDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered p_msisdn:" + p_msisdn);
        }
        ChannelUserVO channelUserVO = null;
        ResultSet rs = null;
        try {
            if (p_pstmtChannelUserDetailSelect != null) {
                p_pstmtChannelUserDetailSelect.clearParameters();
                p_pstmtChannelUserDetailSelect.setString(1, p_msisdn);
                p_pstmtChannelUserDetailSelect.setString(2, PretupsI.USER_STATUS_DELETED);
                p_pstmtChannelUserDetailSelect.setString(3, PretupsI.USER_STATUS_CANCELED);
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, "Before result:" + p_msisdn);
                }
                rs = p_pstmtChannelUserDetailSelect.executeQuery();
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, "After result:" + p_msisdn);
                }
            }

            if (rs != null && rs.next()) {
                channelUserVO = ChannelUserVO.getInstance();
                channelUserVO.setUserID(rs.getString("user_id"));
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setNetworkID(rs.getString("network_code"));
                channelUserVO.setLoginID(rs.getString("login_id"));
                channelUserVO.setPassword(rs.getString("webpassword"));
                channelUserVO.setCategoryCode(rs.getString("category_code"));
                channelUserVO.setParentID(rs.getString("parent_id"));
                channelUserVO.setOwnerID(rs.getString("owner_id"));
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                channelUserVO.setEmpCode(rs.getString("employee_code"));
                channelUserVO.setStatus(rs.getString("userstatus"));
                channelUserVO.setCreatedBy(rs.getString("created_by"));
                channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                channelUserVO.setModifiedBy(rs.getString("modified_by"));
                channelUserVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                channelUserVO.setContactPerson(rs.getString("contact_person"));
                channelUserVO.setContactNo(rs.getString("contact_no"));
                channelUserVO.setDesignation(rs.getString("designation"));
                channelUserVO.setDivisionCode(rs.getString("division"));
                channelUserVO.setDepartmentCode(rs.getString("department"));
                channelUserVO.setUserType(rs.getString("user_type"));
                channelUserVO.setInSuspend(rs.getString("in_suspend"));
                channelUserVO.setOutSuspened(rs.getString("out_suspend"));
                channelUserVO.setAddress1(rs.getString("address1"));
                channelUserVO.setAddress2(rs.getString("address2"));
                channelUserVO.setCity(rs.getString("city"));
                channelUserVO.setState(rs.getString("state"));
                channelUserVO.setCountry(rs.getString("country"));
                channelUserVO.setSsn(rs.getString("ssn"));
                channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                channelUserVO.setExternalCode(rs.getString("external_code"));
                channelUserVO.setUserCode(rs.getString("user_code"));
                channelUserVO.setShortName(rs.getString("short_name"));
                channelUserVO.setReferenceID(rs.getString("reference_id"));
                channelUserVO.setDomainID(rs.getString("domain_code"));
                channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
                channelUserVO.setGeographicalCodeStatus(rs.getString("geostatus"));

                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelUserVO.setTransferProfileStatus(rs.getString("tpstatus"));
                channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelUserVO.setCommissionProfileStatus(rs.getString("csetstatus"));
                channelUserVO.setUserGrade(rs.getString("user_grade"));
                channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));

                // for Zebra and Tango by sanjeew date 06/07/07
                channelUserVO.setAccessType(rs.getString("access_type"));
                channelUserVO.setApplicationID(rs.getString("application_id"));
                channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
                channelUserVO.setUserProfileID(rs.getString("user_profile_id"));
                channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
                channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                channelUserVO.setFromTime(rs.getString("from_time"));
                channelUserVO.setToTime(rs.getString("to_time"));
                channelUserVO.setAllowedDays(rs.getString("allowed_days"));
                // end Zebra and Tango
                CategoryVO categoryVO = CategoryVO.getInstance();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("catseq"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
                categoryVO.setAllowedGatewayTypes(loadMessageGatewayTypeListForCategory(categoryVO.getCategoryCode(), p_pstmtMessageGatewayTypeSelect));
                categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
                categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
                categoryVO.setCategoryType(rs.getString("category_type"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                categoryVO.setGrphDomainSequenceNo(rs.getInt("grphSeq"));
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
                categoryVO.setUserIdPrefix(rs.getString("USER_ID_PREFIX"));
                channelUserVO.setCategoryVO(categoryVO);
                UserPhoneVO userPhoneVO = new UserPhoneVO();
                userPhoneVO.setUserPhonesId(rs.getString("user_phones_id"));
                userPhoneVO.setMsisdn(p_msisdn);
                userPhoneVO.setUserId(rs.getString("user_id"));
                userPhoneVO.setPrimaryNumber(rs.getString("primary_number"));
                userPhoneVO.setSmsPin(rs.getString("sms_pin"));
                userPhoneVO.setPinRequired(rs.getString("pin_required"));
                userPhoneVO.setPhoneProfile(rs.getString(PHONE_PROFILE));
                userPhoneVO.setPhoneLanguage(rs.getString("phlang"));
                userPhoneVO.setCountry(rs.getString("phcountry"));
                userPhoneVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
                userPhoneVO.setLastTransactionStatus(rs.getString("last_transaction_status"));
                userPhoneVO.setLastTransactionOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_transaction_on")));
                userPhoneVO.setPinModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("pin_modified_on")));
                userPhoneVO.setLastTransferID(rs.getString("last_transfer_id"));
                userPhoneVO.setLastTransferType(rs.getString("last_transfer_type"));
                userPhoneVO.setPrefixID(rs.getLong("prefix_id"));
                userPhoneVO.setTempTransferID(rs.getString("temp_transfer_id"));
                userPhoneVO.setFirstInvalidPinTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("first_invalid_pin_time")));
                userPhoneVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("userphone_created_on")));
                channelUserVO.setUserPhoneVO(userPhoneVO);
                channelUserVO.setAssociatedServiceTypeList(loadUserServicesList(channelUserVO.getUserID(), p_pstmtChannelUserServiceListSelect));
            }
            return channelUserVO;
        }// end of try
        catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting channelUserVO:" + channelUserVO);
            }
        }// end of finally
    }

    /**
     * Method for loading Users Assigned Services List(means Services that are
     * assigned to the user). From the table USER_SERVICES
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * 
     */
    public ArrayList loadUserServicesList(String p_userId, PreparedStatement p_pstmtChannelUserServiceListSelect) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserServicesList";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered p_userId=" + p_userId);
        }
        ResultSet rs = null;
        ArrayList list = new ArrayList();
        try {
            if (p_pstmtChannelUserServiceListSelect != null) {
                p_pstmtChannelUserServiceListSelect.clearParameters();
                p_pstmtChannelUserServiceListSelect.setString(1, p_userId);
                rs = p_pstmtChannelUserServiceListSelect.executeQuery();
            }
            while (rs != null && rs.next()) {
                list.add(new ListValueVO(rs.getString("status"), rs.getString("service_type")));
            }
        } catch (SQLException sqe) {
            _log.error(METHOD_NAME, "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(METHOD_NAME, "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting: userServicesList size=" + list.size());
            }
        }
        return list;
    }

    public String loadUserServiceListQuery() {
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT US.service_type,US.status FROM user_services US,users U,category_service_type CST");
        strBuff.append(" WHERE US.user_id = ? AND US.status <> 'N'");
        strBuff.append(" AND U.user_id=US.user_id AND U.category_code=CST.category_code AND CST.service_type=US.service_type  and CST.network_code=U.network_code ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserServiceListQuery", "select query:" + sqlSelect);
        }
        return sqlSelect;
    }

    public String loadChannelUserDetailQuery() {
        String selectQuery = handleUnsettledCasesQry.loadChannelUserDetailQuery();

        if (_log.isDebugEnabled()) {
            _log.debug("loadChannelUserDetailQuery", "select query:" + selectQuery);
        }
        return selectQuery;
    }

    /**
     * Method updateUserDailyBalances. Method to update the user balances
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_executionDate
     *            Date
     * @param p_userBalancesVO
     *            UserBalancesVO
     * @return int
     * @throws BTSLBaseException
     */

    public int updateUserDailyBalances(Date p_executionDate, UserBalancesVO p_userBalancesVO, PreparedStatement p_pstmtUserBalanceSelect, PreparedStatement p_pstmtUserDailyBalanceInsert, PreparedStatement p_pstmtUserDailyBalancesUpdate) throws BTSLBaseException {
        final String METHOD_NAME = "updateUserDailyBalances";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered p_executionDate=" + p_executionDate + ", p_userBalancesVO = " + p_userBalancesVO);
        }
        ResultSet rs = null;
        int count = 1;
        try {
            if (p_pstmtUserDailyBalanceInsert != null) {
                p_pstmtUserDailyBalanceInsert.clearParameters();
            }
            Date dailyBalanceUpdatedOn = null;

            int dayDifference = 0;

            // select the record form the userBalances table.
            if (p_pstmtUserBalanceSelect != null) {
                p_pstmtUserBalanceSelect.clearParameters();
                p_pstmtUserBalanceSelect.setString(1, p_userBalancesVO.getUserID());
                p_pstmtUserBalanceSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_executionDate));
                rs = p_pstmtUserBalanceSelect.executeQuery();
                p_pstmtUserBalanceSelect.clearParameters();
            }

            while (rs != null && rs.next()) {
                dailyBalanceUpdatedOn = rs.getDate("daily_balance_updated_on");
                // if record exist check updated on date with current date
                // day differences to maintain the record of previous days.
                dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyBalanceUpdatedOn, p_executionDate);
                if (dayDifference > 0) {
                    // if dates are not equal get the day differencts and
                    // execute insert qurery no of times of the
                    if (_log.isDebugEnabled()) {
                        _log.debug("updateUserDailyBalances ", "Till now daily Stock is not updated on " + p_executionDate + ", day differences = " + dayDifference);
                    }

                    for (int k = 0; k < dayDifference; k++) {
                        if (p_pstmtUserDailyBalanceInsert != null) {
                            p_pstmtUserDailyBalanceInsert.setDate(1, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn, k)));
                            p_pstmtUserDailyBalanceInsert.setString(2, rs.getString("user_id"));
                            p_pstmtUserDailyBalanceInsert.setString(3, rs.getString("network_code"));

                            p_pstmtUserDailyBalanceInsert.setString(4, rs.getString("network_code_for"));
                            p_pstmtUserDailyBalanceInsert.setString(5, rs.getString("product_code"));
                            p_pstmtUserDailyBalanceInsert.setLong(6, rs.getLong("balance"));
                            p_pstmtUserDailyBalanceInsert.setLong(7, rs.getLong("prev_balance"));
                            p_pstmtUserDailyBalanceInsert.setString(8, p_userBalancesVO.getLastTransferType());

                            p_pstmtUserDailyBalanceInsert.setString(9, p_userBalancesVO.getLastTransferID());
                            p_pstmtUserDailyBalanceInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                            p_pstmtUserDailyBalanceInsert.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_executionDate));
                            p_pstmtUserDailyBalanceInsert.setString(12, PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
                            count = p_pstmtUserDailyBalanceInsert.executeUpdate();
							
							// added to make code compatible with insertion in partitioned table in postgres
							count = BTSLUtil.getInsertCount(count); 
							
                            if (count <= 0) {
                                throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
                            }
                            p_pstmtUserDailyBalanceInsert.clearParameters();
                        }
                    }
                    if (p_pstmtUserDailyBalancesUpdate != null) {
                        p_pstmtUserDailyBalancesUpdate.clearParameters();
                        p_pstmtUserDailyBalancesUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(p_executionDate));
                        p_pstmtUserDailyBalancesUpdate.setString(2, p_userBalancesVO.getUserID());
                        count = p_pstmtUserDailyBalancesUpdate.executeUpdate();
                        if (count <= 0) {
                            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
                        }
                        p_pstmtUserDailyBalancesUpdate.clearParameters();
                    }
                }
            }// end of while loop
        } catch (BTSLBaseException be) {
            _log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting count = " + count);
            }
        }
        return count;
    }

    public String loadUserBalanceQuery() {
        String userBalanceSelectQuery = handleUnsettledCasesQry.loadUserBalanceQuery();

        if (_log.isDebugEnabled()) {
            _log.debug("loadUserBalanceQuery", "select query:" + userBalanceSelectQuery);
        }
        return userBalanceSelectQuery;
    }

    public String updateUserDailyBalancesQuery() {
        StringBuffer updateStrBuff = new StringBuffer();
        updateStrBuff.append("UPDATE user_balances SET daily_balance_updated_on = ? ");
        updateStrBuff.append("WHERE user_id = ? ");
        String updateQuery = updateStrBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("updateUserDailyBalancesQuery", "Update query:" + updateQuery);
        }
        return updateQuery;
    }

    public String addUserDailyBalancesQuery() {
        StringBuffer insertStrBuff = new StringBuffer();
        insertStrBuff.append("INSERT INTO user_daily_balances(balance_date, user_id, network_code, ");
        insertStrBuff.append("network_code_for, product_code, balance, prev_balance, last_transfer_type, ");
        insertStrBuff.append("last_transfer_no, last_transfer_on, created_on,creation_type )");
        insertStrBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?) ");
        String insertQuery = insertStrBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("addUserDailyBalancesQuery", "insertQuery=" + insertQuery);
        }
        return insertQuery;

    }

    /**
     * This method is same as that of loadMessageGatewayTypeList but only store
     * the Gateway Type in ArrayList
     * 
     * @param p_con
     * @param p_categoryCode
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadMessageGatewayTypeListForCategory(String p_categoryCode, PreparedStatement p_pstmtMessageGatewayTypeSelect) throws BTSLBaseException {
        final String METHOD_NAME = "loadMessageGatewayTypeListForCategory";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered: with p_categoryCode=" + p_categoryCode);
        }
        ResultSet rs = null;
        ArrayList messageGatewayTypeList = null;
        try {
            if (p_pstmtMessageGatewayTypeSelect != null) {
                p_pstmtMessageGatewayTypeSelect.clearParameters();
                p_pstmtMessageGatewayTypeSelect.setString(1, p_categoryCode);
                rs = p_pstmtMessageGatewayTypeSelect.executeQuery();
            }
            messageGatewayTypeList = new ArrayList();
            while (rs != null && rs.next()) {
                messageGatewayTypeList.add(rs.getString("gateway_type"));
            }
        } catch (SQLException sqe) {
            _log.error(METHOD_NAME, "SQLException:" + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting:messageGatewayTypeList size=" + messageGatewayTypeList.size());
            }
        }
        return messageGatewayTypeList;
    }

    public String loadMessageGatewayTypeQuery() {
        StringBuffer selectQuery = new StringBuffer(" SELECT gateway_type  ");
        selectQuery.append(" FROM category_req_gtw_types  ");
        selectQuery.append(" WHERE category_code=? ");
        String selectQueryString = selectQuery.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("loadMessageGatewayTypeQuery", "select query:" + selectQueryString);
        }
        return selectQueryString;
    }

    /**
     * Method to Credit back the user balances for a user and a product
     * 
     * @param p_con
     * @param p_userBalancesVO
     * @param p_categoryCode
     * @return int
     * @throws BTSLBaseException
     */
    public int creditUserBalances(UserBalancesVO p_userBalancesVO, PreparedStatement p_pstmtUserProdBalanceSelect, PreparedStatement p_pstmtUserBalanceUpdate) throws BTSLBaseException {
        final String METHOD_NAME = "creditUserBalances";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered p_userBalancesVO : " + p_userBalancesVO.toString());
        }

        int updateCount = 0;
        long balance = 0;
        long newBalance = 0;
        ResultSet rs = null;
        // thresholdValue=(Long)PreferenceCache.getControlPreference(PreferenceI.ZERO_BAL_THRESHOLD_VALUE,p_userBalancesVO.getNetworkCode(),
        // p_categoryCode); //threshold value
        try {
            if (p_pstmtUserProdBalanceSelect != null) {
                p_pstmtUserProdBalanceSelect.clearParameters();
                p_pstmtUserProdBalanceSelect.setString(1, p_userBalancesVO.getUserID());
                p_pstmtUserProdBalanceSelect.setString(2, p_userBalancesVO.getProductCode());
                p_pstmtUserProdBalanceSelect.setString(3, p_userBalancesVO.getNetworkCode());
                p_pstmtUserProdBalanceSelect.setString(4, p_userBalancesVO.getNetworkFor());
                rs = p_pstmtUserProdBalanceSelect.executeQuery();
            }
            if (rs != null && rs.next()) {
                balance = rs.getLong("balance");
            }
            newBalance = balance + p_userBalancesVO.getQuantityToBeUpdated();

            if (p_pstmtUserBalanceUpdate != null) {
                p_pstmtUserBalanceUpdate.clearParameters();
            }
            p_userBalancesVO.setPreviousBalance(balance);
            p_userBalancesVO.setBalance(newBalance);
            if (p_pstmtUserBalanceUpdate != null) {
                p_pstmtUserBalanceUpdate.setLong(1, newBalance);
                p_pstmtUserBalanceUpdate.setString(2, p_userBalancesVO.getLastTransferType());
                p_pstmtUserBalanceUpdate.setString(3, p_userBalancesVO.getLastTransferID());
                p_pstmtUserBalanceUpdate.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                p_pstmtUserBalanceUpdate.setString(5, p_userBalancesVO.getUserID());
                p_pstmtUserBalanceUpdate.setString(6, p_userBalancesVO.getProductCode());
                p_pstmtUserBalanceUpdate.setString(7, p_userBalancesVO.getNetworkCode());
                p_pstmtUserBalanceUpdate.setString(8, p_userBalancesVO.getNetworkFor());
                updateCount = p_pstmtUserBalanceUpdate.executeUpdate();
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Update Balance: Old_balance: " + balance + " , New Balance: " + newBalance + ", p_userBalancesVO.getQuantityToBeUpdated():" + p_userBalancesVO.getQuantityToBeUpdated());
            }

        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            // Removing entry for Zero balance and thresh hold counter because
            // this is a batch process and we cannot maintain such counters
            // for zero balance counter..
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting for Transfer ID:" + p_userBalancesVO.getLastTransferID() + " User ID =" + p_userBalancesVO.getUserID() + " New Balance=" + newBalance);
            }
        }// end of finally
        return updateCount;
    }

    public String loadUserProductBalanceQuery() {
        
        String userBalSelectQuery = handleUnsettledCasesQry.loadUserProductBalanceQuery();

        if (_log.isDebugEnabled()) {
            _log.debug("loadUserProductBalanceQuery", "select query:" + userBalSelectQuery);
        }
        return userBalSelectQuery;
    }

    public String updateUserBalanceQuery() {
        StringBuffer strBuffUpdate = new StringBuffer();
        strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
        strBuffUpdate.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? ");
        String updateQuery = strBuffUpdate.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("updateUserBalanceQuery", "updateQuery=" + updateQuery);
        }

        return updateQuery;
    }

    public String addUserThresholdCounterQuery() {
        StringBuffer strBuffThresholdInsert = new StringBuffer();
        strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
        strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , ");
        strBuffThresholdInsert.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
        strBuffThresholdInsert.append(" VALUES ");
        strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        String insertQuery = strBuffThresholdInsert.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("addUserThresholdCounterQuery", "insertQuery=" + insertQuery);
        }
        return insertQuery;

    }

    /**
     * Load the tarnsfer Profile Product. It have minimumresdual balance and max
     * balance and
     * 
     * @param p_con
     * @param p_profileID
     * @param p_productCode
     * @return TransferProfileProductVO
     * @throws BTSLBaseException
     */
    public TransferProfileProductVO loadTransferProfileProducts(String p_profileID, String p_productCode, PreparedStatement p_pstmtTransferProfileProductSelect) throws BTSLBaseException {
        final String METHOD_NAME = "loadTransferProfileProducts";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered: p_profileID=" + p_profileID + ", p_productCode=" + p_productCode);
        }

        ResultSet rs = null;
        TransferProfileProductVO transferProfileProductVO = null;
        try {
            if (p_pstmtTransferProfileProductSelect != null) {
                p_pstmtTransferProfileProductSelect.clearParameters();
                p_pstmtTransferProfileProductSelect.setString(1, p_profileID);
                p_pstmtTransferProfileProductSelect.setString(2, p_productCode);
                p_pstmtTransferProfileProductSelect.setString(3, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                p_pstmtTransferProfileProductSelect.setString(4, PretupsI.YES);
                rs = p_pstmtTransferProfileProductSelect.executeQuery();
            }
            if (rs != null && rs.next()) {
                transferProfileProductVO = TransferProfileProductVO.getInstance();
                transferProfileProductVO.setProductCode(rs.getString("product_code"));
                transferProfileProductVO.setMinResidualBalanceAsLong(rs.getLong("min_residual_balance"));
                transferProfileProductVO.setMaxBalanceAsLong(rs.getLong("max_balance"));
                transferProfileProductVO.setAltBalanceLong(rs.getLong("alerting_balance"));
                transferProfileProductVO.setAllowedMaxPercentageInt(rs.getInt("max_pct_transfer_allowed"));
                transferProfileProductVO.setC2sMinTxnAmtAsLong(rs.getLong("c2s_min_txn_amt"));
                transferProfileProductVO.setC2sMaxTxnAmtAsLong(rs.getLong("c2s_max_txn_amt"));
                transferProfileProductVO.setC2sMinTxnAmt(PretupsBL.getDisplayAmount(transferProfileProductVO.getC2sMinTxnAmtAsLong()));
                transferProfileProductVO.setC2sMaxTxnAmt(PretupsBL.getDisplayAmount(transferProfileProductVO.getC2sMaxTxnAmtAsLong()));

            }
        } catch (SQLException sqe) {
            _log.error(METHOD_NAME, "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(METHOD_NAME, "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting: ProfileVO =" + transferProfileProductVO);
            }
        }
        return transferProfileProductVO;
    }

    public String loadTransferProfileProductsQuery() {
        StringBuffer strBuff = new StringBuffer();

        strBuff.append("SELECT tpp.product_code,GREATEST(tpp.min_residual_balance,catpp.min_residual_balance) min_residual_balance, ");
        strBuff.append("GREATEST(tpp.c2s_min_txn_amt,catpp.c2s_min_txn_amt) c2s_min_txn_amt, ");
        strBuff.append(" LEAST(tpp.max_balance,catpp.max_balance) max_balance, ");
        strBuff.append("LEAST(tpp.c2s_max_txn_amt,catpp.c2s_max_txn_amt) c2s_max_txn_amt,tpp.alerting_balance, LEAST(tpp.max_pct_transfer_allowed,catpp.max_pct_transfer_allowed) max_pct_transfer_allowed ");
        strBuff.append(" FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
        strBuff.append(" WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
        strBuff.append(" AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status=? AND tp.network_code = catp.network_code  ");
        String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("loadTransferProfileProductsQuery", "select query:" + sqlSelect);
        }
        return sqlSelect;
    }

    /**
     * Method decreaseC2STransferOutCounts. This method is to decrease the C2S
     * transaction counters on the basis of the YEAR/MONTH/WEEK/DAY changes.
     * 
     * @param p_con
     *            Connection
     * @param p_c2sTransferVO
     *            C2STransferVO
     * @param p_isLockRecordForUpdate
     *            boolean
     * @param p_newDate
     *            Date
     * @return UserTransferCountsVO
     * @throws BTSLBaseException
     */
    public UserTransferCountsVO decreaseC2STransferOutCounts(C2STransferVO p_c2sTransferVO, boolean p_isLockRecordForUpdate, Date p_newDate, PreparedStatement p_pstmtTransferCountsSelect, PreparedStatement p_pstmtTransferCountsWithLockSelect) throws BTSLBaseException {
        final String METHOD_NAME = "decreaseC2STransferOutCounts";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered Transfer ID =" + p_c2sTransferVO.getTransferID() + "User   ID " + p_c2sTransferVO.getSenderID() + "p_isLockRecordForUpdate=" + p_isLockRecordForUpdate + ",p_newDate=" + p_newDate);
        }

        UserTransferCountsVO userTransferCountsVO = null;
        try {
            userTransferCountsVO = loadTransferCounts(p_c2sTransferVO.getSenderID(), p_isLockRecordForUpdate, p_pstmtTransferCountsSelect, p_pstmtTransferCountsWithLockSelect);

            /*
             * This condition will never true but for caution we are checking
             * it. if there is no userTransferCountsVO then return null;
             */
            if (userTransferCountsVO == null) {
                return userTransferCountsVO;
            }
            /*
             * To check which Counters needs to be reinitialized.
             */
            boolean isDayCounterChange = false;
            boolean isWeekCounterChange = false;
            boolean isMonthCounterChange = false;

            Date previousDate = p_c2sTransferVO.getTransferDate();
            /*
             * This condition will never true but for caution we are checking
             * it. if transferDate is null then return back null
             */
            if (previousDate != null) {
                Calendar cal = BTSLDateUtil.getInstance();
                cal.setTime(p_newDate);
                int presentDay = cal.get(Calendar.DAY_OF_MONTH);
                int presentWeek = cal.get(Calendar.WEEK_OF_MONTH);
                int presentMonth = cal.get(Calendar.MONTH);
                int presentYear = cal.get(Calendar.YEAR);
                cal.setTime(previousDate);
                int lastTrxWeek = cal.get(Calendar.WEEK_OF_MONTH);
                int lastTrxDay = cal.get(Calendar.DAY_OF_MONTH);
                int lastTrxMonth = cal.get(Calendar.MONTH);
                int lastTrxYear = cal.get(Calendar.YEAR);
                if (presentYear != lastTrxYear) {
                    return null;
                } else if (presentMonth != lastTrxMonth) {
                    return null;
                } else if (presentWeek != lastTrxWeek) {
                    isMonthCounterChange = true;
                } else if (presentDay != lastTrxDay) {
                    isMonthCounterChange = true;
                    isWeekCounterChange = true;
                } else {
                    isMonthCounterChange = true;
                    isWeekCounterChange = true;
                    isDayCounterChange = true;
                }
            } else {
                return null;
            }
            userTransferCountsVO.setLastTransferID(p_c2sTransferVO.getTransferID());
            //If we set the new date then in that case, counter reset functionality will not work and 
            //Settlement is being done for older transaction then it should not be consider as a new transaction date.
            //so last transaction date should not be changed
            userTransferCountsVO.setLastTransferDate(userTransferCountsVO.getLastTransferDate());
			
            /*
             * Check which counter need to be updated since there may be the
             * condition that we are useing same counter for C2C and C2S
             * transactions. This is based on the system preferences.
             */
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_C2S_SEPARATE_TRNSFR_COUNTS))).booleanValue()) {
                if (isDayCounterChange) {
                    if (userTransferCountsVO.getDailyC2STransferOutCount() > 0) {
                        userTransferCountsVO.setDailyC2STransferOutCount(userTransferCountsVO.getDailyC2STransferOutCount() - 1);
                    }
                    if (userTransferCountsVO.getDailyC2STransferOutValue() > 0) {
                        userTransferCountsVO.setDailyC2STransferOutValue(userTransferCountsVO.getDailyC2STransferOutValue() - p_c2sTransferVO.getTransferValue());
                    }
                }
                if (isWeekCounterChange) {
                    if (userTransferCountsVO.getWeeklyC2STransferOutCount() > 0) {
                        userTransferCountsVO.setWeeklyC2STransferOutCount(userTransferCountsVO.getWeeklyC2STransferOutCount() - 1);
                    }
                    if (userTransferCountsVO.getWeeklyC2STransferOutValue() > 0) {
                        userTransferCountsVO.setWeeklyC2STransferOutValue(userTransferCountsVO.getWeeklyC2STransferOutValue() - p_c2sTransferVO.getTransferValue());
                    }
                }
                if (isMonthCounterChange) {
                    if (userTransferCountsVO.getMonthlyC2STransferOutCount() > 0) {
                        userTransferCountsVO.setMonthlyC2STransferOutCount(userTransferCountsVO.getMonthlyC2STransferOutCount() - 1);
                    }
                    if (userTransferCountsVO.getMonthlyC2STransferOutValue() > 0) {
                        userTransferCountsVO.setMonthlyC2STransferOutValue(userTransferCountsVO.getMonthlyC2STransferOutValue() - p_c2sTransferVO.getTransferValue());
                    }
                }
            } else {
                if (isDayCounterChange) {
                    if (userTransferCountsVO.getDailyOutCount() > 0) {
                        userTransferCountsVO.setDailyOutCount(userTransferCountsVO.getDailyOutCount() - 1);
                    }
                    if (userTransferCountsVO.getDailyOutValue() > 0) {
                        userTransferCountsVO.setDailyOutValue(userTransferCountsVO.getDailyOutValue() - p_c2sTransferVO.getTransferValue());
                    }
                }
                if (isWeekCounterChange) {
                    if (userTransferCountsVO.getWeeklyOutCount() > 0) {
                        userTransferCountsVO.setWeeklyOutCount(userTransferCountsVO.getWeeklyOutCount() - 1);
                    }
                    if (userTransferCountsVO.getWeeklyOutValue() > 0) {
                        userTransferCountsVO.setWeeklyOutValue(userTransferCountsVO.getWeeklyOutValue() - p_c2sTransferVO.getTransferValue());
                    }
                }
                if (isMonthCounterChange) {
                    if (userTransferCountsVO.getMonthlyOutCount() > 0) {
                        userTransferCountsVO.setMonthlyOutCount(userTransferCountsVO.getMonthlyOutCount() - 1);
                    }
                    if (userTransferCountsVO.getMonthlyOutValue() > 0) {
                        userTransferCountsVO.setMonthlyOutValue(userTransferCountsVO.getMonthlyOutValue() - p_c2sTransferVO.getTransferValue());
                    }
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, "Exception p_transferID:" + p_c2sTransferVO.getTransferID() + " Exception:" + e.getMessage());
            throw new BTSLBaseException("HandleUnsettledCombinedCasesQueries", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exited with userTransferCountsVO=" + userTransferCountsVO);
        }
        return userTransferCountsVO;
    }

    /**
     * Method loadTransferCounts() This method Load the Transfer Counts by the
     * userID.
     * 
     * @param p_con
     * @param p_userId
     * @param p_isLockRecordForUpdate
     *            boolean
     * @return UserTransferCountsVO
     * @throws BTSLBaseException
     *             Added p_isLockRecordForUpdate so that same query can be used
     *             just before updating the records also :
     */
    public UserTransferCountsVO loadTransferCounts(String p_userId, boolean p_isLockRecordForUpdate, PreparedStatement p_pstmtTransferCountsSelect, PreparedStatement p_pstmtTransferCountsWithLockSelect) throws BTSLBaseException {

        final String METHOD_NAME = "loadTransferCounts";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered  From UserID " + p_userId + " p_isLockRecordForUpdate=" + p_isLockRecordForUpdate);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UserTransferCountsVO countsVO = null;
        try {
            pstmt = p_pstmtTransferCountsSelect;
            if (p_isLockRecordForUpdate) {
                pstmt = p_pstmtTransferCountsWithLockSelect;
            }
            if (pstmt != null) {
                pstmt.clearParameters();
                pstmt.setString(1, p_userId);
                rs = pstmt.executeQuery();
            }
            if (rs != null && rs.next()) {
                countsVO = UserTransferCountsVO.getInstance();
                countsVO.setUserID(rs.getString("user_id"));

                countsVO.setDailyInCount(rs.getLong("daily_in_count"));
                countsVO.setDailyInValue(rs.getLong("daily_in_value"));
                countsVO.setWeeklyInCount(rs.getLong("weekly_in_count"));
                countsVO.setWeeklyInValue(rs.getLong("weekly_in_value"));
                countsVO.setMonthlyInCount(rs.getLong("monthly_in_count"));
                countsVO.setMonthlyInValue(rs.getLong("monthly_in_value"));
                countsVO.setDailyOutCount(rs.getLong("daily_out_count"));
                countsVO.setDailyOutValue(rs.getLong("daily_out_value"));
                countsVO.setWeeklyOutCount(rs.getLong("weekly_out_count"));
                countsVO.setWeeklyOutValue(rs.getLong("weekly_out_value"));
                countsVO.setMonthlyOutCount(rs.getLong("monthly_out_count"));
                countsVO.setMonthlyOutValue(rs.getLong("monthly_out_value"));
                countsVO.setUnctrlDailyInCount(rs.getLong("outside_daily_in_count"));
                countsVO.setUnctrlDailyInValue(rs.getLong("outside_daily_in_value"));
                countsVO.setUnctrlWeeklyInCount(rs.getLong("outside_weekly_in_count"));
                countsVO.setUnctrlWeeklyInValue(rs.getLong("outside_weekly_in_value"));
                countsVO.setUnctrlMonthlyInCount(rs.getLong("outside_monthly_in_count"));
                countsVO.setUnctrlMonthlyInValue(rs.getLong("outside_monthly_in_value"));
                countsVO.setUnctrlDailyOutCount(rs.getLong("outside_daily_out_count"));
                countsVO.setUnctrlDailyOutValue(rs.getLong("outside_daily_out_value"));
                countsVO.setUnctrlWeeklyOutCount(rs.getLong("outside_weekly_out_count"));
                countsVO.setUnctrlWeeklyOutValue(rs.getLong("outside_weekly_out_value"));
                countsVO.setUnctrlMonthlyOutCount(rs.getLong("outside_monthly_out_count"));
                countsVO.setUnctrlMonthlyOutValue(rs.getLong("outside_monthly_out_value"));

                countsVO.setOutsideLastInTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("outside_last_in_time")));
                countsVO.setOutsideLastOutTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("outside_last_out_time")));
                countsVO.setLastInTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_in_time")));
                countsVO.setLastOutTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_out_time")));

                countsVO.setDailyC2STransferOutCount(rs.getLong("daily_subscriber_out_count"));
                countsVO.setDailyC2STransferOutValue(rs.getLong("daily_subscriber_out_value"));
                countsVO.setWeeklyC2STransferOutCount(rs.getLong("weekly_subscriber_out_count"));
                countsVO.setWeeklyC2STransferOutValue(rs.getLong("weekly_subscriber_out_value"));
                countsVO.setMonthlyC2STransferOutCount(rs.getLong("monthly_subscriber_out_count"));
                countsVO.setMonthlyC2STransferOutValue(rs.getLong("monthly_subscriber_out_value"));
                countsVO.setLastTransferID(rs.getString("last_transfer_id"));
                countsVO.setLastTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_transfer_date")));
            }
        } catch (SQLException sqe) {
            _log.error(METHOD_NAME, "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(METHOD_NAME, "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting:  UserTransferCountsVO =" + countsVO);
            }
        }
        return countsVO;
    }

    public String loadTransferCountsQuery() {
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT user_id, daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, monthly_in_count, ");
        strBuff.append(" monthly_in_value, daily_out_count, daily_out_value, weekly_out_count, weekly_out_value, ");
        strBuff.append(" monthly_out_count, monthly_out_value, outside_daily_in_count, outside_daily_in_value, ");
        strBuff.append(" outside_weekly_in_count, outside_weekly_in_value, outside_monthly_in_count, ");
        strBuff.append(" outside_monthly_in_value,  ");
        strBuff.append("  outside_daily_out_count, outside_daily_out_value, outside_weekly_out_count, ");
        strBuff.append(" outside_weekly_out_value, outside_monthly_out_count, outside_monthly_out_value, ");
        strBuff.append(" daily_subscriber_out_count, weekly_subscriber_out_count, monthly_subscriber_out_count, ");
        strBuff.append(" daily_subscriber_out_value, weekly_subscriber_out_value, monthly_subscriber_out_value, ");
        strBuff.append(" outside_last_in_time, last_in_time, last_out_time, outside_last_out_time,last_transfer_id,last_transfer_date ");
        strBuff.append(" FROM user_transfer_counts ");
        strBuff.append(" WHERE user_id = ? ");

        String transferCountsSelectQuery = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("loadTransferCountsQuery", "select query:" + transferCountsSelectQuery);
        }
        return transferCountsSelectQuery;
    }

    public String loadTransferCountsWithLockQuery() {
        String transferCountsWithLockSelectQuery = handleUnsettledCasesQry.loadTransferCountsWithLockQuery();

        if (_log.isDebugEnabled()) {
            _log.debug("loadTransferCountsWithLockQuery", "select query:" + transferCountsWithLockSelectQuery);
        }
        return transferCountsWithLockSelectQuery;
    }

    /**
     * update the user transferCounts
     * 
     * @param p_con
     * @param p_countsVO
     * @param p_exist
     * @return int
     * @throws BTSLBaseException
     */
    public int updateUserTransferCounts(UserTransferCountsVO p_countsVO, boolean p_exist, PreparedStatement p_pstmtUserTransferCountsUpdate, PreparedStatement p_pstmtUserTransferCountsInsert) throws BTSLBaseException {
        final String METHOD_NAME = "updateUserTransferCounts";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered p_countsVO : " + p_countsVO + " p_exist " + p_exist);
        }
        int updateCount = 0;
        PreparedStatement ptstmt = null;
        try {
            if (p_exist) {
                ptstmt = p_pstmtUserTransferCountsUpdate;
            } else {
                ptstmt = p_pstmtUserTransferCountsInsert;
            }
            if (ptstmt != null) {
                ptstmt.clearParameters();
            }
            this.updateTransferCount(ptstmt, p_countsVO);
            updateCount = ptstmt.executeUpdate();
            if (updateCount == 0) {
                throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
            }
        } catch (BTSLBaseException bbe) {
            _log.errorTrace(METHOD_NAME, bbe);
            throw bbe;
        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }// end of catch
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting Success :" + updateCount);
            }
        }// end of finally

        return updateCount;
    }

    public String updateUserTransferCountsQuery() {
        StringBuffer strBuffUpdate = new StringBuffer();
        strBuffUpdate.append(" UPDATE user_transfer_counts  SET ");
        strBuffUpdate.append(" daily_in_count = ?, daily_in_value = ?, weekly_in_count = ?, weekly_in_value = ?, monthly_in_count = ?, ");
        strBuffUpdate.append(" monthly_in_value = ?, daily_out_count = ?, daily_out_value = ?, weekly_out_count = ?, weekly_out_value = ?,  ");
        strBuffUpdate.append(" monthly_out_count = ? , monthly_out_value = ? , outside_daily_in_count = ?, outside_daily_in_value = ?, ");
        strBuffUpdate.append(" outside_weekly_in_count = ? , outside_weekly_in_value = ? , outside_monthly_in_count = ? , outside_monthly_in_value = ? , ");
        strBuffUpdate.append(" outside_daily_out_count = ?, ");
        strBuffUpdate.append(" outside_daily_out_value = ? , outside_weekly_out_count = ? , outside_weekly_out_value = ?, ");
        strBuffUpdate.append(" outside_monthly_out_count = ? , outside_monthly_out_value = ? , ");
        strBuffUpdate.append(" daily_subscriber_out_count = ? , weekly_subscriber_out_count = ? , monthly_subscriber_out_count = ?, ");
        strBuffUpdate.append(" daily_subscriber_out_value = ? , weekly_subscriber_out_value = ? , monthly_subscriber_out_value = ?, ");
        strBuffUpdate.append(" outside_last_in_time = ? , last_in_time = ? , last_out_time = ? , outside_last_out_time = ?,LAST_TRANSFER_ID=?,LAST_TRANSFER_DATE=? ");
        strBuffUpdate.append(" WHERE user_id = ?  ");
        String query = strBuffUpdate.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("updateUserTransferCountsQuery", "Update query:" + query);
        }
        return query;
    }

    public String addUserTransferCountsQuery() {
        StringBuffer strBuffUpdate = new StringBuffer();
        strBuffUpdate.append(" INSERT INTO user_transfer_counts ( ");
        strBuffUpdate.append(" daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, monthly_in_count, ");
        strBuffUpdate.append(" monthly_in_value, daily_out_count, daily_out_value, weekly_out_count, weekly_out_value, ");
        strBuffUpdate.append(" monthly_out_count, monthly_out_value, outside_daily_in_count, outside_daily_in_value, ");
        strBuffUpdate.append(" outside_weekly_in_count, outside_weekly_in_value, outside_monthly_in_count, outside_monthly_in_value, ");
        strBuffUpdate.append(" outside_daily_out_count, ");
        strBuffUpdate.append(" outside_daily_out_value, outside_weekly_out_count, outside_weekly_out_value, ");
        strBuffUpdate.append(" outside_monthly_out_count, outside_monthly_out_value ,  ");
        strBuffUpdate.append(" daily_subscriber_out_count , weekly_subscriber_out_count , monthly_subscriber_out_count , ");
        strBuffUpdate.append(" daily_subscriber_out_value , weekly_subscriber_out_value , monthly_subscriber_out_value , ");
        strBuffUpdate.append(" outside_last_in_time, last_in_time, last_out_time, outside_last_out_time,LAST_TRANSFER_ID,LAST_TRANSFER_DATE,");
        strBuffUpdate.append("  user_id ) ");
        strBuffUpdate.append(" VALUES ");
        strBuffUpdate.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        String insertQuery = strBuffUpdate.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("addUserTransferCountsQuery", "insertQuery=" + insertQuery);
        }
        return insertQuery;

    }

    /**
     * update counts
     * 
     * @param psmt
     * @param p_countsVO
     * @throws SQLException
     */
    private void updateTransferCount(PreparedStatement psmt, UserTransferCountsVO p_countsVO) throws SQLException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateUserTransferCounts", "Entered p_countsVO : " + p_countsVO);
        }

        int m = 0;
        psmt.setLong(++m, p_countsVO.getDailyInCount());
        psmt.setLong(++m, p_countsVO.getDailyInValue());
        psmt.setLong(++m, p_countsVO.getWeeklyInCount());
        psmt.setLong(++m, p_countsVO.getWeeklyInValue());
        psmt.setLong(++m, p_countsVO.getMonthlyInCount());
        psmt.setLong(++m, p_countsVO.getMonthlyInValue());
        psmt.setLong(++m, p_countsVO.getDailyOutCount());
        psmt.setLong(++m, p_countsVO.getDailyOutValue());
        psmt.setLong(++m, p_countsVO.getWeeklyOutCount());
        psmt.setLong(++m, p_countsVO.getWeeklyOutValue());
        psmt.setLong(++m, p_countsVO.getMonthlyOutCount());
        psmt.setLong(++m, p_countsVO.getMonthlyOutValue());
        psmt.setLong(++m, p_countsVO.getUnctrlDailyInCount());
        psmt.setLong(++m, p_countsVO.getUnctrlDailyInValue());
        psmt.setLong(++m, p_countsVO.getUnctrlWeeklyInCount());
        psmt.setLong(++m, p_countsVO.getUnctrlWeeklyInValue());
        psmt.setLong(++m, p_countsVO.getUnctrlMonthlyInCount());
        psmt.setLong(++m, p_countsVO.getUnctrlMonthlyInValue());
        psmt.setLong(++m, p_countsVO.getUnctrlDailyOutCount());
        psmt.setLong(++m, p_countsVO.getUnctrlDailyOutValue());
        psmt.setLong(++m, p_countsVO.getUnctrlWeeklyOutCount());
        psmt.setLong(++m, p_countsVO.getUnctrlWeeklyOutValue());
        psmt.setLong(++m, p_countsVO.getUnctrlMonthlyOutCount());
        psmt.setLong(++m, p_countsVO.getUnctrlMonthlyOutValue());
        psmt.setLong(++m, p_countsVO.getDailyC2STransferOutCount());
        psmt.setLong(++m, p_countsVO.getWeeklyC2STransferOutCount());
        psmt.setLong(++m, p_countsVO.getMonthlyC2STransferOutCount());
        psmt.setLong(++m, p_countsVO.getDailyC2STransferOutValue());
        psmt.setLong(++m, p_countsVO.getWeeklyC2STransferOutValue());
        psmt.setLong(++m, p_countsVO.getMonthlyC2STransferOutValue());
        psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_countsVO.getOutsideLastInTime()));
        psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_countsVO.getLastInTime()));
        psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_countsVO.getLastOutTime()));
        psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_countsVO.getOutsideLastOutTime()));

        psmt.setString(++m, p_countsVO.getLastTransferID());
        psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_countsVO.getLastTransferDate()));
        psmt.setString(++m, p_countsVO.getUserID());
        if (_log.isDebugEnabled()) {
            _log.debug("updateUserTransferCounts", "Exiting :");
        }
    }

    /**
     * Method to debit the user balances for a user and a product. Also checks
     * the transfer counts before final debit
     * 
     * @param p_con
     * @param p_userBalancesVO
     * @param p_transferProfileID
     * @param p_productID
     * @param p_isCheckMinBalance
     *            boolean
     * @param p_categoryCode
     * @return int
     * @throws BTSLBaseException
     */
    public int debitUserBalances(UserBalancesVO p_userBalancesVO, String p_transferProfileID, String p_productID, boolean p_isCheckMinBalance, PreparedStatement p_pstmtUserProdBalanceSelect, PreparedStatement p_pstmtTransferProfileProductSelect, PreparedStatement p_pstmtUserBalanceUpdate) throws BTSLBaseException {
        final String METHOD_NAME = "debitUserBalances";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered p_userBalancesVO : " + p_userBalancesVO + ",p_isCheckMinBalance=" + p_isCheckMinBalance);
        }

        int updateCount = 0;
        long balance = 0;
        long newBalance = 0;
        ResultSet rs = null;
        TransferProfileProductVO transferProfileProductVO = null;
        String[] strArr = null;
        try {
            if (p_pstmtUserProdBalanceSelect != null) {
                p_pstmtUserProdBalanceSelect.clearParameters();
                p_pstmtUserProdBalanceSelect.setString(1, p_userBalancesVO.getUserID());
                p_pstmtUserProdBalanceSelect.setString(2, p_userBalancesVO.getProductCode());
                p_pstmtUserProdBalanceSelect.setString(3, p_userBalancesVO.getNetworkCode());
                p_pstmtUserProdBalanceSelect.setString(4, p_userBalancesVO.getNetworkFor());
                rs = p_pstmtUserProdBalanceSelect.executeQuery();
            }
            if (rs != null && rs.next()) {
                balance = rs.getLong("balance");
            }
            transferProfileProductVO = loadTransferProfileProducts(p_transferProfileID, p_productID, p_pstmtTransferProfileProductSelect);
            if (p_isCheckMinBalance && balance - p_userBalancesVO.getQuantityToBeUpdated() < transferProfileProductVO.getMinResidualBalanceAsLong()) {
                strArr = new String[] { p_userBalancesVO.getProductShortName(), String.valueOf(PretupsBL.getDisplayAmount(p_userBalancesVO.getQuantityToBeUpdated())), String.valueOf(PretupsBL.getDisplayAmount(balance)), String.valueOf(PretupsBL.getDisplayAmount(transferProfileProductVO.getMinResidualBalanceAsLong())) };
                throw new BTSLBaseException("HandleUnsettledCombinedCasesQueries", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_BAL_LESS_RESIDUAL, 0, strArr, null);
            }

            if (balance - p_userBalancesVO.getQuantityToBeUpdated() < 0) {
                strArr = new String[] { p_userBalancesVO.getProductShortName(), String.valueOf(PretupsBL.getDisplayAmount(p_userBalancesVO.getQuantityToBeUpdated())), String.valueOf(PretupsBL.getDisplayAmount(balance)) };
                throw new BTSLBaseException("HandleUnsettledCombinedCasesQueries", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_INSUFF_BALANCE, 0, strArr, null);
            }
            newBalance = balance - p_userBalancesVO.getQuantityToBeUpdated();

            p_pstmtUserBalanceUpdate.clearParameters();
            p_userBalancesVO.setPreviousBalance(balance);
            p_userBalancesVO.setBalance(newBalance);
            p_pstmtUserBalanceUpdate.setLong(1, newBalance);
            p_pstmtUserBalanceUpdate.setString(2, p_userBalancesVO.getLastTransferType());
            p_pstmtUserBalanceUpdate.setString(3, p_userBalancesVO.getLastTransferID());
            p_pstmtUserBalanceUpdate.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
            p_pstmtUserBalanceUpdate.setString(5, p_userBalancesVO.getUserID());
            p_pstmtUserBalanceUpdate.setString(6, p_userBalancesVO.getProductCode());
            p_pstmtUserBalanceUpdate.setString(7, p_userBalancesVO.getNetworkCode());
            p_pstmtUserBalanceUpdate.setString(8, p_userBalancesVO.getNetworkFor());
            updateCount = p_pstmtUserBalanceUpdate.executeUpdate();
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            // Removing entry for Zero balance and thresh hold counter because
            // this is a batch process and we cannot maintain such counters
            // for zero balance counter..
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting for Transfer ID:" + p_userBalancesVO.getLastTransferID() + " User ID =" + p_userBalancesVO.getUserID() + " New Balance=" + newBalance);
            }
        }// end of finally
        return updateCount;
    }

    /**
     * Method to increase the Channel to subscriber transfer out counts and
     * values
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_isCheckThresholds
     *            boolean
     * @throws BTSLBaseException
     */
    public void increaseC2STransferOutCounts(C2STransferVO p_c2sTransferVO, boolean p_isCheckThresholds, PreparedStatement p_pstmtUserTransferCountsUpdate, PreparedStatement p_pstmtUserTransferCountsInsert, PreparedStatement p_pstmtTransferCountsSelect, PreparedStatement p_pstmtTransferCountsWithLockSelect, PreparedStatement p_pstmtTransferProfileSelect, PreparedStatement p_pstmtdEffTrfProfileProductListSelect) throws BTSLBaseException {
        final String METHOD_NAME = "increaseC2STransferOutCounts";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered Transfer ID =" + p_c2sTransferVO.getTransferID() + "User   ID " + p_c2sTransferVO.getSenderID() + ",p_isCheckThresholds=" + p_isCheckThresholds);
        }

        boolean isLockRecordForUpdate = true;
        try {
            UserTransferCountsVO userTransferCountsVO = checkC2STransferOutCounts(p_c2sTransferVO, isLockRecordForUpdate, p_isCheckThresholds, p_pstmtTransferCountsSelect, p_pstmtTransferCountsWithLockSelect, p_pstmtTransferProfileSelect, p_pstmtdEffTrfProfileProductListSelect);
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_C2S_SEPARATE_TRNSFR_COUNTS))).booleanValue()) {
                userTransferCountsVO.setUserID(p_c2sTransferVO.getSenderID());
                userTransferCountsVO.setDailyC2STransferOutCount(userTransferCountsVO.getDailyC2STransferOutCount() + 1);
                userTransferCountsVO.setDailyC2STransferOutValue(userTransferCountsVO.getDailyC2STransferOutValue() + p_c2sTransferVO.getRequestedAmount());
                userTransferCountsVO.setWeeklyC2STransferOutCount(userTransferCountsVO.getWeeklyC2STransferOutCount() + 1);
                userTransferCountsVO.setWeeklyC2STransferOutValue(userTransferCountsVO.getWeeklyC2STransferOutValue() + p_c2sTransferVO.getRequestedAmount());
                userTransferCountsVO.setMonthlyC2STransferOutCount(userTransferCountsVO.getMonthlyC2STransferOutCount() + 1);
                userTransferCountsVO.setMonthlyC2STransferOutValue(userTransferCountsVO.getMonthlyC2STransferOutValue() + p_c2sTransferVO.getRequestedAmount());
                userTransferCountsVO.setLastTransferDate(p_c2sTransferVO.getCreatedOn());
                userTransferCountsVO.setLastOutTime(p_c2sTransferVO.getCreatedOn());
            } else {
                userTransferCountsVO.setUserID(p_c2sTransferVO.getSenderID());
                userTransferCountsVO.setDailyOutCount(userTransferCountsVO.getDailyOutCount() + 1);
                userTransferCountsVO.setDailyOutValue(userTransferCountsVO.getDailyOutValue() + p_c2sTransferVO.getRequestedAmount());
                userTransferCountsVO.setWeeklyOutCount(userTransferCountsVO.getWeeklyOutCount() + 1);
                userTransferCountsVO.setWeeklyOutValue(userTransferCountsVO.getWeeklyOutValue() + p_c2sTransferVO.getRequestedAmount());
                userTransferCountsVO.setMonthlyOutCount(userTransferCountsVO.getMonthlyOutCount() + 1);
                userTransferCountsVO.setMonthlyOutValue(userTransferCountsVO.getMonthlyOutValue() + p_c2sTransferVO.getRequestedAmount());
                userTransferCountsVO.setLastTransferDate(p_c2sTransferVO.getCreatedOn());
                userTransferCountsVO.setLastOutTime(p_c2sTransferVO.getCreatedOn());
            }
            int updateCount = updateUserTransferCounts(userTransferCountsVO, userTransferCountsVO.isUpdateRecord(), p_pstmtUserTransferCountsUpdate, p_pstmtUserTransferCountsInsert);

            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exited  updateCount " + updateCount);
            }
            if (updateCount <= 0) {
                throw new BTSLBaseException("ChannelTransferBL", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
            }

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, "Exception p_transferID:" + p_c2sTransferVO.getTransferID() + " Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[increaseC2STransferOutCounts]", p_c2sTransferVO.getTransferID(), p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException("ChannelTransferBL", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exited ");
        }
    }

    /**
     * Method to check the Channel to subscriber transfer Out counts. A
     * preference is there that will decide that whether separate C2S transfer
     * out will be used or C2C transfer out count to be used
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_isLockRecordForUpdate
     * @param p_isCheckThresholds
     *            boolean
     * @return UserTransferCountsVO
     * @throws BTSLBaseException
     */
    public UserTransferCountsVO checkC2STransferOutCounts(C2STransferVO p_c2sTransferVO, boolean p_isLockRecordForUpdate, boolean p_isCheckThresholds, PreparedStatement p_pstmtTransferCountsSelect, PreparedStatement p_pstmtTransferCountsWithLockSelect, PreparedStatement p_pstmtTransferProfileSelect, PreparedStatement p_pstmtdEffTrfProfileProductListSelect) throws BTSLBaseException {
        final String METHOD_NAME = "checkC2STransferOutCounts";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered Transfer ID =" + p_c2sTransferVO.getTransferID() + "User  ID " + p_c2sTransferVO.getSenderID() + "p_isLockRecordForUpdate=" + p_isLockRecordForUpdate + ",p_isCheckThresholds=" + p_isCheckThresholds);
        }

        UserTransferCountsVO userTransferCountsVO = loadTransferCounts(p_c2sTransferVO.getSenderID(), p_isLockRecordForUpdate, p_pstmtTransferCountsSelect, p_pstmtTransferCountsWithLockSelect);
        if (!p_isCheckThresholds) {
            return userTransferCountsVO;
        }
        TransferProfileVO transferProfileVO = loadTransferProfile(((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getTransferProfileID(), p_c2sTransferVO.getNetworkCode(), false, p_pstmtTransferProfileSelect, p_pstmtdEffTrfProfileProductListSelect);
        String[] strArr = null;
        try {
            // Done so as if someone has defined in Transfer Profile as Allowed
            // Transfer as 0
            if (userTransferCountsVO == null) {
                userTransferCountsVO = new UserTransferCountsVO();
                userTransferCountsVO.setUpdateRecord(false);
            }

            userTransferCountsVO.setLastTransferID(p_c2sTransferVO.getTransferID());

            // To check whether Counters needs to be reinitialized or not
            boolean isCounterReInitalizingReqd = checkResetCountersAfterPeriodChange(userTransferCountsVO, p_c2sTransferVO.getCreatedOn());
            p_c2sTransferVO.setTransferProfileCtInitializeReqd(isCounterReInitalizingReqd);

            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_C2S_SEPARATE_TRNSFR_COUNTS))).booleanValue()) {
                if (transferProfileVO.getDailyC2STransferOutCount() <= userTransferCountsVO.getDailyC2STransferOutCount()) {
                    strArr = new String[] { String.valueOf(transferProfileVO.getDailyC2STransferOutCount()) };
                    throw new BTSLBaseException("ChannelTransferBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_DAILY_OUT_CTREACHED, 0, strArr, null);
                } else if (transferProfileVO.getDailyC2STransferOutValue() < userTransferCountsVO.getDailyC2STransferOutValue() + p_c2sTransferVO.getRequestedAmount()) {
                    strArr = new String[] { PretupsBL.getDisplayAmount(p_c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO.getDailyC2STransferOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getDailyC2STransferOutValue()) };
                    throw new BTSLBaseException("ChannelTransferBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_DAILY_OUT_VALREACHED, 0, strArr, null);
                } else if (transferProfileVO.getWeeklyC2STransferOutCount() <= userTransferCountsVO.getWeeklyC2STransferOutCount()) {
                    strArr = new String[] { String.valueOf(transferProfileVO.getWeeklyC2STransferOutCount()) };
                    throw new BTSLBaseException("ChannelTransferBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_WEEKLY_OUT_CTREACHED, 0, strArr, null);
                } else if (transferProfileVO.getWeeklyC2STransferOutValue() < userTransferCountsVO.getWeeklyC2STransferOutValue() + p_c2sTransferVO.getRequestedAmount()) {
                    strArr = new String[] { PretupsBL.getDisplayAmount(p_c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO.getWeeklyC2STransferOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getWeeklyC2STransferOutValue()) };
                    throw new BTSLBaseException("ChannelTransferBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_WEEKLY_OUT_VALREACHED, 0, strArr, null);
                } else if (transferProfileVO.getMonthlyC2STransferOutCount() <= userTransferCountsVO.getMonthlyC2STransferOutCount()) {
                    strArr = new String[] { String.valueOf(transferProfileVO.getMonthlyC2STransferOutCount()) };
                    throw new BTSLBaseException("ChannelTransferBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_MONTHLY_OUT_CTREACHED, 0, strArr, null);
                } else if (transferProfileVO.getMonthlyC2STransferOutValue() < userTransferCountsVO.getMonthlyC2STransferOutValue() + p_c2sTransferVO.getRequestedAmount()) {
                    strArr = new String[] { PretupsBL.getDisplayAmount(p_c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO.getMonthlyC2STransferOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getMonthlyC2STransferOutValue()) };
                    throw new BTSLBaseException("ChannelTransferBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_MONTHLY_OUT_VALREACHED, 0, strArr, null);
                }
            } else {
                if (transferProfileVO.getDailyOutCount() <= userTransferCountsVO.getDailyOutCount()) {
                    strArr = new String[] { String.valueOf(transferProfileVO.getDailyOutCount()) };
                    throw new BTSLBaseException("ChannelTransferBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_DAILY_OUT_CTREACHED, 0, strArr, null);
                } else if (transferProfileVO.getDailyOutValue() < userTransferCountsVO.getDailyOutValue() + p_c2sTransferVO.getRequestedAmount()) {
                    strArr = new String[] { PretupsBL.getDisplayAmount(p_c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO.getDailyOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getDailyOutValue()) };
                    throw new BTSLBaseException("ChannelTransferBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_DAILY_OUT_VALREACHED, 0, strArr, null);
                } else if (transferProfileVO.getWeeklyOutCount() <= userTransferCountsVO.getWeeklyOutCount()) {
                    strArr = new String[] { String.valueOf(transferProfileVO.getWeeklyOutCount()) };
                    throw new BTSLBaseException("ChannelTransferBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_WEEKLY_OUT_CTREACHED, 0, strArr, null);
                } else if (transferProfileVO.getWeeklyOutValue() < userTransferCountsVO.getWeeklyOutValue() + p_c2sTransferVO.getRequestedAmount()) {
                    strArr = new String[] { PretupsBL.getDisplayAmount(p_c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO.getWeeklyOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getWeeklyOutValue()) };
                    throw new BTSLBaseException("ChannelTransferBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_WEEKLY_OUT_VALREACHED, 0, strArr, null);
                } else if (transferProfileVO.getMonthlyOutCount() <= userTransferCountsVO.getMonthlyOutCount()) {
                    strArr = new String[] { String.valueOf(transferProfileVO.getMonthlyOutCount()) };
                    throw new BTSLBaseException("ChannelTransferBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_MONTHLY_OUT_CTREACHED, 0, strArr, null);
                } else if (transferProfileVO.getMonthlyOutValue() < userTransferCountsVO.getMonthlyOutValue() + p_c2sTransferVO.getRequestedAmount()) {
                    strArr = new String[] { PretupsBL.getDisplayAmount(p_c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO.getMonthlyOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getMonthlyOutValue()) };
                    throw new BTSLBaseException("ChannelTransferBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_MONTHLY_OUT_VALREACHED, 0, strArr, null);
                }
            }
        } catch (BTSLBaseException be) {
            _log.error(METHOD_NAME, "BTSL Exception p_transferID:" + p_c2sTransferVO.getTransferID() + " " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, "Exception p_transferID:" + p_c2sTransferVO.getTransferID() + " Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[checkC2STransferOutCounts]", p_c2sTransferVO.getTransferID(), p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException("ChannelTransferBL", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exited with userTransferCountsVO=" + userTransferCountsVO);
        }
        return userTransferCountsVO;
    }

    /**
     * Load the user transfer profile
     * 
     * @param p_con
     * @param p_transfeProfileID
     * @param p_networkCode
     *            TODO
     * @param p_isProductLoadRequired
     * @return TransferProfileVO
     * @throws BTSLBaseException
     */
    public TransferProfileVO loadTransferProfile(String p_transfeProfileID, String p_networkCode, boolean p_isProductLoadRequired, PreparedStatement p_pstmtTransferProfileSelect, PreparedStatement p_pstmtdEffTrfProfileProductListSelect) throws BTSLBaseException {
        final String METHOD_NAME = "loadTransferProfile";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered   p_transfeProfileID " + p_transfeProfileID);
        }
        ResultSet rs = null;
        TransferProfileVO countsVO = null;
        try {
            p_pstmtTransferProfileSelect.clearParameters();
            p_pstmtTransferProfileSelect.setString(1, p_transfeProfileID);
            p_pstmtTransferProfileSelect.setString(2, PretupsI.YES);
            p_pstmtTransferProfileSelect.setString(3, p_networkCode);
            p_pstmtTransferProfileSelect.setString(4, PretupsI.PARENT_PROFILE_ID_CATEGORY);
            p_pstmtTransferProfileSelect.setString(5, PretupsI.YES);
            rs = p_pstmtTransferProfileSelect.executeQuery();
            if (rs.next()) {
                countsVO = UserTransferCountsVO.getInstance();
                countsVO.setProfileId(rs.getString("profile_id"));
                countsVO.setShortName(rs.getString("short_name"));
                countsVO.setProfileName(rs.getString("profile_name"));
                countsVO.setStatus(rs.getString("status"));
                countsVO.setDescription(rs.getString("description"));
                countsVO.setDailyInCount(rs.getLong("daily_transfer_in_count"));
                countsVO.setDailyInValue(rs.getLong("daily_transfer_in_value"));
                countsVO.setWeeklyInCount(rs.getLong("weekly_transfer_in_count"));
                countsVO.setWeeklyInValue(rs.getLong("weekly_transfer_in_value"));
                countsVO.setMonthlyInCount(rs.getLong("monthly_transfer_in_count"));
                countsVO.setMonthlyInValue(rs.getLong("monthly_transfer_in_value"));
                countsVO.setDailyOutCount(rs.getLong("daily_transfer_out_count"));
                countsVO.setDailyOutValue(rs.getLong("daily_transfer_out_value"));
                countsVO.setWeeklyOutCount(rs.getLong("weekly_transfer_out_count"));
                countsVO.setWeeklyOutValue(rs.getLong("weekly_transfer_out_value"));
                countsVO.setMonthlyOutCount(rs.getLong("monthly_transfer_out_count"));
                countsVO.setMonthlyOutValue(rs.getLong("monthly_transfer_out_value"));
                countsVO.setUnctrlDailyInCount(rs.getLong("outside_daily_in_count"));
                countsVO.setUnctrlDailyInValue(rs.getLong("outside_daily_in_value"));
                countsVO.setUnctrlWeeklyInCount(rs.getLong("outside_weekly_in_count"));
                countsVO.setUnctrlWeeklyInValue(rs.getLong("outside_weekly_in_value"));
                countsVO.setUnctrlMonthlyInCount(rs.getLong("outside_monthly_in_count"));
                countsVO.setUnctrlMonthlyInValue(rs.getLong("outside_monthly_in_value"));
                countsVO.setUnctrlDailyOutCount(rs.getLong("outside_daily_out_count"));
                countsVO.setUnctrlDailyOutValue(rs.getLong("outside_daily_out_value"));
                countsVO.setUnctrlWeeklyOutCount(rs.getLong("outside_weekly_out_count"));
                countsVO.setUnctrlWeeklyOutValue(rs.getLong("outside_weekly_out_value"));
                countsVO.setUnctrlMonthlyOutCount(rs.getLong("outside_monthly_out_count"));
                countsVO.setUnctrlMonthlyOutValue(rs.getLong("outside_monthly_out_value"));
                countsVO.setCreatedBy(rs.getString("created_by"));
                countsVO.setDailyC2STransferOutCount(rs.getLong("daily_subscriber_out_count"));
                countsVO.setDailyC2STransferOutValue(rs.getLong("daily_subscriber_out_value"));
                countsVO.setWeeklyC2STransferOutCount(rs.getLong("weekly_subscriber_out_count"));
                countsVO.setWeeklyC2STransferOutValue(rs.getLong("weekly_subscriber_out_value"));
                countsVO.setMonthlyC2STransferOutCount(rs.getLong("monthly_subscriber_out_count"));
                countsVO.setMonthlyC2STransferOutValue(rs.getLong("monthly_subscriber_out_value"));
                countsVO.setCreatedOn(rs.getDate("created_on"));
                countsVO.setModifiedBy(rs.getString("modified_by"));
                countsVO.setModifiedOn(rs.getDate("modified_on"));
                countsVO.setNetworkCode(rs.getString("network_code"));
                countsVO.setCategory(rs.getString("category_code"));
                countsVO.setDailyInAltCount(rs.getLong("alt_daily_transfer_in_count"));
                countsVO.setDailyInAltValue(rs.getLong("alt_daily_transfer_in_value"));
                countsVO.setWeeklyInAltCount(rs.getLong("alt_weekly_transfer_in_count"));
                countsVO.setWeeklyInAltValue(rs.getLong("alt_weekly_transfer_in_value"));
                countsVO.setMonthlyInAltCount(rs.getLong("alt_monthly_transfer_in_count"));
                countsVO.setMonthlyInAltValue(rs.getLong("alt_monthly_transfer_in_value"));
                countsVO.setDailyOutAltCount(rs.getLong("alt_daily_transfer_out_count"));
                countsVO.setDailyOutAltValue(rs.getLong("alt_daily_transfer_out_value"));
                countsVO.setWeeklyOutAltCount(rs.getLong("alt_weekly_transfer_out_count"));
                countsVO.setWeeklyOutAltValue(rs.getLong("alt_weekly_transfer_out_value"));
                countsVO.setMonthlyOutAltCount(rs.getLong("alt_monthly_transfer_out_count"));
                countsVO.setMonthlyOutAltValue(rs.getLong("alt_monthly_transfer_out_value"));
                countsVO.setUnctrlDailyInAltCount(rs.getLong("alt_outside_daily_in_count"));
                countsVO.setUnctrlDailyInAltValue(rs.getLong("alt_outside_daily_in_value"));
                countsVO.setUnctrlWeeklyInAltCount(rs.getLong("alt_outside_Weekly_in_count"));
                countsVO.setUnctrlWeeklyInAltValue(rs.getLong("alt_outside_Weekly_in_value"));
                countsVO.setUnctrlMonthlyInAltCount(rs.getLong("alt_outside_Monthly_in_count"));
                countsVO.setUnctrlMonthlyInAltValue(rs.getLong("alt_outside_Monthly_in_value"));
                countsVO.setUnctrlDailyOutAltCount(rs.getLong("alt_outside_daily_out_count"));
                countsVO.setUnctrlDailyOutAltValue(rs.getLong("alt_outside_daily_out_value"));
                countsVO.setUnctrlWeeklyOutAltCount(rs.getLong("alt_outside_Weekly_out_count"));
                countsVO.setUnctrlWeeklyOutAltValue(rs.getLong("alt_outside_Weekly_out_value"));
                countsVO.setUnctrlMonthlyOutAltCount(rs.getLong("alt_outside_Monthly_out_count"));
                countsVO.setUnctrlMonthlyOutAltValue(rs.getLong("alt_outside_Monthly_out_value"));
                countsVO.setDailySubscriberOutAltCount(rs.getLong("alt_daily_subs_out_count"));
                countsVO.setDailySubscriberOutAltValue(rs.getLong("alt_daily_subs_out_value"));
                countsVO.setWeeklySubscriberOutAltCount(rs.getLong("alt_weekly_subs_out_count"));
                countsVO.setWeeklySubscriberOutAltValue(rs.getLong("alt_weekly_subs_out_value"));
                countsVO.setMonthlySubscriberOutAltCount(rs.getLong("alt_monthly_subs_out_count"));
                countsVO.setMonthlySubscriberOutAltValue(rs.getLong("alt_monthly_subs_out_value"));
                countsVO.setParentProfileID(rs.getString("parent_profile_id"));
            }
            if (p_isProductLoadRequired) {
                countsVO.setProfileProductList(this.loadEffTrfProfileProductList(countsVO.getProfileId(), p_pstmtdEffTrfProfileProductListSelect));
            }
        } catch (SQLException sqe) {
            _log.error(METHOD_NAME, "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(METHOD_NAME, "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting:  UserTransferCountsVO =" + countsVO);
            }
        }
        return countsVO;
    }

    public String loadTransferProfileQuery() {
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT tp.profile_id,tp.short_name,tp.profile_name,tp.status,tp.description,LEAST(tp.daily_transfer_in_count,catp.daily_transfer_in_count) daily_transfer_in_count, ");
        strBuff.append(" LEAST(tp.daily_transfer_in_value,catp.daily_transfer_in_value) daily_transfer_in_value ,LEAST(tp.weekly_transfer_in_count,catp.weekly_transfer_in_count) weekly_transfer_in_count, ");
        strBuff.append(" LEAST(tp.weekly_transfer_in_value,catp.weekly_transfer_in_value) weekly_transfer_in_value,LEAST(tp.monthly_transfer_in_count,catp.monthly_transfer_in_count) monthly_transfer_in_count, ");
        strBuff.append(" LEAST(tp.monthly_transfer_in_value,catp.monthly_transfer_in_value) monthly_transfer_in_value,LEAST(tp.daily_transfer_out_count,catp.daily_transfer_out_count) daily_transfer_out_count, ");
        strBuff.append(" LEAST(tp.daily_transfer_out_value,catp.daily_transfer_out_value) daily_transfer_out_value,LEAST(tp.weekly_transfer_out_count,catp.weekly_transfer_out_count) weekly_transfer_out_count, ");
        strBuff.append(" LEAST(tp.weekly_transfer_out_value,catp.weekly_transfer_out_value) weekly_transfer_out_value,LEAST(tp.monthly_transfer_out_count,catp.monthly_transfer_out_count) monthly_transfer_out_count, ");
        strBuff.append(" LEAST(tp.monthly_transfer_out_value,catp.monthly_transfer_out_value) monthly_transfer_out_value,LEAST(tp.outside_daily_in_count,catp.outside_daily_in_count) outside_daily_in_count, ");
        strBuff.append(" LEAST(tp.outside_daily_in_value,catp.outside_daily_in_value) outside_daily_in_value,LEAST(tp.outside_weekly_in_count,catp.outside_weekly_in_count) outside_weekly_in_count, ");
        strBuff.append(" LEAST(tp.outside_weekly_in_value,catp.outside_weekly_in_value) outside_weekly_in_value,LEAST(tp.outside_monthly_in_count,catp.outside_monthly_in_count) outside_monthly_in_count, ");
        strBuff.append(" LEAST(tp.outside_monthly_in_value,catp.outside_monthly_in_value) outside_monthly_in_value,LEAST(tp.outside_daily_out_count,catp.outside_daily_out_count) outside_daily_out_count, ");
        strBuff.append(" LEAST(tp.outside_daily_out_value,catp.outside_daily_out_value) outside_daily_out_value,LEAST(tp.outside_weekly_out_count,catp.outside_weekly_out_count) outside_weekly_out_count, ");
        strBuff.append(" LEAST(tp.outside_weekly_out_value,catp.outside_weekly_out_value) outside_weekly_out_value,LEAST(tp.outside_monthly_out_count,catp.outside_monthly_out_count) outside_monthly_out_count, ");
        strBuff.append(" LEAST(tp.outside_monthly_out_value,catp.outside_monthly_out_value) outside_monthly_out_value, ");
        strBuff.append(" tp.created_by,tp.created_on, ");
        strBuff.append(" LEAST(tp.daily_subscriber_out_count,catp.daily_subscriber_out_count) daily_subscriber_out_count,LEAST(tp.weekly_subscriber_out_count,catp.weekly_subscriber_out_count) weekly_subscriber_out_count, ");
        strBuff.append(" LEAST(tp.monthly_subscriber_out_count,catp.monthly_subscriber_out_count) monthly_subscriber_out_count, ");
        strBuff.append(" LEAST(tp.daily_subscriber_out_value,catp.daily_subscriber_out_value) daily_subscriber_out_value,LEAST(tp.weekly_subscriber_out_value,catp.weekly_subscriber_out_value) weekly_subscriber_out_value, ");
        strBuff.append(" LEAST(tp.monthly_subscriber_out_value,catp.monthly_subscriber_out_value) monthly_subscriber_out_value, ");
        strBuff.append(" tp.modified_by,tp.modified_on,tp.network_code,tp.category_code,LEAST(tp.alt_daily_transfer_in_count,catp.alt_daily_transfer_in_count) alt_daily_transfer_in_count, ");
        strBuff.append(" LEAST(tp.alt_daily_transfer_in_value,catp.alt_daily_transfer_in_value) alt_daily_transfer_in_value,LEAST(tp.alt_weekly_transfer_in_count,catp.alt_weekly_transfer_in_count) alt_weekly_transfer_in_count, ");
        strBuff.append(" LEAST(tp.alt_weekly_transfer_in_value,catp.alt_weekly_transfer_in_value) alt_weekly_transfer_in_value,LEAST(tp.alt_monthly_transfer_in_count,catp.alt_monthly_transfer_in_count) alt_monthly_transfer_in_count, ");
        strBuff.append(" LEAST(tp.alt_monthly_transfer_in_value,catp.alt_monthly_transfer_in_value) alt_monthly_transfer_in_value,LEAST(tp.alt_daily_transfer_out_count,catp.alt_daily_transfer_out_count) alt_daily_transfer_out_count, ");
        strBuff.append(" LEAST(tp.alt_daily_transfer_out_value,catp.alt_daily_transfer_out_value) alt_daily_transfer_out_value,LEAST(tp.alt_weekly_transfer_out_count,catp.alt_weekly_transfer_out_count) alt_weekly_transfer_out_count, ");
        strBuff.append(" LEAST(tp.alt_weekly_transfer_out_value,catp.alt_weekly_transfer_out_value) alt_weekly_transfer_out_value,LEAST(tp.alt_monthly_transfer_out_count,catp.alt_monthly_transfer_out_count) alt_monthly_transfer_out_count, ");
        strBuff.append(" LEAST(tp.alt_monthly_transfer_out_value,catp.alt_monthly_transfer_out_value) alt_monthly_transfer_out_value,LEAST(tp.alt_outside_daily_in_count,catp.alt_outside_daily_in_count) alt_outside_daily_in_count, ");
        strBuff.append(" LEAST(tp.alt_outside_daily_in_value,catp.alt_outside_daily_in_value) alt_outside_daily_in_value,LEAST(tp.alt_outside_weekly_in_count,catp.alt_outside_weekly_in_count) alt_outside_weekly_in_count, ");
        strBuff.append(" LEAST(tp.alt_outside_weekly_in_value,catp.alt_outside_weekly_in_value) alt_outside_weekly_in_value,LEAST(tp.alt_outside_monthly_in_count,catp.alt_outside_monthly_in_count) alt_outside_monthly_in_count,  ");
        strBuff.append(" LEAST(tp.alt_outside_monthly_in_value,catp.alt_outside_monthly_in_value) alt_outside_monthly_in_value,LEAST(tp.alt_outside_daily_out_count,catp.alt_outside_daily_out_count) alt_outside_daily_out_count, ");
        strBuff.append(" LEAST(tp.alt_outside_daily_out_value,catp.alt_outside_daily_out_value) alt_outside_daily_out_value,LEAST(tp.alt_outside_weekly_out_count,catp.alt_outside_weekly_out_count) alt_outside_weekly_out_count, ");
        strBuff.append(" LEAST(tp.alt_outside_weekly_out_value,catp.alt_outside_weekly_out_value) alt_outside_weekly_out_value,LEAST(tp.alt_outside_monthly_out_count,catp.alt_outside_monthly_out_count) alt_outside_monthly_out_count, ");
        strBuff.append(" LEAST(tp.alt_outside_monthly_out_value,catp.alt_outside_monthly_out_value) alt_outside_monthly_out_value,LEAST(tp.alt_daily_subs_out_count,catp.alt_daily_subs_out_count) alt_daily_subs_out_count, ");
        strBuff.append(" LEAST(tp.alt_daily_subs_out_value,catp.alt_daily_subs_out_value) alt_daily_subs_out_value,LEAST(tp.alt_weekly_subs_out_count,catp.alt_weekly_subs_out_count) alt_weekly_subs_out_count, ");
        strBuff.append(" LEAST(tp.alt_weekly_subs_out_value,catp.alt_weekly_subs_out_value) alt_weekly_subs_out_value,LEAST(tp.alt_monthly_subs_out_count,catp.alt_monthly_subs_out_count) alt_monthly_subs_out_count, ");
        strBuff.append(" LEAST(tp.alt_monthly_subs_out_value,catp.alt_monthly_subs_out_value) alt_monthly_subs_out_value,tp.parent_profile_id ");
        strBuff.append(" FROM transfer_profile tp,transfer_profile catp ");
        strBuff.append(" WHERE tp.profile_id = ? AND tp.status = ?  AND tp.network_code = ? ");
        strBuff.append(" AND tp.category_code=catp.category_code ");
        strBuff.append(" AND catp.parent_profile_id=? AND catp.status=? AND tp.network_code = catp.network_code ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadTransferProfileQuery", "select query:" + sqlSelect);
        }
        return sqlSelect;
    }

    /**
     * Method to check whether Counters needs to be reinitialized or not
     * 
     * @param p_userTransferCountsVO
     * @param p_newDate
     * @return boolean
     */
    public static boolean checkResetCountersAfterPeriodChange(UserTransferCountsVO p_userTransferCountsVO, java.util.Date p_newDate) {
        if (_log.isDebugEnabled()) {
            _log.debug("isResetCountersAfterPeriodChange", "Entered with transferID=" + p_userTransferCountsVO.getLastTransferID() + " USER ID=" + p_userTransferCountsVO.getUserID());
        }
        boolean isCounterChange = false;
        boolean isDayCounterChange = false;
        boolean isWeekCounterChange = false;
        boolean isMonthCounterChange = false;

        Date previousDate = p_userTransferCountsVO.getLastTransferDate();

        if (previousDate != null) {
            Calendar cal = BTSLDateUtil.getInstance();
            cal.setTime(p_newDate);
            int presentDay = cal.get(Calendar.DAY_OF_MONTH);
            int presentWeek = cal.get(Calendar.WEEK_OF_MONTH);
            int presentMonth = cal.get(Calendar.MONTH);
            int presentYear = cal.get(Calendar.YEAR);
            cal.setTime(previousDate);
            int lastWeek = cal.get(Calendar.WEEK_OF_MONTH);
            int lastTrxDay = cal.get(Calendar.DAY_OF_MONTH);
            int lastTrxMonth = cal.get(Calendar.MONTH);
            int lastTrxYear = cal.get(Calendar.YEAR);
            if (presentDay != lastTrxDay) {
                isDayCounterChange = true;
            }
            if (presentWeek != lastWeek) {
                isWeekCounterChange = true;
            }
            if (presentMonth != lastTrxMonth) {
                isDayCounterChange = true;
                isWeekCounterChange = true;
                isMonthCounterChange = true;
            }
            if (presentYear != lastTrxYear) {
                isDayCounterChange = true;
                isWeekCounterChange = true;
                isMonthCounterChange = true;
            }

            if (isDayCounterChange) {
                p_userTransferCountsVO.setDailyInCount(0);
                p_userTransferCountsVO.setDailyInValue(0);
                p_userTransferCountsVO.setDailyOutCount(0);
                p_userTransferCountsVO.setDailyOutValue(0);
                p_userTransferCountsVO.setUnctrlDailyInCount(0);
                p_userTransferCountsVO.setUnctrlDailyInValue(0);
                p_userTransferCountsVO.setUnctrlDailyOutCount(0);
                p_userTransferCountsVO.setUnctrlDailyOutValue(0);
                p_userTransferCountsVO.setDailySubscriberOutCount(0);
                p_userTransferCountsVO.setDailySubscriberOutValue(0);
                p_userTransferCountsVO.setDailyC2STransferOutCount(0);
                p_userTransferCountsVO.setDailyC2STransferOutValue(0);
                isCounterChange = true;
            }
            if (isWeekCounterChange) {
                p_userTransferCountsVO.setWeeklySubscriberOutValue(0);
                p_userTransferCountsVO.setWeeklyInCount(0);
                p_userTransferCountsVO.setWeeklyInValue(0);
                p_userTransferCountsVO.setWeeklyOutCount(0);
                p_userTransferCountsVO.setWeeklyOutValue(0);
                p_userTransferCountsVO.setUnctrlWeeklyInCount(0);
                p_userTransferCountsVO.setUnctrlWeeklyInValue(0);
                p_userTransferCountsVO.setUnctrlWeeklyOutValue(0);
                p_userTransferCountsVO.setWeeklySubscriberOutCount(0);
                p_userTransferCountsVO.setUnctrlWeeklyOutCount(0);
                p_userTransferCountsVO.setWeeklyC2STransferOutCount(0);
                p_userTransferCountsVO.setWeeklyC2STransferOutValue(0);
                isCounterChange = true;
            }
            if (isMonthCounterChange) {
                p_userTransferCountsVO.setMonthlyInCount(0);
                p_userTransferCountsVO.setMonthlyInValue(0);
                p_userTransferCountsVO.setMonthlyOutCount(0);
                p_userTransferCountsVO.setMonthlyOutValue(0);
                p_userTransferCountsVO.setUnctrlMonthlyInCount(0);
                p_userTransferCountsVO.setUnctrlMonthlyInValue(0);
                p_userTransferCountsVO.setUnctrlMonthlyOutCount(0);
                p_userTransferCountsVO.setUnctrlMonthlyOutValue(0);
                p_userTransferCountsVO.setMonthlySubscriberOutCount(0);
                p_userTransferCountsVO.setMonthlySubscriberOutValue(0);
                p_userTransferCountsVO.setMonthlyC2STransferOutCount(0);
                p_userTransferCountsVO.setMonthlyC2STransferOutValue(0);
                isCounterChange = true;
            }
        } else {
            isCounterChange = true;
        }

        if (_log.isDebugEnabled()) {
            _log.debug("isResetCountersAfterPeriodChange", "Exiting with isCounterChange=" + isCounterChange + " For transferID=" + p_userTransferCountsVO.getLastTransferID() + " USER ID=" + p_userTransferCountsVO.getUserID());
        }
        return isCounterChange;
    }

    /**
     * Method loadEffTrfProfileProduct This method Load the effected tarnsfer
     * Profile Products information i.e. after performing the LEAST or GREATEST
     * functions
     * 
     * @param p_con
     * @param p_profileID
     * @return ArrayList
     * @throws BTSLBaseException
     */
    private ArrayList loadEffTrfProfileProductList(String p_profileID, PreparedStatement p_pstmtdEffTrfProfileProductListSelect) throws BTSLBaseException {
        final String METHOD_NAME = "loadEffTrfProfileProductList";
        if (_log.isDebugEnabled()) {
            _log.debug("loadEffTrfProfileProduct", "Entered: p_profileID=" + p_profileID);
        }

        ResultSet rs = null;
        TransferProfileProductVO transferProfileProductVO = null;
        ArrayList arrayList = new ArrayList();
        try {
            if (p_pstmtdEffTrfProfileProductListSelect != null) {
                p_pstmtdEffTrfProfileProductListSelect.clearParameters();
            }
            p_pstmtdEffTrfProfileProductListSelect.setString(1, p_profileID);
            p_pstmtdEffTrfProfileProductListSelect.setString(2, PretupsI.PARENT_PROFILE_ID_CATEGORY);

            rs = p_pstmtdEffTrfProfileProductListSelect.executeQuery();
            while (rs.next()) {
                transferProfileProductVO = TransferProfileProductVO.getInstance();
                transferProfileProductVO.setProductCode(rs.getString("product_code"));
                transferProfileProductVO.setProductShortCode(rs.getString("product_short_code"));
                transferProfileProductVO.setProductName(rs.getString("product_name"));
                transferProfileProductVO.setMinResidualBalanceAsLong(rs.getLong("min_residual_balance"));
                transferProfileProductVO.setMaxBalanceAsLong(rs.getLong("max_balance"));
                transferProfileProductVO.setMaxBalance(PretupsBL.getDisplayAmount(transferProfileProductVO.getMaxBalanceAsLong()));
                transferProfileProductVO.setMinBalance(PretupsBL.getDisplayAmount(transferProfileProductVO.getMinResidualBalanceAsLong()));

                transferProfileProductVO.setAltBalanceLong(rs.getLong("alerting_balance"));
                transferProfileProductVO.setAltBalance(PretupsBL.getDisplayAmount(transferProfileProductVO.getAltBalanceLong()));
                transferProfileProductVO.setAllowedMaxPercentageInt(rs.getInt("max_pct_transfer_allowed"));
                transferProfileProductVO.setAllowedMaxPercentage(String.valueOf(transferProfileProductVO.getAllowedMaxPercentageInt()));

                transferProfileProductVO.setC2sMinTxnAmtAsLong(rs.getLong("c2s_min_txn_amt"));
                transferProfileProductVO.setC2sMaxTxnAmtAsLong(rs.getLong("c2s_max_txn_amt"));
                transferProfileProductVO.setC2sMinTxnAmt(PretupsBL.getDisplayAmount(transferProfileProductVO.getC2sMinTxnAmtAsLong()));
                transferProfileProductVO.setC2sMaxTxnAmt(PretupsBL.getDisplayAmount(transferProfileProductVO.getC2sMaxTxnAmtAsLong()));
                arrayList.add(transferProfileProductVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadEffTrfProfileProduct", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, "loadEffTrfProfileProduct", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadEffTrfProfileProduct", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, "loadEffTrfProfileProduct", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadEffTrfProfileProduct", "Exiting: arrayList.size() =" + arrayList.size());
            }
        }
        return arrayList;
    }

    public String loadEffTrfProfileProductListQuery() {
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT tpp.product_code,prod.product_short_code,prod.product_name,");
        strBuff.append("GREATEST( tpp.min_residual_balance, ctpp.min_residual_balance) min_residual_balance,");
        strBuff.append("GREATEST(  tpp.c2s_min_txn_amt,ctpp.c2s_min_txn_amt)c2s_min_txn_amt,");
        strBuff.append("LEAST(tpp.max_balance,ctpp.max_balance)max_balance,");
        strBuff.append("LEAST(tpp.c2s_max_txn_amt,ctpp.c2s_max_txn_amt)c2s_max_txn_amt,");
        strBuff.append("LEAST(tpp.alerting_balance,ctpp.alerting_balance)alerting_balance,");
        strBuff.append("LEAST(tpp.max_pct_transfer_allowed,ctpp.max_pct_transfer_allowed)max_pct_transfer_allowed ");
        strBuff.append("FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products ctpp,products prod ");
        strBuff.append("WHERE tpp.profile_id=? AND tpp.profile_id=tp.profile_id AND catp.profile_id=ctpp.profile_id ");
        strBuff.append("AND tpp.product_code=ctpp.product_code AND tp.category_code=catp.category_code ");
        strBuff.append("AND catp.parent_profile_id=? AND catp.status='Y' AND tp.network_code = catp.network_code ");
        strBuff.append("AND tpp.product_code=prod.product_code AND ctpp.product_code=prod.product_code");
        String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("loadEffTrfProfileProductListQuery", "select query:" + sqlSelect);
        }
        return sqlSelect;
    }
    
    
    
    
    public String loadSOSReconciliationList() {
        String selectQuery =handleUnsettledCasesQry.loadSOSReconciliationList();
        if (_log.isDebugEnabled()) {
            _log.debug("loadSOSReconciliationList", "select query:" + selectQuery);
        }
        return selectQuery;
    }
    public String updateSOSReconcilationStatus() {        
        String query = handleUnsettledCasesQry.updateSOSReconcilationStatus();
        if (_log.isDebugEnabled()) {
            _log.debug("updateSOSReconcilationStatus", "Query=" + query);
        }

        return query;

    }
}