/**
 * @(#)ChannelReturnAction.java
 *                              Copyright(c) 2005, Bharti Telesoft Ltd.
 *                              All Rights Reserved
 * 
 *                              <description>
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              avinash.kamthan Aug 16, 2005 Initital Creation
 *                              Sandeep Goel Nov 10,2005
 *                              Customization,Modification
 *                              Sandeep Goel Aug 03,2005 Modification ID OWR001
 *                              Ankit Zindal Nov 20,2006 ChangeID=LOCALEMASTER
 *                              Babu Kunwar Feb 22,2011 User Event Remarks
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 * 
 */

package com.btsl.pretups.channel.userreturn.web;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*//import org.apache.struts.action.ActionForm;
//import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;*/

import com.btsl.common.BTSLBaseException;
//import com.btsl.common.BTSLDispatchAction;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.security.csrf.CSRFTokenUtil;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserLoanVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;

/**
 * @author avinash.kamthan
 * 
 */
public class ChannelReturnAction  {

	private static final Log LOG = LogFactory.getLog(ChannelReturnAction.class.getName());



    /**
     * Populate the transfer items vo from product list.
     * 
     * @param p_transferItemsVO
     * @param p_userBalancesVO
     * @param p_theForm
     * @throws BTSLBaseException
     */
    private void populateTransferItemsVO(ChannelTransferItemsVO p_transferItemsVO, ChannelTransferItemsVO p_userBalancesVO, ChannelReturnForm p_theForm) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("populateTransferItemsVO",
                "Entered p_transferItemsVO " + p_transferItemsVO + " p_userBalancesVO " + p_userBalancesVO + " ChannelReturnForm " + p_theForm);
        }

        p_transferItemsVO.setProductType(p_theForm.getProductType());
        p_transferItemsVO.setProductCode(p_userBalancesVO.getProductCode());
        p_transferItemsVO.setShortName(p_userBalancesVO.getShortName());
        p_transferItemsVO.setProductName(p_userBalancesVO.getShortName());
        p_transferItemsVO.setCommProfileDetailID(p_theForm.getCommissionProfileID());
        p_transferItemsVO.setRequestedQuantity(p_userBalancesVO.getRequestedQuantity());
        p_transferItemsVO.setProductShortCode(p_userBalancesVO.getProductShortCode());
        p_transferItemsVO.setRequiredQuantity(PretupsBL.getSystemAmount(p_userBalancesVO.getRequestedQuantity()));
        p_transferItemsVO.setUnitValue(p_userBalancesVO.getUnitValue());
        p_transferItemsVO.setBalance(p_userBalancesVO.getBalance());

        if (LOG.isDebugEnabled()) {
            LOG.debug("searchTransferUserPopup", "Exiting p_transferItemsVO " + p_transferItemsVO);
        }
    }

    /**
     * Construct the Channel transfer itema vo from form
     * 
     * @param form
     * @param p_channelTransferVO
     * @throws Exception
     */
   /* private void constructVOFromForm(ActionForm form, ChannelTransferVO p_channelTransferVO, Date p_curDate) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("constructVOFromForm", "Entered p_curDate=" + p_curDate + ", p_channelTransferVO = " + p_channelTransferVO.toString());
        }

        final ChannelReturnForm theForm = (ChannelReturnForm) form;
        final long totTax1 = PretupsBL.getSystemAmount(theForm.getTotalTax1());
        final long totTax2 = PretupsBL.getSystemAmount(theForm.getTotalTax2());
        final long totTax3 = PretupsBL.getSystemAmount(theForm.getTotalTax3());
        final long totRequestedQty = PretupsBL.getSystemAmount(theForm.getTotalReqQty());
        final long payableAmount = PretupsBL.getSystemAmount(theForm.getTotalPayableAmount());
        final long netPayableAmt = PretupsBL.getSystemAmount(theForm.getTotalNetPayableAmount());
        final long totTransferedAmt = PretupsBL.getSystemAmount(theForm.getTotalMRP());
        
        if (theForm.getReturnFlag()) {
            p_channelTransferVO.setFromUserID(theForm.getChannelCategoryUserID());
            p_channelTransferVO.setFromUserName(theForm.getChannelCategoryUserName());
            p_channelTransferVO.setToUserID(PretupsI.OPERATOR_TYPE_OPT);
            p_channelTransferVO.setReceiverCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
            p_channelTransferVO.setCategoryCode(theForm.getCategoryCode());
            p_channelTransferVO.setSenderGradeCode(theForm.getGradeCode());
            p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN);
            p_channelTransferVO.setSenderTxnProfile(theForm.getTxnProfileID());
            p_channelTransferVO.setGraphicalDomainCode(theForm.getGeoDomainCode());
            p_channelTransferVO.setDomainCode(theForm.getChannelDomain());
            p_channelTransferVO.setReceiverTxnProfile(null);
        } else {
            if (theForm.getFromUserCodeFlag()) {
                // Sender
                p_channelTransferVO.setFromUserID(theForm.getChannelCategoryUserID());
                p_channelTransferVO.setCategoryCode(theForm.getCategoryCodeForUserCode());
                p_channelTransferVO.setSenderGradeCode(theForm.getGradeCode());
                // OPT
                p_channelTransferVO.setToUserID(PretupsI.OPERATOR_TYPE_OPT);
                p_channelTransferVO.setReceiverCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
                p_channelTransferVO.setToUserName(theForm.getChannelCategoryUserName());// not
                // inserted
                // in
                // DB
                theForm.setChannelDomain(theForm.getChannelDomainForUser());
                theForm.setProductType(theForm.getProductTypeWithUserCode());
                p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
                p_channelTransferVO.setSenderTxnProfile(theForm.getTxnProfileID());
                p_channelTransferVO.setReceiverTxnProfile(null);
                p_channelTransferVO.setGraphicalDomainCode(theForm.getGeoDomainCodeForUserCode());
                p_channelTransferVO.setDomainCode(theForm.getChannelDomainForUser());
            } else {
                String catg = null;
                if (theForm.getCategoryCode() != null && theForm.getCategoryCode().indexOf(":") > 0) {
                    catg = theForm.getCategoryCode().substring(theForm.getCategoryCode().indexOf(":") + 1);
                }
                p_channelTransferVO.setFromUserID(theForm.getChannelCategoryUserID());
                p_channelTransferVO.setCategoryCode(catg);
                p_channelTransferVO.setSenderGradeCode(theForm.getGradeCode());
                p_channelTransferVO.setToUserID(PretupsI.OPERATOR_TYPE_OPT);
                p_channelTransferVO.setReceiverCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
                p_channelTransferVO.setToUserName(theForm.getChannelCategoryUserName());// not
                // inserted
                // in
                // DB
                p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
                p_channelTransferVO.setSenderTxnProfile(theForm.getTxnProfileID());
                p_channelTransferVO.setReceiverTxnProfile(null);
                p_channelTransferVO.setGraphicalDomainCode(theForm.getGeoDomainCodeForUserCode());
                p_channelTransferVO.setDomainCode(theForm.getChannelDomain());
            }
        }

        p_channelTransferVO.setCommProfileSetId(theForm.getCommissionProfileID());
        p_channelTransferVO.setChannelRemarks(theForm.getRemarks());
        p_channelTransferVO.setNetworkCodeFor(theForm.getNetworkCode());
        p_channelTransferVO.setNetworkCode(theForm.getNetworkCode());
        p_channelTransferVO.setTransferDate(p_curDate);
        p_channelTransferVO.setCommProfileVersion(theForm.getCommissionProfileVersionID());
        p_channelTransferVO.setDualCommissionType(theForm.getDualCommissionType());
        p_channelTransferVO.setCreatedOn(p_curDate);
        p_channelTransferVO.setModifiedOn(p_curDate);
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
        p_channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
        p_channelTransferVO.setTotalTax1(totTax1);
        p_channelTransferVO.setTotalTax2(totTax2);
        p_channelTransferVO.setTotalTax3(totTax3);
        p_channelTransferVO.setRequestedQuantity(totRequestedQty);
        p_channelTransferVO.setPayableAmount(payableAmount);
        p_channelTransferVO.setNetPayableAmount(netPayableAmt);
        p_channelTransferVO.setChannelTransferitemsVOList(theForm.getReturnedProductList()); // arraylist
        p_channelTransferVO.setTransferMRP(totTransferedAmt);
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);// TBD
        p_channelTransferVO.setProductType(theForm.getProductType());
        p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
        p_channelTransferVO.setTransferCategory(theForm.getTransferCategory());
        p_channelTransferVO.setControlTransfer(PretupsI.YES);

        // adding the some additional information for sender/reciever
        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_channelTransferVO.getGraphicalDomainCode());
        p_channelTransferVO.setReceiverDomainCode(p_channelTransferVO.getDomainCode());
        // p_channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(p_receiverVO.getUserCode()));
        p_channelTransferVO.setFromUserCode(PretupsBL.getFilteredMSISDN(theForm.getUserCode()));
        // +ve Commision Apply
        p_channelTransferVO.setSenderDrQty(PretupsBL.getSystemAmount(theForm.getSenderDebitQty()));
        p_channelTransferVO.setReceiverCrQty(PretupsBL.getSystemAmount(theForm.getReceiverCreditQty()));
        p_channelTransferVO.setCommQty(PretupsBL.getSystemAmount(theForm.getNetCommQty()));
        p_channelTransferVO.setWalletType(theForm.getWalletType());
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("constructVOFromForm", "Exiting p_channelTransferVO=" + p_channelTransferVO);
        }
    }
*/
    /**
     * method control all method call for returned
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param p_userId
     * @param p_date
     * @param p_forwardPath
     * @throws BTSLBaseException
     */
    public void orderReturnedProcessStart(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userId, Date p_date, String p_forwardPath) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("orderReturnedProcessStart",
                "Entered p_channelTransferVO  " + p_channelTransferVO + " p_userId " + p_userId + " p_date " + p_date + " p_forwardPath: " + p_forwardPath);
        }
        final boolean credit = false;
        // prepare networkStockList credit the network stock
        ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, p_userId, p_date, credit);
        ChannelTransferBL.updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, p_userId, p_date);

        // update user daily balances
        final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
        userBalancesDAO.updateUserDailyBalances(p_con, p_date, constructBalanceVOFromTxnVO(p_channelTransferVO));

        // channel debit the user balances
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
            channelUserDAO.debitUserBalancesForMultipleWallet(p_con, p_channelTransferVO, true, p_forwardPath);
        } else {
            channelUserDAO.debitUserBalances(p_con, p_channelTransferVO, true, p_forwardPath);
        }
        ChannelTransferBL.updateOptToChannelUserInCounts(p_con, p_channelTransferVO, p_forwardPath, p_date);
        if (LOG.isDebugEnabled()) {
            LOG.debug("orderReturnedProcessStart", "Exiting");
        }
    }

    /**
     * Method constructBalanceVOFromTxnVO.
     * 
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @return UserBalancesVO
     */
    private UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("constructBalanceVOFromTxnVO", "Entered:NetworkStockTxnVO=>" + p_channelTransferVO);
        }
        final UserBalancesVO userBalancesVO = UserBalancesVO.getInstance();
        userBalancesVO.setUserID(p_channelTransferVO.getFromUserID());
        userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
        userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
        userBalancesVO.setLastTransferOn(p_channelTransferVO.getModifiedOn());
        // Added to log user MSISDN on 13/02/2008
        userBalancesVO.setUserMSISDN(p_channelTransferVO.getFromUserCode());
        if (LOG.isDebugEnabled()) {
            LOG.debug("constructBalanceVOFromTxnVO", "Exiting userBalancesVO=" + userBalancesVO);
        }
        return userBalancesVO;
    }

}
