/**
 * @(#)ChnlToChnlTransferTransactionCntrl.java
 *                                             Copyright(c) 2005, Bharti
 *                                             Telesoft Ltd.
 *                                             All Rights Reserved
 * 
 *                                             <description>
 *                                             --------------------------------
 *                                             --
 *                                             --------------------------------
 *                                             -------------------------------
 *                                             Author Date History
 *                                             --------------------------------
 *                                             --
 *                                             --------------------------------
 *                                             -------------------------------
 *                                             avinash.kamthan Aug 25, 2005
 *                                             Initital Creation
 *                                             Sandeep Goel 01-Dec-2005
 *                                             modification/Customization
 *                                             --------------------------------
 *                                             --
 *                                             --------------------------------
 *                                             -------------------------------
 * 
 */

package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyBL;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyDAO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.TargetBasedCommissionMessages;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;

/**
 * @author avinash.kamthan
 * 
 */
public class ChnlToChnlTransferTransactionCntrl {
    /**
     * Field _log.
     */
    private static Log _log = LogFactory.getLog(ChnlToChnlTransferTransactionCntrl.class.getName());
    
    /**
	 * ensures no instantiation
	 */
    private ChnlToChnlTransferTransactionCntrl(){
    	
    }
    public static OperatorUtilI operatorUtili = null;
    static {
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChnlToChnlTransferTransactionCntrl[static]", "", "",
                "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param p_isOutSideHierarchy
     * @param p_fromWEB
     * @param p_forwardPath
     * @param p_curDate
     *            TODO
     * @return int
     * @throws BTSLBaseException
     */
    public static int approveChannelToChannelTransfer(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean p_isOutSideHierarchy, boolean p_fromWEB, String p_forwardPath, Date p_curDate) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (_log.isDebugEnabled()) {
        	
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_channelTransferVO: ");
        	loggerValue.append(p_channelTransferVO);
        	loggerValue.append("p_isOutSideHirearchy: ");
        	loggerValue.append(p_isOutSideHierarchy);
        	loggerValue.append(" fromWeb :");
        	loggerValue.append(p_fromWEB);
        	loggerValue.append(" p_forwardPath ");
        	loggerValue.append(p_forwardPath);
            _log.debug("approveChannelToChannelTransfer",loggerValue );
        }
        final String METHOD_NAME = "approveChannelToChannelTransfer";
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        boolean sepOutsideTxnCtrl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SEP_OUTSIDE_TXN_CTRL);
        boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
        String txnSenderUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG));
        String txnReceiverUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG));
        
        int updateCount = 0;

        /*
         * check the user transfer counts
         * Here if Txn is outSide hierarchy then counters are different
         * else counters are different
         */
        if (p_isOutSideHierarchy && sepOutsideTxnCtrl) {

            /*
             * checks the outside out counts and if there is a error message
             * returned then show the error message
             * other wise checks the outside in coutns if there is a error
             * message returned then show the
             * error message
             */
            final String messageOutsideOutCountCheck = ChannelTransferBL.checkOutsideTransferOutCounts(p_con, p_channelTransferVO.getFromUserID(), p_channelTransferVO
                .getSenderTxnProfile(), p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageOutsideOutCountCheck != null) {
                String arg = p_channelTransferVO.getFromUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getFromUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "approveChannelToChannelTransfer", messageOutsideOutCountCheck, 0, args, p_forwardPath);
            }

            final String messageOutsideInCountCheck = ChannelTransferBL.checkOutsideTransferINCounts(p_con, p_channelTransferVO.getToUserID(), p_channelTransferVO
                .getReceiverTxnProfile(), p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageOutsideInCountCheck != null) {
                String arg = p_channelTransferVO.getToUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getToUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "approveChannelToChannelTransfer", messageOutsideInCountCheck, 0, args, p_forwardPath);
            }
        } else {
            /*
             * checks the out counts and if there is a error message returned
             * then show the error message
             * other wise checks the in coutns if there is a error message
             * returned then show the
             * error message
             */
            final String messageOutCountCheck = ChannelTransferBL.checkTransferOutCounts(p_con, p_channelTransferVO.getFromUserID(),
                p_channelTransferVO.getSenderTxnProfile(), p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageOutCountCheck != null) {
                String arg = p_channelTransferVO.getFromUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getFromUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "approveChannelToChannelTransfer", messageOutCountCheck, 0, args, p_forwardPath);
            }

            final String messageInCountCheck = ChannelTransferBL.checkTransferINCounts(p_con, p_channelTransferVO.getToUserID(), p_channelTransferVO.getReceiverTxnProfile(),
                p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageInCountCheck != null) {
                String arg = p_channelTransferVO.getToUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getToUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "approveChannelToChannelTransfer", messageInCountCheck, 0, args, p_forwardPath);
            }
        }
        /*
         * generate the TXN ID for the txn as TRANSFER/RETURN/WITHDRAW
         */
        if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_channelTransferVO.getTransferSubType())) {
            ChannelTransferBL.genrateChnnlToChnnlTrfID(p_channelTransferVO);
        } else if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(p_channelTransferVO.getTransferSubType())) {
            ChannelTransferBL.genrateChnnlToChnnlReturnID(p_channelTransferVO);
        } else if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_channelTransferVO.getTransferSubType())) {
            ChannelTransferBL.genrateChnnlToChnnlWithdrawID(p_channelTransferVO);
        }

        /*
         * Now update user balances in all the updation method we are checking
         * for the update count
         * if it is lessthan or equal to 0 then throw exception so no need to
         * check here
         * first update daily balances of both of the user
         * then debit the Sender user
         * then credit the receiver user
         * and then update the thresholds of the users
         * at last insert the TXN data in the parent and child tables.
         */
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        final ChannelUserTxnDAO channelUserTxnDAO = new ChannelUserTxnDAO();
        int level;
        if(p_channelTransferVO.getIsFileC2C().equalsIgnoreCase("Y"))
        {
        	level=(((Integer) PreferenceCache.getSystemPreferenceValue(PretupsI.C2C_BATCH_APPROVAL_LEVEL)).intValue());
            
        }
         
        else
        	{
        	level=((Integer)PreferenceCache.getControlPreference(PreferenceI.MAX_APPROVAL_LEVEL_C2C_TRANSFER, p_channelTransferVO.getNetworkCode(), p_channelTransferVO.getSenderCategory())).intValue();
        	}
        if(String.valueOf(level).equals("0"))
        {
        	
       final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
        
       final UserBalancesVO userBalancesVO = constructBalanceVOFromTxnVO(p_channelTransferVO);
        userBalancesVO.setUserID(p_channelTransferVO.getFromUserID());
        userBalancesDAO.updateUserDailyBalances(p_con, p_curDate, userBalancesVO);
        userBalancesVO.setUserID(p_channelTransferVO.getToUserID());
        userBalancesDAO.updateUserDailyBalances(p_con, p_curDate, userBalancesVO);

        
        // debit the sender balances
        if(p_channelTransferVO.getIsFileC2C().equalsIgnoreCase("N"))
        {
        if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,p_channelTransferVO.getNetworkCode()) && p_fromWEB && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_channelTransferVO.getTransferSubType()))
        {
        	ChannelTransferBL.increaseOptOTFCounts(p_con, p_channelTransferVO);
        }
        }
        updateCount = channelUserDAO.debitUserBalancesForMultipleWallet(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);

        // For Mali --- +ve commision ,,,, debit the networkStock for commision
        // qty
        final boolean debit = true;
        if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
            ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(p_con, p_channelTransferVO, p_channelTransferVO.getFromUserID(), p_curDate, debit);
            ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(p_con, p_channelTransferVO, p_channelTransferVO.getFromUserID(), p_curDate);
        }
        // credit the receiver
        if (userProductMultipleWallet) {
            updateCount = channelUserDAO.creditUserBalancesForMultipleWallet(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);
        } else {
            updateCount = channelUserDAO.creditUserBalances(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);
        }
        p_channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_C2C);
        
        if (p_isOutSideHierarchy && sepOutsideTxnCtrl) {
            updateCount = ChannelTransferBL.updateChannelToChannelTransferOutSideCounts(p_con, p_channelTransferVO, p_curDate, p_fromWEB, p_forwardPath);
        } else {
            updateCount = ChannelTransferBL.updateChannelToChannelTransferCounts(p_con, p_channelTransferVO, p_curDate, p_fromWEB, p_forwardPath);
        }
        
        if (p_fromWEB) {
            // commented as disscussed with Sanjay Sir, GSB, AC need not to be
            // updated in WEB

       } else {
            updateCount = channelUserTxnDAO.updateUserPhoneAfterTxn(p_con, p_channelTransferVO, p_channelTransferVO.getToUserCode(), p_channelTransferVO.getToUserID(), false);
        }
        }
        // insert the TXN data in the parent and child tables.
        final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
        
        
        if(String.valueOf(level).equals("0"))
     	   {
        	   p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE);
     		   updateCount = channelTransferDAO.addChannelTransfer(p_con, p_channelTransferVO);
     	   }
     	   else
     		   {
     		   updateCount = channelTransferDAO.addChannelTransfer(p_con, p_channelTransferVO);
     		   }
        if (lmsAppl && updateCount == 1 && p_fromWEB) {
            try {
                if (p_channelTransferVO.getStatus().equals("CLOSE")) {
                    final LoyaltyBL _loyaltyBL = new LoyaltyBL();
                    final LoyaltyVO loyaltyVO = new LoyaltyVO();
                    
                    final LoyaltyDAO _loyaltyDAO = new LoyaltyDAO();
                    final ArrayList<String> arr = new ArrayList<String>();
                    loyaltyVO.setModuleType(PretupsI.C2C_MODULE);
                    loyaltyVO.setServiceType(PretupsI.C2C_MODULE);
                    loyaltyVO.setTransferamt(p_channelTransferVO.getRequestedQuantity());
                    loyaltyVO.setCategory(p_channelTransferVO.getCategoryCode());
                    loyaltyVO.setFromuserId(p_channelTransferVO.getFromUserID());
                    loyaltyVO.setTouserId(p_channelTransferVO.getToUserID());
                    loyaltyVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
                    loyaltyVO.setTxnId(p_channelTransferVO.getTransferID());
                    loyaltyVO.setCreatedOn(p_channelTransferVO.getCreatedOn());
                    loyaltyVO.setSenderMsisdn(p_channelTransferVO.getFromUserCode());
                    loyaltyVO.setReciverMsisdn(p_channelTransferVO.getToUserCode());
                    loyaltyVO.setProductCode(p_channelTransferVO.getProductCode());
                    arr.add(loyaltyVO.getFromuserId());
                    arr.add(loyaltyVO.getTouserId());
                    PromotionDetailsVO promotionDetailsVO = _loyaltyDAO.loadSetIdByUserId(p_con, arr);
                    loyaltyVO.setSetId(promotionDetailsVO.get_setId());
                    loyaltyVO.setToSetId(promotionDetailsVO.get_toSetId());

                    if (loyaltyVO.getSetId() == null && loyaltyVO.getToSetId() == null) {
                        _log.error("process", "Exception during LMS Module.SetId not found");
                    } else {
                        _loyaltyBL.distributeLoyaltyPoints(PretupsI.C2C_MODULE, p_channelTransferVO.getTransferID(), loyaltyVO);
                    }
                }
            } catch (Exception ex) {
                _log.error("process", "Exception durign LMS Module " + ex.getMessage());
                _log.errorTrace(METHOD_NAME, ex);
            }

        }
        try {
            if (updateCount == 1 && "CLOSE".equals(p_channelTransferVO.getStatus()) && p_fromWEB) {
                boolean statusChangeRequired = false;
                int updatecount1 = 0;
                int updatecount2 = 0;
                if (!PretupsI.USER_STATUS_ACTIVE.equals(p_channelTransferVO.getFromChannelUserStatus())) {
                    // int updatecount=operatorUtili.changeUserStatusToActive(
                    // p_con,p_channelTransferVO.getFromUserID(),p_channelTransferVO.getFromChannelUserStatus());

                    final String str[] = txnSenderUserStatusChang.split(","); // "CH:Y,EX:Y".split(",");
                    String newStatus[] = null;
                    int strs=str.length;
                    for (int i = 0; i <strs ; i++) {
                        newStatus = str[i].split(":");
                        if (newStatus[0].equals(p_channelTransferVO.getFromChannelUserStatus())) {
                            statusChangeRequired = true;
                            updatecount1 = operatorUtili.changeUserStatusToActive(p_con, p_channelTransferVO.getFromUserID(), p_channelTransferVO.getFromChannelUserStatus(),
                                newStatus[1]);
                            break;
                        }
                    }

                }

                if (!PretupsI.USER_STATUS_ACTIVE.equals(p_channelTransferVO.getToChannelUserStatus())) {
                    // int updatecount=operatorUtili.changeUserStatusToActive(
                    // p_con,p_channelTransferVO.getToUserID(),p_channelTransferVO.getToChannelUserStatus());

                    final String str[] = txnReceiverUserStatusChang.split(","); // "CH:Y,EX:Y".split(",");
                    String newStatus[] = null;
                    int st=str.length;
                    for (int i = 0; i <st ; i++) {
                        newStatus = str[i].split(":");
                        if (newStatus[0].equals(p_channelTransferVO.getToChannelUserStatus())) {
                            statusChangeRequired = true;
                            updatecount2 = operatorUtili.changeUserStatusToActive(p_con, p_channelTransferVO.getToUserID(), p_channelTransferVO.getToChannelUserStatus(),
                                newStatus[1]);
                            break;
                        }
                    }

                }
                if (statusChangeRequired && (updatecount1 > 1 || updatecount2 > 1)) {
                    updateCount = 0;
                }
                
            	if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,p_channelTransferVO.getNetworkCode()))
        		{
                	if(p_channelTransferVO.isTargetAchieved())
        			{
        				TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
        				/*ChannelUserVO channelUserVO = new ChannelUserVO();
        				channelUserVO.setUserID(p_channelTransferVO.getToUserID());
        				tbcm.loadBaseCommissionProfileDetailsForTargetMessages(p_con,channelUserVO);*/
        				tbcm.loadBaseCommissionProfileDetailsForTargetMessages(p_con,p_channelTransferVO.getToUserID(),p_channelTransferVO.getMessageArgumentList());
        			}
        		} 
            }
        } catch (Exception e) {
        	
            _log.errorTrace("approveChannelToChannelTransfer", e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception durign status update to active ");
        	loggerValue.append(e.getMessage());
            _log.error("approveChannelToChannelTransfer",  loggerValue );
        }
        OneLineTXNLog.log(p_channelTransferVO, null);
        if (_log.isDebugEnabled()) {
        	 loggerValue.setLength(0);
         	loggerValue.append("Exit updateCount =");
         	loggerValue.append(updateCount);
            _log.debug("approveChannelToChannelTransfer",  loggerValue);
        }

        return updateCount;
    }
    public static int updateUserBalance(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean p_isOutSideHierarchy, boolean p_fromWEB, String p_forwardPath, Date p_curDate) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (_log.isDebugEnabled()) {
        	
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_channelTransferVO: ");
        	loggerValue.append(p_channelTransferVO);
        	loggerValue.append("p_isOutSideHirearchy: ");
        	loggerValue.append(p_isOutSideHierarchy);
        	loggerValue.append(" fromWeb :");
        	loggerValue.append(p_fromWEB);
        	loggerValue.append(" p_forwardPath ");
        	loggerValue.append(p_forwardPath);
            _log.debug("updateUserBalance",loggerValue );
        }
        final String METHOD_NAME = "updateUserBalance";
        boolean sepOutsideTxnCtrl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SEP_OUTSIDE_TXN_CTRL);
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
        String txnSenderUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG));
        String txnReceiverUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG));
        
        int updateCount = 0;

        /*
         * check the user transfer counts
         * Here if Txn is outSide hierarchy then counters are different
         * else counters are different
         */
        if (p_isOutSideHierarchy && sepOutsideTxnCtrl) {

            /*
             * checks the outside out counts and if there is a error message
             * returned then show the error message
             * other wise checks the outside in coutns if there is a error
             * message returned then show the
             * error message
             */
            final String messageOutsideOutCountCheck = ChannelTransferBL.checkOutsideTransferOutCounts(p_con, p_channelTransferVO.getFromUserID(), p_channelTransferVO
                .getSenderTxnProfile(), p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageOutsideOutCountCheck != null) {
                String arg = p_channelTransferVO.getFromUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getFromUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "updateUserBalance", messageOutsideOutCountCheck, 0, args, p_forwardPath);
            }

            final String messageOutsideInCountCheck = ChannelTransferBL.checkOutsideTransferINCounts(p_con, p_channelTransferVO.getToUserID(), p_channelTransferVO
                .getReceiverTxnProfile(), p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageOutsideInCountCheck != null) {
                String arg = p_channelTransferVO.getToUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getToUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "updateUserBalance", messageOutsideInCountCheck, 0, args, p_forwardPath);
            }
        } else {
            /*
             * checks the out counts and if there is a error message returned
             * then show the error message
             * other wise checks the in coutns if there is a error message
             * returned then show the
             * error message
             */
            final String messageOutCountCheck = ChannelTransferBL.checkTransferOutCounts(p_con, p_channelTransferVO.getFromUserID(),
                p_channelTransferVO.getSenderTxnProfile(), p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageOutCountCheck != null) {
                String arg = p_channelTransferVO.getFromUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getFromUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "updateUserBalance", messageOutCountCheck, 0, args, p_forwardPath);
            }

            final String messageInCountCheck = ChannelTransferBL.checkTransferINCounts(p_con, p_channelTransferVO.getToUserID(), p_channelTransferVO.getReceiverTxnProfile(),
                p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageInCountCheck != null) {
                String arg = p_channelTransferVO.getToUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getToUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "updateUserBalance", messageInCountCheck, 0, args, p_forwardPath);
            }
        }
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        final ChannelUserTxnDAO channelUserTxnDAO = new ChannelUserTxnDAO();
       final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
        
       final UserBalancesVO userBalancesVO = constructBalanceVOFromTxnVO(p_channelTransferVO);
        userBalancesVO.setUserID(p_channelTransferVO.getFromUserID());
        userBalancesDAO.updateUserDailyBalances(p_con, p_curDate, userBalancesVO);
        userBalancesVO.setUserID(p_channelTransferVO.getToUserID());
        userBalancesDAO.updateUserDailyBalances(p_con, p_curDate, userBalancesVO);

        
        // debit the sender balances
        if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,p_channelTransferVO.getNetworkCode()) && p_fromWEB && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_channelTransferVO.getTransferSubType()))
        {
        	ChannelTransferBL.increaseOptOTFCounts(p_con, p_channelTransferVO);
        }
        updateCount = channelUserDAO.debitUserBalancesForMultipleWallet(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);

        // For Mali --- +ve commision ,,,, debit the networkStock for commision
        // qty
        final boolean debit = true;
        if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
            ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(p_con, p_channelTransferVO, p_channelTransferVO.getFromUserID(), p_curDate, debit);
            ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(p_con, p_channelTransferVO, p_channelTransferVO.getFromUserID(), p_curDate);
        }
        // credit the receiver
        if (userProductMultipleWallet) {
            updateCount = channelUserDAO.creditUserBalancesForMultipleWallet(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);
        } else {
            updateCount = channelUserDAO.creditUserBalances(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);
        }
        p_channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_C2C);
        
        if (p_isOutSideHierarchy && sepOutsideTxnCtrl) {
            updateCount = ChannelTransferBL.updateChannelToChannelTransferOutSideCounts(p_con, p_channelTransferVO, p_curDate, p_fromWEB, p_forwardPath);
        } else {
            updateCount = ChannelTransferBL.updateChannelToChannelTransferCounts(p_con, p_channelTransferVO, p_curDate, p_fromWEB, p_forwardPath);
        }
        
        if (p_fromWEB) {
            // commented as disscussed with Sanjay Sir, GSB, AC need not to be
            // updated in WEB

       } else {
            updateCount = channelUserTxnDAO.updateUserPhoneAfterTxn(p_con, p_channelTransferVO, p_channelTransferVO.getToUserCode(), p_channelTransferVO.getToUserID(), false);
        }
        if (lmsAppl && updateCount == 1 && p_fromWEB) {
            try {
                if (p_channelTransferVO.getStatus().equals("CLOSE")) {
                    final LoyaltyBL _loyaltyBL = new LoyaltyBL();
                    final LoyaltyVO loyaltyVO = new LoyaltyVO();
                    
                    final LoyaltyDAO _loyaltyDAO = new LoyaltyDAO();
                    final ArrayList<String> arr = new ArrayList<String>();
                    loyaltyVO.setModuleType(PretupsI.C2C_MODULE);
                    loyaltyVO.setServiceType(PretupsI.C2C_MODULE);
                    loyaltyVO.setTransferamt(p_channelTransferVO.getRequestedQuantity());
                    loyaltyVO.setCategory(p_channelTransferVO.getCategoryCode());
                    loyaltyVO.setFromuserId(p_channelTransferVO.getFromUserID());
                    loyaltyVO.setTouserId(p_channelTransferVO.getToUserID());
                    loyaltyVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
                    loyaltyVO.setTxnId(p_channelTransferVO.getTransferID());
                    loyaltyVO.setCreatedOn(p_channelTransferVO.getCreatedOn());
                    loyaltyVO.setSenderMsisdn(p_channelTransferVO.getFromUserCode());
                    loyaltyVO.setReciverMsisdn(p_channelTransferVO.getToUserCode());
                    loyaltyVO.setProductCode(p_channelTransferVO.getProductCode());
                    arr.add(loyaltyVO.getFromuserId());
                    arr.add(loyaltyVO.getTouserId());
                    PromotionDetailsVO promotionDetailsVO = _loyaltyDAO.loadSetIdByUserId(p_con, arr);
                    loyaltyVO.setSetId(promotionDetailsVO.get_setId());
                    loyaltyVO.setToSetId(promotionDetailsVO.get_toSetId());

                    if (loyaltyVO.getSetId() == null && loyaltyVO.getToSetId() == null) {
                        _log.error("process", "Exception during LMS Module.SetId not found");
                    } else {
                        _loyaltyBL.distributeLoyaltyPoints(PretupsI.C2C_MODULE, p_channelTransferVO.getTransferID(), loyaltyVO);
                    }
                }
            } catch (Exception ex) {
                _log.error("process", "Exception durign LMS Module " + ex.getMessage());
                _log.errorTrace(METHOD_NAME, ex);
            }

        }
        try {
            if (updateCount == 1 && "CLOSE".equals(p_channelTransferVO.getStatus()) && p_fromWEB) {
                boolean statusChangeRequired = false;
                int updatecount1 = 0;
                int updatecount2 = 0;
                if (!PretupsI.USER_STATUS_ACTIVE.equals(p_channelTransferVO.getFromChannelUserStatus())) {
                    // int updatecount=operatorUtili.changeUserStatusToActive(
                    // p_con,p_channelTransferVO.getFromUserID(),p_channelTransferVO.getFromChannelUserStatus());

                    final String str[] = txnSenderUserStatusChang.split(","); // "CH:Y,EX:Y".split(",");
                    String newStatus[] = null;
                    int strs=str.length;
                    for (int i = 0; i <strs ; i++) {
                        newStatus = str[i].split(":");
                        if (newStatus[0].equals(p_channelTransferVO.getFromChannelUserStatus())) {
                            statusChangeRequired = true;
                            updatecount1 = operatorUtili.changeUserStatusToActive(p_con, p_channelTransferVO.getFromUserID(), p_channelTransferVO.getFromChannelUserStatus(),
                                newStatus[1]);
                            break;
                        }
                    }

                }

                if (!PretupsI.USER_STATUS_ACTIVE.equals(p_channelTransferVO.getToChannelUserStatus())) {
                    // int updatecount=operatorUtili.changeUserStatusToActive(
                    // p_con,p_channelTransferVO.getToUserID(),p_channelTransferVO.getToChannelUserStatus());

                    final String str[] = txnReceiverUserStatusChang.split(","); // "CH:Y,EX:Y".split(",");
                    String newStatus[] = null;
                    int st=str.length;
                    for (int i = 0; i <st ; i++) {
                        newStatus = str[i].split(":");
                        if (newStatus[0].equals(p_channelTransferVO.getToChannelUserStatus())) {
                            statusChangeRequired = true;
                            updatecount2 = operatorUtili.changeUserStatusToActive(p_con, p_channelTransferVO.getToUserID(), p_channelTransferVO.getToChannelUserStatus(),
                                newStatus[1]);
                            break;
                        }
                    }

                }
                if (statusChangeRequired && (updatecount1 > 1 || updatecount2 > 1)) {
                    updateCount = 0;
                }
                
            	if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,p_channelTransferVO.getNetworkCode()))
        		{
                	if(p_channelTransferVO.isTargetAchieved())
        			{
        				TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
        				/*ChannelUserVO channelUserVO = new ChannelUserVO();
        				channelUserVO.setUserID(p_channelTransferVO.getToUserID());
        				tbcm.loadBaseCommissionProfileDetailsForTargetMessages(p_con,channelUserVO);*/
        				tbcm.loadBaseCommissionProfileDetailsForTargetMessages(p_con,p_channelTransferVO.getToUserID(),p_channelTransferVO.getMessageArgumentList());
        			}
        		} 
            }
        
        }
        
        catch (Exception e) {
        	
            _log.errorTrace("updateUserBalance", e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception durign status update to active ");
        	loggerValue.append(e.getMessage());
            _log.error("updateUserBalance",  loggerValue );
        }
        
        OneLineTXNLog.log(p_channelTransferVO, null);
        if (_log.isDebugEnabled()) {
        	 loggerValue.setLength(0);
         	loggerValue.append("Exit updateCount =");
         	loggerValue.append(updateCount);
            _log.debug("updateUserBalance",  loggerValue);
        }

        return updateCount;
    }
    public static int updateChannelToChannelTransfer(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean p_isOutSideHierarchy, boolean p_fromWEB, String p_forwardPath, Date p_curDate) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (_log.isDebugEnabled()) {
        	
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_channelTransferVO: ");
        	loggerValue.append(p_channelTransferVO);
        	loggerValue.append("p_isOutSideHirearchy: ");
        	loggerValue.append(p_isOutSideHierarchy);
        	loggerValue.append(" fromWeb :");
        	loggerValue.append(p_fromWEB);
        	loggerValue.append(" p_forwardPath ");
        	loggerValue.append(p_forwardPath);
            _log.debug("updateChannelToChannelTransfer",loggerValue );
        }
        final String METHOD_NAME = "updateChannelToChannelTransfer";
        boolean sepOutsideTxnCtrl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SEP_OUTSIDE_TXN_CTRL);
        boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        String txnSenderUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG));
        String txnReceiverUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG));
        String maxApprovalLevelC2C = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_APPROVAL_LEVEL_C2C);
        int updateCount = 0;

        /*
         * check the user transfer counts
         * Here if Txn is outSide hierarchy then counters are different
         * else counters are different
         */
        if (p_isOutSideHierarchy && sepOutsideTxnCtrl) {

            /*
             * checks the outside out counts and if there is a error message
             * returned then show the error message
             * other wise checks the outside in coutns if there is a error
             * message returned then show the
             * error message
             */
            final String messageOutsideOutCountCheck = ChannelTransferBL.checkOutsideTransferOutCounts(p_con, p_channelTransferVO.getFromUserID(), p_channelTransferVO
                .getSenderTxnProfile(), p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageOutsideOutCountCheck != null) {
                String arg = p_channelTransferVO.getFromUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getFromUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "approveChannelToChannelTransfer", messageOutsideOutCountCheck, 0, args, p_forwardPath);
            }

            final String messageOutsideInCountCheck = ChannelTransferBL.checkOutsideTransferINCounts(p_con, p_channelTransferVO.getToUserID(), p_channelTransferVO
                .getReceiverTxnProfile(), p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageOutsideInCountCheck != null) {
                String arg = p_channelTransferVO.getToUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getToUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "approveChannelToChannelTransfer", messageOutsideInCountCheck, 0, args, p_forwardPath);
            }
        } else {
            /*
             * checks the out counts and if there is a error message returned
             * then show the error message
             * other wise checks the in coutns if there is a error message
             * returned then show the
             * error message
             */
            final String messageOutCountCheck = ChannelTransferBL.checkTransferOutCounts(p_con, p_channelTransferVO.getFromUserID(),
                p_channelTransferVO.getSenderTxnProfile(), p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageOutCountCheck != null) {
                String arg = p_channelTransferVO.getFromUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getFromUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "approveChannelToChannelTransfer", messageOutCountCheck, 0, args, p_forwardPath);
            }

            final String messageInCountCheck = ChannelTransferBL.checkTransferINCounts(p_con, p_channelTransferVO.getToUserID(), p_channelTransferVO.getReceiverTxnProfile(),
                p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageInCountCheck != null) {
                String arg = p_channelTransferVO.getToUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getToUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "approveChannelToChannelTransfer", messageInCountCheck, 0, args, p_forwardPath);
            }
        }

        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        final ChannelUserTxnDAO channelUserTxnDAO = new ChannelUserTxnDAO();
        if(PretupsI.CACHE_ALL.equals(maxApprovalLevelC2C))
        {
       final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
        
       final UserBalancesVO userBalancesVO = constructBalanceVOFromTxnVO(p_channelTransferVO);
        userBalancesVO.setUserID(p_channelTransferVO.getFromUserID());
        userBalancesDAO.updateUserDailyBalances(p_con, p_curDate, userBalancesVO);
        userBalancesVO.setUserID(p_channelTransferVO.getToUserID());
        userBalancesDAO.updateUserDailyBalances(p_con, p_curDate, userBalancesVO);

        
        // debit the sender balances
        if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,p_channelTransferVO.getNetworkCode()) && p_fromWEB && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_channelTransferVO.getTransferSubType()))
        {
        	ChannelTransferBL.increaseOptOTFCounts(p_con, p_channelTransferVO);
        }
        updateCount = channelUserDAO.debitUserBalancesForMultipleWallet(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);

        // For Mali --- +ve commision ,,,, debit the networkStock for commision
        // qty
        final boolean debit = true;
        if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
            ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(p_con, p_channelTransferVO, p_channelTransferVO.getFromUserID(), p_curDate, debit);
            ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(p_con, p_channelTransferVO, p_channelTransferVO.getFromUserID(), p_curDate);
        }
        // credit the receiver
        if (userProductMultipleWallet) {
            updateCount = channelUserDAO.creditUserBalancesForMultipleWallet(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);
        } else {
            updateCount = channelUserDAO.creditUserBalances(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);
        }
        p_channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_C2C);
        
        if (p_isOutSideHierarchy && sepOutsideTxnCtrl) {
            updateCount = ChannelTransferBL.updateChannelToChannelTransferOutSideCounts(p_con, p_channelTransferVO, p_curDate, p_fromWEB, p_forwardPath);
        } else {
            updateCount = ChannelTransferBL.updateChannelToChannelTransferCounts(p_con, p_channelTransferVO, p_curDate, p_fromWEB, p_forwardPath);
        }
        }
        if (p_fromWEB) {
            // commented as disscussed with Sanjay Sir, GSB, AC need not to be
            // updated in WEB

       } else {
            updateCount = channelUserTxnDAO.updateUserPhoneAfterTxn(p_con, p_channelTransferVO, p_channelTransferVO.getToUserCode(), p_channelTransferVO.getToUserID(), false);
        }
        
        // insert the TXN data in the parent and child tables.
        final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
        

     		   updateCount = channelTransferDAO.addChannelTransfer(p_con, p_channelTransferVO);
     		   
        if (lmsAppl && updateCount == 1 && p_fromWEB) {
            try {
                if (p_channelTransferVO.getStatus().equals("CLOSE")) {
                    final LoyaltyBL _loyaltyBL = new LoyaltyBL();
                    final LoyaltyVO loyaltyVO = new LoyaltyVO();
                    
                    final LoyaltyDAO _loyaltyDAO = new LoyaltyDAO();
                    final ArrayList<String> arr = new ArrayList<String>();
                    loyaltyVO.setModuleType(PretupsI.C2C_MODULE);
                    loyaltyVO.setServiceType(PretupsI.C2C_MODULE);
                    loyaltyVO.setTransferamt(p_channelTransferVO.getRequestedQuantity());
                    loyaltyVO.setCategory(p_channelTransferVO.getCategoryCode());
                    loyaltyVO.setFromuserId(p_channelTransferVO.getFromUserID());
                    loyaltyVO.setTouserId(p_channelTransferVO.getToUserID());
                    loyaltyVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
                    loyaltyVO.setTxnId(p_channelTransferVO.getTransferID());
                    loyaltyVO.setCreatedOn(p_channelTransferVO.getCreatedOn());
                    loyaltyVO.setSenderMsisdn(p_channelTransferVO.getFromUserCode());
                    loyaltyVO.setReciverMsisdn(p_channelTransferVO.getToUserCode());
                    loyaltyVO.setProductCode(p_channelTransferVO.getProductCode());
                    arr.add(loyaltyVO.getFromuserId());
                    arr.add(loyaltyVO.getTouserId());
                    PromotionDetailsVO promotionDetailsVO = _loyaltyDAO.loadSetIdByUserId(p_con, arr);
                    loyaltyVO.setSetId(promotionDetailsVO.get_setId());
                    loyaltyVO.setToSetId(promotionDetailsVO.get_toSetId());

                    if (loyaltyVO.getSetId() == null && loyaltyVO.getToSetId() == null) {
                        _log.error("process", "Exception during LMS Module.SetId not found");
                    } else {
                        _loyaltyBL.distributeLoyaltyPoints(PretupsI.C2C_MODULE, p_channelTransferVO.getTransferID(), loyaltyVO);
                    }
                }
            } catch (Exception ex) {
                _log.error("process", "Exception durign LMS Module " + ex.getMessage());
                _log.errorTrace(METHOD_NAME, ex);
            }

        }
        try {
            if (updateCount == 1 && "CLOSE".equals(p_channelTransferVO.getStatus()) && p_fromWEB) {
                boolean statusChangeRequired = false;
                int updatecount1 = 0;
                int updatecount2 = 0;
                if (!PretupsI.USER_STATUS_ACTIVE.equals(p_channelTransferVO.getFromChannelUserStatus())) {
                    // int updatecount=operatorUtili.changeUserStatusToActive(
                    // p_con,p_channelTransferVO.getFromUserID(),p_channelTransferVO.getFromChannelUserStatus());

                    final String str[] = txnSenderUserStatusChang.split(","); // "CH:Y,EX:Y".split(",");
                    String newStatus[] = null;
                    int strs=str.length;
                    for (int i = 0; i <strs ; i++) {
                        newStatus = str[i].split(":");
                        if (newStatus[0].equals(p_channelTransferVO.getFromChannelUserStatus())) {
                            statusChangeRequired = true;
                            updatecount1 = operatorUtili.changeUserStatusToActive(p_con, p_channelTransferVO.getFromUserID(), p_channelTransferVO.getFromChannelUserStatus(),
                                newStatus[1]);
                            break;
                        }
                    }

                }

                if (!PretupsI.USER_STATUS_ACTIVE.equals(p_channelTransferVO.getToChannelUserStatus())) {
                    // int updatecount=operatorUtili.changeUserStatusToActive(
                    // p_con,p_channelTransferVO.getToUserID(),p_channelTransferVO.getToChannelUserStatus());

                    final String str[] = txnReceiverUserStatusChang.split(","); // "CH:Y,EX:Y".split(",");
                    String newStatus[] = null;
                    int st=str.length;
                    for (int i = 0; i <st ; i++) {
                        newStatus = str[i].split(":");
                        if (newStatus[0].equals(p_channelTransferVO.getToChannelUserStatus())) {
                            statusChangeRequired = true;
                            updatecount2 = operatorUtili.changeUserStatusToActive(p_con, p_channelTransferVO.getToUserID(), p_channelTransferVO.getToChannelUserStatus(),
                                newStatus[1]);
                            break;
                        }
                    }

                }
                if (statusChangeRequired && (updatecount1 > 1 || updatecount2 > 1)) {
                    updateCount = 0;
                }
                
            	if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,p_channelTransferVO.getNetworkCode()))
        		{
                	if(p_channelTransferVO.isTargetAchieved())
        			{
        				TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
        				/*ChannelUserVO channelUserVO = new ChannelUserVO();
        				channelUserVO.setUserID(p_channelTransferVO.getToUserID());
        				tbcm.loadBaseCommissionProfileDetailsForTargetMessages(p_con,channelUserVO);*/
        				tbcm.loadBaseCommissionProfileDetailsForTargetMessages(p_con,p_channelTransferVO.getToUserID(),p_channelTransferVO.getMessageArgumentList());
        			}
        		} 
            }
        } catch (Exception e) {
        	
            _log.errorTrace("approveChannelToChannelTransfer", e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception durign status update to active ");
        	loggerValue.append(e.getMessage());
            _log.error("approveChannelToChannelTransfer",  loggerValue );
        }
        OneLineTXNLog.log(p_channelTransferVO, null);
        if (_log.isDebugEnabled()) {
        	 loggerValue.setLength(0);
         	loggerValue.append("Exit updateCount =");
         	loggerValue.append(updateCount);
            _log.debug("approveChannelToChannelTransfer",  loggerValue);
        }

        return updateCount;
    }
    
    public static int approveChannelToChannelTransferInitiate(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean p_isOutSideHierarchy, boolean p_fromWEB, String p_forwardPath, Date p_curDate) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (_log.isDebugEnabled()) {
        	
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_channelTransferVO: ");
        	loggerValue.append(p_channelTransferVO);
        	loggerValue.append("p_isOutSideHirearchy: ");
        	loggerValue.append(p_isOutSideHierarchy);
        	loggerValue.append(" fromWeb :");
        	loggerValue.append(p_fromWEB);
        	loggerValue.append(" p_forwardPath ");
        	loggerValue.append(p_forwardPath);
            _log.debug("approveChannelToChannelTransferInitiate",loggerValue );
        }
        final String methodName = "approveChannelToChannelTransferInitiate";
        boolean sepOutsideTxnCtrl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SEP_OUTSIDE_TXN_CTRL);
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        String txnSenderUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG));
        String txnReceiverUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG));
        
        int updateCount = 0;

        /*
         * check the user transfer counts
         * Here if Txn is outSide hierarchy then counters are different
         * else counters are different
         */
        if (p_isOutSideHierarchy && sepOutsideTxnCtrl) {

            /*
             * checks the outside out counts and if there is a error message
             * returned then show the error message
             * other wise checks the outside in coutns if there is a error
             * message returned then show the
             * error message
             */
            final String messageOutsideOutCountCheck = ChannelTransferBL.checkOutsideTransferOutCounts(p_con, p_channelTransferVO.getFromUserID(), p_channelTransferVO
                .getSenderTxnProfile(), p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageOutsideOutCountCheck != null) {
                String arg = p_channelTransferVO.getFromUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getFromUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "approveChannelToChannelTransferInitiate", messageOutsideOutCountCheck, 0, args, p_forwardPath);
            }

            final String messageOutsideInCountCheck = ChannelTransferBL.checkOutsideTransferINCounts(p_con, p_channelTransferVO.getToUserID(), p_channelTransferVO
                .getReceiverTxnProfile(), p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageOutsideInCountCheck != null) {
                String arg = p_channelTransferVO.getToUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getToUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "approveChannelToChannelTransferInitiate", messageOutsideInCountCheck, 0, args, p_forwardPath);
            }
        } else {
            /*
             * checks the out counts and if there is a error message returned
             * then show the error message
             * other wise checks the in coutns if there is a error message
             * returned then show the
             * error message
             */
            final String messageOutCountCheck = ChannelTransferBL.checkTransferOutCounts(p_con, p_channelTransferVO.getFromUserID(),
                p_channelTransferVO.getSenderTxnProfile(), p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageOutCountCheck != null) {
                String arg = p_channelTransferVO.getFromUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getFromUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "approveChannelToChannelTransferInitiate", messageOutCountCheck, 0, args, p_forwardPath);
            }

            final String messageInCountCheck = ChannelTransferBL.checkTransferINCounts(p_con, p_channelTransferVO.getToUserID(), p_channelTransferVO.getReceiverTxnProfile(),
                p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageInCountCheck != null) {
                String arg = p_channelTransferVO.getToUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getToUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "approveChannelToChannelTransferInitiate", messageInCountCheck, 0, args, p_forwardPath);
            }
        }

            ChannelTransferBL.genrateChnnlToChnnlTrfID(p_channelTransferVO);
            
            // insert the TXN data in the parent and child tables.
            final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
            updateCount = channelTransferDAO.addChannelTransfer(p_con, p_channelTransferVO);
            if (lmsAppl && updateCount == 1 && p_fromWEB) {
                try {
                    if (p_channelTransferVO.getStatus().equals("CLOSE")) {
                        final LoyaltyBL _loyaltyBL = new LoyaltyBL();
                        final LoyaltyVO loyaltyVO = new LoyaltyVO();
                        
                        final LoyaltyDAO _loyaltyDAO = new LoyaltyDAO();
                        final ArrayList<String> arr = new ArrayList<String>();
                        loyaltyVO.setModuleType(PretupsI.C2C_MODULE);
                        loyaltyVO.setServiceType(PretupsI.C2C_MODULE);
                        loyaltyVO.setTransferamt(p_channelTransferVO.getRequestedQuantity());
                        loyaltyVO.setCategory(p_channelTransferVO.getCategoryCode());
                        loyaltyVO.setFromuserId(p_channelTransferVO.getFromUserID());
                        loyaltyVO.setTouserId(p_channelTransferVO.getToUserID());
                        loyaltyVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
                        loyaltyVO.setTxnId(p_channelTransferVO.getTransferID());
                        loyaltyVO.setCreatedOn(p_channelTransferVO.getCreatedOn());
                        loyaltyVO.setSenderMsisdn(p_channelTransferVO.getFromUserCode());
                        loyaltyVO.setReciverMsisdn(p_channelTransferVO.getToUserCode());
                        loyaltyVO.setProductCode(p_channelTransferVO.getProductCode());
                        arr.add(loyaltyVO.getFromuserId());
                        arr.add(loyaltyVO.getTouserId());
                        PromotionDetailsVO promotionDetailsVO = _loyaltyDAO.loadSetIdByUserId(p_con, arr);
                        loyaltyVO.setSetId(promotionDetailsVO.get_setId());
                        loyaltyVO.setToSetId(promotionDetailsVO.get_toSetId());

                        if (loyaltyVO.getSetId() == null && loyaltyVO.getToSetId() == null) {
                            _log.error("process", "Exception during LMS Module.SetId not found");
                        } else {
                            _loyaltyBL.distributeLoyaltyPoints(PretupsI.C2C_MODULE, p_channelTransferVO.getTransferID(), loyaltyVO);
                        }
                    }
                } catch (Exception ex) {
                    _log.error("process", "Exception durign LMS Module " + ex.getMessage());
                    _log.errorTrace(methodName, ex);
                }

            }
            try {
                if (updateCount == 1 && "CLOSE".equals(p_channelTransferVO.getStatus()) && p_fromWEB) {
                    boolean statusChangeRequired = false;
                    int updatecount1 = 0;
                    int updatecount2 = 0;
                    if (!PretupsI.USER_STATUS_ACTIVE.equals(p_channelTransferVO.getFromChannelUserStatus())) {
                        // int updatecount=operatorUtili.changeUserStatusToActive(
                        // p_con,p_channelTransferVO.getFromUserID(),p_channelTransferVO.getFromChannelUserStatus());

                        final String str[] = txnSenderUserStatusChang.split(","); // "CH:Y,EX:Y".split(",");
                        String newStatus[] = null;
                        int strs=str.length;
                        for (int i = 0; i <strs ; i++) {
                            newStatus = str[i].split(":");
                            if (newStatus[0].equals(p_channelTransferVO.getFromChannelUserStatus())) {
                                statusChangeRequired = true;
                                updatecount1 = operatorUtili.changeUserStatusToActive(p_con, p_channelTransferVO.getFromUserID(), p_channelTransferVO.getFromChannelUserStatus(),
                                    newStatus[1]);
                                break;
                            }
                        }

                    }

                    if (!PretupsI.USER_STATUS_ACTIVE.equals(p_channelTransferVO.getToChannelUserStatus())) {
                        // int updatecount=operatorUtili.changeUserStatusToActive(
                        // p_con,p_channelTransferVO.getToUserID(),p_channelTransferVO.getToChannelUserStatus());

                        final String str[] = txnReceiverUserStatusChang.split(","); // "CH:Y,EX:Y".split(",");
                        String newStatus[] = null;
                        int st=str.length;
                        for (int i = 0; i <st ; i++) {
                            newStatus = str[i].split(":");
                            if (newStatus[0].equals(p_channelTransferVO.getToChannelUserStatus())) {
                                statusChangeRequired = true;
                                updatecount2 = operatorUtili.changeUserStatusToActive(p_con, p_channelTransferVO.getToUserID(), p_channelTransferVO.getToChannelUserStatus(),
                                    newStatus[1]);
                                break;
                            }
                        }

                    }
                    if (statusChangeRequired && (updatecount1 > 1 || updatecount2 > 1)) {
                        updateCount = 0;
                    }
                    
                	if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,p_channelTransferVO.getNetworkCode()))
            		{
                    	if(p_channelTransferVO.isTargetAchieved())
            			{
            				TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
            				/*ChannelUserVO channelUserVO = new ChannelUserVO();
            				channelUserVO.setUserID(p_channelTransferVO.getToUserID());
            				tbcm.loadBaseCommissionProfileDetailsForTargetMessages(p_con,channelUserVO);*/
            				tbcm.loadBaseCommissionProfileDetailsForTargetMessages(p_con,p_channelTransferVO.getToUserID(),p_channelTransferVO.getMessageArgumentList());
            			}
            		} 
                }
        } catch (Exception e) {
        	
            _log.errorTrace("approveChannelToChannelTransferInitiate", e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception durign status update to active ");
        	loggerValue.append(e.getMessage());
            _log.error("approveChannelToChannelTransferInitiate",  loggerValue );
        }
        OneLineTXNLog.log(p_channelTransferVO, null);
        if (_log.isDebugEnabled()) {
        	 loggerValue.setLength(0);
         	loggerValue.append("Exit updateCount =");
         	loggerValue.append(updateCount);
            _log.debug("approveChannelToChannelTransferInitiate",  loggerValue);
        }

        return updateCount;
    }

    /**
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param p_isOutSideHierarchy
     * @param p_fromWEB
     * @param p_forwardPath
     * @param p_curDate
     * @return int
     * @throws BTSLBaseException
     */
    public static int withdrawAndReturnChannelToChannel(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean p_isOutSideHierarchy, boolean p_fromWEB, String p_forwardPath, Date p_curDate) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
    		loggerValue.append("Entered p_channelTransferVO: ");
    		loggerValue.append(p_channelTransferVO);
    		loggerValue.append("p_isOutSideHirearchy: ");
    		loggerValue.append(p_isOutSideHierarchy);
    		loggerValue.append(" fromWeb :");
    		loggerValue.append(p_fromWEB);
    		loggerValue.append(" p_forwardPath " );
    		loggerValue.append(p_forwardPath);
            _log.debug("withdrawAndReturnChannelToChannel",loggerValue );
        }

    	boolean sepOutsideTxnCtrl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SEP_OUTSIDE_TXN_CTRL);
        boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
        String txnSenderUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG));
        String txnReceiverUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG));
        
        int updateCount = 0;

        /*
         * generate the TXN ID for the txn as RETURN/WITHDRAW
         */

        if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_channelTransferVO.getTransferSubType())) {
            ChannelTransferBL.genrateChnnlToChnnlWithdrawID(p_channelTransferVO);
        } else {
            ChannelTransferBL.genrateChnnlToChnnlReturnID(p_channelTransferVO);
        }

        /*
         * Now update user balances in all the updation method we are checking
         * for the update count
         * if it is lessthan or equal to 0 then throw exception so no need to
         * check here
         * first update daily balances of both of the user
         * then debit the Sender user
         * then credit the receiver user
         * and then update the thresholds of the users
         * at last insert the TXN data in the parent and child tables.
         */
        final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();

        final UserBalancesVO userBalancesVO = constructBalanceVOFromTxnVO(p_channelTransferVO);
        userBalancesVO.setUserID(p_channelTransferVO.getFromUserID());
        userBalancesDAO.updateUserDailyBalances(p_con, p_curDate, userBalancesVO);
        userBalancesVO.setUserID(p_channelTransferVO.getToUserID());
        userBalancesDAO.updateUserDailyBalances(p_con, p_curDate, userBalancesVO);

        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        final ChannelUserTxnDAO channelUserTxnDAO = new ChannelUserTxnDAO();
        // debit the sender balances
        if (userProductMultipleWallet) {
            updateCount = channelUserDAO.debitUserBalancesForMultipleWallet(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);
            updateCount = channelUserDAO.creditUserBalancesForMultipleWallet(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);
        } else {
            updateCount = channelUserDAO.debitUserBalances(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);
            updateCount = channelUserDAO.creditUserBalances(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);
        }
        // credit the receiver

        if (p_isOutSideHierarchy && sepOutsideTxnCtrl) {
            updateCount = ChannelTransferBL.updateChannelToChannelTransferOutSideCounts(p_con, p_channelTransferVO, p_curDate, p_fromWEB, p_forwardPath);
        } else {
            updateCount = ChannelTransferBL.updateChannelToChannelTransferCounts(p_con, p_channelTransferVO, p_curDate, p_fromWEB, p_forwardPath);
        }
        // insert the TXN data in the parent and child tables.
        OneLineTXNLog.log(p_channelTransferVO, null);
        final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
        updateCount = channelTransferDAO.addChannelTransfer(p_con, p_channelTransferVO);

        if (p_fromWEB) {
            // commented as disscussed with Sanjay Sir, GSB, AC need not to be
            // updated in WEB
            // updateCount=channelUserDAO.updateUserPhoneAfterTxn(p_con,p_channelTransferVO,p_channelTransferVO.getFromUserCode(),p_channelTransferVO.getFromUserID(),true);
            // updateCount=channelUserDAO.updateUserPhoneAfterTxn(p_con,p_channelTransferVO,p_channelTransferVO.getToUserCode(),p_channelTransferVO.getToUserID(),true);
        } else {
            if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_channelTransferVO.getTransferSubType())) {
                updateCount = channelUserTxnDAO.updateUserPhoneAfterTxn(p_con, p_channelTransferVO, p_channelTransferVO.getFromUserCode(),
                    p_channelTransferVO.getFromUserID(), false);
            } else {
                updateCount = channelUserTxnDAO.updateUserPhoneAfterTxn(p_con, p_channelTransferVO, p_channelTransferVO.getToUserCode(), p_channelTransferVO.getToUserID(),
                    false);
            }
        }

        try {
            if (updateCount == 1 && "CLOSE".equals(p_channelTransferVO.getStatus()) && p_fromWEB) {
                if (!PretupsI.USER_STATUS_ACTIVE.equals(p_channelTransferVO.getFromChannelUserStatus())) {
                    // int updatecount=operatorUtili.changeUserStatusToActive(
                    // p_con,p_channelTransferVO.getFromUserID(),p_channelTransferVO.getFromChannelUserStatus());
                    int updatecount = 0;
                    final String str[] = txnSenderUserStatusChang.split(","); // "CH:Y,EX:Y".split(",");
                    String newStatus[] = null;
                    for (int i = 0; i < str.length; i++) {
                        newStatus = str[i].split(":");
                        if (newStatus[0].equals(p_channelTransferVO.getFromChannelUserStatus())) {
                            updatecount = operatorUtili.changeUserStatusToActive(p_con, p_channelTransferVO.getFromUserID(), p_channelTransferVO.getFromChannelUserStatus(),
                                newStatus[1]);
                            break;
                        }
                    }
                    if (updatecount < 1) {
                        updateCount = 0;
                    }
                }

                if (updateCount == 1 && (!PretupsI.USER_STATUS_ACTIVE.equals(p_channelTransferVO.getToChannelUserStatus()))) {
                    // int updatecount=operatorUtili.changeUserStatusToActive(
                    // p_con,p_channelTransferVO.getToUserID(),p_channelTransferVO.getToChannelUserStatus());
                    int updatecount = 0;
                    final String str[] = txnReceiverUserStatusChang.split(","); // "CH:Y,EX:Y".split(",");
                    String newStatus[] = null;
                    for (int i = 0; i < str.length; i++) {
                        newStatus = str[i].split(":");
                        if (newStatus[0].equals(p_channelTransferVO.getToChannelUserStatus())) {
                            updatecount = operatorUtili.changeUserStatusToActive(p_con, p_channelTransferVO.getToUserID(), p_channelTransferVO.getToChannelUserStatus(),
                                newStatus[1]);
                            break;
                        }
                    }
                    if (updatecount < 1) {
                        updateCount = 0;
                    }
                }
            }
        } catch (Exception e) {
            _log.errorTrace("withdrawAndReturnChannelToChannel", e);
            loggerValue.setLength(0);
    		loggerValue.append("Exception during status update to active ");
    		loggerValue.append(e.getMessage());
            _log.error("withdrawAndReturnChannelToChannel",  loggerValue );
        }

        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
    		loggerValue.append("Exited updateCount: ");
    		loggerValue.append(updateCount);
            _log.debug("withdrawAndReturnChannelToChannel", loggerValue );
        }

        return updateCount;
    }

    /**
     * Method constructBalanceVOFromTxnVO.
     * 
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @return UserBalancesVO
     */
    private static UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("constructBalanceVOFromTxnVO", "Entered:NetworkStockTxnVO=>" + p_channelTransferVO);
        }
        final UserBalancesVO userBalancesVO = new UserBalancesVO();
        userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
        userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
        userBalancesVO.setLastTransferOn(p_channelTransferVO.getTransferDate());
        if (_log.isDebugEnabled()) {
            _log.debug("constructBalanceVOFromTxnVO", "Exiting userBalancesVO=" + userBalancesVO);
        }
        return userBalancesVO;
    }

}
