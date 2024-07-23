
package restassuredapi.pojo.c2sUserBalanceresponsepojo;

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
    "openingBalance",
    "closingBalance",
    "status",
    "messageCode",
    "message"
})
public class C2SUserBalanceResponsePojo {

    @JsonProperty("openingBalance")
    private String openingBalance;
    @JsonProperty("closingBalance")
    private String closingBalance;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("message")
    private String message;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("openingBalance")
    public String getOpeningBalance() {
        return openingBalance;
    }

    @JsonProperty("openingBalance")
    public void setOpeningBalance(String openingBalance) {
        this.openingBalance = openingBalance;
    }

    @JsonProperty("closingBalance")
    public String getClosingBalance() {
        return closingBalance;
    }

    @JsonProperty("closingBalance")
    public void setClosingBalance(String closingBalance) {
        this.closingBalance = closingBalance;
    }

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Integer status) {
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
