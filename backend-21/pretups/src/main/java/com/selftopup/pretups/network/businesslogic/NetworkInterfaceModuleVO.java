/**
 * @(#)NetworkInterfaceModuleVO.java
 *                                   Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                   All Rights Reserved
 * 
 *                                   <description>
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Author Date History
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   avinash.kamthan Mar 28, 2005 Initital
 *                                   Creation
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 * 
 */

package com.selftopup.pretups.network.businesslogic;

import java.io.Serializable;

import com.selftopup.util.Constants;

/**
 * @author avinash.kamthan
 * 
 */
public class NetworkInterfaceModuleVO implements Serializable {
    private String _module;
    private String _networkCode;
    private String _methodType;
    private String _communicationType;
    private String _IP;
    private int _port;
    private String _className;

    public String getClassName() {
        return _className;
    }

    public void setClassName(String className) {
        _className = className;
    }

    public String getCommunicationType() {
        return _communicationType;
    }

    public void setCommunicationType(String communicationType) {
        _communicationType = communicationType;
    }

    public String getIP() {
        return _IP;
    }

    public void setIP(String ip) {
        _IP = ip;
    }

    public String getMethodType() {
        return _methodType;
    }

    public void setMethodType(String methodType) {
        _methodType = methodType;
    }

    public String getModule() {
        return _module;
    }

    public void setModule(String module) {
        _module = module;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public int getPort() {
        return _port;
    }

    public void setPort(int port) {
        _port = port;
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer();

        sbf.append(" module :" + _module + ",");
        sbf.append(" networkCode :" + _networkCode + ",");
        sbf.append(" methodType :" + _methodType + ",");
        sbf.append(" communicationType :" + _communicationType + ",");
        sbf.append(" IP :" + _IP + ",");
        sbf.append(" port :" + _port + ",");
        sbf.append(" className :" + _className + ",");

        return sbf.toString();
    }

    public String logInfo() {

        StringBuffer sbf = new StringBuffer(200);

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        sbf.append(startSeperator);
        sbf.append("Communication Type");
        sbf.append(middleSeperator);
        sbf.append(this.getCommunicationType());

        sbf.append(startSeperator);
        sbf.append("IP");
        sbf.append(middleSeperator);
        sbf.append(this.getIP());

        sbf.append(startSeperator);
        sbf.append("Port");
        sbf.append(middleSeperator);
        sbf.append(this.getPort());

        sbf.append(startSeperator);
        sbf.append("Class Name");
        sbf.append(middleSeperator);
        sbf.append(this.getClassName());

        return sbf.toString();
    }

}
