package com.btsl.db.query.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.xl.BatchUserCreationExcelRWPOIQry;

public class BatchUserCreationExcelRWPOIPostgresQry implements BatchUserCreationExcelRWPOIQry{
	private Log log = LogFactory.getLog(getClass());

	@Override
	public PreparedStatement writeModifyExcelQry(Connection con,String categoryCode,String geographyCode , String userId ) throws SQLException {
		boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
		final StringBuilder strBuild = new StringBuilder(" SELECT DISTINCT U.user_id, U.user_name_prefix, U.user_name, U.login_id, U.status,");
        strBuild.append(" U.password, UP.msisdn, UP.sms_pin, U.short_name, D.domain_type_code, ");
        strBuild.append(" U.employee_code, U.external_code, CU.in_suspend, CU.out_suspend,CU.TRANSFER_PROFILE_ID,CU.COMM_PROFILE_SET_ID,CU.USER_GRADE,");
        if (lmsAppl) {
        	strBuild.append(" CU.lms_profile, ");
        }
        strBuild.append(" CU.alert_email,CU.alert_msisdn,CU.alert_type, ");
        strBuild.append(" CU.low_bal_alert_allow, ");
        strBuild.append("CU.trf_rule_type,");
        strBuild.append(" CU.outlet_code,CU.suboutlet_code, CU.contact_person, U.contact_no,");
        strBuild.append(" U.ssn, U.designation, U.address1, U.address2, U.city,U.state,U.country,U.company,U.fax,U.firstname,U.lastname, U.email, U.rsaflag,U.authentication_allowed,");// company,fax,firstname,lastname
        strBuild.append("C.category_name");
        strBuild.append(",U.longitude,U.latitude,U.document_type,U.document_no,U.payment_type");

        strBuild.append(" FROM  user_geographies UG, user_phones UP right join  users U on UP.user_id=U.user_id AND UP.primary_number=? , channel_users CU, categories C, domains D");
        strBuild.append(" WHERE  U.category_code=C.category_code AND C.domain_code=D.domain_code");
        strBuild.append(" AND U.user_id=UG.user_id AND U.user_id=CU.user_id AND U.status NOT IN(?,?) ");
        strBuild.append(" AND U.user_type =? AND U.category_code=? ");
        strBuild.append(" AND UG.grph_domain_code IN (");        
        
        strBuild.append("WITH RECURSIVE q AS ( ");
        strBuild.append("SELECT GD1.grph_domain_code FROM   geographical_domains GD1 WHERE GD1.status IN(?, ?) ");
        strBuild.append("AND  GD1.grph_domain_code IN  (SELECT grph_domain_code FROM user_geographies ug1 ");
        strBuild.append("WHERE UG1.grph_domain_code = case ? when  '" + PretupsI.ALL + "' then  UG1.grph_domain_code else  ? end  AND UG1.user_id=? ) ");
        strBuild.append("UNION ALL ");
        strBuild.append("SELECT GD1.grph_domain_code FROM   geographical_domains GD1 ");
        strBuild.append("join q on q.grph_domain_code = GD1.parent_grph_domain_code ");
        strBuild.append("WHERE  GD1.status IN(?, ?)  ");
        strBuild.append(")SELECT grph_domain_code FROM q ");
        
        strBuild.append( ")");
        
        LogFactory.printLog("writeModifyExcelQry", strBuild.toString(), log);
        PreparedStatement pstmtSelect = con.prepareStatement(strBuild.toString());
        int i = 1;
        pstmtSelect.setString(i++, PretupsI.YES);
        pstmtSelect.setString(i++, PretupsI.USER_STATUS_DELETED);
        pstmtSelect.setString(i++, PretupsI.USER_STATUS_CANCELED);
        pstmtSelect.setString(i++, TypesI.CHANNEL_USER_TYPE);
        pstmtSelect.setString(i++, categoryCode);
        pstmtSelect.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
        pstmtSelect.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND);
        pstmtSelect.setString(i++, geographyCode);
        pstmtSelect.setString(i++, geographyCode);
        pstmtSelect.setString(i++, userId);
        pstmtSelect.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
        pstmtSelect.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND);
		return pstmtSelect;
	}

}
