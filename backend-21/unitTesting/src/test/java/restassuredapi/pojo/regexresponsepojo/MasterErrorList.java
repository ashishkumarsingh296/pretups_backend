package restassuredapi.pojo.regexresponsepojo;

public class MasterErrorList {

    private String errorCode;
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
