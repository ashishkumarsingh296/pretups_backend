package com.web.pretups.networkstock.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.networkadmin.networkStock.NetworkStockTxnVO1;

public class NetworkStockWebDAO {

    /**
     * Commons Logging instance.
     */
    private static Log _log = LogFactory.getLog(NetworkStockWebDAO.class.getName());
    private NetworkStockWebQry networkStockWebQry = (NetworkStockWebQry)ObjectProducer.getObject(QueryConstants.NETWORK_STOCK_WEB_QUERY, QueryConstants.QUERY_PRODUCER);

    /**
     * 
     * This method loads all the products when status Y and the products are
     * associated with the network
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     *            * @param p_networkCode String
     * @param p_networkFor
     *            String
     * @param p_module
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadProductsForStock(Connection p_con, String p_networkCode, String p_networkFor, String p_module) throws BTSLBaseException {
        final String methodName = "loadProductsForStock";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered :: p_networkCode = ");
        	msg.append(p_networkCode);
        	msg.append(", p_networkFor = ");
        	msg.append(p_networkFor);
        	msg.append(", p_module = ");
        	msg.append(p_module);

        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        NetworkStockTxnItemsVO networkStockTxnItemsVO = null;
        ArrayList productList = null;
        try {
        	pstmtSelect=networkStockWebQry.loadProductsForStockQry(p_con, p_networkCode, p_networkFor, p_module);
            rs = pstmtSelect.executeQuery();
            productList = new ArrayList();
            NetworkStockTxnItemsVO tempVO = new NetworkStockTxnItemsVO();
            boolean found = false;
            while (rs.next()) {
                networkStockTxnItemsVO = new NetworkStockTxnItemsVO();
                networkStockTxnItemsVO.setProductCode(rs.getString("product_code"));
                networkStockTxnItemsVO.setProductName(rs.getString("product_name"));
                networkStockTxnItemsVO.setUnitValue(rs.getLong("unit_value"));
                networkStockTxnItemsVO.setWalletBalance(rs.getLong("WALLET_BALANCE"));
                networkStockTxnItemsVO.setWalletType(rs.getString("WALLET_TYPE"));
                tempVO = networkStockTxnItemsVO;

                if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
                    if (PretupsI.SALE_WALLET_TYPE.equals(rs.getString("WALLET_TYPE"))) {
                        found = true;
                        productList.add(tempVO);
                    }

                } else if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue() && !found) {
                    tempVO.setWalletBalance(0L);
                    tempVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
                    productList.add(tempVO);
                } else {
                    productList.add(networkStockTxnItemsVO);
                }
            }

        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[loadProductsForStock]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[loadProductsForStock]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting productList=" + productList.size());
            }
        }
        return productList;
    }

    /**
     * Method loadStockTransactionList.
     * This method loads the list of the transaction to approve the order
     * 
     * @param p_con
     *            Connection
     * @param p_status
     *            String
     * @param p_networkCode
     *            String
     * @param p_networkType
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadStockTransactionList(Connection p_con, String p_status, String p_networkCode, String p_networkType) throws BTSLBaseException {
        final String methodName = "loadStockTransactionList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered :: p_status = ");
        	msg.append(p_status);
        	msg.append(", p_networkCode = ");
        	msg.append(p_networkCode);
        	msg.append(", p_networkType = ");
        	msg.append(p_networkType);

        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList stockList = new ArrayList();
        String strBuff=networkStockWebQry.loadStockTransactionListQry(p_status);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + strBuff);
        }
        try {
            int i = 0;
            pstmtSelect = p_con.prepareStatement(strBuff);
            pstmtSelect.setString(++i, p_networkCode);
            pstmtSelect.setString(++i, p_networkCode);
            pstmtSelect.setString(++i, p_networkCode);
            pstmtSelect.setString(++i, p_networkType);
            pstmtSelect.setString(++i, PretupsI.TRANSFER_STATUS);

            rs = pstmtSelect.executeQuery();
            NetworkStockTxnVO networkStockTxnVO = null;
            while (rs.next()) {
                networkStockTxnVO = new NetworkStockTxnVO();
                networkStockTxnVO.setTxnNo(rs.getString("TXNNO"));
                networkStockTxnVO.setApprovedQuantity(rs.getLong("APPROVED_QUANTITY"));
                networkStockTxnVO.setTxnMrp(rs.getLong("TXNMRP"));
                networkStockTxnVO.setTxnMrpStr(PretupsBL.getDisplayAmount(rs.getLong("TXNMRP")));
                networkStockTxnVO.setInitiatedBy(rs.getString("requester"));
                networkStockTxnVO.setReferenceNo(rs.getString("REFNO"));
                networkStockTxnVO.setTxnDate(rs.getDate("TRANSACTIONDATE"));
                networkStockTxnVO.setTxnDateAsString(rs.getDate("TRANSACTIONDATE"));
                networkStockTxnVO.setInitiaterName(rs.getString("REQUESTER_NAME"));
                networkStockTxnVO.setFirstApprovedBy(rs.getString("FANAME"));
                networkStockTxnVO.setSecondApprovedBy(rs.getString("SANAME"));
                networkStockTxnVO.setFirstApprovedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("FIRSTAPPROVEDON")));
                networkStockTxnVO.setSecondApprovedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("SECONDAPPROVEDON")));
                networkStockTxnVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("CREATEDON")));
                networkStockTxnVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("MODIFIEDON")));
                networkStockTxnVO.setLastModifiedTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("MODIFIEDON")).getTime());
                networkStockTxnVO.setTxnStatus(rs.getString("STOCK_TRANSACTIONSTATUS"));
                networkStockTxnVO.setTxnStatusName(rs.getString("STATUSNAME"));
                networkStockTxnVO.setNetworkName(rs.getString("NETWORKNAME"));
                networkStockTxnVO.setNetworkFor(rs.getString("NETWORKFOR"));
                networkStockTxnVO.setNetworkForName(rs.getString("NETWORKFORNAME"));
                networkStockTxnVO.setInitiaterRemarks(rs.getString("REMARKS"));
                networkStockTxnVO.setFirstApprovedRemarks(rs.getString("FIRST_APPROVER_REMARKS"));
                networkStockTxnVO.setSecondApprovedRemarks(rs.getString("SECOND_APPROVER_REMARKS"));
                networkStockTxnVO.setTxnWallet(rs.getString("TXN_WALLET"));
                stockList.add(networkStockTxnVO);

            }
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[loadStockTransactionList]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[loadStockTransactionList]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting orderList size=" + stockList.size());
            }
        }
        return stockList;
    }

    /**
     * Method loadStockItemList.
     * This method load the list of items which are associated with the given
     * txnNo.
     * 
     * @param p_con
     *            Connection
     * @param p_txnNo
     *            String
     * @param p_networkCode
     *            String
     * @param p_networkFor
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadStockItemList(Connection p_con, String p_txnNo, String p_networkCode, String p_networkFor, String p_txnWalletType) throws BTSLBaseException {
        final String methodName = "loadStockItemList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered :: p_txnNo = ");
        	msg.append(p_txnNo);
        	msg.append(", p_networkCode = ");
        	msg.append(p_networkCode);
        	msg.append(", p_networkFor = ");
        	msg.append(p_networkFor);
        	msg.append(", p_txnWalletType = ");
        	msg.append(p_txnWalletType);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;


        
        final ArrayList txnItemList = new ArrayList();
        try {
            pstmtSelect =networkStockWebQry.loadStockItemListQry(p_con, p_txnNo, p_networkCode, p_networkFor);
            rs = pstmtSelect.executeQuery();
            NetworkStockTxnItemsVO networkStockTxnItemsVO = null;
            final int i = 0;
            final boolean walletFound = false;
            final Long intitialWalletBal = 0L;

            while (rs.next()) {
                if (!BTSLUtil.isNullString(p_txnWalletType)) {
                    networkStockTxnItemsVO = new NetworkStockTxnItemsVO();

                    networkStockTxnItemsVO.setWalletType(rs.getString("wallet_type"));
                    networkStockTxnItemsVO.setUnitValue(rs.getLong("NEWMRP"));
                    networkStockTxnItemsVO.setMrp(rs.getLong("NEWMRP"));
                    networkStockTxnItemsVO.setProductCode(rs.getString("PID"));
                    networkStockTxnItemsVO.setProductName(rs.getString("PNAME"));
                    networkStockTxnItemsVO.setWalletBalance(rs.getLong("stock"));
                    networkStockTxnItemsVO.setStock(rs.getLong("stock"));
                    networkStockTxnItemsVO.setNetworkStock(rs.getLong("stock"));
                    networkStockTxnItemsVO.setRequestedQuantity(rs.getString("RQTY"));
                    networkStockTxnItemsVO.setApprovedQuantity(rs.getLong("AQTY"));
                    networkStockTxnItemsVO.setApprovedQuantityStr(Long.toString(rs.getLong("AQTY")));
                    networkStockTxnItemsVO.setAmount(rs.getLong("amount"));
                    networkStockTxnItemsVO.setAmountStr(PretupsBL.getDisplayAmount(rs.getLong("amount")));
                    // Added on 07/02/08 for addition of new date_time column in
                    // the table NETWORK_STOCK_TRANS_ITEMS.
                    networkStockTxnItemsVO.setDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("date_time")));
                    networkStockTxnItemsVO.setTxnWallet(((LookupsVO) LookupsCache.getObject(PretupsI.MULTIPLE_WALLET_TYPE, rs.getString("txn_wallet"))).getLookupName());

                    if (BTSLUtil.isNullString(rs.getString("wallet_type"))) {
                        networkStockTxnItemsVO.setWalletBalance(intitialWalletBal);
                        networkStockTxnItemsVO.setStock(intitialWalletBal);
                        networkStockTxnItemsVO.setWalletType(p_txnWalletType);
                        txnItemList.add(networkStockTxnItemsVO);
                        continue;
                    }

                    txnItemList.add(networkStockTxnItemsVO);

                }
            }
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[loadStockTransactionList]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadStockTransactionList", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[loadStockTransactionList]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadStockTransactionList", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting txnItemList size=" + txnItemList.size());
            }
        }
        return txnItemList;
    }

    /**
     * Method cancelStockTransaction.
     * This method is used to reject the order.
     * 
     * @param p_con
     *            Connection
     * @param p_networkStockTxnVO
     *            NetworkStockTxnVO
     * @param p_status
     *            String
     * @return int
     * @throws BTSLBaseException
     */
    public int cancelStockTransaction(Connection p_con, NetworkStockTxnVO p_networkStockTxnVO, String p_status) throws BTSLBaseException {
        final String methodName = "cancelStockTransaction";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered :: p_networkStockTxnVO = ");
        	msg.append(p_networkStockTxnVO);
        	msg.append(", p_status = ");
        	msg.append(p_status);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }

        PreparedStatement pstmtUpdate = null;
        int cancelStockTransaction = 0;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("UPDATE network_stock_transactions SET  first_approved_remarks=?,second_approved_remarks=?, ");
        strBuff.append("cancelled_by=?,cancelled_on=?,modified_on=?,txn_status=? ");
        strBuff.append("WHERE txn_no=? AND network_code=? AND network_code_for=? ");
        strBuff.append("AND txn_status IN (" + p_status + ")");

        final String sqlUpdate = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlUpdate=" + sqlUpdate);
        }
        try {
            // commented for DB2 pstmtUpdate =

            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(sqlUpdate);
            // for multilanguage support
            // commented for DB2 pstmtUpdate.setFormOfUse(1,

            pstmtUpdate.setString(1, p_networkStockTxnVO.getFirstApprovedRemarks());

            // for multilanguage support
            // commented for DB2 pstmtUpdate.setFormOfUse(2,

            pstmtUpdate.setString(2, p_networkStockTxnVO.getSecondApprovedRemarks());

            pstmtUpdate.setString(3, p_networkStockTxnVO.getCancelledBy());
            pstmtUpdate.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getCancelledOn()));
            pstmtUpdate.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getModifiedOn()));
            pstmtUpdate.setString(6, p_networkStockTxnVO.getTxnStatus());
            pstmtUpdate.setString(7, p_networkStockTxnVO.getTxnNo());
            pstmtUpdate.setString(8, p_networkStockTxnVO.getNetworkCode());
            pstmtUpdate.setString(9, p_networkStockTxnVO.getNetworkFor());
            final boolean modified = this.isRecordModified(p_con, p_networkStockTxnVO.getLastModifiedTime(), p_networkStockTxnVO.getTxnNo());
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            } else {
                cancelStockTransaction = pstmtUpdate.executeUpdate();
                cancelStockTransaction = BTSLUtil.getInsertCount(cancelStockTransaction); 
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[cancelStockTransaction]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[cancelStockTransaction]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting count = " + cancelStockTransaction);
            }
        }
        return cancelStockTransaction;
    }

    /**
     * Method updateNetworkStock.
     * This method is used to update the network stock it may insert new record
     * or may be update the existin record
     * if record exist.
     * 
     * @param p_con
     *            Connection
     * @param p_networkStockTxnVO
     *            NetworkStockTxnVO
     * @param stockLocationList
     *            ArrayList
     * @return int
     * @throws BTSLBaseException
     */
    public int updateNetworkStock(Connection p_con, NetworkStockTxnVO p_networkStockTxnVO, ArrayList stockLocationList) throws BTSLBaseException {
        final String methodName = "updateNetworkStock";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered :: NetworkCode = ");
        	msg.append(p_networkStockTxnVO.getNetworkCode());
        	msg.append(", List Size = ");
        	msg.append(p_networkStockTxnVO.getNetworkStockTxnItemsList().size());
        	msg.append(", NetworkFor = ");
        	msg.append(p_networkStockTxnVO.getNetworkFor());
        	msg.append(", Entry Type = ");
        	msg.append(p_networkStockTxnVO.getEntryType());
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtUpdate = null;
        NetworkStockVO networkStockVO = null;
        ResultSet rs = null;
        int count = 0;
        long stockCreated = 0;
      
        long stock = 0;
        long approvedQuantity = 0;
        long stockReturned = 0;
        long stockSold = 0;
        final StringBuilder selectStrBuff = new StringBuilder();

        selectStrBuff.append("SELECT * ");
        selectStrBuff.append("FROM network_stocks ");

        // DB220120123for update WITH RS
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            selectStrBuff.append("WHERE network_code = ? AND product_code = ? AND network_code_for = ? and wallet_type = ? FOR UPDATE WITH RS ");
        } else {
            selectStrBuff.append("WHERE network_code = ? AND product_code = ? AND network_code_for = ? and wallet_type = ? FOR UPDATE");
        }

        // Sandeep Goel ID NS001
        // adding the default value 0 for the stock_returned and stock_sold
        // column which comes blank
        // in the history table for the first record

        final StringBuilder insertStrBuff = new StringBuilder();
        insertStrBuff.append("INSERT INTO network_stocks(network_code,network_code_for,product_code,WALLET_TYPE,WALLET_CREATED, ");
        insertStrBuff.append("WALLET_RETURNED,WALLET_BALANCE,WALLET_SOLD,LAST_TXN_NO,LAST_TXN_TYPE,LAST_TXN_BALANCE, ");
        insertStrBuff.append("PREVIOUS_BALANCE,MODIFIED_BY,MODIFIED_ON,CREATED_ON,CREATED_BY,DAILY_STOCK_UPDATED_ON) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

        final String updateStrBuff = networkStockWebQry.updateNetworkStockQry();

        if (_log.isDebugEnabled()) {
            _log.debug("updateNetworkStock ", "Select Query=" + selectStrBuff);
            _log.debug("updateNetworkStock ", "Insert Query=" + insertStrBuff);
            _log.debug("updateNetworkStock ", "Update Query=" + updateStrBuff);
        }
        try {
            NetworkStockTxnItemsVO networkStockTxnItemsVO = null;
            final ArrayList stockItemList = p_networkStockTxnVO.getNetworkStockTxnItemsList();
            pstmtSelect = p_con.prepareStatement(selectStrBuff.toString());
            pstmtUpdate = p_con.prepareStatement(updateStrBuff.toString());
            pstmtInsert = p_con.prepareStatement(insertStrBuff.toString());
            int stockItemListSize = stockItemList.size();
            for (int i = 0; i < stockItemListSize; i++) {
                networkStockVO = new NetworkStockVO();
                stockCreated = 0;
                stock = 0;
                stockReturned = 0;
                stockSold = 0;
                networkStockTxnItemsVO = (NetworkStockTxnItemsVO) stockItemList.get(i);
                if (PretupsI.NETWORK_STOCK_TRANSACTION_DEDUCTION.equals(p_networkStockTxnVO.getEntryType())) {
                    approvedQuantity = -networkStockTxnItemsVO.getApprovedQuantity();
                } else {
                    approvedQuantity = networkStockTxnItemsVO.getApprovedQuantity();
                }
                pstmtSelect.setString(1, p_networkStockTxnVO.getNetworkCode());
                pstmtSelect.setString(2, networkStockTxnItemsVO.getProductCode());
                pstmtSelect.setString(3, p_networkStockTxnVO.getNetworkFor());
                pstmtSelect.setString(4, p_networkStockTxnVO.getTxnWallet());
                rs = pstmtSelect.executeQuery();
                pstmtSelect.clearParameters();
                if (rs.next()) {

                    stockCreated = rs.getLong("WALLET_CREATED");
                    stock = rs.getLong("WALLET_BALANCE");
                    stockReturned = rs.getLong("WALLET_RETURNED");
                    stockSold = rs.getLong("WALLET_SOLD");

                    if (PretupsI.NETWORK_STOCK_TRANSACTION_CREATION.equals(p_networkStockTxnVO.getEntryType())) {
                        pstmtUpdate.setLong(1, approvedQuantity);
                        networkStockVO.setWalletCreated((long) approvedQuantity + stockCreated);
                    } else if (PretupsI.NETWORK_STOCK_TRANSACTION_DEDUCTION.equals(p_networkStockTxnVO.getEntryType())) {
                        pstmtUpdate.setLong(1, approvedQuantity);
                        networkStockVO.setWalletCreated(stockCreated + (long) approvedQuantity);
                    } else {
                        pstmtUpdate.setDouble(1, 0);
                        networkStockVO.setWalletCreated(stockCreated);
                    }
                    pstmtUpdate.setLong(2, approvedQuantity);
                    pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getModifiedOn()));
                    pstmtUpdate.setString(4, p_networkStockTxnVO.getModifiedBy());
                    pstmtUpdate.setString(5, p_networkStockTxnVO.getTxnNo());
                    pstmtUpdate.setString(6, p_networkStockTxnVO.getTxnType());
                    pstmtUpdate.setLong(7, approvedQuantity);
                    if (PretupsI.NETWORK_STOCK_TRANSACTION_RETURN.equals(p_networkStockTxnVO.getEntryType())) {
                        pstmtUpdate.setDouble(8, approvedQuantity);
                        networkStockVO.setWalletReturned((long) (stockReturned + approvedQuantity));
                    } else {
                        pstmtUpdate.setDouble(8, 0);
                        networkStockVO.setWalletReturned((long) stockReturned);
                    }

                    // for the logger


                    pstmtUpdate.setString(9, p_networkStockTxnVO.getNetworkCode());
                    pstmtUpdate.setString(10, networkStockTxnItemsVO.getProductCode());
                    pstmtUpdate.setString(11, p_networkStockTxnVO.getNetworkFor());
                    pstmtUpdate.setString(12, p_networkStockTxnVO.getTxnWallet());
                    count += pstmtUpdate.executeUpdate();

                    pstmtUpdate.clearParameters();
                    networkStockVO.setNetworkCode(p_networkStockTxnVO.getNetworkCode());
                    networkStockVO.setProductCode(networkStockTxnItemsVO.getProductCode());
                    networkStockVO.setWalletBalance((long) (approvedQuantity + stock));
                    networkStockVO.setModifiedBy(p_networkStockTxnVO.getModifiedBy());
                    networkStockVO.setModifiedOn(p_networkStockTxnVO.getModifiedOn());
                    networkStockVO.setCreatedBy(p_networkStockTxnVO.getCreatedBy());
                    networkStockVO.setCreatedOn(p_networkStockTxnVO.getCreatedOn());

                    networkStockVO.setLastTxnNum(p_networkStockTxnVO.getTxnNo());
                    networkStockVO.setLastTxnType(p_networkStockTxnVO.getTxnType());
                    networkStockVO.setPreviousBalance((long) stock);// previous
                    // stock
                    networkStockVO.setWalletSold(stockSold);
                    networkStockVO.setNetworkCodeFor(p_networkStockTxnVO.getNetworkFor());
                    // commented since it will over write the previous value

                } else {
                    pstmtInsert.setString(1, p_networkStockTxnVO.getNetworkCode());
                    pstmtInsert.setString(2, p_networkStockTxnVO.getNetworkFor());
                    pstmtInsert.setString(3, networkStockTxnItemsVO.getProductCode());
                    pstmtInsert.setString(4, p_networkStockTxnVO.getTxnWallet());
                    pstmtInsert.setDouble(5, approvedQuantity);
                    pstmtInsert.setInt(6, 0); // stock_returned
                    pstmtInsert.setDouble(7, approvedQuantity); // wallet_balance
                    pstmtInsert.setInt(8, 0); // stock_sold
                    pstmtInsert.setString(9, p_networkStockTxnVO.getTxnNo());
                    pstmtInsert.setString(10, p_networkStockTxnVO.getTxnType());
                    pstmtInsert.setDouble(11, approvedQuantity); // last_txn_stock
                    pstmtInsert.setInt(12, 0); // previous_stock
                    pstmtInsert.setString(13, p_networkStockTxnVO.getModifiedBy());
                    pstmtInsert.setTimestamp(14, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getModifiedOn()));
                    pstmtInsert.setTimestamp(15, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getCreatedOn()));
                    pstmtInsert.setString(16, p_networkStockTxnVO.getCreatedBy());
                    pstmtInsert.setTimestamp(17, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getCreatedOn())); // daily_stock_updated_on

                    count += pstmtInsert.executeUpdate();

                    pstmtInsert.clearParameters();

                    networkStockVO.setNetworkCode(p_networkStockTxnVO.getNetworkCode());
                    networkStockVO.setProductCode(networkStockTxnItemsVO.getProductCode());
                    networkStockVO.setWalletCreated((long) (approvedQuantity + stockCreated));
                    networkStockVO.setWalletBalance((long) (approvedQuantity + stock));
                    networkStockVO.setModifiedBy(p_networkStockTxnVO.getModifiedBy());
                    networkStockVO.setModifiedOn(p_networkStockTxnVO.getModifiedOn());
                    networkStockVO.setCreatedBy(p_networkStockTxnVO.getCreatedBy());
                    networkStockVO.setCreatedOn(p_networkStockTxnVO.getCreatedOn());

                    networkStockVO.setLastTxnNum(p_networkStockTxnVO.getTxnNo());
                    networkStockVO.setLastTxnType(p_networkStockTxnVO.getTxnType());
                    networkStockVO.setPreviousBalance((long) stock);// previous
                    // stock
                    networkStockVO.setWalletReturned((long) stockReturned);
                    networkStockVO.setWalletSold(stockSold);
                    networkStockVO.setNetworkCodeFor(p_networkStockTxnVO.getNetworkFor());
                    // commented since it will over write the previous value

                }// end of else
                stockLocationList.add(networkStockVO);
            }// end of for loop
        }
        catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[updateNetworkStock]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[updateNetworkStock]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting count = " + count);
            }
        }
        return count;
    }

    /**
     * Method updateLevel1NetworkStockTransaction.
     * This method of the level 1 approval updation
     * 
     * @param p_con
     *            Connection
     * @param p_networkStockTxnVO
     *            NetworkStockTxnVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateLevel1NetworkStockTransaction(Connection p_con, NetworkStockTxnVO p_networkStockTxnVO) throws BTSLBaseException {
        final String methodName = "updateLevel1NetworkStockTransaction";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered subscriberVO : " + p_networkStockTxnVO);
        }

        PreparedStatement psmtUpdate = null;
        int updateCount = 0;
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append(" UPDATE network_stock_transactions SET ");
            strBuff.append(" txn_wallet=?,first_approved_remarks=?,approved_quantity=?,first_approved_by=?,first_approved_on=?,");
            strBuff.append(" modified_on=?, modified_by=?,txn_status=?,first_approver_limit=?,txn_mrp=? ");
            strBuff.append(" WHERE txn_no =? AND network_code=? AND network_code_for=? AND ");
            strBuff.append(" (txn_status =? OR txn_status =?) ");
            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "query:" + query);
            }

            // commented for DB2 psmtUpdate =

            psmtUpdate = (PreparedStatement) p_con.prepareStatement(query);
            int i = 0;

            psmtUpdate.setString(++i, p_networkStockTxnVO.getTxnWallet());
            // for multilanguage support


            psmtUpdate.setString(++i, p_networkStockTxnVO.getFirstApprovedRemarks());

            psmtUpdate.setLong(++i, p_networkStockTxnVO.getApprovedQuantity());

            // for multilanguage support


            psmtUpdate.setString(++i, p_networkStockTxnVO.getFirstApprovedBy());

            psmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getFirstApprovedOn()));
            psmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getModifiedOn()));
            psmtUpdate.setString(++i, p_networkStockTxnVO.getModifiedBy());
            psmtUpdate.setString(++i, p_networkStockTxnVO.getTxnStatus());
            psmtUpdate.setLong(++i, p_networkStockTxnVO.getFirstApproverLimit());
            psmtUpdate.setLong(++i, p_networkStockTxnVO.getTxnMrp());
            psmtUpdate.setString(++i, p_networkStockTxnVO.getTxnNo());
            psmtUpdate.setString(++i, p_networkStockTxnVO.getNetworkCode());
            psmtUpdate.setString(++i, p_networkStockTxnVO.getNetworkFor());
            psmtUpdate.setString(++i, PretupsI.NETWORK_STOCK_TXN_STATUS_NEW);
            psmtUpdate.setString(++i, PretupsI.NETWORK_STOCK_TXN_STATUS_APPROVE1);
            final boolean modified = this.isRecordModified(p_con, p_networkStockTxnVO.getLastModifiedTime(), p_networkStockTxnVO.getTxnNo());
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = psmtUpdate.executeUpdate();
            updateCount = BTSLUtil.getInsertCount(updateCount); 
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            updateCount = this.updateNetworkStockItems(p_con, p_networkStockTxnVO.getNetworkStockTxnItemsList(), p_networkStockTxnVO.getTxnNo());
        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[updateLevel1NetworkStockTransaction]",
                "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[updateLevel1NetworkStockTransaction]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting Success :" + updateCount);
            }
        }// end of finally

        return updateCount;

    }

    /**
     * Method updateLevel2NetworkStockTransaction.
     * This method is for update level2 approval
     * 
     * @param p_con
     *            Connection
     * @param p_networkStockTxnVO
     *            NetworkStockTxnVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateLevel2NetworkStockTransaction(Connection p_con, NetworkStockTxnVO p_networkStockTxnVO) throws BTSLBaseException {
        final String methodName = "updateLevel2NetworkStockTransaction";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered subscriberVO : " + p_networkStockTxnVO);
        }

        PreparedStatement psmtUpdate = null;
        int updateCount = 0;
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append(" UPDATE network_stock_transactions SET ");
            strBuff.append(" txn_wallet=?,second_approved_remarks=?,second_approved_by=?,second_approved_on=?,");
            strBuff.append(" modified_on=?, modified_by=?,txn_status=? ");
            strBuff.append(" WHERE txn_no =? AND network_code=? AND network_code_for=? AND ");
            strBuff.append(" txn_status =? ");
            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "query:" + query);
            }

            // commented for DB2 psmtUpdate =

            psmtUpdate = (PreparedStatement) p_con.prepareStatement(query);
            int i = 0;
            psmtUpdate.setString(++i, p_networkStockTxnVO.getTxnWallet());
            // for multilanguage support


            psmtUpdate.setString(++i, p_networkStockTxnVO.getSecondApprovedRemarks());

            // for multilanguage support


            psmtUpdate.setString(++i, p_networkStockTxnVO.getSecondApprovedBy());

            psmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getSecondApprovedOn()));
            psmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_networkStockTxnVO.getModifiedOn()));
            psmtUpdate.setString(++i, p_networkStockTxnVO.getModifiedBy());
            psmtUpdate.setString(++i, p_networkStockTxnVO.getTxnStatus());
            psmtUpdate.setString(++i, p_networkStockTxnVO.getTxnNo());
            psmtUpdate.setString(++i, p_networkStockTxnVO.getNetworkCode());
            psmtUpdate.setString(++i, p_networkStockTxnVO.getNetworkFor());
            psmtUpdate.setString(++i, PretupsI.NETWORK_STOCK_TXN_STATUS_APPROVE1);

            final boolean modified = this.isRecordModified(p_con, p_networkStockTxnVO.getLastModifiedTime(), p_networkStockTxnVO.getTxnNo());
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = psmtUpdate.executeUpdate();
            updateCount = BTSLUtil.getInsertCount(updateCount); 
            // called this method to update the stock field
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            updateCount = this.updateNetworkStockItems(p_con, p_networkStockTxnVO.getNetworkStockTxnItemsList(), p_networkStockTxnVO.getTxnNo());
        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[updateLevel2NetworkStockTransaction]",
                "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[updateLevel2NetworkStockTransaction]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting Success :" + updateCount);
            }
        }// end of finally

        return updateCount;

    }

    /**
     * Method isRecordModified.
     * This method is used to check that is the record modified during the
     * processing.
     * 
     * @param p_con
     *            Connection
     * @param p_oldlastModified
     *            Long
     * @param p_txnNo
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    private boolean isRecordModified(Connection p_con, long p_oldlastModified, String p_txnNo) throws BTSLBaseException {
        final String methodName = "isRecordModified";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered:p_oldlastModified = ");
        	msg.append(p_oldlastModified);
        	msg.append(", p_txnNo = ");
        	msg.append(p_txnNo);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        final StringBuilder sqlRecordModified = new StringBuilder();
        sqlRecordModified.append("SELECT modified_on FROM network_stock_transactions ");
        sqlRecordModified.append("WHERE txn_no=? ");
        java.sql.Timestamp newlastModified = null;
        if (p_oldlastModified == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY=" + sqlRecordModified);
            }
            final String query = sqlRecordModified.toString();
            pstmtSelect = p_con.prepareStatement(query);
            pstmtSelect.setString(1, p_txnNo);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                newlastModified = rs.getTimestamp("modified_on");
            }
            // The record is not present because the record is modified by other
            // person and the
            // modification is done on the value of the primary key.
            else {
                modified = true;
                return true;
            }
            if (newlastModified.getTime() != p_oldlastModified) {
                modified = true;
            }
        }// end of try
        catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isRecordModified]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[isRecordModified]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exititng:modified=" + modified);
            }
        }// end of finally
        return modified;
    }// end recordModified

    /**
     * Method loadViewStockList.
     * 
     * @param p_con
     *            Connection
     * @param p_stockNo
     *            String
     * @param p_status
     *            String
     * @param p_networkCode
     *            String
     * @param p_fromDate
     *            Date
     * @param p_toDate
     *            Date
     * @param p_networkFor
     *            String
     * @param p_stockEntryType
     *            String
     * @param p_networkForType
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadViewStockList(Connection p_con, String p_stockNo, String p_status, String p_networkCode, Date p_fromDate, Date p_toDate, String p_networkFor, String p_stockEntryType, String p_networkForType) throws BTSLBaseException {
        final String methodName = "loadViewStockList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_stockNo = ");
        	msg.append(p_stockNo);
        	msg.append(", p_status = ");
        	msg.append(p_status);
        	msg.append(", p_networkCode = ");
        	msg.append(p_networkCode);
        	msg.append(", p_fromDate = ");
        	msg.append(p_fromDate);        	
        	msg.append(", p_toDate = ");
        	msg.append(p_toDate);
        	msg.append(", p_networkFor = ");
        	msg.append(p_networkFor);
        	msg.append(", p_stockEntryType = ");
        	msg.append(p_stockEntryType);        	
        	msg.append(", p_networkForType = ");
        	msg.append(p_networkForType);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList stockList = new ArrayList();

        
        final String query = networkStockWebQry.loadViewStockListQry(p_stockNo, p_status, p_networkCode, p_networkForType, p_stockEntryType);
        if (_log.isDebugEnabled()) {
            _log.info(methodName, "QUERY query=" + query);
        }
        try {
            pstmtSelect = p_con.prepareStatement(query);
            int i = 1;
            pstmtSelect.setString(i++, p_networkCode);
            pstmtSelect.setString(i++, p_networkFor);
            pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            if (!PretupsI.ALL.equals(p_stockNo)) {
                pstmtSelect.setString(i++, p_stockNo);
            } else {
                if (!PretupsI.ALL.equals(p_stockEntryType)) {
                    pstmtSelect.setString(i++, p_stockEntryType);
                }
                if (PretupsI.NETWORK_STOCK_TRANSACTION_CREATION.equals(p_stockEntryType) && !PretupsI.ALL.equals(p_status)) {
                    pstmtSelect.setString(i++, p_status);
                }
            }
            pstmtSelect.setString(i++, p_networkCode);
            pstmtSelect.setString(i++, p_networkForType);
            pstmtSelect.setString(i++, PretupsI.TRANSFER_STATUS);
            rs = pstmtSelect.executeQuery();
            NetworkStockTxnVO stockVO = null;
            while (rs.next()) {
                stockVO = new NetworkStockTxnVO();
                stockVO.setTxnWallet(rs.getString("txn_wallet"));
                stockVO.setTxnNo(rs.getString("txn_no"));
                stockVO.setTxnMrp(rs.getLong("txn_mrp"));
                stockVO.setTxnMrpStr(PretupsBL.getDisplayAmount(rs.getLong("txn_mrp")));
                stockVO.setTxnDate(rs.getDate("txn_date"));
                stockVO.setTxnDateStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("txn_date"))));
                stockVO.setTxnStatusName(rs.getString("status_name"));
                stockVO.setTxnStatus(rs.getString("txn_status"));
                stockVO.setEntryType(rs.getString("entry_type"));
                stockVO.setReferenceNo(rs.getString("reference_no"));
                stockVO.setFirstApprovedBy(rs.getString("first_approver"));
                if (BTSLUtil.isNullString(rs.getString("second_approver"))){
                	stockVO.setSecondApprovedBy(rs.getString("first_approver"));
                }else{
                	stockVO.setSecondApprovedBy(rs.getString("second_approver"));
                }
                stockVO.setFirstApprovedOn(rs.getDate("first_approved_on"));
                if (BTSLUtil.isNullString(rs.getString("second_approved_on"))){
                	stockVO.setSecondApprovedOn(rs.getDate("first_approved_on"));
                }else{
                	stockVO.setSecondApprovedOn(rs.getDate("second_approved_on"));
                }
                stockVO.setCreatedOn(rs.getDate("created_on"));
                stockVO.setNetworkName(rs.getString("network_name"));
                stockVO.setFirstApprovedRemarks(rs.getString("first_approved_remarks"));
                stockVO.setSecondApprovedRemarks(rs.getString("second_approved_remarks"));
                stockVO.setInitiaterName(rs.getString("initiator"));
                stockVO.setInitiaterRemarks(rs.getString("initiater_remarks"));
                stockVO.setCancelledBy(rs.getString("CANCELER"));
                stockVO.setCancelledOn(rs.getDate("cancelled_on"));
                stockVO.setNetworkFor(rs.getString("net_for"));
                stockVO.setNetworkForName(rs.getString("net_for_name"));
                stockList.add(stockVO);
            }
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[loadViewStockList]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[loadViewStockList]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting stockList size=" + stockList.size());
            }
        }
        return stockList;
    }// end of loadViewStockList

    /**
     * Method loadStockDeductionList.
     * This method loads the list of the transaction to approve the order
     * 
     * @param p_con
     *            Connection
     * @param p_status
     *            String
     * @param p_networkCode
     *            String
     * @param p_networkType
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadStockDeductionList(Connection p_con, String p_status, String p_networkCode, String p_networkType) throws BTSLBaseException {
        final String methodName = "loadStockDeductionList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_status = ");
        	msg.append(p_status);
        	msg.append(", p_networkCode = ");
        	msg.append(p_networkCode);       	
        	msg.append(", p_networkType = ");
        	msg.append(p_networkType);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList stockList = new ArrayList();
        String strBuff=networkStockWebQry.loadStockDeductionListQry(p_status);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + strBuff);
        }
        try {
            int i = 0;
            pstmtSelect = p_con.prepareStatement(strBuff);
            pstmtSelect.setString(++i, p_networkCode);
            pstmtSelect.setString(++i, p_networkCode);
            pstmtSelect.setString(++i, p_networkCode);
            pstmtSelect.setString(++i, p_networkType);
            pstmtSelect.setString(++i, PretupsI.TRANSFER_STATUS);

            rs = pstmtSelect.executeQuery();
            NetworkStockTxnVO networkStockTxnVO = null;
            while (rs.next()) {
                networkStockTxnVO = new NetworkStockTxnVO();
                networkStockTxnVO.setTxnNo(rs.getString("TXNNO"));
                networkStockTxnVO.setApprovedQuantity(rs.getLong("APPROVED_QUANTITY"));
                networkStockTxnVO.setTxnMrp(rs.getLong("TXNMRP"));
                networkStockTxnVO.setTxnMrpStr(PretupsBL.getDisplayAmount(rs.getLong("TXNMRP")));
                networkStockTxnVO.setInitiatedBy(rs.getString("requester"));
                networkStockTxnVO.setReferenceNo(rs.getString("REFNO"));
                networkStockTxnVO.setTxnDate(rs.getDate("TRANSACTIONDATE"));
                networkStockTxnVO.setInitiaterName(rs.getString("REQUESTER_NAME"));
                networkStockTxnVO.setFirstApprovedBy(rs.getString("FANAME"));
                networkStockTxnVO.setSecondApprovedBy(rs.getString("SANAME"));
                networkStockTxnVO.setFirstApprovedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("FIRSTAPPROVEDON")));
                networkStockTxnVO.setSecondApprovedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("SECONDAPPROVEDON")));
                networkStockTxnVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("CREATEDON")));
                networkStockTxnVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("MODIFIEDON")));
                networkStockTxnVO.setLastModifiedTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("MODIFIEDON")).getTime());
                networkStockTxnVO.setTxnStatus(rs.getString("STOCK_TRANSACTIONSTATUS"));
                networkStockTxnVO.setTxnStatusName(rs.getString("STATUSNAME"));
                networkStockTxnVO.setNetworkName(rs.getString("NETWORKNAME"));
                networkStockTxnVO.setNetworkFor(rs.getString("NETWORKFOR"));
                networkStockTxnVO.setNetworkForName(rs.getString("NETWORKFORNAME"));
                networkStockTxnVO.setInitiaterRemarks(rs.getString("REMARKS"));
                networkStockTxnVO.setFirstApprovedRemarks(rs.getString("FIRST_APPROVER_REMARKS"));
                networkStockTxnVO.setSecondApprovedRemarks(rs.getString("SECOND_APPROVER_REMARKS"));
                networkStockTxnVO.setTxnWallet(rs.getString("TXN_WALLET"));
                stockList.add(networkStockTxnVO);
            }
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[loadStockDeductionList]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch

        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[loadStockDeductionList]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting orderList size=" + stockList.size());
            }
        }
        return stockList;
    }
    public ArrayList loadStockDeductionListNew(Connection p_con, String p_status, String p_networkCode, String p_networkType) throws BTSLBaseException {
        final String methodName = "loadStockDeductionList";
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList stockList = new ArrayList();
        String strBuff=networkStockWebQry.loadStockDeductionListQry(p_status);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + strBuff);
        }
        try {
            int i = 0;
            pstmtSelect = p_con.prepareStatement(strBuff);
            pstmtSelect.setString(++i, p_networkCode);
            pstmtSelect.setString(++i, p_networkCode);
            pstmtSelect.setString(++i, p_networkCode);
            pstmtSelect.setString(++i, p_networkType);
            pstmtSelect.setString(++i, PretupsI.TRANSFER_STATUS);

            rs = pstmtSelect.executeQuery();
            NetworkStockTxnVO1 networkStockTxnVO = null;
            while (rs.next()) {
                networkStockTxnVO = new NetworkStockTxnVO1();
                networkStockTxnVO.setTxnNo(rs.getString("TXNNO"));
                networkStockTxnVO.setApprovedQuantity(rs.getLong("APPROVED_QUANTITY"));
                networkStockTxnVO.setTxnMrp(rs.getLong("TXNMRP"));
                networkStockTxnVO.setTxnMrpStr(PretupsBL.getDisplayAmount(rs.getLong("TXNMRP")));
                networkStockTxnVO.setInitiatedBy(rs.getString("requester"));
                networkStockTxnVO.setReferenceNo(rs.getString("REFNO"));
                networkStockTxnVO.setTxnDate(rs.getDate("TRANSACTIONDATE"));
                networkStockTxnVO.setInitiaterName(rs.getString("REQUESTER_NAME"));
                networkStockTxnVO.setFirstApprovedBy(rs.getString("FANAME"));
                networkStockTxnVO.setSecondApprovedBy(rs.getString("SANAME"));
                networkStockTxnVO.setFirstApprovedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("FIRSTAPPROVEDON")));
                networkStockTxnVO.setSecondApprovedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("SECONDAPPROVEDON")));
                networkStockTxnVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("CREATEDON")));
                networkStockTxnVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("MODIFIEDON")));
                networkStockTxnVO.setLastModifiedTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("MODIFIEDON")).getTime());
                networkStockTxnVO.setTxnStatus(rs.getString("STOCK_TRANSACTIONSTATUS"));
                networkStockTxnVO.setTxnStatusName(rs.getString("STATUSNAME"));
                networkStockTxnVO.setNetworkName(rs.getString("NETWORKNAME"));
                networkStockTxnVO.setNetworkFor(rs.getString("NETWORKFOR"));
                networkStockTxnVO.setNetworkForName(rs.getString("NETWORKFORNAME"));
                networkStockTxnVO.setInitiaterRemarks(rs.getString("REMARKS"));
                networkStockTxnVO.setFirstApprovedRemarks(rs.getString("FIRST_APPROVER_REMARKS"));
                networkStockTxnVO.setSecondApprovedRemarks(rs.getString("SECOND_APPROVER_REMARKS"));
                networkStockTxnVO.setTxnWallet(rs.getString("TXN_WALLET"));
                stockList.add(networkStockTxnVO);
            }
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting orderList size=" + stockList.size());
            }
        }
        return stockList;
    }

    /**
     * update items to Networkstock items
     * This method is for updation of the network stock items at the time of the
     * first level approval of the order
     * 
     * @param p_con
     * @param p_networkStockTxnItemsVOList
     * @param p_txnNum
     * @return int
     * @throws BTSLBaseException
     */
    private int updateNetworkStockItems(Connection p_con, ArrayList p_networkStockTxnItemsVOList, String p_txnNum) throws BTSLBaseException {
        final String methodName = "updateNetworkStockItems";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: NetworkStockTxnItemsVOList Size = ");
        	msg.append(p_networkStockTxnItemsVOList.size());
        	msg.append(", TxnNum = ");
        	msg.append(p_txnNum);       	
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append(" UPDATE network_stock_trans_items SET ");
            strBuff.append(" approved_quantity=?, stock=?, mrp=?, amount=?, date_time=? ");
            strBuff.append(" WHERE txn_no=? AND product_code=? ");

            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Update query:" + query);
            }
            psmtUpdate = p_con.prepareStatement(query);

            NetworkStockTxnItemsVO networkStockTxnItemsVO = null;
            int m = 0;
            for (int i = 0, k = p_networkStockTxnItemsVOList.size(); i < k; i++) {
                networkStockTxnItemsVO = (NetworkStockTxnItemsVO) p_networkStockTxnItemsVOList.get(i);
                m = 0;
                psmtUpdate.setLong(++m, networkStockTxnItemsVO.getApprovedQuantity());
                psmtUpdate.setLong(++m, networkStockTxnItemsVO.getWalletbalance());
                psmtUpdate.setLong(++m, networkStockTxnItemsVO.getMrp());
                psmtUpdate.setLong(++m, networkStockTxnItemsVO.getAmount());
                // Added on 07/02/08 for addition of new date_time column in the
                // table NETWORK_STOCK_TRANS_ITEMS.
                psmtUpdate.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnItemsVO.getDateTime()));
                psmtUpdate.setString(++m, p_txnNum);
                psmtUpdate.setString(++m, networkStockTxnItemsVO.getProductCode());

                updateCount = psmtUpdate.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with updatation in partitioned table in postgres
                psmtUpdate.clearParameters();
                if (updateCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }
        }// end of try
        catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[updateNetworkStockItems]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[updateNetworkStockItems]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting Success :" + updateCount);
            }
        }// end of finally

        return updateCount;
    }

}
