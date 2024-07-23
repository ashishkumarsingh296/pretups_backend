package restassuredapi.pojo.giftrechargerequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;


public class GiftRechargeRequestPojo {
	


	@JsonProperty("data")
	GiftRechargeDetails data;

	// Getter Methods

	

	@JsonProperty("data")
	public GiftRechargeDetails getData() {
		return data;
	}

	// Setter Methods

	

	public void setData(GiftRechargeDetails data) {
		this.data = data;
	}



}
