package com.btsl.db.query.postgres;

import com.btsl.ota.services.businesslogic.ServicesQry;

public class ServicesPostgresQry implements ServicesQry{

	@Override
	public String loadSimProfileListQry() {
		StringBuilder selectQueryBuff = new StringBuilder(" SELECT sim_id, sim_app_version, coalesce(sim_vendor_name,'') sim_vendor_name,  ");
        selectQueryBuff.append("coalesce(sim_type,'') sim_type, bytecode_file_size , no_of_menu_options,  ");
        selectQueryBuff.append("menu_record_len, max_concat_sms, uni_file_record_len, key_set_no, applet_tar_value, coalesce(encrypt_algo,'') encrypt_algo, coalesce(encrypt_mode,'') encrypt_mode, coalesce(encrypt_padding,'') encrypt_padding, coalesce(status,'') status  ");
        selectQueryBuff.append("FROM sim_profile WHERE status=? ");
        return selectQueryBuff.toString();
	}

}
