package com.btsl.db.query.oracle;
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

public class ChannelUserWebOracleQry implements ChannelUserWebQry {
	//first
	@Override 
	public PreparedStatement loadCategoryUsersWithinGeoDomainHirearchyQry(Connection con,String p_networkCode,String p_categoryCode,String p_geographicalDomainCode,String p_userName,String p_loginUserID,String p_ownerUserID,String statusAllowed,String receiverStatusAllowed,String p_userId) throws SQLException{
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT DISTINCT U.user_id, U.user_name FROM users U, user_geographies UG, categories CAT ");
        strBuff.append(" WHERE U.network_code = ? AND U.category_code = ?  ");
        if (p_ownerUserID == null) {
            strBuff.append(" AND u.status IN (" + statusAllowed + ") ");
        } else {
            // loading child users
            strBuff.append(" AND u.status IN (" + receiverStatusAllowed + ") ");
        }
        strBuff.append(" AND u.user_type = 'CHANNEL' ");
        strBuff.append(" AND U.category_code = CAT.category_code AND U.user_id=UG.user_id ");

        strBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status = ? ");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code ");
        strBuff.append(" FROM user_geographies UG1 ");
        strBuff.append(" WHERE UG1.grph_domain_code = ? ");
        strBuff.append(" AND UG1.user_id = ? ))");
        strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?) ");
        if (p_ownerUserID != null && !"NA".equalsIgnoreCase(p_ownerUserID)) {

            strBuff.append(" AND  u.owner_id = ?  ");
        }
        strBuff.append(" AND U.user_id = ?");
        
        strBuff.append(" ORDER BY U.user_name");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadCategoryUsersWithinGeoDomainHirearchyQry", "QUERY sqlSelect=" + sqlSelect);
        }
        
        PreparedStatement pstmt = con.prepareStatement(sqlSelect);
        int i = 0;
        ++i;
        pstmt.setString(i, p_networkCode);
        ++i;
        pstmt.setString(i, p_categoryCode);
        ++i;
        pstmt.setString(i, PretupsI.GEO_DOMAIN_STATUS);
        ++i;
        pstmt.setString(i, p_geographicalDomainCode);
        ++i;
        pstmt.setString(i, p_loginUserID);
        ++i;
        pstmt.setString(i, p_userName);

        if (p_ownerUserID != null && !"NA".equalsIgnoreCase(p_ownerUserID)) {
            ++i;
            pstmt.setString(i, p_ownerUserID);
        }
        
		pstmt.setString(++i, p_userId);
		return pstmt;

	}
	
	
	//third
	@Override 
	public PreparedStatement loadCategoryUsersWithinGeoDomainHirearchyQry(Connection con,String p_networkCode,String p_categoryCode,String p_geographicalDomainCode,String p_userName,String p_loginUserID,String p_ownerUserID,String statusAllowed,String receiverStatusAllowed ) throws SQLException{
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT DISTINCT ou.user_id, ou.user_name,ou.msisdn,ou.login_id FROM users U, users ou ,  user_geographies UG, categories CAT ");
		strBuff.append(" WHERE U.network_code = ? AND U.category_code = ?  ");
		if(p_ownerUserID == null) {
            strBuff.append(" AND u.status IN ("+statusAllowed+") ");
        } else {
            strBuff.append(" AND u.status IN ("+receiverStatusAllowed+") ");
        }
		strBuff.append(" AND u.user_type = 'CHANNEL' ");
		strBuff.append(" AND U.category_code = CAT.category_code AND  UG.user_id=ou.user_id   and ou.user_id = u.owner_id ");
		
		strBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status = ? ");
		strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
		strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code ");
		strBuff.append(" FROM user_geographies UG1 ");
		if(!BTSLUtil.isEmpty(p_geographicalDomainCode))
		{
			strBuff.append(" WHERE UG1.grph_domain_code = ? ");
			strBuff.append(" AND UG1.user_id = ? ))");
		}
		else
		{
			strBuff.append("WHERE UG1.user_id = ? ))");
		}
		strBuff.append(" AND UPPER(ou.user_name) LIKE UPPER(?) ");
		if (p_ownerUserID != null && !p_ownerUserID.equalsIgnoreCase("NA")) {
			strBuff.append(" AND  u.owner_id = ?  ");
		}
		strBuff.append(" ORDER BY ou.user_name");
		PreparedStatement pstmt = con.prepareStatement(strBuff.toString());
		int i = 0;
		pstmt.setString(++i, p_networkCode);
		pstmt.setString(++i, p_categoryCode);
		pstmt.setString(++i, PretupsI.GEO_DOMAIN_STATUS);
		if(!BTSLUtil.isEmpty(p_geographicalDomainCode))
		{
			pstmt.setString(++i, p_geographicalDomainCode);

		}
		pstmt.setString(++i, p_loginUserID);
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
     strBuff.append(" FROM (SELECT U.user_id,U.user_name,U.user_code,U.network_code, U.login_id ");
     strBuff.append(" FROM users U WHERE U.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' AND NOT U.status IN('N','C') ");
     strBuff.append(" CONNECT BY PRIOR U.user_id=U.parent_id START WITH ");
     if (p_isUserCode) {
         strBuff.append(" U.user_code=? ");
     } else {
         strBuff.append(" U.user_id=? ");
     }
     strBuff.append(" ) X, user_phones UP WHERE X.user_id=UP.user_id AND UP.primary_number='Y' ");
     return strBuff.toString();
	}
	
	@Override
	public PreparedStatement deleteOrSuspendChnlUsersInBulkForMsisdn(Connection con,String userID,Map prepareStatementMap) throws SQLException{
		StringBuilder childExist = new StringBuilder();
		childExist.append("SELECT 1 FROM users ");
        childExist.append(" WHERE status <> 'N' AND status <> 'C' ");
        childExist.append("AND user_id != ? and user_type=? ");
        childExist.append("start with  user_id = ? ");
        childExist.append("connect by  prior user_id = parent_id");
        PreparedStatement psmtChildExist = (PreparedStatement) prepareStatementMap.get("psmtChildExist");
        if (psmtChildExist == null  || psmtChildExist.isClosed()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("processUploadedFileForUnReg", "psmtChildExist=" + childExist);
            }
            psmtChildExist = con.prepareStatement(childExist.toString());
            prepareStatementMap.put("psmtChildExist", psmtChildExist);
        }
        
        
        psmtChildExist.clearParameters();
        psmtChildExist.setString(1, userID);
        psmtChildExist.setString(2, PretupsI.CHANNEL_USER_TYPE);
        psmtChildExist.setString(3, userID);
        return psmtChildExist;
        
	}

	@Override
	public PreparedStatement loadChannelUserListQry(Connection con,String p_userCategory,String p_domainCode,String p_userName,String p_userID,String p_zoneCode) throws SQLException{
		 final StringBuilder strBuff = new StringBuilder(" SELECT DISTINCT U.user_id, U.user_name,U.owner_id,OU.user_name owner_name,U.parent_id ");
	        strBuff.append(" FROM users U,users OU, user_geographies UG, categories CAT ");
	        strBuff.append(" WHERE U.owner_id=OU.user_id AND U.category_code = ? AND U.status='Y'  ");
	        strBuff.append(" AND U.category_code = CAT.category_code AND CAT.domain_code = ? ");
	        strBuff.append(" AND U.user_id = UG.user_id AND U.user_type='CHANNEL' AND UG.grph_domain_code IN  ");
	        strBuff.append(" (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN(?, ?) ");
	        strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
	        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code ");
	        strBuff.append(" FROM user_geographies UG1 WHERE UG1.grph_domain_code = ? ");
	        strBuff.append(" AND UG1.user_id= ? )) AND UPPER(U.user_name) LIKE UPPER(?) ");
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
            pstmtSelect.setString(i, PretupsI.STATUS_ACTIVE);
            ++i;
            pstmtSelect.setString(i, PretupsI.STATUS_SUSPEND);
            ++i;
            pstmtSelect.setString(i, p_zoneCode);
            ++i;
            pstmtSelect.setString(i, p_userID);
            ++i;
            pstmtSelect.setString(i, p_userName);
            return pstmtSelect;
	}

	@Override
	public PreparedStatement loadCategoryUserHierarchyQry(Connection con,String p_networkCode,String p_categoryCode,String p_loginUserID,String p_userName) throws SQLException{
		final StringBuilder strBuff = new StringBuilder(" SELECT U.user_id, U.user_name FROM users U ");
        strBuff.append(" WHERE U.status = 'Y' AND  U.network_code = ? AND U.category_code = ? AND user_type='CHANNEL' ");
        strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?) ");
        strBuff.append(" CONNECT BY PRIOR U.user_id=U.parent_id START WITH U.user_id= ? ");
        strBuff.append(" ORDER BY U.user_name");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadCategoryUserHierarchyQry", "QUERY sqlSelect=" + sqlSelect);
        }
        PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);
        int i = 0;
        ++i;
        pstmtSelect.setString(i, p_networkCode);
        ++i;
        pstmtSelect.setString(i, p_categoryCode);
        ++i;
        pstmtSelect.setString(i, p_userName);
        ++i;
        pstmtSelect.setString(i, p_loginUserID);
        return pstmtSelect;

	}
	
	@Override
	public PreparedStatement loadUsersForEnquiryQry(Connection con,String p_networkCode,String p_categoryCode,String p_loginUserID,String p_userName,String p_ownerUserID,boolean p_isOnlyActiveUser,String p_geographicalDomainCode)throws SQLException
	{
	
		final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT DISTINCT U.user_id, U.user_name FROM users U, user_geographies UG, categories CAT ");
        strBuff.append(" WHERE U.network_code = ? AND U.category_code = ?  ");
        if (!p_isOnlyActiveUser) {
            strBuff.append(" AND u.status IN (" + PretupsBL.userStatusIn() + ", '" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "') ");
        } else {
            strBuff.append(" AND u.status = 'Y' ");
        }
        strBuff.append(" AND u.user_type = 'CHANNEL' ");
        strBuff.append(" AND U.category_code = CAT.category_code AND U.user_id=UG.user_id ");
        strBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status = ? ");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code ");
        strBuff.append(" FROM user_geographies UG1 ");
        strBuff.append(" WHERE UG1.grph_domain_code = ? ");
        strBuff.append(" AND UG1.user_id = ? ))");
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
        pstmt.setString(i, PretupsI.GEO_DOMAIN_STATUS);
        ++i;
        pstmt.setString(i, p_geographicalDomainCode);
        ++i;
        pstmt.setString(i, p_loginUserID);
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

   	 final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT U.user_name,U.user_id,U.user_code,U.msisdn,U.login_id,U.category_code,C.category_name,CG.grade_code,U.status,");
        strBuff.append("CG.grade_name,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend,U.external_code, ");
        strBuff.append("CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW))).booleanValue()) {
            strBuff.append(" , CU.trf_rule_type  ");
        }
        strBuff.append("FROM users U,channel_users CU,channel_grades CG,categories C,user_geographies UG, ");
        strBuff.append("commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append("WHERE U.network_code=? AND U.user_id=CU.user_id AND U.user_id=UG.user_id AND ");
        strBuff.append("U.category_code=C.category_code AND U.category_code=CG.category_code AND CU.user_grade=CG.grade_code ");
        strBuff.append(" AND U.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
        strBuff.append("AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append("AND TP.profile_id = CU.transfer_profile_id AND C.category_code IN (" + p_categoryCode + ") ");
        strBuff.append("AND C.domain_code =? AND U.status IN (" + receiverStatusAllowed + ") AND C.status='Y' ");
        strBuff.append("AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 ");
        strBuff.append("WHERE status = 'Y' CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        strBuff.append("START WITH grph_domain_code IN(" + p_geographicalDomainCode + ")) ");
        strBuff.append("AND CPSV.applicable_from =nvl ( (SELECT MAX(applicable_from) FROM ");
        strBuff.append("commission_profile_set_version WHERE applicable_from <= ? AND ");
        strBuff.append("comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from) ");
        strBuff.append("ORDER BY C.sequence_no,CU.user_grade,U.login_id");
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

	        strBuff.append(" AND C.status='Y' AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 ");
	        strBuff.append("WHERE status = 'Y' CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
	        strBuff.append("START WITH grph_domain_code IN(");

	        for (int i = 0; i < m_geographicalDomainCode.length; i++) {
	            strBuff.append(" ?");
	            if (i != m_geographicalDomainCode.length - 1) {
	                strBuff.append(",");
	            }
	        }
	        strBuff.append("))");
	        strBuff.append("AND CPSV.applicable_from =nvl ( (SELECT MAX(applicable_from) FROM ");
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
	public String validateUsersForBatchC2CQry(String p_categoryCode,String StatusAllowed)
{
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT U.user_id,CPSV.dual_comm_type,U.user_code,U.msisdn,U.login_id,U.category_code,C.category_name,CG.grade_code,U.status,");
        strBuff.append("CG.grade_name,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend,U.external_code, ");
        strBuff.append("CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version,CPSV.OTH_COMM_PRF_SET_ID, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
        strBuff.append("FROM users U,channel_users CU,channel_grades CG,categories C,user_geographies UG, ");
        strBuff.append("commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append("WHERE ( U.login_id= ? OR U.msisdn= ? ) AND U.network_code=? AND U.user_id=CU.user_id AND U.user_id=UG.user_id AND ");
        strBuff.append("U.category_code=C.category_code AND U.category_code=CG.category_code AND CU.user_grade=CG.grade_code ");
        strBuff.append("AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append("AND TP.profile_id = CU.transfer_profile_id AND C.category_code IN ('" + p_categoryCode + "') ");
        strBuff.append("AND C.domain_code =? AND U.status IN (" + StatusAllowed + ") AND C.status='Y' ");
        // strBuff.append("AND UG.grph_domain_code IN (SELECT grph_domain_code
        // FROM geographical_domains GD1 ");
        // strBuff.append("WHERE status = 'Y' CONNECT BY PRIOR grph_domain_code
        // = parent_grph_domain_code ");
        // strBuff.append("START WITH grph_domain_code
        // IN("+p_geographicalDomainCode+")) ");
        strBuff.append("AND CPSV.applicable_from =nvl ( (SELECT MAX(applicable_from) FROM ");
        strBuff.append("commission_profile_set_version WHERE applicable_from <= ? AND ");
        strBuff.append("comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from) ");
        strBuff.append("ORDER BY C.sequence_no,CU.user_grade,U.login_id");
        return strBuff.toString();
	}
	
	@Override
	public String ValidateChnlUserDetailsByExtCodeQry(){
		 final StringBuilder selectQueryBuff = new StringBuilder(
		            " SELECT u.user_id, u.password webpassword,u.user_name, u.network_code,u.login_id, u.category_code, u.parent_id, u.owner_id, u.msisdn,");
		        selectQueryBuff.append(" u.employee_code,u.status status,u.created_by,u.created_on,u.modified_by,u.modified_on,");
		        selectQueryBuff.append(" cusers.contact_person,u.contact_no,u.designation,u.division,u.department,u.user_type,cusers.in_suspend,cusers.out_suspend,");
		        selectQueryBuff.append(" u.address1,u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,");
		        selectQueryBuff.append(" cat.domain_code,dom.domain_type_code,cat.sequence_no catseq,cat.sms_interface_allowed,geo.grph_domain_code,gdomains.status geostatus, ");
		        selectQueryBuff
		            .append(" uphones.msisdn prmsisdn, uphones.user_phones_id,uphones.primary_number, uphones.sms_pin, uphones.pin_required, uphones.phone_profile, uphones.phone_language phlang,");
		        selectQueryBuff.append(" uphones.country phcountry, uphones.invalid_pin_count, uphones.last_transaction_status, uphones.last_transaction_on,");
		        selectQueryBuff
		            .append(" uphones.pin_modified_on,uphones.last_transfer_id, uphones.last_transfer_type,uphones.prefix_id,uphones.temp_transfer_id, uphones.first_invalid_pin_time, ");
		        selectQueryBuff
		            .append(" cat.agent_allowed,cat.hierarchy_allowed, cat.category_type,cat.category_name,cat.grph_domain_type,cusers.comm_profile_set_id,cusers.transfer_profile_id, tp.status profile_status,cusers.user_grade grade_code,cset.status commprofilestatus, ");
		        selectQueryBuff
		            .append(" cset.language_1_message comprf_lang_1_msg,cset.language_2_message  comprf_lang_2_msg,cat.restricted_msisdns,gdt.sequence_no grphSeq, cat.transfertolistonly, uphones.created_on uphones_created,CPSV.applicable_from,CPSV.comm_profile_set_version  ");
		        selectQueryBuff
		            .append(" FROM users u,user_geographies geo,categories cat,domains dom,channel_users cusers,user_phones uphones,transfer_profile tp,commission_profile_set cset,commission_profile_set_version CPSV,geographical_domains gdomains,geographical_domain_types gdt ");
		        selectQueryBuff.append(" WHERE u.external_code=? AND uphones.user_id=u.user_id AND uphones.primary_number=? AND u.status <> ? AND u.status <> ? ");
		        selectQueryBuff
		            .append(" AND u.user_id=cusers.user_id(+) AND u.category_code = cat.category_code AND u.user_id=geo.user_id AND geo.grph_domain_code=gdomains.grph_domain_code ");
		        selectQueryBuff
		            .append(" AND cat.domain_code= dom.domain_code AND cusers.transfer_profile_id=tp.profile_id(+) AND cusers.comm_profile_set_id=cset.comm_profile_set_id(+) AND gdt.grph_domain_type=gdomains.grph_domain_type ");
		        selectQueryBuff.append(" AND CPSV.applicable_from =nvl ( (SELECT MAX(applicable_from) FROM ");
		        selectQueryBuff.append(" commission_profile_set_version WHERE applicable_from <= ? AND ");
		        selectQueryBuff.append(" comm_profile_set_id=cusers.comm_profile_set_id),CPSV.applicable_from) ");

		       return selectQueryBuff.toString();

	}
	
	@Override
	public String validateUsersForBatchDP(String p_categoryCode,String p_geographicalDomainCode){
		    final StringBuilder strBuff = new StringBuilder();
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
	        strBuff.append("AND TP.profile_id = CU.transfer_profile_id AND C.category_code IN (" + p_categoryCode + ") ");
	        strBuff.append("AND C.domain_code =? AND U.status <> 'N' AND U.status <> 'C' AND C.status='Y' ");
	        strBuff.append("AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 ");
	        strBuff.append("WHERE status = 'Y' CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
	        strBuff.append("START WITH grph_domain_code IN(" + p_geographicalDomainCode + ")) ");
	        strBuff.append("AND CPSV.applicable_from =nvl ( (SELECT MAX(applicable_from) FROM ");
	        strBuff.append("commission_profile_set_version WHERE applicable_from <= ? AND ");
	        strBuff.append("comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from) ");
	        strBuff.append("ORDER BY C.sequence_no,CU.user_grade,U.login_id");
	        return strBuff.toString();
	      
	}
	
	
	@Override
	public
	PreparedStatement loadStaffUsersDetailsbyLoginIDforSuspend(Connection p_con,String p_chusrid,String p_loginID,String p_status) throws SQLException{
	
		final StringBuilder str = new StringBuilder(" SELECT U.USER_ID FROM USERS U WHERE U.LOGIN_ID=? ");
        str.append("AND u.user_type=? AND u.status NOT IN(" + p_status + ") ");
        str.append("connect by prior user_id=parent_id start with user_id=? ");

        final String query = str.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadStaffUsersDetailsbyLoginIDforSuspend ", "Query: " + query);
        }
        PreparedStatement pstm = p_con.prepareStatement(query);
        int index=0;
        index++;
        pstm.setString(index, p_loginID);
        index++;
        pstm.setString(index, PretupsI.USER_TYPE_STAFF);
        index++;
        pstm.setString(index, p_chusrid);
        return pstm;
	}
	
	@Override
	public PreparedStatement loadStaffUsersDetailsForSuspend(Connection p_con,String p_status,String p_msisdn,String p_chuserid) throws SQLException{
	
		final StringBuilder str = new StringBuilder(" SELECT U.USER_ID FROM USERS U, USER_PHONES UP WHERE UP.MSISDN=? AND ");
        str.append(" up.user_id=u.USER_ID ");
        str.append(" AND u.user_type=? AND u.status NOT IN(" + p_status + ") ");
        str.append("connect by prior u.user_id=u.parent_id start with u.user_id=? ");

        final String query = str.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadStaffUsersDetails ", "Query: " + query);
        }
        PreparedStatement pstm = p_con.prepareStatement(query);
        int index=0;
        index++;
        pstm.setString(index, p_msisdn);
        index++;
        pstm.setString(index, PretupsI.USER_TYPE_STAFF);
        index++;
        pstm.setString(index, p_chuserid);
        return pstm;
	}
	
	@Override
	public String loadChannelUserDetailsByUserNameQry(){
		 final StringBuilder strBuff = new StringBuilder(" SELECT U.user_id, user_name, login_id, user_type, msisdn FROM USERS U,USER_GEOGRAPHIES UG ");
         strBuff.append(" WHERE U.network_code=? AND U.user_name = ?  AND U.user_type=?  AND U.status NOT IN('N','C')   AND U.category_code=?  AND U.user_id=UG.user_id  ");
         strBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y', 'S')  ");
         strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
         strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code FROM USER_GEOGRAPHIES UG1 ");
         strBuff.append("  WHERE	UG1.grph_domain_code = ? AND UG1.user_id=? )) ");
         return strBuff.toString();
        
	}
	
	
	
	
	@Override
	public String loadParentUserDetailsByUserID(){
		final StringBuilder selectQueryBuff = new StringBuilder(
                " SELECT DECODE(u1.parent_id,'ROOT',u1.user_id,u1.parent_id) AS user_id, u2.owner_id, u2.user_name, u2.login_id, u2.network_code, u2.category_code, u2.msisdn, ");
            selectQueryBuff.append(" cat.category_code, cat.category_name, cat.domain_code, cat.multiple_grph_domains,dom.domain_type_code, ");
            selectQueryBuff.append(" cat.sequence_no,cat.multiple_login_allowed, cat.max_login_count,cat.view_on_network_block, ");
            selectQueryBuff.append(" cat.status catstatus, cat.max_txn_msisdn, cat.uncntrl_transfer_allowed, cat.scheduled_transfer_allowed, cat.restricted_msisdns, ");
            selectQueryBuff.append(" cat.parent_category_code, cat.product_types_allowed,cat.category_type,cat.hierarchy_allowed, cat.transfertolistonly, ");
            selectQueryBuff.append(" cat.grph_domain_type, cat.fixed_roles, cat.user_id_prefix,cat.web_interface_allowed, cat.sms_interface_allowed, ");
            selectQueryBuff
                .append(" cat.services_allowed,cat.domain_allowed,cat.fixed_domains,cat.outlets_allowed,cat.status categorystatus, gdt.grph_domain_type_name, gdt.sequence_no grph_sequence_no, ");
            selectQueryBuff
                .append(" cusers.contact_person, cusers.in_suspend, cusers.out_suspend, cusers.outlet_code, cusers.suboutlet_code, cusers.user_grade, cusers.comm_profile_set_id,cusers.transfer_profile_id, ");
            selectQueryBuff.append(" up.PHONE_PROFILE, up.pin_required ");
            selectQueryBuff.append(" FROM users u1, users u2, categories cat, channel_users cusers, geographical_domain_types gdt, domains dom, user_phones up ");
            selectQueryBuff.append(" WHERE UPPER(u1.user_id)=UPPER(?) ");
            selectQueryBuff.append(" AND u2.user_id(+)= DECODE(u1.parent_id,'ROOT',u1.user_id,u1.parent_id) ");
            selectQueryBuff.append(" AND u2.category_code = cat.category_code ");
            selectQueryBuff.append(" AND u2.user_id=cusers.user_id(+) ");
            selectQueryBuff.append(" AND u2.user_id=up.user_id(+) ");
            selectQueryBuff.append(" AND gdt.grph_domain_type = cat.grph_domain_type ");
            selectQueryBuff.append(" AND cat.domain_code= dom.domain_code  ");
            return selectQueryBuff.toString();
	}
	
	@Override
	public String loadChannelUserDetailsByLoginIDANDORMSISDN(String p_msisdn,String  p_loginid){
		 final StringBuilder selectQueryBuff = new StringBuilder(
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
	            selectQueryBuff
	                .append(" FROM users u,user_geographies geo,categories cat,domains dom,channel_users cusers,user_phones uphones,transfer_profile tp,commission_profile_set cset,geographical_domains gdomains,geographical_domain_types gdt ");
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
	                .append(" AND uphones.user_id=u.user_id AND u.user_id=cusers.user_id(+) AND u.category_code = cat.category_code AND u.user_id=geo.user_id AND geo.grph_domain_code=gdomains.grph_domain_code ");
	            selectQueryBuff
	                .append(" AND cat.domain_code= dom.domain_code AND cusers.transfer_profile_id=tp.profile_id(+) AND cusers.comm_profile_set_id=cset.comm_profile_set_id(+) AND gdt.grph_domain_type=gdomains.grph_domain_type ");
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
	        strBuff.append("AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 ");
	        strBuff.append("WHERE status = 'Y' CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
	        strBuff.append("START WITH grph_domain_code IN(");
	        for (int i = 0; i < m_geographicalDomainCode.length; i++) {
	            strBuff.append(" ?");
	            if (i != m_geographicalDomainCode.length - 1) {
	                strBuff.append(",");
	            }
	        }
	        strBuff.append("))");
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
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg, CU.sos_allowed , CU.sos_allowed_amount , CU.sos_threshold_limit, CU.lr_allowed, CU.lr_max_amount ");
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
        strBuff.append("AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 ");
        strBuff.append("WHERE status = 'Y' CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        strBuff.append("START WITH grph_domain_code IN(");
        // akanksha5
        for (int i = 0; i < m_geographicalDomainCode.length; i++) {
            strBuff.append(" ?");
            if (i != m_geographicalDomainCode.length - 1) {
                strBuff.append(",");
            }
        }
        strBuff.append("))");
        strBuff.append("AND CPSV.applicable_from =nvl ( (SELECT MAX(applicable_from) FROM ");
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

        strBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status = ? ");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code ");
        strBuff.append(" FROM user_geographies UG1 ");
        strBuff.append(" WHERE UG1.grph_domain_code = ? ");
        strBuff.append(" AND UG1.user_id = ? ))");
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
        pstmt.setString(i, PretupsI.GEO_DOMAIN_STATUS);
        ++i;
        pstmt.setString(i, p_geographicalDomainCode);
        ++i;
        pstmt.setString(i, p_loginUserID);
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
		 final StringBuilder selectQueryBuff = new StringBuilder("SELECT u.user_id,cu.lms_profile, ps.promotion_type ");
	        selectQueryBuff.append("  FROM users u, channel_users cu, profile_set_version psv, profile_set ps ");
	        selectQueryBuff.append("  WHERE u.user_id = cu.user_id ");
	        selectQueryBuff.append("  AND u.status IN ('Y', 'S') ");
	        selectQueryBuff.append("  AND cu.lms_profile is not null ");
	        selectQueryBuff.append("  AND ps.set_id = psv.set_id ");
	        selectQueryBuff.append("  AND cu.lms_profile=ps.set_id ");
	        selectQueryBuff.append("  AND psv.status IN ('Y', 'S') ");
	        selectQueryBuff.append("  AND ps.promotion_type = ? ");
	        selectQueryBuff.append("  AND trunc(psv.applicable_to) >=trunc(SYSDATE) ");
	        selectQueryBuff.append("  AND u.msisdn = ? ");

	       return selectQueryBuff.toString();

	}
	
	
	@Override
	public PreparedStatement loadUserListOnZoneDomainCategoryWithMSISDN(Connection con,String fromUserID,String userName,String domainCode,String userCategory,String zoneCode,String pLOGinuserID  ) throws SQLException{
	
		StringBuilder strBuff = new StringBuilder(" SELECT  U.MSISDN , U.user_name");
       	strBuff.append(" FROM users U, user_geographies UG, categories CAT ");
    	strBuff.append(" WHERE U.category_code = DECODE(?, '"+PretupsI.ALL+"', U.category_code, ?) ");
    	//strBuff.append(" AND CAT.domain_code = ? ");
    	strBuff.append(" AND CAT.domain_code IN (SELECT UD.domain_code FROM user_domains UD WHERE UD.domain_code = (case ?  when '"+PretupsI.ALL+"' then UD.domain_code else ? end) AND UD.user_id = ?)");
    	strBuff.append(" AND CAT.category_code = U.category_code ");
    	strBuff.append(" AND U.user_type = '"+PretupsI.CHANNEL_USER_TYPE+"' ");
    	strBuff.append(" AND U.status NOT IN ('N','C','W') ");
    	strBuff.append(" AND U.user_id = UG.user_id ");
      	strBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN(?, ?) ");
    	strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
    	strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code ");
    	strBuff.append(" FROM user_geographies UG1 ");
    	strBuff.append(" WHERE	UG1.grph_domain_code = DECODE(?, '"+PretupsI.ALL+"', UG1.grph_domain_code, ?) ");
    	strBuff.append(" AND UG1.user_id= ? ))");
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
	    pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
	    pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);
	    pstmtSelect.setString(++i, zoneCode);
	    pstmtSelect.setString(++i, zoneCode);
	    pstmtSelect.setString(++i, pLOGinuserID);
	    if(!BTSLUtil.isNullString(fromUserID))//for from userid
	        pstmtSelect.setString(++i, fromUserID);
	    if(!BTSLUtil.isNullString(userName))
	    {
	    	//commented for DB2 pstmtSelect.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);
	        pstmtSelect.setString(++i,userName+"%");
	    }
		return pstmtSelect;
	}

	@Override
	public PreparedStatement loadChannelUserListHierarchyQry(Connection con,String p_domainCode,String p_userCategory,String p_userName,String p_userID,String p_zoneCode) throws SQLException{
		 final StringBuilder strBuff = new StringBuilder(" SELECT DISTINCT U.user_id, U.user_name,U.owner_id,OU.user_name owner_name,U.parent_id,U.login_id ");
	        strBuff.append(" FROM users U,users OU, user_geographies UG, categories CAT ");
	        strBuff.append(" WHERE U.owner_id=OU.user_id AND U.category_code = ?  AND U.status='Y' AND U.user_type='CHANNEL' ");
	        strBuff.append(" AND U.category_code = CAT.category_code AND CAT.domain_code = ? AND U.user_id = UG.user_id ");
	        strBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN(?, ?) ");
	        strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
	        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code ");
	        strBuff.append(" FROM user_geographies UG1 WHERE UG1.grph_domain_code = ? ");
	        strBuff.append(" AND UG1.user_id= ? )) AND UPPER(U.user_name) LIKE UPPER(?) ");
	        strBuff.append(" CONNECT BY PRIOR U.user_id=U.parent_id START WITH U.user_id= ? ");
	        strBuff.append(" ORDER BY U.user_name");

	        final String sqlSelect = strBuff.toString();
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("loadChannelUserListHierarchyQry", "QUERY sqlSelect=" + sqlSelect);
	        }
	        PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);
            int i = 0;
            ++i;
            pstmtSelect.setString(i, p_userCategory);
            ++i;
            pstmtSelect.setString(i, p_domainCode);
            ++i;
            pstmtSelect.setString(i, PretupsI.STATUS_ACTIVE);
            ++i;
            pstmtSelect.setString(i, PretupsI.STATUS_SUSPEND);
            ++i;
            pstmtSelect.setString(i, p_zoneCode);
            ++i;
            pstmtSelect.setString(i, p_userID);
            // commented for DB2 pstmtSelect.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            pstmtSelect.setString(i, p_userName);
            ++i;
            pstmtSelect.setString(i, p_userID);
            
            return pstmtSelect;
	}

	@Override
	public PreparedStatement loadTransferredUserPrevHierarchyQry(Connection p_con,String p_mode,String[] p_userId,boolean p_isSearchOnDate,Date p_fromDate,Date p_toDate)throws SQLException{
			StringBuilder strBuff = new StringBuilder() ;
		strBuff.append(" SELECT l,user_id_prefix,U.user_id, U.MODIFIED_ON,U.user_name, U.login_id,U.category_code, U.msisdn, ");
        strBuff.append(" UP.user_name parent_name,U.reference_Id,L.lookup_name,NVL(UB.prev_balance,0)prev_balance, ");
        strBuff.append(" OLD_USR.user_id prev_user_id, OLD_USR.user_name prev_user_name,OLD_PUSR.user_name prev_parent_name, ");
        strBuff.append(" OLD_USR.category_code prev_cat_code ");
        strBuff.append(" FROM ( SELECT LEVEL l,USR.* ,USR.category_code catcode,UCAT.user_id_prefix ");
        strBuff.append(" FROM USERS USR, CATEGORIES UCAT WHERE UCAT.category_code=USR.category_code ");
        strBuff.append(" AND USR.user_type='CHANNEL' ");

        if (p_mode.equalsIgnoreCase(PretupsI.SINGLE) || p_mode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" CONNECT BY PRIOR user_id = parent_id START WITH user_id=? ) U, ");
        } else if (p_mode.equalsIgnoreCase(PretupsI.MULTIPLE)) {
            final StringBuilder str = new StringBuilder();
            for (int k = 0; k < p_userId.length; k++) {
                str.append("'");
                str.append(p_userId[k]);
                str.append("',");
            }
            final String userID = str.substring(0, str.length() - 1);
            strBuff.append(" CONNECT BY PRIOR user_id = parent_id START WITH user_id in (" + userID + ")) U, ");
        }
        strBuff.append(" LOOKUPS L, USER_BALANCES UB, USERS UP, USERS OLD_USR, USERS OLD_PUSR ");
        strBuff.append(" WHERE  L.lookup_code = U.status ");
        strBuff.append(" AND L.lookup_type='URTYP' AND UB.user_id(+)=OLD_USR.user_id ");
        strBuff.append(" AND L.lookup_code<>'C' ");
        strBuff.append(" AND U.parent_id=UP.user_id AND OLD_USR.user_id=U.reference_id AND OLD_PUSR.user_id(+)=OLD_USR.parent_id ");

        if (p_isSearchOnDate) {
            strBuff.append(" AND TRUNC(OLD_USR.modified_on(+))>=? AND TRUNC(OLD_USR.modified_on(+))<=? ");
        }

        String sqlSelect=strBuff.toString();
        if (LOG.isDebugEnabled()) {
             LOG.debug("loadTransferredUserPrevHierarchy ", "QUERY sqlSelect=" + sqlSelect);
         }
        PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);

         if (!p_isSearchOnDate && (p_mode.equalsIgnoreCase(PretupsI.SINGLE) || p_mode.equalsIgnoreCase(PretupsI.ALL))) {
             pstmt.setString(1, p_userId[0]);
         } else if (p_isSearchOnDate && (p_mode.equalsIgnoreCase(PretupsI.SINGLE) || p_mode.equalsIgnoreCase(PretupsI.ALL))) {
             pstmt.setString(1, p_userId[0]);
             pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
             pstmt.setDate(3, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
         } else if (p_isSearchOnDate && p_mode.equalsIgnoreCase(PretupsI.MULTIPLE)) {
             pstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
             pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
         }
   return pstmt;
	}
	

	@Override
	public String validateUsersForBatchFOC(String[] m_categoryCode,String[] m_receiverStatusAllowed,String [] m_geographicalDomainCode){
	
		StringBuilder strBuff =new StringBuilder();
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
        strBuff.append("AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 ");
        strBuff.append("WHERE status = 'Y' CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        strBuff.append("START WITH grph_domain_code IN(");
        // akanksha5
        for (int i = 0; i < m_geographicalDomainCode.length; i++) {
            strBuff.append(" ?");
            if (i != m_geographicalDomainCode.length - 1) {
                strBuff.append(",");
            }
        }
        strBuff.append("))");
        strBuff.append("AND CPSV.applicable_from =nvl ( (SELECT MAX(applicable_from) FROM ");
        strBuff.append("commission_profile_set_version WHERE applicable_from <= ? AND ");
        strBuff.append("comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from) ");
        strBuff.append("ORDER BY C.sequence_no,CU.user_grade,U.login_id");
        return strBuff.toString();
	}
	
	
@Override
public String loadUsersForBatchDP(String[] m_categoryCode,String [] m_geographicalDomainCode){
	 final StringBuilder strBuff = new StringBuilder();
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
     strBuff.append("AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 ");
     strBuff.append("WHERE status = 'Y' CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
     strBuff.append("START WITH grph_domain_code IN(");

     for (int i = 0; i < m_geographicalDomainCode.length; i++) {
         strBuff.append(" ?");
         if (i != m_geographicalDomainCode.length - 1) {
             strBuff.append(",");
         }
     }
     strBuff.append("))");

     strBuff.append("AND CPSV.applicable_from =nvl ( (SELECT MAX(applicable_from) FROM ");
     strBuff.append("commission_profile_set_version WHERE applicable_from <= ? AND ");
     strBuff.append("comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from) ");
     strBuff.append("ORDER BY C.sequence_no,CU.user_grade,U.login_id");
     return strBuff.toString();
     }


@Override
public String loadUsersForBatchDP(String[] m_categoryCode,String [] m_geographicalDomainCode,String p_productCode){
	 final StringBuilder strBuff = new StringBuilder();
     strBuff.append("SELECT U.user_id,U.user_code,U.msisdn,U.login_id,U.category_code,C.category_name,CG.grade_code,U.status,");
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

     strBuff.append("AND C.domain_code =? AND U.status <> 'N' AND U.status <> 'C' AND C.status='Y' ");
     strBuff.append("AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 ");
     strBuff.append("WHERE status = 'Y' CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
     strBuff.append("START WITH grph_domain_code IN(");

     for (int i = 0; i < m_geographicalDomainCode.length; i++) {
         strBuff.append(" ?");
         if (i != m_geographicalDomainCode.length - 1) {
             strBuff.append(",");
         }
     }
     strBuff.append("))");

     strBuff.append("AND CPSV.applicable_from =nvl ( (SELECT MAX(applicable_from) FROM ");
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
	StringBuilder strBuff = new StringBuilder();
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
    strBuff.append("FROM user_phones USR_PHONE,users USR, users PRNT_USR,users ONR_USR,categories USR_CAT, users MOD_USR, ");
    strBuff.append("categories ONR_CAT, categories  PRNT_CAT,lookups l,users USR_CRBY, domains D, user_geographies UG, geographical_domains GD ");
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

    strBuff.append("AND USR.parent_id=PRNT_USR.user_id(+) AND USR.owner_id=ONR_USR.user_id ");
    strBuff.append("AND USR.category_code=USR_CAT.category_code ");
    strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
    strBuff.append("AND PRNT_USR.category_code=PRNT_CAT.category_code(+) ");
    strBuff.append(" AND USR.status = l.lookup_code ");
    strBuff.append(" AND l.lookup_type= ? ");
    strBuff.append(" AND USR.user_id = UG.user_id ");
    strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
    strBuff.append(" AND MOD_USR.user_id(+) = USR.modified_by ");
    strBuff.append(" AND USR_CRBY.user_id(+) = USR.created_by ");
    strBuff.append(" AND USR_CAT.domain_code=D.domain_code ");
    strBuff.append(" AND USR.user_type='STAFF' ");
    if (p_userID != null) {
        strBuff.append(" AND USR.user_id IN ( ");
        strBuff.append(" SELECT user_id from users where user_id != ? ");
        strBuff.append(" CONNECT BY PRIOR user_id = parent_id START WITH  user_id=? ) ");
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
    strBuff.append("FROM users USR, users PRNT_USR,users ONR_USR,categories USR_CAT,categories ONR_CAT,");
    strBuff.append("categories  PRNT_CAT,lookups l,users MOD_USR, users USR_CRBY, domains D,user_geographies UG, geographical_domains GD ");
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

    strBuff.append(" AND USR.parent_id=PRNT_USR.user_id(+) AND USR.owner_id=ONR_USR.user_id ");
    strBuff.append(" AND USR.category_code=USR_CAT.category_code ");
    strBuff.append(" AND ONR_CAT.category_code=ONR_USR.category_code ");
    strBuff.append(" AND PRNT_USR.category_code=PRNT_CAT.category_code(+) ");
    strBuff.append(" AND USR.status = l.lookup_code ");
    strBuff.append(" AND l.lookup_type= ? ");
    strBuff.append(" AND USR.user_id = UG.user_id ");
    strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
    strBuff.append(" AND MOD_USR.user_id(+) = USR.modified_by ");
    strBuff.append(" AND USR_CRBY.user_id(+) = USR.created_by ");
    strBuff.append(" AND USR_CAT.domain_code=D.domain_code ");
    strBuff.append(" AND USR.user_type= 'STAFF' ");
    if (p_userID != null) {
        strBuff.append(" AND USR.user_id IN ( ");
        strBuff.append(" SELECT user_id from users where user_id != ? ");
        strBuff.append(" CONNECT BY PRIOR user_id = parent_id START WITH  user_id=? ) ");
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
	StringBuilder strBuff =new StringBuilder();
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

     strBuff.append("FROM user_phones USR_PHONE,users USR, users PRNT_USR,users ONR_USR,categories USR_CAT, users MOD_USR, ");
     strBuff.append("categories ONR_CAT, categories  PRNT_CAT,lookups l,user_geographies UG, geographical_domains GD, users USR_CRBY, domains D ");
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

     strBuff.append("AND USR.parent_id=PRNT_USR.user_id(+) AND USR.owner_id=ONR_USR.user_id ");
     strBuff.append("AND USR.category_code=USR_CAT.category_code ");
     strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
     strBuff.append("AND PRNT_USR.category_code=PRNT_CAT.category_code(+) ");
     strBuff.append(" AND USR.status = l.lookup_code ");
     strBuff.append(" AND l.lookup_type= ? ");
     strBuff.append(" AND USR.user_id = UG.user_id ");
     strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
     strBuff.append(" AND MOD_USR.user_id(+) = USR.modified_by ");
     strBuff.append(" AND USR_CRBY.user_id(+) = USR.created_by ");
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
    strBuff.append("FROM user_phones USR_PHONE,users USR, users PRNT_USR,users ONR_USR,categories USR_CAT, users MOD_USR, CHANNEL_USERS chnl_usr, ");
    strBuff.append("categories ONR_CAT, categories  PRNT_CAT,lookups l,user_geographies UG, geographical_domains GD, users USR_CRBY, domains D ");
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

    strBuff.append("AND USR.parent_id=PRNT_USR.user_id(+) AND USR.owner_id=ONR_USR.user_id ");
    strBuff.append("AND USR.category_code=USR_CAT.category_code ");
    strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
    strBuff.append("AND PRNT_USR.category_code=PRNT_CAT.category_code(+) ");
    strBuff.append(" AND USR.status = l.lookup_code ");
    strBuff.append(" AND l.lookup_type= ? ");
    strBuff.append(" AND USR.user_id = UG.user_id ");
    strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
    strBuff.append(" AND MOD_USR.user_id(+) = USR.modified_by ");
    strBuff.append(" AND USR_CRBY.user_id(+) = USR.created_by ");
    strBuff.append(" AND USR_CAT.domain_code=D.domain_code ");
    strBuff.append(" AND chnl_usr.user_id=USR.user_id ");
    if (p_userID != null) {
        strBuff.append(" AND USR.user_id IN ( ");
        strBuff.append(" SELECT user_id from users where user_id != ? ");
        strBuff.append(" CONNECT BY PRIOR user_id = parent_id START WITH  user_id=? ) ");
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

    // for Zebra and Tango by sanjeew date 18/07/07
    strBuff.append(" USR_PHONE.access_type user_access_type ");
    // end of Zebra and Tango

    strBuff.append("FROM user_phones USR_PHONE,users USR, users PRNT_USR,users ONR_USR,categories USR_CAT, users MOD_USR, ");
    strBuff.append("categories ONR_CAT, categories  PRNT_CAT,lookups l,user_geographies UG, geographical_domains GD, users USR_CRBY, domains D ");
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

    strBuff.append("AND USR.parent_id=PRNT_USR.user_id(+) AND USR.owner_id=ONR_USR.user_id ");
    strBuff.append("AND USR.category_code=USR_CAT.category_code ");
    strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
    strBuff.append("AND PRNT_USR.category_code=PRNT_CAT.category_code(+) ");
    strBuff.append(" AND USR.status = l.lookup_code ");
    strBuff.append(" AND l.lookup_type= ? ");
    strBuff.append(" AND USR.user_id = UG.user_id ");
    strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
    strBuff.append(" AND MOD_USR.user_id(+) = USR.modified_by ");
    strBuff.append("AND USR_CRBY.user_id(+) = USR.created_by ");
    strBuff.append(" AND USR_CAT.domain_code=D.domain_code AND USR.creation_type='S' and USR.LEVEL1_APPROVED_BY is null and USR.LEVEL1_APPROVED_ON is null ");
    if (p_userID != null) {
        strBuff.append(" AND USR.user_id IN ( ");
        strBuff.append(" SELECT user_id from users where user_id != ? ");
        strBuff.append(" CONNECT BY PRIOR user_id = parent_id START WITH  user_id=? ) ");
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
   strBuff.append(" USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed,"); strBuff.append("USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed,USR_CAT.restricted_msisdns, ");
   strBuff.append("PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn,MOD_USR.user_name request_user_name, ");
   strBuff.append("PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn, USR_CAT.category_type,");
   strBuff.append("ONR_CAT.category_name owner_cat,l.lookup_name, UG.grph_domain_code, GD.grph_domain_name, D.domain_type_code ");
   strBuff.append("FROM users USR, users PRNT_USR,users ONR_USR,categories USR_CAT,categories ONR_CAT,");
   strBuff.append("categories  PRNT_CAT,lookups l,user_geographies UG, geographical_domains GD,users MOD_USR, users USR_CRBY, domains D ");
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

   strBuff.append(" AND USR.parent_id=PRNT_USR.user_id(+) AND USR.owner_id=ONR_USR.user_id ");
   strBuff.append(" AND USR.category_code=USR_CAT.category_code ");
   strBuff.append(" AND ONR_CAT.category_code=ONR_USR.category_code");
   strBuff.append(" AND PRNT_USR.category_code=PRNT_CAT.category_code(+)"); 
    strBuff.append(" AND USR.status = l.lookup_code ");
   strBuff.append(" AND l.lookup_type= ? ");
   strBuff.append(" AND USR.user_id = UG.user_id ");
   strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
   strBuff.append(" AND MOD_USR.user_id(+) = USR.modified_by ");
   strBuff.append(" AND USR_CRBY.user_id(+) = USR.created_by ");
   strBuff.append(" AND USR_CAT.domain_code=D.domain_code");
   if (p_userID != null) {
       strBuff.append(" AND USR.user_id IN ( ");
       strBuff.append(" SELECT user_id from users where user_id != ? ");
       strBuff.append(" CONNECT BY PRIOR user_id = parent_id START WITH  user_id=? ) ");
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


@Override//todo
public PreparedStatement loadUserHierarchyListForTransfer(Connection con,String p_status,String p_statusUsed,String[] p_userId,String p_mode,String p_userCategory)throws SQLException{
	StringBuilder strBuff=new StringBuilder();
	PreparedStatement pstmt=null;
	String methodName="loadUserHierarchyListForTransfer";
	 strBuff.append("SELECT l,U.user_id_prefix,U.user_id, U.user_name, U.network_code, U.login_id, U.password, ");
     strBuff.append("U.category_code, U.parent_id, U.owner_id, U.allowed_ip, U.allowed_days, ");
     strBuff.append("U.from_time,U.to_time, U.last_login_on, U.employee_code, U.status, U.email, ");
     strBuff.append("U.pswd_modified_on, U.contact_person, U.contact_no, U.designation,U.company,U.fax,U.firstname,U.lastname, "); // added
     // by
     // deepika
     // aggarwal
     strBuff.append("U.division, U.department,U.msisdn, U.user_type, U.created_by, U.created_on, ");
     strBuff.append("U.modified_by, U.modified_on, U.address1, U.address2, U.city, U.state, ");
     strBuff.append("U.country, U.ssn, U.user_name_prefix, U.external_code,nvl(U.user_code,U.msisdn) user_code, U.short_name, ");
     strBuff.append("U.reference_id, invalid_password_count, level1_approved_by, ");
     strBuff.append("level1_approved_on, level2_approved_by, U.level2_approved_on, U.appointment_date, ");
     strBuff.append("U.password_count_updated_on,U.previous_status,L.lookup_name ");
     // tango implementation
     strBuff.append(",CU.application_id, CU.mpay_profile_id, CU.user_profile_id, ");
     strBuff.append("CU.mcommerce_service_allow, CU.low_bal_alert_allow,c.grph_domain_type,c.SEQUENCE_NO  ");

     strBuff.append("FROM ( SELECT level l,USR.* ,USR.category_code catcode,UCAT.user_id_prefix FROM users USR, categories UCAT  ");
     strBuff.append("WHERE ");
     if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
         strBuff.append("USR.status IN (" + p_status + ")");
     } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
         strBuff.append("USR.status NOT IN (" + p_status + ")");
     } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
         strBuff.append("USR.status =? ");
     } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
         strBuff.append("USR.status <> ? ");
     }
     int i = 1;
     strBuff.append(" AND UCAT.category_code=USR.category_code ");
     if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.STAFF_AS_USER))).booleanValue()) {
         strBuff.append(" AND (USR.user_type='CHANNEL' OR USR.user_type='STAFF' ) ");
     } else {
         strBuff.append(" AND USR.user_type='CHANNEL' ");
     }
     if (p_mode.equalsIgnoreCase(PretupsI.SINGLE)) {

         strBuff.append(" CONNECT BY PRIOR user_id = parent_id  ");
         strBuff.append(" START WITH user_id in (SELECT user_id FROM users WHERE user_id=? AND  ");
         strBuff.append(" category_code=? )) U,lookups L, channel_users CU, categories c  ");
         strBuff.append(" WHERE  L.lookup_code = U.status AND L.lookup_type=? AND CU.user_id=U.user_id AND c.category_code=U.category_code ");
         if (LOG.isDebugEnabled()) {
             LOG.debug(methodName, "QUERY sqlSelect=" + strBuff);
         }
         pstmt = con.prepareStatement(strBuff.toString());
         if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
             pstmt.setString(i, p_status);
             i++;
         }
         pstmt.setString(i, p_userId[0]);
         i++;
         pstmt.setString(i, p_userCategory);
         i++;
         pstmt.setString(i, PretupsI.USER_STATUS_TYPE);
        
     } else if (p_mode.equalsIgnoreCase(PretupsI.MULTIPLE)) {
         final StringBuilder str = new StringBuilder();
         for (int k = 0; k < p_userId.length; k++) {
             if (!BTSLUtil.isNullString(p_userId[k])) {
                 str.append("'");
                 str.append(p_userId[k]);
                 str.append("',");
             }
         }
         final String userID = str.substring(0, str.length() - 1);
         strBuff.append(" CONNECT BY PRIOR user_id = parent_id ");
         strBuff.append(" START WITH user_id in (SELECT user_id FROM users WHERE user_id in(" + userID + ") AND  ");
         strBuff.append(" category_code=? )) U,lookups L, channel_users CU, categories c ");
         strBuff.append(" WHERE  L.lookup_code = U.status AND L.lookup_type=? AND CU.user_id=U.user_id AND c.category_code=U.category_code ");

         if (LOG.isDebugEnabled()) {
             LOG.debug(methodName, "QUERY sqlSelect=" + strBuff);
         }
          pstmt = con.prepareStatement(strBuff.toString());
         if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
             pstmt.setString(i, p_status);
             i++;
         }
         pstmt.setString(i, p_userCategory);
         i++;
         pstmt.setString(i, PretupsI.USER_STATUS_TYPE);
        
     } else if (p_mode.equalsIgnoreCase(PretupsI.ALL)) {
         strBuff.append(" CONNECT BY PRIOR user_id = parent_id ");
         strBuff.append(" START WITH user_id in (SELECT user_id FROM users WHERE (parent_id=? OR user_id=? )AND  ");
         strBuff.append(" category_code=? )) U,lookups L, channel_users CU, categories c ");
         strBuff.append(" WHERE  L.lookup_code = U.status AND L.lookup_type=? AND CU.user_id=U.user_id AND c.category_code=U.category_code ORDER BY c.SEQUENCE_NO");
         if (LOG.isDebugEnabled()) {
             LOG.debug(methodName, "QUERY sqlSelect=" + strBuff);
         }
         pstmt = con.prepareStatement(strBuff.toString());
         if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
             pstmt.setString(i, p_status);
             i++;
         }
         pstmt.setString(i, p_userId[0]);
         i++;
         pstmt.setString(i, p_userId[0]);
         i++;
         pstmt.setString(i, p_userCategory);
         i++;
         pstmt.setString(i, PretupsI.USER_STATUS_TYPE);
         
     }
    
	  
     return pstmt;
     
}


@Override
public String loadCategoryUsersWithinGeoDomainHirearchy(String p_userName, boolean p_isLoginChannelUsr,String p_loginID,String p_msisdn,String p_userStatusIN){
	StringBuilder strBuff = new StringBuilder();
	strBuff.append(" SELECT DISTINCT U.user_id, U.user_name,U.msisdn FROM ");
    if (p_isLoginChannelUsr) {
        strBuff.append(" (SELECT U11.user_id FROM users U11 CONNECT BY PRIOR U11.user_id = U11.parent_id START WITH U11.user_id=?) X, ");
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
    strBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE (status = 'Y' OR status = 'S') ");
    strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
    strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code ");
    strBuff.append(" FROM user_geographies UG1 ");
    strBuff.append(" WHERE UG1.user_id = ? )) ");
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
	public String debitUserBalancesForRevTxnQry() {
		StringBuilder strBuffSelect = new StringBuilder();
		strBuffSelect.append(" SELECT balance,balance_type FROM user_balances");
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
			strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance with RS");
		} else {
			strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance");
		}
		return strBuffSelect.toString();
	}

@Override
public String debitUserBalancesForRevTxnDeleteQry(){
	StringBuilder strBuffSelect = new StringBuilder();
	strBuffSelect.append(" DELETE user_threshold_counter WHERE transfer_id=? AND user_id=?");

    return strBuffSelect.toString();
}

@Override
	public String creditUserBalancesForRevTxnQry() {
		StringBuilder strBuffSelect = new StringBuilder();
		strBuffSelect.append(" SELECT balance,balance_type FROM user_balances");
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
			strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance with RS");
		} else {
			strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance");
		}
		return strBuffSelect.toString();
	}

@Override
public String creditUserBalancesForRevTxnDeleteQry(){
	StringBuilder strBuffSelect = new StringBuilder();
	strBuffSelect.append(" DELETE user_threshold_counter WHERE transfer_id=? AND user_id=?");

    return strBuffSelect.toString();
}


@Override
public PreparedStatement userHierarchyQryByCategory(Connection con,String p_networkCode,String p_categoryCode,String p_loginUserID,String p_userName) throws SQLException{
	final StringBuilder strBuff = new StringBuilder(" SELECT U.user_id, U.user_name , U.msisdn, U.login_id FROM users U ");
    strBuff.append(" WHERE U.status = 'Y' AND  U.network_code = ? AND U.category_code = ? AND user_type='CHANNEL' ");
    strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?) ");
    strBuff.append(" CONNECT BY PRIOR U.user_id=U.parent_id START WITH U.user_id= ? ");
    strBuff.append(" ORDER BY U.user_name");

    final String sqlSelect = strBuff.toString();
    if (LOG.isDebugEnabled()) {
        LOG.debug("userHierarchyQryByCategory", "QUERY sqlSelect=" + sqlSelect);
    }
    PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);
    int i = 0;
    ++i;
    pstmtSelect.setString(i, p_networkCode);
    ++i;
    pstmtSelect.setString(i, p_categoryCode);
    ++i;
    pstmtSelect.setString(i, p_userName);
    ++i;
    pstmtSelect.setString(i, p_loginUserID);
    return pstmtSelect;

}


@Override
public PreparedStatement loadCategoryUsersWithinGeoDomainHirearchyQryForAutoO2C(Connection con, String categoryCode,
		String geographicalDomainCode, String userName, String loginUserID) throws SQLException {
	
	StringBuilder strBuff = new StringBuilder();
	strBuff.append(" SELECT DISTINCT U.user_id, U.user_name,U.msisdn FROM users U LEFT OUTER JOIN Lookups L ON L.lookup_type = 'URTYP' AND L.lookup_code = u.STATUS ");
	strBuff.append(" LEFT OUTER JOIN USER_PHONES up ON u.user_id = up.user_id  AND up.PRIMARY_NUMBER = 'Y' LEFT OUTER JOIN users crby ON crby.user_id = u.CREATED_BY ");
	strBuff.append(" LEFT OUTER JOIN USER_GEOGRAPHIES ug ON ug.user_id = u.user_id where UPPER(u.user_name) like UPPER(?) ");
	strBuff.append(" AND u.status IN ('PA', 'Y', 'CH', 'DE', 'S') AND u.category_code = CASE  WHEN ? = 'ALL' THEN u.category_code ELSE ? END ");
	strBuff.append(" AND ug.grph_domain_code IN (WITH q AS (SELECT g1.grph_domain_code,g1.status FROM geographical_domains g1 INNER JOIN ");
    strBuff.append(" geographical_domains g2 ON g2.grph_domain_code = g1.parent_grph_domain_code CONNECT BY PRIOR g1.grph_domain_code = ");
    strBuff.append(" g1.parent_grph_domain_code start WITH  g1.grph_domain_code IN (SELECT grph_domain_code FROM user_geographies ug1 ");
    strBuff.append(" WHERE ug1.grph_domain_code = CASE WHEN ? = 'ALL' THEN ug1.grph_domain_code ELSE ? END AND ug1.user_id = ? ))");
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

