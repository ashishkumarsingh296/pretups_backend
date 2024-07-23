package com.restapi.networkadmin.networkStock;


import java.util.ArrayList;
import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalStockResponseVO extends BaseResponse {
    public ArrayList<NetworkStockTxnVO1> networkStockTxnList;
    private String networkCode;
    private String userId;
    private String radioIndex;

}
