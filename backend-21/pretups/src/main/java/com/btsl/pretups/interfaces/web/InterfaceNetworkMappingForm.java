package com.btsl.pretups.interfaces.web;

import java.util.ArrayList;
import java.util.HashMap;

//import org.apache.struts.action.ActionErrors;
//import org.apache.struts.action.ActionMapping;
//import org.apache.struts.validator.ValidatorActionForm;

import com.btsl.pretups.interfaces.businesslogic.InterfaceNetworkMappingVO;

/**
 * @(#)InterfaceNetworkMappingVO.java
 *                                    Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                    All Rights Reserved
 * 
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Author Date History
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Mohit Goel 21/09/2005 Initial Creation
 * 
 *                                    This class is used for Interface Network
 *                                    Mapping
 * 
 */

public class InterfaceNetworkMappingForm /*extends ValidatorActionForm*/ {

    private String _requestType = null;
    private String _code;// selected radio button value

    private String _networkCode = null;
    private String _interfaceName = null;
    private ArrayList _interfaceCategoryList;
    private String _interfaceCategoryID = null;
    private String _interfaceCategoryIDDesc = null;
    private ArrayList _interfaceIDList;
    private String _interfaceID = null;
    private String _interfaceIDDesc = null;
    private String _queueSize = null;
    private String _queueTimeOut = null;
    private String _requestTimeOut = null;
    private String _nextCheckQueueReqSec = null;

    private ArrayList _dataList;

    private long _lastModifiedOn;

    // defined for associatedInterfacePrefix.jsp
    private String _networkName;
    private String _prepaidSeries;
    private String _postpaidSeries;
    private ArrayList _interfaceList;
    private HashMap _seriesMap;// this map contains all series, used during save
                               // to fetch the prefix_id

    public void flush() {
        _requestType = null;
        _code = null;
        _networkCode = null;
        _interfaceName = null;
        _interfaceCategoryList = null;
        _interfaceCategoryID = null;
        _interfaceCategoryIDDesc = null;
        _interfaceIDList = null;
        _interfaceID = null;
        _interfaceIDDesc = null;
        _queueSize = null;
        _queueTimeOut = null;
        _requestTimeOut = null;
        _nextCheckQueueReqSec = null;

        _dataList = null;

        _lastModifiedOn = 0;
    }

    public void semiFlush() {
        _interfaceCategoryList = null;
        _interfaceCategoryID = null;
        _interfaceCategoryIDDesc = null;
        _interfaceIDList = null;
        _interfaceID = null;
        _interfaceIDDesc = null;
        _queueSize = null;
        _queueTimeOut = null;
        _requestTimeOut = null;
        _nextCheckQueueReqSec = null;
    }

    public void flushSeries() {
        _prepaidSeries = null;
        _postpaidSeries = null;
        _networkName = null;
        _interfaceList = null;
        _seriesMap = null;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("InterfaceNetworkMappingVO Data ");

        sb.append("_networkCode=" + _networkCode + ",");
        sb.append("_interfaceCategoryID=" + _interfaceCategoryID + ",");
        sb.append("_interfaceID=" + _interfaceID + ",");
        sb.append("_queueSize=" + _queueSize + ",");
        sb.append("_queueTimeOut=" + _queueTimeOut + ",");
        sb.append("_requestTimeOut=" + _requestTimeOut + ",");
        sb.append("nextCheckQueueReqSec=" + _nextCheckQueueReqSec + ",");
        sb.append("_lastModifiedOn=" + _lastModifiedOn + ",");

        return sb.toString();
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
     * @return Returns the interfaceCategoryID.
     */
    public String getInterfaceCategoryID() {
        return _interfaceCategoryID;
    }

    /**
     * @param interfaceCategoryID
     *            The interfaceCategoryID to set.
     */
    public void setInterfaceCategoryID(String interfaceCategoryID) {
        if (interfaceCategoryID != null) {
            _interfaceCategoryID = interfaceCategoryID.trim();
        }
    }

    /**
     * @return Returns the interfaceCategoryIDDesc.
     */
    public String getInterfaceCategoryIDDesc() {
        return _interfaceCategoryIDDesc;
    }

    /**
     * @param interfaceCategoryIDDesc
     *            The interfaceCategoryIDDesc to set.
     */
    public void setInterfaceCategoryIDDesc(String interfaceCategoryIDDesc) {
        if (interfaceCategoryIDDesc != null) {
            _interfaceCategoryIDDesc = interfaceCategoryIDDesc.trim();
        }
    }

    /**
     * @return Returns the interfaceCategoryList.
     */
    public ArrayList getInterfaceCategoryList() {
        return _interfaceCategoryList;
    }

    /**
     * @param interfaceCategoryList
     *            The interfaceCategoryList to set.
     */
    public void setInterfaceCategoryList(ArrayList interfaceCategoryList) {
        _interfaceCategoryList = interfaceCategoryList;
    }

    /**
     * @return Returns the interfaceID.
     */
    public String getInterfaceID() {
        return _interfaceID;
    }

    /**
     * @param interfaceID
     *            The interfaceID to set.
     */
    public void setInterfaceID(String interfaceID) {
        if (interfaceID != null) {
            _interfaceID = interfaceID.trim();
        }
    }

    /**
     * @return Returns the interfaceIDDesc.
     */
    public String getInterfaceIDDesc() {
        return _interfaceIDDesc;
    }

    /**
     * @param interfaceIDDesc
     *            The interfaceIDDesc to set.
     */
    public void setInterfaceIDDesc(String interfaceIDDesc) {
        if (interfaceIDDesc != null) {
            _interfaceIDDesc = interfaceIDDesc.trim();
        }
    }

    /**
     * @return Returns the interfaceIDList.
     */
    public ArrayList getInterfaceIDList() {
        return _interfaceIDList;
    }

    /**
     * @param interfaceIDList
     *            The interfaceIDList to set.
     */
    public void setInterfaceIDList(ArrayList interfaceIDList) {
        _interfaceIDList = interfaceIDList;
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
        if (networkCode != null) {
            _networkCode = networkCode.trim();
        }
    }

    /**
     * @return Returns the networkName.
     */
    public String getInterfaceName() {
        return _interfaceName;
    }

    /**
     * @param networkName
     *            The networkName to set.
     */
    public void setInterfaceName(String networkName) {
        if (networkName != null) {
            _interfaceName = networkName.trim();
        }
    }

    /**
     * @return Returns the nextCheckQueueReqSec.
     */
    public String getNextCheckQueueReqSec() {
        return _nextCheckQueueReqSec;
    }

    /**
     * @param nextCheckQueueReqSec
     *            The nextCheckQueueReqSec to set.
     */
    public void setNextCheckQueueReqSec(String nextCheckQueueReqSec) {
        if (nextCheckQueueReqSec != null) {
            _nextCheckQueueReqSec = nextCheckQueueReqSec.trim();
        }
    }

    /**
     * @return Returns the queueSize.
     */
    public String getQueueSize() {
        return _queueSize;
    }

    /**
     * @param queueSize
     *            The queueSize to set.
     */
    public void setQueueSize(String queueSize) {
        if (queueSize != null) {
            _queueSize = queueSize.trim();
        }
    }

    /**
     * @return Returns the queueTimeOut.
     */
    public String getQueueTimeOut() {
        return _queueTimeOut;
    }

    /**
     * @param queueTimeOut
     *            The queueTimeOut to set.
     */
    public void setQueueTimeOut(String queueTimeOut) {
        if (queueTimeOut != null) {
            _queueTimeOut = queueTimeOut.trim();
        }
    }

    /**
     * @return Returns the requestTimeOut.
     */
    public String getRequestTimeOut() {
        return _requestTimeOut;
    }

    /**
     * @param requestTimeOut
     *            The requestTimeOut to set.
     */
    public void setRequestTimeOut(String requestTimeOut) {
        if (requestTimeOut != null) {
            _requestTimeOut = requestTimeOut.trim();
        }
    }

    /**
     * @return Returns the requestType.
     */
    public String getRequestType() {
        return _requestType;
    }

    /**
     * @param requestType
     *            The requestType to set.
     */
    public void setRequestType(String requestType) {
        if (requestType != null) {
            _requestType = requestType.trim();
        }
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

    public int getResultCount() {
        if (_dataList != null && !_dataList.isEmpty()) {
            return _dataList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the code.
     */
    public String getCode() {
        return _code;
    }

    /**
     * @param code
     *            The code to set.
     */
    public void setCode(String code) {
        _code = code;
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
     * @return Returns the interfaceList.
     */
    public ArrayList getInterfaceList() {
        return _interfaceList;
    }

    /**
     * @param interfaceList
     *            The interfaceList to set.
     */
    public void setInterfaceList(ArrayList interfaceList) {
        _interfaceList = interfaceList;
    }

    public int getInterfaceListCount() {
        if (_interfaceList != null && !_interfaceList.isEmpty()) {
            return _interfaceList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the InterfaceNetworkMappingVO.
     */
    public InterfaceNetworkMappingVO getInterfaceListIndexed(int index) {
        return (InterfaceNetworkMappingVO) _interfaceList.get(index);
    }

    /**
     * @param interfaceList
     *            The interfaceList to set.
     */
    public void setInterfaceListIndexed(int index, InterfaceNetworkMappingVO interfaceNetworkMappingVO) {
        _interfaceList.set(index, interfaceNetworkMappingVO);
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
}
