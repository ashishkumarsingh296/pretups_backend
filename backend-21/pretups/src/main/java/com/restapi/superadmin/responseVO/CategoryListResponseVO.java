package com.restapi.superadmin.responseVO;

import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BaseResponse;

public class CategoryListResponseVO extends BaseResponse {

	private ArrayList categoryList;
	private String userType;
	private String domainName;
	private String domainTypeCode;
	private HashMap<String, ArrayList> systemRoleMap;
	private HashMap<String, ArrayList> groupRoleMap;
	private ArrayList messageGatewayList;
	private boolean UserPrefixIdDisableinModify;
	private boolean hideAddButton;

	public ArrayList getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList categoryList) {
		this.categoryList = categoryList;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getDomainTypeCode() {
		return domainTypeCode;
	}

	public void setDomainTypeCode(String domainTypeCode) {
		this.domainTypeCode = domainTypeCode;
	}

	public HashMap<String, ArrayList> getSystemRoleMap() {
		return systemRoleMap;
	}

	public void setSystemRoleMap(HashMap<String, ArrayList> systemRoleMap) {
		this.systemRoleMap = systemRoleMap;
	}

	public HashMap<String, ArrayList> getGroupRoleMap() {
		return groupRoleMap;
	}

	public void setGroupRoleMap(HashMap<String, ArrayList> groupRoleMap) {
		this.groupRoleMap = groupRoleMap;
	}

	public ArrayList getMessageGatewayList() {
		return messageGatewayList;
	}

	public void setMessageGatewayList(ArrayList messageGatewayList) {
		this.messageGatewayList = messageGatewayList;
	}

	public boolean isUserPrefixIdDisableinModify() {
		return UserPrefixIdDisableinModify;
	}

	public void setUserPrefixIdDisableinModify(boolean userPrefixIdDisableinModify) {
		UserPrefixIdDisableinModify = userPrefixIdDisableinModify;
	}

	public boolean isHideAddButton() {
		return hideAddButton;
	}

	public void setHideAddButton(boolean hideAddButton) {
		this.hideAddButton = hideAddButton;
	}

}
