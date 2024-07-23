package com.btsl.loadcontroller;

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

    public String toString() {
        StringBuilder strBuild = new StringBuilder();
        strBuild.append("Instance ID=");
        strBuild.append(_instanceID);
        strBuild.append(" Network Code=");
        strBuild.append(_networkCode);
        strBuild.append(" C2S Instance ID=");
        strBuild.append(_c2sInstanceID);
        strBuild.append(" P2P Instance ID=");
        strBuild.append(_p2pInstanceID);

        strBuild.append(" ");
        strBuild.append(super.toString());
        return strBuild.toString();

        /*
         * String temp="Instance ID="+_instanceID+" Network Code="+_networkCode+
         * " C2S Instance ID="
         * +_c2sInstanceID+" P2P Instance ID="+_p2pInstanceID;
         * temp=temp+" "+super.toString();
         * return temp;
         */
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
