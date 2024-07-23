package restassuredapi.pojo.addchanneluserrequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Msisdn {
	
	@JsonProperty("phoneNo")
	String phoneNo;
	
	@JsonProperty("pin")
	String pin;
	
	@JsonProperty("pin")
	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
	@JsonProperty("isprimary")
	public String getIsprimary() {
		return isprimary;
	}

	public void setIsprimary(String isprimary) {
		this.isprimary = isprimary;
	}

	@JsonProperty("isprimary")
	String isprimary;

	@JsonProperty("phoneNo")
	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	
	@JsonProperty("description")
	String description;
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	@JsonProperty("stkProfile")
	public String getStkProfile() {
		return stkProfile;
	}

	public void setStkProfile(String stkProfile) {
		this.stkProfile = stkProfile;
	}

	@JsonProperty("stkProfile")
	String stkProfile;
	
	


}
