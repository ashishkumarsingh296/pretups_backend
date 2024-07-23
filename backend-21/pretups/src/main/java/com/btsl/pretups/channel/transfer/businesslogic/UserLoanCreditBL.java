package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.channel.logging.UserLoanRequestProcessLogger;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileProductsVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserLoanDAO;
import com.btsl.user.businesslogic.UserLoanVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.txn.pretups.channel.profile.businesslogic.CommissionProfileTxnDAO;

public class UserLoanCreditBL {

    private static Log _log = LogFactory.getLog(UserLoanCreditBL.class.getName());

    public void userLoanCredit(ArrayList userLoanist, String userID, long balance, long prevBal,String productCode,String productType) throws BTSLBaseException {
        final String methodName = "userLoanCredit";
        if (_log.isDebugEnabled()) {
            _log.info(methodName, " Enter ChannelTransferVO: " +userLoanist);
        }
        try {
            final ArrayList vo1 = new ArrayList();
            vo1.addAll(userLoanist);
            final UserLoanCreditThread mrt = new UserLoanCreditThread(vo1, userID, balance, prevBal,productCode,productType);
            final Thread t = new Thread(mrt);
            t.start();
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.info(methodName, " End of Main Thread... ");
            }
        }
    }


}


class UserLoanCreditThread implements Runnable {
    private static Log _log = LogFactory.getLog(UserLoanCreditThread.class.getName());
    private ArrayList vo = null;
    private String userID = null;
    private long balance = 0;
    private long prevBalance = 0;
    private String productCode= null;
    private String productType = null;
    public long userBalance=0L;

   

    public UserLoanCreditThread(ArrayList vo1, String userID, long balance, long prevBal,String productCode,String productType) {
        this.vo = vo1;
        this.userID = userID;
        this.balance = balance;
        this.prevBalance = prevBal;
        this.productCode= productCode;
        this.productType = productType;
        
    }

    public void run() {
        final String methodName = "run";
        if (_log.isDebugEnabled()) {
            _log.info(methodName, " Enter vo: " + vo);
        }
       final   String className ="UserLoanCreditThread";
       Connection con = null;
       MComConnectionI mcomCon = null;
     
        try {
            //Thread.sleep(500);
        	UserLoanVO userLoanVO = null;
            PushMessage push=null;
            String senderMessage=null;
           ChannelTransferVO p_channelTransferVO = null;
            UserTransferCountsVO countVO = null;
            TransferProfileVO profileVO = null;
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            long Stock = 0L;
            ChannelTransferItemsVO channelTransferItemsVO = null;
            ChannelUserVO receiverUserVO = null;
            UserPhoneVO userPhoneVO = null;
     	
     	 
            for(int i=0;i< vo.size();i++){
            	userLoanVO = (UserLoanVO)vo.get(i);
             
            	 if (_log.isDebugEnabled()) {
                     _log.info(methodName, " userID: " + userID+",balance="+balance+",prevBalance="+prevBalance,"userLoanVO="+userLoanVO);
                 }
            	if (userLoanVO != null && userID.equals(userLoanVO.getUser_id()) && userLoanVO.getLoan_amount() >0 && balance <= userLoanVO.getLoan_threhold() && prevBalance >= userLoanVO.getLoan_threhold() && PretupsI.NO.equals(userLoanVO.getLoan_given()) &&  PretupsI.YES.equals(userLoanVO.getOptinout_allowed()))
            	{
            		
            		 if (_log.isDebugEnabled()) {
                         _log.info(methodName, "  userID: " + userID+",prevBalance="+prevBalance,"userLoanVO="+userLoanVO);
                     }
            		   final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
                       final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
                       final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
                       final TransferProfileDAO transferProfileDAO = new TransferProfileDAO();
                       final NetworkStockDAO networkStockDAO = new NetworkStockDAO();
                       NetworkVO networkVO = new NetworkVO();
                       final NetworkDAO networkDAO = new NetworkDAO();
                       ArrayList<ListValueVO> stocklist = null;
                       Date currentDate = new Date();
                       ListValueVO stockVO = new ListValueVO();
                       

                    try {

                       final String toUserID = userLoanVO.getUser_id();
                      
                       receiverUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, toUserID, false, currentDate,false);
                         userPhoneVO = new UserPhoneVO();
						userPhoneVO = channelUserDAO.loadUserPhoneDetails(con, receiverUserVO.getUserID());

						final boolean isUserCategoryAllow = ((Boolean) PreferenceCache
								.getControlPreference(
										PreferenceI.CAT_USERWISE_LOAN_ENABLE,
										receiverUserVO.getNetworkID(),
										receiverUserVO.getCategoryCode())).booleanValue();
						
						   _log.debug("process", "isUserCategoryAllow: " + isUserCategoryAllow);
                           
						
						if(!isUserCategoryAllow){
                        	PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.USER_LOAN_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
							pushMessage.push();
                            if (_log.isDebugEnabled()) {
                                _log.debug("process", "User Loan is not possible as Category is not allowed for loan: " + receiverUserVO.getCategoryName());
                            }
                            continue;
                        }
                                        
                        // check user status
                         if (!PretupsI.USER_STATUS_ACTIVE.equals(receiverUserVO.getStatus())) {
                        	PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.USER_LOAN_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
							pushMessage.push();
                            if (_log.isDebugEnabled()) {
                                _log.debug("process", "User Loan is not possible as Receiver is not active: " + receiverUserVO.getUserID());
                            }
                            continue;
                        }



                        // check user's transfer profile status.
                        else if (!PretupsI.YES.equals(receiverUserVO.getTransferProfileStatus())) {
                        	PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.USER_LOAN_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
							pushMessage.push();
                            if (_log.isDebugEnabled()) {
                                _log.debug("process", "User loan is not possible as Receiver's transfer profile is not active : " + receiverUserVO.getUserID());
                            }
                            continue;
                        }

                        // checking receiver's IN suspend
                        if (receiverUserVO.getInSuspend().equalsIgnoreCase(PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND)) {
                            if (_log.isDebugEnabled()) {
                                _log.debug(
                                    "process",
                                    "User loan is not possible as Receiver is IN suspend, receiver user ID: " + receiverUserVO.getUserID() + " receiver's IN suspend: " + receiverUserVO
                                        .getInSuspend());
                            }
                            continue;
                        }

                        countVO = userTransferCountsDAO.loadTransferCounts(con, receiverUserVO.getUserID(), false);
                        profileVO = transferProfileDAO.loadTransferProfileThroughProfileID(con, receiverUserVO.getTransferProfileID(), receiverUserVO.getNetworkID(),
                            receiverUserVO.getCategoryCode(), true);
                        if(countVO!=null) {
                        	
                        	if ((countVO.getDailyInCount() >= profileVO.getDailyInCount()) || (countVO.getDailyInValue() >= profileVO.getDailyInValue())) {
                        		PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.USER_LOAN_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
                        		pushMessage.push();
                        		if (_log.isDebugEnabled()) {
                        			_log.debug(
                        					"process",
                        					"User loan is not possible as receiver's Daily IN count or IN amount is greater than the max IN count and IN amount,receiver user id :" + receiverUserVO.getUserID());
                        		}
                        		continue;
                        	} else if ((countVO.getWeeklyInCount() >= profileVO.getWeeklyInCount()) || (countVO.getWeeklyInValue() >= profileVO.getWeeklyInValue())) {
                        		PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.USER_LOAN_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
                        		pushMessage.push();
                        		if (_log.isDebugEnabled()) {
                        			_log.debug(
                        					"process",
                        					"User loan is not possible as receiver's Weekly IN count or IN amount is greater than the max IN count and IN amount,receiver user id :" + receiverUserVO.getUserID());
                        		}
                        		continue;
                        	} else if ((countVO.getMonthlyInCount() >= profileVO.getMonthlyInCount()) || (countVO.getMonthlyInValue() >= profileVO.getMonthlyInValue())) {
                        		PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.USER_LOAN_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
                        		pushMessage.push();
                        		if (_log.isDebugEnabled()) {
                        			_log.debug(
                        					"process",
                        					"User loan is not possible as receiver's Monthly IN count or IN amount is greater than the max IN count and IN amount,receiver user id :" + receiverUserVO.getUserID());
                        		}
                        		continue;
                        	}
                        }

                        // load transfer rule
                        final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, receiverUserVO.getNetworkID(), receiverUserVO.getDomainID(),
                            PretupsI.CATEGORY_TYPE_OPT, receiverUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);

                        if (channelTransferRuleVO == null) {
							PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.USER_LOAN_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
							pushMessage.push();
                            if (_log.isDebugEnabled()) {
                                _log.debug("process", "User loan is not possible as receiver's trasfer rule does not exists:" + receiverUserVO.getUserID());
                            }
                         
                            continue;

                        } else if (PretupsI.NO.equals(channelTransferRuleVO.getTransferAllowed())) {
            				PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.USER_LOAN_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
							pushMessage.push();
                            if (_log.isDebugEnabled()) {
                                _log.debug("process", "User loan is not possible as receiver's trasfer does not allowed: " + receiverUserVO.getUserID());
                            }
                            continue;

                        }

                        else if (channelTransferRuleVO.getProductVOList() == null || channelTransferRuleVO.getProductVOList().size() == 0) {
                    		PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.USER_LOAN_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
							pushMessage.push();
                        	if (_log.isDebugEnabled()) {
                                _log.debug("process", "User loan is not possible as no product is assign for transfer rule: " + receiverUserVO.getUserID());
                            }
                            continue;

                        }

                        // if network is less than User loan amount
                        networkVO = networkDAO.loadNetwork(con, receiverUserVO.getNetworkID());
                        stocklist = networkStockDAO.loadStockOfProduct(con, networkVO.getNetworkCode(), networkVO.getNetworkCode(),productType );
                        if (stocklist.size() > 0) {
                            stockVO = stocklist.get(0);
                            Stock = Long.parseLong(stockVO.getValue());
                            if (Stock <= userLoanVO.getLoan_amount()) {
                                if (_log.isDebugEnabled()) {
                                    _log.debug("process", "User loan is not possible as netwok stock is not sufficient" + receiverUserVO.getUserID());
                                }
                               continue;
                            }
                        } else {
                            throw new BTSLBaseException("UserLoanCreditBL", "process", "netwok product" + stocklist.size());
                        }

                        // do the User loan transfer
                        final Date p_currentDate = new Date();
                        ArrayList<ChannelTransferItemsVO> list = this.loadO2CXfrProductList(con, productType, networkVO.getNetworkCode(), receiverUserVO
                        		.getCommissionProfileSetID(), p_currentDate,userLoanVO.getLoan_amount());
                        if (list.size() == 0) {
                            throw new BTSLBaseException("UserLoanCreditBL", "process", "commission profile products" + list.size());
                        }

                        /*
                         * Now further filter the list with the transfer
                         * rules list and the above list
                         * of commission profile products.
                         */
                        list = filterProductWithTransferRule(list, channelTransferRuleVO.getProductVOList(),String.valueOf(userLoanVO.getLoan_amount()));
                        if (list.size() == 0) {
                            throw new BTSLBaseException("UserLoanCreditBL", "process", "no product of commission match with transfer rule products" + list.size());
                        }
                        final ArrayList<ChannelTransferItemsVO> itemsList = new ArrayList<ChannelTransferItemsVO>();
                        for (int p = 0, k = list.size(); p < k; p++) {
                            channelTransferItemsVO = list.get(p);
                            if (!BTSLUtil.isNullString(channelTransferItemsVO.getRequestedQuantity())) {
                            	channelTransferItemsVO.setAfterTransSenderPreviousStock(Long.parseLong(stockVO.getValue()));
                            	channelTransferItemsVO.setAfterTransReceiverPreviousStock(balance);
                            	channelTransferItemsVO.setSenderDebitQty(userLoanVO.getLoan_amount());
                				channelTransferItemsVO.setReceiverCreditQty(userLoanVO.getLoan_amount());
                				channelTransferItemsVO.setProductTotalMRP(userLoanVO.getLoan_amount());
                				channelTransferItemsVO.setPayableAmount(userLoanVO.getLoan_amount());
                				channelTransferItemsVO.setNetPayableAmount(userLoanVO.getLoan_amount());
                				channelTransferItemsVO.setApprovedQuantity(userLoanVO.getLoan_amount());
                                itemsList.add(channelTransferItemsVO);
                            }
                        }
                        // load the tax and commission of the products
                        // according to the user (receiver) commission
                        // profile.
                        // make a new channel TransferVO to transfer
                        // into the method during tax calculataion
                        final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
                        channelTransferVO.setChannelTransferitemsVOList(itemsList);
                        channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                        channelTransferVO.setNetworkCode(receiverUserVO.getNetworkCode());
                        channelTransferVO.setCommProfileSetId(receiverUserVO.getCommissionProfileSetID());
                        channelTransferVO.setCommProfileVersion(receiverUserVO.getCommissionProfileSetVersion());
                        channelTransferVO.setToUserID(receiverUserVO.getUserID());
                        /* final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
                        commissionProfileDAO.loadProductListWithTaxes(con, receiverUserVO.getCommissionProfileSetID(), receiverUserVO
                            .getCommissionProfileSetVersion(), itemsList);

                        KeyArgumentVO argumentVO = null;
                        final ArrayList<KeyArgumentVO> errorList = new ArrayList<KeyArgumentVO>();
                        for (int m = 0, k = itemsList.size(); m < k; m++) {

                            channelTransferItemsVO = itemsList.get(m);

                            if (!channelTransferItemsVO.isSlabDefine()) {
                                argumentVO = new KeyArgumentVO();
                                argumentVO.setKey(PretupsErrorCodesI.ERROR_COMMISSION_SLAB_NOT_DEFINE_SUBKEY);
                                argumentVO.setArguments(new String[] { channelTransferItemsVO.getShortName(), channelTransferItemsVO.getRequestedQuantity() });
                                errorList.add(argumentVO);
                            }
                        }// if slab is not defined Show error
                        if (!errorList.isEmpty()) {
                        	AutoO2CLogger.log("process :User loan is not possible as amount is not defined in Commission Slab" );
                            throw new BTSLBaseException(ChannelTransferBL.class.getName(), "loadAndCalculateTaxOnProducts",
                                PretupsErrorCodesI.ERROR_COMMISSION_SLAB_NOT_DEFINE, errorList);
                        }
                        final ChannelTransferBL channelTransferBL = new ChannelTransferBL();
                        channelTransferVO.setOtfFlag(true);
                    	if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()))
                		{
                			ChannelTransferBL.increaseOptOTFCounts(con, channelTransferVO);
                		}*/
                        //channelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO, PretupsI.CHANNEL_TYPE_O2C);
                        long totTax1 = 0, totTax2 = 0, totTax3 = 0, totRequestedQty = 0, payableAmount = 0, netPayableAmt = 0, totTransferedAmt = 0, totalMRP = 0, totcommission = 0;

                        long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
                        ChannelTransferItemsVO transferItemsVO = null;
                        for (int l = 0, k = itemsList.size(); l < k; l++) {
                            transferItemsVO = itemsList.get(l);

                           // totTax1 += transferItemsVO.getTax1Value();
                           // totTax2 += transferItemsVO.getTax2Value();
                           // totTax3 += transferItemsVO.getTax3Value();
                           // totcommission += transferItemsVO.getCommValue();
                            if (_log.isDebugEnabled()) {
                                _log.debug("process", "Total Commission = " + totcommission);
                            }
                            if (transferItemsVO.getRequestedQuantity() != null && BTSLUtil.isDecimalValue(transferItemsVO.getRequestedQuantity())) {
                                totRequestedQty += PretupsBL.getSystemAmount(transferItemsVO.getRequestedQuantity());
                                    totTransferedAmt += (Double.parseDouble(transferItemsVO.getRequestedQuantity()) );
                                
                            }
                            payableAmount += transferItemsVO.getPayableAmount();
                            netPayableAmt += transferItemsVO.getNetPayableAmount();
                            totalMRP += transferItemsVO.getProductTotalMRP();
                            //commissionQty += transferItemsVO.getCommQuantity();
                            if (_log.isDebugEnabled()) {
                                _log.debug("process", "Commission Quantity= " + commissionQty);
                            }
                            senderDebitQty += transferItemsVO.getSenderDebitQty();
                            receiverCreditQty += transferItemsVO.getReceiverCreditQty();
                        }
                        // create vo for transfer
                        final UserVO userVO = channelUserDAO.loadOptUserForO2C(con,receiverUserVO.getNetworkID() );
                        if (userVO != null) {
                            p_channelTransferVO = new ChannelTransferVO();
                            p_channelTransferVO.setNetworkCode(receiverUserVO.getNetworkID());
                            p_channelTransferVO.setReceiverTxnProfile(receiverUserVO.getTransferProfileID());
                            p_channelTransferVO.setReceiverTxnProfileName(receiverUserVO.getTransferProfileName());
                            p_channelTransferVO.setTotalTax1(totTax1);
                            p_channelTransferVO.setTotalTax2(totTax2);

                            p_channelTransferVO.setTotalTax3(totTax3);
                        
                            p_channelTransferVO.setRequestedQuantity(userLoanVO.getLoan_amount());
                            p_channelTransferVO.setPayableAmount(payableAmount);
                            p_channelTransferVO.setNetPayableAmount(netPayableAmt);
                            p_channelTransferVO.setPayInstrumentAmt(netPayableAmt);
                            p_channelTransferVO.setTransferMRP(totTransferedAmt);
                            p_channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
                            p_channelTransferVO.setToUserID(toUserID);
                            p_channelTransferVO.setToUserName(receiverUserVO.getUserName());
                            p_channelTransferVO.setReceiverGgraphicalDomainCode(receiverUserVO.getGeographicalCode());
                            p_channelTransferVO.setReceiverDomainCode(receiverUserVO.getCategoryCode());
                            p_channelTransferVO.setGraphicalDomainCode(receiverUserVO.getGeographicalCode());
                            p_channelTransferVO.setDomainCode(receiverUserVO.getDomainID());
                            p_channelTransferVO.setReceiverCategoryCode(receiverUserVO.getCategoryCode());
                            p_channelTransferVO.setCommProfileSetId(receiverUserVO.getCommissionProfileSetID());
                            p_channelTransferVO.setNetworkCodeFor(receiverUserVO.getNetworkID());
                            p_channelTransferVO.setCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
                            p_channelTransferVO.setTransferDate(p_currentDate);
                            p_channelTransferVO.setCommProfileVersion(receiverUserVO.getCommissionProfileSetVersion());
                            p_channelTransferVO.setCreatedOn(p_currentDate);
                            p_channelTransferVO.setModifiedOn(p_currentDate);
                            
                            p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                            p_channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
                            // Added for Request_Gateway_Type Non empty
                            p_channelTransferVO.setRequestGatewayType(PretupsI.REQUEST_SOURCE_WEB);

                            p_channelTransferVO.setProductType(productType);
                            p_channelTransferVO.setProductCode(productCode);
                            p_channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
                            p_channelTransferVO.setTransactionMode(PretupsI.USER_LOAN_TXN_MODE);
							p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
							p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                            p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                            p_channelTransferVO.setControlTransfer(PretupsI.YES);
                            p_channelTransferVO.setCommQty(commissionQty);
                            p_channelTransferVO.setSenderDrQty(senderDebitQty);
                            p_channelTransferVO.setReceiverCrQty(receiverCreditQty);
                           ChannelTransferBL.genrateTransferID(p_channelTransferVO);
                            p_channelTransferVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
                            p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                            p_channelTransferVO.setChannelTransferitemsVOList(itemsList);
                            p_channelTransferVO.setCreatedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
                            p_channelTransferVO.setModifiedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
                            p_channelTransferVO.setTransferInitatedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
                    		p_channelTransferVO.setCreatedOn(currentDate);
                            p_channelTransferVO.setModifiedOn(currentDate);
                            p_channelTransferVO.setReceiverDomainCode(receiverUserVO.getDomainID());
                            p_channelTransferVO.setReceiverGradeCode(receiverUserVO.getUserGrade());
							p_channelTransferVO.setRequestGatewayCode(PretupsI.REQUEST_SOURCE_WEB);
							p_channelTransferVO.setToUserCode(receiverUserVO.getMsisdn());
							p_channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_SYSTEM); // as
							p_channelTransferVO.setChannelRemarks(" Loan granted..");
		        			
						    final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
	                         
                            final int count = channelTransferDAO.addChannelTransfer(con, p_channelTransferVO);

                            // For message push and email- 22/8/13
                            UserVO newUserVO = new UserVO();
                            final UserDAO userDAO = new UserDAO();
                            UserVO newUserVO1 = new UserVO();
                            if (receiverUserVO.getParentID().equals(PretupsI.ROOT_PARENT_ID)) {
                                newUserVO1 = this.loaduserMsisdn(con, receiverUserVO.getCreatedBy());
                                newUserVO = userDAO.loadUsersDetails(con, newUserVO1.getMsisdn());
                            } else {
                                newUserVO = userDAO.loadUserDetailsFormUserID(con, receiverUserVO.getOwnerID());
                            }

                            PushMessage pushMessage = null;
                            BTSLMessages sendbtslMessage = null;
                            final Locale localeMsisdn = null;
                            Locale locale = null;
                            UserPhoneVO phoneVO = new UserPhoneVO();
                            phoneVO = channelUserDAO.loadUserPhoneDetails(con, receiverUserVO.getUserID());
                            // end

                            if (count > 0) {
                                final boolean debit = true;
                                String Status = null;

                                Status = channelTransferDAO.getStatusOfDomain(con, receiverUserVO.getDomainID());

                                if (Status.equals("N")) {
                                    throw new BTSLBaseException("user.loan.error.invalidDomain");
                                }

                                ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(con, p_channelTransferVO, toUserID, p_currentDate, debit);
                                ChannelTransferBL.updateNetworkStockTransactionDetails(con, p_channelTransferVO, toUserID, p_currentDate);
                                if (SystemPreferences.POSITIVE_COMM_APPLY) {
                                    ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(con, p_channelTransferVO, toUserID, p_currentDate,
                                        debit);
                                    ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(con, p_channelTransferVO, toUserID, p_currentDate);
                                }
                                // update user daily balances
                                final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
                                int upCount = 0;
                                int crCount = 0;
                                int O2CuserINCount = 0;
                                int userLoanCount= 0;
                                upCount = userBalancesDAO.updateUserDailyBalances(con, currentDate, constructBalanceVOFromTxnVO(p_channelTransferVO));
                                p_channelTransferVO.setTransferDate(currentDate);
                                crCount = creditUserBalances(con, p_channelTransferVO, true);
                                O2CuserINCount = updateOptToChannelUserInCounts(con, p_channelTransferVO, currentDate);

                                final UserLoanDAO userLoanDAO = new UserLoanDAO();
                                userLoanVO.setLoan_given(PretupsI.YES);
                                userLoanVO.setLoan_given_amount(userLoanVO.getLoan_amount());
                                userLoanVO.setBalance_before_loan(balance);
                                userLoanVO.setLast_loan_date(currentDate);
                                userLoanVO.setLast_loan_txn_id(p_channelTransferVO.getTransferID());
                                userLoanVO.setLoan_taken_from(PretupsI.OPERATOR_TYPE_OPT);
                             
                                userLoanCount = userLoanDAO.updateUserLoanCredit(con, userLoanVO, currentDate);
                                
                                if (upCount > 0 && crCount > 0 && O2CuserINCount > 0 && userLoanCount >0) {
                                	mcomCon.finalCommit();
                                 
                                	UserLoanRequestProcessLogger.log(userLoanVO, phoneVO.getMsisdn(), "L");
                                    _log.debug("process", "User loan process has been Executed successfully");
                               
                                    // For message push and email-
                                    // 22/8/13
                                    try {
                                    	
                                        locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
                                        String [] arg= new String [5];
                                        arg[0]=p_channelTransferVO.getTransferID();
                                        arg[1]=p_channelTransferVO.getProductCode();
                                        if (_log.isDebugEnabled()) {
                                            _log.debug("process", "userLoanVO.getLoan_amount() = " + userLoanVO.getLoan_amount());
                                        }
                                        
										long  finalAmount = 0;
									   	finalAmount = userLoanVO.getLoan_amount();                                                
                                   
                                        if (_log.isDebugEnabled()) {
                                            _log.debug("process", "Final Amount= " + finalAmount);
                                        }
                                        arg[2]= PretupsBL.getDisplayAmount(userLoanVO.getLoan_amount());
                                        arg[3]=PretupsBL.getDisplayAmount(userBalance+finalAmount);
                                        Thread.sleep(300);
                                        sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.USER_LOAN_TRASFER_SUCCESS, arg);
                                        pushMessage = new PushMessage(phoneVO.getMsisdn(), sendbtslMessage, "", "", locale, receiverUserVO.getNetworkID(),
                                            null);
                                        pushMessage.push();
                                    } catch (Exception e) {
                                        _log.errorTrace(methodName, e);
                                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR,
                                            className+methodName, "", "", "", "Exception while msg push for user-loan success:" + e.getMessage());
                                    }
                         
                                    

                                } else {
                                	mcomCon.finalRollback();
                                    if (_log.isDebugEnabled()) {
                                        _log.debug("process", "User loan is not possible");
                                    }
                                    // For message push and email-
                                    // 22/8/13
                                    try {
                                        locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
                                        sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.USER_LOAN_TRASFER_FAIL, new String[] { String.valueOf(userLoanVO.getLoan_amount()) });
                                        pushMessage = new PushMessage(phoneVO.getMsisdn(), sendbtslMessage, "", "", locale, receiverUserVO.getNetworkID(),
                                            "SMS will be delivered shortly thankyou");
                                        pushMessage.push();
                                    } catch (Exception e) {
                                        _log.errorTrace(methodName, e);
                                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR,
                                            className+methodName, "", "", "", "Exception while msg push for user-loan failure:" + e.getMessage());
                                    }
                                   
                                }
                            }

                            else {
                            	mcomCon.finalRollback();
                                // For message push & email
                                try {
                                    locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
                                    sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.USER_LOAN_TRASFER_FAIL, new String[] { String.valueOf(userLoanVO.getLoan_amount()) });
                                    pushMessage = new PushMessage(phoneVO.getMsisdn(), sendbtslMessage, "", "", locale, receiverUserVO.getNetworkID(),
                                        "SMS will be delivered shortly thankyou");
                                    pushMessage.push();
                                } catch (Exception e) {
                                    _log.errorTrace(methodName, e);
                                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR,
                                        className+methodName, "", "", "", "Exception while msg push for user-loan failure:" + e.getMessage());
                                }
                    
                                if (_log.isDebugEnabled()) {
                                    _log.debug("process", "User loan is not possible");
                                }
                            }
                        } else {
                            if (_log.isDebugEnabled()) {
                                _log.debug("process", "User loan is not possible no user exists to perform user Loan");
                            }
                        }
                    } catch (BTSLBaseException be) {
                        if (con != null) {
                        	mcomCon.finalRollback();
                        }
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, " Exception in executing record  p_channelTransferVO : " + p_channelTransferVO);
                        }
                        _log.errorTrace(methodName, be);
                        
                        
                    } catch (Exception e) {
                    	 if (con != null) {
                         	mcomCon.finalRollback();
                         }
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, " Exception in executing record  p_channelTransferVO : " + p_channelTransferVO);
                        }
                        _log.errorTrace(methodName, e);
                        
                       

                    }
           		
            		
            	}
            	
            	else if(userLoanVO!= null && userID.equals(userLoanVO.getUser_id()) && PretupsI.YES.equals(userLoanVO.getLoan_given()))
            	{

                if (_log.isDebugEnabled()) {
                        _log.debug(
                            methodName,
                            "Loan is already pending on requester account" + userLoanVO.getUser_id());
                    }
                    continue;
            	}
            	else if (userLoanVO!= null && userID.equals(userLoanVO.getUser_id())&&  PretupsI.NO.equals(userLoanVO.getOptinout_allowed())) {
            		if (_log.isDebugEnabled()) {
                        _log.debug(
                            methodName,
                            "request have not opt in for Loan Process" + userLoanVO.getUser_id());
                    }
                    continue;
            		
            	}
            	else if  (userLoanVO!= null && !userID.equals(userLoanVO.getUser_id())) {
            		if (_log.isDebugEnabled()) {
                        _log.debug(
                            methodName,
                            "Debter User ID  not matches " +userID+","+ userLoanVO.getUser_id());
                    }
                    continue;
            	}
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
        	
        	if (mcomCon != null) {
				mcomCon.close(className+methodName);
				mcomCon = null;
			}
        	
            if (_log.isDebugEnabled()) {
                _log.info(methodName,PretupsI.EXITED);
            }
        }
    }
    
    private ArrayList<ChannelTransferItemsVO> loadO2CXfrProductList(Connection p_con, String p_productType, String p_networkCode, String p_commProfileSetId, Date p_currentDate ,long requestedQty) throws BTSLBaseException {
        final String methodName = "loadO2CXfrProductList";
        if (_log.isDebugEnabled()) {
            _log.debug(
                "loadO2CXfrProductList",
                "Entered   p_productType: " + p_productType + " NetworkCode:" + p_networkCode + " CommissionProfileSetID: " + p_commProfileSetId + " CurrentDate: " + p_currentDate);
        }
        final ArrayList<ChannelTransferItemsVO> productList = new ArrayList<ChannelTransferItemsVO>();

        final NetworkProductDAO networkProductDAO = new NetworkProductDAO();

        // load the product list mapped with the network.
        final ArrayList prodList = networkProductDAO.loadProductListForXfr(p_con, p_productType, p_networkCode);

        // check whether product exist or not of the input productType
        if (prodList.size() == 0) {
            throw new BTSLBaseException("UserLoanCreditBL", "process", "product list size is" + prodList.size());
        }

        // checking that the status of network product mapping is active and
        // also construct the new arrayList of
        // channelTransferItemsVOs containg required list.
        ChannelTransferItemsVO channelTransferItemsVO = null;
        NetworkProductVO networkProductVO = null;
        int i, j, m, n;
        for (i = 0, j = prodList.size(); i < j; i++) {
            networkProductVO = (NetworkProductVO) prodList.get(i);
            if (networkProductVO.getStatus().equals(PretupsI.STATUS_ACTIVE)) {
                channelTransferItemsVO = new ChannelTransferItemsVO();
                channelTransferItemsVO.setProductType(networkProductVO.getProductType());
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
                productList.add(channelTransferItemsVO);
            }
        }
        if (productList.isEmpty()) {
            throw new BTSLBaseException("UserLoanCreditBL", "process", "product list size is" + prodList.size());
        }

        // code is commented as disscussed with Sanjay sir, AC, GSB 03/07/2006
        // as there is no need to check it.
        // load the product's stock
        /*
         * NetworkStockDAO networkStockDAO = new NetworkStockDAO();
         * ArrayList networkStockList =
         * networkStockDAO.loadStockOfProduct(p_con,
         * p_networkCode,p_networkCode,p_productType);
         * if(networkStockList ==null || networkStockList.isEmpty())
         * throw new
         * BTSLBaseException(ChannelTransferBL.class,"loadO2CXfrProductList"
         * ,"message.transfer.nodata.networkstock",0,new
         * String[]{p_productType},p_forwardPath);
         */
        // load the latest version of the commission profile set id
        final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
        final CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();
        String latestCommProfileVersion = null;
        try {
            latestCommProfileVersion = commissionProfileTxnDAO.loadCommProfileSetLatestVersion(p_con, p_commProfileSetId, p_currentDate);
        } catch (BTSLBaseException bex) {
            if (PretupsErrorCodesI.COMM_PROFILE_SETVERNOT_ASSOCIATED.equals(bex.getMessage())) {
                throw new BTSLBaseException("UserLoanCreditBL", "process", "commission profile version error");
            }

            _log.error("loadO2CXfrProductList", "BTSLBaseException " + bex.getMessage());
            throw bex;
        }

        // if there is no commission profile version exist upto the current date
        // show the error message.
        if (BTSLUtil.isNullString(latestCommProfileVersion)) {
            throw new BTSLBaseException("UserLoanCreditBL", "process", "no commission profile version exists" + latestCommProfileVersion);
        }

        // load product list associated with the commission profile and latest
        // version.
        final ArrayList commissionProfileProductList = commissionProfileDAO.loadCommissionProfileProductsList(p_con, p_commProfileSetId, latestCommProfileVersion);

        // if list is empty send the error message
        if (commissionProfileProductList == null || commissionProfileProductList.isEmpty()) {
            throw new BTSLBaseException("UserLoanCreditBL", "process", "commission profile product list size is 0 ");
        }

        // filterize the product list with the products of the commission
        // profile products
        CommissionProfileProductsVO commissionProfileProductsVO = null;
        for (i = 0, j = commissionProfileProductList.size(); i < j; i++) {
            commissionProfileProductsVO = (CommissionProfileProductsVO) commissionProfileProductList.get(i);
            for (m = 0, n = productList.size(); m < n; m++) {
                channelTransferItemsVO = productList.get(m);
                if (channelTransferItemsVO.getProductCode().equals(commissionProfileProductsVO.getProductCode())) {
                    channelTransferItemsVO.setMinTransferValue(commissionProfileProductsVO.getMinTransferValue());
                    channelTransferItemsVO.setMaxTransferValue(commissionProfileProductsVO.getMaxTransferValue());
                    channelTransferItemsVO.setTransferMultipleOf(commissionProfileProductsVO.getTransferMultipleOff());
                    channelTransferItemsVO.setDiscountType(commissionProfileProductsVO.getDiscountType());
                    channelTransferItemsVO.setDiscountRate(commissionProfileProductsVO.getDiscountRate());
                    channelTransferItemsVO.setCommProfileDetailID(commissionProfileProductsVO.getCommProfileProductID());
                    channelTransferItemsVO.setTaxOnChannelTransfer(commissionProfileProductsVO.getTaxOnChannelTransfer());
                    channelTransferItemsVO.setTaxOnFOCTransfer(commissionProfileProductsVO.getTaxOnFOCApplicable());
                    break;
                }
            }
        }
        for (m = 0, n = productList.size(); m < n; m++) {
            channelTransferItemsVO = productList.get(m);
            if (BTSLUtil.isNullString(channelTransferItemsVO.getCommProfileDetailID())) {
                productList.remove(m);
                m--;
                n--;
            }
        }

        if (productList.size() == 0) {
            throw new BTSLBaseException("UserLoanCreditBL", "process", "product list size is" + productList.size() + "for commission profile");
        }

		//added for pvg defect:983 issue 2: Multiple of value defined in commission profile is getting validated
        if((requestedQty % channelTransferItemsVO.getTransferMultipleOf())!=0 )
        {
        	throw new BTSLBaseException("UserLoanCreditBL", "process", "requested quantity is not multiple of  defined in commission profile");
        }
        
        if (_log.isDebugEnabled()) {
            _log.debug("loadO2CXfrProductList", "Exited  " + productList.size());
        }
        return productList;
    }

    /**
     * Filter the product on the bases of transfer rule
     * This method returns the list of products, which are comman in the both of
     * the arrayLists
     * 
     * @param p_productList
     * @param p_productListWithXfrRule
     * @return ArrayList
     */
    private ArrayList<ChannelTransferItemsVO> filterProductWithTransferRule(ArrayList<ChannelTransferItemsVO> p_productList, ArrayList<ListValueVO> p_productListWithXfrRule,String loanAmount) {
        final String methodName = "filterProductWithTransferRule";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_productList: " + p_productList.size() + " p_productListWithXfrRule: " + p_productListWithXfrRule.size());
        }
        ChannelTransferItemsVO channelTransferItemsVO = null;
        ListValueVO listValueVO = null;
        final ArrayList<ChannelTransferItemsVO> tempList = new ArrayList<ChannelTransferItemsVO>();
        for (int m = 0, n = p_productList.size(); m < n; m++) {
            channelTransferItemsVO = p_productList.get(m);
            for (int i = 0, k = p_productListWithXfrRule.size(); i < k; i++) {
                listValueVO = p_productListWithXfrRule.get(i);
                if (channelTransferItemsVO.getProductCode().equals(listValueVO.getValue())) {
                    channelTransferItemsVO.setRequestedQuantity(loanAmount);
                    tempList.add(channelTransferItemsVO);
                    break;
                }
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting tempList: " + tempList.size());
        }

        return tempList;
    }

    /**
     * Method constructBalanceVOFromTxnVO.
     * 
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @return UserBalancesVO
     */
    private UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO) {
        final String methodName = "constructBalanceVOFromTxnVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:NetworkStockTxnVO=>" + p_channelTransferVO);
        }
        final UserBalancesVO userBalancesVO = new UserBalancesVO();
        userBalancesVO.setUserID(p_channelTransferVO.getToUserID());
        userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
        userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
        userBalancesVO.setLastTransferOn(p_channelTransferVO.getModifiedOn());

        userBalancesVO.setUserMSISDN(p_channelTransferVO.getUserMsisdn());
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting userBalancesVO=" + userBalancesVO);
        }
        return userBalancesVO;
    }

    private int updateOptToChannelUserInCounts(Connection p_con, ChannelTransferVO p_channelTransferVO, Date p_curDate) throws BTSLBaseException {
        final String methodName = "updateOptToChannelUserInCounts";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ChannelTransferVO =" + p_channelTransferVO, "p_curDate" + p_curDate);
        }
        int updateCount = 0;

        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
        String userID = null;
        if (p_channelTransferVO.getTransferType().equals(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION)) {
            userID = p_channelTransferVO.getToUserID();
        } else {
            userID = p_channelTransferVO.getFromUserID();
        }

        UserTransferCountsVO countsVO = userTransferCountsDAO.loadTransferCounts(p_con, userID, true);
        boolean flag = true;
        if (countsVO == null) {
            flag = false;
            countsVO = new UserTransferCountsVO();
        }
        checkResetCountersAfterPeriodChange(countsVO, p_curDate);

        if (p_channelTransferVO.getTransferType().equals(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION)) {
        	p_channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_O2C);
        	Map<String, Object> hashmap = ChannelTransferBL.checkSOSstatusAndAmount(p_con, countsVO,
        			p_channelTransferVO);
			if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true)) {
				
				throw new BTSLBaseException("UserLoanCreditBL", methodName, PretupsErrorCodesI.SOS_PENDING_FOR_SETTLEMENT);
			}
			
			if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
				ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
				channelSoSWithdrawBL.autoChannelSoSSettlement(p_channelTransferVO,PretupsI.SOS_REQUEST_TYPE);
			}
			Map<String, Object> lrHashMap = ChannelTransferBL.checkLRstatusAndAmount(p_con, countsVO, p_channelTransferVO);
			if(!lrHashMap.isEmpty()&& lrHashMap.get(PretupsI.DO_WITHDRAW).equals(true)){
				ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
				p_channelTransferVO.setLrWithdrawAmt((long)lrHashMap.get(PretupsI.WITHDRAW_AMOUNT));		
				channelSoSWithdrawBL.autoChannelSoSSettlement(p_channelTransferVO,PretupsI.LR_REQUEST_TYPE);
			}
            final String transferCountsMessage = transferInCountsCheck(p_con, countsVO, p_channelTransferVO.getReceiverTxnProfile(), p_channelTransferVO.getNetworkCode(),
                p_channelTransferVO.getTransferMRP());
            if (transferCountsMessage != null) {

                throw new BTSLBaseException("UserLoanCreditBL", methodName, transferCountsMessage);

            }
        }

        if (p_channelTransferVO.getTransferType().equals(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION)) {
            countsVO.setUserID(p_channelTransferVO.getToUserID());
            countsVO.setDailyInCount(countsVO.getDailyInCount() + 1);
            countsVO.setWeeklyInCount(countsVO.getWeeklyInCount() + 1);
            countsVO.setMonthlyInCount(countsVO.getMonthlyInCount() + 1);
            countsVO.setDailyInValue(countsVO.getDailyInValue() + p_channelTransferVO.getTransferMRP());
            countsVO.setWeeklyInValue(countsVO.getWeeklyInValue() + p_channelTransferVO.getTransferMRP());
            countsVO.setMonthlyInValue(countsVO.getMonthlyInValue() + p_channelTransferVO.getTransferMRP());
            countsVO.setLastInTime(p_curDate);
        } else if (p_channelTransferVO.getTransferType().equals(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN)) {
            // As per SRS
            countsVO.setUserID(p_channelTransferVO.getFromUserID());
            if (countsVO.getDailyInValue() >= p_channelTransferVO.getTransferMRP()) {
                countsVO.setDailyInValue(countsVO.getDailyInValue() - p_channelTransferVO.getTransferMRP());
            }
            if (countsVO.getWeeklyInValue() >= p_channelTransferVO.getTransferMRP()) {
                countsVO.setWeeklyInValue(countsVO.getWeeklyInValue() - p_channelTransferVO.getTransferMRP());
            }
            if (countsVO.getMonthlyInValue() >= p_channelTransferVO.getTransferMRP()) {
                countsVO.setMonthlyInValue(countsVO.getMonthlyInValue() - p_channelTransferVO.getTransferMRP());
            }
            countsVO.setLastInTime(p_curDate);
        }

        countsVO.setLastTransferID(p_channelTransferVO.getTransferID());
        countsVO.setLastTransferDate(p_curDate);
        updateCount = userTransferCountsDAO.updateUserTransferCounts(p_con, countsVO, flag);

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exited  updateCount " + updateCount);
        }

        return updateCount;

    }
    private int creditUserBalances(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean isFromWeb) throws BTSLBaseException {
        final String methodName = "creditUserBalances";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_channelTransferVO : " + p_channelTransferVO + " isFromWeb " + isFromWeb);
        }
        int updateCount = 0;
        PreparedStatement pstmt = null;
        PreparedStatement psmtUpdate = null;
        PreparedStatement psmtInsert = null;
        PreparedStatement psmtInsertUserThreshold = null;

        ResultSet rs = null;
        final StringBuffer strBuffSelect = new StringBuffer();

        strBuffSelect.append(" SELECT ");
        strBuffSelect.append(" balance ");
        strBuffSelect.append(" FROM user_balances ");

       
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            strBuffSelect.append(" WHERE user_id = ? and product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance with RS");
        } else {
            strBuffSelect.append(" WHERE user_id = ? and product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE");
        }
        final StringBuffer strBuffUpdate = new StringBuffer();
        strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
        strBuffUpdate.append(" WHERE ");
        strBuffUpdate.append(" user_id = ? ");
        strBuffUpdate.append(" AND ");
        strBuffUpdate.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");

        final StringBuffer strBuffInsert = new StringBuffer();
        strBuffInsert.append(" INSERT ");
        strBuffInsert.append(" INTO user_balances ");
        strBuffInsert.append(" ( prev_balance,daily_balance_updated_on , balance, last_transfer_type, last_transfer_no, last_transfer_on , ");
        strBuffInsert.append(" user_id, product_code , network_code, network_code_for ) ");
        strBuffInsert.append(" VALUES ");
        strBuffInsert.append(" (?,?,?,?,?,?,?,?,?,?) ");

        final StringBuffer strBuffThresholdInsert = new StringBuffer();
        strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
        strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , ");
        strBuffThresholdInsert.append("  type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
        strBuffThresholdInsert.append(" VALUES ");
        strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

        final String updateQuery = strBuffUpdate.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Update query:" + updateQuery);
        }

        final String insertQuery = strBuffInsert.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Insert query:" + insertQuery);
        }

        final String sqlSelect = strBuffSelect.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        final String insertUserThreshold = strBuffThresholdInsert.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY insertUserThreshold=" + insertUserThreshold);
        }

        try {

            pstmt = p_con.prepareStatement(sqlSelect);
            psmtUpdate = p_con.prepareStatement(updateQuery);
            psmtInsert = p_con.prepareStatement(insertQuery);
            psmtInsertUserThreshold = p_con.prepareStatement(insertUserThreshold);

            PreparedStatement handlerStmt = null;

            TransferProfileProductVO transferProfileProductVO = null;
            long maxBalance = 0;

            final Date currentDate = new Date();
            long thresholdValue = -1;

            final ArrayList itemsList = p_channelTransferVO.getChannelTransferitemsVOList();
            ChannelTransferItemsVO channelTransferItemsVO = null;
            String userID = null;
            String profileID = null;
            userID = p_channelTransferVO.getToUserID();
            profileID = p_channelTransferVO.getReceiverTxnProfile();
            boolean isNotToExecuteQuery = false;
            final ArrayList<KeyArgumentVO> errorList = new ArrayList<KeyArgumentVO>();
            KeyArgumentVO keyArgumentVO = null;
            for (int i = 0, k = itemsList.size(); i < k; i++) {
                channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);

                pstmt.setString(1, userID);
                pstmt.setString(2, channelTransferItemsVO.getProductCode());
                pstmt.setString(3, p_channelTransferVO.getNetworkCode());
                pstmt.setString(4, p_channelTransferVO.getNetworkCodeFor());

                rs = pstmt.executeQuery();
                long balance = -1;
                if (rs.next()) {
                    balance = rs.getLong("balance");
                    userBalance=balance;
                    channelTransferItemsVO.setBalance(balance);
                }

                if (balance > -1) {
                    channelTransferItemsVO.setPreviousBalance(balance);
                    channelTransferItemsVO.setAfterTransReceiverPreviousStock(balance);
                    // set receiver previous stock.
                    channelTransferItemsVO.setReceiverPreviousStock(balance);
                        balance += channelTransferItemsVO.getReceiverCreditQty();
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "QUERY balance=" + balance);
                        }
                } else {
                    channelTransferItemsVO.setPreviousBalance(0);
                    channelTransferItemsVO.setAfterTransReceiverPreviousStock(0);
                }
                pstmt.clearParameters();

                // in the case of return we have not to check the max balance
                if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(p_channelTransferVO.getTransferType())) {
                    /*
                     * check for the max balance for the product
                     */

                    transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(profileID, channelTransferItemsVO.getProductCode());
                    maxBalance = transferProfileProductVO.getMaxBalanceAsLong();

                    if (maxBalance < balance) {
                        if (!isNotToExecuteQuery) {
                            isNotToExecuteQuery = true;
                        }
                        keyArgumentVO = new KeyArgumentVO();
                        if (isFromWeb) {
                            final String arg[] = { channelTransferItemsVO.getShortName() };
                            keyArgumentVO.setArguments(arg);
                            keyArgumentVO.setKey("error.transfer.maxbalance.reached");
                        } else {
                            final String arg[] = { String.valueOf(channelTransferItemsVO.getShortName()), PretupsBL.getDisplayAmount(transferProfileProductVO
                                .getMaxBalanceAsLong()) };
                            keyArgumentVO.setArguments(arg);
                            keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_BALANCE_SUBKEY);
                        }
                        errorList.add(keyArgumentVO);
                    }
                    // check for the very first txn of the user containg the
                    // order value larger than maxBalance
                    else if (balance == -1 && maxBalance < channelTransferItemsVO.getApprovedQuantity()) {
                        if (!isNotToExecuteQuery) {
                            isNotToExecuteQuery = true;
                        }
                        keyArgumentVO = new KeyArgumentVO();
                        if (isFromWeb) {
                            final String arg[] = { channelTransferItemsVO.getShortName() };
                            keyArgumentVO.setArguments(arg);
                            keyArgumentVO.setKey("error.transfer.maxbalance.reached");
                        } else {
                            final String arg[] = { String.valueOf(channelTransferItemsVO.getShortName()), PretupsBL.getDisplayAmount(transferProfileProductVO
                                .getMaxBalanceAsLong()) };
                            keyArgumentVO.setArguments(arg);
                            keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_BALANCE_SUBKEY);
                        }
                        errorList.add(keyArgumentVO);
                    }
                }
                if (!isNotToExecuteQuery) {
                    int m = 0;
                    // update
                    if (balance > -1) {
                        handlerStmt = psmtUpdate;
                    } else {
                        // insert
                        handlerStmt = psmtInsert;
                           balance = channelTransferItemsVO.getReceiverCreditQty();
                        channelTransferItemsVO.setPreviousBalance(0);
                        handlerStmt.setLong(++m, 0);// previous balance
                        handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));// updated
                        // on
                        // date
                    }

                    handlerStmt.setLong(++m, balance);
                    handlerStmt.setString(++m, p_channelTransferVO.getTransferType());
                    handlerStmt.setString(++m, p_channelTransferVO.getTransferID());
                    handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getTransferDate()));
                    handlerStmt.setString(++m, userID);

                    // where
                    handlerStmt.setString(++m, channelTransferItemsVO.getProductCode());
                    handlerStmt.setString(++m, p_channelTransferVO.getNetworkCode());
                    handlerStmt.setString(++m, p_channelTransferVO.getNetworkCodeFor());

                    updateCount = handlerStmt.executeUpdate();
                    handlerStmt.clearParameters();

                    if (updateCount <= 0) {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "[creditUserBalances]", "",
                            "", "", "BTSLBaseException: update count <=0");
                        throw new BTSLBaseException(this, "creditUserBalances", "error.general.sql.processing");
                    }

                    transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(profileID, channelTransferItemsVO.getProductCode());
                    thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
                    String threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
                    final String remark = null;
                    if (channelTransferItemsVO.getPreviousBalance() <= transferProfileProductVO.getAltBalanceLong() && balance >= transferProfileProductVO.getMinResidualBalanceAsLong()) {
                        thresholdValue = transferProfileProductVO.getAltBalanceLong();
                        threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
                    }

                    try {

                        if ((channelTransferItemsVO.getPreviousBalance() <= thresholdValue && balance >= thresholdValue) || (channelTransferItemsVO.getPreviousBalance() <= thresholdValue && balance <= thresholdValue)) {
                            psmtInsertUserThreshold.clearParameters();
                            m = 0;
                            psmtInsertUserThreshold.setString(++m, userID);
                            psmtInsertUserThreshold.setString(++m, p_channelTransferVO.getTransferID());
                            psmtInsertUserThreshold.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(currentDate));
                            psmtInsertUserThreshold.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(currentDate));
                            psmtInsertUserThreshold.setString(++m, p_channelTransferVO.getNetworkCode());
                            psmtInsertUserThreshold.setString(++m, channelTransferItemsVO.getProductCode());

                            psmtInsertUserThreshold.setString(++m, p_channelTransferVO.getType());
                            psmtInsertUserThreshold.setString(++m, p_channelTransferVO.getTransferType());
                            if (balance >= thresholdValue) {
                                psmtInsertUserThreshold.setString(++m, PretupsI.ABOVE_THRESHOLD_TYPE);
                            } else if (balance <= thresholdValue) {
                                psmtInsertUserThreshold.setString(++m, PretupsI.BELOW_THRESHOLD_TYPE);
                            }

                            psmtInsertUserThreshold.setString(++m, p_channelTransferVO.getReceiverCategoryCode());
                            psmtInsertUserThreshold.setLong(++m, channelTransferItemsVO.getPreviousBalance());
                            psmtInsertUserThreshold.setLong(++m, balance);
                            psmtInsertUserThreshold.setLong(++m, thresholdValue);

                            psmtInsertUserThreshold.setString(++m, threshold_type);
                            psmtInsertUserThreshold.setString(++m, remark);
                            updateCount = psmtInsertUserThreshold.executeUpdate();
                        }
                    } catch (SQLException sqle) {
                        _log.error(methodName, "SQLException " + sqle.getMessage());
                        _log.errorTrace(methodName, sqle);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLoanCreditBL[creditUserBalances]",
                            p_channelTransferVO.getTransferID(), "", p_channelTransferVO.getNetworkCode(),
                            "Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
                    }// end of catch
                }
            }// for
            if (!errorList.isEmpty()) {
                if (isFromWeb) {
                    throw new BTSLBaseException(this, methodName, errorList);
                }
                throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_BALANCE, errorList);
            }

            p_channelTransferVO.setEntryType(PretupsI.CREDIT);
        } catch (BTSLBaseException bbe) {
            _log.errorTrace(methodName, bbe);
            throw bbe;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLoanCreditBL[creditUserBalances]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLoanCreditBL[creditUserBalances]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtInsertUserThreshold != null) {
                    psmtInsertUserThreshold.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:  updateCount =" + updateCount);
            }
        }
        return updateCount;

    }
    
    private static String transferInCountsCheck(Connection p_con, UserTransferCountsVO p_userTransferCountsVO, String p_profileID, String p_networkCode, long p_totalRequestedQtuantity) throws BTSLBaseException {
        final String methodName = "transferInCountsCheck";
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                "Entered UserTransferCountsVO =" + p_userTransferCountsVO + ",p_profileID " + p_profileID + " p_networkCode  " + p_networkCode + " p_totalRequestedQtuantity: " + p_totalRequestedQtuantity);
        }

        /*
         * Now load the transferProfileVO, which contains the LEAST/GREATEST (as
         * per required) values form
         * USER LEVEL PROFILE and CATEGORY LEVEL PROFILE.
         */
        final TransferProfileVO transferProfileVO = TransferProfileCache.getTransferProfileDetails(p_profileID, p_networkCode);
        if (transferProfileVO == null) {
            return PretupsErrorCodesI.CHANNEL_TRANSFER_PROFILE_NOT_EXIST;
        } else if (transferProfileVO.getDailyInCount() <= p_userTransferCountsVO.getDailyInCount()) {
            return PretupsErrorCodesI.CHANNEL_TRANSFER_DAILY_IN_COUNT;
        } else if (transferProfileVO.getDailyInValue() < (p_userTransferCountsVO.getDailyInValue() + p_totalRequestedQtuantity)) {
            return PretupsErrorCodesI.CHANNEL_TRANSFER_DAILY_IN_VALUE;
        } else if (transferProfileVO.getWeeklyInCount() <= p_userTransferCountsVO.getWeeklyInCount()) {
            return PretupsErrorCodesI.CHANNEL_TRANSFER_WEEKLY_IN_COUNT;
        } else if (transferProfileVO.getWeeklyInValue() < (p_userTransferCountsVO.getWeeklyInValue() + p_totalRequestedQtuantity)) {
            return PretupsErrorCodesI.CHANNEL_TRANSFER_WEEKLY_IN_VALUE;
        } else if (transferProfileVO.getMonthlyInCount() <= p_userTransferCountsVO.getMonthlyInCount()) {
            return PretupsErrorCodesI.CHANNEL_TRANSFER_MONTHLY_IN_COUNT;
        } else if (transferProfileVO.getMonthlyInValue() < (p_userTransferCountsVO.getMonthlyInValue() + p_totalRequestedQtuantity)) {
            return PretupsErrorCodesI.CHANNEL_TRANSFER_MONTHLY_IN_VALUE;
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exited ");
        }
        return null;

    }
    
    private boolean checkResetCountersAfterPeriodChange(UserTransferCountsVO p_userTransferCountsVO, java.util.Date p_newDate) {
        final String methodName = "checkResetCountersAfterPeriodChange";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered with transferID=" + p_userTransferCountsVO.getLastTransferID() + " USER ID=" + p_userTransferCountsVO.getUserID());
        }
        boolean isCounterChange = false;
        boolean isDayCounterChange = false;
        boolean isWeekCounterChange = false;
        boolean isMonthCounterChange = false;

        final Date previousDate = p_userTransferCountsVO.getLastTransferDate();

        if (previousDate != null) {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(p_newDate);
            final int presentDay = cal.get(Calendar.DAY_OF_MONTH);
            final int presentWeek = cal.get(Calendar.WEEK_OF_MONTH);
            final int presentMonth = cal.get(Calendar.MONTH);
            final int presentYear = cal.get(Calendar.YEAR);
            cal.setTime(previousDate);
            final int lastWeek = cal.get(Calendar.WEEK_OF_MONTH);
            final int lastTrxDay = cal.get(Calendar.DAY_OF_MONTH);
            final int lastTrxMonth = cal.get(Calendar.MONTH);
            final int lastTrxYear = cal.get(Calendar.YEAR);
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
            _log.debug(methodName,
                "Exiting with isCounterChange=" + isCounterChange + " For transferID=" + p_userTransferCountsVO.getLastTransferID() + " USER ID=" + p_userTransferCountsVO
                    .getUserID());
        }
        return isCounterChange;

    }
    
    private UserVO loaduserMsisdn(Connection p_con, String userID) throws BTSLBaseException, java.sql.SQLException {
        final String methodName = "loaduserMsisdn";
        if (_log.isDebugEnabled()) {
            _log.info(methodName, PretupsI.ENTERED);
        }

        PreparedStatement pstmt = null;
        ResultSet rst = null;
        final UserVO userVO = new UserVO();
        try {
            final StringBuffer queryBuf = new StringBuffer(" select msisdn from users where user_id=? ");

            final String query = queryBuf.toString();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query:" + query);
            }
            pstmt = p_con.prepareStatement(query.toString());
            pstmt.setString(1, userID);

            rst = pstmt.executeQuery();
            while (rst.next()) {
                userVO.setMsisdn(rst.getString("msisdn"));
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "user Loan TransferProcess[loaduserMsisdn]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "user LoanTransferProcess[loaduserMsisdn]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e2) {
                    _log.error(methodName, "SQLException " + e2.getMessage());
                    _log.errorTrace(methodName, e2);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e3) {
                    _log.errorTrace(methodName, e3);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.info(methodName, PretupsI.EXITED);
            }
        }// end finally

        return userVO;
    }

}