
package restassuredapi.pojo.usersearchresponsepojo;

import java.util.HashMap;
import java.util.List;
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
    "dataObject"
})
public class UserSearchResponsePojo {

    @JsonProperty("statusCode")
    private Integer statusCode;
    @JsonProperty("status")
    private Boolean status;
   

	@JsonProperty("formError")
    private String formError;
    @JsonProperty("successMsg")
    private String successMsg;
    @JsonProperty("dataObject")
    private List<DataObject> dataObject = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
    
    
    public String getFormError() {
		return formError;
	}

	public void setFormError(String formError) {
		this.formError = formError;
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
    public List<DataObject> getDataObject() {
        return dataObject;
    }

    @JsonProperty("dataObject")
    public void setDataObject(List<DataObject> dataObject) {
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
