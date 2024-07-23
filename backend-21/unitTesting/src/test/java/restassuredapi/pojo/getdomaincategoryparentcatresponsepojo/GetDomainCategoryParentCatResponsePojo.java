
package restassuredapi.pojo.getdomaincategoryparentcatresponsepojo;

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
    "loggedInUserDomainCode",
    "loggedInUserDomainName",
    "loggedInUserCatCode",
    "loggedInUserCatName",
    "ownerName",
    "data",
    "status",
    "messageCode",
    "message"
})
public class GetDomainCategoryParentCatResponsePojo {

    @JsonProperty("loggedInUserDomainCode")
    private String loggedInUserDomainCode;
    @JsonProperty("loggedInUserDomainName")
    private String loggedInUserDomainName;
    @JsonProperty("loggedInUserCatCode")
    private String loggedInUserCatCode;
    @JsonProperty("loggedInUserCatName")
    private String loggedInUserCatName;
    @JsonProperty("ownerName")
    private String ownerName;
    @JsonProperty("data")
    private List<Datum> data = null;
    @JsonProperty("status")
    private Long status;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("message")
    private String message;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("loggedInUserDomainCode")
    public String getLoggedInUserDomainCode() {
        return loggedInUserDomainCode;
    }

    @JsonProperty("loggedInUserDomainCode")
    public void setLoggedInUserDomainCode(String loggedInUserDomainCode) {
        this.loggedInUserDomainCode = loggedInUserDomainCode;
    }

    @JsonProperty("loggedInUserDomainName")
    public String getLoggedInUserDomainName() {
        return loggedInUserDomainName;
    }

    @JsonProperty("loggedInUserDomainName")
    public void setLoggedInUserDomainName(String loggedInUserDomainName) {
        this.loggedInUserDomainName = loggedInUserDomainName;
    }

    @JsonProperty("loggedInUserCatCode")
    public String getLoggedInUserCatCode() {
        return loggedInUserCatCode;
    }

    @JsonProperty("loggedInUserCatCode")
    public void setLoggedInUserCatCode(String loggedInUserCatCode) {
        this.loggedInUserCatCode = loggedInUserCatCode;
    }

    @JsonProperty("loggedInUserCatName")
    public String getLoggedInUserCatName() {
        return loggedInUserCatName;
    }

    @JsonProperty("loggedInUserCatName")
    public void setLoggedInUserCatName(String loggedInUserCatName) {
        this.loggedInUserCatName = loggedInUserCatName;
    }

    @JsonProperty("ownerName")
    public String getOwnerName() {
        return ownerName;
    }

    @JsonProperty("ownerName")
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @JsonProperty("data")
    public List<Datum> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<Datum> data) {
        this.data = data;
    }

    @JsonProperty("status")
    public Long getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Long status) {
        this.status = status;
    }

    @JsonProperty("messageCode")
    public String getMessageCode() {
        return messageCode;
    }

    @JsonProperty("messageCode")
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
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
