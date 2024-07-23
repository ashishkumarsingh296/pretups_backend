package com.selftopup.pretups.master.businesslogic;

import java.io.Serializable;
import java.util.Date;

import com.selftopup.pretups.network.businesslogic.NetworkVO;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

/*
 * RequestInterfaceDetailVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 30/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Request interface value object for interaction with backend
 */

public class RequestInterfaceDetailVO implements Serializable {

    private String _reqInterfaceCode;
    private String _reqInterfaceName;
    private String _protocol;
    private String _host;
    private String _servicePort;
    private String _loginID;
    private String _password;
    private String _authType;
    private String _requestHandler;
    private String _encryptionLevel;
    private String _encryptionKey;
    private String _contentType;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;

    public String getAuthType() {
        return _authType;
    }

    public void setAuthType(String authType) {
        _authType = authType;
    }

    public String getContentType() {
        return _contentType;
    }

    public void setContentType(String contentType) {
        _contentType = contentType;
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

    public String getEncryptionKey() {
        return _encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        _encryptionKey = encryptionKey;
    }

    public String getEncryptionLevel() {
        return _encryptionLevel;
    }

    public void setEncryptionLevel(String encryptionLevel) {
        _encryptionLevel = encryptionLevel;
    }

    public String getHost() {
        return _host;
    }

    public void setHost(String host) {
        _host = host;
    }

    public String getLoginID() {
        return _loginID;
    }

    public void setLoginID(String loginID) {
        _loginID = loginID;
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

    public String getPassword() {
        return _password;
    }

    public void setPassword(String password) {
        _password = password;
    }

    public String getProtocol() {
        return _protocol;
    }

    public void setProtocol(String protocol) {
        _protocol = protocol;
    }

    public String getReqInterfaceCode() {
        return _reqInterfaceCode;
    }

    public void setReqInterfaceCode(String reqInterfaceCode) {
        _reqInterfaceCode = reqInterfaceCode;
    }

    public String getReqInterfaceName() {
        return _reqInterfaceName;
    }

    public void setReqInterfaceName(String reqInterfaceName) {
        _reqInterfaceName = reqInterfaceName;
    }

    public String getRequestHandler() {
        return _requestHandler;
    }

    public void setRequestHandler(String requestHandler) {
        _requestHandler = requestHandler;
    }

    public String getServicePort() {
        return _servicePort;
    }

    public void setServicePort(String servicePort) {
        _servicePort = servicePort;
    }

    public String logInfo() {

        StringBuffer sbf = new StringBuffer(200);
        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        sbf.append(startSeperator);
        sbf.append("Req Interface Name");
        sbf.append(middleSeperator);
        sbf.append(this.getReqInterfaceName());

        sbf.append(startSeperator);
        sbf.append("Protocol");
        sbf.append(middleSeperator);
        sbf.append(this.getProtocol());

        sbf.append(startSeperator);
        sbf.append("Host");
        sbf.append(middleSeperator);
        sbf.append(this.getHost());

        sbf.append(startSeperator);
        sbf.append("Service Port");
        sbf.append(middleSeperator);
        sbf.append(this.getServicePort());

        sbf.append(startSeperator);
        sbf.append("Login ID");
        sbf.append(middleSeperator);
        sbf.append(this.getLoginID());

        sbf.append(startSeperator);
        sbf.append("Password");
        sbf.append(middleSeperator);
        sbf.append("***********");

        sbf.append(startSeperator);
        sbf.append("Auth Type");
        sbf.append(middleSeperator);
        sbf.append(this.getAuthType());

        sbf.append(startSeperator);
        sbf.append("Request Handler");
        sbf.append(middleSeperator);
        sbf.append(this.getRequestHandler());

        sbf.append(startSeperator);
        sbf.append("Encryption Level");
        sbf.append(middleSeperator);
        sbf.append(this.getEncryptionLevel());

        sbf.append(startSeperator);
        sbf.append("Encryption Key");
        sbf.append(middleSeperator);
        sbf.append("***********");

        sbf.append(startSeperator);
        sbf.append("Content Type");
        sbf.append(middleSeperator);
        sbf.append(this.getContentType());
        return sbf.toString();
    }

    public String differences(RequestInterfaceDetailVO p_requestInterfaceDetailVO) {
        StringBuffer sbf = new StringBuffer(10);
        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        if (!BTSLUtil.isNullString(this.getReqInterfaceName()) && !BTSLUtil.isNullString(p_requestInterfaceDetailVO.getReqInterfaceName()) && !BTSLUtil.compareLocaleString(this.getReqInterfaceName(), p_requestInterfaceDetailVO.getReqInterfaceName())) {
            sbf.append(startSeperator);
            sbf.append("Req Interface Name");
            sbf.append(middleSeperator);
            sbf.append(p_requestInterfaceDetailVO.getReqInterfaceName());
            sbf.append(middleSeperator);
            sbf.append(this.getReqInterfaceName());
        }

        if (!BTSLUtil.isNullString(this.getProtocol()) && !BTSLUtil.isNullString(p_requestInterfaceDetailVO.getProtocol()) && !BTSLUtil.compareLocaleString(this.getProtocol(), p_requestInterfaceDetailVO.getProtocol())) {
            sbf.append(startSeperator);
            sbf.append("Protocol");
            sbf.append(middleSeperator);
            sbf.append(p_requestInterfaceDetailVO.getProtocol());
            sbf.append(middleSeperator);
            sbf.append(this.getProtocol());
        }

        if (!BTSLUtil.isNullString(this.getHost()) && !BTSLUtil.isNullString(p_requestInterfaceDetailVO.getHost()) && !BTSLUtil.compareLocaleString(this.getHost(), p_requestInterfaceDetailVO.getHost())) {
            sbf.append(startSeperator);
            sbf.append("Host");
            sbf.append(middleSeperator);
            sbf.append(p_requestInterfaceDetailVO.getHost());
            sbf.append(middleSeperator);
            sbf.append(this.getHost());
        }
        if (!BTSLUtil.isNullString(this.getServicePort()) && !BTSLUtil.isNullString(p_requestInterfaceDetailVO.getServicePort()) && !BTSLUtil.compareLocaleString(this.getServicePort(), p_requestInterfaceDetailVO.getServicePort())) {
            sbf.append(startSeperator);
            sbf.append("Service Port");
            sbf.append(middleSeperator);
            sbf.append(p_requestInterfaceDetailVO.getServicePort());
            sbf.append(middleSeperator);
            sbf.append(this.getServicePort());
        }
        if (!BTSLUtil.isNullString(this.getLoginID()) && !BTSLUtil.isNullString(p_requestInterfaceDetailVO.getLoginID()) && !BTSLUtil.compareLocaleString(this.getLoginID(), p_requestInterfaceDetailVO.getLoginID())) {
            sbf.append(startSeperator);
            sbf.append("Login ID");
            sbf.append(middleSeperator);
            sbf.append(p_requestInterfaceDetailVO.getLoginID());
            sbf.append(middleSeperator);
            sbf.append(this.getLoginID());
        }
        if (!BTSLUtil.isNullString(this.getPassword()) && !BTSLUtil.isNullString(p_requestInterfaceDetailVO.getPassword()) && !BTSLUtil.compareLocaleString(this.getPassword(), p_requestInterfaceDetailVO.getPassword())) {
            sbf.append(startSeperator);
            sbf.append("Password");
            sbf.append(middleSeperator);
            sbf.append("***********");
            sbf.append(middleSeperator);
            sbf.append("***********");
        }
        if (!BTSLUtil.isNullString(this.getAuthType()) && !BTSLUtil.isNullString(p_requestInterfaceDetailVO.getAuthType()) && !BTSLUtil.compareLocaleString(this.getAuthType(), p_requestInterfaceDetailVO.getAuthType())) {
            sbf.append(startSeperator);
            sbf.append("Auth Type");
            sbf.append(middleSeperator);
            sbf.append(p_requestInterfaceDetailVO.getAuthType());
            sbf.append(middleSeperator);
            sbf.append(this.getAuthType());
        }
        if (!BTSLUtil.isNullString(this.getRequestHandler()) && !BTSLUtil.isNullString(p_requestInterfaceDetailVO.getRequestHandler()) && !BTSLUtil.compareLocaleString(this.getRequestHandler(), p_requestInterfaceDetailVO.getRequestHandler())) {
            sbf.append(startSeperator);
            sbf.append("Request Handler");
            sbf.append(middleSeperator);
            sbf.append(p_requestInterfaceDetailVO.getRequestHandler());
            sbf.append(middleSeperator);
            sbf.append(this.getRequestHandler());
        }
        if (!BTSLUtil.isNullString(this.getEncryptionLevel()) && !BTSLUtil.isNullString(p_requestInterfaceDetailVO.getEncryptionLevel()) && !BTSLUtil.compareLocaleString(this.getEncryptionLevel(), p_requestInterfaceDetailVO.getEncryptionLevel())) {
            sbf.append(startSeperator);
            sbf.append("Encryption Level");
            sbf.append(middleSeperator);
            sbf.append(p_requestInterfaceDetailVO.getEncryptionLevel());
            sbf.append(middleSeperator);
            sbf.append(this.getEncryptionLevel());
        }
        if (!BTSLUtil.isNullString(this.getEncryptionKey()) && !BTSLUtil.isNullString(p_requestInterfaceDetailVO.getEncryptionKey()) && !BTSLUtil.compareLocaleString(this.getEncryptionKey(), p_requestInterfaceDetailVO.getEncryptionKey())) {
            sbf.append(startSeperator);
            sbf.append("Encryption Key");
            sbf.append(middleSeperator);
            sbf.append("***********");
            sbf.append(middleSeperator);
            sbf.append("***********");
        }
        if (!BTSLUtil.isNullString(this.getContentType()) && !BTSLUtil.isNullString(p_requestInterfaceDetailVO.getContentType()) && !BTSLUtil.compareLocaleString(this.getContentType(), p_requestInterfaceDetailVO.getContentType())) {
            sbf.append(startSeperator);
            sbf.append("Content Type");
            sbf.append(middleSeperator);
            sbf.append(p_requestInterfaceDetailVO.getContentType());
            sbf.append(middleSeperator);
            sbf.append(this.getContentType());
        }

        return sbf.toString();
    }

}
