package com.restapi.networkadmin.vouchercardgroup.response;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculateTransferValueResponseVO extends BaseResponse {


    private String receiverAccessFee;
    private String receiverTax1Value;
    private String receiverTax2Value;
    private String receiverTransferValue;
    private String newValidityDate;
}
