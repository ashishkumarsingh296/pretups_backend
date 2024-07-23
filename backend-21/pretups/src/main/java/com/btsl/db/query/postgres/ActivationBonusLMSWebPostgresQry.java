package com.btsl.db.query.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.common.TypesI;
import com.btsl.pretups.common.PretupsI;
import com.web.pretups.loyaltymgmt.businesslogic.ActivationBonusLMSWebQry;

public class ActivationBonusLMSWebPostgresQry implements ActivationBonusLMSWebQry{
	@Override
	public String loadpromotionListQry(){
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT distinct ps.set_id,ps.set_name ");
        strBuff.append(" from profile_set ps,profile_set_version psv ");
        strBuff.append(" where psv.status =ps.status and ps.status in (?) and ps.profile_type=? and ps.set_id=psv.set_id and psv.applicable_to >=current_timestamp and network_code=? ");
        return strBuff.toString();
	}
		
	@Override
	public PreparedStatement loadUserForLMSAssociationQry (Connection pcon,String pdomainCode, String pgeographyCode, String pcategorycode, String puserid,String pgradeCode) throws SQLException{
		final StringBuilder strBuff = new StringBuilder();
		PreparedStatement pstmt = null;
        strBuff.append("SELECT distinct(U.user_id)  ,UP.msisdn,PS.set_name, CU.CONTROL_GROUP ");
        strBuff.append(" FROM  user_geographies UG, user_phones UP right outer join users U on (UP.user_id=U.user_id AND UP.primary_number=? ),  categories C, domains D ");
        strBuff.append(" ,PROFILE_SET PS right outer join channel_users CU on PS.set_id=CU.LMS_PROFILE");
        strBuff.append(" WHERE  U.category_code=C.category_code AND C.domain_code=D.domain_code");
        strBuff.append(" AND U.user_id=UG.user_id AND U.user_id=CU.user_id AND U.status IN(?,?) ");
        strBuff.append(" AND U.user_type =?");
        if(!PretupsI.ALL.equals(pcategorycode)){
        	strBuff.append(" AND U.category_code=? ");
        }
    	strBuff.append(" AND C.domain_code =? ");
        if (!(pgradeCode.equalsIgnoreCase(PretupsI.ALL))) {
            strBuff.append(" AND CU.user_grade=? ");
        }
        strBuff.append(" AND UG.grph_domain_code IN ( with recursive q as (SELECT grph_domain_code, status FROM  geographical_domains GD1 where  grph_domain_code IN ");
        strBuff.append(" (SELECT grph_domain_code FROM user_geographies ug1 WHERE UG1.grph_domain_code = case ? when  '" + PretupsI.ALL + "' then  UG1.grph_domain_code else ? end  ");
        strBuff.append(" AND UG1.user_id=? ) union all  select m.grph_domain_code , m.status from geographical_domains m  ");
        strBuff.append(" join q on q.grph_domain_code=m.parent_grph_domain_code )select grph_domain_code from q  WHERE status IN(?, ?)  )");
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadBatchUserListForModify", "QUERY sqlSelect=" + sqlSelect);
        }
        int i = 1;
        pstmt = pcon.prepareStatement(sqlSelect);
        pstmt.setString(i++, PretupsI.YES);
        pstmt.setString(i++, PretupsI.USER_STATUS_ACTIVE);
        pstmt.setString(i++, PretupsI.USER_STATUS_SUSPEND);
        pstmt.setString(i++, TypesI.CHANNEL_USER_TYPE);
        if(!PretupsI.ALL.equals(pcategorycode)){
        	pstmt.setString(i++, pcategorycode);
        } 
        pstmt.setString(i++,pdomainCode);
        if (!(pgradeCode.equalsIgnoreCase(PretupsI.ALL))) {
            pstmt.setString(i++, pgradeCode);
        }
        pstmt.setString(i++, pgeographyCode);
        pstmt.setString(i++, pgeographyCode);
        pstmt.setString(i++, puserid);
        pstmt.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
        pstmt.setString(i, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND);
        
        return pstmt;
	}
	@Override
	public String isprofileExpiredQry(){
		final StringBuilder sbf = new StringBuilder();
        sbf.append("select set_id from PROFILE_SET_VERSION where set_id=? and version=? and applicable_to>=CURRENT_TIMESTAMP ");
        return sbf.toString();
	}
	@Override
	public PreparedStatement loadMapForLMSAssociationQry(Connection pcon, String pgeographyCode, String pcategorycode, String puserid, String pgradeCode) throws SQLException {
		 final StringBuilder strBuff = new StringBuilder();
		 PreparedStatement pstmt = null;
         strBuff.append("SELECT distinct(U.user_id)  ,UP.msisdn,PS.set_name");
         strBuff.append(" FROM  user_geographies UG, user_phones UP right outer join users U on (UP.user_id=U.user_id AND UP.primary_number=? ),  categories C, domains D, ");
         strBuff.append(" PROFILE_SET PS right outer join channel_users CU on PS.set_id=CU.LMS_PROFILE  ");
         strBuff.append(" WHERE  U.category_code=C.category_code AND C.domain_code=D.domain_code");
         strBuff.append(" AND U.user_id=UG.user_id AND U.user_id=CU.user_id AND U.status IN(?,?) ");
         strBuff.append(" AND U.user_type =? AND U.category_code=? ");
         if (!(pgradeCode.equalsIgnoreCase(PretupsI.ALL))) {
             strBuff.append(" AND CU.user_grade=? ");
         }
         strBuff.append(" AND UG.grph_domain_code IN ( with recursive q as (SELECT grph_domain_code, status FROM  geographical_domains GD1 WHERE grph_domain_code IN ");
        strBuff.append(" (SELECT grph_domain_code FROM user_geographies ug1 WHERE UG1.grph_domain_code = case ? when  '" + PretupsI.ALL + "' then  UG1.grph_domain_code else ? end  ");
        strBuff.append(" AND UG1.user_id=? ) union all  select m.grph_domain_code, m.status from geographical_domains m  ");
        strBuff.append(" join q on q.grph_domain_code=m.parent_grph_domain_code )select grph_domain_code from q where status IN(?, ?)  )");
         final String sqlSelect = strBuff.toString();
         if (LOG.isDebugEnabled()) {
             LOG.debug("loadBatchUserListForModify", "QUERY sqlSelect=" + sqlSelect);
         }
         int i = 1;
         pstmt = pcon.prepareStatement(sqlSelect);
         pstmt.setString(i++, PretupsI.YES);
         pstmt.setString(i++, PretupsI.USER_STATUS_ACTIVE);
         pstmt.setString(i++, PretupsI.USER_STATUS_SUSPEND);
         pstmt.setString(i++, TypesI.CHANNEL_USER_TYPE);
         pstmt.setString(i++, pcategorycode);
        
         if (!(pgradeCode.equalsIgnoreCase(PretupsI.ALL))) {
             pstmt.setString(i++, pgradeCode);
         }
         pstmt.setString(i++, pgeographyCode);
         pstmt.setString(i++, pgeographyCode);
         pstmt.setString(i++, puserid);
         pstmt.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
         pstmt.setString(i, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND);
         return pstmt;
	}
	@Override
	public String loadLmsProfileCacheQry(){
		final StringBuilder selectQueryBuff = new StringBuilder("SELECT   psv.set_id, psv.version,psv.applicable_from");
        selectQueryBuff.append(" FROM PROFILE_SET_VERSION psv ");
        selectQueryBuff.append(" WHERE applicable_from >= date_trunc('day',?::TIMESTAMP)  OR applicable_from >= ");
        selectQueryBuff.append(" (SELECT   MAX (lpsv.applicable_from) FROM PROFILE_SET_VERSION lpsv ");
        selectQueryBuff.append(" where lpsv.applicable_from <=date_trunc('day',?::TIMESTAMP)   and psv.set_id=lpsv.set_id group by lpsv.set_id) ORDER BY psv.set_id DESC ");
        return selectQueryBuff.toString();
	}
	@Override
	public PreparedStatement loadUserForLMSAssociationQuery(Connection pcon,String pdomainCode, String pgeographyCode, String pcategorycode, String puserid,String pgradeCode) throws SQLException{
		final StringBuilder strBuff = new StringBuilder();
		PreparedStatement pstmt = null;
        strBuff.append(" SELECT distinct(U.user_id), UP.msisdn, PS.set_name, CU.CONTROL_GROUP ");
        strBuff.append(" FROM  user_geographies UG, user_phones UP right outer join users U on (UP.user_id=U.user_id and UP.primary_number=?) ,  categories C, domains D,");
        strBuff.append("  PROFILE_SET PS right outer join channel_users CU on PS.set_id=CU.LMS_PROFILE");
        strBuff.append(" WHERE  U.category_code=C.category_code AND C.domain_code=D.domain_code");
        strBuff.append(" AND U.user_id=UG.user_id AND U.user_id=CU.user_id AND U.status IN(?,?) ");
        strBuff.append(" AND U.user_type =? ");
        if (!(pcategorycode.equalsIgnoreCase(PretupsI.ALL))) {
        	strBuff.append(" AND U.category_code=?"); 
        }
        strBuff.append(" AND C.domain_code =? ");
        if (!(pgradeCode.equalsIgnoreCase(PretupsI.ALL))) {
            strBuff.append(" AND CU.user_grade=? ");
        }
        strBuff.append(" AND UG.grph_domain_code IN (with recursive q as (SELECT grph_domain_code, status FROM  geographical_domains GD1 WHERE grph_domain_code IN ");
        strBuff.append(" (SELECT grph_domain_code FROM user_geographies ug1 WHERE UG1.grph_domain_code = case ? when  '" + PretupsI.ALL + "' then  UG1.grph_domain_code else ? end  ");
        strBuff.append(" AND UG1.user_id=? ) union all  select m.grph_domain_code, m.status from geographical_domains m  ");
        strBuff.append(" join q on q.grph_domain_code=m.parent_grph_domain_code )select grph_domain_code from q  where  status IN(?, ?) )");
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadBatchUserListForModify", "QUERY sqlSelect=" + sqlSelect);
        }
        int i = 1;
        pstmt = pcon.prepareStatement(sqlSelect);
        pstmt.setString(i++, PretupsI.YES);
        pstmt.setString(i++, PretupsI.USER_STATUS_ACTIVE);
        pstmt.setString(i++, PretupsI.USER_STATUS_SUSPEND);
        pstmt.setString(i++, TypesI.CHANNEL_USER_TYPE);
        if(!(pcategorycode.equalsIgnoreCase(PretupsI.ALL))){
        	pstmt.setString(i++, pcategorycode);
        }
        pstmt.setString(i++,pdomainCode);
        if (!(pgradeCode.equalsIgnoreCase(PretupsI.ALL))) {
            pstmt.setString(i++, pgradeCode);
        }
        pstmt.setString(i++, pgeographyCode);
        pstmt.setString(i++, pgeographyCode);
        pstmt.setString(i++, puserid);
        pstmt.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
        pstmt.setString(i, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND);
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
        selectQueryBuff.append("  AND date_trunc('day',psv.applicable_from::TIMESTAMP) <=date_trunc('day',CURRENT_TIMESTAMP::TIMESTAMP) ");
        selectQueryBuff.append("  AND date_trunc('day',psv.applicable_to::TIMESTAMP) >= date_trunc('day',CURRENT_TIMESTAMP::TIMESTAMP) ");
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
        selectQueryBuff.append("  AND date_trunc('day',psv.applicable_from::TIMESTAMP) <=date_trunc('day',CURRENT_TIMESTAMP::TIMESTAMP) ");
        selectQueryBuff.append("  AND date_trunc('day',psv.applicable_to::TIMESTAMP)>=date_trunc('day',CURRENT_TIMESTAMP::TIMESTAMP) ");
        return selectQueryBuff.toString(); 
	}
}
