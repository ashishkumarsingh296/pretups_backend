package com.restapi.networkadmin.serviceproductinterfacemapping.requestVO;

import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingVO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class AddServiceInterfaceMappingRequestVO {
    private String prepaidSeries;
    private String postpaidSeries;
    private ArrayList<ServiceSelectorInterfaceMapVO> serviceSelectorInterfaceMappingList;
    private HashMap seriesMap;
}
