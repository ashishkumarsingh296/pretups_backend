package com.restapi.superadmin.STKServices.responseVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class STKUserServicesVO {
    private int position;
    private String serviceID;
    private String minorVersion;
    private String majorVersion;
    private String label1;
    private String label2;
    private String status;
    private String description;
    private long offset;
    private long length;
}
