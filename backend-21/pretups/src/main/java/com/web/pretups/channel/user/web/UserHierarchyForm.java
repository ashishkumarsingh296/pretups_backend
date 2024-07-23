package com.web.pretups.channel.user.web;

/**
 * @# UserHierarchyForm.java
 * 
 *    Created by Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    Manoj kumar Aug 31, 2005 Initial creation
 *    Sandeep Goel June 23, 2006 Customization Msisdn based operations
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2005 Bharti Telesoft Ltd.
 */

import java.util.ArrayList;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;

public class UserHierarchyForm  {

    private String _userID;
    private String _zoneCode;
    private String _domainCode;
    private String _ownerID;
    private String _parentCategoryCode;
    private String _parentUserID;
    private String _transferUserCategoryCode;
    private String _userTransferMode;
    private String _createdBy;
    private Date _createdOn = null;
    private String _modifiedBy;
    private Date _modifiedOn = null;
    private String _requestFor;
    private String _loginID;

    private ArrayList _domainList = null;
    private ArrayList _parentCategoryList = null;
    private ArrayList _transferUserCategoryList = null;
    private ArrayList _userTransferModeList = null;
    private ArrayList _userHierarchyList = null; // list contains all users
    // hierarchy used to display
    // all information
    private ArrayList _ownerList = null; // list contains all owner users used
    // in the search
    private ArrayList _parentUserList = null; // list contains all parent users
    // used in the search
    private ArrayList _categoryList = null;
    private ArrayList _zoneList = null;
    private ArrayList _userList = null; // list contains of all users which are
    // to be transfered
    private ArrayList _statusList = null; // list contains status of the users
    // for view user hierarchy
    private String _zoneCodeDesc;
    private String _userName;

    private String _domainCodeDesc;
    private String _ownerName;
    private String _ownerCategory;
    private String _parentUserName;
    private String _transferUserCategoryDesc;
    private String _parentCategoryDesc;
    private String _status;
    private boolean _isChannelUser = false;

    private String _msisdn = null;
    private boolean _isOperationNotAllow = false;
    private long _time = 0;
    // Added for the Transferred channel users report on 07-Nov-2008 by Vinay
    private String _fromDate;
    private String _toDate;
    private ArrayList _trnsfrdUsrHierList = null;
    private String _prevBalanceStr = null;
    private String _prevUserName = null;
    private String _prevParentname = null;
    private String _prevCategoryCode = null;
    private String _prevUserNameWithCat = null;
    private String _prevUserAndParentNameWithCat = null;
    private boolean _isHirDownloadAllow = false;

    public int getZoneListSize() {
        if (_zoneList != null) {
            return _zoneList.size();
        }
        return 0;
    }

    public int getDomainListSize() {
        if (_domainList != null) {
            return _domainList.size();
        }
        return 0;
    }

    /**
     * @return Returns the loginID.
     */
    public String getLoginID() {
        return _loginID;
    }

    /**
     * @param loginID
     *            The loginID to set.
     */
    public void setLoginID(String loginID) {
        _loginID = loginID;
    }

    public UserHierarchyForm() {
        super();
    }

    public ArrayList getStatusList() {
        return _statusList;
    }

    public void setStatusList(ArrayList statusList) {
        _statusList = statusList;
    }

    public int getUserListSize() {
        if (_userList != null) {
            return _userList.size();
        }
        return 0;
    }

    public int getParentUserListSize() {
        if (_parentUserList != null) {
            return _parentUserList.size();
        }
        return 0;
    }

    public int getOwnerListSize() {
        if (_ownerList != null) {
            return _ownerList.size();
        }
        return 0;
    }

    public int getUserHierarchyListSize() {
        if (_userHierarchyList != null) {
            return _userHierarchyList.size();
        }
        return 0;
    }

    public int getCategoryListSize() {
        if (_categoryList != null) {
            return _categoryList.size();
        }
        return 0;
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

    public void setUserHierarchyListIndexed(int i, ChannelUserVO vo) {
        _userHierarchyList.set(i, vo);
    }

    public ChannelUserVO getUserHierarchyListIndexed(int i) {
        return (ChannelUserVO) _userHierarchyList.get(i);
    }

    public void setUserListIndexed(int i, ChannelUserTransferVO vo) {
        _userList.set(i, vo);
    }

    public ChannelUserTransferVO getUserListIndexed(int i) {
        return (ChannelUserTransferVO) _userList.get(i);
    }

    public void setCategoryListIndexed(int i, CategoryVO vo) {
        _categoryList.set(i, vo);
    }

    public CategoryVO getCategoryListIndexed(int i) {
        return (CategoryVO) _categoryList.get(i);
    }

    public String getDomainCode() {
        return _domainCode;
    }

    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
    }

    public String getDomainCodeDesc() {
        return _domainCodeDesc;
    }

    public void setDomainCodeDesc(String domainCodeDesc) {
        _domainCodeDesc = domainCodeDesc;
    }

    public ArrayList getDomainList() {
        return _domainList;
    }

    public void setDomainList(ArrayList domainList) {
        _domainList = domainList;
    }

    public String getOwnerCategory() {
        return _ownerCategory;
    }

    public void setOwnerCategory(String ownerCategory) {
        _ownerCategory = ownerCategory;
    }

    public String getOwnerID() {
        return _ownerID;
    }

    public void setOwnerID(String ownerID) {
        _ownerID = ownerID;
    }

    public String getOwnerName() {
        return _ownerName;
    }

    public void setOwnerName(String ownerName) {
        _ownerName = ownerName;
    }

    public String getParentCategoryCode() {
        return _parentCategoryCode;
    }

    public void setParentCategoryCode(String parentCategoryCode) {
        _parentCategoryCode = parentCategoryCode;
    }

    public String getParentCategoryDesc() {
        return _parentCategoryDesc;
    }

    public void setParentCategoryDesc(String parentCategoryDesc) {
        _parentCategoryDesc = parentCategoryDesc;
    }

    public ArrayList getParentCategoryList() {
        return _parentCategoryList;
    }

    public int getParentCategoryListSize() {
        if (_parentCategoryList != null) {
            return _parentCategoryList.size();
        }
        return 0;
    }

    public void setParentCategoryList(ArrayList parentCategoryList) {
        _parentCategoryList = parentCategoryList;
    }

    public String getParentUserID() {
        return _parentUserID;
    }

    public void setParentUserID(String parentUserID) {
        _parentUserID = parentUserID;
    }

    public String getParentUserName() {
        return _parentUserName;
    }

    public void setParentUserName(String parentUserName) {
        _parentUserName = parentUserName;
    }

    public String getUserTransferMode() {
        return _userTransferMode;
    }

    public void setUserTransferMode(String transferMode) {
        _userTransferMode = transferMode;
    }

    public ArrayList getUserTransferModeList() {
        return _userTransferModeList;
    }

    public void setUserTransferModeList(ArrayList transferModeList) {
        _userTransferModeList = transferModeList;
    }

    public String getTransferUserCategoryCode() {
        return _transferUserCategoryCode;
    }

    public void setTransferUserCategoryCode(String transferUserCategoryCode) {
        _transferUserCategoryCode = transferUserCategoryCode;
    }

    public String getTransferUserCategoryDesc() {
        return _transferUserCategoryDesc;
    }

    public void setTransferUserCategoryDesc(String transferUserCategoryDesc) {
        _transferUserCategoryDesc = transferUserCategoryDesc;
    }

    public ArrayList getTransferUserCategoryList() {
        return _transferUserCategoryList;
    }

    public void setTransferUserCategoryList(ArrayList transferUserCategoryList) {
        _transferUserCategoryList = transferUserCategoryList;
    }

    public ArrayList getUserHierarchyList() {
        return _userHierarchyList;
    }

    public void setUserHierarchyList(ArrayList userHierarchyList) {
        _userHierarchyList = userHierarchyList;
    }

    public ArrayList getOwnerList() {
        return _ownerList;
    }

    public void setOwnerList(ArrayList ownerList) {
        _ownerList = ownerList;
    }

    public ArrayList getParentUserList() {
        return _parentUserList;
    }

    public void setParentUserList(ArrayList parentUserList) {
        _parentUserList = parentUserList;
    }

    public ArrayList getUserList() {
        return _userList;
    }

    public void setUserList(ArrayList userList) {
        _userList = userList;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * @return Returns the zoneList.
     */
    public ArrayList getZoneList() {
        return _zoneList;
    }

    /**
     * @param zoneList
     *            The zoneList to set.
     */
    public void setZoneList(ArrayList zoneList) {
        _zoneList = zoneList;
    }

    public String getUserID() {
        return _userID;
    }

    public void setUserID(String userID) {
        _userID = userID;
    }

    public String getUserName() {
        return _userName;
    }

    public void setUserName(String userName) {
        _userName = userName;
    }

    public String getZoneCode() {
        return _zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        _zoneCode = zoneCode;
    }

    public String getZoneCodeDesc() {
        return _zoneCodeDesc;
    }

    public void setZoneCodeDesc(String zoneCodeDesc) {
        _zoneCodeDesc = zoneCodeDesc;
    }

    public String getRequestFor() {
        return _requestFor;
    }

    public void setRequestFor(String requestFor) {
        _requestFor = requestFor;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public boolean getChannelUser() {
        return _isChannelUser;
    }

    public void setChannelUser(boolean isChannelUser) {
        _isChannelUser = isChannelUser;
    }

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    public boolean getIsOperationNotAllow() {
        return _isOperationNotAllow;
    }

    public void setIsOperationNotAllow(boolean isOperationNotAllow) {
        _isOperationNotAllow = isOperationNotAllow;
    }

    public boolean getIsChannelUser() {
        return _isChannelUser;
    }

    public void flush() {
        _userID = null;
        _zoneCode = null;
        _domainCode = null;
        _ownerID = null;
        _parentCategoryCode = null;
        _parentUserID = null;
        _transferUserCategoryCode = null;
        _userTransferMode = null;
        _createdBy = null;
        _modifiedBy = null;
        _zoneCodeDesc = null;
        _userName = null;
        _domainCodeDesc = null;
        _ownerName = null;
        _ownerCategory = null;
        _parentUserName = null;
        _transferUserCategoryDesc = null;
        _parentCategoryDesc = null;
        _status = null;
        _createdOn = null;
        _modifiedOn = null;
        ;
        _domainList = null;
        _parentCategoryList = null;
        _transferUserCategoryList = null;
        _userTransferModeList = null;
        _userHierarchyList = null;
        _ownerList = null;
        _parentUserList = null;
        _categoryList = null;
        _zoneList = null;
        _userList = null;
        _requestFor = null;
        _statusList = null;
        _isChannelUser = false;
        _msisdn = null;
        _isOperationNotAllow = false;
        _loginID = null;
        _fromDate = null;
        _toDate = null;
        _isHirDownloadAllow = false;
    }

    public void semiFlush() {
        _userID = null;
        _userName = null;
        _userHierarchyList = null;
        _userList = null;
        _isOperationNotAllow = false;
    }

    public void flushMsisdnInfo(UserVO p_userVO) {

        if (p_userVO == null || !PretupsI.CHANNEL_USER_TYPE.equals(p_userVO.getUserType())) {
            this.setOwnerName(null);
            this.setOwnerID(null);
            if (this.getZoneListSize() > 1) {
                this.setZoneCode(null);
            }
            this.setDomainCode(null);
            this.setDomainCodeDesc(null);
            this.setParentUserName(null);
            this.setParentUserID(null);
            this.setParentCategoryDesc(null);
            this.setTransferUserCategoryDesc(null);
            this.setTransferUserCategoryCode(null);
        } else {
            // this.setParentUserID(null);
            this.setParentCategoryDesc(null);
            this.setTransferUserCategoryDesc(null);
            this.setTransferUserCategoryCode(null);

            this.setOwnerID(p_userVO.getOwnerID());
            if (PretupsI.CATEGORY_TYPE_AGENT.equals(p_userVO.getCategoryVO().getCategoryType()) && PretupsI.NO.equals(p_userVO.getCategoryVO().getHierarchyAllowed())) {
                this.setParentUserID(p_userVO.getParentID());
                this.setParentUserName(p_userVO.getParentName());
            } else {
                this.setParentUserID(p_userVO.getUserID());
                this.setParentUserName(p_userVO.getUserName());
            }
        }
    }

    /**
     * @return Returns the time.
     */
    public long getTime() {
        return _time;
    }

    /**
     * @param time
     *            The time to set.
     */
    public void setTime(long time) {
        _time = time;
    }

    /**
     * @return Returns the _fromDate.
     */
    public String getFromDate() {
        return _fromDate;
    }

    /**
     * @param date
     *            The _fromDate to set.
     */
    public void setFromDate(String fromDate) {
        _fromDate = fromDate;
    }

    /**
     * @return Returns the _toDate.
     */
    public String getToDate() {
        return _toDate;
    }

    /**
     * @param date
     *            The _toDate to set.
     */
    public void setToDate(String toDate) {
        _toDate = toDate;
    }

    /**
     * @return Returns the _trsfrdUsrHierList.
     */
    public ArrayList getTrnsfrdUsrHierList() {
        return _trnsfrdUsrHierList;
    }

    /**
     * @param usrHierList
     *            The _trsfrdUsrHierList to set.
     */
    public void setTrnsfrdUsrHierList(ArrayList trnsfrdUsrHierList) {
        _trnsfrdUsrHierList = trnsfrdUsrHierList;
    }

    /**
     * @return Returns the _prevBalanceStr.
     */
    public String getPrevBalanceStr() {
        return _prevBalanceStr;
    }

    /**
     * @param balanceStr
     *            The _prevBalanceStr to set.
     */
    public void setPrevBalanceStr(String prevBalanceStr) {
        _prevBalanceStr = prevBalanceStr;
    }

    public int getTrnsfrdUsrHierListSize() {
        if (_trnsfrdUsrHierList != null) {
            return _trnsfrdUsrHierList.size();
        }
        return 0;
    }

    public void setTrnsfrdUsrHierListIndexed(int i, ChannelUserVO vo) {
        _trnsfrdUsrHierList.set(i, vo);
    }

    public ChannelUserVO getTrnsfrdUsrHierListIndexed(int i) {
        return (ChannelUserVO) _trnsfrdUsrHierList.get(i);
    }

    public String getNoBalanceInfo() {
        return " This is not a shifted user.";
    }

    public String getBlankString() {
        return "";
    }

    /**
     * @return Returns the _prevUserAndParentNameWithCat.
     */
    public String getPrevUserAndParentNameWithCat() {
        return _prevUserAndParentNameWithCat;
    }

    /**
     * @param userAndParentNameWithCat
     *            The _prevUserAndParentNameWithCat to set.
     */
    public void setPrevUserAndParentNameWithCat(String userAndParentNameWithCat) {
        _prevUserAndParentNameWithCat = userAndParentNameWithCat;
    }

    /**
     * @return Returns the _prevUserNameWithCat.
     */
    public String getPrevUserNameWithCat() {
        return _prevUserNameWithCat;
    }

    /**
     * @param userNameWithCat
     *            The _prevUserNameWithCat to set.
     */
    public void setPrevUserNameWithCat(String userNameWithCat) {
        _prevUserNameWithCat = userNameWithCat;
    }

    /**
     * @return Returns the _prevCategoryCode.
     */
    public String getPrevCategoryCode() {
        return _prevCategoryCode;
    }

    /**
     * @param categoryCode
     *            The _prevCategoryCode to set.
     */
    public void setPrevCategoryCode(String categoryCode) {
        _prevCategoryCode = categoryCode;
    }

    /**
     * @return Returns the _prevParentname.
     */
    public String getPrevParentname() {
        return _prevParentname;
    }

    /**
     * @param parentname
     *            The _prevParentname to set.
     */
    public void setPrevParentname(String parentname) {
        _prevParentname = parentname;
    }

    /**
     * @return Returns the _prevUserName.
     */
    public String getPrevUserName() {
        return _prevUserName;
    }

    /**
     * @param userName
     *            The _prevUserName to set.
     */
    public void set_prevUserName(String userName) {
        _prevUserName = userName;
    }

    /**
     * @return the isHirDownloadAllow
     */
    public boolean isHirDownloadAllow() {
        return _isHirDownloadAllow;
    }

    /**
     * @param isHirDownloadAllow
     *            the isHirDownloadAllow to set
     */
    public void setHirDownloadAllow(boolean isHirDownloadAllow) {
        _isHirDownloadAllow = isHirDownloadAllow;
    }

}
