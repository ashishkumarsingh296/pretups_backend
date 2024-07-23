package com.btsl.db.query.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.common.TypesI;
import com.btsl.pretups.common.PretupsI;
import com.web.pretups.loyaltymgmt.businesslogic.ActivationBonusLMSWebQry;

public class ActivationBonusLMSWebOracleQry implements ActivationBonusLMSWebQry{
	@Override
	public String loadpromotionListQry(){
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT distinct ps.set_id,ps.set_name ");
        strBuff.append(" from profile_set ps,profile_set_version psv ");
        strBuff.append(" where psv.status =ps.status and ps.status in (?) and ps.profile_type=? and ps.set_id=psv.set_id and psv.applicable_to >=sysdate and network_code=? ");
        return strBuff.toString();
	}
	@Override
	public PreparedStatement loadUserForLMSAssociationQry (Connection pcon,String pdomainCode, String pgeographyCode, String pcategorycode, String puserid,String pgradeCode) throws SQLException{
		 PreparedStatement pstmt;
		 final StringBuilder strBuff = new StringBuilder();
         strBuff.append("SELECT distinct(U.user_id)  ,UP.msisdn,PS.set_name, CU.CONTROL_GROUP ");
         strBuff.append(" FROM users U, user_geographies UG, user_phones UP, channel_users CU, categories C, domains D,PROFILE_SET PS");
         strBuff.append(" WHERE  U.category_code=C.category_code AND C.domain_code=D.domain_code");
         strBuff.append(" AND U.user_id=UG.user_id AND U.user_id=CU.user_id AND U.status IN(?,?) ");
         strBuff.append(" AND U.user_type =? AND UP.user_id(+)=U.user_id");
         if(!PretupsI.ALL.equals(pcategorycode)){
         	strBuff.append(" AND U.category_code=? ");
         }
         strBuff.append(" AND UP.primary_number(+)=?");
     	strBuff.append(" AND C.domain_code =? ");
         if (!(pgradeCode.equalsIgnoreCase(PretupsI.ALL))) {
             strBuff.append(" AND CU.user_grade=? ");
         }
         strBuff.append("and PS.set_id(+)=CU.LMS_PROFILE AND UG.grph_domain_code IN (SELECT grph_domain_code FROM ");
         strBuff.append(" geographical_domains GD1 WHERE status IN(?, ?) ");
         strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN");
         strBuff
             .append(" (SELECT grph_domain_code FROM user_geographies ug1 WHERE UG1.grph_domain_code =DECODE(?, '" + PretupsI.ALL + "', UG1.grph_domain_code, ?)  AND UG1.user_id=? ))");
         final String sqlSelect = strBuff.toString();
         if (LOG.isDebugEnabled()) {
             LOG.debug("loadBatchUserListForModify", "QUERY sqlSelect=" + sqlSelect);
         }
         int i = 1;
         pstmt = pcon.prepareStatement(sqlSelect);
         pstmt.setString(i, PretupsI.USER_STATUS_ACTIVE);
         i++;
         pstmt.setString(i, PretupsI.USER_STATUS_SUSPEND);
         i++;
         pstmt.setString(i, TypesI.CHANNEL_USER_TYPE);
         i++;
         if(!PretupsI.ALL.equals(pcategorycode)){
         	pstmt.setString(i, pcategorycode);
         	i++;
         }
         pstmt.setString(i, PretupsI.YES);
         i++;
         pstmt.setString(i,pdomainCode);
         i++;
         if (!(pgradeCode.equalsIgnoreCase(PretupsI.ALL))) {
             pstmt.setString(i, pgradeCode);
             i++;
         }
         pstmt.setString(i, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
         i++;
         pstmt.setString(i, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND);
         i++;
         pstmt.setString(i, pgeographyCode);
         i++;
         pstmt.setString(i, pgeographyCode);
         i++;
         pstmt.setString(i, puserid);
        return pstmt;
	}
	@Override
	public String isprofileExpiredQry(){
		final StringBuilder sbf = new StringBuilder();
        sbf.append("select set_id from PROFILE_SET_VERSION where set_id=? and version=? and applicable_to>=SYSDATE ");
        return sbf.toString();
	}
	@Override
	public PreparedStatement loadMapForLMSAssociationQry(Connection pcon, String pgeographyCode, String pcategorycode, String puserid, String pgradeCode) throws SQLException {
		 final StringBuilder strBuff = new StringBuilder();
		 PreparedStatement pstmt = null;
         strBuff.append("SELECT distinct(U.user_id)  ,UP.msisdn,PS.set_name");
         strBuff.append(" FROM users U, user_geographies UG, user_phones UP, channel_users CU, categories C, domains D,PROFILE_SET PS");
         strBuff.append(" WHERE  U.category_code=C.category_code AND C.domain_code=D.domain_code");
         strBuff.append(" AND U.user_id=UG.user_id AND U.user_id=CU.user_id AND U.status IN(?,?) ");
         strBuff.append(" AND U.user_type =? AND U.category_code=? AND UP.user_id(+)=U.user_id");
         strBuff.append(" AND UP.primary_number(+)=?");
         if (!(pgradeCode.equalsIgnoreCase(PretupsI.ALL))) {
             strBuff.append(" AND CU.user_grade=? ");
         }
         strBuff.append("and PS.set_id(+)=CU.LMS_PROFILE AND UG.grph_domain_code IN (SELECT grph_domain_code FROM ");
         strBuff.append(" geographical_domains GD1 WHERE status IN(?, ?) ");
         strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN");
         strBuff
             .append(" (SELECT grph_domain_code FROM user_geographies ug1 WHERE UG1.grph_domain_code =DECODE(?, '" + PretupsI.ALL + "', UG1.grph_domain_code, ?)  AND UG1.user_id=? ))");
         final String sqlSelect = strBuff.toString();
         if (LOG.isDebugEnabled()) {
             LOG.debug("loadBatchUserListForModify", "QUERY sqlSelect=" + sqlSelect);
         }
         int i = 1;
         pstmt = pcon.prepareStatement(sqlSelect);
         pstmt.setString(i, PretupsI.USER_STATUS_ACTIVE);
         i++;
         pstmt.setString(i, PretupsI.USER_STATUS_SUSPEND);
         i++;
         pstmt.setString(i, TypesI.CHANNEL_USER_TYPE);
         i++;
         pstmt.setString(i, pcategorycode);
         i++;
         pstmt.setString(i, PretupsI.YES);
         i++;
         if (!(pgradeCode.equalsIgnoreCase(PretupsI.ALL))) {
             pstmt.setString(i, pgradeCode);
             i++;
         }
         pstmt.setString(i, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
         i++;
         pstmt.setString(i, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND);
         i++;
         pstmt.setString(i, pgeographyCode);
         i++;
         pstmt.setString(i, pgeographyCode);
         i++;
         pstmt.setString(i, puserid);
         return pstmt;
	}
	@Override
	public String loadLmsProfileCacheQry(){
		final StringBuilder selectQueryBuff = new StringBuilder("SELECT   psv.set_id, psv.version,psv.applicable_from");
        selectQueryBuff.append(" FROM PROFILE_SET_VERSION psv ");
        selectQueryBuff.append(" WHERE applicable_from >= trunc(?)  OR applicable_from >= ");
        selectQueryBuff.append(" (SELECT   MAX (lpsv.applicable_from) FROM PROFILE_SET_VERSION lpsv ");
        selectQueryBuff.append(" where lpsv.applicable_from <=trunc(?)   and psv.set_id=lpsv.set_id group by lpsv.set_id) ORDER BY psv.set_id DESC ");
        return selectQueryBuff.toString();
	}
	@Override
	public PreparedStatement loadUserForLMSAssociationQuery(Connection pcon,String pdomainCode, String pgeographyCode, String pcategorycode, String puserid,String pgradeCode) throws SQLException{
		final StringBuilder strBuff = new StringBuilder();
		PreparedStatement pstmt = null;
        strBuff.append("SELECT distinct(U.user_id), UP.msisdn, PS.set_name, CU.CONTROL_GROUP ");
        strBuff.append(" FROM users U, user_geographies UG, user_phones UP, channel_users CU, categories C, domains D,PROFILE_SET PS");
        strBuff.append(" WHERE  U.category_code=C.category_code AND C.domain_code=D.domain_code");
        strBuff.append(" AND U.user_id=UG.user_id AND U.user_id=CU.user_id AND U.status IN(?,?) ");
        strBuff.append(" AND U.user_type =? ");
        if (!(pcategorycode.equalsIgnoreCase(PretupsI.ALL))) {
        	strBuff.append(" AND U.category_code=?"); 
        }
        strBuff.append(" AND UP.user_id(+)=U.user_id");
        strBuff.append(" AND UP.primary_number(+)=?");
        strBuff.append(" AND C.domain_code =? ");
        if (!(pgradeCode.equalsIgnoreCase(PretupsI.ALL))) {
            strBuff.append(" AND CU.user_grade=? ");
        }
        strBuff.append("and PS.set_id(+)=CU.LMS_PROFILE AND UG.grph_domain_code IN (SELECT grph_domain_code FROM ");
        strBuff.append(" geographical_domains GD1 WHERE status IN(?, ?) ");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN");
        strBuff
            .append(" (SELECT grph_domain_code FROM user_geographies ug1 WHERE UG1.grph_domain_code =DECODE(?, '" + PretupsI.ALL + "', UG1.grph_domain_code, ?)  AND UG1.user_id=? ))");
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadBatchUserListForModify", "QUERY sqlSelect=" + sqlSelect);
        }
        int i = 1;
        pstmt = pcon.prepareStatement(sqlSelect);
        pstmt.setString(i, PretupsI.USER_STATUS_ACTIVE);
        i++;
        pstmt.setString(i, PretupsI.USER_STATUS_SUSPEND);
        i++;
        pstmt.setString(i, TypesI.CHANNEL_USER_TYPE);
        i++;
        if(!(pcategorycode.equalsIgnoreCase(PretupsI.ALL))){
        	pstmt.setString(i, pcategorycode);
        	i++;
        }
        pstmt.setString(i, PretupsI.YES);
        i++;
        pstmt.setString(i,pdomainCode);
        i++;
        if (!(pgradeCode.equalsIgnoreCase(PretupsI.ALL))) {
            pstmt.setString(i, pgradeCode);
            i++;
        }
        pstmt.setString(i, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
        i++;
        pstmt.setString(i, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND);
        i++;
        pstmt.setString(i, pgeographyCode);
        i++;
        pstmt.setString(i, pgeographyCode);
        i++;
        pstmt.setString(i, puserid);
        return pstmt;
	}
	@Override
	public String isControlledProfileAlreadyAssociatedQry(){
		final StringBuilder selectQueryBuff = new StringBuilder("SELECT u.user_id,cu.lms_profile, ps.promotion_type ");
        selectQueryBuff.append("  FROM users u, channel_users cu, profile_set_version psv, profile_set ps ");
        selectQueryBuff.append("  WHERE u.user_id = cu.user_id ");
        selectQueryBuff.append("  AND u.status IN ('Y', 'S') ");
        selectQueryBuff.append("  AND cu.lms_profile is not null ");
        selectQueryBuff.append("  AND cu.CONTROL_GROUP = ? ");
        selectQueryBuff.append("  AND ps.set_id = psv.set_id ");
        selectQueryBuff.append("  AND cu.lms_profile = ps.set_id ");
        selectQueryBuff.append("  AND psv.status IN ('Y', 'S') ");
        selectQueryBuff.append("  AND ps.promotion_type in ( ?, ?) ");
        selectQueryBuff.append("  AND trunc(psv.applicable_from) <=trunc(SYSDATE) ");
        selectQueryBuff.append("  AND trunc(psv.applicable_to) >=trunc(SYSDATE) ");
        selectQueryBuff.append("  AND u.msisdn = ? ");
        return selectQueryBuff.toString();
	}
	@Override
	public String isProfileActiveQry(){
		final StringBuilder selectQueryBuff = new StringBuilder("SELECT count(*) AS total ");
        selectQueryBuff.append("  FROM profile_set_version psv, profile_set ps ");
        selectQueryBuff.append("  WHERE ps.set_id = psv.set_id ");
        selectQueryBuff.append("  AND ps.set_id = ? ");
        selectQueryBuff.append("  AND psv.status IN ('Y', 'S') ");
        selectQueryBuff.append("  AND ps.promotion_type in ( ?, ?) ");
        selectQueryBuff.append("  AND trunc(psv.applicable_from) <=trunc(SYSDATE) ");
        selectQueryBuff.append("  AND trunc(psv.applicable_to)>=trunc(SYSDATE) ");
        return selectQueryBuff.toString(); 
	}
}
