package restassuredapi.pojo;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BaseResponsePojo {

    @JsonProperty("status")
	private int status;
    @JsonProperty("messageCode")
	private String messageCode;
    @JsonProperty("message")
	private String message;
    @JsonProperty("errorMap")
    private ErrorMap errorMap;
    @JsonProperty("errorMap")

    public ErrorMap getErrorMap() {
		return errorMap;
	}
	public void setErrorMap(ErrorMap errorMap) {
		this.errorMap = errorMap;
	}
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private String transactionId;
    
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
	
	@JsonProperty("transactionId")
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

}
