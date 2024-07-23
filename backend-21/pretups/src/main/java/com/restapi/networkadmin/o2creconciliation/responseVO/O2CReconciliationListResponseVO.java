package com.restapi.networkadmin.o2creconciliation.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class O2CReconciliationListResponseVO extends BaseResponse {
    private ArrayList<O2CReconciliationListVO> o2cReconciliationListVO;
}
