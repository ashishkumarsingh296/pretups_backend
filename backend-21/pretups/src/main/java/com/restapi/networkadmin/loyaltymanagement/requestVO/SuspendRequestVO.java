package com.restapi.networkadmin.loyaltymanagement.requestVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuspendRequestVO {
    private String setId;
    private String version;
}
