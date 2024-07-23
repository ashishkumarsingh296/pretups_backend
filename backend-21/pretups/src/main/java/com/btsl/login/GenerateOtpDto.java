package com.btsl.login;

import com.btsl.util.BTSLUtil;

public class GenerateOtpDto {
	
	private String otp;
	private int resendDuration;
	private int resentCount;
	
	
   public GenerateOtpDto() {
	}
	
	public GenerateOtpDto(String otp, int resendDuration, int resentCount) {
		
		this.otp = otp;
		this.resendDuration = resendDuration;
		this.resentCount = resentCount;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public int getResendDuration() {
		return resendDuration;
	}
	public void setResendDuration(int resendDuration) {
		this.resendDuration = resendDuration;
	}
	public int getResentCount() {
		return resentCount;
	}
	public void setResentCount(int resentCount) {
		this.resentCount = resentCount;
	}

	@Override
	public String toString() {
		return "GenerateOtpDto [otp=" + BTSLUtil.maskParam(otp) + ", resendDuration=" + resendDuration + ", resentCount=" + resentCount
				+ "]";
	}
}
