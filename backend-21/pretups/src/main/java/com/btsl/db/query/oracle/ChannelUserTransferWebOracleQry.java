package com.btsl.db.query.oracle;

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
public class ChannelUserTransferWebOracleQry implements ChannelUserTransferWebQry{

	@Override
	public PreparedStatement loadGeogphicalHierarchyListByToParentId(Connection p_con, String p_toParentID)
			throws SQLException {
		
		PreparedStatement pstmtSelect = null;
		StringBuilder strBuff = new StringBuilder();
		 
        strBuff.append("SELECT grph_domain_code, network_code, grph_domain_name, parent_grph_domain_code, status, grph_domain_type ");
        strBuff.append("FROM GEOGRAPHICAL_DOMAINS  WHERE status=? ");
        strBuff.append("START WITH grph_domain_code IN (SELECT grph_domain_code FROM USER_GEOGRAPHIES WHERE user_id=?) ");
        strBuff.append("CONNECT BY PRIOR grph_domain_code=parent_grph_domain_code");
        
        LogFactory.printLog("loadGeogphicalHierarchyListByToParentId", strBuff.toString(), LOG);
        pstmtSelect = p_con.prepareStatement(strBuff.toString());
        int i = 1;
        pstmtSelect.setString(i++, PretupsI.USER_STATUS_ACTIVE);
        pstmtSelect.setString(i++, p_toParentID);
		
		
		return pstmtSelect;
	}

}
