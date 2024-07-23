package com.restapi.networkadmin.operatorUser.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
@Setter
public class OperatorUserDomainListResponseVO extends BaseResponse {
    ArrayList domainList = new ArrayList();
}
