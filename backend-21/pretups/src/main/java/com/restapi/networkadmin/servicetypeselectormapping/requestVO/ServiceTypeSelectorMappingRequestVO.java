package com.restapi.networkadmin.servicetypeselectormapping.requestVO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServiceTypeSelectorMappingRequestVO {
	@NotNull
	private String serviceType;
	@NotNull
	private String serviceName;
	@NotNull@Size(min=1,max = 20)
	private String productName;
	@NotNull@Size(min=1,max = 3)
	private String productCode;
	@NotNull@Size(min =1, max=1)
	private String srvStatus;
	@NotNull@Size(min=3,max=4)
	private String senderSubscriberType;
	@NotNull@Size(min=3,max=4)
	private String receiverSubscriberType;
	@NotNull@Size(max=1)
	private String isDefault;
	

}
