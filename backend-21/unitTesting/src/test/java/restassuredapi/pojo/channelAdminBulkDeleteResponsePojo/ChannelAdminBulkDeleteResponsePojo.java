package restassuredapi.pojo.channelAdminBulkDeleteResponsePojo;

import java.util.ArrayList;

public class ChannelAdminBulkDeleteResponsePojo {
	
	private Object service;
	private Object referenceId;
	private String status;
	private String messageCode;
	private String message;
	private ErrorMap errorMap;
	private ArrayList<Object> successList;
	private Object fileName;
	
	public Object getService() {
		return service;
	}
	public void setService(Object service) {
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
	public ErrorMap getErrorMap() {
		return errorMap;
	}
	public void setErrorMap(ErrorMap errorMap) {
		this.errorMap = errorMap;
	}
	public ArrayList<Object> getSuccessList() {
		return successList;
	}
	public void setSuccessList(ArrayList<Object> successList) {
		this.successList = successList;
	}
	public Object getFileName() {
		return fileName;
	}
	public void setFileName(Object fileName) {
		this.fileName = fileName;
	}
	
	

}
