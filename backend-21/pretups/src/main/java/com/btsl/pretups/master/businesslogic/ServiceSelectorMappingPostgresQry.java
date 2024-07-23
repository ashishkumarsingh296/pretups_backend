package com.btsl.pretups.master.businesslogic;

public class ServiceSelectorMappingPostgresQry implements ServiceSelectorMappingQry {
	@Override
	public String loadServiceTypeSelectorMapQry(){
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT s.service_type,s.selector_code,s.selector_name,s.sno,s.status,s.created_by,s.created_on,s.modified_by, ");
        strBuff.append(" s.modified_on,s.description,s.sender_bundle_id,s.receiver_bundle_id,");
        strBuff.append(" stype.type,stype.name,s.is_default_code,coalesce(el.amount,0)amount,coalesce(el.modified_allowed,'Y')modified_allowed ");
        strBuff.append(" FROM selector_amount_mapping el  right outer join  service_type_selector_mapping s on( el.selector_code =s.selector_code AND el.service_type =s.service_type ) ,service_type stype WHERE s.service_type=stype.service_type ");
        strBuff.append(" AND s.status<>'N'");
        return strBuff.toString();
	}
	
	@Override
	public String deleteSubscriberSelectorOUQry(){
    StringBuilder strBuff = new StringBuilder();
    strBuff.append(" update service_type_selector_mapping set display_order=display_order::integer - 1 , modified_on=CURRENT_TIMESTAMP  where display_order > ?");
    return strBuff.toString();
	}
	
	@Override
	public String deleteSubscriberSelectorSTSMQry(){
    StringBuilder strBuff = new StringBuilder();
    strBuff.append(" update service_type_selector_mapping set status='N' , display_order=0 , modified_on=CURRENT_TIMESTAMP where sno=? ");
    return strBuff.toString();
	}
	
	@Override
	public String deleteSubscriberSelectorSSMQry(){
    StringBuilder strBuff = new StringBuilder();
    strBuff.append(" update subscriber_selector_mapping set status = 'N' , modified_on=CURRENT_TIMESTAMP where sno=? ");
    return strBuff.toString();
	}

	@Override
	public String loadServiceTypeSelectorParamMapQry() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT s.service_type,s.selector_code,s.selector_name,s.sno,s.status,s.created_by,s.created_on,s.modified_by, ");
        strBuff.append(" s.modified_on,s.description,s.sender_bundle_id,s.receiver_bundle_id,");
        strBuff.append(" stype.type,stype.name,s.is_default_code,coalesce(el.amount,0)amount,coalesce(el.modified_allowed,'Y')modified_allowed ");
        strBuff.append(" FROM selector_amount_mapping el  right outer join  service_type_selector_mapping s on( el.selector_code =s.selector_code AND el.service_type =s.service_type ) ,service_type stype WHERE s.service_type=stype.service_type ");
        strBuff.append(" AND s.status<>'N' and s.service_type =? and s.selector_code = ?    ");
        return strBuff.toString();
	}
}
