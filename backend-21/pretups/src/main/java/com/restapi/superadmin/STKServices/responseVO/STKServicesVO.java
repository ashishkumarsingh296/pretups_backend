package com.restapi.superadmin.STKServices.responseVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class STKServicesVO {
    private String status;
    private long length;
    private String majorVersion;
    private String minorVersion;
    private String description;
    private String label1;
    private String label2;
    private String modifiedBy;
    private String serviceID;
    private String modifiedOnAsString;
}