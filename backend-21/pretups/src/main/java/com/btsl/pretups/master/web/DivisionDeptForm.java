/*
 * #DivisionDeptForm.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Aug 4, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.master.web;

import java.util.ArrayList;
/*import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorActionForm;*/

public class DivisionDeptForm /*extends ValidatorActionForm*/ {
    private String _divDeptId;
    private String _divDeptName = null;
    private String _divDeptShortCode = null;
    private String _status;
    private String _divDept;
    private String _divDeptType;
    private String _createdOn;
    private String _createdBy;
    private String _modifiedOn;
    private String _modifiedBy;
    private String _parentId;
    private String _networkCode;
    private String _networkName;
    private String _userId;
    private long _lastModified;
    private ArrayList _divisionList;
    private ArrayList _divisionTypeList;
    private ArrayList _departmentList;
    private String _modifyFlag;
    private int _radioIndex;
    private String _divisionName;
    private String _requestFrom;
    private String _divisionId;
    private ArrayList _statusList;
    private String _statusName;
    private String _loginUserName;
    private String _userType;
    private String _divDeptTypeName;

    private String _divType;
    private String _divName;

    /**
     * @return Returns the divName.
     */
    public String getDivName() {
        return _divName;
    }

    /**
     * @param divName
     *            The divName to set.
     */
    public void setDivName(String divName) {
        _divName = divName;
    }

    /**
     * @return Returns the divType.
     */
    public String getDivType() {
        return _divType;
    }

    /**
     * @param divType
     *            The divType to set.
     */
    public void setDivType(String divType) {
        _divType = divType;
    }

    public int getDivisionListSize() {
        if (_divisionList != null && ! _divisionList.isEmpty()) {
            return 1;
        } else {
            return 0;
        }
    }

    public int getDepartmentListSize() {
        if (_departmentList != null && !_departmentList.isEmpty()) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * To get the value of divDeptTypeName field
     * 
     * @return divDeptTypeName.
     */
    public String getDivDeptTypeName() {
        return _divDeptTypeName;
    }

    /**
     * To set the value of divDeptTypeName field
     */
    public void setDivDeptTypeName(String divDeptTypeName) {
        _divDeptTypeName = divDeptTypeName;
    }

    /**
     * To get the value of userType field
     * 
     * @return userType.
     */
    public String getUserType() {
        return _userType;
    }

    /**
     * To set the value of userType field
     */
    public void setUserType(String userType) {
        _userType = userType;
    }

    /**
     * To get the value of loginUserName field
     * 
     * @return loginUserName.
     */
    public String getLoginUserName() {
        return _loginUserName;
    }

    /**
     * To set the value of loginUserName field
     */
    public void setLoginUserName(String loginUserName) {
        _loginUserName = loginUserName;
    }

    /**
     * To get the value of statusName field
     * 
     * @return statusName.
     */
    public String getStatusName() {
        return _statusName;
    }

    /**
     * To set the value of statusName field
     */
    public void setStatusName(String statusName) {
        _statusName = statusName;
    }

    /**
     * To get the value of statusList field
     * 
     * @return statusList.
     */
    public ArrayList getStatusList() {
        return _statusList;
    }

    /**
     * To set the value of statusList field
     */
    public void setStatusList(ArrayList statusList) {
        _statusList = statusList;
    }

    /**
     * To get the value of divisionId field
     * 
     * @return divisionId.
     */
    public String getDivisionId() {
        return _divisionId;
    }

    /**
     * To set the value of divisionId field
     */
    public void setDivisionId(String divisionId) {
        _divisionId = divisionId;
    }

    /**
     * To get the value of requestFrom field
     * 
     * @return requestFrom.
     */
    public String getRequestFrom() {
        return _requestFrom;
    }

    /**
     * To set the value of requestFrom field
     */
    public void setRequestFrom(String requestFrom) {
        _requestFrom = requestFrom;
    }

    /**
     * To get the value of divisionName field
     * 
     * @return divisionName.
     */
    public String getDivisionName() {
        return _divisionName;
    }

    /**
     * To set the value of divisionName field
     */
    public void setDivisionName(String divisionName) {
        _divisionName = divisionName;
    }

    /**
     * To get the value of departmentList field
     * 
     * @return departmentList.
     */
    public ArrayList getDepartmentList() {
        return _departmentList;
    }

    /**
     * To set the value of departmentList field
     */
    public void setDepartmentList(ArrayList departmentList) {
        _departmentList = departmentList;
    }

    /**
     * To get the value of divDeptType field
     * 
     * @return divDeptType.
     */
    public String getDivDeptType() {
        return _divDeptType;
    }

    /**
     * To set the value of divDeptType field
     */
    public void setDivDeptType(String divDeptType) {
        _divDeptType = divDeptType;
    }

    /**
     * To get the value of radioIndex field
     * 
     * @return radioIndex.
     */
    public int getRadioIndex() {
        return _radioIndex;
    }

    /**
     * To set the value of radioIndex field
     */
    public void setRadioIndex(int radioIndex) {
        _radioIndex = radioIndex;
    }

    /**
     * To get the value of networkName field
     * 
     * @return networkName.
     */
    public String getNetworkName() {
        return _networkName;
    }

    /**
     * To set the value of networkName field
     */
    public void setNetworkName(String networkName) {
        _networkName = networkName;
    }

    /**
     * To get the value of modifyFlag field
     * 
     * @return modifyFlag.
     */
    public String getModifyFlag() {
        return _modifyFlag;
    }

    /**
     * To set the value of modifyFlag field
     */
    public void setModifyFlag(String modifyFlag) {
        _modifyFlag = modifyFlag;
    }

    /**
     * To get the value of divisionList field
     * 
     * @return divisionList.
     */
    public ArrayList getDivisionList() {
        return _divisionList;
    }

    /**
     * To set the value of divisionList field
     */
    public void setDivisionList(ArrayList divisionList) {
        _divisionList = divisionList;
    }

    /**
     * To get the value of createdBy field
     * 
     * @return createdBy.
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * To set the value of createdBy field
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * To get the value of createdOn field
     * 
     * @return createdOn.
     */
    public String getCreatedOn() {
        return _createdOn;
    }

    /**
     * To set the value of createdOn field
     */
    public void setCreatedOn(String createdOn) {
        _createdOn = createdOn;
    }

    /**
     * To get the value of divDept field
     * 
     * @return divDept.
     */
    public String getDivDept() {
        return _divDept;
    }

    /**
     * To set the value of divDept field
     */
    public void setDivDept(String divDept) {
        _divDept = divDept;
    }

    /**
     * To get the value of divDeptId field
     * 
     * @return divDeptId.
     */
    public String getDivDeptId() {
        return _divDeptId;
    }

    /**
     * To set the value of divDeptId field
     */
    public void setDivDeptId(String divDeptId) {
        _divDeptId = divDeptId;
    }

    /**
     * To get the value of divDeptName field
     * 
     * @return divDeptName.
     */
    public String getDivDeptName() {
        return _divDeptName;
    }

    /**
     * To set the value of divDeptName field
     */
    public void setDivDeptName(String divDeptName) {
        if (divDeptName != null) {
            _divDeptName = divDeptName.trim();
        }
    }

    /**
     * To get the value of divDeptShortCode field
     * 
     * @return divDeptShortCode.
     */
    public String getDivDeptShortCode() {
        return _divDeptShortCode;
    }

    /**
     * To set the value of divDeptShortCode field
     */
    public void setDivDeptShortCode(String divDeptShortCode) {
        if (divDeptShortCode != null) {
            _divDeptShortCode = divDeptShortCode.trim();
        }
    }

    /**
     * To get the value of lastModified field
     * 
     * @return lastModified.
     */
    public long getLastModified() {
        return _lastModified;
    }

    /**
     * To set the value of lastModified field
     */
    public void setLastModified(long lastModified) {
        _lastModified = lastModified;
    }

    /**
     * To get the value of modifiedBy field
     * 
     * @return modifiedBy.
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * To set the value of modifiedBy field
     */
    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    /**
     * To get the value of modifiedOn field
     * 
     * @return modifiedOn.
     */
    public String getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * To set the value of modifiedOn field
     */
    public void setModifiedOn(String modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * To get the value of networkCode field
     * 
     * @return networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * To set the value of networkCode field
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * To get the value of parentId field
     * 
     * @return parentId.
     */
    public String getParentId() {
        return _parentId;
    }

    /**
     * To set the value of parentId field
     */
    public void setParentId(String parentId) {
        _parentId = parentId;
    }

    /**
     * To get the value of status field
     * 
     * @return status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * To set the value of status field
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * To get the value of userId field
     * 
     * @return userId.
     */
    public String getUserId() {
        return _userId;
    }

    /**
     * To set the value of userId field
     */
    public void setUserId(String userId) {
        _userId = userId;
    }

    /**
     * To get the value of divisionTypeList field
     * 
     * @return divisionTypeList.
     */
    public ArrayList getDivisionTypeList() {
        return _divisionTypeList;
    }

    /**
     * To set the value of divisionTypeList field
     */
    public void setDivisionTypeList(ArrayList divisionTypeList) {
        _divisionTypeList = divisionTypeList;
    }

    /**
     * Method validate.
     * This method is used to ignore the validation if the user clicks back
     * button.
     * 
     * @param mapping
     *            ActionMapping
     * @param request
     *            HttpServletRequest
     * @return ActionErrors
     */

    public void semiFlush() {
        _divDeptName = null;
        _divDeptShortCode = null;
        _statusList = null;
        _status = null;
        _statusName = null;
        // _divDeptType=null;
    }

    public void flush() {
        _divDeptId = null;
        _divDeptName = null;
        _divDeptShortCode = null;
        _status = null;
        _divDept = null;
        _divDeptType = null;
        _createdOn = null;
        _createdBy = null;
        _modifiedOn = null;
        _modifiedBy = null;
        _parentId = null;
        _networkCode = null;
        _networkName = null;
        _userId = null;
        _divisionList = null;
        _departmentList = null;
        _modifyFlag = null;
        _divisionName = null;
        _requestFrom = null;
        _divisionId = null;
        _divisionTypeList = null;
        _divDeptTypeName = null;
        _divType = null;
        _divName = null;
    }
}
