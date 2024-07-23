package com.btsl.loadcontroller;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class NetworkServiceHourlyLoadVO implements Serializable, Comparable {
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
    private String _lastRequestID = null;
    private Timestamp _lastReceievedTime = null;
    private Date _lastInitializationTime = null;
    private long _otherNetworkReqCount = 0;
    private String _tempKey = null;

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
     * @return Returns the lastReceievedTime.
     */
    public Timestamp getLastReceievedTime() {
        return  _lastReceievedTime;
    }

    /**
     * @param lastReceievedTime
     *            The lastReceievedTime to set.
     */
    public void setLastReceievedTime(Timestamp lastReceievedTime) {
        this._lastReceievedTime = lastReceievedTime;
    }

    /**
     * @return Returns the lastInitializationTime.
     */
    public Date getLastInitializationTime() {
        return  _lastInitializationTime;
    }

    /**
     * @param lastInitializationTime
     *            The lastInitializationTime to set.
     */
    public void setLastInitializationTime(Date lastInitializationTime) {
        this._lastInitializationTime = lastInitializationTime;
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

    public String toString() {
        StringBuilder sbd = new StringBuilder();
        sbd.append("Instance ID=").append(_instanceID).append(" Network Code=").append(_networkCode).append(" _moduleCode=").append(_moduleCode).append(" _gatewayType=").append(_gatewayType).append(" Service Type=").append(_serviceType).append(" _seqNo=").append(_seqNo).append(" _recievedCount=").append(_recievedCount).append(" _successCount=").append(_successCount).append(" _failCount=").append(_failCount).append(" _otherNetworkReqCount=").append(_otherNetworkReqCount);
        return sbd.toString();

    }

    public int compareTo(Object arg0) {
        // TODO Auto-generated method stub
        NetworkServiceHourlyLoadVO obj = (NetworkServiceHourlyLoadVO) arg0;
        int i = 0;
        int newSeqNo = 0;
        if ((i = this.getInstanceID().compareTo(obj.getInstanceID())) == 0) {
            if ((i = this.getNetworkCode().compareTo(obj.getNetworkCode())) == 0) {

                if ((i = this.getModuleCode().compareTo(obj.getModuleCode())) == 0) {
                    newSeqNo = this.getSeqNo();

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

    public long getRecievedCount() {
        return _recievedCount;
    }

    public void setRecievedCount(long recievedCount) {
        _recievedCount = recievedCount;
    }

    public String getTempKey() {
        return _tempKey;
    }

    public void setTempKey(String tempKey) {
        _tempKey = tempKey;
    }

}
