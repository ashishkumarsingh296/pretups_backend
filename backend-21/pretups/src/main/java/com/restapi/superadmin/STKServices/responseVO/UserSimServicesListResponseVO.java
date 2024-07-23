package com.restapi.superadmin.STKServices.responseVO;

import com.btsl.common.BaseResponse;
import com.btsl.ota.services.businesslogic.UserServicesVO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class UserSimServicesListResponseVO extends BaseResponse {
    private ArrayList<STKUserServicesVO> userSimServicesList;
}
