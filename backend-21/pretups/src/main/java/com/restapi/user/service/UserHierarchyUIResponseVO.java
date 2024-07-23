package com.restapi.user.service;

import java.util.List;

import com.btsl.common.BaseResponse;

public class UserHierarchyUIResponseVO extends BaseResponse
{
	int level;
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	UserHierarchyUIResponseData userHierarchyUIResponseData;

	public UserHierarchyUIResponseData getUserHierarchyUIResponseData() {
		return userHierarchyUIResponseData;
	}

	public void setUserHierarchyUIResponseData(UserHierarchyUIResponseData userHierarchyUIResponseData) {
		this.userHierarchyUIResponseData = userHierarchyUIResponseData;
	}
}
