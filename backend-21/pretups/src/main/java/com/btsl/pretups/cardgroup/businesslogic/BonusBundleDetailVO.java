package com.btsl.pretups.cardgroup.businesslogic;

/*
 * BonusBundleDetailVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Vinay Kumar Singh Jun 16, 2009 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2009 Comviva Technologies Ltd.
 */
import java.io.Serializable;

public class BonusBundleDetailVO implements Serializable {
    private String bundleID = null;
    private String bundleName = null;
    private String bundleCode = null;
    private String bundleType = null;
    private String status = null;
    private String resINStatus = null;

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        final StringBuilder sb = new StringBuilder("CardGroupSetVO Data ");
        sb.append("bundleID=" + bundleID + ",");
        sb.append("bundleName=" + bundleName + ",");
        sb.append("bundleCode=" + bundleCode + ",");
        sb.append("bundleType=" + bundleType + ",");
        sb.append("status=" + status + ",");
        sb.append("resINStatus=" + resINStatus + ",");

        return sb.toString();
    }

    /**
     * @return Returns the bundleCode.
     */
    public String getBundleCode() {
        return bundleCode;
    }

    /**
     * @param bundleCode
     *            The bundleCode to set.
     */
    public void setBundleCode(String bundleCode) {
    	this.bundleCode = bundleCode;
    }

    /**
     * @return Returns the bundleID.
     */
    public String getBundleID() {
        return bundleID;
    }

    /**
     * @param bundleID
     *            The bundleID to set.
     */
    public void setBundleID(String bundleID) {
    	this.bundleID = bundleID;
    }

    /**
     * @return Returns the bundleName.
     */
    public String getBundleName() {
        return bundleName;
    }

    /**
     * @param bundleName
     *            The bundleName to set.
     */
    public void setBundleName(String bundleName) {
    	this.bundleName = bundleName;
    }

    /**
     * @return Returns the resINStatus.
     */
    public String getResINStatus() {
        return resINStatus;
    }

    /**
     * @param resINStatus
     *            The resINStatus to set.
     */
    public void setResINStatus(String resINStatus) {
    	this. resINStatus = resINStatus;
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
     * @return Returns the bundleType.
     */
    public String getBundleType() {
        return bundleType;
    }

    /**
     * @param bundleType
     *            The bundleType to set.
     */
    public void setBundleType(String bundleType) {
    	this.bundleType = bundleType;
    }
}
