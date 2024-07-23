package com.btsl.db.query.postgres;

import com.btsl.pretups.common.PretupsI;
import com.web.pretups.channel.transfer.businesslogic.C2CBatchTransferWebQry;


public class C2CBatchTransferWebPostgresQry implements C2CBatchTransferWebQry {

	@Override
	public String loadBatchC2CMasterDetailsForTxrQry(String p_itemStatus,
			String p_currentLevel) {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT * FROM ( SELECT DISTINCT c2cb.batch_id,c2cb.batch_name,c2cb.batch_total_record,c2cb.user_id, ");
		strBuff.append("  SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS NEW,SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS cncl, SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS CLOSE, ");
		strBuff.append(" c2cb.network_code,c2cb.network_code_for,c2cb.product_code, c2cb.modified_by, c2cb.modified_on , ");
		strBuff.append(" c2cb.domain_code,c2cb.batch_date,c2cb.sms_default_lang,c2cb.sms_second_lang,cbi.transfer_type,cbi.transfer_sub_type,c2cb.created_by, cbi.category_code, p.PRODUCT_NAME ");
		strBuff.append(" FROM C2C_BATCHES c2cb,C2C_BATCH_ITEMS cbi, PRODUCTS p ");
		strBuff.append(" WHERE c2cb.user_id=?");
		strBuff.append(" AND c2cb.status=?  AND c2cb.batch_id=cbi.batch_id AND cbi.PRODUCT_CODE = p.PRODUCT_CODE ");
		strBuff.append(" AND cbi.rcrd_status=? AND cbi.status IN ("
				+ p_itemStatus
				+ ")AND cbi.transfer_sub_type=? GROUP BY c2cb.batch_id,c2cb.batch_name,c2cb.batch_total_record, ");
		strBuff.append(" c2cb.user_id,c2cb.network_code,c2cb.network_code_for,c2cb.product_code, c2cb.modified_by, c2cb.modified_on, p.PRODUCT_NAME, ");
		strBuff.append(" c2cb.sms_default_lang,c2cb.sms_second_lang,cbi.transfer_type,cbi.transfer_sub_type ,c2cb.domain_code,c2cb.batch_date,c2cb.created_by , cbi.category_code ");
		strBuff.append(" ORDER BY c2cb.batch_date DESC ) A ");
		if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(p_currentLevel)) {
			strBuff.append(" WHERE new>0 ");
		}

		return strBuff.toString();

	}

	@Override
	public String loadBatchC2CMasterDetailsForWdrQry(String p_itemStatus,
			String p_currentLevel) {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT * FROM ( SELECT DISTINCT c2cb.batch_id,c2cb.batch_name,c2cb.batch_total_record, c2cb.user_id, ");
		strBuff.append(" SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS NEW,SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS cncl, SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS CLOSE, ");
		strBuff.append(" c2cb.network_code,c2cb.network_code_for,c2cb.product_code, c2cb.modified_by, c2cb.modified_on , ");
		strBuff.append("   c2cb.domain_code,c2cb.batch_date,c2cb.sms_default_lang,c2cb.sms_second_lang,cbi.transfer_type,cbi.transfer_sub_type,c2cb.created_by,cbi.category_code, p.PRODUCT_NAME   ");
		strBuff.append(" FROM C2C_BATCHES c2cb,C2C_BATCH_ITEMS cbi, PRODUCTS p  ");
		strBuff.append(" WHERE c2cb.user_id=? ");
		strBuff.append(" AND c2cb.status= ? AND c2cb.batch_id=cbi.batch_id AND cbi.PRODUCT_CODE = p.PRODUCT_CODE ");
		strBuff.append(" AND cbi.rcrd_status= ? AND cbi.status IN ("
				+ p_itemStatus + ") AND cbi.transfer_sub_type=? ");
		strBuff.append(" GROUP BY c2cb.batch_id,c2cb.batch_name,c2cb.batch_total_record,c2cb.user_id, c2cb.network_code,c2cb.network_code_for,c2cb.product_code,");
		strBuff.append(" c2cb.modified_by, c2cb.modified_on,  c2cb.sms_default_lang,c2cb.sms_second_lang,cbi.transfer_type,cbi.transfer_sub_type ,c2cb.domain_code,c2cb.batch_date,c2cb.created_by,cbi.category_code, p.PRODUCT_NAME ");
		strBuff.append(" ORDER BY c2cb.batch_date DESC ) A ");
		if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(p_currentLevel)) {
			strBuff.append(" WHERE new>0 ");
		}

		return strBuff.toString();

	}

	@Override
	public String processOrderByBatchQry() {
		StringBuilder sqlBuffer = new StringBuilder();
		sqlBuffer
				.append("SELECT c2cb.batch_total_record,SUM(DECODE(cbi.status,?,1,0)) as new,  ");
		sqlBuffer
				.append(" SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS appr,SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS cncl, ");
		sqlBuffer
				.append(" SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS close ");
		sqlBuffer.append(" FROM c2c_batches c2cb,c2c_batch_items cbi ");
		sqlBuffer
				.append(" WHERE c2cb.batch_id=cbi.batch_id AND c2cb.batch_id=? group by c2cb.batch_total_record");

		return sqlBuffer.toString();

	}

	@Override
	public String loadBatchDetailsListQry() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT distinct c2cb.batch_id, c2cb.network_code, c2cb.network_code_for,  ");
		strBuff.append(" c2cb.batch_name, c2cb.status, L.lookup_name status_desc, c2cb.domain_code, c2cb.product_code, c2cb.batch_file_name, ");
		strBuff.append(" c2cb.batch_total_record, c2cb.batch_date, INTU.user_name initated_by, c2cb.created_on, P.product_name, D.domain_name, ");
		strBuff.append(" cbi.batch_detail_id, cbi.category_code, cbi.msisdn, cbi.user_id, cbi.status status_item,  cbi.user_grade_code, cbi.reference_no, ");
		strBuff.append(" cbi.transfer_date, cbi.txn_profile, cbi.commission_profile_set_id,  ");
		strBuff.append(" cbi.commission_profile_ver, cbi.commission_profile_detail_id, cbi.commission_type, cbi.commission_rate, ");
		strBuff.append(" cbi.commission_value, cbi.tax1_type, cbi.tax1_rate, cbi.tax1_value, cbi.tax2_type, cbi.tax2_rate, cbi.tax2_value, ");
		strBuff.append(" cbi.tax3_type, cbi.tax3_rate, cbi.tax3_value, cbi.requested_quantity, cbi.transfer_mrp, cbi.initiator_remarks, cbi.approver_remarks,");
		strBuff.append(" coalesce(FAPP.user_name,CNCL_USR.user_name) approved_by, coalesce(cbi.approved_on,cbi.cancelled_on) approved_on,");
		strBuff.append(" CNCL_USR.user_name cancelled_by, cbi.cancelled_on, cbi.rcrd_status, cbi.external_code, ");
		strBuff.append(" U.user_name, C.category_name, CG.grade_name ");
		strBuff.append(" FROM c2c_batches c2cb LEFT JOIN users INTU ON (c2cb.created_by = INTU.user_id), products P, domains D, c2c_batch_items cbi LEFT JOIN users FAPP ON (cbi.approved_by = FAPP.user_id) ");
		strBuff.append(" LEFT JOIN users CNCL_USR ON (cbi.cancelled_by = CNCL_USR.user_id),categories C, users U, users SAPP, users TAPP, channel_grades CG, lookups L ");
		strBuff.append(" WHERE c2cb.batch_id=?  ");
		strBuff.append(" AND cbi.batch_id = c2cb.batch_id AND cbi.category_code = C.category_code AND cbi.user_id = U.user_id ");
		strBuff.append(" AND P.product_code = c2cb.product_code AND D.domain_code = c2cb.domain_code ");
		strBuff.append(" AND CG.grade_code = cbi.user_grade_code ");
		strBuff.append(" AND L.lookup_type = ? ");
		strBuff.append(" AND L.lookup_code = c2cb.status ");
		strBuff.append(" ORDER BY cbi.batch_detail_id DESC, cbi.category_code, cbi.status ");

		return strBuff.toString();

	}

	@Override
	public String loadBatchC2CMasterDetailsQry(String p_batchid,
			String pLOGinID, String p_categoryCode, String pLOGinCatCode,
			String p_userName) {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT DISTINCT cb.batch_id,cb.batch_name,cb.batch_total_record, p.product_name, p.unit_value, cb.domain_code, cb.batch_date, cb.product_code, ");
		strBuff.append(" SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS NEW,");
		strBuff.append(" SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS CNCL,  ");
		strBuff.append(" SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS CLOSE,cb.created_on ");
		strBuff.append(" FROM C2C_BATCHES cb,C2C_BATCH_ITEMS cbi,PRODUCTS p,USERS U");
		strBuff.append(" WHERE cb.product_code=p.product_code AND cb.batch_id=cbi.batch_id AND cb.created_by =u.user_id");
		if (p_batchid != null) {
			strBuff.append(" AND cb.batch_id = ? ");
			strBuff.append(" AND cb.created_by =? ");
		} else {
			strBuff.append(" AND date_trunc('day',cbi.transfer_date::TIMESTAMP) >= ? AND date_trunc('day',cbi.transfer_date::TIMESTAMP) <= ? ");
			if (p_categoryCode.equals(pLOGinCatCode)) {
				strBuff.append(" AND cb.created_by =? ");
			} else {
				strBuff.append("AND u.CATEGORY_CODE=" + p_categoryCode);
			}
			strBuff.append("AND cb.created_by=" + p_userName);

		}
		strBuff.append(" GROUP BY cb.batch_id,cb.batch_name,cb.batch_total_record, p.product_name,p.unit_value,cb.network_code,cb.network_code_for,cb.product_code, cb.modified_by, cb.modified_on,p.product_type,p.short_name ,cb.domain_code, cb.batch_date, cb.created_on ");
		strBuff.append(" ORDER BY cb.created_on DESC  ");
		return strBuff.toString();

	}
	
	
	@Override
	public String loadBatchC2CMasterDetailsForTxrAndWdrQry(String p_itemStatus,
			String p_currentLevel, String category) {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT * FROM ( SELECT DISTINCT c2cb.batch_id,c2cb.batch_name,c2cb.batch_total_record,c2cb.user_id, ");
		strBuff.append("  SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS NEW,SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS cncl, SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS CLOSE, ");
		strBuff.append(" c2cb.network_code,c2cb.network_code_for, c2cb.modified_by, c2cb.modified_on , ");
		strBuff.append(" c2cb.domain_code,c2cb.batch_date,c2cb.sms_default_lang,c2cb.sms_second_lang,cbi.transfer_type,cbi.transfer_sub_type,c2cb.created_by, cbi.category_code ");
		strBuff.append(" , p.product_code, p.product_name ");
		strBuff.append(" FROM C2C_BATCHES c2cb,C2C_BATCH_ITEMS cbi, products p  ");
		strBuff.append(" WHERE c2cb.user_id=?");
		strBuff.append(" AND cbi.product_code=p.product_code ");
		if(!category.equalsIgnoreCase("ALL")) {
			strBuff.append(" AND cbi.CATEGORY_CODE=?");
		}
		strBuff.append(" AND c2cb.status=?  AND c2cb.batch_id=cbi.batch_id ");
		strBuff.append(" AND cbi.rcrd_status=? AND cbi.status IN ("
				+ p_itemStatus
				+ ")AND cbi.transfer_sub_type in(?, ?) GROUP BY c2cb.batch_id,c2cb.batch_name,c2cb.batch_total_record, ");
		strBuff.append(" c2cb.user_id,c2cb.network_code,c2cb.network_code_for,c2cb.product_code, c2cb.modified_by, c2cb.modified_on, ");
		strBuff.append(" c2cb.sms_default_lang,c2cb.sms_second_lang,cbi.transfer_type,cbi.transfer_sub_type ,c2cb.domain_code,c2cb.batch_date,c2cb.created_by , cbi.category_code ");
		strBuff.append(" , p.product_code, p.product_name ");
		strBuff.append(" ORDER BY c2cb.batch_date DESC ) A ");
		if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(p_currentLevel)) {
			strBuff.append(" WHERE new>0 ");
		}

		return strBuff.toString();

	}
	
	
	@Override
	public String loadBatchDetailsListDownloadQry() {
		StringBuilder strBuff = new StringBuilder();
		
		strBuff.append(" \r\n"
				+ " select  cb.batch_id ,cb.batch_name ,cb.status,u.user_id,u.user_name,cbi.user_grade_code,cb.created_by,cb.created_on ,cbi.approved_by ,cbi.cancelled_by,\r\n"
				+ " cbi.initiator_remarks, cb.batch_file_name ,cb.batch_total_record ,cb.batch_date,cbi.batch_detail_id ,\r\n"
				+ " d.domain_code ,d.domain_name ,p.product_code ,p.product_name ,c.category_code ,c.category_name ,u.msisdn,\r\n"
				+ " cbi.reference_no ,cbi.user_grade_code ,cbi.transfer_date ,cbi.txn_profile ,cbi.commission_profile_set_id ,cbi.commission_profile_ver ,\r\n"
				+ " cbi.commission_profile_detail_id ,cbi.commission_rate ,cbi.commission_type ,cbi.requested_quantity ,cbi.transfer_mrp ,\r\n"
				+ " cbi.initiator_remarks ,cbi.approved_by ,cbi.approved_on ,cbi.approver_remarks ,u.external_code ,\r\n"
				+ " cbi.tax1_type,cbi.tax1_rate,cbi.tax1_value,cbi.tax2_type, cbi.tax2_rate, cbi.tax2_value,cbi.tax3_type,cbi.tax3_rate,cbi.tax3_value\r\n"
				+ "  from c2c_batches cb,c2c_batch_items cbi,domains d ,products p ,categories c,users u\r\n"
				+ "  where cb.batch_id =cbi.batch_id   \r\n"
				+ "  and cbi.batch_id = ? \r\n"
				+ "  and d.domain_code = cb.domain_code \r\n"
				+ "  and p.product_code =cbi.product_code \r\n"
				+ "  and cbi.category_code = c.category_code\r\n"
				+ "  and u.user_id =cbi.user_id ");

		return strBuff.toString();

	}
	@Override
	public String loadBatchDetailsByBatchIdQry() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(""
				+ "    \r\n"
				+ "    SELECT  SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS NEW,  \r\n"
				+ "	  SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS CNCL,    \r\n"
				+ "	  SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS CLOSE,\r\n"
				+ "	  ccb.BATCH_ID ,ccb.BATCH_NAME,p.PRODUCT_NAME ,p.PRODUCT_CODE,\r\n"
				+ "	  ccb.BATCH_DATE ,ccb.BATCH_TOTAL_RECORD	  \r\n"
				+ "      FROM C2C_BATCHES ccb,C2C_BATCH_ITEMS cbi,PRODUCTS p\r\n"
				+ "      WHERE  ccb.BATCH_ID = ?\r\n"
				+ "      AND cbi.BATCH_ID =ccb.BATCH_ID \r\n"
				+ "      AND p.PRODUCT_CODE =cbi.PRODUCT_CODE\r\n"
				+ "      GROUP BY  ccb.BATCH_ID ,ccb.BATCH_NAME ,p.PRODUCT_NAME ,p.PRODUCT_CODE,ccb.BATCH_DATE ,ccb.BATCH_TOTAL_RECORD\r\n"
				+ " "
				+ "");
		return strBuff.toString();
	}
	@Override
	public String loadBatchDetailsByAdvancedQry() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(""
				+ "\r\n"
				+ "  select cb.batch_id,cb.batch_name,cb.batch_total_record, p.product_name,p.PRODUCT_CODE ,p.unit_value, cb.domain_code, cb.batch_date,\r\n"
				+ " SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS NEW,  \r\n"
				+ "	SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS CNCL,    \r\n"
				+ "	SUM(CASE cbi.status WHEN ? THEN 1 ELSE 0 END) AS CLOSE,\r\n"
				+ "  cb.created_on \r\n"
				+ " from c2c_batches cb,c2c_batch_items cbi,domains d ,products p ,categories c,users u\r\n"
				+ "  where cb.batch_id =cbi.batch_id   \r\n"
				+ "   AND date_trunc('day',cbi.transfer_date::TIMESTAMP) >= ? AND date_trunc('day',cbi.transfer_date::TIMESTAMP) <= ?\r\n"
				+ "  and d.domain_code = cb.domain_code \r\n"
				+ "  AND d.DOMAIN_CODE = ?\r\n"
				+ "  AND cb.CREATED_BY = ?\r\n"
				+ "  and p.product_code =cbi.product_code \r\n"
				+ "  and cbi.category_code = c.category_code\r\n"
				+ "  AND c.CATEGORY_CODE = ?\r\n"
				+ "  and u.user_id =cbi.user_id \r\n"
				+ "  and cbi.user_id = ? \r\n"
				+ "  GROUP BY cb.batch_id,cb.batch_name,cb.batch_total_record, p.product_name,p.unit_value,cb.network_code,cb.network_code_for,cb.product_code, cb.modified_by,\r\n"
				+ "  cb.modified_on,p.product_type,p.short_name ,cb.domain_code, cb.batch_date, cb.created_on ,p.PRODUCT_CODE ORDER BY cb.created_on DESC  \r\n"
				+ "");
		return strBuff.toString();
	}
}
