package com.btsl.loadcontroller;

/*
 * NetworkServiceLoadVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 01/02/2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * VO for displaying counters in the network and service type wise
 */

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class NetworkServiceLoadVO implements Serializable, Comparable {
    private String _instanceID = null;
    private String _networkCode = null;
    private String _moduleCode = null;
    private String _gatewayType = null;
    private String _serviceType = null;
    private String _serviceName = null;
    private int _seqNo = 0;
    private long _recievedCount = 0;
    private long _successCount = 0;
    private long _failCount = 0;
    private long _underProcessCount = 0;
    private long _othersFailCount = 0;
    private long _otherNetworkReqCount = 0;
    private Timestamp _lastReceievedTime = null;
    private Date _lastInitializationTime = null;
    private double _averageServiceTime = 0;
    private long _lastRequestServiceTime = 0;
    private long _beforeGatewayFoundError = 0;
    private long _beforeNetworkFoundError = 0;
    private long _beforeServiceTypeFoundError = 0;
    private String _lastRequestID = null;

    public String toString() {

        StringBuilder sbd = new StringBuilder();
        sbd.append("Instance ID=").append(_instanceID).append(" Network Code=").append(_networkCode).append(" _moduleCode=").append(_moduleCode).append(" _gatewayType=").append(_gatewayType).append(" Service Type=").append(_serviceType).append(" _seqNo=").append(_seqNo).append(" _recievedCount=").append(_recievedCount).append(" _successCount=").append(_successCount).append(" _failCount=").append(_failCount).append(" _underProcessCount=").append(_underProcessCount).append(" _othersFailCount=").append(_othersFailCount).append(" _otherNetworkReqCount=").append(_otherNetworkReqCount).append(" _lastRequestID=").append(_lastRequestID);
        return sbd.toString();

    }

    /**
     * @return Returns the beforeGatewayFoundError.
     */
    public long getBeforeGatewayFoundError() {
        return _beforeGatewayFoundError;
    }

    /**
     * @param beforeGatewayFoundError
     *            The beforeGatewayFoundError to set.
     */
    public void setBeforeGatewayFoundError(long beforeGatewayFoundError) {
        _beforeGatewayFoundError = beforeGatewayFoundError;
    }

    /**
     * @return Returns the beforeNetworkFoundError.
     */
    public long getBeforeNetworkFoundError() {
        return _beforeNetworkFoundError;
    }

    /**
     * @param beforeNetworkFoundError
     *            The beforeNetworkFoundError to set.
     */
    public void setBeforeNetworkFoundError(long beforeNetworkFoundError) {
        _beforeNetworkFoundError = beforeNetworkFoundError;
    }

    /**
     * @return Returns the beforeServiceTypeFoundError.
     */
    public long getBeforeServiceTypeFoundError() {
        return _beforeServiceTypeFoundError;
    }

    /**
     * @param beforeServiceTypeFoundError
     *            The beforeServiceTypeFoundError to set.
     */
    public void setBeforeServiceTypeFoundError(long beforeServiceTypeFoundError) {
        _beforeServiceTypeFoundError = beforeServiceTypeFoundError;
    }

    /**
     * @return Returns the failCount.
     */
    public long getFailCount() {
        return _failCount;
    }

    /**
     * @param failCount
     *            The failCount to set.
     */
    public void setFailCount(long failCount) {
        _failCount = failCount;
    }

    /**
     * @return Returns the gatewayType.
     */
    public String getGatewayType() {
        return _gatewayType;
    }

    /**
     * @param gatewayType
     *            The gatewayType to set.
     */
    public void setGatewayType(String gatewayType) {
        _gatewayType = gatewayType;
    }

    /**
     * @return Returns the lastInitializationTime.
     */
    public Date getLastInitializationTime() {
        return _lastInitializationTime;
    }

    /**
     * @param lastInitializationTime
     *            The lastInitializationTime to set.
     */
    public void setLastInitializationTime(Date lastInitializationTime) {
        this._lastInitializationTime = lastInitializationTime;
    }

    /**
     * @return Returns the lastReceievedTime.
     */
    public Timestamp getLastReceievedTime() {
        return _lastReceievedTime;
    }

    /**
     * @param lastReceievedTime
     *            The lastReceievedTime to set.
     */
    public void setLastReceievedTime(Timestamp lastReceievedTime) {
        this._lastReceievedTime = lastReceievedTime;
    }

    /**
     * @return Returns the lastRequestServiceTime.
     */
    public long getLastRequestServiceTime() {
        return _lastRequestServiceTime;
    }

    /**
     * @param lastRequestServiceTime
     *            The lastRequestServiceTime to set.
     */
    public void setLastRequestServiceTime(long lastRequestServiceTime) {
        _lastRequestServiceTime = lastRequestServiceTime;
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
     * @return Returns the otherNetworkReqCount.
     */
    public long getOtherNetworkReqCount() {
        return _otherNetworkReqCount;
    }

    /**
     * @param otherNetworkReqCount
     *            The otherNetworkReqCount to set.
     */
    public void setOtherNetworkReqCount(long otherNetworkReqCount) {
        _otherNetworkReqCount = otherNetworkReqCount;
    }

    /**
     * @return Returns the recievedCount.
     */
    public long getRecievedCount() {
        return _recievedCount;
    }

    /**
     * @param recievedCount
     *            The recievedCount to set.
     */
    public void setRecievedCount(long recievedCount) {
        _recievedCount = recievedCount;
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
     * @return Returns the successCount.
     */
    public long getSuccessCount() {
        return _successCount;
    }

    /**
     * @param successCount
     *            The successCount to set.
     */
    public void setSuccessCount(long successCount) {
        _successCount = successCount;
    }

    /**
     * @return Returns the underProcessCount.
     */
    public long getUnderProcessCount() {
        return _underProcessCount;
    }

    /**
     * @param underProcessCount
     *            The underProcessCount to set.
     */
    public void setUnderProcessCount(long underProcessCount) {
        _underProcessCount = underProcessCount;
    }

    /**
     * @return Returns the instanceID.
     */
    public String getInstanceID() {
        return _instanceID;
    }

    /**
     * @param instanceID
     *            The instanceID to set.
     */
    public void setInstanceID(String instanceID) {
        _instanceID = instanceID;
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

    /**
     * @return Returns the othersFailCount.
     */
    public long getOthersFailCount() {
        return _othersFailCount;
    }

    /**
     * @param othersFailCount
     *            The othersFailCount to set.
     */
    public void setOthersFailCount(long othersFailCount) {
        _othersFailCount = othersFailCount;
    }

    /**
     * @return Returns the lastRequestID.
     */
    public String getLastRequestID() {
        return _lastRequestID;
    }

    /**
     * @param lastRequestID
     *            The lastRequestID to set.
     */
    public void setLastRequestID(String lastRequestID) {
        _lastRequestID = lastRequestID;
    }

    /**
     * @return Returns the averageServiceTime.
     */
    public double getAverageServiceTime() {
        return _averageServiceTime;
    }

    /**
     * @param averageServiceTime
     *            The averageServiceTime to set.
     */
    public void setAverageServiceTime(double averageServiceTime) {
        _averageServiceTime = averageServiceTime;
    }

    public int compareTo(Object arg0) {
        // TODO Auto-generated method stub
        NetworkServiceLoadVO obj = (NetworkServiceLoadVO) arg0;
        int i = 0;
        int newSeqNo = 0;
        if ((i = this.getInstanceID().compareTo(obj.getInstanceID())) == 0) {
            if ((i = this.getNetworkCode().compareTo(obj.getNetworkCode())) == 0) {

                if ((i = this.getModuleCode().compareTo(obj.getModuleCode())) == 0) {
                    newSeqNo = this.getSeqNo();
                    // System.out.println("newSeqNo: "+newSeqNo);
                    if ((i = (newSeqNo - obj.getSeqNo())) == 0) {
                        if ((i = this.getServiceType().compareTo(obj.getServiceType())) == 0) {
                            if ((i = this.getGatewayType().compareTo(obj.getGatewayType())) == 0) {
                                return i;
                            } else {
                                return i;
                            }
                        } else {
                            return i;
                        }
                    } else {
                        return i;
                    }
                } else {
                    return i;
                }
            } else {
                return i;
            }
        } else {
            return i;
        }

    }

    public String getModuleCode() {
        return _moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        _moduleCode = moduleCode;
    }

    public int getSeqNo() {
        return _seqNo;
    }

    public void setSeqNo(int seqNo) {
        _seqNo = seqNo;
    }

}
