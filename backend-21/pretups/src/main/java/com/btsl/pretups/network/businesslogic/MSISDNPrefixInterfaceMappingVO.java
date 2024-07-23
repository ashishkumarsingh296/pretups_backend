/**
 * @(#)MSISDNPrefixInterfaceMappingVO.java
 *                                         Copyright(c) 2005, Bharti Telesoft
 *                                         Ltd.
 *                                         All Rights Reserved
 * 
 *                                         <description>
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 *                                         Author Date History
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 *                                         avinash.kamthan Mar 22, 2005 Initital
 *                                         Creation
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 * 
 */

package com.btsl.pretups.network.businesslogic;

import java.io.Serializable;

import com.btsl.util.Constants;

/**
 * @author avinash.kamthan
 * 
 */
public class MSISDNPrefixInterfaceMappingVO implements Serializable {

    private String _networkCode;
    private long _prefixId;
    private String _action;
    private String _interfaceType;
    private String _interfaceID;
    private String _handlerClass;
    private String _underProcessMsgRequired;
    private boolean _underProcessMsgRequiredBool;
    private String _allServiceClassID;
    private String _externalID;
    private String _interfaceStatus;
    private String _language1Message;
    private String _language2Message;
    private String _statusType;
    private String _interfaceTypeID;
    private String _singleStep;

    public String getAction() {
        return _action;
    }

    public void setAction(String action) {
        _action = action;
    }

    public String getInterfaceID() {
        return _interfaceID;
    }

    public void setInterfaceID(String interfaceID) {
        _interfaceID = interfaceID;
    }

    public String getInterfaceType() {
        return _interfaceType;
    }

    public void setInterfaceType(String interfaceType) {
        _interfaceType = interfaceType;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public long getPrefixId() {
        return _prefixId;
    }

    public void setPrefixId(long prefixId) {
        _prefixId = prefixId;
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer();

        sbf.append("_networkCode " + _networkCode + " ,");
        sbf.append("_prefixId " + _prefixId + " ,");
        sbf.append("_action " + _action + " ,");
        sbf.append("_interfaceType " + _interfaceType + " ,");
        sbf.append("_interfaceId " + _interfaceID + " ,");
        sbf.append("_underProcessMsgRequired " + _underProcessMsgRequired);
        sbf.append("_underProcessMsgRequiredBool " + _underProcessMsgRequiredBool);
        sbf.append("_allServiceClassID " + _allServiceClassID);
        sbf.append("_interfaceStatus " + _interfaceStatus);
        sbf.append("_language1Message " + _language1Message);
        sbf.append("_language2Message " + _language2Message);
        sbf.append("_singleStep " + _singleStep);

        return sbf.toString();
    }

    public String logInfo() {

        StringBuffer sbf = new StringBuffer(200);

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        sbf.append(startSeperator);
        sbf.append("Network Code");
        sbf.append(middleSeperator);
        sbf.append(this.getNetworkCode());

        sbf.append(startSeperator);
        sbf.append("Prefix Id");
        sbf.append(middleSeperator);
        sbf.append(this.getPrefixId());

        sbf.append(startSeperator);
        sbf.append("Action");
        sbf.append(middleSeperator);
        sbf.append(this.getAction());

        sbf.append(startSeperator);
        sbf.append("Interface Type");
        sbf.append(middleSeperator);
        sbf.append(this.getInterfaceType());

        sbf.append(startSeperator);
        sbf.append("Interface Id");
        sbf.append(middleSeperator);
        sbf.append(this.getInterfaceID());

        sbf.append(startSeperator);
        sbf.append("Under process Msg");
        sbf.append(middleSeperator);
        sbf.append(this.getUnderProcessMsgRequired());

        sbf.append(startSeperator);
        sbf.append("ALL service class ID");
        sbf.append(middleSeperator);
        sbf.append(this.getAllServiceClassID());

        sbf.append(startSeperator);
        sbf.append("Under process Msg Required ");
        sbf.append(middleSeperator);
        sbf.append(this.isUnderProcessMsgRequiredBool());

        sbf.append(startSeperator);
        sbf.append("Interface Status ");
        sbf.append(middleSeperator);
        sbf.append(this.getInterfaceStatus());

        sbf.append(startSeperator);
        sbf.append("Lang 1 Message ");
        sbf.append(middleSeperator);
        sbf.append(this.getLanguage1Message());

        sbf.append(startSeperator);
        sbf.append("Lang 2 Message ");
        sbf.append(middleSeperator);
        sbf.append(this.getLanguage2Message());

        sbf.append(startSeperator);
        sbf.append("Single Step ");
        sbf.append(middleSeperator);
        sbf.append(this.getSingleStep());

        return sbf.toString();
    }

    public String getHandlerClass() {
        return _handlerClass;
    }

    public void setHandlerClass(String handlerClass) {
        _handlerClass = handlerClass;
    }

    public String getUnderProcessMsgRequired() {
        return _underProcessMsgRequired;
    }

    public void setUnderProcessMsgRequired(String underProcessMsgRequired) {
        _underProcessMsgRequired = underProcessMsgRequired;
    }

    public boolean isUnderProcessMsgRequiredBool() {
        return _underProcessMsgRequiredBool;
    }

    public void setUnderProcessMsgRequiredBool(boolean underProcessMsgRequiredBool) {
        _underProcessMsgRequiredBool = underProcessMsgRequiredBool;
    }

    public String getAllServiceClassID() {
        return _allServiceClassID;
    }

    public void setAllServiceClassID(String allServiceClassID) {
        _allServiceClassID = allServiceClassID;
    }

    public String getExternalID() {
        return _externalID;
    }

    public void setExternalID(String externalID) {
        _externalID = externalID;
    }

    public String getInterfaceStatus() {
        return _interfaceStatus;
    }

    public void setInterfaceStatus(String interfaceStatus) {
        _interfaceStatus = interfaceStatus;
    }

    public String getLanguage1Message() {
        return _language1Message;
    }

    public void setLanguage1Message(String language1Message) {
        _language1Message = language1Message;
    }

    public String getLanguage2Message() {
        return _language2Message;
    }

    public void setLanguage2Message(String language2Message) {
        _language2Message = language2Message;
    }

    public String getStatusType() {
        return _statusType;
    }

    public void setStatusType(String type) {
        _statusType = type;
    }

    /**
     * @return Returns the interfaceTypeID.
     */
    public String getInterfaceTypeID() {
        return _interfaceTypeID;
    }

    /**
     * @param interfaceTypeID
     *            The interfaceTypeID to set.
     */
    public void setInterfaceTypeID(String interfaceTypeID) {
        _interfaceTypeID = interfaceTypeID;
    }

    /**
     * @return Returns the singleStep.
     */
    public String getSingleStep() {
        return this._singleStep;
    }

    /**
     * @param singleStep
     *            The singleStep to set.
     */
    public void setSingleStep(String singleStep) {
        this._singleStep = singleStep;
    }
}
