package com.restapi.networkadmin.responseVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceProductAmountDetailsVO {
	private String serviceId;
	private String productId;
	private String serviceName;
	private String productName;
	private String amount;
	private String modifiedAllowed;
	private String status;
	
}
