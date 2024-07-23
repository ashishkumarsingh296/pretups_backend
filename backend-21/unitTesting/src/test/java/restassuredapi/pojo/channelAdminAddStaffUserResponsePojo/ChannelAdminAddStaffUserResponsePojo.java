package restassuredapi.pojo.channelAdminAddStaffUserResponsePojo;

public final class ChannelAdminAddStaffUserResponsePojo {

	private int status;
	private String messageCode;
	private String message;
	private Object errorMap;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
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
}
