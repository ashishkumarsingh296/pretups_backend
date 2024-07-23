package restassuredapi.pojo.prepaidrechargepojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrepaidRechargeRequestPojo {
	


	@JsonProperty("data")
	PrepaidRechargeDetails data;

	// Getter Methods

	

	@JsonProperty("data")
	public PrepaidRechargeDetails getData() {
		return data;
	}

	// Setter Methods

	

	public void setData(PrepaidRechargeDetails data) {
		this.data = data;
	}



}
