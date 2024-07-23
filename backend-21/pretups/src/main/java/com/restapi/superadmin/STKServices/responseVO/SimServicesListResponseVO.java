package com.restapi.superadmin.STKServices.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class SimServicesListResponseVO extends BaseResponse {
    private ArrayList<STKServicesVO> simServicesList;
}
