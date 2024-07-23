package com.restapi.networkadmin.servicetypeselectormapping.responseVO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServiceTypeSelectorMappingDetailsVO {
   
	private String selectorCode;
    private String selectorName;
    private String servStatus;
    private String statusDesc;
    private String senderSubscriberType;
    private String receiverSubscriberType;
    private String isDefaultCode;
    private String serviceType;
    private String serviceName;
    private String SNO;
    
}
