package com.btsl.pretups.whitelist.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

public class WhiteListPostgresQry implements WhiteListQry {
	private Log log = LogFactory.getLog(this.getClass().getName());
	private String className = "WhiteListPostgresQry";

	@Override
	public PreparedStatement loadInterfaceDetailsQry(Connection pCon,
			String pMsisdn) throws SQLException {
		PreparedStatement pstmt;
		final String methodName = className + "#loadInterfaceDetails";
		StringBuilder strBuff = new StringBuilder(
				"SELECT WL.interface_id ,I.external_id, I.status,I.message_language1, I.message_language2, IT.handler_class,IT.underprocess_msg_reqd,SC.service_class_id,I.status_type statustype, ");
		strBuff.append(" WL.account_id,WL.account_status,WL.service_class,WL.credit_limit,WL.network_code,WL.msisdn,WL.entry_date,WL.external_interface_code,WL.created_on,WL.created_by,WL.status,WL.imsi,WL.language,WL.country,I.single_state_transaction ");
		strBuff.append(" FROM white_list WL,interfaces I LEFT JOIN service_classes SC ON (I.interface_id=SC.interface_id AND SC.service_class_code=?), ");
		strBuff.append(" interface_types IT WHERE WL.msisdn = ? AND  WL.status='Y' AND I.status<>'N' ");
		strBuff.append(" AND I.interface_id=WL.interface_id AND I.interface_type_id=IT.interface_type_id AND SC.STATUS <>'N' ");
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Select Query= " + strBuff.toString());
		}
		pstmt = pCon.prepareStatement(strBuff.toString());
		pstmt.setString(1, PretupsI.ALL);
		pstmt.setString(2, pMsisdn);
		return pstmt;

	}

	@Override
	public String loadWhiteListSubsDetailsQry() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT W.network_code,W.msisdn,W.account_id,W.entry_date, ");
		strBuff.append(" W.account_status,W.service_class,W.movement_code,W.imsi,LM.name AS LANG,");
		strBuff.append(" SC.service_class_name,W.credit_limit,W.interface_id,IT.interface_name,W.external_interface_code,L1.lookup_name AS WLSTATUS,COALESCE(U.user_name,W.activated_by) AS UN,");
		strBuff.append(" W.activated_on,W.activated_by,W.created_on,W.created_by,W.status,W.language,W.country,L.lookup_name,SC.service_class_name ");
		strBuff.append(" FROM white_list W LEFT JOIN service_classes SC ON (W.service_class=SC.service_class_code AND W.interface_id =SC.interface_id) ");
		strBuff.append(" LEFT JOIN (interfaces I LEFT JOIN interface_types IT ON  (I.interface_type_id=IT.interface_type_id)) ON (W.interface_id =I.interface_id)");
		strBuff.append(" LEFT JOIN lookups L ON (W.movement_code = L.lookup_code AND L.lookup_type=?) LEFT JOIN lookups L1 ON (W.status = L1.lookup_code AND L1.lookup_type=?)");
		strBuff.append(" LEFT JOIN USERS U ON (W.activated_by=U.user_id) LEFT JOIN locale_master LM ON (W.language=LM.language) ");
		strBuff.append(" WHERE W.msisdn =? ORDER BY W.account_id");

		return strBuff.toString();

	}

	@Override
	public String insertIndWhiteListDetail(){
		 StringBuilder strBuff = new StringBuilder(" INSERT INTO white_list ( network_code, msisdn, account_id, entry_date, ");
	        strBuff.append(" account_status, service_class, credit_limit, interface_id, external_interface_code, created_on, created_by, ");
	        strBuff.append(" modified_on, modified_by, status, activated_on, activated_by, movement_code, language, country, imsi ) ");
	        strBuff.append(" VALUES(?,?,?,?,?,?,?::integer,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	        return strBuff.toString();
	}
}
