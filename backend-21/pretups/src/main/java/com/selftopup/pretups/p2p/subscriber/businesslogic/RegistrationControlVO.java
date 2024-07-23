package com.selftopup.pretups.p2p.subscriber.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;

import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

/*
 * RegistrationControlVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 30/10/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

public class RegistrationControlVO implements Serializable {
    private String _networkCode;
    private String _registrationType;
    private String _validationReqd;
    private String _validationInterface;
    private String _altInterfaceCheck;
    private String _alternateInterface;
    private String _registrationToBedone;
    private String _defRegistrationType;
    private boolean _validationReqdBool;
    private boolean _altInterfaceCheckBool;
    private boolean _registrationToBedoneBool;
    private String _createdBy;
    private String _modifiedBy;
    private Timestamp _createdOn;
    private Timestamp _modifiedOn;

    public String getAlternateInterface() {
        return _alternateInterface;
    }

    public void setAlternateInterface(String alternateInterface) {
        _alternateInterface = alternateInterface;
    }

    public String getAltInterfaceCheck() {
        return _altInterfaceCheck;
    }

    public void setAltInterfaceCheck(String altInterfaceCheck) {
        _altInterfaceCheck = altInterfaceCheck;
    }

    public String getDefRegistrationType() {
        return _defRegistrationType;
    }

    public void setDefRegistrationType(String defRegistrationType) {
        _defRegistrationType = defRegistrationType;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getRegistrationToBedone() {
        return _registrationToBedone;
    }

    public void setRegistrationToBedone(String registrationToBedone) {
        _registrationToBedone = registrationToBedone;
    }

    public String getRegistrationType() {
        return _registrationType;
    }

    public void setRegistrationType(String registrationType) {
        _registrationType = registrationType;
    }

    public String getValidationReqd() {
        return _validationReqd;
    }

    public void setValidationReqd(String validationReqd) {
        _validationReqd = validationReqd;
    }

    public boolean isValidationReqdBool() {
        return _validationReqdBool;
    }

    public void setValidationReqdBool(boolean validationReqdBool) {
        _validationReqdBool = validationReqdBool;
    }

    public boolean isAltInterfaceCheckBool() {
        return _altInterfaceCheckBool;
    }

    public void setAltInterfaceCheckBool(boolean altInterfaceCheckBool) {
        _altInterfaceCheckBool = altInterfaceCheckBool;
    }

    public boolean isRegistrationToBedoneBool() {
        return _registrationToBedoneBool;
    }

    public void setRegistrationToBedoneBool(boolean registrationToBedoneBool) {
        _registrationToBedoneBool = registrationToBedoneBool;
    }

    public String getValidationInterface() {
        return _validationInterface;
    }

    public void setValidationInterface(String validationInterface) {
        _validationInterface = validationInterface;
    }

    public String getKey() {
        StringBuffer buffer = new StringBuffer(_networkCode);
        buffer.append("_");
        buffer.append(_registrationType);
        return buffer.toString();
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    public Timestamp getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        _createdOn = createdOn;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    public Timestamp getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Timestamp modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    public boolean equals(RegistrationControlVO p_registrationControlVO) {
        boolean flag = false;
        if (this.getModifiedOn().equals(p_registrationControlVO.getModifiedOn())) {
            flag = true;
        }
        return flag;
    }

    public String logInfo() {

        StringBuffer sbf = new StringBuffer(10);

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        sbf.append(startSeperator);
        sbf.append("Registration Type");
        sbf.append(middleSeperator);
        sbf.append(this.getRegistrationType());

        sbf.append(startSeperator);
        sbf.append("Validation Required");
        sbf.append(middleSeperator);
        sbf.append(this.getValidationReqd());

        sbf.append(startSeperator);
        sbf.append("Validation Interface");
        sbf.append(middleSeperator);
        sbf.append(this.getValidationInterface());

        sbf.append(startSeperator);
        sbf.append("Alternate Interface Check");
        sbf.append(middleSeperator);
        sbf.append(this.getAlternateInterface());

        sbf.append(startSeperator);
        sbf.append("Alternate Interface");
        sbf.append(middleSeperator);
        sbf.append(this.getAlternateInterface());

        sbf.append(startSeperator);
        sbf.append("Registration To Be Done");
        sbf.append(middleSeperator);
        sbf.append(this.getRegistrationToBedone());

        sbf.append(startSeperator);
        sbf.append("Default Registration Type");
        sbf.append(middleSeperator);
        sbf.append(this.getDefRegistrationType());

        sbf.append(startSeperator);
        sbf.append("Network Code");
        sbf.append(middleSeperator);
        sbf.append(this.getNetworkCode());

        return sbf.toString();

    }

    public String differences(RegistrationControlVO p_controlVO) {

        StringBuffer sbf = new StringBuffer(100);

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        if (!BTSLUtil.isNullString(this.getRegistrationType()) && this.getRegistrationType().equals(p_controlVO.getRegistrationType())) {
            sbf.append(startSeperator);
            sbf.append("Registration Type");
            sbf.append(middleSeperator);
            sbf.append(p_controlVO.getRegistrationType());
            sbf.append(middleSeperator);
            sbf.append(this.getRegistrationType());
        }

        if (!BTSLUtil.isNullString(this.getValidationReqd()) && this.getValidationReqd().equals(p_controlVO.getValidationReqd())) {
            sbf.append(startSeperator);
            sbf.append("Validation Required");
            sbf.append(middleSeperator);
            sbf.append(p_controlVO.getValidationReqd());
            sbf.append(middleSeperator);
            sbf.append(this.getValidationReqd());
        }

        if (!BTSLUtil.isNullString(this.getValidationInterface()) && this.getValidationInterface().equals(p_controlVO.getValidationInterface())) {
            sbf.append(startSeperator);
            sbf.append("Validation Interface");
            sbf.append(middleSeperator);
            sbf.append(p_controlVO.getValidationInterface());
            sbf.append(middleSeperator);
            sbf.append(this.getValidationInterface());
        }

        if (!BTSLUtil.isNullString(this.getAltInterfaceCheck()) && this.getAltInterfaceCheck().equals(p_controlVO.getAltInterfaceCheck())) {
            sbf.append(startSeperator);
            sbf.append("Alternate Interface Check");
            sbf.append(middleSeperator);
            sbf.append(p_controlVO.getAltInterfaceCheck());
            sbf.append(middleSeperator);
            sbf.append(this.getAltInterfaceCheck());
        }

        if (!BTSLUtil.isNullString(this.getAlternateInterface()) && this.getAlternateInterface().equals(p_controlVO.getAlternateInterface())) {
            sbf.append(startSeperator);
            sbf.append("Alternate Interface");
            sbf.append(middleSeperator);
            sbf.append(p_controlVO.getAlternateInterface());
            sbf.append(middleSeperator);
            sbf.append(this.getAlternateInterface());
        }

        if (!BTSLUtil.isNullString(this.getRegistrationToBedone()) && this.getRegistrationToBedone().equals(p_controlVO.getRegistrationToBedone())) {
            sbf.append(startSeperator);
            sbf.append("Registration To Be Done");
            sbf.append(middleSeperator);
            sbf.append(p_controlVO.getRegistrationToBedone());
            sbf.append(middleSeperator);
            sbf.append(this.getRegistrationToBedone());
        }

        if (!BTSLUtil.isNullString(this.getDefRegistrationType()) && this.getDefRegistrationType().equals(p_controlVO.getDefRegistrationType())) {
            sbf.append(startSeperator);
            sbf.append("Default Registration Type");
            sbf.append(middleSeperator);
            sbf.append(p_controlVO.getDefRegistrationType());
            sbf.append(middleSeperator);
            sbf.append(this.getDefRegistrationType());
        }

        if (!BTSLUtil.isNullString(this.getNetworkCode()) && this.getNetworkCode().equals(p_controlVO.getNetworkCode())) {
            sbf.append(startSeperator);
            sbf.append("Network Code");
            sbf.append(middleSeperator);
            sbf.append(p_controlVO.getNetworkCode());
            sbf.append(middleSeperator);
            sbf.append(this.getNetworkCode());
        }

        return sbf.toString();

    }

}
