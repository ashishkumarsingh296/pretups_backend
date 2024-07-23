package com.restapi.networkadmin.serviceproductinterfacemapping.requestVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class ModifyServiceInterfaceMappingRequestVO{
    private String prepaidSeries;
    private String postpaidSeries;
    private ArrayList<ServiceSelectorInterfaceMapModifyVO> serviceSelectorInterfaceMappingList;
    private HashMap seriesMap;
}
