package com.pretupsControllers.commissionprofile;

public class UserOTFCountsVO {
	
	private static int _otfCount;
	private static long _otfValue;
	private static String _userID;
	private static String _adnlComOTFDetailId;
	private static String _baseComOTFDetailId;
	
	public void setUserID(String userID) {
		_userID = userID;
	}
	
	public String getUserID() {
		return _userID;
	}
	
	public void setAdnlComOTFDetailId(String adnlComOTFDetailId) {
		_adnlComOTFDetailId = adnlComOTFDetailId;
	}
	
	public String getAdnlComOTFDetailID() {
		return _adnlComOTFDetailId;
	}
	
	public String getBaseComOTFDetailId() {
		return _baseComOTFDetailId;
	}
	public void setBaseComOTFDetailId(String baseComOTFDetailId) {
		_baseComOTFDetailId = baseComOTFDetailId;
	}
	
	public void setOtfCount(int otfCount) {
		_otfCount = otfCount;
	}
	
	public int getOtfCount() {
		return _otfCount;
	}
	
	public void setOtfValue(long otfValue) {
		_otfValue = otfValue;
	}
	
	public long getOtfValue() {
		return _otfValue;
	}
	
}
