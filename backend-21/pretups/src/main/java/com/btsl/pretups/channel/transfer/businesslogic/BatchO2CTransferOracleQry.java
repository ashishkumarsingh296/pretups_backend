package com.btsl.pretups.channel.transfer.businesslogic;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

public class BatchO2CTransferOracleQry implements BatchO2CTransferQry{

	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public String loadBatchO2CMasterDetailsQry(String[] itemStatus, String currentLevel) {
		StringBuilder strBuff = new  StringBuilder();
		strBuff.append(" SELECT * FROM (SELECT DISTINCT fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name,p.short_name,p.unit_value,");
		strBuff.append("sum(DECODE(fbi.status,?,1,0)) new, ");
		strBuff.append(" SUM(DECODE(fbi.status,?,1,0)) appr1,SUM(DECODE(fbi.status,?,1,0)) cncl,  ");
		strBuff.append(" SUM(DECODE(fbi.status,?,1,0)) appr2,SUM(DECODE(fbi.status,?,1,0)) closed,  ");
		strBuff.append(" fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on ,p.product_type,fb.domain_code,fb.batch_date,fb.sms_default_lang,fb.sms_second_lang ");
		strBuff.append(" FROM user_geographies ug , foc_batch_geographies fbg,foc_batches fb,foc_batch_items fbi,products p,user_domains ud, ");
		strBuff.append(" user_product_types upt,geographical_domains gd  ");
		strBuff.append(" WHERE ug.user_id=? AND ug.grph_domain_code=fbg.geography_code AND ug.grph_domain_code=gd.grph_domain_code AND gd.status='Y' ");
		strBuff.append(" AND ud.user_id=ug.user_id AND ud.domain_code= fb.domain_code ");
		strBuff.append(" AND upt.user_id=ud.user_id AND upt.product_type=p.product_type ");
		strBuff.append(" AND fb.transfer_type=? AND fb.transfer_sub_type=? AND fbg.batch_id=fb.batch_id AND fb.status=? ");
		strBuff.append(" AND fb.product_code=p.product_code  ");
		strBuff.append(" AND fb.batch_id=fbi.batch_id AND fbi.rcrd_status=? AND fbi.status IN (");
		for (int i = 0; i < itemStatus.length; i++) {
			strBuff.append(" ?");
			if (i != itemStatus.length - 1) {
				strBuff.append(",");
			}
		}
		strBuff.append(")");
		strBuff
		.append(" AND (SELECT count(fbg1.geography_code) FROM foc_batch_geographies fbg1 WHERE fbg1.batch_id=fbg.batch_id) <= (SELECT count(ug1.user_id) FROM user_geographies ug1 ,geographical_domains gd WHERE ug1.user_id=ug.user_id AND ug1.grph_domain_code=gd.grph_domain_code AND gd.status='Y') ");
		strBuff
		.append(" GROUP BY fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name,p.unit_value,fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on,fb.sms_default_lang,fb.sms_second_lang,p.product_type,fbg.geography_code,p.short_name ,fb.domain_code,fb.batch_date ORDER BY fb.batch_date DESC ) ");
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
	public String loadBatchO2CMasterDetailsQry(String batchid, String msisdn,
			String[] goeDomain, String[] domain, String[] productCode) {
		StringBuilder strBuff = new  StringBuilder();
		strBuff.append(" SELECT DISTINCT fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name, p.unit_value, fb.domain_code, fb.batch_date, ");
		strBuff.append(" SUM(DECODE(fbi.status,?,1,0)) new,");
		strBuff.append(" SUM(DECODE(fbi.status,?,1,0)) appr1, SUM(DECODE(fbi.status,?,1,0)) cncl, ");
		strBuff.append(" SUM(DECODE(fbi.status,?,1,0)) appr2,SUM(DECODE(fbi.status,?,1,0)) closed, fb.created_on ");
		strBuff
		.append(" FROM user_geographies ug , foc_batch_geographies fbg,foc_batches fb,foc_batch_items fbi,products p,user_domains ud,  user_product_types upt, geographical_domains GRPD ");
		strBuff.append(" WHERE ug.grph_domain_code=fbg.geography_code  AND ud.user_id=ug.user_id  AND ud.domain_code= fb.domain_code AND upt.user_id=ud.user_id ");
		strBuff
		.append(" AND upt.product_type=p.product_type AND fb.transfer_type=? AND fb.transfer_sub_type=? AND fbg.batch_id=fb.batch_id AND fb.product_code=p.product_code AND fb.batch_id=fbi.batch_id ");
		strBuff.append(" AND GRPD.grph_domain_code=UG.grph_domain_code AND fb.CREATED_BY = ug.USER_ID ");
		strBuff.append(" AND GRPD.grph_domain_code IN (SELECT GD1.grph_domain_code FROM geographical_domains GD1 WHERE status IN('Y', 'S') ");
		strBuff.append(" CONNECT BY PRIOR GD1.grph_domain_code = GD1.parent_grph_domain_code ");
		strBuff.append(" START WITH GD1.grph_domain_code IN (SELECT UG2.grph_domain_code FROM user_geographies UG2 WHERE UG2.user_id=? ");
		if (batchid == null && msisdn == null) {
			strBuff.append(" AND UG2.grph_domain_code IN(");
			for (int i = 0; i < goeDomain.length; i++) {
				strBuff.append(" ?");
				if (i != goeDomain.length - 1) {
					strBuff.append(",");
				}
			}
			strBuff.append(")");
		}
		strBuff.append("))");
		if (batchid != null) {
			strBuff.append(" AND fb.batch_id = ? ");
		} else if (msisdn != null) {
			strBuff.append(" AND fbi.msisdn = ? AND TRUNC(fbi.transfer_date) >= ? AND TRUNC(fbi.transfer_date) <= ? ");
		} else {

			strBuff.append(" AND fb.domain_code IN(");
			for (int i = 0; i < domain.length; i++) {
				strBuff.append(" ?");
				if (i != domain.length - 1) {
					strBuff.append(",");
				}
			}
			strBuff.append(")");
			strBuff.append(" AND fb.product_code=p.product_code ");
			strBuff.append(" AND p.product_code IN(");
			for (int i = 0; i < productCode.length; i++) {
				strBuff.append(" ?");
				if (i != productCode.length - 1) {
					strBuff.append(",");
				}
			}
			strBuff.append(")");
			strBuff.append(" AND TRUNC(fbi.transfer_date) >= ? AND TRUNC(fbi.transfer_date) <= ? ");
		}
		strBuff
		.append(" AND (SELECT count(fbg1.geography_code) FROM foc_batch_geographies fbg1 WHERE fbg1.batch_id=fbg.batch_id) <= (SELECT count(ug1.user_id) FROM user_geographies ug1 WHERE ug1.user_id=ug.user_id) ");
		strBuff
		.append(" GROUP BY fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name,p.unit_value,fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on,p.product_type,fbg.geography_code,p.short_name ,fb.domain_code, fb.batch_date, fb.created_on ");
		strBuff.append(" ORDER BY fb.created_on DESC ");
		LogFactory.printLog("loadBatchO2CMasterDetailsQry", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public String loadBatchItemsMapQry(String itemStatus) {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT fbi.batch_detail_id, c.category_name,c.category_code, fbi.msisdn, fbi.user_id, ");
		strBuff.append(" fbi.third_approved_on,fbi.modified_on ,fbi.status, cg.grade_name,fbi.user_grade_code, ");
		strBuff.append(" fbi.ext_txn_no, fbi.ext_txn_date,fbi.requested_quantity,fbi.transfer_mrp,fbi.first_approved_by,");
		strBuff.append(" fbi.first_approved_on,fbi.second_approved_by,fbi.second_approved_on,fbi.third_approved_by, ");
		strBuff.append(" fb.created_by,fb.created_on,u.login_id , fbi.modified_by,fbi.reference_no,fbi.ext_txn_no, ");
		strBuff.append(" fbi.txn_profile, fbi.commission_profile_set_id,fbi.commission_profile_ver, fbi.commission_profile_detail_id,   ");
		strBuff.append(" fbi.requested_quantity, fbi.transfer_mrp, fbi.initiator_remarks, fbi.first_approver_remarks, ");
		strBuff.append(" fbi.third_approver_remarks, fbi.first_approved_by, fbi.first_approved_on, fbi.second_approved_by, ");
		strBuff.append(" fbi.third_approved_on, fbi.cancelled_by, fbi.cancelled_on, fbi.rcrd_status, fbi.external_code , ");
		strBuff.append(" fapp.user_name first_approver_name,sapp.user_name second_approver_name,intu.user_name initiater_name, ");
		strBuff.append(" fbi.second_approved_on, fbi.third_approved_by, fbi.second_approver_remarks, fbi.ext_txn_date, fbi.transfer_date, ");
		strBuff.append(" fbi.commission_type, fbi.commission_rate, fbi.commission_value, fbi.tax1_type, ");
		strBuff.append(" fbi.tax1_rate, fbi.tax1_value, fbi.tax2_type, fbi.tax2_rate, fbi.tax2_value, ");
		strBuff.append(" fbi.tax3_type, fbi.tax3_rate, fbi.tax3_value ");
		strBuff.append(" FROM foc_batch_items fbi,foc_batches fb,categories c,channel_grades cg, users u, users intu, users fapp, users sapp");
		strBuff.append(" WHERE fb.batch_id=? AND fb.transfer_type=? AND fb.transfer_sub_type=? AND fb.batch_id=fbi.batch_id AND u.user_id=fbi.user_id  AND fbi.category_code=c.category_code AND");
		strBuff.append(" fbi.user_grade_code=cg.grade_code");
		strBuff.append(" AND fbi.status in(" + itemStatus + ") AND fbi.rcrd_status=? AND  fb.created_by = intu.user_id(+) AND fbi.first_approved_by = fapp.user_id(+) AND fbi.second_approved_by = sapp.user_id(+)");
		LogFactory.printLog("loadBatchItemsMapQry", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public String processO2CWithdrawByBatchSelectItemsDetails() {
		StringBuilder  sqlBuffer = new StringBuilder("SELECT fb.batch_total_record,SUM(DECODE(fbi.status,?,1,0)) new,");
		sqlBuffer.append(" SUM(DECODE(fbi.status,?,1,0)) appr1,SUM(DECODE(fbi.status,?,1,0)) cncl, ");
		sqlBuffer.append(" SUM(DECODE(fbi.status,?,1,0)) appr2,SUM(DECODE(fbi.status,?,1,0)) closed ");
		sqlBuffer.append(" FROM foc_batches fb,foc_batch_items fbi ");
		sqlBuffer.append(" WHERE fb.batch_id=fbi.batch_id AND fb.batch_id=? group by fb.batch_total_record");
		LogFactory.printLog("processO2CWithdrawByBatchSelectItemsDetails", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}

	@Override
	public String closeO2CWithdrawByBatchLoadNetworkStockQry() {
		StringBuilder sqlBuffer = new StringBuilder("SELECT network_code, network_code_for, product_code, wallet_type, wallet_created, wallet_returned, ");
		sqlBuffer.append("wallet_balance, wallet_sold, last_txn_no, last_txn_type, last_txn_balance, previous_balance, ");
		sqlBuffer.append("modified_by, modified_on, created_on, created_by, daily_stock_updated_on ");
		sqlBuffer.append("FROM network_stocks ");
		sqlBuffer.append("WHERE network_code = ? AND network_code_for = ? AND wallet_type = ? AND ");
		sqlBuffer.append("TRUNC(daily_stock_updated_on) <> TRUNC(?) FOR UPDATE OF wallet_balance");
		LogFactory.printLog("closeO2CWithdrawByBatchLoadNetworkStockQry", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}

	@Override
	public String closeO2CWithdrawByBatchSelectNetworkStockQry() {
		StringBuilder sqlBuffer = new StringBuilder(" SELECT ");
		sqlBuffer.append(" wallet_type, wallet_balance , wallet_sold ");
		sqlBuffer.append(" FROM network_stocks ");
		sqlBuffer.append(" WHERE network_code = ? AND product_code = ? AND network_code_for = ? and wallet_type = ? FOR UPDATE OF wallet_balance ");
		LogFactory.printLog("closeO2CWithdrawByBatchSelectNetworkStockQry", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}

	@Override
	public String closeO2CWithdrawByBatchSelectUserBalances() {
		StringBuilder sqlBuffer = new StringBuilder(" SELECT user_id, network_code, network_code_for, product_code, balance, prev_balance, ");
		sqlBuffer.append("last_transfer_type, last_transfer_no, last_transfer_on, daily_balance_updated_on ");
		sqlBuffer.append("FROM user_balances ");
		sqlBuffer.append("WHERE user_id = ? AND TRUNC(daily_balance_updated_on)<> TRUNC(?) FOR UPDATE OF balance ");
		LogFactory.printLog("closeO2CWithdrawByBatchSelectUserBalances", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}

	@Override
	public String closeO2CWithdrawByBatchSelectItemsDetails() {
		StringBuilder sqlBuffer = new StringBuilder("SELECT fb.batch_total_record,SUM(DECODE(fbi.status,?,1,0)) new,");
		sqlBuffer.append(" SUM(DECODE(fbi.status,?,1,0)) appr1,SUM(DECODE(fbi.status,?,1,0)) cncl, ");
		sqlBuffer.append(" SUM(DECODE(fbi.status,?,1,0)) appr2,SUM(DECODE(fbi.status,?,1,0)) closed ");
		sqlBuffer.append(" FROM foc_batches fb,foc_batch_items fbi ");
		sqlBuffer.append(" WHERE fb.batch_id=fbi.batch_id AND fb.batch_id=? group by fb.batch_total_record");
		LogFactory.printLog("closeO2CWithdrawByBatchSelectItemsDetails", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}

	@Override
	public String loadBatchDetailsListQry() {
		StringBuilder strBuff = new StringBuilder(" SELECT FB.batch_id, FB.network_code, FB.network_code_for,  ");
		strBuff.append(" FB.batch_name, FB.status, L.lookup_name status_desc, FB.domain_code, FB.product_code, FB.batch_file_name, ");
		strBuff.append(" FB.batch_total_record, FB.batch_date, INTU.user_name initated_by, FB.created_on, P.product_name, D.domain_name, ");
		strBuff.append(" FBI.batch_detail_id, FBI.category_code, FBI.msisdn, FBI.user_id, FBI.status status_item,  FBI.user_grade_code, FBI.reference_no, ");
		strBuff.append(" FBI.ext_txn_no, FBI.ext_txn_date, FBI.transfer_date, FBI.txn_profile, FBI.commission_profile_set_id,  ");
		strBuff.append(" FBI.commission_profile_ver, FBI.commission_profile_detail_id, FBI.commission_type, FBI.commission_rate, ");
		strBuff.append(" FBI.commission_value, FBI.tax1_type, FBI.tax1_rate, FBI.tax1_value, FBI.tax2_type, FBI.tax2_rate, FBI.tax2_value, ");
		strBuff.append(" FBI.tax3_type, FBI.tax3_rate, FBI.tax3_value, FBI.requested_quantity, FBI.transfer_mrp, FBI.initiator_remarks, FBI.first_approver_remarks,");
		strBuff.append(" FBI.second_approver_remarks, FBI.third_approver_remarks, ");
		strBuff.append(" NVL(FAPP.user_name,CNCL_USR.user_name) first_approved_by, NVL(FBI.first_approved_on,FBI.cancelled_on) first_approved_on,");
		strBuff.append(" DECODE(FBI.first_approved_by, NULL, SAPP.user_name, NVL(SAPP.user_name,CNCL_USR.user_name)) second_approved_by, DECODE(FBI.first_approved_on, NULL, FBI.second_approved_on, NVL(FBI.second_approved_on,FBI.cancelled_on) )second_approved_on,");
		strBuff.append(" DECODE(FBI.second_approved_by, NULL, TAPP.user_name, NVL(TAPP.user_name,CNCL_USR.user_name)) third_approved_by,  DECODE(FBI.second_approved_on, NULL, FBI.second_approved_on, NVL(FBI.third_approved_on, FBI.cancelled_on))third_approved_on,");
		strBuff.append(" CNCL_USR.user_name cancelled_by, FBI.cancelled_on, FBI.rcrd_status, FBI.external_code, ");
		strBuff.append(" U.user_name, C.category_name, CG.grade_name ");
		strBuff.append(" FROM foc_batches FB, products P, domains D, foc_batch_items FBI, categories C, users U, ");
		strBuff.append(" users INTU, users FAPP, users SAPP, users TAPP, channel_grades CG, lookups L, users CNCL_USR ");
		strBuff.append(" WHERE FB.batch_id=?  ");
		strBuff.append(" AND FBI.batch_id = FB.batch_id AND FBI.category_code = C.category_code AND FBI.user_id = U.user_id ");
		strBuff.append(" AND P.product_code = FB.product_code AND D.domain_code = FB.domain_code ");
		strBuff.append(" AND FB.created_by = INTU.user_id(+) ");
		strBuff.append(" AND FBI.first_approved_by = FAPP.user_id(+) ");
		strBuff.append(" AND FBI.second_approved_by = SAPP.user_id(+) ");
		strBuff.append(" AND FBI.third_approved_by = TAPP.user_id(+) ");
		strBuff.append(" AND FBI.cancelled_by = CNCL_USR.user_id(+) ");
		strBuff.append(" AND CG.grade_code = FBI.user_grade_code ");
		strBuff.append(" AND L.lookup_type = ? ");
		strBuff.append(" AND L.lookup_code = FB.status ");
		strBuff.append(" ORDER BY FBI.batch_detail_id DESC, FBI.category_code, FBI.status ");
		LogFactory.printLog("loadBatchDetailsListQry", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public String loadO2CTransferApprovalListQry(String currentLevel, String itemStatus, String categoryCode, String geoDomain, String domainCode) {
		final StringBuilder strBuff = new StringBuilder();
		strBuff
		.append(" SELECT * FROM (SELECT DISTINCT fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name,p.short_name,p.unit_value,sum(DECODE(fbi.status,?,1,0)) new, ");
		strBuff.append(" SUM(DECODE(fbi.status,?,1,0)) appr1,SUM(DECODE(fbi.status,?,1,0)) cncl,  ");
		strBuff.append(" SUM(DECODE(fbi.status,?,1,0)) appr2,SUM(DECODE(fbi.status,?,1,0)) closed,  ");
		strBuff
		.append(" fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on ,p.product_type,fb.domain_code,fb.batch_date,fb.sms_default_lang,fb.sms_second_lang, fbi.category_code ");
		strBuff.append(" FROM user_geographies ug , o2c_batch_geographies fbg,o2c_batches fb,o2c_batch_items fbi,products p,user_domains ud, ");
		strBuff.append(" user_product_types upt,geographical_domains gd  ");
		strBuff.append(" WHERE ug.user_id=? ");
		if (!categoryCode.equalsIgnoreCase(PretupsI.ALL))
			strBuff.append(" AND fbi.category_code=?  ");
	    if (!geoDomain.equalsIgnoreCase(PretupsI.ALL))
	    	strBuff.append(" AND fbg.GEOGRAPHY_CODE=? ");
	    if (!domainCode.equalsIgnoreCase(PretupsI.ALL))
		    strBuff.append(" AND trim(fb.domain_code)= ? ");
		strBuff.append(" AND ug.grph_domain_code=fbg.geography_code AND ug.grph_domain_code=gd.grph_domain_code AND gd.status='Y' ");
		strBuff.append(" AND ud.user_id=ug.user_id AND ud.domain_code= fb.domain_code ");
		strBuff.append(" AND upt.user_id=ud.user_id AND upt.product_type=p.product_type ");
		strBuff.append(" AND fbg.batch_id=fb.batch_id AND fb.status='OPEN' ");
		strBuff.append(" AND fb.product_code=p.product_code  ");
		strBuff.append(" AND fb.batch_id=fbi.batch_id AND fbi.rcrd_status='P' AND fbi.status IN (" + itemStatus + ") ");
		strBuff
		.append(" AND (SELECT count(fbg1.geography_code) FROM o2c_batch_geographies fbg1 WHERE fbg1.batch_id=fbg.batch_id) <= (SELECT count(ug1.user_id) FROM user_geographies ug1 ,geographical_domains gd WHERE ug1.user_id=ug.user_id AND ug1.grph_domain_code=gd.grph_domain_code AND gd.status='Y') ");
		strBuff
		.append(" GROUP BY fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name,p.unit_value,fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on,fb.sms_default_lang,fb.sms_second_lang,p.product_type,fbg.geography_code,p.short_name ,fb.domain_code,fb.batch_date, fbi.category_code ORDER BY fb.batch_date DESC ) ");
		if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(currentLevel)) {
			strBuff.append(" WHERE  new>0 ");
		} else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)){
			strBuff.append(" WHERE  appr1>0 ");
		} else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel)) {
			strBuff.append(" WHERE  appr2>0 ");
		}
		LogFactory.printLog("loadO2CTransferApprovalListQry", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public String loadO2CWithdrawal_FOCApprovalListQry(String approvalLevel, String statusUsed, String categoryCode,
			String geoDomain, String domainCode) {

		final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT * FROM (SELECT DISTINCT fb.batch_id,fb.batch_name,fb.batch_total_record,fb.created_by,p.product_name,p.short_name,p.unit_value,sum(case fbi.status when ? then 1 else 0 end) as new, ");
        strBuff.append(" SUM(case fbi.status when ? then 1 else 0 end) appr1,SUM(case fbi.status when ? then 1 else 0 end) cncl,  ");
        strBuff.append(" SUM(case fbi.status when ? then 1 else 0 end) appr2,SUM(case fbi.status when ? then 1 else 0 end) closed,  ");
        strBuff.append(" fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on , fb.txn_wallet ,p.product_type,fb.domain_code,fb.batch_date,fb.sms_default_lang,fb.sms_second_lang,fbi.category_code ");
        strBuff.append(" FROM user_geographies ug , foc_batch_geographies fbg,foc_batches fb,foc_batch_items fbi,products p,user_domains ud, ");
        strBuff.append(" user_product_types upt,geographical_domains gd  ");
        strBuff.append(" WHERE ug.user_id=?");
        if (!categoryCode.equalsIgnoreCase(PretupsI.ALL))
			strBuff.append(" AND fbi.category_code=?  ");
	    if (!geoDomain.equalsIgnoreCase(PretupsI.ALL))
	    	strBuff.append(" AND fbg.GEOGRAPHY_CODE=? ");
	    if (!domainCode.equalsIgnoreCase(PretupsI.ALL))
		    strBuff.append(" AND trim(fb.domain_code)= ? ");
	    strBuff.append(" AND ug.grph_domain_code=fbg.geography_code AND ug.grph_domain_code=gd.grph_domain_code AND gd.status='Y' ");
        strBuff.append(" AND ud.user_id=ug.user_id AND ud.domain_code= fb.domain_code ");
        strBuff.append(" AND upt.user_id=ud.user_id AND upt.product_type=p.product_type ");
        strBuff.append(" AND fbg.batch_id=fb.batch_id AND fb.status='OPEN' ");
        strBuff.append(" AND fb.product_code=p.product_code  ");
        strBuff.append("AND fb.type=? ");
        strBuff.append(" AND fb.batch_id=fbi.batch_id AND fbi.rcrd_status='P' AND fbi.status IN (" + statusUsed + ") ");
        strBuff
            .append(" AND (SELECT count(fbg1.geography_code) FROM foc_batch_geographies fbg1 WHERE fbg1.batch_id=fbg.batch_id) <= (SELECT count(ug1.user_id) FROM user_geographies ug1 ,geographical_domains gd WHERE ug1.user_id=ug.user_id AND ug1.grph_domain_code=gd.grph_domain_code AND gd.status='Y') ");
        strBuff
            .append(" GROUP BY fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name,p.unit_value,fb.network_code,fb.network_code_for,fb.created_by,fb.product_code, fb.modified_by, fb.modified_on,fb.sms_default_lang,fb.sms_second_lang, fb.txn_wallet,p.product_type,fbg.geography_code,p.short_name ,fb.domain_code,fb.batch_date,fbi.category_code ORDER BY fb.batch_date DESC ) qry ");
        if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(approvalLevel)) {
            strBuff.append(" WHERE  new>0 ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
            strBuff.append(" WHERE  appr1>0  ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
            strBuff.append(" WHERE appr2>0 ");
        }

        return strBuff.toString();
	}
	@Override
		public String loadBatchTransferApprovalDetails(String itemStatus,String approvalLevel) {
			StringBuilder strBuff = new  StringBuilder();
			strBuff.append(" SELECT * FROM ( SELECT DISTINCT fb.batch_id, fb.batch_name, p.product_code, fb.batch_total_record, fb.BATCH_FILE_NAME , p.product_name,");
			strBuff.append("sum(DECODE(fbi.status,?,1,0)) new, ");
			strBuff.append(" SUM(DECODE(fbi.status,?,1,0)) appr1,SUM(DECODE(fbi.status,?,1,0)) cncl,  ");
			strBuff.append(" SUM(DECODE(fbi.status,?,1,0)) appr2,SUM(DECODE(fbi.status,?,1,0)) closed,  ");
			strBuff.append(" fbi.initiator_remarks,fbi.first_approver_remarks ,fbi.second_approver_remarks ,fb.sms_default_lang , fb.sms_second_lang ");
			strBuff.append(" FROM user_geographies ug , o2c_batch_geographies fbg, o2c_batches fb, o2c_batch_items fbi,products p,user_domains ud, ");
			strBuff.append(" user_product_types upt,geographical_domains gd  ");
			strBuff.append(" WHERE ug.user_id=? AND fb.batch_id=? AND ug.grph_domain_code=fbg.geography_code AND ug.grph_domain_code=gd.grph_domain_code AND gd.status='Y' ");
			strBuff.append(" AND ud.user_id=ug.user_id AND ud.domain_code= fb.domain_code ");
			strBuff.append(" AND upt.user_id=ud.user_id AND upt.product_type=p.product_type ");
			strBuff.append(" AND fbg.batch_id=fb.batch_id AND fb.status=? ");
			strBuff.append(" AND fb.product_code=p.product_code  ");
			strBuff.append(" AND fb.batch_id=fbi.batch_id AND fbi.rcrd_status=? AND fbi.status IN ("+itemStatus);
			strBuff.append(")");
			strBuff
			.append(" AND (SELECT count(fbg1.geography_code) FROM foc_batch_geographies fbg1 WHERE fbg1.batch_id=fbg.batch_id) <= (SELECT count(ug1.user_id) FROM user_geographies ug1 ,geographical_domains gd WHERE ug1.user_id=ug.user_id AND ug1.grph_domain_code=gd.grph_domain_code AND gd.status='Y') ");
			strBuff
			.append(" GROUP BY p.product_code,fb.batch_id,fb.batch_name,fb.batch_total_record, fbi.first_approver_remarks ,fb.sms_default_lang , fb.sms_second_lang ,fbi.second_approver_remarks,p.product_name,p.unit_value,fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on,fb.sms_default_lang,fb.sms_second_lang,p.product_type,fbg.geography_code,p.short_name ,fb.domain_code,fb.batch_date ,fbi.initiator_remarks, fb.batch_file_name ORDER BY fb.batch_date DESC ) ");
			if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(approvalLevel)) {
				strBuff.append(" WHERE  new>0 ");
			} else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)){
				strBuff.append(" WHERE  appr1>0 ");
			} else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
				strBuff.append(" WHERE  appr2>0 ");
			}
			LogFactory.printLog("loadBatchTransferApprovalDetails", strBuff.toString(), log);


			return strBuff.toString();

		}
		@Override
		public String loadBatchWithdrawalorFOCApprovalDetails(String itemStatus,String approvalLevel) {
			StringBuilder strBuff = new  StringBuilder();
			strBuff.append(" SELECT * FROM ( SELECT DISTINCT fb.batch_id, fb.batch_name, fb.batch_total_record, fb.BATCH_FILE_NAME ,fb.batch_date, p.product_name,p.product_code,fbi.bonus_type,");
			strBuff.append("sum(DECODE(fbi.status,?,1,0)) new, ");
			strBuff.append(" SUM(DECODE(fbi.status,?,1,0)) appr1,SUM(DECODE(fbi.status,?,1,0)) cncl,  ");
			strBuff.append(" SUM(DECODE(fbi.status,?,1,0)) appr2,SUM(DECODE(fbi.status,?,1,0)) closed,  ");
			strBuff.append(" fbi.initiator_remarks,fbi.first_approver_remarks ,fbi.second_approver_remarks ,fb.sms_default_lang , fb.sms_second_lang ");
			strBuff.append(" FROM user_geographies ug , foc_batch_geographies fbg, foc_batches fb, foc_batch_items fbi,products p,user_domains ud, ");
			strBuff.append(" user_product_types upt,geographical_domains gd  ");
			strBuff.append(" WHERE ug.user_id=? AND fb.batch_id=? AND ug.grph_domain_code=fbg.geography_code AND ug.grph_domain_code=gd.grph_domain_code AND gd.status='Y' ");
			strBuff.append(" AND ud.user_id=ug.user_id AND ud.domain_code= fb.domain_code ");
			strBuff.append(" AND upt.user_id=ud.user_id AND upt.product_type=p.product_type ");
			strBuff.append(" AND fbg.batch_id=fb.batch_id AND fb.status=? ");
			strBuff.append(" AND fb.product_code=p.product_code AND fb.type = ? ");
			strBuff.append(" AND fb.batch_id=fbi.batch_id AND fbi.rcrd_status=? AND fbi.status IN ("+itemStatus);
			strBuff.append(")");
			strBuff.append(" AND (SELECT count(fbg1.geography_code) FROM foc_batch_geographies fbg1 WHERE fbg1.batch_id=fbg.batch_id) <= (SELECT count(ug1.user_id) FROM user_geographies ug1 ,geographical_domains gd WHERE ug1.user_id=ug.user_id AND ug1.grph_domain_code=gd.grph_domain_code AND gd.status='Y') ");
			strBuff.append(" GROUP BY p.product_code,fb.batch_id,fb.batch_name,fb.batch_total_record,fbi.bonus_type ,fbi.first_approver_remarks ,fb.sms_default_lang , fb.sms_second_lang ,fbi.second_approver_remarks, p.product_name,p.unit_value,fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on,fb.sms_default_lang,fb.sms_second_lang,p.product_type,fbg.geography_code,p.short_name ,fb.domain_code,fb.batch_date, fbi.initiator_remarks, fb.batch_file_name ORDER BY fb.batch_date DESC ) ");
			  if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(approvalLevel)) {
		            strBuff.append(" WHERE  new>0 OR  appr1>0 ");
		        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
		            strBuff.append(" WHERE  appr1>0 OR appr2>0 ");
		        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
		            strBuff.append(" WHERE appr2>0 ");
		        }

			LogFactory.printLog("loadBatchWithdrawalorFOCApprovalDetails", strBuff.toString(), log);


			return strBuff.toString();

		}
		@Override
		public String loadBatchO2CItemsMapQry(String itemStatus) {
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append(" SELECT fbi.batch_detail_id, c.category_name,c.category_code, fbi.msisdn, fbi.user_id, ");
			strBuff.append(" fbi.modified_on ,fbi.status, cg.grade_name,fbi.user_grade_code, ");
			strBuff.append(" fbi.ext_txn_no, fbi.ext_txn_date,fbi.requested_quantity,fbi.transfer_mrp,fbi.first_approved_by,");
			strBuff.append(" fbi.first_approved_on,fbi.second_approved_by,fbi.second_approved_on, ");
			strBuff.append(" fb.created_by,fb.created_on,u.login_id , fbi.modified_by,fbi.reference_no,fbi.ext_txn_no, ");
			strBuff.append(" fbi.txn_profile, fbi.commission_profile_set_id,fbi.commission_profile_ver, fbi.commission_profile_detail_id,   ");
			strBuff.append(" fbi.requested_quantity, fbi.transfer_mrp, fbi.initiator_remarks, fbi.first_approver_remarks, ");
			strBuff.append(" fbi.first_approved_by, fbi.first_approved_on, fbi.second_approved_by, ");
			strBuff.append(" fbi.cancelled_by, fbi.cancelled_on, fbi.rcrd_status, fbi.external_code , ");
			strBuff.append(" fapp.user_name first_approver_name,sapp.user_name second_approver_name,intu.user_name initiater_name, ");
			strBuff.append(" fbi.second_approved_on, fbi.second_approver_remarks, fbi.ext_txn_date, fbi.transfer_date, ");
			strBuff.append(" fbi.commission_type, fbi.commission_rate, fbi.commission_value, fbi.tax1_type, ");
			strBuff.append(" fbi.tax1_rate, fbi.tax1_value, fbi.tax2_type, fbi.tax2_rate, fbi.tax2_value, ");
			strBuff.append(" fbi.tax3_type, fbi.tax3_rate, fbi.tax3_value ,fbi.payment_type, fbi.payable_amount, fbi.net_payable_amount,fbi.approved1_quantity,fbi.approved2_quantity,fbi.dual_comm_type ");
			strBuff.append(" FROM o2c_batch_items fbi,o2c_batches fb,categories c,channel_grades cg, users u, users intu, users fapp, users sapp");
			strBuff.append(" WHERE fb.batch_id=? AND fb.transfer_type=? AND fb.transfer_sub_type=? AND fb.batch_id=fbi.batch_id AND u.user_id=fbi.user_id  AND fbi.category_code=c.category_code AND");
			strBuff.append(" fbi.user_grade_code=cg.grade_code");
			strBuff.append(" AND fbi.status in(" + itemStatus + ") AND fbi.rcrd_status=? AND  fb.created_by = intu.user_id(+) AND fbi.first_approved_by = fapp.user_id(+) AND fbi.second_approved_by = sapp.user_id(+)");
			LogFactory.printLog("loadBatchO2CItemsMapQry", strBuff.toString(), log);
			return strBuff.toString();
		}

		@Override
		public String loadBatchO2CItemsMapQryFOCorWithdrawal(String itemStatus) {
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append(" SELECT fbi.batch_detail_id, c.category_name,c.category_code, fbi.msisdn, fbi.user_id, ");
			strBuff.append(" fbi.modified_on ,fbi.status, cg.grade_name,fbi.user_grade_code, ");
			strBuff.append(" fbi.ext_txn_no, fbi.ext_txn_date,fbi.requested_quantity,fbi.transfer_mrp,fbi.first_approved_by,");
			strBuff.append(" fbi.first_approved_on,fbi.second_approved_by,fbi.second_approved_on, ");
			strBuff.append(" fb.created_by,fb.created_on,u.login_id , fbi.modified_by,fbi.reference_no,fbi.ext_txn_no, ");
			strBuff.append(" fbi.txn_profile, fbi.commission_profile_set_id,fbi.commission_profile_ver, fbi.commission_profile_detail_id,   ");
			strBuff.append(" fbi.requested_quantity, fbi.transfer_mrp, fbi.initiator_remarks, fbi.first_approver_remarks, ");
			strBuff.append(" fbi.first_approved_by, fbi.first_approved_on, fbi.second_approved_by, ");
			strBuff.append(" fbi.cancelled_by, fbi.cancelled_on, fbi.rcrd_status, fbi.external_code , ");
			strBuff.append(" fapp.user_name first_approver_name,sapp.user_name second_approver_name,intu.user_name initiater_name, ");
			strBuff.append(" fbi.second_approved_on, fbi.second_approver_remarks, fbi.ext_txn_date, fbi.transfer_date, fbi.bonus_type,");
			strBuff.append(" fbi.commission_type, fbi.commission_rate, fbi.commission_value, fbi.tax1_type, ");
			strBuff.append(" fbi.tax1_rate, fbi.tax1_value, fbi.tax2_type, fbi.tax2_rate, fbi.tax2_value, ");
			strBuff.append(" fbi.tax3_type, fbi.tax3_rate, fbi.tax3_value ,fbi.dual_comm_type ");
			strBuff.append(" FROM foc_batch_items fbi,foc_batches fb,categories c,channel_grades cg, users u, users intu, users fapp, users sapp");
			strBuff.append(" WHERE fb.batch_id=? AND fb.batch_id=fbi.batch_id AND u.user_id=fbi.user_id  AND fbi.category_code=c.category_code AND");
			strBuff.append(" fbi.user_grade_code=cg.grade_code");
			strBuff.append(" AND fbi.status in(" + itemStatus + ") AND fbi.rcrd_status=? AND  fb.created_by = intu.user_id(+) AND fbi.first_approved_by = fapp.user_id(+) AND fbi.second_approved_by = sapp.user_id(+)");
			LogFactory.printLog("loadBatchO2CItemsMapQry", strBuff.toString(), log);
			return strBuff.toString();
		}

}
