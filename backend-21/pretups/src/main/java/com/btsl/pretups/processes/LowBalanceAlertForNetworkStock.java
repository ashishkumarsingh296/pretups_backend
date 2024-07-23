package com.btsl.pretups.processes;

/**
 * @(#)LowBalanceAlertForNetworkStock.java
 *                                         Copyright(c) 2008, Bharti Telesoft
 *                                         Int. Public Ltd.
 *                                         All Rights Reserved
 *                                         This class is used to send SMS alert
 *                                         when gthe network stock is below a
 *                                         minimum threshold.
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 *                                         Author Date History
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 *                                         Ankit Singhal Dec 9,2005 Initial
 *                                         Creation
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         ----------------------
 */
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.EMailSender;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.ProcessesLog;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.MessagesCache;
import com.btsl.util.MessagesCaches;
import com.btsl.util.OracleUtil;

public class LowBalanceAlertForNetworkStock {
    private static Log _log = LogFactory.getLog(LowBalanceAlertForNetworkStock.class.getName());

    /**
     * This method loads the configuration files and calls the process() method.
     */
    
    private LowBalanceAlertForNetworkStock() {
		// TODO Auto-generated constructor stub
	}
    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        try {
            if (args.length != 2) {
                _log.info(METHOD_NAME, "Usage : LowBalanceAlertForNetworkStock [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = Constants.validateFilePath(args[0]);
            if (!constantsFile.exists()) {
                _log.info(METHOD_NAME, " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = Constants.validateFilePath(args[1]);
            if (!logconfigFile.exists()) {
                _log.info(METHOD_NAME, " Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            process();
        } catch (Exception e) {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " " + e.getMessage());
            }
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.info(METHOD_NAME, "Exiting");
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ConfigServlet.destroyProcessCache();
        }
    }// end main

    /**
     * This method which checks the network stock and send alerts.
     */
    private static void process() throws BTSLBaseException {
        Connection con = null;
        long minStockLimit = 0;
        ArrayList networkStockList = null;
        NetworkStockVO networkStockVO = null;
        String msisdnString = null;
        Locale locale = null;
        String message = null;
        String[] msisdn = null;
        int j = 0;
        StringBuffer otherInfo = null;
		String emailString=null; 
		String [] emailId=null;
		
        final String METHOD_NAME = "process";
        try {
            _log.debug(METHOD_NAME, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[process]", "", "",
                    "", "DATABASE Connection is NULL");
                return;
            }
            locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

            try {
                msisdnString = new String(Constants.getProperty("LOW_BAL_MSISDN"));
				emailString=new String(Constants.getProperty("LOW_BAL_EMAIL"));
            } catch (Exception e) {
                _log.error(METHOD_NAME, "Could not find MSISDN in the Constants file to send SMS(LOW_BAL_MSISDN). SMS alert wont be sent.");
                _log.errorTrace(METHOD_NAME, e);
            }
            if (!BTSLUtil.isNullString(msisdnString)) {
                msisdn = msisdnString.split(",");
            }
			if (!BTSLUtil.isNullString(emailString)) {
				emailId=emailString.split(",");
			}
            networkStockList = loadNetworkStockDetails(con);
            String _lowBalRequestCode = (String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.LOW_BAL_MSGGATEWAY, networkStockVO.getNetworkCode());
            String _lowBalRequestCodeDefault=(String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.DEFAULT_MESSGATEWAY,networkStockVO.getNetworkCode());
            for (int i = 0, size = networkStockList.size(); i < size; i++) {
                networkStockVO = (NetworkStockVO) networkStockList.get(i);
                minStockLimit = ((Long) (PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CIRCLEMINLMT, networkStockVO.getNetworkCode()))).longValue();

                if (minStockLimit > networkStockVO.getWalletbalance()) {
                    final String arr[] = { networkStockVO.getNetworkName(), PretupsBL.getDisplayAmount(networkStockVO.getWalletbalance()), PretupsBL
                        .getDisplayAmount(minStockLimit), networkStockVO.getProductName(), networkStockVO.getWalletType() };
                    message = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOW_STOCK_ALERT_MSG, arr);

                    if (!BTSLUtil.isNullString(msisdnString)) {
                        for (j = 0; j < msisdn.length; j++) {
                            final PushMessage pushMessage = new PushMessage(msisdn[j], message, null, null, locale);
                            try {
								pushMessage.push(_lowBalRequestCode,_lowBalRequestCodeDefault);
							} catch (Exception e) {
								 _log.errorTrace(METHOD_NAME, e);
							}
                        }
                    }
					if (!BTSLUtil.isNullString(emailString))
					{
					    for(j=0; j<emailId.length;j++)
						{
					    	sendEmailNotification(locale,emailId[j],message);
						}
					}
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LowBalanceAlertForNetworkStock[process]", "",
                        "", "", " LowBalanceAlertForNetworkStock process executed successfully. Netork stock is below minimum limit.");
                } else {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LowBalanceAlertForNetworkStock[process]", "",
                        "", "", " LowBalanceAlertForNetworkStock process executed successfully. Network stock is Ok");
                }

                otherInfo = new StringBuffer("");
                if (!BTSLUtil.isNullString(msisdnString)) {
                    ProcessesLog.log("LOW-NETWORK-STOCK-ALERT", msisdnString, message, otherInfo);
                } else {
                    ProcessesLog.log("LOW-NETWORK-STOCK-ALERT", "", message, otherInfo);
                }
            }
        }// end of try
        catch (BTSLBaseException be) {
            _log.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception : " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LowBalanceAlertForNetworkStock[process]", "", "", "",
                " LowBalanceAlertForNetworkStock process could not be executed successfully.");
            throw new BTSLBaseException("LowBalanceAlertForNetworkStock", METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, "Exception closing connection ");
                }
                _log.errorTrace(METHOD_NAME, ex);
            }
            _log.debug(METHOD_NAME, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting..... ");
            }
        }
    }

    /**
     * This method loads the network list along with their available stocks.
     * 
     * @param p_con
     *            Connection
     * @return ArrayList
     */
    private static ArrayList loadNetworkStockDetails(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadNetworkStockDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("loadNetworkStockDetails", "Entered");
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        NetworkStockVO stockVO = null;
        ArrayList networkList = null;

        strBuff.append("SELECT ns.network_code,ns.network_code_for,ns.product_code,ns.wallet_type,ns.wallet_created,ns.wallet_returned, ");
        strBuff.append("ns.wallet_balance,ns.wallet_sold,ns.last_txn_no,ns.last_txn_type,ns.last_txn_balance,ns.previous_balance, ");
        strBuff.append("ns.modified_by,ns.modified_on,ns.created_on,ns.created_by,ns.daily_stock_updated_on, ");
        strBuff.append("n.network_name,p.product_name ");
        strBuff.append("FROM NETWORK_STOCKS ns,networks n, products p ");
        strBuff.append("WHERE n.network_code=ns.network_code AND p.product_code=ns.product_code ");

        final String query = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadNetworkStockDetails", "QUERY query=" + query);
        }
        try {
            networkList = new ArrayList();
            pstmtSelect = p_con.prepareStatement(query);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                stockVO = new NetworkStockVO();
                stockVO.setNetworkCode(rs.getString("network_code"));
                stockVO.setNetworkCodeFor(rs.getString("network_code_for"));
                stockVO.setProductCode(rs.getString("product_code"));
                stockVO.setWalletType(rs.getString("wallet_type"));
                stockVO.setWalletCreated(rs.getLong("wallet_created"));
                stockVO.setWalletReturned(rs.getLong("wallet_returned"));
                stockVO.setWalletBalance(rs.getLong("wallet_balance"));
                stockVO.setWalletSold(rs.getLong("wallet_sold"));
                stockVO.setLastTxnNum(rs.getString("last_txn_no"));
                stockVO.setLastTxnType(rs.getString("last_txn_type"));
                stockVO.setLastTxnBalance(rs.getLong("last_txn_balance"));
                stockVO.setPreviousBalance(rs.getLong("previous_balance"));
                stockVO.setModifiedBy(rs.getString("modified_by"));
                stockVO.setModifiedOn(rs.getDate("modified_on"));
                stockVO.setCreatedOn(rs.getDate("created_on"));
                stockVO.setCreatedBy(rs.getString("created_by"));
                stockVO.setNetworkName(rs.getString("network_name"));
                stockVO.setProductName(rs.getString("product_name"));
                networkList.add(stockVO);
            }
        } catch (SQLException sqle) {
            _log.error("loadNetworkStockDetails", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "LowBalanceAlertForNetworkStock[loadNetworkStockDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("LowBalanceAlertForNetworkStock", "loadNetworkStockDetails", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadNetworkStockDetails", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "LowBalanceAlertForNetworkStock[loadNetworkStockDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LowBalanceAlertForNetworkStock", "loadNetworkStockDetails", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadNetworkStockDetails", "Exiting networkList size=" + networkList.size());
            }
        }
        return networkList;
    }
	private static void sendEmailNotification(Locale locale,String p_email, String p_message){
		final String METHOD_NAME = "sendEmailNotification";
    	if (_log.isDebugEnabled())
    		_log.debug(METHOD_NAME, "Entered p_email="+p_email);
		
    	try{
    		
    		if (locale == null) {
    		   _log.error("getMessage",
                    "Locale not defined considering default locale " + (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)) + " " + (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)) + "    key: ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BTSLUtil[getMessage]", "", "", " ",
                    "Locale not defined considering default locale " + (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)) + " " + (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)) + "    key: ");
                locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            }
    		MessagesCache messagesCache = MessagesCaches.get(locale);
    		if (messagesCache == null) {
                _log.error("getMessage", "Messages cache not available for locale: " + locale.getDisplayName() + "    key: ");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", " ",
                    "Messages cache not available for locale " + locale.getDisplayName() + "    key: ");
                locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                messagesCache = MessagesCaches.get(locale);
            }



    		String from=Constants.getProperty("LOW_NETWORK_STOCK_NOTIFICATION_FROM");
    		String cc=Constants.getProperty("LOW_NETWORK_STOCK_NOTIFICATION_CC");;
    		String bcc=Constants.getProperty("LOW_NETWORK_STOCK_NOTIFICATION_BCC");;
/*    		String subject=Constants.getProperty("LOW_NETWORK_STOCK_NOTIFICATION_SUBJECT");
    		
    		String header="";
    		if (!BTSLUtil.isNullString(Constants.getProperty("LOW_NETWORK_STOCK_NOTIFICATION_HEADER")))
    				header=Constants.getProperty("LOW_NETWORK_STOCK_NOTIFICATION_HEADER")+"\n";
    		String footer="";
    		if (!BTSLUtil.isNullString(Constants.getProperty("LOW_NETWORK_STOCK_NOTIFICATION_FOOTER")))
    				footer=Constants.getProperty("LOW_NETWORK_STOCK_NOTIFICATION_FOOTER")+"\n";
*/    		
    		String subject=messagesCache.getProperty("LOW_NETWORK_STOCK_NOTIFICATION_SUBJECT");
    		String header="";
    		if (!BTSLUtil.isNullString(messagesCache.getProperty("LOW_NETWORK_STOCK_NOTIFICATION_HEADER")))
    				header=messagesCache.getProperty("LOW_NETWORK_STOCK_NOTIFICATION_HEADER")+"\n";
    		String footer="";
    		if (!BTSLUtil.isNullString(messagesCache.getProperty("LOW_NETWORK_STOCK_NOTIFICATION_FOOTER")))
    				footer=messagesCache.getProperty("LOW_NETWORK_STOCK_NOTIFICATION_FOOTER")+"\n";

    		String[] split=p_message.split(":");
			String message=header + split[2] +"\n" +footer;
			
    		boolean isAttachment=false;
    		String pathofFile="";
    		String fileNameTobeDisplayed="";
    		String to =p_email;
    		if(!(to==null || "".equals(to))){
    			// Send email
    			EMailSender.sendMail(to, from, bcc, cc, subject, message, isAttachment, pathofFile, fileNameTobeDisplayed);
    		}
    	}catch(Exception e){
    		if(_log.isDebugEnabled())
    			_log.error(METHOD_NAME, " Email sending failed"+e.getMessage());
    	}
    	if (_log.isDebugEnabled())
    		_log.debug(METHOD_NAME, "Exiting ....");
    }
}