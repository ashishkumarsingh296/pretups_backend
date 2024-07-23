
package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.Serializable;
import java.util.Date;

/**
 * @author anshul.goyal2
 * 
 */
public class UserOtpVO implements Serializable {
   
	private String msisdn;
	private String userId;
	private String modifiedOn;
	private String invalidCount;
	private Date barredDate;
	private Date consumedOn;
	private String otpPin;
	private Date generatedOn; 
	
    public Date getGeneratedOn() {
		return generatedOn;
	}


	public void setGeneratedOn(Date generatedOn) {
		this.generatedOn = generatedOn;
	}


	public String getMsisdn() {
		return msisdn;
	}


	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getModifiedOn() {
		return modifiedOn;
	}


	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}


	public String getInvalidCount() {
		return invalidCount;
	}


	public void setInvalidCount(String invalidCount) {
		this.invalidCount = invalidCount;
	}


	public Date getBarredDate() {
		return barredDate;
	}


	public void setBarredDate(Date barredDate) {
		this.barredDate = barredDate;
	}


	public Date getConsumedOn() {
		return consumedOn;
	}


	public void setConsumedOn(Date consumedOn) {
		this.consumedOn = consumedOn;
	}


	public String getOtppin() {
		return otpPin;
	}


	public void setOtppin(String otpPin) {
		this.otpPin = otpPin;
	}


	@Override
  	public String toString() {
      	StringBuilder sbf = new StringBuilder();
      	 sbf.append("UserOtpVO [_userID=").append(userId);
      	 sbf.append(", modifiedOn=").append(modifiedOn);
      	 sbf.append(", msisdn=").append(msisdn);
      	 sbf.append(", invalidCount=").append(invalidCount);
      	 sbf.append(", barredDate=").append(barredDate);
      	 sbf.append(", consumedOn=").append(consumedOn);
      	 sbf.append(", otppin=").append(otpPin).append("]");
      	 return sbf.toString();
  	}
    
}
