package com.btsl.db.query.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.web.user.businesslogic.UserWebQry;

public class UserWebPostgresQry implements UserWebQry {
	public static  final Log _log = LogFactory.getLog(UserWebPostgresQry.class);
	@Override
	public String loadUsersListQry( String p_networkCode, String p_categoryCode, String p_userName, String p_userID, String p_ownerID, String p_sessionUserID, String p_statusUsed, String p_status) {
		final StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
        strBuff.append("USR.login_id,USR.password passwd,USR.category_code usr_category_code,USR.parent_id,USR.contact_person,");
        strBuff.append("USR.owner_id,USR.allowed_ip,USR.allowed_days,USR.payment_type,");
        strBuff.append("USR.from_time,USR.to_time,USR.employee_code,USR.company,USR.fax,USR.firstname,USR.lastname, ");// company,fax,firstname,lastname
        // added
        // by
        // deepika
        // aggarwal
        strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,");
        strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
        strBuff.append("USR.created_by,USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
        strBuff.append("USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
        strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
        strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date, ");
        strBuff.append("USR.previous_status,USR.rsaflag,USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name,USR_CAT.fixed_domains,USR_CAT.domain_allowed,USR_CAT.PRODUCT_TYPES_ALLOWED,USR_CAT.SERVICES_ALLOWED,USR_CAT.MAX_TXN_MSISDN,");
        strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type, ");
        strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed,USR_CAT.services_allowed, ");
        strBuff
            .append("USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed,USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed,USR_CAT.restricted_msisdns, ");
        strBuff.append("PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn, ");
        strBuff.append("PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn, ");
        strBuff
            .append("ONR_CAT.category_name owner_cat,l.lookup_name, USR_CAT.transfertolistonly, USR_CAT.outlets_allowed,USR.AUTHENTICATION_ALLOWED,USR_CAT.AUTHENTICATION_TYPE ");
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_MSISDN_ASSOCIATION_REQ))).booleanValue()) {
            strBuff.append(" ,chnl_usr.ASSOCIATED_MSISDN,chnl_usr.ASSOCIATED_MSISDN_TYPE,chnl_usr.ASSOCIATED_MSISDN_CDATE,chnl_usr.ASSOCIATED_MSISDN_MDATE ");
        }
        if((PretupsI.YES).equals(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOWD_USR_TYP_CREATION))))
			 strBuff.append(",USR.ALLOWD_USR_TYP_CREATION ");
        
        strBuff.append("FROM users USR left join (users PRNT_USR left join  categories  PRNT_CAT on PRNT_USR.category_code=PRNT_CAT.category_code )on USR.parent_id=PRNT_USR.user_id, users ONR_USR,categories USR_CAT, ");
        strBuff.append("categories ONR_CAT, lookups l ");
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_MSISDN_ASSOCIATION_REQ))).booleanValue()) {
            strBuff.append(" ,channel_users chnl_usr ");
        }
        strBuff.append("WHERE USR.network_code = ? ");

        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append("AND USR.status IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append("AND USR.status NOT IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append("AND USR.status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append("AND USR.status <> ? ");
        }

        if (!BTSLUtil.isNullString(p_categoryCode)) {
            strBuff.append(" AND USR.category_code =?");
        }
        if (!BTSLUtil.isNullString(p_userID)) {
            strBuff.append(" AND USR.user_id =?");
        }
        if (!BTSLUtil.isNullString(p_ownerID)) {
            strBuff.append(" AND USR.owner_id =?");
        }
        if (!BTSLUtil.isNullString(p_userName)) {
            strBuff.append(" AND UPPER(USR.user_name) LIKE UPPER(?) ");
        }
        strBuff.append(" AND USR.owner_id=ONR_USR.user_id ");
        strBuff.append("AND USR.category_code=USR_CAT.category_code ");
        strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
        strBuff.append(" AND USR.status = l.lookup_code ");
        strBuff.append(" AND l.lookup_type= ? ");
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_MSISDN_ASSOCIATION_REQ))).booleanValue()) {
            strBuff.append(" AND chnl_usr.user_id=USR.user_id ");
        }
        if (p_sessionUserID != null) {
            strBuff.append(" AND USR.user_id IN ( ");
            strBuff.append(" with recursive q as (SELECT user_id from users where user_id <> ? union  select m.user_id from  ");
            strBuff.append(" users m join q on q.user_id=m.parent_id  ) select q.user_id from q where user_id <> ? ) ");
        }
        strBuff.append(" ORDER BY USR.user_name ");

		return strBuff.toString();
	}

	@Override
	public String loadOwnerUserListQry(String p_statusUsed, String p_status) {
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT u.user_name, u.user_id, u.owner_id, u.login_id ");
        strBuff.append("FROM users u,user_geographies ug,categories c ");
        strBuff.append("WHERE UPPER(u.user_name) LIKE UPPER(?) ");
        // Added for bar for del by shashank
        if ((p_status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) || (p_status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE))) {
            strBuff.append(" AND u.barred_deletion_batchid IS NULL ");
        }
        // end
        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append("AND u.status IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append("AND u.status NOT IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append("AND u.status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append("AND u.status <> ? ");
        }
        strBuff.append("AND u.user_type ='CHANNEL' ");
        strBuff.append("AND u.user_id = ug.user_id AND c.domain_code=? ");
        strBuff.append("AND u.category_code=c.category_code ");
        strBuff.append("AND c.SEQUENCE_NO='1'  ");
        strBuff.append("AND ug.grph_domain_code IN ( ");
        strBuff.append("with recursive q as (SELECT grph_domain_code,grph_domain_type FROM geographical_domains where ");
        strBuff.append(" grph_domain_code = ? union all select m.grph_domain_code, m.grph_domain_type from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code   ");
        strBuff.append(" ) select q.grph_domain_code from q   WHERE grph_domain_type=c.grph_domain_type ");
        strBuff.append(" )  ");
        strBuff.append("ORDER BY user_name ");
		return strBuff.toString();
	}
	@Override
	public PreparedStatement loadReportOwnerUserListQry(Connection p_con, String p_parentGraphDomainCode, String p_userID, String p_username, String p_domainCode, String p_networkCode) throws SQLException{
		PreparedStatement pstmt=null;
		 final StringBuilder strBuff = new StringBuilder();
	        strBuff.append(" SELECT DISTINCT u.user_name,u.user_id, u.owner_id ");
	        strBuff.append(" FROM users u,user_geographies ug,categories c ");
	        strBuff.append(" WHERE u.category_code=c.category_code ");
	        strBuff.append("AND u.user_type ='CHANNEL' ");
	        strBuff.append("AND u.network_code=? ");
	        strBuff.append(" AND c.SEQUENCE_NO='1'  ");
	        strBuff.append(" AND c.domain_code=? ");
	        strBuff.append(" AND ug.grph_domain_code IN ( ");
	        strBuff.append(" with recursive q as ( SELECT grph_domain_code,status  FROM geographical_domains GD1 WHERE grph_domain_code IN (SELECT grph_domain_code ");
	        strBuff.append(" FROM user_geographies UG1 WHERE	UG1.grph_domain_code = case ? when  '" + PretupsI.ALL + "' then  UG1.grph_domain_code else ? end AND UG1.user_id= ? ) union all ");
	        strBuff.append(" SELECT m.grph_domain_code,m.status from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code  ");
	        strBuff.append(" ) select q.grph_domain_code from q where status=? ");
	        strBuff.append(") ");
	        strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) ");
	        strBuff.append(" ORDER BY u.user_name");
	        final String sqlSelect = strBuff.toString();
	        if (_log.isDebugEnabled()) {
	            _log.debug("loadReportOwnerUserListQry", "QUERY sqlSelect=" + sqlSelect);
	            pstmt = p_con.prepareStatement(sqlSelect);

	            pstmt.setString(1, p_networkCode);
	            pstmt.setString(2, p_domainCode);
	            pstmt.setString(3, p_parentGraphDomainCode);
	            pstmt.setString(4, p_parentGraphDomainCode);
	            pstmt.setString(5, p_userID);
	            pstmt.setString(6, PretupsI.GEO_DOMAIN_STATUS);
	            pstmt.setString(7, p_username);
	        }
	        return pstmt;
	}

	@Override
	public PreparedStatement loadUsersListByNameAndOwnerIdQry(Connection p_con, String p_categoryCode, String p_userName, String p_ownerId, String p_userID, String p_statusUsed, String p_status, String p_userType) throws SQLException{
		PreparedStatement pstmt = null;
		 final StringBuilder strBuff = new StringBuilder();
		 if (p_userID != null) {
	        strBuff.append(" with recursive q as ( SELECT user_id,user_name,network_code,");
	        strBuff.append("login_id,password,category_code,parent_id,company,fax,firstname,lastname, ");// company,firstname,lastname,fax
	        strBuff.append("owner_id,allowed_ip,allowed_days,");
	        strBuff.append("from_time,to_time,employee_code,");
	        strBuff.append("status,email,pswd_modified_on,contact_no,");
	        strBuff.append("designation,division,department,msisdn,user_type,");
	        strBuff.append("created_by,created_on,modified_by,modified_on,address1, ");
	        strBuff.append("address2,city,state,country,ssn,user_name_prefix, ");
	        strBuff.append("external_code,short_name,level1_approved_by,level1_approved_on,");
	        strBuff.append("level2_approved_by,level2_approved_on,user_code,barred_deletion_batchid ");
	        strBuff.append("FROM users u WHERE u.user_id = ?");
	        strBuff.append(" union all SELECT m.user_id,m.user_name,m.network_code,");
	        strBuff.append(" m.login_id,m.password,m.category_code,m.parent_id,m.company,m.fax,m.firstname,m.lastname, ");// company,firstname,lastname,fax
	        strBuff.append("m.owner_id,m.allowed_ip,m.allowed_days,");
	        strBuff.append("m.from_time,m.to_time,m.employee_code,");
	        strBuff.append("m.status,m.email,m.pswd_modified_on,m.contact_no,");
	        strBuff.append("m.designation,m.division,m.department,m.msisdn,m.user_type,");
	        strBuff.append("m.created_by,m.created_on,m.modified_by,m.modified_on,m.address1, ");
	        strBuff.append("m.address2,m.city,m.state,m.country,m.ssn,m.user_name_prefix, ");
	        strBuff.append("m.external_code,m.short_name,m.level1_approved_by,m.level1_approved_on,");
	        strBuff.append("m.level2_approved_by,m.level2_approved_on,m.user_code,m.barred_deletion_batchid ");
	        strBuff.append("FROM users m join q on q.user_id=m.parent_id  ");
	        strBuff.append("  ) SELECT q.user_id,q.user_name,q.network_code,");
	        strBuff.append("q.login_id,q.password,q.category_code,q.parent_id,q.company,q.fax,q.firstname,q.lastname, ");// company,firstname,lastname,fax
	        strBuff.append("q.owner_id,q.allowed_ip,q.allowed_days,");
	        strBuff.append("q.from_time,q.to_time,q.employee_code,");
	        strBuff.append("q.status,q.email,q.pswd_modified_on,q.contact_no,");
	        strBuff.append("q.designation,q.division,q.department,q.msisdn,q.user_type,");
	        strBuff.append("q.created_by,q.created_on,q.modified_by,q.modified_on,q.address1, ");
	        strBuff.append("q.address2,q.city,q.state,q.country,q.ssn,q.user_name_prefix, ");
	        strBuff.append("q.external_code,q.short_name,q.level1_approved_by,q.level1_approved_on,");
	        strBuff.append("q.level2_approved_by,q.level2_approved_on,q.user_code ");
	        strBuff.append("FROM q where owner_id = ?  and user_id != ? ");
	        // Added for bar for del by shashank
	        if ((p_status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) || (p_status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE))) {
	            strBuff.append("  barred_deletion_batchid IS NULL ");
	            // end
	        }
	        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
	            strBuff.append("AND status IN (" + p_status + ")");
	        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
	            strBuff.append("AND status NOT IN (" + p_status + ")");
	        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
	            strBuff.append("AND status =? ");
	        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
	            strBuff.append("AND status <> ? ");
	        }
	        strBuff.append(" AND user_type = ? ");
	        strBuff.append(" AND category_code = ? ");
	        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) ");
	        final String sqlSelect = strBuff.toString();
	        if (_log.isDebugEnabled()) {
	            _log.debug("loadUsersListByNameAndOwnerIdQry", "QUERY sqlSelect=" + sqlSelect);
	        }
	        pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            pstmt.setString(++i, p_userID);
            pstmt.setString(++i, p_ownerId);
            pstmt.setString(++i, p_userID);
            if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
                pstmt.setString(++i, p_status);
            }
            pstmt.setString(++i, p_userType);
            pstmt.setString(++i, p_categoryCode);
            pstmt.setString(++i, p_userName);

	        }
	        else{
	        	strBuff.append(" SELECT user_id,user_name,network_code,");
		        strBuff.append("login_id,password,category_code,parent_id,company,fax,firstname,lastname, ");// company,firstname,lastname,fax
		        // added
		        // by
		        // deepika
		        // aggarwal
		        strBuff.append("owner_id,allowed_ip,allowed_days,");
		        strBuff.append("from_time,to_time,employee_code,");
		        strBuff.append("status,email,pswd_modified_on,contact_no,");
		        strBuff.append("designation,division,department,msisdn,user_type,");
		        strBuff.append("created_by,created_on,modified_by,modified_on,address1, ");
		        strBuff.append("address2,city,state,country,ssn,user_name_prefix, ");
		        strBuff.append("external_code,short_name,level1_approved_by,level1_approved_on,");
		        strBuff.append("level2_approved_by,level2_approved_on,user_code ");
		        strBuff.append("FROM users u WHERE  owner_id = ? ");

		        // Added for bar for del by shashank
		        if ((p_status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) || (p_status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE))) {
		            strBuff.append(" AND u.barred_deletion_batchid IS NULL ");
		            // end
		        }

		        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
		            strBuff.append("AND u.status IN (" + p_status + ")");
		        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
		            strBuff.append("AND u.status NOT IN (" + p_status + ")");
		        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
		            strBuff.append("AND u.status =? ");
		        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
		            strBuff.append("AND u.status <> ? ");
		        }

		        // strBuff.append(" AND u.user_type ='CHANNEL' ");
		        strBuff.append(" AND u.user_type = ? ");
		        strBuff.append(" AND category_code = ? ");
		        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) ");
		        strBuff.append(" ORDER BY user_name ");
		        final String sqlSelect = strBuff.toString();
		        if (_log.isDebugEnabled()) {
		            _log.debug("loadUsersListByNameAndOwnerIdQry", "QUERY sqlSelect=" + sqlSelect);
		        }
		        pstmt = p_con.prepareStatement(sqlSelect);
	            int i = 0;
	            pstmt.setString(++i, p_ownerId);
	            if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
	                pstmt.setString(++i, p_status);
	            }
	            pstmt.setString(++i, p_userType);
	            pstmt.setString(++i, p_categoryCode);
	            // commented for DB2pstmt.setFormOfUse(++i,
	            // OraclePreparedStatement.FORM_NCHAR);
	            pstmt.setString(++i, p_userName);

	        }
		return pstmt;
	}
	@Override
	public PreparedStatement loadApprovalUsersListQry(Connection p_con,
			String p_categoryCode, String p_lookupType, int p_sequenceNo,
			String p_grphDomainType, String p_networkCode,
			String p_parentGrphDomainCode, String p_status, String p_userType)
			throws SQLException {
		final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT U.batch_id, U.creation_type, u.user_id,u.user_name,u.network_code, ");
        strBuff.append(" u.login_id,u.password,u.category_code,u.parent_id,u.company,u.fax,u.firstname,u.lastname,UP.phone_language,UP.country CTRY, ");// phone_language,ctry,firstname,lastname,company,fax
        // added
        // by
        // deepika
        // aggarwal
        strBuff.append(" OUSR.user_name owner_name, PUSR.user_name parent_name, ");
        strBuff.append(" u.owner_id,u.allowed_ip,u.allowed_days, ");
        strBuff.append(" u.from_time,u.to_time,u.employee_code, ");
        strBuff.append(" u.status,u.email,u.pswd_modified_on,u.contact_no, ");
        strBuff.append(" u.designation,u.division,u.department,u.msisdn,u.user_type, ");
        strBuff.append(" u.created_by,u.created_on,u.modified_by,u.modified_on,u.address1, ");
        strBuff.append(" u.address2,u.city,u.state,u.country,u.rsaflag,u.ssn,u.user_name_prefix, ");
        strBuff.append(" u.external_code,u.short_name,u.level1_approved_by,u.level1_approved_on,");
        strBuff.append(" u.level2_approved_by,u.level2_approved_on,u.user_code,u.appointment_date,");
        strBuff.append(" u.previous_status,l.lookup_name,u1.user_name parent_user_name,u2.user_name request_user_name ");
        strBuff.append(" FROM users u left join users PUSR on  PUSR.user_id = u.parent_id left join users u2 on u2.user_id= u.modified_by ,  users u1,categories c,lookups l,users OUSR,user_phones UP WHERE ");// UP
        strBuff.append(" u.category_code = ? AND u.category_code = c.category_code ");
        strBuff.append(" AND OUSR.user_id = u.owner_id ");
        strBuff.append(" AND u1.user_id = u.created_by ");
        strBuff.append(" AND u.user_id = UP.user_id ");// added by deepika
        strBuff.append(" AND u.status = l.lookup_code ");
        strBuff.append(" AND l.lookup_type= ? ");
        strBuff.append(" AND u.status in (" + p_status + ") ");
        strBuff.append(" AND u.user_type = ? ");
        strBuff.append("AND c.sequence_no = ? AND u.user_id in((SELECT user_id ");
        strBuff.append("FROM user_geographies WHERE grph_domain_code IN( ");
        
        strBuff.append(" with recursive q as (  SELECT  grph_domain_code,grph_domain_type , network_code ");
        strBuff.append("FROM geographical_domains WHERE  ");
        strBuff.append("  grph_domain_code = ? union all select m.grph_domain_code,m.grph_domain_type , m.network_code  ");
        strBuff.append( "  from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code ");
        strBuff.append(" ) select q.grph_domain_code from q where ");
        strBuff.append( " grph_domain_type= ? AND network_code = ? ");
		 if ((p_status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) || (p_status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE))) {
	            strBuff.append(" AND u.barred_deletion_batchid IS NULL");
	      }
        strBuff.append(" )))");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadApprovalUsersListQry", "QUERY sqlSelect=" + sqlSelect);
        }
        PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);
        pstmt.setString(1, p_categoryCode);
        pstmt.setString(2, p_lookupType);
        pstmt.setString(3, p_userType);
        pstmt.setInt(4, p_sequenceNo);
        pstmt.setString(5, p_parentGrphDomainCode);
        pstmt.setString(6, p_grphDomainType);
        pstmt.setString(7, p_networkCode);
      
        return pstmt;
	}

	@Override
	public PreparedStatement isUserInSameGRPHDomainQry(Connection p_con, String p_userId, String p_sessionUserId,String p_userGrphDomainType, String p_sessionUserGrphDomainType) throws SQLException{
		
		 final StringBuilder strBuff = new StringBuilder();
		 PreparedStatement pstmt = null;
	        /*
	         * if both the searchedUser and sessionUser work on the same domain type
	         * then we need execute the query like " start with  grph_domain_code "
	         * else we need execute the query like
	         * " start with  parent_grph_domain_code "
	         */
	        if (p_userGrphDomainType.equals(p_sessionUserGrphDomainType)) {
	            strBuff.append(" with recursive q as ( SELECT 1,grph_domain_code,status from geographical_domains ");
	            strBuff.append(" WHERE grph_domain_code in (SELECT grph_domain_code from user_geographies where user_id = ?) ");
	            strBuff.append(" union all  ");
	            strBuff.append(" SELECT 1,m.grph_domain_code,m.status from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code ");
	            strBuff.append(" )select 1 from q where status <> 'N' AND grph_domain_code in (SELECT grph_domain_code from user_geographies where user_id = ?)  ");
	        } else {
	        	strBuff.append(" with recursive q as ( SELECT 1,grph_domain_code,status from geographical_domains ");
	            strBuff.append(" WHERE  ");
	            strBuff.append("  parent_grph_domain_code  in (SELECT grph_domain_code from user_geographies where user_id = ?)  union all  ");
	            strBuff.append(" SELECT 1,m.grph_domain_code,m.status from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code ");
	            strBuff.append(" )select 1 from q where status <> 'N' AND grph_domain_code in (SELECT grph_domain_code from user_geographies where user_id = ?) ");
	        }

	        final String sqlSelect = strBuff.toString();
	        if (_log.isDebugEnabled()) {
	            _log.debug("isUserInSameGRPHDomainQry", "QUERY sqlSelect=" + sqlSelect);
	        }
	        pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_sessionUserId);
            pstmt.setString(2, p_userId);
         
            
		return pstmt;
	}
	@Override
	public String loadUsersListForUserTypeQry( String p_statusUsed,String p_status, boolean p_isChannelUser)  {
		final StringBuilder strBuff = new StringBuilder();
		PreparedStatement pstmt = null;
        strBuff.append(" Select distinct(U.user_id) usr_user_id,U.user_name usr_user_name,U.login_id ");
        strBuff.append("FROM users U,user_geographies UG ");
        if (p_isChannelUser) {
            strBuff.append(" ,(with recursive q as (SELECT user_id FROM USERS where user_id= ? union all select m.user_id from USERS m join q on q.user_id=m.parent_id ) select q.user_id from q ) X ");
        }
        strBuff.append(" WHERE U.user_id=UG.user_id ");
        if (p_isChannelUser) {
            strBuff.append(" AND X.user_id=U.user_id ");
        }
        strBuff.append(" AND U.user_type= CASE ? WHEN 'ALL' THEN U.user_type ELSE ? END ");
        strBuff.append(" AND U.network_code = ? AND U.category_code =? ");
        strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?) ");
        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append("AND U.status IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append("AND U.status NOT IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append("AND U.status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append("AND U.status <> ? ");
        }
        strBuff.append(" AND UG.grph_domain_code IN (with recursive q as (SELECT grph_domain_code,status FROM geographical_domains GD1 WHERE ");
        strBuff.append("  grph_domain_code IN (SELECT grph_domain_code  ");
        strBuff.append("FROM user_geographies UG1 WHERE UG1.user_id =? ) union all SELECT m.grph_domain_code,m.status FROM geographical_domains m join q on ");
        strBuff.append(" q.grph_domain_code = m.parent_grph_domain_code ) ");
        strBuff.append("  select q.grph_domain_code from q  where (status = 'Y' OR status = 'S')  ) ");
        strBuff.append(" ORDER BY U.user_name ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersListForUserTypeQry", "QUERY sqlSelect=" + sqlSelect);
        }

		return strBuff.toString();
	}
	@Override
	public String loadUsersListByUserTypeQry(String p_categoryCode, String p_userName, String p_userID, String p_ownerID, String p_sessionUserID, String p_statusUsed, String p_status, String p_userType) {
		final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
        strBuff.append("USR.login_id,USR.password passwd,USR.category_code usr_category_code,USR.parent_id,");
        strBuff.append("USR.owner_id,USR.allowed_ip,USR.allowed_days,USR_CAT.services_allowed,");
        strBuff.append("USR.from_time,USR.to_time,USR.employee_code,USR.company,USR.fax,USR.firstname,USR.lastname, ");// firstname,lastname,company,fax
        // added
        // by
        // deepika
        // aggarwal
        strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,");
        strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
        strBuff.append("USR.created_by,USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
        strBuff.append("USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
        strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,USR_CAT.PRODUCT_TYPES_ALLOWED,USR_CAT.LOW_BAL_ALERT_ALLOW,");
        strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date, ");
        strBuff.append("USR.previous_status,USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name,");
        strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type, ");
        strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, ");
        strBuff
            .append("USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed,USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed,USR_CAT.restricted_msisdns, ");
        strBuff.append("PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn, ");
        strBuff.append("PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn, ");
        strBuff
            .append("ONR_CAT.category_name owner_cat,l.lookup_name, USR_CAT.transfertolistonly, USR_CAT.outlets_allowed,USR.AUTHENTICATION_ALLOWED,usr_cat.AUTHENTICATION_TYPE ");
        strBuff.append("FROM users USR left join (users PRNT_USR left join  categories  PRNT_CAT on PRNT_USR.category_code=PRNT_CAT.category_code )on USR.parent_id=PRNT_USR.user_id, users ONR_USR,categories USR_CAT, ");
        strBuff.append("categories ONR_CAT, lookups l ");
        strBuff.append("WHERE USR.network_code = ? ");

        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append("AND USR.status IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append("AND USR.status NOT IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append("AND USR.status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append("AND USR.status <> ? ");
        }

        if (!BTSLUtil.isNullString(p_categoryCode)) {
            strBuff.append(" AND USR.category_code =?");
        }
        if (!BTSLUtil.isNullString(p_userID)) {
            strBuff.append(" AND USR.user_id =?");
        }
        if (!BTSLUtil.isNullString(p_ownerID)) {
            strBuff.append(" AND USR.owner_id =?");
        }
        if (!BTSLUtil.isNullString(p_userName)) {
            strBuff.append(" AND UPPER(USR.user_name) LIKE UPPER(?) ");
        }
        strBuff.append("AND USR.owner_id=ONR_USR.user_id ");
        strBuff.append("AND USR.category_code=USR_CAT.category_code ");
        strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
        strBuff.append(" AND USR.status = l.lookup_code ");
        strBuff.append(" AND l.lookup_type= ? ");
        if (!BTSLUtil.isNullString(p_userType)) {
            strBuff.append(" AND USR.user_type =?");
        }
        if (p_sessionUserID != null) {
            strBuff.append(" AND USR.user_id IN ( with recursive q as (  SELECT user_id from users where user_id = ? union all ");
            strBuff.append(" SELECT m.user_id from users m join q on q.user_id = m.parent_id ");
            strBuff.append(" )select q.user_id from q   where user_id != ? ) ");
        }
        strBuff.append(" ORDER BY USR.user_name ");
		return strBuff.toString();
	}
	@Override
	public PreparedStatement loadUserListOnZoneCategoryHierarchyQry(Connection p_con, String p_userCategory, String p_zoneCode, String p_userName, String p_loginuserID, String domainCode) throws SQLException{
		PreparedStatement pstmtSelect = null;
		  final StringBuilder strBuff = new StringBuilder("");
	        strBuff.append(" WITH RECURSIVE q AS ( ");
	        strBuff.append(" select U.user_id, U.user_name, U.login_id , U.category_code, CAT.domain_code,U.STATUS,U.user_type,UG.user_id ug_user_id,UG.grph_domain_code ,CAT.category_code cat_category_code FROM	users U, user_geographies UG, categories CAT  ");
	        strBuff.append(" where U.user_id= ? ");
	        strBuff.append(" union all  ");
	        strBuff.append(" select u2.user_id, u2.user_name, u2.login_id, U2.category_code, CAT2.domain_code,U2.STATUS,U2.user_type,UG2.user_id ug_user_id,UG2.grph_domain_code ,CAT2.category_code cat_category_code  FROM	users U2 join q on  q.user_id = u2.parent_id , user_geographies UG2, categories CAT2 ");
	        strBuff.append("  ) SELECT  user_id, user_name, login_id ");
	        strBuff.append(" FROM q where category_code = case ? when  '" + PretupsI.ALL + "' then  category_code else ? end ");
	        strBuff.append(" AND domain_code IN (SELECT UD.domain_code FROM user_domains UD WHERE UD.domain_code = ( case ? when '" + PretupsI.ALL + "' then  UD.domain_code else  ? end )) ");
	        strBuff.append("  AND (STATUS = ? or status = ?) ");
	        strBuff.append(" AND user_type = '" + PretupsI.CHANNEL_USER_TYPE + "' ");
	        strBuff.append(" AND category_code = cat_category_code ");
	        strBuff.append(" AND user_id =ug_user_id ");
	        strBuff.append(" AND grph_domain_code IN (");
	        strBuff.append("  WITH RECURSIVE q2 AS (");
	        		 strBuff.append(" SELECT GD1.grph_domain_code, GD1.status FROM geographical_domains GD1 where GD1.grph_domain_code IN (SELECT grph_domain_code FROM user_geographies UG1 ");
	        		strBuff.append(" WHERE	UG1.grph_domain_code = case ? when  '" + PretupsI.ALL + "' then  UG1.grph_domain_code else ? end ");
	    	        strBuff.append(" AND UG1.user_id= ? ) ");
	    	        strBuff.append("union all ");
	    	        strBuff.append(" SELECT GD2.grph_domain_code, GD2.status FROM geographical_domains GD2 join q2 on q2.grph_domain_code = GD2.parent_grph_domain_code ");
	    	        strBuff.append(")SELECT grph_domain_code FROM q2 where status IN(?, ?) ");
	        
	        strBuff.append( ")");
	        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) ");
	        
	        final String sqlSelect = strBuff.toString();
	        if (_log.isDebugEnabled()) {
	            _log.debug("loadUserListOnZoneCategoryHierarchyQry", "QUERY sqlSelect=" + sqlSelect);
	        }
	  
	        // commented for DB2 pstmtSelect =
            // (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 0;
            pstmtSelect.setString(++i, p_loginuserID);
            pstmtSelect.setString(++i, p_userCategory);
            pstmtSelect.setString(++i, p_userCategory);
            pstmtSelect.setString(++i, domainCode);
            pstmtSelect.setString(++i, domainCode);
            pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);
            pstmtSelect.setString(++i, p_zoneCode);
            pstmtSelect.setString(++i, p_zoneCode);
            pstmtSelect.setString(++i, p_loginuserID);
            pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);
            if (!BTSLUtil.isNullString(p_userName)) {
                // commented for DB2pstmtSelect.setFormOfUse(++i,
                // OraclePreparedStatement.FORM_NCHAR);
                pstmtSelect.setString(++i, "%" + p_userName + "%");
            } else {
                // commented for DB2pstmtSelect.setFormOfUse(++i,
                // OraclePreparedStatement.FORM_NCHAR);
                pstmtSelect.setString(++i, "%");
            }
	        
		return pstmtSelect;
	}
	@Override
	public PreparedStatement loadSTKApprovalUsersListQry(Connection p_con,
			String p_categoryCode, String p_lookupType, int p_sequenceNo,
			String p_grphDomainType, String p_networkCode,
			String p_parentGrphDomainCode, String p_status) throws SQLException {
		final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT U.batch_id, U.creation_type, u.user_id,u.user_name,u.network_code,");
        strBuff.append("u.login_id,u.password,u.category_code,u.parent_id,");
        strBuff.append("OUSR.user_name owner_name, PUSR.user_name parent_name,");
        strBuff.append("u.owner_id,u.allowed_ip,u.allowed_days,");
        strBuff.append("u.from_time,u.to_time,u.employee_code,");
        strBuff.append("u.status,u.email,u.pswd_modified_on,u.contact_no,");
        strBuff.append("u.designation,u.division,u.department,u.msisdn,u.user_type,");
        strBuff.append("u.created_by,u.created_on,u.modified_by,u.modified_on,u.address1, ");
        strBuff.append("u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix, ");
        strBuff.append("u.external_code,u.short_name,u.level1_approved_by,u.level1_approved_on,");
        strBuff.append("u.level2_approved_by,u.level2_approved_on,u.user_code,u.appointment_date,");
        strBuff.append("u.previous_status,l.lookup_name,u1.user_name parent_user_name,u2.user_name request_user_name ");
        strBuff.append("FROM users u left join users PUSR on (PUSR.user_id = u.parent_id ) left join users u2 on u2.user_id = u.modified_by ,users u1,categories c,lookups l,users OUSR WHERE ");
        strBuff.append(" u.category_code = ? AND u.category_code = c.category_code ");
        strBuff.append(" AND OUSR.user_id = u.owner_id ");
        strBuff.append(" AND u1.user_id = u.created_by ");
        strBuff.append(" AND u.status = l.lookup_code ");
        strBuff.append(" AND l.lookup_type= ? ");
        strBuff.append(" AND u.status in (" + p_status + ") ");
        strBuff.append(" AND u.user_type = ? and u.creation_type='S' and u.LEVEL1_APPROVED_BY is null and u.LEVEL1_APPROVED_ON is null ");
        strBuff.append("AND c.sequence_no = ? AND u.user_id in((SELECT user_id ");
        strBuff.append("FROM user_geographies WHERE grph_domain_code IN(");
        strBuff.append("with recursive q as ( SELECT  grph_domain_code,grph_domain_type,network_code ");
        strBuff.append("FROM geographical_domains WHERE  ");
        strBuff.append("  grph_domain_code = ? union all select m.grph_domain_code,m.grph_domain_type,m.network_code from geographical_domains m join q ");
        strBuff.append(" on q.grph_domain_code=m.parent_grph_domain_code ");
        strBuff.append("  ) select q.grph_domain_code from q where grph_domain_type= ? and  network_code = ?");
        strBuff.append(" )))");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadSTKApprovalUsersListQry", "QUERY sqlSelect=" + sqlSelect);
        }
        PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);
        pstmt.setString(1, p_categoryCode);
        pstmt.setString(2, p_lookupType);
        pstmt.setString(3, PretupsI.CHANNEL_USER_TYPE);
        pstmt.setInt(4, p_sequenceNo);
        pstmt.setString(5, p_parentGrphDomainCode);
        pstmt.setString(6, p_grphDomainType);
        pstmt.setString(7, p_networkCode);
		return pstmt;
	}
	@Override
	public String checkBarLimitQry(){
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT COUNT(*) as LIMIT FROM user_event_remarks WHERE date_trunc('day',created_on::TIMESTAMP) = ? AND event_type = ?");
        return strBuff.toString();
	}
	@Override
	public String loadOwnerUserListForUserTransferQry(String p_statusUsed, String p_status) {
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT u.user_name, u.user_id, u.owner_id, u.login_id ");
        strBuff.append("FROM users u,user_geographies ug,categories c ");
        strBuff.append("WHERE UPPER(u.user_name) LIKE UPPER(?) ");
        // Added for bar for del by shashank
        if ((p_status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) || (p_status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE))) {
            strBuff.append(" AND u.barred_deletion_batchid IS NULL ");
        }
        // end
        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append("AND u.status IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append("AND u.status NOT IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append("AND u.status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append("AND u.status <> ? ");
        }
        strBuff.append("AND u.user_type ='CHANNEL' ");
        strBuff.append("AND u.user_id <> ? ");
        strBuff.append("AND u.user_id = ug.user_id AND c.domain_code=? ");
        strBuff.append("AND u.category_code=c.category_code ");
        strBuff.append("AND c.SEQUENCE_NO='1'  ");
        strBuff.append("AND ug.grph_domain_code IN ( ");
        strBuff.append("with recursive q as ( SELECT grph_domain_code  ");
        strBuff.append("FROM geographical_domains WHERE grph_domain_code = ?  union all SELECT m.grph_domain_code ");
        strBuff.append("FROM geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code ");
        strBuff.append(") select q.grph_domain_code from q where grph_domain_type=c.grph_domain_type ");
        strBuff.append(" )  ");
        strBuff.append("ORDER BY user_name ");
		return strBuff.toString();
	}

	@Override
	public String checkOwnerListQuery(String p_statusUsed, String p_status) {
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT u.user_name, u.user_id, u.owner_id, u.login_id ");
        strBuff.append("FROM users u,user_geographies ug,categories c ");
        strBuff.append("WHERE UPPER(u.user_name) LIKE UPPER(?) ");
        // Added for bar for del by shashank
        if ((p_status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) || (p_status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE))) {
            strBuff.append(" AND u.barred_deletion_batchid IS NULL ");
        }
        // end
        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append("AND u.status IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append("AND u.status NOT IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append("AND u.status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append("AND u.status <> ? ");
        }
        strBuff.append("AND u.user_type ='CHANNEL' ");
        strBuff.append("AND u.user_id = ug.user_id AND c.domain_code=? ");
        strBuff.append("AND u.category_code=c.category_code ");
        strBuff.append("AND c.SEQUENCE_NO='1'  ");
        strBuff.append("AND u.login_id = ? ");
        strBuff.append("AND ug.grph_domain_code IN ( ");
        strBuff.append("with recursive q as (SELECT grph_domain_code,grph_domain_type FROM geographical_domains where ");
        strBuff.append(" grph_domain_code = ? union all select m.grph_domain_code, m.grph_domain_type from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code   ");
        strBuff.append(" ) select q.grph_domain_code from q   WHERE grph_domain_type=c.grph_domain_type ");
        strBuff.append(" )  ");
        strBuff.append("ORDER BY user_name ");
		return strBuff.toString();
		
	}

}
