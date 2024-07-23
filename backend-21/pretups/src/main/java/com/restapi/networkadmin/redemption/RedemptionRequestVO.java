package com.restapi.networkadmin.redemption;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedemptionRequestVO {
    private ArrayList<RedemptionProductVO> focProducts;
    private String remarks;
    private String msisdn2;
    public String refnumber;
}
