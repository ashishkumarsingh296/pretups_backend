package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.ArrayList;
import java.util.Arrays;

import com.btsl.user.businesslogic.UserPhoneVO;

/*
 *  @(#)LoginDetailsVO.java
 * Traveling object for users login details
 */
public class LoginDetailsVO {
	private String loginId;
	private String allowedIp;
	private String[] allowedDays;
	private String allowedFromTime;
	private String allowedToTime;
	private String primaryMsisdn;
	private ArrayList<String> secMsisdn;
	private ArrayList<PhoneDetails> secMsisdnWithDetail;
	private String isPrimary;  //return 'Y' or 'N'
	private String profileName;
	private String description;
	private int invalidPinCount;
	private String userPhoneId;
	private String userCode;
	private String networkCode;
	private ArrayList userPhoneList;
	private String networkName;
	private ArrayList networkLis;
	private int invalidPasswordCount; 
	private String pin;
	
	
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}

	public int getInvalidPinCount() {
		return invalidPinCount;
	}
	public void setInvalidPinCount(int invalidPinCount) {
		this.invalidPinCount = invalidPinCount;
	}
	public String getUserPhoneId() {
		return userPhoneId;
	}
	public void setUserPhoneId(String userPhoneId) {
		this.userPhoneId = userPhoneId;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public String getAllowedIp() {
		return allowedIp;
	}
	public void setAllowedIp(String allowedIp) {
		this.allowedIp = allowedIp;
	}
	public String[] getAllowedDays() {
		return allowedDays;
	}
	public void setAllowedDays(String[] allowedDays) {
		this.allowedDays = allowedDays;
	}
	public String getAllowedFromTime() {
		return allowedFromTime;
	}
	public void setAllowedFromTime(String allowedFromTime) {
		this.allowedFromTime = allowedFromTime;
	}
	public String getAllowedToTime() {
		return allowedToTime;
	}
	public void setAllowedToTime(String allowedToTime) {
		this.allowedToTime = allowedToTime;
	}
	public String getPrimaryMsisdn() {
		return primaryMsisdn;
	}
	public void setPrimaryMsisdn(String primaryMsisdn) {
		this.primaryMsisdn = primaryMsisdn;
	}

	public ArrayList<String> getSecMsisdn() {
		return secMsisdn;
	}
	public void setSecMsisdn(ArrayList<String> secMsisdn) {
		this.secMsisdn = secMsisdn;
	}
	public String getIsPrimary() {
		return isPrimary;
	}
	public void setIsPrimary(String isPrimary) {
		this.isPrimary = isPrimary;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ArrayList getUserPhoneList() {
		return userPhoneList;
	}
	public void setUserPhoneList(ArrayList userPhoneList) {
		this.userPhoneList = userPhoneList;
	}
	public String getNetworkName() {
		return networkName;
	}
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	public ArrayList getNetworkLis() {
		return networkLis;
	}
	public void setNetworkLis(ArrayList networkLis) {
		this.networkLis = networkLis;
	}
	
	public int getInvalidPasswordCount() {
		return invalidPasswordCount;
	}
	public void setInvalidPasswordCount(int invalidPasswordCount) {
		this.invalidPasswordCount = invalidPasswordCount;
	}
	
	
	public ArrayList<PhoneDetails> getSecMsisdnWithDetail() {
		return secMsisdnWithDetail;
	}
	public void setSecMsisdnWithDetail(ArrayList<PhoneDetails> secMsisdnWithDetail) {
		this.secMsisdnWithDetail = secMsisdnWithDetail;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LoginDetailsVO [loginId=").append(loginId).append(", allowedIp=").append(allowedIp)
				.append(", allowedDays=").append(Arrays.toString(allowedDays)).append(", allowedFromTime=")
				.append(allowedFromTime).append(", allowedToTime=").append(allowedToTime).append(", primaryMsisdn=")
				.append(primaryMsisdn).append(", secMsisdn=").append(secMsisdn).append(", secMsisdnWithDetail=")
				.append(secMsisdnWithDetail).append(", isPrimary=").append(isPrimary).append(", profileName=")
				.append(profileName).append(", description=").append(description).append(", invalidPinCount=")
				.append(invalidPinCount).append(", userPhoneId=").append(userPhoneId).append(", userCode=")
				.append(userCode).append(", networkCode=").append(networkCode).append(", userPhoneList=")
				.append(userPhoneList).append(", networkName=").append(networkName).append(", networkLis=")
				.append(networkLis).append(", invalidPasswordCount=").append(invalidPasswordCount).append(", pin=")
				.append(pin).append("]");
		return builder.toString();
	}
	
	
	
	
	
	
}
	class PhoneDetails{
		private String msisdn;
		private String profileName;
		private String isPrimary;
		private String desc;
		private int invalidPinCount;
		private String userPhoneId;
		private String pin;
		public String getMsisdn() {
			return msisdn;
		}
		public void setMsisdn(String msisdn) {
			this.msisdn = msisdn;
		}
		public String getProfileName() {
			return profileName;
		}
		public void setProfileName(String profileName) {
			this.profileName = profileName;
		}
		public String getIsPrimary() {
			return isPrimary;
		}
		public void setIsPrimary(String isPrimary) {
			this.isPrimary = isPrimary;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		public int getInvalidPinCount() {
			return invalidPinCount;
		}
		public void setInvalidPinCount(int invalidPinCount) {
			this.invalidPinCount = invalidPinCount;
		}
		public String getUserPhoneId() {
			return userPhoneId;
		}
		public void setUserPhoneId(String userPhoneId) {
			this.userPhoneId = userPhoneId;
		}
		
		public String getPin() {
			return pin;
		}
		public void setPin(String pin) {
			this.pin = pin;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("PhoneDetails [msisdn=").append(msisdn).append(", profileName=").append(profileName)
					.append(", isPrimary=").append(isPrimary).append(", desc=").append(desc)
					.append(", invalidPinCount=").append(invalidPinCount).append(", userPhoneId=").append(userPhoneId)
					.append(", pin=").append(pin).append("]");
			return builder.toString();
		}
		
		
		
	}
	
	


