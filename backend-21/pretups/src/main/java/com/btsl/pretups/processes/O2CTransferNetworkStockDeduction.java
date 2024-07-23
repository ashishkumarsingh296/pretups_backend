package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
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
import com.btsl.pretups.networkstock.businesslogic.NetworkStockBL;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockDAO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

/**
 * @author satakshi.gaur
 *
 */
public class O2CTransferNetworkStockDeduction {
    private static Log log = LogFactory.getLog(O2CTransferNetworkStockDeduction.class.getName());
    private static ProcessStatusVO processStatusVO;
    private static ProcessBL processBL = null;
    private static final  String BTSLBASEEXCEPTION = "BTSLBaseException : ";
    private static final  String CLASSNAME = "O2CTransferNetworkStockDeduction";
    private static final String EXCEPTION = "Exception : ";
    private static final String NETWORKCODE = "networkCode";
    private static final String NETWORKCODEFOR = "networkCodeFor";
    private static final String PRODUCTCODE = "productCode";
    private static final String TRANSFER = "TRANSFER";
    private static final String SYSTEM = "SYSTEM";
    private static final String INITIATER_REMARKS = "CREATED BY SAP SYSTEM";
    /**
     * to ensure no class instantiation 
     */
    private O2CTransferNetworkStockDeduction(){
    	
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
    	final String methodName = "main";
        try {
            if (args.length != 2) {
                log.error(methodName, "Usage : O2CTransferNetworkStockDeduction [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
            	log.error(methodName, " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                log.error(methodName, " Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end try
        catch (Exception ex) {
            log.error(methodName, "Error in Loading Configuration files ...........................: ");
            log.errorTrace("main", ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            process();
        } catch (BTSLBaseException be) {
            log.error("main", BTSLBASEEXCEPTION + be.getMessage());
            log.errorTrace("main", be);
        } finally {
        	LogFactory.printLog(methodName, PretupsI.EXITED , log);
            ConfigServlet.destroyProcessCache();
        }
    }

    private static void process() throws BTSLBaseException {
        Date processedUpto = null;
        Date currentDate = null;
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        int beforeInterval = 0;
        final String methodName = "process";
        try {
            log.debug(methodName, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = new Date();
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);

            con = OracleUtil.getSingleConnection();
            if (con == null) {
            	LogFactory.printLog(methodName, " DATABASE Connection is NULL " , log);
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "O2CTransferNetworkStockDeduction [process]", "", "", "", "DATABASE Connection is NULL");
                return;
            }
            processId = ProcessI.O2CTRFDDT_PROCESS;
            processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(con, processId);
            statusOk = processStatusVO.isStatusOkBool();
            beforeInterval = BTSLUtil.parseLongToInt( processStatusVO.getBeforeInterval() / (60 * 24));
            if (statusOk) {
                con.commit();
                processedUpto = processStatusVO.getExecutedUpto();
                executeTasks(processedUpto, currentDate, con, beforeInterval);
            }
        }// end of try
        catch (BTSLBaseException be) {
            log.error(methodName, BTSLBASEEXCEPTION + be.getMessage());
            log.errorTrace(methodName, be);
        } catch (Exception e) {
        	log.error(methodName, EXCEPTION + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "O2CTransferNetworkStockDeduction[process]", "", "", "",
                " O2CTransferNetworkStockDeduction process could not be executed successfully.");
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.O2CTRFDDT_ERROR_EXCEPTION);
        } finally {
        	finallyExecuteThis(statusOk, con, processId);
            
        }
    }

    private static void finallyExecuteThis(boolean statusOk, Connection con, String processId) {
    	final String methodName = "finallyExecuteThis";
        if (statusOk) {
            try {
            	commmitOrRollback(con, processId);
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (con != null) {
                    OracleUtil.closeQuietly(con);
                }
            } catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Exception closing connection ");
                }
                log.errorTrace(methodName, ex);
            }
        }
        log.debug(methodName, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting..... ");
        }
    
		
	}

	private static void commmitOrRollback(Connection con, String processId) throws BTSLBaseException {
    	final String methodName = "commmitOrRollback";
        if (markProcessStatusAsComplete(con, processId) == 1) {
            try {
                con.commit();
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
        } else {
            try {
                con.rollback();
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
        }
    		
	}

	/**
	 * @param processedUpto
	 * @param currentDate
	 * @param con
	 * @param beforeInterval
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	private static void executeTasks(Date processedUpto, Date currentDate, Connection con, int beforeInterval) throws BTSLBaseException, SQLException, InterruptedException
    {
    	final String methodName = "executeTasks";
    	Date dateCount;
    	boolean isDaySuccess;
    	Date processedUptoNew;
        if (processedUpto != null) {
            if (processedUpto.compareTo(currentDate) == 0) {
                throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.O2CTRFDDT_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
            }
            processedUptoNew = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
            processStatusVO.setStartDate(currentDate);
            for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUptoNew); dateCount.before(BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval)); dateCount = BTSLUtil
            		.addDaysInUtilDate(dateCount, 1)) {

            	isDaySuccess = getAndProcessO2CTransferData(con, dateCount);
            	updateProcessDetail(con, isDaySuccess,dateCount, currentDate );
            	Thread.sleep(500);
            }// end date loop
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "O2CTransferNetworkStockDeduction[process]", "", "",
                "", " O2CTransferNetworkStockDeduction process has been executed successfully.");
        } else {
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.O2CTRFDDT_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
        }
    
    }
    private static void updateProcessDetail(Connection con, boolean isDaySuccess, Date dateCount, Date currentDate) throws BTSLBaseException, SQLException{
    	
    	ProcessStatusDAO processStatusDAO;
    	int maxDoneDateUpdateCount;
    	if (isDaySuccess) {
    		processStatusVO.setExecutedUpto(dateCount);
    		processStatusVO.setExecutedOn(currentDate);
    		processStatusDAO = new ProcessStatusDAO();
    		maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, processStatusVO);
    		if (maxDoneDateUpdateCount > 0) {
    			con.commit();
    		} else {
    			con.rollback();
    			throw new BTSLBaseException(CLASSNAME, "updateProcessDetail", PretupsErrorCodesI.O2CTRFDDT_COULD_NOT_UPDATE_MAX_DONE_DATE);
    		}
    	} else {
    		con.rollback();
    	}
    }
	
    /**
     * @param con
     *            Connection
     * @param processId
     *            String
     * @throws BTSLBaseException
     * @return int
     */
    private static int markProcessStatusAsComplete(Connection con, String processId) throws BTSLBaseException {
        final String methodName = "markProcessStatusAsComplete";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered:  processId:" + processId);
        }
        int updateCount = 0;
        final Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        processStatusVO.setProcessID(processId);
        processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(con, processStatusVO);
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error(methodName, "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "O2CTransferNetworkStockDeduction[markProcessStatusAsComplete]", "", "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.O2CTRFDDT_ERROR_EXCEPTION);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }


    /**
     * @param con
     * @param beingProcessedDate
     * @return
     * @throws BTSLBaseException
     */
    private static boolean getAndProcessO2CTransferData(Connection con, Date beingProcessedDate) throws BTSLBaseException {
        final String methodName = "getAndProcessO2CTransferData";
        LogFactory.printLog(methodName," Entered: beingProcessedDate=" + beingProcessedDate, log);
        boolean isSuccess;
        

        final StringBuilder o2cTrfSelectQueryBuf = new StringBuilder();
        o2cTrfSelectQueryBuf.append(" SELECT CT.network_code, CT.network_code_for, CTI.product_code, CT.type, CT.transfer_type, CT.transfer_sub_type,CT.dual_comm_type");
        o2cTrfSelectQueryBuf.append(" SUM(CASE WHEN CTI.approved_quantity > 0 THEN CTI.approved_quantity ELSE 0 END) trf_amount,");
        o2cTrfSelectQueryBuf.append(" SUM(CASE WHEN CTI.COMMISION_QUANTITY > 0 THEN CTI.COMMISION_QUANTITY ELSE 0 END) comm_amount, CT.txn_wallet");
        o2cTrfSelectQueryBuf.append(" FROM channel_transfers CT, channel_transfers_items CTI ");
        o2cTrfSelectQueryBuf.append(" WHERE CT.transfer_date=? AND CT.transfer_id=CTI.transfer_id AND CT.STOCK_UPDATED='N' AND CT.type='O2C'");
        o2cTrfSelectQueryBuf.append(" AND CT.status='CLOSE' AND CT.transfer_type='TRANSFER' AND  CT.transfer_sub_type='T'");
        o2cTrfSelectQueryBuf.append(" GROUP BY CT.network_code, CT.network_code_for, CTI.product_code, CT.type, CT.transfer_type, CT.transfer_sub_type, CT.txn_wallet ");

        final String o2cTrfSelectQuery = o2cTrfSelectQueryBuf.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select query:" + o2cTrfSelectQuery);
        }
        isSuccess = processO2CTransferData(con, o2cTrfSelectQuery,beingProcessedDate);
        return isSuccess;
    }

    /**
     * @param con
     * @param o2cTrfSelectQuery
     * @param beingProcessedDate
     * @return
     * @throws BTSLBaseException
     */
    @SuppressWarnings("resource")
	private static boolean processO2CTransferData(Connection con, String o2cTrfSelectQuery, Date beingProcessedDate)throws BTSLBaseException
    {

    	final String methodName = "processO2CTransferData";
    	NetworkStockTxnVO debitNetworkStockTxnVO = null;
        Date currentDate = null;
        String ntwrkCode = null;
        String prdctCode = null;
        String ntwrkCodeFor = null;
    	String type = null;
    	String transferType = null;
    	String transferSubType = null;
    	String txn_wallet = null;
    	long trfAmount = 0;
        long commAmount = 0;
        long totalTrfAmount = 0;
        long totalCommAmount = 0;
        boolean isSuccess = true;
        int updateCount = 0;
        ResultSet o2cTrfDataRst = null;
        PreparedStatement o2cSelectPstmt = null;
    	String walletType = null;
    	String txnId;
    	Map<String, String> data = new HashMap<>();
    	Map<String, Long> amounts = new HashMap<>();
    	
    	try{
            currentDate = new Date();
        	walletType = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue() ? PretupsI.INCENTIVE_WALLET_TYPE : PretupsI.SALE_WALLET_TYPE;

            o2cSelectPstmt = con.prepareStatement(o2cTrfSelectQuery);
            o2cSelectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(beingProcessedDate));
            o2cTrfDataRst = o2cSelectPstmt.executeQuery();

            while (o2cTrfDataRst.next()) {
            	ntwrkCode = o2cTrfDataRst.getString("network_code");
            	ntwrkCodeFor = o2cTrfDataRst.getString("network_code_for");
            	prdctCode = o2cTrfDataRst.getString("product_code");
            	type = o2cTrfDataRst.getString("type");
            	transferType = o2cTrfDataRst.getString("transfer_sub_type");
            	transferSubType = o2cTrfDataRst.getString("transfer_type");
            	trfAmount = o2cTrfDataRst.getLong("trf_amount");
            	commAmount = o2cTrfDataRst.getLong("comm_amount");
            	txn_wallet = o2cTrfDataRst.getString("txn_wallet");
            	log.debug("Wallet Type============", txn_wallet);
            	totalTrfAmount += trfAmount;
            	totalCommAmount += commAmount;
            	data.put(NETWORKCODE, ntwrkCode);
            	data.put(NETWORKCODEFOR, ntwrkCodeFor);
            	data.put(PRODUCTCODE, prdctCode);
            	data.put("type", type);
            	data.put("transferType", transferType);
            	data.put("transferSubType", transferSubType);
            	amounts.put("trfAmount", trfAmount);
            	amounts.put("commAmount", commAmount);
            	
            	log.debug(methodName,"networkCode:" + ntwrkCode + " network Code For:" + ntwrkCodeFor + " product Code:" + prdctCode + " type:" + type + " transferType:" + transferType + " transferSubType:" + transferSubType + ",trf_amount:" +trfAmount + ",comm_amount:" + commAmount);

            	debitNetworkStockTxnVO = new NetworkStockTxnVO();
            	debitNetworkStockTxnVO.setCreatedOn(currentDate);
            	debitNetworkStockTxnVO.setNetworkCode(ntwrkCode);
            	debitNetworkStockTxnVO.setDualCommissionType(o2cTrfDataRst.getString("dual_comm_type"));
            	txnId = NetworkStockBL.genrateStockTransctionID(debitNetworkStockTxnVO);
            	data.put(TRANSFER, PretupsI.NETWORK_STOCK_TRANSACTION_TRANSFER);
            	
            	log.debug("Calling for Wallet Type============", txn_wallet);
            	updateCount = prepareNetworkStockListAndCreditDebitStock(con, data, txn_wallet, beingProcessedDate, txnId, trfAmount, true);
            	if (updateCount <= 0) {
            		throw new BTSLBaseException(CLASSNAME, methodName,PretupsErrorCodesI.O2CTRFDDT_ERROR_EXCEPTION1);
            	} else {
            		updateCount = updateNetworkStock(con, data, beingProcessedDate, amounts,  debitNetworkStockTxnVO, walletType,  txnId, txn_wallet);
            		
            	}
            	
            	if (updateCount < 0) {
            		throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.O2CTRFDDT_ERROR_EXCEPTION2);
            	} else {
            		updateCount = updateO2CTransferData(con, beingProcessedDate, type, true, ntwrkCode, ntwrkCodeFor, txn_wallet);
            	}

            	if (updateCount < 0) {
            		throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.O2CTRFDDT_ERROR_EXCEPTION3);
            	}
            	con.commit();
            	sendMessageDatewise(ntwrkCode,prdctCode, walletType, trfAmount, commAmount, beingProcessedDate.toString() );
            }// end while loop
            
        }
        catch (SQLException sqe) {
            isSuccess = false;
            log.error(methodName, "SQLException " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "O2CTransferNetworkStockDeduction [getAndProcessO2CTransferData]", "", "", "", "SQLException: " + sqe.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.O2CTRFDDT_ERROR_EXCEPTION);
        }// end of catch
        catch (BTSLBaseException be) {
            isSuccess = false;
            log.error(methodName, EXCEPTION + be.getMessage());
            log.errorTrace(methodName, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "O2CTransferNetworkStockDeduction[getAndProcessO2CTransferData]", "", "", "", "BTSLBaseException: " + be.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.O2CTRFDDT_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            isSuccess = false;
            log.error(methodName, EXCEPTION + ex.getMessage());
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "O2CTransferNetworkStockDeduction[getAndProcessO2CTransferData]", "", "", "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.O2CTRFDDT_ERROR_EXCEPTION);
        }
    	finally {
            try{
                if (o2cSelectPstmt!= null){
                	o2cSelectPstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
            try{
                if (o2cTrfDataRst!= null){
                	o2cTrfDataRst.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
            LogFactory.printLog(methodName, PretupsI.EXITED , log);
        }// end of finally
        
    return isSuccess;
    }
   
    /**
     * @param ntwrkCode
     * @param prdctCode
     * @param walletType
     * @param trfAmount
     * @param commAmount
     * @param beingProcessedDate
     */
    private static void sendMessageDatewise(String ntwrkCode, String prdctCode,
			String walletType, long trfAmount, long commAmount, String beingProcessedDate) {


    	PushMessage pushMessage = null;
        final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
        final String msisdnString = new String(Constants.getProperty("adminmobile"));
        final String[] msisdn = msisdnString.split(",");
        final BTSLMessages trfMessage = new BTSLMessages(PretupsErrorCodesI.NTWRK_STOCK_DEDUCTION_TRF_AMOUNT,new String[]{PretupsBL.getDisplayAmount(trfAmount), ntwrkCode, prdctCode, PretupsI.SALE_WALLET_TYPE, beingProcessedDate});
        BTSLMessages commMessage = null;
        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue())
        	commMessage = new BTSLMessages(PretupsErrorCodesI.NTWRK_STOCK_DEDUCTION_COMSN_AMOUNT,new String[]{PretupsBL.getDisplayAmount(commAmount), ntwrkCode, prdctCode, walletType, beingProcessedDate});
        for (int i = 0; i < msisdn.length; i++) {
            pushMessage = new PushMessage(msisdn[i], trfMessage, "", null, locale,ntwrkCode);
            pushMessage.push();
            if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()){
            	pushMessage = new PushMessage(msisdn[i], commMessage, "", null, locale,ntwrkCode);
            	pushMessage.push();
            }
        }		
	
	}

	/**
     * @param con
     * @param data
     * @param beingProcessedDate
     * @param trfAmount
     * @param commAmount
     * @param debitNetworkStockTxnVO
     * @param walletType
     * @return
     * @throws BTSLBaseException
     */
    private static int updateNetworkStock(Connection con, Map data, Date beingProcessedDate, Map amounts,  NetworkStockTxnVO debitNetworkStockTxnVO, String walletType, String txnId, String txn_wallet) throws BTSLBaseException {
    	String txnId1;
    	final String methodName = "updateNetworkStock";
    	int updateCount ;
    	long trfAmount ;
        long commAmount ;
        trfAmount = (long) amounts.get("trfAmount");
        commAmount = (long) amounts.get("commAmount");
		updateCount = updateNetworkStockTransactionDetails(con,data, txn_wallet, beingProcessedDate, txnId, trfAmount);
		if (updateCount <= 0) {
    		throw new BTSLBaseException(CLASSNAME, methodName, "PretupsErrorCodesI.error_code");
    	} else {
            if (PretupsI.COMM_TYPE_POSITIVE.equals(debitNetworkStockTxnVO.getDualCommissionType()) && commAmount > 0) {
            	txnId1 = NetworkStockBL.genrateStockTransctionID(debitNetworkStockTxnVO);
            	updateCount = prepareNetworkStockListAndCreditDebitStock(con,data,  walletType, beingProcessedDate,txnId1, commAmount, true);
            	if (updateCount <= 0) {
            		throw new BTSLBaseException(CLASSNAME, methodName, "PretupsErrorCodesI.error_code");
            	} else {
            		updateCount = updateNetworkStockTransactionDetails(con, data, walletType, beingProcessedDate, txnId1, commAmount);
            	}
            }
    	}
	
    	return updateCount;
	}

	
	/**
	 * @param con
	 * @param beingProcessedDate
	 * @param serviceType
	 * @param isPositive
	 * @param networkCode
	 * @param networkCodeFor
	 * @return
	 * @throws BTSLBaseException
	 */
	private static int updateO2CTransferData(Connection con, Date beingProcessedDate, String serviceType, boolean isPositive, String networkCode, String networkCodeFor, String txn_wallet) throws BTSLBaseException {
        final String methodName = "updateO2CTransferData";
        if (log.isDebugEnabled()) {
            log.debug(methodName,
                " Entered: beingProcessedDate:" + beingProcessedDate + " ServiceType:" + serviceType + " isPositive:" + isPositive + "networkCode:" + networkCode + "Wallet_Type:" + txn_wallet);
        }

        int updateCount = 0;
        final StringBuilder updateQueryBuf = new StringBuilder();
        updateQueryBuf.append(" UPDATE channel_transfers SET stock_updated=? WHERE transfer_date=?");
        updateQueryBuf.append(" AND network_code=? AND network_code_for=? AND type='O2C' AND stock_updated='N' AND status='CLOSE' AND txn_wallet = ?");

        final String adjUpdateQuery = updateQueryBuf.toString();
        if (log.isDebugEnabled()) {
            log.debug("updateO2CTransferData", "Update query:" + adjUpdateQuery);
        }

        PreparedStatement updatePstmt = null;

        try {
            int i = 0;
            updatePstmt = con.prepareStatement(adjUpdateQuery);
            updatePstmt.setString(++i, PretupsI.YES);
            updatePstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(beingProcessedDate));
            updatePstmt.setString(++i, networkCode);
            updatePstmt.setString(++i, networkCodeFor);
            updatePstmt.setString(++i, txn_wallet);
            updateCount = updatePstmt.executeUpdate();
            
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CTransferNetworkStockDeduction[updateO2CTransferData]",
                "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(CLASSNAME, "fetchChannelTransactionData", PretupsErrorCodesI.O2CTRFDDT_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            log.error(methodName, EXCEPTION + ex.getMessage());
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CTransferNetworkStockDeduction[updateO2CTransferData]",
                "", "", "", " Exception: " + ex.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.O2CTRFDDT_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (updatePstmt != null) {
                try {
                    updatePstmt.close();
                } catch (Exception ex) {
                    log.errorTrace(methodName, ex);
                }
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting ");
            }
        }// end of finally
        return updateCount;
    }

    
    /**
     * @param con
     * @param data
     * @param walletType
     * @param beingProcessedDate
     * @param txnId
     * @param stock
     * @param isDebit
     * @return
     * @throws BTSLBaseException
     */
    public static int prepareNetworkStockListAndCreditDebitStock(Connection con, Map data,String walletType, Date beingProcessedDate, String txnId, long stock, boolean isDebit) throws BTSLBaseException {
        final String methodName = "prepareNetworkStockListAndCreditDebitStock";
        if (log.isDebugEnabled()) {
            log
                .debug(
                    methodName,
                    "Entered networkCode:" + data.get(NETWORKCODE).toString() + " networkCodeFor:" + data.get(NETWORKCODEFOR).toString() + " walletType:" + walletType + " productCode:" + data.get(PRODUCTCODE).toString() + " serviceType:" + data.get("type").toString() + " txn_id:" + txnId + " beingProcessedDate:" + beingProcessedDate + " txnType:" + data.get(TRANSFER).toString() + " stock:" + stock + " isDebit  " + isDebit);
        }

        int updateCount = 0;
        NetworkStockDAO networkStockDAO = null;
        ArrayList networkStockList = null;
        NetworkStockVO networkStocksVO = null;
        try {
            networkStockDAO = new NetworkStockDAO();
            networkStockList = new ArrayList();
            networkStocksVO = new NetworkStockVO();
            networkStocksVO.setNetworkCode(data.get(NETWORKCODE).toString());
            networkStocksVO.setNetworkCodeFor(data.get(NETWORKCODEFOR).toString());
            networkStocksVO.setProductCode(data.get(PRODUCTCODE).toString());
            networkStocksVO.setLastTxnNum(txnId);
            networkStocksVO.setLastTxnType(data.get(TRANSFER).toString());
            networkStocksVO.setLastTxnBalance(stock);
            networkStocksVO.setWalletBalance(stock);
            networkStocksVO.setModifiedBy(SYSTEM);
            networkStocksVO.setModifiedOn(beingProcessedDate);
            networkStocksVO.setWalletType(walletType);
            networkStockList.add(networkStocksVO);
            if((boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.AUTO_NWSTK_CRTN_ALWD, networkStocksVO.getNetworkCode())){
                new com.btsl.pretups.channel.transfer.businesslogic.AutoNetworkStockBL().networkStockThresholdValidation(networkStocksVO);

            }
            updateCount = networkStockDAO.updateNetworkDailyStock(con, networkStocksVO);

            if (isDebit) {
                updateCount = networkStockDAO.debitNetworkStock(con, networkStockList, true);
            } else {
                updateCount = networkStockDAO.creditNetworkStock(con, networkStockList, true);
            }
        } catch (BTSLBaseException be) {
            log.error(methodName, BTSLBASEEXCEPTION + be.getMessage());
            log.errorTrace(methodName, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "O2CTransferNetworkStockDeduction[prepareNetworkStockListAndCreditDebitStock]", "", "", "", "BTSLBaseException:" + be.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.O2CTRFDDT_ERROR_EXCEPTION);
        } catch (Exception e) {
            log.error("checkUnderprocessTransaction", BTSLBASEEXCEPTION + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "O2CTransferNetworkStockDeduction[prepareNetworkStockListAndCreditDebitStock]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.O2CTRFDDT_ERROR_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited  updateCount " + updateCount);
        }
        return updateCount;
    }

    /**
     * @param con
     * @param data
     * @param walletType
     * @param beingProcessedDate
     * @param txnId
     * @param stock
     * @return
     * @throws BTSLBaseException
     */
    public static int updateNetworkStockTransactionDetails(Connection con,  Map data,String walletType, Date beingProcessedDate,String txnId, long stock) throws BTSLBaseException {
        final String methodName = "updateNetworkStockTransactionDetails";
        if (log.isDebugEnabled()) {
            log
                .debug(
                    methodName,
                    "Entered networkCode:" + data.get(NETWORKCODE).toString() + " networkCodeFor:" + data.get(NETWORKCODEFOR).toString() + " walletType:" + walletType + " productCode:" + data.get(PRODUCTCODE).toString() + " serviceType:" + data.get("type").toString() + " txnId:" + txnId + " beingProcessedDate:" + beingProcessedDate + " txnType:" + data.get(TRANSFER).toString() + " stock:" + stock);
        }
        int updateCount = 0;
        NetworkStockTxnItemsVO networkItemsVO = null;
        ArrayList arrayList = null;
        String netCode = data.get(NETWORKCODE).toString();
        try {
            final NetworkStockTxnVO networkStockTxnVO = new NetworkStockTxnVO();
            networkStockTxnVO.setNetworkCode(data.get(NETWORKCODE).toString());
            networkStockTxnVO.setNetworkFor(data.get(NETWORKCODEFOR).toString());
            if (netCode.equals(data.get(NETWORKCODEFOR).toString())) {
                networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
            } else {
                networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
            }
            networkStockTxnVO.setReferenceNo("O2CTRFDDT-" + PretupsI.DEBIT);
            networkStockTxnVO.setTxnDate(beingProcessedDate);
            networkStockTxnVO.setRequestedQuantity(stock);
            networkStockTxnVO.setApprovedQuantity(stock);
            networkStockTxnVO.setInitiaterRemarks("");
            networkStockTxnVO.setFirstApprovedRemarks("");
            networkStockTxnVO.setSecondApprovedRemarks("");
            networkStockTxnVO.setFirstApprovedBy(SYSTEM);
            networkStockTxnVO.setSecondApprovedBy(SYSTEM);
            networkStockTxnVO.setFirstApprovedOn(beingProcessedDate);
            networkStockTxnVO.setSecondApprovedOn(beingProcessedDate);
            networkStockTxnVO.setCreatedBy(SYSTEM);
            networkStockTxnVO.setCreatedOn(beingProcessedDate);
            networkStockTxnVO.setModifiedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
            networkStockTxnVO.setModifiedOn(beingProcessedDate);
            networkStockTxnVO.setTxnMrp(stock);
            networkStockTxnVO.setTxnStatus(PretupsI.NETWORK_STOCK_TXN_STATUS_CLOSE);
            networkStockTxnVO.setTxnNo(txnId);
            networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_TRANSFER);
            networkStockTxnVO.setTxnType(PretupsI.DEBIT);
            networkStockTxnVO.setInitiaterRemarks(INITIATER_REMARKS);
            networkStockTxnVO.setInitiatedBy(SYSTEM);
            networkStockTxnVO.setUserID(SYSTEM);
            networkItemsVO = new NetworkStockTxnItemsVO();
            networkItemsVO.setSNo(1);
            networkItemsVO.setTxnNo(txnId);
            networkItemsVO.setProductCode(data.get(PRODUCTCODE).toString());
            networkItemsVO.setRequiredQuantity(stock);
            networkItemsVO.setApprovedQuantity(stock);
            networkItemsVO.setMrp(stock);
            networkItemsVO.setAmount(stock);
            networkItemsVO.setDateTime(beingProcessedDate);
            networkItemsVO.setStock(stock);

            arrayList = new ArrayList();
            arrayList.add(networkItemsVO);
            networkStockTxnVO.setNetworkStockTxnItemsList(arrayList);
            networkStockTxnVO.setTxnWallet(walletType);
            final NetworkStockDAO networkStockDAO = new NetworkStockDAO();
            updateCount = networkStockDAO.addNetworkStockTransaction(con, networkStockTxnVO);
        } catch (BTSLBaseException be) {
            log.error(methodName, BTSLBASEEXCEPTION + be.getMessage());
            log.errorTrace(methodName, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "O2CTransferNetworkStockDeduction[updateNetworkStockTransactionDetails]", "", "", "", "BTSLBaseException:" + be.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.O2CTRFDDT_ERROR_EXCEPTION);
        } catch (Exception e) {
            log.error(methodName, BTSLBASEEXCEPTION + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "O2CTransferNetworkStockDeduction[updateNetworkStockTransactionDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.O2CTRFDDT_ERROR_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited  updateCount " + updateCount);
        }

        return updateCount;
    }
}
