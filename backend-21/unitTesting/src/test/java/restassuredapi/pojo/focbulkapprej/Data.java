
package restassuredapi.pojo.focbulkapprej;

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
    "BatchId",
    "Remarks",
    "batchName",
    "language1",
    "language2",
    "pin",
    "request",
    "requestType"
})
public class Data {

    @JsonProperty("BatchId")
    private String batchId;
    @JsonProperty("Remarks")
    private String remarks;
    @JsonProperty("batchName")
    private String batchName;
    @JsonProperty("language1")
    private Integer language1;
    @JsonProperty("language2")
    private Integer language2;
    @JsonProperty("pin")
    private Integer pin;
    @JsonProperty("request")
    private String request;
    @JsonProperty("requestType")
    private String requestType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("BatchId")
    public String getBatchId() {
        return batchId;
    }

    @JsonProperty("BatchId")
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    @JsonProperty("Remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("Remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JsonProperty("batchName")
    public String getBatchName() {
        return batchName;
    }

    @JsonProperty("batchName")
    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    @JsonProperty("language1")
    public Integer getLanguage1() {
        return language1;
    }

    @JsonProperty("language1")
    public void setLanguage1(Integer language1) {
        this.language1 = language1;
    }

    @JsonProperty("language2")
    public Integer getLanguage2() {
        return language2;
    }

    @JsonProperty("language2")
    public void setLanguage2(Integer language2) {
        this.language2 = language2;
    }

    @JsonProperty("pin")
    public Integer getPin() {
        return pin;
    }

    @JsonProperty("pin")
    public void setPin(Integer pin) {
        this.pin = pin;
    }

    @JsonProperty("request")
    public String getRequest() {
        return request;
    }

    @JsonProperty("request")
    public void setRequest(String request) {
        this.request = request;
    }

    @JsonProperty("requestType")
    public String getRequestType() {
        return requestType;
    }

    @JsonProperty("requestType")
    public void setRequestType(String requestType) {
        this.requestType = requestType;
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
