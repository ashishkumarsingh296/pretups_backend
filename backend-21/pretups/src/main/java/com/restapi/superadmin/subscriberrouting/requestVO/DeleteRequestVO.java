package com.restapi.superadmin.subscriberrouting.requestVO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteRequestVO {
    @NotNull
    private String interfaceCategory;
    @NotNull
    public String mobileNumbers;
}
