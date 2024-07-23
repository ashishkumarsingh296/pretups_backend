package com.btsl.db.query.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.web.pretups.channel.user.businesslogic.ChannelUserTransferWebQry;

/**
 * 
 * @author sadhan.k
 *
 */
public class ChannelUserTransferWebPostgresQry implements ChannelUserTransferWebQry{

	@Override
	public PreparedStatement loadGeogphicalHierarchyListByToParentId(Connection p_con, String p_toParentID)
			throws SQLException {
		
		PreparedStatement pstmtSelect = null;
		StringBuilder strBuff = new StringBuilder();
		 
        strBuff.append(" WITH RECURSIVE q AS (");
        strBuff.append(" SELECT grph_domain_code, network_code, grph_domain_name, parent_grph_domain_code, status, grph_domain_type ");
        strBuff.append(" FROM GEOGRAPHICAL_DOMAINS  WHERE grph_domain_code IN (SELECT grph_domain_code FROM USER_GEOGRAPHIES WHERE user_id=? ) ");
        strBuff.append(" union all SELECT m.grph_domain_code, m.network_code, m.grph_domain_name, m.parent_grph_domain_code, m.status, m.grph_domain_type ");
        strBuff.append(" FROM GEOGRAPHICAL_DOMAINS m join q on q.grph_domain_code = m.parent_grph_domain_code ) ");
        strBuff.append(" SELECT q.grph_domain_code, q.network_code, q.grph_domain_name, q.parent_grph_domain_code, q.status, q.grph_domain_type  ");
        strBuff.append(" FROM q where status=? ");
        
        LogFactory.printLog("loadGeogphicalHierarchyListByToParentId", strBuff.toString(), LOG);
        
        pstmtSelect = p_con.prepareStatement(strBuff.toString());
        int i = 1;
        
        pstmtSelect.setString(i++, p_toParentID);
        pstmtSelect.setString(i++, PretupsI.USER_STATUS_ACTIVE);
		
		
		return pstmtSelect;
	}
}
