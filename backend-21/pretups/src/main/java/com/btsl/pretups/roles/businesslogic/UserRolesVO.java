package com.btsl.pretups.roles.businesslogic;

import java.io.Serializable;

/**
 * @(#)UserRolesVO.java
 *                      Copyright(c) 2005, Bharti Telesoft Ltd.
 *                      All Rights Reserved
 * 
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Author Date History
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Mohit Goel 27/06/2005 Initial Creation
 * 
 *                      This class is used for User Roles Info
 * 
 */
public class UserRolesVO implements Serializable {

    private String _domainType;
    private String _categoryCode;
    private String _roleCode;
    private String _roleName;
    private String _groupName;
    private String _status;
    private String _statusDesc;
    private String _roleType;
    private String _roleTypeDesc;
    private String _fromHour;
    private String _toHour;
    private String _groupRole;

    // for Zebra and Tango by sanjeew date 06/07/07
    private String _applicationID;
    private String _gatewayTypes;
    // End Zebra and Tango

    private String _defaultType;
    private String _defaultTypeDesc;
    
//    By Piyush and Priyank
    private String _subgroupName;
    private String _subgroupRole;
    
    /**
     * @return Returns the domainType.
     */
    public String getDomainType() {
        return _domainType;
    }

    /**
     * @param domainType
     *            The domainType to set.
     */
    public void setDomainType(String domainType) {
        _domainType = domainType;
    }

    /**
     * @return Returns the categoryCode.
     */
    public String getCategoryCode() {
        return _categoryCode;
    }

    /**
     * @param categoryCode
     *            The categoryCode to set.
     */
    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    /**
     * @return Returns the formHour.
     */
    public String getFromHour() {
        return _fromHour;
    }

    /**
     * @param formHour
     *            The formHour to set.
     */
    public void setFromHour(String formHour) {
        _fromHour = formHour;
    }

    /**
     * @return Returns the groupName.
     */
    public String getGroupName() {
        return _groupName;
    }

    /**
     * @param groupName
     *            The groupName to set.
     */
    public void setGroupName(String groupName) {
        _groupName = groupName;
    }

    /**
     * @return Returns the roleCode.
     */
    public String getRoleCode() {
        return _roleCode;
    }

    /**
     * @param roleCode
     *            The roleCode to set.
     */
    public void setRoleCode(String roleCode) {
        _roleCode = roleCode;
    }

    /**
     * @return Returns the roleName.
     */
    public String getRoleName() {
        return _roleName;
    }

    /**
     * @param roleName
     *            The roleName to set.
     */
    public void setRoleName(String roleName) {
        _roleName = roleName;
    }

    /**
     * @return Returns the roleType.
     */
    public String getRoleType() {
        return _roleType;
    }

    /**
     * @param roleType
     *            The roleType to set.
     */
    public void setRoleType(String roleType) {
        _roleType = roleType;
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * @return Returns the toHour.
     */
    public String getToHour() {
        return _toHour;
    }

    /**
     * @param toHour
     *            The toHour to set.
     */
    public void setToHour(String toHour) {
        _toHour = toHour;
    }

    /**
     * @return Returns the statusDesc.
     */
    public String getStatusDesc() {
        return _statusDesc;
    }

    /**
     * @param statusDesc
     *            The statusDesc to set.
     */
    public void setStatusDesc(String statusDesc) {
        _statusDesc = statusDesc;
    }

    /**
     * @return Returns the roleTypeDesc.
     */
    public String getRoleTypeDesc() {
        return _roleTypeDesc;
    }

    /**
     * @param roleTypeDesc
     *            The roleTypeDesc to set.
     */
    public void setRoleTypeDesc(String roleTypeDesc) {
        _roleTypeDesc = roleTypeDesc;
    }

    /**
     * @return Returns the groupRole.
     */
    public String getGroupRole() {
        return _groupRole;
    }

    /**
     * @param groupRole
     *            The groupRole to set.
     */
    public void setGroupRole(String groupRole) {
        _groupRole = groupRole;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("DomainType=" + _domainType + ",");
        sb.append("RoleCode=" + _roleCode + ",");
        sb.append("RoleName=" + _roleName + ",");
        sb.append("Group Name=" + _groupName + ",");
        sb.append("Status=" + _status + ",");
        sb.append("Status Desc=" + _statusDesc + ",");
        sb.append("Role Type=" + _roleType + ",");
        sb.append("Role Type Desc=" + _roleTypeDesc + ",");
        sb.append("From Hour=" + _fromHour + ",");
        sb.append("To Hour=" + _toHour + ",");

        // for Zebra and Tango by sanjeew date 06/07/07
        sb.append("Application ID=" + _applicationID + ",");
        sb.append("Gateway Types =" + _gatewayTypes + ",");
        // End Zebra and Tango

        sb.append("Default Type =" + _defaultType + ",");
        sb.append("Default Type Desc =" + _defaultTypeDesc + ",");

        return sb.toString();
    }

    /**
     * @return Returns the applicationID.
     */
    public String getApplicationID() {
        return _applicationID;
    }

    /**
     * @param applicationID
     *            The applicationID to set.
     */
    public void setApplicationID(String applicationID) {
        _applicationID = applicationID;
    }

    /**
     * @return Returns the gatewayTypes.
     */
    public String getGatewayTypes() {
        return _gatewayTypes;
    }

    /**
     * @param gatewayTypes
     *            The gatewayTypes to set.
     */
    public void setGatewayTypes(String gatewayTypes) {
        _gatewayTypes = gatewayTypes;
    }

    /**
     * @return Returns the default type.
     */
    public String getDefaultType() {
        return _defaultType;
    }

    /**
     * @param default type The defaultType to set.
     */
    public void setDefaultType(String defaultType) {
        _defaultType = defaultType;
    }

    /**
     * @return Returns the defaultTypeDesc.
     */
    public String getDefaultTypeDesc() {
        return _defaultTypeDesc;
    }

    /**
     * @param defaultTypeDesc
     *            The defaultTypeDesc to set.
     */
    public void setDefaultTypeDesc(String defaultTypeDesc) {
        _defaultTypeDesc = defaultTypeDesc;
    }

	public String get_subgroupName() {
		return _subgroupName;
	}

	public void set_subgroupName(String _subgroupName) {
		this._subgroupName = _subgroupName;
	}

	public String get_subgroupRole() {
		return _subgroupRole;
	}

	public void set_subgroupRole(String _subgroupRole) {
		this._subgroupRole = _subgroupRole;
	}
    
    
}
