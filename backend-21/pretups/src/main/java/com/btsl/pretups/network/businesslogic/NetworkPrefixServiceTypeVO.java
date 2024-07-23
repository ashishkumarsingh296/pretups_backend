/*
 * Created on Apr 23, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.network.businesslogic;

import java.util.Date;

import com.btsl.util.Constants;

/**
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class NetworkPrefixServiceTypeVO {

    private String _serviceType;
    private String _prefixID;
    private String _networkCode;
    private String _handlerClass;
    private String _series;
    // This fiels id to store service name with service type in bracket, e.g.
    // Recharge(RC).
    private String _serviceNameType;
    private String _seriesType;
    private String _createdBy;
    private Date _createdOn;
    private String _modifiedBy;
    private Date _modifiedOn;

    public NetworkPrefixServiceTypeVO() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void setServiceType(String p_serviceType) {
        _serviceType = p_serviceType;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setPrefixID(String p_prefixID) {
        _prefixID = p_prefixID;
    }

    public String getPrefixID() {
        return _prefixID;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String p_networkCode) {
        _networkCode = p_networkCode;
    }

    public String getHandlerClass() {
        return _handlerClass;
    }

    public void setHandlerClass(String p_handlerClass) {
        _handlerClass = p_handlerClass;
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
        sbf.append("Prefix Id");
        sbf.append(middleSeperator);
        sbf.append(this.getPrefixID());

        sbf.append(startSeperator);
        sbf.append("Service Type");
        sbf.append(middleSeperator);
        sbf.append(this.getServiceType());

        sbf.append(startSeperator);
        sbf.append("Handler Class");
        sbf.append(middleSeperator);
        sbf.append(this.getHandlerClass());

        return sbf.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("networkCode=" + _networkCode + ",");
        sb.append("prefixID=" + _prefixID + ",");
        sb.append("serviceType=" + _serviceType + ",");
        sb.append("handlerClass=" + _handlerClass + ",");
        return sb.toString();
    }

    /**
     * @return Returns the series.
     */
    public String getSeries() {
        return _series;
    }

    /**
     * @param series
     *            The series to set.
     */
    public void setSeries(String series) {
        _series = series;
        if (_series != null) {
            _series = series.trim();
        }
    }

    /**
     * @return Returns the serviceNameType.
     */
    public String getServiceNameType() {
        return _serviceNameType;
    }

    /**
     * @param serviceNameType
     *            The serviceNameType to set.
     */
    public void setServiceNameType(String serviceNameType) {
        _serviceNameType = serviceNameType;
    }

    /**
     * @return Returns the seriesType.
     */
    public String getSeriesType() {
        return _seriesType;
    }

    /**
     * @param seriesType
     *            The seriesType to set.
     */
    public void setSeriesType(String seriesType) {
        _seriesType = seriesType;
    }

    /**
     * @return Returns the createdBy.
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @param createdBy
     *            The createdBy to set.
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * @return Returns the modifiedBy.
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * @param modifiedBy
     *            The modifiedBy to set.
     */
    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    /**
     * @return Returns the createdOn.
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @param createdOn
     *            The createdOn to set.
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * @return Returns the modifiedOn.
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * @param modifiedOn
     *            The modifiedOn to set.
     */
    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }
}
