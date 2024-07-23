package com.btsl.pretups.payment.businesslogic;

public class PaymentOracleQry implements PaymentQry{
	@Override
	public String loadPaymentKeywordCacheQry(){
		StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT ");
        strBuff.append(" PMK.payment_method_keyword, PMK.payment_method_type, PMK.service_type, PMK.network_code, PMK.use_default_interface, PMK.default_interface_id ");
        strBuff.append(" , I.external_id, I.status,I.status_type statustype,I.message_language1, I.message_language2, IT.handler_class,IT.underprocess_msg_reqd,SC.service_class_id ");
        strBuff.append(" FROM  ");
        strBuff.append(" payment_method_keyword PMK, interfaces I,interface_types IT ,service_classes SC");
        strBuff.append(" WHERE  ");
        strBuff.append(" PMK.default_interface_id=I.interface_id AND I.status<>'N' AND I.interface_type_id=IT.interface_type_id ");
        strBuff.append(" AND I.interface_id=SC.interface_id(+) AND SC.service_class_code(+)=? AND SC.STATUS(+)<>'N'");
		return strBuff.toString();
	}
}
