/**
 * 
 */
package com.btsl.voms.voucher.businesslogic;

/**
 * @author samna.soin
 * 
 */
public class VomsSerialUploadCheckVO implements java.io.Serializable {

    private String _startSerialNo;
    private String _endSerialNO;
    private int _denomination;
    private java.util.Date _expiryDate;
    private String _fileName;
    private java.util.Date _uploadDate;
    private String _createdBy;
    private java.util.Date _createdOn;

    public VomsSerialUploadCheckVO() {
        super();
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String by) {
        _createdBy = by;
    }

    public java.util.Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(java.util.Date on) {
        _createdOn = on;
    }

    public int getDenomination() {
        return _denomination;
    }

    public void setDenomination(int _denomination) {
        this._denomination = _denomination;
    }

    public String getEndSerialNO() {
        return _endSerialNO;
    }

    public void setEndSerialNO(String serialNO) {
        _endSerialNO = serialNO;
    }

    public java.util.Date getExpiryDate() {
        return _expiryDate;
    }

    public void setExpiryDate(java.util.Date date) {
        _expiryDate = date;
    }

    public String getFileName() {
        return _fileName;
    }

    public void setFileName(String name) {
        _fileName = name;
    }

    public String getStartSerialNo() {
        return _startSerialNo;
    }

    public void setStartSerialNo(String serialNo) {
        _startSerialNo = serialNo;
    }

    public java.util.Date getUploadDate() {
        return _uploadDate;
    }

    public void setUploadDate(java.util.Date date) {
        _uploadDate = date;
    }

}
