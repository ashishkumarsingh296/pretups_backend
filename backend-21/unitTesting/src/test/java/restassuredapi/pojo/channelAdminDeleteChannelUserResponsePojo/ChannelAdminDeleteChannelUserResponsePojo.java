package restassuredapi.pojo.channelAdminDeleteChannelUserResponsePojo;

import java.util.List;

public class ChannelAdminDeleteChannelUserResponsePojo {

	
	public String service;
	public Object referenceId;
    public String status;
    public String messageCode;
    public String message;
    public Object errorMap;
    public List<Object> successList;
    
    public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public Object getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(Object referenceId) {
		this.referenceId = referenceId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getErrorMap() {
		return errorMap;
	}
	public void setErrorMap(Object errorMap) {
		this.errorMap = errorMap;
	}
	public List<Object> getSuccessList() {
		return successList;
	}
	public void setSuccessList(List<Object> successList) {
		this.successList = successList;
	}
    
}
