package com.btsl.user.businesslogic;



/**
 * Base Inner class for token validation RequestVO(s)
 * @author akhilesh.mittal1
 *
 */
public class OAuthUserData {
	
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String loginid;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String msisdn;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String password;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String pin;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String extcode;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String userid;
	

	public String getExtcode() {
		return extcode;
	}
	public void setExtcode(String extcode) {
		this.extcode = extcode;
	}
	public String getLoginid() {
		return loginid;
	}
	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	@Override
	public String toString() {
		return "OAuthUserData [loginid=" + loginid + ", msisdn=" + msisdn + ", password=" + password + ", pin=" + pin
				+ ", extcode=" + extcode + ", userid=" + userid + "]";
	}
	
	
	

}
