/**
 * @(#)BatchModifyCommissionProfileVO.java
 * 
 * 
 *                                         <description>
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 *                                         Author Date History
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 *                                         gaurav.pandey April 10, 2012 Initital
 *                                         Creation
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 * 
 */

package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BatchModifyCommissionProfileVO implements Serializable {
    private String _domainCode;
    private String _userid;
    private String _categoryCode;
    private String _commProfileSetId;
    private String _commProfileSetName;
    private String _shortCode;
    private String _setVersion;
    private Timestamp _applicableFrom;
    private long _minTransferValue;
    private String _minTransferValueAsString = "0";
    private long _maxTransferValue;
    private String _maxTransferValueAsString = "0";
    private long _transferMultipleOff;
    private String _transferMultipleOffAsString = "0";
    private String _taxOnChannelTransfer;
    private String _taxOnFOCApplicable;
    private long _startRange;
    private String _startRangeAsString;
    private String _productCode;
    private long _endRange;
    private String _endRangeAsString;
    private String _commType;
    private double _commRate;
    private String _commRateAsString = "0";
    private String _tax1Type;
    private double _tax1Rate;
    private String _tax1RateAsString = "0";
    private String _tax2Type;
    private double _tax2Rate;
    private String _tax2RateAsString = "0";
    private String _tax3Type;
    private double _tax3Rate;
    private String _tax3RateAsString = "0";
    private boolean _versionUpdated = false;
    private String _commProfileProductID;
    private long _multipleOff;
    private String _multipleOffAsString = "0";
    private String _batch_ID = null;
    private String _batch_name = null;
    private String _gradeCode = null;
    private String _grphDomainCode = null;
    private double _transferMultipleOffInDouble;
    private String commProfileDetailID;
    private String otfApplicableFromStr;
    private String commissionProfileType;
    private String paymentMode;
    private String transactionType;
    
    public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	
	
	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getCommissionProfileType() {
		return commissionProfileType;
	}

	public void setCommissionProfileType(String commissionProfileType) {
		this.commissionProfileType = commissionProfileType;
	}

	public String getOtfApplicableFromStr() {
		return otfApplicableFromStr;
	}

	public void setOtfApplicableFromStr(String otfApplicableFromStr) {
		this.otfApplicableFromStr = otfApplicableFromStr;
	}

	public String getOtfApplicableToStr() {
		return otfApplicableToStr;
	}

	public void setOtfApplicableToStr(String otfApplicableToStr) {
		this.otfApplicableToStr = otfApplicableToStr;
	}

	public String getOtfTimeSlab() {
		return otfTimeSlab;
	}

	public void setOtfTimeSlab(String otfTimeSlab) {
		this.otfTimeSlab = otfTimeSlab;
	}

	public String getOtfType() {
		return otfType;
	}

	public void setOtfType(String otfType) {
		this.otfType = otfType;
	}

	public List<OTFDetailsVO> getOtfDetails() {
		return otfDetails;
	}

	public void setOtfDetails(List<OTFDetailsVO> otfDetails) {
		List<OTFDetailsVO> otfDetailscpy = new ArrayList();
		if(!otfDetails.isEmpty()){
		for(OTFDetailsVO otfDet:otfDetails){
			otfDetailscpy.add(otfDet);
		}
		}
		this.otfDetails = otfDetailscpy;
	}

	public int getOtfDetailsSize() {
		return otfDetailsSize;
	}

	public void setOtfDetailsSize(int otfDetailsSize) {
		this.otfDetailsSize = otfDetailsSize;
	}


	private String otfApplicableToStr;
    private String otfTimeSlab;
	private String otfType;
	private List<OTFDetailsVO> otfDetails;
	private int otfDetailsSize;

    public String getCommProfileDetailID() {
		return commProfileDetailID;
	}

	public void setCommProfileDetailID(String commProfileDetailID) {
		this.commProfileDetailID = commProfileDetailID;
	}

	public String getBatch_ID() {
        return _batch_ID;
    }

    public void setBatch_ID(String batch_ID) {
        _batch_ID = batch_ID;
    }

    public String getBatch_name() {
        return _batch_name;
    }

    public void setBatch_name(String batch_name) {
        _batch_name = batch_name;
    }

    public long getMultipleOff() {
        return _multipleOff;
    }

    public void setMultipleOff(long multipleOff) {
        _multipleOff = multipleOff;
    }

    public String getMultipleOffAsString() {
        return _multipleOffAsString;
    }

    public void setMultipleOffAsString(String multipleOffAsString) {
        _multipleOffAsString = multipleOffAsString;
    }

    public String getCommProfileProductID() {
        return _commProfileProductID;
    }

    public void setCommProfileProductID(String commProfileProductID) {
        _commProfileProductID = commProfileProductID;
    }

    public BatchModifyCommissionProfileVO() {
    };

    public BatchModifyCommissionProfileVO(
                    BatchModifyCommissionProfileVO batchModifyCommissionProfile) {
        this._applicableFrom = batchModifyCommissionProfile._applicableFrom;
        this._commProfileSetId = batchModifyCommissionProfile._commProfileSetId;
        this._commProfileSetName = batchModifyCommissionProfile._commProfileSetName;
        this._commRate = batchModifyCommissionProfile._commRate;
        this._commRateAsString = batchModifyCommissionProfile._commRateAsString;
        this._commType = batchModifyCommissionProfile._commType;
        this._endRange = batchModifyCommissionProfile._endRange;
        this._endRangeAsString = batchModifyCommissionProfile._endRangeAsString;
        this._maxTransferValue = batchModifyCommissionProfile._maxTransferValue;
        this._minTransferValue = batchModifyCommissionProfile._minTransferValue;
        this._setVersion = batchModifyCommissionProfile._setVersion;
        this._shortCode = batchModifyCommissionProfile._shortCode;
        this._startRange = batchModifyCommissionProfile._startRange;
        this._startRangeAsString = batchModifyCommissionProfile._startRangeAsString;
        this._tax1Rate = batchModifyCommissionProfile._tax1Rate;
        this._tax1RateAsString = batchModifyCommissionProfile._tax1RateAsString;
        this._tax1Type = batchModifyCommissionProfile._tax1Type;
        this._tax2Rate = batchModifyCommissionProfile._tax2Rate;
        this._tax2RateAsString = batchModifyCommissionProfile._tax2RateAsString;
        this._tax2Type = batchModifyCommissionProfile._tax2Type;
        this._tax3Rate = batchModifyCommissionProfile._tax3Rate;
        this._tax3RateAsString = batchModifyCommissionProfile._tax3RateAsString;
        this._tax3Type = batchModifyCommissionProfile._tax3Type;
        this._taxOnChannelTransfer = batchModifyCommissionProfile._taxOnChannelTransfer;
        this._taxOnFOCApplicable = batchModifyCommissionProfile._taxOnFOCApplicable;
        this._transferMultipleOff = batchModifyCommissionProfile._transferMultipleOff;
    }

    public String toString() {
        final StringBuffer sb = new StringBuffer("BatchModifyCommissionProfileVO Data ");
        sb.append("_applicableFrom=" + _applicableFrom + ",");
        sb.append("_commProfileSetId=" + _commProfileSetId + ",");
        sb.append("_commProfileSetName=" + _commProfileSetName + ",");
        sb.append("_commRate=" + _commRate + ",");
        sb.append("_endRange=" + _endRange + ",");
        sb.append("_endRangeAsString=" + _endRangeAsString + ",");
        sb.append("_maxTransferValue=" + _maxTransferValue + ",");
        sb.append("_minTransferValue=" + _minTransferValue + ",");
        sb.append("_setVersion=" + _setVersion + ",");
        sb.append("_shortCode=" + _shortCode + ",");
        sb.append("_startRange=" + _startRange + ",");
        sb.append("_tax1Rate=" + _tax1Rate + ",");
        sb.append("_tax1RateAsString=" + _tax1RateAsString + ",");
        sb.append("_tax1Type=" + _tax1Type + ",");
        sb.append("_tax2Rate=" + _tax2Rate + ",");
        sb.append("_tax2RateAsString=" + _tax2RateAsString + ",");
        sb.append("_tax2Type=" + _tax2Type + ",");
        sb.append("_tax3Rate=" + _tax3Rate + ",");
        sb.append("_tax3RateAsString=" + _tax3RateAsString + ",");
        sb.append("_tax3Type=" + _tax3Type + ",");
        sb.append("_taxOnChannelTransfer+" + _taxOnChannelTransfer + ",");
        sb.append("_taxOnFOCApplicable=" + _taxOnFOCApplicable + ",");
        sb.append("_transferMultipleOff+" + _transferMultipleOff + ",");
        sb.append("paymentMode+" + paymentMode + ",");
        sb.append("transactionType+" + transactionType );
        return sb.toString();
    }

    public String getCommProfileSetId() {
        return _commProfileSetId;
    }

    public void setCommProfileSetId(String commProfileSetId) {
        _commProfileSetId = commProfileSetId;
    }

    public String getCommProfileSetName() {
        return _commProfileSetName;
    }

    public void setCommProfileSetName(String commProfileSetName) {
        _commProfileSetName = commProfileSetName;
    }

    public String getShortCode() {
        return _shortCode;
    }

    public void setShortCode(String shortCode) {
        _shortCode = shortCode;
    }

    public String getSetVersion() {
        return _setVersion;
    }

    public void setSetVersion(String setVersion) {
        _setVersion = setVersion;
    }

    public Timestamp getApplicableFrom() {
        return _applicableFrom;
    }

    public void setApplicableFrom(Timestamp applicableFrom) {
        _applicableFrom = applicableFrom;
    }

    public long getMinTransferValue() {
        return _minTransferValue;
    }

    public void setMinTransferValue(long minTransferValue) {
        _minTransferValue = minTransferValue;
    }

    public long getMaxTransferValue() {
        return _maxTransferValue;
    }

    public void setMaxTransferValue(long maxTransferValue) {
        _maxTransferValue = maxTransferValue;
    }

    public long getTransferMultipleOff() {
        return _transferMultipleOff;
    }

    public void setTransferMultipleOff(long transferMultipleOff) {
        _transferMultipleOff = transferMultipleOff;
    }

    public String getTaxOnChannelTransfer() {
        return _taxOnChannelTransfer;
    }

    public void setTaxOnChannelTransfer(String taxOnChannelTransfer) {
        _taxOnChannelTransfer = taxOnChannelTransfer;
    }

    public String getTaxOnFOCApplicable() {
        return _taxOnFOCApplicable;
    }

    public void setTaxOnFOCApplicable(String taxOnFOCApplicable) {
        _taxOnFOCApplicable = taxOnFOCApplicable;
    }

    public long getStartRange() {
        return _startRange;
    }

    public void setStartRange(long startRange) {
        _startRange = startRange;
    }

    public String getStartRangeAsString() {
        return _startRangeAsString;
    }

    public void setStartRangeAsString(String startRangeAsString) {
        _startRangeAsString = startRangeAsString;
    }

    public long getEndRange() {
        return _endRange;
    }

    public void setEndRange(long endRange) {
        _endRange = endRange;
    }

    public String getEndRangeAsString() {
        return _endRangeAsString;
    }

    public void setEndRangeAsString(String endRangeAsString) {
        _endRangeAsString = endRangeAsString;
    }

    public String getCommType() {
        return _commType;
    }

    public void setCommType(String commType) {
        _commType = commType;
    }

    public double getCommRate() {
        return _commRate;
    }

    public void setCommRate(double commRate) {
        _commRate = commRate;
    }

    public String getCommRateAsString() {
        return _commRateAsString;
    }

    public void setCommRateAsString(String commRateAsString) {
        _commRateAsString = commRateAsString;
    }

    public String getTax1Type() {
        return _tax1Type;
    }

    public void setTax1Type(String tax1Type) {
        _tax1Type = tax1Type;
    }

    public double getTax1Rate() {
        return _tax1Rate;
    }

    public void setTax1Rate(double tax1Rate) {
        _tax1Rate = tax1Rate;
    }

    public String getTax1RateAsString() {
        return _tax1RateAsString;
    }

    public void setTax1RateAsString(String tax1RateAsString) {
        _tax1RateAsString = tax1RateAsString;
    }

    public String getTax2Type() {
        return _tax2Type;
    }

    public void setTax2Type(String tax2Type) {
        _tax2Type = tax2Type;
    }

    public double getTax2Rate() {
        return _tax2Rate;
    }

    public void setTax2Rate(double tax2Rate) {
        _tax2Rate = tax2Rate;
    }

    public String getTax2RateAsString() {
        return _tax2RateAsString;
    }

    public void setTax2RateAsString(String tax2RateAsString) {
        _tax2RateAsString = tax2RateAsString;
    }

    public String getTax3Type() {
        return _tax3Type;
    }

    public void setTax3Type(String tax3Type) {
        _tax3Type = tax3Type;
    }

    public double getTax3Rate() {
        return _tax3Rate;
    }

    public void setTax3Rate(double tax3Rate) {
        _tax3Rate = tax3Rate;
    }

    public String getTax3RateAsString() {
        return _tax3RateAsString;
    }

    public void setTax3RateAsString(String tax3RateAsString) {
        _tax3RateAsString = tax3RateAsString;
    }

    public String getProductCode() {
        return _productCode;
    }

    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    public String getMinTransferValueAsString() {
        return _minTransferValueAsString;
    }

    public void setMinTransferValueAsString(String minTransferValueAsString) {
        _minTransferValueAsString = minTransferValueAsString;
    }

    public String getMaxTransferValueAsString() {
        return _maxTransferValueAsString;
    }

    public void setMaxTransferValueAsString(String maxTransferValueAsString) {
        _maxTransferValueAsString = maxTransferValueAsString;
    }

    public String getTransferMultipleOffAsString() {
        return _transferMultipleOffAsString;
    }

    public void setTransferMultipleOffAsString(String transferMultipleOffAsString) {
        _transferMultipleOffAsString = transferMultipleOffAsString;
    }

    public String getDomainCode() {
        return _domainCode;
    }

    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
    }

    public String getCategoryCode() {
        return _categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    public String getUserid() {
        return _userid;
    }

    public void setUserid(String userid) {
        _userid = userid;
    }

    public boolean isVersionUpdated() {
        return _versionUpdated;
    }

    public void setVersionUpdated(boolean versionUpdated) {
        _versionUpdated = versionUpdated;
    }

    public String getGrphDomainCode() {
        return _grphDomainCode;
    }

    public void setGrphDomainCode(String grphDomainCode) {
        _grphDomainCode = grphDomainCode;
    }

    public String getGradeCode() {
        return _gradeCode;
    }

    public void setGradeCode(String gradeCode) {
        _gradeCode = gradeCode;
    }
    /**
	 * @return the transferMultipleOffInDouble
	 */
	public double getTransferMultipleOffInDouble() {
		return _transferMultipleOffInDouble;
	}


	/**
	 * @param transferMultipleOffInDouble the transferMultipleOffInDouble to set
	 */
	public void setTransferMultipleOffInDouble(double transferMultipleOffInDouble) {
		_transferMultipleOffInDouble = transferMultipleOffInDouble;
	}
}
