package com.btsl.pretups.channel.transfer.businesslogic;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

public class O2CBatchWithdrawPostgresQry implements O2CBatchWithdrawQry{

	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public String loadBatchO2CMasterDetailsQry(String currentLevel, String itemStatus) {
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT * FROM (SELECT DISTINCT fb.batch_id,fb.batch_name,fb.batch_total_record,fb.created_by,p.product_name,p.short_name,p.unit_value,sum(case fbi.status when ? then 1 else 0 end ) as new, ");
		strBuff.append(" SUM(case fbi.status when ? then 1 else 0 end) as appr1,SUM(case fbi.status when ? then 1 else 0 end) as cncl,  ");
		strBuff.append(" SUM(case fbi.status when ? then 1 else 0 end) as appr2,SUM(case fbi.status when ? then 1 else 0 end) as closed,  ");
		strBuff.append(" fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on , fb.txn_wallet ,p.product_type,fb.domain_code,fb.batch_date,fb.sms_default_lang,fb.sms_second_lang ");
		strBuff.append(" FROM user_geographies ug , foc_batch_geographies fbg,foc_batches fb,foc_batch_items fbi,products p,user_domains ud, ");
		strBuff.append(" user_product_types upt,geographical_domains gd  ");
		strBuff.append(" WHERE ug.user_id=? AND ug.grph_domain_code=fbg.geography_code AND ug.grph_domain_code=gd.grph_domain_code AND gd.status='Y' ");
		strBuff.append(" AND ud.user_id=ug.user_id AND ud.domain_code= fb.domain_code ");
		strBuff.append(" AND upt.user_id=ud.user_id AND upt.product_type=p.product_type ");
		strBuff.append(" AND fbg.batch_id=fb.batch_id AND fb.status=? ");
		strBuff.append(" AND fb.product_code=p.product_code  ");
		strBuff.append("AND fb.type='OB' ");
		strBuff.append(" AND fb.batch_id=fbi.batch_id AND fbi.rcrd_status=? AND fbi.status IN (" + itemStatus + ") ");
		strBuff.append(" AND (SELECT count(fbg1.geography_code) FROM foc_batch_geographies fbg1 WHERE fbg1.batch_id=fbg.batch_id) <= (SELECT count(ug1.user_id) FROM user_geographies ug1 ,geographical_domains gd WHERE ug1.user_id=ug.user_id AND ug1.grph_domain_code=gd.grph_domain_code AND gd.status='Y') ");
		strBuff.append(" GROUP BY fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name,p.unit_value,fb.network_code,fb.network_code_for,fb.created_by,fb.product_code, fb.modified_by, fb.modified_on,fb.sms_default_lang,fb.sms_second_lang, fb.txn_wallet,p.product_type,fbg.geography_code,p.short_name ,fb.domain_code,fb.batch_date ORDER BY fb.batch_date DESC ) X");
		if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)) {
			strBuff.append(" WHERE  new>0 OR  appr1>0 ");
		} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel)) {
			strBuff.append(" WHERE  appr1>0 OR appr2>0 ");
		} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(currentLevel)) {
			strBuff.append(" WHERE appr2>0 ");
		}
		LogFactory.printLog("loadBatchO2CMasterDetailsQry", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public String loadBatchItemsMapQry(String itemStatus) {
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT fbi.batch_detail_id, c.category_name,c.category_code, fbi.msisdn, fbi.user_id, ");
		strBuff.append(" fbi.third_approved_on,fbi.modified_on ,fbi.status, cg.grade_name,fbi.user_grade_code, ");
		strBuff.append(" fbi.ext_txn_no, fbi.ext_txn_date,fbi.requested_quantity,fbi.transfer_mrp,fbi.first_approved_by,");
		strBuff.append(" fbi.first_approved_on,fbi.second_approved_by,fbi.second_approved_on,fbi.third_approved_by, ");
		strBuff.append(" fb.created_by,fb.created_on,u.login_id , fbi.modified_by,fbi.reference_no,fbi.ext_txn_no, ");
		strBuff.append(" fbi.txn_profile, fbi.commission_profile_set_id,fbi.commission_profile_ver, fbi.commission_profile_detail_id,   ");
		strBuff.append(" fbi.initiator_remarks, fbi.first_approver_remarks, ");
		strBuff.append(" fbi.third_approver_remarks, fbi.first_approved_by, fbi.first_approved_on, fbi.second_approved_by, ");
		strBuff.append(" fbi.third_approved_on, fbi.cancelled_by, fbi.cancelled_on, fbi.rcrd_status, fbi.external_code , ");
		strBuff.append(" fapp.user_name first_approver_name,sapp.user_name second_approver_name,intu.user_name initiater_name, ");
		strBuff.append(" fbi.second_approved_on, fbi.third_approved_by, fbi.second_approver_remarks, fbi.ext_txn_date, fbi.transfer_date, ");
		strBuff.append(" fbi.commission_type, fbi.commission_rate, fbi.commission_value, fbi.tax1_type, ");
		strBuff.append(" fbi.tax1_rate, fbi.tax1_value, fbi.tax2_type, fbi.tax2_rate, fbi.tax2_value, ");
		strBuff.append(" fbi.tax3_type, fbi.tax3_rate, fbi.tax3_value,fbi.bonus_type ");
		strBuff.append(" FROM foc_batch_items fbi left join  users fapp on fbi.first_approved_by = fapp.user_id left join  users sapp on fbi.second_approved_by = sapp.user_id ,foc_batches fb left join  users intu on fb.created_by = intu.user_id ,categories c,channel_grades cg, users u");
		strBuff.append(" WHERE fb.batch_id=? AND fb.batch_id=fbi.batch_id AND u.user_id=fbi.user_id  AND fbi.category_code=c.category_code AND");
		strBuff.append(" fbi.user_grade_code=cg.grade_code");
		strBuff.append(" AND fbi.status in(" + itemStatus + ") AND fbi.rcrd_status=? ");
		LogFactory.printLog("loadBatchItemsMapQry", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public String processOrderByBatchQry() {
		final StringBuilder sqlBuffer = new StringBuilder("SELECT fb.batch_total_record,SUM(case fbi.status when ? then 1 else 0 end) as new,");
		sqlBuffer.append(" SUM(case fbi.status when ? then 1 else 0 end) as appr1,SUM(case fbi.status when ? then 1 else 0 end) as cncl , ");
		sqlBuffer.append(" SUM(case fbi.status when ? then 1 else 0 end) as appr2,SUM(case fbi.status when ? then 1 else 0 end) as closed ");
		sqlBuffer.append(" FROM foc_batches fb,foc_batch_items fbi ");
		sqlBuffer.append(" WHERE fb.batch_id=fbi.batch_id AND fb.batch_id=? group by fb.batch_total_record");
		LogFactory.printLog("processOrderByBatchQry", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}

	@Override
	public String closeOrderByBatchQry() {
		StringBuilder sqlBuffer ;
		boolean multipleWalletApply = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY);
		if (multipleWalletApply) {
			sqlBuffer = new StringBuilder("SELECT network_code, network_code_for, product_code, foc_stock_created, foc_stock_returned, ");
			sqlBuffer.append("foc_stock, foc_stock_sold, foc_last_txn_no, foc_last_txn_type, foc_last_txn_stock, foc_previous_stock, ");
			sqlBuffer.append("modified_by, modified_on, created_on, created_by, daily_stock_updated_on ");
			sqlBuffer.append("FROM network_stocks ");
			sqlBuffer.append("WHERE network_code = ? AND network_code_for = ? AND ");
			sqlBuffer.append("DATE_TRUNC('day', daily_stock_updated_on::TIMESTAMP) <> DATE_TRUNC('day', ?::TIMESTAMP) FOR UPDATE ");

		} else {
			sqlBuffer = new StringBuilder("SELECT network_code, network_code_for, product_code, stock_created, stock_returned, ");
			sqlBuffer.append("stock, stock_sold, last_txn_no, last_txn_type, last_txn_stock, previous_stock, ");
			sqlBuffer.append("modified_by, modified_on, created_on, created_by, daily_stock_updated_on ");
			sqlBuffer.append("FROM network_stocks ");
			sqlBuffer.append("WHERE network_code = ? AND network_code_for = ? AND ");
			sqlBuffer.append("DATE_TRUNC('day', daily_stock_updated_on::TIMESTAMP) <> DATE_TRUNC('day',?::TIMESTAMP) FOR UPDATE ");
		}
		LogFactory.printLog("closeOrderByBatchQry", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}

	@Override
	public String closeOrderByBatchSelectNetworkStockQry() {
		StringBuilder sqlBuffer ; 
		boolean multipleWalletApply = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY);
		if (multipleWalletApply) {
			sqlBuffer = new StringBuilder(" SELECT ");
			sqlBuffer.append(" foc_stock , foc_stock_sold ");
			sqlBuffer.append(" FROM network_stocks ");
			sqlBuffer.append(" WHERE network_code = ? AND product_code = ? AND network_code_for = ? FOR UPDATE  ");
		} else {
			sqlBuffer = new StringBuilder(" SELECT ");
			sqlBuffer.append(" stock , stock_sold ");
			sqlBuffer.append(" FROM network_stocks ");
			sqlBuffer.append(" WHERE network_code = ? AND product_code = ? AND network_code_for = ? FOR UPDATE ");
		}
		LogFactory.printLog("closeOrderByBatchSelectNetworkStockQry", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}

	@Override
	public String closeOrderByBatchSelectUserBalancesQry() {
		StringBuilder sqlBuffer = new StringBuilder(" SELECT user_id, network_code, network_code_for, product_code, balance, prev_balance, ");
		sqlBuffer.append("last_transfer_type, last_transfer_no, last_transfer_on, daily_balance_updated_on ");
		sqlBuffer.append("FROM user_balances ");
		sqlBuffer.append("WHERE user_id = ? AND DATE_TRUNC('day', daily_balance_updated_on::TIMESTAMP) <> DATE_TRUNC('day', ?::TIMESTAMP) FOR UPDATE ");
		return sqlBuffer.toString();
	}

	@Override
	public String closeOrderByBatchSelectBalanceQry() {
		StringBuilder sqlBuffer = new StringBuilder("  SELECT ");
		sqlBuffer.append(" balance ");
		sqlBuffer.append(" FROM user_balances ");
		sqlBuffer.append(" WHERE user_id = ? and product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE  ");
		LogFactory.printLog("closeOrderByBatchSelectUserBalancesQry", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}
	@Override
	public String focBatchesSelectItemsDetailsQry() {
		StringBuilder sqlBuffer = new StringBuilder("SELECT fb.batch_total_record,SUM(case fbi.status when ? then 1 else 0 end) as new,");
		sqlBuffer.append(" SUM(case fbi.status when ? then 1 else 0 end) as appr1,SUM(case fbi.status when ? then 1 else 0 end) as cncl, ");
		sqlBuffer.append(" SUM(case fbi.status when ? then 1 else 0 end) as appr2,SUM(case fbi.status when ? then 1 else 0 end) as closed ");
		sqlBuffer.append(" FROM foc_batches fb,foc_batch_items fbi ");
		sqlBuffer.append(" WHERE fb.batch_id=fbi.batch_id AND fb.batch_id=? group by fb.batch_total_record");
		LogFactory.printLog("closeOrderByBatchSelectItemsDetails", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}

	@Override
	public String loadBatchO2CMasterDetailsQry(String batchid, String msisdn, String goeDomain, String domain, String productCode) {
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT DISTINCT fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name, p.unit_value, fb.domain_code, fb.batch_date, ");
		strBuff.append(" SUM(case fbi.status when ? then 1 else 0 end) as new,");
		strBuff.append(" SUM(case fbi.status when ? then 1 else 0 end) as appr1,SUM(case fbi.status when ? then 1 else 0 end) as cncl, ");
		strBuff.append(" SUM(case fbi.status when ? then 1 else 0 end) as appr2,SUM(case fbi.status when ? then 1 else 0 end) as closed, fb.created_on ");
		strBuff
		.append(" FROM user_geographies ug , foc_batch_geographies fbg,foc_batches fb,foc_batch_items fbi,products p,user_domains ud,  user_product_types upt, geographical_domains GRPD ");
		strBuff.append(" WHERE ug.grph_domain_code=fbg.geography_code  AND ud.user_id=ug.user_id  AND ud.domain_code= fb.domain_code AND upt.user_id=ud.user_id ");
		strBuff
		.append(" AND upt.product_type=p.product_type AND fb.transfer_type=? AND fb.transfer_sub_type=? AND fbg.batch_id=fb.batch_id AND fb.product_code=p.product_code AND fb.batch_id=fbi.batch_id ");
		strBuff.append(" AND GRPD.grph_domain_code=UG.grph_domain_code AND fb.CREATED_BY = ug.USER_ID ");
		strBuff.append(" AND GRPD.grph_domain_code IN (");

		strBuff.append(" WITH RECURSIVE q AS ( ");
		strBuff.append("SELECT GD1.grph_domain_code,GD1.status FROM geographical_domains GD1 WHERE   ");
		strBuff.append("GD1.grph_domain_code IN (SELECT UG2.grph_domain_code FROM user_geographies UG2 WHERE UG2.user_id=?  ");
		if (batchid == null && msisdn == null) {
			strBuff.append(" AND UG2.grph_domain_code IN(" + goeDomain + ")");
		}
		strBuff.append(")" );
		strBuff.append("UNION ALL ");
		strBuff.append("SELECT GD2.grph_domain_code,GD2.status FROM geographical_domains GD2  ");
		strBuff.append("join q on  q.grph_domain_code = GD2.parent_grph_domain_code  ");
		strBuff.append(" )SELECT grph_domain_code FROM q status IN('Y', 'S')  ");

		strBuff.append( ")");
		if (batchid != null) {
			strBuff.append(" AND fb.batch_id = ? ");
		} else if (msisdn != null) {
			strBuff.append(" AND fbi.msisdn = ? AND DATE_TRUNC('day',fbi.transfer_date::TIMESTAMP) >= ? AND DATE_TRUNC('day',fbi.transfer_date::TIMESTAMP) <= ? ");
		} else {
			strBuff.append(" AND fb.domain_code IN(" + domain + ")");
			strBuff.append(" AND fb.product_code=p.product_code ");
			strBuff.append(" AND p.product_code IN(" + productCode + ") ");
			strBuff.append(" AND DATE_TRUNC('day',fbi.transfer_date::TIMESTAMP) >= ? AND DATE_TRUNC('day',fbi.transfer_date::TIMESTAMP) <= ? ");
		}
		strBuff.append(" AND (SELECT count(fbg1.geography_code) FROM foc_batch_geographies fbg1 WHERE fbg1.batch_id=fbg.batch_id) <= (SELECT count(ug1.user_id) FROM user_geographies ug1 WHERE ug1.user_id=ug.user_id) ");
		strBuff.append(" GROUP BY fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name,p.unit_value,fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on,p.product_type,fbg.geography_code,p.short_name ,fb.domain_code, fb.batch_date, fb.created_on ");
		strBuff.append(" ORDER BY fb.created_on DESC ");
		LogFactory.printLog("loadBatchO2CMasterDetailsQry", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public String loadBatchDetailsListQry() {
		final StringBuilder strBuff = new StringBuilder(" SELECT FB.batch_id, FB.network_code, FB.network_code_for,  ");
        strBuff.append(" FB.batch_name, FB.status, L.lookup_name status_desc, FB.domain_code, FB.product_code, FB.batch_file_name, ");
        strBuff.append(" FB.batch_total_record, FB.batch_date, INTU.user_name initated_by, FB.created_on, P.product_name, D.domain_name, ");
        strBuff.append(" FBI.batch_detail_id, FBI.category_code, FBI.msisdn, FBI.user_id, FBI.status status_item,  FBI.user_grade_code, FBI.reference_no, ");
        strBuff.append(" FBI.ext_txn_no, FBI.ext_txn_date, FBI.transfer_date, FBI.txn_profile, FBI.commission_profile_set_id,  ");
        strBuff.append(" FBI.commission_profile_ver, FBI.commission_profile_detail_id, FBI.commission_type, FBI.commission_rate, ");
        strBuff.append(" FBI.commission_value, FBI.tax1_type, FBI.tax1_rate, FBI.tax1_value, FBI.tax2_type, FBI.tax2_rate, FBI.tax2_value, ");
        strBuff.append(" FBI.tax3_type, FBI.tax3_rate, FBI.tax3_value, FBI.requested_quantity, FBI.transfer_mrp, FBI.initiator_remarks, FBI.first_approver_remarks,");
        strBuff.append(" FBI.second_approver_remarks, FBI.third_approver_remarks, ");
        strBuff.append(" COALESCE(FAPP.user_name,CNCL_USR.user_name) first_approved_by, COALESCE(FBI.first_approved_on,FBI.cancelled_on) first_approved_on,");
        strBuff.append(" case FBI.first_approved_by when NULL then SAPP.user_name else COALESCE(SAPP.user_name,CNCL_USR.user_name) end second_approved_by, case FBI.first_approved_on when NULL then FBI.second_approved_on else COALESCE(FBI.second_approved_on,FBI.cancelled_on) end second_approved_on,");
        strBuff.append(" case FBI.second_approved_by when NULL then TAPP.user_name else COALESCE(TAPP.user_name,CNCL_USR.user_name) end  third_approved_by,  case FBI.second_approved_on when NULL then FBI.second_approved_on else  COALESCE(FBI.third_approved_on, FBI.cancelled_on) end third_approved_on,");
        strBuff.append(" CNCL_USR.user_name cancelled_by, FBI.cancelled_on, FBI.rcrd_status, FBI.external_code, ");
        strBuff.append(" U.user_name, C.category_name, CG.grade_name ");
        strBuff.append(" FROM foc_batches FB left join users INTU on FB.created_by = INTU.user_id , products P, domains D, ");
        strBuff.append(" foc_batch_items FBI left join users FAPP on FBI.first_approved_by = FAPP.user_id ");
        strBuff.append(" left join  users SAPP on  FBI.second_approved_by = SAPP.user_id " );
        strBuff.append(" left join  users TAPP on FBI.third_approved_by = TAPP.user_id");
        strBuff.append(" left join users CNCL_USR  on FBI.cancelled_by = CNCL_USR.user_id ");
        strBuff.append(" , categories C, users U, ");
        strBuff.append("  channel_grades CG, lookups L");
        strBuff.append(" WHERE FB.batch_id=?  ");
        strBuff.append(" AND FBI.batch_id = FB.batch_id AND FBI.category_code = C.category_code AND FBI.user_id = U.user_id ");
        strBuff.append(" AND P.product_code = FB.product_code AND D.domain_code = FB.domain_code ");
        strBuff.append(" AND CG.grade_code = FBI.user_grade_code ");
        strBuff.append(" AND L.lookup_type = ? ");
        strBuff.append(" AND L.lookup_code = FB.status ");
        strBuff.append(" ORDER BY FBI.batch_detail_id DESC, FBI.category_code, FBI.status ");
        LogFactory.printLog("loadBatchDetailsListQry", strBuff.toString(), log);
		return strBuff.toString();
	}


}