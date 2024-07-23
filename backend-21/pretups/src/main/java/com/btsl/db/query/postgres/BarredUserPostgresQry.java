package com.btsl.db.query.postgres;

import com.btsl.pretups.subscriber.businesslogic.BarredUserQry;
import com.btsl.util.BTSLUtil;

public class BarredUserPostgresQry implements BarredUserQry{

	@Override
	public String isExistsQry(String barredType) {
		StringBuilder sqlBuff = new StringBuilder(" SELECT 1 FROM barred_msisdns WHERE ");
		sqlBuff.append("module=? AND network_code=? ");
		sqlBuff.append("AND msisdn=? AND (user_type=? )");// OR
		// user_type='BOTH'
		if (!BTSLUtil.isNullString(barredType)) {
			sqlBuff.append("AND barred_type=? ");
		}
		sqlBuff.append(" limit 1 ");
		return sqlBuff.toString();
	}

	@Override
	public String addBarredUserBulkFromBarredMsisdns() {
		StringBuilder selectBuff = new StringBuilder("SELECT 1 FROM barred_msisdns WHERE ");
		selectBuff.append(" module=? AND network_code=?");
		selectBuff.append("  AND msisdn=? AND user_type=?");
		selectBuff.append(" AND barred_type=COALESCE(?,barred_type)");
		return selectBuff.toString();
	}

	@Override
	public String addBarredUserBulkRecordExistGeography() {
		StringBuilder sqlRecordExistGeography = new StringBuilder();
		sqlRecordExistGeography.append("SELECT 1 FROM geographical_domains WHERE grph_domain_code  IN ");
		sqlRecordExistGeography.append("(");

		sqlRecordExistGeography.append( " WITH RECURSIVE q AS ( ");
		sqlRecordExistGeography.append("SELECT gd1.grph_domain_code FROM geographical_domains gd1 WHERE gd1.status='Y' ");
		sqlRecordExistGeography.append("AND gd1.grph_domain_code IN ");
		sqlRecordExistGeography.append("(SELECT grph_domain_code FROM user_geographies WHERE user_id=?) ");
		sqlRecordExistGeography.append("UNION ALL  ");
		sqlRecordExistGeography.append("SELECT gd1.grph_domain_code FROM geographical_domains gd1 ");
		sqlRecordExistGeography.append("join q on q.grph_domain_code=gd1.parent_grph_domain_code ");
		sqlRecordExistGeography.append("WHERE gd1.status='Y'  ");
		sqlRecordExistGeography.append(" ) ");
		sqlRecordExistGeography.append("SELECT grph_domain_code FROM q ");

		sqlRecordExistGeography.append(") ");
		sqlRecordExistGeography.append("AND grph_domain_code IN (SELECT grph_domain_code FROM USER_GEOGRAPHIES where user_id=?) ");
		return sqlRecordExistGeography.toString();
	}

	@Override
	public String loadBarredUserListForXMLAPIQry(String module, String msisdn) {
		  StringBuilder strBuff = new StringBuilder();
	        strBuff.append("SELECT DISTINCT bm.module,n.network_name,bm.msisdn,name,bm.user_type,bm.barred_type,bm.created_on,U.user_name created_by, ");
	        strBuff.append("bm.barred_reason,SL.sub_lookup_name FROM barred_msisdns bm,networks n ,sub_lookups SL,users U ");
	        strBuff.append("WHERE bm.network_code=n.network_code AND SL.sub_lookup_code=BM.barred_type  AND SL.lookup_type=? ");
	        strBuff.append("AND bm.created_by=U.user_id AND bm.network_code=? ");
	        if (!BTSLUtil.isNullString(msisdn)) {
	            strBuff.append(" AND bm.msisdn=?");
	        }
	        if (!BTSLUtil.isNullString(module)) {
	            strBuff.append("AND bm.module=? ");
	            strBuff.append("AND bm.user_type = case ? when ? then bm.user_type else ? end ");
	            strBuff.append("AND bm.barred_type = case ? when ? then bm.barred_type else ? end  ");
	            strBuff.append("AND DATE_TRUNC('day',bm.created_on::timestamp) >= ? AND DATE_TRUNC('day',bm.created_date::timestamp) <= ? ");
	        }
	        strBuff.append("ORDER BY bm.msisdn ");
		return strBuff.toString();
	}
}
