package com.restapi.networkadmin.serviceproductinterfacemapping.requestVO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class DeleteServiceInterfaceMappingRequestVO {
    private ArrayList<ServiceSelectorInterfaceMapDeleteVO> serviceSelectorInterfaceMappingList;
    private HashMap seriesMap;
}

