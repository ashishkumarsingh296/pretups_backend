package com.restapi.networkadmin.operatorUser.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Setter
@Getter
public class OperatorUserServiceListResponseVO extends BaseResponse {
    ArrayList serviceList = new ArrayList();
}
