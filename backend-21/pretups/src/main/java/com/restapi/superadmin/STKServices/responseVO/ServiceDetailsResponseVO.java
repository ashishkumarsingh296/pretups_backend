package com.restapi.superadmin.STKServices.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceDetailsResponseVO extends BaseResponse {
    private String[] selectedUserTypeCategoryList;
    private String serviceID;
    private String serviceSetID;
    private String label1;
    private String label2;
    private String wmlCode;
    private String description;
    private String majorVersion;
    private String minorVersion;
    private String byteCode;
}
