package com.btsl.pretups.interfaces.web;

/**
 * @(#)ServicePrefixMappingForm.java
 *                                   Copyright(c) 2009, Comviva Technologies
 *                                   Ltd.
 *                                   All Rights Reserved
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Author Date History
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Vinay kumar Singh 12/10/2009 Initial
 *                                   Creation
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   ------------
 *                                   This class is used for Service Network
 *                                   Prefix Mapping
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import jakarta.servlet.http.HttpServletRequest;

/*import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorActionForm;*/

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkPrefixServiceTypeVO;
import com.btsl.util.BTSLUtil;

public class ServicePrefixMappingForm /*extends ValidatorActionForm*/ {
    private String _serviceType;
    private String _serviceName;
    private String _networkCode;
    private ArrayList _dataList;
    private long _lastModifiedOn;
    private String _networkName;
    // private String _seriesValues;
    private String _prepaidSeries;
    private String _postpaidSeries;
    private ArrayList _serviceTypeList;
    private ArrayList _servicePrefixList;
    private HashMap _seriesMap;// this map contains all series, used during save
                               // to fetch the prefix_id

    public void flush() {
        _serviceType = null;
        _serviceName = null;
        _networkCode = null;
        _serviceTypeList = null;
        _servicePrefixList = null;
        _dataList = null;
        _lastModifiedOn = 0;
        _networkName = null;
        _prepaidSeries = null;
        _postpaidSeries = null;
        _seriesMap = null;
    }

    public void flushSeries() {
        _prepaidSeries = null;
        _postpaidSeries = null;
        _networkName = null;
        _seriesMap = null;
    }


    /**
     * @return Returns the dataList.
     */
    public ArrayList getDataList() {
        return _dataList;
    }

    /**
     * @param dataList
     *            The dataList to set.
     */
    public void setDataList(ArrayList dataList) {
        _dataList = dataList;
    }

    /**
     * @return Returns the lastModifiedOn.
     */
    public long getLastModifiedOn() {
        return _lastModifiedOn;
    }

    /**
     * @param lastModifiedOn
     *            The lastModifiedOn to set.
     */
    public void setLastModifiedOn(long lastModifiedOn) {
        _lastModifiedOn = lastModifiedOn;
    }

    /**
     * @return Returns the networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param networkCode
     *            The networkCode to set.
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * @return Returns the networkName.
     */
    public String getNetworkName() {
        return _networkName;
    }

    /**
     * @param networkName
     *            The networkName to set.
     */
    public void setNetworkName(String networkName) {
        _networkName = networkName;
    }

    /**
     * @return Returns the postpaidSeries.
     */
    public String getPostpaidSeries() {
        return _postpaidSeries;
    }

    /**
     * @param postpaidSeries
     *            The postpaidSeries to set.
     */
    public void setPostpaidSeries(String postpaidSeries) {
        _postpaidSeries = postpaidSeries;
    }

    /**
     * @return Returns the prepaidSeries.
     */
    public String getPrepaidSeries() {
        return _prepaidSeries;
    }

    /**
     * @param prepaidSeries
     *            The prepaidSeries to set.
     */
    public void setPrepaidSeries(String prepaidSeries) {
        _prepaidSeries = prepaidSeries;
    }

    /**
     * @return Returns the seriesMap.
     */
    public HashMap getSeriesMap() {
        return _seriesMap;
    }

    /**
     * @param seriesMap
     *            The seriesMap to set.
     */
    public void setSeriesMap(HashMap seriesMap) {
        _seriesMap = seriesMap;
    }

    /**
     * @return Returns the serviceType.
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * @param serviceType
     *            The serviceType to set.
     */
    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    /**
     * @return Returns the serviceTypeList.
     */
    public ArrayList getServiceTypeList() {
        return _serviceTypeList;
    }

    /**
     * @param serviceTypeList
     *            The serviceTypeList to set.
     */
    public void setServiceTypeList(ArrayList serviceTypeList) {
        _serviceTypeList = serviceTypeList;
    }

    /**
     * @return Returns the serviceName.
     */
    public String getServiceName() {
        return _serviceName;
    }

    /**
     * @param serviceName
     *            The serviceName to set.
     */
    public void setServiceName(String serviceName) {
        _serviceName = serviceName;
    }

    public int getServiceListCount() {
        if (_servicePrefixList != null && !_servicePrefixList.isEmpty()) {
            return _servicePrefixList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the NetworkPrefixServiceTypeVO.
     */
    public NetworkPrefixServiceTypeVO getServicePrefixListIndexed(int index) {
        return (NetworkPrefixServiceTypeVO) _servicePrefixList.get(index);
    }

    /**
     * @param serviceTypeList
     *            The serviceTypeList to set.
     */
    public void setServicePrefixListIndexed(int index, NetworkPrefixServiceTypeVO servicePrefixMappingVO) {
        _servicePrefixList.set(index, servicePrefixMappingVO);
    }

    /**
     * @return Returns the servicePrefixList.
     */
    public ArrayList getServicePrefixList() {
        return _servicePrefixList;
    }

    /**
     * @param servicePrefixList
     *            The servicePrefixList to set.
     */
    public void setServicePrefixList(ArrayList servicePrefixList) {
        _servicePrefixList = servicePrefixList;
    }
}
