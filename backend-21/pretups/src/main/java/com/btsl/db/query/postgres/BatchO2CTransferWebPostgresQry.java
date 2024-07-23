package com.btsl.db.query.postgres;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.web.pretups.channel.transfer.businesslogic.BatchO2CTransferWebQry;

public class BatchO2CTransferWebPostgresQry  implements BatchO2CTransferWebQry{
	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public String loadUsersForBatchO2CQry(String[] categoryCode,
			String[] receiverStatusAllowed, String[] geographicalDomainCode) {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT U.user_id,U.user_code,U.msisdn,U.login_id,U.category_code,C.category_name,CG.grade_code,U.status,");
		strBuff.append("CG.grade_name,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend,U.external_code, ");
		strBuff.append("CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
		strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
		strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
		strBuff.append("FROM users U,channel_users CU,channel_grades CG,categories C,user_geographies UG, ");
		strBuff.append("commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
		strBuff.append("WHERE U.network_code=? AND U.user_id=CU.user_id AND U.user_id=UG.user_id AND ");
		strBuff.append("U.category_code=C.category_code AND U.category_code=CG.category_code AND CU.user_grade=CG.grade_code ");
		strBuff.append(" AND U.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
		strBuff.append("AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");

		strBuff.append("AND TP.profile_id = CU.transfer_profile_id AND C.category_code IN (");
		for (int i = 0; i < categoryCode.length; i++) {
			strBuff.append(" ?");
			if (i != categoryCode.length - 1) {
				strBuff.append(",");
			}
		}
		strBuff.append(")");

		strBuff.append(" AND C.domain_code =? AND U.status IN (");
		for (int i = 0; i < receiverStatusAllowed.length; i++) {
			strBuff.append(" ?");
			if (i != receiverStatusAllowed.length - 1) {
				strBuff.append(",");
			}
		}
		strBuff.append(")");

		strBuff.append(" AND C.status='Y' AND UG.grph_domain_code IN (");

		strBuff.append(" WITH RECURSIVE q AS ( ");
		strBuff.append("SELECT GD1.grph_domain_code, GD1.status FROM geographical_domains GD1  WHERE ");
		strBuff.append(" GD1.grph_domain_code IN( ");
		for (int i = 0; i < geographicalDomainCode.length; i++) { 
			strBuff.append(" ?"); 
			if (i != geographicalDomainCode.length - 1) {
				strBuff.append(",");
			}
		}
		strBuff.append(")");
		strBuff.append("UNION ALL ");
		strBuff.append("SELECT GD2.grph_domain_code , GD2.status FROM geographical_domains GD2  ");
		strBuff.append("join q on q.grph_domain_code = GD2.parent_grph_domain_code ");
		strBuff.append(")SELECT grph_domain_code FROM  q where status = 'Y' ");


		strBuff.append(")");

		strBuff.append(" AND CPSV.applicable_from =COALESCE ( (SELECT MAX(applicable_from) FROM ");
		strBuff.append("commission_profile_set_version WHERE applicable_from <= ? AND ");
		strBuff.append("comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from) ");
		strBuff.append("ORDER BY C.sequence_no,CU.user_grade,U.login_id");
		LogFactory.printLog("loadUsersForBatchO2CQry", strBuff.toString(), log);
		return strBuff.toString();
	}
	
	@Override
	public String validateUsersForBatchO2CQry(String receiverStatusAllowed,
			String categoryCode, String geographicalDomainCode) {
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT U.user_id,CPSV.dual_comm_type,U.user_code,U.msisdn,U.login_id,U.category_code,C.category_name,CG.grade_code,U.status,");
        strBuff.append("CG.grade_name,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend,U.external_code, ");
        strBuff.append("CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
        strBuff.append("FROM users U,channel_users CU,channel_grades CG,categories C,user_geographies UG, ");
        strBuff.append("commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append("WHERE U.user_code=? AND U.network_code=? AND U.user_id=CU.user_id AND U.user_id=UG.user_id AND ");
        strBuff.append("U.category_code=C.category_code AND U.category_code=CG.category_code AND CU.user_grade=CG.grade_code ");
        strBuff.append("AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append("AND TP.profile_id = CU.transfer_profile_id AND C.category_code IN (" + categoryCode + ") ");
        strBuff.append("AND C.domain_code =? AND U.status IN (" + receiverStatusAllowed + ") AND C.status='Y' ");
        strBuff.append("AND UG.grph_domain_code IN (" );      
        
        strBuff.append(" WITH RECURSIVE q AS ( ");
        strBuff.append("SELECT GD1.grph_domain_code,GD1.status FROM geographical_domains GD1 WHERE GD1.grph_domain_code IN(" + geographicalDomainCode + ") ");
        strBuff.append("UNION ALL ");
        strBuff.append("SELECT GD2.grph_domain_code,GD2.status FROM geographical_domains GD2 ");
        strBuff.append("join q on q.grph_domain_code = GD2.parent_grph_domain_code ");
        strBuff.append("WHERE GD2.status = 'Y' ");
        strBuff.append(")SELECT grph_domain_code FROM q where status = 'Y' ");
        
        strBuff.append( ") ");
        strBuff.append("AND CPSV.applicable_from = COALESCE ( (SELECT MAX(applicable_from) FROM ");
        strBuff.append("commission_profile_set_version WHERE applicable_from <= ? AND ");
        strBuff.append("comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from) ");
        strBuff.append("ORDER BY C.sequence_no,CU.user_grade,U.login_id");
        LogFactory.printLog("validateUsersForBatchO2CQry", strBuff.toString(), log);
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
	        strBuff.append(" FROM o2c_batch_items fbi  left join  users fapp on fbi.first_approved_by = fapp.user_id left join users sapp on fbi.second_approved_by = sapp.user_id ,o2c_batches fb left join users intu on fb.created_by = intu.user_id,categories c,channel_grades cg, users u");
	        strBuff.append(" WHERE fb.batch_id=? AND fb.transfer_type=? AND fb.transfer_sub_type=? AND fb.batch_id=fbi.batch_id AND u.user_id=fbi.user_id  AND fbi.category_code=c.category_code AND");
	        strBuff.append(" fbi.user_grade_code=cg.grade_code");
	        strBuff.append(" AND fbi.status in(" + itemStatus + ") AND fbi.rcrd_status=? ");
	        LogFactory.printLog("loadBatchO2CItemsMapQry", strBuff.toString(), log);
		return strBuff.toString();
	}
	
	@Override
	public String loadO2CBatchMasterDetailsQry(String currentLevel, String itemStatus) {
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT * FROM (SELECT DISTINCT fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name,p.short_name,p.unit_value,sum(case fbi.status when ? then 1 else 0 end ) as new, ");
        strBuff.append(" SUM(case fbi.status when ? then 1 else 0 end ) appr1,SUM(case fbi.status when ? then 1 else 0 end ) cncl,  ");
        strBuff.append(" SUM(case fbi.status when ? then 1 else 0 end ) appr2,SUM(case fbi.status when ? then 1 else 0 end ) closed,  ");
        strBuff.append(" fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on ,p.product_type,fb.domain_code,fb.batch_date,fb.sms_default_lang,fb.sms_second_lang, fbi.category_code ");
        strBuff.append(" FROM user_geographies ug , o2c_batch_geographies fbg,o2c_batches fb,o2c_batch_items fbi,products p,user_domains ud, ");
        strBuff.append(" user_product_types upt,geographical_domains gd  ");
        strBuff.append(" WHERE ug.user_id=? AND ug.grph_domain_code=fbg.geography_code AND ug.grph_domain_code=gd.grph_domain_code AND gd.status='Y' ");
        strBuff.append(" AND ud.user_id=ug.user_id AND ud.domain_code= fb.domain_code ");
        strBuff.append(" AND upt.user_id=ud.user_id AND upt.product_type=p.product_type ");
        strBuff.append(" AND fbg.batch_id=fb.batch_id AND fb.status=? ");
        strBuff.append(" AND fb.product_code=p.product_code  ");
        strBuff.append(" AND fb.batch_id=fbi.batch_id AND fbi.rcrd_status=? AND fbi.status IN (" + itemStatus + ") ");
        strBuff.append(" AND (SELECT count(fbg1.geography_code) FROM o2c_batch_geographies fbg1 WHERE fbg1.batch_id=fbg.batch_id) <= (SELECT count(ug1.user_id) FROM user_geographies ug1 ,geographical_domains gd WHERE ug1.user_id=ug.user_id AND ug1.grph_domain_code=gd.grph_domain_code AND gd.status='Y') ");
        strBuff.append(" GROUP BY fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name,p.unit_value,fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on,fb.sms_default_lang,fb.sms_second_lang,p.product_type,fbg.geography_code,p.short_name ,fb.domain_code,fb.batch_date, fbi.category_code ORDER BY fb.batch_date DESC ) X ");
        
        if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)) {
            strBuff.append(" WHERE  new>0 ");
        } else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel)){
			strBuff.append(" WHERE  appr1>0 ");
        }
        LogFactory.printLog("loadO2CBatchMasterDetailsQry", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public String closeOrderByBatchLoadNetworkStockQry() {
		StringBuilder sqlBuffer = new StringBuilder("SELECT network_code, network_code_for, product_code, wallet_type, wallet_created, wallet_returned, ");
        sqlBuffer.append("wallet_balance, wallet_sold, last_txn_no, last_txn_type, last_txn_balance, previous_balance, ");
        sqlBuffer.append("modified_by, modified_on, created_on, created_by, daily_stock_updated_on ");
        sqlBuffer.append("FROM network_stocks ");
        sqlBuffer.append("WHERE network_code = ? AND network_code_for = ? AND wallet_type = ? AND ");
        sqlBuffer.append("DATE_TRUNC('day',daily_stock_updated_on::TIMESTAMP) <> DATE_TRUNC('day',?::TIMESTAMP) FOR UPDATE ");
        LogFactory.printLog("loadO2CBatchMasterDetailsQry", sqlBuffer.toString(), log);
        return sqlBuffer.toString();
	}
	
	@Override
	public String closeOrderByBatchSelectNetworkStockQry() {
		StringBuilder  sqlBuffer = new StringBuilder(" SELECT ");
		sqlBuffer.append(" wallet_type, wallet_balance , wallet_sold ");
		sqlBuffer.append(" FROM network_stocks ");
		sqlBuffer.append(" WHERE network_code = ? AND product_code = ? AND network_code_for = ? and wallet_type = ? FOR UPDATE  ");
		LogFactory.printLog("closeOrderByBatchSelectNetworkStockQry", sqlBuffer.toString(), log);
		return  sqlBuffer.toString();
	}
	
	@Override
	public String closeOrderByBatchSelectUserBalancesQry() {
		StringBuilder sqlBuffer = new StringBuilder(" SELECT user_id, network_code, network_code_for, product_code, balance, prev_balance, ");
	        sqlBuffer.append("last_transfer_type, last_transfer_no, last_transfer_on, daily_balance_updated_on ");
	        sqlBuffer.append("FROM user_balances ");
	        sqlBuffer.append("WHERE user_id = ? AND DATE_TRUNC('day',daily_balance_updated_on::TIMESTAMP)<> DATE_TRUNC('day',?::TIMESTAMP) FOR UPDATE  ");
		return  sqlBuffer.toString();
	}
	
	@Override
	public String closeOrderByBatchSelectBalanceQry() {
		StringBuilder sqlBuffer = new StringBuilder("  SELECT ");
	        sqlBuffer.append(" balance,balance_type ");
	        sqlBuffer.append(" FROM user_balances ");
	        sqlBuffer.append(" WHERE user_id = ? and product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE ");
		return sqlBuffer.toString();
	}
	
	@Override
	public String closeOrderByBatchItemsDetailsQry() {
		StringBuilder sqlBuffer = new StringBuilder("SELECT ob.batch_total_record,SUM(case obi.status when ? then 1 else 0 end ) as new,");
	        sqlBuffer.append(" SUM(case obi.status when ? then 1 else 0 end ) appr1,SUM(case obi.status when ? then 1 else 0 end ) cncl, ");
	        sqlBuffer.append(" SUM(case obi.status when ? then 1 else 0 end ) appr2,SUM(case obi.status when ? then 1 else 0 end ) closed ");
	        sqlBuffer.append(" FROM o2c_batches ob,o2c_batch_items obi ");
	        sqlBuffer.append(" WHERE ob.batch_id=obi.batch_id AND ob.batch_id=? group by ob.batch_total_record");
	        LogFactory.printLog("closeOrderByBatchItemsDetailsQry", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}
	
	@Override
	public String processOrderByBatchItemsDetailsQry() {
		StringBuilder sqlBuffer = new StringBuilder("SELECT fb.batch_total_record,SUM(case fbi.status when ? then 1 else 0 end ) as new,");
        sqlBuffer.append(" SUM(case fbi.status when ? then 1 else 0 end ) appr1,SUM(case fbi.status when ? then 1 else 0 end ) cncl, ");
        sqlBuffer.append(" SUM(case fbi.status when ? then 1 else 0 end ) appr2,SUM(case fbi.status when ? then 1 else 0 end ) closed ");
        sqlBuffer.append(" FROM o2c_batches fb,o2c_batch_items fbi ");
        sqlBuffer.append(" WHERE fb.batch_id=fbi.batch_id AND fb.batch_id=? group by fb.batch_total_record");
        LogFactory.printLog("processOrderByBatchItemsDetailsQry", sqlBuffer.toString(), log);  
		return sqlBuffer.toString();
	}

	
}
