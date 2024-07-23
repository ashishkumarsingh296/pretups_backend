package com.btsl.pretups.vastrix.businesslogic;

public class ServiceSelectorInterfaceMappingOracleQry implements ServiceSelectorInterfaceMappingQry{
	@Override
	public String loadServSelInterfMappingCacheQry(){
		StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT  sst.service_type,sst.selector_code,sst.network_code,sst.interface_id,sst.prefix_id");
        strBuff.append(" ,sst.action,sst.method_type,sst.created_on,sst.created_by,sst.modified_on,sst.modified_by");
        strBuff.append(" ,sst.srv_selector_interface_id");
        strBuff.append(" ,i.external_id,i.status,i.message_language1,i.message_language2,i.status_type statustype,i.single_state_transaction");
        strBuff.append(" ,im.handler_class,im.underprocess_msg_reqd,sc.service_class_id,im.interface_type_id");
        strBuff.append(" FROM svc_setor_intfc_mapping sst,interfaces i,interface_types im,service_classes sc");
        strBuff.append(" WHERE sst.interface_id=i.interface_id ");
        strBuff.append(" AND i.interface_type_id = im.interface_type_id AND i.status<>'N'");
        strBuff.append(" AND I.interface_id=sc.interface_id(+) AND sc.service_class_code(+)=? AND sc.STATUS(+)<>'N'");
        return strBuff.toString();
	}
	
	@Override
	public String loadInterfaceForModifyQry(){
    StringBuilder selectQuery = new StringBuilder();
    selectQuery.append(" SELECT  distinct I.interface_id,I.interface_description ");
    selectQuery.append(" FROM interfaces I , svc_setor_intfc_mapping sim ");
    selectQuery.append(" WHERE sim.interface_id=I.interface_id  order by UPPER(interface_description)");
    return selectQuery.toString();
	}
	
	@Override
	public String loadServiceInterfaceMappingRuleListQry(){
    StringBuilder strBuff = new StringBuilder();
    strBuff.append(" SELECT sim.srv_selector_interface_id, sim.service_type,sim.selector_code, sim.network_code,sim.interface_id , ");
    strBuff.append("sim.action,sim.method_type, np.series,sim.created_on,sim.created_by,sim.modified_on,sim.modified_by ");
    strBuff.append("FROM svc_setor_intfc_mapping sim ,network_prefixes np ");
    strBuff.append("WHERE sim.prefix_id = np.prefix_id AND sim.network_code=? AND sim.service_type=? ORDER BY sim.selector_code,sim.interface_id,sim.method_type,sim.action ");
    return strBuff.toString();
	}
}
