package com.btsl.common;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.btsl.pretups.common.PretupsI;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/*
 * This class is basic bean class represent JSON response
 */

@Component
public class PretupsResponse<T> {

    @JsonInclude(Include.NON_NULL)
	private Integer statusCode;
    @JsonInclude(Include.NON_NULL)
	private Boolean status;
    @JsonInclude(Include.NON_NULL)
	private String successMsg;
    @JsonInclude(Include.NON_NULL)
	private String formError;
    @JsonInclude(Include.NON_NULL)
	private String globalError;
    @JsonInclude(Include.NON_NULL)
	private String[] parameters;
    @JsonInclude(Include.NON_NULL)
	private T dataObject;
    @JsonInclude(Include.NON_NULL)
	private Map<String, String> fieldError = null;
    @JsonInclude(Include.NON_NULL)
	private String messageCode;
    
    @JsonInclude(Include.NON_NULL)
	private String messageKey;
	
    @JsonInclude(Include.NON_NULL)
	private String message;
    
	public String getMessageCode() {
		return messageCode;
	}


	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	@JsonInclude(Include.NON_NULL)
	private String[] messageArguments;

	public String[] getMessageArguments() {
		return messageArguments;
	}


	public void setMessageArguments(String[] messageArguments) {
		this.messageArguments = messageArguments;
	}


	public PretupsResponse() {
	}
	
	public Map<String, String> getFieldError() {
		return fieldError;
	}

	public void setFieldError(Map<String, String> fieldError) {
		this.fieldError = fieldError;
	}


	public String getFieldError(String fieldName) {
		return fieldError.get(fieldName);
	}



	public void setFieldError(String fieldName, String errorMessage) {
		fieldError.put(fieldName, errorMessage);
	}


	public Integer getStatusCode() {
		return statusCode;
	}


	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}


	public Boolean getStatus() {
		return status;
	}


	public void setStatus(Boolean status) {
		this.status = status;
	}

	
	public String getSuccessMsg() {
		return successMsg;
	}


	public void setSuccessMsg(String successMsg) {
		this.successMsg = successMsg;
	}


	public String getFormError() {
		return formError;
	}


	public void setFormError(String errorMessage) {
		this.formError = errorMessage;
	}
	
	public void setFormError(String errorMessage, String[] parameters) {
		this.formError = errorMessage;
		this.parameters = parameters;
	}


	public String getGlobalError() {
		return globalError;
	}


	public void setGlobalError(String globalError) {
		this.globalError = globalError;
	}


	public Boolean hasFieldError(){
		if(fieldError == null || fieldError.isEmpty()){
			return false;
		}else{
			return true;
		}
	}
	
	public String[] getParameters() {
		return parameters;
	}


	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}
	
	

	public T getDataObject() {
		return dataObject;
	}


	public void setDataObject(T dataObject) {
		this.dataObject = dataObject;
	}


	public Boolean hasFormError(){
		if(formError == null || formError.equalsIgnoreCase("")){
			return false;
		}else{
			return true;
		}
	}
	
	public Boolean hasGlobalError(){
		if(globalError == null || globalError.equalsIgnoreCase("")){
			return false;
		}else{
			return true;
		}
	}
	
	public void setResponse(Integer statusCode, Boolean status, String message){
		this.statusCode = statusCode;
		this.status = status;
		if(status){
			this.successMsg = message;
		}else{
			this.globalError = message;
		}
	}
	
	public void setResponse(String formError, String[] parameters){
		this.status = false;
		this.statusCode = PretupsI.RESPONSE_FAIL;
		this.formError = formError;
		this.parameters = parameters;
	}
	
	public void setDataObject(Integer statusCode, Boolean status, T object){
		this.statusCode = statusCode;
		this.status = status;
		this.dataObject = object;
	}
	public void setDataObject(Integer statusCode, T object){
		this.statusCode = statusCode;
		this.dataObject = object;
	}
	
	public String getMessageKey() {
		return messageCode;
	}


	public void setMessageKey(String messageCode) {
		this.messageCode = messageCode;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
