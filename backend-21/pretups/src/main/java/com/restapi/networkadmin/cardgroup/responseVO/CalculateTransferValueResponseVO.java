package com.restapi.networkadmin.cardgroup.responseVO;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;


@Getter
@Setter
public class CalculateTransferValueResponseVO extends BaseResponse {


    private String receiverAccessFee;
    private String receiverTax1Value;
    private String receiverTax2Value;
    private String receiverTransferValue;
    private String receiverDate;
}
