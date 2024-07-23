package restassuredapi.pojo.mvdrechargerequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MVDRechargeRequestPojo {
	


	@JsonProperty("data")
	MVDRechargeDetails data;

	// Getter Methods

	

	@JsonProperty("data")
	public MVDRechargeDetails getData() {
		return data;
	}

	// Setter Methods

	

	public void setData(MVDRechargeDetails data) {
		this.data = data;
	}



}
