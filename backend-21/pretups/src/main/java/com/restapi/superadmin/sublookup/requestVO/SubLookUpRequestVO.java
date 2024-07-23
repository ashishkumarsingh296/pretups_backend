package com.restapi.superadmin.sublookup.requestVO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubLookUpRequestVO {
    @NotNull
    private String lookUpCode;
    @NotNull
    private String subLookUpName;
}
