package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;
import java.util.Date;


public class OTFDetailsVO implements Serializable {

	private String otfValue;
	private String otfType;
	private String otfRate;
	private String otfProfileID;
	private String otfDetailID;
	private String otfCountOrAmount;
	private Date otfApplicableFrom;
	private Date otfApplicableTo;
	private String otfApplicableFromStr;
    private String otfApplicableToStr;
	private Date origOtfApplicableTo;
	private String otfTimeSlab;
	private String otfTypePctOrAMt;
	private Object listOtfAllDetails;
	private int size;
    private String _domainCode;
	private String _categoryCode;
	private String _gradeCode = null;
    private String _grphDomainCode = null;
    private String _commProfileSetId;
    private String _commProfileSetName;
    private String _shortCode;
    private String _setVersion;
    private String commissionProfileType;
    private String paymentMode;
    private String _productCode;
    private String _batch_name = null;
    private String batchId;
	
	 
	private static final long serialVersionUID = 1L;
	public	OTFDetailsVO(){
		
	}
	public OTFDetailsVO(OTFDetailsVO otfdetails){
		this.otfProfileID = otfdetails.otfProfileID;
	    this.otfDetailID = otfdetails.otfDetailID;
		this.otfValue = otfdetails.otfValue;
		this.otfType = otfdetails.otfType;
		this.otfRate = otfdetails.otfRate;
		this.otfApplicableFromStr=otfdetails.otfApplicableFromStr;
        this.otfApplicableToStr=otfdetails.otfApplicableToStr;
        this.size = otfdetails.size;
        this.listOtfAllDetails=otfdetails.listOtfAllDetails;
        
	}
 
	@Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OTFDetailsVO Data ");
        sb.append("otfProfileID=" + otfProfileID + ",");
        sb.append("otfDetailID=" + otfDetailID + ",");
        sb.append("otfValue=" + otfValue + ",");
        sb.append("otfType=" + otfType + ",");
        sb.append("otfRate=" + otfRate + ",");
        sb.append("otfApplicableFrom=" + otfApplicableFrom + ",");
        sb.append("otfApplicableTo=" + otfApplicableTo + ",");
        sb.append("otfApplicableFromStr=" + otfApplicableFromStr + ",");
        sb.append("otfApplicableToStr=" + otfApplicableToStr + ",");
        sb.append("otfTimeSlab=" + otfTimeSlab + ",");
        sb.append("otfTypePctOrAMt=" + otfTypePctOrAMt + ",");
        return sb.toString();
    }
	
	public String getOtfCountOrAmount() {
		return otfCountOrAmount;
	}
	public void setOtfCountOrAmount(String otfCountOrAmount) {
		this.otfCountOrAmount = otfCountOrAmount;
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
	public String getOtfTimeSlab() {
		return otfTimeSlab;
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
	public void setOtfTimeSlab(String otfTimeSlab) {
		this.otfTimeSlab = otfTimeSlab;
	}
	public String getOtfValue() {
		return otfValue;
	}
	public long getOtfValueLong() {
		return Long.parseLong(otfValue);
	}
	public void setOtfValue(String otfValue) {
		this.otfValue = otfValue;
	}
	public String getOtfType() {
		return otfType;
	}
	public void setOtfType(String otfType) {
		this.otfType = otfType;
	}
	public String getOtfRate() {
		return otfRate;
	}
	public double getOtfRateDouble() {
		return Double.parseDouble(otfRate);
	}
	public void setOtfRate(String otfRate) {
		this.otfRate = otfRate;
	}
	public String getOtfProfileID() {
		return otfProfileID;
	}
	public void setOtfProfileID(String profileOtfID) {
		this.otfProfileID = profileOtfID;
	}
	public String getOtfDetailID() {
		return otfDetailID;
	}
	public void setOtfDetailID(String otfDetailID) {
		this.otfDetailID = otfDetailID;
	}
	public Date getOrigOtfApplicableTo() {
		return origOtfApplicableTo;
	}
	public void setOrigOtfApplicableTo(Date origOtfApplicableTo) {
		this.origOtfApplicableTo = origOtfApplicableTo;
	}
	public String getOtfTypePctOrAMt() {
		return otfTypePctOrAMt;
	}
	public void setOtfTypePctOrAMt(String otfTypePctOrAMt) {
		this.otfTypePctOrAMt = otfTypePctOrAMt;
	}
	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String get_domainCode() {
		return _domainCode;
	}

	public void setDomainCode(String _domainCode) {
		this._domainCode = _domainCode;
	}

	public String getCategoryCode() {
		return _categoryCode;
	}

	public void setCategoryCode(String _categoryCode) {
		this._categoryCode = _categoryCode;
	}

	public String getGradeCode() {
		return _gradeCode;
	}

	public void setGradeCode(String _gradeCode) {
		this._gradeCode = _gradeCode;
	}

	public String getGrphDomainCode() {
		return _grphDomainCode;
	}

	public void setGrphDomainCode(String _grphDomainCode) {
		this._grphDomainCode = _grphDomainCode;
	}

	public String getCommProfileSetId() {
		return _commProfileSetId;
	}

	public void setCommProfileSetId(String _commProfileSetId) {
		this._commProfileSetId = _commProfileSetId;
	}

	public String getCommProfileSetName() {
		return _commProfileSetName;
	}

	public void setCommProfileSetName(String _commProfileSetName) {
		this._commProfileSetName = _commProfileSetName;
	}

	public String getShortCode() {
		return _shortCode;
	}

	public void setShortCode(String _shortCode) {
		this._shortCode = _shortCode;
	}

	public String getSetVersion() {
		return _setVersion;
	}

	public void setSetVersion(String _setVersion) {
		this._setVersion = _setVersion;
	}

	public String getProductCode() {
		return _productCode;
	}

	public void setProductCode(String _productCode) {
		this._productCode = _productCode;
	}
    

	public String getCommissionProfileType() {
		return commissionProfileType;
	}

	public void setCommissionProfileType(String commissionProfileType) {
		this.commissionProfileType = commissionProfileType;
	}
}
	
	

