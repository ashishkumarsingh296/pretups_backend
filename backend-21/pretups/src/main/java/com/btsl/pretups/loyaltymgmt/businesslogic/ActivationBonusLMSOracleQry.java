package com.btsl.pretups.loyaltymgmt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.common.TypesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;

public class ActivationBonusLMSOracleQry implements ActivationBonusLMSQry{
	 
	// to do
		@Override
		public PreparedStatement viewRedemptionenquiryDetailsQry(Connection con,String pDomainCode,String pCategoryCode,String pUserId,String pZoneCode,String pFromdate,String pTodate) throws SQLException,ParseException{
			String methodName="viewRedemptionenquiryDetailsQry";
			StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT U.user_id,U.category_code,C.domain_code,U.user_name, R.redemption_date ,R.amount_transfered,U.msisdn,R.points_redeemed FROM redemptions R ,users U , user_geographies UG,Categories C ");
            selectQuery.append("WHERE  U.user_id=R.user_id_or_msisdn AND C.domain_code=? and C.category_code=decode(?,'ALL',C.category_code,?) AND C.category_code=U.category_code AND U.user_id=decode(?,'ALL',U.user_id,?)");
            selectQuery.append("AND U.user_id=UG.user_id and ug.GRPH_DOMAIN_CODE in (select GRPH_DOMAIN_CODE from GEOGRAPHICAL_DOMAINS where status IN (?,?) connect by prior GRPH_DOMAIN_CODE=PARENT_GRPH_DOMAIN_CODE  start with GRPH_DOMAIN_CODE=?)");
            selectQuery.append("AND trunc(R.redemption_date)>=?  AND  trunc(R.redemption_date)<=? ");

            if (log.isDebugEnabled()) {
            	log.debug(methodName, "Query=" + selectQuery);
            }
            PreparedStatement pstmtSelect = con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, pDomainCode);
            pstmtSelect.setString(2, pCategoryCode);
            pstmtSelect.setString(3, pCategoryCode);
            pstmtSelect.setString(4, pUserId);
            pstmtSelect.setString(5, pUserId);
            pstmtSelect.setString(6, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(7, PretupsI.STATUS_SUSPEND);
            pstmtSelect.setString(8, pZoneCode);
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
            strBuff.append(" AND st.service_type in (SELECT CST.service_type FROM category_service_type CST WHERE CST.category_code=?) ");
        }
        strBuff.append(" ORDER BY name");

        return strBuff.toString();
	}
	
	
	@Override
	public PreparedStatement validateUserdetailsQry(Connection con,String pDomainCode,String pCategoryCode,String pUserIdLoggedUser,String pZoneCode,String pChannelUser) throws SQLException{
		StringBuilder sqlBuff = new StringBuilder();
		sqlBuff.append("SELECT U.user_id,u.user_name FROM USERS U,CATEGORIES CAT,USER_GEOGRAPHIES UG ");
        sqlBuff.append("WHERE UG.user_id =u.user_id AND UG.GRPH_DOMAIN_CODE IN ");
        sqlBuff.append(" (SELECT grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE status IN(?, ?)");
        sqlBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code  START WITH grph_domain_code IN ");
        sqlBuff.append(" (SELECT grph_domain_code  FROM USER_GEOGRAPHIES UG1  WHERE UG1.grph_domain_code = DECODE(?, 'ALL', UG1.grph_domain_code,?) ");
        sqlBuff.append(" AND UG1.user_id=? )) ");
        sqlBuff.append(" AND CAT.CATEGORY_CODE=u.CATEGORY_CODE AND CAT.domain_code=? ");
        sqlBuff.append(" AND U.CATEGORY_CODE=? AND u.USER_NAME =?");
         if (log.isDebugEnabled()) {
            log.debug("isValidUserExist", "Select Query::" + sqlBuff.toString());
        }
         String selectQuery =sqlBuff.toString();
         PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);
            
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(2, PretupsI.STATUS_SUSPEND);
            pstmtSelect.setString(3, pZoneCode);
            pstmtSelect.setString(4, pZoneCode);
            pstmtSelect.setString(5, pUserIdLoggedUser);
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
            selectQuery.append("FROM USERS U , USER_GEOGRAPHIES UG,CATEGORIES C ,BONUS B WHERE  U.user_id=B.user_id_or_msisdn AND C.domain_code=? AND B.profile_type=?");
            selectQuery.append("AND C.category_code=DECODE(?,'ALL',C.category_code,?) AND C.category_code=U.category_code AND U.user_id=DECODE(?,'ALL',U.user_id,?)");
            selectQuery.append("AND U.user_id=UG.user_id AND ug.GRPH_DOMAIN_CODE IN (SELECT GRPH_DOMAIN_CODE FROM GEOGRAPHICAL_DOMAINS WHERE status IN (?,?)");
            selectQuery.append("CONNECT BY PRIOR GRPH_DOMAIN_CODE=PARENT_GRPH_DOMAIN_CODE  START WITH GRPH_DOMAIN_CODE=?)AND TRUNC(B.points_date)>=?  AND  TRUNC(B.points_date)<=? ");
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
            pstmtSelect.setString(7, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(8, PretupsI.STATUS_SUSPEND);
            pstmtSelect.setString(9, pZoneCode);
            pstmtSelect.setDate(10, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(pFromdate)));
            pstmtSelect.setDate(11, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(pTodate)));
            return pstmtSelect;
	}
	
	@Override
	public String loadpromotionListQry(){
		StringBuilder strBuff = new StringBuilder();
			strBuff.append(" SELECT distinct ps.set_id,ps.set_name ");
            strBuff.append(" from profile_set ps,profile_set_version psv ");
            strBuff.append(" where psv.status =ps.status and ps.status in (?,?) and ps.profile_type=? and ps.set_id=psv.set_id and trunc(psv.applicable_to)>=trunc(sysdate) ");
            return strBuff.toString();
	}
	
	
	@Override
	public String isprofileExpiredQry(){
		
            StringBuilder sbf = new StringBuilder();
            sbf.append("select set_id from PROFILE_SET_VERSION where set_id=? and version=? and applicable_to>=SYSDATE ");
            return sbf.toString();
	}
	
	@Override
	public PreparedStatement loadMapForLMSAssociationQry(Connection con,String gradeCode,String pCategoryCode,String pUserId,String pGeographyCode) throws SQLException{
		
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("SELECT distinct(U.user_id)  ,UP.msisdn,PS.set_name");
            strBuff.append(" FROM users U, user_geographies UG, user_phones UP, channel_users CU, categories C, domains D,PROFILE_SET PS");
            strBuff.append(" WHERE  U.category_code=C.category_code AND C.domain_code=D.domain_code");
            strBuff.append(" AND U.user_id=UG.user_id AND U.user_id=CU.user_id AND U.status IN(?,?) ");
            strBuff.append(" AND U.user_type =? AND U.category_code=? AND UP.user_id(+)=U.user_id");
            strBuff.append(" AND UP.primary_number(+)=?");
            if (!(gradeCode.equalsIgnoreCase(PretupsI.ALL))) {
                strBuff.append(" AND CU.user_grade=? ");
            }
            strBuff.append("and PS.set_id(+)=CU.LMS_PROFILE AND UG.grph_domain_code IN (SELECT grph_domain_code FROM ");
            strBuff.append(" geographical_domains GD1 WHERE status IN(?, ?) ");
            strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN");
            strBuff.append(" (SELECT grph_domain_code FROM user_geographies ug1 WHERE UG1.grph_domain_code =DECODE(?, '" + PretupsI.ALL + "', UG1.grph_domain_code, ?)  AND UG1.user_id=? ))");
            String sqlSelect = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug("loadBatchUserListForModify", "QUERY sqlSelect=" + sqlSelect);
            }
            int i = 1;
            PreparedStatement pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(i++, PretupsI.USER_STATUS_ACTIVE);
            pstmt.setString(i++, PretupsI.USER_STATUS_SUSPEND);
            pstmt.setString(i++, TypesI.CHANNEL_USER_TYPE);
            pstmt.setString(i++, pCategoryCode);
            pstmt.setString(i++, PretupsI.YES);
            if (!(gradeCode.equalsIgnoreCase(PretupsI.ALL))) {
                pstmt.setString(i++, gradeCode);
            }
            pstmt.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
            pstmt.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND);
            pstmt.setString(i++, pGeographyCode);
            pstmt.setString(i++, pGeographyCode);
            pstmt.setString(i++, pUserId);
            return pstmt;
	}
}