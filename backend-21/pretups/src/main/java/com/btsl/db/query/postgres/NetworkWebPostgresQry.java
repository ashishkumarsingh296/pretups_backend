package com.btsl.db.query.postgres;

import com.web.pretups.network.businesslogic.NetworkWebQry;

public class NetworkWebPostgresQry implements NetworkWebQry{
	@Override
	public String loadNWPrefixServiceTypeMappingListQry(){
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT psm.network_code, psm.prefix_id, psm.service_type,");
        strBuff.append(" psm.service_handler_class, np.series ");
        strBuff.append(" FROM prefix_service_mapping psm, network_prefixes np ");
        strBuff.append(" WHERE psm.prefix_id=np.prefix_id::varchar AND psm.network_code=? ");
        return strBuff.toString();
	}
}
