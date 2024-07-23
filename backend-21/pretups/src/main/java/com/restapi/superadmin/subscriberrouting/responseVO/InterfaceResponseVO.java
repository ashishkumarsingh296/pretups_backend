package com.restapi.superadmin.subscriberrouting.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class InterfaceResponseVO extends BaseResponse {
    ArrayList<InterfaceDetailVO> interfaceList;
}
