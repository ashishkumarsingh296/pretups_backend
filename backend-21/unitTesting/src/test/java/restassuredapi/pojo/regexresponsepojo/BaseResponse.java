package restassuredapi.pojo.regexresponsepojo;


import com.fasterxml.jackson.annotation.JsonProperty;


public class BaseResponse {

	    @JsonProperty("status")
		private int status;
	    @JsonProperty("messageCode")
		private String messageCode;
	    @JsonProperty("message")
		private String message;
	    
	    @JsonProperty("status")
		public int getStatus() {
			return status;
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
	
}
