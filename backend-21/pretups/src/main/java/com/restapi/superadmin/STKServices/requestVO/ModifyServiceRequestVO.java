package com.restapi.superadmin.STKServices.requestVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyServiceRequestVO {
    private String[] userTypeCategoriesList;
    private String serviceSetID;
    private String serviceID;
    private String label1;
    private String label2;
    private String wmlCode;
    private String byteCode;
    private String description;
    private String majorVersion;
    private String minorVersion;
    private String status;

}