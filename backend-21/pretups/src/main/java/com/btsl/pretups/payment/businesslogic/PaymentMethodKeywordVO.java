/**
 * @(#)PaymentMethodKeywordVO.java
 *                                 Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                 All Rights Reserved
 * 
 *                                 <description>
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 avinash.kamthan June 20, 2005 Initital
 *                                 Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 * 
 */
package com.btsl.pretups.payment.businesslogic;

import java.io.Serializable;

public class PaymentMethodKeywordVO implements Serializable {

    private String _paymentKeyword; // payment method like bank
    private String _paymentMethodType;
    private String _serviceType;
    private String _networkCode;
    private String _useDefaultInterface;
    private String _defaultInterfaceID;
    private String _externalID;
    private String _status;
    private String _lang1Message;
    private String _lang2Message;
    private String _handlerClass;
    private String _underProcessMsgReq;
    private String _allServiceClassId;
    private String _statusType;

    /**
     * @return Returns the defaultInterfaceID.
     */
    public String getDefaultInterfaceID() {
        return _defaultInterfaceID;
    }

    /**
     * @param defaultInterfaceID
     *            The defaultInterfaceID to set.
     */
    public void setDefaultInterfaceID(String defaultInterfaceID) {
        _defaultInterfaceID = defaultInterfaceID;
    }

    /**
     * @return Returns the networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param networkCode
     *            The networkCode to set.
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * @return Returns the paymentMethodType.
     */
    public String getPaymentMethodType() {
        return _paymentMethodType;
    }

    /**
     * @param paymentMethodType
     *            The paymentMethodType to set.
     */
    public void setPaymentMethodType(String paymentMethodType) {
        _paymentMethodType = paymentMethodType;
    }

    /**
     * @return Returns the serviceType.
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * @param serviceType
     *            The serviceType to set.
     */
    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    /**
     * @return Returns the useDefaultInterface.
     */
    public String getUseDefaultInterface() {
        return _useDefaultInterface;
    }

    /**
     * @param useDefaultInterface
     *            The useDefaultInterface to set.
     */
    public void setUseDefaultInterface(String useDefaultInterface) {
        _useDefaultInterface = useDefaultInterface;
    }

    public String getPaymentKeyword() {
        return _paymentKeyword;
    }

    public void setPaymentKeyword(String paymentMethodKeyword) {
        this._paymentKeyword = paymentMethodKeyword;
    }

    /**
     * @return Returns the allServiceClassId.
     */
    public String getAllServiceClassId() {
        return _allServiceClassId;
    }

    /**
     * @param allServiceClassId
     *            The allServiceClassId to set.
     */
    public void setAllServiceClassId(String allServiceClassId) {
        _allServiceClassId = allServiceClassId;
    }

    /**
     * @return Returns the externalID.
     */
    public String getExternalID() {
        return _externalID;
    }

    /**
     * @param externalID
     *            The externalID to set.
     */
    public void setExternalID(String externalID) {
        _externalID = externalID;
    }

    /**
     * @return Returns the handlerClass.
     */
    public String getHandlerClass() {
        return _handlerClass;
    }

    /**
     * @param handlerClass
     *            The handlerClass to set.
     */
    public void setHandlerClass(String handlerClass) {
        _handlerClass = handlerClass;
    }

    /**
     * @return Returns the lang1Message.
     */
    public String getLang1Message() {
        return _lang1Message;
    }

    /**
     * @param lang1Message
     *            The lang1Message to set.
     */
    public void setLang1Message(String lang1Message) {
        _lang1Message = lang1Message;
    }

    /**
     * @return Returns the lang2Message.
     */
    public String getLang2Message() {
        return _lang2Message;
    }

    /**
     * @param lang2Message
     *            The lang2Message to set.
     */
    public void setLang2Message(String lang2Message) {
        _lang2Message = lang2Message;
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * @return Returns the underProcessMsgReq.
     */
    public String getUnderProcessMsgReq() {
        return _underProcessMsgReq;
    }

    /**
     * @param underProcessMsgReq
     *            The underProcessMsgReq to set.
     */
    public void setUnderProcessMsgReq(String underProcessMsgReq) {
        _underProcessMsgReq = underProcessMsgReq;
    }

    public String toString() {

        StringBuilder strBuild = new StringBuilder();
        strBuild.append(" _paymentKeyword  ").append(_paymentKeyword);
        strBuild.append(" _paymentMethodType  ").append(_paymentMethodType);
        strBuild.append(" _serviceType  ").append(_serviceType);
        strBuild.append(" _networkCode  ").append(_networkCode);
        strBuild.append(" _useDefaultInterface  ").append(_useDefaultInterface);
        strBuild.append(" _defaultInterfaceID  ").append(_defaultInterfaceID);
        strBuild.append(" _externalID ").append(_externalID);
        strBuild.append(" _status ").append(_status);
        strBuild.append(" _lang1Message ").append(_lang1Message);
        strBuild.append(" _lang2Message ").append(_lang2Message);
        strBuild.append(" _handlerClass ").append(_handlerClass);
        strBuild.append(" _underProcessMsgReq ").append(_underProcessMsgReq);
        strBuild.append(" _allServiceClassId ").append(_allServiceClassId);
        return strBuild.toString();
    }

    public String getStatusType() {
        return _statusType;
    }

    public void setStatusType(String statusTy) {
        _statusType = statusTy;
    }

}
