/**
 * @(#)LookupsVO.java
 *                    Copyright(c) 2005, Bharti Telesoft Ltd.
 *                    All Rights Reserved
 * 
 *                    <description>
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    avinash.kamthan Mar 13, 2005 Initital Creation
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 * 
 */

package com.btsl.pretups.master.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;

import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author avinash.kamthan
 * 
 */
public class LookupsVO implements Serializable {

    private String _lookupCode;
    private String _lookupName;
    private String _lookupType;
    private Timestamp _modifiedOn;
    private String _status;

    public String getLookupCode() {
        return _lookupCode;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public void setLookupCode(String lookupCode) {
        _lookupCode = lookupCode;
    }

    public String getLookupName() {
        return _lookupName;
    }

    public void setLookupName(String lookupName) {
        _lookupName = lookupName;
    }

    public Timestamp getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Timestamp modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    public boolean equalsLookup(LookupsVO lookupsVO) {
        boolean flag = false;
        if (lookupsVO.getModifiedOn().equals(this.getModifiedOn())) {
            flag = true;
        }
        return flag;
    }

    @Override
    public native int hashCode();

    public String toString() {

        StringBuffer sbf = new StringBuffer();

        sbf.append("_lookupCode  " + _lookupCode + " \n ");
        sbf.append("_lookupCode  " + _lookupCode + " \n ");
        sbf.append("_lookupName  " + _lookupName + " \n ");
        sbf.append("_lookupType  " + _lookupType + " \n ");
        sbf.append("_modifiedOn  " + _modifiedOn + " \n ");
        sbf.append("_status  " + _status + " \n ");

        return sbf.toString();
    }

 

	@Override
	public native boolean equals(Object obj);

	public String logInfo() {

        StringBuffer sbf = new StringBuffer(100);

        String startSeperator = Constants.getProperty("cachestartseparator");
        String middleSeperator = Constants.getProperty("cachemiddleseparator");

        sbf.append(startSeperator);
        sbf.append("Lookup Code");
        sbf.append(middleSeperator);
        sbf.append(this.getLookupCode());

        sbf.append(startSeperator);
        sbf.append("Lookup Name");
        sbf.append(middleSeperator);
        sbf.append(this.getLookupName());

        sbf.append(startSeperator);
        sbf.append("Status");
        sbf.append(middleSeperator);
        sbf.append(this.getStatus());

        return sbf.toString();
    }

    public String differences(LookupsVO p_lookupsVO) {

        StringBuffer sbf = new StringBuffer(100);
        String startSeperator = Constants.getProperty("cachestartseparator");
        String middleSeperator = Constants.getProperty("cachemiddleseparator");

        if (!BTSLUtil.isNullString(this.getLookupName()) && !BTSLUtil.isNullString(p_lookupsVO.getLookupName()) && !BTSLUtil.compareLocaleString(this.getLookupName(), p_lookupsVO.getLookupName())) {
            sbf.append(startSeperator);
            sbf.append("Name");
            sbf.append(middleSeperator);
            sbf.append(p_lookupsVO.getLookupName());
            sbf.append(middleSeperator);
            sbf.append(this.getLookupName());
        }

        if (!BTSLUtil.isNullString(this.getStatus()) && !this.getStatus().equals(p_lookupsVO.getStatus())) {
            sbf.append(startSeperator);
            sbf.append("Status");
            sbf.append(middleSeperator);
            sbf.append(p_lookupsVO.getStatus());
            sbf.append(middleSeperator);
            sbf.append(this.getStatus());
        }

        return sbf.toString();
    }

    public String getLookupType() {
        return _lookupType;
    }

    public void setLookupType(String lookupType) {
        this._lookupType = lookupType;
    }

}
