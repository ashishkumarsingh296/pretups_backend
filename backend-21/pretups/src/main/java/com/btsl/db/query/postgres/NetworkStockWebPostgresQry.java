package com.btsl.db.query.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.web.pretups.networkstock.businesslogic.NetworkStockWebDAO;
import com.web.pretups.networkstock.businesslogic.NetworkStockWebQry;

public class NetworkStockWebPostgresQry implements NetworkStockWebQry{
	private static Log log = LogFactory.getLog(NetworkStockWebDAO.class.getName());
	@Override
	public PreparedStatement loadProductsForStockQry(Connection pcon, String pnetworkCode, String pnetworkFor, String pmodule) throws SQLException{
		
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT P.product_code ,P.product_name, P.unit_value, NS.wallet_type,NS.wallet_balance  ");

        strBuff.append("FROM products P,network_product_mapping npm left join network_stocks NS on ");
        strBuff.append(" (NS.product_code=NPM.product_code AND NS.network_code=NPM.network_code and NS.network_code_for = ?) ");
        strBuff.append(" WHERE P.status = 'Y' AND P.product_code = NPM.product_code ");
        strBuff.append(" AND P.module_code=case ? when ? then P.module_code else ? end");
        strBuff.append(" AND NPM.network_code = ? AND NPM.status ='Y'  ");
        strBuff.append(" ORDER BY product_name ");
        if (log.isDebugEnabled()) {
            log.debug(" loadProductsForStock", "Query :: " + strBuff.toString());
        }
        PreparedStatement pstmtSelect = pcon.prepareStatement(strBuff.toString());
        int i = 0;
        pstmtSelect.setString(++i, pnetworkFor);
        pstmtSelect.setString(++i, pmodule);
        pstmtSelect.setString(++i, PretupsI.ALL);
        pstmtSelect.setString(++i, pmodule);
        pstmtSelect.setString(++i, pnetworkCode);
        
        return pstmtSelect;
	}
	@Override
	public String loadStockTransactionListQry(String pstatus){
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT ST.approved_quantity, ST.network_code_for NETWORKFOR, ST.TXN_WALLET, ");
        strBuff.append("ST.first_approved_remarks FIRST_APPROVER_REMARKS,ST.second_approved_remarks SECOND_APPROVER_REMARKS,ST.requested_quantity REQUESTED_QUANTITY, ");
        strBuff.append("ST.approved_quantity APPROVED_QUANTITY, ST.initiater_remarks REMARKS, ");
        strBuff.append("ST.initiated_by REQUESTER,ST.txn_no TXNNO,coalesce(ST.reference_no,' ') REFNO,ST.txn_date TRANSACTIONDATE, ");
        strBuff.append("ST.first_approved_by FIRSTAPPROVEDBY,ST.first_approved_on FIRSTAPPROVEDON, ");
        strBuff.append("ST.second_approved_on SECONDAPPROVEDON,ST.requested_quantity REQUESTER_QUANTITY, ");
        strBuff.append("ST.txn_mrp TXNMRP,ST.txn_status STOCK_TRANSACTIONSTATUS,SUD.user_name REQUESTER_NAME, ");
        strBuff.append("ST.created_on CREATEDON,ST.modified_on MODIFIEDON,coalesce(FU.user_name,' ') FANAME,");
        strBuff.append("coalesce(SU.user_name,' ') SANAME,L.lookup_name STATUSNAME,LOC.network_name NETWORKNAME, ");
        strBuff.append("LOC1.network_name NETWORKFORNAME  ");
        strBuff.append("FROM network_stock_transactions ST left join users FU on FU.user_id=ST.first_approved_by left join users SU on SU.user_id=ST.second_approved_by,users SUD,lookups L ,networks LOC,networks LOC1  ");
        strBuff.append("WHERE ST.txn_status IN (" + pstatus + ") ");
        strBuff.append("AND ST.approved_quantity > 0 ");
        strBuff.append("AND ST.requested_quantity > 0 ");
        strBuff.append("AND ST.initiated_by=SUD.user_id ");
        strBuff.append("AND (SUD.status='Y' OR SUD.status is null) ");
        strBuff.append("AND ST.network_code= case ? when 'ALL' then ST.network_code else ? end ");
        strBuff.append("AND LOC1.network_type = case LOC1.network_code when ? then LOC1.network_type else ? end ");
        strBuff.append("AND ST.txn_status=L.lookup_code ");
        strBuff.append("AND ST.network_code=LOC.network_code ");
        strBuff.append("AND ST.network_code_for=LOC1.network_code  AND L.lookup_type=? ");
        strBuff.append("ORDER BY SUD.user_name,ST.txn_date DESC ");
        return strBuff.toString();
	}
	
	@Override
	public PreparedStatement loadStockItemListQry(Connection p_con, String p_txnNo, String p_networkCode, String p_networkFor)throws SQLException{
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT P.unit_value NEWMRP,STI.product_code PID,P.product_name PNAME, ");

        strBuff.append("ls.wallet_balance stock, ls.wallet_type,");
        strBuff.append("STI.txn_no ONO, STI.s_no SNO, STI.required_quantity RQTY,STI.approved_quantity AQTY,amount, STI.date_time, NST.txn_wallet ");
        strBuff.append("FROM products P, network_stocks ls right outer join network_stock_trans_items STI on STI.product_code = ls.product_code  ");
        strBuff.append(" and ls.network_code = ? and ls.network_code_for = case ? when 'ALL' then ls.network_code_for else ? end ");
        strBuff.append(", network_stock_transactions nst ");
        strBuff.append("WHERE STI.txn_no= ? ");
        strBuff.append("AND NST.txn_no=STI.txn_no ");
        strBuff.append("AND STI.product_code=P.product_code ");
        strBuff.append("ORDER BY STI.s_no  ");
        if (log.isDebugEnabled()) {
            log.debug("loadStockItemListQry", " QUERY sqlInsert=" + strBuff.toString());
        }
        PreparedStatement pstmtSelect = p_con.prepareStatement(strBuff.toString());
        
        pstmtSelect.setString(1, p_networkCode);
        pstmtSelect.setString(2, p_networkFor);
        pstmtSelect.setString(3, p_networkFor);
        pstmtSelect.setString(4, p_txnNo);
        return pstmtSelect;
	}
	@Override
	public String loadViewStockListQry(String p_stockNo, String p_status, String p_networkCode,String p_networkFor, String p_stockEntryType){
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT NSTXN.txn_wallet, NSTXN.txn_no, NET.network_name, NETFOR.network_name NET_for_name, ");
        strBuff.append("NSTXN.network_code_for NET_for,NSTXN.reference_no,NSTXN.txn_date,NSTXN.requested_quantity, ");
        strBuff.append("NSTXN.approved_quantity,NSTXN.initiater_remarks, NSTXN.first_approved_remarks, ");
        strBuff.append("NSTXN.second_approved_remarks, approver1.user_name first_approver, ");
        strBuff.append("APPROVER2.user_name second_approver, NSTXN.first_approved_on, ");
        strBuff.append("NSTXN.second_approved_on, NSTXN.cancelled_by, NSTXN.cancelled_on, ");
        strBuff.append("CREATOR.user_name CREATOR, NSTXN.created_on, NSTXN.modified_on, ");
        strBuff.append("LKP.lookup_name status_name,  txn_status, NSTXN.entry_type, NSTXN.txn_type, ");
        strBuff.append("INITIATOR.user_name INITIATOR, NSTXN.first_approver_limit, SEC.user_name distributor_name, ");
        strBuff.append("NSTXN.txn_mrp, CANCELER.user_name CANCELER ");
        strBuff.append("FROM network_stock_transactions NSTXN left join users INITIATOR on NSTXN.initiated_by= INITIATOR.user_id left join users APPROVER1 on  NSTXN.first_approved_by = APPROVER1.user_id ");
        strBuff.append(" left join users APPROVER2 on NSTXN.second_approved_by = APPROVER2.user_id left join users CREATOR on NSTXN.created_by = CREATOR.user_id ");
        strBuff.append(" left join users CANCELER on NSTXN.cancelled_by = CANCELER.user_id left join users SEC on NSTXN.user_id = SEC.user_id ");
        strBuff.append(",networks NET,  ");
        strBuff.append("  networks NETFOR, lookups LKP ");
        strBuff.append("WHERE NSTXN.network_code=?  ");
        if (!PretupsI.ALL.equals(p_networkFor)) {
            strBuff.append("AND NSTXN.network_code_for =? ");
        }
        strBuff.append("AND (date_trunc('day',NSTXN.txn_date::TIMESTAMP) >= ? AND date_trunc('day',NSTXN.txn_date::TIMESTAMP)<= ?) ");

        if (!PretupsI.ALL.equals(p_stockNo)) {
            strBuff.append("AND NSTXN.txn_no=? ");
        } else {
            if (!PretupsI.ALL.equals(p_stockEntryType)) {
                strBuff.append("AND NSTXN.entry_type=? ");
            }
            if (PretupsI.NETWORK_STOCK_TRANSACTION_CREATION.equals(p_stockEntryType) && !PretupsI.ALL.equals(p_status)) {
                strBuff.append("AND NSTXN.txn_status= ? ");
            }
        }
        strBuff.append("AND NETFOR.network_TYPE = case NETFOR.network_code when ? then NETFOR.network_TYPE else ? end ");
        strBuff.append("AND NSTXN.network_code=NET.network_code ");
        strBuff.append("AND NSTXN.network_code_for=NETFOR.network_code ");
        strBuff.append("AND LKP.lookup_code =NSTXN.txn_status AND LKP.lookup_type=?  ");
        strBuff.append("ORDER BY NSTXN.modified_on, NSTXN.txn_no ");
        return strBuff.toString();
	}
	@Override
	public String loadStockDeductionListQry( String p_status){
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT ST.approved_quantity, ST.network_code_for NETWORKFOR, ST.TXN_WALLET, ");
        strBuff.append("ST.first_approved_remarks FIRST_APPROVER_REMARKS,ST.second_approved_remarks SECOND_APPROVER_REMARKS,ST.requested_quantity REQUESTED_QUANTITY, ");
        strBuff.append("ST.approved_quantity APPROVED_QUANTITY, ST.initiater_remarks REMARKS, ");
        strBuff.append("ST.initiated_by REQUESTER,ST.txn_no TXNNO,coalesce(ST.reference_no,' ') REFNO,ST.txn_date TRANSACTIONDATE, ");
        strBuff.append("ST.first_approved_by FIRSTAPPROVEDBY,ST.first_approved_on FIRSTAPPROVEDON, ");
        strBuff.append("ST.second_approved_on SECONDAPPROVEDON,ST.requested_quantity REQUESTER_QUANTITY, ");
        strBuff.append("ST.txn_mrp TXNMRP,ST.txn_status STOCK_TRANSACTIONSTATUS,SUD.user_name REQUESTER_NAME, ");
        strBuff.append("ST.created_on CREATEDON,ST.modified_on MODIFIEDON,coalesce(FU.user_name,' ') FANAME,");
        strBuff.append("coalesce(SU.user_name,' ') SANAME,L.lookup_name STATUSNAME,LOC.network_name NETWORKNAME, ");
        strBuff.append("LOC1.network_name NETWORKFORNAME  ");
        strBuff.append("FROM network_stock_transactions ST left join users FU on ST.first_approved_by=FU.user_id left join users SU on ST.second_approved_by=SU.user_id ,users SUD,lookups L ,networks LOC,networks LOC1  ");
        strBuff.append("WHERE ST.txn_status IN (" + p_status + ") ");
        strBuff.append("AND ST.REQUESTED_QUANTITY < 0 ");
        strBuff.append("AND ST.approved_quantity < 0 ");
        strBuff.append("AND ST.initiated_by=SUD.user_id ");
        strBuff.append("AND (SUD.status='Y' OR SUD.status is null) ");
        strBuff.append("AND ST.network_code= case ? when 'ALL' then ST.network_code else ? end ");
        strBuff.append("AND LOC1.network_type = case  LOC1.network_code when ? then LOC1.network_type else ? end ");
        strBuff.append("AND ST.txn_status=L.lookup_code ");
        strBuff.append("AND ST.network_code=LOC.network_code ");
		strBuff.append("AND ST.network_code_for=LOC1.network_code  AND L.lookup_type=? ");
        strBuff.append("ORDER BY SUD.user_name,ST.txn_date DESC ");
        return strBuff.toString();
	}
	@Override
	public String updateNetworkStockQry() {
		final StringBuilder updateStrBuff = new StringBuilder();

        updateStrBuff.append("UPDATE network_stocks SET WALLET_CREATED = WALLET_CREATED+?, ");
        updateStrBuff.append("PREVIOUS_BALANCE=WALLET_BALANCE, WALLET_BALANCE= WALLET_BALANCE + ?, modified_on = ? , modified_by =?, ");
        updateStrBuff.append("LAST_TXN_NO=?, LAST_TXN_TYPE=?,LAST_TXN_BALANCE=?,WALLET_RETURNED = coalesce(WALLET_RETURNED,0)+? ");
        updateStrBuff.append("WHERE network_code = ? AND product_code = ? AND network_code_for = ? AND  wallet_type = ? ");
		return updateStrBuff.toString();
	}
}

