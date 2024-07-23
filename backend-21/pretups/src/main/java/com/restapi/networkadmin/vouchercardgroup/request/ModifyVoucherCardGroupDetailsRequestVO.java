package com.restapi.networkadmin.vouchercardgroup.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ModifyVoucherCardGroupDetailsRequestVO extends VoucherGroupDetailsRequestVO{
    private String cardGroupSetId;
    private String cardGroupId;
    private String version;
}
