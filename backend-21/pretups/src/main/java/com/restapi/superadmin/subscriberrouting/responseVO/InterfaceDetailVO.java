package com.restapi.superadmin.subscriberrouting.responseVO;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class InterfaceDetailVO implements Serializable {
    private String interfaceId;
    private String interfaceName;
    private String externalId;
}
