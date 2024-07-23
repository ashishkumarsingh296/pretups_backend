package com.restapi.superadmin.sublookup.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubLookUpResponseVO extends BaseResponse {
    private String lookUpCode;
    private String lookUpName;
    private String subLookUpCode;
    private String subLookUpName;
}
