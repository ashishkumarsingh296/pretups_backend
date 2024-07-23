
package restassuredapi.pojo.totaltransactiondetailedviewresponsepojo;

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
    "transactionId",
    "recieverMsisdn",
    "rechargeAmount",
    "status",
    "rechargeDateTime",
    "serviceType"
})
public class DATum {

    @JsonProperty("transactionId")
    private String transactionId;
    @JsonProperty("recieverMsisdn")
    private String recieverMsisdn;
    @JsonProperty("rechargeAmount")
    private String rechargeAmount;
    @JsonProperty("status")
    private String status;
    @JsonProperty("rechargeDateTime")
    private String rechargeDateTime;
    @JsonProperty("serviceType")
    private String serviceType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("transactionId")
    public String getTransactionId() {
        return transactionId;
    }

    @JsonProperty("transactionId")
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @JsonProperty("recieverMsisdn")
    public String getRecieverMsisdn() {
        return recieverMsisdn;
    }

    @JsonProperty("recieverMsisdn")
    public void setRecieverMsisdn(String recieverMsisdn) {
        this.recieverMsisdn = recieverMsisdn;
    }

    @JsonProperty("rechargeAmount")
    public String getRechargeAmount() {
        return rechargeAmount;
    }

    @JsonProperty("rechargeAmount")
    public void setRechargeAmount(String rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("rechargeDateTime")
    public String getRechargeDateTime() {
        return rechargeDateTime;
    }

    @JsonProperty("rechargeDateTime")
    public void setRechargeDateTime(String rechargeDateTime) {
        this.rechargeDateTime = rechargeDateTime;
    }

    @JsonProperty("serviceType")
    public String getServiceType() {
        return serviceType;
    }

    @JsonProperty("serviceType")
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
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
