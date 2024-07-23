package com.btsl.pretups.iccidkeymgmt.businesslogic;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;



/**
 * @(#)PosKeyVO.java Copyright(c) 2003, Bharti Telesoft Ltd.
 *                   All Rights Reserved
 *                   ----------------------------------------------------------
 *                   --------
 *                   Author Date History
 *                   ----------------------------------------------------------
 *                   --------
 *                   Sanjay 01/08/03 Initial Creation
 *                   Gurjeet 13/04/04 Modified
 *                   (Added _errorMessage,_successullyUpdated for change in Map
 *                   ICC and MSISDN)
 *                   Gurjeet 26/06/04 Modified
 *                   (Add _createdOnStr, _modifiedOnStr for associate ICC MSISDN
 *                   ----------------------------------------------------------
 *                   --------
 */
@Getter
@Setter
public class PosKeyVO implements Serializable {

    private String _iccId = null;
    private String _msisdn = null;
    private String _key = null;
    private boolean _registered = false;
    private String _createdBy = null;;
    private java.util.Date _createdOn = null;
    private String _modifiedBy = null;
    private java.util.Date _modifiedOn;
    private String _lastTransaction = null;
    private String _simProfile = null;
    private String _errorMessage = null;
    private boolean _successullyUpdated = false;

    // added by Alok Jain for ICCID-MSISDN Enquiry
    private String _newIccId = null;

    // Added By Gurjeet Singh on 26/06/04
    private String _createdOnStr = null;
    private String _modifiedOnStr = null;
    private String _networkCode = null;

    // added by vikas for sim vender id
    private String _simVenderCode = null;

    private String tmpIccID;
    private String tmpMsisdn;
    private String decryptKeyMask;

    /**
     * PosKeyVO constructor comment.
     */
    public PosKeyVO() {
        super();
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("_iccId=" + _iccId);
        sbf.append(",_createdBy=" + _createdBy);
        sbf.append(",_createdOnStr=" + _createdOnStr);
        sbf.append(",_errorMessage=" + _errorMessage);
        sbf.append(",_key=" + _key);
        sbf.append(",_lastTransaction=" + _lastTransaction);
        sbf.append(",_modifiedBy=" + _modifiedBy);
        sbf.append(",_modifiedOnStr=" + _modifiedOnStr);
        sbf.append(",_msisdn=" + _msisdn);
        sbf.append(",_newIccId=" + _newIccId);
        sbf.append(",_simProfile=" + _simProfile);
        sbf.append(",_registered=" + _registered);
        sbf.append(",_createdOn=" + _createdOn);
        sbf.append(",_modifiedOn=" + _modifiedOn);
        sbf.append(",_successullyUpdated=" + _successullyUpdated);
        return sbf.toString();
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/1/03 11:46:25 AM)
     * 
     * @return String
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/1/03 11:46:52 AM)
     * 
     * @return java.util.Date
     */
    public java.util.Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/1/03 11:44:11 AM)
     * 
     * @return String
     */
    public String getIccId() {
        return _iccId;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/1/03 11:45:30 AM)
     * 
     * @return String
     */
    public String getKey() {
        return _key;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/16/03 4:11:30 PM)
     * 
     * @return String
     */
    public String getLastTransaction() {
        return _lastTransaction;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/16/03 4:07:06 PM)
     * 
     * @return String
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/16/03 4:08:23 PM)
     * 
     * @return java.util.Date
     */
    public java.util.Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/1/03 11:44:37 AM)
     * 
     * @return String
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/1/03 11:46:02 AM)
     * 
     * @return boolean
     */
    public boolean isRegistered() {
        return _registered;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/1/03 11:46:25 AM)
     * 
     * @param new_createdBy
     *            String
     */
    public void setCreatedBy(String new_createdBy) {
        _createdBy = new_createdBy;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/1/03 11:46:52 AM)
     * 
     * @param new_createdOn
     *            java.util.Date
     */
    public void setCreatedOn(java.util.Date new_createdOn) {
        _createdOn = new_createdOn;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/1/03 11:44:11 AM)
     * 
     * @param new_iccId
     *            String
     */
    public void setIccId(String new_iccId) {
        _iccId = new_iccId;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/1/03 11:45:30 AM)
     * 
     * @param new_key
     *            String
     */
    public void setKey(String new_key) {
        _key = new_key;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/16/03 4:11:30 PM)
     * 
     * @param new_lastTransaction
     *            String
     */
    public void setLastTransaction(String new_lastTransaction) {
        _lastTransaction = new_lastTransaction;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/16/03 4:08:23 PM)
     * 
     * @param new_modifiedOn
     *            java.util.Date
     */
    public void setModifiedOn(java.util.Date new_modifiedOn) {
        _modifiedOn = new_modifiedOn;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/1/03 11:44:37 AM)
     * 
     * @param new_msisdn
     *            String
     */
    public void setMsisdn(String new_msisdn) {
        _msisdn = new_msisdn;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/1/03 11:46:02 AM)
     * 
     * @param new_registered
     *            boolean
     */
    public void setRegistered(boolean new_registered) {
        _registered = new_registered;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/16/03 4:07:06 PM)
     * 
     * @param new_modifiedBy
     *            String
     */
    public void setSodifiedBy(String new_modifiedBy) {
        _modifiedBy = new_modifiedBy;
    }

    /**
     * @return
     */
    public String getSimProfile() {
        return _simProfile;
    }

    public String getRegistered() {

        if (_registered) {
            return "Y";
        } else {
            return "N";
        }
    }

    /**
     * @param p_string
     */
    public void setSimProfile(String p_simProfile) {
        _simProfile = p_simProfile;
    }

    /**
     * @return
     */
    public String getErrorMessage() {
        return _errorMessage;
    }

    /**
     * @param string
     */
    public void setErrorMessage(String string) {
        _errorMessage = string;
    }

    /**
     * @return
     */
    public boolean isSuccessullyUpdated() {
        return _successullyUpdated;
    }

    /**
     * @param b
     */
    public void setSuccessullyUpdated(boolean b) {
        _successullyUpdated = b;
    }

    /**
     * @param p_string
     */
    public void setModifiedBy(String p_string) {
        _modifiedBy = p_string;
    }

    /**
     * @return
     */
    public String getNewIccId() {
        return _newIccId;
    }

    /**
     * @param p_string
     */
    public void setNewIccId(String p_string) {
        _newIccId = p_string;
    }

    /**
     * @return
     */
    public String getCreatedOnStr() {
        return _createdOnStr;
    }

    /**
     * @param p_string
     */
    public void setCreatedOnStr(String p_string) {
        _createdOnStr = p_string;
    }

    /**
     * @return
     */
    public String getModifedOnStr() {
        return _modifiedOnStr;
    }

    /**
     * @param p_string
     */
    public void setModifiedOnStr(String p_string) {
        _modifiedOnStr = p_string;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getSimVenderCode() {
        return _simVenderCode;
    }

    public void setSimVenderCode(String simVenderCode) {
        _simVenderCode = simVenderCode;
    }

}
