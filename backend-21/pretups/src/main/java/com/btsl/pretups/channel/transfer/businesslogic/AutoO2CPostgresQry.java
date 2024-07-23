package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;


/**
 * AutoO2CPostgresQry
 * @author sadhan.k
 *
 */
public class AutoO2CPostgresQry implements AutoO2CQry {

	@Override
	public PreparedStatement loadCategoryUsersWithinGeoDomainHirearchy(
			Connection p_con, String p_categoryCode, String p_networkCode,
			String p_userName, String p_ownerUserID,
			String p_geographicalDomainCode, String p_loginUserID,
			String user_type) throws SQLException {
		
		
		final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT DISTINCT U.user_id, U.user_name FROM users U, channel_users cu, user_geographies UG, categories CAT ");
        strBuff.append(" WHERE U.network_code = ? AND U.category_code = ?  ");
        if (p_ownerUserID == null) {
            strBuff.append(" AND u.status IN ('" + PretupsI.USER_STATUS_ACTIVE + "','" + PretupsI.USER_STATUS_SUSPEND + "', '" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "') ");
        } else {
            // loading child users
            strBuff.append(" and  u.user_id=cu.user_id AND u.status = 'Y' ");
        }
        strBuff.append(" AND u.user_type = 'CHANNEL'  ");
        strBuff.append(" AND U.category_code = CAT.category_code AND U.user_id=UG.user_id ");
        if ("child".equals(user_type)) {
            strBuff.append(" and cu.auto_o2C_allow NOT IN ('Y',?,?,?,?)");
        }
        strBuff.append(" AND UG.grph_domain_code IN (WITH RECURSIVE q AS (");
        strBuff.append(" SELECT grph_domain_code, status FROM geographical_domains GD1 WHERE ");
        strBuff.append(" grph_domain_code IN (SELECT grph_domain_code FROM user_geographies UG1 WHERE UG1.grph_domain_code =? AND UG1.user_id = ? )");
        strBuff.append(" UNION ALL");
        strBuff.append(" SELECT gd.grph_domain_code, gd.status FROM geographical_domains gd");
        strBuff.append(" JOIN q ON  q.grph_domain_code = gd.parent_grph_domain_code");
        strBuff.append(" )SELECT grph_domain_code from q where status = ?  )");
        strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?) ");	        
        if (p_ownerUserID != null && !"NA".equalsIgnoreCase(p_ownerUserID)) {
            // the
            // owner
            // user
            // only
            // we
            // are
            // passing
            // owner
            // id
            // as
            // NA
            strBuff.append(" AND  u.owner_id = ?  ");
        }
        strBuff.append(" ORDER BY U.user_name");

        final String sqlSelect = strBuff.toString();   
        
        LogFactory.printLog("loadCategoryUsersWithinGeoDomainHirearchy", strBuff.toString(), LOG);

        PreparedStatement   pstmt =  p_con.prepareStatement(sqlSelect);
            int i = 0;
            pstmt.setString(++i, p_networkCode);
            pstmt.setString(++i, p_categoryCode);
            if ("child".equals(user_type)) {
                pstmt.setString(++i, PretupsI.AUTO_O2C_ORDER_NEW);
                pstmt.setString(++i, PretupsI.AUTO_O2C_ORDER_APPROVE1);
                pstmt.setString(++i, PretupsI.AUTO_O2C_ORDER_APPROVE2);
                pstmt.setString(++i, PretupsI.AUTO_O2C_ORDER_APPROVE3);
            }
            pstmt.setString(++i, p_geographicalDomainCode);
            pstmt.setString(++i, p_loginUserID);
            pstmt.setString(++i, PretupsI.GEO_DOMAIN_STATUS);
          
            pstmt.setString(++i, p_userName);

            if (p_ownerUserID != null && !"NA".equalsIgnoreCase(p_ownerUserID)) {
                // the
                // owner
                // user
                // only
                // we
                // are
                // passing
                // owner
                // id
                // as
                // NA
                pstmt.setString(++i, p_ownerUserID);
            }
		
		return pstmt;
	}

	/**
	 * loadChannelUsersWithinGeoDomainHirearchy
	 */
	@Override
	public PreparedStatement loadChannelUsersWithinGeoDomainHirearchy(
			Connection p_con, String p_categoryCode, String p_networkCode,
			String p_userName, String p_ownerUserID,
			String p_geographicalDomainCode, String p_loginUserID,
			String user_type, int approval_level) throws SQLException {

		
		 final StringBuilder strBuff = new StringBuilder();

	        strBuff.append(" SELECT DISTINCT U.user_id, U.user_name FROM users U, channel_users cu, user_geographies UG, categories CAT ");
	        strBuff.append(" WHERE U.network_code = ? AND U.category_code = ?  ");
	        if (p_ownerUserID == null) {
	            strBuff.append(" AND u.status IN ('" + PretupsI.USER_STATUS_ACTIVE + "','" + PretupsI.USER_STATUS_SUSPEND + "', '" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "') ");
	        } else {
	            // loading child users
	            strBuff.append(" and  u.user_id=cu.user_id AND u.status = 'Y' ");
	        }
	        strBuff.append(" AND u.user_type = 'CHANNEL'  ");
	        strBuff.append(" AND U.category_code = CAT.category_code AND U.user_id=UG.user_id ");
	        if ("child".equals(user_type)) {
	            strBuff.append(" and cu.auto_o2C_allow = ? ");
	        }

	        strBuff.append(" AND UG.grph_domain_code IN (WITH RECURSIVE q AS (");
	        strBuff.append(" SELECT grph_domain_code, status FROM geographical_domains GD1 WHERE ");
	        strBuff.append(" grph_domain_code IN (SELECT grph_domain_code FROM user_geographies UG1 WHERE UG1.grph_domain_code =? AND UG1.user_id = ? )");
	        strBuff.append(" UNION ALL");
	        strBuff.append(" SELECT gd.grph_domain_code, gd.status FROM geographical_domains gd");
	        strBuff.append(" JOIN q ON  q.grph_domain_code = gd.parent_grph_domain_code");
	        strBuff.append(" )SELECT grph_domain_code from q WHERE status =? )");
	        strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?) ");	        
	        
	        
	        if (p_ownerUserID != null && !"NA".equalsIgnoreCase(p_ownerUserID)) {
	            // the
	            // owner
	            // user
	            // only
	            // we
	            // are
	            // passing
	            // owner
	            // id
	            // as
	            // NA
	            strBuff.append(" AND  u.owner_id = ?  ");
	        }
	        strBuff.append(" ORDER BY U.user_name");

	        final String sqlSelect = strBuff.toString();
	       
	        LogFactory.printLog("loadChannelUsersWithinGeoDomainHirearchy", strBuff.toString(), LOG);
	
	        	PreparedStatement      pstmt =  p_con.prepareStatement(sqlSelect);
	            int i = 0;
	            pstmt.setString(++i, p_networkCode);
	            pstmt.setString(++i, p_categoryCode);
	            if ("child".equals(user_type)) {
	                if (approval_level == 1) {
	                    pstmt.setString(++i, PretupsI.AUTO_O2C_ORDER_NEW);
	                } else if (approval_level == 2) {
	                    pstmt.setString(++i, PretupsI.AUTO_O2C_ORDER_APPROVE1);
	                } else if (approval_level == 3) {
	                    pstmt.setString(++i, PretupsI.AUTO_O2C_ORDER_APPROVE2);
	                }
	            }
	            pstmt.setString(++i, p_geographicalDomainCode);
	            pstmt.setString(++i, p_loginUserID);
	            pstmt.setString(++i, PretupsI.GEO_DOMAIN_STATUS);
	          
	            pstmt.setString(++i, p_userName);

	            if (p_ownerUserID != null && !"NA".equalsIgnoreCase(p_ownerUserID)) {
	                // the
	                // owner
	                // user
	                // only
	                // we
	                // are
	                // passing
	                // owner
	                // id
	                // as
	                // NA
	                pstmt.setString(++i, p_ownerUserID);
	            }
		
	        
		return pstmt;
	}

}
