/*
 * @# ChannelUserTransferForm.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Sandeep Goel Aug 30, 2005 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.web.pretups.channel.user.web;

import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;

/*import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;*/

import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;

public class ChannelUserTransferForm extends UserHierarchyForm {
    private String _toZoneCode;
    private String _toOwnerID;
    private String _toParentCategoryCode;
    private String _toParentUserID;

    private ArrayList _toParentCategoryList = null;

    private String _toOwnerName;
    private String _toParentCategoryDesc;
    private String _toParentUserName;
    private String _toZoneCodeDesc;

    public ChannelUserTransferForm() {
        super();
    }

    public void flush() {
        super.flush();
        _toZoneCode = null;
        _toOwnerID = null;
        _toParentCategoryCode = null;
        _toParentUserID = null;
        _toParentCategoryList = null;
        _toOwnerName = null;
        _toParentCategoryDesc = null;
        _toParentUserName = null;
        _toZoneCodeDesc = null;
    }

    public void flushOwner() {
        super.semiFlush();
        _toZoneCode = null;
        _toOwnerID = null;
        _toParentCategoryCode = null;
        _toParentUserID = null;
        _toParentCategoryList = null;
        _toOwnerName = null;
        _toParentCategoryDesc = null;
        _toParentUserName = null;
        _toZoneCodeDesc = null;
    }

    public String getToOwnerID() {
        return _toOwnerID;
    }

    public void setToOwnerID(String toOwnerID) {
        _toOwnerID = toOwnerID;
    }

    public String getToOwnerName() {
        return _toOwnerName;
    }

    public void setToOwnerName(String toOwnerName) {
        _toOwnerName = toOwnerName;
    }

    public String getToParentCategoryCode() {
        return _toParentCategoryCode;
    }

    public void setToParentCategoryCode(String toParentCategoryCode) {
        _toParentCategoryCode = toParentCategoryCode;
    }

    public String getToParentCategoryDesc() {
        return _toParentCategoryDesc;
    }

    public void setToParentCategoryDesc(String toParentCategoryDesc) {
        _toParentCategoryDesc = toParentCategoryDesc;
    }

    public ArrayList getToParentCategoryList() {
        return _toParentCategoryList;
    }

    public int getToParentCategoryListSize() {
        if (_toParentCategoryList == null) {
            return -1;
        }
        return _toParentCategoryList.size();
    }

    public void setToParentCategoryList(ArrayList toParentCategoryList) {
        _toParentCategoryList = toParentCategoryList;
    }

    public String getToParentUserID() {
        return _toParentUserID;
    }

    public void setToParentUserID(String toParentUserID) {
        _toParentUserID = toParentUserID;
    }

    public String getToParentUserName() {
        return _toParentUserName;
    }

    public void setToParentUserName(String toParentUserName) {
        _toParentUserName = toParentUserName;
    }

    public String getToZoneCode() {
        return _toZoneCode;
    }

    public void setToZoneCode(String toZoneCode) {
        _toZoneCode = toZoneCode;
    }

    public String getToZoneCodeDesc() {
        return _toZoneCodeDesc;
    }

    public void setToZoneCodeDesc(String toZoneCodeDesc) {
        _toZoneCodeDesc = toZoneCodeDesc;
    }

}
