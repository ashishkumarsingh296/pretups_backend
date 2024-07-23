package com.restapi.networkadmin.requestVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddServiceProductAmountMappingRequestVO {
    private String serviceName;
    private String serviceId;
    private String productName;
    private String productId;
    private String amount;
    private String modifyAllowed;
 }
