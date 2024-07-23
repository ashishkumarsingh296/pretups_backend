package com.btsl.pretups.channel.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

public class BatchUserPostgresQry implements BatchUserQry{

	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public String loadBatchListForApprovalQry(String[] geographyCode,
			String[] domainCode) {

		final StringBuilder strBuff = new StringBuilder(" SELECT DISTINCT B.batch_id, B.status, B.modified_on, USR_MOD.user_name modified_by,");
		strBuff.append(" B.created_on, USR_CR.user_name created_by,B.batch_type, B.batch_size, B.batch_name,  ");
		// added by shashank
		strBuff.append(" USR_CR.user_type intiator_type, USR_CR.CATEGORY_CODE initiator_category,");
		// end
		strBuff.append(" SUM(CASE WHEN U.status = ? then 1 else 0 end ) as new,SUM(CASE WHEN U.status = ? then 1 else 0 end ) active ");
		strBuff.append(" FROM batches B left join users USR_CR on B.created_by=USR_CR.user_id left join users USR_MOD on  B.modified_by= USR_MOD.user_id , users U, batch_geographies BG, categories C ");
		strBuff.append(" WHERE  U.creation_type=? AND B.status=? AND BG.geography_code IN( ");
		// added by shashank
		// Changes made for Batch User creation by Channel users
		//CONNECT BY START
		strBuff.append(" WITH RECURSIVE q AS (");
		strBuff.append(" select grph_domain_code from geographical_domains ");
		strBuff.append(" where  grph_domain_code in (");

		for (int i = 0; i < geographyCode.length; i++) {
			strBuff.append(" ?");
			if (i != geographyCode.length - 1) {
				strBuff.append(",");
			}
		}
		strBuff.append(" )");
		strBuff.append(" UNION ALL ");
		strBuff.append(" select gd1.grph_domain_code from geographical_domains gd1 ");
		strBuff.append(" join q on q.grph_domain_code=gd1.parent_grph_domain_code ");
		strBuff.append(" )select grph_domain_code from q ");
		//CONNECT BY END

		strBuff.append( ")");
		strBuff.append(" AND B.network_code=? ");
		// End of changes made for Batch User creation for Channel users
		// end
		strBuff.append(" AND BG.batch_id=B.batch_id AND B.batch_id=U.batch_id AND C.category_code= U.category_code AND C.domain_code IN(");

		for (int i = 0; i < domainCode.length; i++) {
			strBuff.append(" ?");
			if (i != domainCode.length - 1) {
				strBuff.append(",");
			}
		}
		strBuff.append(" )");
		strBuff.append(" GROUP BY B.batch_id, B.batch_name, B.status, B.modified_on, USR_MOD.user_name , ");
		strBuff.append(" B.created_on, USR_CR.user_name, B.batch_type, B.batch_size, BG.geography_code , USR_CR.user_type, USR_CR.CATEGORY_CODE");
		strBuff.append(" ORDER BY  B.created_on DESC ");
		LogFactory.printLog("loadBatchListForApprovalQry", strBuff.toString(), log);

		return strBuff.toString();
	}

	@Override
	public String loadBatchDetailsListQry() {
		final StringBuilder strBuff = new StringBuilder(
				" SELECT DISTINCT U.user_id, U.user_name,USR_PRT.status parent_status, C.sequence_no, U.network_code, U.login_id, U.password, U.category_code, U.parent_id, U.owner_id, U.allowed_ip,");
		strBuff.append(" U.allowed_days, U.from_time, U.to_time, U.last_login_on, U.employee_code, U.status, U.email, U.pswd_modified_on,");
		strBuff.append(" U.contact_no, U.designation, U.division, U.department, U.msisdn, U.user_type, U.created_by, U.created_on, U.modified_by, U.modified_on,");
		strBuff.append(" U.address1, U.address2, U.city, U.state, U.country, U.ssn, U.user_name_prefix, U.external_code, U.user_code, U.short_name, U.reference_id,U.company,U.fax,U.firstname,U.lastname, U.rsaflag,U.authentication_allowed,");// added
		strBuff.append(" U.invalid_password_count, U.level1_approved_by, U.level1_approved_on, U.level2_approved_by, U.level2_approved_on,");
		strBuff.append(" U.appointment_date, U.password_count_updated_on, U.previous_status, U.batch_id, U.creation_type,");
		strBuff.append(" CU.user_grade, CU.transfer_profile_id, CU.suboutlet_code, CU.outlet_code, CU.out_suspend, CU.in_suspend, CU.contact_person,");
		strBuff.append(" CU.comm_profile_set_id, CU.activated_on, ");
		strBuff.append(" cu.application_id, cu.mpay_profile_id, cu.user_profile_id, cu.is_primary, cu.mcommerce_service_allow, cu.low_bal_alert_allow, CU.trf_rule_type, ");// added
		strBuff.append(" USR_PRT.login_id parent_login_id, USR_PRT.msisdn parent_msisdn, UG.grph_domain_code,C.fixed_roles,C.services_allowed,C.web_interface_allowed, C.sms_interface_allowed ");
		strBuff.append(" ,U.longitude, U.latitude,U.document_type, U.document_no, U.payment_type ");
		strBuff.append(" FROM users U left join users USR_PRT on U.parent_id = USR_PRT.user_id ,channel_users CU,user_geographies UG,categories C ");
		strBuff.append(" WHERE U.batch_id=? AND U.status=? AND CU.user_id=U.user_id ");
		strBuff.append("  AND U.user_id=UG.user_id AND C.category_code = U.category_code ");
		strBuff.append(" ORDER BY C.sequence_no ");
		LogFactory.printLog("loadBatchDetailsListQry", strBuff.toString(), log);
		return strBuff.toString();
	}


	@Override
	public String loadBatchListForEnquiryQry() {
		final StringBuilder strBuff = new StringBuilder(" SELECT B.batch_id, B.status, LK.lookup_name,B.modified_on, B.created_on,");
		strBuff.append(" USR_CR.user_name created_by, B.batch_type, B.batch_size, B.batch_name, B.file_name,");
		strBuff.append(" SUM(CASE WHEN U.status = ? then 1 else 0 end ) as new, SUM(CASE WHEN U.status = ? then 1 else 0 end ) active, SUM(CASE WHEN U.status = ? then 1 else 0 end ) reject ");
		strBuff.append(" FROM batches B  left join users USR_CR on B.created_by=USR_CR.user_id, users U, lookups LK WHERE B.batch_id=U.batch_id AND B.batch_id=?");
		strBuff.append(" AND U.creation_type=? AND B.network_code=? ");
		strBuff.append(" AND LK.lookup_code=B.status AND LK.lookup_type=?");
		strBuff.append(" GROUP BY B.batch_id, B.batch_name, B.file_name,B.status, B.modified_on,");
		strBuff.append(" B.created_on, B.batch_type, B.batch_size, USR_CR.user_name,LK.lookup_name ORDER BY B.created_on DESC");
		LogFactory.printLog("loadBatchListForEnquiryQry", strBuff.toString(), log);
		return strBuff.toString();
	}



	@Override
	public String loadBatchListForEnquiryQry(String[] geographyCode, String userType) {
		final StringBuilder strBuff = new StringBuilder(" SELECT DISTINCT B.batch_id, B.status,LK.lookup_name, B.modified_on, USR_MOD.user_name modified_by,");
		strBuff.append(" B.created_on, USR_CR.user_name created_by,B.batch_type, B.batch_size, B.batch_name, B.file_name, ");
		strBuff.append(" SUM(CASE WHEN U.status = ? then 1 else 0 end) as new,SUM(CASE WHEN U.status = ? then 1 else 0 end) active, SUM(CASE WHEN U.status = ? then 1 else 0 end) reject ");
		strBuff.append(" FROM batches B left join  users USR_CR on B.created_by=USR_CR.user_id left join  users USR_MOD on B.modified_by= USR_MOD.user_id , users U, batch_geographies BG, categories C,lookups LK  ");
		strBuff.append(" WHERE  U.creation_type=? AND BG.geography_code IN ( ");

		strBuff.append(" WITH RECURSIVE  q AS ( ");
		strBuff.append(" select gd1.grph_domain_code from geographical_domains gd1");
		strBuff.append(" where  gd1.grph_domain_code in ( ");
		for (int i = 0; i < geographyCode.length; i++) {
			strBuff.append(" ?");
			if (i != geographyCode.length - 1) {
				strBuff.append(",");
			}
		}
		strBuff.append(")");
		strBuff.append(" UNION  ALL");
		strBuff.append(" select gd2.grph_domain_code from geographical_domains gd2 ");
		strBuff.append(" join q on q.grph_domain_code=gd2.parent_grph_domain_code ");
		strBuff.append(" )select grph_domain_code from q ");

		strBuff.append( ")");

		strBuff.append(" AND B.network_code=? ");
		// end
		strBuff.append(" AND BG.batch_id=B.batch_id AND B.batch_id=U.batch_id AND DATE_TRUNC('day',B.created_on::timestamp)>=? AND DATE_TRUNC('day',B.created_on::timestamp)<=?");
		strBuff.append(" AND C.category_code=U.category_code AND C.domain_code =?");
		strBuff.append(" AND LK.lookup_code=B.status AND LK.lookup_type=?");
		if (userType.equals(PretupsI.CHANNEL_USER_TYPE)) {
			strBuff.append(" AND C.sequence_no > (SELECT sequence_no FROM categories where category_code=?)");
		}
		strBuff.append(" GROUP BY B.batch_id, B.batch_name, B.status, B.modified_on, USR_MOD.user_name , ");
		strBuff.append(" B.created_on, USR_CR.user_name, B.file_name, B.batch_type, B.batch_size, BG.geography_code,LK.lookup_name ");
		strBuff.append(" ORDER BY  B.created_on DESC ");
		LogFactory.printLog("loadBatchListForEnquiryQry", strBuff.toString(), log);
		return strBuff.toString();
	}


	@Override
	public String loadBatchDetailsListForEnqQry() {
		final StringBuilder strBuff = new StringBuilder(
				" SELECT DISTINCT U.user_id, U.user_name, USR_PRT.status parent_status, C.sequence_no, U.network_code, U.login_id, U.password, U.category_code, U.parent_id, U.owner_id, U.allowed_ip,");
		strBuff.append(" U.allowed_days, U.from_time, U.to_time, U.last_login_on, U.employee_code, U.status, LK1.lookup_name StatusDesc, U.email, U.pswd_modified_on,");
		strBuff.append(" U.contact_no, U.designation, U.division, U.department, U.msisdn, U.user_type, U.created_by, U.created_on, U.modified_by, U.modified_on,");
		strBuff.append(" U.address1, U.address2, U.city, U.state, U.country, U.ssn, U.user_name_prefix, U.external_code, U.user_code, U.short_name, U.reference_id, U.firstname, U.lastname, U.company, U.fax,");
		strBuff.append(" U.invalid_password_count, U.level1_approved_by, U.level1_approved_on, U.level2_approved_by, U.level2_approved_on,");
		strBuff.append(" U.appointment_date, U.password_count_updated_on, U.previous_status, U.batch_id, U.creation_type, U.remarks user_remarks,");
		strBuff.append(" CU.user_grade, CU.transfer_profile_id, TP.profile_name, CU.suboutlet_code,(case when CU.suboutlet_code = '' then '' else SLK.sub_lookup_name end) sub_lookup_name, CU.outlet_code,");
		strBuff.append( " (case when CU.outlet_code = '' then '' else LK2.lookup_name end ) OutletName, CU.out_suspend, CU.in_suspend, CU.contact_person,");
		strBuff.append(" CU.comm_profile_set_id, CPS.comm_profile_set_name, CU.activated_on, ");
		strBuff.append(" USR_PRT.login_id parent_login_id, USR_PRT.msisdn parent_msisdn, UG.grph_domain_code, GD.grph_domain_name, C.category_name, C.fixed_roles, C.services_allowed,");
		strBuff.append(" cu.application_id, cu.mpay_profile_id, cu.user_profile_id, cu.is_primary, cu.mcommerce_service_allow, cu.low_bal_alert_allow");
		strBuff.append(", u.rsaflag, u.authentication_allowed");
		strBuff.append(" FROM users U right join categories C on  C.category_code=U.category_code  left join users USR_PRT on  U.parent_id = USR_PRT.user_id ,channel_users CU,  user_geographies UG,");
		strBuff.append("  lookups LK1, lookups LK2, geographical_domains GD, sub_lookups SLK, commission_profile_set CPS, transfer_profile TP");
		strBuff.append(" WHERE U.batch_id=? AND CU.user_id=U.user_id AND U.network_code=CPS.network_code AND CU.comm_profile_set_id=CPS.comm_profile_set_id  AND GD.grph_domain_code=UG.grph_domain_code");
		strBuff.append(" AND U.user_id=UG.user_id AND C.category_code = U.category_code");
		strBuff.append(" AND SLK.sub_lookup_code=(case when CU.suboutlet_code = '' then (select sub_lookup_code from sub_lookups where lookup_type=? limit 1 ) when CU.suboutlet_code = SLK.sub_lookup_code then CU.suboutlet_code end)");
		strBuff.append(" AND LK1.lookup_code=U.status AND LK2.lookup_code=(case when CU.outlet_code = '' then (select lookup_code from lookups where lookup_type=?  limit 1) when CU.outlet_code = LK2.lookup_code then CU.outlet_code end) AND LK1.lookup_type=?");
		strBuff.append(" AND CU.transfer_profile_id=TP.profile_id AND U.network_code=TP.network_code ");
		strBuff.append(" ORDER BY U.status, C.sequence_no ");
		LogFactory.printLog("loadBatchDetailsListForEnqQry", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public String loadBatchDetailsListForEnqTcpQry() {
		final StringBuilder strBuff = new StringBuilder(
				" SELECT DISTINCT U.user_id, U.user_name, USR_PRT.status parent_status, C.sequence_no, U.network_code, U.login_id, U.password, U.category_code, U.parent_id, U.owner_id, U.allowed_ip,");
		strBuff.append(" U.allowed_days, U.from_time, U.to_time, U.last_login_on, U.employee_code, U.status, LK1.lookup_name StatusDesc, U.email, U.pswd_modified_on,");
		strBuff.append(" U.contact_no, U.designation, U.division, U.department, U.msisdn, U.user_type, U.created_by, U.created_on, U.modified_by, U.modified_on,");
		strBuff.append(" U.address1, U.address2, U.city, U.state, U.country, U.ssn, U.user_name_prefix, U.external_code, U.user_code, U.short_name, U.reference_id, U.firstname, U.lastname, U.company, U.fax,");
		strBuff.append(" U.invalid_password_count, U.level1_approved_by, U.level1_approved_on, U.level2_approved_by, U.level2_approved_on,");
		strBuff.append(" U.appointment_date, U.password_count_updated_on, U.previous_status, U.batch_id, U.creation_type, U.remarks user_remarks,");
		strBuff.append(" CU.user_grade, CU.transfer_profile_id,  CU.suboutlet_code,(case when CU.suboutlet_code = '' then '' else SLK.sub_lookup_name end) sub_lookup_name, CU.outlet_code,");
		strBuff.append( " (case when CU.outlet_code = '' then '' else LK2.lookup_name end ) OutletName, CU.out_suspend, CU.in_suspend, CU.contact_person,");
		strBuff.append(" CU.comm_profile_set_id, CPS.comm_profile_set_name, CU.activated_on, ");
		strBuff.append(" USR_PRT.login_id parent_login_id, USR_PRT.msisdn parent_msisdn, UG.grph_domain_code, GD.grph_domain_name, C.category_name, C.fixed_roles, C.services_allowed,");
		strBuff.append(" cu.application_id, cu.mpay_profile_id, cu.user_profile_id, cu.is_primary, cu.mcommerce_service_allow, cu.low_bal_alert_allow");
		strBuff.append(", u.rsaflag, u.authentication_allowed");
		strBuff.append(" FROM users U right join categories C on  C.category_code=U.category_code  left join users USR_PRT on  U.parent_id = USR_PRT.user_id ,channel_users CU,  user_geographies UG,");
		strBuff.append("  lookups LK1, lookups LK2, geographical_domains GD, sub_lookups SLK, commission_profile_set CPS ");
		strBuff.append(" WHERE U.batch_id=? AND CU.user_id=U.user_id AND U.network_code=CPS.network_code AND CU.comm_profile_set_id=CPS.comm_profile_set_id  AND GD.grph_domain_code=UG.grph_domain_code");
		strBuff.append(" AND U.user_id=UG.user_id AND C.category_code = U.category_code");
		strBuff.append(" AND SLK.sub_lookup_code=(case when CU.suboutlet_code = '' then (select sub_lookup_code from sub_lookups where lookup_type=? limit 1 ) when CU.suboutlet_code = SLK.sub_lookup_code then CU.suboutlet_code end)");
		strBuff.append(" AND LK1.lookup_code=U.status AND LK2.lookup_code=(case when CU.outlet_code = '' then (select lookup_code from lookups where lookup_type=?  limit 1) when CU.outlet_code = LK2.lookup_code then CU.outlet_code end) AND LK1.lookup_type=?");
		strBuff.append("  AND U.network_code=TP.network_code ");
		strBuff.append(" ORDER BY U.status, C.sequence_no ");
		LogFactory.printLog("loadBatchDetailsListForEnqQry", strBuff.toString(), log);
		return strBuff.toString();
	}

	
	@Override
	public String loadBatchDetailsListForEnqSelectServicesQry() {
		final StringBuilder selectServices = new StringBuilder("SELECT US.service_type, US.user_id, US.status, ST.name");
		selectServices.append(" FROM user_services US right join service_type ST on  US.service_type=ST.service_type ,users U,category_service_type CST");
		selectServices.append(" WHERE US.user_id=? AND US.status='Y' ");
		selectServices.append(" AND U.user_id=US.user_id AND U.category_code=CST.category_code AND CST.service_type=US.service_type and CST.network_code=U.network_code");

		LogFactory.printLog("loadBatchDetailsListForEnqSelectServicesQry", selectServices.toString(), log);
		return selectServices.toString();
	}

	@Override
	public String loadBatchDetailsListForEnqSelectRolesQry() {
		final StringBuilder selectRoles = new StringBuilder(
				"SELECT DISTINCT UR.role_code, R.role_name FROM user_roles UR right join roles R on UR.role_code=R.role_code  WHERE UR.user_id=?  ORDER BY UR.role_code");
		LogFactory.printLog("loadBatchDetailsListForEnqSelectRolesQry", selectRoles.toString(), log);
		return selectRoles.toString();
	}

	@Override
	public PreparedStatement loadBatchUserListForModifyQry(Connection con, String categoryCode, String geographyCode, String userID ) throws SQLException {
		boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
		final StringBuilder strBuff = new StringBuilder(" SELECT DISTINCT U.user_id, U.user_name_prefix, U.user_name, U.login_id, U.status,");
		strBuff.append(" U.password, UP.msisdn, UP.sms_pin, U.short_name, D.domain_type_code, ");
		strBuff.append(" U.employee_code, U.external_code, CU.in_suspend, CU.out_suspend,");
		if (lmsAppl) {
			strBuff.append(" CU.lms_profile, ");
		}
		strBuff.append(" CU.alert_email,CU.alert_msisdn,CU.alert_type, ");
		strBuff.append(" CU.low_bal_alert_allow, ");
		strBuff.append(" CU.trf_rule_type,");
		strBuff.append(" CU.outlet_code,CU.suboutlet_code, CU.contact_person, U.contact_no,");
		strBuff.append(" U.ssn, U.designation, U.address1, U.address2, U.city,U.state,U.country,U.company,U.fax,U.firstname,U.lastname, U.email, U.rsaflag,U.authentication_allowed,");
		strBuff.append(" C.category_name,U.longitude,U.latitude,U.document_type, U.document_no, U.payment_type,CU.COMM_PROFILE_SET_ID,CU.TRANSFER_PROFILE_ID,CU.USER_GRADE");
		strBuff.append(" FROM  user_phones UP right join users U on ( UP.user_id=U.user_id AND  UP.primary_number=?) , user_geographies UG, channel_users CU, categories C, domains D");
		strBuff.append(" WHERE U.category_code=C.category_code AND C.domain_code=D.domain_code");
		strBuff.append(" AND U.user_id=UG.user_id AND U.user_id=CU.user_id AND U.status NOT IN(?, ?) ");
		strBuff.append(" AND U.user_type =? AND U.category_code= ? ");
		strBuff.append("  AND UG.grph_domain_code IN (" );

		strBuff.append(" WITH RECURSIVE q AS (");
		strBuff.append(" SELECT GD1.grph_domain_code, gd1.status FROM geographical_domains GD1 WHERE  ");
		strBuff.append(" GD1.grph_domain_code IN (SELECT grph_domain_code FROM user_geographies ug1 WHERE UG1.grph_domain_code =( case when ? =  '" + PretupsI.ALL + "' then UG1.grph_domain_code else  ? end )  AND UG1.user_id=? )");
		strBuff.append(" UNION ALL ");
		strBuff.append(" SELECT GD2.grph_domain_code , gd2.status FROM geographical_domains GD2 ");
		strBuff.append(" JOIN q on  q.grph_domain_code = GD2.parent_grph_domain_code ");
		strBuff.append(" )SELECT grph_domain_code FROM q");
		strBuff.append(" WHERE status IN(?, ?) ");
		strBuff.append( ")");

		PreparedStatement pstmtSelect = con.prepareStatement(strBuff.toString());
		int i = 1;
		pstmtSelect.setString(i++, PretupsI.YES);
		pstmtSelect.setString(i++, PretupsI.USER_STATUS_DELETED);
		pstmtSelect.setString(i++, PretupsI.USER_STATUS_CANCELED);
		pstmtSelect.setString(i++, TypesI.CHANNEL_USER_TYPE);
		pstmtSelect.setString(i++, categoryCode);
		pstmtSelect.setString(i++, geographyCode);
		pstmtSelect.setString(i++, geographyCode);
		pstmtSelect.setString(i++, userID);
		pstmtSelect.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
		pstmtSelect.setString(i, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND);

		return pstmtSelect;
	}

	@Override
	public PreparedStatement loadMasterGeographyForCategoryListQry(Connection con, String category, String geographicsCode, String loginUserID) throws SQLException {
		final StringBuilder strBuff = new StringBuilder();

		strBuff.append(" WITH RECURSIVE q AS ( ");
		strBuff.append(" SELECT distinct GD.grph_domain_code ,GDT.grph_domain_type GDT_grph_domain_type,  gd.status,C.status c_status,  c.category_code c_category_code,C.grph_domain_type C_grph_domain_type, GD.grph_domain_name , GD.grph_domain_type ,  GDT.sequence_no, GDT.grph_domain_type_name ");
		strBuff.append(" FROM geographical_domains GD,geographical_domain_types GDT, categories C ");
		strBuff.append(" WHERE GD.grph_domain_code IN(SELECT UG1.grph_domain_code FROM user_geographies UG1");
		strBuff.append(" WHERE UG1.grph_domain_code = case ? when '" + PretupsI.ALL + "' then UG1.grph_domain_code else ? end ");
		strBuff.append(" AND UG1.user_id=?) ");
		strBuff.append(" UNION ALL");
		strBuff.append(" SELECT distinct GD1.grph_domain_code,GDT1.grph_domain_type GDT_grph_domain_type ,gd1.status,C1.status c_status,  c1.category_code c_category_code,C1.grph_domain_type C_grph_domain_type, GD1.grph_domain_name , GD1.grph_domain_type ,  GDT1.sequence_no, GDT1.grph_domain_type_name ");
		strBuff.append(" FROM geographical_domain_types GDT1, categories C1 , geographical_domains GD1");
		strBuff.append(" JOIN q on q.grph_domain_code = GD1.parent_grph_domain_code ");
		strBuff.append(" )");
		strBuff.append(" SELECT distinct grph_domain_code geography_code,grph_domain_name geography_name, grph_domain_type grph_domain_type,sequence_no, grph_domain_type_name from q"); 
		strBuff.append(" WHERE status IN('Y', 'S')   AND C_category_code=? AND C_grph_domain_type=GDT_grph_domain_type AND C_status='Y' ");
		strBuff.append(" AND GDT_grph_domain_type = grph_domain_type ");
		
		LogFactory.printLog("loadMasterGeographyForCategoryListQry", strBuff.toString(), log);
		PreparedStatement pstmtSelect = con.prepareStatement(strBuff.toString());
		int i = 1;
		pstmtSelect.setString(i++, geographicsCode);
		pstmtSelect.setString(i++, geographicsCode);
		pstmtSelect.setString(i++, loginUserID);
		pstmtSelect.setString(i, category);
		return pstmtSelect;
	}

	@Override
	public String loadGeographyAndDomainDetailsQry() {
		StringBuilder strbuff = new StringBuilder();
		strbuff.append(" WITH RECURSIVE q AS ( ");
		strbuff.append(" select gd.GRPH_DOMAIN_CODE,gdt.SEQUENCE_NO, gd.GRPH_DOMAIN_TYPE, gdt.GRPH_DOMAIN_TYPE gdt_GRPH_DOMAIN_TYPE,gd.PARENT_GRPH_DOMAIN_CODE from GEOGRAPHICAL_DOMAINS gd, GEOGRAPHICAL_DOMAIN_TYPES gdt ");
		strbuff.append(" where ");
		strbuff.append(" gd.GRPH_DOMAIN_CODE in (select GEOGRAPHY_CODE from batch_geographies where BATCH_ID = ?)");
		strbuff.append(" UNION ALL");
		strbuff.append(" select gd2.GRPH_DOMAIN_CODE,gdt2.SEQUENCE_NO, gd2.GRPH_DOMAIN_TYPE, gdt2.GRPH_DOMAIN_TYPE gdt_GRPH_DOMAIN_TYPE, gd2.PARENT_GRPH_DOMAIN_CODE from  GEOGRAPHICAL_DOMAIN_TYPES gdt2 , GEOGRAPHICAL_DOMAINS gd2");
		strbuff.append(" join q on q.PARENT_GRPH_DOMAIN_CODE=gd2.GRPH_DOMAIN_CODE");
		strbuff.append(" ) select GRPH_DOMAIN_CODE from q ");
		strbuff.append(" where gdt_GRPH_DOMAIN_TYPE = GRPH_DOMAIN_TYPE and SEQUENCE_NO=2");

		return strbuff.toString();
	}
	
	@Override
	public PreparedStatement loadBatchUserListForProfileAssociateQry(
			Connection con, String categoryCode, String geographyCode,
			String userId) throws SQLException{
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT DISTINCT U.user_id, U.user_name_prefix, U.user_name,");
		strBuff.append(" UP.msisdn, D.domain_type_code,");
    	strBuff.append(" U.info1, U.info2, U.info3, U.info4, U.info5, U.info6, U.info7, U.info8, U.info9, U.info10,");
    	strBuff.append(" U.external_code,");
    	strBuff.append(" U.firstname,U.lastname,");
    	strBuff.append(" C.category_name,");
    	strBuff.append(" CU.user_grade,CU.transfer_profile_id,CU.comm_profile_set_id");
    	strBuff.append(" FROM user_phones UP right join users U on UP.user_id=U.user_id AND UP.primary_number=?, user_geographies UG,  channel_users CU, categories C, domains D, channel_grades CG, commission_profile_set CP, transfer_profile TP");
    	strBuff.append(" WHERE  U.category_code=C.category_code AND C.domain_code=D.domain_code" );
    	strBuff.append(" AND U.user_id=UG.user_id AND U.user_id=CU.user_id AND U.status NOT IN(?,?) ");
    	strBuff.append(" AND U.user_type =? AND U.category_code=? " );
    	strBuff.append(" AND CU.comm_profile_set_id=CP.comm_profile_set_id AND CU.transfer_profile_id=TP.profile_id AND CU.user_grade=CG.grade_code");
    	strBuff.append(" AND UG.grph_domain_code IN ( ");
    	
    	strBuff.append(" WITH RECURSIVE q AS ( ");
    	strBuff.append(" SELECT GD1.grph_domain_code, gd1.status FROM  geographical_domains GD1 WHERE ");
    	strBuff.append(" GD1.grph_domain_code IN  ");
    	strBuff.append(" (SELECT grph_domain_code FROM user_geographies ug1 WHERE UG1.grph_domain_code =( case ? when '"+PretupsI.ALL+"' then UG1.grph_domain_code else ? end)  AND UG1.user_id=? )");
    	strBuff.append(" UNION ALL ");
    	strBuff.append(" SELECT GD2.grph_domain_code, gd2.status FROM  geographical_domains GD2 ");
    	strBuff.append(" join q on q.grph_domain_code = GD2.parent_grph_domain_code ");
    	strBuff.append(" ) SELECT grph_domain_code FROM q");
    	strBuff.append(" WHERE status IN(?, ?) ");

    	strBuff.append( ")");       
   
    	String sqlSelect = strBuff.toString();
    	LogFactory.printLog("loadBatchUserListForProfileAssociateQry", sqlSelect, log);
    	PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);
    	int i=1;
		
		pstmtSelect.setString(i++,PretupsI.YES);//primary_number
		pstmtSelect.setString(i++,PretupsI.USER_STATUS_DELETED);// sattus in 1
		pstmtSelect.setString(i++,PretupsI.USER_STATUS_CANCELED);// sattus in 2
		pstmtSelect.setString(i++,TypesI.CHANNEL_USER_TYPE);//user_type
		pstmtSelect.setString(i++,categoryCode);//category_code
		pstmtSelect.setString(i++,geographyCode);//grph_domain_code
		pstmtSelect.setString(i++,geographyCode);//grph_domain_code
		pstmtSelect.setString(i++,userId);//user_id
		pstmtSelect.setString(i++,PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);// status in 1
		pstmtSelect.setString(i,PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND);// status in 2
	
		return pstmtSelect;
	}

	@Override
	public PreparedStatement loadBatchUserListForProfileAssociateTcpQry(
			Connection con, String categoryCode, String geographyCode,
			String userId) throws SQLException{
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT DISTINCT U.user_id, U.user_name_prefix, U.user_name,");
		strBuff.append(" UP.msisdn, D.domain_type_code,");
    	strBuff.append(" U.info1, U.info2, U.info3, U.info4, U.info5, U.info6, U.info7, U.info8, U.info9, U.info10,");
    	strBuff.append(" U.external_code,");
    	strBuff.append(" U.firstname,U.lastname,");
    	strBuff.append(" C.category_name,");
    	strBuff.append(" CU.user_grade,CU.transfer_profile_id,CU.comm_profile_set_id");
    	strBuff.append(" FROM user_phones UP right join users U on UP.user_id=U.user_id AND UP.primary_number=?, user_geographies UG,  channel_users CU, categories C, domains D, channel_grades CG, commission_profile_set CP ");
    	strBuff.append(" WHERE  U.category_code=C.category_code AND C.domain_code=D.domain_code" );
    	strBuff.append(" AND U.user_id=UG.user_id AND U.user_id=CU.user_id AND U.status NOT IN(?,?) ");
    	strBuff.append(" AND U.user_type =? AND U.category_code=? " );
    	strBuff.append(" AND CU.comm_profile_set_id=CP.comm_profile_set_id  AND CU.user_grade=CG.grade_code");
    	strBuff.append(" AND UG.grph_domain_code IN ( ");
    	
    	strBuff.append(" WITH RECURSIVE q AS ( ");
    	strBuff.append(" SELECT GD1.grph_domain_code, gd1.status FROM  geographical_domains GD1 WHERE ");
    	strBuff.append(" GD1.grph_domain_code IN  ");
    	strBuff.append(" (SELECT grph_domain_code FROM user_geographies ug1 WHERE UG1.grph_domain_code =( case ? when '"+PretupsI.ALL+"' then UG1.grph_domain_code else ? end)  AND UG1.user_id=? )");
    	strBuff.append(" UNION ALL ");
    	strBuff.append(" SELECT GD2.grph_domain_code, gd2.status FROM  geographical_domains GD2 ");
    	strBuff.append(" join q on q.grph_domain_code = GD2.parent_grph_domain_code ");
    	strBuff.append(" ) SELECT grph_domain_code FROM q");
    	strBuff.append(" WHERE status IN(?, ?) ");

    	strBuff.append( ")");       
   
    	String sqlSelect = strBuff.toString();
    	LogFactory.printLog("loadBatchUserListForProfileAssociateQry", sqlSelect, log);
    	PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);
    	int i=1;
		
		pstmtSelect.setString(i++,PretupsI.YES);//primary_number
		pstmtSelect.setString(i++,PretupsI.USER_STATUS_DELETED);// sattus in 1
		pstmtSelect.setString(i++,PretupsI.USER_STATUS_CANCELED);// sattus in 2
		pstmtSelect.setString(i++,TypesI.CHANNEL_USER_TYPE);//user_type
		pstmtSelect.setString(i++,categoryCode);//category_code
		pstmtSelect.setString(i++,geographyCode);//grph_domain_code
		pstmtSelect.setString(i++,geographyCode);//grph_domain_code
		pstmtSelect.setString(i++,userId);//user_id
		pstmtSelect.setString(i++,PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);// status in 1
		pstmtSelect.setString(i,PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND);// status in 2
	
		return pstmtSelect;
	}

	@Override
	public String loadCommProfileListQry() {
		StringBuilder strBuff = new StringBuilder(" SELECT CAT.category_code, CAT.CATEGORY_NAME, CPS.GRADE_CODE, CG.GRADE_NAME, CPS.GEOGRAPHY_CODE, ");
    	strBuff.append(" GD.GRPH_DOMAIN_NAME, CPS.comm_profile_set_id, CPS.comm_profile_set_name,CAT.SEQUENCE_NO,CPS.STATUS " );
    	strBuff.append(" FROM commission_profile_set CPS left join CHANNEL_GRADES CG on CPS.GRADE_CODE=CG.GRADE_CODE left join GEOGRAPHICAL_DOMAINS GD on GD.GRPH_DOMAIN_CODE=CPS.GEOGRAPHY_CODE,  categories CAT  ");
		strBuff.append(" WHERE CAT.domain_code=? AND CAT.category_code=CPS.category_code ");
		strBuff.append(" AND CAT.status='Y' AND CPS.status!='N' AND CPS.network_code=? AND CPS.category_code=? order by CAT.SEQUENCE_NO, CPS.GEOGRAPHY_CODE,CPS.GRADE_CODE,CPS.comm_profile_set_id");
		LogFactory.printLog("loadCommProfileListQry", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public PreparedStatement loadBatchUserListForModifyPOIQry(Connection con,
			String categoryCode, String geographyCode, String userId)
			throws SQLException {
			boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
			final StringBuilder strBuff = new StringBuilder(" SELECT DISTINCT U.user_id, U.user_name_prefix, U.user_name, U.login_id, U.status,");
	        strBuff.append(" U.password, UP.msisdn, UP.sms_pin, U.short_name, D.domain_type_code, ");
	        strBuff.append(" U.employee_code, U.external_code, CU.in_suspend, CU.out_suspend,CU.COMM_PROFILE_SET_ID,cu.TRANSFER_PROFILE_ID,cu.USER_GRADE,");
	        if (lmsAppl) {
	            strBuff.append(" CU.lms_profile, ");
	        }
	        strBuff.append(" CU.alert_email,CU.alert_msisdn,CU.alert_type, ");
	        strBuff.append(" CU.low_bal_alert_allow, ");
	        strBuff.append(" CU.trf_rule_type,");
	        strBuff.append(" CU.outlet_code,CU.suboutlet_code, CU.contact_person, U.contact_no,");
	        strBuff.append(" U.ssn, U.designation, U.address1, U.address2, U.city,U.state,U.country,U.company,U.fax,U.firstname,U.lastname, U.email, U.rsaflag,U.authentication_allowed,");
	        strBuff.append(" C.category_name,U.longitude,U.latitude,U.document_type, U.document_no, U.payment_type");
	        strBuff.append(" FROM user_phones UP right join users U on UP.user_id=U.user_id AND UP.primary_number=? , user_geographies UG,  channel_users CU, categories C, domains D");
	        strBuff.append(" WHERE  U.category_code=C.category_code AND C.domain_code=D.domain_code");
	        strBuff.append(" AND U.user_id=UG.user_id AND U.user_id=CU.user_id AND U.status NOT IN(?,?) ");
	        strBuff.append(" AND U.user_type =? AND U.category_code=? ");
	        strBuff.append(" AND UG.grph_domain_code IN (" );

	        strBuff.append(" WITH RECURSIVE q AS ( ");
	        strBuff.append(" SELECT grph_domain_code, status FROM geographical_domains GD1 WHERE ");
	        strBuff.append(" grph_domain_code IN  (SELECT grph_domain_code FROM user_geographies ug1 WHERE UG1.grph_domain_code =( case ? when '" + PretupsI.ALL + "' then UG1.grph_domain_code else ? end )  AND UG1.user_id=? )");
	        strBuff.append(" UNION ALL");
	        strBuff.append(" SELECT GD2.grph_domain_code,  GD2.status FROM geographical_domains GD2");
	        strBuff.append(" join q on q.grph_domain_code = GD2.parent_grph_domain_code");
	        strBuff.append(" )SELECT grph_domain_code FROM q");
	        strBuff.append(" WHERE status IN(?, ?)");

	       strBuff.append( ")");
	        
	        LogFactory.printLog("loadBatchUserListForModifyPOIQry", strBuff.toString(), log);
	        PreparedStatement pstmtSelect = con.prepareStatement(strBuff.toString());
	        int i =1;
            pstmtSelect.setString(i++, PretupsI.YES);
	        pstmtSelect.setString(i++, PretupsI.USER_STATUS_DELETED);
            pstmtSelect.setString(i++, PretupsI.USER_STATUS_CANCELED);
            pstmtSelect.setString(i++, TypesI.CHANNEL_USER_TYPE);
            pstmtSelect.setString(i++, categoryCode);
            pstmtSelect.setString(i++, geographyCode);
            pstmtSelect.setString(i++, geographyCode);
            pstmtSelect.setString(i++, userId);
            pstmtSelect.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
            pstmtSelect.setString(i, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND);
		return pstmtSelect;
	}
	
	@Override
	public String loadBatchDetailsListForUsrEnqQry() {
		final StringBuilder strBuff = new StringBuilder(
				" SELECT DISTINCT U.user_id, U.user_name, USR_PRT.status parent_status, C.sequence_no, U.network_code, U.login_id, U.password, U.category_code, U.parent_id, U.owner_id, U.allowed_ip,");
		strBuff.append(" U.allowed_days, U.from_time, U.to_time, U.last_login_on, U.employee_code, U.status, LK1.lookup_name StatusDesc, U.email, U.pswd_modified_on,");
		strBuff.append(" U.contact_no, U.designation, U.division, U.department, U.msisdn, U.user_type, U.created_by, U.created_on, U.modified_by, U.modified_on,");
		strBuff.append(" U.address1, U.address2, U.city, U.state, U.country, U.ssn, U.user_name_prefix, U.external_code, U.user_code, U.short_name, U.reference_id, U.firstname, U.lastname, U.company, U.fax,");
		strBuff.append(" U.invalid_password_count, U.level1_approved_by, U.level1_approved_on, U.level2_approved_by, U.level2_approved_on,");
		strBuff.append(" U.appointment_date, U.password_count_updated_on, U.previous_status, U.batch_id, U.creation_type, U.remarks user_remarks,");
		strBuff.append(" CU.user_grade, CU.transfer_profile_id, TP.profile_name, CU.suboutlet_code,(case when CU.suboutlet_code = '' then '' else SLK.sub_lookup_name end) sub_lookup_name, CU.outlet_code,");
		strBuff.append( " (case when CU.outlet_code = '' then '' else LK2.lookup_name end ) OutletName, CU.out_suspend, CU.in_suspend, CU.contact_person,");
		strBuff.append(" CU.comm_profile_set_id, CPS.comm_profile_set_name, CU.activated_on, ");
		strBuff.append(" USR_PRT.login_id parent_login_id, USR_PRT.msisdn parent_msisdn, UG.grph_domain_code, GD.grph_domain_name, C.category_name, C.fixed_roles, C.services_allowed,");
		strBuff.append(" cu.application_id, cu.mpay_profile_id, cu.user_profile_id, cu.is_primary, cu.mcommerce_service_allow, cu.low_bal_alert_allow");
		strBuff.append(", u.rsaflag, u.authentication_allowed");
		strBuff.append(" from  users U right join categories C on  C.category_code=U.category_code  left join users USR_PRT on  U.parent_id = USR_PRT.user_id ,channel_users CU,  user_geographies UG,");
		strBuff.append("  lookups LK1, lookups LK2, geographical_domains GD, sub_lookups SLK, commission_profile_set CPS, transfer_profile TP , ");
		strBuff.append("  ( with recursive q as( ");
		strBuff.append("		 SELECT USR.user_id, USR.parent_id, USR.OWNER_ID  "); 
		strBuff.append("		 FROM users USR  "); 
		strBuff.append("		 where USR.user_id=? "); 
		strBuff.append("		    union all  ");
		strBuff.append("		 SELECT USR.user_id, USR.parent_id, USR.OWNER_ID "); 
		strBuff.append("		 FROM users USR join q on q.user_id = USR.parent_id  ");
		strBuff.append("		    ) select user_id, parent_id, OWNER_ID from q ) X ");
		strBuff.append(" WHERE U.batch_id=? AND   U.USER_ID = X.user_id  AND CU.user_id=U.user_id AND U.network_code=CPS.network_code AND CU.comm_profile_set_id=CPS.comm_profile_set_id  AND GD.grph_domain_code=UG.grph_domain_code");
		strBuff.append(" AND U.user_id=UG.user_id AND C.category_code = U.category_code");
		strBuff.append(" AND SLK.sub_lookup_code=(case when CU.suboutlet_code = '' then (select sub_lookup_code from sub_lookups where lookup_type=? limit 1 ) when CU.suboutlet_code = SLK.sub_lookup_code then CU.suboutlet_code end)");
		strBuff.append(" AND LK1.lookup_code=U.status AND LK2.lookup_code=(case when CU.outlet_code = '' then (select lookup_code from lookups where lookup_type=?  limit 1) when CU.outlet_code = LK2.lookup_code then CU.outlet_code end) AND LK1.lookup_type=?");
		strBuff.append(" AND CU.transfer_profile_id=TP.profile_id AND U.network_code=TP.network_code ");
		strBuff.append(" ORDER BY U.status, C.sequence_no ");
		LogFactory.printLog("loadBatchDetailsListForEnqQry", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public String loadBatchListForUsrEnquiryQry(String[] geographyCode, String userType, String loggedInUserID) {
		final StringBuilder strBuff = new StringBuilder(" SELECT DISTINCT B.batch_id, B.status,LK.lookup_name, B.modified_on, USR_MOD.user_name modified_by,");
		strBuff.append(" B.created_on, USR_CR.user_name created_by,B.batch_type, B.batch_size, B.batch_name, B.file_name, ");
		strBuff.append(" SUM(CASE WHEN U.status = ? then 1 else 0 end) as new,SUM(CASE WHEN U.status = ? then 1 else 0 end) active, SUM(CASE WHEN U.status = ? then 1 else 0 end) reject ");
		strBuff.append(" from batches B left join  users USR_CR on B.created_by=USR_CR.user_id left join  users USR_MOD on B.modified_by= USR_MOD.user_id , users U, batch_geographies BG, categories C,lookups LK,  ");
		strBuff.append("   ( with recursive q as( ");
		strBuff.append("		 SELECT USR.user_id, USR.parent_id, USR.OWNER_ID  "); 
		strBuff.append("		 FROM users USR  "); 
		strBuff.append("		 where USR.user_id=? "); 
		strBuff.append("		    union all  ");
		strBuff.append("		 SELECT USR.user_id, USR.parent_id, USR.OWNER_ID "); 
		strBuff.append("		 FROM users USR join q on q.user_id = USR.parent_id  ");
		strBuff.append("		    ) select user_id, parent_id, OWNER_ID from q ) X ");
		strBuff.append(" WHERE  U.creation_type=? AND  U.user_id =X.user_id  and  BG.geography_code IN ( ");
		strBuff.append(" WITH RECURSIVE  q AS ( ");
		strBuff.append(" select gd1.grph_domain_code from geographical_domains gd1");
		strBuff.append(" where  gd1.grph_domain_code in ( ");
		for (int i = 0; i < geographyCode.length; i++) {
			strBuff.append(" ?");
			if (i != geographyCode.length - 1) {
				strBuff.append(",");
			}
		}
		strBuff.append(")");
		strBuff.append(" UNION  ALL");
		strBuff.append(" select gd2.grph_domain_code from geographical_domains gd2 ");
		strBuff.append(" join q on q.grph_domain_code=gd2.parent_grph_domain_code ");
		strBuff.append(" )select grph_domain_code from q ");

		strBuff.append( ")");

		strBuff.append(" AND B.network_code=? ");
		// end
		strBuff.append(" AND BG.batch_id=B.batch_id AND B.batch_id=U.batch_id AND DATE_TRUNC('day',B.created_on::timestamp)>=? AND DATE_TRUNC('day',B.created_on::timestamp)<=?");
		strBuff.append(" AND C.category_code=U.category_code AND C.domain_code =?");
		strBuff.append(" AND LK.lookup_code=B.status AND LK.lookup_type=?");
		if (userType.equals(PretupsI.CHANNEL_USER_TYPE)) {
			strBuff.append(" AND C.sequence_no > (SELECT sequence_no FROM categories where category_code=?)");
		}
		strBuff.append(" GROUP BY B.batch_id, B.batch_name, B.status, B.modified_on, USR_MOD.user_name , ");
		strBuff.append(" B.created_on, USR_CR.user_name, B.file_name, B.batch_type, B.batch_size, BG.geography_code,LK.lookup_name ");
		strBuff.append(" ORDER BY  B.created_on DESC ");
		LogFactory.printLog("loadBatchListForEnquiryQry", strBuff.toString(), log);
		return strBuff.toString();


	}

	@Override
	public String loadBatchListForEnquiryQryUsr() {
		final StringBuilder strBuff = new StringBuilder(" SELECT B.batch_id, B.status, LK.lookup_name,B.modified_on, B.created_on,");
		strBuff.append(" USR_CR.user_name created_by, B.batch_type, B.batch_size, B.batch_name, B.file_name,");
		strBuff.append(" SUM(CASE WHEN U.status = ? then 1 else 0 end ) as new, SUM(CASE WHEN U.status = ? then 1 else 0 end ) active, SUM(CASE WHEN U.status = ? then 1 else 0 end ) reject ");
		strBuff.append(" FROM batches B  left join users USR_CR on B.created_by=USR_CR.user_id, users U, lookups LK , " );
	    strBuff.append("   ( with recursive q as( ");
		strBuff.append("		 SELECT USR.user_id, USR.parent_id, USR.OWNER_ID  "); 
		strBuff.append("		 FROM users USR  "); 
		strBuff.append("		 where USR.user_id=? "); 
		strBuff.append("		    union all  ");
		strBuff.append("		 SELECT USR.user_id, USR.parent_id, USR.OWNER_ID "); 
		strBuff.append("		 FROM users USR join q on q.user_id = USR.parent_id  ");
		strBuff.append("		    ) select user_id, parent_id, OWNER_ID from q ) X ");
		strBuff.append("  WHERE B.batch_id=U.batch_id AND B.batch_id=? ");
		strBuff.append(" AND U.USER_ID =X.USER_ID AND  U.creation_type=? AND B.network_code=? ");
		strBuff.append(" AND LK.lookup_code=B.status AND LK.lookup_type=?");
		strBuff.append(" GROUP BY B.batch_id, B.batch_name, B.file_name,B.status, B.modified_on,");
		strBuff.append(" B.created_on, B.batch_type, B.batch_size, USR_CR.user_name,LK.lookup_name ORDER BY B.created_on DESC");
		LogFactory.printLog("loadBatchListForEnquiryQry", strBuff.toString(), log);
		return strBuff.toString();
	}

	
	



}
