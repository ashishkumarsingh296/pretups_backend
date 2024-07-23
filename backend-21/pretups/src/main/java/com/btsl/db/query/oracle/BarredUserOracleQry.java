package com.btsl.db.query.oracle;

import com.btsl.pretups.subscriber.businesslogic.BarredUserQry;
import com.btsl.util.BTSLUtil;

public class BarredUserOracleQry implements BarredUserQry{

	@Override
	public String isExistsQry(String barredType) {
		  StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM barred_msisdns WHERE ");
	        sqlBuff.append("module=? AND network_code=? ");
	        sqlBuff.append("AND msisdn=? AND (user_type=? )");// OR
	        // user_type='BOTH'
	        if (!BTSLUtil.isNullString(barredType)) {
	            sqlBuff.append("AND barred_type=? ");
	        }
	        sqlBuff.append("AND rownum =1 ");
		return sqlBuff.toString();
	}

	@Override
	public String addBarredUserBulkFromBarredMsisdns() {
		StringBuilder selectBuff = new StringBuilder("SELECT 1 FROM barred_msisdns WHERE ");
	        selectBuff.append(" module=? AND network_code=?");
	        selectBuff.append("  AND msisdn=? AND user_type=?");
	        selectBuff.append(" AND barred_type=NVL(?,barred_type)");
		return selectBuff.toString();
	}

	@Override
	public String addBarredUserBulkRecordExistGeography() {
		StringBuilder sqlRecordExistGeography = new StringBuilder();
        sqlRecordExistGeography.append("SELECT 1 FROM geographical_domains WHERE grph_domain_code  IN ");
        sqlRecordExistGeography.append("(SELECT grph_domain_code FROM geographical_domains gd1 WHERE status='Y' ");
        sqlRecordExistGeography.append("CONNECT BY PRIOR grph_domain_code=parent_grph_domain_code   ");
        sqlRecordExistGeography.append("START WITH grph_domain_code IN ");
        sqlRecordExistGeography.append("(SELECT grph_domain_code FROM user_geographies WHERE user_id=?)) ");
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
	            strBuff.append("AND bm.user_type = DECODE(?,?,bm.user_type,?) ");
	            strBuff.append("AND bm.barred_type = DECODE(?,?,bm.barred_type,?) ");
	            strBuff.append("AND TRUNC(bm.created_on) >= ? AND TRUNC(bm.created_date) <= ? ");
	        }
	        strBuff.append("ORDER BY bm.msisdn ");
		return strBuff.toString();
	}

}
