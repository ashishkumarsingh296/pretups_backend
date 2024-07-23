package com.btsl.db.query.oracle;

import com.web.pretups.iccidkeymgmt.businesslogic.PosKeyWebQry;

public class PosKeyWebOracleQry implements PosKeyWebQry{
	
	@Override
	public String isIccExistQry() {
		StringBuilder sqlSelectBuf = new StringBuilder();
		sqlSelectBuf
				.append("SELECT icc_id,NVL(msisdn,'') msisdn,network_code FROM pos_keys WHERE icc_id=? AND (new_icc_id is null OR new_icc_id=' ')");
		return sqlSelectBuf.toString();
		
	}
	
	@Override
	public String isNewIccExistQry() {
		StringBuilder sqlSelectBuf = new StringBuilder();
		sqlSelectBuf
				.append("SELECT icc_id,NVL(msisdn,'') msisdn FROM pos_keys WHERE icc_id=?");
		return sqlSelectBuf.toString();
		
	}
	
	@Override
	public String loadPosKeyDetailsForICCIDAndMsisdnQry( boolean isICCID, boolean isHistory) {
		StringBuilder qryBuf = new StringBuilder();
		qryBuf
				.append("SELECT pk.icc_id, pk.msisdn,NVL(TO_CHAR(pk.modified_on,'dd/mm/yy HH24:MI:SS'),'')");
		qryBuf.append(" modified_on, us.user_name modified,pk.network_code ");
        if (isHistory) {
            qryBuf.append(" FROM pos_key_history pk ");
        } else {
            qryBuf.append(" FROM pos_keys pk  ");
        }
        qryBuf.append(" , users us ");
        if (isICCID) {
            qryBuf.append(" WHERE pk.icc_id= ?  ");
        } else {
            qryBuf.append(" WHERE pk.msisdn= ?  ");
        }
        qryBuf.append(" AND pk.modified_by = us.user_id(+) ORDER BY pk.modified_on DESC");
		return qryBuf.toString();
		
	}

}
