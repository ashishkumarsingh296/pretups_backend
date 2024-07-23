package com.btsl.db.query.postgres;

import com.web.pretups.iccidkeymgmt.businesslogic.PosKeyWebQry;

public class PosKeyWebPostgresQry implements PosKeyWebQry{
	
	@Override
	public String isIccExistQry() {
		StringBuilder sqlSelectBuf = new StringBuilder();
		sqlSelectBuf
				.append("SELECT icc_id,coalesce(msisdn,'') msisdn,network_code FROM pos_keys WHERE icc_id=? AND (new_icc_id is null OR new_icc_id=' ')");
		return sqlSelectBuf.toString();
		
	}

	@Override
	public String isNewIccExistQry() {
		StringBuilder sqlSelectBuf = new StringBuilder();
		sqlSelectBuf
				.append("SELECT icc_id,coalesce(msisdn,'') msisdn FROM pos_keys WHERE icc_id=?");
		return sqlSelectBuf.toString();
		
	}
	
	@Override
	public String loadPosKeyDetailsForICCIDAndMsisdnQry( boolean isICCID, boolean isHistory) {
		StringBuilder qryBuf = new StringBuilder();
		qryBuf
				.append("SELECT pk.icc_id, pk.msisdn,coalesce(TO_CHAR(pk.modified_on,'dd/mm/yy HH24:MI:SS'),'')");
		qryBuf.append(" modified_on, us.user_name modified,pk.network_code ");
        if (isHistory) {
            qryBuf.append(" FROM pos_key_history pk ");
        } else {
            qryBuf.append(" FROM pos_keys pk  ");
        }
        qryBuf.append(" LEFT JOIN users us ON pk.modified_by = us.user_id");
        if (isICCID) {
            qryBuf.append(" WHERE pk.icc_id= ?  ");
        } else {
            qryBuf.append(" WHERE pk.msisdn= ?  ");
        }
        qryBuf.append(" ORDER BY pk.modified_on DESC");
		return qryBuf.toString();
		
	}


}
