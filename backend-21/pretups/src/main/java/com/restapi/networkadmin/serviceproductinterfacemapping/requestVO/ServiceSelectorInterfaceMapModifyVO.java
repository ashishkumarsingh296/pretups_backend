package com.restapi.networkadmin.serviceproductinterfacemapping.requestVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceSelectorInterfaceMapModifyVO {
    private String serviceType;
    private String selectorCode;
    private String interfaceID;
    private String serviceInterfaceMappngID;
    private String methodType;

    private String multiBox;

    private String validatePrepaidSeries = null;
    private String validatePostpaidSeries = null;
    private String updatePrepaidSeries = null;
    private String updatePostpaidSeries = null;
}

