package com.restapi.networkadmin.c2sreconciliation.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class C2SReconciliationTransferDetailsVO extends BaseResponse {
    private String transferID;

    private C2STransferDetailsVO senderDetails;
    private C2STransferDetailsVO receiverDetails;

    private C2STransferDetailsVO creditBackDetails;
    private C2STransferDetailsVO reconcileDetails;

}
