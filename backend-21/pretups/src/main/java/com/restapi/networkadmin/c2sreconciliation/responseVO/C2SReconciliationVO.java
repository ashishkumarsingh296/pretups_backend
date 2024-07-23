package com.restapi.networkadmin.c2sreconciliation.responseVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class C2SReconciliationVO {
    private String transferID;
    private String transferDate;
    private String transferValue;
    private String productName;
    private String senderName;
    private String senderMsisdn;
    private String senderNetworkCode;
    private String receiverMsisdn;
    private String receiverNetworkCode;
    private String txnStatus;
    private String gatewayType;
    private String cardGroupSetId;
    private String cardGroupId;
    private String cardGroupVersion;
}
