
package restassuredapi.pojo.dvdapiresponsepojo;

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
    "row",
    "transactionID",
    "message",
    "profileID"
})
public class TxnDetailsList {

    @JsonProperty("row")
    private String row;
    @JsonProperty("transactionID")
    private String transactionID;
    @JsonProperty("message")
    private String message;
    @JsonProperty("profileID")
    private String profileID;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("row")
    public String getRow() {
        return row;
    }

    @JsonProperty("row")
    public void setRow(String row) {
        this.row = row;
    }

    @JsonProperty("transactionID")
    public String getTransactionID() {
        return transactionID;
    }

    @JsonProperty("transactionID")
    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("profileID")
    public String getProfileID() {
        return profileID;
    }

    @JsonProperty("profileID")
    public void setProfileID(String profileID) {
        this.profileID = profileID;
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
