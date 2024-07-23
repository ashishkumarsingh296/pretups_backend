
package restassuredapi.pojo.viewselfcommenquiryresponsepojo;

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
    "dataObject"
})
public class ViewSelfCommEnquiryResponsePojo {

    @JsonProperty("statusCode")
    private Long statusCode;
    @JsonProperty("status")
    private Boolean status;
    @JsonProperty("dataObject")
    private DataObject dataObject;
    @JsonProperty("formError")
    private String formError;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("messageKey")
    private String messageKey;
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

    @JsonProperty("dataObject")
    public DataObject getDataObject() {
        return dataObject;
    }

    @JsonProperty("dataObject")
    public void setDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
