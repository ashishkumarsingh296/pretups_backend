/*
 * SubscriberRoutingControlVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 30/10/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.routing.master.businesslogic;

import java.io.Serializable;

import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

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

        StringBuilder strBuild = new StringBuilder();
        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        strBuild.append(startSeperator);
        strBuild.append("Network Code");
        strBuild.append(middleSeperator);
        strBuild.append(this.getNetworkCode());

        strBuild.append(startSeperator);
        strBuild.append("Service Type");
        strBuild.append(middleSeperator);
        strBuild.append(this.getServiceType());

        strBuild.append(startSeperator);
        strBuild.append("Interface Category");
        strBuild.append(middleSeperator);
        strBuild.append(this.getInterfaceCategory());

        strBuild.append(startSeperator);
        strBuild.append("Database Check");
        strBuild.append(middleSeperator);
        strBuild.append(this.getDatabaseCheck());

        strBuild.append(startSeperator);
        strBuild.append("Series Check");
        strBuild.append(middleSeperator);
        strBuild.append(this.getSeriesCheck());

        strBuild.append("***********");
        return strBuild.toString();
    }

    /**
     * 
     * @param p_subscriberRoutingControlVO
     * @return String
     */
    public String differences(SubscriberRoutingControlVO p_subscriberRoutingControlVO) {
        StringBuilder strBuild = new StringBuilder();
        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        if (!BTSLUtil.compareLocaleString(this.getDatabaseCheck(), p_subscriberRoutingControlVO.getDatabaseCheck())) {
            strBuild.append(startSeperator);
            strBuild.append("Database Check");
            strBuild.append(middleSeperator);
            strBuild.append(p_subscriberRoutingControlVO.getDatabaseCheck());
            strBuild.append(middleSeperator);
            strBuild.append(this.getDatabaseCheck());
        }

        if (!BTSLUtil.compareLocaleString(this.getInterfaceCategory(), p_subscriberRoutingControlVO.getInterfaceCategory())) {
            strBuild.append(startSeperator);
            strBuild.append("Interface Category");
            strBuild.append(middleSeperator);
            strBuild.append(p_subscriberRoutingControlVO.getInterfaceCategory());
            strBuild.append(middleSeperator);
            strBuild.append(this.getInterfaceCategory());
        }

        if (!BTSLUtil.compareLocaleString(this.getNetworkCode(), p_subscriberRoutingControlVO.getNetworkCode())) {
            strBuild.append(startSeperator);
            strBuild.append("Network Code");
            strBuild.append(middleSeperator);
            strBuild.append(p_subscriberRoutingControlVO.getNetworkCode());
            strBuild.append(middleSeperator);
            strBuild.append(this.getNetworkCode());
        }
        if (!BTSLUtil.compareLocaleString(this.getSeriesCheck(), p_subscriberRoutingControlVO.getSeriesCheck())) {
            strBuild.append(startSeperator);
            strBuild.append("Series Check");
            strBuild.append(middleSeperator);
            strBuild.append(p_subscriberRoutingControlVO.getSeriesCheck());
            strBuild.append(middleSeperator);
            strBuild.append(this.getSeriesCheck());
        }
        if (!BTSLUtil.compareLocaleString(this.getServiceType(), p_subscriberRoutingControlVO.getServiceType())) {
            strBuild.append(startSeperator);
            strBuild.append("Service Type");
            strBuild.append(middleSeperator);
            strBuild.append(p_subscriberRoutingControlVO.getServiceType());
            strBuild.append(middleSeperator);
            strBuild.append(this.getServiceType());
        }
        return strBuild.toString();
    }

    public String toString() {
        StringBuilder strBuild = new StringBuilder();
        strBuild.append("_networkCode  =").append(_networkCode);
        strBuild.append(",_serviceType  =").append(_serviceType);
        strBuild.append(",_interfaceCategory =").append(_interfaceCategory);
        strBuild.append(",_databaseCheck =").append(_databaseCheck);
        strBuild.append(",_seriesCheck =").append(_seriesCheck);
        strBuild.append(",_databaseCheckBool =").append(_databaseCheckBool);
        strBuild.append(",_seriesCheckBool =").append(_seriesCheckBool);
        return strBuild.toString();
    }
}
