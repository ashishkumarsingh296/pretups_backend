package com.btsl.db.query.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.LifeCycleChangeStatusProcessQry;
import com.btsl.util.Constants;

/**
 * LifeCycleChangeStatusProcessOracleQry
 * @author sadhan.k
 *
 */
public class LifeCycleChangeStatusProcessOracleQry implements LifeCycleChangeStatusProcessQry {

	@Override
	public String processUserStatus(String[] status, String[] categoryApplicable) {
		
		StringBuilder selectQueryBuff = new StringBuilder("select u.user_id,u.user_type,u.status,u.network_code,u.msisdn,");
        selectQueryBuff.append(" u.external_code,ub.balance,trunc(ub.last_transfer_on) as last_transfer_on ,ub.product_code,trunc(u.created_on) as created_on ");
        selectQueryBuff.append(" ,up.sms_pin as pin");
        selectQueryBuff.append(" from users u, user_balances ub");
        selectQueryBuff.append(" ,user_phones up ");
        selectQueryBuff.append(" where u.user_id=ub.user_id(+) ");
        selectQueryBuff.append(" and u.user_id=up.user_id(+) ");
        selectQueryBuff.append(" and u.user_type=? and coalesce(ub.last_transfer_on,u.created_on) < sysdate - ? ");
        selectQueryBuff.append(" and u.status in (");
        for (int i = 0; i < status.length - 2; i++) {
            selectQueryBuff.append("'" + status[i] + "',");
        }
        selectQueryBuff.append("'" + status[status.length - 2] + "') ");
        if(categoryApplicable != null) {
        	selectQueryBuff.append(" and u.category_code in (");
        	for(int i = 0; i < categoryApplicable.length; i++) {
        		if(i != 0) {
        			selectQueryBuff.append(",");
        		}
        		selectQueryBuff.append("'" + categoryApplicable[i] + "'");
        	}
        	selectQueryBuff.append(") ");
        }
        selectQueryBuff.append(" order by last_transfer_on desc ");

		
		
		return selectQueryBuff.toString();
	}

	@Override
	public PreparedStatement checkActiveChildren(Connection con, String userid) throws SQLException {
		final StringBuilder strBuff = new StringBuilder();
		PreparedStatement pstmt = null;
		strBuff.append("SELECT 1 FROM users  WHERE status <> ? and status <> ? " );
		strBuff.append(" AND user_id <> ? ");
		strBuff.append(" CONNECT BY nocycle PRIOR  USER_ID = PARENT_ID START WITH USER_ID = ? ");
		final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("checkActiveChildren", "QUERY sqlSelect=" + sqlSelect);
        }
		
        int i = 1;
        pstmt = con.prepareStatement(sqlSelect);
        pstmt.setString(i++, PretupsI.USER_STATUS_DELETED);
        pstmt.setString(i++, PretupsI.USER_STATUS_CANCELED);
        pstmt.setString(i++, userid);
        pstmt.setString(i++, userid);
        
        return pstmt;
	}
	

}
