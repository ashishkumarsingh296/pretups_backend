package com.selftopup.pretups.master.businesslogic;

/**
 * SelectorAmountMappingVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Jasmine kaur 7 FEB 2011 Initial Creation
 * 
 * Copyright(c) 2011, Comviva technologies Ltd.
 */
import java.io.*;

public class SelectorAmountMappingVO implements Serializable {

    private String _serviceType;
    private String _selectorCode;
    private String _selectorName;
    private String _status;
    private String _amount;
    private String _modifiedAllowed;
    private boolean _disableAllow;
    private String _allowAction;
    private String _rowID;

    public String getAmount() {
        return _amount;
    }

    public void setAmount(String _amount) {
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

}
