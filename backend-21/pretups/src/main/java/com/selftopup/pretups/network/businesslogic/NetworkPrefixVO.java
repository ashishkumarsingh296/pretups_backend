package com.selftopup.pretups.network.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

/**
 * @(#)NetworkPrefixVO.java
 *                          Copyright(c) 2005, Bharti Telesoft Ltd.
 *                          All Rights Reserved
 * 
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Mohit Goel 14/06/2005 Initial Creation
 * 
 *                          This class is used for store the values coming from
 *                          the database
 * 
 */
public class NetworkPrefixVO extends NetworkVO implements Serializable {

    private long _prefixID;
    private String _series;
    private String _operator;
    private String _seriesType;
    private Date _modifiedOn;
    private Timestamp _modifiedTimeStamp;
    private String _status;
    private String _createdBy;
    private Date _createdOn;
    private String _modifiedBy;
    private String dbFlag;

    public Timestamp getModifiedTimeStamp() {
        return _modifiedTimeStamp;
    }

    public void setModifiedTimeStamp(Timestamp modifiedTimeStamp) {
        _modifiedTimeStamp = modifiedTimeStamp;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date on) {
        _modifiedOn = on;
    }

    /**
     * @return Returns the operator.
     */
    public String getOperator() {
        return _operator;
    }

    /**
     * @param operator
     *            The operator to set.
     */
    public void setOperator(String operator) {
        _operator = operator;
    }

    /**
     * @return Returns the prefixID.
     */
    public long getPrefixID() {
        return _prefixID;
    }

    /**
     * @param prefixID
     *            The prefixID to set.
     */
    public void setPrefixId(long prefixID) {
        _prefixID = prefixID;
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
     * @return Returns the status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * @return Returns the dbFlag.
     */
    public String getDbFlag() {
        return dbFlag;
    }

    /**
     * @param dbFlag
     *            The dbFlag to set.
     */
    public void setDbFlag(String dbFlag) {
        this.dbFlag = dbFlag;
    }

    /**
     * @param prefixID
     *            The prefixID to set.
     */
    public void setPrefixID(long prefixID) {
        _prefixID = prefixID;
    }

    public boolean equals(NetworkPrefixVO networkPrefixVO) {
        boolean flag = false;

        if (this.getModifiedTimeStamp().equals(networkPrefixVO.getModifiedTimeStamp())) {
            flag = true;
        }
        return flag;
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
        sbf.append("Series Type");
        sbf.append(middleSeperator);
        sbf.append(this.getSeriesType());

        return sbf.toString();
    }

    public String diffrences(NetworkPrefixVO networkPrefixVO) {

        StringBuffer sbf = new StringBuffer(200);

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        if (!BTSLUtil.isNullString(this.getNetworkCode()) && this.getNetworkCode().equals(networkPrefixVO.getNetworkCode())) {
            sbf.append(startSeperator);
            sbf.append("Network Code");
            sbf.append(middleSeperator);
            sbf.append(networkPrefixVO.getNetworkCode());
            sbf.append(middleSeperator);
            sbf.append(this.getNetworkCode());
        }

        if (!BTSLUtil.isNullString(this.getSeriesType()) && this.getSeriesType().equals(networkPrefixVO.getSeriesType())) {
            sbf.append(startSeperator);
            sbf.append("Series Type");
            sbf.append(middleSeperator);
            sbf.append(networkPrefixVO.getSeriesType());
            sbf.append(middleSeperator);
            sbf.append(this.getSeriesType());
        }

        return sbf.toString();
    }

}
