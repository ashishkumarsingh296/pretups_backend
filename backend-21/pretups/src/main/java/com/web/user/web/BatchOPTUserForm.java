package com.web.user.web;

import java.util.ArrayList;
import java.util.HashMap;
/*import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;*/

/**
 * @(#)BatchOPTUserForm.java
 *                           Copyright(c) 2007, Bharti Telesoft Ltd.
 *                           All Rights Reserved
 * 
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Shishupal Singh 19/03/2007 Initial Creation
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 * 
 */

public class BatchOPTUserForm extends UserForm {

	private static final long serialVersionUID = 1L;
	private String _batchName;
    //private FormFile _file;
    private String _categoryStr;
    private HashMap _batchOPTUserMasterMap;
    private ArrayList _errorList;
    private String _errorFlag;
    private int _totalRecords;
    private String _noOfRecords;

    public int getCategoryListSize() {
        if (this.getCategoryList() != null) {
            return this.getCategoryList().size();
        } else {
            return 0;
        }
    }

    public void flush() {
        super.flush();
        _batchName = null;
    }

    public void setBatchName(String batchName) {
        _batchName = batchName;
    }

    public String getBatchName() {
        return _batchName;
    }
/*

    public void setFile(FormFile file) {
        _file = file;
    }

    public FormFile getFile() {
        return _file;
    }
*/

    public void setCategoryStr(String categoryStr) {
        _categoryStr = categoryStr;
    }

    public String getCategoryStr() {
        return _categoryStr;
    }

    public void setBatchOPTUserMasterMap(HashMap batchOPTUserMasterMap) {
        _batchOPTUserMasterMap = batchOPTUserMasterMap;
    }

    public HashMap getBatchOPTUserMasterMap() {
        return _batchOPTUserMasterMap;
    }

    public ArrayList getErrorList() {
        return _errorList;
    }

    public void setErrorList(ArrayList errorList) {
        _errorList = errorList;
    }

    public String getErrorFlag() {
        return _errorFlag;
    }

    public void setErrorFlag(String errorFlag) {
        _errorFlag = errorFlag;
    }

    public int getTotalRecords() {
        return _totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        _totalRecords = totalRecords;
    }

    public String getNoOfRecords() {
        return _noOfRecords;
    }

    public void setNoOfRecords(String noOfRecords) {
        _noOfRecords = noOfRecords;
    }

}
