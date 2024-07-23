package restassuredapi.pojo.c2cenquiryresponsepojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import restassuredapi.pojo.c2CFileUploadApiResponsepojo.ErrorMap;
import restassuredapi.pojo.c2CFileUploadApiResponsepojo.SuccessList;



@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	 "service",
	  "referenceId",
	  "status",
	  "messageCode",
	  "message",
	  "errorMap",
	  "successList",
	  "transferList"
})

public class C2cAndO2cEnquiryResponsePojo {
	    @JsonProperty("service")
	    private String  service;
	    @JsonProperty("referenceId")
	    private  String  referenceId;
	    @JsonProperty("status")
	    private Integer status;
	    @JsonProperty("messageCode")
	    private  String messageCode;
	    @JsonProperty("message")
	    private String message;
	    @JsonProperty("errorMap")
	    private ErrorMap errorMap;
	    @JsonProperty("successList")
	    private List<SuccessList> successList;
	    @JsonProperty("transferList")
	    private List<Object> transferList ;
	    
	    @JsonProperty("transferListSize")
	    private Integer transferListSize;
	    
	   // "transferListSize": 6
	    @JsonProperty("service")
		public String getService() {
			return service;
		}
	    @JsonProperty("service")
		public void setService(String service) {
			this.service = service;
		}
	    @JsonProperty("referenceId")
		public String getReferenceId() {
			return referenceId;
		}
	    @JsonProperty("referenceId")
		public void setReferenceId(String referenceId) {
			this.referenceId = referenceId;
		}
	    @JsonProperty("status")
		public Integer getStatus() {
			return status;
		}
	    @JsonProperty("status")
		public void setStatus(Integer status) {
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
		public ErrorMap getErrorMap() {
			return errorMap;
		}
	    @JsonProperty("errorMap")
		public void setErrorMap(ErrorMap errorMap) {
			this.errorMap = errorMap;
		}
	    @JsonProperty("successList")
		public List<SuccessList> getSuccessList() {
			return successList;
		}
	    @JsonProperty("successList")
		public void setSuccessList(List<SuccessList> successList) {
			this.successList = successList;
		}
	    @JsonProperty("transferList")
		public List<Object> getTransferList() {
			return transferList;
		}
	    @JsonProperty("transferList")
		public void setTransferList(List<Object> transferList) {
			this.transferList = transferList;
		}
	    @JsonProperty("transferListSize")
		public Integer getTransferListSize() {
			return transferListSize;
		}
	    @JsonProperty("transferListSize")
		public void setTransferListSize(Integer transferListSize) {
			this.transferListSize = transferListSize;
		}
	    
	    
	    
	 
}
