package com.restapi.superadmin.requestVO;



public class GroupRoleManagementRequestVO {
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "DISTB_CHAN", required = true, description="Domain Type")
	private String domainType;
	@io.swagger.v3.oas.annotations.media.Schema(example = "AG", required = true, description="Category Code")
	private String categoryCode;
	@io.swagger.v3.oas.annotations.media.Schema(example = "AG03", required = true, description="Role Code")
	private String roleCode;
	@io.swagger.v3.oas.annotations.media.Schema(example = "AG03", required = true, description="Role Name")
	private String roleName;
	@io.swagger.v3.oas.annotations.media.Schema(example = "TESTAG03", required = true, description="Group Name")
	private String groupName;
	@io.swagger.v3.oas.annotations.media.Schema(example = "1", required = true, description="From Hour")
	private String fromHour;
	@io.swagger.v3.oas.annotations.media.Schema(example = "24", required = true, description="To Hour")
	private String toHour;
	@io.swagger.v3.oas.annotations.media.Schema(example = "Y/N", required = true, description="Default Group Role")
	private String defaultGroupRole;
	@io.swagger.v3.oas.annotations.media.Schema(example = "Y/N/S", required = true, description="Status")
	private String status;
	@io.swagger.v3.oas.annotations.media.Schema(example = "[UNBLOCKPINSTAFF]", required = true, description="Role Codes")
	private String[] rolesList;
	public String getDomainType() {
		return domainType;
	}
	public void setDomainType(String domainType) {
		this.domainType = domainType;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
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
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getFromHour() {
		return fromHour;
	}
	public void setFromHour(String fromHour) {
		this.fromHour = fromHour;
	}
	public String getToHour() {
		return toHour;
	}
	public void setToHour(String toHour) {
		this.toHour = toHour;
	}
	public String getDefaultGroupRole() {
		return defaultGroupRole;
	}
	public void setDefaultGroupRole(String defaultGroupRole) {
		this.defaultGroupRole = defaultGroupRole;
	}
	public String[] getRolesList() {
		return rolesList;
	}
	public void setRolesList(String[] rolesList) {
		this.rolesList = rolesList;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}
