package com.restapi.channelAdmin.restrictedlistmgmt.responseVO;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class UserValidationResponseVO extends BaseResponse {

	
	private String userName;
	private String userID;
	private String ownerID;
	private String ownerName;
	private String ownerCategoryName;
	private String categoryName;
	
	
}
