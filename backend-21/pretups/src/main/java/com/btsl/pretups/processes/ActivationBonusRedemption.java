package com.btsl.pretups.processes;

/*
 * ActivationBonusRedemption.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Chetan Kothari 13/02/2009 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2009 Bharti Telesoft Ltd.
 * Main class for redemption of Activation Bonus points
 */
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.profile.businesslogic.ActivationBonusBL;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayForCategoryCache;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.logging.ProfileBonusLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ActivationBonusVO;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.processes.businesslogic.RedemptionVO;
import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.txn.pretups.channel.profile.businesslogic.CommissionProfileTxnDAO;

public class ActivationBonusRedemption {
    private static final Log _logger = LogFactory.getLog(ActivationBonusRedemption.class.getName());
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;
    private static UserBalancesVO _userBalancesVO = null;
    private static PreparedStatement _pstmtSelectUserBalOnly = null;
    private static PreparedStatement _psmtUpdateUserBal = null;
    private static PreparedStatement _psmtInsertUserBal = null;
    private static PreparedStatement _pstmtSelectUser = null;
    private static PreparedStatement _psmtInsertRedemption = null;
    private static PreparedStatement _pstmtSelectUserBal = null;
    private static PreparedStatement _pstmtUpdateBonus = null;

    /**
     * ensures no instantiation
     */
    private ActivationBonusRedemption(){
    	
    }
    
    /**
     * This method is main method for the process it gets all the command line
     * parameters and calls process method
     * for ActivationBonus Redemption process.
     * 
     * @author chetan.kothari
     * @param args
     *            String[]
     * @return void
     */
    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        try {
            if (args.length != 2) {
                System.out.println("Usage : ActivationBonusRedemption [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = Constants.validateFilePath(args[0]);
            if (!constantsFile.exists()) {
                System.out.println(" Constants File Not Found .............");
                return;
            }
            final File logconfigFile = Constants.validateFilePath(args[1]);
            if (!logconfigFile.exists()) {
                System.out.println(" Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (Exception ex) {
            _logger.error(METHOD_NAME, "Error in Loading Configuration files ...........................: " + ex);
            _logger.errorTrace(METHOD_NAME, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            process();
        } catch (BTSLBaseException be) {
            _logger.error("main", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.info("main", " Exiting");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * This method is process method which contains all the processing regarding
     * Transfer of Bonus points to
     * UserBalnce and deduction of stock .
     * 
     * @author chetan.kothari
     * @param void
     * @return void
     * @throws BTSLBaseException
     **/

    private static void process() throws BTSLBaseException {
        final String METHOD_NAME = "process";
        Date processedUpto = null;
        Date currentDate = null;
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        String redemptioID = null;
        SimpleDateFormat sdf = null;
        Iterator bonusDataIterator = null;
        ActivationBonusVO bonusDataVO = null;
        ChannelUserVO channelUserVO = null;
        ChannelTransferVO channelTransferVO = null;
        ChannelTransferItemsVO channelTransferItemVO = null;
        ArrayList channelTransferItemVOList = null;
        BTSLMessages btslMessage = null;
        PushMessage pushMessage = null;
        List bonusDataList = null;
        final Locale defaultLocale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

        try {
            sdf = new SimpleDateFormat("MM/dd/yyyy");
            _logger.debug("process", "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = new Date();
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusRedemption[process]",
                    "", "", "", "DATABASE Connection is NULL");
                return;
            }
            // getting process id
            processId = ProcessI.ACTIVATION_BONUS_PROCESSID;
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            if (statusOk) {
                con.commit();
                // method call to find maximum date till which process has been
                // executed
                processedUpto = _processStatusVO.getExecutedUpto();

                if (processedUpto != null) {
                    if (sdf.format(processedUpto).equals(sdf.format(currentDate))) {
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationBonusRedemption[process]", "",
                            "", "", "Activation Bonus already run for the date =" + String.valueOf(currentDate));
                        throw new BTSLBaseException("ActivationBonusRedemption", "process", "Activation Bonus already run for the date =" + String.valueOf(currentDate));
                    }
                    _processStatusVO.setStartDate(currentDate);
                    bonusDataList = getBonusDataList(con);
                    if (bonusDataList != null && bonusDataList.size() > 0) {
                        generatePreparedStatements(con);
                        final HashMap productVOMap = loadProductVOMap(con);
                        final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
                        final RedemptionVO redemptionVO = new RedemptionVO();
                        final String arguments[] = new String[2];
                        ProductVO productVO = null;
                        int count = 0;
                        final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
                        final CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();
                        CommissionProfileSetVO  commissionProfileSetReceiverVO = null ;
                        Locale locale = null;
                        bonusDataIterator = bonusDataList.iterator();
                        double pointsRemaining = 0;
                        while (bonusDataIterator.hasNext()) {
                            bonusDataVO = (ActivationBonusVO) bonusDataIterator.next();
                            channelUserVO = null;
                            _userBalancesVO = null;
                            channelTransferVO = new ChannelTransferVO();
                            pointsRemaining = bonusDataVO.getPoints() - Math.floor(bonusDataVO.getPoints());
                            bonusDataVO.setRemainingPoints(pointsRemaining);
                            bonusDataVO.setPointsToRedeem( BTSLUtil.parseDoubleToInt(Math.floor(bonusDataVO.getPoints())) );
                            productVO = (ProductVO) productVOMap.get(bonusDataVO.getProductCode());
                            channelUserVO = loadUserDetailsFormUserID(con, bonusDataVO.getUserId());
                            if (channelUserVO.getLanguageCode() != null && channelUserVO.getCountry() != null) {
                                locale = new Locale(channelUserVO.getLanguageCode(), channelUserVO.getCountry());
                            } else {
                                locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                            }
                            if (channelUserVO.getStatus().equals(PretupsI.STATUS_ACTIVE)) {
                                _userBalancesVO = loadUserBalanceForProduct(channelUserVO.getUserID(), bonusDataVO.getProductCode(), channelUserVO.getNetworkID(),
                                    channelUserVO.getNetworkID());
                                if (_userBalancesVO != null) {
                                    channelUserVO.setUserBalance(String.valueOf(_userBalancesVO.getBalance()));
                                } else {
                                    channelUserVO.setUserBalance("0");
                                }
                                long amount = 0l;
                                amount = BTSLUtil.parseDoubleToLong( ActivationBonusBL.convertPointsToAmount(bonusDataVO));

                                channelTransferVO.setNetworkCode(channelUserVO.getNetworkID());
                                channelTransferVO.setCreatedBy(PretupsI.SYSTEM_USER);
                                channelTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                                channelTransferVO.setTransferInitatedBy(PretupsI.SYSTEM_USER);
                                // Note :: Tax1 and Tax 2 not added till now as
                                // transfer rules are bypassed.
                                channelTransferVO.setTotalTax1(0);
                                channelTransferVO.setTotalTax2(0);
                                channelTransferVO.setRequestedQuantity(amount);
                                channelTransferVO.setTotalTax3(0);
                                channelTransferVO.setGrphDomainCodeDesc(channelUserVO.getGeographicalDesc());
                                channelTransferVO.setPayableAmount(0);
                                channelTransferVO.setNetPayableAmount(0);
                                channelTransferVO.setPayInstrumentAmt(0);
                                channelTransferVO.setTransferMRP(amount);
                                channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
                                channelTransferVO.setToUserID(channelUserVO.getUserID());
                                channelTransferVO.setToUserName(channelUserVO.getUserName());
                                channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
                                channelTransferVO.setDomainCode(channelUserVO.getDomainID());
                                channelTransferVO.setReceiverCategoryCode(channelUserVO.getCategoryCode());
                                channelTransferVO.setReceiverGradeCode(channelUserVO.getUserGrade());
                                channelTransferVO.setCommProfileSetId(channelUserVO.getCommissionProfileSetID());
                                channelTransferVO.setExternalTxnDate(currentDate);
                                channelTransferVO.setNetworkCodeFor(channelUserVO.getNetworkID());
                                channelTransferVO.setCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
                                channelTransferVO.setTransferDate(currentDate);
                                commissionProfileSetReceiverVO = commissionProfileTxnDAO.loadCommProfileSetDetails(con, channelUserVO
                                    .getCommissionProfileSetID(), currentDate);
                                channelUserVO.setCommissionProfileSetVersion(commissionProfileSetReceiverVO.getCommProfileVersion());
                                channelTransferVO.setDualCommissionType(commissionProfileSetReceiverVO.getDualCommissionType());
                                channelTransferVO.setCommProfileVersion(channelUserVO.getCommissionProfileSetVersion());
                                channelTransferVO.setCreatedOn(currentDate);
                                channelTransferVO.setModifiedOn(currentDate);
                                channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                                channelTransferVO.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
                                channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
                                channelTransferVO.setProductType(productVO.getProductType());
                                channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
                                channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
                                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                                channelTransferVO.setControlTransfer(PretupsI.YES);
                                channelTransferVO.setReceiverGgraphicalDomainCode(channelUserVO.getGeographicalCode());
                                channelTransferVO.setReceiverDomainCode(channelUserVO.getDomainID());
                                channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(channelUserVO.getUserCode()));
                                // Approval entries
                                // First level Approval
                                channelTransferVO.setFirstApprovedBy(PretupsI.SYSTEM_USER);
                                channelTransferVO.setFirstApprovedByName(PretupsI.SYSTEM_USER);
                                channelTransferVO.setFirstApprovedOn(currentDate);
                                // Second level approval
                                channelTransferVO.setSecondApprovedBy(PretupsI.SYSTEM_USER);
                                channelTransferVO.setSecondApprovedByName(PretupsI.SYSTEM_USER);
                                channelTransferVO.setSecondApprovedOn(currentDate);
                                // third level approval
                                channelTransferVO.setThirdApprovedBy(PretupsI.SYSTEM_USER);
                                channelTransferVO.setThirdApprovedByName(PretupsI.SYSTEM_USER);
                                channelTransferVO.setThirdApprovedOn(currentDate);
                                channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                                ChannelTransferBL.genrateTransferID(channelTransferVO);
                                // Updating details for channel transfer items
                                channelTransferItemVO = new ChannelTransferItemsVO();
                                channelTransferItemVO.setApprovedQuantity(amount);
                                channelTransferItemVO.setCommProfileDetailID(channelUserVO.getCommissionProfileSetID());
                                channelTransferItemVO.setNetPayableAmount(0);
                                channelTransferItemVO.setPayableAmount(0);
                                channelTransferItemVO.setProductTotalMRP(amount);
                                channelTransferItemVO.setProductCode(bonusDataVO.getProductCode());
                                channelTransferItemVO.setReceiverPreviousStock(Long.parseLong(channelUserVO.getUserBalance()));
                                channelTransferItemVO.setUnitValue(productVO.getUnitValue());
                                channelTransferItemVO.setRequiredQuantity(amount);
                                channelTransferItemVO.setRequestedQuantity(PretupsBL.getDisplayAmount(amount));
                                channelTransferItemVO.setSerialNum(1);
                                channelTransferItemVO.setTransferID(channelTransferVO.getTransferID());
                                channelTransferItemVO.setAfterTransReceiverPreviousStock(Long.parseLong(channelUserVO.getUserBalance()));
                                channelTransferItemVO.setShortName(bonusDataVO.getProductCode());
                                channelTransferItemVOList = new ArrayList();
                                channelTransferItemVOList.add(channelTransferItemVO);
                                channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
                                OneLineTXNLog.log(channelTransferVO, null);
                                count = channelTransferDAO.addChannelTransfer(con, channelTransferVO);
                                // prepare networkStockList and debit the
                                // network stock
                                ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(con, channelTransferVO, PretupsI.SYSTEM_USER, currentDate, true);
                                ChannelTransferBL.updateNetworkStockTransactionDetails(con, channelTransferVO, PretupsI.SYSTEM_USER, currentDate);
                                // Credit to user balances
                                creditUserBalances(con, channelTransferVO);
                                redemptioID = ChannelTransferBL.getRedemptionId();
                                bonusDataVO.setLastRedemptionId(redemptioID);
                                count = updateBonusData(bonusDataVO);
                                ProfileBonusLog.log(bonusDataVO);
                                redemptionVO.setReferenceId(channelTransferVO.getTransferID());
                                redemptionVO.setCreatedBy(PretupsI.SYSTEM_USER);
                                redemptionVO.setRedemptionId(redemptioID);
                                redemptionVO.setRedemptionType(PretupsI.PROFILE_TYPE_ACTIVATION);
                                redemptionVO.setProfileType(PretupsI.PROFILE_TYPE_ACTIVATION);
                                redemptionVO.setAmountTransfered(amount);
                                // redemptionVO.setAmountTransfered(ActivationBonusBL.convertPointsToAmount(bonusDataVO));
                                redemptionVO.setPointsRedeemed(bonusDataVO.getPointsToRedeem());
                                redemptionVO.setCreatedBy(PretupsI.SYSTEM_USER);
                                redemptionVO.setCreatedOn(currentDate);
                                redemptionVO.setModifiedBy(PretupsI.SYSTEM_USER);
                                redemptionVO.setModifiedOn(currentDate);
                                redemptionVO.setRedemptionDate(currentDate);
                                redemptionVO.setProductCode(bonusDataVO.getProductCode());
                                redemptionVO.setUserId(bonusDataVO.getUserId());
                                count = 0;
                                count = insertRedemptionData(redemptionVO);
                                if (count > 0) {
                                    con.commit();
                                    arguments[0] = PretupsBL.getDisplayAmount(amount);
                                    arguments[1] = redemptioID;
                                    btslMessage = new BTSLMessages(PretupsErrorCodesI.ACTBONUS_SUCCESSFUL_MESSAGE, arguments);
                                    pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslMessage, null, null, locale, channelUserVO.getNetworkID());
                                    pushMessage.push();
                                } else {
                                    con.rollback();
                                }
                                amount = 0;
                            }// end if to check that user is active or not
                        }// end for bonusdatalist
                    }// end if bonusdatalist >0
                    final String[] adminMsisdn = Constants.getProperty("adminmobile").split(",");
                    for (int k = 0; k < adminMsisdn.length; k++) {
                        pushMessage = new PushMessage(adminMsisdn[k], " ActivationBonusRedemption process has been executed successfully.", null, null, defaultLocale);
                        pushMessage.push();
                    }
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationBonusRedemption[process]", "", "",
                        "", " ActivationBonusRedemption process has been executed successfully.");
                } else {
                    throw new BTSLBaseException("ActivationBonusRedemption", "process",
                        "Activation Bonus process could not find the date till which process has been executed.");
                }
            }
        }// end of try
        catch (BTSLBaseException be) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            _logger.error("process", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            _logger.error("process", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationBonusRedemption[process]", "", "", "",
                " ActivationBonusRedemption process could not be executed successfully.");
            throw new BTSLBaseException("ActivationBonusRedemption", "process", PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        } finally {
            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk) {
                try {
                    if (markProcessStatusAsComplete(con, processId) == 1) {
                        try {
                            if (con != null) {
                                con.commit();
                            }
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                            if (con != null) {
                                con.rollback();
                            }
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    }
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception ex) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("process", "Exception closing connection ");
                    }
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            try {
                if (_pstmtSelectUser != null) {
                    _pstmtSelectUser.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (_pstmtSelectUserBalOnly != null) {
                    _pstmtSelectUserBalOnly.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (_psmtUpdateUserBal != null) {
                    _psmtUpdateUserBal.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (_psmtInsertUserBal != null) {
                    _psmtInsertUserBal.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (_psmtInsertRedemption != null) {
                    _psmtInsertRedemption.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (_pstmtSelectUserBal != null) {
                    _pstmtSelectUserBal.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (_pstmtUpdateBonus != null) {
                    _pstmtUpdateBonus.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }

            _logger.debug("process", "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "Exiting..... ");
            }
        }
    }

    /**
     * This method will get list of bonus data which is in range of given
     * duration and points according to system preferences .
     * 
     * @author chetan.kothari
     * @param p_con
     *            Connection
     * @return List
     * @throws BTSLBaseException
     */

    public static List getBonusDataList(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "getBonusDataList";
        if (_logger.isDebugEnabled()) {
            _logger.debug("getBonusDataList", " Entered:  p_con:" + p_con);
        }
        PreparedStatement psm = null;
        ResultSet rs = null;
        ArrayList bonusList = null;
        ActivationBonusVO bonusVO = null;
        try {
            bonusList = new ArrayList();
            final StringBuffer query = new StringBuffer();
            query.append(" SELECT PROFILE_TYPE,USER_ID_OR_MSISDN, POINTS, BUCKET_CODE, PRODUCT_CODE,");
            query.append(" POINTS_DATE, LAST_REDEMPTION_ID, LAST_REDEMPTION_ON, LAST_ALLOCATION_TYPE,");
            query.append(" LAST_ALLOCATED_ON,CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY");
            query.append(" from BONUS");
            query.append(" where PROFILE_TYPE = ? and ( POINTS > ? or LAST_REDEMPTION_ON <? )");
            if (_logger.isDebugEnabled()) {
                _logger.debug("getBonusDataList", " query:" + query.toString());
            }
            psm = p_con.prepareStatement(query.toString());
            final long redemptionDuration = Long.parseLong(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ACTIVATION_BONUS_REDEMPTION_DURATION)));
            final Date date = new Date(System.currentTimeMillis() - (redemptionDuration * 24 * 60 * 60 * 1000));
            psm.setString(1, PretupsI.ACT_PROF_TYPE);
            psm.setInt(2, Integer.parseInt(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ACTIVATION_BONUS_MIN_REDEMPTION_AMOUNT))));
            psm.setDate(3, BTSLUtil.getSQLDateFromUtilDate(date));
            rs = psm.executeQuery();
            while (rs.next()) {
                bonusVO = new ActivationBonusVO();
                bonusVO.setLastRedemptionDate(rs.getDate("LAST_REDEMPTION_ON"));
                bonusVO.setProductCode(rs.getString("PRODUCT_CODE"));
                bonusVO.setProfileType(rs.getString("PROFILE_TYPE"));
                bonusVO.setUserId(rs.getString("USER_ID_OR_MSISDN"));
                bonusVO.setPoints(rs.getDouble("POINTS"));
                bonusVO.setBucketCode(rs.getString("BUCKET_CODE"));
                bonusVO.setPointsDate(rs.getDate("POINTS_DATE"));
                bonusVO.setLastRedemptionId(rs.getString("LAST_REDEMPTION_ID"));
                bonusVO.setLastAllocationdate(rs.getDate("LAST_ALLOCATED_ON"));
                bonusVO.setLastAllocationType(rs.getString("LAST_ALLOCATION_TYPE"));
                bonusVO.setCreatedBy(rs.getString("CREATED_BY"));
                bonusVO.setCreatedOn(rs.getDate("CREATED_ON"));
                bonusVO.setModifiedBy(rs.getString("MODIFIED_BY"));
                bonusVO.setModifiedOn(rs.getDate("MODIFIED_ON"));
                bonusList.add(bonusVO);
            }
        } catch (SQLException sqe) {
            _logger.error("getBonusDataList", "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusRedemption[getBonusDataList]", "", "",
                "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", "getBonusDataList", PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _logger.error("getBonusDataList", "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusRedemption[getBonusDataList]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", "getBonusDataList", PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (psm != null) {
                try {
                    psm.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("getBonusDataList", "Exiting bonusList.size:" + bonusList.size());
            }
        }// end of finally
        return bonusList;
    }

    /**
     * This method is used to update the bonus table from ActivaqtionBonusVO.
     * 
     * @author chetan.kothari
     * @param p_bonusDataVO
     *            ActivationBonusVO
     * @throws BTSLBaseException
     * @return int
     */
    private static int updateBonusData(ActivationBonusVO p_bonusDataVO) throws BTSLBaseException {
        final String METHOD_NAME = "updateBonusData";
        if (_logger.isDebugEnabled()) {
            _logger.debug("updateBonusData", " Entered: p_bonusDataVO " + p_bonusDataVO.toString());
        }
        int updateCount = 0;
        final Date curDate = new Date();
        int index = 1;
        try {
            _pstmtUpdateBonus.setString(index++, p_bonusDataVO.getLastRedemptionId());
            _pstmtUpdateBonus.setTimestamp(index++, BTSLUtil.getTimestampFromUtilDate(curDate));
            _pstmtUpdateBonus.setDouble(index++, p_bonusDataVO.getRemainingPoints());
            _pstmtUpdateBonus.setTimestamp(index++, BTSLUtil.getTimestampFromUtilDate(curDate));
            _pstmtUpdateBonus.setString(index++, p_bonusDataVO.getUserId());
            _pstmtUpdateBonus.setString(index++, p_bonusDataVO.getProductCode());
            updateCount = _pstmtUpdateBonus.executeUpdate();
        } catch (SQLException sqe) {
            _logger.error("updateBonusData", "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusRedemption[updateBonusData]", "", "",
                "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", "getBonusData", "An error has occured in Activation Bonus Redemption process, cannot continue.");
        }// end of catch
        catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("updateBonusData", "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusRedemption[updateBonusData]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", "updateBonusData", "An error has occured in Activation Bonus Redemption process, cannot continue.");
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("updateBonusData",
                    "Exiting: updateCount=" + updateCount + " bonus is updated for userid = " + p_bonusDataVO.getUserId() + " and product " + p_bonusDataVO.getProductCode());
            }
        } // end of finally
        return updateCount;
    }

    /**
     * This method is used to mark the status of the Activation Redemption
     * Process to complete.
     * 
     * @author chetan.kothari
     * @param p_con
     *            Connection
     * @param p_processId
     *            String
     * @throws BTSLBaseException
     * @return int
     */
    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) throws BTSLBaseException {
        final String METHOD_NAME = "markProcessStatusAsComplete";
        if (_logger.isDebugEnabled()) {
            _logger.debug("markProcessStatusAsComplete", " Entered:  p_processId:" + p_processId);
        }
        int updateCount = 0;
        final Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        // Correcting the executed_upto value
        _processStatusVO.setExecutedUpto(BTSLUtil.addDaysInUtilDate(currentDate, -1));
        _processStatusVO.setExecutedOn(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusRedemption[markProcessStatusAsComplete]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", "markProcessStatusAsComplete", PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    /**
     * This method is used to insert data in redemption table from RedemptionVO.
     * 
     * @author chetan.kothari
     * @param p_bonusDataVO
     *            RedemptionVO
     * @throws BTSLBaseException
     * @return int
     */
    private static int insertRedemptionData(RedemptionVO p_redemptionVO) throws BTSLBaseException {
        final String METHOD_NAME = "insertRedemptionData";
        if (_logger.isDebugEnabled()) {
            _logger.debug("insertRedemptionData", " Entered:  p_redemptionVO " + p_redemptionVO.toString());
        }
        int updateCount = 0;
        int index = 1;
        try {
            _psmtInsertRedemption.setString(index++, p_redemptionVO.getReferenceId());
            _psmtInsertRedemption.setString(index++, p_redemptionVO.getProfileType());
            _psmtInsertRedemption.setString(index++, p_redemptionVO.getRedemptionType());
            _psmtInsertRedemption.setString(index++, p_redemptionVO.getRedemptionId());
            _psmtInsertRedemption.setDate(index++, BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getRedemptionDate()));
            _psmtInsertRedemption.setString(index++, p_redemptionVO.getProductCode());
            _psmtInsertRedemption.setDouble(index++, p_redemptionVO.getPointsRedeemed());
            _psmtInsertRedemption.setDouble(index++, p_redemptionVO.getAmountTransfered());
            _psmtInsertRedemption.setTimestamp(index++, BTSLUtil.getTimestampFromUtilDate(p_redemptionVO.getCreatedOn()));
            _psmtInsertRedemption.setString(index++, p_redemptionVO.getCreatedBy());
            _psmtInsertRedemption.setTimestamp(index++, BTSLUtil.getTimestampFromUtilDate(p_redemptionVO.getModifiedOn()));
            _psmtInsertRedemption.setString(index++, p_redemptionVO.getModifiedBy());
            _psmtInsertRedemption.setString(index++, p_redemptionVO.getUserId());
            updateCount = _psmtInsertRedemption.executeUpdate();
        } catch (SQLException sqe) {
            _logger.error("insertRedemptionData", "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusRedemption[insertRedemptionData]", "",
                "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", "insertRedemptionData", "An error has occured in Activation Bonus settlement process, cannot continue.");
        }// end of catch
        catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("insertRedemptionData", "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusRedemption[insertRedemptionData]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", "insertRedemptionData", "An error has occured in Activation Bonus settlement process, cannot continue.");
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("insertRedemptionData", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    /**
     * Method to load all product details.
     * 
     * @author chetan.kothari
     * @param p_con
     *            Connection
     * @return HashMap
     * @throws BTSLBaseException
     */
    private static HashMap loadProductVOMap(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadProductVOMap";
        if (_logger.isDebugEnabled()) {
            _logger.debug("loadProductVOMap", "Entered ");
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ProductVO channelProductsVO = null;
        final HashMap productVOMap = new HashMap();
        try {
            final StringBuffer selectQueryBuff = new StringBuffer(
                " SELECT PRODUCT_CODE, PRODUCT_TYPE, MODULE_CODE, PRODUCT_NAME, SHORT_NAME, PRODUCT_SHORT_CODE, PRODUCT_CATEGORY, ERP_PRODUCT_CODE, STATUS, UNIT_VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY ");
            selectQueryBuff.append(" FROM products ");
            selectQueryBuff.append(" WHERE status=? ");
            final String selectQuery = selectQueryBuff.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadProductVOMap", "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, PretupsI.YES);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                channelProductsVO = new ProductVO();
                channelProductsVO.setProductCode(rs.getString("product_code"));
                channelProductsVO.setProductName(rs.getString("product_name"));
                channelProductsVO.setShortName(rs.getString("short_name"));
                channelProductsVO.setProductShortCode(rs.getInt("product_short_code"));
                channelProductsVO.setStatus(rs.getString("status"));
                channelProductsVO.setUnitValue(rs.getLong("unit_value"));
                channelProductsVO.setProductCategory(rs.getString("product_category"));
                channelProductsVO.setErpProductCode(rs.getString("erp_product_code"));
                channelProductsVO.setProductType(rs.getString("product_type"));
                channelProductsVO.setModuleCode(rs.getString("module_code"));
                productVOMap.put(channelProductsVO.getProductCode(), channelProductsVO);
            }
        } catch (SQLException sqle) {
            _logger.error("loadProductVOMap", "SQLException " + sqle.getMessage());
            _logger.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusRedemption[loadProductVOMap]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", "loadProductVOMap", "An error has occured in Activation Bonus settlement process, cannot continue.");
        }// end of catch
        catch (Exception e) {
            _logger.error("loadProductVOMap", "Exception " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusRedemption[loadProductVOMap]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", "loadProductVOMap", "An error has occured in Activation Bonus settlement process, cannot continue.");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadProductVOMap", "Exiting productVOMap:" + productVOMap);
            }
        }// end of finally
        return productVOMap;
    }

    /**
     * Method to load the balance for the user for a particular prodct
     * 
     * @author chetan.kothari
     * @param p_userID
     *            String
     * @param p_productCode
     *            String
     * @return UserBalancesVO
     * @throws BTSLBaseException
     */
    public static UserBalancesVO loadUserBalanceForProduct(String p_userID, String p_productCode, String p_networkCode, String p_networkCodeFor) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserBalanceForProduct";
        if (_logger.isDebugEnabled()) {
            _logger.debug("loadUserBalanceForProduct",
                "Entered  p_userID:" + p_userID + " p_productCode=" + p_productCode + "p_networkCode= " + p_networkCode + "p_networkCodeFor=" + p_networkCodeFor);
        }
        ResultSet rs = null;
        UserBalancesVO userBalancesVO = null;
        try {
            _pstmtSelectUserBal.clearParameters();
            _pstmtSelectUserBal.setString(1, p_userID);
            _pstmtSelectUserBal.setString(2, p_productCode);
            _pstmtSelectUserBal.setString(3, p_networkCodeFor);
            _pstmtSelectUserBal.setString(4, p_networkCode);
            rs = _pstmtSelectUserBal.executeQuery();
            if (rs != null && rs.next()) {
                userBalancesVO = new UserBalancesVO();
                userBalancesVO.setUserID(rs.getString("USER_ID"));
                userBalancesVO.setNetworkCode(rs.getString("NETWORK_CODE"));
                userBalancesVO.setNetworkFor(rs.getString("NETWORK_CODE_FOR"));
                userBalancesVO.setProductCode(rs.getString("PRODUCT_CODE"));
                userBalancesVO.setBalance(rs.getLong("BALANCE"));
                userBalancesVO.setPreviousBalance(rs.getLong("PREV_BALANCE"));
                userBalancesVO.setLastTransferType(rs.getString("LAST_TRANSFER_TYPE"));
                userBalancesVO.setLastTransferOn(rs.getDate("LAST_TRANSFER_ON"));
                userBalancesVO.setLastTransferID(rs.getString("LAST_TRANSFER_NO"));
            }
        }// end of try
        catch (SQLException sqle) {
            _logger.error("loadUserBalanceForProduct", "SQLException " + sqle.getMessage());
            _logger.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusRedemption[loadUserBalanceForProduct]",
                p_productCode, "", p_userID, "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", "loadUserBalanceForProduct", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _logger.error("loadUserBalanceForProduct", "Exception " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusRedemption[loadUserBalanceForProduct]",
                p_productCode, "", p_userID, "Exception:" + e.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", "loadUserBalanceForProduct", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadUserBalanceForProduct", "Exiting userBalancesVO:" + ((userBalancesVO != null) ? userBalancesVO.toString() : null));
            }
        }// end of finally
        return userBalancesVO;
    }

    /**
     * This method loads the details of user by user id if the user is active .
     * 
     * @author chetan.kothari
     * @param p_con
     *            Connection
     * @param p_userID
     *            String
     * @throws BTSLBaseException
     * @return ChannelUserVO
     */
    public static ChannelUserVO loadUserDetailsFormUserID(Connection p_con, String p_userID) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserDetailsFormUserID";
        if (_logger.isDebugEnabled()) {
            _logger.debug("loadUserDetailsFormUserID", "Entered p_userID:" + p_userID);
        }
        ChannelUserVO channelUserVO = null;
        ResultSet rsSelectUser = null;
        try {
            _pstmtSelectUser.clearParameters();
            _pstmtSelectUser.setString(1, p_userID);
            rsSelectUser = _pstmtSelectUser.executeQuery();
            if (rsSelectUser.next()) {
                channelUserVO = new ChannelUserVO();
                channelUserVO.setUserID(rsSelectUser.getString("user_id"));
                channelUserVO.setUserName(rsSelectUser.getString("user_name"));
                channelUserVO.setNetworkID(rsSelectUser.getString("network_code"));
                channelUserVO.setLoginID(rsSelectUser.getString("login_id"));
                channelUserVO.setPassword(rsSelectUser.getString("password"));
                channelUserVO.setCategoryCode(rsSelectUser.getString("category_code"));
                channelUserVO.setParentID(rsSelectUser.getString("parent_id"));
                channelUserVO.setOwnerID(rsSelectUser.getString("owner_id"));
                channelUserVO.setMsisdn(rsSelectUser.getString("msisdn"));
                channelUserVO.setPinRequired(rsSelectUser.getString("pin_required"));
                channelUserVO.setSmsPin(rsSelectUser.getString("sms_pin"));
                channelUserVO.setInvalidPinCount(rsSelectUser.getInt("invalid_pin_count"));
                channelUserVO.setEmpCode(rsSelectUser.getString("employee_code"));
                channelUserVO.setStatus(rsSelectUser.getString("userstatus"));
                channelUserVO.setCreatedBy(rsSelectUser.getString("created_by"));
                channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rsSelectUser.getTimestamp("created_on")));
                channelUserVO.setModifiedBy(rsSelectUser.getString("modified_by"));
                channelUserVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rsSelectUser.getTimestamp("modified_on")));
                channelUserVO.setContactPerson(rsSelectUser.getString("contact_person"));
                channelUserVO.setContactNo(rsSelectUser.getString("contact_no"));
                channelUserVO.setDesignation(rsSelectUser.getString("designation"));
                channelUserVO.setDivisionCode(rsSelectUser.getString("division"));
                channelUserVO.setDepartmentCode(rsSelectUser.getString("department"));
                channelUserVO.setUserType(rsSelectUser.getString("user_type"));
                channelUserVO.setInSuspend(rsSelectUser.getString("in_suspend"));
                channelUserVO.setOutSuspened(rsSelectUser.getString("out_suspend"));
                channelUserVO.setAddress1(rsSelectUser.getString("address1"));
                channelUserVO.setAddress2(rsSelectUser.getString("address2"));
                channelUserVO.setCity(rsSelectUser.getString("city"));
                channelUserVO.setState(rsSelectUser.getString("state"));
                channelUserVO.setCountry(rsSelectUser.getString("country"));
                channelUserVO.setSsn(rsSelectUser.getString("ssn"));
                channelUserVO.setUserNamePrefix(rsSelectUser.getString("user_name_prefix"));
                channelUserVO.setExternalCode(rsSelectUser.getString("external_code"));
                channelUserVO.setUserCode(rsSelectUser.getString("user_code"));
                channelUserVO.setShortName(rsSelectUser.getString("short_name"));
                channelUserVO.setReferenceID(rsSelectUser.getString("reference_id"));
                channelUserVO.setDomainID(rsSelectUser.getString("domain_code"));
                channelUserVO.setEmail(rsSelectUser.getString("email"));
                channelUserVO.setAllowedIps(rsSelectUser.getString("allowed_ip"));
                channelUserVO.setAllowedDays(rsSelectUser.getString("allowed_days"));
                channelUserVO.setAppointmentDate(rsSelectUser.getDate("appointment_date"));
                channelUserVO.setFromTime(rsSelectUser.getString("from_time"));
                channelUserVO.setToTime(rsSelectUser.getString("to_time"));

                channelUserVO.setTransferProfileID(rsSelectUser.getString("transfer_profile_id"));
                channelUserVO.setCommissionProfileSetID(rsSelectUser.getString("comm_profile_set_id"));
                channelUserVO.setUserGrade(rsSelectUser.getString("user_grade"));

                // for Zebra and Tango by sanjeew date 06/07/07
                channelUserVO.setApplicationID(rsSelectUser.getString("application_id"));
                channelUserVO.setMpayProfileID(rsSelectUser.getString("mpay_profile_id"));
                channelUserVO.setUserProfileID(rsSelectUser.getString("user_profile_id"));
                channelUserVO.setMcommerceServiceAllow(rsSelectUser.getString("mcommerce_service_allow"));
                // End Zebra and Tango

                final CategoryVO categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rsSelectUser.getString("category_code"));
                categoryVO.setCategoryName(rsSelectUser.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rsSelectUser.getString("domain_code"));
                categoryVO.setSequenceNumber(rsSelectUser.getInt("catseq"));
                categoryVO.setGrphDomainType(rsSelectUser.getString("grph_domain_type"));
                categoryVO.setSmsInterfaceAllowed(rsSelectUser.getString("sms_interface_allowed"));
                categoryVO.setWebInterfaceAllowed(rsSelectUser.getString("web_interface_allowed"));
                categoryVO.setAllowedGatewayTypes(MessageGatewayForCategoryCache.getMessagegatewayforcategoryList(categoryVO.getCategoryCode()));
                categoryVO.setHierarchyAllowed(rsSelectUser.getString("hierarchy_allowed"));
                categoryVO.setAgentAllowed(rsSelectUser.getString("agent_allowed"));
                categoryVO.setCategoryType(rsSelectUser.getString("category_type"));
                categoryVO.setRestrictedMsisdns(rsSelectUser.getString("restricted_msisdns"));
                categoryVO.setTransferToListOnly(rsSelectUser.getString("transfertolistonly"));
                channelUserVO.setCategoryVO(categoryVO);

                channelUserVO.setLanguageCode(rsSelectUser.getString("phone_language"));
                channelUserVO.setCountry(rsSelectUser.getString("country_cd"));
            }
        }// end of try
        catch (SQLException sqle) {
            _logger.error("loadUserDetailsFormUserID", "SQLException " + sqle.getMessage());
            _logger.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusRedemption[loadUserDetailsFormUserID]",
                "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", "loadUserDetailsFormUserID", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _logger.error("loadUserDetailsFormUserID", "Exception " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusRedemption[loadUserDetailsFormUserID]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", "loadUserDetailsFormUserID", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rsSelectUser != null) {
                    rsSelectUser.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadUserDetailsFormUserID", "Exiting channelUserVO:" + channelUserVO);
            }
        }// end of finally
        return channelUserVO;
    }

    /**
     * Method creditUserBalances()
     * This method check user maximum balance limits and Credit the user
     * Balances if limit does not cross by the
     * new balance (existing balance+new requested credit balance)
     * 
     * @author chetan.kothari
     * @param p_con
     *            Connection
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @return int
     * @throws BTSLBaseException
     */
    private static int creditUserBalances(Connection p_con, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {
        final String METHOD_NAME = "creditUserBalances";
        if (_logger.isDebugEnabled()) {
            _logger.debug("creditUserBalances", "Entered p_channelTransferVO : " + p_channelTransferVO);
        }
        int updateCount = 0;
        PreparedStatement handlerStmt = null;
        ResultSet rsUserBal = null;
        try {
            final TransferProfileDAO profileDAO = new TransferProfileDAO();
            TransferProfileProductVO transferProfileProductVO = null;
            long maxBalance = 0;
            final ArrayList itemsList = p_channelTransferVO.getChannelTransferitemsVOList();
            ChannelTransferItemsVO channelTransferItemsVO = null;
            String userID = null;
            String profileID = null;
            userID = p_channelTransferVO.getToUserID();
            profileID = p_channelTransferVO.getReceiverTxnProfile();
            final boolean isNotToExecuteQuery = false;
            for (int i = 0, k = itemsList.size(); i < k; i++) {
                channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
                _pstmtSelectUserBalOnly.clearParameters();
                _pstmtSelectUserBalOnly.setString(1, userID);
                _pstmtSelectUserBalOnly.setString(2, channelTransferItemsVO.getProductCode());
                _pstmtSelectUserBalOnly.setString(3, p_channelTransferVO.getNetworkCode());
                _pstmtSelectUserBalOnly.setString(4, p_channelTransferVO.getNetworkCodeFor());

                rsUserBal = _pstmtSelectUserBalOnly.executeQuery();
                long balance = -1;
                if (rsUserBal != null && rsUserBal.next()) {
                    balance = rsUserBal.getLong("balance");
                    channelTransferItemsVO.setBalance(balance);
                }

                if (balance > -1) {
                    channelTransferItemsVO.setPreviousBalance(balance);
                    channelTransferItemsVO.setAfterTransReceiverPreviousStock(balance);
                    balance += channelTransferItemsVO.getRequiredQuantity();
                } else {
                    channelTransferItemsVO.setPreviousBalance(0);
                    channelTransferItemsVO.setAfterTransReceiverPreviousStock(0);
                }

                // in the case of return we have not to check the max balance
                if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(p_channelTransferVO.getTransferType())) {
                    /*
                     * check for the max balance for the product
                     */
                    transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(profileID, channelTransferItemsVO.getProductCode());
                    maxBalance = transferProfileProductVO.getMaxBalanceAsLong();

                    if (maxBalance < balance) {
                        _userBalancesVO.setOtherInfo("User balance reached to maximum balance still giving balance");
                    }
                    // check for the very first txn of the user containg the
                    // order value larger than maxBalance
                    else if (balance == -1 && maxBalance < channelTransferItemsVO.getRequiredQuantity()) {
                        _userBalancesVO.setOtherInfo("User balance reached to maximum balance still giving balance");
                    }
                }
                if (!isNotToExecuteQuery) {
                    int m = 0;
                    // update
                    if (balance > -1) {
                        handlerStmt = _psmtUpdateUserBal;
                    } else {
                        // insert
                        handlerStmt = _psmtInsertUserBal;
                        balance = channelTransferItemsVO.getRequiredQuantity();
                        handlerStmt.clearParameters();
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
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                            "ActivationBonusRedemption[creditUserBalances]", "", "", "", "BTSLBaseException: update count <=0");
                        throw new BTSLBaseException("ActivationBonusRedemption", "creditUserBalances", "error.general.sql.processing");
                    }
                }
            }// for
            if (_userBalancesVO != null) {
                _userBalancesVO.setBalance(_userBalancesVO.getBalance() + PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
                BalanceLogger.log(_userBalancesVO);
            }
            p_channelTransferVO.setEntryType(PretupsI.CREDIT);
        } catch (BTSLBaseException bbe) {
            _logger.errorTrace(METHOD_NAME, bbe);
            throw bbe;
        } catch (SQLException sqe) {
            _logger.error("creditUserBalances", "SQLException : " + sqe);
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusRedemption[creditUserBalances]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", "creditUserBalances", "error.general.sql.processing");
        } catch (Exception ex) {
            _logger.error("creditUserBalances", "Exception : " + ex);
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusRedemption[creditUserBalances]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", "creditUserBalances", "error.general.processing");
        } finally {
        	try {
                if (rsUserBal != null) {
                	rsUserBal.close();
                }
            } catch (Exception e) {
            	_logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("creditUserBalances", "Exiting:  updateCount =" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method creditUserBalances()
     * This method prepares all the required PreparedStatements.
     * 
     * @author chetan.kothari
     * @param p_con
     *            Connection
     * @return void
     * @throws BTSLBaseException
     */

    private static void generatePreparedStatements(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "generatePreparedStatements";
        if (_logger.isDebugEnabled()) {
            _logger.debug("generatePreparedStatements", "Entered p_con : " + p_con);
        }

         StringBuffer selectUserQueryBuff = null; 
        
         String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
         boolean tcpOn = false;
         Set<String> uniqueTransProfileId = new HashSet();
         
         if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
         	tcpOn = true;
         }
         String sqlSelect = null;
         
         if(tcpOn) {

        	 selectUserQueryBuff = new StringBuffer("SELECT U.address1, U.address2, U.allowed_days, U.allowed_ip, U.appointment_date, U.batch_id,");
             selectUserQueryBuff.append(" U.category_code, U.city, U.contact_no, U.contact_person, U.country, U.created_by, U.created_on, U.creation_type, U.department, U.designation,");
             selectUserQueryBuff.append(" U.division, U.email, U.employee_code, U.external_code, U.from_time,  U.invalid_password_count, U.last_login_on, U.level1_approved_by,");
             selectUserQueryBuff.append(" U.level1_approved_on, U.level2_approved_by, U.level2_approved_on, U.login_id, U.modified_by, U.modified_on, U.network_code,");
             selectUserQueryBuff.append(" U.owner_id, U.parent_id, U.password, U.password_count_updated_on, U.previous_status, U.pswd_modified_on, U.reference_id, U.remarks,");
             selectUserQueryBuff
                 .append(" U.short_name, U.ssn, U.state, U.status userstatus, U.to_time, user_code, U.user_id, U.user_name, U.user_name_prefix, U.user_type, CU.activated_on,");
             selectUserQueryBuff.append(" CU.comm_profile_set_id, CU.contact_person, CU.in_suspend, CU.out_suspend, CU.outlet_code, CU.suboutlet_code, CU.transfer_profile_id,");
             selectUserQueryBuff.append(" CU.user_grade, C.agent_allowed, C.category_name, C.category_type, C.domain_allowed, C.domain_code, C.fixed_domains, C.fixed_roles,");
             selectUserQueryBuff
                 .append(" C.grph_domain_type, C.hierarchy_allowed, C.max_login_count, C.max_txn_msisdn, C.multiple_grph_domains, C.multiple_login_allowed, C.outlets_allowed, ");
             selectUserQueryBuff
                 .append(" C.parent_category_code, C.product_types_allowed, C.restricted_msisdns,C.scheduled_transfer_allowed, C.sequence_no catseq, C.services_allowed, ");
             selectUserQueryBuff
                 .append(" C.sms_interface_allowed, C.status, C.transfertolistonly, C.uncntrl_transfer_allowed,C.user_id_prefix, C.view_on_network_block, C.web_interface_allowed, ");
             selectUserQueryBuff
                 .append(" UP.msisdn, UP.description, UP.sms_pin, UP.pin_required, UP.phone_profile, UP.phone_language, UP.country country_cd, UP.invalid_pin_count, UP.last_transaction_status,");
             selectUserQueryBuff
                 .append(" UP.last_transaction_on, UP.pin_modified_on, UP.last_transfer_id, UP.last_transfer_type, UP.temp_transfer_id, UP.first_invalid_pin_time, ");
             selectUserQueryBuff.append(" geo.grph_domain_code,gdomains.status geostatus, ");
             selectUserQueryBuff.append(" CU.comm_profile_set_id,CU.transfer_profile_id, CU.user_grade,cset.status csetstatus, ");
             selectUserQueryBuff
                 .append(" cset.language_1_message comprf_lang_1_msg,cset.language_2_message  comprf_lang_2_msg,C.restricted_msisdns,gdt.sequence_no grphSeq, C.transfertolistonly, ");
             // for Zebra and Tango by sanjeew date 06/07/07
             selectUserQueryBuff.append(" CU.application_id, CU.mpay_profile_id, CU.user_profile_id, CU.mcommerce_service_allow ");
             // end of Zebra and Tango
             selectUserQueryBuff.append(" FROM users U,user_geographies geo, channel_users CU, categories C, user_phones UP, ");
             selectUserQueryBuff.append("domains dom,commission_profile_set cset,geographical_domains gdomains,geographical_domain_types gdt ");
             selectUserQueryBuff.append(" WHERE U.user_id=?  AND UP.user_id=u.user_id  AND CU.user_id=U.user_id  AND u.user_id=geo.user_id    ");
             selectUserQueryBuff.append(" AND UP.primary_number='Y' ");
             selectUserQueryBuff.append(" AND U.category_code=C.category_code AND geo.grph_domain_code=gdomains.grph_domain_code ");
             selectUserQueryBuff.append(" AND C.domain_code= dom.domain_code  AND CU.comm_profile_set_id=cset.comm_profile_set_id AND gdt.grph_domain_type=gdomains.grph_domain_type ");

  	
         }else {

        selectUserQueryBuff = new StringBuffer("SELECT U.address1, U.address2, U.allowed_days, U.allowed_ip, U.appointment_date, U.batch_id,");
        selectUserQueryBuff.append(" U.category_code, U.city, U.contact_no, U.contact_person, U.country, U.created_by, U.created_on, U.creation_type, U.department, U.designation,");
        selectUserQueryBuff.append(" U.division, U.email, U.employee_code, U.external_code, U.from_time,  U.invalid_password_count, U.last_login_on, U.level1_approved_by,");
        selectUserQueryBuff.append(" U.level1_approved_on, U.level2_approved_by, U.level2_approved_on, U.login_id, U.modified_by, U.modified_on, U.network_code,");
        selectUserQueryBuff.append(" U.owner_id, U.parent_id, U.password, U.password_count_updated_on, U.previous_status, U.pswd_modified_on, U.reference_id, U.remarks,");
        selectUserQueryBuff
            .append(" U.short_name, U.ssn, U.state, U.status userstatus, U.to_time, user_code, U.user_id, U.user_name, U.user_name_prefix, U.user_type, CU.activated_on,");
        selectUserQueryBuff.append(" CU.comm_profile_set_id, CU.contact_person, CU.in_suspend, CU.out_suspend, CU.outlet_code, CU.suboutlet_code, CU.transfer_profile_id,");
        selectUserQueryBuff.append(" CU.user_grade, C.agent_allowed, C.category_name, C.category_type, C.domain_allowed, C.domain_code, C.fixed_domains, C.fixed_roles,");
        selectUserQueryBuff
            .append(" C.grph_domain_type, C.hierarchy_allowed, C.max_login_count, C.max_txn_msisdn, C.multiple_grph_domains, C.multiple_login_allowed, C.outlets_allowed, ");
        selectUserQueryBuff
            .append(" C.parent_category_code, C.product_types_allowed, C.restricted_msisdns,C.scheduled_transfer_allowed, C.sequence_no catseq, C.services_allowed, ");
        selectUserQueryBuff
            .append(" C.sms_interface_allowed, C.status, C.transfertolistonly, C.uncntrl_transfer_allowed,C.user_id_prefix, C.view_on_network_block, C.web_interface_allowed, ");
        selectUserQueryBuff
            .append(" UP.msisdn, UP.description, UP.sms_pin, UP.pin_required, UP.phone_profile, UP.phone_language, UP.country country_cd, UP.invalid_pin_count, UP.last_transaction_status,");
        selectUserQueryBuff
            .append(" UP.last_transaction_on, UP.pin_modified_on, UP.last_transfer_id, UP.last_transfer_type, UP.temp_transfer_id, UP.first_invalid_pin_time, ");
        selectUserQueryBuff.append(" geo.grph_domain_code,gdomains.status geostatus, ");
        selectUserQueryBuff.append(" CU.comm_profile_set_id,CU.transfer_profile_id, tp.status tpstatus,CU.user_grade,cset.status csetstatus, ");
        selectUserQueryBuff
            .append(" cset.language_1_message comprf_lang_1_msg,cset.language_2_message  comprf_lang_2_msg,C.restricted_msisdns,gdt.sequence_no grphSeq, C.transfertolistonly, ");
        // for Zebra and Tango by sanjeew date 06/07/07
        selectUserQueryBuff.append(" CU.application_id, CU.mpay_profile_id, CU.user_profile_id, CU.mcommerce_service_allow ");
        // end of Zebra and Tango
        selectUserQueryBuff.append(" FROM users U,user_geographies geo, channel_users CU, categories C, user_phones UP, ");
        selectUserQueryBuff.append("domains dom,transfer_profile tp,commission_profile_set cset,geographical_domains gdomains,geographical_domain_types gdt ");
        selectUserQueryBuff.append(" WHERE U.user_id=?  AND UP.user_id=u.user_id  AND CU.user_id=U.user_id  AND u.user_id=geo.user_id    ");
        selectUserQueryBuff.append(" AND UP.primary_number='Y' ");
        selectUserQueryBuff.append(" AND U.category_code=C.category_code AND geo.grph_domain_code=gdomains.grph_domain_code ");
        selectUserQueryBuff.append(" AND C.domain_code= dom.domain_code AND CU.transfer_profile_id=tp.profile_id AND CU.comm_profile_set_id=cset.comm_profile_set_id AND gdt.grph_domain_type=gdomains.grph_domain_type ");

         }
        

        final StringBuffer strUpdateUserBalBuff = new StringBuffer();
        strUpdateUserBalBuff.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        strUpdateUserBalBuff.append(" last_transfer_no = ? , last_transfer_on = ? ");
        strUpdateUserBalBuff.append(" WHERE ");
        strUpdateUserBalBuff.append(" user_id = ? ");
        strUpdateUserBalBuff.append(" AND ");
        strUpdateUserBalBuff.append(" product_code = ? AND network_code_for = ? AND network_code = ?  ");

        final StringBuffer strInsertUserBalBuff = new StringBuffer();
        strInsertUserBalBuff.append(" INSERT ");
        strInsertUserBalBuff.append(" INTO user_balances ");
        strInsertUserBalBuff.append(" ( prev_balance,daily_balance_updated_on , balance, last_transfer_type, last_transfer_no, last_transfer_on , ");
        strInsertUserBalBuff.append(" user_id, product_code , network_code, network_code_for ) ");
        strInsertUserBalBuff.append(" VALUES ");
        strInsertUserBalBuff.append(" (?,?,?,?,?,?,?,?,?,?) ");

        final StringBuffer strInsertRedemptionBuff = new StringBuffer();
        strInsertRedemptionBuff.append(" INSERT INTO REDEMPTIONS(REFERENCE_ID,PROFILE_TYPE, REDEMPTION_TYPE,");
        strInsertRedemptionBuff.append(" REDEMPTION_ID, REDEMPTION_DATE, PRODUCT_CODE,  POINTS_REDEEMED,");
        strInsertRedemptionBuff.append(" AMOUNT_TRANSFERED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY,USER_ID_OR_MSISDN)");
        strInsertRedemptionBuff.append(" values (?,?,?,?,?,?,?,?,?,?,?,?,?)");

        final StringBuffer strSelectUserBalBuff = new StringBuffer(
            " SELECT USER_ID, NETWORK_CODE, NETWORK_CODE_FOR, PRODUCT_CODE, BALANCE, PREV_BALANCE, LAST_TRANSFER_TYPE, LAST_TRANSFER_NO, LAST_TRANSFER_ON, DAILY_BALANCE_UPDATED_ON ");
        strSelectUserBalBuff.append(" FROM user_balances ");
        strSelectUserBalBuff.append(" WHERE user_id = ? AND product_code = ? AND network_code_for = ? AND network_code = ? ");

        final StringBuffer strUpdateBonusBuff = new StringBuffer();
        strUpdateBonusBuff.append("update BONUS set LAST_REDEMPTION_ID=?,LAST_REDEMPTION_ON=?,POINTS=?,MODIFIED_ON=? where USER_ID_OR_MSISDN=? and PRODUCT_CODE=?  ");

        final String selectUserQuery = selectUserQueryBuff.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug("generatePreparedStatements", "select User query:" + selectUserQuery);
        }

        final String updateUserBalQuery = strUpdateUserBalBuff.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug("generatePreparedStatements", "Update User balance only query:" + updateUserBalQuery);
        }

        final String insertUserBalQuery = strInsertUserBalBuff.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug("generatePreparedStatements", "Insert User balance query:" + insertUserBalQuery);
        }

        ActivationBonusRedemptionQry strSelectUserBalOnlyBuff = (ActivationBonusRedemptionQry) ObjectProducer.getObject(QueryConstants.ACTIVATION_BONUS_REDEMPTION_QRY, QueryConstants.QUERY_PRODUCER);
        final String selectUserBalOnlyQuery = strSelectUserBalOnlyBuff.selectForUpdateUserBalance();
        if (_logger.isDebugEnabled()) {
            _logger.debug("generatePreparedStatements", "Select User balance query=" + selectUserBalOnlyQuery);
        }

        final String insertRedemptionQuery = strInsertRedemptionBuff.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug("generatePreparedStatements", "Insert redemptions query=" + insertRedemptionQuery);
        }

        final String selectUserBalQuery = strSelectUserBalBuff.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug("generatePreparedStatements", "select User balance query:" + selectUserBalQuery);
        }

        final String updateBonusQuery = strUpdateBonusBuff.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug("generatePreparedStatements", "Update Bonus query:" + updateBonusQuery);
        }

        try {
            _pstmtSelectUser = p_con.prepareStatement(selectUserQuery);
            _pstmtSelectUserBalOnly = p_con.prepareStatement(selectUserBalOnlyQuery);
            _psmtUpdateUserBal = p_con.prepareStatement(updateUserBalQuery);
            _psmtInsertUserBal = p_con.prepareStatement(insertUserBalQuery);
            _psmtInsertRedemption = p_con.prepareStatement(insertRedemptionQuery);
            _pstmtSelectUserBal = p_con.prepareStatement(selectUserBalQuery);
            _pstmtUpdateBonus = p_con.prepareStatement(updateBonusQuery);
        } catch (SQLException sqe) {
            _logger.error("generatePreparedStatements", "SQLException : " + sqe);
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusRedemption[generatePreparedStatements]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", "generatePreparedStatements", "error.general.sql.processing");
        } catch (Exception ex) {
            _logger.error("generatePreparedStatements", "Exception : " + ex);
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusRedemption[generatePreparedStatements]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", "generatePreparedStatements", "error.general.processing");
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("generatePreparedStatements", "Exiting:");
            }
        }
    }
}
