package com.restapi.networkadmin.networkStock;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
@Setter
public class RejectStockDeductionRequestVO {
    private String networkCode;
    private String networkCodeFor;
    private String userId;
    private String txnNo;
    private String txnStatus;
    private double totalQty = 0D;
    private String remarks;
    private String referenceNumber;
    private String entryType;
    private String txnType;
    private String stockType;
    private String walletType;
    private long totalMrp = 0L;
    private String firstLevelRemarks;
    private String secondLevelRemarks;
    private long lastModifiedTime = 0L;
    private long firstLevelAppLimit = 0L;

}
