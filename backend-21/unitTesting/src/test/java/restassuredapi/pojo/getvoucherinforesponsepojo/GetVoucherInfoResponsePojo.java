
package restassuredapi.pojo.getvoucherinforesponsepojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "statusCode",
    "status",
    "formError",
    "messageCode",
    "messageKey",
    "dataObject",
    "globalError"
})
public class GetVoucherInfoResponsePojo {

    @JsonProperty("statusCode")
    private Long statusCode;
    @JsonProperty("status")
    private Boolean status;
    @JsonProperty("formError")
    private String formError;
    @JsonProperty("messageCode")
    private String messageCode;
    public String getGlobalError() {
		return globalError;
	}

	public void setGlobalError(String globalError) {
		this.globalError = globalError;
	}

	@JsonProperty("messageKey")
    private String messageKey;
    @JsonProperty("globalError")
    private String globalError;
    @JsonProperty("dataObject")
    private List<DataObject> dataObject = null;
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

    @JsonProperty("status")
    public void setStatus(Boolean status) {
        this.status = status;
    }

    @JsonProperty("formError")
    public String getFormError() {
        return formError;
    }

    @JsonProperty("formError")
    public void setFormError(String formError) {
        this.formError = formError;
    }

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

   /* @Override
    public String toString() {
        return new ToStringBuilder(this).append("statusCode", statusCode).append("status", status).append("formError", formError).append("messageCode", messageCode).append("messageKey", messageKey).append("dataObject", dataObject).append("additionalProperties", additionalProperties).toString();
    }*/

}
