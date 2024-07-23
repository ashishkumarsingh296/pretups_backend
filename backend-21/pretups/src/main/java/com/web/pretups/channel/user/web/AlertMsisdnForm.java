/*
 * @# AlertMsisdnFrom.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Vikas Jauhari March 4, 2011 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2006 Comviva Technologies Ltd.
 */

package com.web.pretups.channel.user.web;

import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.pretups.batch.businesslogic.BatchesVO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.util.BTSLUtil;

public class AlertMsisdnForm {
    private String _batchName;
    private String _batchID;
    private String _domainCode;
    private String _domainName;
    private String _geographyCode;
    private String _geographyName;
    private String _selectedIndex;
    private String _errorFlag;
    private String _noOfRecords;
    private ArrayList _domainList = null;
    private ArrayList _geographyList = null;
    private ArrayList _batchList = null;
    private ArrayList _batchDetailsList = null;
    private ArrayList _errorList = null;
    private int _totalRecords;
    private HashMap _bulkUserMasterMap;
    //private FormFile _file;
    private BatchesVO _batchesVO = null;
    private String _fromDate;
    private String _toDate;
    private ArrayList _categoryList = null;
    private String _categoryCode = null;
    private String _categoryName = null;
    private CategoryVO _categoryVO = null;
    private ArrayList _domainAllList = null;
    private String _domainType = null;
    private String _fileName = null;

    /**
     * @return Returns the fromDate.
     */
    public String getFromDate() {
        return _fromDate;
    }

    /**
     * @param fromDate
     *            The fromDate to set.
     */
    public void setFromDate(String fromDate) {
        _fromDate = fromDate;
    }

    /**
     * @return Returns the toDate.
     */
    public String getToDate() {
        return _toDate;
    }

    /**
     * @param toDate
     *            The toDate to set.
     */
    public void setToDate(String toDate) {
        _toDate = toDate;
    }

    /**
     * @return Returns the batchID.
     */
    public String getBatchID() {
        return _batchID;
    }

    /**
     * @param batchID
     *            The batchID to set.
     */
    public void setBatchID(String batchID) {
        if (!BTSLUtil.isNullString(_batchID)) {
            _batchID = batchID.trim();
        } else {
            _batchID = batchID;
        }
    }

    /**
     * @return Returns the batchDetailsList.
     */
    public ArrayList getBatchDetailsList() {
        return _batchDetailsList;
    }

    /**
     * @param batchDetailsList
     *            The batchDetailsList to set.
     */
    public void setBatchDetailsList(ArrayList batchDetailsList) {
        _batchDetailsList = batchDetailsList;
    }

    /**
     * @return Returns the batchesVO.
     */
    public BatchesVO getBatchesVO() {
        return _batchesVO;
    }

    /**
     * @param batchesVO
     *            The batchesVO to set.
     */
    public void setBatchesVO(BatchesVO batchesVO) {
        _batchesVO = batchesVO;
    }

    /**
     * @return Returns the batchName.
     */
    public String getBatchName() {
        return _batchName;
    }

    /**
     * @param batchName
     *            The batchName to set.
     */
    public void setBatchName(String batchName) {
        _batchName = batchName;
    }

    /**
     * @return Returns the selectedIndex.
     */
    public String getSelectedIndex() {
        return _selectedIndex;
    }

    /**
     * @param selectedIndex
     *            The selectedIndex to set.
     */
    public void setSelectedIndex(String selectedIndex) {
        _selectedIndex = selectedIndex;
    }

    /**
     * @return Returns the batchList.
     */
    public ArrayList getBatchList() {
        return _batchList;
    }

    /**
     * @param batchList
     *            The batchList to set.
     */
    public void setBatchList(ArrayList batchList) {
        _batchList = batchList;
    }

    public int getSizeOfBatchList() {
        if (_batchList != null) {
            return _batchList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the bulkUserMasterMap.
     */
    public HashMap getBulkUserMasterMap() {
        return _bulkUserMasterMap;
    }

    /**
     * @param bulkUserMasterMap
     *            The bulkUserMasterMap to set.
     */
    public void setBulkUserMasterMap(HashMap bulkUserMasterMap) {
        _bulkUserMasterMap = bulkUserMasterMap;
    }

    /**
     * @return Returns the errorFlag.
     */
    public String getErrorFlag() {
        return _errorFlag;
    }

    /**
     * @param errorFlag
     *            The errorFlag to set.
     */
    public void setErrorFlag(String errorFlag) {
        _errorFlag = errorFlag;
    }

    /**
     * @return Returns the errorList.
     */
    public ArrayList getErrorList() {
        return _errorList;
    }

    /**
     * @param errorList
     *            The errorList to set.
     */
    public void setErrorList(ArrayList errorList) {
        _errorList = errorList;
    }

    /**
     * @return Returns the noOfRecords.
     */
    public String getNoOfRecords() {
        return _noOfRecords;
    }

    /**
     * @param noOfRecords
     *            The noOfRecords to set.
     */
    public void setNoOfRecords(String noOfRecords) {
        _noOfRecords = noOfRecords;
    }

    /**
     * @return Returns the totalRecords.
     */
    public int getTotalRecords() {
        return _totalRecords;
    }

    /**
     * @param totalRecords
     *            The totalRecords to set.
     */
    public void setTotalRecords(int totalRecords) {
        _totalRecords = totalRecords;
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
        _domainCode = domainCode;
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

    public int getSizeOfDomainList() {
        if (_domainList != null) {
            return _domainList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the domainName.
     */
    public String getDomainName() {
        return _domainName;
    }

    /**
     * @param domainName
     *            The domainName to set.
     */
    public void setDomainName(String domainName) {
        _domainName = domainName;
    }

    /**
     * @return Returns the geographyCode.
     */
    public String getGeographyCode() {
        return _geographyCode;
    }

    /**
     * @param geographyCode
     *            The geographyCode to set.
     */
    public void setGeographyCode(String geographyCode) {
        _geographyCode = geographyCode;
    }

    /**
     * @return Returns the geographyList.
     */
    public ArrayList getGeographyList() {
        return _geographyList;
    }

    /**
     * @param geographyList
     *            The geographyList to set.
     */
    public void setGeographyList(ArrayList geographyList) {
        _geographyList = geographyList;
    }

    public int getSizeOfGeographyList() {
        if (_geographyList != null) {
            return _geographyList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the geographyName.
     */
    public String getGeographyName() {
        return _geographyName;
    }

    /**
     * @param geographyName
     *            The geographyName to set.
     */
    public void setGeographyName(String geographyName) {
        _geographyName = geographyName;
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
     * @return Returns the categoryName.
     */
    public String getCategoryName() {
        return _categoryName;
    }

    /**
     * @param categoryName
     *            The categoryName to set.
     */
    public void setCategoryName(String categoryName) {
        _categoryName = categoryName;
    }

    /**
     * @return Returns the categoryVO.
     */
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

    /**
     * @return Returns the domainAllList.
     */
    public ArrayList getDomainAllList() {
        return _domainAllList;
    }

    /**
     * @param domainAllList
     *            The domainAllList to set.
     */
    public void setDomainAllList(ArrayList domainAllList) {
        _domainAllList = domainAllList;
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
        _domainType = domainType;
    }

    /**
     * @return Returns the fileName.
     */
    public String getFileName() {
        return _fileName;
    }

    /**
     * @param fileName
     *            The fileName to set.
     */
    public void setFileName(String fileName) {
        _fileName = fileName;
    }

    public int getSizeOfCategoryList() {
        if (_categoryList != null) {
            return _categoryList.size();
        } else {
            return 0;
        }
    }

    // Flush the contents of form bean
    public void flush() {
        _batchName = null;
        _batchID = null;
        _domainCode = null;
        _domainName = null;
        _geographyCode = null;
        _geographyName = null;
        _selectedIndex = null;
        _errorFlag = null;
        _noOfRecords = null;
        _domainList = null;
        _geographyList = null;
        _batchList = null;
        _batchDetailsList = null;
        _errorList = null;
        _totalRecords = 0;
        _bulkUserMasterMap = null;
        //_file = null;
        _batchesVO = null;
        _fromDate = null;
        _toDate = null;
        _categoryList = null;
        _categoryCode = null;
        _categoryName = null;
        _categoryVO = null;
        _domainAllList = null;
        _domainType = null;
        _fileName = null;
    }

}