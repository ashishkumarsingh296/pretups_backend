package com.btsl.pretups.channel.transfer.businesslogic;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.Constants;

public class FOCBatchTransferOracleQry implements FOCBatchTransferQry{

	private Log  log = LogFactory.getLog(this.getClass());

	@Override
	public String loadBatchItemsMapQry(String itemStatus) {
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT fbi.batch_detail_id, c.category_name,c.category_code, fbi.msisdn, fbi.user_id, ");
		strBuff.append(" fbi.third_approved_on,fbi.modified_on ,fbi.status, cg.grade_name,fbi.user_grade_code, ");
		strBuff.append(" fbi.ext_txn_no, fbi.ext_txn_date,fbi.requested_quantity,fbi.transfer_mrp,fbi.first_approved_by,");
		strBuff.append(" fbi.first_approved_on,fbi.second_approved_by,fbi.second_approved_on,fbi.third_approved_by, ");
		strBuff.append(" fb.created_by,fb.created_on,u.login_id , fbi.modified_by,fbi.reference_no,fbi.ext_txn_no,fb.txn_wallet, ");
		strBuff.append(" fbi.txn_profile, fbi.commission_profile_set_id,fbi.commission_profile_ver, fbi.commission_profile_detail_id,   ");
		strBuff.append(" fbi.requested_quantity, fbi.transfer_mrp, fbi.initiator_remarks, fbi.first_approver_remarks, ");
		strBuff.append(" fbi.third_approver_remarks, fbi.first_approved_by, fbi.first_approved_on, fbi.second_approved_by, ");
		strBuff.append(" fbi.third_approved_on, fbi.cancelled_by, fbi.cancelled_on, fbi.rcrd_status, fbi.external_code , ");
		strBuff.append(" fapp.user_name first_approver_name,sapp.user_name second_approver_name,intu.user_name initiater_name, ");
		strBuff.append(" fbi.second_approved_on, fbi.third_approved_by, fbi.second_approver_remarks, fbi.ext_txn_date, fbi.transfer_date, ");
		strBuff.append(" fbi.commission_type, fbi.commission_rate, fbi.commission_value, fbi.tax1_type, ");
		strBuff.append(" fbi.tax1_rate, fbi.tax1_value, fbi.tax2_type, fbi.tax2_rate, fbi.tax2_value, ");
		strBuff.append(" fbi.tax3_type, fbi.tax3_rate, fbi.tax3_value,fbi.bonus_type ");
		strBuff.append(", fbi.user_wallet,fbi.dual_comm_type ");
		strBuff.append(" FROM foc_batch_items fbi, foc_batches fb, categories c, channel_grades cg, users u, users intu, users fapp, users sapp");
		strBuff.append(" WHERE fb.batch_id=? AND fb.batch_id=fbi.batch_id AND u.user_id=fbi.user_id  AND fbi.category_code=c.category_code AND");
		strBuff.append(" fbi.user_grade_code=cg.grade_code");
		strBuff .append(" AND fbi.status in(" + itemStatus + ") AND fbi.rcrd_status=? AND  fb.created_by = intu.user_id(+) AND fbi.first_approved_by = fapp.user_id(+) AND fbi.second_approved_by = sapp.user_id(+)");
		LogFactory.printLog("loadBatchItemsMap", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public String loadBatchDPMasterDetailsQry(String itemStatus,
			String currentLevel) {
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT * FROM (SELECT DISTINCT fb.batch_id,fb.batch_name,fb.batch_total_record, fb.created_by,p.product_name,p.short_name,p.unit_value,sum(DECODE(fbi.status,?,1,0)) new, ");
		strBuff.append(" SUM(DECODE(fbi.status,?,1,0)) appr1,SUM(DECODE(fbi.status,?,1,0)) cncl,  ");
		strBuff.append(" SUM(DECODE(fbi.status,?,1,0)) appr2,SUM(DECODE(fbi.status,?,1,0)) closed,  ");
		strBuff.append(" fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on ,p.product_type,fb.domain_code,fb.batch_date,fb.sms_default_lang,fb.sms_second_lang,fb.txn_wallet ");
		strBuff.append(" FROM user_geographies ug , foc_batch_geographies fbg,foc_batches fb,foc_batch_items fbi,products p,user_domains ud, ");
		strBuff.append(" user_product_types upt,geographical_domains gd  ");
		strBuff.append(" WHERE ug.user_id=? AND ug.grph_domain_code=fbg.geography_code AND ug.grph_domain_code=gd.grph_domain_code AND gd.status='Y' ");
		strBuff.append(" AND ud.user_id=ug.user_id AND ud.domain_code= fb.domain_code ");
		strBuff.append(" AND upt.user_id=ud.user_id AND upt.product_type=p.product_type ");
		strBuff.append(" AND fbg.batch_id=fb.batch_id AND fb.status=? ");
		strBuff.append(" AND fb.product_code=p.product_code  ");
		strBuff.append(" AND fb.batch_id=fbi.batch_id AND fbi.rcrd_status=? AND fbi.status IN (" + itemStatus + ") ");
		strBuff.append(" AND fb.type='DP' ");
		strBuff.append(" AND (SELECT count(fbg1.geography_code) FROM foc_batch_geographies fbg1 WHERE fbg1.batch_id=fbg.batch_id) <= (SELECT count(ug1.user_id) FROM user_geographies ug1 ,geographical_domains gd WHERE ug1.user_id=ug.user_id AND ug1.grph_domain_code=gd.grph_domain_code AND gd.status='Y') AND fb.type='DP' ");
		strBuff .append(" GROUP BY fb.created_by,p.product_name,p.unit_value,fb.modified_by,fb.modified_on,p.product_type,fb.sms_default_lang,fb.sms_second_lang,fb.txn_wallet,fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name,p.unit_value,fb.network_code,fb.network_code_for,fb.created_by,fb.product_code, fb.modified_by, fb.modified_on,fb.sms_default_lang,fb.sms_second_lang,p.product_type,fbg.geography_code,p.short_name ,fb.domain_code,fb.batch_date ORDER BY fb.batch_date DESC ) ");
		if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)) {
			strBuff.append(" WHERE  new>0 OR  appr1>0 ");
		} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel)) {
			strBuff.append(" WHERE  appr1>0 OR appr2>0 ");
		} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(currentLevel)) {
			strBuff.append(" WHERE appr2>0 ");
		}
		LogFactory.printLog("loadBatchDPMasterDetailsQry", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public String closeOrderByBatchForDirectPayoutLoadNetworkStockQry() {
		final StringBuilder sqlBuffer = new StringBuilder("SELECT network_code, network_code_for, product_code, wallet_type, wallet_created, wallet_returned, ");
		sqlBuffer.append("wallet_balance, wallet_sold, last_txn_no, last_txn_type, last_txn_balance, previous_balance, ");
		sqlBuffer.append("modified_by, modified_on, created_on, created_by, daily_stock_updated_on ");
		sqlBuffer.append("FROM network_stocks ");
		sqlBuffer.append("WHERE network_code = ? AND network_code_for = ? AND wallet_type = ? AND ");
		// DB220120123for update WITH RS
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
			sqlBuffer.append("TRUNC(daily_stock_updated_on) <> TRUNC(?) FOR UPDATE OF wallet_balance WITH RS");
		} else {
			sqlBuffer.append("TRUNC(daily_stock_updated_on) <> TRUNC(?) FOR UPDATE OF wallet_balance");
		}
		LogFactory.printLog("closeOrderByBatchForDirectPayoutLoadNetworkStockQry", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}

	@Override
	public String closeOrderByBatchForDirectPayoutSelectWalletQry() {
		final StringBuilder sqlBuffer = new StringBuilder(" SELECT ");
		sqlBuffer.append(" wallet_balance , wallet_sold ");
		sqlBuffer.append(" FROM network_stocks ");
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
			sqlBuffer.append(" WHERE network_code = ? AND product_code = ? AND network_code_for = ? AND wallet_type = ? FOR UPDATE OF wallet_balance WITH RS");
		} else {
			sqlBuffer.append(" WHERE network_code = ? AND product_code = ? AND network_code_for = ? AND wallet_type = ?  FOR UPDATE OF wallet_balance ");
		}
		LogFactory.printLog("closeOrderByBatchForDirectPayoutselectWalletQry", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}

	@Override
	public String closeOrderByBatchForDirectPayoutSelectBalanceQry() {
		final StringBuilder  sqlBuffer = new StringBuilder("  SELECT ");
		sqlBuffer.append(" balance ");
		sqlBuffer.append(" FROM user_balances ");
		// DB220120123for update WITH RS
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
			sqlBuffer.append(" WHERE user_id = ? and product_code = ? AND network_code = ? AND network_code_for = ? AND balance_type = ? FOR UPDATE OF balance WITH RS");
		} else {
			sqlBuffer.append(" WHERE user_id = ? and product_code = ? AND network_code = ? AND network_code_for = ? AND balance_type = ? FOR UPDATE OF balance ");
		}
		LogFactory.printLog("closeOrderByBatchForDirectPayoutSelectBalanceQry", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}


	@Override
	public String closeOrderByBatchForDirectPayoutUserBalancesQry() {
		final StringBuilder  sqlBuffer = new StringBuilder(" SELECT user_id, network_code, network_code_for, product_code, balance, prev_balance, ");
		sqlBuffer.append("last_transfer_type, last_transfer_no, last_transfer_on, daily_balance_updated_on ");
		sqlBuffer.append("FROM user_balances ");
		// DB220120123for update WITH RS
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
			sqlBuffer.append("WHERE user_id = ? AND TRUNC(daily_balance_updated_on)<> TRUNC(?)  AND balance_type = ? FOR UPDATE OF balance WITH RS ");
		} else {
			sqlBuffer.append("WHERE user_id = ? AND TRUNC(daily_balance_updated_on)<> TRUNC(?) AND balance_type = ? FOR UPDATE OF balance ");
		}
		LogFactory.printLog("closeOrderByBatchForDirectPayoutUserBalancesQry", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}


	@Override
	public String focBatcheSelectItemDetailsQry() {
		final StringBuilder   sqlBuffer = new StringBuilder("SELECT fb.batch_total_record,SUM(DECODE(fbi.status,?,1,0)) new,");
		sqlBuffer.append(" SUM(DECODE(fbi.status,?,1,0)) appr1,SUM(DECODE(fbi.status,?,1,0)) cncl, ");
		sqlBuffer.append(" SUM(DECODE(fbi.status,?,1,0)) appr2,SUM(DECODE(fbi.status,?,1,0)) closed ");
		sqlBuffer.append(" FROM foc_batches fb,foc_batch_items fbi ");
		sqlBuffer.append(" WHERE fb.batch_id=fbi.batch_id AND fb.batch_id=? group by fb.batch_total_record");
		LogFactory.printLog("closeOrderByBatchForDirectPayoutSelectItemsDetailsQry", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}

	@Override
	public String getMultipleOffQry() {
		final StringBuilder strBuffSelectCProfileProd = new StringBuilder("select TRANSFER_MULTIPLE_OFF,MIN_TRANSFER_VALUE,MAX_TRANSFER_VALUE from COMMISSION_PROFILE_PRODUCTS where COMM_PROFILE_SET_ID in  ");
		strBuffSelectCProfileProd.append("(select COMM_PROFILE_SET_ID from channel_users where user_id in  ");
		strBuffSelectCProfileProd.append("(select user_id from users where msisdn=? and login_id=? and status <> ? )) order by to_number(COMM_PROFILE_SET_VERSION) desc");
		LogFactory.printLog("getMultipleOff", strBuffSelectCProfileProd.toString(), log);
		return strBuffSelectCProfileProd.toString();
	}
	
	
	
	


}
