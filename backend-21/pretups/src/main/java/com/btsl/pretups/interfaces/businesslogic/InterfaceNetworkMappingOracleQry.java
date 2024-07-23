package com.btsl.pretups.interfaces.businesslogic;

public class InterfaceNetworkMappingOracleQry implements InterfaceNetworkMappingQry{
	@Override
	public String loadInterfaceNetworkPrefixQry(){
		StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT inpm.network_code,inpm.interface_id,np.series_type, ");
        strBuff.append("inpm.prefix_id,inpm.action,inpm.method_type,np.series ");
        strBuff.append(" FROM intf_ntwrk_prfx_mapping inpm,network_prefixes np ");
        strBuff.append(" where inpm.network_code = ? AND inpm.network_code = np.network_code ");
        strBuff.append(" AND inpm.prefix_id = np.prefix_id order by interface_id ");
        return strBuff.toString();
	}
}
