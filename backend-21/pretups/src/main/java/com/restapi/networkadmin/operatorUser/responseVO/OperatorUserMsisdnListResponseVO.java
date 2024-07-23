package com.restapi.networkadmin.operatorUser.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
@Setter
public class OperatorUserMsisdnListResponseVO extends BaseResponse {
    ArrayList msisdnList =  new ArrayList();
}
