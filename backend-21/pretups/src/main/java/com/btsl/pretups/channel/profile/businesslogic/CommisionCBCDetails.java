package com.btsl.pretups.channel.profile.businesslogic;

/*
 * @(#)FetchUserDetailsResponseVO.java
 * Traveling object for all users details object
 * 
 * @List<UserMsisdnUserIDVO>
 *

 */
public class CommisionCBCDetails  {

	private String otfValue;
	private String otfType;
	private String otfRate;
	public String getOtfValue() {
		return otfValue;
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
	public void setOtfRate(String otfRate) {
		this.otfRate = otfRate;
	}

	@Override
	public String toString() {
		return "CommisionCBCDetails [otfValue=" + otfValue + ", otfType=" + otfType + ", otfRate=" + otfRate
			  + "]";
	}
}
