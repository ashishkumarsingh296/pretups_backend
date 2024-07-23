package com.btsl.pretups.product.businesslogic;

/**
 * @(#)
 *      Copyright(c) 2005, Bharti Telesoft Ltd.
 *      All Rights Reserved
 *      NetworkProductVO.java
 *      ------------------------------------------------------------------------
 *      -------------------------
 *      Author Date History
 *      ------------------------------------------------------------------------
 *      -------------------------
 *      manoj kumar 26/07/2005 Initial Creation
 * 
 *      This class holds the values coming from the DB
 * 
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class NetworkProductVO extends ProductVO implements Serializable {

    private String _networkCode;
    private String _usage;
    private String productUsageName;
    private String status;
    private String _language1Message;
    private String _language2Message;

    private String _createdBy;
    private String _modifiedBy;
    private Date _createdOn;
    private Date _modifiedOn;
    private long _lastModifiedTime;
    private ArrayList _dataList;

    private String _alertingBalance = "";

    /**
     * @return Returns the productUsageName.
     */
    public String getProductUsageName() {
        return productUsageName;
    }

    /**
     * @param productUsageName
     *            The productUsageName to set.
     */
    public void setProductUsageName(String productUsageName) {
        this.productUsageName = productUsageName;
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
     * @return Returns the lastModifiedTime.
     */
    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    /**
     * @param lastModifiedTime
     *            The lastModifiedTime to set.
     */
    public void setLastModifiedTime(long lastModifiedTime) {
        _lastModifiedTime = lastModifiedTime;
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

    public String getUsage() {
        return _usage;
    }

    /**
     * @param usage
     *            The usage to set.
     */
    public void setUsage(String usage) {
        _usage = usage;
    }

    /**
     * @return Returns the languageOneMessage.
     */
    public String getLanguage1Message() {
        return _language1Message;
    }

    /**
     * @param languageOneMessage
     *            The languageOneMessage to set.
     */
    public void setLanguage1Message(String language1Message) {
        _language1Message = language1Message;
    }

    /**
     * @return Returns the languageTwoMessage.
     */
    public String getLanguage2Message() {
        return _language2Message;
    }

    /**
     * @param languageTwoMessage
     *            The languageTwoMessage to set.
     */
    public void setLanguage2Message(String language2Message) {
        _language2Message = language2Message;
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(String status) {
        this.status = status;
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

    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append("NetworkCode=" + _networkCode + ",");
        sb.append("Usage=" + _usage + ",");
        sb.append("Status=" + status + ",");
        sb.append("Language1Message=" + _language1Message + ",");
        sb.append("Language2Message=" + _language2Message + ",");
        sb.append("CreatedBy=" + _createdBy + ",");
        sb.append("ModifiedBy=" + _modifiedBy + ",");
        sb.append("CreatedOn=" + _createdOn + ",");
        sb.append("ModifiedOn=" + _modifiedOn + ",");
        sb.append("LastModifiedtime=" + _lastModifiedTime);
        sb.append("AlertingBalance=" + _alertingBalance);
        return sb.toString();
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
        sbf.append("Product Code");
        sbf.append(middleSeperator);
        sbf.append(this.getProductCode());

        sbf.append(startSeperator);
        sbf.append("Product Name");
        sbf.append(middleSeperator);
        sbf.append(this.getProductName());

        sbf.append(startSeperator);
        sbf.append("Usage");
        sbf.append(middleSeperator);
        sbf.append(this.getUsage());

        sbf.append(startSeperator);
        sbf.append("Status");
        sbf.append(middleSeperator);
        sbf.append(this.getStatus());

        sbf.append(startSeperator);
        sbf.append("Language 1 Message");
        sbf.append(middleSeperator);
        sbf.append(this.getLanguage1Message());

        sbf.append(startSeperator);
        sbf.append("Language 2 Message");
        sbf.append(middleSeperator);
        sbf.append(this.getLanguage2Message());

        sbf.append(startSeperator);
        sbf.append("Alerting Balance");
        sbf.append(middleSeperator);
        sbf.append(this.getAlertingBalance());

        return sbf.toString();
    }

    public String differences(NetworkProductVO p_networkPorductVO) {
        StringBuffer sbf = new StringBuffer(10);
        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        if (this.getProductName() != null && p_networkPorductVO.getProductName() != null && !BTSLUtil.compareLocaleString(this.getProductName(), p_networkPorductVO.getProductName())) {
            sbf.append(startSeperator);
            sbf.append("Product Name");
            sbf.append(middleSeperator);
            sbf.append(p_networkPorductVO.getProductName());
            sbf.append(middleSeperator);
            sbf.append(this.getProductName());
        }

        if (this.getUsage() != null && p_networkPorductVO.getUsage() != null && !BTSLUtil.compareLocaleString(this.getUsage(), p_networkPorductVO.getUsage())) {
            sbf.append(startSeperator);
            sbf.append("Usage");
            sbf.append(middleSeperator);
            sbf.append(p_networkPorductVO.getUsage());
            sbf.append(middleSeperator);
            sbf.append(this.getUsage());
        }

        if (this.getStatus() != null && p_networkPorductVO.getStatus() != null && !BTSLUtil.compareLocaleString(this.getStatus(), p_networkPorductVO.getStatus())) {
            sbf.append(startSeperator);
            sbf.append("Status");
            sbf.append(middleSeperator);
            sbf.append(p_networkPorductVO.getStatus());
            sbf.append(middleSeperator);
            sbf.append(this.getStatus());
        }
        if (this.getLanguage1Message() != null && p_networkPorductVO.getLanguage1Message() != null && !BTSLUtil.compareLocaleString(this.getLanguage1Message(), p_networkPorductVO.getLanguage1Message())) {
            sbf.append(startSeperator);
            sbf.append("Language 1 Message");
            sbf.append(middleSeperator);
            sbf.append(p_networkPorductVO.getLanguage1Message());
            sbf.append(middleSeperator);
            sbf.append(this.getLanguage1Message());
        }
        if (this.getLanguage2Message() != null && p_networkPorductVO.getLanguage2Message() != null && !BTSLUtil.compareLocaleString(this.getLanguage2Message(), p_networkPorductVO.getLanguage2Message())) {
            sbf.append(startSeperator);
            sbf.append("Language 2 Message");
            sbf.append(middleSeperator);
            sbf.append(p_networkPorductVO.getLanguage2Message());
            sbf.append(middleSeperator);
            sbf.append(this.getLanguage2Message());
        }
        return sbf.toString();
    }

    /**
     * @return Returns the alertingBalance.
     */
    public String getAlertingBalance() {
        return _alertingBalance;
    }

    /**
     * @param alertingBalance
     *            The alertingBalance to set.
     */
    public void setAlertingBalance(String alertingBalance) {
        _alertingBalance = alertingBalance;
    }
}
