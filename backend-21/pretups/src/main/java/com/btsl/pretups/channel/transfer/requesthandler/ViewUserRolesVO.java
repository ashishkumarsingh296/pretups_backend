package com.btsl.pretups.channel.transfer.requesthandler;

/**
 * @(#)UserRoles.java
 * @author md.sohail
 *
 */
public class ViewUserRolesVO {
	
	private String roleCode;
	private String roleName;
	private String groupName;
	private String roleType;
	private String groupRole;
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getGroupRole() {
		return groupRole;
	}
	public void setGroupRole(String groupRole) {
		this.groupRole = groupRole;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	@Override
	public String toString() {
		return "ViewUserRolesVO [roleCode=" + roleCode + ", roleName=" + roleName + ", groupName=" + groupName
				+ ", roleType=" + roleType + ", groupRole=" + groupRole + "]";
	}
	

}
