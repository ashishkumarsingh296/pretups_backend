package com.btsl.common;



import com.btsl.pretups.common.SchemaConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * @author deepa.shyam
 *
 */
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {

	    @JsonProperty("status")

		private int status;
	    @JsonProperty("messageCode")
		@Schema(pattern = SchemaConstants.STRING_INPUT_PATTERN,maxLength = SchemaConstants.STRING_MAX_SIZE)
		private String messageCode;
	    @JsonProperty("message")
		@Schema(pattern = SchemaConstants.STRING_INPUT_PATTERN)
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
		@Schema(pattern = SchemaConstants.STRING_INPUT_PATTERN)
	    private String transactionId;
	    
	    @JsonProperty("status")
		@io.swagger.v3.oas.annotations.media.Schema(example = SchemaConstants.STATUS_EXAMPLE, description = SchemaConstants.STATUS_DESC)
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
		@io.swagger.v3.oas.annotations.media.Schema(example = SchemaConstants.MESSAGE_EXAMPLE,pattern = SchemaConstants.STRING_INPUT_PATTERN,maxLength = SchemaConstants.STRING_MAX_SIZE)
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		
		@JsonProperty("transactionId")
		@io.swagger.v3.oas.annotations.media.Schema(example = SchemaConstants.TRANSACTION_ID_EXAMPLE,maxLength = SchemaConstants.STRING_MAX_SIZE,pattern = SchemaConstants.STRING_INPUT_PATTERN)
		public String getTransactionId() {
			return transactionId;
		}
		public void setTransactionId(String transactionId) {
			this.transactionId = transactionId;
		}
	
}
