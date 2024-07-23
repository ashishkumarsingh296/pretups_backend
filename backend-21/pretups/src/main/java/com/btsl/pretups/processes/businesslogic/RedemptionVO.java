package com.btsl.pretups.processes.businesslogic;

import java.io.Serializable;
import java.util.Date;

/*
 * RedemptionVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Chetan Kothari 16/02/2009 Initial Creation
 * Vikas Kumar 01-Aug-2009 Modification
 * ------------------------------------------------------------------------
 * Copyright (c) 2009 Bharti Telesoft Ltd.
 * VO for Redemption for Activation Bonus
 */
public class RedemptionVO implements Serializable {

    private double _pointsRedeemed;
    private double _amountTransfered;
    private String _profileType;
    private String _redemptionId;
    private String _redemptionType;
    private String _createdBy;
    private String _productCode;
    private String _modifiedBy;
    private String _referenceId;
    private String _userId;
    private Date _redemptionDate;
    private Date _createdOn;
    private Date _modifiedOn;
    private String _userName;
    private String _msisdn = null;
    private String _type;
    private java.lang.String _otherInfo;
    private String _statusType;
    protected String label = null; // name
    protected String value = null; // code
    private String codeName;
    private String _pointsRedeemedStr;
	private String _accmulatedPointsStr;
	private String _transferIdStr;
	private String _redemptionIdStr;
	private String _entryDateStr;
	private String _pointDateStr;

    private String _redemptionDateStr = null;

    public RedemptionVO() {
    }

    public RedemptionVO(String label, String value) {
        this.label = label;
        this.value = value;
        this.codeName = value + "|" + label;
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
     * toString() method writes the parameters value to the console or log
     * 
     * @return String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("_profileType=" + _profileType + ",");
        sb.append("_redemptionId=" + _redemptionId + ",");
        sb.append("_userId=" + _userId);
        sb.append("_referenceId=" + _referenceId + ",");
        sb.append("_productCode=" + _productCode + ",");
        sb.append("_pointsRedeemed=" + _pointsRedeemed + ",");
        sb.append("_amountTransfered=" + _amountTransfered + ",");
        sb.append("_redemptionDate=" + _redemptionDate + ",");
        sb.append("_createdBy=" + _createdBy + ",");
        sb.append("_createdOn=" + _createdOn + ",");
        sb.append("_createdBy=" + _createdBy + ",");
        sb.append("_modifiedOn=" + _modifiedOn + ",");
        sb.append("_modifiedBy=" + _modifiedBy + ",");
        sb.append("_pointsRedeemedStr=" + _pointsRedeemedStr + ",");
        return sb.toString();
    }

    /**
     * @return Returns the amountTransfered.
     */
    public double getAmountTransfered() {
        return _amountTransfered;
    }

    /**
     * @param amountTransfered
     *            The amountTransfered to set.
     */
    public void setAmountTransfered(double amountTransfered) {
        _amountTransfered = amountTransfered;
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

    /**
     * @return Returns the pointsRedeemed.
     */
    public double getPointsRedeemed() {
        return _pointsRedeemed;
    }

    /**
     * @param pointsRedeemed
     *            The pointsRedeemed to set.
     */
    public void setPointsRedeemed(double pointsRedeemed) {
        _pointsRedeemed = pointsRedeemed;
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
     * @return Returns the redemptionDate.
     */
    public Date getRedemptionDate() {
        return _redemptionDate;
    }

    /**
     * @param redemptionDate
     *            The redemptionDate to set.
     */
    public void setRedemptionDate(Date redemptionDate) {
        _redemptionDate = redemptionDate;
    }

    /**
     * @return Returns the redemptionId.
     */
    public String getRedemptionId() {
        return _redemptionId;
    }

    /**
     * @param redemptionId
     *            The redemptionId to set.
     */
    public void setRedemptionId(String redemptionId) {
        _redemptionId = redemptionId;
    }

    /**
     * @return Returns the redemptionType.
     */
    public String getRedemptionType() {
        return _redemptionType;
    }

    /**
     * @param redemptionType
     *            The redemptionType to set.
     */
    public void setRedemptionType(String redemptionType) {
        _redemptionType = redemptionType;
    }

    /**
     * @return Returns the referenceId.
     */
    public String getReferenceId() {
        return _referenceId;
    }

    /**
     * @param referenceId
     *            The referenceId to set.
     */
    public void setReferenceId(String referenceId) {
        _referenceId = referenceId;
    }

    /**
     * @return Returns the msisdn.
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param msisdn
     *            The msisdn to set.
     */
    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    /**
     * @return Returns the otherInfo.
     */
    public java.lang.String getOtherInfo() {
        return _otherInfo;
    }

    /**
     * @param otherInfo
     *            The otherInfo to set.
     */
    public void setOtherInfo(java.lang.String otherInfo) {
        _otherInfo = otherInfo;
    }

    /**
     * @return Returns the statusType.
     */
    public String getStatusType() {
        return _statusType;
    }

    /**
     * @param statusType
     *            The statusType to set.
     */
    public void setStatusType(String statusType) {
        _statusType = statusType;
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
     * @return Returns the userName.
     */
    public String getUserName() {
        return _userName;
    }

    /**
     * @param userName
     *            The userName to set.
     */
    public void setUserName(String userName) {
        _userName = userName;
    }

    /**
     * @return Returns the codeName.
     */
    public String getCodeName() {
        return codeName;
    }

    /**
     * @param codeName
     *            The codeName to set.
     */
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    /**
     * @return Returns the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label
     *            The label to set.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return Returns the _redemptionDateStr.
     */
    public String getRedemptionDateStr() {
        return _redemptionDateStr;
    }

    /**
     * @param dateStr
     *            The _redemptionDateStr to set.
     */
    public void setRedemptionDateStr(String dateStr) {
        _redemptionDateStr = dateStr;
    }

    public String getPointsRedeemedStr() {
        return _pointsRedeemedStr;
    }

    public void setPointsRedeemedStr(String redeemedStr) {
        _pointsRedeemedStr = redeemedStr;
    }
	public String getAccmulatedPointsStr() {
		return _accmulatedPointsStr;
	}
	public void setAccmulatedPointsStr(String accmulatedPointsStr) {
		_accmulatedPointsStr = accmulatedPointsStr;
	}
	public String getTransferIdStr() {
		return _transferIdStr;
	}
	public void setTransferIdStr(String transferIdStr) {
		_transferIdStr = transferIdStr;
	}
	public String getRedemptionIdStr() {
		return _redemptionIdStr;
	}
	public void setRedemptionIdStr(String redemptionIdStr) {
		_redemptionIdStr = redemptionIdStr;
	}
	public String getEntryDateStr() {
		return _entryDateStr;
	}
	public void setEntryDateStr(String entryDateStr) {
		_entryDateStr = entryDateStr;
	}
	public String getPointDateStr() {
		return _pointDateStr;
	}
	public void setPointDateStr(String pointDateStr) {
		_pointDateStr = pointDateStr;
	}
}
