package com.btsl.db.query.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.LifeCycleChangeStatusProcessQry;
/**
 * LifeCycleChangeStatusProcessPostgresQry
 * @author sadhan.k
 *
 */
public class LifeCycleChangeStatusProcessPostgresQry implements LifeCycleChangeStatusProcessQry{

	@Override
	public String processUserStatus(String[] status, String[] categoryApplicable) {
		
		StringBuilder selectQueryBuff = new StringBuilder("select u.user_id,u.user_type,u.status,u.network_code,u.msisdn,");
        selectQueryBuff.append(" u.external_code,ub.balance,date_trunc('day',ub.last_transfer_on::timestamp) as last_transfer_on ,ub.product_code,date_trunc('day',u.created_on::timestamp) as created_on ");
        selectQueryBuff.append(" ,up.sms_pin as pin");
        selectQueryBuff.append(" from users u left join  user_balances ub on u.user_id=ub.user_id ");
        selectQueryBuff.append(" left join  user_phones up on u.user_id=up.user_id ");
        selectQueryBuff.append(" where u.user_type=? and coalesce(ub.last_transfer_on,u.created_on) < (now() - (? || ' days')::interval ) ");
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
		strBuff.append("  WITH RECURSIVE q AS ( ");
		strBuff.append(" SELECT user_id, parent_id FROM users ");
		strBuff.append(" WHERE user_id = ?  ");
		strBuff.append(" UNION ALL ");
		strBuff.append(" SELECT u.user_id, u.parent_id ");
		strBuff.append(" FROM users u ");
		strBuff.append(" JOIN q  ON q.user_id = u.parent_id ");
		strBuff.append(" WHERE u.status <> ? ");
		strBuff.append(" AND u.status <> ? AND u.user_id <> ? ) SELECT 1 FROM q WHERE user_id <> ? ");
        
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("checkActiveChildren", "QUERY sqlSelect=" + sqlSelect);
        }
		
        int i = 1;
        pstmt = con.prepareStatement(sqlSelect);
        pstmt.setString(i++, userid);
        pstmt.setString(i++, PretupsI.USER_STATUS_DELETED);
        pstmt.setString(i++, PretupsI.USER_STATUS_CANCELED);
        pstmt.setString(i++, userid);
	pstmt.setString(i++, userid);
        
        return pstmt;
	}

}

