package com.btsl.pretups.processes;

/*
 * CPUserSuspension.java
 * Name Date History
 * ------------------------------------------------------------------------
 * 
 * Vikas Jauhari 21/5/2012 Initial Creation
 * Ashish Kumar Todia 28/05/12 Modification.
 * 
 * ------------------------------------------------------------------------
 * Copyright (c) 2011 Comviva Technologies Ltd.
 * This Process is executed to Suspend those CP Users who has crossed Days limit
 * assigned to them to fulfill
 * their requirement.
 */

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class CPUserSuspension {
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;
    private static final Log _logger = LogFactory.getLog(CPUserSuspension.class.getName());
    private static String _networkCode = null; // new added for category level

    
    /**
     * ensures no instantiation
     */
    private CPUserSuspension(){
    	
    }
    
    // prefernces.
    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        try {
            if (args.length != 3) {
                System.out.println("Usage : CPUserSuspension [Constants file] [LogConfig file] [NetworkCode]");
                return;
            }
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                _logger.debug(METHOD_NAME, " Constants file not found on provided location.");
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                _logger.debug(METHOD_NAME, " Logconfig file not found on provided location.");
                return;
            }
            _networkCode = args[2];
            if (_networkCode == null || _networkCode.length() == 0) {
                System.out.println(" Network Code Not Found .............");
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
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("main", " " + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.info("main", "Exiting");
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * @author vikas.jauhari
     * @throws BTSLBaseException
     * @return void
     */
    private static void process() throws BTSLBaseException {
        final String METHOD_NAME = "process";
        if (_logger.isDebugEnabled()) {
            _logger.info("process", "Entered");
        }

        Date processedUpto = null;
        Date processExecutedCheck = null;
        Date currentDate = new Date();
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        try {
            _logger.debug("process", "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            // Make Connection
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CPUserSuspension[process]", "", "",
                    "", "DATABASE Connection is NULL");
                return;
            }
            // getting process id=CPUSRSUSP
            processId = ProcessI.CPUSER_SUSPENSION_PROCESS;
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
                    // Check whether process has been executed till current date
                    // or not
                    processExecutedCheck = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    if (BTSLUtil.getDifferenceInUtilDates(processExecutedCheck, currentDate) == 0) {
                        throw new BTSLBaseException("CPUserSuspension", "process", PretupsErrorCodesI.CPUSER_SUSPENSION_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
                    }
                    // adding 1 in processed upto date as we have to start from
                    // the next day till which process has been executed
                    processedUpto = BTSLUtil.addDaysInUtilDate(currentDate, -1);

                    // Load all categories other then OPERATOR categories.
                    final ArrayList categoryList = new CategoryDAO().loadOtherCategorList(con, PretupsI.CATEGORY_TYPE_OPT);
                    int categoryListsSize=categoryList.size();
                    for (int i = 0; i < categoryListsSize; i++) {
                        final CategoryVO categoryVO = (CategoryVO) categoryList.get(i);
                        final Date diff = BTSLUtil.addDaysInUtilDate(currentDate, -((Integer) PreferenceCache.getControlPreference(PreferenceI.CP_SUSPENSION_DAYS_LIMIT,
                            _networkCode, categoryVO.getCategoryCode())).intValue());
                        _logger
                            .debug(
                                "process",
                                " Suspending user for Category Code =" + categoryVO.getCategoryCode() + " , Category Name =" + categoryVO.getCategoryName() + ", with preference value=" + diff);
                        suspendSTKUsers(con, diff, categoryVO.getCategoryCode());
                    }
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CPUserSuspension[process]", "", "", "",
                        " CPUserSuspension process has been executed successfully.");
                }
                // To avoid the null pointer exception thrown, in case
                // processesUpto is null
                else {
                    throw new BTSLBaseException("CPUserSuspension", "process", PretupsErrorCodesI.CPUSER_SUSPENSION_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            }
        } catch (BTSLBaseException be) {
            _logger.error("process", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.error("process", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CPUserSuspension[process]", "", "", "",
                " CPUserSuspension process could not be executed successfully.");
            throw new BTSLBaseException("CPUserSuspension", "process", PretupsErrorCodesI.CPUSER_SUSPENSION_ERROR_EXCEPTION);
        } finally {
            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk) {
                try {
                    _processStatusVO.setExecutedUpto(processedUpto);
                    _processStatusVO.setExecutedOn(currentDate);
                    if (markProcessStatusAsComplete(con, processId) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                            con.rollback();
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
            _logger.debug("process", "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "Exiting..... ");
            }
        }
    }

    /**
     * @author vikas.jauhari
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
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CPUserSuspension[markProcessStatusAsComplete]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CPUserSuspension", "markProcessStatusAsComplete", PretupsErrorCodesI.CPUSER_SUSPENSION_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * This method load the STK Users based on systempreference defined and then
     * suspend and send SMS,
     * There will be connection commit after each user suspend.
     * 
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws SQLException 
     * @throws BTSLBaseException 
     * @throws Exception
     */

    private static void suspendSTKUsers(Connection p_con, java.util.Date p_differnce, String p_categoryCode) throws SQLException, BTSLBaseException {
        final String METHOD_NAME = "suspendSTKUsers";
        if (_logger.isDebugEnabled()) {
            _logger.debug("loadUserNeedForApproval", "Entered p_differnce= " + p_differnce);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UserVO userVO = null;
        UserPhoneVO userPhoneVO = null;
        final Date currentdate = new Date();

        PreparedStatement pstmtSuspend = null;
        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("select U.user_id, U.user_name, U.status, U.category_code,U.created_on,U.modified_by, ");
            strBuff.append("U.level1_approved_on,U.msisdn, U.user_type, UP.phone_language, UP.country ");
            strBuff.append("FROM users U, user_phones UP WHERE U.status=? AND U.user_type=? AND ");
            strBuff.append("U.user_id=UP.user_id AND U.creation_type=? AND U.level1_approved_on is null ");
            strBuff.append("And trunc(u.CREATED_ON) <= ?"); // new added
            strBuff.append("And U.category_code =? "); // new added
            final String sqlSelect = strBuff.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadUserNeedForApproval", "QUERY sqlSelect=" + sqlSelect);
            }

            final StringBuffer suspendQuery = new StringBuffer();
            suspendQuery.append("UPDATE users SET status = ?, previous_status = ? , modified_by = ?,");
            suspendQuery.append(" modified_on = ? WHERE user_id = ? ");
            final String suspendQry = suspendQuery.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadUserNeedForApproval", "suspendQry=" + suspendQry);
            }

            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, TypesI.YES);
            pstmt.setString(2, PretupsI.USER_TYPE_CHANNEL);
            pstmt.setString(3, PretupsI.STK_SYSTEM_USR_CREATION_TYPE);
            pstmt.setTimestamp(4, (BTSLUtil.getTimestampFromUtilDate(p_differnce))); // new
            // added
            pstmt.setString(5, p_categoryCode); // new added
            rs = pstmt.executeQuery();

            pstmtSuspend = p_con.prepareStatement(suspendQry);

            while (rs.next()) {
                userVO = new UserVO();
                userVO.setUserID(rs.getString("user_id"));
                userVO.setUserName(rs.getString("user_name"));
                userVO.setStatus(rs.getString("status"));
                userVO.setCategoryCode(rs.getString("category_code"));
                userVO.setCreatedOn(rs.getTimestamp("created_on"));
                userVO.setModifiedBy(rs.getString("modified_by"));
                userVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
                userVO.setMsisdn(rs.getString("msisdn"));
                userVO.setUserType(rs.getString("user_type"));
                userPhoneVO = new UserPhoneVO();
                userPhoneVO.setPhoneLanguage(rs.getString("phone_language"));
                userPhoneVO.setCountry(rs.getString("country"));
                userVO.setUserPhoneVO(userPhoneVO);
                _logger.debug("loadUserNeedForApproval", "Suspending User :" + userVO.getMsisdn());
                pstmtSuspend.setString(1, PretupsI.USER_STATUS_SUSPEND);
                pstmtSuspend.setString(2, userVO.getStatus());
                pstmtSuspend.setString(3, PretupsI.SYSTEM);
                pstmtSuspend.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(currentdate));
                pstmtSuspend.setString(5, userVO.getUserID());
                final int count = pstmtSuspend.executeUpdate();
                if (count > 0) {
                    p_con.commit();
                    sendMessage(userVO);
                } else {
                    _logger.error("loadUserNeedForApproval", "For Channel User =" + userVO.getMsisdn() + " Not able to suspend status getting count during commit: " + count);
                    throw new SQLException();
                }
                pstmtSuspend.clearParameters();
            }
        } catch (SQLException sqlexp) {
            try {
                p_con.rollback();
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            _logger.errorTrace(METHOD_NAME, sqlexp);
            _logger.error("loadUserNeedForApproval", "For Channel User =" + userVO.getMsisdn() + " Not able to suspend status getting SQLException=" + sqlexp.getMessage());
            throw sqlexp;
        } catch (Exception ex) {
            try {
                p_con.rollback();
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            _logger.errorTrace(METHOD_NAME, ex);
            _logger.error("loadUserNeedForApproval", "For Channel User =" + userVO.getMsisdn() + " Not able to suspend status getting Exception=" + ex.getMessage());
            throw new BTSLBaseException("CPUserSuspension", METHOD_NAME, "Exception while suspending status.");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSuspend != null) {
                    pstmtSuspend.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadUserNeedForApproval", "Exiting ");
            }
        }
    }

    /**
     * @param p_con
     *            Connection
     * @param p_userList
     *            ArrayList
     * @return void
     * @throws BTSLBaseException 
     * @throws Exception
     */
    private static void sendMessage(UserVO p_userVO) throws BTSLBaseException {
        final String METHOD_NAME = "sendMessage";
        if (_logger.isDebugEnabled()) {
            _logger.debug("sendMessage", "Entered: sending SMS to :" + p_userVO.getMsisdn());
        }
        Locale locale = null;
        String key = null;
        try {
            if (BTSLUtil.isNullString(p_userVO.getUserPhoneVO().getPhoneLanguage()) && BTSLUtil.isNullString(p_userVO.getUserPhoneVO().getCountry())) {
                locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            } else {
                locale = new Locale(p_userVO.getUserPhoneVO().getPhoneLanguage(), p_userVO.getUserPhoneVO().getCountry());
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("sendMessage", " phoneLanguage= " + p_userVO.getUserPhoneVO().getPhoneLanguage() + "phoneCountry= " + p_userVO.getUserPhoneVO().getCountry());
            }
            final String[] arrMsg = { p_userVO.getMsisdn() };
            key = PretupsErrorCodesI.CPUSER_SUSPENSION_SUCCESSFULL_MESSAGE;
            final String senderMessage = BTSLUtil.getMessage(locale, key, arrMsg);
            final PushMessage pushMessage = new PushMessage(p_userVO.getMsisdn(), senderMessage, null, null, locale);
            pushMessage.push();

            try {
                Thread.sleep(5);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
        } catch (Exception ex) {
            _logger.error("sendMessage", "For Channel User =" + p_userVO.getMsisdn() + " Not able to send SMS, getting Exception=" + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException("CPUserSuspension", METHOD_NAME, "Exception in sending message.");
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("sendMessage", "Exiting sending SMS to :" + p_userVO.getMsisdn());
            }
        }
    }
}
