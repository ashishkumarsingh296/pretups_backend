package com.btsl.pretups.master.businesslogic;

//import org.apache.struts.upload.FormFile;

public class GeographicalDomainCellsVO {
    private String _cellId;
    private String _cellName;
   // private FormFile _file;
    private String _fileName;
    private String _recordNumber;
    private String _grphDomainCode;
    private String _networkCode;
    private String _grphDomainName;
    private String _parentDomainCode;
    private String _status;
    private String _isDefault = "N";
    private String _categoryCode;

    public String getCellId() {
        return _cellId;
    }

    public void setCellId(String id) {
        _cellId = id;
    }

    public String getCellName() {
        return _cellName;
    }

    public void setCellName(String name) {
        _cellName = name;
    }

/*
    public FormFile getFile() {
        return _file;
    }

    public void setFile(FormFile _file) {
        this._file = _file;
    }
*/

    public String getFileName() {
        return _fileName;
    }

    public void setFileName(String name) {
        _fileName = name;
    }

    public String getRecordNumber() {
        return _recordNumber;
    }

    public void setRecordNumber(String number) {
        _recordNumber = number;
    }

    public String getGrphDomainCode() {
        return _grphDomainCode;
    }

    public void setGrphDomainCode(String grphDomainCode) {
        _grphDomainCode = grphDomainCode;
    }

    public String getGrphDomainName() {
        return _grphDomainName;
    }

    public void setGrphDomainName(String grphDomainName) {
        _grphDomainName = grphDomainName;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String getParentDomainCode() {
        return _parentDomainCode;
    }

    public void setParentDomainCode(String parentDomainCode) {
        _parentDomainCode = parentDomainCode;
    }

    public String getIsDefault() {
        return _isDefault;
    }

    /**
     * @param isDefault
     *            the isDefault to set
     */
    public void setIsDefault(String isDefault) {
        _isDefault = isDefault;
    }

    public void setcategoryCode(String catCode) {
        _categoryCode = catCode;
    }

    public String getcategoryCode() {
        return _categoryCode;
    }
}
