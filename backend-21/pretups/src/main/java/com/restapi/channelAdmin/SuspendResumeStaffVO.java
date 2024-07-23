package com.restapi.channelAdmin;

import com.btsl.pretups.domain.businesslogic.CategoryVO;

public class SuspendResumeStaffVO {
	
    private CategoryVO _categoryVO;
    private String _categoryCode;
    private String _loginUserID;
    private String _loginUserDomainCode;
    private String _loginUserCategoryCode;
    private String _geographicalCode;
    private String[] _geographicalCodeArray;
    private String _webLoginID;
    private String _requestType;
    private String _searchCriteria;// M = Mobile Search; L = LoginId Search; D = // Doamin, Category Search
    private String _channelCategoryDesc;
    private String _channelUserName;
    private String _msisdn;
    private String _ownerName;
    private String _parentName;
    private String _userType;
    private String _status;
    private String _email;
    private String _address1;
    private String _address2;
    private String userId;
    private long lastModified;
    
    public String getCategoryCode() {
        if (_categoryCode != null) {
            return _categoryCode.trim();
        }
        return _categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }
    
    public String getLoginUserCategoryCode() {
        return _loginUserCategoryCode;
    }

    /**
     * @param loginUserCategoryCode
     *            The loginUserCategoryCode to set.
     */
    public void setLoginUserCategoryCode(String loginUserCategoryCode) {
        _loginUserCategoryCode = loginUserCategoryCode;
    }

    /**
     * @return Returns the loginUserDomainCode.
     */
    public String getLoginUserDomainCode() {
        return _loginUserDomainCode;
    }

    /**
     * @param loginUserDomainCode
     *            The loginUserDomainCode to set.
     */
    public void setLoginUserDomainCode(String loginUserDomainCode) {
        _loginUserDomainCode = loginUserDomainCode;
    }

    /**
     * @return Returns the loginUserID.
     */
    public String getLoginUserID() {
        return _loginUserID;
    }

    /**
     * @param loginUserID
     *            The loginUserID to set.
     */
    public void setLoginUserID(String loginUserID) {
        _loginUserID = loginUserID;
    }
    
    public CategoryVO getCategoryVO() {
        return _categoryVO;
    }

    /**
     * @param categoryVO
     *            The categoryVO to set.
     */
    public void setCategoryVO(CategoryVO categoryVO) {
        _categoryVO = categoryVO;
    }
    
    public String getGeographicalCode() {
        if (_geographicalCode != null) {
            return _geographicalCode.trim();
        }

        return _geographicalCode;
    }

    /**
     * @param geographicalCode
     *            The geographicalCode to set.
     */
    public void setGeographicalCode(String geographicalCode) {
        _geographicalCode = geographicalCode;
    }

    /**
     * @return Returns the geographicalCodeArray.
     */
    public String[] getGeographicalCodeArray() {
        return _geographicalCodeArray;
    }

    public int getGeographicalCodeArrayCount() {
        if (_geographicalCodeArray != null && _geographicalCodeArray.length > 0) {
            return _geographicalCodeArray.length;
        } else {
            return 0;
        }
    }

    /**
     * @param geographicalCodeArray
     *            The geographicalCodeArray to set.
     */
    public void setGeographicalCodeArray(String[] geographicalCodeArray) {
        _geographicalCodeArray = geographicalCodeArray;
    }
    
    public String getWebLoginID() {
        if (_webLoginID != null) {
            return _webLoginID.trim();
        }

        return _webLoginID;
    }

    /**
     * @param webLoginID
     *            The webLoginID to set.
     */
    public void setWebLoginID(String webLoginID) {
        _webLoginID = webLoginID;
    }
    
    /**
     * @return Returns the requestType.
     */
    public String getRequestType() {
        if (_requestType != null) {
            return _requestType.trim();
        }
        return _requestType;
    }

    /**
     * @param requestType
     *            The requestType to set.
     */
    public void setRequestType(String requestType) {
        _requestType = requestType;
    }
    
    /**
     * @return Returns the searchCriteria.
     */
    public String getSearchCriteria() {
        return _searchCriteria;
    }

    /**
     * @param searchCriteria
     *            The searchCriteria to set.
     */
    public void setSearchCriteria(String searchCriteria) {
        _searchCriteria = searchCriteria;
    }
    
    /**
     * @return Returns the channelCategoryDesc.
     */
    public String getChannelCategoryDesc() {
        if (_channelCategoryDesc != null) {
            return _channelCategoryDesc.trim();
        }

        return _channelCategoryDesc;
    }

    /**
     * @param channelCategoryDesc
     *            The channelCategoryDesc to set.
     */
    public void setChannelCategoryDesc(String channelCategoryDesc) {
        _channelCategoryDesc = channelCategoryDesc;
    }
    
    /**
     * @return Returns the channelUserName.
     */
    public String getChannelUserName() {
        if (_channelUserName != null) {
            return _channelUserName.trim();
        }

        return _channelUserName;
    }

    /**
     * @param channelUserName
     *            The channelUserName to set.
     */
    public void setChannelUserName(String channelUserName) {
        _channelUserName = channelUserName;
    }
    
    /**
     * @return Returns the msisdn.
     */
    public String getMsisdn() {
        if (_msisdn != null) {
            return _msisdn.trim();
        }

        return _msisdn;
    }

    /**
     * @param msisdn
     *            The msisdn to set.
     */
    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }
    
    /**
     * @return Returns the ownerName.
     */
    public String getOwnerName() {
        return _ownerName;
    }

    /**
     * @param ownerName
     *            The ownerName to set.
     */
    public void setOwnerName(String ownerName) {
        _ownerName = ownerName;
    }
    
    public String getParentName() {
        return _parentName;
    }

    /**
     * @param parentName
     *            The parentName to set.
     */
    public void setParentName(String parentName) {
        _parentName = parentName;
    }
    
    /**
     * @return Returns the userType.
     */
    public String getUserType() {
        if (_userType != null) {
            return _userType.trim();
        }

        return _userType;
    }

    /**
     * @param userType
     *            The userType to set.
     */
    public void setUserType(String userType) {
        _userType = userType;
    }
    
    /**
     * @return Returns the email.
     */
    public String getEmail() {
        if (_email != null) {
            return _email.trim();
        }

        return _email;
    }

    /**
     * @param email
     *            The email to set.
     */
    public void setEmail(String email) {
        _email = email;
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        if (_status != null) {
            return _status.trim();
        }

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
     * @return Returns the address1.
     */
    public String getAddress1() {
        if (_address1 != null) {
            return _address1.trim();
        }

        return _address1;
    }

    /**
     * @param address1
     *            The address1 to set.
     */
    public void setAddress1(String address1) {
        _address1 = address1;
    }

    /**
     * @return Returns the address2.
     */
    public String getAddress2() {
        if (_address2 != null) {
            return _address2.trim();
        }

        return _address2;
    }

    /**
     * @param address2
     *            The address2 to set.
     */
    public void setAddress2(String address2) {
        _address2 = address2;
    }
    
    /**
     * @return Returns the userId.
     */
    public String getUserId() {
        if (userId != null) {
            return userId.trim();
        }

        return userId;
    }

    /**
     * @param userId
     *            The userId to set.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return Returns the lastModified.
     */
    public long getLastModified() {
        return lastModified;
    }

    /**
     * @param lastModified
     *            The lastModified to set.
     */
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
    

}
