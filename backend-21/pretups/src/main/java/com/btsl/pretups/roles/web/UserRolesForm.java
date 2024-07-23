package com.btsl.pretups.roles.web;

import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;

/*import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorActionForm;*/

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/**
 * @(#)UserRolesForm.java
 *                        Copyright(c) 2005, Bharti Telesoft Ltd.
 *                        All Rights Reserved
 * 
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Mohit Goel 17/08/2005 Initial Creation
 * 
 */
public class UserRolesForm /*extends ValidatorActionForm*/ {
    private Log _log = LogFactory.getLog(UserRolesForm.class.getName());
    private String _requestType = null;

    // for selectCategoryForRole.jsp
    private String _domainCode = null;
    private String _domainCodeDesc = null;
    private ArrayList _domainList;
    private String _categoryCode = null;
    private String _categoryCodeDesc = null;
    private ArrayList _categoryList;

    // for viewGroupRoleList.jsp
    // contain those roles where group_role = Y in roles table(created by user
    // through addGroupRole.jsp)
    private HashMap _groupRolesMap;
    private String _code = null;

    // for addGroupRole.jsp
    private String _domainType = null;
    private String _roleCode = null;
    private String _roleName = null;
    private String _groupName = null;
    private String _status = null;
    private String _statusDesc = null;
    private ArrayList _statusList;
    private String _roleTypeCode = null;
    private String _roleTypeCodeDesc = null;
    private ArrayList _roleTypeList;
    private String _fromHour = null;
    private String _toHour = null;
    // contain those roles where group_role = N in roles table(created by user
    // through back-end)
    private HashMap _rolesMap;
    private String[] _roleFlag;
    private HashMap _rolesMapSelected;

    private String _defaultType;
    private String _defaultTypeDesc;
    private String _defaultAllowed;

    public void flush() {
        _requestType = null;

        // for selectCategoryForRole.jsp
        _domainCode = null;
        _domainCodeDesc = null;
        _domainType = null;
        _domainList = null;
        _categoryCode = null;
        _categoryCodeDesc = null;
        _categoryList = null;

        // for viewGroupRoleList.jsp
        _groupRolesMap = null;
        _code = null;

        // for addGroupRole.jsp
        _roleCode = null;
        _roleName = null;
        _groupName = null;
        _status = null;
        _statusDesc = null;
        _statusList = null;
        _roleTypeCode = null;
        _roleTypeCodeDesc = null;
        _roleTypeList = null;
        _fromHour = null;
        _toHour = null;
        _rolesMap = null;
        _roleFlag = null;
        _rolesMapSelected = null;

        _defaultType = null;
        _defaultTypeDesc = null;
        _defaultAllowed = null;
    }

    public void semiFlush() {
        // for addGroupRole.jsp
        _roleCode = null;
        _roleName = null;
        _groupName = null;
        _status = null;
        _statusDesc = null;
        // _statusList = null;
        _roleTypeCode = null;
        _roleTypeCodeDesc = null;
        // _roleTypeList = null;
        _fromHour = null;
        _toHour = null;
        _rolesMap = null;
        _roleFlag = null;
        _rolesMapSelected = null;

    }

   /* public void reset(ActionMapping mapping, HttpServletRequest request) {
        // set the default value of the checkboxes,when user click on the
        // addRoles button
        if (request.getParameter("save") != null) {
            _roleFlag = null;
        }
    }*/

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
        if (categoryCode != null) {
            _categoryCode = categoryCode.trim();
        }
    }

    /**
     * @return Returns the categoryCodeDesc.
     */
    public String getCategoryCodeDesc() {
        return _categoryCodeDesc;
    }

    /**
     * @param categoryCodeDesc
     *            The categoryCodeDesc to set.
     */
    public void setCategoryCodeDesc(String categoryCodeDesc) {
        if (categoryCodeDesc != null) {
            _categoryCodeDesc = categoryCodeDesc.trim();
        }
    }

    /**
     * @return Returns the domainCode.
     */
    public String getDomainCode() {
        return _domainCode;
    }

    /**
     * @param domainCode
     *            The domainCode to set.
     */
    public void setDomainCode(String domainCode) {
        if (domainCode != null) {
            _domainCode = domainCode.trim();
        }
    }

    /**
     * @return Returns the domainCodeDesc.
     */
    public String getDomainCodeDesc() {
        return _domainCodeDesc;
    }

    /**
     * @param domainCodeDesc
     *            The domainCodeDesc to set.
     */
    public void setDomainCodeDesc(String domainCodeDesc) {
        if (domainCodeDesc != null) {
            _domainCodeDesc = domainCodeDesc.trim();
        }
    }

    /**
     * @return Returns the fromHour.
     */
    public String getFromHour() {
        return _fromHour;
    }

    /**
     * @param fromHour
     *            The fromHour to set.
     */
    public void setFromHour(String fromHour) {
        if (fromHour != null) {
            _fromHour = fromHour.trim();
        }
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
        if (groupName != null) {
            _groupName = groupName.trim();
        }
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
        if (roleCode != null) {
            _roleCode = roleCode.trim();
        }
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
        if (roleName != null) {
            _roleName = roleName.trim();
        }
    }

    /**
     * @return Returns the roleTypeCode.
     */
    public String getRoleTypeCode() {
        return _roleTypeCode;
    }

    /**
     * @param roleTypeCode
     *            The roleTypeCode to set.
     */
    public void setRoleTypeCode(String roleTypeCode) {
        if (roleTypeCode != null) {
            _roleTypeCode = roleTypeCode.trim();
        }
    }

    /**
     * @return Returns the roleTypeCodeDesc.
     */
    public String getRoleTypeCodeDesc() {
        return _roleTypeCodeDesc;
    }

    /**
     * @param roleTypeCodeDesc
     *            The roleTypeCodeDesc to set.
     */
    public void setRoleTypeCodeDesc(String roleTypeCodeDesc) {
        if (roleTypeCodeDesc != null) {
            _roleTypeCodeDesc = roleTypeCodeDesc.trim();
        }
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
        if (toHour != null) {
            _toHour = toHour.trim();
        }
    }

    /**
     * @return Returns the categoryList.
     */
    public ArrayList getCategoryList() {
        return _categoryList;
    }

    /**
     * @param categoryList
     *            The categoryList to set.
     */
    public void setCategoryList(ArrayList categoryList) {
        _categoryList = categoryList;
    }

    /**
     * @return Returns the domainList.
     */
    public ArrayList getDomainList() {
        return _domainList;
    }

    /**
     * @param domainList
     *            The domainList to set.
     */
    public void setDomainList(ArrayList domainList) {
        _domainList = domainList;
    }

    /**
     * @return Returns the rolesMap.
     */
    public HashMap getGroupRolesMap() {
        return _groupRolesMap;
    }

    /**
     * @param rolesMap
     *            The rolesMap to set.
     */
    public void setGroupRolesMap(HashMap rolesMap) {
        _groupRolesMap = rolesMap;
    }

    public int getGroupRolesMapCount() {
        if (_groupRolesMap != null && _groupRolesMap.size() > 0) {
            return _groupRolesMap.size();
        } else {
            return 0;
        }

    }

    /**
     * @return Returns the code.
     */
    public String getCode() {
        return _code;
    }

    /**
     * @param code
     *            The code to set.
     */
    public void setCode(String code) {
        if (code != null) {
            _code = code.trim();
        }
    }

    /**
     * @return Returns the roleTypeList.
     */
    public ArrayList getRoleTypeList() {
        return _roleTypeList;
    }

    /**
     * @param roleTypeList
     *            The roleTypeList to set.
     */
    public void setRoleTypeList(ArrayList roleTypeList) {
        _roleTypeList = roleTypeList;
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
        if (status != null) {
            _status = status.trim();
        }
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
        if (statusDesc != null) {
            _statusDesc = statusDesc.trim();
        }
    }

    /**
     * @return Returns the statusList.
     */
    public ArrayList getStatusList() {
        return _statusList;
    }

    /**
     * @param statusList
     *            The statusList to set.
     */
    public void setStatusList(ArrayList statusList) {
        _statusList = statusList;
    }

    /**
     * @return Returns the requestType.
     */
    public String getRequestType() {
        return _requestType;
    }

    /**
     * @param requestType
     *            The requestType to set.
     */
    public void setRequestType(String requestType) {
        if (requestType != null) {
            _requestType = requestType.trim();
        }
    }

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
        if (domainType != null) {
            _domainType = domainType.trim();
        }
    }

    /**
     * @return Returns the rolesMap.
     */
    public HashMap getRolesMap() {
        return _rolesMap;
    }

    /**
     * @param rolesMap
     *            The rolesMap to set.
     */
    public void setRolesMap(HashMap rolesMap) {
        _rolesMap = rolesMap;
    }

    public int getRolesMapCount() {
        if (_rolesMap != null && _rolesMap.size() > 0) {
            return _rolesMap.size();
        } else {
            return 0;
        }

    }

    /**
     * @return Returns the roleFlag.
     */
    public String[] getRoleFlag() {
        return _roleFlag;
    }

    /**
     * @param roleFlag
     *            The roleFlag to set.
     */
    public void setRoleFlag(String[] roleFlag) {
        _roleFlag = roleFlag;
    }

    /**
     * @return Returns the rolesMapSelected.
     */
    public HashMap getRolesMapSelected() {
        return _rolesMapSelected;
    }

    /**
     * @param rolesMapSelected
     *            The rolesMapSelected to set.
     */
    public void setRolesMapSelected(HashMap rolesMapSelected) {
        _rolesMapSelected = rolesMapSelected;
    }

    public int getRolesMapSelectedCount() {
        if (_rolesMapSelected != null && _rolesMapSelected.size() > 0) {
            return _rolesMapSelected.size();
        } else {
            return 0;
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("DomainType=" + _domainType + ",");
        sb.append("RoleCode=" + _roleCode + ",");
        sb.append("RoleName=" + _roleName + ",");
        sb.append("Group Name=" + _groupName + ",");
        sb.append("Status=" + _status + ",");
        sb.append("Status Desc=" + _statusDesc + ",");
        sb.append("Role Type Code=" + _roleTypeCode + ",");
        sb.append("Role Type Code Desc=" + _roleTypeCodeDesc + ",");
        sb.append("From Hour=" + _fromHour + ",");
        sb.append("To Hour=" + _toHour + ",");

        sb.append("Default Type =" + _defaultType + ",");

        return sb.toString();
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

    /**
     * @return Returns the defaultAllowed.
     */
    public String getDefaultAllowed() {
        return _defaultAllowed;
    }

    /**
     * @param defaultAllowed
     *            The defaultAllowed to set.
     */
    public void setDefaultAllowed(String defaultAllowed) {
        _defaultAllowed = defaultAllowed;
    }

}
