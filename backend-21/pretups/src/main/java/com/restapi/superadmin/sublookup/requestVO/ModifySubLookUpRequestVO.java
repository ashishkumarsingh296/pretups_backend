package com.restapi.superadmin.sublookup.requestVO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifySubLookUpRequestVO extends SubLookUpRequestVO{
    @NotNull
    private String subLookUpCode;
}
