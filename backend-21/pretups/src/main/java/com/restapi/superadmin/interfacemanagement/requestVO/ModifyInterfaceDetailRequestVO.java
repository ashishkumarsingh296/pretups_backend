package com.restapi.superadmin.interfacemanagement.requestVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ModifyInterfaceDetailRequestVO extends InterfaceDetailRequestVO{
    private String interfaceId;
    private String interfaceCategory;
}
