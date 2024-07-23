package com.restapi.networkadmin.serviceproductinterfacemapping.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class ServiceInterfaceMappingForAddResponseVO extends BaseResponse {
    private String prepaidSeries;
    private String postpaidSeries;
    private HashMap seriesMap;

    private String serviceName;
    private String interfaceType;
    private String networkName;

    private ArrayList productList;
    private ArrayList interfaceList;

    private ArrayList serviceSelectorInterfaceMappingList;
}
