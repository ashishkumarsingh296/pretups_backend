package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EmailSendToUser;
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
import com.btsl.pretups.channel.logging.AutoO2CLogger;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileProductsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSWithdrawBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyBL;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyDAO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.LowBalanceAlertVO;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.ibm.icu.util.Calendar;
import com.txn.pretups.channel.profile.businesslogic.CommissionProfileTxnDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserLoanWithdrawBL;

/**
 * @(#)AutoO2CInitiateAction.java
 * 
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Gaurav pandey 25/03/2013 Initial creation
 * 
 *                                This process is used to control the logic flow
 *                                for Auto O2C initiate.
 * 
 */
public class AutoO2CProcess {
    private static Log _log = LogFactory.getLog(AutoO2CProcess.class.getName());

    private String _auto_o2c_amount = null;

    /**
     * 
     */
    private static OperatorUtilI calculatorI = null;
    private long userBalance=0L;

    public static void main(String arg[]){
        final String METHOD_NAME = "main";
        Connection con = null;
        MComConnectionI mcomCon = null;

        boolean statusOk = false;
        statusOk = true;
        Date currentDate = null;
        try {

            final String constnt = arg[0];
            final File constantsFile = new File(constnt);

            if (!constantsFile.exists()) {
            	if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME,"Constants File Not Found .............");
                    }
                return;
            }
            final String logconfig = arg[1];       
            final File logconfigFile = Constants.validateFilePath(logconfig);

            if (!logconfigFile.exists()) {
            	if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME," Logconfig File Not Found .............");
                    }
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            TransferProfileProductCache.loadTransferProfileProductsAtStartup();
            TransferProfileCache.loadTransferProfileAtStartup();
            final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            try {
                calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();

            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[initialize]", "", "", "",
                    "Exception while loading the class at the call:" + e.getMessage());
            }
        }// // end try
        catch (Exception e) {
        	if (_log.isDebugEnabled()) {
                _log.debug("Error in Loading Configuration files ...........................: ",e);
                }
            _log.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end catch
        try {

            currentDate = new Date();
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            // Getting database connection
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();

            if (con == null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("process", " DATABASE Connection is NULL. ");
                }
                throw new BTSLBaseException("Auto o2c TransferProcess", "process", "Not able to get the connection.");
            }

            new AutoO2CProcess().process(con);
        } catch (BTSLBaseException bse) {

            if (_log.isDebugEnabled()) {
                _log.debug("main", " " + bse.getMessage());
            }
            _log.errorTrace(METHOD_NAME, bse);
        } catch (Exception e) {
            if (_log.isDebugEnabled()) {
                _log.debug("main", " " + e.getMessage());
            }
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            if (statusOk) {

				if (mcomCon != null) {
					mcomCon.close("AutoO2CProcess#DistribuidorDataRequestType.java");
					mcomCon = null;
				}

                if (_log.isDebugEnabled()) {
                    _log.info("main", "Exiting");
                }

                try {
    				Thread.sleep(10000);
    				 ConfigServlet.destroyProcessCache();
    			} catch (InterruptedException e1) {
    				_log.errorTrace(METHOD_NAME, e1);
    			}

            }
        }
        System.out.println("exiting main");
    }

    public void process(Connection p_con) throws BTSLBaseException, java.sql.SQLException {
        final String METHOD_NAME = "process";
        final ChannelUserVO toUserVO = null;
        UserTransferCountsVO countVO = null;
        TransferProfileVO profileVO = null;
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
        final TransferProfileDAO transferProfileDAO = new TransferProfileDAO();
        ArrayList<ListValueVO> stocklist = null;
        ChannelTransferVO p_channelTransferVO = null;
        final NetworkStockDAO networkStockDAO = new NetworkStockDAO();
        NetworkVO networkVO = new NetworkVO();
        final NetworkDAO networkDAO = new NetworkDAO();
        ListValueVO stockVO = new ListValueVO();
        final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
        ProcessStatusVO processStatusVO = null;
        ChannelTransferItemsVO channelTransferItemsVO = null;
        Date currentDate = new Date();
        Date checkDate = null;
        // String product="ETOPUP";
        int updateCount = 0;
        Date process_upto = null;
        
        Boolean isLmsAppl = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        Boolean isEmailServiceAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW);
        Integer amountMultFactor = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR);
        Boolean isTransationTypeAlwd = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
        Integer autoO2cAmount = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_O2C_AMOUNT);
        Boolean autoO2cUserSpecficAmount = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_O2C_USER_SPECIFIC_AMOUNT);

        
        try {
            final ProcessBL processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(p_con, ProcessI.Auto_O2C_Process);
            AutoO2CLogger.log("process :Auto o2c STARTED");
            // check process status.
            if (processStatusVO.isStatusOkBool()) {
                process_upto = processStatusVO.getExecutedUpto();
                if (process_upto != null) {
                    checkDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(new Date()));
                    process_upto = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(process_upto));
                    final int diffDate = BTSLUtil.getDifferenceInUtilDates(process_upto, currentDate);
                    if (diffDate <= 0) {
                        _log.error("process", " Process already executed.....");
                        throw new BTSLBaseException("auto o2c ", "process", PretupsErrorCodesI.AUTO_O2C_PROCESS);
                    }
                    final ArrayList<LowBalanceAlertVO> userList = balanceAlertUsers(p_con,autoO2cUserSpecficAmount);
                    // load user list for auto O2C
                    if (userList.isEmpty()) {
                        System.out.println(" Existing.... No user exists for auto O2C.........");
                        return;
                    } else {
                        LowBalanceAlertVO lowBalanceAlertVO = new LowBalanceAlertVO();

                        long Stock = 0L;
                        String product_type = PretupsI.PRODUCT_TYPE_AUTO_O2C;
                        for (int i = 0, j = userList.size(); i < j; i++) {
                            try {

                                lowBalanceAlertVO = userList.get(i);
                                final String toUserID = lowBalanceAlertVO.getUserId();
                                final String toUserName = lowBalanceAlertVO.getUserName();
                                final String Network_code = lowBalanceAlertVO.getNetworkCode();
                                final String product = lowBalanceAlertVO.getProductCode();
                                if(autoO2cUserSpecficAmount) {
                                	_auto_o2c_amount= (PretupsBL.getDisplayAmount(lowBalanceAlertVO.getAutoO2CTransactionAmount()));

                                }else {
                                	_auto_o2c_amount = Integer.toString(autoO2cAmount);
                                }
                                final Date curDate = new Date();
                                final ChannelUserVO receiverUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(p_con, toUserID, false, curDate,false);
                                UserPhoneVO userPhoneVO = new UserPhoneVO();
								userPhoneVO = channelUserDAO.loadUserPhoneDetails(p_con, receiverUserVO.getUserID());

                                if (PretupsI.PRODUCT_POSTETOPUP.equals(lowBalanceAlertVO.getProductCode())) {
                                    product_type = PretupsI.PRODUCT_TYPE_AUTO_O2C_POST;
                                }

                                if (receiverUserVO == null) {
                                    if (_log.isDebugEnabled()) {
                                        _log.debug("process", "Auto o2c is not possible as Receiver does not exists: " + lowBalanceAlertVO.getUserId());
                                    }
                                    AutoO2CLogger.log("process :Auto o2c is not possible as Receiver does not exists: " + lowBalanceAlertVO.getUserId());
                                    continue;
                                }
                                // check user status
                                else if (!PretupsI.USER_STATUS_ACTIVE.equals(receiverUserVO.getStatus())) {
                                	PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_O2C_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
									pushMessage.push();
                                    if (_log.isDebugEnabled()) {
                                        _log.debug("process", "Auto o2c is not possible as Receiver is not active: " + lowBalanceAlertVO.getUserId());
                                    }
                                    AutoO2CLogger.log("process :Auto o2c is not possible as Receiver does not active: " + lowBalanceAlertVO.getUserId());
                                    continue;
                                }

                                // check user's commission profile.
                                else if (receiverUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
                                	PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_O2C_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
									pushMessage.push();
                                    if (_log.isDebugEnabled()) {
                                        _log.debug("process", "Auto o2c is not possible as Receiver's commission profile is not applicable : " + lowBalanceAlertVO.getUserId());
                                    }
                                    AutoO2CLogger
                                        .log("process :Auto o2c is not possible as Receiver's commission profile is not applicable: " + lowBalanceAlertVO.getUserId());
                                    continue;
                                }

                                // check user's commission profile status.
                                else if (!PretupsI.YES.equals(receiverUserVO.getCommissionProfileStatus())) {
                                	PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_O2C_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
									pushMessage.push();
                                    if (_log.isDebugEnabled()) {
                                        _log.debug("process", "Auto o2c is not possible as Receiver's commission profile is not active : " + lowBalanceAlertVO.getUserId());
                                    }
                                    AutoO2CLogger.log("process :Auto o2c is not possible as Receiver's commission profile is not active: " + lowBalanceAlertVO.getUserId());
                                    continue;
                                }

                                // check user's transfer profile status.
                                else if (!PretupsI.YES.equals(receiverUserVO.getTransferProfileStatus())) {
                                	PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_O2C_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
									pushMessage.push();
                                    if (_log.isDebugEnabled()) {
                                        _log.debug("process", "Auto o2c is not possible as Receiver's transfer profile is not active : " + lowBalanceAlertVO.getUserId());
                                    }
                                    AutoO2CLogger.log("process :Auto o2c is not possible as Receiver's transfer profile is not active: " + lowBalanceAlertVO.getUserId());
                                    continue;
                                }

                                // checking receiver's IN suspend
                                if (receiverUserVO.getInSuspend().equalsIgnoreCase(PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND)) {
                                    if (_log.isDebugEnabled()) {
                                        _log.debug(
                                            "process",
                                            "Auto o2c is not possible as Receiver is IN suspend, receiver user ID: " + lowBalanceAlertVO.getUserId() + " receiver's IN suspend: " + toUserVO
                                                .getInSuspend());
                                    }
                                    AutoO2CLogger.log("process :Auto o2c is not possible as Receiver is IN suspend, receiver user ID: " + lowBalanceAlertVO.getUserId());
                                    continue;
                                }

                                countVO = userTransferCountsDAO.loadTransferCounts(p_con, receiverUserVO.getUserID(), false);
                                profileVO = transferProfileDAO.loadTransferProfileThroughProfileID(p_con, lowBalanceAlertVO.getProfileID(), receiverUserVO.getNetworkID(),
                                    receiverUserVO.getCategoryCode(), true);

                                if ((countVO.getDailyInCount() >= profileVO.getDailyInCount()) || (countVO.getDailyInValue() >= profileVO.getDailyInValue())) {
                                	PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_O2C_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
									pushMessage.push();
                                    if (_log.isDebugEnabled()) {
                                        _log.debug(
                                            "process",
                                            "Auto o2c is not possible as receiver's Daily IN count or IN amount is greater than the max IN count and IN amount,receiver user id :" + lowBalanceAlertVO
                                                .getUserId());
                                    }
                                    AutoO2CLogger
                                        .log("process :Auto o2c is not possible as receiver's Daily IN count or IN amount is greater than the max IN count and IN amount,receiver user id :" + lowBalanceAlertVO
                                            .getUserId());
                                    continue;
                                } else if ((countVO.getWeeklyInCount() >= profileVO.getWeeklyInCount()) || (countVO.getWeeklyInValue() >= profileVO.getWeeklyInValue())) {
	                    				PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_O2C_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
										pushMessage.push();
                                	if (_log.isDebugEnabled()) {
                                        _log.debug(
                                            "process",
                                            "Auto o2c is not possible as receiver's Weekly IN count or IN amount is greater than the max IN count and IN amount,receiver user id :" + lowBalanceAlertVO
                                                .getUserId());
                                    }
                                    AutoO2CLogger
                                        .log("process :Auto o2c is not possible as receiver's Weekly IN count or IN amount is greater than the max IN count and IN amount,receiver user id :" + lowBalanceAlertVO
                                            .getUserId());
                                    continue;
                                } else if ((countVO.getMonthlyInCount() >= profileVO.getMonthlyInCount()) || (countVO.getMonthlyInValue() >= profileVO.getMonthlyInValue())) {
                                	PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_O2C_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
									pushMessage.push();
                                	if (_log.isDebugEnabled()) {
                                        _log.debug(
                                            "process",
                                            "Auto o2c is not possible as receiver's Monthly IN count or IN amount is greater than the max IN count and IN amount,receiver user id :" + lowBalanceAlertVO
                                                .getUserId());
                                    }
                                    AutoO2CLogger
                                        .log("process :Auto o2c is not possible as receiver's Monthly IN count or IN amount is greater than the max IN count and IN amount,receiver user id :" + lowBalanceAlertVO
                                            .getUserId());
                                    continue;
                                }

                                // load transfer rule
                                final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(p_con, Network_code, receiverUserVO.getDomainID(),
                                    PretupsI.CATEGORY_TYPE_OPT, receiverUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);

                                if (channelTransferRuleVO == null) {
									PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_O2C_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
									pushMessage.push();
                                    if (_log.isDebugEnabled()) {
                                        _log.debug("process", "Auto o2c is not possible as receiver's trasfer rule does not exists:" + lowBalanceAlertVO.getUserId());
                                    }
                                    AutoO2CLogger.log("process :Auto o2c is not possible as receiver's trasfer rule does not exists:" + lowBalanceAlertVO.getUserId());

                                    continue;

                                } else if (PretupsI.NO.equals(channelTransferRuleVO.getTransferAllowed())) {
                    				PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_O2C_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
									pushMessage.push();
                                    if (_log.isDebugEnabled()) {
                                        _log.debug("process", "Auto o2c is not possible as receiver's trasfer does not allowed: " + lowBalanceAlertVO.getUserId());
                                    }
                                    AutoO2CLogger.log("process :Auto o2c is not possible as receiver's trasfer does not allowed:" + lowBalanceAlertVO.getUserId());
                                    continue;

                                }

                                else if (channelTransferRuleVO.getProductVOList() == null || channelTransferRuleVO.getProductVOList().size() == 0) {
                            		PushMessage pushMessage=new PushMessage(receiverUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_O2C_CONFIGURATION_ERROR_SNDR,new String[]{receiverUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),receiverUserVO.getNetworkID());
									pushMessage.push();
                                	if (_log.isDebugEnabled()) {
                                        _log.debug("process", "Auto o2c is not possible as no product is assign for transfer rule: " + lowBalanceAlertVO.getUserId());
                                    }
                                    AutoO2CLogger.log("process :Auto o2c is not possible as no product is assign for transfer rule:" + lowBalanceAlertVO.getUserId());
                                    continue;

                                }

                                // if network is less than auto o2c amount
                                networkVO = networkDAO.loadNetwork(p_con, Network_code);
                                stocklist = networkStockDAO.loadStockOfProduct(p_con, networkVO.getNetworkCode(), networkVO.getNetworkCode(), product_type);
                                if (stocklist.size() > 0) {
                                    stockVO = stocklist.get(0);
                                    Stock = Long.parseLong(stockVO.getValue());
                                    if (Stock <= Long.parseLong(_auto_o2c_amount)) {
                                        if (_log.isDebugEnabled()) {
                                            _log.debug("process", "Auto o2C is not possible as netwok stock is not sufficient" + lowBalanceAlertVO.getUserId());
                                        }
                                        AutoO2CLogger.log("process :Auto o2c is not possible as netwok stock is not sufficient:" + lowBalanceAlertVO.getUserId());
                                        continue;
                                    }
                                } else {
                                    throw new BTSLBaseException("AutoO2CProcess", "process", "netwok product" + stocklist.size());
                                }

                                // do the auto o2c transfer
                                final Date p_currentDate = new Date();
                                ArrayList<ChannelTransferItemsVO> list = this.loadO2CXfrProductList(p_con, product_type, Network_code, receiverUserVO
                                    .getCommissionProfileSetID(), p_currentDate,_auto_o2c_amount);
                                if (list.size() == 0) {
                                    throw new BTSLBaseException("AutoO2CProcess", "process", "commission profile products" + list.size());
                                }

                                /*
                                 * Now further filter the list with the transfer
                                 * rules list and the above list
                                 * of commission profile products.
                                 */
                                list = filterProductWithTransferRule(list, channelTransferRuleVO.getProductVOList());
                                if (list.size() == 0) {
                                    throw new BTSLBaseException("AutoO2CProcess", "process", "no product of commission match with transfer rule products" + list.size());
                                }
                                final ArrayList<ChannelTransferItemsVO> itemsList = new ArrayList<ChannelTransferItemsVO>();
                                for (int p = 0, k = list.size(); p < k; p++) {
                                    channelTransferItemsVO = list.get(p);
                                    if (!BTSLUtil.isNullString(channelTransferItemsVO.getRequestedQuantity())) {
                                    	channelTransferItemsVO.setAfterTransSenderPreviousStock(Long.parseLong(stockVO.getValue()));
                                    	channelTransferItemsVO.setAfterTransReceiverPreviousStock(lowBalanceAlertVO.getBalance());
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
                                channelTransferVO.setDualCommissionType(receiverUserVO.getDualCommissionType());
                                channelTransferVO.setToUserID(receiverUserVO.getUserID());
                                final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
                                String type = (isTransationTypeAlwd)?PretupsI.TRANSFER_TYPE_O2C:PretupsI.ALL;
                				String paymentMode = PretupsI.ALL;
                                commissionProfileDAO.loadProductListWithTaxes(p_con, receiverUserVO.getCommissionProfileSetID(), receiverUserVO.getCommissionProfileSetVersion(), itemsList, type, paymentMode);

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
                                	AutoO2CLogger.log("process :Auto o2c is not possible as amount is not defined in Commission Slab" );
                                    throw new BTSLBaseException(ChannelTransferBL.class.getName(), "loadAndCalculateTaxOnProducts",
                                        PretupsErrorCodesI.ERROR_COMMISSION_SLAB_NOT_DEFINE, errorList);
                                }
                                channelTransferVO.setOtfFlag(true);
                            	if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()))
                        		{
                        			ChannelTransferBL.increaseOptOTFCounts(p_con, channelTransferVO);
                        		}
                            	ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO, PretupsI.CHANNEL_TYPE_O2C);
                                long totTax1 = 0, totTax2 = 0, totTax3 = 0, totRequestedQty = 0, payableAmount = 0, netPayableAmt = 0, totTransferedAmt = 0, totalMRP = 0, totcommission = 0;

                                long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
                                ChannelTransferItemsVO transferItemsVO = null;
                                for (int l = 0, k = itemsList.size(); l < k; l++) {
                                    transferItemsVO = itemsList.get(l);

                                    totTax1 += transferItemsVO.getTax1Value();
                                    totTax2 += transferItemsVO.getTax2Value();
                                    totTax3 += transferItemsVO.getTax3Value();
                                    totcommission += transferItemsVO.getCommValue();
                                    if (_log.isDebugEnabled()) {
                                        _log.debug("process", "Total Commission = " + totcommission);
                                    }
                                    if (transferItemsVO.getRequestedQuantity() != null && BTSLUtil.isDecimalValue(transferItemsVO.getRequestedQuantity())) {
                                        totRequestedQty += PretupsBL.getSystemAmount(transferItemsVO.getRequestedQuantity());
                                        if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
                                            totTransferedAmt += transferItemsVO.getReceiverCreditQty() * Long.parseLong(PretupsBL.getDisplayAmount(transferItemsVO
                                                .getUnitValue()));
                                        } else {
                                            totTransferedAmt += (Double.parseDouble(transferItemsVO.getRequestedQuantity()) * transferItemsVO.getUnitValue());
                                        }
                                    }
                                    payableAmount += transferItemsVO.getPayableAmount();
                                    netPayableAmt += transferItemsVO.getNetPayableAmount();
                                    totalMRP += transferItemsVO.getProductTotalMRP();
                                    commissionQty += transferItemsVO.getCommQuantity();
                                    if (_log.isDebugEnabled()) {
                                        _log.debug("process", "Commission Quantity= " + commissionQty);
                                    }
                                    senderDebitQty += transferItemsVO.getSenderDebitQty();
                                    receiverCreditQty += transferItemsVO.getReceiverCreditQty();
                                }
                                // create vo for transfer
                                final UserVO userVO = channelUserDAO.loadOptUserForO2C(p_con, Network_code);
                                if (userVO != null) {
                                    p_channelTransferVO = new ChannelTransferVO();
                                    p_channelTransferVO.setNetworkCode(Network_code);
                                    p_channelTransferVO.setReceiverTxnProfile(receiverUserVO.getTransferProfileID());
                                    p_channelTransferVO.setReceiverTxnProfileName(receiverUserVO.getTransferProfileName());
                                    p_channelTransferVO.setTotalTax1(totTax1);
                                    p_channelTransferVO.setTotalTax2(totTax2);
                                    p_channelTransferVO.setTotalTax3(totTax3);
                                    p_channelTransferVO.setRequestedQuantity(PretupsBL.getSystemAmount(transferItemsVO.getRequestedQuantity()));
                                    p_channelTransferVO.setPayableAmount(payableAmount);
                                    p_channelTransferVO.setNetPayableAmount(netPayableAmt);
                                    p_channelTransferVO.setPayInstrumentAmt(netPayableAmt);
                                    p_channelTransferVO.setTransferMRP(totTransferedAmt);
                                    p_channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
                                    p_channelTransferVO.setToUserID(toUserID);
                                    p_channelTransferVO.setToUserName(toUserName);
                                    p_channelTransferVO.setReceiverGgraphicalDomainCode(receiverUserVO.getGeographicalCode());
                                    p_channelTransferVO.setReceiverDomainCode(receiverUserVO.getCategoryCode());
                                    p_channelTransferVO.setGraphicalDomainCode(receiverUserVO.getGeographicalCode());
                                    p_channelTransferVO.setDomainCode(receiverUserVO.getDomainID());
                                    p_channelTransferVO.setReceiverCategoryCode(receiverUserVO.getCategoryCode());
                                    p_channelTransferVO.setCommProfileSetId(receiverUserVO.getCommissionProfileSetID());
                                    p_channelTransferVO.setDualCommissionType(receiverUserVO.getDualCommissionType());
                                    p_channelTransferVO.setNetworkCodeFor(Network_code);
                                    p_channelTransferVO.setCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
                                    p_channelTransferVO.setTransferDate(p_currentDate);
                                    p_channelTransferVO.setCommProfileVersion(receiverUserVO.getCommissionProfileSetVersion());
                                    p_channelTransferVO.setCreatedOn(p_currentDate);
                                    p_channelTransferVO.setModifiedOn(p_currentDate);
                                    p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                                    p_channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
                                    // Added for Request_Gateway_Type Non empty
                                    p_channelTransferVO.setRequestGatewayType(PretupsI.REQUEST_SOURCE_WEB);

                                    p_channelTransferVO.setProductType(product_type);
                                    p_channelTransferVO.setProductCode(product);
                                    p_channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
                                    p_channelTransferVO.setTransactionMode(PretupsI.AUTO_C2C_TXN_MODE);
									p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
									p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                                    p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                                    p_channelTransferVO.setControlTransfer(PretupsI.YES);
                                    p_channelTransferVO.setCommQty(commissionQty);
                                    p_channelTransferVO.setSenderDrQty(senderDebitQty);
                                    p_channelTransferVO.setReceiverCrQty(receiverCreditQty);
                                    final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
                                    ChannelTransferBL.genrateTransferID(p_channelTransferVO);
                                    p_channelTransferVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
                                    p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                                    p_channelTransferVO.setChannelTransferitemsVOList(itemsList);
                                    p_channelTransferVO.setCreatedBy(userVO.getUserID());
                                    p_channelTransferVO.setModifiedBy(userVO.getUserID());
                                    p_channelTransferVO.setCreatedOn(currentDate);
                                    p_channelTransferVO.setModifiedOn(currentDate);
                                    p_channelTransferVO.setReceiverDomainCode(receiverUserVO.getDomainID());
                                    p_channelTransferVO.setReceiverGradeCode(receiverUserVO.getUserGrade());
									p_channelTransferVO.setRequestGatewayCode(PretupsI.REQUEST_SOURCE_WEB);
									p_channelTransferVO.setToUserCode(receiverUserVO.getMsisdn());
									
									
                                    final int count = channelTransferDAO.addChannelTransfer(p_con, p_channelTransferVO);

                                    // For message push and email- 22/8/13
                                    UserVO newUserVO = new UserVO();
                                    final UserDAO userDAO = new UserDAO();
                                    UserVO newUserVO1 = new UserVO();
                                    if (receiverUserVO.getParentID().equals(PretupsI.ROOT_PARENT_ID)) {
                                        newUserVO1 = this.loaduserMsisdn(p_con, receiverUserVO.getCreatedBy());
                                        newUserVO = userDAO.loadUsersDetails(p_con, newUserVO1.getMsisdn());
                                    } else {
                                        newUserVO = userDAO.loadUserDetailsFormUserID(p_con, receiverUserVO.getOwnerID());
                                    }

                                    PushMessage pushMessage = null;
                                    BTSLMessages sendbtslMessage = null;
                                    final Locale localeMsisdn = null;
                                    Locale locale = null;
                                    UserPhoneVO phoneVO = new UserPhoneVO();
                                    phoneVO = channelUserDAO.loadUserPhoneDetails(p_con, receiverUserVO.getUserID());
                                    String subject = null;
                                    EmailSendToUser emailSendToUser = null;
                                    // end

                                    if (count > 0) {
                                        final boolean debit = true;
                                        String Status = null;

                                        Status = channelTransferDAO.getStatusOfDomain(p_con, receiverUserVO.getDomainID());

                                        if (Status.equals("N")) {
                                            throw new BTSLBaseException("auto.O2C.error.invalidDomain");
                                        }

                                        ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, toUserID, p_currentDate, debit);
                                        ChannelTransferBL.updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, toUserID, p_currentDate);
                                        if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
                                            ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(p_con, p_channelTransferVO, toUserID, p_currentDate,
                                                debit);
                                            ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(p_con, p_channelTransferVO, toUserID, p_currentDate);
                                        }
                                        // update user daily balances
                                        final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
                                        int upCount = 0;
                                        int crCount = 0;
                                        int O2CuserINCount = 0;

                                        upCount = userBalancesDAO.updateUserDailyBalances(p_con, currentDate, constructBalanceVOFromTxnVO(p_channelTransferVO));
                                        p_channelTransferVO.setTransferDate(currentDate);
                                        crCount = creditUserBalances(p_con, p_channelTransferVO, true);
                                        O2CuserINCount = updateOptToChannelUserInCounts(p_con, p_channelTransferVO, currentDate);

                                        if (upCount > 0 && crCount > 0 && O2CuserINCount > 0) {
                                            p_con.commit();
                                            if (isLmsAppl) {
                                                try {

                                                    final LoyaltyBL _loyaltyBL = new LoyaltyBL();
                                                    final LoyaltyVO loyaltyVO = new LoyaltyVO();
                                                    PromotionDetailsVO promotionDetailsVO = new PromotionDetailsVO();
                                                    final LoyaltyDAO _loyaltyDAO = new LoyaltyDAO();
                                                    final ArrayList arr = new ArrayList();
                                                    final Date date = new Date();
                                                    loyaltyVO.setServiceType(PretupsI.O2C_MODULE);
                                                    loyaltyVO.setModuleType(PretupsI.O2C_MODULE);
                                                    loyaltyVO.setTransferamt(p_channelTransferVO.getTransferMRP());
                                                    loyaltyVO.setCategory(p_channelTransferVO.getCategoryCode());
                                                    loyaltyVO.setUserid(p_channelTransferVO.getToUserID());
                                                    loyaltyVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
                                                    loyaltyVO.setSenderMsisdn(p_channelTransferVO.getToUserCode());
                                                    loyaltyVO.setTxnId(p_channelTransferVO.getTransferID());
                                                    loyaltyVO.setCreatedOn(date);
                                                    loyaltyVO.setProductCode(p_channelTransferVO.getProductCode());
                                                    arr.add(loyaltyVO.getUserid());
                                                    promotionDetailsVO = _loyaltyDAO.loadSetIdByUserId(p_con, arr);
                                                    loyaltyVO.setSetId(promotionDetailsVO.get_setId());
                                                    if (loyaltyVO.getSetId() == null) {
                                                        _log.error("process", "Exception durign LMS Module Profile Details are not found");
                                                    } else {
                                                        _loyaltyBL.distributeLoyaltyPoints(PretupsI.O2C_MODULE, channelTransferVO.getTransferID(), loyaltyVO);
                                                    }

                                                } catch (Exception ex) {
                                                    _log.error("process", "Exception durign LMS Module " + ex.getMessage());
                                                    _log.errorTrace(METHOD_NAME, ex);

                                                }
                                            }

                                            processStatusVO.setExecutedUpto(currentDate);
                                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "Auto O2C [process]",
                                                "", "", "", " Auto O2C process has been executed successfully.");
                                            _log.debug("process", "Auto o2c process has been Executed successfully");
                                            AutoO2CLogger.log("process :Auto o2c process has been Executed successfully");

                                            // For message push and email-
                                            // 22/8/13
                                            try {
                                            	
                                                locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
                                                String [] arg= new String [5];
                                                arg[0]=p_channelTransferVO.getTransferID();
                                                arg[1]=p_channelTransferVO.getProductCode();
                                                if (_log.isDebugEnabled()) {
                                                    _log.debug("process", "_auto_o2c_amount = " + _auto_o2c_amount);
                                                }
                                                
                                                long  finalAmount = Long.parseLong(_auto_o2c_amount) + (commissionQty/(int)amountMultFactor);
                                           
                                                if (_log.isDebugEnabled()) {
                                                    _log.debug("process", "Final Amount= " + finalAmount);
                                                }
                                                arg[2]= _auto_o2c_amount;
                                                arg[3]=PretupsBL.getDisplayAmount(userBalance+PretupsBL.getSystemAmount(finalAmount));
                                                Thread.sleep(300);
                                                sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.AUTO_O2C_TRASFER_SUCCESS, arg);
                                                pushMessage = new PushMessage(phoneVO.getMsisdn(), sendbtslMessage, "", "", locale, receiverUserVO.getNetworkID(),
                                                    null);
                                                pushMessage.push();
                                            } catch (Exception e) {
                                                _log.errorTrace(METHOD_NAME, e);
                                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR,
                                                    "AutoO2CProcess[Process]", "", "", "", "Exception while msg push for auto-o2c success:" + e.getMessage());
                                            }
                                            // Email for pin & password-
                                            // 22/08/13
                                            try {
                                                if (isEmailServiceAllow && !BTSLUtil.isNullString(newUserVO.getEmail())) {

                                                    sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.AUTO_O2C_PROCESS_SUCCESS,
                                                        new String[] { receiverUserVO.getUserID() });

    
                                                    subject = "Auto O2C Transfer";
                                                    emailSendToUser = new EmailSendToUser(subject, sendbtslMessage, locale, newUserVO.getNetworkID(),
                                                        "Email will be delivered shortly", newUserVO, newUserVO);
                                                    emailSendToUser.sendMail();

                                                }
                                            } catch (Exception e) {
                                                _log.errorTrace(METHOD_NAME, e);
                                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR,
                                                    "AutoO2CProcess[Process]", "", "", "", "Exception while email push for auto-o2c success:" + e.getMessage());
                                            }
                                            // end- 22/8/13

                                        } else {
                                            p_con.rollback();
                                            if (_log.isDebugEnabled()) {
                                                _log.debug("process", "Auto o2c is not possible");
                                            }
                                            // For message push and email-
                                            // 22/8/13
                                            try {
                                                locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
                                                sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.AUTO_O2C_TRASFER_FAIL, new String[] { _auto_o2c_amount });
                                                pushMessage = new PushMessage(phoneVO.getMsisdn(), sendbtslMessage, "", "", locale, receiverUserVO.getNetworkID(),
                                                    "SMS will be delivered shortly thankyou");
                                                pushMessage.push();
                                            } catch (Exception e) {
                                                _log.errorTrace(METHOD_NAME, e);
                                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR,
                                                    "AutoO2CProcess[Process]", "", "", "", "Exception while msg push for auto-o2c failure:" + e.getMessage());
                                            }
                                            // Email for pin & password- 22/8/13
                                            try {
                                                if (isEmailServiceAllow && !BTSLUtil.isNullString(newUserVO.getEmail())) {

                                                    sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.AUTO_O2C_PROCESS_FAIL, new String[] { receiverUserVO.getUserID() });

                                                    subject = "Auto O2C Transfer";
                                                    emailSendToUser = new EmailSendToUser(subject, sendbtslMessage, locale, newUserVO.getNetworkID(),
                                                        "Email will be delivered shortly", newUserVO, newUserVO);
                                                    emailSendToUser.sendMail();
                                                }

                                            } catch (Exception e) {
                                                _log.errorTrace(METHOD_NAME, e);
                                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR,
                                                    "AutoO2CProcess[Process]", "", "", "", "Exception while email push for auto-o2c failure:" + e.getMessage());
                                            }
                                        }
                                    }

                                    else {
                                        p_con.rollback();
                                        // For message push & email
                                        try {
                                            locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
                                            sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.AUTO_O2C_TRASFER_FAIL, new String[] { _auto_o2c_amount });
                                            pushMessage = new PushMessage(phoneVO.getMsisdn(), sendbtslMessage, "", "", locale, receiverUserVO.getNetworkID(),
                                                "SMS will be delivered shortly thankyou");
                                            pushMessage.push();
                                        } catch (Exception e) {
                                            _log.errorTrace(METHOD_NAME, e);
                                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR,
                                                "AutoO2CProcess[Process]", "", "", "", "Exception while msg push for auto-o2c failure:" + e.getMessage());
                                        }
                                        // Email for pin & password- email send
                                        if (isEmailServiceAllow && !BTSLUtil.isNullString(newUserVO.getEmail())) {
                                            try {
                                                sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.AUTO_O2C_PROCESS_FAIL, new String[] { receiverUserVO.getUserID() });

                                                subject = "Auto O2C Transfer";
                                                emailSendToUser = new EmailSendToUser(subject, sendbtslMessage, locale, newUserVO.getNetworkID(),
                                                    "Email will be delivered shortly", newUserVO, newUserVO);
                                                emailSendToUser.sendMail();
                                            } catch (Exception e) {
                                                _log.errorTrace(METHOD_NAME, e);
                                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR,
                                                    "AutoO2CProcess[Process]", "", "", "", "Exception while email push for auto-o2c failure:" + e.getMessage());
                                            }
                                        }

                                        if (_log.isDebugEnabled()) {
                                            _log.debug("process", "Auto o2c is not possible");
                                        }
                                    }
                                } else {
                                    if (_log.isDebugEnabled()) {
                                        _log.debug("process", "Auto o2c is not possible no user exists to perform auto O2C");
                                    }
                                }
                            } catch (BTSLBaseException be) {
                                if (p_con != null) {
                                    p_con.rollback();
                                }
                                if (_log.isDebugEnabled()) {
                                    _log.debug(METHOD_NAME, " Exception in executing record  p_channelTransferVO : " + p_channelTransferVO);
                                }
                                _log.errorTrace(METHOD_NAME, be);
                                AutoO2CLogger.log("process :Auto o2c is not possible for user " + lowBalanceAlertVO.getUserId()+ " because " +be.getMessage());
                                
                                
                            } catch (Exception e) {
                                if (p_con != null) {
                                    p_con.rollback();
                                }
                                if (_log.isDebugEnabled()) {
                                    _log.debug(METHOD_NAME, " Exception in executing record  p_channelTransferVO : " + p_channelTransferVO);
                                }
                                _log.errorTrace(METHOD_NAME, e);
                                
                                AutoO2CLogger.log("process :Auto o2c is not possible for user " + lowBalanceAlertVO.getUserId()+ "because" +e.getMessage());
                                

                            }
                        }
                    }
                } else {
                    throw new BTSLBaseException("AutoO2CProcess", "process", PretupsErrorCodesI.AUTO_O2C_PROCESS);
                }
            } else {
                throw new BTSLBaseException("AutoO2CProcess", "process", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }

        }// / end try

        catch (SQLException e) {
            p_con.rollback();
            _log.error("Auto o2c process", "Exception : " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "AUTO_O2C_PROCESS[process]", "", "", "",
                " AUTO_O2C_PROCESS could not be executed successfully.");
            throw new BTSLBaseException("Auto o2c", "process", PretupsErrorCodesI.AUTO_O2C_PROCESS);
        } catch (Exception e) {
            p_con.rollback();
            _log.error("Auto o2c process", "Exception : " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "AUTO_O2C_PROCESS[process]", "", "", "",
                " AUTO_O2C_PROCESS could not be executed successfully.");
            throw new BTSLBaseException("Auto o2c", "process", PretupsErrorCodesI.AUTO_O2C_PROCESS);
        } finally {
            try {
                if (processStatusVO.isStatusOkBool()) {
                    processStatusVO.setStartDate(currentDate);
                    processStatusVO.setExecutedOn(currentDate);

                    processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    updateCount = (new ProcessStatusDAO()).updateProcessDetail(p_con, processStatusVO);
                    if (updateCount > 0) {
                        p_con.commit();
                    }
                }
                AutoO2CLogger.log("process :Auto o2c process END");
            } catch (Exception ex) {
                if (_log.isDebugEnabled()) {
                    _log.debug("process", "Exception in closing connection ");
                }
                _log.errorTrace(METHOD_NAME, ex);
            }
            if (p_con != null) {
                try {
                    p_con.close();
                } catch (SQLException e1) {
                    _log.errorTrace(METHOD_NAME, e1);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting..... ");
            }
        }
    }

    /**
     * return list of low balance alerting user
     * 
     * @param p_channelTransferVO
     * @throws BTSLBaseException
     * @throws java.sql.SQLException
     */

    private ArrayList<LowBalanceAlertVO> balanceAlertUsers(Connection p_con,Boolean autoO2cUserSpecficAmount) throws BTSLBaseException, java.sql.SQLException {
        final String METHOD_NAME = "balanceAlertUsers";
        if (_log.isDebugEnabled()) {
            _log.info("balanceAlertUsers", "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rst = null;
        ArrayList<LowBalanceAlertVO> list = null;
        LowBalanceAlertVO alertVO = null;
        try {
            final StringBuffer queryBuf = new StringBuffer(
                "select distinct UTC.user_id,UTC.product_code,U.msisdn,  U.network_code,CU.transfer_profile_id,UB.balance,U.category_code ");
            queryBuf
                .append(",CU.autoo2c_transaction_amt from user_threshold_counter UTC, users U, channel_users CU, user_balances UB,(select USER_ID user_id1,product_code product_code1,Max(ENTRY_DATE_TIME) ENTRY_DATE_TIME1 ");
            queryBuf
                .append("from user_threshold_counter where RECORD_TYPE='BT' group by user_id,product_code  order by user_id) X where CU.AUTO_O2C_ALLOW=?  AND  UTC.user_id=U.user_id ");
            queryBuf.append("AND UTC.RECORD_TYPE=? AND CU.user_id=U.user_id AND UB.user_id=U.user_id AND U.status <> ? AND ");
            if(autoO2cUserSpecficAmount) {
            	queryBuf.append("UB.BALANCE<=CU.AUTOO2C_THRESHOLD_VALUE AND ");
            	
            }else {
            	queryBuf.append("UB.BALANCE<=UTC.THRESHOLD_VALUE AND ");
            }
            queryBuf.append("UTC.ENTRY_DATE_TIME =X.ENTRY_DATE_TIME1 and  UTC.user_id=X.USER_ID1 and UTC.product_code=X.product_code1 and ub.product_code =utc.product_code");
            final String query = queryBuf.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("balanceAlertUsers", "Query:" + query);
            }
            pstmt = p_con.prepareStatement(query.toString());
            pstmt.setString(1, PretupsI.AUTO_O2C_ALLOW);

            pstmt.setString(2, PretupsI.BELOW_THRESHOLD_TYPE);
            pstmt.setString(3, PretupsI.NO);

            rst = pstmt.executeQuery();

            list = new ArrayList<LowBalanceAlertVO>();
            while (rst.next()) {
                alertVO = new LowBalanceAlertVO();
                alertVO.setUserId(rst.getString("user_id"));
                alertVO.setProductCode(rst.getString("product_code"));
                alertVO.setMsisdn(rst.getString("msisdn"));
                alertVO.setNetworkCode(rst.getString("network_code"));
                alertVO.setCategoryCode(rst.getString("category_code"));
                alertVO.setProfileID(rst.getString("transfer_profile_id"));
                alertVO.setBalance(rst.getLong("balance"));
                if(autoO2cUserSpecficAmount) {
                alertVO.setAutoO2CTransactionAmount(rst.getLong("autoo2c_transaction_amt"));
                }
                list.add(alertVO);
            }
        } catch (SQLException sqle) {
            _log.error("balanceAlertUsers", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Auto o2c TransferProcess[balanceAlertUsers]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("Auto o2cTransferProcess", "balanceAlertUsers", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("balanceAlertUsers", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Auto o2cTransferProcess[balanceAlertUsers]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("Auto o2cTransferProcess", "balanceAlertUsers", "error.general.processing");
        }// end of catch
        finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e2) {
                    _log.errorTrace(METHOD_NAME, e2);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e3) {
                    _log.errorTrace(METHOD_NAME, e3);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.info("balanceAlertUsers", " Exiting list size " + list.size());
            }
        }// end finally

        return list;

    }

    private ArrayList<ChannelTransferItemsVO> loadO2CXfrProductList(Connection p_con, String p_productType, String p_networkCode, String p_commProfileSetId, Date p_currentDate ,String reqQty) throws BTSLBaseException {
        final String METHOD_NAME = "loadO2CXfrProductList";
        if (_log.isDebugEnabled()) {
            _log.debug(
                "loadO2CXfrProductList",
                "Entered   p_productType: " + p_productType + " NetworkCode:" + p_networkCode + " CommissionProfileSetID: " + p_commProfileSetId + " CurrentDate: " + p_currentDate);
        }
        long requestedQty = 0;
        requestedQty=PretupsBL.getSystemAmount(reqQty);
        final ArrayList<ChannelTransferItemsVO> productList = new ArrayList<ChannelTransferItemsVO>();

        final NetworkProductDAO networkProductDAO = new NetworkProductDAO();

        // load the product list mapped with the network.
        final ArrayList prodList = networkProductDAO.loadProductListForXfr(p_con, p_productType, p_networkCode);

        // check whether product exist or not of the input productType
        if (prodList.size() == 0) {
            throw new BTSLBaseException("AutoO2CProcess", "process", "product list size is" + prodList.size());
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
            throw new BTSLBaseException("AutoO2CProcess", "process", "product list size is" + prodList.size());
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
          CommissionProfileSetVO commissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(p_con, p_commProfileSetId, p_currentDate);;
            latestCommProfileVersion = commissionProfileSetVO.getCommProfileVersion();
        } catch (BTSLBaseException bex) {
            if (PretupsErrorCodesI.COMM_PROFILE_SETVERNOT_ASSOCIATED.equals(bex.getMessage())) {
                throw new BTSLBaseException("AutoO2CProcess", "process", "commission profile version error",bex);
            }

            _log.error("loadO2CXfrProductList", "BTSLBaseException " + bex.getMessage());
            throw bex;
        }

        // if there is no commission profile version exist upto the current date
        // show the error message.
        if (BTSLUtil.isNullString(latestCommProfileVersion)) {
            throw new BTSLBaseException("AutoO2CProcess", "process", "no commission profile version exists" + latestCommProfileVersion);
        }
        Boolean isTransationTypeAlwd = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
        String type = (isTransationTypeAlwd)?PretupsI.TRANSFER_TYPE_O2C:PretupsI.ALL;
		String paymentMode = PretupsI.ALL;
        final ArrayList commissionProfileProductList = commissionProfileDAO.loadCommissionProfileProductsList(p_con, p_commProfileSetId, latestCommProfileVersion, type, paymentMode);

        // if list is empty send the error message
        if (commissionProfileProductList == null || commissionProfileProductList.isEmpty()) {
            throw new BTSLBaseException("AutoO2CProcess", "process", "commission profile product list size is 0 ");
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
            throw new BTSLBaseException("AutoO2CProcess", "process", "product list size is" + productList.size() + "for commission profile");
        }

		//added for pvg defect:983 issue 2: Multiple of value defined in commission profile is getting validated
        if((requestedQty % channelTransferItemsVO.getTransferMultipleOf())!=0 )
        {
        	throw new BTSLBaseException("AutoO2CProcess", "process", "requested quantity is not multiple of  defined in commission profile");
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
    private ArrayList<ChannelTransferItemsVO> filterProductWithTransferRule(ArrayList<ChannelTransferItemsVO> p_productList, ArrayList<ListValueVO> p_productListWithXfrRule) {
        final String METHOD_NAME = "filterProductWithTransferRule";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered p_productList: " + p_productList.size() + " p_productListWithXfrRule: " + p_productListWithXfrRule.size());
        }
        ChannelTransferItemsVO channelTransferItemsVO = null;
        ListValueVO listValueVO = null;
        final ArrayList<ChannelTransferItemsVO> tempList = new ArrayList<ChannelTransferItemsVO>();
        for (int m = 0, n = p_productList.size(); m < n; m++) {
            channelTransferItemsVO = p_productList.get(m);
            for (int i = 0, k = p_productListWithXfrRule.size(); i < k; i++) {
                listValueVO = p_productListWithXfrRule.get(i);
                if (channelTransferItemsVO.getProductCode().equals(listValueVO.getValue())) {
                    channelTransferItemsVO.setRequestedQuantity(_auto_o2c_amount);
                    tempList.add(channelTransferItemsVO);
                    break;
                }
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exiting tempList: " + tempList.size());
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
        final String METHOD_NAME = "constructBalanceVOFromTxnVO";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered:NetworkStockTxnVO=>" + p_channelTransferVO);
        }
        final UserBalancesVO userBalancesVO = new UserBalancesVO();
        userBalancesVO.setUserID(p_channelTransferVO.getToUserID());
        userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
        userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
        userBalancesVO.setLastTransferOn(p_channelTransferVO.getModifiedOn());

        userBalancesVO.setUserMSISDN(p_channelTransferVO.getUserMsisdn());
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exiting userBalancesVO=" + userBalancesVO);
        }
        return userBalancesVO;
    }

    private int creditUserBalances(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean isFromWeb) throws BTSLBaseException {
        final String METHOD_NAME = "creditUserBalances";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered p_channelTransferVO : " + p_channelTransferVO + " isFromWeb " + isFromWeb);
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
            _log.debug(METHOD_NAME, "Update query:" + updateQuery);
        }

        final String insertQuery = strBuffInsert.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Insert query:" + insertQuery);
        }

        final String sqlSelect = strBuffSelect.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }

        final String insertUserThreshold = strBuffThresholdInsert.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "QUERY insertUserThreshold=" + insertUserThreshold);
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
                    if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
                        balance += channelTransferItemsVO.getReceiverCreditQty();
                    } else {
                        balance += channelTransferItemsVO.getApprovedQuantity();
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
                        if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
                            balance = channelTransferItemsVO.getReceiverCreditQty();
                        } else {
                            balance = channelTransferItemsVO.getApprovedQuantity();
                        }
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
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserDAO[creditUserBalances]", "",
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
                        _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
                        _log.errorTrace(METHOD_NAME, sqle);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoO2CProcess[creditUserBalances]",
                            p_channelTransferVO.getTransferID(), "", p_channelTransferVO.getNetworkCode(),
                            "Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
                    }// end of catch
                }
            }// for
            if (!errorList.isEmpty()) {
                if (isFromWeb) {
                    throw new BTSLBaseException(this, METHOD_NAME, errorList);
                }
                throw new BTSLBaseException(this.getClass().getName(), METHOD_NAME, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_BALANCE, errorList);
            }

            p_channelTransferVO.setEntryType(PretupsI.CREDIT);
        } catch (BTSLBaseException bbe) {
            _log.errorTrace(METHOD_NAME, bbe);
            throw bbe;
        } catch (SQLException sqe) {
            _log.error(METHOD_NAME, "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoO2CProcess[creditUserBalances]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(METHOD_NAME, "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoO2CProcess[creditUserBalances]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtInsertUserThreshold != null) {
                    psmtInsertUserThreshold.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting:  updateCount =" + updateCount);
            }
        }
        return updateCount;

    }

    private int updateOptToChannelUserInCounts(Connection p_con, ChannelTransferVO p_channelTransferVO, Date p_curDate) throws BTSLBaseException {
        final String METHOD_NAME = "updateOptToChannelUserInCounts";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered ChannelTransferVO =" + p_channelTransferVO, "p_curDate" + p_curDate);
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
        	
    		if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)).booleanValue() && p_channelTransferVO.getUserLoanVOList() !=null && p_channelTransferVO.getUserLoanVOList().size()>0) {

				Map hashmap =  ChannelTransferBL.checkUserLoanstatusAndAmount(p_con, p_channelTransferVO);
				if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true)) {
					final String args[] = { PretupsBL.getDisplayAmount((long)hashmap.get(PretupsI.WITHDRAW_AMOUNT)) };
					
					throw new BTSLBaseException("AutoO2CProcess", METHOD_NAME, PretupsErrorCodesI.LOAN_SETTLEMENT_PENDING,args);
				}

				if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
					UserLoanWithdrawBL  userLoanWithdrawBL = new UserLoanWithdrawBL();
					
					userLoanWithdrawBL.autoChannelLoanSettlement(p_channelTransferVO, PretupsI.USER_LOAN_REQUEST_TYPE,(long)hashmap.get(PretupsI.WITHDRAW_AMOUNT));
				}

			}
        	
        	
        	Map<String, Object> hashmap = ChannelTransferBL.checkSOSstatusAndAmount(p_con, countsVO,
        			p_channelTransferVO);
			if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true)) {
				
				throw new BTSLBaseException("AutoO2CProcess", METHOD_NAME, PretupsErrorCodesI.SOS_PENDING_FOR_SETTLEMENT);
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

                throw new BTSLBaseException("AutoO2CProcess", METHOD_NAME, transferCountsMessage);

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
            _log.debug(METHOD_NAME, "Exited  updateCount " + updateCount);
        }

        return updateCount;

    }

    private boolean checkResetCountersAfterPeriodChange(UserTransferCountsVO p_userTransferCountsVO, java.util.Date p_newDate) {
        final String METHOD_NAME = "checkResetCountersAfterPeriodChange";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered with transferID=" + p_userTransferCountsVO.getLastTransferID() + " USER ID=" + p_userTransferCountsVO.getUserID());
        }
        boolean isCounterChange = false;
        boolean isDayCounterChange = false;
        boolean isWeekCounterChange = false;
        boolean isMonthCounterChange = false;

        final Date previousDate = p_userTransferCountsVO.getLastTransferDate();

        if (previousDate != null) {
            final Calendar cal = BTSLDateUtil.getInstance();
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
            _log.debug(METHOD_NAME,
                "Exiting with isCounterChange=" + isCounterChange + " For transferID=" + p_userTransferCountsVO.getLastTransferID() + " USER ID=" + p_userTransferCountsVO
                    .getUserID());
        }
        return isCounterChange;

    }

    private static String transferInCountsCheck(Connection p_con, UserTransferCountsVO p_userTransferCountsVO, String p_profileID, String p_networkCode, long p_totalRequestedQtuantity) throws BTSLBaseException {
        final String METHOD_NAME = "transferInCountsCheck";
        if (_log.isDebugEnabled()) {
            _log.debug(
                METHOD_NAME,
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
            _log.debug(METHOD_NAME, "Exited ");
        }
        return null;

    }

    private UserVO loaduserMsisdn(Connection p_con, String userID) throws BTSLBaseException, java.sql.SQLException {
        final String METHOD_NAME = "loaduserMsisdn";
        if (_log.isDebugEnabled()) {
            _log.info(METHOD_NAME, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rst = null;
        final UserVO userVO = new UserVO();
        try {
            final StringBuffer queryBuf = new StringBuffer(" select msisdn from users where user_id=? ");

            final String query = queryBuf.toString();

            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Query:" + query);
            }
            pstmt = p_con.prepareStatement(query.toString());
            pstmt.setString(1, userID);

            rst = pstmt.executeQuery();
            while (rst.next()) {
                userVO.setMsisdn(rst.getString("msisdn"));
            }
        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Auto o2c TransferProcess[loaduserMsisdn]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("Auto o2cTransferProcess", METHOD_NAME, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Auto o2cTransferProcess[loaduserMsisdn]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("Auto loaduserMsisdn", METHOD_NAME, "error.general.processing");
        }// end of catch
        finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e2) {
                    _log.error(METHOD_NAME, "SQLException " + e2.getMessage());
                    _log.errorTrace(METHOD_NAME, e2);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e3) {
                    _log.errorTrace(METHOD_NAME, e3);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.info(METHOD_NAME, " Exiting user ");
            }
        }// end finally

        return userVO;
    }
}
