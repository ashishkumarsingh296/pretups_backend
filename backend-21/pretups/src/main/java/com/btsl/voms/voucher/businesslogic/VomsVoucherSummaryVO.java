package com.btsl.voms.voucher.businesslogic;

import java.util.Date;

import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;

/*
 * @(#)VomsVoucherSummaryVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Amit Singh 19/07/2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 */

public class VomsVoucherSummaryVO extends VomsProductVO {
    // Instanse variables
    private Date _summaryDate;
    private String _productID;
    private String _productionNetworkCode;
    private String _userNetworkCode;
    private long _totalGenerated;
    private long _totalEnabled;
    private long _totalRecharged;
    private long _totalStolenDmg;
    private long _totalOnHold;
    private long _totalStolenDmgAfterEn;
    private String _type;
    private long _totalReconciled;
    private long _totalReconciledChanged;

    /**
     * Method toString.
     * This method is used to display all of the information of
     * the object of the VomsProductVO class.
     * 
     * @return String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" _summaryDate=" + _summaryDate);
        sb.append(" _productID=" + _productID);
        sb.append(" _productionNetworkCode=" + _productionNetworkCode);
        sb.append(" _userNetworkCode=" + _userNetworkCode);
        sb.append(" _totalGenerated=" + _totalGenerated);
        sb.append(" _totalEnabled=" + _totalEnabled);
        sb.append(" _totalRecharged=" + _totalRecharged);
        sb.append(" _totalStolenDmg=" + _totalStolenDmg);
        sb.append(" _totalOnHold=" + _totalOnHold);
        sb.append(" _totalStolenDmgAfterEn=" + _totalStolenDmgAfterEn);
        sb.append(" _type=" + _type);
        sb.append(" _totalReconciled=" + _totalReconciled);
        sb.append(" _totalReconciledChanged=" + _totalReconciledChanged);

        return sb.toString();
    }

    /**
     * @return Returns the productID.
     */
    public String getProductID() {
        return _productID;
    }

    /**
     * @param productID
     *            The productID to set.
     */
    public void setProductID(String productID) {
        _productID = productID;
    }

    /**
     * @return Returns the productionNetworkCode.
     */
    public String getProductionNetworkCode() {
        return _productionNetworkCode;
    }

    /**
     * @param productionNetworkCode
     *            The productionNetworkCode to set.
     */
    public void setProductionNetworkCode(String productionNetworkCode) {
        _productionNetworkCode = productionNetworkCode;
    }

    /**
     * @return Returns the summaryDate.
     */
    public Date getSummaryDate() {
        return _summaryDate;
    }

    /**
     * @param summaryDate
     *            The summaryDate to set.
     */
    public void setSummaryDate(Date summaryDate) {
        _summaryDate = summaryDate;
    }

    /**
     * @return Returns the totalEnabled.
     */
    public long getTotalEnabled() {
        return _totalEnabled;
    }

    /**
     * @param totalEnabled
     *            The totalEnabled to set.
     */
    public void setTotalEnabled(long totalEnabled) {
        _totalEnabled = totalEnabled;
    }

    /**
     * @return Returns the totalGenerated.
     */
    public long getTotalGenerated() {
        return _totalGenerated;
    }

    /**
     * @param totalGenerated
     *            The totalGenerated to set.
     */
    public void setTotalGenerated(long totalGenerated) {
        _totalGenerated = totalGenerated;
    }

    /**
     * @return Returns the totalOnHold.
     */
    public long getTotalOnHold() {
        return _totalOnHold;
    }

    /**
     * @param totalOnHold
     *            The totalOnHold to set.
     */
    public void setTotalOnHold(long totalOnHold) {
        _totalOnHold = totalOnHold;
    }

    /**
     * @return Returns the totalRecharged.
     */
    public long getTotalRecharged() {
        return _totalRecharged;
    }

    /**
     * @param totalRecharged
     *            The totalRecharged to set.
     */
    public void setTotalRecharged(long totalRecharged) {
        _totalRecharged = totalRecharged;
    }

    /**
     * @return Returns the totalReconciled.
     */
    public long getTotalReconciled() {
        return _totalReconciled;
    }

    /**
     * @param totalReconciled
     *            The totalReconciled to set.
     */
    public void setTotalReconciled(long totalReconciled) {
        _totalReconciled = totalReconciled;
    }

    /**
     * @return Returns the totalReconciledChanged.
     */
    public long getTotalReconciledChanged() {
        return _totalReconciledChanged;
    }

    /**
     * @param totalReconciledChanged
     *            The totalReconciledChanged to set.
     */
    public void setTotalReconciledChanged(long totalReconciledChanged) {
        _totalReconciledChanged = totalReconciledChanged;
    }

    /**
     * @return Returns the totalStolenDmg.
     */
    public long getTotalStolenDmg() {
        return _totalStolenDmg;
    }

    /**
     * @param totalStolenDmg
     *            The totalStolenDmg to set.
     */
    public void setTotalStolenDmg(long totalStolenDmg) {
        _totalStolenDmg = totalStolenDmg;
    }

    /**
     * @return Returns the totalStolenDmgAfterEn.
     */
    public long getTotalStolenDmgAfterEn() {
        return _totalStolenDmgAfterEn;
    }

    /**
     * @param totalStolenDmgAfterEn
     *            The totalStolenDmgAfterEn to set.
     */
    public void setTotalStolenDmgAfterEn(long totalStolenDmgAfterEn) {
        _totalStolenDmgAfterEn = totalStolenDmgAfterEn;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return _type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(String type) {
        _type = type;
    }

    /**
     * @return Returns the userNetworkCode.
     */
    public String getUserNetworkCode() {
        return _userNetworkCode;
    }

    /**
     * @param userNetworkCode
     *            The userNetworkCode to set.
     */
    public void setUserNetworkCode(String userNetworkCode) {
        _userNetworkCode = userNetworkCode;
    }

}
