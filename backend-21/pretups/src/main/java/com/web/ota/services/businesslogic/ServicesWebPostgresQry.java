package com.web.ota.services.businesslogic;

public class ServicesWebPostgresQry implements ServicesWebQry{

	@Override
	public String loadLatestMinorVersionQry() {
		StringBuilder sqlLoadBuf = new StringBuilder();
		sqlLoadBuf
				.append("SELECT MAX(SS.minor_version) AS maxvalue FROM sim_services SS ");
		sqlLoadBuf.append(" WHERE SS.service_id=? AND SS.major_version=? AND SS.minor_version <> 'DD'");
		return sqlLoadBuf.toString();

	}
	
	@Override
	public String loadLatestMajorVersionQry() {
		StringBuilder sqlLoadBuf = new StringBuilder();
		sqlLoadBuf
				.append("SELECT MAX(SS.major_version) AS maxvalue FROM sim_services SS ");
		sqlLoadBuf.append(" WHERE SS.service_id=? AND SS.major_version <> 'DD'");
		return sqlLoadBuf.toString();

	}
	
	@Override
	public String loadSmscDetailsQry() {
		StringBuilder sqlLoadBuf = new StringBuilder();
		sqlLoadBuf.append("SELECT sms_param_id, coalesce(smsc1,'') smsc1, coalesce(smsc2,'') smsc2, coalesce(smsc3,'') smsc3, coalesce(port1,'') port1, coalesce(port2,'') port2, coalesce(port3,'') port3, coalesce(vp1,0) vp1, coalesce(vp2,0) vp2, coalesce(vp3,0) vp3, coalesce(status,'n') status ");
    	sqlLoadBuf.append(" FROM (SELECT row_number() over() as  a,sms_param_id, smsc1, smsc2, smsc3, port1, port2, port3, coalesce(vp1,0) vp1, coalesce(vp2,0) vp2, coalesce(vp3,0) vp3, status   ");
    	sqlLoadBuf.append(" FROM sms_master WHERE network_code=? ORDER BY modified_on DESC  ) q ");
    	sqlLoadBuf.append(" WHERE a > (SELECT (row_number() over())-2  FROM sms_master WHERE network_code=?) ");
		return sqlLoadBuf.toString();

	}

	@Override
	public StringBuilder loadLangParametersQry() {
		StringBuilder sqlLoadBuf = new StringBuilder();
		sqlLoadBuf.append(" SELECT land_id, langparam1, langparam2, langparam3, langparam4, langparam5, status");
		sqlLoadBuf.append(" FROM (select row_number() over() as  a,land_id, langparam1, langparam2, langparam3, langparam4, langparam5, status   ");
		sqlLoadBuf.append(" FROM lang_master WHERE network_code=? ORDER by modified_on DESC  ) q ");
		sqlLoadBuf.append(" WHERE a > (SELECT (row_number() over())-2  FROM lang_master WHERE network_code=?) ");
		
		return sqlLoadBuf;

	}
	
	@Override
	public String getSimProfileInfoQry() {
		StringBuilder sqlLoadBuf = new StringBuilder();
		 sqlLoadBuf.append("SELECT sim_id, sim_app_version, coalesce(sim_vendor_name,'') sim_vendor_name,  ");
         sqlLoadBuf.append("coalesce(sim_type,'') sim_type, bytecode_file_size , no_of_menu_options,  ");
         sqlLoadBuf
             .append(" menu_record_len, max_concat_sms, uni_file_record_len, key_set_no, applet_tar_value, coalesce(encrypt_algo,'') encrypt_algo, coalesce(encrypt_mode,'') encrypt_mode, coalesce(encrypt_padding,'') encrypt_padding, coalesce(status,'') status  ");
         sqlLoadBuf.append(" FROM sim_profile WHERE sim_id=?   ");
		return sqlLoadBuf.toString();

	}
	
	@Override
	public String loadLatestSIMServiceListForSearchQry(String networkCode, boolean isall) {
		StringBuilder sqlLoadBuf;
		if ("ALL".equals(networkCode)) {
		    sqlLoadBuf = new StringBuilder();
			sqlLoadBuf.append("SELECT SS.service_set_id, SS.service_id,SS.major_version, SS.minor_version,SS.created_by,SS.created_on,SS.modified_on,SS.modified_by, ");
			sqlLoadBuf.append("SS.status,SS.label1, SS.label2,SS.length, SS.description FROM sim_services SS ");
			sqlLoadBuf.append(" WHERE (SS.user_type='ALL' OR SS.user_type = ?) AND SS.service_set_id=CASE ? WHEN 'ALL' THEN SS.service_set_id ELSE ? END ");
            sqlLoadBuf.append(" AND UPPER(SS.label1||SS.major_version||'.'||SS.minor_version) LIKE UPPER(?) ");
            sqlLoadBuf
                .append(" AND ((created_on=(SELECT MAX(created_on) FROM sim_services where service_id = SS.service_id AND SS.service_set_id=service_set_id and major_version<>'DD')) ");
            sqlLoadBuf
                .append(" OR  (created_on=(SELECT MAX(created_on) FROM sim_services where service_id = SS.service_id AND SS.service_set_id=service_set_id and major_version='DD'))) ");
            if (!isall) {
                sqlLoadBuf.append(" AND (SS.status='Y' OR SS.status IS NULL OR SS.status='D')   ");
            }
            sqlLoadBuf.append(" ORDER BY SS.label1 ");
        } else {
        	sqlLoadBuf = new StringBuilder();
			sqlLoadBuf.append("SELECT SS.service_set_id, SS.service_id,SS.major_version, SS.minor_version,SS.created_by,SS.created_on,SS.modified_on,SS.modified_by, ");
            sqlLoadBuf.append("SS.status,SS.label1,SS.label2,SS.length,SS.description FROM sim_services SS,networks L ");
            sqlLoadBuf.append(" WHERE (SS.user_type='ALL' OR SS.user_type = ?) AND L.network_code=? AND L.service_set_id=SS.service_set_id ");
            sqlLoadBuf.append(" AND SS.service_set_id=CASE ? WHEN 'ALL' THEN SS.service_set_id ELSE ? END  AND UPPER(SS.label1||SS.major_version||'.'||SS.minor_version) LIKE UPPER(?) ");
            sqlLoadBuf.append(" AND (SS.status='Y' OR SS.status IS NULL OR SS.status='D') AND SS.created_on=(SELECT MAX(created_on) ");
            sqlLoadBuf.append(" FROM sim_services  where service_id = SS.service_id AND SS.service_set_id=service_set_id) ORDER BY SS.label1 ");
        }
		return sqlLoadBuf.toString();
		}
	
}
