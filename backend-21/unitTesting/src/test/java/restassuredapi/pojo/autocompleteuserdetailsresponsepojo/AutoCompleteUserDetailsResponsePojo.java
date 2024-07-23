
package restassuredapi.pojo.autocompleteuserdetailsresponsepojo;

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
    "dataObject",
    "fieldError",
    "formError",
    "globalError",
    "message",
    "messageArguments",
    "messageCode",
    "messageKey",
    "parameters",
    "status",
    "statusCode",
    "successMsg"
})
public class AutoCompleteUserDetailsResponsePojo {

    @JsonProperty("dataObject")
    private DataObject dataObject;
    @JsonProperty("fieldError")
    private FieldError fieldError;
    @JsonProperty("formError")
    private String formError;
    @JsonProperty("globalError")
    private String globalError;
    @JsonProperty("message")
    private String message;
    @JsonProperty("messageArguments")
    private List<String> messageArguments = null;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("messageKey")
    private String messageKey;
    @JsonProperty("parameters")
    private List<String> parameters = null;
    @JsonProperty("status")
    private boolean status;
    @JsonProperty("statusCode")
    private int statusCode;
    @JsonProperty("successMsg")
    private String successMsg;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("dataObject")
    public DataObject getDataObject() {
        return dataObject;
    }

    @JsonProperty("dataObject")
    public void setDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    @JsonProperty("fieldError")
    public FieldError getFieldError() {
        return fieldError;
    }

    @JsonProperty("fieldError")
    public void setFieldError(FieldError fieldError) {
        this.fieldError = fieldError;
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

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("messageArguments")
    public List<String> getMessageArguments() {
        return messageArguments;
    }

    @JsonProperty("messageArguments")
    public void setMessageArguments(List<String> messageArguments) {
        this.messageArguments = messageArguments;
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

    @JsonProperty("parameters")
    public List<String> getParameters() {
        return parameters;
    }

    @JsonProperty("parameters")
    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    @JsonProperty("status")
    public boolean isStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(boolean status) {
        this.status = status;
    }

    @JsonProperty("statusCode")
    public int getStatusCode() {
        return statusCode;
    }

    @JsonProperty("statusCode")
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @JsonProperty("successMsg")
    public String getSuccessMsg() {
        return successMsg;
    }

    @JsonProperty("successMsg")
    public void setSuccessMsg(String successMsg) {
        this.successMsg = successMsg;
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
