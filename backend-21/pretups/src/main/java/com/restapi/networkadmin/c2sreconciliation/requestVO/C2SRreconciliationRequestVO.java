package com.restapi.networkadmin.c2sreconciliation.requestVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class C2SRreconciliationRequestVO {
    String fromDate;
    String toDate;
    String serviceTypeCode;
}
