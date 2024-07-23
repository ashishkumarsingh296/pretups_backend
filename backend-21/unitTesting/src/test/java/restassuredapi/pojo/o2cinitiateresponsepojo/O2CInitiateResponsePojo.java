
package restassuredapi.pojo.o2cinitiateresponsepojo;

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
    "type",
    "txnStatus",
    "message",
    "txnId"
})
public class O2CInitiateResponsePojo {

    @JsonProperty("type")
    private String type;
    @JsonProperty("txnStatus")
    private String txnStatus;
    @JsonProperty("message")
    private String message;
    @JsonProperty("txnId")
    private String txnId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("txnStatus")
    public String getTxnStatus() {
        return txnStatus;
    }

    @JsonProperty("txnStatus")
    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("txnId")
    public String getTxnId() {
        return txnId;
    }

    @JsonProperty("txnId")
    public void setTxnId(String txnId) {
        this.txnId = txnId;
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
