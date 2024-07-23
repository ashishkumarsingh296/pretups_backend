package com.web.pretups.restrictedsubs.web;

import java.util.ArrayList;

/**
 * @# RestrictedSubscriberForm.java
 * 
 *    Created by Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    Sandeep Goel Mar 29, 2006 Initial creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2005 Bharti Telesoft Ltd.
 *    This is the comman FormBean for the Restricted Subscriber Module for the
 *    comman validation and comman jsp
 *    for the user selection. If any new menu option has the requirements to
 *    search the user for the selection then
 *    its form bean must extends it.
 */
public class RestrictedSubscriberForm  {

    private ArrayList _geoDomainList;
    private String _geoDomainListSize;
    private String _geoDomainCode;
    private String _geoDomainName;
    private String _categoryCode;
    private String _categoryName;
    private String _ownerCategoryName;
    private String _userID;
    private String _userName;
    private ArrayList _categoryList;
    private String _domainCode;
    private String _domainName;
    private ArrayList _userList;
    private int _userListSize;
    private ArrayList _domainList;
    private String _loginUserType;
    private String _loginUserCatCode = null;
    private String _loginUserCatName = null;
    private String _loginUserID = null;
    private String _loginUserName = null;
    private boolean _selfAllow = false;
    private String _ownerID = null;
    private String _ownerName = null;
    private boolean _ownerOnly = false;

    /*
     * These variables are used to perform the movement between the parent
     * action and the child action
     */
    private boolean _isOperationPerformed = false;
    private long _time = 0;
    /*
     * ends here
     */
    private boolean _isCorporate = false;
    private boolean _isSoho = false;
    private boolean _isNormal = false;
    private String _fileType = null;

    /**
     * Flush some contents of the form bean
     */

    public void semiFlush() {
        // for addSubscriber.jsp
        _userName = null;
        _userID = null;
    }

    /**
     * Flush some contents of the form bean
     */
    public void flush() {
        _geoDomainList = null;
        _geoDomainListSize = null;
        _geoDomainCode = null;
        _geoDomainName = null;
        _categoryCode = null;
        _categoryName = null;
        _userID = null;
        _userName = null;
        _categoryList = null;
        _domainCode = null;
        _domainName = null;
        _userList = null;
        _userListSize = 0;
        _domainList = null;
        _loginUserType = null;
        //_successForward = null;
        //_errorForward = null;
        _isOperationPerformed = false;
        _loginUserCatCode = null;
        _loginUserID = null;
        _loginUserName = null;
        _ownerID = null;
        _selfAllow = false;
        _ownerCategoryName = null;
        _ownerName = null;
        _ownerOnly = false;
        _loginUserCatName = null;
        _isCorporate = false;
        _isSoho = false;
        _isNormal = false;
    }

    public String getOwnerCategoryName() {
        return _ownerCategoryName;
    }

    public void setOwnerCategoryName(String ownerCategoryName) {
        _ownerCategoryName = ownerCategoryName;
    }

    public void setOperationPerformed(boolean isOperationPerformed) {
        _isOperationPerformed = isOperationPerformed;
    }

    public void setUserListSize(int userListSize) {
        _userListSize = userListSize;
    }

    public String getCategoryCode() {
        return _categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    public ArrayList getCategoryList() {
        return _categoryList;
    }

    public void setCategoryList(ArrayList categoryList) {
        _categoryList = categoryList;
    }

    public String getCategoryName() {
        return _categoryName;
    }

    public void setCategoryName(String categoryName) {
        _categoryName = categoryName;
    }

    public String getDomainCode() {
        return _domainCode;
    }

    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
    }

    public ArrayList getDomainList() {
        return _domainList;
    }

    public int getDomainListSize() {
        if (_domainList != null) {
            return _domainList.size();
        }
        return 0;
    }

    public void setDomainList(ArrayList domainList) {
        _domainList = domainList;
    }

    public String getDomainName() {
        return _domainName;
    }

    public void setDomainName(String domainName) {
        _domainName = domainName;
    }

    public String getGeoDomainCode() {
        return _geoDomainCode;
    }

    public void setGeoDomainCode(String geoDomainCode) {
        _geoDomainCode = geoDomainCode;
    }

    public ArrayList getGeoDomainList() {
        return _geoDomainList;
    }

    public void setGeoDomainList(ArrayList geoDomainList) {
        _geoDomainList = geoDomainList;
    }

    public String getGeoDomainListSize() {
        return _geoDomainListSize;
    }

    public void setGeoDomainListSize(String geoDomainListSize) {
        _geoDomainListSize = geoDomainListSize;
    }

    public String getGeoDomainName() {
        return _geoDomainName;
    }

    public void setGeoDomainName(String geoDomainName) {
        _geoDomainName = geoDomainName;
    }

    public String getLoginUserType() {
        return _loginUserType;
    }

    public void setLoginUserType(String loginUserType) {
        _loginUserType = loginUserType;
    }

    public String getUserID() {
        return _userID;
    }

    public void setUserID(String userID) {
        _userID = userID;
    }

    public ArrayList getUserList() {
        return _userList;
    }

    public void setUserList(ArrayList userList) {
        _userList = userList;
    }

    public String getUserName() {
        return _userName;
    }

    public void setUserName(String userName) {
        _userName = userName;
    }

    public int getUserListSize() {
        return _userListSize;
    }

    public boolean getIsOperationPerformed() {
        return _isOperationPerformed;
    }

    public void setIsOperationPerformed(boolean isOperationPerformed) {
        _isOperationPerformed = isOperationPerformed;
    }

    public String getLoginUserCatCode() {
        return _loginUserCatCode;
    }

    public void setLoginUserCatCode(String loginUserCatCode) {
        _loginUserCatCode = loginUserCatCode;
    }

    public String getLoginUserID() {
        return _loginUserID;
    }

    public void setLoginUserID(String loginUserID) {
        _loginUserID = loginUserID;
    }

    public String getLoginUserName() {
        return _loginUserName;
    }

    public void setLoginUserName(String loginUserName) {
        _loginUserName = loginUserName;
    }

    public String getOwnerID() {
        return _ownerID;
    }

    public void setOwnerID(String ownerID) {
        _ownerID = ownerID;
    }

    public boolean getSelfAllow() {
        return _selfAllow;
    }

    public void setSelfAllow(boolean selfAllow) {
        _selfAllow = selfAllow;
    }

    public String getOwnerName() {
        return _ownerName;
    }

    public void setOwnerName(String ownerName) {
        _ownerName = ownerName;
    }

    public boolean getOwnerOnly() {
        return _ownerOnly;
    }

    public void setOwnerOnly(boolean ownerOnly) {
        _ownerOnly = ownerOnly;
    }

    public String getLoginUserCatName() {
        return _loginUserCatName;
    }

    public void setLoginUserCatName(String loginUserCatName) {
        _loginUserCatName = loginUserCatName;
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
     * @return Returns the isCorporate.
     */
    public boolean isCorporate() {
        return _isCorporate;
    }

    /**
     * @param isCorporate
     *            The isCorporate to set.
     */
    public void setCorporate(boolean isCorporate) {
        _isCorporate = isCorporate;
    }

    /**
     * @return Returns the isNormal.
     */
    public boolean isNormal() {
        return _isNormal;
    }

    /**
     * @param isNormal
     *            The isNormal to set.
     */
    public void setNormal(boolean isNormal) {
        _isNormal = isNormal;
    }

    /**
     * @return Returns the isSoho.
     */
    public boolean isSoho() {
        return _isSoho;
    }

    /**
     * @param isSoho
     *            The isSoho to set.
     */
    public void setSoho(boolean isSoho) {
        _isSoho = isSoho;
    }

    /**
     * @return Returns the fileType.
     */
    public String getFileType() {
        return _fileType;
    }

    /**
     * @param fileType
     *            The fileType to set.
     */
    public void setFileType(String fileType) {
        _fileType = fileType;
    }
}
