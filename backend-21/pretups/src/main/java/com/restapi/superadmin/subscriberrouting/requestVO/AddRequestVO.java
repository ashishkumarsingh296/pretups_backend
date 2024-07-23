package com.restapi.superadmin.subscriberrouting.requestVO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddRequestVO {
    @NotNull
    private String interfaceCategory;
    @NotNull
    private String interfaceType;
    @NotNull
    private String interfaceId;
    @NotNull
    public String mobileNumbers;
}
