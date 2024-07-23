package com.btsl.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class ChannelUserOracleQry implements ChannelUserQry{

	private Log log = LogFactory.getLog(this.getClass()) ;

	@Override
	public String loadChannelUserDetailsQry() {
		boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
		boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
		boolean optInOutAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
		final StringBuilder selectQueryBuff = new StringBuilder(
				" SELECT u.user_id, u.password webpassword,u.user_name, u.network_code,u.login_id, u.category_code, u.parent_id, u.owner_id, u.msisdn,");
		selectQueryBuff.append(" u.employee_code,u.status userstatus,u.created_by,u.created_on,u.email,u.modified_by,u.modified_on,");
		selectQueryBuff.append(" cusers.contact_person,u.contact_no,u.designation,u.division,u.department,u.user_type,cusers.in_suspend,cusers.out_suspend,");
		selectQueryBuff.append(" u.address1,u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,u.LONGITUDE,");
		selectQueryBuff.append(" cat.domain_code,dom.domain_name,dom.domain_type_code,cat.sequence_no catseq,cat.sms_interface_allowed,geo.grph_domain_code,gdomains.status geostatus,u.company,u.fax,u.firstname,u.lastname, ");// company

		selectQueryBuff.append(" uphones.user_phones_id,uphones.primary_number, uphones.sms_pin, uphones.pin_required, uphones.phone_profile, uphones.phone_language phlang,");
		selectQueryBuff.append(" uphones.country phcountry, uphones.invalid_pin_count, uphones.last_transaction_status, uphones.last_transaction_on,");
		selectQueryBuff.append(" uphones.pin_modified_on,uphones.last_transfer_id, uphones.last_transfer_type,uphones.prefix_id,uphones.temp_transfer_id, uphones.first_invalid_pin_time, ");
		selectQueryBuff.append(" cat.agent_allowed,cat.hierarchy_allowed, cat.category_type,cat.category_name,cat.grph_domain_type,cusers.comm_profile_set_id,cusers.transfer_profile_id, tp.profile_name, cusers.sos_allowed,cusers.sos_allowed_amount,cusers.sos_threshold_limit,cusers.lr_allowed,cusers.lr_max_amount, tp.status tpstatus,cusers.user_grade,cset.status csetstatus, ");
		selectQueryBuff.append(" cusers.auto_o2c_allow,cusers.autoo2c_transaction_amt,cusers.autoo2c_threshold_value, ");
		selectQueryBuff.append(" cset.language_1_message comprf_lang_1_msg,cset.language_2_message   comprf_lang_2_msg,cset.COMM_PROFILE_SET_NAME, cset.last_dual_comm_type,cat.restricted_msisdns,gdt.sequence_no grphSeq, cat.transfertolistonly, cat.USER_ID_PREFIX, cpsv.applicable_from, cpsv.comm_profile_set_version, ");

		// for Zebra and Tango by sanjeew date 06/07/07
		selectQueryBuff.append(" uphones.access_type, uphones.created_on, cusers.application_id, cusers.mpay_profile_id, cusers.user_profile_id, cusers.mcommerce_service_allow,cusers.low_bal_alert_allow, uphones.created_on userphone_created_on ");
		// end of Zebra and Tango
		// added for loading PIN reset info
		selectQueryBuff.append(" ,uphones.PIN_RESET,uphones.last_access_on,uphones.modified_on,uphones.modified_by,u.from_time,u.to_time,u.allowed_days,u.invalid_password_count,u.pswd_modified_on, cusers.auto_c2c_allow, cusers.auto_c2c_quantity ");
		// added for transfer rule type
		if (isTrfRuleUserLevelAllow) {
			selectQueryBuff.append(", cusers.trf_rule_type  ");
		}
		if (lmsAppl) {
			selectQueryBuff.append(" , cusers.lms_profile  ");
		}
		//added for owner commission
		selectQueryBuff.append(" , ou.category_code own_category_code, ou.msisdn own_msisdn, ou.user_name owner_name, ou.company owner_company ");
		if (optInOutAllow) {
			selectQueryBuff.append(" , cusers.OPT_IN_OUT_STATUS ");
		}
		selectQueryBuff.append(" , cusers.CONTROL_GROUP,gdomains.GRPH_DOMAIN_NAME ");
		selectQueryBuff.append(" FROM users u,user_geographies geo,categories cat,domains dom,channel_users cusers,user_phones uphones,transfer_profile tp,commission_profile_set cset,geographical_domains gdomains,geographical_domain_types gdt,commission_profile_set_version cpsv ");
		//added for owner commission
		selectQueryBuff.append(" , users ou");
		selectQueryBuff.append(" WHERE uphones.msisdn=? AND  u.user_type=? AND uphones.user_id=u.user_id AND u.status <> ? AND u.status <> ? ");
		selectQueryBuff.append(" AND u.user_id=cusers.user_id(+) AND u.category_code = cat.category_code AND u.user_id=geo.user_id AND geo.grph_domain_code=gdomains.grph_domain_code AND cpsv.comm_profile_set_id  = cset.comm_profile_set_id ");
		selectQueryBuff.append(" AND cat.domain_code= dom.domain_code AND cusers.transfer_profile_id=tp.profile_id(+) AND cusers.comm_profile_set_id=cset.comm_profile_set_id(+) AND gdt.grph_domain_type=gdomains.grph_domain_type ");
		selectQueryBuff.append(" AND cpsv.applicable_from      =NVL ((SELECT MAX(applicable_from) FROM commission_profile_set_version WHERE applicable_from <= ? ");
		selectQueryBuff.append(" AND comm_profile_set_id = cusers.comm_profile_set_id), cpsv.applicable_from)");
		//added for owner commission
		selectQueryBuff.append("  AND ou.user_id=u.owner_id");
		LogFactory.printLog("loadChannelUserDetailsQry",selectQueryBuff.toString() , log);
		return selectQueryBuff.toString();
	}

	@Override
	public String loadChannelUserDetailsTcpQry() {
		final StringBuilder selectQueryBuff = new StringBuilder(
				" SELECT u.user_id, u.password webpassword,u.user_name, u.network_code,u.login_id, u.category_code, u.parent_id, u.owner_id, u.msisdn,");
		selectQueryBuff.append(" u.employee_code,u.status userstatus,u.created_by,u.created_on,u.email,u.modified_by,u.modified_on,");
		selectQueryBuff.append(" cusers.contact_person,u.contact_no,u.designation,u.division,u.department,u.user_type,cusers.in_suspend,cusers.out_suspend,");
		selectQueryBuff.append(" u.address1,u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,u.LONGITUDE,");
		selectQueryBuff.append(" cat.domain_code,dom.domain_name,dom.domain_type_code,cat.sequence_no catseq,cat.sms_interface_allowed,geo.grph_domain_code,gdomains.status geostatus,u.company,u.fax,u.firstname,u.lastname, ");// company

		selectQueryBuff.append(" uphones.user_phones_id,uphones.primary_number, uphones.sms_pin, uphones.pin_required, uphones.phone_profile, uphones.phone_language phlang,");
		selectQueryBuff.append(" uphones.country phcountry, uphones.invalid_pin_count, uphones.last_transaction_status, uphones.last_transaction_on,");
		selectQueryBuff.append(" uphones.pin_modified_on,uphones.last_transfer_id, uphones.last_transfer_type,uphones.prefix_id,uphones.temp_transfer_id, uphones.first_invalid_pin_time, ");
		selectQueryBuff.append(" cat.agent_allowed,cat.hierarchy_allowed, cat.category_type,cat.category_name,cat.grph_domain_type,cusers.comm_profile_set_id,cusers.transfer_profile_id,  cusers.sos_allowed,cusers.sos_allowed_amount,cusers.sos_threshold_limit,cusers.lr_allowed,cusers.lr_max_amount, cusers.user_grade,cset.status csetstatus, ");
		selectQueryBuff.append(" cusers.auto_o2c_allow,cusers.autoo2c_transaction_amt,cusers.autoo2c_threshold_value, ");
		selectQueryBuff.append(" cset.language_1_message comprf_lang_1_msg,cset.language_2_message   comprf_lang_2_msg,cset.COMM_PROFILE_SET_NAME, cset.last_dual_comm_type,cat.restricted_msisdns,gdt.sequence_no grphSeq, cat.transfertolistonly, cat.USER_ID_PREFIX, cpsv.applicable_from, cpsv.comm_profile_set_version, ");

		// for Zebra and Tango by sanjeew date 06/07/07
		selectQueryBuff.append(" uphones.access_type, uphones.created_on, cusers.application_id, cusers.mpay_profile_id, cusers.user_profile_id, cusers.mcommerce_service_allow,cusers.low_bal_alert_allow, uphones.created_on userphone_created_on ");
		// end of Zebra and Tango
		// added for loading PIN reset info
		selectQueryBuff.append(" ,uphones.PIN_RESET,uphones.last_access_on,uphones.modified_on,uphones.modified_by,u.from_time,u.to_time,u.allowed_days,u.invalid_password_count,u.pswd_modified_on, cusers.auto_c2c_allow, cusers.auto_c2c_quantity ");
		// added for transfer rule type
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW))).booleanValue()) {
			selectQueryBuff.append(", cusers.trf_rule_type  ");
		}
		if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
			selectQueryBuff.append(" , cusers.lms_profile  ");
		}
		//added for owner commission
		selectQueryBuff.append(" , ou.category_code own_category_code, ou.msisdn own_msisdn, ou.user_name owner_name, ou.company owner_company ");
		if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
			selectQueryBuff.append(" , cusers.OPT_IN_OUT_STATUS ");
		}
		selectQueryBuff.append(" , cusers.CONTROL_GROUP,gdomains.GRPH_DOMAIN_NAME ");
		selectQueryBuff.append(" FROM users u,user_geographies geo,categories cat,domains dom,channel_users cusers,user_phones uphones,commission_profile_set cset,geographical_domains gdomains,geographical_domain_types gdt,commission_profile_set_version cpsv ");
		//added for owner commission
		selectQueryBuff.append(" , users ou");
		selectQueryBuff.append(" WHERE uphones.msisdn=? AND  u.user_type=? AND uphones.user_id=u.user_id AND u.status <> ? AND u.status <> ? ");
		selectQueryBuff.append(" AND u.user_id=cusers.user_id(+) AND u.category_code = cat.category_code AND u.user_id=geo.user_id AND geo.grph_domain_code=gdomains.grph_domain_code AND cpsv.comm_profile_set_id  = cset.comm_profile_set_id ");
		selectQueryBuff.append(" AND cat.domain_code= dom.domain_code  AND cusers.comm_profile_set_id=cset.comm_profile_set_id(+) AND gdt.grph_domain_type=gdomains.grph_domain_type ");
		selectQueryBuff.append(" AND cpsv.applicable_from      =NVL ((SELECT MAX(applicable_from) FROM commission_profile_set_version WHERE applicable_from <= ? ");
		selectQueryBuff.append(" AND comm_profile_set_id = cusers.comm_profile_set_id), cpsv.applicable_from)");
		//added for owner commission
		selectQueryBuff.append("  AND ou.user_id=u.owner_id");
		LogFactory.printLog("loadChannelUserDetailsQry",selectQueryBuff.toString() , log);
		return selectQueryBuff.toString();
	}
	@Override
	public PreparedStatement loadUsersDetailsQry(Connection con,String status, String userId, String statusUsed , String msisdn) throws SQLException{
		boolean isMsisdnAssociationReq = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_MSISDN_ASSOCIATION_REQ);
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
		strBuff.append(" USR.login_id,USR.password password1,USR.category_code usr_category_code,USR.parent_id,USR.reference_id, ");
		strBuff.append(" USR.owner_id,USR.allowed_ip,USR.allowed_days, D.domain_name, ");
		strBuff.append(" USR.from_time,USR.to_time,USR.employee_code,USR.company,USR.fax,USR.firstname,USR.lastname, "); // firstname,lastname,company,fax

		strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,");
		strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
		strBuff.append("USR.created_by, USR_CRBY.user_name created_by_name, USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
		strBuff.append(" USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
		strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
		strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date,USR.LONGITUDE, ");
		strBuff.append("USR.previous_status,USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name, USR_CAT.max_txn_msisdn, ");
		strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type,MOD_USR.user_name request_user_name, USR_CAT.low_bal_alert_allow, ");
		strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, USR_CAT.services_allowed,USR_CAT.transfertolistonly, ");
		strBuff.append("USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed,USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed, USR_CAT.restricted_msisdns, ");
		strBuff.append("PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn,UG.grph_domain_code, GD.grph_domain_name, ");
		strBuff.append("PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn,USR_CAT.category_type, ");
		strBuff.append("ONR_CAT.category_name owner_cat,USR_PHONE.sms_pin user_sms_pin, USR_PHONE.pin_required required,l.lookup_name, D.domain_type_code, ");

		strBuff.append(" USR_PHONE.access_type user_access_type,USR_PHONE.phone_language planguage ,USR_PHONE.country pcountry ");// phone_language,country

		strBuff.append(" ,USR.rsaflag,USR_CAT.AUTHENTICATION_TYPE,USR.AUTHENTICATION_ALLOWED ");
		if (isMsisdnAssociationReq) {
			strBuff.append(" ,chnl_usr.ASSOCIATED_MSISDN,chnl_usr.ASSOCIATED_MSISDN_TYPE,chnl_usr.ASSOCIATED_MSISDN_CDATE,chnl_usr.ASSOCIATED_MSISDN_MDATE ");
		}
		strBuff.append("FROM user_phones USR_PHONE,users USR, users PRNT_USR,users ONR_USR,categories USR_CAT, users MOD_USR, ");
		strBuff.append("categories ONR_CAT, categories  PRNT_CAT,lookups l,user_geographies UG, geographical_domains GD, users USR_CRBY, domains D ");
		strBuff.append(" ,channel_users chnl_usr ");
		strBuff.append(" WHERE USR_PHONE.msisdn=? AND USR_PHONE.user_id=USR.user_id ");

		if ((status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) || (status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE))) {
			strBuff.append("  AND USR.barred_deletion_batchid IS NULL ");
		}

		if (statusUsed.equals(PretupsI.STATUS_IN)) {
			strBuff.append(" AND USR.status IN (" + status + ") ");
		} else if (statusUsed.equals(PretupsI.STATUS_NOTIN)) {
			strBuff.append(" AND USR.status NOT IN (" + status + ") ");
		} else if (statusUsed.equals(PretupsI.STATUS_EQUAL)) {
			strBuff.append(" AND USR.status =? ");
		} else if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
			strBuff.append("  AND USR.status <> ? ");
		}

		strBuff.append("AND USR.parent_id=PRNT_USR.user_id(+) AND USR.owner_id=ONR_USR.user_id ");
		strBuff.append(" AND USR.category_code=USR_CAT.category_code ");
		strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
		strBuff.append("AND PRNT_USR.category_code=PRNT_CAT.category_code(+) ");
		strBuff.append("  AND USR.status = l.lookup_code ");
		strBuff.append("  AND l.lookup_type= ?  ");
		strBuff.append("  AND USR.user_id = UG.user_id ");
		strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
		strBuff.append("  AND MOD_USR.user_id(+) = USR.modified_by ");
		strBuff.append("  AND USR_CRBY.user_id(+) = USR.created_by ");
		strBuff.append("  AND USR_CAT.domain_code=D.domain_code ");
		if (isMsisdnAssociationReq) {
			strBuff.append(" AND chnl_usr.user_id=USR.user_id ");
		}
		if (userId != null) {
			strBuff.append(" AND USR.user_id IN ( ");
			strBuff.append(" SELECT user_id from users ");
			
			 /*
             * Commenting To get Parent User Details also
             */
			//strBuff.append(" where user_id != ?  ");
			strBuff.append(" CONNECT BY PRIOR user_id = parent_id START WITH  user_id=? ) ");
		}
		LogFactory.printLog("loadUsersDetailsQry",strBuff.toString() , log);
		PreparedStatement pstmtSelect = con.prepareStatement(strBuff.toString());
		int i = 1;
		pstmtSelect.setString(i, msisdn);
		i++;
		if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || statusUsed.equals(PretupsI.STATUS_EQUAL)) {
			pstmtSelect.setString(i, status);
			i++;
		}

		pstmtSelect.setString(i, PretupsI.USER_STATUS_TYPE);
		i++;
		if (userId != null) {
			 /*
             * Commenting To get Parent User Details also
             */
			//pstmtSelect.setString(i++, userId);
			pstmtSelect.setString(i, userId);
		}

		return pstmtSelect;
	}

	@Override
	public PreparedStatement loadUsersDetailsByLoginIdQry(Connection con,
			String status, String userId, String statusUsed, String loginId) throws SQLException {
		boolean isMsisdnAssociationReq = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_MSISDN_ASSOCIATION_REQ);
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code, USR.allowd_usr_typ_creation,");
		strBuff.append("USR.login_id,USR.password password1,USR.category_code usr_category_code,USR.parent_id,USR.reference_id, ");
		strBuff.append("USR.owner_id,USR.allowed_ip,USR.allowed_days,");
		strBuff.append("USR.from_time,USR.to_time,USR.employee_code,USR.company,USR.fax,USR.firstname,USR.lastname, ");
		strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,USR.contact_person,");
		strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
		strBuff.append("USR.created_by, USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
		strBuff.append("USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
		strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
		strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date, ");
		strBuff.append("USR.previous_status,USR.rsaflag,USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name, USR_CAT.max_txn_msisdn, ");
		strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type, USR_CAT.transfertolistonly, USR_CAT.low_bal_alert_allow, ");
		strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, USR_CAT.services_allowed, ");
		strBuff.append("USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed, USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed,USR_CAT.restricted_msisdns, ");
		strBuff.append("USR_CAT.category_type,");
		strBuff.append("l.lookup_name, UG.grph_domain_code, GD.grph_domain_name, D.domain_type_code,GD.parent_grph_domain_code ");
		strBuff.append(",UP.phone_language,UP.country phcountry,USR_CAT.AUTHENTICATION_TYPE,USR.AUTHENTICATION_ALLOWED, D.domain_name ");
		if (isMsisdnAssociationReq) {
			strBuff.append(" ,chnl_usr.ASSOCIATED_MSISDN,chnl_usr.ASSOCIATED_MSISDN_TYPE,chnl_usr.ASSOCIATED_MSISDN_CDATE,chnl_usr.ASSOCIATED_MSISDN_MDATE ");
		}
		strBuff.append("FROM users USR,categories USR_CAT,user_phones UP,");
		strBuff.append("lookups l,user_geographies UG, geographical_domains GD, domains D ");
		if (isMsisdnAssociationReq) {
			strBuff.append(" ,channel_users chnl_usr ");
		}
		strBuff.append("WHERE UPPER(USR.login_id)=UPPER(?) ");
		if ((status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) || (status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE))) {
			strBuff.append(" AND USR.barred_deletion_batchid IS NULL");
		}
		if (statusUsed.equals(PretupsI.STATUS_IN)) {
			strBuff.append(" AND USR.status IN (" + status + ") ");
		} else if (statusUsed.equals(PretupsI.STATUS_NOTIN)) {
			strBuff.append(" AND USR.status NOT IN (" + status + ") ");
		} else if (statusUsed.equals(PretupsI.STATUS_EQUAL)) {
			strBuff.append(" AND USR.status =? ");
		} else if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
			strBuff.append(" AND USR.status <> ? ");
		}
		strBuff.append(" AND USR_CAT.category_code(+)=USR.category_code");
		strBuff.append(" AND USR.status = l.lookup_code ");
		strBuff.append(" AND UP.user_id(+) =USR.user_id "); 
		strBuff.append(" AND l.lookup_type= ? ");
		strBuff.append(" AND UG.user_id(+)=USR.user_id ");
		strBuff.append(" AND GD.grph_domain_code(+)=  UG.grph_domain_code ");
		strBuff.append(" AND D.domain_code(+)=USR_CAT.domain_code  ");
		if (isMsisdnAssociationReq) {
			strBuff.append(" AND chnl_usr.user_id=USR.user_id ");
		}
		if (userId != null) {
			strBuff.append(" AND USR.user_id IN ( ");
			strBuff.append(" SELECT user_id from users ");
            /*
             * Commenting To get Parent User Details also
             */
			/*strBuff.append(" where user_id != ? ");*/
			strBuff.append(" CONNECT BY PRIOR user_id = parent_id START WITH  user_id=? ) ");
		}
		LogFactory.printLog("loadUsersDetailsByLoginIdQry", strBuff.toString(), log);
		PreparedStatement pstmtSelect = con.prepareStatement(strBuff.toString());
		int i = 1;
		pstmtSelect.setString(i++, loginId);
		if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || statusUsed.equals(PretupsI.STATUS_EQUAL)) {
			pstmtSelect.setString(i++, status);
		}
		pstmtSelect.setString(i++, PretupsI.USER_STATUS_TYPE);
		if (userId != null) {
			 /*
             * Commenting To get Parent User Details also
             */
			//pstmtSelect.setString(i++, userId);
			pstmtSelect.setString(i, userId);
		}
		return pstmtSelect;
	}

	@Override
	public String creditUserBalancesQry() {
		final StringBuilder strBuffSelect = new StringBuilder();
		strBuffSelect.append(" SELECT  balance , balance_type FROM user_balances ");
		strBuffSelect.append(" WHERE balance_type = ?");
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
			strBuffSelect.append(" AND user_id = ? and product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance with RS");
		} else {
			strBuffSelect.append(" AND user_id = ? and product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance ");
		}
		LogFactory.printLog("creditUserBalancesQry", strBuffSelect.toString(), log);
		return strBuffSelect.toString();
	}

	@Override
	public String debitUserBalancesQry() {
		final StringBuilder strBuffSelect = new StringBuilder();
		strBuffSelect.append(" SELECT balance,balance_type FROM user_balances");
		// DB220120123for update WITH RS
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
		strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance with RS");
		} else {
			strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance");
		}
		LogFactory.printLog("debitUserBalancesQry", strBuffSelect.toString(), log);
		return strBuffSelect.toString();
	}

	@Override
	public PreparedStatement loadUserForChannelByPassQry(Connection con, String statusAllowed,
			String networkCode,String toCategoryCode ,String userId,String userName ,String parentID) throws SQLException{
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT user_id, user_name,msisdn,login_id FROM users ");
		strBuff.append(" WHERE network_code=? AND status IN (" + statusAllowed + ") AND category_code=? AND user_id != ?");
		// here user_id != ? check is for not to load the sender user in the
		// query for the same level transactions
		strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) AND  parent_id<>?");
		strBuff.append(" AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
		strBuff.append(" CONNECT BY PRIOR user_id=parent_id START WITH  parent_id=? ");
		strBuff.append(" ORDER BY user_name ");
		PreparedStatement pstmt = con.prepareStatement(strBuff.toString());
		int i = 0;
		i++;
		pstmt.setString(i, networkCode);
		i++;
		pstmt.setString(i, toCategoryCode);
		i++;
		pstmt.setString(i, userId);
		i++;
		pstmt.setString(i, userName);
		i++;
		pstmt.setString(i, parentID);
		i++;
		pstmt.setString(i, parentID);
		LogFactory.printLog("debitUserBalancesQry", strBuff.toString(), log);
		return pstmt;
	}

	@Override
	public PreparedStatement loadUsersByParentIDRecursiveQry(Connection con,String statusAllowed,
			String networkCode, String toCategoryCode, String parentID,
			String userName, String userId)
					throws SQLException {
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT user_id, user_name, msisdn, login_id FROM users  ");
		strBuff.append(" WHERE network_code = ? AND status IN (" + statusAllowed + ") AND category_code = ? AND user_id != ?");
		strBuff.append(" AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
		// here user_id != ? check is for not to load the sender user in the
		// query for the same level transactions
		strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) ");
		strBuff.append(" CONNECT BY PRIOR user_id=parent_id START WITH  parent_id = ? ");
		strBuff.append(" ORDER BY user_name ");
		final String sqlSelect = strBuff.toString();
		LogFactory.printLog("loadUsersByParentIDRecursiveQry",  "QUERY sqlSelect=" + sqlSelect, log);
		PreparedStatement pstmt = con.prepareStatement(sqlSelect);
		int i = 0;
		i++;
		pstmt.setString(i, networkCode);
		i++;
		pstmt.setString(i, toCategoryCode);
		i++;
		pstmt.setString(i, userId);
		i++;
		pstmt.setString(i, userName);
		i++;
		pstmt.setString(i, parentID);
		return pstmt;
	}

	@Override
	public PreparedStatement loadUserHierarchyListQry(Connection con,String statusUsed,String mode, String status, String []userId, String userCategory, PreparedStatement pstmt )throws SQLException {
		boolean staffAsUser = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.STAFF_AS_USER);
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT LEVEL l,UCAT.user_id_prefix,USR.user_id, USR.user_name, USR.network_code, USR.login_id, ");
		strBuff.append(" USR.category_code, USR.parent_id, USR.owner_id,USR.company,USR.fax,USR.firstname,USR.lastname, "); 
		strBuff.append(" USR.last_login_on, USR.employee_code, USR.status, ");
		strBuff.append("USR.pswd_modified_on,  USR.contact_no,  ");
		strBuff.append("USR.division, USR.department,USR.msisdn, USR.user_type, USR.created_by, USR.created_on, ");
		strBuff.append("USR.modified_by, USR.modified_on,");
		strBuff.append("USR.external_code,USR.user_code, USR.short_name, ");
		strBuff.append("USR.reference_id, USR.invalid_password_count, level1_approved_by, ");
		strBuff.append("level1_approved_on, level2_approved_by, USR.level2_approved_on, USR.appointment_date, ");
		strBuff.append("USR.password_count_updated_on,USR.previous_status ");
		strBuff.append(", CU.low_bal_alert_allow,c.grph_domain_type,  ");
		strBuff.append("USR.category_code catcode,UCAT.user_id_prefix FROM users USR, categories UCAT, channel_users CU, CATEGORIES c ");
		strBuff.append("WHERE ");
		if (statusUsed.equals(PretupsI.STATUS_IN)) {
			strBuff.append("USR.status IN (" + status + ")");
		} else if (statusUsed.equals(PretupsI.STATUS_NOTIN)) {
			strBuff.append("USR.status NOT IN (" + status + ")");
		} else if (statusUsed.equals(PretupsI.STATUS_EQUAL)) {
			strBuff.append("USR.status =? ");
		} else if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
			strBuff.append("USR.status <> ? ");
		}
		int i = 1;

		strBuff.append(" AND UCAT.category_code=USR.category_code AND CU.USER_ID=USR.USER_ID AND c.category_code=USR.category_code ");
		if (staffAsUser) {
			strBuff.append(" AND (USR.user_type='CHANNEL' OR USR.user_type='STAFF' ) ");
		} else {
			strBuff.append(" AND USR.user_type='CHANNEL' ");
		}
		if (mode.equalsIgnoreCase(PretupsI.SINGLE)) {

			strBuff.append(" CONNECT BY PRIOR USR.user_id = parent_id ");
			strBuff.append(" START WITH USR.user_id in (SELECT user_id FROM users WHERE user_id=? AND  ");
			strBuff.append(" category_code=? ) ");

			pstmt = con.prepareStatement(strBuff.toString());
			if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || statusUsed.equals(PretupsI.STATUS_EQUAL)) {
				pstmt.setString(i++, status);
			}
			pstmt.setString(i++, userId[0]);
		} else if (mode.equalsIgnoreCase(PretupsI.MULTIPLE)) {
			final StringBuilder str = new StringBuilder();
			for (int k = 0; k < userId.length; k++) {
				if (!BTSLUtil.isNullString(userId[k])) {
					str.append("'");
					str.append(userId[k]);
					str.append("',");
				}
			}
			final String userID = str.substring(0, str.length() - 1);
			strBuff.append(" CONNECT BY PRIOR USR.user_id = parent_id ");
			strBuff.append(" START WITH USR.user_id in (SELECT user_id FROM users WHERE user_id in(" + userID + ") AND");
			strBuff.append(" category_code=? )   ");

			pstmt = con.prepareStatement(strBuff.toString());
			if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || statusUsed.equals(PretupsI.STATUS_EQUAL)) {
				pstmt.setString(i++, status);
			}
		} else if (mode.equalsIgnoreCase(PretupsI.ALL)) {
			strBuff.append(" CONNECT BY PRIOR USR.user_id = parent_id ");
			strBuff.append(" START WITH USR.user_id in (SELECT user_id FROM users WHERE (parent_id=? OR user_id=? )AND  ");
			strBuff.append(" category_code=? ) ");

			pstmt = con.prepareStatement(strBuff.toString());
			if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || statusUsed.equals(PretupsI.STATUS_EQUAL)) {
				pstmt.setString(i++, status);
			}
			pstmt.setString(i++, userId[0]);
			pstmt.setString(i++, userId[0]);
		}
		pstmt.setString(i, userCategory);
		LogFactory.printLog("loadUserHierarchyListQry", strBuff.toString(), log);
		return pstmt;
	}

	@Override
	public PreparedStatement isUserExistForChannelByPassQry(Connection con, String networkCode, String toCategoryCode, String parentID, String userCode, String statusAllowed) throws SQLException {
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT 1 FROM users ");
		strBuff.append(" WHERE network_code=? AND status IN (" + statusAllowed + ") AND category_code=? AND user_code = ?");
		strBuff.append(" AND  parent_id<>?");
		strBuff.append(" CONNECT BY PRIOR user_id=parent_id START WITH  parent_id=? ");
		LogFactory.printLog("isUserExistForChannelByPassQry", strBuff.toString(), log);
		PreparedStatement pstmt = con.prepareStatement(strBuff.toString());
		int i = 0;
		i++;
		pstmt.setString(i, networkCode);
		i++;
		pstmt.setString(i, toCategoryCode);
		i++;
		pstmt.setString(i, userCode);
		i++;
		pstmt.setString(i, parentID);
		i++;
		pstmt.setString(i, parentID);
		return pstmt;
	}

	@Override
	public PreparedStatement isUserExistByParentIDRecursiveQry(Connection con,
			String networkCode, String toCategoryCode, String userCode,
			String parentID, String statusAllowed) throws SQLException {
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT 1 FROM users  ");
		strBuff.append(" WHERE network_code = ? AND status IN (" + statusAllowed + ") AND category_code = ? AND user_code = ? ");
		strBuff.append(" CONNECT BY PRIOR user_id=parent_id START WITH  parent_id = ? ");
		LogFactory.printLog("isUserExistByParentIDRecursiveQry", strBuff.toString(), log);
		PreparedStatement pstmt = con.prepareStatement(strBuff.toString());
		int i = 0;
		pstmt.setString(++i, networkCode);
		pstmt.setString(++i, toCategoryCode);
		pstmt.setString(++i, userCode);
		pstmt.setString(++i, parentID);
		return pstmt;
	}

	@Override
	public String loadChannelUserByUserIDQry() {
		final StringBuilder selectQueryBuff = new StringBuilder("SELECT U.user_id,U.payment_type, U.msisdn umsisdn, U.user_name, U.login_id, U.password,UPHONES.sms_pin,");
		selectQueryBuff.append(" UPHONES.phone_language phlang, UPHONES.country phcountry, UPHONES.msisdn upmsisdn, ub.balance FROM users U, user_phones UPHONES, user_balances ub ");
		selectQueryBuff.append(" WHERE U.user_id=? AND U.status=? AND U.user_id=UPHONES.user_id AND primary_number=? AND U.user_id=ub.user_id(+)");
		LogFactory.printLog("loadChannelUserByUserIDQry", selectQueryBuff.toString(), log);
		return selectQueryBuff.toString();
	}

	@Override
	public String loadUsersForParentFixedCatQry(String statusAllowed,String fixedCat, int ctrlLvl) {
		final StringBuilder strBuff = new StringBuilder("SELECT u.login_id,u.user_name,u.msisdn,u.user_id,  FROM users u  ,users pu ");
		strBuff.append("WHERE u.network_code = ? AND u.status IN (" + statusAllowed + ")  AND u.category_code = ? AND u.user_id != ?");
		// here user_id != ? check is for not to load the sender user in the
		// query for the same level transactions
		strBuff.append("AND u.parent_id=pu.user_id  AND pu.category_code IN (" + fixedCat + ") ");
		strBuff.append("AND UPPER(u.user_name) LIKE UPPER(?) ");
		if (ctrlLvl == 1) {
			// strBuff.append(" AND ( pu.parent_id = ? OR u.user_id= ? )")
			// commented
			strBuff.append(" AND pu.parent_id = DECODE(pu.parent_id,'ROOT',pu.parent_id,?) ");
			strBuff.append(" AND u.parent_id = DECODE(pu.parent_id,'ROOT',?,u.parent_id) ");
			// here pu.parent_id = ? check by pu is done since pu.parent_id is
			// the parent of selected user's parent
			// for example POS to POSA and only to POSA which are child of POS,
			// under the hierarchy of POS's parent.
		} else if (ctrlLvl == 2) {
			// strBuff.append(" AND ( u.owner_id = ? OR u.user_id= ? ) ")
			// commented
			strBuff.append(" AND pu.owner_id = ? ");
			// here pu.owner_id = ? or u.owner_id =? any can be used since owner
			// is same for all.
		}
		strBuff.append("ORDER BY u.user_name ");
		LogFactory.printLog("loadUsersForParentFixedCatQry", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public PreparedStatement loadUsersForHierarchyFixedCatQry(Connection con, String statusAllowed, String fixedCat, int ctrlLvl, String networkCode, String toCategoryCode,
			String userId,String userName, String parentUserID ) throws SQLException{
		final StringBuilder strBuff = new StringBuilder("SELECT distinct u.user_id, u.user_name,u.login_id,u.msisdn FROM users u ");
		strBuff.append("WHERE u.network_code = ? AND u.status IN (" + statusAllowed + ") AND u.category_code = ? ");
		strBuff.append("AND user_id != ? AND UPPER(u.user_name) LIKE UPPER(?) ");
		// here user_id != ? check is for not to load the sender user in the
		// query for the same level transactions
		strBuff.append("CONNECT BY PRIOR user_id=parent_id START WITH parent_id IN ");
		strBuff.append("(SELECT user_id FROM users WHERE category_code  IN (" + fixedCat + ")");
		if (ctrlLvl > 0) {
			strBuff.append(" CONNECT BY PRIOR user_id=parent_id START WITH  ");
			if (ctrlLvl == 1) {
				strBuff.append(" parent_id = ? ");
			} else if (ctrlLvl == 2) {
				strBuff.append(" owner_id = ? ");
			}
		}
		strBuff.append(" ) ORDER BY u.user_name ");
		LogFactory.printLog("loadUsersForHierarchyFixedCatQry", strBuff.toString(), log);
		PreparedStatement pstmt = con.prepareStatement(strBuff.toString());
		int i = 0;
		pstmt.setString(++i, networkCode);
		pstmt.setString(++i, toCategoryCode);
		pstmt.setString(++i, userId);
		pstmt.setString(++i, userName);
		if (ctrlLvl > 0) {
			pstmt.setString(++i, parentUserID);
		}

		return pstmt;
	}

	@Override
	public PreparedStatement isUserExistForHierarchyFixedCatQry(Connection con , String statusAllowed, String fixedCat, int ctrlLvl, String networkCode,String toCategoryCode,String userCode, String parentUserID   )
			throws SQLException {
		final StringBuilder strBuff = new StringBuilder("SELECT 1 FROM users u ");
		strBuff.append("WHERE u.network_code = ? AND u.status IN (" + statusAllowed + ") AND u.category_code = ? ");
		strBuff.append("AND user_code = ? CONNECT BY PRIOR user_id=parent_id START WITH u.parent_id IN ");
		strBuff.append("(SELECT user_id FROM users WHERE category_code  IN (" + fixedCat + ")");
		if (ctrlLvl > 0) {
			strBuff.append(" CONNECT BY PRIOR user_id=parent_id START WITH  ");
			if (ctrlLvl == 1) {
				strBuff.append(" parent_id = ? ");
			} else if (ctrlLvl == 2) {
				strBuff.append(" owner_id = ? ");
			}
		}
		strBuff.append(" ) ");
		LogFactory.printLog("isUserExistForHierarchyFixedCatQry", strBuff.toString(), log);
		PreparedStatement pstmt = con.prepareStatement(strBuff.toString());
		int i = 0;
		pstmt.setString(++i, networkCode);
		pstmt.setString(++i, toCategoryCode);
		pstmt.setString(++i, userCode);
		if (ctrlLvl > 0) {
			pstmt.setString(++i, parentUserID);
		}
		return pstmt;
	}

	@Override
	public String loadUsersChnlBypassByGeoQry() {
		final StringBuilder strBuff = new StringBuilder(" SELECT distinct gd1.grph_domain_code grph_domain_code, gd1.GRPH_DOMAIN_NAME  GRPH_DOMAIN_NAME ");
		strBuff.append(" FROM geographical_domains GD1 WHERE status IN('Y', 'S') ");
		strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
		strBuff.append(" START WITH grph_domain_code IN ( ");
		strBuff.append(" SELECT grph_domain_code FROM user_geographies UG1 WHERE UG1.user_id= ? ) ");
		LogFactory.printLog("isUserExistsByGeoQry", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public String isUserExistsByGeoQry() {
		final StringBuilder qrySelect = new StringBuilder(" SELECT distinct gd1.grph_domain_code grph_domain_code, gd1.GRPH_DOMAIN_NAME  GRPH_DOMAIN_NAME ");
		qrySelect.append(" FROM geographical_domains GD1 WHERE status IN('Y', 'S') ");
		qrySelect.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
		qrySelect.append(" START WITH grph_domain_code IN ( ");
		qrySelect.append(" SELECT grph_domain_code FROM user_geographies UG1 ,users U where U.user_id=? and U.user_id=UG1.user_id ) ");
		LogFactory.printLog("loadUsersChnlBypassByGeoQry", qrySelect.toString(), log);
		return qrySelect.toString();
	}

	@Override
	public String creditUserBalancesForMultipleWalletQry() {
		final StringBuilder strBuffSelect = new StringBuilder();
		strBuffSelect.append(" SELECT balance,balance_type FROM user_balances");
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
			strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance with RS");
		} else {
			strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance");
		}
		LogFactory.printLog("creditUserBalancesForMultipleWalletQry", strBuffSelect.toString(), log);
		return strBuffSelect.toString();
	}

	@Override
	public String isValidTimeForOptInOutQry() {
		final StringBuilder selectQueryBuff = new StringBuilder("SELECT 1 ");
		selectQueryBuff.append("  FROM profile_set_version psv, profile_set ps ");
		selectQueryBuff.append("  WHERE ps.set_id = psv.set_id ");
		selectQueryBuff.append("  AND psv.status IN ('Y', 'S') ");
		selectQueryBuff.append("  AND ps.opt_in_out_enabled = ? ");
		selectQueryBuff.append("  AND ps.set_id = ? ");
		selectQueryBuff.append("  AND trunc(sysdate)>=trunc(psv.created_on) ");
		//Added by Diwakar on 30-Nov-15 for handling of version concept
		selectQueryBuff.append("  AND (psv.applicable_from >=( SELECT MIN(applicable_from) FROM PROFILE_SET_VERSION WHERE TRUNC(applicable_to)>=TRUNC(sysdate+1)  and status='Y' ");
		selectQueryBuff.append("  AND psv.set_id=set_id ) ");
		selectQueryBuff.append("  AND psv.set_id=ps.set_id ) ");
		//Ended Here 
		selectQueryBuff.append("  AND trunc(sysdate)<trunc(applicable_to)  ");	
		LogFactory.printLog("isValidTimeForOptInOutQry", selectQueryBuff.toString(), log);
		return selectQueryBuff.toString();
	}

	@Override
	public String isProfileActiveQry() {
		final StringBuilder selectQueryBuff = new StringBuilder("SELECT count(*) AS total ");
		selectQueryBuff.append("  FROM profile_set_version psv, profile_set ps ");
		selectQueryBuff.append("  WHERE ps.set_id = psv.set_id ");
		selectQueryBuff.append("  AND ps.set_id = ? ");
		selectQueryBuff.append("  AND psv.status IN ('Y', 'S') ");
		selectQueryBuff.append("  AND ps.promotion_type in ( ?, ?) ");
		selectQueryBuff.append("  AND trunc(psv.applicable_to)>=trunc(SYSDATE) ");
		selectQueryBuff.append("  AND trunc(psv.applicable_from) <=trunc(SYSDATE) ");
		LogFactory.printLog("isProfileActiveQry", selectQueryBuff.toString(), log);
		return selectQueryBuff.toString();
	}

	@Override
	public PreparedStatement loadUserHierarchyListForTransferQry(Connection con, String statusUsed, String status, String mode, String[] userId, String userCategory,PreparedStatement pstmt) throws SQLException{
		boolean staffAsUser = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.STAFF_AS_USER);
		final StringBuilder strBuff = new StringBuilder();		 
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
		strBuff.append("CU.mcommerce_service_allow, CU.low_bal_alert_allow,c.grph_domain_type,c.SEQUENCE_NO  "); // added origin id by Naveen for Channel transfer

		strBuff.append("FROM ( SELECT level l,USR.* ,USR.category_code catcode,UCAT.user_id_prefix FROM users USR, categories UCAT  ");
		strBuff.append("WHERE ");
		if (statusUsed.equals(PretupsI.STATUS_IN)) {
			strBuff.append("USR.status IN (" + status + ")");
		} else if (statusUsed.equals(PretupsI.STATUS_NOTIN)) {
			strBuff.append("USR.status NOT IN (" + status + ")");
		} else if (statusUsed.equals(PretupsI.STATUS_EQUAL)) {
			strBuff.append("USR.status =? ");
		} else if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
			strBuff.append("USR.status <> ? ");
		}
		int i = 1;
		strBuff.append(" AND UCAT.category_code=USR.category_code ");
		if (staffAsUser) {
			strBuff.append(" AND (USR.user_type='CHANNEL' OR USR.user_type='STAFF' ) ");
		} else {
			strBuff.append(" AND USR.user_type='CHANNEL' ");
		}

		if (mode.equalsIgnoreCase(PretupsI.SINGLE)) {

			strBuff.append(" CONNECT BY PRIOR user_id = parent_id ");
			strBuff.append(" START WITH user_id in (SELECT user_id FROM users WHERE user_id=? AND  ");
			strBuff.append(" category_code=? )) U,lookups L, channel_users CU, categories c ");
			strBuff.append(" WHERE  L.lookup_code = U.status AND L.lookup_type=? AND CU.user_id=U.user_id AND c.category_code=U.category_code ORDER BY c.SEQUENCE_NO");

			pstmt = con.prepareStatement(strBuff.toString());
			if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || statusUsed.equals(PretupsI.STATUS_EQUAL)) {
				pstmt.setString(i++, status);
			}
			pstmt.setString(i++, userId[0]);
		} else if (mode.equalsIgnoreCase(PretupsI.MULTIPLE)) {
			StringBuilder str = new StringBuilder();
			for (int k = 0; k < userId.length; k++) {
				if (!BTSLUtil.isNullString(userId[k])) {
					str.append("'");
					str.append(userId[k]);
					str.append("',");
				}
			}
			String userID = str.substring(0, str.length() - 1);
			strBuff.append(" CONNECT BY PRIOR user_id = parent_id ");
			strBuff.append(" START WITH user_id in (SELECT user_id FROM users WHERE user_id in(" + userID + ") AND  ");
			strBuff.append(" category_code=? )) U,lookups L, channel_users CU, categories c ");
			strBuff.append(" WHERE  L.lookup_code = U.status AND L.lookup_type=? AND CU.user_id=U.user_id AND c.category_code= U.category_code ");

			pstmt = con.prepareStatement(strBuff.toString());
			if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || statusUsed.equals(PretupsI.STATUS_EQUAL)) {
				pstmt.setString(i++, status);
			}
		} else if (mode.equalsIgnoreCase(PretupsI.ALL)) {
			strBuff.append(" CONNECT BY PRIOR user_id = parent_id ");
			strBuff.append(" START WITH user_id in (SELECT user_id FROM users WHERE (parent_id=? OR user_id=? )AND  ");
			strBuff.append(" category_code=? )) U,lookups L, channel_users CU, categories c ");
			strBuff.append(" WHERE  L.lookup_code = U.status AND L.lookup_type=? AND CU.user_id=U.user_id AND c.category_code=U.category_code ORDER BY c.SEQUENCE_NO");

			pstmt = con.prepareStatement(strBuff.toString());
			if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || statusUsed.equals(PretupsI.STATUS_EQUAL)) {
				pstmt.setString(i++, status);
			}
			pstmt.setString(i++, userId[0]);
			pstmt.setString(i++, userId[0]);
		}

		pstmt.setString(i++, userCategory);
		pstmt.setString(i, PretupsI.USER_STATUS_TYPE);
		LogFactory.printLog("loadUserHierarchyListForTransferQry", strBuff.toString(), log);
		return pstmt;
	}

	@Override
	public PreparedStatement loadUserHierarchyListForTransferByCatergoryQry(
			Connection con, String statusUsed, String status, String mode,
			String[] userId, String userCategory, String category,String userName,PreparedStatement pstmt ) throws SQLException {
		boolean staffAsUser = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.STAFF_AS_USER);
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT l,U.user_id_prefix,U.user_id, U.user_name, U.network_code, U.login_id, U.password, ");
		strBuff.append("U.category_code, U.parent_id, U.owner_id, U.allowed_ip, U.allowed_days, ");
		strBuff.append("U.from_time,U.to_time, U.last_login_on, U.employee_code, U.status, U.email, ");
		strBuff.append("U.pswd_modified_on, U.contact_person, U.contact_no, U.designation,U.company,U.fax,U.firstname,U.lastname, "); // added
		// by
		// deepika
		// aggarwal
		strBuff.append("U.division, U.department,U.msisdn, U.user_type, U.created_by, U.created_on, ");
		strBuff.append("U.modified_by, U.modified_on, U.address1, U.address2, U.city, U.state, ");
		strBuff
		.append("U.country, U.ssn, U.user_name_prefix, U.external_code,nvl(U.user_code,U.msisdn) user_code, U.short_name, ");
		strBuff.append("U.reference_id, invalid_password_count, level1_approved_by, ");
		strBuff.append("level1_approved_on, level2_approved_by, U.level2_approved_on, U.appointment_date, ");
		strBuff.append("U.password_count_updated_on,U.previous_status,L.lookup_name ");
		// tango implementation
		strBuff.append(",CU.application_id, CU.mpay_profile_id, CU.user_profile_id, ");
		strBuff.append("CU.mcommerce_service_allow, CU.low_bal_alert_allow,c.grph_domain_type,c.SEQUENCE_NO  "); // added origin id by Naveen for Channel transfer

		strBuff.append("FROM ( SELECT level l,USR.* , USR.category_code catcode,UCAT.user_id_prefix FROM users USR, categories UCAT  ");
		strBuff.append("WHERE ");
		String []args = status.split(",");
		if (statusUsed.equals(PretupsI.STATUS_IN)) {
			strBuff.append("USR.status IN");
			 BTSLUtil.pstmtForInQuery(args, strBuff);
		} else if (statusUsed.equals(PretupsI.STATUS_NOTIN)) {
			strBuff.append("USR.status NOT IN");
			BTSLUtil.pstmtForInQuery(args, strBuff);
		} else if (statusUsed.equals(PretupsI.STATUS_EQUAL)) {
			strBuff.append("USR.status =? ");
		} else if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
			strBuff.append("USR.status <> ? ");
		}
		int i = 1;
		strBuff.append(" AND UCAT.category_code=USR.category_code  and USR.category_code = ? AND UPPER(USR.user_name) LIKE UPPER(?) ");
		if (staffAsUser) {
			strBuff.append(" AND (USR.user_type='CHANNEL' OR USR.user_type='STAFF' ) ");
		} else {
			strBuff.append(" AND USR.user_type='CHANNEL' ");
		}
		if (mode.equalsIgnoreCase(PretupsI.SINGLE)) {

			strBuff.append(" CONNECT BY PRIOR user_id = parent_id ");
			strBuff.append(" START WITH user_id in (SELECT user_id FROM users WHERE user_id=? AND  ");
			strBuff.append(" category_code=? )) U,lookups L, channel_users CU, categories c ");
			strBuff.append(" WHERE  L.lookup_code = U.status AND L.lookup_type=? AND CU.user_id=U.user_id AND c.category_code=U.category_code ");

			pstmt = con.prepareStatement(strBuff.toString());

			if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || statusUsed.equals(PretupsI.STATUS_EQUAL)){
				pstmt.setString(i++, status);

			}
			else if (statusUsed.equals(PretupsI.STATUS_IN) || statusUsed.equals(PretupsI.STATUS_NOTIN)) {
				for(int j=0;j<args.length;j++)
            	{
            		 pstmt.setString(i++, args[j]);
            	}
			}
			pstmt.setString(i++, category);
			pstmt.setString(i++, userName);
			pstmt.setString(i++, userId[0]);
		} else if (mode.equalsIgnoreCase(PretupsI.MULTIPLE)) {
			StringBuilder str = new StringBuilder();
		/*	for (int k = 0; k < userId.length; k++) {
				if (!BTSLUtil.isNullString(userId[k])) {
					str.append("'");
					str.append(userId[k]);
					str.append("',");
				}
			}
			String userID = str.substring(0, str.length() - 1);*/
			strBuff.append(" CONNECT BY PRIOR user_id = parent_id ");
			strBuff.append(" START WITH user_id in (SELECT user_id FROM users WHERE user_id in");
			BTSLUtil.pstmtForInQuery(userId, strBuff);
			strBuff.append("AND category_code=? )) U,lookups L, channel_users CU, categories c ");
			strBuff.append(" WHERE  L.lookup_code = U.status AND L.lookup_type=? AND CU.user_id=U.user_id AND c.category_code=U.category_code ");

			pstmt = con.prepareStatement(strBuff.toString());
			if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || statusUsed.equals(PretupsI.STATUS_EQUAL)) {
				pstmt.setString(i++, status);

			}
			pstmt.setString(i++, category);
			pstmt.setString(i++, userName);
			for(int j=0;j<userId.length;j++)
        	{
        		 pstmt.setString(i++, userId[j]);
        	}

		} else if (mode.equalsIgnoreCase(PretupsI.ALL)) {
			strBuff.append(" CONNECT BY PRIOR user_id = parent_id ");
			strBuff.append(" START WITH user_id in (SELECT user_id FROM users WHERE (parent_id=? OR user_id=? )AND  ");
			strBuff.append(" category_code=? )) U,lookups L, channel_users CU, categories c ");
			strBuff.append(" WHERE  L.lookup_code = U.status AND L.lookup_type=? AND CU.user_id=U.user_id AND c.category_code=U.category_code ORDER BY c.SEQUENCE_NO");

			pstmt = con.prepareStatement(strBuff.toString());
			if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || statusUsed.equals(PretupsI.STATUS_EQUAL)) {
				pstmt.setString(i++, status);

			}
			pstmt.setString(i++, category);
			pstmt.setString(i++, userName);
			pstmt.setString(i++, userId[0]);
			pstmt.setString(i++, userId[0]);
		}
		pstmt.setString(i++, userCategory);
		pstmt.setString(i, PretupsI.USER_STATUS_TYPE);
		LogFactory.printLog("loadUserHierarchyListForTransferByCatergoryQry", strBuff.toString(), log);
		return pstmt;
	}

	@Override
	public String loadChannelUserDetailsForTransferQry(boolean isParentOwnerMsisdnRequired, boolean isUserCode) {
			boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
			final StringBuilder sqlBuffer = new StringBuilder(" SELECT cpsv.applicable_from, u.user_id, u.user_name, u.network_code,l.network_name, ");
	        sqlBuffer.append("u.login_id, u.password, u.category_code, u.parent_id, u.owner_id, u.msisdn , u.allowed_ip, ");
	        sqlBuffer.append("u.allowed_days,u.from_time,u.to_time,u.last_login_on,u.employee_code,u.status userstatus, ");
	        sqlBuffer.append("u.email,u.created_by,u.created_on,u.modified_by,u.modified_on,u.pswd_modified_on, ");
	        sqlBuffer.append("cusers.contact_person,u.contact_no,u.designation,u.division,u.department,u.company,u.fax,u.firstname,u.lastname,u.payment_type, ");// firstname,lastname,company,fax
	        // added
	        // by
	        // deepika
	        // aggarwal
	        sqlBuffer.append("u.user_type,cusers.in_suspend,cusers.out_suspend, u.address1,u.address2,u.city,u.state, ");
	        sqlBuffer.append("u.country,u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id, ");
	        sqlBuffer.append("l.status networkstatus,l.language_1_message,cat.hierarchy_allowed, cat.category_type, ");
	        sqlBuffer.append("cat.category_code,cat.category_name,cat.domain_code,cat.sequence_no, ");
	        sqlBuffer.append("cat.multiple_login_allowed, cat.max_login_count,cat.view_on_network_block, ");
	        sqlBuffer.append("cat.grph_domain_type, cat.multiple_grph_domains, cat.fixed_roles, cat.user_id_prefix, ");
	        sqlBuffer.append("cusers.comm_profile_set_id,cusers.transfer_profile_id,cusers.user_grade,cusers.sos_allowed,cusers.sos_allowed_amount,cusers.sos_threshold_limit , ");
	        sqlBuffer.append("cps.comm_profile_set_name , cg.grade_name , cpsv.comm_profile_set_version,cpsv.OTH_COMM_PRF_SET_ID, ");
	        sqlBuffer.append("tp.profile_name , ug.grph_domain_code, dm.domain_name , GD.grph_domain_name, ");
	        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
	        sqlBuffer.append("cps.language_2_message  comprf_lang_2_msg,dm.domain_type_code ");
	        if (isTrfRuleUserLevelAllow) {
	            sqlBuffer.append(" ,cusers.trf_rule_type  ");
	        }
	        if(isParentOwnerMsisdnRequired)
	        	sqlBuffer.append(", pu.msisdn parent_msisdn, ou.msisdn owner_msisdn ");
	        sqlBuffer.append(" , cpsv.dual_comm_type ");
	        sqlBuffer.append("FROM users u,networks l,categories cat,channel_users cusers,commission_profile_set cps, ");
	        sqlBuffer.append("channel_grades cg, commission_profile_set_version cpsv , ");
	        sqlBuffer.append("transfer_profile tp, user_geographies ug , domains dm, geographical_domains GD ");
	        if(isParentOwnerMsisdnRequired)
	        	sqlBuffer.append(",users pu ,users ou ");
	        sqlBuffer.append("WHERE ");
	        if (isUserCode) {
	            sqlBuffer.append(" u.user_code = ? ");
	        } else {
	            sqlBuffer.append(" u.user_id = ? ");
	        }
	        if(isParentOwnerMsisdnRequired)
	        sqlBuffer.append(" AND  pu.user_id = (CASE u.parent_id WHEN 'ROOT' THEN u.user_id ELSE u.parent_id END)  AND pu.status <> 'N'  AND u.owner_id =ou.user_id AND ou.status <> 'N'  ");
	        sqlBuffer.append(" AND u.status <> 'N' AND u.status <> 'C' AND cat.status='Y' AND  U.network_code=L.network_code  ");
	        sqlBuffer.append(" AND u.user_id=cusers.user_id AND cat.category_code=U.category_code AND ");
	        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND cg.grade_code = cusers.user_grade AND ");
	        sqlBuffer.append(" cpsv.comm_profile_set_id = cps.comm_profile_set_id AND tp.profile_id = cusers.transfer_profile_id  ");
	        sqlBuffer.append(" AND ug.user_id = u.user_id AND dm.domain_code = cat.domain_code AND ug.grph_domain_code = GD.grph_domain_code  ");
	        sqlBuffer.append(" AND cpsv.applicable_from =nvl ( (SELECT MAX(applicable_from) ");
	        sqlBuffer.append(" FROM commission_profile_set_version  ");
	        sqlBuffer.append(" WHERE applicable_from<=? AND comm_profile_set_id=cusers.comm_profile_set_id),cpsv.applicable_from) ");
	        LogFactory.printLog("loadChannelUserDetailsForTransferQry", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}

	@Override
	public String loadChannelUserDetailsForTransferTcpQry(boolean isParentOwnerMsisdnRequired, boolean isUserCode) {
		 final StringBuilder sqlBuffer = new StringBuilder(" SELECT cpsv.applicable_from, u.user_id, u.user_name, u.network_code,l.network_name, ");
	        sqlBuffer.append("u.login_id, u.password, u.category_code, u.parent_id, u.owner_id, u.msisdn , u.allowed_ip, ");
	        sqlBuffer.append("u.allowed_days,u.from_time,u.to_time,u.last_login_on,u.employee_code,u.status userstatus, ");
	        sqlBuffer.append("u.email,u.created_by,u.created_on,u.modified_by,u.modified_on,u.pswd_modified_on, ");
	        sqlBuffer.append("cusers.contact_person,u.contact_no,u.designation,u.division,u.department,u.company,u.fax,u.firstname,u.lastname,u.payment_type, ");// firstname,lastname,company,fax
	        // added
	        // by
	        // deepika
	        // aggarwal
	        sqlBuffer.append("u.user_type,cusers.in_suspend,cusers.out_suspend, u.address1,u.address2,u.city,u.state, ");
	        sqlBuffer.append("u.country,u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id, ");
	        sqlBuffer.append("l.status networkstatus,l.language_1_message,cat.hierarchy_allowed, cat.category_type, ");
	        sqlBuffer.append("cat.category_code,cat.category_name,cat.domain_code,cat.sequence_no, ");
	        sqlBuffer.append("cat.multiple_login_allowed, cat.max_login_count,cat.view_on_network_block, ");
	        sqlBuffer.append("cat.grph_domain_type, cat.multiple_grph_domains, cat.fixed_roles, cat.user_id_prefix, ");
	        sqlBuffer.append("cusers.comm_profile_set_id,cusers.transfer_profile_id,cusers.user_grade,cusers.sos_allowed,cusers.sos_allowed_amount,cusers.sos_threshold_limit , ");
	        sqlBuffer.append("cps.comm_profile_set_name , cg.grade_name , cpsv.comm_profile_set_version,cpsv.OTH_COMM_PRF_SET_ID, ");
	        sqlBuffer.append(" ug.grph_domain_code, dm.domain_name , GD.grph_domain_name, ");
	        sqlBuffer.append("cps.status commprofilestatus,cps.language_1_message comprf_lang_1_msg, ");
	        sqlBuffer.append("cps.language_2_message  comprf_lang_2_msg,dm.domain_type_code ");
	        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW))).booleanValue()) {
	            sqlBuffer.append(" ,cusers.trf_rule_type  ");
	        }
	        if(isParentOwnerMsisdnRequired)
	        	sqlBuffer.append(", pu.msisdn parent_msisdn, ou.msisdn owner_msisdn ");
	        sqlBuffer.append(" , cpsv.dual_comm_type ");
	        sqlBuffer.append("FROM users u,networks l,categories cat,channel_users cusers,commission_profile_set cps, ");
	        sqlBuffer.append("channel_grades cg, commission_profile_set_version cpsv , ");
	        sqlBuffer.append(" user_geographies ug , domains dm, geographical_domains GD ");
	        if(isParentOwnerMsisdnRequired)
	        	sqlBuffer.append(",users pu ,users ou ");
	        sqlBuffer.append("WHERE ");
	        if (isUserCode) {
	            sqlBuffer.append(" u.user_code = ? ");
	        } else {
	            sqlBuffer.append(" u.user_id = ? ");
	        }
	        if(isParentOwnerMsisdnRequired)
	        sqlBuffer.append(" AND  pu.user_id = (CASE u.parent_id WHEN 'ROOT' THEN u.user_id ELSE u.parent_id END)  AND pu.status <> 'N'  AND u.owner_id =ou.user_id AND ou.status <> 'N'  ");
	        sqlBuffer.append(" AND u.status <> 'N' AND u.status <> 'C' AND cat.status='Y' AND  U.network_code=L.network_code  ");
	        sqlBuffer.append(" AND u.user_id=cusers.user_id AND cat.category_code=U.category_code AND ");
	        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND cg.grade_code = cusers.user_grade AND ");
	        sqlBuffer.append(" cpsv.comm_profile_set_id = cps.comm_profile_set_id   ");
	        sqlBuffer.append(" AND ug.user_id = u.user_id AND dm.domain_code = cat.domain_code AND ug.grph_domain_code = GD.grph_domain_code  ");
	        sqlBuffer.append(" AND cpsv.applicable_from =nvl ( (SELECT MAX(applicable_from) ");
	        sqlBuffer.append(" FROM commission_profile_set_version  ");
	        sqlBuffer.append(" WHERE applicable_from<=? AND comm_profile_set_id=cusers.comm_profile_set_id),cpsv.applicable_from) ");
	        LogFactory.printLog("loadChannelUserDetailsForTransferQry", sqlBuffer.toString(), log);
		return sqlBuffer.toString();
	}

	@Override
	public StringBuilder debitUserBalancesForO2CQry(){
		final StringBuilder strBuffSelect = new StringBuilder();
        strBuffSelect.append(" SELECT balance,balance_type FROM user_balances ");
        strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance ");
        return strBuffSelect;
	}

	@Override
	public String loadAllChildUserBalanceQry() {
		final StringBuilder strBuff = new StringBuilder();
		  strBuff.append(" SELECT sum(UB.balance) balance,P.product_short_code,P.short_name  FROM  (SELECT U.user_id FROM users U ,categories C WHERE  ");
	        strBuff.append("U.user_id <> ? AND NOT U.status IN ('N','C') AND U.category_code =C.category_code  ");
	        strBuff.append(" CONNECT BY PRIOR user_id = parent_id START WITH  user_id=? )  X , products P,user_balances UB  WHERE UB.product_code=P.product_code  ");
	        strBuff.append("and X.user_id = ub.user_id  GROUP BY (P.product_short_code,P.short_name)");
        return strBuff.toString();
	}
	@Override
	public String isUserInHierarchyQry( String c_identifierType) {
		final StringBuilder strBuff = new StringBuilder();
		  strBuff.append(" SELECT U." + c_identifierType + " FROM USERS U ");
		  strBuff.append(" CONNECT BY PRIOR U.USER_ID = U.PARENT_ID START WITH U.USER_ID = ? ");
        return strBuff.toString();
	}

	/*
	 * 
	 * @see com.btsl.pretups.user.businesslogic.ChannelUserQry#loadChannelUserDetailsQryLoginID()
	 */
	@Override
	public String loadChannelUserDetailsQryLoginID() {
		
			final StringBuilder selectQueryBuff = new StringBuilder(
					" SELECT u.user_id, u.password webpassword,u.user_name, u.network_code,u.login_id, u.category_code, u.parent_id, u.owner_id, u.msisdn,");
			selectQueryBuff.append(" u.employee_code,u.status userstatus,u.created_by,u.created_on,u.email,u.modified_by,u.modified_on,");
			selectQueryBuff.append(" cusers.contact_person,u.contact_no,u.designation,u.division,u.department,u.user_type,cusers.in_suspend,cusers.out_suspend,");
			selectQueryBuff.append(" u.address1,u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,u.LONGITUDE,");
			selectQueryBuff.append(" cat.domain_code,dom.domain_type_code,cat.sequence_no catseq,cat.sms_interface_allowed,geo.grph_domain_code,gdomains.status geostatus,u.company,u.fax,u.firstname,u.lastname, ");// company

			selectQueryBuff.append(" uphones.user_phones_id,uphones.primary_number, uphones.sms_pin, uphones.pin_required, uphones.phone_profile, uphones.phone_language phlang,");
			selectQueryBuff.append(" uphones.country phcountry, uphones.invalid_pin_count, uphones.last_transaction_status, uphones.last_transaction_on,");
			selectQueryBuff.append(" uphones.pin_modified_on,uphones.last_transfer_id, uphones.last_transfer_type,uphones.prefix_id,uphones.temp_transfer_id, uphones.first_invalid_pin_time, ");
			selectQueryBuff.append(" cat.agent_allowed,cat.hierarchy_allowed, cat.category_type,cat.category_name,cat.grph_domain_type,cusers.comm_profile_set_id,cusers.transfer_profile_id, tp.profile_name, cusers.sos_allowed,cusers.sos_allowed_amount,cusers.sos_threshold_limit,cusers.lr_allowed,cusers.lr_max_amount, tp.status tpstatus,cusers.user_grade,cset.status csetstatus, ");
			selectQueryBuff.append(" cset.language_1_message comprf_lang_1_msg,cset.language_2_message   comprf_lang_2_msg,cset.COMM_PROFILE_SET_NAME, cset.last_dual_comm_type,cat.restricted_msisdns,gdt.sequence_no grphSeq, cat.transfertolistonly, cat.USER_ID_PREFIX, cpsv.applicable_from, cpsv.comm_profile_set_version, ");

			// for Zebra and Tango by sanjeew date 06/07/07
			selectQueryBuff.append(" uphones.access_type, uphones.created_on, cusers.application_id, cusers.mpay_profile_id, cusers.user_profile_id, cusers.mcommerce_service_allow,cusers.low_bal_alert_allow, uphones.created_on userphone_created_on ");
			// end of Zebra and Tango
			// added for loading PIN reset info
			selectQueryBuff.append(" ,uphones.PIN_RESET,uphones.last_access_on,uphones.modified_on,uphones.modified_by,u.from_time,u.to_time,u.allowed_days,u.invalid_password_count,u.pswd_modified_on, cusers.auto_c2c_allow, cusers.auto_c2c_quantity ");
			// added for transfer rule type
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW))).booleanValue()) {
				selectQueryBuff.append(", cusers.trf_rule_type  ");
			}
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
				selectQueryBuff.append(" , cusers.lms_profile  ");
			}
			//added for owner commission
			selectQueryBuff.append(" , ou.category_code own_category_code, ou.msisdn own_msisdn, ou.user_name owner_name, ou.company owner_company ");
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
				selectQueryBuff.append(" , cusers.OPT_IN_OUT_STATUS ");
			}
			selectQueryBuff.append(" , cusers.CONTROL_GROUP,gdomains.GRPH_DOMAIN_NAME ");
			selectQueryBuff.append(" FROM users u,user_geographies geo,categories cat,domains dom,channel_users cusers,user_phones uphones,transfer_profile tp,commission_profile_set cset,geographical_domains gdomains,geographical_domain_types gdt,commission_profile_set_version cpsv ");
			//added for owner commission
			selectQueryBuff.append(" , users ou");
			selectQueryBuff.append(" WHERE u.user_id=? AND uphones.user_id=u.user_id AND u.status <> ? AND u.status <> ? ");
			selectQueryBuff.append(" AND u.user_id=cusers.user_id(+) AND u.category_code = cat.category_code AND u.user_id=geo.user_id AND geo.grph_domain_code=gdomains.grph_domain_code AND cpsv.comm_profile_set_id  = cset.comm_profile_set_id ");
			selectQueryBuff.append(" AND cat.domain_code= dom.domain_code AND cusers.transfer_profile_id=tp.profile_id(+) AND cusers.comm_profile_set_id=cset.comm_profile_set_id(+) AND gdt.grph_domain_type=gdomains.grph_domain_type ");
			selectQueryBuff.append(" AND cpsv.applicable_from      =NVL ((SELECT MAX(applicable_from) FROM commission_profile_set_version WHERE applicable_from <= ? ");
			selectQueryBuff.append(" AND comm_profile_set_id = cusers.comm_profile_set_id), cpsv.applicable_from)");
			//added for owner commission
			selectQueryBuff.append("  AND ou.user_id=u.owner_id");
			LogFactory.printLog("loadChannelUserDetailsQry",selectQueryBuff.toString() , log);
			return selectQueryBuff.toString();
		}

	@Override
	public String loadStaffUserDetailbyCHUser() {
		final StringBuilder strBuff = new StringBuilder(" SELECT u.login_id as LOGINID, U.user_id as USERID, U.user_name as USER_NAME ,up.msisdn as MSISDN ");
        strBuff.append(" FROM users U LEFT OUTER JOIN user_phones up ON u.user_id = up.user_id ,");
        strBuff.append(" (SELECT user_id,parent_id,owner_id,USER_TYPE FROM USERS CONNECT BY PRIOR user_id = parent_id START WITH user_id= (CASE ?  WHEN 'ALL' THEN user_id ELSE  ? END)    ) X , ");
        strBuff.append("user_geographies UG, categories CAT ");
        strBuff.append(" WHERE U.USER_ID =X.USER_ID   AND U.USER_TYPE ='STAFF' AND ");
        strBuff.append("  U.category_code = ( CASE ? WHEN 'ALL' THEN U.category_code ELSE ? END ) ");
        strBuff.append(" AND CAT.domain_code =  (CASE ? WHEN 'ALL' THEN CAT.domain_code ELSE ? END )");
        strBuff.append(" AND CAT.category_code = U.category_code ");
        strBuff.append(" AND U.user_type = '" + PretupsI.STAFF_USER_TYPE + "' ");
        strBuff.append(" AND U.status NOT IN ('N','C','W') ");
//        strBuff.append(" AND U.user_id = up.user_id ");
//        strBuff.append(" AND up.PRIMARY_NUMBER='Y' ");
        strBuff.append(" AND U.user_id = UG.user_id ");
        strBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN(?, ?) ");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code ");
        strBuff.append(" FROM user_geographies UG1 ");
        strBuff.append(" WHERE	UG1.grph_domain_code = (CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END )     ");
        strBuff.append(" AND UG1.user_id= ( CASE ? WHEN 'ALL' THEN UG1.user_id ELSE ? END )  ))  ");
        
		return strBuff.toString();
	}
	
	
	
	
	@Override
	public String loadUserNameAutoSearchOnZoneDomainCategoryQry() {
		final StringBuilder strBuff = new StringBuilder(" SELECT U.user_id as USERID, U.user_name as USER_NAME ,up.msisdn as MSISDN ");
		
        strBuff.append(" FROM users U,user_geographies UG, categories CAT,user_phones up ");
        //strBuff.append(" WHERE U.category_code = DECODE(?, '" + PretupsI.ALL + "', U.category_code, ?)  " );
        strBuff.append(" WHERE U.category_code = ( CASE ? WHEN 'ALL' THEN U.category_code ELSE ? END ) ");
        strBuff.append(" AND CAT.domain_code =  (CASE ? WHEN 'ALL' THEN CAT.domain_code ELSE ? END )");
        strBuff.append(" AND CAT.category_code = U.category_code ");
        strBuff.append(" AND U.user_type = '" + PretupsI.CHANNEL_USER_TYPE + "' ");
        strBuff.append(" AND U.status NOT IN ('N','C','W') ");
        strBuff.append(" AND U.user_id = up.user_id ");
        strBuff.append(" AND up.PRIMARY_NUMBER='Y' ");
        strBuff.append(" AND U.user_id = UG.user_id ");
        strBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN('Y','S') ");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code ");
        strBuff.append(" FROM user_geographies UG1 ");
        strBuff.append(" WHERE	UG1.grph_domain_code = (CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END )     ");
        strBuff.append(" AND UG1.user_id= ? ))  ");
        
		return strBuff.toString();
	}
	
	
		

	@Override
	public String loadChannelUserDetailsQryLoginIDTcp() {
		
			final StringBuilder selectQueryBuff = new StringBuilder(
					" SELECT u.user_id, u.password webpassword,u.user_name, u.network_code,u.login_id, u.category_code, u.parent_id, u.owner_id, u.msisdn,");
			selectQueryBuff.append(" u.employee_code,u.status userstatus,u.created_by,u.created_on,u.email,u.modified_by,u.modified_on,");
			selectQueryBuff.append(" cusers.contact_person,u.contact_no,u.designation,u.division,u.department,u.user_type,cusers.in_suspend,cusers.out_suspend,");
			selectQueryBuff.append(" u.address1,u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,u.LONGITUDE,");
			selectQueryBuff.append(" cat.domain_code,dom.domain_type_code,cat.sequence_no catseq,cat.sms_interface_allowed,geo.grph_domain_code,gdomains.status geostatus,u.company,u.fax,u.firstname,u.lastname, ");// company

			selectQueryBuff.append(" uphones.user_phones_id,uphones.primary_number, uphones.sms_pin, uphones.pin_required, uphones.phone_profile, uphones.phone_language phlang,");
			selectQueryBuff.append(" uphones.country phcountry, uphones.invalid_pin_count, uphones.last_transaction_status, uphones.last_transaction_on,");
			selectQueryBuff.append(" uphones.pin_modified_on,uphones.last_transfer_id, uphones.last_transfer_type,uphones.prefix_id,uphones.temp_transfer_id, uphones.first_invalid_pin_time, ");
			selectQueryBuff.append(" cat.agent_allowed,cat.hierarchy_allowed, cat.category_type,cat.category_name,cat.grph_domain_type,cusers.comm_profile_set_id,cusers.transfer_profile_id,  cusers.sos_allowed,cusers.sos_allowed_amount,cusers.sos_threshold_limit,cusers.lr_allowed,cusers.lr_max_amount, cusers.user_grade,cset.status csetstatus, ");
			selectQueryBuff.append(" cset.language_1_message comprf_lang_1_msg,cset.language_2_message   comprf_lang_2_msg,cset.COMM_PROFILE_SET_NAME, cset.last_dual_comm_type,cat.restricted_msisdns,gdt.sequence_no grphSeq, cat.transfertolistonly, cat.USER_ID_PREFIX, cpsv.applicable_from, cpsv.comm_profile_set_version, ");

			// for Zebra and Tango by sanjeew date 06/07/07
			selectQueryBuff.append(" uphones.access_type, uphones.created_on, cusers.application_id, cusers.mpay_profile_id, cusers.user_profile_id, cusers.mcommerce_service_allow,cusers.low_bal_alert_allow, uphones.created_on userphone_created_on ");
			// end of Zebra and Tango
			// added for loading PIN reset info
			selectQueryBuff.append(" ,uphones.PIN_RESET,uphones.last_access_on,uphones.modified_on,uphones.modified_by,u.from_time,u.to_time,u.allowed_days,u.invalid_password_count,u.pswd_modified_on, cusers.auto_c2c_allow, cusers.auto_c2c_quantity ");
			// added for transfer rule type
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW))).booleanValue()) {
				selectQueryBuff.append(", cusers.trf_rule_type  ");
			}
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
				selectQueryBuff.append(" , cusers.lms_profile  ");
			}
			//added for owner commission
			selectQueryBuff.append(" , ou.category_code own_category_code, ou.msisdn own_msisdn, ou.user_name owner_name, ou.company owner_company ");
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
				selectQueryBuff.append(" , cusers.OPT_IN_OUT_STATUS ");
			}
			selectQueryBuff.append(" , cusers.CONTROL_GROUP,gdomains.GRPH_DOMAIN_NAME ");
			selectQueryBuff.append(" FROM users u,user_geographies geo,categories cat,domains dom,channel_users cusers,user_phones uphones,commission_profile_set cset,geographical_domains gdomains,geographical_domain_types gdt,commission_profile_set_version cpsv ");
			//added for owner commission
			selectQueryBuff.append(" , users ou");
			selectQueryBuff.append(" WHERE u.user_id=? AND uphones.user_id=u.user_id AND u.status <> ? AND u.status <> ? ");
			selectQueryBuff.append(" AND u.user_id=cusers.user_id(+) AND u.category_code = cat.category_code AND u.user_id=geo.user_id AND geo.grph_domain_code=gdomains.grph_domain_code AND cpsv.comm_profile_set_id  = cset.comm_profile_set_id ");
			//selectQueryBuff.append(" AND cat.domain_code= dom.domain_code AND cusers.transfer_profile_id=tp.profile_id(+) AND cusers.comm_profile_set_id=cset.comm_profile_set_id(+) AND gdt.grph_domain_type=gdomains.grph_domain_type ");
			
			selectQueryBuff.append(" AND cat.domain_code= dom.domain_code  AND cusers.comm_profile_set_id=cset.comm_profile_set_id(+) AND gdt.grph_domain_type=gdomains.grph_domain_type ");
			selectQueryBuff.append(" AND cpsv.applicable_from      =NVL ((SELECT MAX(applicable_from) FROM commission_profile_set_version WHERE applicable_from <= ? ");
			selectQueryBuff.append(" AND comm_profile_set_id = cusers.comm_profile_set_id), cpsv.applicable_from)");
			//added for owner commission
			selectQueryBuff.append("  AND ou.user_id=u.owner_id");
			LogFactory.printLog("loadChannelUserDetailsQry",selectQueryBuff.toString() , log);
			return selectQueryBuff.toString();
		}
	
	@Override
	public String loadChannelUserByUserIDAnyStatusQry() {
		final StringBuilder selectQueryBuff = new StringBuilder("SELECT U.user_id,U.payment_type, U.msisdn umsisdn, U.user_name, U.login_id, U.password,UPHONES.sms_pin,");
		selectQueryBuff.append(" UPHONES.phone_language phlang, UPHONES.country phcountry, UPHONES.msisdn upmsisdn, ub.balance FROM users U, user_phones UPHONES, user_balances ub ");
		selectQueryBuff.append(" WHERE U.user_id=?  AND U.user_id=UPHONES.user_id AND primary_number=? AND U.user_id=ub.user_id(+)");
		LogFactory.printLog("loadChannelUserByUserIDQry", selectQueryBuff.toString(), log);
		return selectQueryBuff.toString();
	}
	//stats used ='EQUAL'
	//
	@Override
	public PreparedStatement loadUsersDetailsByLoginOrMsisdnQry(Connection con, String msisdn,  String loginId , String status, String statusUsed,String networkCode) throws SQLException{
		
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT DISTINCT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
		strBuff.append(" USR.login_id,USR.password password1,USR.category_code usr_category_code,USR.parent_id,USR.reference_id, ");
		strBuff.append(" USR.owner_id,USR.allowed_ip,USR.allowed_days, D.domain_name, ");
		strBuff.append(" USR.from_time,USR.to_time,USR.employee_code,USR.company,USR.fax,USR.firstname,USR.lastname, "); // firstname,lastname,company,fax

		strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,");
		strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
		strBuff.append("USR.created_by, USR_CRBY.user_name created_by_name, USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
		strBuff.append(" USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
		strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
		strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date,USR.LONGITUDE, ");
		strBuff.append("USR.previous_status,USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name, USR_CAT.max_txn_msisdn, ");
		strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type,MOD_USR.user_name request_user_name, USR_CAT.low_bal_alert_allow, ");
		strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, USR_CAT.services_allowed,USR_CAT.transfertolistonly, ");
		strBuff.append("USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed,USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed, USR_CAT.restricted_msisdns, ");
		strBuff.append("PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn,UG.grph_domain_code, GD.grph_domain_name, ");
		strBuff.append("PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn,USR_CAT.category_type, ");
		strBuff.append("ONR_CAT.category_name owner_cat,USR_PHONE.sms_pin user_sms_pin, USR_PHONE.pin_required required,l.lookup_name, D.domain_type_code, ");
		strBuff.append(" USR_PHONE.access_type user_access_type,USR_PHONE.phone_language planguage ,USR_PHONE.country pcountry ");// phone_language,country
		strBuff.append(" ,USR.rsaflag,USR_CAT.AUTHENTICATION_TYPE,USR.AUTHENTICATION_ALLOWED , ");
		strBuff.append(" (SELECT ub1.BALANCE FROM USER_BALANCES ub1 WHERE ub1.USER_ID=ub.USER_ID AND ub1.PRODUCT_CODE='ETOPUP') AS etopup, ");
		strBuff.append(" (SELECT ub1.BALANCE FROM USER_BALANCES ub1 WHERE ub1.USER_ID=ub.USER_ID AND ub1.PRODUCT_CODE='POSTETOPUP') AS postetopup ");
		//from
		strBuff.append(" FROM user_phones USR_PHONE,users USR, users PRNT_USR,users ONR_USR,categories USR_CAT, users MOD_USR, ");
		strBuff.append("categories ONR_CAT, categories  PRNT_CAT,lookups l,user_geographies UG, geographical_domains GD, users USR_CRBY, domains D ");
		strBuff.append(" ,channel_users chnl_usr,USER_BALANCES ub ");

		if(!BTSLUtil.isNullorEmpty(msisdn)){
		
			strBuff.append(" WHERE USR_PHONE.msisdn=? AND USR_PHONE.user_id=USR.user_id ");
		}
		else if(!BTSLUtil.isNullorEmpty(loginId)) {
			strBuff.append(" WHERE USR.login_id=? AND USR.user_id=USR_PHONE.user_id ");
		}
		
		strBuff.append(" AND usr.network_code ='"+networkCode+"' ");
		
		if (status!=null  &&  (status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) || (status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE))) {
			strBuff.append("  AND USR.barred_deletion_batchid IS NULL ");
		}

		if (statusUsed.equals(PretupsI.STATUS_IN)) {
			strBuff.append(" AND USR.status IN ('" + status + "') ");
		} else if (statusUsed.equals(PretupsI.STATUS_NOTIN)) {
			strBuff.append(" AND USR.status NOT IN ('"+status+"') ");
		} else if (statusUsed.equals(PretupsI.STATUS_EQUAL)) {
			strBuff.append(" AND USR.status =? ");
		} else if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
			strBuff.append("  AND USR.status <> ? ");
		}

		strBuff.append("AND USR.parent_id=PRNT_USR.user_id(+) AND USR.owner_id=ONR_USR.user_id ");
		strBuff.append(" AND USR.category_code=USR_CAT.category_code ");
		strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
		strBuff.append("AND PRNT_USR.category_code=PRNT_CAT.category_code(+) ");
		strBuff.append("  AND USR.status = l.lookup_code ");
		strBuff.append("  AND l.lookup_type= ?  ");
		strBuff.append("  AND USR.user_id = UG.user_id ");
		strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
		strBuff.append("  AND MOD_USR.user_id(+) = USR.modified_by ");
		strBuff.append("  AND USR_CRBY.user_id(+) = USR.created_by ");
		strBuff.append("  AND USR_CAT.domain_code=D.domain_code ");
		strBuff.append("  AND ub.user_id(+)=usr.user_id");
		strBuff.append("  and usr.user_type ='"+PretupsI.CHANNEL_USER_TYPE+"'");
		
		
		LogFactory.printLog("loadUsersDetailsQry",strBuff.toString() , log);
		PreparedStatement pstmtSelect = con.prepareStatement(strBuff.toString());
		int i = 1;
		if(!BTSLUtil.isNullorEmpty(msisdn)){
			pstmtSelect.setString(i, msisdn);
		}
		else if(!BTSLUtil.isNullorEmpty(loginId)) {
			pstmtSelect.setString(i, loginId);
		}
		i++;
		if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || statusUsed.equals(PretupsI.STATUS_EQUAL)) {
			pstmtSelect.setString(i, status);
			i++;
		}
		pstmtSelect.setString(i, PretupsI.USER_STATUS_TYPE);
		return pstmtSelect;
		
		
	}
	@Override
	public PreparedStatement loadUsersDetailsByExtcode(Connection con, String status,String statusUsed,String userID,String extcode) throws SQLException{
		final StringBuilder strBuff=new StringBuilder();
//		strBuff.append(" SELECT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
//	    strBuff.append(" USR.login_id,USR.password as passwd,USR.category_code usr_category_code,USR.parent_id,USR.reference_id, ");
//	    strBuff.append(" USR.owner_id,USR.allowed_ip,USR.allowed_days, D.domain_name, ");
//	    strBuff.append(" USR.from_time,USR.to_time,USR.employee_code,USR.company,USR.fax,USR.firstname,USR.lastname, ");// added
//	  
//	    strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,");
//	    strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
//	    strBuff.append("USR.created_by, USR_CRBY.user_name created_by_name, USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
//	    strBuff.append("USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
//	    strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
//	    strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date, ");
//	    strBuff.append("USR.previous_status,USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name, USR_CAT.max_txn_msisdn, ");
//	    strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type,MOD_USR.user_name request_user_name, USR_CAT.low_bal_alert_allow, ");
//	    strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, USR_CAT.services_allowed,USR_CAT.transfertolistonly, ");
//	    strBuff.append("USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed,USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed, USR_CAT.restricted_msisdns, ");
//	    strBuff.append("PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn,UG.grph_domain_code, GD.grph_domain_name, ");
//	    strBuff.append("PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn,USR_CAT.category_type, ");
//	    strBuff.append("ONR_CAT.category_name owner_cat,USR_PHONE.sms_pin user_sms_pin, USR_PHONE.pin_required required,l.lookup_name, D.domain_type_code, ");
//	    strBuff.append(" USR_PHONE.access_type user_access_type, chnl_usr.activated_on activated_on,USR_CAT.AUTHENTICATION_TYPE,USR.AUTHENTICATION_ALLOWED ");
//	    strBuff.append("FROM user_phones USR_PHONE,users USR, users PRNT_USR,users ONR_USR,categories USR_CAT, users MOD_USR, CHANNEL_USERS chnl_usr, ");
//	    strBuff.append("categories ONR_CAT, categories  PRNT_CAT,lookups l,user_geographies UG, geographical_domains GD, users USR_CRBY, domains D ");
//	    strBuff.append("WHERE USR.external_code=? AND USR_PHONE.user_id=USR.user_id ");
////	     Added for bar for del by shashank
//	    if (status!=null  &&  (status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) || (status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE))) {
//			strBuff.append("  AND USR.barred_deletion_batchid IS NULL ");
//		}
//	    // end
//	    if (statusUsed.equals(PretupsI.STATUS_IN)) {
//	        strBuff.append(" AND USR.status IN ('" + status + "') ");
//	    } else if (statusUsed.equals(PretupsI.STATUS_NOTIN)) {
//	        strBuff.append(" AND USR.status NOT IN ('" + status + "') ");
//	    } else if (statusUsed.equals(PretupsI.STATUS_EQUAL)) {
//	        strBuff.append(" AND USR.status =? ");
//	    } else if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
//	        strBuff.append(" AND USR.status <> ? ");
//	    }
//	    strBuff.append("AND USR.parent_id=PRNT_USR.user_id(+) AND USR.owner_id=ONR_USR.user_id ");
//	    strBuff.append("AND USR.category_code=USR_CAT.category_code ");
//	    strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
//	    strBuff.append("AND PRNT_USR.category_code=PRNT_CAT.category_code(+) ");
//	    strBuff.append(" AND USR.status = l.lookup_code ");
//	    strBuff.append(" AND l.lookup_type= ? ");
//	    strBuff.append(" AND USR.user_id = UG.user_id ");
//	    strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
//	    strBuff.append(" AND MOD_USR.user_id(+) = USR.modified_by ");
//	    strBuff.append(" AND USR_CRBY.user_id(+) = USR.created_by ");
//	    strBuff.append(" AND USR_CAT.domain_code=D.domain_code ");
//	    strBuff.append(" AND chnl_usr.user_id=USR.user_id ");
//	    strBuff.append("  and usr.user_type ='"+PretupsI.CHANNEL_USER_TYPE+"'");
//	    if (userID != null) {
//	        strBuff.append(" AND USR.user_id IN ( ");
//	        strBuff.append(" SELECT user_id from users where user_id != ? ");
//	        strBuff.append(" CONNECT BY PRIOR user_id = parent_id START WITH  user_id=? ) ");
//	    }
//
//	     LogFactory.printLog("loadUsersDetailsQry",strBuff.toString() , log);
//	     PreparedStatement pstmtSelect = con.prepareStatement(strBuff.toString());
//	     int i = 1;
//	     pstmtSelect.setString(i,extcode);
//	     i++;
////	     if (statusUsed.equals(PretupsI.STATUS_IN) || statusUsed.equals(PretupsI.STATUS_EQUAL)) {
////	         pstmtSelect.setString(i, status);
////	         i++;
////	     }
//	     pstmtSelect.setString(i, status);
//          i++;
//	     pstmtSelect.setString(i, PretupsI.USER_STATUS_TYPE);
//	     i++;
//	     if (userID != null) {
//	         pstmtSelect.setString(i, userID);
//	         i++;
//	         pstmtSelect.setString(i, userID);
//	     }
//	     
//	     return pstmtSelect;
		
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
	    if ((status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) || (status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE))) {
	        strBuff.append(" AND USR.barred_deletion_batchid IS NULL");
	    }
	    // end
	    if (statusUsed.equals(PretupsI.STATUS_IN)) {
	        strBuff.append(" AND USR.status IN ('" + status + "') ");
	    } else if (statusUsed.equals(PretupsI.STATUS_NOTIN)) {
	        strBuff.append(" AND USR.status NOT IN ('" + status + "') ");
	    } else if (statusUsed.equals(PretupsI.STATUS_EQUAL)) {
	        strBuff.append(" AND USR.status =? ");
	    } else if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
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
	    if (userID != null) {
	        strBuff.append(" AND USR.user_id IN ( ");
	        strBuff.append(" SELECT user_id from users where user_id != ? ");
	        strBuff.append(" CONNECT BY PRIOR user_id = parent_id START WITH  user_id=? ) ");
	    }


//		 final String sqlSelect = strBuff.toString();
//	     if (LOG.isDebugEnabled()) {
//	         LOG.debug("loadUserDetailsByExtCode", "QUERY sqlSelect=" + sqlSelect);
//	     }
//	    PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);
	    
	    LogFactory.printLog("loadUserDetailsByExtCode",strBuff.toString() , log);
		PreparedStatement pstmtSelect = con.prepareStatement(strBuff.toString());
	     
	     int i = 1;
	     pstmtSelect.setString(i, extcode);
	     i++;
	     if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || statusUsed.equals(PretupsI.STATUS_EQUAL)) {
	         pstmtSelect.setString(i, status);
	         i++;
	     }
	     pstmtSelect.setString(i, PretupsI.USER_STATUS_TYPE);
	     i++;
	     if (userID != null) {
	         pstmtSelect.setString(i,userID);
	         i++;
	         pstmtSelect.setString(i,userID);
	     }
	     
	     return pstmtSelect;
		
	}
	

	@Override
	public PreparedStatement loadApprovalUsersListQry(Connection con, String p_categoryCode, String p_lookupType,
			String p_networkCode, String p_parentGrphDomainCode, String p_status, String p_userType) throws SQLException{
		final StringBuilder strBuff = new StringBuilder();
		
		strBuff.append("SELECT DISTINCT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,\r\n"
				+ " USR.user_name usr_user_name,USR.network_code, USR.login_id,USR.password password1,\r\n"
				+ " USR.category_code usr_category_code,USR.parent_id,USR.reference_id,  USR.owner_id,USR.allowed_ip,USR.allowed_days,\r\n"
				+ " D.domain_name,  USR.from_time,USR.to_time,USR.employee_code,USR.company,USR.fax,USR.firstname,USR.lastname, USR.status usr_status,\r\n"
				+ " USR.email,USR.pswd_modified_on,USR.contact_no,USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,USR.created_by,\r\n"
				+ " USR_CRBY.user_name created_by_name, USR.created_on,USR.modified_by,USR.modified_on,USR.address1,  USR.address2,USR.city,USR.state,USR.country,\r\n"
				+ " USR.ssn,USR.user_name_prefix, USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,USR.level2_approved_by,\r\n"
				+ " USR.level2_approved_on,USR.user_code,USR.appointment_date,USR.LONGITUDE, USR.previous_status,USR_CAT.category_code usr_cat_category_code,\r\n"
				+ " USR_CAT.category_name, USR_CAT.max_txn_msisdn, USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type,\r\n"
				+ " MOD_USR.user_name request_user_name, USR_CAT.low_bal_alert_allow, USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,\r\n"
				+ " USR_CAT.sms_interface_allowed, USR_CAT.services_allowed,USR_CAT.transfertolistonly, USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,\r\n"
				+ " USR_CAT.multiple_login_allowed,USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed, USR_CAT.restricted_msisdns, \r\n"
				+ " PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn,UG.grph_domain_code, GD.grph_domain_name, PRNT_CAT.category_name parent_cat,\r\n"
				+ " ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn,USR_CAT.category_type, ONR_CAT.category_name owner_cat,\r\n"
				+ " USR_PHONE.sms_pin user_sms_pin, USR_PHONE.pin_required required,l.lookup_name, D.domain_type_code, \r\n"
				+ " USR_PHONE.access_type user_access_type,USR_PHONE.phone_language planguage ,USR_PHONE.country pcountry  ,\r\n"
				+ " USR.rsaflag,USR_CAT.AUTHENTICATION_TYPE,USR.AUTHENTICATION_ALLOWED,\r\n"
				+ " (SELECT ub1.BALANCE FROM USER_BALANCES ub1 WHERE ub1.USER_ID=ub.USER_ID AND ub1.PRODUCT_CODE='ETOPUP') AS etopup,\r\n"
				+ " (SELECT ub1.BALANCE FROM USER_BALANCES ub1 WHERE ub1.USER_ID=ub.USER_ID AND ub1.PRODUCT_CODE='POSTETOPUP') AS postetopup");
		strBuff.append(" FROM \r\n"
				+ "users USR,\r\n"
				+ "users USR_CRBY,\r\n"
				+ "users MOD_USR,\r\n"
				+ "categories USR_CAT,\r\n"
				+ "lookups l,\r\n"
				+ "users ONR_USR,\r\n"
				+ "users PRNT_USR,\r\n"
				+ "user_phones USR_PHONE,\r\n"
				+ "categories ONR_CAT,\r\n"
				+ " categories  PRNT_CAT,\r\n"
				+ " user_geographies UG,\r\n"
				+ " geographical_domains GD,\r\n"
				+ "  domains D ,\r\n"
				+ "USER_BALANCES ub ");
		strBuff.append("WHERE usr .category_code ='"+p_categoryCode+"' AND  \r\n"
				+ "  USR.category_code = USR_CAT.category_code  AND ONR_USR.user_id = USR.owner_id  AND PRNT_USR.user_id(+) = USR.parent_id \r\n"
				+ "  AND USR_CRBY.user_id = USR.created_by  AND MOD_USR.user_id(+)= USR.modified_by  AND USR.user_id = USR_PHONE.user_id  AND USR.status = l.lookup_code ");
		strBuff.append(" AND l.lookup_type= '"+p_lookupType+"'");
		strBuff.append("  AND USR.status in ('"+p_status+"')");
		strBuff.append("  AND USR.user_type = '"+p_userType+"' ");
		strBuff.append("  AND USR.user_id IN ((SELECT user_id FROM user_geographies WHERE grph_domain_code IN\r\n"
				+ "  ( \r\n"
				+ "  SELECT  grph_domain_code FROM geographical_domains WHERE grph_domain_type IN \r\n"
				+ "  ( SELECT c.GRPH_DOMAIN_TYPE  FROM CATEGORIES c ");
		
		strBuff.append("WHERE c.CATEGORY_CODE ='"+p_categoryCode+"'");
		
		strBuff.append(" )");
		
		strBuff.append(" AND network_code = ?  CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH ");
		
		strBuff.append("grph_domain_code = '"+p_parentGrphDomainCode+"'");
		
		strBuff.append("))) ");
		strBuff.append(" AND ub.USER_ID(+)=USR.USER_ID\r\n"
				+ " AND ONR_CAT.category_code (+)=ONR_USR.category_code \r\n"
				+ " AND PRNT_CAT.category_code (+)=PRNT_USR.category_code \r\n"
				+ " AND UG.user_id(+) =USR.user_id \r\n"
				+ " AND  GD.grph_domain_code=UG.grph_domain_code \r\n"
				+ " AND  USR_CAT.domain_code=D.domain_code ");
		LogFactory.printLog("loadUsersDetailsQry",strBuff.toString() , log);
		PreparedStatement pstmtSelect = con.prepareStatement(strBuff.toString());
		int i = 1;
		pstmtSelect.setString(i, p_networkCode);
		return pstmtSelect;
	}
	@Override
	public PreparedStatement loadUsersDetailsByExtCodeQry(Connection con,
			String status, String userId, String statusUsed, String extCode) throws SQLException {
		boolean isMsisdnAssociationReq = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_MSISDN_ASSOCIATION_REQ);
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
		strBuff.append("USR.login_id,USR.password password1,USR.category_code usr_category_code,USR.parent_id,USR.reference_id, ");
		strBuff.append("USR.owner_id,USR.allowed_ip,USR.allowed_days,");
		strBuff.append("USR.from_time,USR.to_time,USR.employee_code,USR.company,USR.fax,USR.firstname,USR.lastname, ");
		strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,");
		strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
		strBuff.append("USR.created_by, USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
		strBuff.append("USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
		strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
		strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date, ");
		strBuff.append("USR.previous_status,USR.rsaflag,USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name, USR_CAT.max_txn_msisdn, ");
		strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type, USR_CAT.transfertolistonly, USR_CAT.low_bal_alert_allow, ");
		strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, USR_CAT.services_allowed, ");
		strBuff.append("USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed, USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed,USR_CAT.restricted_msisdns, ");
		strBuff.append("USR_CAT.category_type,");
		strBuff.append("l.lookup_name, UG.grph_domain_code, GD.grph_domain_name, D.domain_type_code ");
		strBuff.append(",UP.phone_language,UP.country phcountry,USR_CAT.AUTHENTICATION_TYPE,USR.AUTHENTICATION_ALLOWED, D.domain_name ");
		if (isMsisdnAssociationReq) {
			strBuff.append(" ,chnl_usr.ASSOCIATED_MSISDN,chnl_usr.ASSOCIATED_MSISDN_TYPE,chnl_usr.ASSOCIATED_MSISDN_CDATE,chnl_usr.ASSOCIATED_MSISDN_MDATE ");
		}
		strBuff.append("FROM users USR,categories USR_CAT,user_phones UP,");
		strBuff.append("lookups l,user_geographies UG, geographical_domains GD, domains D ");
		if (isMsisdnAssociationReq) {
			strBuff.append(" ,channel_users chnl_usr ");
		}
		strBuff.append("WHERE UPPER(USR.external_code)=UPPER(?) AND UP.user_id=USR.user_id ");
		if ((status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) || (status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE))) {
			strBuff.append(" AND USR.barred_deletion_batchid IS NULL");
		}
		if (statusUsed.equals(PretupsI.STATUS_IN)) {
			strBuff.append(" AND USR.status IN (" + status + ") ");
		} else if (statusUsed.equals(PretupsI.STATUS_NOTIN)) {
			strBuff.append(" AND USR.status NOT IN (" + status + ") ");
		} else if (statusUsed.equals(PretupsI.STATUS_EQUAL)) {
			strBuff.append(" AND USR.status =? ");
		} else if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
			strBuff.append(" AND USR.status <> ? ");
		}
		strBuff.append(" AND USR.category_code=USR_CAT.category_code ");
		strBuff.append(" AND USR.status = l.lookup_code ");
		strBuff.append(" AND USR.user_id = UP.user_id(+) "); 
		strBuff.append(" AND l.lookup_type= ? ");
		strBuff.append(" AND USR.user_id = UG.user_id ");
		strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
		strBuff.append(" AND USR_CAT.domain_code=D.domain_code ");
		if (isMsisdnAssociationReq) {
			strBuff.append(" AND chnl_usr.user_id(+)=USR.user_id ");
		}
		if (userId != null) {
			strBuff.append(" AND USR.user_id IN ( ");
			strBuff.append(" SELECT user_id from users ");
            /*
             * Commenting To get Parent User Details also
             */
			/*strBuff.append(" where user_id != ? ");*/
			strBuff.append(" CONNECT BY PRIOR user_id = parent_id START WITH  user_id=? ) ");
		}
		LogFactory.printLog("loadUsersDetailsByExtCodeQry", strBuff.toString(), log);
		PreparedStatement pstmtSelect = con.prepareStatement(strBuff.toString());
		int i = 1;
		pstmtSelect.setString(i++, extCode);
		if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || statusUsed.equals(PretupsI.STATUS_EQUAL)) {
			pstmtSelect.setString(i++, status);
		}
		pstmtSelect.setString(i++, PretupsI.USER_STATUS_TYPE);
		if (userId != null) {
			 /*
             * Commenting To get Parent User Details also
             */
			//pstmtSelect.setString(i++, userId);
			pstmtSelect.setString(i, userId);
		}
		return pstmtSelect;
	}
	
	@Override
	public String validateUsersForBatchC2CQry(String p_categoryCode) {
		StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT U.user_id,U.user_code,U.msisdn,U.login_id,U.category_code,C.category_name,CG.grade_code,U.status,");
        strBuff.append("CG.grade_name,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend,U.external_code, ");
		strBuff.append("CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version,CPSV.OTH_COMM_PRF_SET_ID, TP.profile_name, ");
		strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
		strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
        strBuff.append("FROM users U,channel_users CU,channel_grades CG,categories C,user_geographies UG, ");
		strBuff.append("commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
		strBuff.append("WHERE ( U.login_id= ? OR U.msisdn= ? ) AND U.network_code=? AND U.user_id=CU.user_id AND U.user_id=UG.user_id AND ");
		strBuff.append("U.category_code=C.category_code AND U.category_code=CG.category_code AND CU.user_grade=CG.grade_code ");
		strBuff.append("AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
		strBuff.append("AND TP.profile_id = CU.transfer_profile_id AND C.category_code IN ('"+p_categoryCode+"') ");
		strBuff.append("AND U.status <> 'N' AND U.status <> 'C' AND C.status='Y' ");
		strBuff.append("AND CPSV.applicable_from =nvl ( (SELECT MAX(applicable_from) FROM ");
		strBuff.append("commission_profile_set_version WHERE applicable_from <= ? AND ");
		strBuff.append("comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from) ");
        strBuff.append("ORDER BY C.sequence_no,CU.user_grade,U.login_id");
        return strBuff.toString();
	}

	@Override
	public String loadChannelUserDetailsByLoginIDANDORMSISDNQry(String p_msisdn, String p_loginid) {
		StringBuffer selectQueryBuff = new StringBuffer(" SELECT u.user_id, u.password webpassword,u.user_name, u.network_code,u.login_id, u.category_code, u.parent_id, u.owner_id, u.msisdn,");
        selectQueryBuff.append(" u.employee_code,u.status userstatus,u.created_by,u.created_on,u.modified_by,u.modified_on,");
        selectQueryBuff.append(" cusers.contact_person,u.contact_no,u.designation,u.division,u.department,u.user_type,cusers.in_suspend,cusers.out_suspend,");
        selectQueryBuff.append(" u.address1,u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,");
        selectQueryBuff.append(" cat.domain_code,dom.domain_type_code,cat.sequence_no catseq,cat.sms_interface_allowed,geo.grph_domain_code,gdomains.status geostatus, ");
        selectQueryBuff.append(" uphones.user_phones_id,uphones.primary_number, uphones.sms_pin, uphones.pin_required, uphones.phone_profile, uphones.phone_language phlang,");
        selectQueryBuff.append(" uphones.country phcountry, uphones.invalid_pin_count, uphones.last_transaction_status, uphones.last_transaction_on,");
        selectQueryBuff.append(" uphones.pin_modified_on,uphones.last_transfer_id, uphones.last_transfer_type,uphones.prefix_id,uphones.temp_transfer_id, uphones.first_invalid_pin_time, ");
        selectQueryBuff.append(" cat.agent_allowed,cat.hierarchy_allowed, cat.category_type,cat.category_name,cat.grph_domain_type,cusers.comm_profile_set_id,cusers.transfer_profile_id, tp.status tpstatus,cusers.user_grade,cset.status csetstatus, ");
		selectQueryBuff.append(" cset.language_1_message comprf_lang_1_msg,cset.language_2_message  comprf_lang_2_msg,cat.restricted_msisdns,gdt.sequence_no grphSeq, cat.transfertolistonly, cat.USER_ID_PREFIX, ");
        
		//for Zebra and Tango by sanjeew date 06/07/07
		selectQueryBuff.append(" uphones.access_type, uphones.created_on, cusers.application_id, cusers.mpay_profile_id, cusers.user_profile_id, cusers.mcommerce_service_allow,cusers.low_bal_alert_allow, uphones.created_on userphone_created_on ");
		//end of Zebra and Tango
		//added for loading PIN reset info
		selectQueryBuff.append(" ,uphones.PIN_RESET,uphones.last_access_on, u.from_time,u.to_time,u.allowed_days,u.invalid_password_count,u.pswd_modified_on  ");
    	selectQueryBuff.append(" FROM users u,user_geographies geo,categories cat,domains dom,channel_users cusers,user_phones uphones,transfer_profile tp,commission_profile_set cset,geographical_domains gdomains,geographical_domain_types gdt ");
        selectQueryBuff.append(" WHERE  u.status <> ? AND u.status <> ? ");
        //modifed by harsh
        if(!BTSLUtil.isNullString(p_msisdn) && !BTSLUtil.isNullString(p_loginid))
        	selectQueryBuff.append(" AND  uphones.msisdn=? AND UPPER(u.login_id)=UPPER(?)");
        
        else if(!BTSLUtil.isNullString(p_msisdn))
        	selectQueryBuff.append(" AND  uphones.msisdn=?");
        
        else if(!BTSLUtil.isNullString(p_loginid) )
        	selectQueryBuff.append(" AND UPPER(u.login_id)=UPPER(?)");
        //end modified by
        selectQueryBuff.append(" AND uphones.user_id=u.user_id AND u.user_id=cusers.user_id(+) AND u.category_code = cat.category_code AND u.user_id=geo.user_id AND geo.grph_domain_code=gdomains.grph_domain_code ");
        selectQueryBuff.append(" AND cat.domain_code= dom.domain_code AND cusers.transfer_profile_id=tp.profile_id(+) AND cusers.comm_profile_set_id=cset.comm_profile_set_id(+) AND gdt.grph_domain_type=gdomains.grph_domain_type ");
        return selectQueryBuff.toString();
	}
}
