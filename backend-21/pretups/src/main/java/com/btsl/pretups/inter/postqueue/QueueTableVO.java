/**
 * @# QueueTableVO
 *    This class used to hold the value to enter into postpaid_cust_pay_master
 *    tabel
 *    or to fetch data from the postpaid_cust_pay_master tabel.
 * 
 *    Created on Created by History
 *    --------------------------------------------------------------------------
 *    ------
 *    March 28, 2006 Ankit Zindal Initial creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2006 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.inter.postqueue;

import java.io.Serializable;
import java.util.Date;

public class QueueTableVO implements Serializable {

    private String _queueID;// record id
    private String _networkID;// network code
    private String _transferID;// transfer id of pretups
    private String _accountID;// account id on billing system
    private String _msisdn;// msisdn of subscriber
    private long _amount;// amount to be transfered
    private String _status;// status of record in table
    private String _processID;// id of process that read data from this table
    private Date _processDate;// date at which process read data from this table

    private Date _entryOn;// date of entry in this table
    private String _description;// description of record
    private String _otherInfo;// other info if required to carry
    private String _serviceType;// service type used i.e. BILLPMT etc.
    private String _sourceType;// source type

    private String _entryType;// entry type i.e. CR or DR
    private String _processStatus;// status of process
    private String _senderID;// sender user id
    private Date _createdOn;// date of creation
    private String _interfaceID;// interface id

    private String _serviceClass;// service class for subscriber
    private String _externalInterfaceID;// external id of interface
    private String _productCode;// product code of pretups
    private String _module;// module code i.e. C2S or P2P

    private long _taxAmount;// tax amount paid
    private long _accessFee;// access fee paid
    private String _entryFor;// entry for sender or receiver
    private long _bonusAmount;// bonus amount given
    private String _senderMsisdn;// mobile number of sender
    private String _gatewayCode;// gateway code used to send request
    private String _cdrFileName;// name of CDR file in which this record apear
    private double _interfaceAmount;// amount to be send to billing system
    private String _interfaceAmountStr;
    private String _imsi;// imsi number of subscriber
    private String _receiverMsisdn;// receiver msisdn
    private String _type;// type ie. RC,PRC etc
    private String _ownerID;

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("_queueID  =" + _queueID);
        sbf.append(",_networkID =" + _networkID);
        sbf.append("_transferID  =" + _transferID);
        sbf.append(",_accountID  =" + _accountID);
        sbf.append(",_msisdn =" + _msisdn);
        sbf.append(",_amount =" + _amount);
        sbf.append(",_status =" + _status);
        sbf.append(",_processID =" + _processID);
        sbf.append(",_processDate =" + _processDate);
        sbf.append(",_entryOn =" + _entryOn);
        sbf.append(",_description =" + _description);
        sbf.append(",_otherInfo =" + _otherInfo);
        sbf.append(",_serviceType =" + _serviceType);
        sbf.append(",_sourceType =" + _sourceType);
        sbf.append(",_entryType =" + _entryType);
        sbf.append(",_processStatus =" + _processStatus);
        sbf.append(",_senderID =" + _senderID);
        sbf.append(",_createdOn =" + _createdOn);
        sbf.append(",_interfaceID =" + _interfaceID);
        sbf.append(",_serviceClass =" + _serviceClass);
        sbf.append(", _externalInterfaceID =" + _externalInterfaceID);
        sbf.append(",_productCode =" + _productCode);
        sbf.append(",_module =" + _module);
        sbf.append(",_taxAmount =" + _taxAmount);
        sbf.append(",_accessFee =" + _accessFee);
        sbf.append(",_entryFor =" + _entryFor);
        sbf.append(",_bonusAmount =" + _bonusAmount);
        sbf.append(",_senderMsisdn =" + _senderMsisdn);
        sbf.append(",_gatewayCode =" + _gatewayCode);
        sbf.append(",_cdrFileName =" + _cdrFileName);
        sbf.append(",_interfaceAmount =" + _interfaceAmount);
        sbf.append(",_imsi =" + _imsi);
        sbf.append(",_receiverMsisdn =" + _receiverMsisdn);
        sbf.append(",_type =" + _type);
        sbf.append(",_ownerID =" + _ownerID);
        return sbf.toString();
    }

    /**
     * @return Returns the accessFee.
     */
    public long getAccessFee() {
        return _accessFee;
    }

    /**
     * @param accessFee
     *            The accessFee to set.
     */
    public void setAccessFee(long accessFee) {
        _accessFee = accessFee;
    }

    /**
     * @return Returns the accountID.
     */
    public String getAccountID() {
        return _accountID;
    }

    /**
     * @param accountID
     *            The accountID to set.
     */
    public void setAccountID(String accountID) {
        _accountID = accountID;
    }

    /**
     * @return Returns the amount.
     */
    public long getAmount() {
        return _amount;
    }

    /**
     * @param amount
     *            The amount to set.
     */
    public void setAmount(long amount) {
        _amount = amount;
    }

    /**
     * @return Returns the bonusAmount.
     */
    public long getBonusAmount() {
        return _bonusAmount;
    }

    /**
     * @param bonusAmount
     *            The bonusAmount to set.
     */
    public void setBonusAmount(long bonusAmount) {
        _bonusAmount = bonusAmount;
    }

    /**
     * @return Returns the createdOn.
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @param createdOn
     *            The createdOn to set.
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return _description;
    }

    /**
     * @param description
     *            The description to set.
     */
    public void setDescription(String description) {
        _description = description;
    }

    /**
     * @return Returns the entryFor.
     */
    public String getEntryFor() {
        return _entryFor;
    }

    /**
     * @param entryFor
     *            The entryFor to set.
     */
    public void setEntryFor(String entryFor) {
        _entryFor = entryFor;
    }

    /**
     * @return Returns the entryOn.
     */
    public Date getEntryOn() {
        return _entryOn;
    }

    /**
     * @param entryOn
     *            The entryOn to set.
     */
    public void setEntryOn(Date entryOn) {
        _entryOn = entryOn;
    }

    /**
     * @return Returns the entryType.
     */
    public String getEntryType() {
        return _entryType;
    }

    /**
     * @param entryType
     *            The entryType to set.
     */
    public void setEntryType(String entryType) {
        _entryType = entryType;
    }

    /**
     * @return Returns the externalInterfaceID.
     */
    public String getExternalInterfaceID() {
        return _externalInterfaceID;
    }

    /**
     * @param externalInterfaceID
     *            The externalInterfaceID to set.
     */
    public void setExternalInterfaceID(String externalInterfaceID) {
        _externalInterfaceID = externalInterfaceID;
    }

    /**
     * @return Returns the interfaceID.
     */
    public String getInterfaceID() {
        return _interfaceID;
    }

    /**
     * @param interfaceID
     *            The interfaceID to set.
     */
    public void setInterfaceID(String interfaceID) {
        _interfaceID = interfaceID;
    }

    /**
     * @return Returns the module.
     */
    public String getModule() {
        return _module;
    }

    /**
     * @param module
     *            The module to set.
     */
    public void setModule(String module) {
        _module = module;
    }

    /**
     * @return Returns the msisdn.
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param msisdn
     *            The msisdn to set.
     */
    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    /**
     * @return Returns the networkID.
     */
    public String getNetworkID() {
        return _networkID;
    }

    /**
     * @param networkID
     *            The networkID to set.
     */
    public void setNetworkID(String networkID) {
        _networkID = networkID;
    }

    /**
     * @return Returns the otherInfo.
     */
    public String getOtherInfo() {
        return _otherInfo;
    }

    /**
     * @param otherInfo
     *            The otherInfo to set.
     */
    public void setOtherInfo(String otherInfo) {
        _otherInfo = otherInfo;
    }

    /**
     * @return Returns the processDate.
     */
    public Date getProcessDate() {
        return _processDate;
    }

    /**
     * @param processDate
     *            The processDate to set.
     */
    public void setProcessDate(Date processDate) {
        _processDate = processDate;
    }

    /**
     * @return Returns the processID.
     */
    public String getProcessID() {
        return _processID;
    }

    /**
     * @param processID
     *            The processID to set.
     */
    public void setProcessID(String processID) {
        _processID = processID;
    }

    /**
     * @return Returns the processStatus.
     */
    public String getProcessStatus() {
        return _processStatus;
    }

    /**
     * @param processStatus
     *            The processStatus to set.
     */
    public void setProcessStatus(String processStatus) {
        _processStatus = processStatus;
    }

    /**
     * @return Returns the productCode.
     */
    public String getProductCode() {
        return _productCode;
    }

    /**
     * @param productCode
     *            The productCode to set.
     */
    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    /**
     * @return Returns the queueID.
     */
    public String getQueueID() {
        return _queueID;
    }

    /**
     * @param queueID
     *            The queueID to set.
     */
    public void setQueueID(String queueID) {
        _queueID = queueID;
    }

    /**
     * @return Returns the senderID.
     */
    public String getSenderID() {
        return _senderID;
    }

    /**
     * @param senderID
     *            The senderID to set.
     */
    public void setSenderID(String senderID) {
        _senderID = senderID;
    }

    /**
     * @return Returns the senderMsisdn.
     */
    public String getSenderMsisdn() {
        return _senderMsisdn;
    }

    /**
     * @param senderMsisdn
     *            The senderMsisdn to set.
     */
    public void setSenderMsisdn(String senderMsisdn) {
        _senderMsisdn = senderMsisdn;
    }

    /**
     * @return Returns the serviceClass.
     */
    public String getServiceClass() {
        return _serviceClass;
    }

    /**
     * @param serviceClass
     *            The serviceClass to set.
     */
    public void setServiceClass(String serviceClass) {
        _serviceClass = serviceClass;
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
     * @return Returns the sourceType.
     */
    public String getSourceType() {
        return _sourceType;
    }

    /**
     * @param sourceType
     *            The sourceType to set.
     */
    public void setSourceType(String sourceType) {
        _sourceType = sourceType;
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
     * @return Returns the taxAmount.
     */
    public long getTaxAmount() {
        return _taxAmount;
    }

    /**
     * @param taxAmount
     *            The taxAmount to set.
     */
    public void setTaxAmount(long taxAmount) {
        _taxAmount = taxAmount;
    }

    /**
     * @return Returns the transferID.
     */
    public String getTransferID() {
        return _transferID;
    }

    /**
     * @param transferID
     *            The transferID to set.
     */
    public void setTransferID(String transferID) {
        _transferID = transferID;
    }

    /**
     * @return Returns the gatewayCode.
     */
    public String getGatewayCode() {
        return _gatewayCode;
    }

    /**
     * @param gatewayCode
     *            The gatewayCode to set.
     */
    public void setGatewayCode(String gatewayCode) {
        _gatewayCode = gatewayCode;
    }

    /**
     * @return Returns the cdrFileName.
     */
    public String getCdrFileName() {
        return _cdrFileName;
    }

    /**
     * @param cdrFileName
     *            The cdrFileName to set.
     */
    public void setCdrFileName(String cdrFileName) {
        _cdrFileName = cdrFileName;
    }

    /**
     * @return Returns the interfaceAmount.
     */
    public double getInterfaceAmount() {
        return _interfaceAmount;
    }

    /**
     * @param interfaceAmount
     *            The interfaceAmount to set.
     */
    public void setInterfaceAmount(double interfaceAmount) {
        _interfaceAmount = interfaceAmount;
    }

    /**
     * @return Returns the imsi.
     */
    public String getImsi() {
        return _imsi;
    }

    /**
     * @param imsi
     *            The imsi to set.
     */
    public void setImsi(String imsi) {
        _imsi = imsi;
    }

    /**
     * @return Returns the interfaceAmountStr.
     */
    public String getInterfaceAmountStr() {
        return _interfaceAmountStr;
    }

    /**
     * @param interfaceAmountStr
     *            The interfaceAmountStr to set.
     */
    public void setInterfaceAmountStr(String interfaceAmountStr) {
        _interfaceAmountStr = interfaceAmountStr;
    }

    /**
     * @return Returns the receiverMsisdn.
     */
    public String getReceiverMsisdn() {
        return _receiverMsisdn;
    }

    /**
     * @param msisdn
     *            The receiverMsisdn to set.
     */
    public void setReceiverMsisdn(String receiverMsisdn) {
        _receiverMsisdn = receiverMsisdn;
    }

    /**
     * @return Returns the type .
     */
    public String getType() {
        return _type;
    }

    /**
     * @param msisdn
     *            The type to set.
     */
    public void setType(String type) {
        _type = type;
    }

    /**
     * @return the ownerID
     */
    public String getOwnerID() {
        return _ownerID;
    }

    /**
     * @param ownerID
     *            the ownerID to set
     */
    public void setOwnerID(String ownerID) {
        _ownerID = ownerID;
    }

}
