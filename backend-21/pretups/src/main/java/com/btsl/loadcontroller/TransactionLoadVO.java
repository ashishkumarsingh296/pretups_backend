package com.btsl.loadcontroller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * TransactionLoadVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Travelling object for the transactions
 */

public class TransactionLoadVO extends LoadVO implements Serializable {

    private String _instanceID = null;
    private String _networkCode = null;
    private String _interfaceID = null;
    private String _serviceType = null;
    private long _minimumServiceTime = 0;
    private long _averageServiceTime = 0;
    private long _lastTimeOutCaseCheckTime = 0;
    private long _nextCheckTimeOutCaseAfterSec = 0;

    private int _overFlowCount = 0;
    private int _definedOverFlowCount = 0;
    private ArrayList _alternateServiceLoadType = null;
    private HashMap _transactionListMap = null;

    private long _totalSenderValidationCount = 0;
    private long _currentSenderValidationCount = 0;
    private long _totalRecieverValidationCount = 0;
    private long _currentRecieverValidationCount = 0;
    private long _totalSenderTopupCount = 0;
    private long _currentSenderTopupCount = 0;
    private long _totalRecieverTopupCount = 0;
    private long _currentRecieverTopupCount = 0;
    private long _totalInternalFailCount = 0;

    private long _totalSenderValFailCount = 0;
    private long _totalRecieverValFailCount = 0;
    private long _totalSenderTopupFailCount = 0;
    private long _totalRecieverTopupFailCount = 0;

    public String toString() {

        StringBuilder sbd = new StringBuilder();
        sbd.append("Instance ID=").append(_instanceID).append(" Network Code=").append(_networkCode).append(" InterfaceID=").append(_interfaceID).append(" Service Type=").append(_serviceType).append(" Over Flow Count=").append(_overFlowCount).append(" Defined OverFlow Count=").append(_definedOverFlowCount).append(" _alternateServiceLoadType=").append(_alternateServiceLoadType).append(" _totalSenderValidationCount=").append(_totalSenderValidationCount).append(" _currentSenderValidationCount=").append(_currentSenderValidationCount);
        sbd.append(" _totalRecieverValidationCount=").append(_totalRecieverValidationCount).append(" _currentRecieverValidationCount=").append(_currentRecieverValidationCount).append(" _totalSenderTopupCount=").append(_totalSenderTopupCount).append("_currentSenderTopupCount=").append(_currentSenderTopupCount).append(" _totalRecieverTopupCount=").append(_totalRecieverTopupCount).append(" _currentRecieverTopupCount=").append(_currentRecieverTopupCount).append(" _totalSenderValFailCount=").append(_totalSenderValFailCount).append(" _totalRecieverValFailCount=").append(_totalRecieverValFailCount);
        sbd.append(" _totalSenderTopupFailCount=").append(_totalSenderTopupFailCount).append(" _totalRecieverTopupFailCount=").append(_totalRecieverTopupFailCount);
        sbd.append(" ").append(super.toString());
        return sbd.toString();

    }

    public String getInstanceID() {
        return _instanceID;
    }

    public void setInstanceID(String instanceID) {
        _instanceID = instanceID;
    }

    public String getInterfaceID() {
        return _interfaceID;
    }

    public void setInterfaceID(String interfaceID) {
        _interfaceID = interfaceID;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    public long getMinimumServiceTime() {
        return _minimumServiceTime;
    }

    public void setMinimumServiceTime(long minimumServiceTime) {
        _minimumServiceTime = minimumServiceTime;
    }

    public int getOverFlowCount() {
        return _overFlowCount;
    }

    public void setOverFlowCount(int overFlowCount) {
        _overFlowCount = overFlowCount;
    }

    public ArrayList getAlternateServiceLoadType() {
        return _alternateServiceLoadType;
    }

    public void setAlternateServiceLoadType(ArrayList alternateServiceLoadType) {
        this._alternateServiceLoadType = alternateServiceLoadType;
    }

    public HashMap getTransactionListMap() {
        return _transactionListMap;
    }

    public void setTransactionListMap(HashMap transactionListMap) {
        _transactionListMap = transactionListMap;
    }

    public long getAverageServiceTime() {
        return _averageServiceTime;
    }

    public void setAverageServiceTime(long averageServiceTime) {
        _averageServiceTime = averageServiceTime;
    }

    public long getLastTimeOutCaseCheckTime() {
        return _lastTimeOutCaseCheckTime;
    }

    public void setLastTimeOutCaseCheckTime(long lastTimeOutCaseCheckTime) {
        _lastTimeOutCaseCheckTime = lastTimeOutCaseCheckTime;
    }

    public long getNextCheckTimeOutCaseAfterSec() {
        return _nextCheckTimeOutCaseAfterSec;
    }

    public void setNextCheckTimeOutCaseAfterSec(long nextCheckTimeOutCaseAfterSec) {
        _nextCheckTimeOutCaseAfterSec = nextCheckTimeOutCaseAfterSec;
    }

    public long getCurrentRecieverTopupCount() {
        return _currentRecieverTopupCount;
    }

    public void setCurrentRecieverTopupCount(long currentRecieverTopupCount) {
        _currentRecieverTopupCount = currentRecieverTopupCount;
    }

    public long getCurrentRecieverValidationCount() {
        return _currentRecieverValidationCount;
    }

    public void setCurrentRecieverValidationCount(long currentRecieverValidationCount) {
        _currentRecieverValidationCount = currentRecieverValidationCount;
    }

    public long getCurrentSenderTopupCount() {
        return _currentSenderTopupCount;
    }

    public void setCurrentSenderTopupCount(long currentSenderTopupCount) {
        _currentSenderTopupCount = currentSenderTopupCount;
    }

    public long getCurrentSenderValidationCount() {
        return _currentSenderValidationCount;
    }

    public void setCurrentSenderValidationCount(long currentSenderValidationCount) {
        _currentSenderValidationCount = currentSenderValidationCount;
    }

    public long getTotalRecieverTopupCount() {
        return _totalRecieverTopupCount;
    }

    public void setTotalRecieverTopupCount(long totalRecieverTopupCount) {
        _totalRecieverTopupCount = totalRecieverTopupCount;
    }

    public long getTotalRecieverValidationCount() {
        return _totalRecieverValidationCount;
    }

    public void setTotalRecieverValidationCount(long totalRecieverValidationCount) {
        _totalRecieverValidationCount = totalRecieverValidationCount;
    }

    public long getTotalSenderTopupCount() {
        return _totalSenderTopupCount;
    }

    public void setTotalSenderTopupCount(long totalSenderTopupCount) {
        _totalSenderTopupCount = totalSenderTopupCount;
    }

    public long getTotalSenderValidationCount() {
        return _totalSenderValidationCount;
    }

    public void setTotalSenderValidationCount(long totalSenderValidationCount) {
        _totalSenderValidationCount = totalSenderValidationCount;
    }

    public long getTotalInternalFailCount() {
        return _totalInternalFailCount;
    }

    public void setTotalInternalFailCount(long totalInternalFailCount) {
        _totalInternalFailCount = totalInternalFailCount;
    }

    public long getTotalRecieverTopupFailCount() {
        return _totalRecieverTopupFailCount;
    }

    public void setTotalRecieverTopupFailCount(long totalRecieverTopupFailCount) {
        _totalRecieverTopupFailCount = totalRecieverTopupFailCount;
    }

    public long getTotalRecieverValFailCount() {
        return _totalRecieverValFailCount;
    }

    public void setTotalRecieverValFailCount(long totalRecieverValFailCount) {
        _totalRecieverValFailCount = totalRecieverValFailCount;
    }

    public long getTotalSenderTopupFailCount() {
        return _totalSenderTopupFailCount;
    }

    public void setTotalSenderTopupFailCount(long totalSenderTopupFailCount) {
        _totalSenderTopupFailCount = totalSenderTopupFailCount;
    }

    public long getTotalSenderValFailCount() {
        return _totalSenderValFailCount;
    }

    public void setTotalSenderValFailCount(long totalSenderValFailCount) {
        _totalSenderValFailCount = totalSenderValFailCount;
    }

    public int getDefinedOverFlowCount() {
        return _definedOverFlowCount;
    }

    public void setDefinedOverFlowCount(int definedOverFlowCount) {
        _definedOverFlowCount = definedOverFlowCount;
    }

}
