package com.btsl.pretups.user.businesslogic;

import com.btsl.pretups.common.PretupsI;

public class ExtUserPostgresQry implements ExtUserQry{
	@Override
	public String loadUsersDetailsforExtReqQry(String p_userID, String p_statusUsed, String p_status) {
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
        strBuff.append("USR.login_id,USR.password ,USR.category_code usr_category_code,USR.parent_id,USR.reference_id, ");
        strBuff.append("USR.owner_id,USR.allowed_ip,USR.allowed_days, D.domain_name, ");
        strBuff.append("USR.from_time,USR.to_time,USR.employee_code,");
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

        // for Zebra and Tango by sanjeew date 18/07/07
        strBuff.append(" USR_PHONE.access_type user_access_type ");
        // end of Zebra and Tango

        strBuff.append("FROM user_phones USR_PHONE,users USR left join (users PRNT_USR left join  categories  PRNT_CAT on PRNT_USR.category_code=PRNT_CAT.category_code )on USR.parent_id=PRNT_USR.user_id ");
        strBuff.append(" left join users MOD_USR  on MOD_USR.user_id = USR.modified_by left join users USR_CRBY on USR_CRBY.user_id= USR.created_by ,users ONR_USR,categories USR_CAT, ");
        strBuff.append("categories ONR_CAT,lookups l,user_geographies UG, geographical_domains GD,  domains D ");
        strBuff.append("WHERE USR_PHONE.msisdn=? AND USR_PHONE.user_id=USR.user_id ");

        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append(" AND USR.status IN (" + p_status + ") ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append(" AND USR.status NOT IN (" + p_status + ") ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append(" AND USR.status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append(" AND USR.status <> ? ");
        }

        strBuff.append("AND USR.owner_id=ONR_USR.user_id ");
        strBuff.append("AND USR.category_code=USR_CAT.category_code ");
        strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
        strBuff.append(" AND USR.status = l.lookup_code ");
        strBuff.append(" AND l.lookup_type= ? ");
        strBuff.append(" AND USR.user_id = UG.user_id ");
        strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
        strBuff.append(" AND USR_CAT.domain_code=D.domain_code ");
        if (p_userID != null) {
            strBuff.append(" AND USR.user_id IN ( ");
            strBuff.append(" with recursive q as ( SELECT user_id from users where user_id = ? union all SELECT m.user_id from users m join q on q.user_id = m.parent_id  ) ");
            strBuff.append(" select q.user_id from q where user_id != ? ) ");
        }

		return strBuff.toString();
	}
	@Override
	public String  loadUsersDetailsforExtCodeReqQry(String p_userID, String p_statusUsed, String p_status){
		 final StringBuilder strBuff = new StringBuilder();
	        strBuff.append(" SELECT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
	        strBuff.append("USR.login_id,USR.password,USR.category_code usr_category_code,USR.parent_id,USR.reference_id, ");
	        strBuff.append("USR.owner_id,USR.allowed_ip,USR.allowed_days, D.domain_name, ");
	        strBuff.append("USR.from_time,USR.to_time,USR.employee_code,");
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

	        // for Zebra and Tango by sanjeew date 18/07/07
	        strBuff.append(" USR_PHONE.access_type user_access_type ");
	        // end of Zebra and Tango

	        strBuff.append("FROM user_phones USR_PHONE,users USR left join (users PRNT_USR left join  categories  PRNT_CAT on PRNT_USR.category_code=PRNT_CAT.category_code )on USR.parent_id=PRNT_USR.user_id  " );
	        strBuff.append(" left join users MOD_USR  on MOD_USR.user_id = USR.modified_by left join users USR_CRBY on USR_CRBY.user_id= USR.created_by ");
	        		strBuff.append(",users ONR_USR,categories USR_CAT,  ");
	        strBuff.append("categories ONR_CAT, lookups l,user_geographies UG, geographical_domains GD,  domains D ");
	        strBuff.append("WHERE USR.EXTERNAL_CODE=? AND USR_PHONE.user_id=USR.user_id ");

	        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
	            strBuff.append(" AND USR.status IN (" + p_status + ") ");
	        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
	            strBuff.append(" AND USR.status NOT IN (" + p_status + ") ");
	        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
	            strBuff.append(" AND USR.status =? ");
	        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
	            strBuff.append(" AND USR.status <> ? ");
	        }

	        strBuff.append(" AND USR.owner_id=ONR_USR.user_id ");
	        strBuff.append("AND USR.category_code=USR_CAT.category_code ");
	        strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
	        strBuff.append(" AND USR.status = l.lookup_code ");
	        strBuff.append(" AND l.lookup_type= ? ");
	        strBuff.append(" AND USR.user_id = UG.user_id ");
	        strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
	        strBuff.append(" AND USR_CAT.domain_code=D.domain_code ");
	        if (p_userID != null) {
	            strBuff.append(" AND USR.user_id IN ( ");
	            strBuff.append(" with recursive q as ( SELECT user_id from users where user_id = ? union all SELECT m.user_id from users m join q on q.user_id = m.parent_id  ) ");
	            strBuff.append(" select q.user_id from q where user_id != ?) ");
	        }
	        return strBuff.toString();
	}
}
