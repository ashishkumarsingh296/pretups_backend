package com.restapi.networkadmin.serviceproductinterfacemapping.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class ServiceInterfaceMappingForModifyResponseVO extends BaseResponse {
    private String prepaidSeries;
    private String postpaidSeries;
    private HashMap seriesMap;

    private ArrayList productList;
    private ArrayList serviceTypeList;
    private ArrayList interfaceList;

    private String networkName;

    private ArrayList ServiceSelectorInterfaceMapVOList;

}
