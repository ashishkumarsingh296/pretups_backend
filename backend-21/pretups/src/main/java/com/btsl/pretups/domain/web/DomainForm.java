/*
 * #DomainForm.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jul 28, 2005 Amit Ruwali Initial creation
 * Aug 22,2005 Manoj Kumar Modified By
 * Nov 19,2005 Manoj Kumar Modified By
 * Change #1 for file TelesoftPreTUPsv5.0-TestlabTest record sheet.xls, the bug
 * no.-484,522,524 on 13/10/06 by Amit Singh
 * 25 nov 2006 ved prakash sharma For Shoho changes
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.domain.web;

import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;

/*import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorActionForm;*/

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.util.BTSLUtil;

public class DomainForm /*extends ValidatorActionForm*/ {
    // ***********Domains
    private String _domainCodeforDomain = null;
    private String _dCode;
    private String _cCode;
    private String _domainName = null;
    private String _domainTypeCode;
    private String _domainTypeName;
    private String _ownerCategory;
    private String _domainStatus;
    private String _createdOn;
    private String _createdBy;
    private String _modifiedOn;
    private String _modifiedBy;
    private String _numberOfCategories;
    private String _oldNumberofCategories;
    private ArrayList _domainList;
    private ArrayList _domainTypeList;
    private ArrayList _domainStatusList;
    private long _lastModifiedTime;
    private String _statusTypeName;
    private int _radioIndex;
    private String _modifyFlag;
    private ArrayList _searchDomainList;
    private ArrayList _messageGatewayTypeList;
    private String _geographicalDomainName;
    private String _roleTypeName;
    // Categories
    private String _categoryCode;
    private String _categoryName = null;
    private String _domainCodeforCategory;
    private String _sequenceNumber;
    private String _grphDomainType;
    private String _multipleGrphDomains;
    private String _webInterfaceAllowed;
    private String _smsInterfaceAllowed;
    private String _fixedRoles;
    private String _multipleLoginAllowed;
    private String _viewOnNetworkBlock;
    private String _maxLoginCount;
    private String _categoryStatus;
    private String _displayAllowed;
    private String _modifyAllowed;
    private String _productTypeAssociationAllowed;
    private String _maxTxnMsisdn;
    private String _maxTxnMsisdnOld;
    private String _unctrlTransferAllowed;
    private String _scheduledTransferAllowed;
    private String _serviceAllowed;
    private String _outletsAllowed;
    private String _restrictedMsisdns;
    private String _hierarchyAllowed;
    private String _agentAllowed;
    private String _categoryType;
    private String _parentCategoryCode;
    private String _txnOutsideHierchy;
    private String _userIdPrefix;
    private ArrayList _geographicalDomainList;
    private ArrayList _roleTypeList;
    private ArrayList _categoryStatusList;
    private ArrayList _categoryList;
    private String _domainNameSearched;
    private String _categorySearched;
    private ArrayList _categorySearchList;
    private ArrayList _modifiedMessageGatewayList;
    private String _transferToListOnly;
    // add by santanu
    private String _rechargeByParentOnly;
    private String _cp2pPayer;
    private String _cp2pPayee;
    private String _cp2pWithinList;
    private String _listLevelCode;
    private String _listLevelType;
    private ArrayList _p2pWithinLevelList;
    private String _userType;
    // for agent
    // add by santanu for category listManagement
    private String _agentRechargeByParentOnly;
    private String _agentCp2pPayer;
    private String _agentCp2pPayee;
    private String _agentCp2pWithinList;
    private String _agentListLevelType;
    private String _agentListLevelCode;
    // Grade
    private String _gradeCode;
    private String _gradeName = null;
    private ArrayList _gradeList;
    private String _networkName;
    private String _networkCode;
    private String _checkArray[];
    private HashMap _groupRolesMap;

    // for User roles assignCategoryRoles.jsp
    private HashMap _rolesMap;// used on jsp "assignRoles.jsp" to show all roles
                              // info
    private HashMap _rolesMapSelected;// used on jsp "addOperator.jsp" to show
                                      // the only assign roles info
    private String[] _roleFlag;// store the role codes that are assigned to the
                               // use

    private String _userExistsFlag;
    private String _agentUserExistsFlag;
    private String _geoDomainName;

    private int _categorySequenceNumber;

    // Agent Category Fields:

    private String _agentGeographicalDomainList;
    private ArrayList _agentRoleTypeList;
    private ArrayList _agentCategoryStatusList;
    private String _agentMultipleGrphDomains;
    private String _agentProductTypeAssociationAllowed;
    private String _agentDisplayAllowed;
    private String _agentModifyAllowed;
    private String _agentAgentAllowed;
    private String _agentCategoryType;
    private String _agentDomainCodeforCategory;
    private String _agentDomainName;
    private String _agentCategoryName;
    private String _agentCategoryCode;
    private String _agentGrphDomainType;
    private String _agentFixedRoles;
    private String _agentCategoryStatus;
    private String _agentUserIdPrefix;
    private String _agentOutletsAllowed;
    private HashMap _agentRolesMapSelected;
    private String _agentRoleName;
    private ArrayList _agentMessageGatewayTypeList;
    private ArrayList _agentModifiedMessageGatewayTypeList;
    private String _agentGatewayName;
    private String _agentCheckArray[];
    private String _agentGatewayType;
    private String _agentMultipleLoginAllowed;
    private String _agentViewOnNetworkBlock;
    private String _agentRestrictedMsisdns;
    private String _agentScheduledTransferAllowed;
    private String _agentUnctrlTransferAllowed;
    private String _agentServiceAllowed;
    private String _agentMaxLoginCount;
    private String _agentMaxTxnMsisdn;
    private String _agentHierarchyAllowed;
    private String _agentAllowedFlag;
    private String _agentTransferToListOnly;

    // for User roles for Agent Category
    private HashMap _agentRolesMap;// used on jsp "assignRoles.jsp" to show all
                                   // roles info
    private String[] _agentRoleFlag;// store the role codes that are assigned to
                                    // the use

    private String _agentWebInterfaceAllowed;
    private String _agentSmsInterfaceAllowed;

    // used to keep track if agent allowed is changed or not.
    private String _previousAgentAllowedFlag;
    // used to keep track the operation performed
    private String _operationPerformed;
    private String _activeAgentExist;

    // used for view category
    private HashMap _categoryMap;
    private String _catIndex;

    private boolean _assignRoleCallInter;

    private String _agentStatusTypeName;
    private String _agentGeoDomainName;
    private String _agentRoleTypeName;

    private ArrayList _agentGoeList;

    // added by AZ on 08/06/06 for CR00043
    // this parameter holds either hierarchy exists for the user of category or
    // not.
    private boolean _isUserHierarchyExists;
    private boolean _isAgentUserHierarchyExists;

    // added by AZ on 19/06/06 for CR00043
    // this parameter holds either uncontrolled transfer allowed is Y for the
    // category or not.
    private boolean _isUncontrolledTransferFlag;
    private boolean _isAgentUncontrolledTransferFlag;
    private boolean _isAgentAllow = false;
    private CategoryVO _categoryVO;

    // added on 10/06/07 for low balance alert
    private String _lowBalanceAlertAllow;
    // Added on 13/07/07 for Low balance alert allow
    private String _agentLowBalanceAlertAllow;
    private boolean _isLowBalAlertUserExists = false;
    private boolean _isAgentLowBalAlertUserExists = false;
    // added by nilesh
    private String _defaultGrade;
    private int _gradeListSize;
    private String _gradeDef;
    private String _gradeDefModify;
    // ADDed for Authentication Type
    private String _authType;
    private ArrayList _authTypeList;
    private String _authTypeName;
    //added for 2FA
    private String _twoFAallowed;
   
    // end
    //2FA start
    public String getTwoFAallowed() {
        return _twoFAallowed;
    }

    public void setTwoFAallowed(String twoFAallowed) {
        this._twoFAallowed = twoFAallowed;
    }
    
    //2FA ends
    /**
     * 
     * @return Returns the categoryVO.
     */
    public CategoryVO getCategoryVO() {
        return this._categoryVO;
    }

    /**
     * @param categoryVO
     *            The categoryVO to set.
     */
    public void setCategoryVO(CategoryVO categoryVO) {
        this._categoryVO = categoryVO;
    }

    /**
     * @return Returns the isAgentAllowFlag.
     */
    public boolean isAgentAllow() {
        return this._isAgentAllow;
    }

    /**
     * @param isAgentAllowFlag
     *            The isAgentAllowFlag to set.
     */
    public void setAgentAllow(boolean isAgentAllowFlag) {
        this._isAgentAllow = isAgentAllowFlag;
    }

    /**
     * @return Returns the transferToListOnly.
     */
    public String getTransferToListOnly() {
        return this._transferToListOnly;
    }

    /**
     * @param transferToListOnly
     *            The transferToListOnly to set.
     */
    public void setTransferToListOnly(String transferToListOnly) {
        this._transferToListOnly = transferToListOnly;
    }

    /**
     * @return Returns the agentTransferToListOnly.
     */
    public String getAgentTransferToListOnly() {
        return this._agentTransferToListOnly;
    }

    /**
     * @param agentTransferToListOnly
     *            The agentTransferToListOnly to set.
     */
    public void setAgentTransferToListOnly(String agentTransferToListOnly) {
        this._agentTransferToListOnly = agentTransferToListOnly;
    }

    /**
     * @return Returns the agentSmsInterfaceAllowed.
     */
    public String getAgentSmsInterfaceAllowed() {
        return _agentSmsInterfaceAllowed;
    }

    /**
     * @param agentSmsInterfaceAllowed
     *            The agentSmsInterfaceAllowed to set.
     */
    public void setAgentSmsInterfaceAllowed(String agentSmsInterfaceAllowed) {
        _agentSmsInterfaceAllowed = agentSmsInterfaceAllowed;
    }

    /**
     * @return Returns the agentWebInterfaceAllowed.
     */
    public String getAgentWebInterfaceAllowed() {
        return _agentWebInterfaceAllowed;
    }

    /**
     * @param agentWebInterfaceAllowed
     *            The agentWebInterfaceAllowed to set.
     */
    public void setAgentWebInterfaceAllowed(String agentWebInterfaceAllowed) {
        _agentWebInterfaceAllowed = agentWebInterfaceAllowed;
    }

    /**
     * @return Returns the agentModifiedMessageGatewayTypeList.
     */
    public ArrayList getAgentModifiedMessageGatewayTypeList() {
        return _agentModifiedMessageGatewayTypeList;
    }

    /**
     * @param agentModifiedMessageGatewayTypeList
     *            The agentModifiedMessageGatewayTypeList to set.
     */
    public void setAgentModifiedMessageGatewayTypeList(ArrayList agentModifiedMessageGatewayTypeList) {
        _agentModifiedMessageGatewayTypeList = agentModifiedMessageGatewayTypeList;
    }

    /**
     * @return Returns the agentRoleFlag.
     */
    public String[] getAgentRoleFlag() {
        return _agentRoleFlag;
    }

    /**
     * @param agentRoleFlag
     *            The agentRoleFlag to set.
     */
    public void setAgentRoleFlag(String[] agentRoleFlag) {
        _agentRoleFlag = agentRoleFlag;
    }

    /**
     * @return Returns the agentRolesMap.
     */
    public HashMap getAgentRolesMap() {
        return _agentRolesMap;
    }

    /**
     * @param agentRolesMap
     *            The agentRolesMap to set.
     */
    public void setAgentRolesMap(HashMap agentRolesMap) {
        _agentRolesMap = agentRolesMap;
    }

    /**
     * @return Returns the agentAllowedFlag.
     */
    public String getAgentAllowedFlag() {
        return _agentAllowedFlag;
    }

    /**
     * @param agentAllowedFlag
     *            The agentAllowedFlag to set.
     */
    public void setAgentAllowedFlag(String agentAllowedFlag) {
        _agentAllowedFlag = agentAllowedFlag;
    }

    /**
     * @return Returns the categoryType.
     */
    public String getCategoryType() {
        return _categoryType;
    }

    /**
     * @param categoryType
     *            The categoryType to set.
     */
    public void setCategoryType(String categoryType) {
        _categoryType = categoryType;
    }

    /**
     * @return Returns the agentAllowed.
     */
    public String getAgentAllowed() {
        return _agentAllowed;
    }

    /**
     * @param agentAllowed
     *            The agentAllowed to set.
     */
    public void setAgentAllowed(String agentAllowed) {
        _agentAllowed = agentAllowed;
    }

    /**
     * @return Returns the hierarchyAllowed.
     */
    public String getHierarchyAllowed() {
        return _hierarchyAllowed;
    }

    /**
     * @param hierarchyAllowed
     *            The hierarchyAllowed to set.
     */
    public void setHierarchyAllowed(String hierarchyAllowed) {
        _hierarchyAllowed = hierarchyAllowed;
    }

    /**
     * @return Returns the outletsAllowed.
     */
    public String getOutletsAllowed() {
        return _outletsAllowed;
    }

    /**
     * @param outletsAllowed
     *            The outletsAllowed to set.
     */
    public void setOutletsAllowed(String outletsAllowed) {
        _outletsAllowed = outletsAllowed;
    }

    /**
     * To get the value of categorySequenceNumber field
     * 
     * @return categorySequenceNumber.
     */
    public int getCategorySequenceNumber() {
        return _categorySequenceNumber;
    }

    /**
     * To set the value of categorySequenceNumber field
     */
    public void setCategorySequenceNumber(int categorySequenceNumber) {
        _categorySequenceNumber = categorySequenceNumber;
    }

    /**
     * To get the value of geoDomainName field
     * 
     * @return geoDomainName.
     */
    public String getGeoDomainName() {
        return _geoDomainName;
    }

    /**
     * To set the value of geoDomainName field
     */
    public void setGeoDomainName(String geoDomainName) {
        _geoDomainName = geoDomainName;
    }

    /**
     * To get the value of maxTxnMsisdnOld field
     * 
     * @return maxTxnMsisdnOld.
     */
    public String getMaxTxnMsisdnOld() {
        return _maxTxnMsisdnOld;
    }

    /**
     * To set the value of maxTxnMsisdnOld field
     */
    public void setMaxTxnMsisdnOld(String maxTxnMsisdnOld) {
        _maxTxnMsisdnOld = maxTxnMsisdnOld;
    }

    /**
     * To get the value of userExistsFlag field
     * 
     * @return userExistsFlag.
     */
    public String getUserExistsFlag() {
        return _userExistsFlag;
    }

    /**
     * To set the value of userExistsFlag field
     */
    public void setUserExistsFlag(String userExistsFlag) {
        _userExistsFlag = userExistsFlag;
    }

    /**
     * @return Returns the systemRolesMap.
     */
    public HashMap getGroupRolesMap() {
        return _groupRolesMap;
    }

    /**
     * @param systemRolesMap
     *            The systemRolesMap to set.
     */
    public void setGroupRolesMap(HashMap groupRolesMap) {
        _groupRolesMap = groupRolesMap;
    }

    /**
     * To get the value of oldNumberofCategories field
     * 
     * @return oldNumberofCategories.
     */
    public String getOldNumberofCategories() {
        return _oldNumberofCategories;
    }

    /**
     * To set the value of oldNumberofCategories field
     */
    public void setOldNumberofCategories(String oldNumberofCategories) {
        _oldNumberofCategories = oldNumberofCategories;
    }

    /**
     * @return Returns the serviceAllowed.
     */
    public String getServiceAllowed() {
        return _serviceAllowed;
    }

    /**
     * @param serviceAllowed
     *            The serviceAllowed to set.
     */
    public void setServiceAllowed(String serviceAllowed) {
        _serviceAllowed = serviceAllowed;
    }

    /**
     * @return Returns the checkArray.
     */
    public String[] getCheckArray() {
        return _checkArray;
    }

    /**
     * @param checkArray
     *            The checkArray to set.
     */
    public void setCheckArray(String[] checkArray) {
        _checkArray = checkArray;
    }

    public boolean getAddShowButton() {
        if (_categoryList != null && !_categoryList.isEmpty()) {
            CategoryVO catVO = (CategoryVO) _categoryList.get(0);
            int maxCount = catVO.getRecordCount();
            if (maxCount > _categoryList.size()) {
                return true;
            } else {
                return false;
            }

        } else {
            return true;
        }
    }

    public void setMessageGatewayTypeListIndexed(int i, MessageGatewayVO vo) {
        _messageGatewayTypeList.set(i, vo);
    }

    public MessageGatewayVO getMessageGatewayTypeListIndexed(int i) {

        return (MessageGatewayVO) _messageGatewayTypeList.get(i);
    }

    public MessageGatewayVO getSelectedMessageGatewayTypeListIndexed(int i) {

        return (MessageGatewayVO) _modifiedMessageGatewayList.get(i);
    }

    public void setSelectedMessageGatewayTypeListIndexed(int i, MessageGatewayVO vo) {
        _modifiedMessageGatewayList.set(i, vo);
    }

    /**
     * @return Returns the modifiedMessageGatewayList.
     */
    public ArrayList getModifiedMessageGatewayList() {
        return _modifiedMessageGatewayList;
    }

    /**
     * @param modifiedMessageGatewayList
     *            The modifiedMessageGatewayList to set.
     */
    public void setModifiedMessageGatewayList(ArrayList modifiedMessageGatewayList) {
        _modifiedMessageGatewayList = modifiedMessageGatewayList;
    }

    /**
     * To get the value of categoryCode field
     * 
     * @return categoryCode.
     */
    public String getCategoryCode() {
        return _categoryCode;
    }

    /**
     * To set the value of categoryCode field
     */
    public void setCategoryCode(String categoryCode) {
        if (_categoryCode != null) {
            _categoryCode = categoryCode.trim();
        } else {
            _categoryCode = categoryCode;
        }
    }

    /**
     * To get the value of categoryList field
     * 
     * @return categoryList.
     */
    public ArrayList getCategoryList() {
        return _categoryList;
    }

    /**
     * To set the value of categoryList field
     */
    public void setCategoryList(ArrayList categoryList) {
        _categoryList = categoryList;
    }

    /**
     * To get the value of categoryName field
     * 
     * @return categoryName.
     */
    public String getCategoryName() {
        return _categoryName;
    }

    /**
     * To set the value of categoryName field
     */
    public void setCategoryName(String categoryName) {
        if (categoryName != null) {
            _categoryName = categoryName.trim();
        }
    }

    /**
     * To get the value of categorySearched field
     * 
     * @return categorySearched.
     */
    public String getCategorySearched() {
        return _categorySearched;
    }

    /**
     * To set the value of categorySearched field
     */
    public void setCategorySearched(String categorySearched) {
        _categorySearched = categorySearched;
    }

    /**
     * To get the value of categorySearchList field
     * 
     * @return categorySearchList.
     */
    public ArrayList getCategorySearchList() {
        return _categorySearchList;
    }

    /**
     * To set the value of categorySearchList field
     */
    public void setCategorySearchList(ArrayList categorySearchList) {
        _categorySearchList = categorySearchList;
    }

    /**
     * To get the value of categoryStatus field
     * 
     * @return categoryStatus.
     */
    public String getCategoryStatus() {
        return _categoryStatus;
    }

    /**
     * To set the value of categoryStatus field
     */
    public void setCategoryStatus(String categoryStatus) {
        _categoryStatus = categoryStatus;
    }

    /**
     * To get the value of categoryStatusList field
     * 
     * @return categoryStatusList.
     */
    public ArrayList getCategoryStatusList() {
        return _categoryStatusList;
    }

    /**
     * To set the value of categoryStatusList field
     */
    public void setCategoryStatusList(ArrayList categoryStatusList) {
        _categoryStatusList = categoryStatusList;
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
     * To get the value of displayAllowed field
     * 
     * @return displayAllowed.
     */
    public String getDisplayAllowed() {
        return _displayAllowed;
    }

    /**
     * To set the value of displayAllowed field
     */
    public void setDisplayAllowed(String displayAllowed) {
        _displayAllowed = displayAllowed;
    }

    /**
     * To get the value of domainCodeforCategory field
     * 
     * @return domainCodeforCategory.
     */
    public String getDomainCodeforCategory() {
        return _domainCodeforCategory;
    }

    /**
     * To set the value of domainCodeforCategory field
     */
    public void setDomainCodeforCategory(String domainCodeforCategory) {
        _domainCodeforCategory = domainCodeforCategory;
    }

    /**
     * To get the value of domainCodeforDomain field
     * 
     * @return domainCodeforDomain.
     */
    public String getDomainCodeforDomain() {
        return _domainCodeforDomain;
    }

    /**
     * To set the value of domainCodeforDomain field
     */
    public void setDomainCodeforDomain(String domainCodeforDomain) {
        if (domainCodeforDomain != null) {
            _domainCodeforDomain = domainCodeforDomain.trim();
        }
    }

    /**
     * To get the value of domainList field
     * 
     * @return domainList.
     */
    public ArrayList getDomainList() {
        return _domainList;
    }

    /**
     * To set the value of domainList field
     */
    public void setDomainList(ArrayList domainList) {
        _domainList = domainList;
    }

    /**
     * To get the value of domainName field
     * 
     * @return domainName.
     */
    public String getDomainName() {
        return _domainName;
    }

    /**
     * To set the value of domainName field
     */
    public void setDomainName(String domainName) {
        if (domainName != null) {
            _domainName = domainName.trim();
        }
    }

    /**
     * To get the value of domainNameSearched field
     * 
     * @return domainNameSearched.
     */
    public String getDomainNameSearched() {
        return _domainNameSearched;
    }

    /**
     * To set the value of domainNameSearched field
     */
    public void setDomainNameSearched(String domainNameSearched) {
        _domainNameSearched = domainNameSearched;
    }

    /**
     * To get the value of domainStatus field
     * 
     * @return domainStatus.
     */
    public String getDomainStatus() {
        return _domainStatus;
    }

    /**
     * To set the value of domainStatus field
     */
    public void setDomainStatus(String domainStatus) {
        _domainStatus = domainStatus;
    }

    /**
     * To get the value of domainStatusList field
     * 
     * @return domainStatusList.
     */
    public ArrayList getDomainStatusList() {
        return _domainStatusList;
    }

    /**
     * To set the value of domainStatusList field
     */
    public void setDomainStatusList(ArrayList domainStatusList) {
        _domainStatusList = domainStatusList;
    }

    /**
     * To get the value of domainTypeCode field
     * 
     * @return domainTypeCode.
     */
    public String getDomainTypeCode() {
        return _domainTypeCode;
    }

    /**
     * To set the value of domainTypeCode field
     */
    public void setDomainTypeCode(String domainTypeCode) {
        _domainTypeCode = domainTypeCode;
    }

    /**
     * To get the value of domainTypeList field
     * 
     * @return domainTypeList.
     */
    public ArrayList getDomainTypeList() {
        return _domainTypeList;
    }

    /**
     * To set the value of domainTypeList field
     */
    public void setDomainTypeList(ArrayList domainTypeList) {
        _domainTypeList = domainTypeList;
    }

    /**
     * To get the value of domainTypeName field
     * 
     * @return domainTypeName.
     */
    public String getDomainTypeName() {
        return _domainTypeName;
    }

    /**
     * To set the value of domainTypeName field
     */
    public void setDomainTypeName(String domainTypeName) {
        _domainTypeName = domainTypeName;
    }

    /**
     * To get the value of fixedRoles field
     * 
     * @return fixedRoles.
     */
    public String getFixedRoles() {
        return _fixedRoles;
    }

    /**
     * To set the value of fixedRoles field
     */
    public void setFixedRoles(String fixedRoles) {
        _fixedRoles = fixedRoles;
    }

    /**
     * To get the value of geographicalDomainList field
     * 
     * @return geographicalDomainList.
     */
    public ArrayList getGeographicalDomainList() {
        return _geographicalDomainList;
    }

    /**
     * To set the value of geographicalDomainList field
     */
    public void setGeographicalDomainList(ArrayList geographicalDomainList) {
        _geographicalDomainList = geographicalDomainList;
    }

    /**
     * To get the value of geographicalDomainName field
     * 
     * @return geographicalDomainName.
     */
    public String getGeographicalDomainName() {
        return _geographicalDomainName;
    }

    /**
     * To set the value of geographicalDomainName field
     */
    public void setGeographicalDomainName(String geographicalDomainName) {
        _geographicalDomainName = geographicalDomainName;
    }

    /**
     * To get the value of gradeCode field
     * 
     * @return gradeCode.
     */
    public String getGradeCode() {
        return _gradeCode;
    }

    /**
     * To set the value of gradeCode field
     */
    public void setGradeCode(String gradeCode) {
        if (_gradeCode != null) {
            _gradeCode = gradeCode.trim();
        } else {
            _gradeCode = gradeCode;
        }

    }

    /**
     * To get the value of gradeList field
     * 
     * @return gradeList.
     */
    public ArrayList getGradeList() {
        return _gradeList;
    }

    /**
     * To set the value of gradeList field
     */
    public void setGradeList(ArrayList gradeList) {
        _gradeList = gradeList;
    }

    /**
     * To get the value of gradeName field
     * 
     * @return gradeName.
     */
    public String getGradeName() {
        return _gradeName;
    }

    /**
     * To set the value of gradeName field
     */
    public void setGradeName(String gradeName) {
        if (gradeName != null) {
            _gradeName = gradeName.trim();
        }
    }

    /**
     * To get the value of grphDomainType field
     * 
     * @return grphDomainType.
     */
    public String getGrphDomainType() {
        return _grphDomainType;
    }

    /**
     * To set the value of grphDomainType field
     */
    public void setGrphDomainType(String grphDomainType) {
        _grphDomainType = grphDomainType;
    }

    /**
     * To get the value of lastModified field
     * 
     * @return lastModified.
     */
    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    /**
     * To set the value of lastModified field
     */
    public void setLastModifiedTime(long lastModifiedTime) {
        _lastModifiedTime = lastModifiedTime;
    }

    /**
     * To get the value of maxLoginCount field
     * 
     * @return maxLoginCount.
     */
    public String getMaxLoginCount() {
        return _maxLoginCount;
    }

    /**
     * To set the value of maxLoginCount field
     */
    public void setMaxLoginCount(String maxLoginCount) {
        _maxLoginCount = maxLoginCount;
    }

    /**
     * To get the value of maxTxnMsisdn field
     * 
     * @return maxTxnMsisdn.
     */
    public String getMaxTxnMsisdn() {
        return _maxTxnMsisdn;
    }

    /**
     * To set the value of maxTxnMsisdn field
     */
    public void setMaxTxnMsisdn(String maxTxnMsisdn) {
        _maxTxnMsisdn = maxTxnMsisdn;
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
     * To get the value of modifyAllowed field
     * 
     * @return modifyAllowed.
     */
    public String getModifyAllowed() {
        return _modifyAllowed;
    }

    /**
     * To set the value of modifyAllowed field
     */
    public void setModifyAllowed(String modifyAllowed) {
        _modifyAllowed = modifyAllowed;
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
     * To get the value of multipleGrphDomains field
     * 
     * @return multipleGrphDomains.
     */
    public String getMultipleGrphDomains() {
        return _multipleGrphDomains;
    }

    /**
     * To set the value of multipleGrphDomains field
     */
    public void setMultipleGrphDomains(String multipleGrphDomains) {
        _multipleGrphDomains = multipleGrphDomains;
    }

    /**
     * To get the value of multipleLoginAllowed field
     * 
     * @return multipleLoginAllowed.
     */
    public String getMultipleLoginAllowed() {
        return _multipleLoginAllowed;
    }

    /**
     * To set the value of multipleLoginAllowed field
     */
    public void setMultipleLoginAllowed(String multipleLoginAllowed) {
        _multipleLoginAllowed = multipleLoginAllowed;
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
     * To get the value of numberOfCategories field
     * 
     * @return numberOfCategories.
     */
    public String getNumberOfCategories() {
        return _numberOfCategories;
    }

    /**
     * To set the value of numberOfCategories field
     */
    public void setNumberOfCategories(String numberOfCategories) {
        _numberOfCategories = numberOfCategories;
    }

    /**
     * To get the value of ownerCategory field
     * 
     * @return ownerCategory.
     */
    public String getOwnerCategory() {
        return _ownerCategory;
    }

    /**
     * To set the value of ownerCategory field
     */
    public void setOwnerCategory(String ownerCategory) {
        _ownerCategory = ownerCategory;
    }

    /**
     * To get the value of parentCategoryCode field
     * 
     * @return parentCategoryCode.
     */
    public String getParentCategoryCode() {
        return _parentCategoryCode;
    }

    /**
     * To set the value of parentCategoryCode field
     */
    public void setParentCategoryCode(String parentCategoryCode) {
        _parentCategoryCode = parentCategoryCode;
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
     * To get the value of restrictedMsisdns field
     * 
     * @return restrictedMsisdns.
     */
    public String getRestrictedMsisdns() {
        return _restrictedMsisdns;
    }

    /**
     * To set the value of restrictedMsisdns field
     */
    public void setRestrictedMsisdns(String restrictedMsisdns) {
        _restrictedMsisdns = restrictedMsisdns;
    }

    /**
     * To get the value of roleTypeList field
     * 
     * @return roleTypeList.
     */
    public ArrayList getRoleTypeList() {
        return _roleTypeList;
    }

    /**
     * To set the value of roleTypeList field
     */
    public void setRoleTypeList(ArrayList roleTypeList) {
        _roleTypeList = roleTypeList;
    }

    /**
     * To get the value of roleTypeName field
     * 
     * @return roleTypeName.
     */
    public String getRoleTypeName() {
        return _roleTypeName;
    }

    /**
     * To set the value of roleTypeName field
     */
    public void setRoleTypeName(String roleTypeName) {
        _roleTypeName = roleTypeName;
    }

    /**
     * To get the value of scheduledTransferAllowed field
     * 
     * @return scheduledTransferAllowed.
     */
    public String getScheduledTransferAllowed() {
        return _scheduledTransferAllowed;
    }

    /**
     * To set the value of scheduledTransferAllowed field
     */
    public void setScheduledTransferAllowed(String scheduledTransferAllowed) {
        _scheduledTransferAllowed = scheduledTransferAllowed;
    }

    /**
     * To get the value of searchDomainList field
     * 
     * @return searchDomainList.
     */
    public ArrayList getSearchDomainList() {
        return _searchDomainList;
    }

    /**
     * To set the value of searchDomainList field
     */
    public void setSearchDomainList(ArrayList searchDomainList) {
        _searchDomainList = searchDomainList;
    }

    /**
     * @return Returns the messageGatewayTypeList.
     */
    public ArrayList getMessageGatewayTypeList() {
        return _messageGatewayTypeList;
    }

    /**
     * @param messageGatewayTypeList
     *            The messageGatewayTypeList to set.
     */
    public void setMessageGatewayTypeList(ArrayList messageGatewayTypeList) {
        _messageGatewayTypeList = messageGatewayTypeList;
    }

    /**
     * To get the value of sequenceNumber field
     * 
     * @return sequenceNumber.
     */
    public String getSequenceNumber() {
        return _sequenceNumber;
    }

    /**
     * To set the value of sequenceNumber field
     */
    public void setSequenceNumber(String sequenceNumber) {
        _sequenceNumber = sequenceNumber;
    }

    /**
     * To get the value of smsInterfaceAllowed field
     * 
     * @return smsInterfaceAllowed.
     */
    public String getSmsInterfaceAllowed() {
        return _smsInterfaceAllowed;
    }

    /**
     * To set the value of smsInterfaceAllowed field
     */
    public void setSmsInterfaceAllowed(String smsInterfaceAllowed) {
        _smsInterfaceAllowed = smsInterfaceAllowed;
    }

    /**
     * To get the value of statusTypeName field
     * 
     * @return statusTypeName.
     */
    public String getStatusTypeName() {
        return _statusTypeName;
    }

    /**
     * To set the value of statusTypeName field
     */
    public void setStatusTypeName(String statusTypeName) {
        _statusTypeName = statusTypeName;
    }

    /**
     * To get the value of txnOutsideHierchy field
     * 
     * @return txnOutsideHierchy.
     */
    public String getTxnOutsideHierchy() {
        return _txnOutsideHierchy;
    }

    /**
     * To set the value of txnOutsideHierchy field
     */
    public void setTxnOutsideHierchy(String txnOutsideHierchy) {
        _txnOutsideHierchy = txnOutsideHierchy;
    }

    /**
     * To get the value of unctrlTransferAllowed field
     * 
     * @return unctrlTransferAllowed.
     */
    public String getUnctrlTransferAllowed() {
        return _unctrlTransferAllowed;
    }

    /**
     * To set the value of unctrlTransferAllowed field
     */
    public void setUnctrlTransferAllowed(String unctrlTransferAllowed) {
        _unctrlTransferAllowed = unctrlTransferAllowed;
    }

    /**
     * To get the value of userIdPrefix field
     * 
     * @return userIdPrefix.
     */
    public String getUserIdPrefix() {
        return _userIdPrefix;
    }

    /**
     * To set the value of userIdPrefix field
     */
    public void setUserIdPrefix(String userIdPrefix) {
        _userIdPrefix = userIdPrefix;
    }

    /**
     * To get the value of viewOnNetworkBlock field
     * 
     * @return viewOnNetworkBlock.
     */
    public String getViewOnNetworkBlock() {
        return _viewOnNetworkBlock;
    }

    /**
     * To set the value of viewOnNetworkBlock field
     */
    public void setViewOnNetworkBlock(String viewOnNetworkBlock) {
        _viewOnNetworkBlock = viewOnNetworkBlock;
    }

    /**
     * To get the value of webInterfaceAllowed field
     * 
     * @return webInterfaceAllowed.
     */
    public String getWebInterfaceAllowed() {
        return _webInterfaceAllowed;
    }

    /**
     * To set the value of webInterfaceAllowed field
     */
    public void setWebInterfaceAllowed(String webInterfaceAllowed) {
        _webInterfaceAllowed = webInterfaceAllowed;
    }

    // flush Check box
    public void flushCheckBoxes() {
        _transferToListOnly = PretupsI.RESET_CHECKBOX;
        _rechargeByParentOnly = PretupsI.RESET_CHECKBOX;
        _cp2pPayer = PretupsI.RESET_CHECKBOX;
        _cp2pPayee = PretupsI.RESET_CHECKBOX;
        _cp2pWithinList = PretupsI.RESET_CHECKBOX;
        _listLevelCode = PretupsI.CP2P_WITHIN_LIST_LEVEL_DOMAIN;
        _restrictedMsisdns = PretupsI.RESET_CHECKBOX;
        _multipleLoginAllowed = PretupsI.RESET_CHECKBOX;
    }

    /**
     * Method reset
     * this method is to reset the value of multibox array to
     * PretupsI.SELECT_CHECKBOX
     * only when the btnSubmit butoon is not clicked
     * 
     * @param mapping
     *            ActionMapping
     * @param request
     *            HttpServletRequest
     */
   /* public void reset(ActionMapping mapping, HttpServletRequest request) {
        if (request.getParameter("addRoles") != null) {
            _roleFlag = null;
        }
        if (request.getParameter("addAgentRoles") != null) {
            _agentRoleFlag = null;
        }
        if (request.getParameter("confirm1") != null || request.getParameter("submit1FromAdd") != null || request.getParameter("btnModifyConfirm") != null) {
            _multipleGrphDomains = PretupsI.RESET_CHECKBOX;
            _viewOnNetworkBlock = PretupsI.RESET_CHECKBOX;
            _webInterfaceAllowed = PretupsI.RESET_CHECKBOX;
            ;
            _smsInterfaceAllowed = PretupsI.RESET_CHECKBOX;
            _multipleLoginAllowed = PretupsI.RESET_CHECKBOX;
            _multipleGrphDomains = PretupsI.RESET_CHECKBOX;
            // _maxTxnMsisdn=PretupsI.RESET_CHECKBOX;
            _displayAllowed = PretupsI.RESET_CHECKBOX;
            _modifyAllowed = PretupsI.RESET_CHECKBOX;
            _productTypeAssociationAllowed = PretupsI.RESET_CHECKBOX;
            _unctrlTransferAllowed = PretupsI.RESET_CHECKBOX;
            _agentUnctrlTransferAllowed = PretupsI.RESET_CHECKBOX;
            _agentAgentAllowed = PretupsI.RESET_CHECKBOX;
            _agentServiceAllowed = PretupsI.RESET_CHECKBOX;
            _agentModifyAllowed = PretupsI.RESET_CHECKBOX;
            _hierarchyAllowed = PretupsI.RESET_CHECKBOX;
            _agentAllowed = PretupsI.RESET_CHECKBOX;
            _outletsAllowed = PretupsI.RESET_CHECKBOX;
            // for agent
            _agentMultipleGrphDomains = PretupsI.RESET_CHECKBOX;
            _agentViewOnNetworkBlock = PretupsI.RESET_CHECKBOX;
            _agentWebInterfaceAllowed = PretupsI.RESET_CHECKBOX;
            ;
            _agentSmsInterfaceAllowed = PretupsI.RESET_CHECKBOX;
            _agentMultipleLoginAllowed = PretupsI.RESET_CHECKBOX;
            _agentMultipleGrphDomains = PretupsI.RESET_CHECKBOX;
            // _agentMaxTxnMsisdn=PretupsI.RESET_CHECKBOX;
            _agentDisplayAllowed = PretupsI.RESET_CHECKBOX;
            _agentModifyAllowed = PretupsI.RESET_CHECKBOX;
            _agentProductTypeAssociationAllowed = PretupsI.RESET_CHECKBOX;
            _agentUnctrlTransferAllowed = PretupsI.RESET_CHECKBOX;

            _agentAgentAllowed = PretupsI.RESET_CHECKBOX;
            _agentServiceAllowed = PretupsI.RESET_CHECKBOX;
            _agentModifyAllowed = PretupsI.RESET_CHECKBOX;
            _agentHierarchyAllowed = PretupsI.RESET_CHECKBOX;
            _agentAllowed = PretupsI.RESET_CHECKBOX;
            _agentOutletsAllowed = PretupsI.RESET_CHECKBOX;
            _agentScheduledTransferAllowed = PretupsI.RESET_CHECKBOX;
            _agentRestrictedMsisdns = PretupsI.RESET_CHECKBOX;
            _agentTransferToListOnly = PretupsI.RESET_CHECKBOX;
            // agent
            _agentRechargeByParentOnly = PretupsI.RESET_CHECKBOX;
            _agentCp2pPayer = PretupsI.RESET_CHECKBOX;
            _agentCp2pPayee = PretupsI.RESET_CHECKBOX;
            _agentCp2pWithinList = PretupsI.RESET_CHECKBOX;
            if (_userExistsFlag == null || "false".equalsIgnoreCase(_userExistsFlag)) {
                _scheduledTransferAllowed = PretupsI.RESET_CHECKBOX;
                // Added By Abhilasha
                _restrictedMsisdns = PretupsI.RESET_CHECKBOX;
                _transferToListOnly = PretupsI.RESET_CHECKBOX;
                _serviceAllowed = PretupsI.RESET_CHECKBOX;
                // added
                _rechargeByParentOnly = PretupsI.RESET_CHECKBOX;
                _cp2pPayer = PretupsI.RESET_CHECKBOX;
                _cp2pPayee = PretupsI.RESET_CHECKBOX;
                _cp2pWithinList = PretupsI.RESET_CHECKBOX;
                _listLevelCode = PretupsI.CP2P_WITHIN_LIST_LEVEL_DOMAIN;
            } else {
                _transferToListOnly = PretupsI.RESET_CHECKBOX;
                _rechargeByParentOnly = PretupsI.RESET_CHECKBOX;
                _cp2pPayer = PretupsI.RESET_CHECKBOX;
                _cp2pPayee = PretupsI.RESET_CHECKBOX;
                _cp2pWithinList = PretupsI.RESET_CHECKBOX;
                _listLevelCode = PretupsI.CP2P_WITHIN_LIST_LEVEL_DOMAIN;

            }
            // added by santanu
            if (_listLevelCode == null || _listLevelCode.equalsIgnoreCase(PretupsI.CP2P_WITHIN_LIST_LEVEL_DOMAIN)) {
                _listLevelCode = PretupsI.CP2P_WITHIN_LIST_LEVEL_DOMAIN;
            }
            if (_rechargeByParentOnly == null || !_rechargeByParentOnly.equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                _rechargeByParentOnly = PretupsI.RESET_CHECKBOX;
            }

            if (_cp2pPayer == null || !_cp2pPayer.equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                _cp2pPayer = PretupsI.RESET_CHECKBOX;
            }
            if (_cp2pPayee == null || !_cp2pPayee.equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                _cp2pPayee = PretupsI.RESET_CHECKBOX;
            }
            if (_cp2pWithinList == null || !_cp2pWithinList.equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                _cp2pWithinList = PretupsI.RESET_CHECKBOX;
                // agent case
            }

            if (_agentListLevelCode == null || _agentListLevelCode.equalsIgnoreCase(PretupsI.CP2P_WITHIN_LIST_LEVEL_DOMAIN)) {
                _agentListLevelCode = PretupsI.CP2P_WITHIN_LIST_LEVEL_DOMAIN;
            }
            if (_agentRechargeByParentOnly == null || !_agentRechargeByParentOnly.equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                _agentRechargeByParentOnly = PretupsI.RESET_CHECKBOX;
            }

            if (_agentCp2pPayer == null || !_agentCp2pPayer.equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                _agentCp2pPayer = PretupsI.RESET_CHECKBOX;
            }
            if (_agentCp2pPayee == null || !_agentCp2pPayee.equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                _agentCp2pPayee = PretupsI.RESET_CHECKBOX;
            }
            if (_agentCp2pWithinList == null || !_agentCp2pWithinList.equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                _agentCp2pWithinList = PretupsI.RESET_CHECKBOX;
            }

            if (_scheduledTransferAllowed == null || !_scheduledTransferAllowed.equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                _scheduledTransferAllowed = PretupsI.RESET_CHECKBOX;
            }
            if (_restrictedMsisdns == null || !_restrictedMsisdns.equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                _restrictedMsisdns = PretupsI.RESET_CHECKBOX;
            }
            if (_transferToListOnly == null || !_transferToListOnly.equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                _transferToListOnly = PretupsI.RESET_CHECKBOX;
            }
            if (_serviceAllowed == null || !_serviceAllowed.equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                _serviceAllowed = PretupsI.RESET_CHECKBOX;
            }
            // Added on 13/07/07 for Low balance alert allow
            if (!_isLowBalAlertUserExists) {
                _lowBalanceAlertAllow = PretupsI.RESET_CHECKBOX;
            }
            if (!_isAgentLowBalAlertUserExists) {
                _agentLowBalanceAlertAllow = PretupsI.RESET_CHECKBOX;
            }

            if (_lowBalanceAlertAllow == null || !_lowBalanceAlertAllow.equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                _lowBalanceAlertAllow = PretupsI.RESET_CHECKBOX;
            }
            if (_agentLowBalanceAlertAllow == null || !_agentLowBalanceAlertAllow.equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                _agentLowBalanceAlertAllow = PretupsI.RESET_CHECKBOX;
            }
            _checkArray = null;
        }

        if (request.getParameter("submitAgent") != null) {
            _agentUnctrlTransferAllowed = PretupsI.RESET_CHECKBOX;
            _agentServiceAllowed = PretupsI.RESET_CHECKBOX;
            _agentHierarchyAllowed = PretupsI.RESET_CHECKBOX;
            _agentOutletsAllowed = PretupsI.RESET_CHECKBOX;
            _agentMultipleLoginAllowed = PretupsI.RESET_CHECKBOX;
            _agentViewOnNetworkBlock = PretupsI.RESET_CHECKBOX;
            _agentRestrictedMsisdns = PretupsI.RESET_CHECKBOX;
            _agentScheduledTransferAllowed = PretupsI.RESET_CHECKBOX;
            _agentWebInterfaceAllowed = PretupsI.RESET_CHECKBOX;
            ;
            _agentSmsInterfaceAllowed = PretupsI.RESET_CHECKBOX;
            _agentTransferToListOnly = PretupsI.RESET_CHECKBOX;
            // Added on 13/07/07 for Agent low balance alert allow
            _agentLowBalanceAlertAllow = PretupsI.RESET_CHECKBOX;
            _agentCheckArray = null;
            // agent for listMngt
            _agentRechargeByParentOnly = PretupsI.RESET_CHECKBOX;
            _agentCp2pPayer = PretupsI.RESET_CHECKBOX;
            _agentCp2pPayee = PretupsI.RESET_CHECKBOX;
            _agentCp2pWithinList = PretupsI.RESET_CHECKBOX;
            _agentListLevelCode = PretupsI.CP2P_WITHIN_LIST_LEVEL_DOMAIN;

        }
        if (request.getParameter("submit") != null) {
            _isAgentAllow = false;
        }
        // added by nilesh
        if (request.getParameter("confirm") != null) {
            _defaultGrade = PretupsI.RESET_CHECKBOX;
            _twoFAallowed = PretupsI.RESET_CHECKBOX;
        }
        if (request.getParameter("confirmmodify") != null) {
            _defaultGrade = PretupsI.RESET_CHECKBOX;
            _twoFAallowed = PretupsI.RESET_CHECKBOX;
        }
        if (request.getParameter("confirmgrade") != null) {
            _defaultGrade = PretupsI.RESET_CHECKBOX;
            _twoFAallowed = PretupsI.RESET_CHECKBOX;
        }

    }*/

    public void semiFlush() {
        _domainCodeforDomain = null;
        _domainName = null;
        _domainTypeCode = null;
        _domainTypeName = null;
        _numberOfCategories = null;
        _domainTypeCode = null;
        _domainStatus = null;
        _userIdPrefix = null;
        _agentUserIdPrefix = null;
        _categoryCode = null;
        _agentCategoryCode = null;
        _categoryName = null;
        _agentCategoryName = null;
        _sequenceNumber = null;
        _grphDomainType = null;
        _agentGrphDomainType = null;
        _multipleGrphDomains = null;
        _agentMultipleGrphDomains = null;
        _webInterfaceAllowed = null;
        _agentWebInterfaceAllowed = null;
        _smsInterfaceAllowed = null;
        _agentSmsInterfaceAllowed = null;
        _fixedRoles = null;
        _agentFixedRoles = null;
        _multipleLoginAllowed = null;
        _agentMultipleLoginAllowed = null;
        _viewOnNetworkBlock = null;
        _agentViewOnNetworkBlock = null;
        _maxLoginCount = null;
        _agentMaxLoginCount = null;
        _categoryStatus = null;
        _agentCategoryStatus = null;
        _createdOn = null;
        _createdBy = null;
        _modifiedOn = null;
        _modifiedBy = null;
        _displayAllowed = null;
        _agentDisplayAllowed = null;
        _modifyAllowed = null;
        _serviceAllowed = null;
        _agentServiceAllowed = null;
        _outletsAllowed = null;
        _agentOutletsAllowed = null;
        _productTypeAssociationAllowed = null;
        _maxTxnMsisdn = null;
        _agentMaxTxnMsisdn = null;
        _unctrlTransferAllowed = null;
        _scheduledTransferAllowed = null;
        _agentScheduledTransferAllowed = null;
        _restrictedMsisdns = null;
        _agentRestrictedMsisdns = null;
        _parentCategoryCode = null;
        _txnOutsideHierchy = null;
        _checkArray = null;
        _agentCheckArray = null;
        _hierarchyAllowed = null;
        _agentHierarchyAllowed = null;
        _agentAllowed = null;
    }

    public void semiFlushCategory() {
        _categoryCode = null;
        _agentCategoryCode = null;
        _categoryName = null;
        _agentCategoryName = null;
        _sequenceNumber = null;
        _grphDomainType = null;
        _agentGrphDomainType = null;
        _multipleGrphDomains = null;
        _agentMultipleGrphDomains = null;
        _webInterfaceAllowed = null;
        _agentWebInterfaceAllowed = null;
        _smsInterfaceAllowed = null;
        _agentSmsInterfaceAllowed = null;
        _fixedRoles = null;
        _agentFixedRoles = null;
        _multipleLoginAllowed = null;
        _agentMultipleLoginAllowed = null;
        _viewOnNetworkBlock = null;
        _agentViewOnNetworkBlock = null;
        _maxLoginCount = null;
        _agentMaxLoginCount = null;
        _categoryStatus = null;
        _agentCategoryStatus = null;
        _createdOn = null;
        _createdBy = null;
        _modifiedOn = null;
        _modifiedBy = null;
        _displayAllowed = null;
        _modifyAllowed = null;
        _serviceAllowed = null;
        _agentServiceAllowed = null;
        _outletsAllowed = null;
        _agentOutletsAllowed = null;
        _productTypeAssociationAllowed = null;
        _maxTxnMsisdn = null;
        _agentMaxTxnMsisdn = null;
        _unctrlTransferAllowed = null;
        _scheduledTransferAllowed = null;
        _agentScheduledTransferAllowed = null;
        _restrictedMsisdns = null;
        _agentRestrictedMsisdns = null;
        _parentCategoryCode = null;
        _txnOutsideHierchy = null;

        _userIdPrefix = null;
        _agentUserIdPrefix = null;
        _messageGatewayTypeList = null;
        _agentMessageGatewayTypeList = null;
        _checkArray = null;
        _agentCheckArray = null;
        _hierarchyAllowed = null;
        _agentHierarchyAllowed = null;
        _agentAllowed = null;
    }

    public void roleFlush() {
        _rolesMap = null;
        _rolesMapSelected = null;
        _roleFlag = null;
        _agentRolesMap = null;
        _agentRolesMapSelected = null;
        _agentRoleFlag = null;
    }

    public void semiFlushGrade() {
        _gradeCode = null;
        _gradeName = null;
        // added by nilesh
        // _defaultGrade=null;
    }

    public void flush() {
        _domainCodeforDomain = null;
        _domainName = null;
        _dCode = null;
        _cCode = null;
        _domainTypeCode = null;
        _domainTypeName = null;
        _ownerCategory = null;
        _domainStatus = null;
        _createdOn = null;
        _createdBy = null;
        _modifiedOn = null;
        _modifiedBy = null;
        _numberOfCategories = null;
        _domainList = null;
        _domainTypeList = null;
        _domainStatusList = null;
        _statusTypeName = null;
        _modifyFlag = null;
        _searchDomainList = null;
        _messageGatewayTypeList = null;
        _modifiedMessageGatewayList = null;
        _categoryCode = null;
        _categoryName = null;
        _domainCodeforCategory = null;
        _sequenceNumber = null;
        _grphDomainType = null;
        _multipleGrphDomains = null;
        _webInterfaceAllowed = null;
        _smsInterfaceAllowed = null;
        _fixedRoles = null;
        _multipleLoginAllowed = null;
        _viewOnNetworkBlock = null;
        _maxLoginCount = null;
        _categoryStatus = null;
        _displayAllowed = null;
        _modifyAllowed = null;
        _serviceAllowed = null;
        _outletsAllowed = null;
        _productTypeAssociationAllowed = null;
        _maxTxnMsisdn = null;
        _unctrlTransferAllowed = null;
        _scheduledTransferAllowed = null;
        _restrictedMsisdns = null;
        _parentCategoryCode = null;
        _txnOutsideHierchy = null;
        _userIdPrefix = null;
        _geographicalDomainList = null;
        _roleTypeList = null;
        _categoryStatusList = null;
        _categoryList = null;
        _domainNameSearched = null;
        _categorySearched = null;
        _categorySearchList = null;
        _gradeCode = null;
        _gradeName = null;
        _gradeList = null;
        _networkName = null;
        _networkCode = null;
        _checkArray = null;
        _agentCheckArray = null;
        _userExistsFlag = null;
        _geoDomainName = null;
        _hierarchyAllowed = null;
        _agentAllowed = null;
        _transferToListOnly = null;
        _rechargeByParentOnly = null;
        _cp2pPayer = null;
        _cp2pPayee = null;
        _cp2pWithinList = null;
        _listLevelCode = null;
        // agent flush
        _agentGeographicalDomainList = null;
        _agentRoleTypeList = null;
        _agentCategoryStatusList = null;
        _agentMultipleGrphDomains = null;
        _agentProductTypeAssociationAllowed = null;
        _agentDisplayAllowed = null;
        _agentModifyAllowed = null;
        _agentAgentAllowed = null;
        _agentCategoryType = null;
        _agentDomainCodeforCategory = null;
        _agentDomainName = null;
        _agentCategoryName = null;
        _agentCategoryCode = null;
        _agentGrphDomainType = null;
        _agentFixedRoles = null;
        _agentCategoryStatus = null;
        _agentUserIdPrefix = null;
        _agentOutletsAllowed = null;
        _agentRolesMapSelected = null;
        _agentRoleName = null;
        _agentMessageGatewayTypeList = null;
        _agentModifiedMessageGatewayTypeList = null;
        _agentGatewayName = null;

        _agentGatewayType = null;
        _agentMultipleLoginAllowed = null;
        _agentViewOnNetworkBlock = null;
        _agentRestrictedMsisdns = null;
        _agentScheduledTransferAllowed = null;
        _agentUnctrlTransferAllowed = null;
        _agentServiceAllowed = null;
        _agentMaxLoginCount = null;
        _agentMaxTxnMsisdn = null;
        _agentHierarchyAllowed = null;
        _agentAllowedFlag = null;
        _previousAgentAllowedFlag = null;
        _operationPerformed = null;
        _activeAgentExist = null;
        _categoryMap = null;
        _assignRoleCallInter = false;
        _agentStatusTypeName = null;
        _agentGeoDomainName = null;
        _agentRoleTypeName = null;
        _agentUserExistsFlag = null;
        _agentGoeList = null;
        _agentTransferToListOnly = null;
        _p2pWithinLevelList = null;
        _listLevelType = null;
        _userType = null;
        _agentListLevelType = null;
        _agentRechargeByParentOnly = null;
        _agentCp2pPayer = null;
        _agentCp2pPayee = null;
        _agentCp2pWithinList = null;
        _agentListLevelCode = null;
        _authTypeList = null;

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

    /**
     * @return Returns the productTypeAssociationAllowed.
     */
    public String getProductTypeAssociationAllowed() {
        return _productTypeAssociationAllowed;
    }

    /**
     * @param productTypeAssociationAllowed
     *            The productTypeAssociationAllowed to set.
     */
    public void setProductTypeAssociationAllowed(String productTypeAssociationAllowed) {
        _productTypeAssociationAllowed = productTypeAssociationAllowed;
    }

    public int getGradeListSize() {
        if (_gradeList != null && !_gradeList.isEmpty()) {
            return 1;
        } else {
            return 0;
        }
    }

    public int getCategoryListSize() {
        if (_categoryList != null && !_categoryList.isEmpty()) {
            return 1;
        } else {
            return 0;
        }
    }

    public int getDomainListSize() {
        if (_domainList != null && !_domainList.isEmpty()) {
            return 1;
        } else {
            return 0;
        }
    }

    // ///////////// Agent Category geter and seter method

    /**
     * @return Returns the agentAgentAllowed.
     */
    public String getAgentAgentAllowed() {
        return _agentAgentAllowed;
    }

    /**
     * @param agentAgentAllowed
     *            The agentAgentAllowed to set.
     */
    public void setAgentAgentAllowed(String agentAgentAllowed) {
        _agentAgentAllowed = agentAgentAllowed;
    }

    /**
     * @return Returns the agentCategoryName.
     */
    public String getAgentCategoryName() {
        return _agentCategoryName;
    }

    /**
     * @param agentCategoryName
     *            The agentCategoryName to set.
     */
    public void setAgentCategoryName(String agentCategoryName) {
        _agentCategoryName = agentCategoryName;
    }

    /**
     * @return Returns the agentCategoryCode.
     */
    public String getAgentCategoryCode() {
        return _agentCategoryCode;
    }

    /**
     * @param agentCategoryCode
     *            The agentCategoryCode to set.
     */
    public void setAgentCategoryCode(String agentCategoryCode) {
        _agentCategoryCode = agentCategoryCode;
    }

    /**
     * @return Returns the agentCategoryStatus.
     */
    public String getAgentCategoryStatus() {
        return _agentCategoryStatus;
    }

    /**
     * @param agentCategoryStatus
     *            The agentCategoryStatus to set.
     */
    public void setAgentCategoryStatus(String agentCategoryStatus) {
        _agentCategoryStatus = agentCategoryStatus;
    }

    /**
     * @return Returns the agentCategoryStatusList.
     */
    public ArrayList getAgentCategoryStatusList() {
        return _agentCategoryStatusList;
    }

    /**
     * @param agentCategoryStatusList
     *            The agentCategoryStatusList to set.
     */
    public void setAgentCategoryStatusList(ArrayList agentCategoryStatusList) {
        _agentCategoryStatusList = agentCategoryStatusList;
    }

    /**
     * @return Returns the agentCategoryType.
     */
    public String getAgentCategoryType() {
        return _agentCategoryType;
    }

    /**
     * @param agentCategoryType
     *            The agentCategoryType to set.
     */
    public void setAgentCategoryType(String agentCategoryType) {
        _agentCategoryType = agentCategoryType;
    }

    /**
     * @return Returns the agentCheckArray.
     */
    public String[] getAgentCheckArray() {
        return _agentCheckArray;
    }

    /**
     * @param agentCheckArray
     *            The agentCheckArray to set.
     */
    public void setAgentCheckArray(String[] agentCheckArray) {
        _agentCheckArray = agentCheckArray;
    }

    /**
     * @return Returns the agentDisplayAllowed.
     */
    public String getAgentDisplayAllowed() {
        return _agentDisplayAllowed;
    }

    /**
     * @param agentDisplayAllowed
     *            The agentDisplayAllowed to set.
     */
    public void setAgentDisplayAllowed(String agentDisplayAllowed) {
        _agentDisplayAllowed = agentDisplayAllowed;
    }

    /**
     * @return Returns the agentDomainCodeforCategory.
     */
    public String getAgentDomainCodeforCategory() {
        return _agentDomainCodeforCategory;
    }

    /**
     * @param agentDomainCodeforCategory
     *            The agentDomainCodeforCategory to set.
     */
    public void setAgentDomainCodeforCategory(String agentDomainCodeforCategory) {
        _agentDomainCodeforCategory = agentDomainCodeforCategory;
    }

    /**
     * @return Returns the agentDomainName.
     */
    public String getAgentDomainName() {
        return _agentDomainName;
    }

    /**
     * @param agentDomainName
     *            The agentDomainName to set.
     */
    public void setAgentDomainName(String agentDomainName) {
        _agentDomainName = agentDomainName;
    }

    /**
     * @return Returns the agentFixedRoles.
     */
    public String getAgentFixedRoles() {
        return _agentFixedRoles;
    }

    /**
     * @param agentFixedRoles
     *            The agentFixedRoles to set.
     */
    public void setAgentFixedRoles(String agentFixedRoles) {
        _agentFixedRoles = agentFixedRoles;
    }

    /**
     * @return Returns the agentGatewayName.
     */
    public String getAgentGatewayName() {
        return _agentGatewayName;
    }

    /**
     * @param agentGatewayName
     *            The agentGatewayName to set.
     */
    public void setAgentGatewayName(String agentGatewayName) {
        _agentGatewayName = agentGatewayName;
    }

    /**
     * @return Returns the agentGatewayType.
     */
    public String getAgentGatewayType() {
        return _agentGatewayType;
    }

    /**
     * @param agentGatewayType
     *            The agentGatewayType to set.
     */
    public void setAgentGatewayType(String agentGatewayType) {
        _agentGatewayType = agentGatewayType;
    }

    /**
     * @return Returns the agentGeographicalDomainList.
     */
    public String getAgentGeographicalDomainList() {
        return _agentGeographicalDomainList;
    }

    /**
     * @param agentGeographicalDomainList
     *            The agentGeographicalDomainList to set.
     */
    public void setAgentGeographicalDomainList(String agentGeographicalDomainList) {
        _agentGeographicalDomainList = agentGeographicalDomainList;
    }

    /**
     * @return Returns the agentGrphDomainType.
     */
    public String getAgentGrphDomainType() {
        return _agentGrphDomainType;
    }

    /**
     * @param agentGrphDomainType
     *            The agentGrphDomainType to set.
     */
    public void setAgentGrphDomainType(String agentGrphDomainType) {
        _agentGrphDomainType = agentGrphDomainType;
    }

    /**
     * @return Returns the agentHierarchyAllowed.
     */
    public String getAgentHierarchyAllowed() {
        return _agentHierarchyAllowed;
    }

    /**
     * @param agentHierarchyAllowed
     *            The agentHierarchyAllowed to set.
     */
    public void setAgentHierarchyAllowed(String agentHierarchyAllowed) {
        _agentHierarchyAllowed = agentHierarchyAllowed;
    }

    /**
     * @return Returns the agentMaxLoginCount.
     */
    public String getAgentMaxLoginCount() {
        return _agentMaxLoginCount;
    }

    /**
     * @param agentMaxLoginCount
     *            The agentMaxLoginCount to set.
     */
    public void setAgentMaxLoginCount(String agentMaxLoginCount) {
        _agentMaxLoginCount = agentMaxLoginCount;
    }

    /**
     * @return Returns the agentMaxTxnMsisdn.
     */
    public String getAgentMaxTxnMsisdn() {
        return _agentMaxTxnMsisdn;
    }

    /**
     * @param agentMaxTxnMsisdn
     *            The agentMaxTxnMsisdn to set.
     */
    public void setAgentMaxTxnMsisdn(String agentMaxTxnMsisdn) {
        _agentMaxTxnMsisdn = agentMaxTxnMsisdn;
    }

    /**
     * @return Returns the agentMessageGatewayTypeList.
     */
    public ArrayList getAgentMessageGatewayTypeList() {
        return _agentMessageGatewayTypeList;
    }

    /**
     * @param agentMessageGatewayTypeList
     *            The agentMessageGatewayTypeList to set.
     */
    public void setAgentMessageGatewayTypeList(ArrayList agentMessageGatewayTypeList) {
        _agentMessageGatewayTypeList = agentMessageGatewayTypeList;
    }

    /**
     * @return Returns the agentModifyAllowed.
     */
    public String getAgentModifyAllowed() {
        return _agentModifyAllowed;
    }

    /**
     * @param agentModifyAllowed
     *            The agentModifyAllowed to set.
     */
    public void setAgentModifyAllowed(String agentModifyAllowed) {
        _agentModifyAllowed = agentModifyAllowed;
    }

    /**
     * @return Returns the agentMultipleGrphDomains.
     */
    public String getAgentMultipleGrphDomains() {
        return _agentMultipleGrphDomains;
    }

    /**
     * @param agentMultipleGrphDomains
     *            The agentMultipleGrphDomains to set.
     */
    public void setAgentMultipleGrphDomains(String agentMultipleGrphDomains) {
        _agentMultipleGrphDomains = agentMultipleGrphDomains;
    }

    /**
     * @return Returns the agentMultipleLoginAllowed.
     */
    public String getAgentMultipleLoginAllowed() {
        return _agentMultipleLoginAllowed;
    }

    /**
     * @param agentMultipleLoginAllowed
     *            The agentMultipleLoginAllowed to set.
     */
    public void setAgentMultipleLoginAllowed(String agentMultipleLoginAllowed) {
        _agentMultipleLoginAllowed = agentMultipleLoginAllowed;
    }

    /**
     * @return Returns the agentOutletsAllowed.
     */
    public String getAgentOutletsAllowed() {
        return _agentOutletsAllowed;
    }

    /**
     * @param agentOutletsAllowed
     *            The agentOutletsAllowed to set.
     */
    public void setAgentOutletsAllowed(String agentOutletsAllowed) {
        _agentOutletsAllowed = agentOutletsAllowed;
    }

    /**
     * @return Returns the agentProductTypeAssociationAllowed.
     */
    public String getAgentProductTypeAssociationAllowed() {
        return _agentProductTypeAssociationAllowed;
    }

    /**
     * @param agentProductTypeAssociationAllowed
     *            The agentProductTypeAssociationAllowed to set.
     */
    public void setAgentProductTypeAssociationAllowed(String agentProductTypeAssociationAllowed) {
        _agentProductTypeAssociationAllowed = agentProductTypeAssociationAllowed;
    }

    /**
     * @return Returns the agentRestrictedMsisdns.
     */
    public String getAgentRestrictedMsisdns() {
        return _agentRestrictedMsisdns;
    }

    /**
     * @param agentRestrictedMsisdns
     *            The agentRestrictedMsisdns to set.
     */
    public void setAgentRestrictedMsisdns(String agentRestrictedMsisdns) {
        _agentRestrictedMsisdns = agentRestrictedMsisdns;
    }

    /**
     * @return Returns the agentRoleName.
     */
    public String getAgentRoleName() {
        return _agentRoleName;
    }

    /**
     * @param agentRoleName
     *            The agentRoleName to set.
     */
    public void setAgentRoleName(String agentRoleName) {
        _agentRoleName = agentRoleName;
    }

    /**
     * @return Returns the agentRolesMapSelected.
     */
    public HashMap getAgentRolesMapSelected() {
        return _agentRolesMapSelected;
    }

    /**
     * @param agentRolesMapSelected
     *            The agentRolesMapSelected to set.
     */
    public void setAgentRolesMapSelected(HashMap agentRolesMapSelected) {
        _agentRolesMapSelected = agentRolesMapSelected;
    }

    /**
     * @return Returns the agentRoleTypeList.
     */
    public ArrayList getAgentRoleTypeList() {
        return _agentRoleTypeList;
    }

    /**
     * @param agentRoleTypeList
     *            The agentRoleTypeList to set.
     */
    public void setAgentRoleTypeList(ArrayList agentRoleTypeList) {
        _agentRoleTypeList = agentRoleTypeList;
    }

    /**
     * @return Returns the agentScheduledTransferAllowed.
     */
    public String getAgentScheduledTransferAllowed() {
        return _agentScheduledTransferAllowed;
    }

    /**
     * @param agentScheduledTransferAllowed
     *            The agentScheduledTransferAllowed to set.
     */
    public void setAgentScheduledTransferAllowed(String agentScheduledTransferAllowed) {
        _agentScheduledTransferAllowed = agentScheduledTransferAllowed;
    }

    /**
     * @return Returns the agentServiceAllowed.
     */
    public String getAgentServiceAllowed() {
        return _agentServiceAllowed;
    }

    /**
     * @param agentServiceAllowed
     *            The agentServiceAllowed to set.
     */
    public void setAgentServiceAllowed(String agentServiceAllowed) {
        _agentServiceAllowed = agentServiceAllowed;
    }

    /**
     * @return Returns the agentUnctrlTransferAllowed.
     */
    public String getAgentUnctrlTransferAllowed() {
        return _agentUnctrlTransferAllowed;
    }

    /**
     * @param agentUnctrlTransferAllowed
     *            The agentUnctrlTransferAllowed to set.
     */
    public void setAgentUnctrlTransferAllowed(String agentUnctrlTransferAllowed) {
        _agentUnctrlTransferAllowed = agentUnctrlTransferAllowed;
    }

    /**
     * @return Returns the agentUserIdPrefix.
     */
    public String getAgentUserIdPrefix() {
        return _agentUserIdPrefix;
    }

    /**
     * @param agentUserIdPrefix
     *            The agentUserIdPrefix to set.
     */
    public void setAgentUserIdPrefix(String agentUserIdPrefix) {
        _agentUserIdPrefix = agentUserIdPrefix;
    }

    /**
     * @return Returns the agentViewOnNetworkBlock.
     */
    public String getAgentViewOnNetworkBlock() {
        return _agentViewOnNetworkBlock;
    }

    /**
     * @param agentViewOnNetworkBlock
     *            The agentViewOnNetworkBlock to set.
     */
    public void setAgentViewOnNetworkBlock(String agentViewOnNetworkBlock) {
        _agentViewOnNetworkBlock = agentViewOnNetworkBlock;
    }

    /**
     * To get the value of userExistsFlag field
     * 
     * @return userExistsFlag.
     */
    public String isUserExistsFlag() {
        return _userExistsFlag;
    }

    /**
     * @return Returns the previousAgentAllowedFlag.
     */
    public String getPreviousAgentAllowedFlag() {
        return _previousAgentAllowedFlag;
    }

    /**
     * @param previousAgentAllowedFlag
     *            The previousAgentAllowedFlag to set.
     */
    public void setPreviousAgentAllowedFlag(String previousAgentAllowedFlag) {
        _previousAgentAllowedFlag = previousAgentAllowedFlag;
    }

    /**
     * @return Returns the operationPerformed.
     */
    public String getOperationPerformed() {
        return _operationPerformed;
    }

    /**
     * @param operationPerformed
     *            The operationPerformed to set.
     */
    public void setOperationPerformed(String operationPerformed) {
        _operationPerformed = operationPerformed;
    }

    /**
     * @return Returns the activeAgentExist.
     */
    public String getActiveAgentExist() {
        return _activeAgentExist;
    }

    /**
     * @param activeAgentExist
     *            The activeAgentExist to set.
     */
    public void setActiveAgentExist(String activeAgentExist) {
        _activeAgentExist = activeAgentExist;
    }

    public HashMap getCategoryMap() {
        return _categoryMap;
    }

    public void setCategoryMap(HashMap categoryMap) {
        _categoryMap = categoryMap;
    }

    public String getCatIndex() {
        return _catIndex;
    }

    public void setCatIndex(String catIndex) {
        _catIndex = catIndex;
    }

    public boolean isAssignRoleCallInter() {
        return _assignRoleCallInter;
    }

    public void setAssignRoleCallInter(boolean assignRoleCallInter) {
        _assignRoleCallInter = assignRoleCallInter;
    }

    public String getAgentGeoDomainName() {
        return _agentGeoDomainName;
    }

    public void setAgentGeoDomainName(String agentGeoDomainName) {
        _agentGeoDomainName = agentGeoDomainName;
    }

    public String getAgentRoleTypeName() {
        return _agentRoleTypeName;
    }

    public void setAgentRoleTypeName(String agentRoleTypeName) {
        _agentRoleTypeName = agentRoleTypeName;
    }

    public String getAgentStatusTypeName() {
        return _agentStatusTypeName;
    }

    public void setAgentStatusTypeName(String agentStatusTypeName) {
        _agentStatusTypeName = agentStatusTypeName;
    }

    public String getAgentUserExistsFlag() {
        return _agentUserExistsFlag;
    }

    public void setAgentUserExistsFlag(String agentUserExistsFlag) {
        _agentUserExistsFlag = agentUserExistsFlag;
    }

    public ArrayList getAgentGoeList() {
        return _agentGoeList;
    }

    public void setAgentGoeList(ArrayList agentGoeList) {
        _agentGoeList = agentGoeList;
    }

    /**
     * @return Returns the isUserHierarchyExists.
     */
    public boolean isUserHierarchyExists() {
        return _isUserHierarchyExists;
    }

    /**
     * @param isUserHierarchyExists
     *            The isUserHierarchyExists to set.
     */
    public void setUserHierarchyExists(boolean isUserHierarchyExists) {
        _isUserHierarchyExists = isUserHierarchyExists;
    }

    /**
     * @return Returns the isAgentUserHierarchyExists.
     */
    public boolean isAgentUserHierarchyExists() {
        return _isAgentUserHierarchyExists;
    }

    /**
     * @param isAgentUserHierarchyExists
     *            The isAgentUserHierarchyExists to set.
     */
    public void setAgentUserHierarchyExists(boolean isAgentUserHierarchyExists) {
        _isAgentUserHierarchyExists = isAgentUserHierarchyExists;
    }

    /**
     * @return Returns the isAgentUncontrolledTransferFlag.
     */
    public boolean isAgentUncontrolledTransferFlag() {
        return _isAgentUncontrolledTransferFlag;
    }

    /**
     * @param isAgentUncontrolledTransferFlag
     *            The isAgentUncontrolledTransferFlag to set.
     */
    public void setAgentUncontrolledTransferFlag(boolean isAgentUncontrolledTransferFlag) {
        _isAgentUncontrolledTransferFlag = isAgentUncontrolledTransferFlag;
    }

    /**
     * @return Returns the isUncontrolledTransferFlag.
     */
    public boolean isUncontrolledTransferFlag() {
        return _isUncontrolledTransferFlag;
    }

    /**
     * @param isUncontrolledTransferFlag
     *            The isUncontrolledTransferFlag to set.
     */
    public void setUncontrolledTransferFlag(boolean isUncontrolledTransferFlag) {
        _isUncontrolledTransferFlag = isUncontrolledTransferFlag;
    }

    /**
     * @return Returns the cCode.
     */
    public String getCCode() {
        return _cCode;
    }

    /**
     * @param code
     *            The cCode to set.
     */
    public void setCCode(String code) {
        _cCode = code;
    }

    /**
     * @return Returns the dCode.
     */
    public String getDCode() {
        return _dCode;
    }

    /**
     * @param code
     *            The dCode to set.
     */
    public void setDCode(String code) {
        _dCode = code;
    }

    /**
     * @return Returns the lowBalanceAlert.
     */
    public String getLowBalanceAlertAllow() {
        return _lowBalanceAlertAllow;
    }

    public void setLowBalanceAlertAllow(String lowBalanceAlertAllow) {
        _lowBalanceAlertAllow = lowBalanceAlertAllow;
    }

    // Added on 13/07/07 for Low balance alert allow
    /**
     * @return Returns the agentLowBalanceAlert.
     */
    public String getAgentLowBalanceAlertAllow() {
        return _agentLowBalanceAlertAllow;
    }

    /**
     * @return Set the agentLowBalanceAlert.
     */
    public void setAgentLowBalanceAlertAllow(String agentLowBalanceAlertAllow) {
        _agentLowBalanceAlertAllow = agentLowBalanceAlertAllow;
    }

    /**
     * @return Returns the isAgentLowBalAlertUserExists.
     */
    public boolean isAgentLowBalAlertUserExists() {
        return _isAgentLowBalAlertUserExists;
    }

    /**
     * @param isAgentLowBalAlertUserExists
     *            The isAgentLowBalAlertUserExists to set.
     */
    public void setAgentLowBalAlertUserExists(boolean isAgentLowBalAlertUserExists) {
        _isAgentLowBalAlertUserExists = isAgentLowBalAlertUserExists;
    }

    /**
     * @return Returns the isLowBalAlertUserExists.
     */
    public boolean isLowBalAlertUserExists() {
        return _isLowBalAlertUserExists;
    }

    /**
     * @param isLowBalAlertUserExists
     *            The isLowBalAlertUserExists to set.
     */
    public void setLowBalAlertUserExists(boolean isLowBalAlertUserExists) {
        _isLowBalAlertUserExists = isLowBalAlertUserExists;
    }

    /**
     * @return Returns the _rechargeByParentOnly.
     */
    public String getRechargeByParentOnly() {
        return _rechargeByParentOnly;
    }

    /**
     * @param byParentOnly
     *            The _rechargeByParentOnly to set.
     */
    public void setRechargeByParentOnly(String byParentOnly) {
        _rechargeByParentOnly = byParentOnly;
    }

    /**
     * @return Returns the _cp2pPayer.
     */
    public String getCp2pPayer() {
        return _cp2pPayer;
    }

    /**
     * @param payer
     *            The _cp2pPayer to set.
     */
    public void setCp2pPayer(String payer) {
        _cp2pPayer = payer;
    }

    /**
     * @return Returns the _cp2pPayee.
     */
    public String getCp2pPayee() {
        return _cp2pPayee;
    }

    /**
     * @param payee
     *            The _cp2pPayee to set.
     */
    public void setCp2pPayee(String payee) {
        _cp2pPayee = payee;
    }

    /**
     * @return Returns the _cp2pWithinList.
     */
    public String getCp2pWithinList() {
        return _cp2pWithinList;
    }

    /**
     * @param withinList
     *            The _cp2pWithinList to set.
     */
    public void setCp2pWithinList(String withinList) {
        _cp2pWithinList = withinList;
    }

    /**
     * @return Returns the _parentOrOwnerRadio.
     */
    public String getListLevelCode() {
        return _listLevelCode;
    }

    /**
     * @param orOwnerRadio
     *            The _parentOrOwnerRadio to set.
     */
    public void setListLevelCode(String orOwnerRadio) {
        _listLevelCode = orOwnerRadio;
    }

    /**
     * @return Returns the _p2pWithinLevelList.
     */
    public ArrayList getP2pWithinLevelList() {
        return _p2pWithinLevelList;
    }

    /**
     * @param withinLevelList
     *            The _p2pWithinLevelList to set.
     */
    public void setP2pWithinLevelList(ArrayList withinLevelList) {
        _p2pWithinLevelList = withinLevelList;
    }

    /**
     * @return Returns the _listLevelType.
     */
    public String getListLevelType() {
        return _listLevelType;
    }

    /**
     * @param levelType
     *            The _listLevelType to set.
     */
    public void setListLevelType(String levelType) {
        _listLevelType = levelType;
    }

    /**
     * @return Returns the _userType.
     */
    public String getUserType() {
        return _userType;
    }

    /**
     * @param type
     *            The _userType to set.
     */
    public void setUserType(String type) {
        _userType = type;
    }

    /**
     * @return Returns the _agentListLevelType.
     */
    public String getAgentListLevelType() {
        return _agentListLevelType;
    }

    /**
     * @param listLevelType
     *            The _agentListLevelType to set.
     */
    public void setAgentListLevelType(String listLevelType) {
        _agentListLevelType = listLevelType;
    }

    /**
     * @return Returns the _agentRechargeByParentOnly.
     */
    public String getAgentRechargeByParentOnly() {
        return _agentRechargeByParentOnly;
    }

    /**
     * @param rechargeByParentOnly
     *            The _agentRechargeByParentOnly to set.
     */
    public void setAgentRechargeByParentOnly(String rechargeByParentOnly) {
        _agentRechargeByParentOnly = rechargeByParentOnly;
    }

    /**
     * @return Returns the _agentCp2pPayer.
     */
    public String getAgentCp2pPayer() {
        return _agentCp2pPayer;
    }

    /**
     * @param cp2pPayer
     *            The _agentCp2pPayer to set.
     */
    public void setAgentCp2pPayer(String cp2pPayer) {
        _agentCp2pPayer = cp2pPayer;
    }

    /**
     * @return Returns the _agentCp2pPayee.
     */
    public String getAgentCp2pPayee() {
        return _agentCp2pPayee;
    }

    /**
     * @param cp2pPayee
     *            The _agentCp2pPayee to set.
     */
    public void setAgentCp2pPayee(String cp2pPayee) {
        _agentCp2pPayee = cp2pPayee;
    }

    /**
     * @return Returns the _agentCp2pWithinList.
     */
    public String getAgentCp2pWithinList() {
        return _agentCp2pWithinList;
    }

    /**
     * @param cp2pWithinList
     *            The _agentCp2pWithinList to set.
     */
    public void setAgentCp2pWithinList(String cp2pWithinList) {
        _agentCp2pWithinList = cp2pWithinList;
    }

    /**
     * @return Returns the _agentListLevelCode.
     */
    public String getAgentListLevelCode() {
        return _agentListLevelCode;
    }

    /**
     * @param listLevelCode
     *            The _agentListLevelCode to set.
     */
    public void setAgentListLevelCode(String listLevelCode) {
        _agentListLevelCode = listLevelCode;
    }

    // added by nilesh
    /**
     * @return Returns the _defaultGrade.
     */
    public String getDefaultGrade() {
        return _defaultGrade;
    }

    /**
     * @param defaultGrade
     *            The _defaultGrade to set.
     */
    public void setDefaultGrade(String defaultGrade) {
        _defaultGrade = defaultGrade;
    }

    /**
     * @return Returns the _gradeListSize.
     */
    public int getGradeSize() {
        return _gradeListSize;
    }

    /**
     * @param gradeListSize
     *            The _gradeListSize to set.
     */
    public void setGradeSize(int gradeListSize) {
        _gradeListSize = gradeListSize;
    }

    /**
     * @return Returns the _gradeDef.
     */
    public String getGradeDef() {
        return _gradeDef;
    }

    /**
     * @param gradeDef
     *            The _gradeDef to set.
     */
    public void setGradeDef(String gradeDef) {
        _gradeDef = gradeDef;
    }

    /**
     * @return Returns the _gradeDefModify.
     */
    public String getGradeDefModify() {
        return _gradeDefModify;
    }

    /**
     * @param gradeDefModify
     *            The _gradeDefModify to set.
     */
    public void setGradeDefModify(String gradeDefModify) {
        _gradeDefModify = gradeDefModify;
    }

    // Added for authentication type
    public String getAuthType() {
        return _authType;
    }

    public void setAuthType(String type) {
        _authType = type;
    }

    public ArrayList getAuthTypeList() {
        return _authTypeList;
    }

    public void setAuthTypeList(ArrayList typeList) {
        _authTypeList = typeList;
    }

    public String getAuthTypeName() {
        return _authTypeName;
    }

    public void setAuthTypeName(String typeName) {
        _authTypeName = typeName;
    }
}