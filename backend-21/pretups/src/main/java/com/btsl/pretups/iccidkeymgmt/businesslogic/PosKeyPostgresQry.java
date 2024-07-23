package com.btsl.pretups.iccidkeymgmt.businesslogic;

public class PosKeyPostgresQry implements PosKeyQry{
	@Override
	public String loadEncryptionParametersQry(){
		String qry = "SELECT network_code,sim_vender_code,sim_profile_id,coalesce(encrypt_algo,'') encrypt_algo, coalesce(encrypt_mode,'') encrypt_mode, coalesce(encrypt_padding,'') encrypt_padding FROM simvender_master_key_mapping";
		return qry;
	}
	@Override
	public String loadPosKeyQry(){
		StringBuilder qryBuf = new StringBuilder();
        qryBuf.append("SELECT pk.icc_id, pk.msisdn, pk.registered, pk.created_by, coalesce(to_char(pk.created_on,'dd/mm/yy HH24:MI:SS'),'') ");
        qryBuf.append("created_on,pk.decrypt_key, pk.new_icc_id, pk.modified_by, coalesce(to_char(pk.modified_on,'dd/mm/yy HH24:MI:SS'),'') ");
        qryBuf.append("modified_on, pk.sim_profile_id, us1.user_name created, us2.user_name modified,pk.network_code ");
        qryBuf.append("FROM pos_keys pk left outer join users us2 on pk.modified_by = us2.user_id, users us1 ");
        qryBuf.append("WHERE (pk.icc_id=? or pk.msisdn=?) AND pk.created_by = us1.user_id");
        return qryBuf.toString();
	}

}
