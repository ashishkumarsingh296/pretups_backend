package com.selftopup.pretups.cardgroup.businesslogic;

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
    private String _bundleID = null;
    private String _bundleName = null;
    private String _bundleCode = null;
    private String _bundleType = null;
    private String _status = null;
    private String _resINStatus = null;

    public String toString() {
        StringBuffer sb = new StringBuffer("CardGroupSetVO Data ");
        sb.append("_bundleID=" + _bundleID + ",");
        sb.append("_bundleName=" + _bundleName + ",");
        sb.append("_bundleCode=" + _bundleCode + ",");
        sb.append("_bundleType=" + _bundleType + ",");
        sb.append("_status=" + _status + ",");
        sb.append("_resINStatus=" + _resINStatus + ",");

        return sb.toString();
    }

    /**
     * @return Returns the bundleCode.
     */
    public String getBundleCode() {
        return _bundleCode;
    }

    /**
     * @param bundleCode
     *            The bundleCode to set.
     */
    public void setBundleCode(String bundleCode) {
        _bundleCode = bundleCode;
    }

    /**
     * @return Returns the bundleID.
     */
    public String getBundleID() {
        return _bundleID;
    }

    /**
     * @param bundleID
     *            The bundleID to set.
     */
    public void setBundleID(String bundleID) {
        _bundleID = bundleID;
    }

    /**
     * @return Returns the bundleName.
     */
    public String getBundleName() {
        return _bundleName;
    }

    /**
     * @param bundleName
     *            The bundleName to set.
     */
    public void setBundleName(String bundleName) {
        _bundleName = bundleName;
    }

    /**
     * @return Returns the resINStatus.
     */
    public String getResINStatus() {
        return _resINStatus;
    }

    /**
     * @param resINStatus
     *            The resINStatus to set.
     */
    public void setResINStatus(String resINStatus) {
        _resINStatus = resINStatus;
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
     * @return Returns the bundleType.
     */
    public String getBundleType() {
        return _bundleType;
    }

    /**
     * @param bundleType
     *            The bundleType to set.
     */
    public void setBundleType(String bundleType) {
        _bundleType = bundleType;
    }
}
