package com.restapi.user.service;

import java.util.List;

import com.btsl.common.BaseResponse;

public class UserHierarchyCAUIResponseVO extends BaseResponse
{
	int level;
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	List<UserHierarchyUIResponseData> userHierarchyUIResponseData;

	public List<UserHierarchyUIResponseData> getUserHierarchyUIResponseData() {
		return userHierarchyUIResponseData;
	}

	public void setUserHierarchyUIResponseData(List<UserHierarchyUIResponseData> userHierarchyUIResponseData) {
		this.userHierarchyUIResponseData = userHierarchyUIResponseData;
	}
}
