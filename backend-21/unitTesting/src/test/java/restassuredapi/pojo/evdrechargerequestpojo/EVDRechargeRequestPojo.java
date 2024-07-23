package restassuredapi.pojo.evdrechargerequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EVDRechargeRequestPojo {
	


	@JsonProperty("data")
	EVDRechargeDetails data;

	// Getter Methods

	

	@JsonProperty("data")
	public EVDRechargeDetails getData() {
		return data;
	}

	// Setter Methods

	

	public void setData(EVDRechargeDetails data) {
		this.data = data;
	}



}
