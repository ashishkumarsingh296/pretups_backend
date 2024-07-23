package com.btsl.voms.vomsprocesses.businesslogic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;
import com.btsl.voms.vomscommon.VOMSI;

/**
 * @(#)LowVoucherAlert
 *                     Copyright(c) 2007, Bharti Telesoft Ltd.
 *                     All Rights Reserved
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Ankit Singhal 20/02/2007 Initial Creation
 *                     Ankit Singhal 05/11/08 Modified
 * 
 *                     This class sends alert message to admin mobile numbers if
 *                     the numbers of vouchers for some product
 *                     goes below a minimum threshold specified with the
 *                     product.
 * */

public class LowVoucherAlert {
    private static Log _logger = LogFactory.getLog(LowVoucherAlert.class.getName());
    private static ProcessStatusVO _processStatusVO;

    /**
	 * ensures no instantiation
	 */
    private LowVoucherAlert() {
        
    }
    public static void main(String arg[]) {
        final String METHOD_NAME = "main";
        try {
            if (arg.length != 2) {
                System.out.println("Usage : LowVoucherAlert [Constants file] [LogConfig file]");
                return;
            }
            File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println("LowVoucherAlert: Constants File Not Found .............");
                return;
            }
            File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println("LowVoucherAlert: Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end of try
        catch (Exception e) {
            System.err.println("Error in Loading Configuration files ...........................: " + e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            process();
        } catch (BTSLBaseException be) {
            _logger.error("main", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("main", "Exiting..... ");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    private static void process() throws BTSLBaseException {
        final String methodName = "process";
        _logger.debug(methodName, " Entered: ");
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        int maxDoneDateUpdateCount = 0;
        KeyArgumentVO keyArgumentVO = null;
        ArrayList arrList = new ArrayList();
        ProcessBL processBL = null;
        ProcessStatusDAO processStatusDAO = null;
        ResultSet resultSet = null;
        PreparedStatement psmt = null;
        Date currentDate = null;

        try {
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LowVoucherAlert[process]", "", "", "", "DATABASE Connection is NULL");
                return;
            }
            processId = ProcessI.VOUCHER_ALERT;
            // method call to check status of the process
            processBL = new ProcessBL();
            _processStatusVO = processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            // the use of process_status table is only for running one instance,
            // executed_upto has no relevance here.
            if (statusOk) {
                con.commit();
                StringBuffer strBuff = new StringBuffer("SELECT product_name,COUNT(serial_no) available_vouchers ");
                strBuff.append(" FROM voms_vouchers VV, voms_products VP");
                strBuff.append(" WHERE VV.product_id=VP.product_id AND VV.current_status=? AND VV.expiry_date>?");
                strBuff.append(" GROUP BY VP.product_id,product_name,min_req_quantity");
                strBuff.append(" HAVING min_req_quantity>COUNT(serial_no)");

                currentDate = new Date();
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, "Query :: " + strBuff.toString());
                }

                psmt = con.prepareStatement(strBuff.toString());
                psmt.setString(1, VOMSI.VOUCHER_ENABLE);
                psmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(currentDate));
                resultSet = psmt.executeQuery();

                while (resultSet.next()) {
                    keyArgumentVO = new KeyArgumentVO();
                    keyArgumentVO.setKey(PretupsErrorCodesI.LOW_VOUCHER_ALERT_MSG_SUBKEY);
                    String arr[] = { resultSet.getString("product_name"), (new Long(resultSet.getLong("available_vouchers"))).toString() };
                    keyArgumentVO.setArguments(arr);
                    arrList.add(keyArgumentVO);
                }
                String senderMessage = null;
                Locale locale = null;
                if (arrList.size() != 0) {
                    locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                    String array = BTSLUtil.getMessage(locale, arrList);
                    String arr[] = new String[1];
                    arr[0] = array;
                    senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOW_VOUCHER_ALERT_MSG, arr);

                } else {
                    return;
                }

                String msisdnString = new String(Constants.getProperty("adminmobile"));
                String[] msisdn = msisdnString.split(",");
                for (int i = 0; i < msisdn.length; i++) {
                    PushMessage pushMessage = new PushMessage(msisdn[i], senderMessage, null, null, locale);
                    pushMessage.push();
                }

                _processStatusVO.setExecutedUpto(currentDate);
                _processStatusVO.setExecutedOn(currentDate);
                // Added By Diwakar on 19-02-2014
                processStatusDAO = new ProcessStatusDAO();
                // Ended Here
                maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, _processStatusVO);
                // if the process is successful, transaction is commit, else
                // rollback
                if (maxDoneDateUpdateCount > 0) {
                    con.commit();
                } else {
                    con.rollback();
                    throw new BTSLBaseException("LowVoucherAlert", methodName, PretupsErrorCodesI.VOMS_ALERT_COULD_NOT_UPDATE_MAX_DONE_DATE);
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LowVoucherAlert[process]", "", "", "", " LowVoucherAlert process has been executed successfully.");
            } else {
                throw new BTSLBaseException("LowVoucherAlert", methodName, PretupsErrorCodesI.VOMS_ALERT_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
            }
        } catch (BTSLBaseException be) {
            _logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            _logger.error(methodName, "Exception : " + e.getMessage());
            _logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LowVoucherAlert[process]", "", "", "", " LowVoucherAlert process could not be executed successfully.");
            throw new BTSLBaseException("LowVoucherAlert", methodName, PretupsErrorCodesI.VOMS_ALERT_ERROR_EXCEPTION,e);
        } finally {
            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk) {
                try {
                    if (markProcessStatusAsComplete(con, processId) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            _logger.errorTrace(methodName, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            _logger.errorTrace(methodName, e);
                        }
                    }
                } catch (Exception e) {
                    _logger.errorTrace(methodName, e);
                }
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(methodName, ex);
            }
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exiting..... ");
            }
        }

    }

    /**
     * This method marks the status of process as complete after successful
     * completion.
     * 
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
        Date currentDate = new Date();
        ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LowVoucherAlert[markProcessStatusAsComplete]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LowVoucherAlert", "markProcessStatusAsComplete", PretupsErrorCodesI.VOMS_ALERT_ERROR_EXCEPTION,e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }
}
