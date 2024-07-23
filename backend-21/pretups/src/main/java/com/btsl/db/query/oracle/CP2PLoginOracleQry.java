package com.btsl.db.query.oracle;

import com.btsl.cp2p.login.businesslogic.CP2PLoginQry;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class CP2PLoginOracleQry implements CP2PLoginQry{
	private String className = "CP2PLoginOracleQry";
	@Override
	public String loadUserDetailsByMsisdnOrLoginIdQry(String msisdn,String loginId )
			{
		String methodName = className+"#loadUserDetailsByMsisdnOrLoginIdQry";
		  StringBuilder sqlBuffer = new StringBuilder(" SELECT uowner.user_name owner_name, uparent.user_name parent_name, u.user_id, u.user_name, u.network_code,l.network_name,l.report_header_name, u.login_id, u.password, ");
	        sqlBuffer.append(" u.category_code,u.company,u.fax, u.parent_id, u.owner_id, u.msisdn, u.allowed_ip,  u.allowed_days,u.from_time,u.to_time,u.firstname,u.lastname, ");
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
	        sqlBuffer.append(" gdt.grph_domain_type_name,dm.domain_name,dm.status domainstatus,dm.domain_type_code,up.sms_pin,up.pin_required,up.invalid_pin_count,dt.restricted_msisdn restricted_msisdn_allow,  ");
	        // for Zebra and Tango by sanjeew date 06/07/07
	        sqlBuffer.append(" cusers.application_id, cusers.mpay_profile_id, cusers.user_profile_id, cusers.mcommerce_service_allow, up.access_type ");
	        // end of Zebra and Tango
	        // added for loading password reset info
	        sqlBuffer.append(", u.PSWD_RESET ");
	        sqlBuffer.append(" FROM users u,users uowner,users uparent,networks l,categories cat,channel_users cusers,geographical_domain_types gdt ,domains dm,user_phones up,domain_types dt ");
	        sqlBuffer.append(" WHERE ");
	        if (!BTSLUtil.isNullString(msisdn))
	            sqlBuffer.append("u.msisdn=? AND ");
	        if (!BTSLUtil.isNullString(loginId))
	            sqlBuffer.append("u.login_id=? AND ");

	        sqlBuffer.append("  u.owner_id=uowner.user_id AND u.parent_id=uparent.user_id(+) AND gdt.grph_domain_type = cat.grph_domain_type ");
	        sqlBuffer.append(" AND u.status <> ? AND u.status <> ? AND U.network_code=L.network_code(+)  AND u.user_id=cusers.user_id(+) ");
	        sqlBuffer.append(" AND cat.category_code=U.category_code AND cat.status <> ? AND dm.domain_code = cat.domain_code ");
	        sqlBuffer.append(" AND u.msisdn = up.msisdn(+) ");
	        sqlBuffer.append(" AND u.user_id=up.user_id(+) ");
	        sqlBuffer.append(" AND dt.domain_type_code = dm.domain_type_code ");
	        LogFactory.printLog(methodName, sqlBuffer.toString(), LOG);
            
		return sqlBuffer.toString();
	}
	@Override
	public String loadCP2PSubscriberDetailsQry(String servicetype) {
		String methodName = className+"#loadCP2PSubscriberDetailsQry";
		StringBuilder sqlBuffer = new StringBuilder(" SELECT p2ps.ACTIVATED_ON, p2ps.BILLING_CYCLE_DATE, p2ps.BILLING_TYPE, p2ps.BUDDY_SEQ_NUMBER, p2ps.CONSECUTIVE_FAILURES, l.network_name,l.report_header_name,");
        sqlBuffer.append(" p2ps.COUNTRY, p2ps.CREATED_BY, p2ps.CREATED_ON, p2ps.CREDIT_LIMIT,p2ps.pswd_reset, ");
        sqlBuffer.append(" p2ps.FIRST_INVALID_PIN_TIME, p2ps.INVALID_PASSWORD_COUNT, p2ps.LANGUAGE, p2ps.LAST_LOGIN_ON, p2ps.LAST_SUCCESS_TRANSFER_DATE, ");
        sqlBuffer.append(" p2ps.LAST_TRANSFER_AMOUNT, p2ps.LAST_TRANSFER_ID, p2ps.LAST_TRANSFER_MSISDN, p2ps.LAST_TRANSFER_ON, p2ps.LAST_TRANSFER_STATUS, ");
        sqlBuffer.append(" p2ps.LAST_TRANSFER_TYPE, p2ps.LOGIN_ID, p2ps.MODIFIED_BY, p2ps.MODIFIED_ON, ");
        sqlBuffer.append(" p2ps.MSISDN, p2ps.NETWORK_CODE, p2ps.PASSWORD, p2ps.PIN, p2ps.PIN_BLOCK_COUNT, p2ps.PIN_MODIFIED_ON, p2ps.PREFIX_ID, l.status networkstatus,l.language_1_message,l.language_2_message,  ");
        if(servicetype!=null){
        	sqlBuffer.append("  p2psc.DAILY_TRANSFER_AMOUNT, p2psc.DAILY_TRANSFER_COUNT, ");
        	sqlBuffer.append(" p2psc.PREV_DAILY_TRANSFER_AMOUNT, p2psc.PREV_DAILY_TRANSFER_COUNT, p2psc.PREV_MONTHLY_TRANSFER_AMOUNT,");
            sqlBuffer.append(" p2psc.PREV_MONTHLY_TRANSFER_COUNT, p2psc.PREV_TRANSFER_DATE, p2psc.PREV_TRANSFER_MONTH_DATE, p2psc.PREV_TRANSFER_WEEK_DATE, ");
            sqlBuffer.append("p2psc.PREV_WEEKLY_TRANSFER_AMOUNT, p2psc.PREV_WEEKLY_TRANSFER_COUNT,p2psc.WEEKLY_TRANSFER_AMOUNT, p2psc.WEEKLY_TRANSFER_COUNT, ");	
            sqlBuffer.append("  p2psc.MONTHLY_TRANSFER_AMOUNT, p2psc.MONTHLY_TRANSFER_COUNT, ");
        }
        sqlBuffer.append("  p2ps.PSWD_MODIFIED_ON, p2ps.REGISTERED_ON, p2ps.REQUEST_STATUS,");
        sqlBuffer.append(" p2ps.SERVICE_CLASS_CODE, p2ps.SERVICE_CLASS_ID, p2ps.SKEY_REQUIRED, p2ps.STATUS, p2ps.SUBSCRIBER_TYPE, p2ps.TOTAL_TRANSFER_AMOUNT, ");
        sqlBuffer.append(" p2ps.TOTAL_TRANSFERS, p2ps.USER_ID, p2ps.USER_NAME,p2ps.PASSWORD_COUNT_UPDATED_ON");
        sqlBuffer.append(" FROM  P2P_SUBSCRIBERS p2ps, NETWORKS l");
        if(servicetype!=null){
        	sqlBuffer.append(" ,P2P_SUBSCRIBERS_COUNTERS p2psc");
        }
        sqlBuffer.append(" WHERE  UPPER(login_id)=UPPER(?) ");
        if(servicetype!=null){
        	sqlBuffer.append(" AND p2ps.msisdn=p2psc.msisdn(+) AND p2psc.service_type=?");
        }
        sqlBuffer.append(" AND p2ps.status <> ? AND p2ps.status <> ? AND p2ps.network_code=L.network_code(+) ");
        LogFactory.printLog(methodName, sqlBuffer.toString(), LOG);
        return sqlBuffer.toString();
	}

}
