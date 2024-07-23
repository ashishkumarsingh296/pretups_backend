package com.pretupsControllers.commissionprofile;

import java.util.Date;

public class CommissionProfileDetailsVO {

	private static Date _otfApplicableFrom;
	private static Date _otfApplicableTo;
	private static String _OtfTimeSlab;
	private static String _baseCommProfileDetailID;
	private static String _baseCommProfileOTFDetailID;
	private static long _otfValue;
	private static double _otfRate;
	private static String _otfTypePctOrAMt;
	
	
	public void setOtfApplicableFrom(Date otfApplicableFrom) {
		_otfApplicableFrom = otfApplicableFrom;
	}
	
	public Date getOtfApplicableFrom() {
		return _otfApplicableFrom;
	}
	
	public void setOtfApplicableTo(Date otfApplicableTo) {
		_otfApplicableTo = otfApplicableTo;
	}
	
	public Date getOtfApplicableTo() {
		return _otfApplicableTo;
	}
	
	public void setOtfTimeSlab(String OtfTimeSlab) {
		_OtfTimeSlab = OtfTimeSlab;
	}
	
	public String getOtfTimeSlab() {
		return _OtfTimeSlab;
	}
	
	public void setBaseCommProfileDetailID(String baseCommProfileDetailID) {
		_baseCommProfileDetailID = baseCommProfileDetailID;
	}
	
	public String getBaseCommProfileDetailID() {
		return _baseCommProfileDetailID;
	}
	
	public String getBaseCommProfileOTFDetailID() {
		return _baseCommProfileOTFDetailID;
	}

	public void setBaseCommProfileOTFDetailID(String baseCommProfileOTFDetailID) {
		_baseCommProfileOTFDetailID = baseCommProfileOTFDetailID;
	}
	
	public void setOtfValue(long otfValue) {
		_otfValue = otfValue;
	}
	
	public long getOtfValue() {
		return _otfValue;
	}

	public void setOtfRate(double otfRate) {
		_otfRate = otfRate;
	}
	
	public double getOtfRate() {
		return _otfRate;
	}
	
    public String getOtfTypePctOrAMt() {
		return _otfTypePctOrAMt;
	}
	public void setOtfTypePctOrAMt(String otfTypePctOrAMt) {
		_otfTypePctOrAMt = otfTypePctOrAMt;
	}

}
