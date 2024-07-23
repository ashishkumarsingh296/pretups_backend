package restassuredapi.pojo.postpaidbillpaymentrequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostpaidBillPaymentRequestPojo {
	


	@JsonProperty("data")
	PostpaidBillPaymentDetails data;

	// Getter Methods

	

	@JsonProperty("data")
	public PostpaidBillPaymentDetails getData() {
		return data;
	}

	// Setter Methods

	

	public void setData(PostpaidBillPaymentDetails data) {
		this.data = data;
	}



}
