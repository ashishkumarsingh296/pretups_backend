package com.restapi.channelAdmin.responseVO;

public class SuspendResumeStaffResponseVO {
	
	private int status;
    private String messageCode;
    private String message;
    
    
    
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



	@Override
	public String toString() {
		return null;
	}
	
}
