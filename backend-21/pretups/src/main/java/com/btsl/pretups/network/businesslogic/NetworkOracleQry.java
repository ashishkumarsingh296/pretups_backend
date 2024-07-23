package com.btsl.pretups.network.businesslogic;

public class NetworkOracleQry implements NetworkQry{
	@Override
	public String loadMSISDNInterfaceMappingCacheQry(){
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT i.external_id, i.status,i.message_language1, i.message_language2,I.status_type statustype, I.single_state_transaction, ");
        strBuff.append("inm.network_code, inm.prefix_id, inm.action, inm.method_type, inm.interface_id,im.handler_class,im.underprocess_msg_reqd,SC.service_class_id,im.interface_type_id ");
        strBuff.append("FROM  ");
        strBuff.append("intf_ntwrk_prfx_mapping inm, interfaces i,interface_types im,service_classes SC ");
        strBuff.append("WHERE ");
        strBuff.append("inm.interface_id = i.interface_id ");
        strBuff.append("AND  ");
        strBuff.append("i.interface_type_id = im.interface_type_id AND i.status<>'N' ");
        strBuff.append("AND I.interface_id=SC.interface_id(+) AND SC.service_class_code(+)=? AND SC.STATUS(+)<>'N' ");
        return strBuff.toString();
	}
}
