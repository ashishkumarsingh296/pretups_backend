package com.restapi.networkadmin.networkStock;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductStockTxnVO {

    public String requestedQuantity;
    public String walletBalance;
    public String wallet_type;
    public String productCode;
    public String productName;
    public long unitValue;
    public String amountStr;
    public long amount;
    private long totalMrp = 0L;
    private String totalMrpStr;
    private long maxAmountLimit = 0L;
    private double totalQty = 0D;
    private String stockTxnType;

}
