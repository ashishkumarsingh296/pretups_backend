package com.btsl.pretups.processes;

/*
 * AutoC2CTransferProcess.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Nilesh kumar 12/10/10 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2010 Comviva technologies Ltd.
 * for auto c2c transfer.
 */

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
//import com.btsl.common.BTSLDispatchAction;
import com.btsl.common.BTSLMessages;
import com.btsl.common.IDGeneratorDAO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.logging.AutoC2CLogger;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSWithdrawBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.LowBalanceAlertVO;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;
import com.txn.pretups.channel.profile.businesslogic.CommissionProfileTxnDAO;
import com.txn.pretups.channel.transfer.businesslogic.ChannelTransferRuleTxnDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserLoanWithdrawBL;

public class AutoC2CTransfer {
    private static final Log LOG = LogFactory.getLog(AutoC2CTransfer.class.getName());
    private long _autoC2CAmount = 0;

 
    private static OperatorUtilI calculatorI = null;
    static {
        try {
            calculatorI = (OperatorUtilI) Class.forName((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS)).newInstance();
        } catch (Exception e) {
            LOG.errorTrace("static", e);
        }
    }

    public static void main(String[] args) {
        // new new

        final String METHOD_NAME = "main";
        Connection con = null;
        boolean statusOk = false;

        Date processedUpto = null;

        final ProcessStatusDAO processDAO = new ProcessStatusDAO();
        ProcessStatusVO processVO = null;
        Date currentDate = null;
        // end new new
        try {
            final File constantsFile = new File(args[0]);

            if (!constantsFile.exists()) {
                LOG.info("main", " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                LOG.info("main", " Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

            /*
             * String taxClass =(String)
             * PreferenceCache.getSystemPreferenceValue
             * (PreferenceI.OPERATOR_UTIL_CLASS);
             * try
             * {
             * calculatorI = (OperatorUtilI)
             * Class.forName(taxClass).newInstance();
             * }
             * catch(Exception e)
             * {
             * LOG.errorTrace(METHOD_NAME,e);
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
             * EventStatusI
             * .RAISED,EventLevelI.FATAL,"ChannelTransferBL[initialize]"
             * ,"","",""
             * ,"Exception while loading the class at the call:"+e.getMessage
             * ());
             * }
             */
        }// end try
        catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("AutoC2CTransferProcess [main] ", "Error in Loading Configuration files ...........................: " + ex);
            }
            LOG.errorTrace(METHOD_NAME, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }// end catch
        try {
            // new new
            currentDate = new Date();
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            // Getting database connection
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("process", " DATABASE Connection is NULL. ");
                }
                throw new BTSLBaseException("AutoC2CTransferProcess", "process", "Not able to get the connection.");
            }
            // new new
            final ProcessBL processBL = new ProcessBL();// This class is used to
            // check
            // the status of the process
            // ProcessStatusDAO processDAO=new ProcessStatusDAO(); //This class
            // used to implement the process related business logics.
            processVO = processBL.checkProcessUnderProcess(con, ProcessI.AUTO_C2C_TRANSFER_PROCESS);// This
            // method
            // is
            // used
            // to
            // check
            // the
            // process
            // status
            statusOk = processVO.isStatusOkBool();// this variable stores the
            // value of processVO as true
            // or false.
            if (statusOk) {
                con.commit();
                // beforeInterval=(int)processVO.getBeforeInterval()/(60*24);//converted
                // the before Interval in days.
                processedUpto = processVO.getExecutedUpto();
                if (processedUpto != null) {

                    new AutoC2CTransfer().process(con, loadMinBalanceUsers(con));
                    processVO.setExecutedOn(new Date());
                    processVO.setExecutedUpto(new Date());
                    // After successful completion of the process, mark the
                    // status as complete in process_status table.
                    if (processDAO.updateProcessDetail(con, processVO) > 0) {
                        con.commit();
                    }
                } else {
                    throw new BTSLBaseException("AutoC2CTransferProcess", "process", PretupsErrorCodesI.AUTO_C2C_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            } else {
                throw new BTSLBaseException("AutoC2CTransferProcess", "process", "Process is already running..");
                // end new new
            }

            // new AutoC2CTransferProcess().process();
        } catch (BTSLBaseException bse) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("main", " " + bse.getMessage());
            }
            LOG.errorTrace(METHOD_NAME, bse);
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("main", " " + e.getMessage());
            }
            LOG.errorTrace(METHOD_NAME, e);
        } finally {
            if (statusOk) {
                processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                try {
                    if (processDAO.updateProcessDetail(con, processVO) > 0) {
                        con.commit();
                    } else {
                        con.rollback();
                    }
                } catch (Exception e) {
                    LOG.error("process", " Exception in update process detail" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoC2CTRansferProcess[process] ", "", "", "",
                        "Exception in update process detail for Process ID=" + ProcessI.AUTO_C2C_TRANSFER_PROCESS + " :" + e.getMessage());
                    LOG.errorTrace(METHOD_NAME, e);
                }
            }
            // end new new
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(METHOD_NAME, e);
            }

            if (LOG.isDebugEnabled()) {
                LOG.info("main", "Exiting");
            }
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "AutoC2CTRansferProcess[process]","","",""," [Auto C2C Process is Completed.");
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
			}
            ConfigServlet.destroyProcessCache();
        }
    }// end main

    public void process(Connection p_con, ArrayList userList) throws BTSLBaseException {

        final String METHOD_NAME = "process";
        ChannelUserDAO channelUserDAO = null;
        ChannelUserVO channelUserVO = null;
        UserTransferCountsVO countVO = null;
        // new nilesh
        UserTransferCountsVO fromCountVO = null;

        ChannelTransferRuleTxnDAO channelTransferRuleTxnDAO = null;
        TransferProfileDAO transferProfileDAO = null;
        TransferProfileVO profileVO = null;
        CommissionProfileDAO commissionProfileDAO = null;
        CommissionProfileTxnDAO commissionProfileTxnDAO = null;
        ChannelUserVO parentUserVO = null;
        UserBalancesDAO userBalancesDAO = null;
        C2sBalanceQueryVO parentBalancesVO = null;
        ChannelTransferVO channelTransferVo = null;
        ChannelUserTransferDAO channelUserTransferDAO = null;
        ChannelTransferItemsVO transferItemsVO = null;
        ChannelTransferDAO channelTransferDAO = null;
        ChannelTransferItemsVO channelTransferItemsVO = null;
        RequestVO p_requestVO = null;
        TransferProfileProductVO transferProfileProductVO = null;
        TransferProfileVO senderProfileVO = null;
        final Date currentDate = new Date();

        boolean isTransferExist = false;
        boolean isCommissionExistForReceiver = false;
        boolean isCommissionExistForSender = false;
        try {
            // Getting database connection

            /*
             * if(p_con==null)
             * {
             * if (LOG.isDebugEnabled())
             * LOG.debug("process"," DATABASE Connection is NULL. ");
             * throw new
             * BTSLBaseException(this,"process","Not able to get the connection."
             * );
             * }
             */
            final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
            channelUserDAO = new ChannelUserDAO();
            channelUserVO = new ChannelUserVO();
            countVO = new UserTransferCountsVO();
            // new nilesh
            fromCountVO = new UserTransferCountsVO();

            channelTransferRuleTxnDAO = new ChannelTransferRuleTxnDAO();
            transferProfileDAO = new TransferProfileDAO();
            profileVO = new TransferProfileVO();
            commissionProfileDAO = new CommissionProfileDAO();
            commissionProfileTxnDAO = new CommissionProfileTxnDAO();
            parentUserVO = new ChannelUserVO();
            userBalancesDAO = new UserBalancesDAO();
            channelUserTransferDAO = new ChannelUserTransferDAO();

            transferItemsVO = new ChannelTransferItemsVO();
            channelTransferDAO = new ChannelTransferDAO();
            p_requestVO = new RequestVO();
            KeyArgumentVO keyArgumentVO = null;
            transferProfileProductVO = new TransferProfileProductVO();
            senderProfileVO = new TransferProfileVO();
            // TransferProfileDAO profileDAO = new TransferProfileDAO();

            // method call balanceAlert to acquire information that to what
            // number alert should be sent
            // ArrayList<LowBalanceAlertVO> userList = balanceAlertUsers(p_con);
            LowBalanceAlertVO lowBalanceAlertVO = new LowBalanceAlertVO();
            String parentMsisdn = null;
            final UserDAO userDAO = new UserDAO();
            String[] arr = null;
            Locale locale = null;
            BTSLMessages btslMessage = null;
            BTSLMessages btslMessage1 = null;
            PushMessage pushMessage = null;
            PushMessage pushMessage1 = null;
            String[] arr1 = null;
            ArrayList channelTransferItemVOList = null;

            for (int i = 0, j = userList.size(); i < j; i++) {
                channelTransferVo = new ChannelTransferVO();
                try {
                    _autoC2CAmount = 0;
                    lowBalanceAlertVO = (LowBalanceAlertVO) userList.get(i);
                    try {
                        _autoC2CAmount = Long.parseLong(PretupsBL.getDisplayAmount(Long.parseLong(lowBalanceAlertVO.getAutoc2cquantity())));// Long.valueOf((String)lowBalanceAlertVO.getAutoc2cquantity());
                    } catch (Exception e) {
                        p_con.rollback();
                        LOG.errorTrace(METHOD_NAME, e);
                        continue;
                    }

                    // _autoC2CAmount=((Integer)PreferenceCache.getControlPreference(PreferenceI.AUTO_C2C_TRANSFER_AMT,
                    // lowBalanceAlertVO.getNetworkCode(),
                    // lowBalanceAlertVO.getCategoryCode())).intValue();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("process", "_autoC2CAmount " + _autoC2CAmount);
                    }
                    channelUserVO = (ChannelUserVO) channelUserDAO.loadChannelUserDetails(p_con, lowBalanceAlertVO.getMsisdn());
                    // checking receiver's IN suspend
                    if (channelUserVO.getInSuspend().equalsIgnoreCase(PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                "process",
                                "Auto c2c is not possible as Receiver is IN suspend, receiver user ID: " + lowBalanceAlertVO.getUserId() + " receiver's IN suspend: " + channelUserVO
                                    .getInSuspend());
                        }
                        AutoC2CLogger
                            .log(
                                lowBalanceAlertVO,
                                "FAIL",
                                "Auto c2c is not possible as Receiver is IN suspend, receiver user ID: " + lowBalanceAlertVO.getUserId() + "receiver's IN suspend: " + channelUserVO
                                    .getInSuspend());
                        continue;
                    }
                    // To check if child user is allowed to receive in
                    // User_allowed_status
                    boolean userReceiverAllowed = false;
                    String args[] = null;

					parentMsisdn=channelUserDAO.loadParentMsisdn(p_con,lowBalanceAlertVO.getParentUserId());
					parentUserVO = (ChannelUserVO)channelUserDAO.loadChannelUserDetails(p_con,parentMsisdn);
					UserPhoneVO userPhoneVO = userDAO.loadUserPhoneVO(p_con,lowBalanceAlertVO.getParentUserId());
                    final UserStatusVO receiverStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(),
                        channelUserVO.getUserType(), PretupsI.REQUEST_SOURCE_TYPE_WEB);
                    {
                        if (receiverStatusVO != null) {
                            final String userStatusAllowed = receiverStatusVO.getUserReceiverAllowed();
                            final String status[] = userStatusAllowed.split(",");
                            for (int s = 0; s < status.length; s++) {
                                if (status[s].equals(channelUserVO.getStatus())) {
                                    userReceiverAllowed = true;
                                }
                            }
                        }
                        if (userReceiverAllowed == false) {
                        	pushMessage=new PushMessage(parentUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_CHNL_CONFIGURATION_ERROR_SNDR,new String[]{channelUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),parentUserVO.getNetworkCode());
    						pushMessage.push();
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("process", "Auto c2c is not possible as the child user is not allowed to receive ");
                            }
                            AutoC2CLogger.log(lowBalanceAlertVO, "FAIL",
                                "Auto c2c is not possible as the child user is not allowed to receive, receiver user ID: " + lowBalanceAlertVO.getUserId());
                            continue;
                        }
                    }
                /*    parentMsisdn = channelUserDAO.loadParentMsisdn(p_con, lowBalanceAlertVO.getParentUserId());
                    parentUserVO = (ChannelUserVO) channelUserDAO.loadChannelUserDetails(p_con, parentMsisdn);*/
                   

                    // 03/12/10
                    // if sender is suspended, then he can not transfer to the
                    // receiver.
                    if (parentUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND) || parentUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_DELETED) || parentUserVO
                        .getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_CANCELED)) {
                    	pushMessage=new PushMessage(parentUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_CHNL_CONFIGURATION_ERROR_SNDR,new String[]{channelUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),parentUserVO.getNetworkCode());
						pushMessage.push();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("process", "Auto c2c is not possible as Sender status is suspended or deleted or cancelled, sender status :" + parentUserVO.getStatus());
                        }
                        AutoC2CLogger.log(lowBalanceAlertVO, "FAIL",
                            "Auto c2c is not possible as Sender status is suspended or deleted or cancelled, sender status: " + parentUserVO.getStatus());
                        continue;
                    }
                    // end 03/12/10
                    // checking parent out suspend
                    if (parentUserVO.getOutSuspened().equalsIgnoreCase(PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND)) {
                        // throw new
                        // BTSLBaseException("process",PretupsErrorCodesI.CHNL_ERROR_SENDER_OUT_SUSPEND);
                    	pushMessage=new PushMessage(parentUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_CHNL_CONFIGURATION_ERROR_SNDR,new String[]{channelUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),parentUserVO.getNetworkCode());
						pushMessage.push();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                "process",
                                "Auto c2c is not possible as Parent's Out suspend, sender msisdn: " + lowBalanceAlertVO.getParentMsisdn() + "parent user ID :" + lowBalanceAlertVO
                                    .getParentUserId());
                        }
                        AutoC2CLogger.log(lowBalanceAlertVO, "FAIL",
                            "Auto c2c is not possible as Parent's Out suspend, sender msisdn :" + lowBalanceAlertVO.getParentMsisdn() + "parent user ID :" + lowBalanceAlertVO
                                .getParentUserId());
                        continue;
                    }

                    // To check parent is allowed to send in User_allowed_status
                    boolean senderStatusAllowed = false;
                    final UserStatusVO senderStatusVO = (UserStatusVO) UserStatusCache.getObject(parentUserVO.getNetworkID(), parentUserVO.getCategoryCode(), parentUserVO
                        .getUserType(), PretupsI.REQUEST_SOURCE_TYPE_WEB);
                    {
                        if (senderStatusVO != null) {
                            final String userStatusAllowed = senderStatusVO.getUserSenderAllowed();
                            final String status[] = userStatusAllowed.split(",");
                            for (int s = 0; s < status.length; s++) {
                                if (status[s].equals(parentUserVO.getStatus())) {
                                    senderStatusAllowed = true;
                                }
                            }
                        }
                        if (senderStatusAllowed == false) {
                        	pushMessage=new PushMessage(parentUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_CHNL_CONFIGURATION_ERROR_SNDR,new String[]{channelUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),parentUserVO.getNetworkCode());
							pushMessage.push();
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("process", "Auto c2c is not possible as the parent user is not allowed to send ");
                            }
                            AutoC2CLogger.log(lowBalanceAlertVO, "FAIL",
                                "Auto c2c is not possible as the parent user is not allowed to send, receiver user ID: " + lowBalanceAlertVO.getUserId());
                            continue;
                        }
                    }
                    final String fromUserID = lowBalanceAlertVO.getParentUserId();
                    final String toUserID = lowBalanceAlertVO.getUserId();
                    // transfer rule exists or not between sender and receiver
                    isTransferExist = channelTransferRuleTxnDAO.isTransferRuleExists(p_con, fromUserID, toUserID);
                    if (!isTransferExist) {
                    	pushMessage=new PushMessage(parentUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_CHNL_CONFIGURATION_ERROR_SNDR,new String[]{channelUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),parentUserVO.getNetworkCode());
						pushMessage.push();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                "process",
                                "Auto c2c is not possible as transfer rule does not exist between sender and receiver, receiver user ID :" + lowBalanceAlertVO.getUserId() + "parent user ID :" + lowBalanceAlertVO
                                    .getParentUserId());
                        }
                        AutoC2CLogger
                            .log(
                                lowBalanceAlertVO,
                                "FAIL",
                                "Auto c2c is not possible as transfer rule does not exist between sender and receiver, receiver user ID :" + lowBalanceAlertVO.getUserId() + "parent user ID :" + lowBalanceAlertVO
                                    .getParentUserId());
                        continue;
                    }
                    // channel user IN count,IN amount is greater or not than
                    // the max IN count and IN amount

                    // new nilesh
                    fromCountVO = (UserTransferCountsVO) userTransferCountsDAO.loadTransferCounts(p_con, fromUserID, false);
                    // end
                    countVO = (UserTransferCountsVO) userTransferCountsDAO.loadTransferCounts(p_con, channelUserVO.getUserID(), false);
                    profileVO = (TransferProfileVO) transferProfileDAO.loadTransferProfileThroughProfileID(p_con, lowBalanceAlertVO.getProfileID(), channelUserVO
                        .getNetworkID(), channelUserVO.getCategoryCode(), true);
                    if ((countVO.getDailyInCount() >= profileVO.getDailyInCount()) || (countVO.getDailyInValue() >= profileVO.getDailyInValue()) || (countVO
                        .getMonthlyInCount() >= profileVO.getMonthlyInCount()) || (countVO.getMonthlyInValue() >= profileVO.getMonthlyInValue()) || (countVO
                        .getWeeklyInCount() >= profileVO.getWeeklyInCount()) || (countVO.getWeeklyInValue() >= profileVO.getWeeklyInValue())) {
                    	pushMessage=new PushMessage(parentUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_CHNL_CONFIGURATION_ERROR_SNDR,new String[]{channelUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),parentUserVO.getNetworkCode());
						pushMessage.push();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                "process",
                                "Auto c2c is not possible as receiver's IN count,IN amount is greater than the max IN count and IN amount,receiver user id :" + lowBalanceAlertVO
                                    .getUserId());
                        }
                        AutoC2CLogger.log(lowBalanceAlertVO, "FAIL",
                            "Auto c2c is not possible as receiver's IN count,IN amount is greater than the max IN count and IN amount,receiver user id :" + lowBalanceAlertVO
                                .getUserId());
                        continue;
                    }
                    // receiver's commission profile is associated or not
                    final String status = "'" + PretupsI.STATUS_DELETE + "'";
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("process", "receiver's id=" + channelUserVO.getUserID() + "receiver's status=" + channelUserVO.getStatus());
                    }

                    // (Discussed with Ved Sir)if receiver is suspended,but his
                    // commission profile is associated,then he can be credited
                    // . But if, he is deleted then no c2c is possible.
                    isCommissionExistForReceiver = commissionProfileTxnDAO.isCommissionProfileIDAssociatedForAutoC2C(p_con, channelUserVO.getUserID(), status);
                    if (!isCommissionExistForReceiver) {
                    	pushMessage=new PushMessage(parentUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_CHNL_CONFIGURATION_ERROR_SNDR,new String[]{channelUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),parentUserVO.getNetworkCode());
						pushMessage.push();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("process", "Auto c2c is not possible as receiver's commission profile is not associated,receiver user id :" + lowBalanceAlertVO
                                .getUserId());
                        }
                        AutoC2CLogger.log(lowBalanceAlertVO, "FAIL",
                            "Auto c2c is not possible as receiver's commission profile is not associated,receiver user id :" + lowBalanceAlertVO.getUserId());
                        continue;
                    }
                    // 03/12/10
                    // sender's commission profile is associated or not
                    isCommissionExistForSender = commissionProfileTxnDAO.isCommissionProfileIDAssociatedForAutoC2C(p_con, parentUserVO.getUserID(), status);
                    if (!isCommissionExistForSender) {
                    	pushMessage=new PushMessage(parentUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_CHNL_CONFIGURATION_ERROR_SNDR,new String[]{channelUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),parentUserVO.getNetworkCode());
						pushMessage.push();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("process", "Auto c2c is not possible as sender's commission profile is not associated,sender user id :" + parentUserVO.getUserID());
                        }
                        AutoC2CLogger.log(lowBalanceAlertVO, "FAIL",
                            "Auto c2c is not possible as sender's commission profile is not associated,sender user id :" + parentUserVO.getUserID());
                        continue;
                    }
                    final CommissionProfileSetVO  commissionProfileSetReceiverVO =commissionProfileTxnDAO.loadCommProfileSetDetails(p_con,channelUserVO.getCommissionProfileSetID(), currentDate);
                    // For commission profile applicable less than and equal to the current date.
                    /*final String commissionProfileVersionForReceiver = commissionProfileTxnDAO.loadCommProfileSetLatestVersion(p_con, channelUserVO
                        .getCommissionProfileSetID(), currentDate);*/
                    final String commissionProfileVersionForReceiver = commissionProfileSetReceiverVO.getCommProfileVersion();
                    // Commission profile is not applicable for the date.
                   
                    if (commissionProfileVersionForReceiver == null) {
                    	pushMessage=new PushMessage(parentUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_CHNL_CONFIGURATION_ERROR_SNDR,new String[]{channelUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),parentUserVO.getNetworkCode());
						pushMessage.push();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                "process",
                                "Auto c2c is not possible as receiver's commission profile is associated for the future version, commissionProfileVersionForReceiver " + commissionProfileVersionForReceiver);
                        }
                        AutoC2CLogger
                            .log(
                                lowBalanceAlertVO,
                                "FAIL",
                                "Auto c2c is not possible as receiver's commission profile is associated for the future version, commissionProfileVersionForReceiver " + commissionProfileVersionForReceiver);
                        continue;
                    }
                    CommissionProfileSetVO commissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(p_con, parentUserVO.getCommissionProfileSetID(), currentDate);
                    final String commissionProfileVersionForSender = commissionProfileSetVO.getCommProfileVersion();
                    // if Commission profile is not applicable for the future
                    // date.
                    if (commissionProfileVersionForSender == null) {
                    	pushMessage=new PushMessage(parentUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_CHNL_CONFIGURATION_ERROR_SNDR,new String[]{channelUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),parentUserVO.getNetworkCode());
						pushMessage.push();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                "process",
                                "Auto c2c is not possible as sender's commission profile is associated for the future version,commissionProfileVersionForSender " + commissionProfileVersionForSender);
                        }
                        AutoC2CLogger
                            .log(
                                lowBalanceAlertVO,
                                "FAIL",
                                "Auto c2c is not possible as sender's commission profile is associated for the future version,commissionProfileVersionForSender " + commissionProfileVersionForSender);
                        continue;
                    }
                    // receiver's transfer profile is associated or not
                    boolean isExistProfileForReceiver = false;

                    // isExistProfileForReceiver =
                    // transferProfileDAO.isTransferProfileExistForCategoryCodeForAutoC2C(p_con,channelUserVO.getCategoryCode(),channelUserVO.getNetworkID(),profileVO.getParentProfileID());
                    isExistProfileForReceiver = transferProfileDAO.isTransferProfileExistForCategoryCodeForAutoC2C(p_con, channelUserVO.getCategoryCode(), channelUserVO
                        .getNetworkID(), profileVO.getParentProfileID(), channelUserVO.getTransferProfileID());
                    if (!isExistProfileForReceiver) {
                    	pushMessage=new PushMessage(parentUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_CHNL_CONFIGURATION_ERROR_SNDR,new String[]{channelUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),parentUserVO.getNetworkCode());
						pushMessage.push();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("process",
                                "Auto c2c is not possible as receiver's transfer profile is not associated,receiver's transfer profile  :" + isExistProfileForReceiver);
                        }
                        AutoC2CLogger.log(lowBalanceAlertVO, "FAIL",
                            "Auto c2c is not possible as receiver's transfer profile is not associated,receiver's transfer profile  :" + isExistProfileForReceiver);
                        continue;
                    }
                    // sender's transfer profile is associated or not
                    senderProfileVO = (TransferProfileVO) transferProfileDAO.loadTransferProfileThroughProfileID(p_con, parentUserVO.getTransferProfileID(), parentUserVO
                        .getNetworkID(), parentUserVO.getCategoryCode(), true);
                    boolean isExistProfileForSender = false;
                    isExistProfileForSender = transferProfileDAO.isTransferProfileExistForCategoryCodeForAutoC2C(p_con, parentUserVO.getCategoryCode(), parentUserVO
                        .getNetworkID(), senderProfileVO.getParentProfileID(), parentUserVO.getTransferProfileID());
                    if (!isExistProfileForSender) {
                    	pushMessage=new PushMessage(parentUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_CHNL_CONFIGURATION_ERROR_SNDR,new String[]{channelUserVO.getMsisdn()}),null,null,new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()),parentUserVO.getNetworkCode());
						pushMessage.push();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("process",
                                "Auto c2c is not possible as sender's transfer profile is not associated,sender's transfer profile  :" + isExistProfileForSender);
                        }
                        AutoC2CLogger.log(lowBalanceAlertVO, "FAIL",
                            "Auto c2c is not possible as sender's transfer profile is not associated,sender's transfer profile  :" + isExistProfileForSender);
                        continue;
                    }
                    // end 03/12/10
                    // load sender's balance for all the associated products.
                    final ArrayList balanceList = userBalancesDAO.loadUserBalances(p_con, parentUserVO.getUserID());
                    // String product="ETOPUP";
                    final String product = lowBalanceAlertVO.getProductCode();
                    if ((balanceList != null) && (balanceList.size() > 0)) {
                        for (int k = 0, l = balanceList.size(); k < l; k++) {
                            parentBalancesVO = (C2sBalanceQueryVO) balanceList.get(k);
                            if (parentBalancesVO.getProductCode().equalsIgnoreCase(product)) {
                                parentBalancesVO = null;
                                parentBalancesVO = (C2sBalanceQueryVO) balanceList.get(k);
                                break;
                            }
                        }
                    }
                    // check if sender's balance is below or above the auto C2C
                    // transfer value (defined based on control preference)
                    if(parentBalancesVO.getBalance()<PretupsBL.getSystemAmount(Long.toString(_autoC2CAmount))) {
                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_ALLOWED))).booleanValue()) {
                            final String smsKey = PretupsErrorCodesI.AUTO_CHNL_ERROR_SNDR_INSUFF_BALANCE;
                            arr = new String[2];
                            final UserPhoneVO phoneVO = userDAO.loadUserPhoneVO(p_con, lowBalanceAlertVO.getParentUserId());
                            arr[0] = parentUserVO.getMsisdn();
                            arr[1] = PretupsBL.getDisplayAmount(parentBalancesVO.getBalance());
                            locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
                            btslMessage = new BTSLMessages(smsKey, arr);
                            pushMessage = new PushMessage(lowBalanceAlertVO.getMsisdn(), btslMessage, null, null, locale, parentUserVO.getNetworkCode());
                            pushMessage.push();

                            arr1 = new String[1];
                            arr1[0] = PretupsBL.getDisplayAmount(parentBalancesVO.getBalance());
                            btslMessage1 = new BTSLMessages(PretupsErrorCodesI.SNDR_INSUFF_BALANCE, arr1);
                            pushMessage1 = new PushMessage(parentUserVO.getMsisdn(), btslMessage1, null, null, locale, parentUserVO.getNetworkCode());
                            pushMessage1.push();
                        }
                        continue;
                    }

                    // do the auto c2c transfer
                    channelTransferVo.setTransactionMode(PretupsI.AUTO_C2C_TXN_MODE);
                    channelTransferVo.setType(PretupsI.CHANNEL_TYPE_C2C);
                    channelTransferVo.setNetworkCode(channelUserVO.getNetworkID());
                    channelTransferVo.setNetworkCodeFor(channelUserVO.getNetworkID());
                    channelTransferVo.setCreatedOn(currentDate);
                    ChannelTransferBL.genrateChnnlToChnnlTrfID(channelTransferVo);
                    channelTransferVo.getTransferID();
                    channelTransferVo.setActiveUserId(channelUserVO.getUserID());
                    channelTransferVo.setProductCode(parentBalancesVO.getProductCode());
                    channelTransferVo.setFromUserID(fromUserID);
                    channelTransferVo.setToUserID(toUserID);
                    channelTransferVo.setSenderTxnProfile(parentUserVO.getTransferProfileID());
                    channelTransferVo.setFromUserCode(parentUserVO.getUserCode());

                    channelTransferItemVOList = channelUserTransferDAO.parentBalanceUpdateValue(p_con, fromUserID, _autoC2CAmount, product);
                    if ((channelTransferItemVOList != null) && (channelTransferItemVOList.size() > 0)) {
                        for (int m = 0, n = channelTransferItemVOList.size(); m < n; m++) {
                            transferItemsVO = (ChannelTransferItemsVO) channelTransferItemVOList.get(m);
                            if (transferItemsVO.getProductCode().equalsIgnoreCase(product)) {
                                transferItemsVO = null;
                                transferItemsVO = (ChannelTransferItemsVO) channelTransferItemVOList.get(m);
                                break;
                            }
                        }
                    }

                    channelTransferVo.setChannelTransferitemsVOList(channelTransferItemVOList);
                    channelTransferVo.setTransferMRP(_autoC2CAmount);
                    channelTransferVo.setTransferType(PretupsI.CHANNEL_TYPE_C2C);
                    channelTransferVo.setCategoryCode(parentUserVO.getCategoryCode());
                    channelTransferVo.setReceiverCategoryCode(lowBalanceAlertVO.getCategoryCode());
                    channelTransferVo.setRequestedQuantity(Long.valueOf(transferItemsVO.getRequestedQuantity()));
					channelTransferVo.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
		            channelTransferVo.setOtfFlag(true);

                    // _channelTransferVo.setRequestedQuantity(Long.valueOf(transferItemsVO.getRequestedQuantity())/((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
                    // Now load the taxes and calculate the taxes on the
                    // requested product quantity for the transaction
                    // /
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("process", "Calculate Tax of products Start ");
                    }
                    ChannelTransferBL.loadAndCalculateTaxOnProducts(p_con, channelUserVO.getCommissionProfileSetID(), commissionProfileVersionForReceiver, channelTransferVo,
                        false, null, PretupsI.TRANSFER_TYPE_C2C);
						
					channelTransferItemVOList = channelTransferVo.getChannelTransferitemsVOList(); // 23
                    
                    if ((channelTransferItemVOList != null) && (channelTransferItemVOList.size() > 0)) {
                        for (int m = 0, n = channelTransferItemVOList.size(); m < n; m++) {
                            transferItemsVO = (ChannelTransferItemsVO) channelTransferItemVOList.get(m);
                            if (transferItemsVO.getProductCode().equalsIgnoreCase(product)) {
                                transferItemsVO = null;
                                transferItemsVO = (ChannelTransferItemsVO) channelTransferItemVOList.get(m);
                                break;
                            }
                        }
                    }
                    
                    
                    
                    if(null !=transferItemsVO &&(PretupsBL.getSystemAmount(channelTransferVo.getRequestedQuantity()) % transferItemsVO.getTransferMultipleOf())!=0 )
                    {
                                 AutoC2CLogger.log(lowBalanceAlertVO, "FAIL",
                            "Auto c2c is not possible as requested quentity is not in the multiple of defined in commission profile  :" + transferItemsVO.getTransferMultipleOf());
                        continue;
                        
                    }

                    // check whether receiver is already above the threshold or
                    // not.If recevier is above the threshold then no auto c2c
                    // will happen.
                    // new nilesh
                    int creditCount = 0;
                    int debitCount = 0;

                    int updateCount = 0;

                    transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(lowBalanceAlertVO.getProfileID(), channelTransferVo.getProductCode());
                    if (lowBalanceAlertVO.getBalance() > transferProfileProductVO.getAltBalanceLong()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("process", "delete receiver's entry from user_threshold_counter table");
                        }
                        // modified by deepika aggarwal while pretups 6.0 code
                        // optimisation
                        // deleteCount =
                        // channelUserDAO.deleteChildUser(p_con,lowBalanceAlertVO.getUserId());
                        channelUserDAO.deleteChildUser(p_con, lowBalanceAlertVO.getUserId());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("process",
                                "receiver's balance is " + lowBalanceAlertVO.getBalance() + " which is above the alerting balance,so auto c2c transfer is not possible.");
                        }
                        AutoC2CLogger.log(lowBalanceAlertVO, "FAIL",
                            " receiver's balance is " + lowBalanceAlertVO.getBalance() + " which is above the alerting balance,so auto c2c transfer is not possible.");
                        continue;
                    }
                    if (lowBalanceAlertVO.getBalance() <= transferProfileProductVO.getAltBalanceLong()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("process", "debit sender ");
                        }
                        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)).booleanValue() ) {
                			
                				channelTransferVo.setUserLoanVOList(parentUserVO.getUserLoanVOList());
                		
                        } 
                        else if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
                        	List<ChannelSoSVO> chnlSoSVOList = new ArrayList<ChannelSoSVO>();
                        	chnlSoSVOList.add(new ChannelSoSVO(parentUserVO.getUserID(),parentUserVO.getMsisdn(),parentUserVO.getSosAllowed(),parentUserVO.getSosAllowedAmount(),parentUserVO.getSosThresholdLimit()));
                        	channelTransferVo.setChannelSoSVOList(chnlSoSVOList);
                        }
                        debitCount = channelUserDAO.debitUserBalances(p_con, channelTransferVo, false, null);
                        
                        

                        channelTransferVo.setToUserID(toUserID);
                        channelTransferVo.setReceiverTxnProfile(channelUserVO.getTransferProfileID());

                        // receiver credit
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("process", "credit receiver ");
                        }

                        creditCount = channelUserDAO.creditUserBalances(p_con, channelTransferVo, false, null);

                    }
                    /*if ((lowBalanceAlertVO.getBalance() + (_autoC2CAmount)) > transferProfileProductVO.getAltBalanceLong()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("process", "delete receiver's entry from user_threshold_counter table");
                        }
                        try {
                            // modified by deepika aggarwal while pretups 6.0
                            // code optimisation
                            // deleteCount =
                            // channelUserDAO.deleteChildUser(p_con,lowBalanceAlertVO.getUserId());
                            channelUserDAO.deleteChildUser(p_con, lowBalanceAlertVO.getUserId());

                        } catch (Exception e) {
                            p_con.rollback();
                            LOG.errorTrace(METHOD_NAME, e);
                            continue;
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("process", "update receiver's entry in user_threshold_counter table");
                        }
                        // int
                        // upCount=channelUserDAO.updateChildUser(con,lowBalanceAlertVo,_channelTransferVo.getRequestedQuantity());
                        try {
                            // modified by deepika aggarwal while pretups 6.0
                            // code optimisation
                            // upCount=channelUserDAO.updateChildUser(p_con,lowBalanceAlertVO,transferItemsVO.getRequiredQuantity());
                            channelUserDAO.updateChildUser(p_con, lowBalanceAlertVO, transferItemsVO.getRequiredQuantity());
                        } catch (Exception e) {
                            p_con.rollback();
                            LOG.errorTrace(METHOD_NAME, e);
                            continue;
                        }
                    }*/
                    channelTransferVo.setGraphicalDomainCode(parentUserVO.getGeographicalCode());
                    channelTransferVo.setDomainCode(parentUserVO.getDomainID());
                    channelTransferVo.setSenderGradeCode(parentUserVO.getUserGrade());
                    channelTransferVo.setReceiverGradeCode(channelUserVO.getUserGrade());
                    channelTransferVo.setReferenceNum(parentUserVO.getReferenceID());
                    channelTransferVo.setCommProfileSetId(channelUserVO.getCommissionProfileSetID());
                    // channelTransferVo.setCommProfileVersion(channelUserVO.getCommissionProfileSetVersion());
                    channelTransferVo.setCommProfileVersion(commissionProfileVersionForReceiver);
                    channelTransferVo.setDualCommissionType(commissionProfileSetReceiverVO.getDualCommissionType());
                    channelTransferVo.setCreatedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
                    channelTransferVo.setModifiedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
                    channelTransferVo.setTransferInitatedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
                    channelTransferVo.setTransferDate(currentDate);
                    channelTransferVo.setCreatedOn(currentDate);
                    channelTransferVo.setModifiedOn(currentDate);
                    channelTransferVo.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                    channelTransferVo.setType(PretupsI.CHANNEL_TYPE_C2C);
                    channelTransferVo.setSource(PretupsI.REQUEST_SOURCE_SYSTEM); // as
                    // discussed
                    // with
                    // Ved
                    // Sir,
                    channelTransferVo.setReceiverCategoryCode(channelUserVO.getCategoryCode());
                    channelTransferVo.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);

                    channelTransferVo.setTransferType(PretupsI.CHANNEL_TYPE_C2C);

                    channelTransferVo.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                    channelTransferVo.setControlTransfer(PretupsI.YES);
                    channelTransferVo.setToUserCode(channelUserVO.getMsisdn());
                    channelTransferVo.setReceiverDomainCode(channelUserVO.getDomainID());
                    channelTransferVo.setReceiverGgraphicalDomainCode(channelUserVO.getGeographicalCode());
                    channelTransferVo.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
                    channelTransferVo.setRequestGatewayCode(PretupsI.GATEWAY_TYPE_WEB);
                    channelTransferVo.setRequestGatewayType(PretupsI.GATEWAY_TYPE_WEB);
                    // insert the TXN data in the parent and child tables.
                    // To avoid report issue..
                    channelTransferVo.setTransferMRP(PretupsBL.getSystemAmount(Long.toString(channelTransferVo.getTransferMRP())));
                    channelTransferVo.setRequestedQuantity(PretupsBL.getSystemAmount(Long.toString(channelTransferVo.getRequestedQuantity())));

                    // for enquiry
                    channelTransferItemsVO = (ChannelTransferItemsVO) channelTransferVo.getChannelTransferitemsVOList().get(0); // 23
                    // dec
                    if (LOG.isDebugEnabled()) {
                        LOG.info("process", "Payable amount=" + channelTransferItemsVO.getPayableAmount());
                        LOG.info("process", "Net Payable amount=" + channelTransferItemsVO.getPayableAmount());
                    }
                    
                    channelTransferVo.setPayableAmount(channelTransferItemsVO.getPayableAmount());
                    channelTransferVo.setNetPayableAmount(channelTransferItemsVO.getNetPayableAmount());
                    
                    if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVo.getDualCommissionType())) {
                   	 //channelTransferItemsVO = (ChannelTransferItemsVO) channelTransferVo.getChannelTransferitemsVOList().get(0);
                   	 //channelTransferItemsVO.setReceiverCreditQty(_autoC2CAmount+channelTransferItemsVO.getCommValue());
                   	 final boolean debit = true;
                   	Date p_curDate= new Date();
                       ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(p_con, channelTransferVo, channelTransferVo.getFromUserID(), p_curDate, debit);
                       ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(p_con, channelTransferVo, channelTransferVo.getFromUserID(), p_curDate);
                   }
                    
                    // end enquiry
                    OneLineTXNLog.log(channelTransferVo, null);
                    try {
                        updateCount = channelTransferDAO.addChannelTransfer(p_con, channelTransferVo);
                    } catch (Exception e) {
                        p_con.rollback();
                        LOG.errorTrace(METHOD_NAME, e);
                        continue;
                    }
                    // if sender is debited and receiver is credited then only
                                       // new nilesh
                    fromCountVO.setUserID(fromUserID);
                    fromCountVO.setUnctrlDailyOutCount(fromCountVO.getUnctrlDailyOutCount() + 1);
                    fromCountVO.setUnctrlWeeklyOutCount(fromCountVO.getUnctrlWeeklyOutCount() + 1);
                    fromCountVO.setUnctrlMonthlyOutCount(fromCountVO.getUnctrlMonthlyOutCount() + 1);
                    fromCountVO.setUnctrlDailyOutValue(fromCountVO.getUnctrlDailyOutValue() + channelTransferVo.getTransferMRP());
                    fromCountVO.setUnctrlWeeklyOutValue(fromCountVO.getUnctrlWeeklyOutValue() + channelTransferVo.getTransferMRP());
                    fromCountVO.setUnctrlMonthlyOutValue(fromCountVO.getUnctrlMonthlyOutValue() + channelTransferVo.getTransferMRP());
                    fromCountVO.setOutsideLastOutTime(currentDate);

                    countVO.setUserID(toUserID);
                    countVO.setUnctrlDailyInCount(countVO.getUnctrlDailyInCount() + 1);
                    countVO.setUnctrlWeeklyInCount(countVO.getUnctrlWeeklyInCount() + 1);
                    countVO.setUnctrlMonthlyInCount(countVO.getUnctrlMonthlyInCount() + 1);
                    countVO.setUnctrlDailyInValue(countVO.getUnctrlDailyInValue() + channelTransferVo.getTransferMRP());
                    countVO.setUnctrlWeeklyInValue(countVO.getUnctrlWeeklyInValue() + channelTransferVo.getTransferMRP());
                    countVO.setUnctrlMonthlyInValue(countVO.getUnctrlMonthlyInValue() + channelTransferVo.getTransferMRP());
                    countVO.setOutsideLastInTime(currentDate);

                    fromCountVO.setUserID(fromUserID);
                    fromCountVO.setDailyOutCount(fromCountVO.getDailyOutCount() + 1);
                    fromCountVO.setWeeklyOutCount(fromCountVO.getWeeklyOutCount() + 1);
                    fromCountVO.setMonthlyOutCount(fromCountVO.getMonthlyOutCount() + 1);
                    fromCountVO.setDailyOutValue(fromCountVO.getDailyOutValue() + channelTransferVo.getTransferMRP());
                    fromCountVO.setWeeklyOutValue(fromCountVO.getWeeklyOutValue() + channelTransferVo.getTransferMRP());
                    fromCountVO.setMonthlyOutValue(fromCountVO.getMonthlyOutValue() + channelTransferVo.getTransferMRP());
                    fromCountVO.setLastOutTime(currentDate);

                    countVO.setUserID(toUserID);
                    countVO.setDailyInCount(countVO.getDailyInCount() + 1);
                    countVO.setWeeklyInCount(countVO.getWeeklyInCount() + 1);
                    countVO.setMonthlyInCount(countVO.getMonthlyInCount() + 1);
                    countVO.setDailyInValue(countVO.getDailyInValue() + channelTransferVo.getTransferMRP());
                    countVO.setWeeklyInValue(countVO.getWeeklyInValue() + channelTransferVo.getTransferMRP());
                    countVO.setMonthlyInValue(countVO.getMonthlyInValue() + channelTransferVo.getTransferMRP());
                    countVO.setLastInTime(currentDate);
                    // for child
                    if (channelTransferVo.getTransferType().equals(PretupsI.CHANNEL_TYPE_C2C)) {
                    	channelTransferVo.setTransactionCode(PretupsI.TRANSACTION_TYPE_C2C);
                    	
                    	
                		if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)).booleanValue() && channelTransferVo.getUserLoanVOList() !=null && channelTransferVo.getUserLoanVOList().size()>0) {

            				Map hashmap = ChannelTransferBL.checkUserLoanstatusAndAmount(p_con, channelTransferVo);
            				if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true)) {
            					AutoC2CLogger.log(lowBalanceAlertVO,"FAIL"," Auto c2c is not possible as user Loan pending " + lowBalanceAlertVO.getUserId() );
                				p_con.rollback();
                				continue;
                				
            				}

            				if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
            					UserLoanWithdrawBL  userLoanWithdrawBL = new UserLoanWithdrawBL();
            					userLoanWithdrawBL.autoChannelLoanSettlement(channelTransferVo, PretupsI.USER_LOAN_REQUEST_TYPE,(long)hashmap.get(PretupsI.WITHDRAW_AMOUNT));
            				}

            			}
                    	
                    	
                    	
                    	
                    	
                    	Map<String, Object> hashmap = ChannelTransferBL.checkSOSstatusAndAmount(p_con, countVO,
                    			channelTransferVo);
            			if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true)) {
            				AutoC2CLogger.log(lowBalanceAlertVO,"FAIL"," Auto c2c is not possible as user SOS pending " + lowBalanceAlertVO.getUserId() );
            				p_con.rollback();
            				continue;
            			}
            			
            			if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
            				ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
            				channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVo,PretupsI.SOS_REQUEST_TYPE);
            			}
            			Map<String, Object> lrHashMap = ChannelTransferBL.checkLRstatusAndAmount(p_con, countVO, channelTransferVo);
            			if(!lrHashMap.isEmpty()&& lrHashMap.get(PretupsI.DO_WITHDRAW).equals(true)){
            				ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
            				channelTransferVo.setLrWithdrawAmt((long)lrHashMap.get(PretupsI.WITHDRAW_AMOUNT));		
            				channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVo,PretupsI.LR_REQUEST_TYPE);
            			}
            			}
                    

                    final int toCount = userTransferCountsDAO.updateUserTransferCounts(p_con, countVO, true);
                    // for parent
                    final int fromCount = userTransferCountsDAO.updateUserTransferCounts(p_con, fromCountVO, true);
                    
                    if (creditCount > 0 && debitCount > 0 && updateCount > 0 && toCount > 0 && fromCount > 0) {
                        p_con.commit();
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
                                PretupsBL.loyaltyPointsDistribution(channelTransferVo,p_con);
                          }
                    }

                    // END

                    // sending SMS on flag basis
                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_ALLOWED))).booleanValue()) {
                        if (updateCount > 0) {
                            // sending sms to receiver(only primary number
                            // allowed)
                            String smsKey = PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER;
                            if (PretupsI.CHANNEL_TYPE_C2C.equals(channelTransferVo.getType())) {
                                smsKey = PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_RECEIVER_AGENT;
                            }
                            final String recAlternetGatewaySMS = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_REQD_BY_ALT_GW"));
                            String reqruestGW = channelTransferVo.getRequestGatewayCode();
                            if (!BTSLUtil.isNullString(recAlternetGatewaySMS) && (recAlternetGatewaySMS.split(":")).length >= 2) {
                                if (reqruestGW.equalsIgnoreCase(recAlternetGatewaySMS.split(":")[0])) {
                                    reqruestGW = (recAlternetGatewaySMS.split(":")[1]).trim();
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("process: Reciver Message push through alternate GW", reqruestGW, "Requested GW was:" + channelTransferVo
                                            .getRequestGatewayCode());
                                    }
                                }
                            }
                            final UserPhoneVO phoneVO = userDAO.loadUserPhoneVO(p_con, channelTransferVo.getToUserID());

                            locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
                            p_requestVO.setLocale(locale);

                            final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiverForC2C(p_con, channelTransferVo,
                                PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_TXNSUBKEY, PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_BALSUBKEY);
                            final String[] array1 = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVo
                                .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVo.getNetPayableAmount()), parentMsisdn };
                            final BTSLMessages messages = new BTSLMessages(smsKey, array1);
                            pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVo.getTransferID(), reqruestGW, locale, channelTransferVo
                                .getNetworkCode());
                            pushMessage.push();

                            // sending sms to sender
                            final ArrayList itemsList = channelTransferVo.getChannelTransferitemsVOList();
                            final ArrayList txnList = new ArrayList();
                            final ArrayList balList = new ArrayList();

                            smsKey = PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS;
                            if (PretupsI.CHANNEL_TYPE_C2C.equals(channelTransferVo.getType())) {
                                smsKey = PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_SENDER_AGENT;
                            }

                            final int lSize = itemsList.size();
                            for (int p = 0; p < lSize; p++) {
                                channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(p);
                                keyArgumentVO = new KeyArgumentVO();
                                keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_TXNSUBKEY);
                                args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()), channelTransferItemsVO.getRequestedQuantity() };
                                keyArgumentVO.setArguments(args);
                                txnList.add(keyArgumentVO);

                                keyArgumentVO = new KeyArgumentVO();
                                keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_BALSUBKEY);
                                // args = new
                                // String[]{String.valueOf(channelTransferItemsVO.getShortName()),PretupsBL.getDisplayAmount(channelTransferItemsVO.getBalance()-channelTransferItemsVO.getRequiredQuantity())};
                                args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()), PretupsBL.getDisplayAmount(channelTransferItemsVO
                                    .getAfterTransSenderPreviousStock() - channelTransferItemsVO.getRequiredQuantity()) };
                                keyArgumentVO.setArguments(args);
                                balList.add(keyArgumentVO);
                            }// end of for
                            String[] array = null;
                            array = new String[] { BTSLUtil.getMessage(locale, txnList), BTSLUtil.getMessage(locale, balList), channelTransferVo.getTransferID(), PretupsBL
                                .getDisplayAmount(channelTransferVo.getNetPayableAmount()), phoneVO.getMsisdn() };

                            int messageLength = 0;
                            final String messLength = BTSLUtil.NullToString(Constants.getProperty("MSG_LENGTH_GW"));
                            if (!BTSLUtil.isNullString(messLength)) {
                                messageLength = (new Integer(messLength)).intValue();
                            }
                            if (reqruestGW.equalsIgnoreCase(channelTransferVo.getRequestGatewayCode())) {
                                final String senderMessage = BTSLUtil.getMessage(locale, smsKey, array);
                                pushMessage = new PushMessage(parentUserVO.getMsisdn(), senderMessage, channelTransferVo.getTransferID(), reqruestGW, locale);
                                // p_requestVO.setRequestGatewayCode(reqruestGW);
                                if ((messageLength > 0) && (senderMessage.length() < messageLength)) {
                                    pushMessage.push();
                                }
                            }
                        } else {
                            p_con.rollback();
                            p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
                            throw new BTSLBaseException("AutoC2CTransferProcess", "process", PretupsErrorCodesI.ERROR_USER_TRANSFER);
                        }
                    }
                } catch (BTSLBaseException be) {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("process", " Exception in executing record  p_channelTransferVO : " + channelTransferVo);
                    }
                    LOG.errorTrace(METHOD_NAME, be);
                    throw be;
                } catch (Exception e) {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("process", " Exception in executing record  p_channelTransferVO : " + channelTransferVo);
                    }
                    LOG.errorTrace(METHOD_NAME, e);
                }
            }

            // end
        }// end try
        catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            LOG.error("process", "BTSLBaseException " + be.getMessage());
            if (be.getMessageList() != null && be.getMessageList().size() > 0) {
                final String[] array = { BTSLUtil.getMessage(p_requestVO.getLocale(), (ArrayList) be.getMessageList()) };
                p_requestVO.setMessageArguments(array);
            }
            if (be.getArgs() != null) {
                p_requestVO.setMessageArguments(be.getArgs());
            }

            if (be.getMessageKey() != null) {
                p_requestVO.setMessageCode(be.getMessageKey());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
            }
            LOG.errorTrace(METHOD_NAME, be);
            return;
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("process", " " + e.getMessage());
            }
            LOG.errorTrace(METHOD_NAME, e);
        }// end catch
        finally {
			//EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "AutoC2CTRansferProcess[process]","","",""," [Auto C2C //Process is Completed.");
            // try {if(p_con!=null)p_con.close();}catch (SQLException e){}
            if (LOG.isDebugEnabled()) {
                LOG.info("process", "Exiting");
                // try{Thread.sleep(5000);}catch(Exception
                // e){e.printStackTrace();}
                // ConfigServlet.destroyProcessCache();
            }
        }// end finally
    }// end main

    /**
     * This method finds the list of numbers to which alert is to be sent.
     * 
     * @param p_con
     *            Connection
     * @return boolean
     */
    public static ArrayList<LowBalanceAlertVO> loadMinBalanceUsers(Connection p_con) throws BTSLBaseException {

        final String METHOD_NAME = "loadMinBalanceUsers";
        if (LOG.isDebugEnabled()) {
            LOG.info(METHOD_NAME, "Entered");
        }
        PreparedStatement pstmt = null;
        ResultSet rst = null;
        ArrayList<LowBalanceAlertVO> list = null;
        LowBalanceAlertVO alertVO = null;
        try {
            final StringBuffer queryBuf = new StringBuffer(
                " select distinct UTC.user_id, U.msisdn, U.parent_id, U.network_code,U.category_code,CU.transfer_profile_id,UB.balance,CU.auto_c2c_quantity ,UTC.product_code ");
            queryBuf.append(" from user_threshold_counter UTC, users U, channel_users CU, user_balances UB ");
            queryBuf.append(" where RECORD_TYPE='BT' and UTC.user_id=U.user_id AND CU.user_id=U.user_id AND UB.user_id=U.user_id AND U.status <> ? AND U.parent_id <> ? AND CU.auto_c2C_allow='Y' and CU.auto_c2c_quantity>0 ");
            queryBuf.append(" and U.category_code in (Select control_code from control_preferences where preference_code= ? and UPPER(value)= UPPER(?)   and  UTC.product_code = UB.product_code) ");
			queryBuf.append(" and UB.BALANCE<=(select distinct THRESHOLD_VALUE  from user_threshold_counter where ENTRY_DATE_TIME= (select Max(ENTRY_DATE_TIME) from user_threshold_counter  where user_id=CU.user_id and RECORD_TYPE='BT' ) and user_id=CU.user_id )");
            
			
            final String query = queryBuf.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Query:" + query);
            }
            pstmt = p_con.prepareStatement(query.toString());
            //pstmt.setString(1, PretupsI.THRESHOLD_TYPE_MIN);
            pstmt.setString(1, PretupsI.NO);
            pstmt.setString(2, PretupsI.ROOT_PARENT_ID);
            pstmt.setString(3, PretupsI.AUTO_C2C_SOS_CAT_ALLOWED);
            pstmt.setString(4, PretupsI.AUTO_C2C_TRUE);
            rst = pstmt.executeQuery();
            list = new ArrayList<LowBalanceAlertVO>();
            while (rst.next()) {
                alertVO = new LowBalanceAlertVO();
                alertVO.setUserId(rst.getString("user_id"));
                alertVO.setMsisdn(rst.getString("msisdn"));
                alertVO.setParentUserId(rst.getString("parent_id"));
                alertVO.setNetworkCode(rst.getString("network_code"));
                alertVO.setCategoryCode(rst.getString("category_code"));
                alertVO.setProfileID(rst.getString("transfer_profile_id"));
                alertVO.setBalance(rst.getLong("balance"));
                alertVO.setAutoc2cquantity(rst.getString("auto_c2c_quantity"));
                alertVO.setProductCode(rst.getString("product_code"));
                list.add(alertVO);
            }
        }// end try
        catch (SQLException sqle) {
            LOG.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoC2CTransferProcess[balanceAlertUsers]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("AutoC2CTransferProcess", METHOD_NAME, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoC2CTransferProcess[balanceAlertUsers]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("AutoC2CTransferProcess", METHOD_NAME, "error.general.processing");
        }// end of catch
        finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e2) {
                    LOG.errorTrace(METHOD_NAME, e2);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e3) {
                    LOG.errorTrace(METHOD_NAME, e3);
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.info(METHOD_NAME, " Exiting list size " + list.size());
            }
        }// end finally
        return list;
    }

    /**
     * Genrate the channel to Channel transfer ID
     * Create single connection for complete transaction
     * 
     * @param p_channelTransferVO
     * @throws BTSLBaseException
     */
    /*
     * public static synchronized void genrateChnnlToChnnlTrfID(Connection
     * p_con,ChannelTransferVO p_channelTransferVO) throws BTSLBaseException
     * {
     * //changes added to change format of C2C Transfer ID on 12-02-2015
     * final String METHOD_NAME = "genrateChnnlToChnnlTrfID";
     * if (LOG.isDebugEnabled())
     * LOG.debug("genrateChnnlToChnnlTrfID", "Entered ChannelTransferVO =" +
     * p_channelTransferVO);
     * String minut2Compare=null;
     * Date mydate = null;
     * try
     * {
     * mydate = new Date();
     * p_channelTransferVO.setCreatedOn(mydate);
     * minut2Compare = _sdfCompare.format(mydate);
     * int currentMinut=Integer.parseInt(minut2Compare);
     * 
     * if(currentMinut !=_prevMinut)
     * {
     * _transactionIDCntrAutoC2C=1;
     * _prevMinut=currentMinut;
     * }
     * else if(_transactionIDCntrAutoC2C >= 65535)
     * {
     * _transactionIDCntrAutoC2C=1;
     * }
     * else
     * {
     * _transactionIDCntrAutoC2C++;
     * }
     * if(_transactionIDCntrAutoC2C==0) {
     * throw new BTSLBaseException("AutoC2CTransfer", METHOD_NAME,
     * PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
     * }
     * 
     * p_channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(
     * p_channelTransferVO
     * ,PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID,_transactionIDCntrAutoC2C));
     * 
     * }
     * //changes ended
     * 
     * try
     * {
     * //p_channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(
     * p_channelTransferVO
     * ,PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID,IDGenerator.getNextID
     * (PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID, BTSLUtil.getFinancialYear() ,
     * p_channelTransferVO
     * .getNetworkCode(),p_channelTransferVO.getCreatedOn())));
     * //long
     * tmpId=IDGenerator.getNextID(p_con,PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID
     * , BTSLUtil.getFinancialYear() , p_channelTransferVO);
     * long tmpId=getNextID(p_con,PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID,
     * BTSLUtil.getFinancialYear() , p_channelTransferVO);
     * p_channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(
     * p_channelTransferVO,PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID,tmpId));
     * 
     * }
     * 
     * catch (Exception e)
     * {
     * LOG.error("genrateChnnlToChnnlTrfID", "Exception " + e.getMessage());
     * LOG.errorTrace(METHOD_NAME,e);
     * //EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
     * EventStatusI
     * .RAISED,EventLevelI.FATAL,"ChannelTransferBL[genrateChnnlToChnnlTrfID]"
     * ,"","","","Exception:"+e.getMessage());
     * throw new BTSLBaseException("AutoC2CTransfer",
     * "genrateChnnlToChnnlTrfID", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
     * }
     * finally
     * {
     * if (LOG.isDebugEnabled())
     * LOG.debug("genrateChnnlToChnnlTrfID",
     * "Exited  ID ="+p_channelTransferVO.getTransferID());
     * }
     * 
     * }
     */

    public static long getNextID(Connection p_con, String p_idType, String p_year, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {

        final String METHOD_NAME = "getNextID";
        try {
            final IDGeneratorDAO _idGeneratorDAO = new IDGeneratorDAO();
            final long id = _idGeneratorDAO.getNextID(p_con, p_idType, p_year, p_channelTransferVO);
            return id;
        } finally {
            if (p_con != null) {
                try {
                    p_con.commit();
                } catch (Exception e) {
                    LOG.errorTrace(METHOD_NAME, e);
                }
            }
        }
    }
}// end class