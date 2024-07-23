package com.btsl.db.query.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.user.businesslogic.ChannelUserWebQry;

public class ChannelUserWebPostgresQry implements ChannelUserWebQry {
	//first
	@Override 
	public PreparedStatement loadCategoryUsersWithinGeoDomainHirearchyQry(Connection con,String p_networkCode,String p_categoryCode,String p_geographicalDomainCode,String p_userName,String p_loginUserID,String p_ownerUserID,String statusAllowed,String receiverStatusAllowed,String p_userId) throws SQLException{
	StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT DISTINCT U.user_id, U.user_name FROM users U, user_geographies UG, categories CAT ");
		strBuff.append(" WHERE U.network_code = ? AND U.category_code = ?  ");
		if(p_ownerUserID == null) {
            strBuff.append(" AND u.status IN ("+statusAllowed+") ");
        } else {
            strBuff.append(" AND u.status IN ("+receiverStatusAllowed+") ");
        }
		strBuff.append(" AND u.user_type = 'CHANNEL' ");
		strBuff.append(" AND U.category_code = CAT.category_code AND U.user_id=UG.user_id ");
		
		strBuff.append(" AND UG.grph_domain_code IN ( WITH RECURSIVE Q AS (SELECT grph_domain_code, status FROM geographical_domains GD1 WHERE ");
		strBuff.append("  grph_domain_code IN (SELECT grph_domain_code ");
		strBuff.append(" FROM user_geographies UG1 ");
		strBuff.append(" WHERE UG1.grph_domain_code = ? ");
		strBuff.append(" AND UG1.user_id = ? ) UNION ALL SELECT M.grph_domain_code, m.status FROM  geographical_domains M JOIN Q ON Q.grph_domain_code = M.parent_grph_domain_code   )SELECT grph_domain_code FROM Q where status = ? )");
		strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?) ");
		if (p_ownerUserID != null && !p_ownerUserID.equalsIgnoreCase("NA")) {
			strBuff.append(" AND  u.owner_id = ?  ");
		}
		 strBuff.append(" AND U.user_id = ?");
		strBuff.append(" ORDER BY U.user_name");

		String sqlSelect = strBuff.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug("", "QUERY sqlSelect=" + sqlSelect);
		}
		PreparedStatement pstmt = con.prepareStatement(sqlSelect);
		int i = 0;
		pstmt.setString(++i, p_networkCode);
		pstmt.setString(++i, p_categoryCode);
		pstmt.setString(++i, p_geographicalDomainCode);
		pstmt.setString(++i, p_loginUserID);
		pstmt.setString(++i, PretupsI.GEO_DOMAIN_STATUS);
		pstmt.setString(++i, p_userName);
		if (p_ownerUserID != null && !p_ownerUserID.equalsIgnoreCase("NA")) {
			pstmt.setString(++i, p_ownerUserID);
		}
		pstmt.setString(++i, p_userId);
		return pstmt;
	}
	
	// third
	@Override
	public PreparedStatement loadCategoryUsersWithinGeoDomainHirearchyQry(Connection con,String p_networkCode,String p_categoryCode,String p_geographicalDomainCode,String p_userName,String p_loginUserID,String p_ownerUserID,String statusAllowed,String receiverStatusAllowed ) throws SQLException{
		
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT DISTINCT OU.user_id, OU.user_name,OU.msisdn,ou.login_id FROM users U, users ou, user_geographies UG, categories CAT ");
		strBuff.append(" WHERE OU.network_code = ? AND OU.category_code = ?  ");
		if(p_ownerUserID == null) {
            strBuff.append(" AND ou.status IN ("+statusAllowed+") ");
        } else {
            strBuff.append(" AND ou.status IN ("+receiverStatusAllowed+") ");
        }
		strBuff.append(" AND ou.user_type = 'CHANNEL' ");
		strBuff.append(" AND OU.category_code = CAT.category_code AND  UG.user_id=ou.user_id    and ou.user_id =u.owner_id ");
		
		strBuff.append(" AND UG.grph_domain_code IN ( WITH RECURSIVE Q AS (SELECT grph_domain_code, status FROM geographical_domains GD1  ");
		strBuff.append(" WHERE grph_domain_code IN (SELECT grph_domain_code ");
		strBuff.append(" FROM user_geographies UG1 ");
		if(!BTSLUtil.isEmpty(p_geographicalDomainCode))
		{
			strBuff.append(" WHERE UG1.grph_domain_code = ? ");
			strBuff.append(" AND UG1.user_id = ? ) UNION ALL SELECT M.grph_domain_code,M.status FROM  geographical_domains M JOIN Q ON Q.grph_domain_code = M.parent_grph_domain_code  )SELECT Q.grph_domain_code FROM Q where  Q.status = ? )");			
		}
		else
		{
			strBuff.append(" WHERE UG1.user_id = ? ) UNION ALL SELECT M.grph_domain_code,M.status FROM  geographical_domains M JOIN Q ON Q.grph_domain_code = M.parent_grph_domain_code  )SELECT Q.grph_domain_code FROM Q where  Q.status = ? )");
		}
		
		strBuff.append(" AND UPPER(ou.user_name) LIKE UPPER(?) ");
		if (p_ownerUserID != null && !p_ownerUserID.equalsIgnoreCase("NA")) {
			strBuff.append(" AND  u.owner_id = ?  ");
		}
		strBuff.append(" ORDER BY OU.user_name");

		String sqlSelect = strBuff.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug("", "QUERY sqlSelect=" + sqlSelect);
		}
		PreparedStatement pstmt = con.prepareStatement(sqlSelect);
		int i = 0;
		pstmt.setString(++i, p_networkCode);
		pstmt.setString(++i, p_categoryCode);
		if(!BTSLUtil.isEmpty(p_geographicalDomainCode))
		{
			pstmt.setString(++i, p_geographicalDomainCode);
		}
		pstmt.setString(++i, p_loginUserID);
		pstmt.setString(++i, PretupsI.GEO_DOMAIN_STATUS);
		pstmt.setString(++i, p_userName);
		if (p_ownerUserID != null && !p_ownerUserID.equalsIgnoreCase("NA")) {
			pstmt.setString(++i, p_ownerUserID);
		}
		return pstmt;
	}


	@Override
	public String loadChannelUserHierarchyQry(boolean p_isUserCode ){
		 StringBuilder strBuff = new StringBuilder();
		 strBuff.append(" SELECT UP.msisdn, X.user_id,X.user_name,X.user_code,X.network_code, X.login_id ");
	     strBuff.append(" FROM ( with recursive q as (SELECT U.user_id,U.user_name,U.user_code,U.network_code, U.login_id, U.user_type, U.status  ");
	     strBuff.append(" FROM users U WHERE ");
	     if (p_isUserCode) {
	         strBuff.append(" U.user_code=? ");
	     } else {
	         strBuff.append(" U.user_id=? ");
	     }
	     strBuff.append(" union all select m.user_id,m.user_name,m.user_code,m.network_code, m.login_id, m.user_type, m.status FROM users m join q on q.user_id=m.parent_id  ");
	     strBuff.append(" )select * from q where  user_type='" + PretupsI.CHANNEL_USER_TYPE + "' AND NOT status IN('N','C') )X, user_phones UP WHERE X.user_id=UP.user_id AND UP.primary_number='Y'  ");
	     return strBuff.toString();
	     
	     
		}
	
	
	@Override
	public PreparedStatement deleteOrSuspendChnlUsersInBulkForMsisdn(Connection con,String userID,Map prepareStatementMap) throws SQLException{
		StringBuilder childExist = new StringBuilder();
		childExist.append("With recursive q as (SELECT 1,u1.status,u1.user_type,u1.user_id   FROM users u1 ");
        childExist.append(" WHERE");
        childExist.append("  u1.user_id = ? Union all SELECT 1,m.status,m.user_type,m.user_id  FROM users m join q on q.user_id = m.parent_id  ");
        childExist.append(" )select * from q where status <> 'N' AND status <> 'C' and user_id != ? and user_type=?  ");
        PreparedStatement psmtChildExist = (PreparedStatement) prepareStatementMap.get("psmtChildExist");
        if (psmtChildExist == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("processUploadedFileForUnReg", "psmtChildExist=" + childExist);
            }
            psmtChildExist = con.prepareStatement(childExist.toString());
            prepareStatementMap.put("psmtChildExist", psmtChildExist);
        }
        psmtChildExist.clearParameters();
        psmtChildExist.setString(1, userID);
        psmtChildExist.setString(2, userID);
        psmtChildExist.setString(3, PretupsI.CHANNEL_USER_TYPE);

        return psmtChildExist;
	}
	
	@Override
	public PreparedStatement loadChannelUserListQry(Connection con,String p_userCategory,String p_domainCode,String p_userName,String p_userID,String p_zoneCode) throws SQLException{
		  StringBuilder strBuff = new StringBuilder(" SELECT DISTINCT U.user_id, U.user_name,U.owner_id,OU.user_name owner_name,U.parent_id ");
	        strBuff.append(" FROM users U,users OU, user_geographies UG, categories CAT ");
	        strBuff.append(" WHERE U.owner_id=OU.user_id AND U.category_code = ? AND U.status='Y'  ");
	        strBuff.append(" AND U.category_code = CAT.category_code AND CAT.domain_code = ? ");
	        strBuff.append(" AND U.user_id = UG.user_id AND U.user_type='CHANNEL' AND UG.grph_domain_code IN  ");
	        strBuff.append(" (WITH RECURSIVE Q AS (SELECT grph_domain_code, status FROM geographical_domains GD1  ");
	        strBuff.append(" where grph_domain_code IN (SELECT grph_domain_code ");
	        strBuff.append(" FROM user_geographies UG1 WHERE UG1.grph_domain_code = ? ");
	        strBuff.append(" AND UG1.user_id= ? ) UNION ALL SELECT M.grph_domain_code, m.status FROM geographical_domains M JOIN Q ON Q.grph_domain_code = M.parent_grph_domain_code  )SELECT grph_domain_code FROM Q WHERE status IN(?, ?) ) AND UPPER(U.user_name) LIKE UPPER(?) ");
	        strBuff.append(" ORDER BY U.user_name");

	        final String sqlSelect = strBuff.toString();
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("loadChannelUserListQry", "QUERY sqlSelect=" + sqlSelect);
	        }

	       PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);
           int i = 0;
           ++i;
           pstmtSelect.setString(i, p_userCategory);
           ++i;
           pstmtSelect.setString(i, p_domainCode);
           ++i;
           pstmtSelect.setString(i, p_zoneCode);
           ++i;
           pstmtSelect.setString(i, p_userID);
           ++i;
           pstmtSelect.setString(i, PretupsI.STATUS_ACTIVE);
           ++i;
           pstmtSelect.setString(i, PretupsI.STATUS_SUSPEND);
           ++i;
           pstmtSelect.setString(i, p_userName);
           return pstmtSelect;
	}
	
	@Override
	public PreparedStatement loadCategoryUserHierarchyQry(Connection con,String p_networkCode,String p_categoryCode,String p_loginUserID,String p_userName) throws SQLException{
		final StringBuffer strBuff = new StringBuffer(" with recursive q as (SELECT U.user_id, U.user_name,U.status,U.network_code , U.category_code, U.user_type FROM users U ");
        strBuff.append(" WHERE U.user_id= ? ");
        strBuff.append(" union all select m.user_id, m.user_name,m.status,m.network_code , m.category_code, m.user_type from users m join q on q.user_id=m.parent_id  ");
        strBuff.append(" ) select user_id , user_name from q  where status = 'Y' AND  network_code = ? AND category_code = ? AND user_type='CHANNEL' ");
        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) ");
        strBuff.append(" ORDER BY user_name");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadCategoryUserHierarchyQry", "QUERY sqlSelect=" + sqlSelect);
        }
        PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);
        int i = 0;
        ++i;
        pstmtSelect.setString(i, p_loginUserID);
        ++i;
        pstmtSelect.setString(i, p_networkCode);
        ++i;
        pstmtSelect.setString(i, p_categoryCode);
        ++i;
        pstmtSelect.setString(i, p_userName);
        return pstmtSelect;

	}
	@Override
	public PreparedStatement loadUsersForEnquiryQry(Connection con,String p_networkCode,String p_categoryCode,String p_loginUserID,String p_userName,String p_ownerUserID,boolean p_isOnlyActiveUser,String p_geographicalDomainCode)throws SQLException
	{
		final StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT DISTINCT U.user_id, U.user_name FROM users U, user_geographies UG, categories CAT ");
        strBuff.append(" WHERE U.network_code = ? AND U.category_code = ?  ");
        if (!p_isOnlyActiveUser) {
            strBuff.append(" AND u.status IN (" + PretupsBL.userStatusIn() + ", '" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "') ");
        } else {
            strBuff.append(" AND u.status = 'Y' ");
        }
        strBuff.append(" AND u.user_type = 'CHANNEL' ");
        strBuff.append(" AND U.category_code = CAT.category_code AND U.user_id=UG.user_id ");
        strBuff.append(" AND UG.grph_domain_code IN (with recursive q as ( SELECT grph_domain_code, status FROM geographical_domains GD1 ");
        strBuff.append(" where  grph_domain_code IN (SELECT grph_domain_code ");
        strBuff.append(" FROM user_geographies UG1 ");
        strBuff.append(" WHERE UG1.grph_domain_code = ? ");
        strBuff.append(" AND UG1.user_id = ? ) union all select m.grph_domain_code,m.status from geographical_domains m join q on  q.grph_domain_code = m.parent_grph_domain_code ) select grph_domain_code from q   where status = ? )");
        strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?) ");
        if (p_ownerUserID != null) {
            strBuff.append(" AND  u.owner_id = ?  ");
        }
        strBuff.append(" ORDER BY U.user_name");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadUsersForEnquiryQry", "QUERY sqlSelect=" + sqlSelect);
        }


        PreparedStatement pstmt = con.prepareStatement(sqlSelect);
        int i = 0;
        ++i;
        pstmt.setString(i, p_networkCode);
        ++i;
        pstmt.setString(i, p_categoryCode);
        ++i;
        pstmt.setString(i, p_geographicalDomainCode);
        ++i;
        pstmt.setString(i, p_loginUserID);
        ++i;
        pstmt.setString(i, PretupsI.GEO_DOMAIN_STATUS);
        ++i;
        pstmt.setString(i, p_userName);

        if (p_ownerUserID != null) {
            ++i;
            pstmt.setString(i, p_ownerUserID);
        }

        return pstmt;
	}
	
	@Override
	public String loadUsersForBatchFOCQry(String  p_categoryCode,String p_geographicalDomainCode,String receiverStatusAllowed){

	   	 final StringBuffer strBuff = new StringBuffer();
	        strBuff.append("SELECT U.user_name,U.user_id,U.user_code,U.msisdn,U.login_id,U.category_code,C.category_name,CG.grade_code,U.status,");
	        strBuff.append("CG.grade_name,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend,U.external_code, ");
	        strBuff.append("CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
	        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
	        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
	        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW))).booleanValue()) {
	            strBuff.append(" , CU.trf_rule_type  ");
	        }
	        strBuff.append(" FROM users U,channel_users CU,channel_grades CG,categories C,user_geographies UG, ");
	        strBuff.append(" commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
	        strBuff.append(" WHERE U.network_code=? AND U.user_id=CU.user_id AND U.user_id=UG.user_id AND ");
	        strBuff.append(" U.category_code=C.category_code AND U.category_code=CG.category_code AND CU.user_grade=CG.grade_code ");
	        strBuff.append(" AND U.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
	        strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
	        strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND C.category_code IN (" + p_categoryCode + ") ");
	        strBuff.append(" AND C.domain_code =? AND U.status IN (" + receiverStatusAllowed + ") AND C.status='Y' ");
	        strBuff.append(" AND UG.grph_domain_code IN (with recursive q as( SELECT grph_domain_code, status FROM geographical_domains GD1 ");
	        strBuff.append(" WHERE   ");
	        strBuff.append("  grph_domain_code IN(" + p_geographicalDomainCode + ") union all select m.grph_domain_code, m.status from geographical_domains m join q on  q.grph_domain_code = m.parent_grph_domain_code ) select grph_domain_code from q where status = 'Y' ) ");
	        strBuff.append(" AND CPSV.applicable_from =coalesce ( (SELECT MAX(applicable_from) FROM ");
	        strBuff.append(" commission_profile_set_version WHERE applicable_from <= ? AND ");
	        strBuff.append(" comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from) ");
	        strBuff.append("ORDER BY C.sequence_no,CU.user_grade,U.login_id");
	        return strBuff.toString();
		}
	
	@Override
	public String validateUsersForBatchC2CQry(String p_categoryCode,String StatusAllowed)
{
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT U.user_id,CPSV.dual_comm_type,U.user_code,U.msisdn,U.login_id,U.category_code,C.category_name,CG.grade_code,U.status,");
        strBuff.append("CG.grade_name,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend,U.external_code, ");
        strBuff.append("CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version,CPSV.OTH_COMM_PRF_SET_ID, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
        strBuff.append(" FROM users U,channel_users CU,channel_grades CG,categories C,user_geographies UG, ");
        strBuff.append(" commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append(" WHERE ( U.login_id= ? OR U.msisdn= ? ) AND U.network_code=? AND U.user_id=CU.user_id AND U.user_id=UG.user_id AND ");
        strBuff.append(" U.category_code=C.category_code AND U.category_code=CG.category_code AND CU.user_grade=CG.grade_code ");
        strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND C.category_code IN ('" + p_categoryCode + "') ");
        strBuff.append(" AND C.domain_code =? AND U.status IN (" + StatusAllowed + ") AND C.status='Y' ");

        strBuff.append(" AND CPSV.applicable_from =coalesce ( (SELECT MAX(applicable_from) FROM ");
        strBuff.append(" commission_profile_set_version WHERE applicable_from <= ? AND ");
        strBuff.append(" comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from) ");
        strBuff.append(" ORDER BY C.sequence_no,CU.user_grade,U.login_id");
        return strBuff.toString();
	}
	
	@Override
	public String ValidateChnlUserDetailsByExtCodeQry(){
		 final StringBuffer selectQueryBuff = new StringBuffer(
		            " SELECT u.user_id, u.password webpassword,u.user_name, u.network_code,u.login_id, u.category_code, u.parent_id, u.owner_id, u.msisdn,");
		        selectQueryBuff.append(" u.employee_code,u.status status,u.created_by,u.created_on,u.modified_by,u.modified_on,");
		        selectQueryBuff.append(" cusers.contact_person,u.contact_no,u.designation,u.division,u.department,u.user_type,cusers.in_suspend,cusers.out_suspend,");
		        selectQueryBuff.append(" u.address1,u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,");
		        selectQueryBuff.append(" cat.domain_code,dom.domain_type_code,cat.sequence_no catseq,cat.sms_interface_allowed,geo.grph_domain_code,gdomains.status geostatus, ");
		        selectQueryBuff.append(" uphones.msisdn prmsisdn, uphones.user_phones_id,uphones.primary_number, uphones.sms_pin, uphones.pin_required, uphones.phone_profile, uphones.phone_language phlang,");
		        selectQueryBuff.append(" uphones.country phcountry, uphones.invalid_pin_count, uphones.last_transaction_status, uphones.last_transaction_on,");
		        selectQueryBuff.append(" uphones.pin_modified_on,uphones.last_transfer_id, uphones.last_transfer_type,uphones.prefix_id,uphones.temp_transfer_id, uphones.first_invalid_pin_time, ");
		        selectQueryBuff.append(" cat.agent_allowed,cat.hierarchy_allowed, cat.category_type,cat.category_name,cat.grph_domain_type,cusers.comm_profile_set_id,cusers.transfer_profile_id, tp.status profile_status,cusers.user_grade grade_code,cset.status commprofilestatus, ");
		        selectQueryBuff.append(" cset.language_1_message comprf_lang_1_msg,cset.language_2_message  comprf_lang_2_msg,cat.restricted_msisdns,gdt.sequence_no grphSeq, cat.transfertolistonly, uphones.created_on uphones_created,CPSV.applicable_from,CPSV.comm_profile_set_version  ");
		        selectQueryBuff.append(" FROM channel_users cusers right outer join users u on cusers.user_id=u.user_id "); 
		        selectQueryBuff.append(" left outer join transfer_profile tp on cusers.transfer_profile_id=tp.profile_id" );
		        selectQueryBuff.append(" left outer join commission_profile_set cset on cusers.comm_profile_set_id=cset.comm_profile_set_id,user_geographies geo,categories cat,domains dom,user_phones uphones,commission_profile_set_version CPSV,geographical_domains gdomains,geographical_domain_types gdt ");
		        selectQueryBuff.append(" WHERE u.external_code=? AND uphones.user_id=u.user_id AND uphones.primary_number=? AND u.status <> ? AND u.status <> ? ");
		        selectQueryBuff
		            .append("  AND u.category_code = cat.category_code AND u.user_id=geo.user_id AND geo.grph_domain_code=gdomains.grph_domain_code ");
		        selectQueryBuff
		            .append(" AND cat.domain_code= dom.domain_code  AND gdt.grph_domain_type=gdomains.grph_domain_type ");
		        selectQueryBuff.append(" AND CPSV.applicable_from =coalesce ( (SELECT MAX(applicable_from) FROM ");
		        selectQueryBuff.append(" commission_profile_set_version WHERE applicable_from <= ? AND ");
		        selectQueryBuff.append(" comm_profile_set_id=cusers.comm_profile_set_id),CPSV.applicable_from) ");

		       return selectQueryBuff.toString();

	}
	
	@Override
	public String validateUsersForBatchDP(String p_categoryCode,String p_geographicalDomainCode){
		  final StringBuffer strBuff = new StringBuffer();
	        strBuff.append("SELECT U.user_id,U.user_code,U.msisdn,U.login_id,U.category_code,C.category_name,CG.grade_code,U.status,");
	        strBuff.append("CG.grade_name,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend,U.external_code, ");
	        strBuff.append("CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version,CPSV.dual_comm_type, TP.profile_name, ");
	        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
	        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
	        strBuff.append(" FROM users U,channel_users CU,channel_grades CG,categories C,user_geographies UG, ");
	        strBuff.append(" commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
	        strBuff.append(" WHERE U.user_code=? AND U.network_code=? AND U.user_id=CU.user_id AND U.user_id=UG.user_id AND ");
	        strBuff.append(" U.category_code=C.category_code AND U.category_code=CG.category_code AND CU.user_grade=CG.grade_code ");
	        strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
	        strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND C.category_code IN (" + p_categoryCode + ") ");
	        strBuff.append(" AND C.domain_code =? AND U.status <> 'N' AND U.status <> 'C' AND C.status='Y' ");
	        strBuff.append(" AND UG.grph_domain_code IN (with recursive q as (SELECT grph_domain_code, status FROM geographical_domains GD1 ");
	        strBuff.append(" WHERE  ");
	        strBuff.append("  grph_domain_code IN(" + p_geographicalDomainCode + ")  union all select m.grph_domain_code, m.status from geographical_domains m join q on  q.grph_domain_code = m.parent_grph_domain_code )SELECT grph_domain_code FROM q where status = 'Y' ) ");
	        strBuff.append(" AND CPSV.applicable_from =coalesce ( (SELECT MAX(applicable_from) FROM ");
	        strBuff.append(" commission_profile_set_version WHERE applicable_from <= ? AND ");
	        strBuff.append(" comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from) ");
	        strBuff.append(" ORDER BY C.sequence_no,CU.user_grade,U.login_id");
	        return strBuff.toString();
	      
	}
	
	@Override
	public
	PreparedStatement loadStaffUsersDetailsbyLoginIDforSuspend(Connection p_con,String p_chusrid,String p_loginID,String p_status) throws SQLException{
		final StringBuffer str = new StringBuffer(" with recursive q as ( SELECT U.USER_ID, u.LOGIN_ID,u.status , u.user_type FROM USERS U WHERE  user_id=? ");
        str.append("union all select m.user_id , m.LOGIN_ID,m.status , m.user_type  from users m join q on q.user_id=m.parent_id )select user_id from q  where LOGIN_ID=? and user_type=? AND status NOT IN(" + p_status + ") ");

        final String query = str.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadStaffUsersDetailsbyLoginIDforSuspend ", "Query: " + query);
        }
        int index=0;
        PreparedStatement pstm = p_con.prepareStatement(query);
        index++;
        pstm.setString(index, p_chusrid);
        index++;
        pstm.setString(index, p_loginID);
        index++;
        pstm.setString(index, PretupsI.USER_TYPE_STAFF);
        return pstm;
	}
	
	@Override
	public PreparedStatement loadStaffUsersDetailsForSuspend(Connection p_con,String p_status,String p_msisdn,String p_chuserid) throws SQLException{
		final StringBuffer str = new StringBuffer("with recursive q as ( SELECT U.USER_ID, up.user_id  up_user_id, up.MSISDN, u.user_type, u.status FROM USERS U, USER_PHONES UP WHERE u.user_id=?  ");
        str.append(" union all select u.USER_ID, up.user_id  up_user_id, up.MSISDN, u.user_type, u.status from users u join q  on  q.user_id=u.parent_id , USER_PHONES UP ");
        str.append( ") select USER_ID FROM q where  MSISDN=? AND up_user_id=USER_ID   AND user_type=? AND status NOT IN(" + p_status + ")  ");

        final String query = str.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadStaffUsersDetails ", "Query: " + query);
        }
        PreparedStatement pstm = p_con.prepareStatement(query);
        int index=0;
        index++;
        pstm.setString(index, p_chuserid);
        index++;
        pstm.setString(index, p_msisdn);
        index++;
        pstm.setString(index, PretupsI.USER_TYPE_STAFF);

        return pstm;

	}
	
	@Override
	public String loadChannelUserDetailsByUserNameQry(){
		 final StringBuffer strBuff = new StringBuffer(" SELECT U.user_id, user_name, login_id, user_type, msisdn FROM USERS U,USER_GEOGRAPHIES UG ");
        strBuff.append(" WHERE U.network_code=? AND U.user_name = ?  AND U.user_type=?  AND U.status NOT IN('N','C')   AND U.category_code=?  AND U.user_id=UG.user_id  ");
        strBuff.append(" AND UG.grph_domain_code IN (with recursive q as (SELECT grph_domain_code, status FROM GEOGRAPHICAL_DOMAINS GD1 WHERE   ");
        strBuff.append("  grph_domain_code IN (SELECT grph_domain_code FROM USER_GEOGRAPHIES UG1 ");
        strBuff.append("  WHERE	UG1.grph_domain_code = ? AND UG1.user_id=? ) union all select  m.grph_domain_code, m.status FROM GEOGRAPHICAL_DOMAINS m join q on q.grph_domain_code = m.parent_grph_domain_code  ) SELECT grph_domain_code from q where status IN('Y', 'S') ) ");
       return strBuff.toString();
       
	}
	@Override
	public PreparedStatement loadUsersForBatchO2CQry(Connection p_con,String[] m_categoryCode,String[] m_senderStatusAllowed,String[] m_geographicalDomainCode,Date p_comPrfApplicableDate,String p_domainCode,String p_networkCode,String p_productCode) throws SQLException{
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT U.user_id,U.user_code,U.msisdn,U.login_id,U.category_code,ub.balance,C.category_name,CG.grade_code,U.status,");
        strBuff.append("CG.grade_name,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend,U.external_code, ");
        strBuff.append("CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
        strBuff.append("FROM users U,channel_users CU,channel_grades CG,categories C,USER_BALANCES ub,user_geographies UG, ");
        strBuff.append("commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append("WHERE U.network_code=? AND U.user_id=CU.user_id AND U.user_id=UG.user_id AND U.user_id=ub.user_id AND ");
        strBuff.append("U.category_code=C.category_code AND U.category_code=CG.category_code AND CU.user_grade=CG.grade_code ");
        strBuff.append(" AND U.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
        strBuff.append("AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append("AND TP.profile_id = CU.transfer_profile_id AND C.category_code IN (");
        for (int i = 0; i < m_categoryCode.length; i++) {
            strBuff.append(" ?");
            if (i != m_categoryCode.length - 1) {
                strBuff.append(",");
            }
        }
        strBuff.append(")");

        strBuff.append("AND C.domain_code =? AND U.status IN (");
        for (int i = 0; i < m_senderStatusAllowed.length; i++) {
            strBuff.append(" ?");
            if (i != m_senderStatusAllowed.length - 1) {
                strBuff.append(",");
            }
        }
        strBuff.append(")");

        strBuff.append(" AND C.status='Y' AND UG.grph_domain_code IN ( with recursive q as (SELECT grph_domain_code, status FROM geographical_domains GD1 ");
        strBuff.append("WHERE  ");
        strBuff.append(" grph_domain_code IN(");

        for (int i = 0; i < m_geographicalDomainCode.length; i++) {
            strBuff.append(" ?");
            if (i != m_geographicalDomainCode.length - 1) {
                strBuff.append(",");
            }
        }
        strBuff.append(") union all select m.grph_domain_code, m.status from geographical_domains m join q on q.grph_domain_code = m.parent_grph_domain_code )select grph_domain_code from q where status = 'Y')");
        strBuff.append("AND CPSV.applicable_from =coalesce ( (SELECT MAX(applicable_from) FROM ");
        strBuff.append("commission_profile_set_version WHERE applicable_from <= ? AND ");
        strBuff.append("comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from) ");
        if(!BTSLUtil.isNullString(p_productCode))
			strBuff.append(" AND ub.product_code=? ");
        strBuff.append("ORDER BY C.sequence_no,CU.user_grade,U.login_id");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadUsersForBatchO2CQry", "QUERY sqlSelect=" + sqlSelect);
        }

        PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);
        int i = 0;
        ++i;
        pstmt.setString(i, p_networkCode);

        for (int x = 0; x < m_categoryCode.length; x++) {
            ++i;
            pstmt.setString(i, m_categoryCode[x]);
        }

        ++i;
        pstmt.setString(i, p_domainCode);

        for (int x = 0; x < m_senderStatusAllowed.length; x++) {
            ++i;
            pstmt.setString(i, m_senderStatusAllowed[x]);
        }

        for (int x = 0; x < m_geographicalDomainCode.length; x++) {
            ++i;
            pstmt.setString(i, m_geographicalDomainCode[x]);
        }
        ++i;
        pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_comPrfApplicableDate));
        if(!BTSLUtil.isNullString(p_productCode)){
        	 ++i;
        	 pstmt.setString(i, p_productCode);
        }
        return pstmt;
	}
	
	@Override
	public String loadParentUserDetailsByUserID(){
		final StringBuffer selectQueryBuff = new StringBuffer(
                " SELECT (case when u1.parent_id='ROOT' then u1.user_id ELSE u1.parent_id END) AS user_id, u2.owner_id, u2.user_name, u2.login_id, u2.network_code, u2.category_code, u2.msisdn, ");
            selectQueryBuff.append(" cat.category_code, cat.category_name, cat.domain_code, cat.multiple_grph_domains,dom.domain_type_code, ");
            selectQueryBuff.append(" cat.sequence_no,cat.multiple_login_allowed, cat.max_login_count,cat.view_on_network_block, ");
            selectQueryBuff.append(" cat.status catstatus, cat.max_txn_msisdn, cat.uncntrl_transfer_allowed, cat.scheduled_transfer_allowed, cat.restricted_msisdns, ");
            selectQueryBuff.append(" cat.parent_category_code, cat.product_types_allowed,cat.category_type,cat.hierarchy_allowed, cat.transfertolistonly, ");
            selectQueryBuff.append(" cat.grph_domain_type, cat.fixed_roles, cat.user_id_prefix,cat.web_interface_allowed, cat.sms_interface_allowed, ");
            selectQueryBuff.append(" cat.services_allowed,cat.domain_allowed,cat.fixed_domains,cat.outlets_allowed,cat.status categorystatus, gdt.grph_domain_type_name, gdt.sequence_no grph_sequence_no, ");
            selectQueryBuff.append(" cusers.contact_person, cusers.in_suspend, cusers.out_suspend, cusers.outlet_code, cusers.suboutlet_code, cusers.user_grade, cusers.comm_profile_set_id,cusers.transfer_profile_id, ");
            selectQueryBuff.append(" up.PHONE_PROFILE, up.pin_required ");
            selectQueryBuff.append(" FROM USERS u2 left outer join  user_phones up on u2.user_id=up.user_id" );
            selectQueryBuff.append(" left outer join channel_users cusers on u2.user_id=cusers.user_id " );
            selectQueryBuff.append(" left outer join users u1 on (u2.user_id= CASE WHEN u1.parent_id='ROOT'THEN u1.user_id ELSE u1.parent_id END) , categories cat, geographical_domain_types gdt, domains dom ");
            selectQueryBuff.append(" WHERE UPPER(u1.user_id)=UPPER(?) ");
            selectQueryBuff.append(" AND u2.category_code = cat.category_code ");
            selectQueryBuff.append(" AND gdt.grph_domain_type = cat.grph_domain_type ");
            selectQueryBuff.append(" AND cat.domain_code= dom.domain_code  ");
            return  selectQueryBuff.toString();
	}
	
	@Override
	public String loadChannelUserDetailsByLoginIDANDORMSISDN(String p_msisdn,String  p_loginid){
		 final StringBuffer selectQueryBuff = new StringBuffer(
	                " SELECT u.user_id, u.password webpassword,u.user_name, u.network_code,u.login_id, u.category_code, u.parent_id, u.owner_id, u.msisdn,");
	            selectQueryBuff.append(" u.employee_code,u.status userstatus,u.created_by,u.created_on,u.modified_by,u.modified_on,");
	            selectQueryBuff.append(" cusers.contact_person,u.contact_no,u.designation,u.division,u.department,u.user_type,cusers.in_suspend,cusers.out_suspend,");
	            selectQueryBuff.append(" u.address1,u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,");
	            selectQueryBuff.append(" cat.domain_code,dom.domain_type_code,cat.sequence_no catseq,cat.sms_interface_allowed,geo.grph_domain_code,gdomains.status geostatus, ");
	            selectQueryBuff
	                .append(" uphones.user_phones_id,uphones.primary_number, uphones.sms_pin, uphones.pin_required, uphones.phone_profile, uphones.phone_language phlang,");
	            selectQueryBuff.append(" uphones.country phcountry, uphones.invalid_pin_count, uphones.last_transaction_status, uphones.last_transaction_on,");
	            selectQueryBuff
	                .append(" uphones.pin_modified_on,uphones.last_transfer_id, uphones.last_transfer_type,uphones.prefix_id,uphones.temp_transfer_id, uphones.first_invalid_pin_time, ");
	            selectQueryBuff
	                .append(" cat.agent_allowed,cat.hierarchy_allowed, cat.category_type,cat.category_name,cat.grph_domain_type,cusers.comm_profile_set_id,cusers.transfer_profile_id, tp.status tpstatus,cusers.user_grade,cset.status csetstatus, ");
	            selectQueryBuff
	                .append(" cset.language_1_message comprf_lang_1_msg,cset.language_2_message  comprf_lang_2_msg,cat.restricted_msisdns,gdt.sequence_no grphSeq, cat.transfertolistonly, cat.USER_ID_PREFIX, ");

	            // for Zebra and Tango by sanjeew date 06/07/07
	            selectQueryBuff
	                .append(" uphones.access_type, uphones.created_on, cusers.application_id, cusers.mpay_profile_id, cusers.user_profile_id, cusers.mcommerce_service_allow,cusers.low_bal_alert_allow, uphones.created_on userphone_created_on ");
	            // end of Zebra and Tango
	            // added for loading PIN reset info
	            selectQueryBuff.append(" ,uphones.PIN_RESET,uphones.last_access_on, u.from_time,u.to_time,u.allowed_days,u.invalid_password_count,u.pswd_modified_on  ");
	            selectQueryBuff.append(" FROM  channel_users cusers RIGHT OUTER JOIN users u ON cusers.user_id=u.user_id ");
	            selectQueryBuff.append(" LEFT OUTER JOIN transfer_profile tp ON cusers.transfer_profile_id=tp.profile_id ");
	            selectQueryBuff.append(" LEFT OUTER JOIN commission_profile_set cset ON cusers.comm_profile_set_id=cset.comm_profile_set_id,user_geographies geo,categories cat,domains dom,user_phones uphones,geographical_domains gdomains,geographical_domain_types gdt ");
	            selectQueryBuff.append(" WHERE  u.status <> ? AND u.status <> ? ");
	            // modifed by harsh
	            if (!BTSLUtil.isNullString(p_msisdn) && !BTSLUtil.isNullString(p_loginid)) {
	                selectQueryBuff.append(" AND  uphones.msisdn=? AND UPPER(u.login_id)=UPPER(?)");
	            } else if (!BTSLUtil.isNullString(p_msisdn)) {
	                selectQueryBuff.append(" AND  uphones.msisdn=?");
	            } else if (!BTSLUtil.isNullString(p_loginid)) {
	                selectQueryBuff.append(" AND UPPER(u.login_id)=UPPER(?)");
	            }
	            // end modified by
	            selectQueryBuff
	                .append(" AND uphones.user_id=u.user_id AND u.category_code = cat.category_code AND u.user_id=geo.user_id AND geo.grph_domain_code=gdomains.grph_domain_code ");
	            selectQueryBuff
	                .append(" AND cat.domain_code= dom.domain_code   AND gdt.grph_domain_type=gdomains.grph_domain_type ");
	            return selectQueryBuff.toString();

	}
	
	@Override
	public String  loadUsersForAdditionalDetail(String[] m_geographicalDomainCode){
		StringBuilder strBuff =new StringBuilder();
		 strBuff.append("SELECT U.user_name,U.user_id,U.msisdn,U.login_id,U.category_code,C.category_name,U.status,UG.grph_domain_code ");
	        strBuff.append("FROM users U,channel_users CU,categories C,user_geographies UG ");
	        strBuff.append("WHERE U.network_code=? AND U.user_id=CU.user_id AND U.user_id=UG.user_id  AND ");
	        strBuff.append("U.category_code=C.category_code  ");
	        strBuff.append("AND U.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
	        strBuff.append("AND C.domain_code =? AND U.status <> 'N' AND U.status <> 'C' AND C.status='Y' ");
	        strBuff.append("AND UG.grph_domain_code IN (WITH RECURSIVE Q AS (SELECT grph_domain_code, status FROM geographical_domains GD1 ");
	        strBuff.append("WHERE   ");
	        strBuff.append(" grph_domain_code IN(");
	        for (int i = 0; i < m_geographicalDomainCode.length; i++) {
	            strBuff.append(" ?");
	            if (i != m_geographicalDomainCode.length - 1) {
	                strBuff.append(",");
	            }
	        }
	        strBuff.append(") UNION ALL SELECT M.grph_domain_code, m.status FROM geographical_domains M join Q ON Q.grph_domain_code = M.parent_grph_domain_code )SELECT grph_domain_code FROM Q where status = 'Y')");
	        strBuff.append("ORDER BY C.sequence_no,U.login_id");

	        return strBuff.toString();

	}
	
	@Override
	public String loadUsersForBulkAutoC2C(String[] m_categoryCode,String[] m_geographicalDomainCode)
	{
		StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT DISTINCT U.user_id,U.user_code,U.msisdn,U.login_id,U.category_code,C.category_name,CG.grade_code,U.status,");
        strBuff.append("CG.grade_name,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend,CU.auto_c2c_allow,CU.auto_c2c_quantity,U.external_code, ");
        strBuff.append("CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg, CU.sos_allowed , CU.sos_allowed_amount , CU.sos_threshold_limit, CU.lr_allowed,CU.lr_max_amount ");
        strBuff.append("FROM users U,channel_users CU,channel_grades CG,categories C,user_geographies UG, ");
        strBuff.append("commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append("WHERE U.network_code=? AND U.user_id=CU.user_id AND U.user_id=UG.user_id AND ");
        strBuff.append("U.category_code=C.category_code AND U.category_code=CG.category_code AND CU.user_grade=CG.grade_code ");
        strBuff.append(" AND U.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
        strBuff.append("AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append("AND TP.profile_id = CU.transfer_profile_id AND C.category_code IN (");
        for (int i = 0; i < m_categoryCode.length; i++) {
            strBuff.append(" ?");
            if (i != m_categoryCode.length - 1) {
                strBuff.append(",");
            }
        }
        strBuff.append(")");
        strBuff.append("AND C.domain_code =? AND U.status <> 'N' AND U.status <> 'C' AND C.status='Y' ");
        strBuff.append("AND UG.grph_domain_code IN (WITH RECURSIVE Q AS (SELECT grph_domain_code, status FROM geographical_domains GD1 ");
        strBuff.append(" where grph_domain_code IN(");
        // akanksha5
        for (int i = 0; i < m_geographicalDomainCode.length; i++) {
            strBuff.append(" ?");
            if (i != m_geographicalDomainCode.length - 1) {
                strBuff.append(",");
            }
        }
        strBuff.append(") UNION ALL SELECT m.grph_domain_code, m.status FROM geographical_domains M join Q ON Q.grph_domain_code = M.parent_grph_domain_code )SELECT grph_domain_code FROM Q  where  status = 'Y' )");
        strBuff.append("AND CPSV.applicable_from =COALESCE ( (SELECT MAX(applicable_from) FROM ");
        strBuff.append("commission_profile_set_version WHERE applicable_from <= ? AND ");
        strBuff.append("comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from) ");

        return strBuff.toString();
	}
	
	@Override
	public PreparedStatement loadCategoryUsersWithinGeoDomainHirearchyForWithdraw(Connection con,String p_networkCode,String p_categoryCode,String p_geographicalDomainCode,String p_userName,String p_loginUserID,String p_ownerUserID,String statusAllowed,String senderStatusAllowed ) throws SQLException{
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT DISTINCT U.user_id, U.user_name FROM users U, user_geographies UG, categories CAT ");
        strBuff.append(" WHERE U.network_code = ? AND U.category_code = ?  ");
        if (p_ownerUserID == null) {
            strBuff.append(" AND u.status IN (" + statusAllowed + ") ");
        } else {
            // loading child users
            strBuff.append(" AND u.status IN (" + senderStatusAllowed + ") ");
        }
        strBuff.append(" AND u.user_type = 'CHANNEL' ");
        strBuff.append(" AND U.category_code = CAT.category_code AND U.user_id=UG.user_id ");

        strBuff.append(" AND UG.grph_domain_code IN (WITH RECURSIVE Q AS (SELECT grph_domain_code, status FROM geographical_domains GD1 ");
        strBuff.append(" where grph_domain_code IN (SELECT grph_domain_code ");
        strBuff.append(" FROM user_geographies UG1 ");
        strBuff.append(" WHERE UG1.grph_domain_code = ? ");
        strBuff.append(" AND UG1.user_id = ? ) UNION ALL SELECT M.grph_domain_code, m.status FROM geographical_domains M JOIN Q ON Q.grph_domain_code = M.parent_grph_domain_code )SELECT grph_domain_code FROM Q where  status = ?)");
        strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?) ");
        if (p_ownerUserID != null && !"NA".equalsIgnoreCase(p_ownerUserID)) {

            strBuff.append(" AND  u.owner_id = ?  ");
        }
        strBuff.append(" ORDER BY U.user_name");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadCategoryUsersWithinGeoDomainHirearchyForWithdraw", "QUERY sqlSelect=" + sqlSelect);
        }

        
        PreparedStatement pstmt = con.prepareStatement(sqlSelect);
        int i = 0;
        ++i;
        pstmt.setString(i, p_networkCode);
        ++i;
        pstmt.setString(i, p_categoryCode);
        ++i;
        pstmt.setString(i, p_geographicalDomainCode);
        ++i;
        pstmt.setString(i, p_loginUserID);
        ++i;
        pstmt.setString(i, PretupsI.GEO_DOMAIN_STATUS);
        ++i;
        pstmt.setString(i, p_userName);
        
        if (p_ownerUserID != null && !"NA".equalsIgnoreCase(p_ownerUserID)) {
            ++i;
            pstmt.setString(i, p_ownerUserID);
        }

        return pstmt;
	}
	
	
	@Override
	public String isControlledProfileAlreadyAssociated(){
		 final StringBuffer selectQueryBuff = new StringBuffer("SELECT u.user_id,cu.lms_profile, ps.promotion_type ");
	        selectQueryBuff.append("  FROM users u, channel_users cu, profile_set_version psv, profile_set ps ");
	        selectQueryBuff.append("  WHERE u.user_id = cu.user_id ");
	        selectQueryBuff.append("  AND u.status IN ('Y', 'S') ");
	        selectQueryBuff.append("  AND cu.lms_profile is not null ");
	        selectQueryBuff.append("  AND ps.set_id = psv.set_id ");
	        selectQueryBuff.append("  AND cu.lms_profile=ps.set_id ");
	        selectQueryBuff.append("  AND psv.status IN ('Y', 'S') ");
	        selectQueryBuff.append("  AND ps.promotion_type = ? ");
	        selectQueryBuff.append("  AND date_trunc('day',psv.applicable_to::TIMESTAMP) >=date_trunc('day',CURRENT_TIMESTAMP::TIMESTAMP) ");
	        selectQueryBuff.append("  AND u.msisdn = ? ");

	        return selectQueryBuff.toString();

	}

	
	@Override
	public PreparedStatement loadUserListOnZoneDomainCategoryWithMSISDN(Connection con,String fromUserID,String userName,String domainCode,String userCategory,String zoneCode,String pLOGinuserID  ) throws SQLException{
		StringBuffer strBuff = new StringBuffer(" SELECT  U.MSISDN , U.user_name");
       	strBuff.append(" FROM users U, user_geographies UG, categories CAT ");
    	strBuff.append(" WHERE U.category_code = (CASE WHEN ?= '"+PretupsI.ALL+"' THEN  U.category_code ELSE ? END) ");
    	strBuff.append(" AND CAT.domain_code IN (SELECT UD.domain_code FROM user_domains UD WHERE UD.domain_code = (case ?  when '"+PretupsI.ALL+"' then UD.domain_code else ? end) AND UD.user_id = ?)");
    	strBuff.append(" AND CAT.category_code = U.category_code ");
    	strBuff.append(" AND U.user_type = '"+PretupsI.CHANNEL_USER_TYPE+"' ");
    	strBuff.append(" AND U.status NOT IN ('N','C','W') ");
    	strBuff.append(" AND U.user_id = UG.user_id ");
      	strBuff.append(" AND UG.grph_domain_code IN ( with recursive q as (");
      	strBuff.append(" SELECT grph_domain_code, status FROM geographical_domains GD1 WHERE  ");
    	strBuff.append("   grph_domain_code IN (SELECT grph_domain_code ");
    	strBuff.append(" FROM user_geographies UG1 ");
    	strBuff.append(" WHERE	UG1.grph_domain_code = CASE WHEN ?= '"+PretupsI.ALL+"' THEN UG1.grph_domain_code ELSE ? END ");
    	strBuff.append(" AND UG1.user_id= ? ) union all select M.grph_domain_code , m.status FROM geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code   )select grph_domain_code from q WHERE status IN(?, ?) )");
    	if(!BTSLUtil.isNullString(fromUserID))//for from userid
    	    strBuff.append(" AND U.user_id <> ?");
    	if(!BTSLUtil.isNullString(userName))
    	    strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?) ");
		strBuff.append(" ORDER BY U.user_name");
		
    	String sqlSelect = strBuff.toString();
    	
    	if (LOG.isDebugEnabled())
    	    LOG.debug("loadUserListOnZoneDomainCategoryWithMSISDN", "QUERY sqlSelect=" + sqlSelect);
    	
		PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);
	    int i = 0;
	    pstmtSelect.setString(++i, userCategory);
	    pstmtSelect.setString(++i, userCategory);
	    pstmtSelect.setString(++i, domainCode);
	    pstmtSelect.setString(++i, domainCode);
	    pstmtSelect.setString(++i, pLOGinuserID);
	    pstmtSelect.setString(++i, zoneCode);
	    pstmtSelect.setString(++i, zoneCode);
	    pstmtSelect.setString(++i, pLOGinuserID);
	    pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
	    pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);

	    if(!BTSLUtil.isNullString(fromUserID))//for from userid
	        pstmtSelect.setString(++i, fromUserID);
	    if(!BTSLUtil.isNullString(userName))
	    {
	        pstmtSelect.setString(++i, userName+"%");
	    }
		return pstmtSelect;
	}


			  @Override
				public PreparedStatement loadChannelUserListHierarchyQry(Connection con,String p_domainCode,String p_userCategory,String p_userName,String p_userID,String p_zoneCode) throws SQLException{
					 final StringBuffer strBuff = new StringBuffer();
				
					  strBuff.append(" WITH RECURSIVE q AS( ");
					  strBuff.append("SELECT DISTINCT U.user_id,UG.user_id UG_user_id,U.user_name,UG.grph_domain_code, CAT.domain_code,U.category_code,");
					  strBuff.append("CAT.category_code CAT_category_code ,U.status,U.user_type , U.owner_id,OU.user_id OU_user_id, OU.user_name owner_name ,U.parent_id,U.login_id  "); 
					  strBuff.append(" FROM users U,users OU, user_geographies UG, categories CAT  ");
					  strBuff.append(" where U.user_id= ?  ");
					  strBuff.append("union all  ");
					  strBuff.append(" SELECT DISTINCT U.user_id,UG.user_id UG_user_id,U.user_name,UG.grph_domain_code, CAT.domain_code,U.category_code,");
					  strBuff.append("CAT.category_code CAT_category_code , U.status,U.user_type , U.owner_id,OU.user_id OU_user_id,OU.user_name owner_name,U.parent_id,U.login_id  "); 
					  strBuff.append(" FROM users U join q on q.user_id=U.parent_id ,users OU, user_geographies UG, categories CAT   ");
					  strBuff.append(")	SELECT DISTINCT user_id, user_name,owner_id ,parent_id,login_id from q  ");
			        		 strBuff.append(" WHERE owner_id=OU_user_id AND category_code = ?  AND status='Y' AND user_type='CHANNEL' ");
					        strBuff.append(" AND category_code = CAT_category_code AND domain_code = ? AND user_id = UG_user_id ");
					        strBuff.append(" AND grph_domain_code IN (");
					    
					        strBuff.append("  WITH RECURSIVE q1 AS( ");
					        strBuff.append(" SELECT grph_domain_code, status FROM geographical_domains GD1 WHERE "); 
					        strBuff.append(" grph_domain_code IN (SELECT grph_domain_code ");
					         strBuff.append(" FROM user_geographies UG1 WHERE UG1.grph_domain_code = ? ");
					        	  strBuff.append(" AND UG1.user_id= ? )");
									 strBuff.append("  union all ");
					        strBuff.append("  SELECT gd1.grph_domain_code, gd1.status FROM geographical_domains GD1 join q1 on  ");
					        strBuff.append("  q1.grph_domain_code = gd1.parent_grph_domain_code ");
					        strBuff.append(" )SELECT grph_domain_code FROM q1 WHERE status IN(?, ?) ");
					        
					        strBuff.append( ") AND UPPER(user_name) LIKE UPPER(?) ");
					
				       

				        final String sqlSelect = strBuff.toString();
				        if (LOG.isDebugEnabled()) {
				            LOG.debug("loadChannelUserListHierarchyQry", "QUERY sqlSelect=" + sqlSelect);
				        }
				        PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);
			            int i = 0;
			            ++i;
			            pstmtSelect.setString(i, p_userID);
			            ++i;
			            pstmtSelect.setString(i, p_userCategory);
			            ++i;
			            pstmtSelect.setString(i, p_domainCode);
			            ++i;
			            pstmtSelect.setString(i, p_zoneCode);
			            ++i;
			            pstmtSelect.setString(i, p_userID);
			            ++i;
			            pstmtSelect.setString(i, PretupsI.STATUS_ACTIVE);
			            ++i;
			            pstmtSelect.setString(i, PretupsI.STATUS_SUSPEND);
			            ++i;
			            pstmtSelect.setString(i, p_userName);			            
			         
			            return pstmtSelect;
				}
			  
	@Override//to test
	public PreparedStatement loadTransferredUserPrevHierarchyQry(Connection p_con,String p_mode,String[] p_userId,boolean p_isSearchOnDate,Date p_fromDate,Date p_toDate)throws SQLException{
		
		StringBuilder strBuff = new StringBuilder() ;
		 strBuff.append("SELECT distinct level as l,user_id_prefix,U.user_id, U.MODIFIED_ON,U.user_name, U.login_id,U.category_code, U.msisdn,  ");
			strBuff.append(" UP.user_name parent_name,U.reference_Id,L.lookup_name,COALESCE(UB.prev_balance,0)prev_balance,  ");
			 strBuff.append("OLD_USR.user_id prev_user_id, OLD_USR.user_name prev_user_name,OLD_PUSR.user_name prev_parent_name, ");  
			 strBuff.append(" OLD_USR.category_code prev_cat_code  ");
			strBuff.append(" FROM  USERS OLD_USR left outer join USERS OLD_PUSR  on (OLD_USR.parent_id = OLD_PUSR.user_id ");
			if (p_isSearchOnDate) {
				strBuff.append(" AND date_trunc('day',OLD_USR.modified_on::TIMESTAMP)>=? AND date_trunc('day',OLD_USR.modified_on::TIMESTAMP)<=? ");
			}
			strBuff.append(" ) ");
			strBuff.append(" left outer join USER_BALANCES UB on OLD_USR.user_id  = UB.user_id ,");
			strBuff.append(" ( WITH RECURSIVE Q As (SELECT 1 as level ,USR.* ,USR.category_code catcode, UCAT.category_code  UCAT_category_code,  UCAT.user_id_prefix  ");
			strBuff.append("  FROM USERS USR, CATEGORIES UCAT WHERE ");
			  if (p_mode.equalsIgnoreCase(PretupsI.SINGLE) || p_mode.equalsIgnoreCase(PretupsI.ALL)) {
		    	  strBuff.append("   user_id=? ");
			  } else if (p_mode.equalsIgnoreCase(PretupsI.MULTIPLE)) {
		          final StringBuilder str = new StringBuilder();
		           for (int k = 0; k < p_userId.length; k++) {
		              str.append("' ");
		               str.append(p_userId[k]);
		               str.append("', ");
		           }
		           final String userID = str.substring(0, str.length() - 1);
		           strBuff.append("   user_id in (" + userID + ") ");
		      }
			  strBuff.append( "UNION ALL SELECT  Q.level+1,USR.* ,USR.category_code catcode, UCAT.category_code  UCAT_category_code,UCAT.user_id_prefix  ");
	    	  strBuff.append(" FROM USERS USR join Q on Q.user_id = USR.parent_id, CATEGORIES UCAT   ");
	    	  strBuff.append(" )select * from Q where  UCAT_category_code=catcode and user_type='CHANNEL' ) U,  ");
           strBuff.append(" LOOKUPS L, USERS UP  WHERE  L.lookup_code = U.status  AND L.lookup_type='URTYP'   AND L.lookup_code<>'C'  AND U.parent_id=UP.user_id AND OLD_USR.user_id=U.reference_id ");
           String sqlSelect=strBuff.toString();
           if (LOG.isDebugEnabled()) {
                LOG.debug("loadTransferredUserPrevHierarchy ", "QUERY sqlSelect=" + sqlSelect);
            }
           PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);
                 int i =1;
            if (p_isSearchOnDate ) {
            	  pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                  pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            } 
            if (p_mode.equalsIgnoreCase(PretupsI.SINGLE) || p_mode.equalsIgnoreCase(PretupsI.ALL)) {
                pstmt.setString(i, p_userId[0]);
            }
		      return pstmt;
			
	}
	
	
	@Override
	public String validateUsersForBatchFOC(String[] m_categoryCode,String[] m_receiverStatusAllowed,String [] m_geographicalDomainCode){
		StringBuilder strBuff=new StringBuilder();
		strBuff.append("SELECT U.user_id,CPSV.dual_comm_type,U.user_code,U.msisdn,U.login_id,U.category_code,C.category_name,CG.grade_code,U.status,");
        strBuff.append("CG.grade_name,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend,U.external_code, ");
        strBuff.append("CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
        strBuff.append("FROM users U,channel_users CU,channel_grades CG,categories C,user_geographies UG, ");
        strBuff.append("commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append("WHERE U.user_code=? AND U.network_code=? AND U.user_id=CU.user_id AND U.user_id=UG.user_id AND ");
        strBuff.append("U.category_code=C.category_code AND U.category_code=CG.category_code AND CU.user_grade=CG.grade_code ");
        strBuff.append("AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append("AND TP.profile_id = CU.transfer_profile_id AND C.category_code IN (");
        for (int i = 0; i < m_categoryCode.length; i++) {
            strBuff.append(" ?");
            if (i != m_categoryCode.length - 1) {
                strBuff.append(",");
            }
        }
        strBuff.append(")");
        strBuff.append("AND C.domain_code =? AND U.status <> 'N' AND U.status IN (");
        for (int i = 0; i < m_receiverStatusAllowed.length; i++) {
            strBuff.append(" ?");
            if (i != m_receiverStatusAllowed.length - 1) {
                strBuff.append(",");
            }
        }
        strBuff.append(")");
        strBuff.append("AND UG.grph_domain_code IN (with recursive q as (SELECT grph_domain_code , status FROM geographical_domains GD1 ");
        strBuff.append("WHERE ");
        strBuff.append(" grph_domain_code IN(");
        // akanksha5
        for (int i = 0; i < m_geographicalDomainCode.length; i++) {
            strBuff.append(" ?");
            if (i != m_geographicalDomainCode.length - 1) {
                strBuff.append(",");
            }
        }
        strBuff.append(") union all select m.grph_domain_code, m.status FROM geographical_domains m join q on q.grph_domain_code = m.parent_grph_domain_code )select grph_domain_code from q  WHERE status = 'Y')");
        strBuff.append("AND CPSV.applicable_from =coalesce ( (SELECT MAX(applicable_from) FROM ");
        strBuff.append("commission_profile_set_version WHERE applicable_from <= ? AND ");
        strBuff.append("comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from) ");
        strBuff.append("ORDER BY C.sequence_no,CU.user_grade,U.login_id");
        return strBuff.toString();
	}
	
	
	@Override
	public String loadUsersForBatchDP(String[] m_categoryCode,String [] m_geographicalDomainCode){
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT U.user_id,U.user_code,U.msisdn,U.login_id,U.category_code,C.category_name,CG.grade_code,U.status,");
        strBuff.append("CG.grade_name,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend,U.external_code, ");
        strBuff.append("CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
        strBuff.append("FROM users U,channel_users CU,channel_grades CG,categories C,user_geographies UG, ");
        strBuff.append("commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append("WHERE U.network_code=? AND U.user_id=CU.user_id AND U.user_id=UG.user_id AND ");
        strBuff.append("U.category_code=C.category_code AND U.category_code=CG.category_code AND CU.user_grade=CG.grade_code ");
        strBuff.append(" AND U.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
        strBuff.append("AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append("AND TP.profile_id = CU.transfer_profile_id AND C.category_code IN (");

        for (int i = 0; i < m_categoryCode.length; i++) {
            strBuff.append(" ?");
            if (i != m_categoryCode.length - 1) {
                strBuff.append(",");
            }
        }
        strBuff.append(")");
        strBuff.append("AND C.domain_code =? AND U.status <> 'N' AND U.status <> 'C' AND C.status='Y' ");
        strBuff.append("AND UG.grph_domain_code IN (WITH RECURSIVE Q As (SELECT grph_domain_code, status FROM geographical_domains GD1 ");
        strBuff.append("WHERE  grph_domain_code IN(");

        for (int i = 0; i < m_geographicalDomainCode.length; i++) {
            strBuff.append(" ?");
            if (i != m_geographicalDomainCode.length - 1) {
                strBuff.append(",");
            }
        }
        strBuff.append(")");
        strBuff.append("union all select m.grph_domain_code, m.status FROM geographical_domains m join Q on Q.grph_domain_code = m.parent_grph_domain_code  )select grph_domain_code from Q WHERE status = 'Y' )");
        strBuff.append("AND CPSV.applicable_from =coalesce ( (SELECT MAX(applicable_from) FROM ");
        strBuff.append("commission_profile_set_version WHERE applicable_from <= ? AND ");
        strBuff.append("comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from) ");
        strBuff.append("ORDER BY C.sequence_no,CU.user_grade,U.login_id");
		return strBuff.toString();
	}
	
	@Override
	public String loadUsersForBatchDP(String[] m_categoryCode,String [] m_geographicalDomainCode, String p_productCode){
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT U.user_id,U.user_code,U.msisdn,U.login_id,U.category_code,C.category_name,CG.grade_code,U.status,");
        strBuff.append("CG.grade_name,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend,U.external_code, ");
        strBuff.append("CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
        strBuff.append("FROM users U,channel_users CU,channel_grades CG,categories C,user_geographies UG,USER_BALANCES ub ");
        strBuff.append("commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append("WHERE U.network_code=? AND U.user_id=CU.user_id AND U.user_id=UG.user_id AND  U.user_id=ub.user_id AND ");
        strBuff.append("U.category_code=C.category_code AND U.category_code=CG.category_code AND CU.user_grade=CG.grade_code ");
        strBuff.append(" AND U.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
        strBuff.append("AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append("AND TP.profile_id = CU.transfer_profile_id AND C.category_code IN (");

        for (int i = 0; i < m_categoryCode.length; i++) {
            strBuff.append(" ?");
            if (i != m_categoryCode.length - 1) {
                strBuff.append(",");
            }
        }
        strBuff.append(")");
        strBuff.append("AND C.domain_code =? AND U.status <> 'N' AND U.status <> 'C' AND C.status='Y' ");
        strBuff.append("AND UG.grph_domain_code IN (WITH RECURSIVE Q As (SELECT grph_domain_code, status FROM geographical_domains GD1 ");
        strBuff.append("WHERE  grph_domain_code IN(");

        for (int i = 0; i < m_geographicalDomainCode.length; i++) {
            strBuff.append(" ?");
            if (i != m_geographicalDomainCode.length - 1) {
                strBuff.append(",");
            }
        }
        strBuff.append(")");
        strBuff.append("union all select m.grph_domain_code, m.status FROM geographical_domains m join Q on Q.grph_domain_code = m.parent_grph_domain_code  )select grph_domain_code from Q WHERE status = 'Y' )");
        strBuff.append("AND CPSV.applicable_from =coalesce ( (SELECT MAX(applicable_from) FROM ");
        strBuff.append("commission_profile_set_version WHERE applicable_from <= ? AND ");
        strBuff.append("comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from) ");
        if(!BTSLUtil.isNullString(p_productCode))
        {
       	 strBuff.append(" AND ub.product_code=? ");
        }
        strBuff.append("ORDER BY C.sequence_no,CU.user_grade,U.login_id");
		return strBuff.toString();
	}
	
	
	@Override
	public PreparedStatement loadUsersDetailsForStaff(Connection con,String p_status,String p_statusUsed,String p_userID,String p_msisdn)throws SQLException{
		StringBuilder strBuff=new StringBuilder();
		strBuff.append(" SELECT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
        strBuff.append("USR.login_id,USR.password password1,USR.category_code usr_category_code,USR.parent_id,USR.reference_id, ");
        strBuff.append("USR.owner_id,USR.allowed_ip,USR.allowed_days, D.domain_name, ");
        strBuff.append("USR.from_time,USR.to_time,USR.employee_code,USR.company,USR.fax,USR.firstname,USR.lastname, ");// firstname,lastname,company,fax
       strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,");
        strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
        strBuff.append("USR.created_by, USR_CRBY.user_name created_by_name, USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
        strBuff.append("USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
        strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
        strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date, ");
        strBuff.append("USR.previous_status,USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name, USR_CAT.max_txn_msisdn, ");
        strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type,MOD_USR.user_name request_user_name, USR_CAT.low_bal_alert_allow, ");
        strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, USR_CAT.services_allowed,USR_CAT.transfertolistonly, ");
        strBuff.append("USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed,USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed, USR_CAT.restricted_msisdns, ");
        strBuff.append("PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn, ");
        strBuff.append("PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn,USR_CAT.category_type, ");
        strBuff.append("ONR_CAT.category_name owner_cat,USR_PHONE.sms_pin user_sms_pin, USR_PHONE.pin_required required,l.lookup_name, D.domain_type_code, ");

        // for Zebra and Tango by sanjeew date 18/07/07
        strBuff.append(" USR_PHONE.access_type user_access_type ");
        // end of Zebra and Tango
        strBuff.append(",UG.grph_domain_code, GD.grph_domain_name  ");
		strBuff.append("FROM users USR left outer join (categories  PRNT_CAT right outer join users PRNT_USR on PRNT_CAT.category_code=PRNT_USR.category_code) ");
		strBuff.append("on USR.parent_id=PRNT_USR.user_id ");
		strBuff.append("left outer join users MOD_USR on USR.modified_by=MOD_USR.user_id ");
		strBuff.append("left outer join users USR_CRBY on USR.created_by=USR_CRBY.user_id, ");
		
        strBuff.append(" user_phones USR_PHONE,users ONR_USR,categories USR_CAT,  ");
        strBuff.append("categories ONR_CAT,lookups l, domains D, user_geographies UG, geographical_domains GD ");
        strBuff.append( "WHERE USR_PHONE.msisdn=? AND USR_PHONE.user_id=USR.user_id ");

        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append(" AND USR.status IN (" + p_status + ") ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append(" AND USR.status NOT IN (" + p_status + ") ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append(" AND USR.status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append(" AND USR.status <> ? ");
        }

        strBuff.append(" AND USR.owner_id=ONR_USR.user_id ");
        strBuff.append("AND USR.category_code=USR_CAT.category_code ");
        strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
       
        strBuff.append(" AND USR.status = l.lookup_code ");
        strBuff.append(" AND l.lookup_type= ? ");
        strBuff.append(" AND USR.user_id = UG.user_id ");
        strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
     
        strBuff.append(" AND USR_CAT.domain_code=D.domain_code ");
        strBuff.append(" AND USR.user_type='STAFF' ");
        if (p_userID != null) {
            strBuff.append(" AND USR.user_id IN ( ");
             strBuff.append(" with recursive q as (SELECT user_id from users where  user_id=? ");
            strBuff.append(" union all SELECT m.user_id from users m join q on q.user_id = m.parent_id ) select user_id from q  where user_id != ?) ");
      }
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadUsersDetailsForStaff", "QUERY sqlSelect=" + sqlSelect);
        }
        
        PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);
        int i = 1;
        pstmtSelect.setString(i, p_msisdn);
        i++;
        if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            pstmtSelect.setString(i, p_status);
            i++;
        }
        pstmtSelect.setString(i, PretupsI.USER_STATUS_TYPE);
        i++;
        if (p_userID != null) {
            pstmtSelect.setString(i, p_userID);
            i++;
            pstmtSelect.setString(i, p_userID);
        }
        return pstmtSelect;
	}
	
	@Override
	public PreparedStatement loadUsersDetailsByLoginIdForStaff(Connection con,String p_status,String p_statusUsed,String p_userID,String p_loginId)throws SQLException{
		StringBuilder strBuff=new StringBuilder();
		strBuff.append(" SELECT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
        strBuff.append("USR.login_id,USR.password as passwd,USR.category_code usr_category_code,USR.parent_id,USR.reference_id, ");
        strBuff.append("USR.owner_id,USR.allowed_ip,USR.allowed_days,");
        strBuff.append("USR.from_time,USR.to_time,USR.employee_code,USR.company,USR.fax,USR.firstname,USR.lastname,");// firstname,lastname,company,fax
        strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,");
        strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
        strBuff.append("USR.created_by,USR_CRBY.user_name created_by_name, USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
        strBuff.append("USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
        strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
        strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date, ");
        strBuff.append("USR.previous_status,USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name, USR_CAT.max_txn_msisdn, ");
        strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type, USR_CAT.transfertolistonly, USR_CAT.low_bal_alert_allow, ");
        strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, USR_CAT.services_allowed, ");
        strBuff
            .append("USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed, USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed,USR_CAT.restricted_msisdns, ");
        strBuff.append("PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn,MOD_USR.user_name request_user_name, ");
        strBuff.append("PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn, USR_CAT.category_type,");
        strBuff.append("ONR_CAT.category_name owner_cat,l.lookup_name, D.domain_type_code,UG.grph_domain_code, GD.grph_domain_name ");
		
		 strBuff.append("FROM users USR left outer join (categories  PRNT_CAT right outer join users PRNT_USR on PRNT_CAT.category_code=PRNT_USR.category_code) ");
		strBuff.append("on USR.parent_id=PRNT_USR.user_id ");
		strBuff.append("left outer join users MOD_USR on USR.modified_by=MOD_USR.user_id ");
		strBuff.append("left outer join users USR_CRBY on USR.created_by=USR_CRBY.user_id, ");
        strBuff.append("users ONR_USR,categories USR_CAT,categories ONR_CAT,");
        strBuff.append("lookups l, domains D,user_geographies UG, geographical_domains GD ");
        strBuff.append("WHERE UPPER(USR.login_id)=UPPER(?) ");

        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append(" AND USR.status IN (" + p_status + ") ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append(" AND USR.status NOT IN (" + p_status + ") ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append(" AND USR.status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append(" AND USR.status <> ? ");
        }

        strBuff.append("  AND USR.owner_id=ONR_USR.user_id ");
        strBuff.append(" AND USR.category_code=USR_CAT.category_code ");
        strBuff.append(" AND ONR_CAT.category_code=ONR_USR.category_code ");

        strBuff.append(" AND USR.status = l.lookup_code ");
        strBuff.append(" AND l.lookup_type= ? ");
        strBuff.append(" AND USR.user_id = UG.user_id ");
        strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
     
        strBuff.append(" AND USR_CAT.domain_code=D.domain_code ");
        strBuff.append(" AND USR.user_type= 'STAFF' ");
        if (p_userID != null) {
            strBuff.append(" AND USR.user_id IN ( ");
           strBuff.append(" with recursive q as (SELECT user_id from users where  user_id=? ");
            strBuff.append(" union all SELECT m.user_id from users m join q on q.user_id = m.parent_id  ) select user_id from q where user_id != ?  ) ");
       }
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadUsersDetailsByLoginIdForStaff", "QUERY sqlSelect=" + sqlSelect);
        }
        PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);
        int i = 1;
        pstmtSelect.setString(i, p_loginId);
        i++;
        if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            pstmtSelect.setString(i, p_status);
            i++;
        }
        pstmtSelect.setString(i, PretupsI.USER_STATUS_TYPE);
        i++;
        if (p_userID != null) {
            pstmtSelect.setString(i, p_userID);
            i++;
            pstmtSelect.setString(i, p_userID);
        }

        return pstmtSelect;
	}
	
	
	@Override
	public String loadUsersDetailsForC2C(String p_status,String p_statusUsed){
		StringBuilder strBuff=new StringBuilder();
		strBuff.append(" SELECT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
        strBuff.append("USR.login_id,USR.password as passwd,USR.category_code usr_category_code,USR.parent_id,USR.reference_id, ");
        strBuff.append("USR.owner_id,USR.allowed_ip,USR.allowed_days, D.domain_name, ");
        strBuff.append("USR.from_time,USR.to_time,USR.employee_code,USR.company,USR.fax,USR.firstname,USR.lastname, ");// firstname,lastname,company,fax
     
        strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,");
        strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
        strBuff.append("USR.created_by, USR_CRBY.user_name created_by_name, USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
        strBuff.append("USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
        strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
        strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date, ");
        strBuff.append("USR.previous_status,USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name, USR_CAT.max_txn_msisdn, ");
        strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type,MOD_USR.user_name request_user_name, USR_CAT.low_bal_alert_allow, ");
        strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, USR_CAT.services_allowed,USR_CAT.transfertolistonly, ");
        strBuff.append("USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed,USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed, USR_CAT.restricted_msisdns, ");
        strBuff.append("PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn,UG.grph_domain_code, GD.grph_domain_name, ");
        strBuff.append("PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn,USR_CAT.category_type, ");
        strBuff.append("ONR_CAT.category_name owner_cat,USR_PHONE.sms_pin user_sms_pin, USR_PHONE.pin_required required,l.lookup_name, D.domain_type_code, ");

        // for Zebra and Tango by sanjeew date 18/07/07
        strBuff.append(" USR_PHONE.access_type user_access_type ");
        // end of Zebra and Tango

		 strBuff.append("FROM users USR left outer join (categories  PRNT_CAT right outer join users PRNT_USR on PRNT_CAT.category_code=PRNT_USR.category_code) ");
		strBuff.append("on USR.parent_id=PRNT_USR.user_id ");
		strBuff.append("left outer join users MOD_USR on USR.modified_by=MOD_USR.user_id ");
		strBuff.append("left outer join users USR_CRBY on USR.created_by=USR_CRBY.user_id, ");
		
        strBuff.append(" user_phones USR_PHONE,users ONR_USR, ");
        strBuff.append("categories ONR_CAT, lookups l,user_geographies UG, geographical_domains GD,  domains D, categories USR_CAT ");
        strBuff.append("WHERE USR_PHONE.msisdn=? AND USR_PHONE.user_id=USR.user_id ");

        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append(" AND USR.status IN (" + p_status + ") ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append(" AND USR.status NOT IN (" + p_status + ") ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append(" AND USR.status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append(" AND USR.status <> ? ");
        }

        strBuff.append("AND  USR.owner_id=ONR_USR.user_id ");
        strBuff.append("AND USR.category_code=USR_CAT.category_code ");
        strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
       
        strBuff.append(" AND USR.status = l.lookup_code ");
        strBuff.append(" AND l.lookup_type= ? ");
        strBuff.append(" AND USR.user_id = UG.user_id ");
        strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
       
        strBuff.append(" AND USR_CAT.domain_code=D.domain_code ");
        strBuff.append(" AND USR.user_id=? ");
        
        return strBuff.toString();
       
	}
	
	@Override
	public PreparedStatement loadUserDetailsByExtCode(Connection p_con,String p_status,String p_statusUsed,String p_userID,String p_extCode) throws SQLException{
		StringBuilder strBuff=new StringBuilder();
		strBuff.append(" SELECT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
        strBuff.append(" USR.login_id,USR.password as passwd,USR.category_code usr_category_code,USR.parent_id,USR.reference_id, ");
        strBuff.append(" USR.owner_id,USR.allowed_ip,USR.allowed_days, D.domain_name, ");
        strBuff.append(" USR.from_time,USR.to_time,USR.employee_code,USR.company,USR.fax,USR.firstname,USR.lastname, ");// added
      
        strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,");
        strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
        strBuff.append("USR.created_by, USR_CRBY.user_name created_by_name, USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
        strBuff.append("USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
        strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
        strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date, ");
        strBuff.append("USR.previous_status,USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name, USR_CAT.max_txn_msisdn, ");
        strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type,MOD_USR.user_name request_user_name, USR_CAT.low_bal_alert_allow, ");
        strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, USR_CAT.services_allowed,USR_CAT.transfertolistonly, ");
        strBuff.append("USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed,USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed, USR_CAT.restricted_msisdns, ");
        strBuff.append("PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn,UG.grph_domain_code, GD.grph_domain_name, ");
        strBuff.append("PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn,USR_CAT.category_type, ");
        strBuff.append("ONR_CAT.category_name owner_cat,USR_PHONE.sms_pin user_sms_pin, USR_PHONE.pin_required required,l.lookup_name, D.domain_type_code, ");
        strBuff.append(" USR_PHONE.access_type user_access_type, chnl_usr.activated_on activated_on,USR_CAT.AUTHENTICATION_TYPE,USR.AUTHENTICATION_ALLOWED ");
        
		  strBuff.append("FROM users USR left outer join (categories  PRNT_CAT right outer join users PRNT_USR on PRNT_CAT.category_code=PRNT_USR.category_code) ");
		strBuff.append("on USR.parent_id=PRNT_USR.user_id ");
		strBuff.append("left outer join users MOD_USR on USR.modified_by=MOD_USR.user_id ");
		strBuff.append("left outer join users USR_CRBY on USR.created_by=USR_CRBY.user_id, ");
		
		strBuff.append(" user_phones USR_PHONE,users ONR_USR,categories USR_CAT, CHANNEL_USERS chnl_usr, ");
        strBuff.append("categories ONR_CAT,lookups l,user_geographies UG, geographical_domains GD, domains D ");
        strBuff.append("WHERE USR.external_code=? AND USR_PHONE.user_id=USR.user_id ");
        // Added for bar for del by shashank
        if ((p_status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) || (p_status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE))) {
            strBuff.append(" AND USR.barred_deletion_batchid IS NULL");
        }
        // end
        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append(" AND USR.status IN (" + p_status + ") ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append(" AND USR.status NOT IN (" + p_status + ") ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append(" AND USR.status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append(" AND USR.status <> ? ");
        }

        strBuff.append(" AND USR.owner_id=ONR_USR.user_id ");
        strBuff.append("AND USR.category_code=USR_CAT.category_code ");
        strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
     
        strBuff.append(" AND USR.status = l.lookup_code ");
        strBuff.append(" AND l.lookup_type= ? ");
        strBuff.append(" AND USR.user_id = UG.user_id ");
        strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
      
        strBuff.append(" AND USR_CAT.domain_code=D.domain_code ");
        strBuff.append(" AND chnl_usr.user_id=USR.user_id ");
        if (p_userID != null) {
		    strBuff.append(" AND USR.user_id IN ( ");
            strBuff.append(" with recursive q as (SELECT user_id from users where  user_id=? ");
            strBuff.append(" union all SELECT m.user_id from users m join q on q.user_id = m.parent_id   ) select user_id from q   where user_id != ?) ");
        }
        
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadUserDetailsByExtCode", "QUERY sqlSelect=" + sqlSelect);
        }
        PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);
        int i = 1;
        pstmtSelect.setString(i, p_extCode);
        i++;
        if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            pstmtSelect.setString(i, p_status);
            i++;
        }
        pstmtSelect.setString(i, PretupsI.USER_STATUS_TYPE);
        i++;
        if (p_userID != null) {
            pstmtSelect.setString(i, p_userID);
            i++;
            pstmtSelect.setString(i, p_userID);
        }
        
        return pstmtSelect;
	}
	
	@Override
	public PreparedStatement loadSTKUsersDetails(Connection p_con,String p_status,String p_statusUsed,String p_userID,String p_msisdn)throws SQLException{
		StringBuilder strBuff=new StringBuilder();
		strBuff.append(" SELECT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
        strBuff.append("USR.login_id,USR.password as passwd,USR.category_code usr_category_code,USR.parent_id,USR.reference_id, ");
        strBuff.append("USR.owner_id,USR.allowed_ip,USR.allowed_days, D.domain_name, ");
        strBuff.append("USR.from_time,USR.to_time,USR.employee_code,");
        strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,");
        strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
        strBuff.append("USR.created_by, USR_CRBY.user_name created_by_name, USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
        strBuff.append("USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
        strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
        strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date, ");
        strBuff.append("USR.previous_status,USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name, USR_CAT.max_txn_msisdn, ");
        strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type,MOD_USR.user_name request_user_name, USR_CAT.low_bal_alert_allow, ");
        strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, USR_CAT.services_allowed,USR_CAT.transfertolistonly, ");
        strBuff.append("USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed,USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed, USR_CAT.restricted_msisdns, ");
        strBuff.append("PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn,UG.grph_domain_code, GD.grph_domain_name, ");
        strBuff.append("PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn,USR_CAT.category_type, ");
        
        strBuff.append("ONR_CAT.category_name owner_cat,USR_PHONE.sms_pin user_sms_pin, USR_PHONE.pin_required required,l.lookup_name, D.domain_type_code, ");
       strBuff.append(" USR_PHONE.access_type user_access_type ");
	   strBuff.append("FROM users USR left outer join (categories  PRNT_CAT right outer join users PRNT_USR on PRNT_CAT.category_code=PRNT_USR.category_code) ");
		strBuff.append("on USR.parent_id=PRNT_USR.user_id ");
		strBuff.append("left outer join users MOD_USR on USR.modified_by=MOD_USR.user_id ");
		strBuff.append("left outer join users USR_CRBY on USR.created_by=USR_CRBY.user_id ");
	   
        strBuff.append(", user_phones USR_PHONE,users ONR_USR,categories USR_CAT, ");
        strBuff.append("categories ONR_CAT,lookups l,user_geographies UG, geographical_domains GD, domains D ");
        strBuff.append(" WHERE USR_PHONE.msisdn=? AND USR_PHONE.user_id=USR.user_id ");

        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append(" AND USR.status IN (" + p_status + ") ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append(" AND USR.status NOT IN (" + p_status + ") ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append(" AND USR.status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append(" AND USR.status <> ? ");
        }

        strBuff.append(" AND USR.owner_id=ONR_USR.user_id ");
        strBuff.append(" AND USR.category_code=USR_CAT.category_code ");
        strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
       
        strBuff.append(" AND USR.status = l.lookup_code ");
        strBuff.append(" AND l.lookup_type= ? ");
        strBuff.append(" AND USR.user_id = UG.user_id ");
        strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
       
        strBuff.append(" AND USR_CAT.domain_code=D.domain_code AND USR.creation_type='S' and USR.LEVEL1_APPROVED_BY is null and USR.LEVEL1_APPROVED_ON is null ");
        if (p_userID != null) {
            strBuff.append(" AND USR.user_id IN ( ");
            strBuff.append(" with recursive q as (SELECT user_id from users where user_id=? ");
            strBuff.append(" union all SELECT m.user_id from users m join q on q.user_id = m.parent_id    ) select user_id from q  where user_id != ?) ");
        }

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadSTKUsersDetails", "QUERY sqlSelect=" + sqlSelect);
        }
        PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);
        int i = 1;
        pstmtSelect.setString(i, p_msisdn);
        i++;
        if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            pstmtSelect.setString(i, p_status);
            i++;
        }
        pstmtSelect.setString(i, PretupsI.USER_STATUS_TYPE);
        i++;
        if (p_userID != null) {
            pstmtSelect.setString(i, p_userID);
            i++;
            pstmtSelect.setString(i, p_userID);
        }
        return pstmtSelect;
	}
	
	
	@Override
	public PreparedStatement loadSTKUsersDetailsByLoginId(Connection p_con,String p_status,String p_statusUsed,String p_userID,String p_loginId)throws SQLException{
		StringBuilder strBuff=new StringBuilder();
		strBuff.append("SELECT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
        strBuff.append("USR.login_id,USR.password as passwd,USR.category_code usr_category_code,USR.parent_id,USR.reference_id, ");
       strBuff.append("USR.owner_id,USR.allowed_ip,USR.allowed_days,");
       strBuff.append(" USR.from_time,USR.to_time,USR.employee_code,");
       strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,");
       strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
       strBuff.append("USR.created_by,USR_CRBY.user_name created_by_name, USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
       strBuff.append(" USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
       strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
       strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date, ");
       strBuff.append("USR.previous_status,USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name, USR_CAT.max_txn_msisdn, ");
       strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type, USR_CAT.transfertolistonly, USR_CAT.low_bal_alert_allow, ");
       strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, USR_CAT.services_allowed, ");
       strBuff.append(" USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed,"); 
       strBuff.append(" USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed,USR_CAT.restricted_msisdns, ");
       strBuff.append("PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn,MOD_USR.user_name request_user_name, ");
       strBuff.append("PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn, USR_CAT.category_type,");
       strBuff.append("ONR_CAT.category_name owner_cat,l.lookup_name, UG.grph_domain_code, GD.grph_domain_name, D.domain_type_code ");
       strBuff.append("FROM users USR left outer join (categories  PRNT_CAT right outer join users PRNT_USR on PRNT_CAT.category_code=PRNT_USR.category_code) ");
		strBuff.append("on USR.parent_id=PRNT_USR.user_id ");
		strBuff.append("left outer join users MOD_USR on USR.modified_by=MOD_USR.user_id ");
		strBuff.append("left outer join users USR_CRBY on USR.created_by=USR_CRBY.user_id,users ONR_USR,categories USR_CAT,categories ONR_CAT,");
       strBuff.append("lookups l,user_geographies UG, geographical_domains GD, domains D ");
       strBuff.append("WHERE UPPER(USR.login_id)=UPPER(?) ");

       if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
           strBuff.append(" AND USR.status IN (" + p_status + ") ");
       } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
           strBuff.append(" AND USR.status NOT IN (" + p_status + ")"); 
       } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
           strBuff.append(" AND USR.status =?");
       } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
           strBuff.append(" AND USR.status <> ? ");
       }

       strBuff.append(" AND USR.owner_id=ONR_USR.user_id ");
       strBuff.append(" AND USR.category_code=USR_CAT.category_code ");
       strBuff.append(" AND ONR_CAT.category_code=ONR_USR.category_code");
       
        strBuff.append("AND USR.status = l.lookup_code ");
       strBuff.append(" AND l.lookup_type= ? ");
       strBuff.append(" AND USR.user_id = UG.user_id ");
       strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");

       strBuff.append("AND USR_CAT.domain_code=D.domain_code ");
       if (p_userID != null) {
           strBuff.append(" AND USR.user_id IN ( ");
           strBuff.append(" with recursive q as (SELECT user_id from users where  user_id=? ");
           strBuff.append(" union all select u.user_id from users u join q on  q.user_id = u.parent_id  )select * from q  where user_id !=? ) ");
       }
       final String sqlSelect = strBuff.toString();
       if (LOG.isDebugEnabled()) {
           LOG.debug("loadSTKUsersDetailsByLoginId", "QUERY sqlSelect=" + sqlSelect);
       }
       PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);
       int i = 1;
       pstmtSelect.setString(i, p_loginId);
       i++;
       if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
           pstmtSelect.setString(i, p_status);
           i++;
       }
       pstmtSelect.setString(i, PretupsI.USER_STATUS_TYPE);
       i++;
       if (p_userID != null) {
           pstmtSelect.setString(i, p_userID);
           i++;
           pstmtSelect.setString(i, p_userID);
        
       }
       return pstmtSelect;
	}

	//to test
	@Override
	public PreparedStatement loadUserHierarchyListForTransfer(Connection con,String p_status,String p_statusUsed,String[] p_userId,String p_mode,String p_userCategory)throws SQLException{
		StringBuilder strBuff=new StringBuilder();
		PreparedStatement pstmt=null;
	     
		String methodName="loadUserHierarchyListForTransfer";
		
		 strBuff.append("SELECT distinct l,U.user_id_prefix,U.user_id, U.user_name, U.network_code, U.login_id, U.password, ");
	     strBuff.append("U.category_code, U.parent_id, U.owner_id, U.allowed_ip, U.allowed_days, ");
	     strBuff.append("U.from_time,U.to_time, U.last_login_on, U.employee_code, U.status, U.email, ");
	     strBuff.append("U.pswd_modified_on, U.contact_person, U.contact_no, U.designation,U.company,U.fax,U.firstname,U.lastname, ");
	     strBuff.append("U.division, U.department,U.msisdn, U.user_type, U.created_by, U.created_on, ");
	     strBuff.append("U.modified_by, U.modified_on, U.address1, U.address2, U.city, U.state, ");
	     strBuff.append("U.country, U.ssn, U.user_name_prefix, U.external_code,coalesce(U.user_code,U.msisdn) user_code, U.short_name, ");
	     strBuff.append("U.reference_id, invalid_password_count, level1_approved_by, ");
	     strBuff.append("level1_approved_on, level2_approved_by, U.level2_approved_on, U.appointment_date, ");
	     strBuff.append("U.password_count_updated_on,U.previous_status,L.lookup_name ");
	     strBuff.append(",CU.application_id, CU.mpay_profile_id, CU.user_profile_id,  ");
	     strBuff.append("CU.mcommerce_service_allow, CU.low_bal_alert_allow,c.grph_domain_type,c.SEQUENCE_NO  ");
	     strBuff.append(" from ( ");
	     strBuff.append(" with recursive q as( SELECT 1 as level ,USR.* , UCAT.category_code UCAT_category_code, USR.category_code catcode,UCAT.user_id_prefix FROM users USR, categories UCAT where  ");
	     if (p_mode.equalsIgnoreCase(PretupsI.SINGLE)) {
	    	 strBuff.append( " user_id in (SELECT user_id FROM users WHERE user_id=? AND  ");
	    	 strBuff.append(" category_code=? ) ");
	     }
	     else if (p_mode.equalsIgnoreCase(PretupsI.MULTIPLE)) {
	    	 strBuff.append(" user_id in (SELECT user_id FROM users WHERE user_id in(" + p_userId + ") AND  ");
	    	 strBuff.append(" category_code=? ) ");
		 }else if (p_mode.equalsIgnoreCase(PretupsI.ALL)) {
			 strBuff.append(" user_id in (SELECT user_id FROM users WHERE (parent_id=? OR user_id=? )AND  ");
			 strBuff.append(" category_code=? ) ");
		 }
	     strBuff.append("  union all ");
	     strBuff.append(" SELECT q.level+1 ,USR.* , UCAT.category_code UCAT_category_code ,USR.category_code catcode,UCAT.user_id_prefix FROM users USR join q on q.user_id = USR.parent_id, categories UCAT ");	     
	     strBuff.append(" ) SELECT level as l , q.* ,catcode FROM q ");
	     strBuff.append(" where ");
	     if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
	         strBuff.append("status IN (" + p_status + ")");
	     } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
	         strBuff.append("status NOT IN (" + p_status + ")");
	     } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
	         strBuff.append("status =? ");
	     } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
	         strBuff.append("status <> ? ");
	     }
	     strBuff.append(" AND UCAT_category_code=catcode ");
	     if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.STAFF_AS_USER))).booleanValue()) {
	         strBuff.append(" AND (user_type='CHANNEL' OR user_type='STAFF' ) ");
	     } else {
	         strBuff.append(" AND user_type='CHANNEL' ");
	     }
	     strBuff.append(" ) U ");
	     
	     strBuff.append(" ,lookups L, channel_users CU, categories c ");
	     
         strBuff.append(" WHERE  L.lookup_code = U.status AND L.lookup_type=? AND CU.user_id=U.user_id AND c.category_code=U.category_code ");
         
	     if (p_mode.equalsIgnoreCase(PretupsI.ALL)){
	    	 strBuff.append("  ORDER BY c.SEQUENCE_NO ");
	     }
	      pstmt = con.prepareStatement(strBuff.toString());
	      int i = 1;
	      if (p_mode.equalsIgnoreCase(PretupsI.SINGLE)) {
	    	  pstmt.setString(i++, p_userId[0]);
	    	  pstmt.setString(i++, p_userCategory);
	      }
	      else if (p_mode.equalsIgnoreCase(PretupsI.MULTIPLE)) {
	    	  pstmt.setString(i++, p_userCategory);
	      }
		 else if (p_mode.equalsIgnoreCase(PretupsI.ALL)) {
			 pstmt.setString(i++, p_userId[0]);
			 pstmt.setString(i++, p_userId[0]);
			 pstmt.setString(i++, p_userCategory);
		 }
	      
	      if (p_statusUsed.equals(PretupsI.STATUS_EQUAL) || p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) ){
	    	  pstmt.setString(i++, p_status);
	      }
	      pstmt.setString(i++, PretupsI.USER_STATUS_TYPE);
		  
	     return pstmt;
	     
	}
	
	
	//second
	@Override
	public String loadCategoryUsersWithinGeoDomainHirearchy(String p_userName, boolean p_isLoginChannelUsr,String p_loginID,String p_msisdn,String p_userStatusIN){
		StringBuilder strBuff = new StringBuilder();
		
		strBuff.append(" SELECT DISTINCT U.user_id, U.user_name,U.msisdn FROM ");
        if (p_isLoginChannelUsr) {
            strBuff.append(" (with recursive q as (SELECT U11.user_id FROM users U11 where U11.user_id=? union all select m.user_id from  users m join q on ");
      		 strBuff.append(" q.user_id = m.parent_id )select user_id from q) X, ");
        }
        strBuff.append(" users U, user_geographies UG, categories CAT,user_phones UP ");
        strBuff.append(" WHERE U.network_code = ? AND U.category_code = ?  ");
        if (p_isLoginChannelUsr) {
            strBuff.append(" AND X.user_id=U.user_id ");
        }
        strBuff.append(" AND u.status IN (" + p_userStatusIN + ") ");
        strBuff.append(" AND u.user_type = 'CHANNEL' ");
        strBuff.append(" AND UP.user_id=U.user_id ");
        strBuff.append(" AND U.category_code = CAT.category_code AND U.user_id=UG.user_id ");
        strBuff.append(" AND UG.grph_domain_code IN ( with recursive q1 as (SELECT grph_domain_code , status FROM geographical_domains GD1 WHERE  ");
        strBuff.append(" grph_domain_code IN (SELECT grph_domain_code FROM user_geographies UG1 WHERE UG1.user_id = ? )  ");
        strBuff.append("  union all select m1.grph_domain_code, m1.status from geographical_domains m1 join q1 on q1.grph_domain_code = m1.parent_grph_domain_code ");
        strBuff.append("  ) select grph_domain_code from q1");
        strBuff.append(" WHERE (status = 'Y' OR status = 'S') ");
        strBuff.append( ")");
        
        if (!BTSLUtil.isNullString(p_userName)) {
            strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?) ");
        }
        if (!BTSLUtil.isNullString(p_loginID)) {
            strBuff.append(" AND U.login_id=? ");
        }
        if (!BTSLUtil.isNullString(p_msisdn)) {
            strBuff.append(" AND UP.msisdn=? ");
        }
        strBuff.append(" ORDER BY U.user_name");
		
	    return strBuff.toString();
	}

	@Override
	public String debitUserBalancesForRevTxnQry(){
		StringBuilder strBuffSelect = new StringBuilder();
		strBuffSelect.append(" SELECT ");
		 strBuffSelect.append(" balance , balance_type ");
	     strBuffSelect.append(" FROM user_balances ");

	     // DB220120123for update WITH RS
	     if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
	         strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ?   FOR UPDATE OF balance WITH RS");
	     } else {
	         strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ?   FOR UPDATE  ");
	     }
	    return strBuffSelect.toString();
	}
	
	@Override
	public String debitUserBalancesForRevTxnDeleteQry(){
		StringBuilder strBuffSelect = new StringBuilder();
		strBuffSelect.append(" DELETE FROM user_threshold_counter WHERE transfer_id=? AND user_id=?");
	
	    return strBuffSelect.toString();
	}
	
	@Override
	public String creditUserBalancesForRevTxnQry(){
		StringBuilder strBuffSelect = new StringBuilder();
		strBuffSelect.append(" SELECT ");
		 strBuffSelect.append(" balance , balance_type ");
	     strBuffSelect.append(" FROM user_balances ");

	     // DB220120123for update WITH RS
	     if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
	         strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ?   FOR UPDATE OF balance WITH RS");
	     } else {
	         strBuffSelect.append("  WHERE user_id = ? and product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE  ");
	     }
	    return strBuffSelect.toString();
	}
	
	@Override
	public String creditUserBalancesForRevTxnDeleteQry(){
		StringBuilder strBuffSelect = new StringBuilder();
		strBuffSelect.append(" DELETE FROM user_threshold_counter WHERE transfer_id=? AND user_id=?");
	
	    return strBuffSelect.toString();
	}
	
	@Override
	public PreparedStatement userHierarchyQryByCategory(Connection con,String p_networkCode,String p_categoryCode,String p_loginUserID,String p_userName) throws SQLException{
		final StringBuffer strBuff = new StringBuffer(" with recursive q as (SELECT U.user_id, U.user_name,U.status,U.network_code , U.category_code, U.user_type, U.msisdn, U.login_id FROM users U ");
        strBuff.append(" WHERE U.user_id= ? ");
        strBuff.append(" union all select m.user_id, m.user_name,m.status,m.network_code , m.category_code, m.user_type, m.msisdn, m.login_id from users m join q on q.user_id=m.parent_id  ");
        strBuff.append(" ) select user_id , user_name, msisdn, login_id from q  where status = 'Y' AND  network_code = ? AND category_code = ? AND user_type='CHANNEL' ");
        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) ");
        strBuff.append(" ORDER BY user_name");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadCategoryUserHierarchyQry", "QUERY sqlSelect=" + sqlSelect);
        }
        PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);
        int i = 0;
        ++i;
        pstmtSelect.setString(i, p_loginUserID);
        ++i;
        pstmtSelect.setString(i, p_networkCode);
        ++i;
        pstmtSelect.setString(i, p_categoryCode);
        ++i;
        pstmtSelect.setString(i, p_userName);
        return pstmtSelect;

	}

	@Override
	public PreparedStatement loadCategoryUsersWithinGeoDomainHirearchyQryForAutoO2C(Connection con,
			String categoryCode, String geographicalDomainCode, String userName, String loginUserID) throws SQLException {
		
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT DISTINCT U.user_id, U.user_name,U.msisdn FROM users U LEFT OUTER JOIN Lookups L ON L.lookup_type = 'URTYP' AND L.lookup_code = u.STATUS ");
		strBuff.append(" LEFT OUTER JOIN USER_PHONES up ON u.user_id = up.user_id  AND up.PRIMARY_NUMBER = 'Y' LEFT OUTER JOIN users crby ON crby.user_id = u.CREATED_BY ");
		strBuff.append(" LEFT OUTER JOIN USER_GEOGRAPHIES ug ON ug.user_id = u.user_id where UPPER(u.user_name) like UPPER(?) ");
		strBuff.append(" AND u.status IN ('PA', 'Y', 'CH', 'DE', 'S') AND u.category_code = CASE  WHEN ? = 'ALL' THEN u.category_code ELSE ? END ");
		strBuff.append(" AND ug.grph_domain_code IN (WITH RECURSIVE q AS (SELECT grph_domain_code,status FROM geographical_domains WHERE grph_domain_code IN (SELECT grph_domain_code FROM user_geographies ug1 ");		
		strBuff.append(" WHERE ug1.grph_domain_code = CASE WHEN ? = 'ALL' THEN ug1.grph_domain_code ELSE ? END AND ug1.user_id = ? ) ");
		strBuff.append(" UNION ALL SELECT m.grph_domain_code,m.status FROM geographical_domains m JOIN q ON q.grph_domain_code = m.parent_grph_domain_code) ");
		strBuff.append(" SELECT q.grph_domain_code FROM q)AND u.CREATED_BY = ? ORDER BY u.user_name");

		String sqlSelect = strBuff.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug("", "QUERY sqlSelect=" + sqlSelect);
		}
		PreparedStatement pstmt = con.prepareStatement(sqlSelect);
		int i = 0;
		pstmt.setString(++i, userName);
		pstmt.setString(++i, categoryCode);
		pstmt.setString(++i, categoryCode);
		pstmt.setString(++i, geographicalDomainCode);
		pstmt.setString(++i, geographicalDomainCode);
		pstmt.setString(++i, loginUserID);
		pstmt.setString(++i, loginUserID);

		return pstmt;
	}
	
}
