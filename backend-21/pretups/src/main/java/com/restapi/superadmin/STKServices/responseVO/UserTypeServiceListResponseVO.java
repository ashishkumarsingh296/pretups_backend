package com.restapi.superadmin.STKServices.responseVO;

import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import com.btsl.ota.services.businesslogic.ServiceSetVO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class UserTypeServiceListResponseVO extends BaseResponse {
    private ArrayList<STKServiceListValueVO> userTypeList;
    private ArrayList<ServiceSetVO> serviceSetList;
}
