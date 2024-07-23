package restassuredapi.pojo.addagentresponsepojo;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddagentResponsePojo {

	@JsonProperty("errorMap")
	private ErrorMap errorMap;
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


	// Getter Methods

	
	public class ErrorMap {
		ArrayList<Object> masterErrorList = new ArrayList<Object>();
		ArrayList<Object> rowErrorMsgLists = new ArrayList<Object>();

		public ArrayList<Object> getMasterErrorList() {
			return masterErrorList;
		}

		public void setMasterErrorList(ArrayList<Object> masterErrorList) {
			this.masterErrorList = masterErrorList;
		}

		public ArrayList<Object> getRowErrorMsgLists() {
			return rowErrorMsgLists;
		}

		public void setRowErrorMsgLists(ArrayList<Object> rowErrorMsgLists) {
			this.rowErrorMsgLists = rowErrorMsgLists;
		}

		// Getter Methods

	}


	public ErrorMap getErrorMap() {
		return errorMap;
	}
	public void setErrorMap(ErrorMap errorMap) {
		this.errorMap = errorMap;
	}

	// Setter Methods

}
