package com.btsl.pretups.master.businesslogic;

public class NetworkServicePostgresQry implements NetworkServiceQry{

	@Override
	public String loadNetworkServicesListQry()
	{
		 StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT network_name,network_code,NTS.language1_message,NTS.language2_message, ");
        strBuff.append("NTS.status,NTS.modified_on modified_on1,NT.modified_on modified_on2 ");
        strBuff.append("FROM network_services NTS ");
        strBuff.append("RIGHT OUTER JOIN networks NT ON ( NTS.sender_network=NT.network_code AND NTS.receiver_network=? AND NTS.module_code=? AND NTS.service_type=? )");
        
        return strBuff.toString();
	}

}
