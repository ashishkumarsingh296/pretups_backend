package com.restapi.networkadmin.loyaltymanagement.requestVO;

import lombok.*;

@Setter
@Getter
public class ApproveProfileRequestVO extends SuspendRequestVO {
    private boolean approveStatus;
}
