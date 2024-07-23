package com.btsl.pretups.processes.businesslogic;

import java.io.Serializable;
import java.util.Date;

/*
 * ActivationBonusVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Chetan Kothari 16/02/2009 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * VO for transfers for Activation Bonus
 */
public class ActivationBonusVO implements Serializable {

    /**
     * @return Returns the points.
     */
    public double getPoints() {
        return _points;
    }

    /**
     * @param points
     *            The points to set.
     */
    public void setPoints(double points) {
        _points = points;
    }

    /**
     * @return Returns the pointstoRedeem.
     */
    public int getPointsToRedeem() {
        return _pointsToRedeem;
    }

    /**
     * @param pointstoRedeem
     *            The pointstoRedeem to set.
     */
    public void setPointsToRedeem(int pointstoRedeem) {
        _pointsToRedeem = pointstoRedeem;
    }

    /**
     * @return Returns the remainingPoints.
     */
    public double getRemainingPoints() {
        return _remainingPoints;
    }

    /**
     * @param remainingPoints
     *            The remainingPoints to set.
     */
    public void setRemainingPoints(double remainingPoints) {
        _remainingPoints = remainingPoints;
    }

    private double _points;
    private double _remainingPoints;
    private int _pointsToRedeem;

    private String _profileType;
    private String _userId;
    private String _lastRedemptionId;
    private String _lastAllocationType;
    private String _createdBy;
    private String _bucketCode;
    private String _productCode;
    private String _modifiedBy;
    private Date _pointsDate;
    private Date _lastRedemptionDate;
    private Date _lastAllocationdate;
    private Date _createdOn;
    private Date _modifiedOn;
    private String _txnCalculationDone;
    private String _transferId;
    private long _amount;
    private long _count;
    // added for lms
    private long _accumulatedPoints;
    // Brajesh
    private String _setId;
    private String _version;

    private String _networkCode;

    /**
     * toString() method writes the parameters value to the console or log
     * 
     * @return String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("_profileType=" + _profileType + ",");
        sb.append("_userId=" + _userId + ",");
        sb.append("_lastRedemptionId=" + _lastRedemptionId + ",");
        sb.append("_lastAllocationType=" + _lastAllocationType + ",");
        sb.append("_createdBy=" + _createdBy + ",");
        sb.append("_bucketCode=" + _bucketCode + ",");
        sb.append("_productCode=" + _productCode + ",");
        sb.append("_createdOn=" + _createdOn + ",");
        sb.append("_createdBy=" + _createdBy + ",");
        sb.append("_modifiedOn=" + _modifiedOn + ",");
        sb.append("_modifiedBy=" + _modifiedBy);
        sb.append("_points=" + _points);
        sb.append("_remainingPoints=" + _remainingPoints);
        sb.append("_txnCalculationDone= " + _txnCalculationDone);
        sb.append("_accumulatedPoints= " + _accumulatedPoints);
        // Brajesh

        sb.append("_setId= " + _setId);
        sb.append("_version= " + _version);
        //
        return sb.toString();
    }

    /**
     * @return Returns the bucketCode.
     */
    public String getBucketCode() {
        return _bucketCode;
    }

    /**
     * @param bucketCode
     *            The bucketCode to set.
     */
    public void setBucketCode(String bucketCode) {
        _bucketCode = bucketCode;
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
     * @return Returns the lastAllocationdate.
     */
    public Date getLastAllocationdate() {
        return _lastAllocationdate;
    }

    /**
     * @param lastAllocationdate
     *            The lastAllocationdate to set.
     */
    public void setLastAllocationdate(Date lastAllocationdate) {
        _lastAllocationdate = lastAllocationdate;
    }

    /**
     * @return Returns the lastAllocationType.
     */
    public String getLastAllocationType() {
        return _lastAllocationType;
    }

    /**
     * @param lastAllocationType
     *            The lastAllocationType to set.
     */
    public void setLastAllocationType(String lastAllocationType) {
        _lastAllocationType = lastAllocationType;
    }

    /**
     * @return Returns the lastRedemptionDate.
     */
    public Date getLastRedemptionDate() {
        return _lastRedemptionDate;
    }

    /**
     * @param lastRedemptionDate
     *            The lastRedemptionDate to set.
     */
    public void setLastRedemptionDate(Date lastRedemptionDate) {
        _lastRedemptionDate = lastRedemptionDate;
    }

    /**
     * @return Returns the lastRedemptionId.
     */
    public String getLastRedemptionId() {
        return _lastRedemptionId;
    }

    /**
     * @param lastRedemptionId
     *            The lastRedemptionId to set.
     */
    public void setLastRedemptionId(String lastRedemptionId) {
        _lastRedemptionId = lastRedemptionId;
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

    /**
     * @return Returns the pointsDate.
     */
    public Date getPointsDate() {
        return _pointsDate;
    }

    /**
     * @param pointsDate
     *            The pointsDate to set.
     */
    public void setPointsDate(Date pointsDate) {
        _pointsDate = pointsDate;
    }

    /**
     * @return Returns the productCode.
     */
    public String getProductCode() {
        return _productCode;
    }

    /**
     * @param productCode
     *            The productCode to set.
     */
    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    /**
     * @return Returns the profileType.
     */
    public String getProfileType() {
        return _profileType;
    }

    /**
     * @param profileType
     *            The profileType to set.
     */
    public void setProfileType(String profileType) {
        _profileType = profileType;
    }

    /**
     * @return Returns the userId.
     */
    public String getUserId() {
        return _userId;
    }

    /**
     * @param userId
     *            The userId to set.
     */
    public void setUserId(String userId) {
        _userId = userId;
    }

    /**
     * @return Returns the txnCalculationDone.
     */
    public String getTxnCalculationDone() {
        return _txnCalculationDone;
    }

    /**
     * @param txnCalculationDone
     *            The txnCalculationDone to set.
     */
    public void setTxnCalculationDone(String txnCalculationDone) {
        _txnCalculationDone = txnCalculationDone;
    }

    /**
     * @return Returns the transferId.
     */
    public String getTransferId() {
        return _transferId;
    }

    /**
     * @param transferId
     *            The transferId to set.
     */
    public void setTransferId(String transferId) {
        _transferId = transferId;
    }

    /**
     * @return Returns the amount.
     */
    public long getAmount() {
        return _amount;
    }

    /**
     * @param amount
     *            The amount to set.
     */
    public void setAmount(long amount) {
        _amount = amount;
    }

    /**
     * @return Returns the count.
     */
    public long getCount() {
        return _count;
    }

    /**
     * @param count
     *            The count to set.
     */
    public void setCount(long count) {
        _count = count;
    }

    public long getAccumulatedPoints() {
        return _accumulatedPoints;
    }

    public void setAccumulatedPoints(long points) {
        _accumulatedPoints = points;
    }

    // Brajesh for profile id entry in bonus and bonus history table
    /**
     * @return Returns the SetId.
     */
    public String getSetId() {
        return _setId;
    }

    /**
     * @param bucketCode
     *            The SetId to set.
     */
    public void setSetID(String setId) {
        _setId = setId;
    }

    /**
	 */
    //
    // Brajesh for version entry in bonus and bonus history table
    /**
     * @return Returns the SetId.
     */
    public String getVersion() {
        return _version;
    }

    /**
     * @param bucketCode
     *            The SetId to set.
     */
    public void setVersion(String version) {
        _version = version;
    }

    /**
	 */
    //

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }
}
