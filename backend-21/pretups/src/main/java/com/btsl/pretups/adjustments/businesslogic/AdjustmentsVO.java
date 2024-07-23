package com.btsl.pretups.adjustments.businesslogic;

/*
 * @(#)AdjustmentsVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 05/09/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Travelling object for Adjustments
 */

import java.io.Serializable;
import java.util.Date;


public class AdjustmentsVO implements Serializable {

    private String adjustmentID;
    private String module;
    private String networkCode;
    private String networkCodeFor;
    private String adjustmentType;
    private String entryType;
    private Date adjustmentDate;
    private String userID;
    private String userCategory;
    private String productCode;
    private String serviceType;
    private long transferValue;
    private String marginType;
    private double marginRate;
    private long marginAmount;
    private String tax1Type;
    private double tax1Rate;
    private long tax1Value;
    private String tax2Type;
    private double tax2Rate;
    private long tax2Value;
    private String tax3Type;
    private double tax3Rate;
    private long tax3Value;
    private double differentialFactor;
    private long previousBalance;
    private long postBalance;
    private String referenceID;
    private String stockUpdated;
    private String createdBy;
    private Date createdOn;
    private String modifiedBy;
    private Date modifiedOn;
    private String addnlCommProfileDetailID;
    private String userMSISDN;
    private String subService;
    // commission type
    private String commisssionType = null;
    
    private String sequenceId = null;
    
	private String previousAdjustmentId;
    private Date otfApplicableFrom;
    private Date otfApplicableTo;
    private String otfType;
    private String otfTimeSlab;
    private String otfValue;
    private double otfRate;
    private String otfDetailType;
	private String addCommProfileOTFDetailID;
	private String otfTypePctOrAMt;
	private long otfAmount;
	
	public long getOtfAmount() {
		return otfAmount;
	}

	public void setOtfAmount(long otfAmount) {
		this.otfAmount = otfAmount;
	}

	public String getOtfTypePctOrAMt() {
		return otfTypePctOrAMt;
	}

	public void setOtfTypePctOrAMt(String otfTypePctOrAMt) {
		this.otfTypePctOrAMt = otfTypePctOrAMt;
	}


    
    public String getOtfDetailType() {
		return otfDetailType;
	}

	public void setOtfDetailType(String otfDetailType) {
		this.otfDetailType = otfDetailType;
	}

    
    public String getAddCommProfileOTFDetailID() {
		return addCommProfileOTFDetailID;
	}

	public void setAddCommProfileOTFDetailID(String addCommProfileOTFDetailID) {
		this.addCommProfileOTFDetailID = addCommProfileOTFDetailID;
	}

	public String getOtfValue() {
		return otfValue;
	}

	public void setOtfValue(String otfValue) {
		this.otfValue = otfValue;
	}

	public double getOtfRate() {
		return otfRate;
	}

	public void setOtfRate(double otfRate) {
		this.otfRate = otfRate;
	}

	public String getPreviousAdjustmentId() {
		return previousAdjustmentId;
	}
	public void setPreviousAdjustmentId(String previousAdjustmentId) {
		this.previousAdjustmentId = previousAdjustmentId;
	}

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getCommisssionType() {
        return commisssionType;
    }

    public void setCommisssionType(String commisssionType) {
        this.commisssionType = commisssionType;
    }

    public Date getAdjustmentDate() {
        return adjustmentDate;
    }

    public void setAdjustmentDate(Date adjustmentDate) {
        this.adjustmentDate = adjustmentDate;
    }

    public String getAdjustmentID() {
        return adjustmentID;
    }

    public void setAdjustmentID(String adjustmentID) {
    	this.adjustmentID = adjustmentID;
    }

    public String getAdjustmentType() {
        return adjustmentType;
    }

    public void setAdjustmentType(String adjustmentType) {
    	this.adjustmentType = adjustmentType;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
    	this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
    	this.createdOn = createdOn;
    }

    public double getDifferentialFactor() {
        return differentialFactor;
    }

    public void setDifferentialFactor(double differentialFactor) {
    	this.differentialFactor = differentialFactor;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
    	this.entryType = entryType;
    }

    public long getMarginAmount() {
        return marginAmount;
    }

    public void setMarginAmount(long marginAmount) {
    	this.marginAmount = marginAmount;
    }

    public double getMarginRate() {
        return marginRate;
    }

    public void setMarginRate(double marginRate) {
    	this.marginRate = marginRate;
    }

    public String getMarginType() {
        return marginType;
    }

    public void setMarginType(String marginType) {
    	this.marginType = marginType;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
    	this.modifiedBy = modifiedBy;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
    	this.modifiedOn = modifiedOn;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
    	this.module = module;
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public void setNetworkCode(String networkCode) {
    	this.networkCode = networkCode;
    }

    public String getNetworkCodeFor() {
        return networkCodeFor;
    }

    public void setNetworkCodeFor(String networkCodeFor) {
    	this.networkCodeFor = networkCodeFor;
    }

    public String getStockUpdated() {
        return stockUpdated;
    }

    public void setStockUpdated(String networkStockUpdated) {
        stockUpdated = networkStockUpdated;
    }

    public long getPostBalance() {
        return postBalance;
    }

    public void setPostBalance(long postBalance) {
    	this.postBalance = postBalance;
    }

    public long getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(long previousBalance) {
    	this.previousBalance = previousBalance;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
    	this.productCode = productCode;
    }

    public String getReferenceID() {
        return referenceID;
    }

    public void setReferenceID(String referenceID) {
    	this.referenceID = referenceID;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
    	this.serviceType = serviceType;
    }

    public double getTax1Rate() {
        return tax1Rate;
    }

    public void setTax1Rate(double tax1Rate) {
    	this. tax1Rate = tax1Rate;
    }

    public String getTax1Type() {
        return tax1Type;
    }

    public void setTax1Type(String tax1Type) {
    	this.tax1Type = tax1Type;
    }

    public long getTax1Value() {
        return tax1Value;
    }

    public void setTax1Value(long tax1Value) {
    	this.tax1Value = tax1Value;
    }

    public double getTax2Rate() {
        return tax2Rate;
    }

    public void setTax2Rate(double tax2Rate) {
    	this.tax2Rate = tax2Rate;
    }

    public String getTax2Type() {
        return tax2Type;
    }

    public void setTax2Type(String tax2Type) {
    	this.tax2Type = tax2Type;
    }

    public long getTax2Value() {
        return tax2Value;
    }

    public void setTax2Value(long tax2Value) {
    	this.tax2Value = tax2Value;
    }

    public long getTransferValue() {
        return transferValue;
    }

    public void setTransferValue(long transferValue) {
    	this.transferValue = transferValue;
    }

    public String getUserCategory() {
        return userCategory;
    }

    public void setUserCategory(String userCategory) {
    	this.userCategory = userCategory;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
    	this. userID = userID;
    }

    public double getTax3Rate() {
        return tax3Rate;
    }

    public void setTax3Rate(double tax3Rate) {
    	this.tax3Rate = tax3Rate;
    }

    public String getTax3Type() {
        return tax3Type;
    }

    public void setTax3Type(String tax3Type) {
    	this.tax3Type = tax3Type;
    }

    public long getTax3Value() {
        return tax3Value;
    }

    public void setTax3Value(long tax3Value) {
    	this.tax3Value = tax3Value;
    }

    public String getAddnlCommProfileDetailID() {
        return addnlCommProfileDetailID;
    }

    public void setAddnlCommProfileDetailID(String addnlCommProfileDetailID) {
    	this.addnlCommProfileDetailID = addnlCommProfileDetailID;
    }

    public String getUserMSISDN() {
        return this.userMSISDN;
    }

    public void setUserMSISDN(String userMSISDN) {
        this.userMSISDN = userMSISDN;
    }

    public String getSubService() {
        return subService;
    }

    public void setSubService(String subService) {
    	this.subService = subService;
    }
  
	public Date getOtfApplicableFrom() {
		return otfApplicableFrom;
	}

	public void setOtfApplicableFrom(Date otfApplicableFrom) {
		this.otfApplicableFrom = otfApplicableFrom;
	}

	public Date getOtfApplicableTo() {
		return otfApplicableTo;
	}

	public void setOtfApplicableTo(Date otfApplicableTo) {
		this.otfApplicableTo = otfApplicableTo;
	}

	public String getOtfType() {
		return otfType;
	}

	public void setOtfType(String otfType) {
		this.otfType = otfType;
	}


	public String getOtfTimeSlab() {
		return otfTimeSlab;
	}

	public void setOtfTimeSlab(String otfTimeSlab) {
		this.otfTimeSlab = otfTimeSlab;
	}
	
}
