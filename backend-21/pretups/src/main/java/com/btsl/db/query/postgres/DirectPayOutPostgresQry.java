package com.btsl.db.query.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.DirectPayOutQry;


public class DirectPayOutPostgresQry implements DirectPayOutQry{
	private Log log = LogFactory.getLog(this.getClass());
	@Override
	public String validateUsersQry() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT U.user_id,U.user_code,U.msisdn,U.login_id,U.category_code,C.category_name,C.domain_code,CG.grade_code,U.status,");
		strBuff.append("CG.grade_name,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend,U.external_code, ");
		strBuff.append("CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
		strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
		strBuff.append("CPS.language_2_message  comprf_lang_2_msg, UG.GRPH_DOMAIN_CODE ");
		strBuff.append("FROM USERS U,CHANNEL_USERS CU,CHANNEL_GRADES CG,CATEGORIES C,USER_GEOGRAPHIES UG, ");
		strBuff.append("COMMISSION_PROFILE_SET CPS, COMMISSION_PROFILE_SET_VERSION CPSV,TRANSFER_PROFILE TP ");
		strBuff.append("WHERE U.msisdn=? AND U.network_code=? AND U.user_id=CU.user_id AND U.user_id=UG.user_id AND ");
		strBuff.append("U.category_code=C.category_code AND U.category_code=CG.category_code AND CU.user_grade=CG.grade_code ");
		strBuff.append("AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
		strBuff.append("AND TP.profile_id = CU.transfer_profile_id AND U.category_code= C.category_code ");
		strBuff.append(" AND U.status <> 'N' AND U.status <> 'C' AND C.status='Y'  AND u.user_id=UG.user_id ");
		strBuff.append("AND CPSV.applicable_from =COALESCE ((SELECT MAX(applicable_from) FROM ");
		strBuff.append("COMMISSION_PROFILE_SET_VERSION WHERE applicable_from <= ? AND ");
		strBuff.append("comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from )");
		return strBuff.toString();
	}
	@Override
	public String selectNetworkDetailsFromNetworkStocksQry() {
		StringBuilder sqlBuffer=new StringBuilder("SELECT network_code, network_code_for, product_code, wallet_type, wallet_created, wallet_returned, ");
		sqlBuffer.append("wallet_balance, wallet_sold, last_txn_no, last_txn_type, last_txn_balance, previous_balance, ");
		sqlBuffer.append("modified_by, modified_on, created_on, created_by, daily_stock_updated_on ");
		sqlBuffer.append("FROM network_stocks ");
		sqlBuffer.append("WHERE network_code = ? AND network_code_for = ? AND wallet_type = ? AND ");
		sqlBuffer.append("DATE_TRUNC('day',daily_stock_updated_on::timestamp) <> DATE_TRUNC('day',?::timestamp) FOR UPDATE ");
		return sqlBuffer.toString();
	}

	@Override
	public String selectWalletDetailsFromNetworkStocksQry() {
		//Select the stock for the requested product for network.
		StringBuilder sqlBuffer=new StringBuilder(" SELECT ");
		sqlBuffer.append(" wallet_balance , wallet_type, wallet_sold ");
		sqlBuffer.append(" FROM network_stocks ");
		sqlBuffer.append(" WHERE network_code = ? AND product_code = ? AND network_code_for = ? AND wallet_type = ? FOR UPDATE ");
		return sqlBuffer.toString();
	}

	@Override
	public String selectFromUserBalanceWheredailyBalanceUpdatedQry() {
		StringBuilder sqlBuffer=new StringBuilder(" SELECT user_id, network_code, network_code_for, product_code, balance, prev_balance, ");
		sqlBuffer.append("last_transfer_type, last_transfer_no, last_transfer_on, daily_balance_updated_on ");
		sqlBuffer.append("FROM user_balances ");
		sqlBuffer.append("WHERE user_id = ? AND DATE_TRUNC('day',daily_balance_updated_on::timestamp)<> DATE_TRUNC('day',?::timestamp) AND balance_type=? FOR UPDATE ");
		return sqlBuffer.toString();
	}

	@Override
	public String selectFromUserBalanceWhereNetworkCode() {
		StringBuilder sqlBuffer=new StringBuilder("  SELECT ");
		sqlBuffer.append(" balance "); 
		sqlBuffer.append(" FROM user_balances ");
		sqlBuffer.append(" WHERE user_id = ? and product_code = ? AND network_code = ? AND network_code_for = ? AND balance_type = ? FOR UPDATE ");
		return sqlBuffer.toString();
	}

	@Override
	public PreparedStatement loadGeographyListQry(Connection con, String[] zoneCodeArr, String zoneCode, String _networkCode)throws SQLException {
		StringBuilder str= new StringBuilder();
		str.append(" WITH RECURSIVE q AS ( ");
		str.append("SELECT DISTINCT GRPH_DOMAIN_CODE,GRPH_DOMAIN_TYPE FROM GEOGRAPHICAL_DOMAINS  ");
		str.append(" WHERE NETWORK_CODE=? AND status <> ?  ");
		if (!PretupsI.ALL.equals(zoneCode)) {
			str.append(" AND grph_domain_code IN(");
			for( int i = 0; i<zoneCodeArr.length; i++){
				str.append(" ?");
				if(i != zoneCodeArr.length -1) str.append(",");
			}
			str.append(")");
		}
		str.append(" UNION ALL  ");
		str.append("SELECT DISTINCT m.GRPH_DOMAIN_CODE,m.GRPH_DOMAIN_TYPE FROM GEOGRAPHICAL_DOMAINS m  ");
		str.append("join q on q.grph_domain_code = parent_grph_domain_code  ");
		str.append("WHERE NETWORK_CODE=? AND status <> ?  ");
		str.append(") SELECT DISTINCT GRPH_DOMAIN_CODE,GRPH_DOMAIN_TYPE FROM q  ");
		LogFactory.printLog("loadGeographyListQry", str.toString(), log);
		PreparedStatement pstm=con.prepareStatement(str.toString());
		int index=0;
		pstm.setString(++index,_networkCode);
		pstm.setString(++index,PretupsI.NO);
		if(!PretupsI.ALL.equals(zoneCode)){
        	for(int x=0;x<zoneCodeArr.length;x++){
        		pstm.setString(++index,zoneCodeArr[x]);
            }
        }
		pstm.setString(++index,_networkCode);
		pstm.setString(++index,PretupsI.NO);
		return pstm;
	}


}
