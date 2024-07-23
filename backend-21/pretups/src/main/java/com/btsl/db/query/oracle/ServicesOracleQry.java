package com.btsl.db.query.oracle;

import com.btsl.ota.services.businesslogic.ServicesQry;

public class ServicesOracleQry implements ServicesQry{

	@Override
	public String loadSimProfileListQry() {
		 StringBuilder selectQueryBuff = new StringBuilder(" SELECT sim_id, sim_app_version, nvl(sim_vendor_name,'') sim_vendor_name,  ");
         selectQueryBuff.append("nvl(sim_type,'') sim_type, bytecode_file_size , no_of_menu_options,  ");
         selectQueryBuff.append("menu_record_len, max_concat_sms, uni_file_record_len, key_set_no, applet_tar_value, nvl(encrypt_algo,'') encrypt_algo, nvl(encrypt_mode,'') encrypt_mode, nvl(encrypt_padding,'') encrypt_padding, nvl(status,'') status  ");
         selectQueryBuff.append("FROM sim_profile WHERE status=? ");
         return selectQueryBuff.toString();
	}

}
