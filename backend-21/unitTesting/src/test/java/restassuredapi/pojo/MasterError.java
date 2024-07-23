package restassuredapi.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MasterError {
	    @JsonProperty("errorCode")
	    private String errorCode;
	    @JsonProperty("errorMsg")
	    private String errorMsg;
	    @JsonProperty("errorCode")
	    public String getErrorCode() {
	        return errorCode;
	    }

	    @JsonProperty("errorCode")
	    public void setErrorCode(String errorCode) {
	        this.errorCode = errorCode;
	    }

	    @JsonProperty("errorMsg")
	    public String getErrorMsg() {
	        return errorMsg;
	    }

	    @JsonProperty("errorMsg")
	    public void setErrorMsg(String errorMsg) {
	        this.errorMsg = errorMsg;
	    }

	}

