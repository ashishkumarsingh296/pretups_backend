package com.restapi.user.service;


import com.fasterxml.jackson.annotation.JsonProperty;



public class SuspendResumeResponse  {

    @JsonProperty("status")
	private int status;
    @JsonProperty("service")
  	private String service;
    
    public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	@JsonProperty("messageCode")
	private String messageCode;
    @JsonProperty("message")
	private String message;
    @JsonProperty("status")
	@io.swagger.v3.oas.annotations.media.Schema(example = "200")
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
	@io.swagger.v3.oas.annotations.media.Schema(example = "Success")
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	

	

}
