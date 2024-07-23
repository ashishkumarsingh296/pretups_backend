package com.restapi.networkadmin.operatorUser.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
@Setter
public class OperatorUesrVoucherResponseVO extends BaseResponse {
    ArrayList voucherType = new ArrayList<>();
    ArrayList voucherSegment = new ArrayList<>();
}
