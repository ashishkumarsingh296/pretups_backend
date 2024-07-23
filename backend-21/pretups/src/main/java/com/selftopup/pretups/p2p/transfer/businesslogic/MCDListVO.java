package com.selftopup.pretups.p2p.transfer.businesslogic;

import java.io.Serializable;
import java.util.Date;

public class MCDListVO implements Serializable {

    private String _msisdn = null;
    private String _parentID = null;
    private String _selector1 = null;
    private long _amount1;
    private String _amount1String;
    private String _amount2String;
    private String _selector2 = null;
    private long _amount2;
    private String action = null;
    private String _networkCode = null;
    private long _prefixID;
    private String _subscriberType = null;
    private String _createdBy;
    private Date _createdOn;
    private String _modifiedBy;
    private Date _modifiedOn;
    private String _listName = null;
    private String _reason1 = null;
    private String _reason2 = null;
    private Date _lastTransfer = null;
    private long _days;

    // added by harsh for Scheduled (Add/Modify/Delete) Request
    private String _mcdReceiverProfile = null;
    private String _mcdListAmountString = null;
    private long _mcdListAmount = 0;
    private String _reason = null;

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    public String getSelector1() {
        return _selector1;
    }

    public void setSelector1(String selector1) {
        _selector1 = selector1;
    }

    public String getSelector2() {
        return _selector2;
    }

    public void setSelector2(String selector2) {
        _selector2 = selector2;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getSubscriberType() {
        return _subscriberType;
    }

    public void setSubscriberType(String subscriberType) {
        _subscriberType = subscriberType;
    }

    public long getPrefixID() {
        return _prefixID;
    }

    public void setPrefixID(long prefixID) {
        _prefixID = prefixID;
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

    public String getListName() {
        return _listName;
    }

    public void setListName(String listName) {
        _listName = listName;
    }

    public String getParentID() {
        return _parentID;
    }

    public void setParentID(String parentID) {
        _parentID = parentID;
    }

    public long getAmount1() {
        return _amount1;
    }

    public void setAmount1(long amount1) {
        _amount1 = amount1;
    }

    public long getAmount2() {
        return _amount2;
    }

    public void setAmount2(long amount2) {
        _amount2 = amount2;
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("_msisdn =" + _msisdn);
        sbf.append("_parentID =" + _parentID);
        sbf.append("_selector1 =" + _selector1);
        sbf.append("_amount1 =" + _amount1);
        sbf.append("_selector2 =" + _selector2);
        sbf.append("_amount2 =" + _amount2);
        sbf.append("action =" + action);
        sbf.append("_networkCode =" + _networkCode);
        sbf.append("_prefixID =" + _prefixID);
        sbf.append("_networkCode =" + _networkCode);
        sbf.append("_createdOn =" + _createdOn);
        sbf.append("_createdBy =" + _createdBy);
        sbf.append("_createdOn =" + _createdOn);
        sbf.append("_modifiedOn =" + _modifiedOn);
        sbf.append("_modifiedBy =" + _modifiedBy);
        sbf.append("_listName =" + _listName);
        sbf.append("_reason1 =" + _reason1);

        return sbf.toString();
    }

    public String getReason1() {
        return _reason1;
    }

    public void setReason1(String reason1) {
        _reason1 = reason1;
    }

    public String getReason2() {
        return _reason2;
    }

    public void setReason2(String reason2) {
        _reason2 = reason2;
    }

    public String getAmount1String() {
        return _amount1String;
    }

    public void setAmount1String(String amount1String) {
        _amount1String = amount1String;
    }

    public String getAmount2String() {
        return _amount2String;
    }

    public void setAmount2String(String amount2String) {
        _amount2String = amount2String;
    }

    public Long getDays() {
        return _days;
    }

    public void setDays(Long _days) {
        this._days = _days;
    }

    public Date getLastTransfer() {
        return _lastTransfer;
    }

    public void setLastTransfer(Date transfer) {
        _lastTransfer = transfer;
    }

    public String getMcdReceiverProfile() {
        return _mcdReceiverProfile;
    }

    public void setMcdReceiverProfile(String mcdReceiverProfile) {
        _mcdReceiverProfile = mcdReceiverProfile;
    }

    public String getMcdListAmountString() {
        return _mcdListAmountString;
    }

    public void setMcdListAmountString(String mcdListAmountString) {
        _mcdListAmountString = mcdListAmountString;
    }

    public long getMcdListAmount() {
        return _mcdListAmount;
    }

    public void setMcdListAmount(long mcdListAmount) {
        _mcdListAmount = mcdListAmount;
    }

    public String getReason() {
        return _reason;
    }

    public void setReason(String reason) {
        _reason = reason;
    }
}
