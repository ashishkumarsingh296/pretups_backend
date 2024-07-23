package com.restapi.networkadmin.networkStock;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.common.PretupsI;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;

@Getter
@Setter
public class NetworkStockTxnVO1 extends BaseResponse {
    private String txnNo;
    private String networkCode;
    private String networkFor;
    private String stockType;
    private String referenceNo;
    private Date txnDate;
    private String txnDateStr;
    private long requestedQuantity;
    private long approvedQuantity;
    private String initiaterRemarks;
    private String firstApprovedRemarks;
    private String secondApprovedRemarks;
    private String firstApprovedBy;
    private String secondApprovedBy;
    private Date firstApprovedOn;
    private Date secondApprovedOn;
    private String cancelledBy;
    private Date cancelledOn;
    private String createdBy;
    private Date createdOn;
    private Date modifiedOn;
    private String modifiedBy;
    private String txnStatus;
    private String entryType;
    private String txnType;
    private String initiatedBy;
    private long firstApproverLimit;
    private String userID;
    private long txnMrp;
    private long totalMrp = 0L;
    private String totalMrpStr;
    private long maxAmountLimit = 0L;
    private double totalQty = 0D;
    private String initiaterName;
    private String txnStatusName;
    private String networkForName;
    private String networkName;
    private long lastModifiedTime;
    private String txnMrpStr;
    private String approvedOnStr;
    private String otherInfo = null;
    private String txnCategory = null;
    private long postStock = 0;
    private long previousStock = 0;
    private String productCode = null;
    private ArrayList networkStockTxnItemsList;
    private long tax3value = 0;
    private String txnWallet = PretupsI.SALE_WALLET_TYPE;
    private String refTxnID;
    private Timestamp dbDateTime = null;
    private static final long serialVersionUID = 1L;
    private String dualCommissionType;

}
