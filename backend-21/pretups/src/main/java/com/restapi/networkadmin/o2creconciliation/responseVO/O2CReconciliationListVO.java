package com.restapi.networkadmin.o2creconciliation.responseVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class O2CReconciliationListVO {
    private String transferID;
    private String referenceNumber;
    private String transactionNumber;
    private String transactionDate;
    private String transferDate;
    private String initiatedBy;
    private String transferValue;
    private String amount;
    private String transactionStatus;
    private String distributionType;
    private String mobileNumber;
}
