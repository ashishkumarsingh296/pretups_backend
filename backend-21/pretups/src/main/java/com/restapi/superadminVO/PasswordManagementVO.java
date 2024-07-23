package com.restapi.superadminVO;

import com.btsl.pretups.user.businesslogic.ChannelUserVO;

public class PasswordManagementVO {
	
    private String msisdn = null;
    private String loginID = null;
    private String resetPassword = null;
    private String resertPin = null;
    private String remarks = null;
    private ChannelUserVO channelUserVO = null;
    
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
	public String getResetPassword() {
		return resetPassword;
	}
	public void setResetPassword(String resetPassword) {
		this.resetPassword = resetPassword;
	}
	public String getResetPin() {
		return resertPin;
	}
	public void setResetPin(String resertPin) {
		this.resertPin = resertPin;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public ChannelUserVO getChannelUserVO() {
		return channelUserVO;
	}
	public void setChannelUserVO(ChannelUserVO channelUserVO) {
		this.channelUserVO = channelUserVO;
	}
    
    
    

}
