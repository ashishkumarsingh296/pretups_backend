package com.selftopup.loadcontroller;

import java.io.Serializable;

/*
 * NetworkLoadVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Travelling object for the Network Level Load
 */

public class NetworkLoadVO extends LoadVO implements Serializable {

    private String _instanceID = null;
    private String _networkCode = null;
    private String _c2sInstanceID = null;
    private String _p2pInstanceID = null;

    public String getInstanceID() {
        return _instanceID;
    }

    public void setInstanceID(String instanceID) {
        _instanceID = instanceID;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }
@Override
    public String toString() {
    	StringBuilder sbf = new StringBuilder();
   	    sbf.append("Instance ID=").append(_instanceID);
   	    sbf.append(" Network Code=").append(_networkCode);
   	    sbf.append(" C2S Instance ID=").append(_c2sInstanceID);
   	    sbf.append(" P2P Instance ID=").append(_p2pInstanceID).append(" ");
   	   
        
        return sbf.toString();
    }

    public String getC2sInstanceID() {
        return _c2sInstanceID;
    }

    public void setC2sInstanceID(String instanceID) {
        _c2sInstanceID = instanceID;
    }

    public String getP2pInstanceID() {
        return _p2pInstanceID;
    }

    public void setP2pInstanceID(String instanceID) {
        _p2pInstanceID = instanceID;
    }
}
