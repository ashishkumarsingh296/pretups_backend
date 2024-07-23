package restassuredapi.pojo.fetchStaffUserDetailsResponsePojo;

import java.util.List;

public class FetchStaffUserDetailsResponse {
	
	private ErrorMap errorMap;
	private String message;
	private String messageCode;
	private String referenceId;
	private String service;
	private String status;
	private List<SuccessList> successList;
	private List<UserList> userList;
	
	public ErrorMap getErrorMap() {
		return errorMap;
	}
	public void setErrorMap(ErrorMap errorMap) {
		this.errorMap = errorMap;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	public String getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<SuccessList> getSuccessList() {
		return successList;
	}
	public void setSuccessList(List<SuccessList> successList) {
		this.successList = successList;
	}
	public List<UserList> getUserList() {
		return userList;
	}
	public void setUserList(List<UserList> userList) {
		this.userList = userList;
	}

}
