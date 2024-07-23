package com.restapi.networkadmin.operatorUser.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
@Setter
public class OperatorUserGeographyResponseVO extends BaseResponse {
    ArrayList geographyList = new ArrayList();
}

