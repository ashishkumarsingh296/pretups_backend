package com.btsl.pretups.channel.reports.businesslogic;

/*
 * @# C2SBonusReportVO.java
 * This class use in C2C Batch Enquiry.
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * JUL 21, 2011 Babu Kunwar Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2008 Comviva Technologies Ltd.
 */

/**
 * @author
 *
 */
public class C2SBonusReportVO {

    private String transDate = null;
    private String serviceType = null;
    private String bundleID = null;
    private String bundleType = null;
    private String transAmount = null;
    private String transCount = null;
    private String serviceClassID = null;
    private String serviceClassCode = null;

    /**
     * 
     * @return
     */
    public String getserviceClassCode() {
        return serviceClassCode;
    }

    /**
     * 
     * @param classCode
     */
    public void setserviceClassCode(String classCode) {
    	 serviceClassCode = classCode;
    }

    /**
     * 
     * @return
     */
    public String getserviceClassID() {
        return serviceClassID;
    }

    /**
     * @param classID
     */
    public void setserviceClassID(String classID) {
    	 serviceClassID = classID;
    }

    /**
     * @return the bundleID
     */
    public String getBundleID() {
        return bundleID;
    }

    /**
     * @param bundleID
     *            the bundleID to set
     */
    public void setBundleID(String bundleID) {
    	 this.bundleID = bundleID;
    }

    /**
     * @return the bundleType
     */
    public String getBundleType() {
        return bundleType;
    }

    /**
     * @param bundleType
     *            the bundleType to set
     */
    public void setBundleType(String bundleType) {
    	 this.bundleType = bundleType;
    }

    /**
     * @return the serviceType
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * @param serviceType
     *            the serviceType to set
     */
    public void setServiceType(String serviceType) {
    	 this.serviceType = serviceType;
    }

    /**
     * @return the transAmount
     */
    public String getTransAmount() {
        return transAmount;
    }

    /**
     * @param transAmount
     *            the transAmount to set
     */
    public void setTransAmount(String transAmount) {
    	 this.transAmount = transAmount;
    }

    /**
     * @return the transCount
     */
    public String getTransCount() {
        return transCount;
    }

    /**
     * @param transCount
     *            the transCount to set
     */
    public void setTransCount(String transCount) {
    	 this.transCount = transCount;
    }

    /**
     * @return the transDate
     */
    public String getTransDate() {
        return transDate;
    }

    /**
     * @param transDate
     *            the transDate to set
     */
    public void setTransDate(String transDate) {
    	 this.transDate = transDate;
    }

}
