package com.btsl.pretups.p2p.reconciliation.businesslogic;

import java.io.Serializable;
import java.util.Date;

/*
 * @# ReconciliationVO.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Sandeep Goel Nov 7, 2005 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
public class ReconciliationVO implements Serializable {
    private String _transferID = null;
    private String _modifiedBy = null;
    private Date _modifiedOn = null;
    private String _transferStatus = null;

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

    public String getTransferID() {
        return _transferID;
    }

    public void setTransferID(String transferID) {
        _transferID = transferID;
    }

    public String getTransferStatus() {
        return _transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        _transferStatus = transferStatus;
    }

    public String toString() {
        final StringBuffer sbf = new StringBuffer();
        sbf.append("_modifiedBy = " + _modifiedBy);
        sbf.append(", _transferID= " + _transferID);
        sbf.append(", _transferStatus= " + _transferStatus);
        sbf.append(", _modifiedOn= " + _modifiedOn);
        // sbf.append(", = "+_);
        return sbf.toString();
    }

}
