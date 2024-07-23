package com.btsl.pretups.channel.profile.businesslogic;

/*
 * @(#)FetchUserDetailsResponseVO.java
 * Traveling object for all users details object
 * 
 * CBCcommSlabDetVO
 *

 */
public class CACDetails {
	

	private String cacDetailValue;
	private String cacDetailType;
	private String cacDetailRate;
	public String getCacDetailValue() {
		return cacDetailValue;
	}
	public void setCacDetailValue(String cacDetailValue) {
		this.cacDetailValue = cacDetailValue;
	}
	public String getCacDetailType() {
		return cacDetailType;
	}
	public void setCacDetailType(String cacDetailType) {
		this.cacDetailType = cacDetailType;
	}
	public String getCacDetailRate() {
		return cacDetailRate;
	}
	public void setCacDetailRate(String cacDetailRate) {
		this.cacDetailRate = cacDetailRate;
	}
	
	
	@Override
	public String toString() {
		return "CACDetails [cacDetailValue=" + cacDetailValue + ", cacDetailType=" + cacDetailType + ", cacDetailRate=" + cacDetailRate
				+ "]";
	}
	
}
