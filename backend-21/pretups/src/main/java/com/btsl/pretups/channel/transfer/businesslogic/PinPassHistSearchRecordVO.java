package com.btsl.pretups.channel.transfer.businesslogic;

public class PinPassHistSearchRecordVO {
	
	private String userName;
	private String msisdnOrLoginID;
	private String moidifiedBy;
	private String modifiedOn;
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getMoidifiedBy() {
		return moidifiedBy;
	}
	public void setMoidifiedBy(String moidifiedBy) {
		this.moidifiedBy = moidifiedBy;
	}
	public String getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public String getMsisdnOrLoginID() {
		return msisdnOrLoginID;
	}
	public void setMsisdnOrLoginID(String msisdnOrLoginID) {
		this.msisdnOrLoginID = msisdnOrLoginID;
	}
	


}
