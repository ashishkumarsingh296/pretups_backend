package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.LoyaltyPointsLog;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyPointsRedemptionVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockDAO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

public class LmsPointsRedemptionProcess {
    private static Log _logger = LogFactory.getLog(LmsPointsRedemptionProcess.class.getName());
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;
    public static OperatorUtilI calculatorI = null;

    /**
     * ensures no instantiation
     */
    private LmsPointsRedemptionProcess(){
    	
    }
    
    /**
     * This method is main method for the process it gets all the command line
     * parameters and calls process method for ActivationBonus Redemption
     * process.
     * 
     * @author Gaurav Pandey
     * @param args
     *            String[]
     * @return void
     */
    public static void main(String[] args) {
        final String methodName = "main";
        try {
            if (args.length != 2) {
                _logger.info(methodName, "Usage : LmsPointsRedemption [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = Constants.validateFilePath(args[0]);
            if (!constantsFile.exists()) {
                _logger.info(methodName, " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = Constants.validateFilePath(args[1]);
            if (!logconfigFile.exists()) {
                _logger.info(methodName, " Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (Exception ex) {
            _logger.errorTrace(methodName, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            process();
        } catch (BTSLBaseException be) {
            _logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(methodName, be);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, " Exiting");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * This method is process method which contains all the processing regarding
     * Transfer of Bonus points to UserBalnce and deduction of stock .
     * 
     * @author Gaurav Pandey
     * @param void
     * @return void
     * @throws BTSLBaseException
     */

    private static void process() throws BTSLBaseException {
        final String methodName = "process";
        Date processedUpto = null;
        Date currentDate = null;
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        SimpleDateFormat sdf = null;
        Iterator bonusDataIterator = null;
        List bonusDataList = null;
        long amount = 0;
        ChannelUserVO parentUserVO = null;
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
        C2sBalanceQueryVO parentBalancesVO = null;
        final NetworkDAO networkDAO = new NetworkDAO();
        NetworkVO networkVO = null;
        final NetworkStockDAO networkStockDAO = new NetworkStockDAO();
        NetworkStockVO stockVO = null;
        final ChannelTransferVO channelTransferVo = new ChannelTransferVO();
        ProcessStatusVO _processStatusMISVO = null;
        Date processedUptoMIS = null;
        try {

            sdf = new SimpleDateFormat("MM/dd/yyyy");
            final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            try {
                calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[initialize]", "", "", "",
                    "Exception while loading the class at the call:" + e.getMessage());
            }
            _logger.debug("process", "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = new Date();
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LmsPointsRedemption[process]", "",
                    "", "", "DATABASE Connection is NULL");
                return;
            }
            _processBL = new ProcessBL();

            String lmsBonusMisCkReq = Constants.getProperty("LMSBOUNS_MIS_CHECK_REQUIRED");
            if (BTSLUtil.isNullString(lmsBonusMisCkReq)) {
                lmsBonusMisCkReq = PretupsI.YES;
            } else if ("null".equalsIgnoreCase(lmsBonusMisCkReq) || !PretupsI.NO.equalsIgnoreCase(lmsBonusMisCkReq)) {
                lmsBonusMisCkReq = PretupsI.YES;
            }
            if (PretupsI.YES.equals(lmsBonusMisCkReq)) {
                // Process should not execute until the MIS has not executed
                // successfully for previous day
                _processStatusMISVO = _processBL.checkProcessUnderProcess(con, ProcessI.C2SMIS);
                processedUptoMIS = _processStatusMISVO.getExecutedUpto();
                if (processedUptoMIS != null) {
                    con.rollback();
                    final Calendar cal4CurrentDate = BTSLDateUtil.getInstance();
                    final Calendar cal14MisExecutedUpTo = BTSLDateUtil.getInstance();
                    cal4CurrentDate.add(Calendar.DAY_OF_MONTH, -1);
                    final Date currentDate1 = cal4CurrentDate.getTime(); // Current
                    // Date
                    cal14MisExecutedUpTo.setTime(processedUptoMIS);
                    final Calendar cal24CurrentDate = BTSLDateUtil.getCalendar(cal4CurrentDate);
                    final Calendar cal34MisExecutedUpTo = BTSLDateUtil.getCalendar(cal14MisExecutedUpTo);
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(methodName, "(currentDate - 1) = " + currentDate1 + " processedUptoMIS = " + processedUptoMIS);
                    }
                    if (cal24CurrentDate.compareTo(cal34MisExecutedUpTo) != 0) {
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(methodName, "The MIS has not been executed for the previous day.");
                        }
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LmsPointsRedemptionProcess[process]", "",
                            "", "", "The MIS has not been executed for the previous day.");
                        throw new BTSLBaseException(methodName, methodName, PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
                    }
                } else {
                    throw new BTSLBaseException(methodName, methodName, PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
                }
            } else {
                throw new BTSLBaseException(methodName, methodName, PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
            }
            // getting process id
            processId = ProcessI.LMS_BONUS_PROCESSID;
            // method call to check status of the process
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            if (statusOk) {
                con.commit();
                // method call to find maximum date till which process has been
                // executed
                processedUpto = _processStatusVO.getExecutedUpto();

                if (processedUpto != null) {
                    if (sdf.format(processedUpto).equals(sdf.format(currentDate))) {
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LmsPointsRedemption[process]", "", "", "",
                            "Activation Bonus already run for the date =" + String.valueOf(currentDate));
                        throw new BTSLBaseException("LmsPointsRedemption", "process", "Activation Bonus already run for the date =" + String.valueOf(currentDate));
                    }
                    _processStatusVO.setStartDate(currentDate);
                    bonusDataList = getBonusDataList(con,processedUpto);
                    if (bonusDataList != null && bonusDataList.size() > 0) {
                        bonusDataIterator = bonusDataList.iterator();
                        LoyaltyPointsRedemptionVO redemptionVO = null;
                        while (bonusDataIterator.hasNext()) {
                            int userLoyaltyPointUpdateCount = 0;
                            final LoyaltyVO loyaltyVO = new LoyaltyVO();
                            redemptionVO = (LoyaltyPointsRedemptionVO) bonusDataIterator.next();
                            loaduserProfileRelatedDetails(con, redemptionVO);
                            if (BTSLUtil.isNullString(redemptionVO.getCurrentLoyaltyPoints())) {
                                continue;
                            }
                            final long requestedLoyaltyPoints = Long.valueOf(redemptionVO.getCurrentLoyaltyPoints());
							//Adding the below line to take the current HH:MM for each channel user to avoid unique constraint error 
							currentDate = new Date();	
                            loyaltyVO.setCreatedOn(currentDate);
                            loyaltyVO.setNetworkCode(redemptionVO.getNetworkID());
                            PretupsBL.generateLMSTransferID(loyaltyVO);
                            final String redmptionTxnId = loyaltyVO.getLmstxnid();
                            redemptionVO.setRedemptionID(redmptionTxnId);
                            // int lmsMultFactor =
                            // Integer.parseInt(Constants.getProperty("LMS_MULTIPLE_FACTOR"));
                            final double lmsMultFactor = Double.parseDouble(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_MULT_FACTOR)));
                            // amount = (int)(lmsMultFactor *
                            // requestedLoyaltyPoints);
                            amount = PretupsBL.getSystemAmount(lmsMultFactor * requestedLoyaltyPoints);
                            if (amount == 0) {
                            	LoyaltyPointsLog.log("LMS", redemptionVO.getUserID(), redemptionVO.getSetId(), currentDate, "CR", redemptionVO.getRedemptionID(), "0", "0",
                                    0l, 0l, 0l, "206", PretupsErrorCodesI.NOT_ENOUGH_POINTS_TO_REDEMPTION);

                                continue;
                            }

                            final long updatedUserPoints = (Long.valueOf(redemptionVO.getCurrentLoyaltyPoints())) - requestedLoyaltyPoints;
                            // Added By Diwakar on 10-MAR-2015
                            redemptionVO.setRedempLoyaltyPoint(Integer.parseInt(Long.toString(requestedLoyaltyPoints)));
                            // Ended here
                            redemptionVO.setCurrentLoyaltyPoints(Long.toString(updatedUserPoints));
                            redemptionVO.setModifiedOn(currentDate);
                            redemptionVO.setMultFactor(String.valueOf(lmsMultFactor));
                            loaduserProfileDetails(con, redemptionVO);
                            redemptionVO.setRedemptionDate(currentDate);
                            if (BTSLUtil.isNullString(redemptionVO.getSetId()) && BTSLUtil.isNullString(redemptionVO.getVersion())) {
                                LoyaltyPointsLog.log("LMS", redemptionVO.getUserID(), redemptionVO.getSetId(), currentDate, "CR", redemptionVO.getRedemptionID(), "0", "0",
                                    0l, 0l, 0l, "206", PretupsErrorCodesI.ASSOCIATED_PROFILE_NOT_ACTIVE);

                                continue;
                            }
                            final long parentCont = (amount) * redemptionVO.getParentContribution() / 100;
                            final long optcont = (amount) * redemptionVO.getOperatorContribution() / 100;
                            redemptionVO.setO2cContribution(optcont);
                            redemptionVO.setC2cContribution(parentCont);
                            if (redemptionVO.getParentContribution() != 0 && !("ROOT".equals(redemptionVO.getParentID()))) {

                                loadLMSParentUserDetails(con, redemptionVO);
                                long sumAmount = 0L;
                                // long
                                // parentCont=(amount)*redemptionVO.getParentContribution()/100;
                                // long
                                // optcont=(amount)*redemptionVO.getOperatorContribution()/100;
                                // redemptionVO.setO2cContribution(optcont);
                                // redemptionVO.setC2cContribution(parentCont);
                                final String parentMSISDN = redemptionVO.getParentMsisdn();
                                // ChannelUserVO parentUserVO = null;
                                parentUserVO = channelUserDAO.loadChannelUserDetails(con, parentMSISDN);
                                if (parentUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND) || parentUserVO.getStatus().equalsIgnoreCase(
                                    PretupsI.USER_STATUS_DELETED) || parentUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_CANCELED)) {

                                    LoyaltyPointsLog.log("LMS", redemptionVO.getUserID(), redemptionVO.getSetId(), currentDate, "CR", redemptionVO.getRedemptionID(), "0",
                                        "0", 0l, 0l, 0l, PretupsErrorCodesI.PARENT_USER_IS_NOT_ACTIVE, "");
                                    continue;
                                }
                                final ArrayList balanceList = userBalancesDAO.loadUserBalances(con, parentUserVO.getUserID());
                                List<TransferProfileProductVO> profileList = new TransferProfileDAO().loadTransferProfileProductsList(con, parentUserVO.getTransferProfileID());
								final String product = redemptionVO.getProductCode();
                                if ((balanceList != null) && (balanceList.size() > 0)) {
                                	int balanceListSizes=balanceList.size();
                                    for (int k = 0, l =balanceListSizes ; k < l; k++) {
                                        parentBalancesVO = (C2sBalanceQueryVO) balanceList.get(k);
                                        if (parentBalancesVO.getProductCode().equalsIgnoreCase(product)) {
                                            parentBalancesVO = null;
                                            parentBalancesVO = (C2sBalanceQueryVO) balanceList.get(k);
                                            for (int i = 0; i < profileList.size(); i++) {
                                            	TransferProfileProductVO transferProfileProdductVO = profileList.get(i);
                                            	if(transferProfileProdductVO.getProductCode().equalsIgnoreCase(product) && ((parentBalancesVO.getBalance() - parentCont)<= transferProfileProdductVO.getMinResidualBalanceAsLong())){
                                            		 LoyaltyPointsLog.log("LMS", redemptionVO.getUserID(), redemptionVO.getSetId(), currentDate, "CR", redemptionVO.getRedemptionID(), "0",
                                                             "0", 0l, 0l, 0l, PretupsErrorCodesI.CHNL_ERROR_SNDR_INSUFF_BALANCE, "");
                                                         continue;
                								}
                							}
                                        }
                                    }

                                    if (parentBalancesVO.getBalance() < parentCont) {

                                        LoyaltyPointsLog.log("LMS", redemptionVO.getUserID(), redemptionVO.getSetId(), currentDate, "CR", redemptionVO.getRedemptionID(), "0",
                                            "0", 0l, 0l, 0l, PretupsErrorCodesI.CHNL_ERROR_SNDR_INSUFF_BALANCE, "");
                                        continue;

                                    }

                                } else {
                                    LoyaltyPointsLog.log("LMS", redemptionVO.getUserID(), redemptionVO.getSetId(), currentDate, "CR", redemptionVO.getRedemptionID(), "0",
                                        "0", 0l, 0l, 0l, PretupsErrorCodesI.CHNL_ERROR_SNDR_INSUFF_BALANCE, "");
                                    continue;

                                }
                                // / network check
                                ArrayList stocklist = new ArrayList();
                                networkVO = networkDAO.loadNetwork(con, redemptionVO.getNetworkID());
                                stocklist = networkStockDAO.loadCurrentStockList(con, networkVO.getNetworkCode(), networkVO.getNetworkCode(), networkVO.getNetworkType());
                                int stockListSizes=stocklist.size();
                                for (int i = 0; i <stockListSizes ; i++) {
                                    stockVO = (NetworkStockVO) stocklist.get(i);
                                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
                                        if (PretupsI.FOC_WALLET_TYPE.equals(stockVO.getWalletType())) {
                                            break;
                                        }
                                    } else {
                                        if (PretupsI.SALE_WALLET_TYPE.equals(stockVO.getWalletType())) {
                                            break;
                                        }
                                    }
                                }

                                if (stockVO.getWalletbalance() <= optcont) {
                                    LoyaltyPointsLog.log("LMS", redemptionVO.getUserID(), redemptionVO.getSetId(), currentDate, "CR", redemptionVO.getRedemptionID(), "0",
                                        "0", 0l, 0l, 0l, PretupsErrorCodesI.ERROR_NW_STOCK_LESS, "");
                                    break;

                                }

                                performC2C(con, parentUserVO, redemptionVO, parentBalancesVO);

                                final String c2cTxnId = redemptionVO.getReferenceNo();
                                if (!BTSLUtil.isNullString(c2cTxnId)) {
                                    perfomFOC(con, parentUserVO, redemptionVO, stockVO);
                                }

                                final String focTxnId = redemptionVO.getReferenceNo();
                                if (!BTSLUtil.isNullString(c2cTxnId) && !BTSLUtil.isNullString(c2cTxnId)) {

                                    redemptionVO.setTxnStatus("200");
                                    sumAmount = parentCont + optcont;
                                    redemptionVO.setSumAmount(sumAmount);
                                } else {
                                    redemptionVO.setTxnStatus("206");

                                }

                                if ("200".equals(redemptionVO.getTxnStatus())) {

                                    userLoyaltyPointUpdateCount = updateUserLoyaltyPointsDetail(redemptionVO);
                                    if (userLoyaltyPointUpdateCount == 1) {

                                        userLoyaltyPointUpdateCount = insertRedemtionDetails(con, redemptionVO);
                                    }

                                    if (userLoyaltyPointUpdateCount == 1) {

                                        con.commit();
                                        try {
                                            Locale locale = null;
											//Handling for sending the message into the user defined language if configured into the system other wise system wise
											if(BTSLUtil.isNullString(redemptionVO.getUserLanguage())){
                                            	locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
											} else {
												locale =new Locale(redemptionVO.getUserLanguage(),redemptionVO.getUserCountry());
											}
                                            String messageRED = null; // /
                                            // String arr[]=
                                            // {String.valueOf(redemptionVO.getRedempLoyaltyPoint())};
                                            final String arr[] = { String.valueOf(requestedLoyaltyPoints), redemptionVO.getProductCode() };
                                            messageRED = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_REDEM_MESSAGE, arr);
                                            final PushMessage pushMessage = new PushMessage(redemptionVO.getMsisdn(), messageRED, null, null, locale);
                                            pushMessage.push();
                                            LoyaltyPointsLog.log("LMS", redemptionVO.getUserID(), redemptionVO.getSetId(), currentDate, "CR", redemptionVO.getRedemptionID(),
                                                "0", "0", redemptionVO.getRedempLoyaltyPoint(), 0l, redemptionVO.getO2cContribution(), PretupsErrorCodesI.TXN_STATUS_SUCCESS,
                                                "");
                                        } catch (Exception e) {
                                            _logger.errorTrace(methodName, e);
                                        }
                                    } else {
                                        con.rollback();
                                    }

                                }

                            }

                            else {
                                redemptionVO.setO2cContribution(amount);
                                ArrayList stocklist = new ArrayList();
                                networkVO = networkDAO.loadNetwork(con, redemptionVO.getNetworkID());
                                stocklist = networkStockDAO.loadCurrentStockList(con, networkVO.getNetworkCode(), networkVO.getNetworkCode(), networkVO.getNetworkType());
                                stockVO = (NetworkStockVO) stocklist.get(0);
                                if (stockVO.getWalletbalance() <= redemptionVO.getO2cContribution()) {
                                    final BTSLMessages messages = new BTSLMessages("message.transaction.lms.failed.network.stock", "initiate");

                                }
                                perfomFOC(con, parentUserVO, redemptionVO, stockVO);
                                final String focTxnId = redemptionVO.getReferenceNo();
                                if (!BTSLUtil.isNullString(focTxnId)) {
                                    if ("200".equals(redemptionVO.getTxnStatus())) {

                                        // redemptionVO.setSumAmount(Long.valueOf(redemptionVO.getRedempLoyaltyAmount()));
                                        redemptionVO.setSumAmount(amount);
                                        userLoyaltyPointUpdateCount = updateUserLoyaltyPointsDetail(redemptionVO);
                                        if (userLoyaltyPointUpdateCount == 1) {
                                            userLoyaltyPointUpdateCount = insertRedemtionDetails(con, redemptionVO);
                                        }
                                        if (userLoyaltyPointUpdateCount == 1) {
                                            con.commit();
                                            // //push
                                            try {
                                                Locale locale = null;
                                            	//Handling for sending the message into the user defined language if configured into the system other wise system wise
    											if(BTSLUtil.isNullString(redemptionVO.getUserLanguage())){
    												locale =new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
    											} else {
    												locale =new Locale(redemptionVO.getUserLanguage(),redemptionVO.getUserCountry());
    											}
                                                String messageRED = null; // /
                                                final String arr[] = { String.valueOf(requestedLoyaltyPoints), redemptionVO.getProductCode() };
                                                messageRED = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_REDEM_MESSAGE, arr);
                                                final PushMessage pushMessage = new PushMessage(redemptionVO.getMsisdn(), messageRED, null, null, locale);
                                                pushMessage.push();
                                            } catch (Exception e) {
                                                _logger.errorTrace(methodName, e);
                                            }
                                        } else {
                                            con.rollback();
                                        }
                                    }
                                } else {
                                    redemptionVO.setTxnStatus("206");
                                }

                            }
                            if (userLoyaltyPointUpdateCount > 0 && "200".equals(redemptionVO.getTxnStatus())) {

                                redemptionVO.setRedempStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                                redemptionVO.setErrorCode(null);
                                redemptionVO.setRedempStatus(PretupsI.SUCCESS);
                                LoyaltyPointsLog.log("LMS", redemptionVO.getUserID(), redemptionVO.getSetId(), currentDate, "CR", redemptionVO.getRedemptionID(), "0", "0",
                                    redemptionVO.getRedempLoyaltyPoint(), 0l, redemptionVO.getO2cContribution(), PretupsErrorCodesI.TXN_STATUS_SUCCESS, "");

                            } else {
                                redemptionVO.setRedempStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                                redemptionVO.setErrorCode(PretupsErrorCodesI.LOYALTY_REDEMPTION_CALULATION_EXCEPTION);
                                redemptionVO.setRedempStatus(PretupsI.FAIL);
                            }
                        }// end if to check that user is active or not
                    }// end for bonusdatalist
                } else {
                    throw new BTSLBaseException("LmsPointsRedemption", "process", "no user found for redemption");
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LmsPointsRedemption[process]", "", "", "",
                    " LmsPointsRedemption process has been executed successfully.");
            } else {
                throw new BTSLBaseException("LmsPointsRedemption", "process", "Activation Bonus process could not find the date till which process has been executed.");
            }

        }// end of try
        catch (BTSLBaseException be) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            _logger.error("process", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ex) {
                _logger.errorTrace(methodName, ex);
            }
            _logger.error("process", "Exception : " + e.getMessage());
            _logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LmsPointsRedemption[process]", "", "", "",
                " LmsPointsRedemption process could not be executed successfully.");
            throw new BTSLBaseException("LmsPointsRedemption", "process", PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
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
                            _logger.errorTrace(methodName, e);
                        }
                    } else {
                        try {
                            if (con != null) {
                                con.rollback();
                            }
                        } catch (Exception e) {
                            _logger.errorTrace(methodName, e);
                        }
                    }
                } catch (Exception e) {
                    _logger.errorTrace(methodName, e);
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception ex) {
                    _logger.errorTrace(methodName, ex);
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("process", "Exception closing connection ");
                    }
                }
            }

            _logger.debug("process", "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            _logger.debug("process", "Exiting..... ");
        }

    }

    /**
     * This method will get list of bonus data which is in range of given
     * duration and points according to system preferences .
     * 
     * @author Gaurav Pandey
     * @param p_con
     *            Connection
	 * @param processedUpto 
     * @return List
     * @throws BTSLBaseException
     */

    public static List getBonusDataList(Connection p_con, Date processedUpto) throws BTSLBaseException {
        final String methodName = "getBonusDataList";
        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName, " Entered:  p_con:" + p_con);
        }
        PreparedStatement psm = null;
        ResultSet rs = null;
        ArrayList bonusList = null;
        // ActivationBonusVO bonusVO=null;
        LoyaltyPointsRedemptionVO redempVO = null;
        try {
            bonusList = new ArrayList();
            LmsPointsRedemptionProcessQry lmsPointsRedemptionProcessQry = (LmsPointsRedemptionProcessQry)ObjectProducer.getObject(QueryConstants.LMS_POINTS_REDMPTION_PROCESS_QRY, QueryConstants.QUERY_PRODUCER);
            String query = lmsPointsRedemptionProcessQry.getBonusDataListQry();
            psm = p_con.prepareStatement(query.toString());
			psm.setDate(1,BTSLUtil.getSQLDateFromUtilDate(processedUpto));
			psm.setDate(2,BTSLUtil.getSQLDateFromUtilDate(new Date()));
            rs = psm.executeQuery();
            while (rs.next()) {
                redempVO = new LoyaltyPointsRedemptionVO();
                redempVO.setUserID(rs.getString("USER_ID_OR_MSISDN"));
                redempVO.setRedemptionDate(rs.getDate("bonus_date"));
				redempVO.setProductCode(rs.getString("PRODUCT_CODE"));
                bonusList.add(redempVO);
            }
        } catch (SQLException sqe) {
            _logger.error(methodName, "SQLException " + sqe.getMessage());
            _logger.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LmsPointsRedemption[getBonusDataList]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("LmsPointsRedemption", methodName, PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _logger.error(methodName, "Exception : " + ex.getMessage());
            _logger.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LmsPointsRedemption[getBonusDataList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException("LmsPointsRedemption", methodName, PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    _logger.errorTrace(methodName, ex);
                }
            }
            if (psm != null) {
                try {
                    psm.close();
                } catch (Exception ex) {
                    _logger.errorTrace(methodName, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exiting bonusList.size:" + bonusList.size());
            }
        }// end of finally
        return bonusList;
    }

    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) throws BTSLBaseException {
        final String methodName = "markProcessStatusAsComplete";
        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName, " Entered:  p_processId:" + p_processId);
        }
        int updateCount = 0;
        final Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        // Added by Diwakar for correcting the process executed_upto value
        _processStatusVO.setExecutedUpto(BTSLUtil.addDaysInUtilDate(currentDate, -1));
        _processStatusVO.setExecutedOn(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(methodName, e);
            _logger.error(methodName, "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LmsPointsRedemption[markProcessStatusAsComplete]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LmsPointsRedemption", methodName, PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    public static LoyaltyPointsRedemptionVO loaduserProfileRelatedDetails(Connection p_con, LoyaltyPointsRedemptionVO loyaltyPointsRedemptionVO) throws BTSLBaseException {

        final String methodName = "loaduserProfileRelatedDetails";
        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName, "Entered ");
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            final StringBuffer selectQueryBuff = new StringBuffer(" select bn.ACCUMULATED_POINTS, ");
            selectQueryBuff.append(" u.NETWORK_CODE,u.msisdn,pd.product_short_code  ");
            selectQueryBuff.append(" from bonus bn, users u,products pd  ");
            selectQueryBuff.append("  where u.user_id=? and bn.POINTS_DATE=? AND  u.USER_ID=bn.USER_ID_OR_MSISDN and u.status='Y' ");
            selectQueryBuff.append(" and bn.product_code=pd.product_code ");
			selectQueryBuff.append(" and bn.product_code=? ");
            final String selectQuery = selectQueryBuff.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadLMSUserDetails", "select query:" + selectQuery);
            }

            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, loyaltyPointsRedemptionVO.getUserID());
            pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(loyaltyPointsRedemptionVO.getRedemptionDate()));
			pstmtSelect.setString(3, loyaltyPointsRedemptionVO.getProductCode());
            rs = pstmtSelect.executeQuery();

            if (rs.next()) {

                loyaltyPointsRedemptionVO.setCurrentLoyaltyPoints(rs.getString("ACCUMULATED_POINTS"));
                loyaltyPointsRedemptionVO.setNetworkID(rs.getString("NETWORK_CODE"));
                loyaltyPointsRedemptionVO.setMsisdn(rs.getString("msisdn"));
                loyaltyPointsRedemptionVO.setProductShortCode(rs.getString("PRODUCT_SHORT_CODE"));
            }
            return loyaltyPointsRedemptionVO;
        } catch (SQLException sqle) {
            _logger.error(methodName, "SQLException " + sqle.getMessage());
            _logger.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _logger.error(methodName, "Exception " + e.getMessage());
            _logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exiting loyaltyPointsRedemptionVO: " + loyaltyPointsRedemptionVO);
            }
        }
    }

    /*
     * public static String
     * initaiateFocRedemptionRequest(LoyaltyPointsRedemptionVO p_redemptionVO)
     * throws BTSLBaseException { if (_logger.isDebugEnabled())
     * _logger.debug("initaiateFocRedemptionRequest", "Entered"); String
     * o2cFocTxnId = null; HttpURLConnection con=null; BufferedReader in=null;
     * InstanceLoadVO instanceLoadVO=null; String urlToSend=null; String
     * msisdn=null; String msisdnPrefix=null; String httpURLPrefix="http://";
     * String msgGWPass=null; String requestXML =null; String responseStr=null;
     * String finalResponse=""; String response =null; try { msisdn=
     * p_redemptionVO.getMsisdn(); msisdnPrefix =
     * PretupsBL.getMSISDNPrefix(msisdn); requestXML =
     * generateRequestFocXML(p_redemptionVO); MessageGatewayVO
     * messageGatewayVO=MessageGatewayCache.getObject(PretupsI.
     * REQUEST_SOURCE_TYPE_EXTGW);
     * if (_logger.isDebugEnabled())
     * _logger.debug("initaiateFocRedemptionRequest", "messageGatewayVO:
     * "+messageGatewayVO);
     * 
     * if(messageGatewayVO==null) throw new
     * BTSLBaseException("LoyaltyPointsRedemptionAction",
     * "initaiateFocRedemptionRequest"
     * ,PretupsErrorCodesI.ERROR_NOTFOUND_MESSAGEGATEWAY);
     * RequestGatewayVO requestGatewayVO=messageGatewayVO.getRequestGatewayVO();
     * if(requestGatewayVO==null) throw new
     * BTSLBaseException("LoyaltyPointsRedemptionAction",
     * "initaiateFocRedemptionRequest"
     * ,PretupsErrorCodesI.ERROR_NOTFOUND_REQMESSAGEGATEWAY);
     * 
     * if(!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus())) throw
     * new BTSLBaseException( "initaiateFocRedemptionRequest",
     * "c2stranfer.c2srecharge.error.messagegatewaynotactive","c2sRecharge");
     * else
     * if(!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getRequestGatewayVO().
     * getStatus()))
     * throw new BTSLBaseException( "initaiateFocRedemptionRequest",
     * "c2stranfer.c2srecharge.error.reqmessagegatewaynotactive","c2sRecharge");
     * 
     * //If Encrypted Password check box is checked. i.e. send password in
     * request as encrypted.
     * if(messageGatewayVO.getReqpasswordtype().equalsIgnoreCase(PretupsI.
     * SELECT_CHECKBOX))
     * msgGWPass
     * =BTSLUtil.decryptText(messageGatewayVO.getRequestGatewayVO().getPassword()
     * );
     * else msgGWPass=messageGatewayVO.getRequestGatewayVO().getPassword();
     * 
     * 
     * String networkCode=p_redemptionVO.getNetworkID(); String
     * smsInstanceID=null; smsInstanceID=Constants.getProperty("INSTANCE_ID");
     * instanceLoadVO=LoadControllerCache.getInstanceLoadForNetworkHash(
     * smsInstanceID+"_"+networkCode+"_"+PretupsI.REQUEST_SOURCE_TYPE_SMS);
     * if(instanceLoadVO==null)
     * instanceLoadVO=LoadControllerCache.getInstanceLoadForNetworkHash(
     * smsInstanceID+"_"+networkCode+"_"+PretupsI.REQUEST_SOURCE_TYPE_WEB);
     * if(instanceLoadVO==null)//Entry for Dummy(used for Apache)
     * instanceLoadVO=LoadControllerCache.getInstanceLoadForNetworkHash(
     * smsInstanceID+"_"+networkCode+"_"+PretupsI.REQUEST_SOURCE_TYPE_DUMMY);
     * 
     * 
     * 
     * StringBuffer sbf1 = new StringBuffer();
     * 
     * sbf1.append("REQUEST_GATEWAY_CODE="+messageGatewayVO.getGatewayCode());
     * sbf1.append("&REQUEST_GATEWAY_TYPE="+messageGatewayVO.getGatewayType());
     * sbf1.append("&LOGIN="+messageGatewayVO.getRequestGatewayVO().getLoginID())
     * ;
     * sbf1.append("&PASSWORD="+msgGWPass ); sbf1.append(
     * "&SERVICE_PORT="+messageGatewayVO.getRequestGatewayVO().getServicePort());
     * sbf1.append("&SOURCE_TYPE="+PretupsI.REQUEST_SOURCE_TYPE_EXTGW); String
     * authorization = sbf1.toString(); if(instanceLoadVO==null) {
     * _logger.error("LoyaltyPointsRedemptionAction"," Not able to get the
     * instance detaile for the network="+networkCode+" where the request for
     * o2c needs to be send"); throw new
     * BTSLBaseException("LoyaltyPointsRedemptionAction",
     * "initaiateFocRedemptionRequest"
     * ,PretupsErrorCodesI.INSTANCE_CODE_NOT_FOUND); }
     * else {
     * urlToSend=httpURLPrefix+instanceLoadVO.getHostAddress()+":"+instanceLoadVO
     * .getHostPort()+Constants.getProperty("CHANNEL_WEB_RECHARGE_SERVLET")+"?";
     * }
     * 
     * try { String encodeUrl = URLEncoder.encode(urlToSend); URL url = new
     * URL(urlToSend); URLConnection uc = url.openConnection(); con =
     * (HttpURLConnection) uc; con.addRequestProperty("Content-Type",
     * "text/xml"); con.addRequestProperty("Authorization", authorization);
     * con.setUseCaches(false); con.setDoInput(true); con.setDoOutput(true);
     * con.setRequestMethod("POST"); BufferedWriter wr = new BufferedWriter(new
     * OutputStreamWriter(con.getOutputStream(), "UTF8")); // Send data
     * wr.write(requestXML); wr.flush(); // Get response in= new
     * BufferedReader(new InputStreamReader(con.getInputStream())); while
     * ((responseStr = in.readLine()) !=null)
     * {finalResponse=finalResponse+responseStr ;} wr.close(); in.close(); }
     * catch (Exception e) {_log.errorTrace(methodName,e);} finally { if(con !=
     * null){con.disconnect();} }
     * 
     * if(!BTSLUtil.isNullString(finalResponse)) { int
     * index=finalResponse.indexOf("<TXNID>");
     * o2cFocTxnId=finalResponse.substring(index+"<TXNID>".length(),finalResponse
     * .indexOf("</TXNID>",index));
     * index=finalResponse.indexOf("<TXNSTATUS>"); String
     * focO2cTxnStatus=finalResponse.substring(index+"<TXNSTATUS>".length(),
     * finalResponse.indexOf("</TXNSTATUS>",index));
     * 
     * response=o2cFocTxnId +"@"+focO2cTxnStatus; if (_logger.isDebugEnabled())
     * _logger.debug("LoyaltyPointsRedemptionAction",
     * "initaiateFocRedemptionRequest: o2cFocTxnId="+o2cFocTxnId+"
     * focO2cTxnStatus= "+focO2cTxnStatus); } else { o2cFocTxnId=null;
     * response=null; } } catch(BTSLBaseException bse) {
     * bs_log.errorTrace(methodName,e); } catch(Exception e) {
     * _log.errorTrace(methodName,e); } finally { if (_logger.isDebugEnabled())
     * _logger.debug("initaiateFocRedemptionRequest" , "Exiting response="
     * +response); } return response; }
     * 
     * 
     * public static String generateRequestFocXML(LoyaltyPointsRedemptionVO
     * p_redempVO) { if (_logger.isDebugEnabled())
     * _logger.debug("initaiateFocRedemptionRequest", "Entered"); String
     * requesStr=null; StringBuffer sbf=null; try { Random exttxnno = new
     * Random(); Random refno = new Random(); Random pinstno = new Random();
     * 
     * sbf=new StringBuffer(); sbf.append("<?xml version=\"1.0\"?><!DOCTYPE
     * COMMAND PUBLIC
     * \"-//Ocam//DTD XML Command1.0//EN\" \"xml/command.dtd\"><COMMAND>");
     * sbf.append("<TYPE>O2CINTREQ</TYPE>");
     * sbf.append("<EXTNWCODE>"+p_redempVO.getNetworkID()+"</EXTNWCODE>");
     * sbf.append("<MSISDN>"+p_redempVO.getMsisdn()+"</MSISDN>");
     * sbf.append("<PIN>"+((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN))+"</PIN>");
     * if(!BTSLUtil.isNullString(p_redempVO.getExternalCode()))
     * sbf.append("<EXTCODE>"+p_redempVO.getExternalCode()+"</EXTCODE>");
     * else sbf.append("<EXTCODE></EXTCODE>");
     * 
     * sbf.append("<EXTTXNNUMBER>"+
     * exttxnno.nextInt(1000000)+"</EXTTXNNUMBER>");
     * sbf.append("<EXTTXNDATE>"+BTSLUtil.getDateStringFromDate(p_redempVO.
     * getRedemptionDate()) +"</EXTTXNDATE>");
     * 
     * sbf.append("<PRODUCTS>");
     * sbf.append("<PRODUCTCODE>"+p_redempVO.getProductShortCode
     * ()+"</PRODUCTCODE>");
     * sbf.append("<QTY>"+p_redempVO.getO2cContribution()+"</QTY>");
     * sbf.append("</PRODUCTS>");
     * 
     * sbf.append("<TRFCATEGORY>FOC</TRFCATEGORY>");
     * sbf.append("<REFNUMBER>"+refno.nextInt(1000000)+"</REFNUMBER>");
     * sbf.append("<PAYMENTDETAILS>");
     * sbf.append("<PAYMENTTYPE>CHQ</PAYMENTTYPE>");
     * sbf.append("<PAYMENTINSTNUMBER>"+
     * pinstno.nextInt(1000)+"</PAYMENTINSTNUMBER>");
     * sbf.append("<PAYMENTDATE>"+BTSLUtil.getDateStringFromDate(p_redempVO.
     * getRedemptionDate()) +"</PAYMENTDATE>");
     * sbf.append("</PAYMENTDETAILS>");
     * sbf.append("<REMARKS>"+PretupsI.LMSFOCO2C+"</REMARKS>");
     * sbf.append("</COMMAND>"); requesStr = sbf.toString(); } catch(Exception
     * ex) { ex.printStackTrace(); } finally { if (_logger.isDebugEnabled())
     * _logger.debug("generateRequestFocXML" , "Exiting requesStr=" +requesStr);
     * }
     * return requesStr; }
     */

    public static LoyaltyPointsRedemptionVO loadLMSParentUserDetails(Connection p_con, LoyaltyPointsRedemptionVO loyaltyPointsRedemptionVO) throws BTSLBaseException {

        final String methodName = "loadLMSParentUserDetails";
        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName, "Entered ");
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            final StringBuffer selectQueryBuff = new StringBuffer(
                "select u.msisdn,u.user_id,up.sms_pin from users u,user_phones up where u.user_id=? and u.user_id=up.user_id ");

            final String selectQuery = selectQueryBuff.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "select query:" + selectQuery);
            }

            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, loyaltyPointsRedemptionVO.getParentID());
            rs = pstmtSelect.executeQuery();

            if (rs.next()) {

                loyaltyPointsRedemptionVO.setParentMsisdn(rs.getString("msisdn"));
                loyaltyPointsRedemptionVO.setParentEncryptedPin(rs.getString("sms_pin"));
            }
            return loyaltyPointsRedemptionVO;
        } catch (SQLException sqle) {
            _logger.error(methodName, "SQLException " + sqle.getMessage());
            _logger.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("loaduserProfileRelatedDetails", "error.general.sql.processing");
        } catch (Exception e) {
            _logger.error(methodName, "Exception " + e.getMessage());
            _logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("loaduserProfileRelatedDetails", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exiting loyaltyPointsRedemptionVO: " + loyaltyPointsRedemptionVO);
            }
        }
    }

    public static int updateUserLoyaltyPointsDetail(LoyaltyPointsRedemptionVO p_loyaltyPointsRedemptionVO) throws BTSLBaseException {
        final String methodName = "updateUserLoyaltyPointsDetail";
        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName, "Entered ");
        }
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        int count = 0;
        try {
            LmsPointsRedemptionProcessQry lmsPointsRedemptionProcessQry = (LmsPointsRedemptionProcessQry)ObjectProducer.getObject(QueryConstants.LMS_POINTS_REDMPTION_PROCESS_QRY, QueryConstants.QUERY_PRODUCER);

        	String selectBuff =  lmsPointsRedemptionProcessQry.updateUserLoyaltyPointsDetailQry();
            final String selectQuery = selectBuff.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "selectQuery " + selectQuery);
            }
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            short index = 1;
            pstmt = con.prepareStatement(selectQuery);
            pstmt.setString(index++, p_loyaltyPointsRedemptionVO.getUserID());
            pstmt.setString(index++, p_loyaltyPointsRedemptionVO.getUserID());
			pstmt.setString(index++,p_loyaltyPointsRedemptionVO.getProductCode());
			pstmt.setString(index++,p_loyaltyPointsRedemptionVO.getProductCode());
            rs = pstmt.executeQuery();
            long accuPoint = 0L;
            if (rs.next()) {
                accuPoint = rs.getLong("ACCUMULATED_POINTS");
            }

            pstmt.clearParameters();

            long currAccuPoint = Long.parseLong(String.valueOf(p_loyaltyPointsRedemptionVO.getRedempLoyaltyPoint()));
            currAccuPoint = accuPoint - currAccuPoint;
            if (_logger.isDebugEnabled()) {
                _logger
                    .debug(methodName, " Point redeemed for user : " + p_loyaltyPointsRedemptionVO.getRedempLoyaltyPoint() + " , currently user have points = " + accuPoint);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, " current Points which user has after perfomring redeemption: " + currAccuPoint);
            }

            final StringBuffer updateBuff = new StringBuffer("UPDATE bonus SET  ACCUMULATED_POINTS= ?,LAST_REDEMPTION_ID= ?, LAST_REDEMPTION_ON=? , POINTS=?, TRANSFER_ID=? ");
			updateBuff.append("WHERE USER_ID_OR_MSISDN=? and POINTS_DATE=(SELECT MAX(POINTS_DATE) FROM BONUS WHERE USER_ID_OR_MSISDN=?  AND PRODUCT_CODE= ? ) ");
			updateBuff.append(" AND PRODUCT_CODE= ? ");
            final String updateQuery = updateBuff.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "updateQuery " + updateQuery);
            }
            //con = OracleUtil.getConnection();
            pstmt1 = con.prepareStatement(updateQuery);

            index = 1;
            pstmt1.setLong(index++, currAccuPoint);
            pstmt1.setString(index++, p_loyaltyPointsRedemptionVO.getRedemptionID());
            pstmt1.setTimestamp(index++, BTSLUtil.getTimestampFromUtilDate(p_loyaltyPointsRedemptionVO.getModifiedOn()));
            pstmt1.setLong(index++, -(Long.parseLong(String.valueOf(p_loyaltyPointsRedemptionVO.getRedempLoyaltyPoint()))));
            pstmt1.setString(index++, p_loyaltyPointsRedemptionVO.getRedemptionID());
            pstmt1.setString(index++, p_loyaltyPointsRedemptionVO.getUserID());
            pstmt1.setString(index++, p_loyaltyPointsRedemptionVO.getUserID());
			pstmt1.setString(index++, p_loyaltyPointsRedemptionVO.getProductCode());
			pstmt1.setString(index++, p_loyaltyPointsRedemptionVO.getProductCode());
            count = pstmt.executeUpdate();
            if (count > 0) {
                mcomCon.finalCommit();
            } else {
                mcomCon.finalRollback();
            }
        } catch (SQLException sqle) {
            try {
              mcomCon.finalRollback();
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            _logger.error(methodName, "SQLException " + sqle.getMessage());
            _logger.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "LoyaltyPointsRedemptionDAO[updateUserLoyaltyPointsDetail]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                mcomCon.finalRollback();
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            _logger.error(methodName, "Exception " + ex.getMessage());
            _logger.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "LoyaltyPointsRedemptionDAO[updateUserLoyaltyPointsDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(methodName, "error.general.processing");
        } finally {
			if (mcomCon != null) {
				mcomCon.close("LmsPointsRedemptionProcess#updateUserLoyaltyPointsDetail");
				mcomCon = null;
			}
			try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            try {
                if (pstmt1 != null) {
                    pstmt1.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exiting count= " + count);
            }
        }
        return count;
    }

    public static LoyaltyPointsRedemptionVO loaduserProfileDetails(Connection p_con, LoyaltyPointsRedemptionVO loyaltyPointsRedemptionVO) throws BTSLBaseException {

        final String methodName = "loaduserProfileDetails";
        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName, "Entered ");
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            LmsPointsRedemptionProcessQry lmsPointsRedemptionProcessQry = (LmsPointsRedemptionProcessQry)ObjectProducer.getObject(QueryConstants.LMS_POINTS_REDMPTION_PROCESS_QRY, QueryConstants.QUERY_PRODUCER);

            
            final String selectQuery = lmsPointsRedemptionProcessQry.loaduserProfileDetailsQry();
           

            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, loyaltyPointsRedemptionVO.getUserID());
            rs = pstmtSelect.executeQuery();

            if (rs.next()) {

                loyaltyPointsRedemptionVO.setParentContribution(rs.getInt("PRT_CONTRIBUTION"));
                loyaltyPointsRedemptionVO.setOperatorContribution(rs.getInt("OPT_CONTRIBUTION"));
                loyaltyPointsRedemptionVO.setParentID(rs.getString("PARENT_ID"));
                loyaltyPointsRedemptionVO.setSetId(rs.getString("set_id"));
                loyaltyPointsRedemptionVO.setVersion(rs.getString("version"));

			} else {
				//Independent of LMS Profile for redemption
				loyaltyPointsRedemptionVO.setParentContribution(0);
				loyaltyPointsRedemptionVO.setOperatorContribution(100);
				loyaltyPointsRedemptionVO.setSetId("1");
				loyaltyPointsRedemptionVO.setVersion("1");
            }
            return loyaltyPointsRedemptionVO;
        } catch (SQLException sqle) {
            _logger.error(methodName, "SQLException " + sqle.getMessage());
            _logger.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("loaduserProfileRelatedDetails", "error.general.sql.processing");
        } catch (Exception e) {
            _logger.error(methodName, "Exception " + e.getMessage());
            _logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("loaduserProfileRelatedDetails", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exiting loyaltyPointsRedemptionVO: " + loyaltyPointsRedemptionVO);
            }
        }
    }

    /*
     * public static String
     * initaiateC2CTransferRequest(LoyaltyPointsRedemptionVO p_redemptionVO)
     * throws BTSLBaseException { if (_logger.isDebugEnabled())
     * _logger.debug("initaiateC2CTransferRequest", "Entered"); String
     * o2cFocTxnId = null; HttpURLConnection con=null; BufferedReader in=null;
     * InstanceLoadVO instanceLoadVO=null; String urlToSend=null; String
     * httpURLPrefix="http://"; String msgGWPass=null; String requestXML =null;
     * String responseStr=null; String finalResponse=""; String response =null;
     * try { //msisdn= p_redemptionVO.getMsisdn(); //msisdnPrefix =
     * PretupsBL.getMSISDNPrefix(msisdn); requestXML =
     * generateRequestC2CTRFXML(p_redemptionVO); MessageGatewayVO
     * messageGatewayVO=MessageGatewayCache.getObject(PretupsI.
     * REQUEST_SOURCE_TYPE_EXTGW);
     * if (_logger.isDebugEnabled())
     * _logger.debug("initaiateC2CTransferRequest", "messageGatewayVO:
     * "+messageGatewayVO);
     * 
     * if(messageGatewayVO==null) throw new
     * BTSLBaseException("RunLMSForTargetCredit","initaiateC2CTransferRequest",
     * PretupsErrorCodesI.ERROR_NOTFOUND_MESSAGEGATEWAY);
     * RequestGatewayVO requestGatewayVO=messageGatewayVO.getRequestGatewayVO();
     * if(requestGatewayVO==null) throw new
     * BTSLBaseException("RunLMSForTargetCredit","initaiateC2CTransferRequest",
     * PretupsErrorCodesI.ERROR_NOTFOUND_REQMESSAGEGATEWAY);
     * 
     * if(!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus())) throw
     * new BTSLBaseException( "initaiateC2CTransferRequest",
     * "c2stranfer.c2srecharge.error.messagegatewaynotactive","c2sRecharge");
     * else
     * if(!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getRequestGatewayVO().
     * getStatus()))
     * throw new BTSLBaseException( "initaiateC2CTransferRequest",
     * "c2stranfer.c2srecharge.error.reqmessagegatewaynotactive","c2sRecharge");
     * 
     * //If Encrypted Password check box is checked. i.e. send password in
     * request as encrypted.
     * if(messageGatewayVO.getReqpasswordtype().equalsIgnoreCase(PretupsI.
     * SELECT_CHECKBOX))
     * msgGWPass
     * =BTSLUtil.decryptText(messageGatewayVO.getRequestGatewayVO().getPassword()
     * );
     * else msgGWPass=messageGatewayVO.getRequestGatewayVO().getPassword();
     * 
     * //NetworkPrefixVO
     * networkPrefixVO=(NetworkPrefixVO)NetworkPrefixCache.getObject(msisdnPrefix
     * );
     * /*if(networkPrefixVO==null) { strArr=new String[]{msisdn}; throw new
     * BTSLBaseException("RunLMSForTargetCredit","initaiateC2CTransferRequest",
     * PretupsErrorCodesI
     * .CHNL_ERROR_RECR_NOTFOUND_RECEIVERNETWORK,0,strArr,null); }
     * String networkCode=networkPrefixVO.getNetworkCode();
     * 
     * String networkCode=p_redemptionVO.getNetworkID(); String instanceID=null;
     * /*if(!networkCode.equalsIgnoreCase(p_redemptionVO.getNetworkID())) {
     * strArr=new String[]{msisdn}; throw new
     * BTSLBaseException("RunLMSForTargetCredit","initaiateC2CTransferRequest",
     * PretupsErrorCodesI
     * .CHNL_ERROR_RECR_NOTFOUND_RECEIVERNETWORK,0,strArr,null); }
     * 
     * 
     * //Changed to handle multiple SMS servers for C2S and P2P on 20/07/06
     * //if(LoadControllerCache.getNetworkLoadHash()!=null &&
     * LoadControllerCache.getNetworkLoadHash().containsKey(LoadControllerCache.
     * getInstanceID()+"_"+networkCode)) //
     * smsInstanceID=((NetworkLoadVO)(LoadControllerCache.getNetworkLoadHash().get
     * (
     * LoadControllerCache.getInstanceID()+"_"+networkCode))).getC2sInstanceID()
     * ;
     * instanceID=Constants.getProperty("INSTANCE_ID");
     * if(BTSLUtil.isNullString(instanceID)) {
     * _logger.error("RunLMSForTargetCredit"," Not able to get the instance ID
     * for the network="+networkCode+" where the request for o2c needs to be
     * send"); throw new
     * BTSLBaseException("RunLMSForTargetCredit","initaiateC2CTransferRequest",
     * "lms.point.redemption.form.error.unableto.initiateo2c",""); }
     * instanceLoadVO=LoadControllerCache.getInstanceLoadForNetworkHash(instanceID
     * +"_"+networkCode+"_"+PretupsI.REQUEST_SOURCE_TYPE_SMS);
     * 
     * StringBuffer sbf1 = new StringBuffer();
     * 
     * sbf1.append("REQUEST_GATEWAY_CODE="+messageGatewayVO.getGatewayCode());
     * sbf1.append("&REQUEST_GATEWAY_TYPE="+messageGatewayVO.getGatewayType());
     * sbf1.append("&LOGIN="+messageGatewayVO.getRequestGatewayVO().getLoginID())
     * ;
     * sbf1.append("&PASSWORD="+msgGWPass );
     * sbf1.append("&SERVICE_PORT="+messageGatewayVO.getRequestGatewayVO().
     * getServicePort());
     * sbf1.append("&SOURCE_TYPE="+PretupsI.REQUEST_SOURCE_TYPE_EXTGW); String
     * authorization = sbf1.toString(); if(instanceLoadVO==null) {
     * _logger.error("RunLMSForTargetCredit"," Not able to get the instance
     * detaile for the network="+networkCode+" where the request for o2c needs
     * to be send"); throw new
     * BTSLBaseException("RunLMSForTargetCredit","initaiateC2CTransferRequest",
     * PretupsErrorCodesI.INSTANCE_CODE_NOT_FOUND); }
     * else {
     * urlToSend=httpURLPrefix+instanceLoadVO.getHostAddress()+":"+instanceLoadVO
     * .getHostPort()+Constants.getProperty("CHANNEL_WEB_RECHARGE_SERVLET")+"?";
     * }
     * 
     * try { String encodeUrl = URLEncoder.encode(urlToSend); URL url = new
     * URL(urlToSend); URLConnection uc = url.openConnection(); con =
     * (HttpURLConnection) uc; con.addRequestProperty("Content-Type",
     * "text/xml"); con.addRequestProperty("Authorization", authorization);
     * con.setUseCaches(false); con.setDoInput(true); con.setDoOutput(true);
     * con.setRequestMethod("POST"); BufferedWriter wr = new BufferedWriter(new
     * OutputStreamWriter(con.getOutputStream(), "UTF8")); // Send data
     * wr.write(requestXML); wr.flush(); // Get response in= new
     * BufferedReader(new InputStreamReader(con.getInputStream())); while
     * ((responseStr = in.readLine()) !=null)
     * {finalResponse=finalResponse+responseStr ;} wr.close(); in.close(); }
     * catch (Exception e) {_log.errorTrace(methodName,e);} finally { if(con !=
     * null){con.disconnect();} }
     * 
     * if(!BTSLUtil.isNullString(finalResponse)) { int
     * index=finalResponse.indexOf("<TXNID>");
     * o2cFocTxnId=finalResponse.substring(index+"<TXNID>".length(),finalResponse
     * .indexOf("</TXNID>",index));
     * index=finalResponse.indexOf("<TXNSTATUS>"); String
     * focO2cTxnStatus=finalResponse.substring(index+"<TXNSTATUS>".length(),
     * finalResponse.indexOf("</TXNSTATUS>",index));
     * 
     * response=o2cFocTxnId +"@"+focO2cTxnStatus; if (_logger.isDebugEnabled())
     * _logger.debug("RunLMSForTargetCredit", "initaiateC2CTransferRequest:
     * C2CTxnId="+o2cFocTxnId+" C2CTxnStatus= "+focO2cTxnStatus); } else {
     * o2cFocTxnId=null; response=null; } } catch(BTSLBaseException bse) {
     * bs_log.errorTrace(methodName,e);
     * p_redemptionVO.setErrorCode(PretupsErrorCodesI.INSTANCE_CODE_NOT_FOUND);
     * throw new
     * BTSLBaseException("RunLMSForTargetCredit","initaiateC2CTransferRequest",
     * PretupsErrorCodesI.INSTANCE_CODE_NOT_FOUND); }
     * catch(Exception e) { _log.errorTrace(methodName,e); } finally { if
     * (_logger.isDebugEnabled()) _logger.debug("initaiateFocRedemptionRequest"
     * ,
     * "Exiting response=" +response); } return response; }
     * 
     * public static String generateRequestC2CTRFXML(LoyaltyPointsRedemptionVO
     * p_redempVO) { if (_logger.isDebugEnabled())
     * _logger.debug("generateRequestC2CTRFXML", "Entered"); String
     * requesStr=null; StringBuffer sbf=null; try { Random exttxnno = new
     * Random();
     * 
     * 
     * sbf=new StringBuffer(); sbf.append("<?xml version=\"1.0\"?><!DOCTYPE
     * COMMAND PUBLIC
     * \"-//Ocam//DTD XML Command1.0//EN\" \"xml/command.dtd\"><COMMAND>");
     * sbf.append("<TYPE>EXC2CTRFREQ</TYPE>");
     * sbf.append("<DATE>"+BTSLUtil.getDateStringFromDate(new
     * Date())+"</DATE>");
     * sbf.append("<EXTNWCODE>"+p_redempVO.getNetworkID()+"</EXTNWCODE>");
     * sbf.append("<MSISDN1>"+p_redempVO.getParentMsisdn()+"</MSISDN1>");
     * sbf.append("<PIN>"+ new
     * CryptoUtil().decrypt(p_redempVO.getParentEncryptedPin(),Constants.KEY)+
     * "</PIN>");
     * sbf.append("<LOGINID></LOGINID>"); sbf.append("<PASSWORD></PASSWORD>");
     * if(!BTSLUtil.isNullString(p_redempVO.getExternalCode()))
     * sbf.append("<EXTCODE>"+p_redempVO.getExternalCode()+"</EXTCODE>");
     * else sbf.append("<EXTCODE></EXTCODE>"); sbf.append("<EXTREFNUM>"+
     * exttxnno.nextInt(10000)+"</EXTREFNUM>");
     * sbf.append("<MSISDN2>"+p_redempVO.getMsisdn()+"</MSISDN2>");
     * sbf.append("<LOGINID2></LOGINID2>"); sbf.append("<EXTCODE2></EXTCODE2>");
     * sbf.append("<PRODUCTS>");
     * sbf.append("<PRODUCTCODE>"+p_redempVO.getProductShortCode
     * ()+"</PRODUCTCODE>");
     * sbf.append("<QTY>"+p_redempVO.getC2cContribution()+"</QTY>");
     * sbf.append("</PRODUCTS>"); sbf.append("<LANGUAGE1></LANGUAGE1>");
     * sbf.append("</COMMAND>"); requesStr = sbf.toString(); }
     * 
     * catch(Exception ex) { ex.printStackTrace(); } finally { if
     * (_logger.isDebugEnabled()) _logger.debug("generateRequestC2CTRFXML" ,
     * "Exiting requesStr=" +requesStr); } return requesStr; }
     */
    public static int insertRedemtionDetails(Connection p_con, LoyaltyPointsRedemptionVO loyaltyPointsRedemptionVO) throws BTSLBaseException {
        final String methodName = "insertRedemtionDetails";
        if (_logger.isDebugEnabled()) {
            _logger.debug("loaduserProfileDetails", "Entered ");
        }
        PreparedStatement _psmtInsertRedemption = null;
        final ResultSet rs = null;
        int count = 0;
        int index = 1;
        try {
            final StringBuffer selectQueryBuff = new StringBuffer("INSERT INTO REDEMPTIONS(REFERENCE_ID,PROFILE_TYPE, REDEMPTION_TYPE, ");
            selectQueryBuff.append(" REDEMPTION_ID, REDEMPTION_DATE, PRODUCT_CODE,  POINTS_REDEEMED,");
            selectQueryBuff.append(" AMOUNT_TRANSFERED, CREATED_ON, CREATED_BY, MODIFIED_ON,MODIFIED_BY,USER_ID_OR_MSISDN)");
            selectQueryBuff.append(" values (?,?,?,?,?,?,?,?,?,?,?,?,?)");
            final String selectQuery = selectQueryBuff.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadLMSUserDetails", "select query:" + selectQuery);
            }

            _psmtInsertRedemption = p_con.prepareStatement(selectQuery);
            _psmtInsertRedemption.setString(index++, loyaltyPointsRedemptionVO.getReferenceNo());
            _psmtInsertRedemption.setString(index++, PretupsI.LMS_PROFILE_TYPE);
            _psmtInsertRedemption.setString(index++, PretupsI.REDEMP_TYPE_STOCK);
            _psmtInsertRedemption.setString(index++, loyaltyPointsRedemptionVO.getRedemptionID());
            _psmtInsertRedemption.setDate(index++, BTSLUtil.getSQLDateFromUtilDate(loyaltyPointsRedemptionVO.getRedemptionDate()));
            _psmtInsertRedemption.setString(index++, loyaltyPointsRedemptionVO.getProductShortCode());
            // Commented By Diwakar on 10-MAR-2015
            // _psmtInsertRedemption.setDouble(index++,Long.valueOf(loyaltyPointsRedemptionVO.getCurrentLoyaltyPoints()));
            _psmtInsertRedemption.setDouble(index++, Long.valueOf(loyaltyPointsRedemptionVO.getRedempLoyaltyPoint()));
            // Ended Here
            _psmtInsertRedemption.setDouble(index++, loyaltyPointsRedemptionVO.getSumAmount());
            _psmtInsertRedemption.setTimestamp(index++, BTSLUtil.getTimestampFromUtilDate(loyaltyPointsRedemptionVO.getRedemptionDate()));
            _psmtInsertRedemption.setString(index++, "SYSTEM");
            _psmtInsertRedemption.setTimestamp(index++, BTSLUtil.getTimestampFromUtilDate(loyaltyPointsRedemptionVO.getRedemptionDate()));
            _psmtInsertRedemption.setString(index++, "SYSTEM");
            _psmtInsertRedemption.setString(index++, loyaltyPointsRedemptionVO.getUserID());

            count = _psmtInsertRedemption.executeUpdate();

            return count;
        } catch (SQLException sqle) {
            _logger.error("loaduserProfileDetails", "SQLException " + sqle.getMessage());
            _logger.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("loaduserProfileRelatedDetails", "error.general.sql.processing");
        } catch (Exception e) {
            _logger.error("loaduserProfileDetails", "Exception " + e.getMessage());
            _logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("loaduserProfileRelatedDetails", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            try {
                if (_psmtInsertRedemption != null) {
                    _psmtInsertRedemption.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("loaduserProfileDetails", "Exiting loyaltyPointsRedemptionVO: " + loyaltyPointsRedemptionVO);
            }
        }
    }

    public static void performC2C(Connection con, ChannelUserVO parentUserVO, LoyaltyPointsRedemptionVO redempVO, C2sBalanceQueryVO parentBalancesVO) {
        final String methodName = "performC2C";
        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName, "Entered");
        }

        try {
            final ChannelTransferVO channelTransferVo = new ChannelTransferVO();
            final Date currentDate = new Date();
            // long
            // requestedvalue=PretupsBL.getSystemAmount(redempVO.getC2cContribution());
            final long requestedvalue = redempVO.getC2cContribution();
            ChannelTransferItemsVO channelTransferItemsVO = null;
            ChannelTransferItemsVO transferItemsVO = null;
            ChannelUserVO channelUserVO = new ChannelUserVO();
            ArrayList channelTransferItemVOList = new ArrayList();
            final ChannelUserTransferDAO channelUserTransferDAO = new ChannelUserTransferDAO();
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			String product = redempVO.getProductCode();
            channelTransferVo.setTransactionMode(PretupsI.AUTO_C2C_TXN_MODE);
            channelTransferVo.setNetworkCode(redempVO.getNetworkID());
            channelTransferVo.setNetworkCodeFor(redempVO.getNetworkID());
            channelTransferVo.setCreatedOn(currentDate);
            // genrateChnnlToChnnlTrfID(con, channelTransferVo);
            ChannelTransferBL.genrateChnnlToChnnlTrfID(channelTransferVo);
            channelTransferVo.getTransferID();
            channelTransferVo.setActiveUserId(parentUserVO.getUserID());
            channelTransferVo.setProductCode(parentBalancesVO.getProductCode());
            channelTransferVo.setFromUserID(parentUserVO.getUserID());
            channelTransferVo.setToUserID(redempVO.getUserID());
            channelTransferVo.setSenderTxnProfile(parentUserVO.getTransferProfileID());
            channelTransferVo.setFromUserCode(parentUserVO.getUserCode());
            channelUserVO = channelUserDAO.loadChannelUserDetails(con, redempVO.getMsisdn());
            channelTransferItemVOList = channelUserTransferDAO.parentBalanceUpdateValue(con, channelTransferVo.getFromUserID(), requestedvalue);
            if ((channelTransferItemVOList != null) && (channelTransferItemVOList.size() > 0)) {
                for (int m = 0, n = channelTransferItemVOList.size(); m < n; m++) {
                    transferItemsVO = (ChannelTransferItemsVO) channelTransferItemVOList.get(m);
                    if (transferItemsVO.getProductCode().equalsIgnoreCase(product)) {
                        transferItemsVO = null;
                        transferItemsVO = (ChannelTransferItemsVO) channelTransferItemVOList.get(m);
                        transferItemsVO.setReceiverCreditQty(requestedvalue);
                    }
                }
            }
            channelTransferVo.setChannelTransferitemsVOList(channelTransferItemVOList);
            channelTransferVo.setTransferMRP(requestedvalue);
            channelTransferVo.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
            channelTransferVo.setCategoryCode(parentUserVO.getCategoryCode());
            channelTransferVo.setReceiverCategoryCode(channelUserVO.getCategoryCode());
            if (transferItemsVO != null) {
                channelTransferVo.setRequestedQuantity(Long.valueOf(transferItemsVO.getRequestedQuantity()));
            }
            int creditCount = 0;
            int debitCount = 0;
            int updateCount = 0;
            int upcount = 0;
            final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
            final UserBalancesVO userBalancesVO = constructBalanceVOFromTxnVO(channelTransferVo);
            userBalancesVO.setUserID(channelTransferVo.getFromUserID());
            upcount = userBalancesDAO.updateUserDailyBalances(con, currentDate, userBalancesVO);
            userBalancesVO.setUserID(channelTransferVo.getToUserID());
            upcount = userBalancesDAO.updateUserDailyBalances(con, currentDate, userBalancesVO);

            debitCount = channelUserDAO.debitUserBalances(con, channelTransferVo, false, null);

            channelTransferVo.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
            creditCount = channelUserDAO.creditUserBalances(con, channelTransferVo, false, null);

            channelTransferVo.setGraphicalDomainCode(parentUserVO.getGeographicalCode());
            channelTransferVo.setDomainCode(parentUserVO.getDomainID());
            channelTransferVo.setSenderGradeCode(parentUserVO.getUserGrade());
            channelTransferVo.setReceiverGradeCode(channelUserVO.getUserGrade());
            channelTransferVo.setReferenceNum(parentUserVO.getReferenceID());
            channelTransferVo.setCommProfileSetId(redempVO.getSetId());
            // channelTransferVo.setCommProfileVersion(channelUserVO.getCommissionProfileSetVersion());
            channelTransferVo.setCommProfileVersion(redempVO.getVersion());
            channelTransferVo.setDualCommissionType(channelUserVO.getDualCommissionType());
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

            channelTransferVo.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_LMS);
            channelTransferVo.setControlTransfer(PretupsI.YES);
            channelTransferVo.setToUserCode(redempVO.getMsisdn());
            channelTransferVo.setReceiverDomainCode(channelUserVO.getDomainID());
            channelTransferVo.setReceiverGgraphicalDomainCode(channelUserVO.getGeographicalCode());
            channelTransferVo.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
            channelTransferVo.setTransferMRP(requestedvalue);
            channelTransferVo.setRequestedQuantity(requestedvalue);

            // for enquiry
            channelTransferItemsVO = (ChannelTransferItemsVO) channelTransferVo.getChannelTransferitemsVOList().get(0); // 23
            // dec
            if (_logger.isDebugEnabled()) {
                _logger.info("process", "Payable amount=" + channelTransferItemsVO.getPayableAmount());
                _logger.info("process", "Net Payable amount=" + channelTransferItemsVO.getPayableAmount());
            }
            channelTransferVo.setPayableAmount(channelTransferItemsVO.getPayableAmount());
            channelTransferVo.setNetPayableAmount(channelTransferItemsVO.getNetPayableAmount());
            try {
                updateCount = channelTransferDAO.addChannelTransfer(con, channelTransferVo);
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
                con.rollback();

            }
            if (creditCount > 0 && debitCount > 0 && updateCount > 0 && upcount > 0) {
                con.commit();
                redempVO.setTxnStatus("200");
                redempVO.setReferenceNo(channelTransferVo.getTransferID());

            } else {
                redempVO.setTxnStatus("206");

            }

        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback();
                }

                _logger.error(methodName, "Exception " + e.getMessage());
                _logger.errorTrace(methodName, e);
            } catch (Exception e1) {
                _logger.errorTrace(methodName, e1);
            }
        } catch (BTSLBaseException e) {
            try {
                if (con != null) {
                    con.rollback();
                }

                _logger.error(methodName, "Exception " + e.getMessage());
                _logger.errorTrace(methodName, e);
            } catch (Exception e1) {
                _logger.errorTrace(methodName, e1);
            }
        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                }

                _logger.error(methodName, "Exception " + e.getMessage());
                _logger.errorTrace(methodName, e);
            } catch (Exception e1) {
                _logger.errorTrace(methodName, e1);
            }
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exited  ID =" + redempVO.getReferenceNo());
            }

        }

    }

    /*
     * private static void genrateOprtToChnnlTrfID(Connection p_con,
     * ChannelTransferVO p_channelTransferVO)
     * throws BTSLBaseException {
     * 
     * final String methodName = "genrateOprtToChnnlTrfID";
     * if (_logger.isDebugEnabled()) {
     * _logger.debug(methodName, "Entered ChannelTransferVO =" +
     * p_channelTransferVO);
     * }
     * 
     * try {
     * 
     * long tmpId = getNextID(p_con, PretupsI.CHANNEL_TRANSFER_O2C_ID,
     * BTSLUtil.getFinancialYear(),
     * p_channelTransferVO);
     * p_channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(
     * p_channelTransferVO,
     * PretupsI.CHANNEL_TRANSFER_O2C_ID, tmpId));
     * 
     * } catch (Exception e) {
     * _logger.error(methodName, "Exception " + e.getMessage());
     * _logger.errorTrace(methodName, e);
     * 
     * throw new BTSLBaseException("ChannelTransferBL", methodName,
     * PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
     * } finally {
     * if (_logger.isDebugEnabled()) {
     * _logger.debug(methodName, "Exited  ID =" +
     * p_channelTransferVO.getTransferID());
     * }
     * }
     * 
     * }
     */

    public static void perfomFOC(Connection con, ChannelUserVO parentUserVO, LoyaltyPointsRedemptionVO redempVO, NetworkStockVO stockVO) {
        final String methodName = "perfomFOC";
        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Entered ChannelTransferVO ");
            }
            int creditCount = 0;
            // int debitCount =0;
            // int deleteCount=0;
            int updateCount = 0;
            int upCount = 0;
			String product = redempVO.getProductCode();
            // long
            // transferAmount=PretupsBL.getSystemAmount(redempVO.getO2cContribution());
            final long transferAmount = redempVO.getO2cContribution();

            // String productType=PREPROD
            final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
            // channelUserVO=(ChannelUserVO)channelUserDAO.loadChannelUserDetails(con,redempVO.getUserCode());
            final Date currentDate = new Date();
            ChannelTransferItemsVO transferItemsVO = null;
            ChannelUserVO channelUserVO = new ChannelUserVO();
            ArrayList channelTransferItemVOList = new ArrayList();
            final ChannelUserTransferDAO channelUserTransferDAO = new ChannelUserTransferDAO();
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();

            channelUserVO = channelUserDAO.loadChannelUserDetails(con, redempVO.getMsisdn());
            channelTransferVO.setTransactionMode(PretupsI.AUTO_FOC_TXN_MODE);
            channelTransferVO.setNetworkCode(channelUserVO.getNetworkID());
            channelTransferVO.setNetworkCodeFor(channelUserVO.getNetworkID());
            channelTransferVO.setCreatedOn(currentDate);
            // genrateOprtToChnnlTrfID(con, channelTransferVO);
            ChannelTransferBL.genrateTransferID(channelTransferVO);
            channelTransferVO.getTransferID();
            channelTransferVO.setTransferDate(currentDate);
            channelTransferVO.getTransferID();
            channelTransferVO.setActiveUserId(channelUserVO.getUserID());
            channelTransferVO.setToUserID(redempVO.getUserID());

            channelTransferItemVOList = channelUserTransferDAO.getTransferlistForAutoFOC(con, redempVO.getUserID(), transferAmount,redempVO.getProductCode());
            if ((channelTransferItemVOList != null) && (channelTransferItemVOList.size() > 0)) {
                for (int m = 0, n = channelTransferItemVOList.size(); m < n; m++) {
                    transferItemsVO = (ChannelTransferItemsVO) channelTransferItemVOList.get(m);
                    if (transferItemsVO.getProductCode().equalsIgnoreCase(product)) {
                        transferItemsVO = null;
                        transferItemsVO = (ChannelTransferItemsVO) channelTransferItemVOList.get(m);
                    }
                }
            }
            channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
            channelTransferVO.setTransferMRP(transferAmount);
            channelTransferVO.setReceiverCategoryCode(channelUserVO.getCategoryCode());
            channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            channelTransferVO.setType(PretupsI.TRANSFER_TYPE_FOC);
            channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
            channelTransferVO.setModifiedOn(currentDate);
            channelTransferVO.setRequestedQuantity(transferAmount);

            channelTransferVO.setWalletType(PretupsI.TRANSFER_TYPE_FOC);
            // added by harsh set Missing Values in O2C Transfer request
            // triggered during LMS Redemption Process
            channelTransferVO.setToUserCode(redempVO.getMsisdn());
            channelTransferVO.setReferenceNum(redempVO.getRedemptionID());
            channelTransferVO.setFirstApprovedBy(PretupsI.SYSTEM_USER);
            channelTransferVO.setFirstApprovedOn(currentDate);
            channelTransferVO.setSecondApprovedBy(PretupsI.SYSTEM_USER);
            channelTransferVO.setSecondApprovedOn(currentDate);
            channelTransferVO.setThirdApprovedBy(PretupsI.SYSTEM_USER);
            channelTransferVO.setThirdApprovedOn(currentDate);
            channelTransferVO.setReceiverDomainCode(channelUserVO.getDomainID());
            channelTransferVO.setTransferInitatedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
            channelTransferVO.setReceiverGradeCode(channelUserVO.getUserGrade());
            channelTransferVO.setProductType(PretupsI.PRODUCT_TYPE_AUTO_O2C);
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
                channelTransferVO.setWalletType(PretupsI.FOC_WALLET_TYPE);
            } else {
                channelTransferVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
            }

            final boolean debit = true;
            int updateCount1 = -1;
            updateCount = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(con, channelTransferVO, redempVO.getUserID(), currentDate, debit);
            if (updateCount < 1) {
                throw new BTSLBaseException("O2CDirectTransferController", "transactionApproval", PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

            updateCount1 = ChannelTransferBL.updateNetworkStockTransactionDetails(con, channelTransferVO, redempVO.getUserID(), currentDate);
            if (updateCount1 < 1) {
                throw new BTSLBaseException("O2CDirectTransferController", "transactionApproval", PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

            channelTransferVO.setToUserID(redempVO.getUserID());
            channelTransferVO.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                channelTransferVO.setUserWalletCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN)));
                creditCount = channelUserDAO.creditUserBalancesForMultipleWallet(con, channelTransferVO, false, null);
            } else {
                creditCount = channelUserDAO.creditUserBalances(con, channelTransferVO, false, null);
            }
            // channelTransferVO.setNetworkCode(Network_code);
            // p_channelTransferVO.setTransferProfileID(receiverUserVO.getTransferProfileID());
            channelTransferVO.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
            channelTransferVO.setReceiverTxnProfileName(channelUserVO.getTransferProfileName());
            channelTransferVO.setTotalTax1(0);
            channelTransferVO.setTotalTax2(0);

            channelTransferVO.setTotalTax3(0);
            channelTransferVO.setRequestedQuantity(transferAmount);
            channelTransferVO.setPayableAmount(0);
            channelTransferVO.setNetPayableAmount(0);
            channelTransferVO.setPayInstrumentAmt(transferAmount);
            channelTransferVO.setTransferMRP(transferAmount);
            channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);

            channelTransferVO.setToUserName(channelUserVO.getUserName());

            channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
            channelTransferVO.setDomainCode(channelUserVO.getCategoryCode());
            channelTransferVO.setReceiverCategoryCode(channelUserVO.getCategoryCode());
            channelTransferVO.setCommProfileSetId(redempVO.getSetId());
            channelTransferVO.setNetworkCodeFor(channelUserVO.getNetworkID());
            channelTransferVO.setCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
            channelTransferVO.setTransferDate(currentDate);
            channelTransferVO.setCommProfileVersion(redempVO.getVersion());
            channelTransferVO.setDualCommissionType(channelUserVO.getDualCommissionType());
            channelTransferVO.setCreatedOn(currentDate);
            channelTransferVO.setModifiedOn(currentDate);
            channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
            channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_SYSTEM);

            // channelTransferVO.setProductType();
            channelTransferVO.setProductCode(redempVO.getProductShortCode());
            channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
            channelTransferVO.setType(PretupsI.TRANSFER_TYPE_O2C);
            channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_LMS);
            channelTransferVO.setControlTransfer(PretupsI.YES);
            channelTransferVO.setCommQty(0);
            channelTransferVO.setSenderDrQty(0);
            channelTransferVO.setReceiverCrQty(0);
            // ChannelTransferDAO channelTransferDAO = new
            // ChannelTransferDAO();
            // ChannelTransferBL.genrateTransferID(p_channelTransferVO);
            // channelTransferVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
            channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            // p_channelTransferVO.setChannelTransferitemsVOList(itemsList);
            channelTransferVO.setCreatedBy(PretupsI.SYSTEM);
            channelTransferVO.setModifiedBy(PretupsI.SYSTEM);
            channelTransferVO.setCreatedOn(currentDate);
            channelTransferVO.setModifiedOn(currentDate);
            final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();

            final UserBalancesVO userBalancesVO = constructBalanceVOFromTxnVO(channelTransferVO);
            userBalancesVO.setUserID(channelTransferVO.getToUserID());
            upCount = userBalancesDAO.updateUserDailyBalances(con, currentDate, userBalancesVO);
            final int count = channelTransferDAO.addChannelTransfer(con, channelTransferVO);
            if (upCount > 0 && count > 0 && updateCount1 > 0 && creditCount > 0) {
                con.commit();
                redempVO.setReferenceNo(channelTransferVO.getTransferID());
                redempVO.setTxnStatus("200");
            } else {
                con.rollback();
                redempVO.setTxnStatus("206");
            }
        } catch (Exception e) {
            _logger.errorTrace(methodName, e);
            try {
                con.rollback();
            } catch (Exception e1) {
                _logger.errorTrace(methodName, e1);
            }
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("performFOC", "Exited  ID =" + redempVO.getReferenceNo());
            }

        }
    }

    public static UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("constructBalanceVOFromTxnVO", "Entered:NetworkStockTxnVO=>" + p_channelTransferVO);
        }
        final UserBalancesVO userBalancesVO = new UserBalancesVO();
        userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
        userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
        userBalancesVO.setLastTransferOn(p_channelTransferVO.getTransferDate());
        if (_logger.isDebugEnabled()) {
            _logger.debug("constructBalanceVOFromTxnVO", "Exiting userBalancesVO=" + userBalancesVO);
        }
        return userBalancesVO;
    }

    /*
     * public static void genrateChnnlToChnnlTrfID(Connection p_con,
     * ChannelTransferVO p_channelTransferVO)
     * throws BTSLBaseException {
     * 
     * final String methodName = "genrateChnnlToChnnlTrfID";
     * if (_logger.isDebugEnabled()) {
     * _logger.debug(methodName, "Entered ChannelTransferVO =" +
     * p_channelTransferVO);
     * }
     * 
     * try {
     * // p_channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(
     * p_channelTransferVO
     * ,PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID,IDGenerator.getNextID
     * (PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID,
     * // BTSLUtil.getFinancialYear() ,
     * //
     * p_channelTransferVO.getNetworkCode(),p_channelTransferVO.getCreatedOn()
     * )));
     * // long
     * //
     * tmpId=IDGenerator.getNextID(p_con,PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID
     * ,
     * // BTSLUtil.getFinancialYear() , p_channelTransferVO);
     * long tmpId = getNextID(p_con, PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID,
     * BTSLUtil.getFinancialYear(),
     * p_channelTransferVO);
     * p_channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(
     * p_channelTransferVO,
     * PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID, tmpId));
     * 
     * } catch (Exception e) {
     * _logger.error(methodName, "Exception " + e.getMessage());
     * _logger.errorTrace(methodName, e);
     * //
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"ChannelTransferBL[genrateChnnlToChnnlTrfID]",""
     * ,"","","Exception:"+e.getMessage());
     * throw new BTSLBaseException("ChannelTransferBL", methodName,
     * PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
     * } finally {
     * if (_logger.isDebugEnabled()) {
     * _logger.debug(methodName, "Exited  ID =" +
     * p_channelTransferVO.getTransferID());
     * }
     * }
     * 
     * }
     * 
     * public static long getNextID(Connection p_con, String p_idType, String
     * p_year, ChannelTransferVO p_channelTransferVO)
     * throws BTSLBaseException {
     * final String methodName = "getNextID";
     * try {
     * IDGeneratorDAO _idGeneratorDAO = new IDGeneratorDAO();
     * long id = _idGeneratorDAO.getNextID(p_con, p_idType, p_year,
     * p_channelTransferVO);
     * return id;
     * } finally {
     * if (p_con != null) {
     * try {
     * p_con.commit();
     * } catch (Exception e) {
     * _logger.errorTrace(methodName,e);
     * }
     * }
     * }
     * }
     */

}
