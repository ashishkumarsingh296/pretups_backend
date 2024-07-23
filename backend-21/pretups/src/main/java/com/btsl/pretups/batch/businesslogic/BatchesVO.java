package com.btsl.pretups.batch.businesslogic;

/*
 * @# BacthesVO.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Ved prakash July 21, 2006 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2006 Bharti Telesoft Ltd.
 * This class use for batch user creation.
 */

import java.io.Serializable;
import java.util.Date;

public class BatchesVO implements Serializable {
    private String batchID;
    private String batchType;
    private long batchSize;
    private String batchName;
    private String networkCode;
    private String status;
    private String statusDesc;
    private String fileName;
    private String createdBy;
    private Date createdOn;
    private String createdOnStr;
    private String modifiedBy;
    private Date modifiedOn;
    private String modifiedOnStr;
    private int totalRecords;
    private int newRecords;
    private int activeRecords;
    private int rejectRecords;
    // Added for finding channel user type (Channel or Operator)
    private String intiatorUserType;
    // Added for find initiator category to display list in master data sheet
    // during approval.
    private String initiatorCategory;

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder sbf = new StringBuilder("batchID=" + batchID + " batchType=" + batchType + " batchSize=" + batchSize + " batchName=" + batchName + " networkCode=" + networkCode);
        sbf.append(" status=" + status + " statusDesc=" + statusDesc + " createdBy=" + createdBy + " createdOn=" + createdOn + " createdOnStr=" + createdOnStr);
        sbf.append(" modifiedBy=" + modifiedBy + " modifiedOn=" + modifiedOn + " modifiedOnStr=" + modifiedOnStr + " totalRecords=" + totalRecords + " newRecords=" + newRecords);
        sbf.append(" activeRecords=" + activeRecords + " rejectRecords=" + rejectRecords + " fileName=" + fileName);
        sbf.append(" ,intiatorUserType=" + intiatorUserType + ",initiatorCategory=" + initiatorCategory);// adding
                                                                                                             // for
                                                                                                             // channel
                                                                                                             // user
                                                                                                             // type.
        return sbf.toString();
    }

    /**
     * @return Returns the fileName.
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * @param fileName
     *            The fileName to set.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return Returns the batchID.
     */
    public String getBatchID() {
        return batchID;
    }

    /**
     * @param batchID
     *            The batchID to set.
     */
    public void setBatchID(String batchID) {
    	this.batchID = batchID;
    }

    /**
     * @return Returns the batchName.
     */
    public String getBatchName() {
        return batchName;
    }

    /**
     * @param batchName
     *            The batchName to set.
     */
    public void setBatchName(String batchName) {
    	this.batchName = batchName;
    }

    /**
     * @return Returns the batchSize.
     */
    public long getBatchSize() {
        return batchSize;
    }

    /**
     * @param batchSize
     *            The batchSize to set.
     */
    public void setBatchSize(long batchSize) {
    	this. batchSize = batchSize;
    }

    /**
     * @return Returns the batchType.
     */
    public String getBatchType() {
        return batchType;
    }

    /**
     * @param batchType
     *            The batchType to set.
     */
    public void setBatchType(String batchType) {
    	this. batchType = batchType;
    }

    /**
     * @return Returns the createdBy.
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy
     *            The createdBy to set.
     */
    public void setCreatedBy(String createdBy) {
    	this. createdBy = createdBy;
    }

    /**
     * @return Returns the createdOn.
     */
    public Date getCreatedOn() {
        return createdOn;
    }

    /**
     * @param createdOn
     *            The createdOn to set.
     */
    public void setCreatedOn(Date createdOn) {
    	this.createdOn = createdOn;
    }

    /**
     * @return Returns the createdOnStr.
     */
    public String getCreatedOnStr() {
        return createdOnStr;
    }

    /**
     * @param createdOnStr
     *            The createdOnStr to set.
     */
    public void setCreatedOnStr(String createdOnStr) {
    	this.createdOnStr = createdOnStr;
    }

    /**
     * @return Returns the modifiedBy.
     */
    public String getModifiedBy() {
        return modifiedBy;
    }

    /**
     * @param modifiedBy
     *            The modifiedBy to set.
     */
    public void setModifiedBy(String modifiedBy) {
    	this.modifiedBy = modifiedBy;
    }

    /**
     * @return Returns the modifiedOn.
     */
    public Date getModifiedOn() {
        return modifiedOn;
    }

    /**
     * @param modifiedOn
     *            The modifiedOn to set.
     */
    public void setModifiedOn(Date modifiedOn) {
    	this. modifiedOn = modifiedOn;
    }

    /**
     * @return Returns the modifiedOnStr.
     */
    public String getModifiedOnStr() {
        return modifiedOnStr;
    }

    /**
     * @param modifiedOnStr
     *            The modifiedOnStr to set.
     */
    public void setModifiedOnStr(String modifiedOnStr) {
    	this.modifiedOnStr = modifiedOnStr;
    }

    /**
     * @return Returns the networkCode.
     */
    public String getNetworkCode() {
        return networkCode;
    }

    /**
     * @param networkCode
     *            The networkCode to set.
     */
    public void setNetworkCode(String networkCode) {
    	this.networkCode = networkCode;
    }

    /**
     * @return Returns the statusDesc.
     */
    public String getStatusDesc() {
        return statusDesc;
    }

    /**
     * @param statusDesc
     *            The statusDesc to set.
     */
    public void setStatusDesc(String statusDesc) {
    	this.statusDesc = statusDesc;
    }

    /**
     * @return Returns the activeRecords.
     */
    public int getActiveRecords() {
        return activeRecords;
    }

    /**
     * @param activeRecords
     *            The activeRecords to set.
     */
    public void setActiveRecords(int activeRecords) {
    	this.activeRecords = activeRecords;
    }

    /**
     * @return Returns the newRecords.
     */
    public int getNewRecords() {
        return newRecords;
    }

    /**
     * @param newRecords
     *            The newRecords to set.
     */
    public void setNewRecords(int newRecords) {
    	this.newRecords = newRecords;
    }

    /**
     * @return Returns the rejectRecords.
     */
    public int getRejectRecords() {
        return rejectRecords;
    }

    /**
     * @param rejectRecords
     *            The rejectRecords to set.
     */
    public void setRejectRecords(int rejectRecords) {
    	this.rejectRecords = rejectRecords;
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
     * @return Returns the totalRecords.
     */
    public int getTotalRecords() {
        return totalRecords;
    }

    /**
     * @param totalRecords
     *            The totalRecords to set.
     */
    public void setTotalRecords(int totalRecords) {
    	this.totalRecords = totalRecords;
    }

    /**
     * @return
     */
    public String getIntiatorUserType() {
        return intiatorUserType;
    }

    /**
     * @param userType
     */
    public void setIntiatorUserType(String intiatorUserType) {
    	this.intiatorUserType = intiatorUserType;
    }

    /**
     * @return
     */
    public String getInitiatorCategory() {
        return initiatorCategory;
    }

    /**
     * @param category
     */
    public void setInitiatorCategory(String initiatorCategory) {
    	this.initiatorCategory = initiatorCategory;
    }
}
