package com.btsl.loadcontroller;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/*
 * LoadVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Base class for all travelling object use din Load controller
 */

public class LoadVO implements Serializable {

    private long _transactionLoad = 0;
    private long _currentTransactionLoad = 0; // Currently how many requests are
                                              // there in network
    private long _receiverCurrentTransactionLoad = 0; // Currently how many
                                                      // requests for receiver
                                                      // are there in network
    private long _requestTimeoutSec = 0;
    private long _defualtTPS = 0; // Default TPS Value with which the last no of
                                  // request time will be compared
    private String _loadType = null; // Load Type to be used TPS or TXN counts
    private long _currentTPS = 0; // Current TPS To be used
    private long _noOfRequestSameSec = 0; // Total No of Request in same second
    private long _previousSecond = 0; // Current TPS To be used
    private long _recievedCount = 0; // Total Transaction request counts in the
                                     // network (Success+ Failed)
    private long _requestCount = 0; // Total Transaction request counts in the
                                    // network (Only Success)
    private Timestamp _lastReceievedTime = null;
    private Timestamp _lastTxnProcessStartTime = null;
    private long _totalRefusedCount = 0;
    private Timestamp _lastRefusedTime = null;
    private long _firstRequestTime = 0; // Stored intentionally so that it will
                                        // help in caluclating Average
    private long _firstRequestCount = 0; // Stored intentionally so that it will
                                         // help in caluclating Average

    private long _definedTransactionLoad = 0;
    private long _definedTPS = 0; // Current TPS To be used

    private Date _lastInitializationTime = null;
    private long _minimumProcessTime = 0;

    private String _modifiedBy = null;
    private Date _modifiedOn = null;

    private String _transactionLoadStr = null;
    private String _requestTimeOutSecStr = null;
    private String _minimumProcessTimeStr = null;
    private String _definedTPSStr = null;
@Override
    public String toString() {
    	StringBuilder sbf = new StringBuilder();
    	sbf.append("Transaction Load=").append(_transactionLoad);
    	sbf.append(" Current Transaction Load=").append(_currentTransactionLoad);
    	sbf.append(" Receiver Current Transaction Load=").append(_receiverCurrentTransactionLoad);
    	sbf.append(" _requestTimeoutSec=").append(_requestTimeoutSec);
    	sbf.append(" _defualtTPS=").append(_defualtTPS);
    	sbf.append(" _currentTPS=").append(_currentTPS);
    	sbf.append(" No Of Request in Same Sec=").append(_noOfRequestSameSec);
    	sbf.append(" Previous Second=").append(_previousSecond);
    	sbf.append(" Recieved Count=").append(_recievedCount);
    	sbf.append(" Request Count=").append(_requestCount);
    	sbf.append(" Total Refused Count=").append(_totalRefusedCount);
    	sbf.append(" First Request Count=").append(_firstRequestCount);
    	sbf.append(" _minimumProcessTime=").append(_minimumProcessTime);
        return sbf.toString();
    }

    public long getCurrentTPS() {
        return _currentTPS;
    }

    public void setCurrentTPS(long currentTPS) {
        _currentTPS = currentTPS;
    }

    public long getCurrentTransactionLoad() {
        return _currentTransactionLoad;
    }

    public void setCurrentTransactionLoad(long currentTransactionLoad) {
        _currentTransactionLoad = currentTransactionLoad;
    }

    public long getDefualtTPS() {
        return _defualtTPS;
    }

    public void setDefualtTPS(long defualtTPS) {
        _defualtTPS = defualtTPS;
    }

    public long getFirstRequestCount() {
        return _firstRequestCount;
    }

    public void setFirstRequestCount(long firstRequestCount) {
        _firstRequestCount = firstRequestCount;
    }

    public long getFirstRequestTime() {
        return _firstRequestTime;
    }

    public void setFirstRequestTime(long firstRequestTime) {
        _firstRequestTime = firstRequestTime;
    }

    public Timestamp getLastReceievedTime()
    {
    	Timestamp temp = _lastReceievedTime;
        return temp;
    }

    public void setLastReceievedTime(Timestamp lastReceievedTime)
    {
    	Timestamp temp = lastReceievedTime;
        this._lastReceievedTime = temp;
    }

    public Timestamp getLastRefusedTime() {
    	Timestamp temp = _lastRefusedTime;
        return temp;    
    }

    public void setLastRefusedTime(Timestamp lastRefusedTime)
    {
    	Timestamp temp = lastRefusedTime;
        this._lastRefusedTime = temp;
    }

    public Timestamp getLastTxnProcessStartTime() {
    	Timestamp temp = _lastTxnProcessStartTime;
        return temp;
    }

    public void setLastTxnProcessStartTime(Timestamp lastTxnProcessStartTime)
    {
    	Timestamp temp = lastTxnProcessStartTime;
        this._lastTxnProcessStartTime = temp;
    }

    public String getLoadType() {
        return _loadType;
    }

    public void setLoadType(String loadType) {
        _loadType = loadType;
    }

    public long getNoOfRequestSameSec() {
        return _noOfRequestSameSec;
    }

    public void setNoOfRequestSameSec(long noOfRequestSameSec) {
        _noOfRequestSameSec = noOfRequestSameSec;
    }

    public long getPreviousSecond() {
        return _previousSecond;
    }

    public void setPreviousSecond(long previousSecond) {
        _previousSecond = previousSecond;
    }

    public long getRecievedCount() {
        return _recievedCount;
    }

    public void setRecievedCount(long recievedCount) {
        _recievedCount = recievedCount;
    }

    public long getRequestCount() {
        return _requestCount;
    }

    public void setRequestCount(long requestCount) {
        _requestCount = requestCount;
    }

    public long getRequestTimeoutSec() {
        return _requestTimeoutSec;
    }

    public void setRequestTimeoutSec(long requestTimeoutSec) {
        _requestTimeoutSec = requestTimeoutSec;
    }

    public long getTotalRefusedCount() {
        return _totalRefusedCount;
    }

    public void setTotalRefusedCount(long totalRefusedCount) {
        _totalRefusedCount = totalRefusedCount;
    }

    public long getTransactionLoad() {
        return _transactionLoad;
    }

    public void setTransactionLoad(long transactionLoad) {
        _transactionLoad = transactionLoad;
    }

    public long getDefinedTPS() {
        return _definedTPS;
    }

    public void setDefinedTPS(long definedTPS) {
        _definedTPS = definedTPS;
    }

    public long getDefinedTransactionLoad() {
        return _definedTransactionLoad;
    }

    public void setDefinedTransactionLoad(long definedTransactionLoad) {
        _definedTransactionLoad = definedTransactionLoad;
    }

    public long getReceiverCurrentTransactionLoad() {
        return _receiverCurrentTransactionLoad;
    }

    public void setReceiverCurrentTransactionLoad(long receiverCurrentTransactionLoad) {
        _receiverCurrentTransactionLoad = receiverCurrentTransactionLoad;
    }

    public Date getLastInitializationTime()
    {
    	Date temp = _lastInitializationTime;
        return temp;
    }

    public void setLastInitializationTime(Date lastInitializationTime) {
    	Date temp = lastInitializationTime;
        this._lastInitializationTime = temp;
    }

    public long getMinimumProcessTime() {
        return _minimumProcessTime;
    }

    public void setMinimumProcessTime(long minimumProcessTime) {
        _minimumProcessTime = minimumProcessTime;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    public Date getModifiedOn() {
    	Date temp = _modifiedOn;
        return temp;
    }

    public void setModifiedOn(Date modifiedOn) {
    	Date temp = modifiedOn;
        this._modifiedOn = temp;
    }

    public String getDefinedTPSStr() {
        return _definedTPSStr;
    }

    public void setDefinedTPSStr(String definedTPSStr) {
        _definedTPSStr = definedTPSStr;
    }

    public String getMinimumProcessTimeStr() {
        return _minimumProcessTimeStr;
    }

    public void setMinimumProcessTimeStr(String minimumProcessTimeStr) {
        _minimumProcessTimeStr = minimumProcessTimeStr;
    }

    public String getRequestTimeOutSecStr() {
        return _requestTimeOutSecStr;
    }

    public void setRequestTimeOutSecStr(String requestTimeOutSecStr) {
        _requestTimeOutSecStr = requestTimeOutSecStr;
    }

    public String getTransactionLoadStr() {
        return _transactionLoadStr;
    }

    public void setTransactionLoadStr(String transactionLoadStr) {
        _transactionLoadStr = transactionLoadStr;
    }

}
