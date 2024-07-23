package com.restapi.networkadmin.ICCIDIMSIkeymanagement.responseVO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDetailsVO {
    private String userName;
    private String address1;
    private String address2;
    private String categoryName;
    private String status;
    private String parentName;
    private String parentMobileNo;
    private String parentCategoryName;
    private String ownerName;
    private String ownerCategoryName;
    private String ownerMobileNo;
    private String smsPin;
    private String pinRequired;
    private String loginId;

}
