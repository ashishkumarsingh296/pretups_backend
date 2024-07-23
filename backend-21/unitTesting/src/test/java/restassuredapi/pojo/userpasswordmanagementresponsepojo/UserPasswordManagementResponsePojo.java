package restassuredapi.pojo.userpasswordmanagementresponsepojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "statusCode",
    "status",
    "formError",
    "msg"
})

public class UserPasswordManagementResponsePojo {

    @JsonProperty("statusCode")
    private Integer statusCode;
    @JsonProperty("status")
    private Boolean status;
    @JsonProperty("successMsg")
    private String successMsg;
	@JsonProperty("globalError")
    private String globalError;
	@JsonProperty("formError")
    private String formError;
	@JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("messageKey")
    private String messageKey;
    
    @JsonProperty("messageCode")
	public String getMessageCode() {
		return messageCode;
	}

    @JsonProperty("messageCode")
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

    @JsonProperty("messageKey")
	public String getMessageKey() {
		return messageKey;
	}

    @JsonProperty("messageKey")
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	@JsonProperty("formError")
	public String getFormError() {
		return formError;
	}

	@JsonProperty("formError")
	public void setFormError(String formError) {
		this.formError = formError;
	}

	@JsonProperty("globalError")
	public String getGlobalError() {
			return globalError;
	}

	@JsonProperty("globalError")
	public void setGlobalError(String globalError) {
			this.globalError = globalError;
		}
    @JsonProperty("statusCode")
    public Integer getStatusCode() {
        return statusCode;
    }

    @JsonProperty("statusCode")
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @JsonProperty("status")
    public Boolean getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Boolean status) {
        this.status = status;
    }

    @JsonProperty("successMsg")
    public String getSuccessMsg() {
        return successMsg;
    }

    @JsonProperty("successMsg")
    public void setSuccessMsg(String successMsg) {
        this.successMsg = successMsg;
    }


    
    
}
