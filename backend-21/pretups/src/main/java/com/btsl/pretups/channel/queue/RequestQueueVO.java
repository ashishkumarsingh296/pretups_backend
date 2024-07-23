package com.btsl.pretups.channel.queue;

/*
 * @(#)RequestQueueVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Anu Garg 15/07/2013 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.pretups.receiver.RequestVO;

public class RequestQueueVO {
    private String _senderMsisdn;
    private String _receiverMsisdn;
    private RequestVO _requestVO = null;
    private String _serviceType;
    private String _requestHandlerClass;
    // private ServiceKeywordControllerI _controllerObject;
    private long _queueAdditionTime;
    private long _requestIDMethod = 0;
    private String _externalInterfaceAllowed = null;
    private HttpServletResponse _response;
    private String _queueForAll = null;
    private List _serviceList = null;
    private int _priority;
    private long _requestTimeout;

    @Override
    public boolean equals(Object object) {
        final RequestQueueVO element = ((RequestQueueVO) object);
        if (element.getReceiverMsisdn().equals(this._receiverMsisdn) && element.getSenderMsisdn().equals(this._senderMsisdn) && element.getServiceType().equals(
            this._serviceType)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode(){
    	int hash = 7;
        hash = 31 * hash + (this._receiverMsisdn == null ? 0 : this._receiverMsisdn.hashCode());
        hash = 31 * hash + (this._senderMsisdn == null ? 0 : this._senderMsisdn.hashCode());
        hash = 31 * hash + (this._serviceType == null ? 0 : this._serviceType.hashCode());
        return hash;
    }

    /*	*//**
     * @return the controllerObject
     */
    /*
     * public ServiceKeywordControllerI getControllerObject() {
     * return _controllerObject;
     * }
     *//**
     * @param controllerObject
     *            the controllerObject to set
     */
    /*
     * public void setControllerObject(ServiceKeywordControllerI
     * controllerObject) {
     * _controllerObject = controllerObject;
     * }
     */

    /**
     * @return the _receiverMsisdn
     */
    public String getReceiverMsisdn() {
        return _receiverMsisdn;
    }

    /**
     * @param receiverMsisdn
     *            the _receiverMsisdn to set
     */
    public void setReceiverMsisdn(String receiverMsisdn) {
        _receiverMsisdn = receiverMsisdn;
    }

    /**
     * @return the _senderMsisdn
     */
    public String getSenderMsisdn() {
        return _senderMsisdn;
    }

    /**
     * @param senderMsisdn
     *            the _senderMsisdn to set
     */
    public void setSenderMsisdn(String senderMsisdn) {
        _senderMsisdn = senderMsisdn;
    }

    /**
     * @return the queueAdditionTime
     */
    public long getQueueAdditionTime() {
        return _queueAdditionTime;
    }

    /**
     * @param queueAdditionTime
     *            the queueAdditionTime to set
     */
    public void setQueueAdditionTime(long queueAdditionTime) {
        _queueAdditionTime = queueAdditionTime;
    }

    /**
     * @return the _serviceType
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * @param serviceType
     *            the _serviceType to set
     */
    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    /**
     * @return the requestVO
     */
    public RequestVO getRequestVO() {
        return _requestVO;
    }

    /**
     * @param requestVO
     *            the requestVO to set
     */
    public void setRequestVO(RequestVO requestVO) {
        _requestVO = requestVO;
    }

    public long getRequestIDMethod() {
        return _requestIDMethod;
    }

    public void setRequestIDMethod(long requestIDMethod) {
        this._requestIDMethod = requestIDMethod;
    }

    public String getRequestHandlerClass() {
        return _requestHandlerClass;
    }

    public void setRequestHandlerClass(String requestHandlerClass) {
        _requestHandlerClass = requestHandlerClass;
    }

    public HttpServletResponse getResponse() {
        return _response;
    }

    public void setResponse(HttpServletResponse response) {
        this._response = response;
    }

    public String getExternalInterfaceAllowed() {
        return _externalInterfaceAllowed;
    }

    public void setExternalInterfaceAllowed(String externalInterfaceAllowed) {
        this._externalInterfaceAllowed = externalInterfaceAllowed;
    }

    public String getQueueForAll() {
        return _queueForAll;
    }

    public void setQueueForAll(String queueForAll) {
        _queueForAll = queueForAll;
    }

    public List getServiceList() {
        return _serviceList;
    }

    public void setServiceList(List list) {
        _serviceList = list;
    }

    public int getPriority() {
        return _priority;
    }

    public void setPriority(int _priority) {
        this._priority = _priority;
    }

    public long getRequestTimeout() {
        return _requestTimeout;
    }

    public void setRequestTimeout(long timeout) {
        _requestTimeout = timeout;
    }

}
