package restassuredapi.pojo.fixedrechargerequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FixedRechargeRequestPojo {
	


	@JsonProperty("data")
	FixedRechargeDetails data;

	// Getter Methods

	

	@JsonProperty("data")
	public 	FixedRechargeDetails getData() {
		return data;
	}

	// Setter Methods

	

	public void setData(FixedRechargeDetails data) {
		this.data = data;
	}



}
