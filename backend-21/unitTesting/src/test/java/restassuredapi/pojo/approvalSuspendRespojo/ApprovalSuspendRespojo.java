package restassuredapi.pojo.approvalSuspendRespojo;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "service",
    "referenceId",
    "status",
    "messageCode",
    "message",
    "errorMap",
    "successList",
    "changeStatus"
})
public class ApprovalSuspendRespojo {
	@JsonProperty("service")
	    public String service;
	@JsonProperty("referenceId")
	    public Object referenceId;
	@JsonProperty("status")
	    public String status;
	@JsonProperty("messageCode")
	    public String messageCode;
	@JsonProperty("message")
	    public String message;
	@JsonProperty("errorMap")
	    public Object errorMap;
	@JsonProperty("successList")
	    public ArrayList<Object> successList;
	@JsonProperty("changeStatus") 
	    public boolean changeStatus;
	    
	@JsonProperty("service")
		public String getService() {
			return service;
		}
	@JsonProperty("service")
		public void setService(String service) {
			this.service = service;
		}
	@JsonProperty("referenceId")
		public Object getReferenceId() {
			return referenceId;
		}
	@JsonProperty("referenceId")
		public void setReferenceId(Object referenceId) {
			this.referenceId = referenceId;
		}
	
	@JsonProperty("status")
		public String getStatus() {
			return status;
		}
	@JsonProperty("status")
		public void setStatus(String status) {
			this.status = status;
		}
	@JsonProperty("messageCode")
		public String getMessageCode() {
			return messageCode;
		}
	@JsonProperty("messageCode")
		public void setMessageCode(String messageCode) {
			this.messageCode = messageCode;
		}
	@JsonProperty("message")
		public String getMessage() {
			return message;
		}
	@JsonProperty("message")
		public void setMessage(String message) {
			this.message = message;
		}
	@JsonProperty("errorMap")
		public Object getErrorMap() {
			return errorMap;
		}
	@JsonProperty("errorMap")
		public void setErrorMap(Object errorMap) {
			this.errorMap = errorMap;
		}
	@JsonProperty("successList")
		public ArrayList<Object> getSuccessList() {
			return successList;
		}
	@JsonProperty("successList")
		public void setSuccessList(ArrayList<Object> successList) {
			this.successList = successList;
		}
	@JsonProperty("changeStatus")
		public boolean isChangeStatus() {
			return changeStatus;
		}
	
	@JsonProperty("changeStatus")
		public void setChangeStatus(boolean changeStatus) {
			this.changeStatus = changeStatus;
		}
}
