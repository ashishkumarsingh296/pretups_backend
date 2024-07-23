/*
 * #SubLookUpForm.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jun 21, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.master.web;

import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;

public class SubLookUpForm {

    private String _lookupCode;
    private String _lookupName;
    private String _subLookupName = null;
    private String _lookupType;
    private ArrayList _lookupList;

    // For modify sub lookup

    private ArrayList _subLookupList;
    private String _subLookupCode;
    private String _deleteAllowed;

    private String _lkCode;
    private String _subLkCode;

    private long _lastModified;

    /**
     * @return Returns the lkCode.
     */
    public String getLkCode() {
        return _lkCode;
    }

    /**
     * @param lkCode
     *            The lkCode to set.
     */
    public void setLkCode(String lkCode) {
        _lkCode = lkCode;
    }

    /**
     * @return Returns the subLkCode.
     */
    public String getSubLkCode() {
        return _subLkCode;
    }

    /**
     * @param subLkCode
     *            The subLkCode to set.
     */
    public void setSubLkCode(String subLkCode) {
        _subLkCode = subLkCode;
    }

    /**
     * To get the value of lookupCode field
     * 
     * @return lookupCode.
     */
    public String getLookupCode() {
        return _lookupCode;
    }

    /**
     * To set the value of lookupCode field
     */
    public void setLookupCode(String lookupCode) {
        _lookupCode = lookupCode;
    }

    /**
     * To get the value of lookupName field
     * 
     * @return lookupName.
     */
    public String getLookupName() {
        return _lookupName;
    }

    /**
     * To set the value of lookupName field
     */
    public void setLookupName(String lookupName) {
        _lookupName = lookupName;
    }

    /**
     * To get the value of lookupType field
     * 
     * @return lookupType.
     */
    public String getLookupType() {
        return _lookupType;
    }

    /**
     * To set the value of lookupType field
     */
    public void setLookupType(String lookupType) {
        _lookupType = lookupType;
    }

    /**
     * To get the value of subLookupName field
     * 
     * @return subLookupName.
     */
    public String getSubLookupName() {
        return _subLookupName;
    }

    /**
     * To set the value of subLookupName field
     */
    public void setSubLookupName(String subLookupName) {
        if (subLookupName != null) {
            _subLookupName = subLookupName.trim();
        }
    }

    /**
     * To get the value of lookupList field
     * 
     * @return lookupList.
     */
    public ArrayList getLookupList() {
        return _lookupList;
    }

    /**
     * To set the value of lookupList field
     */
    public void setLookupList(ArrayList lookupList) {
        _lookupList = lookupList;
    }

    /**
     * To get the value of subLookupList field
     * 
     * @return subLookupList.
     */
    public ArrayList getSubLookupList() {
        return _subLookupList;
    }

    /**
     * To set the value of subLookupList field
     */
    public void setSubLookupList(ArrayList subLookupList) {
        _subLookupList = subLookupList;
    }

    /**
     * To get the value of subLookupCode field
     * 
     * @return subLookupCode.
     */
    public String getSubLookupCode() {
        return _subLookupCode;
    }

    /**
     * To set the value of subLookupCode field
     */
    public void setSubLookupCode(String subLookupCode) {
        _subLookupCode = subLookupCode;
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

    public void flush() {
        _lookupCode = null;
        _lookupName = null;
        _subLookupName = null;
        _lookupType = null;
        _lookupList = null;
        _subLookupList = null;
        _subLookupCode = null;
        _lastModified = 0;
        _deleteAllowed = null;
        _lkCode = null;
        _subLkCode = null;
    }

    /**
     * @return Returns the deleteAllowed.
     */
    public String getDeleteAllowed() {
        return _deleteAllowed;
    }

    /**
     * @param deleteAllowed
     *            The deleteAllowed to set.
     */
    public void setDeleteAllowed(String deleteAllowed) {
        _deleteAllowed = deleteAllowed;
    }
}
