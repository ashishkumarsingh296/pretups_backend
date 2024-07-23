package com.btsl.pretups.loyaltymgmt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.common.TypesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;

public class ActivationBonusLMSPostgresQry implements ActivationBonusLMSQry{
	@Override
	public PreparedStatement viewRedemptionenquiryDetailsQry(Connection con,String pDomainCode,String pCategoryCode,String pUserId,String pZoneCode,String pFromdate,String pTodate) throws SQLException,ParseException{
			String methodName="viewRedemptionenquiryDetailsQry";	
			StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT U.user_id,U.category_code,C.domain_code,U.user_name, R.redemption_date ,R.amount_transfered,U.msisdn,R.points_redeemed FROM redemptions R ,users U , user_geographies UG,Categories C ");
            selectQuery.append(" WHERE  U.user_id=R.user_id_or_msisdn AND C.domain_code=? and C.category_code=(CASE WHEN ?='ALL' THEN C.category_code ELSE ? END) AND ");
			selectQuery.append(" C.category_code=U.category_code AND U.user_id=(CASE WHEN ?='ALL' THEN U.user_id ELSE ? END)");
            selectQuery.append(" AND U.user_id=UG.user_id and ug.GRPH_DOMAIN_CODE in ( ");
			selectQuery.append(" with recursive q as ( select GRPH_DOMAIN_CODE, status from GEOGRAPHICAL_DOMAINS where GRPH_DOMAIN_CODE=? ");
			selectQuery.append(" union all select m.grph_domain_code,m.status from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code ");
			selectQuery.append(" ) select GRPH_DOMAIN_CODE from q where status IN (?,?) ) ");
            selectQuery.append(" AND date_trunc('day',R.redemption_date :: TIMESTAMP)>=?  AND  date_trunc('day',R.redemption_date :: TIMESTAMP)<=? ");
			
            if (log.isDebugEnabled()) 
            	log.debug(methodName, "Query=" + selectQuery);
            PreparedStatement pstmtSelect = con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, pDomainCode);
            pstmtSelect.setString(2, pCategoryCode);
            pstmtSelect.setString(3, pCategoryCode);
            pstmtSelect.setString(4, pUserId);
            pstmtSelect.setString(5, pUserId);
            pstmtSelect.setString(6, pZoneCode);
            pstmtSelect.setString(7, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(8, PretupsI.STATUS_SUSPEND);
            pstmtSelect.setDate(9, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(pFromdate)));
            pstmtSelect.setDate(10, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(pTodate)));
            return pstmtSelect;
	}
	
		@Override
		public String loadServicesListQry(String pCatCode){
		StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT st.module,st.service_type,st.name,st.type ");
        strBuff.append("FROM service_type st,network_services ns ");
        strBuff.append(" WHERE st.external_interface = 'Y' AND st.status = 'Y' AND st.service_type = ns.service_type");
        strBuff.append(" AND ns.sender_network = ? AND ns.receiver_network = ? AND st.module = ? AND st.type <> ?");
        if (!BTSLUtil.isNullString(pCatCode)) {
            strBuff.append(" AND st.service_type in (SELECT CST.service_type FROM category_service_type CST WHERE CST.category_code=?)q ");
        }
        strBuff.append(" ORDER BY name");

        return strBuff.toString();
	}

	
	@Override
	public PreparedStatement validateUserdetailsQry(Connection con,String pDomainCode,String pCategoryCode,String pUserIdLoggedUser,String pZoneCode,String pChannelUser) throws SQLException{
		StringBuilder sqlBuff = new StringBuilder();
        sqlBuff.append("SELECT U.user_id,u.user_name FROM USERS U,CATEGORIES CAT,USER_GEOGRAPHIES UG ");
        sqlBuff.append(" WHERE UG.user_id =u.user_id AND UG.GRPH_DOMAIN_CODE IN ");
        sqlBuff.append(" ( ");
        sqlBuff.append(" with recursive q as ( ");
        sqlBuff.append(" SELECT grph_domain_code, status FROM GEOGRAPHICAL_DOMAINS GD1 WHERE   ");
        sqlBuff.append(" grph_domain_code IN ");
        sqlBuff.append(" (SELECT grph_domain_code  FROM USER_GEOGRAPHIES UG1  WHERE UG1.grph_domain_code = (CASE WHEN ?= 'ALL' THEN UG1.grph_domain_code ELSE ? END) ");
        sqlBuff.append(" AND UG1.user_id=? ) ");
        sqlBuff.append(" union all select m.grph_domain_code, m.status from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code ");
        sqlBuff.append(" )select grph_domain_code from q where status IN(?, ?) )");
        sqlBuff.append(" AND CAT.CATEGORY_CODE=u.CATEGORY_CODE AND CAT.domain_code=? ");
        sqlBuff.append(" AND U.CATEGORY_CODE=? AND u.USER_NAME =?");
         
		 if (log.isDebugEnabled()) {
            log.debug("isValidUserExist", "Select Query::" + sqlBuff.toString());
        }

		PreparedStatement pstmtSelect = con.prepareStatement(sqlBuff.toString());
            pstmtSelect.setString(1, pZoneCode);
            pstmtSelect.setString(2, pZoneCode);
            pstmtSelect.setString(3, pUserIdLoggedUser);
            pstmtSelect.setString(4, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(5, PretupsI.STATUS_SUSPEND);
            pstmtSelect.setString(6, pDomainCode);
            pstmtSelect.setString(7, pCategoryCode);
            pstmtSelect.setString(8, pChannelUser);
            
		return pstmtSelect;
		
	}
	
	@Override
	public PreparedStatement loadBonusPointDetailsQry(Connection con,String pDomainCode,String pCategoryCode,String pUserId,String pZoneCode,String pFromdate,String pTodate)throws SQLException ,ParseException{
		String methodName="loadBonusPointDetailsQry";
	StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT U.user_id,U.category_code,C.domain_code,U.user_name, B.points_date ,U.msisdn,B.points ");
            selectQuery.append(" FROM USERS U , USER_GEOGRAPHIES UG,CATEGORIES C ,BONUS B WHERE  U.user_id=B.user_id_or_msisdn AND C.domain_code=? AND B.profile_type=?");
            selectQuery.append(" AND C.category_code=(CASE WHEN ?='ALL' THEN C.category_code ELSE ? END) AND C.category_code=U.category_code AND ");
			selectQuery.append(" U.user_id=(CASE WHEN ?='ALL' THEN U.user_id ELSE ? END)");
            selectQuery.append(" AND U.user_id=UG.user_id AND ug.GRPH_DOMAIN_CODE IN (");
			selectQuery.append(" with recursive q as (");
			selectQuery.append(" SELECT GRPH_DOMAIN_CODE, status FROM GEOGRAPHICAL_DOMAINS WHERE GRPH_DOMAIN_CODE=?");
			selectQuery.append(" union  all select m.grph_domain_code, m.status from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code  ");
            selectQuery.append("  ) select GRPH_DOMAIN_CODE from q where status IN (?,?) ) ");
			selectQuery.append(" AND date_trunc('day',B.points_date::TIMESTAMP)>=?  AND  date_trunc('day',B.points_date :: TIMESTAMP)<=? ");	
			if (log.isDebugEnabled()) {
                log.debug(methodName, "Query=" + selectQuery);
            }
			PreparedStatement pstmtSelect = con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, pDomainCode);
            pstmtSelect.setString(2, PretupsI.PROFILE_TYPE_ACTIVATION_BONUS);
            pstmtSelect.setString(3, pCategoryCode);
            pstmtSelect.setString(4, pCategoryCode);
            pstmtSelect.setString(5, pUserId);
            pstmtSelect.setString(6, pUserId);
            pstmtSelect.setString(7, pZoneCode);
            pstmtSelect.setString(8, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(9, PretupsI.STATUS_SUSPEND);
            pstmtSelect.setDate(10, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(pFromdate)));
            pstmtSelect.setDate(11, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(pTodate)));
            return pstmtSelect;
	}
	
	
		@Override
		public String loadpromotionListQry(){
			StringBuilder strBuff = new StringBuilder();
			strBuff.append(" SELECT distinct ps.set_id,ps.set_name ");
            strBuff.append(" from profile_set ps,profile_set_version psv ");
            strBuff.append(" where psv.status =ps.status and ps.status in (?,?) and ps.profile_type=? and ps.set_id=psv.set_id and date_trunc('day',psv.applicable_to::TIMESTAMP)>=date_trunc('day', CURRENT_TIMESTAMP ::TIMESTAMP) ");
            return strBuff.toString();
	}
	
	@Override
	public String isprofileExpiredQry(){
		
            StringBuilder sbf = new StringBuilder();
            sbf.append("select set_id from PROFILE_SET_VERSION where set_id=? and version=? and applicable_to>=CURRENT_TIMESTAMP ");
            return sbf.toString();
	}
	
	@Override
	public PreparedStatement loadMapForLMSAssociationQry(Connection con,String gradeCode,String pCategoryCode,String pUserId,String pGeographyCode) throws SQLException{
		
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("SELECT distinct(U.user_id)  ,UP.msisdn,PS.set_name");
            strBuff.append(" FROM user_phones UP right outer join users U on (UP.user_id =U.user_id AND UP.primary_number=? ) , PROFILE_SET PS right outer join channel_users CU on  PS.set_id=CU.LMS_PROFILE ,user_geographies UG,  categories C, domains D ");
            strBuff.append(" WHERE  U.category_code=C.category_code AND C.domain_code=D.domain_code");
            strBuff.append(" AND U.user_id=UG.user_id AND U.user_id=CU.user_id AND U.status IN(?,?) ");
            strBuff.append(" AND U.user_type =? AND U.category_code=?  ");
            if (!(gradeCode.equalsIgnoreCase(PretupsI.ALL))) {
                strBuff.append(" AND CU.user_grade=? ");
            }
            strBuff.append("  AND UG.grph_domain_code IN ( ");
            strBuff.append(" with recursive q as ( ");
            strBuff.append(" SELECT grph_domain_code, status FROM geographical_domains GD1 WHERE grph_domain_code IN ");
            strBuff.append("(SELECT grph_domain_code FROM user_geographies ug1 WHERE UG1.grph_domain_code = (CASE WHEN ?= '" + PretupsI.ALL + "' THEN UG1.grph_domain_code ELSE ? END)   AND UG1.user_id=? ) ");
            strBuff.append(" union all select m.grph_domain_code, m.status from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code ");
            strBuff.append("  )select grph_domain_code from q where status IN(?, ?)");
            strBuff.append(" )");
            String sqlSelect = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug("loadBatchUserListForModify", "QUERY sqlSelect=" + sqlSelect);
            }
            int i = 1;
            PreparedStatement pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(i++, PretupsI.YES);
            pstmt.setString(i++, PretupsI.USER_STATUS_ACTIVE);
            pstmt.setString(i++, PretupsI.USER_STATUS_SUSPEND);
            pstmt.setString(i++, TypesI.CHANNEL_USER_TYPE);
            pstmt.setString(i++, pCategoryCode);
            if (!(gradeCode.equalsIgnoreCase(PretupsI.ALL))) {
                pstmt.setString(i++, gradeCode);
            }
            pstmt.setString(i++, pGeographyCode);
            pstmt.setString(i++, pGeographyCode);
            pstmt.setString(i++, pUserId);
            pstmt.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
            pstmt.setString(i, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND);
            return pstmt;
	}
}