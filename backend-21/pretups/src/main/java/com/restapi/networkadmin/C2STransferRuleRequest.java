package com.restapi.networkadmin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class C2STransferRuleRequest {

	public String senderSubscriberType;
	public String recieverSubscriberType;
	public String recieverServiceClassId;
	public String cardGroupSet;
	public String subServiceId;
	public String serviceType;
	public String gatewayCode;
	public String gradeCode;
	public String categoryCode;
}
