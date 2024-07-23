package com.restapi.superadmin.interfacemanagement.responseVO;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class InterfaceDetailResponseVO extends BaseResponse {
    private ArrayList<InterfaceVO> interfaceDetailsList;

    private String interfaceId;
}
