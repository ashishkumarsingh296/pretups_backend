package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.IDGeneratorDAO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserDeletionBL;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockDAO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.txn.pretups.channel.profile.businesslogic.CommissionProfileTxnDAO;

public class BarForDeletionProcess {
    private static final Log logger = LogFactory.getLog(BarForDeletionProcess.class.getName());
    private static Date _currentDate = new Date();
    private static PreparedStatement _fetchDataStmt = null;
    private static PreparedStatement _loadBalanceStmt = null;
    private static PreparedStatement _updateDataStmt = null;
    private static HashMap<String, NetworkProductVO> _networkProductMap = null;
    private static IDGeneratorDAO _idGeneratorDAO = null;
    public static OperatorUtilI calculatorI = null;
    private static String _msisdnString = null;

    /**
     * ensures no instantiation
     */
    private BarForDeletionProcess(){
    	
    }
    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        try {
            if (args.length != 2) {
                logger.info(METHOD_NAME, "Usage : BarForDeletionProcess [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                logger.info(METHOD_NAME, " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                logger.info(METHOD_NAME, " Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (Exception ex) {
            logger.errorTrace(METHOD_NAME, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            process();
        } catch (Exception e) {
            logger.error(METHOD_NAME, " " + e.getMessage());
            logger.errorTrace(METHOD_NAME, e);
          }
        finally {
            if (logger.isDebugEnabled()) {
                logger.debug(METHOD_NAME, " Exiting");
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                logger.errorTrace(METHOD_NAME, e);
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    private static void process() throws Exception {
        final String METHOD_NAME = "process";
        if (logger.isDebugEnabled()) {
            logger.debug("process", "Entered");
        }
        Connection con = null;
        String processId = null;
        Date currentDate = null;
        Date processedUpto = null;
        ProcessStatusVO processStatusVO = null;
        ProcessStatusDAO processStatusDAO = null;
        int beforeInterval = 0;
        Date dateCount = null;
        int updateCount = 0;
        HashMap<Integer, ChannelUserVO> userData = null;
        HashMap<Integer, ChannelUserVO> finalList = null;
        ChannelUserVO uservo = null;
        ChannelUserVO fromChannelUserVO = null;// change
        final ChannelUserVO senderVO = null;// change
        ChannelUserDAO channelUserDAO = null;
        CommissionProfileDAO commissionProfileDAO = null;
        CommissionProfileTxnDAO commissionProfileTxnDAO = null;
        final NetworkProductVO networkProductVO = null;
        NetworkProductDAO networkProductDao = null;
        String commProfileLatestVer = null;
        ArrayList<UserBalancesVO> userBalanceList = null;
        UserBalancesVO userBalanceVO = null;
        final ChannelTransferVO channelTransferVO = null;
        final ArrayList<ChannelTransferItemsVO> tempChannelTransferItemsList = null;
        final ChannelTransferItemsVO channelTransferItemsVO = null;
        final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
        int count = 0;
        final int insertCount = -1;
        ChannelTransferDAO channelTrfDAO = null;
        try {
            logger.debug("process",
                " Start of process Total memory: " + Runtime.getRuntime().totalMemory() / 1048576 + " Free memory: " + Runtime.getRuntime().freeMemory() / 1048576);

            currentDate = BTSLUtil.getSQLDateFromUtilDate(_currentDate);
            processId = ProcessI.BAR_FOR_DELETE;
            _msisdnString = new String(Constants.getProperty("adminmobile"));
			
            if (logger.isDebugEnabled())
            	logger.debug("process", "_msisdnString: " + _msisdnString);
            
            String[] adm_msisdn = _msisdnString.split(",");
            PushMessage pushMessage = null;
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                logger.debug("process", "Barred for deletion process connection is null");
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarForDeletionProcess[process]", "",
                    "", "", "DATABASE Connection is NULL");
                return;
            }
            processStatusVO = checkProcessUnderProcess(con, processId);

            // method call to find maximum date till which process has been
            // executed
            processedUpto = processStatusVO.getExecutedUpto();
            final int diffDate = BTSLUtil.getDifferenceInUtilDates(processedUpto, currentDate);
            beforeInterval = BTSLUtil.parseLongToInt( processStatusVO.getBeforeInterval() / (60 * 24) );
            if (diffDate <= beforeInterval) {
                logger.error("process", " Process already executed for the days less than before interval.....");
                throw new BTSLBaseException("BarForDeletionProcess", "process", PretupsErrorCodesI.BAR_FOR_DEL_PROCESS_ALREADY_EXECUTED);
            }

            // if difference is less than before interval then do not execute
            // process, process is already executed for the days less than
            // before interval
            if (diffDate <= beforeInterval) {
                logger.error("process", " Process already executed....." + diffDate);
                return;
            }
            processStatusVO.setStartDate(_currentDate);
            processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
            processStatusDAO = new ProcessStatusDAO();
            // execute process for all the days that are less than before
            // interval from current date,
            // means execute process upto currentDate-beforeInterval
            for (dateCount = processedUpto; dateCount.before(BTSLUtil.addDaysInUtilDate(_currentDate, -beforeInterval)); dateCount = BTSLUtil.addDaysInUtilDate(dateCount, 1)) {
                processStatusVO.setProcessStatus(ProcessI.STATUS_UNDERPROCESS);
                updateCount = processStatusDAO.updateProcessDetail(con, processStatusVO);
                if (updateCount > 0) {
                    con.commit();
                }
                processedUpto = dateCount;
            }
            final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            try {
                calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
            } catch (Exception e) {
                logger.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarForDeletionProcess[process]", "", "", "",
                    "Exception while loading the class at the call:" + e.getMessage());
            }
            makeQuery(con);
            userData = fetchData(con);
            channelUserDAO = new ChannelUserDAO();
            networkProductDao = new NetworkProductDAO();
            commissionProfileDAO = new CommissionProfileDAO();
            commissionProfileTxnDAO = new CommissionProfileTxnDAO();
            channelTrfDAO = new ChannelTransferDAO();
            finalList = new HashMap<Integer, ChannelUserVO>();
            int userDataSizes=userData.size();
            for (int i = 0; i < userDataSizes; i++) {
                uservo = userData.get(i);
                userBalanceList = loadUserBalancesForUserId(con, uservo.getUserID());
                if (userBalanceList.isEmpty()) {
                    uservo.setStatus(PretupsI.USER_STATUS_DELETED);
                    uservo.setPreviousStatus(PretupsI.USER_STATUS_BARRED);
                    uservo.setModifiedOn(_currentDate);
                    finalList.put(i, uservo);
                } else if (BTSLUtil.getDifferenceInUtilDates(uservo.getCreatedOn(), _currentDate) > Integer.parseInt(Constants.getProperty("NO_OF_DAYS_USER_REMAINS_BARRED"))) {
                	boolean sendMsgToOwner = false;
                    long totBalance = 0;
                	fromChannelUserVO = new ChannelUserVO();
                    fromChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, uservo.getUserID(), false, _currentDate,false);
                    fromChannelUserVO.setGateway(PretupsI.REQUEST_SOURCE_TYPE_WEB);
                    CommissionProfileSetVO commissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(con, fromChannelUserVO.getCommissionProfileSetID(), _currentDate);
                    commProfileLatestVer = commissionProfileSetVO.getCommProfileVersion();
                    fromChannelUserVO.setCommissionProfileSetVersion(commProfileLatestVer);
                    int userBalanceListsSizes=userBalanceList.size();
                    for (int j = 0; j <userBalanceListsSizes ; j++) {
                        userBalanceVO = (UserBalancesVO) userBalanceList.get(j);
                        if (userBalanceVO.getBalance() == 0 && j == (userBalanceList.size() - 1)) {
                            uservo.setStatus(PretupsI.USER_STATUS_DELETED);
                            uservo.setPreviousStatus(PretupsI.USER_STATUS_BARRED);
                            uservo.setModifiedOn(_currentDate);
                            finalList.put(i, uservo);
                        } else if (userBalanceVO.getBalance() == 0) {
                            continue;
                        } else {
                            if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RETURN_TO_OPERATOR_STOCK)).booleanValue() && !(("ROOT")
                                .equals(fromChannelUserVO.getParentID()))) {
                                UserDeletionBL.updateBalNChnlTransfersNItemsC2C(con, fromChannelUserVO, uservo, uservo.getUserID(), PretupsI.REQUEST_SOURCE_TYPE_WEB,
                                    userBalanceVO);
                                sendMsgToOwner = true; 
                                totBalance += userBalanceVO.getBalance();
                            } else {
                                UserDeletionBL.updateBalNChnlTransfersNItemsO2C(con, fromChannelUserVO, uservo, PretupsI.REQUEST_SOURCE_TYPE_WEB,
                                    PretupsI.REQUEST_SOURCE_TYPE_WEB, userBalanceVO);
                            }
                            // ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
                            uservo.setStatus(PretupsI.USER_STATUS_DELETED);
                            uservo.setPreviousStatus(PretupsI.USER_STATUS_BARRED);
                            uservo.setModifiedOn(_currentDate);
                            finalList.put(i, uservo);
                        }
                    }
                  //ASHU
                    if(sendMsgToOwner) {
                    	   // Change done for defect def 1086 650 PVG
	                    	ChannelUserVO prntChnlUserVO = new ChannelUserDAO().loadChannelUserByUserID(con, fromChannelUserVO.getParentID());
	                        String[] msgArr = {fromChannelUserVO.getMsisdn(),PretupsBL.getDisplayAmount(totBalance)};
                    	 /*  ChannelUserVO chnlUserVO = new ChannelUserDAO().loadUsersDetails(con, fromChannelUserVO.getMsisdn(), null, PretupsI.STATUS_IN, "'" + PretupsI.USER_STATUS_ACTIVE + "'");
                           String msgArr [] = {fromChannelUserVO.getMsisdn(),Long.toString(totBalance)};*/
                           final BTSLMessages sendBtslMessageToOwner = new BTSLMessages(PretupsErrorCodesI.OWNER_USR_BALCREDIT,msgArr);
                           final PushMessage pushMessageToOwner = new PushMessage(prntChnlUserVO.getMsisdn(), sendBtslMessageToOwner, "", "", locale, fromChannelUserVO.getNetworkID());
                           pushMessageToOwner.push();   
                    } 
                }
            }
            count = updateData(con, finalList);
            if (count < 0) {
                logger.error("process", " Data not updated.....");
                throw new BTSLBaseException("BarForDeletionProcess", "process", PretupsErrorCodesI.DATA_NOT_UPDATED);
            }
            processStatusVO.setExecutedUpto(processedUpto);
            processStatusVO.setExecutedOn(_currentDate);
            processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
            updateCount = processStatusDAO.updateProcessDetail(con, processStatusVO);
            if (updateCount > 0) {
                con.commit();
            }
            for (int i = 0; i < finalList.size(); i++) {
                uservo = finalList.get(i);
                new PushMessage(uservo.getMsisdn(), new BTSLMessages(PretupsErrorCodesI.BARRED_USER_DELETED), null, null, locale, uservo.getNetworkID()).push();
            }
            String messages="Process executed successfully.";
            for (int i = 0, len = adm_msisdn.length; i < len; i++) {
                pushMessage = new PushMessage(adm_msisdn[i], messages, null, null, locale);
                pushMessage.push();
            }
        } catch (BTSLBaseException be) {
            logger.error("process", "BTSLBaseException : " + be.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception e1) {
                    logger.errorTrace(METHOD_NAME, e1);
                }
            }
            logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            logger.error("process", "Exception : " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception e1) {
                    logger.errorTrace(METHOD_NAME, e1);
                }
            }
            logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "BarForDeletionProcess[process]", "", "", "",
                " BarForDeletionProcess process not executed successfully.");
            throw new BTSLBaseException("BarForDeletionProcess", "process", PretupsErrorCodesI.BAR_FOR_DEL_EXCEPTION);
        } finally {
            if (con != null) {
                try {
                    con.close();
                    con = null;
                } catch (SQLException e1) {
                    logger.errorTrace(METHOD_NAME, e1);
                }
            }
            if (_fetchDataStmt != null) {
                try {
                    _fetchDataStmt.close();
                } catch (Exception ex) {
                    logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_loadBalanceStmt != null) {
                try {
                    _loadBalanceStmt.close();
                } catch (Exception ex) {
                    logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_updateDataStmt != null) {
                try {
                    _updateDataStmt.close();
                } catch (Exception ex) {
                    logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("process", "Exiting..... ");
            }
        }
    }

    /**
     * Load process details, if process is already marked UnderProcess then
     * check expiry time
     * of process
     * 
     * @param p_con
     *            Connection
     * @param p_processID
     *            String
     * @return ProcessStatusVO
     * @throws BTSLBaseException
     */
    private static ProcessStatusVO checkProcessUnderProcess(Connection p_con, String p_processID) throws BTSLBaseException {
        final String METHOD_NAME = "checkProcessUnderProcess";
        if (logger.isDebugEnabled()) {
            logger.debug("checkProcessUnderProcess", "Entered with p_processID=" + p_processID);
        }
        long dateDiffInMinute = 0;
        int successC = 0;
        ProcessStatusDAO processStatusDAO = null;
        ProcessStatusVO processStatusVO = null;
        final Date date = new Date();
        try {
            processStatusDAO = new ProcessStatusDAO();
            // load the Scheduler information - start date and status of
            // scheduler
            processStatusVO = processStatusDAO.loadProcessDetail(p_con, p_processID);

            // Check Process Entry,if no entry for the process throw the
            // exception and stop the process
            if (processStatusVO == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarForDeletionProcess[checkProcessunderProcess]",
                    "", "", "", "No entry found in the process_status table for processId=" + p_processID);
                throw new BTSLBaseException("checkProcessUnderProcess", PretupsErrorCodesI.PROCESS_ENTRY_NOT_FOUND);
            }
            // if the scheduler status is UnderProcess check the expiry of
            // scheduler.
            else if (ProcessI.STATUS_UNDERPROCESS.equals(processStatusVO.getProcessStatus())) {
                // set the current date while updating the start date of process
                if (processStatusVO.getStartDate() != null) {
                    dateDiffInMinute = ((date.getTime() - processStatusVO.getStartDate().getTime()) / (1000 * 60));
                } else {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "BarForDeletionProcess[checkProcessunderProcess]", "", "", "", "Process start date is null for processId=" + p_processID);
                    throw new BTSLBaseException("checkProcessUnderProcess", "Process Start Date is NULL");
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("checkProcessUnderProcess",
                        "startDate = " + processStatusVO.getStartDate() + "dateDiffInMinute= " + dateDiffInMinute + " expiryTime = " + processStatusVO.getExpiryTime());
                }
                // Checking for the expiry time of the process.
                if (dateDiffInMinute >= processStatusVO.getExpiryTime()) {
                    processStatusVO.setStartDate(date);
                    processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    successC = processStatusDAO.updateProcessDetail(p_con, processStatusVO);
                    if (successC > 0) {
                        processStatusVO.setStatusOkBool(true);
                    } else {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "BarForDeletionProcess[checkProcessunderProcess]", "", "", "",
                            "The entry in the process_status could not be updated to 'Underprocess' after the expiry of underprocess time limit for processId=" + p_processID);
                        throw new BTSLBaseException("checkProcessUnderProcess", PretupsErrorCodesI.PROCESS_ERROR_UPDATE_STATUS);
                    }
                } else {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "BarForDeletionProcess[checkProcessunderProcess]", "", "", "", "Process is already running for processId=" + p_processID);
                    throw new BTSLBaseException("checkProcessUnderProcess", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
                }
            }
        }// end of try-block
        catch (BTSLBaseException be) {
            logger.error("checkProcessUnderProcess", "BTSLBaseException while loading process detail" + be);
            logger.errorTrace(METHOD_NAME, be);
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            logger.error("checkProcessUnderProcess", "Exception while loading process detail " + e);
            logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarForDeletionProcess[checkProcessUnderProcess]",
                "processStatusVO.getProcessID()" + processStatusVO.getProcessID(), "", "", " Exception while loadng process detail " + e.getMessage());
            throw new BTSLBaseException("checkProcessUnderProcess", e.getMessage());
        }// end of catch-Exception
        finally {
            if (logger.isDebugEnabled()) {
                logger.debug("checkProcessUnderProcess", "Exiting processStatusVO=" + processStatusVO);
            }
        }// end of finally
        return processStatusVO;
    }

    /**
     * generate preparedStatement for all the queries that are used more
     * 
     * @param p_con
     *            Connection
     * @throws BTSLBaseException
     */

    private static void makeQuery(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "makeQuery";

        if (logger.isDebugEnabled()) {
            logger.debug("makeQuery", "Entered");
        }
        String query = null;
        try {

            StringBuilder qryBuffer = new StringBuilder();
            
            String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
            boolean tcpOn = false;
            Set<String> uniqueTransProfileId = new HashSet();
            
            if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
            	tcpOn = true;
            }
            String sqlSelect = null;
            
            if(tcpOn) {
                qryBuffer.append(" SELECT u.user_id,up.msisdn,ue.created_on,ue.created_by, cusers.transfer_profile_id, dm.domain_type_code,");
                qryBuffer.append("u.category_code,cusers.user_grade, u.network_code,c.domain_code ,GD.grph_domain_name, ug.grph_domain_code ");
                qryBuffer.append(" FROM users u, user_phones up, user_event_remarks ue,categories c,channel_grades cg,channel_users cusers ");
                qryBuffer.append(" ,categories cat ,user_geographies ug,geographical_domains GD, domains dm  ");
                qryBuffer.append(" WHERE u.user_id=ue.user_id and u.user_id=up.user_id and u.status='BD' ");
                qryBuffer.append("  and ue.event_type='BAR_REQ' and c.category_code=u.category_code AND u.user_id=cusers.user_id AND cat.category_code=U.category_code");
                qryBuffer.append(" AND cg.grade_code = cusers.user_grade  AND ug.user_id = u.user_id ");
                qryBuffer.append(" AND dm.domain_code = cat.domain_code AND ug.grph_domain_code = GD.grph_domain_code");

     	
            }else {

            qryBuffer.append(" SELECT u.user_id,up.msisdn,ue.created_on,ue.created_by, cusers.transfer_profile_id, dm.domain_type_code,");
            qryBuffer.append("u.category_code,cusers.user_grade, u.network_code,c.domain_code ,GD.grph_domain_name, ug.grph_domain_code ");
            qryBuffer.append(" FROM users u, user_phones up, user_event_remarks ue,categories c,channel_grades cg,channel_users cusers,transfer_profile tp");
            qryBuffer.append(" ,categories cat ,user_geographies ug,geographical_domains GD, domains dm  ");
            qryBuffer.append(" WHERE u.user_id=ue.user_id and u.user_id=up.user_id and u.status='BD' ");
            qryBuffer.append("  and ue.event_type='BAR_REQ' and c.category_code=u.category_code AND u.user_id=cusers.user_id AND cat.category_code=U.category_code");
            qryBuffer.append(" AND cg.grade_code = cusers.user_grade AND tp.profile_id = cusers.transfer_profile_id AND ug.user_id = u.user_id ");
            qryBuffer.append(" AND dm.domain_code = cat.domain_code AND ug.grph_domain_code = GD.grph_domain_code");

            }
            query = qryBuffer.toString();
            if (logger.isDebugEnabled()) {
                logger.debug("makeQuery", "Query: " + query);
            }
            _fetchDataStmt = p_con.prepareStatement(query);

            qryBuffer = new StringBuilder();
            qryBuffer.append(" update users set status=?, modified_on=?,previous_status=?, login_id=? where user_id=? ");
            query = qryBuffer.toString();
            if (logger.isDebugEnabled()) {
                logger.debug("makeQuery", "Query: " + query);
            }
            _updateDataStmt = p_con.prepareStatement(query);

            qryBuffer = new StringBuilder();
            qryBuffer.append("SELECT ub.balance,ub.prev_balance,ub.balance_type,ub.network_code,ub.network_code_for,p.product_short_code,p.product_name, ");
            qryBuffer.append("p.product_code,ub.last_transfer_no FROM user_balances ub,products p ");
            qryBuffer.append("WHERE ub.product_code=p.product_code ");
            qryBuffer.append("AND ub.user_id=?");
            query = qryBuffer.toString();
            if (logger.isDebugEnabled()) {
                logger.debug("makeQuery", "Query: " + query);
            }
            _loadBalanceStmt = p_con.prepareStatement(query);
        } catch (SQLException se) {
            logger.error("makeQuery", "SQLException: " + se.getMessage());
            logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("BarForDeletionProcess", "makeQuery", PretupsErrorCodesI.BAR_FOR_DEL_EXCEPTION);
        } catch (Exception e) {
            logger.error("makeQuery", "Exception: " + e.getMessage());
            logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("BarForDeletionProcess", "makeQuery", PretupsErrorCodesI.BAR_FOR_DEL_EXCEPTION);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("makeQuery", "Exiting..... ");
            }
        }
    }

    private static HashMap<Integer, ChannelUserVO> fetchData(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "fetchData";
        if (logger.isDebugEnabled()) {
            logger.debug("fetchData", "Entered ");
        }
        HashMap<Integer, ChannelUserVO> userMap = null;
        ChannelUserVO uservo = null;
         
        try {
            userMap = new HashMap<Integer, ChannelUserVO>();
            try(ResultSet rst = _fetchDataStmt.executeQuery();)
            {
            int i = 0;
            while (rst.next()) {
                uservo = new ChannelUserVO();
                uservo.setUserID(rst.getString("user_id"));
                uservo.setMsisdn(rst.getString("msisdn"));
                uservo.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(rst.getTimestamp("created_on")));
                uservo.setCreatedBy(rst.getString("created_by"));
                uservo.setNetworkID(rst.getString("network_code"));
                uservo.setCategoryCode(rst.getString("category_code"));
                uservo.setDomainID(rst.getString("domain_code"));
                uservo.setDomainTypeCode(rst.getString("domain_type_code"));
                uservo.setTransferProfileID(rst.getString("transfer_profile_id"));
                uservo.setGeographicalCode(rst.getString("grph_domain_code"));
                uservo.setGeographicalDesc(rst.getString("grph_domain_name"));
                uservo.setUserGrade(rst.getString("user_grade"));
                userMap.put(i, uservo);
                i++;
            }
            }
        } catch (Exception e) {
            logger.error("makeQuery", "Exception: " + e.getMessage());
            logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("BarForDeletionProcess", "fetchData", PretupsErrorCodesI.BAR_FOR_DEL_EXCEPTION);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("fetchData", "Exiting..... ");
            }
        }
        return userMap;
    }

    private static ArrayList<UserBalancesVO> loadUserBalancesForUserId(Connection p_con, String p_userId) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserBalancesForUserId";
        if (logger.isDebugEnabled()) {
            logger.debug("loadUserBalancesForUserId", "Entered p_userId=" + p_userId);
        }
        ArrayList<UserBalancesVO> userList = null;
        UserBalancesVO balanceVO = null;
        
        try {
            userList = new ArrayList<UserBalancesVO>();
            _loadBalanceStmt.setString(1, p_userId);
           try( ResultSet rst = _loadBalanceStmt.executeQuery();)
           {
            while (rst.next()) {
                balanceVO = new UserBalancesVO();
                balanceVO.setBalance(rst.getLong("balance"));
                balanceVO.setProductShortCode(rst.getString("product_short_code"));
                balanceVO.setProductName(rst.getString("product_name"));
                balanceVO.setProductCode(rst.getString("product_code"));
                balanceVO.setNetworkCode(rst.getString("network_code"));
                balanceVO.setNetworkFor(rst.getString("network_code_for"));
                balanceVO.setPreviousBalance(rst.getLong("prev_balance"));
                balanceVO.setBalanceType(rst.getString("balance_type"));
			    balanceVO.setLastTransferID(rst.getString("last_transfer_no"));
				
                userList.add(balanceVO);
            }
        }
        }catch (Exception e) {
            logger.error("loadUserBalancesForUserId", "Exception: " + e.getMessage());
            logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("BarForDeletionProcess", "loadUserBalancesForUserId", PretupsErrorCodesI.BAR_FOR_DEL_EXCEPTION);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("loadUserBalancesForUserId", "Exiting..... ");
            }
        }
        return userList;
    }

    private static int updateData(Connection p_con, HashMap<Integer, ChannelUserVO> p_finalList) throws BTSLBaseException {
        final String METHOD_NAME = "updateData";
        if (logger.isDebugEnabled()) {
            logger.debug("updateData", "Entered ");
        }
        ChannelUserVO uservo = null;
        int[] updateCount;
        try {
            Integer key = null;
            final Iterator iterator = p_finalList.keySet().iterator();
            while (iterator.hasNext()) {
                int j = 0;
                key = (Integer) iterator.next();
                uservo = p_finalList.get(key);
                _updateDataStmt.setString(++j, uservo.getStatus());
                _updateDataStmt.setDate(++j, BTSLUtil.getSQLDateFromUtilDate(uservo.getModifiedOn()));
                _updateDataStmt.setString(++j, uservo.getPreviousStatus());
                _updateDataStmt.setString(++j, uservo.getUserID());
                _updateDataStmt.setString(++j, uservo.getUserID());
                _updateDataStmt.addBatch();
                _updateDataStmt.clearParameters();
            }
            updateCount = _updateDataStmt.executeBatch();
        } catch (Exception e) {
            logger.error("makeQuery", "Exception: " + e.getMessage());
            logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("BarForDeletionProcess", "updateData", PretupsErrorCodesI.BAR_FOR_DEL_EXCEPTION);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("updateData", "Exiting..... ");
            }
        }
        return updateCount.length;
    }

    /*
     * public static void genrateWithdrawID(Connection con,ChannelTransferVO
     * p_channelTransferVO) throws BTSLBaseException
     * {
     * //for generating new withdraw id.
     * //returns new unique withdraw id
     * final String METHOD_NAME = "genrateWithdrawID";
     * if (logger.isDebugEnabled()) {
     * logger.debug("genrateWithdrawID", "Entered ChannelTransferVO =" +
     * p_channelTransferVO);
     * }
     * try
     * {
     * _idGeneratorDAO = new IDGeneratorDAO();
     * long id=_idGeneratorDAO.getNextID(con,PretupsI.CHANNEL_WITHDRAW_O2C_ID,
     * BTSLUtil.getFinancialYear() , p_channelTransferVO);
     * p_channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(
     * p_channelTransferVO,PretupsI.CHANNEL_WITHDRAW_O2C_ID,id));
     * 
     * } catch (Exception e)
     * {
     * logger.error("genrateWithdrawID", "Exception " + e.getMessage());
     * logger.errorTrace(METHOD_NAME,e);
     * throw new BTSLBaseException("BarForDeletionProcess", "genrateWithdrawID",
     * PretupsErrorCodesI.BAR_FOR_DEL_EXCEPTION);
     * }finally
     * {
     * if (logger.isDebugEnabled()) {
     * logger.debug("genrateReturnID",
     * "Exited  ID ="+p_channelTransferVO.getTransferID());
     * }
     * }
     * }
     */
    public static String genrateStockTransctionID(Connection con, NetworkStockTxnVO p_networkStockTxnVO) throws BTSLBaseException {
        final String METHOD_NAME = "genrateStockTransctionID";
        if (logger.isDebugEnabled()) {
            logger.debug("genrateStockTransctionID", "Entered ");
        }
        String uniqueID = null;
        try {
            _idGeneratorDAO = new IDGeneratorDAO();
            final long id = _idGeneratorDAO.getNextID(con, PretupsI.NETWORK_STOCK_TRANSACTION_ID, BTSLUtil.getFinancialYear(), p_networkStockTxnVO);
            uniqueID = calculatorI.formatNetworkStockTxnID(p_networkStockTxnVO, id);
        } catch (Exception e) {
            logger.error("genrateStockTransctionID", "Exception " + e.getMessage());
            logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("BarForDeletionProcess", "genrateStockTransctionID", PretupsErrorCodesI.BAR_FOR_DEL_EXCEPTION);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("genrateStockTransctionID", "Exited  " + uniqueID);
            }
        }
        return uniqueID;
    }

    public static int updateNetworkStockTransactionDetails(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userID, Date p_curDate) throws BTSLBaseException {
        if (logger.isDebugEnabled()) {
            logger.debug("updateNetworkStockTransactionDetails", "Entered ChannelTransferVO =" + p_channelTransferVO + " USERID " + p_userID + " Curdate " + p_curDate);
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
        networkStockTxnVO.setTxnNo(genrateStockTransctionID(p_con, networkStockTxnVO));
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

        final ArrayList<NetworkStockTxnItemsVO> networkStockTxnItemsVOList = new ArrayList<>();
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
        if (logger.isDebugEnabled()) {
            logger.debug("updateNetworkStockTransactionDetails", "Exited  updateCount " + updateCount);
        }
        return updateCount;
    }

    /*
     * private static ChannelTransferItemsVO
     * prepareChannelTransferItemsVO(Connection p_con, ChannelUserVO
     * p_channelUserVO, UserBalancesVO p_userBalanceVO) throws BTSLBaseException
     * {
     * if (logger.isDebugEnabled()) {
     * logger.debug("prepareChannelTransferItemsVO",
     * "Entering  : p_channelUserVO" + p_channelUserVO + "UserBalancesVO" +
     * p_userBalanceVO);
     * }
     * NetworkProductVO networkProductVO = null;
     * ChannelTransferItemsVO channelTransferItemsVO = null;
     * networkProductVO = (NetworkProductVO)
     * _networkProductMap.get(p_userBalanceVO.getProductCode());
     * // default commission rate
     * double commRate = 0.0;
     * if
     * (p_userBalanceVO.getProductCode().equals(networkProductVO.getProductCode
     * ())) {
     * channelTransferItemsVO = new ChannelTransferItemsVO();
     * channelTransferItemsVO.setProductType(networkProductVO.getProductType());
     * channelTransferItemsVO.setProductShortCode(networkProductVO.
     * getProductShortCode());
     * channelTransferItemsVO.setProductCode(networkProductVO.getProductCode());
     * channelTransferItemsVO.setProductName(networkProductVO.getProductName());
     * channelTransferItemsVO.setShortName(networkProductVO.getShortName());
     * channelTransferItemsVO.setProductShortCode(networkProductVO.
     * getProductShortCode());
     * channelTransferItemsVO.setProductCategory(networkProductVO.getProductCategory
     * ());
     * channelTransferItemsVO.setErpProductCode(networkProductVO.getErpProductCode
     * ());
     * channelTransferItemsVO.setStatus(networkProductVO.getStatus());
     * channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());
     * channelTransferItemsVO.setModuleCode(networkProductVO.getModuleCode());
     * channelTransferItemsVO.setProductUsage(networkProductVO.getProductUsage())
     * ;
     * channelTransferItemsVO.setRequestedQuantity(PretupsBL.getDisplayAmount(
     * p_userBalanceVO.getBalance()));
     * channelTransferItemsVO.setRequiredQuantity(p_userBalanceVO.getBalance());
     * // setting the default value for this
     * channelTransferItemsVO.setTax1Type(PretupsI.AMOUNT_TYPE_PERCENTAGE);
     * channelTransferItemsVO.setTax1Rate(commRate);
     * channelTransferItemsVO.setTax2Type(PretupsI.AMOUNT_TYPE_PERCENTAGE);
     * channelTransferItemsVO.setTax2Rate(commRate);
     * channelTransferItemsVO.setTax3Type(PretupsI.AMOUNT_TYPE_PERCENTAGE);
     * channelTransferItemsVO.setTax3Rate(commRate);
     * channelTransferItemsVO.setCommType(PretupsI.AMOUNT_TYPE_PERCENTAGE);
     * channelTransferItemsVO.setCommRate(commRate);
     * channelTransferItemsVO.setCommProfileDetailID(PretupsI.NOT_APPLICABLE);
     * channelTransferItemsVO.setRequiredQuantity(PretupsBL.getSystemAmount(
     * channelTransferItemsVO.getRequestedQuantity()));
     * channelTransferItemsVO.setDiscountType(PretupsI.AMOUNT_TYPE_PERCENTAGE);
     * channelTransferItemsVO.setDiscountRate(commRate);
     * } else {
     * logger.error("prepareChannelTransferItemsVO ",
     * ": Associated product for the user could not be found for the user id : "
     * + p_channelUserVO.getUserID());
     * throw new BTSLBaseException("BarForDeletionProcess",
     * "prepareChannelTransferItemsVO", PretupsErrorCodesI.ASS_PROD_NOT_FOUND);
     * }
     * if (logger.isDebugEnabled()) {
     * logger.debug("prepareChannelTransferItemsVO",
     * "Exiting : channelTransferItemsVO" + channelTransferItemsVO);
     * }
     * return channelTransferItemsVO;
     * }
     */

    private static ChannelTransferVO prepareChannelTransferVO(ChannelTransferVO p_channelTransferVO, ChannelTransferItemsVO p_channelTransferItemsVO, Date p_curDate, ChannelUserVO p_channelUserVO, UserVO p_userVO) throws BTSLBaseException {

        if (logger.isDebugEnabled()) {
            logger.debug("prepareChannelTransferVO", "Entering  : p_channelTransferVO" + p_channelTransferVO + "p_channelUserVO" + p_channelUserVO + "p_userVO" + p_userVO);
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
        p_channelTransferVO.setCreatedOn(p_curDate);
        p_channelTransferVO.setCreatedBy(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setModifiedOn(p_curDate);
        p_channelTransferVO.setModifiedBy(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
        p_channelTransferVO.setTransferInitatedBy(p_userVO.getUserID());
        p_channelTransferVO.setReceiverTxnProfile("");
        p_channelTransferVO.setSenderTxnProfile(p_channelUserVO.getTransferProfileID());
        p_channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_TYPE_WEB);
        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setFromUserCode(p_channelUserVO.getUserCode());
        p_channelTransferVO.setTransferCategory(PretupsI.TRANSFER_TYPE_SALE);
        String wallet = BTSLUtil.NullToString(Constants.getProperty("WALLET_TYPE"));
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
        totRequestQty += PretupsBL.getSystemAmount(p_channelTransferItemsVO.getRequestedQuantity());
        totMRP += (Double.parseDouble(p_channelTransferItemsVO.getRequestedQuantity()) * p_channelTransferItemsVO.getUnitValue());
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
        p_channelTransferVO.setProductType(p_channelTransferItemsVO.getProductType());
        p_channelTransferVO.setProductType(productType);

        if (logger.isDebugEnabled()) {
            logger.debug("prepareChannelTransferVO", "Exiting .....  :p_channelTransferVO" + p_channelTransferVO);
        }
        return p_channelTransferVO;
    }

    private static void transactionApproval(Connection p_con, ChannelTransferVO p_channelTransferVO, ChannelTransferItemsVO p_channelTransferItemsVO, String p_userID, Date p_date) throws BTSLBaseException {

        if (logger.isDebugEnabled()) {
            logger.debug("transactionApproval", "Entering  : p_channelTransferVO " + p_channelTransferVO);
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
        }// end of if
        UserBalancesVO userBalanceVO = null;
        final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
        userBalanceVO = new UserBalancesVO();

        userBalanceVO.setUserID(p_channelTransferVO.getFromUserID());
        userBalanceVO.setProductCode(p_channelTransferItemsVO.getProductCode());
        userBalanceVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
        userBalanceVO.setNetworkFor(p_channelTransferVO.getNetworkCodeFor());
        userBalanceVO.setLastTransferID(p_channelTransferVO.getTransferID());
        userBalanceVO.setLastTransferType(p_channelTransferVO.getTransferType());
        userBalanceVO.setLastTransferOn(p_channelTransferVO.getTransferDate());
        userBalanceVO.setPreviousBalance(userBalanceVO.getBalance());
        userBalanceVO.setQuantityToBeUpdated(p_channelTransferItemsVO.getRequiredQuantity());

        updateCount = -1;
        // this method updates the user balances performing debit/credit on his
        // balance ar applicable
        updateCount = userBalancesDAO.updateUserDailyBalances(p_con, p_date, userBalanceVO);
        if (updateCount < 1) {
            throw new BTSLBaseException("BarForDeletionProcess", "transactionApproval", PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
        }// end of if
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        updateCount = -1;
        updateCount = channelUserDAO.debitUserBalances(p_con, p_channelTransferVO, false, null);
        if (updateCount < 1) {
            throw new BTSLBaseException("BarForDeletionProcess", "transactionApproval", PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
        }//
        updateCount = -1;
        // this call updates the counts/values for daily, weekly and monthly IN
        updateCount = ChannelTransferBL.updateOptToChannelUserInCounts(p_con, p_channelTransferVO, null, p_date);
        if (updateCount < 1) {
            throw new BTSLBaseException("BarForDeletionProcess", "transactionApproval", PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
        }// end of if

        if (logger.isDebugEnabled()) {
            logger.debug("transactionApproval", "Exiting...... : p_channelTransferVO " + p_channelTransferVO);
        }
    }
}