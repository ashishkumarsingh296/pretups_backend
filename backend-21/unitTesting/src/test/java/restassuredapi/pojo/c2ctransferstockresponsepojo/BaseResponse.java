package restassuredapi.pojo.c2ctransferstockresponsepojo;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


public class BaseResponse {

	    @JsonProperty("status")
		private int status;
	    @JsonProperty("messageCode")
		private String messageCode;
	    @JsonProperty("message")
		private String message;
	    @JsonInclude(JsonInclude.Include.NON_NULL)
	    @JsonProperty("txnid")
	    private String txnid;
	    @JsonProperty("receiverTrfValue")
	    private String receiverTrfValue;
	    @JsonProperty("receiveraccessValue")
	    private String receiveraccessValue;
		public int getStatus() {
			return status;
		}
		@JsonProperty("receiverTrfValue")
		public String getReceiverTrfValue() {
			return receiverTrfValue;
		}
		public void setReceiverTrfValue(String receiverTrfValue) {
			this.receiverTrfValue = receiverTrfValue;
		}
		@JsonProperty("receiveraccessValue")
		public String getReceiveraccessValue() {
			return receiveraccessValue;
		}
		public void setReceiveraccessValue(String receiveraccessValue) {
			this.receiveraccessValue = receiveraccessValue;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		
		@JsonProperty("messageCode")
		public String getMessageCode() {
			return messageCode;
		}
		public void setMessageCode(String messageCode) {
			this.messageCode = messageCode;
		}
		
		@JsonProperty("message")
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
	
		@JsonProperty("txnid")
		public String getTransactionId() {
			return txnid;
		}
		public void setTransactionId(String txnid) {
			this.txnid = txnid;
		}
}
