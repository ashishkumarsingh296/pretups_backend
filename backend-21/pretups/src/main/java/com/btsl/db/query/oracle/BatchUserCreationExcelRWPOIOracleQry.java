package com.btsl.db.query.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.xl.BatchUserCreationExcelRWPOIQry;

public class BatchUserCreationExcelRWPOIOracleQry implements BatchUserCreationExcelRWPOIQry{
	
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
        if (SystemPreferences.USERWISE_LOAN_ENABLE) 
        	strBuild.append(",culoan.profile_id");
        strBuild.append(" FROM users U, user_geographies UG, user_phones UP, channel_users CU, categories C, domains D");
        if (SystemPreferences.USERWISE_LOAN_ENABLE) 
        	strBuild.append(",channel_user_loan_info culoan");
        strBuild.append(" WHERE  U.category_code=C.category_code AND C.domain_code=D.domain_code");
        strBuild.append(" AND U.user_id=UG.user_id AND U.user_id=CU.user_id AND U.status NOT IN(?,?) ");
        strBuild.append(" AND U.user_type =? AND U.category_code=? AND UP.user_id(+)=U.user_id");
        if (SystemPreferences.USERWISE_LOAN_ENABLE) 
        	strBuild.append(" AND culoan.user_id(+)=U.user_id");
        strBuild.append(" AND UP.primary_number(+)=? AND UG.grph_domain_code IN (SELECT grph_domain_code FROM ");
        strBuild.append(" geographical_domains GD1 WHERE status IN(?, ?) ");
        strBuild.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN");
        strBuild.append(" (SELECT grph_domain_code FROM user_geographies ug1 WHERE UG1.grph_domain_code =DECODE(?, '" + PretupsI.ALL + "', UG1.grph_domain_code, ?)  AND UG1.user_id=? ))");
        
        LogFactory.printLog("writeModifyExcelQry", strBuild.toString(), log);
        PreparedStatement pstmtSelect = con.prepareStatement(strBuild.toString()); 
        int i = 1;
        pstmtSelect.setString(i, PretupsI.USER_STATUS_DELETED);
        i++;
        pstmtSelect.setString(i, PretupsI.USER_STATUS_CANCELED);
        i++;
        pstmtSelect.setString(i, TypesI.CHANNEL_USER_TYPE);
        i++;
        pstmtSelect.setString(i, categoryCode);
        i++;
        pstmtSelect.setString(i, PretupsI.YES);
        i++;
        pstmtSelect.setString(i, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
        i++;
        pstmtSelect.setString(i, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND);
        i++;
        pstmtSelect.setString(i, geographyCode);
        i++;
        pstmtSelect.setString(i, geographyCode);
        i++;
        pstmtSelect.setString(i, userId);
		return pstmtSelect;
	}

}
