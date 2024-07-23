package restassuredapi.pojo.c2cbulkapprovallistresponsepojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MasterErrorList {

	@JsonProperty("errorCode")
    private String errorCode;
	@JsonProperty("errorMsg")
    private String errorMsg;
    

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("errorCode = ").append(errorCode)
        		.append("errorMsg").append( errorMsg)
        		).toString();
    }


}
