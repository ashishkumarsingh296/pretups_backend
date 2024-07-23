package com.restapi.superadmin.STKServices.requestVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushWmlRequestVO {
    private String length;
    private String position;
    private String offset;
    private String mobileNo;
    private String serviceSetID;
    private String label1;
    private String label2;
    private String wmlCode;
    private String bytecode;
    private String description;
}
