
package restassuredapi.pojo.o2cdirectapprovalresponsepojo;

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
    "txnstatus",
    "txnid",
    "message"
})
public class O2CDirectApprovalResponsePojo {

    @JsonProperty("type")
    private String type;
    @JsonProperty("txnStatus")
    private String txnStatus;
    @JsonProperty("txnId")
    private String txnId;
    @JsonProperty("message")
    private String message;
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
    public String getTxnstatus() {
        return txnStatus;
    }

    @JsonProperty("txnStatus")
    public void setTxnstatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }

    @JsonProperty("txnId")
    public String getTxnid() {
        return txnId;
    }

    @JsonProperty("txnId")
    public void setTxnid(String txnId) {
        this.txnId = txnId;
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
