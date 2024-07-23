package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockBL;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockDAO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.user.requesthandler.ChannelSOSSettlementHandler;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.transfer.businesslogic.BatchO2CTransferWebDAO;

public class UserDeletionBL {
    private static Log _log = LogFactory.getLog(UserDeletionBL.class.getName());
    
    
    protected static OperatorUtilI operatorUtili;
    
    /**
   	 * ensures no instantiation
   	 */
    private UserDeletionBL(){
    	
    }
    
    static {
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
        	_log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDeletionBL[static]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * @description : Method checks validation rules for deletion process
     * @param p_con
     * @param p_channelUserVO
     * @throws BTSLBaseException
     */
    public static void validateForDelete(Connection p_con, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        final UserDAO userDAO = new UserDAO();
        final boolean isChildFlag = userDAO.isChildUserActive(p_con, p_channelUserVO.getUserID());
        if (isChildFlag) {
            throw new BTSLBaseException(UserDeletionBL.class, "validateForDelete", PretupsErrorCodesI.ERROR_ERP_CHILD_USER_EXISTS);
        }
        // Checking SOS Pending transactions if feature is enabled
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue())  {
        ChannelSOSSettlementHandler channelSOSSettlementHandler = new ChannelSOSSettlementHandler();
        boolean isSOSPendingFlag = channelSOSSettlementHandler.validateSOSPending(p_con, p_channelUserVO.getUserID());
        if (isSOSPendingFlag) 
            throw new BTSLBaseException(UserDeletionBL.class, "validateForDelete", PretupsErrorCodesI.SOS_NOT_SETTLED_FOR_DELETION);
        }
        // Checking last recharge Pending transaction if feature is enabled
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED))).booleanValue())  {
        UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
        UserTransferCountsDAO userTrfCntDAO = new UserTransferCountsDAO();
        userTrfCntVO = userTrfCntDAO.selectLastLRTxnID(p_channelUserVO.getUserID(), p_con, false, null);
        if (userTrfCntVO!=null) 
            throw new BTSLBaseException(UserDeletionBL.class, "validateForDelete", PretupsErrorCodesI.LR_NOT_SETTLED_FOR_DELETION);
        }
        // Checking O2C Pending transactions
        final boolean isO2CPendingFlag = new ChannelTransferDAO().isPendingTransactionExist(p_con, p_channelUserVO.getUserID());
        if (isO2CPendingFlag) {
            throw new BTSLBaseException(UserDeletionBL.class, "validateForDelete", PretupsErrorCodesI.ERROR_ERP_O2C_TXN_PENDING);
        }
        // Checking Batch FOC Pending transactions
        final boolean isbatchFocPendingTxn = new FOCBatchTransferDAO().isPendingTransactionExist(p_con, p_channelUserVO.getUserID());
        if (isbatchFocPendingTxn) {
            throw new BTSLBaseException(UserDeletionBL.class, "validateForDelete", PretupsErrorCodesI.ERROR_ERP_FOC_TXN_PENDING);
        }
        boolean isC2SPendingFlag = new ChannelTransferDAO().isC2SPendingTransactionExist(p_con, p_channelUserVO.getUserID());
        if (isC2SPendingFlag) {
            throw new BTSLBaseException(UserDeletionBL.class, "validateForDelete", PretupsErrorCodesI.ERROR_ERP_C2S_TXN_PENDING);
        }
        
        boolean isRestrictedMsisdnFlag = false;
        if (PretupsI.YES.equals(p_channelUserVO.getCategoryVO().getRestrictedMsisdns())) {
            isRestrictedMsisdnFlag = new RestrictedSubscriberDAO().isSubscriberExistByChannelUser(p_con, p_channelUserVO.getUserID());
        }
        if (isRestrictedMsisdnFlag) {
            throw new BTSLBaseException(UserDeletionBL.class, "validateForDelete", PretupsErrorCodesI.ERROR_ERP_RESTRICTED_LIST_EXISTS);
        }
        
        boolean isBatchC2CTxnPendingFlag = new C2CBatchTransferDAO().isPendingC2CTransactionExist(p_con, p_channelUserVO.getUserID());
        if (isBatchC2CTxnPendingFlag) {
            throw new BTSLBaseException(UserDeletionBL.class, "validateForDelete", PretupsErrorCodesI.ERROR_ERP_BATCH_C2C_TXN_PENDING);
        }
        
        boolean isBatchO2CTxnPendingFlag = new BatchO2CTransferWebDAO().isPendingO2CTransactionExist(p_con, p_channelUserVO.getUserID());
        if (isBatchO2CTxnPendingFlag) {
            throw new BTSLBaseException(UserDeletionBL.class, "validateForDelete", PretupsErrorCodesI.ERROR_ERP_BATCH_O2C_TXN_PENDING);
        }
    }

    private static ChannelTransferVO prepareChannelTransferVO(ChannelTransferVO p_channelTransferVO, ChannelTransferItemsVO p_channelTransferItemsVO, Date p_curDate, ChannelUserVO p_channelUserVO, UserVO p_userVO) throws BTSLBaseException {
        final String methodName = "prepareChannelTransferVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering  : p_channelTransferVO" + p_channelTransferVO + "p_channelUserVO" + p_channelUserVO + "p_userVO" + p_userVO);
        }

        p_channelTransferVO.setNetworkCode(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setNetworkCodeFor(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setGraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
        p_channelTransferVO.setCategoryCode(p_channelUserVO.getCategoryCode());
        p_channelTransferVO.setReceiverGradeCode("");
        p_channelTransferVO.setSenderGradeCode(p_channelUserVO.getUserGrade());
        p_channelTransferVO.setFromUserID(p_channelUserVO.getUserID());
        p_channelTransferVO.setFromUserCode(p_channelUserVO.getUserCode());
        p_channelTransferVO.setToUserID(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setToUserCode(p_userVO.getUserCode());
        p_channelTransferVO.setTransferDate(p_curDate);
        p_channelTransferVO.setCommProfileSetId(p_channelUserVO.getCommissionProfileSetID());
        p_channelTransferVO.setCommProfileVersion(p_channelUserVO.getCommissionProfileSetVersion());
        p_channelTransferVO.setDualCommissionType(p_channelUserVO.getDualCommissionType());
        p_channelTransferVO.setCreatedOn(p_curDate);
        p_channelTransferVO.setCreatedBy(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setModifiedOn(p_curDate);
        p_channelTransferVO.setModifiedBy(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
        p_channelTransferVO.setTransferInitatedBy(p_userVO.getUserID());
        p_channelTransferVO.setActiveUserId(p_userVO.getUserID());
        p_channelTransferVO.setReceiverTxnProfile("");
        p_channelTransferVO.setSenderTxnProfile(p_channelUserVO.getTransferProfileID());
        p_channelTransferVO.setSource(p_channelUserVO.getGateway());
        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setFromUserCode(p_channelUserVO.getUserCode());
        p_channelTransferVO.setTransferCategory(PretupsI.TRANSFER_TYPE_SALE);
        String wallet = BTSLUtil.NullToString(Constants.getProperty("WALLET_TYPE_USER_DELETE"));// added
        // for
        // configuring
        // wallet
        // for
        // user
        // deletion
        if (BTSLUtil.isNullString(wallet)) {
            p_channelTransferVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
        } else {
            wallet = (wallet.trim()).toUpperCase();
            if (PretupsI.SALE_WALLET_TYPE.equals(wallet)) {
                p_channelTransferVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
            } else if (PretupsI.FOC_WALLET_TYPE.equals(wallet)) {
                p_channelTransferVO.setWalletType(PretupsI.FOC_WALLET_TYPE);
            } else if (PretupsI.INCENTIVE_WALLET_TYPE.equals(wallet)) {
                p_channelTransferVO.setWalletType(PretupsI.INCENTIVE_WALLET_TYPE);
            } else {
                p_channelTransferVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
            }
        }

        String productType = null;
        long totRequestQty = 0, totMRP = 0, totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
        totRequestQty += Long.parseLong(p_channelTransferItemsVO.getRequestedQuantity());
        totMRP += (Double.parseDouble(p_channelTransferItemsVO.getRequestedQuantity()) * Double.parseDouble(PretupsBL.getDisplayAmount(p_channelTransferItemsVO.getUnitValue())));
        totPayAmt += p_channelTransferItemsVO.getPayableAmount();
        totNetPayAmt += p_channelTransferItemsVO.getNetPayableAmount();
        totTax1 += p_channelTransferItemsVO.getTax1Value();
        totTax2 += p_channelTransferItemsVO.getTax2Value();
        totTax3 += p_channelTransferItemsVO.getTax3Value();
        productType = p_channelTransferItemsVO.getProductType();
        p_channelTransferVO.setRequestedQuantity(totRequestQty);
        p_channelTransferVO.setTransferMRP(totMRP);
        p_channelTransferVO.setPayableAmount(totPayAmt);
        p_channelTransferVO.setNetPayableAmount(totNetPayAmt);
        p_channelTransferVO.setTotalTax1(totTax1);
        p_channelTransferVO.setTotalTax2(totTax2);
        p_channelTransferVO.setTotalTax3(totTax3);
        p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
        p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
		p_channelTransferVO.setTransactionMode(PretupsI.TRANSACTION_MODE_DELETE);
        p_channelTransferVO.setProductType(p_channelTransferItemsVO.getProductType());
        p_channelTransferVO.setProductType(productType);
        p_channelTransferVO.setProductCode(p_channelTransferItemsVO.getProductCode());
        p_channelTransferVO.setRequestGatewayCode(p_channelUserVO.getGateway());
        p_channelTransferVO.setRequestGatewayType(p_channelUserVO.getGateway());

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting .....  :p_channelTransferVO" + p_channelTransferVO);
        }
        return p_channelTransferVO;
    }

    public static NetworkStockTxnVO preparenetworkStockTxnVO(Connection p_con, ChannelTransferVO p_channelTransferVO, Date p_curDate, String p_userID) throws BTSLBaseException {

        final String methodName = "preparenetworkStockTxnVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ChannelTransferVO = " + p_channelTransferVO + ", USER ID = " + p_userID + ", Curdate = " + p_curDate);
        }
        final NetworkStockTxnVO networkStockTxnVO = new NetworkStockTxnVO();
        networkStockTxnVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
        networkStockTxnVO.setNetworkFor(p_channelTransferVO.getNetworkCodeFor());
        if (p_channelTransferVO.getNetworkCode().equals(p_channelTransferVO.getNetworkCodeFor())) {
            networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
        } else {
            networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
        }
        networkStockTxnVO.setReferenceNo(p_channelTransferVO.getReferenceNum());
        networkStockTxnVO.setTxnDate(p_channelTransferVO.getModifiedOn());
        networkStockTxnVO.setRequestedQuantity(p_channelTransferVO.getRequestedQuantity());

        networkStockTxnVO.setInitiaterRemarks(p_channelTransferVO.getChannelRemarks());
        networkStockTxnVO.setFirstApprovedRemarks(p_channelTransferVO.getFirstApprovalRemark());
        networkStockTxnVO.setSecondApprovedRemarks(p_channelTransferVO.getSecondApprovalRemark());
        networkStockTxnVO.setFirstApprovedBy(p_channelTransferVO.getFirstApprovedBy());
        networkStockTxnVO.setSecondApprovedBy(p_channelTransferVO.getSecondApprovedBy());
        networkStockTxnVO.setFirstApprovedOn(p_channelTransferVO.getFirstApprovedOn());
        networkStockTxnVO.setSecondApprovedOn(p_channelTransferVO.getSecondApprovedOn());
        networkStockTxnVO.setCancelledBy(p_channelTransferVO.getCanceledBy());
        networkStockTxnVO.setCancelledOn(p_channelTransferVO.getCanceledOn());
        networkStockTxnVO.setCreatedBy(p_userID);
        networkStockTxnVO.setCreatedOn(p_curDate);
        networkStockTxnVO.setModifiedOn(p_curDate);
        networkStockTxnVO.setModifiedBy(p_userID);
        networkStockTxnVO.setTax3value(p_channelTransferVO.getTotalTax3());

        networkStockTxnVO.setTxnStatus(p_channelTransferVO.getStatus());
        networkStockTxnVO.setTxnNo(NetworkStockBL.genrateStockTransctionID(p_con, networkStockTxnVO));
        p_channelTransferVO.setReferenceID(networkStockTxnVO.getTxnNo());

        if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(p_channelTransferVO.getTransferType())) {
            networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_TRANSFER);
            networkStockTxnVO.setTxnType(PretupsI.DEBIT);
        } else if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(p_channelTransferVO.getTransferType())) {
            networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_RETURN);
            networkStockTxnVO.setTxnType(PretupsI.CREDIT);
        }

        networkStockTxnVO.setInitiatedBy(p_userID);
        networkStockTxnVO.setFirstApproverLimit(p_channelTransferVO.getFirstApproverLimit());
        networkStockTxnVO.setUserID(p_channelTransferVO.getFromUserID());
        networkStockTxnVO.setTxnMrp(p_channelTransferVO.getTransferMRP());

        final ArrayList<ChannelTransferItemsVO> list = p_channelTransferVO.getChannelTransferitemsVOList();
        ChannelTransferItemsVO channelTransferItemsVO = null;
        NetworkStockTxnItemsVO networkItemsVO = null;

        final ArrayList<NetworkStockTxnItemsVO> arrayList = new ArrayList<NetworkStockTxnItemsVO>();
        int j = 1;
        for (int i = 0, k = list.size(); i < k; i++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) list.get(i);

            networkItemsVO = new NetworkStockTxnItemsVO();
            networkItemsVO.setSNo(j++);
            networkItemsVO.setTxnNo(networkStockTxnVO.getTxnNo());
            networkItemsVO.setRequiredQuantity(p_channelTransferVO.getRequestedQuantity());
            networkItemsVO.setApprovedQuantity(p_channelTransferVO.getRequestedQuantity());
            networkItemsVO.setMrp(channelTransferItemsVO.getApprovedQuantity() * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue())));
            networkItemsVO.setAmount(channelTransferItemsVO.getPayableAmount());
            networkItemsVO.setDateTime(p_curDate);

            if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(p_channelTransferVO.getTransferType())) {
                networkItemsVO.setStock(channelTransferItemsVO.getAfterTransSenderPreviousStock());
            } else {
                networkItemsVO.setStock(channelTransferItemsVO.getAfterTransReceiverPreviousStock());
            }

            networkItemsVO.setProductCode(channelTransferItemsVO.getProductCode());
            arrayList.add(networkItemsVO);
        }
        networkStockTxnVO.setNetworkStockTxnItemsList(arrayList);
        networkStockTxnVO.setApprovedQuantity(p_channelTransferVO.getRequestedQuantity());
        networkStockTxnVO.setTxnMrp(p_channelTransferVO.getRequestedQuantity());
        networkStockTxnVO.setRefTxnID(p_channelTransferVO.getTransferID());

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exited networkStockTxnVO = " + networkStockTxnVO);
        }

        return networkStockTxnVO;
    }

    /**
     * Method to update the user balances, channel transfers, and channel
     * transfer items and user daily balances
     * while the leaf user is being deleted and the balance needs to go to the
     * owner.
     * 
     * @author akanksha
     * @param p_con
     * @param p_c2sTransferVO
     * @throws BTSLBaseException
     */
    public static void updateBalNChnlTransfersNItemsC2C(Connection p_con, ChannelUserVO fromChannelUserVO, ChannelUserVO toChannelUserVO, String p_loggedUserId, String p_gateway, UserBalancesVO userBalancesVO) throws BTSLBaseException {
        final String methodName = "updateBalNChnlTransfersNItemsC2C";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered fromUser ID = " + fromChannelUserVO.getUserID());
        }
        UserBalancesDAO userBalancesDAO = null;
        final ArrayList<UserBalancesVO> userBal = null;
        ChannelTransferVO channelTransferVO = null;
        try {
            int countCredit = 0;
            int countDebit = 0;
            int countTransfer = 0;
            long balCal = 0;
            final Date currDate = new Date();
            userBalancesDAO = new UserBalancesDAO();
            NetworkProductDAO networkProductDAO = null;
            final ArrayList<ChannelTransferItemsVO> itemsList = new ArrayList<ChannelTransferItemsVO>();
            ChannelTransferItemsVO channelTransferItemsVO = null;
            networkProductDAO = new NetworkProductDAO();
            networkProductDAO = new NetworkProductDAO();
            HashMap<String, NetworkProductVO> _networkProductMap = null;
            NetworkProductVO networkProductVO = null;
            String str[] = null;
            String newStatus[] = null;
            boolean changeStatusRequired = false;
            int updatecount1 = 0;
            
            long afterTransReceiverPreviousStock=0;
            long afterTransSenderPreviousStock=0;
          
            // userBalancesVO=itr.next();
            balCal += userBalancesVO.getBalance();
            if (userBalancesVO.getBalance() > 0) {
                channelTransferVO = new ChannelTransferVO();
                channelTransferVO.setNetworkCode(userBalancesVO.getNetworkCode());
                // to generate the withdraw ID
                ChannelTransferBL.genrateChnnlToChnnlWithdrawID(channelTransferVO);
                channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
                channelTransferVO.setTransferID(channelTransferVO.getTransferID());
                channelTransferVO.setTransferDate(currDate);
                channelTransferVO.setProductCode(userBalancesVO.getProductCode());

                channelTransferVO.setNetworkCodeFor(userBalancesVO.getNetworkFor());
                constructBalanceVOFromTxnVO(channelTransferVO);

                // to update the balance of the owner user
                countCredit += userBalancesDAO.updateCreditUserBalancesForDeleteC2C(p_con, fromChannelUserVO.getOwnerID(), channelTransferVO, userBalancesVO.getBalance(),
                    userBalancesVO.getBalanceType());
                afterTransReceiverPreviousStock=userBalancesDAO.getUserBalanceafterDeletionC2C(p_con, fromChannelUserVO.getOwnerID(), userBalancesVO.getBalanceType(), fromChannelUserVO.getNetworkID());
                // to update the balance of the leaf user
                countDebit += userBalancesDAO.updateUserBalancesForDelete(p_con, fromChannelUserVO.getUserID(), channelTransferVO, 0, userBalancesVO.getBalanceType());
                afterTransSenderPreviousStock=userBalancesDAO.getUserBalanceafterDeletionC2C(p_con, fromChannelUserVO.getUserID(), userBalancesVO.getBalanceType(), fromChannelUserVO.getNetworkID());
                // updating channel transfers and channel transfer items
                constructVofromChannelUserVOC2C(fromChannelUserVO, toChannelUserVO, channelTransferVO, currDate, userBalancesVO.getBalance(), p_loggedUserId);
                final ArrayList networkProductList = networkProductDAO.loadProductListForXfr(p_con, null, fromChannelUserVO.getNetworkID());
                _networkProductMap = new HashMap<String, NetworkProductVO>();
                int networksProductLists=networkProductList.size();
                for (int k = 0; k <networksProductLists ; k++) {
                    networkProductVO = (NetworkProductVO) networkProductList.get(k);
                    _networkProductMap.put(networkProductVO.getProductCode(), networkProductVO);
                }
                channelTransferItemsVO = new ChannelTransferItemsVO();
                channelTransferItemsVO = prepareChannelTransferItemsVO(p_con, fromChannelUserVO, userBalancesVO, _networkProductMap);
              
                channelTransferItemsVO.setAfterTransReceiverPreviousStock(afterTransReceiverPreviousStock);
                channelTransferItemsVO.setAfterTransSenderPreviousStock(afterTransSenderPreviousStock);
             
                itemsList.add(channelTransferItemsVO);
                channelTransferVO.setChannelTransferitemsVOList(itemsList);
                final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
                countTransfer += channelTransferDAO.addChannelTransfer(p_con, channelTransferVO);

                ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);

                itemsList.clear();

                if (countCredit > 0 && countDebit > 0 && countTransfer > 0) {
                    // updating user daily balances for owner credit
                    userBalancesVO.setLastTransferType(channelTransferVO.getTransferType());
                    userBalancesVO.setLastTransferOn(channelTransferVO.getTransferDate());
                    userBalancesVO.setLastTransferID(channelTransferVO.getTransferID());
                    userBalancesVO.setUserID(fromChannelUserVO.getOwnerID());
                    final int userDailyCount = userBalancesDAO.updateUserDailyBalancesForMultipleProductAndWallet(p_con, currDate, userBalancesVO);
                    // updating user daily balances for leaf user debit
                    userBalancesVO.setUserID(fromChannelUserVO.getUserID());
                    final int userDailyCount2 = userBalancesDAO.updateUserDailyBalancesForMultipleProductAndWallet(p_con, currDate, userBalancesVO);
                    //changing reciever user's status on receiving stock
                    if (!PretupsI.USER_STATUS_ACTIVE.equals(toChannelUserVO.getStatus())) {
                       str = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG)).split(","); // "CH:Y,EX:Y".split(",")
                     int st=str.length; 
                        for (int l = 0; l <st ; l++) {
                            newStatus = str[l].split(":");
                            if (newStatus[0].equals(toChannelUserVO.getStatus())) {
                                changeStatusRequired = true;
                                updatecount1 = operatorUtili.changeUserStatusToActive(p_con, toChannelUserVO.getUserID(), toChannelUserVO.getStatus(), newStatus[1]);
                                break;
                            }
                        }
                    }
                } else if (balCal > 0) {
                    throw new BTSLBaseException(UserDeletionBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                }
            }

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception p_transferID : " + channelTransferVO.getTransferID() + ", Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[updateBalNChnlTransfersNItemsC2C]",
                channelTransferVO.getTransferID(), fromChannelUserVO.getMsisdn(), userBalancesVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(UserDeletionBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exited...");
        }
    }

    /**
     * Method constructBalanceVOFromTxnVO.
     * 
     * @author akanksha
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @return UserBalancesVO
     */
    private static UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO) {
        final String methodName = "constructBalanceVOFromTxnVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_channelTransferVO=>" + p_channelTransferVO);
        }
        final UserBalancesVO userBalancesVO = new UserBalancesVO();
        userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
        userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
        userBalancesVO.setLastTransferOn(p_channelTransferVO.getTransferDate());
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting userBalancesVO=" + userBalancesVO);
        }
        return userBalancesVO;
    }

    /**
     * Method constructVofromChannelUserVOC2C
     * This method is to construct VO from the ChannelUserVO
     * 
     * @author akanksha
     * @param request
     * @param p_theForm
     * @param p_channelTransferVO
     * @param p_curDate
     * @throws BTSLBaseException
     */
    private static void constructVofromChannelUserVOC2C(ChannelUserVO p_fromChannelUserVO, ChannelUserVO p_toChannelUserVO, ChannelTransferVO p_channelTransferVO, Date p_curDate, long p_bal, String p_loggedUserId) throws BTSLBaseException {
        final String methodName = "constructVofromChannelUserVOC2C";
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                "Entered p_fromChannelUserVO: " + p_fromChannelUserVO + " p_toChannelUserVO" + p_toChannelUserVO + " ChannelTransferVO: " + p_channelTransferVO + " CurDate " + p_curDate);
        }

        p_channelTransferVO.setNetworkCode(p_fromChannelUserVO.getNetworkID());
        p_channelTransferVO.setNetworkCodeFor(p_fromChannelUserVO.getNetworkID());
        p_channelTransferVO.setCategoryCode(p_fromChannelUserVO.getCategoryCode());
        p_channelTransferVO.setSenderGradeCode(p_fromChannelUserVO.getUserGrade());
        p_channelTransferVO.setReceiverGradeCode(p_toChannelUserVO.getUserGrade());
        p_channelTransferVO.setDomainCode(p_fromChannelUserVO.getDomainID());
        p_channelTransferVO.setFromUserID(p_fromChannelUserVO.getUserID());
        p_channelTransferVO.setFromUserName(p_fromChannelUserVO.getUserName());
        // p_channelTransferVO.setToUserID(p_toChannelUserVO.getUserID());
        p_channelTransferVO.setToUserID(p_fromChannelUserVO.getOwnerID());
        p_channelTransferVO.setToUserName(p_toChannelUserVO.getUserName());
        p_channelTransferVO.setTransferDate(p_curDate);
        p_channelTransferVO.setGraphicalDomainCode(p_fromChannelUserVO.getGeographicalCode());
        p_channelTransferVO.setCommProfileSetId(p_fromChannelUserVO.getCommissionProfileSetID());
        p_channelTransferVO.setCommProfileVersion(p_fromChannelUserVO.getCommissionProfileSetVersion());
        p_channelTransferVO.setChannelRemarks(p_fromChannelUserVO.getRemarks());
        p_channelTransferVO.setCreatedOn(p_curDate);
        p_channelTransferVO.setCreatedBy(p_loggedUserId);
        p_channelTransferVO.setModifiedOn(p_curDate);
        p_channelTransferVO.setModifiedBy(p_loggedUserId);
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
        p_channelTransferVO.setTransferInitatedBy(p_loggedUserId);
        p_channelTransferVO.setSenderTxnProfile(p_fromChannelUserVO.getTransferProfileID());
        p_channelTransferVO.setReceiverTxnProfile(p_toChannelUserVO.getTransferProfileID());
        p_channelTransferVO.setSource(p_fromChannelUserVO.getGateway());
        p_channelTransferVO.setReceiverCategoryCode(p_toChannelUserVO.getCategoryCode());
        p_channelTransferVO.setTransferCategory(PretupsI.TRANSFER_TYPE_SALE);
        p_channelTransferVO.setRequestedQuantity(p_bal);
        p_channelTransferVO.setTransferMRP(p_bal);
        p_channelTransferVO.setPayableAmount(p_bal);
        p_channelTransferVO.setNetPayableAmount(p_bal);
        p_channelTransferVO.setTotalTax1(0);
        p_channelTransferVO.setTotalTax2(0);
        p_channelTransferVO.setTotalTax3(0);
        p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);
        p_channelTransferVO.setControlTransfer(PretupsI.NO);
        p_channelTransferVO.setActiveUserId(p_loggedUserId);
        // p_channelTransferVO.setControlTransfer(p_toChannelUserVO.getControlGroup());
        p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
        p_channelTransferVO.setTransactionMode(PretupsI.TRANSACTION_MODE_DELETE);

        p_channelTransferVO.setRequestGatewayCode(p_fromChannelUserVO.getGateway());
        p_channelTransferVO.setRequestGatewayType(p_fromChannelUserVO.getGateway());

        // adding the some additional information for sender/reciever
        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_toChannelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverDomainCode(p_toChannelUserVO.getDomainID());
        p_channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(p_toChannelUserVO.getMsisdn()));
        p_channelTransferVO.setFromUserCode(PretupsBL.getFilteredMSISDN(p_fromChannelUserVO.getMsisdn()));

        p_channelTransferVO.setToChannelUserStatus(p_toChannelUserVO.getStatus());
        p_channelTransferVO.setFromChannelUserStatus(p_fromChannelUserVO.getStatus());

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exited ChannelTransferVO: " + p_channelTransferVO + " CurDate " + p_curDate);
        }
    }

    public static void updateBalNChnlTransfersNItemsO2C(Connection con, ChannelUserVO fromChannelUserVO, ChannelUserVO _senderVO, String p_gateway, String p_gatewayType, UserBalancesVO userBalancesVO) throws BTSLBaseException {
        final String methodName = "updateBalNChnlTransfersNItemsO2C";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered fromChannelUserVO = " + fromChannelUserVO + ", _senderVO = " + _senderVO);
        }
        UserBalancesDAO userBalancesDAO = null;
        ChannelTransferVO channelTransferVO = null;
        ChannelTransferDAO channelTrfDAO = null;
        final Date currentDate = new Date();
        try {
            userBalancesDAO = new UserBalancesDAO();
            NetworkProductDAO networkProductDAO = null;
            final ArrayList<ChannelTransferItemsVO> itemsList = new ArrayList<ChannelTransferItemsVO>();
            ChannelTransferItemsVO channelTransferItemsVO = null;
            networkProductDAO = new NetworkProductDAO();
            HashMap<String, NetworkProductVO> _networkProductMap = null;
            NetworkProductVO networkProductVO = null;
            final long balance = 0;
            if (userBalancesVO.getBalance() > 0) {
                channelTrfDAO = new ChannelTransferDAO();
                channelTransferItemsVO = new ChannelTransferItemsVO();
                channelTransferVO = new ChannelTransferVO();
                final ArrayList networkProductList = networkProductDAO.loadProductListForXfr(con, null, fromChannelUserVO.getNetworkID());
                _networkProductMap = new HashMap<String, NetworkProductVO>();
                int networkProductLists=networkProductList.size();
                for (int k = 0; k < networkProductLists; k++) {
                    networkProductVO = (NetworkProductVO) networkProductList.get(k);
                    _networkProductMap.put(networkProductVO.getProductCode(), networkProductVO);
                }
                channelTransferItemsVO = prepareChannelTransferItemsVO(con, fromChannelUserVO, userBalancesVO, _networkProductMap);
                itemsList.add(channelTransferItemsVO);
                channelTransferVO.setChannelTransferitemsVOList(itemsList);
               // ChannelTransferBL.calculateMRPWithTaxAndDiscount(itemsList, PretupsI.TRANSFER_TYPE_O2C);
                // prepareChannelTransferVO(p_gateway,p_gatewayType,
                // channelTransferVO, currentDate, fromChannelUserVO,
                // userBalancesVO, _senderVO, productVO);
                prepareChannelTransferVO(channelTransferVO, channelTransferItemsVO, currentDate, fromChannelUserVO, _senderVO);
                ChannelTransferBL.genrateWithdrawID(channelTransferVO);
                //
                channelTransferItemsVO.setTransferID(channelTransferVO.getTransferID());
                channelTransferVO.setControlTransfer(PretupsI.YES);
                // channelTransferVO.setProductType(productVO.getProductType());
                transactionApproval(con, channelTransferVO, channelTransferItemsVO, fromChannelUserVO.getUserID(), currentDate);
                // channelTrfDAO.addChannelTransfer(con, channelTransferVO);
                // networkStockTxnVO=UserDeletionBL.preparenetworkStockTxnVO(con,channelTransferVO,currentDate,fromChannelUserVO.getUserID());
                final int insertCount = channelTrfDAO.addChannelTransfer(con, channelTransferVO);
                if (insertCount < 1) {
                    con.rollback();
                    throw new BTSLBaseException("BarForDeletionProcess", "processRecords", PretupsErrorCodesI.DATA_NOT_UPDATED);
                }
                ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
                // networkStockDAO.addNetworkStockTransaction(con,
                // networkStockTxnVO);
                // ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(con,
                // channelTransferVO, fromChannelUserVO.getUserID(),
                // currentDate, false);
                itemsList.clear();

                final int updateCount = userBalancesDAO.updateUserBalancesForDeleteO2C(con, fromChannelUserVO.getUserID(), channelTransferVO, balance, userBalancesVO
                    .getBalanceType());
                if (updateCount > 0) {
                    // updating user daily balances for user debit
                    userBalancesVO.setLastTransferType(channelTransferVO.getTransferType());
                    userBalancesVO.setLastTransferOn(channelTransferVO.getTransferDate());
                    userBalancesVO.setLastTransferID(channelTransferVO.getTransferID());
                    userBalancesVO.setUserID(fromChannelUserVO.getUserID());
                    final int userDailyCount = userBalancesDAO.updateUserDailyBalancesForMultipleProductAndWallet(con, currentDate, userBalancesVO);
                }

                else {
                    if (updateCount < 1) {
                        throw new BTSLBaseException("UserDeletionBL", methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
                    }
                }
            }

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception p_transferID : " + channelTransferVO.getTransferID() + ", Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDeletionBL[updateBalNChnlTransfersNItemsO2C]",
                channelTransferVO.getTransferID(), fromChannelUserVO.getMsisdn(), userBalancesVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(UserDeletionBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exited...");
        }
    }

    public static ChannelTransferItemsVO prepareChannelTransferItemsVO(Connection p_con, ChannelUserVO p_channelUserVO, UserBalancesVO p_userBalanceVO, HashMap _networkProductMap) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("prepareChannelTransferItemsVO", "Entering  : p_channelUserVO" + p_channelUserVO + "UserBalancesVO" + p_userBalanceVO);
        }
        NetworkProductVO networkProductVO = null;
        ChannelTransferItemsVO channelTransferItemsVO = null;
        networkProductVO = (NetworkProductVO) _networkProductMap.get(p_userBalanceVO.getProductCode());
        // default commission rate
        final double commRate = 0.0;
        if (p_userBalanceVO.getProductCode().equals(networkProductVO.getProductCode())) {
            channelTransferItemsVO = new ChannelTransferItemsVO();
            channelTransferItemsVO.setProductType(networkProductVO.getProductType());
            channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
            channelTransferItemsVO.setProductCode(networkProductVO.getProductCode());
            channelTransferItemsVO.setProductName(networkProductVO.getProductName());
            channelTransferItemsVO.setShortName(networkProductVO.getShortName());
            channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
            channelTransferItemsVO.setProductCategory(networkProductVO.getProductCategory());
            channelTransferItemsVO.setErpProductCode(networkProductVO.getErpProductCode());
            channelTransferItemsVO.setStatus(networkProductVO.getStatus());
            channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());
            channelTransferItemsVO.setModuleCode(networkProductVO.getModuleCode());
            channelTransferItemsVO.setProductUsage(networkProductVO.getProductUsage());
            channelTransferItemsVO.setRequestedQuantity(String.valueOf(p_userBalanceVO.getBalance()));
            channelTransferItemsVO.setRequiredQuantity(p_userBalanceVO.getBalance());
            // setting the default value for this
            channelTransferItemsVO.setTax1Type(PretupsI.AMOUNT_TYPE_PERCENTAGE);
            channelTransferItemsVO.setTax1Rate(commRate);
            channelTransferItemsVO.setTax2Type(PretupsI.AMOUNT_TYPE_PERCENTAGE);
            channelTransferItemsVO.setTax2Rate(commRate);
            channelTransferItemsVO.setTax3Type(PretupsI.AMOUNT_TYPE_PERCENTAGE);
            channelTransferItemsVO.setTax3Rate(commRate);
            channelTransferItemsVO.setCommType(PretupsI.AMOUNT_TYPE_PERCENTAGE);
            channelTransferItemsVO.setCommRate(commRate);
            
            //defect: balance of the receiver was not coming in channel enquiry
            //sol to a problem in fixing: last txn id from user balance ..with that i can get detail id ..n that detail id is to be set in channel transfer item
            ChannelTransferDAO chnlTransferDAO=new ChannelTransferDAO();
            ArrayList<ChannelTransferItemsVO> ChannelTransferItemList=new ArrayList<ChannelTransferItemsVO>();
            ChannelTransferItemList=chnlTransferDAO.loadChannelTransferItems(p_con,p_userBalanceVO.getLastTransferID());
            if (ChannelTransferItemList != null && !ChannelTransferItemList.isEmpty()){
            	channelTransferItemsVO.setCommProfileDetailID(ChannelTransferItemList.get(0).getCommProfileDetailID());
            }
            
           // channelTransferItemsVO.setCommProfileDetailID(PretupsI.NOT_APPLICABLE);
            //channelTransferItemsVO.setRequiredQuantity(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
            channelTransferItemsVO.setDiscountType(PretupsI.AMOUNT_TYPE_PERCENTAGE);
            channelTransferItemsVO.setDiscountRate(commRate);
            channelTransferItemsVO.setNetPayableAmount(p_userBalanceVO.getBalance());
            channelTransferItemsVO.setPayableAmount(p_userBalanceVO.getBalance());
            channelTransferItemsVO.setProductTotalMRP(p_userBalanceVO.getBalance());
            channelTransferItemsVO.setApprovedQuantity(p_userBalanceVO.getBalance());
            channelTransferItemsVO.setSenderDebitQty(p_userBalanceVO.getBalance());
            channelTransferItemsVO.setReceiverCreditQty(p_userBalanceVO.getBalance());
            channelTransferItemsVO.setSenderPreviousStock(p_userBalanceVO.getBalance());
            channelTransferItemsVO.setAfterTransSenderPreviousStock(p_userBalanceVO.getBalance());
        } else {
            _log.error("prepareChannelTransferItemsVO ", ": Associated product for the user could not be found for the user id : " + p_channelUserVO.getUserID());
            throw new BTSLBaseException("BarForDeletionProcess", "prepareChannelTransferItemsVO", PretupsErrorCodesI.ASS_PROD_NOT_FOUND);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("prepareChannelTransferItemsVO", "Exiting : channelTransferItemsVO" + channelTransferItemsVO);
        }
        return channelTransferItemsVO;
    }

    private static void transactionApproval(Connection p_con, ChannelTransferVO p_channelTransferVO, ChannelTransferItemsVO p_channelTransferItemsVO, String p_userID, Date p_date) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("transactionApproval", "Entering  : p_channelTransferVO " + p_channelTransferVO);
        }

        int updateCount = -1;

        updateCount = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, p_userID, p_date, false);
        if (updateCount < 1) {
            throw new BTSLBaseException("BarForDeletionProcess", "transactionApproval", PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
        }// end of if
        updateCount = -1;
        // this method updates the network stock and also updates the network
        // transaction details
        updateCount = updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, p_userID, p_date);
        if (updateCount < 1) {
            throw new BTSLBaseException("BarForDeletionProcess", "transactionApproval", PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
        }
        updateCount = -1;
        // this call updates the counts/values for daily, weekly and monthly IN
        updateCount = ChannelTransferBL.updateOptToChannelUserInCounts(p_con, p_channelTransferVO, null, p_date);
        if (updateCount < 1) {
            throw new BTSLBaseException("BarForDeletionProcess", "transactionApproval", PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
        }// end of if

        if (_log.isDebugEnabled()) {
            _log.debug("transactionApproval", "Exiting...... : p_channelTransferVO " + p_channelTransferVO);
        }
    }

    public static int updateNetworkStockTransactionDetails(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userID, Date p_curDate) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateNetworkStockTransactionDetails", "Entered ChannelTransferVO =" + p_channelTransferVO + " USERID " + p_userID + " Curdate " + p_curDate);
        }
        int updateCount = 0;

        final NetworkStockTxnVO networkStockTxnVO = new NetworkStockTxnVO();
        networkStockTxnVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
        networkStockTxnVO.setNetworkFor(p_channelTransferVO.getNetworkCodeFor());
        if (p_channelTransferVO.getNetworkCode().equals(p_channelTransferVO.getNetworkCodeFor())) {
            networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
        } else {
            networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
        }
        networkStockTxnVO.setReferenceNo(p_channelTransferVO.getReferenceNum());
        networkStockTxnVO.setTxnDate(p_channelTransferVO.getModifiedOn());
        networkStockTxnVO.setRequestedQuantity(p_channelTransferVO.getRequestedQuantity());
        networkStockTxnVO.setApprovedQuantity(p_channelTransferVO.getRequestedQuantity());
        networkStockTxnVO.setInitiaterRemarks(p_channelTransferVO.getChannelRemarks());
        networkStockTxnVO.setFirstApprovedRemarks(p_channelTransferVO.getFirstApprovalRemark());
        networkStockTxnVO.setSecondApprovedRemarks(p_channelTransferVO.getSecondApprovalRemark());
        networkStockTxnVO.setFirstApprovedBy(p_channelTransferVO.getFirstApprovedBy());
        networkStockTxnVO.setSecondApprovedBy(p_channelTransferVO.getSecondApprovedBy());
        networkStockTxnVO.setFirstApprovedOn(p_channelTransferVO.getFirstApprovedOn());
        networkStockTxnVO.setSecondApprovedOn(p_channelTransferVO.getSecondApprovedOn());
        networkStockTxnVO.setCancelledBy(p_channelTransferVO.getCanceledBy());
        networkStockTxnVO.setCancelledOn(p_channelTransferVO.getCanceledOn());
        networkStockTxnVO.setCreatedBy(PretupsI.OPERATOR_TYPE_OPT);
        networkStockTxnVO.setCreatedOn(p_curDate);
        networkStockTxnVO.setModifiedOn(p_curDate);
        networkStockTxnVO.setModifiedBy(PretupsI.OPERATOR_TYPE_OPT);

        networkStockTxnVO.setTxnStatus(p_channelTransferVO.getStatus());
        networkStockTxnVO.setTxnNo(NetworkStockBL.genrateStockTransctionID(p_con, networkStockTxnVO));
        p_channelTransferVO.setReferenceID(networkStockTxnVO.getTxnNo());

        if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(p_channelTransferVO.getTransferType())) {
            networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_TRANSFER);
            networkStockTxnVO.setTxnType(PretupsI.DEBIT);
        } else if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(p_channelTransferVO.getTransferType())) {
            networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_RETURN);
            networkStockTxnVO.setTxnType(PretupsI.CREDIT);
        }

        networkStockTxnVO.setInitiatedBy(PretupsI.OPERATOR_TYPE_OPT);
        networkStockTxnVO.setFirstApproverLimit(p_channelTransferVO.getFirstApproverLimit());
        networkStockTxnVO.setUserID(p_channelTransferVO.getFromUserID());
        networkStockTxnVO.setTxnMrp(p_channelTransferVO.getTransferMRP());

        final ArrayList list = p_channelTransferVO.getChannelTransferitemsVOList();
        ChannelTransferItemsVO channelTransferItemsVO = null;
        NetworkStockTxnItemsVO networkItemsVO = null;

        final ArrayList<NetworkStockTxnItemsVO> networkStockTxnItemsVOList = new ArrayList<NetworkStockTxnItemsVO>();
        int j = 1;
        for (int i = 0, k = list.size(); i < k; i++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) list.get(i);

            networkItemsVO = new NetworkStockTxnItemsVO();
            networkItemsVO.setSNo(j++);
            networkItemsVO.setTxnNo(networkStockTxnVO.getTxnNo());
            networkItemsVO.setRequiredQuantity(channelTransferItemsVO.getRequiredQuantity());
            networkItemsVO.setApprovedQuantity(channelTransferItemsVO.getApprovedQuantity());
            networkItemsVO.setMrp(channelTransferItemsVO.getApprovedQuantity() * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue())));
            networkItemsVO.setAmount(channelTransferItemsVO.getPayableAmount());
            if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(p_channelTransferVO.getTransferType())) {
                networkItemsVO.setStock(channelTransferItemsVO.getAfterTransSenderPreviousStock());
            } else {
                networkItemsVO.setStock(channelTransferItemsVO.getAfterTransReceiverPreviousStock());
            }
            networkItemsVO.setProductCode(channelTransferItemsVO.getProductCode());
            networkItemsVO.setDateTime(p_curDate);
            networkStockTxnItemsVOList.add(networkItemsVO);
        }
        networkStockTxnVO.setNetworkStockTxnItemsList(networkStockTxnItemsVOList);
        final NetworkStockDAO networkStockDAO = new NetworkStockDAO();
        updateCount = networkStockDAO.addNetworkStockTransaction(p_con, networkStockTxnVO);
        if (_log.isDebugEnabled()) {
            _log.debug("updateNetworkStockTransactionDetails", "Exited  updateCount " + updateCount);
        }
        return updateCount;
    }

}
