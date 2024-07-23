package com.restapi.networkadmin.networkStock;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class NetworkInitiateStockDeductionRequestVO {
    public String networkCode;
    public String networkCodeFor;
    public String networkForName;
    public String networkName;
    public String requesterName;
    public String stockDate;
    public String remarks;
    public String referenceNumber;
    public long walletBalance;
    public String userId;
    public ArrayList<ProductStockTxnVO> productList;
    public ArrayList stockList;

    public int getStockProductListSize() {
        if (productList != null) {
            return productList.size();
        } else {
            return 0;
        }
    }


}
