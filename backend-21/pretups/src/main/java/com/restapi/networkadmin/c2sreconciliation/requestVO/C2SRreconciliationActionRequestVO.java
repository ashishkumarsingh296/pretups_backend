package com.restapi.networkadmin.c2sreconciliation.requestVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class C2SRreconciliationActionRequestVO {
    String transferId;
    String action;
    String serviceTypeCode;
}
