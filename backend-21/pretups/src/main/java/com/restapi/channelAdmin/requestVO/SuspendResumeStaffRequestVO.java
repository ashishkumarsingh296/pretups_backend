package com.restapi.channelAdmin.requestVO;

public class SuspendResumeStaffRequestVO {
	
	private String msisdn;
    private String loginID;
    
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getLoginID() {
		return loginID;
	}
	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	
	@Override
	public String toString() {
		return "SuspendResumeStaffRequestVO [msisdn=" + msisdn + ", loginID=" + loginID + "]";
	}
        
}
