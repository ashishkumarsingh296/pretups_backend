package com.btsl.pretups.processes;

/*
 * @(#)BalanceMismatchAlert.java
 * Copyright(c) 2006, Bharti Telesoft Ltd.
 * All Rights Reserved
 * This class is used to check the authencity of channel user's balance and
 * network stock.
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Ankit Singhal 27/11/2005 Initial creation
 * Ankit Singhal 09/10/2006 Modified
 * ------------------------------------------------------------------------------
 * -------------------
 */

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Locale;

import com.btsl.db.util.QueryConstants;
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
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

/**
 * 
 */
public class BalanceMismatchAlert {
    private static ProcessBL _processBL = null;
    private static ProcessStatusVO _processStatusVO;
    private static final Log _logger = LogFactory.getLog(BalanceMismatchAlert.class.getName());

    /**
     * ensures no instantiation
     */
    private BalanceMismatchAlert(){
    	
    }
    
    /**
     * Method main.
     * 
     * @param args
     *            String[]
     */
    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        try {
            if (args.length != 2) {
                System.out.println("Usage : BalanceMismatchAlert [Constants file] [LogConfig file]");
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
        }// end try
        catch (Exception ex) {
            _logger.error(METHOD_NAME, "Error in Loading Configuration files ...........................: " + ex);
            _logger.errorTrace(METHOD_NAME, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }// end catch

        try {
            final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            networkBalanceMismatchAlert(locale);
            Thread.sleep(1000);
            channelBalanceMismatchAlert(locale);
        } catch (Exception e) {
            _logger.error("main", " " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
        }// end catch
        finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("main", " Exiting");
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
     * Method networkBalanceMismatchAlert.
     * 
     * @param p_locale
     *            Locale
     */
    public static void networkBalanceMismatchAlert(Locale p_locale) {
        final String METHOD_NAME = "networkBalanceMismatchAlert";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered with p_locale:" + p_locale);
        }
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        Date processedUpto = null;
        Date currentDateTime = null;
         String errorCode=null;
  	    String message=null; 
        CallableStatement cstmt = null;
        CallableStatement cstmt1 = null;
        CallableStatement cstmt2 = null;
        ResultSet rs = null;
        try {
            final Calendar cal = BTSLDateUtil.getInstance();
            currentDateTime = cal.getTime(); // Current Date
            // currentDateTimeStr=BTSLUtil.getDateStringFromDate(currentDateTime);//commented
            // by deepika aggarwal while pretups 6.0 code optimisation

            // Getting database connection
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "Not able to get Connection for ChannelBalanceMismatchAlert: ");
                }
                throw new SQLException();
            }
            processId = ProcessI.NW_STK_MISMATCH;
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            if (statusOk) {
                con.commit();
                processedUpto = _processStatusVO.getExecutedUpto();
            }

         
            String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                cstmt = con.prepareCall("{call " + Constants.getProperty("currentschema") + ".PKG_BALANCE_MISMATCH.sp_net_stocks_balance_mismatch(?,?,?)}");
                cstmt.setDate(3, BTSLUtil.getSQLDateFromUtilDate(processedUpto)); // Date
                cstmt.registerOutParameter(1, Types.VARCHAR); // errorcode
                 cstmt.registerOutParameter(2, Types.VARCHAR); // message
                 cstmt.executeUpdate();
                  errorCode = cstmt.getString(1);
                  message = cstmt.getString(2);
            }
            else if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                cstmt1 = con.prepareCall("{call sp_net_stocks_balance_mismatch(?)}");
                cstmt1.setDate(1, BTSLUtil.getSQLDateFromUtilDate(processedUpto));
                rs = cstmt1.executeQuery();
                if(rs.next()){
                 errorCode =rs.getString("v_errorcode");
                 message = rs.getString("v_message");
                 }
            }
            else {
                cstmt2 = con.prepareCall("{call PKG_BALANCE_MISMATCH.sp_net_stocks_balance_mismatch(?,?,?)}");
                cstmt2.setDate(3, BTSLUtil.getSQLDateFromUtilDate(processedUpto)); // Date
                cstmt2.registerOutParameter(1, Types.VARCHAR); // errorcode
                 cstmt2.registerOutParameter(2, Types.VARCHAR); // message
                 cstmt2.executeUpdate();
                errorCode = cstmt2.getString(1);
                 message = cstmt2.getString(2);
            }
           
         
            // till
            // which
            // no
            // mismatch
            // was
            // found
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Before calling procedure");
            }
           

            String balanceMismatchMessage = null;
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "After calling procedure, Error code:" + errorCode + " Message:" + message);
            }

            ProcessesLog.balanceMismatchLog("NETWORK STOCK MISMATCH with error code:" + errorCode, message);

            if (PretupsErrorCodesI.NETWORK_STOCK_MISMATCH_SUCCESS.equals(errorCode)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "BalanceMismatchAlert[networkBalanceMismatchAlert]",
                    "", "No missmatch found, Message:" + message, "", "");
                // if the process is executed successfully and no mismatch is
                // found
                // then there will not be any mismatch till current date
                // so setting the current date as excuted upto date
                _processStatusVO.setExecutedUpto(currentDateTime);               
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                    "BalanceMismatchAlert[networkBalanceMismatchAlert]", "", " Message:" + message, "", "");
            }

            _processStatusVO.setExecutedOn(currentDateTime);
            balanceMismatchMessage = BTSLUtil.getMessage(p_locale, errorCode, null);
            final String msisdnString = Constants.getProperty("adminmobile");
            final String[] msisdn = msisdnString.split(",");
            PushMessage pushMessage = null;
            for (int i = 0; i < msisdn.length; i++) {
                try {
					pushMessage = new PushMessage(msisdn[i], balanceMismatchMessage, null, null, p_locale);
					pushMessage.push();
				} catch (Exception e) {
					 _logger.errorTrace(METHOD_NAME,e);
				}
            }
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Error:" + e.getMessage());
            ProcessesLog.balanceMismatchLog("NETWORK STOCK MISMATCH", "ERROR ENCOUNTERED WHILE EXECUTION");
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            try {
                if (statusOk) {
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
                }
                try {
                    if (cstmt != null) {
                        cstmt.close();
                    }
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                }
                try {
                    if (cstmt1 != null) {
                        cstmt1.close();
                    }
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                }
                try {
                    if (cstmt2 != null) {
                        cstmt2.close();
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
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                }
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "Exception while closing statement in NetworkBalanceMismatchAlert method ");
                }
                _logger.errorTrace(METHOD_NAME, ex);
            }
        }
    }

    /**
     * Method channelBalanceMismatchAlert.
     * 
     * @param p_locale
     *            Locale
     */
    public static void channelBalanceMismatchAlert(Locale p_locale) {
        final String METHOD_NAME = "channelBalanceMismatchAlert";
        if (_logger.isDebugEnabled()) {
            _logger.debug("channelBalanceMismatchAlert", "Entered with p_locale:" + p_locale);
        }
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        Date processedUpto = null;
        Date currentDateTime = null;
        String message=null;
        String errorCode=null;
        
        
        CallableStatement cstmt = null;
        CallableStatement cstmt1 = null;
        CallableStatement cstmt2 = null;
        ResultSet rs = null;
        try {
            final Calendar cal = Calendar.getInstance();
            currentDateTime = cal.getTime(); // Current Date
            // currentDateTimeStr=BTSLUtil.getDateStringFromDate(currentDateTime);//commented
            // by deepika aggarwal while pretups 6.0 code optimisation
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("channelBalanceMismatchAlert", "Not able to get Connection for ChannelBalanceMismatchAlert: ");
                }
                throw new SQLException();
            }
            processId = ProcessI.CHNL_USR_BAL_MISMATCH;
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            if (statusOk) {
                con.commit();
                processedUpto = _processStatusVO.getExecutedUpto();
            }
            
            String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                cstmt = con.prepareCall("{call " + Constants.getProperty("currentschema") + ".PKG_BALANCE_MISMATCH.sp_system_usr_bal_mismatch(?,?,?,?)}");
                cstmt.registerOutParameter(1, Types.VARCHAR); // error code
                cstmt.registerOutParameter(2, Types.VARCHAR); // mismatch string
                cstmt.setDate(3, BTSLUtil.getSQLDateFromUtilDate(processedUpto)); // Date String till which no mismatch was found
                cstmt.registerOutParameter(4, Types.NUMERIC); // mismatch amount
                cstmt.executeUpdate();
                errorCode = cstmt.getString(1);
                message = cstmt.getString(2);
            } 
            else if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
            	cstmt1 = con.prepareCall(" {call sp_system_usr_bal_mismatch(?)}");
                cstmt1.setDate(1, BTSLUtil.getSQLDateFromUtilDate(processedUpto));
                 rs = cstmt1.executeQuery();
                if(rs.next()){
                   errorCode = rs.getString("v_errorcode");
                   message = rs.getString("v_message");
                 }
            }else {
                cstmt2 = con.prepareCall("{call  PKG_BALANCE_MISMATCH.sp_system_usr_bal_mismatch(?,?,?,?)}");
                cstmt2.registerOutParameter(1, Types.VARCHAR); // error code
                cstmt2.registerOutParameter(2, Types.VARCHAR); // mismatch string
                cstmt2.setDate(3, BTSLUtil.getSQLDateFromUtilDate(processedUpto)); // Date String till which no mismatch was found
                cstmt2.registerOutParameter(4, Types.NUMERIC); // mismatch amount
                cstmt2.executeUpdate();
                errorCode = cstmt2.getString(1);
                message = cstmt2.getString(2);
            }
         
            if (_logger.isDebugEnabled()) {
                _logger.debug("channelBalanceMismatchAlert", "Before calling procedure");
            }
      
            String balanceMismatchMessage = null;
            if (_logger.isDebugEnabled()) {
                _logger.debug("channelBalanceMismatchAlert", "After calling procedure, Error code:" + errorCode + " Message=" + message);
            }

            ProcessesLog.balanceMismatchLog("USER BALANCE MISMATCH with error code:" + errorCode, message);

            if (PretupsErrorCodesI.CHNL_BALANCE_MISMATCH_SUCCESS.equals(errorCode)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "BalanceMismatchAlert[channelBalanceMismatchAlert]",
                    "", "No missmatch found, Message:" + message, "", "");
                // if the process is executed successfully and no mismatch is
                // found
                // then there will not be any mismatch till current date
                // so setting the current date as excuted upto date
                _processStatusVO.setExecutedUpto(currentDateTime);
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                    "BalanceMismatchAlert[channelBalanceMismatchAlert]", "", " Message:" + message, "", "");
            }

            _processStatusVO.setExecutedOn(currentDateTime);
            balanceMismatchMessage = BTSLUtil.getMessage(p_locale, errorCode, null);
            final String msisdnString = Constants.getProperty("adminmobile");
            final String[] msisdn = msisdnString.split(",");
            PushMessage pushMessage = null;
            for (int i = 0; i < msisdn.length; i++) {
                try {
                	pushMessage = new PushMessage(msisdn[i], balanceMismatchMessage, null, null, p_locale);
                    pushMessage.push();
				} catch (Exception e) {
					_logger.errorTrace(METHOD_NAME, e);
				}
            }
        } catch (Exception e) {
            _logger.error("channelBalanceMismatchAlert", "Error:" + e.getMessage());
            ProcessesLog.balanceMismatchLog("USER BALANCE MISMATCH", "ERROR ENCOUNTERED WHILE EXECUTION");
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            try {
                if (statusOk) {
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
                }
                try {
                    if (cstmt != null) {
                        cstmt.close();
                    }
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                }
                try {
                    if (cstmt1 != null) {
                        cstmt1.close();
                    }
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                }
                try {
                    if (cstmt2 != null) {
                        cstmt2.close();
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
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                }
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("channelBalanceMismatchAlert", "Exception while closing statement in ChannelBalanceMismatchAlert method ");
                }
                _logger.errorTrace(METHOD_NAME, ex);
            }
        }
    }

    /**
     * @param p_con
     *            Connection
     * @param p_processId
     *            String
     * @return int
     */
    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) {
        final String METHOD_NAME = "markProcessStatusAsComplete";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered:  p_processId:" + p_processId);
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
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exception= " + e.getMessage());
            }
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;

    }
}