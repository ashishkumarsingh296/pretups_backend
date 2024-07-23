package com.btsl.db.query.oracle;

import com.web.pretups.domain.businesslogic.CategoryWebQry;

public class CategoryWebOracleQry implements CategoryWebQry{

	@Override
	public String saveCategoryQry() {
		StringBuilder strBuff = new StringBuilder();
		strBuff
				.append("INSERT INTO categories (category_code,");
		strBuff.append("category_name,domain_code,sequence_no,grph_domain_type,multiple_grph_domains,");
        strBuff.append("web_interface_allowed,sms_interface_allowed,fixed_roles,multiple_login_allowed,");
        strBuff.append("view_on_network_block,max_login_count,status,created_on,created_by,");
        strBuff.append("modified_on,modified_by,display_allowed,modify_allowed,product_types_allowed,services_allowed,max_txn_msisdn,");
        strBuff.append("uncntrl_transfer_allowed,scheduled_transfer_allowed,restricted_msisdns,");
        strBuff
            .append("parent_category_code,user_id_prefix,outlets_allowed,agent_allowed,hierarchy_allowed,category_type,transfertolistonly,low_bal_alert_allow,c2s_payee_status,cp2p_payee_status,cp2p_payer_status,cp2p_within_list,cp2p_within_list_level,AUTHENTICATION_TYPE) VALUES(UPPER(?),?,UPPER(?),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,UPPER(?),UPPER(?),?,?,?,?,?,?,UPPER(?),UPPER(?),UPPER(?),UPPER(?),UPPER(?),?)");
		return strBuff.toString();
		
	}
	
	

}
