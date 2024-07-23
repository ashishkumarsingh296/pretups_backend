package com.btsl.pretups.channel.logging;

/*
 * @(#)BalanceLogger.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 05/09/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for logging all the balance related Logs for channel user
 */

import java.util.Iterator;
import java.util.List;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.user.businesslogic.wallet.UserProductWalletMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLDateUtil;

public class BalanceLogger {

    private static final Log _log = LogFactory.getFactory().getInstance(BalanceLogger.class.getName());

    /**
	 * ensures no instantiation
	 */
    private BalanceLogger(){
    	
    }
    
    /**
     * @param userBalancesVO
     */
    public static void log(UserBalancesVO userBalancesVO) {
        final String methodName = "BalanceLogger.log";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered...");
        }
        final StringBuilder strBuff = new StringBuilder();
        try {
            strBuff.append(" [Transfer ID:" ).append( userBalancesVO.getLastTransferID()).append( "]");
            strBuff.append(" [User ID:" ).append( userBalancesVO.getUserID() ).append( "]");
            strBuff.append(" [Network ID:" ).append( userBalancesVO.getNetworkCode()).append( "]");
            strBuff.append(" [Network For:" ).append( userBalancesVO.getNetworkFor()).append( "]");
            strBuff.append(" [Product Code:" ).append( userBalancesVO.getProductCode()).append( "]");
            strBuff.append(" [Requested Qty:" ).append( userBalancesVO.getRequestedQuantity()).append( "]");
            strBuff.append(" [Transfer Qty:" ).append( userBalancesVO.getQuantityToBeUpdated()).append( "]");
            strBuff.append(" [Previous Bal:" ).append( userBalancesVO.getPreviousBalance()).append( "]");
            strBuff.append(" [Post Bal:" ).append( userBalancesVO.getBalance()).append( "]");
            strBuff.append(" [Net Amount:" ).append( userBalancesVO.getNetAmount()).append( "]");
            strBuff.append(" [Entry Type:" ).append( userBalancesVO.getEntryType()).append( "]");
            strBuff.append(" [Type:" ).append( userBalancesVO.getType()).append( "]");
            strBuff.append(" [Transfer Category:" ).append( userBalancesVO.getTransferCategory()).append( "]");
            strBuff.append(" [Transfer Type:" ).append( userBalancesVO.getLastTransferType()).append( "]");
            strBuff.append(" [Source:" ).append( userBalancesVO.getSource()).append( "]");
            strBuff.append(" [Transfer On:" ).append(BTSLDateUtil.getSystemLocaleDate(userBalancesVO.getLastTransferOn(), true)).append( "]");
            strBuff.append(" [Transfer By:" ).append( userBalancesVO.getCreatedBy()).append( "]");
            // To add subscriber number on 11/02/2008
            strBuff.append(" [User MSISDN:" ).append( userBalancesVO.getUserMSISDN()).append( "]");
            strBuff.append(" [Other Info:" ).append( userBalancesVO.getOtherInfo()).append( "]");
            /**
             * @author birendra.mishra
             *         To Add Additional Field in the Balance Logger based upon
             *         the System Preference of Multiple Wallet Setting.
             */
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                final StringBuilder addWalletDetails = getDebitedWalletDetails(userBalancesVO);
                strBuff.append(addWalletDetails);
            } else {
                strBuff.append(" [Wallets Debited : [" ).append( ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)) ).append( " : " ).append( userBalancesVO.getRequestedQuantity()).append( " ]");
            }
            _log.info("", strBuff.toString());

          
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append(" Exception :");
            loggerValue.append( e.getMessage());
            _log.error(methodName, userBalancesVO.getLastTransferID(), loggerValue);
            StringBuilder handleHandler= new StringBuilder(); 
            handleHandler.append("Not able to log info for Transfer ID:");
            handleHandler.append(userBalancesVO.getLastTransferID());
            handleHandler.append(" and User ID:");
            handleHandler.append(userBalancesVO.getUserID());
            handleHandler.append(" ,getting Exception=");
            handleHandler.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BalanceLogger[" + methodName + "]", userBalancesVO
             .getLastTransferID(), "", "",handleHandler.toString() );
        }
    }

    /**
     * @author birendra.mishra
     *         This Method Iterated over the PDA wallets and picks the
     *         walletCode and debitedBalance of each wallet which participated
     *         in the debit of recharge amount,
     *         to append the same in the BalanceLogger log.
     * @author birendra.mishra
     * @param userBalancesVO
     * @return
     */
    private static StringBuilder getDebitedWalletDetails(UserBalancesVO userBalancesVO) {
        final String methodName = "getDebitedWalletDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered... ");
        }

        UserProductWalletMappingVO walletVO = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" [Wallets Debited : ");
        final List<UserProductWalletMappingVO> pdaWalletList = userBalancesVO.getPdaWalletList();

        for (final Iterator<UserProductWalletMappingVO> iterator = pdaWalletList.iterator(); iterator.hasNext();) {
            walletVO = iterator.next();
            if (walletVO.getDebitBalance() > 0) {
                strBuff.append(" [" ).append( walletVO.getAccountCode() ).append( " : " ).append( walletVO.getDebitBalance() ).append( " ]");
            }
        }
        strBuff.append(" ]");

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting... ");
        }
        return strBuff;
    }
}
