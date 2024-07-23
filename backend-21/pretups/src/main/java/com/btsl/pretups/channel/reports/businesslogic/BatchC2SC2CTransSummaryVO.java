/*
 * @(#)BatchC2SC2CTransSummaryVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Anu Garg 08/11/2011 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright(c) 2011, Comviva Technologies Ltd.
 */
package com.btsl.pretups.channel.reports.businesslogic;

public class BatchC2SC2CTransSummaryVO {

    private static final long serialVersionUID = 1L;
    private String _msisdn = null;
    private String _loginId = null;
    private String _recordNumber = null;
    private String _userName = null;
    private String _geographicalName = null;
    private String _categoryName = null;
    private String _productName = null;

    private String _transferSubType = null;
    private String _transInAmount = null;
    private String _transOutAmount = null;
    private String _transInCount = null;
    private String _transOutCount = null;

    private String _serviceType = null;
    private String _c2sTotalTransactions = null;
    private String _c2sTotalFailTransactions = null;
    private String _c2sRechargeCount = null;
    private String _c2sRechargeAmount = null;

    public String getLoginId() {
        return _loginId;
    }

    public void setLoginId(String loginId) {
        _loginId = loginId;
    }

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    /**
     * @return Returns the recordNumber.
     */
    public String getRecordNumber() {
        return _recordNumber;
    }

    /**
     * @param recordNumber
     *            The recordNumber to set.
     */
    public void setRecordNumber(String recordNumber) {
        _recordNumber = recordNumber;
    }

    public String getUserName() {
        return _userName;
    }

    public void setUserName(String userName) {
        _userName = userName;
    }

    public String getGeographicalName() {
        return _geographicalName;
    }

    public void setGeographicalName(String geographicalName) {
        _geographicalName = geographicalName;
    }

    public String getCategoryName() {
        return _categoryName;
    }

    public void setCategoryName(String categoryName) {
        _categoryName = categoryName;
    }

    public String getProductName() {
        return _productName;
    }

    public void setProductName(String productName) {
        _productName = productName;
    }

    public String getTransferSubType() {
        return _transferSubType;
    }

    public void setTransferSubType(String transferSubType) {
        _transferSubType = transferSubType;
    }

    public String getTransInAmount() {
        return _transInAmount;
    }

    public void setTransInAmount(String transInAmount) {
        _transInAmount = transInAmount;
    }

    public String getTransOutAmount() {
        return _transOutAmount;
    }

    public void setTransOutAmount(String transOutAmount) {
        _transOutAmount = transOutAmount;
    }

    public String getTransInCount() {
        return _transInCount;
    }

    public void setTransInCount(String transInCount) {
        _transInCount = transInCount;
    }

    public String getTransOutCount() {
        return _transOutCount;
    }

    public void setTransOutCount(String transOutCount) {
        _transOutCount = transOutCount;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    public String getC2STotalTransactions() {
        return _c2sTotalTransactions;
    }

    public void setC2STotalTransactions(String c2sTotalTransactions) {
        _c2sTotalTransactions = c2sTotalTransactions;
    }

    public String getC2STotalFailTransactions() {
        return _c2sTotalFailTransactions;
    }

    public void setC2STotalFailTransactions(String c2sTotalFailTransactions) {
        _c2sTotalFailTransactions = c2sTotalFailTransactions;
    }

    public String getC2sRechargeCount() {
        return _c2sRechargeCount;
    }

    public void setC2sRechargeCount(String c2sRechargeCount) {
        _c2sRechargeCount = c2sRechargeCount;
    }

    public String getC2sRechargeAmount() {
        return _c2sRechargeAmount;
    }

    public void setC2sRechargeAmount(String c2sRechargeAmount) {
        _c2sRechargeAmount = c2sRechargeAmount;
    }

}
