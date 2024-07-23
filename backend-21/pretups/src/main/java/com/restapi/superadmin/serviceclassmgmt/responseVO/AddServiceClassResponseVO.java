package com.restapi.superadmin.serviceclassmgmt.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddServiceClassResponseVO extends BaseResponse {
    private String serviceClassID;
}
