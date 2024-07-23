package com.btsl.pretups.channel.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.pretups.common.PretupsI;

public class ChannelUserTransferOracleQry implements ChannelUserTransferQry {

	@Override
	public String createStatementForLloadInitiatedUserListForUserTransferQry(String status,String statusUsed){
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append("select u.user_name, u.user_id, u.login_id,up.msisdn,umg.created_on,umg.created_by,pu.USER_NAME parent_name,ou.USER_NAME owner_name ");
        strBuff.append(", c.category_name, u.status,u.network_code ");
        strBuff.append("FROM users u, user_geographies ug, categories c,user_migration_request umg,users pu,users ou,user_phones up ");
        strBuff.append("WHERE u.category_code= ? ");
          if ((status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) || (status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE))) {
            strBuff.append(" AND u.barred_deletion_batchid IS NULL  ");
        }
        if (statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append("AND u.status IN ( " + status + ")");
        }
        strBuff.append("AND u.user_type ='CHANNEL' ");
        strBuff.append("AND umg.TO_USER_ID= u.USER_ID  AND u.parent_id = pu.user_id  AND u.owner_id = ou.user_id AND u.user_id = up.USER_ID AND up.PRIMARY_NUMBER='Y' ");
        strBuff.append("AND u.user_id = ug.user_id AND c.domain_code=? ");
        strBuff.append("AND u.category_code=c.category_code  ");
        strBuff.append("AND umg.FROM_USER_ID=?  AND umg.created_by = ? AND umg.status= ?  and umg.created_on > ? ");
	     strBuff.append("AND ug.grph_domain_code IN ( ");
        strBuff.append("SELECT grph_domain_code FROM geographical_domains WHERE grph_domain_type=c.grph_domain_type ");
        strBuff.append("CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        strBuff.append("START WITH grph_domain_code = ? )  ");
        strBuff.append("ORDER BY user_name ");

        return strBuff.toString();
		
	}
	@Override
	public PreparedStatement loadGeogphicalHierarchyListByToParentIdQry(Connection con,String toParentID ) throws SQLException{
		String methodName ="loadGeogphicalHierarchyListByToParentIdQry";
		StringBuilder strBuff = new StringBuilder();
    	strBuff.append("SELECT grph_domain_code, network_code, grph_domain_name, parent_grph_domain_code, status, grph_domain_type ");
    	strBuff.append("FROM GEOGRAPHICAL_DOMAINS  WHERE status=? ");
    	strBuff.append("START WITH grph_domain_code IN (SELECT grph_domain_code FROM USER_GEOGRAPHIES WHERE user_id=?) ");
    	strBuff.append("CONNECT BY PRIOR grph_domain_code=parent_grph_domain_code");
    	PreparedStatement pstmt ;
    	pstmt = con.prepareStatement(strBuff.toString());
    	
    	if (log.isDebugEnabled()) {
			log.debug(methodName, "QUERY sqlSelect=" + strBuff.toString());
		}
			int i=1;
			pstmt.setString(i++,PretupsI.USER_STATUS_ACTIVE);
			pstmt.setString(i++,toParentID);
			return pstmt;
	}
}