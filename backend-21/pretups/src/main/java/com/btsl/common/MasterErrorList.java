package com.btsl.common;

import com.btsl.pretups.common.SchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;

public class MasterErrorList {
    @Schema(pattern = SchemaConstants.STRING_INPUT_PATTERN)
    private String errorCode;
    @Schema(pattern = SchemaConstants.STRING_INPUT_PATTERN)
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
