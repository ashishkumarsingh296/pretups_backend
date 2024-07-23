package com.restapi.networkadmin.c2sreconciliation.responseVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class C2STransferDetailsVO {
    private String msisdn;
    private String entryType;
    private String transferValue;
    private String previousBalance;
    private String postBalance;
    private String serviceClassCode;
    private String InterfaceResponseCode;
    private String referenceID;
    private String transferStatus;
    private String protocolStatus;
    private String accountStatus;
}
