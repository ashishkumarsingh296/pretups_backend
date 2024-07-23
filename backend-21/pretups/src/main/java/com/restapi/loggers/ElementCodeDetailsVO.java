package com.restapi.loggers;

import java.util.HashMap;

public class ElementCodeDetailsVO {

	private String roleCode;
	private HashMap<String , String> groupNameMap;
	public HashMap<String, String> getGroupNameMap() {
		return groupNameMap;
	}
	public void setGroupNameMap(HashMap<String, String> groupNameMap) {
		this.groupNameMap = groupNameMap;
	}
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ElementCodeDetailsVO [roleCode=");
		builder.append(roleCode);
		builder.append(", groupNameMap=");
		builder.append(groupNameMap);
		builder.append("]");
		return builder.toString();
	}
}
