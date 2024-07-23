package com.restapi.networkadmin.networkStock;

import java.util.ArrayList;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproveStockDeductionRequestVO {
    private String networkCode;
    private String networkCodeFor;
    private String userId;
    private long firstLevelAppLimit = 0L;;
    private String firstLevelApprovedBy;
    private String txnNo;
    private ArrayList<NetworkStockTxnItemsVO> stockItemsList = null;
    private String firstApproverRemarks;
    private String entryType;
    private String txnType;
    private String stockType;
    private String txnStatus;
    private String walletType;
    private double totalQty = 0D;
    private long totalMrp = 0L;
    private String remarks;
    private String referenceNumber;
    private String firstLevelRemarks;
    private String secondLevelRemarks;
    private long lastModifiedTime = 0L;
    private String totalMrpStr;

}
