package com.btsl.pretups.gateway.businesslogic;

public class MessageGatewayPostgresQry implements MessageGatewayQry{
	@Override
	public String loadMessageGatewayCacheQry(){
		StringBuilder strBuff = new StringBuilder();
		 strBuff.append(" SELECT mg.gateway_code, mg.gateway_type, mg.gateway_subtype, mg.protocol, ");
	     strBuff.append(" mg.handler_class, mg.network_code,   mg.host , mg.modified_on,mg.status, ");
	     strBuff.append(" mgty.flow_type,mgty.response_type,mgty.timeout_value,mgty.user_authorization_reqd authReqd,req_password_plain, ");
	     strBuff.append(" mgty.plain_msg_allowed,mgty.binary_msg_allowed,mgty.access_from ,mgsubty.gateway_subtype_name ");
	     strBuff.append(" FROM  message_gateway_types mgty,message_gateway_subtypes mgsubty  right outer join message_gateway mg  on  (mg.gateway_type = mgsubty.gateway_type AND mg.gateway_subtype = mgsubty.gateway_subtype) ");
	     strBuff.append(" WHERE mgty.gateway_type=mg.gateway_type AND mg.status <> ? ");
		return strBuff.toString();
	}
}
