package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.util.PretupsBL;

public class UserReportOracleQry implements UserReportQry{
	@Override
	public PreparedStatement loadUserClosingBalanceQry(Connection con, String networkCode, String zone, String domainCode, String categoryCode, String userId, String loginUserId, Date formDate, Date toDate, String fromAmt, String toAmt, String userType) 
	throws  BTSLBaseException, SQLException{
		String methodName = "loadUserClosingBalanceQry";
		StringBuilder strUserBal = new StringBuilder("SELECT U.user_id,C.DOMAIN_CODE, C.SEQUENCE_NO, U.user_name user_name,U.msisdn user_msisdn,C.category_name ");
         strUserBal.append("user_category,GD.grph_domain_name user_geography,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name,(CASE U.parent_id WHEN 'ROOT' THEN '' ELSE PU.msisdn END) parent_msisdn,UW.user_name owner_user,");
         strUserBal.append("UW.msisdn owner_msisdn,(CASE PU.parent_id WHEN 'ROOT' THEN N'' ELSE GP.user_name END) grand_parent,(CASE PU.parent_id WHEN 'ROOT' THEN '' ELSE GP.msisdn END) gp_msisdn,GGD.grph_domain_name gp_domain, GC.category_name gp_category,");
         strUserBal.append("UserClosingBalance(U.user_id,?,?,?,?) userbal ");
         strUserBal.append("FROM users U,USERS UW,USERS PU,CATEGORIES C,");
         strUserBal.append("USER_GEOGRAPHIES UG,GEOGRAPHICAL_DOMAINS GD, USERS GP,USER_GEOGRAPHIES GUG, CATEGORIES GC,GEOGRAPHICAL_DOMAINS GGD ");

         if (PretupsI.USER_TYPE_CHANNEL.equals(userType)) {
             strUserBal.append(",(SELECT U11.user_id FROM USERS U11 WHERE U11.user_id=CASE ?  WHEN 'ALL' THEN U11.user_id ELSE ? END ");
             strUserBal.append("CONNECT BY PRIOR U11.user_id = U11.parent_id START WITH U11.user_id=?)X ");
         }
         strUserBal.append("WHERE C.category_code=U.category_code ");
         if (PretupsI.USER_TYPE_CHANNEL.equals(userType)) {
             strUserBal.append("AND X.user_id=u.user_id ");
         }
         strUserBal.append("AND U.owner_id=UW.user_id ");
         strUserBal.append("AND U.user_id = UG.user_id AND GUG.grph_domain_code = GGD.grph_domain_code AND GUG.user_id=GP.user_id ");
         strUserBal.append("AND GP.category_code=GC.category_code AND UG.grph_domain_code = GD.grph_domain_code ");
         strUserBal.append("AND GP.USER_ID= (CASE PU.PARENT_ID WHEN 'ROOT' THEN PU.user_id ELSE PU.PARENT_ID END) ");
         strUserBal.append("AND PU.user_id=(CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END) ");
         strUserBal.append("AND U.network_code=? AND U.status NOT IN ('C','N','W') AND u.USER_TYPE='CHANNEL' ");
         strUserBal.append("AND UG.grph_domain_code IN (SELECT GD1.grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE GD1.status IN ('Y','S') CONNECT BY PRIOR ");
         strUserBal.append("GD1.grph_domain_code = GD1.parent_grph_domain_code START WITH GD1.grph_domain_code IN (SELECT UG1.grph_domain_code  FROM USER_GEOGRAPHIES ");
         strUserBal.append("UG1 WHERE ug1.GRPH_DOMAIN_CODE=CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END  AND UG1.user_id=? )) ");
         strUserBal.append("AND C.domain_code = ? AND C.category_code = CASE ? WHEN 'ALL' THEN C.category_code ELSE ? END ");
         strUserBal.append("AND U.user_id = (CASE ?  WHEN 'ALL' THEN U.user_id ELSE ? END) ");
         strUserBal.append("ORDER BY C.DOMAIN_CODE,C.SEQUENCE_NO,U.user_id,U.user_name ASC");
         LogFactory.printLog(methodName, strUserBal.toString(), LOG);
         PreparedStatement pstmt = con.prepareStatement(strUserBal.toString());
         int i = 1;
         pstmt.setDate(i++, formDate);
         pstmt.setDate(i++, toDate);
         pstmt.setLong(i++, PretupsBL.getSystemAmount(fromAmt));
         pstmt.setLong(i++, PretupsBL.getSystemAmount(toAmt));
         if (PretupsI.USER_TYPE_CHANNEL.equals(userType)) {
             pstmt.setString(i++, userId);
             pstmt.setString(i++, userId);
             pstmt.setString(i++, loginUserId);
         }
         pstmt.setString(i++, networkCode);
         pstmt.setString(i++, zone);
         pstmt.setString(i++, zone);
         pstmt.setString(i++, loginUserId);
         pstmt.setString(i++, domainCode);
         pstmt.setString(i++, categoryCode);
         pstmt.setString(i++, categoryCode);
         pstmt.setString(i++, userId);
         pstmt.setString(i, userId);
      
        return pstmt;
	}

}
