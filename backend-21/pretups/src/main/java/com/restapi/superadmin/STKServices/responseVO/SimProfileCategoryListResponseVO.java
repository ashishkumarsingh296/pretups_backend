package com.restapi.superadmin.STKServices.responseVO;

import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class SimProfileCategoryListResponseVO extends BaseResponse {
    private ArrayList<STKServiceListValueVO> userTypeList;
    private ArrayList<STKServiceListValueVO> profileList;
    private ArrayList<STKServiceListValueVO> simProfileList;
}
