/*
 * @# NetworkServiceVO.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Sandeep Goel Aug 16, 2005 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.master.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class NetworkServiceVO implements Serializable {
    public NetworkServiceVO() {

    }

    private String _moduleCode;
    private String _serviceType;
    private String _senderNetwork;
    private String _receiverNetwork;
    private String _status;
    private String _language1Message;
    private String _language2Message;
    private String _createdBy;
    private Date _createdOn;
    private String _modifiedBy;
    private Date _modifiedOn;

    private long _lastModifiedTime;
    private String _senderNetworkDes;
    private ArrayList _networkServicesVOList;

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

    public String getModuleCode() {
        return _moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        _moduleCode = moduleCode;
    }

    public String getReceiverNetwork() {
        return _receiverNetwork;
    }

    public void setReceiverNetwork(String receiverNetwork) {
        _receiverNetwork = receiverNetwork;
    }

    public String getSenderNetwork() {
        return _senderNetwork;
    }

    public void setSenderNetwork(String senderNetwork) {
        _senderNetwork = senderNetwork;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        _lastModifiedTime = lastModifiedTime;
    }

    public String getSenderNetworkDes() {
        return _senderNetworkDes;
    }

    public void setSenderNetworkDes(String senderNetworkDes) {
        _senderNetworkDes = senderNetworkDes;
    }

    public ArrayList getNetworkServicesVOList() {
        return _networkServicesVOList;
    }

    public void setNetworkServicesVOList(ArrayList networkServicesVOList) {
        _networkServicesVOList = networkServicesVOList;
    }

    public String logInfo() {

        StringBuffer sbf = new StringBuffer(200);
        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        sbf.append(startSeperator);
        sbf.append("Module Code");
        sbf.append(middleSeperator);
        sbf.append(this.getModuleCode());

        sbf.append(startSeperator);
        sbf.append("Sender network ");
        sbf.append(middleSeperator);
        sbf.append(this.getSenderNetwork());

        sbf.append(startSeperator);
        sbf.append("Receiver network");
        sbf.append(middleSeperator);
        sbf.append(this.getReceiverNetwork());

        sbf.append(startSeperator);
        sbf.append("Service Type");
        sbf.append(middleSeperator);
        sbf.append(this.getServiceType());

        sbf.append(startSeperator);
        sbf.append("Status");
        sbf.append(middleSeperator);
        sbf.append(this.getStatus());

        sbf.append(startSeperator);
        sbf.append("Lang 1 Message");
        sbf.append(middleSeperator);
        sbf.append(this.getLanguage1Message());

        sbf.append(startSeperator);
        sbf.append("Lang 2 Message");
        sbf.append(middleSeperator);
        sbf.append(this.getLanguage2Message());

        return sbf.toString();
    }

    public String differences(NetworkServiceVO p_networkServiceVO) {
        StringBuffer sbf = new StringBuffer(10);
        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        if (this.getModuleCode() != null && p_networkServiceVO.getModuleCode() != null && !BTSLUtil.compareLocaleString(this.getModuleCode(), p_networkServiceVO.getModuleCode())) {
            sbf.append(startSeperator);
            sbf.append("Module Code");
            sbf.append(middleSeperator);
            sbf.append(p_networkServiceVO.getModuleCode());
            sbf.append(middleSeperator);
            sbf.append(this.getModuleCode());
        }

        if (this.getSenderNetwork() != null && p_networkServiceVO.getSenderNetwork() != null && !BTSLUtil.compareLocaleString(this.getSenderNetwork(), p_networkServiceVO.getSenderNetwork())) {
            sbf.append(startSeperator);
            sbf.append("Sender Network");
            sbf.append(middleSeperator);
            sbf.append(p_networkServiceVO.getSenderNetwork());
            sbf.append(middleSeperator);
            sbf.append(this.getSenderNetwork());
        }

        if (this.getReceiverNetwork() != null && p_networkServiceVO.getReceiverNetwork() != null && !BTSLUtil.compareLocaleString(this.getReceiverNetwork(), p_networkServiceVO.getReceiverNetwork())) {
            sbf.append(startSeperator);
            sbf.append("Receiver Network");
            sbf.append(middleSeperator);
            sbf.append(p_networkServiceVO.getReceiverNetwork());
            sbf.append(middleSeperator);
            sbf.append(this.getReceiverNetwork());
        }

        if (this.getServiceType() != null && p_networkServiceVO.getServiceType() != null && !BTSLUtil.compareLocaleString(this.getServiceType(), p_networkServiceVO.getServiceType())) {
            sbf.append(startSeperator);
            sbf.append("Service Type");
            sbf.append(middleSeperator);
            sbf.append(p_networkServiceVO.getServiceType());
            sbf.append(middleSeperator);
            sbf.append(this.getServiceType());
        }
        if (p_networkServiceVO.getStatus() != null && this.getStatus() != null && !BTSLUtil.compareLocaleString(this.getStatus(), p_networkServiceVO.getStatus())) {
            sbf.append(startSeperator);
            sbf.append("Status");
            sbf.append(middleSeperator);
            sbf.append(p_networkServiceVO.getStatus());
            sbf.append(middleSeperator);
            sbf.append(this.getStatus());
        }
        if (this.getLanguage1Message() != null && p_networkServiceVO.getLanguage1Message() != null && !BTSLUtil.compareLocaleString(this.getLanguage1Message(), p_networkServiceVO.getLanguage1Message())) {
            sbf.append(startSeperator);
            sbf.append("Language1 Message");
            sbf.append(middleSeperator);
            sbf.append(p_networkServiceVO.getLanguage1Message());
            sbf.append(middleSeperator);
            sbf.append(this.getStatus());
        }
        if (p_networkServiceVO.getLanguage2Message() != null && this.getLanguage2Message() != null && !BTSLUtil.compareLocaleString(this.getLanguage2Message(), p_networkServiceVO.getLanguage2Message())) {
            sbf.append(startSeperator);
            sbf.append("Language2 Message");
            sbf.append(middleSeperator);
            sbf.append(p_networkServiceVO.getLanguage1Message());
            sbf.append(middleSeperator);
            sbf.append(this.getStatus());
        }

        return sbf.toString();
    }

}
