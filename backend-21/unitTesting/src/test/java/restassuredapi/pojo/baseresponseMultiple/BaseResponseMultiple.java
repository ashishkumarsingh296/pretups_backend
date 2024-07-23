package restassuredapi.pojo.baseresponseMultiple;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import restassuredapi.pojo.c2ctransferstockresponsepojo.BaseResponse;
import restassuredapi.pojo.c2ctransferstockresponsepojo.ErrorMap;
import restassuredapi.pojo.prepaidrechargeresponsepojo.PrepaidRechargeResponsePojo.DataObject;

public class BaseResponseMultiple {

    private String service;
    private Integer referenceId;
    private String status;
    private String messageCode;
    private String message;
    private ErrorMap errorMap;
    private ArrayList<BaseResponse> successList = new ArrayList<BaseResponse>();

	public ArrayList<BaseResponse> getSuccessList() {
		return successList;
	}

	public void setSuccessList(ArrayList<BaseResponse> successList) {
		this.successList = successList;
	}

	public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
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

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("service = ").append(service)
        		.append("referenceId").append( referenceId)
        		.append("status").append( status)
        		.append("messageCode").append( messageCode)
        		.append("message").append( message)
        		.append("errorMap").append( errorMap)
        		).toString();
    }

}
