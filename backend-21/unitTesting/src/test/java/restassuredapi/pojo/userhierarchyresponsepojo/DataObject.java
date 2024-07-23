
package restassuredapi.pojo.userhierarchyresponsepojo;

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
    "type",
    "statusmsg",
    "upwardhierarchy",
    "txnstatus"
})
public class DataObject {

    @JsonProperty("type")
    private String type;
    @JsonProperty("message")
    private String message;
    
    
    @JsonProperty("txnstatus")
    private String txnstatus;
    
    
    
    @JsonProperty("upwardhierarchy")
    private List<Upwardhierarchy> upwardhierarchy = null;
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

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }
    
    
    
    
    @JsonProperty("txnstatus")
    public String getTxnstatus() {
		return txnstatus;
	}

    @JsonProperty("txnstatus")
	public void setTxnstatus(String txnstatus) {
		this.txnstatus = txnstatus;
	}

	@JsonProperty("upwardhierarchy")
    public List<Upwardhierarchy> getUpwardhierarchy() {
        return upwardhierarchy;
    }

    @JsonProperty("upwardhierarchy")
    public void setUpwardhierarchy(List<Upwardhierarchy> upwardhierarchy) {
        this.upwardhierarchy = upwardhierarchy;
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
