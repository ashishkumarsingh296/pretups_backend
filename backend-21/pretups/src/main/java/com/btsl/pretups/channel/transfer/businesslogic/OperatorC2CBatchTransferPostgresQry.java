package com.btsl.pretups.channel.transfer.businesslogic;

import com.btsl.pretups.common.PretupsI;

public class OperatorC2CBatchTransferPostgresQry implements OperatorC2CBatchTransferQry {

	@Override
	public String loadBatchC2CMasterDetailsForTxrQry(String p_itemStatus, String p_currentLevel) {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(" SELECT * FROM ( SELECT DISTINCT c2cb.opt_batch_id,c2cb.batch_id,c2cb.batch_name,c2cb.batch_total_record,c2cb.user_id, p.product_name,p.short_name, p.unit_value, ");
		strBuff.append(" SUM(case cbi.status when ? then 1 else 0 end ) as new,SUM(case cbi.status when ? then 1 else 0 end ) as cncl, SUM(case cbi.status when ? then 1 else 0 end ) as close, ");
		strBuff.append(" c2cb.network_code,c2cb.network_code_for,c2cb.product_code, c2cb.modified_by, c2cb.modified_on ,p.product_type, ");
		strBuff.append(" c2cb.domain_code,c2cb.batch_date,c2cb.sms_default_lang,c2cb.sms_second_lang,cbi.transfer_type,cbi.transfer_sub_type,c2cb.created_by  ");
		strBuff.append(" FROM C2C_BATCHES c2cb,C2C_BATCH_ITEMS cbi,PRODUCTS p ");
		strBuff.append(" WHERE c2cb.product_code=p.product_code ");
		strBuff.append(" AND c2cb.status=?  AND c2cb.batch_id=cbi.batch_id AND c2cb.opt_batch_id is not null ");
		strBuff.append(" AND cbi.rcrd_status=? AND cbi.status IN ("+p_itemStatus+")AND cbi.transfer_sub_type=? ");
		strBuff.append(" GROUP BY c2cb.batch_id,c2cb.batch_name,c2cb.batch_total_record, ");
		strBuff.append(" c2cb.user_id,p.product_name,p.unit_value,c2cb.network_code,c2cb.network_code_for,c2cb.product_code, c2cb.modified_by, c2cb.modified_on, ");
		strBuff.append(" c2cb.sms_default_lang,c2cb.sms_second_lang,cbi.transfer_type,cbi.transfer_sub_type ,p.product_type,p.short_name ,c2cb.domain_code,c2cb.batch_date,c2cb.created_by, c2cb.opt_batch_id ");
		strBuff.append(" ORDER BY c2cb.batch_date DESC, c2cb.opt_batch_id desc ) ");
		strBuff.append(" as x ");
		if(PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(p_currentLevel))
			strBuff.append(" WHERE new>0 ");
		return strBuff.toString();
	}

	@Override
	public String loadBatchC2CMasterDetailsQry(String p_batchid) {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(" SELECT DISTINCT c2cb.batch_id,c2cb.batch_name,c2cb.batch_total_record, p.product_name, p.unit_value, c2cb.domain_code, c2cb.batch_date,");
		strBuff.append(" SUM(case cbi.status when ? then 1 else 0 end ) as NEW, SUM(case cbi.status when ? then 1 else 0 end ) as CNCL, SUM(case cbi.status when ? then 1 else 0 end ) as CLOSE, c2cb.created_on ");
		strBuff.append(" FROM C2C_BATCHES c2cb,C2C_BATCH_ITEMS cbi,PRODUCTS p");
		strBuff.append(" WHERE  c2cb.product_code=p.product_code AND c2cb.batch_id=cbi.batch_id ");
        strBuff.append(" AND c2cb.created_by =? ");
        
		if(p_batchid !=null)
		    strBuff.append(" AND c2cb.batch_id = ? ");
		else
		strBuff.append(" AND TRUNC(cbi.transfer_date) >= ? AND TRUNC(cbi.transfer_date) <= ? ");
				 
		strBuff.append(" GROUP BY c2cb.batch_id,c2cb.batch_name,c2cb.batch_total_record, p.product_name,p.unit_value,c2cb.network_code,c2cb.network_code_for,c2cb.product_code, c2cb.modified_by, c2cb.modified_on,p.product_type,p.short_name ,c2cb.domain_code, c2cb.batch_date, c2cb.created_on ");
		strBuff.append(" ORDER BY c2cb.created_on DESC  ");
		return strBuff.toString();
	}

	@Override
	public String loadBatchC2CMasterDetailsForWdrQry(String p_itemStatus, String p_currentLevel) {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(" SELECT * FROM ( SELECT DISTINCT c2cb.batch_id,c2cb.batch_name,c2cb.batch_total_record, c2cb.user_id,p.product_name,p.short_name,  p.unit_value,  ");
		strBuff.append(" SUM(case cbi.status when ? then 1 else 0 end ) as NEW,SUM(case cbi.status when ? then 1 else 0 end ) as cncl,  SUM(case cbi.status when ? then 1 else 0 end ) as CLOSE, ");
		strBuff.append("c2cb.network_code,c2cb.network_code_for,c2cb.product_code, c2cb.modified_by, c2cb.modified_on , ");
		strBuff.append(" p.product_type,  c2cb.domain_code,c2cb.batch_date,c2cb.sms_default_lang,c2cb.sms_second_lang,cbi.transfer_type,cbi.transfer_sub_type,c2cb.created_by   ");
		strBuff.append(" FROM C2C_BATCHES c2cb,C2C_BATCH_ITEMS cbi,PRODUCTS p  ");
		strBuff.append(" WHERE c2cb.user_id=? AND c2cb.PRODUCT_CODE=p.PRODUCT_CODE");
		strBuff.append(" AND c2cb.status= ? AND c2cb.product_code=p.product_code AND c2cb.batch_id=cbi.batch_id  ");
		strBuff.append(" AND cbi.rcrd_status= ? AND cbi.status IN ("+p_itemStatus+") AND cbi.transfer_sub_type=? ");
		strBuff.append(" GROUP BY c2cb.batch_id,c2cb.batch_name,c2cb.batch_total_record,c2cb.user_id, p.product_name,p.unit_value,c2cb.network_code,c2cb.network_code_for,c2cb.product_code,");
		strBuff.append(" c2cb.modified_by, c2cb.modified_on,  c2cb.sms_default_lang,c2cb.sms_second_lang,cbi.transfer_type,cbi.transfer_sub_type ,p.product_type,p.short_name ,c2cb.domain_code,c2cb.batch_date,c2cb.created_by ");
		strBuff.append(" ORDER BY c2cb.batch_date DESC ) ");
		if(PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(p_currentLevel))
			strBuff.append(" WHERE new>0 ");
		return strBuff.toString();
	}

	@Override
	public String loadBatchItemsMapQry(String p_itemStatus) {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(" SELECT cbi.batch_id, cbi.batch_detail_id, c.category_name,c.category_code, usender.msisdn as sendermsisdn, cbi.msisdn, cbi.user_id, ");
		strBuff.append(" cbi.modified_on ,cbi.status, cg.grade_name,cbi.user_grade_code, cbi.ext_txn_no, ");
		strBuff.append(" cbi.ext_txn_date,cbi.requested_quantity,cbi.transfer_mrp,cbi.approved_by,");
		strBuff.append(" cbi.approved_on, c2cb.created_by,c2cb.created_on, c2cb.modified_on as batch_modified_on,u.login_id , cbi.modified_by,");
		strBuff.append(" cbi.reference_no,cbi.ext_txn_no, cbi.txn_profile, cbi.commission_profile_set_id,cbi.commission_profile_ver,");
		strBuff.append(" cbi.commission_profile_detail_id,cbi.requested_quantity, cbi.transfer_mrp, cbi.initiator_remarks, ");
		strBuff.append(" cbi.approver_remarks, cbi.approved_by, cbi.approved_on, cbi.cancelled_by, ");
		strBuff.append(" cbi.cancelled_on, cbi.rcrd_status, cbi.external_code , fapp.user_name approver_name,");
		strBuff.append(" intu.user_name initiater_name,  cbi.ext_txn_date, cbi.transfer_date, cbi.commission_type, ");
		strBuff.append(" cbi.commission_rate, cbi.commission_value, cbi.tax1_type, cbi.tax1_rate, cbi.tax1_value, ");
		strBuff.append(" cbi.tax2_type, cbi.tax2_rate, cbi.tax2_value, cbi.tax3_type, cbi.tax3_rate, cbi.tax3_value,cbi.transfer_type ,cbi.transfer_sub_type ");
		strBuff.append(" FROM C2C_BATCH_ITEMS cbi Left OUTER JOIN USERS fapp ON cbi.approved_by = fapp.user_id, C2C_BATCHES c2cb Left OUTER JOIN USERS intu ON c2cb.created_by = intu.user_id,CATEGORIES c,CHANNEL_GRADES cg, USERS u, USERS usender ");
		strBuff.append(" WHERE c2cb.opt_batch_id=? AND c2cb.batch_id=cbi.batch_id AND c2cb.opt_batch_id is not null AND u.user_id=cbi.user_id and usender.user_id= c2cb.user_id");
		strBuff.append(" AND cbi.category_code=c.category_code AND cbi.user_grade_code=cg.grade_code AND cbi.status IN("+ p_itemStatus +") ");
		strBuff.append(" AND cbi.rcrd_status=? ");
		return strBuff.toString();
	}

	@Override
	public String processOrderByBatchQry() {
		StringBuffer sqlBuffer = new StringBuffer("SELECT c2cb.batch_total_record,");
        sqlBuffer.append("SUM(case cbi.status when ? then 1 else 0 end ) as new, SUM(case cbi.status when ? then 1 else 0 end ) as appr,SUM(case cbi.status when ? then 1 else 0 end ) as cncl,  SUM(case cbi.status when ? then 1 else 0 end ) as close ");
        sqlBuffer.append(" FROM c2c_batches c2cb,c2c_batch_items cbi ");
        sqlBuffer.append(" WHERE c2cb.batch_id=cbi.batch_id AND c2cb.opt_batch_id=cbi.opt_batch_id AND c2cb.batch_id=? AND c2cb.opt_batch_id=? group by c2cb.batch_total_record");
        return sqlBuffer.toString();
	}

	@Override
	public String closeOrderByBatchQry() {
		StringBuffer sqlBuffer = new StringBuffer("SELECT fb.batch_total_record,");
        sqlBuffer.append(" SUM(case cbi.status when ? then 1 else 0 end ) as new, SUM(case cbi.status when ? then 1 else 0 end ) as appr, SUM(case cbi.status when ? then 1 else 0 end ) as cncl, SUM(case cbi.status when ? then 1 else 0 end ) as close ");
        sqlBuffer.append(" FROM c2c_batches fb,c2c_batch_items cbi ");
        sqlBuffer.append(" WHERE fb.batch_id=cbi.batch_id AND fb.batch_id=? AND fb.opt_batch_id=? group by fb.batch_total_record");
        return sqlBuffer.toString();
	}

	@Override
	public String loadBatchC2CMasterDetailsQry(String p_batchid, String p_categoryCode, String p_loginCatCode, String p_userName) {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(" SELECT DISTINCT cb.batch_id,cb.batch_name,cb.batch_total_record, p.product_name, p.unit_value, cb.domain_code, cb.batch_date,");
		strBuff.append(" SUM(case cbi.status when ? then 1 else 0 end ) as NEW, SUM(case cbi.status when ? then 1 else 0 end ) as CNCL,   SUM(case cbi.status when ? then 1 else 0 end ) as CLOSE,cb.created_on ");
		strBuff.append(" FROM C2C_BATCHES cb,C2C_BATCH_ITEMS cbi,PRODUCTS p,USERS U");
		strBuff.append(" WHERE cb.product_code=p.product_code AND cb.batch_id=cbi.batch_id AND cb.created_by =u.user_id");
		if(p_batchid !=null)
		{
			strBuff.append(" AND cb.batch_id = ? ");                        
			strBuff.append(" AND cb.created_by =? ");
		}
		else
		{
			strBuff.append(" AND DATE_TRUNC('day',cbi.transfer_date::TIMESTAMP) >= ? AND DATE_TRUNC('day',cbi.transfer_date::TIMESTAMP) <= ? ");
			if(p_categoryCode.equals(p_loginCatCode))
				strBuff.append(" AND cb.created_by =? ");
			else
				strBuff.append("AND u.CATEGORY_CODE="+p_categoryCode); 	
			strBuff.append("AND cb.created_by="+p_userName);
		}                                
		strBuff.append(" GROUP BY cb.batch_id,cb.batch_name,cb.batch_total_record, p.product_name,p.unit_value,cb.network_code,cb.network_code_for,cb.product_code, cb.modified_by, cb.modified_on,p.product_type,p.short_name ,cb.domain_code, cb.batch_date, cb.created_on ");
		strBuff.append(" ORDER BY cb.created_on DESC  ");
		return strBuff.toString();
	}

}
