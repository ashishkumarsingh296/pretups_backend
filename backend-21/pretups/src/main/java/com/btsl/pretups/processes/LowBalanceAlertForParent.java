package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
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
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.ProcessesLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.LowBalanceAlertVO;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;

/**
 * @(#)LowBalanceAlertForParent.java
 * 
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Author Date History
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Manisha Jain 30/05/2008 Initial Creation
 *                                   ------------------------------------------
 *                                   ------------------------------
 *                                   Copyright (c) 2008 Bharti Telesoft Ltd.
 *                                   All Rights Reserved
 */

/**
 * Class will sent alert to the parent if its child user have low balance
 * if child is assiociated with more than one product and have low balance
 * in multiple product, then message will contain information about multiple
 * products
 * Message will contain - mobile number of child user, product name, balance for
 * product
 */
public class LowBalanceAlertForParent {
    private static Log _logger = LogFactory.getLog(LowBalanceAlertForParent.class.getName());
    private static String _parentAlert = null;
    
    /**
     * ensures no instantiation
     */
    private LowBalanceAlertForParent(){
    	
    }
    public static void main(String args[]) {
        final String METHOD_NAME = "main";
        try {
            if (args.length < 2 || args.length > 3) {
            	_logger.error("LowBalanceAlertForParent[main]","LowBalanceAlertForParent [Constants file] [Logconfig File] [Y/N]" + args.length);
                return;
            }
            // load constants.props
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                _logger.error("LowBalanceAlertForParent[main]", "Constants file not found on location: " + constantsFile.toString());
                return;
            }
            // load log config file
            final File logFile = new File(args[1]);
            if (!logFile.exists()) {
                _logger.error("LowBalanceAlertForParent[main]", "Logconfig File not found on location: " + logFile.toString());
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logFile.toString());
            _parentAlert = args[2];
            if (BTSLUtil.isNullString(_parentAlert)) {
                _parentAlert = "Y";
            }
        }// end of try block
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("main", " Error in Loading Files ...........................: " + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch block
        try {
            process();
        }// end of try block
        catch (BTSLBaseException be) {
            _logger.error("main", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            return;
        }// end of catch block
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("main", " " + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
            return;
        }// end of catch block
        finally {
            if (_logger.isDebugEnabled()) {
                _logger.info("main", "Exiting");
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            ConfigServlet.destroyProcessCache();
        }// end of finally
    }

    /**
     * This method checks the process is under process/complete for the process
     * id
     * specified in process_status table
     * 
     * @return void
     * @throws BTSLBaseException
     */
    private static void process() throws BTSLBaseException {
        final String METHOD_NAME = "process";
        if (_logger.isDebugEnabled()) {
            _logger.info(METHOD_NAME, "Entered ");
        }
        String processId = null;
        ProcessBL processBL = null;
        Connection con = null;
        int beforeInterval = 0;
        ProcessStatusVO processStatusVO = null;
        Date currentDate = null;
        Date processedUpto = null;
        int updateCount = 0; // check process details are updated or not
        try {
            processId = ProcessI.LOW_BAL_ALERT_PARENT;
            con = OracleUtil.getSingleConnection();
            processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(con, processId);
            beforeInterval = BTSLUtil.parseLongToInt( processStatusVO.getBeforeInterval() / (60 * 24));
            if (processStatusVO.isStatusOkBool()) {
                // method call to find maximum date till which process has been
                // executed
                processedUpto = processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(new Date()));
                    processedUpto = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(processedUpto));
                    final int diffDate = BTSLUtil.getDifferenceInUtilDates(processedUpto, currentDate);
                    currentDate = new Date();
                    if (diffDate <= 1) {
                        _logger.error(METHOD_NAME, " Process already executed.....");
                        throw new BTSLBaseException("LowBalanceAlertForParent", "process", PretupsErrorCodesI.LOW_BAL_ALERT_PARENT_ALREADY_EXECUTED);
                    }
                    con.commit();
                    processedUpto = BTSLUtil.addDaysInUtilDate(currentDate, -(beforeInterval + 1));
                    // call process for uploading transfer details
                    final boolean isDataProcessed = balanceAlert(con);
                    if (isDataProcessed) {
                        processStatusVO.setExecutedUpto(processedUpto);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LowBalanceAlertForParent[process]", "", "",
                            "", " Low balance alert for parent process has been executed successfully.");
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(METHOD_NAME, "message sent successfully");
                        }
                    }
                } else {
                    throw new BTSLBaseException("LowBalanceAlertForParent", METHOD_NAME, PretupsErrorCodesI.LOW_BAL_ALERT_PARENT_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            } else {
                throw new BTSLBaseException("LowBalanceAlertForParent", METHOD_NAME, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }
            
            
         // send the message as SMS
            PushMessage pushMessage = null;
            final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            final BTSLMessages message = new BTSLMessages(PretupsErrorCodesI.PROCESS_ADMIN_MESSAGE);
            final String msisdnString = new String(Constants.getProperty("adminmobile"));
            final String[] msisdn = msisdnString.split(",");
            for (int i = 0; i < msisdn.length; i++) {
                pushMessage = new PushMessage(msisdn[i], message, "", "", locale,"");
                pushMessage.push();
            }
            
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LowBalanceAlertForParent[process]", "", "", "",
                " LowBalanceAlertForParent process could not be executed successfully.");
            throw new BTSLBaseException("LowBalanceAlertForParent", METHOD_NAME, PretupsErrorCodesI.ERROR_IN_DAILY_ALERT);
        } finally {
            try {
                if (processStatusVO.isStatusOkBool()) {
                    processStatusVO.setStartDate(currentDate);
                    processStatusVO.setExecutedOn(currentDate);
                    processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    updateCount = (new ProcessStatusDAO()).updateProcessDetail(con, processStatusVO);
                    if (updateCount > 0) {
                        con.commit();
                    }
                }
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "Exception in closing connection ");
                }
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e1) {
                    _logger.errorTrace(METHOD_NAME, e1);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
        }
    }

    /**
     * This method finds the list of numbers of users for whom alert is to be
     * sent to the parent.
     * 
     * @param p_con
     *            Connection
     * @return boolean
     */
    private static boolean balanceAlert(Connection p_con) {
        final String METHOD_NAME = "balanceAlert";
        if (_logger.isDebugEnabled()) {
            _logger.info(METHOD_NAME, "Entered ");
        }
        PreparedStatement pstmt = null;
        ResultSet rst = null;
        ArrayList alertList = null;
        LowBalanceAlertVO alertVO = null;
        Locale locale = null;
        int alertsSendCount = 0;
        boolean returnValue = false;
        try {
            final String defaultLanguage = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
            final String defaultCountry = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
            
            StringBuilder queryBuf = null;
            
            String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
            boolean tcpOn = false;
			Set<String> uniqueTransProfileId = new HashSet();

			if (tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
				tcpOn = true;
			}
			String sqlSelect = null;

			if (tcpOn) {


				queryBuf = new StringBuilder(" SELECT UPP.msisdn parent_msisdn, UP.msisdn, ");
				queryBuf.append(" UB.user_id, UB.product_code,UB.balance, ");
				queryBuf.append(" P.short_name,UP.country,UP.phone_language as language,U.network_code, U.parent_id  ");
				queryBuf.append(" FROM user_balances UB,user_phones UP,products P,users U, ");
				queryBuf.append(
						" transfer_profile_products TPP,channel_users CU, channel_users CU1, ");
				queryBuf.append(" transfer_profile_products CATPP,user_phones UPP ");
				queryBuf.append(
						" WHERE UP.primary_number='Y' AND UP.user_id=UB.user_id AND P.product_code=UB.product_code ");
				queryBuf.append(" AND U.status='Y' AND U.user_id=UB.user_id ");
				queryBuf.append(" AND TPP.product_code=UB.product_code ");
				queryBuf.append(" AND UB.balance<=greatest(TPP.alerting_balance,catpp.alerting_balance) ");
				//queryBuf.append(" AND TPP.profile_id=TP.profile_id AND TP.status!='N' ");
				//AND CU.transfer_profile_id=TP.profile_id
				//AND TP.category_code=CATP.category_code AND TP.network_code = CATP.network_code
				queryBuf.append(" AND UB.user_id=CU.user_id  ");
				queryBuf.append(" AND CATP.profile_id=CATPP.profile_id AND TPP.product_code=CATPP.product_code ");
				//queryBuf.append(" AND TP.category_code=CATP.category_code AND TP.network_code = CATP.network_code ");
				queryBuf.append(" AND CU.low_bal_alert_allow='Y' ");
				queryBuf.append(" AND CATP.parent_profile_id= ? AND CATP.status='Y' ");
				queryBuf.append(" AND UPP.user_id= CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END ");
				queryBuf.append(" AND CU1.user_id= UPP.user_id AND CU1.low_bal_alert_allow='Y'  ");
				if (TypesI.NO.equals(_parentAlert)) {
					queryBuf.append(" AND U.parent_id != 'ROOT' ");
				}
				queryBuf.append(" ORDER BY UPP.msisdn,UB.user_id ");
				
			} else {

				queryBuf = new StringBuilder(" SELECT UPP.msisdn parent_msisdn, UP.msisdn, ");
				queryBuf.append(" UB.user_id, UB.product_code,UB.balance, ");
				queryBuf.append(" P.short_name,UP.country,UP.phone_language as language,U.network_code, U.parent_id  ");
				queryBuf.append(" FROM user_balances UB,user_phones UP,products P,users U, ");
				queryBuf.append(
						" transfer_profile_products TPP,transfer_profile TP,channel_users CU, channel_users CU1, ");
				queryBuf.append(" transfer_profile CATP,transfer_profile_products CATPP,user_phones UPP ");
				queryBuf.append(
						" WHERE UP.primary_number='Y' AND UP.user_id=UB.user_id AND P.product_code=UB.product_code ");
				queryBuf.append(" AND U.status='Y' AND U.user_id=UB.user_id ");
				queryBuf.append(" AND TPP.product_code=UB.product_code ");
				queryBuf.append(" AND UB.balance<=greatest(TPP.alerting_balance,catpp.alerting_balance) ");
				queryBuf.append(" AND TPP.profile_id=TP.profile_id AND TP.status!='N' ");
				queryBuf.append(" AND UB.user_id=CU.user_id AND CU.transfer_profile_id=TP.profile_id ");
				queryBuf.append(" AND CATP.profile_id=CATPP.profile_id AND TPP.product_code=CATPP.product_code ");
				queryBuf.append(" AND TP.category_code=CATP.category_code AND TP.network_code = CATP.network_code ");
				queryBuf.append(" AND CU.low_bal_alert_allow='Y' ");
				queryBuf.append(" AND CATP.parent_profile_id= ? AND CATP.status='Y' ");
				queryBuf.append(" AND UPP.user_id= CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END ");
				queryBuf.append(" AND CU1.user_id= UPP.user_id AND CU1.low_bal_alert_allow='Y'  ");
				if (TypesI.NO.equals(_parentAlert)) {
					queryBuf.append(" AND U.parent_id != 'ROOT' ");
				}
				queryBuf.append(" ORDER BY UPP.msisdn,UB.user_id ");
			}
            final String query = queryBuf.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Query:" + query);
            }
            pstmt = p_con.prepareStatement(query.toString());
            pstmt.setString(1, PretupsI.PARENT_PROFILE_ID_CATEGORY);
            rst = pstmt.executeQuery();
            alertList = new ArrayList();
            while (rst.next()) {
                alertVO = new LowBalanceAlertVO();
                alertVO.setParentMsisdn(rst.getString("parent_msisdn")); // parent
                // msisdn
                alertVO.setMsisdn(rst.getString("msisdn"));
                alertVO.setUserId(rst.getString("user_id"));
                alertVO.setBalance(rst.getLong("balance"));
                alertVO.setProductCode(rst.getString("product_code"));
                alertVO.setProductShortName(rst.getString("short_name"));
                alertVO.setParentUserId(rst.getString("parent_id"));
                alertVO.setNetworkCode(rst.getString("network_code"));

                try {
                    locale = new Locale(rst.getString("language"), rst.getString("country"));
                } catch (Exception e) {
                    locale = new Locale(defaultLanguage, defaultCountry);
                    _logger.errorTrace(METHOD_NAME, e);
                }
                alertVO.setLocale(locale);
                alertList.add(alertVO);
            }
            if (alertList.size() > 0) {
                alertsSendCount = sendAlert(p_con, alertList);
            }
            if (alertsSendCount > 0) {
                try {
                    p_con.commit();
                } catch (SQLException e) {
                    p_con.rollback();
                    _logger.errorTrace(METHOD_NAME, e);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.info(METHOD_NAME, "Total alert send:" + alertsSendCount);
            }
            returnValue = true;
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Error:" + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
            returnValue = false;
        }// end of catch
        finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(METHOD_NAME, e2);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(METHOD_NAME, e3);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.info(METHOD_NAME, " Exiting");
            }
        }// end of finally
        return returnValue;
    }

    /**
     * This method sends alert message to the parent for the channel users with
     * low balance,
     * if user dont have parent or its parent is ROOT then send alert to the
     * channel user itself.
     * 
     * @param p_con
     *            Connection
     * @param p_alertList
     *            ArrayList
     * @return int
     */
    private static int sendAlert(Connection p_con, ArrayList p_alertList) {
        final String METHOD_NAME = "sendAlert";
        if (_logger.isDebugEnabled()) {
            _logger.info(METHOD_NAME, "Entered with p_arrayListSize:" + p_alertList.size());
        }
        int recordCount = 0;
        String alertSleepTime = null;
        long lAlertSleepTime = 50;
        Locale locale = null;
        final Date currentDate = new Date();
        KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
        ArrayList arrList = new ArrayList();
        final StringBuilder strBuf = new StringBuilder("INSERT INTO LOW_BALANCE_ALERT (user_id, product_code, balance, alert_send_on_date, parent_user_id, ALERT_TO) ");
        strBuf.append("VALUES (?,?,?,?,?,?)");
        final String insertQuery = strBuf.toString();
        if (_logger.isDebugEnabled()) {
            _logger.info(METHOD_NAME, "Query= " + insertQuery);
        }
        PreparedStatement insertPstmt = null;
        LowBalanceAlertVO alertVO = null;
        String userId = null;
        String msisdn = null;
        String lowBalRequestCode = null;
        try {
            alertSleepTime = Constants.getProperty("ALERT_SLEEP_TIME");
            if (BTSLUtil.isNullString(alertSleepTime) || alertSleepTime.equals("")) {
                _logger.error(METHOD_NAME, "ALERT_SLEEP_TIME is not found set default value");
                alertSleepTime = "50"; // in milliseconds
            }
            lAlertSleepTime = Integer.parseInt(alertSleepTime);
            insertPstmt = p_con.prepareStatement(insertQuery.toString());
            for (int j = 0, listSize = p_alertList.size(); j < listSize; j++) {
                alertVO = (LowBalanceAlertVO) p_alertList.get(j);
                userId = alertVO.getUserId();
                msisdn = alertVO.getMsisdn();
                locale = alertVO.getLocale();

                keyArgumentVO = new KeyArgumentVO();
                keyArgumentVO.setKey(PretupsErrorCodesI.LOW_BALANCE_ALERT_PARENT_MSG_SUBKEY);
                final String arr[] = { alertVO.getProductShortName(), PretupsBL.getDisplayAmount(alertVO.getBalance()).toString() };
                keyArgumentVO.setArguments(arr);
                arrList.add(keyArgumentVO);
                try {
                    insertPstmt.clearParameters();
                    int i = 1;
                    insertPstmt.setString(i++, userId);
                    insertPstmt.setString(i++, alertVO.getProductCode());
                    insertPstmt.setLong(i++, alertVO.getBalance());
                    insertPstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
                    /*
                     * if("ROOT".equalsIgnoreCase(alertVO.getParentUserId()))
                     * insertPstmt.setString(i++,alertVO.getUserId());
                     * else
                     */
                    insertPstmt.setString(i++, alertVO.getParentUserId());
                    if ("ROOT".equalsIgnoreCase(alertVO.getParentUserId())) {
                        insertPstmt.setString(i++, "U");
                    } else {
                        insertPstmt.setString(i++, "P");
                    }
                    insertPstmt.executeUpdate();

                } catch (Exception sqlEx) {
                    _logger.errorTrace(METHOD_NAME, sqlEx);
                }
                recordCount++;
                if (j <= (p_alertList.size() - 2) && !(msisdn.equals(((LowBalanceAlertVO) p_alertList.get(j + 1)).getMsisdn()) && alertVO.getProductCode().equals(
                    ((LowBalanceAlertVO) p_alertList.get(j + 1)).getProductCode()))) {
                    final String message[] = { alertVO.getMsisdn(), BTSLUtil.getMessage(alertVO.getLocale(), arrList) };
                    final String senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOW_BALANCE_ALERT_PARENT_MSG, message);
                    lowBalRequestCode = (String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.LOW_BAL_MSGGATEWAY, alertVO.getNetworkCode());
                    final PushMessage pushMessage = new PushMessage(alertVO.getParentMsisdn(), senderMessage, null, null, locale);
                    pushMessage.push(lowBalRequestCode, null);
                    final StringBuffer otherInfo = new StringBuffer();
                    otherInfo.append(("[PRODUCT(S) AND THEIR BALANCE(S) = " + message[1] + "] "));
                    ProcessesLog.log("LOW-BALANCE-ALERT-PARENT", alertVO.getParentMsisdn(), senderMessage, otherInfo);
                    arrList = new ArrayList();
                    try {
                        Thread.sleep(lAlertSleepTime);
                    } catch (Exception exSleep) {
                        _logger.errorTrace(METHOD_NAME, exSleep);
                    }
                } else if (j == (p_alertList.size() - 1)) {
                    final String message1[] = { alertVO.getMsisdn(), BTSLUtil.getMessage(alertVO.getLocale(), arrList) };
                    final String senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOW_BALANCE_ALERT_PARENT_MSG, message1);
                    lowBalRequestCode = (String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.LOW_BAL_MSGGATEWAY, alertVO.getNetworkCode());
                    final PushMessage pushMessage = new PushMessage(alertVO.getParentMsisdn(), senderMessage, null, null, locale);
                    pushMessage.push(lowBalRequestCode, null);
                    final StringBuffer otherInfo = new StringBuffer();
                    otherInfo.append(("[PRODUCT(S) AND THEIR BALANCE(S) = " + message1[1] + "] "));
                    ProcessesLog.log("LOW-BALANCE-ALERT-PARENT", alertVO.getParentMsisdn(), senderMessage, otherInfo);
                    arrList = new ArrayList();
                    try {
                        Thread.sleep(lAlertSleepTime);
                    } catch (Exception exSleep) {
                        _logger.errorTrace(METHOD_NAME, exSleep);
                    }
                }
            }// end of for loop
        }// end of try block
        catch (SQLException e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Error:" + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
        }// end of catch block
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Error:" + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
        }// end of catch block
        finally {
            if (insertPstmt != null) {
                try {
                    insertPstmt.close();
                } catch (SQLException e1) {
                    _logger.errorTrace(METHOD_NAME, e1);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.info(METHOD_NAME, " Exiting");
            }
        }
        return recordCount;
    }
}
