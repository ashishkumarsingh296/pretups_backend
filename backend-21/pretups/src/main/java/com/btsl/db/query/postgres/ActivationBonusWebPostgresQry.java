package com.btsl.db.query.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.db.query.oracle.ActivationBonusWebOracleQry;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.profile.businesslogic.ActivationBonusWebQry;

public class ActivationBonusWebPostgresQry implements ActivationBonusWebQry{
	private Log log = LogFactory.getLog(ActivationBonusWebOracleQry.class.getName());
	@Override
	public String searchSubscriberMappingQry(){
	   StringBuilder strBuff = new StringBuilder();
	  strBuff.append(" SELECT A.subscriber_msisdn ,A.user_id,U.msisdn,U.user_name,A.registered_on,A.subscriber_type,A.set_id, ");
	  strBuff.append("	A.version,A.expiry_date,A.network_code,A.created_by,A.created_on ");
	  strBuff.append(" ,P.user_name parentName,P.msisdn parentMsisdn,O.user_name ownerName,O.msisdn ownerMsisdn ");
	  strBuff.append(" FROM USERS P right outer join USERS U on P.user_id=U.parent_id, ACT_BONUS_SUBS_MAPPING A,USERS O ");
	  strBuff.append(" WHERE A.network_code = ? AND A.user_id=U.user_id ");
	  strBuff.append(" AND U.owner_id=O.user_id ");
	  strBuff.append(" AND A.subscriber_msisdn=? AND A.status=? ");
	  return strBuff.toString();
	}
	
	@Override
	public String searchNewRetailerQry(){
		 StringBuilder strBuff = new StringBuilder();
		  strBuff.append("SELECT U.msisdn,U.user_name,U.user_id ");
		  strBuff.append(" ,P.user_name parentName,P.msisdn parentMsisdn,O.user_name ownerName,O.msisdn ownerMsisdn ");
		  strBuff.append("  FROM USERS P right outer join USERS U on P.user_id=U.parent_id ,USERS O ");
		  strBuff.append(" WHERE U.network_code = ? AND U.msisdn=? ");
		  strBuff.append("   AND U.owner_id=O.user_id    AND U.status =? AND U.user_type=? 	");
		 return strBuff.toString();
	}
	
	@Override
	public String retailerSubsMappListQry(){
		  StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT  A.subscriber_msisdn,A.registered_on,U.msisdn,U.user_name,A.subscriber_type,A.expiry_date,A.status,L.lookup_name,A.approved_on  ");
       strBuff.append(" FROM ACT_BONUS_SUBS_MAPPING A, USERS U,LOOKUPS L ");
       strBuff.append(" WHERE A.user_id=U.user_id ");
       strBuff.append(" AND L.LOOKUP_TYPE=? ");
       strBuff.append(" AND L.lookup_code=A.status ");
       strBuff.append(" AND U.msisdn=? ");
       strBuff.append(" AND date_trunc('day',A.registered_on::TIMESTAMP) >=? ");
       strBuff.append(" AND date_trunc('day',A.registered_on::TIMESTAMP)<=? ");
       return strBuff.toString();
	}
	
	@Override
	public PreparedStatement viewRedemptionenquiryDetailsQry(Connection con,String domainCode,String categoryCode,String userId,String zoneCode,String fromdate,String todate) throws SQLException, ParseException{
		String methodName="viewRedemptionenquiryDetailsQry";
		StringBuilder selectQuery = new StringBuilder();
        selectQuery.append("SELECT U.user_id,U.category_code,C.domain_code,U.user_name, R.redemption_date ");
        selectQuery.append(" ,R.amount_transfered,U.msisdn,R.points_redeemed FROM redemptions R ,users U ,");
        selectQuery.append(" user_geographies UG,Categories C  WHERE  U.user_id=R.user_id_or_msisdn AND C.domain_code=? and ");
        selectQuery.append(" C.category_code= (CASE WHEN ?='ALL' then C.category_code ELSE ? END ) ");
        selectQuery.append(" AND C.category_code=U.category_code AND U.user_id=(CASE WHEN ?='ALL' then U.user_id ELSE ? END)");
        selectQuery.append("  AND U.user_id=UG.user_id and ug.GRPH_DOMAIN_CODE in (");
        selectQuery.append("	with recursive q as (");
        selectQuery.append(" select GRPH_DOMAIN_CODE, status from GEOGRAPHICAL_DOMAINS where GRPH_DOMAIN_CODE=?");
        selectQuery.append(" union all select m.grph_domain_code, m.status from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code ");
        selectQuery.append("	)select GRPH_DOMAIN_CODE from q  where status IN (?,?) )");
        selectQuery.append(" AND date_trunc('day',R.redemption_date::TIMESTAMP) >=?");
        selectQuery.append(" AND  date_trunc('day',R.redemption_date::TIMESTAMP) <=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Query=" + selectQuery.toString());
        }
        PreparedStatement pstmtSelect;
        pstmtSelect = con.prepareStatement(selectQuery.toString());
        pstmtSelect.setString(1, domainCode);
        pstmtSelect.setString(2, categoryCode);
        pstmtSelect.setString(3, categoryCode);
        pstmtSelect.setString(4, userId);
        pstmtSelect.setString(5, userId);
        pstmtSelect.setString(6, zoneCode);
        pstmtSelect.setString(7, PretupsI.STATUS_ACTIVE);
        pstmtSelect.setString(8, PretupsI.STATUS_SUSPEND);
        pstmtSelect.setDate(9, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(fromdate)));
        pstmtSelect.setDate(10, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(todate)));

        return pstmtSelect;
	}
	
	@Override
	public String loadProfileMappingListForDeleteQry()  {
		StringBuilder strBuff = new StringBuilder("SELECT ABSM.subscriber_msisdn,");
    strBuff.append(" U1.user_name ch_user_name ,U1.msisdn ch_user_msisdn,");
    strBuff.append(" U2.user_name p_user_name,U2.msisdn p_user_msisdn, ");
    strBuff.append(" U3.user_name o_user_name,U3.msisdn o_user_msisdn, ");
    strBuff.append(" ABSM.registered_on ,U1.user_id ch_user_id ");
    strBuff.append(" FROM USERS U2 right outer join USERS U1 on U2.user_id=U1.parent_id , ACT_BONUS_SUBS_MAPPING ABSM, USERS U3 ");
    strBuff.append("   WHERE U1.user_id=ABSM.user_id  ");
    strBuff.append(" AND U3.user_id=U1.owner_id AND ABSM.subscriber_msisdn=? ");
    strBuff.append( " AND U1.network_code=? AND ABSM.status='Y' ORDER BY ABSM.subscriber_msisdn ");
    
    return strBuff.toString();
	}
	
	@Override
	public String populateListFromTableQry(){
		  StringBuilder strBuff = new StringBuilder();
		    strBuff.append("SELECT absm.SUBSCRIBER_MSISDN, absm.USER_ID, absm.MODIFIED_ON, absm.MODIFIED_BY, u.MSISDN Retailer_Msisdn,");
	        strBuff.append(" u.USER_NAME Retailer_User_Name, u1.MSISDN Parent_Msisdn, u1.USER_NAME Parent_User_Name,");
	        strBuff.append(" u2.MSISDN Owner_Msisdn, u2.USER_NAME Owner_User_Name, absm.REGISTERED_ON, absm.STATUS ");
	        strBuff.append(" FROM USERS u1 right outer join USERS u on  u1.USER_ID=u.PARENT_ID , ACT_BONUS_SUBS_MAPPING absm, USERS u2 WHERE u.USER_ID=absm.USER_ID ");
	        strBuff.append(" AND absm.NETWORK_CODE=? AND u2.USER_ID=u.OWNER_ID AND absm.STATUS IN ('S','W')");
	        strBuff.append(" order by absm.STATUS DESC");
	        
	        return strBuff.toString();
	}
	
	@Override
	public PreparedStatement searchNewRetailerProfileQry(Connection con,String retailerID) throws SQLException{
		 String methodName ="searchNewRetailerProfileQry";
		 StringBuilder strBuff = new StringBuilder();
         strBuff.append(" SELECT (CASE WHEN uo.set_id IS NULL THEN p.set_id ELSE uo.set_id END) setId ");
         strBuff.append(" FROM USERS u left outer join USER_OTH_PROFILES uo on (u.user_id =uo.user_id  AND  uo.profile_type=?) ");
         strBuff.append(" left outer join PROFILE_MAPPING p on (u.category_code = p.srv_class_or_category_code AND u.network_code= p.network_code  AND p.profile_type=?");
         strBuff.append(" AND p.is_default=? ) WHERE u.user_id=?	");
         String sqlSelect =strBuff.toString();
         PreparedStatement pstmt = con.prepareStatement(sqlSelect);
         if (log.isDebugEnabled()) {
             log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
         }
         pstmt.setString(1, PretupsI.PROFILE_TYPE_ACTIVATION);
         pstmt.setString(2, PretupsI.PROFILE_TYPE_ACTIVATION);
         pstmt.setString(3, PretupsI.PROFILE_STATUS_ACTIVE);
         pstmt.setString(4, retailerID);
        return pstmt;
	}
	
	
	
	@Override
	public String activeUsersListWithProfilesQry(){
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT uop.SET_ID,uop.PROFILE_TYPE, u.LOGIN_ID, u.MSISDN, u.CATEGORY_CODE, c.CATEGORY_NAME FROM USER_OTH_PROFILES uop right outer join USERS u on ");
		strBuff.append(" uop.USER_ID=u.USER_ID, CATEGORIES c  WHERE 'ACT'= CASE WHEN uop.SET_ID IS NULL THEN 'ACT' ELSE uop.profile_type END");
		strBuff.append(" AND c.DOMAIN_CODE=? AND u.CATEGORY_CODE= CASE ? WHEN 'ALL' THEN c.CATEGORY_CODE ELSE ? END ");
		strBuff.append(" AND u.STATUS IN ('Y','PA') AND c.STATUS='Y' AND u.CATEGORY_CODE=c.CATEGORY_CODE AND u.NETWORK_CODE=? AND u.USER_TYPE<>?");
		return strBuff.toString();
	}
	
	 @Override
	public PreparedStatement validateUserdetailsQry(Connection con,String zoneCode,String userIdLoggedUser,String domainCode,String categoryCode,String channelUser) throws SQLException{
		 StringBuilder sqlBuff = new StringBuilder();
	        sqlBuff.append(" SELECT U.user_id,u.user_name FROM USERS U,CATEGORIES CAT,USER_GEOGRAPHIES UG ");
	        sqlBuff.append(" WHERE UG.user_id =u.user_id AND UG.GRPH_DOMAIN_CODE IN ");
	        sqlBuff.append(" (with recursive q as (");
	        sqlBuff.append(" SELECT grph_domain_code, status FROM GEOGRAPHICAL_DOMAINS where grph_domain_code IN ");
	        sqlBuff.append(" (SELECT grph_domain_code  FROM USER_GEOGRAPHIES UG1  WHERE UG1.grph_domain_code = (CASE WHEN ?='ALL' then UG1.grph_domain_code ELSE ? END) ");
	        sqlBuff.append(" AND UG1.user_id=? )");
	        sqlBuff.append(" union all select m.grph_domain_code, m.status from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code ");
	        sqlBuff.append(" )select grph_domain_code from q WHERE status IN(?, ?) )  ");
	        sqlBuff.append(" AND CAT.CATEGORY_CODE=u.CATEGORY_CODE AND CAT.domain_code=? ");
	        sqlBuff.append(" AND U.CATEGORY_CODE=? AND u.USER_NAME =?");
	        String selectQuery =sqlBuff.toString();
	        if (log.isDebugEnabled()) {
	            log.debug("isValidUserExist", "Select Query::" + selectQuery);
	        }
	        PreparedStatement pstmtSelect;
	        pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, zoneCode);
            pstmtSelect.setString(2, zoneCode);
            pstmtSelect.setString(3, userIdLoggedUser);
            pstmtSelect.setString(4, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(5, PretupsI.STATUS_SUSPEND);
            pstmtSelect.setString(6, domainCode);
            pstmtSelect.setString(7, categoryCode);
            pstmtSelect.setString(8, channelUser);
            return pstmtSelect;
	 }
	 
		
	 @Override
		public PreparedStatement loadBonusPointDetailsQry(Connection con,String zoneCode,String userId,String domainCode,String categoryCode,String fromdate,String todate) throws SQLException,ParseException{
			 StringBuilder selectQuery = new StringBuilder();
	         selectQuery.append(" SELECT U.user_id,U.category_code,C.domain_code,U.user_name, B.points_date ,U.msisdn,B.points,B.ACCUMULATED_POINTS,B.TRANSFER_ID,B.LAST_REDEMPTION_ID,B.profile_type,LAST_REDEMPTION_ON ,ENTRY_DATE ");
	         selectQuery.append(" FROM USERS U , USER_GEOGRAPHIES UG,CATEGORIES C ,BONUS_HISTORY B WHERE  U.user_id=B.user_id_or_msisdn AND C.domain_code=? AND B.profile_type IN (?,?) ");
	         selectQuery.append(" AND C.category_code=(CASE WHEN ?='ALL' then C.category_code ELSE ? END) AND C.category_code=U.category_code AND U.user_id=(CASE WHEN ?='ALL' then U.user_id ELSE ? END) ");
	         selectQuery.append(" AND U.user_id=UG.user_id AND ug.GRPH_DOMAIN_CODE IN (");
	         selectQuery.append(" with recursive q as ( SELECT GRPH_DOMAIN_CODE, status FROM GEOGRAPHICAL_DOMAINS WHERE  GRPH_DOMAIN_CODE=? "); 
	         selectQuery.append(" union  all select m.grph_domain_code, m.status from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code  ");
	         selectQuery.append(" )select grph_domain_code from q  WHERE status IN (?,?) ) ");
	 	 	 selectQuery.append(" AND ( (date_trunc('day',B.points_date::TIMESTAMP) >=?  AND  date_trunc('day',B.points_date::TIMESTAMP) <=?)");
	 	 	 selectQuery.append(" OR ( date_trunc('day',B.LAST_REDEMPTION_ON ::TIMESTAMP)>=?  AND  date_trunc('day',B.LAST_REDEMPTION_ON ::TIMESTAMP)<=? ))");
	         selectQuery.append(" ORDER BY ENTRY_DATE desc,U.msisdn desc ");
	         PreparedStatement pstmtSelect = con.prepareStatement(selectQuery.toString());
	         pstmtSelect.setString(1, domainCode);
	         pstmtSelect.setString(2, PretupsI.PROFILE_TYPE_ACTIVATION_BONUS);
	         pstmtSelect.setString(3, PretupsI.LMS_PROFILE_TYPE);
	         pstmtSelect.setString(4, categoryCode);
	         pstmtSelect.setString(5, categoryCode);
	         pstmtSelect.setString(6, userId);
	         pstmtSelect.setString(7, userId);
	         pstmtSelect.setString(8, zoneCode);
	         pstmtSelect.setString(9, PretupsI.STATUS_ACTIVE);
	         pstmtSelect.setString(10, PretupsI.STATUS_SUSPEND);
	         pstmtSelect.setDate(11, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(fromdate)));
	         pstmtSelect.setDate(12, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(todate)));
	         pstmtSelect.setDate(13, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(fromdate)));
	         pstmtSelect.setDate(14, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(todate)));
	         return pstmtSelect;
		 }   
    

	
	 			
	 			
	             
	 			
	 			
	
		

    
     

}