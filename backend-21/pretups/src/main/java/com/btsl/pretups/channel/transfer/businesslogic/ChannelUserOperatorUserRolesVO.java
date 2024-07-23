package com.btsl.pretups.channel.transfer.businesslogic;

/**
 * @author mohit.miglani
 *
 */
public class ChannelUserOperatorUserRolesVO {
private String parentName;
private String parentMsisdn;

private String ownerName;
private String ownerMsisdn;
private String catCode;
private String status;
private String userName;
private String loginId;
private String msisdn;

private String catName;
private String domainName;
private String grphCode;
private String roleName;
private String roleType;
public String getParentName() {
	return parentName;
}
public void setParentName(String parentName) {
	this.parentName = parentName;
}

public String getParentMsisdn() {
	return parentMsisdn;
}
public void setParentMsisdn(String parentMsisdn) {
	this.parentMsisdn = parentMsisdn;
}
public String getOwnerName() {
	return ownerName;
}
public void setOwnerName(String ownerName) {
	this.ownerName = ownerName;
}
public String getOwnerMsisdn() {
	return ownerMsisdn;
}
public void setOwnerMsisdn(String ownerMsisdn) {
	this.ownerMsisdn = ownerMsisdn;
}
public String getCatCode() {
	return catCode;
}
public void setCatCode(String catCode) {
	this.catCode = catCode;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}
public String getLoginId() {
	return loginId;
}
public void setLoginId(String loginId) {
	this.loginId = loginId;
}
public String getMsisdn() {
	return msisdn;
}
public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
}
public String getCatName() {
	return catName;
}
public void setCatName(String catName) {
	this.catName = catName;
}
public String getDomainName() {
	return domainName;
}
public void setDomainName(String domainName) {
	this.domainName = domainName;
}
public String getGrphCode() {
	return grphCode;
}
public void setGrphCode(String grphCode) {
	this.grphCode = grphCode;
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
@Override
public String toString() {
	return "ChannelUserOperatorUserRolesVO [parentName=" + parentName
			+ ", parentMsisdn=" + parentMsisdn + ", ownerName=" + ownerName
			+ ", ownerMsisdn=" + ownerMsisdn + ", parentName="
			+ parentName + ", catCode=" + catCode
			+ ", status=" +status + ", userName=" + userName
			+ ", loginId=" + loginId + ", msisdn=" + msisdn
			+ ", catName=" + catName + ", domainName="
			+ domainName + ", grphCode=" + grphCode + ", roleName=" + roleName + ", roleType=" + roleType+ "]";
}
}
