/*
 * SubscriberRoutingControlVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 30/10/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
package com.selftopup.pretups.routing.master.businesslogic;

import java.io.Serializable;

import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

public class SubscriberRoutingControlVO implements Serializable {

    private String _networkCode;
    private String _serviceType;
    private String _interfaceCategory;
    private String _databaseCheck;
    private String _seriesCheck;
    private boolean _databaseCheckBool;
    private boolean _seriesCheckBool;

    public String getDatabaseCheck() {
        return _databaseCheck;
    }

    public void setDatabaseCheck(String databaseCheck) {
        _databaseCheck = databaseCheck;
    }

    public boolean isDatabaseCheckBool() {
        return _databaseCheckBool;
    }

    public void setDatabaseCheckBool(boolean databaseCheckBool) {
        _databaseCheckBool = databaseCheckBool;
    }

    public String getInterfaceCategory() {
        return _interfaceCategory;
    }

    public void setInterfaceCategory(String interfaceCategory) {
        _interfaceCategory = interfaceCategory;
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

    public String getSeriesCheck() {
        return _seriesCheck;
    }

    public void setSeriesCheck(String seriesCheck) {
        _seriesCheck = seriesCheck;
    }

    public boolean isSeriesCheckBool() {
        return _seriesCheckBool;
    }

    public void setSeriesCheckBool(boolean seriesCheckBool) {
        _seriesCheckBool = seriesCheckBool;
    }

    public String logInfo() {

        StringBuffer sbf = new StringBuffer(200);
        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        sbf.append(startSeperator);
        sbf.append("Network Code");
        sbf.append(middleSeperator);
        sbf.append(this.getNetworkCode());

        sbf.append(startSeperator);
        sbf.append("Service Type");
        sbf.append(middleSeperator);
        sbf.append(this.getServiceType());

        sbf.append(startSeperator);
        sbf.append("Interface Category");
        sbf.append(middleSeperator);
        sbf.append(this.getInterfaceCategory());

        sbf.append(startSeperator);
        sbf.append("Database Check");
        sbf.append(middleSeperator);
        sbf.append(this.getDatabaseCheck());

        sbf.append(startSeperator);
        sbf.append("Series Check");
        sbf.append(middleSeperator);
        sbf.append(this.getSeriesCheck());

        sbf.append("***********");
        return sbf.toString();
    }

    /**
     * 
     * @param p_subscriberRoutingControlVO
     * @return String
     */
    public String differences(SubscriberRoutingControlVO p_subscriberRoutingControlVO) {
        StringBuffer sbf = new StringBuffer(10);
        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        if (!BTSLUtil.compareLocaleString(this.getDatabaseCheck(), p_subscriberRoutingControlVO.getDatabaseCheck())) {
            sbf.append(startSeperator);
            sbf.append("Database Check");
            sbf.append(middleSeperator);
            sbf.append(p_subscriberRoutingControlVO.getDatabaseCheck());
            sbf.append(middleSeperator);
            sbf.append(this.getDatabaseCheck());
        }

        if (!BTSLUtil.compareLocaleString(this.getInterfaceCategory(), p_subscriberRoutingControlVO.getInterfaceCategory())) {
            sbf.append(startSeperator);
            sbf.append("Interface Category");
            sbf.append(middleSeperator);
            sbf.append(p_subscriberRoutingControlVO.getInterfaceCategory());
            sbf.append(middleSeperator);
            sbf.append(this.getInterfaceCategory());
        }

        if (!BTSLUtil.compareLocaleString(this.getNetworkCode(), p_subscriberRoutingControlVO.getNetworkCode())) {
            sbf.append(startSeperator);
            sbf.append("Network Code");
            sbf.append(middleSeperator);
            sbf.append(p_subscriberRoutingControlVO.getNetworkCode());
            sbf.append(middleSeperator);
            sbf.append(this.getNetworkCode());
        }
        if (!BTSLUtil.compareLocaleString(this.getSeriesCheck(), p_subscriberRoutingControlVO.getSeriesCheck())) {
            sbf.append(startSeperator);
            sbf.append("Series Check");
            sbf.append(middleSeperator);
            sbf.append(p_subscriberRoutingControlVO.getSeriesCheck());
            sbf.append(middleSeperator);
            sbf.append(this.getSeriesCheck());
        }
        if (!BTSLUtil.compareLocaleString(this.getServiceType(), p_subscriberRoutingControlVO.getServiceType())) {
            sbf.append(startSeperator);
            sbf.append("Service Type");
            sbf.append(middleSeperator);
            sbf.append(p_subscriberRoutingControlVO.getServiceType());
            sbf.append(middleSeperator);
            sbf.append(this.getServiceType());
        }
        return sbf.toString();
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("_networkCode  =" + _networkCode);
        sbf.append(",_serviceType  =" + _serviceType);
        sbf.append(",_interfaceCategory =" + _interfaceCategory);
        sbf.append(",_databaseCheck =" + _databaseCheck);
        sbf.append(",_seriesCheck =" + _seriesCheck);
        sbf.append(",_databaseCheckBool =" + _databaseCheckBool);
        sbf.append(",_seriesCheckBool =" + _seriesCheckBool);
        return sbf.toString();
    }
}
