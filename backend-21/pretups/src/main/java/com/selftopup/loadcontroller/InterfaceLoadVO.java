package com.selftopup.loadcontroller;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * InterfaceLoadVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Travelling object for the Interface Level Load
 */

public class InterfaceLoadVO extends LoadVO implements Serializable {

    private String _instanceID = null;
    private String _networkCode = null;
    private String _interfaceID = null;
    private long _queueSize = 0;
    private long _queueTimeOut = 0;
    private long _currentQueueSize = 0;
    private Timestamp _lastQueueAdditionTime = null;
    private ArrayList _queueList = new ArrayList();
    private long _lastQueueCaseCheckTime = 0;
    private long _nextQueueCheckCaseAfterSec = 0;
@Override
    public String toString() {
    	StringBuilder sbf = new StringBuilder();
   	sbf.append("Instance ID=").append(_instanceID);
   	sbf.append(" Network Code=").append(_networkCode);
   	sbf.append(" InterfaceID=").append(_interfaceID);
   	sbf.append(" Queue Size=").append(_queueSize);
   	sbf.append(" Current queue Size=").append(_currentQueueSize);
   	sbf.append(" Queue Time Out=").append(_queueTimeOut);
   	sbf.append(" Queue List=").append(_queueList);
   	sbf.append(" _lastQueueCaseCheckTime=").append(_lastQueueCaseCheckTime);
   	sbf.append(" _nextQueueCheckCaseAfterSec=").append(_nextQueueCheckCaseAfterSec);
   	
        return sbf.toString();
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

    public long getQueueSize() {
        return _queueSize;
    }

    public void setQueueSize(long queueSize) {
        _queueSize = queueSize;
    }

    public long getQueueTimeOut() {
        return _queueTimeOut;
    }

    public void setQueueTimeOut(long queueTimeOut) {
        _queueTimeOut = queueTimeOut;
    }

    public long getCurrentQueueSize() {
        return _currentQueueSize;
    }

    public void setCurrentQueueSize(long currentQueueSize) {
        _currentQueueSize = currentQueueSize;
    }

    public Timestamp getLastQueueAdditionTime() {
        return _lastQueueAdditionTime;
    }

    public void setLastQueueAdditionTime(Timestamp lastQueueAdditionTime) {
        _lastQueueAdditionTime = lastQueueAdditionTime;
    }

    public ArrayList getQueueList() {
        return _queueList;
    }

    public void setQueueList(ArrayList queueList) {
        _queueList = queueList;
    }

    public long getLastQueueCaseCheckTime() {
        return _lastQueueCaseCheckTime;
    }

    public void setLastQueueCaseCheckTime(long lastQueueCaseCheckTime) {
        _lastQueueCaseCheckTime = lastQueueCaseCheckTime;
    }

    public long getNextQueueCheckCaseAfterSec() {
        return _nextQueueCheckCaseAfterSec;
    }

    public void setNextQueueCheckCaseAfterSec(long nextQueueCheckCaseAfterSec) {
        _nextQueueCheckCaseAfterSec = nextQueueCheckCaseAfterSec;
    }
}
