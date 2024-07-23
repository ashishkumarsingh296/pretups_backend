/**
 * @(#)NetworkStockDAO.java
 *                          Copyright(c) 2005, Bharti Telesoft Ltd.
 *                          All Rights Reserved
 * 
 *                          <description>
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          avinash.kamthan Aug 11, 2005 Initital Creation
 *                          Sandeep Goel Aug 03, 2006 Modification ID NS001
 *                          Sandeep Goel Nov 06, 2006 Modification ID NS002
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 * 
 */

package com.btsl.pretups.networkstock.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

// commented for DB2import oracle.jdbc.OraclePreparedStatement;
import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.ObjectProducer;
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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.restapi.networkadmin.networkStock.NetworkStockTxnVO1;

/**
 * @author avinash.kamthan
 */
public class NetworkStockDAO {
    /**
     * Commons Logging instance.
     */
    private static Log log = LogFactory.getLog(NetworkStockDAO.class.getName());
    private NetworkStockQry networkStockQry = (NetworkStockQry) ObjectProducer.getObject(QueryConstants.NETWORK_STOCK_QRY, QueryConstants.QUERY_PRODUCER);
    /**
     * Debit the Network Stock
     * 
     * @param p_con
     * @param p_networkStockList
     * @return int
     * @throws BTSLBaseException
     */
    @SuppressWarnings("resource")
	public int debitNetworkStock(Connection p_con, ArrayList p_networkStockList) throws BTSLBaseException {
        final String methodName = "debitNetworkStock";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered List Size : " + p_networkStockList.size());
        }

        int updateCount = 0;
        

       

        final String sqlSelect = networkStockQry.debitNetworkStockSelectForQry();
 
        StringBuffer strBuffUpdate = new StringBuffer();
        strBuffUpdate.append(" UPDATE network_stocks SET previous_balance = wallet_balance , wallet_balance = ?, ");
        strBuffUpdate.append(" wallet_sold = ? , last_txn_no = ? , last_txn_type = ?, last_txn_balance= ?, ");
        strBuffUpdate.append(" modified_by =?, modified_on =? ");
        strBuffUpdate.append(" WHERE ");
        strBuffUpdate.append(" network_code = ? ");
        strBuffUpdate.append(" AND ");
        strBuffUpdate.append(" product_code = ? AND network_code_for = ? and wallet_type=? ");
        String updateQuery = strBuffUpdate.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Update query:" + updateQuery);
        }

        try {

            NetworkStockVO networkStockVO = null;
            for (int i = 0, k = p_networkStockList.size(); i < k; i++) {

                networkStockVO = (NetworkStockVO) p_networkStockList.get(i);
                try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);PreparedStatement psmtUpdate = p_con.prepareStatement(updateQuery);)
                {

                pstmt.setString(1, networkStockVO.getNetworkCode());
                pstmt.setString(2, networkStockVO.getProductCode());
                pstmt.setString(3, networkStockVO.getNetworkCodeFor());
                pstmt.setString(4, networkStockVO.getWalletType());

                try(ResultSet rs = pstmt.executeQuery();)
                {
                long stock = -1;
                long stockSold = -1;

                if (rs.next()) {
                    stock = rs.getLong("wallet_balance");
                    stockSold = rs.getLong("wallet_sold");

                } else {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_NW_STOCK_NOT_EXIST);
                }

                if (stock <= networkStockVO.getWalletbalance()) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_NW_STOCK_LESS);
                }

                if (stock != -1) {
                    stock -= networkStockVO.getWalletbalance();
                }
                if (stockSold != -1) {
                    stockSold += networkStockVO.getWalletbalance();
                }

                int m = 0;
                final Date curDate=new Date();
                psmtUpdate.setLong(++m, stock);
                psmtUpdate.setLong(++m, stockSold);
                psmtUpdate.setString(++m, networkStockVO.getLastTxnNum());
                psmtUpdate.setString(++m, networkStockVO.getLastTxnType());
                psmtUpdate.setLong(++m, networkStockVO.getLastTxnBalance());
                psmtUpdate.setString(++m, networkStockVO.getModifiedBy());
                psmtUpdate.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(curDate));
                psmtUpdate.setString(++m, networkStockVO.getNetworkCode());
                psmtUpdate.setString(++m, networkStockVO.getProductCode());
                psmtUpdate.setString(++m, networkStockVO.getNetworkCodeFor());
                psmtUpdate.setString(++m, networkStockVO.getWalletType());
                updateCount = psmtUpdate.executeUpdate();

                psmtUpdate.clearParameters();

                if (updateCount <= 0) {
                    psmtUpdate.close();
                    String[] arr = { networkStockVO.getProductName() };
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing", arr);
               
                }

                // for logging
                networkStockVO.setPreviousBalance(stock);

                // AutoNetworkStockCreation logic
                if((boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.AUTO_NWSTK_CRTN_ALWD, networkStockVO.getNetworkCode())){
                	new com.btsl.pretups.channel.transfer.businesslogic.AutoNetworkStockBL().networkStockThresholdValidation(networkStockVO);
                }

            }
                }
            }// for
        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[debitNetworkStock]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[debitNetworkStock]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
          
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:  updateCount =" + updateCount);
            }
        }
        return updateCount;
    }
    

    /**
     * Credit the network stock, This method will be called in case of the
     * Channel to0 Operator return/Withdrawal
     * 
     * @param p_con
     * @param p_networkStockList
     * @return int
     * @throws BTSLBaseException
     */
    public int creditNetworkStock(Connection p_con, ArrayList p_networkStockList) throws BTSLBaseException {
        final String methodName = "creditNetworkStock";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered List Size : " + p_networkStockList.size());
        }

        int updateCount = 0;
        

        

        final String sqlSelect = networkStockQry.creditNetworkStockSelectForQry();
 
        StringBuffer strBuffUpdate = new StringBuffer(" UPDATE network_stocks SET previous_balance = wallet_balance , wallet_balance =wallet_balance+?, ");
        strBuffUpdate.append(" wallet_returned = ? , last_txn_no = ? , last_txn_type = ?, last_txn_balance= ?, ");
        strBuffUpdate.append(" modified_by =?, modified_on =? ");
        strBuffUpdate.append(" WHERE ");
        strBuffUpdate.append(" network_code = ? ");
        strBuffUpdate.append(" AND ");
        strBuffUpdate.append(" product_code = ? AND network_code_for = ?  AND wallet_type =  ? ");
        String updateQuery = strBuffUpdate.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Update query:" + updateQuery);
        }

        try {

            NetworkStockVO networkStockVO = null;
            for (int i = 0, k = p_networkStockList.size(); i < k; i++) {
                networkStockVO = (NetworkStockVO) p_networkStockList.get(i);
                try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);PreparedStatement psmtUpdate = p_con.prepareStatement(updateQuery);)
                {
                // }
                pstmt.setString(1, networkStockVO.getNetworkCode());
                pstmt.setString(2, networkStockVO.getProductCode());
                pstmt.setString(3, networkStockVO.getNetworkCodeFor());
                pstmt.setString(4, networkStockVO.getWalletType());

                try(ResultSet rs = pstmt.executeQuery();)
                {
                long stock = -1;
                long stockReturned = -1;
                if (rs.next()) {
                    stock = rs.getLong("wallet_balance");
                    if(PretupsI.AUTO_NETWORKSTOCK_CREATE.equals(networkStockVO.getLastTxnType()) && !BTSLUtil.isNullString(networkStockVO.getOtherValue()) && stock >= Long.parseLong(networkStockVO.getOtherValue()))
                    	throw new BTSLBaseException(this, methodName, "error.autonetworkstock.alreadycreated");
                    stockReturned = rs.getLong("wallet_returned");
                } else {
                    throw new BTSLBaseException(this, methodName, "error.transfer.networkstock.notexist");
                }
                if(!PretupsI.AUTO_NETWORKSTOCK_CREATE.equals(networkStockVO.getLastTxnType())){
                	 stockReturned += networkStockVO.getWalletbalance();
                }
               

                int m = 0;
                psmtUpdate.setLong(++m, networkStockVO.getWalletbalance());
                psmtUpdate.setLong(++m, stockReturned);
                psmtUpdate.setString(++m, networkStockVO.getLastTxnNum());
                psmtUpdate.setString(++m, networkStockVO.getLastTxnType());
                psmtUpdate.setLong(++m, networkStockVO.getLastTxnBalance());
                psmtUpdate.setString(++m, networkStockVO.getModifiedBy());
                psmtUpdate.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(networkStockVO.getModifiedOn()));
                psmtUpdate.setString(++m, networkStockVO.getNetworkCode());
                psmtUpdate.setString(++m, networkStockVO.getProductCode());
                psmtUpdate.setString(++m, networkStockVO.getNetworkCodeFor());
                psmtUpdate.setString(++m, networkStockVO.getWalletType());
                updateCount = psmtUpdate.executeUpdate();
                psmtUpdate.clearParameters();

                if (updateCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }

                // Setting for logging
                networkStockVO.setPreviousBalance(stock);
                }
            }
            // for
        }
        }catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[creditNetworkStock]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[creditNetworkStock]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:  updateCount =" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Debit the Network Stock
     * 
     * @param p_con
     * @param p_networkStockList
     * @param p_returnErrorCode
     * @return int
     * @throws BTSLBaseException
     */
    public int debitNetworkStock(Connection p_con, ArrayList p_networkStockList, boolean p_returnErrorCode) throws BTSLBaseException {
        final String methodName = "debitNetworkStock";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered List Size : " + p_networkStockList.size() + " p_returnErrorCode :" + p_returnErrorCode);
        }
        int updateCount = 0;

        try {
            updateCount = debitNetworkStock(p_con, p_networkStockList);
        } catch (BTSLBaseException be) {
            log.errorTrace(methodName, be);
            if (p_returnErrorCode) {
                if (("error.transfer.networkstock.notexist").equals(be.getMessage())) {
                    throw new BTSLBaseException("NetworkStockDAO", methodName, PretupsErrorCodesI.ERROR_NW_STOCK_NOT_EXIST);
                }
                if (("error.transfer.networkstock.less").equals(be.getMessage())) {
                    throw new BTSLBaseException("NetworkStockDAO", methodName, PretupsErrorCodesI.ERROR_NW_STOCK_LESS);
                }
            }
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:  updateCount =" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Credit the network stock, This method will be called in case of the
     * Channel to0 Operator return/Withdrawal
     * 
     * @param p_con
     * @param p_networkStockList
     * @param p_returnErrorCode
     * @return int
     * @throws BTSLBaseException
     */
    public int creditNetworkStock(Connection p_con, ArrayList p_networkStockList, boolean p_returnErrorCode) throws BTSLBaseException {
        final String METHOD_NAME = "creditNetworkStock";
        if (log.isDebugEnabled()) {
            log.debug("creditNetworkStock", "Entered List Size : " + p_networkStockList.size() + " p_returnErrorCode :" + p_returnErrorCode);
        }
        int updateCount = 0;
        try {
            updateCount = creditNetworkStock(p_con, p_networkStockList);
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            if (p_returnErrorCode) {
                if (("error.transfer.networkstock.notexist").equals(be.getMessage())) {
                    throw new BTSLBaseException("NetworkStockDAO", "debitNetworkStock", PretupsErrorCodesI.ERROR_NW_STOCK_NOT_EXIST);
                }
            }
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("creditNetworkStock", "Exiting:  updateCount =" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * add the details to network stock transaction
     * 
     * @param p_con
     * @param p_networkStockTxnVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addNetworkStockTransaction(Connection p_con, NetworkStockTxnVO p_networkStockTxnVO) throws BTSLBaseException {
        final String methodName = "addNetworkStockTransaction";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_networkStockTxnVO : " + p_networkStockTxnVO);
        }
        // commented for DB2OraclePreparedStatement pstmtInsert = null;
        
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        int updateCount = 0;
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append(" INSERT INTO network_stock_transactions ( ");
            strBuff.append(" ref_txn_id,txn_wallet, txn_no, network_code, network_code_for, stock_type, reference_no, txn_date, requested_quantity, ");
            strBuff.append(" approved_quantity, initiater_remarks, first_approved_remarks, second_approved_remarks, ");
            strBuff.append(" first_approved_by, second_approved_by, first_approved_on, second_approved_on, ");
            strBuff.append(" cancelled_by, cancelled_on, created_by, created_on, modified_on, modified_by, ");
            strBuff.append(" txn_status, entry_type, txn_type, initiated_by, first_approver_limit, user_id, txn_mrp, tax3_value ) ");
            strBuff.append(" VALUES ");
            strBuff.append(" (");
            strBuff.append("?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            String query = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "insert query:" + query);
            }

            // commented for DB2 pstmtInsert =
            // (OraclePreparedStatement)p_con.prepareStatement(query);
            try(PreparedStatement pstmtInsert = (PreparedStatement) p_con.prepareStatement(query);)
            {
            int i = 0;
            pstmtInsert.setString(++i, p_networkStockTxnVO.getRefTxnID());
            pstmtInsert.setString(++i, p_networkStockTxnVO.getTxnWallet());
            pstmtInsert.setString(++i, p_networkStockTxnVO.getTxnNo());
            pstmtInsert.setString(++i, p_networkStockTxnVO.getNetworkCode());
            pstmtInsert.setString(++i, p_networkStockTxnVO.getNetworkFor());
            pstmtInsert.setString(++i, p_networkStockTxnVO.getStockType());
            pstmtInsert.setString(++i, p_networkStockTxnVO.getReferenceNo());
            if (p_networkStockTxnVO.getTxnDate() != null) {
                pstmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getTxnDate()));
            } else {
                pstmtInsert.setTimestamp(++i,  timestamp);
            }
            pstmtInsert.setLong(++i, p_networkStockTxnVO.getRequestedQuantity());
            pstmtInsert.setLong(++i, p_networkStockTxnVO.getApprovedQuantity());
            pstmtInsert.setString(++i, p_networkStockTxnVO.getInitiaterRemarks());

            // for multilanguage support
            // commented for DB2 pstmtInsert.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(++i, p_networkStockTxnVO.getFirstApprovedRemarks());

            // for multilanguage support
            // commented for DB2 pstmtInsert.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(++i, p_networkStockTxnVO.getSecondApprovedRemarks());

            // for multilanguage support
            // commented for DB2 pstmtInsert.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(++i, p_networkStockTxnVO.getFirstApprovedBy());

            // for multilanguage support
            // commented for DB2 pstmtInsert.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(++i, p_networkStockTxnVO.getSecondApprovedBy());

            pstmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getFirstApprovedOn()));
            pstmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getSecondApprovedOn()));
            pstmtInsert.setString(++i, p_networkStockTxnVO.getCancelledBy());
            pstmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getCancelledOn()));
            pstmtInsert.setString(++i, p_networkStockTxnVO.getCreatedBy());
            pstmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getCreatedOn()));
            pstmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getModifiedOn()));
            pstmtInsert.setString(++i, p_networkStockTxnVO.getModifiedBy());
            pstmtInsert.setString(++i, p_networkStockTxnVO.getTxnStatus());
            pstmtInsert.setString(++i, p_networkStockTxnVO.getEntryType());
            pstmtInsert.setString(++i, p_networkStockTxnVO.getTxnType());
            pstmtInsert.setString(++i, p_networkStockTxnVO.getInitiatedBy());
            pstmtInsert.setLong(++i, p_networkStockTxnVO.getFirstApproverLimit());
            pstmtInsert.setString(++i, p_networkStockTxnVO.getUserID());
            pstmtInsert.setLong(++i, p_networkStockTxnVO.getTxnMrp());
            pstmtInsert.setLong(++i, p_networkStockTxnVO.getTax3value());

            updateCount = pstmtInsert.executeUpdate();
            // added to make code compatible with insertion in partitioned table in postgres DB
            updateCount = BTSLUtil.getInsertCount(updateCount); 
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }

            // adding the networkstock items
            addNetworkStockItems(p_con, p_networkStockTxnVO.getNetworkStockTxnItemsList(), p_networkStockTxnVO.getTxnNo());

        } 
        }catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[addNetworkStockTransaction]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[addNetworkStockTransaction]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting Success :" + updateCount);
            }
        }// end of finally

        return updateCount;

    }

    /**
     * Add items to Networkstock items
     * 
     * @param p_con
     * @param p_networkStockTxnItemsVOList
     * @param p_txnNum
     * @return int
     * @throws BTSLBaseException
     */
    private int addNetworkStockItems(Connection p_con, ArrayList p_networkStockTxnItemsVOList, String p_txnNum) throws BTSLBaseException {

        final String methodName = "addNetworkStockItems";
        if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered NetworkStockTxnItemsVOList Size: ");
        	loggerValue.append(p_networkStockTxnItemsVOList.size());
        	loggerValue.append(" TxnNum : ");
        	loggerValue.append(p_txnNum);
        	
            log.debug(methodName, "Entered NetworkStockTxnItemsVOList Size: " + p_networkStockTxnItemsVOList.size() + " TxnNum : " + p_txnNum);
        }

         
        int updateCount = 0;
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append(" INSERT INTO network_stock_trans_items "); 
            strBuff.append(" (s_no, txn_no, product_code, required_quantity, approved_quantity, stock, mrp, amount, date_time) ");
            strBuff.append(" VALUES ");
            strBuff.append(" (?,?,?,?,?,?,?,?,?) ");

            String query = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "insert query:" + query);
            }
           try(PreparedStatement pstmtInsert = p_con.prepareStatement(query);)
           {
            NetworkStockTxnItemsVO networkStockTxnItemsVO = null;
            for (int i = 0, k = p_networkStockTxnItemsVOList.size(); i < k; i++) {
                networkStockTxnItemsVO = (NetworkStockTxnItemsVO) p_networkStockTxnItemsVOList.get(i);

                int m = 0;

                pstmtInsert.setInt(++m, networkStockTxnItemsVO.getSNo());
                pstmtInsert.setString(++m, networkStockTxnItemsVO.getTxnNo());
                pstmtInsert.setString(++m, networkStockTxnItemsVO.getProductCode());
                pstmtInsert.setLong(++m, networkStockTxnItemsVO.getRequiredQuantity());
                pstmtInsert.setLong(++m, networkStockTxnItemsVO.getApprovedQuantity());
                pstmtInsert.setLong(++m, networkStockTxnItemsVO.getWalletbalance());
                pstmtInsert.setLong(++m, networkStockTxnItemsVO.getMrp());
                pstmtInsert.setLong(++m, networkStockTxnItemsVO.getAmount());
                // Added on 07/02/08 for addition of new date_time column in the
                // table NETWORK_STOCK_TRANS_ITEMS.
                pstmtInsert.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnItemsVO.getDateTime()));

                updateCount = pstmtInsert.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                pstmtInsert.clearParameters();
                if (updateCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }

        }
        }// end of try
        catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[addNetworkStockItems]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[addNetworkStockItems]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting Success :" + updateCount);
            }
        }// end of finally

        return updateCount;
    }

    /**
     * Method loadCurrentStockList.
     * This method is to load the list of current stocks txn
     * 
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_networkFor
     *            String
     * @param p_networkForType
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCurrentStockList(Connection p_con, String p_networkCode, String p_networkFor, String p_networkForType) throws BTSLBaseException {
    	 final String methodName = "loadCurrentStockList";
    	if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append( " p_network_For=");
        	loggerValue.append(p_networkFor);
        	loggerValue.append(",p_network_type=");
        	loggerValue.append(p_networkForType);
            log.debug(methodName,loggerValue);
        }
       
       
        ArrayList stockItemList = new ArrayList();
       
        final String query = networkStockQry.loadCurrentStockListQry();
       
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY query=" + query);
        }
        try( PreparedStatement pstmtSelect = p_con.prepareStatement(query);) {
            
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_networkFor);
            pstmtSelect.setString(3, p_networkFor);
            pstmtSelect.setString(4, p_networkCode);
            pstmtSelect.setString(5, p_networkForType);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            NetworkStockVO stockVO = null;
            long productMRP = 0L;
            while (rs.next()) {
                stockVO = new NetworkStockVO();
                stockVO.setNetworkName(rs.getString("network_name"));
                stockVO.setNetworkForName(rs.getString("network_code_for"));
                stockVO.setProductName(rs.getString("product_name"));
                productMRP = rs.getLong("unit_value");
                stockVO.setProductMrp(PretupsBL.getDisplayAmount(productMRP));
                stockVO.setWalletType(rs.getString("wallet_type"));
                stockVO.setWalletBalance(rs.getLong("wallet_balance"));
                stockVO.setWalletReturned(rs.getLong("wallet_returned"));
                stockVO.setWalletSold(rs.getLong("wallet_sold"));
                stockVO.setWalletCreated(rs.getLong("wallet_created"));
                stockVO.setWalletBalanceValue(PretupsBL.getDisplayAmount(stockVO.getWalletbalance() * productMRP));
                stockItemList.add(stockVO);
            }
        } 
        }catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[loadCurrentStockList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[loadCurrentStockList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting stockList size=" + stockItemList.size());
            }
        }
        return stockItemList;
    }

    /**
     * Method loadStockOfProduct.
     * This method is to load the stock of the products of the p_productType of
     * C2S module
     * 
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_networkFor
     *            String
     * @param p_productType
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadStockOfProduct(Connection p_con, String p_networkCode, String p_networkFor, String p_productType) throws BTSLBaseException {
        final String methodName = "loadStockOfProduct";
        if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered :: p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_networkFor=");
        	loggerValue.append(p_networkFor);
        	loggerValue.append(" p_productType=");
        	loggerValue.append(p_productType);
            log.debug(methodName,loggerValue);
        }
         
       
        ListValueVO listValueVO = null;
        ArrayList productList = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT ns.wallet_balance,ns.wallet_type, ns.product_code ");
        strBuff.append("FROM products p, network_stocks ns ");
        strBuff.append("WHERE p.module_code = ? AND p.product_type =? ");
        strBuff.append("AND p.status = 'Y' ");
        strBuff.append("AND ns.network_code = ? AND ns.network_code_for = ? AND wallet_type = ?");
        strBuff.append("AND ns.product_code = p.product_code ");
        strBuff.append("ORDER BY product_code ");
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(strBuff.toString());) {
            if (log.isDebugEnabled()) {
                log.debug(" loadStockOfProduct", "Query :: " + strBuff.toString());
            }
           
            pstmtSelect.setString(1, PretupsI.C2S_MODULE);
            pstmtSelect.setString(2, p_productType);
            pstmtSelect.setString(3, p_networkCode);
            pstmtSelect.setString(4, p_networkFor);
            pstmtSelect.setString(5, PretupsI.SALE_WALLET_TYPE);
            try( ResultSet rs = pstmtSelect.executeQuery();)
            {
            productList = new ArrayList();
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("product_code"), rs.getString("wallet_balance"));
                productList.add(listValueVO);
            }
        }
        }catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[loadStockOfProduct]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[loadStockOfProduct]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting productList=" + productList.size());
            }
        }
        return productList;
    }

    /**
     * Method updateNetworkDailyStock.
     * 
     * @author Ashutosh
     *         Method signature is changed and method body and references are
     *         changed accordingly.
     * @param p_con
     *            Connection
     * @param p_networkStockVO
     *            NetworkStockVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateNetworkDailyStock(Connection p_con, NetworkStockVO p_networkStockVO) throws BTSLBaseException {
        final String methodName = "updateNetworkDailyStock";
        if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_networkStockVO = ");
        	loggerValue.append(p_networkStockVO);
            log.debug(methodName, loggerValue);
        }
        
       
        int count = 1;
       final String selectStrBuff = networkStockQry.updateNetworkDailyStockQry();
        StringBuffer updateStrBuff = new StringBuffer();
        updateStrBuff.append("UPDATE network_stocks SET daily_stock_updated_on = ? ");
        updateStrBuff.append("WHERE network_code = ? AND network_code_for = ? AND wallet_type = ? ");

        StringBuffer insertStrBuff = new StringBuffer();

        insertStrBuff.append("INSERT INTO network_daily_stocks(wallet_date, wallet_type,network_code, network_code_for, ");
        insertStrBuff.append("product_code,wallet_created, wallet_returned, wallet_balance, wallet_sold, ");
        insertStrBuff.append("last_txn_no, last_txn_type, last_txn_balance, previous_balance, created_on,creation_type )");
        insertStrBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

        if (log.isDebugEnabled()) {
            log.debug("updateNetworkDailyStock ", "Insert Query=" + insertStrBuff);
            log.debug("updateNetworkDailyStock ", "Update Query=" + updateStrBuff);
        }
        try (PreparedStatement  pstmtSelect = p_con.prepareStatement(selectStrBuff);
        		PreparedStatement pstmtUpdate = p_con.prepareStatement(updateStrBuff.toString());
        		PreparedStatement pstmtInsert = p_con.prepareStatement(insertStrBuff.toString());){
          

            Date dailyStockUpdatedOn = null;

            int dayDifference = 0;
            // select the record form the network stock table.
            pstmtSelect.setString(1, p_networkStockVO.getNetworkCode());
            pstmtSelect.setString(2, p_networkStockVO.getNetworkCodeFor());
            pstmtSelect.setString(3, p_networkStockVO.getWalletType());
            pstmtSelect.setDate(4, BTSLUtil.getSQLDateFromUtilDate(p_networkStockVO.getModifiedOn()));

            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            pstmtSelect.clearParameters();
            while (rs.next()) {
                dailyStockUpdatedOn = rs.getDate("daily_stock_updated_on");

                // if record exist check updated on date with current date
                // day differences to maintain the record of previous days.
                dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyStockUpdatedOn, p_networkStockVO.getModifiedOn());

                if (dayDifference > 0) {
                    // if dates are not equal get the day differencts and
                    // execute insert qurery no of times of the
                    if (log.isDebugEnabled()) {
                        log.debug("updateNetworkDailyStock ", "Till now daily Stock is not updated on " + p_networkStockVO.getModifiedOn() + ", day differences = " + dayDifference);
                    }

                    for (int k = 0; k < dayDifference; k++) {
                        pstmtInsert.setDate(1, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyStockUpdatedOn, k)));
                        pstmtInsert.setString(2, rs.getString("wallet_type"));
                        pstmtInsert.setString(3, rs.getString("network_code"));
                        pstmtInsert.setString(4, rs.getString("network_code_for"));
                        pstmtInsert.setString(5, rs.getString("product_code"));

                        pstmtInsert.setLong(6, rs.getLong("wallet_created"));
                        pstmtInsert.setLong(7, rs.getLong("wallet_returned"));
                        pstmtInsert.setLong(8, rs.getLong("wallet_balance"));
                        pstmtInsert.setLong(9, rs.getLong("wallet_sold"));
                        pstmtInsert.setString(10, p_networkStockVO.getLastTxnNum());
                        pstmtInsert.setString(11, p_networkStockVO.getLastTxnType());
                        pstmtInsert.setLong(12, rs.getLong("last_txn_balance"));
                        pstmtInsert.setLong(13, rs.getLong("previous_balance"));
                        pstmtInsert.setTimestamp(14, BTSLUtil.getTimestampFromUtilDate(p_networkStockVO.getModifiedOn()));
                        pstmtInsert.setString(15, PretupsI.DAILY_STOCK_CREATION_TYPE_MAN);
                        count = pstmtInsert.executeUpdate();
						// added to make code compatible with insertion in partitioned table in postgres
						count = BTSLUtil.getInsertCount(count); 
							
                        if (count <= 0) {
                            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                        }
                        pstmtInsert.clearParameters();
                    }
                    final Date curDate=new Date();
                    pstmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(curDate));
                    pstmtUpdate.setString(2, p_networkStockVO.getNetworkCode());
                    pstmtUpdate.setString(3, p_networkStockVO.getNetworkCodeFor());
                    pstmtUpdate.setString(4, p_networkStockVO.getWalletType());
                    count = pstmtUpdate.executeUpdate();
                    if (count <= 0) {
                        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                    }
                    pstmtUpdate.clearParameters();
                }
            }// end of while
        }
        }catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[updateNetworkDailyStock]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[updateNetworkDailyStock]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	
          
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting count = " + count);
            }
        }
        return count;
    }

    /**
     * Method isProductExistInStock.
     * This method is use to check whether the sock of the specified product is
     * ever created in the network or
     * not.
     * 
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_networkFor
     *            String
     * @param p_productCode
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isProductExistInStock(Connection p_con, String p_networkCode, String p_networkFor, String p_productCode) throws BTSLBaseException {
        final String methodName = "isProductExistInStock";
        if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered :: p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_networkFor=");
        	loggerValue.append(p_networkFor);
        	loggerValue.append(" p_productCode=");
        	loggerValue.append(p_productCode);
   
            log.debug(methodName,loggerValue);
        }
        
         
        boolean isExist = false;
        StringBuffer strBuff = new StringBuffer("SELECT 1 FROM network_stocks ");
        strBuff.append("WHERE network_code = ? AND network_code_for = ? AND product_code = ? ");
        try( PreparedStatement pstmtSelect = p_con.prepareStatement(strBuff.toString());) {
            if (log.isDebugEnabled()) {
                log.debug(" isProductExistInStock", "Query :: " + strBuff.toString());
            }
           
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_networkFor);
            pstmtSelect.setString(3, p_productCode);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                isExist = true;
            }
            }
        } catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[isProductExistInStock]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[isProductExistInStock]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting isExist=" + isExist);
            }
        }
        return isExist;
    }
    public int addNetworkStockTransaction1(Connection p_con, NetworkStockTxnVO1 p_networkStockTxnVO) throws BTSLBaseException {
        final String methodName = "addNetworkStockTransaction1";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_networkStockTxnVO : " + p_networkStockTxnVO);
        }

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        int updateCount = 0;
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append(" INSERT INTO network_stock_transactions ( ");
            strBuff.append(" ref_txn_id,txn_wallet, txn_no, network_code, network_code_for, stock_type, reference_no, txn_date, requested_quantity, ");
            strBuff.append(" approved_quantity, initiater_remarks, first_approved_remarks, second_approved_remarks, ");
            strBuff.append(" first_approved_by, second_approved_by, first_approved_on, second_approved_on, ");
            strBuff.append(" cancelled_by, cancelled_on, created_by, created_on, modified_on, modified_by, ");
            strBuff.append(" txn_status, entry_type, txn_type, initiated_by, first_approver_limit, user_id, txn_mrp, tax3_value ) ");
            strBuff.append(" VALUES ");
            strBuff.append(" (");
            strBuff.append("?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            String query = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "insert query:" + query);
            }
            try(PreparedStatement pstmtInsert = (PreparedStatement) p_con.prepareStatement(query);)
            {
                int i = 0;
                pstmtInsert.setString(++i, p_networkStockTxnVO.getRefTxnID());
                pstmtInsert.setString(++i, p_networkStockTxnVO.getTxnWallet());
                pstmtInsert.setString(++i, p_networkStockTxnVO.getTxnNo());
                pstmtInsert.setString(++i, p_networkStockTxnVO.getNetworkCode());
                pstmtInsert.setString(++i, p_networkStockTxnVO.getNetworkFor());
                pstmtInsert.setString(++i, p_networkStockTxnVO.getStockType());
                pstmtInsert.setString(++i, p_networkStockTxnVO.getReferenceNo());
                if (p_networkStockTxnVO.getTxnDate() != null) {
                    pstmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getTxnDate()));
                } else {
                    pstmtInsert.setTimestamp(++i,  timestamp);
                }
                pstmtInsert.setLong(++i, p_networkStockTxnVO.getRequestedQuantity());
                pstmtInsert.setLong(++i, p_networkStockTxnVO.getApprovedQuantity());
                pstmtInsert.setString(++i, p_networkStockTxnVO.getInitiaterRemarks());
                pstmtInsert.setString(++i, p_networkStockTxnVO.getFirstApprovedRemarks());
                pstmtInsert.setString(++i, p_networkStockTxnVO.getSecondApprovedRemarks());
                pstmtInsert.setString(++i, p_networkStockTxnVO.getFirstApprovedBy());
                pstmtInsert.setString(++i, p_networkStockTxnVO.getSecondApprovedBy());
                pstmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getFirstApprovedOn()));
                pstmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getSecondApprovedOn()));
                pstmtInsert.setString(++i, p_networkStockTxnVO.getCancelledBy());
                pstmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getCancelledOn()));
                pstmtInsert.setString(++i, p_networkStockTxnVO.getCreatedBy());
                pstmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getCreatedOn()));
                pstmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getModifiedOn()));
                pstmtInsert.setString(++i, p_networkStockTxnVO.getModifiedBy());
                pstmtInsert.setString(++i, p_networkStockTxnVO.getTxnStatus());
                pstmtInsert.setString(++i, p_networkStockTxnVO.getEntryType());
                pstmtInsert.setString(++i, p_networkStockTxnVO.getTxnType());
                pstmtInsert.setString(++i, p_networkStockTxnVO.getInitiatedBy());
                pstmtInsert.setLong(++i, p_networkStockTxnVO.getFirstApproverLimit());
                pstmtInsert.setString(++i, p_networkStockTxnVO.getUserID());
                pstmtInsert.setLong(++i, p_networkStockTxnVO.getTxnMrp());
                pstmtInsert.setLong(++i, p_networkStockTxnVO.getTax3value());

                updateCount = pstmtInsert.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount);
                if (updateCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
                addNetworkStockItems(p_con, p_networkStockTxnVO.getNetworkStockTxnItemsList(), p_networkStockTxnVO.getTxnNo());

            }
        }catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[addNetworkStockTransaction]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }
        catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[addNetworkStockTransaction]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }
        finally {


            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting Success :" + updateCount);
            }
        }

        return updateCount;

    }

}
