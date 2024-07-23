package com.restapi.networkadmin.servicetypeselectormapping.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ServiceTypeSelectorMappingListResponseVO  extends BaseResponse {
    private List<ServiceTypeSelectorMappingDetailsVO> serviceSelectorMappingList;
}
