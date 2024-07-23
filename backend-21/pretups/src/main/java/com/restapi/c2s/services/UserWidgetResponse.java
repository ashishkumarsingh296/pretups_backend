package com.restapi.c2s.services;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;



public class UserWidgetResponse  {
        @JsonProperty("status")
		private int status;
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
