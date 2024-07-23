package com.restapi.networkadmin.c2sreconciliation.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class C2SReconciliationResponseListVO extends BaseResponse {
    private ArrayList<C2SReconciliationVO> c2SRreconciliationItemsVOList;
}
