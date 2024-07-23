package com.btsl.pretups.master.businesslogic;

public class NetworkServiceOracleQry implements NetworkServiceQry{
	
	public String loadNetworkServicesListQry()
	{
		 StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT network_name,network_code,NTS.language1_message,NTS.language2_message, ");
        strBuff.append("NTS.status,NTS.modified_on modified_on1,NT.modified_on modified_on2 ");
        strBuff.append("FROM networks NT,network_services NTS ");
        strBuff.append("WHERE NTS.receiver_network(+)=? AND NTS.module_code(+)=? ");
        strBuff.append("AND NTS.service_type(+)=? AND NTS.sender_network(+)=NT.network_code ");
        
        return strBuff.toString();
	}

}
