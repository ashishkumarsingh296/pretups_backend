package restassuredapi.pojo.superAdminAddOperatorUserRequestPojo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CreateOperatorUserMsisdn {
	
	private String phoneNo;
	private String pin;
	private String confirmPin;
	private Object description;
	private String isprimary;
	
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public String getConfirmPin() {
		return confirmPin;
	}
	public void setConfirmPin(String confirmPin) {
		this.confirmPin = confirmPin;
	}
	public Object getDescription() {
		return description;
	}
	public void setDescription(Object description) {
		this.description = description;
	}
	public String getIsprimary() {
		return isprimary;
	}
	public void setIsprimary(String isprimary) {
		this.isprimary = isprimary;
	}

}
