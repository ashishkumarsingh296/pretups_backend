package com.btsl.pretups.master.businesslogic;

public class ServiceSelectorMappingOracleQry implements ServiceSelectorMappingQry {
	@Override
	public String loadServiceTypeSelectorMapQry(){
		StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT s.service_type,s.selector_code,s.selector_name,s.sno,s.status,s.created_by,s.created_on,s.modified_by, ");
        strBuff.append(" s.modified_on,s.description,s.sender_bundle_id,s.receiver_bundle_id,");
        strBuff.append(" stype.type,stype.name,s.is_default_code,NVL(el.amount,0)amount,NVL(el.modified_allowed,'Y')modified_allowed ");
        strBuff.append(" FROM service_type_selector_mapping s,service_type stype,selector_amount_mapping el WHERE s.service_type=stype.service_type ");
        strBuff.append(" AND s.status<>'N'AND el.selector_code(+) =s.selector_code AND el.service_type(+) =s.service_type ");
        return strBuff.toString();
	}
	
	@Override
	public String deleteSubscriberSelectorOUQry(){
    StringBuilder strBuff = new StringBuilder();
    strBuff.append(" update service_type_selector_mapping set display_order=display_order - 1 , modified_on=sysdate  where display_order > ?");
    return strBuff.toString();
	}
	
	@Override
	public String deleteSubscriberSelectorSTSMQry(){
    StringBuilder strBuff = new StringBuilder();
    strBuff.append(" update service_type_selector_mapping set status='N' , display_order=0 , modified_on=sysdate where sno=? ");
    return strBuff.toString();
	}
	
	@Override
	public String deleteSubscriberSelectorSSMQry(){
    StringBuilder strBuff = new StringBuilder();
    strBuff.append(" update subscriber_selector_mapping set status = 'N' , modified_on=sysdate where sno=? ");
    return strBuff.toString();
	}

	@Override
	public String loadServiceTypeSelectorParamMapQry() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT s.service_type,s.selector_code,s.selector_name,s.sno,s.status,s.created_by,s.created_on,s.modified_by, ");
	    strBuff.append(" s.modified_on,s.description,s.sender_bundle_id,s.receiver_bundle_id,");
	    strBuff.append(" stype.type,stype.name,s.is_default_code,NVL(el.amount,0)amount,NVL(el.modified_allowed,'Y')modified_allowed ");
	    strBuff.append(" FROM service_type_selector_mapping s,service_type stype,selector_amount_mapping el WHERE s.service_type=stype.service_type ");
	    strBuff.append(" AND s.status<>'N'AND el.selector_code(+) =s.selector_code AND el.service_type(+) =s.service_type  and s.service_type =? and s.selector_code = ?    ");
		return strBuff.toString();
	}
	
	
	
}
