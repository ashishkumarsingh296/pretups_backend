package com.btsl.pretups.master.businesslogic;

/**
 * SelectorAmountMappingVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Jasmine kaur 7 FEB 2011 Initial Creation
 * 
 * Copyright(c) 2011, Comviva technologies Ltd.
 */

import java.io.Serializable;
import java.util.Date;

public class SelectorAmountMappingVO implements Serializable {

    private String _serviceType;
    private String _selectorCode;
    private String _selectorName;
    private String _status;
    private long _amount;
    private String _modifiedAllowed;
    private boolean _disableAllow;
    private String _allowAction;
    private String _rowID;
    private String _amountStr;
    private int _radioIndex;
    private String _serviceName;

    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date on) {
        _createdOn = on;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String by) {
        _createdBy = by;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date on) {
        _modifiedOn = on;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String by) {
        _modifiedBy = by;
    }

    public int getRadioIndex() {
        return _radioIndex;
    }

    /**
     * To set the value of radioIndex field
     */
    public void setRadioIndex(int radioIndex) {
        _radioIndex = radioIndex;
    }

    public long getAmount() {
        return _amount;
    }

    public void setAmount(long _amount) {
        this._amount = _amount;
    }

    public String getModifiedAllowed() {
        return _modifiedAllowed;
    }

    public void setModifiedAllowed(String allowed) {
        _modifiedAllowed = allowed;
    }

    public String getSelectorCode() {
        return _selectorCode;
    }

    public void setSelectorCode(String code) {
        _selectorCode = code;
    }

    public String getSelectorName() {
        return _selectorName;
    }

    public void setSelectorName(String name) {
        _selectorName = name;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String type) {
        _serviceType = type;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String _status) {
        this._status = _status;
    }

    @Override
    public String toString() {

        StringBuffer sbf = new StringBuffer();

        sbf.append("_serviceType  " + _serviceType + " \n ");
        sbf.append("_selectorCode  " + _selectorCode + " \n ");
        sbf.append("_selectorName  " + _selectorName + " \n ");
        sbf.append("_status  " + _status + " \n ");
        sbf.append("_amount  " + _amount + " \n ");
        sbf.append("_modifiedAllowed  " + _modifiedAllowed + " \n ");
        sbf.append("_allowAction  " + _allowAction + " \n ");

        return sbf.toString();
    }

    public boolean getDisableAllow() {
        return _disableAllow;
    }

    public void setDisableAllow(boolean allow) {
        _disableAllow = allow;
    }

    public String getAllowAction() {
        return _allowAction;
    }

    public void setAllowAction(String action) {
        _allowAction = action;
    }

    public String getRowID() {
        return _rowID;
    }

    public void setRowID(String rowID) {
        _rowID = rowID;
    }

    public String getAmountStr() {
        return _amountStr;
    }

    public void setAmountStr(String str) {
        _amountStr = str;
    }

    public String getServiceName() {
        return _serviceName;
    }

    public void setServiceName(String name) {
        _serviceName = name;
    }

    /**
     * Create new object of this class
     * @return SelectorAmountMappingVO new object of this class
     */
    public static SelectorAmountMappingVO getInstance(){
		return new SelectorAmountMappingVO();
	}
}
