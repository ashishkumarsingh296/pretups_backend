package com.restapi.networkadmin.loyaltymanagement.responseVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDetailsSet {

    private String setId;
    private String shortCode;
    private String profileName;
    private String version;
    private String applicableFrom;
    private String applicableTo;
    private String status;
    private String promotionType;
    private String promotionTypeDesc;
    private String messageConfig;
    private String messageConfigDes;

}
