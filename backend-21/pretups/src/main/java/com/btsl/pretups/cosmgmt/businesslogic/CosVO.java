package com.btsl.pretups.cosmgmt.businesslogic;

public class CosVO {

    private String _cosID;
    private String _oldCosCode;
    private String _newCosCode;
    private String _fromRecharge;
    private String _toRecharge;
    private String _status;
    private String _networkCode;
    private String _createdOn;
    private String _createdBy;
    private String _modifiedOn;
    private String _modifiedBy;
    private String _recordNumber;
    private String _fileName;

    /**
     * @return Returns the _codID.
     */
    public String getCosID() {
        return _cosID;
    }

    /**
     * @param _codid
     *            The _codID to set.
     */
    public void setCosID(String _cosid) {
        _cosID = _cosid;
    }

    /**
     * @return Returns the _createdBy.
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @param by
     *            The _createdBy to set.
     */
    public void setCreatedBy(String by) {
        _createdBy = by;
    }

    /**
     * @return Returns the _createdOn.
     */
    public String getCreatedOn() {
        return _createdOn;
    }

    /**
     * @param on
     *            The _createdOn to set.
     */
    public void setCreatedOn(String on) {
        _createdOn = on;
    }

    /**
     * @return Returns the _fromRecharge.
     */
    public String getFromRecharge() {
        return _fromRecharge;
    }

    /**
     * @param recharge
     *            The _fromRecharge to set.
     */
    public void setFromRecharge(String recharge) {
        _fromRecharge = recharge;
    }

    /**
     * @return Returns the _modifiedBy.
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * @param by
     *            The _modifiedBy to set.
     */
    public void setModifiedBy(String by) {
        _modifiedBy = by;
    }

    /**
     * @return Returns the _modifiedOn.
     */
    public String getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * @param on
     *            The _modifiedOn to set.
     */
    public void setModifiedOn(String on) {
        _modifiedOn = on;
    }

    /**
     * @return Returns the _networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param code
     *            The _networkCode to set.
     */
    public void setNetworkCode(String code) {
        _networkCode = code;
    }

    /**
     * @return Returns the _newCosCode.
     */
    public String getNewCosCode() {
        return _newCosCode;
    }

    /**
     * @param cosCode
     *            The _newCosCode to set.
     */
    public void setNewCosCode(String cosCode) {
        _newCosCode = cosCode;
    }

    /**
     * @return Returns the _oldCosCode.
     */
    public String getOldCosCode() {
        return _oldCosCode;
    }

    /**
     * @param cosCode
     *            The _oldCosCode to set.
     */
    public void setOldCosCode(String cosCode) {
        _oldCosCode = cosCode;
    }

    /**
     * @return Returns the _status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param _status
     *            The _status to set.
     */
    public void setStatus(String _status) {
        this._status = _status;
    }

    /**
     * @return Returns the _toRecharge.
     */
    public String getToRecharge() {
        return _toRecharge;
    }

    /**
     * @param recharge
     *            The _toRecharge to set.
     */
    public void setToRecharge(String recharge) {
        _toRecharge = recharge;
    }

    /**
     * @return Returns the _recordNumber.
     */
    public String getRecordNumber() {
        return _recordNumber;
    }

    /**
     * @param number
     *            The _recordNumber to set.
     */
    public void setRecordNumber(String number) {
        _recordNumber = number;
    }

    /**
     * @return Returns the _fileName.
     */
    public String getFileName() {
        return _fileName;
    }

    /**
     * @param name
     *            The _fileName to set.
     */
    public void setFileName(String name) {
        _fileName = name;
    }
}
