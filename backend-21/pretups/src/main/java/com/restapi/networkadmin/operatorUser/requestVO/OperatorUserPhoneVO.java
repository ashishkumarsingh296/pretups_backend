package com.restapi.networkadmin.operatorUser.requestVO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperatorUserPhoneVO {
    @JsonProperty("confirmPin")
    private String confirmPin;

    @JsonProperty("stkProfile")
    private String stkProfile;


    @JsonProperty("description")
    private String description;

    @JsonProperty("isPrimary")
    private String isPrimary;

    @JsonProperty("phoneNo")
    private String phoneNo;

    @JsonProperty("pin")
    private String pin;



}
