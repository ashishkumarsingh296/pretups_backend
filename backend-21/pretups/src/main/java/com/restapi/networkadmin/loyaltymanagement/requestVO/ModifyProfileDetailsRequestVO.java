package com.restapi.networkadmin.loyaltymanagement.requestVO;

import lombok.*;

@Setter
@Getter
public class ModifyProfileDetailsRequestVO extends AddProfileDetailsRequestVO {
    private String setId;
    private int lastVersion;
}
