package com.btsl.db.query.oracle;

import com.btsl.login.LoginQry;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;

public class LoginOracleQry implements LoginQry {
	@Override
	public String loadUserDetailsQry(){
			boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
			boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
			boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
			boolean optInOutAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
			String allowdUsrTypCreation = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOWD_USR_TYP_CREATION);
			
			StringBuilder sqlBuffer = new StringBuilder(" SELECT u.user_id, u.user_name, u.network_code,l.network_name,l.report_header_name, u.login_id, u.password, ");
	        sqlBuffer.append(" u.category_code, u.parent_id,u.company,u.fax, u.owner_id, u.msisdn, u.allowed_ip,  u.allowed_days,u.from_time,u.to_time,u.firstname,u.lastname, "); 
	        sqlBuffer.append(" u.last_login_on,u.employee_code,u.status userstatus,u.email,u.created_by,u.created_on,u.modified_by, ");
	        sqlBuffer.append(" u.modified_on,u.pswd_modified_on,  cusers.contact_person,u.contact_no,u.designation,u.division,u.department, ");
	        sqlBuffer.append(" u.msisdn,u.user_type,cusers.in_suspend,cusers.out_suspend,u.address1,u.address2,u.city,u.state,u.country, ");
	        sqlBuffer.append(" u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,u.invalid_password_count,u.password_count_updated_on, u.payment_type,");
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
	        if(channelSosEnable){
	        	 sqlBuffer.append(" cusers.sos_allowed ,cusers.sos_allowed_amount, cusers.sos_threshold_limit, ");
	        }
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
	        
	        if ((PretupsI.YES).equals(allowdUsrTypCreation)) //Added for allowed user type creation from Network Admin
				sqlBuffer.append(", u.ALLOWD_USR_TYP_CREATION ");
	        
			sqlBuffer.append(" FROM users u,networks l,categories cat,channel_users cusers,geographical_domain_types gdt ,domains dm,user_phones up,domain_types dt ");  
			sqlBuffer.append(" WHERE  UPPER(u.login_id)=? AND gdt.grph_domain_type = cat.grph_domain_type ");
	        sqlBuffer.append(" AND u.status <> ? AND u.status <> ? AND U.network_code=L.network_code(+)  AND u.user_id=cusers.user_id(+) ");
	        sqlBuffer.append(" AND cat.category_code=U.category_code AND cat.status <> ? AND dm.domain_code = cat.domain_code ");
	        sqlBuffer.append(" AND u.msisdn = up.msisdn(+) ");
	        sqlBuffer.append(" AND u.user_id=up.user_id(+) ");
	        sqlBuffer.append(" AND dt.domain_type_code = dm.domain_type_code ");
	        return sqlBuffer.toString();
	}
	@Override
	public String loadUserDetailsByMsisdnOrLoginIdQry(String msisdn, String loginId){
		boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
		StringBuilder sqlBuffer = new StringBuilder(" SELECT uowner.user_name owner_name, uparent.user_name parent_name, u.user_id, u.user_name, u.network_code,l.network_name,l.report_header_name, u.login_id, u.password, ");
        sqlBuffer.append(" u.category_code, u.parent_id,u.company,u.fax, u.owner_id, u.msisdn, u.allowed_ip,  u.allowed_days,u.from_time,u.to_time,u.firstname,u.lastname, ");// firstname,lastname,company,fax
                                                                                                                                                                              // added
                                                                                                                                                                              // by
                                                                                                                                                                              // deepika
                                                                                                                                                                              // aggarwal
        sqlBuffer.append(" u.last_login_on,u.employee_code,u.status userstatus,u.email,u.created_by,u.created_on,u.modified_by, ");
        sqlBuffer.append(" u.modified_on,u.pswd_modified_on,  u.contact_person,u.contact_no,u.designation,u.division,u.department, ");
        sqlBuffer.append(" u.msisdn,u.user_type,cusers.in_suspend,cusers.out_suspend,u.address1,u.address2,u.city,u.state,u.country, ");
        sqlBuffer.append(" u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,u.invalid_password_count,u.password_count_updated_on, ");
        sqlBuffer.append(" l.status networkstatus,l.language_1_message,l.language_2_message, cat.category_code,cat.category_name, ");
        sqlBuffer.append(" cat.domain_code,cat.sequence_no,cat.multiple_login_allowed, cat.max_login_count,cat.view_on_network_block, ");
        sqlBuffer.append(" cat.status catstatus, cat.max_txn_msisdn, cat.uncntrl_transfer_allowed, cat.scheduled_transfer_allowed, cat.restricted_msisdns, ");
        sqlBuffer.append(" cat.parent_category_code, cat.product_types_allowed,cat.category_type,cat.hierarchy_allowed, cat.transfertolistonly, ");
        sqlBuffer.append(" cat.grph_domain_type, cat.multiple_grph_domains, cat.fixed_roles, cat.user_id_prefix,cat.web_interface_allowed, ");
        sqlBuffer.append(" cat.services_allowed,cat.domain_allowed,cat.fixed_domains,cat.outlets_allowed,cat.status categorystatus,cusers.comm_profile_set_id,cusers.transfer_profile_id,cusers.user_grade,gdt.sequence_no grph_sequence_no, ");
        sqlBuffer.append(" gdt.grph_domain_type_name,dm.domain_name,dm.status domainstatus,dm.domain_type_code,up.sms_pin,up.pin_required,up.invalid_pin_count,dt.restricted_msisdn restricted_msisdn_allow,  ");
        // for Zebra and Tango by sanjeew date 06/07/07
        sqlBuffer.append(" cusers.application_id, cusers.mpay_profile_id, cusers.user_profile_id, cusers.mcommerce_service_allow, up.access_type ");
        // end of Zebra and Tango
        // added for loading password reset info
        sqlBuffer.append(", u.PSWD_RESET ");
        sqlBuffer.append(", u.rsaflag ");// Added for RSA Authentication
        sqlBuffer.append(", u.AUTHENTICATION_ALLOWED ");// Added For
                                                        // Authentication Type
        if (isTrfRuleUserLevelAllow)
            sqlBuffer.append(", cusers.trf_rule_type ");// Added for User level
                                                        // Transfer Rule allow
        sqlBuffer.append(" FROM users u,users uowner,users uparent,networks l,categories cat,channel_users cusers,geographical_domain_types gdt ,domains dm,user_phones up,domain_types dt ");
        sqlBuffer.append(" WHERE ");
        if (!BTSLUtil.isNullString(msisdn))
            sqlBuffer.append("u.msisdn=? AND ");
        if (!BTSLUtil.isNullString(loginId))
            sqlBuffer.append("UPPER(u.login_id)=? AND ");

        sqlBuffer.append("  u.owner_id=uowner.user_id AND u.parent_id=uparent.user_id(+) AND gdt.grph_domain_type = cat.grph_domain_type ");
        sqlBuffer.append(" AND u.status <> ? AND u.status <> ? AND U.network_code=L.network_code(+)  AND u.user_id=cusers.user_id(+) ");
        sqlBuffer.append(" AND cat.category_code=U.category_code AND cat.status <> ? AND dm.domain_code = cat.domain_code ");
        sqlBuffer.append(" AND u.msisdn = up.msisdn(+) ");
        sqlBuffer.append(" AND u.user_id=up.user_id(+) ");
        sqlBuffer.append(" AND dt.domain_type_code = dm.domain_type_code ");
		return sqlBuffer.toString();
	}
	
	
	@Override
	public String loadUserLoanDetailsQry(){
		
			StringBuilder sqlBuffer = new StringBuilder(" SELECT u.user_id,cl.profile_id,cl.product_code,cl.loan_threhold,cl.loan_amount,cl.loan_given,cl.loan_given_amount,cl.last_loan_date,cl.last_loan_txn_id,cl.settlement_id, ");
				 sqlBuffer.append("cl.settlement_date,cl.settlement_loan_amount,cl.settlement_loan_interest,cl.loan_taken_from,cl.settlement_to,cl.optinout_allowed,cl.optinout_on,cl.optinout_by ");  
			sqlBuffer.append(" FROM users u,channel_user_loan_info cl ");  
	      sqlBuffer.append(" WHERE  u.status <> ? AND u.status <> ?  AND  u.user_id=cl.user_id AND u.user_id= ? ");  
				
	        return sqlBuffer.toString();
	}

}
