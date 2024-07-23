package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.ArrayList;
import java.util.HashMap;
/*
 * @(#)GroupedUserRolesVO.java
 * Traveling object for users roles grouped by role group
 */
public class GroupedUserRolesVO {
	private String roleType;
	private String roleTypeDesc;
	private HashMap groupRolesMap;
	private HashMap systemRolesMap;
	
    public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getRoleTypeDesc() {
		return roleTypeDesc;
	}
	public void setRoleTypeDesc(String roleTypeDesc) {
		this.roleTypeDesc = roleTypeDesc;
	}
	public HashMap getGroupRolesMap() {
		return groupRolesMap;
	}
	public void setGroupRolesMap(HashMap groupRolesMap) {
		this.groupRolesMap = groupRolesMap;
	}
	public HashMap getSystemRolesMap() {
		return systemRolesMap;
	}
	public void setSystemRolesMap(HashMap systemRolesMap) {
		this.systemRolesMap = systemRolesMap;
	}
	@Override
	public String toString() {
		return "GroupedUserRolesVO [roleType=" + roleType + ", roleTypeDesc=" + roleTypeDesc + ", groupRolesMap="
				+ groupRolesMap + ", systemRolesMap=" + systemRolesMap + "]";
	}	
	

}
