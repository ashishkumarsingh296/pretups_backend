package com.restapi.networkadmin.servicetypeselectormapping.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ServiceTypeSelectorMappingDropDownResponseVO extends BaseResponse {
   private List<ServiceTypeResponseVO> serviceTypeList;
}
