
package restassuredapi.pojo.userprofilethresholdresponsepojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "statusCode",
    "status",
    "formError",
    "successMsg",
    "globalError",
    "dataObject"
})
public class UserProfileThresholdResponsePojo {

    @JsonProperty("statusCode")
    private Long statusCode;
    @JsonProperty("status")
    private Boolean status;
    @JsonProperty("formError")
    private String formError;
    @JsonProperty("successMsg")
    private String successMsg;
    @JsonProperty("dataObject")
    private DataObject dataObject;
    @JsonProperty("globalError")
    private String globalError;
    public String getGlobalError() {
		return globalError;
	}

	public void setGlobalError(String globalError) {
		this.globalError = globalError;
	}

	@JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("statusCode")
    public Long getStatusCode() {
        return statusCode;
    }

    @JsonProperty("statusCode")
    public void setStatusCode(Long statusCode) {
        this.statusCode = statusCode;
    }

    @JsonProperty("status")
    public Boolean getStatus() {
        return status;
    }

    public String getFormError() {
		return formError;
	}

	public void setFormError(String formError) {
		this.formError = formError;
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

    @JsonProperty("dataObject")
    public DataObject getDataObject() {
        return dataObject;
    }

    @JsonProperty("dataObject")
    public void setDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
