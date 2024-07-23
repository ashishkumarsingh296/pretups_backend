package com.btsl.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.AutoCompleteUserDetailsRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.GetParentOwnerProfileReq;
import com.btsl.pretups.channel.transfer.businesslogic.LowThreshHoldReportDTO;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistoryReqDTO;
import com.btsl.pretups.channeluser.businesslogic.ApplistReqVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.restapi.channelAdmin.StaffUserListByParntReqVO;

public class UserPostgresQry implements UserQry{
	
	private Log log = LogFactory.getLog(this.getClass().getName());

	@Override
	public String loadUsersDetailsQry(){
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
        strBuff.append("USR.login_id,USR.password passwd,USR.category_code usr_category_code,USR.parent_id,");
        strBuff.append("USR.owner_id,USR.allowed_ip,USR.allowed_days,USR.company,USR.fax,USR.firstname,USR.lastname, ");// firstname,lastname,company,fax
        strBuff.append("USR.from_time,USR.to_time,USR.employee_code,");
        strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,");
        strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
        strBuff.append("USR.created_by,USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
        strBuff.append("USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
        strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
        strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date, ");
        strBuff.append("USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name,");
        strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type, ");
        strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, USR_CAT.domain_allowed, USR_CAT.fixed_domains, ");
        strBuff.append("USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed,");
        strBuff.append("PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn, ");
        strBuff.append("PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn, ");
        strBuff.append("ONR_CAT.category_name owner_cat ");
        strBuff.append("FROM users USR left outer join (users PRNT_USR left join categories  PRNT_CAT on PRNT_CAT.category_code=PRNT_USR.category_code )on PRNT_USR.user_id= USR.parent_id, users ONR_USR,categories USR_CAT, ");
        strBuff.append("categories ONR_CAT ");
        strBuff.append("WHERE USR.status <> 'N' AND USR.status <> 'C' AND USR.msisdn=? ");
        strBuff.append("AND USR.owner_id=ONR_USR.user_id ");
        strBuff.append("AND USR.category_code=USR_CAT.category_code ");
        strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
        return strBuff.toString();
	}
	@Override
	public String loadUserDetailsByEmpcodeQry(String p_catCode){
		StringBuilder sqlBuffer = new StringBuilder(" SELECT uowner.user_name owner_name, uparent.user_name parent_name, u.user_id, u.user_name, u.network_code,l.network_name,l.report_header_name, u.login_id, u.password, ");
        sqlBuffer.append(" u.category_code, u.parent_id, u.owner_id, u.msisdn, u.allowed_ip,  u.allowed_days,u.from_time,u.to_time, ");
        sqlBuffer.append(" u.last_login_on,u.employee_code,u.status userstatus,u.email,u.created_by,u.created_on,u.modified_by, ");
        sqlBuffer.append(" u.modified_on,u.pswd_modified_on,u.contact_no,u.designation,u.division,u.department, ");
        sqlBuffer.append(" u.msisdn,u.user_type,u.address1,u.address2,u.city,u.state,u.country,u.company,u.fax,u.firstname,u.lastname, ");// firstname,lastname,company,fax
        sqlBuffer.append(" u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,u.invalid_password_count,u.password_count_updated_on, ");
        sqlBuffer.append(" l.status networkstatus,l.language_1_message,l.language_2_message, cat.category_code,cat.category_name, ");
        sqlBuffer.append(" cat.domain_code,cat.sequence_no,cat.multiple_login_allowed, cat.max_login_count,cat.view_on_network_block, ");
        sqlBuffer.append(" cat.status catstatus, cat.max_txn_msisdn, cat.uncntrl_transfer_allowed, cat.scheduled_transfer_allowed, cat.restricted_msisdns, ");
        sqlBuffer.append(" cat.parent_category_code, cat.product_types_allowed,cat.category_type,cat.hierarchy_allowed, cat.transfertolistonly, ");
        sqlBuffer.append(" cat.grph_domain_type, cat.multiple_grph_domains, cat.fixed_roles, cat.user_id_prefix,cat.web_interface_allowed, ");
        sqlBuffer.append(" cat.services_allowed,cat.domain_allowed,cat.fixed_domains,cat.outlets_allowed,cat.status categorystatus,gdt.sequence_no grph_sequence_no, ");
        sqlBuffer.append(" gdt.grph_domain_type_name,dm.domain_name,dm.status domainstatus,dm.domain_type_code,dt.restricted_msisdn restricted_msisdn_allow  ");
        sqlBuffer.append(" FROM users u left join networks l on L.network_code=U.network_code left join users uparent on uparent.user_id=u.parent_id ,users uowner,categories cat,geographical_domain_types gdt ,domains dm,domain_types dt ");
        sqlBuffer.append(" WHERE  UPPER(u.employee_code)=UPPER(?) " );
        if(!BTSLUtil.isNullString(p_catCode))
        {
        	sqlBuffer.append(" AND UPPER(u.category_code)=UPPER(?) " );
        }
        sqlBuffer.append(" AND u.owner_id=uowner.user_id AND gdt.grph_domain_type = cat.grph_domain_type ");
        sqlBuffer.append(" AND u.status <> ? AND u.status <> ?  ");
        sqlBuffer.append(" AND cat.category_code=U.category_code AND cat.status <> ? AND dm.domain_code = cat.domain_code ");
        sqlBuffer.append(" AND dt.domain_type_code = dm.domain_type_code ");
        return sqlBuffer.toString();
	}
	@Override
	public String loadUserDetailsFormUserIDQry(){
		StringBuilder selectQueryBuff = new StringBuilder("SELECT U.address1, U.address2, U.allowed_days, U.allowed_ip, U.appointment_date, U.batch_id,U.creation_type,");
        selectQueryBuff.append(" U.category_code, U.city, U.contact_no, U.contact_person, U.country, U.created_by, U.created_on, U.creation_type, U.department, U.designation,");
        selectQueryBuff.append(" U.division, U.email, U.employee_code, U.external_code, U.from_time,  U.invalid_password_count, U.last_login_on, U.level1_approved_by,");
        selectQueryBuff.append(" U.level1_approved_on, U.level2_approved_by, U.level2_approved_on, U.login_id, U.modified_by, U.modified_on, U.network_code,U.company,U.fax,U.firstname,U.lastname, ");// firstname,lastname,company,fax
        selectQueryBuff.append(" U.owner_id, U.parent_id, U.password, U.password_count_updated_on, U.previous_status, U.pswd_modified_on, U.reference_id, U.remarks,");
        selectQueryBuff.append(" U.short_name, U.ssn,U.rsaflag, U.state, U.status userstatus, U.to_time, U.user_code, U.user_id, U.user_name, U.user_name_prefix, U.user_type, CU.activated_on,");
        selectQueryBuff.append(" CU.comm_profile_set_id, CU.contact_person, CU.in_suspend, CU.out_suspend, CU.outlet_code, CU.suboutlet_code, CU.transfer_profile_id,");
        selectQueryBuff.append(" CU.user_grade, C.agent_allowed, C.category_name, C.category_type, C.domain_allowed, C.domain_code, C.fixed_domains, C.fixed_roles,");
        selectQueryBuff.append(" C.grph_domain_type, C.hierarchy_allowed, C.max_login_count, C.max_txn_msisdn, C.multiple_grph_domains, C.multiple_login_allowed, C.outlets_allowed, ");
        selectQueryBuff.append(" C.parent_category_code, C.product_types_allowed, C.restricted_msisdns,C.scheduled_transfer_allowed, C.sequence_no catseq, C.services_allowed, ");
        selectQueryBuff.append(" C.sms_interface_allowed, C.status, C.transfertolistonly, C.uncntrl_transfer_allowed,C.user_id_prefix, C.view_on_network_block, C.web_interface_allowed, ");
        selectQueryBuff.append(" UP.msisdn, UP.description, UP.sms_pin, UP.pin_required, UP.phone_profile, UP.phone_language, UP.country coun , UP.invalid_pin_count, UP.last_transaction_status,");
        selectQueryBuff.append(" UP.last_transaction_on, UP.pin_modified_on, UP.last_transfer_id, UP.last_transfer_type, UP.temp_transfer_id, UP.first_invalid_pin_time, ");

        // for Zebra and Tango by sanjeew date 06/07/07
        selectQueryBuff.append(" CU.application_id, CU.mpay_profile_id, CU.user_profile_id, CU.mcommerce_service_allow, UP.PREFIX_ID,l.lookup_name, ");
        // end of Zebra and Tango
        selectQueryBuff.append(" USR_CRBY.user_name created_by_name,u.AUTHENTICATION_ALLOWED,PRNT_USR.login_id as parentloginID ");

        selectQueryBuff.append(" FROM users U left join user_phones UP on (UP.user_id=U.user_id and UP.primary_number='Y' ) ");
        selectQueryBuff.append(" left join (users PRNT_USR left join categories  PRNT_CAT on PRNT_CAT.category_code=PRNT_USR.category_code) on  PRNT_USR.user_id=U.parent_id ");
        selectQueryBuff.append(" left join users USR_CRBY on USR_CRBY.user_id = U.created_by ,");
        selectQueryBuff.append(" channel_users CU, categories C,  lookups l");
        selectQueryBuff.append(" WHERE CU.user_id=U.user_id AND U.category_code=C.category_code AND U.user_id=? ");
        selectQueryBuff.append(" AND U.status = l.lookup_code AND l.lookup_type= ? ");
        return selectQueryBuff.toString();
	}
	@Override
	public String loadUserDetailsByEmpcodeQuery(){
		StringBuilder sqlBuffer = new StringBuilder(" SELECT uowner.user_name owner_name, uparent.user_name parent_name, u.user_id, u.user_name,u.category_code, u.network_code,l.network_name,l.report_header_name, u.login_id, u.password, ");
        sqlBuffer.append(" u.category_code, u.parent_id, u.owner_id, u.msisdn, u.allowed_ip,  u.allowed_days,u.from_time,u.to_time, ");
        sqlBuffer.append(" u.last_login_on,u.employee_code,u.status userstatus,u.email,u.created_by,u.created_on,u.modified_by, ");
        sqlBuffer.append(" u.modified_on,u.pswd_modified_on,u.contact_no,u.designation,u.division,u.department, ");
        sqlBuffer.append(" u.msisdn,u.user_type,u.address1,u.address2,u.city,u.state,u.country,u.company,u.fax,u.firstname,u.lastname, ");// firstname,lastname,company,fax
        sqlBuffer.append(" u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,u.invalid_password_count,u.password_count_updated_on, ");
        sqlBuffer.append(" l.status networkstatus,l.language_1_message,l.language_2_message, cat.category_code,cat.category_name, ");
        sqlBuffer.append(" cat.domain_code,cat.sequence_no,cat.multiple_login_allowed, cat.max_login_count,cat.view_on_network_block, ");
        sqlBuffer.append(" cat.status catstatus, cat.max_txn_msisdn, cat.uncntrl_transfer_allowed, cat.scheduled_transfer_allowed, cat.restricted_msisdns, ");
        sqlBuffer.append(" cat.parent_category_code, cat.product_types_allowed,cat.category_type,cat.hierarchy_allowed, cat.transfertolistonly, ");
        sqlBuffer.append(" cat.grph_domain_type, cat.multiple_grph_domains, cat.fixed_roles, cat.user_id_prefix,cat.web_interface_allowed, ");
        sqlBuffer.append(" cat.services_allowed,cat.domain_allowed,cat.fixed_domains,cat.outlets_allowed,cat.status categorystatus,gdt.sequence_no grph_sequence_no, ");
        sqlBuffer.append(" gdt.grph_domain_type_name,dm.domain_name,dm.status domainstatus,dm.domain_type_code,dt.restricted_msisdn restricted_msisdn_allow  ");
        sqlBuffer.append(" FROM users u left join users uparent on uparent.user_id=u.parent_id ");
        sqlBuffer.append(" left join networks l on L.network_code=U.network_code ,users uowner,categories cat,geographical_domain_types gdt ,domains dm,domain_types dt ");
        sqlBuffer.append(" WHERE  UPPER(u.employee_code)=UPPER(?) AND u.owner_id=uowner.user_id AND  gdt.grph_domain_type = cat.grph_domain_type ");
        sqlBuffer.append(" AND u.status <> ? AND u.status <> ?  ");
        sqlBuffer.append(" AND cat.category_code=U.category_code AND cat.status <> ? AND dm.domain_code = cat.domain_code ");
        sqlBuffer.append(" AND dt.domain_type_code = dm.domain_type_code ");
        return sqlBuffer.toString();
	}
	@Override
	public String loadUserDetailsByLoginIdQry(){
		StringBuilder selectQueryBuff = new StringBuilder("SELECT U.address1, U.address2, U.allowed_days, U.allowed_ip, U.appointment_date, U.batch_id,U.creation_type,");
        selectQueryBuff.append(" U.category_code, U.city, U.contact_no, U.contact_person, U.country, U.created_by, U.created_on, U.creation_type, U.department, U.designation,");
        selectQueryBuff.append(" U.division, U.email, U.employee_code, U.external_code, U.from_time,  U.invalid_password_count, U.last_login_on, U.level1_approved_by,");
        selectQueryBuff.append(" U.level1_approved_on, U.level2_approved_by, U.level2_approved_on, U.login_id, U.modified_by, U.modified_on, U.network_code,U.company,U.fax,U.firstname,U.lastname, ");// firstname,lastname,company,fax
        selectQueryBuff.append(" U.owner_id, U.parent_id, U.password, U.password_count_updated_on, U.previous_status, U.pswd_modified_on, U.reference_id, U.remarks,");
        selectQueryBuff.append(" U.short_name, U.ssn,U.rsaflag, U.state, U.status userstatus, U.to_time, user_code, U.user_id, U.user_name, U.user_name_prefix, U.user_type, CU.activated_on,");
        selectQueryBuff.append(" CU.comm_profile_set_id, CU.contact_person, CU.in_suspend, CU.out_suspend, CU.outlet_code, CU.suboutlet_code, CU.transfer_profile_id,");
        selectQueryBuff.append(" CU.user_grade, C.agent_allowed, C.category_name, C.category_type, C.domain_allowed, C.domain_code, C.fixed_domains, C.fixed_roles,");
        selectQueryBuff.append(" C.grph_domain_type, C.hierarchy_allowed, C.max_login_count, C.max_txn_msisdn, C.multiple_grph_domains, C.multiple_login_allowed, C.outlets_allowed, ");
        selectQueryBuff.append(" C.parent_category_code, C.product_types_allowed, C.restricted_msisdns,C.scheduled_transfer_allowed, C.sequence_no catseq, C.services_allowed, ");
        selectQueryBuff.append(" C.sms_interface_allowed, C.status, C.transfertolistonly, C.uncntrl_transfer_allowed,C.user_id_prefix, C.view_on_network_block, C.web_interface_allowed, ");
        selectQueryBuff.append(" UP.msisdn, UP.description, UP.sms_pin, UP.pin_required, UP.phone_profile, UP.phone_language, UP.country, UP.invalid_pin_count, UP.last_transaction_status,");
        selectQueryBuff.append(" UP.last_transaction_on, UP.pin_modified_on, UP.last_transfer_id, UP.last_transfer_type, UP.temp_transfer_id, UP.first_invalid_pin_time, ");

        // for Zebra and Tango by sanjeew date 06/07/07
        selectQueryBuff.append(" CU.application_id, CU.mpay_profile_id, CU.user_profile_id, CU.mcommerce_service_allow, UP.PREFIX_ID,l.lookup_name ");
        // end of Zebra and Tango

        selectQueryBuff.append(" FROM users U left join user_phones UP on (UP.user_id=U.user_id and UP.primary_number='Y'), channel_users CU, categories C, lookups l ");
        selectQueryBuff.append(" WHERE CU.user_id=U.user_id AND U.category_code=C.category_code AND U.login_id=?");
        selectQueryBuff.append("  AND U.status = l.lookup_code AND l.lookup_type= ? ");
         return selectQueryBuff.toString();
	}
	@Override
	public String loadUserDetailsByMsisdnQry(){
		StringBuilder selectQueryBuff = new StringBuilder("SELECT U.address1, U.address2, U.allowed_days, U.allowed_ip, U.appointment_date, U.batch_id,U.creation_type,");
        selectQueryBuff.append(" U.category_code, U.city, U.contact_no, U.contact_person, U.country, U.created_by, U.created_on, U.creation_type, U.department, U.designation,");
        selectQueryBuff.append(" U.division, U.email, U.employee_code, U.external_code, U.from_time,  U.invalid_password_count, U.last_login_on, U.level1_approved_by,");
        selectQueryBuff.append(" U.level1_approved_on, U.level2_approved_by, U.level2_approved_on, U.login_id, U.modified_by, U.modified_on, U.network_code,U.company,U.fax,U.firstname,U.lastname, ");// firstname,lastname,company,fax
        selectQueryBuff.append(" U.owner_id, U.parent_id, U.password, U.password_count_updated_on, U.previous_status, U.pswd_modified_on, U.reference_id, U.remarks,");
        selectQueryBuff.append(" U.short_name, U.ssn,U.rsaflag, U.state, U.status userstatus, U.to_time, user_code, U.user_id, U.user_name, U.user_name_prefix, U.user_type, CU.activated_on,");
        selectQueryBuff.append(" CU.comm_profile_set_id, CU.contact_person, CU.in_suspend, CU.out_suspend, CU.outlet_code, CU.suboutlet_code, CU.transfer_profile_id,");
        selectQueryBuff.append(" CU.user_grade, C.agent_allowed, C.category_name, C.category_type, C.domain_allowed, C.domain_code, C.fixed_domains, C.fixed_roles,");
        selectQueryBuff.append(" C.grph_domain_type, C.hierarchy_allowed, C.max_login_count, C.max_txn_msisdn, C.multiple_grph_domains, C.multiple_login_allowed, C.outlets_allowed, ");
        selectQueryBuff.append(" C.parent_category_code, C.product_types_allowed, C.restricted_msisdns,C.scheduled_transfer_allowed, C.sequence_no catseq, C.services_allowed, ");
        selectQueryBuff.append(" C.sms_interface_allowed, C.status, C.transfertolistonly, C.uncntrl_transfer_allowed,C.user_id_prefix, C.view_on_network_block, C.web_interface_allowed, ");
        selectQueryBuff.append(" UP.msisdn, UP.description, UP.sms_pin, UP.pin_required, UP.phone_profile, UP.phone_language, UP.country, UP.invalid_pin_count, UP.last_transaction_status,");
        selectQueryBuff.append(" UP.last_transaction_on, UP.pin_modified_on, UP.last_transfer_id, UP.last_transfer_type, UP.temp_transfer_id, UP.first_invalid_pin_time, UP.user_phones_id, ");

        // for Zebra and Tango by sanjeew date 06/07/07
        selectQueryBuff.append(" CU.application_id, CU.mpay_profile_id, CU.user_profile_id, CU.mcommerce_service_allow, UP.PREFIX_ID,l.lookup_name, geo.GRPH_DOMAIN_CODE ");
        // end of Zebra and Tango

        selectQueryBuff.append(" FROM users U left join user_phones UP on (UP.user_id=U.user_id AND UP.primary_number='Y'), channel_users CU, categories C,  lookups l, user_geographies geo ");
        selectQueryBuff.append(" WHERE CU.user_id=U.user_id AND U.category_code=C.category_code AND U.msisdn=?");
        selectQueryBuff.append("  AND U.status = l.lookup_code AND l.lookup_type= ? AND u.user_id=geo.user_id AND U.status NOT IN ('N') ");
        return selectQueryBuff.toString();
	}
	@Override
	public String loadAllUserDetailsByLoginIDQry(){
		boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
		boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
		boolean optInOutAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
		StringBuilder sqlBuffer = new StringBuilder(" SELECT u.user_id, u.user_name, u.network_code,l.network_name,l.report_header_name, u.login_id, u.password, ");
        sqlBuffer.append(" u.category_code, u.parent_id,u.company,u.fax, u.owner_id, u.msisdn, u.allowed_ip,  u.allowed_days,u.from_time,u.to_time,u.firstname,u.lastname, "); 
        sqlBuffer.append(" u.last_login_on,u.employee_code,u.status userstatus,u.email,u.created_by,u.created_on,u.modified_by, ");
        sqlBuffer.append(" u.modified_on,u.pswd_modified_on,  cusers.contact_person,u.contact_no,u.designation,u.division,u.department, ");
        sqlBuffer.append(" u.msisdn,u.user_type,cusers.in_suspend,cusers.out_suspend,u.address1,u.address2,u.city,u.state,u.country, ");
        sqlBuffer.append(" u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,u.invalid_password_count,u.password_count_updated_on,u.APPOINTMENT_DATE, ");
        sqlBuffer.append(" l.status networkstatus,l.language_1_message,l.language_2_message, cat.category_code,cat.category_name, ");
        sqlBuffer.append(" cat.domain_code,cat.sequence_no,cat.multiple_login_allowed, cat.max_login_count,cat.view_on_network_block, ");
        sqlBuffer.append(" cat.status catstatus, cat.max_txn_msisdn, cat.uncntrl_transfer_allowed, cat.scheduled_transfer_allowed, cat.restricted_msisdns, ");
        sqlBuffer.append(" cat.parent_category_code, cat.product_types_allowed,cat.category_type,cat.hierarchy_allowed, cat.transfertolistonly, ");
        sqlBuffer.append(" cat.grph_domain_type, cat.multiple_grph_domains, cat.fixed_roles, cat.user_id_prefix,cat.web_interface_allowed, ");
        sqlBuffer.append(" cat.services_allowed,cat.domain_allowed,cat.fixed_domains,cat.outlets_allowed,cat.status categorystatus,cusers.comm_profile_set_id,cusers.transfer_profile_id,cusers.user_grade,gdt.sequence_no grph_sequence_no, ");
        sqlBuffer.append(" gdt.grph_domain_type_name,dm.domain_name,dm.status domainstatus,dm.domain_type_code,up.sms_pin,up.PREFIX_ID,up.pin_required,up.invalid_pin_count,dt.restricted_msisdn restricted_msisdn_allow,up.pin_reset,  ");// rahul.d
                                                                                                                                                                                                                                           // for
                                                                                                                                                                                                                                           // korek
        // for Zebra and Tango by sanjeew date 06/07/07
        sqlBuffer.append(" cusers.application_id, cusers.mpay_profile_id, cusers.user_profile_id, cusers.mcommerce_service_allow, up.access_type ");
        // end of Zebra and Tango
        // added for loading password reset info
        sqlBuffer.append(", u.PSWD_RESET, up.PHONE_PROFILE ");
        // added for RSA Authentication
        sqlBuffer.append(", u.rsaflag,cat.SMS_INTERFACE_ALLOWED ");
        sqlBuffer.append(", u.AUTHENTICATION_ALLOWED,cat.authentication_type ");// Added
                                                                                // For
                                                                                // Authentication
                                                                                // Type
        if (isTrfRuleUserLevelAllow)
            sqlBuffer.append(", cusers.trf_rule_type ");
        if (lmsAppl)
            sqlBuffer.append(" , cusers.lms_profile  ");
        if (optInOutAllow) {
            sqlBuffer.append(" , cusers.OPT_IN_OUT_STATUS ");
        }
        sqlBuffer.append(" , cusers.CONTROL_GROUP ");
            sqlBuffer.append(" FROM users u left join networks l on L.network_code=U.network_code ");
            sqlBuffer.append("left join channel_users cusers on cusers.user_id=u.user_id ");
            sqlBuffer.append(" left join user_phones up on(up.msisdn=u.msisdn and up.user_id=u.user_id),categories cat,geographical_domain_types gdt ,domains dm,domain_types dt ");  
            sqlBuffer.append(" WHERE  UPPER(u.login_id)=? AND gdt.grph_domain_type = cat.grph_domain_type ");
        sqlBuffer.append(" AND u.status <> ? AND u.status <> ?   ");
        sqlBuffer.append(" AND cat.category_code=U.category_code AND cat.status <> ? AND dm.domain_code = cat.domain_code ");
        sqlBuffer.append(" AND dt.domain_type_code = dm.domain_type_code ");
        return sqlBuffer.toString();
	}
	@Override
	public String loadAllUserDetailsByExternalCodeQry(){
		boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
		boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
		boolean optInOutAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
		 StringBuilder sqlBuffer = new StringBuilder(" SELECT u.user_id, u.user_name, u.network_code,l.network_name,l.report_header_name, u.login_id, u.password, ");
	        sqlBuffer.append(" u.category_code, u.parent_id,u.company,u.fax, u.owner_id, u.msisdn, u.allowed_ip,  u.allowed_days,u.from_time,u.to_time,u.firstname,u.lastname, "); 
	        sqlBuffer.append(" u.last_login_on,u.employee_code,u.status userstatus,u.email,u.created_by,u.created_on,u.modified_by, ");
	        sqlBuffer.append(" u.modified_on,u.pswd_modified_on,  cusers.contact_person,u.contact_no,u.designation,u.division,u.department, ");
	        sqlBuffer.append(" u.msisdn,u.user_type,cusers.in_suspend,cusers.out_suspend,u.address1,u.address2,u.city,u.state,u.country, ");
	        sqlBuffer.append(" u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,u.invalid_password_count,u.password_count_updated_on, ");
	        sqlBuffer.append(" l.status networkstatus,l.language_1_message,l.language_2_message, cat.category_code,cat.category_name, ");
	        sqlBuffer.append(" cat.domain_code,cat.sequence_no,cat.multiple_login_allowed, cat.max_login_count,cat.view_on_network_block, ");
	        sqlBuffer.append(" cat.status catstatus, cat.max_txn_msisdn, cat.uncntrl_transfer_allowed, cat.scheduled_transfer_allowed, cat.restricted_msisdns, ");
	        sqlBuffer.append(" cat.parent_category_code, cat.product_types_allowed,cat.category_type,cat.hierarchy_allowed, cat.transfertolistonly, ");
	        sqlBuffer.append(" cat.grph_domain_type, cat.multiple_grph_domains, cat.fixed_roles, cat.user_id_prefix,cat.web_interface_allowed, ");
	        sqlBuffer.append(" cat.services_allowed,cat.domain_allowed,cat.fixed_domains,cat.outlets_allowed,cat.status categorystatus,cusers.comm_profile_set_id,cusers.transfer_profile_id,cusers.user_grade,gdt.sequence_no grph_sequence_no, ");
	        sqlBuffer.append(" gdt.grph_domain_type_name,dm.domain_name,dm.status domainstatus,dm.domain_type_code,up.sms_pin,up.PREFIX_ID,up.pin_required,up.invalid_pin_count,dt.restricted_msisdn restricted_msisdn_allow,up.pin_reset,up.user_phones_id,  ");// rahul.d
	                                                                                                                                                                                                                                           // for
	                                                                                                                                                                                                                                           // korek
	        // for Zebra and Tango by sanjeew date 06/07/07
	        sqlBuffer.append(" cusers.application_id, cusers.mpay_profile_id, cusers.user_profile_id, cusers.mcommerce_service_allow, up.access_type ");
	        // end of Zebra and Tango
	        // added for loading password reset info
	        sqlBuffer.append(", u.PSWD_RESET, up.PHONE_PROFILE ");
	        // added for RSA Authentication
	        sqlBuffer.append(", u.rsaflag,cat.SMS_INTERFACE_ALLOWED ");
	        sqlBuffer.append(", u.AUTHENTICATION_ALLOWED,cat.authentication_type ");// Added
	                                                                                // For
	                                                                                // Authentication
	                                                                                // Type
	        if (isTrfRuleUserLevelAllow)
	            sqlBuffer.append(", cusers.trf_rule_type ");
	        if (lmsAppl)
	            sqlBuffer.append(" , cusers.lms_profile  ");
	        if (optInOutAllow) {
	            sqlBuffer.append(" , cusers.OPT_IN_OUT_STATUS ");
	        }
	        sqlBuffer.append(" , cusers.CONTROL_GROUP ");
	        sqlBuffer.append(" FROM users u left join networks l on L.network_code=U.network_code left join channel_users cusers on cusers.user_id=u.user_id ");
	        sqlBuffer.append(" left join user_phones up on (up.msisdn=u.msisdn and up.user_id=u.user_id) ,categories cat,geographical_domain_types gdt ,domains dm,domain_types dt ");  
	        sqlBuffer.append(" WHERE  UPPER(u.external_code)=? AND gdt.grph_domain_type = cat.grph_domain_type ");
	        sqlBuffer.append(" AND u.status <> ? AND u.status <> ?  ");
	        sqlBuffer.append(" AND cat.category_code=U.category_code AND cat.status <> ? AND dm.domain_code = cat.domain_code ");
	        sqlBuffer.append(" AND dt.domain_type_code = dm.domain_type_code ");
        return sqlBuffer.toString();
	}
	@Override
	public String isChildUserActiveQry() {
		StringBuilder strBuff  = new StringBuilder ("with recursive q as( SELECT user_id,status,user_type FROM users ");
		strBuff.append(" WHERE  user_id = ? union all select m.user_id,m.status,m.user_type from users m join q on q.user_id = m.parent_id ) select ");
		strBuff.append(" 1 from q where q.status <> 'N' AND q.status <> 'C' and q.user_type=? AND q.user_id != ? ");
		return strBuff.toString();
	}

	@Override
	public String fetchUserHierarchy(String searchCriteria) {

		StringBuffer strBuff = new StringBuffer("");
		strBuff.append(" SELECT MSISDN, ");
		strBuff.append(" FIRSTNAME , ");
		strBuff.append(" LASTNAME, ");
		strBuff.append(" CAT.CATEGORY_CODE, ");
		strBuff.append(" LOGIN_ID   , CAT.CATEGORY_NAME ");
		strBuff.append(" FROM USERS USR  , CATEGORIES CAT  ");
		strBuff.append(" WHERE USR.USER_ID IN ");
		strBuff.append(" ( ");

		strBuff.append(" with recursive q as( ");
		strBuff.append(" select ");
		strBuff.append(" US1.PARENT_ID ");
		strBuff.append(" from ");

		if ("MSISDN".equalsIgnoreCase(searchCriteria)) {
			strBuff.append(" USERS US1 WHERE US1.msisdn =? ");
		} else {
			strBuff.append(" USERS US1 WHERE US1.login_id =? ");
		}

		strBuff.append(" union all select ");
		strBuff.append(" GD1.PARENT_ID ");
		strBuff.append(" from ");
		strBuff.append(" USERS GD1 join q on ");
		strBuff.append(" q.parent_id = GD1.user_id ");

		strBuff.append(" ) select ");
		strBuff.append(" PARENT_ID ");
		strBuff.append(" from ");
		strBuff.append(" q ");

		strBuff.append(" ) and CAT.CATEGORY_CODE = USR.CATEGORY_CODE  ");

		return strBuff.toString();
	}
	@Override
	public String fetchRecentC2cTxn(String data) {
		
			StringBuilder strBuff = new StringBuilder(" ");
	
			strBuff.append("SELECT ct.msisdn, ct.TO_USER_ID, u.LOGIN_ID,cat.CATEGORY_NAME,");
			strBuff.append("u.USER_NAME_PREFIX,ct.to_msisdn,cat.category_code,lk.LOOKUP_NAME,");
			strBuff.append("u.FIRSTNAME,u.LASTNAME FROM ");
			strBuff.append("CHANNEL_TRANSFERS ct , users u,  CATEGORIES cat, lookups lk ");
			if("LOGINID".equalsIgnoreCase(data)){
				strBuff.append(",users touser ");
			}
			strBuff.append("WHERE ct.STATUS = ? ");
			strBuff.append("AND ct.TYPE = ? ");
			if("MSISDN".equalsIgnoreCase(data)){
				strBuff.append("AND TO_MSISDN = ? ");
			}
			strBuff.append("AND TRANSFER_SUB_TYPE IN('T', 'V') ");
			strBuff.append("and ct.FROM_USER_ID = u.USER_ID ");
			strBuff.append("AND u.CATEGORY_CODE = cat.CATEGORY_CODE ");
			strBuff.append("AND u.USER_NAME_PREFIX = lk.lookup_code ");
			if("LOGINID".equalsIgnoreCase(data)){
				strBuff.append("and touser.user_id = ct.TO_USER_ID ");
				strBuff.append("and touser.login_id= ?");
			}
			strBuff.append(" ORDER BY ct.CLOSE_DATE DESC");

			return strBuff.toString();
	}
	
	@Override
	public String fetchC2cTrfData() {
	
		
		StringBuilder strBuff = new StringBuilder(" ");
	
		strBuff.append("SELECT SUM( transaction_amount ) total,DC.SERVICE_TYPE,");
		strBuff.append("ST.NAME FROM DAILY_C2S_TRANS_DETAILS DC,SERVICE_TYPE ST WHERE ");
		strBuff.append("  DC.USER_ID = ?");
		strBuff.append(" AND DC.TRANS_DATE BETWEEN ? AND ?");
		strBuff.append(" AND DC.SERVICE_TYPE = ST.SERVICE_TYPE");
		strBuff.append(" GROUP BY DC.SERVICE_TYPE,ST.NAME");
		return strBuff.toString();
	}
	
	@Override
	public String fetchTotalTrans() {


		
		StringBuilder strBuff = new StringBuilder(" ");

		
	
		strBuff.append(" SELECT SUM(DTD.TRANSACTION_COUNT) AS TXNCOUNT");
		strBuff.append(" FROM DAILY_C2S_TRANS_DETAILS DTD ");
		strBuff.append(" WHERE DTD.TRANS_DATE BETWEEN ? AND ? ");
		strBuff.append(" AND DTD.USER_ID = ? " );
		
		
		return strBuff.toString();
	}
	/**
  	 * @param request
  	 * @param identifierType
  	 * @return strBuff
  	 */
	@Override
	public String fetchUserDetails( AutoCompleteUserDetailsRequestVO request, String identifierType) {

		
		StringBuilder strBuff = new StringBuilder(" ");
		 strBuff.append(" SELECT U.USER_NAME, U.LOGIN_ID, U.MSISDN,U.USER_ID FROM USERS U ");
		 
		 
		 if(!"".equalsIgnoreCase(request.getCategory())  &&  !"".equalsIgnoreCase(request.getDomain())) {
        	 strBuff.append(" INNER JOIN CATEGORIES C ON U.CATEGORY_CODE = C.CATEGORY_CODE ");
        	 strBuff.append(" WHERE UPPER(U." + identifierType +") " );
        	 strBuff.append(" LIKE UPPER(?) ");
        	 strBuff.append(" AND C.CATEGORY_CODE = ? AND C.DOMAIN_CODE = ? ");
     		 }
         else if( !"".equalsIgnoreCase(request.getDomain())  &&  "".equalsIgnoreCase(request.getCategory())) {
        	 strBuff.append(" INNER JOIN CATEGORIES C ON U.CATEGORY_CODE = C.CATEGORY_CODE ");
        	 strBuff.append(" WHERE UPPER(U." + identifierType +") " );
        	 strBuff.append(" LIKE UPPER(?) ");
        	 strBuff.append(" AND C.DOMAIN_CODE = ? ");
         }
         else if(!"".equalsIgnoreCase(request.getCategory())  &&  "".equalsIgnoreCase(request.getDomain())){ 
        	 strBuff.append(" INNER JOIN CATEGORIES C ON U.CATEGORY_CODE = C.CATEGORY_CODE ");
        	 strBuff.append(" WHERE UPPER(U." + identifierType +") " );
        	 strBuff.append(" LIKE UPPER(?) ");
        	 strBuff.append(" AND C.CATEGORY_CODE = ? ");
         }
         else {
        	 strBuff.append(" WHERE UPPER(U." + identifierType +") " );
        	 strBuff.append(" LIKE UPPER(?) ");
         }
		strBuff.append(" LIMIT ? ");
		return strBuff.toString();
	}
	/**
  	 * @return strBuff
  	 */
	@Override
	public String fetchDomainCat() {

		StringBuilder strBuff = new StringBuilder(" ");
		strBuff.append("SELECT DISTINCT ct.TO_CATEGORY,c.CATEGORY_NAME");
		strBuff.append(" FROM CHNL_TRANSFER_RULES ct,CATEGORIES c");
		strBuff.append(" WHERE ct.DOMAIN_CODE=? AND (ct.DIRECT_TRANSFER_ALLOWED='Y' or ct.TRANSFER_CHNL_BYPASS_ALLOWED='Y' ) AND ");
		strBuff.append("ct.to_category = c.category_code" );
		
		
		return strBuff.toString();
	}
	
	/**
  	 * @return strBuff
  	 */
public String fetchDomainCatFrOpt(){


        
        StringBuilder strBuff = new StringBuilder(" ");

        
    
        strBuff.append("SELECT DISTINCT ct.TO_CATEGORY,c.CATEGORY_NAME");
        strBuff.append(" FROM CHNL_TRANSFER_RULES ct,CATEGORIES c");
        strBuff.append(" WHERE ct.FROM_CATEGORY=? AND (ct.DIRECT_TRANSFER_ALLOWED='Y' or ct.TRANSFER_CHNL_BYPASS_ALLOWED='Y' ) AND ");
        strBuff.append("ct.to_category = c.category_code" );
        
        
        return strBuff.toString();
    }


/**
 * @throws SQLException 
 * 
 */
public PreparedStatement getUsersInHierachyWithCatQry(Connection con,String catCode,String userId) throws SQLException{


        
        StringBuilder strBuff = new StringBuilder(" ");

        
    
        strBuff.append("with recursive usr as(SELECT");
        strBuff.append(" U.USER_NAME,U.USER_ID,U.MSISDN,u.category_code,u.LOGIN_ID,U.STATUS FROM USERS U WHERE  u.USER_TYPE='CHANNEL'AND U.USER_ID = ?");
        strBuff.append(" union all SELECT U1.USER_NAME,U1.USER_ID,U1.MSISDN,u1.category_code,U1.LOGIN_ID,U1.STATUS FROM USERS U1 join usr ur on ur.user_id = U1.parent_id)select   USER_NAME, USER_ID,MSISDN, LOGIN_ID, STATUS from usr where category_code=?");
   
		PreparedStatement pstmtSelect = con.prepareStatement(strBuff.toString());
		 int i = 0;
         ++i;
         pstmtSelect.setString(i, userId);
         ++i;
         pstmtSelect.setString(i, catCode);
        
         return pstmtSelect;
    }


/**
 * @throws SQLException 
 * 
 */
public PreparedStatement getChannelUsersListQry(Connection con,String userDomain,String userCategoryCode,
		String userGeography,String userId,String status,boolean selfAllowed) throws SQLException{


        
	   StringBuilder strBuffer = new StringBuilder(" ");

	   strBuffer.append("with recursive usr as(");
   	strBuffer.append(" SELECT DISTINCT u.user_id,u.CREATED_ON, u.LOGIN_ID, u.USER_NAME,u.MSISDN ,u.user_type,u.STATUS,u2.USER_NAME AS");
   	strBuffer.append(" owner_name,u.OWNER_ID,c.DOMAIN_CODE,d.DOMAIN_NAME,c.category_name,c.category_code,ub.BALANCE,p.PRODUCT_NAME,ub.product_code,u3.USER_NAME AS parent_name,u.PARENT_ID,ug.GRPH_DOMAIN_CODE,gd.GRPH_DOMAIN_NAME,cu.COMM_PROFILE_SET_ID,cu.TRANSFER_PROFILE_ID,cu.USER_GRADE,u.CONTACT_PERSON,cp.COMM_PROFILE_SET_NAME,tp.PROFILE_NAME,u.MODIFIED_ON,ub.last_transfer_on");
   	strBuffer.append(" FROM USERS u");
   	strBuffer.append(" left outer JOIN USERS u2");
   	strBuffer.append(" ON u2.USER_ID=u.OWNER_ID");
//   	strBuffer.append(" left outer JOIN USERS u6");
//   	strBuffer.append(" ON u6.modified_by=u.user_id");
  	strBuffer.append(" left outer JOIN categories c");
   	strBuffer.append(" ON c.category_code=u.category_code");
   	strBuffer.append(" left outer JOIN domains d");
   	strBuffer.append(" ON c.DOMAIN_CODE=d.DOMAIN_CODE");
   	strBuffer.append(" left outer JOIN user_balances ub");
   	strBuffer.append(" on u.user_id=ub.user_id");
   	strBuffer.append(" left outer JOIN users u3");
   	strBuffer.append(" on u.PARENT_ID=u3.user_id");
   	strBuffer.append(" left outer JOIN USER_GEOGRAPHIES ug");
   	strBuffer.append(" on u.user_id=ug.user_id");
   	strBuffer.append(" left outer JOIN GEOGRAPHICAL_DOMAINS gd");
   	strBuffer.append(" on gd.GRPH_DOMAIN_CODE=ug.GRPH_DOMAIN_CODE");
   	strBuffer.append(" left outer JOIN CHANNEL_USERS cu ON u.USER_ID=cu.USER_ID");
	strBuffer.append(" left outer JOIN COMMISSION_PROFILE_SET cp ON cp.COMM_PROFILE_SET_ID=cu.COMM_PROFILE_SET_ID");
	strBuffer.append(" left outer JOIN TRANSFER_PROFILE tp ON tp.PROFILE_ID=cu.TRANSFER_PROFILE_ID");
	strBuffer.append(" left outer JOIN PRODUCTS p ON p.PRODUCT_CODE = ub.PRODUCT_CODE");
	if(!selfAllowed)
	{
		strBuffer.append(" WHERE u.USER_TYPE='CHANNEL'");
	}
	else
	{
		strBuffer.append(" WHERE u.USER_TYPE IN ('CHANNEL', 'STAFF') ");
	}
   	
   	strBuffer.append(" and u.user_id=?");
   	strBuffer.append(" union all SELECT DISTINCT u4.user_id,u4.CREATED_ON, u4.LOGIN_ID, u4.USER_NAME,u4.MSISDN ,u4.USER_TYPE, u4.STATUS,u5.USER_NAME AS");
   	strBuffer.append(" owner_name,u4.OWNER_ID,c1.DOMAIN_CODE,d1.DOMAIN_NAME,c1.category_name,c1.category_code,ub1.BALANCE,p1.PRODUCT_NAME,ub1.product_code,u6.USER_NAME AS parent_name,u4.PARENT_ID,ug1.GRPH_DOMAIN_CODE,gd1.GRPH_DOMAIN_NAME,cu1.COMM_PROFILE_SET_ID,cu1.TRANSFER_PROFILE_ID,cu1.USER_GRADE,u4.CONTACT_PERSON,cp1.COMM_PROFILE_SET_NAME,tp1.PROFILE_NAME,u4.MODIFIED_ON,ub1.last_transfer_on");
   	strBuffer.append(" FROM USERS u4");
   	strBuffer.append(" left outer JOIN USERS u5");
   	strBuffer.append(" ON u5.USER_ID=u4.OWNER_ID");
//	strBuffer.append(" left outer JOIN USERS u7");
//   	strBuffer.append(" ON u7.modified_by=u4.user_id");
   	strBuffer.append(" left outer JOIN categories c1");
   	strBuffer.append(" ON c1.category_code=u4.category_code");
   	strBuffer.append(" left outer JOIN domains d1");
   	strBuffer.append(" ON c1.DOMAIN_CODE=d1.DOMAIN_CODE");
   	strBuffer.append(" left outer JOIN user_balances ub1");
   	strBuffer.append(" on u4.user_id=ub1.user_id");
   	strBuffer.append(" left outer JOIN users u6");
   	strBuffer.append(" on u4.PARENT_ID=u6.user_id");
   	strBuffer.append(" left outer JOIN USER_GEOGRAPHIES ug1");
   	strBuffer.append(" on u4.user_id=ug1.user_id");
   	strBuffer.append(" left outer JOIN GEOGRAPHICAL_DOMAINS gd1");
   	strBuffer.append(" on gd1.GRPH_DOMAIN_CODE=ug1.GRPH_DOMAIN_CODE");
	strBuffer.append(" left outer JOIN CHANNEL_USERS cu1 ON u4.USER_ID=cu1.USER_ID");
	strBuffer.append(" left outer JOIN COMMISSION_PROFILE_SET cp1 ON cp1.COMM_PROFILE_SET_ID=cu1.COMM_PROFILE_SET_ID");
	strBuffer.append(" left outer JOIN TRANSFER_PROFILE tp1 ON tp1.PROFILE_ID=cu1.TRANSFER_PROFILE_ID");
	strBuffer.append(" left outer JOIN PRODUCTS p1 ON p1.PRODUCT_CODE = ub1.PRODUCT_CODE"); 
   	strBuffer.append(" join usr ur on ur.user_id = u4.parent_id )  SELECT user_id,CREATED_ON, LOGIN_ID, USER_NAME,MSISDN,USER_TYPE,STATUS,owner_name,OWNER_ID,DOMAIN_CODE,DOMAIN_NAME,category_name,category_code,BALANCE,product_code,PRODUCT_NAME,parent_name,PARENT_ID,GRPH_DOMAIN_CODE,GRPH_DOMAIN_NAME,MODIFIED_ON,CREATED_ON,COMM_PROFILE_SET_ID,TRANSFER_PROFILE_ID,USER_GRADE,CONTACT_PERSON,COMM_PROFILE_SET_NAME,PROFILE_NAME,last_transfer_on from usr");
   	
   	boolean whereAdded=false;
   	if(!userCategoryCode.equalsIgnoreCase("ALL")) {
   		
   		if(!whereAdded)
   		{
   			strBuffer.append(" where category_code=?");
   			whereAdded=true;
   		}else {
   			
   			strBuffer.append(" AND category_code=?");
   		}
    	
    	}
   	
   	if(!userDomain.equalsIgnoreCase("ALL")) {
   		
   		if(!whereAdded)
   		{
   			strBuffer.append(" where DOMAIN_CODE=?");
   			whereAdded=true;
   		}else {
   			
   			strBuffer.append(" AND DOMAIN_CODE=?");
   		}
   	
   	}
   	
   	
   if(!userGeography.equalsIgnoreCase("ALL"))
   	{
	   if(!whereAdded)
  		{
  			strBuffer.append(" where GRPH_DOMAIN_CODE=?");
  			whereAdded=true;
  		}else {
  			
  			strBuffer.append(" AND GRPH_DOMAIN_CODE=?");
  		}
	   
	   
       }
   if(!status.equalsIgnoreCase("ALL"))
   	{
	   if(!whereAdded)
 		{
 			strBuffer.append(" where status=?");
 			whereAdded=true;
 		}else {
 			
 			strBuffer.append(" and status=?");
 		}
   			
   		
        }
   	strBuffer.append("  order by created_on desc");
   
   	log.debug("My QUery coming is", strBuffer.toString());
   	 
   		PreparedStatement pstmt = con.prepareStatement(strBuffer.toString());
   		
   		int i = 0;
        ++i;
        pstmt.setString(i, userId);
        if(!userCategoryCode.equalsIgnoreCase("ALL")) {
            
            
            pstmt.setString(++i, userCategoryCode);
         
          
        	}
		if(!userDomain.equalsIgnoreCase("ALL"))
    	{
			 pstmt.setString(++i, userDomain);  
     
           
             
            
    	}
		if(!userGeography.equalsIgnoreCase("ALL"))
    	{
			 pstmt.setString(++i, userGeography);  
           
             
            
    	}
		
		if(!status.equalsIgnoreCase("ALL"))
    	{
			 pstmt.setString(++i, status);  
           
             
            
    	}
	
		/*if(!BTSLUtil.isNullString(msisdn2)) {
           ++i;
           pstmt.setString(i, msisdn2);
          
       	}
		else if(!BTSLUtil.isNullString(loginId2))
   	{
			
           ++i;
           pstmt.setString(i, loginId2);  
          
           
   	}
		else if(!BTSLUtil.isNullString(userName))
		{
			
           ++i;
           pstmt.setString(i, userName); 
           ++i;
           pstmt.setString(i, userCategoryCode); 
           ++i;
           pstmt.setString(i, userGeography); 
           ++i;
           pstmt.setString(i, userDomain);           
          
           
           
   	}
		else if(!BTSLUtil.isNullString(fromRow)&&!BTSLUtil.isNullString(toRow)) {
   		int fromPage=Integer.parseInt(fromRow);
			int entriesPerPage=Integer.parseInt(toRow);
   		int offSet=((fromPage*entriesPerPage)-entriesPerPage);
			int fetch=entriesPerPage;
			String offSetStr= Integer.toString(offSet);
			String fetchStr=Integer.toString(fetch);
           ++i;
           pstmt.setString(i, offSetStr); 
           ++i;
           pstmt.setString(i, fetchStr); 
			
		}
*/
       
        return pstmt;

    }	





/**
 * @throws SQLException 
 * 
 */
public PreparedStatement getChannelUsersListTcpQry(Connection con,String userDomain,String userCategoryCode,
		String userGeography,String userId,String status) throws SQLException{


        
	   StringBuilder strBuffer = new StringBuilder(" ");

	   strBuffer.append("with recursive usr as(");
   	strBuffer.append(" SELECT DISTINCT u.user_id,u.CREATED_ON, u.LOGIN_ID, u.USER_NAME,u.MSISDN ,u.STATUS,u2.USER_NAME AS");
   	strBuffer.append(" owner_name,u.OWNER_ID,c.DOMAIN_CODE,d.DOMAIN_NAME,c.category_name,c.category_code,ub.BALANCE,p.PRODUCT_NAME,ub.product_code,u3.USER_NAME AS parent_name,u.PARENT_ID,ug.GRPH_DOMAIN_CODE,gd.GRPH_DOMAIN_NAME,cu.COMM_PROFILE_SET_ID,cu.TRANSFER_PROFILE_ID,cu.USER_GRADE,u.CONTACT_PERSON,cp.COMM_PROFILE_SET_NAME,u.MODIFIED_ON,u6.USER_NAME AS MODIFIED_BY,ub.last_transfer_on");
   	strBuffer.append(" FROM USERS u");
   	strBuffer.append(" left outer JOIN USERS u2");
   	strBuffer.append(" ON u2.USER_ID=u.OWNER_ID");
   	strBuffer.append(" left outer JOIN USERS u6");
   	strBuffer.append(" ON u6.modified_by=u.user_id");
  	strBuffer.append(" left outer JOIN categories c");
   	strBuffer.append(" ON c.category_code=u.category_code");
   	strBuffer.append(" left outer JOIN domains d");
   	strBuffer.append(" ON c.DOMAIN_CODE=d.DOMAIN_CODE");
   	strBuffer.append(" left outer JOIN user_balances ub");
   	strBuffer.append(" on u.user_id=ub.user_id");
   	strBuffer.append(" left outer JOIN users u3");
   	strBuffer.append(" on u.PARENT_ID=u3.user_id");
   	strBuffer.append(" left outer JOIN USER_GEOGRAPHIES ug");
   	strBuffer.append(" on u.user_id=ug.user_id");
   	strBuffer.append(" left outer JOIN GEOGRAPHICAL_DOMAINS gd");
   	strBuffer.append(" on gd.GRPH_DOMAIN_CODE=ug.GRPH_DOMAIN_CODE");
   	strBuffer.append(" left outer JOIN CHANNEL_USERS cu ON u.USER_ID=cu.USER_ID");
	strBuffer.append(" left outer JOIN COMMISSION_PROFILE_SET cp ON cp.COMM_PROFILE_SET_ID=cu.COMM_PROFILE_SET_ID");
//	strBuffer.append(" left outer JOIN TRANSFER_PROFILE tp ON tp.PROFILE_ID=cu.TRANSFER_PROFILE_ID");
	strBuffer.append(" left outer JOIN PRODUCTS p ON p.PRODUCT_CODE = ub.PRODUCT_CODE");
   	strBuffer.append(" WHERE u.USER_TYPE='CHANNEL'");
   	strBuffer.append(" and u.user_id=?");
   	strBuffer.append(" union all SELECT DISTINCT u4.user_id,u4.CREATED_ON, u4.LOGIN_ID, u4.USER_NAME,u4.MSISDN ,u4.STATUS,u5.USER_NAME AS");
   	strBuffer.append(" owner_name,u4.OWNER_ID,c1.DOMAIN_CODE,d1.DOMAIN_NAME,c1.category_name,c1.category_code,ub1.BALANCE,p1.PRODUCT_NAME,ub1.product_code,u6.USER_NAME AS parent_name,u4.PARENT_ID,ug1.GRPH_DOMAIN_CODE,gd1.GRPH_DOMAIN_NAME,cu1.COMM_PROFILE_SET_ID,cu1.TRANSFER_PROFILE_ID,cu1.USER_GRADE,u4.CONTACT_PERSON,cp1.COMM_PROFILE_SET_NAME,u4.MODIFIED_ON,u7.USER_NAME AS MODIFIED_BY,ub1.last_transfer_on");
   	strBuffer.append(" FROM USERS u4");
   	strBuffer.append(" left outer JOIN USERS u5");
   	strBuffer.append(" ON u5.USER_ID=u4.OWNER_ID");
	strBuffer.append(" left outer JOIN USERS u7");
   	strBuffer.append(" ON u7.modified_by=u4.user_id");
   	strBuffer.append(" left outer JOIN categories c1");
   	strBuffer.append(" ON c1.category_code=u4.category_code");
   	strBuffer.append(" left outer JOIN domains d1");
   	strBuffer.append(" ON c1.DOMAIN_CODE=d1.DOMAIN_CODE");
   	strBuffer.append(" left outer JOIN user_balances ub1");
   	strBuffer.append(" on u4.user_id=ub1.user_id");
   	strBuffer.append(" left outer JOIN users u6");
   	strBuffer.append(" on u4.PARENT_ID=u6.user_id");
   	strBuffer.append(" left outer JOIN USER_GEOGRAPHIES ug1");
   	strBuffer.append(" on u4.user_id=ug1.user_id");
   	strBuffer.append(" left outer JOIN GEOGRAPHICAL_DOMAINS gd1");
   	strBuffer.append(" on gd1.GRPH_DOMAIN_CODE=ug1.GRPH_DOMAIN_CODE");
	strBuffer.append(" left outer JOIN CHANNEL_USERS cu1 ON u4.USER_ID=cu1.USER_ID");
	strBuffer.append(" left outer JOIN COMMISSION_PROFILE_SET cp1 ON cp1.COMM_PROFILE_SET_ID=cu1.COMM_PROFILE_SET_ID");
//	strBuffer.append(" left outer JOIN TRANSFER_PROFILE tp1 ON tp1.PROFILE_ID=cu1.TRANSFER_PROFILE_ID");
	strBuffer.append(" left outer JOIN PRODUCTS p1 ON p1.PRODUCT_CODE = ub1.PRODUCT_CODE"); 
   	strBuffer.append(" join usr ur on ur.user_id = u4.parent_id )  SELECT user_id,CREATED_ON, LOGIN_ID, USER_NAME,MSISDN,STATUS,owner_name,OWNER_ID,DOMAIN_CODE,DOMAIN_NAME,category_name,category_code,BALANCE,product_code,PRODUCT_NAME,parent_name,PARENT_ID,GRPH_DOMAIN_CODE,GRPH_DOMAIN_NAME,MODIFIED_BY,MODIFIED_ON,CREATED_ON,COMM_PROFILE_SET_ID,TRANSFER_PROFILE_ID,USER_GRADE,CONTACT_PERSON,COMM_PROFILE_SET_NAME,PROFILE_NAME,last_transfer_on from usr");
   	
   	boolean whereAdded=false;
   	if(!userCategoryCode.equalsIgnoreCase("ALL")) {
   		
   		if(!whereAdded)
   		{
   			strBuffer.append(" where category_code=?");
   			whereAdded=true;
   		}else {
   			
   			strBuffer.append(" AND category_code=?");
   		}
    	
    	}
   	
   	if(!userDomain.equalsIgnoreCase("ALL")) {
   		
   		if(!whereAdded)
   		{
   			strBuffer.append(" where DOMAIN_CODE=?");
   			whereAdded=true;
   		}else {
   			
   			strBuffer.append(" AND DOMAIN_CODE=?");
   		}
   	
   	}
   	
   	
   if(!userGeography.equalsIgnoreCase("ALL"))
   	{
	   if(!whereAdded)
  		{
  			strBuffer.append(" where GRPH_DOMAIN_CODE=?");
  			whereAdded=true;
  		}else {
  			
  			strBuffer.append(" AND GRPH_DOMAIN_CODE=?");
  		}
	   
	   
       }
   if(!status.equalsIgnoreCase("ALL"))
   	{
	   if(!whereAdded)
 		{
 			strBuffer.append(" where status=?");
 			whereAdded=true;
 		}else {
 			
 			strBuffer.append(" and status=?");
 		}
   			
   		
        }
   	strBuffer.append("  order by created_on desc");
   
   	
   	 
   		PreparedStatement pstmt = con.prepareStatement(strBuffer.toString());
   		
   		int i = 0;
        ++i;
        pstmt.setString(i, userId);
        if(!userCategoryCode.equalsIgnoreCase("ALL")) {
            
            
            pstmt.setString(++i, userCategoryCode);
         
          
        	}
		if(!userDomain.equalsIgnoreCase("ALL"))
    	{
			 pstmt.setString(++i, userDomain);  
     
           
             
            
    	}
		if(!userGeography.equalsIgnoreCase("ALL"))
    	{
			 pstmt.setString(++i, userGeography);  
           
             
            
    	}
		
		if(!status.equalsIgnoreCase("ALL"))
    	{
			 pstmt.setString(++i, status);  
           
             
            
    	}
	
       
        return pstmt;

    }

public PreparedStatement getPinPassword(Connection con,String login_id) throws SQLException{


    
    StringBuilder strBuff = new StringBuilder(" ");


    strBuff.append("SELECT u.password,up.sms_pin ");
    strBuff.append(" FROM users u, USER_PHONES up");
    strBuff.append(" WHERE u.USER_ID=up.user_id AND u.login_id = ?");

	PreparedStatement pstmtSelect = con.prepareStatement(strBuff.toString());
	 int i = 0;
     ++i;
     pstmtSelect.setString(i, login_id);
    
    
     return pstmtSelect;
}
	
public PreparedStatement loadUserDetailsByLoginId(Connection con,String login_id) throws SQLException{


    
    StringBuilder strBuff = new StringBuilder(" ");


    strBuff.append("SELECT * ");
    strBuff.append(" FROM users u");
    strBuff.append(" WHERE u.login_id = ?");

	PreparedStatement pstmtSelect = con.prepareStatement(strBuff.toString());
	 int i = 0;
     ++i;
     pstmtSelect.setString(i, login_id);
    
    
     return pstmtSelect;
}

	public PreparedStatement loadUserDetailsBydentifierType(Connection con, String identifierType,
			String identifierValue, String pinOrPass) throws SQLException {

		StringBuilder strBuff = new StringBuilder(" ");
		PreparedStatement pstmtSelect = null;

		
		int i = 0;
		++i;

		if (identifierType != null) {
			if (identifierType.equalsIgnoreCase("loginid")) {
				strBuff.append("SELECT U.login_id, U.msisdn, U.external_code, U.user_id, U.ALLOWED_IP ");
				strBuff.append(" FROM users U");
				strBuff.append(" WHERE  U.status <> 'N' AND U.status <> 'C' AND U.login_id = ? AND U.password = ? ");
				
				pstmtSelect = con.prepareStatement(strBuff.toString());
				
				pstmtSelect.setString(i, identifierValue);
				++i;
				pstmtSelect.setString(i, BTSLUtil.encryptText(pinOrPass));
			} else if (identifierType.equalsIgnoreCase("msisdn")) {
				strBuff.append("SELECT U.login_id, U.msisdn, U.external_code, U.user_id , U.ALLOWED_IP ");
				strBuff.append(" FROM users U, USER_PHONES UP ");
				strBuff.append(
						" WHERE   U.USER_ID = UP.USER_ID AND U.status <> 'N' AND U.status <> 'C' AND UP.msisdn = ? AND UP.SMS_PIN = ?  ");
				
				pstmtSelect = con.prepareStatement(strBuff.toString());
				pstmtSelect.setString(i, identifierValue);
				++i;
				pstmtSelect.setString(i, BTSLUtil.encryptText(pinOrPass));
			} else {
				strBuff.append("SELECT U.login_id, U.msisdn, U.external_code, U.user_id , U.ALLOWED_IP");
				strBuff.append(" FROM users U");
				strBuff.append(" WHERE  U.status <> 'N' AND U.status <> 'C' AND U.external_code = ? ");
				pstmtSelect = con.prepareStatement(strBuff.toString());
				pstmtSelect.setString(i, identifierValue);
			}
		}

		return pstmtSelect;
}
	
	@Override
	public String loadTransactionData() {

		
		StringBuilder strBuff = new StringBuilder(" ");

			strBuff.append("SELECT sum(TRANS_IN_AMOUNT) AS INAMOUNT,sum(TRANS_IN_COUNT) INCOUNT,");
			strBuff.append(" sum(TRANS_OUT_AMOUNT) AS OUTAMOUNT,sum(TRANS_OUT_COUNT) AS OUTCOUNT ");
			strBuff.append(" FROM DAILY_CHNL_TRANS_DETAILS dctd WHERE TYPE = ? AND TRANSFER_SUB_TYPE = ? AND ");
			strBuff.append(" USER_ID = ? AND TRANS_DATE BETWEEN ? AND ?");
			
		
		return strBuff.toString();
	}
	
	@Override
	public String loadUserIncomeC2CandO2CQry() {
		
		StringBuilder strBuff = new StringBuilder(" ");
		
		strBuff.append("SELECT COALESCE( SUM(DCTD.TOTAL_COMMISSION_VALUE), 0) AS netcom, COALESCE( SUM(DCTD.TOTAL_OTF_AMOUNT), 0) AS cbc, DCTD.TRANS_DATE AS transfer_date ");
		strBuff.append(" FROM DAILY_CHNL_TRANS_DETAILS DCTD WHERE ");
		strBuff.append(" DCTD.USER_ID = ? AND DCTD.TRANS_DATE BETWEEN ? AND ? ");
		strBuff.append(" AND ( DCTD.TOTAL_COMMISSION_VALUE > 0 OR DCTD.TOTAL_OTF_AMOUNT > 0) ");
		strBuff.append(" GROUP BY DCTD.TRANS_DATE ORDER BY DCTD.TRANS_DATE");
		
		return strBuff.toString();
	}
	@Override
	public String loadUserTotalIncomeDetailsBetweenRangeC2CAndO2CQry() {
		
		StringBuilder strBuff = new StringBuilder(" ");
		
		strBuff.append("SELECT COALESCE(SUM( DCTD.TOTAL_COMMISSION_VALUE ), 0) AS netcom, COALESCE(SUM( DCTD.TOTAL_OTF_AMOUNT ), 0) AS cbc ");
		strBuff.append(" FROM DAILY_CHNL_TRANS_DETAILS DCTD WHERE ");
		strBuff.append(" DCTD.USER_ID = ? AND DCTD.TRANS_DATE BETWEEN ? AND ?");
		
		return strBuff.toString();
	}

	@Override
	public String loadUserIncomeC2SQry() {
		
		StringBuilder strBuff = new StringBuilder(" ");
		
		strBuff.append("SELECT COALESCE( SUM(DC2SD.TOTAL_MARGIN_AMOUNT) , 0) AS margin, COALESCE( SUM(DC2SD.TOTAL_OTF_AMOUNT), 0) AS cac, DC2SD.TRANS_DATE AS transfer_date ");
		strBuff.append(" FROM DAILY_C2S_TRANS_DETAILS DC2SD WHERE ");
		strBuff.append(" DC2SD.USER_ID = ? AND DC2SD.TRANS_DATE BETWEEN ? AND ?");
		strBuff.append(" AND ( DC2SD.TOTAL_MARGIN_AMOUNT > 0 OR DC2SD.TOTAL_OTF_AMOUNT > 0 ) ");
		strBuff.append(" GROUP BY DC2SD.TRANS_DATE ORDER BY DC2SD.TRANS_DATE");
		return strBuff.toString();
	}

	@Override
	public String loadUserTotalIncomeDetailsBetweenRangeC2SQry() {
		
		StringBuilder strBuff = new StringBuilder(" ");
		
		strBuff.append("SELECT COALESCE( SUM(DC2SD.TOTAL_MARGIN_AMOUNT) , 0) AS margin, COALESCE( SUM(DC2SD.TOTAL_OTF_AMOUNT), 0) AS cac ");
		strBuff.append(" FROM DAILY_C2S_TRANS_DETAILS DC2SD WHERE ");
		strBuff.append(" DC2SD.USER_ID = ? AND DC2SD.TRANS_DATE BETWEEN ? AND ?");
		return strBuff.toString();
	}

	@Override
	public String getLowthreshHoldReportQry(LowThreshHoldReportDTO lowThreshHoldReportDTO) {
			StringBuilder strBuff = new StringBuilder();
			
			strBuff.append( " SELECT U.user_name as USER_NAME , U.msisdn as MSISDN  , UTC.transfer_id as TRANSACTION_ID, UTC.entry_date_time  as ENTRY_DATE_TIME, ");
			strBuff.append( " CAT.category_name as CATEGORY_NAME, UTC.transaction_type  as TRANSFER_TYPE, P.product_name as PRODUCT_NAME ,UTC.type, ");
			strBuff.append( " L1.lookup_name as THRSHOLD_TYPE, L2.lookup_name as USERS_TATUS, UTC.threshold_value as THRESHOLD_VALUE, UTC.previous_balance as PREVIOUS_BALANCE, UTC.current_balance as CURRENT_BALANCE ");
			strBuff.append( " FROM USERS U, USER_THRESHOLD_COUNTER UTC,  LOOKUPS L1,LOOKUPS L2, CATEGORIES CAT, ");   
			strBuff.append( "  PRODUCTS P, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD " );
			strBuff.append( " WHERE UTC.ENTRY_DATE >= ? AND  UTC.entry_date <= ? ");
			strBuff.append( " AND  UTC.network_code= ? ");
			strBuff.append( " AND UTC.user_id IN (with recursive q as ( ");
			strBuff.append( "  SELECT U11.user_id   FROM users U11 where U11.user_id=?  ");
			strBuff.append( "  union all  select m.user_id from USERS m join q on q.user_id=m.parent_id)  " );
			strBuff.append( " select user_id  from q ");
			strBuff.append( " where q.user_id=CASE 'ALL' WHEN 'ALL' THEN q.user_id ELSE  'ALL' END) ");
			strBuff.append( " AND U.user_id=UTC.user_id ");
			strBuff.append( " AND UTC.RECORD_TYPE=CASE ?  WHEN 'ALL' THEN UTC.RECORD_TYPE ELSE  ? END ");
			strBuff.append( " AND L1.lookup_type='THRTP' ");
			strBuff.append( "  AND L1.lookup_code=UTC.RECORD_TYPE "); 
			strBuff.append( "  AND UTC.category_code = CASE ?  WHEN 'ALL' THEN UTC.category_code ELSE ? END ");
			strBuff.append( " AND CAT.category_code=UTC.category_code ");
			strBuff.append( "  AND CAT.domain_code =  ?  ");			   
			strBuff.append( " AND p.PRODUCT_CODE=UTC.PRODUCT_CODE ");
			strBuff.append( "   AND UG.user_id = UTC.user_id "); 
			strBuff.append( "    AND L2.lookup_type='URTYP' ");
			strBuff.append( "   AND L2.lookup_code=U.status ");
			strBuff.append( " AND UG.grph_domain_code = GD.grph_domain_code ");
			strBuff.append("  AND UG.grph_domain_code IN ( ");
			strBuff.append("   with recursive q as (  ");
			strBuff.append("   SELECT grph_domain_code,status ");  
			strBuff.append("  from geographical_domains "); 
			strBuff.append("  WHERE grph_domain_code IN ");
			strBuff.append("   (SELECT grph_domain_code  ");
			strBuff.append("   FROM user_geographies UG1 ");
			strBuff.append("  WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end) ");
			strBuff.append("  AND UG1.user_id= ?) ");
			strBuff.append("   union all "); 
			strBuff.append("  select m.grph_domain_code,m.status "); 
			strBuff.append(" from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code ) ");
			strBuff.append(" select q.grph_domain_code  from q  where status IN('Y', 'S')  ) ");
			
			
			return strBuff.toString();
		

	}
	@Override
	public String getPinPassHistReportQry(PinPassHistoryReqDTO pinPassHistoryReqDTO) {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append( "select u.user_name AS USER_NAME, ");
		strBuff.append( "   pph.msisdn_or_loginid AS MSISDN_OR_LOGINID, ");
		strBuff.append( "     pph.modified_on AS MODIFIED_ON, ");
		strBuff.append( "    modifyUser.user_name AS MODIFIED_BY ");
		strBuff.append( " FROM pin_password_history PPH,USERs U, categories CAT, user_geographies UG, geographical_domains GD , USERS modifyUser   ");
		strBuff.append( "  WHERE  PPH.modified_on >= ? ");
		strBuff.append( "  AND PPH.modified_on <= ? ");
		strBuff.append( "  AND  U.network_Code = ? ");
		strBuff.append( "  AND PPH.USER_ID IN ");
		strBuff.append(  " (with recursive q as ( ");
        strBuff.append( "  SELECT U11.user_id   FROM users U11 where U11.user_id=?  ");
		strBuff.append( "  union all  select m.user_id from USERS m join q on q.user_id=m.parent_id)  " );
		strBuff.append( " select user_id  from q ");
		strBuff.append( " where q.user_id=CASE 'ALL' WHEN 'ALL' THEN q.user_id ELSE  'ALL' END) ");
		strBuff.append( " 	AND PPH.USER_ID = u.user_id ");
		strBuff.append( "  AND    PPH.USER_ID =  CASE 'ALL'  WHEN 'ALL' THEN PPH.user_id    ELSE 'ALL'  END ");
		strBuff.append( "   AND  PPH.USER_TYPE = CASE ? WHEN 'ALL' THEN PPH.USER_TYPE    ELSE ? END ");
		strBuff.append( "  AND  PPH.MODIFICATION_TYPE= ? ");
		strBuff.append( "  AND    PPH.USER_CATEGORY = CASE ? WHEN 'ALL' THEN PPH.USER_CATEGORY ELSE ? END ");
		strBuff.append( " AND    CAT.category_code = PPH.USER_CATEGORY ");
		strBuff.append( " AND    CAT.domain_code = ? ");
		strBuff.append( " AND    UG.user_id = PPH.user_id ");
		strBuff.append( " AND    modifyUser.user_id =pph.modified_by ");
		strBuff.append( " AND    UG.grph_domain_code = GD.grph_domain_code ");
		strBuff.append( " AND    UG.grph_domain_code IN ( WITH recursive q AS ");
		strBuff.append( "        ( SELECT grph_domain_code, status  FROM   geographical_domains  WHERE  grph_domain_code IN  ( SELECT grph_domain_code  "); 
		strBuff.append( " FROM   user_geographies ug1 ");
		strBuff.append( "   WHERE  ug1.grph_domain_code = ( ");
		strBuff.append( "  CASE 'ALL'  WHEN 'ALL' THEN ug1.grph_domain_code ");
		strBuff.append( "    ELSE 'ALL'    END)   AND    ug1.user_id = ? ) ");
		strBuff.append( "   UNION ALL  SELECT m.grph_domain_code, ");
		strBuff.append( "    m.status FROM   geographical_domains m ");
		strBuff.append( " JOIN   q  ON  q.grph_domain_code=m.parent_grph_domain_code ) SELECT q.grph_domain_code ");
		strBuff.append( " FROM   q  WHERE  status IN('Y','S') ) ");
		strBuff.append( "  order by   pph.modified_on desc ");
		return strBuff.toString();
	}
	@Override
	public String getParentOwnerInfo() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append ( " SELECT loggedinUser.user_id loginUserID, ");
	    strBuff.append ( " loggedinUser.status  AS status, ");
	    strBuff.append ( " cu.user_grade    AS grade, ");
	    strBuff.append ( " loggedinUser.email          AS emailID, ");
	    strBuff.append ( " loggedinUser.address1 as address1,");
	    strBuff.append ( " loggedinUser.address2 as address2, ");
	    strBuff.append ( " loggedinUser.state as state, ");
	    strBuff.append ( " loggedinUser.city  as city, ");
	    strBuff.append ( " loggedinUser.country  as country, ");
	    strBuff.append (  " loggedinUser.external_code  AS ERPCODE, ");
	    strBuff.append (  " loggedinUser.category_code  AS category_Code, ");
	    strBuff.append (  " loggedinUser.msisdn         AS msisdn, ");
	    strBuff.append (  " loggedinUser.user_name      USER_NAME, ");
	    strBuff.append (  " loggedinUser.user_name_prefix      USER_NAME_PREFIX, ");
	    strBuff.append (  " loggedinUser.short_name      SHORT_NAME, ");
	    strBuff.append (  " CASE loggedinUser.PARENT_ID WHEN  'ROOT'   THEN  'ROOT'    ELSE PU.user_id   END   AS  PARENTUSERID, ");
	    strBuff.append (  " PU.user_name    AS parent_name, ");
	    strBuff.append (  " CASE loggedinUser.PARENT_ID WHEN  'ROOT'   THEN  'ROOT'   ELSE PU.msisdn    END   as  parent_msisdn, ");
	    strBuff.append (  " PC.category_name      AS  Parent_category_name , ");
	    strBuff.append (  " OU.user_id   as    OwnerUserID, ");
	    strBuff.append (  " OU.user_name     AS owner_name, ");
	    strBuff.append (  " OU.msisdn        AS owner_msisdn, ");
	    strBuff.append (  " OC.category_name AS Owner_Category ");
	    strBuff.append (  " FROM   USERS loggedinUser , ");
	    strBuff.append (  " channel_users cu, ");
	    strBuff.append (  " USERS PU , ");
	    strBuff.append (  " users OU, ");
	    strBuff.append (  " categories PC, ");
	    strBuff.append (  " categories OC ");
	    strBuff.append (  " WHERE  loggedinUser.user_id = ? ");
	    strBuff.append (  " AND cu.user_id =loggedinUser.user_id ");
	    strBuff.append (  " AND  PU.USER_ID = CASE WHEN loggedinUser.PARENT_ID='ROOT'   THEN  ?    ELSE loggedinUser.PARENT_ID  END ");
	    strBuff.append (  " AND OU.user_id = loggedinUser.owner_id ");
	    strBuff.append (  " AND PC.category_code = PU.category_code ");
	    strBuff.append (  " AND OC.category_code = OU.category_code ");
		return strBuff.toString();
	}
	@Override
	public String getParentOwnerInfoForAllUsers() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append ( " SELECT loggedinUser.user_id loginUserID, ");
	    strBuff.append ( " loggedinUser.status  AS status, ");
	    strBuff.append ( " ");
	    strBuff.append ( " loggedinUser.email          AS emailID, ");
	    strBuff.append ( " loggedinUser.address1 as address1,");
	    strBuff.append ( " loggedinUser.address2 as address2, ");
	    strBuff.append ( " loggedinUser.state as state, ");
	    strBuff.append ( " loggedinUser.city  as city, ");
	    strBuff.append ( " loggedinUser.country  as country, ");
	    strBuff.append (  " loggedinUser.external_code  AS ERPCODE, ");
	    strBuff.append (  " loggedinUser.category_code  AS category_Code, ");
	    strBuff.append (  " loggedinUser.msisdn         AS msisdn, ");
	    strBuff.append (  " loggedinUser.user_name      USER_NAME, ");
	    strBuff.append (  " loggedinUser.user_name_prefix      USER_NAME_PREFIX, ");
	    strBuff.append (  " loggedinUser.short_name      SHORT_NAME, ");
	    strBuff.append (  " CASE loggedinUser.PARENT_ID WHEN  'ROOT'   THEN  'ROOT'    ELSE PU.user_id   END   AS  PARENTUSERID, ");
	    strBuff.append (  " PU.user_name    AS parent_name, ");
	    strBuff.append (  " CASE loggedinUser.PARENT_ID WHEN  'ROOT'   THEN  'ROOT'   ELSE PU.msisdn    END   as  parent_msisdn, ");
	    strBuff.append (  " PC.category_name      AS  Parent_category_name , ");
	    strBuff.append (  " OU.user_id   as    OwnerUserID, ");
	    strBuff.append (  " OU.user_name     AS owner_name, ");
	    strBuff.append (  " OU.msisdn        AS owner_msisdn, ");
	    strBuff.append (  " OC.category_name AS Owner_Category ");
	    strBuff.append (  " FROM   USERS loggedinUser , ");
	    strBuff.append (  " ");
	    strBuff.append (  " USERS PU , ");
	    strBuff.append (  " users OU, ");
	    strBuff.append (  " categories PC, ");
	    strBuff.append (  " categories OC ");
	    strBuff.append (  " WHERE  loggedinUser.user_id = ? ");
	    strBuff.append (  " ");
	    strBuff.append (  " AND  PU.USER_ID = CASE WHEN loggedinUser.PARENT_ID='ROOT'   THEN  ?    ELSE loggedinUser.PARENT_ID  END ");
	    strBuff.append (  " AND OU.user_id = loggedinUser.owner_id ");
	    strBuff.append (  " AND PC.category_code = PU.category_code ");
	    strBuff.append (  " AND OC.category_code = OU.category_code ");
		return strBuff.toString();
	}
	
	
	@Override
	public String loadUserDetailsCompletelyByMsisdnQry() {
		
		 StringBuilder selectQueryBuff = new StringBuilder("SELECT U.address1, U.address2, U.allowed_days, U.allowed_ip, U.appointment_date, U.batch_id,U.creation_type,");
	      selectQueryBuff.append(" U.category_code, U.city, U.contact_no, U.contact_person, U.created_by, U.created_on, U.creation_type, U.department, U.designation,");
	      selectQueryBuff.append(" U.division, U.email, U.employee_code, U.external_code, U.from_time,  U.invalid_password_count, U.last_login_on, U.level1_approved_by,");
	      selectQueryBuff.append(" U.level1_approved_on, U.level2_approved_by, U.level2_approved_on, U.login_id, U.modified_by, U.modified_on, U.network_code,U.company,U.fax,U.firstname,U.lastname, ");
	      selectQueryBuff.append(" U.owner_id, U.parent_id, U.password, U.password_count_updated_on, U.previous_status, U.pswd_modified_on, U.reference_id, U.remarks,");
	      selectQueryBuff.append(" U.short_name, U.ssn,U.rsaflag, U.state, U.status userstatus, U.to_time, user_code, U.user_id, U.user_name, U.user_name_prefix, U.user_type, CU.activated_on,");
	      selectQueryBuff.append(" CU.comm_profile_set_id, CU.contact_person, CU.in_suspend, CU.out_suspend, CU.outlet_code, CU.suboutlet_code, CU.transfer_profile_id,");
	      selectQueryBuff.append(" CU.user_grade, C.agent_allowed, C.category_name, C.category_type, C.domain_allowed, C.domain_code, C.fixed_domains, C.fixed_roles,");
	      selectQueryBuff.append(" C.grph_domain_type, C.hierarchy_allowed, C.max_login_count, C.max_txn_msisdn, C.multiple_grph_domains, C.multiple_login_allowed, C.outlets_allowed, ");
	      selectQueryBuff.append(" C.parent_category_code, C.product_types_allowed, C.restricted_msisdns,C.scheduled_transfer_allowed, C.sequence_no catseq, C.services_allowed, ");
	      selectQueryBuff.append(" C.sms_interface_allowed, C.status, C.transfertolistonly, C.uncntrl_transfer_allowed,C.user_id_prefix, C.view_on_network_block, C.web_interface_allowed, ");
	      selectQueryBuff.append(" UP.msisdn,UP.primary_number, UP.description, UP.sms_pin, UP.pin_required, UP.phone_profile, UP.phone_language, UP.country, UP.invalid_pin_count, UP.last_transaction_status,");
	      selectQueryBuff.append(" UP.last_transaction_on, UP.pin_modified_on, UP.last_transfer_id, UP.last_transfer_type, UP.temp_transfer_id, UP.first_invalid_pin_time, UP.user_phones_id, ");
	      selectQueryBuff.append("UP.created_by,UP.created_on,UP.modified_by,UP.modified_on,UP.pin_reset,UP.last_transfer_type,");
	    
	      selectQueryBuff.append(" CU.application_id, CU.mpay_profile_id, CU.user_profile_id, CU.mcommerce_service_allow, UP.PREFIX_ID,l.lookup_name, geo.GRPH_DOMAIN_CODE ");
	     

	      selectQueryBuff.append(" FROM users U, channel_users CU, categories C, user_phones UP, lookups l, user_geographies geo ");
	      selectQueryBuff.append(" WHERE CU.user_id=U.user_id AND U.category_code=C.category_code AND UP.msisdn=?");
	      selectQueryBuff.append(" AND U.user_id=UP.user_id  AND U.status = l.lookup_code AND l.lookup_type= ? AND u.user_id=geo.user_id ");
	      return selectQueryBuff.toString();  
	}
	@Override
	public String checkChildUserUnderLoggedInUserQry() {
		
		StringBuilder selectQueryBuff = new StringBuilder(" SELECT X.user_id,X.parent_id,X.OWNER_ID,X.CATEGORY_CODE,X.USER_name	FROM ( with recursive q as(  "); 
		selectQueryBuff.append( "  SELECT USR.user_id, USR.parent_id, USR.OWNER_ID,USR.CATEGORY_CODE,USR.USER_name ");  
		selectQueryBuff.append( "		 FROM users USR  ");  
		selectQueryBuff.append( "		 where USR.user_id=? "); 
		selectQueryBuff.append( "	    union all ");  
		selectQueryBuff.append( "		 SELECT USR.user_id, USR.parent_id, USR.OWNER_ID,USR.CATEGORY_CODE,USR.USER_name "); 
		selectQueryBuff.append( "		 FROM users USR join q on q.user_id = USR.parent_id ");  
		selectQueryBuff.append( "		    ) select user_id, parent_id, OWNER_ID,CATEGORY_CODE,USER_name from q ) X ");
		selectQueryBuff.append( "		    where X.USER_ID = ? ");
		
		return selectQueryBuff.toString();  
	}
	
	@Override
	public String loadOwnerUserListQry(String p_statusUsed, String p_status) {
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT u.user_name, u.user_id, u.owner_id, u.login_id,ug.grph_domain_code ");
        strBuff.append("FROM users u,user_geographies ug,categories c ");
        strBuff.append("WHERE UPPER(u.user_name) LIKE UPPER(?) ");
        // Added for bar for del by shashank
        if ((p_status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) || (p_status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE))) {
            strBuff.append("u.barred_deletion_batchid IS NULL ");
        }
        // end
        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append("AND u.status IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append("AND u.status NOT IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append("AND u.status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append("AND u.status <> ? ");
        }
        strBuff.append("AND u.user_type ='CHANNEL' ");
        strBuff.append("AND u.user_id = ug.user_id AND c.domain_code=? ");
        strBuff.append("AND u.category_code=c.category_code ");
        strBuff.append("AND c.SEQUENCE_NO='1'  ");
        strBuff.append("AND ug.grph_domain_code IN ( ");
        strBuff.append("with recursive q as (SELECT grph_domain_code,grph_domain_type FROM geographical_domains where ");
        strBuff.append(" grph_domain_code = ? union all select m.grph_domain_code, m.grph_domain_type from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code   ");
        strBuff.append(" ) select q.grph_domain_code from q   WHERE grph_domain_type=c.grph_domain_type ");
        strBuff.append(" )  ");
        strBuff.append("ORDER BY user_name ");
		return strBuff.toString();
	}

	
	  /**
	   * @author harshita.bajaj
	   * @param con
	   * @param userDomain
	   * @param userCategoryCode
	   * @param userGeography
	   * @param userId
	   * @param status
	   * @param selfAllowed
	   * @return
	   * @throws SQLException
	   */
	
	public PreparedStatement getChannelUsersListQry1(Connection con, String userDomain, String userCategoryCode,
			String userGeography, String userId, String status, boolean selfAllowed) throws SQLException {

		StringBuilder strBuffer = new StringBuilder(" ");

		strBuffer.append("with recursive usr as(");
		strBuffer.append(
				" SELECT DISTINCT u.user_id,u.CREATED_ON, u.LOGIN_ID, u.USER_NAME,u.MSISDN ,u.user_type,u.STATUS,u2.USER_NAME AS");
		strBuffer.append(
				" owner_name,u.OWNER_ID,c.DOMAIN_CODE,d.DOMAIN_NAME,c.category_name,c.category_code,ub.BALANCE,p.PRODUCT_NAME,ub.product_code,u3.USER_NAME AS parent_name,u.PARENT_ID,ug.GRPH_DOMAIN_CODE,gd.GRPH_DOMAIN_NAME,cu.COMM_PROFILE_SET_ID,cu.TRANSFER_PROFILE_ID,cu.USER_GRADE,u.CONTACT_PERSON,cp.COMM_PROFILE_SET_NAME,tp.PROFILE_NAME,u.MODIFIED_ON,u6.USER_NAME AS MODIFIED_BY,ub.last_transfer_on");
		strBuffer.append(" FROM USERS u");
		strBuffer.append(" left outer JOIN USERS u2");
		strBuffer.append(" ON u2.USER_ID=u.OWNER_ID");
		strBuffer.append(" left outer JOIN USERS u6");
		strBuffer.append(" ON u.modified_by=u6.user_id");
		strBuffer.append(" left outer JOIN categories c");
		strBuffer.append(" ON c.category_code=u.category_code");
		strBuffer.append(" left outer JOIN domains d");
		strBuffer.append(" ON c.DOMAIN_CODE=d.DOMAIN_CODE");
		strBuffer.append(" left outer JOIN user_balances ub");
		strBuffer.append(" on u.user_id=ub.user_id");
		strBuffer.append(" left outer JOIN users u3");
		strBuffer.append(" on u.PARENT_ID=u3.user_id");
		strBuffer.append(" left outer JOIN USER_GEOGRAPHIES ug");
		strBuffer.append(" on u.user_id=ug.user_id");
		strBuffer.append(" left outer JOIN GEOGRAPHICAL_DOMAINS gd");
		strBuffer.append(" on gd.GRPH_DOMAIN_CODE=ug.GRPH_DOMAIN_CODE");
		strBuffer.append(" left outer JOIN CHANNEL_USERS cu ON u.USER_ID=cu.USER_ID");
		strBuffer.append(" left outer JOIN COMMISSION_PROFILE_SET cp ON cp.COMM_PROFILE_SET_ID=cu.COMM_PROFILE_SET_ID");
		strBuffer.append(" left outer JOIN TRANSFER_PROFILE tp ON tp.PROFILE_ID=cu.TRANSFER_PROFILE_ID");
		strBuffer.append(" left outer JOIN PRODUCTS p ON p.PRODUCT_CODE = ub.PRODUCT_CODE");
		strBuffer.append(" WHERE u.USER_TYPE='CHANNEL'");
		strBuffer.append(" and u.user_id=?");
		strBuffer.append(
				" union all SELECT DISTINCT u4.user_id,u4.CREATED_ON, u4.LOGIN_ID, u4.USER_NAME,u4.MSISDN ,u4.USER_TYPE, u4.STATUS,u5.USER_NAME AS");
		strBuffer.append(
				" owner_name,u4.OWNER_ID,c1.DOMAIN_CODE,d1.DOMAIN_NAME,c1.category_name,c1.category_code,ub1.BALANCE,p1.PRODUCT_NAME,ub1.product_code,u6.USER_NAME AS parent_name,u4.PARENT_ID,ug1.GRPH_DOMAIN_CODE,gd1.GRPH_DOMAIN_NAME,cu1.COMM_PROFILE_SET_ID,cu1.TRANSFER_PROFILE_ID,cu1.USER_GRADE,u4.CONTACT_PERSON,cp1.COMM_PROFILE_SET_NAME,tp1.PROFILE_NAME,u4.MODIFIED_ON,u7.USER_NAME AS MODIFIED_BY,ub1.last_transfer_on");
		strBuffer.append(" FROM USERS u4");
		strBuffer.append(" left outer JOIN USERS u5");
		strBuffer.append(" ON u5.USER_ID=u4.OWNER_ID");
		strBuffer.append(" left outer JOIN USERS u7");
		strBuffer.append(" ON u4.modified_by=u7.user_id");
		strBuffer.append(" left outer JOIN categories c1");
		strBuffer.append(" ON c1.category_code=u4.category_code");
		strBuffer.append(" left outer JOIN domains d1");
		strBuffer.append(" ON c1.DOMAIN_CODE=d1.DOMAIN_CODE");
		strBuffer.append(" left outer JOIN user_balances ub1");
		strBuffer.append(" on u4.user_id=ub1.user_id");
		strBuffer.append(" left outer JOIN users u6");
		strBuffer.append(" on u4.PARENT_ID=u6.user_id");
		strBuffer.append(" left outer JOIN USER_GEOGRAPHIES ug1");
		strBuffer.append(" on u4.user_id=ug1.user_id");
		strBuffer.append(" left outer JOIN GEOGRAPHICAL_DOMAINS gd1");
		strBuffer.append(" on gd1.GRPH_DOMAIN_CODE=ug1.GRPH_DOMAIN_CODE");
		strBuffer.append(" left outer JOIN CHANNEL_USERS cu1 ON u4.USER_ID=cu1.USER_ID");
		strBuffer.append(
				" left outer JOIN COMMISSION_PROFILE_SET cp1 ON cp1.COMM_PROFILE_SET_ID=cu1.COMM_PROFILE_SET_ID");
		strBuffer.append(" left outer JOIN TRANSFER_PROFILE tp1 ON tp1.PROFILE_ID=cu1.TRANSFER_PROFILE_ID");
		strBuffer.append(" left outer JOIN PRODUCTS p1 ON p1.PRODUCT_CODE = ub1.PRODUCT_CODE");
		strBuffer.append(
				" join usr ur on ur.user_id = u4.parent_id )  SELECT user_id,CREATED_ON, LOGIN_ID, USER_NAME,MSISDN,USER_TYPE,STATUS,owner_name,OWNER_ID,DOMAIN_CODE,DOMAIN_NAME,category_name,category_code,BALANCE,product_code,PRODUCT_NAME,parent_name,PARENT_ID,GRPH_DOMAIN_CODE,GRPH_DOMAIN_NAME,MODIFIED_BY,MODIFIED_ON,CREATED_ON,COMM_PROFILE_SET_ID,TRANSFER_PROFILE_ID,USER_GRADE,CONTACT_PERSON,COMM_PROFILE_SET_NAME,PROFILE_NAME,last_transfer_on from usr");

		boolean whereAdded = false;
		if (!userCategoryCode.equalsIgnoreCase("ALL")) {

			if (!whereAdded) {
				strBuffer.append(" where category_code=?");
				whereAdded = true;
			} else {

				strBuffer.append(" AND category_code=?");
			}

		}

		if (!userDomain.equalsIgnoreCase("ALL")) {

			if (!whereAdded) {
				strBuffer.append(" where DOMAIN_CODE=?");
				whereAdded = true;
			} else {

				strBuffer.append(" AND DOMAIN_CODE=?");
			}

		}

		if (!userGeography.equalsIgnoreCase("ALL")) {
			if (!whereAdded) {
				strBuffer.append(" where GRPH_DOMAIN_CODE=?");
				whereAdded = true;
			} else {

				strBuffer.append(" AND GRPH_DOMAIN_CODE=?");
			}

		}
		if (!status.equalsIgnoreCase("ALL")) {
			if (!whereAdded) {
				strBuffer.append(" where status=?");
				whereAdded = true;
			} else {

				strBuffer.append(" and status=?");
			}

		}
		strBuffer.append("  order by created_on desc");

		log.debug("My QUery coming is", strBuffer.toString());

		PreparedStatement pstmt = con.prepareStatement(strBuffer.toString());

		int i = 0;
		++i;
		pstmt.setString(i, userId);
		if (!userCategoryCode.equalsIgnoreCase("ALL")) {

			pstmt.setString(++i, userCategoryCode);

		}
		if (!userDomain.equalsIgnoreCase("ALL")) {
			pstmt.setString(++i, userDomain);

		}
		if (!userGeography.equalsIgnoreCase("ALL")) {
			pstmt.setString(++i, userGeography);

		}

		if (!status.equalsIgnoreCase("ALL")) {
			pstmt.setString(++i, status);

		}

		return pstmt;

	}
	@Override
	public PreparedStatement getChannelUserListByParentQry1(Connection con, String userDomain, String userCategoryCode,
			String userGeography, String parentUserID,String userName,String ownerUserID) throws SQLException {
		final String methodName ="getChannelUserListByParentQry1";
		StringBuilder strBuffer = new StringBuilder(" ");

		strBuffer.append("	    SELECT distinct u.user_id, ");
		strBuffer.append(" u.modified_on, ");
		strBuffer.append(" u.created_on, ");
		strBuffer.append(" u.user_type, ");
		strBuffer.append(" u.login_id, ");
		strBuffer.append(" u.user_name, ");
		strBuffer.append(" up.msisdn, ");
		strBuffer.append(" u.status, ");
		strBuffer.append(" l.LOOKUP_NAME as statusdesc, ");
		strBuffer.append(" u2.user_name AS owner_name, ");
		strBuffer.append(" u4.user_name AS MODIFIED_BY, ");
		strBuffer.append(" u.owner_id, ");
		strBuffer.append(" c.domain_code, ");
		strBuffer.append(" d.domain_name, ");
		strBuffer.append(" c.category_name, ");
		strBuffer.append(" c.sequence_no, ");
		strBuffer.append(" c.category_code, ");
		strBuffer.append(" u3.user_name AS parent_name, ");
		strBuffer.append(" u.parent_id, ");
		strBuffer.append(" ug.grph_domain_code, ");
		strBuffer.append(" gd.grph_domain_name, ");
		strBuffer.append(" u.contact_person ");
		strBuffer.append("             FROM  ");                
		strBuffer.append("    ( with recursive q as(  "); 
		strBuffer.append("   SELECT USR.user_id, USR.parent_id, USR.OWNER_ID,USR.USER_TYPE ,USR.STATUS ");  
		strBuffer.append(" 	 FROM users USR  ");   
		strBuffer.append("	 where USR.user_id=( CASE ? WHEN 'ALL' THEN usr.user_id ELSE ?   END) "); 
		strBuffer.append("	    union all  ");  
		strBuffer.append(" SELECT USR1.user_id, USR1.parent_id, USR1.OWNER_ID ,USR1.USER_TYPE,USR1.STATUS ");   
		strBuffer.append("	 FROM users USR1 join q on q.user_id = USR1.parent_id ");  
		strBuffer.append("	    ) select user_id, parent_id, OWNER_ID,USER_TYPE,STATUS from q where STATUS !='N'  and user_type='CHANNEL'  )X , ");
		strBuffer.append("			users u   "); 
		strBuffer.append("		INNER JOIN users u2   ");
		strBuffer.append(" ON u2.user_id = u.owner_id and U.USER_TYPE ='CHANNEL' ");
		strBuffer.append(" INNER  JOIN Lookups l ON l.lookup_type = 'TFTYP'AND l.lookup_code = u.status  and l.status ='Y' ");
		strBuffer.append("  INNER JOIN users u4 ");
		strBuffer.append(" ON u.modified_by = u4.user_id ");
		strBuffer.append(" INNER JOIN categories c ");
		strBuffer.append(" ON c.category_code = u.category_code ");
		strBuffer.append(" INNER JOIN user_phones up ");
		strBuffer.append("  ON up.user_id = u.user_id ");
		strBuffer.append("  INNER JOIN domains d ");
		strBuffer.append("  ON c.domain_code = d.domain_code ");
		strBuffer.append(" left JOIN users u3 ");
		strBuffer.append("    ON u.parent_id = u3.user_id ");
		strBuffer.append("        left JOIN user_geographies ug ");
		strBuffer.append("         ON u.user_id = ug.user_id ");
		strBuffer.append("     left outer JOIN geographical_domains gd ");
		strBuffer.append("    ON gd.grph_domain_code = ug.grph_domain_code "); 
		strBuffer.append("   where X.USER_ID =U.USER_ID  ");
		
		
		if (!userCategoryCode.equalsIgnoreCase("ALL")) {
			strBuffer.append("AND  u.CATEGORY_CODE= ? ");
		}
		if (!userDomain.equalsIgnoreCase("ALL")) {
			strBuffer.append("AND d.DOMAIN_CODE=? ");
		}
		
		
		if (!userGeography.equalsIgnoreCase("ALL")|| (userGeography!=null && userGeography.trim().length()>0  ) ) {
			strBuffer.append("AND ug.GRPH_DOMAIN_CODE IN ( ");
			strBuffer.append("   with recursive q as( ");
			strBuffer.append("		  SELECT gd1.grph_domain_code, gd1.status  "); 
			strBuffer.append("		  FROM geographical_domains GD1 ");  
			strBuffer.append("		  where grph_domain_code IN  (SELECT grph_domain_code  "); 
			strBuffer.append("		  FROM user_geographies UG1  "); 
			strBuffer.append("		  WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end)  )");
			strBuffer.append("		     union all  ");
			strBuffer.append("		  SELECT gd1.grph_domain_code, gd1.status  "); 
			strBuffer.append("			  FROM geographical_domains GD1 join q on q.grph_domain_code = gd1.parent_grph_domain_code    ) ");
			strBuffer.append("		  SELECT grph_domain_code  FROM q   WHERE status IN('Y','S')   ) ");
			
			
		}
	
		
		
		if (userName!=null &&  userName.trim().length()>0 ) {	
		userName="%"+userName+"%";
		userName=userName.toUpperCase();
		if (userName!=null && !BTSLUtil.isNullorEmpty(userName)) {
			strBuffer.append(" AND UPPER(u.user_name) like ?  ");
		}
		}
		
		if(!BTSLUtil.isNullString(ownerUserID)  ) {
			strBuffer.append(" AND u.owner_id = ?");
		}

		strBuffer.append(" ORDER BY c.SEQUENCE_NO");

		int i = 0;

		PreparedStatement pstmt = con.prepareStatement(strBuffer.toString());
		
		if(parentUserID==null || (parentUserID!=null && parentUserID.trim().length()==0)) {
			parentUserID="ALL";
		}
		
		
			pstmt.setString(++i, parentUserID);
			pstmt.setString(++i, parentUserID);
		
		
		if (!userCategoryCode.equalsIgnoreCase("ALL")) {
			pstmt.setString(++i, userCategoryCode);
		}
		if (!userDomain.equalsIgnoreCase("ALL")) {
			pstmt.setString(++i, userDomain);

		}
		if (!userGeography.equalsIgnoreCase("ALL")|| (userGeography!=null && userGeography.trim().length()>0  ) ) {
			pstmt.setString(++i, userGeography);
			pstmt.setString(++i, userGeography);

		}

		
		if (userName!=null &&  userName.trim().length()>0 ) {
			pstmt.setString(++i, userName);
		}
		
		if(!BTSLUtil.isNullString(ownerUserID)  ) {
			pstmt.setString(++i, ownerUserID);
		}

		log.debug(methodName, strBuffer.toString());
		return pstmt;
	}
	
	/**
	   * @author subesh.vasu
	   * @param con
	   * @param userDomain
	   * @param userCategoryCode
	   * @param userGeography
	   * @param userId
	   * @param status
	   * @param selfAllowed
	   * @return
	   * @throws SQLException
	   */
	  
	public PreparedStatement getStaffUserListByParentQry1(Connection con,StaffUserListByParntReqVO requestVO) throws SQLException{

		final String methodName ="getStaffUserListByParentQry1";
		String userName = requestVO.getUserName();
			StringBuilder strBuffer = new StringBuilder(" ");

			strBuffer.append("	    SELECT distinct  u.user_id, ");
			strBuffer.append(" u.modified_on, ");
			strBuffer.append(" u.created_on, ");
			strBuffer.append(" u.user_type, ");
			strBuffer.append(" u.login_id, ");
			strBuffer.append(" u.user_name, ");
			strBuffer.append(" up.msisdn, ");
			strBuffer.append(" u.status, ");
			strBuffer.append(" l.LOOKUP_NAME as statusdesc, ");
			strBuffer.append(" u2.user_name AS owner_name, ");
			strBuffer.append(" u4.user_name AS MODIFIED_BY, ");
			strBuffer.append(" u.owner_id, ");
			strBuffer.append(" c.domain_code, ");
			strBuffer.append(" d.domain_name, ");
			strBuffer.append(" c.category_name, ");
			strBuffer.append(" c.sequence_no, ");
			strBuffer.append(" c.category_code, ");
			strBuffer.append(" u3.user_name AS parent_name, ");
			strBuffer.append(" u3.login_id as parentLoginID, ");
			strBuffer.append(" u.parent_id, ");
			strBuffer.append(" ug.grph_domain_code, ");
			strBuffer.append(" gd.grph_domain_name, ");
			strBuffer.append(" u.contact_person ");
			strBuffer.append("             FROM  ");                
			strBuffer.append("    ( with recursive q as(  "); 
			strBuffer.append("   SELECT USR.user_id, USR.parent_id, USR.OWNER_ID,USR.USER_TYPE ,USR.STATUS ");  
			strBuffer.append(" 	 FROM users USR  ");   
			strBuffer.append("	 where USR.user_id=(CASE ? WHEN 'ALL' THEN USR.user_id  ELSE ?  END)         "); 
			strBuffer.append("	    union all  ");  
			strBuffer.append(" SELECT USR1.user_id, USR1.parent_id, USR1.OWNER_ID ,USR1.USER_TYPE,USR1.STATUS ");   
			strBuffer.append("	 FROM users USR1 join q on q.user_id = USR1.parent_id ");  
			strBuffer.append("	    ) select user_id, parent_id, OWNER_ID,USER_TYPE,STATUS from q where STATUS !='N'  and user_type='STAFF'  )X , ");
			strBuffer.append("			users u   "); 
			strBuffer.append("		INNER JOIN users u2   ");
			strBuffer.append(" ON u2.user_id = u.owner_id and U.USER_TYPE ='STAFF' ");
			strBuffer.append(" INNER  JOIN Lookups l ON l.lookup_type = 'TFTYP'AND l.lookup_code = u.status  and l.status ='Y' ");
			strBuffer.append("  INNER JOIN users u4 ");
			strBuffer.append(" ON u.modified_by = u4.user_id ");
			strBuffer.append(" INNER JOIN categories c ");
			strBuffer.append(" ON c.category_code = u.category_code ");
			strBuffer.append(" LEFT JOIN user_phones up ");
			strBuffer.append("  ON up.user_id = u.user_id ");
			strBuffer.append("  INNER JOIN domains d ");
			strBuffer.append("  ON c.domain_code = d.domain_code ");
			strBuffer.append(" left JOIN users u3 ");
			strBuffer.append("    ON u.parent_id = u3.user_id ");
			strBuffer.append("        left JOIN user_geographies ug ");
			strBuffer.append("         ON u.user_id = ug.user_id ");
			strBuffer.append("     left outer JOIN geographical_domains gd ");
			strBuffer.append("    ON gd.grph_domain_code = ug.grph_domain_code "); 
			strBuffer.append("   where X.USER_ID =U.USER_ID   and u.status!='N'  AND (up.primary_number = 'Y' or  up.primary_number is null)  ");
			
			
			
			
			if(requestVO.getLoginID()!=null && !requestVO.getLoginID().equalsIgnoreCase(PretupsI.ALL)) {
				strBuffer.append("AND  u.login_id= ? ");
				
			}
			
			if(requestVO.getMsisdn()!=null && !requestVO.getMsisdn().equalsIgnoreCase(PretupsI.ALL)) {
				strBuffer.append("AND  up.msisdn= ? ");
				
			}

			if (requestVO.getUserCategory()!=null && !requestVO.getUserCategory().equalsIgnoreCase(PretupsI.ALL)) {
				strBuffer.append("AND  u.CATEGORY_CODE= ? ");
			}
			if (requestVO.getDomain()!=null &&   !requestVO.getDomain().equalsIgnoreCase(PretupsI.ALL)) {
				strBuffer.append("AND d.DOMAIN_CODE=? ");
			}
			

			if (!requestVO.getGeography().equalsIgnoreCase("ALL")|| (requestVO.getGeography()!=null && requestVO.getGeography().trim().length()>0  ) ) {
				strBuffer.append("   AND UG.grph_domain_code IN ( ");
				strBuffer.append("   with recursive q as( ");
				strBuffer.append("		  SELECT gd1.grph_domain_code, gd1.status  "); 
				strBuffer.append("		  FROM geographical_domains GD1 ");  
				strBuffer.append("		  where grph_domain_code IN  (SELECT grph_domain_code  "); 
				strBuffer.append("		  FROM user_geographies UG1  "); 
				strBuffer.append("		  WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end)  ");
				strBuffer.append("		     )  ");
				strBuffer.append("		     union all  ");
				strBuffer.append("		  SELECT gd1.grph_domain_code, gd1.status  "); 
				strBuffer.append("			  FROM geographical_domains GD1 join q on q.grph_domain_code = gd1.parent_grph_domain_code    ) ");
				strBuffer.append("		  SELECT grph_domain_code  FROM q   WHERE status IN('Y','S')   ) ");
			}
			
			
			
			
			if (userName!=null && !BTSLUtil.isNullorEmpty(userName) && !userName.equalsIgnoreCase(PretupsI.ALL) ) {
				userName="%"+userName+"%";
				userName=userName.toUpperCase();
				strBuffer.append(" AND UPPER(u.user_name) like ?  ");
			}
			
			if(!BTSLUtil.isNullString(requestVO.getOwnerUserID())  && !requestVO.getOwnerUserID().equals(PretupsI.ALL) ) {
				strBuffer.append(" AND u.owner_id = ? ");
			}
			
			strBuffer.append(" ORDER BY c.SEQUENCE_NO");
			

			int i = 0;

			PreparedStatement pstmt = con.prepareStatement(strBuffer.toString());
			
			if(!BTSLUtil.isNullString(requestVO.getParentUserID())) {
				pstmt.setString(++i, requestVO.getParentUserID());
				pstmt.setString(++i, requestVO.getParentUserID());
			}
			
			if(requestVO.getLoginID()!=null && !requestVO.getLoginID().equalsIgnoreCase(PretupsI.ALL)) {
				pstmt.setString(++i, requestVO.getLoginID());
				
			}
			
			if(requestVO.getMsisdn()!=null && !requestVO.getMsisdn().equalsIgnoreCase(PretupsI.ALL)) {
				pstmt.setString(++i, requestVO.getMsisdn());
			}
			
			if (!requestVO.getUserCategory().equalsIgnoreCase(PretupsI.ALL)) {
				pstmt.setString(++i, requestVO.getUserCategory());
			}
			if (!requestVO.getDomain().equalsIgnoreCase(PretupsI.ALL)) {
				pstmt.setString(++i, requestVO.getDomain());

			}
			

			if (!requestVO.getGeography().equalsIgnoreCase(PretupsI.ALL)|| (requestVO.getGeography()!=null && requestVO.getGeography().trim().length()>0  ) ) {
				pstmt.setString(++i, requestVO.getGeography());
				pstmt.setString(++i, requestVO.getGeography());

			}
			
			if (userName!=null && !BTSLUtil.isNullorEmpty(userName) && !userName.equalsIgnoreCase(PretupsI.ALL) ) {
				pstmt.setString(++i, userName);
			}
			
			if(!BTSLUtil.isNullString(requestVO.getOwnerUserID())  && !requestVO.getOwnerUserID().equals(PretupsI.ALL) ) {
				pstmt.setString(++i, requestVO.getOwnerUserID());
			}

			log.debug(methodName, strBuffer.toString());

			return pstmt;
	}
	@Override
	public PreparedStatement checkChannelUserUnderParent(Connection con, String channelUserLoginID, String parentUserID)
			throws SQLException {
		StringBuilder strBuff = new StringBuilder(" ");
		
		strBuff.append("  with recursive usr as(SELECT ");
		strBuff.append("  U.USER_NAME,U.USER_ID,U.MSISDN,U.category_code,u.LOGIN_ID,U.STATUS,u.parent_id,u.owner_id,c.sequence_no FROM USERS U,categories c ");
		strBuff.append(" WHERE  u.USER_TYPE='CHANNEL'AND  U.USER_ID = ?  and U.category_code =c.category_code ");  
		strBuff.append("  union all SELECT U1.USER_NAME,U1.USER_ID,U1.MSISDN,u1.category_code,U1.LOGIN_ID,U1.STATUS,U1.parent_id,U1.owner_id,c1.sequence_no ");   
		strBuff.append("          FROM USERS U1 join usr ur on ur.user_id = U1.parent_id  "); 
		strBuff.append("                  join   categories c1 on c1.category_code =U1.category_code ) "); 
		strBuff.append("    select   USER_NAME, USER_ID,MSISDN,category_code, LOGIN_ID, STATUS , parent_id ,owner_id,sequence_no from usr where status ='Y' and upper(login_id) = ?  order by SEQUENCE_NO ");
		
		
		PreparedStatement pstmtSelect = con.prepareStatement(strBuff.toString());
		 int i = 0;
        ++i;
        pstmtSelect.setString(i, parentUserID);
        ++i;
        pstmtSelect.setString(i, channelUserLoginID.toUpperCase());
        return pstmtSelect;

		    		
		
	}
	@Override
	public String fetchPagesUIRoles(String pageType, String tabName , boolean groupRole) {
		String sqlSelect = null;
		
		if(pageType == null) {
		sqlSelect = "SELECT distinct PAGES.page_code,\r\n" + 
				"  PAGES.module_code,\r\n" + 
				"  PAGES.page_url,\r\n" + 
				"  PAGES.menu_name,\r\n" + 
				"  PAGES.PAGE_URL,\r\n" + 
				"  PAGES.IMAGE,\r\n" + 
				"  PAGES.PARENT_PAGE_CODE,\r\n" + 
				"  PAGES.SHOW_MENU,\r\n" + 
				"  PAGES.sequence_no,\r\n" + 
				"  --MODULE.module_name,\r\n" + 
				"  PAGES.menu_level,\r\n" + 
				"  coalesce (ROLES.from_hour :: INTEGER, 0) from_hour,\r\n" + 
				"  coalesce (ROLES.to_hour :: INTEGER, 24) to_hour,\r\n" + 
				"  --MODULE.sequence_no MSEQ,\r\n" + 
				//"  ROLES.role_code ,\r\n" + 
				"  ROLES.ACCESS_TYPE, \r\n" + 
				" PAGES.APP_NAME "+
				"FROM CATEGORY_ROLES,\r\n" + 
				"  USER_ROLES,\r\n" + 
				"  ROLES ,\r\n" + 
				"  PAGE_UI_ROLES PAGE_ROLES,\r\n" + 
				"  PAGES_UI PAGES,\r\n" + 
				" GROUP_ROLES \r\n"+
				"  --,MODULES MODULE\r\n" + 
				"WHERE \r\n" + 
				"USER_ROLES.user_id        =? " + 
				"AND ((PAGES.DOMAIN_TYPE IS NULL) OR PAGES.DOMAIN_TYPE = ?)\r\n" +
				"AND ((PAGES.CATEGORY_CODE IS NULL) OR PAGES.CATEGORY_CODE = ?)\r\n" + 
				(groupRole ? "AND USER_ROLES.role_code=GROUP_ROLES.group_role_code\r\n":"AND USER_ROLES.role_code=ROLES.role_code\r\n") +
				"AND ROLES.domain_type           =? " +
				(groupRole ? "AND ROLES.role_code =GROUP_ROLES.role_code \r\n":"")+
				"AND CATEGORY_ROLES.category_code= ? " + 
				(groupRole? "AND CATEGORY_ROLES.role_code=GROUP_ROLES.group_role_code \r\n" : "AND CATEGORY_ROLES.role_code=USER_ROLES.role_code \r\n") +
				"AND ROLES.role_code             =PAGE_ROLES.role_code\r\n" + 
				"AND (ROLES.status              IS NULL\r\n" + 
				"OR ROLES.status                 ='Y')\r\n" + 
				"AND PAGE_ROLES.page_code        =PAGES.page_code\r\n" + 
				"--AND PAGES.module_code           =MODULE.module_code\r\n" + 
				"AND (ROLES.role_type           IS NULL\r\n" + 
				"OR ROLES.role_type              =(case 'ALL' when 'ALL' then ROLES.role_type else 'ALL' end))\r\n" + 
				"AND ROLES.gateway_types LIKE '%WEB%'\r\n" + 
				"AND PAGES.PAGE_TYPE IS NULL ";
		
		if(tabName == null) {
			sqlSelect = sqlSelect +" AND PAGES.APP_NAME IS NULL ";
		}else if(tabName.equalsIgnoreCase("ALL")){
			
		}else {
			sqlSelect = sqlSelect +"AND PAGES.APP_NAME ='"+tabName+"'  ";
		}
		
		
		
		sqlSelect = sqlSelect +" ORDER BY \r\n" + 
				"--MODULE.application_id,\r\n" + 
				"  --MODULE.sequence_no,\r\n" + 
				"  PAGES.sequence_no" ;
		}else {

			sqlSelect = "SELECT distinct PAGES.page_code,\r\n" + 
					"  PAGES.module_code,\r\n" + 
					"  PAGES.page_url,\r\n" + 
					"  PAGES.menu_name,\r\n" + 
					"  PAGES.PAGE_URL,\r\n" + 
					"  PAGES.IMAGE,\r\n" + 
					"  PAGES.PARENT_PAGE_CODE,\r\n" + 
					"  PAGES.SHOW_MENU,\r\n" + 
					"  PAGES.sequence_no,\r\n" + 
					"  --MODULE.module_name,\r\n" + 
					"  PAGES.menu_level,\r\n" + 
					"  coalesce (ROLES.from_hour :: INTEGER, 0) from_hour,\r\n" + 
					"  coalesce (ROLES.to_hour :: INTEGER, 24) to_hour,\r\n" + 
					"  --MODULE.sequence_no MSEQ,\r\n" + 
					//"  ROLES.role_code ,\r\n" + 
					"  ROLES.ACCESS_TYPE, \r\n" + 
					" PAGES.APP_NAME "+
					"FROM CATEGORY_ROLES,\r\n" + 
					"  USER_ROLES,\r\n" + 
					"  ROLES ,\r\n" + 
					"  PAGE_UI_ROLES PAGE_ROLES,\r\n" + 
					"  PAGES_UI PAGES,\r\n" +
					" GROUP_ROLES \r\n"+
					"  --,MODULES MODULE\r\n" + 
					"WHERE \r\n" + 
					"USER_ROLES.user_id        =? " + 
					"AND ((PAGES.DOMAIN_TYPE IS NULL) OR PAGES.DOMAIN_TYPE = ?)\r\n" +
					"AND ((PAGES.CATEGORY_CODE IS NULL) OR PAGES.CATEGORY_CODE = ?)\r\n" + 
					(groupRole ? "AND USER_ROLES.role_code=GROUP_ROLES.group_role_code\r\n":"AND USER_ROLES.role_code=ROLES.role_code\r\n") +
					"AND ROLES.domain_type           =? " + 
					"AND CATEGORY_ROLES.category_code= ? " + 
					(groupRole? "AND CATEGORY_ROLES.role_code=GROUP_ROLES.group_role_code \r\n" : "AND CATEGORY_ROLES.role_code=USER_ROLES.role_code \r\n") +
					"AND ROLES.role_code             =PAGE_ROLES.role_code\r\n" + 
					"AND (ROLES.status              IS NULL\r\n" + 
					"OR ROLES.status                 ='Y')\r\n" +
					(groupRole ? "AND ROLES.role_code =GROUP_ROLES.role_code \r\n":"")+
					"AND PAGE_ROLES.page_code        =PAGES.page_code\r\n" + 
					"--AND PAGES.module_code           =MODULE.module_code\r\n" + 
					"AND (ROLES.role_type           IS NULL\r\n" + 
					"OR ROLES.role_type              =(case 'ALL' when 'ALL' then ROLES.role_type else 'ALL' end))\r\n" + 
					"AND ROLES.gateway_types LIKE '%WEB%'\r\n" + 
					"AND PAGES.PAGE_TYPE = ? ";
			
			if(tabName == null) {
				sqlSelect = sqlSelect +" AND PAGES.APP_NAME IS NULL ";
			}else if(tabName.equalsIgnoreCase("ALL")){
				
			}else {
				sqlSelect = sqlSelect +"AND PAGES.APP_NAME ='"+tabName+"'  ";
			}
			
			
			sqlSelect = sqlSelect +" ORDER BY \r\n" + 
					"--MODULE.application_id,\r\n" + 
					"  --MODULE.sequence_no,\r\n" + 
					"  PAGES.sequence_no" ;
		}
		return sqlSelect;
	}
	
	
	@Override
	public String fetchPagesUIRolesFixed(String pageType, String tabName , boolean groupRole) {
		String sqlSelect = null;
		
		if(pageType == null) {
		sqlSelect = "SELECT distinct PAGES.page_code,\r\n" + 
				"  PAGES.module_code,\r\n" + 
				"  PAGES.page_url,\r\n" + 
				"  PAGES.menu_name,\r\n" + 
				"  PAGES.PAGE_URL,\r\n" + 
				"  PAGES.IMAGE,\r\n" + 
				"  PAGES.PARENT_PAGE_CODE,\r\n" + 
				"  PAGES.SHOW_MENU,\r\n" + 
				"  PAGES.sequence_no,\r\n" + 
				"  --MODULE.module_name,\r\n" + 
				"  PAGES.menu_level,\r\n" + 
				"  coalesce (ROLES.from_hour :: INTEGER, 0) from_hour,\r\n" + 
				"  coalesce (ROLES.to_hour :: INTEGER, 24) to_hour,\r\n" + 
				"  --MODULE.sequence_no MSEQ,\r\n" + 
				//"  ROLES.role_code ,\r\n" + 
				"  ROLES.ACCESS_TYPE, \r\n" + 
				" PAGES.APP_NAME "+
				"FROM CATEGORY_ROLES,\r\n" + 
//				"  USER_ROLES,\r\n" +
				"  ROLES ,\r\n" + 
				"  PAGE_UI_ROLES PAGE_ROLES,\r\n" + 
				"  PAGES_UI PAGES\r\n" + 
				"  --,MODULES MODULE\r\n" + 
				"WHERE \r\n" + 
				"\r\n" + 
				"CATEGORY_ROLES.category_code= ? \r\n" + 
				"AND CATEGORY_ROLES.role_code    =ROLES.role_code\r\n" + 
				"AND ROLES.domain_type           = ? \r\n" + 
				"AND ROLES.role_code             =PAGE_ROLES.role_code\r\n" +
//				"AND ROLES.role_code =GROUP_ROLES.role_code\r\n" +
//				(groupRole ? "AND USER_ROLES.role_code=GROUP_ROLES.group_role_code\r\n":"AND USER_ROLES.role_code=ROLES.role_code\r\n") +
//				(groupRole? "AND CATEGORY_ROLES.role_code=GROUP_ROLES.group_role_code \r\n" : "AND CATEGORY_ROLES.role_code=USER_ROLES.role_code \r\n") +
				(groupRole ? "AND ROLES.role_code =GROUP_ROLES.role_code \r\n":"")+
				"AND (ROLES.status              IS NULL\r\n" + 
				"OR ROLES.status                 ='Y')\r\n" + 
				"AND PAGE_ROLES.page_code        =PAGES.page_code\r\n" + 
				"AND (ROLES.role_type           IS NULL\r\n" + 				
				"OR ROLES.role_type              =(case 'ALL' when 'ALL' then ROLES.role_type else 'ALL' end))\r\n" +
				"AND ROLES.gateway_types LIKE '%WEB%'\r\n" + 
				
				"AND ((PAGES.DOMAIN_TYPE IS NULL) OR PAGES.DOMAIN_TYPE = ?)\r\n" + 
				"AND ((PAGES.CATEGORY_CODE IS NULL) OR PAGES.CATEGORY_CODE = ?)\r\n" + 
				"AND PAGES.PAGE_TYPE IS NULL ";
		if(tabName == null) {
			sqlSelect = sqlSelect +" AND PAGES.APP_NAME IS NULL ";
		}else if(tabName.equalsIgnoreCase("ALL")){
			
		}else {
			sqlSelect = sqlSelect +"AND PAGES.APP_NAME ='"+tabName+"'  ";
		}
		
		sqlSelect = sqlSelect +"" +
				"\r\n" + 
				"ORDER BY \r\n" + 
				"  PAGES.sequence_no" ;
		}else {

			sqlSelect = "SELECT distinct PAGES.page_code,\r\n" + 
					"  PAGES.module_code,\r\n" + 
					"  PAGES.page_url,\r\n" + 
					"  PAGES.menu_name,\r\n" + 
					"  PAGES.PAGE_URL,\r\n" + 
					"  PAGES.IMAGE,\r\n" + 
					"  PAGES.PARENT_PAGE_CODE,\r\n" + 
					"  PAGES.SHOW_MENU,\r\n" + 
					"  PAGES.sequence_no,\r\n" + 
					"  --MODULE.module_name,\r\n" + 
					"  PAGES.menu_level,\r\n" + 
					"  coalesce (ROLES.from_hour :: INTEGER, 0) from_hour,\r\n" + 
					"  coalesce (ROLES.to_hour :: INTEGER, 24) to_hour,\r\n" + 
					"  --MODULE.sequence_no MSEQ,\r\n" + 
					//"  ROLES.role_code ,\r\n" + 
					"  ROLES.ACCESS_TYPE, \r\n" +
					" PAGES.APP_NAME "+
					"FROM CATEGORY_ROLES,\r\n" + 
//					"  USER_ROLES,\r\n" +
					"  ROLES ,\r\n" + 
					"  PAGE_UI_ROLES PAGE_ROLES,\r\n" + 
					"  PAGES_UI PAGES\r\n" + 
					"  --,MODULES MODULE\r\n" +
					"WHERE \r\n" +
					"\r\n" +
					"CATEGORY_ROLES.category_code= ? \r\n" +
					"AND CATEGORY_ROLES.role_code    =ROLES.role_code\r\n" +
					"AND ROLES.domain_type           = ? \r\n" +
					"AND ROLES.role_code             =PAGE_ROLES.role_code\r\n" +
//				"AND ROLES.role_code =GROUP_ROLES.role_code\r\n" +
//					(groupRole ? "AND USER_ROLES.role_code=GROUP_ROLES.group_role_code\r\n":"AND USER_ROLES.role_code=ROLES.role_code\r\n") +
//					(groupRole? "AND CATEGORY_ROLES.role_code=GROUP_ROLES.group_role_code \r\n" : "AND CATEGORY_ROLES.role_code=USER_ROLES.role_code \r\n") +
					(groupRole ? "AND ROLES.role_code =GROUP_ROLES.role_code \r\n":"")+
					"AND (ROLES.status              IS NULL\r\n" +
					"OR ROLES.status                 ='Y')\r\n" +
					"AND PAGE_ROLES.page_code        =PAGES.page_code\r\n" +
					"AND (ROLES.role_type           IS NULL\r\n" +
					"OR ROLES.role_type              =(case 'ALL' when 'ALL' then ROLES.role_type else 'ALL' end))\r\n" +
					"AND ROLES.gateway_types LIKE '%WEB%'\r\n" +

					"AND ((PAGES.DOMAIN_TYPE IS NULL) OR PAGES.DOMAIN_TYPE = ?)\r\n" +
					"AND ((PAGES.CATEGORY_CODE IS NULL) OR PAGES.CATEGORY_CODE = ?)\r\n" +
					"AND PAGES.PAGE_TYPE = ? ";
			if(tabName == null) {
				sqlSelect = sqlSelect +" AND PAGES.APP_NAME IS NULL ";
			}else if(tabName.equalsIgnoreCase("ALL")){
				
			}else {
				sqlSelect = sqlSelect +"AND PAGES.APP_NAME ='"+tabName+"'  ";
			}
			
			sqlSelect = sqlSelect +"" +
					"\r\n" + 
					"ORDER BY \r\n" + 
					"  PAGES.sequence_no" ;

		}
		return sqlSelect;
	}
	@Override
	public String loadUsersDetailsQryFromLoginID() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
        strBuff.append("USR.login_id,USR.password passwd,USR.category_code usr_category_code,USR.parent_id,");
        strBuff.append("USR.owner_id,USR.allowed_ip,USR.allowed_days,USR.company,USR.fax,USR.firstname,USR.lastname, ");// firstname,lastname,company,fax
        strBuff.append("USR.from_time,USR.to_time,USR.employee_code,");
        strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,");
        strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
        strBuff.append("USR.created_by,USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
        strBuff.append("USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
        strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
        strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date, ");
        strBuff.append("USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name,");
        strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type, ");
        strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, ");
        strBuff.append("USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed,");
        strBuff.append("PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn, ");
        strBuff.append("PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn, ");
        strBuff.append("ONR_CAT.category_name owner_cat ");
        strBuff.append("FROM users USR left outer join (users PRNT_USR left join categories  PRNT_CAT on PRNT_CAT.category_code=PRNT_USR.category_code )on PRNT_USR.user_id= USR.parent_id, users ONR_USR,categories USR_CAT, ");
        strBuff.append("categories ONR_CAT ");
        strBuff.append("WHERE USR.status <> 'N' AND USR.status <> 'C' AND USR.login_id=? ");
        strBuff.append("AND USR.owner_id=ONR_USR.user_id ");
        strBuff.append("AND USR.category_code=USR_CAT.category_code ");
        strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
        return strBuff.toString();
	}
	@Override
	public String loadApprovalListbyCreater(ApplistReqVO applistReqVO) {
		StringBuilder strBuff = new StringBuilder("SELECT u.USER_ID ,u.login_id,u.USER_NAME,up.msisdn,crby.user_name  CREATEDBY ,u.status,L.LOOKUP_NAME as statusDesc  FROM users u ");
	    strBuff.append(" LEFT OUTER JOIN Lookups L ON L.lookup_type = 'URTYP' AND L.lookup_code = u.STATUS ");
	    strBuff.append(",USER_PHONES up,users crby ");
	    if(PretupsI.ADVANCED_TAB.equals(applistReqVO.getReqTab())){
	    	strBuff.append(" , USER_GEOGRAPHIES ug ");	
	    }
	    
	    strBuff.append( " WHERE u.user_id =up.user_id AND up.PRIMARY_NUMBER ='Y' AND crby.user_id =u.CREATED_BY  ");
	    
	    if(PretupsI.ADVANCED_TAB.equals(applistReqVO.getReqTab())){
	    	strBuff.append(" and  ug.user_id =u.user_id ");	
	    }
	    
	    if(PretupsI.CHANNEL_USER_APPROVE1.equals(applistReqVO.getApprovalLevel())) {
	    	strBuff.append(" AND (u.LEVEL1_APPROVED_BY is NULL AND u.LEVEL1_APPROVED_ON  is NULL) AND (u.LEVEL2_APPROVED_BY is  NULL AND u.LEVEL2_APPROVED_ON  is NULL )");	
	    }else {
	    	strBuff.append(" AND (u.LEVEL1_APPROVED_BY is not NULL AND u.LEVEL1_APPROVED_ON  is  not NULL) AND (u.LEVEL2_APPROVED_BY is  NULL AND u.LEVEL2_APPROVED_ON  is  NULL )");    	
	    }
	    
	    if(PretupsI.LOGIN_ID_TAB.equals(applistReqVO.getReqTab())){
	    	
	    	strBuff.append(" AND u.login_id =? ");
	    }else if(PretupsI.MSISDN_TAB.equals(applistReqVO.getReqTab())){
	    	
	    	strBuff.append(" AND up.msisdn =? ");
	    }else { //Advanced Tab
	    	
	   // 	strBuff.append(" AND ud.domain_code = ( CASE ? WHEN 'ALL' THEN ud.domain_code  ELSE ? END) ");
	    	strBuff.append(" AND u.category_code = ( CASE ? WHEN 'ALL' THEN u.category_code  ELSE ? END) ");
	    	strBuff.append(" AND ug.grph_domain_code in  ");
	    	strBuff.append(" ( WITH recursive q AS "); 
	    	strBuff.append("   (  SELECT grph_domain_code, status "); 
	    	strBuff.append("    FROM   geographical_domains "); 
	    	strBuff.append("    WHERE  grph_domain_code IN ( "); 
	    	strBuff.append("  SELECT grph_domain_code  FROM   user_geographies ug1 WHERE  ug1.grph_domain_code = (  CASE ? WHEN 'ALL' THEN ug1.grph_domain_code  ELSE ?   END ) "); 
	    	strBuff.append("  AND    ug1.user_id = ?   ) ");
	    	strBuff.append("         UNION ALL "); 
	    	strBuff.append("          SELECT m.grph_domain_code,  m.status  FROM   geographical_domains m   JOIN   q "); 
	    	strBuff.append("             ON     q.grph_domain_code = m.parent_grph_domain_code ) SELECT   q.grph_domain_code  FROM     q    ) ");
	    	
	    }
	    strBuff.append(" AND u.status =? ");
	    
	    strBuff.append(" AND u.CREATED_BY =?  order by  u.created_on desc ");
	     
	    String sqlSelect = strBuff.toString();
	    return sqlSelect;

	}
	
	
	
	
	
	
	/**
	   * @author harshita.bajaj
	   * @param con
	   * @param userDomain
	   * @param userCategoryCode
	   * @param userGeography
	   * @param userId
	   * @param status
	   * @param selfAllowed
	   * @return
	   * @throws SQLException
	   */
	
	public PreparedStatement getChannelUsersListQry2(Connection con, String userDomain, String userCategoryCode,
			String userGeography, String userId, String status, boolean selfAllowed,boolean onlyChannelUser) throws SQLException {

		StringBuilder strBuffer = new StringBuilder(" ");

		strBuffer.append("with recursive usr as(");
		strBuffer.append(
				" SELECT DISTINCT u.user_id,u.CREATED_ON, u.LOGIN_ID, u.USER_NAME,u.MSISDN ,u.user_type,u.STATUS,u2.USER_NAME AS");
		strBuffer.append(
				" owner_name,u.OWNER_ID,c.DOMAIN_CODE,d.DOMAIN_NAME,c.category_name,c.category_code,ub.BALANCE,p.PRODUCT_NAME,ub.product_code,u3.USER_NAME AS parent_name,u.PARENT_ID,ug.GRPH_DOMAIN_CODE,gd.GRPH_DOMAIN_NAME,cu.COMM_PROFILE_SET_ID,cu.TRANSFER_PROFILE_ID,cu.USER_GRADE,u.CONTACT_PERSON,cp.COMM_PROFILE_SET_NAME,tp.PROFILE_NAME,u.MODIFIED_ON,u6.USER_NAME AS MODIFIED_BY,ub.last_transfer_on");
		strBuffer.append(" FROM USERS u");
		strBuffer.append(" left outer JOIN USERS u2");
		strBuffer.append(" ON u2.USER_ID=u.OWNER_ID");
		strBuffer.append(" left outer JOIN USERS u6");
		strBuffer.append(" ON u.modified_by=u6.user_id");
		strBuffer.append(" left outer JOIN categories c");
		strBuffer.append(" ON c.category_code=u.category_code");
		strBuffer.append(" left outer JOIN domains d");
		strBuffer.append(" ON c.DOMAIN_CODE=d.DOMAIN_CODE");
		strBuffer.append(" left outer JOIN user_balances ub");
		strBuffer.append(" on u.user_id=ub.user_id");
		strBuffer.append(" left outer JOIN users u3");
		strBuffer.append(" on u.PARENT_ID=u3.user_id");
		strBuffer.append(" left outer JOIN USER_GEOGRAPHIES ug");
		strBuffer.append(" on u.user_id=ug.user_id");
		strBuffer.append(" left outer JOIN GEOGRAPHICAL_DOMAINS gd");
		strBuffer.append(" on gd.GRPH_DOMAIN_CODE=ug.GRPH_DOMAIN_CODE");
		strBuffer.append(" left outer JOIN CHANNEL_USERS cu ON u.USER_ID=cu.USER_ID");
		strBuffer.append(" left outer JOIN COMMISSION_PROFILE_SET cp ON cp.COMM_PROFILE_SET_ID=cu.COMM_PROFILE_SET_ID");
		strBuffer.append(" left outer JOIN TRANSFER_PROFILE tp ON tp.PROFILE_ID=cu.TRANSFER_PROFILE_ID");
		strBuffer.append(" left outer JOIN PRODUCTS p ON p.PRODUCT_CODE = ub.PRODUCT_CODE");
		strBuffer.append(" WHERE u.USER_TYPE='CHANNEL'");
		strBuffer.append(" and u.user_id=?");
		strBuffer.append(
				" union all SELECT DISTINCT u4.user_id,u4.CREATED_ON, u4.LOGIN_ID, u4.USER_NAME,u4.MSISDN ,u4.USER_TYPE, u4.STATUS,u5.USER_NAME AS");
		strBuffer.append(
				" owner_name,u4.OWNER_ID,c1.DOMAIN_CODE,d1.DOMAIN_NAME,c1.category_name,c1.category_code,ub1.BALANCE,p1.PRODUCT_NAME,ub1.product_code,u6.USER_NAME AS parent_name,u4.PARENT_ID,ug1.GRPH_DOMAIN_CODE,gd1.GRPH_DOMAIN_NAME,cu1.COMM_PROFILE_SET_ID,cu1.TRANSFER_PROFILE_ID,cu1.USER_GRADE,u4.CONTACT_PERSON,cp1.COMM_PROFILE_SET_NAME,tp1.PROFILE_NAME,u4.MODIFIED_ON,u7.USER_NAME AS MODIFIED_BY,ub1.last_transfer_on");
		strBuffer.append(" FROM USERS u4");
		strBuffer.append(" left outer JOIN USERS u5");
		strBuffer.append(" ON u5.USER_ID=u4.OWNER_ID");
		strBuffer.append(" left outer JOIN USERS u7");
		strBuffer.append(" ON u4.modified_by=u7.user_id");
		strBuffer.append(" left outer JOIN categories c1");
		strBuffer.append(" ON c1.category_code=u4.category_code");
		strBuffer.append(" left outer JOIN domains d1");
		strBuffer.append(" ON c1.DOMAIN_CODE=d1.DOMAIN_CODE");
		strBuffer.append(" left outer JOIN user_balances ub1");
		strBuffer.append(" on u4.user_id=ub1.user_id");
		strBuffer.append(" left outer JOIN users u6");
		strBuffer.append(" on u4.PARENT_ID=u6.user_id");
		strBuffer.append(" left outer JOIN USER_GEOGRAPHIES ug1");
		strBuffer.append(" on u4.user_id=ug1.user_id");
		strBuffer.append(" left outer JOIN GEOGRAPHICAL_DOMAINS gd1");
		strBuffer.append(" on gd1.GRPH_DOMAIN_CODE=ug1.GRPH_DOMAIN_CODE");
		strBuffer.append(" left outer JOIN CHANNEL_USERS cu1 ON u4.USER_ID=cu1.USER_ID");
		strBuffer.append(
				" left outer JOIN COMMISSION_PROFILE_SET cp1 ON cp1.COMM_PROFILE_SET_ID=cu1.COMM_PROFILE_SET_ID");
		strBuffer.append(" left outer JOIN TRANSFER_PROFILE tp1 ON tp1.PROFILE_ID=cu1.TRANSFER_PROFILE_ID");
		strBuffer.append(" left outer JOIN PRODUCTS p1 ON p1.PRODUCT_CODE = ub1.PRODUCT_CODE");
		strBuffer.append(
				" join usr ur on ur.user_id = u4.parent_id )  SELECT user_id,CREATED_ON, LOGIN_ID, USER_NAME,MSISDN,USER_TYPE,STATUS,owner_name,OWNER_ID,DOMAIN_CODE,DOMAIN_NAME,category_name,category_code,BALANCE,product_code,PRODUCT_NAME,parent_name,PARENT_ID,GRPH_DOMAIN_CODE,GRPH_DOMAIN_NAME,MODIFIED_BY,MODIFIED_ON,CREATED_ON,COMM_PROFILE_SET_ID,TRANSFER_PROFILE_ID,USER_GRADE,CONTACT_PERSON,COMM_PROFILE_SET_NAME,PROFILE_NAME,last_transfer_on from usr");

		boolean whereAdded = false;
		if (!userCategoryCode.equalsIgnoreCase("ALL")) {

			if (!whereAdded) {
				strBuffer.append(" where category_code=?");
				whereAdded = true;
			} else {

				strBuffer.append(" AND category_code=?");
			}

		}

		if (!userDomain.equalsIgnoreCase("ALL")) {

			if (!whereAdded) {
				strBuffer.append(" where DOMAIN_CODE=?");
				whereAdded = true;
			} else {

				strBuffer.append(" AND DOMAIN_CODE=?");
			}

		}

		if (!userGeography.equalsIgnoreCase("ALL")) {
			if (!whereAdded) {
				strBuffer.append(" where GRPH_DOMAIN_CODE=?");
				whereAdded = true;
			} else {

				strBuffer.append(" AND GRPH_DOMAIN_CODE=?");
			}

		}
		if (!status.equalsIgnoreCase("ALL")) {
			if (!whereAdded) {
				strBuffer.append(" where status=?");
				whereAdded = true;
			} else {

				strBuffer.append(" and status=?");
			}

		}
		
		if(onlyChannelUser) {
			if (!whereAdded) {
				strBuffer.append(" where user_type='CHANNEL' ");
				whereAdded = true;
			} else {
				strBuffer.append(" and user_type='CHANNEL' ");
			}

		}
		
	 
		 
		strBuffer.append("  order by created_on desc");
		

		log.debug("My QUery coming is", strBuffer.toString());

		PreparedStatement pstmt = con.prepareStatement(strBuffer.toString());

		int i = 0;
		++i;
		pstmt.setString(i, userId);
		if (!userCategoryCode.equalsIgnoreCase("ALL")) {

			pstmt.setString(++i, userCategoryCode);

		}
		if (!userDomain.equalsIgnoreCase("ALL")) {
			pstmt.setString(++i, userDomain);

		}
		if (!userGeography.equalsIgnoreCase("ALL")) {
			pstmt.setString(++i, userGeography);

		}

		if (!status.equalsIgnoreCase("ALL")) {
			pstmt.setString(++i, status);

		}

		return pstmt;

	}

	public String loadApprovalListbyCreaterAdvance(ApplistReqVO applistReqVO) {
		StringBuilder strBuff = new StringBuilder("SELECT u.USER_ID ,u.login_id,u.USER_NAME,up.msisdn,crby.user_name  CREATEDBY ,u.status,L.LOOKUP_NAME as statusDesc  FROM users u ");
		strBuff.append(" LEFT OUTER JOIN Lookups L ON L.lookup_type = 'URTYP' AND L.lookup_code = u.STATUS ");
		strBuff.append(",USER_PHONES up,users crby ");
		strBuff.append(" , USER_GEOGRAPHIES ug ");
		strBuff.append( " WHERE u.user_id =up.user_id AND u.USER_TYPE=? AND u.CREATION_TYPE AND up.PRIMARY_NUMBER ='Y' AND crby.user_id =u.CREATED_BY  ");
		strBuff.append(" and  ug.user_id =u.user_id ");
		strBuff.append(" AND u.category_code = ( CASE ? WHEN 'ALL' THEN u.category_code  ELSE ? END) ");
		strBuff.append(" AND ug.grph_domain_code in  ");
		strBuff.append(" ( WITH recursive q AS ");
		strBuff.append("   (  SELECT grph_domain_code, status ");
		strBuff.append("    FROM   geographical_domains ");
		strBuff.append("    WHERE  grph_domain_code IN ( ");
		strBuff.append("  SELECT grph_domain_code  FROM   user_geographies ug1 WHERE  ug1.grph_domain_code = (  CASE ? WHEN 'ALL' THEN ug1.grph_domain_code  ELSE ?   END ) ");
		strBuff.append("     ) ");
		strBuff.append("         UNION ALL ");
		strBuff.append("          SELECT m.grph_domain_code,  m.status  FROM   geographical_domains m   JOIN   q ");
		strBuff.append("             ON     q.grph_domain_code = m.parent_grph_domain_code ) SELECT   q.grph_domain_code  FROM     q    ) ");


		strBuff.append(" AND u.status =? ");
		String sqlSelect = strBuff.toString();
		return sqlSelect;

	}

	public String loadApprovalListbyCreaterMob(ApplistReqVO applistReqVO) {
		StringBuilder strBuff = new StringBuilder("SELECT USR.USER_ID ,USR.login_id,USR.USER_NAME,USR_PHONE.msisdn,USR_CRBY.user_name CREATEDBY ,USR.status,L.LOOKUP_NAME as statusDesc  FROM user_phones USR_PHONE, users USR ");
		strBuff.append(" left join users PRNT_USR on USR.parent_id = PRNT_USR.user_id ");
		strBuff.append(" left join users MOD_USR on MOD_USR.user_id = USR.modified_by ");
		strBuff.append(" left join categories PRNT_CAT on PRNT_USR.category_code = PRNT_CAT.category_code ");
		strBuff.append(" left join users USR_CRBY on USR_CRBY.user_id = USR.created_by ,users ONR_USR,categories USR_CAT,categories ONR_CAT,lookups l,user_geographies UG,geographical_domains GD,domains D ");
		strBuff.append( " WHERE USR_PHONE.user_id = USR.user_id ");
		if(PretupsI.LOGIN_ID_TAB.equals(applistReqVO.getReqTab())){

			strBuff.append(" AND USR.login_id =? ");
		}else if(PretupsI.MSISDN_TAB.equals(applistReqVO.getReqTab())){

			strBuff.append(" AND USR_PHONE.msisdn =? ");
		}
		strBuff.append(" AND USR.owner_id = ONR_USR.user_id AND USR.category_code = USR_CAT.category_code ");
		strBuff.append(" AND ONR_CAT.category_code = ONR_USR.category_code ");
		strBuff.append(" AND USR.status = l.lookup_code AND l.lookup_type = 'URTYP' AND USR.user_id = UG.user_id ");
		strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code AND USR_CAT.domain_code = D.domain_code ");
		strBuff.append(" AND USR.status =? AND USR.creation_type=?");
		String sqlSelect = strBuff.toString();
		return sqlSelect;

	}

	public PreparedStatement getChannelUsersListQryCCE(Connection con, String userDomain, String userCategoryCode,
													   String userGeography, String userId, String status, boolean selfAllowed,boolean onlyChannelUser) throws SQLException {

		StringBuilder strBuffer = new StringBuilder(" ");

		strBuffer.append("SELECT\n" +
				"DISTINCT u.USER_ID,\n" +
				"ub.LAST_TRANSFER_ON,\n" +
				"u.MODIFIED_ON,\n" +
				"u.CREATED_ON,\n" +
				"u.user_type,\n" +
				"u.LOGIN_ID,\n" +
				"u.USER_NAME,\n" +
				"u.MSISDN ,\n" +
				"u.STATUS,\n" +
				"u2.USER_NAME AS owner_name,\n" +
				"u4.USER_NAME AS MODIFIED_BY,\n" +
				"u.OWNER_ID,\n" +
				"c.DOMAIN_CODE,\n" +
				"d.DOMAIN_NAME,\n" +
				"c.category_name,\n" +
				"c.SEQUENCE_NO,\n" +
				"c.category_code,\n" +
				"ub.BALANCE,\n" +
				"p.PRODUCT_NAME,\n" +
				"ub.product_code,\n" +
				"u3.USER_NAME AS parent_name,\n" +
				"u.PARENT_ID,\n" +
				"ug.GRPH_DOMAIN_CODE,\n" +
				"gd.GRPH_DOMAIN_NAME,\n" +
				"cu.COMM_PROFILE_SET_ID,\n" +
				"cu.TRANSFER_PROFILE_ID,\n" +
				"cu.USER_GRADE,\n" +
				"u.CONTACT_PERSON,\n" +
				"cp.COMM_PROFILE_SET_NAME,\n" +
				"tp.PROFILE_NAME\n" +
				"FROM\n" +
				"USERS u\n" +
				"INNER JOIN USERS u2 ON\n" +
				"u2.USER_ID = u.OWNER_ID\n" +
				"INNER JOIN USERS u4 ON\n" +
				"u.MODIFIED_BY = u4.USER_ID\n" +
				"INNER JOIN categories c ON\n" +
				"c.category_code = u.category_code\n" +
				"INNER JOIN domains d ON\n" +
				"c.DOMAIN_CODE = d.DOMAIN_CODE\n" +
				"LEFT OUTER JOIN user_balances ub ON\n" +
				"u.user_id = ub.user_id\n" +
				"LEFT OUTER JOIN users u3 ON\n" +
				"u.PARENT_ID = u3.user_id\n" +
				"LEFT OUTER JOIN USER_GEOGRAPHIES ug ON\n" +
				"u.user_id = ug.user_id\n" +
				"LEFT OUTER JOIN GEOGRAPHICAL_DOMAINS gd ON\n" +
				"gd.GRPH_DOMAIN_CODE = ug.GRPH_DOMAIN_CODE\n" +
				"LEFT OUTER JOIN CHANNEL_USERS cu ON\n" +
				"u.USER_ID = cu.USER_ID\n" +
				"LEFT OUTER JOIN COMMISSION_PROFILE_SET cp ON\n" +
				"cp.COMM_PROFILE_SET_ID = cu.COMM_PROFILE_SET_ID\n" +
				"LEFT OUTER JOIN TRANSFER_PROFILE tp ON\n" +
				"tp.PROFILE_ID = cu.TRANSFER_PROFILE_ID\n" +
				"LEFT OUTER JOIN PRODUCTS p ON\n" +
				"p.PRODUCT_CODE = ub.PRODUCT_CODE\n" +
				"WHERE\n" +
				"u.USER_TYPE = 'CHANNEL'\n" +
				"AND u2.user_type = 'CHANNEL'\n" +
				"and u.user_id = ?\n" +
				"limit 1;");
		int i = 0;

		PreparedStatement pstmt = con.prepareStatement(strBuffer.toString());

		pstmt.setString(++i, userId);

		return pstmt;
	}


}
