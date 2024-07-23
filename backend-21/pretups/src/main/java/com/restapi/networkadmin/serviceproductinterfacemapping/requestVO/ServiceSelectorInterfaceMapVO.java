package com.restapi.networkadmin.serviceproductinterfacemapping.requestVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceSelectorInterfaceMapVO {
    private String serviceType;
    private String selectorCode;
    private String interfaceID;
    private String interfaceType;
    private String validatePrepaidSeries = null;
    private String validatePostpaidSeries = null;
    private String updatePrepaidSeries = null;
    private String updatePostpaidSeries = null;
}
