package com.btsl.loadcontroller;

import java.io.Serializable;

/*
 * InstanceLoadVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Travelling object for the Instance Level Load
 */

public class InstanceLoadVO extends LoadVO implements Serializable {

    private String _instanceID = null;
    private String _instanceName = null;
    private String _currentStatus = null;
    private String _currentStatusDesc = null;
    private String _hostAddress = null;
    private String _hostPort = null;
    private boolean _instanceLoadStatus = false; // Instance Load Type
    private long _instanceMemoryTxnCount = 0;
    private long _instanceLastMemoryTxnNo = 0;
    private String _instanceType;

    private String _loadTypeTps = null;
    private long _maxAllowedLoad = 0;
    private long _maxAllowedTps = 0;
    private String _moduleCode = null;

    private String _showSmscStatus = null;// for showing SMSC status on
                                          // monitorserver screen (Y/N)
    private String _showOamlogs = null;// for showing OAM status on
                                       // monitorserver screen (Y/N)
    private String _isDR = null;
    private String _authPass = null;
    private String _context = null;
    private String rstInstanceID;
    
    
    public String getRstInstanceID() {
		return rstInstanceID;
	}

	public void setRstInstanceID(String rstInstanceID) {
		this.rstInstanceID = rstInstanceID;
	}

	/**
     * @return Returns the currentStatusDesc.
     */
    public String getCurrentStatusDesc() {
        return _currentStatusDesc;
    }

    /**
     * @param currentStatusDesc
     *            The currentStatusDesc to set.
     */
    public void setCurrentStatusDesc(String currentStatusDesc) {
        _currentStatusDesc = currentStatusDesc;
    }

    /**
     * @return Returns the loadTypeTps.
     */
    public String getLoadTypeTps() {
        return _loadTypeTps;
    }

    /**
     * @param loadTypeTps
     *            The loadTypeTps to set.
     */
    public void setLoadTypeTps(String loadTypeTps) {
        _loadTypeTps = loadTypeTps;
    }

    /**
     * @return Returns the maxAllowedLoad.
     */
    public long getMaxAllowedLoad() {
        return _maxAllowedLoad;
    }

    /**
     * @param maxAllowedLoad
     *            The maxAllowedLoad to set.
     */
    public void setMaxAllowedLoad(long maxAllowedLoad) {
        _maxAllowedLoad = maxAllowedLoad;
    }

    /**
     * @return Returns the maxAllowedTps.
     */
    public long getMaxAllowedTps() {
        return _maxAllowedTps;
    }

    /**
     * @param maxAllowedTps
     *            The maxAllowedTps to set.
     */
    public void setMaxAllowedTps(long maxAllowedTps) {
        _maxAllowedTps = maxAllowedTps;
    }

    /**
     * @return Returns the instanceType.
     */
    public String getInstanceType() {
        return _instanceType;
    }

    /**
     * @param instanceType
     *            The instanceType to set.
     */
    public void setInstanceType(String instanceType) {
        _instanceType = instanceType;
    }

    public String getCurrentStatus() {
        return _currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        _currentStatus = currentStatus;
    }

    public String getHostAddress() {
        return _hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        _hostAddress = hostAddress;
    }

    public String getHostPort() {
        return _hostPort;
    }

    public void setHostPort(String hostPort) {
        _hostPort = hostPort;
    }

    public String getModule() {
        return _moduleCode;
    }

    public void setModule(String moduleCode) {
        _moduleCode = moduleCode;
    }

    /*
     * public long getInstanceCurrentTPS() {
     * return _instanceCurrentTPS;
     * }
     * public void setInstanceCurrentTPS(long instanceCurrentTPS) {
     * _instanceCurrentTPS = instanceCurrentTPS;
     * }
     * public long getInstanceDefualtTPS() {
     * return _instanceDefualtTPS;
     * }
     * public void setInstanceDefualtTPS(long instanceDefualtTPS) {
     * _instanceDefualtTPS = instanceDefualtTPS;
     * }
     */
    public String getInstanceID() {
        return _instanceID;
    }

    public void setInstanceID(String instanceID) {
        _instanceID = instanceID;
    }

    public long getInstanceLastMemoryTxnNo() {
        return _instanceLastMemoryTxnNo;
    }

    public void setInstanceLastMemoryTxnNo(long instanceLastMemoryTxnNo) {
        _instanceLastMemoryTxnNo = instanceLastMemoryTxnNo;
    }

    public long getInstanceMemoryTxnCount() {
        return _instanceMemoryTxnCount;
    }

    public void setInstanceMemoryTxnCount(long instanceMemoryTxnCount) {
        _instanceMemoryTxnCount = instanceMemoryTxnCount;
    }

    public String getInstanceName() {
        return _instanceName;
    }

    public void setInstanceName(String instanceName) {
        _instanceName = instanceName;
    }

    /*
     * public long getTransactionLoad() {
     * return _transactionLoad;
     * }
     * public void setTransactionLoad(long transactionLoad) {
     * _transactionLoad = transactionLoad;
     * }
     * public long getCurrentTransactionLoad() {
     * return _currentTransactionLoad;
     * }
     * public void setCurrentTransactionLoad(long currentTransactionLoad) {
     * _currentTransactionLoad = currentTransactionLoad;
     * }
     * public Timestamp getLastReceievedTime() {
     * return _lastReceievedTime;
     * }
     * public void setLastReceievedTime(Timestamp lastReceievedTime) {
     * _lastReceievedTime = lastReceievedTime;
     * }
     * public long getTotalRefusedCount() {
     * return _totalRefusedCount;
     * }
     * public void setTotalRefusedCount(long totalRefusedCount) {
     * _totalRefusedCount = totalRefusedCount;
     * }
     * public Timestamp getLastRefusedTime() {
     * return _lastRefusedTime;
     * }
     * public void setLastRefusedTime(Timestamp lastRefusedTime) {
     * _lastRefusedTime = lastRefusedTime;
     * }
     * public long getInstanceTimeoutSec() {
     * return _instanceTimeoutSec;
     * }
     * public void setInstanceTimeoutSec(long instanceTimeoutSec) {
     * _instanceTimeoutSec = instanceTimeoutSec;
     * }
     * public Timestamp getLastTxnProcessStartTime() {
     * return _lastTxnProcessStartTime;
     * }
     * public void setLastTxnProcessStartTime(Timestamp lastTxnProcessStartTime)
     * {
     * _lastTxnProcessStartTime = lastTxnProcessStartTime;
     * }
     * public long getInstanceRequestCount() {
     * return _instanceRequestCount;
     * }
     * public void setInstanceRequestCount(long instanceRequestCount) {
     * _instanceRequestCount = instanceRequestCount;
     * }
     * public long getInstanceRecievedCount() {
     * return _instanceRecievedCount;
     * }
     * public void setInstanceRecievedCount(long instanceRecievedCount) {
     * _instanceRecievedCount = instanceRecievedCount;
     * }
     * public long getNoOfRequestSameSec() {
     * return _noOfRequestSameSec;
     * }
     * public void setNoOfRequestSameSec(long noOfRequestSameSec) {
     * _noOfRequestSameSec = noOfRequestSameSec;
     * }
     * public long getPreviousSecond() {
     * return _previousSecond;
     * }
     * public void setPreviousSecond(long previousSecond) {
     * _previousSecond = previousSecond;
     * }
     * public long getFirstRequestTime() {
     * return _firstRequestTime;
     * }
     * public void setFirstRequestTime(long firstRequestTime) {
     * _firstRequestTime = firstRequestTime;
     * }
     * public long getFirstRequestCount() {
     * return _firstRequestCount;
     * }
     * public void setFirstRequestCount(long firstRequestCount) {
     * _firstRequestCount = firstRequestCount;
     * }
     * public String getInstanceLoadType() {
     * return _instanceLoadType;
     * }
     * public void setInstanceLoadType(String instanceLoadType) {
     * _instanceLoadType = instanceLoadType;
     * }
     */
    public boolean isInstanceLoadStatus() {
        return _instanceLoadStatus;
    }

    public void setInstanceLoadStatus(boolean instanceLoadStatus) {
        _instanceLoadStatus = instanceLoadStatus;
    }

    public String toString() {
        StringBuilder strBuild = new StringBuilder();
        strBuild.append("Instance ID=");
        strBuild.append(_instanceID);
        strBuild.append(" Host Address=");
        strBuild.append(_hostAddress);
        strBuild.append(" Host Port=");
        strBuild.append(_hostPort);
        strBuild.append(" Instance Load Status=");
        strBuild.append(_instanceLoadStatus);
        strBuild.append(" Instance Memory Txn Count=");
        strBuild.append(_instanceMemoryTxnCount);
        strBuild.append("Instance Last Memory Txn No:");
        strBuild.append(_instanceLastMemoryTxnNo);
        strBuild.append(" Instance Type=");
        strBuild.append(_instanceType);
        strBuild.append("Show OAM Status");
        strBuild.append(_showOamlogs);
        strBuild.append("Show SMSC Status");
        strBuild.append(_showSmscStatus);
        strBuild.append(" ");
        strBuild.append(super.toString());
        return strBuild.toString();

        /*
         * String temp="Instance ID="+_instanceID+" Host Address="+_hostAddress+
         * " Host Port="+_hostPort+" Instance Load Status="+_instanceLoadStatus+
         * " Instance Memory Txn Count="
         * +_instanceMemoryTxnCount+"Instance Last Memory Txn No:"
         * +_instanceLastMemoryTxnNo
         * +" Instance Type="+_instanceType+"Show OAM Status"
         * +_showOamlogs+"Show SMSC Status"+_showSmscStatus;
         * temp=temp+" "+super.toString();
         * return temp;
         */
    }

    /**
     * @return Returns the showOamlogs.
     */
    public String getShowOamlogs() {
        return _showOamlogs;
    }

    /**
     * @param showOamlogs
     *            The showOamlogs to set.
     */
    public void setShowOamlogs(String showOamlogs) {
        _showOamlogs = showOamlogs;
    }

    /**
     * @return Returns the showSmscStatus.
     */
    public String getShowSmscStatus() {
        return _showSmscStatus;
    }

    /**
     * @param showSmscStatus
     *            The showSmscStatus to set.
     */
    public void setShowSmscStatus(String showSmscStatus) {
        _showSmscStatus = showSmscStatus;
    }

    public String getIsDR() {
        return _isDR;
    }

    public void setIsDR(String _isdr) {
        _isDR = _isdr;
    }

    public String getAuthPass() {
        return _authPass;
    }

    public void setAuthPass(String authPass) {
        _authPass = authPass;
    }

    public String getContext() {
        return _context;
    }

    public void setContext(String _context) {
        this._context = _context;
    }
}
