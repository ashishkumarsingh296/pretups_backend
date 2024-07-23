package com.restapi.superadmin.interfacemanagement.responseVO;

import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
@Setter
public class InterfaceTypeResponseVO extends BaseResponse {
    ArrayList<InterfaceTypeVO> interfaceTypeList;
}
