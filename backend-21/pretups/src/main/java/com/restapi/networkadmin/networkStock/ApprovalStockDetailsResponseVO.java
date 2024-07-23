package com.restapi.networkadmin.networkStock;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ApprovalStockDetailsResponseVO extends BaseResponse {

    private String networkCode;
    private String userId;
    private ArrayList<NetworkStockTxnItemsVO> stockItemsList;
    private String networkCodeFor;
    private String networkName;
    private String requesterName;
    private String stockDateStr;
    private String referenceNumber;
    private String remarks;
    private String userID;
    private String entryType;
    private String stockType;
    private String txnType;
    private String txnStatus;
    private String txnNo;
    private String networkForName;
    private String firstLevelRemarks;
    private String secondLevelRemarks;
    private String firstLevelApprovedBy;
    private String secondLevelApprovedBy;
    private long firstLevelAppLimit = 0L;
    private long totalMrp = 0L;
    private String totalMrpStr;
    private long maxAmountLimit = 0L;
    private double totalQty = 0D;
    private long lastModifiedTime = 0L;
    private String radioIndex;
    private String fromDateStr;
    private String toDateStr;
    private String tmpTxnNo;
    private String txnStatusDesc;
    private String stockTxnType;
    private String walletType;

}
