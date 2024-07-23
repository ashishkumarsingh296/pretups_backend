package restassuredapi.pojo.internetrechargerequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternetRechargeRequestPojo {
	


	@JsonProperty("data")
	InternetRechargeDetails data;

	// Getter Methods

	

	@JsonProperty("data")
	public InternetRechargeDetails getData() {
		return data;
	}

	// Setter Methods

	

	public void setData(InternetRechargeDetails data) {
		this.data = data;
	}



}
