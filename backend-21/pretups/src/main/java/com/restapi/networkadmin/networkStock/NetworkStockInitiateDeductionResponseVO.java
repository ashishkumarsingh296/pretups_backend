package com.restapi.networkadmin.networkStock;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class NetworkStockInitiateDeductionResponseVO extends BaseResponse {
    public String requestor_name;
    public String network_code;
    public String date;
    public String stock_type;
    public String reference_number;
    public String txnType;
    public String txnStatus;
    public String entryType;
    public String networkName;
    public ArrayList productList;
    public ArrayList roamNetworkList;

}
