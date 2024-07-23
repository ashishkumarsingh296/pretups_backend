package com.restapi.superadmin.STKServices.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateByteCodeResponseVO extends BaseResponse {
    public String byteCodeLength;
    public String byteCode;
    public String serviceSetName;
}
