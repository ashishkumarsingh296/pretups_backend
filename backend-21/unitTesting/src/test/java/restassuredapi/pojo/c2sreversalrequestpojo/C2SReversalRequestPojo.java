package restassuredapi.pojo.c2sreversalrequestpojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class C2SReversalRequestPojo {
	@JsonProperty("data")
	List<C2SRechargeDetails> data;

	@JsonProperty("data")
	public List<C2SRechargeDetails> getData() {
		return data;
	}

	public void setData(List<C2SRechargeDetails> data) {
		this.data = data;
	}
	@JsonProperty("senderPin")
	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
	@JsonProperty("senderPin")
	public String pin;

}
